package com.cannontech.thirdparty.digi;

import javax.annotation.PostConstruct;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

import com.cannontech.common.config.ConfigurationSource;

public class DigiClientHttpRequestFactory extends CommonsClientHttpRequestFactory{

	private String user;
	private String password;
	private ConfigurationSource configSource;
		
	@Override
	public HttpClient getHttpClient() {
		HttpClient client = super.getHttpClient();
		
		if (this.user != null) {
			client.getState().setCredentials(AuthScope.ANY, 
						new UsernamePasswordCredentials(this.user,this.password));
			client.getParams().setAuthenticationPreemptive(true);
		}
		
		return client;
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
}
