package com.cannontech.esub.editor.element;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.util.Util;
import com.loox.jloox.LxAbstractImage;
import com.loox.jloox.LxSaveUtils;
/**
 * Creation date: (1/22/2002 10:15:09 AM)
 * @author: 
 */
public class StaticImage extends LxAbstractImage implements DrawingElement {
	
	private LiteYukonImage yukonImage = LiteYukonImage.NONE_IMAGE;
	
	private Drawing drawing;
	private String linkTo = null;
	private Properties props = new Properties();
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
		this.yukonImage = yukonImage;
		setImage( Util.prepareImage(yukonImage.getImageValue()));
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

        int imgID = LxSaveUtils.readInt(in);
        LiteYukonImage img = YukonImageFuncs.getLiteYukonImage(imgID);
        setYukonImage(img);
        setLinkTo(LxSaveUtils.readString(in));
        
        LxSaveUtils.readEndOfPart(in);
}
/**
 * Creation date: (12/17/2001 3:49:44 PM)
 * @param out java.io.OutputStream
 */
public synchronized void saveAsJLX(OutputStream out) throws IOException 
{
        super.saveAsJLX(out);

        LxSaveUtils.writeInt(out, getYukonImage().getImageID());
        LxSaveUtils.writeString(out, getLinkTo());
        
        LxSaveUtils.writeEndOfPart(out);
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

}
