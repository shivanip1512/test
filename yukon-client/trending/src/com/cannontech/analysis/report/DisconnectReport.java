package com.cannontech.analysis.report;

import java.awt.BasicStroke;
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
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.elementfactory.DateFieldElementFactory;
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.tablemodel.DisconnectModel;
//import com.cannontech.analysis.data.device.Disconnect;

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
	 * Runs this report and shows a preview dialog.
	 * @param args the arguments (ignored).
	 * @throws Exception if an error occurs (default: print a stack trace)
	*/
	public static void main(final String[] args) throws Exception
	{
		// initialize JFreeReport
		Boot.start();
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

		DisconnectReport DisconnectReport = new DisconnectReport();
		
		DisconnectReport.setModel( new DisconnectModel("History"));
		GregorianCalendar cal = new GregorianCalendar();
		DisconnectReport.getModel().setStopTime(cal.getTime().getTime());
		cal.set(Calendar.MONTH,0);
		cal.set(Calendar.DAY_OF_MONTH,1);
	
		DisconnectReport.getModel().setStartTime(cal.getTime().getTime());
		DisconnectReport.getModel().setCollectionGroups(new String[] {"Cycle 1"});
		
		DisconnectReport.getModel().collectData();
		
		
		//Define the report Paper properties and format.
		java.awt.print.Paper reportPaper = new java.awt.print.Paper();
		reportPaper.setImageableArea(30, 40, 552, 712);	//8.5 x 11 -> 612w 792h
		java.awt.print.PageFormat pageFormat = new java.awt.print.PageFormat();
		pageFormat.setPaper(reportPaper);
		
		//Create the report
		JFreeReport report = DisconnectReport.createReport();
		report.setDefaultPageFormat(pageFormat);
		report.setData(DisconnectReport.getModel());
		
				
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

	  final GroupHeader header = new GroupHeader();

	  header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
	  header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));

	  LabelElementFactory factory = new LabelElementFactory();
	  factory.setName("Label 5");
	  factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 1));
	  factory.setMinimumSize(new FloatDimension(100, 20));
	  factory.setHorizontalAlignment(ElementAlignment.LEFT);
	  factory.setVerticalAlignment(ElementAlignment.BOTTOM);
	  factory.setText("COLLECTION GROUP:");
	  header.addElement(factory.createElement());

	  final TextFieldElementFactory tfactory = new TextFieldElementFactory();
	  tfactory.setName("Collection Group Element");
	  tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(110, 1));
	  tfactory.setMinimumSize(new FloatDimension(200, 20));
	  tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
	  tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
	  tfactory.setNullString("<null>");
	  tfactory.setFieldname("Collection Group");
	  header.addElement(tfactory.createElement());

	  header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(1.5f), new java.awt.geom.Line2D.Float(0, 22, 0, 22)));
	  collGrpGroup.setHeader(header);

	  final GroupFooter footer = new GroupFooter();
	  footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
	  footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));

	  return collGrpGroup;
	}
	/**
	 * Create a Group for Device, (by collectionGroup).  
	 * @return Group
	 */
	 
	private Group createDeviceGroup()
	{
		final Group deviceGroup = new Group();
		deviceGroup.setName("Device Name");
		deviceGroup.addField("Collection Group");
		deviceGroup.addField("Device Name");
		
		  
		final GroupHeader header = new GroupHeader();

		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 22));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));

		LabelElementFactory factory = new LabelElementFactory();
		factory.setName("Label Device");
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(10, 1));
		factory.setMinimumSize(new FloatDimension(40, 20));
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		factory.setText("DEVICE:");
		
		header.addElement(factory.createElement());

		final TextFieldElementFactory tfactory = new TextFieldElementFactory();
		tfactory.setName("Device Name Group Element");
		tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(60, 1));
		tfactory.setMinimumSize(new FloatDimension(400, 20));
		tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
		tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
		tfactory.setNullString("<null>");
		tfactory.setFieldname("Device Name");
		header.addElement(tfactory.createElement());

		header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(1.0f), new java.awt.geom.Line2D.Float(0, 20, 0, 20)));
		deviceGroup.setHeader(header);
		
		final GroupFooter footer = new GroupFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 12));
		footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
		footer.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(1.0f), new java.awt.geom.Line2D.Float(0, 4, 0, 4)));
	
		deviceGroup.setFooter(footer);
		return deviceGroup;
		
	}

	


	/**
	 * Creates the function collection. The xml definition for this construct:
	 * @return the functions.
	 * @throws FunctionInitializeException if there is a problem initialising the functions.
	 */
	protected ExpressionCollection getFunctions() throws FunctionInitializeException
	{
		super.getFunctions();
		
		/*org.jfree.report.function.ItemHideFunction hideItem = new org.jfree.report.function.ItemHideFunction();
		hideItem.setName("hideItem");
		hideItem.setProperty("field", Disconnect.DEVICE_NAME_STRING);
		hideItem.setProperty("element", "Device Element");
		functions.add(hideItem);
*/
		return functions;
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
		final ItemBand items = new ItemBand();

		items.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 10));
		items.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 10));

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
			TextFieldElementFactory factory = new TextFieldElementFactory();
			if( getModel().getColumnClass(i).equals(String.class))
				factory = new TextFieldElementFactory();
			else if( getModel().getColumnClass(i).equals(java.util.Date.class))
			{
				factory = new DateFieldElementFactory();
				((DateFieldElementFactory)factory).setFormatString(getModel().getColumnProperties(i).getValueFormat());
			}
			
			if( factory != null)
			{
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
}
