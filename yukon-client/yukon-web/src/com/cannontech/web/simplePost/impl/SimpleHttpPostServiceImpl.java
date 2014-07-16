package com.cannontech.web.simplePost.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.cannontech.web.simplePost.SimpleHttpPostService;

public class SimpleHttpPostServiceImpl implements SimpleHttpPostService {

	private String url;
	private int port;
	private String userName;
	private String password;

	public SimpleHttpPostServiceImpl(String url, int port, String userName,
			String password) {

		this.url = url;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}

	@Override
    public String postValue(String name, String value) throws IOException,HttpException {
		HttpPost post = null;
		String response = "";
		CloseableHttpClient httpClient = null;
		try {
			
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
			URL urlParser = new URL(url);
			String urlHost = urlParser.getHost();
			
			//Set Credentials
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(urlHost, port,
					AuthScope.ANY_REALM), new UsernamePasswordCredentials(
					userName, password));
			httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
			
			//Set AUTH PREFRENCES
			List<String> authPrefs = new ArrayList<String>(3);
			authPrefs.add(AuthSchemes.BASIC);
			authPrefs.add(AuthSchemes.NTLM);
			authPrefs.add(AuthSchemes.DIGEST);
			RequestConfig config = RequestConfig.custom()
					.setProxyPreferredAuthSchemes(authPrefs).build();
			httpClientBuilder.setDefaultRequestConfig(config);
			// Build the HTTP Client
			httpClient = httpClientBuilder.build();
			// POST the request
			StringBuffer buffer = new StringBuffer();
			post = new HttpPost(url);
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(name, value));
			post.setEntity(new UrlEncodedFormEntity(postParameters));
			HttpResponse httpResponse = httpClient.execute(post);
			
			// Process the response 
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent()));
			String dataLine = null;
			while ((dataLine = reader.readLine()) != null) {
				buffer.append(dataLine);
			}
			response = buffer.toString();
		} finally {
			post.releaseConnection();
			httpClient.close();
		}

		return response;
	}
}
