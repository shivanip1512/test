package com.cannontech.message.util;

import java.io.IOException;
import java.util.Vector;


public class VectorExtract 
{
    
    public static Vector extractVector(com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws IOException
    {
        
        int num = (int) vstr.extractUnsignedInt();
        Vector v = new Vector(num);

        for (int i = 0; i < num; i++) {
            v.addElement(vstr.restoreObject(polystr));
        }
        return v;
    }
}
