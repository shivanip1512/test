package com.cannontech.thirdparty.digi;

import java.io.IOException;
import java.net.URI;
import javax.annotation.PostConstruct;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import com.cannontech.common.config.ConfigurationSource;

public class DigiClientHttpRequestFactory implements ClientHttpRequestFactory{

	private String user;
	private String password;	
	private ConfigurationSource configSource;
	private HttpClient httpClient;
	private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);
	
	public DigiClientHttpRequestFactory(){
		//Create a client builder.
		HttpClientBuilder clientBuilder=  HttpClientBuilder.create();
		
		//Set user credentails
		if (this.user != null) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(AuthScope.ANY, 
					new UsernamePasswordCredentials(this.user,this.password));
			clientBuilder.setDefaultCredentialsProvider(credsProvider);
		}
		// Set Socket time out 
		RequestConfig requestConfig = RequestConfig.custom()
		        .setSocketTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS).build();
	
		clientBuilder.setDefaultRequestConfig(requestConfig);
		this.httpClient = clientBuilder.build();	
	}
	
	public HttpClient getHttpClient() {
		return this.httpClient;
	}
	
	@PostConstruct
	public void initialize() {
        this.user = configSource.getString("DIGI_USERNAME", "default");
        this.password = configSource.getString("DIGI_PASSWORD", "default");
	}
	
	@Autowired
	public void setConfigSource(ConfigurationSource configSource) {
        this.configSource = configSource;
    }

	@Override
	public ClientHttpRequest createRequest(URI arg0, HttpMethod arg1)
			throws IOException {

		return null;
	}
	

}
