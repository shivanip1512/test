package com.cannontech.message.util;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.google.common.collect.Lists;
import com.roguewave.vsj.ObjectStreamer;


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
    
    public static <T> List<T> extractList(com.roguewave.vsj.VirtualInputStream vstr, ObjectStreamer type, Class<T> expectedType) throws IOException {
        int listSize = (int) vstr.extractUnsignedInt();

        List<T> result = Lists.newArrayListWithCapacity(listSize);
        for (int index = 0; index < listSize; index++) {
            result.add(expectedType.cast(vstr.restoreObject(type)));
        }

        return result;
    }
    
    public static int[] extractIntArray(com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws IOException {
        int num = (int) vstr.extractUnsignedInt();
        int[] v = new int[num];
        
        for (int i = 0; i < num; i++) {
            v[i] = vstr.extractInt();
        }
        return v;
    }
    
    public static List<Double> extractDoubleList(com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws IOException {
        int listSize = (int) vstr.extractUnsignedInt();
        
        List<Double> result = Lists.newArrayListWithCapacity(listSize);
        for (int i = 0; i < listSize; i++) {
            result.add(vstr.extractDouble());
        }
        return result;
    }
    
    public static List<Integer> extractIntList(com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws IOException {
        int listSize = (int) vstr.extractUnsignedInt();
        
        List<Integer> result = Lists.newArrayListWithCapacity(listSize);
        for (int i = 0; i < listSize; i++) {
            result.add(vstr.extractInt());
        }
        return result;
    }
}
