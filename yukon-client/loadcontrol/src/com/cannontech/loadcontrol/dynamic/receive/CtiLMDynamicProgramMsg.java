package com.cannontech.loadcontrol.dynamic.receive;

/**
 * Insert the type's description here.
 * Creation date: (9/5/07 3:06:09 PM)
 * @author: jdayton
 */
import java.util.GregorianCalendar;

import com.cannontech.messaging.message.loadcontrol.dynamic.receive.ProgramChanged;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class CtiLMDynamicProgramMsg implements com.roguewave.vsj.DefineCollectable {
    
	//The roguewave class id
	private static int CTILMDYNAMICPROGRAMMSG_ID = 635;

    public CtiLMDynamicProgramMsg() {
        super();
    }
    
    public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
    	return new ProgramChanged();
    }
    
    public com.roguewave.tools.v2_0.Comparator getComparator() {
    	return new Comparator() {
    		public int compare(Object x, Object y) {
    			return (((ProgramChanged)x).getPaoId() - ((ProgramChanged)y).getPaoId() );
    		}
    	};
    }
    
    public int getCxxClassId() {
    	return CtiLMDynamicProgramMsg.CTILMDYNAMICPROGRAMMSG_ID;
    }
    
    public String getCxxStringId() {
    	return DefineCollectable.NO_STRINGID;
    }
    
    public Class getJavaClass() {
    	return ProgramChanged.class;
    }
    
    public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
    	ProgramChanged lmProgramChanged = (ProgramChanged) obj;
    	
    	lmProgramChanged.setPaoId((int)vstr.extractUnsignedInt());
    	lmProgramChanged.setDisableFlag(vstr.extractUnsignedInt() > 0);
        lmProgramChanged.setCurrentGearNumber((int)vstr.extractUnsignedInt());
        lmProgramChanged.setLastGroupControlled((int)vstr.extractUnsignedInt());
        lmProgramChanged.setProgramState((int)vstr.extractUnsignedInt());
        lmProgramChanged.setReductionTotal(vstr.extractDouble());
        GregorianCalendar directStartTime = new GregorianCalendar();
        directStartTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmProgramChanged.setDirectStartTime(directStartTime);
        GregorianCalendar directStopTime = new GregorianCalendar();
        directStopTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmProgramChanged.setDirectStopTime(directStopTime);
        GregorianCalendar notifyActiveTime = new GregorianCalendar();
        notifyActiveTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmProgramChanged.setNotifyActiveTime(notifyActiveTime);
        GregorianCalendar notifyInactiveTime = new GregorianCalendar();
        notifyInactiveTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmProgramChanged.setNotifyInactiveTime(notifyInactiveTime);
        GregorianCalendar startedRampingOutTime = new GregorianCalendar();
        startedRampingOutTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmProgramChanged.setStartedRampingOutTime(startedRampingOutTime);
    }
    
    public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {}
}
