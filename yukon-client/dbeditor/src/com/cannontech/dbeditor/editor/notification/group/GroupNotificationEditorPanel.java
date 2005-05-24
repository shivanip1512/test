package com.cannontech.dbeditor.editor.notification.group;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.notification.NotificationGroup;

/**
 * Insert the type's description here.
 * Creation date: (11/20/00 10:45:15 AM)
 * @author: 
 */
public class GroupNotificationEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelFrom = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JPanel ivjJPanelEmail = null;
	private javax.swing.JTextField ivjJTextFieldFromAddress = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JTextField ivjJTextFieldSubject = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisableGroup = null;
	private javax.swing.JEditorPane ivjJEditorPaneAdditionalMessage = null;
	private javax.swing.JLabel ivjJLabelAdditionalMessage = null;
	private javax.swing.JLabel ivjJLabelDefaultSubject = null;

	private JScrollPane editorScrollPane = null;

/**
 * GroupNotificationEditorPanel constructor comment.
 */
public GroupNotificationEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJCheckBoxDisableGroup()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldName()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldFromAddress()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldSubject()) 
		connEtoC3(e);
	if (e.getSource() == getJEditorPaneAdditionalMessage()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupNotificationEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JTextFieldFromAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupNotificationEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JTextFieldSubject.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupNotificationEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JEditorPaneMessage.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupNotificationEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JCheckBoxDisable.action.actionPerformed(java.awt.event.ActionEvent) --> GroupNotificationEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JCheckBoxDisable property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisableGroup() {
	if (ivjJCheckBoxDisableGroup == null) {
		try {
			ivjJCheckBoxDisableGroup = new javax.swing.JCheckBox();
			ivjJCheckBoxDisableGroup.setName("JCheckBoxDisableGroup");
			ivjJCheckBoxDisableGroup.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxDisableGroup.setText("Disable Group");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisableGroup;
}
/**
 * Return the JEditorPaneMessage property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getJEditorPaneAdditionalMessage() {
	if (ivjJEditorPaneAdditionalMessage == null) {
		try {
			ivjJEditorPaneAdditionalMessage = new javax.swing.JEditorPane();
			ivjJEditorPaneAdditionalMessage.setName("JEditorPaneAdditionalMessage");
			// user code begin {1}			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJEditorPaneAdditionalMessage;
}


private JScrollPane getEditorScrollPane()
{
	if( editorScrollPane == null )
	{
		editorScrollPane = new JScrollPane();
		editorScrollPane.setViewportView( getJEditorPaneAdditionalMessage() );

		editorScrollPane.setBorder(
				BorderFactory.createLoweredBevelBorder() );
	}

	return editorScrollPane;
}

/**
 * Return the JLabelMessage property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAdditionalMessage() {
	if (ivjJLabelAdditionalMessage == null) {
		try {
			ivjJLabelAdditionalMessage = new javax.swing.JLabel();
			ivjJLabelAdditionalMessage.setName("JLabelAdditionalMessage");
			ivjJLabelAdditionalMessage.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAdditionalMessage.setText("Additional Message:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAdditionalMessage;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDefaultSubject() {
	if (ivjJLabelDefaultSubject == null) {
		try {
			ivjJLabelDefaultSubject = new javax.swing.JLabel();
			ivjJLabelDefaultSubject.setName("JLabelDefaultSubject");
			ivjJLabelDefaultSubject.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDefaultSubject.setText("Default Subject:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDefaultSubject;
}
/**
 * Return the JLabelFrom property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFrom() {
	if (ivjJLabelFrom == null) {
		try {
			ivjJLabelFrom = new javax.swing.JLabel();
			ivjJLabelFrom.setName("JLabelFrom");
			ivjJLabelFrom.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelFrom.setText("From:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFrom;
}
/**
 * Return the JLabelName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}
/**
 * Return the JPanelEmail property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelEmail() {
	if (ivjJPanelEmail == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("E-mail");
			ivjJPanelEmail = new javax.swing.JPanel();
			ivjJPanelEmail.setName("JPanelEmail");
			ivjJPanelEmail.setBorder(ivjLocalBorder);
			ivjJPanelEmail.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelFrom = new java.awt.GridBagConstraints();
			constraintsJLabelFrom.gridx = 1; constraintsJLabelFrom.gridy = 1;
			constraintsJLabelFrom.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelFrom.ipadx = 24;
			constraintsJLabelFrom.ipady = -2;
			constraintsJLabelFrom.insets = new java.awt.Insets(4, 19, 4, 6);
			getJPanelEmail().add(getJLabelFrom(), constraintsJLabelFrom);

			java.awt.GridBagConstraints constraintsJTextFieldFromAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldFromAddress.gridx = 2; constraintsJTextFieldFromAddress.gridy = 1;
			constraintsJTextFieldFromAddress.gridwidth = 2;
			constraintsJTextFieldFromAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldFromAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldFromAddress.weightx = 1.0;
			constraintsJTextFieldFromAddress.ipadx = 227;
			constraintsJTextFieldFromAddress.insets = new java.awt.Insets(2, 7, 3, 11);
			getJPanelEmail().add(getJTextFieldFromAddress(), constraintsJTextFieldFromAddress);

			java.awt.GridBagConstraints constraintsJTextFieldSubject = new java.awt.GridBagConstraints();
			constraintsJTextFieldSubject.gridx = 3; constraintsJTextFieldSubject.gridy = 2;
			constraintsJTextFieldSubject.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSubject.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldSubject.weightx = 1.0;
			constraintsJTextFieldSubject.ipadx = 186;
			constraintsJTextFieldSubject.insets = new java.awt.Insets(3, 2, 9, 11);
			getJPanelEmail().add(getJTextFieldSubject(), constraintsJTextFieldSubject);

			java.awt.GridBagConstraints constraintsJLabelDefaultSubject = new java.awt.GridBagConstraints();
			constraintsJLabelDefaultSubject.gridx = 1; constraintsJLabelDefaultSubject.gridy = 2;
			constraintsJLabelDefaultSubject.gridwidth = 2;
			constraintsJLabelDefaultSubject.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelDefaultSubject.ipadx = 11;
			constraintsJLabelDefaultSubject.ipady = -2;
			constraintsJLabelDefaultSubject.insets = new java.awt.Insets(6, 19, 9, 2);
			getJPanelEmail().add(getJLabelDefaultSubject(), constraintsJLabelDefaultSubject);

			java.awt.GridBagConstraints constraintsJLabelAdditionalMessage = new java.awt.GridBagConstraints();
			constraintsJLabelAdditionalMessage.gridx = 1; constraintsJLabelAdditionalMessage.gridy = 3;
			constraintsJLabelAdditionalMessage.gridwidth = 3;
			constraintsJLabelAdditionalMessage.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAdditionalMessage.ipadx = 25;
			constraintsJLabelAdditionalMessage.ipady = 2;
			constraintsJLabelAdditionalMessage.insets = new java.awt.Insets(10, 20, 2, 163);
			getJPanelEmail().add(getJLabelAdditionalMessage(), constraintsJLabelAdditionalMessage);

			java.awt.GridBagConstraints constraintsJEditorPaneAdditionalMessage = new java.awt.GridBagConstraints();
			constraintsJEditorPaneAdditionalMessage.gridx = 1; constraintsJEditorPaneAdditionalMessage.gridy = 4;
			constraintsJEditorPaneAdditionalMessage.gridwidth = 3;
			constraintsJEditorPaneAdditionalMessage.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJEditorPaneAdditionalMessage.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJEditorPaneAdditionalMessage.weightx = 1.0;
			constraintsJEditorPaneAdditionalMessage.weighty = 1.0;
			constraintsJEditorPaneAdditionalMessage.ipady = 124;
			constraintsJEditorPaneAdditionalMessage.insets = new java.awt.Insets(2, 20, 23, 19);
			// user code begin {1}
			
			getJPanelEmail().add(getEditorScrollPane(), constraintsJEditorPaneAdditionalMessage);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelEmail;
}
/**
 * Return the JTextFieldFromAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFromAddress() {
	if (ivjJTextFieldFromAddress == null) {
		try {
			ivjJTextFieldFromAddress = new javax.swing.JTextField();
			ivjJTextFieldFromAddress.setName("JTextFieldFromAddress");
			// user code begin {1}
			ivjJTextFieldFromAddress.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_100));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFromAddress;
}
/**
 * Return the JTextFieldName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			// user code begin {1}
			ivjJTextFieldName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_40));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
}
/**
 * Return the JTextFieldSubject property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSubject() {
	if (ivjJTextFieldSubject == null) {
		try {
			ivjJTextFieldSubject = new javax.swing.JTextField();
			ivjJTextFieldSubject.setName("JTextFieldSubject");
			// user code begin {1}
			ivjJTextFieldSubject.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_60));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSubject;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val) 
{
	NotificationGroup gn = null;
	if( val == null )
		gn = new NotificationGroup();
	else
		gn = (NotificationGroup)val; 


	String groupName = getJTextFieldName().getText();
	if( groupName != null )
		gn.getNotificationGroup().setGroupName(groupName);
		
	String fromAddress = getJTextFieldFromAddress().getText();
	if( fromAddress != null )
		gn.getNotificationGroup().setEmailFromAddress( fromAddress );
	else
		gn.getNotificationGroup().setEmailFromAddress(" ");

	String subject = getJTextFieldSubject().getText();
	if( subject != null )
		gn.getNotificationGroup().setEmailSubject( subject );
	else
		gn.getNotificationGroup().setEmailSubject(" ");

	String message = getJEditorPaneAdditionalMessage().getText();
	if( message != null )
		gn.getNotificationGroup().setEmailMessage( message );
	else
		gn.getNotificationGroup().setEmailMessage(" ");
	
	if( getJCheckBoxDisableGroup().isSelected() )
		gn.getNotificationGroup().setDisableFlag("Y");
	else
		gn.getNotificationGroup().setDisableFlag("N");

	return gn;
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
	getJTextFieldName().addCaretListener(this);
	getJTextFieldFromAddress().addCaretListener(this);
	getJTextFieldSubject().addCaretListener(this);
	getJEditorPaneAdditionalMessage().addCaretListener(this);
	getJCheckBoxDisableGroup().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GroupNotificationEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(371, 377);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelName.ipadx = 3;
		constraintsJLabelName.ipady = -1;
		constraintsJLabelName.insets = new java.awt.Insets(17, 17, 8, 2);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 1;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 132;
		constraintsJTextFieldName.insets = new java.awt.Insets(17, 2, 6, 171);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJPanelEmail = new java.awt.GridBagConstraints();
		constraintsJPanelEmail.gridx = 1; constraintsJPanelEmail.gridy = 2;
		constraintsJPanelEmail.gridwidth = 2;
		constraintsJPanelEmail.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelEmail.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelEmail.weightx = 1.0;
		constraintsJPanelEmail.weighty = 1.0;
		constraintsJPanelEmail.ipadx = -56;
		constraintsJPanelEmail.ipady = -4;
		constraintsJPanelEmail.insets = new java.awt.Insets(6, 17, 5, 19);
		add(getJPanelEmail(), constraintsJPanelEmail);

		java.awt.GridBagConstraints constraintsJCheckBoxDisableGroup = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDisableGroup.gridx = 1; constraintsJCheckBoxDisableGroup.gridy = 3;
		constraintsJCheckBoxDisableGroup.gridwidth = 2;
		constraintsJCheckBoxDisableGroup.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxDisableGroup.ipadx = 16;
		constraintsJCheckBoxDisableGroup.ipady = -5;
		constraintsJCheckBoxDisableGroup.insets = new java.awt.Insets(5, 17, 13, 222);
		add(getJCheckBoxDisableGroup(), constraintsJCheckBoxDisableGroup);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method must be implemented if a notion of data validity needs to be supported.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0 ||
		getJTextFieldName().getText().equalsIgnoreCase(CtiUtilities.STRING_NONE) )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	if( getJTextFieldFromAddress().getText().indexOf("@") == -1 )
	{
		setErrorString("The e-mail Address you entered is invalid");
		return false;
	}
	
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GroupNotificationEditorPanel aGroupNotificationEditorPanel;
		aGroupNotificationEditorPanel = new GroupNotificationEditorPanel();
		frame.setContentPane(aGroupNotificationEditorPanel);
		frame.setSize(aGroupNotificationEditorPanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object val) 
{
	if( val == null )
		return;

	NotificationGroup gn = (NotificationGroup)val;
	
	String groupName = gn.getNotificationGroup().getGroupName();
	if( groupName != null )
		getJTextFieldName().setText(groupName);
	
	String emailFrom = gn.getNotificationGroup().getEmailFromAddress();
	if( emailFrom != null )
		getJTextFieldFromAddress().setText(emailFrom);

	String emailSubject = gn.getNotificationGroup().getEmailSubject();
	if( emailSubject != null )
		getJTextFieldSubject().setText(emailSubject);

	String emailMessage = gn.getNotificationGroup().getEmailMessage();
	if( emailMessage != null )
		getJEditorPaneAdditionalMessage().setText(emailMessage);

	String disabled = gn.getNotificationGroup().getDisableFlag();
	if( disabled != null )
	{
		if( disabled.equalsIgnoreCase("Y") )
			getJCheckBoxDisableGroup().setSelected(true);
		else
			getJCheckBoxDisableGroup().setSelected(false);
	}
	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GEAF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DF8D45515818191D1828609C83F22E0B72E29D2CB3BEC1B6E4614D6E87113EE912922E595B5D55C46CAD7E86947CDC2FE00A08182A41018A8A0C90DA101E80690B5FF840291A304B06E8665A57312CCB219B73CF913CCA0421EF3FF5E3C193CC9G6BFE72FD07776EB9771CFB6E39671CFB6EFD931211BBBDA9BEA1031078A4427F154593927EA1A1DF06CD3CA0F0A1F2CCB2B17CFB93E08B39F157
	BAF806822DF2C6CCF2BA69CBC90576CCE82F3CB3A6F9A73CEFA383A9356F43CB10F13201B67F795F768CBD4E5DBB45B8A934FD29A98D1E2F83C4G8E1FABE59C7F3626CC0EDF407182629388D9510EFD6EEF4A66B88B349B81A2G225AD97FC570DCCFC79EDDEE617DDAA6CEA7ABFF35BFE1934AB14894D05A41F82D7C19C79E08A71DB85663D4CF6489855ABCG1CFCA2F9F14B47702CE86C6E7830CD69A9EA131CCE452949ED1DC5324D2EA9EA11D3516C6D76B6C933AB4E228ED571384A4B0F61632401F01476
	ED141C32038C5071F24F24D2790454C2BB550E3866B254A19A5ACBG62F4FC0E85718D70EE8398300BF9FF303F024F4BFA6981527238B3F1F310F9075959FC236C3EF907F97E1212C5576819CCDC0BD5C04B6E0AC9AE86E88250G42GF6D35F78434E3F43332AD52BD1FAFA94E7154BD56D30291FA8B659816F6565C06138D3F21B225A88E16B7FD4D32109BEC38163FB691A7918CDA28E89DF08BC75BD3274079FA7241858E4E9C4F1C2E49733455CAE1FCD5858FB4F849BFBF9173F2F7A0FFD169847EE9823A4
	A6190CBD7B310204D5DCCEDA37EF6C0AEEDC6BDFD19FA80477F86E9F8C3F0862C38CF86637AD940F6DD0202D6C963EE1FDDD685230EEAAC9AFE9B035C3F8CCD5025541F4F0B982F519C87DEE1A030D39D157653B949FE540B3DD76D0BC3617822D32A7A6197957588C0C6319500E86D8829087106E0CC9AE8428F48A9FBB18F960AAFD2CD1D235EA0F5DE113D542F8DF4A5C81CF592B29D23D532DC94EB639D9D26DD22BC3F6935EE7E5A65A20550FEFB5757DB69863186C14D59A2DA0DD6A11B5D9F533BE89D9DB
	300FAAD3EA133DC70681FADCC4485D10358D1E8E492D1DF159A4CD7675E0742DD938A63DD2B3C88481704E6C72236DE82F73B0FFAF40D85D8E37D0F99F4BAA240B727286C56976381AF0A6E409936DFC04660EE970BE5BA9EC7C039D084BD6E212CB95A1E7EE56D6031CDAFB9B2AAF297DA2469616E12EBE2E881F79AF2B70195ACD77104247CE187A8C759505794AD8A5B0F61E20B2D1AF56EEAD33DEE39C9B6138E29EEF7E396252579AA3F198BA33EFEEA62E55AC50E781408A5D46EB3372184FD6CBEED9F869
	27AAB818EC73D91C69B94512416D09E3A6F17D576B733807525739A0AEG1AG86GE63A04AD8BAC56A07C57BB7741362C26E3844A5F788F9A0359ECD2866ED3F22F6C54EAE5176C34410B1B536E484071F89C37ABBA4FF7A95E85F1DF830166567177E4E0ECCA1A265ADBBD1AFCDA56B43B3343C77FBE9D4B6DE9F579D20698EBF6864ED1690338577B7FEB8646CC1B62F44AF471F496CE7FA90DD959A1770052B50A4729B17CC35B516F3B65FE1EB3FE319D0B23DE4961417C43EA09D9925A51255AFB40A5B005
	11628B984F274EE49AEC34G569BEB22F5C04B3CA0E233EF0F084D3D976FA251DF6ECBB05BDB426F29CD70DEE0B1B37E02AFB619CEBF5EE12EFB944D9D9C6FE63A06D7E24E9E2A10E1F1E14DC21911B0D6B440128BA2AEACBB11AEF28F8F089A4561E0E6556D3DAFB39D57C73E6011A1E5E3127BD837E1E16656GDAB337A72317E622BD9D067CB8CAC5FBBEE191F5E39C68138A38FC15595426B21B30FE5FB9AC7AAD04F6EC50FE1F150BFE296E1864BCF730FEC9C7C43FCA68E38D5AEF2E5E4F8BFDC281E6397D
	7B31B5C879926B90335A3343E3AF72E8F64C6A1286016272E8F4CDD8ED99C5778714916796B564E2AB2ED33E867BAD00D593FE751BCD22068C6B1ECE164D589534F6F6E9CC37B04D67D7F9B7089A726EDDB866A220AD57048FBC10E95C472A5CEE254DCE3D082F5F97D9C63FE338362C8D70EC372BEE2D9E9203D72C351D6E213847F2B42FB9928F2771392E02F10BBD10AFBD087F3DA74320FB27076D152170CC24723A8EE32D51A62932D6C47FE7DBF7FE1754ABF90B5CFD10C40AE806AA2AC16AD1FCAD606BB2
	37E0AB53D12616671C854D228E3CFBDB2508657B27120AAB5B924C6A72311BF392D6F818ADB3FAFDB69DF8B0B9F14597FFCDE1737B25BE3F7350A73CD74CAF3EBFF0FE8B0136920071DDAC97FC29FD3066FD02EFCC23991F37CF70959D91FC6381B7D7673B6CB75E1ABE46176F95FCAF6BFC9500533C02AF52E2640B7432F5D864956B50D4F1901E92CD8AC56C51A82B6D0A5AA35B68E294C9FD9ADF09AACA66CB41772B0786556A31DEDF4D3046BB38FEE0B56C42DD28EFFABF2BE1576B7827A83E5000E773DBCA
	63875B47019659AF6A2E12EC4C5D6997E14F862885E88198G18F6D14441ABD90748B0F5EDBD44356A149C277DB6AA6E7B7F348BED85FB8BEF5F378B6DCC7796A26C307EF0209D66DC741F7B2200B6635BB9082FA8203DAA000F45403E7CEC111B8CA1D3DEFE2289766EBA3B6C3031DC3423D71CF353AF091C73D51DB167C4EDBE1830B1C84EB9FE090D5DF949979F057A3975499C54A182E8F38116822C82489E08C936G348C887B3F377B03616CCF1D2AC929815CF158CFEA8277FF5DDE8B2E534ECF8CEB6701
	81FFBED69B38DB0C35C158G0B1FB970DCG40ECEC6997B6DEBEA0EA4510726B537356C1FC01FAE6FF654F47F478D923502174AB21C369116B5321FA90DF208E0E2F4CFC32A4AD475CA7FDD50B2E6F2581216FA8DD5FD72FD35F780A61740DB2557743665D667A56F44AED5D550A176B7A2CA5150F35886430B360923A4727DD06FA6432404DB80B38DAE837DC96F3DA3C8B6B2B90E807834C86D820531E2534384B2CC642FC0E358823BDE38DBCD3A12F319DF2597CEC96EE02DFF0594CEE3D3B8247C889E887BB
	1BA51960824FC1AC764B33031FB52A324D4F9A47324D4F9A1FE4EFE1F810E13C019074B359C31FC54EE51B1DC5F268BC676F58CCE53B5D321BA6A3AC6CB86D85EDBBB1B9131C14FAE4EE23ED247270FA5E672826541468B65E5DBB54BDDA7A1541EB45F2EBEBAE6E5BFB01DE89E085F08104DE91B975556CE17734235EA1763467F2022F4B0A9C73F579D38E5626EE452336490136DF1D132953FC593BDE13FB50CC7C9E678D6457A435C39E34F66B5F4854E94178373E91ECFD599AA4169B5760209E672F96E41A
	473921E63F2FC544782CAB22E67FE2A3569C3150A6A1F0968C913179C17FD0EBDA9CB2F8CD19FE7F62275FA15DC7E29A0712D79BD4DE7791233CD2DDDE4840D0722CC1657D3D42A86FBDDDDE2C45A84F4179C378B3AC283CBF1E41B338C36AD7BC9A5458A35026494DE2BF0DG3EE800D800B5A146F3578F7A309EE9D7259ED82653AE390D44D15E1F1C953CAD50F68104824C82D8B8C25433D967717CF222C1EA159DACC0993E229A79FC55111116388547E35EF034C732BBB87E6DAFB06E755D20566E46C3AEBD
	13831E5557BBF79B6BEB4BC8D69F340C94FB4241DE31A738C60A586D4A8D9EFB97F24DE32FAF57BCA7FE15BBF4CE9C1D9BACE6585A4EEAB12EEDB46838E824EFBDE3C7862B5F975358D8B31255691BF47CF3941FF7038F4F6AF79B45E33B96E833EE9076E96A9776499EA5F03FEB94B8070E1BB2A0F0D1BAEEAD5D1753C6836FE801AB216760E2E8D76A38C93B84EF43E87D1C55AC3EB7CD835C225162FBD349F93F3A9ACFBF78BD49EE2B16BA9A28A733FC515B1C4A79534608FCB1134A2C05F68B00B6C6545B5E
	37C53D9D793D2924A164E353B37E322603896347B0FB4F9A632B37A3205EE6E3AEBBAB46DC81743DB7A26EF52B517F50EFDFCDE0758F23D77A5151976068E6202BFEF4547785BF7E19FE74A483BF1B5B2F7709392D79F584F17C68F0507B8B470DECCE217C59583C076C9D7757941E235B0364E89A49EC7EA7F70B9CBD8FE4140EC5DCFDEF2D49D8C6FD938DC000A783E08F2765437B7AF826AF73150769BEB0G78503142FF7615A1EEB634676938C99661D3AB460AFDE169C6BA071BE2121BG3CG61GF3G16
	5EA47A3CC47B2C9B476E3B70AEEBAF3C570EE377D52CCF574673A6F32960F2D02E91BF47508EB73CB75DE4DE9F9AEB3DADE43469D809BCD6836BC163BD34315EB32B870D40562A3B7FEA56CABCED637C574A8B6D0779DAB1F9F10DC349CB89D87FD1817242216D7113F77F40D07292096FDB813E4F847EB96318BCECAF9847F25EEAA27615913B71CE3ADDF1EA99B7237C7C4368B3CB205F72F1E23FFAB18D7BE810702DB763777C975FA63ABEC74E8F270FF10E99865F26F98D6461EF82DEDB3BF6ADBF8FB6DF5F
	37606A6B643BF44DFCEC8F69BD0BF9B462E6E64FF89DFF0562E79B70AC177F331E4B230136F73C389BFAE4BF465AD8E84F840882D8843092A07D9631976F4EABA6435451CD0A2B81B6EB07E1FBE573FB6E6C75FDA3FFA8C5644C59BF391B4C9933BABE587D4B5EDBD88C5772E7EC539E0AE773BBCF6BDA8DE821G338196GAC83D8254F2FAD2F104D8F4F60D51AA635F5525BDEB139E63B5C67C3E3314566F6FA37F1EEE937727D08BFCBEF9D1CDBF269F804249271AF6E2341B828BF291FFF748E516D711FE942
	9E59775DC9EA9FDF9DAFFCB8F00C63FC5C70DBFD7BE3EC571A9496CBCF37E03D592A2834GE275661F757B1B0540938B10B781760E896C3C4DEB433241F56A69B24157B0015529B6F8FAA7707953783C57B2DCFCA691DFFC06826F4C893EFCB1875EA7C2EC2E86E0FA7C2C47D73B5629CA0F48A36BBBE89D2A572EC82B3259D4596DBB7BB8BCDB7C7A40212F8B4ED0BABD055629CCF62D5CAEF99C9A6F42689B49CE1D8E326D744329639398C26A10390C9FFF06351161B6E7D0C7D6933F1996D893232D67CE90F7
	521FF84D68CBF47A8CD37A8A201B79987327FB51DDCDFD1051FF77EEB0FAF01E4F8E787CF5A29E2F389D766DFA8B1ECE39AFCF3FA02F03A41F7F164DAEB9140E378A730A8B73CACA8B04FFCF6FB37EBE4A32214C745BA18289655FA378B36A46BDDC46237FEB1411BE754D0478A0B29AB82FB55497A3EC8E15786BB21E1B874E7A5EC79B5EE7B5FF1DEF79D737AF4C7FC647E0F9EF717E60E72C27761B1F311E5DEFFE467A437E216F15DE599F7C9B772A7D46EF5CACEF271721FF0F077509GD8G308CE0E52848
	59E372513F7057B6037375D1DB875E2F60AFECB47D5BDCC170393A8A067FDDCEFF81661DD6C553149E14AF70D78AF03DB445C5118CB72AGED245ABBBAB55615634795E07CB864F60AE6B5F257EEF1BECB1FA86A665F26097359DE9DE759A4EA696209E26FF6525C9A826DE9G515897E085403AC9B14919G05G15GD6G9740A800F000C800A80005G4BA7891B0EFA874FAD4DD2975F61D99223D685A4BF60D1432A129DCD4BEB0B1CF36277DD2BC0D671ED621C78C28E4A9B7A5BF013DDF34855FCBB62F749
	72B971ED3873B6F1EED45F47B126C3FB8E40425B449975D21928EBE2811797A63EB77C3CCC1C832DE1C246AB34EFA834A3G22434CF3E570DCF7221C68BADA5344B9EB09BE66A82F4025CF9638A7F45C7149E25DDEABC7DC883427CD9636DFDC7A4E9036EFD495172CEA7D8CCBFFE1406364CFC7086EDF596F6D8BA6832262846461CF69EE56A4F568BC19E54163A237EC78384827F616411F1B7ADD7C8E72DDDAABA86D6DEED90F3F03E569BA2E5E5D2838692648EA7EA9B48622409673G182DCF6167B0DEC3FC
	3AE9389A428801B57E2A496C4C88A544DA264BC8CB70B95FE499FE4E13AC6639A05CB2B897FC47E21E8BE6D80C390061A2AC68739D90E7D63D5F77F55C67BA6EC1AA93F1E76DB62DD3474F21FAA17E380C838A7C3FD13D7AA84E4AF1BFB760BE6738F996DCB718F227228A5EC7AC74FCE6F7B8D87BB7340F5D894B285BE3B155516E128CFABF2963BEA7E2CF5BF3EE3833403A5B4D3E41148C4B77C6C01B5F91D03E3C5B07BE638BA805FE6C8C75BC4DB547EF8FBCDB7D11621BEE8FBCDBF95F97B968BC50665EAE
	6EA8C614DD5F19F042F5724D3BE6BEB6AF197EA6B6FFCA60FC1D94DFBAA5F03E2F69B93792E8B32708791ECBB30E1F9A4E6C1E4D1F4561035701715D64B5725572FED67EF484657B3D9FDF886F97461F91A6FC4CB75714633BDE2B30F44AE909C7306E30BBA1BDC98E7BC57AD320535051594172522B9F2199D80D7BF05A5556380C37398AF959773268ED1B68B88E3BD32EE13F6CA562FEED845D7F22C07FC561E27F8FABC5DCAC3457048BFB2FB0542AFFB63CDFFE7F1B2FCF277CBF0C51ED714DC9B33C7F6DEB
	7D16766A465EE179A647E0EB2A519AA2758E28556E906BBC2D8EF105502E50F17147C46DF7DC47CD778A5EF09D779FE785EF240EBBE4953CF3F45CFF6B3C29D385EE40AAF833F55CD3BAEF7ED4F18F90FE8E779E8D2BB674F5F8E7FB78AC6A33DE68B7CA6FFBDFE91268CBC48C74D763BAB27A4DE97EF7FFF7D1FB4C867EE8DD468F6B7C6F7E2655A0BF2B8BB20FFD1D3AA08E4640EF50FF033A000DBF1F5AE58D60D36F94F5483F577A46BBE13D163B0C4071AC7CEF18FC6365326FBA00EF5447FB6628EF3C0E37
	2E66EEB3A548F8A1A07390570749FE74044F76E3CB715E53A4F7D5C131630054D542D317380B7A44479BDAF7EDF9AF7AA45165C4FE6613734B3357A66751D3BEB96F1C7149F1D92FCD4E49E664E5FFCF563B33928C9879B2C95248A67E75B20FFE3EF42BE433AE2F1E96BC4BFA8F15A7BEBBA3B1012C2BA7492CG2CA76B300A2BA7C934482BA709D0GFA7163A796CA89G77E1C2F1DFA017751F45631261F5E6020EDD4F7BA2B6D147220A99049DC792C01FCDFE737B45DD6FBD3F6069E6F7A2C92EA7D998C627
	E5156EDA328DFF19417F14230E2CF7A9F0DCBB8DF8D056CD9FC9EE2D5FA1B7100C76D37853E537A65B2A44FE68564876869C69G9E518E9C175C1D9A1A65C883D96744BFBAA1DB9959A1B9BB8E304D71881DF1FC55F934ACF5CF827AEE237A2214A2DD4A361FEE5E77660965F392C9C29D5960D0A4DB1D54864714FA32DE6BD4E5F7276230F9DD9FD5D2C129FD293AD1D432DB1FBECE36886F71F4253B3D5F993FA9F6D39D98512B128334A7E60DA23D072D26D3D2E1A8D92D57700FE894F52FCE9B71067676A347
	F636A410F8BEC9DC02D42FCBA3496DD20F5D518FAF35541B686224E9202DAAB95D78A3D050ACAFC0B3C15287A83AEDFE7164FB7F72BFF4G9F2BC6F219A90175C0072C749C28EAEF070AC097GF68571B93AF86CD39450E72BF76276930F2DED85BF28A509BD4545747F927A7FC17C3F04E2CAA82664A098B70E8C79AF106E95F3264E234785AEFFD19D2C2076444B736FFE32A2EE854C2E0EA4519FFD42A2247BFA1D7474344A6A184849B99B43165D0CEB64C2D771225CF42F6E6FD89261B552531836F4EC9247
	E294A4EBA489FF16D0CE294903FC8E319B86BB8F22DF571790DBACA037517FF7DC33C110AB13726E52677116EF9E29DCE30C68AD20B17D4184BC71D23ADCA3697CF2B90F5FAE83EA1B6F1AB80F5F931BCFB1BE48945779CFD12CD8B2BB61B1D511671B197B3B5EEF5FFA3C6FDB8FACA12877607B37CF538F3CFE7BG4D76286D37CF53DA1F2628A16ECDB3GB6307B7702AB467B77D523B3035E610F1A413FD14D705D3F735F749D22FF238178389962EE6C410378E1A2583D56699E496150A4C7F7155306F7DC55
	7D1A6C3BF76EB804725D228FA7B21A73906A5DE56079BFD0CB87882A467C214D95GG40BEGGD0CB818294G94G88G88GEAF954AC2A467C214D95GG40BEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8796GGGG
**end of data**/
}
}
