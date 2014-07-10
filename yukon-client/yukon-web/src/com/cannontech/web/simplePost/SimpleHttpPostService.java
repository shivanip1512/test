package com.cannontech.web.simplePost;

import java.io.IOException;



public interface SimpleHttpPostService {

	public String postValue(String name, String value) throws IOException;
}
