package de.fuberlin.wiwiss.d2rq.map;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.d2rq.pp.PrettyPrinter;
import de.fuberlin.wiwiss.d2rq.rdql.NodeConstraint;
import de.fuberlin.wiwiss.d2rq.sql.ResultRow;

/**
 * NodeMaker that returns a fixed node.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: FixedNodeMaker.java,v 1.9 2006/09/09 23:25:15 cyganiak Exp $
 */
public class FixedNodeMaker implements NodeMaker {
	private Node fixedNode;

	public FixedNodeMaker(Node fixedNode) {
		this.fixedNode = fixedNode;
	}
	
    public void matchConstraint(NodeConstraint c) {
        c.matchFixedNode(fixedNode);
    }

	public boolean couldFit(Node node) {
		return Node.ANY.equals(node) || this.fixedNode.equals(node);
	}

	public Set getColumns() {
		return Collections.EMPTY_SET;
	}

	public Set getJoins() {
		return Collections.EMPTY_SET;
	}
	
	public Expression condition() {
		return Expression.TRUE;
	}

	public AliasMap getAliases() {
		return AliasMap.NO_ALIASES;
	}
	
	public Map getColumnValues(Node node) {
		return Collections.EMPTY_MAP;
	}

	public Node getNode(ResultRow row) {
		return this.fixedNode;
	}
	
	public boolean isUnique() {
		return true;
	}
	
	public String toString() {
		return "Fixed(" + PrettyPrinter.toString(this.fixedNode) + ")";
	}
}