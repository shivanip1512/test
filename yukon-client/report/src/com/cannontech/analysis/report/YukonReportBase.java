package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;

import org.jfree.report.Boot;
import org.jfree.report.ElementAlignment;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.PageFooter;
import org.jfree.report.PageHeader;
import org.jfree.report.ReportFooter;
import org.jfree.report.ReportHeader;
import org.jfree.report.ShapeElement;
import org.jfree.report.elementfactory.DateFieldElementFactory;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.function.ElementVisibilitySwitchFunction;
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.function.PageFunction;
import org.jfree.report.function.PageTotalFunction;
import org.jfree.report.function.TextFormatExpression;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.modules.gui.base.PreviewInternalFrame;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.gui.PreviewPanel;
import com.cannontech.analysis.tablemodel.ReportModelBase;

/**
 * Created on Dec 15, 2003
 * Base class for all Report classes.
 * Contains default expressions and functions:
 *  DateExp - requires a String date format and the column name to reference.
 *  PageTotal - the total number of pages in the report
 *  PageNumber - the current page number
 *  PageXofY - uses PageNumber(X) and PageTotal(Y)
 * 
 * Extending classes must implement their own:
 *  Grouping rules (createGroups())
 *  Item bands (createItemBands())
 *  
 * createReport() returns an instance of the JFreeReport.
 * This method calls all required getters/creators for the report to be created.
 * PageHeader/PageFooter, ReportHeader/ReportFooter are consistent for all report models.
 * All of the createXXX() functions may be overridden by an extending class.
 * @author snebben
 */
public abstract class YukonReportBase extends java.awt.event.WindowAdapter
{
	/** Flag indicating background coloring added to separate items in band */
	protected boolean showBackgroundColor = false;
	/** Flag indicating report footer is displayed*/
	protected boolean showReportHeader = true;
	/** Flag indicating report header is displayed*/
	protected boolean showReportFooter = true;
	/** TableModel data structure */
	protected ReportModelBase model = null;
	/** collection of functions */
//	protected ExpressionCollection functions = null;
	/** collection of expressions */
	protected ExpressionCollection expressions = null;
	
	protected String DATE_EXPRESSION = "Date Exp";
	protected String PAGE_TOTAL_FUNCTION = "Page Total";
	protected String PAGE_NUMBER_FUNCTION = "Page Number";
	protected String PAGE_XOFY_EXPRESSION = "PageXofY";
	
	protected PageFormat pageFormat= null;

	public void showPreviewFrame(ReportModelBase model_) throws Exception
	{
		// initialize JFreeReport
		Boot.start();
		
		model = model_;
		model.collectData();

		JFreeReport report = createReport();
		report.setData(model);
		
		
		final PreviewDialog dialog = new PreviewDialog(report);
		dialog.setModal(true);
		dialog.pack();
		dialog.setVisible(true);
	}
	
	public PreviewPanel getPreviewPanel(ReportModelBase model_) throws Exception
	{
//		Boot.start();
		model = model_;
		model.collectData();
		JFreeReport report = createReport();
		report.setData(model);
		final PreviewPanel pPanel = new PreviewPanel(report);
		return pPanel;
	}
	
	public PreviewInternalFrame getPreviewFrame(ReportModelBase model_) throws Exception
	{
		Boot.start();
		model = model_;
		model.collectData();
		JFreeReport report = createReport();
		report.setData(model);
		final PreviewInternalFrame pFrame = new PreviewInternalFrame(report);
		return pFrame;
	}	
	public PreviewDialog getPreviewDialog(ReportModelBase model_) throws Exception
	{
		Boot.start();
		model = model_;
		model.collectData();
		JFreeReport report = createReport();
		report.setData(model);
		final PreviewDialog pDialog = new PreviewDialog(report);
		
		return pDialog;
	}
	
	/**
	 * Creates the report.
	 * @return the constructed report.
	 * @throws FunctionInitializeException if there was a problem initialising any of the functions.
	 */
	public JFreeReport createReport() throws org.jfree.report.function.FunctionInitializeException
	{
		final JFreeReport report = new JFreeReport();
		report.setName(getModel().getTitleString());
		if(isShowReportFooter())
			report.setReportFooter(createReportFooter());
		if(isShowReportHeader())
			report.setReportHeader(createReportHeader());
		report.setPageFooter(createPageFooter());
		report.setPageHeader(createPageHeader());
		report.setGroups(createGroups());
		report.setItemBand(createItemBand());
		report.setExpressions(getExpressions());
		report.setPropertyMarked("report.date", true);
		report.setDefaultPageFormat(getPageFormat());
		return report;
	}

	/**
	 * Extend this method to implement an ItemBand.
	 * This is basically the row data.
	 * @return the itemBand
	 */
	protected abstract ItemBand createItemBand();

	/**
	 * Extend this method to implement report Grouping.
	 * This is a GroupList of Groups by which the report
	 *  will be organized.
	 * @return
	 */
	protected abstract GroupList createGroups();
	
	/**
	 * Returns the expressions, use .add(...) for new expressions
	 * @return ExpressionCollection expressions
	 * @throws FunctionInitializeException
	 */
	protected ExpressionCollection getExpressions() throws FunctionInitializeException
	{
		if( expressions == null)
		{
			expressions = new ExpressionCollection();
			expressions.add(getPageXofYExpression());
			expressions.add(getBackgroundTriggerFunction());
			expressions.add(getPageNumberFunction());
			expressions.add(getPageTotalFunction());
		}
		return expressions;
	}	

	/**
	 * @return a background element
	 */
	protected ElementVisibilitySwitchFunction getBackgroundTriggerFunction()
	{
		final ElementVisibilitySwitchFunction backgroundTrigger = new ElementVisibilitySwitchFunction();
		backgroundTrigger.setName("backgroundTrigger");
		backgroundTrigger.setProperty("element", "background");
		return backgroundTrigger;
	}

	/**
	 * Return a date expression formatted using dateFormat and 
	 *  data coming from columnName.
	 * @param dateFormat - SimpleDateFormat
	 * @param columnName - String, MUST be a column name from data AbstractTableModel
	 * @return TextFormatExpression 
	 */
	protected TextFormatExpression getDateExpression(String dateFormat, String columnName)
	{
		final TextFormatExpression dateExpression = new TextFormatExpression();
		dateExpression.setName(DATE_EXPRESSION);
		dateExpression.setPattern("{0, date, " + dateFormat + "}");
		dateExpression.setProperty("0", columnName);
		return dateExpression;
	}
	
	/**
	 * Return a PageNumber function.
	 * @return current page number function of a particular page.
	 */
	protected PageFunction getPageNumberFunction()
	{
		final PageFunction pageNumber = new PageFunction();
		pageNumber.setName(PAGE_NUMBER_FUNCTION);
		return pageNumber;
	}
		
	/**
	 * Return a PageTotalNumber function.
	 * @return total of all pages in report.
	 */
	protected PageTotalFunction getPageTotalFunction()
	{
		final PageTotalFunction pageTotalFunction = new PageTotalFunction();
		pageTotalFunction.setName(PAGE_TOTAL_FUNCTION);
		return pageTotalFunction;
	}

	/**
	 * Return a TextFormatExprssion for pageNumber of pageCount. 
	 * @return String in format "Page x of y"
	 */
	protected TextFormatExpression getPageXofYExpression()
	{
		final TextFormatExpression pageXofYExp = new TextFormatExpression();
		pageXofYExp.setName(PAGE_XOFY_EXPRESSION);
		pageXofYExp.setPattern("Page {0} of {1}");
		pageXofYExp.setProperty("0", PAGE_NUMBER_FUNCTION);
		pageXofYExp.setProperty("1", PAGE_TOTAL_FUNCTION);
		return pageXofYExp;
	}
	
	/**
	 * Creates a page footer.
	 * @return The page footer.
	 */
	protected PageFooter createPageFooter()
	{
		final PageFooter pageFooter = new PageFooter();
		pageFooter.getStyle().setStyleProperty (ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
		pageFooter.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 10));

		/** A rectangle around the page footer */
		pageFooter.addElement(ReportFactory.createBasicLine("pfLine", 0.5f, 6));
		/** A label for Cannon Technologies in the footer object */
		/*
		final LabelElementFactory lfactory = new LabelElementFactory();
		lfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 12));
		lfactory.setMinimumSize(new FloatDimension(-100, 0));
		lfactory.setHorizontalAlignment(ElementAlignment.LEFT);
		lfactory.setVerticalAlignment(ElementAlignment.MIDDLE);
		lfactory.setText("Cannon Technologies, Inc.");
		lfactory.setDynamicHeight(Boolean.TRUE);
		pageFooter.addElement(lfactory.createElement());
		*/
	
		final DateFieldElementFactory factory = new DateFieldElementFactory();
		factory.setAbsolutePosition(new Point2D.Float(0, 8));
		factory.setMinimumSize(new FloatDimension(-100, 0));
		factory.setDynamicHeight(new Boolean(true));
		factory.setHorizontalAlignment(ElementAlignment.RIGHT);
		factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		factory.setNullString("<null>");
		factory.setFormatString("d-MMM-yyyy HH:mm:ss  ");
		factory.setFieldname("report.date");
		pageFooter.addElement(factory.createElement());

		final TextFieldElementFactory tfactory = new TextFieldElementFactory();
		tfactory.setAbsolutePosition(new Point2D.Float(0, 8));
		tfactory.setMinimumSize(new FloatDimension(-100, 0));
		tfactory.setHorizontalAlignment(ElementAlignment.CENTER);
		tfactory.setVerticalAlignment(ElementAlignment.BOTTOM);
		tfactory.setFieldname(PAGE_XOFY_EXPRESSION);
		tfactory.setDynamicHeight(Boolean.TRUE);
		pageFooter.addElement(tfactory.createElement());
		return pageFooter;
	}

	/**
	 * Creates the page header.
	 * @return the page header.
	 */
	protected PageHeader createPageHeader()
	{
		final PageHeader header = new PageHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 14));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 10));
		header.setDisplayOnFirstPage(true);
		header.setDisplayOnLastPage(false);
		return header;
	}

	/**
	 * Creates the report footer.
	 * @return the report footer.
	 */
	protected ReportFooter createReportFooter()
	{
		ReportFooter footer = ReportFactory.createReportFooterDefault();
		
		final LabelElementFactory factory = new LabelElementFactory();
		factory.setAbsolutePosition(new Point2D.Float(0, 0));
		factory.setMinimumSize(new FloatDimension(-100, 16));
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText("*** END OF REPORT ***");
		footer.addElement(factory.createElement());
		return footer;
	}

	/**
	 * Creates the report header.
	 * @return the report header.
	 */
	protected ReportHeader createReportHeader()
	{
		ReportHeader header = ReportFactory.createReportHeaderDefault();
	
		LabelElementFactory factory = new LabelElementFactory();
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 0));
		factory.setMinimumSize(new FloatDimension(-100, 24));
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText(getModel().getTitleString());
		header.addElement(factory.createElement());
				
		factory = new LabelElementFactory();
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 20));
		factory.setMinimumSize(new FloatDimension(-100, 24));
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText(getModel().getDateRangeString());
		factory.setFontSize(new Integer(12));
		header.addElement(factory.createElement());		

		return header;
	}
	/**
	 * @return
	 */
	public ReportModelBase getModel()
	{
		return model;
	}

	/**
	 * @param base
	 */
	public void setModel(ReportModelBase base)
	{
		model = base;
	}

	/**
	 * @return
	 */
	public PageFormat getPageFormat()
	{
		if( pageFormat == null)
		{
			//Define the report Paper properties and format.
			java.awt.print.Paper reportPaper = new java.awt.print.Paper();
			reportPaper.setImageableArea(30, 40, 552, 712);	//8.5 x 11 -> 612w 792h
			pageFormat = new java.awt.print.PageFormat();
			pageFormat.setPaper(reportPaper);
		}
		return pageFormat;
	}

	/**
	 * @param format
	 */
	public void setPageFormat(PageFormat pageFormat_)
	{
		pageFormat = pageFormat_;
	}

	/**
	 * @return Returns the showBackgroundColor.
	 */
	public boolean isShowBackgroundColor() {
		return showBackgroundColor;
	}
	/**
	 * @param showBackgroundColor The showBackgroundColor to set.
	 */
	public void setShowBackgroundColor(boolean showBackgroundColor) {
		this.showBackgroundColor = showBackgroundColor;
	}
	/**
	 * @return Returns the showReportFooter.
	 */
	public boolean isShowReportFooter() {
		return showReportFooter;
	}
	/**
	 * @param showReportFooter The showReportFooter to set.
	 */
	public void setShowReportFooter(boolean showReportFooter) {
		this.showReportFooter = showReportFooter;
	}
	/**
	 * @return Returns the showReportHeader.
	 */
	public boolean isShowReportHeader() {
		return showReportHeader;
	}
	/**
	 * @param showReportHeader The showReportHeader to set.
	 */
	public void setShowReportHeader(boolean showReportHeader) {
		this.showReportHeader = showReportHeader;
	}
}
