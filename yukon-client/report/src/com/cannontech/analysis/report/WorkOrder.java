package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.report.Boot;
import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.PageHeader;
import org.jfree.report.ReportHeader;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.WorkOrderModel;

/**
 * Created on Dec 15, 2003
 * Creates a WorkOrderReport using the com.cannontech.analysis.tablemodel.WorkOrderModel tableModel
 * Groups data by Date.  Orders asc/desc based on tableModel definition.  
 * @author snebben
 */
public class WorkOrder extends YukonReportBase
{
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 */
	public WorkOrder()
	{
		this(new WorkOrderModel());
	}
	/**
	 * Constructor for Report.
	 * Data Base for this report type is instanceOf SystemLogModel.
	 * @param data_ - SystemLogModel TableModel data
	 */
	public WorkOrder(WorkOrderModel woModel_)
	{
		super();
		setModel(woModel_);
	}

	/**
	 * Creates the report.
	 * @return the constructed report.
	 * @throws FunctionInitializeException if there was a problem initialising any of the functions.
	 */
	public JFreeReport createReport() throws org.jfree.report.function.FunctionInitializeException
	{
		// Show the report header and footer if there is no specific work order
		setShowReportFooter( ((WorkOrderModel)getModel()).getOrderID() == null);
		setShowReportHeader(((WorkOrderModel)getModel()).getOrderID() == null);
		return super.createReport();
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

		//Define start and stop parameters for a default 90 day report.
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		cal.add(java.util.Calendar.DATE, 1);
		long stop = cal.getTimeInMillis();
		cal.add(java.util.Calendar.DATE, -90);
		long start = cal.getTimeInMillis();

		//Initialize the report data and populate the TableModel (collectData).
		WorkOrderModel model = new WorkOrderModel();
		model.setStartTime(start);
		model.setStopTime(stop);
		model.setECIDs(new Integer(0));
//		model.setAccountID(new Integer(1));
//		model.setSearchColumn(WorkOrderModel.SEARCH_COL_DATE_CLOSED);
		
		YukonReportBase workOrder = new WorkOrder(model);
		workOrder.getModel().collectData();

		//Create the report
		JFreeReport report = workOrder.createReport();
		report.setData(workOrder.getModel());
		
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
	 * Create a GroupList and all Group(s) to it.
	 * @return the groupList.
	 */
	protected GroupList createGroups()
	{
		final GroupList list = new GroupList();
		//Add a Grouping for Date column.
		list.add(createWorkOrderGroup());
		return list;
	}


	/**
	 * Creates the itemBand, the rows of data.
	 * @return the item band.
	 */
	protected ItemBand createItemBand()
	{
		ItemBand items = ReportFactory.createItemBandDefault();
		
		items.addElement(StaticShapeElementFactory.createRectangleShapeElement
			("box", Color.decode("#DFDFDF"), new BasicStroke(0),
				new Rectangle2D.Float(0, 0, -100, -100), true, false));
	
		TextFieldElementFactory factory;
		for (int i = WorkOrderModel.ITEM_BAND_START_INDEX; i <= WorkOrderModel.ITEM_BAND_END_INDEX; i++)
		{
			factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			factory.setNullString("---");
			items.addElement(factory.createElement());
		}
		return items;
	}
	
	private Group createWorkOrderGroup()
	{
		//This group is treated like an item band as it holds all of the display information about the work order,
		// the item band only contains the (multiple) listing of installed hardware.
		// Therefore, alot of the settings are overridden to make them feel like an item band verses a group.
		final Group acctGroup = new Group();
		acctGroup.setName(WorkOrderModel.ORDER_NO_STRING + ReportFactory.NAME_GROUP);
		acctGroup.addField(WorkOrderModel.ORDER_NO_STRING);
		
		GroupHeader header = ReportFactory.createGroupHeaderDefault();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, ReportFactory.ITEM_BAND_STYLE_DIMENSION);
		header.getBandDefaults().setFontDefinitionProperty(ReportFactory.ITEM_BAND_FONT);

		int colHeight = 14;	//USE 14 because we know that's what will fit?  SN...ick, ick
		int yLine = colHeight * 6;
		header.addElement(ReportFactory.createBasicLine("woGroupLine", 0.5f, yLine));
		
		//Use reportFactory's regular elements, not the group ones.
		LabelElementFactory factory;
		
		TextFieldElementFactory tfactory;

		for (int i = WorkOrderModel.HEADER_START_INDEX; i <= WorkOrderModel.HEADER_END_INDEX; i++)
		{
			factory = ReportFactory.createLabelElementDefault(getModel(), i);
			factory.setText(factory.getText() + ":");		header.addElement(factory.createElement());
		
			tfactory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			tfactory.setNullString("");
			tfactory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX()+90, getModel().getColumnProperties(i).getPositionY()));
			header.addElement(tfactory.createElement());
		}

		//Installed Hardware header label
		factory = ReportFactory.createLabelElementDefault(getModel(), WorkOrderModel.HEADER_END_INDEX);
		ColumnProperties headEndProps = getModel().getColumnProperties(WorkOrderModel.HEADER_END_INDEX);
		factory.setAbsolutePosition(new Point2D.Float(headEndProps.getPositionX(), headEndProps.getPositionY() + 18 /*group size*/	+ colHeight *2));
		factory.setText("INSTALLED HARDWARE:");
		header.addElement(factory.createElement());
		
		for (int i = WorkOrderModel.ITEM_BAND_START_INDEX; i <= WorkOrderModel.ITEM_BAND_END_INDEX; i++)
		{
			factory = ReportFactory.createLabelElementDefault(getModel(), i);
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 
				headEndProps.getPositionY() + 18 + colHeight *3));
			header.addElement(factory.createElement());
		}
		acctGroup.setHeader(header);
		
		GroupFooter footer = ReportFactory.createGroupFooterDefault();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, ReportFactory.ITEM_BAND_STYLE_DIMENSION);
		footer.getBandDefaults().setFontDefinitionProperty(ReportFactory.ITEM_BAND_FONT);

		for (int i = WorkOrderModel.FOOTER_START_INDEX; i <= WorkOrderModel.FOOTER_END_INDEX; i++)
		{
			factory = ReportFactory.createLabelElementDefault(getModel(), i);
			factory.setText(factory.getText() + ":");
			footer.addElement(factory.createElement());
		
			tfactory = ReportFactory.createTextFieldElementDefault(getModel(), i);
			tfactory.setNullString("");
			tfactory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY()+colHeight));
			footer.addElement(tfactory.createElement());
		}
		//move the signature line down the page a bit
		float posY = getModel().getColumnProperties(WorkOrderModel.FOOTER_END_INDEX).getPositionY()+colHeight*4;
		
		//Signature label
		factory = ReportFactory.createLabelElementDefault("Signature", 0, posY + colHeight, 50);
		footer.addElement(factory.createElement());
		footer.addElement(StaticShapeElementFactory.createLineShapeElement("sigLine", null, new BasicStroke(0.5f), new Line2D.Float(50, posY + colHeight*2, 300, posY+ colHeight*2)));
		
		//Date label
		factory = ReportFactory.createLabelElementDefault("Date", 330, posY + colHeight, 30);
		footer.addElement(factory.createElement());
		footer.addElement(StaticShapeElementFactory.createLineShapeElement("dateLine", null, new BasicStroke(0.5f), new Line2D.Float(360, posY + colHeight *2, 500, posY+ colHeight*2)));

		footer.setPagebreakAfterPrint(true);
		acctGroup.setFooter(footer);
		return acctGroup;
	}
	
	/**
	 * Creates the page header.
	 * @return the page header.
	 */
	protected PageHeader createPageHeader()
	{
		super.createPageHeader();
		final PageHeader header = new PageHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 14));
		header.setDisplayOnFirstPage(!isShowReportHeader());
		header.setDisplayOnLastPage(false);

		TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), WorkOrderModel.EC_NAME_COLUMN );
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		header.addElement(factory.createElement());

		factory = ReportFactory.createTextFieldElementDefault(getModel(), WorkOrderModel.EC_INFO_COLUMN);
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(WorkOrderModel.EC_INFO_COLUMN).getPositionX(),14));
		factory.setFontSize(new Integer(12));
		factory.setNullString("");
		header.addElement(factory.createElement());

		return header;
	}

	/**
	 * Creates the report header.
	 * @return the report header.
	 */
	protected ReportHeader createReportHeader()
	{
		final ReportHeader header = super.createReportHeader();
		header.setPagebreakAfterPrint(true);
		return header;
	}
}