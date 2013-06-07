package com.cannontech.message.notif;

import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.cannontech.messaging.message.notif.VoiceDataRequestMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_VoiceDataRequestMsg extends DefineCollectableMessage {
    //RogueWave classId
    public static final int MSG_ID = 708;

    public Object create(VirtualInputStream vstr) throws java.io.IOException {
        return new VoiceDataRequestMessage();
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
        return VoiceDataRequestMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        VoiceDataRequestMessage msg = (VoiceDataRequestMessage) obj;

        msg.setCallToken ((String) vstr.restoreObject(SimpleMappings.CString));
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        VoiceDataRequestMessage msg = (VoiceDataRequestMessage) obj;

        vstr.saveObject(msg.getCallToken(), SimpleMappings.CString);

    }

}
