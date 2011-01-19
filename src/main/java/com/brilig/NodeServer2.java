package com.brilig;

import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class NodeServer2 extends ServerResource {
	
	public static void main(String[] args) throws Exception {
		 new Server(Protocol.HTTP, 8182, NodeServer2.class).start(); 
	}
	
	@Post("json")
	public String acceptJson(String content) {
		return content;
	}
	
	
}
