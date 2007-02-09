package de.fuberlin.wiwiss.d2rs;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.servlet.VelocityServlet;

import de.fuberlin.wiwiss.d2rq.GraphD2RQ;

public class RootServlet extends VelocityServlet {
	
	public Template handleRequest(HttpServletRequest request,
			HttpServletResponse response,
			Context context) throws IOException, ServletException {
		if (request.getPathInfo() != null) {
			response.sendError(404);
			return null;
		}
		Map classMapLinks = new TreeMap();
		Iterator it = graphD2RQ().classMapNames().iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			classMapLinks.put(name, D2RServer.instance().baseURI() + "directory/" + name);
		}
		context.put("truncated_results", new Boolean(D2RServer.instance().hasTruncatedResults()));
		context.put("server_name", D2RServer.instance().serverName());
		context.put("home_link", D2RServer.instance().baseURI());
		context.put("rdf_link", D2RServer.instance().baseURI() + "all");
		context.put("classmap_links", classMapLinks);
		response.addHeader("Content-Type", "application/xhtml+xml; charset=utf-8");
		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Pragma", "no-cache");
		setContentType(request, response);
		try {
			return getTemplate("root_page.vm");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private GraphD2RQ graphD2RQ() {
		return (GraphD2RQ) D2RServer.instance().currentGraph();
	}

	private static final long serialVersionUID = 8398973058486421941L;
}
