package com.cannontech.esub.element;

import static java.lang.String.format;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.persist.PersistDrawingMetaElement;
import com.loox.jloox.LxAbstractText;

/**
 * The purpose of this element is to store meta information about a drawing.
 * It extends LxAbstractText only because its parents LxElement and LxComponent
 * have methods that are not visible outside of their package meaning I don't
 * think I can directory subclass them.
 * 
 * This class has no visible representation.
 * 
 * @author alauinger
 */
public class DrawingMetaElement extends LxAbstractText implements DrawingElement {

	private static final String ELEMENT_ID = "metaElement";
	
	private static final int CURRENT_VERSION = 2;
	
	private int drawingWidth;
	private int drawingHeight;
	private int drawingRGBColor = Color.BLACK.getRGB();
	private YukonRole role = YukonRole.OPERATOR_ESUBSTATION_DRAWINGS;
		
	private transient Drawing drawing = null;
	private String linkTo = null;
	private Properties props = new Properties();
	private int version = CURRENT_VERSION;
	
	public DrawingMetaElement() {
	}	
	
	/**
	 * @see com.loox.jloox.LxComponent#readFromJLX(InputStream, String)
	 */
	@Override
    public void readFromJLX(InputStream in, String version) throws IOException {
		super.readFromJLX(in, version);
		PersistDrawingMetaElement.getInstance().readFromJLX(this,in);
	}

	/**
	 * @see com.loox.jloox.LxComponent#saveAsJLX(OutputStream)
	 */
	@Override
    public void saveAsJLX(OutputStream out) throws IOException {
		super.saveAsJLX(out);
		PersistDrawingMetaElement.getInstance().saveAsJLX(this,out);
	}

	/**
	 * Returns the drawingHeight.
	 * @return int
	 */
	public int getDrawingHeight() {
		return drawingHeight;
	}

	/**
	 * Returns the drawingWidth.
	 * @return int
	 */
	public int getDrawingWidth() {
		return drawingWidth;
	}

	/**
	 * Sets the drawingHeight.
	 * @param drawingHeight The drawingHeight to set
	 */
	public void setDrawingHeight(int drawingHeight) {
		this.drawingHeight = drawingHeight;
	}

	/**
	 * Sets the drawingWidth.
	 * @param drawingWidth The drawingWidth to set
	 */
	public void setDrawingWidth(int drawingWidth) {
		this.drawingWidth = drawingWidth;
	}

	/**
	 * Returns the version.
	 * @return int
	 */
	@Override
    public int getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 * @param version The version to set
	 */
	@Override
    public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#getDrawing()
	 */
	@Override
    public Drawing getDrawing() {
		return drawing;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#setDrawing(Drawing)
	 */
	@Override
    public void setDrawing(Drawing d) {
		drawing = d;
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
	 * Returns the linkTo.
	 * @return String
	 */
	@Override
    public String getLinkTo() {
		return linkTo;
	}

	/**
	 * Sets the linkTo.
	 * @param linkTo The linkTo to set
	 */
	@Override
    public void setLinkTo(String linkTo) {
		this.linkTo = linkTo;
	}

	public YukonRole getRole() {
        return role;
    }
	
	public void setRole(YukonRole role) {
        this.role = role;
    }
	
	@Override
    public boolean isCopyable() {
		return false;
	}
	
	@Override
    public String getElementID() {
		return ELEMENT_ID;
	}

    public int getDrawingRGBColor() {
        return drawingRGBColor;
    }
    
    public String getDrawingHTMLColor() {
        Color color = new Color(drawingRGBColor);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        String htmlColorCode = format("#%02X%02X%02X", red, green, blue);
        return htmlColorCode;
    }

    public void setDrawingRgbColor(int drawingRGBColor) {
        this.drawingRGBColor = drawingRGBColor;
    }
    
}
