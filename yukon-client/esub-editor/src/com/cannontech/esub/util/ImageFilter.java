package com.cannontech.esub.util;

import java.io.File;

import com.cannontech.esub.util.Util;

public class ImageFilter extends FileFilter {
    
    // Accept all directories and all gif, jpg, or tiff files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Util.getExtension(f);
	if (extension != null) {
            if (extension.equals(Util.tiff) ||
                extension.equals(Util.tif) ||
                extension.equals(Util.gif) ||
                extension.equals(Util.jpeg) ||
                extension.equals(Util.jpg)) {
                    return true;
            } else {
                return false;
            }
    	}

        return false;
    }
    // The description of this filter
    public String getDescription() {
        return "Just Images";
    }
}
