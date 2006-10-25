package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import com.cannontech.analysis.tablemodel.ReportModelLayout;

/**
 * This class is meant to be used as a base class for reports that are generated from
 * models that extend the BareReportModel interface. It makes use of the 
 * BareReportModelAdapter to preserve compatibility with older code (e.g. ReportFactory).
 * Overriding classes only need to indicate which columns are in the body, their order, 
 * and their widths.
 */
public abstract class BareModelYukonReportBase extends YukonReportBase implements ReportModelLayout {
    private final BareReportModel bareModel;
    private final BareReportModelAdapter adapter;

    protected Map<Integer,ColumnProperties> columnProperties = new HashMap<Integer, ColumnProperties>();

    public BareModelYukonReportBase(BareReportModel bareModel) {
        super();
        this.bareModel = bareModel;
        adapter = new BareReportModelAdapter(this.bareModel, this);
        setModel(adapter);
        
        initializeColumns();
    }

    protected void initializeColumns() {
        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns();
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

    protected abstract Iterator<ColumnLayoutData> getBodyColumns();

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
        
        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns();
        while (bodyColumns.hasNext()) {
            Integer i = bodyColumns.next().modelIndex;
            
            TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            //configureColumn(factory, i);
            items.addElement(factory.createElement());
        }
        
        return items;
    }
    
    protected Group createSingleGroup() {
        final Group collHdgGroup = new Group();
        collHdgGroup.setName("Column Heading");
    
        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        LabelElementFactory factory;

        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns();
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

    public ColumnProperties getColumnProperties(int i) {
        return columnProperties.get(i);
    }

}
