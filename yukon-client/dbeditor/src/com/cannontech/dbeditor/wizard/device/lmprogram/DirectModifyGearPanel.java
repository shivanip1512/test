package com.cannontech.dbeditor.wizard.device.lmprogram;
/**
 * This type was created in VisualAge.
 */

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.database.data.device.lm.BeatThePeakGear;
import com.cannontech.database.data.device.lm.EcobeeCycleGear;
import com.cannontech.database.data.device.lm.HoneywellCycleGear;
import com.cannontech.database.data.device.lm.ItronCycleGear;
import com.cannontech.database.data.device.lm.LatchingGear;
import com.cannontech.database.data.device.lm.MasterCycleGear;
import com.cannontech.database.data.device.lm.NestCriticalCycleGear;
import com.cannontech.database.data.device.lm.NestStandardCycleGear;
import com.cannontech.database.data.device.lm.NoControlGear;
import com.cannontech.database.data.device.lm.RotationGear;
import com.cannontech.database.data.device.lm.SepCycleGear;
import com.cannontech.database.data.device.lm.SepTemperatureOffsetGear;
import com.cannontech.database.data.device.lm.SimpleThermostatRampingGear;
import com.cannontech.database.data.device.lm.SmartCycleGear;
import com.cannontech.database.data.device.lm.ThermostatSetbackGear;
import com.cannontech.database.data.device.lm.TimeRefreshGear;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;

public class DirectModifyGearPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, javax.swing.event.CaretListener, com.cannontech.common.gui.util.DataInputPanelListener {
    private GearControlMethod gearControlMethod;
    private JLabel ivjJLabelGearName;
    private JTextField ivjJTextFieldGearName;
    private JComboBox<GearControlMethod> ivjJComboBoxGearType;
    private JLabel ivjJLabelGearType;
    private JScrollPane ivjJScrollPane1;
    private GenericGearPanel ivjGenericGearPanel1;
    private LatchingGearPanel ivjLatchingGearPanel1;
    private MasterCycleGearPanel ivjMasterGearPanel1;
    private SmartCycleGearPanel ivjSmartGearPanel1;
    private SepCycleGearPanel sepCycleGearPanel;
    private SepTemperatureOffsetGearPanel sepTemperatureOffsetGearPanel;
    private EcobeeCycleGearPanel ecobeeCycleGearPanel;
    private NestCriticalCycleGearPanel nestCriticalCycleGearPanel;
    private NestStandardCycleGearPanel nestStandardCycleGearPanel;
    private ItronCycleGearPanel itronCycleGearPanel;
    private HoneywellCycleGearPanel honeywellCycleGearPanel;
    private TimeRefreshGearPanel ivjTimeGearPanel1;
    private RotationGearPanel ivjRotationGearPanel1;
    private ThermostatSetbackGearPanel ivjThermoSetbackGearPanel1;
    private SimpleThermostatSetbackGearPanel ivjSimpleThermoSetbackGearPanel1;
    private BeatThePeakGearPanel ivjBeatThePeakGearPanel1;
    private NoControlGearPanel ivjNoControlGearPanel1;
    
    private PaoType programType;
/**
 * Constructor
 */
public DirectModifyGearPanel(PaoType programType) {
    super();
    this.programType = programType;
    initialize();
}

/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
@Override
public void actionPerformed(ActionEvent e) {
    if (e.getSource() == getJComboBoxGearType()) {
        connEtoC2(e);
    }
}

/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
@Override
public void caretUpdate(CaretEvent e) {
    if (e.getSource() == getJTextFieldGearName()) {
        connEtoC3(e);
    }
}

/**
 * connEtoC10:  (JComboBoxHowToStop.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC10(ActionEvent arg1) {
    try {
        fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC11:  (JComboBoxCycleCountSndType.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC11(ActionEvent arg1) {
    try {
        fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC12:  (JComboBoxCycleCountSndType1.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC12(ActionEvent arg1) {
    try {
        fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC13:  (JTextFieldChangeTriggerOffset.caret.caretUpdate(javax.swing.event.CaretEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC13(CaretEvent arg1) {
    try {
        fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC2:  (JComboBoxGearType.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.jComboBoxGearType_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC2(ActionEvent arg1) {
    try {
        jComboBoxGearType_ActionPerformed(arg1);
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC3:  (JTextFieldGearName.caret.caretUpdate(javax.swing.event.CaretEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC3(CaretEvent arg1) {
    try {
        fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC4:  (JComboBoxShedTime.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC4(ActionEvent arg1) {
    try {
        fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC5:  (JComboBoxNumGroups.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC5(ActionEvent arg1) {
    try {
        fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC6:  (JComboBoxPeriodCount.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC6(ActionEvent arg1) {
    try {
        fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC7:  (JComboBoxSendRate.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC7(ActionEvent arg1) {
    try {
        fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}


/**
 * connEtoC8:  (JComboBoxGroupSelection.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC8(ActionEvent arg1) {
    try {
        fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}

/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 5:37:00 PM)
 * @return java.lang.String
 */
public GearControlMethod getGearType() {
    return gearControlMethod;
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
private JComboBox<GearControlMethod> getJComboBoxGearType() {
    if (ivjJComboBoxGearType == null) {
        try {
            ivjJComboBoxGearType = new JComboBox<>();
            ivjJComboBoxGearType.setName("JComboBoxGearType");
            ivjJComboBoxGearType.setAlignmentX(LEFT_ALIGNMENT);
            ivjJComboBoxGearType.setAlignmentY(TOP_ALIGNMENT);

            if(programType == PaoType.LM_SEP_PROGRAM) {
                ivjJComboBoxGearType.addItem(GearControlMethod.SepCycle);
                ivjJComboBoxGearType.addItem(GearControlMethod.SepTemperatureOffset);
                ivjJComboBoxGearType.addItem(GearControlMethod.NoControl);  
            } else if(programType == PaoType.LM_ECOBEE_PROGRAM) {
                ivjJComboBoxGearType.addItem(GearControlMethod.EcobeeCycle);
            } else if(programType == PaoType.LM_HONEYWELL_PROGRAM) {
                ivjJComboBoxGearType.addItem(GearControlMethod.HoneywellCycle);
            } else if(programType == PaoType.LM_ITRON_PROGRAM) {
                ivjJComboBoxGearType.addItem(GearControlMethod.ItronCycle);
            } else if(programType == PaoType.LM_NEST_PROGRAM) {
                ivjJComboBoxGearType.addItem(GearControlMethod.NestCriticalCycle);
                ivjJComboBoxGearType.addItem(GearControlMethod.NestStandardCycle);
            } else {
                for(GearControlMethod gearControlMethod : GearControlMethod.values()) {
                    ivjJComboBoxGearType.addItem(gearControlMethod);
                }
                // Remove SepCycle and SepTempOffset from the combobox for non SEP programs.
                ivjJComboBoxGearType.removeItem(GearControlMethod.SepCycle);
                ivjJComboBoxGearType.removeItem(GearControlMethod.SepTemperatureOffset);
                ivjJComboBoxGearType.removeItem(GearControlMethod.EcobeeCycle);
                ivjJComboBoxGearType.removeItem(GearControlMethod.HoneywellCycle);
                ivjJComboBoxGearType.removeItem(GearControlMethod.NestCriticalCycle);
                ivjJComboBoxGearType.removeItem(GearControlMethod.NestStandardCycle);
                ivjJComboBoxGearType.removeItem(GearControlMethod.ItronCycle);
            }
	            
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
private JLabel getJLabelGearName() {
    if (ivjJLabelGearName == null) {
        try {
            ivjJLabelGearName = new javax.swing.JLabel();
            ivjJLabelGearName.setName("JLabelGearName");
            ivjJLabelGearName.setFont(new Font("dialog", 0, 12));
            ivjJLabelGearName.setAlignmentX(LEFT_ALIGNMENT);
            ivjJLabelGearName.setText("Gear Name:");
            ivjJLabelGearName.setAlignmentY(TOP_ALIGNMENT);
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
            ivjJLabelGearType.setFont(new Font("dialog", 0, 12));
            ivjJLabelGearType.setAlignmentX(LEFT_ALIGNMENT);
            ivjJLabelGearType.setText("Gear Type:");
            ivjJLabelGearType.setAlignmentY(TOP_ALIGNMENT);
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
            ivjJScrollPane1.setAlignmentX(LEFT_ALIGNMENT);
            ivjJScrollPane1.setAlignmentY(TOP_ALIGNMENT);
            ivjJScrollPane1.getVerticalScrollBar().setUnitIncrement(10);

            // Set the gear type so that the correct panel can be displayed.
            setGearType((GearControlMethod)getJComboBoxGearType().getSelectedItem());
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
            ivjJTextFieldGearName.setAlignmentX(LEFT_ALIGNMENT);
            ivjJTextFieldGearName.setAlignmentY(TOP_ALIGNMENT);
            ivjJTextFieldGearName.setDocument(
                    new TextFieldDocument(
                        TextFieldDocument.MAX_BASELINE_NAME_LENGTH,
                        PaoUtils.ILLEGAL_NAME_CHARS) );
        } catch (java.lang.Throwable ivjExc) {

            handleException(ivjExc);
        }
    }
    return ivjJTextFieldGearName;
}

/**
 * getValue method comment.
 */
@Override
public Object getValue(Object o) 
{
	Object obj = null;
    LMProgramDirectGear gear = null;
    GearControlMethod method = (GearControlMethod)getJComboBoxGearType().getSelectedItem();
    
    setGearType(method);
    gear = method.createNewGear();  

    if( o != null ){
    	gear.setGearID(((LMProgramDirectGear)o).getGearID());
	}
    
    gear.setGearName(getJTextFieldGearName().getText());
    gear.setControlMethod(method);
    
    switch (method) {
        case TrueCycle:// All of these are 'smart' gears
        case MagnitudeCycle:
        case TargetCycle:
	    case SmartCycle: {
	    	obj = getIvjSmartGearPanel1().getValue(gear);
	    	break;
	    }
	    case EcobeeCycle:
	        obj = getEcobeeCycleGearPanel().getValue(gear);
	        break;
	    case HoneywellCycle:
            obj = getHoneywellCycleGearPanel().getValue(gear);
            break;
        case NestCriticalCycle:
            obj = getNestCriticalCycleGearPanel().getValue(gear);
            break;
        case ItronCycle:
            obj = getItronCycleGearPanel().getValue(gear);
            break;
        case NestStandardCycle:
            obj = getNestStandardCycleGearPanel().getValue(gear);
            break;
	    case SepCycle:
	        obj = getSepCycleGearPanel().getValue(gear);
	        break;
	    case SepTemperatureOffset:
	        obj = getSepTemperatureOffsetGearPanel().getValue(gear);
	       break;
	    case MasterCycle: {
	    	obj = getIvjMasterGearPanel1().getValue(gear);
	    	break;
	    }
	    case TimeRefresh: {
	    	obj = getIvjTimeGearPanel1().getValue(gear);
	    	break;
	    }
	    case Rotation: {
	    	obj = getIvjRotationGearPanel1().getValue(gear);
	    	break;
	    }
	    case Latching: {
	    	obj = getIvjLatchingGearPanel1().getValue(gear);
	    	break;
	    }
	    case ThermostatRamping: {
	    	obj = getIvjThermoSetbackGearPanel1().getValue(gear);
	    	break;
	    }
	    case SimpleThermostatRamping: {
	        obj = getIvjSimpleThermoSetbackGearPanel1().getValue(gear);
	        break;
	    }
	    case BeatThePeak: {
	        obj = getIvjBeatThePeakGearPanel1().getValue(gear);
	        break;
	    }
	    case NoControl: {
	    	obj = getNoControlGearPanel().getValue(gear);
	    	break;
	    }
	    default: {
	    	obj = gear;
	    	break;
	    }	
    }
    
    return obj;
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
    getEcobeeCycleGearPanel().addDataInputPanelListener(this);
    getHoneywellCycleGearPanel().addDataInputPanelListener(this);
    getItronCycleGearPanel().addDataInputPanelListener(this);
    getNestCriticalCycleGearPanel().addDataInputPanelListener(this);
    getNestStandardCycleGearPanel().addDataInputPanelListener(this);
    getSepCycleGearPanel().addDataInputPanelListener(this);
    getSepTemperatureOffsetGearPanel().addDataInputPanelListener(this);
    getIvjThermoSetbackGearPanel1().addDataInputPanelListener(this);
    getIvjSimpleThermoSetbackGearPanel1().addDataInputPanelListener(this);
    getIvjBeatThePeakGearPanel1().addDataInputPanelListener(this);
    getNoControlGearPanel().addDataInputPanelListener(this);
    

}

/**
 * Initialize the class.
 */
private void initialize() {
    try {
        setName("DirectGearPanel");
        setToolTipText("");
        setLayout(new GridBagLayout());
        setAlignmentY(TOP_ALIGNMENT);
        setPreferredSize(new Dimension(303, 194));
        setAlignmentX(LEFT_ALIGNMENT);
        setSize(426, 496);
        setMinimumSize(new Dimension(10, 10));

        java.awt.GridBagConstraints constraintsJLabelGearName = new java.awt.GridBagConstraints();
        constraintsJLabelGearName.gridx = 1; constraintsJLabelGearName.gridy = 1;
        constraintsJLabelGearName.ipadx = 9;
        constraintsJLabelGearName.insets = new Insets(3, 6, 3, 0);
        add(getJLabelGearName(), constraintsJLabelGearName);

        java.awt.GridBagConstraints constraintsJTextFieldGearName = new java.awt.GridBagConstraints();
        constraintsJTextFieldGearName.gridx = 2; constraintsJTextFieldGearName.gridy = 1;
        constraintsJTextFieldGearName.fill = GridBagConstraints.HORIZONTAL;
        constraintsJTextFieldGearName.weightx = 1.0;
        constraintsJTextFieldGearName.ipadx = 210;
        constraintsJTextFieldGearName.insets = new Insets(1, 0, 1, 130);
        add(getJTextFieldGearName(), constraintsJTextFieldGearName);

        java.awt.GridBagConstraints constraintsJLabelGearType = new java.awt.GridBagConstraints();
        constraintsJLabelGearType.gridx = 1; constraintsJLabelGearType.gridy = 2;
        constraintsJLabelGearType.ipadx = 17;
        constraintsJLabelGearType.insets = new Insets(5, 6, 6, 0);
        add(getJLabelGearType(), constraintsJLabelGearType);

        java.awt.GridBagConstraints constraintsJComboBoxGearType = new java.awt.GridBagConstraints();
        constraintsJComboBoxGearType.gridx = 2; constraintsJComboBoxGearType.gridy = 2;
        constraintsJComboBoxGearType.fill = GridBagConstraints.HORIZONTAL;
        constraintsJComboBoxGearType.weightx = 1.0;
        constraintsJComboBoxGearType.ipadx = 88;
        constraintsJComboBoxGearType.insets = new Insets(2, 0, 2, 130);
        add(getJComboBoxGearType(), constraintsJComboBoxGearType);

        java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
        constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 3;
        constraintsJScrollPane1.gridwidth = 2;
        constraintsJScrollPane1.fill = GridBagConstraints.BOTH;
        constraintsJScrollPane1.weightx = 1.0;
        constraintsJScrollPane1.weighty = 1.0;
        constraintsJScrollPane1.ipadx = 398;
        constraintsJScrollPane1.ipady = 419;
        constraintsJScrollPane1.insets = new Insets(2, 0, 4, 6);
        add(getJScrollPane1(), constraintsJScrollPane1);
        initConnections();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
@Override
public boolean isInputValid() 
{
    if( getJTextFieldGearName().getText() == null
         || getJTextFieldGearName().getText().length() <= 0 )
    {
        setErrorString("A name for this gear must be specified");
        return false;
    }
    
    if (getJComboBoxGearType().getSelectedItem() == GearControlMethod.NestStandardCycle) {
         if (!getNestStandardCycleGearPanel().isInputValid()) {
             setErrorString("Peak as Uniform and Post as Ramping is not valid combination");
             return false;
          }
     }
    
    return true;
}


/**
 * Comment
 */
public void jComboBoxGearType_ActionPerformed(ActionEvent actionEvent) 
{

    if( getJComboBoxGearType().getSelectedItem() != null )
    {
       	GearControlMethod gearControlMethod = (GearControlMethod)getJComboBoxGearType().getSelectedItem();
    	setGearType(gearControlMethod);
        getGenericGearPanel1().jComboBoxWhenChange_ActionPerformed(actionEvent);
        
        fireInputUpdate();
    }
    
    return;
}

/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 5:37:00 PM)
 * @param newGearType java.lang.String
 */
private void setGearType(GearControlMethod method)
{
    gearControlMethod = method;

    if( getGearType() == null ) {
        return;
    }

    switch (method) {
    case Latching:
        getJScrollPane1().setViewportView(getIvjLatchingGearPanel1());
    	break;
    case MasterCycle:
    	getJScrollPane1().setViewportView(getIvjMasterGearPanel1());
    	break;
    case Rotation:
    	getJScrollPane1().setViewportView(getIvjRotationGearPanel1());
    	break;
    	
    case SmartCycle:
    case TrueCycle:
        getJScrollPane1().setViewportView(getIvjSmartGearPanel1());
        getIvjSmartGearPanel1().setTrueOrSmartCycle(true);
        getIvjSmartGearPanel1().setTargetCycle(false);
        break;
    case MagnitudeCycle:
    case TargetCycle:
        getJScrollPane1().setViewportView(getIvjSmartGearPanel1());
        getIvjSmartGearPanel1().setTrueOrSmartCycle(false);
        getIvjSmartGearPanel1().setTargetCycle(method == GearControlMethod.TargetCycle);
    	break;

    case EcobeeCycle:
        getJScrollPane1().setViewportView(getEcobeeCycleGearPanel());
        break;
    case HoneywellCycle:
        getJScrollPane1().setViewportView(getHoneywellCycleGearPanel());
        break;
    case ItronCycle:
        getJScrollPane1().setViewportView(getItronCycleGearPanel());
        break;
    case NestCriticalCycle:
        getJScrollPane1().setViewportView(getNestCriticalCycleGearPanel());
        break;
    case NestStandardCycle:
        getJScrollPane1().setViewportView(getNestStandardCycleGearPanel());
        break;
    case SepCycle:
        getJScrollPane1().setViewportView(getSepCycleGearPanel());
        break;
    case SepTemperatureOffset:
        getJScrollPane1().setViewportView(getSepTemperatureOffsetGearPanel());
        break;
    case TimeRefresh:
    	getJScrollPane1().setViewportView(getIvjTimeGearPanel1());
    	break;
    case ThermostatRamping:
    	getJScrollPane1().setViewportView(getIvjThermoSetbackGearPanel1());
    	break;
    case SimpleThermostatRamping:
    	getJScrollPane1().setViewportView(getIvjSimpleThermoSetbackGearPanel1());
    	break;
    case BeatThePeak:
        getJScrollPane1().setViewportView(getIvjBeatThePeakGearPanel1());
        break;
    case NoControl:
    	getJScrollPane1().setViewportView(getNoControlGearPanel());
    	break;
    default:
        throw new Error("Unknown LMProgramDirectGear " + "type found, the value = " 
        		+ getGearType().toString() );
    }

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
@Override
public void setValue(Object o) {
    LMProgramDirectGear gear = null;
    
    if (o == null) {
        return;
    } else {
        gear = (LMProgramDirectGear)o;
    }

    getJComboBoxGearType().setSelectedItem(gear.getControlMethod());
    getJTextFieldGearName().setText( gear.getGearName() );

    if (gear instanceof SmartCycleGear) {
        getIvjSmartGearPanel1().setValue(gear);
    } else if(gear instanceof EcobeeCycleGear) {
        getEcobeeCycleGearPanel().setValue(gear);
    } else if(gear instanceof SepCycleGear) {
        getSepCycleGearPanel().setValue(gear);
    } else if(gear instanceof SepTemperatureOffsetGear) {
        getSepTemperatureOffsetGearPanel().setValue(gear);
    } else if(gear instanceof MasterCycleGear) {
        getIvjMasterGearPanel1().setValue(gear);    
    } else if(gear instanceof TimeRefreshGear) {
        getIvjTimeGearPanel1().setValue(gear);
    } else if(gear instanceof RotationGear) {
        getIvjRotationGearPanel1().setValue(gear);
    } else if(gear instanceof LatchingGear) {
        getIvjLatchingGearPanel1().setValue(gear);
    } else if(gear instanceof ThermostatSetbackGear) {
        getIvjThermoSetbackGearPanel1().setValue(gear); 
    } else if (gear instanceof SimpleThermostatRampingGear) {
        getIvjSimpleThermoSetbackGearPanel1().setValue(gear);
    } else if(gear instanceof BeatThePeakGear) {
        getIvjBeatThePeakGearPanel1().setValue(gear);
    } else if(gear instanceof NoControlGear) {
        getNoControlGearPanel().setValue(gear); 
    }  else if(gear instanceof HoneywellCycleGear) {
        getHoneywellCycleGearPanel().setValue(gear); 
    }  else if(gear instanceof ItronCycleGear) {
        getItronCycleGearPanel().setValue(gear);
    } else if(gear instanceof NestCriticalCycleGear) {
        getNestCriticalCycleGearPanel().setValue(gear); 
    } else if(gear instanceof NestStandardCycleGear) {
        getNestStandardCycleGearPanel().setValue(gear); 
    } else {
        return;
    }
    
}


/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
@Override
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
    fireInputUpdate();
}


/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
@Override
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
    /**
     * Returns the ivjLatchingGearPanel1.
     * @return LatchingGearPanel
     */
    public LatchingGearPanel getIvjLatchingGearPanel1() {
        if(ivjLatchingGearPanel1 == null) {
            ivjLatchingGearPanel1 = new LatchingGearPanel();
        }
        return ivjLatchingGearPanel1;
    }

    /**
     * Returns the ivjMasterGearPanel1.
     * @return MasterCycleGearPanel
     */
    public MasterCycleGearPanel getIvjMasterGearPanel1() {
        if(ivjMasterGearPanel1 == null) {
            ivjMasterGearPanel1 = new MasterCycleGearPanel();
        }
        return ivjMasterGearPanel1;
    }

    /**
     * Returns the ivjRotationGearPanel1.
     * @return RotationGearPanel
     */
    public RotationGearPanel getIvjRotationGearPanel1() {
        if(ivjRotationGearPanel1 == null) {
            ivjRotationGearPanel1 = new RotationGearPanel();
        }
        return ivjRotationGearPanel1;
    }

    /**
     * Returns the ivjSmartGearPanel1.
     * @return SmartCycleGearPanel
     */
    public SmartCycleGearPanel getIvjSmartGearPanel1() {
        if(ivjSmartGearPanel1 == null) {
            ivjSmartGearPanel1 = new SmartCycleGearPanel();
        }
        return ivjSmartGearPanel1;
    }

    public EcobeeCycleGearPanel getEcobeeCycleGearPanel() {
        if (ecobeeCycleGearPanel == null) {
            ecobeeCycleGearPanel = new EcobeeCycleGearPanel();
        }
        return ecobeeCycleGearPanel;
    }
    
    public HoneywellCycleGearPanel getHoneywellCycleGearPanel() {
        if (honeywellCycleGearPanel == null) {
            honeywellCycleGearPanel = new HoneywellCycleGearPanel();
        }
        return honeywellCycleGearPanel;
    }
    
    public ItronCycleGearPanel getItronCycleGearPanel() {
        if (itronCycleGearPanel == null) {
            itronCycleGearPanel = new ItronCycleGearPanel();
        }
        return itronCycleGearPanel;
    }
    
    public NestCriticalCycleGearPanel getNestCriticalCycleGearPanel() {
        if (nestCriticalCycleGearPanel == null) {
            nestCriticalCycleGearPanel = new NestCriticalCycleGearPanel();
        }
        return nestCriticalCycleGearPanel;
    }
    
    public NestStandardCycleGearPanel getNestStandardCycleGearPanel() {
        if (nestStandardCycleGearPanel == null) {
            nestStandardCycleGearPanel = new NestStandardCycleGearPanel();
        }
        return nestStandardCycleGearPanel;
    }
    
    public SepCycleGearPanel getSepCycleGearPanel() {
        if(sepCycleGearPanel == null) {
            sepCycleGearPanel = new SepCycleGearPanel();
        }
        return sepCycleGearPanel;
    }
    
    public SepTemperatureOffsetGearPanel getSepTemperatureOffsetGearPanel() {
        if(sepTemperatureOffsetGearPanel == null) {
            sepTemperatureOffsetGearPanel = new SepTemperatureOffsetGearPanel();
        }
        return sepTemperatureOffsetGearPanel;
    }

    /**
     * Returns the ivjThermoSetbackGearPanel1.
     * @return ThermostatSetbackGearPanel
     */
    public ThermostatSetbackGearPanel getIvjThermoSetbackGearPanel1() {
        if(ivjThermoSetbackGearPanel1 == null) {
            ivjThermoSetbackGearPanel1 = new ThermostatSetbackGearPanel();
        }
        return ivjThermoSetbackGearPanel1;
    }
    
    public SimpleThermostatSetbackGearPanel getIvjSimpleThermoSetbackGearPanel1() {
        if(ivjSimpleThermoSetbackGearPanel1 == null) {
            ivjSimpleThermoSetbackGearPanel1 = new SimpleThermostatSetbackGearPanel();
        }
        return ivjSimpleThermoSetbackGearPanel1;
    }

    public BeatThePeakGearPanel getIvjBeatThePeakGearPanel1() {
        if(ivjBeatThePeakGearPanel1 == null) {
            ivjBeatThePeakGearPanel1 = new BeatThePeakGearPanel();
        }
        return ivjBeatThePeakGearPanel1;
    }
    
    public NoControlGearPanel getNoControlGearPanel() {
        if(ivjNoControlGearPanel1 == null) {
            ivjNoControlGearPanel1 = new NoControlGearPanel();
        }
        return ivjNoControlGearPanel1;
    }
    /**
     * Returns the ivjTimeGearPanel1.
     * @return TimeRefreshGearPanel
     */
    public TimeRefreshGearPanel getIvjTimeGearPanel1() {
        if(ivjTimeGearPanel1 == null) {
            ivjTimeGearPanel1 = new TimeRefreshGearPanel();
        }
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
    
    public void setIvjBeatThePeakGearPanel1(BeatThePeakGearPanel ivjBeatThePeakGearPanel1) {
        this.ivjBeatThePeakGearPanel1 = ivjBeatThePeakGearPanel1;
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
    
    public void setEcobeeCycleGearPanel(EcobeeCycleGearPanel ecobeeCycleGearPanel) {
        this.ecobeeCycleGearPanel = ecobeeCycleGearPanel;
    }
    
    public void setSepCycleGearPanel(SepCycleGearPanel sepCycleGearPanel) {
        this.sepCycleGearPanel = sepCycleGearPanel;
    }

    public void setSepThermostatGearPanel(SepTemperatureOffsetGearPanel sepTemperatureOffsetGearPanel) {
        this.sepTemperatureOffsetGearPanel = sepTemperatureOffsetGearPanel;
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
    @Override
    public void inputUpdate(PropertyPanelEvent event) {
        fireInputUpdate();      
    }

}