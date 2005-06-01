package com.cannontech.analysis.report;

import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.util.Calendar;
import java.util.Date;

import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.JFreeReportBoot;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.LPDataSummaryModel;

/**
 * Created on Dec 15, 2003
 *
 * TODO - Rework report when subreporting feature is available in jfreereport  
 * @author snebben
 */
public class LPDataSummaryReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf LPDataSummaryModel.
	 */
	public LPDataSummaryReport()
	{
		this(new LPDataSummaryModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf LPDataSummaryModel.
	 * @param data_ - DatabaseModel TableModel data
	 */
	public LPDataSummaryReport(LPDataSummaryModel model_)
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
	
		//Define start and stop parameters for a default 90 day report.
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		Date stop = cal.getTime();
		cal.add(Calendar.DATE, -5);
		Date start = cal.getTime();
		
		LPDataSummaryModel reportModel = new LPDataSummaryModel(start, stop);
		YukonReportBase dbReport = new LPDataSummaryReport(reportModel);
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
	private Group createDeviceGroup()
	{
		final Group devGroup = new Group();
		devGroup.setName(LPDataSummaryModel.PAO_NAME_STRING + ReportFactory.NAME_GROUP);
		devGroup.addField(LPDataSummaryModel.PAO_NAME_STRING);
	
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
		
		for (int i = LPDataSummaryModel.PAO_NAME_COLUMN; i <= LPDataSummaryModel.PAO_TYPE_COLUMN; i++)
		{
			LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			header.addElement(factory.createElement());
			
			TextFieldElementFactory tfactory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			tfactory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 20));	//override the posX location
			header.addElement(tfactory.createElement());
		}
		devGroup.setHeader(header);
	
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		devGroup.setFooter(footer);

		return devGroup;
	}
	/**
	 * Create a Group for Device, (by collectionGroup).  
	 * @return Group
	 */
	private Group createPointGroup()
	{
		final Group devGrpGroup = new Group();
		devGrpGroup.setName(LPDataSummaryModel.POINT_NAME_STRING + ReportFactory.NAME_GROUP);
		devGrpGroup.addField(LPDataSummaryModel.PAO_NAME_STRING);
		devGrpGroup.addField(LPDataSummaryModel.POINT_NAME_STRING);
		  
		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		LabelElementFactory factory;
		TextFieldElementFactory tfactory;

		for (int i = LPDataSummaryModel.POINT_NAME_COLUMN; i <= LPDataSummaryModel.MISSING_DATA_FLAG_COLUMN; i++)
		{
			factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			header.addElement(factory.createElement());
			
			tfactory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			tfactory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 20));	//override the posX location
			header.addElement(tfactory.createElement());
		}
		
		header.addElement(ReportFactory.createBasicLine("pointGrpLine", 0.5f, 20));
		devGrpGroup.setHeader(header);
	
		GroupFooter footer = ReportFactory.createGroupFooterDefault();

		factory = ReportFactory.createGroupLabelElementDefault(getModel(), LPDataSummaryModel.PEAKS_TITLE_COLUMN);
		factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(LPDataSummaryModel.PEAKS_TITLE_COLUMN).getPositionX(), 12));
		footer.addElement(factory.createElement());
		
		factory = ReportFactory.createGroupLabelElementDefault(getModel(), LPDataSummaryModel.LOWS_TITLE_COLUMN);
		factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(LPDataSummaryModel.LOWS_TITLE_COLUMN).getPositionX(), 12));
		footer.addElement(factory.createElement());
		
//		footer.addElement(StaticShapeElementFactory.createLineShapeElement("peakSumLine", null, new BasicStroke(0.3f), new Line2D.Float(50, 30, 225, 30)));
//		footer.addElement(StaticShapeElementFactory.createLineShapeElement("lowSumLine", null, new BasicStroke(0.3f), new Line2D.Float(325, 30, 500, 30)));

		for (int i = LPDataSummaryModel.PEAK_TIMESTAMP_0_COLUMN; i <= LPDataSummaryModel.LOW_VALUE_4_COLUMN; i++)
		{
			tfactory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			tfactory.setBold(Boolean.FALSE);
			footer.addElement(tfactory.createElement());
		}
		
		devGrpGroup.setFooter(footer);
		
		return devGrpGroup;
	}
	
	
	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createDeviceGroup());
	  list.add(createPointGroup());
	  return list;
	}


	/**
	 * Creates the itemBand, the rows of data.
	 * @return the item band.
	 */
	protected ItemBand createItemBand()
	{
		//No item band, only summary data!
		ItemBand items = ReportFactory.createItemBandDefault();
		items.setVisible(false);	
		return items;
	}
}
