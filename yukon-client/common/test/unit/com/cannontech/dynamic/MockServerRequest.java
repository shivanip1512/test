package com.cannontech.dynamic;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.yukon.IServerConnection;

public class MockServerRequest implements ServerRequest {

    List<Message> messagesWritten = new ArrayList<Message>();
    public ServerResponseMsg makeServerRequest(IServerConnection conn,
            Message msg) {
        messagesWritten.add(msg);
        return makeGoodResponse();
    }

    private ServerResponseMsg makeGoodResponse() {
        ServerResponseMsg srm = new ServerResponseMsg();
        srm.setStatus(ServerResponseMsg.STATUS_OK);
        return srm;
    }
    
    private ServerResponseMsg makeBadResponse() {
        ServerResponseMsg srm = new ServerResponseMsg();
        srm.setStatus(ServerResponseMsg.STATUS_ERROR);
        return srm;
    }
    public ServerResponseMsg makeServerRequest(IServerConnection conn,
            Message msg, long timeout) {
        // TODO Auto-generated method stub
        return null;
    }

    public void messageReceived(MessageEvent e) {
        // TODO Auto-generated method stub

    }

}
