package com.cannontech.support.rfn.message;

import java.io.Serializable;

public class RfnSupportBundleResponse implements Serializable {

private static final long serialVersionUID = 1L;
    
    private RfnSupportBundleResponseType responseType;
    private String token;

    public RfnSupportBundleResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(RfnSupportBundleResponseType responseType) {
        this.responseType = responseType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((responseType == null) ? 0 : responseType.hashCode());
        result = prime * result + ((token == null) ? 0 : token.hashCode());
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
        RfnSupportBundleResponse other = (RfnSupportBundleResponse) obj;
        if (responseType != other.responseType)
            return false;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RfnSupportBundleResponse [responseType=" + responseType + ", token=" + token + "]";
    }
}
