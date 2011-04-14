package com.cannontech.dbeditor.editor.regenerate;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.DimensionUIResource;

import org.apache.commons.lang.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteUsageHelper;
import com.cannontech.database.data.route.RouteRole;
import com.cannontech.database.db.route.RepeaterRoute;

public class RoleConflictDialog extends javax.swing.JDialog implements java.awt.event.ActionListener {

    private JLabel descriptionLabel = null;
    private JLabel noDescriptionLabel = null;
    private JLabel duplicatesLabel = null;
    private JLabel repeater1VariableLabel = null;
    private JLabel repeater2VariableLabel = null;
    private JLabel repeater3VariableLabel = null;
    private JLabel repeater4VariableLabel = null;
    private JLabel repeater5VariableLabel = null;
    private JLabel repeater6VariableLabel = null;
    private JLabel repeater7VariableLabel = null;
    private JLabel ccuFixedLabel = null;
    private JLabel ccuVariableLabel = null;
    private JCheckBox ccu1CheckBox = null;
    private JCheckBox ccu2CheckBox = null;
    private JCheckBox ccu3CheckBox = null;
    private JCheckBox ccu4CheckBox = null;
    private JCheckBox ccu5CheckBox = null;
    private JCheckBox ccu6CheckBox = null;
    private JCheckBox ccu7CheckBox = null;
    
    private JTextField repeater1VariableTextField = null;
    private JTextField repeater2VariableTextField = null;
    private JTextField repeater3VariableTextField = null;
    private JTextField repeater4VariableTextField = null;
    private JTextField repeater5VariableTextField = null;
    private JTextField repeater6VariableTextField = null;
    private JTextField repeater7VariableTextField = null;
    private JTextField ccuFixedTextField = null;
    private JTextField ccuVariableTextField = null;
    
//    private JTextPane duplicateCCUTextArea = null;
    private JPanel duplicateCheckBoxPanel = null;
    
    private JLabel suggestLabel = null;
    private JPanel contentPanel = null;
    private JPanel componentPanel = null;
    private JPanel buttonPanel = null;
    private Frame owner = null;
    private JButton noButton = null;
    private JButton yesButton = null;
    private JButton cancelButton = null;
    private RouteRole role = null;
    private CCURoute route = null;
    private RouteUsageHelper routeMaster = null;
    private Vector blackList = new Vector();
    
    private String choice = "No";
    private JTextField[] repeaterTextFieldArray = {
            getRepeater1VariableTextField(),
            getRepeater2VariableTextField(),
            getRepeater3VariableTextField(),
            getRepeater4VariableTextField(),
            getRepeater5VariableTextField(),
            getRepeater6VariableTextField(),
            getRepeater7VariableTextField()
            };
    private JLabel[] repeaterLabelArray = {
            getRepeater1VariableLabel(),
            getRepeater2VariableLabel(),
            getRepeater3VariableLabel(),
            getRepeater4VariableLabel(),
            getRepeater5VariableLabel(),
            getRepeater6VariableLabel(),
            getRepeater7VariableLabel()
            };
    private JCheckBox[] ccuCheckBoxArray = {
            getCCU1CheckBox(),
            getCCU2CheckBox(),
            getCCU3CheckBox(),
            getCCU4CheckBox(),
            getCCU5CheckBox(),
            getCCU6CheckBox(),
            getCCU7CheckBox(),
            };
    
    /**
     * This method was created in VisualAge.
     * @param contentPanel JPanel
     */
    public RoleConflictDialog(java.awt.Frame owner, RouteRole role_, CCURoute route_, RouteUsageHelper regenerateRoute_) {
        super(owner, true);
        this.owner = owner;
        role = role_;
        route = route_;
        routeMaster = regenerateRoute_;
        initialize();
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == getYesButton()) {
            choice = "Yes";
            this.dispose();
        }else if (source == getNoButton()) {
            choice = "No";
            getBlackList();
            getNextSuggestion();
        }else if (source == getCancelButton()) {
            choice = "Cancel";
            this.dispose();
        }
    }
    
    public void setNewRole(RouteRole role_) {
        role = role_;
        initializeRoles();
    }
    
    private Vector getBlackList(){
        for(int i = 0; i < ccuCheckBoxArray.length; i++){
            if(ccuCheckBoxArray[i].isEnabled() && ccuCheckBoxArray[i].isSelected()){
                Enumeration enummer = role.getDuplicates().elements();
                while(enummer.hasMoreElements()){
                    LiteYukonPAObject pao = (LiteYukonPAObject)enummer.nextElement();
                    if(pao.getPaoName().equalsIgnoreCase(ccuCheckBoxArray[i].getText())){
                        if(!blackList.contains(pao)){
                            blackList.add(pao);
                        }
                    }
                }
            }
        }
        return blackList;
    }
    
    public RouteRole getRole() {
        return role;
    }
    
    private void getNextSuggestion(){
        role = routeMaster.assignRouteLocation(route, role, blackList);
        if(role.getFixedBit() == -1){
            role.setFixedBit(0);
            role.setVarbit(0);
            role = routeMaster.assignRouteLocation(route, role, blackList);
        }
        setNewRole(role);
    }
    
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error( exception.getMessage(), exception );;
    }
    
    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getNoButton().addActionListener(this);
        getYesButton().addActionListener(this);
        getCancelButton().addActionListener(this);
    }
    
    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("RoleConflictFrame");
            setTitle("Route Role Conflict");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setSize(450, 560);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        
            initializeRoles();
        try {
            setContentPane(getContentPanel());
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
    
    private void initializeRoles() {
        int fixed = role.getFixedBit() % 32;
        int var = role.getVarbit();
        int size = route.getRepeaterVector().size();
        getCCUFixedTextField().setText(new Integer(fixed).toString());
        getCCUVariableTextField().setText(new Integer(var).toString());
        
        for(int i = 0; i < repeaterTextFieldArray.length; i++) {
            JTextField field = repeaterTextFieldArray[i];
            JLabel label = repeaterLabelArray[i];
            if(i < size) {
                RepeaterRoute rr = (RepeaterRoute)route.getRepeaterVector().get(i);
                int rptVarBit = (var + i)+1;
                if(i + 1 == size) {
                    rptVarBit = 7;
                }
                field.setText(new Integer(rptVarBit).toString());
                String routeName = "";
                try {
                    routeName = DaoFactory.getPaoDao().getYukonPAOName(rr.getDeviceID().intValue());
                } catch (Exception e) {
                    routeName = route.getPAOName();
                }
                if(routeName == null){
                    routeName = route.getPAOName();
                }
                label.setText(routeName + " Var Bit:");
                field.setEnabled(true);
                label.setEnabled(true);
            }else {
                field.setEnabled(false);
                label.setEnabled(false);
            }
        }
        
        Vector dups = role.getDuplicates();
        for (int i = 0; i < ccuCheckBoxArray.length; i++){
            if(i < dups.size()){
                ccuCheckBoxArray[i].setText(((LiteYukonPAObject) dups.get(i)).getPaoName());
                ccuCheckBoxArray[i].setEnabled(true);
                ccuCheckBoxArray[i].setVisible(true);
                ccuCheckBoxArray[i].setSelected(false);
            }else{
                ccuCheckBoxArray[i].setEnabled(false);
                ccuCheckBoxArray[i].setVisible(false);
            }
        }
    }

    public JButton getNoButton() {
        if( noButton == null ){
            noButton = new JButton();
            noButton.setText("No");
        }
        return noButton;
    }

    public JButton getYesButton() {
        if( yesButton == null ){
            yesButton = new JButton();
            yesButton.setText("Yes");
        }
        return yesButton;
    }
    
    public JButton getCancelButton() {
        if( cancelButton == null ){
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
        }
        return cancelButton;
    }
    
    public JCheckBox getCCU1CheckBox() {
        if( ccu1CheckBox == null ){
            ccu1CheckBox = new JCheckBox();
            ccu1CheckBox.setText("CCU 1");
        }
        return ccu1CheckBox;
    }
    
    public JCheckBox getCCU2CheckBox() {
        if( ccu2CheckBox == null ){
            ccu2CheckBox = new JCheckBox();
            ccu2CheckBox.setText("CCU 1");
        }
        return ccu2CheckBox;
    }
    
    public JCheckBox getCCU3CheckBox() {
        if( ccu3CheckBox == null ){
            ccu3CheckBox = new JCheckBox();
            ccu3CheckBox.setText("CCU 1");
        }
        return ccu3CheckBox;
    }
    
    public JCheckBox getCCU4CheckBox() {
        if( ccu4CheckBox == null ){
            ccu4CheckBox = new JCheckBox();
            ccu4CheckBox.setText("CCU 1");
        }
        return ccu4CheckBox;
    }
    
    public JCheckBox getCCU5CheckBox() {
        if( ccu5CheckBox == null ){
            ccu5CheckBox = new JCheckBox();
            ccu5CheckBox.setText("CCU 1");
        }
        return ccu5CheckBox;
    }
    
    public JCheckBox getCCU6CheckBox() {
        if( ccu6CheckBox == null ){
            ccu6CheckBox = new JCheckBox();
            ccu6CheckBox.setText("CCU 1");
        }
        return ccu6CheckBox;
    }
    
    public JCheckBox getCCU7CheckBox() {
        if( ccu7CheckBox == null ){
            ccu7CheckBox = new JCheckBox();
            ccu7CheckBox.setText("CCU 1");
        }
        return ccu7CheckBox;
    }
    
    public JLabel getDescriptionLabel() {
        if( descriptionLabel == null ){
            descriptionLabel = new JLabel();
            descriptionLabel.setText("No un-used bit range available.  Some Fixed/Variable bits must be reused.");
            descriptionLabel.setFont(new java.awt.Font("Arial", 1, 12));
        }
        return descriptionLabel;
    }
    
    public JLabel getNoDescriptionLabel() {
        if( noDescriptionLabel == null ){
            noDescriptionLabel = new JLabel();
            noDescriptionLabel.setText("* Clicking the 'No' button will generate a new suggestion.");
        }
        return noDescriptionLabel;
    }
    
    public JLabel getSuggestLabel() {
        if( suggestLabel == null ){
            suggestLabel = new JLabel();
            suggestLabel.setText("Suggested Fixed/Variable bit roles:");
        }
        return suggestLabel;
    }
    
    public JLabel getCCUFixedLabel() {
        if( ccuFixedLabel == null ){
            ccuFixedLabel = new JLabel();
            ccuFixedLabel.setText("CCU Fixed Bit:");
        }
        return ccuFixedLabel;
    }
    
    public JLabel getCCUVariableLabel() {
        if( ccuVariableLabel == null ){
            ccuVariableLabel = new JLabel();
            ccuVariableLabel.setText("CCU Variable Bit:");
        }
        return ccuVariableLabel;
    }
    
    public JLabel getRepeater1VariableLabel() {
        if( repeater1VariableLabel == null ){
            repeater1VariableLabel = new JLabel();
            repeater1VariableLabel.setText("Repeater 1 Variable Bit:");
        }
        return repeater1VariableLabel;
    }
    
    public JLabel getRepeater2VariableLabel() {
        if( repeater2VariableLabel == null ){
            repeater2VariableLabel = new JLabel();
            repeater2VariableLabel.setText("Repeater 2 Variable Bit:");
        }
        return repeater2VariableLabel;
    }
    
    public JLabel getRepeater3VariableLabel() {
        if( repeater3VariableLabel == null ){
            repeater3VariableLabel = new JLabel();
            repeater3VariableLabel.setText("Repeater 3 Variable Bit:");
        }
        return repeater3VariableLabel;
    }
    
    public JLabel getRepeater4VariableLabel() {
        if( repeater4VariableLabel == null ){
            repeater4VariableLabel = new JLabel();
            repeater4VariableLabel.setText("Repeater 4 Variable Bit:");
        }
        return repeater4VariableLabel;
    }
    
    public JLabel getRepeater5VariableLabel() {
        if( repeater5VariableLabel == null ){
            repeater5VariableLabel = new JLabel();
            repeater5VariableLabel.setText("Repeater 5 Variable Bit:");
        }
        return repeater5VariableLabel;
    }
    
    public JLabel getRepeater6VariableLabel() {
        if( repeater6VariableLabel == null ){
            repeater6VariableLabel = new JLabel();
            repeater6VariableLabel.setText("Repeater 6 Variable Bit:");
        }
        return repeater6VariableLabel;
    }
    
    public JLabel getRepeater7VariableLabel() {
        if( repeater7VariableLabel == null ){
            repeater7VariableLabel = new JLabel();
            repeater7VariableLabel.setText("Repeater 7 Variable Bit:");
        }
        return repeater7VariableLabel;
    }
    
    public JLabel getDuplicatesLabel() {
        if( duplicatesLabel == null ){
            duplicatesLabel = new JLabel();
            duplicatesLabel.setText("Other CCU's using these bit roles:");
        }
        return duplicatesLabel;
    }
    
protected javax.swing.JPanel getComponentPanel() {
        
        if(componentPanel == null) {
            componentPanel = new JPanel();
            componentPanel.setLayout(new java.awt.GridBagLayout());
            componentPanel.setBorder(new TitleBorder("Suggested Fixed/Vairable bit roles:"));
            
            java.awt.GridBagConstraints constraintsCCUFixedLabel = new java.awt.GridBagConstraints();
            constraintsCCUFixedLabel.gridx = 0; constraintsCCUFixedLabel.gridy = 0;
            constraintsCCUFixedLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUFixedLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsCCUFixedLabel.gridwidth = 1;
            componentPanel.add(getCCUFixedLabel(), constraintsCCUFixedLabel);
            
            java.awt.GridBagConstraints constraintsCCUFixedTextField = new java.awt.GridBagConstraints();
            constraintsCCUFixedTextField.gridx = 1; constraintsCCUFixedTextField.gridy = 0;
            constraintsCCUFixedTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUFixedTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsCCUFixedTextField.gridwidth = 1;
            componentPanel.add(getCCUFixedTextField(), constraintsCCUFixedTextField);
            
            java.awt.GridBagConstraints constraintsCCUVariableLabel = new java.awt.GridBagConstraints();
            constraintsCCUVariableLabel.gridx = 2; constraintsCCUVariableLabel.gridy = 0;
            constraintsCCUVariableLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUVariableLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsCCUVariableLabel.gridwidth = 1;
            componentPanel.add(getCCUVariableLabel(), constraintsCCUVariableLabel);
            
            java.awt.GridBagConstraints constraintsCCUVariableTextField = new java.awt.GridBagConstraints();
            constraintsCCUVariableTextField.gridx = 3; constraintsCCUVariableTextField.gridy = 0;
            constraintsCCUVariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUVariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsCCUVariableTextField.gridwidth = 1;
            componentPanel.add(getCCUVariableTextField(), constraintsCCUVariableTextField);
            
            java.awt.GridBagConstraints constraintsRepeater1VariableLabel = new java.awt.GridBagConstraints();
            constraintsRepeater1VariableLabel.gridx = 0; constraintsRepeater1VariableLabel.gridy = 1;
            constraintsRepeater1VariableLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater1VariableLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater1VariableLabel.gridwidth = 1;
            componentPanel.add(getRepeater1VariableLabel(), constraintsRepeater1VariableLabel);
            
            java.awt.GridBagConstraints constraintsRepeater1VariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater1VariableTextField.gridx = 1; constraintsRepeater1VariableTextField.gridy = 1;
            constraintsRepeater1VariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater1VariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater1VariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater1VariableTextField(), constraintsRepeater1VariableTextField);
            
            
            java.awt.GridBagConstraints constraintsRepeater2VariableLabel = new java.awt.GridBagConstraints();
            constraintsRepeater2VariableLabel.gridx = 0; constraintsRepeater2VariableLabel.gridy = 2;
            constraintsRepeater2VariableLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater2VariableLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater2VariableLabel.gridwidth = 1;
            componentPanel.add(getRepeater2VariableLabel(), constraintsRepeater2VariableLabel);
            
            java.awt.GridBagConstraints constraintsRepeater2VariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater2VariableTextField.gridx = 1; constraintsRepeater2VariableTextField.gridy = 2;
            constraintsRepeater2VariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater2VariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater2VariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater2VariableTextField(), constraintsRepeater2VariableTextField);
            
            java.awt.GridBagConstraints constraintsRepeater3VariableLabel = new java.awt.GridBagConstraints();
            constraintsRepeater3VariableLabel.gridx = 0; constraintsRepeater3VariableLabel.gridy = 3;
            constraintsRepeater3VariableLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater3VariableLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater3VariableLabel.gridwidth = 1;
            componentPanel.add(getRepeater3VariableLabel(), constraintsRepeater3VariableLabel);
            
            java.awt.GridBagConstraints constraintsRepeater3VariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater3VariableTextField.gridx = 1; constraintsRepeater3VariableTextField.gridy = 3;
            constraintsRepeater3VariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater3VariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater3VariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater3VariableTextField(), constraintsRepeater3VariableTextField);
            
            java.awt.GridBagConstraints constraintsRepeater4VariableLabel = new java.awt.GridBagConstraints();
            constraintsRepeater4VariableLabel.gridx = 0; constraintsRepeater4VariableLabel.gridy = 4;
            constraintsRepeater4VariableLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater4VariableLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater4VariableLabel.gridwidth = 1;
            componentPanel.add(getRepeater4VariableLabel(), constraintsRepeater4VariableLabel);
            
            java.awt.GridBagConstraints constraintsRepeater4VariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater4VariableTextField.gridx = 1; constraintsRepeater4VariableTextField.gridy = 4;
            constraintsRepeater4VariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater4VariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater4VariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater4VariableTextField(), constraintsRepeater4VariableTextField);
            
            java.awt.GridBagConstraints constraintsRepeater5VariableLabel = new java.awt.GridBagConstraints();
            constraintsRepeater5VariableLabel.gridx = 0; constraintsRepeater5VariableLabel.gridy = 5;
            constraintsRepeater5VariableLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater5VariableLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater5VariableLabel.gridwidth = 1;
            componentPanel.add(getRepeater5VariableLabel(), constraintsRepeater5VariableLabel);
            
            java.awt.GridBagConstraints constraintsRepeater5VariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater5VariableTextField.gridx = 1; constraintsRepeater5VariableTextField.gridy = 5;
            constraintsRepeater5VariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater5VariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater5VariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater5VariableTextField(), constraintsRepeater5VariableTextField);
            
            java.awt.GridBagConstraints constraintsRepeater6VariableLabel = new java.awt.GridBagConstraints();
            constraintsRepeater6VariableLabel.gridx = 0; constraintsRepeater6VariableLabel.gridy = 6;
            constraintsRepeater6VariableLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater6VariableLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater6VariableLabel.gridwidth = 1;
            componentPanel.add(getRepeater6VariableLabel(), constraintsRepeater6VariableLabel);
            
            java.awt.GridBagConstraints constraintsRepeater6VariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater6VariableTextField.gridx = 1; constraintsRepeater6VariableTextField.gridy = 6;
            constraintsRepeater6VariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater6VariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater6VariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater6VariableTextField(), constraintsRepeater6VariableTextField);
            
            java.awt.GridBagConstraints constraintsRepeater7VariableLabel = new java.awt.GridBagConstraints();
            constraintsRepeater7VariableLabel.gridx = 0; constraintsRepeater7VariableLabel.gridy = 7;
            constraintsRepeater7VariableLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater7VariableLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater7VariableLabel.gridwidth = 1;
            componentPanel.add(getRepeater7VariableLabel(), constraintsRepeater7VariableLabel);
            
            java.awt.GridBagConstraints constraintsRepeater7VariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater7VariableTextField.gridx = 1; constraintsRepeater7VariableTextField.gridy = 7;
            constraintsRepeater7VariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater7VariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater7VariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater7VariableTextField(), constraintsRepeater7VariableTextField);
            
//            java.awt.GridBagConstraints constraintsDuplicatesLabel = new java.awt.GridBagConstraints();
//            constraintsDuplicatesLabel.gridx = 0; constraintsDuplicatesLabel.gridy = 8;
//            constraintsDuplicatesLabel.anchor = java.awt.GridBagConstraints.WEST;
//            constraintsDuplicatesLabel.insets = new java.awt.Insets(5, 5, 5, 5);
//            constraintsDuplicatesLabel.gridwidth = 4;
//            componentPanel.add(getDuplicatesLabel(), constraintsDuplicatesLabel);
            
            java.awt.GridBagConstraints constraintsDuplicateCheckBoxPanel = new java.awt.GridBagConstraints();
            constraintsDuplicateCheckBoxPanel.gridx = 0; constraintsDuplicateCheckBoxPanel.gridy = 8;
            constraintsDuplicateCheckBoxPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDuplicateCheckBoxPanel.insets = new java.awt.Insets(0, 0, 0, 0);
            constraintsDuplicateCheckBoxPanel.gridwidth = 4;
            componentPanel.add(getDuplicateCheckBoxPanel(), constraintsDuplicateCheckBoxPanel);
            
        }
        return componentPanel;
    }
    
    private JPanel getDuplicateCheckBoxPanel(){
        if(duplicateCheckBoxPanel == null){
            duplicateCheckBoxPanel = new JPanel();
            duplicateCheckBoxPanel.setLayout(new java.awt.GridBagLayout());
            duplicateCheckBoxPanel.setBorder(new TitleBorder("Other CCU's using these bit roles: Check ccu's you want to exclude."));
            duplicateCheckBoxPanel.setMinimumSize(new Dimension(400, 140));
            duplicateCheckBoxPanel.setPreferredSize(new Dimension(400, 140));
            
            java.awt.GridBagConstraints constraintsCheckBox1 = new java.awt.GridBagConstraints();
            constraintsCheckBox1.gridx = 0; constraintsCheckBox1.gridy = 0;
            constraintsCheckBox1.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox1.insets = new java.awt.Insets(5, 5, 0, 0);
            constraintsCheckBox1.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU1CheckBox(), constraintsCheckBox1);
            
            java.awt.GridBagConstraints constraintsCheckBox2 = new java.awt.GridBagConstraints();
            constraintsCheckBox2.gridx = 0; constraintsCheckBox2.gridy = 1;
            constraintsCheckBox2.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox2.insets = new java.awt.Insets(5, 5, 0, 0);
            constraintsCheckBox2.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU2CheckBox(), constraintsCheckBox2);
            
            java.awt.GridBagConstraints constraintsCheckBox3 = new java.awt.GridBagConstraints();
            constraintsCheckBox3.gridx = 0; constraintsCheckBox3.gridy = 2;
            constraintsCheckBox3.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox3.insets = new java.awt.Insets(5, 5, 0, 0);
            constraintsCheckBox3.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU3CheckBox(), constraintsCheckBox3);
            
            java.awt.GridBagConstraints constraintsCheckBox4 = new java.awt.GridBagConstraints();
            constraintsCheckBox4.gridx = 0; constraintsCheckBox4.gridy = 3;
            constraintsCheckBox4.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox4.insets = new java.awt.Insets(5, 5, 0, 0);
            constraintsCheckBox4.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU4CheckBox(), constraintsCheckBox4);
            
            java.awt.GridBagConstraints constraintsCheckBox5 = new java.awt.GridBagConstraints();
            constraintsCheckBox5.gridx = 1; constraintsCheckBox5.gridy = 0;
            constraintsCheckBox5.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox5.insets = new java.awt.Insets(5, 5, 0, 0);
            constraintsCheckBox5.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU5CheckBox(), constraintsCheckBox5);
            
            java.awt.GridBagConstraints constraintsCheckBox6 = new java.awt.GridBagConstraints();
            constraintsCheckBox6.gridx = 1; constraintsCheckBox6.gridy = 1;
            constraintsCheckBox6.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox6.insets = new java.awt.Insets(5, 5, 0, 0);
            constraintsCheckBox6.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU6CheckBox(), constraintsCheckBox6);
            
            java.awt.GridBagConstraints constraintsCheckBox7 = new java.awt.GridBagConstraints();
            constraintsCheckBox7.gridx = 1; constraintsCheckBox7.gridy = 2;
            constraintsCheckBox7.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox7.insets = new java.awt.Insets(5, 5, 0, 0);
            constraintsCheckBox7.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU7CheckBox(), constraintsCheckBox7);
            
        }
        return duplicateCheckBoxPanel;
    }
    
    protected JPanel getButtonPanel() {
        
        if(buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new java.awt.GridBagLayout());
            
            java.awt.GridBagConstraints constraintsYesButton = new java.awt.GridBagConstraints();
            constraintsYesButton.gridx = 0; constraintsYesButton.gridy = 0;
            constraintsYesButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsYesButton.insets = new java.awt.Insets(0, 5, 0, 5);
            constraintsYesButton.gridwidth = 1;
            buttonPanel.add(getYesButton(), constraintsYesButton);
            
            java.awt.GridBagConstraints constraintsNoButton = new java.awt.GridBagConstraints();
            constraintsNoButton.gridx = 1; constraintsYesButton.gridy = 0;
            constraintsNoButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsNoButton.insets = new java.awt.Insets(0, 5, 0, 5);
            constraintsNoButton.gridwidth = 1;
            buttonPanel.add(getNoButton(), constraintsNoButton);
            
            java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
            constraintsCancelButton.gridx = 2; constraintsCancelButton.gridy = 0;
            constraintsCancelButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCancelButton.insets = new java.awt.Insets(0, 5, 0, 5);
            constraintsCancelButton.gridwidth = 1;
            buttonPanel.add(getCancelButton(), constraintsCancelButton);
        }
        return buttonPanel;
    }
    
     /**
     * Sets up and returns the panel used for the content of this dialog.
     * @return javax.swing.JPanel
     */
    protected javax.swing.JPanel getContentPanel() {
        
        if(contentPanel == null) {
            contentPanel = new JPanel();
            contentPanel.setLayout(new java.awt.GridBagLayout());
            contentPanel.setSize( 400,500);
            
            java.awt.GridBagConstraints constraintsDescriptionLabel = new java.awt.GridBagConstraints();
            constraintsDescriptionLabel.gridx = 0; constraintsDescriptionLabel.gridy = 0;
            constraintsDescriptionLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDescriptionLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsDescriptionLabel.gridwidth = 4;
            contentPanel.add(getDescriptionLabel(), constraintsDescriptionLabel);
            
            java.awt.GridBagConstraints constraintsComponentPanel = new java.awt.GridBagConstraints();
            constraintsComponentPanel.gridx = 0; constraintsComponentPanel.gridy = 1;
            constraintsComponentPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsComponentPanel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsComponentPanel.gridwidth = 4;
            contentPanel.add(getComponentPanel(), constraintsComponentPanel);
            
            java.awt.GridBagConstraints constraintsNoDescriptionLabel = new java.awt.GridBagConstraints();
            constraintsNoDescriptionLabel.gridx = 0; constraintsNoDescriptionLabel.gridy = 2;
            constraintsNoDescriptionLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsNoDescriptionLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsNoDescriptionLabel.gridwidth = 4;
            contentPanel.add(getNoDescriptionLabel(), constraintsNoDescriptionLabel);
            
            java.awt.GridBagConstraints constraintsButtonPanel = new java.awt.GridBagConstraints();
            constraintsButtonPanel.gridx = 0; constraintsButtonPanel.gridy = 3;
            constraintsButtonPanel.anchor = java.awt.GridBagConstraints.EAST;
            constraintsButtonPanel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsButtonPanel.gridwidth = 4;
            contentPanel.add(getButtonPanel(), constraintsButtonPanel);
            
        }
        return contentPanel;
    }

    public JTextField getCCUFixedTextField() {
        if (ccuFixedTextField == null) {
            ccuFixedTextField = new JTextField("boo");
            ccuFixedTextField.setMinimumSize(new Dimension(30, 20));
            ccuFixedTextField.setPreferredSize(new Dimension(30, 20));
            ccuFixedTextField.setEditable(false);
        }
        return ccuFixedTextField;
    }
    
    public JTextField getCCUVariableTextField() {
        if (ccuVariableTextField == null) {
            ccuVariableTextField = new JTextField();
            ccuVariableTextField.setMinimumSize(new Dimension(30, 20));
            ccuVariableTextField.setPreferredSize(new Dimension(30, 20));
            ccuVariableTextField.setEditable(false);
        }
        return ccuVariableTextField;
    }
    
    public JTextField getRepeater1VariableTextField() {
        if (repeater1VariableTextField == null) {
            repeater1VariableTextField = new JTextField();
            repeater1VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater1VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater1VariableTextField.setEditable(false);
        }
        return repeater1VariableTextField;
    }

    public JTextField getRepeater2VariableTextField() {
        if (repeater2VariableTextField == null) {
            repeater2VariableTextField = new JTextField();
            repeater2VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater2VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater2VariableTextField.setEditable(false);
        }
        return repeater2VariableTextField;
    }

    public JTextField getRepeater3VariableTextField() {
        if (repeater3VariableTextField == null) {
            repeater3VariableTextField = new JTextField();
            repeater3VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater3VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater3VariableTextField.setEditable(false);
        }
        return repeater3VariableTextField;
    }

    public JTextField getRepeater4VariableTextField() {
        if (repeater4VariableTextField == null) {
            repeater4VariableTextField = new JTextField();
            repeater4VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater4VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater4VariableTextField.setEditable(false);
        }
        return repeater4VariableTextField;
    }
    
    public JTextField getRepeater5VariableTextField() {
        if (repeater5VariableTextField == null) {
            repeater5VariableTextField = new JTextField();
            repeater5VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater5VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater5VariableTextField.setEditable(false);
        }
        return repeater5VariableTextField;
    }
    
    public JTextField getRepeater6VariableTextField() {
    if (repeater6VariableTextField == null) {
            repeater6VariableTextField = new JTextField();
            repeater6VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater6VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater6VariableTextField.setEditable(false);
        }
        return repeater6VariableTextField;
    }
    
    public JTextField getRepeater7VariableTextField() {
        if( repeater7VariableTextField == null ){
            repeater7VariableTextField = new JTextField();
            repeater7VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater7VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater7VariableTextField.setEditable(false);
        }
        return repeater7VariableTextField;
    }
    
//    public JTextPane getDuplicateCCUTextArea() {
//        if( duplicateCCUTextArea == null) {
//            duplicateCCUTextArea = new JTextPane();
//            duplicateCCUTextArea.setMinimumSize(new Dimension(400, 50));
//            duplicateCCUTextArea.setPreferredSize(new Dimension(400, 50));
//            duplicateCCUTextArea.setBackground(Color.pink);
//            duplicateCCUTextArea.setEditable(false);
//            duplicateCCUTextArea.setText("");
//            duplicateCCUTextArea.setFont(new Font("Arial", 0, 11));
//            Vector dups = role.getDuplicates();
//
//            String largeString = StringUtils.join(dups.iterator(), ", ");
//            getDuplicateCCUTextArea().setText(largeString);
//            duplicateCCUTextArea.setBorder(new EtchedBorder());
//        }
//        return duplicateCCUTextArea;
//    }
    
    /**
     * Returns the choice selected
     * @return String
     */
    public String getValue() {

        if( isShowing() == false )
        {
            setModal(true);
            show();
        }

        return choice;
    }
    
    public static void main(java.lang.String[] args) {
        try {
            RoleConflictDialog aRoleConflictDialog;
            aRoleConflictDialog = new RoleConflictDialog(null,null,null, null);
            aRoleConflictDialog.setModal(true);
            aRoleConflictDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            aRoleConflictDialog.show();
            aRoleConflictDialog.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            CTILogger.error( exception.getMessage(), exception );
        }
    }
}
