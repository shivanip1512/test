package com.cannontech.message.notif;

import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.cannontech.messaging.message.notif.CallStatus;
import com.cannontech.messaging.message.notif.VoiceCompletedMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_NotifCompletedMsg extends DefineCollectableMessage {
    // RogueWave classId
    public static final int MSG_ID = 705;

    public Object create(VirtualInputStream vstr) throws IOException {
        return new VoiceCompletedMessage();
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
        return VoiceCompletedMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        VoiceCompletedMessage msg = (VoiceCompletedMessage) obj;

        msg.setCallToken ((String) vstr.restoreObject(SimpleMappings.CString));
        String callStateStr = (String) vstr.restoreObject(SimpleMappings.CString);
		msg.setCallStatus(CallStatus.valueOf(callStateStr));
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        VoiceCompletedMessage msg = (VoiceCompletedMessage) obj;

        vstr.saveObject(msg.getCallToken(), SimpleMappings.CString);
        vstr.saveObject(msg.getCallStatus().name(), SimpleMappings.CString);

    }

}
