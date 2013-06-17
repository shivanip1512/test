package com.cannontech.message.notif;
import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefColl_CurtailmentEventDeleteMsg extends DefineCollectableMessage {
    // RogueWave classId
    public static final int MSG_ID = 711;

    public Object create(VirtualInputStream vstr) throws IOException {
        return new CurtailmentEventDeleteMsg();
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
        return CurtailmentEventDeleteMsg.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        CurtailmentEventDeleteMsg msg = (CurtailmentEventDeleteMsg) obj;

        msg.curtailmentEventId = vstr.extractInt();
        msg.deleteStart = vstr.extractInt() == 1;
        msg.deleteStop = vstr.extractInt() == 1;
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        CurtailmentEventDeleteMsg msg = (CurtailmentEventDeleteMsg) obj;

        vstr.insertInt(msg.curtailmentEventId);
        vstr.insertInt(msg.deleteStart ? 1 : 0);
        vstr.insertInt(msg.deleteStop ? 1 : 0);
    }

}
