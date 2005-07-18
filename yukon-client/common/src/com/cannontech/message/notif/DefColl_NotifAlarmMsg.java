package com.cannontech.message.notif;
import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.*;

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
        return NotifAlarmMsg.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        NotifAlarmMsg msg = (NotifAlarmMsg) obj;

        msg.notifGroupIds = new int[vstr.extractInt()];
        for(int i = 0; i < msg.notifGroupIds.length; i++) {
        	msg.notifGroupIds[i] = vstr.extractInt();
        }        
        msg.pointId = vstr.extractInt();
        msg.condition = vstr.extractInt();
        msg.value = vstr.extractDouble();
        msg.acknowledged = (vstr.extractInt() != 0);
        msg.abnormal = (vstr.extractInt()  != 0);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        NotifAlarmMsg msg = (NotifAlarmMsg) obj;

        vstr.insertInt(msg.notifGroupIds.length);
        for(int i = 0; i < msg.notifGroupIds.length; i++) {
        	vstr.insertInt(msg.notifGroupIds[i]);
        }        
        vstr.insertInt(msg.pointId);
        vstr.insertInt(msg.condition);
        vstr.insertDouble(msg.value);
        vstr.insertUnsignedInt(msg.acknowledged ? 1 : 0);
        vstr.insertUnsignedInt(msg.abnormal ? 1 : 0);
    }

}
