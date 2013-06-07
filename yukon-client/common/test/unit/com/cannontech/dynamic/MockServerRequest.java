package com.cannontech.dynamic;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.messaging.util.MessageEvent;
import com.cannontech.messaging.util.ServerRequest;
import com.cannontech.yukon.IServerConnection;

public class MockServerRequest implements ServerRequest {

    List<BaseMessage> messagesWritten = new ArrayList<BaseMessage>();
    public ServerResponseMessage makeServerRequest(IServerConnection conn,
            BaseMessage msg) {
        messagesWritten.add(msg);
        return makeGoodResponse();
    }

    private ServerResponseMessage makeGoodResponse() {
        ServerResponseMessage srm = new ServerResponseMessage();
        srm.setStatus(ServerResponseMessage.STATUS_OK);
        return srm;
    }
    
    private ServerResponseMessage makeBadResponse() {
        ServerResponseMessage srm = new ServerResponseMessage();
        srm.setStatus(ServerResponseMessage.STATUS_ERROR);
        return srm;
    }
    public ServerResponseMessage makeServerRequest(IServerConnection conn,
            BaseMessage msg, long timeout) {
        // TODO Auto-generated method stub
        return null;
    }

    public void messageReceived(MessageEvent e) {
        // TODO Auto-generated method stub

    }

}
