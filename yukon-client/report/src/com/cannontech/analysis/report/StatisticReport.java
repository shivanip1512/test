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

import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.tablemodel.StatisticModel;

/**
 * Created on Dec 15, 2003
 * Creates a StatisticReport using the com.cannontech.analysis.data.statistic.* tableModel(s)
 * Groups data by column headings only.
 * See Constructor for options to create different types of statistic reports.  
 * @author snebben
 */
public class StatisticReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 */
	public StatisticReport()
	{
		//We must default the model to something!
		this(new StatisticModel());
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param data_ - SystemLogModel TableModel data
	 */
	public StatisticReport(StatisticModel model_)
	{
		super();
		setModel(model_);
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param reportType_ - valid report types are:
	 * 	carr[ier] - StatisticCarrierCommData
	 * 	trans[mitter] - StatisticalTransmitterCommData
	 * 	dev[ice] = StatisticalDeviceCommData
	 * 	comm[channel] - StatisticalCommChannelData
	 * @param statType_ - DYNAMICPAOSTATISTICS.statType
	 */
	public StatisticReport(String modelTypeString, String statType_)
	{
		super();
		StatisticModel model = null;
		if( modelTypeString.startsWith("carr"))
			model = (StatisticModel)ReportTypes.create(ReportTypes.STAT_CARRIER_COMM_DATA);
		else if( modelTypeString.startsWith("trans"))
			model = (StatisticModel)ReportTypes.create(ReportTypes.STAT_TRANS_COMM_DATA);
		else if( modelTypeString.startsWith("dev"))
			model = (StatisticModel)ReportTypes.create(ReportTypes.STAT_DEVICE_COMM_DATA);
		else if( modelTypeString.startsWith("comm"))
			model = (StatisticModel)ReportTypes.create(ReportTypes.STAT_COMM_CHANNEL_DATA);

		model.setStatType(statType_);
		setModel(model);
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
		Boot.start();
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

		String modelType = "carrier";	//default
		if( args.length > 0)
				modelType = args[0].toLowerCase();	//StatisticData report type
		String statType = "Monthly";
		if( args.length >= 2)	//DynamicPaoStatistics.statisticType
			statType = args[1];
		
		YukonReportBase statReport = new StatisticReport(modelType, statType);
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
		collHdgGroup.setName("Column Heading");
	
		final GroupHeader header = new GroupHeader();
	
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
	
		LabelElementFactory factory;
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
			factory = new LabelElementFactory();
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY()));
			factory.setText(getModel().getColumnNames()[i]);
			factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), getModel().getColumnProperties(i).getHeight() ));
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
			
		TextFieldElementFactory factory = new TextFieldElementFactory();
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(0).getPositionX(), getModel().getColumnProperties(0).getPositionY()));
		factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(0).getWidth(), 10));
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setNullString("<null>");
		factory.setFieldname(getModel().getColumnNames()[0]);
		items.addElement(factory.createElement());
	
		for (int i = 0; i < getModel().getColumnNames().length; i++)
		{
			NumberFieldElementFactory nfactory;			
			if( getModel().getColumnClass(i).equals(String.class))
			{
				factory = new TextFieldElementFactory();
			}
			else if( getModel().getColumnClass(i).equals(Integer.class) ||
					getModel().getColumnClass(i).equals(Double.class))
			{
				factory = new NumberFieldElementFactory();
				((NumberFieldElementFactory)factory).setFormatString(getModel().getColumnProperties(i).getValueFormat());
			}
			
			if( factory != null)
			{
				factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(i).getPositionX(),getModel().getColumnProperties(i).getPositionY()));
				factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), 10));
				factory.setHorizontalAlignment(ElementAlignment.LEFT);
				factory.setVerticalAlignment(ElementAlignment.MIDDLE);
				factory.setNullString("<null>");
				factory.setFieldname(getModel().getColumnNames()[i]);
				items.addElement(factory.createElement());
			}
		}

		return items;
	}
}
