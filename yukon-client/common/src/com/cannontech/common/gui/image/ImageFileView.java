package com.cannontech.common.gui.image;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

import com.cannontech.common.util.CtiUtilities;

public class ImageFileView extends FileView {
    ImageIcon jpgIcon = new ImageIcon("images/jpgIcon.gif");
    ImageIcon gifIcon = new ImageIcon("images/gifIcon.gif");
    ImageIcon tiffIcon = new ImageIcon("images/tiffIcon.gif");
    
    public String getDescription(File f) {
        return null; // let the L&F FileView figure this out
    }
    public Icon getIcon(File f) {
        String extension = CtiUtilities.getExtension(f);
        Icon icon = null;

        if (extension != null) {
            if (extension.equals(CtiUtilities.jpeg) ||
                extension.equals(CtiUtilities.jpg)) {
                icon = jpgIcon;
            } else if (extension.equals(CtiUtilities.gif)) {
                icon = gifIcon;
            } else if (extension.equals(CtiUtilities.tiff) ||
                       extension.equals(CtiUtilities.tif)) {
                icon = tiffIcon;
            } 
        }
        return icon;
    }
    public String getName(File f) {
        return null; // let the L&F FileView figure this out
    }
    public String getTypeDescription(File f) {
        String extension = CtiUtilities.getExtension(f);
        String type = null;

        if (extension != null) {
            if (extension.equals(CtiUtilities.jpeg) ||
                extension.equals(CtiUtilities.jpg)) {
                type = "JPEG Image";
            } else if (extension.equals(CtiUtilities.gif)){
                type = "GIF Image";
            } else if (extension.equals(CtiUtilities.tiff) ||
                       extension.equals(CtiUtilities.tif)) {
                type = "TIFF Image";
            } 
        }
        return type;
    }
    public Boolean isTraversable(File f) {
        return null; // let the L&F FileView figure this out
    }
}
