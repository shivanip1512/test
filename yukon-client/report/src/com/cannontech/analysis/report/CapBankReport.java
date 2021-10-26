package com.cannontech.analysis.report;

import java.awt.BasicStroke;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.GroupList;
import org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers.JFreeReportReadHandler;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.CapBankListModel;

/**
 * Created on Dec 15, 2003
 * Creates a DatabaseReport using the com.cannontech.analysis.data.DatabaseModel tableModel
 * No Group by used, only by column headings.
 *  
 * @author snebben
 */
public class CapBankReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 */
	public CapBankReport()
	{
		this(new CapBankListModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 * @param data_ - DatabaseModel TableModel data
	 */
	public CapBankReport(CapBankListModel model_)
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
		
		CapBankListModel model = new CapBankListModel();
		YukonReportBase dbReport = new CapBankReport(model);
		dbReport.getModel().collectData();
	
		//Create the report
		JFreeReportReadHandler report = dbReport.createReport();
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
	private Group createColumnHeadingGroup()
	{
		final Group collHdgGroup = new Group();
		collHdgGroup.setName("Column Heading");
	
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
	
		LabelElementFactory factory;
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
			factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			header.addElement(factory.createElement());
		}
	
		header.addElement(ReportFactory.createBasicLine("chGroupLine", 0.5f, 22));
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
	
		if( showBackgroundColor )
		{
			items.addElement(RectangleElementFactory.createFilledRectangle
				(0, 0, -100, -100, java.awt.Color.decode("#DFDFDF")));
			items.addElement(HorizontalLineElementFactory.createHorizontalLine
			    (0, java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f)));
			items.addElement(HorizontalLineElementFactory.createHorizontalLine
				(0, java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f)));
		}
			
		TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), 0);
		items.addElement(factory.createElement());
	
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
			factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			items.addElement(factory.createElement());
		}

		return items;
	}
}
