package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Date;

import org.jfree.report.Boot;
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
import com.cannontech.analysis.tablemodel.LoadGroupModel;

/**
 * Created on Dec 15, 2003
 * Creates a LGAccountingReport using the com.cannontech.analysis.data.loadgroup.LoadGroupReportData tableModel
 * Groups data by Load Group.  
 * @author snebben
 */
public class LGAccountingReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf LoadGroupReportData.
	 */
	public LGAccountingReport()
	{
		this(new LoadGroupModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf LoadGroupReportData.
	 * @param data_ - LoadGroupReportData TableModel data
	 */
	public LGAccountingReport(LoadGroupModel model_)
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
		cal.add(java.util.Calendar.DATE, 1);
		Date stop = cal.getTime();
		cal.add(java.util.Calendar.DATE, -30);
		Date start = cal.getTime();

		//Initialize the report data and populate the TableModel (collectData).
		LoadGroupModel model = new LoadGroupModel(start, stop);

		//Create the report
		YukonReportBase lgaReport = new LGAccountingReport(model);
		lgaReport.getModel().collectData();
		JFreeReport report = lgaReport.createReport();
		report.setData(lgaReport.getModel());
	
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
	 * Create a Group for Load Group column.  
	 * @return
	 */
	private Group createLoadGrpGroup()
	{
		final Group collGrpGroup = new Group();
		collGrpGroup.setName("Load Group");
		collGrpGroup.addField(LoadGroupModel.PAO_NAME_STRING);

		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), LoadGroupModel.PAO_NAME_COLUMN);
		factory.setText("Control History for Group:");	//override default
		header.addElement(factory.createElement());

		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), LoadGroupModel.PAO_NAME_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(140, 1));	//override
		header.addElement(tfactory.createElement());
		header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(0.5f), new Line2D.Float(0, 20, 0, 20)));

		for (int i = 1; i <= LoadGroupModel.MONTHLY_CONTROL_COLUMN; i++)
		{
			factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 30));	//lower this row of "headings"
			header.addElement(factory.createElement());
		}
		collGrpGroup.setHeader(header);

		GroupFooter footer = ReportFactory.createGroupFooterDefault();

		factory = ReportFactory.createGroupLabelElementDefault(getModel(), LoadGroupModel.SEASONAL_CONTROL_COLUMN);
		factory.setText(factory.getText() + ":");
		footer.addElement(factory.createElement());

		tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), LoadGroupModel.SEASONAL_CONTROL_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(100, 1));	//override
		footer.addElement(tfactory.createElement());
		
		factory = ReportFactory.createGroupLabelElementDefault(getModel(), LoadGroupModel.ANNUAL_CONTROL_COLUMN);
		factory.setText(factory.getText() + ":");
		factory.setAbsolutePosition(new Point2D.Float(0, 12));	//override
		footer.addElement(factory.createElement());

		tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), LoadGroupModel.ANNUAL_CONTROL_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(100, 12));	//override
		footer.addElement(tfactory.createElement());
		
		collGrpGroup.setFooter(footer);

		return collGrpGroup;
	}

	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createLoadGrpGroup());
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
			items.addElement(StaticShapeElementFactory.createLineShapeElement
				("top", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f),
					new java.awt.geom.Line2D.Float(0, 0, 0, 0)));
			items.addElement(StaticShapeElementFactory.createLineShapeElement
				("bottom", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f),
					new java.awt.geom.Line2D.Float(0, 10, 0, 10)));
		}	

		TextFieldElementFactory factory;
		//Start at 1, we don't want to include the Load Group column, our group by column.
		for (int i = 1; i <= LoadGroupModel.MONTHLY_CONTROL_COLUMN; i++)
		{
			factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			items.addElement(factory.createElement());
		}
		return items;
	}
}
