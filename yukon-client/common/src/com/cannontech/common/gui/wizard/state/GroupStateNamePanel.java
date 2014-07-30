package com.cannontech.common.gui.wizard.state;

import com.cannontech.common.gui.editor.state.GroupStateEditorPanel;
import com.cannontech.database.data.state.GroupState;
import com.cannontech.database.db.state.StateGroupUtils;

/**
 * This type was created in VisualAge.
 */
public class GroupStateNamePanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjStateGroupNameLabel = null;
	private javax.swing.JTextField ivjStateGroupNameTextField = null;
	private javax.swing.JLabel ivjStateNumberLabel = null;
	private com.klg.jclass.field.JCSpinField ivjStateNumberSpinner = null;
/**
 * Constructor
 */
public GroupStateNamePanel() {
	super();
	initialize();
}
/**
 * Return the StateGroupNameLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getStateGroupNameLabel() {
	if (ivjStateGroupNameLabel == null) {
		try {
			ivjStateGroupNameLabel = new javax.swing.JLabel();
			ivjStateGroupNameLabel.setName("StateGroupNameLabel");
			ivjStateGroupNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateGroupNameLabel.setText("State Group Name:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateGroupNameLabel;
}
/**
 * Return the StateGroupNameTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateGroupNameTextField() {
	if (ivjStateGroupNameTextField == null) {
		try {
			ivjStateGroupNameTextField = new javax.swing.JTextField();
			ivjStateGroupNameTextField.setName("StateGroupNameTextField");
			ivjStateGroupNameTextField.setPreferredSize(new java.awt.Dimension(150, 21));
			ivjStateGroupNameTextField.setMinimumSize(new java.awt.Dimension(150, 21));
			ivjStateGroupNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_GROUP_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateGroupNameTextField;
}
/**
 * Return the StateNumberLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getStateNumberLabel() {
	if (ivjStateNumberLabel == null) {
		try {
			ivjStateNumberLabel = new javax.swing.JLabel();
			ivjStateNumberLabel.setName("StateNumberLabel");
			ivjStateNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateNumberLabel.setText("Number of States:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNumberLabel;
}
/**
 * Return the StateNumberSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getStateNumberSpinner() {
	if (ivjStateNumberSpinner == null) {
		try {
			ivjStateNumberSpinner = new com.klg.jclass.field.JCSpinField();
			ivjStateNumberSpinner.setName("StateNumberSpinner");
			ivjStateNumberSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjStateNumberSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			ivjStateNumberSpinner.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(null, 
						new Integer(2), new Integer(GroupStateEditorPanel.STATE_COUNT), null, true, null, 
						new Integer(1), "#,##0.###;-#,##0.###", false, false, false, 
						null, new Integer(2)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
							new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), 
							new java.awt.Color(255, 255, 255, 255))));

			ivjStateNumberSpinner.setValue( new Integer(2) );

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNumberSpinner;
}
/**
 * getValue method comment.
 */
@Override
public Object getValue(Object val) {

    GroupState gs = (GroupState) val;

	String stateGroupName = getStateGroupNameTextField().getText();
	if( stateGroupName != null ) {
        gs.getStateGroup().setName(stateGroupName);
    }



	gs.getStatesVector().removeAllElements();
   
   com.cannontech.database.data.state.State
         tempStateData = new com.cannontech.database.data.state.State();

	// Add an 'Any' state to all state groups!!!  12-6-2000
	tempStateData.setState( new com.cannontech.database.db.state.State( gs.getStateGroup().getStateGroupID(),
										new Integer(com.cannontech.database.db.state.State.ANY_ID),
										com.cannontech.database.db.state.State.ANY,
										new Integer(com.cannontech.common.gui.util.Colors.BLACK_ID),
										new Integer(com.cannontech.common.gui.util.Colors.WHITE_ID) ) );

   gs.getStatesVector().add( tempStateData );
   

   com.cannontech.database.db.state.State tempState = null;

    if(gs.getStateGroup().getGroupType().equalsIgnoreCase(StateGroupUtils.GROUP_TYPE_STATUS))
    {
        Object stateNumberSpinVal = getStateNumberSpinner().getValue();
        Integer numberOfStates = null;
        if( stateNumberSpinVal instanceof Long ) {
            numberOfStates = new Integer( ((Long)stateNumberSpinVal).intValue() );
        } else if( stateNumberSpinVal instanceof Integer ) {
            numberOfStates = new Integer( ((Integer)stateNumberSpinVal).intValue() );
        }
        
    	// add the rest of the states below
    	for(int i=0;i<numberOfStates.intValue();i++)
    	{
            tempStateData = new com.cannontech.database.data.state.State();
          
    		tempStateData.setState( new com.cannontech.database.db.state.State(	
                                     gs.getStateGroup().getStateGroupID(),
                                     new Integer(i),
          									"DefaultStateName" + (Integer.toString(i)),
          									new Integer(i),
          									new Integer(com.cannontech.common.gui.util.Colors.BLACK_ID) ) );
    
    		gs.getStatesVector().add(tempStateData);
    	}
    }else if(gs.getStateGroup().getGroupType().equalsIgnoreCase(StateGroupUtils.GROUP_TYPE_ANALOG))
    {
        
        gs = StateGroupUtils.buildAnalogStateGroup(gs);
        
    }

	return gs;
}
/**
 * Called whenever the part throws an exception.
 * 
 * @param exception
 *            java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("VersacomAddressingEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(336, 256);

		java.awt.GridBagConstraints constraintsStateGroupNameTextField = new java.awt.GridBagConstraints();
		constraintsStateGroupNameTextField.gridx = 1; constraintsStateGroupNameTextField.gridy = 0;
		constraintsStateGroupNameTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateGroupNameTextField.insets = new java.awt.Insets(5, 15, 5, 0);
		add(getStateGroupNameTextField(), constraintsStateGroupNameTextField);

		java.awt.GridBagConstraints constraintsStateGroupNameLabel = new java.awt.GridBagConstraints();
		constraintsStateGroupNameLabel.gridx = 0; constraintsStateGroupNameLabel.gridy = 0;
		constraintsStateGroupNameLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateGroupNameLabel.insets = new java.awt.Insets(5, 5, 5, 0);
		add(getStateGroupNameLabel(), constraintsStateGroupNameLabel);

		java.awt.GridBagConstraints constraintsStateNumberLabel = new java.awt.GridBagConstraints();
		constraintsStateNumberLabel.gridx = 0; constraintsStateNumberLabel.gridy = 1;
		constraintsStateNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateNumberLabel.insets = new java.awt.Insets(5, 5, 5, 0);
		add(getStateNumberLabel(), constraintsStateNumberLabel);

		java.awt.GridBagConstraints constraintsStateNumberSpinner = new java.awt.GridBagConstraints();
		constraintsStateNumberSpinner.gridx = 1; constraintsStateNumberSpinner.gridy = 1;
		constraintsStateNumberSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateNumberSpinner.insets = new java.awt.Insets(5, 15, 5, 0);
		add(getStateNumberSpinner(), constraintsStateNumberSpinner);
        initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Initializes connections
 */
private void initConnections() throws java.lang.Exception {
    getStateGroupNameTextField().addCaretListener(this);
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
@Override
public boolean isInputValid() {
    if( getStateGroupNameTextField().getText().length() > 0 )
    {
        setErrorString("The State Group Name text field must be filled in");
        return true;
    } else {
        return false;
    }
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GroupStateNamePanel aGroupStateNamePanel;
		aGroupStateNamePanel = new GroupStateNamePanel();
		frame.setContentPane(aGroupStateNamePanel);
		frame.setSize(aGroupStateNamePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * setValue method comment.
 */
@Override
public void setValue(Object val)
{
}

@Override
public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        @Override
        public void run() 
            { 
            getStateGroupNameTextField().requestFocus(); 
        } 
    });    
}

/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */

@Override
public void caretUpdate(javax.swing.event.CaretEvent e) {
    if (e.getSource() == getStateGroupNameTextField()) {
        connEtoC3(e);
    }
}

/**
 * connEtoC3:  (DescriptionTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> SimpleLocalPortSettingsPanel.descriptionTextField_CaretUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
    try {
        stateGroupNameTextField_CaretUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}

/**
 * Comment
 */
public void stateGroupNameTextField_CaretUpdate() {
    fireInputUpdate();
}

public void setTypeAnalog()
{
    getStateNumberLabel().setVisible(false);
    getStateNumberSpinner().setVisible(false);
}

}
