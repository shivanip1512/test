package com.cannontech.servlet;

/**
 * Sends password requests to the requestors EnergyCompany,
 * can be retrieved by account numbers.
 * 
 * @author: neuharthr 
 */

import javax.servlet.http.HttpServletRequest;

import com.cannontech.servlet.logic.RequestPword;
import com.cannontech.servlet.logic.StarsRequestPword;

public class StarsPWordRequest extends PWordRequest 
{
	
	protected RequestPword createRequest( HttpServletRequest req_,
			String userName_, String email_, String fName_, String lName_, String accNum_ )
	{
		StarsRequestPword reqPword = new StarsRequestPword( 
				userName_,
				email_,
				fName_,
				lName_,
				accNum_ );


		return reqPword;
	}	
}
