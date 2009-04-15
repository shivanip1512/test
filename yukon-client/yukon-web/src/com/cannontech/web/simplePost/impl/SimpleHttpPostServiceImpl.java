package com.cannontech.web.simplePost.impl;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;

import com.cannontech.web.simplePost.SimpleHttpPostService;

public class SimpleHttpPostServiceImpl implements SimpleHttpPostService {

	private String url;
	private int port;
	private String userName;
	private String password;
	
	public SimpleHttpPostServiceImpl(String url, int port, String userName, String password) {
		
		this.url = url;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	
	public String postValue(String name, String value) throws IOException, HttpException {
		
		HttpClient client = new HttpClient();

		AuthScope authScope = new AuthScope(url, port);
		UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(userName, password);
		client.getState().setCredentials(authScope, usernamePasswordCredentials);

		PostMethod post = new PostMethod(url);
		post.setDoAuthentication(true);
		
		NameValuePair[] nameValuePairs = {new NameValuePair(name, value)};
		post.setRequestBody(nameValuePairs);
		
		String response = "";
		try {
			client.executeMethod(post);
			response = post.getResponseBodyAsString();
		} finally {
			post.releaseConnection();
		}
		
		return response;
	}
}
