package com.cannontech.esub.element.persist;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.LineElement;
import com.loox.jloox.LxSaveUtils;

/**
 * @author asolberg
*/
public class PersistLineElement extends BasePersistElement {

    // Only create one of these
    private static PersistElement instance = null;
    
    public static synchronized PersistElement getInstance() {
        if(instance == null) {
            instance = new PersistLineElement();
        }
        return instance;
    }
    
    /**
     * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
     */
    public void readFromJLX(DrawingElement drawingElem, InputStream in, int version)
        throws IOException {
            
            LineElement elem = (LineElement) drawingElem;
            
            switch(version) {
                
                case 1: {
                    
                    Color c = PersistUtils.readColor(in);
                    int thickness = LxSaveUtils.readInt(in);
                    int arrow = LxSaveUtils.readInt(in);
                    float opacity = LxSaveUtils.readFloat(in);
                    elem.setPaint(c);
                    elem.setLineThickness(thickness);
                    elem.setLineArrow(arrow);
                    elem.setTransparency(opacity);
                    elem.setLinkTo( LxSaveUtils.readString(in) );
                }
                
                case 2: {
                    
                    Color c = PersistUtils.readColor(in);
                    int colorPointID = LxSaveUtils.readInt(in);
                    Map customColorMap = PersistUtils.readIntColorMap(in);
                    int thickness = LxSaveUtils.readInt(in);
                    int thicknessPointID = LxSaveUtils.readInt(in);
                    Map customThicknessMap = PersistUtils.readIntFloatMap(in);
                    int arrow = LxSaveUtils.readInt(in);
                    int arrowPointID = LxSaveUtils.readInt(in);
                    Map customArrowMap = PersistUtils.readIntIntMap(in);
                    float opacity = LxSaveUtils.readFloat(in);
                    int opacityPointID = LxSaveUtils.readInt(in);
                    Map customOpacityMap = PersistUtils.readIntFloatMap(in);
                    int blink = LxSaveUtils.readInt(in);
                    int blinkPointID = LxSaveUtils.readInt(in);
                    Map customBlinkMap = PersistUtils.readIntIntMap(in);
                    elem.setPaint(c);
                    elem.setColorPointID(colorPointID);
                    elem.setCustomColorMap(customColorMap);
                    elem.setLineThickness(thickness);
                    elem.setThicknessPointID(thicknessPointID);
                    elem.setCustomThicknessMap(customThicknessMap);
                    elem.setLineArrow(arrow);
                    elem.setArrowPointID(arrowPointID);
                    elem.setCustomArrowMap(customArrowMap);
                    elem.setTransparency(opacity);
                    elem.setOpacityPointID(opacityPointID);
                    elem.setCustomOpacityMap(customOpacityMap);
                    elem.setLineBlink(blink);
                    elem.setBlinkPointID(blinkPointID);
                    elem.setCustomBlinkMap(customBlinkMap);
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
            LineElement elem = (LineElement) drawingElem;
            
            PersistUtils.writeColor(out, (Color)elem.getPaint());
            LxSaveUtils.writeInt(out, elem.getColorPointID());
            PersistUtils.writeIntColorMap(out, elem.getCustomColorMap());
            LxSaveUtils.writeFloat(out, elem.getLineThickness());
            LxSaveUtils.writeInt(out, elem.getThicknessPointID());
            PersistUtils.writeIntFloatMap(out, elem.getCustomThicknessMap());
            LxSaveUtils.writeInt(out, elem.getLineArrow());
            LxSaveUtils.writeInt(out, elem.getArrowPointID());
            PersistUtils.writeIntIntMap(out, elem.getCustomArrowMap());
            LxSaveUtils.writeFloat(out, elem.getTransparency());
            LxSaveUtils.writeInt(out, elem.getOpacityPointID());
            PersistUtils.writeIntFloatMap(out, elem.getCustomOpacityMap());
            LxSaveUtils.writeInt(out, elem.getLineBlink());
            LxSaveUtils.writeInt(out, elem.getBlinkPointID());
            PersistUtils.writeIntIntMap(out, elem.getCustomBlinkMap());
            LxSaveUtils.writeString(out, elem.getLinkTo() );
        }
}