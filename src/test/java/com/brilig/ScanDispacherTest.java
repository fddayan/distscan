package com.brilig;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class ScanDispacherTest extends TestCase {
	
	public void test_single_scanner() throws IOException {
		HTable table = new HTable("user");
		Scan scan = new Scan();
		
		scan.setCaching(200);
		
		ResultScanner scanner = table.getScanner(scan);
		
		int c = 0;
		for (Result result:scanner) {
			c++;
		}
		
		System.out.println(c);
	}
	

	public void test_create_scanners_for_htable() throws IOException {
		ScanDispacher dispacher = new ScanDispacher(new HBaseConfiguration());
		HTable table = new HTable("user");
		
		List<ResultScanner> scanners = dispacher.create(table);
		
		assertNotNull(scanners);
		assertTrue(scanners.size() > 0);

		System.out.println("SCANNERS===>" + scanners.size());
		
		long stime = System.currentTimeMillis();
		List<String> results = dispacher.count(scanners);
		long etime = System.currentTimeMillis();
		
		System.out.println("Time:" + (etime-stime));
		assertNotNull(results);
//		assertTrue(results.size() == scanners.size());
		
		System.out.println("VALUES");
		int sum=0;
		for (String s : results) {
			System.out.println(s);
			sum+=Integer.valueOf(s);
		}
		
		System.out.println("Total:"+sum);
	}
	
//	public void test_put_many_data() throws IOException {
//		HTable table = new HTable("user");
//		
//		table.setAutoFlush(false);
//		int s = 1000000;
//		for (int i=s;i<(s+3000000);i++){
//			table.put(
//					new Put(Bytes.toBytes(String.valueOf(i)))
//					.add(Bytes.toBytes("data"),Bytes.toBytes("number"),Bytes.toBytes(String.valueOf(i)))
//					.add(Bytes.toBytes("data"),Bytes.toBytes("name"),Bytes.toBytes(StringUtils.repeat("A",20)))
//					.add(Bytes.toBytes("data"),Bytes.toBytes("name"),Bytes.toBytes(StringUtils.repeat("B",20)))
//					.add(Bytes.toBytes("data"),Bytes.toBytes("name"),Bytes.toBytes(StringUtils.repeat("C",20)))
//					.add(Bytes.toBytes("data"),Bytes.toBytes("name"),Bytes.toBytes(StringUtils.repeat("D",20)))
//					.add(Bytes.toBytes("data"),Bytes.toBytes("name"),Bytes.toBytes(StringUtils.repeat("E",20)))
//					.add(Bytes.toBytes("scanhelper"),Bytes.toBytes("scanhelper"),Bytes.toBytes("."))
//			);
//		}
//		
//		table.flushCommits();
//	}
}
