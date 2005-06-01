package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.function.ItemHideFunction;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.CapControlNewActivityModel;

/**
 * Created on May 18, 2005
 * @author snebben
 */
public class CapControlNewActivityReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf CapControlNewActivityModel.
	 */
	public CapControlNewActivityReport()
	{
		this(new CapControlNewActivityModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf CapControlNewActivityModel.
	 * @param data_ - CapControlNewActivityModel TableModel data
	 */
	public CapControlNewActivityReport(CapControlNewActivityModel model_)
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
		cal.add(java.util.Calendar.DATE, -7);
		
		Date start = cal.getTime();	//default start date is begining of year		
		CapControlNewActivityModel model = new CapControlNewActivityModel();
		model.setOrderBy(CapControlNewActivityModel.CAP_BANK_NAME_COLUMN);
		model.setSortOrder(CapControlNewActivityModel.DESCENDING);
		model.setStartDate(start);		
		YukonReportBase ccActivityReport = new CapControlNewActivityReport(model);
		ccActivityReport.getModel().collectData();

		//Create the report
		JFreeReport report = ccActivityReport.createReport();
		report.setData(ccActivityReport.getModel());
	
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

	protected ExpressionCollection getExpressions() throws FunctionInitializeException
	{
		super.getExpressions();
		
		ItemHideFunction hideItem = new ItemHideFunction();
		hideItem.setName("hideItemCapBank");
		hideItem.setField(CapControlNewActivityModel.CAP_BANK_NAME_STRING);
		hideItem.setElement(CapControlNewActivityModel.CAP_BANK_NAME_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName("hideItemCBCName");
		hideItem.setField(CapControlNewActivityModel.CBC_NAME_STRING);
		hideItem.setElement(CapControlNewActivityModel.CBC_NAME_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(hideItem);

		return expressions;
	}	

	private Group createSubFeederGroup()
	{
		final Group subFeederGroup = new Group();
		subFeederGroup.setName(CapControlNewActivityModel.SUB_NAME_STRING + ReportFactory.NAME_GROUP);
		subFeederGroup.addField(CapControlNewActivityModel.SUB_NAME_STRING);
		subFeederGroup.addField(CapControlNewActivityModel.FEEDER_NAME_STRING);
		
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
		
		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), CapControlNewActivityModel.SUB_NAME_COLUMN);
		factory.setText("Substation Name: ");
		header.addElement(factory.createElement());
		
		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), CapControlNewActivityModel.SUB_NAME_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(80, 1));	//override posX
		factory.setMinimumSize(new FloatDimension( 300, 18));
		header.addElement(tfactory.createElement());
		
		factory = ReportFactory.createGroupLabelElementDefault(getModel(), CapControlNewActivityModel.FEEDER_NAME_COLUMN);
		factory.setText("Feeder Name: ");
		factory.setAbsolutePosition(new Point2D.Float(0, 18) );	//override location, need to be lower than macroroute text		
		header.addElement(factory.createElement());
		
		tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), CapControlNewActivityModel.FEEDER_NAME_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(80, 18));	//override posX
		header.addElement(tfactory.createElement());		

		header.addElement(ReportFactory.createBasicLine("rmGroupLine", 0.5f, 38));
		
		for (int i = CapControlNewActivityModel.DATE_TIME_COLUMN; i < getModel().getColumnNames().length; i++)
		{
			factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 36) );	//override location, need to be lower than macroroute text
			header.addElement(factory.createElement());
		}
		subFeederGroup.setHeader(header);
		
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		subFeederGroup.setFooter(footer);
		
		return subFeederGroup;
	}
	
	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createSubFeederGroup());
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
				("background", Color.decode("#DFDFDF"), new BasicStroke(0),
					new Rectangle2D.Float(0, 0, -100, -100), false, true));
			items.addElement(StaticShapeElementFactory.createHorizontalLine
				("top", Color.decode("#DFDFDF"), new BasicStroke(0.1f), 0));
			items.addElement(StaticShapeElementFactory.createHorizontalLine
				("bottom", Color.decode("#DFDFDF"), new BasicStroke(0.1f), 10));
		}
		
		for (int i = CapControlNewActivityModel.DATE_TIME_COLUMN; i < getModel().getColumnNames().length; i++)
		{
			TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			items.addElement(factory.createElement());
		}
			
		return items;
	}
}
