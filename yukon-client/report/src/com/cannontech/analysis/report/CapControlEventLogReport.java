package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;

import org.jfree.report.JFreeReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.FunctionProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ItemHideFunction;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.GroupList;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.CapControlEventLogModel;
import com.cannontech.util.ServletUtil;

/**
 * Created on Mar 1, 2006
 * Creates a Cap Control Event Log Report
 *  
 * @author snebben
 */
public class CapControlEventLogReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf CapControlEventLogModel.
	 */
	public CapControlEventLogReport()
	{
		this(new CapControlEventLogModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf CapControlEventLogModel.
	 * @param data_ - CapControlStatusModel TableModel data
	 */
	public CapControlEventLogReport(CapControlEventLogModel model_)
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
	
		CapControlEventLogModel reportModel = new CapControlEventLogModel();
		reportModel.setPaoIDs(new int[]{27});
		reportModel.setStartDate(ServletUtil.getYesterday());
		YukonReportBase dbReport = new CapControlEventLogReport(reportModel);
		
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
	 * Create a Group for Substation Bus  
	 * @return Group
	 */
	private RelationalGroup createSubBusGroup()
	{
	    final RelationalGroup subBusGroup = new RelationalGroup();
	    subBusGroup.setName( ((CapControlEventLogModel)getModel()).getColumnName(CapControlEventLogModel.SUB_BUS_NAME_COLUMN) + ReportFactory.NAME_GROUP);
	    subBusGroup.addField( ((CapControlEventLogModel)getModel()).getColumnName(CapControlEventLogModel.SUB_BUS_NAME_COLUMN));

	    GroupHeader header = ReportFactory.createGroupHeaderDefault();

	    LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), CapControlEventLogModel.SUB_BUS_NAME_COLUMN);
	    factory.setText(factory.getText() + ":");
	    header.addElement(factory.createElement());

	    TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), CapControlEventLogModel.SUB_BUS_NAME_COLUMN);
	    tfactory.setAbsolutePosition(new Point2D.Float(110, 1));	//override the posX location
	    header.addElement(tfactory.createElement());

	    header.addElement(HorizontalLineElementFactory.createHorizontalLine(20, null, new BasicStroke(0.5f)));

	    for (int i = CapControlEventLogModel.FEEDER_NAME_COLUMN; i < getModel().getColumnCount(); i++)
	    {
	        factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY() + 18));
		    header.addElement(factory.createElement());
		}
	    header.addElement(HorizontalLineElementFactory.createHorizontalLine(38, null, new BasicStroke(0.5f)));
	    subBusGroup.setHeader(header);


	  	GroupFooter footer = ReportFactory.createGroupFooterDefault();
	  	subBusGroup.setFooter(footer);
	  	return subBusGroup;
	}	
	
	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createSubBusGroup());
	  return list;
	}

	/**
	 * Creates the itemBand, the rows of data.
	 * @return the item band.
	 */
	protected ItemBand createItemBand()
	{
		ItemBand items = ReportFactory.createItemBandDefault();
	
			items.addElement(HorizontalLineElementFactory.createHorizontalLine
				(12, java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f)));
			
		TextFieldElementFactory factory;
		for (int i = CapControlEventLogModel.FEEDER_NAME_COLUMN; i < getModel().getColumnNames().length; i++)
		{
			factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			items.addElement(factory.createElement());
		}

		return items;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.report.YukonReportBase#getExpressions()
	 */
	protected ExpressionCollection getExpressions() throws FunctionProcessingException
	{
		super.getExpressions();
		ItemHideFunction hideItem = new ItemHideFunction();
		hideItem.setName(CapControlEventLogModel.SUB_BUS_NAME_STRING + ReportFactory.NAME_HIDDEN);
		hideItem.setField(CapControlEventLogModel.SUB_BUS_NAME_STRING);
		hideItem.setElement(CapControlEventLogModel.SUB_BUS_NAME_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName(CapControlEventLogModel.FEEDER_NAME_STRING + ReportFactory.NAME_HIDDEN);
		hideItem.setField(CapControlEventLogModel.FEEDER_NAME_STRING);
		hideItem.setElement(CapControlEventLogModel.FEEDER_NAME_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(hideItem);

		hideItem = new ItemHideFunction();
		hideItem.setName(CapControlEventLogModel.CAP_BANK_NAME_STRING + ReportFactory.NAME_HIDDEN);
		hideItem.setField(CapControlEventLogModel.CAP_BANK_NAME_STRING);
		hideItem.setElement(CapControlEventLogModel.CAP_BANK_NAME_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(hideItem);

		return expressions;
	}
}
