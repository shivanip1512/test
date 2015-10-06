package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

/**
 * Message sent from NM to Yukon on a temporary queue as a result of a
 * {@link RfnUpdateServerAvailableVersionRequest}
 */
public class RfnUpdateServerAvailableVersionResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String updateServerURL;
    private String availableVersion;
    public RfnUpdateServerAvailableVersionResult result;

    /**
     * @return the updateServerURL
     */
    public String getUpdateServerURL() {
        return updateServerURL;
    }

    /**
     * @param updateServerURL the updateServerURL to set
     */
    public void setUpdateServerURL(String updateServerURL) {
        this.updateServerURL = updateServerURL;
    }

    /**
     * @return the releaseVersion
     */
    public String getAvailableVersion() {
        return availableVersion;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((availableVersion == null) ? 0 : availableVersion.hashCode());
        result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
        result = prime * result + ((updateServerURL == null) ? 0 : updateServerURL.hashCode());
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
        if (!(obj instanceof RfnUpdateServerAvailableVersionResponse)) {
            return false;
        }
        RfnUpdateServerAvailableVersionResponse other = (RfnUpdateServerAvailableVersionResponse) obj;
        if (availableVersion == null) {
            if (other.availableVersion != null) {
                return false;
            }
        } else if (!availableVersion.equals(other.availableVersion)) {
            return false;
        }
        if (result != other.result) {
            return false;
        }
        if (updateServerURL == null) {
            if (other.updateServerURL != null) {
                return false;
            }
        } else if (!updateServerURL.equals(other.updateServerURL)) {
            return false;
        }
        return true;
    }

    /**
     * @param releaseVersion the releaseVersion to set
     */
    public void setAvailableVersion(String availableVersion) {
        this.availableVersion = availableVersion;
    }

    /**
     * @return the result
     */
    public RfnUpdateServerAvailableVersionResult getResult() {
        return result;
    }

    /**
     * @param result the rfnUpdateServerReleaseVersionResult to set
     */
    public void setResult(RfnUpdateServerAvailableVersionResult result) {
        this.result = result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(
            "RfnUpdateServerAvailableVersionResponse [result=%s, updateServerURL=%s, availableVersion=%s]", result,
            updateServerURL, availableVersion);
    }
}
