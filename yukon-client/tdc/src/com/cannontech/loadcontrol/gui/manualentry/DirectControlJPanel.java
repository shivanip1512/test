package com.cannontech.loadcontrol.gui.manualentry;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.panel.IMultiSelectModel;
import com.cannontech.common.gui.table.MultiJComboCellEditor;
import com.cannontech.common.gui.table.MultiJComboCellRenderer;
import com.cannontech.common.gui.util.ComboBoxTableRenderer;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.LMDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.popup.ControlAreaPopUpMenu;
import com.cannontech.spring.YukonSpringHook;

/**
 * Insert the type's description here.
 * Creation date: (3/12/2001 9:57:47 AM)
 * @author: 
 */
public class DirectControlJPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("MMMMMMMM dd, yyyy");
	//modes the panel is in
	public static final int MODE_START_STOP = 0;
	public static final int MODE_STOP = 1;
	public static final int MODE_MULTI_SELECT_ONLY = 2;
	//choices the user may choose
	public static final int CANCEL_CHOICE = 0;
	public static final int OK_CHOICE = 1;
	private int choice = CANCEL_CHOICE;
	private int mode = MODE_START_STOP;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JLabel ivjJLabelStartTime = null;
	private javax.swing.JLabel ivjJLabelStopTime = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStartTime = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStopTime = null;
	private javax.swing.JCheckBox ivjJCheckBoxNeverStop = null;
	private javax.swing.JLabel ivjJLabelLabelStartHRMN = null;
	private javax.swing.JLabel ivjJLabelLabelStopHRMN = null;
	private javax.swing.JPanel ivjJPanelOkCancel = null;
	private javax.swing.JCheckBox ivjJCheckBoxStartStopNow = null;
	private javax.swing.JComboBox ivjJComboBoxGear = null;
    private javax.swing.JComboBox jComboBoxStopGear = null;
    private javax.swing.JLabel jLabelStopGear = null;
	private javax.swing.JLabel ivjJLabelGear = null;
	private com.cannontech.common.gui.panel.MultiSelectJPanel ivjJPanelMultiSelect = null;
	private javax.swing.JPanel ivjJPanelControls = null;
	private com.cannontech.common.gui.util.DateComboBox ivjDateComboStart = null;
	private com.cannontech.common.gui.util.DateComboBox ivjDateComboStop = null;
	private IMultiSelectModel multiSelectPrgModel = null;
	private javax.swing.JComboBox ivjJComboBoxScenario = null;
	private javax.swing.JLabel ivjJLabelScenario = null;
	//attributes used when a scenarios are present
	private boolean _isScenario = false;
	//contains <Integer(ProgID), LMProgramBase>
	private Map allPrograms = null;
	private javax.swing.JComboBox ivjJComboBoxConstraints = null;
	private javax.swing.JLabel ivjJLabelGear1 = null;
 
	/*Target Cycle Adjust*/
    private JTextField[] ivjJTargetAdjustmentInput = null;  
	private JLabel[] ivjJTargetAdjustmentLabel = null;
    private javax.swing.JCheckBox ivjJCheckBoxTargetAdgust = null;

    private JButton targetAdjustButton = null;
    private TargetCycleConfigPanel gearConfigJPanel = null;
    private boolean isMulti = false;

    boolean canSpecifyStopGear = false;
    /**
	 * ManualChangeJPanel constructor comment.
	 */
	public DirectControlJPanel() {
		super();
		initialize();
    }
	/**
	 * ManualChangeJPanel constructor comment.
	 */
	public DirectControlJPanel( Map allProgs )
	{
		this( true, allProgs );
	}


	/**
	 * ManualChangeJPanel constructor comment.
	 */
	private DirectControlJPanel( boolean showScenario, Map allProgs ) 
	{
		super();
		_isScenario = showScenario;
		allPrograms = allProgs;		
		initialize();
	}


	private void action_Scenario( java.awt.event.ActionEvent event )
	{
		if( allPrograms == null || getJComboBoxScenario().getSelectedItem() == null)
			return;

		LiteYukonPAObject litePAO =
			(LiteYukonPAObject)getJComboBoxScenario().getSelectedItem();

		if( litePAO != null )
		{
			LiteLMProgScenario[] programs = 
					YukonSpringHook.getBean(LMDao.class).getLMScenarioProgs( litePAO.getYukonID() );

			ArrayList selPrgs = new ArrayList( programs.length );

			for( int i = 0; i < programs.length; i++ )
			{
				LiteLMProgScenario p = programs[i];
				
				LMProgramBase lmProg = 
					(LMProgramBase)allPrograms.get( new Integer(p.getProgramID()) );
					
				if( lmProg != null )
				{
					MultiSelectProg selProg = new MultiSelectProg( lmProg );
					selProg.setGearNum( new Integer(p.getStartGear()) );
					selProg.setStartDelay( new Integer(p.getStartOffset()) );
					selProg.setStopOffset( new Integer(p.getStopOffset()) );

					selPrgs.add( selProg );
				}
				else
				{
					CTILogger.error( 
						" *** A Control Scenario contains a LMProgram that does not belong to a Control Area, programID = " +
						p.getProgramID() );
				}
				
				
			}

			
			MultiSelectProg[] progArray = new MultiSelectProg[ selPrgs.size() ];
            setMultiSelectObject( (MultiSelectProg[])selPrgs.toArray(progArray), true );
		}


	}


	/**
	 * Method to handle events for the ActionListener interface.
	 * @param e java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		
		if( e.getSource() == getJComboBoxGear() ) 
			jComboBoxGear_ActionPerformed( e );
		
		if (e.getSource() == getJButtonCancel()) 
			connEtoC1(e);
		if (e.getSource() == getJButtonOk()) 
			connEtoC2(e);
		if (e.getSource() == getJCheckBoxNeverStop()) 
			connEtoC3(e);
		if (e.getSource() == getJCheckBoxStartStopNow()) 
			connEtoC4(e);
		
		if( e.getSource() == getJComboBoxScenario() ) 
			action_Scenario( e );
        if (e.getSource() == getTargetAdjustButton())
            launchTargetAdjConfig();
		
    }

/**
 * method to generate configs for gear
 *
 */
    private void launchTargetAdjConfig() {
        final JDialog d = new JDialog(SwingUtil.getParentFrame(this));
        Date start  = getStartTime();
        Date stop = getStopTime();

        gearConfigJPanel = new TargetCycleConfigPanel(start, stop) {   
            public void exit() {
                d.dispose();
            }
        };
        d.setModal(true);
        d.setTitle ("Target Cycle Gear Configuration");
        d.setContentPane(gearConfigJPanel);
        d.setSize( 350, (gearConfigJPanel.getTimeSlots() * 30) + 85 );
        d.setLocationRelativeTo(this);
        d.setLocation(d.getLocation().x, d.getLocation().y + 150);
        d.setVisible(true);

        d.dispose();
    }

    /**
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1(java.awt.event.ActionEvent arg1) {
		try {
			
			this.jButtonCancel_ActionPerformed(arg1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	/**
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC2(java.awt.event.ActionEvent arg1) {
		try {
			this.jButtonOK_ActionPerformed(arg1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	/**
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC3(java.awt.event.ActionEvent arg1) {
		try {
			
			this.jCheckBoxNeverStop_ActionPerformed(arg1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoC4:  (JCheckBoxStartStopNow.action.actionPerformed(java.awt.event.ActionEvent) --> DirectControlJPanel.jCheckBoxStartStopNow_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC4(java.awt.event.ActionEvent arg1) {
		try {
			
			this.jCheckBoxStartStopNow_ActionPerformed(arg1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	/**
	 * A method to create a LMManualControlRequest with some set values. 
	 * Creation date: (5/14/2002 10:50:02 AM)
	 * @param
	 */
	public synchronized LMManualControlRequest createMessage( LMProgramBase program, Integer gearNum ) 
	{
		int constID =
			LMManualControlRequest.getConstraintID( getJComboBoxConstraints().getSelectedItem().toString() );

		if (getMode() == MODE_STOP) 
        {
            LMManualControlRequest cmd = LCUtils.createProgMessage(
                                            isStopStartNowSelected(),
                                            getMode() == MODE_STOP,
                                            getStartTime(), getStopTime(),
                                            program, gearNum, constID, ProgramOriginSource.MANUAL);
            
            if(canSpecifyStopGear && !getJCheckBoxStartStopNow().isSelected()) {
                cmd.setStartGear(((LMProgramDirectGear)getSelectedStopGear()).getGearNumber());
                cmd.setCommand(LMManualControlRequest.CHANGE_GEAR);
            }
            
            return cmd;
        }
        else
        {
            //special case when we create a start message dro direct program, with
            //target cycle gear
            if (program instanceof LMProgramDirect) {
                List<LMProgramDirectGear> gears = ((LMProgramDirect)program).getDirectGearVector();
                for (Iterator iter = gears.iterator(); iter.hasNext();) {
                    LMProgramDirectGear lmProgramDirectGear = (LMProgramDirectGear) iter.next();
                    if (isTargetCycleGear(lmProgramDirectGear)) {
                        String additionalInfo = null;
                        if (getGearConfigJPanel() != null)
                        {
                            additionalInfo = getGearConfigJPanel().getAdditionalInfo();
                        }
                        return LCUtils.createStartMessage(isStopStartNowSelected(),
                                                          getStartTime(), getStopTime(),
                                                          program, gearNum, constID, 
                                                          additionalInfo, ProgramOriginSource.MANUAL );
                    }
                }
            }
        }
            
        return LCUtils.createStartMessage(
                                       isStopStartNowSelected(),
                                     getStartTime(), getStopTime(),
                                       program, gearNum, constID, null, ProgramOriginSource.MANUAL );
            
       
    
    
    }


	/**
	 * Create start/stop messages for programs inside a Scenario
	 *
	 * @param
	 */
	public synchronized LMManualControlRequest createScenarioMessage( MultiSelectProg program ) 
	{
		int constID =
			LMManualControlRequest.getConstraintID( getJComboBoxConstraints().getSelectedItem().toString() );
		
		boolean doItNow = false;
		if( getMode() == MODE_STOP )
			doItNow = isStopStartNowSelected() && (program.getStopOffset().intValue() <= 0); 
		else
			doItNow = isStopStartNowSelected() && (program.getStartDelay().intValue() <= 0); 

		return LCUtils.createScenarioMessage(
			program.getBaseProgram(),
			getMode() == MODE_STOP,
			doItNow,
			program.getStartDelay().intValue(),
			program.getStopOffset().intValue(),
			program.getGearNum().intValue(),
			getStartTime(), getStopTime(),
			constID );
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (3/12/2001 3:40:34 PM)
	 *
	 * Method to override if desired 
	 */
	public void exit() {}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/12/2001 3:43:40 PM)
	 * @return int
	 */
	public int getChoice() {
		return choice;
	}


	/**
	 * Return the DateComboStart property value.
	 * @return com.cannontech.common.gui.util.DateComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.util.DateComboBox getDateComboStart() {
		if (ivjDateComboStart == null) {
			try {
				ivjDateComboStart = new com.cannontech.common.gui.util.DateComboBox();
				ivjDateComboStart.setName("DateComboStart");
				ivjDateComboStart.setEnabled(false);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjDateComboStart;
	}


	/**
	 * Return the DateComboStop property value.
	 * @return com.cannontech.common.gui.util.DateComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.util.DateComboBox getDateComboStop() {
		if (ivjDateComboStop == null) {
			try {
				ivjDateComboStop = new com.cannontech.common.gui.util.DateComboBox();
				ivjDateComboStop.setName("DateComboStop");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjDateComboStop;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (7/10/2001 10:48:08 AM)
	 * @return java.text.SimpleDateFormat
	 */
	public java.text.SimpleDateFormat getDateFormatter() {
		return dateFormatter;
	}

	/**
	 * Return the JButtonCancel property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JButton getJButtonCancel() {
		if (ivjJButtonCancel == null) {
			try {
				ivjJButtonCancel = new javax.swing.JButton();
				ivjJButtonCancel.setName("JButtonCancel");
				ivjJButtonCancel.setMnemonic(67);
				ivjJButtonCancel.setText("Cancel");
				ivjJButtonCancel.setMaximumSize(new java.awt.Dimension(73, 25));
				ivjJButtonCancel.setActionCommand("Cancel");
				ivjJButtonCancel.setMinimumSize(new java.awt.Dimension(73, 25));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJButtonCancel;
	}


	/**
	 * Return the JButtonOk property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JButton getJButtonOk() {
		if (ivjJButtonOk == null) {
			try {
				ivjJButtonOk = new javax.swing.JButton();
				ivjJButtonOk.setName("JButtonOk");
				ivjJButtonOk.setMnemonic(79);
				ivjJButtonOk.setMaximumSize(new java.awt.Dimension(73, 25));
				ivjJButtonOk.setPreferredSize(new java.awt.Dimension(73, 25));
				ivjJButtonOk.setMinimumSize(new java.awt.Dimension(73, 25));
				ivjJButtonOk.setMargin(new java.awt.Insets(2, 14, 2, 14));
				ivjJButtonOk.setText("Submit");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJButtonOk;
	}


	/**
	 * Return the JCheckBoxNeverStop property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JCheckBox getJCheckBoxNeverStop() {
		if (ivjJCheckBoxNeverStop == null) {
			try {
				ivjJCheckBoxNeverStop = new javax.swing.JCheckBox();
				ivjJCheckBoxNeverStop.setName("JCheckBoxNeverStop");
				ivjJCheckBoxNeverStop.setToolTipText("Forces the schedule to run forever");
				ivjJCheckBoxNeverStop.setMnemonic(78);
				ivjJCheckBoxNeverStop.setText("Never Stop");
				ivjJCheckBoxNeverStop.setMaximumSize(new java.awt.Dimension(87, 22));
				ivjJCheckBoxNeverStop.setActionCommand("Never Stop");
				ivjJCheckBoxNeverStop.setMinimumSize(new java.awt.Dimension(87, 22));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJCheckBoxNeverStop;
	}

    /**
     * Return the JCheckBoxNeverStop property value.
     * @return javax.swing.JCheckBox
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getJCheckBoxTargetAdgust() {
        if (ivjJCheckBoxTargetAdgust == null) {
            try {
                ivjJCheckBoxTargetAdgust = new javax.swing.JCheckBox();
                ivjJCheckBoxTargetAdgust.setName("JCheckBoxTargetAdjustEn");
                ivjJCheckBoxTargetAdgust.setToolTipText("Forces the schedule to run forever");
                ivjJCheckBoxTargetAdgust.setMnemonic(78);
                ivjJCheckBoxTargetAdgust.setText("Target Adjust");
                ivjJCheckBoxTargetAdgust.setMaximumSize(new java.awt.Dimension(187, 22));
                ivjJCheckBoxTargetAdgust.setActionCommand("Target Adjust");
                ivjJCheckBoxTargetAdgust.setMinimumSize(new java.awt.Dimension(187, 22));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxTargetAdgust;
    }

    private JTextField[] getJTextFieldArrayTargetAdgust() {
        if (ivjJTargetAdjustmentInput == null) {
            try {
                ivjJTargetAdjustmentInput = new JTextField[5];
                for (int i = 0; i < ivjJTargetAdjustmentInput.length; i++) {
                    JTextField adjustment = new JTextField();
                    adjustment.setName("JCheckBoxTargetAdjust_" + i);
                    adjustment.setToolTipText("Forces the schedule to run forever");
                    adjustment.setMaximumSize(new java.awt.Dimension(50, 22));
                    adjustment.setActionCommand("Target Adjust");
                    adjustment.setMinimumSize(new java.awt.Dimension(50, 22));
                    ivjJTargetAdjustmentInput [i] = adjustment;
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTargetAdjustmentInput;
    }

    private JLabel[] getJLabelArrayTargetAdgust() {
        if (    ivjJTargetAdjustmentLabel == null) {
            try {
                ivjJTargetAdjustmentLabel = new JLabel[5];
                for (int i = 0; i <    ivjJTargetAdjustmentLabel.length; i++) {
                    JLabel adjustment = new JLabel();
                    adjustment.setName("JCheckBoxTargetAdjust_" + i);
                    adjustment.setToolTipText("Forces the schedule to run forever");
                    adjustment.setMaximumSize(new java.awt.Dimension(100, 22));
                    adjustment.setText("Adjustmet " + i);
                    adjustment.setMinimumSize(new java.awt.Dimension(100, 22));
                    ivjJTargetAdjustmentLabel [i] = adjustment;
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTargetAdjustmentLabel;
    }
    
	/**
	 * Return the JCheckBoxStartStopNow property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JCheckBox getJCheckBoxStartStopNow() {
		if (ivjJCheckBoxStartStopNow == null) {
			try {
				ivjJCheckBoxStartStopNow = new javax.swing.JCheckBox();
				ivjJCheckBoxStartStopNow.setName("JCheckBoxStartStopNow");
				ivjJCheckBoxStartStopNow.setMnemonic(83);
				ivjJCheckBoxStartStopNow.setText("Start Now");
				ivjJCheckBoxStartStopNow.setMaximumSize(new java.awt.Dimension(81, 22));
				ivjJCheckBoxStartStopNow.setActionCommand("Start Now");
				ivjJCheckBoxStartStopNow.setMinimumSize(new java.awt.Dimension(81, 22));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJCheckBoxStartStopNow;
	}


/**
 * Return the JComboBoxConstraints property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JComboBox getJComboBoxConstraints() {
	if (ivjJComboBoxConstraints == null) {
		try {
			ivjJComboBoxConstraints = new javax.swing.JComboBox();
			ivjJComboBoxConstraints.setName("JComboBoxConstraints");
			ivjJComboBoxConstraints.setEditor(new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource());
			ivjJComboBoxConstraints.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource());
			RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);

			if( rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS,
			                        ClientSession.getInstance().getUser()))
				ivjJComboBoxConstraints.addItem( 
					LMManualControlRequest.CONSTRAINT_FLAG_STRS[LMManualControlRequest.CONSTRAINTS_FLAG_USE] );

			if( rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS,
			                                    ClientSession.getInstance().getUser()))
				ivjJComboBoxConstraints.addItem(
					LMManualControlRequest.CONSTRAINT_FLAG_STRS[LMManualControlRequest.CONSTRAINTS_FLAG_CHECK] );

			if( ivjJComboBoxConstraints.getItemCount() > 0 ) {
				//set our initial selection to be the value specified in our
				// role property
				String defSel = 
					YukonSpringHook.getBean(RolePropertyDao.class).getPropertyStringValue(
					    YukonRoleProperty.DEFAULT_CONSTRAINT_SELECTION,
						ClientSession.getInstance().getUser());
	
				ivjJComboBoxConstraints.setSelectedItem( defSel );
			} else {
				//force our constraints to do something, just
				// observe them for now
				ivjJComboBoxConstraints.setEnabled( false );
				ivjJComboBoxConstraints.addItem( 
					LMManualControlRequest.CONSTRAINT_FLAG_STRS[LMManualControlRequest.CONSTRAINTS_FLAG_USE] );				
			}

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxConstraints;
}


	/**
	 * Return the JComboBoxGear property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JComboBox getJComboBoxGear() {
		if (ivjJComboBoxGear == null) {
			try {
				ivjJComboBoxGear = new javax.swing.JComboBox();
				ivjJComboBoxGear.setName("JComboBoxGear");
				ivjJComboBoxGear.setEditor(new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource());
				ivjJComboBoxGear.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource());
                
				ivjJComboBoxGear.setToolTipText( "The gear or gear number the program(s) should begin control with");
				
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJComboBoxGear;
	}


/**
 * Return the JComboBoxScenario property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getJComboBoxScenario() {
	if (ivjJComboBoxScenario == null) {
		try {
			ivjJComboBoxScenario = new javax.swing.JComboBox();
			ivjJComboBoxScenario.setName("JComboBoxScenario");
			ivjJComboBoxScenario.setEditor(new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource());
			ivjJComboBoxScenario.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource());
			
			if( _isScenario )
			{
				LiteYukonPAObject[] scenarios = YukonSpringHook.getBean(LMDao.class).getAllLMScenarios();
				for( int i = 0; i < scenarios.length; i++ )
				{
					ivjJComboBoxScenario.addItem( scenarios[i] );
				}				
			}
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxScenario;
}


	/**
	 * Return the JLabelGear property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelGear() {
		if (ivjJLabelGear == null) {
			try {
				ivjJLabelGear = new javax.swing.JLabel();
				ivjJLabelGear.setName("JLabelGear");
				ivjJLabelGear.setFont(new java.awt.Font("dialog", 0, 14));
				ivjJLabelGear.setText("Gear:");
				ivjJLabelGear.setMaximumSize(new java.awt.Dimension(36, 19));
				ivjJLabelGear.setMinimumSize(new java.awt.Dimension(36, 19));
	
				ivjJLabelGear.setToolTipText( "The gear or gear number the program(s) should begin control with");
	
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJLabelGear;
	}


/**
 * Return the JLabelGear1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGear1() {
	if (ivjJLabelGear1 == null) {
		try {
			ivjJLabelGear1 = new javax.swing.JLabel();
			ivjJLabelGear1.setName("JLabelGear1");
			ivjJLabelGear1.setToolTipText("How constraints on the program should be handled");
			ivjJLabelGear1.setText("Constraints:");
			ivjJLabelGear1.setMaximumSize(new java.awt.Dimension(36, 19));
			ivjJLabelGear1.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGear1.setMinimumSize(new java.awt.Dimension(36, 19));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelGear1;
}


	/**
	 * Return the JLabelLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelLabelStartHRMN() {
		if (ivjJLabelLabelStartHRMN == null) {
			try {
				ivjJLabelLabelStartHRMN = new javax.swing.JLabel();
				ivjJLabelLabelStartHRMN.setName("JLabelLabelStartHRMN");
				ivjJLabelLabelStartHRMN.setText("(HH:mm)");
				ivjJLabelLabelStartHRMN.setMaximumSize(new java.awt.Dimension(51, 16));
				ivjJLabelLabelStartHRMN.setFont(new java.awt.Font("dialog", 0, 12));
				ivjJLabelLabelStartHRMN.setEnabled(false);
				ivjJLabelLabelStartHRMN.setMinimumSize(new java.awt.Dimension(51, 16));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJLabelLabelStartHRMN;
	}


	/**
	 * Return the JLabelLabel1 property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelLabelStopHRMN() {
		if (ivjJLabelLabelStopHRMN == null) {
			try {
				ivjJLabelLabelStopHRMN = new javax.swing.JLabel();
				ivjJLabelLabelStopHRMN.setName("JLabelLabelStopHRMN");
				ivjJLabelLabelStopHRMN.setFont(new java.awt.Font("dialog", 0, 12));
				ivjJLabelLabelStopHRMN.setText("(HH:mm)");
				ivjJLabelLabelStopHRMN.setMaximumSize(new java.awt.Dimension(51, 16));
				ivjJLabelLabelStopHRMN.setMinimumSize(new java.awt.Dimension(51, 16));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJLabelLabelStopHRMN;
	}


/**
 * Return the JLabelScenario property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelScenario() {
	if (ivjJLabelScenario == null) {
		try {
			ivjJLabelScenario = new javax.swing.JLabel();
			ivjJLabelScenario.setName("JLabelScenario");
			ivjJLabelScenario.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelScenario.setText("Scenario:");
			ivjJLabelScenario.setMaximumSize(new java.awt.Dimension(68, 19));
			ivjJLabelScenario.setMinimumSize(new java.awt.Dimension(68, 19));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelScenario;
}


	/**
	 * Return the JLabelTime property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelStartTime() {
		if (ivjJLabelStartTime == null) {
			try {
				ivjJLabelStartTime = new javax.swing.JLabel();
				ivjJLabelStartTime.setName("JLabelStartTime");
				ivjJLabelStartTime.setText("Start Time:");
				ivjJLabelStartTime.setMaximumSize(new java.awt.Dimension(69, 19));
				ivjJLabelStartTime.setFont(new java.awt.Font("dialog", 0, 14));
				ivjJLabelStartTime.setEnabled(false);
				ivjJLabelStartTime.setMinimumSize(new java.awt.Dimension(69, 19));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJLabelStartTime;
	}


	/**
	 * Return the JLabelStopTime property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelStopTime() {
		if (ivjJLabelStopTime == null) {
			try {
				ivjJLabelStopTime = new javax.swing.JLabel();
				ivjJLabelStopTime.setName("JLabelStopTime");
				ivjJLabelStopTime.setFont(new java.awt.Font("dialog", 0, 14));
				ivjJLabelStopTime.setText("Stop Time:");
				ivjJLabelStopTime.setMaximumSize(new java.awt.Dimension(68, 19));
				ivjJLabelStopTime.setMinimumSize(new java.awt.Dimension(68, 19));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJLabelStopTime;
	}


	/**
	 * Return the JPanelControls property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanelControls() {
	if (ivjJPanelControls == null) {
		try {
			ivjJPanelControls = new javax.swing.JPanel();
			ivjJPanelControls.setName("JPanelControls");
			ivjJPanelControls.setPreferredSize(new java.awt.Dimension(285, 232));
			ivjJPanelControls.setLayout(new java.awt.GridBagLayout());
			ivjJPanelControls.setMaximumSize(new java.awt.Dimension(285, 232));

			java.awt.GridBagConstraints constraintsJCheckBoxStartStopNow = new java.awt.GridBagConstraints();
			constraintsJCheckBoxStartStopNow.gridx = 1; constraintsJCheckBoxStartStopNow.gridy = 2;
			constraintsJCheckBoxStartStopNow.gridwidth = 3;
			constraintsJCheckBoxStartStopNow.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxStartStopNow.ipadx = 9;
			constraintsJCheckBoxStartStopNow.insets = new java.awt.Insets(2, 5, 1, 2);
			getJPanelControls().add(getJCheckBoxStartStopNow(), constraintsJCheckBoxStartStopNow);

			java.awt.GridBagConstraints constraintsJLabelStartTime = new java.awt.GridBagConstraints();
			constraintsJLabelStartTime.gridx = 1; constraintsJLabelStartTime.gridy = 3;
			constraintsJLabelStartTime.gridwidth = 3;
			constraintsJLabelStartTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStartTime.ipadx = 17;
			constraintsJLabelStartTime.insets = new java.awt.Insets(2, 5, 4, 6);
			getJPanelControls().add(getJLabelStartTime(), constraintsJLabelStartTime);

			java.awt.GridBagConstraints constraintsJTextFieldStartTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldStartTime.gridx = 4; constraintsJTextFieldStartTime.gridy = 3;
			constraintsJTextFieldStartTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldStartTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldStartTime.weightx = 1.0;
			constraintsJTextFieldStartTime.ipadx = 117;
			constraintsJTextFieldStartTime.insets = new java.awt.Insets(2, 2, 3, 1);
			getJPanelControls().add(getJTextFieldStartTime(), constraintsJTextFieldStartTime);

			java.awt.GridBagConstraints constraintsJLabelLabelStartHRMN = new java.awt.GridBagConstraints();
			constraintsJLabelLabelStartHRMN.gridx = 5; constraintsJLabelLabelStartHRMN.gridy = 3;
			constraintsJLabelLabelStartHRMN.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelLabelStartHRMN.ipadx = 7;
			constraintsJLabelLabelStartHRMN.ipady = 3;
			constraintsJLabelLabelStartHRMN.insets = new java.awt.Insets(5, 2, 1, 9);
			getJPanelControls().add(getJLabelLabelStartHRMN(), constraintsJLabelLabelStartHRMN);

			java.awt.GridBagConstraints constraintsJCheckBoxNeverStop = new java.awt.GridBagConstraints();
			constraintsJCheckBoxNeverStop.gridx = 1; constraintsJCheckBoxNeverStop.gridy = 5;
			constraintsJCheckBoxNeverStop.gridwidth = 3;
			constraintsJCheckBoxNeverStop.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxNeverStop.ipadx = 3;
			constraintsJCheckBoxNeverStop.insets = new java.awt.Insets(3, 5, 0, 2);
			getJPanelControls().add(getJCheckBoxNeverStop(), constraintsJCheckBoxNeverStop);

			java.awt.GridBagConstraints constraintsJTextFieldStopTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldStopTime.gridx = 4; constraintsJTextFieldStopTime.gridy = 6;
			constraintsJTextFieldStopTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldStopTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldStopTime.weightx = 1.0;
			constraintsJTextFieldStopTime.ipadx = 117;
			constraintsJTextFieldStopTime.insets = new java.awt.Insets(0, 2, 3, 1);
			getJPanelControls().add(getJTextFieldStopTime(), constraintsJTextFieldStopTime);

			java.awt.GridBagConstraints constraintsJLabelStopTime = new java.awt.GridBagConstraints();
			constraintsJLabelStopTime.gridx = 1; constraintsJLabelStopTime.gridy = 6;
			constraintsJLabelStopTime.gridwidth = 3;
			constraintsJLabelStopTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStopTime.ipadx = 18;
			constraintsJLabelStopTime.insets = new java.awt.Insets(1, 5, 3, 6);
			getJPanelControls().add(getJLabelStopTime(), constraintsJLabelStopTime);

			java.awt.GridBagConstraints constraintsJLabelLabelStopHRMN = new java.awt.GridBagConstraints();
			constraintsJLabelLabelStopHRMN.gridx = 5; constraintsJLabelLabelStopHRMN.gridy = 6;
			constraintsJLabelLabelStopHRMN.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelLabelStopHRMN.ipadx = 7;
			constraintsJLabelLabelStopHRMN.ipady = 3;
			constraintsJLabelLabelStopHRMN.insets = new java.awt.Insets(3, 2, 1, 9);
			getJPanelControls().add(getJLabelLabelStopHRMN(), constraintsJLabelLabelStopHRMN);

			java.awt.GridBagConstraints constraintsJComboBoxGear = new java.awt.GridBagConstraints();
			constraintsJComboBoxGear.gridx = 2; constraintsJComboBoxGear.gridy = 8;
			constraintsJComboBoxGear.gridwidth = 4;
			constraintsJComboBoxGear.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxGear.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxGear.weightx = 1.0;
			constraintsJComboBoxGear.ipadx = 106;
			constraintsJComboBoxGear.insets = new java.awt.Insets(3, 2, 2, 5);
			getJPanelControls().add(getJComboBoxGear(), constraintsJComboBoxGear);

			java.awt.GridBagConstraints constraintsJLabelGear = new java.awt.GridBagConstraints();
			constraintsJLabelGear.gridx = 1; constraintsJLabelGear.gridy = 8;
			constraintsJLabelGear.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGear.ipadx = 9;
			constraintsJLabelGear.insets = new java.awt.Insets(5, 5, 4, 1);
			getJPanelControls().add(getJLabelGear(), constraintsJLabelGear);

            java.awt.GridBagConstraints constraintsJComboBoxStopGear = new java.awt.GridBagConstraints();
            constraintsJComboBoxStopGear.gridx = 2; constraintsJComboBoxStopGear.gridy = 9;
            constraintsJComboBoxStopGear.gridwidth = 4;
            constraintsJComboBoxStopGear.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxStopGear.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxStopGear.weightx = 1.0;
            constraintsJComboBoxStopGear.ipadx = 106;
            constraintsJComboBoxStopGear.insets = new java.awt.Insets(3, 2, 2, 5);
            getJPanelControls().add(getJComboBoxStopGear(), constraintsJComboBoxStopGear);

            java.awt.GridBagConstraints constraintsJLabelStopGear = new java.awt.GridBagConstraints();
            constraintsJLabelStopGear.gridx = 1; constraintsJLabelStopGear.gridy = 9;
            constraintsJLabelStopGear.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelStopGear.ipadx = 9;
            constraintsJLabelStopGear.insets = new java.awt.Insets(5, 5, 4, 1);
            getJPanelControls().add(getJLabelStopGear(), constraintsJLabelStopGear);
            
            /******Gear Adjustments - Elliot Khazon******/
                    java.awt.GridBagConstraints buttonAnchor = new java.awt.GridBagConstraints();
                    buttonAnchor.gridx = 1; buttonAnchor.gridy = 10;
                    buttonAnchor.gridwidth = 3;
                    buttonAnchor.anchor = java.awt.GridBagConstraints.WEST;
                    buttonAnchor.ipadx = 3;
                    buttonAnchor.insets = new java.awt.Insets(2, 5, 4, 6);
/*                    JLabel[] labels = getJLabelArrayTargetAdgust();
                    for (int i = 0; i < labels.length; i++) {
                        JLabel label = labels[i];
                        getJPanelControls().add (label, constraintsJLabelGearAdjustFields);    
                        constraintsJLabelGearAdjustFields.gridy ++;
                        
                    }*/
                    JButton targAdjButton = getTargetAdjustButton();
                    getJPanelControls().add(targAdjButton, buttonAnchor);
  /*                  
                    java.awt.GridBagConstraints constraintsJTextGearAdjustFields = new java.awt.GridBagConstraints();
                    constraintsJTextGearAdjustFields.gridx = 5; constraintsJTextGearAdjustFields.gridy = 10;
                    constraintsJTextGearAdjustFields.weightx = 1.0;
                    constraintsJTextGearAdjustFields.anchor = java.awt.GridBagConstraints.WEST;
                    constraintsJTextGearAdjustFields.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    constraintsJTextGearAdjustFields.ipadx = 100;
                    constraintsJTextGearAdjustFields.insets = new java.awt.Insets(2, 2, 3, 1);
                    JTextField[] fields = getJTextFieldArrayTargetAdgust();
                    for (int i = 0; i < fields.length; i++) {
                        JTextField field = fields[i];
                        getJPanelControls().add(field, constraintsJTextGearAdjustFields);
                        constraintsJTextGearAdjustFields.gridy ++;
                    }
                    
    */
            
            /********************************************/
            
            
            java.awt.GridBagConstraints constraintsDateComboStop = new java.awt.GridBagConstraints();
			constraintsDateComboStop.gridx = 4; constraintsDateComboStop.gridy = 7;
			constraintsDateComboStop.gridwidth = 2;
			constraintsDateComboStop.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDateComboStop.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDateComboStop.weightx = 1.0;
			constraintsDateComboStop.ipadx = 85;
			constraintsDateComboStop.insets = new java.awt.Insets(1, 2, 2, 5);
			getJPanelControls().add(getDateComboStop(), constraintsDateComboStop);

			java.awt.GridBagConstraints constraintsDateComboStart = new java.awt.GridBagConstraints();
			constraintsDateComboStart.gridx = 4; constraintsDateComboStart.gridy = 4;
			constraintsDateComboStart.gridwidth = 2;
			constraintsDateComboStart.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDateComboStart.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDateComboStart.weightx = 1.0;
			constraintsDateComboStart.ipadx = 85;
			constraintsDateComboStart.insets = new java.awt.Insets(1, 2, 3, 5);
			getJPanelControls().add(getDateComboStart(), constraintsDateComboStart);

			java.awt.GridBagConstraints constraintsJLabelScenario = new java.awt.GridBagConstraints();
			constraintsJLabelScenario.gridx = 1; constraintsJLabelScenario.gridy = 1;
			constraintsJLabelScenario.gridwidth = 3;
			constraintsJLabelScenario.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelScenario.ipadx = 22;
			constraintsJLabelScenario.insets = new java.awt.Insets(14, 5, 3, 2);
			getJPanelControls().add(getJLabelScenario(), constraintsJLabelScenario);

			java.awt.GridBagConstraints constraintsJComboBoxScenario = new java.awt.GridBagConstraints();
			constraintsJComboBoxScenario.gridx = 4; constraintsJComboBoxScenario.gridy = 1;
			constraintsJComboBoxScenario.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxScenario.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxScenario.weightx = 1.0;
			constraintsJComboBoxScenario.ipadx = -5;
			constraintsJComboBoxScenario.insets = new java.awt.Insets(12, 2, 1, 1);
			getJPanelControls().add(getJComboBoxScenario(), constraintsJComboBoxScenario);

			java.awt.GridBagConstraints constraintsJLabelGear1 = new java.awt.GridBagConstraints();
			constraintsJLabelGear1.gridx = 1; constraintsJLabelGear1.gridy = 15;
			constraintsJLabelGear1.gridwidth = 2;
			constraintsJLabelGear1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGear1.ipadx = 45;
			constraintsJLabelGear1.insets = new java.awt.Insets(5, 5, 13, 1);
			getJPanelControls().add(getJLabelGear1(), constraintsJLabelGear1);

			java.awt.GridBagConstraints constraintsJComboBoxConstraints = new java.awt.GridBagConstraints();
			constraintsJComboBoxConstraints.gridx = 3; constraintsJComboBoxConstraints.gridy = 15;
			constraintsJComboBoxConstraints.gridwidth = 3;
			constraintsJComboBoxConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxConstraints.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxConstraints.weightx = 1.0;
			constraintsJComboBoxConstraints.ipadx = 70;
			constraintsJComboBoxConstraints.insets = new java.awt.Insets(3, 2, 11, 5);
			getJPanelControls().add(getJComboBoxConstraints(), constraintsJComboBoxConstraints);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelControls;
}

	/**
	 * Return the JPanelMultiSelect property value.
	 * @return com.cannontech.common.gui.util.MultiSelectJPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.panel.MultiSelectJPanel getJPanelMultiSelect() {
	if (ivjJPanelMultiSelect == null) {
		try {
			ivjJPanelMultiSelect = new com.cannontech.common.gui.panel.MultiSelectJPanel();
			ivjJPanelMultiSelect.setName("JPanelMultiSelect");
			ivjJPanelMultiSelect.setPreferredSize(new java.awt.Dimension(285, 232));
			ivjJPanelMultiSelect.setMinimumSize(new java.awt.Dimension(285, 232));
			ivjJPanelMultiSelect.setMaximumSize(new java.awt.Dimension(285, 232));
				
				getJPanelMultiSelect().setTableModel( getMultiSelectPrgModel() );
				
				// Do any column specific initialization here				
				javax.swing.table.TableColumn gearColumn = getJPanelMultiSelect().getTableColumn( MultiSelectPrgModel.COL_GEAR );
			
				gearColumn.setPreferredWidth(90);
                gearColumn.setCellEditor( new DefaultCellEditor(new JComboBox()) );
                gearColumn.setCellRenderer( new ComboBoxTableRenderer() );
				

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelMultiSelect;
}


	/**
	 * Return the JPanel1 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanelOkCancel() {
	if (ivjJPanelOkCancel == null) {
		try {
			ivjJPanelOkCancel = new javax.swing.JPanel();
			ivjJPanelOkCancel.setName("JPanelOkCancel");
			ivjJPanelOkCancel.setLayout(getJPanelOkCancelFlowLayout());
			ivjJPanelOkCancel.setMinimumSize(new java.awt.Dimension(161, 35));
			getJPanelOkCancel().add(getJButtonOk(), getJButtonOk().getName());
			getJPanelOkCancel().add(getJButtonCancel(), getJButtonCancel().getName());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelOkCancel;
}


	/**
	 * Return the JPanelOkCancelFlowLayout property value.
	 * @return java.awt.FlowLayout
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.FlowLayout getJPanelOkCancelFlowLayout() {
		java.awt.FlowLayout ivjJPanelOkCancelFlowLayout = null;
		try {
			/* Create part */
			ivjJPanelOkCancelFlowLayout = new java.awt.FlowLayout();
			ivjJPanelOkCancelFlowLayout.setAlignment(java.awt.FlowLayout.CENTER);
			ivjJPanelOkCancelFlowLayout.setVgap(5);
			ivjJPanelOkCancelFlowLayout.setHgap(5);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		};
		return ivjJPanelOkCancelFlowLayout;
	}


	/**
	 * Return the JTextFieldStartTime property value.
	 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStartTime() {
		if (ivjJTextFieldStartTime == null) {
			try {
				ivjJTextFieldStartTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
				ivjJTextFieldStartTime.setName("JTextFieldStartTime");
				ivjJTextFieldStartTime.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
				ivjJTextFieldStartTime.setText("16:25");
				ivjJTextFieldStartTime.setCaretPosition(5);
				ivjJTextFieldStartTime.setSelectionEnd(5);
				ivjJTextFieldStartTime.setEnabled(false);
				ivjJTextFieldStartTime.setSelectionStart(5);
	
				ivjJTextFieldStartTime.setTimeText( new Date() );
				
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldStartTime;
	}


	/**
	 * Return the JTextFieldStopTime property value.
	 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStopTime() {
		if (ivjJTextFieldStopTime == null) {
			try {
				ivjJTextFieldStopTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
				ivjJTextFieldStopTime.setName("JTextFieldStopTime");
				ivjJTextFieldStopTime.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
				ivjJTextFieldStopTime.setText("20:25");
	
				if( getMode() == MODE_STOP )
					ivjJTextFieldStopTime.setTimeText( new Date() );
				else
				{
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime( new Date() );
	
					StringBuffer hour = new StringBuffer( String.valueOf(cal.get( GregorianCalendar.HOUR_OF_DAY)+4) );
					if( hour.length() < 2 )
						hour.insert(0, "0" );
						
					StringBuffer minute = new StringBuffer( String.valueOf(cal.get(GregorianCalendar.MINUTE)) );
					if( minute.length() < 2 )
						minute.insert(0, "0" );
						
					if( cal.get( GregorianCalendar.HOUR_OF_DAY) > 20 )
						hour = new StringBuffer("23");
						
					ivjJTextFieldStopTime.setText( hour + ":" + minute );
				}
			
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldStopTime;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (7/18/2001 5:13:45 PM)
	 * @return int
	 */
	public int getMode() {
		return mode;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (1/21/2001 5:32:52 PM)
	 * @param newLoadControlProgram LMProgramBase
	 */
	public MultiSelectProg[] getMultiSelectObject()
	{
		//used to change the type of our array
		List l = Arrays.asList( getJPanelMultiSelect().getSelectedData() );			

		//even if we only have 1 program, this will find it!
		return (MultiSelectProg[])l.toArray( new MultiSelectProg[l.size()] );
	}


	private IMultiSelectModel getMultiSelectPrgModel()
	{
		if( multiSelectPrgModel == null )
		{
			if( _isScenario )
				multiSelectPrgModel = new MultiSelectPrgScenModel();
			else
				multiSelectPrgModel = new MultiSelectPrgModel();
		}
		
		return multiSelectPrgModel;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (3/12/2001 2:56:28 PM)
	 * @return Date
	 */
	public Date getStartTime()
	{
		if( getJTextFieldStartTime().getText() == null
			 || getJTextFieldStartTime().getText().length() <= 0 )
		{
			//assume they want to start using the time of now
			// and the Date in the JComboBox
			GregorianCalendar c = new GregorianCalendar();
			c.setTime( getDateComboStart().getSelectedDate() );
			
			GregorianCalendar tCal = new GregorianCalendar();
			c.set(GregorianCalendar.HOUR_OF_DAY, tCal.get(GregorianCalendar.HOUR_OF_DAY) );
			c.set(GregorianCalendar.MINUTE, tCal.get(GregorianCalendar.MINUTE) );
			c.set(GregorianCalendar.SECOND, tCal.get(GregorianCalendar.SECOND) );

			return c.getTime();
		}
		else
		{
			GregorianCalendar c = new GregorianCalendar();
			c.setTime( getDateComboStart().getSelectedDate() );
			
			String start = getJTextFieldStartTime().getTimeText();
			
			try
			{
				c.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt( start.substring(0,2) ) );
				c.set(GregorianCalendar.MINUTE, Integer.parseInt( start.substring(3,5) ) );
				c.set(GregorianCalendar.SECOND, 0 );
				return c.getTime();
			}
			catch( Exception e )
			{
				CTILogger.info("*** Received a bad value in getStartTime() of " + this.getClass().getName() + " : " + e.getMessage() );
				return CtiUtilities.get1990GregCalendar().getTime();
			}
			
		}
	
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (3/12/2001 2:56:28 PM)
	 * @return Date
	 */
	public Date getStopTime()
	{
		if( getJCheckBoxNeverStop().isSelected() )
		{
			GregorianCalendar c = new GregorianCalendar();
			c.set( c.YEAR, 2035 ); //watch for End of Unix time 2037
			return c.getTime();
		}
		else if( getJTextFieldStopTime().getText() == null
					 || getJTextFieldStopTime().getText().length() <= 0 )
		{
			//default the stop to 1 day from now
			GregorianCalendar c = new GregorianCalendar();
			c.add( GregorianCalendar.DATE, 1 );
			return c.getTime();
		}
		else
		{
			GregorianCalendar c = new GregorianCalendar();
			c.setTime( getDateComboStop().getSelectedDate() );
	
			String stop = getJTextFieldStopTime().getTimeText();
	
			try
			{
				c.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt( stop.substring(0,2) ) );
				c.set(GregorianCalendar.MINUTE, Integer.parseInt( stop.substring(3,5) ) );
				c.set(GregorianCalendar.SECOND, 0 );
				
				if( getJCheckBoxStartStopNow().isSelected() && getMode() == MODE_STOP )
					return new Date();  //use a time of now
				else
					return c.getTime();
			}
			catch( Exception e )
			{
				CTILogger.info("*** Received a bad value in getStopTime() of " + this.getClass().getName() + " : " + e.getMessage() );
				return CtiUtilities.get1990GregCalendar().getTime();
			}
			
		}
	}


	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		CTILogger.error( exception.getMessage(), exception );;
	}


	/**
	 * Initializes connections
	 * @exception java.lang.Exception The exception description.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initConnections() throws java.lang.Exception {
		
		getJComboBoxGear().addActionListener( this );
		getJComboBoxScenario().addActionListener( this );
        getJComboBoxStopGear().addActionListener( this );
		
		getJButtonCancel().addActionListener(this);
		getJButtonOk().addActionListener(this);
		getJCheckBoxNeverStop().addActionListener(this);
		getJCheckBoxStartStopNow().addActionListener(this);
        getTargetAdjustButton().addActionListener(this);
	}


/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("DirectControlJPanel");
		setLayout(new java.awt.GridBagLayout());
		setMaximumSize(new java.awt.Dimension(315, 260));
		setPreferredSize(new java.awt.Dimension(315, 260));
		setBounds(new java.awt.Rectangle(0, 0, 300, 234));
		setSize(577, 289);
		setMinimumSize(new java.awt.Dimension(315, 260));

		java.awt.GridBagConstraints constraintsJPanelControls = new java.awt.GridBagConstraints();
		constraintsJPanelControls.gridx = 1; constraintsJPanelControls.gridy = 1;
		constraintsJPanelControls.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelControls.weightx = 1.0;
		constraintsJPanelControls.weighty = 1.0;
		constraintsJPanelControls.insets = new java.awt.Insets(0, 0, 1, 1);
		add(getJPanelControls(), constraintsJPanelControls);

		java.awt.GridBagConstraints constraintsJPanelMultiSelect = new java.awt.GridBagConstraints();
		constraintsJPanelMultiSelect.gridx = 2; constraintsJPanelMultiSelect.gridy = 1;
		constraintsJPanelMultiSelect.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelMultiSelect.weightx = 1.0;
		constraintsJPanelMultiSelect.weighty = 1.0;
		constraintsJPanelMultiSelect.ipadx = 1;
		constraintsJPanelMultiSelect.ipady = 20;
		constraintsJPanelMultiSelect.insets = new java.awt.Insets(0, 1, 1, 0);
		add(getJPanelMultiSelect(), constraintsJPanelMultiSelect);

		java.awt.GridBagConstraints constraintsJPanelOkCancel = new java.awt.GridBagConstraints();
		constraintsJPanelOkCancel.gridx = 1; constraintsJPanelOkCancel.gridy = 2;
		constraintsJPanelOkCancel.gridwidth = 2;
		constraintsJPanelOkCancel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelOkCancel.weightx = 1.0;
		constraintsJPanelOkCancel.weighty = 1.0;
		constraintsJPanelOkCancel.ipadx = 417;
		constraintsJPanelOkCancel.insets = new java.awt.Insets(1, 0, 0, 0);
		add(getJPanelOkCancel(), constraintsJPanelOkCancel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	
	if( _isScenario )
		action_Scenario( null );

}


	/**
	 * Insert the method's description here.
	 * Creation date: (7/11/2001 12:46:05 PM)
	 * @return boolean
	 */
	public boolean isStopStartNowSelected() 
	{
		return getJCheckBoxStartStopNow().isSelected();
	}


	/**
	 * Comment
	 */
	public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
	{
		choice = CANCEL_CHOICE;
		exit();
		
		return;
	}


	/**
	 * Comment
	 */
	public void jButtonOK_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
	{
		if( getMode() == MODE_START_STOP 
		 	 && getStartTime() != null
		 	 && getStopTime() != null )
		{
            if( getStartTime().after(CtiUtilities.get1990GregCalendar().getTime())
				 && getStopTime().after(CtiUtilities.get1990GregCalendar().getTime()) )
			{
				if( getStartTime().getTime() >= getStopTime().getTime() )
				{
					javax.swing.JOptionPane.showConfirmDialog( this, "Start time can not be greater than the stop time, try again.", 
								"Incorrect Entry", 
								javax.swing.JOptionPane.CLOSED_OPTION,							
								javax.swing.JOptionPane.WARNING_MESSAGE );
					return;
				}

				if( !getJCheckBoxStartStopNow().isSelected()
					&& getStartTime().getTime() <= System.currentTimeMillis() )
				{
					javax.swing.JOptionPane.showConfirmDialog( this, 
						"Start time must be a valid time in the future, try again.", 
						"Incorrect Entry",
						javax.swing.JOptionPane.CLOSED_OPTION,							
						javax.swing.JOptionPane.WARNING_MESSAGE );
					return;
				}
			}
	
		}
		else if( getMode() == MODE_STOP
					 && getStopTime() != null )
		{
			if( getStartTime().after(CtiUtilities.get1990GregCalendar().getTime())
				 && getStopTime().after(CtiUtilities.get1990GregCalendar().getTime()) )
			{
				Date cDate = new Date();
				
				if( getStopTime().before(cDate) )
				{
					javax.swing.JOptionPane.showConfirmDialog( this, "Stop time can not be less than the current time, try again.", 
								"Incorrect Entry", 
								javax.swing.JOptionPane.CLOSED_OPTION, 
								javax.swing.JOptionPane.WARNING_MESSAGE );
					return;
				}
			}
		}
	
		//make sure we are not setting the target adjustments
        Object gear = getJComboBoxGear().getItemAt(getJComboBoxGear().getSelectedIndex());
        resetForNonTargetCycleGear(gear);
            
		choice = OK_CHOICE;
		exit();
	
		return;
	}
    private void resetForNonTargetCycleGear(Object gear) {
        if (gear instanceof LMProgramDirectGear) 
        {
            //unless we selected target cycle disable the adjustment config
            LMProgramDirectGear directGear = (LMProgramDirectGear) gear;
            if (!isTargetCycleGear(directGear)) {
                getGearConfigJPanel().setAdditonalInfo(null);
            }
        }
        else 
            getGearConfigJPanel().setAdditonalInfo(null);
    }


	/**
	 * Comment
	 */
	public void jCheckBoxNeverStop_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
	{
		getJLabelStopTime().setEnabled( !getJCheckBoxNeverStop().isSelected() );
		getJTextFieldStopTime().setEnabled( !getJCheckBoxNeverStop().isSelected() );
		getJLabelLabelStopHRMN().setEnabled( !getJCheckBoxNeverStop().isSelected() );
		getDateComboStop().setEnabled( !getJCheckBoxNeverStop().isSelected() );
        getTargetAdjustButton().setEnabled(!getJCheckBoxNeverStop().isSelected());
        //make sure that we disable the button permanently
        int selectedIndex = getJComboBoxGear().getSelectedIndex();
        Object selectedGear = getJComboBoxGear().getItemAt( selectedIndex);
        if (!isMulti) {
            if (selectedGear instanceof LMProgramDirectGear) {
                if (!isTargetCycleGear((LMProgramDirectGear) selectedGear)) {
                    getTargetAdjustButton().setEnabled(false);
                }

            } else {
                getTargetAdjustButton().setEnabled(false);

            }
        }
        
		if( getJCheckBoxNeverStop().isSelected() )
			getJButtonOk().setEnabled( true );
		
		return;
	}


	/**
	 * Comment
	 */
	public void jCheckBoxStartStopNow_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
	{
		if( getMode() == MODE_STOP )
		{
			getJLabelStopTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
			getJTextFieldStopTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
			getJLabelLabelStopHRMN().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
			getDateComboStop().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
            getJComboBoxStopGear().setEnabled(!getJCheckBoxStartStopNow().isSelected());
            getJLabelStopGear().setEnabled(!getJCheckBoxStartStopNow().isSelected());
		}
		else if( getMode() == MODE_START_STOP )
		{
			getJLabelStartTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
			getJTextFieldStartTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
			getJLabelLabelStartHRMN().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
			getDateComboStart().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		}
	
		if( getJCheckBoxStartStopNow().isSelected() )
			getJButtonOk().setEnabled( true );
	
		return;
	}


	private void jComboBoxGear_ActionPerformed( java.awt.event.ActionEvent actionEvent )
	{
        int selectedItem = getJComboBoxGear().getSelectedIndex();
        Object gear = getJComboBoxGear().getItemAt(selectedItem);
        if( selectedItem >= 0
		    && selectedItem < IlmDefines.MAX_GEAR_COUNT )
		{
			//add 1 to the gear selected index since gear numbers start at 1
			getMultiSelectPrgModel().setAllGearNumbers(
					new Integer(getJComboBoxGear().getSelectedIndex()+1) );
		}
        if (gear instanceof LMProgramDirectGear) {
            //unless we selected target cycle disable the adjustment config
            LMProgramDirectGear directGear = (LMProgramDirectGear) gear;
            getTargetAdjustButton().setEnabled(isTargetCycleGear(directGear));
        }

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (7/17/2001 9:24:14 AM)
	 * @param gears java.util.Vector
	 */
	public void setGearList(List<LMProgramDirectGear> gears) 
	{
		getJComboBoxGear().removeAllItems();
        getJComboBoxStopGear().removeAllItems();
		
		if( gears != null )
		{
			for( int i = 0; i < gears.size(); i++ )
			{
				getJComboBoxGear().addItem( gears.get(i) );
                getJComboBoxStopGear().addItem( gears.get(i) );
			}
	
			if( getJComboBoxGear().getItemCount() > 0 ) {
				getJComboBoxGear().setSelectedIndex(0);
                getJComboBoxStopGear().setSelectedIndex(0);
            }
		}
	
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (7/18/2001 5:13:45 PM)
	 * @param newMode int
	 */
	public void setMode(int newMode) 
	{
		mode = newMode;
		getJCheckBoxStartStopNow().doClick();
		
		getJLabelScenario().setVisible( _isScenario );
		getJComboBoxScenario().setVisible( _isScenario );
		
        switch( mode )
		{
			case MODE_MULTI_SELECT_ONLY:
				getJPanelControls().setVisible( false );
				//no break, let this fall through

            case MODE_START_STOP:
                getJComboBoxStopGear().setVisible(false);
                getJLabelStopGear().setVisible(false);
                break;
                
			case MODE_STOP:
				getJLabelStartTime().setVisible(false);
				getJTextFieldStartTime().setVisible(false);
				getJLabelLabelStartHRMN().setVisible(false);
				getDateComboStart().setVisible(false);
				getJComboBoxGear().setVisible(false);
				getJLabelGear().setVisible(false);

                canSpecifyStopGear = 
                    YukonSpringHook.getBean(RolePropertyDao.class).checkProperty(
                        YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS,
                        ClientSession.getInstance().getUser());
                if(canSpecifyStopGear) {
                    getJComboBoxStopGear().setVisible(true);
                    getJLabelStopGear().setVisible(true);
                }
                else {
                    getJComboBoxStopGear().setVisible(false);
                    getJLabelStopGear().setVisible(false);
                }
                
				getJComboBoxConstraints().setVisible(false);
				getJLabelGear1().setVisible(false);

				
				//getMultiSelectPrgModel().get
				((DefaultCellEditor)
					getJPanelMultiSelect().getTableColumn( 
							MultiSelectPrgModel.COL_GEAR ).getCellEditor()).getComponent().setEnabled( false );
				
				((ComboBoxTableRenderer)
					getJPanelMultiSelect().getTableColumn( 
							MultiSelectPrgModel.COL_GEAR ).getCellRenderer()).setEnabled( false ) ;
				
				getJCheckBoxNeverStop().setVisible(false);
				getJCheckBoxStartStopNow().setText("Stop Now");
                getTargetAdjustButton().setVisible(false);
				break;
           
				
			default:  //done for completness
                Object gear = getSelectedGear();
                getTargetAdjustButton().setEnabled(false);
                if (gear instanceof LMProgramDirectGear) {
                	getTargetAdjustButton().setEnabled(isTargetCycleGear((LMProgramDirectGear)gear));
                }
                break;
		}
	
	}
    
    private Object getSelectedGear() {
        int selectedItem = getJComboBoxGear().getSelectedIndex();
        Object gear = getJComboBoxGear().getItemAt(selectedItem);
        return gear;
    }
    
    private Object getSelectedStopGear() {
        int selectedItem = getJComboBoxStopGear().getSelectedIndex();
        Object gear = getJComboBoxStopGear().getItemAt(selectedItem);
        return gear;
    }

    private boolean isTargetCycleGear (LMProgramDirectGear gear) {
    	return gear.getControlMethod() == GearControlMethod.TargetCycle;
    }

	/**
	 * Insert the method's description here.
	 * Creation date: (1/21/2001 5:32:52 PM)
	 * @param newLoadControlProgram LMProgramBase
	 */
	public boolean setMultiSelectObject( LMProgramBase[] rows ) 
	{
		if( rows == null )
			return false;
	
	
		//get all the programs and copy the needed values into a different object
		MultiSelectProg[] prgs = new MultiSelectProg[ rows.length ]; 
		for( int i = 0; i < rows.length; i++ )
			prgs[i] = new MultiSelectProg( (LMProgramBase)rows[i] );
	
			
		return setMultiSelectObject( prgs, false );
	}


	private boolean setMultiSelectObject( MultiSelectProg[] rows, boolean isScenario ) 
	{
		if( rows == null )
			return false;

		isMulti = rows.length  > 1 || isScenario;

        if( isScenario )
        {
            getJPanelMultiSelect().setSelectableData( rows );
            getJLabelGear().setVisible(false);
            getTargetAdjustButton().setVisible(false);
        }
        else
        {
            //get all the programs and copy the needed values into a different object
    		MultiSelectProg[] prgs = new MultiSelectProg[ rows.length ]; 
    		for( int i = 0; i < rows.length; i++ )
    			prgs[i] = new MultiSelectProg( rows[i].getBaseProgram() );
    				
    		getJPanelMultiSelect().setSelectableData( prgs );
            setParentWidth( isMulti ? 285 : 0 ); //300, 250

            boolean multiSelectVisible = isMultiSelectVisible(rows, getTopLevelAncestor());
            getJPanelMultiSelect().setVisible( multiSelectVisible);     
        }

        getJComboBoxGear().removeAllItems();
        
		if( isMulti )
		{
            getJComboBoxGear().setVisible(false);

		    //Do any column specific initialization here               
            javax.swing.table.TableColumn gearColumn = 
                    getJPanelMultiSelect().getTableColumn( MultiSelectPrgModel.COL_GEAR );
        
            DefaultComboBoxModel[] models = new DefaultComboBoxModel[ rows.length ];
            for( int i = 0; i < rows.length; i++ ) {
                if( rows[i].getBaseProgram() instanceof IGearProgram ) {
                    LMProgramBase base = rows[i].getBaseProgram();
                    IGearProgram progGear =(IGearProgram)rows[i].getBaseProgram();
                    
                    DefaultComboBoxModel combModel = new DefaultComboBoxModel();
                    for( int j = 0; j < progGear.getDirectGearVector().size(); j++ ) {
                        combModel.addElement(progGear.getDirectGearVector().get(j) );
                    }
                    
                    if(isScenario) {
                        LiteYukonPAObject litePao = (LiteYukonPAObject) getJComboBoxScenario().getSelectedItem();
                        int startGear = YukonSpringHook.getBean(LMDao.class).getStartingGearForScenarioAndProgram(base.getYukonID(), litePao.getLiteID());
                        combModel.setSelectedItem(progGear.getDirectGearVector().get(startGear-1));
                    }
                    
                    models[i] = combModel;
                }

            }
            
            gearColumn.setCellRenderer( new MultiJComboCellRenderer(models) );
            gearColumn.setCellEditor( new MultiJComboCellEditor(models) );
		}
		else if( rows.length == 1 && rows[0].getBaseProgram() instanceof IGearProgram ) {
			//only 1 program, lets just show the gears for this program
		    MultiSelectProg msp = rows[0];
			setGearList( ((IGearProgram)msp.getBaseProgram()).getDirectGearVector() );
			TableColumn gearColumn = getJPanelMultiSelect().getTableColumn( MultiSelectPrgModel.COL_GEAR );
			DefaultComboBoxModel[] models = new DefaultComboBoxModel[ rows.length ];
			LMProgramBase base = rows[0].getBaseProgram();
			
			if( msp.getBaseProgram() instanceof IGearProgram ) {
                IGearProgram progGear = (IGearProgram)msp.getBaseProgram();

                DefaultComboBoxModel combModel = new DefaultComboBoxModel();
                for( int j = 0; j < progGear.getDirectGearVector().size(); j++ ) {
                    combModel.addElement( progGear.getDirectGearVector().get(j) );
                }
                
                if(isScenario) {
                    LiteYukonPAObject litePao = (LiteYukonPAObject) getJComboBoxScenario().getSelectedItem();
                    int startGear = YukonSpringHook.getBean(LMDao.class).getStartingGearForScenarioAndProgram(base.getYukonID(), litePao.getLiteID());
                    combModel.setSelectedItem(progGear.getDirectGearVector().get(startGear-1));
                }
                
                models[0] = combModel;
            }
			gearColumn.setCellRenderer( new MultiJComboCellRenderer(models) );
            gearColumn.setCellEditor( new MultiJComboCellEditor(models) );
        }
	
        getJPanelMultiSelect().selectAllSelected( true );
	
		return ( rows.length > 0 );
	}
    //we want to show the programs if we selected en/dis programs from the control are popup menu
    //this is a cosmetic issue because it is confusing to the user.

    public boolean isMultiSelectVisible(MultiSelectProg[] rows, Container container) {
        boolean prgrmEnableSelected = false;
        if (container instanceof JDialog) {
            JDialog topLevelAncestor = (JDialog) container;
            if (topLevelAncestor.getTitle().equalsIgnoreCase(ControlAreaPopUpMenu.STR_EN_PRGRMS) ||
                    topLevelAncestor.getTitle().equalsIgnoreCase(ControlAreaPopUpMenu.STR_DIS_PRGRMS))
            {
                prgrmEnableSelected = true;
            }
        }

        boolean showMulti = rows.length > 1;
        return showMulti  || prgrmEnableSelected;
    }


    /**
	 * Insert the method's description here.
	 * Creation date: (3/12/2001 3:40:34 PM)
	 *
	 * Method to override if desired 
	 */
	public void setParentWidth( int x ) {}

    public JButton getTargetAdjustButton() {
            if (targetAdjustButton == null) {
                try {
                    targetAdjustButton = new javax.swing.JButton();
                    targetAdjustButton.setName("TargetAdjButton");
                    targetAdjustButton.setMnemonic(79);
                    targetAdjustButton.setMaximumSize(new java.awt.Dimension(175, 25));
                    targetAdjustButton.setPreferredSize(new java.awt.Dimension(175, 25));
                    targetAdjustButton.setMinimumSize(new java.awt.Dimension(175, 25));
                    targetAdjustButton.setMargin(new java.awt.Insets(2, 14, 2, 14));
                    targetAdjustButton.setText("Add Target Adjustments");

                } catch (java.lang.Throwable ivjExc) {
                    handleException(ivjExc);
                }
            }
            return targetAdjustButton;
        }
    //hook into the configuration of the gear (i.e - target cycle)
    public TargetCycleConfigPanel getGearConfigJPanel() {
        if (gearConfigJPanel == null)
        {
            gearConfigJPanel = new TargetCycleConfigPanel();
        }
        return gearConfigJPanel;
    }
    
    public javax.swing.JComboBox getJComboBoxStopGear() {
        if (jComboBoxStopGear == null) {
            try {
                jComboBoxStopGear = new javax.swing.JComboBox();
                jComboBoxStopGear.setName("JComboBoxStopGear");
                jComboBoxStopGear.setEditor(new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource());
                jComboBoxStopGear.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource());
                jComboBoxStopGear.setToolTipText( "The gear or gear number the program(s) should stop control with");
                jComboBoxStopGear.setMaximumSize(new java.awt.Dimension(25,20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return jComboBoxStopGear;
    }
    
    public void setJComboBoxStopGear(javax.swing.JComboBox ivjJComboBoxStopGear) {
        this.jComboBoxStopGear = ivjJComboBoxStopGear;
    }
    public javax.swing.JLabel getJLabelStopGear() {
        if (jLabelStopGear == null) {
            try {
                jLabelStopGear = new javax.swing.JLabel();
                jLabelStopGear.setName("JLabelStopGear");
                jLabelStopGear.setFont(new java.awt.Font("dialog", 0, 14));
                jLabelStopGear.setText("Stop Gear:");
                jLabelStopGear.setMaximumSize(new java.awt.Dimension(36, 19));
                jLabelStopGear.setMinimumSize(new java.awt.Dimension(36, 19));
                jLabelStopGear.setToolTipText( "The gear or gear number the program(s) should stop control with");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return jLabelStopGear;
    }
    public void setJLabelStopGear(javax.swing.JLabel ivjJLabelStopGear) {
        this.jLabelStopGear = ivjJLabelStopGear;
    }
    
    
}