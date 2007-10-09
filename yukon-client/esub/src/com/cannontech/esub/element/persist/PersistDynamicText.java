package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.DynamicText;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
*/
public class PersistDynamicText extends BasePersistElement {

	// Only create one of these
	private static PersistElement instance = null;
	
	public static synchronized PersistElement getInstance() {
		if(instance == null) {
			instance = new PersistDynamicText();
		}
		return instance;
	}
	
	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
	 */
	public void readFromJLX(DrawingElement drawingElem, InputStream in, int version) throws IOException {
			
			DynamicText elem = (DynamicText) drawingElem;
			
			switch(version) {
				
				case 1: {
					elem.setPointId(LxSaveUtils.readInt(in));
        			elem.setDisplayAttribs(LxSaveUtils.readInt(in));     
        			elem.setLinkTo(LxSaveUtils.readString(in));   
                    elem.setControlPointId(elem.getPointId());
                }
				break;
                
                case 2: {
                    elem.setPointId(LxSaveUtils.readInt(in));
                    elem.setDisplayAttribs(LxSaveUtils.readInt(in));     
                    elem.setLinkTo(LxSaveUtils.readString(in));   
                    elem.setControlEnabled(LxSaveUtils.readBoolean(in));
                    elem.setControlPointId(elem.getPointId());
                }
                break;
                
                case 3: {
                    elem.setPointId(LxSaveUtils.readInt(in));
                    elem.setDisplayAttribs(LxSaveUtils.readInt(in));     
                    elem.setLinkTo(LxSaveUtils.readString(in));   
                    elem.setControlEnabled(LxSaveUtils.readBoolean(in));
                    elem.setColorPointID(LxSaveUtils.readInt(in));
                    elem.setCustomColorMap(PersistUtils.readIntColorMap(in));
                    elem.setCustomTextMap(PersistUtils.readIntStringMap(in));
                    int blink = LxSaveUtils.readInt(in);
                    int blinkPointID = LxSaveUtils.readInt(in);
                    Map customBlinkMap = PersistUtils.readIntIntMap(in);
                    elem.setTextBlink(blink);
                    elem.setBlinkPointID(blinkPointID);
                    elem.setCustomBlinkMap(customBlinkMap);
                    elem.setControlPointId(elem.getPointId());
                }
                break;
                
                case 4: {
                    elem.setPointId(LxSaveUtils.readInt(in));
                    elem.setDisplayAttribs(LxSaveUtils.readInt(in));     
                    elem.setLinkTo(LxSaveUtils.readString(in));   
                    elem.setControlEnabled(LxSaveUtils.readBoolean(in));
                    elem.setColorPointID(LxSaveUtils.readInt(in));
                    elem.setCustomColorMap(PersistUtils.readIntColorMap(in));
                    elem.setCustomTextMap(PersistUtils.readIntStringMap(in));
                    int blink = LxSaveUtils.readInt(in);
                    int blinkPointID = LxSaveUtils.readInt(in);
                    Map customBlinkMap = PersistUtils.readIntIntMap(in);
                    int controlPointId = LxSaveUtils.readInt(in);
                    int currentStateId = LxSaveUtils.readInt(in);
                    elem.setTextBlink(blink);
                    elem.setBlinkPointID(blinkPointID);
                    elem.setCustomBlinkMap(customBlinkMap);
                    elem.setControlPointId(controlPointId);
                    elem.setCurrentStateID(currentStateId);
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
	public void saveAsJLX(DrawingElement drawingElem, OutputStream out, int version) throws IOException {
		DynamicText elem = (DynamicText) drawingElem;
		LxSaveUtils.writeInt(out, elem.getPointId());
		LxSaveUtils.writeInt(out, elem.getDisplayAttribs());
		LxSaveUtils.writeString(out, elem.getLinkTo());
        LxSaveUtils.writeBoolean(out, elem.getControlEnabled());
        LxSaveUtils.writeInt(out, elem.getColorPointID());
        PersistUtils.writeIntColorMap(out, elem.getCustomColorMap());
        PersistUtils.writeIntStringMap(out, elem.getCustomTextMap());
        LxSaveUtils.writeInt(out, elem.getTextBlink());
        LxSaveUtils.writeInt(out, elem.getBlinkPointID());
        PersistUtils.writeIntIntMap(out, elem.getCustomBlinkMap());
        LxSaveUtils.writeInt(out, elem.getControlPointId());
        LxSaveUtils.writeInt(out, elem.getCurrentStateID());
	}

}
