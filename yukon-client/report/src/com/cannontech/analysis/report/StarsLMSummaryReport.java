package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
import com.cannontech.analysis.tablemodel.StarsLMSummaryModel;

/**
 * Created on May 22, 2005
 * Creates a STARS LM Summary Report using com.cannontech.analysis.data.StarsLMSummaryModel tableModel
 * @author snebben
 */
public class StarsLMSummaryReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf StarsLMSummaryModel.
	 */
	public StarsLMSummaryReport()
	{
		this(new StarsLMSummaryModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf StarsLMSummaryModel.
	 * @param data_ - StarsLMSummaryModel TableModel data
	 */
	public StarsLMSummaryReport(StarsLMSummaryModel model_)
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
		JFreeReportBoot.getInstance().start();
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

		StarsLMSummaryModel model = new StarsLMSummaryModel();
		
		//start and stop time are only valid when model.showHist is false
		GregorianCalendar cal = new GregorianCalendar();
		model.setStopDate(cal.getTime());

		cal.set(Calendar.MONTH,0);
		cal.set(Calendar.DAY_OF_MONTH,1);
		model.setStartDate(cal.getTime());

		YukonReportBase disconnectReport = new StarsLMSummaryReport(model);
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
	 * Create a Group for Column Headings only.  
	 * @return Group
	 */
	private Group createColumnHeadingGroup()
	{
		final Group collHdgGroup = new Group();
		collHdgGroup.setName("Column Heading");
	
		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		LabelElementFactory factory;
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
		    boolean show = true;
		    if( !((StarsLMSummaryModel)getModel()).isShowCapacity() &&
		        ( StarsLMSummaryModel.GROUP_CAPACITY_COLUMN == i ||
		          StarsLMSummaryModel.RECEIVERS_TOTAL_CAPACITY_COLUMN == i ||
		          StarsLMSummaryModel.ADJUSTED_TOTAL_CAPACITY_COLUMN == i) )
		        show = false;

		    if( show)
		    {
				factory = ReportFactory.createGroupLabelElementDefault(model, i);
				header.addElement(factory.createElement());
		    }
		}
	
		header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", null, new BasicStroke(0.5f), 22));
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
				("background", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0),
				new java.awt.geom.Rectangle2D.Float(0, 0, -100, -100), false, true));
			items.addElement(StaticShapeElementFactory.createHorizontalLine
				("top", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f),0));
			items.addElement(StaticShapeElementFactory.createHorizontalLine
				("bottom", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f), 10));
		}
		
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
		    boolean show = true;
		    if( !((StarsLMSummaryModel)getModel()).isShowCapacity() &&
		        ( StarsLMSummaryModel.GROUP_CAPACITY_COLUMN == i ||
		          StarsLMSummaryModel.RECEIVERS_TOTAL_CAPACITY_COLUMN == i ||
		          StarsLMSummaryModel.ADJUSTED_TOTAL_CAPACITY_COLUMN == i) )
		        show = false;

		    if( show)
		    {
			    
				TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
				items.addElement(factory.createElement());
		    }
		}
		return items;
	}
}
