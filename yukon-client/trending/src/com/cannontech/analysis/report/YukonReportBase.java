package com.cannontech.analysis.report;

import java.awt.BasicStroke;

import org.jfree.report.Boot;
import org.jfree.report.ElementAlignment;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.PageFooter;
import org.jfree.report.PageHeader;
import org.jfree.report.ReportFooter;
import org.jfree.report.ReportHeader;
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
	
	/** TableModel data structure */
	public ReportModelBase data = null;
	/** collection of functions */
	protected ExpressionCollection functions = null;
	/** collection of expressions */
	protected ExpressionCollection expressions = null;
	
	protected String DATE_EXPRESSION = "Date Exp";
	protected String PAGE_TOTAL_FUNCTION = "Page Total";
	protected String PAGE_NUMBER_FUNCTION = "Page Number";
	protected String PAGE_XOFY_EXPRESSION = "PageXofY";
	
	 
	public void showPreviewFrame(ReportModelBase data_) throws Exception
	{
		// initialize JFreeReport
		Boot.start();
		
		data = data_;
		data.collectData();

		JFreeReport report = createReport();
		report.setData(data);
		
		
		final PreviewDialog dialog = new PreviewDialog(report);
		dialog.setModal(true);
		dialog.pack();
		dialog.setVisible(true);
	}
	
	public PreviewInternalFrame getPreviewFrame(ReportModelBase data_) throws Exception
	{
		Boot.start();
		data = data_;
		data.collectData();
		JFreeReport report = createReport();
		report.setData(data);
		final PreviewInternalFrame pFrame = new PreviewInternalFrame(report);
		
		return pFrame;
	}
	
	/**
	 * Creates the report.
	 * @return the constructed report.
	 * @throws FunctionInitializeException if there was a problem initialising any of the functions.
	 */
	public JFreeReport createReport() throws org.jfree.report.function.FunctionInitializeException
	{
		final JFreeReport report = new JFreeReport();
		report.setName(data.getTitleString());
		report.setReportFooter(createReportFooter());
		report.setReportHeader(createReportHeader());
		report.setPageFooter(createPageFooter());
		report.setPageHeader(createPageHeader());
		report.setGroups(createGroups());
		report.setItemBand(createItemBand());
		report.setFunctions(getFunctions());
		report.setExpressions(getExpressions());
		report.setPropertyMarked("report.date", true);
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
		}
		return expressions;
	}	

	/**
	 * Returns the functions, use .add(...) for new functions
	 * @return ExpressionCollection functions
	 * @throws FunctionInitializeException
	 */
	protected ExpressionCollection getFunctions() throws FunctionInitializeException
	{
		if( functions == null)
		{
			functions = new ExpressionCollection();
			functions.add(getBackgroundTriggerFunction());
			functions.add(getPageNumberFunction());
			functions.add(getPageTotalFunction());
		}
		return functions;
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
		pageFooter.getStyle().setStyleProperty
			(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 22));
		pageFooter.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 10));

		/** A rectangle around the page footer */
		/*		
		pageFooter.addElement(StaticShapeElementFactory.createRectangleShapeElement
			(null, java.awt.Color.black, null, new java.awt.geom.Rectangle2D.Float(0, 0, -100, -100), true, false));
		*/
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
		final TextFieldElementFactory tfactory = new TextFieldElementFactory();
		tfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 12));
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
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 10));
		header.setDisplayOnFirstPage(true);
		header.setDisplayOnLastPage(false);

		// is by default true, but it is defined in the xml template, so I add it here too.
		header.addElement( StaticShapeElementFactory.createRectangleShapeElement(
							null, java.awt.Color.decode("#AFAFAF"), null,
							new java.awt.geom.Rectangle2D.Float(0, 0, -100, -100),
							false, true)
		);
	
		final DateFieldElementFactory factory = new DateFieldElementFactory();
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 0));
		factory.setMinimumSize(new FloatDimension(-100, 14));
		factory.setHorizontalAlignment(ElementAlignment.RIGHT);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setNullString("<null>");
		factory.setFormatString("d-MMM-yyyy");
		factory.setFieldname("report.date");
		header.addElement(factory.createElement());

		header.addElement( StaticShapeElementFactory.createLineShapeElement(
							"line1", java.awt.Color.decode("#CFCFCF"),
							new BasicStroke(2), new java.awt.geom.Line2D.Float(0, 16, 0, 16))
		);
		return header;
	}

	/**
	 * Creates the report footer.
	 * @return the report footer.
	 */
	protected ReportFooter createReportFooter()
	{
		final ReportFooter footer = new ReportFooter();
		footer.getStyle().setStyleProperty( ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 48));
		footer.getBandDefaults().setFontDefinitionProperty( new FontDefinition("Serif", 16, true, false, false, false));

		final LabelElementFactory factory = new LabelElementFactory();
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 0));
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
		final ReportHeader header = new ReportHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 58));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 20, true, false, false, false));
	
		LabelElementFactory factory = new LabelElementFactory();
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 0));
		factory.setMinimumSize(new FloatDimension(-100, 24));
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText(data.getTitleString());
		header.addElement(factory.createElement());
				
		factory = new LabelElementFactory();
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 20));
		factory.setMinimumSize(new FloatDimension(-100, 24));
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText(data.getDateRangeString());
		factory.setFontSize(new Integer(12));
		header.addElement(factory.createElement());		

		return header;
	}
}
