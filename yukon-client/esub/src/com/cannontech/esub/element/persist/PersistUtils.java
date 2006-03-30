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
		return new Color(	LxSaveUtils.readInt(in), 
							LxSaveUtils.readInt(in),
							LxSaveUtils.readInt(in));
	}
	
	/**
	 * Write out a Color to a JLoox stream.
	 * @param out	JLoox output stream
	 * @param color	the color that will be serialized
	 * @throws IOException
	 */
	public static void writeColor(OutputStream out, Color color) throws IOException {
		LxSaveUtils.writeInt(out, color.getRed());
		LxSaveUtils.writeInt(out, color.getGreen());
		LxSaveUtils.writeInt(out, color.getBlue());		
	}
	
	public static Map readIntIntMap(InputStream in) throws IOException {
		int numEntries = LxSaveUtils.readInt(in);
		Map m = new HashMap();
		for(int i = 0; i < numEntries; i++) {
			int rawState = LxSaveUtils.readInt(in);
			int imageId = LxSaveUtils.readInt(in);
			
			m.put(new Integer(rawState), new Integer(imageId));
		}
		return m;
	}
	
	public static void writeIntIntMap(OutputStream out, Map m) throws IOException {
		Set s = m.keySet();

		LxSaveUtils.writeInt(out, s.size());
		
		Iterator iter = s.iterator();
		while (iter.hasNext()) {
			Integer rawState = (Integer) iter.next();			
			Integer imageId = (Integer) m.get(rawState);
			
			LxSaveUtils.writeInt(out, rawState.intValue());
			LxSaveUtils.writeInt(out, imageId.intValue());
		}
	}
	
}
