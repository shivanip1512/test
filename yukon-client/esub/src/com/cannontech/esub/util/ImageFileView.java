package com.cannontech.esub.util;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

import com.cannontech.esub.util.Util;

public class ImageFileView extends FileView {
    ImageIcon jpgIcon = new ImageIcon("images/jpgIcon.gif");
    ImageIcon gifIcon = new ImageIcon("images/gifIcon.gif");
    ImageIcon tiffIcon = new ImageIcon("images/tiffIcon.gif");
    
    public String getDescription(File f) {
        return null; // let the L&F FileView figure this out
    }
    public Icon getIcon(File f) {
        String extension = Util.getExtension(f);
        Icon icon = null;

        if (extension != null) {
            if (extension.equals(Util.jpeg) ||
                extension.equals(Util.jpg)) {
                icon = jpgIcon;
            } else if (extension.equals(Util.gif)) {
                icon = gifIcon;
            } else if (extension.equals(Util.tiff) ||
                       extension.equals(Util.tif)) {
                icon = tiffIcon;
            } 
        }
        return icon;
    }
    public String getName(File f) {
        return null; // let the L&F FileView figure this out
    }
    public String getTypeDescription(File f) {
        String extension = Util.getExtension(f);
        String type = null;

        if (extension != null) {
            if (extension.equals(Util.jpeg) ||
                extension.equals(Util.jpg)) {
                type = "JPEG Image";
            } else if (extension.equals(Util.gif)){
                type = "GIF Image";
            } else if (extension.equals(Util.tiff) ||
                       extension.equals(Util.tif)) {
                type = "TIFF Image";
            } 
        }
        return type;
    }
    public Boolean isTraversable(File f) {
        return null; // let the L&F FileView figure this out
    }
}
