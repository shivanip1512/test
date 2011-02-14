package com.cannontech.amr.porterResponseMonitor.service;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.monitors.message.PorterResponseMessage;
import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorTransaction;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.MapMaker;

public class PorterResponseMessageListener implements MessageListener {

    private PaoDao paoDao;
    private AttributeService attributeService;
    private AsyncDynamicDataSource asyncDynamicDataSource;
    private DynamicDataSource dynamicDataSource;
    private PorterResponseMonitorDao porterResponseMonitorDao;
    private static final Logger log = YukonLogManager.getLogger(PorterResponseMessageListener.class);
    private Map<Integer, PorterResponseMonitor> monitors = new ConcurrentHashMap<Integer, PorterResponseMonitor>();

    //Map entries will be removed after 12 hours
    private ConcurrentMap<TransactionIdentifier, PorterResponseMonitorTransaction> transactions
                = new MapMaker().expiration(12, TimeUnit.HOURS).makeMap();

    @PostConstruct
    public void initialize() {
        List<PorterResponseMonitor> monitorsList = porterResponseMonitorDao.getAllMonitors();
        for (PorterResponseMonitor monitor : monitorsList) {
            monitors.put(monitor.getMonitorId(), monitor);
        }

        createDatabaseChangeListener();
    }

    private void createDatabaseChangeListener(){
        DatabaseChangeEventListener addOrUpdateListener = new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                PorterResponseMonitor newMonitor = 
                    porterResponseMonitorDao.getMonitorById(event.getPrimaryKey());
                monitors.put(newMonitor.getMonitorId(), newMonitor);
            }
        };

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.PORTER_RESPONSE_MONITOR,
                                                              EnumSet.of(DbChangeType.ADD, DbChangeType.UPDATE),
                                                              addOrUpdateListener);
        DatabaseChangeEventListener deleteListener = new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                monitors.remove(event.getPrimaryKey());
            }
        };

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.PORTER_RESPONSE_MONITOR,
                                                              EnumSet.of(DbChangeType.DELETE),
                                                              deleteListener);
    }

    @Override
    public void onMessage(Message message) {
        if (monitors.isEmpty()) { //possibly also add a check in here for if there are no statusPointMonitors
            log.trace("Recieved porter response message from jms queue: not processing because " +
            		"no monitors are configured");
            return;
        }
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof PorterResponseMessage) {
                    PorterResponseMessage porterResponseMessage = (PorterResponseMessage)object;
                    handleMessage(porterResponseMessage);
                }
            } catch (JMSException e) {
                log.warn("Unable to extract PorterResponseMessage from message", e);
            }
        }
    }

    /**
     * IMPORTANT: ensure this guy is only called from a single thread to guarantee the ordering of the
     * message such that the "expectMore" flag actually makes sense.
     * @param message
     */
    private synchronized void handleMessage(PorterResponseMessage message) {
        TransactionIdentifier transactionId = new TransactionIdentifier(message);

        if (message.isFinalMsg()) {
            PorterResponseMonitorTransaction transaction = transactions.remove(transactionId);

            if (transaction != null) {
                transaction.addErrorCode(message.getErrorCode());
            } else {
                transaction = new PorterResponseMonitorTransaction(message.getPaoId(), message.getErrorCode());
            }
            processTransaction(transaction);
        } else {
            PorterResponseMonitorTransaction transaction = transactions.get(transactionId);
            if (transaction != null) {
                transaction.addErrorCode(message.getErrorCode());
            } else {
                transaction = new PorterResponseMonitorTransaction(message.getPaoId(), message.getErrorCode());
                transactions.put(transactionId, transaction);
            }
        }
    }

    private void processTransaction(PorterResponseMonitorTransaction transaction) {
        for (PorterResponseMonitor monitor : monitors.values()) {
            for (PorterResponseMonitorRule rule : monitor.getRules()) {
                if(shouldSendPointData(rule, transaction)) {
                    sendPointData(monitor, rule, transaction);
                    break;
                }
            }
        }
    }

    public static boolean shouldSendPointData(PorterResponseMonitorRule rule, PorterResponseMonitorTransaction transaction) {
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

    private void sendPointData(PorterResponseMonitor monitor, PorterResponseMonitorRule rule, PorterResponseMonitorTransaction transaction) {
        //The two DB hits below are a possible point of efficiency improvement. Leaving as is for now
        YukonPao yukonPao;
        yukonPao = paoDao.getYukonPao(transaction.getPaoId());
        LitePoint litePoint;
        try {
            litePoint = attributeService.getPointForAttribute(yukonPao, monitor.getAttribute());
        } catch (IllegalUseOfAttribute e) {
            LogHelper.debug(log, "Attribute %s configured on PorterResponseMonitor [monitorId: %s] " +
            		"could not be found on yukonPao [paoId: %s, paoType: %s]", monitor.getAttribute().getDescription(), 
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
        dynamicDataSource.putValue(pointData);

        LogHelper.debug(log, "Successfully generated PointData [pointId: %s, value: %s, type: %s]",
                        pointData.getId(), pointData.getValue(), pointData.getType());
    }

    private class TransactionIdentifier {
        private int userMessageId;
        private int connectionId;

        public TransactionIdentifier(PorterResponseMessage message) {
            this.userMessageId = message.getUserMessageId();
            this.connectionId = message.getConnectionId();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + connectionId;
            result = prime * result + userMessageId;
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
            TransactionIdentifier other = (TransactionIdentifier) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (connectionId != other.connectionId)
                return false;
            if (userMessageId != other.userMessageId)
                return false;
            return true;
        }

        private PorterResponseMessageListener getOuterType() {
            return PorterResponseMessageListener.this;
        }
    }

    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    @Autowired
    public void setAsyncDynamicDataSource(AsyncDynamicDataSource asyncDynamicDataSource) {
        this.asyncDynamicDataSource = asyncDynamicDataSource;
    }
    @Autowired
    public void setPorterResponseMonitorDao(PorterResponseMonitorDao porterResponseMonitorDao) {
        this.porterResponseMonitorDao = porterResponseMonitorDao;
    }
}
