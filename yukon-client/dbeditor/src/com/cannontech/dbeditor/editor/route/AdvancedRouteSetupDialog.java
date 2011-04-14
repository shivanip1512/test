package com.cannontech.dbeditor.editor.route;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteUsageHelper;
import com.cannontech.database.data.route.RouteRole;
import com.cannontech.database.db.route.RepeaterRoute;

@SuppressWarnings("serial")
public class AdvancedRouteSetupDialog extends javax.swing.JDialog implements ActionListener,  FocusListener{

    private JLabel duplicatesLabel = null;
    private JLabel suggestedDuplicatesLabel = null;
    private JLabel repeater1VariableLabel = null;
    private JLabel repeater2VariableLabel = null;
    private JLabel repeater3VariableLabel = null;
    private JLabel repeater4VariableLabel = null;
    private JLabel repeater5VariableLabel = null;
    private JLabel repeater6VariableLabel = null;
    private JLabel repeater7VariableLabel = null;
    private JLabel ccuFixedLabel = null;
    private JLabel ccuVariableLabel = null;
    private JCheckBox lockCheckBox = null;
    
    private JCheckBox ccu1CheckBox = null;
    private JCheckBox ccu2CheckBox = null;
    private JCheckBox ccu3CheckBox = null;
    private JCheckBox ccu4CheckBox = null;
    private JCheckBox ccu5CheckBox = null;
    private JCheckBox ccu6CheckBox = null;
    private JCheckBox ccu7CheckBox = null;
    
    private JCheckBox ccu1SuggestedCheckBox = null;
    private JCheckBox ccu2SuggestedCheckBox = null;
    private JCheckBox ccu3SuggestedCheckBox = null;
    private JCheckBox ccu4SuggestedCheckBox = null;
    private JCheckBox ccu5SuggestedCheckBox = null;
    private JCheckBox ccu6SuggestedCheckBox = null;
    private JCheckBox ccu7SuggestedCheckBox = null;
    
    private JTextField repeater1VariableTextField = null;
    private JTextField repeater2VariableTextField = null;
    private JTextField repeater3VariableTextField = null;
    private JTextField repeater4VariableTextField = null;
    private JTextField repeater5VariableTextField = null;
    private JTextField repeater6VariableTextField = null;
    private JTextField repeater7VariableTextField = null;
    private JTextField repeater1SuggestedVariableTextField = null;
    private JTextField repeater2SuggestedVariableTextField = null;
    private JTextField repeater3SuggestedVariableTextField = null;
    private JTextField repeater4SuggestedVariableTextField = null;
    private JTextField repeater5SuggestedVariableTextField = null;
    private JTextField repeater6SuggestedVariableTextField = null;
    private JTextField repeater7SuggestedVariableTextField = null;
    private JTextField ccuFixedTextField = null;
    private JTextField ccuVariableTextField = null;
    private JTextField ccuFixedSuggestedTextField = null;
    private JTextField ccuVariableSuggestedTextField = null;
    
    private JPanel duplicateCheckBoxPanel = null;
    private JPanel duplicateSuggestedCheckBoxPanel = null;
    
    private JPanel contentPanel = null;
    private JPanel componentPanel = null;
    private JPanel buttonPanel = null;
    private Frame owner = null;
    private JButton suggestionButton = null;
    private JButton useSuggestionButton = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private CCURoute route = null;
    private RouteRole role = null;
    private int unMaskedFixedBit = -1;
    private int varBit = -1;
    private RouteUsageHelper routeMaster = null;
    private Vector blackList = new Vector();
    
    private String choice = "No";
    private String myCCUName = "";
    private String routeName = "";
    public JTextField[] repeaterTextFieldArray = {
            getRepeater1VariableTextField(),
            getRepeater2VariableTextField(),
            getRepeater3VariableTextField(),
            getRepeater4VariableTextField(),
            getRepeater5VariableTextField(),
            getRepeater6VariableTextField(),
            getRepeater7VariableTextField()
            };
    public JLabel[] repeaterLabelArray = {
            getRepeater1VariableLabel(),
            getRepeater2VariableLabel(),
            getRepeater3VariableLabel(),
            getRepeater4VariableLabel(),
            getRepeater5VariableLabel(),
            getRepeater6VariableLabel(),
            getRepeater7VariableLabel()
            };
    private JTextField[] repeaterSuggestedTextFieldArray = {
            getRepeater1SuggestedVariableTextField(),
            getRepeater2SuggestedVariableTextField(),
            getRepeater3SuggestedVariableTextField(),
            getRepeater4SuggestedVariableTextField(),
            getRepeater5SuggestedVariableTextField(),
            getRepeater6SuggestedVariableTextField(),
            getRepeater7SuggestedVariableTextField()
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
    
    private JCheckBox[] ccuSuggestedCheckBoxArray = {
            getCCU1SuggestedCheckBox(),
            getCCU2SuggestedCheckBox(),
            getCCU3SuggestedCheckBox(),
            getCCU4SuggestedCheckBox(),
            getCCU5SuggestedCheckBox(),
            getCCU6SuggestedCheckBox(),
            getCCU7SuggestedCheckBox(),
            };
    
    /**
     * This method was created in VisualAge.
     * @param contentPanel JPanel
     */
    public AdvancedRouteSetupDialog(java.awt.Frame owner,  CCURoute route_) {      //RouteRole role_, 
        super(owner, true);
        this.owner = owner;
        routeMaster = new RouteUsageHelper();
        route = route_;
        myCCUName = DaoFactory.getPaoDao().getYukonPAOName(route.getDeviceID());
        routeName = route.getPAOName();
        initialize();
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == getOkButton()) {
            if(isInputValid()) {
                choice = "OK";
                setRouteSettings();
                this.dispose();
            }else {
                JOptionPane.showMessageDialog(this, "Invalid input for fixed or variable bits.", "INVALID BIT VALUES", JOptionPane.ERROR_MESSAGE); 
            }
        }else if (source == getSuggestionButton()) {
            getSuggestion();
            getSuggestedDuplicatesLabel().setVisible(true);
            getDuplicateSuggestedCheckBoxPanel().setVisible(true);
            getUseSuggestionButton().setEnabled(true);
        }else if (source == getUseSuggestionButton()) {
            useSuggestion();
            getSuggestedDuplicatesLabel().setVisible(false);
            getDuplicateSuggestedCheckBoxPanel().setVisible(false);
            getUseSuggestionButton().setEnabled(false);
        }else if (source == getCancelButton()) {
            choice = "Cancel";
            this.dispose();
        }else if (source == getCCUFixedTextField() ||
                source == getCCUVariableTextField() ||
                source == getRepeater1VariableTextField() || 
                source == getRepeater2VariableTextField() ||
                source == getRepeater3VariableTextField() ||
                source == getRepeater4VariableTextField() ||
                source == getRepeater5VariableTextField() ||
                source == getRepeater6VariableTextField() ||
                source == getRepeater7VariableTextField()) {
            findDuplicates();
        }
        this.validate();
    }
    
    @SuppressWarnings("unchecked")
	private boolean setRouteSettings() {
        //find masked fixed bit value
        int maskedFixed = new Integer(getCCUFixedTextField().getText()).intValue();
        Vector variables = new Vector();
        variables.add(new Integer(getCCUVariableTextField().getText()));
        for (int i = 0; i < route.getRepeaterVector().size(); i++) {
            Integer variable = new Integer(repeaterTextFieldArray[i].getText());
            if (variable.intValue() != 7) {
                variables.add(variable);
            }
        }
        Vector changingRoutes = new Vector(1);
        changingRoutes.add(route);
        routeMaster.removeChanginRoutes(changingRoutes);
        int unMaskedFixed = routeMaster.findFixedBit(maskedFixed, variables);
        varBit = new Integer(getCCUVariableTextField().getText()).intValue();
        if(unMaskedFixed > -1) {
            unMaskedFixedBit = unMaskedFixed;
            return true;
        }else {
            // do some error, we used this fixed bit too many times
            return false;
        }
        
    }
    
    public int getUnMaskedFixedBit() {
        return unMaskedFixedBit;
    }
    
    public int getVariableBit() {
        return varBit;
    }
    
    private boolean isInputValid() {
        if(getCCUFixedTextField().getText().equalsIgnoreCase("")) {
            return false;
        }else if (getCCUVariableTextField().getText().equalsIgnoreCase("")) {
            return false;
        }else {
            for (int i = 0; i < route.getRepeaterVector().size(); i++) {
                if(repeaterTextFieldArray[i].getText().equalsIgnoreCase("")){
                    return false;
                }
            }
        }
        return true;
    }
    
    private void getSuggestion() {
        
        routeMaster = new RouteUsageHelper();
        role = routeMaster.assignRouteLocation(route, role, getBlackList());
        if(role.getFixedBit() == -1){
            role.setFixedBit(0);
            role.setVarbit(0);
            role = routeMaster.assignRouteLocation(route, role, blackList);
        }
        int roleFixedBitMasked = role.getFixedBit() % 32;
                
        getCCUFixedSuggestedTextField().setText(new Integer(roleFixedBitMasked).toString());
        getCCUFixedSuggestedTextField().setVisible(true);
        getCCUVariableSuggestedTextField().setText(new Integer(role.getVarbit() ).toString());
        getCCUVariableSuggestedTextField().setVisible(true);
        int size = route.getRepeaterVector().size();
        
        for(int i = 0; i < size; i++) {
            repeaterSuggestedTextFieldArray[i].setVisible(true);
            int rptVarBit = (role.getVarbit() + i)+1;
            if(i + 1 == size) {
                rptVarBit = 7;
            }
            repeaterSuggestedTextFieldArray[i].setText(new Integer(rptVarBit).toString());
        }
        findSuggestedDuplicates();
        this.validate();
        this.repaint();
    }
    
    private void findSuggestedDuplicates() {
        int fixed = new Integer(getCCUFixedSuggestedTextField().getText()).intValue();
        int variable = new Integer(getCCUVariableSuggestedTextField().getText()).intValue();
        int[] rptVariables = new int[route.getRepeaterVector().size()-1];
        for(int i = 0; i < rptVariables.length; i++) {
            rptVariables[i] = new Integer(repeaterSuggestedTextFieldArray[i].getText()).intValue();
        }
        Vector dups = routeMaster.findConflicts(route.getRouteID().intValue(), fixed, variable, rptVariables);
        for (int i = 0; i < ccuSuggestedCheckBoxArray.length; i++){
            if(i < dups.size()){
                ccuSuggestedCheckBoxArray[i].setText(((LiteYukonPAObject) dups.get(i)).getPaoName());
                ccuSuggestedCheckBoxArray[i].setEnabled(true);
                ccuSuggestedCheckBoxArray[i].setVisible(true);
                ccuSuggestedCheckBoxArray[i].setSelected(false);
            }else{
                ccuSuggestedCheckBoxArray[i].setEnabled(false);
                ccuSuggestedCheckBoxArray[i].setVisible(false);
            }
        }
    }
    
    private void findDuplicates() {
        int fixed = new Integer(getCCUFixedTextField().getText()).intValue();
        int variable = new Integer(getCCUVariableTextField().getText()).intValue();
        int[] rptVariables = new int[route.getRepeaterVector().size()-1];
        for(int i = 0; i < rptVariables.length; i++) {
            rptVariables[i] = new Integer(repeaterTextFieldArray[i].getText()).intValue();
        }
        Vector dups = routeMaster.findConflicts(route.getRouteID().intValue(), fixed, variable, rptVariables);
        for (int i = 0; i < ccuCheckBoxArray.length; i++){
            if(i < dups.size()){
                ccuCheckBoxArray[i].setText(((LiteYukonPAObject) dups.get(i)).getPaoName());
                ccuCheckBoxArray[i].setEnabled(true);
                ccuCheckBoxArray[i].setVisible(true);
            }else{
                ccuCheckBoxArray[i].setEnabled(false);
                ccuCheckBoxArray[i].setVisible(false);
            }
        }
    }
    
    private void useSuggestion() {
        int roleFixedBitUnMasked = role.getFixedBit() % 32;
        getCCUFixedTextField().setText(new Integer(roleFixedBitUnMasked).toString());
        getCCUVariableTextField().setText(new Integer(role.getVarbit()).toString());
        getCCUFixedSuggestedTextField().setVisible(false);
        getCCUVariableSuggestedTextField().setVisible(false);
        int size = route.getRepeaterVector().size();
        
        for(int i = 0; i < size; i++) {
            repeaterSuggestedTextFieldArray[i].setVisible(false);
            int rptVarBit = (role.getVarbit() + i)+1;
            if(i + 1 == size) {
                rptVarBit = 7;
            }
            repeaterTextFieldArray[i].setText(new Integer(rptVarBit).toString());
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
    
    @SuppressWarnings("unchecked")
	private Vector getBlackList(){
        
        
        int fixed_ = route.getCarrierRoute().getCcuFixBits().intValue() % 32;
        int var_ = route.getCarrierRoute().getCcuVariableBits().intValue();
        int[] rptVariables_ = new int[route.getRepeaterVector().size()-1];
        for(int i = 0; i < rptVariables_.length; i++) {
            rptVariables_[i] = new Integer(repeaterTextFieldArray[i].getText()).intValue();
        }
        Vector dups_ = routeMaster.findConflicts(route.getRouteID().intValue(), fixed_, var_, rptVariables_);
        
        for(int i = 0; i < ccuCheckBoxArray.length; i++){
            if(ccuCheckBoxArray[i].isEnabled() && ccuCheckBoxArray[i].isSelected()){
                Enumeration enummer = dups_.elements();
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
        
        if(getDuplicateSuggestedCheckBoxPanel().isVisible()){
            int fixed = new Integer(getCCUFixedSuggestedTextField().getText()).intValue();
            int variable = new Integer(getCCUVariableSuggestedTextField().getText()).intValue();
            int[] rptVariables = new int[route.getRepeaterVector().size()-1];
            for(int i = 0; i < rptVariables.length; i++) {
                rptVariables[i] = new Integer(repeaterSuggestedTextFieldArray[i].getText()).intValue();
            }
            Vector dups = routeMaster.findConflicts(route.getRouteID().intValue(), fixed, variable, rptVariables);
            
            for(int i = 0; i < ccuSuggestedCheckBoxArray.length; i++){
                if(ccuSuggestedCheckBoxArray[i].isEnabled() && ccuSuggestedCheckBoxArray[i].isSelected()){
                    Enumeration enummer = dups.elements();
                    while(enummer.hasMoreElements()){
                        LiteYukonPAObject pao = (LiteYukonPAObject)enummer.nextElement();
                        if(pao.getPaoName().equalsIgnoreCase(ccuSuggestedCheckBoxArray[i].getText())){
                            if(!blackList.contains(pao)){
                                blackList.add(pao);
                            }
                        }
                    }
                }
            }
        }
        //always add my own ccu to my blackList
        LiteYukonPAObject myCCU = DaoFactory.getPaoDao().getLiteYukonPAO(route.getDeviceID());
        if(!blackList.contains(myCCU)) {
            blackList.add(myCCU);
        }
        return blackList;
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
     * Initializes action listeners.
     */
    private void initConnections() throws java.lang.Exception {
        getSuggestionButton().addActionListener(this);
        getOkButton().addActionListener(this);
        getUseSuggestionButton().addActionListener(this);
        getCancelButton().addActionListener(this);
        getCCUFixedTextField().addActionListener(this);
        getCCUFixedTextField().addFocusListener(this);
        getCCUVariableTextField().addActionListener(this);
        getCCUVariableTextField().addFocusListener(this);
        for( int i = 0; i < repeaterTextFieldArray.length; i ++ ) {
            repeaterTextFieldArray[i].addActionListener(this);
            repeaterTextFieldArray[i].addFocusListener(this);
        }
    }
    
    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            
            setName("AdvanceRouteSetupDialog");
            setTitle("Advanced Route Setup: " + routeName);
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setSize(520, 600);
            initializeBits();
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
    
    private void initializeBits() {
        
        getCCUFixedSuggestedTextField().setVisible(false);
        getCCUVariableSuggestedTextField().setVisible(false);
        for(int i = 0; i < repeaterTextFieldArray.length; i++) {
            repeaterSuggestedTextFieldArray[i].setVisible(false);
        }
        
        int fixed = route.getCarrierRoute().getCcuFixBits().intValue() % 32;
        int var = route.getCarrierRoute().getCcuVariableBits().intValue();
        Vector rptVector = route.getRepeaterVector();
        getCCUFixedTextField().setText(new Integer(fixed).toString());
        getCCUVariableTextField().setText(new Integer(var).toString());
        
        int[] rptVariables = new int[route.getRepeaterVector().size()-1];
        for(int i = 0; i < route.getRepeaterVector().size(); i++) {
            RepeaterRoute rr = (RepeaterRoute)rptVector.get(i);
            int deviceID = rr.getDeviceID().intValue();
            String rptName = DaoFactory.getPaoDao().getYukonPAOName(deviceID);
            repeaterLabelArray[i].setText(rptName);
            if(rr.getVariableBits().intValue() != 7) {
                rptVariables[i] = rr.getVariableBits().intValue();
                repeaterTextFieldArray[i].setEditable(true);
                repeaterTextFieldArray[i].setDocument(new LongRangeDocument(0L,6L));
            }
            repeaterTextFieldArray[i].setText(rr.getVariableBits().toString());
            repeaterTextFieldArray[i].setEnabled(true);
        }
        
        Vector dups = routeMaster.findConflicts(route.getRouteID().intValue(), route.getCarrierRoute().getCcuFixBits().intValue() , route.getCarrierRoute().getCcuVariableBits().intValue(), rptVariables);

        for (int i = 0; i < ccuCheckBoxArray.length; i++){
            if(i < dups.size()){
                ccuCheckBoxArray[i].setText(((LiteYukonPAObject) dups.get(i)).getPaoName());
                ccuCheckBoxArray[i].setEnabled(true);
                ccuCheckBoxArray[i].setVisible(true);
            }else{
                ccuCheckBoxArray[i].setEnabled(false);
                ccuCheckBoxArray[i].setVisible(false);
            }
        }
        
        for (int i = 0; i < ccuSuggestedCheckBoxArray.length; i++){
            ccuSuggestedCheckBoxArray[i].setEnabled(false);
            ccuSuggestedCheckBoxArray[i].setVisible(false);
        }
        
        getDuplicateSuggestedCheckBoxPanel().setVisible(false);
        
        if(route.getCarrierRoute().getUserLocked().equalsIgnoreCase("Y")) {
            getLockCheckBox().setSelected(true);
        }else {
            getLockCheckBox().setSelected(false);
        }
    }

    public JButton getSuggestionButton() {
        if( suggestionButton == null ){
            suggestionButton = new JButton();
            suggestionButton.setText("Get Suggestion");
        }
        return suggestionButton;
    }
    
    public JButton getUseSuggestionButton() {
        if( useSuggestionButton == null ){
            useSuggestionButton = new JButton();
            useSuggestionButton.setText("Use Suggestion");
            useSuggestionButton.setEnabled(false);
        }
        return useSuggestionButton;
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
            duplicatesLabel.setText("Check the CCUs to exclude from future suggestions.");
        }
        return duplicatesLabel;
    }
    
    public JLabel getSuggestedDuplicatesLabel() {
        if( suggestedDuplicatesLabel == null ){
            suggestedDuplicatesLabel = new JLabel();
            suggestedDuplicatesLabel.setText("CCU's using suggested bit roles:");
            suggestedDuplicatesLabel.setVisible(false);
        }
        return suggestedDuplicatesLabel;
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
    
    public JCheckBox getCCU1SuggestedCheckBox() {
        if( ccu1SuggestedCheckBox == null ){
            ccu1SuggestedCheckBox = new JCheckBox();
            ccu1SuggestedCheckBox.setText("CCU 1");
        }
        return ccu1SuggestedCheckBox;
    }
    
    public JCheckBox getCCU2SuggestedCheckBox() {
        if( ccu2SuggestedCheckBox == null ){
            ccu2SuggestedCheckBox = new JCheckBox();
            ccu2SuggestedCheckBox.setText("CCU 1");
        }
        return ccu2SuggestedCheckBox;
    }
    
    public JCheckBox getCCU3SuggestedCheckBox() {
        if( ccu3SuggestedCheckBox == null ){
            ccu3SuggestedCheckBox = new JCheckBox();
            ccu3SuggestedCheckBox.setText("CCU 1");
        }
        return ccu3SuggestedCheckBox;
    }
    
    public JCheckBox getCCU4SuggestedCheckBox() {
        if( ccu4SuggestedCheckBox == null ){
            ccu4SuggestedCheckBox = new JCheckBox();
            ccu4SuggestedCheckBox.setText("CCU 1");
        }
        return ccu4SuggestedCheckBox;
    }
    
    public JCheckBox getCCU5SuggestedCheckBox() {
        if( ccu5SuggestedCheckBox == null ){
            ccu5SuggestedCheckBox = new JCheckBox();
            ccu5SuggestedCheckBox.setText("CCU 1");
        }
        return ccu5SuggestedCheckBox;
    }
    
    public JCheckBox getCCU6SuggestedCheckBox() {
        if( ccu6SuggestedCheckBox == null ){
            ccu6SuggestedCheckBox = new JCheckBox();
            ccu6SuggestedCheckBox.setText("CCU 1");
        }
        return ccu6SuggestedCheckBox;
    }
    
    public JCheckBox getCCU7SuggestedCheckBox() {
        if( ccu7SuggestedCheckBox == null ){
            ccu7SuggestedCheckBox = new JCheckBox();
            ccu7SuggestedCheckBox.setText("CCU 1");
        }
        return ccu7SuggestedCheckBox;
    }
    
protected javax.swing.JPanel getComponentPanel() {
        
        if(componentPanel == null) {
            componentPanel = new JPanel();
            componentPanel.setLayout(new java.awt.GridBagLayout());
            componentPanel.setBorder(new TitleBorder("Fixed/Variable bit roles on: " + myCCUName));
            componentPanel.setMinimumSize(new Dimension(500, 490));
            componentPanel.setPreferredSize(new Dimension(500, 490));
            
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
            
            java.awt.GridBagConstraints constraintsCCUFixedSuggestedTextField = new java.awt.GridBagConstraints();
            constraintsCCUFixedSuggestedTextField.gridx = 2; constraintsCCUFixedSuggestedTextField.gridy = 0;
            constraintsCCUFixedSuggestedTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUFixedSuggestedTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsCCUFixedSuggestedTextField.gridwidth = 1;
            componentPanel.add(getCCUFixedSuggestedTextField(), constraintsCCUFixedSuggestedTextField);
            
            java.awt.GridBagConstraints constraintsCCUVariableLabel = new java.awt.GridBagConstraints();
            constraintsCCUVariableLabel.gridx = 3; constraintsCCUVariableLabel.gridy = 0;
            constraintsCCUVariableLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUVariableLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsCCUVariableLabel.gridwidth = 1;
            componentPanel.add(getCCUVariableLabel(), constraintsCCUVariableLabel);
            
            java.awt.GridBagConstraints constraintsCCUVariableTextField = new java.awt.GridBagConstraints();
            constraintsCCUVariableTextField.gridx = 4; constraintsCCUVariableTextField.gridy = 0;
            constraintsCCUVariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUVariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsCCUVariableTextField.gridwidth = 1;
            componentPanel.add(getCCUVariableTextField(), constraintsCCUVariableTextField);
            
            java.awt.GridBagConstraints constraintsCCUVariableSuggestedTextField = new java.awt.GridBagConstraints();
            constraintsCCUVariableSuggestedTextField.gridx = 5; constraintsCCUVariableSuggestedTextField.gridy = 0;
            constraintsCCUVariableSuggestedTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUVariableSuggestedTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsCCUVariableSuggestedTextField.gridwidth = 1;
            componentPanel.add(getCCUVariableSuggestedTextField(), constraintsCCUVariableSuggestedTextField);
            
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
            
            java.awt.GridBagConstraints constraintsRepeater1SuggestedVariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater1SuggestedVariableTextField.gridx = 2; constraintsRepeater1SuggestedVariableTextField.gridy = 1;
            constraintsRepeater1SuggestedVariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater1SuggestedVariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater1SuggestedVariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater1SuggestedVariableTextField(), constraintsRepeater1SuggestedVariableTextField);
            
            
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
            
            java.awt.GridBagConstraints constraintsRepeater2SuggestedVariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater2SuggestedVariableTextField.gridx = 2; constraintsRepeater2SuggestedVariableTextField.gridy = 2;
            constraintsRepeater2SuggestedVariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater2SuggestedVariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater2SuggestedVariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater2SuggestedVariableTextField(), constraintsRepeater2SuggestedVariableTextField);
            
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
            
            java.awt.GridBagConstraints constraintsRepeater3SuggestedVariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater3SuggestedVariableTextField.gridx = 2; constraintsRepeater3SuggestedVariableTextField.gridy = 3;
            constraintsRepeater3SuggestedVariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater3SuggestedVariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater3SuggestedVariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater3SuggestedVariableTextField(), constraintsRepeater3SuggestedVariableTextField);
            
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
            
            java.awt.GridBagConstraints constraintsRepeater4SuggestedVariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater4SuggestedVariableTextField.gridx = 2; constraintsRepeater4SuggestedVariableTextField.gridy = 4;
            constraintsRepeater4SuggestedVariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater4SuggestedVariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater4SuggestedVariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater4SuggestedVariableTextField(), constraintsRepeater4SuggestedVariableTextField);
            
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
            
            java.awt.GridBagConstraints constraintsRepeater5SuggestedVariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater5SuggestedVariableTextField.gridx = 2; constraintsRepeater5SuggestedVariableTextField.gridy = 5;
            constraintsRepeater5SuggestedVariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater5SuggestedVariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater5SuggestedVariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater5SuggestedVariableTextField(), constraintsRepeater5SuggestedVariableTextField);
            
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
            
            java.awt.GridBagConstraints constraintsRepeater6SuggestedVariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater6SuggestedVariableTextField.gridx = 2; constraintsRepeater6SuggestedVariableTextField.gridy = 6;
            constraintsRepeater6SuggestedVariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater6SuggestedVariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater6SuggestedVariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater6SuggestedVariableTextField(), constraintsRepeater6SuggestedVariableTextField);
            
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
            
            java.awt.GridBagConstraints constraintsRepeater7SuggestedVariableTextField = new java.awt.GridBagConstraints();
            constraintsRepeater7SuggestedVariableTextField.gridx = 2; constraintsRepeater7SuggestedVariableTextField.gridy = 7;
            constraintsRepeater7SuggestedVariableTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeater7SuggestedVariableTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsRepeater7SuggestedVariableTextField.gridwidth = 1;
            componentPanel.add(getRepeater7SuggestedVariableTextField(), constraintsRepeater7SuggestedVariableTextField);
            
            java.awt.GridBagConstraints constraintsDuplicatesLabel = new java.awt.GridBagConstraints();
            constraintsDuplicatesLabel.gridx = 0; constraintsDuplicatesLabel.gridy = 8;
            constraintsDuplicatesLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDuplicatesLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsDuplicatesLabel.gridwidth = 6;
            componentPanel.add(getDuplicatesLabel(), constraintsDuplicatesLabel);
            
            java.awt.GridBagConstraints constraintsDuplicateCheckBoxPanel = new java.awt.GridBagConstraints();
            constraintsDuplicateCheckBoxPanel.gridx = 0; constraintsDuplicateCheckBoxPanel.gridy = 9;
            constraintsDuplicateCheckBoxPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDuplicateCheckBoxPanel.insets = new java.awt.Insets(0, 0, 0, 0);
            constraintsDuplicateCheckBoxPanel.gridwidth = 3;
            componentPanel.add(getDuplicateCheckBoxPanel(), constraintsDuplicateCheckBoxPanel);
            
            java.awt.GridBagConstraints constraintsDuplicateSuggestedCheckBoxPanel = new java.awt.GridBagConstraints();
            constraintsDuplicateSuggestedCheckBoxPanel.gridx = 3; constraintsDuplicateSuggestedCheckBoxPanel.gridy = 9;
            constraintsDuplicateSuggestedCheckBoxPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDuplicateSuggestedCheckBoxPanel.insets = new java.awt.Insets(0, 0, 0, 0);
            constraintsDuplicateSuggestedCheckBoxPanel.gridwidth = 3;
            componentPanel.add(getDuplicateSuggestedCheckBoxPanel(), constraintsDuplicateSuggestedCheckBoxPanel);
            
        }
        return componentPanel;
    }

    private JPanel getDuplicateCheckBoxPanel(){
        if(duplicateCheckBoxPanel == null){
            duplicateCheckBoxPanel = new JPanel();
            duplicateCheckBoxPanel.setLayout(new java.awt.GridBagLayout());
            duplicateCheckBoxPanel.setMinimumSize(new Dimension(190, 200));
            duplicateCheckBoxPanel.setPreferredSize(new Dimension(190, 200));
            duplicateCheckBoxPanel.setBorder(new TitleBorder("Other CCUs using these bit roles:"));
            
            java.awt.GridBagConstraints constraintsCheckBox1 = new java.awt.GridBagConstraints();
            constraintsCheckBox1.gridx = 0; constraintsCheckBox1.gridy = 0;
            constraintsCheckBox1.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox1.insets = new java.awt.Insets(2, 2, 0, 0);
            constraintsCheckBox1.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU1CheckBox(), constraintsCheckBox1);
            
            java.awt.GridBagConstraints constraintsCheckBox2 = new java.awt.GridBagConstraints();
            constraintsCheckBox2.gridx = 0; constraintsCheckBox2.gridy = 1;
            constraintsCheckBox2.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox2.insets = new java.awt.Insets(2,2,0,0);
            constraintsCheckBox2.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU2CheckBox(), constraintsCheckBox2);
            
            java.awt.GridBagConstraints constraintsCheckBox3 = new java.awt.GridBagConstraints();
            constraintsCheckBox3.gridx = 0; constraintsCheckBox3.gridy = 2;
            constraintsCheckBox3.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox3.insets = new java.awt.Insets(2,2,0,0);
            constraintsCheckBox3.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU3CheckBox(), constraintsCheckBox3);
            
            java.awt.GridBagConstraints constraintsCheckBox4 = new java.awt.GridBagConstraints();
            constraintsCheckBox4.gridx = 0; constraintsCheckBox4.gridy = 3;
            constraintsCheckBox4.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox4.insets = new java.awt.Insets(2,2,0,0);
            constraintsCheckBox4.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU4CheckBox(), constraintsCheckBox4);
            
            java.awt.GridBagConstraints constraintsCheckBox5 = new java.awt.GridBagConstraints();
            constraintsCheckBox5.gridx = 0; constraintsCheckBox5.gridy = 4;
            constraintsCheckBox5.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox5.insets = new java.awt.Insets(2,2,0,0);
            constraintsCheckBox5.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU5CheckBox(), constraintsCheckBox5);
            
            java.awt.GridBagConstraints constraintsCheckBox6 = new java.awt.GridBagConstraints();
            constraintsCheckBox6.gridx = 0; constraintsCheckBox6.gridy = 5;
            constraintsCheckBox6.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox6.insets = new java.awt.Insets(2,2,0,0);
            constraintsCheckBox6.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU6CheckBox(), constraintsCheckBox6);
            
            java.awt.GridBagConstraints constraintsCheckBox7 = new java.awt.GridBagConstraints();
            constraintsCheckBox7.gridx = 0; constraintsCheckBox7.gridy = 6;
            constraintsCheckBox7.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCheckBox7.insets = new java.awt.Insets(2,2,0,0);
            constraintsCheckBox7.gridwidth = 1;
            duplicateCheckBoxPanel.add(getCCU7CheckBox(), constraintsCheckBox7);
            
        }
        return duplicateCheckBoxPanel;
    }
    
    private JPanel getDuplicateSuggestedCheckBoxPanel(){
        if(duplicateSuggestedCheckBoxPanel == null){
            duplicateSuggestedCheckBoxPanel = new JPanel();
            duplicateSuggestedCheckBoxPanel.setLayout(new java.awt.GridBagLayout());
            duplicateSuggestedCheckBoxPanel.setMinimumSize(new Dimension(190, 200));
            duplicateSuggestedCheckBoxPanel.setPreferredSize(new Dimension(190, 200));
            duplicateSuggestedCheckBoxPanel.setBorder(new TitleBorder("CCUs using suggested bit roles:"));
            
            java.awt.GridBagConstraints constraintsSuggestedCheckBox1 = new java.awt.GridBagConstraints();
            constraintsSuggestedCheckBox1.gridx = 0; constraintsSuggestedCheckBox1.gridy = 0;
            constraintsSuggestedCheckBox1.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSuggestedCheckBox1.insets = new java.awt.Insets(2,2,0,0);
            constraintsSuggestedCheckBox1.gridwidth = 1;
            duplicateSuggestedCheckBoxPanel.add(getCCU1SuggestedCheckBox(), constraintsSuggestedCheckBox1);
            
            java.awt.GridBagConstraints constraintsSuggestedCheckBox2 = new java.awt.GridBagConstraints();
            constraintsSuggestedCheckBox2.gridx = 0; constraintsSuggestedCheckBox2.gridy = 1;
            constraintsSuggestedCheckBox2.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSuggestedCheckBox2.insets = new java.awt.Insets(2,2,0,0);
            constraintsSuggestedCheckBox2.gridwidth = 1;
            duplicateSuggestedCheckBoxPanel.add(getCCU2SuggestedCheckBox(), constraintsSuggestedCheckBox2);
            
            java.awt.GridBagConstraints constraintsSuggestedCheckBox3 = new java.awt.GridBagConstraints();
            constraintsSuggestedCheckBox3.gridx = 0; constraintsSuggestedCheckBox3.gridy = 2;
            constraintsSuggestedCheckBox3.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSuggestedCheckBox3.insets = new java.awt.Insets(2,2,0,0);
            constraintsSuggestedCheckBox3.gridwidth = 1;
            duplicateSuggestedCheckBoxPanel.add(getCCU3SuggestedCheckBox(), constraintsSuggestedCheckBox3);
            
            java.awt.GridBagConstraints constraintsSuggestedCheckBox4 = new java.awt.GridBagConstraints();
            constraintsSuggestedCheckBox4.gridx = 0; constraintsSuggestedCheckBox4.gridy = 3;
            constraintsSuggestedCheckBox4.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSuggestedCheckBox4.insets = new java.awt.Insets(2,2,0,0);
            constraintsSuggestedCheckBox4.gridwidth = 1;
            duplicateSuggestedCheckBoxPanel.add(getCCU4SuggestedCheckBox(), constraintsSuggestedCheckBox4);
            
            java.awt.GridBagConstraints constraintsSuggestedCheckBox5 = new java.awt.GridBagConstraints();
            constraintsSuggestedCheckBox5.gridx = 0; constraintsSuggestedCheckBox5.gridy = 4;
            constraintsSuggestedCheckBox5.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSuggestedCheckBox5.insets = new java.awt.Insets(2,2,0,0);
            constraintsSuggestedCheckBox5.gridwidth = 1;
            duplicateSuggestedCheckBoxPanel.add(getCCU5SuggestedCheckBox(), constraintsSuggestedCheckBox5);
            
            java.awt.GridBagConstraints constraintsSuggestedCheckBox6 = new java.awt.GridBagConstraints();
            constraintsSuggestedCheckBox6.gridx = 0; constraintsSuggestedCheckBox6.gridy = 5;
            constraintsSuggestedCheckBox6.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSuggestedCheckBox6.insets = new java.awt.Insets(2,2,0,0);
            constraintsSuggestedCheckBox6.gridwidth = 1;
            duplicateSuggestedCheckBoxPanel.add(getCCU6SuggestedCheckBox(), constraintsSuggestedCheckBox6);
            
            java.awt.GridBagConstraints constraintsSuggestedCheckBox7 = new java.awt.GridBagConstraints();
            constraintsSuggestedCheckBox7.gridx = 0; constraintsSuggestedCheckBox7.gridy = 6;
            constraintsSuggestedCheckBox7.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSuggestedCheckBox7.insets = new java.awt.Insets(2,2,0,0);
            constraintsSuggestedCheckBox7.gridwidth = 1;
            duplicateSuggestedCheckBoxPanel.add(getCCU7SuggestedCheckBox(), constraintsSuggestedCheckBox7);
            
        }
        return duplicateSuggestedCheckBoxPanel;
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
            
            java.awt.GridBagConstraints constraintsSuggestionButton = new java.awt.GridBagConstraints();
            constraintsSuggestionButton.gridx = 1; constraintsYesButton.gridy = 0;
            constraintsSuggestionButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSuggestionButton.insets = new java.awt.Insets(0, 5, 0, 5);
            constraintsSuggestionButton.gridwidth = 1;
            buttonPanel.add(getSuggestionButton(), constraintsSuggestionButton);
            
            java.awt.GridBagConstraints constraintsUseSuggestionButton = new java.awt.GridBagConstraints();
            constraintsUseSuggestionButton.gridx = 2; constraintsYesButton.gridy = 0;
            constraintsUseSuggestionButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsUseSuggestionButton.insets = new java.awt.Insets(0, 5, 0, 5);
            constraintsUseSuggestionButton.gridwidth = 1;
            buttonPanel.add(getUseSuggestionButton(), constraintsUseSuggestionButton);
            
            java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
            constraintsCancelButton.gridx = 3; constraintsCancelButton.gridy = 0;
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
            contentPanel.setSize( 500,600);
            
            java.awt.GridBagConstraints constraintsComponentPanel = new java.awt.GridBagConstraints();
            constraintsComponentPanel.gridx = 0; constraintsComponentPanel.gridy = 1;
            constraintsComponentPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsComponentPanel.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsComponentPanel.gridwidth = 4;
            contentPanel.add(getComponentPanel(), constraintsComponentPanel);
            
            java.awt.GridBagConstraints constraintsLockComboBox = new java.awt.GridBagConstraints();
            constraintsLockComboBox.gridx = 0; constraintsLockComboBox.gridy = 2;
            constraintsLockComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsLockComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
            constraintsLockComboBox.gridwidth = 1;
            contentPanel.add(getLockCheckBox(), constraintsLockComboBox);
            
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
            ccuFixedTextField = new JTextField();
            ccuFixedTextField.setMinimumSize(new Dimension(30, 20));
            ccuFixedTextField.setPreferredSize(new Dimension(30, 20));
            ccuFixedTextField.setEditable(true);
            ccuFixedTextField.setColumns(2);
            ccuFixedTextField.setDocument( new LongRangeDocument(0L, 31L) );
        }
        return ccuFixedTextField;
    }
    
    public JTextField getCCUVariableTextField() {
        if (ccuVariableTextField == null) {
            ccuVariableTextField = new JTextField();
            ccuVariableTextField.setMinimumSize(new Dimension(30, 20));
            ccuVariableTextField.setPreferredSize(new Dimension(30, 20));
            ccuVariableTextField.setEditable(true);
            ccuVariableTextField.setColumns(2);
            ccuVariableTextField.setDocument( new LongRangeDocument(0L, 6L) );
        }
        return ccuVariableTextField;
    }
    
    public JTextField getCCUFixedSuggestedTextField() {
        if (ccuFixedSuggestedTextField == null) {
            ccuFixedSuggestedTextField = new JTextField("");
            ccuFixedSuggestedTextField.setMinimumSize(new Dimension(30, 20));
            ccuFixedSuggestedTextField.setPreferredSize(new Dimension(30, 20));
            ccuFixedSuggestedTextField.setEditable(false);
            ccuFixedSuggestedTextField.setColumns(2);
        }
        return ccuFixedSuggestedTextField;
    }
    
    public JTextField getCCUVariableSuggestedTextField() {
        if (ccuVariableSuggestedTextField == null) {
            ccuVariableSuggestedTextField = new JTextField();
            ccuVariableSuggestedTextField.setMinimumSize(new Dimension(30, 20));
            ccuVariableSuggestedTextField.setPreferredSize(new Dimension(30, 20));
            ccuVariableSuggestedTextField.setEditable(false);
            ccuVariableSuggestedTextField.setColumns(2);
        }
        return ccuVariableSuggestedTextField;
    }
    
    public JTextField getRepeater1VariableTextField() {
        if (repeater1VariableTextField == null) {
            repeater1VariableTextField = new JTextField();
            repeater1VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater1VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater1VariableTextField.setEditable(false);
            repeater1VariableTextField.setColumns(2);
        }
        return repeater1VariableTextField;
    }

    public JTextField getRepeater2VariableTextField() {
        if (repeater2VariableTextField == null) {
            repeater2VariableTextField = new JTextField();
            repeater2VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater2VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater2VariableTextField.setEditable(false);
            repeater2VariableTextField.setColumns(2);
        }
        return repeater2VariableTextField;
    }

    public JTextField getRepeater3VariableTextField() {
        if (repeater3VariableTextField == null) {
            repeater3VariableTextField = new JTextField();
            repeater3VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater3VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater3VariableTextField.setEditable(false);
            repeater3VariableTextField.setColumns(2);
        }
        return repeater3VariableTextField;
    }

    public JTextField getRepeater4VariableTextField() {
        if (repeater4VariableTextField == null) {
            repeater4VariableTextField = new JTextField();
            repeater4VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater4VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater4VariableTextField.setEditable(false);
            repeater4VariableTextField.setColumns(2);
        }
        return repeater4VariableTextField;
    }
    
    public JTextField getRepeater5VariableTextField() {
        if (repeater5VariableTextField == null) {
            repeater5VariableTextField = new JTextField();
            repeater5VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater5VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater5VariableTextField.setEditable(false);
            repeater5VariableTextField.setColumns(2);
        }
        return repeater5VariableTextField;
    }
    
    public JTextField getRepeater6VariableTextField() {
    if (repeater6VariableTextField == null) {
            repeater6VariableTextField = new JTextField();
            repeater6VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater6VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater6VariableTextField.setEditable(false);
            repeater6VariableTextField.setColumns(2);
        }
        return repeater6VariableTextField;
    }
    
    public JTextField getRepeater7VariableTextField() {
        if( repeater7VariableTextField == null ){
            repeater7VariableTextField = new JTextField();
            repeater7VariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater7VariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater7VariableTextField.setEditable(false);
            repeater7VariableTextField.setColumns(2);
        }
        return repeater7VariableTextField;
    }
    
    public JTextField getRepeater1SuggestedVariableTextField() {
        if (repeater1SuggestedVariableTextField == null) {
            repeater1SuggestedVariableTextField = new JTextField();
            repeater1SuggestedVariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater1SuggestedVariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater1SuggestedVariableTextField.setEditable(false);
            repeater1SuggestedVariableTextField.setColumns(2);
        }
        return repeater1SuggestedVariableTextField;
    }

    public JTextField getRepeater2SuggestedVariableTextField() {
        if (repeater2SuggestedVariableTextField == null) {
            repeater2SuggestedVariableTextField = new JTextField();
            repeater2SuggestedVariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater2SuggestedVariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater2SuggestedVariableTextField.setEditable(false);
            repeater2SuggestedVariableTextField.setColumns(2);
        }
        return repeater2SuggestedVariableTextField;
    }

    public JTextField getRepeater3SuggestedVariableTextField() {
        if (repeater3SuggestedVariableTextField == null) {
            repeater3SuggestedVariableTextField = new JTextField();
            repeater3SuggestedVariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater3SuggestedVariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater3SuggestedVariableTextField.setEditable(false);
            repeater3SuggestedVariableTextField.setColumns(2);
        }
        return repeater3SuggestedVariableTextField;
    }

    public JTextField getRepeater4SuggestedVariableTextField() {
        if (repeater4SuggestedVariableTextField == null) {
            repeater4SuggestedVariableTextField = new JTextField();
            repeater4SuggestedVariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater4SuggestedVariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater4SuggestedVariableTextField.setEditable(false);
            repeater4SuggestedVariableTextField.setColumns(2);
        }
        return repeater4SuggestedVariableTextField;
    }
    
    public JTextField getRepeater5SuggestedVariableTextField() {
        if (repeater5SuggestedVariableTextField == null) {
            repeater5SuggestedVariableTextField = new JTextField();
            repeater5SuggestedVariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater5SuggestedVariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater5SuggestedVariableTextField.setEditable(false);
            repeater5SuggestedVariableTextField.setColumns(2);
        }
        return repeater5SuggestedVariableTextField;
    }
    
    public JTextField getRepeater6SuggestedVariableTextField() {
    if (repeater6SuggestedVariableTextField == null) {
            repeater6SuggestedVariableTextField = new JTextField();
            repeater6SuggestedVariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater6SuggestedVariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater6SuggestedVariableTextField.setEditable(false);
            repeater6SuggestedVariableTextField.setColumns(2);
        }
        return repeater6SuggestedVariableTextField;
    }
    
    public JTextField getRepeater7SuggestedVariableTextField() {
        if( repeater7SuggestedVariableTextField == null ){
            repeater7SuggestedVariableTextField = new JTextField();
            repeater7SuggestedVariableTextField.setMinimumSize(new Dimension(30, 20));
            repeater7SuggestedVariableTextField.setPreferredSize(new Dimension(30, 20));
            repeater7SuggestedVariableTextField.setEditable(false);
            repeater7SuggestedVariableTextField.setColumns(2);
        }
        return repeater7SuggestedVariableTextField;
    }
    
    /**
     * Returns the choice selected
     * @return String
     */
    @SuppressWarnings("deprecation")
	public String getValue() {

        if( isShowing() == false )
        {
            setModal(true);
            show();
        }

        return choice;
    }
    
    @SuppressWarnings("deprecation")
	public static void main(java.lang.String[] args) {
        try {
            AdvancedRouteSetupDialog anAdvancedRouteSetupDialog;
            anAdvancedRouteSetupDialog = new AdvancedRouteSetupDialog(null,null);
            anAdvancedRouteSetupDialog.setModal(true);
            anAdvancedRouteSetupDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            anAdvancedRouteSetupDialog.show();
            anAdvancedRouteSetupDialog.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            CTILogger.error( exception.getMessage(), exception );
        }
    }

    public JCheckBox getLockCheckBox() {
        if( lockCheckBox == null ){
            lockCheckBox = new JCheckBox();
            lockCheckBox.setText("User Locked");
            
        }
        return lockCheckBox;
    }

    public void focusGained(FocusEvent e) {
        // do nothing
        
    }

    public void focusLost(FocusEvent e) {
        Object source = e.getSource();
        if(source == getCCUFixedTextField() ||
                source == getCCUVariableTextField() ||
                source == getRepeater1VariableTextField() || 
                source == getRepeater2VariableTextField() ||
                source == getRepeater3VariableTextField() ||
                source == getRepeater4VariableTextField() ||
                source == getRepeater5VariableTextField() ||
                source == getRepeater6VariableTextField() ||
                source == getRepeater7VariableTextField()) {
            if(isInputValid()) {
                findDuplicates();
                this.validate();
            }
        }
    }
}
