/*
 * $Id: TranslationTableTest.java,v 1.1 2004/08/02 22:48:44 cyganiak Exp $
 */
package de.fuberlin.wiwiss.d2rq;

import com.hp.hpl.jena.graph.Node;

import junit.framework.TestCase;

/**
 * Tests the TranslationTable functionality
 *
 * @author Richard Cyganiak <richard@cyganiak.de>
 */
public class TranslationTableTest extends TestCase {

	public TranslationTableTest(String arg) {
		super(arg);
	}

	public void testCreation() {
		TranslationTable table = new TranslationTable();
		assertEquals(0, table.size());
		table.addTranslation("key1", Node.createLiteral("value1", null, null).getLiteral().getLexicalForm());
		assertEquals(1, table.size());
	}

	public void testTranslation() {
		TranslationTable table = new TranslationTable();
		table.addTranslation("key1", Node.createLiteral("value1", null, null).getLiteral().getLexicalForm());
		table.addTranslation("key2", Node.createLiteral("value2", null, null).getLiteral().getLexicalForm());
		table.addTranslation("key3", Node.createLiteral("value3", null, null).getLiteral().getLexicalForm());
		assertEquals(Node.createLiteral("value1", null, null).getLiteral().getLexicalForm(), table.toRDFValue("key1"));
		assertEquals(Node.createLiteral("value2", null, null).getLiteral().getLexicalForm(), table.toRDFValue("key2"));
		assertEquals("key1", table.toDBValue(Node.createLiteral("value1", null, null).getLiteral().getLexicalForm()));
		assertEquals("key2", table.toDBValue(Node.createLiteral("value2", null, null).getLiteral().getLexicalForm()));
	}

	public void testUndefinedTranslation() {
		TranslationTable table = new TranslationTable();
		table.addTranslation("key1", Node.createLiteral("value1", null, null).getLiteral().getLexicalForm());
		assertNull(table.toRDFValue("unknownKey"));
		assertNull(table.toDBValue(Node.createURI("http://example.org/").getURI()));
	}
	
	public void testNullTranslation() {
		TranslationTable table = new TranslationTable();
		table.addTranslation("key1", Node.createLiteral("value1", null, null).getLiteral().getLexicalForm());
		assertNull(table.toRDFValue(null));
		assertNull(table.toDBValue(null));
	}
}