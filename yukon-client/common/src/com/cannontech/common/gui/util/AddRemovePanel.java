package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JList;
import com.cannontech.common.util.CtiUtilities;
import javax.swing.AbstractAction;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import java.awt.event.InputEvent;

public class AddRemovePanel extends javax.swing.JPanel implements com.cannontech.common.gui.dnd.DragAndDropListener, java.awt.event.ActionListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener, javax.swing.event.ListSelectionListener {
	private javax.swing.JButton ivjAddButton = null;
	private javax.swing.JPanel ivjButtonPanel = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JScrollPane ivjJScrollPane2 = null;
	private javax.swing.JLabel ivjLeftListLabel = null;
	private javax.swing.JButton ivjRemoveButton = null;
	private javax.swing.JLabel ivjRightListLabel = null;
	private javax.swing.JList ivjLeftList = null;
	private com.cannontech.common.gui.dnd.DragAndDropJlist ivjRightList = null;
	private Integer rightListMax = null;
	public static final int TRANSFER_MODE = 1;
	public static final int COPY_MODE = 2;
	private int mode = TRANSFER_MODE;
	protected transient com.cannontech.common.gui.util.AddRemovePanelListener fieldAddRemovePanelListenerEventMulticaster = null;
	private static OkCancelDialog dialog = null;
	private static final TreeFindPanel FND_PANEL = new TreeFindPanel();

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AddRemovePanel.this.getAddButton()) 
				connEtoC1(e);
			if (e.getSource() == AddRemovePanel.this.getRemoveButton()) 
				connEtoC2(e);
			if (e.getSource() == AddRemovePanel.this.getAddButton()) 
				connEtoC10(e);
			if (e.getSource() == AddRemovePanel.this.getRemoveButton()) 
				connEtoC12(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == AddRemovePanel.this.getRightList()) 
				connEtoC13(e);
		};
		public void mouseDragged(java.awt.event.MouseEvent e) {
			if (e.getSource() == AddRemovePanel.this.getRightList()) 
				connEtoC3(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {
			if (e.getSource() == AddRemovePanel.this.getRightList()) 
				connEtoC14(e);
		};
		public void mouseExited(java.awt.event.MouseEvent e) {
			if (e.getSource() == AddRemovePanel.this.getRightList()) 
				connEtoC9(e);
		};
		public void mouseMoved(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == AddRemovePanel.this.getRightList()) 
				connEtoC5(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == AddRemovePanel.this.getRightList()) 
				connEtoC11(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == AddRemovePanel.this.getRightList()) 
				connEtoC4(e);
		};
	};
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public AddRemovePanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getAddButton()) 
		connEtoC1(e);
	if (e.getSource() == getRemoveButton()) 
		connEtoC2(e);
	if (e.getSource() == getAddButton()) 
		connEtoC10(e);
	if (e.getSource() == getRemoveButton()) 
		connEtoC12(e);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener com.cannontech.common.gui.util.AddRemovePanelListener
 */
public void addAddRemovePanelListener(com.cannontech.common.gui.util.AddRemovePanelListener newListener) {
	fieldAddRemovePanelListenerEventMulticaster = com.cannontech.common.gui.util.AddRemovePanelListenerEventMulticaster.add(fieldAddRemovePanelListenerEventMulticaster, newListener);
	return;
}
/**
 * Comment
 */
public void addButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	if( rightListGetModel().getSize() < rightListMax.intValue() )
	{
		if( mode == TRANSFER_MODE )
		{
			transferSelection( getLeftList(), getRightList() );
		}
		else
		if( mode == COPY_MODE )
		{
			copySelection( getLeftList(), getRightList() );
		}

		revalidate();
		repaint();
	}
}
/**
 * connEtoC1:  (AddButton.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemovePanel.addButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.addButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (AddButton.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemovePanel.fireAddButtonAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireAddButtonAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (RightList.mouse.mouseReleased(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireRightListMouse_mouseReleased(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc); 
	}
}
/**
 * connEtoC12:  (RemoveButton.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemovePanel.fireRemoveButtonAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireRemoveButtonAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  (RightList.mouse.mouseClicked(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouse_mouseClicked(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireRightListMouse_mouseClicked(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC14:  (RightList.mouse.mouseEntered(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouse_mouseEntered(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireRightListMouse_mouseEntered(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (RemoveButton.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemovePanel.removeButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.removeButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (RightList.mouseMotion.mouseDragged(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouseMotion_mouseDragged(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireRightListMouseMotion_mouseDragged(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (RightList.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> AddRemovePanel.fireRightListListSelection_valueChanged(Ljava.util.EventObject;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireRightListListSelection_valueChanged(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (RightList.mouse.mousePressed(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireRightListMouse_mousePressed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (RightList.mouse.mouseExited(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireRightListMouse_mouseExited(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * This method was created in VisualAge.
 * @param src javax.swing.JList
 * @param dest javax.swing.JList
 */
protected void copySelection(JList src, JList dest) {

	int[] itemsToCopy = src.getSelectedIndices();

	Object[] destItems = new Object[ dest.getModel().getSize() + itemsToCopy.length ];

	int i;
	for( i = 0; i < dest.getModel().getSize(); i++ )
		destItems[i] = dest.getModel().getElementAt(i);

	for( int j = 0; j < itemsToCopy.length; j++, i++ )
		destItems[i] = src.getModel().getElementAt( itemsToCopy[j] );

	dest.setListData(destItems);
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2002 10:49:19 AM)
 * @param newEvent java.util.EventObject
 */
public void drop_actionPerformed(java.util.EventObject newEvent)
{
	//just act like a user pressed the add button
	fireAddButtonAction_actionPerformed( new java.util.EventObject(this) );
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireAddButtonAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAddRemovePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemovePanelListenerEventMulticaster.addButtonAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireLeftListListSelection_valueChanged(java.util.EventObject newEvent) {
	if (fieldAddRemovePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemovePanelListenerEventMulticaster.leftListListSelection_valueChanged(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireRemoveButtonAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAddRemovePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemovePanelListenerEventMulticaster.removeButtonAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireRightListListSelection_valueChanged(java.util.EventObject newEvent) {
	if (fieldAddRemovePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemovePanelListenerEventMulticaster.rightListListSelection_valueChanged(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireRightListMouse_mouseClicked(java.util.EventObject newEvent) {
	if (fieldAddRemovePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemovePanelListenerEventMulticaster.rightListMouse_mouseClicked(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireRightListMouse_mouseEntered(java.util.EventObject newEvent) {
	if (fieldAddRemovePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemovePanelListenerEventMulticaster.rightListMouse_mouseEntered(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireRightListMouse_mouseExited(java.util.EventObject newEvent) {
	if (fieldAddRemovePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemovePanelListenerEventMulticaster.rightListMouse_mouseExited(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireRightListMouse_mousePressed(java.util.EventObject newEvent) {
	if (fieldAddRemovePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemovePanelListenerEventMulticaster.rightListMouse_mousePressed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireRightListMouse_mouseReleased(java.util.EventObject newEvent) {
	if (fieldAddRemovePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemovePanelListenerEventMulticaster.rightListMouse_mouseReleased(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireRightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	if (fieldAddRemovePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemovePanelListenerEventMulticaster.rightListMouseMotion_mouseDragged(newEvent);
}
/**
 * Return the AddButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddButton() {
	if (ivjAddButton == null) {
		try {
			ivjAddButton = new javax.swing.JButton();
			ivjAddButton.setName("AddButton");
			ivjAddButton.setText("Add >>");
			ivjAddButton.setMaximumSize(new java.awt.Dimension(90, 31));
			ivjAddButton.setPreferredSize(new java.awt.Dimension(90, 31));
			ivjAddButton.setFont(new java.awt.Font("dialog", 0, 12));
			ivjAddButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjAddButton.setMinimumSize(new java.awt.Dimension(90, 31));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddButton;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G03FD7AB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF8D455313422C5CBDB3454262F747BF89A7B30CDAD3E66D57A64FB8F7B70ABD65A260AADD63E9AABD536C50D029AB56A4DEEC2C292C8488FA430C09282A490C27E107C11E4730384960810C200A8D932B7494AE6F75D3D01C529FDB3675C39FBF7F3F7B7094847646E1919F34E1CB9B3674C19F3F7055C6D3313937539021018AC487F8EA68AC2D23FA018B236BFC8383817D69884553FA2008DC2
	DDE392A32EB098CF6A8CD942DB4D29D04C05F25F911DE193FC4E91DE5D5E540BC86DFEF201365BA95E9C3A1F03E954CF8AAB6B8EE540F3BDC0BE601AG2681E489094D79B23E9F4A6B654F8809FAC128FB99F9D2BE4CD761974BFC99C0AB84D846F80C4DC619BE854A4893814F2817B96FC2F82EE5525D5CD0A773755DBEC7D87D6B8A7DBA6CCB55BEC149AB3C6E9179D9A87C1CA942A09C37544033297B72D07300F324E24062F0B89D12B8B0DC8145912723E2E854D6B1AA596C8D8DA62B7514B8623CA2F6DB9C
	22DD18778A4A3A6F43D4568E488A650ED5083B67C354CF8214F321DC3E0A647D7D3AA6D91E1917678831F7978515F75AAAAEE754AA1F3C7D2F3E1554379A5B6FEC4E106B2D84FA896038AC5FE8A6D9E62FA6D9EEE23CCB219C87107AAA610F36A03E904AF5G5D2F120C07F6130C3D1FBDA044DF7AC01F99C44661D739ECB3DE75491831FD353E0059487F2563BCAF86DA9CC052EBAB8C25GC7G068136B05B7CBCED8FBCCD1724F626EA134B55E6373A0FBB2D229DBE0342AFC9B26E14B860F4DB85015BDF7507
	659A7290CCF94D5F769078584E8AE4AFD1276E93A469FDFD324658221F28544F145B18731AEFEC426B2837F39F215E96819E6B210FF17C8843F73C6E4373397AB13385ACDF87DADDBC6939F7BD49E2CE3ACB482ABA2DCF5110A56217C7742B63E59FF943A78B57718F56210E6BG5F83E087188A908930708D527122341AF0BA6E82FBF7BBAC76532317DC963754B6EA33DBC5372C6BE34D21F42D853C5E39895763FA19D3231E23E5BFBB252917E5F38E6AE3E5FD442B74427B3EA3E85F79EF0E350D754A9C3E4E
	FC257CCDBE0786851F48FC25C90567F3FBAB63473299E8F381B87FBB0CBF772DC09B319EC53C792DC09B69EEA19B198A3456049586BE3F0D19388EA6C0B97F6D9506CA00A3G43GD381A25E26B94EC95FBFCEBF6AD64DAF2F3BB8FDA3BCC52F6436F4B9BC1245B1A01E3138ED16CBF651A348BCCF2423DEAF79557B93736F816863FD51A13AAD124D690056ADA3A298130773BC1B3E81F95CA2237658C6C468E044A5D03BFFCF4F01275D62117E6132DAA4514741692B5251A62ED84EC00B50GFCDE71B66A2B0B
	2D397360731237C9D743D908ABFF875637F7C8AF0E0CECF89E836396E1796E308D20989677B532510B9F211D7482FF9C400CF7C147024E3084E0EA224EE0546B8C51G3D861DE1A54014A41D6C3BAF76120DF62C7B09D07E44871AB64A6CADD9476C2DD2FE7AEC54437B863C97A082E09E40D200F8007CB5BAC39DC0AF0097A08200573B7F433970C459564B320773AB9CD7344C9F88B8B67A0CE38F24239E16E86083E125C11B87750AFA633247B259B3E51AA1CC1B3E35C41B16237A3CD6030EFBEB7AB87A488B
	02CF16FD125B57B39B50C76741BCAC81D88D40ED6C5E0CC26EFBED960FC85EF6528D0EA2DAFD9647F9FF388E47AEDE919DD20768929DD6786011ED7DA76B567A7CF0502978484F989E5765F6F0940F027F453A8D9C8FED283D1A688FB13AC5125C36CB2312F8DA14A41BE348D77F91A60B47EF497748E3FDB69B471A1122B314G542650D8CF2D4FD1791359693CBC6A6A4115C2764BFBD97CF184787BD3E84E5FEE4658F12AD2CEE941320F7E2D66DE8D5DA37FC4760E5076B3C7330F71585F787BB8F3947D1976
	07FDG7BE53B2CD7EC086BFE27ECF33C5E472C5EEC986FA200B8007CD41D2195E098E08640BC00D8005435BAC365DA525B1916392A7E027981798E59F801ACC36098F68614C97EC03E7A9B222B1B196CA8F7320A96682BA47BA67909761A2351E6207F26E868015603AFA2BB77014E4C60BEEC4E547661D3195ABE5C1B995AC77BB335FC145306B2D1F74E2B8E154F5BB3F13F9BF0BA9CA2DBF814AA82BF6B3C58036330B9EC52D91BF5C814CE5BBE95A1DEDE0BE747FE36277642E77BDA5A672E6E20FD2E6453FB
	0585974BB5636508FB0E69972E657E3AF42DEF1F63B20DE461DAA25A4591D0D43BF354A1F13C076D57B25EE3E0382E3669D837948EDAED231264F414DB58B8AA7823DBF48FBA5DA3223574E34B95CB05652AD44166214244481D78392CF05F3E66C27F13DA39055A3BE88AE8489C3C254632B3E515DC2EF7AE5F416462ED5ED0515C2AA6A7AADD9577219C61769DF7FCA9FA9B3439C525CD4EFCB130C9AEA39E10ABD8C3A7AFFD8C460762D5D713FCBD5E08AFCBFFA8223A5909CBD943E57C5E7EC8AE23EDE8D8B2
	5BBCD2399D7E1C868365F2DE3158C74576E10BE3C89652DB61398A5EAA4BE9D6B3079117FA702BF3512F03907256F1F9FFF46956GF9C71C239E31027DED375B86AE0FD566F1240DD3B846FB512F3560C2D5D58BB4571B3704102B93CF1AB7CCAEEAAD04DCB50ADCFB0A3E23A557F1A71BDD763943ED999A3A815271B6AF7A3599C246BAC546344353026B2E5BADFABCB7CCF754DAF039EA39CC3BAE5F92E2BE3DB66946CDA7EFAC38C4F52427EDE9DF8FAE53A970A84B0DD31452DC08992C14633B0D3938574543
	9E18118631F39A454DEFE6A11D62E679F0506E344B2E8F34E9E938875F77D12EEADFED82DC131C9F6574BF05212F39981ADE9D067ED1987AEDE168BF8FC37FCB987A7A7E507406B0744120F4BE8F4FEF4842F8CA7CE4D4F48C08F8F0B9A6DE13F395AFEE488E201D4185D949013C368163D33B6A4C43713AEC6D786625ECBC67DCD65ADF153DC640DC86EF1347E3F3AD58264BED9B01A3A29E62649C7902E6F50EBC86460179745570CCCD5799F85DCF9311961012961625239E6EB9CA7973C2A85781742673B6A2
	52F9FC4679EC47082FB4834E9C99BE3E7E8CB55FD6B3714D49606DAD4860FC42BA1D418C4079060F93DF7FBA5E9E71F9213CD0610BB8C1FCCB81978710351E731961B9F3BD717D6524526FFA4E37C0664B4854997AB2092F728362F3E5F23EE9191CEFA1BCB33208CF68D5741245796A32B81F8B1E518ADF57B962DBA473454AFC468DBA03F58371CDBBCFFC5E8D1CEF46864E9783CFE3B671AD3AC0FC15591C6FC8B6671B8A4F45G315948F7D539CB40339747FD13611E04C34D4DC914A3BD1E253E7730FABAB4
	63F296376770F92C4B5185640E0F6531F89F7043B934CE0D64284F77A60F47B9E0E329BB59560D396A181D630AF25360892B27C76A024310177CE5F7AE4A0A39BD99AD5B7C0F779E924666AABB2DB649692674C97BD5364276B85B1DD6718838DC3BF224B9E3932F8A2407FF9ED65F274C4F2C5727875143F5F97C33D4FA085936DA4FE5DAD5067E466E00D411DDC343D166D57CEEE5CFCB2ABC06E550863FC3B95346723C39BAC3B9C087G4F677E1665F911378FF0B381787A94D925DE1F9683FEB5C0FE1E4E40
	77358347B00E1AF8E0CC3A55EFC53B186CE9076B742E1CD6126F3E3B0426FFED563C7F1916D922AF4F633A6C497369D428FAD61F8B4075F047B15243EC60D91AC7FAF829C8CB8FD99BE9EDFB6CFCA3BC3F4051C84E2B9FB6D3FE3E845A6E5BC8FA7E09790B69B93A78903779491F0E20BE372B15E67279199B49FEEEBFCEF2AF86DC52A6524B9FEADB65FE3F481107773B7B84755B34097AFD688475AB81AEEA934D576EE275FCAD87FCC9BE2CE179244F521318AF1AD02C2A2C878FED2927B1CDA6CCD574B8F812
	465317CF63C96F26714C845CE2C5DEF7B76A710B1E6F387C4986D3207C13B957A963086E26F1A49550B84EFDC063E8821CD4C063B8FCB6C06F614FFD0A5E27A70752FB58A31FA26F5433A4EF14A2EFD9AF493B9CF0464DA46F0C5EF2FFF9431E9F382C25463A903206BBB7F0B9FF53CBF276ECA6B95D67C84E6900DB2848796439G3D06BF37AAFA7D6BBFC269B56C11D5516B2673A4EF02A26F7D67C95E5AADBA03FD8B491BFF2106641D58F9D616793EFF36E84ABC21032CAC77598BA4F76496127B658BA477B2
	40659AC96E5E3E8AFFBD0F636CC6FA1EEFEB8821677027B652731DFDA42F59C8F94F33556A78EAC1ECDD5078EA2A116FFD91464078EA7FA6ECB79A700B0C94DF5D150B79DE0A2FCE01A030AA40C902E2266EBC14834E8CF239A78F7BE58B08C0F78876FCDC0B17C11B71GD9DBF506D200A3DB290F2772425E77F7FAC35C776FEF893CBB9F5E6A3B0B393EF56C3D8C3F5B7EB50B05E6G9D75142E6017B27CDC951E5BC49F43E3B986E8355B68DEE34951407EA3367977999DD066757EB2265E3600725200FA5C0E
	B2CF23DEB5E2BF3E398A745ED2FD6E18B3995E57C01BC95BF5727BB7CFD5611CE33CE4849C1F2FDF96E06CBF854A33G2281E2GD683E49542388BE95E76EC540A0B7D67ADD8D06C3B7B633A08B8B6B99D7EF0CC3DC09D76947A5763635ED5CC63369752BC1A4D615E7F501661FC58FAB105DA75DEE675D6940E7F5E10577B6E714949F9CF58FA891AF2161F40FA39C5931573D7A7A6A76793E16B1596E979456F0BEB05306F45D99D5606865C99CC8EEB075B69BA0AB195771724E2353F3496F1FF3143F3CA9179
	4BC7453476C694115DDCBE097246CCD8BF9B3AA7271F6DE16BADAF522A778BD6AFE142F2DE7CE0F2F20E042D17D52CD56F5633D82F34F822F26E6F1D1C1C074256EB521473B1D62FE742F2FA4FCDCE4EDB42569B561453F89E6BCD19301C8F1C1F1C1C8F072DB7D3D34E3E8BD8AFEA42F23EFEE1F2F2A677052B37C0D34E6F33FACBA7A0A75FD775DBF06DD88975920AE98F7D5AC63C6327782A5BE9F3C88245D6351BB07F6AF45B06EC8E4255B35C00534DAE51B9CE4A8FFE3F7FD93E767D7ED772356F773F169F
	7A9D1DEF65873B7F676F539CE6B9270C9DBAC3A1G476DB0A12E834AFDBBE84D0CE94318F08A14A3G6682AC82D82E507F4C68C2894F3360593D84BEF714907DD21B7A7D982CBF5E39A040FE27287A9DBB4F1F797511B2417691783E44F3FF016F0AECE534C7B6E6B2FDFBBC220785CB9D9649A2531E1772840DF7A3CED8C6C4016E15520F2D15F9BAA5E7FB15C0714DA739147B3316D06E6F05CA0CDFE7C1B99AE031224B58AA2DF70D1617F01D4417E86BB6E3A74C71CEFFDAAD140FG74297072BB1B8541ED74
	7182ED9BFD32GFD43639CF58F0801F698DB102F50FCD1E517A40E20FA64FB8ED6DF32380744B1F69C476AF3DA307AEF948433F52E7B5F79693EDA311B3D45DA7AF449BA193AD3DB1F91000F8A20AD0072920095AABC6F7BA6F31AFA5EAB497FD20F633CA7GFF46AEBA337E76389FEFB5713633FC1A91784A955EF7CE2847F4C0316121CE34C90DB34769813753EE472C33E2FBCDACC6C2DDB46DA25BF397216DC9D01E8E90390BECEF20B8549ABCFF57D8DDF1593F4AF2E7B1C00FD5E4BF522D163DD6610DE179
	29B86093F6936F1D9F78692406F8E7335CD0967095AA3C033D6AF66B15F6F5ACBFD389FC2D8A6FCF4F7935DBCB3CB5ACB7528DFC7D8A6F0B67556D56A86DFAD8BE4285FCD3945E0ABE3FF62B0877BFD9AEE086704D5ECDFBCA4A96BC37FA95DBCFE765EB0A6DFC5802FB085DF24DB98A1B0D31947325D28B453EF3211DC5GF1GC925BC8666B6F07FD5DC43865D1691187A53AEF1C008E7F59BCCD4578AE5AFC08440BC002525A47F2D75B87FAAEB2962781AE2EC139FDC7905A0476FCB8A602F6678858E74E933
	B848531616CBF805A857A95A0EB278F21A1C48E37B1CF62D583E35CCE768A9231C7183BB54B9E3FB9967192150E30CDA7448B23265AB390136BC2F4CE73F0B4B465AB24FB7D82AF02C31E5BC5F102C606D8C3FDA0567F908D515F4C7113A874EA7FBE81F55D7B31F00F22C029BE23815D0CE5ACB382EC64465C339D6417D4781769EAC14FB955CA1A36D5BF68567A9C55CFCA847A8B8BB7377E5D0DE2D600E9713AC2965043B2F18E449D5F0D5C61225D4413DD4C432B4A9B8DBA349522D606E58CB7D4ED6F099C6
	12AF3A1CECEF7BF13C5706253EEAE5B94E6381D6DE20504F1F70273BCE901D4F2BC9D923AA7741FA364FFF9EFB77851B5767581AE75F278B78EEC69C43DFD761793CFA0B145CAE5062765118BEAD263177D4104C5FBB69AF73CFD9592B501F6F76273F4D4A118A7D01337E745F30F20CC2974E7953B732F202C2BFFA5E1F5EFF5E3F7EED977C6933AF78539F697327BF5DC7F42E0BD9AD74FD31527D60E77BE98D2A6A475C14328FE142352161FD374D5AE699B233950D6FC14BD99C0A75A376539E145D0AED46C0
	F98500D0C939D36FAE3A130D7A61DD02F94AA94D6F384576546B73AB79DCB7D57AF227055BD76BF91F7DA66A538B7439156CAE5C24DEB3F06C0DFAC12800A727AF7023E33C01F9F99F7D95BFBA4AFB40202617342969492A7AFCEC0F6E2431A5BCBAD7307FD7EB506F6F454BE3B2D649F15F89FB6270734F4AE3BB56CEFBFACF9569739D767D0269D00E8418DFC5F334EAB0485DB4DB11F9FBAF6E4076D016A5D514DBC959CAFD54D5136DFF034531AEA8CFD5F06ECD648FF32A49C707D89EB39E4AD9B504F3B39C
	3E7B016FF5F05C9B156A588CF935628EECE722313E6F5C106D97CFA787611D6C3B7C5CFF8A2A51DFA4881ACBE18C0250DFACA8344F2BF1CF9D31F8EDA323A36C0569489A3415BB59DEE0074F5E9A72437EC61C8317DB9C945DEE514ADF2F16792F31B5F785FCDE2D707F4F816CF7506910B84F9236EF9456C28C89F03D96F1BD460EB0722B218F6AA0EC126D52851F1FF9775DF79315715CD63A060D474D4E41FC3C4F6D700D77A696C3C0D092B05E6F33F5F3A1349783406B2D452F8D4874BFB2FA8250D28F008D
	B35CF936FF54C239D54165B3BEBB143D87E80CD39A430DB1D9E30C5B65B14EB9609BA36F63C13617C583FE41811235FE17EF0C470EE3DD07F835D073F628D49A36F94AE41FBFA42A6F02AABFCB559B84ED1FDFF9005BF9027C34CE31A7F29F6BE5E3AEFF8F4EF36F115F7D1965AF7A20BCC54125A8B1424C774847D696210DACD74A2F31B23EC3444B1F97E91D0FA68A58FEF09F7C6E8D69A3180F723877F6A5DF1CG32E49D5499AA813A81DC8749DF9EBEB91EF9633CB99F0C1F7759336367ADBFB79EDE2E373B
	465C250DEF4D1BBB49FA3A895763B154F236174FB848E328C4857F82434FD261F9BC115FCA71449C20E55451DD57455DB807AEA8CF870884188F308CA03E0E4ED3B3364071BA74BDD70F53E5962F08F6D54A0C0FCFB4CD6EBB458F25503E9D7D5FBF92665DA284FDCFAC231E7BC1397C1CF15C4E5E336463FB2594475787B4AF40AC00E800C5G3175B43E25DB4A79787015B613A4D98606595BF2B4B8FC014D0746039F9FDBF7BB0DEDB534155A606F0F4606E0F1311B3DFFDA59909897DFE77856064038B8A91B
	6262EE20C5B750BA32EA07160F67B74A7AE854DE9FDA815F93C09B067275C62D9CE046D6757C4D10F9A26567BC0DBE384FC5660EBDE3BF285C47AEEE64E76C65700CEF64B9CE3E2F5C3AA9543E12AC786FABD9CDBAC389C01EDC2EEB7259BD176363E434A7651DAF857F83B76AD47DCE96373B5FDDD06F9DD97FD62A277682E569E862E36FEE724542FC5F5A36D7D07AE9EE447BD8E56D4177A3BAECE0C6786568527A4A72421A2AB2327959D755FD5725B587FF07C16EB3224937EF713E27157B7A1EF5E0BCFD73
	FA732BFC752A0C4501758EC340D3DAEC35D96C4E216272C228DBD58DF5378B7C7BFD29026F9FEF6FE9CC874A63FAA603FC5978633B050EBF8A7A9DC14695C33ABC64536934783712F81B5F2C754978D8517847F67F81DF3DDD467157DB721E2F5E728974672A77557B30F13C3A64F53F5D605321215957CE62386466EB5EDC767D5D5EC3343EDD5F05B21C31FCBC764DDDC0623B8927A5B71CD0147839BDF8CC7C959E8BC25B5EC34A3B19FBB0F63DA23AA55B00454ED3D6ED96F73753EE9B38A657DB34876B61BB
	DA1107485737050C13D382FCFDB9540BBF6473750CE6788CC674F4A249ED6AC3FBD372DF8265E39FA8263B007CE6BA173ED49CF8AEFDD9D01FAB0BF6841ECB937D6827F7841ECBCD09EA7A4DBB824F25D41F2F2BFFEB95144F2BD41F37355E0838AD14CEB788873F54BE384D8D6F8EFE5F615C2DFD5FB13ADB7BCE6E335D213FF3FB536E605F393DED377ABB37726F5C34605CF403AD5881E6GC4824CEBA6FF49AFC5FF41DF60989B8BF4DA07703E85FFF5C3227E5FAA8DBED6FDD9785F6AC8AF435C76A5A76CC2
	A358BE61B71461FCC9CE97C3F25C46B254119BED1C3352FB7EE54CAF45C10616F31F6DF406D952CC6715BB770CBD433C34074EB0AB1AA93670326F425435D0792B2CECD64AE9596331A12CBFF9BB5DB12EBEA61BC760E34FAB21BC2D3D0574F105613CD01E21607632F3DEA41497A8B821187234CB945C9DF6C5178AAE124DC3DDAB1C0DDA15BB612DD4572A605206C8962F023BBCC46DCDED255C603339944BAF81DC060972C7FB73A87F56E1229C557FEEE439D6A84F8408B271B3387F1E9D57220563FB50136C
	27F0385F5C02611BECEFD1E518C8505CD340CB0E429E458FD7BD16A1253E2B0065A3C006D893795A405E3D415A81E4375B6902ED659A4732EFF749EDC5ED463DC3E853990CG2DG3DGF60029G91GD1G8BG16824481A4356B8C79G65GCDG5DG56F612232426AC049CF20215B93E5B22FEC7A13A9963DC0F3CFF4AB19826E865BD6971AD05C250FD3B2A96B66E6A4A4AB24E7B749E8C4254AF322B6EFFAA4EF41E6A69EAB719AF1EEEBFF552ECEEB31D3AE8B21FB51DBBADE845E121667467F815A97B58
	FC3F7BC7AF68E3FAFBE81F6563FE34A5781A77D8CB78B56FA9161F96E12D6A3966126F1BE3593D1CF3F050A3AAEB6BDFDB3294DC17275B693161BDB50FA93E4B7CEAB6489B55CEB1451A3D9349CBAD05FAAB5BFDB105509198D35C120FFD50FD9B3F877BD936FAEF4F00BA781EC6E587652D1E5C661B7FEF14E867A26FE1EBCC8754696E20B82A4D98BC6E3A40561F6B40BB2D0372F2E5EC7D090672028E9A7F3D7649649EF748FA096B88BCBFBD582E1DDBFC1C69BE8178D3BBC99EB3DB53EA215C5AC93AC8C0B3
	9438ECDF9D8A976F29E5F3C19BD3BBFD32C5F47AE413F3A7E36E9B47372F3CBE49FAF7EE1CDC2E26E6932E41111D01391AA6061F5B99182BF9F8A32D6F7301D6F81856285182ECD3090378F2A87B49758234C97C1D90B8D758BEE5DF8164D1BD27AF589C5CDFFF35B93C3F46EC560ED17E30F9EC0C7254E66D9865194D6A9805631E5B0CF3BB846BEB2F4277F785F7CE414531B691F756E61506957CEBCCAE449F9131C342A7B0392EB2DC2F0CCB5C62431D13F1EB36601A83C39EF63AFD6F8B309C3F4DEE6765CC
	46E3F340922468A3F78B4AE8F3D9D4F297A8B8141B6F5F0FB39F1D8AF30867830EDB4AF033G07E78E6EA75B71E7BBE51F7B7A1EC2A17C1EB131B53F0D5D0F70356F51B25C54356E27705444E3093FE553FB3E738E13AF0B2ABB0742D06751776A60CC7B1EF2261DD6622B77EA28F3F4C03DCB3BFD75EE319785566367E893982A9D0E51D5703F36CC51674D250272F9CE192F1DB99310FB26AA17724E60F87B574A09844F13BC00C7E2F9FD295F647B5C267A7C6846AF7FCC387CCB6D035B1DE5972EEB729A455C
	18630D5EF5BEBC3931AC57CB5921565C3843415E0FDA13C3F1F3527BBA833F2F581AC37947D240B5817481DC871875BE45506DB994C34FFF1FF2851F64044E955C1D93BAD770C4CE68DCC1CAEE68DC41111C096EBB7CF7040F565D8F8BD7548BC232A42CB1D92D054D4D9E37102998F097DB51E22F54B4981E3D5B2097123A04B5FC056F9212F0196E921259AA5EA598E00577622FC06A55A089EB1B1A8AF99255730970B9135B0F837A4B9E6BE826EFBE7947280B9D39FAC18FDD8E0B3C0F642BE8922C97741773
	2C45E42564965679395E437FFE603945FFBE63B188EB3A04823C11BDAD3A5966A7DA71A5D27997A88F8BEBDD78D6B146AEB0AC8FFBA4FB24EBF651AC648E1E42DF0F70C82255C45BAAAC01F96CABE87B7122E87F910BE7D8422D3051ACA4B97037B205ECCE36DB9CC37B799E5B4824CEB43522538AE86582481B633BEED2DA49F9B0F3E7515165738C027E3010E6F7DA2C07AD83F052689256CA43EE51B36C34DB3D2E965ED06AD5B4C68C408C505C96E578B8580A9E58309521AFFBFFB0FD5D0AF507C169DE3730
	1FF16265F80542E1ED9F363821AB515DA561EFFFBA5DA58A6DAB5B24DD3FF9FFD31FDEC81487092ACF733A40EE86ADA3B67BB57850419216ECD2564344C3306070607716C13242G490824F4D0716DC7AA3FF36F1B67D9873E2A12301D2B922A6E9F921DA37BCD0303B60728B4GFA056637AA4DA3CFC5GCF3677763C23CF3CFB896C20C3B00CD4D6323FD56CEFB57E2DE218AA06292A86654689A17F85523DB4E6E6BC0A876174D79C0699147E7442A3F7BD55943792C6F7D8C8E65F7D03C94872F11D989D39A4
	3AEF097A4E56741931DF47B9F22129F8315DEC2F62B9196ADE832CBFDCA9F50C7DA1B6E32CA1A1BACB19CEAC65B056BC76B7FF424A41DA1BD9DD23B2261D3EB1297FE5EA480DBC97853EDA60E733423388E07A18D5057D2A39A424192ED8ECF6F4288630C51347E39BF20856069015136527CF31F8306C50A8F1D576B311F1B1CD8C224AA4FFD512152C6181A6D789563931BA6E174B6F796B18063766468D88CB8771CF7D17B382137F88E8F6B1566677C6E57FD25F07455F3CBD68FDD87FC99E87C8A747FC2737
	89C7B797700BCF52B96ADF0D98F4854BDD1D9E31586D1245FE596430E29E2B6D1AA47AF24D35CD583E07F8E4A227B5B6215C9F2B6A7CBFD0CB8788C6CD7F61059EGGACE0GGD0CB818294G94G88G88G03FD7AB0C6CD7F61059EGGACE0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3F9EGGGG
**end of data**/
}
/**
 * Return the ButtonPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getButtonPanel() {
	if (ivjButtonPanel == null) {
		try {
			ivjButtonPanel = new javax.swing.JPanel();
			ivjButtonPanel.setName("ButtonPanel");
			ivjButtonPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsAddButton = new java.awt.GridBagConstraints();
			constraintsAddButton.gridx = 1; constraintsAddButton.gridy = 1;
			constraintsAddButton.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsAddButton.ipadx = -16;
			constraintsAddButton.insets = new java.awt.Insets(0, 0, 15, 0);
			getButtonPanel().add(getAddButton(), constraintsAddButton);

			java.awt.GridBagConstraints constraintsRemoveButton = new java.awt.GridBagConstraints();
			constraintsRemoveButton.gridx = 1; constraintsRemoveButton.gridy = 2;
			constraintsRemoveButton.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsRemoveButton.ipadx = -16;
			constraintsRemoveButton.insets = new java.awt.Insets(15, 0, 0, 0);
			getButtonPanel().add(getRemoveButton(), constraintsRemoveButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonPanel;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setPreferredSize(new java.awt.Dimension(140, 200));
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getLeftList());
			// user code begin {1}

			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			ivjJScrollPane2.setPreferredSize(new java.awt.Dimension(140, 200));
			ivjJScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getJScrollPane2().setViewportView(getRightList());
			// user code begin {1}

			ivjJScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane2.setToolTipText("Use click-and-drag to reorder the elements in the list.");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
}
/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getLeftList() {
	if (ivjLeftList == null) {
		try {
			ivjLeftList = new javax.swing.JList();
			ivjLeftList.setName("LeftList");
			ivjLeftList.setBounds(0, 0, 160, 120);
			// user code begin {1}
			ivjLeftList.setToolTipText("Alt-S will search.");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftList;
}
/**
 * Method generated to support the promotion of the leftListFont attribute.
 * @return java.awt.Font
 */
public java.awt.Font getLeftListFont() {
	return getLeftList().getFont();
}
/**
 * Return the LeftListLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLeftListLabel() {
	if (ivjLeftListLabel == null) {
		try {
			ivjLeftListLabel = new javax.swing.JLabel();
			ivjLeftListLabel.setName("LeftListLabel");
			ivjLeftListLabel.setText("Available:");
			ivjLeftListLabel.setMaximumSize(new java.awt.Dimension(68, 16));
			ivjLeftListLabel.setPreferredSize(new java.awt.Dimension(68, 16));
			ivjLeftListLabel.setFont(new java.awt.Font("Arial", 1, 14));
			ivjLeftListLabel.setMinimumSize(new java.awt.Dimension(68, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftListLabel;
}
/**
 * Return the RemoveButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRemoveButton() {
	if (ivjRemoveButton == null) {
		try {
			ivjRemoveButton = new javax.swing.JButton();
			ivjRemoveButton.setName("RemoveButton");
			ivjRemoveButton.setText("<< Remove");
			ivjRemoveButton.setMaximumSize(new java.awt.Dimension(90, 31));
			ivjRemoveButton.setPreferredSize(new java.awt.Dimension(90, 31));
			ivjRemoveButton.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRemoveButton.setMinimumSize(new java.awt.Dimension(90, 31));
			ivjRemoveButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRemoveButton;
}
/**
 * Return the RightList property value.
 * @return com.cannontech.common.gui.dnd.DragAndDropJlist
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.dnd.DragAndDropJlist getRightList() {
	if (ivjRightList == null) {
		try {
			ivjRightList = new com.cannontech.common.gui.dnd.DragAndDropJlist();
			ivjRightList.setName("RightList");
			ivjRightList.setBounds(0, 0, 160, 120);
			// user code begin {1}

			ivjRightList.setToolTipText("Use click-and-drag to reorder the elements in the list.  Alt-S will search.");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightList;
}
/**
 * Method generated to support the promotion of the rightListFont attribute.
 * @return java.awt.Font
 */
public java.awt.Font getRightListFont() {
	return getRightList().getFont();
}
/**
 * Return the RightListLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRightListLabel() {
	if (ivjRightListLabel == null) {
		try {
			ivjRightListLabel = new javax.swing.JLabel();
			ivjRightListLabel.setName("RightListLabel");
			ivjRightListLabel.setFont(new java.awt.Font("Arial", 1, 14));
			ivjRightListLabel.setText("Assigned:");
			// user code begin {1}

			ivjRightListLabel.setToolTipText("Use click-and-drag to reorder the elements in the list.  Alt-S will search.");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightListLabel;
}
/**
 * returns the maximum number of entries in the assigned list
 */
public Integer getRightListMax() {
	return rightListMax;
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
	dialog = new OkCancelDialog(
		CtiUtilities.getParentFrame(this),
		"Search",
		true, FND_PANEL );
	
	final AbstractAction searchActionLeftList = new AbstractAction()
	{
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if( !dialog.isShowing() )
			{
				dialog.setSize(250, 120);
				dialog.setLocationRelativeTo( AddRemovePanel.this );
				dialog.show();
		
				if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
				{
					Object value = FND_PANEL.getValue(null);
					boolean found = false;
							
					if( value != null )
					{
						int numberOfRows = getLeftList().getModel().getSize();
						for(int j = 0; j < numberOfRows; j++)
						{
							String objectName = getLeftList().getModel().getElementAt(j).toString();
							if(objectName.compareTo(value.toString()) == 0)
							{
								getLeftList().setSelectedIndex(j);
								getLeftList().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getLeftList().getHeight() * (j+1) - getLeftList().getHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
							//in case they don't know the full name and just entered a partial
							if(objectName.indexOf(value.toString()) > -1 && objectName.indexOf(value.toString()) < 2)
							{
								getLeftList().setSelectedIndex(j);
								getLeftList().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getLeftList().getHeight() * (j+1) - getLeftList().getHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
						}
							
						if( !found )
							javax.swing.JOptionPane.showMessageDialog(
								AddRemovePanel.this, "Unable to find your selected item", "Item Not Found",
								javax.swing.JOptionPane.INFORMATION_MESSAGE );
					}
				}
				dialog.setVisible(false);
			}
		}
	};	
	
	final AbstractAction searchActionRightList = new AbstractAction()
	{
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if( !dialog.isShowing() )
			{
				dialog.setSize(250, 120);
				dialog.setLocationRelativeTo( AddRemovePanel.this );
				dialog.show();
		
				if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
				{
					Object value = FND_PANEL.getValue(null);
					boolean found = false;
							
					if( value != null )
					{
						int numberOfRows = getRightList().getModel().getSize();
						for(int j = 0; j < numberOfRows; j++)
						{
							String objectName = getRightList().getModel().getElementAt(j).toString();
							if(objectName.compareTo(value.toString()) == 0)
							{
								getRightList().setSelectedIndex(j);
								getRightList().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getRightList().getHeight() * (j+1) - getRightList().getHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
							//in case they don't know the full name and just entered a partial
							if(objectName.indexOf(value.toString()) > -1 && objectName.indexOf(value.toString()) < 2)
							{
								getRightList().setSelectedIndex(j);
								getRightList().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getRightList().getHeight() * (j+1) - getRightList().getHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
						}
							
						if( !found )
							javax.swing.JOptionPane.showMessageDialog(
								AddRemovePanel.this, "Unable to find your selected item", "Item Not Found",
								javax.swing.JOptionPane.INFORMATION_MESSAGE );
					}
				}
				dialog.setVisible(false);
			}
		}
	};
	
	//do the secret magic key combo: ALT + S
	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK, true);
	getLeftList().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "FindAction");
	getLeftList().getActionMap().put("FindAction", searchActionLeftList);
	getRightList().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "FindAction");
	getRightList().getActionMap().put("FindAction", searchActionRightList);
	
	

	getRightList().addDragAndDropListener(this);

	// user code end
	getAddButton().addActionListener(ivjEventHandler);
	getRemoveButton().addActionListener(ivjEventHandler);
	getRightList().addMouseListener(ivjEventHandler);
	getRightList().addListSelectionListener(ivjEventHandler);
	getRightList().addMouseMotionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AddRemovePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(410, 285);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 2;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.ipadx = 138;
		constraintsJScrollPane1.ipady = 239;
		constraintsJScrollPane1.insets = new java.awt.Insets(0, 5, 2, 2);
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsButtonPanel = new java.awt.GridBagConstraints();
		constraintsButtonPanel.gridx = 2; constraintsButtonPanel.gridy = 2;
		constraintsButtonPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsButtonPanel.weightx = 1.0;
		constraintsButtonPanel.weighty = 1.0;
		constraintsButtonPanel.insets = new java.awt.Insets(76, 2, 95, 1);
		add(getButtonPanel(), constraintsButtonPanel);

		java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
		constraintsJScrollPane2.gridx = 3; constraintsJScrollPane2.gridy = 2;
		constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane2.weightx = 1.0;
		constraintsJScrollPane2.weighty = 1.0;
		constraintsJScrollPane2.ipadx = 138;
		constraintsJScrollPane2.ipady = 239;
		constraintsJScrollPane2.insets = new java.awt.Insets(0, 2, 2, 4);
		add(getJScrollPane2(), constraintsJScrollPane2);

		java.awt.GridBagConstraints constraintsLeftListLabel = new java.awt.GridBagConstraints();
		constraintsLeftListLabel.gridx = 1; constraintsLeftListLabel.gridy = 1;
		constraintsLeftListLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsLeftListLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsLeftListLabel.insets = new java.awt.Insets(6, 5, 0, 2);
		add(getLeftListLabel(), constraintsLeftListLabel);

		java.awt.GridBagConstraints constraintsRightListLabel = new java.awt.GridBagConstraints();
		constraintsRightListLabel.gridx = 3; constraintsRightListLabel.gridy = 1;
		constraintsRightListLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsRightListLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsRightListLabel.insets = new java.awt.Insets(6, 2, 0, 4);
		add(getRightListLabel(), constraintsRightListLabel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	setRightListMax( new Integer(Integer.MAX_VALUE) );
	// user code end
}
/**
 * 
 * @return javax.swing.ListModel
 */
public javax.swing.ListModel leftListGetModel() {
		return getLeftList().getModel();
}
/**
 * 
 * @return int
 */
public int leftListGetSelectedIndex() {
		return getLeftList().getSelectedIndex();
}
/**
 * 
 * @param text java.lang.String
 */
public void leftListLabelSetText(String text) {
		getLeftListLabel().setText(text);
}
/**
 * 
 */
public void leftListRemoveAll() {
		getLeftList().removeAll();
}
/**
 * 
 */
public void leftListRepaint() {
		getLeftList().repaint();
}
/**
 * 
 */
public void leftListRevalidate() {
		getLeftList().revalidate();
}
/**
 * 
 * @param f java.awt.Font
 */
public void leftListSetFont(java.awt.Font f) {
		getLeftList().setFont(f);
}
/**
 * 
 * @param listData java.lang.Object[]
 */
public void leftListSetListData(java.lang.Object[] listData) {
		getLeftList().setListData(listData);
}
/**
 * 
 * @param listData java.util.Vector
 */
public void leftListSetListData(java.util.Vector listData) {
		getLeftList().setListData(listData);
}
/**
 * 
 * @param model javax.swing.ListModel
 */
public void leftListSetModel(javax.swing.ListModel model) {
		getLeftList().setModel(model);
}
/**
 * This method was created in VisualAge.
 */
public void leftListSetSize(int w, int x, int y, int z) {

	getLeftList().setBounds(w, x, y, z);
	getJScrollPane1().setPreferredSize(new java.awt.Dimension(y, z));
	getJScrollPane1().setViewportView(getLeftList());
	
	
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		AddRemovePanel aAddRemovePanel;
		aAddRemovePanel = new AddRemovePanel();
		frame.add("Center", aAddRemovePanel);
		frame.setSize(aAddRemovePanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseClicked(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getRightList()) 
		connEtoC13(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseMotionListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseDragged(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getRightList()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseEntered(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getRightList()) 
		connEtoC14(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseExited(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getRightList()) 
		connEtoC9(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseMotionListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseMoved(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mousePressed(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getRightList()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseReleased(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getRightList()) 
		connEtoC11(e);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener com.cannontech.common.gui.util.AddRemovePanelListener
 */
public void removeAddRemovePanelListener(com.cannontech.common.gui.util.AddRemovePanelListener newListener) {
	fieldAddRemovePanelListenerEventMulticaster = com.cannontech.common.gui.util.AddRemovePanelListenerEventMulticaster.remove(fieldAddRemovePanelListenerEventMulticaster, newListener);
	return;
}
/**
 * Comment
 */
public void removeButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	if( mode == TRANSFER_MODE )
	{
		transferSelection( getRightList(), getLeftList() );
	}
	else
	if( mode == COPY_MODE )
	{
		removeSelection( getRightList() );
	}

	revalidate();
	repaint();
}
/**
 * This method was created in VisualAge.
 * @param list javax.swing.JList
 */
protected void removeSelection(JList list) {

	javax.swing.ListModel model = list.getModel();

	Object[] items = new Object[model.getSize()];

	for( int i = 0; i < model.getSize(); i++ )
		items[i] = model.getElementAt(i);

	int[] selectedItems = list.getSelectedIndices();

	for( int i = 0; i < selectedItems.length; i++ )
	{
		items[ selectedItems[i] ] = null;
	}

	Object[] itemsRemaining = new Object[ items.length - selectedItems.length ];

	int j = 0;

	for( int i = 0; i < items.length; i++ )
		if( items[i] != null )
			itemsRemaining[j++] = items[i];

	list.setListData(itemsRemaining);
}
/**
 * 
 * @return javax.swing.ListModel
 */
public javax.swing.ListModel rightListGetModel() {
		return getRightList().getModel();
}
/**
 * 
 * @return int
 */
public int rightListGetSelectedIndex() {
		return getRightList().getSelectedIndex();
}
/**
 * 
 * @return java.lang.Object
 */
public Object rightListGetSelectedValue() {
		return getRightList().getSelectedValue();
}
/**
 * 
 * @return java.awt.Dimension
 */
public java.awt.Dimension rightListGetSize() {
		return getRightList().getSize();
}
/**
 * 
 * @param text java.lang.String
 */
public void rightListLabelSetText(String text) {
		getRightListLabel().setText(text);
}
/**
 * 
 */
public void rightListRemoveAll() {
		getRightList().removeAll();
}
/**
 * 
 */
public void rightListRepaint() {
		getRightList().repaint();
}
/**
 * 
 */
public void rightListRevalidate() {
		getRightList().revalidate();
}
/**
 * 
 * @param f java.awt.Font
 */
public void rightListSetFont(java.awt.Font f) {
		getRightList().setFont(f);
}
/**
 * 
 * @param listData java.lang.Object[]
 */
public void rightListSetListData(java.lang.Object[] listData) {
		getRightList().setListData(listData);
}
/**
 * 
 * @param listData java.util.Vector
 */
public void rightListSetListData(java.util.Vector listData) {
		getRightList().setListData(listData);
}
/**
 * 
 * @param model javax.swing.ListModel
 */
public void rightListSetModel(javax.swing.ListModel model) {
		getRightList().setModel(model);
}
/**
 * This method was created in VisualAge.
 */
public void rightListSetSize(int w, int x, int y, int z) {

	getRightList().setBounds(w, x, y, z);
	getJScrollPane2().setPreferredSize(new java.awt.Dimension(y, z));
	getJScrollPane2().setViewportView(getRightList());
	
}
/**
 * Method generated to support the promotion of the leftListFont attribute.
 * @param arg1 java.awt.Font
 */
public void setLeftListFont(java.awt.Font arg1) {
	getLeftList().setFont(arg1);
}
/**
 * This method was created in VisualAge.
 * @param mode int
 */
public void setMode(int mode) {

	if( mode == TRANSFER_MODE ||
		mode == COPY_MODE )
	{
		this.mode = mode;
	}
}
/**
 * This method was created in VisualAge.
 */
public void setPanelSize(int w, int x, int y, int z) {

	this.setBounds(w, x, y, z);
}
/**
 * Method generated to support the promotion of the rightListFont attribute.
 * @param arg1 java.awt.Font
 */
public void setRightListFont(java.awt.Font arg1) {
	getRightList().setFont(arg1);
}
/**
 * sets the maximum number of entries in the assigned list
 */
public void setRightListMax(Integer newValue) {
	this.rightListMax = newValue;
}
/**
 * This method was created in VisualAge.
 * @param src JList
 * @param dest JList
 */
protected void transferSelection(JList src, JList dest) {
		
	copySelection( src, dest );

	removeSelection( src );
}
/**
 * Method to handle events for the ListSelectionListener interface.
 * @param e javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(javax.swing.event.ListSelectionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getRightList()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}

public void setAddRemoveEnabled(boolean enable)
{
	getAddButton().setEnabled(enable);
	getJScrollPane1().setEnabled(enable);
	getJScrollPane2().setEnabled(enable);
	getRemoveButton().setEnabled(enable);
	getLeftListLabel().setEnabled(enable);
	getRightListLabel().setEnabled(enable);
	getLeftList().setEnabled(enable);
	getRightList().setEnabled(enable);
}
}
