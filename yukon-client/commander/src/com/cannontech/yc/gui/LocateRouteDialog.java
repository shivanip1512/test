package com.cannontech.yc.gui;

import java.awt.Frame;

/**
 * Insert the type's description here.
 * Creation date: (2/25/2002 3:46:52 PM)
 * @author: 
 */
public class LocateRouteDialog extends javax.swing.JDialog implements java.awt.event.ActionListener
{
	private javax.swing.JButton ivjLocateButton = null;
	private javax.swing.JPanel ivjLocateRouteContentPane = null;
	private javax.swing.JLabel ivjResultsLabel = null;
	private javax.swing.JComboBox ivjRouteComboBox = null;
	private javax.swing.JLabel ivjSelectRouteLabel = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JLabel ivjDeviceLabel = null;
	private javax.swing.JTextField ivjDeviceNameTextField = null;
	/**
	 * ClearPrintButtonPanel constructor comment.
	 */
	public LocateRouteDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		initialize();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/3/2002 4:02:28 PM)
	 * @param event java.awt.event.ActionEvent
	 */
	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		if( event.getSource() == getCancelButton())
			exit();
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
	 * Return the CancelButton property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JButton getCancelButton() {
		if (ivjCancelButton == null) {
			try {
				ivjCancelButton = new javax.swing.JButton();
				ivjCancelButton.setName("CancelButton");
				ivjCancelButton.setText("Cancel");
				// user code begin {1}
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
	 * Return the DeviceLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JLabel getDeviceLabel() {
		if (ivjDeviceLabel == null) {
			try {
				ivjDeviceLabel = new javax.swing.JLabel();
				ivjDeviceLabel.setName("DeviceLabel");
				ivjDeviceLabel.setText("Selected Device:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDeviceLabel;
	}
	/**
	 * Return the DeviceNameTextField property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JTextField getDeviceNameTextField() {
		if (ivjDeviceNameTextField == null) {
			try {
				ivjDeviceNameTextField = new javax.swing.JTextField();
				ivjDeviceNameTextField.setName("DeviceNameTextField");
				ivjDeviceNameTextField.setEditable(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDeviceNameTextField;
	}
	/**
	 * Return the LocateButton property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JButton getLocateButton() {
		if (ivjLocateButton == null) {
			try {
				ivjLocateButton = new javax.swing.JButton();
				ivjLocateButton.setName("LocateButton");
				ivjLocateButton.setText("Locate");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLocateButton;
	}
	/**
	 * Return the LocateRouteContentPane property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getLocateRouteContentPane() {
		if (ivjLocateRouteContentPane == null) {
			try {
				ivjLocateRouteContentPane = new javax.swing.JPanel();
				ivjLocateRouteContentPane.setName("LocateRouteContentPane");
				ivjLocateRouteContentPane.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsSelectRouteLabel = new java.awt.GridBagConstraints();
				constraintsSelectRouteLabel.gridx = 0; constraintsSelectRouteLabel.gridy = 2;
				constraintsSelectRouteLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsSelectRouteLabel.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getSelectRouteLabel(), constraintsSelectRouteLabel);
	
				java.awt.GridBagConstraints constraintsRouteComboBox = new java.awt.GridBagConstraints();
				constraintsRouteComboBox.gridx = 0; constraintsRouteComboBox.gridy = 3;
				constraintsRouteComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsRouteComboBox.weightx = 1.0;
				constraintsRouteComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getRouteComboBox(), constraintsRouteComboBox);
	
				java.awt.GridBagConstraints constraintsLocateButton = new java.awt.GridBagConstraints();
				constraintsLocateButton.gridx = 1; constraintsLocateButton.gridy = 3;
				constraintsLocateButton.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getLocateButton(), constraintsLocateButton);
	
				java.awt.GridBagConstraints constraintsResultsLabel = new java.awt.GridBagConstraints();
				constraintsResultsLabel.gridx = 0; constraintsResultsLabel.gridy = 4;
				constraintsResultsLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsResultsLabel.weightx = 1.0;
				constraintsResultsLabel.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getResultsLabel(), constraintsResultsLabel);
	
				java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
				constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 4;
				constraintsCancelButton.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getCancelButton(), constraintsCancelButton);
	
				java.awt.GridBagConstraints constraintsDeviceLabel = new java.awt.GridBagConstraints();
				constraintsDeviceLabel.gridx = 0; constraintsDeviceLabel.gridy = 0;
				constraintsDeviceLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsDeviceLabel.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getDeviceLabel(), constraintsDeviceLabel);
	
				java.awt.GridBagConstraints constraintsDeviceNameTextField = new java.awt.GridBagConstraints();
				constraintsDeviceNameTextField.gridx = 0; constraintsDeviceNameTextField.gridy = 1;
				constraintsDeviceNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsDeviceNameTextField.anchor = java.awt.GridBagConstraints.WEST;
				constraintsDeviceNameTextField.weightx = 1.0;
				constraintsDeviceNameTextField.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getDeviceNameTextField(), constraintsDeviceNameTextField);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLocateRouteContentPane;
	}
	/**
	 * Return the ResultsLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getResultsLabel() {
		if (ivjResultsLabel == null) {
			try {
				ivjResultsLabel = new javax.swing.JLabel();
				ivjResultsLabel.setName("ResultsLabel");
				ivjResultsLabel.setText("Results!");
				ivjResultsLabel.setVisible(true);
				ivjResultsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
				ivjResultsLabel.setRequestFocusEnabled(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjResultsLabel;
	}
	/**
	 * Return the RouteComboBox property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JComboBox getRouteComboBox() {
		if (ivjRouteComboBox == null) {
			try {
				ivjRouteComboBox = new javax.swing.JComboBox();
				ivjRouteComboBox.setName("RouteComboBox");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjRouteComboBox;
	}
	/**
	 * Return the SelectRouteLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getSelectRouteLabel() {
		if (ivjSelectRouteLabel == null) {
			try {
				ivjSelectRouteLabel = new javax.swing.JLabel();
				ivjSelectRouteLabel.setName("SelectRouteLabel");
				ivjSelectRouteLabel.setText("Select Route to Locate.");
				ivjSelectRouteLabel.setRequestFocusEnabled(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjSelectRouteLabel;
	}
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		// exception.printStackTrace(System.out);
	}
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("LocateRouteDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(362, 177);
			setModal(true);
			setTitle("Loop Locate Route");
			setContentPane(getLocateRouteContentPane());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
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
	/**
	 * Insert the method's description here.
	 * Creation date: (4/11/2002 4:52:55 PM)
	 * @param parent javax.swing.JFrame
	 */
	public void showLocateDialog()
	{
		this.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				exit();
			};
		});


		javax.swing.KeyStroke ks = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true);
		getRootPane().getInputMap().put(ks, "CloseAction");
		getRootPane().getActionMap().put("CloseAction", new javax.swing.AbstractAction()
		{
			public void actionPerformed(java.awt.event.ActionEvent ae)
			{
				dispose();
				setVisible(false);
			}
		});
		
		//set the app to start as close to the center as you can....
		//  only works with small gui interfaces.
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(d.width * .3),(int)(d.height * .2));
		this.show();
		//this.toFront();
	}
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
	/*V1.1
	**start of data**
		D0CB838494G88G88GA3F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BA8BCCDC5715448687FF1260AED25844DD31291B5246DB3BDDD77636F4C5C75EB6ED3D2AF7B75E3AABDAF9D52BC22DDBF99B54BA2D15DEE6E0E0ECEC3081E3DC6FFA62CF62A4AECB93D7F190A4E0868792E2E0986309CA958F4C839E3C1977BC738606CF4C1EF3BFEF9E70869CCB51DABA3E6F1EFB4F671EFB6EB9675E01D41F4DAEA932D793D2D4C2783FB70A88597FAAA1D75232D78BDC0AE4F39253
		3F97G0E134DB70A214D00314C8C1B33127CF3EB9974B7C25F77084DD98B5FD564DFDBB73E8C9FC9644C4258A3EF7976ACAD47F6D748A9257DAAEFB93447GF2G07ED210472DF751E62781D9CAF2048CE4876E11CF35DFB06632EC23F87E09DC076F0E23E0BCACEEB3C4A7B836B33C961F7DE329FC5BEA61E824888233D40DBB759DC34F384E529D4CF249900FEA6GAE5EC17EA07F815A261E7111370654D07DD0A09CD6433AB4B4DABFB5D4BF92139B9B3B542100AEF95518AEF96400220E407C0ED164394DDB
		C6F990D2897DE7A86E5B55A8E79DFCEF844885E0EBE8FE29096B681F59CC36FEBF6C28C83206D3B2533DC5CE2CA1B3761B52BAEAEFBBC13B4E42D8B640D600BDG8781CE0D591C3F277B6C1AFB855A56C13DCD8D0554F02B26DDD302117755202440F7E3A30CF01CD79AD2A3C1C258DE463C17AD74D98A985D4A0FCD47EC12733A5857CD5EA7C97E5F3FE7AF3530C9FECE033DE10C5922E3ACE193A6BBB7296C513179FEB7DF76ADE2163DF1156AA831103DF5774B76D94EA743A4BBEF8C773A398677FA8BFC97F1
		BFE078F7A83EF8BC01E7BE78BD6A9B582F06317838708D7FB121CBD771E324720FDD16F64832355A734779999BCF68427C62089D4F12DB31B91BGFCG8A40BA009C00DCC57806F76EFF6F51B7FA82917DDACCD602D204B05AA364A434D2DC0F84BA43D1BD909E127A8391B9B028C8D14267D4D31F981CC7D7C7FDF688E43CA30525C8C01755B0F08F04A4DD0AC4591CF330861893116828CF8EC9A0A02491417795GC214C0D43F298561BCA6E6307157683ECE847A01A3B000EFE6172B8E34F9BE2C3F9020B8A4
		6C70F7145FFBD2A4ACA9F84655711866431590DF88776A5F6859BE855F8DG0C5715E244E5C1FF0B41E79FB9E162631107D07DC0E4CA1C0D369A0C17BBC2E22FFF6197FB6DB97AD9F2F9773B16FB0D30AF447678D0E8214F3FCFF922DE2C3F3B5AFF8FE7EEC7487ABC5C5B997F45BD49F05C877FE346DEBDD102FBF5A1ECF336G5C8E8B9B1FADF2B31F3D9608CA42CB3BA360E0D2B0E1F1B637460EF1D51A10423AC75224F090BE225CC74E58DD897F9DD68D5FBAC7719A78FB9BB8D85440DF3223CF86F4BDA28F
		C2746F13F4DD8E0FA4469B282CE8ECD0CB9C95A16B8DBB4693F5927C5D185FE4BFC14FC1B8ACD1258D92BE7E9675D5C911C220F41B9A8B6B8C6FF160FE0FCBD37C2CF4B81C545715981EBB164736840E609AA2F288CC01C7176C8C239F7F54484388F140E1BE4E06F6EBD8786464F9611397267F1664FDF851EE958B33BE6B31D72B4C07AE29891FE4BA3DD9EC2D7B5B744CF0FC1469FA7AAE5A55AA2FB2BF780993F9F5000C81GDD95FE703562BA53D9638E5026AA8AB327E1671C12CA5C9769CECC029005077A
		BD43F61BCBCEAC986B67F6E4635BCA500E0AA99EB01D3DF7D0B62D9346829301F8FDF492FC2131715D85B5012F8F570BF6DD270A1A20349F5733837A79G052A307B3F9C95B5C166F8B6597B1953C96B1AF20D593BC1CB585D3DD254848DE5A8D3033194CD582A59E90EEF2D5128BAA453504A57395FE95E97067BAF67E1E80765C8D46F04039397B679B55597E3AFC7731870C19F665ECC4DBA2E64F0FCAEEFF7C14B72638C3547BEEDE15ECC2378C2EDE15EBC5AAF72E6719D1BF3740E0847A1977A76CE6897G
		108854C0GD7817C91E10739525749B2792E936A3DC8B820744DBB48FC6DFFAAF33033477BEF1615083387AD4B959A5DF39DE4E681B05C37A8EE9374F798386BB791D7G7DC3866E910A2B0C5A1C9722A21EBF50CF633B51DFCB7BBE23DF55FFAF3178FEA0A17372A7A643E11C5389D772D87D114BBAD62FF4D9476A552E638C8FBC4C150B98DF6FDABA167F354BAA16BB685803FDB550862561C0CC8131EAD46A56F8998378ADD15C47A768FE23EF442362EE7125C90CA543D0680028BECD9AA269BA4E7DD11F08
		4905B0378CF017GBAGC681184C3F5CC49EA1B598D08C997ACDF15F58E54CFB124E53E5DDC1FF5C2B631DAD02610F6B3E229DEBB6D32CED436B8D5AA88C84ACC764761BF34405982BF9BA209D0889B97FC04F3AA974555F1243C1F59258E1A528C76BBD1DFDBD5DFD6D9FF65F78302D8BBED80C4B1EB45F5D2E9EFE43B237A068B196A752F91B7B60F99ED30BFDE51CA68F468E469017612BDE60D3C3B0219EDAB6EE1BB70EFB7F95E29EAF1CB70E3A6E361B47DF3CE99EC71D7F0A5333F5FD7722085D073E1BC3
		142F3C19B4F637CC307584F95B53FF1EDCD8735BD2E65735B771EC6B11184478FE6F4FE6FBD5BE76023DA8895F8D1CDF4EC4A2A7B03FFB76E55CCF0BDC051B2EB01BFE611670577C89112736CFE31C2D1E34B9AF83F8A6C51C7D65F11CBBG7D7824082B5BAADF0536BFB046A3EAFBD05655888D29006C0928F0C274A906259916670C915F7D5D953FD1AA6C3D752BCF106D2B1EABBA1244AED9134C9E5BF9DB60BBEF2AB1872A50B685B0D69C07F38750826083D062E2ED4FD7220E1099D07DD6DD8F8C0D52ACA9
		92C63FACCDA650F830996FB9F4999ECFBC47AFA6338FF0F8973625FBF08C6A96DFGEB87E61B4D37965AA6A51E482D197145F916654D26995C371CB84B1B95863E0B62F71970AC1F7EA35DE76C9F04B1FDCA2C7B5747112791A359367175BCFF9C97A707E59D6A9DF91A863FBE18989EE1EBDFD703638700D779B4448FG8F00FFDA70DEF1629C332905BD5B03A31208A3C21ED415BCB7A8D54B5FC3E3D5D84B8C2A3A2E06103F404FD4212DF5D523C806CB29C29F0B48A323BA1B4A712B2A506F95E91822D93E7E
		A6BDB7BA2CABFDDA6470D553A257EF1A9679735A8C5E47F7C1FFBFC0590C4D6986388A5083B0BAA366F9693CF46867GBC8DD0GD0B68B3E8950B1AB666D1856781EA30FFBBDBBEE53DC140BFA20BE589FDD024FAEBE07497E31A17B4847BC37627BA867D5427B95CB5009BBA45B034C1A64FE70F84D72FE303106563938FFD31A44F01F2F417B13BABC9C158CFF7BE2CD25016B0C7628D199BD1C45689F5E4279CAE08A72A7A14F4C527CF1CB644CD16827G64G6482E01DF666A31C736969D34B2C330A7C6E9C
		4A6D13708EC373F3D7E0D0D2585844B1349B4F5A21C1751A9AB768FEDB0BE7186575EBB170F22EEBE79071DEA98A25CA5444EB75B0625B50C64A3C79E15CD30FB4A18FB1511C7F2A16AA83FFA39012FCE0666B32248449863A3655B3667C987C082DBB9DDAB65E5BE29E5F47474B6F0A7161E973387BAE9B6FB046AFE9D67C870C71FE2F953DE60C3BDCD674294678571B4C63D91CBE8FDA96A34F7845FB71B300AB1F93B55D9FEF63F9B5F2EBFDE0D2EFECFCA7A2872F85C63A2807307C9A740B1A45B3A7726B27
		A84FD968E782EC1A937740780BA237EDFA72B15215721EE5AD3157771AFD479C4FE9F3091C1F937BCDA963B3B6AE781CF96AF1BA162CA63910425FD3D392FCC41D837543F0127A01BA88DBDF5ECC99BF5369A9E29D9FDDB75FE7F3ABDE33A74B51F9DC6EDE137C4B308EA6FFC4793D35FC6394B09D52DBC4BD6CFEC06CCFC5DF12FBBAF3FAA26E690ED9D17F78011661BE549007BAE79838073CE25E53862E45A5F0E40510FB715299EB394641E13405CD02766A8A915B4F796F6F5D3D61BE69640FCD475F35E95D
		202DE075C411016F2178D9931E1D21AC638C2543587E95221E4C2BC57F6AD989B597C0AA408600DC00BDABC53D50F63201ACF3EF7729DA97A48645F45DE37CAF56A17F7D402BAC95EAD9G8F009FC0CB957C9FBA096F82496AD72F2A529B92DE1682F2D80A5072A4821FBA3761BF4DD859B0B51579F3D6EAA237EE625FCC37C7CEE35C4983DCBEC0A1C0F99A6818A6747A4149E55FAB5A63166F95CC2F07DA165A5B2E3445B91F51154DDA515D66737524F4C14D0AAE1D4F5FG6D96009DGFB524CB54527ACFF37
		594F6710D54964353B2C682A797CCBC9691EEBDA4AAECDC0573300163F35CF93633B5F747DB77ECF6AFDA4A1FB5BA7AA03795B37EB1357D73BEA2D5FE07E3D567A8DA63FF669B716FD35495F4BFFD63B783D3C3EAE39EEFF2ADB3E76FB2B4E7A8E6029DBFC87682C333E83F457AD3E8364535C9784FF51D709BBC06B2C3883249838DF3E0D38DD50BF30CA1C6FC7CFA7DF53CFAB2C6DFD20425A5E3F2AD87A4D6B790AE47B41745B58A2F4AEFED068FCF6D62C4DE760D20C355DB6F0B31A1837454065EBE2DE2E01
		9B6CC5DCF9BA5CFF52856EA90A3B02FD83773BDEE12BD18377F91AFBE7213F4E409DB35ED5338C5CG4D478550BFE8603E61B25636DA60AA1B854EE760C255746FA4203FFD35F86BFAF6063E4940C6307734B4B4A3593F9A677BE7BDCB1CA94CF52C9E606F57C003F01E0F6DDD1D4811CC160B5659917CBDA22ADFD707E25176B0BAF1103F791DEF91F56415B510C3298EDFEBD9CE07D2138E9DEB188EB5DC3701B5899D587AF2CEA4567768126BF3AC60AD56D743790DAE62FD20AA417BD9EDB93DDDA65E7A823D
		5357AC345DD4AF6AB2A1C7E57C1D0659AB23D7540419EB44FE5AFAD109D1B5A2CFE3BED7DA95F9A40CAF901CA63FD758F8A350B05CA52F40BDE37049734E778BF7CDE2ED977B445A8A56AC4EC10E85EBBB84F390045DAA57AEDCDB54B5DFD65B49042CBDAEA16B4C5A45324CFC909AE08E0270C10FC9966377D233605713041F4384C193BF7A5E3DC87754EAF66716F574E4E64F472B05BDD3E17ED9BA0F904409FFEC1DCF611FAA71B73F7FACB75FE7BC5FFF2EC82CE1210ED9DC6F0235097BC44158A17E465A58
		02FA8E8714A8467545328F3C4DE43FD2175043FF1AACB9F7BD06D83E5E4F750AB7DC9EBFFA71EF254CEF65FD78FF62293C2D7D062F5DEB503F6DB56B476E897D466F896EF5CB596B4D7FA6860D6B30F127D912E660FF92B47EE6C113E1C9A356BBF7B6315EAFD71B75653F453951071E869E7BG4A9E02F88FF085208B60B6008E108E108D309DE0A740DE00C2006207ED4ED3G179F96F972C0AD7E29DA327A1CDF38CDD53928197E5CE72E73DA001F6F61255F4B985D3143E63AD120193DA73A3FB41B69B200E6
		63B2F4ECFD8F38135781696E65EB1B75EE6B5A66D177625AE6035B3A36F942ED2EED986EF3EE5C5B91302F5F18374940FDE0603EC4F9A26E169C54C78D7CB62A9762BBA494A870DF27FACDD21C1F63ECA65C879C77ADB73E354112A1DC8B5A1DEEBCDF433222703A144E1143D1C9B76C311B6AA8EB81135E7FE160D0EF166FA374DCEDFF1845AC061B2438DD73F0978FAF1E774A61457374664573261A4D73D8BD90F8311C986F6754E48CFFA6FEB40197A6A36C6FB5DFF87F8AB05874F3D2221363DD2A2A3959FB
		1F1BBE896A2416BD9B322EDBD779F03DCE1C6CCBA7E57CA57085403147BF1DD4B2B2A968E64FFB0D518829900FD9751D542CFBA7DEEFF47C78B38EBBA96EA4CE66E61D2498FD251314D0D76AA48EF033384617E8C0748E39CB1C7F3FD816BFDE1EB37F16F6B06DD4525F9E77E430B33BBFE3615F55BD590DFBF9E93D4D69D9AFE2532FEE605F0CA67BED27AF94D09428B246DB43C17C1D675A14AEA5EA76273A11FFD44C6103EC6C4B5D28771809667FGD0CB8788CCE6B7C4A790GG94ACGGD0CB818294G94
		G88G88GA3F954ACCCE6B7C4A790GG94ACGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE190GGGG
	**end of data**/
	}
	
	public void addItems(Object [] allRoutes)
	{
		if( allRoutes != null)
		{
			for ( int i = 0; i < allRoutes.length; i++)
			{
				getRouteComboBox().addItem(allRoutes[i]);
			}
			if( getRouteComboBox().getItemCount() > 0 )
				getRouteComboBox().setSelectedIndex(0);
		}
	}
}
