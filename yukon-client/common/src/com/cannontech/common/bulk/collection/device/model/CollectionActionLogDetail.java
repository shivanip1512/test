package com.cannontech.common.bulk.collection.device.model;

import org.joda.time.Instant;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;

/**
 * Represents collection action log entry, as written to a file cacheKey.csv located in
 * \Yukon\ExportArchive\CollectionAction
 */
public class CollectionActionLogDetail {
    private SimpleDevice device;
    private CollectionActionDetail detail;
    private Instant time;
    private String executionExceptionText;
    private String deviceErrorText;
    private String lastValue;
    private SimplePointValue value;
    private String configName;
    
    public CollectionActionLogDetail(YukonPao pao, CollectionActionDetail detail, String deviceErrorText) {
        this(pao, detail);
        this.deviceErrorText = deviceErrorText;
    }

    public CollectionActionLogDetail(YukonPao pao, CollectionActionDetail detail) {
        this.device = new SimpleDevice(pao);
        this.detail = detail;
    }

    public CollectionActionLogDetail(YukonPao pao) {
        this.device = new SimpleDevice(pao);
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

    public SimplePointValue getValue() {
        return value;
    }

    public void setValue(PointValueHolder valueHolder) {
        this.value = new SimplePointValue(valueHolder);
    }

    public SimpleDevice getDevice() {
        return device;
    }

    public String getLastValue() {
        return lastValue;
    }

    public void setLastValue(String lastValue) {
        this.lastValue = lastValue;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((detail == null) ? 0 : detail.hashCode());
        result = prime * result + ((device == null) ? 0 : device.hashCode());
        result = prime * result + ((deviceErrorText == null) ? 0 : deviceErrorText.hashCode());
        result = prime * result + ((executionExceptionText == null) ? 0 : executionExceptionText.hashCode());
        result = prime * result + ((lastValue == null) ? 0 : lastValue.hashCode());
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
        if (device == null) {
            if (other.device != null) {
                return false;
            }
        } else if (!device.equals(other.device)) {
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
        if (lastValue == null) {
            if (other.lastValue != null) {
                return false;
            }
        } else if (!lastValue.equals(other.lastValue)) {
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
