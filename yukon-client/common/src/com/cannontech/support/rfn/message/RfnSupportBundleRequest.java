package com.cannontech.support.rfn.message;

import java.io.Serializable;

public class RfnSupportBundleRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private String fileName;
    private long fromTimestamp;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + (int) (fromTimestamp ^ (fromTimestamp >>> 32));
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
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnSupportBundleRequest [fileName=%s, fromTimestamp=s%]", fileName, fromTimestamp);
    }
}
