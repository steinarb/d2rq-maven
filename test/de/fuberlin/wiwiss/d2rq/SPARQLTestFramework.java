package de.fuberlin.wiwiss.d2rq;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class SPARQLTestFramework extends RDQLTestFramework {

	public SPARQLTestFramework(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	protected void sparql(String sparql) {
//	    rdqlLogger.debug("RDQL-Query: " + rdql);
		queryString=sparql;
		Query query = QueryFactory.create(sparql);
		QueryExecution qe = QueryExecutionFactory.create(query, this.model);
		this.results = new HashSet();
		ResultSet resultSet = qe.execSelect();
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();
			addSolution(solution);
		}
	}
	
	private void addSolution(QuerySolution solution) {
		Map map = new HashMap();
		Iterator it = solution.varNames();
		while (it.hasNext()) {
			String variable = (String) it.next();
			RDFNode value = solution.get(variable);
			map.put(variable, value);
		}
		this.results.add(map);
	}
}
