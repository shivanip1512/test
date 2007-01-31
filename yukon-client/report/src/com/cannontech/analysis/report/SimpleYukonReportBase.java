package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.BareReportModelAdapter;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelDelegate;
import com.cannontech.analysis.tablemodel.ReportModelLayout;

/**
 * This class is meant to be used as a base class for reports that have a very simple
 * layout. It uses either a BareReportModel or a ReportModelBase as input.
 * Overriding classes only need to indicate which columns are in the body, their order, 
 * and their widths.
 */
public abstract class SimpleYukonReportBase extends YukonReportBase {
    private final ReportModelBase model;

    protected Map<Integer,ColumnProperties> columnProperties = new HashMap<Integer, ColumnProperties>();

    public SimpleYukonReportBase(BareReportModel bareModel) {
        model = new BareReportModelAdapter(bareModel, new ReportModelLayout() {
            public ColumnProperties getColumnProperties(int i) {
                return columnProperties.get(i);
            }
        });
        setModel(model);
        
        initializeColumns();
    }

    public SimpleYukonReportBase(ReportModelBase model) {
        if (model instanceof BareReportModelAdapter) {
            throw new IllegalArgumentException("The BareReportModelAdapter is used internally and should not be instantiated by the calling code.");
        }
        this.model = new ReportModelDelegate(model, new ReportModelLayout() {
            public ColumnProperties getColumnProperties(int i) {
                return columnProperties.get(i);
            }
        });
        setModel(model);
        
        initializeColumns();
    }
    
    protected void initializeColumns() {
        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns().iterator();
        int accumulativeWidth = 0;
        while (bodyColumns.hasNext()) {
            ColumnLayoutData data = bodyColumns.next();
            int width = data.width;
            ColumnProperties p = new ColumnProperties(accumulativeWidth, 1, width, null);
            p.setValueFormat(data.format);
            columnProperties.put(data.modelIndex, p);
            accumulativeWidth += width;
        }
    }

    protected abstract List<ColumnLayoutData> getBodyColumns();
    protected void decorateColumn(TextFieldElementFactory factory, Integer column) {
        // extending classes can choose to implement
    }

    protected ItemBand createItemBand() {
        ItemBand items = ReportFactory.createItemBandDefault();
        
        if ( showBackgroundColor ) {
            items.addElement(StaticShapeElementFactory.createRectangleShapeElement
                ("background", Color.decode("#DFDFDF"), new BasicStroke(0),
                    new Rectangle2D.Float(0, 0, -100, -100), false, true));
            items.addElement(StaticShapeElementFactory.createHorizontalLine
                ("top", Color.decode("#DFDFDF"), new BasicStroke(0.1f), 0));
            items.addElement(StaticShapeElementFactory.createHorizontalLine
                ("bottom", Color.decode("#DFDFDF"), new BasicStroke(0.1f), 10));
        }
        
        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns().iterator();
        while (bodyColumns.hasNext()) {
            ColumnLayoutData layoutData = bodyColumns.next();
            Integer i = layoutData.modelIndex;
            
            TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            if (layoutData.horizontalAlignment != null) {
                factory.setHorizontalAlignment(layoutData.horizontalAlignment);
            }
            decorateColumn(factory, i);
            items.addElement(factory.createElement());
        }
        
        return items;
    }
    

    protected Group createSingleGroup() {
        final Group collHdgGroup = new Group();
        collHdgGroup.setName("Column Heading");
    
        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        LabelElementFactory factory;

        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns().iterator();
        while (bodyColumns.hasNext()) {
            Integer i = bodyColumns.next().modelIndex;
            factory = ReportFactory.createGroupLabelElementDefault(model, i);
            header.addElement(factory.createElement());
        }
    
        header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", null, new BasicStroke(0.5f), 22));
        collHdgGroup.setHeader(header);
    
        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        collHdgGroup.setFooter(footer);

        return collHdgGroup;
    }

    protected GroupList createGroups() {
      GroupList list = new GroupList();
      list.add(createSingleGroup());
      return list;
    }
}
