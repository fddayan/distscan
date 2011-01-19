package com.brilig;

import org.apache.hadoop.hbase.HBaseConfiguration;

import junit.framework.TestCase;

public class DistributedScannerClientTest extends TestCase {

	public void test_count() {
		DistributedScannerClient fixture = new DistributedScannerClient(new HBaseConfiguration());
		
		long count = fixture.count("user");
		
	}
}
