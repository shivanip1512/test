package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;

import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableStreamableCapObject implements DefineCollectable {

    public DefineCollectableStreamableCapObject() {
        super();
    }
    
    public Object create(VirtualInputStream vstr) throws IOException {
        return null;
    }
    
    public Comparator getComparator() {
        return new Comparator() {
            public int compare(Object x, Object y) {
                return (((StreamableCapObject)x).getCcId().intValue() - ((StreamableCapObject)y).getCcId().intValue() );
            }
        };
    }
    
    public int getCxxClassId() {
        return DefineCollectable.NO_CLASSID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<?> getJavaClass() {
        return StreamableCapObject.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        StreamableCapObject capObj = (StreamableCapObject)obj;
        
        capObj.setCcId((int)vstr.extractUnsignedInt());
        capObj.setCcCategory((String)vstr.restoreObject(SimpleMappings.CString));
        capObj.setCcClass((String)vstr.restoreObject(SimpleMappings.CString));
        capObj.setCcName((String)vstr.restoreObject(SimpleMappings.CString));
        capObj.setCcType((String)vstr.restoreObject(SimpleMappings.CString));
        capObj.setCcArea((String)vstr.restoreObject(SimpleMappings.CString));
        
        capObj.setCcDisableFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
    
        capObj.setParentID((int)vstr.extractUnsignedInt());
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        StreamableCapObject capObj = (StreamableCapObject)obj;
    
        vstr.insertUnsignedInt(capObj.getCcId());
        vstr.saveObject(capObj.getCcCategory(), SimpleMappings.CString);
        vstr.saveObject(capObj.getCcClass(), SimpleMappings.CString);
        vstr.saveObject(capObj.getCcName(), SimpleMappings.CString);
        vstr.saveObject(capObj.getCcType(), SimpleMappings.CString);
        vstr.saveObject(capObj.getCcArea(), SimpleMappings.CString);
        
        vstr.insertUnsignedInt(capObj.getCcDisableFlag() ? 1 : 0);
    
        vstr.insertUnsignedInt(capObj.getParentID());
    }
    
}