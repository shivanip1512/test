package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.jfree.report.Boot;
import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.ReportFooter;
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
import com.cannontech.analysis.tablemodel.ActivityModel;
import com.cannontech.util.ServletUtil;

/**
 * Created on Dec 15, 2003
 * Creates a LGControlLogReport using the com.cannontech.analysis.data.loadgroup.LMControlLogData tableModel
 * Groups data by Date.  Orders asc/desc based on tableModel definition.  
 * @author snebben
 */
public class ECActivityLogReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 */
	public ECActivityLogReport()
	{
		this(new ActivityModel());
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param data_ - SystemLogModel TableModel data
	 */
	public ECActivityLogReport(ActivityModel model_)
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
	public ECActivityLogReport(long startTime_, long stopTime_)
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
		YukonReportBase report = ReportFuncs.createYukonReport(ReportTypes.EC_ACTIVITY_LOG_DATA);
		//Define default start and stop parameters for a default year to date report.
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		cal.add(java.util.Calendar.DATE, 1);	//default stop date is tomorrow
		long stop = cal.getTimeInMillis();

		cal.set(java.util.Calendar.DATE, 1);
		cal.set(java.util.Calendar.MONTH, 0);
		long start = cal.getTimeInMillis();	//default start date is begining of year

		report.getModel().setStartTime(start);
		report.getModel().setStopTime(stop);

		for (int i = 0; i < args.length; i++)
		{
			String arg = (String)args[i].toLowerCase();
				
			int startIndex = arg.indexOf('=');
			startIndex += 1;
			String subString = arg.substring(startIndex);				
			
			if( arg.startsWith("ec"))
				report.getModel().setECIDs(Integer.valueOf(subString));
			else if( arg.startsWith("start"))
			{
				Date startDate = ServletUtil.parseDateStringLiberally(subString);
				report.getModel().setStartTime(startDate.getTime());
			}
			else if( arg.startsWith("stop"))
			{
				Date stopDate = ServletUtil.parseDateStringLiberally(subString);
				report.getModel().setStartTime(stopDate.getTime());
			}			
		}
		
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
		hideItem.setName(ActivityModel.CONTACT_STRING + " Hidden");
		hideItem.setProperty("field", ActivityModel.CONTACT_STRING);
		hideItem.setProperty("element", ActivityModel.CONTACT_STRING+" Element");
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName(ActivityModel.ACCOUNT_NUMBER_STRING + " Hidden");
		hideItem.setProperty("field", ActivityModel.ACCOUNT_NUMBER_STRING);
		hideItem.setProperty("element", ActivityModel.ACCOUNT_NUMBER_STRING+" Element");
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName(ActivityModel.USERNAME_STRING + " Hidden");
		hideItem.setProperty("field", ActivityModel.USERNAME_STRING);
		hideItem.setProperty("element", ActivityModel.USERNAME_STRING+" Element");
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
		ecGroup.setName(ActivityModel.ENERGY_COMPANY_STRING +" Group");
		ecGroup.addField(getModel().getColumnName(ActivityModel.ENERGY_COMPANY_COLUMN));

		final GroupHeader header = new GroupHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));

		final TextFieldElementFactory tfactory = new TextFieldElementFactory();
		tfactory.setName(ActivityModel.ENERGY_COMPANY_COLUMN + " Group Element");
		tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(ActivityModel.ENERGY_COMPANY_COLUMN).getPositionX(), getModel().getColumnProperties(ActivityModel.ENERGY_COMPANY_COLUMN).getPositionY()));
		tfactory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(ActivityModel.ENERGY_COMPANY_COLUMN).getWidth(), getModel().getColumnProperties(ActivityModel.ENERGY_COMPANY_COLUMN).getHeight()));
		tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
		tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
		tfactory.setNullString("<null>");
		tfactory.setFieldname(getModel().getColumnName(ActivityModel.ENERGY_COMPANY_COLUMN));
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
		ecGroup.setFooter(footer);
		
		return ecGroup;
	}


	/**
	 * Create a Group for LMControlLogData.Date column.  
	 * @return
	 */
	private Group createContactGroup()
	{
		final Group contGroup = new Group();
		contGroup.setName(ActivityModel.CONTACT_STRING +" Group");
		contGroup.addField(getModel().getColumnName(ActivityModel.ENERGY_COMPANY_COLUMN));
		contGroup.addField(getModel().getColumnName(ActivityModel.CONTACT_COLUMN));

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
				if( i == ActivityModel.ACTION_COUNT_COLUMN )
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





	/**
	 * Creates the report footer.
	 * @return the report footer.
	 */
	protected ReportFooter createReportFooter()
	{

		final ReportFooter footer = new ReportFooter();
		footer.getStyle().setStyleProperty( ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 48));
//		footer.getBandDefaults().setFontDefinitionProperty( new FontDefinition("Serif", 12, true, false, false, false));
		footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 10));

		
		//Add all columns (excluding Date) to the table model.
		LabelElementFactory factory = new LabelElementFactory();
		factory.setName(ActivityModel.TOTALS_HEADER_STRING+" Group Element");
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		factory.setAbsolutePosition(new Point2D.Float(0, 1));
		factory.setMinimumSize(new FloatDimension(100, 18));
		factory.setText(ActivityModel.TOTALS_HEADER_STRING);
		footer.addElement(factory.createElement());
		
		factory.setName(ActivityModel.ACTION_STRING+ " Group Element");
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		factory.setAbsolutePosition(new Point2D.Float(150, 1));
		factory.setMinimumSize(new FloatDimension(348, 18));
		factory.setText(ActivityModel.ACTION_STRING);
		footer.addElement(factory.createElement());

		factory.setName(ActivityModel.ACTION_COUNT_STRING + " Group Element");
		factory.setHorizontalAlignment(ElementAlignment.RIGHT);
		factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		factory.setAbsolutePosition(new Point2D.Float(500, 1));
		factory.setMinimumSize(new FloatDimension(25, 18));
		factory.setText(ActivityModel.ACTION_COUNT_STRING);
		footer.addElement(factory.createElement());

		Iterator iter = ((ActivityModel)getModel()).getTotals().entrySet().iterator();
		int offset = 8;
		while( iter.hasNext())
		{
			offset += 10;
			Map.Entry entry = ((Map.Entry)iter.next());

			factory = new LabelElementFactory();
			factory.setName(entry.getKey().toString()+ " Group Element");
			factory.setHorizontalAlignment(ElementAlignment.LEFT);
			factory.setVerticalAlignment(ElementAlignment.BOTTOM);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(ActivityModel.ACTION_COLUMN).getPositionX(), offset));
			factory.setMinimumSize(new FloatDimension(348, 18));
			factory.setText(entry.getKey().toString());
			factory.setBold(new Boolean(false));
			footer.addElement(factory.createElement());
			
			factory = new LabelElementFactory();
			factory.setName(entry.getValue().toString() +" Group Element");
			factory.setHorizontalAlignment(ElementAlignment.RIGHT);
			factory.setVerticalAlignment(ElementAlignment.BOTTOM);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(ActivityModel.ACTION_COUNT_COLUMN).getPositionX(), offset));
			factory.setMinimumSize(new FloatDimension(25, 18));
			factory.setText(entry.getValue().toString());
			factory.setBold(new Boolean(false));
			footer.addElement(factory.createElement());
		}
		offset += 20;
		footer.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(0.5f), new java.awt.geom.Line2D.Float(0, 20, 0, 20)));
		
		factory = new LabelElementFactory();
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, offset));
		factory.setMinimumSize(new FloatDimension(-100, 16));
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText("*** END OF REPORT ***");
		footer.addElement(factory.createElement());
		return footer;
	
	}
		
}



