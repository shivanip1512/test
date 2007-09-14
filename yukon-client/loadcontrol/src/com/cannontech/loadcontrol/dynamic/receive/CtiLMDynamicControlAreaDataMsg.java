package com.cannontech.loadcontrol.dynamic.receive;

/**
 * Insert the type's description here.
 * Creation date: (9/5/07 3:06:09 PM)
 * @author: jdayton
 */
import java.util.ArrayList;
import java.util.List;

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class CtiLMDynamicControlAreaDataMsg implements com.roguewave.vsj.DefineCollectable {
    
	//The roguewave class id
	private static int CTILMDYNAMICCONTROLAREADATAMSG_ID = 637;

    public CtiLMDynamicControlAreaDataMsg() {
        super();
    }
    
    public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
    	return new LMControlAreaChanged();
    }
    
    public com.roguewave.tools.v2_0.Comparator getComparator() {
    	return new Comparator() {
    		public int compare(Object x, Object y) {
    			return (((LMControlAreaChanged)x).getPaoID().intValue() - ((LMControlAreaChanged)y).getPaoID().intValue() );
    		}
    	};
    }
    
    public int getCxxClassId() {
    	return CtiLMDynamicControlAreaDataMsg.CTILMDYNAMICCONTROLAREADATAMSG_ID;
    }
    
    public String getCxxStringId() {
    	return DefineCollectable.NO_STRINGID;
    }
    
    public Class getJavaClass() {
    	return LMControlAreaChanged.class;
    }
    
    public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
    	LMControlAreaChanged lmControlAreaChanged = (LMControlAreaChanged) obj;
    	
    	lmControlAreaChanged.setPaoID((int)vstr.extractUnsignedInt());
    	lmControlAreaChanged.setDisableFlag(vstr.extractUnsignedInt() > 0);
        java.util.GregorianCalendar nextCheckTime = new java.util.GregorianCalendar();
        nextCheckTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmControlAreaChanged.setNextCheckTime(nextCheckTime);
    	lmControlAreaChanged.setControlAreaState((int)vstr.extractUnsignedInt());
    	lmControlAreaChanged.setCurrentPriority((int)vstr.extractUnsignedInt());
    	lmControlAreaChanged.setCurrentDailyStartTime((int)vstr.extractUnsignedInt());
    	lmControlAreaChanged.setCurrentDailyStopTime((int)vstr.extractUnsignedInt());
        
        //deal with triggers
        int totalTriggers = (int) vstr.extractUnsignedInt();
        List<LMTriggerChanged> triggers = new ArrayList<LMTriggerChanged>(totalTriggers);
        for (int i = 0; i < totalTriggers; i++) {
            triggers.add((LMTriggerChanged)vstr.restoreObject(polystr));
        }
        lmControlAreaChanged.setTriggers(triggers);
    }
    
    public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {}
}
