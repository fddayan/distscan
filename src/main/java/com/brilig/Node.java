package com.brilig;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.mapreduce.TableSplit;

public class Node {

	public String scan(HTable table,TableSplit split,String jsFunction) throws IOException {
		ScanDispacher scanDispacher = new ScanDispacher(new HBaseConfiguration());
		ResultScanner scanner = scanDispacher.newResultScanner(table,split);
		String res = scanDispacher.scanWithJS(scanner,jsFunction);
		
		return res;
	}
	
	public String scan(HTable table,byte[] from,byte[] to,String jsFunction) throws IOException {
		ScanDispacher scanDispacher = new ScanDispacher(new HBaseConfiguration());
		ResultScanner scanner = scanDispacher.newResultScanner(table,from,to);
		String res = scanDispacher.scanWithJS(scanner,jsFunction);
		
		return res;
	}
	
}
