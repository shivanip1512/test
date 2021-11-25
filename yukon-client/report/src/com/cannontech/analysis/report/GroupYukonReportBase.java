package com.cannontech.analysis.report;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.elementfactory.DateFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;

/**
 * This class is meant to be used as a base class for reports that are generated from
 * models that extend the BareReportModel interface. In addition, it is designed for
 * reports that must be grouped into sections based on the value of a single column.
 * Overriding classes only need to indicate which column to group over, which columns
 * are in the body, their order, and their widths.
 */
public abstract class GroupYukonReportBase extends SimpleYukonReportBase {

    public GroupYukonReportBase(BareReportModel bareModel) {
        super(bareModel);
    }

    protected List<RelationalGroup> createGroups() {
        List<RelationalGroup> groups = new ArrayList<>();

        for (ColumnLayoutData columnLayoutData : getMultiGroupFieldData()) {
            groups.add(createGroup(columnLayoutData));
        }
        return groups;
    }

    private RelationalGroup createGroup(ColumnLayoutData columnLayoutData) {
        final RelationalGroup singleGroup = new RelationalGroup();
        int groupFieldIndex = columIndexLookup.get(columnLayoutData.getFieldName());
        singleGroup.setName(getGroupName(columnLayoutData));

        // Add all previous columnLayoutData fields up until the one that is being processed.
        List<ColumnLayoutData> groupFieldData = getMultiGroupFieldData();
        for (int i = 0; !groupFieldData.get(i).equals(columnLayoutData); i++) {
            singleGroup.addField(groupFieldData.get(i).getFieldName());
        }
        singleGroup.addField(columnLayoutData.getFieldName());
        GroupHeader header = new GroupHeader();

        header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 30f);
        header.getStyle().getStyleProperty(TextStyleKeys.FONT, ReportFactory.GROUP_HEADER_BAND_FONT);

        header.setRepeat(true);

        LabelElementFactory groupLabelFactory = ReportFactory.createGroupLabelElementDefault(columnLayoutData.getColumnName(), 0,
                1, 129);
        header.addElement(groupLabelFactory.createElement());

        TextFieldElementFactory groupFieldFactory;
        Class<?> columnClass = getModel().getColumnClass(groupFieldIndex);
        groupFieldFactory = objectFieldFactoryMethod(columnLayoutData, columnClass);
        groupFieldFactory.setDynamicHeight(Boolean.TRUE);
        groupFieldFactory.setHorizontalAlignment(ElementAlignment.LEFT);
        groupFieldFactory.setVerticalAlignment(ElementAlignment.BOTTOM);
        groupFieldFactory.setNullString("  ---  ");
        groupFieldFactory.setBold(Boolean.TRUE);
        groupFieldFactory.setName(columnLayoutData.getFieldName() + ReportFactory.NAME_GROUP_ELEMENT);
        groupFieldFactory.setFieldname(columnLayoutData.getFieldName());

        groupFieldFactory.setAbsolutePosition(new Point2D.Float(130, 1));
        groupFieldFactory.setMinimumSize(new FloatDimension(300, 18));
        header.addElement(groupFieldFactory.createElement());

        header.addElement(ReportFactory.createBasicLine(0.5f, 18));

        // Only add the detail band column headers on the inner most group
        ColumnLayoutData innerMostGroup = groupFieldData.get(groupFieldData.size() - 1);
        boolean innerGroup = columnLayoutData.equals(innerMostGroup);
        if (innerGroup) {
            createGroupLabels(header);
        }

        singleGroup.setHeader(header);

        // Only add the footer data on the outer most group
        ColumnLayoutData outerMostGroup = groupFieldData.get(0);
        boolean outerGroup = columnLayoutData.equals(outerMostGroup);
        if (outerGroup) {
            GroupFooter footer = ReportFactory.createGroupFooterDefault();
            createFooterFields(footer);
            footer.addElement(ReportFactory.createBasicLine(0.5f, 0));
            singleGroup.setFooter(footer);
        }

        return singleGroup;
    }

    private String getGroupName(ColumnLayoutData columnLayoutData) {
        return columnLayoutData.getFieldName() + ReportFactory.NAME_GROUP;
    }

    @Override
    protected void applyLabelProperties(LabelElementFactory labelFactory, ColumnLayoutData layoutData) {
        super.applyLabelProperties(labelFactory, layoutData);
        Point2D pos = labelFactory.getAbsolutePosition();
        labelFactory.setAbsolutePosition(new Point2D.Float((float) pos.getX(), 20f));
    }

    protected abstract List<ColumnLayoutData> getMultiGroupFieldData();

    /**
     * This method takes a class type and returns a textFieldElementFactory object to
     * build a group.
     */
    protected TextFieldElementFactory objectFieldFactoryMethod(ColumnLayoutData columnLayoutData, Class<?> columnClass) {
        TextFieldElementFactory groupFieldFactory;
        if (Number.class.isAssignableFrom(columnClass)) {
            NumberFieldElementFactory numFieldFactory = new NumberFieldElementFactory();
            numFieldFactory.setHorizontalAlignment(columnLayoutData.getHorizontalAlignment());
            numFieldFactory.setFormatString(columnLayoutData.getFormat());
            groupFieldFactory = numFieldFactory;
        } else if (Date.class.isAssignableFrom(columnClass)) {
            DateFieldElementFactory dateFieldFactory = new DateFieldElementFactory();
            dateFieldFactory.setVerticalAlignment(ElementAlignment.BOTTOM);
            dateFieldFactory.setFormatString(columnLayoutData.getFormat());
            groupFieldFactory = dateFieldFactory;
        } else {
            groupFieldFactory = new TextFieldElementFactory();
        }
        return groupFieldFactory;
    }
}
