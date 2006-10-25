package com.cannontech.message.util;

import java.io.IOException;
import java.util.List;

import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualOutputStream;

public class VectorInsert 
{
    public static void insertVector(List v, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException
    {
        vstr.insertUnsignedInt( v.size() );
        for(int i=0;i<v.size();i++)
        {
            vstr.saveObject( v.get(i), polystr );
        }
    }
    
    public static void insertIntArray(int[] a, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException
    {
        vstr.insertUnsignedInt( a.length );
        for(int i=0; i < a.length; i++)
        {
            vstr.insertInt(a[i]);
        }
    }
    
}
