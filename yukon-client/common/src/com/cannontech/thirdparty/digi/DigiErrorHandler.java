package com.cannontech.thirdparty.digi;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.cannontech.thirdparty.exception.DigiRESTException;

public class DigiErrorHandler implements ResponseErrorHandler {
	
	private static final Log logger = LogFactory.getLog(DigiErrorHandler.class);
	
	public boolean hasError(ClientHttpResponse response) throws IOException {
		HttpStatus status = response.getStatusCode();
		if (status == HttpStatus.BAD_REQUEST || 
		    status == HttpStatus.INTERNAL_SERVER_ERROR ||
		    status == HttpStatus.UNAUTHORIZED) {
			return true;
		}

		return false;
	}

	public void handleError(ClientHttpResponse response) throws IOException {
		logger.error(response.getStatusCode() + " - " + response.getStatusText());
		throw new DigiRESTException("Error in iDigi Webservices call: " + response.getStatusCode() + " - " + response.getStatusText());
	}
}
