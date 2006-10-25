package com.cannontech.message.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;


public class VectorExtract 
{
    
    @SuppressWarnings("unchecked")
    public static Vector extractVector(com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws IOException
    {
        
        int num = (int) vstr.extractUnsignedInt();
        Vector v = new Vector(num);

        for (int i = 0; i < num; i++) {
            v.addElement(vstr.restoreObject(polystr));
        }
        return v;
    }
    
    @SuppressWarnings("unchecked")
    public static void extractVector(Collection c, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws IOException {
        int num = (int) vstr.extractUnsignedInt();

        for (int i = 0; i < num; i++) {
            c.add(vstr.restoreObject(polystr));
        }
    }
    
    public static int[] extractIntArray(com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws IOException {
        int num = (int) vstr.extractUnsignedInt();
        int[] v = new int[num];
        
        for (int i = 0; i < num; i++) {
            v[i] = vstr.extractInt();
        }
        return v;
    }
}
