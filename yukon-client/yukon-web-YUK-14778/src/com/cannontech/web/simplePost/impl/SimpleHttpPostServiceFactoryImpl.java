package com.cannontech.web.simplePost.impl;

import com.cannontech.web.simplePost.SimpleHttpPostServiceFactory;

public class SimpleHttpPostServiceFactoryImpl implements SimpleHttpPostServiceFactory {

	@Override
	public SimpleHttpPostServiceImpl getSimpleHttpPostService(String url, int port, String userName, String password) {
		
		return new SimpleHttpPostServiceImpl(url, port, userName, password);
	}
}
