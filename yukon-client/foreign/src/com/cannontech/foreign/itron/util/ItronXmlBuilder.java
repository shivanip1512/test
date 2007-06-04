package com.cannontech.foreign.itron.util;

import java.util.List;

import org.jdom.Document;

import com.cannontech.billing.device.base.BillableDevice;

public interface ItronXmlBuilder {
    
    public Document buildDocument(List<BillableDevice> deviceList) throws Exception;
    
    public String documentToString(Document doc);
    
}
