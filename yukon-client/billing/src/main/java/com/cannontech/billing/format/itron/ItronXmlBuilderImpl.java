package com.cannontech.billing.format.itron;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

public class ItronXmlBuilderImpl implements ItronXmlBuilder{
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    private static final XMLOutputter OUT_PUTTER = new XMLOutputter(Format.getPrettyFormat());

    public ItronXmlBuilderImpl() { 

    }

    public Document buildDocument(final List<BillableDevice> deviceList) throws Exception {
        final Document doc = new Document();
        doc.addContent(createMeterReadingDocument(deviceList));
        return doc;
    }
    
    public String documentToString(Document doc) {
        return OUT_PUTTER.outputString(doc);
    }

    private Element createMeterReadingDocument(final List<BillableDevice> deviceList) throws Exception {
        final Element element = new Element("MeterReadingDocument");
        element.addNamespaceDeclaration(Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema"));
        element.addNamespaceDeclaration(Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
        element.addContent(createImportExportParametersElement());
        element.addContent(createChannelsElement(deviceList));
        return element;
    }
    
    private Element createImportExportParametersElement() {
        final Element element = new Element("ImportExportParameters");
        element.setAttribute("CreateResubmitFile", "true");
        element.setAttribute("CreateReadingGroupForRegisterReads", "false");
        element.addContent(createDataFormatElement());
        return element;
    }
    
    private Element createDataFormatElement() {
        final Element element = new Element("DataFormat");
        element.setAttribute("ReadingTimestampType", "MeterDefault");
        element.setAttribute("DSTTransitionType", "ITRON_Compliant");
        return element;
    }

    private Element createChannelsElement(final List<BillableDevice> deviceList) {
        final Element channelsElement = new Element("Channels");
        for (BillableDevice device : deviceList) {
            for (Channel channel : Channel.values()) {
                Element element = createChannelElement(device, channel);
                if (element != null ) channelsElement.addContent(element);
            }
        }
        return channelsElement;
    }
    
    private Element createChannelElement(final BillableDevice device, final Channel channel) {
        String meterId = device.getData(ReadingType.DEVICE_DATA, BillableField.meterNumber);
        Double value = getValue(device, channel, BillableField.totalConsumption); 
        Timestamp timeStamp = device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption);

        if (meterId == null || value == null || timeStamp == null) return null;

        final Element element = new Element("Channel");
        element.addContent(createChannelIdElement(meterId));
        element.addContent(createReadingsElement(createReadingElement(value, timeStamp)));
        return element;
    }
    
    private Double getValue(final BillableDevice device, final Channel channel, final BillableField field) {
        for (ReadingType type : ReadingType.values()) {
            Double value = device.getValue(channel, type, field);
            if (value != null) return value;
        }
        return null;
    }
    
    private Element createChannelIdElement(final String meterId) {
        final Element element = new Element("ChannelID");
        element.setAttribute("RegisterChannelID", meterId + ":1");
        return element;
    }
    
    private Element createReadingsElement(final Element readingsElement) {
        final Element element = new Element("Readings");
        element.addContent(readingsElement);
        return element;
    }
    
    private Element createStatusElement() {
        Element element = new Element("ReadingStatus");
        element.addContent(new Element("UnencodedStatus").setAttribute("SourceValidation", "NV"));
        return element;
    }
    
    private Element createReadingElement(final double value, final Timestamp timeStamp) {
        final Element element = new Element("Reading");
        element.setAttribute("Value", Double.toString(value));
        element.setAttribute("ReadingTime", df.format(timeStamp));
        element.addContent(createStatusElement());
        return element;
    }
    
    
    
}
