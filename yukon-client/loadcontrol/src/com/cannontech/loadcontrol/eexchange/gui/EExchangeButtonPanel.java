package com.cannontech.loadcontrol.eexchange.gui;

/**
 * Insert the type's description here.
 * Creation date: (9/27/00 2:21:31 PM)
 * @author: 
 */
public class EExchangeButtonPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private javax.swing.BoxLayout ivjButtonBarPanelBoxLayout = null;
	protected transient com.cannontech.loadcontrol.gui.ButtonBarPanelListener fieldButtonBarPanelListenerEventMulticaster = null;
	private javax.swing.JButton ivjJButtonEnableControlArea = null;
	protected transient com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener fieldEExchangeButtonPanelListenerEventMulticaster = null;
	private javax.swing.JButton ivjJButtonCreateOffer = null;
	private javax.swing.JButton ivjJButtonCreateRevision = null;
	private javax.swing.JButton ivjJButtonViewRevisions = null;
	private javax.swing.JButton ivjJButtonShowOffersCustomers = null;
/**
 * ButtonBarPanel constructor comment.
 */
public EExchangeButtonPanel() {
	super();
	initialize();
}
/**
 * ButtonBarPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public EExchangeButtonPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ButtonBarPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public EExchangeButtonPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ButtonBarPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public EExchangeButtonPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
//	if (e.getSource() == getJButtonEnableControlArea()) 
//		connEtoC5(e);
	if (e.getSource() == getJButtonCreateOffer()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonCreateRevision()) 
		connEtoC3(e);
	if (e.getSource() == getJButtonViewRevisions()) 
		connEtoC6(e);
	if (e.getSource() == getJButtonShowOffersCustomers()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener com.cannontech.loadcontrol.gui.ButtonBarPanelListener
 */
public void addButtonBarPanelListener(com.cannontech.loadcontrol.gui.ButtonBarPanelListener newListener) {
	fieldButtonBarPanelListenerEventMulticaster = com.cannontech.loadcontrol.gui.ButtonBarPanelListenerEventMulticaster.add(fieldButtonBarPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * 
 * @param newListener com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener
 */
public void addEExchangeButtonPanelListener(com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener newListener) {
	fieldEExchangeButtonPanelListenerEventMulticaster = com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListenerEventMulticaster.add(fieldEExchangeButtonPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC1:  (JButtonShowOffersCustomers.action.actionPerformed(java.awt.event.ActionEvent) --> EExchangeButtonPanel.fireJButtonShowOffersCustomersAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonShowOffersCustomersAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonEnableAll.action.actionPerformed(java.awt.event.ActionEvent) --> ButtonBarPanel.fireJButtonEnableAllAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonCreateOfferAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonCreateRevision.action.actionPerformed(java.awt.event.ActionEvent) --> EExchangeButtonPanel.fireJButtonCreateRevisionAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonCreateRevisionAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  (JButtonViewRevisions.action.actionPerformed(java.awt.event.ActionEvent) --> EExchangeButtonPanel.fireJButtonViewRevisionsAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonViewRevisionsAction_actionPerformed(new java.util.EventObject(this));
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
protected void fireJButtonCreateOfferAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldEExchangeButtonPanelListenerEventMulticaster == null) {
		return;
	};
	fieldEExchangeButtonPanelListenerEventMulticaster.JButtonCreateOfferAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonCreateRevisionAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldEExchangeButtonPanelListenerEventMulticaster == null) {
		return;
	};
	fieldEExchangeButtonPanelListenerEventMulticaster.JButtonCreateRevisionAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonDisableAllAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldButtonBarPanelListenerEventMulticaster == null) {
		return;
	};
	fieldButtonBarPanelListenerEventMulticaster.JButtonDisableAllAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonEnableAllAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldButtonBarPanelListenerEventMulticaster == null) {
		return;
	};
	fieldButtonBarPanelListenerEventMulticaster.JButtonEnableAllAction_actionPerformed(newEvent);
}

/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonShowOffersCustomersAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldEExchangeButtonPanelListenerEventMulticaster == null) {
		return;
	};
	fieldEExchangeButtonPanelListenerEventMulticaster.JButtonShowOffersCustomersAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonViewRevisionsAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldEExchangeButtonPanelListenerEventMulticaster == null) {
		return;
	};
	fieldEExchangeButtonPanelListenerEventMulticaster.JButtonViewRevisionsAction_actionPerformed(newEvent);
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/00 2:24:47 PM)
 * @return javax.swing.JButton[]
 */
public javax.swing.JButton[] getAllJButtons() 
{
	javax.swing.JButton[] buttons =
	{
		getJButtonCreateOffer(),
		getJButtonCreateRevision(),
		getJButtonViewRevisions(),
		getJButtonShowOffersCustomers(),
		getJButtonEnableControlArea()
	};
	
	
	return buttons;
}
/**
 * Return the ButtonBarPanelBoxLayout property value.
 * @return javax.swing.BoxLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.BoxLayout getButtonBarPanelBoxLayout() {
	javax.swing.BoxLayout ivjButtonBarPanelBoxLayout = null;
	try {
		/* Create part */
		ivjButtonBarPanelBoxLayout = new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjButtonBarPanelBoxLayout;
}
/**
 * Return the JButtonEnableAll property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getJButtonCreateOffer() {
	if (ivjJButtonCreateOffer == null) {
		try {
			ivjJButtonCreateOffer = new javax.swing.JButton();
			ivjJButtonCreateOffer.setName("JButtonCreateOffer");
			ivjJButtonCreateOffer.setPreferredSize(new java.awt.Dimension(105, 23));
			ivjJButtonCreateOffer.setMnemonic('c');
			ivjJButtonCreateOffer.setText("Create Offer");
			ivjJButtonCreateOffer.setMaximumSize(new java.awt.Dimension(105, 23));
			ivjJButtonCreateOffer.setMinimumSize(new java.awt.Dimension(105, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCreateOffer;
}
/**
 * Return the JButtonDisableAll property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getJButtonCreateRevision() {
	if (ivjJButtonCreateRevision == null) {
		try {
			ivjJButtonCreateRevision = new javax.swing.JButton();
			ivjJButtonCreateRevision.setName("JButtonCreateRevision");
			ivjJButtonCreateRevision.setPreferredSize(new java.awt.Dimension(123, 23));
			ivjJButtonCreateRevision.setMnemonic('v');
			ivjJButtonCreateRevision.setText("Create Revision");
			ivjJButtonCreateRevision.setMaximumSize(new java.awt.Dimension(123, 23));
			ivjJButtonCreateRevision.setMinimumSize(new java.awt.Dimension(123, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCreateRevision;
}
/**
 * Return the JButtonEnableControlArea property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getJButtonEnableControlArea() {
	if (ivjJButtonEnableControlArea == null) {
		try {
			ivjJButtonEnableControlArea = new javax.swing.JButton();
			ivjJButtonEnableControlArea.setName("JButtonEnableControlArea");
			ivjJButtonEnableControlArea.setMnemonic('a');
			ivjJButtonEnableControlArea.setText("Enable Area");
			ivjJButtonEnableControlArea.setMaximumSize(new java.awt.Dimension(110, 23));
			ivjJButtonEnableControlArea.setPreferredSize(new java.awt.Dimension(110, 23));
			ivjJButtonEnableControlArea.setMinimumSize(new java.awt.Dimension(110, 23));
			// user code begin {1}
			
			//this does not seem to fit in EExchange
			ivjJButtonEnableControlArea.setEnabled( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonEnableControlArea;
}
/**
 * Return the JButtonShowControlArea property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getJButtonShowOffersCustomers() {
	if (ivjJButtonShowOffersCustomers == null) {
		try {
			ivjJButtonShowOffersCustomers = new javax.swing.JButton();
			ivjJButtonShowOffersCustomers.setName("JButtonShowOffersCustomers");
			ivjJButtonShowOffersCustomers.setMnemonic('s');
			ivjJButtonShowOffersCustomers.setText("Show Customers");
			ivjJButtonShowOffersCustomers.setMaximumSize(new java.awt.Dimension(135, 23));
			ivjJButtonShowOffersCustomers.setPreferredSize(new java.awt.Dimension(135, 23));
			ivjJButtonShowOffersCustomers.setMinimumSize(new java.awt.Dimension(135, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonShowOffersCustomers;
}
/**
 * Return the JButtonViewRevisions property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getJButtonViewRevisions() {
	if (ivjJButtonViewRevisions == null) {
		try {
			ivjJButtonViewRevisions = new javax.swing.JButton();
			ivjJButtonViewRevisions.setName("JButtonViewRevisions");
			ivjJButtonViewRevisions.setMnemonic('i');
			ivjJButtonViewRevisions.setText("View Revisions");
			ivjJButtonViewRevisions.setMaximumSize(new java.awt.Dimension(123, 23));
			ivjJButtonViewRevisions.setPreferredSize(new java.awt.Dimension(123, 23));
			ivjJButtonViewRevisions.setMinimumSize(new java.awt.Dimension(123, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonViewRevisions;
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
	getJButtonEnableControlArea().addActionListener(this);
	getJButtonCreateOffer().addActionListener(this);
	getJButtonCreateRevision().addActionListener(this);
	getJButtonViewRevisions().addActionListener(this);
	getJButtonShowOffersCustomers().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ButtonBarPanel");
		setLayout(getButtonBarPanelBoxLayout());
		setSize(665, 34);
		add(getJButtonCreateOffer());
		add(getJButtonCreateRevision());
		add(getJButtonViewRevisions(), getJButtonViewRevisions().getName());
		add(getJButtonShowOffersCustomers(), getJButtonShowOffersCustomers().getName());
		add(getJButtonEnableControlArea(), getJButtonEnableControlArea().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		EExchangeButtonPanel aEExchangeButtonPanel;
		aEExchangeButtonPanel = new EExchangeButtonPanel();
		frame.setContentPane(aEExchangeButtonPanel);
		frame.setSize(aEExchangeButtonPanel.getSize());
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
/**
 * 
 * @param newListener com.cannontech.loadcontrol.gui.ButtonBarPanelListener
 */
public void removeButtonBarPanelListener(com.cannontech.loadcontrol.gui.ButtonBarPanelListener newListener) {
	fieldButtonBarPanelListenerEventMulticaster = com.cannontech.loadcontrol.gui.ButtonBarPanelListenerEventMulticaster.remove(fieldButtonBarPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * 
 * @param newListener com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener
 */
public void removeEExchangeButtonPanelListener(com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener newListener) {
	fieldEExchangeButtonPanelListenerEventMulticaster = com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListenerEventMulticaster.remove(fieldEExchangeButtonPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GAFF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4D457197897C90206C4CC0DF14F212949328D1B10546E1A9EF763921B5244F356F6CDC33BE4175343C6764438C94BEEECE33B178186E8089A4506A426A1D12BD41670270CC8D4849A0D4490C1C214A08F66818FE7668DB3EF06C1D0767B6EFD773DB770069949A967FC673D7B5D7B7D5C6F7B6EF73FFB67C10A5F0B4F4B369493121DC7543F3AECC21A0F907241AF9E286638A8B159CA8CFF6F83
	3CC1A6BF4D05E7AC74ED3EA7593A13541CA80076CAE8C7AFCB363E896F3B096DC4AAF289A1A7897A763CFF63524CF2EEEF67F272E97B44E491BC3783A4G8E1FD9A24A6F1CACD1716BD5BC07EC8BA12983B8E6E4F22F0A33C13B8DA08EA0E1000DDF8B4FC2AAF9C13DCD9D57FDE7BC49FA6A2065F564E3604921F41051B62B4FD272F07666C114351C6A09B49350DE8500134FA171D29DBC9B5A2E8E1E68171D957D024BA53B9431FF28C294837DC302EBD02C9874C97575ADADEA7334CFD1E4D71B60929DE4E888
	F97FFC320072A224925AD994F7FFAD4ACB0477F5GE99A5EF18471B689668BB0A471B9FEF430C11DC3577843E455332E1C9DA16698A53139A5C87A9C63FC3F4EFF0B7A63698E34FB8674958CA7DB77821C8268850882F80746410F5B7F844F46BE25C9F6BAE5D7235BFD5AE177FCAA5BC5873C5757C30F0A3BA0764B9EBBA14C57711365A67A4C840C6E211B26E3B6C92861FECF3C70GC97B4EB9CB3E09CD5292AAAD4B0619AD1206F51BB0591BC24A5EB09C9C1741323FA0C659ABEF11F372CCE42FFA264C1221
	7259EA10FD68AA7A7AAD6A6B92F84FD663036177D13C4300E7B17AA00D99ECCFC0DF7AD59E9BDD5B39AE2D394B494E2AD6D3BBACCBEE34B4BB188E5D0E293A24D319518EA673B5CD17AD941FE040B3DDBC940F6D9468ABF7A6DBD9FCED6C44B59B8B6DC400F5GE9G5BGCADCB04E45E36C1D0E8FA30C31B641231C76C98E3B68A10CF6E3476F61A986940FF05665D584D73F58A9F8A4214FA1FA09BA66398E34C1DF905D4FE96C77030C0FC517689194895729C7F00A0A687132B1AFF43C01E3BCA26D6D101CA2
	88F03A89677B4B0E5D689B412BDCF45B85C554C7307ED7BB50A7FE2193B88283F8E7F6496EC2FB35427C6DG0AE6877FC564F7CE74C03A282FEF15652BBEF7874E042CF3211DFFCBF3C7B43C473B380DFF53053882B959DAAAF3BEFFFBF11701CF33540F6A8B1EB13E465EBB02F939D666B133250B47CC736B771372E7BEB10D9904B619454A10BCF56D14D11E2897EBBFD25BF5136BF83B2AD364396229D9494809F09C336F55CB682BD850AB91E03DE6634C0B25ACE6CF8BDE11C769E79E88B0512EDB1C69797C
	9158C0E92EC05AF75535D2254DA30B76E7G6FA2F7323596209BE03E1B5BB2F044B84FAA22671E57424C014B8BEF4BBB03E4F03EB92A1C196873D49E617458CD7E2D9377A951AF3A14E651AD3A6C7062D57B7E239365286BF8C056E8DE20F8B72C7BA6D8E8DE8D7FF2A72ECDC1D1BCD21FCF915BC5C511DC03FA7F2F28AC2F2F4F2D278CAE0BFC01BA4B23306E3571F9DF601A6917DDAE11BACFA3D17B73691A959D229314EE12FDAE0561DF6F42383FAA0E29B9630DAEAC047C0243077907558DC9825A536D111C
	9092184248B6B72E67BD2444E023C440E17D33811E99EE3EB6C7777135397F5AB7495AAB87ACE6FB4B327B1BAD4DA3EC6DF40D68EB1369E47952DC7702AF8BF43C1769DAD005342CEEB9ADF8D869222E05151711CFG7847G2C9C61EBE17E17584F730D3A881AE4070319D2337148251D6893F144A7C21A4644F6CE33DB60522EA9FD1D2A8DD9FF54A5342143109363BDE84332233CAEDC837AEC84489AE1F62C057E98GB6CE296363B6C3BB571BEC5D6FE5636C70DC6D65639E3771F16B8117E198172B403AD4
	78387FBA46473981978D102030F11970EC7071F175477938B6408D81C47B583894F86E77735A7217E5988B34369C967CC22042BB8A71DCDF7F8975G2B9DA7C9013A5EECFEDEA3BE1903BEF1C3BB9AA05E4F6366C72F739AB16EEABCC97F565BA16B6055FE96AFE9FEBDEECA67719AF1CBB74A5CBB8A7B75A877772B170C7BD42357AB77CBF40BD4FD756D4B4638E23807AF7F8E1E8312472B1C05059F60FE7D6EE5549777D0952D5694970E727A6694488DGEC9EC5FC2E3F5820FB52A85B8BD36119C379BDF694
	0FAD828D3C8A76E893BD833247A95A8F21E5AB04D1250266200AC65A5D026FE5FC2D3E73F1AB53D37C04AD029697C6DB93947A9E59518835G233AB215F7ABE56ED3A4C7856578D95FB0047F7132CE663B2DC7F95DDF9B60BE3AEF77A99EEB8FADA78D133BADE6F5FC740EF7AD0EG73CDDCC077514423561C4DBD2F66B3FB7DD59D37D7BA0CA99963761A17EBE62F36B1EE2FDC5B5733D7E75F1F036D55046512785940006839D9CB91750C54E463363263BC687CB2EDFCFEABG37D11BDFDD15597C0A2E71F515
	5FF656CC3F8B22DF421A6C66D5D4F53CF10C6BD8FA0D6BD8D24FF56C82DC6CB52E63E3D6B39DD32EF19FCC56FFBD9F648D1F891EE327A40E7299FAE7972F7FD84F6717264D2F7FB81FDF71F83235F51C4F6F56EA33794D9F6773FB6A78571B5FBD7B4E874F2FFDC89E259166ED72F9955989AF330D354286BE4FE5633C5E1ACF261E83B70C6BF5E94678749A151D0F6AF351BEDB47271E1B4ED0FC494454F3537A9CFEEE32C15F0A89DE53DF6F413D00DC07B3B9C0ADC0AB0083A066BA4F419FF55710B0E72633AE
	B84A3884C7FBD091444FAE5DB8E728DB5476AB5DE8EBDA3790DE93F69D8DF796CF389EEC8BC6B79016EE7D94BA66679D6DB8FF13BD10F9DDDBFB477D68B35C4FB22E73DCFA7D4F467DEE4D0EEA4BEBA63994614C8D758CF3C34F25653EDF67B35DFFC3F5CF389159B9C4BFA79C6C45FDFE8D5025823C84D0B419EC2D84E885F0CFF23F4D796A23F0FE23015AA1B7816E8C54BBCDD3EBD24D4E5F2A1B1DFF9E89CB97BB99CC476C7E3D5C82B5DE93A679DAB9E3C3DE6913115B0B51ED314DCE77EDE16932CCF5F7D5
	F15D0B22386E730EA12F66281B557D8F7533533DBCACDDD714196EAFD9F93C3BB55D1F203CB65E346E52715969BE9A16AE53D4773555DCF7B2076B3E3D81F95967C42EBB3B7F3817563F81200B81E038A59644A5C1BBC563FF0A56BC1B21DDB49756A0C09BC0E0AE6FBFCF7BF3557A9EEBF72C4B716C407AFF1C936EDE81F9727722396673E8D37181C37FE6CD4797424AB01E2973C20C61E7F363BDG66F516EBEE6989FD5EDE54E3FE5EDE52E3FE5E3E27678D06879E46DBB85E7F4D1E194F63FF53E3F69EFF17
	6A3BC959C3CC4E5E678527284EE7B73963E821BA26C5111B0EE8F1F94ABF550EAB405EC92A4D570570CD2A89FEB360483CE0BC4B6D8F8998AF4550D789F0862097A0EA9E4F69A72F045D0BDB8226FBB10B6162BA0C61F8601724712C6A8D6DBBDB2F396FCE7462BA734ABECF3FB855BFAD3DA5DA1F3E231CD5C4A71A190DB9CF699541B3A8CE736FC5CA4F7AC2517774060A8166431F5599FDD82D797071DC1EDB562A765F884FCC75FD9BBCF74E8F768B335B95FACE588FFD3573F92DF96018D1C60DA643DB45E5
	B42B3C3A6069D05F236099E7AAE3B92D739320EF35A663E23DD146D1CD46FFDB2746E28A502429FCDF1AEF9E0B398B2663CBGD73E40CC1F1F513A7C947435AD607A449FB76A235FABFE3FFA2ABE3DC02328FC23CD645275E302CF825CBAD3FD8E507AB9957AB2B5FD9EA5F887669046E41F82F97E969C77C36D2EEBAB0C5B89B89BC06BAD6C2C406A3FC7C6B10F8DF884A704CD3BDB6CA72796A26D37AD1CB691462F85C887A050D73A104BFCFB9F5E9FCDBF037258FB33F34F54FEFEC25262730FFFA85647841D
	21D83F755C5BFA3F49990473E9ADAD5147CD3BBFE0F66B23B5ECB4683FE2A15F4B9E2438A4E82F55F0BBC791379E5A999AEEFEAD6236C23BB81A632A7DFC9FBC2461AED0DCAB346D51BC2F7C1D90BA2F7C03E01ED71E904C77049F8AB36F89BF92E64E87076B706E94D2CA554AE854371136831A7EBF2175E6A634F3EF65386B8E4415C03BDC43BDC9F127205D26618EBB91578DEDC543E58FD21BC3BB7ED69EB7FEDB308EB1343DCA23392D0A6B102E617EF59757A1CB43ADA866BA945C46F14F97F39D0AB55C3D
	67388E07EE63BABCF2ACD8079F9C8B56614DFC24E90071ED9A1FDE0A33C3DB51F05F2738A8E8AF53F05DF4FDA6C2BBC543D9A6F82C24E9388FA80E5C1EECAD391D6B75F2FD30DE794731DDFEBB2759C4EB3BAEE8BBB4DCE58D55815A319AEEA9452D0076AA8DB7D7C1DC8A34B3B4DCF2B557EB2B06CBB8C26F9FE3A04747F03D9A9A02753AC05BE7E278BD454B047FFE2FGAEB10667178BE7B0AE271FAFCF4B01D6162CD84DFAAC1B5F89E44470FB5674EC6439F7916881503A081FBB83FF64674E448716135628
	F326772C699D55967BA2B56FAE524F1D89F06EE4B2B750BA966956C17F36C57477BE0B71DE8575E98128GE07D15B946FEDCEBE5C17DFDC17D28EFCD1E31BFAE28BF4FC04F66767481BE37EDCFA79047F70F053CC3EEDE2C1E2955E7DB67BE327F36D773193F46E89EF23AC4A80E247E2445A8BB16621EG08DF4C7D1A6CC7BFAA100032966377A4A7A422650A03F4EFF08A814969F336CB57C442781C247951F6C73235798E4E675EDAFAD67208101DBD221D0EA66A78D5B4F73AE16C84C0429D08BB6BAFB11913
	516E3B09642772A5579479F368BCDE82BE5BB479DF543D87CF17B8DAEAFA7BF4C899123CE52A0DBF920D5F2BD40EE7DBF2C25878D0AC332DCDFD2ED938CF556D455C5073DD13077A8D810D924B750BF3CC7747039477837407B66EB9C730BF9E9B567DF15AE96E0FCBB46FA6810FB59A1F12C1F35BD552FC1C89633234317759A2319D237F8E4D4D25F742DEG40F0393B9177A266A540B13D532E924D9EBB76D0FBC01F22512C23B9B99E5A09F7F2BDAA72275B2805627E85A0D59BB71A9FEC238F1D3A0D52724D
	ED14C579E422FE9A9FEFC3E89F46509CDEFA974C73AE9E33EFCD040F59E39366F67F1C667AAE60E53F0B4BFF77F8A4F6E7E3FFDBBD5DB6FBE80E5F84902B717C38A658B6D7DC3AED92EB4CED330E7209879E099A1FFB145036F9146EA329B0B6C3334DCF2B435B6671A36636F9166EB7BB17A4DB77AE6172079AA2318D2B39DF19F6F79C8E98DDDE766C68CEE8F4E716C4F61F426E24BBAC381FF5AFE1F752599A7EAB0A9FB260591E7AFC363627C2DF6A92FED7FDDA263FDB46A5DB3BG5CG51G89G6B62F84D
	B958D7C9421C0FBBE4F7AB94258EC3994966770CE5F65F037DD3BE5F3BD6FD6FBE327A16DFE5073A8FCE0DD33F49D21F199D7B8CE77FE75CB83FC3CBE1FF81988208820883C8DC4A6777F0DFB91BDF0BDDD29A95C5689F223FFC724961D1C2C76341064555DF7B71F9D0B8286A9CBFEA5C8BEC3F2BB37D2D9FE16DD2751C3CD45F8B980FFF3EEC6431F3797B166C90BC0A6EE634A5F76B7588DB27A731C4D75749D8EDD06C638FB54D92CC830FC5072A2AAA4BCB2BEB4A88236BD3F43A037EC868184EA9DF996B0C
	52433926F546562FBE34342A3A3A8DBAEF9C5E164F647F3BCF179F9D115E4C47B1346ED8F937F6C7AFFDG4FCEE1D80DD4F42B6C215E84A45E4B37431146B5C854F97FE2D4176BF14CA4377288132B56BB8E6A7F7E03AA1F43E3BA1F7F9908048F23BBFFCD27FB3BAAF23A11F11DEE432E19682AEB0CFA4FA926FA7B395EF193BA1F58F31170E1F48DB7F43A95564865F7533A72204465DF195479FC5E90397CA709CE774AF864F47F192D53B92AA3573B3C066A6D65FA976568FCC61519785484796D43DC1D6E6F
	E7149FCCF7A1CF275B640B040E1D41AE5273506A3B591E407272A58B515E7B8D6F1B33E773CD5ECD6E15163F248C16834E49216FC9AE486677A4DD3279BDC90FBC735D78009C7ADBB527EC7CD60D652B8F687784A9600F8C0002EFC02D82D0798D6D7B9C997397FEB5BBFDAFEA318F62E594FEA92BE85F6539E778AE4F9D7E7B5AADEE7C3627CFD69459097CB9BE4B0D7ED2E4B7C52A5F653951C69EE9F0C8E1C3D57C7F3AB1469C62GC533F91E24F5F2AB4CEBC81BDB6C48E192BAB737F9E43768D1469816FEC1
	265A0CDE1B2A32FEB2F2A0948F3050A7309ED8E157A18CEAB67F3C906B00E850E3A5C08AC0BAC096C051B22E5B834A2199F4E3D78C54769EC1625F60457A712E542B6EA7EACE4926779B81C97DF66ACF56507E29320677CFBD7D7DD0843BF60C39553B7A13D614AB8F8CF8C5AD8E1A2DBBB55CD9EF1B6C15700EDF6D4B5FEE0CE1E6DA3ED672B19E2114D6A441A1DD231F02319D1275DFAA8C2D3FD098DE7F61C27378F297CE0FAFDF21F9FC0D979A630B7D0F4BF367AEC2C2CBFC1164A964B5F6CFDB0AB7348AA9
	E2B744257402D7A1BB54A63F8FD648F62C6934369790F89FDC2ADD85FBBDE45774445A3878274FA6DEE9AE36904BD9E29D92D14B33A4EFD432ABC3817C2E9C75F2F9C7488DFA9B932EA5C55C9A7F8B34964E54A23C8EAF1BB45623DB9714042CE9AB172B7F0F33DC2F6D585A731F27F70A00DF311C2F35A7BF7D3F9956F03BD3F0B89441F13551E54775FCFACC9175FCBBFE9E79FB79983513754DBD0FFA8F9BE87E9FD0CB87885369C909F192GGECB5GGD0CB818294G94G88G88GAFF954AC5369C909F1
	92GGECB5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2B92GGGG
**end of data**/
}
}
