package com.cannontech.servlet;

/**
 * GraphGenerator generates a gif, png, svg, or jpg image of a graph based on the state
 * of an instance of com.cannontech.graph.GraphBean stored in the session.
 *
 * If an instance of GraphBean isn't found, one is created and placed in the session.
 * 
 * All parameters are optional. 
 * (ie if you provide no parameters the same graph will be generated as last time)
 * 
 * 	
 * Parameters
 * 
 * gdefid	- GraphDefinition ID
 * pointid	- A pointID for an AD-HOC trend.  This can be a comma separated list of PointIds (Ex. "1,2,3")
 * **NOTE**	  ONLY gdefid or pointid should be set, if both are...then by default we choose gdefid to work with!!!
 
 * start	- Start date string, see dtFormat optional parameter for formatting
 * period	- See com.cannontech.util.ServletUtil for valid period strings
 * view		- See com.cannontech.graph.model.TrendModelType for valid view strings
 * option	- ?See com.cannontech.graph.model.TrendModelType for valid option masks(?)
 * format	- gif | png | jpg | svg
 * dtFormat - overrides the date format, see java.text.SimpleDateFormat
 * 				Default is MM:dd:yyyy:HH:mm:ss
 * width	- Width of the generated graph
 * height	- Height of the generated graph	
 * 
 * @author: Aaron Lauinger
 * @author: Stacey Nebben
 */

import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.util.ServletUtil;
import com.cannontech.util.SessionAttribute;

public class GraphGenerator extends javax.servlet.http.HttpServlet {
		
/**
 * 
 * Creation date: (12/9/99 3:24:30 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public synchronized void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException
{
	try
	{	 	
		HttpSession session = req.getSession(false);
		String destURL = req.getParameter( ServletUtil.ATT_REDIRECT );	//successsful action URL
		String errorURL = req.getParameter( ServletUtil.ATT_REFERRER );	//failed action URL

		com.cannontech.graph.GraphBean localBean = (com.cannontech.graph.GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
		if(localBean == null)
		{
			session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, new com.cannontech.graph.GraphBean());
			localBean = (com.cannontech.graph.GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
		}

		
		String param;
		String param2;

		param = req.getParameter("pointid");
		if( param != null)
		{
			int []pointIDs = null;
			Vector ptIdsVec = new Vector();
			int startIndex = 0;
			int endIndex = param.indexOf(',');
			while( startIndex < endIndex )
			{
				ptIdsVec.add(Integer.valueOf(param.substring(startIndex, endIndex).trim()));
				startIndex = endIndex+1;
				endIndex = param.indexOf(',', startIndex); 
			}
			ptIdsVec.add(Integer.valueOf(param.substring(startIndex).trim()));

			pointIDs = new int[ptIdsVec.size()];
			for (int i = 0; i < ptIdsVec.size(); i++)
				pointIDs[i] = ((Integer)ptIdsVec.get(i)).intValue();

			localBean.setPointIDs(pointIDs);
		}
		
		param = req.getParameter("gdefid");
		if( param != null)
			localBean.setGdefid(Integer.parseInt( param));
			
		param = req.getParameter("start");
		if( param != null) 								
			localBean.setStart(param);

		param = req.getParameter("period");
		if( param != null)
			localBean.setPeriod(param);

		param = req.getParameter("view");
		if( param != null)
			localBean.setViewType(Integer.parseInt(param));

		param = req.getParameter("option");
		if( param != null)
			localBean.setOption(Integer.parseInt(param));

		param = req.getParameter("format");
		if( param != null)
			localBean.setFormat(param);

		param = req.getParameter("page");
		if( param != null)
			localBean.setPage(Integer.parseInt(param));
				
		param = req.getParameter("width");
		param2 = req.getParameter("height");
		if(param != null && param2 != null) {
			localBean.setSize(Integer.parseInt(param),Integer.parseInt(param2));
		}	
		
		param = req.getParameter("action");
		if (param != null )
		{
			if(  param.equalsIgnoreCase("UpdateOptions"))
			{
				if (destURL == null)
					destURL = req.getContextPath() + "/user/Metering/user_trending.jsp";
			}
			else if ( param.equalsIgnoreCase("EncodeGraph"))
			{
				localBean.updateCurrentPane();
				javax.servlet.ServletOutputStream out = null;
				try
				{	
					
					// Try to defeat caching
					 /* These are commented out because IE 5/6 has a bug where sometimes images won't appear when using SSL **
					 I'm pretty sure it isn't a webserver issue since mozilla has no trouble with this
					 Hopefully the Expires header will do what we want.
					 -Aaron
					 resp.setHeader("Cache-Control","no-store"); //HTTP 1.1 
					 resp.setHeader("Pragma","no-cache"); 		//HTTP 1.0
					*/
					resp.setHeader("Content-Type", "image/" + localBean.getFormat()); 
					resp.setDateHeader ("Expires", 0); 			//prevents caching at the proxy server
				
					out = resp.getOutputStream();
	    	
					localBean.encode(out);
					out.flush();
				}
				catch( java.io.IOException ioe )
				{
					ioe.printStackTrace();
				}			
			}
			else if ( param.equalsIgnoreCase("GetDataNow"))
			{
				com.cannontech.database.data.lite.LiteYukonUser liteYukonUser = (com.cannontech.database.data.lite.LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
				if( liteYukonUser != null)
				{
					String[] custDevices = req.getParameterValues("custDevices");
					if( custDevices != null)
					{
						SessionAttribute att = localBean.getDataNow(liteYukonUser, custDevices);
						if( att != null)
							session.setAttribute( att.getAttName(), att.getAttValue());	
					}	
				}
				if (destURL == null)
					destURL = req.getContextPath() + "/user/Metering/user_get_data_now.jsp";				
			}		
		}
		if(destURL != null)
			resp.sendRedirect( destURL );
	}
	catch( Throwable t )
	{
		CTILogger.error("An exception was throw in GraphGenerator:  ", t);
		t.printStackTrace();
	}
}

}
