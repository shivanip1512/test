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
import org.jfree.report.elementfactory.NumberFieldElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.tablemodel.DatabaseModel;

/**
 * Created on Dec 15, 2003
 * Creates a DatabaseReport using the com.cannontech.analysis.data.DatabaseModel tableModel
 * No Group by used, only by column headings.
 *  
 * @author snebben
 */
public class DatabaseReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 */
	public DatabaseReport()
	{
		super();
	}
	
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 * @param String paoClass_ (YukonPaoclass.paoclass)
	 */
	public DatabaseReport(String paoClass_)
	{
		super();
		data = new DatabaseModel(paoClass_);
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 * @param data_ - DatabaseModel TableModel data
	 */
	public DatabaseReport(DatabaseModel data_)
	{
		super();
		data = data_;
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
		
		DatabaseReport dbReport = new DatabaseReport("CARRIER");
		dbReport.data.collectData();

		//Define the report Paper properties and format.
		java.awt.print.Paper reportPaper = new java.awt.print.Paper();
		reportPaper.setImageableArea(30, 40, 552, 712);	//8.5 x 11 -> 612w 792h
		java.awt.print.PageFormat pageFormat = new java.awt.print.PageFormat();
		pageFormat.setPaper(reportPaper);
	
		//Create the report
		JFreeReport report = dbReport.createReport();
		report.setDefaultPageFormat(pageFormat);
		report.setData(dbReport.data);
	
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
	 * Create a Group for Column Headings only.  
	 * @return Group
	 */
	private Group createColumnHeadingGroup()
	{
		final Group collHdgGroup = new Group();
		collHdgGroup.setName("Column Heading");
	
		final GroupHeader header = new GroupHeader();
	
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
	
		LabelElementFactory factory;
		for (int i = 0; i < data.getColumnNames().length; i++)
		{
			factory = new LabelElementFactory();
			factory.setAbsolutePosition(new Point2D.Float(data.getColumnProperties(i).getPositionX(), data.getColumnProperties(i).getPositionY()));
			factory.setText(data.getColumnNames()[i]);
			factory.setMinimumSize(new FloatDimension(data.getColumnProperties(i).getWidth(), data.getColumnProperties(i).getHeight() ));
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
			
		TextFieldElementFactory factory = new TextFieldElementFactory();
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(data.getColumnProperties(0).getPositionX(),data.getColumnProperties(0).getPositionY()));
		factory.setMinimumSize(new FloatDimension(data.getColumnProperties(0).getWidth(), 10));
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setNullString("<null>");
		factory.setFieldname(data.getColumnNames()[0]);
		items.addElement(factory.createElement());
	
		for (int i = 0; i < data.getColumnNames().length; i++)
		{
			NumberFieldElementFactory nfactory;			
			if( data.getColumnClass(i).equals(String.class))
			{
				factory = new TextFieldElementFactory();
			}
			else if( data.getColumnClass(i).equals(Integer.class) ||
					data.getColumnClass(i).equals(Double.class))
			{
				factory = new NumberFieldElementFactory();
				((NumberFieldElementFactory)factory).setFormatString(data.getColumnProperties(i).getValueFormat());
			}
			
			if( factory != null)
			{
				factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(data.getColumnProperties(i).getPositionX(),data.getColumnProperties(i).getPositionY()));
				factory.setMinimumSize(new FloatDimension(data.getColumnProperties(i).getWidth(), 10));
				factory.setHorizontalAlignment(ElementAlignment.LEFT);
				factory.setVerticalAlignment(ElementAlignment.MIDDLE);
				factory.setNullString("<null>");
				factory.setFieldname(data.getColumnNames()[i]);
				items.addElement(factory.createElement());
			}
		}

		return items;
	}
}
