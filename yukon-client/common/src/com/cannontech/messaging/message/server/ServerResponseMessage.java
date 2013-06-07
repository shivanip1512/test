package com.cannontech.messaging.message.server;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.util.ServerRequest;

/**
 * ServerResponse is used to respond back to a particular client request with a status and a textual message as well as
 * a collectable payload. That might be some updated state info or whatever is relevant to the response to a request.
 * @author aaron
 */
public class ServerResponseMessage extends BaseMessage {

    // Possible values for status
    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_UNINIT = 2;

    public static final String[] STATUS_STRS = { "Success", "Error", "Uninitialized" };

    private int id;
    private int status;
    private String message;
    private Object payload;

    protected ServerResponseMessage(int id, int status, String message) {
        this(id);
        setStatus(status);
        setMessage(message);
    }

    public ServerResponseMessage(int id) {
        this.id = id;
    }

    public ServerResponseMessage() {}

    public static ServerResponseMessage createTimeoutResp() {
        return new ServerResponseMessage(CtiUtilities.NONE_ZERO_ID, STATUS_ERROR,
                                         "Response from the server took too long and timed out (Timeout= " +
                                             (ServerRequest.DEFAULT_TIMEOUT / 1000) + " seconds)");
    }

    public static String getStatusStr(int status) {
        return STATUS_STRS[status];
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Object getPayload() {
        return payload;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusStr() {
        return STATUS_STRS[getStatus()];
    }

    public void setId(int i) {
        id = i;
    }

    public void setMessage(String string) {
        message = string;
    }

    public void setPayload(Object object) {
        payload = object;
    }

    public void setStatus(int i) {
        status = i;
    }

    @Override
    public String toString() {
        ToStringCreator toStringCreator = new ToStringCreator(this);
        toStringCreator.append("id", id);
        toStringCreator.append("status", status);
        toStringCreator.append("payload", payload);
        return toStringCreator.toString();
    }
}
