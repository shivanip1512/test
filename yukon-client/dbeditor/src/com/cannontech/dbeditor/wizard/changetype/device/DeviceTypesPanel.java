package com.cannontech.dbeditor.wizard.changetype.device;

import com.cannontech.database.db.device.*;
import com.cannontech.database.data.*;
import com.cannontech.database.data.device.*;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.database.db.*;
import com.cannontech.database.data.device.*;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.DeviceClasses;

public class DeviceTypesPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.ListSelectionListener
{
	private javax.swing.JPanel ivjJPanel1 = null;

	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	int deviceCategory[] =
		{
			DeviceClasses.CARRIER,
			DeviceClasses.TRANSMITTER,
			DeviceClasses.METER,
			DeviceClasses.RTU,
			DeviceClasses.VIRTUAL,
			DeviceClasses.GROUP };
		
	String deviceType[][] = { { //MCTs
				PAOGroups.STRING_MCT_370[0],
				PAOGroups.STRING_MCT_360[0],
				PAOGroups.STRING_MCT_318L[0],
				PAOGroups.STRING_MCT_318[0],
				PAOGroups.STRING_MCT_310ID[0],
				PAOGroups.STRING_MCT_310IL[0],
				PAOGroups.STRING_MCT_310[0],
				PAOGroups.STRING_MCT_250[0],
				PAOGroups.STRING_MCT_248[0],
				PAOGroups.STRING_MCT_240[0],
				PAOGroups.STRING_MCT_210[0],
				PAOGroups.STRING_LMT_2[0],
				PAOGroups.STRING_DCT_501[0] },
				{ //Signal Transmitters									
			PAOGroups.STRING_CCU_710[0],
				PAOGroups.STRING_CCU_711[0],
				PAOGroups.STRING_LCU_415[0],
				PAOGroups.STRING_LCU_LG[0],
				PAOGroups.STRING_TCU_5000[0],
				PAOGroups.STRING_TCU_5500[0] },
				{ //Electronic Meters
			PAOGroups.STRING_ALPHA_POWERPLUS[0],
				PAOGroups.STRING_ALPHA_A1[0],
				PAOGroups.STRING_DR_87[0],
				PAOGroups.STRING_FULCRUM[0],
				PAOGroups.STRING_LANDISGYR_RS4[0],
				PAOGroups.STRING_QUANTUM[0], 
				PAOGroups.STRING_VECTRON[0]  }, { //RTUs
			
            //PAOGroups.STRING_RTU_DNP[0],  //dont allow this for now
				PAOGroups.STRING_RTU_ILEX[0],
				PAOGroups.STRING_RTU_WELCO[0] },
				{ // Virtual Devices
			PAOGroups.STRING_VIRTUAL_SYSTEM[0] }, {
			//LMGroups
			"LCR 2000", "LCR 3000", "LCR 3000 Emetcon Mode", "LCR 4000", "LCR 5000", "LMT 100 Series" }};

	private int devClass;
	private javax.swing.JList ivjJListDeviceTypes = null;
	private javax.swing.JLabel ivjSelectDeviceTypeLabel = null;
	private String[] repeaters = {PAOGroups.STRING_REPEATER[1], PAOGroups.STRING_REPEATER_800[0]};
	private String[] valueList =
	{
			PAOGroups.STRING_VERSACOM_GROUP[0],
			PAOGroups.STRING_VERSACOM_GROUP[0],
			PAOGroups.STRING_EMETCON_GROUP[0],
			PAOGroups.STRING_VERSACOM_GROUP[0],
			PAOGroups.STRING_VERSACOM_GROUP[0],
			PAOGroups.STRING_EMETCON_GROUP[0] 
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceTypesPanel() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/2001 3:52:28 PM)
 * @return int
 */
public int getDevClass()
{
	return devClass;
}
/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListDeviceTypes()
{
	if (ivjJListDeviceTypes == null)
	{
		try
		{
			ivjJListDeviceTypes = new javax.swing.JList();
			ivjJListDeviceTypes.setName("JListDeviceTypes");
			ivjJListDeviceTypes.setBounds(0, 0, 342, 50);
			ivjJListDeviceTypes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			// user code begin {1}
			// user code end
		}
		catch (java.lang.Throwable ivjExc)
		{
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListDeviceTypes;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1()
{
	if (ivjJPanel1 == null)
	{
		try
		{
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0;
			constraintsJScrollPane1.gridy = 0;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(115, 25, 15, 175);
			getJPanel1().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsSelectDeviceTypeLabel = new java.awt.GridBagConstraints();
			constraintsSelectDeviceTypeLabel.gridx = 0;
			constraintsSelectDeviceTypeLabel.gridy = 0;
			constraintsSelectDeviceTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSelectDeviceTypeLabel.ipadx = 5;
			constraintsSelectDeviceTypeLabel.insets = new java.awt.Insets(0, 25, 175, 5);
			getJPanel1().add(getSelectDeviceTypeLabel(), constraintsSelectDeviceTypeLabel);
			// user code begin {1}
			// user code end
		}
		catch (java.lang.Throwable ivjExc)
		{
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1()
{
	if (ivjJScrollPane1 == null)
	{
		try
		{
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getJListDeviceTypes());
			// user code begin {1}
			// user code end
		}
		catch (java.lang.Throwable ivjExc)
		{
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize()
{
	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize()
{
	return new Dimension(350, 200);
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSelectDeviceTypeLabel()
{
	if (ivjSelectDeviceTypeLabel == null)
	{
		try
		{
			ivjSelectDeviceTypeLabel = new javax.swing.JLabel();
			ivjSelectDeviceTypeLabel.setName("SelectDeviceTypeLabel");
			ivjSelectDeviceTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSelectDeviceTypeLabel.setText("Select the new device type:");
			// user code begin {1}
			// user code end
		}
		catch (java.lang.Throwable ivjExc)
		{
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectDeviceTypeLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 11:42:42 AM)
 * @return int
 */
public int getSelectedDeviceType()
{
	int type = com.cannontech.database.data.pao.PAOGroups.getDeviceType( (String)getJListDeviceTypes().getSelectedValue() );
	return type;

}
/**
* This method was created in VisualAge.
* @return java.lang.Object
* @param val java.lang.Object
*/
public Object getValue(Object val)
{

	String type = null;

	if (getDevClass() == DeviceClasses.GROUP)
		for (int i = 0; i < deviceType[5].length; i++)
		{
			if (getJListDeviceTypes().getSelectedValue() == deviceType[5][i])
			{
				type = (String) valueList[i];
				break;
			}
		}
	else
		type = (String) getJListDeviceTypes().getSelectedValue();

	if (val == null)
		return new Integer( com.cannontech.database.data.pao.PAOGroups.getDeviceType(type) );

	else
	{

		((DeviceBase) val).setDeviceType( type );

		String devName = ((com.cannontech.database.data.device.DeviceBase) val).getPAOName();
		com.cannontech.database.db.device.Device device = ((com.cannontech.database.data.device.DeviceBase) val).getDevice();
		Integer address = null;
		Integer routeID = null;
		Integer portID = null;
		DeviceScanRate[] scanRates = null;
		//DeviceStatistics[] deviceStatistics = null;

		if (val instanceof com.cannontech.database.data.device.CarrierBase)
		{
			routeID = ((com.cannontech.database.data.device.CarrierBase) val).getDeviceRoutes().getRouteID();

			address = ((com.cannontech.database.data.device.CarrierBase) val).getDeviceCarrierSettings().getAddress();
		}
		else if (val instanceof com.cannontech.database.data.device.lm.LMGroup)
			if (val instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon)
				routeID = ((com.cannontech.database.data.device.lm.LMGroupEmetcon) val).getLmGroupEmetcon().getRouteID();
			else
				routeID = ((com.cannontech.database.data.device.lm.LMGroupVersacom) val).getLmGroupVersacom().getRouteID();
		else if (val instanceof com.cannontech.database.data.device.IDLCBase)
			address = ((com.cannontech.database.data.device.IDLCBase) val).getDeviceIDLCRemote().getAddress();
		if (val instanceof com.cannontech.database.data.device.RemoteBase)
			portID = ((com.cannontech.database.data.device.RemoteBase) val).getDeviceDirectCommSettings().getPortID();

		try
		{

			com.cannontech.database.Transaction t =
				com.cannontech.database.Transaction.createTransaction(
					com.cannontech.database.Transaction.DELETE_PARTIAL,
					((DBPersistent) val));

			t.execute();
		}
		catch (com.cannontech.database.TransactionException e)
		{

			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		try
		{
			scanRates = DeviceScanRate.getDeviceScanRates(((DeviceBase) val).getDevice().getDeviceID());
			//deviceStatistics = DeviceStatistics.getDeviceStatistics(((DeviceBase) val).getDevice().getDeviceID());
		}
		catch (java.sql.SQLException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

		val = com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType(type) );
		((com.cannontech.database.data.device.DeviceBase) val).setDevice(device);
		((com.cannontech.database.data.device.DeviceBase) val).setPAOName(devName);

		if (val instanceof com.cannontech.database.data.device.CarrierBase)
		{

			((com.cannontech.database.data.device.CarrierBase) val).getDeviceCarrierSettings().setAddress(address);

			((com.cannontech.database.data.device.CarrierBase) val).getDeviceRoutes().setRouteID(routeID);
		}
		else if (val instanceof com.cannontech.database.data.device.lm.LMGroup)
			if (val instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon)
				 ((com.cannontech.database.data.device.lm.LMGroupEmetcon) val).getLmGroupEmetcon().setRouteID(routeID);
			else
				 ((com.cannontech.database.data.device.lm.LMGroupVersacom) val).getLmGroupVersacom().setRouteID(routeID);
		else if (val instanceof com.cannontech.database.data.device.IDLCBase)
			 ((com.cannontech.database.data.device.IDLCBase) val).getDeviceIDLCRemote().setAddress(address);
		if (val instanceof com.cannontech.database.data.device.RemoteBase)
			 ((com.cannontech.database.data.device.RemoteBase) val).getDeviceDirectCommSettings().setPortID(portID);

		try
		{
			com.cannontech.database.Transaction t2 =
				com.cannontech.database.Transaction.createTransaction(
					com.cannontech.database.Transaction.ADD_PARTIAL,
					((DBPersistent) val));
			t2.execute();

		}
		catch (com.cannontech.database.TransactionException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );

		}

		if (scanRates != null)
		{
			for (int i = 0; i < scanRates.length; i++)
			{
				((TwoWayDevice) val).getDeviceScanRateVector().add(scanRates[i]);
			}
		}
		
/*		if (deviceStatistics != null)
		{
			for (int i = 0; i < deviceStatistics.length; i++)
			{
				((TwoWayDevice) val).getDeviceScanRateVector().add(deviceStatistics[i]);
			}
		}
*/

		return val;
	}

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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
      
      getJListDeviceTypes().addListSelectionListener( this );
		// user code end
		setName("DeviceNameAddressPanel");
		setLayout(new java.awt.GridLayout());
		setSize(350, 200);
		add(getJPanel1(), getJPanel1().getName());
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
	return getJListDeviceTypes().getSelectedIndex() >= 0;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args)
{
	try
	{
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceTypesPanel aDeviceTypesPanel;
		aDeviceTypesPanel = new DeviceTypesPanel();
		frame.getContentPane().add("Center", aDeviceTypesPanel);
		frame.setSize(aDeviceTypesPanel.getSize());
		frame.setVisible(true);
	}
	catch (Throwable exception)
	{
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/2001 3:51:22 PM)
 */
public void setDevClass(int deviceClass)
{
	devClass = deviceClass;
}
/**
 * Insert the method's description here.
 * Creation date: (6/13/2001 12:06:30 PM)
 */
public void setList(int deviceClass, int type)
{
	//sets device types according to class --- repeaters are a unique case
	setDevClass(deviceClass);

	if ((type == PAOGroups.REPEATER_800) || (type == PAOGroups.REPEATER))
	{

		getJListDeviceTypes().setListData(repeaters);
		getJListDeviceTypes().setSelectedIndex(0);
	}

	else
	{

		for (int i = 0; i < deviceCategory.length; i++)
		{
			if (deviceClass == deviceCategory[i])
			{
				getJListDeviceTypes().setListData(deviceType[i]);
				getJListDeviceTypes().setSelectedIndex(0);

			}

		}

	}

}

public void valueChanged( javax.swing.event.ListSelectionEvent ev )
{
   if( ev.getSource() == getJListDeviceTypes() )
      fireInputUpdate();
}

/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB6F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD4D45719382051E0CE63AEB975247654B424471EE5D7F6CB3A1C2E5D321C3447B674A459CD1A1C2C5DF5CFB89B36B5A927F1DB13356D1D9998B4C2A40AA9B6510C51C4CDF0G359BCAE8830A48608491061F34FA74413C998606F943FBEF18810C6E775D1FB70FF1860C299CBF5F3B5F3D777B3B5F7D3E6F5EB724664D556596EB8DA116F2427F5E379012D1C748B3D9434B84AECDAE3493535F81
	00D749221AB2F85E8DFDF5AB8A6D5564F97BB6E8E7C33B602EC27B2F61FDB7F941DEFD84DED270A9B9CC4851630F2D1C1F4F753302CF85ED67CDD642739500B5004367E6997997CD56F2FC91478B30D889596843B14FCE3E41F1CD506E81D885106BE3635743F3BB653C3839090F3B38E2B5597C3D4356AA24E322A960081F4DF57327032C35EC76A32FEA35124F490076BAGD45EC63E547787F836740C7B7FB022CCB8C724D0C88969724828B39A1811D42FF3E4D48A79E5FDBAACBB3D72D4E0C4EEEEEE27CF8F
	20349EA9A48789890FA20FA1F59B25897AC0FBAB45F59FC33EF970DE84D0EC6077B8915F9AA8348F810483C2575F9FEE613A8C4D2EA5F9CF06ECBBD3683AB440F44C8D44F54D0A3CD431172E4B37BA517E1B202FFA2C50FE882093E094E0A9406B549F3EF376DDF8368D6B27150989A554968E1F8AFA550F942F9C047766E6686138DEF9C4D13D0430B5FFCD2DCBA24FFC40669D7A547318CD561C906B1F537B953261EB672C95C9EC32E1CD03F5759833C57ED85CA60C7725143C1F9A1B6B1FF3F98F92B36F6CA5
	0A2DBC896F3CA70EDAB7F1BAE4BC4E3BE19C573A107A00835EAD5CBF983E0862F5931E792A0762311D81FDA56342B706DE9132384B6EA5550D6E24F6D8D958E6F587198C5EE022AC4B285F5D9DE4BCAD06ACFFC9712BCCF8A64B6BCE31EF56C0DF65C4211D79573FF6615E8DC3FBB9C0B640BA008DGDBGAAC34247F6F6565F240F75C82AFEAA9288FAE51530396BBBDF03279C53D5499552F4A9B4A28FC8EAC09A8E4A9A61E39E6BC49B8C4F1977836A7BA340632C9C12D5C98FA8A120AECD483A2CEAEC4CFFF4
	3E0AE3D41976FA8293B2B0188893C177594E5D708CCA1A5E9F76CA3A9C9F417AFF5209EBB2A58DGC5A0G6F4CAE440D762A837D5B81BC069D46DCC86F1C2CC23CE8EEF6AB4AF8A46CC1CDC8F6886D2C5358B18A6FE9A1E1632DEE4415C01BA8024EFD2EDDA6BA6D019194DFD227459EDBFF8C63F30DA2FC6679A161B36DD58F103AA73A127A8C0D418A73159EA5F16F7C8B2509F231769F0F8DAD30EFEB1464FB6A66E30536A08F5BAD52FF45D82B31EEDC2B98481A8510E758F8034B41FC7614244942CB4F2B60
	E032B7EEF1A667671C4410F925697D00F3A179BF8BB09E4D37158733458F3BB07F48D3F2C8EF1743F2488BAF9A6FAB6EC29F637B532798F3FEC471E1584F27E183E9863E348B771C246BEAE0B8224BFD322E87C27EF87F964ACB0B8C076321C0707AD59746DDA58A7B5998DFF68E774208928A49F4D10CA93C5FCE77229C14A7C06853CAA424B3FC259B7DF9DC1E6631205A0D054E14940CE0DCE1F5C12E3483F5D083933054981AC8C39877E926DAEB32D191788C56B7E550D79B96FBAEFAD06C39C3B37711024B
	EFDB13650C158F34DBE3E136CF160763FB0E49E479A8396C959FED0B63B5A66B79F7B1CE322A249B22DC1B572B4A1A28CD182F5FFF9E6965G1F82002742425753BF42FE91CF38131FD602C1E6D24356616EEADC9BF9B2A2C3984640F54E30DF34FBD7C25FG37A56B3F5E0D368C1AE29E133B62B26664E43517BF92F0C674GC6B8C987678AC7F42A8D2B337E0746CA24D19A96755401A9546F48E4213D95E0E8D22C45E3D5221E4A9ADFCDB65E7F1B1435E3F8122DC156E4FCAD9C69221EFA2E87F9BE8EFD4513
	42F6AFF51BE3FA1B26A9A3811ACE385EDFBDEFDEAB067B5B73AF435397D0B55D851BA9A6EC14F79E6545FC4351FC0F3FF4C35D32E5B29EDBAB559B63AC4B6777D0FBB854441A63CB945F20A6569C0727C44D518AFD592A48C16F76223FEF53200684E88170G68GD91A30C3CB4F8932C00EF7413A2AA1A958B7E7F3F35DFF5AE3E37B1137FF56D3AE76A3BCD9FEFCD5C3DB66824F07G986EF91D66C7E893DD6426AFCC615EBEE4349F206DA623FDECEA3E180973BEFBDC6E181747E749DB7138FC22B7F5DCEE6E
	CD9E17DBFB1347650E5ED7999EE8182BB0515F5DBBFF5C9E68CD9637ED346F08E627FEBD2DC4F4B8536938EE9FEAA206DE04368E308AA0D7E7358F0B8DFF9745186103228DD875056591129D41396591B1379A5A07G5A813CG99G0C675AF6DC07AE9AB19AF95C7E4114B9EEEF0AB0DE55D3C083006D813FA6EA5D964069D3C216C7AE63DE20F1EACC1A121CD2D4EFEEBE2B863CEE2E96BF7F2D25BE0A73F326C4DC7AA125D90CED001A2808CB31F7C4DC4A794A3D441DF6AEE9DC5A68B9EE2D0B723AAC9A0FCB
	EB601C47F83E9791BC17C37FC3D144FD9C29C9703795B0G0B93561FB9279F5776A0B177FFF3CEBF4A7BCD2B397FA7F37A51C67B78FC265B43EF8B5D36BE3C06849F7CDD4A18DB99E3BAB570E74F40C1F2684E7F2DE0F6FF61E82D596EB1279685E7E8EE6EB26502D722A2970C46044DD7CC234DD7C3BB9F605118587B935A2D1DDBE7EFF1DE5E271E47E274DA9D65AF09A51E5172A9FEF3AC710C760C6137E49A764234085D87BDE83F7550AE86588AD0B3D3E8BF8950B3A3E2770FFB9B4882315B23045D90E802
	26504074EB085C5AF97E1FAB04FF64FD634BA4FF490B169DA97C43BB437CA203BFCBBC87A91E697775FE54AF8F7A9E85588CD0B9DBE8F7GB44D8A7DCE7455B17D4EF883FA1B2ECBA323B49B8B658682F2B40E46C043F45B9DCD544DBD9B4F8DD7E6D3655D7299FA261FE5EBD4EE602BA9FE29894F566E7354D7313D8A7A48D5612B8FE2D861FE74CF26777F56EE7F39422A5F7EF3C51DA7F57EBA61C91E3FFE67C91E3FBE744C1F1F4EFAD21FAB3ABD66F385BFF78FE0FC7342DA2481E483648394DDB54E071E37
	18CFA57127B3DE3FFCDA2137953A607F2D7E543AFE27FF61BB0EC77A31E69CD6F4DD19C07A827FC4BF2E172E04A912611E6EC79B29817F284E06F27C46FE0C13C159C7512CFEFA17464E9250EB6BD5D1D3DD1E96B5156393610BBBE690578E6D0B866EB50A8BC3BB4B40FDF1FABE3F4139B7639F6DA946B19EDF1F17C779CD7AE02A9A0C1FAB87D32F570E4105572BE610567DE0E73CA3E6383D03D887A9BE1FA69BFE31FF305A4039349EC58B60610155B0ED519B6B26212828E17240C6C5G1B8136DD03F8877D
	0EEBB8E6EA3AF6813DE13FDC45185355B72242B98FF3E9A34B372F4DA24FAE37B40C67C6D6B7F54E18690DF2DED970DC83300572FC325CDC8F9470B14557C4DD75FFB1111FDA2F975AE357C5DD55F8856F901354D527A4BFAB2DD80E3F9693B5CE6EF51163F3A84D92E813B4B8FB27093AEA7DA5F36D61F82FACE96D3165D23D35A10D7919A7ADDED7BDBE3615579E7EB37BC8525AA3EEB5A26A3E3499F16F3FB4CD4877388565AB0076D3G25E9C26797FCA9EA9AEAF5DE5350EFB7686F5B96893DBE69B01FE357
	6DBCEECD15B3DB97B1FD2EAC0A6BD58775A25BBFAD31DB2BE53AEEF1DE7A271E4772250355F00B92EB2023941F33A831867AFB434772216F0D3F90B55013C3E86FB4E82F82A8GF88AE093C0D93A0867390378D9ECC09A63558F168A0ACAA3BAA019A752182ECA01104E74DABCB31FDE3569296A03CEAB3D9FCECFBC1777D2FCCBFA62397CE90BB8173B21AFB7DD445A5DF6B37F1A8C466F88FF36E45C481F59C37760DE6D017EA0C08640AA00F5994296AF8EAEF816BF93CBFA16E7F23DB3CD0C352FB73D6747EE
	FF5D7258ED65417368D06A5C706C50423921F4A8F9AE7F59500D395C5917BC171F6C3BB1172F1A91F9FBC3067011EEAB5EFD54AD966DA15AEEB55A9BADFFAEFBA10F64EB72621F0DC7321C4D6B989AAF2F008C6145220E693900388CE8E7993887AF889B2DDEAC7C7D6ABCF5E8FA0ABAF4D90ABA74F31E796FD1BE1F32CEEDDA0272FDED261D6BD51A441EA25E59B814411CDA00BD3CDD37A49E93D91E7F07CBAC1F87B43D91F29E4D554B2C66DC1D86F330064BDEA26ECE5A6C68A3BEA524EF26B28DD25B66437B
	C346987F859C2303E11A6E405F885C73A7E268D0F4C168F0750E05F5389B46A09C6478ECF897F90D65426299F13E4F053EB7A9FF480BC47C15FE188C4764AA3908AD16BF7F33D2644FB77E7A8B247D4997AD02E7220CA5DC6E2625717CD9E254858F9F455FD2A44B5FE0E5AEF301D5489C869ACC168F060931AE6D97707C9012238ED176B8BB4060C75E7286242062FF2B4E51D067E8BCF9D4483EBAEA7E7DC3534B6FA77D1E0030064B3CCEBC6FB848E9BC72A7B30D6AFB8FD8ADA9E8EC66F34932387ECC2FDFDC
	90FA1DDC16ECAD40E6D4579F0D60BBBF2F4E4913C1181736EC7EF3G4BD34FF8510F32E0ECAE4063G1BG481D6073GF5G2DG9E00A0C08640CA009C008200C7810A81365CA9767931CBE04E14B91E971D71A4EF487FED6BFCF93EB2F32EAE0ECCD1F738FCE81713506E4994B2248D3FB70F8CBD2A9216D5FD1AE1699794EE0BF6BF5A131F97075FCEC583D2F39778537961B1F9C477C8FEC387EB2FD907B0481311B97F9A30F8C9E6D1071519AC2EB0DCB94565F29C1369675E54B174175E05F3E2051CBCA7D6
	49B76644DDF2721CD8AB1BF3A2436D1351D77D3036C34638378C5C47866EB02509384100D79FB5704728DC086F141121F177CE650AD25C9047B519F09FF35C6FE50C3F207228220A3927E49ADF8341A03FFB27E382A1B8829A76B8C7E58C04A5135CBD866EE3E3ADB6D8C5BEAB4A94B92E228CF125502EDCAE7C2E5B07EBEA649D66DA1CEE3F2F0672072327948C4C504FD7FDB0B064E77476765E68837BFB4DBE40622274CEE51C8EB5A3433F912B0A63059979DD7E8BD6F136F4002CA781BCGBAC056F211EB76
	1761FCCD665FA3319005B64BCD6F1709F3D79E0CE7B1EA2FCD4428AD4B45FD7885DB62FDB852F092F11F1DEECB3C8F374C69FF5016F89F7EB7F36E431F35A55E070B79CC076EB20C3D26F46144E451E756A9686CF3F51FF51F396C6A761C699DE8F3DF6EBB63BEF35A63BA5F4D7670FE1BF98FB75CD5E8EF3F6BE67670EFEDE27DD0E606EB360975DB8402325F860E6E3A824266BCC74AF572BA934D210F4A8E4CD26CF7EF8E7C8DDC3326121DC656F2D1375DB8F52259764C7DB6ABA9F391BB5B46AED206FB51C5
	4A69D6F5919BEC63987E3C86C385063535382CC02DEACE8E7C76FD3F7D2F22FF9F50EC446EA2FB318C6913D56A193297BF85731F5BF4106DE18542F49F600115C69F651ABE9D145D2446570B1F22B5DD7636891F075C3C470D1C6A7133F2FD27240D6A9803BEF01332907EB0086CE25DC1A9642FE79B6083AA2F256D8AA15C2484645DCD65C5AACE034A6EFF5C7976012E62FC9B31F61017030A646D10C6A0AC3B48F6FDD41535D1A5680D05BFEC25043651DBB72CF2ECC06EF5C3FDD4566901C8E988BD9E7B6272
	2A122A8ED83818CA6A69C82C3A1C46886F69D1C985D6326A527107CE0AFA4868DB34DFFF677BE7FFFD51CAACDC49C9F20D144742BA317B2409C0F09ADED84521E14FF69D7E29D2C873A96A84C866C810CCF4998C1CFF755D06FB3E7A529FA90378D41D3C49CC89D36B7D32B2D15F667385C232C1G6C8A64779964F10CB3E14C2E580ABDDDCF7CEA987C201D58A69A9A687F0D747F6378FFA345B4D2CC63F1B0EEA91977AF31BFA6F4264EE3F8B5AE3F3383D6D0FF7A396FFE795FDACAB701F69D241CFE0C06C528
	0E0F6A0ECC8C4B6A121CFB766D485A1809EB94C6D709A15DAAC65E427DFCAF8F8B3C96BBFA5DDC0BEDD9DC1B321E8B2F605FBFD644EBB1D6934476209795GFE438A916757579CA329EB033E89A998542560F8DB480BF54229E9DD0E1FDDBE590374B5B106F7323E0CBD5BF97C92F37E9FD0CB8788BA363CF22F90GG2CACGGD0CB818294G94G88G88GB6F954ACBA363CF22F90GG2CACGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBA
	GGG6990GGGG
**end of data**/
}
}
