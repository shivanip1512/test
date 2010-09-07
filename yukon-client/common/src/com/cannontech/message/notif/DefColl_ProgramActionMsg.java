package com.cannontech.message.notif;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.cannontech.enums.EconomicEventAction;
import com.cannontech.message.util.DefineCollectableMessage;
import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.message.util.CollectionInserter;
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
        return new ProgramActionMsg();
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
        return ProgramActionMsg.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        ProgramActionMsg msg = (ProgramActionMsg) obj;
        
        msg.programId = vstr.extractInt();
        msg.eventDisplayName = (String) vstr.restoreObject(SimpleMappings.CString);
        msg.action = (String) vstr.restoreObject(SimpleMappings.CString);
        msg.startTime = (Date) vstr.restoreObject(SimpleMappings.Time);
        msg.stopTime = (Date) vstr.restoreObject(SimpleMappings.Time);
        msg.notificationTime = (Date) vstr.restoreObject(SimpleMappings.Time);
        msg.customerIds = CollectionExtracter.extractIntArray(vstr, polystr);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        ProgramActionMsg msg = (ProgramActionMsg) obj;

        vstr.insertInt(msg.programId);
        vstr.saveObject(msg.eventDisplayName, SimpleMappings.CString);
        vstr.saveObject(msg.action, SimpleMappings.CString );
        vstr.saveObject(msg.startTime, SimpleMappings.Time);
        vstr.saveObject(msg.stopTime, SimpleMappings.Time);
        vstr.saveObject(msg.notificationTime, SimpleMappings.Time);
        CollectionInserter.insertIntArray(msg.customerIds, vstr, polystr);
    }

}
