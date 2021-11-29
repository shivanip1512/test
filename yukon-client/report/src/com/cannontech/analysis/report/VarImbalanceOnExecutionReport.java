package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.VarImbalanceOnExecutionModel;
import com.cannontech.spring.YukonSpringHook;

public class VarImbalanceOnExecutionReport extends SimpleYukonReportBase {

    private static final ColumnLayoutData SUBSTATIONBUS_COLUMN = new ColumnLayoutData("Substation Bus", "substationbus", 200);
    private static final ColumnLayoutData SUBSTATION_COLUMN = new ColumnLayoutData("Substation", "substation", 200);
    private static final ColumnLayoutData AREA_COLUMN = new ColumnLayoutData("Area", "area", 200);
    private static final ColumnLayoutData[] bodyColumns = new ColumnLayoutData[] {
            AREA_COLUMN,
            SUBSTATION_COLUMN,
            SUBSTATIONBUS_COLUMN,
            new ColumnLayoutData("Feeder", "feeder", 135),
            new ColumnLayoutData("Cap Bank", "capbank", 90),
            new ColumnLayoutData("Cap Bank State", "capbankstate", 90),
            new ColumnLayoutData("Operation Time", "opTime", 105, columnDateFormat),
            new ColumnLayoutData("Operation", "operation", 195),
            new ColumnLayoutData("A Var", "avar", 45),
            new ColumnLayoutData("B Var", "bvar", 45),
            new ColumnLayoutData("C Var", "cvar", 45)
    };

    private RelationalGroup createHeaderGroup() {
        final RelationalGroup headerGroup = new RelationalGroup();
        headerGroup.setName("header" + ReportFactory.NAME_GROUP);
        headerGroup.addField("area");
        headerGroup.addField("substation");
        headerGroup.addField("substationbus");

        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        List<ColumnLayoutData> columnList = getBodyColumns();
        ColumnLayoutData cldArea = columnList.get(VarImbalanceOnExecutionModel.AREA_NAME_COLUMN);
        Point2D point2DArea = columnProperties.get(cldArea);

        LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(cldArea.getColumnName(), 1.0f, 1.0f,
                cldArea.getWidth());
        factory.setText("Area: ");
        header.addElement(factory.createElement());

        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(point2DArea, cldArea);
        tfactory.setAbsolutePosition(new Point2D.Float(80, 1)); // override posX
        factory.setMinimumSize(new FloatDimension(300, 18));
        header.addElement(tfactory.createElement());

        ColumnLayoutData cldSubstation = columnList.get(VarImbalanceOnExecutionModel.SUBSTATION_NAME_COLUMN);
        Point2D point2DSubStation = columnProperties.get(cldSubstation);

        factory = ReportFactory.createGroupLabelElementDefault(cldSubstation.getColumnName(), 80.0f, 18.0f,
                cldSubstation.getWidth());
        factory.setText("Substation: ");
        factory.setAbsolutePosition(new Point2D.Float(0, 16)); // override location
        header.addElement(factory.createElement());

        tfactory = ReportFactory.createGroupTextFieldElementDefault(point2DSubStation, cldSubstation);
        tfactory.setAbsolutePosition(new Point2D.Float(80, 16)); // override posX
        header.addElement(tfactory.createElement());

        ColumnLayoutData cldSubstationBus = columnList.get(VarImbalanceOnExecutionModel.SUBSTATIONBUS_NAME_COLUMN);
        Point2D point2DSubStationBus = columnProperties.get(cldSubstationBus);

        factory = ReportFactory.createGroupLabelElementDefault(cldSubstationBus.getColumnName(), 80.0f, 18.0f,
                cldSubstationBus.getWidth());
        factory.setText("Substation Bus: ");
        factory.setAbsolutePosition(new Point2D.Float(0, 28)); // override location
        header.addElement(factory.createElement());

        tfactory = ReportFactory.createGroupTextFieldElementDefault(point2DSubStationBus, cldSubstationBus);
        tfactory.setAbsolutePosition(new Point2D.Float(80, 28)); // override posX
        header.addElement(tfactory.createElement());

        header.addElement(ReportFactory.createBasicLine(0.5f, 44));

        for (int i = VarImbalanceOnExecutionModel.FEEDER_NAME_COLUMN; i < bodyColumns.length; i++) {
            ColumnLayoutData columnLayoutData = getBodyColumns().get(i);
            Point2D point2D = columnProperties.get(columnLayoutData);
            factory = ReportFactory.createGroupLabelElementDefault(columnLayoutData.getColumnName(),
                    new Double(point2D.getX()).floatValue(), new Double(point2D.getY()).floatValue(),
                    columnLayoutData.getWidth());
            factory.setAbsolutePosition(new Point2D.Float(new Double(point2D.getX()).floatValue(), 44)); // override location,
                                                                                                         // need to be lower
            header.addElement(factory.createElement());
        }
        headerGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        headerGroup.setFooter(footer);

        return headerGroup;
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

        for (int i = VarImbalanceOnExecutionModel.FEEDER_NAME_COLUMN; i < bodyColumns.length; i++) {
            ColumnLayoutData cld = bodyColumns[i];
            Point2D pt2D = columnProperties.get(cld);
            TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(pt2D, cld);
            items.addElement(factory.createElement());
        }

        return items;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */
    protected List<RelationalGroup> createGroups() {
        return List.of(createHeaderGroup());
    }

    protected void initializeColumns() {
        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns().iterator();
        int accumulativeWidth = 0;
        while (bodyColumns.hasNext()) {
            ColumnLayoutData data = bodyColumns.next();
            if (data.equals(AREA_COLUMN) || data.equals(SUBSTATION_COLUMN) || data.equals(SUBSTATIONBUS_COLUMN))
                continue;
            int width = data.getWidth();
            Float position = new Point2D.Float(accumulativeWidth, 1);
            columnProperties.put(data, position);
            accumulativeWidth += width + getExtraFieldSpacing();
        }

        buildColumnIndexLookup();
    }

    private void buildColumnIndexLookup() {
        int columnCount = getModel().getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            String columnName = getModel().getColumnName(i);
            columIndexLookup.put(columnName, i);
        }
    }

    public VarImbalanceOnExecutionReport(BareReportModel bareModel) {
        super(bareModel);
    }

    public VarImbalanceOnExecutionReport() {
        this((VarImbalanceOnExecutionModel) YukonSpringHook.getBean("varImbalanceOnExecutionModel"));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}