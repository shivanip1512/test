package com.cannontech.common.gui.util;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JDialog;

/**
 * A dialog that has a an optional image, a title, a message, and an indeterminate progress bar.
 * @author aaron
 */

public class WorkingDialog extends JDialog {
	private final WorkingPanel workingPanel;
	
    public WorkingDialog(Frame containingFrame, String title, String message) {
    	 this(containingFrame,title,message,null); 
    }

    public WorkingDialog(Frame containingFrame, String title, String message, String imageFileName) {
        super(containingFrame,title);
		workingPanel = new WorkingPanel(title,message,imageFileName);
		getContentPane().add(workingPanel, BorderLayout.CENTER);
       }

    public void show() { 
        pack(); 
        setLocationRelativeTo(getParent()); 
        super.show();
    }
}
        
