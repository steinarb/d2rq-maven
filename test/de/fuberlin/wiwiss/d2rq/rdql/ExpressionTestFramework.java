package de.fuberlin.wiwiss.d2rq.rdql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import de.fuberlin.wiwiss.d2rq.GraphD2RQ;
import de.fuberlin.wiwiss.d2rq.ModelD2RQ;
import de.fuberlin.wiwiss.d2rq.RDQLTestFramework;
import de.fuberlin.wiwiss.d2rq.SPARQLTestFramework;
import de.fuberlin.wiwiss.d2rq.find.SQLResultSet;
import de.fuberlin.wiwiss.d2rq.helpers.Logger;
import de.fuberlin.wiwiss.d2rq.map.Database;

public class ExpressionTestFramework extends SPARQLTestFramework {
    String condition, sqlCondition=null;
    Logger translatedLogger=new Logger();

    public ExpressionTestFramework(String arg0) {
        super(arg0);
    }

    protected static void activateExpressionTranslator(ModelD2RQ model) {
    	Map dbMap=((GraphD2RQ)model.getGraph()).getPropertyBridgesByDatabase();
    	Iterator it=dbMap.keySet().iterator();
    	while (it.hasNext()) {
    		Database db=(Database)it.next();
    		String tr;
    		tr=ExpressionTranslator.class.getName();
    		//tr=MySQLExpressionTranslator.class.getName();
    		db.setExpressionTranslator(tr);
    	}
    }
    	
	protected void setUp() throws Exception {
		super.setUp();
		ExpressionTranslator.logger.setDebug(true);
		sqlResultSetLogger.setDebug(false); // true
		rdqlLogger.setDebug(false); // true
		translatedLogger.setDebug(true);
		if (translatedLogger.debugEnabled())
		    ExpressionTranslator.logSqlExpressions=new HashSet();
		SQLResultSet.simulationMode=true;
		setUpMixOutputs(false);
		GraphD2RQ.setUsingD2RQQueryHandler(true);
		SQLResultSet.protocol=new ArrayList();
		activateExpressionTranslator(model);
	}

}
