package com.cannontech.esub.element.persist;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.RectangleElement;
import com.loox.jloox.LxSaveUtils;

/**
 * @author asolberg
*/
public class PersistRectangleElement extends BasePersistElement {

    // Only create one of these
    private static PersistElement instance = null;
    
    public static synchronized PersistElement getInstance() {
        if(instance == null) {
            instance = new PersistRectangleElement();
        }
        return instance;
    }
    
    /**
     * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
     */
    public void readFromJLX(DrawingElement drawingElem, InputStream in, int version)
        throws IOException {
            
        RectangleElement elem = (RectangleElement) drawingElem;
            
            switch(version) {
                
                case 1: {
                    
                    Color borderColor = PersistUtils.readColor(in);
                    Color fillColor = PersistUtils.readColor(in);
                    int thickness = LxSaveUtils.readInt(in);
                    float opacity = LxSaveUtils.readFloat(in);
                    elem.setLineColor(borderColor);
                    elem.setPaint(fillColor);
                    elem.setLineThickness(thickness);
                    elem.setTransparency(opacity);
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
    public void saveAsJLX(DrawingElement drawingElem, OutputStream out, int version) throws IOException {
            RectangleElement elem = (RectangleElement) drawingElem;
            PersistUtils.writeColor(out, elem.getLineColor());
            PersistUtils.writeColor(out, (Color)elem.getPaint());
            LxSaveUtils.writeFloat(out, elem.getLineThickness());
            LxSaveUtils.writeFloat(out, elem.getTransparency());
            LxSaveUtils.writeString(out, elem.getLinkTo() );
        }
}
