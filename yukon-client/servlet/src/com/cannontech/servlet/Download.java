package com.cannontech.servlet;

/**
 * Creation date: (10/19/2001 3:32:09 PM)
 * @author: 
 */
import javax.servlet.http.HttpServlet;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.util.ServletUtil;

public class Download extends HttpServlet
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
	throws javax.servlet.ServletException, java.io.IOException
{
	java.text.SimpleDateFormat fileNameFormat = new java.text.SimpleDateFormat("yyyyMMdd");
	try
	{
		CTILogger.debug("doPost invoked");
		javax.servlet.http.HttpSession session = req.getSession(false);

		if (session == null)
			resp.sendRedirect("/login.jsp");

		// Try to defeat caching
		 /* These are commented out because IE 5/6 has a bug where sometimes images won't appear when using SSL **
		 I'm pretty sure it isn't a webserver issue since mozilla has no trouble with this
		 Hopefully the Expires header will do what we want.
		 -Aaron
		 resp.setHeader("Cache-Control","no-store"); //HTTP 1.1 
		 resp.setHeader("Pragma","no-cache"); 		//HTTP 1.0
		*/
		resp.setDateHeader("Expires", 0); //prevents caching at the proxy server


		com.cannontech.graph.GraphBean localBean = (com.cannontech.graph.GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
		if(localBean == null)
		{
			System.out.println("!!! BEAN IS NULL !!! ");
			session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, new com.cannontech.graph.GraphBean());
		}
			
		localBean.updateCurrentPane();
		javax.servlet.ServletOutputStream out = null;

		try
		{
			String extension = req.getParameter("ext");
			if (extension == null)
				extension = "png";	//default
				
			out = resp.getOutputStream();
//			com.cannontech.graph.Graph graph = localBean.getGraph();
			
			String fileName = localBean.getTrendModel().getChartName().toString();
			fileName += fileNameFormat.format(localBean.getTrendModel().getStartDate());
			fileName += "." + extension;

			resp.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			if (extension.equalsIgnoreCase("csv"))
			{
				resp.setContentType("text/x-comma-separated-values");
				localBean.encodeCSV(out);
			}
			else if (extension.equalsIgnoreCase("pdf"))
			{
				resp.setContentType("application/pdf");
				localBean.encodePDF(out);
			}
			else if (extension.equalsIgnoreCase("jpeg"))
			{
				resp.setContentType("image/jpeg");
				localBean.encodeJpeg(out);
			}
			else if (extension.equalsIgnoreCase("png"))
			{
				resp.setContentType("image/x-png");
				localBean.encodePng(out);
			}
			else if (extension.equalsIgnoreCase("html"))
			{
				resp.setContentType("text/html");
				if( localBean.getViewType() == GraphRenderers.TABULAR)
					localBean.encodeTabularHTML(out);
				else if (localBean.getViewType() == GraphRenderers.SUMMARY)
					localBean.encodeSummmaryHTML(out);
			}
			else if (extension.equalsIgnoreCase(""))
			{
				resp.setContentType("image/x-png");
				localBean.encodePng(out);
			}
			out.flush();
		}
		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
/*		finally
		{
			try
			{
				if( out != null )
				{
					out.flush();
//					out.close();
				}
			} 
			catch( java.io.IOException ioe )
			{
				ioe.printStackTrace();
			}
		}//end finally
		*/
	}
	catch (Throwable t)
	{
		CTILogger.debug("Exception occurd in TabularDataGenerator:  " + t.getMessage());
		t.printStackTrace();
	}
	finally
	{
		System.out.println(" *** SERVLET, FINALLY!!!");
		System.gc();
	}
}
}