package com.cannontech.servlet;

/**
 * Sends password requests to the requestors EnergyCompany
 * 
 * @author: neuharthr 
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.servlet.logic.RequestPword;
import com.cannontech.util.ServletUtil;

public class PWordRequest extends javax.servlet.http.HttpServlet 
{
	private static final String INVALID_URI = "/pwordreq.jsp?failedMsg=";
	private static final String SUCCESS_URI = "/pwordreq.jsp?success=true";

	
	/**
	 * Handles password request operation
	 * 
	 * @param req javax.servlet.http.HttpServletRequest
	 * @param resp javax.servlet.http.HttpServletResponse
	 * @exception javax.servlet.ServletException The exception description.
	 * @exception java.io.IOException The exception description.
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
	{
		String userName = ServletUtil.getParm( req, "USERNAME");
		String email = ServletUtil.getParm( req, "EMAIL");
		String fName = ServletUtil.getParm( req, "FIRST_NAME");
		String lName = ServletUtil.getParm( req, "LAST_NAME");
		String accNum = ServletUtil.getParm( req, "ACCOUNT_NUM");		
		String notes = ServletUtil.getParm( req, "NOTES");
        String energyComp = ServletUtil.getParm( req, "ENERGY_COMPANY");


		RequestPword reqPword = createRequest( 
			req, userName,
			email, fName, lName, accNum );

		reqPword.setNotes( notes );
        reqPword.setEnergyCompany( energyComp );

		String returnURI = "";

		if( !reqPword.isValidParams() )
		{
			//not enough info, return error
			returnURI = INVALID_URI + "More information needs to be entered";
		}
		else
		{
			//do the work
			reqPword.doRequest();
			
			//decide where we need to go next
			if( reqPword.getState() == RequestPword.RET_SUCCESS )
				returnURI = SUCCESS_URI;
			else
				returnURI = INVALID_URI + reqPword.getResultString();
		}
		
		
		resp.sendRedirect( req.getContextPath() + returnURI );		
	}
	

	protected RequestPword createRequest( HttpServletRequest req_,
			String userName_, String email_, String fName_, String lName_, String accNum_ )
	{
		RequestPword reqPword = new RequestPword( 
				userName_,
				email_,
				fName_,
				lName_);


		return reqPword;
	}
	

}
