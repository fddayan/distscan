package com.brilig;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.util.Bytes;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class NodeResource extends ServerResource {



	//THIS WORKS!!!!FINALLLYYYY
	@Post
	public Representation acceptRepresentation(Representation rep) {
		try {
			String content = rep.getText();
			System.out.println("Node recivied ==>" +content);
			System.out.println(getReference().getQuery());
			
			HTable table = new HTable(getReference().getQueryAsForm().getFirstValue("table").toString());
			byte[] from = Bytes.toBytes(getReference().getQueryAsForm().getFirstValue("from","").toString());
			byte[] to = Bytes.toBytes(getReference().getQueryAsForm().getFirstValue("to","").toString());

			Node node = new Node();

			String responseContent = node.scan(table,from,to,content);
			
			
			
			return new StringRepresentation(responseContent);
		} catch (IOException e) {
			e.printStackTrace();
			return rep;
		}
	}

}
