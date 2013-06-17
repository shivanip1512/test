package com.cannontech.message.porter.message;

import java.io.IOException;
import java.util.Date;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableRequestCancel extends DefineCollectableMessage {
    private static final int classId = 1651;
    /**
     * DefineCollectableRequest constructor comment.
     */
    public DefineCollectableRequestCancel() {
        super();
    }

    public Object create(VirtualInputStream vstr) throws java.io.IOException {
        return new RequestCancel();
    }

    public int getCxxClassId() {
        return classId;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }

    public Class getJavaClass() {
        return RequestCancel.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException 
    {
        super.restoreGuts( obj, vstr, polystr );
        
        RequestCancel req = (RequestCancel) obj;
        
        req.setRequestId(vstr.extractLong());
        req.setRequestIdCount(vstr.extractUnsignedInt());
        req.setTime((Date) vstr.restoreObject(SimpleMappings.Time));
        req.setUserMessageId(vstr.extractLong());
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException 
    {
        super.saveGuts(obj, vstr, polystr );

        RequestCancel req = (RequestCancel) obj;

        vstr.insertLong(req.getRequestId());
        vstr.insertUnsignedInt(req.getRequestIdCount());
        vstr.saveObject(req.getTime(), SimpleMappings.Time);
        vstr.insertLong(req.getUserMessageId());
    }
}

