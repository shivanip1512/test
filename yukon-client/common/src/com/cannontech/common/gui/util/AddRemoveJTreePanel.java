package com.cannontech.common.gui.util;

import com.cannontech.common.gui.util.TreeViewPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import com.cannontech.database.model.ModelFactory;
import com.cannontech.database.model.DBTreeModel;
import com.cannontech.database.model.DBTreeNode;
import com.cannontech.database.data.lite.LiteBase;
import java.util.Vector;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.model.LiteBaseTreeModel;
import com.cannontech.database.model.DeviceTreeModel;
import javax.swing.JComboBox;

/**
 * Insert the type's description here.
 * Creation date: (9/1/2004 9:28:52 AM)
 * @author: 
 */
public class AddRemoveJTreePanel extends javax.swing.JPanel implements java.awt.event.ActionListener 
{
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JLabel ivjJLabelAssigned = null;
	private javax.swing.JLabel ivjJLabelAvailable = null;
	private javax.swing.JScrollPane ivjJScrollPaneAssigned = null;
	private javax.swing.JScrollPane ivjJScrollPaneAvailable = null;

	protected transient com.cannontech.common.gui.util.AddRemoveJTablePanelListener fieldAddRemoveJTablePanelListenerEventMulticaster = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	
	private boolean showPoints = false;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AddRemoveJTreePanel.this.getJButtonAdd()) 
				connEtoC1(e);
			if (e.getSource() == AddRemoveJTreePanel.this.getJButtonRemove()) 
				connEtoC2(e);
			if (e.getSource() == AddRemoveJTreePanel.this.getJButtonAdd()) 
				connEtoC3(e);
			if (e.getSource() == AddRemoveJTreePanel.this.getJButtonRemove()) 
				connEtoC4(e);

		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == AddRemoveJTreePanel.this.getJScrollPaneAssigned()) 
				connEtoC5(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	};
	
	private TreeViewPanel ivjAssignedTree;
	private TreeViewPanel ivjAvailableTree;
	
/**
 * AddRemoveJTablePanel constructor comment.
 */
public AddRemoveJTreePanel() {
	super();
	initialize();
}
/**
 * AddRemoveJTablePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public AddRemoveJTreePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * AddRemoveJTablePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public AddRemoveJTreePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * AddRemoveJTablePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public AddRemoveJTreePanel(boolean isDoubleBuffered) {
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
	if (e.getSource() == getJButtonAdd()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonAdd()) 
		connEtoC3(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener com.cannontech.common.gui.util.AddRemoveJTablePanelListener
 */
public void addAddRemoveJTablePanelListener(com.cannontech.common.gui.util.AddRemoveJTablePanelListener newListener) {
	fieldAddRemoveJTablePanelListenerEventMulticaster = com.cannontech.common.gui.util.AddRemoveJTablePanelListenerEventMulticaster.add(fieldAddRemoveJTablePanelListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC1:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemoveJTablePanel.jButtonAdd_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAdd_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemoveJTablePanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemoveJTablePanel.fireJButtonAddAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonAddAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemoveJTablePanel.fireJButtonRemoveAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonRemoveAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JScrollPaneAssigned.mouse.mouseClicked(java.awt.event.MouseEvent) --> AddRemoveJTreePanel.jScrollPaneAssigned_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		//this.jScrollPaneAssigned_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JTableAssigned.mouse.mouseClicked(java.awt.event.MouseEvent) --> AddRemoveJTablePanel.jTableAssigned_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		//this.jTableAssigned_MouseClicked(arg1);
		this.fireMouseTableAction_actionPerformed(new java.util.EventObject(this));
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
protected void fireJButtonAddAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAddRemoveJTablePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemoveJTablePanelListenerEventMulticaster.JButtonAddAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonRemoveAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAddRemoveJTablePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemoveJTablePanelListenerEventMulticaster.JButtonRemoveAction_actionPerformed(newEvent);
}
protected void fireMouseTableAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAddRemoveJTablePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemoveJTablePanelListenerEventMulticaster.MouseTableAction_actionPerformed(newEvent);
}
/**
 * Return the AssignedTree property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TreeViewPanel getAssignedTree() {
	if (ivjAssignedTree == null) {
		try {
			ivjAssignedTree = new TreeViewPanel();
			ivjAssignedTree.setName("AssignedTree");
			ivjAssignedTree.setBounds(0, 0, 78, 72);
			// user code begin {1}
			ivjAssignedTree.getTree().getSelectionModel().setSelectionMode(
				javax.swing.tree.TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAssignedTree;
}
/**
 * Return the AvailableTree property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TreeViewPanel getAvailableTree() {
	if (ivjAvailableTree == null) {
		try {
			ivjAvailableTree = new TreeViewPanel();
			ivjAvailableTree.setName("AvailableTree");
			ivjAvailableTree.setBounds(0, 0, 78, 72);
			// user code begin {1}
			ivjAvailableTree.getTree().getSelectionModel().setSelectionMode(
				javax.swing.tree.TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAvailableTree;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G64FBA1B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8BF8D46515148F95F771DBDCE3C505CA11ED5315C57AED5A25B53A2C5AADEE592F58EAE5AD3B4596AD7D1A3A38321FD9BFDA7FCCA60FC9A68FC8C202F9128412C8C8A68FB406704803BCB0CA168C88B6AB5CE4EE1211494CB0F313CC94481E73BF6E5CCC6E4C706473645E7F7C2FF34EFF4E794FFF7EBB126C622589F106ECC26292887F77C19CA16BBB89796636BF1788DCEC3249C8B47F76G6CA0
	938D08CCB6190CDBBFB199B349C8D1B2148B20BCFA42E44C0577AC62A8BA568AAFC16649033A9FDBFF5E9BFA1E3E4BE21EA4DA5E1FE206E7BAC08960BAG961AF07E7614BC0E9F05F2BAFFC708B390521D02ED9E1F485360DF606D8A204E02100AEDBECFA960758B8197899083C0D2D95BBCF81AA8F5F7B4B771F68377C612ED7F7209A18D67520CAF40477BAE664F92728BAA08F87246FB9F4233256F624811A157D84D1064F43A1C0ABCB4DA8345B11733E6E45CDEB32E589D4D4D2DB65BA9F94CB5A11F30CA03
	8E39CFF24A8E68EBB6A34DAFCEA4537100A718E39145FDBC09F26A0377D100B9E6C17773E9AD1C2E080B11E453232DC1695EE8E67412B4BF5D97361D0EDF1506635FC557817B9D03328F007194BD5CF483BCF9E44692636727A9021FEDE9021F2F5762FC55693066G8369827FFF9644ABD0DEG30BCDD70F938C270F9762BFFA03103178C99C1780CCEE77CEDCE777369B65EE758CD75ED63955419128C189760AC40E50045G51G79D44F9F387C9EBCDB8715F64AE22B5B5D6630F9BEF659E4873C8323038A47
	1D1207DC9E9BA1CC17DF1E285621C7554F0C19FA0C40F87B14885DDBF1EAA5D11491C3028EEFD1AF76993670B136E778F933E6225C62FCA83782F847FEE82F8C1FC3710BB4F8365E7DD41F303C9E6A966C90F2BE1BAEE869B5AEA319F5DEC3128EAD0B1E9EB5F46FE4B4DC5869270549787B53A86385000F84D8833081A096A0AFCB4878154B8D61E45C83FA66F1CA0E7E71C13764D15A466D8E1B6C61323EA7A5142C750075FB783A7BB139C4B6E857E8635FFBF46532B172D3C3E39613C7EF16DFAEEC6E97024E
	6D481A2D9B696A9A26D01B18134556B0DE45E7D37CC28D1E2D6DE2EADBD8DE8AF546EC131135FF1E369F4DE66DD355F1FEC57173B5F8B64E9045E3B9926A4CB9A6A3DBDFEB9C7AD4B71497GAC8708865888908BE05EA556387ACA6DB55AD11FE6FDD95FD72F6C02276CD3BCD20F532BC84EA1790C6431233374925E6675ABA85741997D5E2476BD84F3F449CE59A3A9F61793C61746E4D0A6AFEB337D4A8EEC6311E92D55BEA64384E3EEA246B5DE4D42B5113C4AE937CDD2E4FF8BD6EF3E0ABAB1A11D0191E1G
	F89F580572FA037A6DEAF86F5EA5E475B5836216C2F935AA173B2E6E0467C7205CB2384D8E7B9012A1F926040EA679D0CF56C1FBC72E49181D877285985D8D6F6F1A0CD1G56FC13F1BB405282133137D0586E6BE7050EF6247D9D29FE71122E0EA2ECABE47AE6AE8A5451FCBAF7A3602DG8AC084C094408BG5BG320BCD46C6GAB00D2AC7671F3132BC2589052FBAD36063C619379522BC7994C7075F9CC8EA8A7AC232C68735D597DFB8B7DB8142D5FA74C8FC97B0EEB209BA103BFF30254278614CDD49788
	C9A0FE7F044FB41D3E391A77F87EE47A751F71E82391308E2BG56978B9DFB7AEA89333DB649AB8BEBBB6981C311EDFE0DE3ED7FF19AE917A7E42752A13BE5278DDE3CDC577FF95A643743E117EAA36BA89E7DF2BB980AD7457FECFA87434398DA2B9675AF53FAC9D1BC7641F1C56E1795456E9C71777FB52545BB4365FBB92FEF981157AD40A3840B464C92416BC5AAC7E1CF3DAE5745F1B78D2B38DD7E1E469F65503E25C468EB268563508BEA79DDDA7657AF2BBDFB0D6B9E8AF07C60FB519337E40E78A0F8B6
	47CB96885409589F92B85E24563FCA6B67G1D11G6B81E281CA7688B958ADE703743FD93A7DB44E25F3840E9FE8AFB78ACCC7BE0F8B2E63F21C3E0EDF0C535771CBF121F5F8AACECF07D95D9CFAD6F0CDBAB5B6F1C79C6E87C3AE27D3260629F6A1AC86FF5D0AFC581DF665D33BEDC4D67A6DDF4AC0579E546D64C9546DEEF89F58A3760149B2318F14FF799D3266FC2FEEBC3968EFAE9A2276B07DDC3B473F8FB01A6EB4202D498EF98C84556E9AF7AA8C3F006EE79C6F0D2738720E886CDBF922EDDCD1E01F37
	592AA54AC88DFB74491EE117E7CC36D5FEA1CDC8B55224D2C397222615D69F47772A1283870E14689D0B2A3FD007BD5F9AB0DEEF70818FD51D2932B07A6C279F525247E6389DA4320A6B25320E69FE96395A70606D1463305DA37B07E6FD4E870E4CE84583DD8D9D6F6460972014C8E6BD91F4A66FFF64B64BD3C3AA2B3CEEEA9BB82D8FA7ADE134768FF9DC8E878E5F6A755AC71C322DFA4CB56E15EB685FF607FD6862ECEABF463A300BBFFB68739FEB878DDCFF7F28C8E923AA571C547B982DF470DBCE660CD1
	2F0742031C3E2403775DFA7D9429021BB1F3CE5F7DA98F5E963D8CA471064D1C53792B22C537D10EB7EB660C46251FFF63764A72D618B8176757F7AD3EBDB66EDF781BB4F1963F16A7527895765A6DG5925A2FECD0E47FA913F72A03D9D66E361G5479CAF12FBF634B566C5F5525ACDF1A2E564F8DD33FB2CC7D4BE16A9327C2575707297FBCCC7DDF04297FFE187ADF0729CF7FB2D8BDDB077F0E4F4438CD3EB4AEBB07E474169F49D3A2E7903FB3206E0C64987767A20CC60CD79D1A3387433FE5540F237EE8
	447346C5F57C3804C402B985B6A60B7BD6C9B8265BE39F03239A9E26F85E5B102A4DFB2F81BEB0C73E991E31G4CA61EC844B3110EAE1375E5B8463BF5A2AF1E8965CA006EB2B64E7CB2A693566EC90FE8D7D08E677EF2FFBBEB39365DA52FE837381C0D97D54E5AED0367313DF036206D2295514E3A170DA75AB9209C2D360B99976D5683EE8B00F99FEB573D0F4D9B318F5B95CF0A5CF22D0AFB1F627E8D8E92F798C57E6E63CCEDFE5F6635694644889B7878B17B82731A6712F0DCF30549D8D0A1EC772B846D
	59931C03EB48CE53CAFC7D5FCB54464B8CD71B1882CFF000DE25878EA2BE21C3879311D64CBBF1B4570347769FA63373E847EDF6456591C77B76C96AED2C2EF617CD6E86B5ECD70F93E76C72A491F23852293DAF084EB8E7C88DA287CB85633F2F42AF07C663FD86C6531BD5280374DEGDD1F2F46BB89E7206666931A3C3FA3B519733030C26477DB52519E56C2F993C0ACG4BB516FA4C3CEDE6A52CB1G3359F75235B6BB27121D61A3AB051E5F6D4138747A77A5A15B372B714E6606E38FD52E4B320EF19A57
	2DDCC6DA261BF56FA766E7F49822AB797DC425DF2E5D6F4EB58CGB0D93C264A42D285BADFA5E4E11856134552AAF1D753D459626765060294B67FC3DE11BF0E0231E32A042C63141B1375873B0FB03D3F0978C45C119DD3045D173FA7F468D5C550BDG38087704DCDA92DBE74EFBE3818791F3FFB5AE66DE274E1DB3A1564C389F568500796A28A22D2FD67673B503E754FEA1574B938760F9DD110A6A9B9E9FC5195EDCFC225EC355EBFD641ACDE7037A48F57B196EEE5A9F68A38F1BD0B65B811FD9ADFC642A
	449D9A9FF98A24A9B9C7E007D4F3B3C9A887588BF9F9B0896725FBA191F94EAFD3D0774BE14CE3GE7815CG8B55B9FE179476BE6938AF44FD522554403B19D5557E5C5D33553373F86C6EA40A7A338D556C6EA4CD45BFC3715BB5F826A7ED940F651283A6630283A24FD669891CBF76404CB933EBE616D93F77E775AB8AA8B78574E3FA13510FF255715F4CC2D4DDFFBFA3194F55D7A36EB45627625EBC9F4A4B812281B6GEC87482B857D2E95EB52142C37EF4DDC13E01B16BFEF4C780C39C1793C9DD6BE8335
	B37BB13EBF1C96FCAB35E20D16FA435D9D6A5330B2EC3F9535FA7D0EAB586F595A593A978CD83F5FABB7C6677F046D37C1174EB96358EF6BF55319BF6B3E7C5A5634EAD63F40B5B5DA74F4FE64F8A15155F93F77D457BF26C82CFF01C544AE8BCA51F62DD0F6G4C3388FB48CC43360B213C42A2F47F7EE434F161737ADCF627C2043FEBB3E12C6F7258C76CCE016B2238A11707A60699EEEECA701C755DA97AB96B3FCA514FD95F1F927ADEE6C9CA301CB63BC319CFE339E860EFBDG43BDE6C65CB6A81B6B040D
	7CB09D77BB8B143B81ECG73G16832CD15BBCC75B609981637F0DF5AC3EA775A6E3C93DE8531DFEC1C7EFF0BCFCF657855735B53C8E67357039C36B61106EDC62BEAEA1C8DF84F1D7E7A2AC9E88F5E7D1C85BBC37AB032E0357ABFB29736B109409577DD649A1BA77G1FC8E3B291390E739E93EFF3DCF1355729BC9C4A883409E3A0CBEBBD63FD34FE368C78FDDCEAF09DFBA5D5DF47FE1B0AFCFBDD631EA1B9D00FDE27E7D3D6677766BD0ABC06EC70B3B16D2FC81E91F9169E7E0976E7F5417AA7190369AA13
	D119D7AB23FAD5C6C72683E5F4996432104BE6390E0C58F8AF4D982FC13509819A032E06FEEB6BC5ACD7BA2EED5B2836FD00460C9B205D96356D2989ED5B03AA1DEF9489BAE3212D39E1A6DDA58D41E291D2067E2931A170BB11940A6FE8887CCE642FCBC53CEC053A558D42F76524E1BB1F3AA63BE9F9CA1547A329682B9C5214EB9C1C1A3B1166E6E664A48AG5781F03611514A7C6FF7A751BE06BD5298ACD73FDB9EA23D87316F53E651F7B534DF879083E0063A6A03C2D68FBA704C6CBFB788DA6E4CCC7363
	D9DC2F52796DCB98B3EA330293125D014999D67F03CF33826AF96C4C7B7BA650B6CE74CA039030082E3C2E79C03A3FCEF4637919951DFAB1FF847092F9D01C65FEDC2EBD4B2DB9485AECD66B2523FA753187056FBDC563B2737B60B35F97B8EB9E629A215C2162FE10A7FCF41F0ABBBDAD7AAED5F18FCE0B3ED1AA6E6DE951F74D7BE29D3C8A662D40BC6BDABE40B6F753F2668722FE65784C7AE747C5BD9BB33998471C836565AACE223855D05E24621EA896F3EF7BC06C8909E5B8D6C713FA07CF4B83EAF9D129
	5EFDBA363F5638A4FC4C72DD5DB9025D41DFAFB0BB293B0071359A2760312D39394BE33735C9A33D546AD80C32116E49E81B171BC40C12E2C61B0E0272BA004DCD62CE5837CF64FDD6FCEB99691D63517DCEF413751CE1BB17D749077E335DBC63FD86B6E7E71A18F3G6AA3BEC41C122655D51445F386C2EA60496A3FB5239EFDB71EDF7D759BE654A33DFB623575A9B3F2C0891A7E0C371F6C953CED7F49AA62785E05605F40F21EC8B3FB46BED5C6D62D1F4BF95B5CA972D5CD4DC21ED979A8CFB7146783ACED
	96B905B70703643F28C7E0637D29DC44102B219F43ADBAAAF0DB1B054D5829AD0FB941FBB96DC3CD07305D9C0AFB86207210E8F7B18F75DD018D7972A17C367A99948B59C45B2F4F6B8823D7C9FCED9056C09F049CAE2797607DC37E4E663FCAED7FD51C3F45E4E4733D16631FEF8789B5DFD218791ADA18DE0A7D88769425981ECEF932C4B77DD2290C5A3DD5FC3D8F4B5ABC05652BCAC3FC10756EE8E16BEC65CF5B56B2BEDFF1B1FED3B5A1FB947B1064E0DBC01B6469F3B96CC3D3DC864D5487E1EEE6DE0BD8
	83DFB1467C23903CFF69F2AA33FB8A79C5503E18F7FA967AB2197D2CB8144CC47CA864E6BC8C676943CCE6D82E067717402EFE2E4AEC65906A2658FDB0434F760E56B22D3DB49DE6717540E161A7DF19156B8887AC0E38CAFD0B7BB00BA362D47CDD94FFD903E771C526D9FCD3BE9F6AE28E0B7312B997ED276F881C23G6681AC86088658F0C444A05FC83310B039832B4B5D8B012BC3F31CE17CFD14F6E35F787ED31270A3D1BF7CDB327A4E1220397118A37C7B49236C397049B20AE77CBD12C7FD83541D8538
	8C90819089306628606FB5CAA3E6C0ECF625D5D12421D1FAC3A01843243D9F0D4192636DF71D0237970E4E5E57366860789AD1BF367DE8E0ECF8AF451B0F8546063B73C5ECD8GF5F30E895D095F2B156D66E35C0F722739F5767C2C5F12235AFEA53CDDA3FFF66B74E3BA7ED0AEBEAB248A3EC6CBA735365F1472F9509C65G9F535D6A37FDB646CF7F2C9DA37321EEC3DC10B1A2F95F28D67FDED8CC58F71B49447F0F79477D3834FC0ED74CC2178A9EFB14F43CD4E2A88BB24796BEF6D31BFF0E79EF1D0E675F
	B4CCD37EBD95FC0EE5989AF0BDBBD9087B013BCD68D4552ED2F81E113E18FDC384C84C3175431149B9A2E47B3F45F8FE54ECDFB51AD838664C71D3561E76565E737D6D27CE767636351EBA5F5A7BE96B39FEF5ED9EABEE88513F7B64291E4FCEFEE2559BE1368C379E5543B11E3F07699DF1A7EAC53F2D336F227B63399BDF19484F4FEB93FABB8F55D97CF6C0733E4BFCAB3E418C358F6EA03537F58EFEE64B8DFE0EAF4855BF4717666A670AAAF3C3FF5FE8498D7EFD6387395A6F9B191E5ED10C7BD1B42C4586
	006DG196DA6E3F93B505B9F65225E622F81E67B416336914CA360AFG94B1FFE4DEF0DE9F4F8B7F3B0155F9F8169CF44111F18C4797782772E83E576526C806FBA68FE564310F0CAA2CA947BFC74FD58EF91822591905F4A16E9870E5EB976718F3DD629C63EB973AF8CFBE5A6DDA357CGADEFD64B4579FA7A026DEF1DCEB6694EF12B4EB10C77F777A26F468E13B13BC348036408F3E6250A13F2040C9AD5DC54D151F71E0AFB5CAA7AAED2F11927C55F659DA23ED8D6C2F1D04EBCAEF46C335D7BC9F05F5867
	F139A1941CE2D87AE587575DA57833C53E66CBF76F8DB686E86A89F0C7ACD835CAA32A6E2FAD45184F82F4748238816683AC860882D88B3091E0ABC0F2A71C65819A813A8186G9CGF3GA2BA859F6E7C2A90FC70C3A2B5980F244DB97797E24E464B7DBF5301ABE5216224484EE0B943FB0A035BDFC4F1F87BDBC2E3E3996C46BA6566B93DE545B4A7BEBC6C15D5BB7FF6F1260A6B7176393CF64C85323DA737ABFC7CDE5FA5627768CE91033B3B4245605A3352F6681759650F414B3BD88C7ECBA276400953F8
	56D433C3F497925F9B2D4D8F0C43B157ED6992E7093A3DE87743F00EE074F6D1FB69007A3EAEF166DADE0E7A47E877650423DD8B01E72EB9B0260BE84FF87FCEEDEC8160972973656F734FD7F1BA5CFC2690732D05B10F73F73E1EA558CF5DD71819713A15A5A8AB7C465DAEB96CDF52CFE8D834426AB7168657395F1406573918D2FD1F7FDF2533FD7EDB257ABE7F0F25DA1F4FF006B2541791303B33EA3BA495F7CE45194BCC9C77295D260C2A781DE5A99C5FAD6304821FD706F4CDD25CD90EAB54604E09BD3C
	8C6D86D806A32968DBC173E243F60703157753B6F6A71811AA8F8B25516E16B4F49FD4F1C8B7732F91E5423F3E206AEDCF570D460D57977775D7E00945F46F640B18AED655550D683E389DEE4BC6C72D668F70CB07CEB02C4A52D6D084C7E9F5C99D7CD70F3E1375FD875D82FFFF58629F67D16B750D73940A0267319FAB93F9DFCB37495881E0831887303CDB6C6D4602D0BE77596E59BE17717FFC19483BED6896F937F74A8273EE7792ED5E2C2EBCB06F96B723FE28BCB06F36E4C67D7D6501F9B7511F69C6FE
	01500D4C9E415FAFC26EA9FA40E4D7DFA0E45788E3F5830C824C87086C913933278A058C5676889944940616C1D1D1E89974950516415C22E0B2E07CBED3F83D7C327F1702E219826DD9718712201078D61BCDA1096C76426BA1992A6A75D01B5DB451509C7F1BC7638D4458C392198F6BA1C6F4C4BDA4017A299E928FBE4C676674A1204DA7F8AF112B4420410C4C4066F2ACBD33AAA4C55D471A95E2920925E63539110BGDD9681C253E658480F3EF97035F57FF1469BCF92FB48EE5C567AE58F7566328DEFB9
	794FE5BB09490D576178ABA66042CB9F89DEE54AA177126C61D378092DD711ED2DE21F70AAA407FE3FD40BD9415AEE49BB2AA00D07FA095109BF6CA5BBD935C3F20E5432CD639093C56BB1241450B494501B654FAD2A23E4BD1131F74F094D2B6309211324B8DC122DD39A02102A071814D10F6C9DF5B9ECBE77D1B6907B3595E6050CB0DC3E4ABEB2DBE3059DC8A57A22EF490234ADE91DA0B61F0754521618392DD1DB585AC7A58FCCA5FBFA947C21324BD32E56FD2DC859772FDD39838692471944A5C8753995
	A6B0D5C910651ACE20DAF9798F4FAD7BE54B56986852C9926847D3B0F1263F55A763E30332674E95779726C6EC7ACB244B0D62210B1B816AB7AC0D599DD3705281EB669AD171EC3F21C57A0D2328B041F66B7462C7F3C0CDC900A8C4154AD14DFD4FD96E7F4E5B1FD10E7CDD95D24C568E3A560E482E315A5661E13BD3D687000504618B556131CDCDC01B1D3EFBF3CE3C784EA0A8DE87099F33D8685FBA7A379E7F56D1CC9D45545543EAEEA5A17F85567BFC48F31ECFB584428D817FA5D22CE9163AA1B6ED365E
	A0FA8713A6B64717DD2A5031349B7F64631F825A2C087E5DF3E3B4D74C2499ED9D3FE347B24BD3D5CDEBF3E131F79C8D1ACFEBBC49F3F5A7833F799BEBC46E6681FE69C9F1CEF93281B758E0E7257EB14961D0A44745D6278D4FCDEDD30A6C4FAFF89AF1FC2FE843AB79EFAB9B116EAFB4FD7E9FD0CB8788DC2BB03B3A97GG78C6GGD0CB818294G94G88G88G64FBA1B1DC2BB03B3A97GG78C6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G
	81GBAGGG7497GGGG
**end of data**/
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAdd() {
	if (ivjJButtonAdd == null) {
		try {
			ivjJButtonAdd = new javax.swing.JButton();
			ivjJButtonAdd.setName("JButtonAdd");
			ivjJButtonAdd.setMnemonic('a');
			ivjJButtonAdd.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAdd;
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRemove() {
	if (ivjJButtonRemove == null) {
		try {
			ivjJButtonRemove = new javax.swing.JButton();
			ivjJButtonRemove.setName("JButtonRemove");
			ivjJButtonRemove.setMnemonic('r');
			ivjJButtonRemove.setText("Remove");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemove;
}
/**
 * Return the JLabelAssigned property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAssigned() {
	if (ivjJLabelAssigned == null) {
		try {
			ivjJLabelAssigned = new javax.swing.JLabel();
			ivjJLabelAssigned.setName("JLabelAssigned");
			ivjJLabelAssigned.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelAssigned.setText("Assigned:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAssigned;
}
/**
 * Return the JLabelAvailable property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAvailable() {
	if (ivjJLabelAvailable == null) {
		try {
			ivjJLabelAvailable = new javax.swing.JLabel();
			ivjJLabelAvailable.setName("JLabelAvailable");
			ivjJLabelAvailable.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelAvailable.setText("Available:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAvailable;
}

/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAssigned() {
	if (ivjJScrollPaneAssigned == null) {
		try {
			ivjJScrollPaneAssigned = new javax.swing.JScrollPane();
			ivjJScrollPaneAssigned.setName("JScrollPaneAssigned");
			ivjJScrollPaneAssigned.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAssigned.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPaneAssigned().setViewportView(getAssignedTree());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAssigned;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAvailable() {
	if (ivjJScrollPaneAvailable == null) {
		try {
			ivjJScrollPaneAvailable = new javax.swing.JScrollPane();
			ivjJScrollPaneAvailable.setName("JScrollPaneAvailable");
			ivjJScrollPaneAvailable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getJScrollPaneAvailable().setViewportView(getAvailableTree());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAvailable;
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
	getJButtonAdd().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
	getJScrollPaneAssigned().addMouseListener(ivjEventHandler);
	getAssignedTree().getSortByComboBox().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AddRemoveJTreePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(400, 356);

		java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
		constraintsJButtonAdd.gridx = 2; constraintsJButtonAdd.gridy = 3;
		constraintsJButtonAdd.ipadx = 28;
		constraintsJButtonAdd.insets = new java.awt.Insets(5, 1, 1, 6);
		add(getJButtonAdd(), constraintsJButtonAdd);

		java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
		constraintsJButtonRemove.gridx = 3; constraintsJButtonRemove.gridy = 3;
		constraintsJButtonRemove.ipadx = 4;
		constraintsJButtonRemove.insets = new java.awt.Insets(5, 6, 1, 103);
		add(getJButtonRemove(), constraintsJButtonRemove);

		java.awt.GridBagConstraints constraintsJScrollPaneAvailable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAvailable.gridx = 1; constraintsJScrollPaneAvailable.gridy = 2;
		constraintsJScrollPaneAvailable.gridwidth = 3;
		constraintsJScrollPaneAvailable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAvailable.weightx = 1.0;
		constraintsJScrollPaneAvailable.weighty = 1.0;
		constraintsJScrollPaneAvailable.ipadx = 363;
		constraintsJScrollPaneAvailable.ipady = 109;
		constraintsJScrollPaneAvailable.insets = new java.awt.Insets(0, 5, 4, 10);
		add(getJScrollPaneAvailable(), constraintsJScrollPaneAvailable);

		java.awt.GridBagConstraints constraintsJScrollPaneAssigned = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAssigned.gridx = 1; constraintsJScrollPaneAssigned.gridy = 5;
		constraintsJScrollPaneAssigned.gridwidth = 3;
		constraintsJScrollPaneAssigned.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAssigned.weightx = 1.0;
		constraintsJScrollPaneAssigned.weighty = 1.0;
		constraintsJScrollPaneAssigned.ipadx = 363;
		constraintsJScrollPaneAssigned.ipady = 105;
		constraintsJScrollPaneAssigned.insets = new java.awt.Insets(0, 6, 23, 9);
		add(getJScrollPaneAssigned(), constraintsJScrollPaneAssigned);

		java.awt.GridBagConstraints constraintsJLabelAvailable = new java.awt.GridBagConstraints();
		constraintsJLabelAvailable.gridx = 1; constraintsJLabelAvailable.gridy = 1;
		constraintsJLabelAvailable.ipadx = 55;
		constraintsJLabelAvailable.insets = new java.awt.Insets(6, 5, 0, 0);
		add(getJLabelAvailable(), constraintsJLabelAvailable);

		java.awt.GridBagConstraints constraintsJLabelAssigned = new java.awt.GridBagConstraints();
		constraintsJLabelAssigned.gridx = 1; constraintsJLabelAssigned.gridy = 4;
		constraintsJLabelAssigned.ipadx = 34;
		constraintsJLabelAssigned.insets = new java.awt.Insets(2, 6, 0, 18);
		add(getJLabelAssigned(), constraintsJLabelAssigned);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	Object[] transfers = getAvailableTree().getSelectedObjects();
	
	if( transfers != null )
	{
		for( int i = 0; i < transfers.length; i++ )
		{
			LiteBase movingLite = ((LiteBase)((DBTreeNode)transfers[i]).getUserObject());
			getAssignedTree().treeObjectInsert(movingLite);
			getAvailableTree().treeObjectDelete(movingLite);
		}
	}
}

public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	Object[] transfers = getAssignedTree().getSelectedObjects();
	
	if( transfers != null )
	{
		for( int i = 0; i < transfers.length; i++ )
		{
			LiteBase movingLite = ((LiteBase)((DBTreeNode)transfers[i]).getUserObject());
			getAvailableTree().treeObjectInsert(movingLite);
			getAssignedTree().treeObjectDelete(movingLite);
		}
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AddRemoveJTablePanel aAddRemoveJTablePanel;
		aAddRemoveJTablePanel = new AddRemoveJTablePanel();
		frame.setContentPane(aAddRemoveJTablePanel);
		frame.setSize(aAddRemoveJTablePanel.getSize());
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
		exception.printStackTrace(System.out);
	}
}

//this assumes that the vector contains lite objects
public void pruneTheTrees(Vector assignedObjects) 
{
	if(assignedObjects != null)
	{
		for(int j = 0; j < assignedObjects.size(); j++)
		{
			LiteBase ownedLite = (LiteBase)assignedObjects.elementAt(j);
			getAvailableTree().treeObjectDelete(ownedLite);
		}
	}
}

public void setTreeModels(Integer[] models)
{
	DBTreeModel[] newModels = new DBTreeModel[models.length];
	
	for( int i = 0; i < newModels.length; i++ )
	{
		newModels[i] = ModelFactory.create( models[i].intValue() );
		newModels[i].setAsksAllowsChildren(false);
		if(newModels[i] instanceof DeviceTreeModel)
			((DeviceTreeModel)newModels[i]).setShowPoints(showPoints);
	}
	
	getAssignedTree().setTreeModels(newModels);
	getAvailableTree().setTreeModels(newModels);
}

public void setTreeModels(LiteBaseTreeModel[] models)
{
	getAssignedTree().setTreeModels(models);
	getAvailableTree().setTreeModels(models);
}

public LiteBaseTreeModel[] getAssignedTreeModels()
{
	return getAssignedTree().getTreeModels();
}

public LiteBaseTreeModel[] getAvailableTreeModels()
{
	return getAvailableTree().getTreeModels();
}

public void setAssignedTreeModels(LiteBaseTreeModel[] models)
{
	getAssignedTree().setTreeModels(models);
}

public void setAvailableTreeModels(LiteBaseTreeModel[] models)
{
	getAvailableTree().setTreeModels(models);
}

public LiteBaseTreeModel[] getTreeModels()
{
	return getAssignedTree().getTreeModels();
}

public void setShowPoints(boolean reveal)
{
	showPoints = reveal;
}

//these are for ease of eventhandling in a containing panel
public JComboBox getAssignedSortBy()
{
	return getAssignedTree().getSortByComboBox();
}

public JComboBox getAvailableSortBy()
{
	return getAvailableTree().getSortByComboBox();
}

public javax.swing.JButton getAddButton()
{
	return getJButtonAdd();
}

public javax.swing.JButton getRemoveButton()
{
	return getJButtonRemove();
}

}
