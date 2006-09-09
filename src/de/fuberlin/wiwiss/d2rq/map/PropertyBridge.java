package de.fuberlin.wiwiss.d2rq.map;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.d2rq.algebra.RDFRelation;
import de.fuberlin.wiwiss.d2rq.find.QueryContext;

/**
 * A respresentation of a d2rq:PropertyBridge, describing how
 * a set of virtual triples are to be obtained
 * from a database. The virtual subjects, predicates and objects
 * are generated by {@link NodeMaker}s.
 *
 * @author Chris Bizer chris@bizer.de
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: PropertyBridge.java,v 1.9 2006/09/09 15:40:02 cyganiak Exp $
 */
public class PropertyBridge implements RDFRelation {
	private Node id;
	private NodeMaker subjectMaker;
	private NodeMaker predicateMaker;
	private NodeMaker objectMaker; 
	private Database database;
	private AliasMap aliases;
	private Set joins = new HashSet(2);
	private Expression condition;
	private URIMatchPolicy uriMatchPolicy = new URIMatchPolicy();
	private boolean mightContainDuplicates = false;
	private Set selectColumns = new HashSet();
	
	public PropertyBridge(Node id, NodeMaker subjectMaker, NodeMaker predicateMaker, NodeMaker objectMaker, Database database, URIMatchPolicy policy) {
		this.id = id;
		this.subjectMaker = subjectMaker;
		this.predicateMaker = predicateMaker;
		this.objectMaker = objectMaker;
		this.database = database;
		this.uriMatchPolicy = policy;
		this.selectColumns.addAll(this.subjectMaker.getColumns());
		this.selectColumns.addAll(this.predicateMaker.getColumns());
		this.selectColumns.addAll(this.objectMaker.getColumns());
		this.joins.addAll(this.subjectMaker.getJoins());
		this.joins.addAll(this.predicateMaker.getJoins());
		this.joins.addAll(this.objectMaker.getJoins());
		this.condition = 
				this.subjectMaker.condition().and(
				this.predicateMaker.condition().and(
				this.objectMaker.condition()));
		this.aliases = this.subjectMaker.getAliases();
		this.aliases = this.aliases.applyTo(this.predicateMaker.getAliases());
		this.aliases = this.aliases.applyTo(this.objectMaker.getAliases());
		boolean oneOrMoreUnique = this.subjectMaker.isUnique()
				|| this.predicateMaker.isUnique()
				|| this.objectMaker.isUnique();
		boolean allUnique = this.subjectMaker.isUnique()
				&& this.predicateMaker.isUnique()
				&& this.objectMaker.isUnique();
		this.mightContainDuplicates = !((joins.isEmpty() && oneOrMoreUnique)
				|| (joins.size() >= 1 && allUnique));
	}
	
	public AliasMap getAliases() {
		return this.aliases;
	}

	public Map getColumnValues() {
		return Collections.EMPTY_MAP;
	}
	
	public Set getSelectColumns() {
		return this.selectColumns;
	}

	public Expression condition() {
		return this.condition;
	}

	public int getEvaluationPriority() {
		return this.uriMatchPolicy.getEvaluationPriority();
	}

	public boolean mightContainDuplicates() {
		return this.mightContainDuplicates;
	}
	
	public boolean couldFit(Triple t, QueryContext context) {
		if (!this.subjectMaker.couldFit(t.getSubject()) ||
				!this.predicateMaker.couldFit(t.getPredicate()) ||
				!this.objectMaker.couldFit(t.getObject())) {
			return false;
		}
		if (t.getSubject().isConcrete()) {
			if (!this.uriMatchPolicy.couldFitSubjectInContext(context)) {
				return false;
			}
			this.uriMatchPolicy.updateContextAfterSubjectMatch(context);
		}
		if (t.getObject().isConcrete()) {
			if (!this.uriMatchPolicy.couldFitObjectInContext(context)) {
				return false;
			}
			this.uriMatchPolicy.updateContextAfterObjectMatch(context);
		}
		return true;
	}

	public Database getDatabase() {
		return this.database;
	}

	public NodeMaker getSubjectMaker() {
		return this.subjectMaker;
	}

	public NodeMaker getPredicateMaker() {
		return this.predicateMaker;
	}

	public NodeMaker getObjectMaker() {
		return this.objectMaker;
	}
    
	public Set getJoins() {
		return this.joins;
	}

	public String toString() {
		return "PropertyBridge(\n" +
				"    " + this.subjectMaker + "\n" +
				"    " + this.predicateMaker + "\n" +
				"    " + this.objectMaker + "\n" +
				")";
	}
	
	public RDFRelation withPrefix(int index) {
		return new PropertyBridge(this.id,
				RenamingNodeMaker.prefix(getSubjectMaker(), index),
				RenamingNodeMaker.prefix(getPredicateMaker(), index),
				RenamingNodeMaker.prefix(getObjectMaker(), index),
				this.database,
				this.uriMatchPolicy);
	}
}