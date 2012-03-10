package de.fuberlin.wiwiss.d2rq.dbschema;

/**
 * Represents a column type obtained from {@link java.sql.DatabaseMetaData#getColumns(String, String, String, String)} 
 * in both integer and string forms.
 * 
 * TODO: Merge with ConnectedDB.ColumnType
 *
 * @author Christian Becker <http://beckr.org#chris>
 */
public class ColumnType {
	
	/**
	 * Constant indicating an unmappable column type. A column type is
	 * unmappable if no reasonable RDF literal representation of its
	 * value is known.
	 */
	public final static String UNMAPPABLE = "UNMAPPABLE";
	
	private int typeId;
	private String typeName;

	ColumnType(int typeId, String typeName) {
		this.typeId = typeId;
		this.typeName = typeName;
	}
	
	public int typeId() {
		return this.typeId;
	}

	public String typeName() {
		return this.typeName;
	}
}	