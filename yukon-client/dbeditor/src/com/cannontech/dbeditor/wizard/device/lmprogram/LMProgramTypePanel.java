package com.cannontech.dbeditor.wizard.device.lmprogram;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.lm.LMFactory;

public class LMProgramTypePanel extends com.cannontech.common.gui.util.DataInputPanel {
    ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButton ivjJRadioButtonDirectControl = null;
    private JRadioButton ivjJRadioButtonSepControl = null;
    private JRadioButton ivjJRadioButtonEcobeeControl = null;
    /**
     * Constructor
     */
    public LMProgramTypePanel() {
        initialize();
    }
    
    /**
     * Return the JRadioButtonDirectControl property value.
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getJRadioButtonDirectControl() {
        if (ivjJRadioButtonDirectControl == null) {
            try {
                ivjJRadioButtonDirectControl = new JRadioButton();
                ivjJRadioButtonDirectControl.setName("JRadioButtonDirectControl");
                ivjJRadioButtonDirectControl.setMnemonic('d');
                ivjJRadioButtonDirectControl.setText("Direct Control");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButtonDirectControl;
    }
    /**
     * Return the JRadioButtonSepControl property value.
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getJRadioButtonSepControl() {
        if (ivjJRadioButtonSepControl == null) {
            try {
                ivjJRadioButtonSepControl = new JRadioButton();
                ivjJRadioButtonSepControl.setName("SEPControlButton");
                ivjJRadioButtonSepControl.setMnemonic('s');
                ivjJRadioButtonSepControl.setText("SEP Control");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButtonSepControl;
    }
    
    //ecobee program
    private JRadioButton getJRadioButtonEcobeeControl() {
        if (ivjJRadioButtonEcobeeControl == null) {
            try {
                ivjJRadioButtonEcobeeControl = new JRadioButton();
                ivjJRadioButtonEcobeeControl.setName("EcobeeControlButton");
                ivjJRadioButtonEcobeeControl.setMnemonic('e');
                ivjJRadioButtonEcobeeControl.setText("ecobee Control");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButtonEcobeeControl;
    }
    /**
     * Insert the method's description here.
     * Creation date: (2/5/2001 10:32:24 AM)
     */
    public PaoType getLMSelectedType() {
        if( getJRadioButtonDirectControl().isSelected() )
        {
            return PaoType.LM_DIRECT_PROGRAM;
        }
         else if( getJRadioButtonSepControl().isSelected() )
         {
             return PaoType.LM_SEP_PROGRAM;
         }
         else if( getJRadioButtonEcobeeControl().isSelected() )
        {
            return PaoType.LM_ECOBEE_PROGRAM;
        }
         else
             throw new Error(getClass() + "::getLMSelectedType() - No radio button is selected");
    }
    /**
     * getValue method comment.
     */
    public Object getValue(Object o) {
        //create a new LMBase here
        return LMFactory.createLoadManagement(getLMSelectedType().getDeviceTypeId() );
    }
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {
    
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
    }
    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("VersacomRelayPanel");
            setLayout(new GridBagLayout());
            setSize(350, 300);
    
            GridBagConstraints constraintsJRadioButtonDirectControl = new GridBagConstraints();
            constraintsJRadioButtonDirectControl.gridx = 1; constraintsJRadioButtonDirectControl.gridy = 1;
            constraintsJRadioButtonDirectControl.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonDirectControl.ipadx = 41;
            constraintsJRadioButtonDirectControl.insets = new Insets(48, 105, 8, 101);
            add(getJRadioButtonDirectControl(), constraintsJRadioButtonDirectControl);
    
            
            GridBagConstraints constraintsJRadioButtonEnergyExchange = new GridBagConstraints();
            constraintsJRadioButtonEnergyExchange.gridx = 1; constraintsJRadioButtonEnergyExchange.gridy = 2;
            constraintsJRadioButtonEnergyExchange.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonEnergyExchange.ipadx = 21;
            constraintsJRadioButtonEnergyExchange.insets = new Insets(9, 105, 8, 101);
            add(getJRadioButtonSepControl(), constraintsJRadioButtonEnergyExchange);
    
            
            GridBagConstraints constraintsJRadioButtonEcobeeProgram = new GridBagConstraints();
            constraintsJRadioButtonEcobeeProgram.gridx = 1; constraintsJRadioButtonEcobeeProgram.gridy = 3;
            constraintsJRadioButtonEcobeeProgram.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonEcobeeProgram.ipadx = 1;
            constraintsJRadioButtonEcobeeProgram.insets = new Insets(9, 105, 151, 101);
            add(getJRadioButtonEcobeeControl(), constraintsJRadioButtonEcobeeProgram);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        buttonGroup.add( getJRadioButtonDirectControl() );
        buttonGroup.add( getJRadioButtonSepControl() );
        buttonGroup.add( getJRadioButtonEcobeeControl() );
    
        // default selected button
        getJRadioButtonDirectControl().setSelected(true);
        
    }
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame();
            LMProgramBasePanel aLMProgramBasePanel;
            aLMProgramBasePanel = new LMProgramBasePanel();
            frame.setContentPane(aLMProgramBasePanel);
            frame.setSize(aLMProgramBasePanel.getSize());
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.show();
            java.awt.Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
            com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
        }
    }
    /**
     * setValue method comment.
     */
    public void setValue(Object o) {
    }

    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater( new Runnable() 
            { 
            public void run() 
                { 
                getJRadioButtonDirectControl().requestFocus(); 
            } 
        });    
    }

}
