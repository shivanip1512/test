package com.cannontech.message.notif;
import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.cannontech.messaging.message.notif.AlarmMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_NotifAlarmMsg extends DefineCollectableMessage {
    // RogueWave classId
    public static final int MSG_ID = 706;

    public Object create(VirtualInputStream vstr) throws IOException {
        return new AlarmMessage();
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
        return AlarmMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        AlarmMessage msg = (AlarmMessage) obj;

        msg.setNotifGroupIds(new int[vstr.extractInt()]);
        for(int i = 0; i < msg.getNotifGroupIds().length; i++) {
        	msg.getNotifGroupIds()[i] = vstr.extractInt();
        }       
        msg.setAlarmCategoryId(vstr.extractInt());
        msg.setPointId(vstr.extractInt());
        msg.setCondition(vstr.extractInt());
        msg.setValue(vstr.extractDouble());
        msg.setAlarmTimestamp((java.util.Date) vstr.restoreObject( SimpleMappings.Time ));
        msg.setAcknowledged((vstr.extractInt() != 0));
        msg.setAbnormal((vstr.extractInt()  != 0));
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        AlarmMessage msg = (AlarmMessage) obj;

        vstr.insertInt(msg.getNotifGroupIds().length);
        for(int i = 0; i < msg.getNotifGroupIds().length; i++) {
        	vstr.insertInt(msg.getNotifGroupIds()[i]);
        }        
        vstr.insertInt(msg.getAlarmCategoryId());
        vstr.insertInt(msg.getPointId());
        vstr.insertInt(msg.getCondition());
        vstr.insertDouble(msg.getValue());
        vstr.saveObject( msg.getAlarmTimestamp(), SimpleMappings.Time );
        vstr.insertUnsignedInt(msg.isAcknowledged() ? 1 : 0);
        vstr.insertUnsignedInt(msg.isAbnormal() ? 1 : 0);
    }

}
