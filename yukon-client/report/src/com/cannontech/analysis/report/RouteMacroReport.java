package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;

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
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.tablemodel.RouteMacroModel;

/**
 * Created on Mar 26, 2004
 * Creates a DatabaseReport using the com.cannontech.analysis.data.DatabaseModel tableModel
 * No Group by used, only by column headings.
 *  
 * @author snebben
 */
public class RouteMacroReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 */
	public RouteMacroReport()
	{
		this(new RouteMacroModel());
	}
	
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 * @param String paoClass_ (YukonPaoclass.paoclass)
	 */
	public RouteMacroReport(String paoClass_)
	{
		this( new RouteMacroModel(paoClass_));
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 * @param data_ - DatabaseModel TableModel data
	 */
	public RouteMacroReport(RouteMacroModel model_)
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
		
		YukonReportBase dbReport = ReportFuncs.createYukonReport(ReportTypes.CARRIER_ROUTE_MACRO_DATA);
		dbReport.getModel().collectData();

		//Create the report
		JFreeReport report = dbReport.createReport();
		report.setData(dbReport.getModel());
	
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
	private Group createRouteMacroGroup()
		{
		  final Group routeMacroGroup = new Group();
		  routeMacroGroup.setName("Route Macro Name");
		  routeMacroGroup.addField("Route Macro Name");

		  final GroupHeader header = new GroupHeader();

		  header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
		  header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));

		  LabelElementFactory factory = new LabelElementFactory();
		  factory.setName("Label 5");
		  factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 1));
		  factory.setMinimumSize(new FloatDimension(100, 20));
		  factory.setHorizontalAlignment(ElementAlignment.LEFT);
		  factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		  factory.setText("Route Macro:");
		  header.addElement(factory.createElement());

		  final TextFieldElementFactory tfactory = new TextFieldElementFactory();
		  tfactory.setName("Route Macro Element");
		  tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(110, 1));
		  tfactory.setMinimumSize(new FloatDimension(200, 20));
		  tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
		  tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
		  tfactory.setNullString("<null>");
		  tfactory.setFieldname("Route Macro Name");
		  header.addElement(tfactory.createElement());

		  header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(1.5f), new java.awt.geom.Line2D.Float(0, 22, 0, 22)));
		  routeMacroGroup.setHeader(header);

		  final GroupFooter footer = new GroupFooter();
		  footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
		  footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));

		  return routeMacroGroup;
		}
	/**
	 * Create a Group for Column Headings only.  
	 * @return Group
	 */
	private Group createColumnHeadingGroup()
	{
		final Group collHdgGroup = new Group();
		collHdgGroup.setName("Column Heading");
		collHdgGroup.addField("Route Macro Name");
	
		final GroupHeader header = new GroupHeader();
	
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
	
		LabelElementFactory factory;
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
			factory = new LabelElementFactory();
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY()));
			factory.setText(getModel().getColumnNames()[i]);
			factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), getModel().getColumnProperties(i).getHeight() ));
			factory.setHorizontalAlignment(ElementAlignment.LEFT);
			factory.setVerticalAlignment(ElementAlignment.BOTTOM);
			header.addElement(factory.createElement());
		}
	
		header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(0.5f), new java.awt.geom.Line2D.Float(0, 22, 0, 22)));
		collHdgGroup.setHeader(header);
	
		final GroupFooter footer = new GroupFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
		footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
		collHdgGroup.setFooter(footer);

		return collHdgGroup;
	}
	
	
	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createRouteMacroGroup());
	  list.add(createColumnHeadingGroup());
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
		
		for (int i = 1; i < getModel().getColumnNames().length; i++)
				{
					TextFieldElementFactory factory = new TextFieldElementFactory();
					if( getModel().getColumnClass(i).equals(String.class))
						factory = new TextFieldElementFactory();
					//else if( getModel().getColumnClass(i).equals(java.util.Date.class))
					//{
					//	factory = new DateFieldElementFactory();
					//	((DateFieldElementFactory)factory).setFormatString(getModel().getColumnProperties(i).getValueFormat());
					//}
			
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
