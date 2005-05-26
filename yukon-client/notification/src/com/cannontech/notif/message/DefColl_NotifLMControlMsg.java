package com.cannontech.notif.message;

import java.io.IOException;
import java.util.Date;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.*;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_NotifLMControlMsg extends DefineCollectableMessage {
    //RogueWave classId
    public static final int MSG_ID = 704; //TODO what is my number?

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

        msg.programId = vstr.extractInt();
        msg.startTime = (Date) vstr.restoreObject(SimpleMappings.Date);
        msg.durration = vstr.extractInt();
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        NotifLMControlMsg msg = (NotifLMControlMsg) obj;

        vstr.insertInt(msg.programId);
        vstr.saveObject(msg.startTime, SimpleMappings.Date);
        vstr.insertInt(msg.durration);

    }

}
