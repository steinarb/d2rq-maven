package org.d2rq.lang;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.d2rq.D2RQException;
import org.d2rq.ResourceCollection;
import org.d2rq.algebra.DownloadRelation;
import org.d2rq.algebra.TripleRelation;
import org.d2rq.db.SQLConnection;
import org.d2rq.db.SQLScriptLoader;
import org.d2rq.db.expr.ColumnListEquality;
import org.d2rq.db.op.DatabaseOp;
import org.d2rq.db.op.InnerJoinOp;
import org.d2rq.db.op.LimitOp;
import org.d2rq.db.op.OpVisitor;
import org.d2rq.db.op.ProjectOp;
import org.d2rq.db.op.ProjectionSpec;
import org.d2rq.db.op.ProjectionSpec.ColumnProjectionSpec;
import org.d2rq.db.op.SelectOp;
import org.d2rq.db.op.TableOp;
import org.d2rq.db.schema.ColumnName;
import org.d2rq.db.types.DataType;
import org.d2rq.db.types.DataType.GenericType;
import org.d2rq.nodes.FixedNodeMaker;
import org.d2rq.nodes.NodeMaker;
import org.d2rq.nodes.TypedNodeMaker;
import org.d2rq.values.ValueMaker;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;


public class D2RQCompiler implements D2RQMappingVisitor {
	private static final Log log = LogFactory.getLog(CompiledD2RQMapping.class);

	private final Mapping mapping;
	private final CompiledD2RQMapping result;
	private final Map<Resource,SQLConnection> sqlConnections =
		new HashMap<Resource,SQLConnection>();
	private final Collection<SQLConnection> overriddenSQLConnections =
		new ArrayList<SQLConnection>();
	private final Collection<TripleRelation> currentClassMapTripleRelations =
		new ArrayList<TripleRelation>();
	private Map<ColumnName,GenericType> overriddenColumnTypes = null;
	private NodeMaker currentSubjects = null;
	private SQLConnection currentSQLConnection = null;
	private TabularBuilder currentClassMapRelationBuilder = null;
	private boolean done = false;
	
	public D2RQCompiler(Mapping mapping) {
		this.mapping = mapping;
		result = new CompiledD2RQMapping();
	}
	
	public CompiledD2RQMapping getResult() {
		if (!done) {
			mapping.accept(this);
		}
		return result;
	}
	
	/**
	 * Allows injection of an existing {@link SQLConnection}. When a
	 * connection to this one's JDBC URL is needed, then the connection
	 * details specified in the mapping will be ignored, and instead this
	 * one will be used.
	 */
	public void useConnection(SQLConnection sqlConnection) {
		overriddenSQLConnections.add(sqlConnection);
	}
	
	public boolean visitEnter(Mapping mapping) {
		result.setPrefixes(mapping.getPrefixes());
		D2RQValidator.validate(mapping);
		return true;
	}

	public void visitLeave(Mapping mapping) {
		for (TripleRelation t: result.getTripleRelations()) {
			checkColumns(t.getBaseTabular());
		}
		log.info("Compiled " + result.getTripleRelations().size() + " property bridges");
		if (log.isDebugEnabled()) {
			for (TripleRelation rel: result.getTripleRelations()) {
				log.debug(rel);
			}
		}
	}

	public void visit(Configuration configuration) {
		result.setFastMode(configuration.getUseAllOptimizations());
		if (configuration.getServeVocabulary()) {
			result.setAdditionalTriples(mapping.getVocabularyModel().getGraph());
		}
	}

	public void visit(Database database) {
		SQLConnection sqlConnection = null;
		for (SQLConnection conn: overriddenSQLConnections) {
			if (conn.getJdbcURL().equals(database.getJdbcURL())) {
				sqlConnection = conn;
			}
		}
		if (sqlConnection == null && mapping.databases().size() == 1 && overriddenSQLConnections.size() == 1) {
			sqlConnection = overriddenSQLConnections.iterator().next();
		}
		if (sqlConnection == null && database.getJdbcURL() == null) {
			throw new D2RQException(database + 
					" must have d2rq:jdbcURL",
					D2RQException.DATABASE_MISSING_JDBC_URL);

		}
		if (sqlConnection == null) {
			sqlConnection = new SQLConnection(database.getJdbcURL(),
					database.getJDBCDriver(), database.getUsername(),
					database.getPassword(), database.getConnectionProperties());
			if (database.getStartupSQLScript() != null) {
				try {
					URI url = URI.create(database.getStartupSQLScript());
					SQLScriptLoader.loadURI(url, sqlConnection.connection());
				} catch (IOException ex) {
					sqlConnection.close();
					throw new D2RQException(ex);
				} catch (SQLException ex) {
					sqlConnection.close();
					throw new D2RQException(ex);
				}
			}
		}
		sqlConnection.setFetchSize(database.getFetchSize());
		sqlConnection.setLimit(database.getResultSizeLimit());
		overriddenColumnTypes = database.getColumnTypes();
		sqlConnection.connection();
		sqlConnections.put(database.resource(), sqlConnection);
		result.addSQLConnection(sqlConnection);
		for (ColumnName column: overriddenColumnTypes.keySet()) {
			TableOp table = sqlConnection.getTable(column.getQualifier());
			if (table == null || !table.hasColumn(column)) {
				throw new D2RQException(
						database + ": Datatype-overridden column does not exist: " + 
								column, D2RQException.SQL_COLUMN_NOT_FOUND);
			}
		}
	}

	public boolean visitEnter(ClassMap classMap) {
		currentClassMapTripleRelations.clear();
		currentSQLConnection = sqlConnections.get(classMap.getDatabase().resource());
		currentSubjects = createNodeMaker(classMap);
		currentClassMapRelationBuilder = createRelationBuilder(classMap);
		currentClassMapRelationBuilder.addProjections(currentSubjects.projectionSpecs());
		return true;
	}

	public void visitLeave(ClassMap classMap) {
		DatabaseOp tabular = currentClassMapRelationBuilder.getTabular();
		for (Resource class_: classMap.getClasses()) {
			NodeMaker predicates = new FixedNodeMaker(RDF.type.asNode());
			NodeMaker objects = new FixedNodeMaker(class_.asNode());
			currentClassMapTripleRelations.add(new TripleRelation(currentSQLConnection,
					tabular, currentSubjects, predicates, objects));
		}
		result.addResourceCollection(classMap.resource().getLocalName(), 
				new ResourceCollection(result, currentSQLConnection, currentSubjects,
						tabular, currentClassMapTripleRelations));
		for (TripleRelation triples: currentClassMapTripleRelations) {
			result.addTripleRelation(triples);
		}
	}

	public void visit(PropertyBridge propertyBridge) {
		TabularBuilder builder = createRelationBuilder(propertyBridge);
		if (propertyBridge.getRefersToClassMap() != null) {
			builder.addAliasedRelationBuilder(createRelationBuilder(propertyBridge.getRefersToClassMap()));
		}
		builder.addRelationBuilder(currentClassMapRelationBuilder);
		if (propertyBridge.getLimit() != LimitOp.NO_LIMIT) {
			builder.setLimit(propertyBridge.getLimit());
		}
		if (propertyBridge.getLimitInverse() != LimitOp.NO_LIMIT) {
			builder.setLimitInverse(propertyBridge.getLimitInverse());
		}
		if (propertyBridge.getOrder() != null) {
			builder.setOrderColumn(Microsyntax.parseColumn(propertyBridge.getOrder())); 
			builder.setOrderDesc(propertyBridge.getOrderDesc().booleanValue());
		}
		NodeMaker objects = createNodeMaker(propertyBridge);
		builder.addProjections(objects.projectionSpecs());
		DatabaseOp tabular = builder.getTabular();
		for (Resource property: propertyBridge.getProperties()) {
			NodeMaker predicates = new FixedNodeMaker(property.asNode());
			currentClassMapTripleRelations.add(new TripleRelation(
					currentSQLConnection, tabular, 
					currentSubjects, predicates, objects));
		}
		for (String pattern: propertyBridge.getDynamicPropertyPatterns()) {
			NodeMaker predicates = new TypedNodeMaker(
					TypedNodeMaker.URI, new Pattern(pattern).toTemplate(currentSQLConnection));
			TabularBuilder dynamicBuilder = new TabularBuilder(
					currentSQLConnection, overriddenColumnTypes);
			dynamicBuilder.addRelationBuilder(builder);
			dynamicBuilder.addProjections(predicates.projectionSpecs());
			currentClassMapTripleRelations.add(new TripleRelation(
					currentSQLConnection, dynamicBuilder.getTabular(), 
					currentSubjects, predicates, objects));
		}
	}

	public void visit(DownloadMap downloadMap) {
		currentSQLConnection = sqlConnections.get(
				downloadMap.getDatabaseFromHereOrClassMap().resource()); 
		String mediaType = downloadMap.getMediaType();
		ValueMaker mediaTypeMaker = mediaType == null ? ValueMaker.NULL : new Pattern(mediaType).toTemplate(currentSQLConnection);
		NodeMaker nodeMaker = createNodeMaker(downloadMap);
		TabularBuilder builder = createRelationBuilder(downloadMap);
		builder.addProjections(nodeMaker.projectionSpecs());
		builder.addProjection(ColumnProjectionSpec.create(
				downloadMap.getContentDownloadColumn()));
		builder.addProjections(mediaTypeMaker.projectionSpecs());
		if (downloadMap.getBelongsToClassMap() != null) {
			builder.addRelationBuilder(createRelationBuilder(downloadMap.getBelongsToClassMap()));
		}
		result.addDownloadRelation(new DownloadRelation(
				currentSQLConnection,
				builder.getTabular(),
				nodeMaker,
				mediaTypeMaker,
				downloadMap.getContentDownloadColumn()));
	}

	public void visit(TranslationTable translationTable) {
		// Translation tables don't need compiling.
	}

	private TabularBuilder createRelationBuilder(ResourceMap resourceMap) {
		TabularBuilder result = new TabularBuilder(
				currentSQLConnection, overriddenColumnTypes);
		result.addJoinExpressions(resourceMap.getJoins());
		result.addConditions(resourceMap.getConditions());
		result.addAliasDeclarations(resourceMap.getAliasesParsed());
		result.setContainsDuplicates(resourceMap.getContainsDuplicates());
		return result;
	}
	
	private NodeMaker createNodeMaker(ResourceMap map) {
		return new NodeMakerFactory(currentSQLConnection, mapping.getBaseURI()).createFrom(map);
	}
	
	private void checkColumns(final DatabaseOp op) {
		op.accept(new OpVisitor.Default(true) {
			@Override
			public boolean visitEnter(InnerJoinOp table) {
				for (ColumnListEquality join: table.getJoinConditions()) {
					check(join.getColumns(), table);
				}
				return true;
			}
			@Override
			public boolean visitEnter(SelectOp table) {
				check(table.getCondition().getColumns(), table.getWrapped());
				return true;
			}
			@Override
			public boolean visitEnter(ProjectOp table) {
				for (ProjectionSpec p: table.getProjections()) {
					check(p.getColumns(), table.getWrapped());
					for (ColumnName column: p.getColumns()) {
						DataType datatype = table.getWrapped().getColumnType(column);
						if (datatype == null) {
							throw new D2RQException(
									"Column " + column + " has unknown datatye. Use d2rq:xxxColumn to override its type.",
									D2RQException.DATATYPE_UNKNOWN);
						}
						if (datatype.isUnsupported()) {
							throw new D2RQException(
									"Column " + column + " has unsupported datatype: " + datatype,
									D2RQException.DATATYPE_UNMAPPABLE);
						}						
					}
				}
				return true;
			}
			private void check(Set<ColumnName> columns, DatabaseOp op) {
				for (ColumnName column: columns) {
					if (!op.hasColumn(column)) {
						throw new D2RQException(
								"Column used in mapping not found: " + column, 
								D2RQException.SQL_COLUMN_NOT_FOUND);
					}
				}
			}
		});
	}
}
