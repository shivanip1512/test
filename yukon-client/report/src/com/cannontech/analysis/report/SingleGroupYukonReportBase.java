package com.cannontech.analysis.report;

import java.awt.geom.Point2D;
import java.util.Iterator;

import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;

/**
 * This class is meant to be used as a base class for reports that are generated from
 * models that extend the BareReportModel interface. In addition, it is designed for 
 * reports that must be grouped into sections based on the value of a single column.
 * Overriding classes only need to indicate which column to group over, which columns 
 * are in the body, their order, and their widths.
 */
public abstract class SingleGroupYukonReportBase extends BareModelYukonReportBase {

    public SingleGroupYukonReportBase(BareReportModel bareModel) {
        super(bareModel);
    }

    @Override
    protected void initializeColumns() {
        super.initializeColumns();
        
        // setup ColumnProperties for group column
        ColumnLayoutData groupFieldData = getGroupFieldData();
        ColumnProperties p = new ColumnProperties(0, 1, groupFieldData.width, null);
        columnProperties.put(groupFieldData.modelIndex, p);
    }
    
    protected Group createSingleGroup() {
        final Group routeMacroGroup = new Group();
        int groupFieldIndex = getGroupFieldData().modelIndex;
        String columnName = getModel().getColumnName(groupFieldIndex);
        routeMacroGroup.setName(columnName + ReportFactory.NAME_GROUP);
        routeMacroGroup.addField(columnName);
        
        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        
        LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), groupFieldIndex);
        header.addElement(factory.createElement());
        
        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), groupFieldIndex);
        tfactory.setAbsolutePosition(new Point2D.Float(130, 1));    //override posX
        factory.setMinimumSize(new FloatDimension( 300, 18));
        header.addElement(tfactory.createElement());
        
        header.addElement(ReportFactory.createBasicLine("rmGroupLine", 0.5f, 20));
        
        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns();
        while (bodyColumns.hasNext()) {
            Integer i = bodyColumns.next().modelIndex;
            
            factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
            //override location, need to be lower than macroroute text
            factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18) ); 
            header.addElement(factory.createElement());
        }
        
        routeMacroGroup.setHeader(header);
        
        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        routeMacroGroup.setFooter(footer);
        
        return routeMacroGroup;
    }

    protected abstract ColumnLayoutData getGroupFieldData();
    
}
