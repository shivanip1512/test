package com.cannontech.message.notif;
import java.io.IOException;

import com.cannontech.enums.EconomicEventAction;
import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefColl_EconomicEventMsg extends DefineCollectableMessage {
    // RogueWave classId
    public static final int MSG_ID = 712;

    public Object create(VirtualInputStream vstr) throws IOException {
        return new EconomicEventMsg();
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
        return EconomicEventMsg.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        EconomicEventMsg msg = (EconomicEventMsg) obj;

        msg.economicEventId = vstr.extractInt();
        msg.revisionNumber = vstr.extractInt();
        int actionOrdinal = vstr.extractInt();
        msg.action = EconomicEventAction.values()[actionOrdinal];
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        EconomicEventMsg msg = (EconomicEventMsg) obj;

        vstr.insertInt(msg.economicEventId);
        vstr.insertInt(msg.revisionNumber);
        vstr.insertInt(msg.action.ordinal());
    }

}
