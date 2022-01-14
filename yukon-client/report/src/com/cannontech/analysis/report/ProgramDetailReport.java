package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Date;
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
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.function.ElementVisibilityEvalFunction;
import com.cannontech.analysis.tablemodel.ProgramDetailModel;
import com.cannontech.util.ServletUtil;

/**
 * Created on Dec 15, 2003
 * Creates a ProgramDetailReport using the com.cannontech.analysis.tablemodel.ProgramDetailModel tableModel
 * Groups data by Date. Orders asc/desc based on tableModel definition.
 * 
 * @author snebben
 */
public class ProgramDetailReport extends YukonReportBase {
    private boolean showNotEnrolled = false;

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf SystemLogModel.
     */
    public ProgramDetailReport() {
        this(new ProgramDetailModel());
    }

    public ProgramDetailReport(boolean showNotEnrolled_) {
        this(new ProgramDetailModel(), showNotEnrolled_);
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf SystemLogModel.
     * 
     * @param data_ - SystemLogModel TableModel data
     */
    public ProgramDetailReport(ProgramDetailModel model_) {
        this(model_, false);
    }

    public ProgramDetailReport(ProgramDetailModel model_, boolean showNotEnrolled_) {
        super();
        setModel(model_);
        setShowNotEnrolled(showNotEnrolled_);
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf ProgramDetailModel.
     * 
     * @param stopTime_ - stopTime in millis for data query
     * 
     */
    public ProgramDetailReport(Date stop_) {
        this(new ProgramDetailModel(stop_));

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

        // Define default start and stop parameters for a default year to date report.
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        cal.add(java.util.Calendar.DATE, 1); // default stop date is tomorrow
        Date stop = cal.getTime();

        // Define start and stop parameters for a default 90 day report.
        ProgramDetailModel model = new ProgramDetailModel(stop);
        boolean showNotEnrolled = false;
        for (int i = 0; i < args.length; i++) {
            String arg = (String) args[i].toLowerCase();

            int startIndex = arg.indexOf('=');
            startIndex += 1;
            String subString = arg.substring(startIndex);

            if (arg.startsWith("ec"))
                model.setEnergyCompanyID(Integer.valueOf(subString));
            else if (arg.startsWith("stop")) {
                Date stopDate = ServletUtil.parseDateStringLiberally(subString);
                model.setStopDate(stopDate);
            } else if (arg.startsWith("notEnroll")) // when true, show those "Not Enrolled"
            {
                showNotEnrolled = Boolean.valueOf(subString).booleanValue();
            }
        }

        YukonReportBase report = new ProgramDetailReport(model, showNotEnrolled);
        report.getModel().collectData();

        // Create the report
        MasterReport freeReport = report.createReport();
        freeReport.setDataFactory(new TableDataFactory("default", report.getModel()));

        final PreviewDialog dialog = new PreviewDialog(freeReport);
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

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.analysis.report.YukonReportBase#getExpressions()
     */
    protected ExpressionCollection getExpressions() throws FunctionProcessingException {
        super.getExpressions();
        ItemHideFunction hideItem = new ItemHideFunction();
        hideItem.setName(ProgramDetailModel.CONTACT_STRING + ReportFactory.NAME_HIDDEN);
        hideItem.setField(ProgramDetailModel.CONTACT_STRING);
        hideItem.setElement(ProgramDetailModel.CONTACT_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        hideItem = new ItemHideFunction();
        hideItem.setName(ProgramDetailModel.ACCOUNT_NUMBER_STRING + ReportFactory.NAME_HIDDEN);
        hideItem.setField(ProgramDetailModel.ACCOUNT_NUMBER_STRING);
        hideItem.setElement(ProgramDetailModel.ACCOUNT_NUMBER_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        hideItem = new ItemHideFunction();
        hideItem.setName(ProgramDetailModel.PROGRAM_NAME_STRING + ReportFactory.NAME_HIDDEN);
        hideItem.setField(ProgramDetailModel.PROGRAM_NAME_STRING);
        hideItem.setElement(ProgramDetailModel.PROGRAM_NAME_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        // We negate the showNotEnrolled flag because if true, then we DO NOT want to hide the item.
        ElementVisibilityEvalFunction action = new ElementVisibilityEvalFunction(ProgramDetailModel.STATUS_COLUMN, "Not Enrolled",
                !isShowNotEnrolled());
        action.setName("Hide Not Enrolled String");
        action.setElement(ProgramDetailModel.STATUS_STRING + " Element");
        expressions.add(action);

        return expressions;
    }

    /**
     * Create a Group for EnergyCompany column.
     * 
     * @return
     */
    private RelationalGroup createECGroup() {
        final RelationalGroup ecGroup = new RelationalGroup();
        ecGroup.setName(ProgramDetailModel.ENERGY_COMPANY_STRING + ReportFactory.NAME_GROUP);
        ecGroup.addField(getModel().getColumnName(ProgramDetailModel.ENERGY_COMPANY_COLUMN));

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(),
                ProgramDetailModel.ENERGY_COMPANY_COLUMN);
        header.addElement(tfactory.createElement());

        header.addElement(ReportFactory.createBasicLine(0.5f, 20));

        // Add all columns (excluding Date) to the table model.
        for (int i = 1; i < getModel().getColumnNames().length; i++) {
            LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
            factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18)); // posY must be
                                                                                                                  // below
                                                                                                                  // energy_company
                                                                                                                  // textfield
                                                                                                                  // (size 18)
            header.addElement(factory.createElement());
        }
        ecGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        ecGroup.setFooter(footer);

        return ecGroup;
    }

    /**
     * Create a Group for Program column.
     * 
     * @return
     */
    private RelationalGroup createProgramGroup() {
        final RelationalGroup progGroup = new RelationalGroup();
        progGroup.setName(ProgramDetailModel.PROGRAM_NAME_STRING + ReportFactory.NAME_GROUP);
        progGroup.addField(getModel().getColumnName(ProgramDetailModel.ENERGY_COMPANY_COLUMN));
        progGroup.addField(getModel().getColumnName(ProgramDetailModel.PROGRAM_NAME_COLUMN));

        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 5f); // override size
        progGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        footer.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 5f); // override the size
        progGroup.setFooter(footer);

        return progGroup;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */

    protected List<RelationalGroup> createGroups() {
        return List.of(createECGroup(), createProgramGroup());
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
        // Start at 1, we don't want to include the EnergyCompany column, it's our group by column.
        for (int i = 1; i < getModel().getColumnNames().length; i++) {
            TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            items.addElement(factory.createElement());
        }
        return items;
    }

    /**
     * @return
     */
    public boolean isShowNotEnrolled() {
        return showNotEnrolled;
    }

    /**
     * @param b
     */
    public void setShowNotEnrolled(boolean b) {
        showNotEnrolled = b;
    }

    @Override
    public boolean supportsPdf() {
        return false;
    }
}