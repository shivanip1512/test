package com.cannontech.yc.gui;

/**
 * Insert the type's description here.
 * Creation date: (4/11/2002 3:51:40 PM)
 * @author: 
 */
public class AdvancedOptionsPanel extends javax.swing.JDialog implements java.awt.event.ActionListener 
{
	private YCDefaults ycDefaults = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JButton ivjOkButton = null;
	private javax.swing.JPanel ivjAdvancedPanel = null;
	private javax.swing.JPanel ivjOkCancelButtonPanel = null;
	private javax.swing.JLabel ivjCommandPriorityLabel = null;
	private javax.swing.JTextField ivjCommandPriorityTextField = null;
	private javax.swing.JCheckBox ivjConfirmCommandCheckBox = null;
	private javax.swing.JCheckBox ivjQueueCommandCheckBox = null;
	private javax.swing.JCheckBox ivjShowMessageLogCheckBox = null;
	private java.awt.Panel ivjAdvancedOptionsPanel = null;
	private javax.swing.JButton ivjBrowseButton = null;
	private javax.swing.JLabel ivjCommandFileDirectoryLabel = null;
	private javax.swing.JTextField ivjCommandFileDirectoryTextField = null;
/**
 * DBPurgePanel constructor comment.
 */
public AdvancedOptionsPanel() {
	super();
	initialize();
}
/**
 * DBPurgePanel constructor comment.
 */
public AdvancedOptionsPanel(YCDefaults defaultSettings) {
	super();
	ycDefaults = defaultSettings;
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2002 11:49:08 AM)
 * @param source java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent event)
{
	if( event.getSource() == getBrowseButton() )
		getCommandFileDirectoryTextField().setText( browse() );
			
/*	if( event.getSource() == getOkButton())
	{
		ycDefaults = new YCDefaults(
			(new Integer((String)getCommandPriorityTextField().getText())).intValue(),
			getQueueCommandCheckBox().isSelected(),
			getShowMessageLogCheckBox().isSelected(),
			getConfirmCommandCheckBox().isSelected());

		ycDefaults.writeDefaultsFile();
		this.setVisible(false);
		this.dispose();
	}
	*/
	else if( event.getSource() == getCancelButton())
	{
		exit();
	}

}
/**
 * Comment
 */
public String browse()
{
	javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
	
	chooser.setCurrentDirectory(new java.io.File(ycDefaults.getCommandFileDirectory()));
	chooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
	
	int returnVal = chooser.showDialog(this, "Folder");
	
	if( returnVal == javax.swing.JFileChooser.APPROVE_OPTION )
	{
		return chooser.getCurrentDirectory().toString();
	}
	return ycDefaults.getCommandFileDirectory();
}
/**
 * Insert the method's description here.
 * Creation date: (6/3/2002 4:00:35 PM)
 */
public void exit() 
{
	setVisible(false);
	dispose();
}
/**
 * Return the Panel1 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getAdvancedOptionsPanel() {
	if (ivjAdvancedOptionsPanel == null) {
		try {
			ivjAdvancedOptionsPanel = new java.awt.Panel();
			ivjAdvancedOptionsPanel.setName("AdvancedOptionsPanel");
			ivjAdvancedOptionsPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsAdvancedPanel = new java.awt.GridBagConstraints();
			constraintsAdvancedPanel.gridx = 0; constraintsAdvancedPanel.gridy = 0;
			constraintsAdvancedPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsAdvancedPanel.weightx = 1.0;
			constraintsAdvancedPanel.weighty = 1.0;
			getAdvancedOptionsPanel().add(getAdvancedPanel(), constraintsAdvancedPanel);

			java.awt.GridBagConstraints constraintsOkCancelButtonPanel = new java.awt.GridBagConstraints();
			constraintsOkCancelButtonPanel.gridx = 0; constraintsOkCancelButtonPanel.gridy = 1;
			constraintsOkCancelButtonPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOkCancelButtonPanel.weightx = 1.0;
			constraintsOkCancelButtonPanel.weighty = 1.0;
			getAdvancedOptionsPanel().add(getOkCancelButtonPanel(), constraintsOkCancelButtonPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedOptionsPanel;
}
/**
 * Return the AdvancedPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAdvancedPanel() {
	if (ivjAdvancedPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Advanced Properties");
			ivjAdvancedPanel = new javax.swing.JPanel();
			ivjAdvancedPanel.setName("AdvancedPanel");
			ivjAdvancedPanel.setBorder(ivjLocalBorder);
			ivjAdvancedPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCommandPriorityLabel = new java.awt.GridBagConstraints();
			constraintsCommandPriorityLabel.gridx = 0; constraintsCommandPriorityLabel.gridy = 0;
			constraintsCommandPriorityLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommandPriorityLabel.weightx = 1.0;
			constraintsCommandPriorityLabel.weighty = 1.0;
			constraintsCommandPriorityLabel.insets = new java.awt.Insets(10, 10, 5, 10);
			getAdvancedPanel().add(getCommandPriorityLabel(), constraintsCommandPriorityLabel);

			java.awt.GridBagConstraints constraintsCommandPriorityTextField = new java.awt.GridBagConstraints();
			constraintsCommandPriorityTextField.gridx = 1; constraintsCommandPriorityTextField.gridy = 0;
			constraintsCommandPriorityTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCommandPriorityTextField.weightx = 1.0;
			constraintsCommandPriorityTextField.insets = new java.awt.Insets(10, 10, 5, 10);
			getAdvancedPanel().add(getCommandPriorityTextField(), constraintsCommandPriorityTextField);

			java.awt.GridBagConstraints constraintsQueueCommandCheckBox = new java.awt.GridBagConstraints();
			constraintsQueueCommandCheckBox.gridx = 0; constraintsQueueCommandCheckBox.gridy = 1;
			constraintsQueueCommandCheckBox.gridwidth = 2;
			constraintsQueueCommandCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsQueueCommandCheckBox.weightx = 1.0;
			constraintsQueueCommandCheckBox.weighty = 1.0;
			constraintsQueueCommandCheckBox.insets = new java.awt.Insets(5, 10, 5, 10);
			getAdvancedPanel().add(getQueueCommandCheckBox(), constraintsQueueCommandCheckBox);

			java.awt.GridBagConstraints constraintsConfirmCommandCheckBox = new java.awt.GridBagConstraints();
			constraintsConfirmCommandCheckBox.gridx = 0; constraintsConfirmCommandCheckBox.gridy = 3;
			constraintsConfirmCommandCheckBox.gridwidth = 2;
			constraintsConfirmCommandCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsConfirmCommandCheckBox.weightx = 1.0;
			constraintsConfirmCommandCheckBox.weighty = 1.0;
			constraintsConfirmCommandCheckBox.insets = new java.awt.Insets(5, 10, 5, 10);
			getAdvancedPanel().add(getConfirmCommandCheckBox(), constraintsConfirmCommandCheckBox);

			java.awt.GridBagConstraints constraintsShowMessageLogCheckBox = new java.awt.GridBagConstraints();
			constraintsShowMessageLogCheckBox.gridx = 0; constraintsShowMessageLogCheckBox.gridy = 2;
			constraintsShowMessageLogCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsShowMessageLogCheckBox.weightx = 1.0;
			constraintsShowMessageLogCheckBox.weighty = 1.0;
			constraintsShowMessageLogCheckBox.insets = new java.awt.Insets(5, 10, 5, 10);
			getAdvancedPanel().add(getShowMessageLogCheckBox(), constraintsShowMessageLogCheckBox);

			java.awt.GridBagConstraints constraintsCommandFileDirectoryTextField = new java.awt.GridBagConstraints();
			constraintsCommandFileDirectoryTextField.gridx = 0; constraintsCommandFileDirectoryTextField.gridy = 5;
			constraintsCommandFileDirectoryTextField.gridwidth = 2;
			constraintsCommandFileDirectoryTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCommandFileDirectoryTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommandFileDirectoryTextField.weightx = 1.0;
			constraintsCommandFileDirectoryTextField.insets = new java.awt.Insets(0, 5, 10, 5);
			getAdvancedPanel().add(getCommandFileDirectoryTextField(), constraintsCommandFileDirectoryTextField);

			java.awt.GridBagConstraints constraintsCommandFileDirectoryLabel = new java.awt.GridBagConstraints();
			constraintsCommandFileDirectoryLabel.gridx = 0; constraintsCommandFileDirectoryLabel.gridy = 4;
			constraintsCommandFileDirectoryLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommandFileDirectoryLabel.insets = new java.awt.Insets(5, 5, 0, 5);
			getAdvancedPanel().add(getCommandFileDirectoryLabel(), constraintsCommandFileDirectoryLabel);

			java.awt.GridBagConstraints constraintsBrowseButton = new java.awt.GridBagConstraints();
			constraintsBrowseButton.gridx = 2; constraintsBrowseButton.gridy = 5;
			constraintsBrowseButton.insets = new java.awt.Insets(0, 5, 10, 5);
			getAdvancedPanel().add(getBrowseButton(), constraintsBrowseButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedPanel;
}
/**
 * Return the BrowseButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBrowseButton() {
	if (ivjBrowseButton == null) {
		try {
			ivjBrowseButton = new javax.swing.JButton();
			ivjBrowseButton.setName("BrowseButton");
			ivjBrowseButton.setFont(new java.awt.Font("Arial", 1, 12));
			ivjBrowseButton.setText("Browse");
			// user code begin {1}
			ivjBrowseButton.addActionListener(this);

			// Not in use currently.  Can't figure out a way to select 
			// only the directory without specifying a file name.
			ivjBrowseButton.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBrowseButton;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G9CFD4DACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD4D457353C56343E566659162E5ABE3ACA92E8C9EBD7FC2B3ED75258BEDFEA3BECE3921AE0C4E3AA9A1A180806249AE924717097878515A4A8AA03C1032D6123E2A89FAB9F8998A2A82828A88C4C85C66E4C9DE7AEB298523C3D4F39675E4B4C9D9453F4153536F74EBE7BFF765967B3F7A4B979412909C9B904A42692656F3DC4C2E6369032417E5AA79CD771E80809687E8AG3611480E94F8B6
	C1DF664ED0D3B6F929E693343B209D109DEAFA8BBE6FA0EBEBF26B61039FBDF1F504088BEBAAA756D3380B6BC9276D4BDD19705C8A508338A9G91G59445D152B6043203DD5790C101884F6BD06B4F11539BAFC4EE30C2EC9F996BE0EB4F7F76DD67AC3GBF87A09EA073F1CDE686356E2E4A8A255D7555E092791B5834AD28CBA71F032870CED71E0448E99808E432F2E791BC2B5B06860E77C97662BE0B43A1B9E421EF30D870B8A517DCD959D057B66C9A905AAC8EC1A491E85F1BD51BA8AFA173C19E336537
	DB2A95DD01C341E46EFD4B7C5A529041EC980C50EC914FBAD2F2FF0B32FFC2E30BFCE1509F8D406C9C5BDA6A474E513E6201E1DBE5E52D75064551A7D8DB1D32CDF238F576EE6C6276F6010E25D44F41CA5493881FE7824C7BAD7723669D6EC767C78F10285E2C74ACBFFE181FD0466E894D0F580DD119BBE90EBC140763BC857A42G66GC48310A7C3CCE600BC1A1BB7F67D991E353DF23DE437CB0EDA2733CE343ABE142C02881F4123DED9411D957AA417159016FFF9D5078C6C61D07164785C537265826179
	B2736C77C8071C1A1EEA60DB6042B5E9AD0A0C1EA7B55F18DF977620DF81008F82188D30GA096A0B3127BB5F75711DB79558CE367F2D8447661DE2745A5578D5BC42B60D27C7BD957C47E998163FBE452FCACAEE1C774F11973636C74F403384C89DB17D6944962F1A2D20B8B531DF2451F6E1EC85F71C0189209F9789D1A07B2FCCED4F214613FCF7173F5F8B6BFCAA89E5BD150D73D1067ED67D66EC3CB4A37C9DC69B6C39FBC8F272587AFE47604AF749E5BA379D8F7E2GCF1E8AB165821482348108G81CF
	7171B565155CE65E36694616714E4BFB931E02C7F6D91A9DEE99E76CF90B4BE6699585B7D1E89E4DC35BFB4771BDC167D39F68F8DFF088AE8B4EF110EE318B10C8EEC673F45EB624F189343743E697C0015DC9385CE773F640D3343865F3CE2BC596B48A563FAA8FE3F943F29EA402G789C7194467CB02DF74170F98EG0BD50F99F1B90BC2CC07967138746F5E8E4F8FA03185916A262D8F4D3038C6397E1EBDD8CB049B02C3EE901C02438A9F5CCA5F401E8CADAE7D12EA331D62F10E540361EE956F5E330D61
	C106BE4A3C7FA35AEF11E5172DF7D8965A85D936B986B47E4FD3DB5C63261F1BA8F33CGF36C84785589B0287A1739F7074E3F96C99A9AF6F66048A9F14A3B02B199837A40C5FC2D3C2F92574E70C5BCCF57F472BCED5892CAB297E69966A9C254A2161FF10B3C675A0F28CCCDC7F9E567A4673DB704AFB21E23DAEDB97D29F5A4CF5047627E1BFD98775C289053D1000EA89E7777771A59FC2B333885BE43DADDB0E202D54BB2C6DB136FBF479A720DF36CD43EF10E3565CF1CC31D79C6B9447A3A73F1ED11C69C
	3A1C3466EF27F35861902851AA8BE1EB7C1A8E0C054DE113AF582C83025CEE3BA940B8C7E1EEB553B9A8426700A81ECBA3EF73DCAA3CF98F896EFEB14DE8BD7304E624C7C531710D0D52F20959A418B16602A858A1D0755230C3E678A1BA3F953CD2DF2E9498537695E0BE8E89238A5DC0C19A41BAA28E8B2AEF33AC3831F43AECF698AA1C304AFE7070957DFEB0F3B16C97E06FF8941ECDG49D45E410A9C9C7F717B1BC62CFDACEF128B512E9E20178126AD66F973EC41CE5DFCD59226DE92C596FEF5DCA276E5
	63B88A570785D000937A835517C57B36FB751DD77CE27D4B7621DF222E9604ACC63FFA54BDA22E958CB7C2F1AFC13036A473357743EC7D9E36411DE438A7C1D839180DDD42E26FF52BE2BF4A5D3D84EA71926E7B253775352A566D167AECF4D9D046636CF564257B49EB169B96CF31FB84F23B3272D4034DA2CA83A81E6E9D03E8CDC39FCFAC61FB4753960C37875A538142D41D99053EB93335F0B3BC7BEDAEB76CF92C020747AD27907D4735C8C173BD5AE1FDBC0223DFB54CE90439CBD89CA217F847636F8750
	0EB84027BC4DED7B0F42ED3AF840FE92165E8198ADEECF4D7EE41643CA3BF6FFAA4FE142EB60BDF40F137B34F12DBC847893G9DCA7FA0BC59DE66E19A2F31273D77B88BA8FE5A535EFB1C8E8B5F6384C19FD94A57DC799D744B8AEDF9A9776B297DDBF4FE35C9B6072C7A747983B88FA517ED406660382994873BE9DA18583A95CE47F88A488C83E0382C2B081B836DC895F7F7B76262211D798C47AD22B8B3340F2AB8F38F621A205D758CDF2366DB30C68C2A6DA7E8FB2A5A3EE479346B0BB7202E71EB187B9F
	2EC33F2631780E3E03E369D15B9F537628C23B03BCFE658D9CC3215FB2AC42F2A33905D627321D83FC4CB3983F9769F8E07E4DFC061F7F7EEB8467E0BF94BD18266DCE210F4C799D529E384A6BE791342B813AG46G02FE476BE7E8B77AFF436636E1FDE2BA66F4737A14320C532D69C6DDF649EA91D5DBD2FBB8DD0ECA77DE8F5249B6194E1F42E5F8E7F03A1828BEAE59E2D02F69F914557C64DEFD4D9FDC4676CBD36199246AC83C0AF3CDD71A0AAF589CD6E924DE223BE659DD5C505C5E565AFE323B75F4F7
	FD8BFCE075A3F8C4DFBFF2B7A7782D9F3317317C182FBC8B4B0B52D84DEB21390FBCA4BA44D4910D38282BB9DE637EDE92A168B46B5F5C2D6F47DA73AE5177D70F6BC7DB570F63F70C6BC71B77AB7C4C2FC77673F3F942A3E124653FDF77FB3F90904D7C89D11E6149C5E953039CA9AC2ECFF6633C13DD4382EB6F6E61ED2667714B7A7845FD6B3934C4BFFA16AA72D7C6FB5F29ACAEC2DF54352558B2A2C3E9526EA32AFAF9FE6FDE4E57941308B52F835AA24014653C66BDD60234C1508EDF4EEB5E570B7F824F
	7316EB4AA96B2455A6CBAE3AB184E41BCB0299A50FB2ACDD2D15797F4055BBBB9F7FDFBA0F7D6C1F5ECBE6FC6167E9A6BFB109DA4EE2912FBC5B120A5270292C53E51877C5ABC2CC8DG03G81G01GB3D7F05F1694230D303FC573EBE55952B7C8D7D33E6DBDEF93C6B4B49682A67B72E1341449B0176C77979F10F08A062435779A2C009D96DC67D9ECCE74FA47E66E8A2DFEC62E70DD73589A76899D3715AB589A162A623FC47189BABCDB5B36D1BC36331F05F56CD96E7713A5A8D35DEB33E1D37CD9D20279
	047BE2581B58EE52E2598E040E81667B60916C0F82D9EB81F2E3E09F84F0A2064B3EDA3A0F4554A01EA7E1234DEB8A57D7DB667F8C51D4F66B737B19B25C7374CA32AC59D1BE47DFA843D84B1213A2992E2B8CF38C1668C11911AA785EB23A5F977AA91A2D2B97683C69823F1CB1FC2DDDBC44576940983E165EA5A2AE9C5A11AAEEBA452D05F66E6FB96EAD71D31F9BE3FC73A2D241B19D251FDA075139B1156857DC67917F63B5F264566395F0046EDBA14E9D23CE01616EBAC277CE7D7DEEC14D0BAF9D49D6F1
	4D6EB649ED43CCE4F554510B7422E5D49A867A305F6358CC3D465739EAE8F7G0C81CC8740FDCF9C25099B4A3D056FBB082D8CEBBDDFCFD9A165FDDFA944FDFE6BD0BD7609F54310F38EC641565A07C53D7C0A67186EAEF8BA81984D005908E6AABC03G583C1DF50D5FCB4F86DC6CF3FCDFD25603F7E6DE357EFD174DDAE799E8E15120B5BF7E9ADFD38B1F67B57F40904A6C02368CB06DF9FE2E759C6075F666773ECD8E85E4279B2DF5F3BB562705BC2F2C614FEBEB90A10BB3191CEBC3DC4E6E9F7EA76D7337
	E6262CE47CC5ABB5B9BC4F12499F8E6692433312EECDFB0D4EAF4C7941155C3F2C7368DFB0344381A2G986DF74DFC7DDB29521E9EC5DA738B305FGE8FB014F517F39F6E7F741736E106FCF1366E3757E5BF4AC87DFE075BED1455FC771328E4F726950904F27G680BF9012F77ED65986B2697C3CCD600B100C00059G91AF72FA1EFF943F8B19707E35C3F236C0C190F5D703CC7E8F0E217C9810C5D605188A812A815AG86D7F1793623F8FF6FEFFFF1D612E49CF8D8B5E40B8D0ED1F479F0D9706445E238D1
	5447F08C64CEDF25554910D57E56526F52FC89DF45E235CD454F2278989D1E45708D0A47F6F6AC4C55D81EB3A1E66FB1ACDA2D692CDE6DCF7F43B4875BD6FB2F650FD23CF53577DA5EB14A57F28F74ADDD4D75878F91B51F6E57FDBEBC740FBEFF79666F871F298E169F974B7D2FB7BD6546F70C7D6546F70CE27944F70CF2397FBB460F4AFD6F98FFFC4C3FEDBFBDF66B357097470C77AE3FBE663BF7F96C18715EA572186F5E65CDBB62CE2C01F5F08D5F3BECF0A04E836DE9AAAE7ABA5F4F4CDA4373A9743C99
	1368259013992081408AB095E0A6C094G09D36BA5254D05F685C0A70087E086C0B8C094C03CCA1BF7BEC099D72BA2CBBF56C457C6797A3E28170C6F004C0ADDFADC039F5A71F9FBDC3585E33A450BC63F875A66551765C7AE660649C057C43630B1F8484C47A577E5BEAE657B7838D43F2C5E690CA2EE9A34C3D45C1394978E6DA895F7E9545F1CC45EBB1D0BA1AF6B7719EE3FBAA67A5E61D6102A1331033CEB47BDDE2F6422DFB431BDDE031D6F1D62411E6CD7C2CCCDGDDG3857E6FC5BA55F3D61C1097349
	2F303D61B4F886ABFC312FA04D7A6B8D9358465753E4854201E781409BCABB7A95ED4DE5F6941C44F392AE1A9607354DE513DCB6F9344552AB082C7FD13AD7746A6F00FA51E893C4AB23F97F844AB8B3AC8C8B8AE17D2050B7D4A7F9D8FFF6691BD406235F6632FBD1F0BB4AAFA2CD7B20B4722160F6DB860496E9401BA66B6DBD1AAD0DB6D1E830396048AA39B403D43F2246F67A21254E29F497AF60FAD56712C65C825BA90BFF40B82FB92F3FFBB97A87B6861D70E47DB3C6757D33157E25EA7F36F17DF1CAFF
	6EDA5E1FF558287F105AFF2E5D287F045A1F54ED243FC36DF7579859BF28763FB82EFFECAD6B8F01A74B4598F57F9F3E1665E284BCE3563233A24B4516FD374A45E49DA4GEF76BA160BB8078A57F9676247F5B8967AD38B437FE2G6BC76B10FEFCD2EFFA5B0FF6152E637E2D2AB66AEFD8474FAE221D5F27F782EE7ABAFEF6F9FE4FC1E27CCE4D40302DF8D8360915159DF84BD927CCF965BB1159F6757D1FF5FC7F5E64C09D64D55089D07AAA577D93A75F67B681AE60D5FE86586864672170D7390CF72FA3ED
	9C34B72D07358EA01F46049024E5FFF1FFB34231BB58AF63551DF2E735BCD3FF76A9DC7473343709715927E2BD2B23D3579B1EFD7A7D1CFD5854223E6C3F404FBEF35773B35C470D7A77210232567B3DB30BD7746646EB7A17E6AE5D42746FAF5AED2CDFAB83540647C7398D1D71FCFC0F1F4FB366550A8D65ED223CE87774F8BEBE0D07B9EE3E0A1B564EF1640F9C77D7759E3DC2457D20065B627CA337A52B5D4F77EDAC51555CAAB84EF9437E48F3A8330067D0B462DE63FABA8AF88E353D46F368F314F6AA34
	038142DF1BEC8E5532F781297C6A1B7C1C9A216A5CD7433F8F5C34012F6FAE7B2D4E0CA7B60CDF6799DF40AD7976FA35995F03376453C357867EBD5C9CBAC71D9B3C4F283F2478318D5EE754538E9E772950972B7A7BB3679D3E1FF507FCA9576F6CCCFE8F2DA7ED895E7E7E00623B923C7DBDF21D7BBB88FD8B9278193C248AEBFF691FC2CCADGCE0029GC1G33816683AC8508835874BA54AD0023GCDGDDGA2C0GC0606B7CFC9DD101F5575F79DA39BC521D2AF99C7EFCC19F073057D9CE4DD11E912F7B
	6EA5197DCB697A90837DE4A34C9F00D200360D5C1EDDFF3D657BFAA7BD933C2F97B94A6DF2EE54EC1822FC1E315157AE467761285E1F198A5DDCF8C6822C744B372DDC4F172050E513D0D3A1C08500B15FD75B112FC5696FD11E32721C6A176FF877C4799764176F019ABD5FAC05EE1E720C724BF760381EAFD621A3096C1913680FEFFB411D4DAF739D72DD1CB49F1BFF2F529ADD84FE0C1F179BA97E288E4F6665D4357E1F003E90G263F7626117ECDC9ACBE3B137C45699DF5DCCAF5B48D4A67CEF80AG813A
	BE5C0BA71339BB70DCEAF308B63C7D22EFA292FEE60C20FB22C06089CB6276BD0C5D3C7669BEF7B8BE7BFBA07BBFC147772FEBBE4D52FD7E6B754FDE77594FD4872B0B75D57E6F12CED55D7AAE293D4A78AE69E2156FDD5255AA633BA4EB157EAEC9391F2D42BD49G2C931DAA1DC345DDD4F1EEAA93F197ECD6F9D0450FD23B905FA420C20E7F045AB5C2F11D8AEE0A8EF7D1417DFB951E674165C14945F96F2E426F517BED22485ADF23B4B607DB1055F8FC135AE8F3DAF4F68729B8341B4D25DA3A1F1E8D73E8
	9EG43BD580A38A8E82FD4F1FB5B781D4CDA40B1F9B1557E47ECD535715D644B5546F7136B2BA7FE47F6E3353F3BCBE65FAA3A2FEEC88EB575A4F31BE7DFC41C8C6DA9AAEE67A56E47F495E79D65B108D6F14B6B68F71850CEC9613846FA44E5C33BD4459567F0F98DAA2E281C4B8BD4F18D45088B03F6380A3B3F58D75E9345DC5EBC9597C64F82B929212622D40EFB391D6B28D6F1EB2EF29DEDAAEEC6BA175725624EF5F35CCC95772B9AAEAFC14505FCC26DCB8BB51953B8AE03F05EC3AA2E66B8679DD3F1C7
	B2B86FF495971841F90352F85DCE401F7DA8F5A6F51F76796E02FFC2CD7DCCF5B01FBBEF7239942B4661411BBC8E71AA6EB80DFFD3FA28491A4EF13B3AB82FC745FD5445F9272838CEDA37A2219D2B622EDF26672DCD2126DCGF65F713C5D77DD2A3F9FC7B2565FF901569549A13377319C34B69C827E8AD5C6C4ABFD0787A6B6630F7F9B7233FB1B72B6FEEFB33849F7BF60FD07B886B4538178DDC14826D02FFB1B241B3887F03A04FE4165922C6C95E565DD31BC9A43D940B3C7356D110B63ED4B2B55ECAB3D
	44EDDBE9E0DB3217ED89C0131D912A5DA9E5E836B17DDF21B52394705599DCFFFC5DF87D476B3458DE2947F3C11FE4F7CA8EA8F12DAE9B7CCB5FAEE172C6E8ED91C116870059FCB2075BBCAB43B81E49045F1D40DE97E822B234F8467A58FC065625F84027EC66B6DFAE9EEF730DFAADE6AF94F37DC7B79B6BCF5769EF821A2E4D1AFE67E66F717CDFDA335C823E93ADD819CDCF3573BB0DB120E738DD9D742E9F5AB3G18DD5FEA506CFAA01D5BB55F0FDD5CA60425C0932733AB4524594564095DDCDE3649D81E
	C9A73390E8AACCCC9E62DACC5EF1E63F0D6C843C5544637C50A763637C44C94D1F2F75F37D01FE74E7687C295069273F5D73515F4F6AB160671AF87C56B5EA7AFE184175459968635F65F86BFB0347AFD35347E467B6E932A3EECEF4F76BBD4F32C1968217DD18691DB367EA7C57G870DF3A970D4E7F2BF2B1AF5F16D1A4CF79A5E368C7A58A2D5E1DC6C960F4DBEEC2716B0BB3EC86B2F8C748119FC3C27DE2634B60797EDB02D4BB301AEDC35F976E9226A78D1ABAE028EE1444CDFF1A9EE0402DC344FCA5F15
	5FF748DCFA48DCF698AFB5689DD9FF9B3FB70B81F9133BFFDB51CA54B1BCF9C633617E0B795EB6B4586C0283FF28D5D4F67870A1736152030AFE4BA5757D4AAD372B1F6B9CBC2BFD1E572169DFD8F73BB1E03CE31D9A2FB9875F57D4796A8D8AEEF14B49460E6E0E566E334D6FB7F5A8FE5C4861FEF45C369FBC0E5F3D287911562559D2D1BC39315CD542ED18B6E99BEA2EE8FAD7377377ECD9DFDC3D5637626A64EC0ACE67B64D1B34CDBBE80DE3F23E534D65A4CCBACF9EEA54BE3F552C79B263134979720BFE
	EE43512DFC6ED51E52E45F5B2249FEA72340D8F6AD9CBCC590DD46C5B359CBB338ECD1157D4B8F315B3FDFABCFEB1FCDED1A6E2C2E3D5E3A3579775E9118FE65EA6E971F51E41CB92B49683FFC7BB264F60DAF3372F678587EF928CDFFA79482FE07EF75DDCFFC775D7D1774FC8B00A77A3678BE4A51732D851ECDD9374317DF22675B8DBC253745777BF4BDDF8370F45E965F3D636EB0C5608938AD3E797DFA3EC060893BAD3E6599FA3EB94093F18BBEF6C6789CDDBBE23258BA47F0DF243804F1380FAF7952FD
	6E12AF5D57F2FC693E1963CBF72C44172E2A4417AEA15D17AEF113AF5D035D3EF4BF6F7625DB566FCB97536FCB971F61CB77278CBD9D5B8BDA8E60DD29722EB83D76E1785D1EAD9A1EDF7B9076FFE4384FFC8C02E62EA629B2493AB8BCA4B94C4A4B84024B6DA2D96A2CEB2657B951B70ED4A6BFFBDFF292C9E9A6E96CFA2B1924609DD5B3C925D7D84DA4D9161C9E7C697538BF77F572F77643927D1FCCF67017FB4D4A2F97EC02DBA63B94AB4C7CDD8CF3D11979DDF359510315B2C9AABBAA132D74C58CEEAD30
	6CD45E3C60A873C90F50B70CAB2CCC32710D8B33724A0539C59A10C9AEA74337A65CE6753D89101F465E13008F6CCBFA50587A81C4E34B385A7B4BFB4A1FDB703B736EE41256CCF662B135DDF051377485EB03C566BFA5EFA499CE7CE99AEE3AA0D4EE7AC8F54B232250C2F27A4F624FAB5DF07E2865F7C9604A9BAD2829843F06AFE9323887E53CDB2CEAA1A98E7C51BB594E3AC50BE320047D98200A46BB3176848DAA7DBA9E6C5DC16DC5A9452A149D8FE56DAFB895931ECC129A49E6D132D89BAD68F7B34910
	87DD02FBD0922D9E675F1820CDA37C9B4FE49017273A0F4E966337502A51C31E6FCC5B32F2CBE392A19E97A92114F83AA8D6A92C750396972892DC4DB27E6ACBF2952AFD7F36D7BE70687BEFF5A511C445C9CC16EC0F9306205FE237092370017DBA1326D10E8C563AAC8EF73F643203E5E6AF4BF8172A20786B3FAE7D467D2FDD228AB4D6196433D082EB4900A059CBEA7B7BED8EC195GF18571FBD471C8D36CC5335D7355B7CEAD5C588BF950C0126D2525745FB27A6FE17C370CE24AA8266CB084B74EFB960C
	7F736E77F01FE972283392073F3891C6D0DE327A5777BEDD9D978B5EB512D47A43CD9804EC0D6A74303DD7F0FDE166B7761882233F0CE36444D471205CDC0FBAB331DCEC66GFA367B2E3935F7AFDEB4333BA1A709A4C1819894580CCF9D41EAC3F3902F95EE7A1108582DBEF90A586D3E890768B755E14756CECA1AC77F5DBB69A0A21719726ED3FD7F0BE75C689A6D4EFF756277CF5E935437GE25BCCCC1256C13DCDB374B475DF4F532346237CAFE8E955C416A672168264DF4F7EFA4F38150EDBEA9C69C4BF
	16260C37149700B476E58C5308BC1F0D8B27BC93841BE3F394E0537AGE1EF9CB45FB60D5AA27C3D2D031F68FFFF99F557B3FEFF3F5A36136DA72D4AD37B7D7BA367B00441001F3313FFAF14F78EFF0F6A6FB7016DF60BA84A96F12856E1455F87560D4A0276BDD044B9146F66B4CA27727FA31CC33B2F69F87E9FD0CB8788DDE226D3999AGG54CCGGD0CB818294G94G88G88G9CFD4DACDDE226D3999AGG54CCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB85
	86GGGG81G81GBAGGGD39AGGGG
**end of data**/
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getCancelButton() {
	if (ivjCancelButton == null) {
		try {
			ivjCancelButton = new javax.swing.JButton();
			ivjCancelButton.setName("CancelButton");
			ivjCancelButton.setText("Cancel");
			// user code begin {1}
			// This listener is not used because it's calling class implements it instead
			ivjCancelButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelButton;
}
/**
 * Return the CommandFileDirectoryLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCommandFileDirectoryLabel() {
	if (ivjCommandFileDirectoryLabel == null) {
		try {
			ivjCommandFileDirectoryLabel = new javax.swing.JLabel();
			ivjCommandFileDirectoryLabel.setName("CommandFileDirectoryLabel");
			ivjCommandFileDirectoryLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjCommandFileDirectoryLabel.setText("Command Files Directory:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandFileDirectoryLabel;
}
/**
 * Return the CommandFileDirectoryTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getCommandFileDirectoryTextField() {
	if (ivjCommandFileDirectoryTextField == null) {
		try {
			ivjCommandFileDirectoryTextField = new javax.swing.JTextField();
			ivjCommandFileDirectoryTextField.setName("CommandFileDirectoryTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandFileDirectoryTextField;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCommandPriorityLabel() {
	if (ivjCommandPriorityLabel == null) {
		try {
			ivjCommandPriorityLabel = new javax.swing.JLabel();
			ivjCommandPriorityLabel.setName("CommandPriorityLabel");
			ivjCommandPriorityLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjCommandPriorityLabel.setText("Command Priority (1 - 14):");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandPriorityLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getCommandPriorityTextField() {
	if (ivjCommandPriorityTextField == null) {
		try {
			ivjCommandPriorityTextField = new javax.swing.JTextField();
			ivjCommandPriorityTextField.setName("CommandPriorityTextField");
			ivjCommandPriorityTextField.setPreferredSize(new java.awt.Dimension(122, 20));
			ivjCommandPriorityTextField.setText("14");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandPriorityTextField;
}
/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getConfirmCommandCheckBox() {
	if (ivjConfirmCommandCheckBox == null) {
		try {
			ivjConfirmCommandCheckBox = new javax.swing.JCheckBox();
			ivjConfirmCommandCheckBox.setName("ConfirmCommandCheckBox");
			ivjConfirmCommandCheckBox.setFont(new java.awt.Font("dialog", 0, 12));
			ivjConfirmCommandCheckBox.setText("Confirm Command Execution");
			ivjConfirmCommandCheckBox.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfirmCommandCheckBox;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getOkButton() {
	if (ivjOkButton == null) {
		try {
			ivjOkButton = new javax.swing.JButton();
			ivjOkButton.setName("OkButton");
			ivjOkButton.setPreferredSize(new java.awt.Dimension(73, 25));
			ivjOkButton.setText("OK");
			ivjOkButton.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjOkButton.setMinimumSize(new java.awt.Dimension(73, 25));
			// user code begin {1}
			// This listener is not used because it's calling class implements it instead
			//ivjOkButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkButton;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getOkCancelButtonPanel() {
	if (ivjOkCancelButtonPanel == null) {
		try {
			ivjOkCancelButtonPanel = new javax.swing.JPanel();
			ivjOkCancelButtonPanel.setName("OkCancelButtonPanel");
			ivjOkCancelButtonPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
			constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 0;
			constraintsCancelButton.insets = new java.awt.Insets(10, 20, 10, 20);
			getOkCancelButtonPanel().add(getCancelButton(), constraintsCancelButton);

			java.awt.GridBagConstraints constraintsOkButton = new java.awt.GridBagConstraints();
			constraintsOkButton.gridx = 0; constraintsOkButton.gridy = 0;
			constraintsOkButton.insets = new java.awt.Insets(10, 20, 10, 20);
			getOkCancelButtonPanel().add(getOkButton(), constraintsOkButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkCancelButtonPanel;
}
/**
 * Return the AutoEmailCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getQueueCommandCheckBox() {
	if (ivjQueueCommandCheckBox == null) {
		try {
			ivjQueueCommandCheckBox = new javax.swing.JCheckBox();
			ivjQueueCommandCheckBox.setName("QueueCommandCheckBox");
			ivjQueueCommandCheckBox.setFont(new java.awt.Font("dialog", 0, 12));
			ivjQueueCommandCheckBox.setText("Queue Commands");
			ivjQueueCommandCheckBox.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueueCommandCheckBox;
}
/**
 * Return the ShowMessageLogCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getShowMessageLogCheckBox() {
	if (ivjShowMessageLogCheckBox == null) {
		try {
			ivjShowMessageLogCheckBox = new javax.swing.JCheckBox();
			ivjShowMessageLogCheckBox.setName("ShowMessageLogCheckBox");
			ivjShowMessageLogCheckBox.setSelected(true);
			ivjShowMessageLogCheckBox.setFont(new java.awt.Font("dialog", 0, 12));
			ivjShowMessageLogCheckBox.setText("Show Message Log");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShowMessageLogCheckBox;
}
public YCDefaults getYCDefaults()
{
	if( ycDefaults == null)
		ycDefaults = new YCDefaults();
	return ycDefaults;
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		//setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CommanderIcon.gif"));
		// user code end
		setName("AdvancedOptionsFrame");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(379, 283);
		setVisible(true);
		setModal(true);
		setTitle("Yukon Commander");
		setContentPane(getAdvancedOptionsPanel());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getCommandPriorityTextField().setText(String.valueOf(getYCDefaults().getCommandPriority()));
	getQueueCommandCheckBox().setSelected(getYCDefaults().getQueueExecuteCommand());
	getShowMessageLogCheckBox().setSelected(getYCDefaults().getShowMessageLog());
	getConfirmCommandCheckBox().setSelected(getYCDefaults().getConfirmCommandExecute());
	getCommandFileDirectoryTextField().setText(getYCDefaults().getCommandFileDirectory());
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		AdvancedOptionsPanel aAdvancedOptionsPanel;
		aAdvancedOptionsPanel = new AdvancedOptionsPanel();
		aAdvancedOptionsPanel.showAdvancedOptions();
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/3/2002 4:10:09 PM)
 * @param event java.awt.event.KeyEvent
 */
public void processKeyEvent(java.awt.event.KeyEvent event)
{	
	if( event.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE)
	{
		exit();
	}
}
public void setYCDefaults(YCDefaults newDefaults)
{
	ycDefaults = newDefaults;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 4:52:55 PM)
 * @param parent javax.swing.JFrame
 */
public void showAdvancedOptions()
{
	this.addWindowListener(new java.awt.event.WindowAdapter()
	{
		public void windowClosing(java.awt.event.WindowEvent e)
		{
			exit();
		};
	});

	//set the app to start as close to the center as you can....
	//  only works with small gui interfaces.
	java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	this.setLocation((int)(d.width * .3),(int)(d.height * .2));
	this.show();
}
}
