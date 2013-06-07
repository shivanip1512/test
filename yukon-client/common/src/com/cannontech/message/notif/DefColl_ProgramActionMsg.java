package com.cannontech.message.notif;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.cannontech.enums.EconomicEventAction;
import com.cannontech.message.util.DefineCollectableMessage;
import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.message.util.CollectionInserter;
import com.cannontech.messaging.message.notif.ProgramActionMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_ProgramActionMsg extends DefineCollectableMessage {
    // RogueWave classId
    public static final int MSG_ID = 714;

    public Object create(VirtualInputStream vstr) throws IOException {
        return new ProgramActionMessage();
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
        return ProgramActionMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        ProgramActionMessage msg = (ProgramActionMessage) obj;
        
        msg.setProgramId(vstr.extractInt());
        msg.setEventDisplayName ( (String) vstr.restoreObject(SimpleMappings.CString));
        msg.setAction ( (String) vstr.restoreObject(SimpleMappings.CString));
        msg.setStartTime ( (Date) vstr.restoreObject(SimpleMappings.Time));
        msg.setStopTime ((Date) vstr.restoreObject(SimpleMappings.Time));
        msg.setNotificationTime ((Date) vstr.restoreObject(SimpleMappings.Time));
        msg.setCustomerIds (CollectionExtracter.extractIntArray(vstr, polystr));
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        ProgramActionMessage msg = (ProgramActionMessage) obj;

        vstr.insertInt(msg.getProgramId());
        vstr.saveObject(msg.getEventDisplayName(), SimpleMappings.CString);
        vstr.saveObject(msg.getAction(), SimpleMappings.CString );
        vstr.saveObject(msg.getStartTime(), SimpleMappings.Time);
        vstr.saveObject(msg.getStopTime(), SimpleMappings.Time);
        vstr.saveObject(msg.getNotificationTime(), SimpleMappings.Time);
        CollectionInserter.insertIntArray(msg.getCustomerIds(), vstr, polystr);
    }

}
