package com.cannontech.thirdparty.digi;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class DigiErrorHandler implements ResponseErrorHandler {
	
	private static final Log logger = LogFactory.getLog(DigiErrorHandler.class);
	
	public boolean hasError(ClientHttpResponse response) throws IOException {
		HttpStatus status = response.getStatusCode();
		if (status == HttpStatus.BAD_REQUEST || status == HttpStatus.INTERNAL_SERVER_ERROR) {
			return true;
		}

		return false;
	}

	public void handleError(ClientHttpResponse response) throws IOException {
		logger.error(response.getStatusCode() + " - " + response.getStatusText());
	}
}
