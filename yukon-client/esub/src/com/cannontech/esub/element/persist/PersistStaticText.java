package com.cannontech.esub.element.persist;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.StaticText;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
*/
public class PersistStaticText extends BasePersistElement {

	// Only create one of these
	private static PersistElement instance = null;
	
	public static synchronized PersistElement getInstance() {
		if(instance == null) {
			instance = new PersistStaticText();
		}
		return instance;
	}
	
	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
	 */
	public void readFromJLX(DrawingElement drawingElem, InputStream in, int version)
		throws IOException {
			
			StaticText elem = (StaticText) drawingElem;
			
			switch(version) {
				
				case 1: {
					
					String fontName = LxSaveUtils.readString(in);
        			int fontSize = LxSaveUtils.readInt(in);
        			elem.setFont(fontName, fontSize);
        			
        			Color c = PersistUtils.readColor(in);

        			elem.setPaint(c);
	                elem.setLinkTo( LxSaveUtils.readString(in) );
				}
				break;
				
				default: {
					throw new IOException("Unknown version: " + version + " in " + elem.getClass().getName());
				}
			}
	}

	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#saveAsJLX(DrawingElement, OutputStream)
	 */
	public void saveAsJLX(DrawingElement drawingElem, OutputStream out, int version)
		throws IOException {
			StaticText elem = (StaticText) drawingElem;

			Font f = elem.getFont();
            LxSaveUtils.writeString(out, f.getFontName() );
        	LxSaveUtils.writeInt(out, f.getSize() );

			PersistUtils.writeColor(out, (Color)elem.getPaint());			
        
            LxSaveUtils.writeString(out, elem.getLinkTo() );
		}

}
