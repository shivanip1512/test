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
import org.jfree.report.elementfactory.NumberFieldElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.activity.ActivityLog;
import com.cannontech.analysis.tablemodel.ActivityModel;

/**
 * Created on Dec 15, 2003
 * Creates a LGControlLogReport using the com.cannontech.analysis.data.loadgroup.LMControlLogData tableModel
 * Groups data by Date.  Orders asc/desc based on tableModel definition.  
 * @author snebben
 */
public class EnergyCompanyActivityLogReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 */
	public EnergyCompanyActivityLogReport()
	{
		super();
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param data_ - SystemLogModel TableModel data
	 */
	public EnergyCompanyActivityLogReport(ActivityModel model_)
	{
		super();
		model = model_;
		model.setReportType(ReportTypes.ENERGY_COMPANY_ACTIVITY_LOG_DATA);
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param startTime_ - startTime in millis for data query
	 * @param stopTime_ - stopTime in millis for data query
	 * 
	 */
	public EnergyCompanyActivityLogReport(long startTime_, long stopTime_)
	{
		this(new ActivityModel(startTime_, stopTime_ ));
		
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
		EnergyCompanyActivityLogReport ecActivityLogReport= new EnergyCompanyActivityLogReport();
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
		ActivityModel model = new ActivityModel(start, stop);
//		model.setEnergyCompanyID(new Integer(1004));
		ecActivityLogReport.setModel(model);
		ecActivityLogReport.getModel().setReportType(ReportTypes.ENERGY_COMPANY_ACTIVITY_LOG_DATA); 
		ecActivityLogReport.getModel().collectData();

		//Define the report Paper properties and format.
		java.awt.print.Paper reportPaper = new java.awt.print.Paper();
		reportPaper.setImageableArea(30, 40, 552, 712);	//8.5 x 11 -> 612w 792h
		java.awt.print.PageFormat pageFormat = new java.awt.print.PageFormat();
		pageFormat.setPaper(reportPaper);
		
		//Create the report
		JFreeReport report = ecActivityLogReport.createReport();
		report.setDefaultPageFormat(pageFormat);
		report.setData(ecActivityLogReport.getModel());
		
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
//		expressions.add(getDateExpression(getModel().getColumnProperties(5).getValueFormat(), getModel().getColumnName(5)));
		return expressions;
	}
	/**
	 * Creates the function collection. The xml definition for this construct:
	 * @return the functions.
	 * @throws FunctionInitializeException if there is a problem initialising the functions.
	 */
	protected ExpressionCollection getFunctions() throws FunctionInitializeException
	{
		super.getFunctions();
		
		org.jfree.report.function.ItemHideFunction hideItem = new org.jfree.report.function.ItemHideFunction();
		hideItem.setName(ActivityLog.CONTACT_STRING + "Hidden");
		hideItem.setProperty("field", ActivityLog.CONTACT_STRING);
		hideItem.setProperty("element", ActivityLog.CONTACT_STRING+" Element");
		functions.add(hideItem);
		
		hideItem = new org.jfree.report.function.ItemHideFunction();
		hideItem.setName(ActivityLog.ACCOUNT_NUMBER_STRING + " Hidden");
		hideItem.setProperty("field", ActivityLog.ACCOUNT_NUMBER_STRING);
		hideItem.setProperty("element", ActivityLog.ACCOUNT_NUMBER_STRING+" Element");
		functions.add(hideItem);
		
		hideItem = new org.jfree.report.function.ItemHideFunction();
		hideItem.setName(ActivityLog.USERNAME_STRING + " Hidden");
		hideItem.setProperty("field", ActivityLog.USERNAME_STRING);
		hideItem.setProperty("element", ActivityLog.USERNAME_STRING+" Element");
		functions.add(hideItem);
		
		return functions;
	}

	/**
	 * Create a Group for LMControlLogData.Date column.  
	 * @return
	 */
	private Group createECGroup()
	{
		final Group ecGroup = new Group();
		ecGroup.setName(ActivityLog.ENERGY_COMPANY_STRING +" Group");
		ecGroup.addField(getModel().getColumnName(ActivityLog.ENERGY_COMPANY_COLUMN));

		final GroupHeader header = new GroupHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));

		final TextFieldElementFactory tfactory = new TextFieldElementFactory();
		tfactory.setName(ActivityLog.ENERGY_COMPANY_COLUMN + " Group Element");
		tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(ActivityLog.ENERGY_COMPANY_COLUMN).getPositionX(), getModel().getColumnProperties(ActivityLog.ENERGY_COMPANY_COLUMN).getPositionY()));
		tfactory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(ActivityLog.ENERGY_COMPANY_COLUMN).getWidth(), getModel().getColumnProperties(ActivityLog.ENERGY_COMPANY_COLUMN).getHeight()));
		tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
		tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
		tfactory.setNullString("<null>");
		tfactory.setFieldname(getModel().getColumnName(ActivityLog.ENERGY_COMPANY_COLUMN));
	  	header.addElement(tfactory.createElement());
		
		header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(0.5f), new java.awt.geom.Line2D.Float(0, 20, 0, 20)));

		//Add all columns (excluding Date) to the table model.
		for (int i = 1; i < getModel().getColumnNames().length; i++)
		{
			LabelElementFactory factory = new LabelElementFactory();
			factory.setName(getModel().getColumnName(i)+" Group Element");
			factory.setHorizontalAlignment(ElementAlignment.LEFT);
			factory.setVerticalAlignment(ElementAlignment.BOTTOM);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18));
			factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), getModel().getColumnProperties(i).getHeight() ));
			factory.setText(getModel().getColumnNames()[i]);
			header.addElement(factory.createElement());
		}
		
		ecGroup.setHeader(header);

		final GroupFooter footer = new GroupFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
		footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
		
		return ecGroup;
	}


	/**
	 * Create a Group for LMControlLogData.Date column.  
	 * @return
	 */
	private Group createContactGroup()
	{
		final Group contGroup = new Group();
		contGroup.setName(ActivityLog.CONTACT_STRING +" Group");
		contGroup.addField(getModel().getColumnName(ActivityLog.ENERGY_COMPANY_COLUMN));
		contGroup.addField(getModel().getColumnName(ActivityLog.CONTACT_COLUMN));

		final GroupHeader header = new GroupHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 5));
		contGroup.setHeader(header);
		final GroupFooter footer = new GroupFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 5));
		contGroup.setFooter(footer);

		return contGroup;
	}
	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
		final GroupList list = new GroupList();
		//Add a Grouping for Date column.
		list.add(createECGroup());
		list.add(createContactGroup());
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
		for (int i = 1; i < getModel().getColumnNames().length; i++)
		{
			TextFieldElementFactory factory = new TextFieldElementFactory();

			if( getModel().getColumnClass(i).equals(String.class))
				factory = new TextFieldElementFactory();
			else if( getModel().getColumnClass(i).equals(Integer.class))
				factory = new NumberFieldElementFactory();
			else if( getModel().getColumnClass(i).equals(java.util.Date.class))
			{
				factory = new DateFieldElementFactory();
				((DateFieldElementFactory)factory).setFormatString(getModel().getColumnProperties(i).getValueFormat());
			}
			
			if( factory != null)
			{
				factory.setName(getModel().getColumnNames()[i]+ " Element");
				factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(i).getPositionX(),getModel().getColumnProperties(i).getPositionY()));
				factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), 10));
				if( i == ActivityLog.ACTION_COUNT_COLUMN )
					factory.setHorizontalAlignment(ElementAlignment.RIGHT);
				else 
					factory.setHorizontalAlignment(ElementAlignment.LEFT);
				factory.setVerticalAlignment(ElementAlignment.MIDDLE);
				factory.setNullString("<null>");
				factory.setFieldname(getModel().getColumnNames()[i]);
				items.addElement(factory.createElement());
			}
		}

		return items;
	}
}
