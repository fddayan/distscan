package com.brilig;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableSplit;
import org.apache.hadoop.hbase.util.Bytes;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class Master {

	
	List<String> nodes;
	
	public Master(List<String> nodes) {
		super();
		this.nodes = nodes;
	}

	public List<String> scan(HTable table, Scan scan,String function) throws IOException {
		ScanDispacher dispacher = new ScanDispacher(new HBaseConfiguration());
		List<TableSplit> splits = dispacher.getSplits(table,scan);
		
		List<Future<String>> futures = new ArrayList<Future<String>>();
		int idx = 0;
		for (TableSplit split:splits) {
			String address = nodes.get(idx);
			
			futures.add(dispacheToNode(address,split,function));
			
			if ((idx+1) < nodes.size()) 
				idx++;
			else 
				idx=0;
		}
		
		List<String> responses = dispacher.collect(futures);

		return responses;
	}
	
	
	private ExecutorService executorService = Executors.newFixedThreadPool(25);
	
	public Future<String> dispacheToNode(String address,TableSplit split,String javascript) throws ResourceException, IOException {
		return executorService.submit(new RemoteNodeScan(address,split,javascript));
	}
	
	public class RemoteNodeScan implements Callable<String> {

		String address;
		
		TableSplit split;
		
		String javascript;
		
		public RemoteNodeScan(String address, TableSplit split,String javascript) {
			this.address = address;
			this.split = split;
			this.javascript = javascript;
		}

		@Override
		public String call() throws Exception {
			String url = buildUrl();
			
			System.out.println("Sending ==>"+url);
			
			ClientResource client = new ClientResource(url);
			StringWriter writer = new StringWriter();
			
			client.post(javascript,MediaType.APPLICATION_JSON).write(writer);
			
			return writer.toString();
		}

		private String buildUrl() {
			String url = "http://"+address+"/table/scan?table="+Bytes.toString(split.getTableName())+"&from=" + Bytes.toString(split.getStartRow()) + "&to=" + Bytes.toString(split.getEndRow());
			return url;
		}
	}

	public List<String> scan(String tableName, String jsContent) throws IOException {
		HTable table = new HTable(tableName);
		
		List<String> res = scan(table,new Scan(),jsContent);
		
		table.close();
		
		return res;
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}
}
