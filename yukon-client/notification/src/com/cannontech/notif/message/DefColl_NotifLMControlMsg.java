package com.cannontech.notif.message;

import java.io.IOException;
import java.util.Date;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.*;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_NotifLMControlMsg extends DefineCollectableMessage {
    //RogueWave classId
    public static final int MSG_ID = 707;

    public Object create(VirtualInputStream vstr) throws java.io.IOException {
        return new NotifLMControlMsg();
    }

    public Comparator getComparator() {
        return new Comparator() {
            public int compare(Object x, Object y) {
                if (x == y) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };
    }

    public int getCxxClassId() {
        return MSG_ID;
    }

    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }

    public Class getJavaClass() {
        return DefColl_NotifLMControlMsg.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        NotifLMControlMsg msg = (NotifLMControlMsg) obj;

        msg.notifGroupIds = new int[vstr.extractInt()];
        for(int i = 0; i < msg.notifGroupIds.length; i++) {
        	msg.notifGroupIds[i] = vstr.extractInt();
        }
        msg.notifType = vstr.extractInt();
        msg.programId = vstr.extractInt();
        msg.startTime = (Date) vstr.restoreObject(SimpleMappings.Time);
        msg.stopTime = (Date) vstr.restoreObject(SimpleMappings.Time);        
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        NotifLMControlMsg msg = (NotifLMControlMsg) obj;

        vstr.insertInt(msg.notifGroupIds.length);
        for(int i = 0; i < msg.notifGroupIds.length; i++) {
        	vstr.insertInt(msg.notifGroupIds[i]);
        }
        vstr.insertInt(msg.notifType);
        vstr.insertInt(msg.programId);
        vstr.saveObject(msg.startTime, SimpleMappings.Time);
        vstr.saveObject(msg.stopTime, SimpleMappings.Time);
    }
}
