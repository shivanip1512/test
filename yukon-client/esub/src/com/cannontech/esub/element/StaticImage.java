package com.cannontech.esub.element;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.persist.PersistStaticImage;
import com.cannontech.esub.util.Util;
import com.loox.jloox.LxAbstractImage;

/**
 * Creation date: (1/22/2002 10:15:09 AM)
 * @author: alauinger
 */
public class StaticImage extends LxAbstractImage implements DrawingElement, YukonImageElement {
	
	private static final String ELEMENT_ID = "staticImage";	
	private static final int CURRENT_VERSION = 1;
	
	private LiteYukonImage yukonImage = LiteYukonImage.NONE_IMAGE;
	
	private Drawing drawing;
	private String linkTo = null;
	private Properties props = new Properties();
	private int version = CURRENT_VERSION; 
	
/**
 * StaticImage constructor comment.
 */
public StaticImage() {
	super();
	initialize();
}

/**
 * Creation date: (1/22/2002 10:20:30 AM)
 */
private void initialize() {
	yukonImage.setImageValue(Util.DEFAULT_IMAGE_BYTES);
	setYukonImage(yukonImage);
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
	 * Returns the yukonImage.
	 * @return LiteYukonImage
	 */
	public LiteYukonImage getYukonImage() {
		return yukonImage;
	}

	/**
	 * Sets the yukonImage.
	 * @param yukonImage The yukonImage to set
	 */
	public void setYukonImage(LiteYukonImage yukonImage) {
		if(yukonImage == null) {
			this.yukonImage = LiteYukonImage.NONE_IMAGE;
			this.yukonImage.setImageValue(Util.DEFAULT_IMAGE_BYTES);
		}
		else {
			this.yukonImage = yukonImage;
		}
			
		setImage( Util.prepareImage(this.yukonImage.getImageValue()));
	}
	
	/**
	 * Sets the yukonImage by image name
	 * @param imageName
	 */
	public void setYukonImage(String imageName) {
		setYukonImage(YukonImageFuncs.getLiteYukonImage(imageName));
	}
	
	public void setImage(Image img) {
		if( img != null ) 
			setSize(img.getWidth(null), img.getHeight(null));
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
		PersistStaticImage.getInstance().readFromJLX(this,in);
}

/**
 * Creation date: (12/17/2001 3:49:44 PM)
 * @param out java.io.OutputStream
 */
public synchronized void saveAsJLX(OutputStream out) throws IOException 
{
        super.saveAsJLX(out);
		PersistStaticImage.getInstance().saveAsJLX(this,out);
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
}
