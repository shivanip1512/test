package com.cannontech.thirdparty.digi;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.cannontech.thirdparty.exception.DigiWebServiceException;

public class DigiErrorHandler implements ResponseErrorHandler {
	
	private static final Log logger = LogFactory.getLog(DigiErrorHandler.class);
	
	public boolean hasError(ClientHttpResponse response) throws IOException {
	    MediaType contentType = response.getHeaders().getContentType();
		
	    // We are expecting a XML response. Make sure that happened.
	    if (contentType != null) {
		    if (contentType.getSubtype().equals(MediaType.APPLICATION_XML.getSubtype())) {
		        return false;
		    }
		} else {
		    //A few iDigi commands do not return data, if there is no content check the HttpStatus.
		    HttpStatus status = response.getStatusCode();
		    if (status == HttpStatus.OK ||
		        status == HttpStatus.CREATED ||
		        status == HttpStatus.ACCEPTED ||
		        status == HttpStatus.MULTI_STATUS) {
		        return false;
		    }
		}

		return true;
	}

	public void handleError(ClientHttpResponse response) throws IOException {
		logger.error(response.getStatusCode() + " - " + response.getStatusText());

		throw new DigiWebServiceException(response.getStatusCode() + " - " + response.getStatusText());
	}
	
}