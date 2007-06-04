package com.cannontech.foreign.itron.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.jdom.Document;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.billing.format.BillingFormatter;
import com.cannontech.billing.mainprograms.BillingFileDefaults;
import com.cannontech.foreign.itron.util.ItronXmlBuilder;
import com.cannontech.foreign.itron.util.ItronXmlBuilderImpl;

public class ItronClientHandler implements BillingFormatter {
    private ItronClient client;
    
    public void setItronClient(final ItronClient client) {
        this.client = client;
    }

    public String dataToString(BillableDevice device) {
        throw new java.lang.UnsupportedOperationException("method not supported");
    }
    
    public int writeBillingFile(final List<BillableDevice> deviceList) throws IOException {
        try {
            ItronXmlBuilder builder = new ItronXmlBuilderImpl();
            Document doc = builder.buildDocument(deviceList);
            String docString = builder.documentToString(doc);
            client.invoke(new Object[]{docString});
            return doc.getRootElement().getChild("Channels").getChildren().size();  
        } catch (Exception e) {
            throw new IOException("Unable to create XML file: " + e);
        }
    }
    
    public int writeBillingFile(final List<BillableDevice> deviceList, final OutputStream out) throws IOException {
        try {
            ItronXmlBuilder builder = new ItronXmlBuilderImpl();
            Document doc = builder.buildDocument(deviceList);
            String docToString = builder.documentToString(doc);
            out.write(docToString.getBytes());
            return doc.getRootElement().getChild("Channels").getChildren().size();   
        } catch (Exception e) {
            throw new IOException("Unable to create XML file: " + e);
        }
    }

    public void setBillingFileDefaults(BillingFileDefaults billingFileDefaults) {
        
    }
    
}
