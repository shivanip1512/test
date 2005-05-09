/*
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

import org.jfree.report.JFreeReport;
import org.jfree.report.PageDefinition;
import org.jfree.report.modules.output.csv.CSVQuoter;
import org.jfree.report.modules.output.pageable.base.PageableReportProcessor;
import org.jfree.report.modules.output.pageable.pdf.PDFOutputTarget;

import com.cannontech.analysis.report.CapBankReport;
import com.cannontech.analysis.report.CarrierDBReport;
import com.cannontech.analysis.report.DailyPeaksReport;
import com.cannontech.analysis.report.DisconnectReport;
import com.cannontech.analysis.report.ECActivityDetailReport;
import com.cannontech.analysis.report.ECActivityLogReport;
import com.cannontech.analysis.report.LGAccountingReport;
import com.cannontech.analysis.report.MeterReadReport;
import com.cannontech.analysis.report.PointDataIntervalReport;
import com.cannontech.analysis.report.PointDataSummaryReport;
import com.cannontech.analysis.report.PowerFailReport;
import com.cannontech.analysis.report.ProgramDetailReport;
import com.cannontech.analysis.report.RouteMacroReport;
import com.cannontech.analysis.report.StatisticReport;
import com.cannontech.analysis.report.SystemLogReport;
import com.cannontech.analysis.report.WorkOrder;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.LMControlLogModel;
import com.cannontech.clientutils.CTILogger;
import com.keypoint.PngEncoder;
import com.klg.jclass.util.swing.encode.EncoderException;
import com.klg.jclass.util.swing.encode.page.PDFEncoder;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReportFuncs
{
	public static YukonReportBase createYukonReport(int reportType)
	{
		CTILogger.info(" REPRT TYPE: " + reportType);
		switch (reportType)
		{
			case ReportTypes.STATISTIC_DATA:
				return new StatisticReport();
			
			case ReportTypes.SYSTEM_LOG_DATA:
				return new SystemLogReport();
			case ReportTypes.LM_CONTROL_LOG_DATA:	//override the systemLogModel
				return new SystemLogReport(new LMControlLogModel());
				
			case ReportTypes.LG_ACCOUNTING_DATA:
				return new LGAccountingReport();
				
			case ReportTypes.LM_DAILY_PEAKS_DATA:
				return new DailyPeaksReport();
				
			case ReportTypes.METER_READ_DATA:
				return new MeterReadReport();
				
			case ReportTypes.CARRIER_DB_DATA:
				return new CarrierDBReport();
				
			case ReportTypes.POWER_FAIL_DATA:
				return new PowerFailReport();
				
			case ReportTypes.DISCONNECT_METER_DATA:
				return new DisconnectReport();
				
			case ReportTypes.EC_ACTIVITY_LOG_DATA:
				return new ECActivityLogReport();

			case ReportTypes.CARRIER_ROUTE_MACRO_DATA:
				return new RouteMacroReport();

			case ReportTypes.EC_ACTIVITY_DETAIL_DATA:
				return new ECActivityDetailReport();
				
			case ReportTypes.PROGRAM_DETAIL_DATA:
				return new ProgramDetailReport();
			
			case ReportTypes.EC_WORK_ORDER_DATA:
				return new WorkOrder();
			
			case ReportTypes.POINT_DATA_INTERVAL_DATA:
				return new PointDataIntervalReport();

			case ReportTypes.POINT_DATA_SUMMARY_DATA:
				return new PointDataSummaryReport();
				
			case ReportTypes.CBC_BANK_DATA:
				return new CapBankReport();
				
			default:
				return new CarrierDBReport();	//give us something at least....
		}
	}
	
	public static void outputYukonReport(JFreeReport report, String ext, OutputStream out)
		throws Exception
	{
		PageDefinition pageDefinition = report.getPageDefinition();
		//create buffered image
		BufferedImage image = createImage(pageDefinition);
		final Graphics2D g2 = image.createGraphics();
		g2.setPaint(Color.white);
		g2.fillRect(0,0, (int) pageDefinition.getWidth(), (int) pageDefinition.getHeight());
		
		if (ext.equalsIgnoreCase("pdf"))
		{
//			final PDFOutputTarget target = new PDFOutputTarget(out,pageFormat,true);
		    final PDFOutputTarget target = new PDFOutputTarget(out);
			
			target.setProperty(PDFOutputTarget.TITLE, "Title");
			target.setProperty(PDFOutputTarget.AUTHOR, "Author");
			
			final PageableReportProcessor processor = new PageableReportProcessor(report);
			processor.setOutputTarget(target);
			target.open();
			processor.processReport();
			target.close();
			encodePDF(out, image);
		}
		else if (ext.equalsIgnoreCase("png"))
		{
			throw new Exception("The 'png' format is not supported by the outputYukonReport() method.");
		}
		else if (ext.equalsIgnoreCase("csv"))
		{
			CSVQuoter quoter = new CSVQuoter(","); 

			//Write column headers
			for (int r = 0; r < report.getData().getColumnCount(); r++) 
			{
				if( r != 0 )
					out.write(new String(",").getBytes());
					
				out.write(report.getData().getColumnName(r).getBytes());
			}
			out.write(new String("\r\n").getBytes());
			
			//Write data
			for (int r = 0; r < report.getData().getRowCount(); r++) 
			{
				for (int c = 0; c < report.getData().getColumnCount(); c++) 
				{ 
					if (c != 0) 
					{ 
						out.write(new String(",").getBytes()); 
					} 
					String rawValue = String.valueOf (report.getData().getValueAt(r,c)); 
					out.write(quoter.doQuoting(rawValue).getBytes()); 
				} 
				out.write(new String("\r\n").getBytes());
			} 
		}
		/*else if (ext.equalsIgnoreCase("jpeg"))
		{
		}*/
	}
	
	/**
	  * Create the empty image for the given page size.
	  *
	  * @param pf the page format that defines the image bounds.
	  * @return the generated image.
	  */
	public static BufferedImage createImage(final PageDefinition pd)
	{
		final double width = pd.getWidth();
		final double height = pd.getHeight();
		//write the report to the temp file
		final BufferedImage bi = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_BYTE_INDEXED);
		return bi;
	}
	
	public static void encodePDF(java.io.OutputStream out, Image image) throws java.io.IOException
	{
		try
		{
			PDFEncoder encoder = new PDFEncoder();
			encoder.encode(image, out);
		}		
		catch( java.io.IOException io )
		{
			io.printStackTrace();
		}
		catch( EncoderException ee )
		{
			ee.printStackTrace();
		}
	}

	public static void encodePNG(java.io.OutputStream out, Image image) throws java.io.IOException
	{
		final PngEncoder encoder = new PngEncoder(image, true, 0, 9);
		final byte[] data = encoder.pngEncode();
		out.write(data);
	}
}
