package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;

import org.jfree.report.Boot;
import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.elementfactory.DateFieldElementFactory;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.tablemodel.SystemLogModel;
import com.cannontech.database.db.point.SystemLog;

/**
 * Created on Dec 15, 2003
 * Creates a LGControlLogReport using the com.cannontech.analysis.data.loadgroup.LMControlLogData tableModel
 * Groups data by Date.  Orders asc/desc based on tableModel definition.  
 * @author snebben
 */
public class LMControlLogReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 */
	public LMControlLogReport()
	{
		super();
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param data_ - SystemLogModel TableModel data
	 */
	public LMControlLogReport(SystemLogModel data_)
	{
		super();
		data = data_;
		data.setReportType(ReportTypes.LM_CONTROL_LOG_DATA);
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param startTime_ - startTime in millis for data query
	 * @param stopTime_ - stopTime in millis for data query
	 * @param logType_ - SystemLog.TYPE_x, type of logging to report on. 
	 * 
	 */
	public LMControlLogReport(long startTime_, long stopTime_, Integer logType_)
	{
		this(new SystemLogModel( startTime_, stopTime_,  logType_));
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
		LMControlLogReport lmControlLogReport= new LMControlLogReport();
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		cal.add(java.util.Calendar.DATE, 1);
		long stop = cal.getTimeInMillis();
		cal.add(java.util.Calendar.DATE, -90);
		long start = cal.getTimeInMillis();

		//Initialize the report data and populate the TableModel (collectData).
		lmControlLogReport.data = new SystemLogModel(start, stop, new Integer(SystemLog.TYPE_LOADMANAGEMENT));
		lmControlLogReport.data.setReportType(ReportTypes.LM_CONTROL_LOG_DATA); 
//		lmControlLogReport.data = new LMControlLog(start, stop, new Integer(SystemLog.TYPE_LOADMANAGEMENT));
		lmControlLogReport.data.collectData();

		//Define the report Paper properties and format.
		java.awt.print.Paper reportPaper = new java.awt.print.Paper();
		reportPaper.setImageableArea(30, 40, 552, 712);	//8.5 x 11 -> 612w 792h
		java.awt.print.PageFormat pageFormat = new java.awt.print.PageFormat();
		pageFormat.setPaper(reportPaper);
		
		//Create the report
		JFreeReport report = lmControlLogReport.createReport();
		report.setDefaultPageFormat(pageFormat);
		report.setData(lmControlLogReport.data);
		
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
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.report.YukonReportBase#getExpressions()
	 */
	protected ExpressionCollection getExpressions() throws FunctionInitializeException
	{
		super.getExpressions();
		expressions.add(getDateExpression(data.getColumnProperties(0).getValueFormat(), data.getColumnName(0)));
		return expressions;
	}
	
	/**
	 * Create a Group for LMControlLogData.Date column.  
	 * @return
	 */
	private Group createDateGroup()
	{
		final Group dateGroup = new Group();
		dateGroup.setName("Date Exp Group");
		dateGroup.addField(DATE_EXPRESSION);

		final GroupHeader header = new GroupHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));

		final TextFieldElementFactory tfactory = new TextFieldElementFactory();
		tfactory.setName("Date Group Element");
		tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(data.getColumnProperties(0).getPositionX(), data.getColumnProperties(0).getPositionY()));
		tfactory.setMinimumSize(new FloatDimension(data.getColumnProperties(0).getWidth(), data.getColumnProperties(0).getHeight()));
		tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
		tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
		tfactory.setNullString("<null>");
		tfactory.setFieldname(DATE_EXPRESSION);
	  	header.addElement(tfactory.createElement());
		
		header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(0.5f), new java.awt.geom.Line2D.Float(0, 20, 0, 20)));

		//Add all columns (excluding Date) to the table model.
		for (int i = 1; i < data.getColumnNames().length; i++)
		{
			LabelElementFactory factory = new LabelElementFactory();
			factory.setHorizontalAlignment(ElementAlignment.LEFT);
			factory.setVerticalAlignment(ElementAlignment.BOTTOM);
			factory.setAbsolutePosition(new Point2D.Float(data.getColumnProperties(i).getPositionX(), 18));
			factory.setMinimumSize(new FloatDimension(data.getColumnProperties(i).getWidth(), data.getColumnProperties(i).getHeight() ));
			factory.setText(data.getColumnNames()[i]);
			header.addElement(factory.createElement());
		}
		
		dateGroup.setHeader(header);

		final GroupFooter footer = new GroupFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
		footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
		
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
		final ItemBand items = new ItemBand();
		items.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 10));
		items.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 10));
	
		if( showBackgroundColor )
		{
			items.addElement(StaticShapeElementFactory.createRectangleShapeElement
				("background", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0),
					new java.awt.geom.Rectangle2D.Float(0, 0, -100, -100), false, true));
			items.addElement(StaticShapeElementFactory.createLineShapeElement
				("top", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f),
					new java.awt.geom.Line2D.Float(0, 0, 0, 0)));
			items.addElement(StaticShapeElementFactory.createLineShapeElement
				("bottom", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f),
					new java.awt.geom.Line2D.Float(0, 10, 0, 10)));
		}
		//Start at 1, we don't want to include the Date column, Date is our group by column.
		for (int i = 1; i < data.getColumnNames().length; i++)
		{
			TextFieldElementFactory factory = new TextFieldElementFactory();

			if( data.getColumnClass(i).equals(String.class))
				factory = new TextFieldElementFactory();
			else if( data.getColumnClass(i).equals(java.util.Date.class))
			{
				factory = new DateFieldElementFactory();
				((DateFieldElementFactory)factory).setFormatString(data.getColumnProperties(i).getValueFormat());
			}
			
			if( factory != null)
			{
				factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(data.getColumnProperties(i).getPositionX(),data.getColumnProperties(i).getPositionY()));
				factory.setMinimumSize(new FloatDimension(data.getColumnProperties(i).getWidth(), 10));
				factory.setHorizontalAlignment(ElementAlignment.LEFT);
				factory.setVerticalAlignment(ElementAlignment.MIDDLE);
				factory.setNullString("<null>");
				factory.setFieldname(data.getColumnNames()[i]);
				items.addElement(factory.createElement());
			}
		}

		return items;
	}
}
