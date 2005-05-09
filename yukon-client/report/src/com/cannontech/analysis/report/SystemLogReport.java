package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Date;

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
import org.jfree.report.modules.gui.base.PreviewDialog;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.LMControlLogModel;
import com.cannontech.analysis.tablemodel.SystemLogModel;

/**
 * Created on Dec 15, 2003
 * Creates a SystemLogReport using the com.cannontech.analysis.tablemodel.SystemLogModel tableModel
 * Groups data by Date.  Orders asc/desc based on tableModel definition.  
 * @author snebben
 */
public class SystemLogReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 */
	public SystemLogReport()
	{
		this(new SystemLogModel());
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param data_ - SystemLogModel TableModel data
	 */
	public SystemLogReport(SystemLogModel model_)
	{
		super();
		setModel(model_);
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param startTime_ - startTime in millis for data query
	 * @param stopTime_ - stopTime in millis for data query
	 * @param logType_ - SystemLog.TYPE_x, type of logging to report on. 
	 * 
	 */
	public SystemLogReport(Date start_, Date stop_, Integer logType_)
	{
		this(new SystemLogModel( start_, stop_,  logType_));
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

		//Define start and stop parameters for a default 90 day report.
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		cal.add(java.util.Calendar.DATE, 1);
		Date stop = cal.getTime();
		cal.add(java.util.Calendar.DATE, -90);
		Date start = cal.getTime();

//		SystemLogModel model = new SystemLogModel(start, stop);
		LMControlLogModel model = new LMControlLogModel(start, stop);

		//Initialize the report data and populate the TableModel (collectData).
		SystemLogReport sysLogReport = new SystemLogReport(model);
		sysLogReport.getModel().collectData();

		//Create the report
		JFreeReport report = sysLogReport.createReport();
		report.setData(sysLogReport.getModel());
		
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
	 * Create a Group for SystemLog.date column.  
	 * @return
	 */
	private Group createDateGroup()
	{
		final Group dateGroup = new Group();
		dateGroup.setName(SystemLogModel.DATE_STRING + ReportFactory.NAME_GROUP);
		dateGroup.addField(SystemLogModel.DATE_STRING);

		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), SystemLogModel.DATE_COLUMN);
	  	header.addElement(tfactory.createElement());
		
		header.addElement(ReportFactory.createBasicLine("dGroupLine", 0.5f, 20));

		LabelElementFactory factory;
		//Add all columns (excluding Date) to the table model.
		for (int i = 1; i < getModel().getColumnNames().length; i++)
		{		
			factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18));
			header.addElement(factory.createElement());
		}
		dateGroup.setHeader(header);

		GroupFooter footer = ReportFactory.createGroupFooterDefault();
//		dateGroup.setFooter(footer);
		
		return dateGroup;
	}

	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
		final GroupList list = new GroupList();
		//Add a Grouping for Date column.
		list.add(createDateGroup());
		return list;
	}


	/**
	 * Creates the itemBand, the rows of data.
	 * @return the item band.
	 */
	protected ItemBand createItemBand()
	{
		ItemBand items = ReportFactory.createItemBandDefault();
	
		if( showBackgroundColor )
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
		//Start at 1, we don't want to include the Date column, Date is our group by column.
		for (int i = 1; i < getModel().getColumnNames().length; i++)
		{
			factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			items.addElement(factory.createElement());
		}

		return items;
	}
}
