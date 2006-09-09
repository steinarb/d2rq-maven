package de.fuberlin.wiwiss.d2rq.map;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import de.fuberlin.wiwiss.d2rq.rdql.NodeConstraint;
import de.fuberlin.wiwiss.d2rq.sql.ResultRow;

/**
 * Restriction which can be chained with another {@link ValueSource} to state
 * that all its values match a certain regular expression. This is useful because the
 * query engine can exclude sources if a value doesn't match the expression.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: RegexRestriction.java,v 1.5 2006/09/09 23:25:14 cyganiak Exp $
 */
public class RegexRestriction implements ValueSource {
	private ValueSource valueSource;
	private Pattern regex;

	public RegexRestriction(ValueSource valueSource, String regex) {
		this.valueSource = valueSource;
		this.regex = Pattern.compile(regex);
	}

	public boolean couldFit(String value) {
		if (value == null) {
			return true;
		}
		return this.regex.matcher(value).matches() && this.valueSource.couldFit(value);
	}

	public Set getColumns() {
		return this.valueSource.getColumns();
	}

	public Map getColumnValues(String value) {
		return this.valueSource.getColumnValues(value);
	}

	public String getValue(ResultRow row) {
		return this.valueSource.getValue(row);
	}
	
	public void matchConstraint(NodeConstraint c) {
		this.valueSource.matchConstraint(c);
	}
}
