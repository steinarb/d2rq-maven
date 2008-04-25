package de.fuberlin.wiwiss.d2rq.nodes;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Set;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.AnonId;

import de.fuberlin.wiwiss.d2rq.algebra.ColumnRenamer;
import de.fuberlin.wiwiss.d2rq.algebra.MutableRelation;
import de.fuberlin.wiwiss.d2rq.algebra.Relation;
import de.fuberlin.wiwiss.d2rq.pp.PrettyPrinter;
import de.fuberlin.wiwiss.d2rq.sql.ResultRow;
import de.fuberlin.wiwiss.d2rq.values.ValueMaker;

public class TypedNodeMaker implements NodeMaker {
	public final static NodeType URI = new URINodeType();
	public final static NodeType BLANK = new BlankNodeType();
	public final static NodeType PLAIN_LITERAL = new LiteralNodeType("", null);
	public final static NodeType XSD_DATE = new DateLiteralNodeType();
	public final static NodeType XSD_DATETIME = new DateTimeLiteralNodeType();
	public final static NodeType XSD_BOOLEAN = new BooleanLiteralNodeType();
	
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
		if (datatype.equals(XSDDatatype.XSDboolean)) {
			return XSD_BOOLEAN;
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
	
	public Set projectionSpecs() {
		return this.valueMaker.projectionSpecs();
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
		if (value == null) {
			return NodeMaker.EMPTY;
		}
		valueMaker.selectValue(value, relation);
		if (Relation.EMPTY.equals(relation.immutableSnapshot())) {
			return NodeMaker.EMPTY;
		}
		return new FixedNodeMaker(node, isUnique());
	}
	
	public NodeMaker renameAttributes(ColumnRenamer renamer, MutableRelation relation) {
		relation.renameColumns(renamer);
		return new TypedNodeMaker(this.nodeType, 
				this.valueMaker.renameAttributes(renamer), this.isUnique);
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
			try {
				Calendar c = Calendar.getInstance();
				Timestamp t = Timestamp.valueOf(value);
				c.setTimeInMillis(t.getTime());
				return Node.createLiteral(
						new XSDDateTime(c).toString(), null, XSDDatatype.XSDdateTime);
			} catch (RuntimeException ex) {
				// TODO: This is hackish ... Support for PostgreSQL TIMESTAMP WITH TIME ZONE type
				// PostgreSQL answers a TIMESTAMP with an added [+-]HH(:MM)?,
				// and the Timestamp class won't parse that
				value = value.replace(' ', 'T').replaceAll("\\.\\d+", "").replaceAll("[+-]\\d\\d$", "$0:00");
				return Node.createLiteral(value, null, XSDDatatype.XSDdateTime);
			}
		}
	}
	
	private static class BooleanLiteralNodeType extends LiteralNodeType {
		BooleanLiteralNodeType() {
			super("", XSDDatatype.XSDboolean);
		}
		public boolean matches(Node node) {
			return super.matches(node) && XSDDatatype.XSDboolean.isValid(node.getLiteralLexicalForm());
		}
		public String extractValue(Node node) {
			if ("0".equals(node.getLiteralLexicalForm()) || "false".equals(node.getLiteralLexicalForm())) {
				return "0";
			}
			return "1";
		}
		public Node makeNode(String value) {
			boolean b = false;
			try {
				int intValue = Integer.parseInt(value);
				b = intValue != 0;
			} catch (NumberFormatException ex) {
				if ("true".equals(value.toLowerCase())) {
					b = true;
				}
			}
			return Node.createLiteral(b ? "true" : "false", null, XSDDatatype.XSDboolean);
		}
	}
}
