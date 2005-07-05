package com.cannontech.dbeditor.editor.alarmcategories;

import com.cannontech.common.gui.unchanging.StringRangeDocument;

/**
 * Insert the type's description here.
 * Creation date: (12/5/00 9:57:57 AM)
 * @author: 
 */
public class AlarmCategoriesGeneralEditor extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JLabel ivjJLabelNotificationGroup = null;
	private javax.swing.JPanel ivjJPanelAssociation = null;
	private javax.swing.JComboBox ivjJComboBoxNotificationGroup = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JLabel ivjJLabelDuration = null;
	private javax.swing.JLabel ivjJLabelSoundFile = null;
	private javax.swing.JTextField ivjJTextFieldDuration = null;
	private javax.swing.JTextField ivjJTextFieldSoundFile = null;
/**
 * AlarmStateGeneralEditor constructor comment.
 */
public AlarmCategoriesGeneralEditor() {
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
	if (e.getSource() == getJComboBoxNotificationGroup()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxNotificationGroup()) 
		connEtoC3(e);
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
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> AlarmStateGeneralEditor.fireInputUpdate()V)
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
 * connEtoC2:  (JComboBox1.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmStateGeneralEditor.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JComboBoxNotificationGroup.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmCategoriesGeneralEditor.jComboBoxNotificationGroup_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxNotificationGroup_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxNotificationGroup() {
	if (ivjJComboBoxNotificationGroup == null) {
		try {
			ivjJComboBoxNotificationGroup = new javax.swing.JComboBox();
			ivjJComboBoxNotificationGroup.setName("JComboBoxNotificationGroup");
			// user code begin {1}


			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List notifGroups = cache.getAllContactNotificationGroups();

				for( int i = 0; i < notifGroups.size(); i++ )
					ivjJComboBoxNotificationGroup.addItem( notifGroups.get(i) );
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxNotificationGroup;
}
/**
 * Return the JLabelDuration property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDuration() {
	if (ivjJLabelDuration == null) {
		try {
			ivjJLabelDuration = new javax.swing.JLabel();
			ivjJLabelDuration.setName("JLabelDuration");
			ivjJLabelDuration.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDuration.setText("Duration:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDuration;
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
 * Return the JLabelNotificationGroup property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNotificationGroup() {
	if (ivjJLabelNotificationGroup == null) {
		try {
			ivjJLabelNotificationGroup = new javax.swing.JLabel();
			ivjJLabelNotificationGroup.setName("JLabelNotificationGroup");
			ivjJLabelNotificationGroup.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNotificationGroup.setText("Notification Group:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNotificationGroup;
}
/**
 * Return the JLabelSoundFile property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSoundFile() {
	if (ivjJLabelSoundFile == null) {
		try {
			ivjJLabelSoundFile = new javax.swing.JLabel();
			ivjJLabelSoundFile.setName("JLabelSoundFile");
			ivjJLabelSoundFile.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelSoundFile.setText("Sound File:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSoundFile;
}
/**
 * Return the JPanelAssociation property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelAssociation() {
	if (ivjJPanelAssociation == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Alerts");
			ivjJPanelAssociation = new javax.swing.JPanel();
			ivjJPanelAssociation.setName("JPanelAssociation");
			ivjJPanelAssociation.setBorder(ivjLocalBorder);
			ivjJPanelAssociation.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelNotificationGroup = new java.awt.GridBagConstraints();
			constraintsJLabelNotificationGroup.gridx = 1; constraintsJLabelNotificationGroup.gridy = 1;
			constraintsJLabelNotificationGroup.gridwidth = 2;
			constraintsJLabelNotificationGroup.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelNotificationGroup.ipadx = 5;
			constraintsJLabelNotificationGroup.ipady = -1;
			constraintsJLabelNotificationGroup.insets = new java.awt.Insets(43, 18, 7, 1);
			getJPanelAssociation().add(getJLabelNotificationGroup(), constraintsJLabelNotificationGroup);

			java.awt.GridBagConstraints constraintsJComboBoxNotificationGroup = new java.awt.GridBagConstraints();
			constraintsJComboBoxNotificationGroup.gridx = 3; constraintsJComboBoxNotificationGroup.gridy = 1;
			constraintsJComboBoxNotificationGroup.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxNotificationGroup.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxNotificationGroup.weightx = 1.0;
			constraintsJComboBoxNotificationGroup.ipadx = 9;
			constraintsJComboBoxNotificationGroup.insets = new java.awt.Insets(41, 2, 4, 16);
			getJPanelAssociation().add(getJComboBoxNotificationGroup(), constraintsJComboBoxNotificationGroup);

//			java.awt.GridBagConstraints constraintsJLabelSoundFile = new java.awt.GridBagConstraints();
//			constraintsJLabelSoundFile.gridx = 1; constraintsJLabelSoundFile.gridy = 2;
//			constraintsJLabelSoundFile.anchor = java.awt.GridBagConstraints.WEST;
//			constraintsJLabelSoundFile.ipadx = 11;
//			constraintsJLabelSoundFile.ipady = -1;
//			constraintsJLabelSoundFile.insets = new java.awt.Insets(5, 19, 5, 3);
//			getJPanelAssociation().add(getJLabelSoundFile(), constraintsJLabelSoundFile);
//
//			java.awt.GridBagConstraints constraintsJTextFieldSoundFile = new java.awt.GridBagConstraints();
//			constraintsJTextFieldSoundFile.gridx = 2; constraintsJTextFieldSoundFile.gridy = 2;
//			constraintsJTextFieldSoundFile.gridwidth = 2;
//			constraintsJTextFieldSoundFile.fill = java.awt.GridBagConstraints.HORIZONTAL;
//			constraintsJTextFieldSoundFile.anchor = java.awt.GridBagConstraints.WEST;
//			constraintsJTextFieldSoundFile.weightx = 1.0;
//			constraintsJTextFieldSoundFile.ipadx = 166;
//			constraintsJTextFieldSoundFile.insets = new java.awt.Insets(4, 3, 4, 16);
//			getJPanelAssociation().add(getJTextFieldSoundFile(), constraintsJTextFieldSoundFile);
//
//			java.awt.GridBagConstraints constraintsJLabelDuration = new java.awt.GridBagConstraints();
//			constraintsJLabelDuration.gridx = 1; constraintsJLabelDuration.gridy = 3;
//			constraintsJLabelDuration.anchor = java.awt.GridBagConstraints.WEST;
//			constraintsJLabelDuration.ipadx = 16;
//			constraintsJLabelDuration.ipady = -1;
//			constraintsJLabelDuration.insets = new java.awt.Insets(5, 19, 72, 12);
//			getJPanelAssociation().add(getJLabelDuration(), constraintsJLabelDuration);
//
//			java.awt.GridBagConstraints constraintsJTextFieldDuration = new java.awt.GridBagConstraints();
//			constraintsJTextFieldDuration.gridx = 2; constraintsJTextFieldDuration.gridy = 3;
//			constraintsJTextFieldDuration.gridwidth = 2;
//			constraintsJTextFieldDuration.fill = java.awt.GridBagConstraints.HORIZONTAL;
//			constraintsJTextFieldDuration.anchor = java.awt.GridBagConstraints.WEST;
//			constraintsJTextFieldDuration.weightx = 1.0;
//			constraintsJTextFieldDuration.ipadx = 166;
//			constraintsJTextFieldDuration.insets = new java.awt.Insets(6, 3, 69, 16);
//			getJPanelAssociation().add(getJTextFieldDuration(), constraintsJTextFieldDuration);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelAssociation;
}
/**
 * Return the JTextFieldDuration property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldDuration() {
	if (ivjJTextFieldDuration == null) {
		try {
			ivjJTextFieldDuration = new javax.swing.JTextField();
			ivjJTextFieldDuration.setName("JTextFieldDuration");
			// user code begin {1}

			ivjJTextFieldDuration.setText("**NOT IMPLEMENTED**");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldDuration;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setDocument(new StringRangeDocument(41));
			// user code begin {1}
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
 * Return the JTextFieldSoundFile property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSoundFile() {
	if (ivjJTextFieldSoundFile == null) {
		try {
			ivjJTextFieldSoundFile = new javax.swing.JTextField();
			ivjJTextFieldSoundFile.setName("JTextFieldSoundFile");
			// user code begin {1}

			ivjJTextFieldSoundFile.setText("**NOT IMPLEMENTED**");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSoundFile;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val)
{
	com.cannontech.database.db.notification.AlarmCategory alarmState = (com.cannontech.database.db.notification.AlarmCategory) val;

	String name = getJTextFieldName().getText();
	if( name != null )
		alarmState.setCategoryName(name);

	alarmState.setNotificationGroupID( new Integer(
		((com.cannontech.database.data.lite.LiteNotificationGroup)getJComboBoxNotificationGroup().getSelectedItem()).getNotificationGroupID())  );
	
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
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
	getJComboBoxNotificationGroup().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AlarmStateGeneralEditor");
		setLayout(new java.awt.GridBagLayout());
		setSize(314, 354);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelName.ipadx = 13;
		constraintsJLabelName.ipady = -4;
		constraintsJLabelName.insets = new java.awt.Insets(44, 9, 10, 1);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 1;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 147;
		constraintsJTextFieldName.insets = new java.awt.Insets(41, 1, 8, 99);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJPanelAssociation = new java.awt.GridBagConstraints();
		constraintsJPanelAssociation.gridx = 1; constraintsJPanelAssociation.gridy = 2;
		constraintsJPanelAssociation.gridwidth = 2;
		constraintsJPanelAssociation.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelAssociation.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelAssociation.weightx = 1.0;
		constraintsJPanelAssociation.weighty = 1.0;
		constraintsJPanelAssociation.ipadx = -10;
		constraintsJPanelAssociation.ipady = -26;
		constraintsJPanelAssociation.insets = new java.awt.Insets(8, 9, 86, 11);
		add(getJPanelAssociation(), constraintsJPanelAssociation);
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
	if( getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0 )
	{
		setErrorString("The Name field must be filled in");
		return false;
	}
	
	return true;
}
/**
 * Comment
 */
public void jComboBoxNotificationGroup_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( ((com.cannontech.database.data.lite.LiteNotificationGroup)getJComboBoxNotificationGroup().getSelectedItem()).getNotificationGroupID() == com.cannontech.database.db.notification.NotificationGroup.NONE_NOTIFICATIONGROUP_ID )
	{
		getJTextFieldSoundFile().setEnabled(false);
		getJTextFieldDuration().setEnabled(false);
	}
	else
	{
		getJTextFieldSoundFile().setEnabled(true);
		getJTextFieldDuration().setEnabled(true);
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
		AlarmCategoriesGeneralEditor aAlarmCategoriesGeneralEditor;
		aAlarmCategoriesGeneralEditor = new AlarmCategoriesGeneralEditor();
		frame.setContentPane(aAlarmCategoriesGeneralEditor);
		frame.setSize(aAlarmCategoriesGeneralEditor.getSize());
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
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object val)
{
	com.cannontech.database.db.notification.AlarmCategory alarmState = (com.cannontech.database.db.notification.AlarmCategory) val;

	String name = alarmState.getCategoryName();
	if( name != null )
		getJTextFieldName().setText(name);

	for( int i = 0; i < getJComboBoxNotificationGroup().getItemCount(); i++ )
	{
		if( ((com.cannontech.database.data.lite.LiteNotificationGroup)getJComboBoxNotificationGroup().getItemAt(i)).getNotificationGroupID() == alarmState.getNotificationGroupID().intValue() )
		{
			getJComboBoxNotificationGroup().setSelectedIndex(i);
			break;
		}
	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCFF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD4D45735EAD009A189A6C429A1913FA1CA937BCADB5A5A37A835F966452E58D6DFECE3DB5A7A9A5BC75E332DCDB5F13DDC188F23E0A0E22A297C64A7C4C47C4490B40DFCD4B0E8F9F1E27155E59D188B0CDC660EB3F7E4D454374FE75F39F319991427EB453536F74EBEE71F3D4F5E7B6C334F3967A2ECAD0EB7E5E4EE95048C13407FFD10817F1D910403DFB67C86F1E344D4332079379360ED61
	B533911E51D0177035D4F31E505FEC0172ECA827BD1BEA5E863F7385D7F35DC97891020F856AAE6FFFF1FBF8BE5DBB10CF96ADFF5B10834F37G92GC71EEBC5427FC7066D9C3F046391B2B285E1F58FE93356D040F175D06EG188D105C435AAF0567C64AF9FCC3BDEFF7FEF23C3076DFF7E5EEA67DE87AC4286BE534D67EAC911E4D5850CBF8959AF3B8CD9414978110419B045357DB61F924233F77AF5D72C0ED374D69141D0A585DD7AB5A9D0A6C2E35C9B677C037CD91FBE537C374B4B4B492C4330AB8A1BA
	C537CDBAC6DB7B7A88AF51E821FD8B42D1A89B9D8477E983613F904A4BG52D57C3B07893E95FEDF8470B9F04C9F6DBA424754F97DD9A169A527A1B74418239DEC2C498E7F18E33C2FE73DCB6DB32B0058E1AD54ED3F14EA2E8168GF081C4836C20FEB1EF47FB70EC6CD21A650181595968F2B5C9F677A959AECA709BC65C25F05C27E2376C368B8233FD1931A608BC6100516D3BE3BA261304836887091F4E95D6FE79134C2CA0BAD919D01719F00969A26512DFA70C7725103C53AE857AE9A06F3F89DA5E33A7
	4886D3905EC94B2BB35772FEAC7DFE5E757D44564FD19F28025F995CBF98FEA9458FE9704CE7BBA91E142320EECDBF7AC667DBA8CB3BF11A10372FBD289EE2D39BB32D9213C112743264B7123EE3A546B3CB1525186297E870CC16E26A27243C986AEA86D24D4C3F920B489C0E06F2A240A20015G9BG36BB211D93FD6C77BB765E260FF5585CCA1357A159C53740E8938B7E84CF512738EDEDCE0FE2F3F60BE7EDEE072DCB92BD82EF731582A2DBD7G5DB7286FF7838FB6C995076C045EED8322A23ABD2C4DF7
	8A5EA6ED5CA22D35BA86C4E0B06092305F978A72097EEC9E65B3179DA63D3F852B7FE1813149955BD96891BA005FCCAFBFAAA67AEA07715B8194D58F278BC9FF1F08EE27A8B5B4344BF23F57E5A5A39196B9091E47525891893F631D286365A584E711D34DA5B276B33EF00B261F96C7B7915F663E0AF3ECC983095387E574197F6CC41FE959BCC728D9FEB2284F50F8A3B3DFF1497A3973EF34CFA297ABDFE8683C43F99C8A8E4A43679E63B1F857F898026018FE1BCB69DCG9912G1629BACEA9ACE1BE5BE473
	086825FFF5030309F63F46191C6FB6627AC27818D479ABA8BE9D7A5C6ACAB59FF1216EAA8EF76A5A5F0DB1866A6EAF1407D61E3B490369682BC5E4DD922F08CE25C5F409CEBB7C70703AEF95915F637336C7D6E93EC371AE18674DB031BCAA7E79A2B297ED0A62F6F4F995710C28A88EE72F3F7E7B1417475B65720788643522080CD19E04F92E367FC99119A35D3253A9D2E329A43C7EE7F40E0A12B8GC2B74BDE2742707FDECC7C3CDF3C4AE3442F0BC9A2F445A6F9C93CE1F94382DBB699035BB1GAEC0C216
	3058C566EF2CF13BC6C7F640117CA78E1E8BDCB897874BF0AE16DF1BA1247C23A2B358DA92BB27A5B36FB21B9765177DF3114974631260322751D841719EA66B3623A47F23F94B9985E2D8C04A426740D099692F8578F48238AE639CB83F1354E31C614E5FACCB92D3292A6B3652BCE29B7132D70470CC825AA72A7EBACA37686A4EF2DD327A4ED222CBC9938B8B5CF47DEB447CB092640987C8394C7439016B03355B51046D7A20BC86A05E9D2C5D4F54F6E93C5DBA5E2E5E435AB1FDE57D03648841F242DE2F23
	562BB8C84435A96054AE2F52E1834D303CAF53E461FDC4FBB03FAB30903DAE06F29A40BA8F7A4077B7E3FE97539FAF244D7AF3481CB6CFE1329DD47CBED0F29F66F74F15931E8A54C5A8E8339F16EA5718C60FC76EF650650D6BFBDC1956C798EEE259A6F876B85C9E258DA6318FED73E09911172CFF9C4D730131B42E113146825F25GADDE02FF7858D60D6C96AFDB476A60E9207D2DEEA4F9C0374DADAA3574FF362CD6DD32DD3179EABD03B061EBE9B429EDA63547484FEAEE17C555DB180CA255BC272CBE0B
	B95A4C7C231C6F12E72689C7EE65E7864B19A3F38BB3233CCC17C9DE3FCE077E49ECC83F70C6969BDFCB930ECF38827EF18547B7BBDBBBBE9760E38196DE41714DEF5ACFE2981D9C356C51A13AFBE47700E827032C358DAAFC040D341A8F116B55520CFC17C21F16C16CD7EE9EED3FCCF7C7F35B60F989322FAE39C9761D1695C70F231BFA4589376CF57D23D157E7FB68CE8F931573B5A64B244F270F8E7A57F26B6070F51D651951F4DE748D6A734FB80A9FB3284FBFCBAD18FF46C25D06C14C0DBE2BA4717622
	AF55BC84908310883084A05D0773611D7283428839E79B4CEF3753A61D89D8DCB087ACA7B600750017DFACA7BA256B012A07090DFABDD82E860E3DDCD7E6F40F8D232B5515DBF5F44C1E95D5859823B4D32721612495565B638ED1323318745CB15C0BDE3C0A316746F1ED6CD9183BBFB3BBC46C09653C132F7A67C90D3A37FCF39711E19D54ED3D8639BEC0AB0084908910F88D75FF2E62231174CF7D4AAAB7832E955601E67D1A2D6A6BE45368745CB90CCE2F67A557026939D75A91DC4FB813783A7F758F356B
	7E2AEBAC6ED92E271A8BG18ADDE4EC6DB9C3C0EF960DB4D239BCF41B03AA2DDD93A9EC8477697C5F40D0A003AD8G067B35196096C2F919AA5794BA5F56C1B9EF88ECBB047819945FBE4456E3927B983E4AB2D21E3E6EFA707DB569BF9C1D1FF76308BC026578E84BF0F424FF662B97AAC36733E2E570FC363FB2F8BEFB3972ED0607BE343BDA2C3FDA99BE5F9DDB99AC5FAD2463FC7ECF3934EF0FC7745049C39292DE770A720E90A46FBD8D9BDF2E23FC611F9BB772B6479439F91F6AD7AF9C8BF7B6A3853195
	0B85932A493AB8866A63G12G16G248FE18C5836EB4498FC4C97A686EF2C8AED173C2A60F679D3954929BC32575DAD6AF5DFD835DD2D73C71BB6C59CA0EAE2EDAAA93DE2F3770A43EC370752333AD074072AC2591759E0EC1356867B850CB93F2FA6FD8709B91ADC73379FDA384DF2EEE02E7972A61A43C379BC00F28367CDDAF6B81BC65D98EED3A65F31E62DFC87D47C7BA6329F12ECD7E52F827CEF925E6DA60CFF7150DFB2C09A405A9B5A3C7ACB03E41D69F15B86E0B8E7DCE237301A528AD9C8EB05F29F
	C0A4C08240621B187F0CABA1796949F6DB17A8B1C766F9D19321732FFA5A3A399251BF531AB68B869D51DCEE39C51B4B555DE2B15BFA8B6362AF0EE1DC6C3B05FE7CD0F5E8BFFC2CBA389FBEDE9DBCBE4C288E9F9F1E2E8E65BF4C2E2FFD2835EBB2483864165F16E937C265DEE732091F2C3D45F22C4DAA3E0B62370F7163D96E75F36AD724FC846A924620FE7E180D7A59BA96F2B25AD6B161B9089DF0F1E3711CFD5F4500FC05A4168D8DA75C8EFB132D371DFA92736B05F4FDA2744B4622DF7F067619B3AE55
	DC8EF0E49C66B13ECA4CE39267CE935A47FC92F48F15E65D1FE99D47F4619B674FE392A00FE1BCAB3310E79254270FA3B8EB1656B708BFFDB713658C2CFEC2C0BD3193245A1A7A6400FAA22FB7007EB78175A68DBD9B5B8B95B8368DAFA48852D7BE8C39BFBCF89F9BD3A7FFF61CAD934AA73D1145E3C9CF08D8C2E7929BFF4EC60CA5515047067B88EE7D3116B0B14344F5023098E8D68214713AB570FBD7861317794A66864223A5A2555C99017EB357CEF0C3D00ED6F179BD68D3C991983F4EE513B19847271A
	2BGBAGA400A800047158260F36D9BC1E6D6BD74353B2815A8334CE40B62FEC3A4865A7347AB1D571FE35B8232E1C90042EE3FC307CACB39BF9E507505FC69DCE1F3BEC8EC227BF43E336FAABA71C2D32F4366A1B90E82BA1A867F35FE232DF9B36279B8998DD64A8693EFD47F4ACDEADB4133890B901452B8C957FDD0A0F5160D9ECDA234626F828B3CE44BD24341B78CE9F14A3G62G12G16812C1908F17F63776A049172972B6CEA0705C15204F2B63E5DD923FB5F756DAC0C81C95F18A9A4CFD81F91EA1F
	E60C6467047C395ADAC671FC1FD6C346E705BA97C08CC0A2C08A400AC89C5F4477EA58784829E02322583A7B6869908E6E2CC39C742349624D467676C66D5852B9FF61FE765CFA7F7079D2CC7941BEC740FFEBBE9E0E0379F8BFF97F5CF4D0D07571739C54C7417C4705161756E7140550074479AE3C5F9F6FD7DF5A104572027286123FF449EE3A58327C65F08366A0EB01A6E7D22A3985209340B589F310EF590767BD4B6CC897B30965BD0970CC194478327979DECF387909677398D3D7GED7AA4B6BFC9D9F8
	A0557C6A1BEF3E79BDGA647775FA66739BC97529F64705CA76B23BF13B6A14FFBB0C79AE84BD57BBAA3FB1D76638EC964F59DD6E293FFEE252FB71FFFCB25ED713AF9F2456ACE5E5C92C06B2FE7395713597A5C0B6CA5F39E40F3365E93416ACB547A24D37AF3C6D2FFD02D0FB08723EFD56B3FEE8AC6FF9E6A0379A773454477C90979BD3B9B4177CC1A5F2F196E567B2A5071B257FCAFF930F8605D9DBA4F9D5A9DBC4F9D37BBF81E9B39BB7CBEB7FAF76877BAD3F7EB5F6B70F79507086DDCE02BA80085G0B
	GD6BC00312C2226D42031ACC89CBBE66FA57BBD7296D9C17E16BD21471A3BE764F74F5B77103DC9172CA872G699F71C5FB083D9459C5118CD7360768486D686DD3D8D30E2F5EC37CCD92FBA81A65C2D5B4AFDB8B63B2C6E1FE34240B6036C339CA456D6B461C69E0946646AF7EDDFBE7A6EF5A4ECCDC8B75760E08E231AABA4A9FAB191E0B6BC95CDA8478B4G630329668200BA00D60073GAE008800B800A40045GAB8156G888F413EG206AA1F50DB9C8F25C3336CBFC75E4AF2028E5G49B78354B8EE1B
	0318072F5B5D6ABD08A3501772906EA9DEA9A47D05FFCFE3F5A8125844C33D40725D798D781EA671A15CE37C2E0870D885658DC46E07E1AC74DCEFCE317EDC2F866ABAG6C8F631EE7039D730AA140A5BD0C67FDADA26EAB529FC6BEEE1134AD0F06FD8C00B5BAF83C899D035E78D0D04731BC9B73F6A99AF9BEF982F133D55CFF1C42F34715AA6E1BE6EC37F5B262561830DD7BE434D9442150F36301C3A34F0D18C374DD937834752A0B1F0BFC61908918F2CF0FC7D46760130772D4DC1B27C376B848EA415612
	0FB7127731BDE05B1449C44E4F683C08005F5113F1ED7EE597E92300B0CCCF2E43C4CFEC7DED6D9EE97D5542CA4DDE06584E78885345FAD8FF593CA8A4E988173B6E030AD0BE8D5166A4EC397E5AF5C96CD62C36DE1566C0FDE81DB6540F2C53667A6071262DFEF83CB9DD9FBC5ED86B3571061F535713B9518B7330D3EDF7C1451DD3F15D34CF027B1B432E7429F887150B60DBC5429071B215EB1062BAB9CE51604E61F9E8BD718B98F21F6CC65A1B7554768EC962EB8CED63F0021B287A18C8E5F438EC9A3923
	D45CB98157CDC743C8F9F85EA34176F203A352694F5E995D82CAD773C87873ED0423500E6DDFFA683D846BA37AFD0DCC71979F516FEB5695E17C1120EE69A3983BD20A75F7918EBE8AF19B4075A84E79670C9887A29F459855C74F78D3203C82E04D2338FF4E94C7373F4B9BA5DD5B9D53B1BD7D0A46D8CB0CDEFF7FC571DBE3747A7309283F92280B0841717EB8605CF4F58C4F6979B36F31D0F716E61D50521573F6757C599E12EE4D29F0633D9812AE452C25D3F83BC87E0C8BC97707307B6E85C168D8ECEDEC
	041FBC4FCF69A23916D39CACD15F979F07C0DDD5EAF758A439373426242E26E49FD978D9DE92BF284DCB6AB7FD90746E8B01C51C7F8A7EDCB8310C737FDA9369093D6BC849A767A48E2764F00A4D6C261F00E7CE7FC257793CA91003CC41DCE9EBA1419D01F247943433C416DB2E932F6677FF975D7BDCFEDB713D64416ED93FDC0C77DE4ECFE1772C95F8C6CEE1FA0D1F0231607ECA4C0B1227E0ACF8230A4C0DB5D036CC057D57D454598B8177E86B815FBE556FAF97278E779DB687D7525822CCE5F3502462DF
	2178C88D1E4DCD27856FC344C11D900B7C53C53F1D7E2079FDD33C77B61BFACFF9B0BF7DEDB6667E76D8745D0F6D187B7BD4DC1F9DF37F8895778D1A83AE0372AA95F7135E79D987E54B9790774BB2757CDE45E51DC61CDD453DE4467E12D45C758B585FE2953772027A9ED94565286F164B2761F91B1DFE1B20C0B9FA9A6619939AF78B2177869DEE59A53A152B8CCBEF0F717873538692D79412651338833FD90E789A1D7B8B207F0553B0C75CDDE8423682463265A4B5643EE6AC41F5A09DE84A9F47F951DE02
	F9717947F1DECC26EDE3201C8830C845F76EA47815D0DE8B1093077E7AD025BE5616477967C8FDDC287972FE8DFD5F9E273FA753C87176B87DBD190CEA3CA7B384F5E9F16A3BAC3A4E37BC11EA6EFC82F1DB28ED06209C2D625E4FD24F5F1FC059FFE1A1B8BCFFE738D7A9EE8534D92736D3C23E1F571E23D785B9C33F7D3977E0C89E5A7373515E27E53E73679CBA4F1E849FGE0FE326E9875933A1F38E53F1D7D841E67697793D668131C633E69BF4FBB91383FF9054E5F0B50CEFA92FD773132407D4D0E93FE
	F9DEAF3B1D7345D07B1B38A75977C1C49E56DF5169115E7DE84FAB931E8CFC1F30904A27783B9FB69E017A54E240AFD375F9DF2BDF7E5FDF98C97E2CB0729B27271AF78648DFE69EC97EAC0D7CF9538365AF1F6E171F6FEB1A891DFAFE45428F2FF3B51333A80793362BB649F10D1EF11E01064EDEB64E3AD30200391225104899A4D7EA048D1E842952BE1EAAF1DF5FD4B23A3CF97BA8697EA7A4DD5D7460F31645A2930DF9AD53755F3564D23CF53A7E3B1629A5F81EE3073A4553F15F3304362F09576786AFD1
	7C51F8FDEEF0E1A76686D628CB0E4798B60766B185B3D24DF5B39037F89746B56B8C0CD707EB025FF1BAC171BEE897BB031D7F733B0E55777E0ED34908BC7EBFF71C189F2EB78EFF7F11E7523F7FC81E21FF7FD1ED9E7E7E6328D93B47DC82B442CC48A7E67277D06CEE08FA3F238670C781AE820C81081F096771459B75677504EF0ADA3F28DCFF6F1654AF1B0971A46A2F7EF9F640FE3BFB92C67BEC8721E53FF71E717763AE3B33FEDA2C04B674595D754F7CFC5E6F747311AF5C991FCF6D61793CCE73E11E9F
	166077C4645C1279D876CE7CB608155F21E5FF7D93A5777E3B1E2F5EB59E4166836371031DF87EEAD0F1BF21385560B7F933F03C761D5AEFF16E06CC43E53931D37BAD4E5D66417CE8E16570797943CA7D7C2C07F1374F524E4F1F6D42B6F6407BGE2811281D64C4279F72DC6BFBF093F2CD36B9FAC553FCFA375B933B107EED53F0DA8190D67773D25646CD4BD3FF7F48D54FA2F8C545A3A947A71EDC343E92FA435736170B36F9EB35A34F3B6665CB174FDFFB41493GD2D41EEE75DE4F0A59F82FA7FE583D1E
	CFB23477F2FEB06CDE4F9F84ED3D51223F57F3AA60DE4FA10B7EDECFBE27E7B21DD56F5A644DC1B95FA8477DC5AB605837AA392538E79FB3072DC331F35879DD60B7AD474B315D32B9AC97CF5735E3BC56D722AEEA66225EF66D22FB88A88F81444CC5F9CCD558B6F1AE6A6DC0D5787BD0E2D5787BD0D32B425F07FA3EAA54FDA8A65317EAD0EF2BD4B99715225E8A1E4239FC0A1EE9D91F3A3DBDC61FCA57EF3E531C0965A616AC7A6D6ED37A334637A9BE72A97DD963B4B36EB1E320EE1D4AFF700E6F08B03A3F
	875C3FB81F403F5D644F0804D071D7288847AFB6889D133F3E94F32EC4E833AA81F3037C7251FC57437A144B314F0227A1BFFB9A7BBCD9B95AEFDE78795D30BD776D5974F1BAAF221E565BF4B64547BE2D37691EDDE85359D0E71907B65DD4B5BA1BFE35C6CBA74DE33698431FB173C259747952F07C920250319C41EE9AFECE7A20894FC9972A63F90BBC705CD273BB7E1E5EC7E0BC3EFECFF930B13BE9ECDE8C63DDB1CFBDA3216F565367E94F5CBC6A7B768DEA3BED9508AB190F7A7A4BB0FB5C5E379D6D4368
	EAF565167941683C652363F75FB0BABD3F73C1794D58657701F91A5F87F65DFBFFE83F27BC18CD3FD7037E204CC7BBFFE0C0FF705F3F309B507611EA3B34D244A54E47BDCAA1F9854CF335D5B501E7BE0FD35FDB8AEDD74E4778F75A00E7BEACCF1B540DF9DACEA2789940D1000BGE3GD81B5A8A759EBE6012G16812C85587AC56C57DB2A7F760361934B755FA3B059DE27FAB88874C73F08FC2E2BFF5F458538880059G0BGD629ED7ED80AED0C4F241A37835483F482781EC11DDCF578F5E26E3E5DFD90DB
	7F97F4E31E927B8C66A9BF6D5667A99502B64F48336B73143F8554B75975F9CAE07E275875F98A764FE432D620CCCBD51946D568E55A9B50E7D205DE26B381753F2A504B149B1033EE2F544BD44C6B7999F73FDF473B8DF7266399869C0F65D99C4F0B867DF80A83644DB06A47F3AE207E00D1BF1E7E00FA5198BE07AEACC3190E2AB2FDD22617A9B07F3CD196DE474F1669E55A9B20631F1769E59A4B69D9BC7ACA7768567AF3E1733050F4F38D2323ABAD3BD3BA7637231EB3AC048E92DF95CC9E37102B7ACE9B
	3D0811F665C0036117338C190231CDB0330BA9ED02115CAEE993CC7472C91BE0D0E4170F7CE195B4DEB7B1126732F013F827C0C29D6C88043CG6CD9EE8CF2E0EED18493793C2781A4589C603D5F1D717E2F167C742C47A0185B04F7C9D0BDA33A69F92CE8A75F4571BF4EF2DC5868129DD089F8904FC39FA60FF2D5925B052DBD1F12BFD060D1C4FBA31E74FA94619D7A9D74DEF219ECEF2B4D5327101BB8075B8523137C9999E18B2B16EC4E5E3D6C5877B09DE3C663C5AABFD120G7266D3F9C9AF35EAAF795F
	4C2D58F9F2D532C148BCAEEC12E41B7DB8A439323BCD58287439C5CF1FAC59FD2E0F0F520EAC03962E8683F437C39DBE99ECAD39E52B8A5D6FFBA2EA736A4D47C119BE37301736A4EBC82D5A425E5CE7F383AB515D2610BF0BA33B4B553A31C5CA6577CEECBB1FA9E470C192D3E479DC0AE06E318DB8242B702305468AEA24EC8524F55B1C9E121603E4A5BA4930CAE5D07B6873F50FBD7D7A7FD286FED2C5A8E62A84523D3D22BC30373127476194558EC02F50FD215ABDE9D32BEB3345B7791D134B5F6C82BFE8
	918C83F5F5747FFD747F7D647FFD94330FE2766D87652E91427E53577BF04C54F95419C04CDFFB9CAC287C785567E73EFCE4CDBA0C6E38E0221F080391727C2DCEFB873AC4770444478A33E3529EA0B6F291D77111FEF3FD2A5FEF62FECFB76C2071C6C9F4AB600E795A0B5CA574A6F703A2E4531B59A564EAB614B662DD6B865A1DC9670E99943BF118DF9989FA73B06B1A599DADDAFC1750109FF93438057E7F4E9DAB11D0EDE39D2AE3AF738595D688A12CA1D058FBA3D30D5F9EA4F8EE6176587D79137400DF
	D2DCAE69D1E3C0F425AF3B486F4F1F7C077D7237F279F38264BF26115F8C8E2F791C7373B71A164F21AF1F74317BD7553734772F560D5F9E727ED57A577837BC497AFB612F52F355A3006FCC467B9B9FE56D91C25F03BDB3E013A445A675B7BA6D64CEEC53D5C5745F7F7FDD96695F03EDF8A52BDB1FC5643E242179BFD0CB87884E27580B639AGGA8D3GGD0CB818294G94G88G88GCFF954AC4E27580B639AGGA8D3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0
	CB8586GGGG81G81GBAGGG9D9BGGGG
**end of data**/
}
}
