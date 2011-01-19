package com.brilig;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableSplit;

public class NodeTest extends TestCase {

	public void test_scan() throws IOException {
		Node node = new Node();
		HTable table = new HTable("user");
		ScanDispacher dispacher = new ScanDispacher(new HBaseConfiguration());
		
		List<TableSplit> splits = dispacher.getSplits(table,new Scan());
		
		String ret = node.scan(table,splits.get(0),"x=0;function foreach(result){x++}");
		
		System.out.println("["+ret+"]");
	}
}
