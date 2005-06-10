package com.cannontech.message.notif;

import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.*;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_NotifCompletedMsg extends DefineCollectableMessage {
    // RogueWave classId
    public static final int MSG_ID = 705;

    public Object create(VirtualInputStream vstr) throws IOException {
        return new NotifCompletedMsg();
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
        return NotifCompletedMsg.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        NotifCompletedMsg msg = (NotifCompletedMsg) obj;

        msg.token = (String) vstr.restoreObject(SimpleMappings.CString);
        msg.gotConfirmation = 
            vstr.restoreObject(SimpleMappings.CString).equals("y");
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        NotifCompletedMsg msg = (NotifCompletedMsg) obj;

        vstr.saveObject(msg.token, SimpleMappings.CString);
        vstr.saveObject(msg.gotConfirmation ? "y" : "n", SimpleMappings.CString);

    }

}
