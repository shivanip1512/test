package com.cannontech.esub.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JComponent;

import com.cannontech.common.util.FileFilter;
import com.cannontech.esub.editor.Drawing;

import com.loox.jloox.LxGraph;
import com.loox.jloox.LxRotatable;

/**
 * Creation date: (1/14/2002 4:28:01 PM)
 * @author: 
 */
public class Util {
    public static byte[] DEFAULT_IMAGE_BYTES = { 71,73,70,56,57,97,30,0,30,0,-14,0,0,0,0,0,-52,-52,-52,51,51,51,-46,-46,-46,-1,-1,-1,66,66,66,-38,-38,-38,-5,-99,-89,33,-7,4,0,0,0,0,0,44,0,0,0,0,30,0,30,0,64,3,-121,8,-70,-36,-2,43,-56,105,-120,-103,-104,-38,28,20,-49,-107,17,126,-104,39,-115,-28,-121,118,-64,-71,-91,36,106,-58,-17,-6,-51,-104,-99,-65,25,80,-16,-80,96,-91,0,-48,5,57,33,-100,-111,118,-103,-32,118,77,40,-23,-87,34,0,-89,45,-90,20,11,-70,86,-93,18,-49,-14,24,24,41,6,96,-78,106,32,-10,-86,-51,-39,-78,59,-75,122,-114,-73,-50,56,-66,-101,14,-21,-7,123,61,127,72,22,119,126,71,21,115,37,-125,91,-122,84,46,125,99,84,-122,70,74,-118,-128,121,-112,106,91,10,-122,-120,22,0,2,68,16,-92,16,5,2,9,0,59 };
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
 
 public static Image prepareImage(byte[] imageBuf) {
 	Image img = Toolkit.getDefaultToolkit().createImage(imageBuf);
	Component comp = new JComponent() { };
 	 	  	
 	// Wait for the image to be prepared.
    MediaTracker mt = new MediaTracker(comp);
    mt.addImage(img, 0);
    try {
    	mt.waitForID(0);
    } catch(InterruptedException e) {
    	e.printStackTrace();
         }
   
    return img;       
 }
 
/**
 * Rotate all the selected elements in g by the number
 * of radians specified by r
 * @param g
 * @param r
 */
 public static void rotateSelected(LxGraph g, double r) {
 	Object[] sel = g.getSelectedObjects();
 	for( int i = 0; i < sel.length; i++ ) {
 		if( sel[i] instanceof LxRotatable ) {
 			LxRotatable rot = (LxRotatable) sel[i];
 			rot.rotate(r);
 			//LxComponent comp = (LxComponent) sel[i]; 		
 			//comp.rotateCenter(r, comp.getCenter()); 
 		}
 	}
 }
}
