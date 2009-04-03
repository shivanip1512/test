package com.cannontech.web.simplePost.impl;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;

import com.cannontech.web.simplePost.SimplePostService;

public class SimplePostServiceImpl implements SimplePostService {

	public String postValue(String name, String value, String url, int port, String userName, String password) throws IOException, HttpException {
		
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
