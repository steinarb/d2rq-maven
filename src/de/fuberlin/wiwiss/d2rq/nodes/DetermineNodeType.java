package de.fuberlin.wiwiss.d2rq.nodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.Var;

import de.fuberlin.wiwiss.d2rq.algebra.Attribute;
import de.fuberlin.wiwiss.d2rq.expr.Expression;
import de.fuberlin.wiwiss.d2rq.values.BlankNodeID;
import de.fuberlin.wiwiss.d2rq.values.Pattern;

public class DetermineNodeType implements NodeSetFilter {

	private final Logger logger = LoggerFactory.getLogger(DetermineNodeType.class);
	
	private boolean limitedToURIs       = false;
	private boolean limitedToBlankNodes = false;
	private boolean limitedToLiterals   = false;
	
	private RDFDatatype datatype = null;
	private String      language = null;
	
	public boolean isLimittedToURIs() {
		return limitedToURIs;
	}
		
	public RDFDatatype getDatatype() {
		return datatype;
	}
		
	public String getLanguage() {
		return language;
	}

	public boolean isLimittedToBlankNodes() {
		return limitedToBlankNodes;
	}
	
	public boolean isLimittedToLiterals() {
		return limitedToLiterals;
	}
	
	public void limitTo(Node node) {
		logger.debug("limitting to {}", node);

		if (node.isURI())
			limitedToURIs = true;
		else if (node.isLiteral())
			limitedToLiterals = true;
		else if (Var.isBlankNodeVar(node))
			limitedToBlankNodes = true;
	}

	public void limitToBlankNodes() {
		logger.debug("limitting to blank nodes");

		limitedToBlankNodes = true;
	}

	public void limitToEmptySet() {
		logger.warn("TODO DetermineNodeType.limitToEmptySet()");
	}

	public void limitToLiterals(String language, RDFDatatype datatype) {
		logger.debug("limitting to literals");
		
		limitedToLiterals = true;
		this.datatype = datatype;
		this.language = language;
	}

	public void limitToURIs() {
		logger.debug("limitting to URIs");
		
		limitedToURIs = true;
	}

	public void limitValues(String constant) {
		logger.warn("TODO DetermineNodeType.limitValues() {}", constant);
	}

	public void limitValuesToAttribute(Attribute attribute) {
		logger.warn("TODO DetermineNodeType.limitValuesToAttribute() {}", attribute);
	}

	public void limitValuesToBlankNodeID(BlankNodeID id) {
		logger.warn("TODO DetermineNodeType.limitValuesToBlankNodeID() {}", id);
	}

	public void limitValuesToExpression(Expression expression) {
		logger.warn("TODO DetermineNodeType.limitValuesToExpression() {}", expression);
	}

	public void limitValuesToPattern(Pattern pattern) {
		logger.warn("TODO DetermineNodeType.limitValuesToPattern() {}", pattern);
	}

}
