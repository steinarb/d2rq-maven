package de.fuberlin.wiwiss.d2rq.sql;

import junit.framework.TestCase;
import de.fuberlin.wiwiss.d2rq.algebra.Attribute;
import de.fuberlin.wiwiss.d2rq.algebra.Relation;
import de.fuberlin.wiwiss.d2rq.algebra.RelationName;
import de.fuberlin.wiwiss.d2rq.expr.Expression;
import de.fuberlin.wiwiss.d2rq.expr.SQLExpression;

/**
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: SQLBuildingTest.java,v 1.5 2008/04/25 15:21:28 cyganiak Exp $
 */
public class SQLBuildingTest extends TestCase {

	public void testSingleQuoteEscape() {
		ConnectedDB db = new DummyDB();
		assertEquals("'a'", db.singleQuote("a"));
		assertEquals("''''", db.singleQuote("'"));
		assertEquals("'\\\\'", db.singleQuote("\\"));
		assertEquals("'Joe''s'", db.singleQuote("Joe's"));
		assertEquals("'\\\\''\\\\''\\\\'", db.singleQuote("\\'\\'\\"));
		assertEquals("'\"'", db.singleQuote("\""));
		assertEquals("'`'", db.singleQuote("`"));
	}

	public void testSingleQuoteEscapeOracle() {
		ConnectedDB db = new DummyDB(ConnectedDB.Oracle);
		assertEquals("'a'", db.singleQuote("a"));
		assertEquals("''''", db.singleQuote("'"));
		assertEquals("'\\'", db.singleQuote("\\"));
		assertEquals("'Joe''s'", db.singleQuote("Joe's"));
		assertEquals("'\\''\\''\\'", db.singleQuote("\\'\\'\\"));
		assertEquals("'\"'", db.singleQuote("\""));
		assertEquals("'`'", db.singleQuote("`"));
	}

	public void testDoubleQuoteEscape() {
		ConnectedDB db = new DummyDB();
		assertEquals("\"a\"", db.doubleQuote("a"));
		assertEquals("\"'\"", db.doubleQuote("'"));
		assertEquals("\"\"\"\"", db.doubleQuote("\""));
		assertEquals("\"`\"", db.doubleQuote("`"));
		assertEquals("\"\\\"", db.doubleQuote("\\"));
		assertEquals("\"A \"\"good\"\" idea\"", db.doubleQuote("A \"good\" idea"));
	}

	public void testBacktickQuoteEscape() {
		ConnectedDB db = new DummyDB(ConnectedDB.MySQL);
		assertEquals("`a`", db.backtickQuote("a"));
		assertEquals("````", db.backtickQuote("`"));
		assertEquals("`\\\\`", db.backtickQuote("\\"));
		assertEquals("`Joe``s`", db.backtickQuote("Joe`s"));
		assertEquals("`\\\\``\\\\``\\\\`", db.backtickQuote("\\`\\`\\"));
		assertEquals("`'`", db.backtickQuote("'"));
	}

	public void testAttributeQuoting() {
		ConnectedDB db = new DummyDB();
		assertEquals("\"schema\".\"table\".\"column\"",
				db.quoteAttribute(new Attribute("schema", "table", "column")));
		assertEquals("\"table\".\"column\"",
				db.quoteAttribute(new Attribute(null, "table", "column")));
	}
	
	public void testDoubleQuotesInAttributesAreEscaped() {
		ConnectedDB db = new DummyDB();
		assertEquals("\"sch\"\"ema\".\"ta\"\"ble\".\"col\"\"umn\"",
				db.quoteAttribute(new Attribute("sch\"ema", "ta\"ble", "col\"umn")));
	}
	
	public void testAttributeQuotingMySQL() {
		ConnectedDB db = new DummyDB(ConnectedDB.MySQL);
		assertEquals("`table`.`column`",
				db.quoteAttribute(new Attribute(null, "table", "column")));
	}

	public void testRelationNameQuoting() {
		ConnectedDB db = new DummyDB();
		assertEquals("\"schema\".\"table\"",
				db.quoteRelationName(new RelationName("schema", "table")));
		assertEquals("\"table\"",
				db.quoteRelationName(new RelationName(null, "table")));
	}
	
	public void testBackticksInRelationsAreEscapedMySQL() {
		ConnectedDB db = new DummyDB(ConnectedDB.MySQL);
		assertEquals("`ta``ble`",
				db.quoteRelationName(new RelationName(null, "ta`ble")));
	}
	
	public void testRelationNameQuotingMySQL() {
		ConnectedDB db = new DummyDB(ConnectedDB.MySQL);
		assertEquals("`table`",
				db.quoteRelationName(new RelationName(null, "table")));
	}

	public void testEmptyBuilder() {
		SelectStatementBuilder builder = new SelectStatementBuilder(new DummyDB());
		assertNull(builder.getSQLStatement());
		assertTrue(builder.isTrivial());
	}
	
	public void testConditionWithNoSelectColumnsIsNotTrivial() {
		SelectStatementBuilder builder = new SelectStatementBuilder(new DummyDB());
		builder.addCondition(SQLExpression.create("foo.bar = 1"));
		assertFalse(builder.isTrivial());
	}

	public void testQueryWithSelectColumnsIsNotTrivial() {
		DummyDB db = new DummyDB();
		SelectStatementBuilder builder = new SelectStatementBuilder(db);
		builder.addRelation(Relation.createSimpleRelation(db, 
				new Attribute[]{new Attribute(null, "foo", "bar")}));
		assertFalse(builder.isTrivial());
	}
	
	public void testQueryWithFalseConditionIsEmpty() {
		DummyDB db = new DummyDB();
		SelectStatementBuilder builder = new SelectStatementBuilder(db);
		builder.addRelation(Relation.createSimpleRelation(db, 
				new Attribute[]{new Attribute(null, "foo", "bar")}));
		builder.addCondition(Expression.FALSE);
		assertNull(builder.getSQLStatement());
		assertTrue(builder.isEmpty());
	}
}
