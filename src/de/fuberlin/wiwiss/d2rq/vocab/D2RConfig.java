/* CVS $Id: $ */
package de.fuberlin.wiwiss.d2rq.vocab; 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from file:doc/terms/config.ttl 
 * @author Auto-generated by schemagen on 01 Apr 2012 20:10 
 */
public class D2RConfig {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>Whether D2R Server should check for an updated mapping file with every request.</p> */
    public static final Property autoReloadMapping = m_model.createProperty( "http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#autoReloadMapping" );
    
    /** <p>Base URI for a D2R Server installation; the URI of the running server's start 
     *  page.</p>
     */
    public static final Property baseURI = m_model.createProperty( "http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#baseURI" );
    
    /** <p>A template resource whose properties will be attached as metadata to all RDF 
     *  documents published by a D2R Server installation.</p>
     */
    public static final Property documentMetadata = m_model.createProperty( "http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#documentMetadata" );
    
    /** <p>Maximum number of values for each property bridge that will be displayed in 
     *  the web interface when browsing resources.</p>
     */
    public static final Property limitPerPropertyBridge = m_model.createProperty( "http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#limitPerPropertyBridge" );
    
    /** <p>A RDF metadata template.</p> */
    public static final Property metadataTemplate = m_model.createProperty( "http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#metadataTemplate" );
    
    /** <p>The TCP port on which a D2R Server installation listens.</p> */
    public static final Property port = m_model.createProperty( "http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#port" );
    
    /** <p>The D2RQ-mapped database that is published by a D2R Server installation.</p> */
    public static final Property publishes = m_model.createProperty( "http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#publishes" );
    
    /** <p>Whether views of vocabulary resources should include instance data.</p> */
    public static final Property vocabularyIncludeInstances = m_model.createProperty( "http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#vocabularyIncludeInstances" );
    
    /** <p>A configuration for a D2R Server installation.</p> */
    public static final Resource Server = m_model.createResource( "http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#Server" );
    
}
