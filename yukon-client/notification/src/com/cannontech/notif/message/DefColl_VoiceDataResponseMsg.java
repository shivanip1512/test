package com.cannontech.notif.message;

import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.*;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_VoiceDataResponseMsg extends DefineCollectableMessage {
    //RogueWave classId
    public static final int MSG_ID = 709;

    public Object create(VirtualInputStream vstr) throws java.io.IOException {
        return new VoiceDataResponseMsg();
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

    public String getCxxStringId() //TODO this is the same as base class, is it needed?
    {
        return DefineCollectable.NO_STRINGID;
    }

    public Class getJavaClass() {
        return DefColl_VoiceDataResponseMsg.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        VoiceDataResponseMsg msg = (VoiceDataResponseMsg) obj;

        msg.token = (String) vstr.restoreObject(SimpleMappings.CString);
        msg.xmlData = (String) vstr.restoreObject(SimpleMappings.CString);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        VoiceDataResponseMsg msg = (VoiceDataResponseMsg) obj;

        vstr.saveObject(msg.token, SimpleMappings.CString);
        vstr.saveObject(msg.xmlData, SimpleMappings.CString);

    }

}
