package com.brilig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableSplit;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ScanDispacher {

	public ScanDispacher(HBaseConfiguration hBaseConfiguration) {
	}

	public List<ResultScanner> create(HTable table) {
		List<ResultScanner> scanners = new ArrayList<ResultScanner>();
		try {
			List<TableSplit> splites = getSplits(table,new Scan());

			for (TableSplit tableSplit:splites) {
				ResultScanner scanner = newResultScanner(table, tableSplit);

				scanners.add(scanner);
			}

			return scanners;
		} catch (IOException e) {
			e.printStackTrace();
			return scanners;
		}
	}

	public ResultScanner newResultScanner(HTable table, TableSplit tableSplit) throws IOException {
		byte[] startRow = tableSplit.getStartRow();
		byte[] endRow = tableSplit.getEndRow();
		
		return newResultScanner(table, startRow, endRow);
	}

	public ResultScanner newResultScanner(HTable table, byte[] startRow,
			byte[] endRow) throws IOException {
		Scan scan = new Scan();
		
		scan.setCaching(1000);
		scan.addFamily(Bytes.toBytes("scanhelper"));
		
		scan.setStartRow(startRow);
		scan.setStopRow(endRow);

		ResultScanner scanner = table.getScanner(scan);
		return scanner;
	}

	public List<TableSplit> getSplits(HTable table, Scan scan) throws IOException {
		Pair<byte[][], byte[][]> keys = table.getStartEndKeys();
		if (keys == null || keys.getFirst() == null || 
				keys.getFirst().length == 0) {
			throw new IOException("Expecting at least one region.");
		}
		if (table == null) {
			throw new IOException("No table was provided.");
		}
		int count = 0;
		List<TableSplit> splits = new ArrayList<TableSplit>(keys.getFirst().length); 
		for (int i = 0; i < keys.getFirst().length; i++) {
			String regionLocation = table.getRegionLocation(keys.getFirst()[i]).
			getServerAddress().getHostname();
			byte[] startRow = scan.getStartRow();
			byte[] stopRow = scan.getStopRow();
			// determine if the given start an stop key fall into the region
			if ((startRow.length == 0 || keys.getSecond()[i].length == 0 ||
					Bytes.compareTo(startRow, keys.getSecond()[i]) < 0) &&
					(stopRow.length == 0 || 
							Bytes.compareTo(stopRow, keys.getFirst()[i]) > 0)) {
				byte[] splitStart = startRow.length == 0 || 
				Bytes.compareTo(keys.getFirst()[i], startRow) >= 0 ? 
						keys.getFirst()[i] : startRow;
						byte[] splitStop = (stopRow.length == 0 || 
								Bytes.compareTo(keys.getSecond()[i], stopRow) <= 0) &&
								keys.getSecond()[i].length > 0 ? 
										keys.getSecond()[i] : stopRow;
										TableSplit split = new TableSplit(table.getTableName(),
												splitStart, splitStop, regionLocation);
										splits.add(split);
										System.out.println("getSplits: split -> " + (count++) + " -> " + split);
			}
		}
		return splits;
	}


	private ExecutorService executorService = Executors.newFixedThreadPool(25);

	public List<String> count(List<ResultScanner> scanners) {
		//Run
		List<Future<String>> futures = new ArrayList<Future<String>>();

//		int i=0;
//		for (ResultScanner scanner:scanners) {
//			i++;
//			futures.add(executorService.submit(new ScannerExecution(scanner,String.valueOf(i))));
//		}
		futures.add(executorService.submit(new ScannerExecution(scanners.get(0),String.valueOf(1))));

		//Collect
		return collect(futures);
	}

	public List<String> collect(List<Future<String>> futures) {
		List<String> results = new ArrayList<String>();

		try {
			for (Future<String> future:futures) {
				results.add(future.get(30,TimeUnit.MINUTES));
			}
			
			return results;
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new ScannerConcurrentExecutionException(e.getMessage(),e);
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new ScannerConcurrentExecutionException(e.getMessage(),e);
		} catch (TimeoutException e) {
			e.printStackTrace();
			throw new ScannerConcurrentExecutionException(e.getMessage(),e);
		}
	}

	public static class ScannerExecution implements Callable<String> {

		String description;
		
		ResultScanner scanner;

		public ScannerExecution(ResultScanner scanner,String description) {
			super();
			this.scanner = scanner;
			this.description = description;
		}

		@Override
		public String call() throws Exception {
			long c = 0;
			System.out.println(description);
			
//			Iterator<Result> it = scanner.iterator();
//			
//			while (it.hasNext()) {
//				it.next();
//				c++;
//			}
			
			for (Result result:scanner) {
				c++;
				
//				System.out.println(description);
			}

			return String.valueOf(c);
		}
	}
	
	
	public static class ScannerConcurrentExecutionException extends RuntimeException {

		public ScannerConcurrentExecutionException() {
			super();
		}

		public ScannerConcurrentExecutionException(String message,
				Throwable cause) {
			super(message, cause);
		}

		public ScannerConcurrentExecutionException(String message) {
			super(message);
		}

		public ScannerConcurrentExecutionException(Throwable cause) {
			super(cause);
		}
		
	}

	public String scanWithJS(ResultScanner scanner, String jsFunction) {
		System.out.println("Starting scanner...");
		
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		Object result = cx.evaluateString(scope, jsFunction, "<cmd>", 1, null);
		
		long count = 0;
		for (Result hresult:scanner) {
			count++;
			
//			if (count%10000==0) 
//				System.out.print("*");
			
			Object wrappedOut = Context.javaToJS(hresult, scope);
			ScriptableObject.putProperty(scope, "result", wrappedOut);
			Function foreach = (Function)scope.get("foreach",scope);
			
			foreach.call(cx, scope, scope, new Object[]{result});
		}

		return Context.toString(scope.get("x",scope));
	}


}
