package com.cannontech.common.gui.image;

import com.cannontech.common.util.*;

/**
 * Creation date: (1/21/2002 3:16:08 PM)
 * @author: 
 */
public class ImageChooser {
	private static javax.swing.JFileChooser instance = null;

/**
 * ImageChooser constructor comment.
 */
private ImageChooser() {
	super();
}
	public static synchronized javax.swing.JFileChooser getInstance() {
		if( instance == null ) {
			instance = new javax.swing.JFileChooser();
			instance.addChoosableFileFilter(new ImageFilter());
			instance.setFileView(new ImageFileView());
			instance.setAccessory(new ImagePreview(instance));			
		}

		return instance;
	}
}
