package com.cannontech.common.gui.unchanging;

/**
 * Insert the type's description here.
 * Creation date: (2/14/2001 2:59:30 PM)
 * @author: 
 */
public class JCheckBoxSeasonChooser extends javax.swing.JPanel implements java.awt.event.ActionListener {
	public static final String SPRING_STRING = "Spring";
	public static final String SUMMER_STRING = "Summer";
	public static final String FALL_STRING = "Fall";
	public static final String WINTER_STRING = "Winter";
	private javax.swing.JCheckBox ivjJCheckBoxFall = null;
	private javax.swing.JCheckBox ivjJCheckBoxSpring = null;
	private javax.swing.JCheckBox ivjJCheckBoxSummer = null;
	private javax.swing.JCheckBox ivjJCheckBoxWinter = null;
	protected transient com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener fieldJCheckBoxSeasonChooserListenerEventMulticaster = null;
/**
 * JCheckBoxDayChooser constructor comment.
 */
public JCheckBoxSeasonChooser() {
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
	if (e.getSource() == getJCheckBoxSummer()) 
		connEtoC1(e);
	if (e.getSource() == getJCheckBoxWinter()) 
		connEtoC2(e);
	if (e.getSource() == getJCheckBoxSpring()) 
		connEtoC3(e);
	if (e.getSource() == getJCheckBoxFall()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener
 */
public void addJCheckBoxSeasonChooserListener(com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener newListener) {
	fieldJCheckBoxSeasonChooserListenerEventMulticaster = com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListenerEventMulticaster.add(fieldJCheckBoxSeasonChooserListenerEventMulticaster, newListener);
	return;
}
/**
 * Comment
 */
public void anyCheckBoxSpring_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * connEtoC1:  (JCheckBoxSummer.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxSeasonChooser.fireJCheckBoxSummerAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJCheckBoxSummerAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JCheckBoxWinter.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxSeasonChooser.fireJCheckBoxWinterAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJCheckBoxWinterAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JCheckBoxSpring.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxSeasonChooser.fireJCheckBoxSpringAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJCheckBoxSpringAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JCheckBoxFall.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxSeasonChooser.fireJCheckBoxFallAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJCheckBoxFallAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJCheckBoxFallAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldJCheckBoxSeasonChooserListenerEventMulticaster == null) {
		return;
	};
	fieldJCheckBoxSeasonChooserListenerEventMulticaster.JCheckBoxFallAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJCheckBoxSpringAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldJCheckBoxSeasonChooserListenerEventMulticaster == null) {
		return;
	};
	fieldJCheckBoxSeasonChooserListenerEventMulticaster.JCheckBoxSpringAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJCheckBoxSummerAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldJCheckBoxSeasonChooserListenerEventMulticaster == null) {
		return;
	};
	fieldJCheckBoxSeasonChooserListenerEventMulticaster.JCheckBoxSummerAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJCheckBoxWinterAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldJCheckBoxSeasonChooserListenerEventMulticaster == null) {
		return;
	};
	fieldJCheckBoxSeasonChooserListenerEventMulticaster.JCheckBoxWinterAction_actionPerformed(newEvent);
}
/**
 * Return the JCheckBoxTuesday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxFall() {
	if (ivjJCheckBoxFall == null) {
		try {
			ivjJCheckBoxFall = new javax.swing.JCheckBox();
			ivjJCheckBoxFall.setName("JCheckBoxFall");
			ivjJCheckBoxFall.setMnemonic('f');
			ivjJCheckBoxFall.setText("Fall");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxFall;
}
/**
 * Return the JCheckBoxMonday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSpring() {
	if (ivjJCheckBoxSpring == null) {
		try {
			ivjJCheckBoxSpring = new javax.swing.JCheckBox();
			ivjJCheckBoxSpring.setName("JCheckBoxSpring");
			ivjJCheckBoxSpring.setMnemonic('s');
			ivjJCheckBoxSpring.setText("Spring");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSpring;
}
/**
 * Return the JCheckBoxFriday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSummer() {
	if (ivjJCheckBoxSummer == null) {
		try {
			ivjJCheckBoxSummer = new javax.swing.JCheckBox();
			ivjJCheckBoxSummer.setName("JCheckBoxSummer");
			ivjJCheckBoxSummer.setMnemonic('m');
			ivjJCheckBoxSummer.setText("Summer");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSummer;
}
/**
 * Return the JCheckBoxSaturday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxWinter() {
	if (ivjJCheckBoxWinter == null) {
		try {
			ivjJCheckBoxWinter = new javax.swing.JCheckBox();
			ivjJCheckBoxWinter.setName("JCheckBoxWinter");
			ivjJCheckBoxWinter.setMnemonic('w');
			ivjJCheckBoxWinter.setText("Winter");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxWinter;
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 * @return java.lang.String[]
 */
public Object[] getSelectedSeasons() 
{
	java.util.ArrayList list = new java.util.ArrayList(4);
	
	if( getJCheckBoxSpring().isSelected() )
		list.add(SPRING_STRING);
	if( getJCheckBoxSummer().isSelected() )
		list.add(SUMMER_STRING);
	if( getJCheckBoxFall().isSelected() )
		list.add(FALL_STRING);
	if( getJCheckBoxWinter().isSelected() )
		list.add(WINTER_STRING);

	return list.toArray();
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 * @return java.lang.String[]
 */
public String getSelectedSeasons4Chars() 
{
	StringBuffer buff = new StringBuffer("NNNN");
	
	if( getJCheckBoxSpring().isSelected() )
		buff.setCharAt( 0, 'Y' );
	if( getJCheckBoxSummer().isSelected() )
		buff.setCharAt( 1, 'Y' );
	if( getJCheckBoxFall().isSelected() )
		buff.setCharAt( 2, 'Y' );
	if( getJCheckBoxWinter().isSelected() )
		buff.setCharAt( 3, 'Y' );
		
	return buff.toString();
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJCheckBoxSummer().addActionListener(this);
	getJCheckBoxWinter().addActionListener(this);
	getJCheckBoxSpring().addActionListener(this);
	getJCheckBoxFall().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("JCheckBoxDayChooser");
		setLayout(new java.awt.GridBagLayout());
		setSize(162, 48);

		java.awt.GridBagConstraints constraintsJCheckBoxSpring = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSpring.gridx = 1; constraintsJCheckBoxSpring.gridy = 1;
		constraintsJCheckBoxSpring.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxSpring.ipadx = 15;
		constraintsJCheckBoxSpring.insets = new java.awt.Insets(1, 0, 0, 2);
		add(getJCheckBoxSpring(), constraintsJCheckBoxSpring);

		java.awt.GridBagConstraints constraintsJCheckBoxSummer = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSummer.gridx = 1; constraintsJCheckBoxSummer.gridy = 2;
		constraintsJCheckBoxSummer.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxSummer.ipadx = 3;
		constraintsJCheckBoxSummer.insets = new java.awt.Insets(0, 0, 3, 2);
		add(getJCheckBoxSummer(), constraintsJCheckBoxSummer);

		java.awt.GridBagConstraints constraintsJCheckBoxFall = new java.awt.GridBagConstraints();
		constraintsJCheckBoxFall.gridx = 2; constraintsJCheckBoxFall.gridy = 1;
		constraintsJCheckBoxFall.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxFall.ipadx = 28;
		constraintsJCheckBoxFall.insets = new java.awt.Insets(1, 2, 0, 9);
		add(getJCheckBoxFall(), constraintsJCheckBoxFall);

		java.awt.GridBagConstraints constraintsJCheckBoxWinter = new java.awt.GridBagConstraints();
		constraintsJCheckBoxWinter.gridx = 2; constraintsJCheckBoxWinter.gridy = 2;
		constraintsJCheckBoxWinter.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxWinter.ipadx = 9;
		constraintsJCheckBoxWinter.insets = new java.awt.Insets(0, 2, 3, 9);
		add(getJCheckBoxWinter(), constraintsJCheckBoxWinter);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener
 */
public void removeJCheckBoxSeasonChooserListener(com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener newListener) {
	fieldJCheckBoxSeasonChooserListenerEventMulticaster = com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListenerEventMulticaster.remove(fieldJCheckBoxSeasonChooserListenerEventMulticaster, newListener);
	return;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
public void setEnabled(boolean value)
{
	getJCheckBoxSpring().setEnabled( value );
	getJCheckBoxSummer().setEnabled( value );
	getJCheckBoxFall().setEnabled( value );
	getJCheckBoxWinter().setEnabled( value );

	super.setEnabled( value );
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 */
public void setSelectedSeasons( String seasons )
{
	try
	{
		if( seasons != null && seasons.length() >= 4 )
		{
			boolean selected = (seasons.charAt(0) == 'Y' || seasons.charAt(0) == 'y');
			getJCheckBoxSpring().setSelected( selected );

			selected = (seasons.charAt(1) == 'Y' || seasons.charAt(1) == 'y');
			getJCheckBoxSummer().setSelected( selected );

			selected = (seasons.charAt(2) == 'Y' || seasons.charAt(2) == 'y');
			getJCheckBoxFall().setSelected( selected );

			selected = (seasons.charAt(3) == 'Y' || seasons.charAt(3) == 'y');
			getJCheckBoxWinter().setSelected( selected );	
		}
		//else  //default action
			//getJCheckBoxSummer().setSelected( true );
	}
	catch( Exception e )
	{
		//default action
		//getJCheckBoxSummer().setSelected( true );
	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G67F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359AFFF094659501GC5AA5152B1D22645DFCCEAB39595BB0C658F1A312DE2DA310D15D11C46298EB8C60AC3DAD1E9674BA539A4104005A485A548A9D1920AB1829DB102E3C8C082D123C408A3D332395B3B5B5C5E6EF13717DCG216FFDBF7676AEF7B9C85B4C3C6C6EFB5F77FE3F6FFB5F6E113A9DF34B0AEDF5049417917E77FEB1A12BF793F25CF67EF9011BA0675A09656F8D009A7277CFCB611A89
	342C1939F687B95FDE814F3741F37EF53976ADF0DFCBAE37B7B643CD8AB96B1A8849BFDA56B3361CB74B041CF27A3CC62A026BC600EC406135C8C6799B8636F2FC9E478BA83691325C03E35E9EE8603883705C8310851063E1636F07EBA5151C51F6000FBBF37DDCD234E4172D9A79D8F88AE8743239477855C96EACDE6DC5D91F8FD471B99360F9A1G9ADFC22A4F374175E00F5F7B11CB8F3438A4CD53B5C3F679DA3C9125A52239FC1266D5B4EFDB5B718E1F6C729F5623A7E5A92CEB9DBEDD8F4BA1C2D49F72
	3FF1200272A3E4BFBC2F253857BDA8F3BE5CBFGD0E0621F217803CA2E3D97C0D5041D07F69D64F674DD38132CFBCCAB5914424E498A33AFC70959B9AB72F2792BB4A683F6747DCA20D58D665A1DG1DGEE0049G5BE8AEA8E510D624FD4068508381DDEB8F868F2B6E50273ADBD6613E2D8DA89C77256C52C3EEC2D83C3F9A5813C41F31004DF3DD75BC66136CFDA276B9DF4EA34B6FB9E1ABCF621365592D36B9034C978B86E3BEE1326724141DBF981F1B7132CF932B6C42A9FAC9D992598B9EED362D64FC56
	D9E46F71E32CEF2031EE007BE21E9F8CBF07620396BC4B53C60A476789C0FB5AAFF223EF2350253BF4B6F13C571D548FD9396D36E3AA5321DFCD5465EBAF721E21B2191526AE9E0A4F366019AE4FD1DD7079FE20B585F26DAC3F7C95D837B360B99BE0B140B20035GF59A0C53C40E5DDD766E9566D80F94B28EC794558D8545667A6D7F002B9CB5C2D21796B6A44DA51F12C20AB4204AE1424798F48D9A081BB7C2F35F85B2BE11B5B9A4990A2E81F7A9A09BF2A84C46CC3457601810CC293DCAC0868101A091FC
	275BEB612ACAE1632B20DBB2645888C63F0146E4C8BA859C01815CB33FE4D5223F0E017D7DGC1538F5565486F049C52E4352D2DDB577D11E0AFDAC296EB6867FB685AB1996E67E842473356A32ED44F35B76802CFE779E68B1FCE45056ACB2191D1E3D99EDC1BDBF511B32B7AC44EF4D65FCE76BCFABCE94EA07468ACD7FCFAE26D5CC2F922DE6CF93727AFCD5D366A49EB6A4A570A0E34B2CA4649FF2399ABDB9546AA93F44D8148B3FDFC395C49F276309416C516FE9E0284135DB10FB3BD4FFA9D5C1F28CF99
	571F51D4CAFB9A78D685F36D7B814E84058FDF2076A54E9B2FBD49FD385E5B17403F72FFA80379AAD2017B13BCA4EBC6279C14B5B75C04B96DE2854A63756B514DB993A9BE88755E818596B67153AA30A6A54388A98391C3BEA99B866C3AB1FAA615950E8C84E3CB0510F5E3855A288FEB389DF35C1C8A2C95172EE9B28D1AB90553FFC8EBD5D665GA85D21C7B40361EF2B44FC774BA3FC2D482E44A6E8C8D2A3386E301EE13E3481ED88A981C885DC3AC8FE906BF8095973A0CC869C76BE59F0DD9494B5B93CD3
	54E463791B496233EF59126DA9D937F75A9A4E31BAEABD972BC92653CD6B136B7EA33AE6F0FC1869BA5507EB0F593BF4CAA322F1E1F570E1B5726A8199EE000B67C49DEC5B00F4315670826850D515395374F3C59566F9D8BE9711E10946C56D04693BCDD51B93E827389F99FDCB957AD1352C0741907AF12ED7740533C01F9C003CF34C17D5C0BF834046BD6793639641F3FEA8B62E886EBB43E25C36C1B12E8FF1615838CC38AFB0471D700BF1C5002BB2E26376407DC5C3740CEB1AB14E34E79C140624E8CBF8
	18F508BD92F80B75037BDD953C76B28D517775537D7481F8AEC01CA1F26211EA51774D724FA585373E16323FF5C4D8AE6C0F44F242B9C974FD199BD1268134499191479B2A2CFBCFFBB82C3B943A6D719834D7DBF306610ED62F072BC789050DAEA86A2808D7F7B56A0B7BA2C773BE61C12F68D932C06ED2004EA144FF952D3368DEB1447637D63816D0FE2D3E3DD873B421DA5825C78EF974D0C0F6B721E7DB24E123052EAFAD6D14FC846F1BC59D96D5F5B0BD65D8EF9E898464909BFCB611E5B76599B1943505
	B27AFCE09032794366D33C0757FD22071FB1A4E2F3CB6D47DC7F3C1F4EA687AF575A12756453B6ED375DBF44E2D2B8940B4D453B6DA5057FFE251C7929CE91FEBAB08CB9B7AC7C34AA104CCF5943A25F7685C659F9DA01C34AF86D3CC391F6AE8299CC374EC121DB916076C405EE5B3C49F4F3C705EEF9DBC647GD6C64DBBCE5D9E9F943AC523C2B743AFF41B8F381526EEA4E9FEB9C704EE57AB6D093A9D15D4752AB593E73C752AF9469B91FB79872E443305BA926BFFA60C0C6E05D84F1DCF5702D9A3093D78
	D36C4CB812580BFB4CDEBC8FE82D67C51F384301EB51ACF81E8F70GC081G39GFE38A056007C6AFDA4CD9F5E056924C96A49388D16577D77B7224D30A772671BB622FF691EC8C43F7130B75D796E6005F8DF30F9CF261D571FB00F457623D328CB12B5D8C41A47ECC634024B0ADE90B57D6DD16BFA3BE853DE5B06A4B50DB01F4BDEFA41F21602F3B6535D70216E0E0BD75E6F3218BDD8037BCCA34C7B98208F40G4882D8G306C22085B4ECD0752450DE6EA2F5E8138E3300FF6A476BB261FDFD346971F2634
	7356A6584F7C7E16A67C6E78D6544A1D145731EF2F5CDFEC5E714171695E17F65E19EF13697E25D768EE183A7F11724A39EA5D4F7B47277B34347396A7557DFDB35F17193AEFD2E91EDE3AF25D5939340776B00DB06FGG433DA3A36E8CBC9B17847F47BD5883E64173820025G05A6ED8525E14F05FD9C76D4582771B35228F3DEE18A9D856FAB55FF29A9BFE3140CD467A0F1D63A1A7832BAEDF224BE873D6BC8FE8E7A17A379B968C3C78D43838F6BDB91C16FF00CFDCEBA61C8F6CE5ACE6DDBE15413A4E722
	4F24004C6D2FA5F6DFA59FF34450BB5EB373717AE8E2BED6DC4E35B7DC66E7144B23E343563A1EDA5C23BB015E8F9085488418FFD92CF1CFB9526ECDC722C977A6160F57521EEDA970ABB0F97E3EA6F5BC9E2FC99E0F029A6C3943FAA4641293FD3E22E62BC90B2D30DD069CC05731B12B69FCC38AF965D1B1FB114EE734D4734966D4F1E5F179C23146E52F99175DDAE2DCD6039FCAA730F8EC1DB0BAAE4CEF3F22BD609E209F1CA07A71EB862DB27619B2CE0F5A233BE1CEBF67ED241471D75A4BCD83FA16A963
	AE3FD546FED34641D179158DF396F15ECBD34AF81B76E4CF833D481461F56119DD15C674088135F48D0E3B6492E773D2F8EEG6884683B06750C2C2738EB987BGCFC88A006BCF86E597691EC8FBE3D94CDD886373G8A812A0036E7221079C2BD66DCC28F4CE3776B32F89ABD23F05A6E46EAAB8D7BD3B366FBDF0D1B47FAFEEEF7B6DD67E7017C6C09E2EDAE2438C5701CE762BE9D916B75320922BE7AEAD35747595A6475A157A6DF2F7C35E32FD7615A31737A969F3EEF0152F82FF7926A3B10BEB7CD927A37
	EB424EAC93771CAE6C4CB1F15DC1E16742C9A2A6CFAA713C57A8713CB52F60DDB1D96082BE413B41447D4EA7F8B7CD963C2B7D713CF77A63F91FB46393B5795CE846E70609DB9B953C33CC5E0754F85EA7D5411B1D85663944372C15B04719A172773DFE6CAB4D1E98CF4FEDED1F049477E1495BCD2B0175882B683E0E737BB2443B0986B7FD9F8A4F59G0BB3C42F9CFDDB744AB973E6C35D1FC87AEE2220F72FEDD9867F8612916B153321D7E6B287E5A1734094B87BCEC1DC16ECBD3F216D92C08B669325AF09
	23E39EB54751574551D15FA345D6FA0B47CAAF334CE736BD741630ED5DC359C43D7703146FDD96CEE1B6AD67571ED3BBC96374D74AD93CB2E89E85B499B6B04555B895E5BFC2637A84C05DD4915749B4378DA8AE5FD47C367AE3E4C57268F877C8E79239093DC989079C180330136B3BF8AA6BF744BB65DA72DB5F8E38EA7230B36931364970A961E66E07C332757BEA6B05E2DBC98ABF54CDE376B7726B222960872A7FA02D53BF8374820D59771AA3E61FABB816FDE5E96CF3834FDAFE4F644D21BEDF0131B665
	9D2A0949FB56174E1FA5E3481BGBCF744493BC863799B0099263C4866183CCDE3462FAC0D3CB9407383FE4F76431CD16F952E249F2DA1770DF35E1A2B1E475ED72C226BC676B4763E2258443FC8718BACF836F63D6996EB57E220B5FEC73C47682B45BDF69ABC5F863088A09F208840B1DD6CDB15F52DA4CD2F582B873BE1E3D3ADDB9133CF1747777D799765E20DD8F05FAD308E3CD41C6ADDC163F4D6931D7C3A32F727258FFE2E0E7600C04B84D8G1087D0G3056346F0B3ABD4C3EA3EE45E8B78C496523EF
	E505F12794F9B806468605557A4AA8DEF785F6F11D678EDB6B79407A7713FEE3C0283A162F5557466A1971F8789BAB8F476CB7EC45A9F87471396EEBE36B7E8E2A971C91C86C0F550C8BFF0A42E5BCD5A5FC5B70139F104E47DEAA5E19C2C696673D52A2E36560BA71FB072D382EBAE788BF96EEFC932E2724C11EA168CEBDC43D88C8FCD7F252206D99DFABD76854FF433B380E552F21F60C2678A8ADACE82D5BE2346FB13FFB840DFE0E6075D4640E5D7F4DF25F64CE77ED733F81A6237D7FAA0379D89A230774
	2568A13594BD241132C7645F92EBD3FF7B1BD4EB7D76477221E3BB6E7B7BA19FFAG8C00E9GF34C7C2829437C40DF9F0C2E31A3EEAF1E717097870610DFD4175A56D76A527FCE21349E3F278C68062187103F402F2F276F1874A0C572EFE675682310627599ECA8475757E3AD29320722191D7D2FA3EEA15815EF5AB62F7E1FE364FECFC88F4AA1E304E16957B86E33EF30E4392C427A37D271G8F9D07FE02B5913D1257747994A76EFFC52007633B39767DG5DG7DGD10099GF38196G6481BC8D3096206A
	BA68DF81DA81BA817A2E93F6E4EFE99E438E5E5452C005A485C345F577EC4533DF1857B7FFA732BDF57C3E5C1EBEFE5FEC275F48406F3DA3C1FEDE6F5F0EF2F40FA7AC1BF9A2EFF718382EF00F9ED6701C4FE92FEF3466B8F3BD1F273E0E792AE8B0DCD2157374739CDB2198FD36B3357EB7BB536BFF07B3F97E4DF70E4E3FFB1D49736F67CEEB7EB15CE2A7561E97E251E70E7B25097B5A44AD21BC91F7DAF19BBE933F146A0578E3B28A9478BFD03D06A92E0F631E30603E966FEC1C9883B0592707445CE71C38
	97F894D5E54F4F52B10A86A1B17D310A6A2884A50B5EEBCC9C6A4DFEAFB9337BE7F04DF91E1499444ECE6DF0430E610821E7FC03146221BD9CA21B4CCF16DDD4DC41503E3612BF5DDAE2A325DD444E5C5FC5CA5107DD240C3A380B14007B237853A35CCA704F067F426748A5D24A3108290C43924B5862B82C98DBAC7658664B56FDFCED4656143D407219ECEFDDBDB33647325A7E4BE774DDD1E62E7DD826284FA31F3DB3469AF1B2GAEB1A4555F2E39F13DB8BCE24831757C654F10FFD80C61C4C6AB7E9C759E
	344C798FD0CB87886B88595FFA8FGG98ABGGD0CB818294G94G88G88G67F854AC6B88595FFA8FGG98ABGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG348FGGGG
**end of data**/
}
}
