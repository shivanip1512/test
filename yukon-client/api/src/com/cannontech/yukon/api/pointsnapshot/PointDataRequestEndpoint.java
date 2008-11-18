package com.cannontech.yukon.api.pointsnapshot;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class PointDataRequestEndpoint {
    private PointDataSnapshotService pointDataSnapshotService;
    
    private XPath newSinceExpression;
    private XPath pointIdExpression;


    public PointDataRequestEndpoint() {
    }

    @PostConstruct
    public void initialize() throws JDOMException {
        
        newSinceExpression = XPath.newInstance("//y:newSince");
        newSinceExpression.addNamespace(YukonXml.getYukonNamespace());
        pointIdExpression = XPath.newInstance("//y:pointIds/y:id");
        pointIdExpression.addNamespace(YukonXml.getYukonNamespace());
    }

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="pointDataRequestCmd")
    public Element invoke(Element pointDataRequest) throws Exception {
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(pointDataRequest);
        Number numberValueOf = template.evaluateAsLong("//y:newSince");
        Long newSince = null;
        if (numberValueOf != null) {
            newSince = numberValueOf.longValue();
        }
        
        List<Integer> pointIdList = template.evaluateAsIntegerList("//y:pointIds/y:id");
        
        SnapshotResult snapshots = pointDataSnapshotService.getUpdatedSnapshots(pointIdList, newSince);
        
        Element result = new Element("pointDataResponseMsg", YukonXml.getYukonNamespace());
        Element pointValuesEl = new Element("pointValues", YukonXml.getYukonNamespace());
        
        for (PointValueQualityHolder pointValue : snapshots.getValues()) {
            Element pointDataEl = new Element("pointData", YukonXml.getYukonNamespace());
            pointDataEl.addContent(XmlUtils.createIntegerElement("pointId", YukonXml.getYukonNamespace(), pointValue.getId()));
            pointDataEl.addContent(XmlUtils.createDateElement("time", YukonXml.getYukonNamespace(), pointValue.getPointDataTimeStamp()));
            pointDataEl.addContent(XmlUtils.createDoubleElement("value", YukonXml.getYukonNamespace(), pointValue.getValue()));
            pointDataEl.addContent(XmlUtils.createStringElement("quality", YukonXml.getYukonNamespace(), pointValue.getQualityEnum().toString()));
            pointValuesEl.addContent(pointDataEl);
        }
        result.addContent(pointValuesEl);
        
        result.addContent(XmlUtils.createLongElement("validThrough", YukonXml.getYukonNamespace(), snapshots.getAsOf()));
        
        return result;
    }
    
    @Autowired
    public void setPointDataSnapshotService(
            PointDataSnapshotService pointDataSnapshotService) {
        this.pointDataSnapshotService = pointDataSnapshotService;
    }
}