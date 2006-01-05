package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.JFreeReportBoot;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.modules.gui.base.PreviewDialog;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.HECO_MonthlyBillingSettlementModel;

/**
 * Created on Feb 06, 2003
 * Creates a DisconnectReport using the com.cannontech.analysis.data.DisconnectData tableModel
 * Groups data by Collection Group and then by Device.  
 * @author snebben
 * @author bjonasson
 */
public class HECO_MonthlyBillingSettlementReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf LoadControlEventSummaryModel.
	 */
	public HECO_MonthlyBillingSettlementReport()
	{
		this(new HECO_MonthlyBillingSettlementModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf LoadControlEventSummaryModel.
	 * @param data_ - DatabaseModel TableModel data
	 */
	public HECO_MonthlyBillingSettlementReport(HECO_MonthlyBillingSettlementModel model_)
	{
		super();
		setModel(model_);
	}	
	/**
	 * Runs this report and shows a preview dialog.
	 * @param args the arguments (ignored).
	 * @throws Exception if an error occurs (default: print a stack trace)
	*/
	public static void main(final String[] args) throws Exception
	{
		// initialize JFreeReport
		JFreeReportBoot.getInstance().start();
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

		HECO_MonthlyBillingSettlementModel model = new HECO_MonthlyBillingSettlementModel();
		
		//start and stop time are only valid when model.showHist is false
		GregorianCalendar cal = new GregorianCalendar();
		model.setStopDate(cal.getTime());

		cal.set(Calendar.MONTH,Calendar.NOVEMBER);
		cal.set(Calendar.DAY_OF_MONTH,1);
		cal.set(Calendar.HOUR_OF_DAY,1);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		model.setStartDate(cal.getTime());

//		cal.set(Calendar.DAY_OF_MONTH, 15);
//		model.setStopDate(cal.getTime());
		YukonReportBase lcSummaryReport = new HECO_MonthlyBillingSettlementReport(model);
		lcSummaryReport.getModel().collectData();		
		//Create the report
		JFreeReport report = lcSummaryReport.createReport();
		report.setData(lcSummaryReport.getModel());
				
		final PreviewDialog dialog = new PreviewDialog(report);
		// Add a window closeing event, even though I think it's already handled by setDefaultCloseOperation(..)
		dialog.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				dialog.setVisible(false);
				dialog.dispose();
				System.exit(0);
			};
		});
		
		dialog.setModal(true);
		dialog.pack();
		dialog.setVisible(true);
	}		

	/**
	 * Create a Group for Column Headings only.  
	 * @return Group
	 */
	private Group createColumnHeadingGroup()
	{
		final Group collHdgGroup = new Group();
		collHdgGroup.setName("Column Heading");
	
		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		LabelElementFactory factory;
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
			factory = ReportFactory.createGroupLabelElementDefault(model, i);
			factory.setWrapText(Boolean.TRUE);
			header.addElement(factory.createElement());
		}
	
		header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", null, new BasicStroke(0.5f), 22));
		collHdgGroup.setHeader(header);
	
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		collHdgGroup.setFooter(footer);

		return collHdgGroup;
	}

	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createColumnHeadingGroup());
	  return list;
	}


	/**
	 * Creates the itemBand, the rows of data.
	 * @return the item band.
	 */
	protected ItemBand createItemBand()
	{
		ItemBand items = ReportFactory.createItemBandDefault();
		items.addElement(StaticShapeElementFactory.createHorizontalLine
			("top", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f), 0));

		
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
			TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			if( i > 1)
				factory.setHorizontalAlignment(ElementAlignment.RIGHT);
			if( i == HECO_MonthlyBillingSettlementModel.CONTROLLED_DEMAND_INCENTIVE_DATA)
				factory.setLineHeight(new Float(30));
			items.addElement(factory.createElement());
		}
		return items;
	}
	/**
	 * Creates the function collection. The xml definition for this construct:
	 * @return the functions.
	 * @throws FunctionInitializeException if there is a problem initialising the functions.
	 */
	protected ExpressionCollection getExpressions() throws FunctionInitializeException
	{
		super.getExpressions();
		
//		BoldFormatFunction boldItem = new BoldFormatFunction(HECO_LMEventSummaryModel.START_TIME_COLUMN);
//		boldItem.setName("boldItem");
//		boldItem.setElement(getModel().getColumnName(HECO_LMEventSummaryModel.START_TIME_COLUMN) + ReportFactory.NAME_ELEMENT);
//		expressions.add(boldItem);
		return expressions;
	}	
}
