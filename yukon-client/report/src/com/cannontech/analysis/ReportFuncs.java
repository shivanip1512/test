/*
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

import org.jfree.report.JFreeReport;
import org.jfree.report.PageDefinition;
import org.jfree.report.modules.output.csv.CSVQuoter;
import org.jfree.report.modules.output.pageable.base.PageableReportProcessor;
import org.jfree.report.modules.output.pageable.pdf.PDFOutputTarget;

import com.cannontech.analysis.report.*;
import com.cannontech.analysis.tablemodel.*;
import com.keypoint.PngEncoder;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReportFuncs
{
	public static YukonReportBase createYukonReport(ReportModelBase model)
	{
	    YukonReportBase returnVal = null;
	    if( model instanceof StatisticModel)
	        returnVal = new StatisticReport();
	    else if( model instanceof SystemLogModel)
	        returnVal = new SystemLogReport();
	    else if( model instanceof LMControlLogModel)
			returnVal = new SystemLogReport((LMControlLogModel)model);
	    else if( model instanceof LoadGroupModel)
			returnVal = new LGAccountingReport();
	    else if( model instanceof DailyPeaksModel)
			returnVal = new DailyPeaksReport();
	    else if( model instanceof MeterReadModel)
			returnVal = new MeterReadReport();
	    else if( model instanceof MeterOutageModel)
		    returnVal = new MeterOutageReport();
	    else if( model instanceof CarrierDBModel)
	        returnVal = new CarrierDBReport();
	    else if( model instanceof PowerFailModel)
	        returnVal = new PowerFailReport();
	    else if( model instanceof DisconnectModel)
	        returnVal = new DisconnectReport();
	    else if( model instanceof ActivityModel)
			returnVal = new ECActivityLogReport();
	    else if( model instanceof RouteMacroModel)
	        returnVal = new RouteMacroReport();
	    else if(model instanceof RouteDBModel)
	        returnVal = new RouteDBReport();
	    else if( model instanceof LPSetupDBModel)
	        returnVal = new LPSetupDBReport();
	    else if( model instanceof LPDataSummaryModel)
	        returnVal = new LPDataSummaryReport();
	    else if( model instanceof ActivityDetailModel)
	        returnVal = new ECActivityDetailReport();
	    else if( model instanceof ProgramDetailModel)
	        returnVal = new ProgramDetailReport();
	    else if( model instanceof WorkOrderModel)
	        returnVal = new WorkOrder();
	    else if( model instanceof StarsLMSummaryModel)
	        returnVal = new StarsLMSummaryReport();
	    else if( model instanceof StarsLMDetailModel)
	        returnVal = new StarsLMDetailReport();
//	    else if( model instanceof StarsAMRSummaryModel) TODO
//	        returnVal = new StarsAMRSummaryReport();
	    else if( model instanceof StarsAMRDetailModel)
	        returnVal = new StarsAMRDetailReport();
	    else if( model instanceof PointDataIntervalModel)
	        returnVal = new PointDataIntervalReport();
	    else if( model instanceof PointDataSummaryModel)
	        returnVal = new PointDataSummaryReport();
	    else if( model instanceof CapBankListModel)
	        returnVal = new CapBankReport();
	    else if( model instanceof CapControlNewActivityModel)
	        returnVal = new CapControlNewActivityReport();
	    else
	        return null;

	    returnVal.setModel(model);
	    return returnVal;
	}
	
	public static void outputYukonReport(JFreeReport report, String ext, OutputStream out)
		throws Exception
	{
		if (ext.equalsIgnoreCase("pdf"))
		{
		    final PDFOutputTarget target = new PDFOutputTarget(out);
			
			target.setProperty(PDFOutputTarget.TITLE, "Title");
			target.setProperty(PDFOutputTarget.AUTHOR, "Author");
			
			final PageableReportProcessor processor = new PageableReportProcessor(report);
			processor.setOutputTarget(target);
			target.open();
			processor.processReport();
			target.close();
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

	public static void encodePNG(java.io.OutputStream out, Image image) throws java.io.IOException
	{
		final PngEncoder encoder = new PngEncoder(image, true, 0, 9);
		final byte[] data = encoder.pngEncode();
		out.write(data);
	}
}
