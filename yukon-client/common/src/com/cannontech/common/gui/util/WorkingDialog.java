package com.cannontech.common.gui.util;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;

import java.awt.BorderLayout;
import java.awt.Frame;

/**
 * A dialog that has a an optional image, a title, a message, and an indeterminate progress bar.
 * @author aaron
 */

public class WorkingDialog extends JDialog {
	
    public WorkingDialog(Frame containingFrame, String title, String message) {
    	 this(containingFrame,title,message,null); 
    }

    public WorkingDialog(Frame containingFrame, String title, String message, String imageFileName) {
        super(containingFrame,title);

		if(imageFileName != null) {		
        	ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(imageFileName));
        	JLabel   label = new JLabel("", icon, JLabel.CENTER);
        	icon.setImageObserver(this);
        	label.setBorder( BorderFactory.createEmptyBorder(20,20,20,20) );
                
        	getContentPane().add(label, BorderLayout.WEST);
		}
		
        Box b = Box.createVerticalBox();
		b.add(Box.createVerticalStrut(5));
        b.add(new JLabel(message));
        b.add(Box.createVerticalStrut(5));
		JProgressBar myBar = new JProgressBar();
		myBar.setIndeterminate(true);
        b.add(myBar);
        getContentPane().add(b, BorderLayout.CENTER);       
        
       }

    public void show() { 
        pack(); 
        setLocationRelativeTo(getParent()); 
        super.show();
    }
}
        
