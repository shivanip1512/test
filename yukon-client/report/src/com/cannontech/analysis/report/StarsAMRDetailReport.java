package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jfree.report.JFreeReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.GroupList;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.tablemodel.StarsAMRDetailModel;

/**
 * Created on May 22, 2005
 * Creates a STARS LM Summary Report using com.cannontech.analysis.data.StarsLMSummaryModel tableModel
 * @author snebben
 */
public class StarsAMRDetailReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf StarsAMRDetailModel.
	 */
	public StarsAMRDetailReport()
	{
		this(new StarsAMRDetailModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf StarsAMRDetailModel.
	 * @param data_ - StarsAMRDetailModel TableModel data
	 */
	public StarsAMRDetailReport(StarsAMRDetailModel model_)
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
		ClassicEngineBoot.getInstance().start();
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

		StarsAMRDetailModel model = new StarsAMRDetailModel();
		
		//start and stop time are only valid when model.showHist is false
		GregorianCalendar cal = new GregorianCalendar();
		model.setStopDate(cal.getTime());

		cal.set(Calendar.MONTH,0);
		cal.set(Calendar.DAY_OF_MONTH,1);
		model.setStartDate(cal.getTime());
		model.setOrderBy(StarsAMRDetailModel.ORDER_BY_LAST_NAME);
		YukonReportBase yukonReport = new StarsAMRDetailReport(model);
		yukonReport.getModel().collectData();		
		//Create the report
		JFreeReport report = yukonReport.createReport();
		report.setData(yukonReport.getModel());
				
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
	private RelationalGroup createSortByGroup()
	{
	    final RelationalGroup sortByGroup = new RelationalGroup();
	    if ( getModel().getFilterModelType().equals(ReportFilter.ROUTE))
	    {
		    sortByGroup.setName(StarsAMRDetailModel.ROUTE_NAME_STRING + ReportFactory.NAME_GROUP);
		    sortByGroup.addField(StarsAMRDetailModel.ROUTE_NAME_STRING);
	    }
        
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
		
		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), StarsAMRDetailModel.ROUTE_NAME_COLUMN);
		factory.setText(factory.getText() + ":");
		header.addElement(factory.createElement());
		
		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), StarsAMRDetailModel.ROUTE_NAME_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(80, 1));	//override posX		
		header.addElement(tfactory.createElement());
		
		header.addElement(ReportFactory.createBasicLine(0.5f, 20));
		
		for (int i = StarsAMRDetailModel.ACCOUNT_NUMBER_COLUMN; i < getModel().getColumnNames().length; i++)
		{
		    factory = ReportFactory.createGroupLabelElementDefault(model, i);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 22) );	//override location, need to be lower than macroroute text
			header.addElement(factory.createElement());
		}
		sortByGroup.setHeader(header);
		
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		sortByGroup.setFooter(footer);
		
		return sortByGroup;
	}

	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createSortByGroup());
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
		    items.addElement(RectangleElementFactory.createFilledRectangle
		                (0, 0, -100, -100, java.awt.Color.decode("#DFDFDF"))); 
		            items.addElement(HorizontalLineElementFactory.createHorizontalLine
		                (0, java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f)));
		            items.addElement(HorizontalLineElementFactory.createHorizontalLine
		                (10, java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f)));
		}
		
		for (int i = StarsAMRDetailModel.ACCOUNT_NUMBER_COLUMN; i < getModel().getColumnNames().length; i++)
		{
		    TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			items.addElement(factory.createElement());
		}
		return items;
	}
}
