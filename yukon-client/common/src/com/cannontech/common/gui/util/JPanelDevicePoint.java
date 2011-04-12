package com.cannontech.common.gui.util;

import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (5/14/2002 3:35:52 PM)
 * @author: 
 */
public class JPanelDevicePoint extends javax.swing.JPanel implements java.awt.event.ActionListener 
{
	public static String PROPERTY_PAO_UPDATE = "UpdatePAOProperty";
	public static String PROPERTY_POINT_UPDATE = "UpdatePointProperty";
	
	private int[] pointTypeFilter = null;
	//represents
	//  [Category][Class][Type]
	private int[][][] paoFilter = null;
	private javax.swing.JComboBox ivjJComboBoxDevice = null;
	private javax.swing.JLabel ivjJLabelDevice = null;
	private javax.swing.JComboBox ivjJComboBoxPoint = null;
	private javax.swing.JLabel ivjJLabelPoint = null;
	//a mutable lite point used for comparisons
	private static final com.cannontech.database.data.lite.LitePoint DUMMY_LITE_POINT = 
					new com.cannontech.database.data.lite.LitePoint(
						Integer.MIN_VALUE, 
						"**DUMMY**", 0, 0, 0, 0 );
/**
 * DevicePointJPanel constructor comment.
 */
public JPanelDevicePoint() {
	super();
	initialize();
}
/**
 * DevicePointJPanel constructor comment.
 */
public JPanelDevicePoint( boolean showPoints ) 
{
	this();
	setShowPoints( showPoints );
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJComboBoxDevice()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxPoint()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 12:00:09 PM)
 * @param list java.beans.PropertyChangeListener
 */
public void addComboBoxPropertyChangeListener(java.beans.PropertyChangeListener list) 
{
	addPropertyChangeListener( PROPERTY_PAO_UPDATE, list );
	addPropertyChangeListener( PROPERTY_POINT_UPDATE, list );
}
/**
 * connEtoC1:  (JComboBoxDevice.action.actionPerformed(java.awt.event.ActionEvent) --> DevicePointJPanel.jComboBoxDevice_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxDevice_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JComboBoxPoint.action.actionPerformed(java.awt.event.ActionEvent) --> DevicePointJPanel.jComboBoxPoint_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxPoint_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JComboBoxDevice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxDevice() {
	if (ivjJComboBoxDevice == null) {
		try {
			ivjJComboBoxDevice = new javax.swing.JComboBox();
			ivjJComboBoxDevice.setName("JComboBoxDevice");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxDevice;
}
/**
 * Return the JComboBoxPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxPoint() {
	if (ivjJComboBoxPoint == null) {
		try {
			ivjJComboBoxPoint = new javax.swing.JComboBox();
			ivjJComboBoxPoint.setName("JComboBoxPoint");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxPoint;
}
/**
 * Return the JLabelDevice property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDevice() {
	if (ivjJLabelDevice == null) {
		try {
			ivjJLabelDevice = new javax.swing.JLabel();
			ivjJLabelDevice.setName("JLabelDevice");
			ivjJLabelDevice.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDevice.setText("Device:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDevice;
}
/**
 * Return the JLabelPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPoint() {
	if (ivjJLabelPoint == null) {
		try {
			ivjJLabelPoint = new javax.swing.JLabel();
			ivjJLabelPoint.setName("JLabelPoint");
			ivjJLabelPoint.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPoint.setText("Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPoint;
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 9:20:24 AM)
 * @return com.cannontech.database.data.lite.LiteYukonPAObject
 */
public com.cannontech.database.data.lite.LiteYukonPAObject getSelectedDevice() 
{
	//return null if there is no device selected
	return (com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxDevice().getSelectedItem();
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 9:20:24 AM)
 * @return com.cannontech.database.data.lite.LiteYukonPAObject
 *   will return null if no point is available or if the point combo is not visible
 */
public com.cannontech.database.data.lite.LitePoint getSelectedPoint() 
{
	if( getJComboBoxPoint().isEnabled() && getJComboBoxPoint().isVisible() )
		return (com.cannontech.database.data.lite.LitePoint)getJComboBoxPoint().getSelectedItem();
	else
		return null;
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
	getJComboBoxDevice().addActionListener(this);
	getJComboBoxPoint().addActionListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2002 12:22:36 PM)
 */
private void initDeviceComboBox()
{
	getJComboBoxDevice().removeAllItems();

	//add all of our devices to the ComboBox
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List deviceList = cache.getAllDevices();
		
		if( deviceList.size() > 0 )
		{
			for( int i = 0; i < deviceList.size(); i++ )
			{
				com.cannontech.database.data.lite.LiteYukonPAObject pao = 
						(com.cannontech.database.data.lite.LiteYukonPAObject)deviceList.get(i);

				if( isPAOValid(pao) )
					getJComboBoxDevice().addItem( pao );
			}

		}	
	}

	//fire the point combo box event to fill the point combo box
	if( getJComboBoxDevice().getModel().getSize() > 0 )
		getJComboBoxDevice().setSelectedIndex(0);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DevicePointJPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(231, 54);

		java.awt.GridBagConstraints constraintsJLabelDevice = new java.awt.GridBagConstraints();
		constraintsJLabelDevice.gridx = 1; constraintsJLabelDevice.gridy = 1;
		constraintsJLabelDevice.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelDevice.ipadx = 6;
		constraintsJLabelDevice.ipady = -2;
		constraintsJLabelDevice.insets = new java.awt.Insets(5, 2, 5, 0);
		add(getJLabelDevice(), constraintsJLabelDevice);

		java.awt.GridBagConstraints constraintsJComboBoxDevice = new java.awt.GridBagConstraints();
		constraintsJComboBoxDevice.gridx = 2; constraintsJComboBoxDevice.gridy = 1;
		constraintsJComboBoxDevice.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxDevice.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxDevice.weightx = 1.0;
		constraintsJComboBoxDevice.ipadx = 44;
		constraintsJComboBoxDevice.insets = new java.awt.Insets(3, 1, 1, 5);
		add(getJComboBoxDevice(), constraintsJComboBoxDevice);

		java.awt.GridBagConstraints constraintsJLabelPoint = new java.awt.GridBagConstraints();
		constraintsJLabelPoint.gridx = 1; constraintsJLabelPoint.gridy = 2;
		constraintsJLabelPoint.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelPoint.ipadx = 10;
		constraintsJLabelPoint.ipady = -2;
		constraintsJLabelPoint.insets = new java.awt.Insets(3, 2, 7, 7);
		add(getJLabelPoint(), constraintsJLabelPoint);

		java.awt.GridBagConstraints constraintsJComboBoxPoint = new java.awt.GridBagConstraints();
		constraintsJComboBoxPoint.gridx = 2; constraintsJComboBoxPoint.gridy = 2;
		constraintsJComboBoxPoint.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxPoint.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxPoint.weightx = 1.0;
		constraintsJComboBoxPoint.ipadx = 44;
		constraintsJComboBoxPoint.insets = new java.awt.Insets(1, 1, 3, 5);
		add(getJComboBoxPoint(), constraintsJComboBoxPoint);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initDeviceComboBox();

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 9:25:15 AM)
 * @return boolean
 * @param point com.cannontech.database.data.lite.LiteYukonPAO
 */
private boolean isPAOValid(com.cannontech.database.data.lite.LiteYukonPAObject pao)
{
	//if not set, we want all the PAO's
	if( paoFilter == null )
		return true;
	else
	{
		//Watch out now!!
		for( int i = 0; i < paoFilter.length; i++ )
			if( paoFilter[i][0][0] == pao.getPaoType().getPaoCategory().getCategoryId() )
				if( paoFilter[i][0][1] == pao.getPaoType().getPaoClass().getPaoClassId() )
					if( paoFilter[i][0][2] == pao.getPaoType().getDeviceTypeId() )
						return true;
	}			

	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 9:25:15 AM)
 * @return boolean
 * @param point com.cannontech.database.data.lite.LitePoint
 */
private boolean isPointValid(com.cannontech.database.data.lite.LitePoint point) 
{
	if( getJComboBoxPoint().isVisible() )
	{
		//if not set, we want all the points
		if( pointTypeFilter == null )
			return true;
		else
		{
			for( int i = 0; i < pointTypeFilter.length; i++ )
				if( pointTypeFilter[i] == point.getPointType() )
					return true;
		}			
	}

	return false;
}
/**
 * Comment
 */
public void jComboBoxDevice_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxDevice().getSelectedItem() != null )
	{
		getJComboBoxPoint().removeAllItems();

		com.cannontech.database.data.lite.LiteYukonPAObject selectedDevice = 
				(com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxDevice().getSelectedItem();
		
        List<LitePoint> devicePoints = DaoFactory.getPointDao().getLitePointsByPaObjectId(selectedDevice.getYukonID());
        for (LitePoint point : devicePoints) {
            getJComboBoxPoint().addItem(point);
        }
   		
		//disable the point combo if there are no points found
		getJComboBoxPoint().setEnabled( getJComboBoxPoint().getModel().getSize() > 0 );

		if( !getJComboBoxPoint().isEnabled() )
			getJComboBoxPoint().addItem( "(No Points Found)" );	
	}

	//may as well use and existing messaging scheme
	firePropertyChange( 
		PROPERTY_PAO_UPDATE, 0, 1 );
	
	return;
}
/**
 * Comment
 */
public void jComboBoxPoint_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	//may as well use and existing messaging scheme
	firePropertyChange(
		PROPERTY_POINT_UPDATE, 0, 1 );

	return;
}

/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 11:19:20 AM)
 * @param count int
 */
public void setDisplayedPAOs(int count) 
{
	getJComboBoxDevice().setMaximumRowCount( count );
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 11:19:20 AM)
 * @param count int
 */
public void setDisplayedPoints(int count) 
{
	getJComboBoxPoint().setMaximumRowCount( count );
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 9:07:46 AM)
 * @param paoTypes int[]
 */
 
/*		int[][][] paoTypeFilter = 
		{ 
			{ {4,6,9} },  // 4(Category), 6(PAOClass), 9(Type)
			{ {2,4,6} },
			{ {2,0,4} },
			{ {8,5,9} },
			{ {3,8,2} }   // 3(Category), 8(PAOClass), 2(Type)
		};
*/
public void setPAOFilter(int[][][] paoFilter_) 
{
	//ensure our array has the correct field lengths
	if( paoFilter_ != null
		 && paoFilter_.length >= 1 
		 && paoFilter_[0].length == 1
		 && paoFilter_[0][0].length == 3 )
	{
		paoFilter = paoFilter_;
	}
	else
		paoFilter = null;

	initDeviceComboBox();
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 9:07:46 AM)
 * @param pointTypes int[]
 */
public void setPointTypeFilter(int[] pointTypes) 
{
	pointTypeFilter = pointTypes;
	initDeviceComboBox();
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 12:24:03 PM)
 * @param paoID int
 */
public void setSelectedLitePAO(int paoID) 
{
	for( int i = 0; i < getJComboBoxDevice().getModel().getSize(); i++ )
		if( ((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxDevice().getItemAt(i)).getYukonID() == paoID )
		{
			getJComboBoxDevice().setSelectedIndex(i);
			break;
		}

}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 12:24:03 PM)
 * @param paoID int
 */
public void setSelectedLitePoint(int pointID) 
{
	for( int i = 0; i < getJComboBoxPoint().getModel().getSize(); i++ )
		if( getJComboBoxPoint().getItemAt(i) instanceof com.cannontech.database.data.lite.LitePoint
			 && ((com.cannontech.database.data.lite.LitePoint)getJComboBoxPoint().getItemAt(i)).getPointID() == pointID )
		{
			getJComboBoxPoint().setSelectedIndex(i);
			break;
		}

}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 9:08:31 AM)
 * @param value boolean
 */
public void setShowPoints(boolean value) 
{
	getJLabelPoint().setVisible(value);
	getJComboBoxPoint().setVisible(value);
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G86F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DF0935719A600E39A12AB935252541DF3921AF1F33EC22F640E39631A54173B8B8D1D108E19B2F73E9CEDE94BDCDC463DE426846864C932E491A0183F06DCA0F1E228CDCF16ED70A5C4987F8A5B42CE6A02D01D44855656DA163D524A3BABDB66773E6F3DFD2B35AC59A113FA66736E7B3E775E773D6FFFF7C58E3C15E737D88F90E231937D6FBD8BA17E63046C4BFA6C990E4B928B9D447477B640
	DE722B32D238FE91E85B6FAEF414936BB5A70C1742F87D02C247EBF03F1F14DD2BBE8AB799789020ED9C7D6F684CFC325E67FC4A6858655A8557D7817281075797C5646FF59D5271EBF4BC870B1510A707F04EE557E19DF78A463DG73G168C31794B613A03F21E6FB9254F6BDF14C7DE7CDE15F5B76EE35A13C3F9082DED502F95E41925A8043CF2281C38C60271D2GBC3C0DEC3ED88D57465E51D053209CF18FEAE1F7A89EF647353064711C6B9522226495474303E22F9C0EEAFD433857DA17132E057DE13C
	1162DEEB427DF361FE85409A837F9C45D7078B9DADGFDE1FE26B3D50D3A4CFD5716116DBF0C5A76E4B8D3AC4C4E32A41CBC536278563257297E1F72201E5781ED8B402E11C2C79DC0AFC08C608DEA77FFFD7FF7F0ED9D505A65C8C40E3646E2EDD2D079C08E0A925CFBBCC051F16745C1D9899242ECFB0F4B15C61E19002D4B3D6DF5CCA779A7391D8B4EBFCC0A3E5DEDADCB2313227CBAEB4E8853C55EC8D2A70C773399F9BFBEB2558F27727EA3B173AE4E16ED76B43CD7BCFB5C3ACE5FE713097721D13475DD
	5456CE3837687E417077D1FC3F894FFC72884563D8825A5AD16E9BFD2FF2D9FACA6FA76575BDE975302430555AA0B199FA24D4D94EB7635E890971349B327C0962971A70CC160DD4969C67812DB4D268E07E352E91E3F4984673GF281D6G2C83A881A80DF29FAB7754FECA9F6B95942DAD9E160222C2585AF51E5F40D5CCE80A600B2A1A909D948302929686A4D1A57A1C7F70208E86262C7BB1757DC16051A9C6C5C55042F294F697A222A6AAAA1B733CE7AF4ED1C4CA7507A3A2B00844885F77854FFE543F20
	EA97E3C1C19313B398FD1B87EDB2AE84E0C758G6E19DEACD4DFC7617C0DGBD069E46CF63FE5D228209436369116551F84C0FA7A139D154F3B14D9DD760BE9165BA7EF59362B640F80B314FDF1F5EE75A479B9EC47185E512475822A64C433BE46EB31B7B384FF8F7FF1D381EBD1756E790DCB273952F1C9ABBDF25FB22DCECDC5954F71BF1BC936C1227469F635178397230250C19FEC7DB50D692704F814817390EB71D2EE0BE5BA628A277525FAB60E0E2B029F1A6E7DF33D357274D10FD10629E077DB649DC
	E77FC54FE3C9A34B67810C072D39CF5F5F7EE76041F473DCA356A3F1DC0CEADEB1A6C603F0236A341FB6224F6971BAA49BEB1E27789844F7BB8414EA60FF5908B1A8E81A929E08EB6285D1534251D0127E9265254687E2495440F911B3F8C6F982625B18EFBF0331B1A8C723A2B51231C42717515894A5B182C2374B712846703B29FF0F0A13FAEE585B048D4E38A045B14F30FEE03930934F2004A3E0FACCD564CA8C6356D6F3482423F240E1DF53G572E980F4109CA9E03C72FBEC09E3FFC4C1A2E06AC793A57
	1A9FE33130AA160CC1A61335B93D6CCE6AEBBADEE5323EE3C53D1A5A93562F98FE3F349577AA829EA5G4EB16E77731A114EF30B6E706D32A4B1F59AFA9EEBA9C73B08E3F191D2B2A631EEC3F70916FDA934002EC7C64FEA59C9E3AE197FB60E51DA584CFB3E8A981F82689BE33ADCB246F44166FD54426755A920E3A5B9EFD8E173188EDEB80EF6217D5B08B0AEA45C6A847896EF5DD83F9629F16A73D7A83CAF3B338EF5D38C63D23550F1D865B6FCE6B76F4B960F66118D8F7DCF46DE33C1E5F27429C99BD64C
	65FD5966B6643984E879AA577B3696F3EDE8D5D5F9B0CC4B122E33C7DA4DB6E638E52D2F40F5A82C281A8F02B041757BE8AB4A0BF5CBC76BF57CBB4D3C0E2F823EAF828CEB085F5AFE40A43BCBE375478FD79B5D6FD84B890CD16AGEEF6699515A1D9090841EA542CDB18505CB49F38DBA979AC5E9F67F628FC33892EA3D07D8764B6B9411C72F2EB4AC6BD19F7BAFDBC40FB3187773768F22E79667D247156FEEB3A5EB8E74F916BF20D69FE3D163441753FF1580AAFEDABE37A3823056B63E83C50519F677AF8
	7AD6BAFDAC0E73670207CE1FB11F070657ED9FC74F57CACDEACF1A9FCF5642D57169FDA96B5536D19FDD9BE73D5A8E836F2C273EEB4233335ECD676358B9DE681046F9FFF1430BB132964645G2593606FG0DG7D935CB74F341DA43374EF3EA834D5D1C13AB0A5D173BE2A4D46F22BBE7ED51B1D67D6C38FCFB4276AA1B1B1756C0BA7527588BF18366E4D14714A14F54C06A1690D743901DB15307CD06F654FF3EBA7F89E3851E14E832B761C306E4C10871C89463BBA1174C11771FCD655C17BE7204D834885
	D889D08430856070A4577F1C76B333691F7A15DFEE87DC97645576547AE768AB2765336979CB5356256A39F1F26ABA26BF87617A73831D75398BE84EDD8863BC83672FC55C93B0AE1A647D12259EEB8C39DA68B88450G50F31553DE2534612B2CCE64DCE5F500516E2DCB6D9BF1BD6EB3DBFF5310E6CE4F55F47AA8184643CEBEDDDFD7168637A3E54C6C1E6D4D5CE75D6DCD5FE75D6BCD5FE7FD453B1761E18F73D39627BF601D398F7B06B7DD9FF6041EF975EF76503DD5D5D429A3FA85CD50E9BF578E12B47D
	580770A0266B8BEADF738EFD4ED9CDEE2FB7F47C2EF726F78579E9EC45626A1B1D183FD781FDBDC0890073DA21237A9A0F27663333663333099972D9FDC7E63B1C6AC8EF17268E6C95D4B92E8C0A293ABF5BF14820A5A357270991D4931B73A1DD2F89CAC81CE63B0BF4BD23E5DAFF29A313FD198D9EEBB15B6004E103B14BCCB6683AB65D86EC3F3C9A6C1FA5E1D20EEB04BCF98DE37C6F0CBE399F469AC0AE4072EB661E6DDB9318CB0760C19D443B90938749526B38F634162FAD0771D1GAFC090E05EF55E27
	BC6A409AF72EC79860AF66987E7B8E345739297D07AE674447BB0DB534106B783F7878D5739AC6A22CBFE82BB777874557D9FFD0FE03778FADD56968C7EF70DC770117673AD3B738CF7EEDE7E61F7A474E74BE75CF1D69E3FDF5674C317ECCE7A6DFE07265D838FCB9A09F6B9F7E33063FA3DD8F38721B3C972A3F02B54C28235870F8BC1DCAB858A604FA2875D99D28F273E7642E1B3C4F3E6846BD33E03C84E079CDDEDF933F65753560617BC9CFD6F75ABEFB037F0475491B4CF70AEFA66BEBBE54D746F3D1AD
	67D9FD2B50F16596629E2BB55B86ED9D81F083B07AAF2750D1CF472750AB6B4CF41477E90B19FEF18A5DEEDA4F4E7654B1FE366DCF6593695177B3BEC3645FE2E7DA23DFFB83156468973615694FB6C3991EED28A73373CF56BB75B8AE4D02E7A40A1B286238C64065E7A1AE68754E90633C2EF150E0CD8EC025CE4B057B0F74B3307D7E52B25BFE66BDCBD276AB1D135C0F65201FCE6B11BFCD0D5DCBDE780C6BDC6E5BDD47FA6F2ADA746772B92C77369878BA0ABFEC4233187A444DE32A9AE88B67701EFC318F
	EDDB8263DDF7C05EGE8846887C85C4173C5282B0E4CD2437CF22C87920AE4CA81FAAFD77B595E41FF370C7B6E0A3FFF102C4CFE4912296FDDF8877359827D3A49DFE92A4FD55DF83EA220ED81B8BC97FABB00AE00ABF3797916F53958794E8643DA2B268903437449179FAE9096A712E8AC946CECB76B4DE75366B27EB97AB5F76E74FA4556DD2FB22FDB2E4FFBC23F2ECF336EADAAA7217DAD7BABE91E0E035CB4973F15DDBECF8CBD7EE8975763613F7AAA717E70A5CBE586BD564DE3FC3366A5735B26116DE5
	69F8B3BE2BAF9063CCB535497BF7CD77027B73FC2F1ADE1FD77F2CBC180FFC4D1739E6AE75252F190F7852574C653E197B5F153E4C6FA19F73195FC3B29F7F0A9F6B40CA305BBA00ADG3B6697BAAA66F37F7E605CBB047AF79A5FBE9B8CE19F08DFBBB44E3F2EBB73D95F6B1E7D9BC9CBB73EAB9A10B5CD0E607E9C5F510D76526498C5B2DCF7B76AC8890706B5B6D5477F219B7DCD9207A81A758A77519A5388676A194F7B1B2DA768F78C98478C5C65133C2738EE606E2535E88D0C0B8C5C7768B710929813EC
	0E2B2D61EBF7E5739E6169CF4C5FFD4B6FFF5B4AF3EB2A2F74E73358994ECE469033D1E7G6BD4AE60D78294813C88603C936C8450G5085508F2081AC844883D88170A440FA00E2G1243ED3A74BC567A00B022E7E3CCDD32C22D8AC83DA92206D5845ACE72E7AFFFE65BFE5BBF3BEDD779693BC13009FFB226777B5F75E37FAD8F8D292261C37F62AFB7F0BE35D7D643781C407A5D02FAFC7FBDA4C3471803BAAF24F6BDG7795B93C3F5EFB9267E8A08C4BB5159862FA4D9FBFB9D34D3725C00E2966234D9683
	8F6C03FE7D724BAFB3DE0FD4CD1567F16AABF9F0DFE04873A357D4F91E7FC3D21E0E1A5964314FA0CF9170B8284B43FCE5894D5FCC0FFD970FE532B3C46FB9E847FEBFB0828FE1FEA1E42C79A4105946830159EDBC9AC89F3FE3016971BB9EC89F3F57836678555FAD85D08FA1704DBEE3DE36017B48405DC577C45C9F43C1ED5840AF22F2A13ECBC4069C7FE5AA578445756938DC936EA36E7B8174D3B872302C70358F87286D43122467ECBAA79C8537B574310C4A980E89A639D798B8141B79447B54C7193FEC
	BE01B9B7AACED4986FE6BB40412A5F890685C98E3D632A28F3D554B79C67F5BAEF421CE3CE3D72DE5AEFB6343F49E139E53BFEDDF5E7254EFF990A225FFF521F1465BB2ECFAB8BDB3B60CF381679623B17500E8D8B8A9D3D8BF86E3959773FB3641EDEC50E090AB6493074EB0C2E335317D0DE236EB112CEEB3BF4006A9D5204A0052F524F9197E0E2B444FEB3345CFE908CD5708BE2D7953247B8070F9AEA4378C90F6DA78F592C2454C79C4CF1FD24943D4FC76C54B9FD44860E1B0869CA65BFBA3029E364A661
	2A62D80BC64A58133BC7A38E7ABC6E81363B27286F1F9F783F1F2DF9AE205A0843C7DE476CF1C1D42870E290DFCA69DF6ABB480E98EEF08170A013CAAFF6D51B14449EF2E068BCFE39D2B5B1584A5522EA64A0FD21DF0BBF7E296D9254E18D4373F48FA90D62EF8A48BEC61604E828166968B4BBD86B95FA9454B281F977D3F9F19737314B7EFF58F36C6DF39BD75A0835033CA249C230C39884BB79488EEDD89155E1D98AA6E24DADF4A367849AFFA75513C25EB00E0F07F57BA10998C20FA63E36F077265D9DD6
	F822D6C8AD1D094952ED4C88368F8B8A3092951F063F1110152386ED4E1B5AEF3F5F79DA3F15D874C322D54A92B1507E109089CB13F063254ECA6D35D383E995A12A62778F102CA2C5B2CEB298386FDDDDF75FB736FECC99A417EA64AD26CAD8DA9B9265C8ED6B50D0B8AA9A9B00DEE17BA34676B8471DB2E7DFE25141F34F3EBCGFE60A536C8DD9D7DDFCF7F1F407F7594D3CFB175A7C039A5E4463FD4FA021F19BA0F617EE8FEF787D8D07B37DF2CFE705F9BCB0A61F49D44CE3FF500914A1333BE0CC786C4A5
	3B603EA3BB97EF388BED94C3D7C9603EFB92C688206BDBF526F60A35CF73AA446E186E9E085EED9889C765F46ABE7A7F60ED9F99D73DC6573EEEC87AD6D252B2DD52B2BDF0D98EBDFE4B1CC3374CBF14B107365C43F2277F1E54BEED33934F74C540975C4373DD133306E42E4197A202A4E902B45A9A8DE2BDEE1B5444E4AF7F029377D7799C1D48E8DB1DA87708E94D7F83D0CB87887B8674148890GGFCA9GGD0CB818294G94G88G88G86F954AC7B8674148890GGFCA9GG8CGGGGGGGGG
	GGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC290GGGG
**end of data**/
}
}
