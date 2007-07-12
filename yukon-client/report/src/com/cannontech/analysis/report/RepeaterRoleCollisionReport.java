package com.cannontech.analysis.report;

import java.awt.BasicStroke;

import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.RepeaterRoleCollisionModel;

public class RepeaterRoleCollisionReport extends YukonReportBase
{
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf DatabaseModel.
     */
    public RepeaterRoleCollisionReport()
    {
        this(new RepeaterRoleCollisionModel());
    }

    public RepeaterRoleCollisionReport(RepeaterRoleCollisionModel model) {
        super();
        setModel(model);
    }

    /**
     * Create a Group for Column Headings only.  
     * @return Group
     */
    private Group createColumnHeadingGroup()
    {
        final Group collHdgGroup = new Group();
        collHdgGroup.setName("Column Heading");
    
        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        LabelElementFactory factory;
        for (int i = 0; i < getModel().getColumnNames().length; i++)
        {
            factory = ReportFactory.createGroupLabelElementDefault(model, i);
            header.addElement(factory.createElement());
        }
    
        header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", null, new BasicStroke(0.5f), 22));
        collHdgGroup.setHeader(header);
    
        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        collHdgGroup.setFooter(footer);

        return collHdgGroup;
    }
    /**
     * Create a GroupList and all Group(s) to it.
     * @return the groupList.
     */
    protected GroupList createGroups()
    {
      final GroupList list = new GroupList();
      list.add(createColumnHeadingGroup());
      return list;
    }


    /**
     * Creates the itemBand, the rows of data.
     * @return the item band.
     */
    protected ItemBand createItemBand()
    {
        ItemBand items = ReportFactory.createItemBandDefault();
    
        if( showBackgroundColor )
        {
            items.addElement(StaticShapeElementFactory.createRectangleShapeElement
                ("background", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0),
                    new java.awt.geom.Rectangle2D.Float(0, 0, -100, -100), false, true));
            items.addElement(StaticShapeElementFactory.createHorizontalLine
                ("top", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f), 0));
            items.addElement(StaticShapeElementFactory.createHorizontalLine
                ("bottom", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f), 10));
        }
            
        TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), 0);
        items.addElement(factory.createElement());
    
        for (int i = 0; i < getModel().getColumnNames().length; i++)
        {
            factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            items.addElement(factory.createElement());
        }

        return items;
    }
}