package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;

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
import org.jfree.report.function.ItemHideFunction;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.tablemodel.ActivityDetailModel;

/**
 * Created on Dec 15, 2003
 * Creates a LGControlLogReport using the com.cannontech.analysis.data.loadgroup.LMControlLogData tableModel
 * Groups data by Date.  Orders asc/desc based on tableModel definition.  
 * @author snebben
 */
public class ECActivityDetailReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 */
	public ECActivityDetailReport()
	{
		this(new ActivityDetailModel());
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param data_ - SystemLogModel TableModel data
	 */
	public ECActivityDetailReport(ActivityDetailModel model_)
	{
		super();
		setModel(model_);
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param startTime_ - startTime in millis for data query
	 * @param stopTime_ - stopTime in millis for data query
	 * 
	 */
	public ECActivityDetailReport(long startTime_, long stopTime_)
	{
		this(new ActivityDetailModel(startTime_, stopTime_ ));
		
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
		YukonReportBase report = ReportFuncs.createYukonReport(ReportTypes.EC_ACTIVITY_DETAIL_DATA);
		((ActivityDetailModel)report.getModel()).setProgramInfoOnly(true);
		((ActivityDetailModel)report.getModel()).setECIDs(new Integer(1010));
		
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		cal.add(java.util.Calendar.DATE, 1);
		long stop = cal.getTimeInMillis();
//		cal.add(java.util.Calendar.DATE, -30);
		cal.set(java.util.Calendar.DATE, 1);
		cal.set(java.util.Calendar.MONTH, 0);
		long start = cal.getTimeInMillis();

		report.getModel().setStartTime(start);
		report.getModel().setStopTime(stop);
//		model.setEnergyCompanyID(new Integer(1004));
		report.getModel().collectData();

		//Create the report
		JFreeReport freeReport = report.createReport();
		freeReport.setData(report.getModel());
		
		final PreviewDialog dialog = new PreviewDialog(freeReport);
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

		ItemHideFunction hideItem = new ItemHideFunction();
		hideItem.setName(ActivityDetailModel.DATE_TIME_STRING + " Hidden");
		hideItem.setProperty("field", ActivityDetailModel.DATE_TIME_STRING);
		hideItem.setProperty("element", ActivityDetailModel.DATE_TIME_STRING+" Element");
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName(ActivityDetailModel.CONTACT_STRING+ " Hidden");
		hideItem.setProperty("field", ActivityDetailModel.CONTACT_STRING);
		hideItem.setProperty("element", ActivityDetailModel.CONTACT_STRING +" Element");
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName(ActivityDetailModel.USERNAME_STRING + " Hidden");
		hideItem.setProperty("field", ActivityDetailModel.USERNAME_STRING);
		hideItem.setProperty("element", ActivityDetailModel.USERNAME_STRING+" Element");
		expressions.add(hideItem);
		
		hideItem = new ItemHideFunction();
		hideItem.setName(ActivityDetailModel.ACCOUNT_NUMBER_STRING + " Hidden");
		hideItem.setProperty("field", ActivityDetailModel.ACCOUNT_NUMBER_STRING);
		hideItem.setProperty("element", ActivityDetailModel.ACCOUNT_NUMBER_STRING+" Element");
		expressions.add(hideItem);

//		expressions.add(getDateExpression(getModel().getColumnProperties(5).getValueFormat(), getModel().getColumnName(5)));
		return expressions;
	}

	/**
	 * Create a Group for LMControlLogData.Date column.  
	 * @return
	 */
	private Group createECGroup()
	{
		final Group ecGroup = new Group();
		ecGroup.setName(ActivityDetailModel.ENERGY_COMPANY_STRING +" Group");
		ecGroup.addField(getModel().getColumnName(ActivityDetailModel.ENERGY_COMPANY_COLUMN));

		final GroupHeader header = new GroupHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 12));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
		
		final TextFieldElementFactory tfactory = new TextFieldElementFactory();
		tfactory.setName(ActivityDetailModel.ENERGY_COMPANY_STRING + " Group Element");
		tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(ActivityDetailModel.ENERGY_COMPANY_COLUMN).getPositionX(), getModel().getColumnProperties(ActivityDetailModel.ENERGY_COMPANY_COLUMN).getPositionY()));
		tfactory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(ActivityDetailModel.ENERGY_COMPANY_COLUMN).getWidth(), getModel().getColumnProperties(ActivityDetailModel.ENERGY_COMPANY_COLUMN).getHeight()));
		tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
		tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
		tfactory.setNullString("<null>");
		tfactory.setFieldname(getModel().getColumnName(ActivityDetailModel.ENERGY_COMPANY_COLUMN));
		header.addElement(tfactory.createElement());
		
		header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(0.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 20f), new java.awt.geom.Line2D.Float(0, 20, 100, 20)));
		
		ecGroup.setHeader(header);
		
		final GroupFooter footer = new GroupFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
		footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
//		ecGroup.setFooter(footer);
		
		return ecGroup;
	}


	/**
	 * Create a Group for LMControlLogData.Date column.  
	 * @return
	 */
	private Group createTimestampGroup()
	{
		int colIndex = ActivityDetailModel.DATE_COLUMN;
		
		final Group tsGroup = new Group();
		tsGroup.setName(ActivityDetailModel.DATE_TIME_STRING +" Group");
		tsGroup.addField(getModel().getColumnName(ActivityDetailModel.ENERGY_COMPANY_COLUMN));
		tsGroup.addField(getModel().getColumnName(colIndex));

		final GroupHeader header = new GroupHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
		header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(0.5f), new java.awt.geom.Line2D.Float(0, 28, 60, 28)));
		
		header.addElement(StaticShapeElementFactory.createLineShapeElement("line2", null, new BasicStroke(0.5f), new java.awt.geom.Line2D.Float(75, 28, 800, 28)));
		
		final DateFieldElementFactory dfactory = new DateFieldElementFactory();
		dfactory.setName(ActivityDetailModel.DATE_TIME_STRING + " Group Element");
		dfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(colIndex).getPositionX(), 10));
		dfactory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(colIndex).getWidth(), getModel().getColumnProperties(colIndex).getHeight()));
		dfactory.setHorizontalAlignment(ElementAlignment.LEFT);
		dfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
		dfactory.setNullString("<null>");
		dfactory.setFieldname(getModel().getColumnName(colIndex));
		dfactory.setFormatString(getModel().getColumnProperties(colIndex).getValueFormat());
		dfactory.setBold(new Boolean(true));
		header.addElement(dfactory.createElement());
		
		//Add all columns (excluding Date) to the table model.
		for (int i = ActivityDetailModel.CONTACT_COLUMN; i < ActivityDetailModel.DESCRIPTION_COLUMN; i++)
		{
			LabelElementFactory factory = new LabelElementFactory();
			factory.setName(getModel().getColumnName(i)+" Group Element");
			factory.setHorizontalAlignment(ElementAlignment.LEFT);
			factory.setVerticalAlignment(ElementAlignment.BOTTOM);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 10));
			factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), getModel().getColumnProperties(i).getHeight() ));
			factory.setText(getModel().getColumnNames()[i]);
			header.addElement(factory.createElement());
		}
		
		tsGroup.setHeader(header);
				
		final GroupFooter footer = new GroupFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
		tsGroup.setFooter(footer);

		return tsGroup;
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
		list.add(createTimestampGroup());
		return list;
	}


	/**
	 * Creates the itemBand, the rows of data.
	 * @return the item band.
	 */
	protected ItemBand createItemBand()
	{
		final ItemBand items = new ItemBand();
		items.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
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
		for (int i = ActivityDetailModel.TIME_COLUMN; i < getModel().getColumnNames().length; i++)
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
				factory.setHorizontalAlignment(ElementAlignment.LEFT);
				factory.setVerticalAlignment(ElementAlignment.MIDDLE);
				factory.setNullString("<null>");
				factory.setFieldname(getModel().getColumnNames()[i]);
				items.addElement(factory.createElement());
			}
		}

		return items;
	}
	
	/**
	 * @return
	 */
	public PageFormat getPageFormat()
	{
		super.getPageFormat();
		super.pageFormat.setOrientation(PageFormat.LANDSCAPE);
		return pageFormat;
	}
}
