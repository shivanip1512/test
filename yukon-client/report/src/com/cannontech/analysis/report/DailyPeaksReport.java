package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
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
import org.jfree.report.modules.gui.base.PreviewDialog;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.DailyPeaksModel;

/**
 * Created on Dec 15, 2003
 * Creates a DatabaseReport using the com.cannontech.analysis.data.DatabaseModel tableModel
 * No Group by used, only by column headings.
 *  
 * @author snebben
 */
public class DailyPeaksReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 */
	public DailyPeaksReport()
	{
		this(new DailyPeaksModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 * @param data_ - DatabaseModel TableModel data
	 */
	public DailyPeaksReport(DailyPeaksModel model_)
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
	
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		Date start = cal.getTime();
		
		cal.add(java.util.Calendar.DATE, 1);
		Date stop = cal.getTime();
		
		DailyPeaksModel reportModel = new DailyPeaksModel(start, stop);
		YukonReportBase dbReport = new DailyPeaksReport(reportModel);
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

	/**
	 * Create a Group for Column Headings only.  
	 * @return Group
	 */
	private Group createControlAreaGroup()
	{
		final Group collHdgGroup = new Group();
		collHdgGroup.setName(DailyPeaksModel.CONTROL_AREA_STRING + ReportFactory.NAME_GROUP);
	
		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), DailyPeaksModel.CONTROL_ARAEA_COLUMN);
		factory.setText(factory.getText() + ":");
		header.addElement(factory.createElement());

		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DailyPeaksModel.CONTROL_ARAEA_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(80, 1));	//override the default posX
		header.addElement(tfactory.createElement());

		tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DailyPeaksModel.PEAK_TITLE_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float( getModel().getColumnProperties(DailyPeaksModel.PEAK_TITLE_COLUMN).getPositionX() , 18));	//override the default posX
		tfactory.setHorizontalAlignment(ElementAlignment.CENTER);
		header.addElement(tfactory.createElement());

		tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DailyPeaksModel.OFF_PEAK_TITLE_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float( getModel().getColumnProperties(DailyPeaksModel.OFF_PEAK_TITLE_COLUMN).getPositionX() , 18));	//override the default posX
		tfactory.setHorizontalAlignment(ElementAlignment.CENTER);
		header.addElement(tfactory.createElement());
		

		for (int i = DailyPeaksModel.RANK_COLUMN; i <= DailyPeaksModel.OFF_PEAK_TIME_COLUMN; i++)
		{
			factory = ReportFactory.createGroupLabelElementDefault(model, i);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 36));	//lower this row of "headings"
			header.addElement(factory.createElement());
		}
	
		header.addElement(ReportFactory.createBasicLine("caGroupLine", 0.5f, 36));
		collHdgGroup.setHeader(header);
	
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		
		tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DailyPeaksModel.THRESHOLD_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(0, 1));	//overrider the default posX
		footer.addElement(tfactory.createElement());

		tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DailyPeaksModel.CURRENT_PEAK_VALUE_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(0, 24));	//overrider the default posX
		footer.addElement(tfactory.createElement());

		tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DailyPeaksModel.CURRENT_PEAK_TIMESTAMP_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(125, 24));	//overrider the default posX
		footer.addElement(tfactory.createElement());


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
	  list.add(createControlAreaGroup());
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
				("background", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0),
					new java.awt.geom.Rectangle2D.Float(0, 0, -100, -100), false, true));
			items.addElement(StaticShapeElementFactory.createLineShapeElement
				("top", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f),
					new java.awt.geom.Line2D.Float(0, 0, 0, 0)));
			items.addElement(StaticShapeElementFactory.createLineShapeElement
				("bottom", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f),
					new java.awt.geom.Line2D.Float(0, 10, 0, 10)));
		}
			
		TextFieldElementFactory factory;
		for (int i = DailyPeaksModel.RANK_COLUMN; i <= DailyPeaksModel.OFF_PEAK_TIME_COLUMN; i++)
		{
			factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			items.addElement(factory.createElement());
		}

		return items;
	}
	
	/**
	 * @return
	 */
	public PageFormat getPageFormat()
	{
		super.getPageFormat();
		super.pageFormat.setOrientation(PageFormat.LANDSCAPE);
		return pageFormat;
	}


}
