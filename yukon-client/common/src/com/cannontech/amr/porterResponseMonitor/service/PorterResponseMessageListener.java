package com.cannontech.amr.porterResponseMonitor.service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.StreamMessage;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.amr.monitors.message.PorterResponseMessage;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorTransaction;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class PorterResponseMessageListener implements MessageListener {

    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private DeviceGroupService deviceGroupService;
    private static final Logger log = YukonLogManager.getLogger(PorterResponseMessageListener.class);
  

    //Map entries will be removed after 12 hours
    private Cache<TransactionIdentifier, PorterResponseMonitorTransaction> transactions
                = CacheBuilder.newBuilder().expireAfterWrite(12, TimeUnit.HOURS).build();

    @Override
    public void onMessage(Message message) {
        if (monitorCacheService.getEnabledPorterResponseMonitors().isEmpty()) {
            log.trace("Received porter response message from jms queue: not processing because " +
            		"no monitors exist or they are all disabled");
            return;
        }
        if (message instanceof StreamMessage) {
            StreamMessage streamMessage = (StreamMessage) message;
            try {
                PorterResponseMessage porterResponseMessage = buildPorterResponseMessage(streamMessage);
                handleMessage(porterResponseMessage);
            } catch (JMSException e) {
                log.warn("Unable to extract PorterResponseMessage from message", e);
            }
        }
    }

    private PorterResponseMessage buildPorterResponseMessage(StreamMessage streamMessage) throws JMSException {
        long connectionId = streamMessage.readLong();
        int paoId = streamMessage.readInt();
        boolean finalMsg = streamMessage.readBoolean();
        int errorCode = streamMessage.readInt();
        int userMessageId = streamMessage.readInt();
        
        PorterResponseMessage porterResponseMessage = 
            new PorterResponseMessage(userMessageId, connectionId, paoId, errorCode, finalMsg);

        return porterResponseMessage;
    }

    /**
     * IMPORTANT: ensure this guy is only called from a single thread to guarantee the ordering of the
     * message such that the "expectMore" flag actually makes sense.
     * @param message
     */
    public synchronized void handleMessage(PorterResponseMessage message) {
        TransactionIdentifier transactionId = new TransactionIdentifier(message);

        if (message.isFinalMsg()) {
            PorterResponseMonitorTransaction transaction = transactions.getIfPresent(transactionId);
            transactions.invalidate(transactionId);

            if (transaction != null) {
                if (transaction.getPaoId() != message.getPaoId()) {
                    LogHelper.warn(log, "PaoId stored in transaction [%s] does not match PaoId of message [%s]", transaction.getPaoId(), message.getPaoId());
                }
                transaction.addErrorCode(message.getErrorCode());
            } else {
                transaction = new PorterResponseMonitorTransaction(message.getPaoId(), message.getErrorCode());
            }
            processTransaction(transaction);
        } else {
            PorterResponseMonitorTransaction transaction = transactions.getIfPresent(transactionId);
            if (transaction != null) {
                if (transaction.getPaoId() != message.getPaoId()) {
                    LogHelper.warn(log, "PaoId stored in transaction [%s] does not match PaoId of message [%s]", transaction.getPaoId(), message.getPaoId());
                }
                transaction.addErrorCode(message.getErrorCode());
            } else {
                transaction = new PorterResponseMonitorTransaction(message.getPaoId(), message.getErrorCode());
                transactions.put(transactionId, transaction);
            }
        }
    }

    private void processTransaction(PorterResponseMonitorTransaction transaction) {
        monitorCacheService.getEnabledPorterResponseMonitors().stream()
        .filter(monitor -> shouldProcessDevice(monitor, transaction.getPaoId()))
        .forEach(monitor -> monitor.getRules().stream()
            .filter(rule -> shouldSendPointData(transaction, rule))
            .findFirst()
            .ifPresent(rule -> sendPointData(monitor, rule, transaction)));
    }
    
    // Checks if device is PLC or not. Also checks if it belongs to device group of monitor.
    private boolean shouldProcessDevice(PorterResponseMonitor monitor, Integer paoId) {
        
        return Optional.of(paoId)
                .map(databaseCache.getAllPaosMap()::get)
                // Process further if the device is of PLC type
                .filter(yukonPao -> yukonPao.getPaoIdentifier().getPaoType().isPlc())
                // Do not do further processing as device do not belong to the device group of monitor
                .filter(yukonPao -> 
                    Optional.ofNullable(deviceGroupService.findGroupName(monitor.getGroupName()))
                        .map(group -> deviceGroupService.isDeviceInGroup(group, yukonPao))
                        .orElse(false))
                .isPresent();
    }
    
    private boolean shouldSendPointData(PorterResponseMonitorTransaction transaction, PorterResponseMonitorRule rule) {
        Set<Integer> ruleErrorCodes = rule.getErrorCodesAsIntegers();

        if (rule.isSuccess() == transaction.isSuccess()) {
            if (rule.getMatchStyle().getMatchStyle().matches(transaction.getErrorCodes(), ruleErrorCodes)) { 
                LogHelper.trace(log, "found matching rule for rule %s, on transaction %s", rule, transaction);
                return true;
            }
        }

        LogHelper.trace(log, "did not find matching rule for rule %s, on transaction %s", rule, transaction);
        return false;
    }

    protected void sendPointData(PorterResponseMonitor monitor, PorterResponseMonitorRule rule, PorterResponseMonitorTransaction transaction) {
        //The two DB hits below are a possible point of efficiency improvement. Leaving as is for now
        YukonPao yukonPao = databaseCache.getAllPaosMap().get(transaction.getPaoId());
        LitePoint litePoint;
        try {
            litePoint = attributeService.getPointForAttribute(yukonPao, monitor.getAttribute());
        } catch (IllegalUseOfAttribute e) {
            LogHelper.trace(log, "Attribute %s configured on PorterResponseMonitor [monitorId: %s] " +
                    "could not be found on yukonPao [paoId: %s, paoType: %s]", monitor.getAttribute(),
                    monitor.getMonitorId(), yukonPao.getPaoIdentifier().getPaoId(), yukonPao.getPaoIdentifier().getPaoType());
            return;
        }

        if (litePoint.getStateGroupID() != monitor.getStateGroup().getStateGroupID()) {
            LogHelper.debug(log, "Point [pointId: %s, pointName: %s] with StateGroupId of %s does not match StateGroupId of %s " +
                    "of PorterResponseMonitor [monitorId: %s]", litePoint.getPointID(), litePoint.getPointName(),
                    litePoint.getStateGroupID(), monitor.getStateGroup().getStateGroupID(), monitor.getMonitorId());
            return;
        }

        PointData pointData = new PointData();
        pointData.setId(litePoint.getPointID());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setValue(rule.getStateInt());
        pointData.setType(litePoint.getPointTypeEnum().getPointTypeId());
        asyncDynamicDataSource.putValue(pointData);

        LogHelper.debug(log, "Successfully generated PointData [pointId: %s, value: %s, type: %s]",
                        pointData.getId(), pointData.getValue(), pointData.getType());
    }

    private class TransactionIdentifier {
        private int userMessageId;
        private long connectionId;

        public TransactionIdentifier(PorterResponseMessage message) {
            this.userMessageId = message.getUserMessageId();
            this.connectionId = message.getConnectionId();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + (int) (connectionId ^ (connectionId >>> 32));
            result = prime * result + userMessageId;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TransactionIdentifier other = (TransactionIdentifier) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (connectionId != other.connectionId) {
                return false;
            }
            if (userMessageId != other.userMessageId) {
                return false;
            }
            return true;
        }

        private PorterResponseMessageListener getOuterType() {
            return PorterResponseMessageListener.this;
        }
    }
    
    /** 
     * Only for testing
     */
    public void setMonitorCache(MonitorCacheService monitorCacheService) {
        this.monitorCacheService = monitorCacheService;
    }
}