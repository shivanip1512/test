package com.cannontech.esub.element;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.image.dao.YukonImageDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.persist.PersistStateImage;
import com.cannontech.esub.util.Util;
import com.cannontech.spring.YukonSpringHook;
import com.loox.jloox.LxAbstractImage;

/**
 * 
 * Creation date: (1/8/2002 1:47:20 PM)
 * @author: Aaron Lauinger
 */
public class StateImage extends LxAbstractImage implements DrawingElement, YukonImageElement, IdAttachable  {
		
	private static final String ELEMENT_ID = "stateImage";
	private static final int CURRENT_VERSION = 3;
	
	private int pointID = -1;
	
	// Map<Integer - rawstate, Integer - image id>
	// There may or may not be an entry for all of the points stategroups
	// Look here first to find the current image
	private Map<Integer, Integer> customImageMap = new HashMap<Integer, Integer>(13);
	
	private LiteState currentState;
		
	private Drawing drawing;
	private String linkTo = null;
	private Properties props = new Properties();
	private int version = CURRENT_VERSION;
    private boolean controlEnabled = true;
	
/**
 * StateImage constructor comment.
 */
public StateImage() {
	super();
	initialize();
}

/**
 * Gets the litepoint associated with this state image, if any
 * Returns null if a lite point cannot be found
 * Creation date: (1/8/2002 1:56:26 PM)
 * @return com.cannontech.database.data.lite.LitePoint
 */
public LitePoint getPoint() {
    try {
        return YukonSpringHook.getBean(PointDao.class).getLitePoint(pointID);
    } catch(NotFoundException nfe) {
        //this is OK
    }
    return null;
}

public int getPointID() {
	return pointID;
}

/**
 * Creation date: (1/8/2002 2:07:06 PM)
 */
private void initialize() {	
}

/**
 * Creation date: (1/8/2002 1:56:26 PM)
 * @param newPoint com.cannontech.database.data.lite.LitePoint
 */
public void setPoint(com.cannontech.database.data.lite.LitePoint newPoint) {
	setPointID(newPoint.getLiteID());
}

public void setPointID(int pointID) {
	this.pointID = pointID;
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
	 * Returns the currentState.
	 * @return LiteState
	 */
	public LiteState getCurrentState() {
		return currentState;
	}
    
    public boolean getControlEnabled()
    {
        return controlEnabled;
    }
    
    public void setControlEnabled(boolean value)
    {
        controlEnabled = value;
    }

	/**
	 * Sets the currentState.
	 * @param currentState The currentState to set
	 */
	public void setCurrentState(LiteState currentState) {
		this.currentState = currentState;
	}
	
	public String getImageName() {
		LiteYukonImage lyi = getYukonImage();
		if(lyi != null) {
			return lyi.getImageName();
		}
		else {
			return Util.DEFAULT_IMAGE_NAME;
		}
	}
	
	public LiteYukonImage getYukonImage() {
		LiteState state = getCurrentState();
		if(state != null) {
			int imageId = state.getImageID();
			Integer customImageId = customImageMap.get(new Integer(state.getStateRawState()));
			if(customImageId != null) {
				imageId = customImageId.intValue();
			}
			return YukonSpringHook.getBean(YukonImageDao.class).getLiteYukonImage(imageId);
		}
		return null;
	}
	
	/**
	 * Returns a list of image names corresponding to each of the points raw states
	 * @return
	 */
	public List<String> getImageNames() {
		List<String> imageNames = new ArrayList<String>(6);
		LitePoint point = getPoint();
        if(point == null) {
            imageNames.add(Util.DEFAULT_IMAGE_NAME);
            return imageNames;
        }
        
		LiteStateGroup lsg = YukonSpringHook.getBean(StateDao.class).getLiteStateGroup(point.getStateGroupID());
		List<LiteState> states = lsg.getStatesList();
		for(int i = 0; i < states.size(); i++) {
			Integer imgIdObj = customImageMap.get(new Integer(i));
			int imgId; 
			if(imgIdObj != null) {
				imgId = imgIdObj.intValue();
			} 
			else {
				imgId = states.get(i).getImageID();
			}
			LiteYukonImage lyi = YukonSpringHook.getBean(YukonImageDao.class).getLiteYukonImage(imgId); 
			if(lyi != null) {
				imageNames.add(lyi.getImageName());
			}
			else {
				imageNames.add(Util.DEFAULT_IMAGE_NAME);
			}
		}
		return imageNames;
		                                      
	}
	/**
	 * Updates the elements actual image with the currentStates
	 * Displays the default image if there is no current state
	 * available
	 * image
	 */
	public void updateImage() {
		byte[] imgBuf = Util.DEFAULT_IMAGE_BYTES;
		
		LiteYukonImage lyi = getYukonImage();
		if( lyi != null ) {
			imgBuf = lyi.getImageValue();
		}		

		Image img = Util.prepareImage(imgBuf);		
		setImage(img);
	}
	
	/**
	 * Overriden simply to make sure our size is the same as the image
	 * we are changing to
	 * @see com.loox.jloox.LxAbstractImage#setImage(Image)
	 */
	public void setImage(Image img) {
		if( img != null ) { 
			setSize(img.getWidth(null), img.getHeight(null));
		}
		super.setImage(img);			
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
		PersistStateImage.getInstance().readFromJLX(this,in);
        updateImage();
}

/**
 * Creation date: (12/17/2001 3:49:44 PM)
 * @param out java.io.OutputStream
 */
public synchronized void saveAsJLX(OutputStream out) throws IOException 
{
        super.saveAsJLX(out);
		PersistStateImage.getInstance().saveAsJLX(this,out);
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
		return false;
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
	
	/**
	 * This exists only for persistence, do not use it for other purposes
	 * @return
	 */
	public Map<Integer, Integer> getCustomImageMap() {
		return customImageMap;
	}
	
	public void setCustomImageMap(Map<Integer, Integer> m) {
		customImageMap = m;
	}

    @Override
    public boolean fixIds() {
        PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
        if(getPointID() != -1) {
            try {
                pointDao.getLitePoint(pointID);
            } catch (NotFoundException e) {
                setPointID(-1);
                byte[] imgBuf = Util.DEFAULT_IMAGE_BYTES;
                Image img = Util.prepareImage(imgBuf);      
                setImage(img);
                return true;
            }
        }
        return false;
    }
}
