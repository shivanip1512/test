package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.DisconnectModel;

/**
 * Created on Feb 06, 2003
 * Creates a DisconnectReport using the com.cannontech.analysis.data.DisconnectData tableModel
 * Groups data by Collection Group and then by Device.  
 * @author snebben
 * @author bjonasson
 */
public class DisconnectReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 */
	public DisconnectReport()
	{
		this(new DisconnectModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 * @param data_ - DatabaseModel TableModel data
	 */
	public DisconnectReport(DisconnectModel model_)
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

		DisconnectModel model = new DisconnectModel(false);
		
		//start and stop time are only valid when model.showHist is false
		GregorianCalendar cal = new GregorianCalendar();
		model.setStopTime(cal.getTime().getTime());
		cal.set(Calendar.MONTH,0);
		cal.set(Calendar.DAY_OF_MONTH,1);
	
		model.setStartTime(cal.getTime().getTime());
//		model.setCollectionGroups(new String[] {"Cycle 1"});


		model.setShowConnected(true);
		model.setShowDisconnected(true);

		YukonReportBase disconnectReport = new DisconnectReport(model);
		disconnectReport.getModel().collectData();		
		//Create the report
		JFreeReport report = disconnectReport.createReport();
		report.setData(disconnectReport.getModel());
				
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
	  collGrpGroup.setName("Collection Group");
	  collGrpGroup.addField("Collection Group");

	  GroupHeader header = ReportFactory.createGroupHeaderDefault();

	  LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), DisconnectModel.COLL_GROUP_NAME_COLUMN);
	  factory.setText(factory.getText() + ":");
	  header.addElement(factory.createElement());

	  TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DisconnectModel.COLL_GROUP_NAME_COLUMN);
	  tfactory.setAbsolutePosition(new Point2D.Float(110, 1));	//override the default posX
	  header.addElement(tfactory.createElement());

	  header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(1.5f), new java.awt.geom.Line2D.Float(0, 22, 0, 22)));
	  collGrpGroup.setHeader(header);

	  GroupFooter footer = ReportFactory.createGroupFooterDefault();
	  return collGrpGroup;
	}
	/**
	 * Create a Group for Device, (by collectionGroup).  
	 * @return Group
	 */
	 
	private Group createDeviceGroup()
	{
		final Group deviceGroup = new Group();
		deviceGroup.setName(DisconnectModel.DEVICE_NAME_STRING + " Group");
		deviceGroup.addField(DisconnectModel.COLL_GROUP_NAME_STRING);
		deviceGroup.addField(DisconnectModel.DEVICE_NAME_STRING);
		
		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), DisconnectModel.DEVICE_NAME_COLUMN);
		factory.setText(DisconnectModel.DEVICE_NAME_STRING + ":");	//override the default text
		header.addElement(factory.createElement());

		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DisconnectModel.DEVICE_NAME_COLUMN);
		tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(110, 1));
		header.addElement(tfactory.createElement());

		header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(1.0f), new java.awt.geom.Line2D.Float(0, 20, 0, 20)));
		deviceGroup.setHeader(header);
		
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		footer.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(1.0f), new java.awt.geom.Line2D.Float(0, 4, 0, 4)));
	
		deviceGroup.setFooter(footer);
		return deviceGroup;
	}


	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createCollGrpGroup());
	  list.add(createDeviceGroup());
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
			items.addElement(StaticShapeElementFactory.createLineShapeElement
				("top", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f),
				 new java.awt.geom.Line2D.Float(0, 0, 0, 0)));
			items.addElement(StaticShapeElementFactory.createLineShapeElement
				("bottom", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f),
				new java.awt.geom.Line2D.Float(0, 10, 0, 10)));
		}
		
		for (int i = 2; i < getModel().getColumnNames().length; i++)
		{
			TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			items.addElement(factory.createElement());
		}
		return items;
	}
}
