package com.cannontech.esub.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.*;
import com.cannontech.esub.editor.Drawing;

/**
 * Creation date: (1/14/2002 4:28:01 PM)
 * @author: 
 */
public class Util {
    
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
/* 
 public static Image[] getStaticButtons() {
 	String sep = "/";
 	String home = CtiProperties.getInstance().getProperty(CtiProperties.KEY_YUKON_HOME, "c:" + sep + "yukon");
 	String buttonHome = home + sep + "client" + sep + "esub" + sep + "images" + sep + "buttons";
 	
 	File buttonDir = new File(buttonHome);
 	if( !buttonDir.exists() || !buttonDir.isDirectory()) {
 		CTILogger.error("Can't find images at " + buttonDir.getPath());
 		return null;
 	}
 	
	return loadAllImagesInDir(buttonDir); 	
 }
 
 
 public static Image[] getOnelineImages() {
 	String sep = "/";
 	String home = CtiProperties.getInstance().getProperty(CtiProperties.KEY_YUKON_HOME, "c:" + sep + "yukon");
 	String onelineHome = home + sep + "client" + sep + "esub" + sep + "images" + sep + "oneline";
 	
 	File onelineDir = new File(onelineHome);
 	if( !onelineDir.exists() || !onelineDir.isDirectory()) {
 		CTILogger.error("Can't find images at " + onelineDir.getPath());
 		return null;
 	}
 	
	return loadAllImagesInDir(onelineDir); 		
 }

 private static Image[] loadAllImagesInDir(File dir) {
 	File[] all = dir.listFiles();
 	ArrayList images = new ArrayList(all.length);
 	ImageFilter filter = new ImageFilter();
 	for( int i = 0; i < all.length; i++ ) {
 		if( filter.accept(all[i]) ) {
 			images.add( ImageCache.getInstance().getImage(all[i].getPath())); 			
 		}
 	}
 	
 	Image[] iArr = new Image[images.size()];
 	images.toArray(iArr);
 	return iArr;	
 } */
}
