package com.cannontech.web.simplePost;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;


public interface SimpleHttpPostService {

	public String postValue(String name, String value) throws IOException, HttpException;
}
