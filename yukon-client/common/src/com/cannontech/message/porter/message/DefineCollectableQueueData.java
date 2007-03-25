package com.cannontech.message.porter.message;

import java.io.IOException;
import java.util.Date;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableQueueData extends DefineCollectableMessage {
    private static final int classId = 1650;
    /**
     * DefineCollectableRequest constructor comment.
     */
    public DefineCollectableQueueData() {
        super();
    }

    public Object create(VirtualInputStream vstr) throws java.io.IOException {
        return new Request();
    }

    public int getCxxClassId() {
        return classId;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }

    public Class getJavaClass() {
        return Request.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException 
    {
        QueueData req = (QueueData) obj;
        
        req.setQueueId(vstr.extractLong());
        req.setQueueCount(vstr.extractUnsignedInt());
        req.setRate(vstr.extractUnsignedInt());
        req.setRequestId(vstr.extractLong());
        req.setRequestIdCount(vstr.extractUnsignedInt());
        req.setTime((Date) vstr.restoreObject(SimpleMappings.Time));
        req.setUserMessageId(vstr.extractLong());
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException 
    {
        super.saveGuts(obj, vstr, polystr );

        QueueData req = (QueueData) obj;

        vstr.insertLong(req.getQueueId());
        vstr.insertUnsignedInt(req.getQueueCount());
        vstr.insertUnsignedInt(req.getRate());
        vstr.insertLong(req.getRequestId());
        vstr.insertUnsignedInt(req.getRequestIdCount());
        vstr.saveObject(req.getTime(), SimpleMappings.Time);
        vstr.insertLong(req.getUserMessageId());
    }
}

