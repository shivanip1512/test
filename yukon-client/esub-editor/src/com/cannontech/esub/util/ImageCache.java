package com.cannontech.esub.util;

import java.util.HashMap;
import java.awt.Image;
import java.net.*;

/**
 * Loads and stores images.
 * Allows lookup of images by key.
 * Creation date: (1/8/2002 2:41:17 PM)
 * @author:  Aaron Lauinger
 */
public class ImageCache {
	private static ImageCache instance = null;
	//(key = String) (value = Image)
	private HashMap imageMap = new HashMap();
	// Indicates where to get images from
	private String imageHost;
	private int imagePort;
	private String imageDir;
/**
 * Image constructor comment.
 */
private ImageCache() {
	super();
}
/**
 * Creation date: (1/8/2002 2:45:28 PM)
 * @return java.awt.Image
 * @param imageName java.lang.String
 */
public Image getImage(String imageName) {
	Image i = (Image) imageMap.get(imageName);
	
	if( i == null ) {
		synchronized(this) {
			i = loadImage(imageName);
			if( i != null ) {
				imageMap.put(imageName, i);
			}
		}
	}

	return i;	
}
/**
 * Creation date: (1/9/2002 1:46:22 PM)
 * @return java.lang.String
 */
public java.lang.String getImageDir() {
	return imageDir;
}
/**
 * Creation date: (1/9/2002 1:46:22 PM)
 * @return java.lang.String
 */
public java.lang.String getImageHost() {
	return imageHost;
}
/**
 * Creation date: (1/9/2002 1:46:22 PM)
 * @return int
 */
public int getImagePort() {
	return imagePort;
}
/**
 * Creation date: (1/9/2002 1:54:06 PM)
 * @return java.net.URL
 * @param imageName java.lang.String
 */
private URL getImageURL(String imageName) throws MalformedURLException {
	return new URL("http", getImageHost(), getImagePort(), getImageDir() + "/" + imageName);
}
/**
 * Creation date: (1/8/2002 2:44:15 PM)
 * @return com.cannontech.esub.util.ImageCache
 */
public static synchronized ImageCache getInstance() {
	if(instance == null)
		instance = new ImageCache();

	return instance;
}
/**
 * Loads an image from the classpath
 * Creation date: (1/8/2002 2:47:15 PM)
 * @return java.awt.Image
 * @param name java.lang.String
 */
private Image loadImage(String name) {

	Image i = null;
	
	try {
		URL u = getImageURL(name);
		System.out.println("Loading image from: " + u);
		i = new javax.swing.ImageIcon(u).getImage();
	}
	catch(MalformedURLException e ) {
		e.printStackTrace();
	}
	
	return i;
}
/**
 * Creation date: (1/9/2002 1:46:22 PM)
 * @param newImageDir java.lang.String
 */
public void setImageDir(java.lang.String newImageDir) {
	imageDir = newImageDir;
}
/**
 * Creation date: (1/9/2002 1:46:22 PM)
 * @param newImageHost java.lang.String
 */
public void setImageHost(java.lang.String newImageHost) {
	imageHost = newImageHost;
}
/**
 * Creation date: (1/9/2002 1:46:22 PM)
 * @param newImagePort int
 */
public void setImagePort(int newImagePort) {
	imagePort = newImagePort;
}
}
