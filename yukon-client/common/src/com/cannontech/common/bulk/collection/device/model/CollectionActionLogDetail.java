package com.cannontech.common.bulk.collection.device.model;

import org.joda.time.Instant;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dynamic.PointValueHolder;

public class CollectionActionLogDetail {
    private YukonPao pao;
    private CollectionActionDetail detail;
    private Instant time;
    private String executionExceptionText;
    private String deviceErrorText;
    private PointValueHolder value;
    
    public CollectionActionLogDetail(YukonPao pao, CollectionActionDetail detail) {
        this.pao = pao;
        this.detail = detail;
    }

    public CollectionActionLogDetail(YukonPao pao) {
        this.pao = pao;
    }

    public CollectionActionLogDetail(String executionExceptionText) {
        this.executionExceptionText = executionExceptionText;
    }

    public CollectionActionDetail getDetail() {
        return detail;
    }

    public void setDetail(CollectionActionDetail detail) {
        this.detail = detail;
    }

    public YukonPao getPao() {
        return pao;
    }

    public void setPao(YukonPao pao) {
        this.pao = pao;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getExecutionExceptionText() {
        return executionExceptionText;
    }

    public void setExecutionExceptionText(String executionExceptionText) {
        this.executionExceptionText = executionExceptionText;
    }

    public String getDeviceErrorText() {
        return deviceErrorText;
    }

    public void setDeviceErrorText(String deviceErrorText) {
        this.deviceErrorText = deviceErrorText;
    }

    public PointValueHolder getValue() {
        return value;
    }

    public void setValue(PointValueHolder value) {
        this.value = value;
    }   
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((detail == null) ? 0 : detail.hashCode());
        result = prime * result + ((deviceErrorText == null) ? 0 : deviceErrorText.hashCode());
        result = prime * result + ((executionExceptionText == null) ? 0 : executionExceptionText.hashCode());
        result = prime * result + ((pao == null) ? 0 : pao.getPaoIdentifier().getPaoId());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CollectionActionLogDetail other = (CollectionActionLogDetail) obj;
        if (detail != other.detail) {
            return false;
        }
        if (deviceErrorText == null) {
            if (other.deviceErrorText != null) {
                return false;
            }
        } else if (!deviceErrorText.equals(other.deviceErrorText)) {
            return false;
        }
        if (executionExceptionText == null) {
            if (other.executionExceptionText != null) {
                return false;
            }
        } else if (!executionExceptionText.equals(other.executionExceptionText)) {
            return false;
        }
        if (pao == null) {
            if (other.pao != null) {
                return false;
            }
        } else if (pao.getPaoIdentifier().getPaoId() != other.getPao().getPaoIdentifier().getPaoId()) {
            return false;
        }
        if (time == null) {
            if (other.time != null) {
                return false;
            }
        } else if (!time.equals(other.time)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
