package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.util.Date;

import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.JFreeReportBoot;
import org.jfree.report.ReportFooter;
import org.jfree.report.ReportHeader;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.function.ItemHideFunction;
import org.jfree.report.function.ItemSumFunction;
import org.jfree.report.function.TotalGroupSumFunction;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.MeterOutageCountModel;

/**
 * Created on Feb 17, 2004
 * Creates a MissedMeterReport using the com.cannontech.analysis.data.PowerFailData tableModel
 * Groups data by Collection Group and then by Device.  
 * @author bjonasson
 */
public class MeterOutageCountReport extends YukonReportBase
{
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf PowerFailModel.
     * @param data_ - PowerFailModel TableModel data
     */
    public MeterOutageCountReport(MeterOutageCountModel model_)
    {
        super();
        setPageOrientation(PageFormat.PORTRAIT);
        setModel(model_);
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf PowerFailModel.
     * @param data_ - PowerFailModel TableModel data
     */
    public MeterOutageCountReport()
    {
        this(new MeterOutageCountModel());
    }
        
    /**
     * Runs this report and shows a preview dialog.
     * @param args the arguments (ignored).
     * @throws Exception if an error occurs (default: print a stack trace)
    */
    public static void main(final String[] args) throws Exception
    {
        // initialize JFreeReport
        JFreeReportBoot.getInstance().start();
        javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

        //Get a default start date of 90 days previous.
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        cal.add(java.util.Calendar.DATE, 1);
        cal.set(java.util.Calendar.MONTH, 6);
      
        Date stop = cal.getTime();
        cal.add(java.util.Calendar.DATE, -65);
        Date start = cal.getTime();

        MeterOutageCountModel model = new MeterOutageCountModel(start, stop);
//        model.setMinimumOutageCount(1);
        model.setBillingGroups(new String[]{"/Meters/Collection/coll"});
        model.setIncompleteDataReport(true);
        YukonReportBase powerFailReport = new MeterOutageCountReport(model);
        powerFailReport.getModel().collectData();
        //Create the report
        JFreeReport report = powerFailReport.createReport();
        report.setData(powerFailReport.getModel());
                
        final PreviewDialog dialog = new PreviewDialog(report);
        // Add a window closeing event, even though I think it's already handled by setDefaultCloseOperation(..)
        dialog.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                dialog.setVisible(false);
                dialog.dispose();
                System.exit(0);
            };
        });
        
        dialog.setModal(true);
        dialog.pack();
        dialog.setVisible(true);
    }       

    /**
     * Create a Group for Group  
     * @return Group
     */
    private Group createGroupGroup()
    {
        final Group groupGroup = new Group();
        groupGroup.setName( ((MeterOutageCountModel)getModel()).getColumnName(MeterOutageCountModel.GROUP_NAME_COLUMN) + ReportFactory.NAME_GROUP);
        groupGroup.addField( ((MeterOutageCountModel)getModel()).getColumnName(MeterOutageCountModel.GROUP_NAME_COLUMN));

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), MeterOutageCountModel.GROUP_NAME_COLUMN);
        factory.setText(factory.getText() + ":");
        header.addElement(factory.createElement());

        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), MeterOutageCountModel.GROUP_NAME_COLUMN);
        tfactory.setAbsolutePosition(new Point2D.Float(110, 1));    //override the posX location
        header.addElement(tfactory.createElement());

        header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", null, new BasicStroke(0.5f), 20));

        for (int i = MeterOutageCountModel.DEVICE_NAME_COLUMN; i < getModel().getColumnCount(); i++)
        {
            factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
            factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), getModel().getColumnProperties(i).getPositionY() + 18));
            if( i >= MeterOutageCountModel.VALUE_COLUMN)
                factory.setHorizontalAlignment(ElementAlignment.RIGHT);
            header.addElement(factory.createElement());
        }

        groupGroup.setHeader(header);


        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        groupGroup.setFooter(footer);
        return groupGroup;
    }   

    /**
     * Create a Group for DeviceName, (by scheduleName, collGorup).  
     * @return Group
     */
    private Group createDeviceGroup()
    {
        final Group devGroup = new Group();
        devGroup.setName(MeterOutageCountModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
        devGroup.addField( ((MeterOutageCountModel)getModel()).getColumnName(MeterOutageCountModel.GROUP_NAME_COLUMN));
        devGroup.addField(MeterOutageCountModel.DEVICE_NAME_STRING);
          
        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 2));

        header.addElement(StaticShapeElementFactory.createHorizontalLine("line2", Color.LIGHT_GRAY, new BasicStroke(0.5f), 0));
        devGroup.setHeader(header);     
        
        if (! ((MeterOutageCountModel)getModel()).isIncompleteDataReport()) {
            GroupFooter footer = ReportFactory.createGroupFooterDefault();
            footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 20));

        	LabelElementFactory lfactory = ReportFactory.createLabelElementDefault(getModel(), MeterOutageCountModel.VALUE_COLUMN);
    		lfactory.setText("Total:");
    		lfactory.setHorizontalAlignment(ElementAlignment.RIGHT);		
    		footer.addElement(lfactory.createElement());

        	TextFieldElementFactory tfactory = ReportFactory.createTextFieldElementDefault(getModel(), MeterOutageCountModel.OUTAGE_COUNT_CALC_COLUMN);
			tfactory.setFieldname("outageCountTotal");
			tfactory.setHorizontalAlignment(ElementAlignment.RIGHT);		
			footer.addElement(tfactory.createElement());
	        devGroup.setFooter(footer);
        }
       
        return devGroup;
    }
    
    /**
     * Creates the expression collection. The xml definition for this construct:
     * @return the functions.
     * @throws FunctionInitializeException if there is a problem initialising the functions.
     */
    protected ExpressionCollection getExpressions() throws FunctionInitializeException
    {
        super.getExpressions();
        
        ItemHideFunction hideItem = new ItemHideFunction();
        hideItem.setName("hideDevice");
        hideItem.setField(MeterOutageCountModel.DEVICE_NAME_STRING);
        hideItem.setElement(MeterOutageCountModel.DEVICE_NAME_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        hideItem = new ItemHideFunction();
        hideItem.setName("hidePoint");
        hideItem.setField(MeterOutageCountModel.POINT_NAME_STRING);
        hideItem.setElement(MeterOutageCountModel.POINT_NAME_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        ItemSumFunction sumFunction = new ItemSumFunction();
        sumFunction.setName("outageCountTotal");
        sumFunction.setField(MeterOutageCountModel.OUTAGE_COUNT_CALC_STRING);
        sumFunction.setGroup(MeterOutageCountModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
        expressions.add(sumFunction);

        TotalGroupSumFunction groupSumFunction = new TotalGroupSumFunction();
        groupSumFunction.setName("groupOutageCountTotal");
        groupSumFunction.setField(MeterOutageCountModel.OUTAGE_COUNT_CALC_STRING);
//        groupSumFunction.setGroup(PowerFailModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
        expressions.add(groupSumFunction);

        
        return expressions;
    }


    /**
     * Create a GroupList and all Group(s) to it.
     * @return the groupList.
     */
    protected GroupList createGroups()
    {
        final GroupList list = new GroupList();
        list.add(createGroupGroup());
        list.add(createDeviceGroup());
        return list;
    }


    /**
     * Creates the itemBand, the rows of data.
     * @return the item band.
     */
    protected ItemBand createItemBand()
    {
        ItemBand items = ReportFactory.createItemBandDefault();

        if(showBackgroundColor)
        {
            items.addElement(StaticShapeElementFactory.createRectangleShapeElement
                ("background", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0),
                new java.awt.geom.Rectangle2D.Float(0, 0, -100, -100), false, true));
            items.addElement(StaticShapeElementFactory.createHorizontalLine
                ("top", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f), 0));
            items.addElement(StaticShapeElementFactory.createHorizontalLine
                ("bottom", java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f), 10));
        }

        for (int i = MeterOutageCountModel.DEVICE_NAME_COLUMN; i <= MeterOutageCountModel.OUTAGE_COUNT_CALC_COLUMN; i++)
        {
            TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            if( i >= MeterOutageCountModel.VALUE_COLUMN)
                factory.setHorizontalAlignment(ElementAlignment.RIGHT);
            items.addElement(factory.createElement());
        }
    
        return items;
    }
    
    @Override
    protected ReportFooter createReportFooter() {
    	ReportFooter reportFooter = super.createReportFooter();

    	LabelElementFactory lfactory = ReportFactory.createLabelElementDefault(getModel(), MeterOutageCountModel.VALUE_COLUMN);
		lfactory.setText("TOTAL:");
		lfactory.setHorizontalAlignment(ElementAlignment.RIGHT);		
		reportFooter.addElement(lfactory.createElement());
		
    	TextFieldElementFactory tfactory = ReportFactory.createTextFieldElementDefault(getModel(), MeterOutageCountModel.OUTAGE_COUNT_CALC_COLUMN);
		tfactory.setFieldname("groupOutageCountTotal");
		tfactory.setHorizontalAlignment(ElementAlignment.RIGHT);		
		reportFooter.addElement(tfactory.createElement());
		
    	return reportFooter;
    }
}