package com.cannontech.esub.element;

import java.awt.Color;
import java.awt.Shape;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.persist.PersistLineElement;
import com.cannontech.esub.svg.BaseSVGGenerator;
import com.loox.jloox.LxAbstractLine;
import com.loox.jloox.LxArrowElement;

/**
 * @author: asolberg
 */
public class LineElement extends LxAbstractLine implements DrawingElement {
    private static final String ELEMENT_ID = "lineElement";
    private static final int CURRENT_VERSION = 2;
    private boolean isNew = true;
    static final Color DEFAULT_COLOR = java.awt.Color.white;
    
    private transient Drawing drawing;
    private String linkTo;
    private Properties props = new Properties();
    private int version = CURRENT_VERSION;
    private Color color = DEFAULT_COLOR;
    private int blink = 0;
    private int colorPointID = -1;
    private int thicknessPointID = -1;
    private int arrowPointID = -1;
    private int opacityPointID = -1;
    private int blinkPointID = -1;
    private Map customColorMap = new HashMap(13);
    private Map customThicknessMap = new HashMap(13);
    private Map customArrowMap = new HashMap(13);
    private Map customOpacityMap = new HashMap(13);
    private Map customBlinkMap = new HashMap(13);
    private LiteState currentColorState;
    private LiteState currentThicknessState;
    private LiteState currentArrowState;
    private LiteState currentOpacityState;
    private LiteState currentBlinkState;
    private HashMap oldColorMap = new HashMap(11);
    
    
    /**
     * LineElment constructor
     */
    public LineElement() {
        super();
        initialize();
    }
   
    @SuppressWarnings("unchecked")
    private void initialize() {
        setColor(color);
        setLineColor(color);
        setPaint(DEFAULT_COLOR);
        setLineThickness(1.0f);
        setLineArrow(LxArrowElement.ARROW_NONE);
        setTransparency(1.0f);
        setLineBlink(0);
        oldColorMap.put(0, java.awt.Color.green);
        oldColorMap.put(1, java.awt.Color.red);
        oldColorMap.put(2, java.awt.Color.white);
        oldColorMap.put(3, java.awt.Color.yellow);
        oldColorMap.put(4, java.awt.Color.blue);
        oldColorMap.put(5, java.awt.Color.cyan);
        oldColorMap.put(6, java.awt.Color.black);
        oldColorMap.put(7, java.awt.Color.orange);
        oldColorMap.put(8, java.awt.Color.magenta);
        oldColorMap.put(9, java.awt.Color.gray);
        oldColorMap.put(10, java.awt.Color.pink);
    }
    
    public Color getColor(){
        return color;
    }
    
    public void setColor(Color c) {
        color = c;
    }
    
    public int getLineBlink(){
        return blink;
    }
    
    public void setLineBlink(int b) {
        blink = b;
    }
    
    public void setIsNew(boolean b) {
        isNew = b;
    }
    
    public boolean isNew() {
        return isNew;
    }
    
    @SuppressWarnings("unchecked")
    public List getColors() {
        List lineColors = new ArrayList(6);
        LitePoint point = null;
        try {
            point = DaoFactory.getPointDao().getLitePoint(getColorPointID());
        }catch(NotFoundException nfe) {
            // this point may have been deleted.
            CTILogger.error("The point (pointId:"+ getColorPointID() + ") for this line might have been deleted!", nfe);
        }
        if(point == null) {
            lineColors.add(getLineColor());
        }else {
        
            LiteStateGroup lsg = DaoFactory.getStateDao().getLiteStateGroup(point.getStateGroupID());
            List states = lsg.getStatesList();
            for(int i = 0; i < states.size(); i++) {
                Color colorObj = (Color) customColorMap.get(new Integer(i));
                Color color; 
                if(colorObj != null) {
                    color = colorObj;
                } 
                else {
                    color = (Color) oldColorMap.get(((LiteState) states.get(i)).getFgColor());
                }
                lineColors.add(color);
            }
        }
        return lineColors;
    }
    
    @SuppressWarnings("unchecked")
    public List getWidths() {
        List lineWidths = new ArrayList(6);
        
        LitePoint point = null;
        try {
            point = DaoFactory.getPointDao().getLitePoint(getThicknessPointID());
        }catch(NotFoundException nfe) {
            // this point may have been deleted.
            CTILogger.error("The point (pointId:"+ getThicknessPointID() + ") for this line might have been deleted!", nfe);
        }
        
        if(point == null) {
            lineWidths.add(new Float(getLineThickness()));
        }else {
        
            LiteStateGroup lsg = DaoFactory.getStateDao().getLiteStateGroup(point.getStateGroupID());
            List states = lsg.getStatesList();
            for(int i = 0; i < states.size(); i++) {
                Float widthObj = (Float) customThicknessMap.get(new Integer(i));
                Float width; 
                if(widthObj != null) {
                    width = widthObj;
                } 
                else {
                    width = new Float(1.0f);
                }
                lineWidths.add(width);
            }
        }
        return lineWidths;
    }
    
    @SuppressWarnings("unchecked")
    public List getArrows() {
        List lineArrows = new ArrayList(6);
        
        LitePoint point = null;
        try {
            point = DaoFactory.getPointDao().getLitePoint(getArrowPointID());
        }catch(NotFoundException nfe) {
            // this point may have been deleted.
            CTILogger.error("The point (pointId:"+ getArrowPointID() + ") for this line might have been deleted!", nfe);
        }
        
        if(point == null) {
            return lineArrows;
        }else {
            int originalArrowInt = getLineArrow();
            LiteStateGroup lsg = DaoFactory.getStateDao().getLiteStateGroup(point.getStateGroupID());
            List states = lsg.getStatesList();
            for(int i = 0; i < states.size(); i++) {
                Integer arrowObj = (Integer) customArrowMap.get(new Integer(i));
                Integer arrow; 
                if(arrowObj != null) {
                    arrow = arrowObj;
                } 
                else {
                    arrow = new Integer(0);
                }
                setLineArrow(arrow.intValue());
                Shape[] s = getShape();
                String dString = BaseSVGGenerator.getPathString(s, getCenterX(), getCenterY());
    
                lineArrows.add(dString);
            }
            setLineArrow(originalArrowInt);
        }
        return lineArrows;
    }
    
    @SuppressWarnings("unchecked")
    public List getOpacities() {
        List lineOpacities = new ArrayList(6);
        
        LitePoint point = null;
        try {
            point = DaoFactory.getPointDao().getLitePoint(getOpacityPointID());
        }catch(NotFoundException nfe) {
            // this point may have been deleted.
            CTILogger.error("The point (pointId:"+ getOpacityPointID() + ") for this line might have been deleted!", nfe);
        }
        
        if(point == null) {
            lineOpacities.add(new Float(getTransparency()));
            return lineOpacities;
        }else {
        
            LiteStateGroup lsg = DaoFactory.getStateDao().getLiteStateGroup(point.getStateGroupID());
            List states = lsg.getStatesList();
            for(int i = 0; i < states.size(); i++) {
                Float opacityObj = (Float) customOpacityMap.get(new Integer(i));
                Float opacity; 
                if(opacityObj != null) {
                    opacity = opacityObj;
                } 
                else {
                    opacity = new Float(1.0f);
                }
                lineOpacities.add(opacity);
            }
        }
        return lineOpacities;
    }
    
    @SuppressWarnings("unchecked")
    public List getBlinks() {
        List lineBlinks = new ArrayList(6);
        
        LitePoint point = null;
        try {
            point = DaoFactory.getPointDao().getLitePoint(getBlinkPointID());
        }catch(NotFoundException nfe) {
            // this point may have been deleted.
            CTILogger.error("The point (pointId:"+ getBlinkPointID() + ") for this line might have been deleted!", nfe);
        }
        
        if(point == null) {
            return lineBlinks;
        }else {
        
            LiteStateGroup lsg = DaoFactory.getStateDao().getLiteStateGroup(point.getStateGroupID());
            List states = lsg.getStatesList();
            for(int i = 0; i < states.size(); i++) {
                Integer blinkObj = (Integer)customBlinkMap.get(new Integer(i));
                Integer blink; 
                if(blinkObj != null) {
                    blink =blinkObj;
                } 
                else {
                    blink = new Integer(0);
                }
                lineBlinks.add(blink);
            }
        }
        return lineBlinks;
    }
    
    public int getColorPointID(){
        return colorPointID;
    }
    
    public void setColorPointID(int pointID) {
        colorPointID = pointID;
    }
    
    public int getThicknessPointID() {
        return thicknessPointID;
    }
    
    public void setThicknessPointID(int pointID) {
        thicknessPointID = pointID;
    }
    
    public int getArrowPointID() {
        return arrowPointID;
    }
    
    public void setArrowPointID(int pointID) {
        arrowPointID = pointID;
    }
    
    public int getOpacityPointID() {;
        return opacityPointID;
    }
    
    public void setOpacityPointID(int pointID) {
        opacityPointID = pointID;
    }
    
    public int getBlinkPointID() {;
        return blinkPointID;
    }

    public void setBlinkPointID(int pointID) {
        blinkPointID = pointID;
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
 * Creation date: (12/17/2001 3:50:28 PM)
 * @param in java.io.InputStream
 * @param version java.lang.String
 */
public synchronized void readFromJLX(InputStream in, String version) throws IOException
{
        super.readFromJLX(in, version);
        PersistLineElement.getInstance().readFromJLX(this,in);
}

/**
 * Creation date: (12/17/2001 3:49:44 PM)
 * @param out java.io.OutputStream
 */
public synchronized void saveAsJLX(OutputStream out) throws IOException 
{
        super.saveAsJLX(out);
        PersistLineElement.getInstance().saveAsJLX(this,out);
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
    
    public Map getCustomColorMap() {
        return customColorMap;
    }
    
    public void setCustomColorMap(Map m) {
        customColorMap = m;
    }
    
    public Map getCustomThicknessMap() {
        return customThicknessMap;
    }
    
    public void setCustomThicknessMap(Map m) {
        customThicknessMap = m;
    }
    
    public Map getCustomArrowMap() {
        return customArrowMap;
    }
    
    public void setCustomArrowMap(Map m) {
        customArrowMap = m;
    }
    
    public Map getCustomOpacityMap() {
        return customOpacityMap;
    }
    
    public void setCustomOpacityMap(Map m) {
        customOpacityMap = m;
    }
    
    public Map getCustomBlinkMap() {
        return customBlinkMap;
    }
    
    public void setCustomBlinkMap(Map m) {
        customBlinkMap = m;
    }
    
    /**
     * Returns the currentState.
     * @return LiteState
     */
    public LiteState getCurrentColorState() {
        return currentColorState;
    }

    /**
     * Sets the currentState.
     * @param currentState The currentState to set
     */
    public void setCurrentColorState(LiteState currentState) {
        this.currentColorState = currentState;
    }
    
    /**
     * Returns the currentState.
     * @return LiteState
     */
    public LiteState getCurrentThicknessState() {
        return currentThicknessState;
    }

    /**
     * Sets the currentState.
     * @param currentState The currentState to set
     */
    public void setCurrentThicknessState(LiteState currentState) {
        this.currentThicknessState = currentState;
    }
    
    /**
     * Returns the currentState.
     * @return LiteState
     */
    public LiteState getCurrentArrowState() {
        return currentArrowState;
    }

    /**
     * Sets the currentState.
     * @param currentState The currentState to set
     */
    public void setCurrentArrowState(LiteState currentState) {
        this.currentArrowState = currentState;
    }
    
    /**
     * Returns the currentState.
     * @return LiteState
     */
    public LiteState getCurrentOpacityState() {
        return currentOpacityState;
    }

    /**
     * Sets the currentState.
     * @param currentState The currentState to set
     */
    public void setCurrentOpacityState(LiteState currentState) {
        this.currentOpacityState = currentState;
    }
    
    /**
     * Returns the currentState.
     * @return LiteState
     */
    public LiteState getCurrentBlinkState() {
        return currentBlinkState;
    }

    /**
     * Sets the currentState.
     * @param currentState The currentState to set
     */
    public void setCurrentBlinkState(LiteState currentState) {
        this.currentBlinkState = currentState;
    }
    
    /**
     * Updates the elements actual color with the current state
     * This should only be called if the LineElement is being point driven
     */
    public void updateColor() {
        LiteState state = getCurrentColorState();
        if(state != null) {
            color = com.cannontech.common.gui.util.Colors.getColor(state.getFgColor());
            Color customColor = (java.awt.Color) customColorMap.get(new Integer(state.getStateRawState()));
            if(customColor != null) {
                color = customColor;
            }
            
        }
        
        setPaint(color);
        setColor(color);
        setLineColor(color);
    }

    public void updateThickness() {
        LiteState state = getCurrentThicknessState();
        float thickness = 1f;
        if(state != null) {
            Float customThickness = (Float)customThicknessMap.get(new Integer(state.getStateRawState()));
            if(customThickness != null) {
                thickness = customThickness;
            }
            
        }
        setLineThickness(thickness);
    }
    
    public void updateArrow() {
        LiteState state = getCurrentArrowState();
        int arrow = 0;
        if(state != null) {
            Integer customArrow = (Integer)customArrowMap.get(new Integer(state.getStateRawState()));
            if(customArrow != null) {
                arrow = customArrow;
            }
            
        }
        setLineArrow(arrow);
    }
    
    public void updateOpacity() {
        LiteState state = getCurrentOpacityState();
        float opacity = 1.0f;
        if(state != null) {
            Float customOpacity= (Float)customOpacityMap.get(new Integer(state.getStateRawState()));
            if(customOpacity != null) {
                opacity = customOpacity;
            }
            
        }
        setTransparency(opacity);
    }
    
    public void updateBlink() {
        LiteState state = getCurrentBlinkState();
        int blink = 0;
        if(state != null) {
            Integer customBlink = (Integer)customBlinkMap.get(new Integer(state.getStateRawState()));
            if(customBlink != null) {
                blink = customBlink;
            }
        }
        setLineBlink(blink);
    }
}