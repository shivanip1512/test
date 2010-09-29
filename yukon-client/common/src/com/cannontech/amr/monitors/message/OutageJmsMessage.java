package com.cannontech.amr.monitors.message;

import java.io.Serializable;

import com.cannontech.amr.statusPointMonitoring.model.OutageActionType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueQualityHolder;

/**
 * JMS Queue name: yukon.notif.obj.amr.OutageJmsMessage
 */

public class OutageJmsMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private PaoIdentifier paoIdentifier;
    private PointValueQualityHolder pointValueQualityHolder;
    private OutageActionType actionType;
    private String source;
    
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }
    public void setPointValueQualityHolder(PointValueQualityHolder pointValueQualityHolder) {
        this.pointValueQualityHolder = pointValueQualityHolder;
    }
    public PointValueQualityHolder getPointValueQualityHolder() {
        return pointValueQualityHolder;
    }
    public OutageActionType getActionType() {
        return actionType;
    }
    public void setActionType(OutageActionType actionType) {
        this.actionType = actionType;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    @Override
    public String toString() {
        return String.format("OutageJmsMessage [actionType=%s, paoIdentifier=%s, pointValueQualityHolder=%s, source=%s]",
                             actionType, paoIdentifier,
                             pointValueQualityHolder, source);
    }
}