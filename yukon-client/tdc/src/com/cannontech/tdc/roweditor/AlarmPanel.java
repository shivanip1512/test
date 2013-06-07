package com.cannontech.tdc.roweditor;

/**
 * Insert the type's description here.
 * Creation date: (4/5/00 3:14:21 PM)
 * @author: 
 * @Version: <version>
 */
import javax.swing.JPanel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.messaging.message.dispatch.SignalMessage;
import com.cannontech.tdc.commandevents.AckAlarm;
import com.cannontech.tdc.commandevents.ClearAlarm;


public class AlarmPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.util.Observer 
{
	private JPanel parentPanel = null;
	private java.util.Observable observingData = null;
	private javax.swing.JButton ivjJButtonAck = null;
	private javax.swing.JLabel ivjJLabelDescription = null;
	private javax.swing.JLabel ivjJLabelUser = null;
	private javax.swing.JPanel ivjJPanelJButtons = null;
	private javax.swing.JLabel ivjJLabelDescText = null;
	private javax.swing.JLabel ivjJLabelUserText = null;

	private SignalMessage signal = null;

/**
 * AlarmPanel constructor comment.
 */
public AlarmPanel() {
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
	if (e.getSource() == getJButtonAck()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonClearAlarm.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmPanel.jButtonClearAlarm_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonClearAlarm_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonAck.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmPanel.jButtonAck_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAck_ActionPerformed(arg1);
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
 * Creation date: (7/27/00 5:08:02 PM)
 */
public void deleteObserver() 
{
	if( observingData != null )
		observingData.deleteObserver( this );	
}
/**
 * Return the JButtonAck property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAck() {
	if (ivjJButtonAck == null) {
		try {
			ivjJButtonAck = new javax.swing.JButton();
			ivjJButtonAck.setName("JButtonAck");
			ivjJButtonAck.setMnemonic('A');
			ivjJButtonAck.setText("Acknowledge");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAck;
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
			ivjJLabelDescription.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelDescription.setText("Description");
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
 * Return the JLabelDescText property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDescText() {
	if (ivjJLabelDescText == null) {
		try {
			ivjJLabelDescText = new javax.swing.JLabel();
			ivjJLabelDescText.setName("JLabelDescText");
			ivjJLabelDescText.setText("ALARM DESCRIPTION");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDescText;
}
/**
 * Return the JLabelUser property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelUser() {
	if (ivjJLabelUser == null) {
		try {
			ivjJLabelUser = new javax.swing.JLabel();
			ivjJLabelUser.setName("JLabelUser");
			ivjJLabelUser.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelUser.setText("User");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelUser;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelUserText() {
	if (ivjJLabelUserText == null) {
		try {
			ivjJLabelUserText = new javax.swing.JLabel();
			ivjJLabelUserText.setName("JLabelUserText");
			ivjJLabelUserText.setText("ALARM USER");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelUserText;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelJButtons() {
	if (ivjJPanelJButtons == null) {
		try {
			ivjJPanelJButtons = new javax.swing.JPanel();
			ivjJPanelJButtons.setName("JPanelJButtons");
			ivjJPanelJButtons.setLayout(new java.awt.GridBagLayout());


			java.awt.GridBagConstraints constraintsJButtonAck = new java.awt.GridBagConstraints();
			constraintsJButtonAck.gridx = 2; constraintsJButtonAck.gridy = 1;
			constraintsJButtonAck.ipadx = 3;
			constraintsJButtonAck.insets = new java.awt.Insets(4, 3, 3, 2);
			getJPanelJButtons().add(getJButtonAck(), constraintsJButtonAck);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelJButtons;
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 4:02:24 PM)
 * @return javax.swing.JPanel
 */
private JPanel getParentPanel() 
{
	if( parentPanel == null )
		throw new IllegalArgumentException("The Parent panel in " + this.getClass().toString() + " is not defined");
	else
		return parentPanel;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
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
	// user code begin {1}
	// user code end
	getJButtonAck().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AlarmPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(404, 62);

		java.awt.GridBagConstraints constraintsJLabelDescription = new java.awt.GridBagConstraints();
		constraintsJLabelDescription.gridx = 1; constraintsJLabelDescription.gridy = 1;
		constraintsJLabelDescription.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelDescription.ipadx = 1;
		constraintsJLabelDescription.insets = new java.awt.Insets(6, 2, 0, 2);
		add(getJLabelDescription(), constraintsJLabelDescription);

		java.awt.GridBagConstraints constraintsJLabelUser = new java.awt.GridBagConstraints();
		constraintsJLabelUser.gridx = 1; constraintsJLabelUser.gridy = 2;
		constraintsJLabelUser.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelUser.ipadx = 18;
		constraintsJLabelUser.insets = new java.awt.Insets(7, 2, 15, 23);
		add(getJLabelUser(), constraintsJLabelUser);

		java.awt.GridBagConstraints constraintsJPanelJButtons = new java.awt.GridBagConstraints();
		constraintsJPanelJButtons.gridx = 3; constraintsJPanelJButtons.gridy = 2;
		constraintsJPanelJButtons.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelJButtons.weightx = 1.0;
		constraintsJPanelJButtons.weighty = 1.0;
		constraintsJPanelJButtons.ipadx = 196;
		constraintsJPanelJButtons.ipady = 34;
		constraintsJPanelJButtons.insets = new java.awt.Insets(1, 1, 4, 2);
		add(getJPanelJButtons(), constraintsJPanelJButtons);

		java.awt.GridBagConstraints constraintsJLabelUserText = new java.awt.GridBagConstraints();
		constraintsJLabelUserText.gridx = 2; constraintsJLabelUserText.gridy = 2;
		constraintsJLabelUserText.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelUserText.ipadx = 56;
		constraintsJLabelUserText.insets = new java.awt.Insets(7, 3, 15, 1);
		add(getJLabelUserText(), constraintsJLabelUserText);

		java.awt.GridBagConstraints constraintsJLabelDescText = new java.awt.GridBagConstraints();
		constraintsJLabelDescText.gridx = 2; constraintsJLabelDescText.gridy = 1;
		constraintsJLabelDescText.gridwidth = 2;
		constraintsJLabelDescText.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelDescText.ipadx = 203;
		constraintsJLabelDescText.insets = new java.awt.Insets(6, 3, 0, 7);
		add(getJLabelDescText(), constraintsJLabelDescText);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}

public SignalMessage getSignal()
{
	return signal;
}

public void setSignal( SignalMessage signal_ )
{
	signal = signal_;

	if( getSignal() != null )
	{	
		getJLabelDescText().setText( getSignal().getDescription() );
		getJLabelUserText().setText( getSignal().getUserName() );
	}
		
}

/**
 * Comment
 */
public void jButtonAck_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getSignal() != null )
		AckAlarm.send( getSignal().getPointId(), getSignal().getCondition() );

	return;
}
/**
 * Comment
 */
public void jButtonClearAlarm_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getSignal() != null )
		ClearAlarm.send( getSignal().getPointId(), getSignal().getCondition() );

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AlarmPanel aAlarmPanel;
		aAlarmPanel = new AlarmPanel();
		frame.setContentPane(aAlarmPanel);
		frame.setSize(aAlarmPanel.getSize());
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
 * Insert the method's description here.
 * Creation date: (7/28/00 2:55:31 PM)
 * @param obsRow com.cannontech.tdc.ObservableRow
 */
public void setObservableData(java.util.Observable obsData) 
{
	observingData = obsData;

	if( observingData != null )
		observingData.addObserver( this );
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 3:52:20 PM)
 * @param panel javax.swing.JPanel
 */
public void setParentPanel(javax.swing.JPanel panel) 
{
	parentPanel = panel;	
}

/**
 * Insert the method's description here.
 * Creation date: (3/10/00 5:06:20 PM)
 */
public void update( java.util.Observable originator, Object newValue ) 
{
	if( newValue instanceof SignalMessage )
	{
		SignalMessage sig = (SignalMessage)newValue;

		if( sig.equals(getSignal()) )
		{
			if( !this.isVisible() )
			{
				updateNonVisiblePanel( sig );				
			}
			else
			{
				updateVisiblePanel( sig );
			}

			getParentPanel().revalidate();
			getParentPanel().repaint();
		}

	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 4:00:30 PM)
 * @param signal com.cannontech.message.dispatch.message.Signal
 */
private void updateNonVisiblePanel(SignalMessage signal) 
{
	if( (signal.getTags() & SignalMessage.TAG_ACTIVE_ALARM) > 0 )
	{
		getJLabelDescText().setText( signal.getDescription() );
		getJLabelUserText().setText( signal.getUserName() );
		this.setVisible( true );
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 4:01:07 PM)
 * @param signal com.cannontech.message.dispatch.message.Signal
 */
private void updateVisiblePanel(SignalMessage signal) 
{
	// if some user acknowledged the alarm
	if( (signal.getTags() & SignalMessage.TAG_ACTIVE_ALARM) > 0 )
	{					
		// update the text fields just in case
		getJLabelDescText().setText( signal.getDescription() );
		getJLabelUserText().setText( signal.getUserName() );										
	}
	else
	{
		this.setVisible( false ); // clear the alarm because we have and acknowledgment					
	}
	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF9F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD4D4D7FA987FB0EA0ADDF735E65B10C8D2B6EB336E46B656F076D856BDA76E5AB6CDCD6359FA366C561330DD332527F6EBB676F899E0C4C4C101B09ACD08C294198CBF0A62082B48CFC4594891B044F0929E4C8386E6668D6F3DE186856C775D7B6E1B47B08351BD611C0F7B6E775D7BFD77FEFF773B6F8D49FFBFB6A35514CFC8EA86517E4E279252DEC5483A6D4A279C97A5A61A09616F830003
	64425EF4E8E300366BDB0966DC727AE8A67463205F7D270966C2F84EA33F9EDDED050788F2220176790ADF25CDAF67CEA117330776B32A3321BDG908FB8ECF708A87FD835C543EF52709CD2CD043C5C07E3EA2A0FEA389A683782AC82086DE36357C23B17CA1EEB2F5146F5AD09A5BBFEF252349F7998F8F23074333935DADBC43E173A2D9FE589F41DB8C7057EAAG5CFC9A79B6798847378E757F3ED7F25BFA850FC77228E26F00CDF5745AE449AFBA1C2AA45B6DF5AEC1F6378A9E5185633B871067F72AB3A9
	8FC2CEC1FFBB45F50E201CB8F8CEG582C635F77A33E4219E83E86506D64FB3BF432D6DBFB673D6F112E57BCE9B9917696F032BD45BA03FBDB6A7B5D1E43548EABAC286F2DC0A3030966FC009A00CE00G403B547ECF961E06362EC72D175CEE49D36775DEF5B964BFC88E51854F30459ED5435D92FBA559C18833F1FF35B54CFA2683B64F77407318CE624FF0FB2F3A758C597A7C8D531EB0BA599ADFE10A99E43A089F8C6A0449FEAE226C1786A77B63E4591F92236C16F9D2DAC69859EBDEADB5ED55782498E4
	978D212DB7D19F480567D44DBF987EDFA85EE140B35F7C0262312F82ED4B90770D4E83FCADAD69ABC8EEE5CBD8BDACCF2CB3553A589A5ADD21EB7986753BA8971319212F65AFA8FE29814F56D2C271580F87DA36BB514C7C6BEE8E462A977A0BG62G92G36826C844876F09F7B0F42722F68E32D022CDE75B9DD8ED1A6EC6ECA4BBB500A81D5961ABD0AAAF8FA455B026C94FADC22C234B12BAC28031EC9733ECFFD3F97E4FCACFAC4D9D01D1287388BEED195E5050DD9E7B908E3E411D25B1DEE9184383D0473
	CD3464C16B929435436B90D4B1B80251FFE2C11B0C883701A3B000E726173F4CC5FDD9E17F8DG6D3A9E9A8FA33F9B228C8943EEEF1124A11F379DF7C262BC28679F535CB1G4FD19E2E63158791178CFDA2F1BEE3078F9978B4B8FBF17902BC4AE36C15914C47799277195FF4F21FE9587FB4313EFABD2C4FA0D8A566AB8DD2E86CEC25BCF1DD2C6F98690C90CB79D278D8FA70DCB19ED1C65A9F497F00EE2B23F9E8AB972CB99AA0DE57718B070B184FDE959411FB69CD999CCCF484B54E5679E77ECCCD1FE97A
	5A63A8EEBD70CB16384E4A7C1D06F1FF6C7A436B2C014AC07E99DF038C261BB9B9F89E09A322C7ED903D2247818F0AC6DB1803BE2745EB1F244FD9C271DE086FFA88A8C547AF4B419894D4D5F67678D431CDD4D52727BFC8FF024AD2FCBD5EE0EA60321E4941BDCAFE08EFFD7CF7F3B0B6FAA50FC724C65227E8743F2231A93AC4B7AC3ADE72F9D406FFBE977DFBC89C55F2438B39D8680C88AE9F6699D6972C9676619EE4279BCC0F290AF4FBB1EE376AF58DC2AE60303EB98B6DB5AF0FC17F899E0345F71FA46B
	3FACB105BBC316BF5DE00A773278C97086E3102DA97EE07835BFC7F30406D758DAFF9448C27FB594A84C5FBB721147D6601D8210B94C7D7D74A124731C22B9FA3D64F2B1B56A7ABD1E170B769007FDA224E2CCDEB7F41D15669D8A215D5674476815F97BE82C85735E36E1FA867AF94DD7847D9A004EE126436543CC87EC5C0AG9FD7A103EE65603881190DE3FB7F6FD234872D5F860591A1E0D37C60D3F67BF52A89D627B51D4B54462F16F9BDB6FE81F5138CFD22A41AAD8A375DBF6D6775585221D8123472C8
	44DA33C2514EDC45F0664E6675589385A8F3A95062942E77D5F946B321CED124DEA7BD0EB41DFD1EEF34AD43F56723ED7B1C3222B6C3708538FE7B72F13DF8DEE9E86D7C5E2D1F5FEBC1EE8AC034CA6B4F71FC435ACF812E95C085C8E37C82D5981B5481EC2CE99565BEC9F60B0ED328D91B60D7EDB48F586AA839911FCB399D7E216F8A3403D7FD2AAAF96ADD22A0D3FF7C32AE04D5CBE4DE97CBEFB39BBC15F7C5DB672667D6105A7BF926F0B5F1F44EB1534BAA537DCEB5E803319F185336FF71769E260F9F85
	38BE9AFC0966B19F57477D8B61743146476F856786AF8477D357BB74009BA1BCC75FB897DA073E648B1E19DBFDD34FCFD61F7D97754F949F2B4F7669785D941FBB924433FD4E26F86CD7GED69882FA9EC05989FBB211F6FCFB4DB819AG9CGF37C5CAFFF13FF064CD033B5431DC876882E36C949D973453802B416CF35FEFCC1864F273A9EB28221FA0875CF5EFBC2C81F4D4B1BB26F3D10FED248BCE63F1C364361738233A850D8BE7F22ACB41FE35D3393F8E687601EC6F3456D71CCEDBD45811EA7461B0CF9
	A2A1274A34AFC21E688A30750D8502BEEA556FEDDBAC186792003685A085A0FBB451DC815082609D65B62AFA6752CCB6223E57AE55836E9A645D7A50F3D1576946073445EBB35AA2E6F472BC263F3F394075978FF4D67F76501C1C847D9DBA6EF28D62ACF7E17FF7F99D753FB4CE5B212F82AC820887582053B36CC8CF02BE3947E238885AAB77B8FD4105CEED3EF1DD0B347E063B8FD73F2A5ABA180C27AE70BAACD4E7873418FC9899684768EB4C7E678BA357E13FAF8CDF07B594062F432E979EE4F860E13C05
	F17A2D42696B34BB05616A34E3D4879B5F49213C95C5D428C3B6882A20515ED08BC818FA6D933828E97A4BA335013D5A18C6D52A2F54FD68DBE55C073A6F71B3FCE08CF35EF2682F86F8C93779676321FE1A84349D77182DB347265A1C456217C750170E823D862095E0GA0FA0C47E0CA610CF932B1B0CD1E7C25A5328DFFE589EF437F34E07D21C8BE39D78C3553EF693D175102515E2C0AEED4A99B331B4ED78539DF1CE2E773E10BCE0BB4BF67F0A4DFE076A223C6FBD5693962458B21B60887BDA6E83AFFB9
	0C8D983F51F3D80B3B04D149279252BA0EF60EB947F374B618378B209870B563ACD733FC7FFDBF46D99FDC7AE1F9EDDE3117D8A7E86E2E61F3174378D5G1BG3683E4CF705A67B1976E71FA0B50A33A9AC425D7F6FA35BA18516BA632F4FA07028AE4784D434817D51A5A3922F05A33C78FCC1A538E2665340C4E831364B19A2BCB6CF6E3DD22CE30BDAE1F60F54B165AF074D5BABD5D9F0E3EDE271F2D8EC75FBC41E36771B2235D12813FEBA2E82B5C7B116A152DB4960B6F073EE7FA0362EB6E073EE7720C71
	3A2C85E82B6F73B3C026E7EDFED42279D494473DC8F13550EF51F1C5937C2C680C627159FFA4F2FCF90E040FAF75C8789CF9774874B97211A31162022D6F158BFC9F717A1A5F3173FDA468B83F1D6F63A540B15D2CBD475FD5DBE6C19E1A45FD3C329BEB853D26416253EE7FD8F6BA2E8A7DADB4E2D82EA46779BB0A68D9BCD75A4EA34F75505F8210B20B57B1018FF99D336A199524A56AC6587BCED2FB15A97391E6FB6BA341BAA69E6A98A6F32C064B9CGFA6CA308FB364668EB687B63GB6GC6FFE3929D75
	7971A47A61C9B5BA2E37B44DC83FB1090EBA3A2C4DE7FB7BE9895F5B2E1F469357DFDF08F817CB567614BD1B352D37CF1062C75F5E235DB17BA25CB1E984337D8F5B797954B01B570DB917B8EECC47DD74733B689A9DB7D44DF15BE7F33F7881BD6F0A67C01E1B43F1EFD15CB568F769381F5B387FC44D61B17C4BB1443D8C7D949D77A64561FD9B6F528C270EC5FA67968E109FFFCE19B3F35D44E4ACFEA099469A6A2B3C53E279F4814D8FEE0F8807323357B2977592CBF17381B26772BC7F4AB83E77D1A189F4
	4D45EFDB2F63A7AD321E0EBFBF51B0039CA344429C04824D1F5640B377ADA67FED9A7F33D17EBC309715D7FCA2A86FB17BF472B2E610E71947EC182A6B7907D36E19DF4DBE1B9EF2DE4679871D474E83D2C34F09F96CBCC85571FBA93E4200E739706CF91E8BEB0196BB0F5FEB7794E3CC264F07FA9F20862095408B30E8BEBF8FAC6FD610996A35F6495B8287064B1062597EE655BC5C372B3F5B43F34E1AF5CF1135735ECA0DF4AF0C1D4FE2E83D56A6371F20F86DFBDE896EAF85E85951F09F823886508520C6
	737DF53FEBE57BEBF4B855BAD595FA87681BA33E395BCE519FC4E3D14476B6E2B76EADBA1A497F36562E0E1E9A5BECDE7EA5633C8D5A38ADDA3BBD623CAB7EF03AA48B58787C851166F943FE43BB250D2F8DB30F457946B22CA1FBA4199E58DD8B500F124AB076DB9770DC10EB1DDAE71E3472DAB1F6812BB357C23BE9813BD732387D9F5BF4F10B6BB6466EB6183BF3810BDB6CE7BF1AE8DEG4E50282F356D902DBDC35F1B69F477212CA03D2EF70863DF984776E47DC94D6FE27D46EF64B5D967433EF3C630BC
	2A1D63DA1BB07F0446639FBFB77248DD7101A9B5828F07B6577BE83096F81F2E8B729D897E313CD7F00A6871710BEC9E9FC73F7BB8E9F86D2D549391E444E93CF798E4A48F6E5A4376FF48CE77DFF7D21331281468FEE32E89BE67981E6D679F64ACFAD0E0B25A3ED699AC6EF30BA357390545616B5CE345616B5C9345535F754B0AA3FF1329AAB6FE13E1F92B6BB456E45605102FGDCGD1GCB97721C353E78B821B9ABCC3EEAF47463BD963F7C2ADC7E4A1248FBFD36E4666F454F17E07C74C890BEEE644F71
	FFDB0276D2A5AFC5B25C8FCBD0C73233FFC0E5C3B57C069274E91758C7512C6EDACC6B3D55302F758BF9ADD6F087F11B213FCD475D3D436B33949DD7F6094F3D320863F2BB785CF69DB75C4167F66B38CFE97D389A7A6BF5DC021F0F5B246366D773F1C797F3DCF2B59FF7EAB14F033FAD8B4666A551987F9577D2CDE991E2B3301845E47462E08EE16775CDABBF2F5782ADE5B12FF17F6FA3FEBFB875983F131459F0ECB77483GB1GD3F3C84A65F0B89673D5F8BCE8FE77EFD62447830F8D0FF1DF6B7890B7F7
	DB9854CE7EC67AD39E6AED00EC15A52FA82B238CCB3FDEE93CFE06BF8952EC7F7807A511F80047DE076B404D1EC131D7ED977A7535E4146399318D56C17EA451DC8CD08B508AB0GB0869083908710G3099A099E097C0EE8C54B3GE7812E4570FD5CB9D5BA4DBE348BA08D9CD9F0E268E8EBF71C0E9CBBC327E70E9D75B47D9E85BE5FBE6A555E870D1E46F3D36A6BD3C4BDC6A7CE676A38E625D5D21C787A0479D71C9D4F5DBE4923323AF89D0D1BAE585BC08C77C16B1D49F579AF30EC536A64A5F79E26AE67
	E74AF2CDC630AEEF273197877855B13CAECF2F8A4A0B6E18C9DE46B4721200E78989FE774FA39E7F643DADBB9314754F7E196EG53495A8E320635E746FB5D59A06F5D55DF65FE11910177CE603DAC15BDB31F6E2F46FD24AF61BE19D3F6FC9A1F44EF90EDF024F87A7577019FBD5CBDE363C34EB3599E6EFE72844DC916A5217713F80AAFDE92FABF293471FCD78134253AFE36D91127FE2E327422454B2F2DF8C6BABD90A6024BF917FE82E65AE274436511633628FC6638ADAD8FFF661517CFBD734E1607BF73
	6C6546B30F61AE17233F74C33E695447B568384FF4DCB3650938CF1D8EF5C0475F246BC27CB59185F2FC87DD171F62BA79B786836EB38D77E5B9669E587200A46BB92E1C6697274B457A83F40C538329C857071B2E5169958C6B16F55CE7FA7CB4DDA404476D5EBB685F9E51DF24FF67ED02A4F66A3883ECA6759F3796D5D80BAA2BCA79DCA716355AF36E65A01F740E8763F366CA104F6D3A607368D5222FFD289A7728CC0A2F27EDC4777D678C4F67EDDFFF4D5B7A354AE03FDD5EEC0E81C32FFA13E428A4035E
	13D4328F6EC39E496F4293CA11C90E2E61E6EA7E2411B37634FFDF19E6A2694D444C4221192423CFB7138C6A724DA48D42A160554C052EE442D699A6937409A75F49D89C172AE1D3E19D060F86AAD8DC013202E45535545D7AC3D1C7DB63AD159CE41D0646367ADB4D2D6D4DB7BF01756E1F6491BFFE327A75CDBF3F2D2491F3B3B90C87E61BA8537CA0BA709B1A764343A63257AB41A15F86F8580CC21B8CC59DF509ADA43F6F967EA0C7D1C5C79D4FBC0ACA8A68EF954AFD2A53D5FECDD086D44C9697DBC83A87
	FFA2C98EB132CB70741733B4F411EAA4350E7AA24184C7E03DF9F43D484526F349FBB12764036B5B562691D39349F2C90223C968050320196CD587E4D19910DC0E0077B2E314C96BD4FC9F12866C5E55370F1B3561C790FD51C301BFDF34BFF9FF93A8B8A013F2BA924FD01BBE42D1BFA048A0CA141BD57C4927A4976B34D96F299F7E7D4705DDA6122AED928D179D702A445CA7381D2ED1F8E82071C7CD1A2B42EAE5412360CFBBE0E5C5A1AB63A4DD006D9B9BAB3E791D5F5D218202D3D572BED3A5CCAD6F97A5
	F7F9DDDF1F53A36A8CC02F407E184E9E4758C2469C8AACA9387E6A6E9E70038612662E28207FAB697FAA7CDFC9B11594D3D9854ACDA1537E055283FC4F54F97438C1735B1A40026A4F5E5C78543F5626EC075DB5118C7AB38EB0C2EEF054A7BEF70FA84FDB754DE37B16A6ADC49BF951D58248B7BF20C78951A28441CC31D9D33C8A3187263A87228F69C642DE819DDAC87F9FF960AD632C23F46EFB7ACA0B03AB4D51D60929E64FA4ACAF9E77CC5A957F6927768D25743E715E34F32EA5623B17AB4B345F0CAD8B
	3E53E075D5C596EEBE86702B16719AA2EAAFDEC4A25DBD5A5C024B258A2E21BA0F836FA1D7C7D5B1F8EF2F4EC27E8A9F2391992DB68B57BDE818737FD0CB8788F02F06135292GGFCB2GGD0CB818294G94G88G88GF9F954ACF02F06135292GGFCB2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8C93GGGG
**end of data**/
}
}
