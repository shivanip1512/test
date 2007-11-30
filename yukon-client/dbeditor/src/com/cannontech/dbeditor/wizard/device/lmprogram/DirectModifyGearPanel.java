package com.cannontech.dbeditor.wizard.device.lmprogram;
/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;

public class DirectModifyGearPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, javax.swing.event.CaretListener, com.cannontech.common.gui.util.DataInputPanelListener {
    private String gearType = null;
    private javax.swing.JLabel ivjJLabelGearName = null;
    private javax.swing.JTextField ivjJTextFieldGearName = null;
    private javax.swing.JComboBox ivjJComboBoxGearType = null;
    private javax.swing.JLabel ivjJLabelGearType = null;
    private javax.swing.JScrollPane ivjJScrollPane1 = null;
    private GenericGearPanel ivjGenericGearPanel1 = null;
    private LatchingGearPanel ivjLatchingGearPanel1 = null;
    private MasterCycleGearPanel ivjMasterGearPanel1 = null;
    private SmartCycleGearPanel ivjSmartGearPanel1 = null;
    private TimeRefreshGearPanel ivjTimeGearPanel1 = null;
    private RotationGearPanel ivjRotationGearPanel1= null;
    private ThermostatSetbackGearPanel ivjThermoSetbackGearPanel1 = null;
    private SimpleThermostatSetbackGearPanel ivjSimpleThermoSetbackGearPanel1;
    private NoControlGearPanel ivjNoControlGearPanel1 = null;
    
/**
 * Constructor
 */
public DirectModifyGearPanel() {
    super();
    initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
    if (e.getSource() == getJComboBoxGearType()) 
        connEtoC2(e);
}

/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
public void caretUpdate(javax.swing.event.CaretEvent e) {
    if (e.getSource() == getJTextFieldGearName()) 
        connEtoC3(e);
}

/**
 * connEtoC10:  (JComboBoxHowToStop.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC11:  (JComboBoxCycleCountSndType.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC12:  (JComboBoxCycleCountSndType1.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC13:  (JTextFieldChangeTriggerOffset.caret.caretUpdate(javax.swing.event.CaretEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC13(javax.swing.event.CaretEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC2:  (JComboBoxGearType.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.jComboBoxGearType_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
    try {
        this.jComboBoxGearType_ActionPerformed(arg1);
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC3:  (JTextFieldGearName.caret.caretUpdate(javax.swing.event.CaretEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC4:  (JComboBoxShedTime.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC5:  (JComboBoxNumGroups.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC6:  (JComboBoxPeriodCount.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC7:  (JComboBoxSendRate.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC8:  (JComboBoxGroupSelection.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}

/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 5:37:00 PM)
 * @return java.lang.String
 */
public java.lang.String getGearType() {
    return gearType;
}


/**
 * Return the GenericGearPanel1 property value.
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel
 */
private GenericGearPanel getGenericGearPanel1() {
    if (ivjGenericGearPanel1 == null) {
        try {
            ivjGenericGearPanel1 = new com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel();
            ivjGenericGearPanel1.setName("GenericGearPanel1");
            ivjGenericGearPanel1.setLocation(0, 0);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjGenericGearPanel1;
}


/**
 * Return the JComboBoxGearType property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getJComboBoxGearType() {
    if (ivjJComboBoxGearType == null) {
        try {
            ivjJComboBoxGearType = new javax.swing.JComboBox();
            ivjJComboBoxGearType.setName("JComboBoxGearType");
            ivjJComboBoxGearType.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            ivjJComboBoxGearType.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);

            ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_TIME_REFRESH ) );
            ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_ROTATION ) );
            ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_MASTER_CYCLE ) );
            ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_SMART_CYCLE ) );
            ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_TRUE_CYCLE ) );
            ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_MAGNITUDE_CYCLE ) );
            ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_TARGET_CYCLE ) );
            ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_LATCHING ) );
            ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.THERMOSTAT_SETBACK ) );
            ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.SIMPLE_THERMOSTAT_SETBACK ) );
            ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.NO_CONTROL) );

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjJComboBoxGearType;
}


/**
 * Return the JLabelGearName property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelGearName() {
    if (ivjJLabelGearName == null) {
        try {
            ivjJLabelGearName = new javax.swing.JLabel();
            ivjJLabelGearName.setName("JLabelGearName");
            ivjJLabelGearName.setFont(new java.awt.Font("dialog", 0, 12));
            ivjJLabelGearName.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            ivjJLabelGearName.setText("Gear Name:");
            ivjJLabelGearName.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjJLabelGearName;
}


/**
 * Return the JLabelGearType property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelGearType() {
    if (ivjJLabelGearType == null) {
        try {
            ivjJLabelGearType = new javax.swing.JLabel();
            ivjJLabelGearType.setName("JLabelGearType");
            ivjJLabelGearType.setFont(new java.awt.Font("dialog", 0, 12));
            ivjJLabelGearType.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            ivjJLabelGearType.setText("Gear Type:");
            ivjJLabelGearType.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjJLabelGearType;
}


/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane1() {
    if (ivjJScrollPane1 == null) {
        try {
            ivjJScrollPane1 = new javax.swing.JScrollPane();
            ivjJScrollPane1.setName("JScrollPane1");
            ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            ivjJScrollPane1.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            ivjJScrollPane1.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
            getJScrollPane1().setViewportView(getIvjTimeGearPanel1());
            getJScrollPane1().getVerticalScrollBar().setUnitIncrement(10);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjJScrollPane1;
}

/**
 * Return the JTextFieldGearName property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getJTextFieldGearName() {
    if (ivjJTextFieldGearName == null) {
        try {
            ivjJTextFieldGearName = new javax.swing.JTextField();
            ivjJTextFieldGearName.setName("JTextFieldGearName");
            ivjJTextFieldGearName.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            ivjJTextFieldGearName.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
            ivjJTextFieldGearName.setDocument(
                    new TextFieldDocument(
                        TextFieldDocument.MAX_BASELINE_NAME_LENGTH,
                        TextFieldDocument.INVALID_CHARS_PAO) );
        } catch (java.lang.Throwable ivjExc) {

            handleException(ivjExc);
        }
    }
    return ivjJTextFieldGearName;
}

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
    LMProgramDirectGear gear = null;
    
    if( o == null )
   {
      setGearType( getJComboBoxGearType().getSelectedItem().toString() );
        gear = LMProgramDirectGear.createGearFactory( getGearType() );  
   }
    else
    {       
      setGearType( getJComboBoxGearType().getSelectedItem().toString() );
        gear = LMProgramDirectGear.createGearFactory( getGearType() );
        gear.setGearID(((LMProgramDirectGear)o).getGearID());
    }
    
    gear.setGearName( getJTextFieldGearName().getText() );
    gear.setControlMethod( getGearType() );
    
    if( gear instanceof com.cannontech.database.data.device.lm.SmartCycleGear )
    {
        return getIvjSmartGearPanel1().getValue(gear);          
    }
    else if( gear instanceof com.cannontech.database.data.device.lm.MasterCycleGear )
    {
        return getIvjMasterGearPanel1().getValue(gear); 
    }
    else if( gear instanceof com.cannontech.database.data.device.lm.TimeRefreshGear )
    {
        return getIvjTimeGearPanel1().getValue(gear);
    }
    //else if( gear.getControlMethod() == LMProgramDirectGearDefines.CONTROL_ROTATION)
    else if( gear instanceof com.cannontech.database.data.device.lm.RotationGear )
    {
        return getIvjRotationGearPanel1().getValue(gear);
    }
    else if( gear instanceof com.cannontech.database.data.device.lm.LatchingGear )
    {
        return getIvjLatchingGearPanel1().getValue(gear);       
    }
    else if( gear instanceof com.cannontech.database.data.device.lm.ThermostatSetbackGear )
    {
        return getIvjThermoSetbackGearPanel1().getValue(gear);  
    }
    else if (gear instanceof com.cannontech.database.data.device.lm.SimpleThermostatRampingGear)
    {
        return getIvjSimpleThermoSetbackGearPanel1().getValue(gear);
    }
    else if( gear instanceof com.cannontech.database.data.device.lm.NoControlGear)
    {
        return getNoControlGearPanel().getValue(gear);  
    }
    else
        return gear;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

    /* Uncomment the following lines to print uncaught exceptions to stdout */
    com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
    com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
    getJComboBoxGearType().addActionListener(this);
    getJTextFieldGearName().addCaretListener(this);
    
    //Listeners for the variety of gear panels
    getIvjTimeGearPanel1().addDataInputPanelListener(this);
    getIvjLatchingGearPanel1().addDataInputPanelListener(this);
    getIvjMasterGearPanel1().addDataInputPanelListener(this);
    getIvjRotationGearPanel1().addDataInputPanelListener(this);
    getIvjSmartGearPanel1().addDataInputPanelListener(this);
    getIvjThermoSetbackGearPanel1().addDataInputPanelListener(this);
    getIvjSimpleThermoSetbackGearPanel1().addDataInputPanelListener(this);
    getNoControlGearPanel().addDataInputPanelListener(this);
    

}

/**
 * Initialize the class.
 */
private void initialize() {
    try {
        setName("DirectGearPanel");
        setToolTipText("");
        setLayout(new java.awt.GridBagLayout());
        setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
        setPreferredSize(new java.awt.Dimension(303, 194));
        setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        setSize(426, 496);
        setMinimumSize(new java.awt.Dimension(10, 10));

        java.awt.GridBagConstraints constraintsJLabelGearName = new java.awt.GridBagConstraints();
        constraintsJLabelGearName.gridx = 1; constraintsJLabelGearName.gridy = 1;
        constraintsJLabelGearName.ipadx = 9;
        constraintsJLabelGearName.insets = new java.awt.Insets(3, 6, 3, 0);
        add(getJLabelGearName(), constraintsJLabelGearName);

        java.awt.GridBagConstraints constraintsJTextFieldGearName = new java.awt.GridBagConstraints();
        constraintsJTextFieldGearName.gridx = 2; constraintsJTextFieldGearName.gridy = 1;
        constraintsJTextFieldGearName.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsJTextFieldGearName.weightx = 1.0;
        constraintsJTextFieldGearName.ipadx = 210;
        constraintsJTextFieldGearName.insets = new java.awt.Insets(1, 0, 1, 130);
        add(getJTextFieldGearName(), constraintsJTextFieldGearName);

        java.awt.GridBagConstraints constraintsJLabelGearType = new java.awt.GridBagConstraints();
        constraintsJLabelGearType.gridx = 1; constraintsJLabelGearType.gridy = 2;
        constraintsJLabelGearType.ipadx = 17;
        constraintsJLabelGearType.insets = new java.awt.Insets(5, 6, 6, 0);
        add(getJLabelGearType(), constraintsJLabelGearType);

        java.awt.GridBagConstraints constraintsJComboBoxGearType = new java.awt.GridBagConstraints();
        constraintsJComboBoxGearType.gridx = 2; constraintsJComboBoxGearType.gridy = 2;
        constraintsJComboBoxGearType.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsJComboBoxGearType.weightx = 1.0;
        constraintsJComboBoxGearType.ipadx = 88;
        constraintsJComboBoxGearType.insets = new java.awt.Insets(2, 0, 2, 130);
        add(getJComboBoxGearType(), constraintsJComboBoxGearType);

        java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
        constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 3;
        constraintsJScrollPane1.gridwidth = 2;
        constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
        constraintsJScrollPane1.weightx = 1.0;
        constraintsJScrollPane1.weighty = 1.0;
        constraintsJScrollPane1.ipadx = 398;
        constraintsJScrollPane1.ipady = 419;
        constraintsJScrollPane1.insets = new java.awt.Insets(2, 0, 4, 6);
        add(getJScrollPane1(), constraintsJScrollPane1);
        initConnections();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }

    getJComboBoxGearType().setSelectedItem( LMProgramDirectGear.CONTROL_TIME_REFRESH );
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
    if( getJTextFieldGearName().getText() == null
         || getJTextFieldGearName().getText().length() <= 0 )
    {
        setErrorString("A name for this gear must be specified");
        return false;
    }
    
    return true;
}


/**
 * Comment
 */
public void jComboBoxGearType_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

    if( getJComboBoxGearType().getSelectedItem() != null )
    {
        setGearType( getJComboBoxGearType().getSelectedItem().toString() );
        getGenericGearPanel1().jComboBoxWhenChange_ActionPerformed(actionEvent);
        
        fireInputUpdate();
    }
    
    return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
    try {
        javax.swing.JFrame frame = new javax.swing.JFrame();
        DirectModifyGearPanel aDirectModifyGearPanel;
        aDirectModifyGearPanel = new DirectModifyGearPanel();
        frame.setContentPane(aDirectModifyGearPanel);
        frame.setSize(aDirectModifyGearPanel.getSize());
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
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
        com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
    }
}

/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 5:37:00 PM)
 * @param newGearType java.lang.String
 */
private void setGearType(java.lang.String newGearType)
{
    gearType = StringUtils.removeChars( ' ', newGearType );

    if( getGearType() == null )
        return;

    if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_LATCHING) )
    {
        //Latching
        getJScrollPane1().setViewportView(getIvjLatchingGearPanel1());
    }
    else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_MASTER_CYCLE) )
    {
        //MasterCycle
        getJScrollPane1().setViewportView(getIvjMasterGearPanel1());
    }
    else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_ROTATION) )
    {
        //Rotation
        getJScrollPane1().setViewportView(getIvjRotationGearPanel1());
    }
    else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_SMART_CYCLE)
                 || getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_TRUE_CYCLE)
                 || getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_MAGNITUDE_CYCLE)
                 || getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_TARGET_CYCLE))
    {
        //SmartCycle
        getJScrollPane1().setViewportView(getIvjSmartGearPanel1());
        getIvjSmartGearPanel1().setTargetCycle(getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_TARGET_CYCLE));
    }
    else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_TIME_REFRESH) )
    {
        //TimeRefresh
        getJScrollPane1().setViewportView(getIvjTimeGearPanel1());
    }

    else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.THERMOSTAT_SETBACK) )
    {
        //Thermostat Setback
        getJScrollPane1().setViewportView(getIvjThermoSetbackGearPanel1());
    }
    else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.SIMPLE_THERMOSTAT_SETBACK) )
    {
        //Simple Thermostat Setback
        getJScrollPane1().setViewportView(getIvjSimpleThermoSetbackGearPanel1());
    }
    else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.NO_CONTROL) )
    {
        //No Control
        getJScrollPane1().setViewportView(getNoControlGearPanel());
    }
    else
        throw new Error("Unknown LMProgramDirectGear " +
            "type found, the value = " + getGearType() );

    return;
    
}


/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 5:22:51 PM)
 * @param newIvjGenericGearPanel1 com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel
 */
public void setIvjGenericGearPanel1(GenericGearPanel newIvjGenericGearPanel1) {
    ivjGenericGearPanel1 = newIvjGenericGearPanel1;
}


/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
    LMProgramDirectGear gear = null;
    
    if( o == null )
    {
        return;
    }
    else
        gear = (LMProgramDirectGear)o;

    getJComboBoxGearType().setSelectedItem( StringUtils.addCharBetweenWords( ' ', gear.getControlMethod() ) );
    getJTextFieldGearName().setText( gear.getGearName() );

    if( gear instanceof com.cannontech.database.data.device.lm.SmartCycleGear )
    {
        getIvjSmartGearPanel1().setValue(gear);         
    }
    else if( gear instanceof com.cannontech.database.data.device.lm.MasterCycleGear )
    {
        getIvjMasterGearPanel1().setValue(gear);    
    }
    else if( gear instanceof com.cannontech.database.data.device.lm.TimeRefreshGear )
    {
        getIvjTimeGearPanel1().setValue(gear);
    }
    else if( gear instanceof com.cannontech.database.data.device.lm.RotationGear )
    {
        getIvjRotationGearPanel1().setValue(gear);
    }
    else if( gear instanceof com.cannontech.database.data.device.lm.LatchingGear )
    {
        getIvjLatchingGearPanel1().setValue(gear);      
    }

    else if( gear instanceof com.cannontech.database.data.device.lm.ThermostatSetbackGear )
    {
        getIvjThermoSetbackGearPanel1().setValue(gear); 
    }
    else if (gear instanceof com.cannontech.database.data.device.lm.SimpleThermostatRampingGear)
    {
        getIvjSimpleThermoSetbackGearPanel1().setValue(gear);
    }
    else if( gear instanceof com.cannontech.database.data.device.lm.NoControlGear )
    {
        getNoControlGearPanel().setValue(gear); 
    }
    else
        return;
    
}


/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
    //fire this event for all JCSpinFields!!
    this.fireInputUpdate();
}


/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
    /**
     * Returns the ivjLatchingGearPanel1.
     * @return LatchingGearPanel
     */
    public LatchingGearPanel getIvjLatchingGearPanel1() {
        if(ivjLatchingGearPanel1 == null)
            ivjLatchingGearPanel1 = new LatchingGearPanel();
        return ivjLatchingGearPanel1;
    }

    /**
     * Returns the ivjMasterGearPanel1.
     * @return MasterCycleGearPanel
     */
    public MasterCycleGearPanel getIvjMasterGearPanel1() {
        if(ivjMasterGearPanel1 == null)
            ivjMasterGearPanel1 = new MasterCycleGearPanel();
        return ivjMasterGearPanel1;
    }

    /**
     * Returns the ivjRotationGearPanel1.
     * @return RotationGearPanel
     */
    public RotationGearPanel getIvjRotationGearPanel1() {
        if(ivjRotationGearPanel1 == null)
            ivjRotationGearPanel1 = new RotationGearPanel();
        return ivjRotationGearPanel1;
    }

    /**
     * Returns the ivjSmartGearPanel1.
     * @return SmartCycleGearPanel
     */
    public SmartCycleGearPanel getIvjSmartGearPanel1() {
        if(ivjSmartGearPanel1 == null)
            ivjSmartGearPanel1 = new SmartCycleGearPanel();
        return ivjSmartGearPanel1;
    }


    /**
     * Returns the ivjThermoSetbackGearPanel1.
     * @return ThermostatSetbackGearPanel
     */
    public ThermostatSetbackGearPanel getIvjThermoSetbackGearPanel1() {
        if(ivjThermoSetbackGearPanel1 == null)
            ivjThermoSetbackGearPanel1 = new ThermostatSetbackGearPanel();
        return ivjThermoSetbackGearPanel1;
    }
    
    public SimpleThermostatSetbackGearPanel getIvjSimpleThermoSetbackGearPanel1() {
        if(ivjSimpleThermoSetbackGearPanel1 == null)
            ivjSimpleThermoSetbackGearPanel1 = new SimpleThermostatSetbackGearPanel();
        return ivjSimpleThermoSetbackGearPanel1;
    }

    public NoControlGearPanel getNoControlGearPanel() {
        if(ivjNoControlGearPanel1 == null)
            ivjNoControlGearPanel1 = new NoControlGearPanel();
        return ivjNoControlGearPanel1;
    }
    /**
     * Returns the ivjTimeGearPanel1.
     * @return TimeRefreshGearPanel
     */
    public TimeRefreshGearPanel getIvjTimeGearPanel1() {
        if(ivjTimeGearPanel1 == null)
            ivjTimeGearPanel1 = new TimeRefreshGearPanel();
        return ivjTimeGearPanel1;
    }

    /**
     * Sets the ivjLatchingGearPanel1.
     * @param ivjLatchingGearPanel1 The ivjLatchingGearPanel1 to set
     */
    public void setIvjLatchingGearPanel1(LatchingGearPanel ivjLatchingGearPanel1) {
        this.ivjLatchingGearPanel1 = ivjLatchingGearPanel1;
    }

    /**
     * Sets the ivjMasterGearPanel1.
     * @param ivjMasterGearPanel1 The ivjMasterGearPanel1 to set
     */
    public void setIvjMasterGearPanel1(MasterCycleGearPanel ivjMasterGearPanel1) {
        this.ivjMasterGearPanel1 = ivjMasterGearPanel1;
    }

    public void setIvjNoControlGearPanel1(NoControlGearPanel ivjNoControlGearPanel1) {
        this.ivjNoControlGearPanel1 = ivjNoControlGearPanel1;
    }
    /**
     * Sets the ivjRotationGearPanel1.
     * @param ivjRotationGearPanel1 The ivjRotationGearPanel1 to set
     */
    public void setIvjRotationGearPanel1(RotationGearPanel ivjRotationGearPanel1) {
        this.ivjRotationGearPanel1 = ivjRotationGearPanel1;
    }

    /**
     * Sets the ivjSmartGearPanel1.
     * @param ivjSmartGearPanel1 The ivjSmartGearPanel1 to set
     */
    public void setIvjSmartGearPanel1(SmartCycleGearPanel ivjSmartGearPanel1) {
        this.ivjSmartGearPanel1 = ivjSmartGearPanel1;
    }

    /**
     * Sets the ivjThermoSetbackGearPanel1.
     * @param ivjThermoSetbackGearPanel1 The ivjThermoSetbackGearPanel1 to set
     */
    public void setIvjThermoSetbackGearPanel1(ThermostatSetbackGearPanel ivjThermoSetbackGearPanel1) {
        this.ivjThermoSetbackGearPanel1 = ivjThermoSetbackGearPanel1;
    }

    /**
     * Sets the ivjTimeGearPanel1.
     * @param ivjTimeGearPanel1 The ivjTimeGearPanel1 to set
     */
    public void setIvjTimeGearPanel1(TimeRefreshGearPanel ivjTimeGearPanel1) {
        this.ivjTimeGearPanel1 = ivjTimeGearPanel1;
    }


    /* (non-Javadoc)
     * @see com.cannontech.common.gui.util.DataInputPanelListener#inputUpdate(com.cannontech.common.editor.PropertyPanelEvent)
     */
    public void inputUpdate(PropertyPanelEvent event) {
        fireInputUpdate();      
    }

}