
package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.jfree.report.ElementAlignment;
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
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.function.ItemHideFunction;
import org.jfree.report.function.TextFormatExpression;
import org.jfree.report.layout.DefaultSizeCalculator;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.ActivityDetailModel;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.util.ServletUtil;

/**
 * Created on Dec 15, 2003
 * Creates a detail report of the activityLog using the com.cannontech.analysis.tablemodel.ActivityDetailModel tableModel
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
	 * Runs this report and shows a preview dialog.
 	 * @param args the arguments (ignored).
	 * @throws Exception if an error occurs (default: print a stack trace)
	*/
	public static void main(final String[] args) throws Exception
	{
		// initialize JFreeReport
		JFreeReportBoot.getInstance().start();
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

		//Define default start and stop parameters for a default year to date report.
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		cal.add(java.util.Calendar.DATE, 1);	//default stop date is tomorrow
		Date stop = cal.getTime();

		cal.set(java.util.Calendar.DATE, 1);
		cal.set(java.util.Calendar.MONTH, Calendar.MAY);
		Date start = cal.getTime();	//default start date is begining of year

		ActivityDetailModel model = new ActivityDetailModel(start, stop);

		model.setActionGroupTypes(ActivityLogActions.LOGIN_ACTIONS);
		boolean detail = true; 
		for (int i = 0; i < args.length; i++)
		{
			String arg = (String)args[i].toLowerCase();
				
			int startIndex = arg.indexOf('=');
			startIndex += 1;
			String subString = arg.substring(startIndex);				
			
			if( arg.startsWith("ec"))
				model.setEnergyCompanyID(Integer.valueOf(subString));
//			else if( arg.startsWith("program"))
//				model.setProgramInfoOnly(Boolean.valueOf(subString).booleanValue());
			else if( arg.startsWith("start"))
			{
				Date startDate = ServletUtil.parseDateStringLiberally(subString);
				model.setStartDate(startDate);
			}
			else if( arg.startsWith("stop"))
			{
				Date stopDate = ServletUtil.parseDateStringLiberally(subString);
				model.setStartDate(stopDate);
			}			
			else if( arg.startsWith("detail"))
			{
				detail = Boolean.valueOf(subString).booleanValue();
			}
		}

		YukonReportBase report = new ECActivityDetailReport(model);
		((ActivityDetailModel)model).setShowDetail(detail);
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
		if(expressions == null)
		{
			super.getExpressions();

			ItemHideFunction hideItem = new ItemHideFunction();
			hideItem.setName(ActivityDetailModel.DATE_TIME_STRING + ReportFactory.NAME_HIDDEN);
			hideItem.setField(ActivityDetailModel.DATE_TIME_STRING);
			hideItem.setElement(ActivityDetailModel.DATE_TIME_STRING + ReportFactory.NAME_ELEMENT);
			expressions.add(hideItem);
	
			hideItem = new ItemHideFunction();
			hideItem.setName(ActivityDetailModel.CONTACT_STRING+ ReportFactory.NAME_HIDDEN);
			hideItem.setField(ActivityDetailModel.CONTACT_STRING);
			hideItem.setElement(ActivityDetailModel.CONTACT_STRING + ReportFactory.NAME_ELEMENT);
			expressions.add(hideItem);
	
			hideItem = new ItemHideFunction();
			hideItem.setName(ActivityDetailModel.USERNAME_STRING + ReportFactory.NAME_HIDDEN);
			hideItem.setField(ActivityDetailModel.USERNAME_STRING);
			hideItem.setElement(ActivityDetailModel.USERNAME_STRING + ReportFactory.NAME_ELEMENT);
			expressions.add(hideItem);
			
			hideItem = new ItemHideFunction();
			hideItem.setName(ActivityDetailModel.ACCOUNT_NUMBER_STRING + ReportFactory.NAME_HIDDEN);
			hideItem.setField(ActivityDetailModel.ACCOUNT_NUMBER_STRING);
			hideItem.setElement(ActivityDetailModel.ACCOUNT_NUMBER_STRING + ReportFactory.NAME_ELEMENT);
			expressions.add(hideItem);

			expressions.add(getActionString());
		}
		return expressions;
	}
	
	protected TextFormatExpression getActionString()
	{
		final class TextFormatSummaryCount extends TextFormatExpression
		{
			/* (non-Javadoc)
			 * @see org.jfree.report.function.TextFormatExpression#getValue()
			 */
			public Object getValue()
			{
				// dataRow is predefined and allows access to the 
				// current row of the tablemodel ... 
				if (getDataRow() == null || getDataRow().get(ActivityDetailModel.DATE_TIME_STRING) == null) return null;
	
				//Hard code the pattern to be the actual text we want!
				SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
				String pattern = format.format((Date)getDataRow().get(ActivityDetailModel.DATE_TIME_STRING));
				float stringWidth = DefaultSizeCalculator.getDefaultSizeCalculator(ReportFactory.GROUP_HEADER_BAND_FONT).getStringWidth(pattern, 0, pattern.length());
				do{
				    pattern += " ";
				    stringWidth = DefaultSizeCalculator.getDefaultSizeCalculator(ReportFactory.GROUP_HEADER_BAND_FONT).getStringWidth(pattern, 0, pattern.length());
				}while(stringWidth < 200);	//attempt to center over the count (which is 220 pixels big)
				pattern += "TOTALS";

				
				format = new SimpleDateFormat("yyyyMMdd");
				String key = format.format((Date)getDataRow().get(ActivityDetailModel.DATE_TIME_STRING));			

				Iterator iter = ((ActivityDetailModel)getModel()).getTotals().entrySet().iterator();
				while( iter.hasNext())
				{
					Map.Entry entry = ((Map.Entry)iter.next());
					String keyEntry = entry.getKey().toString();
	
					if(keyEntry.startsWith( key ) )
					{
					    //take a substring of it since the key actually as the 8 char date (yyyyMMdd) in front of it.
						String action = entry.getKey().toString().substring(8);
						pattern += "\r\n";
						stringWidth = DefaultSizeCalculator.getDefaultSizeCalculator(ReportFactory.GROUP_HEADER_BAND_FONT).getStringWidth(action, 0, action.length());
						do{
						    action += " ";
						    stringWidth = DefaultSizeCalculator.getDefaultSizeCalculator(ReportFactory.GROUP_HEADER_BAND_FONT).getStringWidth(action, 0, action.length());
						}while(stringWidth < 220);
						
						pattern += action + entry.getValue().toString();
					}
				}
				setPattern(pattern);				   
				return super.getValue();
			}
		};
		TextFormatSummaryCount action = new TextFormatSummaryCount();
		action.setName(ActivityDetailModel.ACTION_STRING + " String");
		return action;
	}

	/**
	 * Create a Group for EnergyCompany column.  
	 * @return
	 */
	private Group createECGroup()
	{
		final Group ecGroup = new Group();
		ecGroup.setName(ActivityDetailModel.ENERGY_COMPANY_STRING + ReportFactory.NAME_GROUP);
		ecGroup.addField(getModel().getColumnName(ActivityDetailModel.ENERGY_COMPANY_COLUMN));

		GroupHeader header = ReportFactory.createGroupHeaderDefault();
		
		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), ActivityDetailModel.ENERGY_COMPANY_COLUMN);
		header.addElement(tfactory.createElement());
		header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", null, new BasicStroke(0.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 20f), 20));
		ecGroup.setHeader(header);
		
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
//		ecGroup.setFooter(footer);
		
		return ecGroup;
	}

	/**
	 * Create a Group for ActivityLog.Date column.  
	 * @return
	 */
	private Group createTimestampGroup()
	{
		int colIndex = ActivityDetailModel.DATE_COLUMN;
		
		final Group tsGroup = new Group();
		tsGroup.setName(ActivityDetailModel.DATE_TIME_STRING +" Group");
		tsGroup.addField(getModel().getColumnName(ActivityDetailModel.ENERGY_COMPANY_COLUMN));
		tsGroup.addField(getModel().getColumnName(colIndex));

		if( ((ActivityDetailModel)getModel()).isShowDetail())
		{
			GroupHeader header = ReportFactory.createGroupHeaderDefault();
			header.addElement(StaticShapeElementFactory.createShapeElement("line1", null, new BasicStroke(0.5f), new java.awt.geom.Line2D.Float(0, 28, 60, 28), true, false));
			header.addElement(StaticShapeElementFactory.createShapeElement("line2", null, new BasicStroke(0.5f), new java.awt.geom.Line2D.Float(75, 28, 800, 28), true, false));
			
			TextFieldElementFactory tFactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), ActivityDetailModel.DATE_COLUMN);
			header.addElement(tFactory.createElement());

			LabelElementFactory lFactory = null;
			//Add all columns (excluding Date) to the table model.
			for (int i = ActivityDetailModel.CONTACT_COLUMN; i < ActivityDetailModel.DESCRIPTION_COLUMN; i++)
			{
				lFactory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
//TODO??		lFactory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 10));
				header.addElement(lFactory.createElement());
			}
			
			tsGroup.setHeader(header);
		}	//end is showDetail
				
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		footer.addElement(StaticShapeElementFactory.createShapeElement("line3", null, new BasicStroke(0.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] { 12, 12 }, 0) , new Line2D.Float(0, 0, 800, 0), true, false));
		
		//Can't use ReportFactory as that is only good when using a tablemodel 
		TextFieldElementFactory tfactory = new TextFieldElementFactory();
		tfactory.setName(ActivityDetailModel.ACTION_STRING + " Group Element");
		tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(ActivityDetailModel.DATE_COLUMN).getPositionX(), 10));
		tfactory.setMinimumSize(new FloatDimension(500, 10));
		tfactory.setDynamicHeight(Boolean.TRUE);
		tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
		tfactory.setVerticalAlignment(ElementAlignment.TOP);
		tfactory.setNullString("<null>");
		tfactory.setFieldname(ActivityDetailModel.ACTION_STRING + " String");
		footer.addElement(tfactory.createElement());
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
		ItemBand items = ReportFactory.createItemBandDefault();
		if( ((ActivityDetailModel)getModel()).isShowDetail())
		{		
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
			TextFieldElementFactory factory = null;
			//Start at 1, we don't want to include the Date column, Date is our group by column.
			for (int i = ActivityDetailModel.TIME_COLUMN; i < getModel().getColumnNames().length; i++)
			{
				factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
				items.addElement(factory.createElement());
			}
		}//end if showDetail
		else
		{
			items.setVisible(false);
		}
		return items;
	}
}
