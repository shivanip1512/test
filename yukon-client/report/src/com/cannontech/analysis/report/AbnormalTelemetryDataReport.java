package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Float;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.AbnormalTelemetryDataModel;
import com.cannontech.analysis.tablemodel.BareReportModel;

public class AbnormalTelemetryDataReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData SUBSTATION_COLUMN = new ColumnLayoutData("Substation Bus", "substationBus", 200);
    private static final ColumnLayoutData FEEDER_COLUMN = new ColumnLayoutData("Feeder", "feederName", 200);
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        SUBSTATION_COLUMN,
        new ColumnLayoutData("Substation Bus Var Point", "subVarPoint", 65),
        new ColumnLayoutData("Quality", "subVarQuality", 60),
        new ColumnLayoutData("Substation Bus Volt Point", "subVoltPoint", 65),
        new ColumnLayoutData("Quality", "subVoltQuality", 60),
        new ColumnLayoutData("Substation Bus Watt Point", "subWattPoint", 70),
        new ColumnLayoutData("Quality", "subWattQuality", 60),
        FEEDER_COLUMN,
        new ColumnLayoutData("Var Point Feeder", "varPoint", 60),
        new ColumnLayoutData("Quality", "fdrVarQuality", 60),
        new ColumnLayoutData("Volt Point Feeder", "voltPoint", 60),
        new ColumnLayoutData("Quality", "fdrVoltQuality", 60),
        new ColumnLayoutData("Watt Point Feeder", "wattPoint", 60),
        new ColumnLayoutData("Quality", "fdrWattQuality", 60)
    };

    public AbnormalTelemetryDataReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public AbnormalTelemetryDataReport() {
        this(new AbnormalTelemetryDataModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
    private Group createSubFeederGroup()
    {
        final Group subFeederGroup = new Group();
        subFeederGroup.setName("substation Bus"+ ReportFactory.NAME_GROUP);
        subFeederGroup.addField("substationBus");
        subFeederGroup.addField("feederName");
        
        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        List<ColumnLayoutData> columnList = getBodyColumns();
        ColumnLayoutData cldSub = columnList.get(AbnormalTelemetryDataModel.SUB_NAME_COLUMN);
        Point2D point2DSub = columnProperties.get(cldSub);
        
        LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(cldSub.getColumnName(), 1.0f, 1.0f, cldSub.getWidth());
        factory.setText("Substation: ");
        header.addElement(factory.createElement());
        
        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(point2DSub, cldSub);
        tfactory.setAbsolutePosition(new Point2D.Float(80, 1)); //override posX
        factory.setMinimumSize(new FloatDimension( 300, 18));
        header.addElement(tfactory.createElement());
        
        ColumnLayoutData cldFeeder = columnList.get(AbnormalTelemetryDataModel.FEEDER_NAME_COLUMN);
        Point2D point2DFeeder = columnProperties.get(cldFeeder);
        
        factory = ReportFactory.createGroupLabelElementDefault(cldFeeder.getColumnName(), 80.0f, 18.0f, cldFeeder.getWidth());
        factory.setText("Feeder: ");
        factory.setAbsolutePosition(new Point2D.Float(0, 16) ); //override location, need to be lower than macroroute text      
        header.addElement(factory.createElement());
        
        tfactory = ReportFactory.createGroupTextFieldElementDefault(point2DFeeder, cldFeeder);
        tfactory.setAbsolutePosition(new Point2D.Float(80, 16));    //override posX
        header.addElement(tfactory.createElement());

        header.addElement(ReportFactory.createBasicLine("rmGroupLine", 0.5f, 34));
        
        for (int i = AbnormalTelemetryDataModel.SUB_VAR_COLUMN; i < bodyColumns.length; i++) {
            if( i == AbnormalTelemetryDataModel.FEEDER_NAME_COLUMN) continue; // skip the feeder column
            ColumnLayoutData columnLayoutData = getBodyColumns().get(i);
            Point2D point2D = columnProperties.get(columnLayoutData);
            
            factory = ReportFactory.createGroupLabelElementDefault(columnLayoutData.getColumnName(), new Double(point2D.getX()).floatValue(), new Double(point2D.getY()).floatValue(), columnLayoutData.getWidth());
            factory.setAbsolutePosition(new Point2D.Float(new Double(point2D.getX()).floatValue(), 36) );  //override location, need to be lower
            header.addElement(factory.createElement());
        }
        subFeederGroup.setHeader(header);
        
        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        subFeederGroup.setFooter(footer);
        
        return subFeederGroup;
    }
    
    /**
     * Creates the itemBand, the rows of data.
     * @return the item band.
     */
    protected ItemBand createItemBand() {
        ItemBand items = ReportFactory.createItemBandDefault();
    
        if( showBackgroundColor ) {
            items.addElement(StaticShapeElementFactory.createRectangleShapeElement
                ("background", Color.decode("#DFDFDF"), new BasicStroke(0),
                    new Rectangle2D.Float(0, 0, -100, -100), false, true));
            items.addElement(StaticShapeElementFactory.createHorizontalLine
                ("top", Color.decode("#DFDFDF"), new BasicStroke(0.1f), 0));
            items.addElement(StaticShapeElementFactory.createHorizontalLine
                ("bottom", Color.decode("#DFDFDF"), new BasicStroke(0.1f), 10));
        }
        
        for (int i = AbnormalTelemetryDataModel.SUB_VAR_COLUMN; i < bodyColumns.length; i++) {
            if(i == AbnormalTelemetryDataModel.FEEDER_NAME_COLUMN) continue;
            ColumnLayoutData cld = bodyColumns[i];
            Point2D pt2D = columnProperties.get(cld);
            TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(pt2D, cld);
            items.addElement(factory.createElement());
        }
            
        return items;
    }
    
    /**
     * Create a GroupList and all Group(s) to it.
     * @return the groupList.
     */
    protected GroupList createGroups()
    {
      final GroupList list = new GroupList();
      list.add(createSubFeederGroup());
      return list;
    }
    
    protected void initializeColumns() {
        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns().iterator();
        int accumulativeWidth = 0;
        while (bodyColumns.hasNext()) {
            ColumnLayoutData data = bodyColumns.next();
            if(data.equals(SUBSTATION_COLUMN) || data.equals(FEEDER_COLUMN)) continue;
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
    
}
