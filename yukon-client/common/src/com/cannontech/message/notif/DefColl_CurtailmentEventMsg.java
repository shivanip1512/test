package com.cannontech.message.notif;
import java.io.IOException;

import com.cannontech.enums.CurtailmentEventAction;
import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefColl_CurtailmentEventMsg extends DefineCollectableMessage {
    // RogueWave classId
    public static final int MSG_ID = 710;

    public Object create(VirtualInputStream vstr) throws IOException {
        return new CurtailmentEventMsg();
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
        return CurtailmentEventMsg.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        CurtailmentEventMsg msg = (CurtailmentEventMsg) obj;

        msg.curtailmentEventId = vstr.extractInt();
        int actionOrdinal = vstr.extractInt();
        msg.action = CurtailmentEventAction.values()[actionOrdinal];
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        CurtailmentEventMsg msg = (CurtailmentEventMsg) obj;

        vstr.insertInt(msg.curtailmentEventId);
        vstr.insertInt(msg.action.ordinal());
    }

}
