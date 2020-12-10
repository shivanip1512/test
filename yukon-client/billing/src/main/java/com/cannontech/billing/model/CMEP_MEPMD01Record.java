package com.cannontech.billing.model;

import java.util.List;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.amr.archivedValueExporter.model.CMEPUnitEnum;
import com.cannontech.billing.record.BillingRecordBase;
import com.google.common.collect.Lists;

public class CMEP_MEPMD01Record implements BillingRecordBase {
    private static final DateTimeFormatter dateTimeFormmater = DateTimeFormat.forPattern("CCYYMMddHHmm");
    private static final String RECORD_TYPE = "MEPMD01"; // This means "Metering Data Type 1"
    private static final String RECORD_VERSION = "19970819";
    private static final String DS =",";
    
    // Sender and reciever Information
    private String senderId = "CANNON";
    private String senderCustomerId = "";
    private String recieverId = "";
    private String recieverCustomerId = "";

    // Message information
    private Instant timestamp;
    private String meterId; // This is the serial number on the face plate of the meter
    private static final CMEPPurposeEnum PURPOSE = CMEPPurposeEnum.OK;
    private CMEPCommodityEnum commodity;
    private CMEPUnitEnum units;
    private float calculationConstant = 1;
    private String interval = "";
    private int count = 1; // This is set to one for the time being.  If we decide to print out multiple lines in a row we'll want to change this.
    
    // Data
    private List<DataEntry> readingData = Lists.newArrayList();

    @Override
    public String dataToString() {
        StringBuilder strBuilder = new StringBuilder();
        
        strBuilder.append(RECORD_TYPE).append(DS).append(RECORD_VERSION).append(DS);
        strBuilder.append(senderId).append(DS).append(senderCustomerId).append(DS);
        strBuilder.append(recieverId).append(DS).append(recieverCustomerId).append(DS);
        
        strBuilder.append(dateTimeFormmater.print(timestamp)).append(DS).append(meterId).append(DS).append(PURPOSE).append(DS).append(commodity.getBillingRepresentation()).append(DS);
        strBuilder.append(units).append(DS).append(calculationConstant).append(DS).append(interval).append(DS).append(count).append(DS);
        
        for (DataEntry dataEntry : readingData) {
            strBuilder.append(dateTimeFormmater.print(dataEntry.readingTimstamp)).append(DS).append(dataEntry.protocolText).append(DS).append(dataEntry.readingValue);
        }
        strBuilder.append("\r\n");
        
        return strBuilder.toString();
    }

    public class DataEntry {
        private Instant readingTimstamp;
        private String protocolText = "R 00 00";
        private double readingValue;
        
        public void setReadingTimstamp(Instant readingTimstamp) {
            this.readingTimstamp = readingTimstamp;
        }

        public void setProtocolText(String protocolText) {
            this.protocolText = protocolText;
        }

        public void setReadingValue(double readingValue) {
            this.readingValue = readingValue;
        }
    }

    public String getSenderId() {
        return senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderCustomerId() {
        return senderCustomerId;
    }
    public void setSenderCustomerId(String senderCustomerId) {
        this.senderCustomerId = senderCustomerId;
    }

    public String getRecieverId() {
        return recieverId;
    }
    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public String getRecieverCustomerId() {
        return recieverCustomerId;
    }
    public void setRecieverCustomerId(String recieverCustomerId) {
        this.recieverCustomerId = recieverCustomerId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMeterId() {
        return meterId;
    }
    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public CMEPCommodityEnum getCommodity() {
        return commodity;
    }
    public void setCommodity(CMEPCommodityEnum commodity) {
        this.commodity = commodity;
    }

    public CMEPUnitEnum getUnits() {
        return units;
    }
    public void setUnits(CMEPUnitEnum units) {
        this.units = units;
    }

    public float getCalculationConstant() {
        return calculationConstant;
    }
    public void setCalculationConstant(float calculationConstant) {
        this.calculationConstant = calculationConstant;
    }

    public String getInterval() {
        return interval;
    }
    public void setInterval(String interval) {
        this.interval = interval;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public List<DataEntry> getReadingData() {
        return readingData;
    }
    public void setReadingData(List<DataEntry> readingData) {
        this.readingData = readingData;
    }
    public void addReadingData(DataEntry readingDataEntry) {
        readingData.add(readingDataEntry);
    }
}
