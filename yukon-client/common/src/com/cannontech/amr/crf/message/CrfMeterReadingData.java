package com.cannontech.amr.crf.message;

import java.io.Serializable;
import java.util.List;


public class CrfMeterReadingData implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ChannelData> channelDataList;
    private CrfMeterReadingType crfMeterReadingType;
    private String sensorManufacturer;
    private String sensorModel;
    private String sensorSerialNumber;

    private long timeStamp;

    public List<ChannelData> getChannelDataList() {
        return channelDataList;
    }

    public CrfMeterReadingType getMeterReadingType() {
        return crfMeterReadingType;
    }

    public String getSensorManufacturer() {
        return sensorManufacturer;
    }

    public String getSensorModel() {
        return sensorModel;
    }

    public String getSensorSerialNumber() {
        return sensorSerialNumber;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setChannelDataList(List<ChannelData> channelDataList) {
        this.channelDataList = channelDataList;
    }

    public void setMeterReadingType(CrfMeterReadingType crfMeterReadingType) {
        this.crfMeterReadingType = crfMeterReadingType;
    }

    public void setSensorManufacturer(String sensorManufacturer) {
        this.sensorManufacturer = sensorManufacturer;
    }

    public void setSensorModel(String sensorModel) {
        this.sensorModel = sensorModel;
    }

    public void setSensorSerialNumber(String sensorSerialNumber) {
        this.sensorSerialNumber = sensorSerialNumber;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channelDataList == null) ? 0 : channelDataList.hashCode());
        result = prime * result + ((crfMeterReadingType == null) ? 0 : crfMeterReadingType.hashCode());
        result = prime * result + ((sensorManufacturer == null) ? 0 : sensorManufacturer.hashCode());
        result = prime * result + ((sensorModel == null) ? 0 : sensorModel.hashCode());
        result = prime * result + ((sensorSerialNumber == null) ? 0 : sensorSerialNumber.hashCode());
        result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CrfMeterReadingData other = (CrfMeterReadingData) obj;
        if (channelDataList == null) {
            if (other.channelDataList != null)
                return false;
        } else if (!channelDataList.equals(other.channelDataList))
            return false;
        if (crfMeterReadingType == null) {
            if (other.crfMeterReadingType != null)
                return false;
        } else if (!crfMeterReadingType.equals(other.crfMeterReadingType))
            return false;
        if (sensorManufacturer == null) {
            if (other.sensorManufacturer != null)
                return false;
        } else if (!sensorManufacturer.equals(other.sensorManufacturer))
            return false;
        if (sensorModel == null) {
            if (other.sensorModel != null)
                return false;
        } else if (!sensorModel.equals(other.sensorModel))
            return false;
        if (sensorSerialNumber == null) {
            if (other.sensorSerialNumber != null)
                return false;
        } else if (!sensorSerialNumber.equals(other.sensorSerialNumber))
            return false;
        if (timeStamp != other.timeStamp)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("MeterReadingDataMessage [channelDataList=%s, meterReadingType=%s, sensorManufacturer=%s, sensorModel=%s, sensorSerialNumber=%s, timeStamp=%tc]",
                             channelDataList,
                             crfMeterReadingType,
                             sensorManufacturer,
                             sensorModel,
                             sensorSerialNumber,
                             timeStamp);
    }
    
    
}