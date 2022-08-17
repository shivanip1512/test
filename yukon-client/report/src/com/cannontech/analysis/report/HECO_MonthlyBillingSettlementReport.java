package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.print.PageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jfree.report.Element;
import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.JFreeReportBoot;
import org.jfree.report.PageFooter;
import org.jfree.report.SimplePageDefinition;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.event.ReportEvent;
import org.jfree.report.function.ElementVisibilitySwitchFunction;
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.function.FunctionUtilities;
import org.jfree.report.modules.gui.base.PreviewDialog;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.HECO_MonthlyBillingSettlementModel;

/**
 * Created on Feb 06, 2003
 * Creates a DisconnectReport using the com.cannontech.analysis.data.DisconnectData tableModel
 * Groups data by Collection Group and then by Device.  
 * @author snebben
 * @author bjonasson
 */
public class HECO_MonthlyBillingSettlementReport extends YukonReportBase
{
    private class SpecificRowVisibilityFunction extends ElementVisibilitySwitchFunction
    {
        int count = 0;
        public SpecificRowVisibilityFunction() {
            super();
        }
        public void itemsAdvanced(final ReportEvent event) {
            updateVisibleState(event);
        }
        private void updateVisibleState(final ReportEvent event) {
            int x = (count++ % 17);
            boolean show = (x == HECO_MonthlyBillingSettlementModel.CONTROLLED_DEMAND_INCENTIVE_DATA || 
                            x == HECO_MonthlyBillingSettlementModel.EFSL_DISPATCHED_DATA ||
                            x == HECO_MonthlyBillingSettlementModel.TOTAL_CIDLC_CREDITS_DEBITS_DATA );
            
            final Element[] e = FunctionUtilities.findAllElements(event.getReport().getItemBand(), getElement());
            if (e.length > 0)
            {
              for (int i = 0; i < e.length; i++)
              {
                e[i].setVisible(show);
              }
            }
       }
    }
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf LoadControlEventSummaryModel.
	 */
	public HECO_MonthlyBillingSettlementReport()
	{
		this(new HECO_MonthlyBillingSettlementModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf LoadControlEventSummaryModel.
	 * @param data_ - DatabaseModel TableModel data
	 */
	public HECO_MonthlyBillingSettlementReport(HECO_MonthlyBillingSettlementModel model_)
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

		HECO_MonthlyBillingSettlementModel model = new HECO_MonthlyBillingSettlementModel();
		
		//start and stop time are only valid when model.showHist is false
		GregorianCalendar cal = new GregorianCalendar();
		model.setStopDate(cal.getTime());

		cal.set(Calendar.MONTH,Calendar.NOVEMBER);
		cal.set(Calendar.DAY_OF_MONTH,1);
		cal.set(Calendar.HOUR_OF_DAY,1);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		model.setStartDate(cal.getTime());

//		cal.set(Calendar.DAY_OF_MONTH, 15);
//		model.setStopDate(cal.getTime());
		YukonReportBase lcSummaryReport = new HECO_MonthlyBillingSettlementReport(model);
		lcSummaryReport.getModel().collectData();		
		//Create the report
		JFreeReport report = lcSummaryReport.createReport();
		report.setData(lcSummaryReport.getModel());
				
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
			factory = ReportFactory.createGroupLabelElementDefault(model, i);
			factory.setWrapText(Boolean.TRUE);
			header.addElement(factory.createElement());
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
		items.addElement(StaticShapeElementFactory.createHorizontalLine
			("top", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f), 0));
        items.addElement(StaticShapeElementFactory.createHorizontalLine
            ("boldLine", null, new BasicStroke(0.5f), 0));
		
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
			TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			if( i > 0)
				factory.setHorizontalAlignment(ElementAlignment.RIGHT);
			if( i == HECO_MonthlyBillingSettlementModel.CONTROLLED_DEMAND_INCENTIVE_DATA)
				factory.setLineHeight(new Float(30));
			items.addElement(factory.createElement());
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.report.YukonReportBase#getPageDefinition()
	 */
	public SimplePageDefinition getPageDefinition()
	{
		if( pageDefinition == null)
		{
			java.awt.print.Paper reportPaper = new java.awt.print.Paper();
			//Adjust the imagable width in the case of the columns being too large for one page.
            ColumnProperties prop = getModel().getColumnProperties(getModel().getColumnProperties().length -1);
            int totalWidth = (int) (prop.getPositionX() + prop.getWidth());

			int numPagesWide = (totalWidth/732) + 1;
			
			reportPaper.setImageableArea(30, 30, 552, 732);	//8.5 x 11 -> 612w 792h
			PageFormat pageFormat = new java.awt.print.PageFormat();
			pageFormat.setOrientation(getPageOrientation());
			pageFormat.setPaper(reportPaper);
			pageDefinition = new SimplePageDefinition(pageFormat, numPagesWide, 1);
		}
		return pageDefinition;
	}
    
    /* (non-Javadoc)
     * @see com.cannontech.analysis.report.YukonReportBase#getExpressions()
     */
    protected ExpressionCollection getExpressions() throws FunctionInitializeException
    {
        super.getExpressions();
        final SpecificRowVisibilityFunction boldLineFunction = new SpecificRowVisibilityFunction();
        boldLineFunction.setName("boldLineTrigger");
        boldLineFunction.setElement("boldLine");
        expressions.add(boldLineFunction);

        return expressions;
    }
    
    protected PageFooter createPageFooter() {
    	PageFooter pageFooter = super.createPageFooter();
    	pageFooter.addElement(getVersionLabel().createElement());
		return pageFooter;
    }
}