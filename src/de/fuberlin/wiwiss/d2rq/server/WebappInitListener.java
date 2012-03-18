package de.fuberlin.wiwiss.d2rq.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.fuberlin.wiwiss.d2rq.SystemLoader;

/**
 * Initialize D2R server on startup of an appserver such as Tomcat. This listener should
 * be included in the web.xml. This is compatible with Servlet 2.3 spec compliant appservers.
 *
 * @author Inigo Surguy
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class WebappInitListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();

		// Is there already a loader in the servlet context, put there
		// by the JettyLauncher?
		SystemLoader loader = D2RServer.retrieveFromContext(context);
		if (loader == null) {
			// We are running as pure webapp, without JettyLauncher,
			// so initalize the loader from the configFile context parameter.
			loader = new SystemLoader();
			D2RServer.storeInContext(loader, context);
			if (context.getInitParameter("configFile") == null) {
				throw new RuntimeException("No configFile configured in web.xml");
			}
			String configFileName = absolutize(context.getInitParameter("configFile"), context);
			loader.setMappingURL(configFileName);
			loader.setResourceStem("resource/");
		}
		D2RServer server = loader.getD2RServer();
		server.start();
		server.putIntoServletContext(context);
		VelocityWrapper.initEngine(server, context);
	}

	public void contextDestroyed(ServletContextEvent event) {
		D2RServer server = D2RServer.fromServletContext(event.getServletContext());
		if (server != null)
			server.shutdown();
	}
	
	private String absolutize(String fileName, ServletContext context) {
		if (!fileName.matches("[a-zA-Z0-9]+:.*")) {
			fileName = context.getRealPath("WEB-INF/" + fileName);
		}
		return ConfigLoader.toAbsoluteURI(fileName);
	}
}
