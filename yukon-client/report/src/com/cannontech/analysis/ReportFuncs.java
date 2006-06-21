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
import org.jfree.report.modules.output.pageable.base.PageableReportProcessor;
import org.jfree.report.modules.output.pageable.pdf.PDFOutputTarget;

import com.cannontech.analysis.report.CapBankReport;
import com.cannontech.analysis.report.CapControlCurrentStatusReport;
import com.cannontech.analysis.report.CapControlEventLogReport;
import com.cannontech.analysis.report.CapControlNewActivityReport;
import com.cannontech.analysis.report.CarrierDBReport;
import com.cannontech.analysis.report.DailyPeaksReport;
import com.cannontech.analysis.report.DisconnectReport;
import com.cannontech.analysis.report.ECActivityDetailReport;
import com.cannontech.analysis.report.ECActivityLogReport;
import com.cannontech.analysis.report.HECO_CustomerMonthlyBillingSettlementReport;
import com.cannontech.analysis.report.HECO_DSMISReport;
import com.cannontech.analysis.report.HECO_LMEventSummaryReport;
import com.cannontech.analysis.report.HECO_MonthlyBillingSettlementReport;
import com.cannontech.analysis.report.LGAccountingReport;
import com.cannontech.analysis.report.LPDataSummaryReport;
import com.cannontech.analysis.report.LPSetupDBReport;
import com.cannontech.analysis.report.LoadControlVerificationReport;
import com.cannontech.analysis.report.MeterOutageReport;
import com.cannontech.analysis.report.MeterReadReport;
import com.cannontech.analysis.report.PointDataIntervalReport;
import com.cannontech.analysis.report.PointDataSummaryReport;
import com.cannontech.analysis.report.PowerFailReport;
import com.cannontech.analysis.report.ProgramDetailReport;
import com.cannontech.analysis.report.RouteDBReport;
import com.cannontech.analysis.report.RouteMacroReport;
import com.cannontech.analysis.report.StarsAMRDetailReport;
import com.cannontech.analysis.report.StarsLMDetailReport;
import com.cannontech.analysis.report.StarsLMSummaryReport;
import com.cannontech.analysis.report.StatisticReport;
import com.cannontech.analysis.report.SystemLogReport;
import com.cannontech.analysis.report.WorkOrder;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ActivityDetailModel;
import com.cannontech.analysis.tablemodel.ActivityModel;
import com.cannontech.analysis.tablemodel.CapBankListModel;
import com.cannontech.analysis.tablemodel.CapControlCurrentStatusModel;
import com.cannontech.analysis.tablemodel.CapControlEventLogModel;
import com.cannontech.analysis.tablemodel.CapControlNewActivityModel;
import com.cannontech.analysis.tablemodel.CarrierDBModel;
import com.cannontech.analysis.tablemodel.DailyPeaksModel;
import com.cannontech.analysis.tablemodel.DisconnectModel;
import com.cannontech.analysis.tablemodel.HECO_CustomerMonthlyBillingSettlementModel;
import com.cannontech.analysis.tablemodel.HECO_DSMISModel;
import com.cannontech.analysis.tablemodel.HECO_LMEventSummaryModel;
import com.cannontech.analysis.tablemodel.HECO_MonthlyBillingSettlementModel;
import com.cannontech.analysis.tablemodel.LMControlLogModel;
import com.cannontech.analysis.tablemodel.LPDataSummaryModel;
import com.cannontech.analysis.tablemodel.LPSetupDBModel;
import com.cannontech.analysis.tablemodel.LoadControlVerificationModel;
import com.cannontech.analysis.tablemodel.LoadGroupModel;
import com.cannontech.analysis.tablemodel.MeterOutageModel;
import com.cannontech.analysis.tablemodel.MeterReadModel;
import com.cannontech.analysis.tablemodel.PointDataIntervalModel;
import com.cannontech.analysis.tablemodel.PointDataSummaryModel;
import com.cannontech.analysis.tablemodel.PowerFailModel;
import com.cannontech.analysis.tablemodel.ProgramDetailModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.RouteDBModel;
import com.cannontech.analysis.tablemodel.RouteMacroModel;
import com.cannontech.analysis.tablemodel.StarsAMRDetailModel;
import com.cannontech.analysis.tablemodel.StarsLMDetailModel;
import com.cannontech.analysis.tablemodel.StarsLMSummaryModel;
import com.cannontech.analysis.tablemodel.StatisticModel;
import com.cannontech.analysis.tablemodel.SystemLogModel;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
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
		else if( model instanceof CapControlCurrentStatusModel)
			returnVal = new CapControlCurrentStatusReport();
		else if( model instanceof CapControlEventLogModel)
			returnVal = new CapControlEventLogReport();	        
		else if( model instanceof LoadControlVerificationModel)
			returnVal = new LoadControlVerificationReport();
		else if( model instanceof HECO_LMEventSummaryModel)
			returnVal = new HECO_LMEventSummaryReport();
		else if( model instanceof HECO_MonthlyBillingSettlementModel)
			returnVal = new HECO_MonthlyBillingSettlementReport();
		else if( model instanceof HECO_CustomerMonthlyBillingSettlementModel)
			returnVal = new HECO_CustomerMonthlyBillingSettlementReport();
		else if( model instanceof HECO_DSMISModel)
			returnVal = new HECO_DSMISReport(); 	        
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
			out.write(((ReportModelBase)report.getData()).buildByteStream());
/*			CSVQuoter quoter = new CSVQuoter(","); 
		
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
			}*/ 
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
