package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;

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
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
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
	 * @param data_ - DatabaseModel TableModel data
	 */
	public RouteMacroReport(RouteMacroModel model_)
	{
		super();
		setPageOrientation(PageFormat.PORTRAIT);
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
		
		RouteMacroModel model = new RouteMacroModel();
		YukonReportBase rmReport = new RouteMacroReport(model);
		rmReport.getModel().collectData();

		//Create the report
		JFreeReport report = rmReport.createReport();
		report.setData(rmReport.getModel());
	
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
		routeMacroGroup.setName(RouteMacroModel.MACRO_ROUTE_NAME_STRING + ReportFactory.NAME_GROUP);
		routeMacroGroup.addField(RouteMacroModel.MACRO_ROUTE_NAME_STRING);
		
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
		
		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(),RouteMacroModel.MACRO_ROUTE_NAME_COLUMN);
		header.addElement(factory.createElement());
		
		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), RouteMacroModel.MACRO_ROUTE_NAME_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(130, 1));	//override posX
		factory.setMinimumSize(new FloatDimension( 300, 18));
		header.addElement(tfactory.createElement());
		
		header.addElement(ReportFactory.createBasicLine("rmGroupLine", 0.5f, 20));
		
		for (int i = 1; i < getModel().getColumnNames().length; i++)
		{
			factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18) );	//override location, need to be lower than macroroute text
			header.addElement(factory.createElement());
		}
		routeMacroGroup.setHeader(header);
		
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		routeMacroGroup.setFooter(footer);
		
		return routeMacroGroup;
	}
	
	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createRouteMacroGroup());
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
		
		for (int i = 1; i < getModel().getColumnNames().length; i++)
		{
			TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			if (i == RouteMacroModel.CCU_BUS_NUMBER_COLUMN ||
				i == RouteMacroModel.FIXED_BITS_COLUMN ||
				i == RouteMacroModel.VARIABLE_BITS_COLUMN ||
				i == RouteMacroModel.DEFAULT_ROUTE_COLUMN )
			factory.setHorizontalAlignment(ElementAlignment.CENTER);
			items.addElement(factory.createElement());
		}
			
		return items;
	}
}
