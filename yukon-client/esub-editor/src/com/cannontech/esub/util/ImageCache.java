package com.cannontech.esub.util;

import java.util.HashMap;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.*;

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
 * Returns an image, will return the cached version if available.
 * Creation date: (1/8/2002 2:45:28 PM)
 * @return java.awt.Image
 * @param imageName java.lang.String
 */
public Image getImage(String imageName) {
	imageName = imageName.replace('\\', '/');
	
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
 * Loads an image from the classpath
 * Creation date: (1/8/2002 2:47:15 PM)
 * @return java.awt.Image
 * @param name java.lang.String
 */
private Image loadImage(String name) {

	return Toolkit.getDefaultToolkit().getImage(name);
}


}
