package com.cannontech.yc.gui;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.CommandFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.command.DeviceTypeCommand;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.command.Command;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.yukon.SystemRole;


/**
 * Insert the type's description here.
 * Creation date: (4/11/2002 3:51:40 PM)
 * @author: 
 */
public class DeviceTypeCommandSetupPanel extends javax.swing.JPanel implements com.cannontech.common.gui.dnd.DragAndDropListener, java.awt.event.ActionListener, javax.swing.event.ListSelectionListener {
	private javax.swing.JDialog dialog = null;
	private String dialogTitle = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JButton ivjOkButton = null;
	private javax.swing.JPanel ivjOkCancelButtonPanel = null;
	private int CANCEL = 0;
	private int OK = 1;
	private int buttonPushed = CANCEL;
	private String deviceType = null;
	private com.cannontech.common.gui.dnd.DragAndDropTable ivjDandDCommandTable = null;
	private javax.swing.JScrollPane ivjDandDCommandTableScrollPane = null;
	private javax.swing.JButton ivjAddCommandButton = null;
	private javax.swing.JButton ivjRemoveCommandButton = null;
	private javax.swing.JPanel ivjAddRemovePanel = null;
	private javax.swing.JPanel ivjDeviceTypeCommandSetupPanel = null;
	private javax.swing.JList ivjCategoryList = null;
	private javax.swing.JScrollPane ivjCategoryListScrollPane = null;
	
	private ClientConnection connToDispatch = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceTypeCommandSetupPanel() {
	super();
	initialize();
}
	/**
	 * AdvancedOptionsPanel constructor comment.
	 */
	public DeviceTypeCommandSetupPanel(String deviceType_) {
		super();
		initialize();
		setDeviceType(deviceType_);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/17/2002 11:49:08 AM)
	 * @param source java.awt.event.ActionEvent
	 */
	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		if( event.getSource() == getAddCommandButton())
		{
			CommandSetupPanel selectCreatePanel = new CommandSetupPanel();
			selectCreatePanel.setDialogTitle("Commands");
			//should return a Command value
			Object o = selectCreatePanel.showAdvancedOptions(this.dialog);
			if (o != null)
			{
				/** Contains Command value */
				if( o instanceof Command)
				{
					((Command)o).setCategory(getDeviceType());
					saveObject((Command)o, Transaction.INSERT, DBChangeMsg.CHANGE_TYPE_ADD );
					Command cmd = (Command)o;
					if( CommandCategory.isCommandCategory(getDeviceType()))
					{
//						The deviceType is actually a category, not a deviceType from YukonPaobject.paoType column
						ArrayList devTypes = CommandCategory.getAllTypesForCategory(getDeviceType());
						DeviceTypeCommand dbP = null;
						for (int i = 0; i < devTypes.size(); i++)
						{
							//Add to DeviceTypeCommand table, entries for all deviceTypes! yikes...I know
							dbP = new DeviceTypeCommand();
							dbP.getDeviceTypeCommand().setDeviceCommandID(com.cannontech.database.db.command.DeviceTypeCommand.getNextID(CtiUtilities.getDatabaseAlias()));
							dbP.getDeviceTypeCommand().setDeviceType((String)devTypes.get(i));
							dbP.getDeviceTypeCommand().setDisplayOrder(new Integer(20));//hey, default it, we're going to update it in a bit anyway right? 
							dbP.getDeviceTypeCommand().setVisibleFlag(new Character('Y'));
							dbP.setCommand(cmd);
							saveObject(dbP, Transaction.INSERT, DBChangeMsg.CHANGE_TYPE_ADD);
						}
						if( dbP  != null)
							((DeviceTypeCommandsTableModel)getDandDCommandTable().getModel()).addRowToEnd(dbP);						
					}
					else
					{
						//Add to DeviceTypeCommand table, entries for all deviceTypes! yikes...I know
						DeviceTypeCommand dbP = new DeviceTypeCommand();
						dbP.getDeviceTypeCommand().setDeviceCommandID(com.cannontech.database.db.command.DeviceTypeCommand.getNextID(CtiUtilities.getDatabaseAlias()));
						dbP.getDeviceTypeCommand().setDeviceType(getDeviceType());
						dbP.getDeviceTypeCommand().setDisplayOrder(new Integer(20));//hey, default it, we're going to update it in a bit anyway right? 
						dbP.getDeviceTypeCommand().setVisibleFlag(new Character('Y'));
						dbP.setCommand(cmd);
						saveObject(dbP, Transaction.INSERT, DBChangeMsg.CHANGE_TYPE_ADD);

						((DeviceTypeCommandsTableModel)getDandDCommandTable().getModel()).addRowToEnd(dbP);						
					}
				}
			}
		}				
		else if( event.getSource() == getRemoveCommandButton())
		{
			String message = "Are you sure you want to delete this Entry?";
			if( CommandCategory.isCommandCategory(getDeviceType()))
				message = "This entry will be deleted from all associated device types, do you want to continue?";
			
			javax.swing.JFrame popupFrame = new javax.swing.JFrame();
			popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(YukonCommander.COMMANDER_GIF));
			int response = javax.swing.JOptionPane.showConfirmDialog(popupFrame, message, "Verify Command Deletion", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
			if( response == javax.swing.JOptionPane.OK_OPTION )
			{
				// DELETE THE DEVICETYPECOMMAND FOR ALL DEVICETYPES PER THIS "CATEGORY"
				int rowToRemove = getDandDCommandTable().getSelectedRow();

				DeviceTypeCommand dbP = ((DeviceTypeCommandsTableModel)getDandDCommandTable().getModel()).getRow(rowToRemove);
				Command cmd = dbP.getCommand();
				saveObject(cmd, Transaction.DELETE, DBChangeMsg.CHANGE_TYPE_DELETE);			
				((DeviceTypeCommandsTableModel)getDandDCommandTable().getModel()).removeRow(rowToRemove);
				getDandDCommandTable().getSelectionModel().clearSelection();
			}
		}
		else if( event.getSource() == getOkButton())
		{
			saveChanges();
			setButtonPushed(OK);
			exit();
		}				
		else if( event.getSource() == getCancelButton())
		{
			setButtonPushed(CANCEL);
			exit();
		}
	}
	private void connect() 
	{
		String host = "127.0.0.1";
		int port = 1510;
		try
		{
			host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );
			port = Integer.parseInt( RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT ) ); 
		}
		catch( Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}

		connToDispatch = new ClientConnection();

		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName("Yukon Trending");
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 1000000 );
	
		connToDispatch.setHost(host);
		connToDispatch.setPort(port);
		connToDispatch.setAutoReconnect(true);
		connToDispatch.setRegistrationMsg(reg);

		try
		{
			connToDispatch.connectWithoutWait();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.dnd.DragAndDropListener#drop_actionPerformed(java.util.EventObject)
	 */
	public void drop_actionPerformed(EventObject newEvent)
	{
		System.out.println("   drop_actionPerformed() occured int CustomCommandEditPanel");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/3/2002 4:00:35 PM)
	 */
	public void exit() 
	{
		removeAll();
		setVisible(false);
		dialog.dispose();
	}
/**
 * Return the AddCommandButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddCommandButton() {
	if (ivjAddCommandButton == null) {
		try {
			ivjAddCommandButton = new javax.swing.JButton();
			ivjAddCommandButton.setName("AddCommandButton");
			ivjAddCommandButton.setText("Add");
			// user code begin {1}
			ivjAddCommandButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddCommandButton;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddRemovePanel() {
	if (ivjAddRemovePanel == null) {
		try {
			ivjAddRemovePanel = new javax.swing.JPanel();
			ivjAddRemovePanel.setName("AddRemovePanel");
			ivjAddRemovePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsAddCommandButton = new java.awt.GridBagConstraints();
			constraintsAddCommandButton.gridx = 0; constraintsAddCommandButton.gridy = 0;
			constraintsAddCommandButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAddCommandButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getAddRemovePanel().add(getAddCommandButton(), constraintsAddCommandButton);

			java.awt.GridBagConstraints constraintsRemoveCommandButton = new java.awt.GridBagConstraints();
			constraintsRemoveCommandButton.gridx = 0; constraintsRemoveCommandButton.gridy = 1;
			constraintsRemoveCommandButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getAddRemovePanel().add(getRemoveCommandButton(), constraintsRemoveCommandButton);
			// user code begin {1}
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
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G1402BBB1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D45715FC2DE95CFC18904336A475EBE9E3326C97E2CD5726266B6E32ED521AC493FFC8C1C3ABD6121084A3C6E35010781881C49DF4081A7A839195FFC814A08C84819301A101C8048281F4591511F942484C1B494CC386234D1EFB6FBBEF9EEF5E70E3BE4B771D6F3DFB6EBD771EF36E39671EF35F9D381D7B43B352F4BBB9AEAD03137EBECE633888B3474D2FD97DAD622266F568B9455F8100
	9C6E7BE7526139G6AC20C1DFAA3B77BFA96149320EC585E297FAB3C6F60AE1F482D0517G632C2D61386D41879E9EFD1C6E7DB84EA6DA0E319960398DA0110CB53FC35F85E064D659F6CB78E2A8EF135E89246900AF5A466E5A2D40CF135AAD101E331EA1ED36DC49156A0FC3598CB099E05AB33EBEB7D36EEE2B2C104A5DF707F3EB1F4A51E51331947DA324CA34794FCACF6EDE2A88BD17386798BC2BDB867ABE69F5BA0AFAAD026094C43E373F08773A1CEE313252DC5BB2686E63DBAC82EF8FF91670B75515
	C5E9B92E8B4A0C1705595552D8218361DC7C7D6981F90916F8C8D170929FB9FFF3448252F7699503C4D781E597G63B3FD7863GFC8E7796758D5A00CF7E2A2D17EF9FF671F5CE0743A2D85BF8F150C55966985F5FDCC13E1320DF7BC202FB40C946AB0077EA007605A84F6923A8CF57B73F60D2AE19F45B8348B3F3A113A3F621CF1E141F4C5F3C075ACAE2BE196F1D0BBA7495GCDGAE00900019G7B280D5E3F7F83F856DC928963CE2146652A35DB5DDFBA2D3C9D5EABAB21C64235723DCE371563189D7E48
	F5DC039F04390BC65A204FEEBAB9341B485687B9D17CC01721A15B2C67F6686224BE129779E4E3F2CDBAC864B2AE6E509F87B083F483DC8718369865DA393FF4AC399AE18E5D02455EB6F849E5F10B3503B63B15F7CB721D3AB21AFCDA4068BE1FB09D53CBC429D2AFF17FD6265B24211738083D3A590B25F53B582797B6F6D0403113977B4F87011D51448E7738089D2643FB1AE4238CFF08623B94F836CE9E22F6CB4A7DD0B7AF9A6D36EB9B4A5014FE9FE7BCD92EA9C358EFBF5495BF4778E9F8CEBD37BD8544
	7F08008F8108G08820887C885B07E916737A27F23F15AED0BE2EE996D6A7C776049FBC53725D170089621176F3038ED16CBF65E43C9EDB664935EAF0D205BC857D3AF0C71B9AF70EE0BE8F38A503B454103A1F9D81B2C83B9240D1B27356DB6878F83B8DC9C76EBBC30831EF60BC77C4AE535083C2F852B5FFD0068722A2583FA048E603D670FC467B3285FAB0677A6G262BA90789EEBA146748FA3967403B70BC8B064D5B41AF59FA899B9677B00EBF65A071A57CD5DE904D3C0B972C7062116AC28FEE766965
	32D36679BE0AA7EB248E987748787003B98C8FFDA8350C7591345EA20AEE5B25C111871FA85A04BE9F7DCC4A0BE7447273F0529ABFC2ECEC8148158810A24BF76A508E05FCCDCE6740202B1D4C1C24279FD1BFEB0869509744601E395FC5765006983453573A50CE4D598FF0471FB3E95AA981D78C334F49B16A35D6C87B740D710833EB026BDE8D8DB15AEB5467DB1E7A4EE368C729E3FAFF73A8517BCC60E59EC0D28C6ADDBC144F56DB2D4543638AEBF6430C71D61F153136D78A825B5857855AB6B6D420EDE3
	B78AC637215B8A34EC08558597103D45B9A4A8EC726E02F7699A96841EB2AD13F0EC2FFF2D1D6842A65844CE1B350F975BEC57F80805E209EDBDC157E0BA3C6F0EC5DB9ABA0436D4F06DE7DC54456D1A7BD95883C5BAD7AC1B5FC931BEDBE2BCCDBDCCF44E5BF987A82A4EB9A8088C9FC6573704177C4B9DC7345B069CA176B8408FCB6DC20FE4F2440F5887F9D93619162DC447EE1B83260AACD8A9AE1CF1C59997CE877EC88CB9971EF1GFA5A1F2E05742B9D67FCC6DC20E4BFD505043F35C0173524C31F3F84
	6D47F8F40FE25DCA06D367345B59B448737376D1A319CF7E6BC19E5CB2D958E7E519520F3D2B2A6B1064E37506E3C4BE3B42A7D4ACA1724549B1A359B398EE8D458D43A4E6E7629E7C25D19953DABDC7B567124075A5EC8EC3162877AF5787245F19000F12E53F7D1852E755F8BC4EDE9B5D9E24F9D9D3C874CF634BAB162B96EF11E788EC3C32728BC55C3BFBA0CB728351CBB0FE1CA290DD9B16022F8328DE0A6355947A5B4DE705DB60F9596676C05CE3653D283326C2A2BB590FA4B446E9A5CADDC4456F8B28
	0B7625CC877DCB553AF874C34247A44007493C7D21B0C7218B08A9E17B6D0319C2FE86BF50B3BB164A439FE4209DF36887738654F1CE64D21F6F0C02776825A3FDA90BE752E85C1238D49D67EC257814256AB8679E81631C2C67C1674F633E9BF91C4835G4A714F23DC5BBE4CD6485562348922AC533FFFC8562253ED6B3389080B22B8082829F3E2FB17094EF1B274199E5721E7389C87419D03729999D7A2901C954A5EB85CFFB6B9497A8F164BB934BCCBAE5FAB0C36F7903A6F322F30B1FEB96A98236DBD63
	812637C70E13B9724A65DF517230547F8E6E6CC0A63567E167A0CC0079CFC4DF1786503F45021C49G3B81FF7CCFACC6E0EB7011A162BBAE03A70325576662FB397CBF935AE38E248D067661GD1G71G5C1F51B7B68AC47E2BB60F0D789CAF256B915067744B6D92BF8A6CC3D92E487CF12DD36D0F891FF3201F85G4CB66F9840DCB591F0794BD00E13BDA4FE16FDC911E58852684F5DB6EB2D252F09EA057913C5545688FD57B274A71C1D74998265880028E53847F90FE0BE9A7960FDDCD3D019E6FE9D5F1E
	2B0BDD466CA8F5194F97A4C0BE4A46BC6740B1AB62A1970DA7386B0E1DAA9B39DB47F1C570E475B3C654935FF112D356470F28A77C5E13292CFFD7D056939D6D176819ECCF9FC659D21F0E606C3FBA94706CE0F6BC13A9CEFAA6BF3E174B8A1B31194D6D10C0561B689E64D93F61C3CA7FD93165C0C07F39F3B96B6F18746CDE37D7BAB3A826FB94216BD90EF3344BC466A88C4AB38166AE47759D6267934737E6FFF213F4B19326E33EF691353178654C5726497867A9BEC901E7F61EE9C7BBCF013A6E3F208F1E
	D1C456C8941463G568298D6401A86E8D801BE3972C4B1B7C60E586EF4B5C120EAD724AFCC3EB6474D65717F3D896DE956EFFE4E4D7E410A342D81663DFB851B6F6B52F3726F76D2BC132F2618489789F5F38112G529340EF8194A720FC5FBBF11C49D7EF3509B522E8696D27BBBE8A57E16307FCE86254D85F5613442E4C50CF0F5C5713A7BE04E707650A24A5522553CDFB82E40B5B69625D62B0435228CB3203B23FF32020845FDE921A603F2F30797EA7EA479189EC3EB74B789FD27C638ABC3303FD94CF4A
	71D0E7FE816DBE564E49B639DC71EE30FF57DCE96CB5F078160E41666557C501F3297FAC524E259EAF524E251EAA9ABD17DAD494B817DAD2244C25189DFD2F04785688180B47819281D2810CAF22CD75FDF410592706ED56C3F2D5672427AFA20E7FC5F1E0D97FD6BC76194D05E29263DEF20A2253C17AC7FCCFB119AF516922C806630B090EA0A46B97D9D389BFD0CC56061D3FCC518CE7BB39CD29B7E67ED2FB57C9228312AF5AACF65BB59A5637C1C3210F45EACD54079F871D1CF9916337E706B126333E08F6
	FC170964BD33209C8D108A307BA52C1BC66B2A215C83B089A0C22EBBE05232C16CE3A276C6464377D992AD9B23D2F30C8C29FE22BE925FB19E14725A1240F3FFF74958F3FFDF894DF9E04EC8E445F0BFAD21F177654B9EDE363169A5C6995768E9F1FAECE446589E5E6B720F9DEFF8B1961B77920B9D93601982C0E2EBF3A2E9734230F99419F1BFA37AA2310591E88AGF6C9BA28C8746DF90C0FA985A4DFEC9E28A3125AEB87411E85969F327A49C7E89C892123D98AA1E9365B566B06FC1AB4145ABD7189993F
	46EAED659D4E2B6CAB8A0E31A10F68344EA272FDCE77F0134DA3AA4825B8F401C99D07921903015FF0G56A65723ECB3BB1135C900E70A5CE6DD25D6BF4617C18FAFE31B494DDA7DD4C3FD4B4B9873BE6045D8209FF061GACCEDA694538B276E50C13768D133646D7BA7447G4EG6C27F2C3AE4761DF7227DA382ED748F738D927B88E777465865C53F39F7AB1E70ED911F608535E53273D4256CD52AB3E78B7F672DB990C4F2CB20C678ADEC5BE03CF92BE3B21AC82843F0AF1FCDFFD9E27F9A6201CA6A9CEEC
	77605940F439DFC7A969B78E4AEB818CC9282B22AA1907A4EC7B33FD1487A80BG41F25B28E64CE1264BEDF7BAE83FD0DE8BE0D809FE68A16F4D45110F5DA45D6B9326E37145DD54AE8AD62A634FB00AAFDE290EBF8F8C234DD5C3DD78CA4C091E7D9FE5FCEF3CEF2B8E695463263F46EC41781A7AFC24280A788DB360FBGA681CC831889B097A08EA099A0FDD507BE97408450G508D60828882888508D8057B6D7AD305DC6098CECA8269266B36307389264B6CB235AE672C726D858BD6850A613892221B04D5
	CCE799B2FEAB4527A870CC17D3292D1332A199E4C8C65BB96F417123811718BCF23C5464C063AF27B6EEDC2DBE4FF9156273D72B4FF3BC25F81ED38CF5212BF17C52AA357CA92BFDE39ADE8FB47E2EDCBA4E6BEA7973A95E743ADA7EFF590772B7C0DD646BB87E934D235932F1CD2071A75335D830C6AD7FD40AB72DD14B3F5701721B21AEE28D0E7F4F64788473C1457B5161DB9FC31F3925E330F596CA5C37347FFFDAF9188B30C6A0AA7D82DCED7325ABFC2F58EE6913E9EA2B8247A60DD5E347A62DD55AF1E9
	D715FFDC7A3FD55AF1E9EF15FFDC5ADFC5EC298F56F5175C4EA1634E4BB8B76D1360BAEDD631DF46FBA9DF845F401381917FF74A579045F5C9386FABF067A55C64AA92AF01487DCE377CFD310A1C03DC36596DD26CC65B58848837E4FD7C1072E8F3D994FC7FD846913ED91CDCC077CB435A8EFD7EDA0C1D6FAF4758593C966DB71346FE21D00E14F1B994978565F89997F6B2102D915A1B353148354AD8791100E3FC17734F8C6949F460227BBD778644CFEF20DE1E24388AD216F107C4541578866A6025D2B2BF
	0F4B6555341CA01707CB473B9EC99FB73766270EFB0C1B5199137D8B3ADE3256415E39CE56D135FC163E8E655D17CBF0A1D00E14F1C7A8EE9E1413E45C437B46621974A15B443A0969060D717BB147D06AA3E3827D7B7492570CBAB03C09325D69A0B650AD1743E8D9144B790E0978E8524F44E5BFB521B1A6EA97AC6729AA734F6FDA4BB06F88FD1365F711700C82A079B64B6FFED30E79DD2C06EC3847EE161EC95006CBE179860D89D254795D0DDA328FAB33BB067FDD9F191B6681066BCEA1E35715A84F2F73
	D398DF8D70E475F377E855CFCF41EF8E37FB48DD442B3CDB3475DA6CACF9283538DB1CF6DB6F30742D626754F71058F136CC976FA1FBA7B8F65BB527A07AD3F292EDAA257D94A0A58575D5A9223EAA567B6BABC3218B82CD5026E73DAF9F96572B753538021CDB79653B9C662987C76429C16B59B9C994BC235723BC16AA7F3977D6A1DD52FAB677699BBA743BB730B396A6CBFCB54AD23C41DF16CDAAD94C5026EB03CF967B8635AC8B9D647C81F2F2C992657C1B4E12DC14A56BA32AB9B657DD396A39A6FCFAB7
	20AD5CFDDE2BBEF8834A322699E5895710A5CDA54BACE8B3D7A1CB1C1FACB736B3BB761DA1B0BE1E9C71CDA6F183BB2FB23C05F97C35B24C297281577596661B3FAD43BCBE64AD4CB713E95B39D00E87C8115B3EDA0EF92CB1D5AB37E75FF954B8165BEFBC4DF1F8A7E2FB9B31F30DBCDB52BC665857CAF03C06D44C81EFFC263CF7BAE7FB2EAE50D9FFD0AA53F7F82AAF87B47D943F0BED5CC37AA6347129E82F0FF9CC70D430D424487C0BBA6ADB5B9B6BEA1AAE365535B6B7B5555634DE2CE93BF82E3E5EDCEF
	E674BB4412D1689B1ADB9BAFB41FEB9F514339FA6896659D90D15E5DEFEB69B76BCC60339361D31FFE5FABB6F15AF7E7FB69C799F2FF3648AAD8ABAB4DEECBDF8DACF13713DD5EE0FCDCB06159C64B5BE893F51544A6A6C3B99CE0765BE8D761D5E8D78BG17758E5ACAE2955A5519F7300F772AC9DBAFE98390A1374DB90DF2C73DB3DE3BC239D71C61E4FB39AB977BC9117B1EFB9EF15D9B91D7500238500D93BD234A2C75697969423FEA5BB1D91692CF1C00E34D5B08FBFB10DF6EBF3E7D7A4E1B247B430469
	D8CEEBAD27F7D439CE55390D03620D8ABC3305F5320F49053AA0GB6FEF239F27C844027G0C4D778B25DAFC9B5298EDFE1AFF9FAC775CD0CBFC21896ADBG7A8102G4281D85E7379A7E35ED32E770EF2CFB9FDAF72952560A1DAFACF04E7AA00D1672BE3F4AED1A9CF8154D7684623070BA6C25784EDFBB45A0B9AB8B6CF3FE6EBD6271E3FA80A8F5529672F319267AF9C6A5275B8FF8F7A1D550C4F6EE65DA45D4A8953B1396E207E25DF2F16779E0A9775EAF9F32BD15EA028CB1065FD6F34F27C0674CEFDF7
	7AF866A9B4D7AB1EF60DC12B8513B4E85898734FEB69A6CCEA9F99102E30C5C997856D2247A5D306B05ADCA42A7AC8CDC79DDEA96764BEBC0A775432DBFF3E352B7CD60E41F23ACD9F933FEE4CE0B645F0860ABBB68217DACA3F7B832E9D0061ACA64C87FBB250A7696A821FF7ED2E537E663C3DCE7B1B732E3A516F086755857AA64D785B37175E27845E6649BCDF3C01F2444B38289BA8C7120CDB779541D5E7023C19085B54C8F07DD08E12F1370B7E7A8B967D75F7156AAA9468A2E45AB7E84CBA8F4A09B26E
	8B790CAAB593ED70BFAAFDF677FB45FBF3652D37417EFF4098A155BE196ED53C9F2C3E75E3D75D52B1581C1EB86DEFA3A5277DED64D19A7765EF6A5497EFC2FB58170776D0AD63924EE33BE099D7FF815B054938B3AD7E635637780F1BABA0ED22CCFB5AC94FCF33BA7547GD81CE8217747257B8A74B81861F33D59BEBC9E87CBF95E8E93BD8F02C54F624E3BC89A49311C52EB9A6D5BB721D7661455408701DD923EA52BD315D3166CA5FC385C7CE55E6D662D6C7A3DF456F0167A02EE206987E03C7C325E47CB
	4C0D71FC078F44CB289FAF599DA4A7F3D83CB6472003F24278B85CC17888077611G2C6D7B0D34ADA8F0E45BF26AEFE221DD025CF60A09685CC1E2BC8E4FA2264BE798464DB0DFGF600A0GF2B64B688C65245EB2A8BADBF98F8CC122C4544D61F27CD69B86B46C6E2A259C7BDCC07A81C85F5229AFGB0EFC1DE66670E1C5BD7BE736933A48F75E95D623FBF2B756905B68408BE09FC41DBFC7AE4FDE7B7787A6E3E00FD078568DB9F20EFD29729681B49DAAA202C3321EE9EC09200E1EB277E78D614355DC9EC
	4B43CB3FB9E06A137467F0E20E58846D831F9DBC5468FBEFBB4B493652FE63FDF88A7CD0BEDE3BADB25B9C3CC0FE44F56C63526379A5E505C8F7354957473DDF7A7AC8ED98FF9FF34E7968B63A47C347624CF9A67C86B899640C5A0AFEE13F17FC4F1E50EFDCBDB4CD27298A4BAFF74A67B7A950AF4B33EBE99ED6EC6854B7G7498F0AE2A4B496FCEC71CCF506E0AEA3EEAEF3E58DA5F56F8217E62397A2E760BF54DCDDFFDF98E736AAFCF6198DE680B5D717D66145F9D62B4659D6007CF296F9093FADF7D32D3
	6ABB44A369F3AA55F708111E71541B07F975CC43E836F3280513E926DCC01AC4837A324F7E662BBFAF201C7959A8676D02DA4EFBC75C15FECC98DD4ED785351CBBC64819678CA4A71B33C94E0FD4F3C66F8DA957133D282D715C67CD75975B6A1B6A6B5A9B1B4FC9FB74A253D8F9D4D776C8BF4068624724FBDFD5E6F4FBEE721C61D1EA2FE236BAEF7BAF0A8F4AD667ED8D72B9C3B054A5E7E33C57FA6A66724D7EBCA5DD7BB6586B360DA7B71AFAC1C9F79DE8C246C577FF2366D4DA10C76DA1EA870B976CB681
	7C817DC52C5C671CB2CEFEFFC271FE6E9F10EF755D52B1D8DC75ED1EFFCCB6A94FBFA67B578B7E6DE6DCD036E37FA7A26113A1A8C42E64B2BCEEEE3B6C439B69C773782B25157A95776BF5DCFAA31749AE91B4F26964A6C0A317C1AF8AB4F2FA516972129F99E7A84173B577F7227D89E15315205DD664B25947AB1153B51F95B9FD0D558AB87661C2A6A1F78BF6CA308584FB57FFF32A39F3C9CC64C573CE9D2783597AF9A6CC469089CB29B8648C2DB1C00F843BF5C41B2DFE946390DC3A63D7782D72BED74AED
	3A003F5708B6CAE7EB5253F71F6BDA8D195B021CCE7D199C4C251FBAF3028BFC5F244DE1315BC50BFD20C630123BA73543A26F4B1D3F2DA17DFB300DD4496AFED0C3783E222079FFD0CB8788152067074696GG50C4GGD0CB818294G94G88G88G1402BBB1152067074696GG50C4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGG97GGGG
**end of data**/
}
	/**
	 * Returns the buttonPushed.
	 * @return int
	 */
	private int getButtonPushed()
	{
		return buttonPushed;
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
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getCategoryList() {
	if (ivjCategoryList == null) {
		try {
			ivjCategoryList = new javax.swing.JList();
			ivjCategoryList.setName("CategoryList");
			ivjCategoryList.setBounds(0, 0, 137, 332);
			ivjCategoryList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			// user code begin {1}
			DefaultListModel model = new DefaultListModel();
			for (int i = 0; i < CommandCategory.getAllCategories().length; i++)
				model.addElement(CommandCategory.getAllCategories()[i]);
				
			Vector distinctTypes = retrieveDistinctDeviceTypes();
			for (int i = 0; i < distinctTypes.size(); i++)
				model.addElement(distinctTypes.get(i));

			ivjCategoryList.setModel(model);
			ivjCategoryList.addListSelectionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCategoryList;
}
/**
 * Return the CategoryListScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getCategoryListScrollPane() {
	if (ivjCategoryListScrollPane == null) {
		try {
			ivjCategoryListScrollPane = new javax.swing.JScrollPane();
			ivjCategoryListScrollPane.setName("CategoryListScrollPane");
			getCategoryListScrollPane().setViewportView(getCategoryList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCategoryListScrollPane;
}
	public com.cannontech.message.util.ClientConnection getClientConnection()
	{
		if( connToDispatch == null)
			connect();	
		return connToDispatch;
	}

/**
 * Return the DandDCommandTable property value.
 * @return com.cannontech.common.gui.dnd.DragAndDropTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.dnd.DragAndDropTable getDandDCommandTable() {
	if (ivjDandDCommandTable == null) {
		try {
			ivjDandDCommandTable = new com.cannontech.common.gui.dnd.DragAndDropTable(new DeviceTypeCommandTableRenderer());
			ivjDandDCommandTable.setName("DandDCommandTable");
			getDandDCommandTableScrollPane().setColumnHeaderView(ivjDandDCommandTable.getTableHeader());
			ivjDandDCommandTable.setModel(new com.cannontech.yc.gui.DeviceTypeCommandsTableModel());
			ivjDandDCommandTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjDandDCommandTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			getDandDCommandTableScrollPane().getViewport().setScrollMode(javax.swing.JViewport.BACKINGSTORE_SCROLL_MODE);			
			ivjDandDCommandTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			ivjDandDCommandTable.setToolTipText("Click-and-drag to reorder");
			ivjDandDCommandTable.getColumnModel().getColumn(DeviceTypeCommandsTableModel.LABEL_COLUMN).setPreferredWidth(150);
			ivjDandDCommandTable.getColumnModel().getColumn(DeviceTypeCommandsTableModel.COMMAND_COLUMN).setPreferredWidth(150);
			ivjDandDCommandTable.getColumnModel().getColumn(DeviceTypeCommandsTableModel.VISIBILTY_COLUMN).setPreferredWidth(15);
			ivjDandDCommandTable.getColumnModel().getColumn(DeviceTypeCommandsTableModel.CATEGORY_COLUMN).setPreferredWidth(50);
			ivjDandDCommandTable.addDragAndDropListener(this);
			ivjDandDCommandTable.getSelectionModel().addListSelectionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDandDCommandTable;
}
/**
 * Return the DandDCommandTableScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getDandDCommandTableScrollPane() {
	if (ivjDandDCommandTableScrollPane == null) {
		try {
			ivjDandDCommandTableScrollPane = new javax.swing.JScrollPane();
			ivjDandDCommandTableScrollPane.setName("DandDCommandTableScrollPane");
			ivjDandDCommandTableScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjDandDCommandTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			getDandDCommandTableScrollPane().setViewportView(getDandDCommandTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDandDCommandTableScrollPane;
}
/**
 * @return
 */
public String getDeviceType()
{
	return deviceType;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDeviceTypeCommandSetupPanel() {
	if (ivjDeviceTypeCommandSetupPanel == null) {
		try {
			ivjDeviceTypeCommandSetupPanel = new javax.swing.JPanel();
			ivjDeviceTypeCommandSetupPanel.setName("DeviceTypeCommandSetupPanel");
			ivjDeviceTypeCommandSetupPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOkCancelButtonPanel = new java.awt.GridBagConstraints();
			constraintsOkCancelButtonPanel.gridx = 0; constraintsOkCancelButtonPanel.gridy = 1;
			constraintsOkCancelButtonPanel.gridwidth = 4;
			constraintsOkCancelButtonPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOkCancelButtonPanel.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsOkCancelButtonPanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getDeviceTypeCommandSetupPanel().add(getOkCancelButtonPanel(), constraintsOkCancelButtonPanel);

			java.awt.GridBagConstraints constraintsDandDCommandTableScrollPane = new java.awt.GridBagConstraints();
			constraintsDandDCommandTableScrollPane.gridx = 2; constraintsDandDCommandTableScrollPane.gridy = 0;
			constraintsDandDCommandTableScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDandDCommandTableScrollPane.weightx = 3.0;
			constraintsDandDCommandTableScrollPane.weighty = 1.0;
			constraintsDandDCommandTableScrollPane.insets = new java.awt.Insets(5, 5, 5, 5);
			getDeviceTypeCommandSetupPanel().add(getDandDCommandTableScrollPane(), constraintsDandDCommandTableScrollPane);

			java.awt.GridBagConstraints constraintsAddRemovePanel = new java.awt.GridBagConstraints();
			constraintsAddRemovePanel.gridx = 3; constraintsAddRemovePanel.gridy = 0;
			constraintsAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsAddRemovePanel.weighty = 1.0;
			constraintsAddRemovePanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDeviceTypeCommandSetupPanel().add(getAddRemovePanel(), constraintsAddRemovePanel);

			java.awt.GridBagConstraints constraintsCategoryListScrollPane = new java.awt.GridBagConstraints();
			constraintsCategoryListScrollPane.gridx = 0; constraintsCategoryListScrollPane.gridy = 0;
			constraintsCategoryListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsCategoryListScrollPane.weightx = 1.0;
			constraintsCategoryListScrollPane.weighty = 1.0;
			constraintsCategoryListScrollPane.insets = new java.awt.Insets(5, 5, 5, 5);
			getDeviceTypeCommandSetupPanel().add(getCategoryListScrollPane(), constraintsCategoryListScrollPane);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceTypeCommandSetupPanel;
}
	/**
	 * @return
	 */
	public String getDialogTitle()
	{
		if (dialogTitle == null)
			dialogTitle = "Edit Custom Command File";
		return dialogTitle;
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
				ivjOkButton.addActionListener(this);
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
 * Return the RemoveCommandButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRemoveCommandButton() {
	if (ivjRemoveCommandButton == null) {
		try {
			ivjRemoveCommandButton = new javax.swing.JButton();
			ivjRemoveCommandButton.setName("RemoveCommandButton");
			ivjRemoveCommandButton.setText("Remove");
			// user code begin {1}
			ivjRemoveCommandButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRemoveCommandButton;
}
	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#getValue(Object)
	 */
	public Object getValue(Object o)
	{
		return null;
	}
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		 com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
	try {
		// user code begin {1}
			//set the app to start as close to the center as you can....
			//  only works with small gui interfaces.
			java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((int)(d.width * .3),(int)(d.height * .2));
		// user code end
		setName("DeviceTypeCommandSetupFrame");
		setLayout(new java.awt.GridBagLayout());
		setSize(841, 328);
		setVisible(true);

		java.awt.GridBagConstraints constraintsDeviceTypeCommandSetupPanel = new java.awt.GridBagConstraints();
		constraintsDeviceTypeCommandSetupPanel.gridx = 0; constraintsDeviceTypeCommandSetupPanel.gridy = 0;
		constraintsDeviceTypeCommandSetupPanel.gridwidth = 2;
constraintsDeviceTypeCommandSetupPanel.gridheight = 6;
		constraintsDeviceTypeCommandSetupPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsDeviceTypeCommandSetupPanel.weightx = 1.0;
		constraintsDeviceTypeCommandSetupPanel.weighty = 1.0;
		constraintsDeviceTypeCommandSetupPanel.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getDeviceTypeCommandSetupPanel(), constraintsDeviceTypeCommandSetupPanel);
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
	public static void main(java.lang.String[] args)
	{
		try
		{
			AdvancedOptionsPanel aAdvancedOptionsPanel;
			aAdvancedOptionsPanel = new AdvancedOptionsPanel();
			aAdvancedOptionsPanel.showAdvancedOptions(new javax.swing.JFrame());
		}
		catch (Throwable exception)
		{
			System.err.println("Exception occurred in main() of javax.swing.JDialog");
			exception.printStackTrace(System.out);
		}
	}
	/**
	 * Retrieves the distinct types from yukonPaobject table
	 */
	private Vector retrieveDistinctDeviceTypes() 
	{
		java.util.Vector types = new java.util.Vector();
	
		StringBuffer sql = new StringBuffer	("SELECT DISTINCT TYPE FROM YUKONPAOBJECT ORDER BY TYPE");
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;

		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null )
			{
				CTILogger.info(getClass() + ":  Error getting database connection.");
				return types;
			}
			else
			{
				stmt = conn.prepareStatement(sql.toString());
				rset = stmt.executeQuery();
				while( rset.next())
				{
					types.addElement(rset.getString(1));
				}
			}
		}
			
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( stmt != null )
					stmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}
		return types;
	}
	public void saveChanges()
	{
		int rowCount = getDandDCommandTable().getModel().getRowCount();
		if( !CommandCategory.isCommandCategory(getDeviceType()))
		{
			DeviceTypeCommandsTableModel model = (DeviceTypeCommandsTableModel) getDandDCommandTable().getModel();
			for (int i = 0; i < rowCount; i++)
			{
				int newIndex = i+1;
				DeviceTypeCommand tempValue = model.getRow(i);
				
				//store the original value for comparison, this way we only save to db those that have actually changed
				int origOrder = tempValue.getDeviceTypeCommand().getDisplayOrder().intValue();
				if( origOrder != newIndex)
				{
					//Set the DisplayOrder of the objects based on their order in the table.
					tempValue.getDeviceTypeCommand().setDisplayOrder(new Integer (newIndex));
					saveObject(tempValue, Transaction.UPDATE, DBChangeMsg.CHANGE_TYPE_UPDATE);
				}
			}
		}
	}
	public void saveObject(DBPersistent item, int transType, int changeType) 
	{
		if( item != null )
		{
			try
			{
				Transaction t = Transaction.createTransaction(transType, item);
				item = t.execute();

				//write the DBChangeMessage out to Dispatch since it was a Successfull ADD
				DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange)item, changeType);
			
				for( int i = 0; i < dbChange.length; i++)
				{
					DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
					getClientConnection().write(dbChange[i]);
				}
			}
			catch( com.cannontech.database.TransactionException e )
			{
				CTILogger.error( e.getMessage(), e );
			}
			catch( NullPointerException e )
			{
				CTILogger.error( e.getMessage(), e );
			}
			
		}
	}
	/**
	 * Sets the buttonPushed.
	 * @param buttonPushed The buttonPushed to set
	 */
	private void setButtonPushed(int buttonPushed)
	{
		this.buttonPushed = buttonPushed;
	}
/**
 * @param string
 */
public void setDeviceType(String string)
{
	deviceType = string;
	getCategoryList().setSelectedValue(getDeviceType(), true);
}
	/**
	 * @param string
	 */
	public void setDialogTitle(String string)
	{
		dialogTitle = string;
	}
	/**
	 * o is a Vector of LiteDeviceTypeCommand values
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
	 */
	public void setValue(Object o)
	{
		if ( !(o instanceof Vector))
			return;
		
		/** Remove all existing rows before adding any rows on */
		((DeviceTypeCommandsTableModel)getDandDCommandTable().getModel()).removeAllRows();
				
		
		/** Setting this variable also removes all previous entries in the tableModel
		 * and re-adds them back in to catch any changes in the vector */
		for (int i = 0; i < ((Vector)o).size(); i++)
		{
			Object liteObject = ((Vector)o).get(i);
			DeviceTypeCommand dtc = null;
			if( liteObject instanceof LiteDeviceTypeCommand)
			{
				dtc = (DeviceTypeCommand)LiteFactory.createDBPersistent((LiteDeviceTypeCommand)liteObject);
			}
			else if( liteObject instanceof LiteCommand )
			{
				//We have to create a "fake" DeviceTypeCommand.  This will NOT be entered into the table,
				// but rather used as a template for all deviceTypes that fit into the CATEGORY!
				dtc = new DeviceTypeCommand();
				dtc.setCommand((Command)LiteFactory.createDBPersistent((LiteCommand)liteObject));
			}
			((DeviceTypeCommandsTableModel)getDandDCommandTable().getModel()).addRowToEnd(dtc);
		}
	}
	/**
	 * Show AdvancedOptionsPanel with a JDialog to control the closing time.
	 * @param parent javax.swing.JFrame
	 */
	public void showCommandSetup(javax.swing.JFrame parent)
	{
		dialog = new javax.swing.JDialog(parent);
		dialog.setTitle(getDialogTitle());
		dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setContentPane(getDeviceTypeCommandSetupPanel());
		dialog.setModal(true);	
		dialog.getContentPane().add(this);
		dialog.setSize(841, 328);
		
		// Add a keyListener to the Escape key.
		javax.swing.KeyStroke ks = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true);
		dialog.getRootPane().getInputMap().put(ks, "CloseAction");
		dialog.getRootPane().getActionMap().put("CloseAction", new javax.swing.AbstractAction()
		{
			public void actionPerformed(java.awt.event.ActionEvent ae)
			{
				setButtonPushed(CANCEL);
				exit();
			}
		});
		
		// Add a window closeing event, even though I think it's already handled by setDefaultCloseOperation(..)
		dialog.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				setButtonPushed(CANCEL);
				exit();
			};
		});

		dialog.show();
	}
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e)
	{
		if(e.getValueIsAdjusting())
			return;	//not yet!

		if( e.getSource() == getCategoryList())
		{
			setDeviceType((String)getCategoryList().getSelectedValue());
			Vector objects = null;
			if( CommandCategory.isCommandCategory(getDeviceType()))
				objects = CommandFuncs.getAllCommandsByCategory(getDeviceType());
			else
				objects = CommandFuncs.getAllDevTypeCommands(deviceType);
			setValue(objects);
		}
		else if( e.getSource() == getDandDCommandTable().getSelectionModel())
		{
			int selectedRow = getDandDCommandTable().getSelectedRow();
			if (selectedRow > -1 )
			{
				DeviceTypeCommand dtc = ((DeviceTypeCommandsTableModel)getDandDCommandTable().getModel()).getRow(selectedRow);
					
				if(dtc.getCommandID().intValue() <= 0 )
					getRemoveCommandButton().setEnabled(false);
				else
					getRemoveCommandButton().setEnabled(true);
			}
		} 
	}
}
