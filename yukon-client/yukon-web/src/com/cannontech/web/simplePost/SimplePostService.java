package com.cannontech.web.simplePost;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;


public interface SimplePostService {

	public String postValue(String name, String value, String url, int port, String userName, String password) throws IOException, HttpException;
}
