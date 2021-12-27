package com.cannontech.analysis.report;

import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.DateFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.ElementVisibilitySwitchFunction;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.FunctionProcessingException;
import org.pentaho.reporting.engine.classic.core.function.PageFunction;
import org.pentaho.reporting.engine.classic.core.function.PageTotalFunction;
import org.pentaho.reporting.engine.classic.core.function.TextFormatExpression;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewInternalFrame;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.common.version.VersionTools;

/**
 * Created on Dec 15, 2003
 * Base class for all Report classes.
 * Contains default expressions and functions:
 * DateExp - requires a String date format and the column name to reference.
 * PageTotal - the total number of pages in the report
 * PageNumber - the current page number
 * PageXofY - uses PageNumber(X) and PageTotal(Y)
 * 
 * Extending classes must implement their own:
 * Grouping rules (createGroups())
 * Item bands (createItemBands())
 * 
 * createReport() returns an instance of the Pentaho MasterReport.
 * This method calls all required getters/creators for the report to be created.
 * PageHeader/PageFooter, ReportHeader/ReportFooter are consistent for all report models.
 * All of the createXXX() functions may be overridden by an extending class.
 * 
 * @author snebben
 */
public abstract class YukonReportBase extends java.awt.event.WindowAdapter {
    /** Flag indicating background coloring added to separate items in band */
    protected boolean showBackgroundColor = false;
    /** Flag indicating report footer is displayed */
    protected boolean showReportHeader = true;
    /** Flag indicating report header is displayed */
    protected boolean showReportFooter = true;
    /** TableModel data structure */
    protected ReportModelBase model = null;
    /** collection of expressions */
    protected ExpressionCollection expressions = null;

    protected String DATE_EXPRESSION = "Date Exp";
    protected String PAGE_TOTAL_FUNCTION = "Page Total";
    protected String PAGE_NUMBER_FUNCTION = "Page Number";
    protected String PAGE_XOFY_EXPRESSION = "PageXofY";

    protected int pageOrientation = PageFormat.LANDSCAPE;
    protected SimplePageDefinition pageDefinition = null;

    public void showPreviewFrame(ReportModelBase model_) throws Exception {
        // initialize Report
        ClassicEngineBoot.getInstance().start();

        model = model_;
        model.collectData();

        MasterReport report = new MasterReport();
        report.setDataFactory(new TableDataFactory("default", model));

        final PreviewDialog dialog = new PreviewDialog(report);
        dialog.setModal(true);
        dialog.pack();
        dialog.setVisible(true);
    }

    public PreviewInternalFrame getPreviewFrame(ReportModelBase model_) throws Exception {
        ClassicEngineBoot.getInstance().start();
        model = model_;
        model.collectData();
        MasterReport report = createReport();
        report.setDataFactory(new TableDataFactory("default", model));
        final PreviewInternalFrame pFrame = new PreviewInternalFrame(report);
        return pFrame;
    }

    public PreviewDialog getPreviewDialog(ReportModelBase model_) throws Exception {
        ClassicEngineBoot.getInstance().start();
        model = model_;
        model.collectData();
        MasterReport report = createReport();
        report.setDataFactory(new TableDataFactory("default", model));
        final PreviewDialog pDialog = new PreviewDialog(report);

        return pDialog;
    }

    /**
     * Creates the report.
     * 
     * @return the constructed report.
     * @throws FunctionProcessingException if there was a problem initializing any of the functions.
     */
    public MasterReport createReport()
            throws org.pentaho.reporting.engine.classic.core.function.FunctionProcessingException {
        ClassicEngineBoot.getInstance().start();
        final MasterReport report = new MasterReport();
        report.setName(getModel().getTitleString());
        if (isShowReportFooter())
            report.setReportFooter(createReportFooter());
        if (isShowReportHeader())
            report.setReportHeader(createReportHeader());
        report.setPageFooter(createPageFooter());
        report.setPageHeader(createPageHeader());
        
        //Add the column heading groups/sub-groups to the report
        List<RelationalGroup> createdGroups = createGroups();
        for (int i = 0; i < createGroups().size(); i++) {
            RelationalGroup rg = createdGroups.get(i);
            GroupBody groupBody = report.getGroup(i).getBody();
            ((GroupDataBody) groupBody).setItemBand(createItemBand());
            report.addGroup(rg);
        }

        report.setExpressions(getExpressions());
        report.setPageDefinition(getPageDefinition());
        report.getReportConfiguration().setConfigProperty(
                "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Encoding", "Identity-H");

        return report;
    }

    /**
     * Extend this method to implement an ItemBand.
     * This is basically the row data.
     * 
     * @return the itemBand
     */
    protected abstract ItemBand createItemBand();

    /**
     * Extend this method to implement report Grouping.
     * These are Groups by which the report
     * will be organized.
     * 
     * @return the createdGroup
     */

    protected abstract List<RelationalGroup> createGroups();

    /**
     * Returns the expressions, use .add(...) for new expressions
     * 
     * @return ExpressionCollection expressions
     * @throws FunctionInitializeException
     */
    protected ExpressionCollection getExpressions() throws FunctionProcessingException {
        expressions = new ExpressionCollection();
        expressions.add(getPageXofYExpression());
        expressions.add(getBackgroundTriggerFunction());
        expressions.add(getPageNumberFunction());
        expressions.add(getPageTotalFunction());
        return expressions;
    }

    /**
     * @return a background element
     */
    protected ElementVisibilitySwitchFunction getBackgroundTriggerFunction() {
        final ElementVisibilitySwitchFunction backgroundTrigger = new ElementVisibilitySwitchFunction();
        backgroundTrigger.setName("backgroundTrigger");
        backgroundTrigger.setElement("background");
        return backgroundTrigger;
    }

    /**
     * Return a PageNumber function.
     * 
     * @return current page number function of a particular page.
     */
    protected PageFunction getPageNumberFunction() {
        final PageFunction pageNumber = new PageFunction();
        pageNumber.setName(PAGE_NUMBER_FUNCTION);
        return pageNumber;
    }

    /**
     * Return a PageTotalNumber function.
     * 
     * @return total of all pages in report.
     */
    protected PageTotalFunction getPageTotalFunction() {
        final PageTotalFunction pageTotalFunction = new PageTotalFunction();
        pageTotalFunction.setName(PAGE_TOTAL_FUNCTION);
        return pageTotalFunction;
    }

    /**
     * Return a TextFormatExprssion for pageNumber of pageCount.
     * 
     * @return String in format "Page x of y"
     */
    protected TextFormatExpression getPageXofYExpression() {
        final TextFormatExpression pageXofYExp = new TextFormatExpression();
        pageXofYExp.setName(PAGE_XOFY_EXPRESSION);
        pageXofYExp.setPattern("Page {0} of {1}");
        pageXofYExp.setField(0, PAGE_NUMBER_FUNCTION);
        pageXofYExp.setField(1, PAGE_TOTAL_FUNCTION);
        return pageXofYExp;
    }

    /**
     * Creates a page footer.
     * 
     * @return The page footer.
     */
    protected PageFooter createPageFooter() {
        final PageFooter pageFooter = new PageFooter();
        pageFooter.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 18f);
        pageFooter.getStyle().getStyleProperty(TextStyleKeys.FONT, "Calibri");
        pageFooter.getStyle().getIntStyleProperty(TextStyleKeys.FONTSIZE, 10);

        /** A rectangle around the page footer */
        pageFooter.addElement(ReportFactory.createBasicLine(0.5f, 6));
        /** A label for Cannon Technologies in the footer object */
        /*
         * final LabelElementFactory lfactory = new LabelElementFactory();
         * lfactory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 12));
         * lfactory.setMinimumSize(new FloatDimension(-100, 0));
         * lfactory.setHorizontalAlignment(ElementAlignment.LEFT);
         * lfactory.setVerticalAlignment(ElementAlignment.MIDDLE);
         * lfactory.setText("Cannon Technologies");
         * lfactory.setDynamicHeight(Boolean.TRUE);
         * pageFooter.addElement(lfactory.createElement());
         */

        final DateFieldElementFactory factory = new DateFieldElementFactory();
        factory.setAbsolutePosition(new Point2D.Float(0, 8));
        factory.setMinimumSize(new FloatDimension(-100, 0));
        factory.setDynamicHeight(new Boolean(true));
        factory.setHorizontalAlignment(ElementAlignment.RIGHT);
        factory.setVerticalAlignment(ElementAlignment.BOTTOM);
        factory.setNullString("<null>");
        factory.setFormatString("d-MMM-yyyy HH:mm:ss z");
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
     * 
     * @return the page header.
     */
    protected PageHeader createPageHeader() {
        final PageHeader header = new PageHeader();
        header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 18f);
        header.setDisplayOnFirstPage(true);
        header.setDisplayOnLastPage(false);
        return header;
    }

    /**
     * Creates the report footer.
     * 
     * @return the report footer.
     */
    protected ReportFooter createReportFooter() {
        ReportFooter footer = ReportFactory.createReportFooterDefault();

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
     * 
     * @return the report header.
     */
    protected ReportHeader createReportHeader() {
        ReportHeader header = ReportFactory.createReportHeaderDefault();

        LabelElementFactory factory = new LabelElementFactory();
        factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 0));
        factory.setMinimumSize(new FloatDimension(-100, 24));
        factory.setHorizontalAlignment(ElementAlignment.CENTER);
        factory.setVerticalAlignment(ElementAlignment.MIDDLE);
        factory.setText(getModel().getTitleString());
        factory.setFontSize(20);
        factory.setBold(true);
        factory.setItalic(true);
        header.addElement(factory.createElement());

        factory = new LabelElementFactory();
        factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, 20));
        factory.setMinimumSize(new FloatDimension(-100, 24));
        factory.setHorizontalAlignment(ElementAlignment.CENTER);
        factory.setVerticalAlignment(ElementAlignment.MIDDLE);
        factory.setText(getDateRangeString());
        factory.setFontSize(12);
        header.addElement(factory.createElement());

        return header;
    }

    protected String getDateRangeString() {
        return getModel().getDateRangeString();
    }

    /**
     * @return
     */
    public ReportModelBase getModel() {
        return model;
    }

    /**
     * @param base
     */
    public void setModel(ReportModelBase base) {
        model = base;
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

    /**
     * @return Returns the pageDefinition.
     */
    public SimplePageDefinition getPageDefinition() {
        if (pageDefinition == null) {
            java.awt.print.Paper reportPaper = new java.awt.print.Paper();
            reportPaper.setImageableArea(30, 30, 552, 732); // 8.5 x 11 -> 612w 792h
            PageFormat pageFormat = new java.awt.print.PageFormat();
            pageFormat.setOrientation(getPageOrientation());
            pageFormat.setPaper(reportPaper);

            pageDefinition = new SimplePageDefinition(pageFormat);
        }
        return pageDefinition;
    }

    /**
     * @return
     */
    public int getPageOrientation() {
        return pageOrientation;
    }

    /**
     * @param format
     */
    public void setPageOrientation(int pageOrientation_) {
        this.pageOrientation = pageOrientation_;
        pageDefinition = null; // force a new one to be created with the new pageOrientation
    }

    public LabelElementFactory getVersionLabel() {
        LabelElementFactory factory = new LabelElementFactory();
        factory.setAbsolutePosition(new Point2D.Float(0, 8));
        factory.setMinimumSize(new FloatDimension(-100, 0));
        factory.setHorizontalAlignment(ElementAlignment.LEFT);
        factory.setVerticalAlignment(ElementAlignment.BOTTOM);
        factory.setText("V." + VersionTools.getYUKON_VERSION() + "  DB." +
                VersionTools.getDatabaseVersion().getVersion() + "." +
                VersionTools.getDatabaseVersion().getBuild());
        factory.setDynamicHeight(Boolean.TRUE);
        return factory;
    }

    public boolean supportsPdf() {
        return true;
    }
}