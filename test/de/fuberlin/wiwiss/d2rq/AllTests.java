/*
 * $Id: AllTests.java,v 1.4 2005/03/07 10:08:47 garbers Exp $
 */
package de.fuberlin.wiwiss.d2rq;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for D2RQ
 *
 * @author Richard Cyganiak <richard@cyganiak.de>
 */
public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTests.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for de.fuberlin.wiwiss.d2rq");
        //$JUnit-BEGIN$
        suite.addTestSuite(ColumnTest.class);
        suite.addTestSuite(ParserTest.class);
        suite.addTestSuite(ValueRestrictionTest.class);
        suite.addTestSuite(PatternTest.class);
        suite.addTestSuite(TranslationTableTest.class);
        suite.addTestSuite(SQLStatementMakerTest.class);
        suite.addTestSuite(CSVParserTest.class);
        suite.addTestSuite(ResultIteratorTest.class);
        suite.addTestSuite(TablePrefixer.class);
        //$JUnit-END$
        suite.addTest(de.fuberlin.wiwiss.d2rq.functional_tests.AllTests.suite());
        return suite;
    }
}