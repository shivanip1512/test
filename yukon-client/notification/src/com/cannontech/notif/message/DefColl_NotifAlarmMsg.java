package com.cannontech.notif.message;

import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.*;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_NotifAlarmMsg extends DefineCollectableMessage {
    // RogueWave classId
    public static final int MSG_ID = 706;

    public Object create(VirtualInputStream vstr) throws IOException {
        return new NotifAlarmMsg();
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
        return DefColl_NotifAlarmMsg.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        NotifAlarmMsg msg = (NotifAlarmMsg) obj;

        msg.pointId = vstr.extractInt();
        msg.condition = vstr.extractInt();
        msg.value = vstr.extractDouble();
        msg.acknowledged = 
            vstr.restoreObject(SimpleMappings.CString).equals("y");
        msg.abnormal = vstr.restoreObject(SimpleMappings.CString).equals("y");
        msg.categoryId = vstr.extractInt();
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        NotifAlarmMsg msg = (NotifAlarmMsg) obj;

        vstr.insertInt(msg.pointId);
        vstr.insertInt(msg.condition);
        vstr.insertDouble(msg.value);
        vstr.saveObject(msg.acknowledged ? "y" : "n", SimpleMappings.CString);
        vstr.saveObject(msg.abnormal ? "y" : "n", SimpleMappings.CString);
        vstr.insertInt(msg.categoryId);

    }

}
