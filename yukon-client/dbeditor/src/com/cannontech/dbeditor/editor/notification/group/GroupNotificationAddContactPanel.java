package com.cannontech.dbeditor.editor.notification.group;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.db.notification.NotificationDestination;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.data.lite.LiteComparators;
import java.util.Collections;

/**
 * Insert the type's description here.
 * Creation date: (11/20/00 4:13:29 PM)
 * @author: 
 */
public class GroupNotificationAddContactPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private com.cannontech.common.gui.util.AddRemovePanel ivjAddRemovePanel = null;
/**
 * GroupNotificationAddLocationPanel constructor comment.
 */
public GroupNotificationAddContactPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> GroupNotificationAddLocationPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> GroupNotificationAddLocationPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the addRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getAddRemovePanel() {
	if (ivjAddRemovePanel == null) {
		try {
			ivjAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjAddRemovePanel.setName("AddRemovePanel");
			// user code begin {1}
			
			ivjAddRemovePanel.leftListLabelSetText("Available Notifications");
			ivjAddRemovePanel.rightListLabelSetText("Assigned Notifications");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val)
{
	com.cannontech.database.data.notification.GroupNotification gn = (com.cannontech.database.data.notification.GroupNotification)val;
	
	java.util.Vector destinationVector = new java.util.Vector( getAddRemovePanel().rightListGetModel().getSize() );
	Integer locationID = null;
	
	for( int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		locationID = new Integer( 
				((LiteContactNotification)getAddRemovePanel().rightListGetModel().getElementAt(i)).getContactNotifID() );

		com.cannontech.database.db.notification.NotificationDestination dest = 
			new com.cannontech.database.db.notification.NotificationDestination
			(
				new Integer(i+1),
				gn.getNotificationGroup().getNotificationGroupID(),
				locationID
			);
		
		destinationVector.addElement(dest);
	}

	gn.setDestinationVector(destinationVector);
	
	return val;
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
	getAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GroupNotificationAddLocationPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(418, 338);

		java.awt.GridBagConstraints constraintsAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsAddRemovePanel.gridx = 1; constraintsAddRemovePanel.gridy = 1;
		constraintsAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddRemovePanel.weightx = 1.0;
		constraintsAddRemovePanel.weighty = 1.0;
		constraintsAddRemovePanel.ipadx = 4;
		constraintsAddRemovePanel.ipady = 4;
		constraintsAddRemovePanel.insets = new java.awt.Insets(17, 3, 32, 1);
		add(getAddRemovePanel(), constraintsAddRemovePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
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
		GroupNotificationAddContactPanel aGroupNotificationAddLocationPanel;
		aGroupNotificationAddLocationPanel = new GroupNotificationAddContactPanel();
		frame.setContentPane(aGroupNotificationAddLocationPanel);
		frame.setSize(aGroupNotificationAddLocationPanel.getSize());
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
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanel()) 
		connEtoC2(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object val) 
{
	com.cannontech.database.data.notification.GroupNotification gn = (com.cannontech.database.data.notification.GroupNotification)val;
	java.util.Vector destinationVector = gn.getDestinationVector();

	java.util.Vector assignedLocations = new java.util.Vector();
	java.util.Vector availableLocations = new java.util.Vector();
	
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		//locations
		java.util.List cntctNotifs = ContactFuncs.getAllContactNotifications();
		Collections.sort( cntctNotifs, LiteComparators.liteStringComparator );

		for(int i = 0; i < destinationVector.size(); i++)
		{
			for( int j = 0; j < cntctNotifs.size(); j++ )
			{
				if( ((LiteContactNotification)cntctNotifs.get(j)).getContactNotifID() ==
					((NotificationDestination)destinationVector.elementAt(i)).getRecipientID().intValue() )
				{
					assignedLocations.addElement( cntctNotifs.get(j) );
					break;
				}
			}
		}
			
		boolean alreadyAssigned = false;
		for(int i = 0; i < cntctNotifs.size(); i++)
		{
			alreadyAssigned = false;
			LiteContactNotification possibleNotif = 
					(LiteContactNotification)cntctNotifs.get(i);

			if( possibleNotif.getContactNotifID() == CtiUtilities.NONE_ID )
				continue;
				
			for(int j = 0; j < assignedLocations.size(); j++)
			{
				if( possibleNotif.getContactNotifID() == 
					((LiteContactNotification)assignedLocations.elementAt(j)).getContactNotifID() )
				{
					alreadyAssigned = true;
				}
			}
			
			if( !alreadyAssigned && !availableLocations.contains(cntctNotifs.get(i)) )				
			{
				availableLocations.addElement( cntctNotifs.get(i) );
			}
			
		}
	}
	
	Collections.sort( assignedLocations, LiteComparators.liteStringComparator );
	Collections.sort( availableLocations, LiteComparators.liteStringComparator );	
	getAddRemovePanel().rightListSetListData( assignedLocations );
	getAddRemovePanel().leftListSetListData( availableLocations );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE9F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D9FDEC9C4595F740C1E920A828069A77839392E49597C2EB2A8825A8BD39D58A118AD5A8E9EBD5E909D47FD12A340A5AG2E985F796E1292FCF736C3CDC90AE99CF0D0F07355B6B8F69A47CE3149B59C0EE3CCB1A46B3B35EF6D3D5B736E1E6F7C913B6F4D6C6C2D4FF7F69311131EE6673D19F973FE735E1B37FB246E35C2E7253D0E10CAA7B1FE7F2CA42428011021EF2D5B4AF9B9224DC5AC3F5701
	FCA4642A02F6854882CBEDAEAF599E58897DD5505F16EBF37D891EEB493301532F41C396BD75A0DB7E490F3F3B301E26845763267D5B2BF7C3DB8DD484BCEC370B287F6B557586FF234167D4E9A7E45B900E59D03D57601D00FEG2898E87D909B3F995ADDD4735216934638015B8A4976C75F343F046BD85664F4E2184D9DB05A8672C0E5D598F57D066E93676401ECA3909A6FA09FB48620ED8D0C8E1F8AAA116620900DAAD1DD8C061B451024ABEAF3D45125A1A9A8681292ED9ED615F82C2565DDEC4EDB846D
	2110DFE10F81A1AA4AB9D467BB1E1DD487A1DD505FADA16F6613380FD2681781EDB779BD2D488F40F398A8C76236373D59EA5856BF75G99F8AA6A70E431BDDFE2B62F17D23667451FF33FC24FA91C447328GD943084DF59C289FA889148F74AA758FA57196346D03FA07920900D1315899B9247EC78909B2BC37340044607508C1C58D9142FC60775507B26CE7A1E2731C1E2B1D47B0A9BA467D213867DED2764DF3F6F786CC4A0A8E5B570CB0ACB60C24B0E13ACFE555DDBEB257DF676A7E10D8F57BEFD29C4E
	8C3ACB1EBCE82FB056710E26F41F9E4533FE017A40E1F82EB47C0371F7D19F483570196F5EC647E3BF8FE4BBC639EF74D7733D782B8A087708BFA38E7936F67B004C76204B69FB799E55D9A8B31DCEF3AFCFD0FE09054F7652CF79585FG32C3911B0B795758A446728A68978315821581D5G55C7E1DC147B585A445F7ECF9F8B882AFEA6AE49A1D1A5EC6ED872E5E845242E8A5DD1CD9722C131CFD0A5E1D096B5E20C19CCA28603F3664DD25F8F020EF74528289AE12A8A91D197D50D0DD916746198D52452DE
	A9A20202C80C70F5D7A4EB211585CD3F988B893A189A416479C9BC13F1218FD604856019617255A9444B8F760700F493075A89DC6F1C28C236E8E971AB4AE8BC560B161052A862FC11660EE570DC98659897CCA3EF2782B12C70F55E1B28312C53A985F17B02BAE146D8AB666B638A7719DF77F31F69FCE9B5B97464FB99FD86A924B0DF19D652E347C35744FD317E97CF769BBA2EA6162FAED72C21BA9C57E17DEA732CF64E60D92D82BB5683EDB1B1DEBA5140FC760C20095CCB5FD7414144D08AF1364F0D2DBB
	8DBC9D665E1F223C9DF8EEB10E59272D7D16F1578733C4AB474CF99DF4B0EC26A671BE924745285EA94644E8889EB4C3F643A47A1C912FC30AB9E7996547A03EBBA020B413FF6BA44620206B2AB49857458B222ECB5161147C8E2ACB0B8F46D229016B7A4AA45A28A4A03E4D71ABA7B1B602CAB4AA52C3B2279872D5B4B6C5D90C4026BB14F8D4E77C2FCD21FF0F0A93C6EE38FF8A8B21F1C10EE31EE1F5439A61C534C115A2F074182AC8F18C63D67654DBB06A829E56BFB9506645F88CA6767398EC1C3C1B2C3F
	F4401E698E49DF5DE95F9EE3B135F3AC9503ECCFF7CDE75E7BEA1AA38C3E46763ADC465AED5122450C0376D9DC3B89F43682750E71B8F87D8A4AF92EB18220C311E5862F09FB750C974FC99C0B0B1022B1291DB3315CB3D313A66BB3F0E572FDB3082BEC4907FBD5FAB73671DAD1067D6482950DB1ECAB8CEC58389F1A6342AA9B17234E9D47B0F1DF427BBFD35DB79C171A633A0459D45041F1E3F15DD2537D0866E5DCE3134AEB37EDB50857EE4D66EA84EA5578B9BF71922F5D72C68B4916FB7E1C35EE6D5558
	5E12DA6A1C9BEE64355B4D84F52E8559860D1FC57E0C757EE857B4A5A85123B4F0FCF7566A870C671F7DA334C3122A695D9028C90EF94FAC6E976FB603ED5C75C75B78DDDF86FA6BF4385B290F2F2E357A38DFE7F7D4AE3479C08E3A661DEDE7B116C3219EB1220C0B9441E6681E096BBA389EF518CB82ED8222BA242891B154B4A20C8B947B663318D05E9F9C814FBAE81ED969419A36FF111E8A3B081B8E76715AECE56DE9E3CF9B6FAFA02D333576CC3572B24FBE7B9A5DF84750D3F8CFFF43657855274F3B19
	6D4F1B36B746ED2E01B837EB70D457FC3BD47AFC8D2631BC7D53F935E8DEBCF5179645675F2B2CC68B55629E5745536B48A865EF0C2757113F2B61F5E49948CE0F733A62009D4FF395745783ED865A8664CD58DC07935C5FFEB2FB0CACD237F5C37C28D1C13EB0A7C1737A096A051CEA7473A836B42792FEDF1DE8CB47411F18EBFBB82D4F667DFB5E3C66347EADE97318BF75D5AD1E835A671CB44B8165357C3DEED50267004DAD08ED997437A3AF49313D51017928917A27017A01F420FC2092204DC90E6FDFAA
	5B964317BACE2F52813CAE480F9D69771A09C76529EB4351B3EFDEBA0EBB12F36731DA7537B4A7FAA7A07F81B15E37FD486B027E6589DE730C54623D1AEB76F5569F637D0A1A7EB4FD5546784F2EA65AD31BC947E7D5033133EC36E72F097EEE4FDC931D34E72E095A6DBE4607B52CEFC4DC5EEDDF38E67A401E29E65AC7ED7E7E4B9E3A3626099AF5AE3C708C59AF75BDA4C36DF49EDE1A8C3CEA0952364B98F3D6D7BA0E1898BFD33B50FBFDE10673E4F94BCF5FF3CBA6D85E2AB47997A9FF3D054F72594FDA78
	B71D0DA0BBBC49E369730E6C7867B9B263DF60403BD9D362EAD0CC4778EEC73DA9CBC5DD37AEC6908E23E62473F5C19D96671D5103F4BE13E51B7F30A35BB9B22CCF1C32E2FD54646F72E2EDAA8B93CADC0707A94425414B6F67BC402498E893D07924357EF9B001F556903C84031A8BB1B1C84A685CCDBEBE378B7A83C053C0DF86DAB7456F40DF4C20FCEEE66439202A0E47FD63B40F6B1E1344740D0FAD4FDBDBB24562E7C5CC479F2E2B8E6607DDDE6E07BA58DCBC4D3FAD9E390C391F5EAF747A9792BAFEBB
	14C2E704E1BFBDB1F61F2C7072F7462D537CBEF9042E39770A4DF59C48FF05571449B7F8CDD9FCEF8171671C4BD8D3EE69BDEA8FDDE1673D6CCA2A46A90ABF67E6BA1F73F11D25A02F38023C261ABA4B9AF8562B6D6C8EE0727F4E11233FE2ED16125FB2C70E7BBD6B304A9F19A3F7DA66B35B9EBB40ED2BF82C08488F3D1335DE6E1CE1B6050DB650371FB4AEFF5E3DE986753CDA57394019B95228906614G6DB17A9BE698563BCC9FBA6F3D366F1B8357B86F762B1E4772648A9F751959747CF9A765B74D2667
	4FD253EF0F03ACFF1657AC4B1D74DBB1746BF3A02F82F5810501F2F3F80E7D246AB0D924DE69D5E2FEC84232655AE2763D603B366F3E5FF1F37FA8F9F8A5D9FB533315AFE6710F7C9C6697EB0D363CF7BF65B37B7639513EF210D5AD31391A00CE83758249CB38FD2BE9ED08D5D7C8525BF5DD8806691B94B72ECF9293A9B6A6505768BCC25F5558EF5B3FB271F216607FAA97F1BB06DDBF5F4D6D5AFB5F17C867D34FD66E4FE2D76992E6CF5D8D2918AE9F29F0B3DC6F4010B4FC68AE4B73235E6B1FE71F3E2EBA
	5819FC41197DDEAFF0E63E578B1D196B2AFB1D8B57CD77BB33FFEBFA48E97D5644FCCA76E0EE7343390401EE81AA84AA39017BD3076B2F4C1FB278525950B0569578C5DB677A5FF0E73775ED77625F417F6146F77DC185DEC9A338BE6737D2DF511598E59AB5269BB1D22561304E0672EF88EE74DBD99C22EC662BBF78587ADF1C3760F5BB4FA969E776386123E596DFE5D8F9FC181F3DB75ADC070082C0E120E920BC20A220F5C00F83158311DC1B2B8E28892895488FF499C84F655896D463BD52A70C98D968AC
	7D670E228BCC634225G2B0204909B76691E6C98CFF99647B8173E770800CD6FC44C2863BE6741FACC999A52C473ACEF75F8CDDE3796D0B4896BBF06C59CBFE998639A3C87325981DE729E14906C33C12FB0EC4E596F4BEE43DB3E45EDB8664B6CA76F78667B49A9DFE6BF6974D97D44F8AF71E10C8C83767D663873A66FA313F7012E093C8F25109EB6799F51FDA13FCBC4051CFF096EABC1F97D86AFE461FDE470A49F1E83189CD6D4BEB76A43774DA1C916D9DF25E324A89C0B09C7026ED10A8916FDCF1BBC5C
	B77BAF3D5A5983ED71B344292944434B3B66EE3A581671E3AD0E276FF158C9D5B7F1B1F03BC995A254CD1C9440EE62GF013B19A982C7028248EE15C8987E72DF136E3E9FD46EF44C8F9CBD9FC95ADCD0F330FE97CEE85BE3909474AFD2E379770318B91C116F5C19EED0F06505F4ECC68E2AAA7DEF6637A9A9FE38819EC186610914B1C7F81D0CB878847133D64948CGG50A0GGD0CB818294G94G88G88GE9F954AC47133D64948CGG50A0GG8CGGGGGGGGGGGGGGGGGE2F5E9EC
	E4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCE8CGGGG
**end of data**/
}
}
