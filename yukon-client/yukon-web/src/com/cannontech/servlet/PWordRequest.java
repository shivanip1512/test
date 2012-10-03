package com.cannontech.servlet;

/**
 * Sends password requests to the requestors EnergyCompany
 * 
 * @author: neuharthr 
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.model.RequestPword;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.impl.YukonSettingsDaoImpl;
import com.cannontech.util.ServletUtil;

public class PWordRequest extends javax.servlet.http.HttpServlet 
{
	private static final String INVALID_URI = "/spring/login/forgotPassword?failedMsg=";
	private static final String SUCCESS_URI = "/spring/login/forgotPassword?success=true";

	
	/**
	 * Handles password request operation
	 * 
	 * @param req javax.servlet.http.HttpServletRequest
	 * @param resp javax.servlet.http.HttpServletResponse
	 * @exception javax.servlet.ServletException The exception description.
	 * @exception java.io.IOException The exception description.
	 */
	@Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
	{
		String userName = ServletUtil.getParameter( req, "USERNAME");
		String email = ServletUtil.getParameter( req, "EMAIL");
		String fName = ServletUtil.getParameter( req, "FIRST_NAME");
		String lName = ServletUtil.getParameter( req, "LAST_NAME");
		String accNum = ServletUtil.getParameter( req, "ACCOUNT_NUM");		
		String notes = ServletUtil.getParameter( req, "NOTES");
        String energyComp = ServletUtil.getParameter( req, "ENERGY_COMPANY");


		RequestPword reqPword = createRequest( 
			req, userName,
			email, fName, lName, accNum );

		reqPword.setNotes( notes );
        reqPword.setEnergyCompany( energyComp );

		String returnURI = "";
		boolean authorized = YukonSpringHook.getBean("yukonSettingsDao",YukonSettingsDaoImpl.class).checkSetting(YukonSetting.ENABLE_PASSWORD_RECOVERY);
		if(!authorized) {
		    throw new NotAuthorizedException("Missing a required role or property to use this page.");
        }
		else if( !reqPword.isValidParams() )
		{
			//not enough info, return error
			returnURI = INVALID_URI + "MORE_INFO";
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
