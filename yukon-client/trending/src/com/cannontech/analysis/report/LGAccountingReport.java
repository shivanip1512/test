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
import org.jfree.report.elementfactory.DateFieldElementFactory;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.BandStyleSheet;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.data.lm.LGAccounting;
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
		super();
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param startTime_ - startTime in millis for data query
	 * @param stopTime_ - stopTime in millis for data query
	 */
	public LGAccountingReport(long startTime_, long stopTime_)
	{
		super();
		model = new LoadGroupModel( startTime_, stopTime_);
	}
	
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf LoadGroupReportData.
	 * @param data_ - LoadGroupReportData TableModel data
	 */
	public LGAccountingReport(LoadGroupModel model_)
	{
		super();
		model = model_;
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
		long stop = cal.getTimeInMillis();
		cal.add(java.util.Calendar.DATE, -30);
		long start = cal.getTimeInMillis();

		//Initialize the report data and populate the TableModel (collectData).
		LGAccountingReport lgaReport = new LGAccountingReport(start, stop);
		lgaReport.getModel().collectData();

		//Define the report Paper properties and format.
		java.awt.print.Paper reportPaper = new java.awt.print.Paper();
		reportPaper.setImageableArea(30, 40, 552, 712);	//8.5 x 11 -> 612w 792h
		java.awt.print.PageFormat pageFormat = new java.awt.print.PageFormat();
		pageFormat.setPaper(reportPaper);
	
		//Create the report
		JFreeReport report = lgaReport.createReport();
		report.setDefaultPageFormat(pageFormat);
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
	  collGrpGroup.addField(LGAccounting.PAO_NAME_STRING);

	  final GroupHeader header = new GroupHeader();

	  header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 50));
	  header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));
	  header.getStyle().setStyleProperty(BandStyleSheet.REPEAT_HEADER, Boolean.TRUE);
	
	  LabelElementFactory factory = new LabelElementFactory();
	  factory.setName("Load Group Label");
	  factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 1));
	  factory.setMinimumSize(new FloatDimension(110, 20));
	  factory.setHorizontalAlignment(ElementAlignment.LEFT);
	  factory.setVerticalAlignment(ElementAlignment.BOTTOM);
	  factory.setText("Control History for Group:");
	  header.addElement(factory.createElement());

	  final TextFieldElementFactory tfactory = new TextFieldElementFactory();
	  tfactory.setName("Load Group Element");
	  tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(115, 1));
	  tfactory.setMinimumSize(new FloatDimension(300, 20));
	  tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
	  tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
	  tfactory.setNullString("<null>");
	  tfactory.setFieldname(LGAccounting.PAO_NAME_STRING);
	  header.addElement(tfactory.createElement());
	  
	  for (int i = 1; i < getModel().getColumnNames().length; i++)
	  {
		  factory = new LabelElementFactory();
		  factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 30));
		  factory.setText(getModel().getColumnNames()[i]);
		  factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), getModel().getColumnProperties(i).getHeight() ));
		  factory.setHorizontalAlignment(ElementAlignment.LEFT);
		  factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		  header.addElement(factory.createElement());
	  }
	  header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(0.5f), new java.awt.geom.Line2D.Float(0, 20, 0, 20)));
	  collGrpGroup.setHeader(header);

	  final GroupFooter footer = new GroupFooter();
	  footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
	  footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 9, true, false, false, false));

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

		//Start at 1, we don't want to include the Load Group column, our group by column.
		for (int i = 1; i < getModel().getColumnNames().length; i++)
		{
			TextFieldElementFactory factory = new TextFieldElementFactory();
			if( getModel().getColumnClass(i).equals(String.class))
				factory = new TextFieldElementFactory();
			else if( getModel().getColumnClass(i).equals(java.util.Date.class))
			{
				factory = new DateFieldElementFactory();
				((DateFieldElementFactory)factory).setFormatString(getModel().getColumnProperties(i).getValueFormat());
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
