package com.cannontech.amr.rfn.service;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectRequest;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectCmdType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.jms.JmsReplyReplyHandler;
import com.cannontech.common.util.jms.RequestReplyReplyTemplate;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.dispatch.message.PointData;

public class RfnMeterDisconnectService {

    private static final Logger log = YukonLogManager.getLogger(RfnMeterDisconnectService.class);
    
    private static final String queue = "yukon.qr.obj.amr.rfn.MeterDisconnectRequest";
    private static final String cparm = "RFN_METER_DISCONNECT";
    
    private static final String firstReplyTimeout = "yukon.web.widgets.disconnectMeterWidget.rfn.sendCommand.firstReplyTimeout";
    private static final String secondReplyTimeout = "yukon.web.widgets.disconnectMeterWidget.rfn.sendCommand.secondReplyTimeout";
    private static final String error = "yukon.web.widgets.disconnectMeterWidget.rfn.sendCommand.error";
    private static final String confirmError = "yukon.web.widgets.disconnectMeterWidget.rfn.sendCommand.confirmError";

    private RequestReplyReplyTemplate<RfnMeterDisconnectInitialReply, RfnMeterDisconnectConfirmationReply> rrrTemplate;
    
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    
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
     *  If not provided they default to 1 minute and 10 minutes.
     *  
     * @param meter The meter to disconnect.
     * @param callback The callback to use for updating status, errors and disconnect result.
     */
    public void send(final RfnMeter meter, 
                     final RfnMeterDisconnectCmdType action, 
                     final RfnMeterDisconnectCallback callback) {
        
        JmsReplyReplyHandler<RfnMeterDisconnectInitialReply, RfnMeterDisconnectConfirmationReply> handler 
                = new JmsReplyReplyHandler<RfnMeterDisconnectInitialReply, RfnMeterDisconnectConfirmationReply>() {

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
                    YukonMessageSourceResolvable.createSingleCodeWithArguments(error, e.toString());
                callback.processingExceptionOccurred(message);
            }

            @Override
            public boolean handleReply1(RfnMeterDisconnectInitialReply initialReply) {
                if (!initialReply.isSuccess()) {
                    /* Request failed */
                    MessageSourceResolvable message = 
                        YukonMessageSourceResolvable.createSingleCodeWithArguments(error, initialReply);
                    callback.receivedError(message, null, null);
                    return false;
                } else {
                    /* Request successful, wait for reply 2 */
                    return true;
                }
            }

            @Override
            public void handleReply2(RfnMeterDisconnectConfirmationReply confirmationReplyMessage) {
                RfnDisconnectStatusState state = RfnDisconnectStatusState.getForNmState(confirmationReplyMessage.getState());
                if (!confirmationReplyMessage.isSuccess()) {
                    /* Request failed */
                    MessageSourceResolvable message = YukonMessageSourceResolvable.createSingleCodeWithArguments(confirmError, confirmationReplyMessage);
                    callback.receivedError(message, state, confirmationReplyMessage.getReplyType());
                    if (RfnMeterDisconnectConfirmationReplyType.FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT == confirmationReplyMessage.getReplyType()
                        || RfnMeterDisconnectConfirmationReplyType.FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT == confirmationReplyMessage.getReplyType()
                        || RfnMeterDisconnectConfirmationReplyType.FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD == confirmationReplyMessage.getReplyType()) {
                        publishPointData(confirmationReplyMessage.getState().getRawState(), meter);
                    }
                } else {
                    PointValueQualityHolder pointData = publishPointData(confirmationReplyMessage.getState().getRawState(), meter);
                    /* Confirmation response successful, process point data */
                    callback.receivedSuccess(state, pointData);
                }
           }
            
            @Override
            public void handleTimeout1() {
                MessageSourceResolvable createSingleCodeWithArguments = 
                    YukonMessageSourceResolvable.createSingleCodeWithArguments(firstReplyTimeout);
                callback.receivedError(createSingleCodeWithArguments, null, null);
            }

            @Override
            public void handleTimeout2() {
                MessageSourceResolvable createSingleCodeWithArguments = 
                    YukonMessageSourceResolvable.createSingleCodeWithArguments(secondReplyTimeout);
                callback.receivedError(createSingleCodeWithArguments, null, null);
            }
        };
        
        rrrTemplate.send(new RfnMeterDisconnectRequest(meter.getRfnIdentifier(), action), handler);
    }
    
    private PointValueQualityHolder publishPointData(int rawState, RfnMeter meter) {
        PointData pointData = null;
        try {
            LitePoint point = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
            pointData = new PointData();
            pointData.setId(point.getLiteID());
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setValue(rawState);
            pointData.setTime(new Date());
            pointData.setType(point.getPointType());
            pointData.setTagsPointMustArchive(true); // temporary solution

            asyncDynamicDataSource.putValue(pointData);

            log.debug("PointData generated for RfnMeterDisconnectRequest");
        } catch (IllegalUseOfAttribute e) {
            log.error("There is no Disconnect Status Point for " + meter, e);
        }
        return pointData;
    }
    
    @PostConstruct
    public void initialize() {
        rrrTemplate = new RequestReplyReplyTemplate<>(
                cparm, configurationSource, connectionFactory, queue, false);
    }
    
}