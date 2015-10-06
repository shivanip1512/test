package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

/**
 * Message sent from Yukon to NM to request available version about an existing gateway.
 * JMS queue name: yukon.qr.obj.common.rfn.GatewayDataRequest
 */
public class RfnUpdateServerAvailableVersionRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private String updateServerUrl;

    /**
     * @return the updateServerUrl
     */
    public String getUpdateServerUrl() {
        return updateServerUrl;
    }

    /**
     * @param updateServerUrl the updateServerUrl to set
     */
    public void setUpdateServerUrl(String updateServerUrl) {
        this.updateServerUrl = updateServerUrl;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((updateServerUrl == null) ? 0 : updateServerUrl.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RfnUpdateServerAvailableVersionRequest)) {
            return false;
        }
        RfnUpdateServerAvailableVersionRequest other = (RfnUpdateServerAvailableVersionRequest) obj;
        if (updateServerUrl == null) {
            if (other.updateServerUrl != null) {
                return false;
            }
        } else if (!updateServerUrl.equals(other.updateServerUrl)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("RfnUpdateServerAvailableVersionRequest [updateServerUrl=%s]", updateServerUrl);
    }

}
