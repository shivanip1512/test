package com.cannontech.dbeditor.editor.route;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

/**
 * This type was created in VisualAge.
 */
public class MacroRouteEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener, javax.swing.event.CaretListener {
	private com.cannontech.common.gui.util.AddRemovePanel ivjRoutesAddRemovePanel = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private int rightListItemIndex = getRoutesAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
	private PaoDao paoDao = DaoFactory.getPaoDao();
	private DBPersistentDao persistantDao = DaoFactory.getDbPersistentDao();
public MacroRouteEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	if (newEvent.getSource() == getRoutesAddRemovePanel()) 
		connEtoC1(newEvent);
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	if (e.getSource() == getNameTextField()) 
		connEtoC6(e);
}
/**
 * connEtoC1:  (RoutesAddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
private void connEtoC1(java.util.EventObject arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (RoutesAddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
private void connEtoC2(java.util.EventObject arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
private void connEtoC3(java.util.EventObject arg1) {
	try {
		this.routesAddRemovePanel_RightListMouse_mouseExited(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
private void connEtoC4(java.util.EventObject arg1) {
	try {
		this.routesAddRemovePanel_RightListMouse_mousePressed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
private void connEtoC5(java.util.EventObject arg1) {
	try {
		this.routesAddRemovePanel_RightListMouse_mouseReleased(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> MacroRouteEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC6(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setText("Route Macro Name:");
			ivjNameLabel.setMaximumSize(new java.awt.Dimension(125, 16));
			ivjNameLabel.setPreferredSize(new java.awt.Dimension(125, 16));
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setMinimumSize(new java.awt.Dimension(125, 16));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setMaximumSize(new java.awt.Dimension(2147483647, 24));
			ivjNameTextField.setColumns(20);
			ivjNameTextField.setPreferredSize(new java.awt.Dimension(132, 24));
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setMinimumSize(new java.awt.Dimension(132, 24));
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_ROUTE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension(350, 200);
}
/**
 * Return the RoutesAddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
private com.cannontech.common.gui.util.AddRemovePanel getRoutesAddRemovePanel() {
	if (ivjRoutesAddRemovePanel == null) {
		try {
			ivjRoutesAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjRoutesAddRemovePanel.setName("RoutesAddRemovePanel");
			ivjRoutesAddRemovePanel.setMode( com.cannontech.common.gui.util.AddRemovePanel.COPY_MODE );
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRoutesAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.route.MacroRoute macroRoute = ((com.cannontech.database.data.route.MacroRoute)val);
	macroRoute.setRouteName(getNameTextField().getText());
	
	Integer routeID = macroRoute.getRouteID();
	java.util.Vector macroRouteVector = macroRoute.getMacroRouteVector();
	macroRouteVector.removeAllElements();

	javax.swing.ListModel rightListModel = getRoutesAddRemovePanel().rightListGetModel();
	for( int i = 0; i < rightListModel.getSize(); i++ )
	{
		com.cannontech.database.db.route.MacroRoute mRoute = new com.cannontech.database.db.route.MacroRoute();
		mRoute.setRouteID(routeID);
		mRoute.setSingleRouteID( new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)rightListModel.getElementAt(i)).getYukonID()) );
		mRoute.setRouteOrder( new Integer(i+1) );

		macroRouteVector.addElement( mRoute );	
	}
	
	return val;
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
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	getRoutesAddRemovePanel().addAddRemovePanelListener(this);
	getNameTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("RouteMacroCommunicationRoutes");
		setLayout(new java.awt.GridBagLayout());
		setSize(487, 354);

		GridBagConstraints constraintsRoutesAddRemovePanel = new GridBagConstraints();
		constraintsRoutesAddRemovePanel.gridx = 1; constraintsRoutesAddRemovePanel.gridy = 2;
		constraintsRoutesAddRemovePanel.gridwidth = 2;
		constraintsRoutesAddRemovePanel.insets = new Insets(5, 5, 5, 5);
		constraintsRoutesAddRemovePanel.fill = GridBagConstraints.HORIZONTAL;
		add(getRoutesAddRemovePanel(), constraintsRoutesAddRemovePanel);

		GridBagConstraints constraintsNameLabel = new GridBagConstraints();
		constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 0;
		constraintsNameLabel.anchor = GridBagConstraints.WEST;
		constraintsNameLabel.insets = new Insets(5, 5, 5, 5);
		add(getNameLabel(), constraintsNameLabel);

		GridBagConstraints constraintsNameTextField = new GridBagConstraints();
		constraintsNameTextField.gridx = 1; constraintsNameTextField.gridy = 1;
		constraintsNameTextField.weightx = 1;
		constraintsNameTextField.anchor = GridBagConstraints.WEST;
		constraintsNameTextField.fill = GridBagConstraints.HORIZONTAL;
		constraintsNameTextField.insets = new Insets(5, 5, 5, 5);
		add(getNameTextField(), constraintsNameTextField);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		MacroRouteEditorPanel aMacroRouteEditorPanel;
		aMacroRouteEditorPanel = new MacroRouteEditorPanel();
		frame.add("Center", aMacroRouteEditorPanel);
		frame.setSize(aMacroRouteEditorPanel.getSize());
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
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	if (newEvent.getSource() == getRoutesAddRemovePanel()) 
		connEtoC2(newEvent);
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
	if (newEvent.getSource() == getRoutesAddRemovePanel()) 
		connEtoC3(newEvent);
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
	if (newEvent.getSource() == getRoutesAddRemovePanel()) 
		connEtoC4(newEvent);
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
	if (newEvent.getSource() == getRoutesAddRemovePanel()) 
		connEtoC5(newEvent);
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
}

/**
 * 
 * @param newEvent
 */
public void routesAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}

/**
 * 
 * @param newEvent
 */
public void routesAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
	rightListItemIndex = getRoutesAddRemovePanel().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}
/**
 * 
 * @param newEvent
 */
public void routesAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
	int indexSelected = getRoutesAddRemovePanel().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		Object itemSelected = new Object();
		java.util.Vector destItems = new java.util.Vector( getRoutesAddRemovePanel().rightListGetModel().getSize() + 1 );

		for( int i = 0; i < getRoutesAddRemovePanel().rightListGetModel().getSize(); i++ )
			destItems.addElement( getRoutesAddRemovePanel().rightListGetModel().getElementAt(i) );

		itemSelected = destItems.elementAt( rightListItemIndex );
		destItems.removeElementAt( rightListItemIndex );
		destItems.insertElementAt( itemSelected, indexSelected );
		getRoutesAddRemovePanel().rightListSetListData(destItems);

		getRoutesAddRemovePanel().revalidate();
		getRoutesAddRemovePanel().repaint();

		// reset the values
		rightListItemIndex = -1;
		fireInputUpdate();
	}

	rightListDragging = false;

	return;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {

	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	java.util.Vector availableRoutes = null;
	java.util.Vector assignedRoutes = null;
	synchronized(cache)
	{
		java.util.Vector macroRoutesVector = ((com.cannontech.database.data.route.MacroRoute)val).getMacroRouteVector();
		List<LiteYukonPAObject> allRoutes = cache.getAllRoutes();

		assignedRoutes = new java.util.Vector();
		int singleRouteID;
		for(int i=0;i<macroRoutesVector.size();i++)
		{
			singleRouteID = ((com.cannontech.database.db.route.MacroRoute)macroRoutesVector.get(i)).getSingleRouteID().intValue();
			for(int j=0;j<allRoutes.size();j++)
			{
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)allRoutes.get(j)).getYukonID() == singleRouteID )
				{
					assignedRoutes.addElement(allRoutes.get(j));
					break;
				}
			}
		}

		availableRoutes = new java.util.Vector();
		for (LiteYukonPAObject liteRoute : allRoutes) {
			if( liteRoute.getPaoType() != PaoType.ROUTE_MACRO ) {
				availableRoutes.addElement(liteRoute);
			}
		}		
	}

	com.cannontech.common.gui.util.AddRemovePanel routesPanel = getRoutesAddRemovePanel();
	routesPanel.leftListRemoveAll();
	routesPanel.rightListRemoveAll();

	routesPanel.leftListSetListData(availableRoutes);
	routesPanel.rightListSetListData(assignedRoutes);

	getNameTextField().setText( ((com.cannontech.database.data.route.MacroRoute)val).getRouteName() );
}

public boolean isInputValid() {
    
    // This code is going to find all RPT 850's that are not at the end of the list and move them there.
    // The user will be warned if any routes are moved, and a validation error will be returned.
    boolean firstNon850Found = false;
    boolean listModified = false;
    
    LiteYukonPAObject itemSelected;
    javax.swing.ListModel rightListModel = getRoutesAddRemovePanel().rightListGetModel();
    java.util.Vector<LiteYukonPAObject> newList = new java.util.Vector<LiteYukonPAObject>( rightListModel.getSize() );
    
    for( int i = 0; i < rightListModel.getSize(); i++ ) {
        if( rightListModel.getElementAt(i) instanceof LiteYukonPAObject )
        newList.addElement( (LiteYukonPAObject)rightListModel.getElementAt(i) );
    }
    
    for( int i = rightListModel.getSize()-1; i >= 0; i-- ) {
        LiteYukonPAObject device = paoDao.getLiteYukonPAO((((com.cannontech.database.data.lite.LiteYukonPAObject)rightListModel.getElementAt(i)).getPaoIdentifier().getPaoId()));
        YukonPAObject yukonPaobject = (YukonPAObject)persistantDao.retrieveDBPersistent(device);

        // if it is a CCU Route, check if it has a RPT 850.
        boolean routeHas850 = false;
        if(yukonPaobject instanceof CCURoute) {
            java.util.Vector<RepeaterRoute> repeaterRoutes =  ((CCURoute)yukonPaobject).getRepeaterVector();

            List<Integer> paoIds = Lists.newArrayList();
            
            
            for( RepeaterRoute route : repeaterRoutes ) {
                paoIds.add(route.getDeviceID());
            }
            
            List<PaoIdentifier> paoIdentifiers = paoDao.getPaoIdentifiersForPaoIds(paoIds);
            
            for (PaoIdentifier identifier : paoIdentifiers) {
                if(identifier.getPaoType() == PaoType.REPEATER_850) {
                    // We have a repeater 850, this one needs to be at the end of the list!
                    routeHas850 = true;
                    break;
                }
            }
        }
        
        if (!routeHas850) {
            firstNon850Found = true;
        }
        
        // if we already found the first non 850, and we found another who has it, it must be moved!
        if (firstNon850Found && routeHas850) {
            itemSelected = newList.elementAt( i );
            newList.removeElementAt(i);
            newList.add(itemSelected);
            listModified = true;
        }
            
    }
    
    if (listModified) {
        getRoutesAddRemovePanel().rightListSetListData(newList);

        getRoutesAddRemovePanel().revalidate();
        getRoutesAddRemovePanel().repaint();

        // reset the values
        rightListItemIndex = -1;
        fireInputUpdate();
        setErrorString("Macro routes containing Repeater 850's automatically moved to end of macro list. Route was not saved.");
        return false;
    }
    
    return true;
}

}
