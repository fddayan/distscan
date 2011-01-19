package com.brilig;

import junit.framework.TestCase;

public class CliTest extends TestCase {

	public void test_main() throws Exception {
		String[] args = new String[] {"user","src/test/resources/count.js"};
		
		Cli.main(args);
	}
}
