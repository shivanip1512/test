package com.cannontech.thirdparty.messaging.rw;

import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.cannontech.thirdparty.messaging.ControlHistoryMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableControlHistory extends DefineCollectableMessage implements DefineCollectable {

    //This is to match the C++ ID in common\include\collectable.h  #define MSG_LMCONTROLHISTORY 1598
    private final static int CTI_CONTROL_HISTORY_MESSAGE_ID = 1598;
    
    @Override
    public Object create(VirtualInputStream arg0) throws IOException {
        return new ControlHistoryMessage();
    }

    @Override
    public Comparator getComparator() {
        return new Comparator() {
            
            @Override
            public int compare(Object o1, Object o2) {
                Integer paoId1 = ((ControlHistoryMessage)o1).getPaoId();
                Integer paoId2 = ((ControlHistoryMessage)o2).getPaoId();
                return paoId1.compareTo(paoId2);
            }
        };
    }

    @Override
    public int getCxxClassId() {
        return CTI_CONTROL_HISTORY_MESSAGE_ID;
    }

    @Override
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getJavaClass() {
        return ControlHistoryMessage.class;
    }

    @Override
    public void restoreGuts(Object arg0, VirtualInputStream arg1, CollectableStreamer arg2)
            throws IOException {
        throw new UnsupportedOperationException("ControlHistoryMessage. Recieving is not supported.");
    }

    @Override
    public void saveGuts(Object obj, VirtualOutputStream outputStream, CollectableStreamer collectableStream)
            throws IOException {
        
        super.saveGuts(obj, outputStream, collectableStream);
        
        ControlHistoryMessage message = (ControlHistoryMessage) obj;
        
        outputStream.insertUnsignedInt(message.getPaoId());
        outputStream.insertUnsignedInt(message.getPointId());
        outputStream.insertInt(message.getRawState());
        outputStream.saveObject(message.getStartTime(),SimpleMappings.Time);
        outputStream.insertInt(message.getControlDuration());
        outputStream.insertInt(message.getReductionRatio());
        outputStream.saveObject(message.getControlType(),SimpleMappings.CString);
        outputStream.saveObject(message.getActiveRestore(),SimpleMappings.CString);
        outputStream.insertDouble(message.getReductionValue());
        outputStream.insertInt(message.getControlPriority());
        
    }

}
