package com.cannontech.servlet;

/**
 * Parameters
 * 
 * start	- Start date string, undefined
 * stop     - Stop date string, undefined
 * reportType - the type of report to create, value int from ReportTypes
 * period	- undefined
 * ext - gif | png | jpg | svg
 * action - the action/function to perform
 * 
 * @author: Stacey Nebben
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.report.JFreeReport;
import org.jfree.report.ext.servletdemo.AbstractPageableReportServletWorker;
import org.jfree.report.ext.servletdemo.AbstractTableReportServletWorker;
import org.jfree.report.modules.output.pageable.graphics.G2OutputTarget;
import org.jfree.report.modules.output.table.xls.ExcelProcessor;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.servlet.worker.StaticPageableReportServletWorker;
import com.cannontech.servlet.worker.StaticTableReportServletWorker;
import com.cannontech.util.ServletUtil;
import com.keypoint.PngEncoder;

public class ReportGenerator extends javax.servlet.http.HttpServlet
{
			
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
	
			String param;
			int page = 0, reportType=ReportTypes.SYSTEM_LOG_DATA;
			String ext = "png";
			
			param = req.getParameter("page");
			if( param != null)
				page = Integer.valueOf(param).intValue();

			param = req.getParameter("reportType");
			if( param != null)
				reportType = Integer.valueOf(param).intValue();

			param = req.getParameter("ext");
			if( param != null)
				ext = param.toLowerCase();
				
			String fileName = "MINE";
			fileName += "." + ext;

			param = req.getParameter("action");
			if( param != null && param.equalsIgnoreCase("download"))
				resp.setHeader("Content-Disposition","inline;filename=\"report-page-"+page+".png\"");
			else
				resp.addHeader("Content-Disposition", "attachment; filename=" + fileName);			


			//Define start and stop parameters for a default 90 day report.
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
			cal.set(java.util.Calendar.MINUTE, 0);
			cal.set(java.util.Calendar.SECOND, 0);
			cal.set(java.util.Calendar.MILLISECOND, 0);
			cal.add(java.util.Calendar.DATE, 1);
			long stop = cal.getTimeInMillis();
			cal.add(java.util.Calendar.DATE, -90);
			long start = cal.getTimeInMillis();

			YukonReportBase rpt = ReportFuncs.createYukonReport(reportType);
			rpt.getModel().setStartTime(start);
			rpt.getModel().setStopTime(stop);
			
			//Initialize the report data and populate the TableModel (collectData).
			rpt.getModel().collectData();

			//Define the report Paper properties and format.
			java.awt.print.Paper reportPaper = new java.awt.print.Paper();
			reportPaper.setImageableArea(30, 40, 552, 712);	//8.5 x 11 -> 612w 792h
			java.awt.print.PageFormat pageFormat = new java.awt.print.PageFormat();
			pageFormat.setPaper(reportPaper);
		
	
			//Create the report
			JFreeReport report = rpt.createReport();
			report.setDefaultPageFormat(pageFormat);
			report.setData(rpt.getModel());
		

			//create buffered image
			BufferedImage image = createImage(pageFormat);
			final Graphics2D g2 = image.createGraphics();
			g2.setPaint(Color.white);
			g2.fillRect(0,0, (int) pageFormat.getWidth(), (int) pageFormat.getHeight());
			final G2OutputTarget target = new G2OutputTarget(g2, pageFormat);


			final ServletOutputStream out = resp.getOutputStream();
			
			if (ext.equalsIgnoreCase("csv"))
			{
				try
				{
					final AbstractTableReportServletWorker worker = new StaticTableReportServletWorker(report, report.getData()); 
					// this throws an exception if the report could not be parsed
					final ExcelProcessor processor = new ExcelProcessor(worker.getReport());
					processor.setOutputStream(out);
				  	worker.setTableProcessor(processor);
				}
				catch (Exception e)
				{
				  CTILogger.debug("Failed to parse the report");
				  resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				  return;
				}				
			}
			else if (ext.equalsIgnoreCase("pdf"))
			{
				resp.setContentType("application/pdf");
			}
			else if (ext.equalsIgnoreCase("jpeg"))
			{
				resp.setContentType("image/jpeg");
			}
			else if (ext.equalsIgnoreCase("png"))
			{
				resp.setHeader("Content-Type", "image/png");

				final AbstractPageableReportServletWorker worker = new StaticPageableReportServletWorker(session, report, report.getData()); 
				worker.setOutputTarget(target);
				
				if (page >= worker.getNumberOfPages() || page < 0)
				{
				  CTILogger.debug("The page-parameter is invalid: " + page + ": " + worker.getNumberOfPages());
				  resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				  return;
				}
				worker.processPage(page);
				final PngEncoder encoder = new PngEncoder(image, true, 0, 9);
				final byte[] data = encoder.pngEncode();
				
				out.write(data);
				out.flush();
			}
//			else if (ext.equalsIgnoreCase("html"))
//			{
//				resp.setContentType("text/html");
//			}

//			try
//			{ 
//				final PDFOutputTarget target = new PDFOutputTarget(out,
//												   worker.getReportPageFormat(),true);
//				  target.setProperty(PDFOutputTarget.TITLE, "Title");
//				  target.setProperty(PDFOutputTarget.AUTHOR, "Author");
//				  worker.setOutputTarget(target);
//				  worker.processReport();
//				  out.flush();

//			{ 
//				final PDFOutputTarget target = new PDFOutputTarget(resp.getOutputStream(),pageFormat, true); 
//				worker.setOutputTarget(target); 
//				worker.processReport();
////				resp.getOutputStream().flush(); 
//			} 
//			catch (IOException ioe) 
//			{ 
//				throw ioe; 
//			} 
//			catch (Exception e) 
//			{ 
//				throw new ServletException("Failed to create the report", e);
//			} 
		}
		catch( Throwable t )
		{
			CTILogger.error("An exception was throw in GraphGenerator:  ");
			t.printStackTrace();
		}
	}
	
	/**
	  * Create the empty image for the given page size.
	  *
	  * @param pf the page format that defines the image bounds.
	  * @return the generated image.
	  */
	 private BufferedImage createImage(final PageFormat pf)
	 {
	   final double width = pf.getWidth();
	   final double height = pf.getHeight();
	   //write the report to the temp file
	   final BufferedImage bi = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_BYTE_INDEXED);
	   return bi;
	 }
	 
	/*public void encodePDF(java.io.OutputStream out, Image image) throws java.io.IOException
	{
		try
		{
			PDFEncoder encoder = new PDFEncoder();
			encoder.encode(image, out);
			out.flush();	
		}		
		catch( java.io.IOException io )
		{
			io.printStackTrace();
		}
		catch( com.klg.jclass.util.swing.encode.EncoderException ee )
		{
			ee.printStackTrace();
		}
	}*/
}