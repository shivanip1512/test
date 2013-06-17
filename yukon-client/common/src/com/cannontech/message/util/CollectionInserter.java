package com.cannontech.message.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.cannontech.message.capcontrol.model.SerializableByIdentifier;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualOutputStream;

public class CollectionInserter 
{
    public static void insertVector(List v, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
    	int size = 0;
    	
    	if (v != null) {
    		size = v.size();
    	}
        
    	vstr.insertUnsignedInt( size );
        
    	for (int i=0; i < size;i++) {
            vstr.saveObject( v.get(i), polystr );
        }
    }
    
    public static void insertIntArray(int[] a, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        vstr.insertUnsignedInt( a.length );
        for(int i=0; i < a.length; i++) {
            vstr.insertInt(a[i]);
        }
    }
    
    public static <K extends SerializableByIdentifier> void insertIntegerMap(Map<K,Integer> map, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        vstr.insertUnsignedInt(map.size());
        
        for (Map.Entry<K,Integer> entry : map.entrySet()) {
            vstr.insertUnsignedInt(entry.getKey().getSerializeId());
            vstr.insertUnsignedInt(entry.getValue());
        }
    }
    
    public static <K extends SerializableByIdentifier> void insertDoubleMap(Map<K,Double> map, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        vstr.insertUnsignedInt(map.size());
        
        for (Map.Entry<K,Double> entry : map.entrySet()) {
            vstr.insertUnsignedInt(entry.getKey().getSerializeId());
            vstr.insertDouble(entry.getValue());
        }
    }
} 
