package com.cannontech.dbeditor.wizard.copy.lm;

/**
 * This type was created in VisualAge.
 */

import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JLabel;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.device.lm.IGroupRoute;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.yukon.IDatabaseCache;

/*
 * @author jdayton
 * GUI NOTE: Websphere GUI builder over a previous Visual Age code shell
 */
public class LMGroupCopyNameRoutePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener 
{
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjJComboBoxRoutes = null;
	private javax.swing.JLabel ivjJLabelRoute = null;
	private JLabel jLabelErrorMessage = null;

	class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMGroupCopyNameRoutePanel.this.getJComboBoxRoutes()) 
				connEtoC1(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMGroupCopyNameRoutePanel.this.getJTextFieldName()) 
				connEtoC2(e);
		};
	};


public LMGroupCopyNameRoutePanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {}

/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */

public void caretUpdate(javax.swing.event.CaretEvent e) {
	if (e.getSource() == getJTextFieldName()) 
		connEtoC2(e);
}
/**
 * connEtoC1:  (JComboBoxOperationalState.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private JLabel getJLabelErrorMessage()
{
   if( jLabelErrorMessage == null )
   {
       jLabelErrorMessage = new javax.swing.JLabel();
       jLabelErrorMessage.setFont(new java.awt.Font("dialog.bold", 1, 10));
       jLabelErrorMessage.setMaximumSize(new java.awt.Dimension(350, 60));
       jLabelErrorMessage.setPreferredSize(new java.awt.Dimension(350, 60));
       jLabelErrorMessage.setMinimumSize(new java.awt.Dimension(350, 60));            
   }
   
   return jLabelErrorMessage;
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxRoutes() {
	if (ivjJComboBoxRoutes == null) {
		try {
			ivjJComboBoxRoutes = new javax.swing.JComboBox();
			ivjJComboBoxRoutes.setName("JComboBoxRoutes");
			ivjJComboBoxRoutes.setPreferredSize(new java.awt.Dimension(204, 23));
			ivjJComboBoxRoutes.setMinimumSize(new java.awt.Dimension(204, 23));
			// user code begin {1}
			ivjJComboBoxRoutes.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					fireInputUpdate(); 
				}
			});
			
			ivjJComboBoxRoutes.setVisible(false);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxRoutes;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRoute() {
	if (ivjJLabelRoute == null) {
		try {
			ivjJLabelRoute = new javax.swing.JLabel();
			ivjJLabelRoute.setName("JLabelRoute");
			ivjJLabelRoute.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelRoute.setText("Communication Route: ");
			ivjJLabelRoute.setMaximumSize(new java.awt.Dimension(131, 23));
			ivjJLabelRoute.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			ivjJLabelRoute.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRoute;
}
/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("New Name:");
			ivjJLabelName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setToolTipText("Name of Group");
			// user code begin {1}

			ivjJTextFieldName.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
						PaoUtils.ILLEGAL_NAME_CHARS) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMGroup group = (LMGroup)o;
	int previousGroupID = group.getPAObjectID().intValue();
	
    PaoDao paoDao = DaoFactory.getPaoDao();
	group.setPAObjectID(paoDao.getNextPaoId());
	
	group.setPAOName( getJTextFieldName().getText() );
	
	//only set the route ID for certain LmGroups
	if( group instanceof IGroupRoute)
	{
		((IGroupRoute) group).setRouteID( 
			new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxRoutes().getSelectedItem()).getYukonID()) );
	}
	
	com.cannontech.database.data.multi.MultiDBPersistent objectsToAdd = new com.cannontech.database.data.multi.MultiDBPersistent();
	objectsToAdd.getDBPersistentVector().add(group);
	
    List<LitePoint> points = DaoFactory.getPointDao().getLitePointsByPaObjectId(previousGroupID);
	PointDao pointDao = DaoFactory.getPointDao();

    for (LitePoint litePoint : points) {
		PointBase pointBase = (PointBase) com.cannontech.database.data.lite.LiteFactory.createDBPersistent(litePoint);
		try
		{
			com.cannontech.database.Transaction t =
				com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, pointBase);
			t.execute();
		}
		catch (com.cannontech.database.TransactionException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		pointBase.setPointID(pointDao.getNextPointId());
		pointBase.getPoint().setPaoID(group.getPAObjectID());
		objectsToAdd.getDBPersistentVector().add(pointBase);
	}	
	
	return objectsToAdd;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldName().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMGroupCopyNameSettingsPanel");
		java.awt.GridBagConstraints consGridBagConstraints1 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints2 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints4 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints5 = new java.awt.GridBagConstraints();
		
		//name label
		consGridBagConstraints1.gridy = 0;
		consGridBagConstraints1.gridx = 0;
		consGridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		consGridBagConstraints1.insets = new java.awt.Insets(0,20,20,0);
		
		//name textfield
		consGridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		consGridBagConstraints2.weightx = 1.0;
		consGridBagConstraints2.gridwidth = 2;
		consGridBagConstraints2.gridy = 0;
		consGridBagConstraints2.gridx = 1;
		consGridBagConstraints2.insets = new java.awt.Insets(0,0,20,20);
		
		//route label
		consGridBagConstraints3.gridy = 1;
		consGridBagConstraints3.gridx = 0;
		consGridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
		consGridBagConstraints3.insets = new java.awt.Insets(0,20,20,0);
		
		//route combo
		consGridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints4.weightx = 1.0;
        consGridBagConstraints4.gridwidth = 2;
        consGridBagConstraints4.gridy = 1;
        consGridBagConstraints4.gridx = 1;
        consGridBagConstraints4.insets = new java.awt.Insets(0,0,20,20);
		
		//error label
		consGridBagConstraints5.gridx = 0;
		consGridBagConstraints5.gridy = 2;
		consGridBagConstraints5.weightx = 1.0;
		consGridBagConstraints5.anchor = GridBagConstraints.WEST;
		consGridBagConstraints5.gridwidth = GridBagConstraints.REMAINDER;
		consGridBagConstraints5.insets = new java.awt.Insets(0, 20, 0, 0);
		
		setLayout(new java.awt.GridBagLayout());
		this.add(getJLabelName(), consGridBagConstraints1);
		this.add(getJTextFieldName(), consGridBagConstraints2);
		this.add(getJLabelRoute(), consGridBagConstraints3);
		this.add(getJComboBoxRoutes(), consGridBagConstraints4);
		this.add(getJLabelErrorMessage(), consGridBagConstraints5);
		setSize(412, 392);

		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	boolean isValid;
	String newName = getJTextFieldName().getText();

    //check to make sure a unique name has been entered
    if(isUniquePao(newName, PaoCategory.DEVICE.toString(), PaoClass.GROUP.toString())){
        setErrorString("");
        isValid = true;
    } else {
        setErrorString("(The name \'" + newName + "\' is already in use.)");
        isValid = false;
    }
    
    if( newName == null || newName.length() <= 0 ) {
        setErrorString("(The Name text field must be filled in.)");
        isValid = false;
    }
    
    getJLabelErrorMessage().setText(getErrorString());
    return isValid;
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMGroupCopyNameRoutePanel aLMGroupBasePanel;
		aLMGroupBasePanel = new LMGroupCopyNameRoutePanel();
		frame.setContentPane(aLMGroupBasePanel);
		frame.setSize(aLMGroupBasePanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	LMGroup group = (LMGroup)o;

	getJTextFieldName().setText( group.getPAOName() + "(copy)");

	if(group instanceof IGroupRoute)
	{
		getJComboBoxRoutes().setVisible(true);
		getJLabelRoute().setVisible(true);
		
		int groupRouteId = ((IGroupRoute) group).getRouteID();
		IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{
			java.util.List<LiteYukonPAObject> routes = cache.getAllRoutes();
			for( LiteYukonPAObject route : routes ) {
			    getJComboBoxRoutes().addItem(route);
			    if (route.getYukonID() == groupRouteId) {
			        getJComboBoxRoutes().setSelectedItem(route);
			    }
			}
		}
	}
	else
	{
		getJComboBoxRoutes().setVisible(false);
		getJLabelRoute().setVisible(false);	
	}
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
}  //  @jve:visual-info  decl-index=0 visual-constraint="17,106"
