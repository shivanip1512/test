package com.cannontech.dbeditor.wizard.state;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JRadioButton;

import com.cannontech.database.db.state.StateGroupUtils;

public class GroupTypePanel extends com.cannontech.common.gui.util.DataInputPanel implements ActionListener  {
    
    private JRadioButton StatusRadioButton;
    private JRadioButton AnalogRadioButton;
    private javax.swing.ButtonGroup buttonGroup = new javax.swing.ButtonGroup();
    private String type = StateGroupUtils.GROUP_TYPE_STATUS;
    
    
    public GroupTypePanel() {
        super();
        initialize();
        initializeConnections();
    }
    
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == getAnalogRadioButton())
        {
            setGroupType(StateGroupUtils.GROUP_TYPE_ANALOG);
        }else
        {
           setGroupType(StateGroupUtils.GROUP_TYPE_STATUS);
        }
    }
    
    private void initializeConnections()
    {
        getAnalogRadioButton().addActionListener(this);
        getStatusRadioButton().addActionListener(this);
    }
    
    /**
     * Return the StatusTypeLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JRadioButton getStatusRadioButton() {
        if (StatusRadioButton == null) {
            try {
                StatusRadioButton = new javax.swing.JRadioButton();
                StatusRadioButton.setName("StatusRadioButton");
                StatusRadioButton.setSelected(true);
                StatusRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
                StatusRadioButton.setText("Status State Group");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return StatusRadioButton;
    }
    
    /**
     * Return the AnalogTypeLabel property value.
     * @return javax.swing.JLabel
     */
    private JRadioButton getAnalogRadioButton() {
        if (AnalogRadioButton == null) {
            try {
                AnalogRadioButton = new javax.swing.JRadioButton();
                AnalogRadioButton.setName("AnalogRadioButtonl");
                AnalogRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
                AnalogRadioButton.setText("Analog State Group");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return AnalogRadioButton;
    }
    
    public Object getValue(Object val)
    {
        com.cannontech.database.data.state.GroupState gs = com.cannontech.database.data.state.StateFactory.createGroupState();
        
        if ( getStatusRadioButton().isSelected())
        {
            gs.setGroupType(StateGroupUtils.GROUP_TYPE_STATUS);
        }else 
        {
            gs.setGroupType(StateGroupUtils.GROUP_TYPE_ANALOG);
        }
        return gs;
    }
    
    private void setGroupType(String grouptype)
    {
        type = grouptype;
    }
    
    public String getGroupType()
    {
        return type;
    }
    
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );
    }
    
    private void initialize()
    {
        try {

            setName("GroupTypePanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(350, 300);

            java.awt.GridBagConstraints constraintsJRadioButtonStatus = new java.awt.GridBagConstraints();
            constraintsJRadioButtonStatus.gridx = 1; constraintsJRadioButtonStatus.gridy = 1;
            constraintsJRadioButtonStatus.anchor = java.awt.GridBagConstraints.WEST;
            add(getStatusRadioButton(), constraintsJRadioButtonStatus);

            java.awt.GridBagConstraints constraintsJRadioButtonAnalog = new java.awt.GridBagConstraints();
            constraintsJRadioButtonAnalog.gridx = 1; constraintsJRadioButtonAnalog.gridy = 2;
            constraintsJRadioButtonAnalog.anchor = java.awt.GridBagConstraints.WEST;
            add(getAnalogRadioButton(), constraintsJRadioButtonAnalog);

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        buttonGroup.add( getStatusRadioButton() );
        buttonGroup.add( getAnalogRadioButton() );
        
        // default selected button
        getStatusRadioButton().setSelected(true);
    }
    
    /**
     * setValue method comment.
     */
    public void setValue(Object val) {
        
    }

    public void setFirstFocus() 
    {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater( new Runnable() 
            { 
            public void run() 
                { 
                getStatusRadioButton().requestFocus(); 
            } 
        });    
    }
    
    /**
     * This method was created in VisualAge.
     * @return boolean
     */
    public boolean isInputValid() 
    {
            return true;
    }
}
