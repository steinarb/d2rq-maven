package de.fuberlin.wiwiss.d2rq.algebra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.d2rq.algebra.AliasMap.Alias;
import de.fuberlin.wiwiss.d2rq.nodes.NodeMaker;
import de.fuberlin.wiwiss.d2rq.sql.ResultRow;
import de.fuberlin.wiwiss.d2rq.sql.TripleMaker;

/**
 * A respresentation of a d2rq:PropertyBridge, describing how
 * a set of virtual triples are to be obtained
 * from a database. The virtual subjects, predicates and objects
 * are generated by {@link NodeMaker}s.
 *
 * @author Chris Bizer chris@bizer.de
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: TripleRelation.java,v 1.8 2008/04/27 22:42:36 cyganiak Exp $
 */
public class TripleRelation implements TripleMaker {
	public static final String SUBJECT_NODE_MAKER = "subject";
	public static final String PREDICATE_NODE_MAKER = "predicate";
	public static final String OBJECT_NODE_MAKER = "object";

	public static final List S_P_O_NODE_MAKERS = Arrays.asList(
			new String[]{SUBJECT_NODE_MAKER, PREDICATE_NODE_MAKER, OBJECT_NODE_MAKER});
	
	private NodeMaker subjectMaker;
	private NodeMaker predicateMaker;
	private NodeMaker objectMaker; 
	private Relation baseRelation;
	
	public TripleRelation(Relation baseRelation, NodeMaker subjectMaker, NodeMaker predicateMaker, NodeMaker objectMaker) {
		this.subjectMaker = subjectMaker;
		this.predicateMaker = predicateMaker;
		this.objectMaker = objectMaker;
		this.baseRelation = baseRelation;
	}
	
	public Relation baseRelation() {
		return this.baseRelation;
	}
	
	public String toString() {
		return "PropertyBridge(\n" +
				"    " + this.subjectMaker + "\n" +
				"    " + this.predicateMaker + "\n" +
				"    " + this.objectMaker + "\n" +
				")";
	}
	
	public TripleRelation withPrefix(int index) {
		Collection newAliases = new ArrayList();
		Iterator it = baseRelation().tables().iterator();
		while (it.hasNext()) {
			RelationName tableName = (RelationName) it.next();
			newAliases.add(new Alias(tableName, tableName.withPrefix(index)));
		}
		AliasMap renamer = new AliasMap(newAliases);
		NodeMaker s = subjectMaker.renameAttributes(renamer);
		NodeMaker p = predicateMaker.renameAttributes(renamer);
		NodeMaker o = objectMaker.renameAttributes(renamer);
		return new TripleRelation(baseRelation.renameColumns(renamer), s, p, o);
	}
	
	public Collection makeTriples(ResultRow row) {
		Node s = this.subjectMaker.makeNode(row);
		Node p = this.predicateMaker.makeNode(row);
		Node o = this.objectMaker.makeNode(row);
		if (s == null || p == null || o == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.singleton(new Triple(s, p, o));
	}
	
	public TripleRelation selectTriple(Triple t) {
		MutableRelation newBase = new MutableRelation(this.baseRelation);
		NodeMaker s = this.subjectMaker.selectNode(t.getSubject(), newBase);
		if (s.equals(NodeMaker.EMPTY)) return null;
		NodeMaker p = this.predicateMaker.selectNode(t.getPredicate(), newBase);
		if (p.equals(NodeMaker.EMPTY)) return null;
		NodeMaker o = this.objectMaker.selectNode(t.getObject(), newBase);
		if (o.equals(NodeMaker.EMPTY)) return null;
		Set projections = new HashSet();
		projections.addAll(s.projectionSpecs());
		projections.addAll(p.projectionSpecs());
		projections.addAll(o.projectionSpecs());
		newBase.project(projections);
		return new TripleRelation(newBase.immutableSnapshot(), s, p, o);
	}
	
	public Collection nodeMakerNames() {
		return S_P_O_NODE_MAKERS;
	}
	
	public NodeMaker nodeMaker(String name) {
		if (SUBJECT_NODE_MAKER.equals(name)) {
			return this.subjectMaker;
		}
		if (PREDICATE_NODE_MAKER.equals(name)) {
			return this.predicateMaker;
		}
		if (OBJECT_NODE_MAKER.equals(name)) {
			return this.objectMaker;
		}
		return null;
	}
}