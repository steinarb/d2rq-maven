package de.fuberlin.wiwiss.d2rs;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joseki.RDFServer;
import org.joseki.Registry;
import org.joseki.Service;
import org.joseki.ServiceRegistry;
import org.joseki.processors.SPARQL;

import com.hp.hpl.jena.query.describe.DescribeHandlerRegistry;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.fuberlin.wiwiss.d2rq.ModelD2RQ;

/**
 * A D2R Server instance. Sets up a service, loads the D2RQ model, and starts Joseki.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: D2RServer.java,v 1.5 2006/08/31 14:53:22 cyganiak Exp $
 */
public class D2RServer {
	private static D2RServer instance = null;
	private static String SPARQLServiceName = "sparql";
	private static String resourceServiceName = "resource";
	private static String defaultBaseURI = "http://localhost";
	
	public static D2RServer instance() {
		if (D2RServer.instance == null) {
			D2RServer.instance = new D2RServer();
		}
		return D2RServer.instance;
	}
	
	private int port = 2020;
	private String baseURI = null;
	private Model model = null;
	private NamespacePrefixModel prefixesModel;
	private Log log = LogFactory.getLog(D2RServer.class);
	
	private D2RServer() {
		// private to enforce singleton
	}
	
	public void setPort(int port) {
		log.info("using port " + port);
		this.port = port;
	}

	public void setBaseURI(String baseURI) {
		if (!baseURI.endsWith("/")) {
			baseURI += "/";
		}
		log.info("using custom base URI: " + baseURI);
		this.baseURI = baseURI;
	}
	
	public String baseURI() {
		if (this.baseURI == null) {
			return D2RServer.defaultBaseURI + ":" + this.port + "/";
		}
		return this.baseURI;
	}
	
	public String resourceBaseURI() {
		return this.baseURI() + D2RServer.resourceServiceName + "/";
	}
	
	public String graphURLDescribingResource(String resourceURI) {
		if (resourceURI.indexOf(":") == -1) {
			resourceURI = resourceBaseURI() + resourceURI;
		}
		String query = "DESCRIBE <" + resourceURI + ">";
		try {
			return this.baseURI() + D2RServer.SPARQLServiceName + "?query=" + URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public Model model() {
		return this.model;
	}

	public ModelD2RQ reloadModelD2RQ(String mappingFileURL) {
		Model mapModel = ModelFactory.createDefaultModel();
		mapModel.read(mappingFileURL, resourceBaseURI(), "N3");
		return new ModelD2RQ(mapModel, resourceBaseURI());
	}
	
	public void initFromMappingFile(String mappingFileURL) {
		log.info("using mapping file: " + mappingFileURL);
		if (mappingFileURL.startsWith("file://")) {
			initAutoReloading(mappingFileURL.substring(7));
			return;
		}
		if (mappingFileURL.startsWith("file:")) {
			initAutoReloading(mappingFileURL.substring(5));
			return;
		}
		if (mappingFileURL.indexOf(":") == -1) {
			initAutoReloading(mappingFileURL);
			return;
		}
		this.model = reloadModelD2RQ(mappingFileURL);
		this.prefixesModel = new NamespacePrefixModel();
		this.prefixesModel.update(this.model);
	}

	private void initAutoReloading(String filename) {
		AutoReloader reloader = new AutoReloader(new File(filename));
		this.model = ModelFactory.createModelForGraph(reloader);
		DescribeHandlerRegistry.get().clear();
		DescribeHandlerRegistry.get().add(new FindDescribeHandler(this.model));
		this.prefixesModel = new NamespacePrefixModel();		
		reloader.setPrefixModel(this.prefixesModel);
		reloader.forceReload();
	}
	
	public void start() {
		Registry.add(RDFServer.ServiceRegistryName, createJosekiServiceRegistry());
		new RDFServer(null, this.port).start();
		log.info("[[[ Server started at " + baseURI() + " ]]]");
	}
	
	protected ServiceRegistry createJosekiServiceRegistry() {
		ServiceRegistry services = new ServiceRegistry();
		Service service = createJosekiService();
		services.add(D2RServer.SPARQLServiceName, service);
		return services;
	}
	
	protected Service createJosekiService() {
		return new Service(new SPARQL(), D2RServer.SPARQLServiceName, new D2RQDatasetDesc(this.model, this.prefixesModel));
	}
	
	protected void checkIfModelWorks() {
		log.info("verifying mapping file ...");
		this.model.isEmpty();
		log.info("--------------------");
	}
}
