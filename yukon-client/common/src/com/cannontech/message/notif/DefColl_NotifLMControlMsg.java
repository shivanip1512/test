package com.cannontech.message.notif;

import java.io.IOException;
import java.util.Date;

import com.cannontech.message.util.DefineCollectableMessage;
import com.cannontech.messaging.message.notif.ControlMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_NotifLMControlMsg extends DefineCollectableMessage {
    //RogueWave classId
    public static final int MSG_ID = 707;

    public Object create(VirtualInputStream vstr) throws java.io.IOException {
        return new ControlMessage();
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
        return ControlMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        ControlMessage msg = (ControlMessage) obj;

        msg.setNotifGroupIds(new int[vstr.extractInt()]);
        for(int i = 0; i < msg.getNotifGroupIds().length; i++) {
        	msg.getNotifGroupIds()[i] = vstr.extractInt();
        }
        msg.setNotifType(vstr.extractInt());
        msg.setProgramId(vstr.extractInt());
        msg.setStartTime((Date) vstr.restoreObject(SimpleMappings.Time));
        msg.setStopTime((Date) vstr.restoreObject(SimpleMappings.Time));        
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        ControlMessage msg = (ControlMessage) obj;

        vstr.insertInt(msg.getNotifGroupIds().length);
        for(int i = 0; i < msg.getNotifGroupIds().length; i++) {
        	vstr.insertInt(msg.getNotifGroupIds()[i]);
        }
        vstr.insertInt(msg.getNotifType());
        vstr.insertInt(msg.getProgramId());
        vstr.saveObject(msg.getStartTime(), SimpleMappings.Time);
        vstr.saveObject(msg.getStopTime(), SimpleMappings.Time);
    }
}
