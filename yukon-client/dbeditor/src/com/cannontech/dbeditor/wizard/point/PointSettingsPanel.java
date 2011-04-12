package com.cannontech.dbeditor.wizard.point;

import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */

public class PointSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JComboBox ivjDeviceComboBox = null;
	private javax.swing.JLabel ivjDeviceLabel = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
public PointSettingsPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getNameTextField()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC2:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointSettingsPanel.nameTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.nameTextField_CaretUpdate(arg1);
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
private javax.swing.JComboBox getDeviceComboBox() {
	if (ivjDeviceComboBox == null) {
		try {
			ivjDeviceComboBox = new javax.swing.JComboBox();
			ivjDeviceComboBox.setName("DeviceComboBox");
			ivjDeviceComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceComboBox;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDeviceLabel() {
	if (ivjDeviceLabel == null) {
		try {
			ivjDeviceLabel = new javax.swing.JLabel();
			ivjDeviceLabel.setName("DeviceLabel");
			ivjDeviceLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDeviceLabel.setText("Device:");
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
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setText("Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setColumns(12);
			// user code begin {1}
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_POINT_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public Integer getPointDeviceID() {

	try
	{
		return new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getDeviceComboBox().getSelectedItem()).getYukonID());
	}
	catch(NullPointerException npe)
	{
		return new Integer(1);
	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	//Assuming only a PointBase
	com.cannontech.database.data.point.PointBase point = (com.cannontech.database.data.point.PointBase) val;

	String name = getNameTextField().getText();
	com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject) getDeviceComboBox().getSelectedItem();

    int nextId = DaoFactory.getPointDao().getNextPointId();
	point.setPointID(nextId);
	point.getPoint().setPointName(name);
	point.getPoint().setPaoID( new Integer(liteDevice.getYukonID()) );

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getNameTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointSettingsPanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
		constraintsNameLabel.gridx = 0; constraintsNameLabel.gridy = 0;
		constraintsNameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(getNameLabel(), constraintsNameLabel);

		java.awt.GridBagConstraints constraintsDeviceLabel = new java.awt.GridBagConstraints();
		constraintsDeviceLabel.gridx = 0; constraintsDeviceLabel.gridy = 1;
		constraintsDeviceLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDeviceLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getDeviceLabel(), constraintsDeviceLabel);

		java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
		constraintsNameTextField.gridx = 1; constraintsNameTextField.gridy = 0;
		constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsNameTextField.insets = new java.awt.Insets(5, 10, 5, 25);
		add(getNameTextField(), constraintsNameTextField);

		java.awt.GridBagConstraints constraintsDeviceComboBox = new java.awt.GridBagConstraints();
		constraintsDeviceComboBox.gridx = 1; constraintsDeviceComboBox.gridy = 1;
		constraintsDeviceComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDeviceComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDeviceComboBox.insets = new java.awt.Insets(5, 10, 5, 25);
		add(getDeviceComboBox(), constraintsDeviceComboBox);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{	
	if( getNameTextField().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		PointSettingsPanel aPointSettingsPanel;
		aPointSettingsPanel = new PointSettingsPanel();
		frame.add("Center", aPointSettingsPanel);
		frame.setSize(aPointSettingsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void nameTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
}
/**
 * Insert the method's description here.
 * Creation date: (6/4/2001 8:50:39 AM)
 * @param index int
 */
public void setSelectedDeviceIndex(int deviceID)
{
	//sets selected index of DeviceComboBox to selected device
	for (int i = 0; i < getDeviceComboBox().getItemCount(); i++)
	{
		if( ((com.cannontech.database.data.lite.LiteYukonPAObject)getDeviceComboBox().getItemAt(i)).getYukonID()
			== deviceID )
		{
			getDeviceComboBox().setSelectedIndex(i);
			break;
		}

	}

}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}
/**
 * Insert the method's description here.
 * @param val java.lang.Object
 */
public void setValueCapControl(Object val, Integer initialPAOId ) 
{
	if( getDeviceComboBox().getModel().getSize() > 0 )
		getDeviceComboBox().removeAllItems();
		
	//Load the device list
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List capObjects = cache.getAllYukonPAObjects();
		for(int i=0;i<capObjects.size();i++)
			if( com.cannontech.database.data.pao.PAOGroups.isCapControl( (com.cannontech.database.data.lite.LiteYukonPAObject)capObjects.get(i) ) )
			{
				getDeviceComboBox().addItem( ((com.cannontech.database.data.lite.LiteYukonPAObject)capObjects.get(i)) );

				if( initialPAOId != null && initialPAOId.intValue()
					 == ((com.cannontech.database.data.lite.LiteYukonPAObject)capObjects.get(i)).getYukonID() )
				{
					getDeviceComboBox().setSelectedIndex( getDeviceComboBox().getItemCount() - 1 );
				}

			}
	}


}
/**
 * Insert the method's description here.
 * Creation date: (5/1/00 4:17:27 PM)
 * @param val java.lang.Object
 */
public void setValueCore(Object val, Integer initialPAOId)
{
	//PointBase base = (PointBase)val;
    
    //Load the device list
	if( getDeviceComboBox().getModel().getSize() > 0 )
		getDeviceComboBox().removeAllItems();
    if(initialPAOId == null || initialPAOId.intValue() !=  0)
    {
    	IDatabaseCache cache = DefaultDatabaseCache.getInstance();
    	synchronized(cache)
    	{
    		List<LiteYukonPAObject> devices = cache.getAllDevices();
    		for (LiteYukonPAObject liteYukonPAObject : devices) {
				if( DeviceClasses.isCoreDeviceClass(liteYukonPAObject.getPaoType().getPaoClass().getPaoClassId() ) )
    			{
    				getDeviceComboBox().addItem(liteYukonPAObject);
    
    				if( initialPAOId != null && initialPAOId.intValue() == liteYukonPAObject.getYukonID() )
    				{
    					getDeviceComboBox().setSelectedIndex( getDeviceComboBox().getItemCount() - 1 );
    				}
    			}			
    	    }
        }
    }else
    {
        getDeviceComboBox().addItem(DaoFactory.getPaoDao().getLiteYukonPAO(0));
        getDeviceComboBox().setEnabled(false);
    }
    
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/00 4:17:27 PM)
 * @param val java.lang.Object
 */
public void setValueLM( Object val, Integer initialPAOId ) 
{
	//Load the device list
	if( getDeviceComboBox().getModel().getSize() > 0 )
		getDeviceComboBox().removeAllItems();

	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List devices = cache.getAllLoadManagement();
		java.util.Collections.sort( devices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		for(int i=0;i<devices.size();i++)
		{
			if( com.cannontech.database.data.pao.PAOGroups.isLoadManagement( (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i) ) )
			{
				getDeviceComboBox().addItem( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)) );

				if( initialPAOId != null && initialPAOId.intValue()
					 == ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getYukonID() )
				{
					getDeviceComboBox().setSelectedIndex( getDeviceComboBox().getItemCount() - 1 );
				}
				
			}
		}
	}
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getNameTextField().requestFocus(); 
        } 
    });    
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD0F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BA8BF0D457F542E0FE4699EC4B9D7CCBB14689EEB4B5F6C8CB93B235B53875C779F402470E43B4F243B4CC2BF898D753121899DF6D2EBE00849212319CC310117825E2919F8FF8ADA7C8DA848245DE109042D201A76DDB6D135E6EDB5EFB5ADD8981BD67FE5EBE2DF6C5F06219D6B3E75F5EF36F795CF34EBD673C3BA2D5BB669796B82A88A9A8A47C6FE881A10D7B89A9BD607E97014B10330B086D6F
	83008A3269308B1EF3E1EE55BD59C565649F6ACAE03C90463DF3320B5E056F1564253A2C5D70A50D1CE9B0775CD71FFAE8F2B97BFDC2CEB19D5FA8A80567A600C50043E73E0C727F22201A63F3B8DEC00103109581DC73D441B60EBB86E39F40DC00058136FE99BC57D349F7FA0E71F53D774CA779AF6EF114A19F9BCF815B86986DF17E2CA1CF962C9EC0D92FD1BD11A68E6345G38F9A7B9A59F01E723EFE860F77DDA485DAF0543DA58147B036E18B2AA69FEF7C4D34226476343C707EC1ACAF84070C9E1D975
	87116FCEC78965C3C8BD0C57D05C6AE61435883EBF83702A05FF328571C7146C22F6G3FA27677491EC62EFF7735A74912D7424E4DE976B72630FDADD4927B4B9C7ED571FB54972133E873DC18F38DE697D583B48274820C813CCFE340747D961ECDFDE60B968AE96126C824D9756BFF507C328A5FBD9E186138F3F23F267B89E1FE4EAD28CF214FE440685E38EDBAE613C507054F334EFD0D2C7C6699C7F18A1B2CDC5460489CE436489ACC5804495E63C8A7BBE7F0FCCC0E17FD0158E5AF1C2EB98BD348DE7272
	BEC7AE6713EF13DDB7043E6E253E2E026F85BCBE98BEC071C19B1E45679BB4E6F09C073915C3A2B63AB789DD5ADD8F1272036DA96DB0AF3B49F1DCE5BAF42B493A54FA1177B4B5D9177D941F29A66B722D96214BC218AB89E59731780AF660F9D5E1BC93E0BE40D200D7817281CA42A2463E6BBB70C74618CF524D66E1C5754BBAE1345133DB61A947CDDDEA8B9B26946E173BA4DD117AD459A0FC4D4D33E8033EF1F453E86C77030C13F2D856A5D3514240DD8A49262C9BEC4D5DE7ABF00DAE5359CEA5A40300D0
	04883E771F2D447DC906F9BE6217CCB931024DBFF296FD92153A00A3B0006F4CAE0FF5223D76427E0F8378ACBBD41FC3FEE7E49D320547532EE9C34311CE5C89199FC6BB7F0E668EBFFC9F8B8B9B7F65F9442D0271BA0B4FE767365878F815FED4DF52C744996BEB411CDC2E0918F923DB440C376CF1D27F726914B103502031D8B925A51F9D1572C43D58B83F391B5314EB294F4E1F82CCC6D90B10617CB3735FE4792A6C827AAA82FB18831025899B4FBED743E236D9B2E491251F6990E032BFE1F12667BE2F0B
	6B48460769F839A6EC751DAFF59FCC460AAFD58633C9C68756A1B9AA07CD2F9C1143FE78E27039999D98EB7C1C86B40B66EE0A0F4039EE0103E4D8787BBA706CC92629ABFD4326ACAA29B57F9015E58C77C592A9C148DA500179D70B41393656FF3D834FC43F968E4B54B996891FFF021EC9D915C320F40BB69CB6997E49CE745510BC42F342B7BB314909CA6AB066975693AC16B6629EF4A584AE4794C5FC91BC2F679D55B69B65013F3137290339E391F176E235626C550DBECA1E393C4B112AF64CFB5C6B1897
	61F5AB12B8FBCC270567D36B7ED7B4B7F03C41F4FD7BB25AE1E2F7A2623D359BF965000CDC003591916F3B3BF0DE64949E68AD1A2AB2F3DAF62E3CD00EFE112F8E4B100AB1F91D31EC776B8BDB12663A389D597C4E8BE8C7551677F22E229D7DDE51771543F8AF40292B4C16532EB2DB303D95DF467A1D2AB79B98D65C43260259D0B2A1GA343262DA77BFB1AD7711CD46B22775ABE02F67041B88810218BFF7DD319683DB20766135C47FE1D36371C27733ED4CF782BE62A683D66DEC4196BE02E5490B6FE6402
	BD7FB79906562F5052436D535EED77A7431D6D5EG4F00229BE69B9C3838306505EE5497EB93C773DAFDB72D61E82F9A105B8D10E3A07EE8EB15CD7739862BB1CB6169247C723DD8237BA5DDB65D7413153C3D03D2D40A3B0D9884121B1EFCF78B4E1E402F7B38DF0E3C6B01E798EAEBA7A400D6856CFE39454623FDB2A69F6F6B92FD56024A63DC3F1C3FFE10B45E2CF4246AFBE7EE5E6E28B2194D9B4D046D471EAEF266DDFA2B18654365D62FB7132FF958CC54294566441A45FA201323E83F65A66B0156DB78
	CE0A4F3561193DEBE99C61F89D4CDD9996F5FBC78F660665B04E85D88BD09E4DAEEAG70C5C5BC7C347BB039C5DF5486712D07A535E3DCA2947D49C5A74BDDFC7C4045C2113B08288B77F913FBDEB5BAFE6FB313460C6E5189F43FC99AE7A55131D85835F71BB823B62FFBBC272D50E0E7B25CAA4E647228B8FB57DB6DE7EF5966C30E0DE94E5EBAAEFBDBAC617FFA6B3D6705DE5421976662GF38132GFE881087D09E97762F7D7C13DB591F06E827568238D31037DA126B8B50335E6DA7B447B4G6FF6G06AB
	0BA26E8A0CCD8B77EFB1444D03716238688F7EB00AB9F5850C73GDCA359C5B5A3E26EF3BA5788635E91169B472C39FDA35DC9F6C15A496AB84A300F73EC631A11D4F150BCC1C671A47CD3759F05366F6B1366189F7676246FAB8E74246EABBE6CC95DD7FC5CD3417040437EB6A166DBFAA66FBB4E74246ABB3653FD3D30F5B365ED9832C183830B8D1F7B39798EC951FFFC8AC9115B24123C68DD4F571CB03516C3160DFFFC52FE569ECE7243622471729111E3FC17B0273F8A63FC0052D168BDG3C23A23EDF6F
	39E5FEB9911FA43F7C28B73DDFDE6BCD6D175CDE2C1706B62C774B49367FF9EF35B517B8C9ED269CC2B331B5EBA83DA9698372845F7D9725E7F36968497F2473AF73411B341EC7F4B9A06B5061F4A823681F07C771ECEEF45277AF789E9995F531AE0A7D20AA0DE843A63CF3DF43F58FC4C55F178B6B568254G7E48B5D6D3D99EFCAA0639ABGAF1CA03EA3A277133DE3C8DB9097341930FE914073G2B81CA46044C6395E8DB5CD33B54072D9B57BD6FBC72744AD1251F4FB0FC576F4B78FAAB49DAB4551FEE32
	E8A08C7A34E68D1A855693FCE5545E93C446187E19E322E7F8EDA8557CA2EB5E63314FAF63732B46C42EDBF3926D399646A557852E52A5725FB68B77530F902742B8633A085D6ECB69E36F72255431A7DFCA1D9306AECD1E930CCB69E2A673BA5EF97AB1C438BD9D519AF80665D88D16B73794B35DDE857AB53CG58FB7843067A1AC38D8D7BD8BD1B9F335FD39E5BF0B4E56F0F706AF5F6467378F3590CDA5EDFBC9B957714F5B720865C9071F5704A38BA0B0AF8BCA7F5455FAC8D3453E8E5F5F6BD2DB7C81FF9
	C3743EC7E3749DG462BG565D9075B73EDB545F2C2FBDC85AB34E246CFDF3BB8FB92AEEB2BD0F5DCC545FC5D0FF194CBDF1A15304792C1B74DDA9EE0F953465BD30400D32687C0CF173680FFD44BE7FF76366D15F6FBA6C737FBEEE9EEDD44B69595EDE5AA576366E25C5C47D56C7E97B7AFC3E276A8C7674F555123A59EF95B33B7FC7201A246CEF6801E47BFFEED474B6BE6041F0952A404D4C90BE9CD05FCF4D4BBA280C762A27046F69198C11A30ACF61FBEBG5EFFD86E5AFE0A1EB518F7CD49AED227A06E
	37AEAF5F5331A9937B82A75F3F13C3BB2CC12865322ECCE13D2ED8D3C97E73A3543FDFD307C3E103674CB22A9B5623A5D3C49C7D20479E77658FFE6090FC12F5D84E757A6114C49C315A669EC03E2EBBA0375EA1F2429372FF4333CB9A64156C04DFB1B51D16B2C07AF4ADA26B66884352B7CFDEDF7FB17A456E2ADF7E02F41BE237CB475EB13EC14F6571BB92F6E778A5945FEE43335C70C9CC6406EE18DBF607688BAE87B1C6EB26E697B582F48228GF3G96CD9536FCD3EEA037688DBA35C8BBA4E05516B259
	7E6A63DF6C8E7F59E2F18617FCFB81D9BA7D1785697A7BE5D3D9BC2C644F551D35944F767726027BAB1D867DB0C0BB009FA08EB0F71A581FA7D04F76076150E41AD2FF103ED50B4DF5A9F2AC0146824D76766228FDEF8B27B179CB79B3E75A44F34468362AF63ADC3EAE1FBFCB6ECCC79775586936A52DEBC8C147EA12874B9EBF17C52EDDA429A635C2E2583B531042502B6ACAE0E7FD8DD42603C7B027321C526392B9458BB2F6D0BE20A7C97CB1B93370BA1C7B6F75D2613FED5FF808F8DF79E5C1ED9A7F0DF1
	3DF36E347D6EB6382E1877D49EC85D6460A1614F6BD711BB3FE38C60DED21EF1705AE9585FE7FD035078F5CA83964D618114E155EFF674791CE7G6B0292D6CCC5D215D1FA5D52818B43834C7ECF4508650BEF5B3E3744125FFB7E1C40E474FE29B2587E034174FD1496CC5DC78D87D377D15702133FBBCD8D263F331D9D345F59B23F4F5651B72E691063813CG5DG6AF46B7792E5A721E7B84579BD619F40F7887CC54894725FD2526F55B1F86B5F11B68E62DDDB1FE61ADA88798BFC45A07A4B54A29449F0DB
	8751C63AB290B459D20E5FB6C8FBD9B9C0512C3FFD1D7683B3E1DF73260B1E77150D084B0271B28B57D8AAFA6367AD5CFED550D64F90B8CFD45056DB380722027618053B5BA37A6F1996EE30C22CFBF80638C73862326720BC401319135FA3B07F2DB6303F2802358DG1DGAAC08640BC00AC00E7GD6G648138E6419EG6A810E8378G7CG71D9425F5F0F6CCBF77681491BDB6AF4DDD2506D5C66776A697D7E00FEEB3FAF50693DAB782BF3A4425FA33F2E63FB1F9688983295DFD9FA3905EBB3FC1A21E0B6
	E1B9ED6D6878FEEC53B564B98776B6EF1668595E5808EBCCD0066556654318EFD82F56D1AAFA3567E7CD343D3D8F3377EAF8670067EA652CC4EF4064F75138D98D78FCGA6EFC9BCA16F692810D7B7BB35BCE78AF922B7BCB6BBD95E5F50D873825EB7DB741FD3AA040C0C94B21C371011E91341E2B7C6EB567C592C4FE438B10ADB8A38BDD6BC2CB3524703533CF5BC141A29734096F3E29EF857CC1D87361B76BC40FF7FB0F1DF8390475D563ABD966E22052B27BC91F7C1711BC18BFF086A0578D3B28A9478E3
	D42F9845F5F35CA7B65CC50EEBB6B126E14BC1CD9734A7CD9A370A2A727BAF3AC689C308DB7670D19D1508E4537B3C05C33DD9FCDD39C60868C9DE5A0839BBC5CF6207BA2C8D589A92560FCCAB957D480A59A2DE270E910B770E1B895E6E686D717ED9D470AE3DCB70EEA6895EDFF5A4F8DF2A38BD5ECFD7D8FD14457BD9E4456F43255D25446ABF28BB99FEDB3CAC01976E64FD412CD2FBBE9E833EF36F1ABC9FB33A55635ED796824D52BF0A6EEFAB6CF4B9C013FB8BBAF67666170A73F85642BDDEAA4E638796
	6E6D28D8F7594239E29357FD3FC22CABAC90389FD508F5C761497EEFAD5CF08D0C18758BD2E868E44B44FB18262F7C78152C4B5EAA87F1341122A0B5EE9BA90C217563584FBE8FD9659250160D7371F30FFE782FB97F5CE5B8C9D19BF98FD3F987744E58B54AFE3C69657F6651CA56537F81432B43B662B2682350B0C7D4391DD4854E614F1F06A97B1BC41FE418649D7A9358817C097240A949881A18EFBEEEA7AE7C514CA4DB5834AA05878E306674E32AE7C153959A26E8F2827AD6D2FD110B5B62D2791D4D3B
	BEB83DEA2913B8DA4986D5137C2DD2BF144DB6325E8C6A329154D4FFBC727B6314D18935BA3EF5B9015D7B5676F133EE3CBC34149E0ABFB227ECF5D9AB9C0D38CE8E5015D8395C568AFFCBD052C1142C3719788FB61ADEE74DCD790D397BFBA75F6DF510823E492B6486F145A3A6A98AC8A1C59DB149FA6B6D4440499240C0D28E9B81CD8F01F2B5C94A09A9CB067B3E979A6EFF62D71FD39989D2136CE056845283833296BA509488A8E159E2G2685765BAD7638461D34E6CB7C1EF7CE3F7CF69F040217B8C38D
	8D7473A07DBC041F87A966A0459CBC8476DDC3A67DCB1E0F0BBD537881FDB6D3FDB0825C2D60C47335DF3C3060A70DEB72E0F72D24107E2687FEA8CF2C7AF4B854A76B53336E5F3EB1B377AEF4D38423A50EFCCB6396C75CE19B4D663951439E674F9EF3BA088BCE852BEE903FD822A0FEF5F6C81CD05DE868E11642EA031DDE21C98A71F659E312E2F6136C216ABBA604A8E20BA6469A22B7006BD98E45D199DD3A19FEEE39ED63A1D595255DEA6DF987B369FB49447F77B45D7D7FC6537D89CD4745833F8F58F7
	53FE4F37764E6A34775BFB6F653F575F1BFC4FD7F884F79487FC663D62BD607C617DA47D9DC0C7C8D2D5D3D2071A42FE3C8FE89EB16544BB6806A3485F90EB78A4FF0FBB02FA8F5AE87E97D0CB87882BF668EFD191GGE4ADGGD0CB818294G94G88G88GD0F954AC2BF668EFD191GGE4ADGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0B91GGGG
**end of data**/
}
}
