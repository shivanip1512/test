package com.cannontech.analysis.report;

import java.awt.geom.Point2D;
import java.util.List;

import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.google.common.collect.Lists;

public abstract class MultiGroupYukonReportBase extends SingleGroupYukonReportBase {

    public MultiGroupYukonReportBase(BareReportModel bareModel) {
        super(bareModel);
    }

    protected GroupList createGroups() {
        GroupList list = new GroupList();
        for (ColumnLayoutData columnLayoutData : getGroupWrapperFieldData()) {
            list.add(createGroupWrapper(columnLayoutData));
        }
        list.add(createSingleGroup());
        return list;
    }
    
    protected Group createSingleGroup() {
        final Group singleGroup = new Group();
        int groupFieldIndex = columIndexLookup.get(getGroupFieldData().getFieldName());
        singleGroup.setName(getSingleGroupName());
        for (ColumnLayoutData columnLayoutData : getGroupWrapperFieldData()) {
            singleGroup.addField(columnLayoutData.getFieldName());
        }
        singleGroup.addField(getGroupFieldData().getFieldName());

        GroupHeader header = new GroupHeader();
        		
        header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, ReportFactory.GROUP_HEADER_STYLE_DIMENSION);
        header.getStyle().setFontDefinitionProperty(ReportFactory.GROUP_HEADER_BAND_FONT);
        header.setRepeat(true);

        LabelElementFactory groupLabelFactory = ReportFactory.createGroupLabelElementDefault(getGroupFieldData().getColumnName(), 0, 1, 129);
        header.addElement(groupLabelFactory.createElement());
        
        TextFieldElementFactory groupFieldFactory;
        Class<?> columnClass = getModel().getColumnClass(groupFieldIndex);
        groupFieldFactory = objectFieldFactoryMethod(columnClass);
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
        footer.addElement(ReportFactory.createBasicLine("footerGroupLine", 0.5f, 0));
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
    
    protected Group createGroupWrapper(ColumnLayoutData columnLayoutData) {
        Group headerGroup = new Group();
        int headerGroupFieldIndex = columIndexLookup.get(columnLayoutData.getFieldName());
        headerGroup.setName(getGroupWrapperGroupName(columnLayoutData));
        
        List<ColumnLayoutData> headerGroupFieldData = getGroupWrapperFieldData();
        for (int i = 0; !headerGroupFieldData.get(i).equals(columnLayoutData); i++) {
            headerGroup.addField(headerGroupFieldData.get(i).getFieldName());
        }
        headerGroup.addField(columnLayoutData.getFieldName());
        GroupHeader header = new GroupHeader();
        
        header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, ReportFactory.GROUP_HEADER_STYLE_DIMENSION);
        header.getStyle().setFontDefinitionProperty(ReportFactory.GROUP_HEADER_BAND_FONT);
        header.setRepeat(true);

        LabelElementFactory groupLabelFactory = ReportFactory.createGroupLabelElementDefault(columnLayoutData.getColumnName(), 0, 1, 129);
        header.addElement(groupLabelFactory.createElement());
        
        TextFieldElementFactory groupFieldFactory;
        Class<?> columnClass = getModel().getColumnClass(headerGroupFieldIndex);
        groupFieldFactory = objectFieldFactoryMethod(columnClass);
        groupFieldFactory.setDynamicHeight(Boolean.TRUE);
        groupFieldFactory.setHorizontalAlignment(ElementAlignment.LEFT);
        groupFieldFactory.setVerticalAlignment(ElementAlignment.BOTTOM);
        groupFieldFactory.setNullString("  ---  ");
        groupFieldFactory.setBold(Boolean.TRUE);
        groupFieldFactory.setName(columnLayoutData.getFieldName() + ReportFactory.NAME_GROUP_ELEMENT);
        groupFieldFactory.setFieldname(columnLayoutData.getFieldName());
        
        groupFieldFactory.setAbsolutePosition(new Point2D.Float(130, 1));
        groupFieldFactory.setMinimumSize(new FloatDimension( 300, 18));
        header.addElement(groupFieldFactory.createElement());

        header.addElement(ReportFactory.createBasicLine("GroupWrapperHeaderLine", 0.5f, 18));

        headerGroup.setHeader(header);
        
        return headerGroup;
    }

    protected String getGroupWrapperGroupName(ColumnLayoutData columnLayoutData) {
        return columnLayoutData.getFieldName() + ReportFactory.NAME_GROUP;
    }

    protected List<ColumnLayoutData> getGroupWrapperFieldData() {
        return Lists.newArrayList();
    }
    
}
