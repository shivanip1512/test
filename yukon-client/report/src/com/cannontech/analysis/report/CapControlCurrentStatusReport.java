package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
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
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.FunctionProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ItemHideFunction;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.CapControlCurrentStatusModel;

/**
 * Created on Dec 15, 2003
 * Creates a DatabaseReport using the com.cannontech.analysis.data.DatabaseModel tableModel
 * No Group by used, only by column headings.
 * 
 * @author snebben
 */
public class CapControlCurrentStatusReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf CapControlStatusModel.
     */
    public CapControlCurrentStatusReport() {
        this(new CapControlCurrentStatusModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf CapControlStatusModel.
     * 
     * @param data_ - CapControlStatusModel TableModel data
     */
    public CapControlCurrentStatusReport(CapControlCurrentStatusModel model_) {
        super();
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

        CapControlCurrentStatusModel reportModel = new CapControlCurrentStatusModel();
        YukonReportBase dbReport = new CapControlCurrentStatusReport(reportModel);
        dbReport.getModel().collectData();

        // Create the report
        MasterReport report = dbReport.createReport();
        report.setDataFactory(new TableDataFactory("CapControlCurrentStatus", dbReport.getModel()));

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
     * 
     * @return Group
     */
    private RelationalGroup createColumnHeadingGroup() {
        final RelationalGroup collHdgGroup = new RelationalGroup();
        collHdgGroup.setName("Column Heading");

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        LabelElementFactory factory;
        for (int i = 0; i < getModel().getColumnNames().length; i++) {
            factory = ReportFactory.createGroupLabelElementDefault(model, i);
            header.addElement(factory.createElement());
        }

        header.addElement(HorizontalLineElementFactory.createHorizontalLine(22, null, new BasicStroke(0.5f)));
        collHdgGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        collHdgGroup.setFooter(footer);

        return collHdgGroup;
    }

    /**
     * Create a GroupList
     * 
     * @return the grouplist
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
            items.addElement(factory.createElement());
        }

        return items;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.analysis.report.YukonReportBase#getExpressions()
     */
    protected ExpressionCollection getExpressions() throws FunctionProcessingException {
        super.getExpressions();
        ItemHideFunction hideItem = new ItemHideFunction();
        hideItem.setName(CapControlCurrentStatusModel.SUB_BUS_NAME_STRING + ReportFactory.NAME_HIDDEN);
        hideItem.setField(CapControlCurrentStatusModel.SUB_BUS_NAME_STRING);
        hideItem.setElement(CapControlCurrentStatusModel.SUB_BUS_NAME_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        hideItem = new ItemHideFunction();
        hideItem.setName(CapControlCurrentStatusModel.FEEDER_NAME_STRING + ReportFactory.NAME_HIDDEN);
        hideItem.setField(CapControlCurrentStatusModel.FEEDER_NAME_STRING);
        hideItem.setElement(CapControlCurrentStatusModel.FEEDER_NAME_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        return expressions;
    }
}
