package org.d2rq.db.vendor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	MySQLTest.class,
	VendorTest.class
})

public class AllTests {}