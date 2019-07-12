package com.cannontech.amr.rfn.message.event;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * For status code meanings, refer to <a href=
 * "http://teams.etn.com/es/EASEngineering/EAS%20Engineering%20Documents/End%20Point%20Automation/Network%20Communications%20Software/Releases/Release%209.2/Design/RemoteMeterConfigNodeFirmwareDesign.docx">RemoteMeterConfigNodeFirmwareDesign.docx</a>
 * Table 26 - Meter Status Codes. Any unsupported codes are mapped to {@link Status#OTHER}.
 */
public class MeterStatusCode implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {
        CONFIGURED((short) 0),
        UNCHANGED((short) 1),
        CHANGED((short) 2),
        BRICKED((short) 3),
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

    public MeterStatusCode(short code) {
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
        MeterStatusCode other = (MeterStatusCode) obj;
        if (code != other.code)
            return false;
        if (status != other.status)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("MeterStatusCode [status=%s, code=%s]", status, code);
    }
}
