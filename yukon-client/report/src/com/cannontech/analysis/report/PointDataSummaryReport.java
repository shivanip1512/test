package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Calendar;

import org.jfree.report.Boot;
import org.jfree.report.Element;
import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.NumberFieldElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.function.ItemAvgFunction;
import org.jfree.report.function.ItemMaxFunction;
import org.jfree.report.function.ItemMinFunction;
import org.jfree.report.function.ItemSumFunction;
import org.jfree.report.function.TotalGroupSumFunction;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.tablemodel.PointDataSummaryModel;
import com.cannotech.analysis.jfreereport.ItemMaxValueFunction;
import com.cannotech.analysis.jfreereport.ItemMinValueFunction;

/**
 * Created on Dec 15, 2003
 * Creates a RPHSummaryReport using the com.cannontech.analysis.data.RPHSummaryModel tableModel
 * Group by DeviceName, PointName
 *
 * TODO - Rework report when subreporting feature is available in jfreereport  
 * @author snebben
 */
public class PointDataSummaryReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 */
	public PointDataSummaryReport()
	{
		this(new PointDataSummaryModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 * @param data_ - DatabaseModel TableModel data
	 */
	public PointDataSummaryReport(PointDataSummaryModel model_)
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
		Boot.start();
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());
	
		//Define start and stop parameters for a default 90 day report.
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		cal.set(Calendar.YEAR, 2004);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DATE, 13);
		long start = cal.getTimeInMillis();
		
		cal.set(Calendar.DATE, 15);
		long stop = cal.getTimeInMillis();
		
		
		PointDataSummaryModel reportModel = new PointDataSummaryModel(start, stop, PointDataSummaryModel.LOAD_PROFILE_POINT_TYPE);
		YukonReportBase dbReport = new PointDataSummaryReport(reportModel);
		dbReport.getModel().collectData();
	
		//Create the report
		JFreeReport report = dbReport.createReport();
		report.setData(dbReport.getModel());
	
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
	private Group createDeviceGroup()
	{
		final Group devGroup = new Group();
		devGroup.setName(PointDataSummaryModel.PAO_NAME_STRING + ReportFactory.NAME_GROUP);
		devGroup.addField(PointDataSummaryModel.PAO_NAME_STRING);
	
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
		
		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), PointDataSummaryModel.PAO_NAME_COLUMN);
		factory.setText(factory.getText() + ":");
		header.addElement(factory.createElement());
		
		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), PointDataSummaryModel.PAO_NAME_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(75, 1));	//override the posX location
		header.addElement(tfactory.createElement());
//
//		header.addElement(ReportFactory.createBasicLine("devGroupLine", 0.5f, 22));
		devGroup.setHeader(header);
	
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		devGroup.setFooter(footer);

		return devGroup;
	}
	/**
	 * Create a Group for Device, (by collectionGroup).  
	 * @return Group
	 */
	private Group createPointGroup()
	{
		final Group devGrpGroup = new Group();
		devGrpGroup.setName(PointDataSummaryModel.POINT_NAME_STRING + ReportFactory.NAME_GROUP);
		devGrpGroup.addField(PointDataSummaryModel.PAO_NAME_STRING);
		devGrpGroup.addField(PointDataSummaryModel.POINT_NAME_STRING);
		  
		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), PointDataSummaryModel.POINT_NAME_COLUMN);
		factory.setText(factory.getText() + ":");
		header.addElement(factory.createElement());

		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), PointDataSummaryModel.POINT_NAME_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(75, 1));
		header.addElement(tfactory.createElement());

		for (int i = PointDataSummaryModel.START_DATE_COLUMN; i <= PointDataSummaryModel.TOTAL_KWH_COLUMN; i++)
		{
			factory = ReportFactory.createGroupLabelElementDefault(model, i);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18));
			header.addElement(factory.createElement());
		}
	
		header.addElement(ReportFactory.createBasicLine("pointGrpLine", 0.5f, 20));
		devGrpGroup.setHeader(header);
	
		GroupFooter footer = ReportFactory.createGroupFooterDefault();

		footer.addElement(StaticShapeElementFactory.createLineShapeElement("totalKwhLine", null, new BasicStroke(0.3f), new Line2D.Float(450, 1, 550, 1)));

		tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.POINT_TOTAL_KWH_COLUMN);
		tfactory.setFieldname(PointDataSummaryModel.POINT_TOTAL_KWH_STRING + ReportFactory.NAME_ELEMENT);
		footer.addElement(tfactory.createElement());

//		factory = ReportFactory.createGroupLabelElementDefault(getModel(), RPHSummaryModel.SUMMARY_TITLE_COLUMN);
//		factory.setHorizontalAlignment(ElementAlignment.CENTER);
//		footer.addElement(factory.createElement());
		
		factory = ReportFactory.createGroupLabelElementDefault(getModel(), PointDataSummaryModel.PEAKS_TITLE_COLUMN);
		factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(PointDataSummaryModel.PEAKS_TITLE_COLUMN).getPositionX(), 12));
		footer.addElement(factory.createElement());
		
		factory = ReportFactory.createGroupLabelElementDefault(getModel(), PointDataSummaryModel.LOWS_TITLE_COLUMN);
		factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(PointDataSummaryModel.LOWS_TITLE_COLUMN).getPositionX(), 12));
		footer.addElement(factory.createElement());
		
//		footer.addElement(StaticShapeElementFactory.createLineShapeElement("peakSumLine", null, new BasicStroke(0.3f), new Line2D.Float(50, 30, 225, 30)));
//		footer.addElement(StaticShapeElementFactory.createLineShapeElement("lowSumLine", null, new BasicStroke(0.3f), new Line2D.Float(325, 30, 500, 30)));

		for (int i = PointDataSummaryModel.PEAK_TIMESTAMP_0_COLUMN; i <= PointDataSummaryModel.LOW_VALUE_4_COLUMN; i++)
		{
			tfactory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			tfactory.setBold(Boolean.FALSE);
			footer.addElement(tfactory.createElement());
		}
		
		devGrpGroup.setFooter(footer);
		
		return devGrpGroup;
	}
	
	private Group createStartDateGroup()
	{
		final Group startDateGroup = new Group();
		startDateGroup.setName(PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
		startDateGroup.addField(PointDataSummaryModel.PAO_NAME_STRING);
		startDateGroup.addField(PointDataSummaryModel.POINT_NAME_STRING);
		startDateGroup.addField(PointDataSummaryModel.START_DATE_STRING);
		  
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
//		startDateGroup.setHeader(header);
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 10));
		footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition(ReportFactory.DEFAULT_FONT, 10, false, false, false, false));

		TextFieldElementFactory tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.START_DATE_COLUMN);
		footer.addElement(tfactory.createElement());

		tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.DAILY_HIGH_TIME_COLUMN);
		tfactory.setFieldname(PointDataSummaryModel.DAILY_HIGH_TIME_STRING + ReportFactory.NAME_ELEMENT);
		footer.addElement(tfactory.createElement());

		tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.DAILY_HIGH_COLUMN);
		tfactory.setFieldname(PointDataSummaryModel.DAILY_HIGH_STRING + ReportFactory.NAME_ELEMENT);
		footer.addElement(tfactory.createElement());

		tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.DAILY_AVERAGE_COLUMN);
		tfactory.setFieldname(PointDataSummaryModel.DAILY_AVERAGE_STRING + ReportFactory.NAME_ELEMENT);
		footer.addElement(tfactory.createElement());

		tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.DAILY_LOW_TIME_COLUMN);
		tfactory.setFieldname(PointDataSummaryModel.DAILY_LOW_TIME_STRING + ReportFactory.NAME_ELEMENT);
		footer.addElement(tfactory.createElement());

		tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.DAILY_LOW_COLUMN);
		tfactory.setFieldname(PointDataSummaryModel.DAILY_LOW_STRING + ReportFactory.NAME_ELEMENT);
		footer.addElement(tfactory.createElement());


		tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.TOTAL_KWH_COLUMN);
		tfactory.setFieldname(PointDataSummaryModel.TOTAL_KWH_STRING + ReportFactory.NAME_ELEMENT);
		footer.addElement(tfactory.createElement());
		startDateGroup.setFooter(footer);
		
		return startDateGroup;
	}	
	
	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createDeviceGroup());
	  list.add(createPointGroup());
	  list.add(createStartDateGroup());
	  return list;
	}


	/**
	 * Creates the itemBand, the rows of data.
	 * @return the item band.
	 */
	protected ItemBand createItemBand()
	{
		//No item band, only summary data!
		ItemBand items = ReportFactory.createItemBandDefault();
		items.setVisible(false);	
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
		
		ItemMaxFunction maxItem = new ItemMaxFunction();
		maxItem.setName(PointDataSummaryModel.DAILY_HIGH_STRING + ReportFactory.NAME_ELEMENT);
		maxItem.setProperty("group", PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
		maxItem.setProperty("field", PointDataSummaryModel.VALUE_STRING);
		expressions.add(maxItem);
		
		ItemMaxValueFunction maxValueItem = new ItemMaxValueFunction();
		maxValueItem.setName(PointDataSummaryModel.DAILY_HIGH_TIME_STRING + ReportFactory.NAME_ELEMENT);
		maxValueItem.setDataField(PointDataSummaryModel.TIME_STRING);
		maxValueItem.setProperty("group", PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
		maxValueItem.setProperty("field", PointDataSummaryModel.VALUE_STRING);
		expressions.add(maxValueItem);

		ItemMinFunction minItem = new ItemMinFunction();
		minItem.setName(PointDataSummaryModel.DAILY_LOW_STRING + ReportFactory.NAME_ELEMENT);
		minItem.setProperty("group", PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
		minItem.setProperty("field", PointDataSummaryModel.VALUE_STRING);
		expressions.add(minItem);
		
		ItemMinValueFunction minValueItem = new ItemMinValueFunction();
		minValueItem.setName(PointDataSummaryModel.DAILY_LOW_TIME_STRING + ReportFactory.NAME_ELEMENT);
		minValueItem.setDataField(PointDataSummaryModel.TIME_STRING);
		minValueItem.setProperty("group", PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
		minValueItem.setProperty("field", PointDataSummaryModel.VALUE_STRING);
		expressions.add(minValueItem);

		ItemAvgFunction avgItem = new ItemAvgFunction();
		avgItem.setName(PointDataSummaryModel.DAILY_AVERAGE_STRING + ReportFactory.NAME_ELEMENT);
		avgItem.setProperty("group", PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
		avgItem.setProperty("field", PointDataSummaryModel.VALUE_STRING);
		expressions.add(avgItem);
		
		ItemSumFunction sumItem = new ItemSumFunction();
		sumItem.setName(PointDataSummaryModel.TOTAL_KWH_STRING + ReportFactory.NAME_ELEMENT);
		sumItem.setProperty("group", PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
		sumItem.setProperty("field", PointDataSummaryModel.VALUE_STRING);
		expressions.add(sumItem);
				
		TotalGroupSumFunction groupSumItem = new TotalGroupSumFunction();
		groupSumItem.setName(PointDataSummaryModel.POINT_TOTAL_KWH_STRING + ReportFactory.NAME_ELEMENT);
		groupSumItem.setProperty("group", PointDataSummaryModel.POINT_NAME_STRING + ReportFactory.NAME_GROUP);
		groupSumItem.setProperty("field", PointDataSummaryModel.VALUE_STRING);
		expressions.add(groupSumItem);

		return expressions;
	}
}