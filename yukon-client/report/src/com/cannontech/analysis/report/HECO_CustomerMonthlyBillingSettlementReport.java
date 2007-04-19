package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
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
import org.jfree.report.PageFooter;
import org.jfree.report.PageHeader;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.HECO_CustomerMonthlyBillingSettlementModel;

/**
 * Created on Feb 06, 2003
 * Creates a DisconnectReport using the com.cannontech.analysis.data.DisconnectData tableModel
 * Groups data by Collection Group and then by Device.  
 * @author snebben
 * @author bjonasson
 */
public class HECO_CustomerMonthlyBillingSettlementReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf LoadControlEventSummaryModel.
	 */
	public HECO_CustomerMonthlyBillingSettlementReport()
	{
		this(new HECO_CustomerMonthlyBillingSettlementModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf LoadControlEventSummaryModel.
	 * @param data_ - DatabaseModel TableModel data
	 */
	public HECO_CustomerMonthlyBillingSettlementReport(HECO_CustomerMonthlyBillingSettlementModel model_)
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

		HECO_CustomerMonthlyBillingSettlementModel model = new HECO_CustomerMonthlyBillingSettlementModel();
		
		//start and stop time are only valid when model.showHist is false
		GregorianCalendar cal = new GregorianCalendar();
		model.setStopDate(cal.getTime());

		cal.set(Calendar.MONTH,Calendar.NOVEMBER);
		cal.set(Calendar.DAY_OF_MONTH,13);
		cal.set(Calendar.HOUR_OF_DAY,1);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		model.setStartDate(cal.getTime());
		
//		cal.add(Calendar.MONTH, 1);

//		model.setStopDate(cal.getTime());

		cal.set(Calendar.DAY_OF_MONTH, 15);
		model.setStopDate(cal.getTime());
		YukonReportBase lcSummaryReport = new HECO_CustomerMonthlyBillingSettlementReport(model);
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
	private Group createCustomerHeadingGroup()
	{
		final Group custHdgGroup = new Group();
		custHdgGroup.setName( ((HECO_CustomerMonthlyBillingSettlementModel)getModel()).getColumnName(HECO_CustomerMonthlyBillingSettlementModel.CUSTOMER_NAME_DATA) + ReportFactory.NAME_GROUP);
		custHdgGroup.addField( ((HECO_CustomerMonthlyBillingSettlementModel)getModel()).getColumnName(HECO_CustomerMonthlyBillingSettlementModel.CUSTOMER_NAME_DATA));
	
		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		//Use reportFactory's regular elements, not the group ones.
		LabelElementFactory factory;
		
		TextFieldElementFactory tfactory;
		for (int i = HECO_CustomerMonthlyBillingSettlementModel.CUSTOMER_NAME_DATA; i <= HECO_CustomerMonthlyBillingSettlementModel.EVENT_DURATION_HEADING_DATA; i++)
		{
			factory = ReportFactory.createLabelElementDefault(getModel(), i);
			factory.setText(factory.getText() + ":");
			if( i < HECO_CustomerMonthlyBillingSettlementModel.LOAD_CONTROL_EVENT_SUMMARY_DATA)
				factory.setHorizontalAlignment(ElementAlignment.RIGHT);
			header.addElement(factory.createElement());
		
			if( i < HECO_CustomerMonthlyBillingSettlementModel.EVENT_TYPE_HEADING_DATA)
			{
				header.addElement(StaticShapeElementFactory.createHorizontalLine
						("bottom", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f), getModel().getColumnProperties(i+1).getPositionY()));

				tfactory = ReportFactory.createTextFieldElementDefault(getModel(), i);
				tfactory.setHorizontalAlignment(ElementAlignment.RIGHT);
				tfactory.setNullString("");
				tfactory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX()+175, getModel().getColumnProperties(i).getPositionY()));
				if (i != HECO_CustomerMonthlyBillingSettlementModel.LOAD_CONTROL_EVENT_SUMMARY_DATA)
					tfactory.setBold(Boolean.FALSE);

				header.addElement(tfactory.createElement());
			}
		}
		custHdgGroup.setHeader(header);
	
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		footer.setPagebreakAfterPrint(true);
		custHdgGroup.setFooter(footer);

		return custHdgGroup;
	}

	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createCustomerHeadingGroup());
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

		
		for (int i = HECO_CustomerMonthlyBillingSettlementModel.EVENT_TYPE_HEADING_DATA; i <= HECO_CustomerMonthlyBillingSettlementModel.EVENT_DURATION_HEADING_DATA; i++)
		{
			TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 1));
			items.addElement(factory.createElement());
		}
		return items;
	}
	
	/**
	 * Add the same elements as those from the report header so they show on every page.
	 * Creates the page header.
	 * @return the page header.
	 */
	protected PageHeader createPageHeader()
	{
		final PageHeader header = super.createPageHeader();
		header.getStyle().setStyleProperty( ElementStyleSheet.MINIMUMSIZE, ReportFactory.REPORT_STYLE_DIMENSION);
		header.getStyle().setFontDefinitionProperty(ReportFactory.REPORT_HEADER_BAND_FONT);

		LabelElementFactory factory = new LabelElementFactory();
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 0));
		factory.setMinimumSize(new FloatDimension(-100, 24));
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText(getModel().getTitleString());
		header.addElement(factory.createElement());
				
		factory = new LabelElementFactory();
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 20));
		factory.setMinimumSize(new FloatDimension(-100, 24));
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText(getModel().getDateRangeString());
		factory.setFontSize(new Integer(12));
		header.addElement(factory.createElement());		
		header.setDisplayOnFirstPage(false);
		header.setDisplayOnLastPage(false);
		return header;
	}
	protected PageFooter createPageFooter() {
    	PageFooter pageFooter = super.createPageFooter();
    	pageFooter.addElement(getVersionLabel().createElement());
		return pageFooter;
    }
}
