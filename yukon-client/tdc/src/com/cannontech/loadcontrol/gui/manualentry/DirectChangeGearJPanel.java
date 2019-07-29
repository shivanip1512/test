package com.cannontech.loadcontrol.gui.manualentry;

import java.util.List;

import javax.swing.JPanel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;

/**
 * Insert the type's description here.
 * Creation date: (3/12/2001 9:57:47 AM)
 * @author: 
 */
public class DirectChangeGearJPanel extends JPanel implements java.awt.event.ActionListener {
	public static final int CANCEL_CHOICE = 0;
	public static final int OK_CHOICE = 1;
	private int choice = CANCEL_CHOICE;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private JPanel ivjJPanelOkCancel = null;
	private javax.swing.JComboBox ivjJComboBoxGear = null;
	private javax.swing.JLabel ivjJLabelGear = null;
 
    private JPanel ivjJPanelControls = null;
    
	public DirectChangeGearJPanel() {
		super();
		initialize();
    }
    
	public void actionPerformed(java.awt.event.ActionEvent e) {
		// user code begin {1}
		
		/*if( e.getSource() == getJComboBoxGear() ) 
			jComboBoxGear_ActionPerformed( e );*/
		
		// user code end
		if (e.getSource() == getJButtonCancel()) 
			connEtoC1(e);
		if (e.getSource() == getJButtonOk()) 
			connEtoC2(e);
		// user code end
    }

	private void connEtoC1(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.jButtonCancel_ActionPerformed(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	private void connEtoC2(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.jButtonOK_ActionPerformed(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	public synchronized LMManualControlRequest createMessage( LMProgramDirect program, Integer gearNum ) 
	{
        return LCUtils.createStartMessage( true, program.getStartTime().getTime(), program.getStopTime().getTime(),
                                       program, gearNum, LMManualControlRequest.CONSTRAINTS_FLAG_CHECK, null, ProgramOriginSource.MANUAL );
    }

	public void exit() {}
    
	public int getChoice() {
		return choice;
	}

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
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJButtonCancel;
	}

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
				// user code begin {1}

				ivjJButtonOk.setText("Submit");

				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJButtonOk;
	}

	private javax.swing.JComboBox getJComboBoxGear() {
		if (ivjJComboBoxGear == null) {
			try {
				ivjJComboBoxGear = new javax.swing.JComboBox();
				ivjJComboBoxGear.setName("JComboBoxGear");
				ivjJComboBoxGear.setEditor(new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource());
				ivjJComboBoxGear.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource());
				// user code begin {1}
                
				ivjJComboBoxGear.setToolTipText( "The gear or gear number the program should now control with");
				
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJComboBoxGear;
	}

	private javax.swing.JLabel getJLabelGear() {
		if (ivjJLabelGear == null) {
			try {
				ivjJLabelGear = new javax.swing.JLabel();
				ivjJLabelGear.setName("JLabelGear");
				ivjJLabelGear.setFont(new java.awt.Font("dialog", 0, 14));
				ivjJLabelGear.setText("Gear:");
				ivjJLabelGear.setMaximumSize(new java.awt.Dimension(36, 19));
				ivjJLabelGear.setMinimumSize(new java.awt.Dimension(36, 19));
				// user code begin {1}
	
				ivjJLabelGear.setToolTipText( "The gear or gear number the program should now control with");
	
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelGear;
	}

	private JPanel getJPanelControls() {
	if (ivjJPanelControls == null) {
		try {
			ivjJPanelControls = new JPanel();
			ivjJPanelControls.setName("JPanelControls");
			ivjJPanelControls.setPreferredSize(new java.awt.Dimension(285, 232));
			ivjJPanelControls.setLayout(new java.awt.GridBagLayout());
			ivjJPanelControls.setMaximumSize(new java.awt.Dimension(285, 232));

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
            
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelControls;
}

	private JPanel getJPanelOkCancel() {
	if (ivjJPanelOkCancel == null) {
		try {
			ivjJPanelOkCancel = new JPanel();
			ivjJPanelOkCancel.setName("JPanelOkCancel");
			ivjJPanelOkCancel.setLayout(getJPanelOkCancelFlowLayout());
			ivjJPanelOkCancel.setMinimumSize(new java.awt.Dimension(161, 35));
			getJPanelOkCancel().add(getJButtonOk(), getJButtonOk().getName());
			getJPanelOkCancel().add(getJButtonCancel(), getJButtonCancel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOkCancel;
}

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

	private void handleException(java.lang.Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		CTILogger.error( exception.getMessage(), exception );;
	}
    
	private void initConnections() throws java.lang.Exception {
		// user code begin {1}
		getJComboBoxGear().addActionListener( this );
		
		// user code end
		getJButtonCancel().addActionListener(this);
		getJButtonOk().addActionListener(this);
	}

	private void initialize() {
    	try {
    		// user code begin {1}
    		// user code end
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
    	// user code begin {2}
    	// user code end
    }

	public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
		choice = CANCEL_CHOICE;
		exit();
		
		return;
	}

	public void jButtonOK_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        
		choice = OK_CHOICE;
		exit();
	
		return;
	}

	public void setGearList(List<LMProgramDirectGear> gears) {
		getJComboBoxGear().removeAllItems();
		        
		if( gears != null )
		{
			for( int i = 0; i < gears.size(); i++ )
			{
				getJComboBoxGear().addItem( gears.get(i) );
			}
	
			if( getJComboBoxGear().getItemCount() > 0 )
				getJComboBoxGear().setSelectedIndex(0);
		}
	
	}

    private Object getSelectedGear() {
        int selectedItem = getJComboBoxGear().getSelectedIndex();
        Object gear = getJComboBoxGear().getItemAt(selectedItem);
        return gear;
    }
    
    public Integer getSelectedGearNumber() {
        return ((LMProgramDirectGear)getSelectedGear()).getGearNumber();
    }
    
	public void setParentWidth( int x ) {}
}