package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
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
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.RouteDBModel;

/**
 * Created on Dec 15, 2003
 * Creates a DatabaseReport using the com.cannontech.analysis.data.DatabaseModel tableModel
 * No Group by used, only by column headings.
 * 
 * @author snebben
 */
public class RouteDBReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf DatabaseModel.
     */
    public RouteDBReport() {
        this(new RouteDBModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf DatabaseModel.
     * 
     * @param data_ - DatabaseModel TableModel data
     */
    public RouteDBReport(RouteDBModel model_) {
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

        RouteDBModel reportModel = new RouteDBModel();
        YukonReportBase dbReport = new RouteDBReport(reportModel);
        dbReport.getModel().collectData();

        // Create the report
        MasterReport report = dbReport.createReport();
        report.setDataFactory(new TableDataFactory("default", dbReport.getModel()));

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
     * Create a Group for Device.
     * 
     * @return Group
     */
    private RelationalGroup createRouteGroup() {
        final RelationalGroup routeGroup = new RelationalGroup();
        routeGroup.setName(RouteDBModel.ROUTEDB_ROUTE_NAME_STRING + ReportFactory.NAME_GROUP);
        routeGroup.addField(RouteDBModel.ROUTEDB_ROUTE_NAME_STRING);

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(),
                RouteDBModel.ROUTEDB_ROUTE_NAME_COLUMN);
        header.addElement(factory.createElement());

        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(),
                RouteDBModel.ROUTEDB_ROUTE_NAME_COLUMN);
        tfactory.setAbsolutePosition(new Point2D.Float(130, 1)); // override posX
        factory.setMinimumSize(new FloatDimension(300, 18));
        header.addElement(tfactory.createElement());

        header.addElement(ReportFactory.createBasicLine(0.5f, 20));

        for (int i = 1; i < getModel().getColumnNames().length; i++) {
            factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
            factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18)); // override
                                                                                                                  // location,
                                                                                                                  // need to be
                                                                                                                  // lower than
                                                                                                                  // macroroute
                                                                                                                  // text
            header.addElement(factory.createElement());
        }
        routeGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        routeGroup.setFooter(footer);

        return routeGroup;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */

    protected List<RelationalGroup> createGroups() {
        return List.of(createRouteGroup());
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
        for (int i = 1; i < getModel().getColumnNames().length; i++) {
            factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            items.addElement(factory.createElement());
        }

        return items;
    }

}
