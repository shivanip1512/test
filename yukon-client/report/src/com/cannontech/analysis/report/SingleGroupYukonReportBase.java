package com.cannontech.analysis.report;

import java.awt.geom.Point2D;
import java.util.Date;

import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.elementfactory.DateFieldElementFactory;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.NumberFieldElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;

/**
 * This class is meant to be used as a base class for reports that are generated from
 * models that extend the BareReportModel interface. In addition, it is designed for 
 * reports that must be grouped into sections based on the value of a single column.
 * Overriding classes only need to indicate which column to group over, which columns 
 * are in the body, their order, and their widths.
 */
public abstract class SingleGroupYukonReportBase extends SimpleYukonReportBase {

    public SingleGroupYukonReportBase(BareReportModel bareModel) {
        super(bareModel);
    }

    protected Group createSingleGroup() {
        final Group singleGroup = new Group();
        int groupFieldIndex = columIndexLookup.get(getGroupFieldData().getFieldName());
        singleGroup.setName(getSingleGroupName());
        singleGroup.addField(getGroupFieldData().getFieldName());
        GroupHeader header = new GroupHeader();
        		
        header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, ReportFactory.GROUP_HEADER_STYLE_DIMENSION);
        header.getStyle().setFontDefinitionProperty(ReportFactory.GROUP_HEADER_BAND_FONT);
        header.setRepeat(true);

        LabelElementFactory groupLabelFactory = ReportFactory.createGroupLabelElementDefault(getGroupFieldData().getColumnName(), 0, 1, 129);
        header.addElement(groupLabelFactory.createElement());
        
        TextFieldElementFactory groupFieldFactory;
        Class<?> columnClass = getModel().getColumnClass(groupFieldIndex);
        if (Number.class.isAssignableFrom(columnClass)) {
            NumberFieldElementFactory numFieldFactory = new NumberFieldElementFactory();
            numFieldFactory.setHorizontalAlignment(getGroupFieldData().getHorizontalAlignment());
            numFieldFactory.setFormatString(getGroupFieldData().getFormat());
            groupFieldFactory = numFieldFactory;
        } else if (Date.class.isAssignableFrom(columnClass)) {
            DateFieldElementFactory dateFieldFactory = new DateFieldElementFactory();
            dateFieldFactory.setVerticalAlignment(ElementAlignment.BOTTOM);
            dateFieldFactory.setFormatString(getGroupFieldData().getFormat());
            groupFieldFactory = dateFieldFactory;
        } else {
            groupFieldFactory = new TextFieldElementFactory();
        }
        groupFieldFactory.setDynamicHeight(Boolean.TRUE);
        groupFieldFactory.setHorizontalAlignment(ElementAlignment.LEFT);
        groupFieldFactory.setVerticalAlignment(ElementAlignment.BOTTOM);
        groupFieldFactory.setNullString("  ---  ");
        groupFieldFactory.setBold(Boolean.TRUE);
        groupFieldFactory.setName(getGroupFieldData().getFieldName() + ReportFactory.NAME_GROUP_ELEMENT);
        groupFieldFactory.setFieldname(getGroupFieldData().getFieldName());
        
        groupFieldFactory.setAbsolutePosition(new Point2D.Float(130, 1));
        groupFieldFactory.setMinimumSize(new FloatDimension( 300, 18));
        header.addElement(groupFieldFactory.createElement());
        
        header.addElement(ReportFactory.createBasicLine("rmGroupLine", 0.5f, 18));
        
        createGroupLabels(header);
        singleGroup.setHeader(header);
        
        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        createFooterFields(footer);
        singleGroup.setFooter(footer);
        
        return singleGroup;
    }
    
    @Override
    protected String getSingleGroupName() {
        return getGroupFieldData().getFieldName() + ReportFactory.NAME_GROUP;
    }
    
    @Override
    protected void applyLabelProperties(LabelElementFactory labelFactory, ColumnLayoutData layoutData) {
        super.applyLabelProperties(labelFactory, layoutData);
        Point2D pos = labelFactory.getAbsolutePosition();
        labelFactory.setAbsolutePosition(new Point2D.Float((float)pos.getX(), 18f) ); 
    }

    protected abstract ColumnLayoutData getGroupFieldData();
    
}
