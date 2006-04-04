package com.cannontech.esub.element;

import java.awt.Image;
import java.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.plaf.IconUIResource;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.persist.PersistFunctionElement;
import com.cannontech.esub.element.persist.PersistStaticImage;
import com.cannontech.esub.util.Util;
import com.loox.jloox.LxAbstractImage;

/**
 * Creation date: (1/22/2002 10:15:09 AM)
 * @author: alauinger
 */
public class FunctionElement extends LxAbstractImage implements DrawingElement{
    
    private static final String ELEMENT_ID = "functionElement"; 
    private static final int CURRENT_VERSION = 1;
    private Drawing drawing;
    private String linkTo = null;
    private Properties props = new Properties();
    private int version = CURRENT_VERSION; 
    private Integer functionID = 0;
    private ArrayList argList = new ArrayList();
    private static final String CONTROL_BY_POINT = "submitControl(";
    private static Class imageClass = ImageIcon.class;
    private static ImageIcon icon = null;
    
    {
        byte[] imagebuffer = null;
        try 
        {
            InputStream resource = null;

            resource = imageClass.getResourceAsStream("/Function2.gif");

            BufferedInputStream in = new BufferedInputStream(resource);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) > 0) 
            {
                out.write(buffer, 0, n);
            }
            in.close();
            out.flush();
            imagebuffer = out.toByteArray();
        } catch (IOException ioe)
        {
            System.err.println(ioe.toString());
        }

        icon = new ImageIcon(imagebuffer);
    }

/**
 * StaticImage constructor comment.
 */
public FunctionElement() {
    super();
    initialize();
}

public ImageIcon getImageIcon()
{
    
    byte[] imagebuffer = null;
    try 
    {
        InputStream resource = null;

        resource = imageClass.getResourceAsStream("/Function2.gif");

        BufferedInputStream in = new BufferedInputStream(resource);
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

        byte[] buffer = new byte[1024];
        int n;
        while ((n = in.read(buffer)) > 0) 
        {
            out.write(buffer, 0, n);
        }
        in.close();
        out.flush();
        imagebuffer = out.toByteArray();
    } catch (IOException ioe)
    {
        System.err.println(ioe.toString());
    }

    return new ImageIcon(imagebuffer);
    
}

/**
 * Creation date: (1/22/2002 10:20:30 AM)
 */
private void initialize() {
    
    setImage(getImageIcon().getImage());
    //setImage(icon.getImage());
    
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
        PersistFunctionElement.getInstance().readFromJLX(this,in);
}

/**
 * Creation date: (12/17/2001 3:49:44 PM)
 * @param out java.io.OutputStream
 */
public synchronized void saveAsJLX(OutputStream out) throws IOException 
{
        super.saveAsJLX(out);
        PersistFunctionElement.getInstance().saveAsJLX(this,out);
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
    
    public void setFunctionID(Integer function )
    {
        functionID = function;
    }
    
    public Integer getFunctionID()
    {
        return functionID;
    }
    
    public void setArgList(ArrayList list)
    {
        argList = list;
    }
    
    public ArrayList getArgList()
    {
        return argList;
    }
    
    public String getScript()
    {
        
        String script = CONTROL_BY_POINT;
        String ret = new String(script);
        for(int i = 0; i < argList.size(); i++)
        {
            
            
            if(i  != argList.size()-1)
            {
                ret = ret + "\"";
                ret = ret + (argList.get(i));
                ret = ret + "\"";
                ret = ret+",";
            }else
            {
                ret = ret + argList.get(i);
            }
        }
        ret = ret + ");";
        System.out.println(ret);
        return ret;
    }

    public String getLinkTo() {
        return null;
    }

    public void setLinkTo(String linkTo) {
    }
    
}