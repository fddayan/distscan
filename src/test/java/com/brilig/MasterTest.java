package com.brilig;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Scan;

public class MasterTest extends TestCase {

	
	public void test_scan_on_nodes() throws IOException {
		
		Master master = new Master(Arrays.asList("localhost:8182"));
		HTable table = new HTable("user");
		List<String> ret = master.scan(table,new Scan(),"x=0;function foreach(result){x++}");
		
		assertNotNull(ret);
		assertTrue(ret.size() > 0);
		
		for (String resp:ret) {
			System.out.println(resp);
		}
		
	}
}
