package com.cannontech.message.notif;

import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.cannontech.messaging.message.notif.VoiceDataResponseMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_VoiceDataResponseMsg extends DefineCollectableMessage {
    //RogueWave classId
    public static final int MSG_ID = 709;

    public Object create(VirtualInputStream vstr) throws java.io.IOException {
        return new VoiceDataResponseMessage();
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
        return VoiceDataResponseMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        VoiceDataResponseMessage msg = (VoiceDataResponseMessage) obj;

        msg.setCallToken( (String) vstr.restoreObject(SimpleMappings.CString));
        msg.setXmlData ( (String) vstr.restoreObject(SimpleMappings.CString));
        msg.setContactId ( vstr.extractInt());
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
            CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        VoiceDataResponseMessage msg = (VoiceDataResponseMessage) obj;

        vstr.saveObject(msg.getCallToken(), SimpleMappings.CString);
        vstr.saveObject(msg.getXmlData(), SimpleMappings.CString);
        vstr.insertInt(msg.getContactId());

    }

}
