package com.cannontech.amr.rfn.service;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectRequest;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeterIdentifier;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.jms.JmsReplyReplyHandler;
import com.cannontech.common.util.jms.RequestReplyReplyTemplate;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class RfnMeterDisconnectService {
    
    private ConnectionFactory connectionFactory;
    private ConfigurationSource configurationSource;
    private RequestReplyReplyTemplate rrrTemplate;
    
    /**
     * Attempts to send a disconnect request for a RFN meter.  Will use a separate thread to make the request.
     * Will expect two responses. 
     * 
     * The first is a status message indicating this is a known meter and a disconnect
     * will be tried, or a disconnect is not possible for this meter.  This response should come back within seconds.
     * 
     *  The second response is the confirmation that the disconnect happened or and error occurred.  
     *  This response is only expected if the first response was 'OK'.  
     *  This response can take anywhere from seconds to minutes to tens of minutes depending
     *  on network performance.
     *  
     *  The master.cfg can contain two parameters to define the timeouts:
     *  
     *  RFN_METER_DISCONNECT_REPLY1_TIMEOUT
     *  RFN_METER_DISCONNECT_REPLY2_TIMEOUT
     *  
     *  If not provided they default to 10 seconds and 30 minutes.
     *  
     * @param meter The meter to disconnect.
     * @param callback The callback to use for updating status, errors and disconnect result.
     */
    public void send(final RfnMeterIdentifier meter, final RfnMeterDisconnectStatusType action, final RfnMeterDisconnectCallback callback) {
        JmsReplyReplyHandler<RfnMeterDisconnectInitialReply, RfnMeterDisconnectConfirmationReply> handler = new JmsReplyReplyHandler<RfnMeterDisconnectInitialReply, RfnMeterDisconnectConfirmationReply>() {

            @Override
            public void complete() {
                callback.complete();
            }

            @Override
            public Class<RfnMeterDisconnectInitialReply> getExpectedType1() {
                return RfnMeterDisconnectInitialReply.class;
            }

            @Override
            public Class<RfnMeterDisconnectConfirmationReply> getExpectedType2() {
                return RfnMeterDisconnectConfirmationReply.class;
            }

            @Override
            public void handleException(Exception e) {
                MessageSourceResolvable message = 
                    YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.web.widgets.rfnMeterDisconnectWidget.sendCommand.error", e.toString());
                callback.processingExceptionOccured(message);
            }

            @Override
            public boolean handleReply1(RfnMeterDisconnectInitialReply initialReply) {
                if (!initialReply.isSuccess()) {
                    /* Request failed */
                    MessageSourceResolvable message = 
                        YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.web.widgets.rfnMeterDisconnectWidget.sendCommand.error", initialReply);
                    callback.receivedError(message);
                    return false;
                } else {
                    /* Request successful, wait for reply 2 */
                    return true;
                }
            }

            @Override
            public void handleReply2(RfnMeterDisconnectConfirmationReply confirmationReplyMessage) {
                if (!confirmationReplyMessage.isSuccess()) {
                    /* Request failed */
                    MessageSourceResolvable message = 
                        YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.web.widgets.rfnMeterDisconnectWidget.sendCommand.confirmError", confirmationReplyMessage);
                    callback.receivedError(message);
                } else {
                    /* Confirmation response successful, process point data */
                    callback.receivedSuccess(confirmationReplyMessage.getState());
                }
           }

            @Override
            public void handleTimeout1() {
                MessageSourceResolvable createSingleCodeWithArguments = 
                    YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.web.widgets.rfnMeterDisconnectWidget.sendCommand.error", "T1");
                callback.receivedError(createSingleCodeWithArguments);
            }

            @Override
            public void handleTimeout2() {
                MessageSourceResolvable createSingleCodeWithArguments = 
                    YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.web.widgets.rfnMeterDisconnectWidget.sendCommand.confirmError", "T2");
                callback.receivedError(createSingleCodeWithArguments);
            }
        };
        
        rrrTemplate.send(new RfnMeterDisconnectRequest(meter, action), handler);
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    @PostConstruct
    public void initialize() {
        rrrTemplate = new RequestReplyReplyTemplate();
        rrrTemplate.setConfigurationName("RFN_METER_DISCONNECT");
        rrrTemplate.setConfigurationSource(configurationSource);
        rrrTemplate.setConnectionFactory(connectionFactory);
        rrrTemplate.setRequestQueueName("yukon.rr.obj.amr.rfn.MeterDisconnectRequest", false);
    }
    
}