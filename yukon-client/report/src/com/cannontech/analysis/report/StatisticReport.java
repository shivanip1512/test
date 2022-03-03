package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.print.PageFormat;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.StatisticModel;

/**
 * Created on Dec 15, 2003
 * Creates a StatisticReport using the com.cannontech.analysis.data.statistic.* tableModel(s)
 * Groups data by column headings only.
 * See Constructor for options to create different types of statistic reports.
 * 
 * @author snebben
 */
public class StatisticReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf SystemLogModel.
     */
    public StatisticReport() {
        // We must default the model to something!
        this(new StatisticModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf SystemLogModel.
     * 
     * @param data_ - SystemLogModel TableModel data
     */
    private StatisticReport(StatisticModel model_) {
        super();
        setPageOrientation(PageFormat.PORTRAIT);
        setModel(model_);
    }

    /**
     * Runs this report and shows a preview dialog.
     *
     * @param args the arguments (ignored).
     * @throws Exception if an error occurs (default: print a stack trace)
     */
    public static void main(final String[] args) throws Exception {
        // initialize Report
        ClassicEngineBoot.getInstance().start();
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

        String modelType = "carrier"; // default
        if (args.length > 0)
            modelType = args[0].toLowerCase(); // StatisticData report type
        String statPeriodType = StatisticModel.MONTHLY_STAT_PERIOD_TYPE_STRING;
        if (args.length >= 2) // DynamicPaoStatistics.statisticType
            statPeriodType = args[1];

        int statType = -1;
        if (modelType.startsWith("trans"))
            statType = StatisticModel.STAT_TRANS_COMM_DATA;
        else if (modelType.startsWith("dev"))
            statType = StatisticModel.STAT_DEVICE_COMM_DATA;
        else if (modelType.startsWith("comm"))
            statType = StatisticModel.STAT_COMM_CHANNEL_DATA;
        else // if( modelTypeString.startsWith("carr"))
            statType = StatisticModel.STAT_CARRIER_COMM_DATA;

        StatisticModel model = new StatisticModel(statPeriodType, statType);
        YukonReportBase statReport = new StatisticReport(model);
        statReport.getModel().collectData();

        // Create the report
        MasterReport report = statReport.createReport();
        report.setDataFactory(new TableDataFactory("default", statReport.getModel()));

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
     * Create a Group for <nothing> actually. Only column headings.
     * 
     * @return
     */
    private RelationalGroup createColumnHeadingGroup() {
        final RelationalGroup collHdgGroup = new RelationalGroup();
        collHdgGroup.setName("Column Heading" + ReportFactory.NAME_GROUP);

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        LabelElementFactory factory;
        for (int i = 0; i < getModel().getColumnNames().length; i++) {
            factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
            if (i > 1) // all but paoName and paoType
                factory.setHorizontalAlignment(ElementAlignment.RIGHT);
            header.addElement(factory.createElement());
        }

        header.addElement(ReportFactory.createBasicLine(0.5f, 20));
        collHdgGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        collHdgGroup.setFooter(footer);

        return collHdgGroup;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */
    protected List<RelationalGroup> createGroups() {
        return List.of(createColumnHeadingGroup());
    }

    /**
     * Creates the itemBand, the rows of data.
     * 
     * @return the item band.
     */
    protected ItemBand createItemBand() {
        ItemBand items = ReportFactory.createItemBandDefault();

        if (showBackgroundColor) {
            items.addElement(RectangleElementFactory.createFilledRectangle(0, 0, -100, -100, java.awt.Color.decode("#DFDFDF")));
            items.addElement(HorizontalLineElementFactory.createHorizontalLine(0, java.awt.Color.decode("#DFDFDF"),
                    new BasicStroke(0.1f)));
            items.addElement(HorizontalLineElementFactory.createHorizontalLine(10, java.awt.Color.decode("#DFDFDF"),
                    new BasicStroke(0.1f)));
        }

        TextFieldElementFactory factory;
        for (int i = 0; i < getModel().getColumnNames().length; i++) {
            factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            if (i > 1) // all but paoName, paoType
                factory.setHorizontalAlignment(ElementAlignment.RIGHT);
            items.addElement(factory.createElement());
        }

        return items;
    }
}
