package com.cannontech.simplereport;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.report.ColumnLayoutData;
import com.cannontech.analysis.report.SimpleYukonReportBase;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.simplereport.reportlayoutdata.ReportLayoutData;

public class SimpleYukonReport extends SimpleYukonReportBase {
    
    private final YukonReportDefinition<? extends BareReportModel> definition;
    private LinkedHashMap<String, String> metaInfo = null;
    
    public SimpleYukonReport(YukonReportDefinition<? extends BareReportModel> definition, BareReportModel bareModel) {
        super(bareModel);
        Validate.notNull(definition, "YukonReportDefinition must not be null");
        this.definition = definition;
    }
    
    public SimpleYukonReport(YukonReportDefinition<? extends BareReportModel> definition, BareReportModel bareModel, LinkedHashMap<String, String> metaInfo) {
        this(definition, bareModel);
        this.metaInfo = metaInfo;
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        
        ReportLayoutData reportLayoutData = definition.getReportLayoutData();
        ColumnLayoutData[] bodyColumns = reportLayoutData.getBodyColumns();
        List<ColumnLayoutData> asList = Arrays.asList(bodyColumns);
        return asList;
    }
    
    @Override
    protected ReportHeader createReportHeader()
    {
        ReportHeader header = ReportFactory.createReportHeaderDefault();
        int positionOffset = 0;
        
        LabelElementFactory factory = new LabelElementFactory();
        factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, positionOffset));
        factory.setMinimumSize(new FloatDimension(-100, 24));
        factory.setHorizontalAlignment(ElementAlignment.CENTER);
        factory.setVerticalAlignment(ElementAlignment.MIDDLE);
        factory.setText(getModel().getTitleString());
        header.addElement(factory.createElement());
                
        positionOffset += 20;
        factory = new LabelElementFactory();
        factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, positionOffset));
        factory.setMinimumSize(new FloatDimension(-100, 24));
        factory.setHorizontalAlignment(ElementAlignment.CENTER);
        factory.setVerticalAlignment(ElementAlignment.MIDDLE);
        factory.setText(getDateRangeString());
        factory.setFontSize(new Integer(12));
        header.addElement(factory.createElement());   
        
        if(this.metaInfo != null) {

            positionOffset += 22;
            for(String key : metaInfo.keySet()) {

                factory = new LabelElementFactory();
                factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(0, positionOffset));
                factory.setMinimumSize(new FloatDimension(-100, 24));
                factory.setHorizontalAlignment(ElementAlignment.LEFT);
                factory.setVerticalAlignment(ElementAlignment.MIDDLE);
                factory.setText(key + ": " + metaInfo.get(key));
                factory.setFontSize(new Integer(10));
                header.addElement(factory.createElement()); 

                positionOffset += 12;
            }
        }
        return header;
    }

    public void setMetaInfo(LinkedHashMap<String, String> metaInfo) {
        this.metaInfo = metaInfo;
    }
    
}
