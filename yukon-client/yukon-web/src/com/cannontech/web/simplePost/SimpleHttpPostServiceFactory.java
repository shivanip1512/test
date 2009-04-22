package com.cannontech.web.simplePost;


public interface SimpleHttpPostServiceFactory {

	public SimpleHttpPostService getSimpleHttpPostService(String url, int port, String userName, String password);
}
