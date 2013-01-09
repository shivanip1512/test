package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionListener;

import com.cannontech.common.gui.dnd.DragAndDropListener;
import com.cannontech.common.util.SwingUtil;

public class AddRemovePanel extends JPanel implements DragAndDropListener, MouseListener, MouseMotionListener, ListSelectionListener {
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

	class IvjEventHandler implements ActionListener, MouseListener, MouseMotionListener, ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AddRemovePanel.this.getAddButton()) {
                if(getLeftList().getSelectedValues().length > 0) {
                    if( addSelectedListItems(e) ){
                        connEtoC10(e);
                    }
                }
            }
			if (e.getSource() == AddRemovePanel.this.getRemoveButton()) {
                if(getRightList().getSelectedValues().length > 0) {
    				if( removeSelectedListItems(e) ){
    				    connEtoC12(e);
    				}
                };
            }
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
public AddRemovePanel() {
	super();
	initialize();
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
 * @return boolean
 */
protected boolean validateAddAction() {
    return true;
}

/**
 * @return boolean
 */
protected boolean validateRemoveAction() {
    return true;
}

/**
 * connEtoC1:  (AddButton.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemovePanel.addButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private boolean addSelectedListItems(java.awt.event.ActionEvent arg1) {
	boolean actionWorked = true;
    
    try {
	    actionWorked = validateAddAction();
	    if( actionWorked ) {
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
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	
	return actionWorked;
}
/**
 * connEtoC10:  (AddButton.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemovePanel.fireAddButtonAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		this.fireAddButtonAction_actionPerformed(new java.util.EventObject(this));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (RightList.mouse.mouseReleased(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
private void connEtoC11(java.awt.event.MouseEvent arg1) {
	try {
		this.fireRightListMouse_mouseReleased(new java.util.EventObject(this));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc); 
	}
}
/**
 * connEtoC12:  (RemoveButton.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemovePanel.fireRemoveButtonAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		this.fireRemoveButtonAction_actionPerformed(new java.util.EventObject(this));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  (RightList.mouse.mouseClicked(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouse_mouseClicked(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
private void connEtoC13(java.awt.event.MouseEvent arg1) {
	try {
		this.fireRightListMouse_mouseClicked(new java.util.EventObject(this));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC14:  (RightList.mouse.mouseEntered(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouse_mouseEntered(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
private void connEtoC14(java.awt.event.MouseEvent arg1) {
	try {
		this.fireRightListMouse_mouseEntered(new java.util.EventObject(this));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (RemoveButton.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemovePanel.removeButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private boolean removeSelectedListItems(java.awt.event.ActionEvent arg1) {
    boolean actionWorked = true;
    
    try {
	    actionWorked = validateRemoveAction();
	    if( actionWorked ) {
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
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	
	return actionWorked;
}
/**
 * connEtoC3:  (RightList.mouseMotion.mouseDragged(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouseMotion_mouseDragged(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	try {
		this.fireRightListMouseMotion_mouseDragged(new java.util.EventObject(this));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (RightList.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> AddRemovePanel.fireRightListListSelection_valueChanged(Ljava.util.EventObject;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
private void connEtoC4(javax.swing.event.ListSelectionEvent arg1) {
	try {
		this.fireRightListListSelection_valueChanged(new java.util.EventObject(this));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (RightList.mouse.mousePressed(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
private void connEtoC5(java.awt.event.MouseEvent arg1) {
	try {
		this.fireRightListMouse_mousePressed(new java.util.EventObject(this));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (RightList.mouse.mouseExited(java.awt.event.MouseEvent) --> AddRemovePanel.fireRightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
private void connEtoC9(java.awt.event.MouseEvent arg1) {
	try {
		this.fireRightListMouse_mouseExited(new java.util.EventObject(this));
	} catch (java.lang.Throwable ivjExc) {
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
public javax.swing.JButton getAddButton() {
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAddButton;
}

/**
 * Return the ButtonPanel property value.
 * @return javax.swing.JPanel
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjButtonPanel;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setPreferredSize(new java.awt.Dimension(140, 200));
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getLeftList());
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			ivjJScrollPane2.setPreferredSize(new java.awt.Dimension(140, 200));
			ivjJScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getJScrollPane2().setViewportView(getRightList());

			ivjJScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane2.setToolTipText("Use click-and-drag to reorder the elements in the list.");

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
}
/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
public javax.swing.JList getLeftList() {
	if (ivjLeftList == null) {
		try {
			ivjLeftList = new javax.swing.JList();
			ivjLeftList.setName("LeftList");
			ivjLeftList.setBounds(0, 0, 160, 120);
			ivjLeftList.setToolTipText("Alt-S will search.");
			
		} catch (java.lang.Throwable ivjExc) {
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLeftListLabel;
}
/**
 * Return the RemoveButton property value.
 * @return javax.swing.JButton
 */
public javax.swing.JButton getRemoveButton() {
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoveButton;
}
/**
 * Return the RightList property value.
 * @return com.cannontech.common.gui.dnd.DragAndDropJlist
 */
public com.cannontech.common.gui.dnd.DragAndDropJlist getRightList() {
	if (ivjRightList == null) {
		try {
			ivjRightList = new com.cannontech.common.gui.dnd.DragAndDropJlist();
			ivjRightList.setName("RightList");
			ivjRightList.setBounds(0, 0, 160, 120);

			ivjRightList.setToolTipText("Use click-and-drag to reorder the elements in the list.  Alt-S will search.");
			
		} catch (java.lang.Throwable ivjExc) {
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
private javax.swing.JLabel getRightListLabel() {
	if (ivjRightListLabel == null) {
		try {
			ivjRightListLabel = new javax.swing.JLabel();
			ivjRightListLabel.setName("RightListLabel");
			ivjRightListLabel.setFont(new java.awt.Font("Arial", 1, 14));
			ivjRightListLabel.setText("Assigned:");

			ivjRightListLabel.setToolTipText("Use click-and-drag to reorder the elements in the list.  Alt-S will search.");

		} catch (java.lang.Throwable ivjExc) {
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
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
private void initConnections() throws java.lang.Exception {
	dialog = new OkCancelDialog(SwingUtil.getParentFrame(this), "Search", true, FND_PANEL);

	final AbstractAction searchActionLeftList = new AbstractAction()
	{
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if( !dialog.isShowing() )
			{
				dialog.setSize(250, 120);
				dialog.setLocationRelativeTo( AddRemovePanel.this );
				dialog.setVisible(true);
		
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
				dialog.setVisible(true);
		
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

	getAddButton().addActionListener(ivjEventHandler);
	getRemoveButton().addActionListener(ivjEventHandler);
	getRightList().addMouseListener(ivjEventHandler);
	getRightList().addListSelectionListener(ivjEventHandler);
	getRightList().addMouseMotionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("AddRemovePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(410, 285);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 2;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.ipadx = 130;
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
		constraintsJScrollPane2.ipadx = 130;
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
	setRightListMax( new Integer(Integer.MAX_VALUE) );
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
public void leftListSetListData(java.util.Vector<?> listData) {
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
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseClicked(java.awt.event.MouseEvent e) {
	if (e.getSource() == getRightList()) 
		connEtoC13(e);
}
/**
 * Method to handle events for the MouseMotionListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseDragged(java.awt.event.MouseEvent e) {
	if (e.getSource() == getRightList()) 
		connEtoC3(e);
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseEntered(java.awt.event.MouseEvent e) {
	if (e.getSource() == getRightList()) 
		connEtoC14(e);
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseExited(java.awt.event.MouseEvent e) {
	if (e.getSource() == getRightList()) 
		connEtoC9(e);
}
/**
 * Method to handle events for the MouseMotionListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseMoved(java.awt.event.MouseEvent e) {
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mousePressed(java.awt.event.MouseEvent e) {
	if (e.getSource() == getRightList()) 
		connEtoC5(e);
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseReleased(java.awt.event.MouseEvent e) {
	if (e.getSource() == getRightList()) 
		connEtoC11(e);
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
public void rightListSetListData(java.util.Vector<?> listData) {
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
public void valueChanged(javax.swing.event.ListSelectionEvent e) {
	if (e.getSource() == getRightList()) 
		connEtoC4(e);
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
