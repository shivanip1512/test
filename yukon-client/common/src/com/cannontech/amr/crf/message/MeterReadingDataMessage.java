package com.cannontech.amr.crf.message;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class MeterReadingDataMessage implements Serializable {

    private static final long serialVersionUID = 2L;

    private List<ChannelData> channelDataList;
    private MeterReadingType meterReadingType;
    private String sensorManufacturer;
    private String sensorModel;
    private String sensorSerialNumber;

    private long timeStamp;

    public List<ChannelData> getChannelDataList() {
        return channelDataList;
    }

    public MeterReadingType getMeterReadingType() {
        return meterReadingType;
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

    public void setMeterReadingType(MeterReadingType meterReadingType) {
        this.meterReadingType = meterReadingType;
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
        result = prime * result + ((meterReadingType == null) ? 0 : meterReadingType.hashCode());
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
        MeterReadingDataMessage other = (MeterReadingDataMessage) obj;
        if (channelDataList == null) {
            if (other.channelDataList != null)
                return false;
        } else if (!channelDataList.equals(other.channelDataList))
            return false;
        if (meterReadingType == null) {
            if (other.meterReadingType != null)
                return false;
        } else if (!meterReadingType.equals(other.meterReadingType))
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
                             meterReadingType,
                             sensorManufacturer,
                             sensorModel,
                             sensorSerialNumber,
                             timeStamp);
    }
    
    
}