package com.cannontech.loadcontrol.dynamic.receive;

/**
 * Insert the type's description here.
 * Creation date: (9/5/07 3:06:09 PM)
 * @author: jdayton
 */

import java.util.GregorianCalendar;

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class CtiLMDynamicGroupMsg implements com.roguewave.vsj.DefineCollectable {
    
	//The roguewave class id
	private static int CTILMDYNAMICGROUPMSG_ID = 636;

    public CtiLMDynamicGroupMsg() {
        super();
    }
    
    public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
    	return new LMGroupChanged();
    }
    
    public com.roguewave.tools.v2_0.Comparator getComparator() {
    	return new Comparator() {
    		public int compare(Object x, Object y) {
    			return (((LMGroupChanged)x).getPaoID().intValue() - ((LMGroupChanged)y).getPaoID().intValue() );
    		}
    	};
    }
    
    public int getCxxClassId() {
    	return CtiLMDynamicGroupMsg.CTILMDYNAMICGROUPMSG_ID;
    }
    
    public String getCxxStringId() {
    	return DefineCollectable.NO_STRINGID;
    }
    
    public Class getJavaClass() {
    	return LMGroupChanged.class;
    }
    
    public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
    	LMGroupChanged lmGroupChanged = (LMGroupChanged) obj;
    	
    	lmGroupChanged.setPaoID((int)vstr.extractUnsignedInt());
    	lmGroupChanged.setDisableFlag(vstr.extractUnsignedInt() > 0);
        lmGroupChanged.setGroupControlState((int)vstr.extractUnsignedInt());
        lmGroupChanged.setCurrentHoursDaily((int)vstr.extractUnsignedInt());
        lmGroupChanged.setCurrentHoursMonthly((int)vstr.extractUnsignedInt());
        lmGroupChanged.setCurrentHoursSeasonal((int)vstr.extractUnsignedInt());
        lmGroupChanged.setCurrentHoursAnnually((int)vstr.extractUnsignedInt());
        
        GregorianCalendar lastControlSent = new GregorianCalendar();
        lastControlSent.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmGroupChanged.setLastControlSent(lastControlSent);
        GregorianCalendar controlStartTime = new GregorianCalendar();
        controlStartTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmGroupChanged.setControlStartTime(controlStartTime);
        GregorianCalendar controlCompleteTime = new GregorianCalendar();
        controlCompleteTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmGroupChanged.setControlCompleteTime(controlCompleteTime);
        GregorianCalendar nextControlTime = new GregorianCalendar();
        nextControlTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmGroupChanged.setNextControlTime(nextControlTime);
        lmGroupChanged.setInternalState((int)vstr.extractUnsignedInt());
        lmGroupChanged.setDailyOps((int)vstr.extractUnsignedInt());
        
    }
    
    public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {}
}
