package de.fuberlin.wiwiss.d2rq.sql;

import de.fuberlin.wiwiss.d2rq.algebra.RelationName;
import de.fuberlin.wiwiss.d2rq.expr.Expression;
import de.fuberlin.wiwiss.d2rq.expr.SQLExpression;
import de.fuberlin.wiwiss.d2rq.map.Database;

/**
 * This syntax class implements MySQL-compatible SQL syntax.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: OracleSyntax.java,v 1.1 2009/09/29 19:56:53 cyganiak Exp $
 */
public class OracleSyntax extends SQL92Syntax {

	public String getRelationNameAliasExpression(RelationName relationName,
			RelationName aliasName) {
		return quoteRelationName(relationName) + " " + quoteRelationName(aliasName);
	}
	
	public Expression getRowNumLimitAsExpression(int limit) {
		if (limit == Database.NO_LIMIT) return Expression.TRUE;
		return SQLExpression.create("ROWNUM <= " + limit);
	}

	public String getRowNumLimitAsQueryAppendage(int limit) {
		return "";
	}
}
