package com.cannontech.stars.web.servlet;

/**
 * See com.cannontech.common.constants.LoginController for a list of parameters and usage info
 * 
 * Creation date: (12/7/99 9:46:12 AM)
 * @author: neuharthr 
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.stars.servletutils.RequestPword;

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
		String userName = req.getParameter("USERNAME").length() <= 0 ? null : req.getParameter("USERNAME");
		String email = req.getParameter("EMAIL").length() <= 0 ? null : req.getParameter("EMAIL");
		String fName = req.getParameter("FIRST_NAME").length() <= 0 ? null : req.getParameter("FIRST_NAME");
		String lName = req.getParameter("LAST_NAME").length() <= 0 ? null : req.getParameter("LAST_NAME");
		String accNum = req.getParameter("ACCOUNT_NUM").length() <= 0 ? null : req.getParameter("ACCOUNT_NUM");		
		String notes = req.getParameter("NOTES").length() <= 0 ? null : req.getParameter("NOTES");
		
		
		//put all params here as we will use them later
		final String[] allParams = new String[] { userName, email, fName, lName, accNum };

		String returnURI = "";

		RequestPword reqPword = new RequestPword( 
				userName,
				email,
				fName,
				lName,
				accNum );

		reqPword.setNotes( notes );

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
			if( reqPword.getResultState() == RequestPword.RET_SUCCESS )
				returnURI = SUCCESS_URI;
			else
				returnURI = INVALID_URI + reqPword.getResultString();
		}
		
		
		resp.sendRedirect( req.getContextPath() + returnURI );		
	}
	

	
}
