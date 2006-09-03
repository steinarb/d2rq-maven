package de.fuberlin.wiwiss.d2rq.rdql;

import java.util.Iterator;

/**
 * @author jgarbers
 * @version $Id: ExpressionTest.java,v 1.6 2006/09/03 00:08:12 cyganiak Exp $
 */
public class ExpressionTest extends ExpressionTestFramework { 
    
	String getRDQLQuery(String condition) {
	    String triples=
	        "(?x, <http://annotation.semanticweb.org/iswc/iswc.daml#author>, ?z), " +
			"(?z, <http://annotation.semanticweb.org/iswc/iswc.daml#eMail> , ?y)";
	    String query="SELECT ?x, ?y WHERE " + triples + " AND " + condition;
	    return query;
	}
		
	void rdqlQuery() {
	    rdql(getRDQLQuery(condition));
	    if (translatedLogger.debugEnabled()) {
	        translatedLogger.debug(condition + " ->");
	        Iterator it=ExpressionTranslator.logSqlExpressions.iterator();
	        while (it.hasNext()) {
	            String translated=(String) it.next();
	            translatedLogger.debug(translated);
	        }
	        if (sqlCondition!=null)
	            assertTrue(ExpressionTranslator.logSqlExpressions.contains(sqlCondition));
	        // else
	        //    assertTrue(ExpressionTranslator.logSqlExpressions.size()==0);
	    }
	}

	public void testRDQLGetAuthorsAndEmailsWithCondition() {
	    // GraphD2RQ.setUsingD2RQQueryHandler(false);
	    condition="(?x eq ?z)";
	    //sqlCondition=""
		rdqlQuery();
	}
	public void testNobody() {
	    condition="(?z != \"Nobody\")";
		rdqlQuery();
	}
	public void testIsSeaborne() {
	    //condition="?z eq \"http://www-uk.hpl.hp.com/people#andy_seaborne\"";
	    condition="(?z eq \"http://www-uk.hpl.hp.com/people#andy_seaborne\")";
	    rdqlQuery();
	}
	public void testEasy() {
	    condition="(1 == 1)";
		rdqlQuery();
	}
	public void testImpossible() {
	    condition="! (1 == 1)";
		rdqlQuery();
	}
	public void testAnd() {
	    condition="(1 == 1) && true";
		rdqlQuery();
	}

}
