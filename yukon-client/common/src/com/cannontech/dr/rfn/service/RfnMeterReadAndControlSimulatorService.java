package com.cannontech.dr.rfn.service;

import com.cannontech.dr.rfn.model.RfnMeterReadAndControlDisconnectSimulatorSettings;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlReadSimulatorSettings;
import com.cannontech.simulators.AutoStartableSimulator;

/**
 * This is a development testing service that is intended to act as a replacement for Network Manager
 * in handling RFN meter requests.
 */

public interface RfnMeterReadAndControlSimulatorService extends AutoStartableSimulator{

    /**
     * The simulator begins automatically processing meter read requests and sending meter read responses.
     * @param settings Configuration that determines the type of response sent. If null, default settings will be used.
     * @return True if meter read reply was started, and false if meter read reply was already running or in the
     * process of stopping.
     */
    boolean startMeterReadReply(RfnMeterReadAndControlReadSimulatorSettings settings);

    /**
     * The simulator stops automatically processing meter read requests.
     */
    void stopMeterReadReply();

    /**
     * The simulator begins automatically processing meter disconnect requests and sending meter disconnect responses.
     * @param settings Configuration that determines the type of response sent. If null, default settings will be used.
     * @return True if meter disconnect reply was started, and false if meter disconnect reply was already running or in the
     * process of stopping.
     */
    boolean startMeterDisconnectReply(RfnMeterReadAndControlDisconnectSimulatorSettings settings);

    /**
     * The simulator stops automatically processing meter disconnect requests.
     */
    void stopMeterDisconnectReply();

    /**
     * Check to see if the meter read simulator is currently active.
     * @return True if the meter read simulator is active, false if it isn't.
     */
    public boolean isMeterReadReplyActive();
    
    /**
     * Check to see if the meter disconnect simulator is currently active.
     * @return True if the meter disconnect simulator is active, false if it isn't.
     */
    public boolean isMeterDisconnectReplyActive();
    
    /**
     * Check to see if the meter read simulator is stopping.
     * @return True if the meter read simulator is stopping, false if it isn't.
     */
    public boolean isMeterReadReplyStopping();
    
    /**
     * Check to see if the meter disconnect simulator is stopping.
     * @return True if the meter disconnect simulator is stopping, false if it isn't.
     */
    public boolean isMeterDisconnectReplyStopping();
    
    /**
     * Get the current settings for the read replies.
     * @return the settings if the thread is running, otherwise null.
     */
    public RfnMeterReadAndControlReadSimulatorSettings getReadSettings();
    
    /**
     * Get the current settings for the disconnect replies.
     * @return the settings if the thread is running, otherwise null.
     */
    public RfnMeterReadAndControlDisconnectSimulatorSettings getDisconnectSettings();
    
    @Override
    void startSimulatorWithCurrentSettings();
}
