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
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.tablemodel.WorkOrderModel;

/**
 * Created on Dec 15, 2003
 * Creates a LGControlLogReport using the com.cannontech.analysis.data.loadgroup.LMControlLogData tableModel
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
		// Show the report header and footer if there is no specific work order
		setShowReportFooter(woModel_.getOrderID() == null);
		setShowReportHeader(woModel_.getOrderID() == null);
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
		YukonReportBase workOrder = ReportFuncs.createYukonReport(ReportTypes.EC_WORK_ORDER_DATA);
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
		WorkOrderModel woModel = new WorkOrderModel();
		woModel.setStartTime(start);
		woModel.setStopTime(stop);
		woModel.setECIDs(new Integer(0));
//		woModel.setAccountID(new Integer(1));
//		woModel.setSearchColumn(WorkOrderModel.SEARCH_COL_DATE_CLOSED);
		workOrder.setModel( woModel );
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
		final ItemBand items = new ItemBand();
		items.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 10));
		items.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 12));
		
		items.addElement(StaticShapeElementFactory.createRectangleShapeElement
			("box", Color.decode("#DFDFDF"), new BasicStroke(0),
				new Rectangle2D.Float(0, 0, -100, -100), true, false));
	
		TextFieldElementFactory factory = new TextFieldElementFactory();
		factory.setDynamicHeight(new Boolean(true));
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setNullString("---");
		for (int i = WorkOrderModel.ITEM_BAND_START_INDEX; i <= WorkOrderModel.ITEM_BAND_END_INDEX; i++)
		{
			factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(i).getPositionX(),getModel().getColumnProperties(i).getPositionY()));
			factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), getModel().getColumnProperties(i).getHeight()));
			factory.setFieldname(getModel().getColumnNames()[i]);
			items.addElement(factory.createElement());
		}

		return items;
	}
	
	private Group createWorkOrderGroup()
	{
		final Group acctGroup = new Group();
		acctGroup.setName("Work Order Group");
		acctGroup.addField(getModel().getColumnName(WorkOrderModel.ORDER_NO_COLUMN));
		
		final GroupHeader header = new GroupHeader();
		
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 20));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 12, false, false, false, false));
		int yLine = ((WorkOrderModel)getModel()).getColHeight() * 6;
		header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(0.5f), new Line2D.Float(0, yLine, 0, yLine)));
		LabelElementFactory factory = new LabelElementFactory();
		factory.setDynamicHeight(new Boolean(true));
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		
		TextFieldElementFactory tfactory = new TextFieldElementFactory();
		tfactory.setDynamicHeight(new Boolean(true));
		tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
		tfactory.setVerticalAlignment(ElementAlignment.MIDDLE);
		tfactory.setNullString("");
		
		for (int i = WorkOrderModel.HEADER_START_INDEX; i <= WorkOrderModel.HEADER_END_INDEX; i++)
		{
			factory.setName(getModel().getColumnName(i)+ " Label");
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY()));
			factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), getModel().getColumnProperties(i).getHeight() ));
			factory.setText(getModel().getColumnName(i)+ ":");
			header.addElement(factory.createElement());
		
			tfactory.setName(getModel().getColumnName(i)+" Element");
			tfactory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX()+90, getModel().getColumnProperties(i).getPositionY()));
			tfactory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), getModel().getColumnProperties(i).getHeight() ));
			tfactory.setFieldname(getModel().getColumnName(i));
			header.addElement(tfactory.createElement());
		}

		//Installed Hardware header label
		factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(WorkOrderModel.HEADER_END_INDEX).getPositionX(), 
				getModel().getColumnProperties(WorkOrderModel.HEADER_END_INDEX).getPositionY()+getModel().getColumnProperties(WorkOrderModel.HEADER_END_INDEX).getHeight()+((WorkOrderModel)getModel()).getColHeight()*2));
		factory.setText("INSTALLED HARDWARE:");
		factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(WorkOrderModel.HEADER_END_INDEX).getWidth(), getModel().getColumnProperties(WorkOrderModel.HEADER_END_INDEX).getHeight() ));
		factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		header.addElement(factory.createElement());
		
		for (int i = WorkOrderModel.ITEM_BAND_START_INDEX; i <= WorkOrderModel.ITEM_BAND_END_INDEX; i++)
		{
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 
					getModel().getColumnProperties(WorkOrderModel.HEADER_END_INDEX).getPositionY()+getModel().getColumnProperties(WorkOrderModel.HEADER_END_INDEX).getHeight()+((WorkOrderModel)getModel()).getColHeight()*3));
			factory.setText(getModel().getColumnNames()[i]);
			factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), getModel().getColumnProperties(i).getHeight() ));
			factory.setVerticalAlignment(ElementAlignment.BOTTOM);
			header.addElement(factory.createElement());
		}
		acctGroup.setHeader(header);
		
		final GroupFooter footer = new GroupFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 20));
		footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 12, false, false, false, false));
		for (int i = WorkOrderModel.FOOTER_START_INDEX; i <= WorkOrderModel.FOOTER_END_INDEX; i++)
		{
			factory.setName(getModel().getColumnName(i)+ " Label");
			factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY()));
			factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), getModel().getColumnProperties(i).getHeight() ));
			factory.setText(getModel().getColumnName(i)+ ":");
			footer.addElement(factory.createElement());
		
			tfactory.setName(getModel().getColumnName(i)+" Element");
			tfactory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY()+((WorkOrderModel)getModel()).getColHeight()));
			tfactory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(i).getWidth(), getModel().getColumnProperties(i).getHeight() ));
			tfactory.setFieldname(getModel().getColumnName(i));
			footer.addElement(tfactory.createElement());
		}
		//move the signature line down the page a bit
		float posY = getModel().getColumnProperties(WorkOrderModel.FOOTER_END_INDEX).getPositionY()+((WorkOrderModel)getModel()).getColHeight()*4;

		
		//Signature label
		factory.setName("Signature Label");
		factory.setAbsolutePosition(new Point2D.Float(0, posY + ((WorkOrderModel)getModel()).getColHeight()));
		factory.setMinimumSize(new FloatDimension(50, ((WorkOrderModel)getModel()).getColHeight()));
		factory.setText("Signature");
		footer.addElement(factory.createElement());
		footer.addElement(StaticShapeElementFactory.createLineShapeElement("sigLine", null, new BasicStroke(0.5f), new Line2D.Float(50, posY + ((WorkOrderModel)getModel()).getColHeight()*2, 300, posY+ ((WorkOrderModel)getModel()).getColHeight()*2)));
		
		//Date label
		factory.setName("Date Label");
		factory.setAbsolutePosition(new Point2D.Float(330, posY + ((WorkOrderModel)getModel()).getColHeight()));
		factory.setMinimumSize(new FloatDimension(30, ((WorkOrderModel)getModel()).getColHeight()));
		factory.setText("Date");
		footer.addElement(factory.createElement());
		footer.addElement(StaticShapeElementFactory.createLineShapeElement("dateLine", null, new BasicStroke(0.5f), new Line2D.Float(360, posY + ((WorkOrderModel)getModel()).getColHeight()*2, 500, posY+ ((WorkOrderModel)getModel()).getColHeight()*2)));
		
		
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
		final PageHeader header = new PageHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 14));
		header.setDisplayOnFirstPage(false);
		header.setDisplayOnLastPage(false);

		TextFieldElementFactory factory = new TextFieldElementFactory();
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setDynamicHeight(new Boolean(true));
		
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(WorkOrderModel.EC_NAME_COLUMN).getPositionX(),getModel().getColumnProperties(WorkOrderModel.EC_NAME_COLUMN).getPositionY()));
		factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(WorkOrderModel.EC_NAME_COLUMN).getWidth(), getModel().getColumnProperties(WorkOrderModel.EC_NAME_COLUMN).getHeight()));
		factory.setNullString("Energy Company");
		factory.setFieldname(getModel().getColumnNames()[WorkOrderModel.EC_NAME_COLUMN]);
		header.addElement(factory.createElement());

		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(getModel().getColumnProperties(WorkOrderModel.EC_INFO_COLUMN).getPositionX(),14));
		factory.setMinimumSize(new FloatDimension(getModel().getColumnProperties(WorkOrderModel.EC_INFO_COLUMN).getWidth(), getModel().getColumnProperties(WorkOrderModel.EC_INFO_COLUMN).getHeight()));
		factory.setFontSize(new Integer(12));
		factory.setNullString("");
		factory.setFieldname(getModel().getColumnNames()[WorkOrderModel.EC_INFO_COLUMN]);
		header.addElement(factory.createElement());

		return header;
	}
}

