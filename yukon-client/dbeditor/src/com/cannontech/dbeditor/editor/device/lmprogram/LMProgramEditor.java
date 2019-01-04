package com.cannontech.dbeditor.editor.device.lmprogram;

import java.util.Vector;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramBasePanel;
import com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramControlWindowPanel;
import com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectMemberControlPanel;
import com.cannontech.user.UserUtils;
/**
 * This type was created in VisualAge.
 */
public class LMProgramEditor extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor, java.awt.event.ActionListener
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;
	
	private LMProgramBasePanel basePanel;
	private LMProgramControlWindowPanel controlWindowPanel;

	private PaoType programType;
	
	private static final PaoType[][] EDITOR_TYPES =
	{
		//LMProgramBasePanel
		{ PaoType.LM_CURTAIL_PROGRAM, 
			PaoType.LM_DIRECT_PROGRAM,
			PaoType.LM_SEP_PROGRAM,
			PaoType.LM_ECOBEE_PROGRAM,
			PaoType.LM_HONEYWELL_PROGRAM,
            PaoType.LM_ITRON_PROGRAM,
            PaoType.LM_NEST_PROGRAM,
			PaoType.LM_ENERGY_EXCHANGE_PROGRAM },

		//LMProgramCurtailmentPanel
		{ PaoType.LM_CURTAIL_PROGRAM },
		//LMProgramCurtailListPanel
		{ PaoType.LM_CURTAIL_PROGRAM },

		//LMProgramDirectPanel
        { PaoType.LM_DIRECT_PROGRAM, PaoType.LM_SEP_PROGRAM, PaoType.LM_ECOBEE_PROGRAM,
                PaoType.LM_HONEYWELL_PROGRAM, PaoType.LM_NEST_PROGRAM, PaoType.LM_ITRON_PROGRAM },
		//LMProgramControlWindowPanel
        { PaoType.LM_DIRECT_PROGRAM, PaoType.LM_SEP_PROGRAM, PaoType.LM_ECOBEE_PROGRAM,
                PaoType.LM_HONEYWELL_PROGRAM, PaoType.LM_NEST_PROGRAM, PaoType.LM_ITRON_PROGRAM },
		//LMProgramListPanel
        { PaoType.LM_DIRECT_PROGRAM, PaoType.LM_SEP_PROGRAM, PaoType.LM_ECOBEE_PROGRAM,
                PaoType.LM_HONEYWELL_PROGRAM, PaoType.LM_NEST_PROGRAM, PaoType.LM_ITRON_PROGRAM },
		//LMProgramDirectCustomerListPanel
        { PaoType.LM_DIRECT_PROGRAM, PaoType.LM_SEP_PROGRAM, PaoType.LM_ECOBEE_PROGRAM,
                PaoType.LM_HONEYWELL_PROGRAM, PaoType.LM_NEST_PROGRAM, PaoType.LM_ITRON_PROGRAM },
		//LMProgramDirectMemberControlPanel
        { PaoType.LM_DIRECT_PROGRAM, PaoType.LM_SEP_PROGRAM, PaoType.LM_ECOBEE_PROGRAM,
                PaoType.LM_HONEYWELL_PROGRAM, PaoType.LM_NEST_PROGRAM, PaoType.LM_ITRON_PROGRAM },
		
		//LMProgramEnergyExchangePanel
		{ PaoType.LM_ENERGY_EXCHANGE_PROGRAM},
		//LMProgramEnergyExchangePanel
		{ PaoType.LM_ENERGY_EXCHANGE_PROGRAM},
	};
	
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTabbedPane ivjStateEditorTabbedPane = null;
	
	class IvjEventHandler implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == basePanel.getActionPasser())
			{
				if(basePanel.isTimedOperationalState()) {
                    controlWindowPanel.getWindowChangePasser().doClick();
                } else
				{
					controlWindowPanel.getWindowChangePasser().setSelected(true);
					controlWindowPanel.getWindowChangePasser().doClick();
				}
			}
		};
	}
	
public LMProgramEditor() {
	super();
	initialize();
}

public void setProgramType(PaoType programType) {
	this.programType = programType;
}

public PaoType getProgramType() {
	return programType;
}

/**
 * Insert the method's description here.
 * Creation date: (3/15/2002 1:17:24 PM)
 * @return Object[]
 * 
 *  This method should return an object array with 2 elements,
 *   Object[0] is a DataInputPanel
 *   Object[1] is a String (Tab Name)
 */
@Override
public Object[] createNewPanel(int panelIndex)
{
	Object[] objs = new Object[2];

	switch( panelIndex )
	{
		case 0: 
			basePanel = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramBasePanel(getProgramType());
			objs[0] = basePanel;
			objs[1] = "General";
			break;

		case 1:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramCurtailmentPanel();
			objs[1] = "Curtailment";
			break;

		case 2:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramCurtailListPanel();
			objs[1] = "Curtail Customers";
			break;

		case 3:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectPanel(getProgramType());
			objs[1] = "Gears";
			break;

		case 4:
			controlWindowPanel = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramControlWindowPanel();
			objs[0] = controlWindowPanel;
			objs[1] = "Control Window";
			break;
			
		case 5:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramListPanel();
			objs[1] = "Groups";
			break;
			
		case 6:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectNotifGroupListPanel();
			objs[1] = "Notification";
			break;
			
		case 7:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectMemberControlPanel();
			objs[1] = "Member Control";
			break;
			
		case 8:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramEnergyExchangePanel();
			objs[1] = "Energy Exchange";
			break;

		case 9:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramEnergyExchangeCustomerListPanel();
			objs[1] = "Exchange Customers";
			break;
	}
		
	return objs;
}
/**
 * This method was created in VisualAge.
 * @return DataInputPanel[]
 */
@Override
public DataInputPanel[] getInputPanels() {
	//At least guarantee a non-null array if not a meaningful one
	if( this.inputPanels == null ) {
        this.inputPanels = new DataInputPanel[0];
    }
		
	return this.inputPanels;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
@Override
public java.awt.Dimension getPreferredSize() {
	return new java.awt.Dimension( 400, 450 );
}
/**
 * Return the RouteEditorTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getStateEditorTabbedPane() {
	if (ivjStateEditorTabbedPane == null) {
		try {
			ivjStateEditorTabbedPane = new javax.swing.JTabbedPane();
			ivjStateEditorTabbedPane.setName("StateEditorTabbedPane");
			ivjStateEditorTabbedPane.setPreferredSize(new java.awt.Dimension(400, 350));
			ivjStateEditorTabbedPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateEditorTabbedPane.setBounds(0, 0, 400, 350);
			ivjStateEditorTabbedPane.setMaximumSize(new java.awt.Dimension(400, 350));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateEditorTabbedPane;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
@Override
public String[] getTabNames() {
	if( this.inputPanelTabNames == null ) {
        this.inputPanelTabNames = new String[0];
    }
		
	return this.inputPanelTabNames;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramEditorPanel");
		setPreferredSize(new java.awt.Dimension(400, 350));
		setLayout(null);
		setSize(400, 350);
		setMaximumSize(new java.awt.Dimension(400, 350));
		add(getStateEditorTabbedPane(), getStateEditorTabbedPane().getName());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 2:49:24 PM)
 * @return boolean
 *
 * We must override isInputValid() because this is a special case since
 * there are 2 panels that, based on state, can make each other invalid!
 * These panels are:
 *		com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectPanel()
 *		com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramListPanel()
 *  
 */
@Override
public boolean isInputValid() 
{
	//do what is necessary for Timed operational state
	//checkTimedOpStatus();
	
	boolean retVal = super.isInputValid();
	
	boolean isLatching = false;
	boolean hasLMGroupPoint = false;
	String errTitle = null;

	//be sure we are Ok up to this point
	if( retVal )
	{
		for( int i = 0; i < getInputPanels().length; i++ )
		{
			if( getInputPanels()[i] instanceof com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectPanel )
			{
				isLatching = ((com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectPanel)
					getInputPanels()[i]).hasLatchingGear();
			}
			else if( getInputPanels()[i] instanceof com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramListPanel )
			{
				hasLMGroupPoint = ((com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramListPanel)
					getInputPanels()[i]).hasLMGroupPoint();
					
				errTitle = getTabNames()[i];
			}
			
		}

		if( !isLatching && hasLMGroupPoint )
		{
			setErrorString("The '" + errTitle + "' panel had the following error(s): \n   -> " +
				"LMGroupPoint groups are only allowed if a latching gear is present,\n   -> " +
				"Remove the group from the assigned group list or change the gear type to " + 
				GearControlMethod.Latching);

			retVal = false;
		}		
	}

	return retVal;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val) 
{	
	//Vector to hold the panels temporarily
	Vector<DataInputPanel> panels = new Vector<>(EDITOR_TYPES.length);
	Vector<Object> tabs = new Vector<>(EDITOR_TYPES.length);
	
	DataInputPanel tempPanel;
	PaoType paoType = ((LMProgramBase)val).getPaoType();
	setProgramType(paoType);
	
 	for( int i = 0; i < EDITOR_TYPES.length; i++ )
 	{
	 	for( int j = 0; j < EDITOR_TYPES[i].length; j++ )
	 	{
		 	if( paoType == EDITOR_TYPES[i][j] )
			{
				Object[] panelTabs = createNewPanel(i);
				tempPanel = (DataInputPanel)panelTabs[0];
				
				//make sure that this user is allowed to have a member control tab
				boolean allowMemCntrl = false;
				try {
				    allowMemCntrl = Boolean.parseBoolean(
				                               ClientSession.getInstance().getRolePropertyValue(
				                                                               YukonRoleProperty.ALLOW_MEMBER_PROGRAMS).trim());
				}
				catch (Exception e)
				{/*Leave allowMemCntrl false*/}
				
				if((!allowMemCntrl && ClientSession.getInstance().getUser().getUserID() != UserUtils.USER_ADMIN_ID) && 
				        tempPanel instanceof LMProgramDirectMemberControlPanel)
				{
					i++;
				}
				else
				{
					panels.addElement( tempPanel );
					tabs.addElement( panelTabs[1] );
					break;
				}				
			}
	 	}
 	}
	
	this.inputPanels = new DataInputPanel[panels.size()];
	panels.copyInto( this.inputPanels );

	this.inputPanelTabNames = new String[tabs.size()];
	tabs.copyInto( this.inputPanelTabNames );
	
	//Allow super to do whatever it needs to
	super.setValue( val );
	
	//check for special Timed Operational State case but only if it is a direct program
	if(controlWindowPanel != null)
	{
		controlWindowPanel.setTimedOperationalStateCondition(basePanel.isTimedOperationalState());
		controlWindowPanel.getWindowChangePasser().setSelected(basePanel.isTimedOperationalState());
		basePanel.getActionPasser().addActionListener(ivjEventHandler);
	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
@Override
public String toString() {
	return "LMProgram Editor";
}
}
