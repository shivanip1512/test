package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
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

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.tablemodel.ActivityModel;
import com.cannontech.util.ServletUtil;

/**
 * Created on Dec 15, 2003
 * Creates a ECActivityLogReport using the com.cannontech.analysis.tablemodel.ActivityModel tableModel
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
	 * Runs this report and shows a preview dialog.
 	 * @param args the arguments (ignored).
	 * @throws Exception if an error occurs (default: print a stack trace)
	*/
	public static void main(final String[] args) throws Exception
	{
		// initialize JFreeReport
		Boot.start();
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

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


		ActivityModel model = new ActivityModel(start, stop);
		model.setStartTime(start);
		model.setStopTime(stop);

		for (int i = 0; i < args.length; i++)
		{
			String arg = (String)args[i].toLowerCase();
				
			int startIndex = arg.indexOf('=');
			startIndex += 1;
			String subString = arg.substring(startIndex);				
			
			if( arg.startsWith("ec"))
				model.setECIDs(Integer.valueOf(subString));
			else if( arg.startsWith("start"))
			{
				Date startDate = ServletUtil.parseDateStringLiberally(subString);
				model.setStartTime(startDate.getTime());
			}
			else if( arg.startsWith("stop"))
			{
				Date stopDate = ServletUtil.parseDateStringLiberally(subString);
				model.setStartTime(stopDate.getTime());
			}			
		}
		
		//Define start and stop parameters for a default 90 day report.
		YukonReportBase report = new ECActivityLogReport(model);
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
		hideItem.setName(ActivityModel.CONTACT_STRING + ReportFactory.NAME_HIDDEN);
		hideItem.setProperty("field", ActivityModel.CONTACT_STRING);
		hideItem.setProperty("element", ActivityModel.CONTACT_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName(ActivityModel.ACCOUNT_NUMBER_STRING + ReportFactory.NAME_HIDDEN);
		hideItem.setProperty("field", ActivityModel.ACCOUNT_NUMBER_STRING);
		hideItem.setProperty("element", ActivityModel.ACCOUNT_NUMBER_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName(ActivityModel.USERNAME_STRING + ReportFactory.NAME_HIDDEN);
		hideItem.setProperty("field", ActivityModel.USERNAME_STRING);
		hideItem.setProperty("element", ActivityModel.USERNAME_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(hideItem);
				
		return expressions;
	}

	/**
	 * Create a Group for EnergyCompany column.  
	 * @return
	 */
	private Group createECGroup()
	{
		final Group ecGroup = new Group();
		ecGroup.setName(ActivityModel.ENERGY_COMPANY_STRING + ReportFactory.NAME_GROUP);
		ecGroup.addField(getModel().getColumnName(ActivityModel.ENERGY_COMPANY_COLUMN));

		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), ActivityModel.ENERGY_COMPANY_COLUMN);
		header.addElement(tfactory.createElement());
		
		LabelElementFactory lFactory = null;
		//Add all columns (excluding Date) to the table model.
		for (int i = 1; i < getModel().getColumnNames().length; i++)
		{
			lFactory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			lFactory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18));	//override the position
			header.addElement(lFactory.createElement());
		}
		header.addElement(ReportFactory.createBasicLine("ecHeaderLine", 0.5f, 36));		//36 position, group pos(18) + 18 from labelElements
		ecGroup.setHeader(header);

		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		ecGroup.setFooter(footer);
		
		return ecGroup;
	}


	/**
	 * Create a Group for Contact column.  
	 * @return
	 */
	private Group createContactGroup()
	{
		final Group contGroup = new Group();
		contGroup.setName(ActivityModel.CONTACT_STRING + ReportFactory.NAME_GROUP);
		contGroup.addField(getModel().getColumnName(ActivityModel.ENERGY_COMPANY_COLUMN));
		contGroup.addField(getModel().getColumnName(ActivityModel.CONTACT_COLUMN));

		GroupHeader header = ReportFactory.createGroupHeaderDefault();	
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 5));	//override the size
		contGroup.setHeader(header);
		
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 5));	//override the size
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
		ItemBand items = ReportFactory.createItemBandDefault();
	
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
			TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			if( i == ActivityModel.ACTION_COUNT_COLUMN )
				factory.setHorizontalAlignment(ElementAlignment.RIGHT);
			else 
				factory.setHorizontalAlignment(ElementAlignment.LEFT);
			items.addElement(factory.createElement());
		}

		return items;
	}

	/**
	 * Creates the report footer.
	 * @return the report footer.
	 */
	protected ReportFooter createReportFooter()
	{
		ReportFooter footer = ReportFactory.createReportFooterDefault();
		
		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(ActivityModel.TOTALS_HEADER_STRING, 0, 1, 100);
		footer.addElement(factory.createElement());
		
		factory = ReportFactory.createGroupLabelElementDefault( getModel(), ActivityModel.ACTION_COLUMN);
		footer.addElement(factory.createElement());

		factory = ReportFactory.createGroupLabelElementDefault(getModel(), ActivityModel.ACTION_COUNT_COLUMN);
		footer.addElement(factory.createElement());

		Iterator iter = ((ActivityModel)getModel()).getTotals().entrySet().iterator();
		int offset = 8;
		while( iter.hasNext())
		{
			offset += 10;
			Map.Entry entry = ((Map.Entry)iter.next());

			factory = ReportFactory.createGroupLabelElementDefault(entry.getKey().toString(), 
				getModel().getColumnProperties(ActivityModel.ACTION_COLUMN).getPositionX(), 
				offset, getModel().getColumnProperties(ActivityModel.ACTION_COLUMN).getWidth());
			footer.addElement(factory.createElement());
			
			factory = ReportFactory.createGroupLabelElementDefault(entry.getValue().toString(), 
				getModel().getColumnProperties(ActivityModel.ACTION_COUNT_COLUMN).getPositionX(), 
				offset, getModel().getColumnProperties(ActivityModel.ACTION_COUNT_COLUMN).getWidth());
			factory.setHorizontalAlignment(ElementAlignment.RIGHT);
			footer.addElement(factory.createElement());
		}
		offset += 20;

		footer.addElement(ReportFactory.createBasicLine("rfLine",0.5f, 20));
		
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