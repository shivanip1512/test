package com.cannontech.common.rfn.model;

import java.util.Set;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.Locatable;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.AppMode;
import com.cannontech.common.rfn.message.gateway.DataSequence;
import com.cannontech.common.rfn.message.gateway.LastCommStatus;

public class RfnGateway extends RfnDevice implements Locatable, Comparable<RfnGateway> {
    
    private RfnGatewayData data;
    private PaoLocation paoLocation;
    
    public RfnGateway(String name, YukonPao pao, RfnIdentifier rfnIdentifier, RfnGatewayData data) {
        super(name, pao, rfnIdentifier);
        this.data = data;
    }
    
    public RfnGatewayData getData() {
        return data;
    }
    
    public void setData(RfnGatewayData data) {
        this.data = data;
    }
    
    public double getTotalCompletionPercentage() {
        if (data == null) {
            return 0.0;
        }
        
        Set<DataSequence> sequences = data.getSequences();
        int numberOfSequences = sequences.size();
        
        if (numberOfSequences == 0) {
            return 0.0;
        }
        
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
        if (data == null) {
            return false;
        }
        return data.getLastCommStatus() == LastCommStatus.FAILED;
    }
    
    public boolean isLastCommMissed() {
        if (data == null) {
            return false;
        }
        return data.getLastCommStatus() == LastCommStatus.MISSED;
    }
    
    public boolean isLastCommUnknown() {
        if (data == null) {
            return false;
        }
        return data.getLastCommStatus() == LastCommStatus.UNKNOWN;
    }
    
    public boolean isAppModeNonNormal() {
        if (data == null) {
            return false;
        }
        return data.getMode() != AppMode.NORMAL;
    }
    
    @Override
    public PaoLocation getLocation() {
        return paoLocation;
    }
    
    public void setLocation(PaoLocation paoLocation) {
        this.paoLocation = paoLocation;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((paoLocation == null) ? 0 : paoLocation.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RfnGateway other = (RfnGateway) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (paoLocation == null) {
            if (other.paoLocation != null) {
                return false;
            }
        } else if (!paoLocation.equals(other.paoLocation)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(RfnGateway o) {
        return name.compareToIgnoreCase(o.getName());
    }
    
    public RfnGateway withUpdateServer(GatewayUpdateServer updateServer) {

        RfnGatewayData.Builder builder = new RfnGatewayData.Builder();

        RfnGatewayData data = builder.copyOf(getData())
            .updateServerUrl(updateServer.getUpdateServerUrl())
            .updateServerLogin(updateServer.getUpdateServerLogin())
            .build();

        setData(data);

        return this;
    }
}