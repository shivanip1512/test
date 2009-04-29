package com.cannontech.esub.element.persist;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.loox.jloox.LxSaveUtils;

/**
 * A collection of static methods to make some object convenient to persist
 * in the JLoox way.
 * 
 * @author alauinger
 * 
 */
public class PersistUtils {

	/**
	 * Read in a color from a JLoox stream
	 * @param in 	JLoox input stream
	 * @return 		the color that was read from the stram
	 * @throws IOException
	 */
	public static Color readColor(InputStream in) throws IOException {
	    int red = LxSaveUtils.readInt(in);
	    int green = LxSaveUtils.readInt(in);
	    int blue = LxSaveUtils.readInt(in);
	    if(red > -1) {
	        return new Color(red, green, blue);
	    }else {
	        return null;
	    }
	}
	
	/**
	 * Write out a Color to a JLoox stream.
	 * @param out	JLoox output stream
	 * @param color	the color that will be serialized
	 * @throws IOException
	 */
	public static void writeColor(OutputStream out, Color color) throws IOException {
	    if(color != null) {
    		LxSaveUtils.writeInt(out, color.getRed());
    		LxSaveUtils.writeInt(out, color.getGreen());
    		LxSaveUtils.writeInt(out, color.getBlue());
	    }else {
	        LxSaveUtils.writeInt(out, -1);
            LxSaveUtils.writeInt(out, -1);
            LxSaveUtils.writeInt(out, -1);
	    }
	}
    
    public static Map<Integer, Float> readIntFloatMap(InputStream in) throws IOException {
        int numEntries = LxSaveUtils.readInt(in);
        Map<Integer, Float> m = new HashMap<Integer, Float>();
        for(int i = 0; i < numEntries; i++) {
            int rawState = LxSaveUtils.readInt(in);
            float f = LxSaveUtils.readFloat(in);
            
            m.put(new Integer(rawState), new Float(f));
        }
        return m;
    }
    
    public static void writeIntFloatMap(OutputStream out, Map<Integer,Float> m) throws IOException {
        Set<Integer> s = m.keySet();

        LxSaveUtils.writeInt(out, s.size());
        
        Iterator<Integer> iter = s.iterator();
        while (iter.hasNext()) {
            Integer rawState = iter.next();           
            Float f = m.get(rawState);
            
            LxSaveUtils.writeInt(out, rawState.intValue());
            LxSaveUtils.writeFloat(out, f.floatValue());
        }
    }
	
    public static Map<Integer, String> readIntStringMap(InputStream in) throws IOException {
        int numEntries = LxSaveUtils.readInt(in);
        Map<Integer, String> m = new HashMap<Integer, String>();
        for(int i = 0; i < numEntries; i++) {
            int rawState = LxSaveUtils.readInt(in);
            String str = LxSaveUtils.readString(in);
            
            m.put(new Integer(rawState), str);
        }
        return m;
    }
    
    public static void writeIntStringMap(OutputStream out, Map<Integer, String> m) throws IOException {
        Set<Integer> s = m.keySet();

        LxSaveUtils.writeInt(out, s.size());
        
        Iterator<Integer> iter = s.iterator();
        while (iter.hasNext()) {
            Integer rawState = iter.next();           
            String str = m.get(rawState);
            
            LxSaveUtils.writeInt(out, rawState.intValue());
            LxSaveUtils.writeString(out, str);
        }
    }
    
    public static Map<Integer, Integer> readIntIntMap(InputStream in) throws IOException {
		int numEntries = LxSaveUtils.readInt(in);
		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for(int i = 0; i < numEntries; i++) {
			int rawState = LxSaveUtils.readInt(in);
			int imageId = LxSaveUtils.readInt(in);
			
			m.put(new Integer(rawState), new Integer(imageId));
		}
		return m;
	}
	
	public static void writeIntIntMap(OutputStream out, Map<Integer, Integer> m) throws IOException {
		Set<Integer> s = m.keySet();

		LxSaveUtils.writeInt(out, s.size());
		
		Iterator<Integer> iter = s.iterator();
		while (iter.hasNext()) {
			Integer rawState = iter.next();			
			Integer imageId = m.get(rawState);
			
			LxSaveUtils.writeInt(out, rawState.intValue());
			LxSaveUtils.writeInt(out, imageId.intValue());
		}
	}
    
    public static Map<Integer, Color> readIntColorMap(InputStream in) throws IOException {
        int numEntries = LxSaveUtils.readInt(in);
        Map<Integer, Color> m = new HashMap<Integer, Color>();
        for(int i = 0; i < numEntries; i++) {
            int rawState = LxSaveUtils.readInt(in);
            Color stateColor = readColor(in);
            
            m.put(new Integer(rawState), stateColor);
        }
        return m;
    }
    
    public static void writeIntColorMap(OutputStream out, Map<Integer, Color> m) throws IOException {
        Set<Integer> s = m.keySet();

        LxSaveUtils.writeInt(out, s.size());
        
        Iterator<Integer> iter = s.iterator();
        while (iter.hasNext()) {
            Integer rawState = iter.next();           
            Color stateColor = m.get(rawState);
            
            LxSaveUtils.writeInt(out, rawState.intValue());
            writeColor(out, stateColor);
        }
    }
	
}
