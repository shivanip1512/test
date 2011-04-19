package com.cannontech.thirdparty.digi;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigHelper;

public class DigiClientHttpRequestFactory extends CommonsClientHttpRequestFactory{

	private final String user;
	private final String password;
	private static ConfigurationSource configSource;
	
	public DigiClientHttpRequestFactory() {
		configSource =  MasterConfigHelper.getConfiguration();
		
		this.user = configSource.getString("DIGI_USERNAME", "default");
		this.password = configSource.getString("DIGI_PASSWORD", "default");
	}
	
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
}
