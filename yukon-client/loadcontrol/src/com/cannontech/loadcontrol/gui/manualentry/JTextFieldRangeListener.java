package com.cannontech.loadcontrol.gui.manualentry;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JTextField;

public class JTextFieldRangeListener implements FocusListener {
    private JTextField textField = null;
    private long min = Long.MIN_VALUE;
    private long max = Long.MAX_VALUE;
    //optional components
    private JButton control = null;
    
    public JTextFieldRangeListener(JTextField textField, long min, long max) {
        super();
        // TODO Auto-generated constructor stub
        this.textField = textField;
        this.min = min;
        this.max = max;
    }

    public JTextFieldRangeListener(JTextField textField) {
        super();
        this.textField = textField;
    }

    private JTextFieldRangeListener() {
        super();
    }

    public void focusGained(FocusEvent e) {
        //field was misused
        if((textField.getBackground().equals(Color.RED)) && getControl() != null)
        {
            textField.setBackground(Color.WHITE);
            getControl().setEnabled(true);
        }
    }

    public void focusLost(FocusEvent e) {
        String val = textField.getText();
        
        try 
        {
            long adjValue = Long.parseLong(val);
            if ((adjValue < min) || (adjValue > max)) 
            {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException nfe) 
        {
                textField.setText("");  
        }
    

    }

    public JButton getControl() {
        return control;
    }

    public void setControl(JButton control) {
        this.control = control;
    }


}
