package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

/**
 * Message sent from Yukon to NM to request available version of update server for an existing gateway.
 * JMS queue name: yukon.qr.obj.common.rfn.UpdateServerAvailableVersionRequest
 */
public class RfnUpdateServerAvailableVersionRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private String updateServerUrl;
    private Authentication updateServerLogin;

    /**
     * @return the updateServerLogin
     */
    public Authentication getUpdateServerLogin() {
        return updateServerLogin;
    }

    /**
     * @param updateServerLogin the updateServerLogin to set
     */
    public void setUpdateServerLogin(Authentication updateServerLogin) {
        this.updateServerLogin = updateServerLogin;
    }

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
        result = prime * result + ((updateServerLogin == null) ? 0 : updateServerLogin.hashCode());
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
        if (updateServerLogin == null) {
            if (other.updateServerLogin != null) {
                return false;
            }
        } else if (!updateServerLogin.equals(other.updateServerLogin)) {
            return false;
        }
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
        return "RfnUpdateServerAvailableVersionRequest [updateServerUrl=" + updateServerUrl + ", updateServerLogin="
            + updateServerLogin + "]";
    }

}
