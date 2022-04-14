package com.cannontech.multispeak.event.v4;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.message.porter.message.Return;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.data.v4.ReadableDevice;

public class MeterReadEvent extends MultispeakEvent {

    private ReadableDevice device = null;
    
    /**
     * @param mspVendor_
     * @param pilMessageID_
     * @param returnMessages_
     */
    public MeterReadEvent(MultispeakVendor mspVendor_, long pilMessageID_, YukonMeter meter, int returnMessages_,
            String responseUrl) {
        super(mspVendor_, pilMessageID_, returnMessages_, null, responseUrl);
    }

    /**
     * @param mspVendor_
     * @param pilMessageID_
     */
    public MeterReadEvent(MultispeakVendor mspVendor_, long pilMessageID_, YukonMeter meter, String responseUrl) {
        this(mspVendor_, pilMessageID_, meter, 1, responseUrl);
    }

    /**
     * @return Returns the device.
     */
    public ReadableDevice getDevice() {
        return device;
    }

    /**
     * @param device The device to set.
     */
    public void setDevice(ReadableDevice device) {
        this.device = device;
    }

    @Override
    public boolean messageReceived(Return returnMsg) {
        // TODO 
        return false;
    }
    
    @Override
    public boolean isPopulated() {
        return getDevice().isPopulated();
    }

}
