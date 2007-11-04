package de.fuberlin.wiwiss.d2rq.nodes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.AnonId;

import de.fuberlin.wiwiss.d2rq.algebra.Attribute;
import de.fuberlin.wiwiss.d2rq.algebra.ColumnRenamer;
import de.fuberlin.wiwiss.d2rq.algebra.MutableRelation;
import de.fuberlin.wiwiss.d2rq.expr.AttributeValue;
import de.fuberlin.wiwiss.d2rq.expr.Conjunction;
import de.fuberlin.wiwiss.d2rq.pp.PrettyPrinter;
import de.fuberlin.wiwiss.d2rq.sql.ResultRow;
import de.fuberlin.wiwiss.d2rq.values.ValueMaker;

public class TypedNodeMaker implements NodeMaker {
	public final static NodeType URI = new URINodeType();
	public final static NodeType BLANK = new BlankNodeType();
	public final static NodeType PLAIN_LITERAL = new LiteralNodeType("", null);
	public final static NodeType XSD_DATE = new DateLiteralNodeType();
	public final static NodeType XSD_DATETIME = new DateTimeLiteralNodeType();
	
	public static NodeType languageLiteral(String language) {
		return new LiteralNodeType(language, null);
	}
	public static NodeType typedLiteral(RDFDatatype datatype) {
		if (datatype.equals(XSDDatatype.XSDdate)) {
			return XSD_DATE;
		}
		if (datatype.equals(XSDDatatype.XSDdateTime)) {
			return XSD_DATETIME;
		}
		return new LiteralNodeType("", datatype);
	}
	
	private NodeType nodeType;
	private ValueMaker valueMaker;
	private boolean isUnique;
	
	public TypedNodeMaker(NodeType nodeType, ValueMaker valueMaker, boolean isUnique) {
		this.nodeType = nodeType;
		this.valueMaker = valueMaker;
		this.isUnique = isUnique;
	}
	
	public Set projectionColumns() {
		return this.valueMaker.projectionAttributes();
	}
	
	public boolean isUnique() {
		return this.isUnique;
	}

	public void describeSelf(NodeSetFilter c) {
		this.nodeType.matchConstraint(c);
		this.valueMaker.describeSelf(c);
	}

	public Node makeNode(ResultRow tuple) {
		String value = this.valueMaker.makeValue(tuple);
		if (value == null) {
			return null;
		}
		return this.nodeType.makeNode(value);
	}
	
	public NodeMaker selectNode(Node node, MutableRelation relation) {
		if (node.equals(Node.ANY) || node.isVariable()) {
			return this;
		}
		if (!this.nodeType.matches(node)) {
			return NodeMaker.EMPTY;
		}
		String value = this.nodeType.extractValue(node);
		if (!this.valueMaker.matches(value)) {
			return NodeMaker.EMPTY;
		}
		List conditions = new ArrayList();
		Iterator it = this.valueMaker.attributeConditions(value).entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			conditions.add(AttributeValue.create((Attribute) entry.getKey(), (String) entry.getValue()));
		}
		relation.select(Conjunction.create(conditions));
		return new FixedNodeMaker(node, isUnique());
	}
	
	public NodeMaker renameColumns(ColumnRenamer renamer, MutableRelation relation) {
		relation.renameColumns(renamer);
		return new TypedNodeMaker(this.nodeType, 
				this.valueMaker.replaceColumns(renamer), this.isUnique);
	}

	public String toString() {
		return this.nodeType.toString() + "(" + this.valueMaker + ")";
	}

	public interface NodeType {
		String extractValue(Node node);
		Node makeNode(String value);
		void matchConstraint(NodeSetFilter c);
		boolean matches(Node node);
	}
	
	private static class URINodeType implements NodeType {
		public String extractValue(Node node) { return node.getURI(); }
		public Node makeNode(String value) { return Node.createURI(value); }
		public void matchConstraint(NodeSetFilter c) { c.limitToURIs(); }
		public boolean matches(Node node) { return node.isURI(); }
		public String toString() { return "URI"; }
	}
	
	private static class BlankNodeType implements NodeType {
		public String extractValue(Node node) { return node.getBlankNodeLabel(); }
		public Node makeNode(String value) { return Node.createAnon(new AnonId(value)); }
		public void matchConstraint(NodeSetFilter c) { c.limitToBlankNodes(); }
		public boolean matches(Node node) { return node.isBlank(); }
		public String toString() { return "Blank"; }
	}
	
	private static class LiteralNodeType implements NodeType {
		private String language;
		private RDFDatatype datatype;
		LiteralNodeType(String language, RDFDatatype datatype) {
			this.language = language;
			this.datatype = datatype;
		}
		public String extractValue(Node node) {
			return node.getLiteralLexicalForm();
		}
		public Node makeNode(String value) {
			return Node.createLiteral(value, this.language, this.datatype);
		}
		public void matchConstraint(NodeSetFilter c) {
	        c.limitToLiterals(this.language, this.datatype);
		}
		public boolean matches(Node node) { 
			return node.isLiteral()
					&& this.language.equals(node.getLiteralLanguage())
					&& ((this.datatype == null && node.getLiteralDatatype() == null)
							|| (this.datatype != null && this.datatype.equals(node.getLiteralDatatype())));
		}
		public String toString() {
			StringBuffer result = new StringBuffer("Literal");
			if (!"".equals(this.language)) {
				result.append("@" + this.language);
			}
			if (this.datatype != null) {
				result.append("^^");
				result.append(PrettyPrinter.toString(this.datatype));
			}
			return result.toString();
		}
	}
	
	private static class DateLiteralNodeType extends LiteralNodeType {
		DateLiteralNodeType() {
			super("", XSDDatatype.XSDdate);
		}
		public boolean matches(Node node) {
			return super.matches(node) && XSDDatatype.XSDdate.isValid(node.getLiteralLexicalForm());
		}
		public String extractValue(Node node) {
			XSDDateTime xsd = (XSDDateTime) node.getLiteralValue();
			return new java.sql.Date(xsd.asCalendar().getTimeInMillis()).toString();
		}
		public Node makeNode(String value) {
			// Couldn't figure out a safe way to convert java.sql.Date to an XSD date
			if (!XSDDatatype.XSDdate.isValid(value)) return null;
			return Node.createLiteral(value, null, XSDDatatype.XSDdate);
		}
	}
	
	private static class DateTimeLiteralNodeType extends LiteralNodeType {
		DateTimeLiteralNodeType() {
			super("", XSDDatatype.XSDdateTime);
		}
		public boolean matches(Node node) {
			return super.matches(node) && XSDDatatype.XSDdateTime.isValid(node.getLiteralLexicalForm());
		}
		public String extractValue(Node node) {
			XSDDateTime xsd = (XSDDateTime) node.getLiteralValue();
			return new java.sql.Timestamp(xsd.asCalendar().getTimeInMillis()).toString();
		}
		public Node makeNode(String value) {
			Timestamp t = Timestamp.valueOf(value);
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(t.getTime());
			return Node.createLiteral(new XSDDateTime(c).toString(), null, XSDDatatype.XSDdateTime);
		}
	}
}
