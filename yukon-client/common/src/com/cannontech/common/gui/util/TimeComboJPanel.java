package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (2/7/2002 12:49:31 PM)
 * @author: 
 */
public class TimeComboJPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private javax.swing.JComboBox ivjJComboBoxHour = null;
	private javax.swing.JComboBox ivjJComboBoxMinute = null;
	private javax.swing.JLabel ivjJLabelHour = null;
	private javax.swing.JLabel ivjJLabelMinute = null;
/**
 * TimeComboJPanel constructor comment.
 */
public TimeComboJPanel() {
	super();
	initialize();
}
/**
 * TimeComboJPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public TimeComboJPanel(java.awt.LayoutManager layout) {
	super(layout);
	initialize();
}
/**
 * TimeComboJPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public TimeComboJPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
	initialize();
}
/**
 * TimeComboJPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public TimeComboJPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
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
	if (e.getSource() == getJComboBoxHour()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxMinute()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/** 
 * Adds an ActionListener. The listener will receive an action event
 * the user finishes making a selection.
 *
 * @param l  the ActionListener that is to be notified
 */
public void addActionListener(java.awt.event.ActionListener l) 
{
	listenerList.add(java.awt.event.ActionListener.class,l);
}
/**
 * connEtoC1:  (JComboBoxHour.action.actionPerformed(java.awt.event.ActionEvent) --> TimeComboJPanel.jComboBoxHour_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxHour_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JComboBoxMinute.action.actionPerformed(java.awt.event.ActionEvent) --> TimeComboJPanel.jComboBoxMinute_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxMinute_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Notify all listeners that have registered interest for
 * notification on this event type.
 *  
 * @see EventListenerList
 */
protected void fireActionEvent()
{
/*	if ( !firingActionEvent ) 
	{
		firingActionEvent = true;
*/
		java.awt.event.ActionEvent e = null;
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = listeners.length-2; i>=0; i-=2 ) 
		{
			if ( listeners[i]==java.awt.event.ActionListener.class ) 
			{
				if ( e == null )
					e = new java.awt.event.ActionEvent(
								this,
								java.awt.event.ActionEvent.ACTION_PERFORMED,
								this.getClass().getName() + "ActionExecuted" );
					
				((java.awt.event.ActionListener)listeners[i+1]).actionPerformed(e);
			}
		}

/*		firingActionEvent = false;
	}
*/
}
/**
 * Return the JComboBoxHour property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxHour() {
	if (ivjJComboBoxHour == null) {
		try {
			ivjJComboBoxHour = new javax.swing.JComboBox();
			ivjJComboBoxHour.setName("JComboBoxHour");
			// user code begin {1}

			for( int i = 0; i < 24; i++ )
				ivjJComboBoxHour.addItem( new Integer(i) );


			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxHour;
}
/**
 * Return the JComboBoxMinute property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxMinute() {
	if (ivjJComboBoxMinute == null) {
		try {
			ivjJComboBoxMinute = new javax.swing.JComboBox();
			ivjJComboBoxMinute.setName("JComboBoxMinute");
			ivjJComboBoxMinute.setPreferredSize(new java.awt.Dimension(130, 23));
			// user code begin {1}

			for( int i = 0; i < 60; i++ )
				ivjJComboBoxMinute.addItem( new Integer(i) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxMinute;
}
/**
 * Return the JLabelHour property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHour() {
	if (ivjJLabelHour == null) {
		try {
			ivjJLabelHour = new javax.swing.JLabel();
			ivjJLabelHour.setName("JLabelHour");
			ivjJLabelHour.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelHour.setText("Hr");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHour;
}
/**
 * Return the JLabelMinute property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinute() {
	if (ivjJLabelMinute == null) {
		try {
			ivjJLabelMinute = new javax.swing.JLabel();
			ivjJLabelMinute.setName("JLabelMinute");
			ivjJLabelMinute.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMinute.setText("Min");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinute;
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/2002 1:54:35 PM)
 * @return int
 */
public int getTimeInSeconds() 
{
	int hour = ((Integer)getJComboBoxHour().getSelectedItem()).intValue();
					
	return hour * 3600
			 + ((Integer)getJComboBoxMinute().getSelectedItem()).intValue() * 60;
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
	getJComboBoxHour().addActionListener(this);
	getJComboBoxMinute().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TimeComboJPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(237, 25);

		java.awt.GridBagConstraints constraintsJComboBoxHour = new java.awt.GridBagConstraints();
		constraintsJComboBoxHour.gridx = 0; constraintsJComboBoxHour.gridy = 0;
		constraintsJComboBoxHour.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxHour.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxHour.weightx = 1.0;
		constraintsJComboBoxHour.insets = new java.awt.Insets(1, 1, 1, 0);
		add(getJComboBoxHour(), constraintsJComboBoxHour);

		java.awt.GridBagConstraints constraintsJLabelHour = new java.awt.GridBagConstraints();
		constraintsJLabelHour.gridx = 1; constraintsJLabelHour.gridy = 0;
		constraintsJLabelHour.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelHour.ipadx = 6;
		constraintsJLabelHour.insets = new java.awt.Insets(5, 1, 6, 1);
		add(getJLabelHour(), constraintsJLabelHour);

		java.awt.GridBagConstraints constraintsJLabelMinute = new java.awt.GridBagConstraints();
		constraintsJLabelMinute.gridx = 3; constraintsJLabelMinute.gridy = 0;
		constraintsJLabelMinute.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinute.ipadx = 3;
		constraintsJLabelMinute.insets = new java.awt.Insets(5, 1, 6, 1);
		add(getJLabelMinute(), constraintsJLabelMinute);

		java.awt.GridBagConstraints constraintsJComboBoxMinute = new java.awt.GridBagConstraints();
		constraintsJComboBoxMinute.gridx = 2; constraintsJComboBoxMinute.gridy = 0;
		constraintsJComboBoxMinute.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxMinute.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxMinute.weightx = 1.0;
		constraintsJComboBoxMinute.insets = new java.awt.Insets(1, 2, 1, 1);
		add(getJComboBoxMinute(), constraintsJComboBoxMinute);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}
/**
 * Comment
 */
public void jComboBoxHour_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	fireActionEvent();
	
	return;
}
/**
 * Comment
 */
public void jComboBoxMinute_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	fireActionEvent();
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TimeComboJPanel aTimeComboJPanel;
		aTimeComboJPanel = new TimeComboJPanel();
		frame.setContentPane(aTimeComboJPanel);
		frame.setSize(aTimeComboJPanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/** Removes an ActionListener 
 *
 * @param l  the ActionListener to remove
 */
public void removeActionListener(java.awt.event.ActionListener l) 
{
	listenerList.remove(java.awt.event.ActionListener.class,l);
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/2002 2:14:06 PM)
 * @param value boolean
 */
public void setEnabled(boolean value) 
{
	super.setEnabled( value );

	for( int i = 0; i < getComponentCount(); i++ )
		getComponent(i).setEnabled(value);
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/2002 1:54:35 PM)
 */
public void setTimeInSeconds( int seconds )
{
	//86400 == 1 day
	if( seconds < 0 || seconds >= 86400 )
		return;

	//43200 is the number of seconds in a 12 hour period
/*	if( seconds >= 43200 )
	{
		seconds -= 43200;
		getJButtonAMPM().setText("PM");
	}
	else
		getJButtonAMPM().setText("AM");
*/
	//truly, the zeroth hour is hour 12
	Integer h = new Integer( (seconds / 3600) );

	Integer m = new Integer( 
						(seconds - (h.intValue() * 3600)) / 60 );

	getJComboBoxHour().setSelectedItem( h );
	getJComboBoxMinute().setSelectedItem( m );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5CF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BF0D457F5E08BB03647A889CE09F19A453FD1DAA6412D6C715424256E5AE38725A593BC461FB6CA4224CCABBC6E1434101071557E34C2DAE9C13211GE3814206C4558FD293A1BEC20BAC896128E61184E5EABDE95FCAAB3D5D373C77D6DA01BEBD675EF75F3EDD6D0A60F1B4F3745EBD675EF34EBD3FFB5EFBCBAA5E4FF294D8AB88A9F0907D6F2382C26C3FA6C4797229A047E50B96A7B17D9D84
	58C5FC976DF0FD86E89B9E30B8BDE45F99970CF3E1BCF52F4539876E4BC9751947EA60A60D1CA92005273B3EB13F1C52E9AE27100E1F7315403594E0BD60703ABF0072DF73D56A782DBA1EC3011510EDF44E4FFD7BF55CG0CE3GB9GAF86587CBC3896D1490B1ACF6A732EFFA90BEC7D5E07D6B772B17164F0EE182D756B57EA721D02129114F560CA093E66E120EDG404D5B48FD2D7F8557161E3161B3C3F228EEC888076530A68E0D548DC703F5D1ADA8B5B77B02A131CD8E8D4ADDBDC2D814E07EBD14E760
	0A0B72A024874695C144B56DC1B939B05E84304340EF2778FE380FG5C9364FBBB7DE10B2EFB7F2D6F109D2F04EDE5E9761695E4FBFAB1985F5B7268AF8A2B289FBE19C1FB5BC7AD4E23GADG83G8BG32G76D17FDF1906D0A22D039AECA6A407DBA3117312DF39A47BC5896E1B1B012263BE9507E445CF8873F14D155A947A4C87ECDD539D2FE3B649BE4E7D3D6A53A7489BFFF951DA1842A6EFE4B7D81F99E536D8BF9A378913BD16D676DB23097118A83B0F18E567AF16ED0E9432F3DEBEE6350FB1BE15E3
	F1599DE3686BA7282FCF42FD019E9F8C3F1A626FB761D9EC1E24F89CBF8CB4A2715868AF653AF45B9FA21E466E14F6D871DC2B55AFB19D2624E4DDC21548BBDBE2B29D06AEB10ADFE342B3DD5E263A60F8BD50CE04ACCE96DF0384F3F5850CF3GF281B681584396E7B54009B00F31BF1D2E7F83E32CC7D0347351206497954256764D3C8BD7B126A9C2E7D85504701058ABA8C1E1D092D5224F9918C19B8CA62C93E96C8F010C0F45302888DAD08E83F7A1A4EA222232B96119DDB8C791A995B39A8404A204739D
	1FA9072BA4285A1508DF5044788CC61F19C11F0C8B3D40919840BD334B328234178F76AF81CC99F658CE79DD9495A8984D4D5D32BC960D78F0A7A4B70CF63E0B560ECC384F8EF39B5FE7C51CC7069C16B91FD3333BCDFC3C41A1D4DFD0A6F90E2D5A037538C566B173E6BF0F992F7BF1D27BF2D74A182175C6E631921113F367E94A9375E263B3FB7AD32CEF11D367511D82135117D2066D8B60DFEA78EA3B8DFD3582744E8158E058F8FC361A456CF9C195F9147EDE01G937DF10BB3BD55CA17EECF1B217B8C45
	65833F0A88371923325FB46F0B5843DC1B9D20B2103F630FA00359C6A2F89E0963E2D8730A91B16C079BD5272934E66A799A100DB513949F017CEE0304D28D7CAC419C94B4CD898EC6B5713228E94170F01C3E044AD2230311F8E960329E20FB14A7A03F0D79DFA1189BC3F2B8ACD2A799CBF47A03B4B7C5C98C0152EDF2B42CB17C4A820C6FB1F1D22F8D5FA840C6E7DC1022D8E7D8DF30DAA846BDA84190389ECB9539BF02F97B35AB15A69B35G8E7B1B8138EA911E039307F98E565C7CA6D97B5991EB2AB3E4
	4563DE6B06884B214D11F88EB21DB22D29F57FAA2D91BADEE53A9E3A09F6CB6ED2D84CB7B9100F6B0645399F60448D9E733BEDC867F5C58F76B6D91218A98D9B3FED7320CF449BD1914AB196300B065D1C765DC934DE5D060CDEEAC79BCA265AD72250F3301277FD5D200F9FA063863323658633831B27D571F97DB00EA871F9198A1B47767F6F4750A734079B954605D81DBA81F1555C4C8C41FAB5DB2FCB1F7F0642FB327A2BE81B8A55622C85702A5CFFBFF0731EEC79D89649FBF4EF5AFE733A4A74D820467D
	D7FDB76F49261CA8F3AD50562B5C6ECB6D66F321D5D56521A0BD12F41B1DF4187D4BF0279DBB619A88AA2A568989986376EDF3203EF8E669E87D8CFF35121F611BC06EFE8D6C25A17EDF2ED618F477E96C6C19022B0D72BBD7550479C983200EDDFAC4A5A0ABA151FF94ADDBA7CCE8F534965435D272853CBF467D70E6835AED148661F9B956A1C7154FDA1358F42767F36AD8AF337FA36567F49DF33F7D90E919AD37266A096FA9FB4F3AD1E3F6A751387D277E42E94B7F3FDF96B2DBFC3D0A5B229B66E4C439AD
	84D7AADB2C097267021C1DE74CFB39948CC3453A435D70BA6D6ACD6EC557C763E76726685CF31475E80F52785C96E5BDDA1101DF4D62F6BC0EE7FB7DAD45633881E84B47F9DFD16C427C5886630A8908F5GAF009FA0E3024765BA47F1F21B3E2D931E0B1430A0DDCEA850FAACAE24B184B5D59FAF22F624B515F0BB7C28B2598ED99309FBDF13B4E66B364CD9F7A0E91C17340E79F0D85A173AAEF02F6A4FF1872F716738ED933C86CC371BEB401A32A6EBF11A9A5090E332FBE271982CB51E4B1EAA429A168934
	D5G9600BCG7BA4744DG5EC9EE7F3242533733BF8DAB1F5C86388E2829ED49671EE12FC72BBE1F1D1F1C33AE594E7E4944F54CFEABDC3C36CE811D75376F53FA1B8363978D5C0EBE5AA74158FE1377C95F3A0A674BD198FB812E83E8866DE9CA4373A193F02BEF32B30051CEDD455E8757F0DD0E5E3C73FE74FA529ACD9FB399BEAA435CDBFD1EDE2EB0E9DCC44C7DD505ABFDFF354F153A3FBA68CA5DDFFD685A457040437CF44569752E797B2F0FDC297A2F77683EDFFA370C72D6D5D125016895B4C1277D33
	768EC95107FD828FE03A4D4A493BD5C57A1C8B1A5C56E8587803EB733DA3D81F4227AC2FDA0A31FEEF82BA39E5F1D682B4G745C627974FD57ED6B590558BC756CEF0A527B65052254FE49AD42BEC105E3EFC8CC367D8F0BAA8DDABCF3BBB5B104E6E2F35E206BB5C1999667786E27F4BD2325DB1FDF144E3F4C872FA57820497001EDA777C16456DCFBE72440B1FE7F5D0BFD33A4CC4AD10D907B94667825DE5E9F2F00F52B81B682645FE267AB2B4BCFCEE0AD8D4083BA28F7B9A28E11BC3A363C1F2F6D0671G
	408200AC0035D33CC7E9F2220D3A4CBD06214F1FB4E21F5455AD8C0A92A3B0FC47E00901E787B943BFF93A4C4C0B11886B8F1655187B032353CC7F6EE95EBF6CABC9C59F1866356E74B5DE6B34E99E135E6274B1F531B8F5CC7DCFF16ADC6FAB1EBF57BFABCE978BCC3F3DBBD1BF8B6816E7683C6DAC57F9AB60D8CF916D656FCD3DB350AB4EF09FB68E6039E61C2D5883B5B7FF2C847D670561EE9A916CEC78EEAFFFDE4E1961FD773FD21E79B0364FC2FFBA4B4F5C58877C4CDD7544C324FB414514FDF71E2F49
	FAE21645E47FEC7C4C4D06B317497CDDBF17399C683933089B6EB77B8B7D0F75218E6DC169DFCB20236D0EA55057A550D15F578B4C74DFA5509D2675ECEF6B0E703D6DD817CD2427FE17761922E2815B53C97D5A53FB18545C7B4BC27DD9A71066D907C6B75B7F8BB5AEBD37A3400361BEAD6138EC40D5ACC41C6F1AB705FC8E85A47E7E82A18F56ED85B82C6F4F3510751FFCCEB9E95C09FB0AA8E2C0D460E972F270A6567F65D4D6970D376BF0BF32104BAFBBB71FFCC7127C0D8B9365671B6433BAF6EBCE1F7D
	071C4D3B48524F396E39BBDE477A776F76513E5E24BF433FC071763B62F816037FE6642087E85ADD3C2FBF6446D85888632DGA5F7DB1CB5GDE00013BF94D79F9F183394DB96813A35DD014A4D399515F7B75FF3E7777FFDB48E3BD672F9EA14FAC5EDE102EF7566EE6B13EDC3FEE769DB61D71CF15627EACC04B8330E7C02DGE8G6848607BBBDDD24B76F7419F54DAB5CD989A21CF4EFCF33DC1F1A20E4643066DEDDD0DF9EF57B374F7217AB5A3E36E19273F27ACB12FDB214FDB25DF5726D877BE55937205
	703F374E4E459132A1833F37ED41D6CA37630FCB389D777F79CA62FDE5FB4161B4F62CDC44640EAC0A57434D23BB8A994F0771731F1E23DB286C30B8D1EDBC3AFA03E09AFC29FD3471F8C3EDF55371E33A7C2C8973F73713BBBFCA799E8BA1CB171B23DF57ACB91CF25FCC1F8368DE5D1EFF57973F4FB55D7FE7EF2A7741DF94B0991E3EBF268C961F3FF127BF731B5C294F7C136E54E77E99777C7DFB3BBB7D7B53CBEE737BD316DFEFD66119158FBEF3AD36B8EB814E817896735CFA5DFD08505CCA11D7977C43
	584762D79A0D4B7F6B5274FBFD3E74765FF67E3E947B37C1D95364907267788D2568AFCD0ED0A4433DDA0AB6D20243A39A1B2A63FFD40A71A609810AE6FD4DCF68F99801FDADD84CFB1DD88B62164378B183770FAD3C7FD9ED6042745CA4CB60FE89471D75A12E8646A78C5CE3BE3E765C925E4F7C437F1A73477350C1AB2F6B4931123904654D43CB62794BFC94706099399E70792843BD9667D10096G9F40884094C0A6C0B640DA008DG1B81F6G14AC35B82B81CEAC65BEEDFA977B12DEE1D4BF8930EC4A8A
	75AAA075860EBAD6910268DABDD6F2F7617EBA000F5F607563BD3F1907D70FA2C7C4C51BE4D87AE6D87751FFD4250F13DFD55DBECE1CFB69BBD2702FEFB222BF7B946F45E791B990D0C5A39EBDFBBD862ED36D1155A0BEB331BA383D863F81846430467A1332A348B38376163914BFEBACEB41B99AA8436A56E9ACE70475AEDBDB666BDDECA431F759EA6A1D903F96E4380939F7DAD412284FCA9AF739B0EF2321CF2FAFD11F010A38BE4F7A66532770B67A386EE56FD58A8CFF3B69B924FF83AABB124E4FD0893A
	609C797D60A8BC107A04E1E34DCDCFFA9FAF745C5E4777FAD25702CC4F5CDA7020A7F5ADD869B15782064B72E09D9C06B86FB766BDEE602E9A38BF23BC91579774EBA386FEB5558B719DA28A64782729DE9394572F631EB5612E6A38359E0CD35872082C70354FFB286F03124446AF51B941B004ED3CB6D29D03914124778F8DDCDC6F9F6CC2FF9BE784CBBB1D36F1D785658BE9A0C8411B743343E518989EE6715431179871A835E06DCF68935AA1B80F9E7243DAF978D0EDB5348A0DA70E713EE58CC568777B2B
	63FC0E7B6E0C4FD97C1991E13FD7A21D1DE018D5DB08C3D5C819313E139AA6EF7CF8336D270F5A2C445EC91CACD0BA099D3D5DC99CB498BA098D82A596A17107997CABGEE3B67F6BF2D8F3C7A4A2A4F3C95D6E28506A3A2636018C077D39E2854DD6A8DB24338GD8B9E70DD8BB940D58AE01539462CE586A735F7C6D4FF27F29D73591E7A729428C3FACAA5481229FDF22693FA8E8A7C59199AAAEBEB541BED4FAF1285A24A4F6130A40277815CD55C4FFABF72D2A11F7687707FA7C15D2FD07200EE818C2273A
	09BD0C3FFDA03B99D9924243754C4F2718B1DA876896B0D28868DBCE75C5AEF5861772E74B0E9C6C5A740C0DD85B49CEC9967C6D4290E4FDA7A952C694D19D11A5FFACF2769CE5641A40C443A65F866C76995B474D5679A0518D2547E2DF3F5F3D595D8E160BA9241E4E4402D6E74C70370D888A089215CE8DFF4BA1ABB586ED61816D036FFF3C673A1594681BC4EF9445A29AF18604D0D01A049BAF8DAC6AA737865AAAC2D8454FB520D9F512E61CE488287B4ACB8D8FFE6B9757280078D20D3C4FCC89CB6B07C5
	B9D45F9A8884432241G6C8A6C5FB358631C3A24B93BE3DFFA276B653787A18E3C4496EAE8207F9B697FA67C5FC8B10D9453588446FD0B4C7B17CC0F71BD5360B1D2865DDF578E9E54DE5B7252A32F373C158F3BEBA78E7AED861C60094F7AA49A9A9415452B9EFC2FF8F95EFD68238806CA8C796E0A99216FB4A51403E28B67C495E20B670687224B8CA761E8B71DDAC17F6F3963AD632AAA3AF61F216941B41A927EDCF5EC565C17EDDBD419763926F2996B47EA17A577E5973BF1CFA3004FD8467B2115977137
	04694E494BA1C112B4C19AEB8D7B714CBCBF2909715E3D279B792BFC0ECE545F75F5235E2326B57F8FD0CB878853711491E790GGACAAGGD0CB818294G94G88G88G5CF854AC53711491E790GGACAAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2190GGGG
**end of data**/
}
}
