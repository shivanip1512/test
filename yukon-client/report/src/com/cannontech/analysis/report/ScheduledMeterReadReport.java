package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.JFreeReportBoot;
import org.jfree.report.ReportFooter;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.ui.FloatDimension;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.tablemodel.ScheduledMeterReadModel;
import com.cannontech.spring.YukonSpringHook;

/**
 * Created on Dec 15, 2003
 * Creates a MissedMeterReport using the com.cannontech.analysis.data.MissedMeterData tableModel
 * Groups data by Collection Group and then by Device.  
 * @author snebben
 */
public class ScheduledMeterReadReport extends YukonReportBase
{
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
		model.setFilterModelType(ReportFilter.GROUPS);
		model.setGroupBy(ScheduledMeterReadModel.GroupBy.GROUP_BY_DEVICE);
//		model.setStatusCodeType(ScheduledMeterReadModel.StatusCodeType.SUCCESS_METER_READ_TYPE);
		YukonReportBase meterReadReport = new ScheduledMeterReadReport(model);
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
        
        
        if ( ((ScheduledMeterReadModel)getModel()).getGroupBy() != ScheduledMeterReadModel.GroupBy.GROUP_BY_SCHEDULE_REQUESTS){
            header.addElement(StaticShapeElementFactory.createHorizontalLine("line2", Color.LIGHT_GRAY, new BasicStroke(0.5f), 18));
            for (int i = ScheduledMeterReadModel.DEVICE_NAME_COLUMN; i < getModel().getColumnCount(); i++)
            {
                factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
                factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties()[i].getWidth(), 12));
                factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY() + 20));
                header.addElement(factory.createElement());
            }
        }
            
	    scheduleGroup.setHeader(header);
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
	 * Create a Group for DeviceName, (by scheduleName, collGorup).  
	 * @return Group
	 */
	private Group createDeviceGroup()
	{
		final Group collGrpGroup = new Group();
		collGrpGroup.setName(ScheduledMeterReadModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
		collGrpGroup.addField(ScheduledMeterReadModel.SCHEDULE_NAME_STRING);
		collGrpGroup.addField(ScheduledMeterReadModel.SCHEDULE_START_TIME_STRING);
		collGrpGroup.addField(ScheduledMeterReadModel.SCHEDULE_STOP_TIME_STRING);
		collGrpGroup.addField(ScheduledMeterReadModel.DEVICE_NAME_STRING);
		  
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 2));

	    header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", Color.LIGHT_GRAY, new BasicStroke(0.5f), 1));
		collGrpGroup.setHeader(header);		
		return collGrpGroup;
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
	  else {
		  list.add(createDeviceGroup());
	  }
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
	
	@Override
	protected ReportFooter createReportFooter()
	{
		ReportFooter footer = ReportFactory.createReportFooterDefault();
		
		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault("TOTALS", 0, 1, 792);
		factory.setHorizontalAlignment(ElementAlignment.CENTER);		
		footer.addElement(factory.createElement());
		
		factory = ReportFactory.createGroupLabelElementDefault( ScheduledMeterReadModel.STATUS_CODE_STRING, 0, 1,
				getModel().getColumnProperties(ScheduledMeterReadModel.STATUS_CODE_COLUMN).getWidth());
		footer.addElement(factory.createElement());
		
		factory = ReportFactory.createGroupLabelElementDefault("Counts", 
				getModel().getColumnProperties(ScheduledMeterReadModel.STATUS_CODE_COLUMN).getPositionX(), 
				1, getModel().getColumnProperties(ScheduledMeterReadModel.STATUS_CODE_COLUMN).getWidth());
		factory.setHorizontalAlignment(ElementAlignment.RIGHT);
		footer.addElement(factory.createElement());
		
		Iterator iter = ((ScheduledMeterReadModel)getModel()).getTotals().entrySet().iterator();
		int offset = 8;
		
		DeviceErrorTranslatorDao deviceErrorTrans = YukonSpringHook.getBean("deviceErrorTranslator", DeviceErrorTranslatorDao.class);
		offset += 12;
		while( iter.hasNext())
		{		
			Map.Entry entry = ((Map.Entry)iter.next());
			String statusCode = entry.getKey().toString();
			
			factory = ReportFactory.createGroupLabelElementDefault(statusCode, 0, 
				offset, getModel().getColumnProperties(ScheduledMeterReadModel.STATUS_CODE_COLUMN).getWidth());
			footer.addElement(factory.createElement());
			
			DeviceErrorDescription deviceErrorDesc = deviceErrorTrans.translateErrorCode(Integer.valueOf(statusCode));
			factory = ReportFactory.createGroupLabelElementDefault(deviceErrorDesc.getDescription(), 50, 
					offset, 692);
			footer.addElement(factory.createElement());
		
			factory = ReportFactory.createGroupLabelElementDefault(entry.getValue().toString(), 
				getModel().getColumnProperties(ScheduledMeterReadModel.STATUS_CODE_COLUMN).getPositionX(), 
				offset, getModel().getColumnProperties(ScheduledMeterReadModel.STATUS_CODE_COLUMN).getWidth());
			factory.setHorizontalAlignment(ElementAlignment.RIGHT);
			footer.addElement(factory.createElement());
			
			//Doing some magic to adjust the offset for text that exceeds the given height (by length too long)
			int x = (deviceErrorDesc.getDescription().length() / 90) + 1;
			int offExtra = x * 12;	//where 12 is the default height
			offset+=offExtra;
		}
		offset += 20;

		footer.addElement(ReportFactory.createBasicLine("rfLine",0.5f, 20));
		
		factory = new LabelElementFactory();
		factory.setAbsolutePosition(new Point2D.Float(0, offset));
		factory.setMinimumSize(new FloatDimension(-100, 16));
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText("*** END OF REPORT ***");
		footer.addElement(factory.createElement());
		return footer;
	
	}	
}
