package com.cannontech.dbeditor.wizard.device.lmprogram;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.spring.YukonSpringHook;

public class LMProgramTypePanel extends com.cannontech.common.gui.util.DataInputPanel {
    ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButton ivjJRadioButtonDirectControl = null;
    private JRadioButton ivjJRadioButtonSepControl = null;
    private JRadioButton ivjJRadioButtonEcobeeControl = null;
    private JRadioButton ivjJRadioButtonHoneywellControl = null;
    private JRadioButton ivjJRadioButtonItronControl = null;
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
    
    // honeywell program
    private JRadioButton getJRadioButtonHoneywellControl() {
        if (ivjJRadioButtonHoneywellControl == null) {
            try {
                ivjJRadioButtonHoneywellControl = new JRadioButton();
                ivjJRadioButtonHoneywellControl.setName("HoneywellControlButton");
                ivjJRadioButtonHoneywellControl.setMnemonic('h');
                ivjJRadioButtonHoneywellControl.setText("Honeywell Control");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButtonHoneywellControl;
    }
    
    // itron program
    private JRadioButton getJRadioButtonItronControl() {
        if (ivjJRadioButtonItronControl == null) {
            try {
                ivjJRadioButtonItronControl = new JRadioButton();
                ivjJRadioButtonItronControl.setName("ItronControlButton");
                ivjJRadioButtonItronControl.setMnemonic('n');
                ivjJRadioButtonItronControl.setText("Itron Control");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButtonItronControl;
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
         else if( getJRadioButtonHoneywellControl().isSelected() )
         {
             return PaoType.LM_HONEYWELL_PROGRAM;
         }
         else if( getJRadioButtonItronControl().isSelected() )
         {
             return PaoType.LM_ITRON_PROGRAM;
         } else {
            throw new Error(getClass() + "::getLMSelectedType() - No radio button is selected");
        }
    }
    /**
     * getValue method comment.
     */
    @Override
    public Object getValue(Object o) {
        //create a new LMBase here
        return LMFactory.createLoadManagement(getLMSelectedType());
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
            constraintsJRadioButtonDirectControl.gridx = 1; 
            constraintsJRadioButtonDirectControl.gridy = 1;
            constraintsJRadioButtonDirectControl.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonDirectControl.ipadx = 41;
            constraintsJRadioButtonDirectControl.insets = new Insets(100, 105, 8, 101);
            add(getJRadioButtonDirectControl(), constraintsJRadioButtonDirectControl);
    
            
            GridBagConstraints constraintsJRadioButtonEnergyExchange = new GridBagConstraints();
            constraintsJRadioButtonEnergyExchange.gridx = 1; 
            constraintsJRadioButtonEnergyExchange.gridy = 2;
            constraintsJRadioButtonEnergyExchange.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonEnergyExchange.ipadx = 21;
            constraintsJRadioButtonEnergyExchange.insets = new Insets(9, 105, 8, 101);
            add(getJRadioButtonSepControl(), constraintsJRadioButtonEnergyExchange);
    
            
            GridBagConstraints constraintsJRadioButtonEcobeeProgram = new GridBagConstraints();
            constraintsJRadioButtonEcobeeProgram.gridx = 1; 
            constraintsJRadioButtonEcobeeProgram.gridy = 3;
            constraintsJRadioButtonEcobeeProgram.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonEcobeeProgram.ipadx = 1;
            constraintsJRadioButtonEcobeeProgram.insets = new Insets(9, 105, 8, 101);
            add(getJRadioButtonEcobeeControl(), constraintsJRadioButtonEcobeeProgram);
            
            GridBagConstraints constraintsJRadioButtonItronProgram = new GridBagConstraints();
            constraintsJRadioButtonItronProgram.gridx = 1; 
            constraintsJRadioButtonItronProgram.gridy = 5;
            constraintsJRadioButtonItronProgram.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonItronProgram.ipadx = 1;
            constraintsJRadioButtonItronProgram.insets = new Insets(9, 105, 10, 101);
            add(getJRadioButtonItronControl(), constraintsJRadioButtonItronProgram);
            
            ConfigurationSource masterConfigSource =
                YukonSpringHook.getBean("configurationSource", ConfigurationSource.class);
            boolean honeywellEnabled =
                masterConfigSource.getBoolean(MasterConfigBoolean.HONEYWELL_SUPPORT_ENABLED, false);
            if (honeywellEnabled) {
                GridBagConstraints constraintsJRadioButtonHoneywellProgram = new GridBagConstraints();
                constraintsJRadioButtonHoneywellProgram.gridx = 1;
                constraintsJRadioButtonHoneywellProgram.gridy = 6;
                constraintsJRadioButtonHoneywellProgram.anchor = GridBagConstraints.WEST;
                constraintsJRadioButtonHoneywellProgram.ipadx = 1;
                constraintsJRadioButtonHoneywellProgram.insets = new Insets(9, 105, 151, 101);
                add(getJRadioButtonHoneywellControl(), constraintsJRadioButtonHoneywellProgram);
            }

            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        buttonGroup.add( getJRadioButtonDirectControl() );
        buttonGroup.add( getJRadioButtonSepControl() );
        buttonGroup.add( getJRadioButtonEcobeeControl() );
        buttonGroup.add( getJRadioButtonHoneywellControl() );
        buttonGroup.add( getJRadioButtonItronControl() );
    
        // default selected button
        getJRadioButtonDirectControl().setSelected(true);
        
    }

    /**
     * setValue method comment.
     */
    @Override
    public void setValue(Object o) {
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater( new Runnable() 
            { 
            @Override
            public void run() 
                { 
                getJRadioButtonDirectControl().requestFocus(); 
            } 
        });    
    }

}
