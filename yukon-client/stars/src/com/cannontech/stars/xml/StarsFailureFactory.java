package com.cannontech.stars.xml;

import com.cannontech.stars.xml.serialize.StarsFailure;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsFailureFactory {

	public static StarsFailure newStarsFailure(int statusCode, String description) {
		StarsFailure failure = new StarsFailure();
		failure.setStatusCode( statusCode );
		failure.setDescription( description );
		
		return failure;
	}
}
