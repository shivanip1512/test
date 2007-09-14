package com.cannontech.loadcontrol.dynamic.receive;

import java.util.GregorianCalendar;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class CtiLMDynamicLMTriggerMsg implements com.roguewave.vsj.DefineCollectable {
    
	//The roguewave class id
	private static int CTILMDYNAMICLMTRIGGERDATAMSG_ID = 638;

    public CtiLMDynamicLMTriggerMsg() {
        super();
    }
    
    public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
    	return new LMTriggerChanged();
    }
    
    public com.roguewave.tools.v2_0.Comparator getComparator() {
    	return new Comparator() {
    		public int compare(Object x, Object y) {
    			return (((LMTriggerChanged)x).getPaoID().intValue() - ((LMTriggerChanged)y).getPaoID().intValue() );
    		}
    	};
    }
    
    public int getCxxClassId() {
    	return CtiLMDynamicLMTriggerMsg.CTILMDYNAMICLMTRIGGERDATAMSG_ID;
    }
    
    public String getCxxStringId() {
    	return DefineCollectable.NO_STRINGID;
    }
    
    public Class getJavaClass() {
    	return LMTriggerChanged.class;
    }
    
    public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
    	LMTriggerChanged lmTriggerChanged = (LMTriggerChanged) obj;
    	
    	lmTriggerChanged.setPaoID((int)vstr.extractUnsignedInt());
        lmTriggerChanged.setTriggerNumber((int)vstr.extractUnsignedInt());
        lmTriggerChanged.setPointValue(vstr.extractDouble());
        GregorianCalendar lastPointValueTimestamp = new GregorianCalendar();
        lastPointValueTimestamp.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmTriggerChanged.setLastPointValueTimestamp(lastPointValueTimestamp);
        lmTriggerChanged.setNormalState((int)vstr.extractUnsignedInt());
        lmTriggerChanged.setThreshold(vstr.extractDouble());
        lmTriggerChanged.setPeakPointValue(vstr.extractDouble());
        GregorianCalendar lastPeakPointValueTimestamp = new GregorianCalendar();
        lastPeakPointValueTimestamp.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
        lmTriggerChanged.setLastPeakPointValueTimestamp(lastPeakPointValueTimestamp);
        lmTriggerChanged.setProjectedPointValue(vstr.extractDouble());
    }
    
    public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {}
}
