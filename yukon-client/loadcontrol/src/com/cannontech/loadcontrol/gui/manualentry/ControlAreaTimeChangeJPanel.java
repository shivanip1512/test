package com.cannontech.loadcontrol.gui.manualentry;

/**
 * Insert the type's description here.
 * Creation date: (5/15/2002 4:49:59 PM)
 * @author: 
 */
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.data.LMControlArea;

public class ControlAreaTimeChangeJPanel extends com.cannontech.common.gui.util.ConfirmationJPanel implements com.cannontech.common.gui.util.OkCancelPanelListener, java.awt.event.ActionListener 
{
	private LMControlArea lmControlArea = null;
	private javax.swing.JCheckBox ivjJCheckBoxStartTime = null;
	private javax.swing.JCheckBox ivjJCheckBoxStopTime = null;
	private com.cannontech.common.gui.util.TimeComboJPanel ivjTimeComboStart = null;
	private com.cannontech.common.gui.util.TimeComboJPanel ivjTimeComboStop = null;
	private com.cannontech.common.gui.util.OkCancelPanel ivjJPanelOkCancel = null;
/**
 * ControlAreaTimeChangeJPanel constructor comment.
 */
public ControlAreaTimeChangeJPanel() {
	super();
	initialize();
}
/**
 * ControlAreaTimeChangeJPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ControlAreaTimeChangeJPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ControlAreaTimeChangeJPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ControlAreaTimeChangeJPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ControlAreaTimeChangeJPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ControlAreaTimeChangeJPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJCheckBoxStartTime()) 
		connEtoC1(e);
	if (e.getSource() == getJCheckBoxStopTime()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JCheckBoxStartTime.action.actionPerformed(java.awt.event.ActionEvent) --> ControlAreaTimeChangeJPanel.jCheckBoxStartTime_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxStartTime_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JCheckBoxStopTime.action.actionPerformed(java.awt.event.ActionEvent) --> ControlAreaTimeChangeJPanel.jCheckBoxStopTime_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxStopTime_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JPanelOkCancel.okCancelPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> ControlAreaTimeChangeJPanel.jButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCancelAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JPanelOkCancel.okCancelPanel.JButtonOkAction_actionPerformed(java.util.EventObject) --> ControlAreaTimeChangeJPanel.jButtonOkAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOkAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JCheckBoxStartTime property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxStartTime() {
	if (ivjJCheckBoxStartTime == null) {
		try {
			ivjJCheckBoxStartTime = new javax.swing.JCheckBox();
			ivjJCheckBoxStartTime.setName("JCheckBoxStartTime");
			ivjJCheckBoxStartTime.setMnemonic('d');
			ivjJCheckBoxStartTime.setText("Dialy Start Time");
			// user code begin {1}
			
			ivjJCheckBoxStartTime.setText("Daily Start Time");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxStartTime;
}
/**
 * Return the JCheckBoxStopTime property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxStopTime() {
	if (ivjJCheckBoxStopTime == null) {
		try {
			ivjJCheckBoxStopTime = new javax.swing.JCheckBox();
			ivjJCheckBoxStopTime.setName("JCheckBoxStopTime");
			ivjJCheckBoxStopTime.setMnemonic('s');
			ivjJCheckBoxStopTime.setText("Dialy Stop Time");
			// user code begin {1}
			
			ivjJCheckBoxStopTime.setText("Daily Stop Time");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxStopTime;
}
/**
 * Return the JPanelOkCancel property value.
 * @return com.cannontech.common.gui.util.OkCancelPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.OkCancelPanel getJPanelOkCancel() {
	if (ivjJPanelOkCancel == null) {
		try {
			ivjJPanelOkCancel = new com.cannontech.common.gui.util.OkCancelPanel();
			ivjJPanelOkCancel.setName("JPanelOkCancel");
			ivjJPanelOkCancel.setPreferredSize(new java.awt.Dimension(144, 38));
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
/**
 * Insert the method's description here.
 * Creation date: (5/16/2002 10:20:30 AM)
 * @return com.cannontech.loadcontrol.data.LMControlArea
 */
public com.cannontech.loadcontrol.data.LMControlArea getLmControlArea() {
	return lmControlArea;
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 5:02:51 PM)
 */
public int getStartTime()
{
	if( getJCheckBoxStartTime().isSelected() )
		return getTimeComboStart().getTimeInSeconds();
	else
		return LMControlArea.INVAID_INT;
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 5:02:51 PM)
 */
public int getStopTime()
{
	if( getJCheckBoxStopTime().isSelected() )
		return getTimeComboStop().getTimeInSeconds();
	else
		return LMControlArea.INVAID_INT;
}
/**
 * Return the TimeComboStart property value.
 * @return com.cannontech.common.gui.util.TimeComboJPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TimeComboJPanel getTimeComboStart() {
	if (ivjTimeComboStart == null) {
		try {
			ivjTimeComboStart = new com.cannontech.common.gui.util.TimeComboJPanel();
			ivjTimeComboStart.setName("TimeComboStart");
			ivjTimeComboStart.setPreferredSize(new java.awt.Dimension(190, 30));
			ivjTimeComboStart.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeComboStart;
}
/**
 * Return the TimeComboStop property value.
 * @return com.cannontech.common.gui.util.TimeComboJPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TimeComboJPanel getTimeComboStop() {
	if (ivjTimeComboStop == null) {
		try {
			ivjTimeComboStop = new com.cannontech.common.gui.util.TimeComboJPanel();
			ivjTimeComboStop.setName("TimeComboStop");
			ivjTimeComboStop.setPreferredSize(new java.awt.Dimension(190, 30));
			ivjTimeComboStop.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeComboStop;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJCheckBoxStartTime().addActionListener(this);
	getJCheckBoxStopTime().addActionListener(this);
	getJPanelOkCancel().addOkCancelPanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ControlAreaTimeChangeJPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(263, 149);

		java.awt.GridBagConstraints constraintsTimeComboStart = new java.awt.GridBagConstraints();
		constraintsTimeComboStart.gridx = 1; constraintsTimeComboStart.gridy = 2;
		constraintsTimeComboStart.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTimeComboStart.anchor = java.awt.GridBagConstraints.WEST;
		constraintsTimeComboStart.weightx = 1.0;
		constraintsTimeComboStart.weighty = 1.0;
		constraintsTimeComboStart.ipadx = 47;
		constraintsTimeComboStart.ipady = -5;
		constraintsTimeComboStart.insets = new java.awt.Insets(1, 22, 5, 4);
		add(getTimeComboStart(), constraintsTimeComboStart);

		java.awt.GridBagConstraints constraintsJCheckBoxStartTime = new java.awt.GridBagConstraints();
		constraintsJCheckBoxStartTime.gridx = 1; constraintsJCheckBoxStartTime.gridy = 1;
		constraintsJCheckBoxStartTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxStartTime.ipadx = 140;
		constraintsJCheckBoxStartTime.ipady = -2;
		constraintsJCheckBoxStartTime.insets = new java.awt.Insets(4, 4, 0, 5);
		add(getJCheckBoxStartTime(), constraintsJCheckBoxStartTime);

		java.awt.GridBagConstraints constraintsJCheckBoxStopTime = new java.awt.GridBagConstraints();
		constraintsJCheckBoxStopTime.gridx = 1; constraintsJCheckBoxStopTime.gridy = 3;
		constraintsJCheckBoxStopTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxStopTime.ipadx = 142;
		constraintsJCheckBoxStopTime.ipady = -2;
		constraintsJCheckBoxStopTime.insets = new java.awt.Insets(5, 3, 0, 6);
		add(getJCheckBoxStopTime(), constraintsJCheckBoxStopTime);

		java.awt.GridBagConstraints constraintsTimeComboStop = new java.awt.GridBagConstraints();
		constraintsTimeComboStop.gridx = 1; constraintsTimeComboStop.gridy = 4;
		constraintsTimeComboStop.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTimeComboStop.anchor = java.awt.GridBagConstraints.WEST;
		constraintsTimeComboStop.weightx = 1.0;
		constraintsTimeComboStop.weighty = 1.0;
		constraintsTimeComboStop.ipadx = 47;
		constraintsTimeComboStop.ipady = -5;
		constraintsTimeComboStop.insets = new java.awt.Insets(1, 21, 1, 5);
		add(getTimeComboStop(), constraintsTimeComboStop);

		java.awt.GridBagConstraints constraintsJPanelOkCancel = new java.awt.GridBagConstraints();
		constraintsJPanelOkCancel.gridx = 1; constraintsJPanelOkCancel.gridy = 5;
		constraintsJPanelOkCancel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelOkCancel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelOkCancel.weightx = 1.0;
		constraintsJPanelOkCancel.weighty = 1.0;
		constraintsJPanelOkCancel.ipadx = 32;
		constraintsJPanelOkCancel.insets = new java.awt.Insets(1, 39, 3, 48);
		add(getJPanelOkCancel(), constraintsJPanelOkCancel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/16/2002 10:31:30 AM)
 */
private boolean isNewDailyTimeValid() 
{
	if( getStartTime() == LMControlArea.INVAID_INT 
		 && getStopTime() == LMControlArea.INVAID_INT )
		return true;

	//we only have a stop time selected and that stop time >= DefDailStartTime
	if( getStartTime() == LMControlArea.INVAID_INT
	    && getStopTime() != LMControlArea.INVAID_INT )
	{
		return getStopTime() >= getLmControlArea().getDefDailyStartTime().intValue();
	}

	//we only have a start time selected and that start time >= DefDailStopTime
	if( getStopTime() == LMControlArea.INVAID_INT
		 && getStartTime() != LMControlArea.INVAID_INT )
	{
		 return getStartTime() <= getLmControlArea().getDefDailyStopTime().intValue();
	}

	//both start and stop boxes must be checked
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5/16/2002 10:31:30 AM)
 */
private boolean isTimeValid() 
{
	//if the start or stop time == -1, then make sure the startTime > stopTime
	return ( getStartTime() < getStopTime()
		 		||
		 		getStartTime() == LMControlArea.INVAID_INT
		 	   || 
		 	   getStopTime() == LMControlArea.INVAID_INT );

}
/**
 * Comment
 */
public void jButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
{
	setChoice( this.CANCELED_PANEL );
	disposePanel();

	return;
}
/**
 * Method to handle events for the OkCancelPanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getJPanelOkCancel()) 
		connEtoC3(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jButtonOkAction_actionPerformed(java.util.EventObject newEvent) 
{
	if( !isTimeValid() )
	{
		javax.swing.JOptionPane.showMessageDialog(
			this, 
			"The start time must be before the stop time.",
			"Invalid Time Entry",
			javax.swing.JOptionPane.WARNING_MESSAGE );

	}
	else
	{
		if( !isNewDailyTimeValid() )
		{
			javax.swing.JOptionPane.showMessageDialog(
				this, 
				"The new daily start/stop time must be before/after the real start/stop time.",
				"Invalid Time Entry",
				javax.swing.JOptionPane.WARNING_MESSAGE );			
		}
		else
		{
			setChoice( this.CONFIRMED_PANEL );
			disposePanel();
		}

	}

	return;
}
/**
 * Method to handle events for the OkCancelPanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getJPanelOkCancel()) 
		connEtoC4(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jCheckBoxStartTime_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getTimeComboStart().setEnabled( getJCheckBoxStartTime().isSelected() );

	return;
}
/**
 * Comment
 */
public void jCheckBoxStopTime_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getTimeComboStop().setEnabled( getJCheckBoxStopTime().isSelected() );
	
	return;
}
/**
 * Comment
 */
public void jPanelOkCancel_JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
{
	setChoice( this.CANCELED_PANEL );
	disposePanel();

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ControlAreaTimeChangeJPanel aControlAreaTimeChangeJPanel;
		aControlAreaTimeChangeJPanel = new ControlAreaTimeChangeJPanel();
		frame.setContentPane(aControlAreaTimeChangeJPanel);
		frame.setSize(aControlAreaTimeChangeJPanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.ConfirmationJPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/16/2002 10:20:30 AM)
 * @param newLmControlArea com.cannontech.loadcontrol.data.LMControlArea
 */
public void setLmControlArea(com.cannontech.loadcontrol.data.LMControlArea newLmControlArea) 
{
	lmControlArea = newLmControlArea;

	if( getLmControlArea() != null )
	{
		
		//try to use the CurrentDaily first, then try the DefDaily
		getTimeComboStart().setTimeInSeconds(
			LCUtils.decodeStartWindow(getLmControlArea()) );

		//try to use the CurrentDaily first, then try the DefDaily
		getTimeComboStop().setTimeInSeconds(
				LCUtils.decodeStopWindow(getLmControlArea()) );
		
		
		
//		getTimeComboStart().setTimeInSeconds( getLmControlArea().getCurrentDailyStartTime().intValue() );
//		getTimeComboStop().setTimeInSeconds( getLmControlArea().getCurrentDailyStopTime().intValue() );

	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G76F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BAEBD0D4E7160C2F7840C894E7DDE34D3A0931289AEA47E419CDAAC3F6333D4E2EEE2C2C4E1ACAF6569F4E8C35E193B3E5EA5C9D13E1B79F8DB420BC9AE940A4C6E1B5BEB608982D9589F16425BC1A31B7A2A1B3EE74CADF2061F65F367BB6B4CAE44F799E37AF4DED1818112AD377FE67FC5FF75E67BB5FED0863506A1CCC2B03104C9C427F4EE592922EA524690B0CE58297A7DBEC4470D785D0C2F6
	8DE443B381E8154BADB6BB7932B38F46EBE03CE31945D6816F25242F332186DEE270F1GEDCE780FBF1B1E0F47A678645271353682F89681A4818E1F3BE4646FED5B4F711BB8DEC02615109D7DB8E7FE5B810E2B03318BA089A0351F4D5F8A4FFC4AF9EEFD9D1FF77D1155E4575F9F339662BE06BD851C9DE0EBFB793312FCAF138CA22FA7291C38A681E89BGD079AC7278A7CEF8B6380687AE74295E1ABE4967D3FD1A5CB7D82328123B8F5E832AD2B3907254F8A5DFC8D2E4C00C5557B7B3C2E3C0163ABDDE39
	F9D0728D489DAE49A7AB63145FDBEDF9F4FFC2DAE01C67C15C06FDA84373B0FE85E0270EDFD20AF8A73C3B8146BDC26FCF0FB5F03DFA6FFE0FE43C6C4BAA0E21F72207691B6A09681D98FABB77DD6A2314DC7445EE209D983258EA813A81B400C40077E9EC7C40765F70EC3C25B52BDE2F6AEB747B1B94F720CBF54B8A3C5757830563BE137B54001B90667F275B2ACD64198E583A9FFE6DF54CA6C9E7C4ACA4FF3616EC7B73CEEB2E09CD36A555DA130718AD568FC5EC42F87FE7CC5EE9C313E3F5B26F4F091177
	0EF9EAD60E896F1417CED8F773FD8A06A33C9B0651578D545755701E49630361AFD1FC1C814F6276C59AB3B8CEG5A2EE1919B3DC5C296E776CAE2BF6DB435438ACB2335D7E1B27815E8D9329C3877AA05714C57E531D37CB386BC13E5B9159147DB01D6673558D8FCBD170FF93C8A464FGEC86C8874873D9ECC7G6AFCA24632F3CF7D1EB1661282DAD348233865GE1EB1F4BAD07279C5682D23BAF28C93EBE39C78AF824DB0A9CA4FC4E5F66228DEECDDA77820D7DBE60F1D9764981C97328BE58DD724A1A9C
	8832B9DBF3CBF08E64B2D2B11F010157CF443E5BF2CB6129C8C16D1A5FADE9F2E48623276522CDC6249E5891B600F7E6175776203DFAC1FFBFC01CEE079BF938DF279C00EAD1DF6FD45561103F9BB5A11BFDE86732F62C9D09701E6C93B67E173D08F32896DB35AA7679F65EBE43BEAD1EBE94DF8A0C099CEBAA45DAFDD195B17373DE91B3AD05CF106A17BACCE386E1D0E5319227C6670E136E09F2317156FD3DDFB30F236122EA1EF311DA7146B76611B58D0D59373B90FD1584322C87C853ED3CA52F1245EC13
	9414C514DE89C000496E0845191C798EBB37A7724B6172B3DAB925E5403EC77C961B93C0738B9BBEDA5AEB326E1B68BB5506A91407F17F7CBFA08FE62B3F4B4773C99E01B332C5764BBEB73C84B96D45FC64477337DF5557EC25F8BF64FBB3A4D8D0477FB89FF3D252340067D6C8132F4A1A66718DC468BF253C0221DB7EC829903C7EB59FF5D4C7A15F7579BF4F47DC01735CA7D32769CBB8FDA74DD5D9113DA0F4339A72E98C7F6FFBB05E0765B1DEAB5E5E03CD51082404306E309EE21D3497F588F83C908AD8
	3AC876ED4C636A767D869B2D82FFE3AF748ABCF778C5CE0E9E96B9F9644E1F1267EFFCE8B5BBD3D6BC51E2F55EE6F9F273F6A4A719CC2F6FB5177DCDDAB3B8BE48E4DDFC97E95334AFBC9F9697517388F8A582A45F967960ADC03A28B9BC911AD5C5E1E6556D7D5BC20C7720FCBBA4C329466256295BD0AA5C97C56B6176E4F4CFA15AD3B1544578G5A3343A17A4575A04FB6000C5B4C268D81E693B62F28CC4CFB8546BB8311F97620F11EC51FF79D709A400AA01B37E3523CC565FA1F2A31F9AE0D4DDB2D31F9
	4C364F15C12BEE5ACBE27F9852BC8AED9C7BBD81AFBDE90C7D6237E94D47FD52B55197DA1C68034A901C21G2E100819AD0522AFCC9CDECD36BF7EDE4CFEF8B0447588C5E226F236688BDFAEC61E1B0036ADA47C1BD6E8BC1B9A03C1354FC305653ED9D6E40CA906DBD134871E20D6D0EB07248F8BBFFE27886545F31323F99F91247D856A3A93789E9F3158D60DA05EFA49E1107D6688BB7F6643B30B55844747D893E820553007CB8E742B812F6CBEBEA40DC8B55228D6C36BCFCDA3A537627B89611F9F75B742
	F328F9D06E9BEED243D7B5C8D58C7F9B0DD1FBB9E3EF7649099EA27A4147CAAFF2D9B77D59CA52B0D1EAB56B4F6797FFE0CD9B617D60E8448F63CF5932F6FC792BDCE613F3E542A63DB0A7E1D45824602419CDB60C0A7BCBF5734549BA297E7BD1094A703AAE43B6585F913658D89D7B67BCE39DF306198CF36039A2ACFC73D9F99B565C61E66C4C949A58B59DCDA1CDD3FD8C45985EB0F59B26C68D15654A2DA1A8A4A718EE3FF5FC0A3A7D9E3BB8CD37E13EE23A25148B5D56014C693A5C6FD73444127B4A70FD
	48BCB0F1BEA2F34C9D26131777846D51F7144A31C81FF4F6ECEA4F44FA777F69C2BD5B46D86F3ED74737D07C20814F6C320156989C4F875A0EB151EF7617204FDDF7ACB685E08E40AA00D400EDF7C42DB8DAFC064C504F37C37D8B78A4656A2403DA7455456883B8DB79F8FBB15A101E2D3A9D469C51F658FDE7326E0E3B1347EC5DC3D3569D0C9A57C52DE37E3B500332509A0EBE8A5784C7216F282F6F9099C6D83DBEFFA90F73F25D95F5792BCB463A1CDA7C31F52FC9BD20B50173CE3A9B2987557AFD5DDE0A
	67CDBA503247614E8ED087F09DA08C30EADC58FF487E69CC762751552DB6832E8D4E5366687EC7375727E577E7674EA96B226D1CBABEF99D33DF73C9E13F2D63228FFE73BEE5F8E7C6998A3EB27375DC6FC7E426737A0A31B2F059CB7345D9FD64ABF1D61751FB7AF598EBG71DF891D1EAD3FBF1DB6CCD9972D531AA81D585D71A95AC72482EDB38245AD215F82F74138601E102B218B4F15DA98BB819400F800A41D5EC1696B6F31BE8BFB2874FB2C77E174971D51F7B2A7DFAF4635774C6FF5CA94BE5EE49E63
	716A949EE8733D06F12CFBDD747D496C2E43F2C8AD09FDE799A9B13F330C17185FD9E61514B0BC6CE17C02A1688BCB263F53ACADB13B53FCC0755CD8DECC778E8665A0CD6A96C913B86DD52D0C185CEDFEA3F9E5EE23D2324E114F6734EAEA73E95D4697AEC547D9410445D6B941FCF2F6E22AEFD80D7AA38746C79B505DG63G09GA99322B69D5FB76359509A1E66ECF82FB436DF2ACA4D7DF22C947B60209A8A744951362FA95D2F53A2D530DD133DE8A6B6679CDD8FDD6100BC45F79768FAC60B357EF2E9AC
	7FB29FE8E5C69FFC2C7B206DE4348F36029D53396DB3CCFC4076BBDEEE5C6F0C3E5F7EFC311FBDEE6A5AA3A6B856A3AC20B52CAE0E758819BAFEA9453798702CF7F8FF1F78266E82DACA1C68295E1CA457D9DD5E0D6DF8F7D624B1B52491B267DB3847F67D0E3C9F4635G3DG4A37D80F496A74132318737D81498B663C6A177B484DD938F6ED277E9B834C5F8EC0GDF89F0F11610655D8A2453CB2E6A3D25521EDF487334976F219DD36E8302DE62AA19CCE73D352ECF202FF0725E2ADF27BD6AC33E6C06A74E
	90423F8BCCF4997B6924D9CC57B4F86E1EA5FA6E9F971B4D314F36580E4C96F57D9A6DC19AE06C52F15F21B8B70CB59D17F5D9547F3859A2BF7F4491BB3F56BA4C73AB59E1DE771EF2CCDF771EF5444A8BA65F8D5A8BEC865952F4190BCEA1EEB70C736688DC701450432163CE5133588D63B89D770F75E2DE629CF1DEADC1D7737CD8E1F82F7A465F96278346237E0172E0391857A1F2B18DF4BEB0D7644E691B58CB6A7DAC5EG6B6BAF87BC6EA6E940C9B31175B42E0EBC3E3EFB2E68E96E51BD63E13C86A0F5
	2E68F343C7C51F1B3CF6A5F146F51AFEFF585E7D31F56BDCD6EBF64F0D7439C95067B21E1BBA854F3373A06767A16E6DCE63FD97F369A318D003F503522B1DC6BA4695DC6F8D74AFA751D15EE22B111EB0091EE3D84FF4FB61C321DB468BC9C4797E79185FD6D266B11DDE61CFD74FE1F2E4612FF2195D97B51EA4B37516FA6560DF5EC4EF7CF417680D6D8F89DFDCE92577BA985F84888330397B6CE2EE02BE77F89E4E5DG636DG3B9F9235D02135416B1361C07674D54E473933A9EEA3721AAF66FD7F92FE73
	5420G386763EF406771139059C0673FF739E51AD84C0A02A4D803F0185BB0955E5F05177FF8671DF7982F1113D3E5DACCF12F22CC8B04CC9F1E1AAC53EDC7C4260527E612A9F79A19AA01478713E4FA2D8BFD6E8F487DF2A0A03B2FFA6EE04DE7327552FAD38DEB6AG987F576AE7623F57405F8D6B428B98FF9C4743FBA31705550B220E7B7BCD7540FD2E3B763557313E405509F136E2C1F43F7085452FD9905DAF4C51EB54BA20B9960AEF8D5DE5F85605E11C88108C309EA08D00AC9267150BFEDB1D361F6C
	D67DCEB85094439144745B52F9FF3FB57FF52E2885A94FBEC61E1977D6E62C7B3CE3916F1779B33D7BB045B37DDEADC77D5CC00B83D883108A3099A0DD57EF853D43E2D76C76E80D1AA6758D52AF2CC2399E0FBC9AC1E39344F4CB6D923AE5AC1A5A57596321DF081F0CE7BE7A9BDACF6A6223FD370962DB6223FDF72DD56F75001692AF4E558A3B603FFF71D47E55003B38580C7FAADA233A97C773FF0262EFAE0E66DF13A778ABC05B3CD85405151770F94CFD0C7BEC752871FFC56A761CB37DBD057A6AE1A6D7
	9BFF26BEF4186F718F3FB36EE1DFD9E54D0C31C79CDF9B7FF064FCBBC465A2A40FC47ED84DBA0D6140F97C24C04456016FBECADADEFEAB73F08C9E4F733D6DCBA2BC5207B2F2197EEBCED27D0347B80FB75E0B70FB9F0FFE5E0FEE28C739FC72E8257EA138850E9F9F7EE0FD7C7459335515E76ACE1029326F7C35990E6DFF812F1F3C0674F4C65EEF985E97F6BC787E6A71874A0365677F15456E175DE5667D72D019F93F6CAF1B7EBBC038AC76EF1B93E5465FB6D9FDA9BAC86F62909FEE0079G2BG56AD9135
	25307C3F882DADA6F52555BD0077E17C0F8ACD7FAD34BC362E3FAC1F797FB0C8857E9EF4CB55B4550B7B8B3C2D827D8537A40AE4383D95E82300E7E0D0E3D3B93E248263CD117BA91A7775DD22575F34C4747AABDBC52F3FDD477D2285F1358996DBCB0228D39BDBA3B179A3437B6FDA9FFCFCEA8F1487533946AEEC531BA06C304DA5EC236838AD3942B6293AED9E4B0B58633B067733F98F5EB6DD8F14874B0F0F8E517BA5683BA3C16444E395786D37C79A621D849E3BEA00E685A0DD8155AF8734B106253FCE
	733D0ED692BDC653ABBE0C3587E4D68754D9763BCE37B4204BF23D8AFB4502C7AC3663GCEGB7C098A081E08D40B3G1BG52GB2G6CCB618C872883E883388E60DFAA7498F97744B4FA700B9ECD70006441946732179F0C1D639F9C1CB9470F9E243F83C3EEF60F7979772F0F8E62779D353FBFA86B35247620DD473587DDEA50035F4DD8AF1D759B5CE38E681230D47452CF364E54CB6710C8AF1D8A6BB6AF0D7452E970FE831E3F54FB79BD2D98E7328F05F7739E3E2AD55CBBF742FC067B1C66CF76B2F8DF
	A6E47905EBA6D9728D32B440BA573208AC6EE591D9980F37F385DF7FB2613F5AC33817FE9630D06336BBF7886B3A4787A613944F9D7AB37CD518689B6067C8D5EC9F0ED64D6C4338AA73BABD2FEAEA1DDED4E5DE279F29B256E97EFFE9D5682381083DDEFD5E23BA6E8B9D371A6E09384FBDEEEDD0472F25F2A13ECDC6067A77A7AA57A84575F25C13865C979C7797D598072072209A90EBFFD0057747FE0F223071DF51B99E9F0425EE0F75D4C60FDFB2483DD147215C2C6F69C297733EEAC96BF4FD5579DA682B
	EA3FEEDF75EFC7A27B5735A27FFEC9894ABA7F1A23917A96579F1E7F7F9E0B6CDF12E7644F7E0F7C254F0FC190A73FC1F2B4E2EF01589C2B245FCEAB710306C60A85CA75D3CCB0C00AF5615A2945370F1C294F7A5963D9D6125DCEECAC825BC9B606D1BB4921D156CE32E003301F4B065EC3C932033749BDE265D814917D1883BC76CDB5C063437F74F2720D960715D8014D204C764D9945E823BB23A193749DE9A78D00B74DCC0AC50E783B5BDF6AF390D34E6509B587B33C0C4F4D647D7E0989631DE2775C7DB1
	6FA56E652C4F8FAF0F747B6C6CF8FD90FD1582784D4BC57D78E4903FB345BA032EFAA5C551A4E5385167467328E9CC13A3FD664EC15CBFA866F0A26F7386D16EA1431A7F87D0CB878837834E54CB91GG20B0GGD0CB818294G94G88G88G76F854AC37834E54CB91GG20B0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0591GGGG
**end of data**/
}
}
