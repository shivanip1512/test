package com.cannontech.amr.rfn.message.event;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * For status code meanings, refer to <a href=
 * "http://teams.etn.com/es/EASEngineering/EAS%20Engineering%20Documents/End%20Point%20Automation/Network%20Communications%20Software/Releases/Release%209.2/Design/RemoteMeterConfigNodeFirmwareDesign.docx">RemoteMeterConfigNodeFirmwareDesign.docx</a>
 * Table 27 - Detailed Configuration Status Codes. Any unsupported codes are mapped to
 * {@link Status#OTHER}.
 */
public class DetailedConfigurationStatusCode implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {
        SUCCESS((short) 0),
        REASON_UNKNOWN((short) 1),
        SERVICE_NOT_SUPPORTED((short) 2),
        INSUFFICIENT_SECURITY_CLEARANCE((short) 3),
        OPERATION_NOT_POSSIBLE((short) 4),
        INAPPROPRIATE_ACTION_REQUESTED((short) 5),
        DEVICE_BUSY((short) 6),
        DATA_NOT_READY((short) 7),
        DATA_LOCKED((short) 8),
        RENEGOTIATE_REQUEST((short) 9),
        INVALID_SERVICE_SEQUENCE_STATE((short) 10),
        DOWNLOAD_ABORTED((short) 32),
        FILE_TOO_LARGE((short) 33),
        CONFIGURATION_IN_PROGRESS((short) 34),
        UNABLE_TO_GET_FILE((short) 35),
        FILE_EXPIRED((short) 37),
        FAILED_REQUIREMENTS((short) 38),
        MALFORMED_RECORD_IN_CONFIGURATION_FILE((short) 39),
        VERIFICATION_FAILED((short) 40),
        WRITE_KEY_FAILED((short) 41),
        CATASTROPHIC_FAILURE_FULL_REPROGRAM_REQUIRED((short) 42),
        OTHER,
        ;

        private static final Map<Short, Status> lookup = Arrays.stream(Status.values())
            .filter(s -> s.getCode() != null)
            .collect(Collectors.toMap(Status::getCode, Function.identity()));

        private Short code;

        private Status(Short code) {
            this.code = code;
        }

        private Status() {
            code = null;
        }

        private Short getCode() {
            return code;
        }

        public static Status valueOf(short code) {
            return lookup.getOrDefault(code, OTHER);
        }
    }

    private final Status status;
    private final short code;

    public DetailedConfigurationStatusCode(short code) {
        this.status = Status.valueOf(code);
        this.code = code;
    }

    public Status getStatus() {
        return status;
    }

    public short getCode() {
        return code;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + code;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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
        DetailedConfigurationStatusCode other = (DetailedConfigurationStatusCode) obj;
        if (code != other.code)
            return false;
        if (status != other.status)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("DetailedConfigurationStatusCode [status=%s, code=%s]", status, code);
    }
}
