package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
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

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.StatisticHistoryModel;
import com.cannontech.analysis.tablemodel.StatisticHistoryPortModel;
import com.cannontech.analysis.tablemodel.StatisticModel;

/**
 * Created on Dec 15, 2003
 * Creates a StatisticReport using the com.cannontech.analysis.data.statistic.* tableModel(s)
 * Groups data by column headings only.
 * See Constructor for options to create different types of statistic reports.  
 * @author snebben
 */
public class StatisticHistoryReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 */
	public StatisticHistoryReport()
	{
		//We must default the model to something!
		this(new StatisticHistoryModel());
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param data_ - SystemLogModel TableModel data
	 */
	public StatisticHistoryReport(StatisticHistoryModel model_)
	{
		super();
		setPageOrientation(PageFormat.PORTRAIT);
		setModel(model_);
	}

	/**
	 * Runs this report and shows a preview dialog.
 	 *
	 * @param args the arguments (ignored).
	 * @throws Exception if an error occurs (default: print a stack trace)
	*/
	public static void main(final String[] args) throws Exception
	{
		// initialize JFreeReport
		JFreeReportBoot.getInstance().start();
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

		String modelType = "carrier";	//default
		if( args.length > 0)
				modelType = args[0].toLowerCase();	//StatisticData report type
		String statPeriodType = StatisticHistoryModel.DAILY_STAT_PERIOD_TYPE_STRING;
		if( args.length >= 2)	//DynamicPaoStatistics.statisticType
			statPeriodType = args[1];
		
		int statType = -1;
		if( modelType.startsWith("trans"))
			statType = StatisticHistoryModel.STAT_TRANS_COMM_DATA;
		else if( modelType.startsWith("dev"))
			statType = StatisticHistoryModel.STAT_DEVICE_COMM_DATA;
		else if( modelType.startsWith("comm"))
			statType = StatisticHistoryModel.STAT_COMM_CHANNEL_DATA;
		else if( modelType.startsWith("carr"))
			statType = StatisticHistoryModel.STAT_CARRIER_COMM_DATA;

		StatisticHistoryModel model = new StatisticHistoryModel(statPeriodType, statType);
		YukonReportBase statReport = new StatisticHistoryReport(model);
		statReport.getModel().collectData();

		//Create the report
		JFreeReport report = statReport.createReport();
		report.setData(statReport.getModel());
				
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
	 * Create a Group for <nothing> actually.  Only column headings.  
	 * @return
	 */
	private Group createColumnHeadingGroup()
	{
		final Group collHdgGroup = new Group();
		collHdgGroup.setName("Column Heading" + ReportFactory.NAME_GROUP);
	
		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		LabelElementFactory factory;
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
			factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			if (i > 1)	//all but paoName and paoType
				factory.setHorizontalAlignment(ElementAlignment.RIGHT);
			header.addElement(factory.createElement());
		}
	
		header.addElement(ReportFactory.createBasicLine("chGroupLine", 0.5f, 20));
		collHdgGroup.setHeader(header);
	
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
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
		ItemBand items = ReportFactory.createItemBandDefault();
	
		if(showBackgroundColor)
		{
			items.addElement(StaticShapeElementFactory.createRectangleShapeElement
				("background", Color.decode("#DFDFDF"), new BasicStroke(0),
					new Rectangle2D.Float(0, 0, -100, -100), false, true));
			items.addElement(StaticShapeElementFactory.createHorizontalLine
				("top", Color.decode("#DFDFDF"), new BasicStroke(0.1f), 0));
			items.addElement(StaticShapeElementFactory.createHorizontalLine
				("bottom", Color.decode("#DFDFDF"), new BasicStroke(0.1f), 10));
		}
			
		TextFieldElementFactory factory;
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
			factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			if (i > 1)	//all but paoName, paoType
				factory.setHorizontalAlignment(ElementAlignment.RIGHT);
			items.addElement(factory.createElement());
		}

		return items;
	}
}
