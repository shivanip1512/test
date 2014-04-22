package com.cannontech.dbeditor.wizard.device;

/**
 * Insert the type's description here.
 * Creation date: (10/4/00 4:18:25 PM)
 * @author: 
 */
import java.awt.Dimension;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.GridAdvisorBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;



// This is here in the event we add a port
public class DeviceGridPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JComboBox PortComboBox = null;
	private javax.swing.JLabel PortLabel = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	
	private DeviceBase deviceBase = null;
	private javax.swing.JLabel ivjJLabelErrorMessage = null;
	  
/**
 * DeviceBaseNamePanel constructor comment.
 */
public DeviceGridPanel() {
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
	if (e.getSource() == getJTextFieldName()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}

/**
 * connEtoC1:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceBaseNamePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextFieldName_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * 
 * Return the JLabelName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Device Name:");
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
private javax.swing.JComboBox getPortComboBox() {
	if (PortComboBox == null) {
		try {
			PortComboBox = new javax.swing.JComboBox();
			PortComboBox.setName("PortComboBox");
			PortComboBox.setMaximumSize(new java.awt.Dimension(162,20));
			PortComboBox.setPreferredSize(new java.awt.Dimension(162,20));
			PortComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			PortComboBox.setMinimumSize(new java.awt.Dimension(162,20));
			
		    IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		    synchronized(cache)
		    {
		        java.util.List ports = cache.getAllPorts();
		        if( getPortComboBox().getModel().getSize() > 0 )
		            getPortComboBox().removeAllItems();
		            
		        LiteYukonPAObject litePort = null;
		        for( int i = 0; i < ports.size(); i++ )
		        {
		            litePort = (LiteYukonPAObject)ports.get(i);
		            getPortComboBox().addItem(litePort);
		        }
		    }			

			// user code begin {1}
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return PortComboBox;
}

private javax.swing.JLabel getPortLabel() {
	if (PortLabel == null) {
		try {
			PortLabel = new javax.swing.JLabel();
			PortLabel.setName("PortLabel");
			PortLabel.setText("Communication Channel:");
			PortLabel.setMaximumSize(new java.awt.Dimension(172,19));
			PortLabel.setPreferredSize(new java.awt.Dimension(172,19));
			PortLabel.setFont(new java.awt.Font("dialog", 0, 14));
			PortLabel.setMinimumSize(new java.awt.Dimension(172,19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return PortLabel;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			// user code begin {1}

			ivjJTextFieldName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
			ivjJTextFieldName.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH, PaoUtils.ILLEGAL_NAME_CHARS));		          
			
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
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.device.DeviceBase device = (com.cannontech.database.data.device.DeviceBase)val;
	int groupId = YukonSpringHook.getBean(PaoDao.class).getNextPaoId();
	
	String nameString = getJTextFieldName().getText();
	device.setPAOName( nameString );
	device.setDeviceID(groupId);
	
	//SmartMultiDBPersistent smartDB = new SmartMultiDBPersistent();
	//Integer pointID = DaoFactory.getPointDao().getNextPointId();
	LiteYukonPAObject port = null;
	port = (LiteYukonPAObject)getPortComboBox().getSelectedItem();
	((GridAdvisorBase)val).getDeviceDirectCommSettings().setPortID( port.getLiteID() );//
	
    if (true) {

        PaoDefinitionService paoDefinitionService = (PaoDefinitionService) YukonSpringHook.getBean("paoDefinitionService");
        DeviceDao deviceDao = (DeviceDao) YukonSpringHook.getBean("deviceDao");
        SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(device);
        List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(yukonDevice);

        SmartMultiDBPersistent persistant = new SmartMultiDBPersistent();
        persistant.addOwnerDBPersistent(device);
        for (PointBase point : defaultPoints) {
            persistant.addDBPersistent(point);
        }
        return persistant;
    }
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldName().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceGridAdvisorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(351, 264);
		
		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.insets = new java.awt.Insets(3,3,3,17);
		constraintsJTextFieldName.ipadx = -5;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.gridwidth = 2;
		constraintsJTextFieldName.gridy = 0;
		constraintsJTextFieldName.gridx = 1;
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.insets = new java.awt.Insets(3,33,7,2);
		constraintsJLabelName.ipady = -3;
		constraintsJLabelName.gridy = 0;
		constraintsJLabelName.gridx = 0;
		add(getJLabelName(), constraintsJLabelName);
        
		java.awt.GridBagConstraints constraintsJComboBox = new java.awt.GridBagConstraints();
		constraintsJComboBox.insets = new java.awt.Insets(4,3,5,17);
		constraintsJComboBox.ipadx = -5;
		constraintsJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBox.weightx = 1.0;
		constraintsJComboBox.gridwidth = 2;
		constraintsJComboBox.gridy = 1;
		constraintsJComboBox.gridx = 1;
		add(getPortComboBox(), constraintsJComboBox);

		java.awt.GridBagConstraints constraintsPortLabelName = new java.awt.GridBagConstraints();
		constraintsPortLabelName.gridx = 0; constraintsJLabelName.gridy = 1;
		constraintsPortLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPortLabelName.ipady = -5;
		constraintsPortLabelName.insets = new java.awt.Insets(6,33,4,2);
		add(getPortLabel(), constraintsJLabelName);       
		
		java.awt.GridBagConstraints constraintsJLabelErrorMsg = new java.awt.GridBagConstraints();
		constraintsJLabelErrorMsg.gridx = 0; constraintsJLabelErrorMsg.gridy = 2;
		constraintsJLabelErrorMsg.gridwidth = 2;
		add(getJLabelErrorMessage(), constraintsJLabelErrorMsg);

        
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
private javax.swing.JLabel getJLabelErrorMessage() {
	if (ivjJLabelErrorMessage == null) {
		try {
			ivjJLabelErrorMessage = new javax.swing.JLabel();
			ivjJLabelErrorMessage.setName("JLabelErrorMsg");
			ivjJLabelErrorMessage.setOpaque(false);
			ivjJLabelErrorMessage.setVisible(true);
			ivjJLabelErrorMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelErrorMessage.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelErrorMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}

			ivjJLabelErrorMessage.setVisible( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelErrorMessage;
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	String deviceName = getJTextFieldName().getText();
	if( StringUtils.isBlank(deviceName)) {
		setErrorString("The Name text field must be filled in");
		return false;
	}
	
	if( !isUniquePao(deviceName, deviceBase.getPAOCategory(), deviceBase.getPAOClass())) {
		setErrorString("Name '" + deviceName + "' is already in use.");
     	getJLabelErrorMessage().setText( "(" + getErrorString() + ")" );
     	getJLabelErrorMessage().setToolTipText( "(" + getErrorString() + ")" );
     	getJLabelErrorMessage().setVisible( true );
		return false;
	}

	getJLabelErrorMessage().setText( "" );
   	getJLabelErrorMessage().setToolTipText( "" );
    getJLabelErrorMessage().setVisible( false );
	return true;
}

public void setDeviceType(int deviceType) 
{
	deviceBase = DeviceFactory.createDevice(deviceType);
}

/**
 * Comment
 */
public void jTextFieldName_CaretUpdate(javax.swing.event.CaretEvent caretEvent) 
{
	fireInputUpdate();
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceGridPanel aDeviceVirtualNamePanel;
		aDeviceVirtualNamePanel = new DeviceGridPanel();
		frame.setContentPane(aDeviceVirtualNamePanel);
		frame.setSize(aDeviceVirtualNamePanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getJTextFieldName().requestFocus(); 
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
	D0CB838494G88G88GDCF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359A8BF0D45595B09134CC87ABF6D0698CAA5AB8E67C628C3534E206CE71D325D32956B2B656D429031536B8A2B2656EEEF693C2FE7CCCD2910284850DCBC09CE340CF486633C954A8E90C15AAAF59371B173C6CDB5EFB1BEC7825675C4F5B17E5B7711B71705EBD675EFB4EBD7F7BD632E5479C3743390590071B703F379C0464D491F273060F66885CA4B94BC3ECFFBB81B612B6DFAEBCE7GED7A85D9
	1ED2F2D5EDBE0C6742387A7CACCF993CEFA6993527AB61A5851FCE20798317D60F4F6711B302CF9E9DEFEDAA04E7B1C086607039CAC67E2FB715F37CBD9CAF4061A4E4D9906734B4ED6338DA98F7G4C82488C32790B60391EF2CE2F2B65730EDDB4072C3A6B95E7916EE35BD3C0D5082DED614FCAF203E3E588F91D20F2621AC9B01E8F0007F711DB075F0667610E01503B3D5A2037D78A073530A97776F9071513129E708664A125D72E2B7351E717221BD1C97DD09A14BB24302C42DA358F777FD9D3BE5D0F10
	03B0DECDF1D74B4873C6F8DF8C10ED6155A0628FABD91ECEGD59167FC6715437C9C5D27EEA0E98F04DDA5A94E19263073E5AA71F34E0C2E492B20B659B90A3ADF8E34427EACCFA5008FA0G108670827505BDE7DF05E7FD0F59208D8EEA617AC864089A50BF5082328A6FF5F5C06138CF64DECD8F90426CBD5CD41DC41E71002DCB7F5A6B18CEB25E90364F7C649A326C16B6E7DE921DAC4B28F14E6EE73A18579F5789637D4B143C6F6F9F6B1BE3F9FFCA6C3C4D73B517BB896FF9CB77BA1773FD565AF8D78F20
	2DAB282D37413B037B87436F2278088D4F7C74BA6AB3B81E84341C81619B5D45C216765C4BC8697E7624FA1815D56FECD1198C4755C4D932A94F69EA22ACCFD07CECB5D196BDA8E44984DA69E0160779572B8E0CDB9346B3GB2G9682E4832C85A88D8B9FBBF1F65FD774318EC9B70FC495B5A06B042DFDF574F9F84AB1D3177CE143144290FB122EC8BD2AEC90BE670DD154C14F18F5355477FB01C7339C16F549D434B06C8E71EA4A3A4166540FEE44B93ACC291D4A208C8C86A3C46C6B9F5D0CF1AD9966FFA2
	81491463B3987D63D13449105485BB4286704E7472299375D58367EF81683674F08F5D2FCD56A1E1545535EB5AC0B45209A7A199E15473D21ABBD4F8CF8B8B9D9FF5A1EE850CF3B5310F0BEC326D63D3FAD1FCC99F91B136A3083939DC93BE7337EE61B33E222BC975525624BE03F0D8E33E5229A5464EDE3AA74A4546B7495DFCCD3916BCF63E8DB09EF7D9BCDC5F717E45162D6E48C5DB1D06B34C8218EF6978DDB8BB7559A312A18BAF7DC887871383F10DB3B99F233AF1D8F22F2463A51A50D5AC581DC25F5F
	1D2EAE22BC3E8FBDA1B01D9CF4E09D120764306913A3F2B8GAF86279DF2202F71B88DEA561AFA0A0FC0DCB7C0A099963E590131A719262E74C4CD7928EC1ACAB89427FFC4F999511EC8BCA588DEDD34E15106A12E2D791FBBB0A6FA35F0D826463116F07A7FE8CC4A2ABC88C2B7E8513049703DCE345500BC42F3C248094D4E1024C6B13F305E60C6E9831EC1D78641641822483188466B8A2B37C1D88D76469E2786E83E08083D61DDA2762ACEDEC196FE395B192CF64C3A5A671C9BE1F1B4BF920FBDA6D337AB
	396CDF505C407186137506814C17291A946673EB5C385F0340E785C06E8961738F3910AE728AF77686CDD519CAADDD7FBA3794EDA31F084A100EB10135D97A3BB7F7D3822D0B6B1251FF1F0B3AD4ED396F4193284B1DA15183EE0371C1004E93CC1FB3CEB0FD307365FD09B5BCD91F960AAA5E2829E0C614CCF042C8542427E6FDD9D7F3BE5F23CA977D5729D65443B1981BG53F5E1335F96097EEB66409C12FD553FD37619F3F5A65BE2BDEE334A73C47F654BC31E79C616275C90BA7E58ED2F8175062175AA34
	7CF07D900F5D268C67769440B32868066907200B89DDEE2072E2FD62E8DE2FDBC3057C2C5540B7G7020017883EDDBEC324FB6D81DD984CF975D6F26905669DEC917CDAF7D17153DBD7D521094739A439020DE9A7D5E8624B6626BDEEE17DFB49E02E7FFA7E400A6C5D68368F4DFB658B6E99FEF17C3FB3BF82FF565667739FC77DCFF99B9BC3A59192C771DD67222335264BD3E99577D691BBD2E95DFBC1747F262674D097D5ECCB3DE2BB24CF36B966B031E22FE3140E4FD105B422F2178A5B6BC537764D6216F
	9C2035C4C56D7EDDBE6607CC98AF864886D88DD0BE1465299D927EF031678DB2C1EF64877F564312FAF4CCB294BDCA1E0B65AFBE7EA04FAD7297913521A31428074E2131E70FA40C593AAF4ED937BDD11F896B18AF6C5E33CD44284D68F5F52D16F73018D45A44DDA9F3C8445E19A6FB6CADA8B9605C10A276F2B86F5CE1DBFFEB5DFD4E94208CBE209D8708814C84188FF0BF406AE1217F87725E19C87F54C3BB35864035C05EEAC82CB168CB34FF53E90EA90F41DD9B00615E71A32E8346FDB1518FBC5D0A3869
	B84742912A8B4CFB8BAD5C54566EA46736832ECF061F1E82FF2E0D67CC48431D827FD5FA8D2663E579297B06C77313778DFF4DCF5EB7BC153F1161E18F7BEDC1501F499F3F2FD8171F2C2FF8111E674E67CB685E06A19B5468D8C8B86DF1F3ABC952DFE01A633A58CC26065673B90D2656305F52F138ED3CBBEFF692DBB19FAEA944FC3D8A6825A3D91EBDG6F839C9F913EFB497A89F3C7E3EC1C5CB135A035DDFED8105CAEB38B30969ADAD46F1593F5FFF9C139C50BC7095F1487D1CDEC4EDC3A5E147410FC0E
	6D2E236B99AD557AF985296C4BECF0C1B376EF2AB422C5CD38B70DE08C4EE996FD5AA4984F82D888F07F083D2F38E99873CC90AE08305D51085CCB961F4435DF7A455A03B0EE816883188610F1D2544D20027456B135CF4873701BD83BDB5B259ED9E5F8D60B7F9035576252D32C969FBCA572C6FA1B489BAD27043D4B8AD35BEBFBE1F2FBD595A60F23D78B470F23830529744CEA5E62E6719DED9648376414780E367F78185C6F15064D3A3AE6DD899C11C26D54AAAC7717366473757927C5BF76EE8B6E79BE0C
	3B81A227C5CD083DACEAC266B5171176C9EDC97B316C4E834EE92779773053711A1081B50171CC778B1E4B01DEF986F137786DF6C01BEDF492624535147E17B1F45449DEE22717355A69A86F6DE3567B47505D36756CECF76F96E7DBFBF786D1EFFDBBE52F59F7061DE9BAFFF6F46DA2D597BE1747747E4FE0B9C9DAF32967F11D8F65731C34887628B90B3859ED3EF1F2131B6BC440DABC932C5B45E9D5707E1A43FE773D771C1E6B2B5521E55FF05D56162F3B0E75F225FE743558D9564BB9ACFC85452759704C
	5FFDAD425FE7GED55D95163BDDD04FA6F9E05BB8440F40039G0BG32C7C55CAEAE2CA1936469CEAD528E012D5AC2111D6FC27FB77BDEFAC71E702BF937DDC9661F7F2CA3D59F35EA14795316C96C393CF3172D86CDAB41737900F69CA08DE0B6400DG0BA709731596D53373B58694335EB4255EBEFAFB91076BD26461B89A13AB375D107DEC4BB87F157C19BB795C3A380372031CC1445F4A774E45C18FB699FF33D808D7E12E0FC78A05BE36DDF7B971BD702CE3D78AFD9C63FCB3A747F348727E357CCE713C
	1FD83E3743765E5AB2D19F75ED0071687DDEF9B0DB7E2BA8F50DF197A72FB19B0A1357180D456377E29545293F71D4965B3F71B0DF7CF39966529C346D14ACCF95C0ADC07B94610757973F447CB0098FB686C258136097E4D3703F28A475D9E715CC7C5D790A922C6DBD1AE9EA03383F407F34846DE5EA910AE4384C9254112E047ACCB61563EFAE215FFC65A0C5335ADFCD73F49F1CABB6C57483CB5C744EG6359966ECD0F6891B22608DAF95F67765F48CAAF5B6994F9AC515E2B2770B8BBAF6E67CC4F6995D8
	93EA815F8990831881B097E0814092009C0055G25E95017829C8668G88G18G53GE627893B384B3066F5C97DBC73E11A50F4EA99C07266009AC71794B48F574D93E5296D737732096D33260CFECB813DF60EC4F87F382E8C7BC3AD98B4E44B8FF24BCBAD1C5F68508C857BCE563FB58C6177412096B6D724214EBF233639915E9724097E6FF1B74EB1C19816F7BE25F9075557F64FF8755515GD8DBC5FDC51B552433BA744C3AF56B18EDF271A7DCAE770F4BF72752ABF8FCAB34819F7574C3935DA9052CB5
	17D62456691C0A09F5FAEDC5F21F3F2162DC1F1FD7115C67EF2B307BBC435DDE01719E82DF683666AD32F01FD938BB691E087BD48918FD967EBEAA9762DBE4E4A870CB29DC4394574DF18F59F01FF15C1FAA50AF6048FD1AAE5666D4D05BAB2A4A464B699CA58CEEE26963C9AA239211ECF27F4342215C4CA75231AD66F702C5EE4CD3E1F938D234485EA6F030BDAF8594C955C2AFD5D756D4D76EBF38D7542EB94376182EADF8AB69B7E40443693CB7644F85D3F7F17ECF61B5166751595BD17F53E04E5CF49123
	F7EDFBED1C986D5035082C1BA38CCB3F680A9C389D79D8B91E11B8ADE37B962AAF88A7C9D5CE524F1CC7E1E2B8447EBF04B70FFC8C8A49FC12388D1D14D83A70D385E78F3DD167FA74AA1713647A0907B91C1F64225778091BBA151F384061E278035D942E5CCEF4B81394328F6015F8632A034D0B46A87AD7D73C7958BD8FF799AE6271138A0C6D2332CEC51483F865673F67B51175918D7251D14083E703BE5C06B9224A6DE4CB70937C46ED18F220DE9C5EB049D67A19F39FFE075E57A299FDA6864F21F6129B
	46DF9E49A6C6D625F0E89F5344A1AA3E237EB835B46A12003C1B293C380B575AE5734FCBF66FEC4D196FA24EA6D220EAD220C96A85EB7849FA33CF170DBECD8D44A26F3DCFB74A9FC693E34B67026DDE300E0F077562C55592FAA0761369C54B0B1A6056925349BEBA93D319571A91E86813F4E0A56BF09557A5E0D9E551A6EFB7DF7ECDF359B1A7F170C31EA0E709BB96B109A788F7E9F584DEFC542D8D242CB761BFDD8A9BC1CD9F8449AA93A493A40B017762BBEBAE39F64DFFA90378D2136CE02A04257BC232
	B6382FBE98D442323581689536FF515A9E67F89366EC0ADD3435F5693A9E70839FF18D565450FF7753FF8F603F7BA9E6BF456CBFG4ADDC9467DCB244744192973D8CE0E6677B601854D071E3C734ABF9EDE3982CE57C45C742BAD9821B4BE6B4368E00F2C1F1FF9490B9BE6E67FGED94C1D709613EC5ECFB7C1B8A00395B096EC3C45E58BBEA4F9B2B534BD3660DA59730FC11F3C1E2AFF024919D2972C22871970ADC71A35F6B29F285541D2303122A1A12BAD09F8EE08DBAB2E24A365F7D9AF1FFC34C61C4C6
	7B3091656E372D79BFD0CB87888B454E5B378EGG8CA5GGD0CB818294G94G88G88GDCF954AC8B454E5B378EGG8CA5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG718EGGGG
**end of data**/
}
}
