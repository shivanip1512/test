package com.cannontech.tdc.addpoints;

/**
 * Insert the type's description here.
 * Creation date: (1/28/00 2:05:35 PM)
 * @author: 
 */
import com.cannontech.database.model.DBTreeModel;
import com.cannontech.database.model.ModelFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.dnd.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;

import com.cannontech.common.gui.dnd.TransferableTreeNode;
import com.cannontech.common.gui.dnd.DragSourceListenerClass;

public class AddPointsLeftTree extends javax.swing.JTree implements java.awt.dnd.DragGestureListener
{
	private com.cannontech.database.model.DBTreeModel dbTreeModel = null;

	private DragSource dragSource = DragSource.getDefaultDragSource();

	final static DragSourceListener dragSourceListener =
			new DragSourceListenerClass();

/**
 * AddPointsTree constructor comment.
 */
public AddPointsLeftTree() 
{
	super();

	initialize();
	
	dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_MOVE, this );
	
}
/**
 * AddPointsTree constructor comment.
 * @param value java.util.Vector
 */
public void dragGestureRecognized(java.awt.dnd.DragGestureEvent dge) 
{
	TreePath path = getSelectionPath();

	if( path != null )
	{
		DefaultMutableTreeNode selection =
				(DefaultMutableTreeNode)path.getLastPathComponent();

		TransferableTreeNode node = new TransferableTreeNode( selection );

		dragSource.startDrag( dge, DragSource.DefaultMoveDrop, node, dragSourceListener );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/00 11:55:16 AM)
 * @param deviceName java.lang.String
 */
public Object[] getDevicesChildren(String deviceName) 
{

	DefaultMutableTreeNode rootNode = 
		((DefaultMutableTreeNode)getModel().getRoot());

	for( int i = 0; i < rootNode.getChildCount(); i++ )
	{
		if( deviceName.equals( rootNode.getChildAt(i).toString() ) )
		{
			Object[] childNames = new Object[ rootNode.getChildAt( i ).getChildCount() ];
			
			for( int j = 0; j < childNames.length; j++ )
			{
				childNames[j] = rootNode.getChildAt( i ).getChildAt( j );
			}
			return childNames;
		}
		
	}
	
	return null;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION AddPointsLeftTree() ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		
		dbTreeModel = new com.cannontech.database.model.TDCDeviceTreeModel();
		
		// user code end
		setName("AddPointsTree");
		setModel(dbTreeModel);
		setSize(78, 72);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// set the renderer for our custom tree
	this.setCellRenderer( new com.cannontech.common.gui.util.CtiTreeCellRenderer() );

	// for now, we do not need to use the cache, so release it and
	// the dbTreeModel.update() call will get a fresh database
	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().releaseAllCache();
	dbTreeModel.update();

	// user code end
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE6F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E11DD7EBECD4C594EE8CA27AC7C89A5298B14500B68685C3B50D698FE88C290281E388EAA0EA0226C6E2A4419FAA466976419B8A34349522150788E95A052DADB6E0688384A9E153DD162A98FB3BBBED6F766E3D37774E725444F36631FBCBF6855C64E46E1CB3F366BC3EF3E61654FE1BDFD56E2BA5243C0A48DFC7B9A1EDB504FCF31466A8DE8EAD29A61E5FF7C0BB09D5DB8963D410ED6073F87FD69837
	8395G8F47F5B05610877A9BA4FF0964ABAA7791D2BC0CEBE6746F133C93B08F82CD851AB51CDE3F091F7CE06789B93FBAAD1F2CFB7530EF9B6A7168D4D4B9A2767EA047A6B237FC59881E35085B09FB86E11E87044ED71001D3EDB01E8C0E0D7C9C3592ADD14DB4AD135168E88B0BC5DB34D84C36F413391D1DDD31D810FF7651E196F6A86D9BC53D5BC39B399EC2EAE17EA6674D3C0CE7CD056F822085C0420FD307CFCABB87EE4FA513D61895BB327831CF9776776AE9BFF2131FEFE864B11FDB07314D8159AC
	20A22015C06B017645CB2A7702ECB7F93176A80CDDC32C47CAA4AC334B363B0D18F3510AD1833E411DA1A6F97DB4EAB9B1C2C4BE1774B7E730673FC86CFB7B3E7709189484D4EE6774BFC5D64E3F605B10A1A6AB8B7C3E363808C5DFBC9D93F1766ED036330D78C46CCDBC7B8A711E4DA6DB95D5994EAEDCFE4417A37564FA4EAE0DE32E6BA2186BC27846FD38DF701BB8FF6BD81AAFF078B447864E1BC0B6E9CCE1E3E03B32252F72B1D2F32CAFE39C72CA3AFCE5E34286E224ED9138A82A47FAEA867EB920C120
	9BC0F9C005C025064246D7352D77080D2066306E24EE4428C3445E223ABDB0529B4C514E1BAE534CA80DE80E2E8D9954A5F24D42BA34FDE8423EC59C33D1B86397EAD2C7E33AE502F6ADC199F5DC31E6E95DCEDC63D0AE8D6B898A87A4EC22742E285B0DEEEBAE3BEC47B4C653AB047C1DBA0C65B5AD829AC181FC0B383C5F00B1AF837FC982F210D0F178FD8F6A3BC09D139A1D1DFD16B51634436889390A6B48D1DE5B87617BA4105075DEA37272E1DE1452B3ED4FAE0F1EDEBD0A66EB4ECDD59BFBA338E7892C
	AFCB28FCFFBC20725D3BEDB6E9DE7EEB46FC73BC1BA24F8D661D387F1E6BC55BCE1B2A9FBFFBF9609EEACF59F2BF751A2E75C577F4C60544797D1EA1627CF0AB66EC907C7A9B68F1D345FA491EA6015DEE4D258A2D179CG9A0D25A3AF561654E3FF25572849FA29CD4D98FC3892AB2F54EFCA63F858CAE1ECA9675B007B9EG1A1B62AF2FC7ECEA0CB97AD0125190E5CCB7C75272D57CACB7B9E427CBC61D353A9E63E0DD875C2756FFD83F0B5703E9D2EEF4EA0B14FF44B1CB8D1AG23FB2C244984FFED835E3F
	E374262C19CF9B2AB9660DA4561F385366E9DB5087C7CFC0A83004C92505F86E8A55FA62DC88B1453B7993D3E1727A8105490337E61285FF9D72E56A07F933FBFDFD16404E20154624306703464CF62F61F5A3792E30B36E47FBBCF5316225AAF1331B3CFE8975701EF4D60BB24B39A9F0D162C7FE8E1C1B8BB447D238F03FEE74542084C40FE598A23CA093B108C73CB1F85992B1581E4A4B232DB518C7BA1E24505A3099DCC845FAFA6B2EBBE49199F7A1FF229563EEF87A0870B3F456CF6E6F0D11FEDFBC43FB
	90FF2859AACF7F1CD56F085C31FCD23C6338EFCB06BCA11D3005EF83F6BACF4D29F744566328BB8FE485360AE3235F5B933BDC570A6A3C9DCB9FCB7D5EBC8A5EEB7E4DB08E6B0E4B4EC3115DD071F8430F76E23F16EC990FCBC636F8C0D757062030DB9218784ECEEF7EC56F194B6F7AE2BBF36FDA1601AFFCBD97403C2F86B999AF29EEG72838547155FFF9C8B103B5C09E7EE40D5E5EAC6E8C211CBDFF79E2F90F5A967F5472BD4DD42A86E13C5BC17B61CB989C870EA42484B05791C14AD41C0763EF5A51039
	EF7D99485C3722011D028FBA3C3739120F853275B5D1072BC26887CF870CE563E8733A907620E5636A8D7D7CF5445FB0DC6630B5E453A8D960603A43E1759F20996627018601F2007201047E4DE15C5B5B0D08675116EF33D3547B66775FAE77D5E44178C6D75E0FF2ACFE68G9135F3114796771C83D90E0B3C37425E7AC71D5300DA3027F079164BDEB946664884792989F234F5468439B5C1DE6559AF7CDAFCC8556E7A458544F861272C7FE5CA25BFEB64980C9CA0879F79C2566E5AB64C41C126B073E5E0BF
	0C91ADAE11FBA626C351F068823387A29B02941BA36A8E1F977EFF6F75059B147D05AFBDC90AA6FFD61E3577B059FB64D896BE602947E0BBE2E38A48E68115823D8B348EE8EBD279545D56ACEA921D69E2CC0B0E722E238AB222536BE9B682CF5E09EDF8A73A52DFF15E748E4CCB9368BE89B488440026244E3A7663FEF1D606F34E44C6E80F45DF3BCC554DEF6D59EBF4307D6EEFE43D9DFB6610451815C07D0AEF35E3FE18E5F3266031F60C3D230F0CB231D472EF35A33E8D78BF0AECF987F5E437EBF6475D6D
	FA2E0377F750973EE9CB1D73BBB01E567030CBD37E97F55424F8675D20656AF8C97C8BD0CB8788A525754D8187GGDC90GGD0CB818294G94G88G88GE6F954ACA525754D8187GGDC90GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGBB87GGGG
**end of data**/
}
}
