package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JList;

public class AddRemovePanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener, javax.swing.event.ListSelectionListener, com.cannontech.common.gui.dnd.DragAndDropListener
{
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

			ivjRightList.setToolTipText("Use click-and-drag to reorder the elements in the list.");
			
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

			ivjRightListLabel.setToolTipText("Use click-and-drag to reorder the elements in the list.");

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

	getRightList().addDragAndDropListener(this);

	// user code end
	getAddButton().addActionListener(this);
	getRemoveButton().addActionListener(this);
	getRightList().addListSelectionListener(this);
	getRightList().addMouseListener(this);
	getRightList().addMouseMotionListener(this);
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
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G64F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF8D455152808C1A3A20DB4A2E2040041C60C1536694AFEC5B76D520A3BE9CBABDD79F6F1172E340BAE35F4BF54D8DFA6496447A0A0C1C1D1C28818C888C9C8A0BF289988C8E054C8E20C0AA6134CCBB2497CB973128C904A1EF36FBBF75ECC5ECCFE2AFE9E5E3B671EF36F39671EFB6E39673E091437BBA6A35510A7C92999127A5F51D4C94AEF162479375E300CF0F1F212D1527C3787E02B9459
	188E4FC428DB7F23A4639669036CACA8AF07F259FDC946D761FD1BF4F5F95BD9F88951CF99544DFBE6736C707DBC76A67513494A8F26664273A500E44061F3D7B7767F3B549DAAFE030AA7C8B5G0E51ECC85D2562BA206C83C8GC86A66742B60194DFA3E3636DA25BBFFF30C346197EF99B6E1BB1AB6890E75F05E8E7519AF5D1B1A550BFD3DE148D5F92221AE99G871FA6352617A0DFD3FF4F3BDDAEC7E9174569F4B995392B3734E740D6BA20586C3535A62B751C6CF08D4ACD9627EC1F4C5A138DD90CDF12
	4CD836A1AEFFA776318C4A2B81B68A7C938C5F826FF60049B69A57BBEF9DD365EE3BF42F147228B3EDF308F14D367171A4597C630A9AF8B6F3A71B035773D157D2DF123190209A60BC40B040EC0057595C976CFE9B1E26CE65044B61F0B9CDEE77F13B5573014BAA5B619D4658292838F3F2174BE315A4BE3FFB8DA5BA7204834ED7BEEEBE2E133843B457716796C82B6EBFEB485451492A38B24362BE2E0B65FDFE1D703E7BC2763D2EAF5096837B7ED452763DEE0AABADC3276F453FA9B6C87D3C1D3CFEFF5F8D
	7DB857D1EC2EAB61BDD5358F0E1F4370919ABC374B7D8C0F65E828CB69A75BE8FB09E4B1274F1236141BF575907D104950E167B2786C41325C769A369DEB67FD3EAAE4097992712BB4F8AE4B13CC96AC67B9E00DGF07BAAAC40F59A69CCB24681AC85D8893091A04F15E4ACF2110D756FBEB4C69BEB32F8146383B63BD576C81C37B07F95F84ABE45E3E9F4FA950B33CBEE35F8EC16CE3B6C15D41A037928034EG3E8AE67BDD50477B32D376D8941B4B892DDB9C32A2FB3C1C26AEFFAB52F8E4D65BE2F3485001
	43ADD13BA772374153EE71AA1F382D96C576D3707A3379B8A70316D6E8919A00F72E170F77223ECE4378BBG5CC28FBFE36D1D15BD60ADEAEB4DAED77F003B85C7A2ADF5211E2FE23EE3B23C4FF6110EBFA8C4DC3ABB49384BCD6DBC1F7F32261D86DB970AEF71DC20B5B6EBA77A62B2B7594CD3EDE4B38D1B66CBA53FB923EBB34C265C5CD62C6E6035934B5AC439F839E4E71B8EFF19DBFF9D0D97F89FA7F57BC87B865AFFC94C55837BF02EA2C16EB800E5C24755FB72394D9E37F8E532520FBCE0E032552FF1
	AE670CAFCBD4FD22EC8781505EEB54BE303DFD9277A5E8EF5B457896B03E5550E75657309F833481CC820883C8865888106FC9B21E86F0FBC877FF5E192069CF6B2B34635B2ABE37EB467B3A0E9E768515C97EE03A1AEFE4CE8BDECBD0654E5054ED8B22A519DFD51F382FEF56E9F32BCE9F41FAC0F8716F141D5BC4E9814AA08F4ACE25C1F64BCEAB3CF8553A2A826CD775D35DAE41D34770EE70EBA74011F8855ED40032DB9445E36B9CD064E6D9D1EC4E9EFFFDA36B4BBB5069763BC46A2B29GF5679A823FA6
	68BFA9C01F5065F2BAE5E61C02C52D7F1479A459AEBBC068932E81274271DF6E45F55DAFDFD0FD22E5AF86F703967BG7AD79E8BA5D8D0FFEE0F4D81A60FAEDADA6BC1FF95ED582151D1B460B026DB824FE48F791E213D64FB8AAF5EA9ADED5FE7505BBB2367B7984A3C5CF7B4F87D3E074BF42ED0DF76B6669BD53C174BFAB195F983A3B3DA6B9B76E3BBD6E85F87B05DCBEBFDC5A15613BFD5977989175D4ED5A9F47C603EADB8A7725783B2ECC368384F8A3DAD5B77F2D0DD2B2AC3DE1F3C8FF5E85778FC2B02
	BAFC7CCB0AF563C01E25G2B3DDC0F790A6A0B995D7C2F08AE814A4B94BF5D5AG3A4F5A092EEC0037F7FA0053C540F385G270B6DA03A35833CBD221386130C66C122DBE19131B860FCG51031CEE8DBCAB07082E200B68CE8FF13A0EA1CEB79B1EEB841D5DCAF49BD53AADBECEE706E7140F684A7B05DEFC1CEE29CAA7DDG5A8BC47735DD48F7015379AEF03A4570CC91F48FB808EE4BC5CED7F41153F540B376A251253B08AED125CBD669F2AFA599DBAE5179604F45E87B6CFC50E7993478CA3DC330FEEBEB
	4FE84E820634AC554F75DE22183F5408B6988365C40064CB34CEFE3909E27E287E98E9753C57C21EE556DE62731DBB6CDFA77957D04C7FEE9176E9063A7343E45FE777EB638E13576B6A323110C7354DE777EB5791473D30BF871E5DB60FD7E984C767A3BB4EF88B654518C8C52BB1E24ED7396AD85D50EF9CC07DE5447FECB8CFA37BEA40E7813E84A00D357775D795688759C2AB650FA659536D72B8E4EB91EA3654B22414B21FDBEAE255A7713D18FC13618D74AF962B75780022381C6D2620A64C215B28ABEE
	653A1F3B2DDE15EF7922D95231AB5B8CFA672D084DEF98A2AEF31DA7DC766BFE787B4634F5DFBD17497570AFC28FA9C0D379B7520333D2CF8F763F11BD7D6A73BAF8FA18531A50D054F9383A1D4E8D13206D443F111E7F507E7769F9F141BB5C9EE493691A133587B746454493F2A9EB62234EBE7023A8A14436CC46228EBAA32F8779385C0FF4105CC5D7E0CDDEA13D7C3632DE6D771CC6B7636D1A775BE521FE232FD03F4FDB285FE444CD22797A32CABBDF75009F860819C47A34F622DCF61BD7E91655FD2214
	6D1AA7FAAD4E9ED5272AD7D055EA5692F36D72B1E62419E80C361EDE856982E85B839A9EDD3F8BBAE91C0BA751B84FF651B857812E70AA9A47773BB05EF338863CF2A97B77045D5655BF52A6BE40BAB28955A6A2B3EA0265E6E46D818D0DAE6F7AAE12777CD5A46FADD612B78AF04B053CA956A0F9CFBA955C2E47A26F6FBF89A7AFB5B43A3CCB7BC85E742BC95E4F7BC95EE300D32EA6F99F6CBFCC72FE60E2D6425E9BBC161E1E70D2F31997DDFECFD7E65ED8FBC0E321A567F2CBF612BBDE48BD4BCEF22F815C
	2EEBC86ED4C7101E1BBC3257BBB6BDBF78FAB8BDD3C3236B79BD87495BF28D493B46C172C682AEC948FB56D992E497BE1B92D6DCAEEB512E6AF0B6419B99452FC4B8C94E94A167C19749D9B9997C7CE412B34AD59AA867B9D8651651944BE5DDE22B8DA32BE8A80CE2393C3FF2113C311369CC1919961C9BDBB559FF16DFBFF964391E670C6632F8A67DDA1EB3FAD16093983ED00367FA29E4F8C687F5F157D21EE3A50B27F227C0DC86D08F50866083081EC2F14C0DEF61DA8A1BC7EA44C56C34581B838ECE144F
	A942B143D9C7AD3FDF04FAE54ED5227369162F027530F8CA605813034A1C6F4D91FCEF8615B7847171B95D5C0C635709A379C687F57C2C75036C60339666E2F22F03F382G1F576707F1DEB1F6293E0E624A4B2734F165124D9586543DEEAE79BAAE5F126B7C31CD0948A5DFFDG636294285B9111E4AC83B0833881A28192A2E80ED6953DB35A9CB123ECF11DG5CE9B88F1D88BE338A1D2E1860DC7C6728F3119C91484775B73F127437B60256C5C47BE879EAFD99E60E4AE71E2A47F73083792CD3471EC763FC
	3F6E18181C0F0F4A675315335702FC5347ADE7DA6744645CBAAADF6CD43D7575B3936EE3216EF12CCEEBEDADEE1FA627354163F21F4100CE22B57757CA6D1ADBB2152F393570CC19CAEBAE2A0AFCE95E75E4B35FE3321A2F9F2FEEBE6E1A18EE5AC7656B38DE0F6FAF0C4FB7EEB9EF33CECC4E052372C56A4A7953BE640B9D371C9D7D931353B9AADF222E1C99F664CB9E371CB176094939E8D43EB53AF2B6B810AFE55CF27E51B1B1B9B70E4A376586BD3E9BD9CCD7F443F865BC6C1A181C26D1790E694A79A863
	EB99071C7C3E67CD169F6A853EE1G0EFBAE9DF1B1D0CE3C017CC91C914F222B203C81A0B7924EEFG7511D47FC3D6FFBE126794701CBA895EE3C4FD2DD1FB0F027CE31513G7B457E295F11BAE88E68A3F31C6DA3F0FF4D73774179FA9EDBBCDE9CBA7F3E36D8BF7F7ED431FE7EFDC371D60E07B634370ED41FD29CBEBF1F7E36DEFE7E8DB6061F3F3219358D479CAF8BF69AAC0AC52DFBC2413B0D91F97A8FAD8ED99DBF4425DFE52BB4A7955709F2096213A733A91F38B41272091B07B11E5E08659BA1F63E11
	665CF0D96F6E2C7EC6BEF7ADB76A5BC0AF6007036A26C3B996A0D10367B1E176C1343B6500DF8BA0CD03589020FA9A4502335E9EB5DEBF698B932FC79E88BD5F5FB920BF5F379E403C2957B56069120367B466408ED1670FBA9B9559016A67B48B993FE2717448A3ECA20171733AD07CBFBE904AEE785C3E582E1D5B8A31FE6E2E541B2F36E9DC6776E97A73B58970D1C1F5F1D0DE82102C41733EFF521E23352BB272C38DAC9F378668B7CC23F3EA0EA5003602E82F309CDA7ACDC9463C1B083636D3BB26CA3116
	FBCE224D6B1CED1A3BBCAE3B9DB36542360F30B88AF5D1F89359F6DCA55A76F9A8ABG11B711ED5FDA954ECF475EB4D2D7DC7607D8DEAC916A178959A33A8246F918E8F32DC83B9268568A5ADFD983E8AB09F6954B87ED843A2C69C47B5DFE2DCE8E8B99F6319C4C8E20AB92340F5A3534D502768B167F28863A5302F60FD3CBDBA3E8E3D98E228D68FA85ED1FABC05EF222FD0A1D7FFDC097A1E82B52705E4FEE39609A00GB87DE624B327D17C9B8DF489GAB8156CD67F1B01F637B0650D7F7FBAC8E185AE637
	5CA52DE63CAFE7902F994A9DG13GE2G165ECCFDCE2D417955D8C3B9479FAE40B6798118DF60F17C4174A07A8A0EDF6A44B5EB163BF96A545209D7FEAA4F1E5DA803BF2F4AABF9FC7FBFC63D783EE8C61271588C4A83BFF9C41B87EE1941E9FC223EFDC82FBEF286596A535941369AB343EF1F0BE70C34D51EBF39067975E5B3F87EA4C36027B17CAA8D1E67D5368F533D43FA28EB798E45930517194DC3F91940CDE53895D0DEA7F059AD081322120C3B22887774CE4415C139DE60BE6F257824C560EEE7F7CE
	31D0CE9438590308CB02722AA85A8F369E8C6D4FF79C547767EF9E545F3F8B8F065F3FCB8E067243DC3E6515241775C26603D524179401738E12DE365CC2B8290274D2A8F09B3713DE2A852E2202F490A1F06D03242B593750BA7822836F60E1DBA9DFF18B3B53E465B8D1BF5392D89FEF21FAEEE3F5429F6E1819E4AC1999E8D375B3C359582EAA767D614C60EF5276B33CF5E670B7E90BAAA92729C05D4A19B42604AA9A7B316F124C3F6B8C1479FFD939C3546F3586565730F224287FF3DFE0FDF6FFE07DE1FB
	E0FD23BD305E63882C0FE065C4D17F00B3307E172C3CC654E738836BDFF7D37BDC97CA9AFDB73AA59A7CF3B47943F2F62FAE76BCCC6856563E6F31D90FDBFA4C4C3B727DEE890B7D11FFD2B46DF7FFE4EDA6C2B999E0EDB46570FC7BA907973FE016E41EF4D677EEF8F5CB05C13A154FF5612D7E9CDE5C4033193C4F6A8C6A33836A23EFC5DCF70656FF61586B8C12D48ACFDE9F9DD00FEB8A737C7E7ADF8454233CD5E95A7A14007A8C8DBF9F5B437BE8ECA98F47C9769F54043C77DE2D0EA9FD96FFB6356E158A
	2FFFCE9D5B5ECC0A9F0E4DA2FDDE4CC4FDAAD00E84081DA56E6E3BC35C5D335D0137D7F8945BC3D9164C22DC4F4CA16AA37FB661B7F3688C57A6F0FF482575B0FC9B2D5129ACB73B9C4AEB84AE0A61709B8D7C7E0263DE9F564601C82B9763AC8F019F8DF89F1581FDE404201D68F7F6FC7D54DD46752200C3CF398D3F973F8E43C235AE66B26E6F8E0B4F6698F0B45BAE4A12EFB66AAD116DCB7570EE1ECD6BB03F8567406D113BE50FC736B2EAC9252FE1FB5692F8DFA66833DF43FE3BDDCE05536CFE8DE9B640
	3BF4FB123169F666DFFB9BC611DF8BE54003702AEA178D707E788BAF3C10AA463338484846636931B9410E59F8B79D710F777EC1B6DE1BB3E83C8F333DA09A5A0B8560FCE5C7A5B13ED666D71321EE95G4771FDB5EB8E445FF388D74C7619FAA81B675098B73FB25A98B3F44638DB9D63709C7F98F99FDF30BEA2G9FB507E46D28760F713A8E64F54AC3793A37CFC5CA2F4DDB2C2E79F7E46DF77BE517D28DE9127E1ADFBA075BF9729C3A1B582B6E5F6718EE24BBA0DE3903561D0D69268C4AAD82B7E31056E2
	6F9D34468A2A58DEA04AA52C0C5F7A7072E25D3358F8815B8F3D869F79C67A8835C6F98C7E4FD5945FA603ACEBG52E3A0E681E808917BDC57D8660D53FEE49DBB6D9C4758E91FF00E05166B6D6208FB4A31793C088972BDB8EEBE9EC3ADE1E795EB8C0F21D2857E278C6F5660F9BC31DE449343D037BA066EC52754609CB65C89E7A8G9F40F400840065F7D2DC7D50C1B84A074F3334385CE6883C6D1AD0190F6F1D0C097D9E6341CC5A37977F43DCA9F14AB329216E95D75F495701B417BF57366CE5F8BE3E1D
	F5B83EB228B3833881A2GE683AC1ECB634BA9AD6163BBE935A9A6C531F47532AF55E8F02DB6F9480F46C3A89FDBD1A60DED993435EAEE60FADCBFB7D4DC7C940B9B52678547459B99BEEFDEF0DCBCA9136262C2280B18C7FE2478285E9ADFB7CF55C72C3EFF4883FCD1D05DB1A8B7456A65DDEF9B524E1FD52571294FC81DBE78593EA207FCC3CCACBF5BA742F3F9AC4FA173BDC45A94EE8F4910827710B5403B91E03BDA4E1D6F37F1B59F110136A33E9F93785BBD28BF6D77DD5C467E758B6DBE31E5569E8335
	97AC4B0E79EA5CBB5F9F7772BD6AA4DEC72B7D7C35996F01051F41EFAD9AECE0B278630D221A32127C436545E45FB1C35A3E2BF30E6AFE370C502676E91F6F5F23F85F773E6B6F7B091DE3691B732DBC61677B3037A0186F9484B7C585D61B456E6AA9A849875E728A605DAD716F68593E287E475BFB8E27D19D57633934EEF7FD6FB6296151E7D2770698D72CBA1E8D9A1D2E6DCB4964ED5EFD4AAF634715E39F5BA36F7B79FA87474E377E341F6FAF65E3670BBC6B67DB37FD2C3AD4674F6C57616EA6FFBB533C
	E3691F7B3765CD98DB14ACA0DFB62F9AE5E8357429DE9ADD1A4B43BC99A0711B08E6458327916A2BB5B3F47CFB0945C0755036F9816D67551998278E4A9E4556E53173D459F10B2749E537F5DDD0791A59D9896F45FD8BE82DAF9C8C3756B30356FAA270ADDF60DF6B2B719D0C68BFD2C9EE53F934B711F713449DC6955DE12CDBC0E750FDC7024F20FF1234E748CF0F841FC1D3836A279E89BE031AD235758F9C89BE0392BF77218F6194296F8FEB5E7FAF6D1B08514269F42B34B97D5B6C035B5CCDB52173EEB3
	EB7473EE33EB7473EEF3EB425F0B5DDD937AF7AB7755E8FF374257479FBF4639A93CAB49D88FE0857081C45EC56B653DDADCAF788B41117B7EC9EB8F5E63602F82956A3F20AE74D80B6BC67FADE1C59D664CBBDD308BB930FD42D75761FCA9AEB7C3F25CBBF528A38F5AB8A7256F4C6A50466DF2B7C373F348FB19F4DE093B0B4EA6FBB2C71ED7AEE652F9E549DD94C7EC48C4234C0D2372DFD9B9DF1427E40E450610FF62F6FA47187A18E84E000F7D6CD14A49564711BE62994E8CE52B40BD484EF4BEA8C7891C
	290AF232F18237740A5025402DEDC5DC6EC2B887ADA45C77070837DA60E61BC8963340BDEF22765A960A6FFEF3A8EE0F835C6A0514AB1A1BC339369DF7D3BEAA1951B6C13997E0525D7C3C9D38E72FFFCF8F4777204B4D12D8B7EF37C8425EFEDD073F61535DD3E0151C01BD0A9F24DAACBD02BF6F9316FBG99965FCDEB6D7AF78E04EA8710CD9E179B36158B9C4BFE5D243655540AFB478AE8E7BDC05E77120CA5G75GEDGF600C9GD1G71GC9GAB815681C871209700C2006AF812237EE4F198B954E4AA
	DB789E0B8D17BE7D462A8973B0DEF57F64BAABC133D577242F867325107BF6F9A5EC5C95E5E545EA2C1A09776E5A0F78B577CE252DA74F35B41EB0195B1BCF1C7B48ECBEEEBA57EEB2FFEA7A8CA7E8FCF37AF52FA456585381771AE650C7C7FC78B54B477D744721FD5EB39F0F6E738C67586FFF40D735DCF02B77581967D0172E6EEE2FACFCEB6E39AD825768EDF2F9EDF87F4DE38A9773DB8A48BBA91EE20A1BB34713030A873E25717E18E2C5FCF0CCB1E58773856ABD9F3FFFBB1495703B9A60416FE0526F21
	9CD5144DBF7F7FF4D4BF6F789873B1BB0027709E0A23D68E060E3B7E447CCF93501E3F07F2F0FD4C7FC4C0B96A9E9A7F23D7A612E75C2B6AA5691E6073538B2D7AF944D7BFE5F9C220DFA564F90C79342CC5309E9711AEFE54073A6032659D9FAD5E534A56GED34AD724BE6DF6417CD7D56AEE7E279956784796E9AB79FBF435F1D0BBE5837A8B8AFF3BF43CFDE941C17792D706F5321EE43A272D1BBBF41B6C59C445D213ACE72BFC11B04E9D260DCE13B48FEEA4723FADEEFED8D3DDE7BDBC7DF2FCA2BFE0CF2
	31F5E40CF225D5BFC63936D59B23287E3F956736877CEB1B203BC960BE9338A856A662BE35D915DE011F4564C27CE999BB945F5DB03906982ECD454D5760BE23583195FD8E8C395765A15EFBDBD9BE5FE63773727D0C46668497A4741148E43439AD9A397FD160D0EE752E66887BC6625EA4A31E8FB86EA506F3838E4F9CFC1D7C3EDF92EB6E154CFCE974BDE3FCBEBF4BA1C9647B9EAE46731A5EDD941E1AF8AC511DC55F98C75ECBEB790053AF638F431D230FD443197608B853FE6872739D299ABB5FE10F1FEF
	7115BD41FC7C9CED82C33543B13A9C7E2FAC967AEC4563017ABED97137F3FCE86C7D4BC3FE3E692631762F1793891DA709401F332A7EA5A1577F7EE34D7B2A1CEF7FCC7817EF358FEEF79676DBF855C731E54C713BFC1B7CF8DA462ADC7DD961FCEE523D213E3B3ABA0B626615C0436FA6EE4D227CE38A6036A4405E87508460CE20987AA7D994C3CFCF20DC4113D961F3853B3343678ABE4E8E1FAB38A6BBFC2EA0A92C8E74007F0D2DB9276E838B0BFFD24AD0A42349EA4DFF77DD2FC75AAC8C3811F934550307
	EB537EEBDE1AC1CAEF140C5C43B7CA6968269B258C66459B25B47070BE7CABB586ADA8D27631E379BC096A7DDA7A9B13BB00827AFBF964C2B3CD7B5D23716D8DF98649GDD764A3C0F0CA1B489568B2E174FD90B99226411B685AC3D1F5ED9755F4B7F3D551BA6999B251DF87B5AACFB5866A7DB7163D775AF641C12325DAE0858B1F601E1F959A3432BDC304BE6A92F7B9C7E75842FA2DBCD342D028B5C4EFE4EF688AF058E1D36F8FB955C8A6B4CD23A937F160F74B22F36DB1CBD07789EDB4724CEB555632215
	585285F937792F16C4AB5B9E583CEF4F19B509E912611414E3F7D92C27ACDDF052E8143215DE0F6C6DF5592DBE77FB3C21AC96D0E1G16864D3DAE060F03ADED018DDB885D6F3BBDF2535ACD27C069BE0FF408D162C5F82920301E6835F820AB595328605FA6F2F98AC55DD5EFAA7B9FF97F557386A9D59DA42ABE4767863B6936B8EC768B7052409216ECD2DE020907E04169455FC003E479C112D115682074BBBFAF3BE561331F338E7C2C0A341B2B92D88F7548AE47A1D3F7374DA90B86C02F507C9B22F924A9
	8D22F959F77376B33FF921936C20C1CAF3141531FF4B593F9578EFB94314B3CCF985A8F73D94763F60FA9F0D19990FD8C1B87D2527E08615FFFB7267F39FBB36FE9D0C6E141441FE5B87133045CF75610023D376CC093F650D9723D65F00F364C6D371E13BAF7B444A592C6DB5487AC7ABE50D30BF44660EB4A4C4EF91530925ED0CF4BB7BF747381503DC2FB15EDDE2CC7B7CE3527E37D9C7EE24E917383740F7332041BFB298B9422B30D429A2651886ADB6BBAE28DA30C51357EB6BF14A565AB04C996A532FD8
	BCD8B6681438AABB184868CCD3C328B2BDD015E4A5C69EE0F215A04FB72B638E35FCA4D047B4BC63B7B7A0AC9D45FFEA3E1D911882C7C0330B31B63FB7AA3E223D8F5BF86D0E1077E11509BC8EB81D683FB762F922EB4E303FEF867818C4BAC77D3A916FA1C2652E1A9D963BDD31587BCDCEAB66310EDFD0E4FF2EB9628C366FA59A35D2BD371CC1397BB4BC7F8FD0CB87886A3BA8B43C9BGGC8D3GGD0CB818294G94G88G88G64F854AC6A3BA8B43C9BGGC8D3GG8CGGGGGGGGGGGGG
	GGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG769BGGGG
**end of data**/
}
}
