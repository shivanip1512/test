package com.cannontech.servlet;

/**
 * Creation date: (10/19/2001 3:32:09 PM)
 * @author: 
 */
import javax.servlet.http.HttpServlet;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.graph.exportdata.ExportDataFile;

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

		resp.setHeader("Cache-Control", "no-store"); //HTTP 1.1
		resp.setHeader("Pragma", "no-cache"); //HTTP 1.0
		resp.setDateHeader("Expires", 0); //prevents caching at the proxy server


		com.cannontech.graph.GraphBean localBean = (com.cannontech.graph.GraphBean)session.getAttribute("graphBean");
		if(localBean == null)
		{
			System.out.println("!!! BEAN IS NULL !!! ");
			session.setAttribute("graphBean", new com.cannontech.graph.GraphBean());
		}
			
		localBean.updateCurrentPane();

		String extension = req.getParameter("ext");
		javax.servlet.ServletOutputStream out = null;

		try
		{
			out = resp.getOutputStream();
			com.cannontech.graph.Graph graph = localBean.getGraph();
			
			int viewType = localBean.getViewType();
			//Extra code that needs to be done somewhere else.  Done here for craps sake now...
//			int tab = 0;
//			if( localBean.getTab().equalsIgnoreCase(com.cannontech.graph.GraphDefines.GRAPH_PANE_STRING))
//				tab = com.cannontech.graph.GraphDefines.GRAPH_PANE;
//			else if( localBean.getTab().equalsIgnoreCase(com.cannontech.graph.GraphDefines.TABULAR_PANE_STRING))
//				tab = com.cannontech.graph.GraphDefines.TABULAR_PANE;
//			else if( localBean.getTab().equalsIgnoreCase(com.cannontech.graph.GraphDefines.SUMMARY_PANE_STRING))
//				tab = com.cannontech.graph.GraphDefines.SUMMARY_PANE;
			
			if (extension.equalsIgnoreCase("csv"))
			{
				resp.setContentType("text/comma-separated-values");
				String fileName = graph.getTrendModel().getChartName().toString();
				fileName += fileNameFormat.format(graph.getTrendModel().getStartDate());
				fileName += ".csv";
				resp.addHeader("Content-Disposition", "filename=" + fileName);
				resp.setContentType("text/x-comma-separated-values");
				
				ExportDataFile eDataFile = new ExportDataFile(
				viewType,
				graph.getFreeChart(), 
				graph.getTrendModel().getChartName(), 
				graph.getTrendModel());

				eDataFile.setExtension(extension);
				String[] data = eDataFile.createCSVFormat();
				if( data != null)
					for (int i = 0; i < data.length; i++)
						out.write(data[i].getBytes());
			}
			else if (extension.equalsIgnoreCase("pdf"))
			{
				resp.setContentType("application/pdf");
				graph.encodePDF(out);
			}
			else if (extension.equalsIgnoreCase("jpeg"))
			{
				resp.setContentType("image/jpeg");
				graph.encodeJpeg(out);
			}
			else if (extension.equalsIgnoreCase("png"))
			{
				resp.setContentType("image/x-png");
				graph.encodePng(out);
			}
			else if (extension.equalsIgnoreCase(""))
			{
				resp.setContentType("image/x-png");
				graph.encodePng(out);
			}


			out.flush();
			System.out.println("*** Just tried to flush the out!");
		}
		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
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