package com.cannontech.web.simplePost.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
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
		
		URL urlParser = new URL(url);
		String urlHost = urlParser.getHost();
		
		HttpClient client = new HttpClient();
        client.getState().setCredentials(
                new AuthScope(urlHost, port, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(userName, password));
        
        List<String> authPrefs = new ArrayList<String>(3);
        authPrefs.add(AuthPolicy.BASIC);
        authPrefs.add(AuthPolicy.NTLM);
        authPrefs.add(AuthPolicy.DIGEST);
        
        client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
		
        PostMethod post = new PostMethod(url);
        NameValuePair params[] = new NameValuePair[1];
		params[0] = new NameValuePair(name, value);
		post.setRequestBody(params);

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
