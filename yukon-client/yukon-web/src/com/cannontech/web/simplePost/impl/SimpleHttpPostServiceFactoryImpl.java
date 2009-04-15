package com.cannontech.web.simplePost.impl;

import javax.annotation.Resource;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.web.simplePost.SimpleHttpPostServiceFactory;

public class SimpleHttpPostServiceFactoryImpl implements SimpleHttpPostServiceFactory {

	private ConfigurationSource configurationSource;
	
	@Override
	public SimpleHttpPostServiceImpl getSimpleHttpPostService(String url, int port, String userName, String password) {
		
		return new SimpleHttpPostServiceImpl(url, port, userName, password);
	}
	
	@Override
	public SimpleHttpPostServiceImpl getCayentaPostService() {
		
		String url = configurationSource.getRequiredString("CAYENTA_API_SERVER_URL");
		int port = configurationSource.getRequiredInteger("CAYENTA_API_SERVER_PORT");
		String userName = configurationSource.getRequiredString("CAYENTA_API_SERVER_USERNAME");
		String password = configurationSource.getRequiredString("CAYENTA_API_SERVER_PASSSWORD");
		
		return new SimpleHttpPostServiceImpl(url, port, userName, password);
	}
	
	@Resource(name="configurationSource")
	public void setConfigurationSource(ConfigurationSource configurationSource) {
		this.configurationSource = configurationSource;
	}
}
