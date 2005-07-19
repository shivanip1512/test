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
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.function.ItemHideFunction;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.MeterReadModel;
import com.cannontech.database.db.device.DeviceMeterGroup;

/**
 * Created on Dec 15, 2003
 * Creates a MissedMeterReport using the com.cannontech.analysis.data.MissedMeterData tableModel
 * Groups data by Collection Group and then by Device.  
 * @author snebben
 */
public class MeterReadReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf MissedMeterModel.
	 * @param data_ - PowerFailModel TableModel data
	 */
	public MeterReadReport(MeterReadModel model_)
	{
		super();
		setModel(model_);
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf MissedMeterModel.
	 * @param data_ - MissedMeterModel TableModel data
	 */
	public MeterReadReport()
	{
		this(new MeterReadModel());
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
		cal.add(java.util.Calendar.DATE, -90);
		Date start = cal.getTime();
		
		MeterReadModel model = new MeterReadModel(start);
		model.setFilterModelType(DeviceMeterGroup.TEST_COLLECTION_GROUP);
		YukonReportBase meterReadReport =new MeterReadReport(model);
		meterReadReport.getModel().collectData();
		
		//Create the report
		JFreeReport report = meterReadReport.createReport();
		report.setData(meterReadReport.getModel());
				
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
	    collGrpGroup.setName( ((MeterReadModel)getModel()).getColumnName(MeterReadModel.SORT_BY_GROUP_NAME_COLUMN) + ReportFactory.NAME_GROUP);
	    collGrpGroup.addField( ((MeterReadModel)getModel()).getColumnName(MeterReadModel.SORT_BY_GROUP_NAME_COLUMN));

	    GroupHeader header = ReportFactory.createGroupHeaderDefault();

	    LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), MeterReadModel.SORT_BY_GROUP_NAME_COLUMN);
	    factory.setText(factory.getText() + ":");
	    header.addElement(factory.createElement());

	    TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), MeterReadModel.SORT_BY_GROUP_NAME_COLUMN);
	    tfactory.setAbsolutePosition(new Point2D.Float(110, 1));	//override the posX location
	    header.addElement(tfactory.createElement());

	    header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", null, new BasicStroke(0.5f), 20));

	    for (int i = MeterReadModel.DEVICE_NAME_COLUMN; i < getModel().getColumnCount(); i++)
	    {
	        factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY() + 18));
		    header.addElement(factory.createElement());
		}
	    header.addElement(StaticShapeElementFactory.createHorizontalLine("line2", null, new BasicStroke(0.5f), 38));
	    collGrpGroup.setHeader(header);


	  	GroupFooter footer = ReportFactory.createGroupFooterDefault();
	  	collGrpGroup.setFooter(footer);
	  	return collGrpGroup;
	}
	/**
	 * Create a Group for Device, (by collectionGroup).  
	 * @return Group
	 */
	private Group createDeviceGroup()
	{
		final Group devGrpGroup = new Group();
		devGrpGroup.setName(MeterReadModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
		devGrpGroup.addField( ((MeterReadModel)getModel()).getColumnName(MeterReadModel.SORT_BY_GROUP_NAME_COLUMN));
		devGrpGroup.addField(MeterReadModel.DEVICE_NAME_STRING);
		  
		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		LabelElementFactory factory;
		for (int i = MeterReadModel.DEVICE_NAME_COLUMN; i < getModel().getColumnCount(); i++)
		{
		    factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
		    header.addElement(factory.createElement());
		}

		header.addElement(ReportFactory.createBasicLine("line3", 0.5f, 20));
		devGrpGroup.setHeader(header);
	
		final GroupFooter footer = new GroupFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 12));
		footer.getStyle().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
		footer.addElement(ReportFactory.createBasicLine("line4", 0.5f, 4));
//		devGrpGroup.setFooter(footer);
		
		return devGrpGroup;
	}


	/**
	 * Creates the function collection. The xml definition for this construct:
	 * @return the functions.
	 * @throws FunctionInitializeException if there is a problem initialising the functions.
	 */
	protected ExpressionCollection getExpressions() throws FunctionInitializeException
	{
		super.getExpressions();
		
		ItemHideFunction hideItem = new ItemHideFunction();
		hideItem.setName("hideItem");
		hideItem.setField(MeterReadModel.DEVICE_NAME_STRING);
		hideItem.setElement(MeterReadModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
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
//	  list.add(createDeviceGroup());
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

		TextFieldElementFactory factory;
		for (int i = MeterReadModel.DEVICE_NAME_COLUMN; i < getModel().getColumnCount(); i++)
		{
		    factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
		    items.addElement(factory.createElement());
		}
	
		return items;
	}
}
