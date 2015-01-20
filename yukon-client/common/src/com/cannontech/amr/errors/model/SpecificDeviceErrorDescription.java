package com.cannontech.amr.errors.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public final class SpecificDeviceErrorDescription {
    private final DeviceErrorDescription errorDescription;
    private final String resultString;
    private final MessageSourceResolvable summary;
    private final MessageSourceResolvable detail;

    /**
     * Constructs SpecificDeviceErrorDescription. Summary is constructed using
     * description and error code from errorDescription. resultString is set to
     * empty string if it is null.
     * 
     * @throws IllegalArgumentException
     *         if detail or errorDescription is null.
     */
    public SpecificDeviceErrorDescription(DeviceErrorDescription errorDescription, String resultString,
            MessageSourceResolvable detail) {
        this(errorDescription, resultString, YukonMessageSourceResolvable.createSingleCodeWithArguments(
            "yukon.common.device.errorSummary", errorDescription.getDescription(), errorDescription.getErrorCode()),
            detail);
    }

    /**
     * Constructs SpecificDeviceErrorDescription. Summary is constructed using
     * description and error code from errorDescription. resultString is set to
     * empty string.
     * 
     * @throws IllegalArgumentException if detail or errorDescription is null.
     */
    public SpecificDeviceErrorDescription(DeviceErrorDescription errorDescription, MessageSourceResolvable detail) {
        this(errorDescription, "", detail);
    }

    /**
     * Constructs SpecificDeviceErrorDescription. resultString is set to empty string.
     * 
     * @throws IllegalArgumentException if errorDescription, summary or detail is null.
     */
    public SpecificDeviceErrorDescription(DeviceErrorDescription errorDescription, MessageSourceResolvable summary,
            MessageSourceResolvable detail) {
        this(errorDescription, "", summary, detail);
    }

    /**
     * Constructs SpecificDeviceErrorDescription. resultString is set to empty
     * string if it is null.
     * 
     * @throws IllegalArgumentException if errorDescription, summary or detail is null.
     */
    private SpecificDeviceErrorDescription(DeviceErrorDescription errorDescription, String resultString,
            MessageSourceResolvable summary, MessageSourceResolvable detail) {

        if (errorDescription == null) {
            throw new IllegalArgumentException("errorDescription is null");
        }
        if (summary == null) {
            throw new IllegalArgumentException("summary is null");
        }

        this.errorDescription = errorDescription;
        if (StringUtils.isEmpty(resultString)) {
            this.resultString = "";
        } else {
            this.resultString = resultString;
        }
        this.summary = summary;
        this.detail = detail;
    }

    public Integer getErrorCode() {
        return errorDescription.getErrorCode();
    }

    public String getDescription() {
        return errorDescription.getDescription();
    }

    public String getTroubleshooting() {
        return errorDescription.getTroubleshooting();
    }

    public String getCategory() {
        return errorDescription.getCategory();
    }

    public String getPorter() {
        return resultString;
    }

    public MessageSourceResolvable getSummary() {
        return summary;
    }

    public MessageSourceResolvable getDetail() {
        return detail;
    }

    public DeviceError getDeviceError() {
        return errorDescription.getError();
    }

    @Override
    public String toString() {
        return errorDescription + "(" + resultString + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errorDescription == null) ? 0 : errorDescription.hashCode());
        result = prime * result + ((resultString == null) ? 0 : resultString.hashCode());
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
        SpecificDeviceErrorDescription other = (SpecificDeviceErrorDescription) obj;
        if (errorDescription == null) {
            if (other.errorDescription != null) {
                return false;
            }
        } else if (!errorDescription.equals(other.errorDescription)) {
            return false;
        }
        if (resultString == null) {
            if (other.resultString != null) {
                return false;
            }
        } else if (!resultString.equals(other.resultString)) {
            return false;
        }
        return true;
    }
}
