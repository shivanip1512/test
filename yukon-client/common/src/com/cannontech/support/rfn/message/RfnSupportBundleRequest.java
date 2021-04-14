package com.cannontech.support.rfn.message;

import java.io.Serializable;

public class RfnSupportBundleRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private String fileName;
    private long fromTimestamp;
    private SupportBundleRequestType type;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFromTimestamp() {
        return fromTimestamp;
    }

    public void setFromTimestamp(long fromTimestamp) {
        this.fromTimestamp = fromTimestamp;
    }
    
    public SupportBundleRequestType getType() {
        return type;
    }

    public void setType(SupportBundleRequestType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + (int) (fromTimestamp ^ (fromTimestamp >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        RfnSupportBundleRequest other = (RfnSupportBundleRequest) obj;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (fromTimestamp != other.fromTimestamp)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RfnSupportBundleRequest [fileName=" + fileName + ", fromTimestamp=" + fromTimestamp + ", type=" + type
                + "]";
    }

}
