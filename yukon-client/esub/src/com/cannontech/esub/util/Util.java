package com.cannontech.esub.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import com.cannontech.esub.editor.Drawing;

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
 * Generates an absolute path from the drawings location
 * and the relative path given.
 * @param d
 * @param relPath
 * @return String
 */
public static String getAbsolutePath(Drawing d, String relPath) {
	return new File(d.getFileName()).getParent() + "/" + relPath;
}
	
/**
 * Determines the image in image paths relative path to the drawing
 * Returns null if it cannot be determined
 * @param d
 * @param imagePath
 * @return String
 */
public static String getRelativePath(Drawing d, String absImagePath) {	
	String dFileName = d.getFileName();
	
	if( dFileName == null ) {
		return null;
	}
	
	File dFile = new File(d.getFileName());
	File iFile = new File(absImagePath);
	
	String relPath = null;
	
	try {
		relPath = getRelativePath(dFile,iFile);	
	}
	catch(IOException e) {
	}
	
	return relPath;
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

public static Image findImage(String imageName)
{
  if( !imageName.startsWith("/") ) {
  	imageName = "/" + imageName;
  }
 
  URL imageURL = Util.class.getResource(imageName);
  
  if( imageURL == null ) {
  	Logger.global.warning("Could not locate: " + imageName);
  	return null;
  }
  	
  return(Toolkit.getDefaultToolkit().getImage(imageURL));
 }
 
/**
 * Loads an image from the filesystem relative to the drawing.
 * @param d
 * @param image
 * @return Image
 */
 /*public static Image loadImage(Drawing d, String image) {
 	String drawingPath = (new File( d.getFileName() )).getParent();
 	String imagePath = drawingPath + "/" + image;
 	
	return Toolkit.getDefaultToolkit().getImage(imagePath);
 	
 }*/
 
 /**
 * Loads an image from the filesystem.
 * @param image
 * @return Image
 */
 public static Image loadImage(String imageFile) { 	
	return Toolkit.getDefaultToolkit().getImage(imageFile);
 	
 }


}
