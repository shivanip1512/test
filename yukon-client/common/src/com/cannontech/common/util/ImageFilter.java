package com.cannontech.common.util;

import java.io.File;

import com.cannontech.common.util.*;

public class ImageFilter extends FileFilter {
    
    // Accept all directories and all gif, jpg, or tiff files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = CtiUtilities.getExtension(f);
	if (extension != null) {
            if (extension.equals(CtiUtilities.tiff) ||
                extension.equals(CtiUtilities.tif) ||
                extension.equals(CtiUtilities.gif) ||
                extension.equals(CtiUtilities.jpeg) ||
                extension.equals(CtiUtilities.jpg)) {
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
