package com.cannontech.dbeditor.wizard.device;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;

import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.pao.PAOGroups;
 
public class DeviceTypePanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.ListSelectionListener 
{
	private javax.swing.JLabel ivjTypeLabel = null;
	//CHANGE THESE STRINGS TO CONSTANTS AT LEAST AND GET THE THE STRINGS
	//BY CALLING DeviceTypes!!!!
	String deviceCategory[] = {
									"MCT",
									"Signal Transmitter",
									"Electronic Meter",
									"RTU",
									"Virtual"
								};
	String deviceType[][] = {
								{	//MCTs
									PAOGroups.STRING_MCT_410IL[0],
									PAOGroups.STRING_MCT_410_KWH_ONLY[0],
									PAOGroups.STRING_MCT_370[0],
									PAOGroups.STRING_MCT_360[0],
									PAOGroups.STRING_MCT_318L[0],
									PAOGroups.STRING_MCT_318[0],
                           PAOGroups.STRING_MCT_310CT[0],
									PAOGroups.STRING_MCT_310ID[0],
									PAOGroups.STRING_MCT_310IDL[0],
									PAOGroups.STRING_MCT_310IL[0],                           
                           PAOGroups.STRING_MCT_310IM[0],
									PAOGroups.STRING_MCT_310[0],
									PAOGroups.STRING_MCT_250[0],
									PAOGroups.STRING_MCT_248[0],
									PAOGroups.STRING_MCT_240[0],
									PAOGroups.STRING_MCT_213[0],
									PAOGroups.STRING_MCT_210[0],
									PAOGroups.STRING_MCT_BROADCAST[0],
									PAOGroups.STRING_LMT_2[0],
									PAOGroups.STRING_DCT_501[0]
								},
								{	//Signal Transmitters									
									PAOGroups.STRING_CCU_710[0],
									PAOGroups.STRING_CCU_711[0],
									PAOGroups.STRING_LCU_415[0],
									PAOGroups.STRING_LCU_LG[0],
									PAOGroups.STRING_LCU_T3026[0],
									PAOGroups.STRING_LCU_ER[0],
									PAOGroups.STRING_REPEATER[1],
									PAOGroups.STRING_REPEATER_800[0],
									PAOGroups.STRING_TAP_TERMINAL[2],
									PAOGroups.STRING_WCTP_TERMINAL[0],
									PAOGroups.STRING_TCU_5000[0],
									PAOGroups.STRING_TCU_5500[0],
									PAOGroups.STRING_SERIES_5_LMI[0],
									PAOGroups.STRING_RTC[0]
								},								
								{	//Electronic Meters
									PAOGroups.STRING_ALPHA_POWERPLUS[0],
									PAOGroups.STRING_ALPHA_A1[0],
									PAOGroups.STRING_DR_87[0],
									PAOGroups.STRING_FULCRUM[0],  // Schlumberger
                           PAOGroups.STRING_ION_7330[0],
                           PAOGroups.STRING_ION_7700[0],
                           PAOGroups.STRING_ION_8300[0],									
									PAOGroups.STRING_LANDISGYR_RS4[0],
									PAOGroups.STRING_QUANTUM[0],
									PAOGroups.STRING_SIXNET[0],
									PAOGroups.STRING_TRANSDATA_MARKV[0],
									PAOGroups.STRING_VECTRON[0],
									PAOGroups.STRING_KV[0],
									PAOGroups.STRING_KVII[0]

								},								
								{	//RTUs
                           PAOGroups.STRING_DAVIS_WEATHER[0],
                           PAOGroups.STRING_RTU_DART[0],
                           PAOGroups.STRING_RTU_DNP[0],
									PAOGroups.STRING_RTU_ILEX[0],
									PAOGroups.STRING_RTU_WELCO[0],
									PAOGroups.STRING_RTM[0]
								},
								{  // Virtual Devices
									PAOGroups.STRING_VIRTUAL_SYSTEM[0]
								}
							};
	
	private javax.swing.JList ivjDeviceCategoryList = null;
	private javax.swing.JScrollPane ivjDeviceCategoryScrollPane = null;
	private javax.swing.JList ivjDeviceTypeList = null;
	private javax.swing.JScrollPane ivjDeviceTypeScrollPane = null;
	private javax.swing.JPanel ivjListBoxPanel = null;
	private GridLayout ivjListBoxPanelGridLayout = null;

public DeviceTypePanel() {
	super();
	initialize();
}


/**
 * connEtoC1:  (TypeList.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> DeviceTypePanel.typeList_ValueChanged(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.deviceCategoryList_ValueChanged();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public void deviceCategoryList_ValueChanged() {

	
	int selected = getDeviceCategoryList().getSelectedIndex();

	getDeviceTypeList().setListData( deviceType[selected] );

	getDeviceTypeList().setSelectedIndex(0);
	
	invalidate();
	repaint();
}


/**
 * Return the DeviceCategoryList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getDeviceCategoryList() {
	if (ivjDeviceCategoryList == null) {
		try {
			ivjDeviceCategoryList = new javax.swing.JList();
			ivjDeviceCategoryList.setName("DeviceCategoryList");
			ivjDeviceCategoryList.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDeviceCategoryList.setBounds(0, 0, 160, 120);
			ivjDeviceCategoryList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceCategoryList;
}


/**
 * Return the DeviceCategoryScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getDeviceCategoryScrollPane() {
	if (ivjDeviceCategoryScrollPane == null) {
		try {
			ivjDeviceCategoryScrollPane = new javax.swing.JScrollPane();
			ivjDeviceCategoryScrollPane.setName("DeviceCategoryScrollPane");
			getDeviceCategoryScrollPane().setViewportView(getDeviceCategoryList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceCategoryScrollPane;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getDeviceType() 
{
	return com.cannontech.database.data.pao.PAOGroups.getDeviceType( ((String)getDeviceTypeList().getSelectedValue()).trim() );
}


/**
 * Return the DeviceTypeList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getDeviceTypeList() {
	if (ivjDeviceTypeList == null) {
		try {
			ivjDeviceTypeList = new javax.swing.JList();
			ivjDeviceTypeList.setName("DeviceTypeList");
			ivjDeviceTypeList.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDeviceTypeList.setVisibleRowCount(12);
			ivjDeviceTypeList.setBounds(-9, 0, 169, 120);
			ivjDeviceTypeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceTypeList;
}


/**
 * Return the DeviceTypeScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getDeviceTypeScrollPane() {
	if (ivjDeviceTypeScrollPane == null) {
		try {
			ivjDeviceTypeScrollPane = new javax.swing.JScrollPane();
			ivjDeviceTypeScrollPane.setName("DeviceTypeScrollPane");
			ivjDeviceTypeScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			getDeviceTypeScrollPane().setViewportView(getDeviceTypeList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceTypeScrollPane;
}


/**
 * Return the ListBoxPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getListBoxPanel() {
	if (ivjListBoxPanel == null) {
		try {
			ivjListBoxPanel = new javax.swing.JPanel();
			ivjListBoxPanel.setName("ListBoxPanel");
			ivjListBoxPanel.setLayout(getListBoxPanelGridLayout());
			getListBoxPanel().add(getDeviceCategoryScrollPane(), getDeviceCategoryScrollPane().getName());
			getListBoxPanel().add(getDeviceTypeScrollPane(), getDeviceTypeScrollPane().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjListBoxPanel;
}


/**
 * Return the ListBoxPanelGridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getListBoxPanelGridLayout() {
	java.awt.GridLayout ivjListBoxPanelGridLayout = null;
	try {
		/* Create part */
		ivjListBoxPanelGridLayout = new java.awt.GridLayout();
		ivjListBoxPanelGridLayout.setHgap(20);
		ivjListBoxPanelGridLayout.setColumns(2);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjListBoxPanelGridLayout;
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
 * Return the TypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTypeLabel() {
	if (ivjTypeLabel == null) {
		try {
			ivjTypeLabel = new javax.swing.JLabel();
			ivjTypeLabel.setName("TypeLabel");
			ivjTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTypeLabel.setText("Select the type of device:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTypeLabel;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val)
{
	//Determine the correct type of device and return it

	String typeString = (String) getDeviceTypeList().getSelectedValue();

	int type = com.cannontech.database.data.pao.PAOGroups.getDeviceType(typeString);
	DeviceBase returnDevice = com.cannontech.database.data.device.DeviceFactory.createDevice(type);

	return returnDevice;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getDeviceCategoryList().addListSelectionListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceTypePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsTypeLabel = new java.awt.GridBagConstraints();
		constraintsTypeLabel.gridx = 1; constraintsTypeLabel.gridy = 1;
		constraintsTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsTypeLabel.ipadx = 4;
		constraintsTypeLabel.insets = new java.awt.Insets(17, 12, 4, 175);
		add(getTypeLabel(), constraintsTypeLabel);

		java.awt.GridBagConstraints constraintsListBoxPanel = new java.awt.GridBagConstraints();
		constraintsListBoxPanel.gridx = 1; constraintsListBoxPanel.gridy = 2;
		constraintsListBoxPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsListBoxPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsListBoxPanel.weightx = 1.0;
		constraintsListBoxPanel.weighty = 1.0;
		constraintsListBoxPanel.ipadx = 264;
		constraintsListBoxPanel.ipady = 125;
		constraintsListBoxPanel.insets = new java.awt.Insets(4, 12, 9, 10);
		add(getListBoxPanel(), constraintsListBoxPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	//Manually add the device types to the TypeList for now

	getDeviceCategoryList().setListData(deviceCategory);
	getDeviceCategoryList().setSelectedIndex(0);
	
	// user code end
}


/**
 * isDataComplete method comment.
 */
public boolean isDataComplete() {

	if( getDeviceTypeList().getSelectedValue() != null )
		return true;
	else
		return false;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getDeviceTypeList().getSelectedValue() == null )
	{
		setErrorString("A device type must be selected");		
		return false;
	}
	else
		return true;

}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		Frame frame = new java.awt.Frame();
		DeviceTypePanel aDeviceTypePanel;
		aDeviceTypePanel = new DeviceTypePanel();
		frame.add("Center", aDeviceTypePanel);
		frame.setSize(aDeviceTypePanel.getSize());
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
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
	return;
}


/**
 * Comment
 */
public void typeList_ValueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
	fireInputUpdate();
}


/**
 * Method to handle events for the ListSelectionListener interface.
 * @param e javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(javax.swing.event.ListSelectionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getDeviceCategoryList()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD7F161ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DF4D45519D6312BDB0A51868FF2E817AA6D62E9F62DDDD4361B5D259E5A2232963B32D22B9E7618F6539E2B2932E73316BD5CCC82CCB011G23268A92FE54D053A1812C9882E4E692C8F088E1E692C2B2C372B273123C644D3C493C17E482E8777B6EFD774D4BE4A6B12E1C331C73653D7B5D3F6FFE7F77FB83316DDE383140E2A324E0A3517FFDD8C0C84687045C547062890E1B57643492533FBD
	G5B486DF5C570DC8CFD2D1FB82DE5E4348A87E5C3BBBB6E343E816FBB48152AF58761A54DBEAB216F6747AE4E1FFA1F47A43E4FE65AFE75C489BCDF85D886B8FC16B4637E6F1EA85771B9BA1EC301051067689847091DBA2E955A83G0B81321B59784761D9CCF73E317628BE4EFFEBA6D977706B16D2DC4734A607A3E7585C8E7DD9C13ED1C0BE463D86A91DB8A7837AD6G60618B493A66C3702CEB9D6ABF91D44276209009A891CD8C8E5847244BC2ACE48F0923D2D02C2DED20CF5FF8D4EC95A222CC48D53A
	6E77CED869BA04B4C05B6AC65C36EE5CEB993457G649978B5946F06779040D5B7BF5F71776AF47ABB2E5CC596FD2F3AE8EB1A73E53A5939325D09732D8CD6957D1E4AE25B9072BC9F7AF61EF5DA2B81FCG9AC0A640DBD4874A877FGCFC72F66D242E1A56208C61DF2A8F6D68909B23C5756C20F0E7308C1A596A20449B9FA22AA85BDD3811B77690C67B11EACBE4CE51E6519CF1E7A3BDA4B6694BCF9EA71EECB56D9460B65E793BCE1FBBF10F66F1C3393F5F2625E9708F96F4C8766D8B62658FB496AF2CB3E
	3ECEC9CBE26F3A9614750DD456D570DE206B87435FC271571970CCBF5F20F8ECE7C05F3A962E9B9D2FF2DA5CC5F3C8D94D3114FC1877E03525639C23A1FAAE1996F78F2E3D60DCB2AD6D941FF5AE1916E78CDA32212F2255E9E57A75E6986DF596349782AC85D8851087D0F29E461D67BAA68C9E7C0CBA56AA44B46708A4074498E1F35F9CFA9D1EE2DC0B894D91D593A2C131DD08C9C22FAC2AC49F33EF88F950BBE1DEA5557DA06CF1DA0C08B1C11314882CAE04C5CD0C29ECCC4D50B69C9393E92FCF8A0B30C1
	B8CA783A9F8D6D0027AC28DADBB4A4E8E2E2846BF78C21CCC605F6D8919600F74617539164D7BD1C3F83E04060C3B6DD6FE3B1867E2236562DA8C3A3D19F1E04ACBD0FFCFE147A0E2B701EF91E735825A0EEBD34ED9E3E4ECBC35BCD6BB4C8C1A4DF080DF39B7BCAB77A64CA8F571967BB384EB4145EC12AD6FF14D2E7E83F07690A5F13ECBBF3691AC897EB6F6A6E18219D2702CACFEA3BE3FB7C698B5923B08D1E71775EA84AEA96503190E01941E33BDC41F456A928A25752F3B1D0B0B11460B8235353D32473
	13357D343D4643F915C34F110A5FDF8430BD7E076E116E2C7FD7E0BC599F46B8A40E0A912DC10C0A11903C28FA5F7BE154B55DCE7B94E3CE8D45C7412EDDE0C82A017FB00C36A7E8DACC6A9D51C42F28E9D224BF51FF126E250E74C693AE016FF5AA0C7ED799833BB646FF9CC61B88AA1108C805E3CC517B4FD21B94E5B18CC43B14110846705E884AEAC89C57FD420548A602B6AD0F20FFE1B941F705D7708CB1A98CA2C797C5C2DE3457DF98B98D82819C66B6C7616976F25B9B5B47EDEF7F6539E4D937ADE56C
	18772D4316ACAF3305E55E046DB11A1A14543437D05F2063D5C6EBD6AD72ADB9B9E13A7E6BE1DCE7AD2C3F8E204447F57DE1857B39BF51155C2548B2E32541638722E5A893F1F8C484B70C0E6BE303EF7F945D1E54572E7310757FA80ABC14CDBEEF0D8FF978559E1E736D07F69D005F477838584778404E35399BE3F72A3C2CFFC4320FE892FAC2C183650B0EE87450AC9FBBF7522A2F313F0D67DD12937960073686B03B0D4B6A31D21EF7E58EE51235F795274DAB9735B15AD634A5E4157B534D050C4FC7D45C
	535A6E341637F39EBB064D3E5F212ACAD022E1C767CF5E30D9168C771F43DB6059A745D42D990CAD4EF9793BE1249763120E5663747DB4FE63D92BE05F90C0FEBB62D73759CC34AFG5CB2009C00C23A5E3341FADCCFD2B52F284B5CCEAD40B5A0C47A45D06520B0AA446D6A98D8281D1A3F5DED9E5C082883CCCE37D56D05A7CB12DD90B37B1558B88E6EEEB7AFF8201D67D6F76E285769DA7937F3C85D1FAD16D4396ECDDBCBAC7597982F87AEA4F82E4D2BAA3A890079C05F49647CEE4905C4FCD9F1E1F22CE1
	F94F53D49F1E3A40721E0D86BE176273CCF846E7454979DC5481713D03476AC7B57487AB211D8BC0AE027C810E82F4DC64FAF0C36CB019A697EA863D0EC58459BB4179711CC4ADE47ECAEF9F27F6CF25C5F8AC38BE184C876D624433E7A435593C0CC9736C497CCC1A47E47E4801B738ED1AB42536768C8ADE771573FDE6DF39925649875859897792GE62BA56DD61D1626CEEE131F1CB25BE47656BD16D7525864F51D0C36051D897D48B06EC271D174B3EB21EFBD002D8B76857081C481B23B38FCFE2C9E1FCE
	BED45DFD0A8BF0CD604FDC49B1876633FCEE087A1EA530768AG067B1383F14FC13B484F7303E769453A925A75866EF90A6B70B3BEB15C08B3DDBE04F313F175A9F0536791F3526E11AA3758B8037593F94493DA7ABC6299ADF59E1123254EA3FE2DEDE3F8D843FCFB607DAFEAD36799AFEB29720CDD743C8F3D3E152E2D2A22CA050E81C66F7B8F6DB512A25FE8018B064E2B9D646F03457A18C6CDF1559876525F38AB353DF803B1083A983D201F594D574F1B6DA66EE7F70385707C2E1F5B4DCFFD3C6630424F
	E3DCC59B7AA892G9F84D09D60BA342D5DEC63CD0109BA62CFEAC7835C37ED984398B28B5A8B8116822C82488BF03B499819562FB5462770EB570F2657091BC7D36B44AD23981FD5E5A496941365FE7BE83951173050E6CD8C230858183F2273B5A156AFCE521BEF53792CAF5D7CFBC6536996137F75A7B1171405F1E5C403FB5DA5144B9DA739BC4B20DD89508A903AE44EF56E9DC39F578717D5D84E9B950324239B671EF570394BE17C9A00FC00F268BB5A4DE379FA6AB7B173F38B3DD81652E959FD0A561A40
	A1BB15B84D0FD8EED061B267868B3B19FEAD6F667530178E142756D5D30E357ABC4F3156F4F37D7B44037B15754099816AFB387EBD611B2A7E535113AEC6E7D2FD16FB12EB965F20782BBD49B50BF75BF84DE2B6746575F07FDBC9E30CCFG1E8B9C676864BEB9EE60FA3B9037G5B8257714D7174BA7AEABC350E5A62297D561B71297D565EF8BA5DE2677B41C9DE3FDC89749575F2F95550FC5A1017DD9853EAEBCF472410D368F7D3CDE4B2AB2E376A736BFA394CBE2C47B5B5E84F86D8544BE3F07CDD9E0333
	3EBD07382F2BCD19972F756D31E477B2792D6DCD4460359003591E2A036F39B38831B70838EFBA4C7A07723A3500659D2C7FDFA774A3CF8E90F37FC627399F697DC70B397F43897D9BCD7359599EF9071FED7DA38B08FCDFE55A1CFFC1D03F1F694F7C7B7712C5FF30966AF6521746CE28993273DFF0717CA6BF486D6149734976D09DCA58C0C3A81DBDDC70212CBAC249B9EBAF454B21641C75868F4FD92F033E1C90577367FDDC771BC4CE537CB63C934BC6FB91ED635D0C352B5AE6DA174135E61607B44EF80F
	1954BE18CFBC590E676A03FB648A91F951C6FD43ECF81F8740FCEEC7A77739A5FDCEEB85C0BDC08F40F5FD5C670EF751B8442FC39874204D563C2D0B6B7C42BE4E3F9FCF3A27FCB61E2C7E1C73DE2D1F69BC26CB458EFAAF6AE33AD4E06037D3FC2E894FF46CF8BD77C96B202F231F678EF9634843ECE82F814883308EC0DCGE898603EF5E93C1ACC13BF7814289B1C2FECF2176CFCF147677B2670034D5C7617FC7FF632744B3985696E969D83CCFF637AB3633E3D944F4EF74395BC5FE268DB8E108BC0A438E3
	GD4C97CFCE43C0A1D2FB1A4E98ECD93028374264F8F57AE09E389B486FD3D1668B21F2DDEE27B7B746700B4592E18DD7F1646642B501FA1F1DBAFB0ECFD0944F5B1C0F1C503A00FC10E9B20B82C91B499381BBDB331C7DCAFDDDFE50ABE36471DB35AE3AAFBCFF5AF1187E93CE8AB1FE64D9D641FC3D84BEA18D443GBD9944EB59DBC82EBE7603CE2CF7C444318A9EEE6D274021D46E8DC9022C746F2D2A282E2A28B9F2006B5C42B173374B23DB5ECDD98FC4485779D4B2441E215C3D7AB746CFBB39CFE99862
	F1B42BB3B90E7E16186360B31D49F134E0C2FFD9D7F29CF5CC08434E2E64B84A67B3BE5C5D055FF84C052289A5A2EA48F6EFF34BE9F7E33737515D6872B51FEB51653E55C18C9DA8B73D3B6A3F087A7F543E51FFCD77E0367F5F636973C6F2B9F55E3869F26A3C3164725477D95B6574F57337AE1B6B664CF79D3222DC2F029E4D834886D88510B3447D565D17D13F718B5DE41F55986A473B95FE1553787E332F24BF6B572ECC7FADEF41952CF976AA1A2604F1FD0E3F738A4ACBD3229449F0FFFD85F994137A87
	B4B6D4477F4D95348FD96C23E8667BD41AFB91995EE56E8FFFC46DE8BF340F9838773B380F2C17F9FC37FAA65F63F6FB389F4900F10B81D68164482CDED39D46B1653E06A9F42310707B9E0375C89B4C5B273777437BB648E5FFE75858DC976EB951AFA5CA86ECCC49B19BB1577F937D2C1E70F0D72ABADC9C761A9D66797E0A00B9A72EF87F2502D4B9F1FE60ED4B2230DE878FA7727D055F3A59425679C9175957154DAF339424D8076E234F37C6926BB07D7CD9B14A238E709D912E13BD1B5F07E73BB028E789
	98D2159855CAC0364614289853469916161DF5DB3F21F326F9825B2F561ADE279D5669F57A0C155676C197D1A28CF74E0ABA2574752922E1BBBEEB1901EBD6DB95D5C25749647A172E09396AC22A4FF160492C88575327BBF10C864430B8F586539942F45159B51DAE1AE1892C0930CF17570A88530DC42EF75A43F33D47212FC461774F1FF4235CD25CBF5D3A7968B16C968F2F99B4A87C7E596225F7F9E8E782E4816C2667823DA07CDF5E49D4B8F65EC5346445F46F5FCB388E6F7C4EAD2461095C82FE166433
	AFD7184E5922895D1B8DBA4C683478B91D6EA82773EEBF7DE68C6D85GCB22BC4FDDD5CA6B7F504E85A899F6DA77839C85E89D663A7BCBEB65943A4B18C4FDEACC10A29ADB77A5A40BFFE7ADFEA75DFC7048E7006567FA87A1667A04FEE34EB345380091B318C968FD3FA8C6FFA1C5C05584D93ACCBFC9F9E1E0241F7174AE8CB73A7F797EE74EF5A64B0867CE2FA3DA3747331E4FF71F697A1C73B2271D979ACE6426F1F8E7771D8729DE4E9ECE3E87BDC2717306136FC1BEAF370DC550E70DF17DA832218D4A50
	3E89A08BE0B9409A00BC00A255E9AD872882E8G7083C481E6814C83D88C108D30CA653A742B1283D36812DE74C9A81341074DFE4E079CF5F2AE1E1F82474E3BAB0067AA5112EBF06FD17CCEAD3986F70F1F57602A20AFC363F47F2CB43DBFDDDBBA3DBF7DD5E96A9C61055249B9423A5254B9424B2513F3046FD01F348E68AC9A617940CB94D78E6D2A91FE97DA61CD6870E32677CE6F354FD755EB3A077E1BC0DBFA796C36CDAF1FCADBEA7958ED1365F35816DABE9F594C72E13893B674437D20579D46B81701
	3BE8601A681A083BA0053481835FC269C2FC13089B9AF11752B5C6F19DBA6E2289F7D147DD32E14C06A38FA8B1BEB7E823F1D912E5566E27E3248804F0039FB225D10A8AA63A078D9C524DF46DED9AEB6AC14FC2067E0D790D5A3421FF7B82386782237DC75ADEEA346F73DFAB9DC96C7968B55B0367A0BB48039EBC57C03F90E5B94F9AEA0B6873560C701CE70B976989AA72C8B8226AB537BDDE9E33F30DF19F78719B75A8A406D2D010D9D66C94E22D0AAC854775F92DFE3EFEBE4CE37502B97E696B85F56F61
	FBA267E6358276FD65D2A0793797D8D3B9BA4A69FA4183A9BEB9D44D57BA6AC59FFA48D83B5FFF04CC3CD71B3E925A5B9BBD3EE617435D6DF5F94E395DCE07275B616DEEE9ECECE8ECE0794B5CG1E896D41BDEA54F78366B8B8EB14797BF97AB3EBF4227FDFB64AF5715EG7FF67238019B2438DCE8970DF15CFD812E3365866E5FAE193F27B0FEBFC94755411826B14E0FAF8550770CCA2A849E45230C511FBB697219CF47E3AC0D0F717A6C4AG174FCD00DBG108D709440FAG16F3DCC6516B7C3C94782CB9
	8C9BFFEFG65D3F308672DAF860CEF98F1DEFB2938B4F56D45F3E96A5A4B2CCB4935976F045A4B7D1712EBAFAFEC5830817B19BC7FABB053FC087D7E7FF06DDF0001E57D06ECD4E324F482BFFE784D8FFE397219F63510ECEAA63F47EB07D70C518CD18C61D7F07DA72F27C8F1D40154428B78E6D2245247C6D59B17C5B73175F97067E02AA6069CBC778443F84D0DBB9D441FEC9DEC9254818DE347B1B7A90A600FF349F656AD8B117E03AC91BD96471F6496B8FAD00A14FC82746E207462AAF6E3159D7F30751D
	BDE7F21696924BA932C5D60450A9A188A9D1B3A95686E222BA204821F874E4BDDD48BA061244ABD2A1AC7716F1FCBC2C5D87914EA0FAA87E755925392527C09362B1F2100E44DB165D9891F28D88B158CA0CB5EB78E3E3A5365F687B525B5A3B7FF27A8D3F059468079CA61F92CBBC2A11CDFDC2D812476105E91F0ABD1BB420B6A6C454BEA596864AAA12A863DD4686762FBDD4BD776E17BB698609299A594DD889D38F760BCA7820232FCF0A08468240D7D8FE1731BC0E31A70D599E3F7535B32BB774029EB410
	42F0F5B57DDBC37F9E423FB594D3C3B1B5070039F9E44AFF497DF1FEE62ABCC04FD6CA8F0A5FFE8AA428BD791B076E7CF9DD5EF3F03AD3E4A37D21938821ACB12AE5A45CAB463E1CB5F757AB19EB3F02B20A222A44F15DADF10D3C490A10955A00D82101F72ED07AAA747F44E1EC0747EF26B1FB6D68615A42E76FAA341022E6320965B02059180800E653BC251994C28EC31592ED95F3DC4CAB0A68C948A4D5C5EC51E41DC3345510BC36DE21C3CB695F32997391E7ED27F3ED4671F7B26E2207AC52A93D87E05B
	7FBFCA774575FB7B01BF1BEBC679B7EECEDB9F4F7C044529451FA4570C3EFA9CE94F81BC79145FB91E2E7BA3C9FFEF760685D95684F9489189619D5AB92E09091A6AED47F1FD150F51BBD95F9D47116EC1531C7F85D0CB87886877A5A83393GG38B5GGD0CB818294G94G88G88GD7F161AC6877A5A83393GG38B5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6D93GGGG
**end of data**/
}
}