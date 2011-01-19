package com.brilig;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Cli {
	
	/**
	 * Usage: [table name] [script.js]
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		File nodes = new File("nodes.lst");
		File js = new File(args[1]);
		
		System.out.println("Reading nodes addresses from " + nodes.getAbsolutePath());
		System.out.println("Reading javascript content from " + js.getAbsolutePath());
		
		List<String> address = FileUtils.readLines(nodes);
		
		Master master = new Master(address);
		
		long stime = System.currentTimeMillis();
		List<String> results = master.scan(args[0],FileUtils.readFileToString(js));
		long etime = System.currentTimeMillis();
		
		System.out.println("==============");
		for (String result:results) {
			System.out.println(result);
		}
		System.out.println("==============");
		System.out.println("Total time:" + (etime-stime) + " ms");
		
	
		
		System.exit(0);
	}
}
