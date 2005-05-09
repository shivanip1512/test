package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.util.Date;

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
import org.jfree.report.modules.gui.base.PreviewDialog;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.PowerFailModel;

/**
 * Created on Feb 17, 2004
 * Creates a MissedMeterReport using the com.cannontech.analysis.data.PowerFailData tableModel
 * Groups data by Collection Group and then by Device.  
 * @author bjonasson
 */
public class PowerFailReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf PowerFailModel.
	 * @param data_ - PowerFailModel TableModel data
	 */
	public PowerFailReport(PowerFailModel model_)
	{
		super();
		setPageOrientation(PageFormat.PORTRAIT);
		setModel(model_);
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf PowerFailModel.
	 * @param data_ - PowerFailModel TableModel data
	 */
	public PowerFailReport()
	{
		this(new PowerFailModel());
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
		cal.add(java.util.Calendar.DATE, 1);
		Date stop = cal.getTime();
		cal.add(java.util.Calendar.DATE, -1);
		Date start = cal.getTime();

		PowerFailModel model = new PowerFailModel(start, stop);
		
		YukonReportBase powerFailReport = new PowerFailReport(model);
		powerFailReport.getModel().collectData();
		
		//Create the report
		JFreeReport report = powerFailReport.createReport();
		report.setData(powerFailReport.getModel());
				
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
	 * Create a Group for CollectionGroup  
	 * @return Group
	 */
	private Group createCollGrpGroup()
	{
		final Group collGrpGroup = new Group();
		collGrpGroup.setName(PowerFailModel.COLL_GROUP_NAME_STRING + ReportFactory.NAME_GROUP);
		collGrpGroup.addField(PowerFailModel.COLL_GROUP_NAME_STRING);
		
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
		
		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), PowerFailModel.COLL_GROUP_NAME_COLUMN);
		factory.setText(factory.getText() + ":");
		header.addElement(factory.createElement());
		
		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), PowerFailModel.COLL_GROUP_NAME_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(80, 1));
		header.addElement(tfactory.createElement());
		
		header.addElement(ReportFactory.createBasicLine("cgGroupLine", 0.5f, 20));
		collGrpGroup.setHeader(header);
		
//		GroupFooter footer = ReportFactory.createGroupFooterDefault();
//		collGrpGroup.setFooter(footer);
		
		return collGrpGroup;
	}
	/**
	 * Create a Group for Device, (by collectionGroup).  
	 * @return Group
	 */
	private Group createColHeadingsGroup()
	{
		final Group colHdgGroup = new Group();
		colHdgGroup.setName(PowerFailModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
		colHdgGroup.addField(PowerFailModel.COLL_GROUP_NAME_STRING);
		colHdgGroup.addField(PowerFailModel.DEVICE_NAME_STRING);
	  
		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		LabelElementFactory factory;
		for (int i = PowerFailModel.DEVICE_NAME_COLUMN; i <= PowerFailModel.POWER_FAIL_COUNT_COLUMN; i++)
		{
			factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			if( i == PowerFailModel.POWER_FAIL_COUNT_COLUMN)
				factory.setHorizontalAlignment(ElementAlignment.RIGHT);

			header.addElement(factory.createElement());
		}
		
		colHdgGroup.setHeader(header);

		GroupFooter footer = ReportFactory.createGroupFooterDefault();
//		footer.addElement(ReportFactory.createBasicLine("dGroupFootLine", 0.5f, 4));
		colHdgGroup.setFooter(footer);
		return colHdgGroup;
	}


	/**
	 * Creates the expression collection. The xml definition for this construct:
	 * @return the functions.
	 * @throws FunctionInitializeException if there is a problem initialising the functions.
	 */
	protected ExpressionCollection getExpressions() throws FunctionInitializeException
	{
		super.getExpressions();
		
		ItemHideFunction hideItem = new ItemHideFunction();
		hideItem.setName("hideDevice");
		hideItem.setField(PowerFailModel.DEVICE_NAME_STRING);
		hideItem.setElement(PowerFailModel.DEVICE_NAME_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName("hidePoint");
		hideItem.setField(PowerFailModel.POINT_NAME_STRING);
		hideItem.setElement(PowerFailModel.POINT_NAME_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(hideItem);

		return expressions;
	}


	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
		final GroupList list = new GroupList();
		list.add(createCollGrpGroup());
		list.add(createColHeadingsGroup());
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

		for (int i = PowerFailModel.DEVICE_NAME_COLUMN; i <= PowerFailModel.POWER_FAIL_COUNT_COLUMN; i++)
		{
			TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			if( i == PowerFailModel.POWER_FAIL_COUNT_COLUMN)
				factory.setHorizontalAlignment(ElementAlignment.RIGHT);
			items.addElement(factory.createElement());
		}
	
		return items;
	}		
}
