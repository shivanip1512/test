package com.cannontech.esub.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;

import sun.awt.image.ByteInterleavedRaster;

/**
 * Creation date: (1/14/2002 4:28:01 PM)
 * @author: 
 */
public class Util {
	 public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";

    private static javax.swing.JColorChooser colorChooser = null;
    private static javax.swing.JFileChooser drawingFileChooser = null;
/**
 * Util constructor comment.
 */
public Util() {
	super();
}
public static synchronized javax.swing.JFileChooser getDrawingJFileChooser()
{
    if (drawingFileChooser == null) {
        drawingFileChooser = new javax.swing.JFileChooser();
        FileFilter filter = new FileFilter(new String("jlx"), "JLX files");
        drawingFileChooser.addChoosableFileFilter(filter);
    }

    return drawingFileChooser;
}
    /*
     * Get the extension of a file.
     */
    public static String getExtension(java.io.File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
public static synchronized javax.swing.JColorChooser getJColorChooser()
{
    if (colorChooser == null) {
        colorChooser = new javax.swing.JColorChooser();
    }

    return colorChooser;
}
/**
 * Determines the path to f2 from f1.
 * f1 = c:\temp\joe.txt
 * f2 = c:\temp\images\joe.gif
 * getRelativePath will return
 * note this only works if f2 is below f1
 * images\joe.gif
*/	
public static String getRelativePath(java.io.File f1, java.io.File f2) throws java.io.IOException{
	String p1 = f1.getCanonicalPath();
	String p2 = f2.getCanonicalPath();

	// strip the file off the path to f1
	p1 = p1.substring(0, p1.lastIndexOf(java.io.File.separatorChar) );
	
	if( p2.indexOf(p1) == -1 ) // start of paths do not match
		return null;	
	p2 = p2.substring(p1.length()+1);
	
	return p2;
}
}
