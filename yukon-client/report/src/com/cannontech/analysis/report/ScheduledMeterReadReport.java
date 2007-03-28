package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.Date;

import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.JFreeReportBoot;
import org.jfree.report.ReportFooter;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.function.ItemHideFunction;
import org.jfree.report.function.TotalItemCountFunction;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.ScheduledMeterReadModel;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

/**
 * Created on Dec 15, 2003
 * Creates a MissedMeterReport using the com.cannontech.analysis.data.MissedMeterData tableModel
 * Groups data by Collection Group and then by Device.  
 * @author snebben
 */
public class ScheduledMeterReadReport extends YukonReportBase
{
	private TotalItemCountFunction totalItemCount = null;
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf MissedMeterModel.
	 * @param data_ - PowerFailModel TableModel data
	 */
	public ScheduledMeterReadReport(ScheduledMeterReadModel model_)
	{
		super();
		setModel(model_);
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf MissedMeterModel.
	 * @param data_ - MissedMeterModel TableModel data
	 */
	public ScheduledMeterReadReport()
	{
		this(new ScheduledMeterReadModel());
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

		//Get a default start date of 90 days previous.
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		cal.set(java.util.Calendar.MONTH, Calendar.MARCH);
		cal.set(java.util.Calendar.DATE, 27);
		
		Date stop = cal.getTime();
		cal.add(java.util.Calendar.DATE, -20);
		Date start = cal.getTime();
		
		ScheduledMeterReadModel model = new ScheduledMeterReadModel();
//		model.setStopDate(stop);
		model.setFilterModelType(ReportFilter.ALTERNATEGROUP);
		model.setGroupBy(ScheduledMeterReadModel.GroupBy.GROUP_BY_SCHEDULE_REQUESTS);
		model.setStatusCodeType(ScheduledMeterReadModel.StatusCodeType.SUCCESS_METER_READ_TYPE);
		YukonReportBase meterReadReport =new ScheduledMeterReadReport(model);
		meterReadReport.getModel().collectData();
		
		//Create the report
		JFreeReport report = meterReadReport.createReport();
		report.setData(meterReadReport.getModel());
				
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
	@Override
	protected ReportFooter createReportFooter() {
		return super.createReportFooter();
	}

	/**
	 * Create a Group for ScheduleName  
	 * @return Group
	 */
	private Group createScheduleGroup()
	{
	    final Group scheduleGroup = new Group();
	    scheduleGroup.setName( ScheduledMeterReadModel.SCHEDULE_NAME_STRING + ReportFactory.NAME_GROUP);
	    scheduleGroup.addField( ScheduledMeterReadModel.SCHEDULE_NAME_STRING);
	    scheduleGroup.addField( ScheduledMeterReadModel.SCHEDULE_START_TIME_STRING);
	    scheduleGroup.addField(ScheduledMeterReadModel.SCHEDULE_STOP_TIME_STRING);

	    GroupHeader header = ReportFactory.createGroupHeaderDefault();

	    LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), ScheduledMeterReadModel.SCHEDULE_NAME_COLUMN);
	    factory.setText(factory.getText() + ":");
	    header.addElement(factory.createElement());

	    TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), ScheduledMeterReadModel.SCHEDULE_NAME_COLUMN);
	    tfactory.setBold(false);
	    tfactory.setAbsolutePosition(new Point2D.Float(100, 1));	//override the posX location
	    header.addElement(tfactory.createElement());

	    factory = ReportFactory.createGroupLabelElementDefault(getModel(), ScheduledMeterReadModel.SCHEDULE_START_TIME_COLUMN);
	    factory.setText(factory.getText() + ":");
	    header.addElement(factory.createElement());

	    tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), ScheduledMeterReadModel.SCHEDULE_START_TIME_COLUMN);
	    tfactory.setBold(false);
	    tfactory.setAbsolutePosition(new Point2D.Float(460, 1));	//override the posX location
	    header.addElement(tfactory.createElement());
	    
	    factory = ReportFactory.createGroupLabelElementDefault(getModel(), ScheduledMeterReadModel.SCHEDULE_STOP_TIME_COLUMN);
	    factory.setText(factory.getText() + ":");
	    header.addElement(factory.createElement());

	    tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), ScheduledMeterReadModel.SCHEDULE_STOP_TIME_COLUMN);
	    tfactory.setBold(false);
	    tfactory.setAbsolutePosition(new Point2D.Float(610, 1));	//override the posX location
	    header.addElement(tfactory.createElement());
	    
	    
	    header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", null, new BasicStroke(0.5f), 20));

//	    for (int i = ScheduledMeterReadModel.DEVICE_NAME_COLUMN; i < getModel().getColumnCount(); i++)
//	    {
//	        factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
//			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY() + 18));
//		    header.addElement(factory.createElement());
//		}
//	    header.addElement(StaticShapeElementFactory.createHorizontalLine("line2", null, new BasicStroke(0.5f), 38));
	    scheduleGroup.setHeader(header);


	  	GroupFooter footer = ReportFactory.createGroupFooterDefault();
		/*final Group statCodeGroup = new Group();

		tfactory = tfactory = new TextFieldElementFactory();
		tfactory.setAbsolutePosition(new Point2D.Float(100, 1));
		tfactory.setMinimumSize(new FloatDimension(-100, 12));
		tfactory.setHorizontalAlignment(ElementAlignment.CENTER);
		tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
		tfactory.setFieldname("totalItemCount");
		tfactory.setDynamicHeight(Boolean.TRUE);
		footer.addElement(tfactory.createElement());
//		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 12));
//		footer.getStyle().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
//		footer.addElement(ReportFactory.createBasicLine("line4", 0.5f, 4));
		statCodeGroup.setFooter(footer);
	  	*/
		scheduleGroup.setFooter(footer);
	  	return scheduleGroup;
	}

	/**
	 * Create a Group for ScheduleName  
	 * @return Group
	 */
	private Group createRequestGroup()
	{
	    final Group requestGroup = new Group();
	    requestGroup.setName( ScheduledMeterReadModel.REQUEST_COMMAND_STRING + ReportFactory.NAME_GROUP);
	    requestGroup.addField(ScheduledMeterReadModel.SCHEDULE_NAME_STRING);
		requestGroup.addField(ScheduledMeterReadModel.SCHEDULE_START_TIME_STRING);
		requestGroup.addField(ScheduledMeterReadModel.SCHEDULE_STOP_TIME_STRING);
	    requestGroup.addField( ScheduledMeterReadModel.REQUEST_COMMAND_STRING);
	    requestGroup.addField( ScheduledMeterReadModel.REQUEST_START_TIME_STRING);
	    requestGroup.addField(ScheduledMeterReadModel.REQUEST_STOP_TIME_STRING);

	    GroupHeader header = ReportFactory.createGroupHeaderDefault();

	    LabelElementFactory factory = ReportFactory.createLabelElementDefault(getModel(), ScheduledMeterReadModel.REQUEST_COMMAND_COLUMN);
	    factory.setText(factory.getText() + ":");
	    header.addElement(factory.createElement());

	    TextFieldElementFactory tfactory = ReportFactory.createTextFieldElementDefault(getModel(), ScheduledMeterReadModel.REQUEST_COMMAND_COLUMN);
	    tfactory.setBold(false);
	    tfactory.setAbsolutePosition(new Point2D.Float(100, 1));	//override the posX location
	    header.addElement(tfactory.createElement());

	    factory = ReportFactory.createLabelElementDefault(getModel(), ScheduledMeterReadModel.REQUEST_START_TIME_COLUMN);
	    factory.setText(factory.getText() + ":");
	    header.addElement(factory.createElement());

	    tfactory = ReportFactory.createTextFieldElementDefault(getModel(), ScheduledMeterReadModel.REQUEST_START_TIME_COLUMN);
	    tfactory.setBold(false);
	    tfactory.setAbsolutePosition(new Point2D.Float(460, 1));	//override the posX location
	    header.addElement(tfactory.createElement());
	    
	    factory = ReportFactory.createLabelElementDefault(getModel(), ScheduledMeterReadModel.REQUEST_STOP_TIME_COLUMN);
	    factory.setText(factory.getText() + ":");
	    header.addElement(factory.createElement());

	    tfactory = ReportFactory.createTextFieldElementDefault(getModel(), ScheduledMeterReadModel.REQUEST_STOP_TIME_COLUMN);
	    tfactory.setBold(false);
	    tfactory.setAbsolutePosition(new Point2D.Float(610, 1));	//override the posX location
	    header.addElement(tfactory.createElement());
	    
	    
	    header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", Color.LIGHT_GRAY, new BasicStroke(0.5f), 12));
		for (int i = ScheduledMeterReadModel.DEVICE_NAME_COLUMN; i < getModel().getColumnCount(); i++)
		{
		    factory = ReportFactory.createLabelElementDefault(getModel(), i);
		    factory.setColor(Color.DARK_GRAY);
		    factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY() + 12));
		    header.addElement(factory.createElement());
		}

		header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", Color.LIGHT_GRAY, new BasicStroke(0.5f), 26));
		header.setRepeat(true);
		header.getStyle().setStyleProperty(ElementStyleSheet.VALIGNMENT, ElementAlignment.TOP);
	    requestGroup.setHeader(header);
	  	return requestGroup;
	}
	
	/**
	 * Create a Group for CollectionGroup, (by scheduleName).  
	 * @return Group
	 */
	private Group createCollGrpGroup()
	{
		final Group collGrpGroup = new Group();
		collGrpGroup.setName(ScheduledMeterReadModel.COLLECTION_GROUP_STRING + ReportFactory.NAME_GROUP);
		collGrpGroup.addField(ScheduledMeterReadModel.SCHEDULE_NAME_STRING);
		collGrpGroup.addField(ScheduledMeterReadModel.SCHEDULE_START_TIME_STRING);
		collGrpGroup.addField(ScheduledMeterReadModel.SCHEDULE_STOP_TIME_STRING);
	    collGrpGroup.addField( ScheduledMeterReadModel.REQUEST_COMMAND_STRING);
	    collGrpGroup.addField( ScheduledMeterReadModel.REQUEST_START_TIME_STRING);
	    collGrpGroup.addField(ScheduledMeterReadModel.REQUEST_STOP_TIME_STRING);
		collGrpGroup.addField(ScheduledMeterReadModel.COLLECTION_GROUP_STRING);
		  
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 20));

		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), ScheduledMeterReadModel.COLLECTION_GROUP_COLUMN);
	    factory.setText(factory.getText() + ":");
	    header.addElement(factory.createElement());

	    TextFieldElementFactory  tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), ScheduledMeterReadModel.COLLECTION_GROUP_COLUMN);
	    tfactory.setAbsolutePosition(new Point2D.Float(150, 1));	//override the posX location
	    
	    header.addElement(tfactory.createElement());
	    header.addElement(ReportFactory.createBasicLine("line3", 0.5f, 18));
		for (int i = ScheduledMeterReadModel.DEVICE_NAME_COLUMN; i < getModel().getColumnCount(); i++)
		{
		    factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
		    factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties()[i].getWidth(), 12));
		    factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY() + 18));
		    header.addElement(factory.createElement());
		}

		header.addElement(ReportFactory.createBasicLine("line3", 0.5f, 32));
		collGrpGroup.setHeader(header);
	
//		GroupFooter footer = ReportFactory.createGroupFooterDefault();
//		tfactory = tfactory = new TextFieldElementFactory();
//		tfactory.setAbsolutePosition(new Point2D.Float(0, 1));
//		tfactory.setMinimumSize(new FloatDimension(100, 12));
//		tfactory.setHorizontalAlignment(ElementAlignment.CENTER);
//		tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
//		tfactory.setFieldname("countItem");
//		tfactory.setDynamicHeight(Boolean.TRUE);
//		footer.addElement(tfactory.createElement());
//		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 12));
//		footer.getStyle().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
//		footer.addElement(ReportFactory.createBasicLine("line4", 0.5f, 4));
//		collGrpGroup.setFooter(footer);
		
		return collGrpGroup;
	}

	/**
	 * Create a Group for ScheduleName, (by StatusCode).  
	 * @return Group
	 */
	private Group createStatusCodeGroup()
	{
		final Group statCodeGroup = new Group();
		statCodeGroup.setName(ScheduledMeterReadModel.STATUS_CODE_STRING + ReportFactory.NAME_GROUP);
		statCodeGroup.addField(ScheduledMeterReadModel.SCHEDULE_NAME_STRING);
		statCodeGroup.addField(ScheduledMeterReadModel.COLLECTION_GROUP_STRING);
		statCodeGroup.addField(ScheduledMeterReadModel.STATUS_CODE_STRING);
		  
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		TextFieldElementFactory tfactory = new TextFieldElementFactory();
		tfactory.setAbsolutePosition(new Point2D.Float(0, 1));
		tfactory.setMinimumSize(new FloatDimension(100, 12));
		tfactory.setHorizontalAlignment(ElementAlignment.CENTER);
		tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
		tfactory.setFieldname("totalItemCount");
		tfactory.setDynamicHeight(Boolean.TRUE);
		footer.addElement(tfactory.createElement());
//		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 12));
//		footer.getStyle().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
//		footer.addElement(ReportFactory.createBasicLine("line4", 0.5f, 4));
		statCodeGroup.setFooter(footer);
		return statCodeGroup;
	}

	/**
	 * Creates the function collection. The xml definition for this construct:
	 * @return the functions.
	 * @throws FunctionInitializeException if there is a problem initialising the functions.
	 */
	protected ExpressionCollection getExpressions() throws FunctionInitializeException
	{
		super.getExpressions();
		
		ItemHideFunction hideItem = new ItemHideFunction();
		hideItem.setName("hideItem");
		hideItem.setField(ScheduledMeterReadModel.DEVICE_NAME_STRING);
		hideItem.setElement(ScheduledMeterReadModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
		expressions.add(hideItem);

		totalItemCount = new TotalItemCountFunction();
		totalItemCount.setName("totalItemCount");
		totalItemCount.setGroup(ScheduledMeterReadModel.STATUS_CODE_STRING + ReportFactory.NAME_GROUP);
		totalItemCount.setActive(true);
		expressions.add(totalItemCount);

		return expressions;
	}


	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createScheduleGroup());
	  if ( ((ScheduledMeterReadModel)getModel()).getGroupBy() == ScheduledMeterReadModel.GroupBy.GROUP_BY_SCHEDULE_REQUESTS)
		  list.add(createRequestGroup());
//	  list.add(createCollGrpGroup());
//	  list.add(createStatusCodeGroup());
	  return list;
	}


	/**
	 * Creates the itemBand, the rows of data.
	 * @return the item band.
	 */
	protected ItemBand createItemBand()
	{
		ItemBand items = ReportFactory.createItemBandDefault();

		if(showBackgroundColor)
		{
			items.addElement(StaticShapeElementFactory.createRectangleShapeElement
				("background", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0),
				new java.awt.geom.Rectangle2D.Float(0, 0, -100, -100), false, true));
			items.addElement(StaticShapeElementFactory.createHorizontalLine
				("top", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f), 0));
			items.addElement(StaticShapeElementFactory.createHorizontalLine
				("bottom", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f), 10));
		}

		TextFieldElementFactory factory;
		for (int i = ScheduledMeterReadModel.DEVICE_NAME_COLUMN; i < getModel().getColumnCount(); i++)
		{
		    factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
		    items.addElement(factory.createElement());
		}
	
		return items;
	}
}
