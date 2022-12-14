package com.cannontech.common.gui.image;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.util.HashMap;

/**
 * Loads and caches images.
 * Allows lookup of images by key.
 * Creation date: (1/8/2002 2:41:17 PM)
 * @author:  Aaron Lauinger
 */
public class ImageCache {
	private static ImageCache instance = null;
	
	// Map to store images
	//(key = String) (value = Image)
	private HashMap imageMap = new HashMap();

/**
 * Image constructor comment.
 */
private ImageCache() {
	super();
}

/**
 * Returns an image. will return a cached version if available.
 * @param imageName
 * @return Image
 */
public Image getImage(String imageName) {
	return getImage(imageName, null);
}
/**
 * Returns an image, will return the cached version if available.
 * If component is not null, getImage will return when the image is 
 * finished loading.
 * Creation date: (1/8/2002 2:45:28 PM)
 * @return java.awt.Image
 * @param imageName java.lang.String
 * @param imageName java.awt.Component
 */
public synchronized Image getImage(String imageName, Component comp) {
	imageName = imageName.replace('\\', '/');
	
	Image i = (Image) imageMap.get(imageName);
	
	if( i == null ) {		
		i = loadImage(imageName);
		if( i != null ) {
			imageMap.put(imageName, i);
		}
	}
	
	if( comp != null ) {
		// Wait for the image to be prepared.
		MediaTracker mt = new MediaTracker(comp);
		mt.addImage(i, 0);
		try {
			mt.waitForID(0);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	return i;	
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
 * Clears all images from the cache.
 */
public synchronized void clear() {
	imageMap.clear();
}

/**
 * Loads an image from the filesystem
 * Creation date: (1/8/2002 2:47:15 PM)
 * @return java.awt.Image
 * @param name java.lang.String
 */
private Image loadImage(String name) {
	return Toolkit.getDefaultToolkit().createImage(name);
}


}
