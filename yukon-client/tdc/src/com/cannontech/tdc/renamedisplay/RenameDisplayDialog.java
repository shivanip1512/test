package com.cannontech.tdc.renamedisplay;

/**
 * Insert the type's description here.
 * Creation date: (2/28/00 3:35:33 PM)
 * @author: 
 */

import com.cannontech.tdc.utils.DataBaseInteraction;

import com.cannontech.tdc.createdisplay.CreateTopPanel;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.utils.TDCDefines;

public class RenameDisplayDialog extends javax.swing.JDialog {
	private String newName = null;
	private javax.swing.JOptionPane closeBox = null;
	private String previousName = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JLabel ivjJLabelNewName = null;
	private javax.swing.JLabel ivjJLabelPrevName = null;
	private javax.swing.JTextField ivjJTextFieldNewName = null;
	private javax.swing.JTextField ivjJTextFieldPrevName = null;
	private com.cannontech.common.gui.util.OkCancelPanel ivjOkCancelPanel = null;

class IvjEventHandler implements com.cannontech.common.gui.util.OkCancelPanelListener {
		public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == RenameDisplayDialog.this.getOkCancelPanel()) 
				connEtoC3(newEvent);
		};
		public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == RenameDisplayDialog.this.getOkCancelPanel()) 
				connEtoC4(newEvent);
		};
	};
/**
 * RenameDisplayDialog constructor comment.
 */
public RenameDisplayDialog() {
	super();
	initialize();
}
/**
 * RenameDisplayDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public RenameDisplayDialog(java.awt.Frame owner, String prevName) 
{
	super(owner);

	previousName = new String( prevName );
	initialize();
}
/**
 * RenameDisplayDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public RenameDisplayDialog(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * connEtoC3:  (OkCancelPanel.okCancelPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> RenameDisplayDialog.okCancelPanel_JButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.okCancelPanel_JButtonCancelAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (OkCancelPanel.okCancelPanel.JButtonOkAction_actionPerformed(java.util.EventObject) --> RenameDisplayDialog.okCancelPanel_JButtonOkAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.okCancelPanel_JButtonOkAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelPrevName = new java.awt.GridBagConstraints();
			constraintsJLabelPrevName.gridx = 2; constraintsJLabelPrevName.gridy = 1;
			constraintsJLabelPrevName.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsJLabelPrevName.ipadx = 2;
			constraintsJLabelPrevName.insets = new java.awt.Insets(13, 25, 1, 103);
			getJDialogContentPane().add(getJLabelPrevName(), constraintsJLabelPrevName);

			java.awt.GridBagConstraints constraintsJLabelNewName = new java.awt.GridBagConstraints();
			constraintsJLabelNewName.gridx = 1; constraintsJLabelNewName.gridy = 1;
			constraintsJLabelNewName.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsJLabelNewName.ipadx = 4;
			constraintsJLabelNewName.insets = new java.awt.Insets(13, 10, 1, 139);
			getJDialogContentPane().add(getJLabelNewName(), constraintsJLabelNewName);

			java.awt.GridBagConstraints constraintsJTextFieldNewName = new java.awt.GridBagConstraints();
			constraintsJTextFieldNewName.gridx = 1; constraintsJTextFieldNewName.gridy = 2;
			constraintsJTextFieldNewName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldNewName.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsJTextFieldNewName.weightx = 1.0;
			constraintsJTextFieldNewName.ipadx = 175;
			constraintsJTextFieldNewName.insets = new java.awt.Insets(1, 10, 11, 25);
			getJDialogContentPane().add(getJTextFieldNewName(), constraintsJTextFieldNewName);

			java.awt.GridBagConstraints constraintsJTextFieldPrevName = new java.awt.GridBagConstraints();
			constraintsJTextFieldPrevName.gridx = 2; constraintsJTextFieldPrevName.gridy = 2;
			constraintsJTextFieldPrevName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPrevName.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsJTextFieldPrevName.weightx = 1.0;
			constraintsJTextFieldPrevName.ipadx = 175;
			constraintsJTextFieldPrevName.insets = new java.awt.Insets(1, 25, 11, 12);
			getJDialogContentPane().add(getJTextFieldPrevName(), constraintsJTextFieldPrevName);

			java.awt.GridBagConstraints constraintsOkCancelPanel = new java.awt.GridBagConstraints();
			constraintsOkCancelPanel.gridx = 1; constraintsOkCancelPanel.gridy = 3;
			constraintsOkCancelPanel.gridwidth = 2;
			constraintsOkCancelPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOkCancelPanel.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsOkCancelPanel.weightx = 1.0;
			constraintsOkCancelPanel.weighty = 1.0;
			constraintsOkCancelPanel.ipadx = 238;
			constraintsOkCancelPanel.insets = new java.awt.Insets(11, 6, 9, 4);
			getJDialogContentPane().add(getOkCancelPanel(), constraintsOkCancelPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Return the JLabelNewName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNewName() {
	if (ivjJLabelNewName == null) {
		try {
			ivjJLabelNewName = new javax.swing.JLabel();
			ivjJLabelNewName.setName("JLabelNewName");
			ivjJLabelNewName.setText("New Name");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNewName;
}
/**
 * Return the JLabelPrevName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPrevName() {
	if (ivjJLabelPrevName == null) {
		try {
			ivjJLabelPrevName = new javax.swing.JLabel();
			ivjJLabelPrevName.setName("JLabelPrevName");
			ivjJLabelPrevName.setText("Previous Name");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPrevName;
}
/**
 * Return the JTextFieldNewName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldNewName() {
	if (ivjJTextFieldNewName == null) {
		try {
			ivjJTextFieldNewName = new javax.swing.JTextField();
			ivjJTextFieldNewName.setName("JTextFieldNewName");
			// user code begin {1}

			ivjJTextFieldNewName.setDocument( new com.cannontech.common.gui.unchanging.StringRangeDocument( CreateTopPanel.DISPLAY_NAME_WIDTH ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldNewName;
}
/**
 * Return the JTextFieldPrevName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPrevName() {
	if (ivjJTextFieldPrevName == null) {
		try {
			ivjJTextFieldPrevName = new javax.swing.JTextField();
			ivjJTextFieldPrevName.setName("JTextFieldPrevName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPrevName;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/00 4:08:16 PM)
 * @return java.lang.Object
 */
public Object getNewDisplayName() 
{
	return newName;
}
/**
 * Return the OkCancelPanel property value.
 * @return com.cannontech.tdc.utils.OkCancelPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.OkCancelPanel getOkCancelPanel() {
	if (ivjOkCancelPanel == null) {
		try {
			ivjOkCancelPanel = new com.cannontech.common.gui.util.OkCancelPanel();
			ivjOkCancelPanel.setName("OkCancelPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkCancelPanel;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION RenameDisplayDialog() ---------");
	exception.printStackTrace(System.out);

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getOkCancelPanel().addOkCancelPanelListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RenameDisplayDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(451, 144);
		setTitle("Rename Display");
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getJTextFieldPrevName().setText( previousName );
	getJTextFieldPrevName().setEnabled( false );
	// user code end
}
/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	//newName = getJTextFieldPrevName().getText();
//	this.setVisible( false );

	this.dispose();
	return;
}
/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJTextFieldNewName().getText().equalsIgnoreCase("") )
	{
		closeBox.showMessageDialog(this,
						"You Must Enter a New Name",
						"Message Box", closeBox.WARNING_MESSAGE );
		this.repaint();				
	}
	else
	{
		if( !getJTextFieldNewName().getText().equals( previousName ) )
			updateDatabase();

		TDCMainFrame.messageLog.addMessage(previousName + " renamed to " + getJTextFieldNewName().getText() + " successfully", MessageBoxFrame.INFORMATION_MSG );
		
		// close the box
		newName = getJTextFieldNewName().getText();
		this.setVisible( false );
	}
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		RenameDisplayDialog aRenameDisplayDialog;
		aRenameDisplayDialog = new RenameDisplayDialog();
		aRenameDisplayDialog.setModal(true);
		aRenameDisplayDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aRenameDisplayDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void okCancelPanel_JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
{
	//newName = getJTextFieldPrevName().getText();
//	this.setVisible( false );

	this.dispose();
	return;
}
/**
 * Comment
 */
public void okCancelPanel_JButtonOkAction_actionPerformed(java.util.EventObject newEvent) 
{
	if( getJTextFieldNewName().getText().equalsIgnoreCase("") )
	{
		closeBox.showMessageDialog(this,
						"You Must Enter a New Name",
						"Message Box", closeBox.WARNING_MESSAGE );
		this.repaint();				
	}
	else
	{
		if( !getJTextFieldNewName().getText().equals( previousName ) )
			updateDatabase();

		TDCMainFrame.messageLog.addMessage(previousName + " renamed to " + getJTextFieldNewName().getText() + " successfully", MessageBoxFrame.INFORMATION_MSG );
		
		// close the box
		newName = getJTextFieldNewName().getText();
		this.setVisible( false );
	}
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/00 3:52:23 PM)
 */
private void updateDatabase() 
{
	String query = new String
		("update display set name = ? " +
		 "where name = ?");
	Object[] objs = new Object[2];
	objs[0] = getJTextFieldNewName().getText();
	objs[1] = previousName;
	DataBaseInteraction.updateDataBase( query, objs );	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G52F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BF0944715324DC78E5896F53E0490DC27986C139392130424DC89D745A9D402EDDD622BD34E140FD4A9F5A4274AB9A9A726D2A6166316D6BF10B06293DBE703AD9098F0C04606AA30CCC0BFEC8984DACB0B34D428C8A36D6C6EE8C7B32B19D9E905FE77DE77746C6CEAC60A89D6552B69FE2F7B756B776B573DA2FB5F4AACA9AC5ACBC8E189B17F4E949212F11C103B06F37F40F1E9E2F6A9317D9D
	G58C5D69EA806EF86504EAE49AE2DA2252F16C3FFA574FD9959252FC1FB8F79765E156FC043E51D89201DE9B852B87BBA4F7B79BAE5349F72D442779500AC4061F70B086BCFFB2ACDFC0E0967D0D8C44826A00ED96659E7624EC25F8B3094E0D5100DDF8F5FEDF4657975E74DF1B716E412ADCF9CA95A01FCECBCB99C8A31392D663706BCDA78DC88577ABE15936724C17FB1G5C3C07F4FFD48F5FF35EC8687C00BAD4B7A0A80A2A9862C0384E888C54E922A28C0981C90F4A42D8FDFDA76D3630EE0BA448EA08
	90B90C7CAB0A4BA9BFC2CEC37F850A6B3E00EB2E06768600BC8B3F3F817167A4308D00AC71FD7E7948B9F39F7E71C7C941530AE7274BBE67C9ECFF2B2444BE1F723CD876FF54A69FDCC15D67832DF2B03B34862085A0GB08F608D6A8B8D6D688A0D7DC633BAB424AA0D51E8139C502E2881D106F6FDBDD0CCDC27B820EA81C2183D9F779CF710E7B6E0731E7E547318CE32CEF15B2F6AFC18EC7A566522B2871DEC4ABAD934FC1069E24DE0C2A7EC6D2AE23735F38713FDB3F96D9EE2DF7B59852A2744E16DB5BF
	B9D614EF72A9302DFDBC0236FE027A40BEE8971A7E417039949F3561191F5E20F86C2781EDF3047B067F95AECBFB71B2D275DE3B239E16E6B796354ACC06BEB9D516A5546F964BEC4D92CB1665941FE943B3D9EAA99E7BEB00D6BD14DD4A7C2B298363F6827ACB81D683ECG4887A8D6E01C42FD6C6E76F77FC69F738A1A51941364002891B637694A2B70956306A634A93AA1A883E23720C9C23FAC6A449CF3798A6A20BFE91E177A7EG2C713128081AE0C82A825CA1EC8DD15359186BD7F66198CD24D41FB4A4
	4282C3D142798AD77640D796F463DAB4A098E2E28423CBD750A6A3C2B7F0848650E6FAD13D282F3330FFAFC01F25074D145FE5D1D3C4393E3EDDD5A331288FF7C2D6AB2867A3B4F79850DE2CF09D0FFC0238AD502FD4B91FB72E6E3671E91186D0FCC19B63B17654854C4DB5AA7719DF7B394F346CF8089C7F49A5C71FC1E8D0192F78555458F90672C439D83F7F025FE1FE0D6A9CC71F96589A630EEBF8EE837FD7ACDBED6EC4DB25015C4B8156DABA6E3EDA43FC36C950C56E259D9AB81898C8E81C49790506F2
	B3BE4AAC599F2438DC60D720F21D9DEB306F070F3F9DFBC9682CA9E98D0F394E6DDA0369263D834FA3F1C4D40C96B1AAAA81E868A62D2B83FD4E0C5720EA4D39CE71D1086FE688A85D4277F6E08C8A0621C97DB1C36C928DC3D2C289FA082E25477A2309544057D2BAF0EF6AA84437B5DE6F405898D095C524C632261874980DCDD19607C068E6B526988CBF6EC57F0E08E3E6EE1876E251B3A248B14CB32CC6D8ADD4609EB4E988CC0F290A44239837670B2BEDBABA84B82CF55A615B9765B1B8DA4BE370504DAF
	13F53D070B1C4E10258F35942D0B32984A0DA6E250147193E7596F20B942446BCC5674B318EB1C8A9566777BFD48EB8B2CD1BE8C3541B0777B17BEC1BA4FAD2643B72B324C54E96979171DD5E897F1B8A6C2CA46A4F659525DEFBAF727503ACDBDB27A56CE5423EC4BFFB59ABD8B9B2CBA906471829843CC179B06CDDD50F18FB472F13246464D535838DC0D0DE3BA689C463D501AEED0989162F57AA878D6FD7DA5DE3219B5DB964D3B98BFC7F5DE1B6DAEC37D74C1BF8E1021F3BB7E7B8EDE1B5D9F49A4F9ABDE
	F72DC133F4A6CB0E1E30630DDD3CB68BDD43B5AB0D6C52FD86573D51E5BF9F9AF5DD9D106851E46A2D56E737B3439D75ED07EFD052F4238D82B14EF5DC67C3F9716CB25166D97E12F5161F04F543G4C07BD325D07B3813F9120G20AA16DD6A21BC830D2D98EF11E6BC05E52FGE256DDEA0A99062AB0D4A37511DE01FE3C2296D4B5281A0F22726BE206A457DD441C5151BF884EF40C5928784065D41E3DFF8B4FF6C7269F9E6BB6EB368777B418FB49797AB2F2EEFACF11D38D1D3EF3FF515198330FB7163053
	44B7CBBDC7FF75E29953598F9A394E164318DCCB9FA71ADA5C7451913985DD3C7B4E47EE3AF065672E87F69EFC0D563C15A34E67F2CD8A7EB474D9AD38007AFF6BC8EA3D1AC1717E1154FA353A0C572BE120650C707A65F75D68D7A7C7A19F8284GA6G16832C9D65FE7F9D5FA9B2C7FD5886F7B1CD91642E24034074F5791A0765EE339F3DD642F3B761672227B135765FB81A3C77ADA9FDB6EF570CF9EF26742B63497318AD3F9CBFCE5C6F13E8AF9D522B5D54AC8EFFD8E60F4313F1D68B7A609B86E0796AC3
	195FB15362BCDF2DB80F7AFF8C7ACF816447397EFDFE4C15E48C728C40E9GAF0081108130E60C5B6077577EBC178D28FF7954E64035C26EEECEBDE3AD1D655C222E1F19D35739E34973D85D3C1E666FE701D6F5936EA19477C689628EC17F1C05DBD60A38BE689BB7F9CD36388C4F7DB42B7FF95A475C477AEF16B95520FF8F60DA497BBEF35B573057484C3EAFF73B57EC25BD4EB5DBE50FF34D363BE79743838F7B0D0D53DF6F193D26BB5863D4536D27F2BF7E6ACE4ADB57C51DBADC0BE088A66DFF0CBF9207
	5A6EAA14B8661E77107A46ED66180B065AFC4A0A47670FD59347DA00469E0B2B174BF89D1FF91347D5F4BB4655C668BF87D0BE4E7D22C11E6D9D23E6FCE63EE57973B5FA2FBFBD4E72E721052F2178869B1E6555C74E73772AF6202D9E67717AD73F3BBDC53F33BDA3FE2CD5F4B5268D0829B6537C5596AD9159ED06B8046AB56BCFBA5F903410B84366F752790C66B6FF115F4DAF186DBCCDF65B1D36707FD50CF68F08C1A1A603AB2A3A5891B52F7428178954536F0A396D76CE70B73DEF0CE21D9904CBBFAC53
	9595874821C99C7BFE894F3199B0F6A540FA004DG45137CEC7F8665E1C8060C3EA5CF629B2852CBAC199E5D0FB52ED9C9B6E3EA47D8D0C49E534565297E31FAB261936BA75D7CE3B37337C9668795967E37947F028D4F7CE3046231DFBD85E76B944FF5BDB24FFFF9D35CE777DFC079D97BEDDB7B517337BB7F4C4C6D9BBE53B518CDF614204EED61DD57A3A981F5944C03AFC706DE575256656D683A585BF13537399D9A2CD64C9C353FAB1F5DFE4671EE06F0F21A592D457C2EDDD8EB5EA17C743C41B993C0DB
	B3CDEDD932B7C54683B0208E3E0C7EEA291D0E31D1CB6C7456A4BA4A3A50E3275FCC22234C474C79ECDFCF9E66F71502A733087C6D8FDC6FAA8566FEEA524CDA383B1674D59714B1BEBF7E0BDDBFD54B8E94953A70711B7383E909DA5ABC0F4BD1BFCB813FAA0D7B68796D5CC71FCA63BE7A4B6D3827AD502F3A83EAD100F6G798ECEFF015253211F853081A07F8EF687E474096D3762E7489F5743F5791A6E3E7C0F3734C68561E79163334DE55C39BBD1B70A5CB2073F17597815431CFDGEF197D13507EE9A1
	33912B83FF5D073A1E87784CBB7999722D5EBF4137DB98B42B3D0B8149D0B5DA6E81522B2910E50DB10625B7F8F37F5FAB3935B77CFFAD637E38663B8F124796ECAD2CF07123F5F7B27D67195FFCDFAD45333D787BB1FFD75D85368270820481A6G6E3F4B2AE7FBD1C62CDAC17CC64390864274564C0B59EEC99CCD2071F0E03C4787D0D27337113E43EE7A818E17A04F305B16CF88911E035EAF47F7A3D998D3E3861CC5735016ED657C3C49827956816483945C4576F6030E39363D5AC18F49796DD89C5B175A
	05FE38B2E86288ABFC98ADB83E4322DD95C713EA222F3C033965128FAA020FA4D18E24522F3E3DBB091E4AFB6DE5641DF4D9017EA6AA77098AFB8EDAB90F3D9BEC00AF238FCDB9518F4D67F4E152CEBFBB1F517BAC7ACBB71D6846FC666B78DED2CE785FF3971CF06C6C2E456D1AFE7833CA6E077B3E7AA55272745642DA97BFCC1F4FEC34F1FEA21F9D5ADAD0467C6379BE44D9758C8B9153F7FE5F07F2CB0AE4C005A05D2445CA978CD4C24C4F6A03C82FDAGBE8C608588G4497F07F1D928EB27FF5705D0B01
	10484FB46B370AFE77FAB05CBF77EFA8437D780E542F9A06BA047CB9FE349FE35ED0239449F0937D28C7CD8A058DB61457007D28F7D98CD2B42BCF0EEC63756B7D30371C853CB6FB2F8F6F25D6BDDEA70C9A75759FEBD220C988355368E1672A3A0D1F2B458BF9CD7EG65F98E7ABEGF9A1FF1B0B3F4D6D3A6A61E524BD6D3263F91767FB3FE85EC2E64F2C05893BEE763CE81EF703913BFFA04D6D5C5CEC4EAFCFCF7061E7B15CB902AEF78E9A25ECFF018ADE37362673FDCCFEE4FFE3DC3B737DA237BC19E62E
	1BE9DBBF8C7BE03CD7CEF15EF969DC6F070F6EF316490AFEB6F769A4FFE3283E1B3FBB1C9E67B86F5D7CFDE1AC0C67D4BA74D782ECG48835882D079396C529A0053G2DGFDGF10045G19GEBG5683EC84F876F35C6F43A256B0EEE71269A05479B5C1C277673F93865D7D3FADB8377FF786695BAA782DEFACEA5ED7FA02184B55E0D0972DB83B912C32F0ED3AD755A50CECF6AE1E58F6EB3F09FFF80B730CCFBD0F5DA5F651BB83D914FA87FD1D62AB97255EC1D7D8B1DC8D3409C55C76FF9CC47F4E03FE81
	405E45104BG5A81420B39BDFF93BCC966F8D7722951F6B01CECFBCAB0FFC70DA07F89603594E0ADC0AEC0BEG3907737FB0F4EA96FF69D4D5FAAB438B9A380B2859BC0769D02AF052615EFBD8BC9D3DE7E69DE87EEEA8E37EBE87F4BFC094A09DA04B12EBE1E84EB74D0BF147B7CDA657A3D3CEF2EDB0655964A0971B774064ECBE711C6B3CFF1A18EDDE713DB36731FD5E93F60F3B7B43F3475D83E167F3E7C5F866391395F6BEF73E9E361FBBE6ED9B46FC9702FC613746BDE6612EDB38FF21BC9157A3850C30
	057FBE158B712DA2AE48710FD339C6A94EEF62FEE443DDB7F17F91465C81DB8E2B9A1F3BB10CEF0BC1C916D9FF939DA3A910CAACFD7C0C4AA8C5851B5C3F30F057ADFB3D0662196D9ADB7B11ED1F7DDDBE7BB3DD036D794DC1F73FBABC687C3E7527C167774A7786E7FF0F7CE0507DB7660641193FB147A36E32CDC76676797408334F5F9B1969737F90F17679AFC66CBE4F6E53E934162806D8BDFAAF3FE37F3C025F31CF5F4BF35333323B7C4F4B4E3A5DAABB6B76E5F976376032A1B75DB37956CDF11967D9B2
	9F1866B2E7D838B51356FB16057B4E84471D3C0F3F5BFD2182E34C008D303AFFCC4158E0F76A020A596E547CAE4DA18DF8A2607FCC527A75BED6C3E11B71FEC4CD70BEBBBD57FDFDB65E1977315A2F5062776464DC7C6C722E3271C37CBAE877965A797D5B44DCFB37734BC96117E763B7730E45751193CD34AF228BB80C65773130BEBB7D2BD85A91767F2AA7EA7E937778ABD2E210D7583FB454187F5320EBE4A73FBF543551CC1AB7F22A5E735FABBCC524380D1432A8EBA34598AAED2404C6D29B71C01445D1
	DC54BAE6E1741A8A036C403B2F2446749A3CG9B249C6E4A34298F13A9424BA2AC81704A3FA3890BC13DA889DBE1E2F1FCE19296B1E6D145EA7EE35376B7369766D73B3EC1CEACE1E7F14612C44D4FEA12AF9DC4BD66GBEFF890F757FBDF002383FE1F48D8932EC88F224D18960FBC65318A1A6E2BB73A0725779981348E87FFC90659E344D79FFD0CB8788016076D37E8FGG38ACGGD0CB818294G94G88G88G52F854AC016076D37E8FGG38ACGG8CGGGGGGGGGGGGGGGGGE2F5
	E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB890GGGG
**end of data**/
}
}
