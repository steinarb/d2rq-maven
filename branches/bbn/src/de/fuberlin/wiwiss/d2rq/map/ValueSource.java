/*
 * $Id: ValueSource.java,v 1.1 2006/04/12 09:53:04 garbers Exp $
 */
package de.fuberlin.wiwiss.d2rq.map;

import java.util.Map;
import java.util.List;

/**
 * Describes a set of strings that are obtained in some way
 * from one or more database columns.
 * <p>
 * Typical implementations are {@link Column} (describing the set
 * of strings contained in one database column), {@link Pattern}
 * (describing the set of strings that is obtained by sticking
 * the values of several database fields into a string pattern),
 * and {@link BlankNodeIdentifier} (similar).
 * <p>
 * There are several other ValueSources that modify the behaviour
 * of another underlying ValueSource, implementing the Decorator
 * pattern. This includes {@link TranslationTable}.TranslatingValueSource
 * (translates values using a translation table or translation class)
 * and the various value restrictions (@link RegexRestriction et. al.).
 * <p>
 * ValueSources are used by {@link NodeMaker}s. A node maker
 * wraps the strings into Jena nodes, thus creating a description
 * of a set of RDF nodes.
 * <p>
 * TODO: Better name for ValueSource: ValueSetDescription?
 * 
 * <p>History:<br>
 * 2004-08-02: Initial version.<br>
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 */
public interface ValueSource {
    
	/**
	 * Checks if a given value fits this source without querying the
	 * database.
	 */
	boolean couldFit(String value);

	/**
	 * Returns a map of database fields and values corresponding
	 * to the argument.
	 * 
	 * <p>For example, a ValueSource that corresponds directly
	 * to a single DB column would return a single-entry map with that
	 * column as the key, and value as the value.
	 * 
	 * @param value a non-<tt>null</tt> value
	 * @return a map with {@link Column} keys, and string values.
	 */
	Map getColumnValues(String value);

	/**
	 * Returns a set of all columns containing data necessary
	 * for this ValueSource.
	 * @return a set of {Column}s
	 */
	List getColumns();

	/**
	 * Retrieves a value from a database row according to some rule or pattern.
	 * @param row the database row
	 * @param columnNameNumberMap a map from <tt>Table.Column</tt> style column
	 * 							names to Integer indices into the row array
	 * @return a value created from the row
	 */
	String getValue(String[] row, Map columnNameNumberMap);
}
