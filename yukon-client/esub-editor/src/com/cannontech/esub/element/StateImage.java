package com.cannontech.esub.element;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.element.persist.PersistStateImage;
import com.cannontech.esub.util.Util;
import com.loox.jloox.LxAbstractImage;

/**
 * 
 * Creation date: (1/8/2002 1:47:20 PM)
 * @author: Aaron Lauinger
 */
public class StateImage extends LxAbstractImage implements DrawingElement {
		
	private static final int CURRENT_VERSION = 1;
	
	private LitePoint point;
	private LiteState currentState;
		
	private Drawing drawing;
	private String linkTo = null;
	private Properties props = new Properties();
	private int version = CURRENT_VERSION;
	
/**
 * StateImage constructor comment.
 */
public StateImage() {
	super();
	initialize();
}

/**
 * Creation date: (1/8/2002 1:56:26 PM)
 * @return com.cannontech.database.data.lite.LitePoint
 */
public com.cannontech.database.data.lite.LitePoint getPoint() {
	return point;
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
	point = newPoint;
}

public void setPointID(int pointID) {
	setPoint(PointFuncs.getLitePoint(pointID));
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

	/**
	 * Sets the currentState.
	 * @param currentState The currentState to set
	 */
	public void setCurrentState(LiteState currentState) {
		this.currentState = currentState;
	}
	
	public String getImageName() {
		String imageName = Util.DEFAULT_IMAGE_NAME;
		
		LiteState state = getCurrentState();
		if(state != null) {
			int imageID = state.getImageID();
			LiteYukonImage lyi = YukonImageFuncs.getLiteYukonImage(imageID);
			if(lyi != null) {
				imageName = lyi.getImageName();
			}
		}
			return imageName;		
		}
	/**
	 * Updates the elements actual image with the currentStates
	 * Displays the default image if there is no current state
	 * available
	 * image
	 */
	public void updateImage() {
		LiteState state = getCurrentState();
		byte[] imgBuf = Util.DEFAULT_IMAGE_BYTES;
		
		if( state != null ) {
			int imageID = getCurrentState().getImageID();
			LiteYukonImage lyi = YukonImageFuncs.getLiteYukonImage(imageID);		
			if( lyi != null ) {
				imgBuf = lyi.getImageValue();
			}		
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

}
