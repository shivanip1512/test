package com.cannontech.esub.element;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.persist.PersistRectangleElement;
import com.loox.jloox.LxAbstractRectangle;

/**
 * @author: asolberg
 */
public class RectangleElement extends LxAbstractRectangle implements DrawingElement {
    private static final String ELEMENT_ID = "rectangleElement";
    private static final int CURRENT_VERSION = 1;
    private boolean isNew = true;
    static final Color DEFAULT_COLOR = java.awt.Color.white;
    
    private transient Drawing drawing;
    private String linkTo;
    private Properties props = new Properties();
    private int version = CURRENT_VERSION;
    private Color color = DEFAULT_COLOR;
    
    /**
     * LineElment constructor
     */
    public RectangleElement() {
        super();
        initialize();
    }
   
    private void initialize() {
        setColor(color);
        setLineColor(color);
        setPaint(null);
        setLineThickness(1.0f);
        setTransparency(1.0f);
    }
    
    public Color getColor(){
        return color;
    }
    
    public void setColor(Color c) {
        color = c;
    }
    
    public void setIsNew(boolean b) {
        isNew = b;
    }
    
    public boolean isNew() {
        return isNew;
    }
    
    /**
     * @see com.cannontech.esub.editor.element.DrawingElement#getDrawing()
     */
    public Drawing getDrawing() {
        return drawing;
    }
    /**
     * @see com.cannontech.esub.editor.element.DrawingElement#setDrawing(Drawing)
     */
    public void setDrawing(Drawing d) {
        this.drawing = d;
    }

    /**
     * @see com.cannontech.esub.editor.element.DrawingElement#getElementProperties()
     */
    public Properties getElementProperties() {
        return props;
    }

    /**
     * @see com.cannontech.esub.editor.element.DrawingElement#setElementProperties(Properties)
     */
    public void setElementProperties(Properties props) {
        this.props = props;
    }

    /**
     * Reads the element from the jloox file.
     * @param in java.io.InputStream
     * @param version java.lang.String
     */
    public synchronized void readFromJLX(InputStream in, String version) throws IOException {
        super.readFromJLX(in, version);
        PersistRectangleElement.getInstance().readFromJLX(this,in);
    }

    /**
     * Writes the element to the jloox file.
     * @param out java.io.OutputStream
     */
    public synchronized void saveAsJLX(OutputStream out) throws IOException {
        super.saveAsJLX(out);
        PersistRectangleElement.getInstance().saveAsJLX(this,out);
    }

    /**
     * Returns the linkTo.
     * @return String
     */
    public String getLinkTo() {
        return linkTo;
    }

    /**
     * Sets the linkTo.
     * @param linkTo The linkTo to set
     */
    public void setLinkTo(String linkTo) {
        this.linkTo = linkTo;
    }
    
    public boolean isCopyable() {
        return true;
    }

    /**
     * @see com.cannontech.esub.element.DrawingElement#getVersion()
     */
    public int getVersion() {
        return version;
    }

    /**
     * @see com.cannontech.esub.element.DrawingElement#setVersion(int)
     */
    public void setVersion(int newVer) {
        this.version = newVer;
    }

    public String getElementID() {
        return ELEMENT_ID;
    }
    
}
