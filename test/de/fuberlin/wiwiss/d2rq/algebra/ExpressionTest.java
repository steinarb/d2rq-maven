package de.fuberlin.wiwiss.d2rq.algebra;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: ExpressionTest.java,v 1.5 2006/09/15 12:25:25 cyganiak Exp $
 */
public class ExpressionTest extends TestCase {

	public void testCreateWithoutExpression() {
		Expression e = new Expression(Collections.EMPTY_LIST);
		assertEquals("Expression(1)", e.toString());
		assertTrue(e.isTrue());
	}
	
	public void testCreateWithOneExpression() {
		Expression e = new Expression("papers.publish = 1");
		assertEquals("Expression(papers.publish = 1)", e.toString());
		assertFalse(e.isTrue());
	}
	
	public void testCreateWithSingletonExpressionSet() {
		Expression e = new Expression(Collections.singletonList("papers.publish = 1"));
		assertEquals("Expression(papers.publish = 1)", e.toString());
		assertFalse(e.isTrue());
	}
	
	public void testCreateWithMultipleExpressions() {
		Expression e = new Expression(Arrays.asList(
				new String[]{"papers.publish = 1", "papers.rating > 4"}));
		assertEquals("Expression(papers.publish = 1 AND papers.rating > 4)", e.toString());		
		assertFalse(e.isTrue());
	}
	
	public void testFindsColumns() {
		Expression e = new Expression(Arrays.asList(
				new String[]{"papers.publish = 1", "papers.rating > 4"}));
		Set expectedColumns = new HashSet(Arrays.asList(
				new Attribute[]{new Attribute("papers.publish"), new Attribute("papers.rating")}));
		assertEquals(expectedColumns, e.columns());
	}
	
	public void testToString() {
		Expression e = new Expression(Collections.EMPTY_LIST);
		assertEquals("Expression(1)", e.toString());
		e = new Expression(Collections.singletonList("papers.publish = 1"));
		assertEquals("Expression(papers.publish = 1)", e.toString());
		e = new Expression(Arrays.asList(
				new String[]{"papers.publish = 1", "papers.rating > 4"}));
		assertEquals("Expression(papers.publish = 1 AND papers.rating > 4)", e.toString());		
	}
	
	public void testTwoExpressionsAreEqual() {
		assertEquals(new Expression("1"), new Expression("1"));
		assertEquals(new Expression("1").hashCode(), new Expression("1").hashCode());
	}
	
	public void testTwoExpressionsAreNotEqual() {
		assertFalse(new Expression("1").equals(new Expression("2")));
		assertFalse(new Expression("1").hashCode() == new Expression("2").hashCode());
	}
	
	public void testRenameColumnsWithAliasMap() {
		Map map = new HashMap();
		map.put(new RelationName(null, "bar"), new RelationName(null, "foo"));
		assertEquals(new Expression("bar.col1 = baz.col1"),
				new Expression("foo.col1 = baz.col1").renameColumns(new AliasMap(map)));
	}
	
	public void testRenameColumnsWithColumnReplacer() {
		Map map = new HashMap();
		map.put(new Attribute("foo.col1"), new Attribute("foo.col2"));
		assertEquals(new Expression("foo.col2=foo.col3"), 
				new Expression("foo.col1=foo.col3").renameColumns(new ColumnRenamerMap(map)));
	}
	
	public void testApplyAnd() {
		assertEquals(new Expression("t1.c1 = 1 AND t2.c2 = 2"),
				new Expression("t1.c1 = 1").and(new Expression("t2.c2 = 2")));
	}
	
	public void testFooAndTrueIsFoo() {
		assertEquals(new Expression("foo"), new Expression("foo").and(Expression.TRUE));
	}
	
	public void testTrueAndFooIsFoo() {
		assertEquals(new Expression("foo"), Expression.TRUE.and(new Expression("foo")));
	}
}
