package com.cannontech.dbeditor.wizard.device.lmprogram;

import javax.swing.JComboBox;

import com.cannontech.database.db.device.lm.IlmDefines;

/**
 * Insert the type's description here.
 * Creation date: (8/1/2002 2:59:27 PM)
 * @author: 
 */

public class GenericGearPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, javax.swing.event.CaretListener {

    protected class NewComboBoxEditor extends javax.swing.plaf.basic.BasicComboBoxEditor
    {
        public javax.swing.JTextField getJTextField()
        {
            //create this method so we don't have to cast the getEditorComponent() call
            return editor;
        }

    }
    /**
     * GenericGearPanel constructor comment.
     */
    public GenericGearPanel() {
        super();
        initialize();
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {}


    /**
     * Called when the caret position is updated.
     *
     * @param e the caret event
     */
    public void caretUpdate(javax.swing.event.CaretEvent e) {}

    /**
     * @return java.lang.Object
     * @param o java.lang.Object
     */
    @Override
    public Object getValue(Object o) {
        System.out.println("Generic get value I am in, my foolish apprentice.");
        return null;
    }


    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("GenericGearPanel");
            setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
            setLayout(null);
            setSize(402, 430);
            setMaximumSize(new java.awt.Dimension(402, 430));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    public void jComboBoxWhenChange_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
    { }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    @SuppressWarnings("deprecation")
    public static void main(java.lang.String[] args) {
        try {
            javax.swing.JFrame frame = new javax.swing.JFrame();
            GenericGearPanel aGenericGearPanel;
            aGenericGearPanel = new GenericGearPanel();
            frame.setContentPane(aGenericGearPanel);
            frame.setSize(aGenericGearPanel.getSize());
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.show();
            java.awt.Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
            exception.printStackTrace(System.out);
        }
    }


    /**
     * This method was created in VisualAge.
     * @param o java.lang.Object
     */
    @Override
    public void setValue(Object o) {
        System.out.println("Generic set value of this gear panel.");
    }


    /**
     * valueChanged method comment.
     */
    public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
    {
        //fire this event for all JCSpinFields!!
        this.fireInputUpdate();
    }
    /**
     * valueChanging method comment.
     */
    public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {}
    /**
     * Method getJComboBoxWhenChange.
     */

    public void setTargetCycle(boolean truth) {}

    protected void setChangeCondition(final JComboBox box, final String change) {
        if (change == null ) return;

        if (change.equalsIgnoreCase(IlmDefines.CHANGE_NONE)) {
            box.setSelectedItem("Manually Only");
            return;
        }
        
        if (change.equalsIgnoreCase(IlmDefines.CHANGE_DURATION)) {
            box.setSelectedItem("After a Duration");
            return;
        }
        
        if (change.equalsIgnoreCase(IlmDefines.CHANGE_PRIORITY)) {
            box.setSelectedItem("Priority Change");
            return;
        }
        
        if (change.equalsIgnoreCase(IlmDefines.CHANGE_TRIGGER_OFFSET)) {
            box.setSelectedItem("Above Trigger");
            return;
        }
    }
    
    protected String getChangeCondition(String change) {
        if (change.equalsIgnoreCase("After a Duration")) {
            
            return IlmDefines.CHANGE_DURATION;
            
        } else if (change.equalsIgnoreCase("Priority Change")) {
            
            return IlmDefines.CHANGE_PRIORITY;
            
        } else if (change.equalsIgnoreCase("Above Trigger")) {
            
            return IlmDefines.CHANGE_TRIGGER_OFFSET;
            
        } else {
            
            return IlmDefines.CHANGE_NONE;
            
        }

    }

}