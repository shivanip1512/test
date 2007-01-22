package com.cannontech.esub.element;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.PointAttributes;
import com.cannontech.esub.element.persist.PersistDynamicText;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxContainer;

/**
 * DynamicText is a text element that is bound to a point attribute.
 * Creation date: (12/17/2001 1:44:37 PM)
 * @author: 
 */
public class DynamicText extends LxAbstractText implements DrawingElement, Serializable {
	
	private static final String ELEMENT_ID = "dynamicText";
	public static final int INVALID_POINT = -1;	
	
	private static final int CURRENT_VERSION = 3;
	
	static final Font DEFAULT_FONT = new java.awt.Font("arial", java.awt.Font.BOLD, 12);
	static final Color DEFAULT_COLOR = java.awt.Color.white;
	
	private com.cannontech.database.data.lite.LitePoint point;	
	private int displayAttribs = 0x00;
    private Color color = DEFAULT_COLOR;
    private String text = "";
	private transient Drawing drawing = null;
	private String linkTo = null;
	private Properties props = new Properties();
	private int version = CURRENT_VERSION;
    private boolean controlEnabled = false;
    private int colorPointID = -1;
    private int controlPointID = -1;
    private int currentStateID = -1;
    private Map customColorMap = new HashMap(13);
    private Map customTextMap = new HashMap(13);
    private LiteState currentColorState;
    private LiteState currentTextState;
    private HashMap oldColorMap = new HashMap(11);
	
/**
 * DynamicText constructor comment.
 */
public DynamicText() {
	super();
	initialize();
}
/**
 * DynamicText constructor comment.
 * @param arg1 com.loox.jloox.LxContainer
 */
public DynamicText(LxContainer arg1) {
	super(arg1);
	initialize();
}
/**
 * DynamicText constructor comment.
 * @param arg1 com.loox.jloox.LxContainer
 * @param arg2 java.awt.geom.Rectangle2D
 */
public DynamicText(LxContainer arg1, java.awt.geom.Rectangle2D arg2) {
	super(arg1, arg2);
	initialize();
}
/**
 * DynamicText constructor comment.
 * @param arg1 com.loox.jloox.LxContainer
 * @param arg2 java.awt.geom.Rectangle2D
 * @param arg3 java.lang.String
 */
public DynamicText(LxContainer arg1, java.awt.geom.Rectangle2D arg2, String arg3) {
	super(arg1, arg2, arg3);
	initialize();
}
/**
 * DynamicText constructor comment.
 * @param arg1 com.loox.jloox.LxContainer
 * @param arg2 java.lang.String
 */
public DynamicText(LxContainer arg1, String arg2) {
	super(arg1, arg2);
	initialize();
}
/**
 * DynamicText constructor comment.
 * @param arg1 java.lang.String
 */
public DynamicText(String arg1) {
	super(arg1);
	initialize();
}

public Map getCustomColorMap() {
    return customColorMap;
}

public void setCustomColorMap(Map m) {
    customColorMap = m;
}

public Map getCustomTextMap() {
    return customTextMap;
}

public void setCustomTextMap(Map m) {
    customTextMap = m;
}

public int getColorPointID(){
    return colorPointID;
}

public void setColorPointID(int pointID) {
    colorPointID = pointID;
}

public int getControlPointID(){
    return controlPointID;
}

public void setControlPointID(int pointID) {
    controlPointID = pointID;
}
public boolean getControlEnabled()
{
    return controlEnabled;
}

public void setControlEnabled(boolean value)
{
    controlEnabled = value;
}

public int getCurrentStateID(){
    return currentStateID;
}

public void setCurrentStateID(int pointID) {
    currentStateID = pointID;
}

/**
 * Creation date: (12/18/2001 4:51:49 PM)
 * @return com.cannontech.database.data.lite.LitePoint
 */
public com.cannontech.database.data.lite.LitePoint getPoint() {	
	return point;
}
/**
 * Creation date: (12/18/2001 12:47:22 PM)
 * @return int
 */
public int getPointID() {
	return point.getPointID();
}
/**
 * Creation date: (12/18/2001 4:58:59 PM)
 */
@SuppressWarnings("unchecked")
private void initialize() {
	
	setFont(DEFAULT_FONT);
	setPaint(DEFAULT_COLOR);
	point = new com.cannontech.database.data.lite.LitePoint(INVALID_POINT);
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

/**
 * Creation date: (1/14/2002 2:33:29 PM)
 * @param f java.awt.Font
 */
public void setFont(Font f) {
	super.setFont(f);
}

/**
 * Creation date: (12/18/2001 4:51:49 PM)
 * @param newPoint com.cannontech.database.data.lite.LitePoint
 */
public void setPoint(com.cannontech.database.data.lite.LitePoint newPoint) {
	point = newPoint;
}
/**
 * Creation date: (12/18/2001 12:47:22 PM)
 * @param newPointID int
 */
public void setPointID(int newPointID)  {
	LitePoint lp = DaoFactory.getPointDao().getLitePoint( newPointID ); 
	if(lp != null) {
		point = lp;
	}
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
	 * @see java.lang.Object#clone()
	 */
	public Object clone()  {
		Object v = super.clone();		 
		return v;
	}

	
	/**
	 * Returns the displayAttribs.
	 * @return int
	 */
	public int getDisplayAttribs() {
		return displayAttribs;
	}

	/**
	 * Sets the displayAttribs.
	 * @param displayAttribs The displayAttribs to set
	 */
	public void setDisplayAttribs(int displayAttribs) {
		this.displayAttribs = displayAttribs;
	}
    
    /**
     * Sets the currentState.
     * @param currentState The currentState to set
     */
    public void setCurrentColorState(LiteState currentState) {
        this.currentColorState = currentState;
    }
    
    /**
     * Sets the currentState.
     * @param currentState The currentState to set
     */
    public void setCurrentTextState(LiteState currentState) {
        this.currentTextState = currentState;
    }

	/**
	 * Returns the editable.
	 * @return boolean
	 */
	public boolean isEditable() {
		return 
			(	displayAttribs == PointAttributes.LOW_LIMIT  ||
			 	displayAttribs == PointAttributes.HIGH_LIMIT ||
			 	displayAttribs == PointAttributes.LIMIT_DURATION ||
				displayAttribs == PointAttributes.MULTIPLIER ||
				displayAttribs == PointAttributes.DATA_OFFSET );		
	}
    
    @SuppressWarnings("unchecked")
    public List getColors() {
        List textColors = new ArrayList(6);
        LitePoint point = DaoFactory.getPointDao().getLitePoint(getColorPointID());
        if(point == null) {
            textColors.add(getPaint());
            return textColors;
        }
        
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
            textColors.add(color);
        }
        return textColors;
    }
    
    @SuppressWarnings("unchecked")
    public List getTextStrings() {
        List textStrings = new ArrayList(6);
        LitePoint point = DaoFactory.getPointDao().getLitePoint(getControlPointID());
        if(point == null) {
            textStrings.add(getText());
            return textStrings;
        }
        
        LiteStateGroup lsg = DaoFactory.getStateDao().getLiteStateGroup(point.getStateGroupID());
        List states = lsg.getStatesList();
        for(int i = 0; i < states.size(); i++) {
            String textObj = (String) customTextMap.get(new Integer(i));
            String text;
            if(textObj != null) {
                text = textObj;
            } 
            else {
                text = ((LiteState) states.get(i)).getStateText();
            }
            textStrings.add(text);
        }
        return textStrings;
    }
    
    /**
     * Returns the currentState.
     * @return LiteState
     */
    public LiteState getCurrentColorState() {
        return currentColorState;
    }
    
    /**
     * Returns the currentState.
     * @return LiteState
     */
    public LiteState getCurrentTextState() {
        return currentTextState;
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
    
    /**
     * Updates the elements actual color with the current state
     * This should only be called if the LineElement is being point driven
     */
    public void updateText() {
        LiteState state = getCurrentTextState();
        if(state != null) {
            text = state.getStateText();
            String customString = (String) customTextMap.get(new Integer(state.getStateRawState()));
            if(customString != null) {
                text = customString;
            }
            
        }
        
        setText(text);
    }
    
    public void setColor(Color c) {
        color = c;
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
public void readFromJLX(InputStream in, String version) throws IOException
{  
        super.readFromJLX(in, version);
		PersistDynamicText.getInstance().readFromJLX(this,in);
}
/**
 * Creation date: (12/17/2001 3:49:44 PM)
 * @param out java.io.OutputStream
 */
public void saveAsJLX(OutputStream out) throws IOException 
{
        super.saveAsJLX(out);
 		PersistDynamicText.getInstance().saveAsJLX(this,out);
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
