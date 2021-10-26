package com.cannontech.analysis.report;

import java.awt.BasicStroke;

import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.GroupList;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.CapBankRecentMaxDailyOpsModel;

public class CapBankRecentMaxDailyOpsReport extends YukonReportBase
{
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf DatabaseModel.
     */
    public CapBankRecentMaxDailyOpsReport()
    {
        this(new CapBankRecentMaxDailyOpsModel());
    }

    public CapBankRecentMaxDailyOpsReport(CapBankRecentMaxDailyOpsModel model) {
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
    
        header.addElement(HorizontalLineElementFactory.createHorizontalLine(22));
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
            items.addElement(RectangleElementFactory.createFilledRectangle(0, 0, -100, -100, java.awt.Color.decode("#DFDFDF")));
            items.addElement(HorizontalLineElementFactory.createHorizontalLine
                (0, java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f)));
            items.addElement(HorizontalLineElementFactory.createHorizontalLine
                (10, java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f)));
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
