package com.cannontech.tdc.createdisplay;

/**
 * Insert the type's description here.
 * Creation date: (1/24/00 3:50:10 PM)
 * @author: 
 */
import com.cannontech.tdc.utils.DataBaseInteraction;

import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.tdc.utils.TDCDefines;

public class CreateTopPanel extends javax.swing.JPanel {
	private javax.swing.JComboBox ivjJComboBoxType = null;
	private javax.swing.JEditorPane ivjJEditorPaneDescription = null;
	private javax.swing.JLabel ivjJLabelDescription = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JLabel ivjJLabelTitle = null;
	private javax.swing.JLabel ivjJLabelType = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JTextField ivjJTextFieldTitle = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	public static final int DISPLAY_TITLE_WIDTH = 30;
	public static final int DISPLAY_NAME_WIDTH = 40;
	public static final int DISPLAY_DESCRITPION_WIDTH = 200;
	private javax.swing.JScrollPane ivjJScrollPaneEditorPane = null;
/**
 * CreateTopPanel constructor comment.
 */
public CreateTopPanel() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 10:14:08 AM)
 */
public void editInitialize( Long displayNumber )
{

	String query = new String
		("select  name, title, type, description " +
		 "from display where displaynum = ?");
	Object[] objs = new Object[1];
	objs[0] = displayNumber;
	Object[][] values = DataBaseInteraction.queryResults( query, objs );

	if ( values.length > 0 )
	{
		
		for( int i = 0; i < values.length; i++ )
		{
			getJTextFieldName().setText( CommonUtils.createString( values[i][0] ) );
			getJTextFieldTitle().setText( CommonUtils.createString( values[i][1] ) );
			getJComboBoxType().setSelectedItem( values[i][2] );
			getJEditorPaneDescription().setText( CommonUtils.createString( values[i][3] ) );
		}
	}
		
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 10:14:08 AM)
 */
public String getDescription() 
{
	return getJEditorPaneDescription().getText();
}
/**
 * Return the JComboBoxType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxType() {
	if (ivjJComboBoxType == null) {
		try {
			ivjJComboBoxType = new javax.swing.JComboBox();
			ivjJComboBoxType.setName("JComboBoxType");
			ivjJComboBoxType.setBackground(java.awt.Color.white);
			ivjJComboBoxType.setVisible(false);
			// user code begin {1}

			
			ivjJComboBoxType.addItem(com.cannontech.tdc.data.Display.DISPLAY_TYPES[ com.cannontech.tdc.data.Display.CUSTOM_DISPLAYS_TYPE_INDEX ] );
			
			// ADD OTHER TYPES HERE
			//ivjJComboBoxType.addItem("Other");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxType;
}
/**
 * Return the JEditorPaneDescription property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getJEditorPaneDescription() {
	if (ivjJEditorPaneDescription == null) {
		try {
			ivjJEditorPaneDescription = new javax.swing.JEditorPane();
			ivjJEditorPaneDescription.setName("JEditorPaneDescription");
			ivjJEditorPaneDescription.setBorder(new javax.swing.border.CompoundBorder());
			ivjJEditorPaneDescription.setBounds(0, 0, 354, 117);
			ivjJEditorPaneDescription.setMaximumSize(new java.awt.Dimension(454, 17));
			// user code begin {1}

			ivjJEditorPaneDescription.setDocument( new com.cannontech.common.gui.unchanging.StringRangeDocument( DISPLAY_DESCRITPION_WIDTH ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJEditorPaneDescription;
}
/**
 * Return the JLabelDescription property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDescription() {
	if (ivjJLabelDescription == null) {
		try {
			ivjJLabelDescription = new javax.swing.JLabel();
			ivjJLabelDescription.setName("JLabelDescription");
			ivjJLabelDescription.setText("Description");
			ivjJLabelDescription.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabelDescription.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDescription;
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
 * Return the JLabelTitle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTitle() {
	if (ivjJLabelTitle == null) {
		try {
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setText("Title:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTitle;
}
/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelType() {
	if (ivjJLabelType == null) {
		try {
			ivjJLabelType = new javax.swing.JLabel();
			ivjJLabelType.setName("JLabelType");
			ivjJLabelType.setText("Type");
			ivjJLabelType.setVisible(false);
			ivjJLabelType.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelType;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
			constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 2;
			constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldName.weightx = 1.0;
			constraintsJTextFieldName.ipadx = 151;
			constraintsJTextFieldName.ipady = 1;
			constraintsJTextFieldName.insets = new java.awt.Insets(4, 47, 4, 21);
			getJPanel1().add(getJTextFieldName(), constraintsJTextFieldName);

			java.awt.GridBagConstraints constraintsJLabelType = new java.awt.GridBagConstraints();
			constraintsJLabelType.gridx = 2; constraintsJLabelType.gridy = 2;
			constraintsJLabelType.gridwidth = -1;
constraintsJLabelType.gridheight = -1;
			constraintsJLabelType.ipadx = -27;
			constraintsJLabelType.ipady = -14;
			getJPanel1().add(getJLabelType(), constraintsJLabelType);

			java.awt.GridBagConstraints constraintsJComboBoxType = new java.awt.GridBagConstraints();
			constraintsJComboBoxType.gridx = 2; constraintsJComboBoxType.gridy = 2;
			constraintsJComboBoxType.gridwidth = -1;
constraintsJComboBoxType.gridheight = -1;
			constraintsJComboBoxType.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxType.weightx = 1.0;
			constraintsJComboBoxType.ipadx = -126;
			constraintsJComboBoxType.ipady = -23;
			getJPanel1().add(getJComboBoxType(), constraintsJComboBoxType);

			java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
			constraintsJLabelName.gridx = 2; constraintsJLabelName.gridy = 2;
			constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelName.ipadx = 3;
			constraintsJLabelName.ipady = 5;
			constraintsJLabelName.insets = new java.awt.Insets(5, 9, 5, 175);
			getJPanel1().add(getJLabelName(), constraintsJLabelName);

			java.awt.GridBagConstraints constraintsJTextFieldTitle = new java.awt.GridBagConstraints();
			constraintsJTextFieldTitle.gridx = 4; constraintsJTextFieldTitle.gridy = 2;
			constraintsJTextFieldTitle.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTitle.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTitle.weightx = 1.0;
			constraintsJTextFieldTitle.ipadx = 191;
			constraintsJTextFieldTitle.ipady = 1;
			constraintsJTextFieldTitle.insets = new java.awt.Insets(4, 2, 4, 4);
			getJPanel1().add(getJTextFieldTitle(), constraintsJTextFieldTitle);

			java.awt.GridBagConstraints constraintsJLabelTitle = new java.awt.GridBagConstraints();
			constraintsJLabelTitle.gridx = 3; constraintsJLabelTitle.gridy = 2;
			constraintsJLabelTitle.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTitle.ipadx = 8;
			constraintsJLabelTitle.ipady = 5;
			constraintsJLabelTitle.insets = new java.awt.Insets(5, 21, 5, 1);
			getJPanel1().add(getJLabelTitle(), constraintsJLabelTitle);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelDescription = new java.awt.GridBagConstraints();
			constraintsJLabelDescription.gridx = 1; constraintsJLabelDescription.gridy = 1;
			constraintsJLabelDescription.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelDescription.ipadx = 14;
			constraintsJLabelDescription.ipady = 4;
			constraintsJLabelDescription.insets = new java.awt.Insets(4, 8, 41, 394);
			getJPanel2().add(getJLabelDescription(), constraintsJLabelDescription);

			java.awt.GridBagConstraints constraintsJScrollPaneEditorPane = new java.awt.GridBagConstraints();
			constraintsJScrollPaneEditorPane.gridx = 1; constraintsJScrollPaneEditorPane.gridy = 1;
			constraintsJScrollPaneEditorPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneEditorPane.weightx = 1.0;
			constraintsJScrollPaneEditorPane.weighty = 1.0;
			constraintsJScrollPaneEditorPane.ipadx = 442;
			constraintsJScrollPaneEditorPane.ipady = 17;
			constraintsJScrollPaneEditorPane.insets = new java.awt.Insets(22, 7, 5, 10);
			getJPanel2().add(getJScrollPaneEditorPane(), constraintsJScrollPaneEditorPane);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneEditorPane() {
	if (ivjJScrollPaneEditorPane == null) {
		try {
			ivjJScrollPaneEditorPane = new javax.swing.JScrollPane();
			ivjJScrollPaneEditorPane.setName("JScrollPaneEditorPane");
			ivjJScrollPaneEditorPane.setMaximumSize(new java.awt.Dimension(457, 20));
			getJScrollPaneEditorPane().setViewportView(getJEditorPaneDescription());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneEditorPane;
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
			ivjJTextFieldName.setText("");
			ivjJTextFieldName.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
			// user code begin {1}

			ivjJTextFieldName.setDocument( new com.cannontech.common.gui.unchanging.StringRangeDocument( DISPLAY_NAME_WIDTH ) );
						
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
 * Return the JTextFieldTitle property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTitle() {
	if (ivjJTextFieldTitle == null) {
		try {
			ivjJTextFieldTitle = new javax.swing.JTextField();
			ivjJTextFieldTitle.setName("JTextFieldTitle");
			// user code begin {1}
			
			ivjJTextFieldTitle.setDocument( new com.cannontech.common.gui.unchanging.StringRangeDocument( DISPLAY_TITLE_WIDTH ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTitle;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 10:14:08 AM)
 */
public String getName() 
{
	return getJTextFieldName().getText();	
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 10:14:08 AM)
 */
public String getTitle() 
{
	return getJTextFieldTitle().getText();
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 10:14:08 AM)
 */
public Object getType() 
{
	return getJComboBoxType().getSelectedItem();
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION CreateTopPanel() ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CreateTopPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(484, 96);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(0, 0, 0, 3);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 1; constraintsJPanel2.gridy = 2;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.weighty = 1.0;
		constraintsJPanel2.ipady = 1;
		constraintsJPanel2.insets = new java.awt.Insets(0, 1, 0, 2);
		add(getJPanel2(), constraintsJPanel2);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

//	getJTextFieldName().grabFocus();
//	getJPanel1().grabFocus();

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (1/26/00 1:07:56 PM)
 * @return boolean
 */
public boolean isColumnDataNULL() 
{

	try
	{
		if ( getJTextFieldName().equals("") ||
			  getJComboBoxType().getSelectedItem().equals("") )
			return true;
	}
	catch( NullPointerException e )
	{
		return true;
	}

	
	return false;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CreateTopPanel aCreateTopPanel;
		aCreateTopPanel = new CreateTopPanel();
		frame.setContentPane(aCreateTopPanel);
		frame.setSize(aCreateTopPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G72F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DF8945535818284449895B4C2780C85B5553CA7FD4D6B1727FC96A3DA2A74BD5A7A133E72BE23255F6333580FE7E91B96ECEFB67FA4C010A80828D1C28D1606E022D9B6899A88CA42BE4CA3B189021A4D6EA43B49E6E7181D4DEE425FBB675E39B3334B6E0670FD590F434CBD675EFB4EBD775CB3671C191072F71273F3CC650464649375D71FC3480686C2DCC7B62E653898A1230098FE6F82ECA7
	2B3F4C03EB82501212B38A4AC8D2CB913497C3FB5D420C021DF03F035C5552E8061B88FC4A01D6B272E2FEF4BE0BB4BE05345DD6DF8A572DGA900436B86817977556FD271ABD5BC079C93A1197D5867D27D9E95E706F6BB40DC0014FE56FF99DC0BA9676996335A6F5C4D49E443936F1B3660BC06B9B9D48C3031AD6A3502BC1033EEGF95DC36544B197211D86008B4DA5F7EE7E882E0D6DC383C76C62F0355D6671089EC530BB2B95073D5AAE8BB6C5F0383C125BB6EA319C234DCED1EA37F984B70CF3BAF16E
	5F5697513988290176860AFB0672CB057B65G2BB57CC2B3624DAED8AF005345575874FE233A061E8B8F10CD4FFAF237C5D8E30C0B2DAD45252FF12E6F4F05EF507D78D5976AFD8D500A86B38A76GFC8A508B908370A63503973F7CG2E4DFD4AB1F1F8D874B4CB52D137C37EDF51A138615EE2810A0ABBAD58C559C1885B6B547A83E1640986EC5C03939E47F412D24B77BD7574BDE47577CF1A8A4368E4F5CA0DA9F11069E249202E93467BF791F92F9A8C364BE05E5D4448FB5D8CB1B7BF8C6F3427771B5628
	73E49BF8D78D61DE37513D5E8577B92AFDB0FCA7453B8DF8E6232B294DE07BA250320638ED74EC6532D873661332C35630FAC84CE8B63538198C675C2132ECBF0CF34739994F9CCD165D94BF5700E732ACB27373128C343C610C82E6DF6BFA704CBA219D8B10841086709440FA00BC8F37315A2E0357E8E36DB6D9B96AF3399D02CC5858F5DD2F43D588A8322D5563D5EC9E3B50E513DD36BE3760A5EA1F5FF7218E7A02467D195A3E9DF8FCAEF884592638C48F4CEE9B9694C17632BE395D5B318F9EE420F63A06
	85E0B0AC91BEEFC9778E5C9B1BD779D2F260E157FAB07A0EEE5C1391DB974C889340BD534B9BDF213EAAE17D0DGD6CD8F71F43E13028C8E43E2310A6210CF6A441510A48F6AF93399FDC7AF5C87BCDC472F1DC5DC96343335F99E6BFE55B0CF0B4B0E625B64D1FE465A4D6817CBC5EEB33F6D61B6533265EEF260693630B603D0A5B2DB79D48CBDBBDF51B9D1AE56FE213EE7026778DA8165E6BCB6CE9A0FDCED2F624F61DEB901E7AC40E2116B1874D4B01BBDEA738A5CCA3F10414084072EF1A6674E4328E5F6
	DFE1387F2EF93264577DE9462472E03A78C38FBEFF0491412334881260F1400DD7253D5203B6261E4FFED19B13FB8671921C67E3F0003C9A3E788C1EB91B22482EBE1FA2F4880A62728C68746DE710175757A7692E00735AF5867D2E6807732C75FF6B8C1E853B687188F4D334A1AA7D9DFA968537B08CC29F93FD9E056177FD0576BCA40C2A3EE07FD798600C585CBE74ABAC9ED8E2AB41B5482EE158EAF4CD24D442F37A2EF917C1C7CB41E6B02E49026B866D4C797772B3D7B9F697D9766DBED338E7C6625DAD
	26CE091DB527241FB9A6536E3361E52F20BEC145FB192C7F5702E3C30392E663BF7A1A7AFB18BF89A0CD62B61EF496695C0F2846FDCCF43B19AAB59D4FBDD706FBA21C778960FE51E11D547436605C2BA134AED5070C3E689C6A50ED70F5CC5E5F6DC79D52D8EB50B6E28BD4FB7DE08796CB9B151C45D2B7D0FF08F63ED262B1537A86DCCB5EF908E7G6A4EF3FD7FF48B0F19668EA5132CC53BA346055673CC4F52F9DD5F9553F84C347DDB643984E84B4EF3BD3D7D3551EFB7FB3D225DC59F996A9AFF7235F1BF
	986EE7DFEF06EB3FCB76AA2DF0E082DC9F19DF233C78CCD1516AB9CEB00746A6CF1D57E31235F03F69FCF00C421E590EC654C7194C1E59799ADE2478CA831EBD4B7FC07507EDB35012E47E1CB9550B7BC13C20D700BAGAB009BE00E176B21771BDAB24EF33C9562E559E3F3F7849DE0F56D2FFF1B4B4E1C5A5E7DEDBEBFF3F0E54F40FB691E27G4F25G8CA7DB68B3905A599A6E5686446DD1A026D57873E23091F16D50F6E8B89F4585201D2461B6B5C47319B81E5FBB1470F16178FE793546E8BCF28D777993
	1CDF774B5D3D117D72B73D617D3223B73CDF9E6C5D4E70B007B18A63F439B73A5F9E6B8D6737F3A96DE1F3813579D1512790F240077B773466C317010EB38188604BFDCC5F4CC7FC4F0F3E239F02B6E055A189F6D2B402E3E7D978D8857A478124G2CGD88B40F85E5700F12387947019E877F951730055CA22875687B1B11D673186FC86340F70B175E7F16F1887BA244A7ED1150E7BD0757F77B6067AFF0C0B12E11EB4G566799A5DC1F15C04F82E0E77195AD2E4E86DC0D1F672107FAB12E56FCE3354D2FD8
	AC1F4BAE47D15B0015EA11794763070B5471CEBF770FDE9A43A7C3BB9DE0151F7B47405F38FFCC3DE7BE31461C8C6B9F33BABFB42D7533BDA88D687EB185F2CA4673818B67F98268B181447D1725BC44369F812886E074ED8DC6BA5A517EA0FAF3909D657DF12E116E8D22679B463335BD390F2FED5313A9447D2F07A37A7E956A1A562957762E3D24F27646C236573FECA05A1CCD02314ED0F3A147149BE14E4AD1B6D71DFADDBAF32FDA8B1857D4244A6B98657BB3137A65A4E827812C84E0FD377AF85FB5DA5F
	17EB316F1EB17047G56B16E3FF28FDFDF6E3D7DBA4735CDF89CFB869D37207C0E3150BC739445CBE321F96668E12DAE83342CB17EEC3A5207E72965C2C6C1AFC0GA081E089404A8B7C59344DD6C346F9B6C1D8E485E769B638B7363EB44B7555A49EA964F6177660F7C87A0CBF6514C430112C8B4CB648C5F6DD5B391762597ADEF5607AEA00E68510G62G12G52AE7275F55B8E30759DF73814E6C531591DB45A600B6BF289FE9D0D4E124D7D1100BEEE05B6CF126DBD38F65986D58D61F422CCE78164B150
	D78738F84FG5F7B79CD213AD9FDD1FFA62E3FF87573116D71DF291D67DDE2FB3CC543EFEDC2FC15814F767EC65A9F5BD62025DF62363E43974A3F72324E53FCB9927F7EBA1C4FFAB9B4AEBACF713D17C363226CDA9E97A9C04B3C4C791F34906D9CF49A6E2F9C1E7C5CAEF1D2F9B0FB086B0B9CC35C5497BE06185797BE06D85097BDC6D85497B9373B2F4F185BB15BBD510FFE3F71CAC6C18F40C500840045D738AD1B6D6F31B39166BC9CF78C40831FD60C944E7FCDC764353E67983F4E74F7874674FD222208
	43B8BF479FF260FEA922C4118C773183F5A43B861C8A6B2A629B9D68675DC2BFC5B35CE18163A32FFA6ED8FC7B3DA69E87AF3B42E35EB7BE61F1701AAB5CBE731B50F75445E4943483B881E28112G52E3F81FF25AE79534578314CD01D883209DC01A427B5C5A589B45D6F0BE6401774E1860B3579E93BEC6CE8F4147EA727CF3D4DE68C3F9FCDC98C20B94ABEF89E9236E194EBE70F1BDA6CC61FADCBC427518262D5FDD07383C29998515D3B94ECFF1A7206D54F0FF296DD17BDD4B992B3C46FE4167FEE7DD8F
	894E912241C47387E6F36D7D114FC2F77F78E721371F663CE0431D2312EA47C2BF5A31585F6F9534B3B758DF2661DA3D6D22570509B34B817E33094748F326329CE0B1DC53D51D65CD43BEB51F34C4D9CBEE882C0231EBGDED35B59D375980345F826C29C5756090901CBF0BBCE590685CE6B4E43735DE6357589EEDC954797B723FD35C1E451A79E95830C2452269C56479867BAFEECDB901FCE176296D45A6953A5BA9F0A0F23EBEDEBB24639D640E15E2A4015516303682BD47A860DEE2DB1520BE2993DA616
	538FB7069B5F22511BFD617877E87407EA430DF7831D6DE7070F6FE7CCAC5B4FC4382646325C1B6D67C3A3636DE73EE1BF1763BC317AFE2E0B555F13B0DD5ED93CDD53E50B60354BAEC92DDDB17AB7C7F1ECDB07DD965DEE4C62D84003F700E76B7AEEDD683AD05E6CD81E4B55840C39DC69F4C6B74355BA1D67F226A3BCF6F5A0FDBA0F6B5B0E70FC692969BC2E8F503E25B34057GB4823CC3E505BDA37C377E13F0384E99789EF6EC98DBAC86FD3E144720FB6EDFC0DA1E7DD34EDE92BE86CD1C21461EB3743C
	EE6D6026C2266FFDD5C1B927DE0F53AC182DE7CA932F49D54C643965254F0C3537255BBEB4C50A03FBE6B25E01193A8C87A037D4EB027D91EA02F4F7997F47A8FF641BBE13674B836EB7430F63A7D64D472655F059574F647BA3B5723C2BAA0E6382BE7E8E37D7433DDC4B7BA54671BD3F6063FB3E8AF0C533F85FC7FDFC4FBF1D45777CFFE85FG3493GD2E6CDF44F9BB07CD25711D547F530EC16260363EF055701EE795AF8E7002FA5DB136FC1BF4AD7B3BB236084C06FEC7E3CABBAF2FDB9636B57B96E0B89
	0FE3F17DBAFA268233C3F34D5FD3FC6C6C50DC7332F6DE930036E1B637657FB8EB4C7B4B663FEB62632E32659B188DBBEE50ED19452D9E89FDDCA26053GB2815683644D49A85883D08350825083A0814481A481AC81D88E709440DA00CDF3F81C7B43E12CF7C44A595442908DF6E51B8B43DDD517B32E4A5D904AEE843F83E03E716A5804497F260C7E508AF4A7C08CC0A2C07A0DDC1EE24F38F560630130F5E0A65793E1654AD465D987D7921FD1D09E2F4B47464555C4320DAA685B88509E9F3CA6DE838EB74E
	21768D44DF2D87B66E52D539279166C49C77475AF06312557EE99147CD094AAF72193969BA472D1870B8F6260491BCBBAB62C34F1A07621F0A8FBDEBFF547CE49650DAEE623E6557F5617827GFDA9402A1B027534C69BB79C08A6F7B674ABCD88772E24BA00F2D4A404FE03E126781A0450EFB0167979B7981F82EDF182677F72912269F02361BE764864578496CEAA8FF67EAD18322B717B95F19F0960FB6018374123660B3EC1412EF45A8634B1D325483945ACE97C5C6296A9FC1E1DA8DD1DE7AF144267590B
	242B736C94891FF98360AFFB34FE7F24614EE838FF21F3A22E5B65D01C9ABE1D4A05789382B264780729DCFE0A6BD1F10F9AF0E7D45C8A8973A3D832D31479581FC8D81F6BF7395D2C7DD35A476501F4C95347B3D4C617E4B3487DCB8D07F2333CF6A10D0D0AEE4EA858F5B34FF5CDB47E2C04F60B063B2F09673FBDB7F31F3ED70E3CE79F48616BC0B5F278BA5047F274F7C9C764C8F5A2A65F732C76813225EBB227EA350F659AEE61C144155F12D1D0F58B477DB745B5C27B1C063B1346FD923463B45CB94267
	CB52F0AFB4707956E938B90DDC96F2AB473D54484716EA3839BE0EBB276136567131299ACE2C637D52EF657EE4E8C477A10A617E955F647B1357C7A61387DB73A6CD8FC5F3399E7652773C65502E53F0D34DDCB7A7B45C93FE44A5C2BBDD434586F83F159AEE494774FD70BC18EF9E57EBDDG4F56892DFD04365DDA7BF17F352E9D6738BE7DBEF74DBC2E355E93BAE687C9EB42F5A9F0C0D92EB09FC3C0157646910C656C7C556797328B7E279FAF2C1C07FA2B257EE18E5CA74E63B97AA3CD51F274FC921C2317
	5D8671A440DE95EF067B03B9469CBDBBC43EA7FCA89F6375E8547A8E6775D775D72972421C9E1F55ADF07FA460FF8D206A62A06AFD4465F56137A6EC0DCF9E6479D87BED7CDD6FDF8EE21E850E58B5A602C25C4DEE570087AB486A18DDB5BC47F2E8E3FED0C31FCBB67B5000AC7ABC8E22767DF75AB7817AA55D46F568A593D957D2433A903F8A6E73817FF3G462324112FE1B550189EDF9A517518B721BD2BB46C994E397EB6F6A6CA34796E77C51B2FB8E43ECDA1B6D0F6FBC641955586182C3D81DD5699F551
	66AE9CC756BDB077F6D5D6B67734D15D5EADE6240D883262329BF7156FD527196B316AF63ED7F718A7D2E36C01F1887B54F95CF07F162A93A643737EF2038CD8C061F5CFA643C6BF17A1204930A0B09199D2E05C525B753A58AA3877414DDECD069FFD0C7A993685DC433E618E5798BF8B4FD1BF399A7A2F3D1D5B758FAFE2CC5626D746028AE92CC6A6860CB53272C4D6937FB4115B62C3E37A9EACECC25D70CF972A0F05F1BE55DF1EBAF96A0B6ED3045731127D463C5B3C39BE6C77F2881D096AFB08C47DFD7B
	3555D2383DB4DC546F3F39244B7D42C16459EFF3FB757A6E411A8AF5ADEAC9253A837F672F163C5527DBBFBF5149663A710ABED7A7A98F5601DB142B7DCE17A2C42E71ACA17A78779A0D32B0FA2689691147EF4C555775F63EBE5708795D68EBB8F67CD4677153E1676673B59768733D9A387E7922553844C21D5F43C586FB7263980F602F5074596202D30D9FDDD755FDD4FF2062431A7D2A4FB8D49A218E96D477E5F530FB823C9E181C483FB598AAC6DCA634B7A4EA5F82DF78BB09DCFFE917C5890E7AA84352
	2F9F55E77F7DC1F9EA799DA1B9A9349BC3F00C1FD40C77DAFC4C26D4E97EE234AB17875C094D6D9A2307093D31E7F1D086DDD3BB4C6B380347C64DE3BCDE0A53F06E8BBCDECA3D037B01DF84F0137BC419866B2C263FDE2BFDAF03FE19G2573B38AAAGB00EE25FE3750C71BEE640DB1186B087A09500791DF9145FCAE8E74E677553DD23BC3F4EDBG3AD840EB19665177436CE1B5934CE2217E849E3FC71920ECAFFBC739CFD596701AE6BC1DBF955A4B81D6AF6075594BE33C7E39E1817F066987E3215FB07D
	06983FC1DABB967A8DD3CE90FD770550EF189EB39969568B215FB071714C5ED7EF566D7D5F6A235AFB5D010AC3757BD59BFBFBF43C9A4E0124F0B53183630EFBBB244D4689630E8B85D6E3B10D61DE1CC88A2D9915D0FCE7D2E84DE86EA82F997582EDF9920F59270D9979D7AD843BD998BDB6FF0B6AF5590EAD745C38DD9E61987B2B90EDCDC5745925FEA3E5381FBFBA79795777A71587BB6B73293D37031E50A6551C18621C007B8D6171166DEF25C473AD34744170FB82DBF4BCAFFD9076F704AFF7AC06B9D3
	DFA279DE19EC536C351596B032C6EAAD393FDA14EBA2F92D240095F4DAC99ED6E5DAC9BEAD5A3412DCC5148278C7B77CC71FE25E73643206C1B3204FBE857F65618BD80564636BD60BC28A68ABD438A9B104ADA04CDBD1831047DF3C7778DDC97615A61253CACAC41DC0C5A95458E36602FF3D0411D6DE10D05A8FF8ED8DFE50675C3DF3DA59CD35BE93A97A0C9440C3DCF25AE04174CBE7ABA96923AB97E4682B10B2AB0EBD88E38F52318DD6E232FB9572260E37A31ED1BBA41BDD00AE458E1BBCC4E7C011361C
	A5457D72C09FE5209053594F4E82DB5AC272B6AB592CB871F3984C419432D31F3A9DDF98280486368B180FC87497E1399C0B46010E6E813C8FD218C93D66857565C91B5E97DFEB0099664C3E2AAF9E8E74082F03F2DF3D5A8BB6477F625954EFDB4ACD448406659418A5657B51E669AA70AFDAFFCCE75CC50F9949F91C45197B2F98634C6C693BA24619D9496A5F30A6073E5F79F9971E1D1A3B60D9F717D6BF6B7A8709DCA76D9836395D10E58EB5FB9CD8B3BDBA2A88FA3DAC338B677772BEAA11511E6BC23987
	8DE37E9FD0CB878840462E9C7294GG9CBCGGD0CB818294G94G88G88G72F854AC40462E9C7294GG9CBCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGAC95GGGG
**end of data**/
}
}
