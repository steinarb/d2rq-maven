/* CVS $Id: D2RQ.java,v 1.14 2009/07/29 12:03:53 fatorange Exp $ */
package de.fuberlin.wiwiss.d2rq.vocab; 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from file:doc/specification/d2rq-rdfs.n3 
 * @author Auto-generated by schemagen on 29 Jul 2009 13:51 
 */
public class D2RQ {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>An additional property to be served for all associated class definitions</p> */
    public static final Property additionalClassDefinitionProperty = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#additionalClassDefinitionProperty" );
    
    public static final Property additionalProperty = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#additionalProperty" );
    
    /** <p>An additional property to be served for all associated property definitions</p> */
    public static final Property additionalPropertyDefinitionProperty = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#additionalPropertyDefinitionProperty" );
    
    public static final Property alias = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#alias" );
    
    /** <p>Value: true/false that describe the databases ability to handle DISTINCT correctly. 
     *  (e.g. MSAccess cuts fields longer than 256 chars)</p>
     */
    public static final Property allowDistinct = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#allowDistinct" );
    
    /** <p>Comma-separated list of database columns used for construction of blank nodes.</p> */
    public static final Property bNodeIdColumns = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#bNodeIdColumns" );
    
    public static final Property belongsToClassMap = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#belongsToClassMap" );
    
    /** <p>Links d2rq:classMaps to RDFS or OWL classes.</p> */
    public static final Property class__ = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#class" );
    
    /** <p>A comment to be served as rdfs:comment for all associated class definitions</p> */
    public static final Property classDefinitionComment = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#classDefinitionComment" );
    
    /** <p>A label to be served as rdfs:label for all associated class definitions</p> */
    public static final Property classDefinitionLabel = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#classDefinitionLabel" );
    
    /** <p>Used to link RDFS or OWL classes to d2r:classMaps.</p> */
    public static final Property classMap = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#classMap" );
    
    public static final Property column = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#column" );
    
    /** <p>SQL WHERE condition that must be satisfied for a database row to be mapped.</p> */
    public static final Property condition = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#condition" );
    
    /** <p>A constant RDF node to be used as the value of this property bridge, or as 
     *  the resource of a singleton ClassMap.</p>
     */
    public static final Property constantValue = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#constantValue" );
    
    public static final Property containsDuplicates = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#containsDuplicates" );
    
    public static final Property dataStorage = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#dataStorage" );
    
    public static final Property databaseValue = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#databaseValue" );
    
    /** <p>The datatype of literals created by this bridge.</p> */
    public static final Property datatype = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#datatype" );
    
    public static final Property dateColumn = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#dateColumn" );
    
    /** <p>Links a d2rq:PropertyBridge to a dynamic property.</p> */
    public static final Property dynamicProperty = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#dynamicProperty" );
    
    /** <p>The number of rows that should be fetched from the database at once</p> */
    public static final Property fetchSize = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#fetchSize" );
    
    /** <p>Link to a translation table in an external CSV file.</p> */
    public static final Property href = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#href" );
    
    /** <p>Qualified name of a Java class that implements de.fuberlin.wiwiss.d2rq.Translator 
     *  and translates between database and RDF.</p>
     */
    public static final Property javaClass = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#javaClass" );
    
    public static final Property jdbcDSN = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#jdbcDSN" );
    
    public static final Property jdbcDriver = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#jdbcDriver" );
    
    /** <p>SQL join condition over tables in the database.</p> */
    public static final Property join = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#join" );
    
    /** <p>The language tag of literals created by this bridge.</p> */
    public static final Property lang = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#lang" );
    
    /** <p>The number of results to retrieve from the database for this PropertyBridge</p> */
    public static final Property limit = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#limit" );
    
    /** <p>The number of results to retrieve from the database for the inverse statements 
     *  for this PropertyBridge</p>
     */
    public static final Property limitInverse = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#limitInverse" );
    
    /** <p>URL of a D2RQ mapping file.</p> */
    public static final Property mappingFile = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#mappingFile" );
    
    public static final Property numericColumn = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#numericColumn" );
    
    public static final Property odbcDSN = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#odbcDSN" );
    
    /** <p>The column after which to sort results in ascending order for this PropertyBridge</p> */
    public static final Property orderAsc = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#orderAsc" );
    
    /** <p>The column after which to sort results in descending order for this PropertyBridge</p> */
    public static final Property orderDesc = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#orderDesc" );
    
    public static final Property password = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#password" );
    
    public static final Property pattern = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#pattern" );
    
    /** <p>Links a d2rq:PropertyBridge to an RDF property.</p> */
    public static final Property property = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#property" );
    
    /** <p>Used for linking RDFS properties to D2R property bridges.</p> */
    public static final Property propertyBridge = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#propertyBridge" );
    
    /** <p>A comment to be served as rdfs:comment for all associated properties</p> */
    public static final Property propertyDefinitionComment = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#propertyDefinitionComment" );
    
    /** <p>A label to be served as rdfs:label for all associated properties</p> */
    public static final Property propertyDefinitionLabel = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#propertyDefinitionLabel" );
    
    public static final Property propertyName = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#propertyName" );
    
    public static final Property propertyValue = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#propertyValue" );
    
    public static final Property rdfValue = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#rdfValue" );
    
    /** <p>Has to be used if a join refers to a different classMap.</p> */
    public static final Property refersToClassMap = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#refersToClassMap" );
    
    /** <p>Base URI for resources generated by relative URI patterns.</p> */
    public static final Property resourceBaseURI = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#resourceBaseURI" );
    
    /** <p>Enforced upper limit for the size of SQL result sets.</p> */
    public static final Property resultSizeLimit = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#resultSizeLimit" );
    
    /** <p>Whether to serve inferred and user-supplied vocabulary data</p> */
    public static final Property serveVocabulary = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#serveVocabulary" );
    
    /** <p>A SQL expression whose result will be the value of this property bridge.</p> */
    public static final Property sqlExpression = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#sqlExpression" );
    
    public static final Property textColumn = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#textColumn" );
    
    public static final Property timestampColumn = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#timestampColumn" );
    
    public static final Property translateWith = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#translateWith" );
    
    public static final Property translation = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#translation" );
    
    /** <p>Database column which contains URIs.</p> */
    public static final Property uriColumn = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#uriColumn" );
    
    /** <p>URI pattern with placeholders that will be filled with values from a database 
     *  column.</p>
     */
    public static final Property uriPattern = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#uriPattern" );
    
    /** <p>An SQL expression whose result will be the URI value of this property bridge.</p> */
    public static final Property uriSqlExpression = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#uriSqlExpression" );
    
    public static final Property username = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#username" );
    
    /** <p>Optimizing hint: a string contained in every value of this PropertyBridge.</p> */
    public static final Property valueContains = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#valueContains" );
    
    /** <p>Optimizing hint: the maximum length of values of this PropertyBridge.</p> */
    public static final Property valueMaxLength = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#valueMaxLength" );
    
    /** <p>Optimizing hint: a regular expression matching every value of this PropertyBridge.</p> */
    public static final Property valueRegex = m_model.createProperty( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#valueRegex" );
    
    /** <p>Represents an additional property that may be added to instances as well as 
     *  class and property definitions.</p>
     */
    public static final Resource AdditionalProperty = m_model.createResource( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#AdditionalProperty" );
    
    /** <p>Maps an RDFS or OWL class to its database representation.</p> */
    public static final Resource ClassMap = m_model.createResource( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#ClassMap" );
    
    /** <p>Represents general settings.</p> */
    public static final Resource Configuration = m_model.createResource( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#Configuration" );
    
    /** <p>Jena Assemler specification for a relational database, mapped to RDF using 
     *  the D2RQ tool.</p>
     */
    public static final Resource D2RQModel = m_model.createResource( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#D2RQModel" );
    
    /** <p>Represents a database.</p> */
    public static final Resource Database = m_model.createResource( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#Database" );
    
    /** <p>(Deprecated) Maps a datatype property to one or more database columns.</p> */
    public static final Resource DatatypePropertyBridge = m_model.createResource( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#DatatypePropertyBridge" );
    
    /** <p>(Deprecated) Maps an object property to one or more database columns.</p> */
    public static final Resource ObjectPropertyBridge = m_model.createResource( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#ObjectPropertyBridge" );
    
    /** <p>Maps an RDF property to one or more database columns.</p> */
    public static final Resource PropertyBridge = m_model.createResource( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#PropertyBridge" );
    
    /** <p>A database-to-RDF mapping from one or more database columns to a set of RDF 
     *  resources.</p>
     */
    public static final Resource ResourceMap = m_model.createResource( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#ResourceMap" );
    
    /** <p>Translation Key/Value Pair.</p> */
    public static final Resource Translation = m_model.createResource( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#Translation" );
    
    /** <p>Lookup table for translations used in the mapping process.</p> */
    public static final Resource TranslationTable = m_model.createResource( "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#TranslationTable" );
    
}
