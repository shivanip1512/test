package com.cannontech.common.gui.util;

import com.cannontech.common.editor.PropertyPanelEvent;

/**
 * Insert the type's description here.
 * Creation date: (2/6/2002 11:16:48 AM)
 * @author: 
 */
/* This dialog is meant to be used a container for a single panel
/*  that needs the Ok and Cancel options. After Ok or Cancel is pressed,
/*  this dialog is set invisible so the values can be retrieved from the original
/*  display panel the user set. 
/***
	NOTE: ITS UP TO THE USER TO DISPOSE OF THIS DIALOG!!!!!
/***/
public class OkCancelDialog extends javax.swing.JDialog implements DataInputPanelListener, java.awt.event.ActionListener {
	public static final int OK_PRESSED = 0;
	public static final int CANCEL_PRESSED = 1;
	private int buttonPressed = CANCEL_PRESSED;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanelSlot = null;
/**
 * OkCancelDialog constructor comment.
 */
public OkCancelDialog() {
	super();
	initialize();
}

/**
 * OkCancelDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public OkCancelDialog(String title, boolean modal, com.cannontech.common.gui.util.DataInputPanel displayPanel )
{
	super();
	setModal( modal );

	initialize();

	setTitle( title );
	setDisplayPanel( displayPanel );

	if( displayPanel == null )
		throw new IllegalArgumentException("*** Can not have a null panel in the constructor of : " + this.getClass().getName() );

}

/**
 * OkCancelDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public OkCancelDialog(java.awt.Dialog owner, String title, boolean modal, com.cannontech.common.gui.util.DataInputPanel displayPanel )
{
	super(owner, modal);

	initialize();

	setTitle( title );
	setDisplayPanel( displayPanel );

	if( displayPanel == null )
		throw new IllegalArgumentException("*** Can not have a null panel in the constructor of : " + this.getClass().getName() );

}
/**
 * OkCancelDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public OkCancelDialog(java.awt.Frame owner, String title, boolean modal, com.cannontech.common.gui.util.DataInputPanel displayPanel )
{
	super(owner, modal);

	initialize();
	
	setTitle( title );
	setDisplayPanel( displayPanel );

	if( displayPanel == null )
		throw new IllegalArgumentException("*** Can not have a null panel in the constructor of : " + this.getClass().getName() );
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonOk()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonCancel()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> OkCancelDialog.jButtonOk_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOk_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> OkCancelDialog.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
/**
 * Insert the method's description here.
 * Creation date: (2/6/2002 11:23:04 AM)
 * @return int
 */
public int getButtonPressed() {
	return buttonPressed;
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
			ivjJButtonCancel.setMnemonic('c');
			ivjJButtonCancel.setText("Cancel");
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
			ivjJButtonOk.setMnemonic('k');
			ivjJButtonOk.setText("Ok");
			ivjJButtonOk.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setPreferredSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setMinimumSize(new java.awt.Dimension(73, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
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

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 2;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel1.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsJPanel1.ipadx = 259;
			constraintsJPanel1.ipady = -4;
			constraintsJPanel1.insets = new java.awt.Insets(1, 3, 6, 3);
			getJDialogContentPane().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsJPanelSlot = new java.awt.GridBagConstraints();
			constraintsJPanelSlot.gridx = 1; constraintsJPanelSlot.gridy = 1;
			constraintsJPanelSlot.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelSlot.weightx = 1.0;
			constraintsJPanelSlot.weighty = 1.0;
			constraintsJPanelSlot.ipadx = 426;
			constraintsJPanelSlot.ipady = 174;
			constraintsJPanelSlot.insets = new java.awt.Insets(0, 0, 1, 0);
			getJDialogContentPane().add(getJPanelSlot(), constraintsJPanelSlot);
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
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.FlowLayout());
			getJPanel1().add(getJButtonOk(), getJButtonOk().getName());
			getJPanel1().add(getJButtonCancel(), getJButtonCancel().getName());
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
 * Return the JPanelSlot property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelSlot() {
	if (ivjJPanelSlot == null) {
		try {
			ivjJPanelSlot = new javax.swing.JPanel();
			ivjJPanelSlot.setName("JPanelSlot");
			ivjJPanelSlot.setLayout(null);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelSlot;
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
	getJButtonOk().addActionListener(this);
	getJButtonCancel().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}

		// user code end
		setName("OkCancelDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setSize(426, 213);
		setTitle("Ok Canel Option");
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 */
public void inputUpdate(PropertyPanelEvent event)
{
	getJButtonOk().setEnabled( 
		((DataInputPanel)getJPanelSlot()).isInputValid() );
	
	if( event != null && event.getID() == PropertyPanelEvent.OK_SELECTION )
		getJButtonOk().doClick();

	if( event != null && event.getID() == PropertyPanelEvent.CANCEL_SELECTION )
		getJButtonCancel().doClick();
}

/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	buttonPressed = CANCEL_PRESSED;
	setVisible(false);
	return;
}
/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	buttonPressed = OK_PRESSED;
	setVisible(false);
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		OkCancelDialog aOkCancelDialog;
		aOkCancelDialog = new OkCancelDialog();
		aOkCancelDialog.setModal(true);
		aOkCancelDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aOkCancelDialog.show();
		java.awt.Insets insets = aOkCancelDialog.getInsets();
		aOkCancelDialog.setSize(aOkCancelDialog.getWidth() + insets.left + insets.right, aOkCancelDialog.getHeight() + insets.top + insets.bottom);
		aOkCancelDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2002 11:46:11 AM)
 * @param newPanel javax.swing.JPanel
 */
private void setDisplayPanel(javax.swing.JPanel displayPanel) 
{
	java.awt.GridBagConstraints g = null;		
	if( getContentPane().getLayout() instanceof java.awt.GridBagLayout )
		g = ((java.awt.GridBagLayout)getContentPane().getLayout()).getConstraints(ivjJPanelSlot);
	else
		throw new IllegalArgumentException( "***" + this.getClass().getName() + " does not have a GridBagLayout!!!");
			
	//just use the SpecificPanel object as a place and GridBagConstraint holder
	getContentPane().remove( ivjJPanelSlot );
	ivjJPanelSlot = displayPanel;
	getContentPane().add( ivjJPanelSlot, g );

	((DataInputPanel)getJPanelSlot()).addDataInputPanelListener(this);

	((DataInputPanel)getJPanelSlot()).fireInputUpdate();
}

/**
 * Sets the OK to visible or not
 * Creation date: (3/6/2003 11:46:11 AM)
 * @param booelan visible
 */
public void setOKButtonVisible( boolean visible ) 
{
	getJButtonOk().setVisible( visible );
}

/**
 * Sets the CANCEL to visible or not
 * Creation date: (3/6/2003 11:46:11 AM)
 * @param booelan visible
 */
public void setCancelButtonVisible( boolean visible ) 
{
	getJButtonCancel().setVisible( visible );
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G87F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DB8DD095D716C603CA22A698F106B2EE161D18AC3363E41C9DF64B4AB8BB8EE3B6198C33E137F4F5F61D94B3F1F65D0D1331AAD40DE65C58BC600108721F90C5FD7C8988B278130A0BCC820209C4D6A8F8A0E6F53061B550506F7523DFBFFE94F54F3937EF3F66518F440DD41D6A3E675EFB6E3D67FE675CF3CFBF385C6391498916DC0ECBC866343F0F93B86E72D90E5B3424E302710204E8ABE778AB
	844A62D2861360998AF53B3E9EED4D643E3C128A6555D0CE027287701E4375DC311F0617G63D4C15D2D1F15044FBC4E73C9EC1C94D27EC1F1BABC8F81C5828F1F71820E3F39B8DF6347E8FCC689966075E31BDD45859A6FBC145B00C200D6755376D1705CCFC6DED0FBDEEBF7E3D984977FDA1965A04AB148E414BBC07B1E571EB66EBB89F183B85607A569DA9FB714A301F071095C3C52AAF856358D8FFC52A7BB2B7BF817CBF629C25FE07500D72C762A22D4DB5BBA5C483B7A8429C964A5F96046A04A63CBD2
	C9FF0EAB01728E421B6840B1A260FD9DD02C4E2F37A33FCA0C36B6815D90593A7ED8D6274D3B7B4EF7383DDBDC09998156250AF4BD2BC45F3A423C6F26FCC876605749286B2DD03797A8F3A85AFA8E488E24829DA1FB3FB369A4BC9BFA55C6596914DD8DEE77C549217C0F6C90A4F82F2D059A0D57AE74490A0363687E9EAF29B2194FCCC47B1D1AF3BF2A134833EC2F57343F40ED7B5E95CB0A09CE36C556D8168CD1DD2C9E7269040E5D9FF06C0DC3D331B8F56CEB1CF16C1D8B6544E4133123B6D7D836EAF2F6
	9A46AE98463D7E8D41C0BA3CA7E878207C5F917EAD831F6272BE61E3598DF51B0699B63A8F313934A42D64B2CF37186AA1BC3A41F2DE22F3E8137C6792C5F0B7A951B11375397C1070C38DFCBA174F889F4B2B21AE5599ED2578FABB856DD402F288D08450BA202DC071C029AE06310F127F700098EB63957522D7149C0242513EEFA73F8FCFE1DCD578E617C7C5FB6B6295116F15848F273579DDB26A20F7CA3F3D847BFDB0466702CBD0F8D514DDA01DF78A2A20F8E81BE4EB9636D184D2EB971D828C60F4F3CC
	6EC1EB8E2E0F77281DEE872F8A3E9634BE0F6C49A85F8592C1G3CD33D9C590F7AAA077557833569FA88A5722E880ACBG6F51A24B43DE379DD742C538D04FF7096FF0407B240B697803B464ED0772DEDD4EAB56EC031CA6318F274FAB934C465E69C0DF1CA9B34C3C5D4DB053F470F92EEA73972618C12A12A9D61AE4FF5B79AFA29367C54B8BBA3A35B166E24BF373954F12B1929F017CC37ADEADB800FBA541BAC200A2E526E3CB0A0DE276A26F9198CADB95G186070E91C4E73A5FB2A264FC4FD6EAF935E86
	1037C3E6BAEB31F79B5ABD9A1D89F6263364C7B086554D6E94BC0F04D14125B689EE416500970FD637AF85B12759EB3F2C77C9CEC53E9B6C3B918C4A23738F24228D722A2A083DDED56890D4D5F48D786AF3D2F1AC0F37576DF38DEC2C432938C6F98C6CDBEFFFBC95ED23CFF6398432C9FA972D3E105826A089CE18F4236CF52914DF369F71BDACCCE83E21F2BF86B9233C64C5BFC3E302357C81DC03A2BAE16B51D5F18EB75AEDD8C93EC1C71940435866BCBCDB5C4C86470A198D165C7E8BEEC3CF29456C8C89
	FF3E493246CDEDEA035BE703F4CE85E966F3AFA2BEC263FB68DC5D0958F7EA0442B07FF769A8A78E644783250FB04CBF1B06754C2FE8E0EF14A5092AD2577162831938A74208D7G6168402E68FAFB7AC036DFDD1726C3DA3F724081E2EFBE5F97B7C24EC1BB0B79CA20DC87F4E3046AF155885583ED777DCE562ECA0138C97135F3A8349DDDFF7B882E0544EFC37CA8BFDE6D9983DC5556FE41D44151F86DBD62F351F656A8ACAE9BACC37DEC0772DE20DC8F5B43FFBC486232306188AE6E394381634DAA0FF676
	FAFCFBE8FB0C45E52907F04CD0280B70B05D67259B4F06860FC76E9349312469EDE33AF10FA96FA769E9706C97950F5A8CC6B84EF47CFABA4E974FAD0D2D1D6336CEF60EC74138BB0102D5645BAF649A66DE823C96A0B7D0A2113728738C5AA881C1B5FD3489CA3F2CB885C7B9EA371A9FD32B09BF28EEA05517703D026DC5FC93E2FF6822D7D5E5D76BF0CF031F081640B2AED4F4D15DFFA327DE1BDF4C0BAB393A7BB9963318B8A46328A5C6556E9B2ACF7713FFEDCD5C79275F27D0BDEC5775D06F0D360EFB19
	9EBE4FB65343DAAF3B938C09F53EF5D0431A63DA280FCEA4F70B0DDE7333F32B9FFFA714E9FC360D60129B750FA17F0D70B3C77DE3C8D7990BA1EDD097BC4AE20A2FE7203F5A816524B1108DF48E488E240EB1BCDEBB88574B19E336E6388FA9AEDE6A18621CD96CF4A8117AD32D1CF5A81979D30E1DD5A7BA7D6371503129EBDF63D7267D4ECD6BF74C2F9C6B570F6EDFC6C7G1FC0F7946A28BFFF2A52684FF7002C7471E8EB8DD09390759BDF3B00760876FFE31C79073B1F997D437A0CB31683817C4306F14D
	270D7BB0DA255F5B9A32504759A6408F8259015CC0E1C0EB01B6CD30BDFA66509FE75BA382BC3B5C883C4B60F39B7D4FC5DD277F7910FB31FB563D081F185A0F6A6FDEB6733B6937418F127877F0A9721A20BC2873C688EF924AE1BAEF5789642D03F24CED96DB7D2A8C4F6F2DFAF9A7A9633E5132DA56ED328F7F9F4231266AE269D7BE06B1CE24FB9E1191B8767A4B8C73586B5B9966315777B232A89FE498EFDE2C7E7B99B347E6BF4EB00B4D0E12F97F647D8CA25B6391BC8404CD3C4AEBF57F2E66F1A6B15A
	D5381CE9EB4E61EEF66E575ADCD265C6CCCFD19BFE2782ED5344065BF8B8B9297D9EAD7737DF3C376C067DCA3F436C77154AD4CDE6499DF66E7FD9AD7A4B9BD0D601165CE158F966027F3E2E063AF5F7687E475CB17763F1A67CF86025CECE65D31BFF151CBD85D0F79E288DE890A8E412597A754CD97D712571997C71152C4038E94FB247CDF7967AB70F6CD57A84FFEC74E4656BF5BE2F522C8ACE5CC65AE61074D7F9E5C01806AD197427F5017A0FE785429F45C8E11791A3E7F4BBFCAC5B5FA7C5029E57EBFA
	0F1D1C3EAFD45E69D24425C368673D92180A6C91DA5DDA6AG7766AEE2242514657736CF3238603BE368677B955E89536BF08BFDDCBCE99BD24AF0F88B4AAAD098D09450263BAC36CAA9C3992A28CA087D3AFB18DB0DC6C8E8F30AB90B71349635B6E28A92ED55A5B0BF5AD94EF09CF40F61F86BC94471BAA847A26F9E43F1CC253FDE387BBEDD645E1F2E979ADFE4DDA0790C7B7E792282422F3B6F1FAFFA2E1645D7EDD097F51F796D9F10B3B3BFA85ADA9544F856BA6455C339CD678D935E8DA82BBA6F359A64
	05C0F9354E3BD2494E06F53E3E04378D4A71C14CF6E23383E3FFCB36B976FF11ED6EB35F4C1E59E73E159D8833F4FEAB33198ED26731B93F114DF4D02073BE4DE1EBAB1F473052D78AA1B5E7F0FB5557C417C39E83D8E0E6CC75D4DFEEEE3A5453FA3527312535639247721791E346BC7979340FCD6F1EC8AD73A886EEE94F750B0AB51C3DD84AF29C61D097BB8FF935A70C71B24E71DB50209AED0C54CBD36AD1AFB8FFDF7D33D36AF12E1D09467A58A97549067EF4DDBFADE571565E1FC6F2525F7CF7407BD87E
	FC3A1EBA6D5956D54C15BC717B94AA67757FB56AA7F3E521A5A1009CD56B9FB45FBFBF76E5393FEDC54D7759532B738359D6CBA55A4A36797E36F50D70F74D77372DB7CEB25BCAFA8C4E084798DE5EA4380F01F21C4E3BF90AE1A8A91879008F088D5A21AC693CA24243BBB75E27A96F1B35F309E5D05E03341334F6F40C68B90D7120B933E4C37B9CEE93D91B53A54021A976D19F7E9661ED818A89E636F5348E63C5959C8175413FC538EAB2665721FFF7726322536B6C90EF8B5AD940FD02F242C146EADD4EAF
	EB9003EEC5689794C5F010566C6CF8177830D8E83B8DA8F58172FA275CB54C4F257915E4FC5165B77ECA3267EDA0678690657D187812D7F0ED4023EBA9435482C7476B4A19E9BC9A0BECA23EA79C5AC7ADE0BEB465F0A5BC3B78A1AD72386490D5D9A12187B05B9499CECAF502F2C946C75B632C938F773D67C7A94C3623DE7A8637EE611E04C0F729CD8B347B3E765CE1AFB62C6513BCDCEF4942E8EBBD1083E892A894A8F2A1DB5B5543B8C70C20E07A8D2A4A778D12AC8B8B2C3AC4E14C4746831E4A66715304
	6677438E1786528FC87882FCF6EB6F9084B9F69EF3CF5A5D261C596C7A054CE6DFB849ECB6EEA13345F6324FC90B222D790B986FA661E1FE22C667EDBFB913BDE1FF628B97BDDC4EDE7B8EB063984CC69FB677CC719ED20E39D7091F103DAA5C159721DE522B98EE6DD016004200A297519CCDDD8836D1CFB54DA2BBD9FBA225C21F8220639A3F865ED3AC9C37E75F3EFDF49EE7487970854BDB6973AB7E68200FCF73C0FA1D258E1FE543E5DAAC778EFEE2576AFED803F7E317B0E65313C4CDA2C08ABF9E15FFFC
	364AF666238A96833EEF08816BAB9F249F351BD7EF9A4FC4DBE5126919387B669FAC41A1948711A13E33EC53505E94BA7E3D433E71CFE6BF4878346D3FE413757715E9F671739C142D47C354B5E8F5F1B9F83E03DBD3A13A95EF13C03B839A3A8628FDDDBD02755BE0FE3BF12F9E872C83B5BD4E6C5611DBC46D5644E6AFB9868496973171BE4F8B9C87DE4D1B7DBB6335BC4C37764A306DCE144F78B77350B6D459CD1814571B077B21088303AAED2A71877350EEA5211F30A9367FDC3F2F5E02350D83C5GC5BD
	CE73CEB10F135879646C67C4DAAA4E035E04CFEB3CCF8F26693C8EC9D6D90EEBA447AC47157B84441BC0B456B8D745FE9BD18F3C71A758FD676C19A9F9B484C4ED6DE712BC5622192C96B3CED63118F16D936CEE72D7C4668EA8F30B411FADE6393371938C37EBDED8493584DDB145ED1C7D0C25E6B1452BFD318F37115EF7D30E137502DDF33EBFB39E45F7A826A2B41B79E5BA9B3B60DB4FF0CDDB76A4941B0C0D942A0D99E798FB87588C456BF70F21CE7317C4DB4F81F58339010201D6ADE1187D7243F2AE70
	B9CA95C8F02B70A27B3E53F5A4B0EEEF9E199D377DC7483783401BFD422D5D2D070F200F157B7BBD02EE9F6EA319BA2F5953A6FBC434C80A053B958C8BEBE1ADBB16B0AC1C3ED5B09D8B1FAB2263A2BFC0D7C33170F2854302EDA94342BBC4E6B7145DC0A1CB999606060D7B01BA8E9457C7AC257B30F129EFBF8E69787BE68E3B9BA7BD49467C7BF6F2E705329DC8FA121D1D35D58F77FB164B8F59EF5E1C7B51FB4131D3B87F60A7693DA0C1671791FE2801CF77ED2DEE432B200EFB0AFD836834A1D69DD08E82
	8A87DA8B948B3473A9065564029AEE161C13DDF637GB0A543D5184AFF29904FE2AE947CB6D0BDD0B71084948C948E3486E88350D620DDC0C94B409FGD58155813581B9163139ECBEDAB1035DE80073998E47B07D523140367323E3335B4EEB474CFDFE6C3169BEFF73B1F31F7F2F470CBEDF4BD1D5BC9CEE16BDE43F57665C0F62EA005CB7471779634DCD78414B7C7136C7779321D0973F0C612D0B7C6E2F7B69E82B8A948E3496E8A3503627599E3B0E1F1DE10F5BE519E43EB08986DBACA8065D26EBFCB147
	FF0D3B1E76453AC94B835D41EB49EF2D72177B2F311170CB167B2FB1261D2D31866A4216B39FD2F20A5375FD4A707E37D5DF75F701697B7B7AA39D036E4F1FEC01EDC93019675106ED66F934915B4C5F96A6EC01FF7791E41B7E3B0FFFA88CBC374D05335B799B0566F63E3DF03A1D7FC721391D7F3650E8671497DF08F39DG7F542D375B23732E6B3C7708CC64DD939D6A204ECFAACA537817859C1071530AF0DEE304572D71B28C3C6B9AAF37884FF9D872202C303E9F96E12E23DF14A4DABECA5A08AEB876F5
	FD9495619CC5B7EF18F7194E3B2E634EF9520773D1437B3E0AC70F797C996F22DF8D66FB0A83632A3FF8F6DC494566387296CF47556DE2F3DC85959BF1C57384AF67305C416E65FAAEBD0F658E8A4218CF2A6F706DC933617D1F5A9F7D7E3C75C84720F7273775FB0C84EB8E8E23BEFEDD984D0B503C40513C597282C60A053E5B01723472EEF8473C744D84667F5F4FE57EBFE9850C3102454283C5188352FFB3AE76BA2B3D234EEA3ED72D9660A82B2D3D6A15A47D0EC463518EA2CB8599CB00D62F20790829F7
	16DD1F1A7168BD665D925F1E9664BEDCECF0F24E7D6879788A197B7A957E67E6AC613F3A427F5C3C2E6BED93541D7F9A43684FF2FDEB78E743FBCF6E23476878A39D037EFF45026EB8F82E798D172CF2992D4336C63C0C5BDA5DF83BD2B9CB6B304AD9E99A4923F05953F3AC8DCF7EFC4B1A1E26DC8BE7E9662C03443FB7F349E3F8D00C634F15B144EA760CF07788E04DCA8A17214BEDA60EBEEE74ECED621B4FA5DA38A490CA9DD4B31704DE86E4ABF40CC4F0D0E484AB67B31084A21B7D04D87F23F7370A7B46
	EFA93B9764877C56F4AE1C5A6A65F05F5D0D460A6F8DA24E03019F912E67E887B1208A14DF6DF07212247252F0034B0139560B932A600BA592C5146FE1ED34CA6D9C95F15EC306BE7F87D0CB87883BC2C1889892GGF0B3GGD0CB818294G94G88G88G87F954AC3BC2C1889892GGF0B3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD292GGGG
**end of data**/
}
}
