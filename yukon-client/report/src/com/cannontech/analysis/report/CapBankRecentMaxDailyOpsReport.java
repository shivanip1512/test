package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.CapBankRecentMaxDailyOpsModel;

public class CapBankRecentMaxDailyOpsReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf DatabaseModel.
     */
    public CapBankRecentMaxDailyOpsReport() {
        this(new CapBankRecentMaxDailyOpsModel());
    }

    public CapBankRecentMaxDailyOpsReport(CapBankRecentMaxDailyOpsModel model) {
        super();
        setModel(model);
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

        TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), 0);
        items.addElement(factory.createElement());

        for (int i = 0; i < getModel().getColumnNames().length; i++) {
            factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            items.addElement(factory.createElement());
        }

        return items;
    }
}
