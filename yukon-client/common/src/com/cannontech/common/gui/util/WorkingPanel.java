package com.cannontech.common.gui.util;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import java.awt.BorderLayout;

/**
 * A panel that has a an optional image, a title, a message, and an indeterminate progress bar.
 * @author aaron
 */

public class WorkingPanel extends JPanel {
	
    public WorkingPanel(String title, String message) {
    	 this(title,message,null); 
    }

    public WorkingPanel(String title, String message, String imageFileName) {
 		if(imageFileName != null) {		
        	ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(imageFileName));
        	JLabel   label = new JLabel("", icon, JLabel.CENTER);
        	icon.setImageObserver(this);
        	label.setBorder( BorderFactory.createEmptyBorder(20,20,20,20) );
                
        	add(label, BorderLayout.WEST);
		}
		
        Box b = Box.createVerticalBox();
		b.add(Box.createVerticalStrut(5));
        b.add(new JLabel(message));
        b.add(Box.createVerticalStrut(5));
		JProgressBar myBar = new JProgressBar();
		myBar.setIndeterminate(true);
        b.add(myBar);
        add(b, BorderLayout.CENTER);       
        
       }  
}
        
