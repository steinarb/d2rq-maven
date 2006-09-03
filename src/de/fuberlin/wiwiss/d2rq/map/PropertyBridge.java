package de.fuberlin.wiwiss.d2rq.map;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.d2rq.find.QueryContext;

/**
 * A respresentation of a d2rq:PropertyBridge, describing how
 * a set of virtual triples are to be obtained
 * from a database. The virtual subjects, predicates and objects
 * are generated by {@link NodeMaker}s.
 *
 * @author Chris Bizer chris@bizer.de
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: PropertyBridge.java,v 1.6 2006/09/03 00:08:10 cyganiak Exp $
 */
public class PropertyBridge {
	private Node id;
	private NodeMaker subjectMaker;
	private NodeMaker predicateMaker;
	private NodeMaker objectMaker; 
	private Database database;
	private AliasMap aliases;
	private Set joins = new HashSet(2);
	private Set conditions = new HashSet(1);
	private URIMatchPolicy uriMatchPolicy = new URIMatchPolicy();
	private boolean mightContainDuplicates = false;

	public PropertyBridge(Node id, NodeMaker subjectMaker, NodeMaker predicateMaker, NodeMaker objectMaker, Database database, URIMatchPolicy policy) {
		this.id = id;
		this.subjectMaker = subjectMaker;
		this.predicateMaker = predicateMaker;
		this.objectMaker = objectMaker;
		this.database = database;
		this.uriMatchPolicy = policy;
		this.joins.addAll(this.subjectMaker.getJoins());
		this.joins.addAll(this.predicateMaker.getJoins());
		this.joins.addAll(this.objectMaker.getJoins());
		this.conditions.addAll(this.subjectMaker.getConditions());
		this.conditions.addAll(this.predicateMaker.getConditions());
		this.conditions.addAll(this.objectMaker.getConditions());
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
	
	public Object clone() throws CloneNotSupportedException {return super.clone();}

	public AliasMap getAliases() {
		return this.aliases;
	}
	
	/**
	 * Returns the SQL WHERE conditions that must hold for a given
	 * database row or the bridge will not generate a triple.
	 * @return a set of Strings
	 */
	public Set getConditions() {
		return this.conditions;
	}

	public int getEvaluationPriority() {
		return this.uriMatchPolicy.getEvaluationPriority();
	}

	public boolean mightContainDuplicates() {
		return this.mightContainDuplicates;
	}
	
	/**
	 * Checks if a given triple could match this bridge without
	 * querying the database.
	 */
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
		return super.toString() + "(" + this.id.toString() + ")";
	}
	
	public PropertyBridge withPrefix(int index) {
		return new PropertyBridge(this.id,
				TableRenamingNodeMaker.prefix(getSubjectMaker(), index),
				TableRenamingNodeMaker.prefix(getPredicateMaker(), index),
				TableRenamingNodeMaker.prefix(getObjectMaker(), index),
				this.database,
				this.uriMatchPolicy);
	}
}