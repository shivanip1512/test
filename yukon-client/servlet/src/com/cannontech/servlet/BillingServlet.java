package com.cannontech.servlet;

/**
 * Creation date: (10/19/2001 3:32:09 PM)
 * @author: 
 */
import javax.servlet.http.HttpServlet;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.billing.mainprograms.BillingBean;

public class BillingServlet extends HttpServlet
{	
	static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	
	String []exportArray = null;

/**
 * Creation date: (12/9/99 3:39:10 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
	throws javax.servlet.ServletException, java.io.IOException {
	try
	{
		CTILogger.debug("doPost invoked");
		javax.servlet.http.HttpSession session = req.getSession(false);

		if (session == null)
			resp.sendRedirect("/login.jsp");

		resp.setHeader("Cache-Control", "no-store"); //HTTP 1.1
		resp.setHeader("Pragma", "no-cache"); //HTTP 1.0
		resp.setDateHeader("Expires", 0); //prevents caching at the proxy server
		resp.setContentType("text/comma-separated-values");


		BillingBean localBean = (BillingBean)session.getAttribute("billingBean");
		if(localBean == null)
		{
			CTILogger.debug("!!! BEAN IS NULL !!! ");
			session.setAttribute("billingBean", new BillingBean());
		}
			
		javax.servlet.ServletOutputStream out = null;
		try
		{
			out = resp.getOutputStream();
			localBean.generateFile( out );
			out.flush();
			CTILogger.debug("*** Just tried to flush the out!");
		}
		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	catch (Throwable t)
	{
		CTILogger.debug("Exception occurd in BillingServlet:  " + t.getMessage());
		t.printStackTrace();
	}
	finally
	{
		CTILogger.debug(" *** SERVLET, FINALLY!!!");
		System.gc();
	}
}
}