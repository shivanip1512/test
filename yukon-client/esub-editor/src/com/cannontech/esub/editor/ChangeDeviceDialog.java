/**
 * 
 */
package com.cannontech.esub.editor;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.esub.editor.element.DeviceSelectionPanel;

/**
 * @author asolberg
 *
 */
public class ChangeDeviceDialog extends JDialog implements ActionListener{

    private Object[] comps;
    private Frame owner = null;
    private JPanel contentPanel = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private JPanel buttonPanel = null;
    private JPanel componentPanel = null;
    private DeviceSelectionPanel deviceSelectionPanel = null;
    
    public ChangeDeviceDialog(java.awt.Frame owner, Object[] components) {
        super(owner, true);
        this.owner = owner;
        this.comps = components;
        initialize();
    }
    
    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            
            setName("ChangeDevice");
            setTitle("Change Device");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setSize(520, 600);
            setContentPane(getContentPanel());
            this.validate();
            initConnections();
            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        if( owner != null )
            setLocationRelativeTo(this.owner);
        
        java.awt.event.WindowAdapter w = new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e )
            {
                dispose();
            }
        };

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener( w );
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == getOkButton()) {
            
            this.dispose();
        }else if (source == getCancelButton()) {
            this.dispose();
        }
    }
    
    /**
     * Initializes action listeners.
     */
    private void initConnections() throws java.lang.Exception {
        getOkButton().addActionListener(this);
        getCancelButton().addActionListener(this);
    }
    
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error( exception.getMessage(), exception );
    }
    
    /**
     * Sets up and returns the panel used for the content of this dialog.
     * @return javax.swing.JPanel
     */
    protected javax.swing.JPanel getContentPanel() {
        
        if(contentPanel == null) {
            contentPanel = new JPanel();
            contentPanel.setLayout(new java.awt.GridBagLayout());
            contentPanel.setSize( 500,600);
            
            java.awt.GridBagConstraints constraintsComponentPanel = new java.awt.GridBagConstraints();
            constraintsComponentPanel.gridx = 0; constraintsComponentPanel.gridy = 1;
            constraintsComponentPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsComponentPanel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsComponentPanel.gridwidth = 1;
            contentPanel.add(getComponentPanel(), constraintsComponentPanel);
            
            
            java.awt.GridBagConstraints constraintsButtonPanel = new java.awt.GridBagConstraints();
            constraintsButtonPanel.gridx = 0; constraintsButtonPanel.gridy = 2;
            constraintsButtonPanel.anchor = java.awt.GridBagConstraints.EAST;
            constraintsButtonPanel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsButtonPanel.gridwidth = 1;
            contentPanel.add(getButtonPanel(), constraintsButtonPanel);
            
        }
        return contentPanel;
    }
    
protected javax.swing.JPanel getButtonPanel() {
        
        if(buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new java.awt.GridBagLayout());
            
            java.awt.GridBagConstraints constraintsYesButton = new java.awt.GridBagConstraints();
            constraintsYesButton.gridx = 0; constraintsYesButton.gridy = 0;
            constraintsYesButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsYesButton.insets = new java.awt.Insets(0, 5, 0, 5);
            constraintsYesButton.gridwidth = 1;
            buttonPanel.add(getOkButton(), constraintsYesButton);
            
            java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
            constraintsCancelButton.gridx = 3; constraintsCancelButton.gridy = 0;
            constraintsCancelButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCancelButton.insets = new java.awt.Insets(0, 5, 0, 5);
            constraintsCancelButton.gridwidth = 1;
            buttonPanel.add(getCancelButton(), constraintsCancelButton);
        }
        return buttonPanel;
    }

    protected javax.swing.JPanel getComponentPanel() {
        
        if(componentPanel == null) {
            componentPanel = new JPanel();
            componentPanel.setLayout(new java.awt.GridBagLayout());
            componentPanel.setMinimumSize(new Dimension(500, 490));
            componentPanel.setPreferredSize(new Dimension(500, 490));
            
            java.awt.GridBagConstraints constraintsPointSelectionPanel = new java.awt.GridBagConstraints();
            constraintsPointSelectionPanel.gridx = 0; constraintsPointSelectionPanel.gridy = 0;
            constraintsPointSelectionPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsPointSelectionPanel.weightx = 1.0;
            constraintsPointSelectionPanel.weighty = 1.0;
            constraintsPointSelectionPanel.gridwidth = 1;
            constraintsPointSelectionPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsPointSelectionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
            componentPanel.add(getPointSelectionPanel(), constraintsPointSelectionPanel);
            
        }
        return componentPanel;
    }
    
    /**
     * Return the PointSelectionPanel property value.
     * @return com.cannontech.esub.editor.element.PointSelectionPanel
     */
    public DeviceSelectionPanel getPointSelectionPanel() {
        if (deviceSelectionPanel == null) {
            try {
                deviceSelectionPanel = new DeviceSelectionPanel();      
                deviceSelectionPanel.setName("DeviceSelectionPanel");
                deviceSelectionPanel.setPreferredSize(new Dimension(150,400));
                deviceSelectionPanel.setMinimumSize(new Dimension(150,400));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return deviceSelectionPanel;
    }
    
    public JButton getOkButton() {
        if( okButton == null ){
            okButton = new JButton();
            okButton.setText("OK");
        }
        return okButton;
    }
    
    public JButton getCancelButton() {
        if( cancelButton == null ){
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
        }
        return cancelButton;
    }
}