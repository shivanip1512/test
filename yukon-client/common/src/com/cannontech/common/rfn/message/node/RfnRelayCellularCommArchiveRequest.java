package com.cannontech.common.rfn.message.node;

import java.io.Serializable;
import java.util.Map;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.RfnRelayCellularCommArchiveRequest
 */
public class RfnRelayCellularCommArchiveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // Map referenceID to relayCellularComm
    private Map<Long, RelayCellularComm> relayCellularComms;

    public Map<Long, RelayCellularComm> getRelayCellularComms() {
        return relayCellularComms;
    }

    public void setRelayCellularComms(Map<Long, RelayCellularComm> relayCellularComms) {
        this.relayCellularComms = relayCellularComms;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((relayCellularComms == null) ? 0 : relayCellularComms.hashCode());
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
        RfnRelayCellularCommArchiveRequest other = (RfnRelayCellularCommArchiveRequest) obj;
        if (relayCellularComms == null) {
            if (other.relayCellularComms != null)
                return false;
        } else if (!relayCellularComms.equals(other.relayCellularComms))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnRelayCellularCommArchiveRequest [relayCellularComms=%s]",
                                                                  relayCellularComms);
    }
}