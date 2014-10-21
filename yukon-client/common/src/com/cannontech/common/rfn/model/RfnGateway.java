package com.cannontech.common.rfn.model;

import java.util.Set;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.Locatable;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.AppMode;
import com.cannontech.common.rfn.message.gateway.DataSequence;
import com.cannontech.common.rfn.message.gateway.LastCommStatus;

public class RfnGateway extends RfnDevice implements Locatable {
    
    private RfnGatewayData gatewayData;
    private PaoLocation paoLocation;
    private String name;
    
    public RfnGateway(String name, YukonPao pao, RfnIdentifier rfnIdentifier, RfnGatewayData gatewayData) {
        super(pao, rfnIdentifier);
        this.gatewayData = gatewayData;
        this.name = name;
    }
    
    public RfnGatewayData getData() {
        return gatewayData;
    }
    
    public double getTotalCompletionPercentage() {
        if (gatewayData == null) {
            return 0.0;
        }
        
        Set<DataSequence> sequences = gatewayData.getSequences();
        int numberOfSequences = sequences.size();
        double sumOfCompletionPercentages = 0.0;
        
        for (DataSequence sequence : sequences) {
            sumOfCompletionPercentages += sequence.getCompletionPercentage();
        }
        return sumOfCompletionPercentages / numberOfSequences;
    }
    
    public boolean isTotalCompletionLevelWarning() {
        double totalCompletion = getTotalCompletionPercentage();
        return totalCompletion < 90 && totalCompletion >= 75;
    }
    
    public boolean isTotalCompletionLevelDanger() {
        double totalCompletion = getTotalCompletionPercentage();
        return totalCompletion < 75;
    }
    
    public boolean isLastCommFailed() {
        if (gatewayData == null) {
            return false;
        }
        return gatewayData.getLastCommStatus() == LastCommStatus.FAILED;
    }
    
    public boolean isLastCommMissed() {
        if (gatewayData == null) {
            return false;
        }
        return gatewayData.getLastCommStatus() == LastCommStatus.MISSED;
    }
    
    public boolean isLastCommUnknown() {
        if (gatewayData == null) {
            return false;
        }
        return gatewayData.getLastCommStatus() == LastCommStatus.UNKNOWN;
    }
    
    public boolean isAppModeNonNormal() {
        if (gatewayData == null) {
            return false;
        }
        return gatewayData.getMode() != AppMode.NORMAL;
    }
    
    @Override
    public PaoLocation getLocation() {
        return paoLocation;
    }
    
    public void setLocation(PaoLocation paoLocation) {
        this.paoLocation = paoLocation;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
