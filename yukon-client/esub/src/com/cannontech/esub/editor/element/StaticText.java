package com.cannontech.esub.editor.element;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.cannontech.esub.editor.Drawing;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxSaveUtils;

/**
 * Creation date: (1/23/2002 11:36:05 AM)
 * @author: 
 */
public class StaticText extends LxAbstractText implements DrawingElement {
	static final Font DEFAULT_FONT = new java.awt.Font("arial", java.awt.Font.BOLD, 12);
	static final Color DEFAULT_COLOR = java.awt.Color.white;
	
	private Drawing drawing;
	private Properties props = new Properties();
/**
 * StaticText constructor comment.
 */
public StaticText() {
	super();
	initialize();
}

/**
 * Creation date: (1/23/2002 12:08:38 PM)
 */
private void initialize() {
	setText("");
	setFont(DEFAULT_FONT);	
	setPaint(DEFAULT_COLOR);
}

/**
 * Creation date: (1/23/2002 11:39:22 AM)
 * @param name java.lang.String
 * @param size int
 */
public void setFont(String name, int size) {
	setFont( new java.awt.Font(name, java.awt.Font.PLAIN, size));
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

        String fontName = LxSaveUtils.readString(in);
        int fontSize = LxSaveUtils.readInt(in);
        
        setFont(fontName, fontSize);

        //read color
        int r = LxSaveUtils.readInt(in);
        int g = LxSaveUtils.readInt(in);
        int b = LxSaveUtils.readInt(in);

        Color c = new Color(r, g, b);
        setPaint(c);
        
        //restore link
        //setLinkTo( LxSaveUtils.readString(in) );
                
        LxSaveUtils.readEndOfPart(in);
}
/**
 * Creation date: (12/17/2001 3:49:44 PM)
 * @param out java.io.OutputStream
 */
public synchronized void saveAsJLX(OutputStream out) throws IOException 
{
        super.saveAsJLX(out);

        java.awt.Font f = getFont();
        
        LxSaveUtils.writeString(out, f.getFontName() );
        LxSaveUtils.writeInt(out, f.getSize() );

        //save color
        Color textColor = (Color) getPaint();
        LxSaveUtils.writeInt(out, textColor.getRed());
        LxSaveUtils.writeInt(out, textColor.getGreen());
        LxSaveUtils.writeInt(out, textColor.getBlue());
        
        //save link
       // LxSaveUtils.writeString(out, getLinkTo() );
        
        LxSaveUtils.writeEndOfPart(out);
}

}
