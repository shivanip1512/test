package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.HECO_CustomerMonthlyBillingSettlementModel;

/**
 * Created on Feb 06, 2003
 * Creates a DisconnectReport using the com.cannontech.analysis.data.DisconnectData tableModel
 * Groups data by Collection Group and then by Device.
 * 
 * @author snebben
 * @author bjonasson
 */
public class HECO_CustomerMonthlyBillingSettlementReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf LoadControlEventSummaryModel.
     */
    public HECO_CustomerMonthlyBillingSettlementReport() {
        this(new HECO_CustomerMonthlyBillingSettlementModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf LoadControlEventSummaryModel.
     * @param data_ - DatabaseModel TableModel data
     */
    public HECO_CustomerMonthlyBillingSettlementReport(HECO_CustomerMonthlyBillingSettlementModel model_) {
        super();
        setModel(model_);
    }

    /**
     * Runs this report and shows a preview dialog.
     * @param args the arguments (ignored).
     * @throws Exception if an error occurs (default: print a stack trace)
     */
    public static void main(final String[] args) throws Exception {
        // initialize Report
        ClassicEngineBoot.getInstance().start();
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

        HECO_CustomerMonthlyBillingSettlementModel model = new HECO_CustomerMonthlyBillingSettlementModel();

        // start and stop time are only valid when model.showHist is false
        GregorianCalendar cal = new GregorianCalendar();
        model.setStopDate(cal.getTime());

        cal.set(Calendar.MONTH, Calendar.NOVEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 13);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        model.setStartDate(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, 15);
        model.setStopDate(cal.getTime());
        YukonReportBase lcSummaryReport = new HECO_CustomerMonthlyBillingSettlementReport(model);
        lcSummaryReport.getModel().collectData();
        // Create the report
        MasterReport report = lcSummaryReport.createReport();
        report.setDataFactory(new TableDataFactory("default", lcSummaryReport.getModel()));

        final PreviewDialog dialog = new PreviewDialog(report);
        // Add a window closing event, even though I think it's already handled by setDefaultCloseOperation(..)
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
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
    private RelationalGroup createCustomerHeadingGroup() {
        final RelationalGroup custHdgGroup = new RelationalGroup();
        custHdgGroup.setName(((HECO_CustomerMonthlyBillingSettlementModel) getModel())
                .getColumnName(HECO_CustomerMonthlyBillingSettlementModel.CUSTOMER_NAME_DATA) + ReportFactory.NAME_GROUP);
        custHdgGroup.addField(((HECO_CustomerMonthlyBillingSettlementModel) getModel())
                .getColumnName(HECO_CustomerMonthlyBillingSettlementModel.CUSTOMER_NAME_DATA));

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        // Use reportFactory's regular elements, not the group ones.
        LabelElementFactory factory;

        TextFieldElementFactory tfactory;
        for (int i = HECO_CustomerMonthlyBillingSettlementModel.CUSTOMER_NAME_DATA; i <= HECO_CustomerMonthlyBillingSettlementModel.EVENT_DURATION_HEADING_DATA; i++) {
            factory = ReportFactory.createLabelElementDefault(getModel(), i);
            factory.setText(factory.getText() + ":");
            if (i < HECO_CustomerMonthlyBillingSettlementModel.LOAD_CONTROL_EVENT_SUMMARY_DATA)
                factory.setHorizontalAlignment(ElementAlignment.RIGHT);
            header.addElement(factory.createElement());

            if (i < HECO_CustomerMonthlyBillingSettlementModel.EVENT_TYPE_HEADING_DATA) {
                header.addElement(
                        HorizontalLineElementFactory.createHorizontalLine(getModel().getColumnProperties(i + 1).getPositionY(),
                                java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f)));

                tfactory = ReportFactory.createTextFieldElementDefault(getModel(), i);
                tfactory.setHorizontalAlignment(ElementAlignment.RIGHT);
                tfactory.setNullString("");
                tfactory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX() + 175,
                        getModel().getColumnProperties(i).getPositionY()));
                if (i != HECO_CustomerMonthlyBillingSettlementModel.LOAD_CONTROL_EVENT_SUMMARY_DATA)
                    tfactory.setBold(Boolean.FALSE);

                header.addElement(tfactory.createElement());
            }
        }
        custHdgGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        footer.setPagebreakAfterPrint(true);
        custHdgGroup.setFooter(footer);

        return custHdgGroup;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * @return the groupList.
     */
    protected List<RelationalGroup> createGroups() {
        return List.of(createCustomerHeadingGroup());
    }

    /**
     * Creates the itemBand, the rows of data.
     * @return the item band.
     */
    protected ItemBand createItemBand() {
        ItemBand items = ReportFactory.createItemBandDefault();
        items.addElement(
                HorizontalLineElementFactory.createHorizontalLine(0, java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f)));

        for (int i = HECO_CustomerMonthlyBillingSettlementModel.EVENT_TYPE_HEADING_DATA; i <= HECO_CustomerMonthlyBillingSettlementModel.EVENT_DURATION_HEADING_DATA; i++) {
            TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 1));
            items.addElement(factory.createElement());
        }
        return items;
    }

    /**
     * Add the same elements as those from the report header so they show on every page.
     * Creates the page header.
     * @return the page header.
     */
    protected PageHeader createPageHeader() {
        final PageHeader header = super.createPageHeader();
        header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 48f);
        header.getStyle().getStyleProperty(TextStyleKeys.FONT, ReportFactory.REPORT_HEADER_BAND_FONT);

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
        factory.setFontSize(12);
        header.addElement(factory.createElement());
        header.setDisplayOnFirstPage(false);
        header.setDisplayOnLastPage(false);
        return header;
    }

    protected PageFooter createPageFooter() {
        PageFooter pageFooter = super.createPageFooter();
        pageFooter.addElement(getVersionLabel().createElement());
        return pageFooter;
    }
}
