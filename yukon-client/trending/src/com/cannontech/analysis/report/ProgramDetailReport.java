package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Date;

import org.jfree.report.Boot;
import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.elementfactory.LabelElementFactory;
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
import com.cannontech.analysis.function.ElementVisibilityEvalFunction;
import com.cannontech.analysis.tablemodel.ProgramDetailModel;
import com.cannontech.util.ServletUtil;

/**
 * Created on Dec 15, 2003
 * Creates a LGControlLogReport using the com.cannontech.analysis.data.loadgroup.LMControlLogData tableModel
 * Groups data by Date.  Orders asc/desc based on tableModel definition.  
 * @author snebben
 */
public class ProgramDetailReport extends YukonReportBase
{
	private boolean showNotEnrolled = false;

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 */
	public ProgramDetailReport()
	{
		this(new ProgramDetailModel());
	}
	public ProgramDetailReport(boolean showNotEnrolled_)
	{
		this(new ProgramDetailModel(), showNotEnrolled_);
	}	
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param data_ - SystemLogModel TableModel data
	 */
	public ProgramDetailReport(ProgramDetailModel model_)
	{
		this(model_, false);
	}
	public ProgramDetailReport(ProgramDetailModel model_, boolean showNotEnrolled_)
	{
		super();
		setModel(model_);
		setShowNotEnrolled(showNotEnrolled_);
	}	
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf ProgramDetailModel.
	 * @param stopTime_ - stopTime in millis for data query
	 * 
	 */
	public ProgramDetailReport(long stopTime_)
	{
		this(new ProgramDetailModel(stopTime_ ));
		
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
		YukonReportBase report = ReportFuncs.createYukonReport(ReportTypes.PROGRAM_DETAIL_DATA);
		
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
			else if( arg.startsWith("notEnroll"))	//when true, show those "Not Enrolled"			
			{
				((ProgramDetailReport)report).setShowNotEnrolled(Boolean.valueOf(subString).booleanValue());				
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
		hideItem.setName(ProgramDetailModel.CONTACT_STRING + " Hidden");
		hideItem.setProperty("field", ProgramDetailModel.CONTACT_STRING);
		hideItem.setProperty("element", ProgramDetailModel.CONTACT_STRING+" Element");
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName(ProgramDetailModel.ACCOUNT_NUMBER_STRING + " Hidden");
		hideItem.setProperty("field", ProgramDetailModel.ACCOUNT_NUMBER_STRING);
		hideItem.setProperty("element", ProgramDetailModel.ACCOUNT_NUMBER_STRING+" Element");
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName(ProgramDetailModel.PROGRAM_NAME_STRING + " Hidden");
		hideItem.setProperty("field", ProgramDetailModel.PROGRAM_NAME_STRING);
		hideItem.setProperty("element", ProgramDetailModel.PROGRAM_NAME_STRING+" Element");
		expressions.add(hideItem);

		//We negate the showNotEnrolled flag because if true, then we DO NOT want to hide the item.
		ElementVisibilityEvalFunction action = new ElementVisibilityEvalFunction(ProgramDetailModel.STATUS_COLUMN, "Not Enrolled", !isShowNotEnrolled());
		action.setName("Hide Not Enrolled String");
		action.setProperty("element", ProgramDetailModel.STATUS_STRING +" Element");
		expressions.add(action);

//		expressions.add(getDateExpression(getModel().getColumnProperties(5).getValueFormat(), getModel().getColumnName(5)));
		return expressions;
	}

	/**
	 * Create a Group for LMControlLogData.Date column.  
	 * @return
	 */
	private Group createECGroup()
	{
		int index = ProgramDetailModel.ENERGY_COMPANY_COLUMN;
		final Group ecGroup = new Group();
		ecGroup.setName(ProgramDetailModel.ENERGY_COMPANY_STRING +" Group");
		ecGroup.addField(getModel().getColumnName(index));

		final GroupHeader header = new GroupHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));

		final TextFieldElementFactory tfactory = new TextFieldElementFactory();
		tfactory.setName(ProgramDetailModel.ENERGY_COMPANY_STRING + " Group Element");
		tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(index).getPositionX(), getModel().getColumnProperties(index ).getPositionY()));
		tfactory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(index).getWidth(), getModel().getColumnProperties(index).getHeight()));
		tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
		tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
		tfactory.setNullString("<null>");
		tfactory.setFieldname(getModel().getColumnName(index));
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
	private Group createProgramGroup()
	{
		final Group progGroup = new Group();
		progGroup.setName(ProgramDetailModel.PROGRAM_NAME_STRING +" Group");
		progGroup.addField(getModel().getColumnName(ProgramDetailModel.ENERGY_COMPANY_COLUMN));
		progGroup.addField(getModel().getColumnName(ProgramDetailModel.PROGRAM_NAME_COLUMN ));

		final GroupHeader header = new GroupHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 5));
		header.setDynamicContent(true);
		progGroup.setHeader(header);
		
		final GroupFooter footer = new GroupFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 5));
		footer.setDynamicContent(true);
		progGroup.setFooter(footer);

		return progGroup;
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
		list.add(createProgramGroup());
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
		//Start at 1, we don't want to include the EnergyCompany column, it's our group by column.
		for (int i = 1; i < getModel().getColumnNames().length; i++)
		{
			TextFieldElementFactory factory = new TextFieldElementFactory();
			factory.setName(getModel().getColumnNames()[i]+ " Element");
			factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(i).getPositionX(),getModel().getColumnProperties(i).getPositionY()));
			factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), 10));
			factory.setDynamicHeight(Boolean.TRUE);
			factory.setHorizontalAlignment(ElementAlignment.LEFT);
			factory.setVerticalAlignment(ElementAlignment.MIDDLE);
			factory.setNullString("<null>");
			factory.setFieldname(getModel().getColumnNames()[i]);
			items.addElement(factory.createElement());
		}
		items.setDynamicContent(true);
		return items;
	}
	
	/**
	 * @return
	 */
	public boolean isShowNotEnrolled()
	{
		return showNotEnrolled;
	}

	/**
	 * @param b
	 */
	public void setShowNotEnrolled(boolean b)
	{
		showNotEnrolled = b;
	}

}
