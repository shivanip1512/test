package com.cannontech.message.notif;
import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.cannontech.messaging.message.notif.EconomicEventDeleteMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefColl_EconomicEventDeleteMsg extends DefineCollectableMessage {
    // RogueWave classId
    public static final int MSG_ID = 713;

    public Object create(VirtualInputStream vstr) throws IOException {
        return new EconomicEventDeleteMessage();
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
        return EconomicEventDeleteMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        EconomicEventDeleteMessage msg = (EconomicEventDeleteMessage) obj;

        msg.setEconomicEventId (vstr.extractInt());
        msg.setDeleteStart (vstr.extractInt() == 1);
        msg.setDeleteStop (vstr.extractInt() == 1);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        EconomicEventDeleteMessage msg = (EconomicEventDeleteMessage) obj;

        vstr.insertInt(msg.getEconomicEventId());
        vstr.insertInt(msg.isDeleteStart() ? 1 : 0);
        vstr.insertInt(msg.isDeleteStop() ? 1 : 0);
    }

}
