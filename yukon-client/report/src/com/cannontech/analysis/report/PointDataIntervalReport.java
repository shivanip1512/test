package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Calendar;

import org.jfree.report.Boot;
import org.jfree.report.Element;
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
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.function.ItemHideFunction;
import org.jfree.report.function.ItemMaxFunction;
import org.jfree.report.function.ItemMinFunction;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.tablemodel.PointDataIntervalModel;
import com.cannotech.analysis.jfreereport.ItemMaxValueFunction;

/**
 * Created on Dec 15, 2003
 * Creates a DatabaseReport using the com.cannontech.analysis.data.DatabaseModel tableModel
 * No Group by used, only by column headings.
 *  
 * @author snebben
 */
public class PointDataIntervalReport extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 */
	public PointDataIntervalReport()
	{
		this(new PointDataIntervalModel());
	}

	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf DatabaseModel.
	 * @param data_ - DatabaseModel TableModel data
	 */
	public PointDataIntervalReport(PointDataIntervalModel model_)
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
//		cal.add(java.util.Calendar.DATE, 1);
		cal.set(java.util.Calendar.DATE, 23);
		cal.set(java.util.Calendar.MONTH, Calendar.JANUARY);
		long stop = cal.getTimeInMillis();
		cal.add(java.util.Calendar.DATE, -1);
		long start = cal.getTimeInMillis();
		
		PointDataIntervalModel reportModel = new PointDataIntervalModel(start, stop);
		YukonReportBase dbReport = new PointDataIntervalReport(reportModel);
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
	 * Create a Group for Pao Name.  
	 * @return Group
	 */
	private Group createPaoGroup()
	{
		final Group paoGroup = new Group();
		paoGroup.setName(PointDataIntervalModel.PAO_NAME_STRING + ReportFactory.NAME_GROUP);
	
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
	
		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), PointDataIntervalModel.PAO_NAME_COLUMN);
		factory.setText(factory.getText() + ":");
		header.addElement(factory.createElement());
		
		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), PointDataIntervalModel.PAO_NAME_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(75, 1));	//override the posX location
		header.addElement(tfactory.createElement());

		paoGroup.setHeader(header);
	
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		paoGroup.setFooter(footer);

		return paoGroup;
	}

	/**
	 * Create a Group for Point Name.  
	 * @return Group
	 */
	private Group createPointGroup()
	{
		final Group pointGroup = new Group();
		pointGroup.setName(PointDataIntervalModel.POINT_NAME_STRING + ReportFactory.NAME_GROUP);
		pointGroup.addField(PointDataIntervalModel.PAO_NAME_STRING);
		pointGroup.addField(PointDataIntervalModel.POINT_NAME_STRING);
		  
		GroupHeader header = ReportFactory.createGroupHeaderDefault();

		LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), PointDataIntervalModel.POINT_NAME_COLUMN);
		factory.setText(factory.getText() + ":");
		header.addElement(factory.createElement());

		TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), PointDataIntervalModel.POINT_NAME_COLUMN);
		tfactory.setAbsolutePosition(new Point2D.Float(75, 1));
		header.addElement(tfactory.createElement());

//		for (int i = PointDataIntervalModel.DATE_COLUMN; i <= PointDataIntervalModel.QUALITY_COLUMN; i++)
//		{
//			factory = ReportFactory.createGroupLabelElementDefault(model, i);
//			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18));
//			header.addElement(factory.createElement());
//		}
	
		header.addElement(ReportFactory.createBasicLine("pointGrpLine", 0.5f, 20));
		pointGroup.setHeader(header);
	
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		
		tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataIntervalModel.VALUE_COLUMN);
		tfactory.setFieldname("X"+PointDataIntervalModel.VALUE_STRING + ReportFactory.NAME_ELEMENT);
		footer.addElement(tfactory.createElement());
		pointGroup.setFooter(footer);
		
		tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataIntervalModel.TIME_COLUMN);
		tfactory.setFieldname("X"+PointDataIntervalModel.TIME_STRING + ReportFactory.NAME_ELEMENT);
		footer.addElement(tfactory.createElement());
		pointGroup.setFooter(footer);
		
		return pointGroup;
	}
	
	/**
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
	  final GroupList list = new GroupList();
	  list.add(createPaoGroup());
	  list.add(createPointGroup());
	  return list;
	}


	/**
	 * Creates the itemBand, the rows of data.
	 * @return the item band.
	 */
	protected ItemBand createItemBand()
	{
//		ItemBand items = ReportFactory.createItemBandDefault();
		ItemBand items = new ItemBand();
		items.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE,  new FloatDimension(0, 10));
		items.getBandDefaults().setFontDefinitionProperty( new FontDefinition("Serif", 10, false, false, false, false));
		items.setVisible(false);
	
//		if( showBackgroundColor )
//		{
//			items.addElement(StaticShapeElementFactory.createRectangleShapeElement
//				("background", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0),
//					new java.awt.geom.Rectangle2D.Float(0, 0, -100, -100), false, true));
//			items.addElement(StaticShapeElementFactory.createLineShapeElement
//				("top", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f),
//					new java.awt.geom.Line2D.Float(0, 0, 0, 0)));
//			items.addElement(StaticShapeElementFactory.createLineShapeElement
//				("bottom", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f),
//					new java.awt.geom.Line2D.Float(0, 10, 0, 10)));
//		}
			
//		TextFieldElementFactory factory;
//		factory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataIntervalModel.VALUE_COLUMN);
//		factory.setFieldname("X"+PointDataIntervalModel.VALUE_STRING + ReportFactory.NAME_ELEMENT);
//		items.addElement(factory.createElement());
	
//		for (int i = PointDataIntervalModel.DATE_COLUMN; i <= PointDataIntervalModel.QUALITY_COLUMN; i++)
//		{
//			factory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataIntervalModel.VALUE_COLUMN);
//			items.addElement(factory.createElement());
//		}

		return items;
	}
	
	/**
	 * Creates the function collection. The xml definition for this construct:
	 * @return the functions.
	 * @throws FunctionInitializeException if there is a problem initialising the functions.
	 */
	protected ExpressionCollection getExpressions() throws FunctionInitializeException
	{ 
		super.getExpressions();
		
		ItemMaxFunction totalItem = new ItemMaxFunction();
		totalItem.setName("X"+PointDataIntervalModel.VALUE_STRING + ReportFactory.NAME_ELEMENT);
		totalItem.setProperty("group", PointDataIntervalModel.POINT_NAME_STRING + ReportFactory.NAME_GROUP);
		totalItem.setProperty("field", PointDataIntervalModel.VALUE_STRING);
		totalItem.setProperty("element", PointDataIntervalModel.VALUE_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(totalItem);
		
		ItemMaxValueFunction totalValueItem = new ItemMaxValueFunction();
		totalValueItem.setName("X"+PointDataIntervalModel.TIME_STRING + ReportFactory.NAME_ELEMENT);
		totalValueItem.setDataField(PointDataIntervalModel.TIME_STRING);
		totalValueItem.setProperty("group", PointDataIntervalModel.POINT_NAME_STRING + ReportFactory.NAME_GROUP);
		totalValueItem.setProperty("field", PointDataIntervalModel.VALUE_STRING);
		totalValueItem.setProperty("element", PointDataIntervalModel.TIME_STRING + ReportFactory.NAME_ELEMENT);
		expressions.add(totalValueItem);

//	
//		ItemHideFunction hideItem = new ItemHideFunction();
//		hideItem.setName("hideItem");
//		hideItem.setProperty("field", PointDataIntervalModel.VALUE_STRING);
//		hideItem.setProperty("element", PointDataIntervalModel.VALUE_STRING+ ReportFactory.NAME_ELEMENT);
//		expressions.add(hideItem);

		return expressions;
		
		
	}
}
