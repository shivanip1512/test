/*
 * Created on Feb 28, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.yimp.util.DBFuncs;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImporterServlet extends HttpServlet 
{
	//this doesn't have to do a whole lot yet
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
	{
		//Flag to force an import event
		String forceImport = req.getParameter("forceImp");
			if( forceImport != null )
			{
				DBFuncs.forceImport();
				//show status of currently running on JSP
				DBFuncs.writeNextImportTime(new java.util.Date(), true);
			}
				
		resp.sendRedirect(req.getContextPath() + "/bulk/importer.jsp");
		
		/*
		 * There will be a lot more here down the road!!!
		 */
	}
		
}
