package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (5/7/2001 9:28:52 AM)
 * @author: 
 */
public class AddRemoveJTablePanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JLabel ivjJLabelAssigned = null;
	private javax.swing.JLabel ivjJLabelAvailable = null;
	private javax.swing.JList ivjJListAvailable = null;
	private javax.swing.JScrollPane ivjJScrollPaneAssigned = null;
	private javax.swing.JScrollPane ivjJScrollPaneAvailable = null;
	private javax.swing.JTable ivjJTableAssigned = null;
	public static final int MODE_TRANSFER = 0;
	public static final int MODE_COPY = 1;
	private int mode = MODE_TRANSFER;
	protected transient com.cannontech.common.gui.util.AddRemoveJTablePanelListener fieldAddRemoveJTablePanelListenerEventMulticaster = null;
/**
 * AddRemoveJTablePanel constructor comment.
 */
public AddRemoveJTablePanel() {
	super();
	initialize();
}
/**
 * AddRemoveJTablePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public AddRemoveJTablePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * AddRemoveJTablePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public AddRemoveJTablePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * AddRemoveJTablePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public AddRemoveJTablePanel(boolean isDoubleBuffered) {
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
 * This method was created in VisualAge.
 * @param src javax.swing.JList
 * @param dest javax.swing.JList
 */
protected void copySelection()
{
	int[] itemsToCopy = getJListAvailable().getSelectedIndices();

	for( int i = 0; i < itemsToCopy.length; i++ )
	{
		getJTableModel().addRow( getJListAvailable().getModel().getElementAt( itemsToCopy[i] ) );
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
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListAvailable() {
	if (ivjJListAvailable == null) {
		try {
			ivjJListAvailable = new javax.swing.JList();
			ivjJListAvailable.setName("JListAvailable");
			ivjJListAvailable.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListAvailable;
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
			getJScrollPaneAssigned().setViewportView(getJTableAssigned());
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
			getJScrollPaneAvailable().setViewportView(getJListAvailable());
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
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTable getJTableAssigned() {
	if (ivjJTableAssigned == null) {
		try {
			ivjJTableAssigned = new javax.swing.JTable();
			ivjJTableAssigned.setName("JTableAssigned");
			getJScrollPaneAssigned().setColumnHeaderView(ivjJTableAssigned.getTableHeader());
			getJScrollPaneAssigned().getViewport().setBackingStoreEnabled(true);
			ivjJTableAssigned.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableAssigned.setAutoCreateColumnsFromModel(true);
			ivjJTableAssigned.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableAssigned.setGridColor( java.awt.Color.black );
			ivjJTableAssigned.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			ivjJTableAssigned.setRowHeight(20);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableAssigned;
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 9:45:52 AM)
 * @return javax.swing.table.TableModel
 */
protected AddRemoveJTableModel getJTableModel() 
{
	try
	{
		return (AddRemoveJTableModel)getJTableAssigned().getModel();
	}
	catch( ClassCastException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		throw new Error("AddRemoveJTablePanel should only have JTable models that are AddRemoveJTableModel!!");
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 9:43:36 AM)
 * @return int
 */
public int getMode() {
	return mode;
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
	getJButtonAdd().addActionListener(this);
	getJButtonRemove().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AddRemoveJTablePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(400, 356);

		java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
		constraintsJButtonAdd.gridx = 2; constraintsJButtonAdd.gridy = 3;
		constraintsJButtonAdd.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonAdd.ipadx = 28;
		constraintsJButtonAdd.insets = new java.awt.Insets(5, 1, 1, 6);
		add(getJButtonAdd(), constraintsJButtonAdd);

		java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
		constraintsJButtonRemove.gridx = 3; constraintsJButtonRemove.gridy = 3;
		constraintsJButtonRemove.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonRemove.ipadx = 4;
		constraintsJButtonRemove.insets = new java.awt.Insets(5, 6, 1, 103);
		add(getJButtonRemove(), constraintsJButtonRemove);

		java.awt.GridBagConstraints constraintsJScrollPaneAvailable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAvailable.gridx = 1; constraintsJScrollPaneAvailable.gridy = 2;
		constraintsJScrollPaneAvailable.gridwidth = 3;
		constraintsJScrollPaneAvailable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAvailable.anchor = java.awt.GridBagConstraints.WEST;
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
		constraintsJScrollPaneAssigned.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneAssigned.weightx = 1.0;
		constraintsJScrollPaneAssigned.weighty = 1.0;
		constraintsJScrollPaneAssigned.ipadx = 363;
		constraintsJScrollPaneAssigned.ipady = 105;
		constraintsJScrollPaneAssigned.insets = new java.awt.Insets(0, 6, 23, 9);
		add(getJScrollPaneAssigned(), constraintsJScrollPaneAssigned);

		java.awt.GridBagConstraints constraintsJLabelAvailable = new java.awt.GridBagConstraints();
		constraintsJLabelAvailable.gridx = 1; constraintsJLabelAvailable.gridy = 1;
		constraintsJLabelAvailable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAvailable.ipadx = 55;
		constraintsJLabelAvailable.insets = new java.awt.Insets(6, 5, 0, 0);
		add(getJLabelAvailable(), constraintsJLabelAvailable);

		java.awt.GridBagConstraints constraintsJLabelAssigned = new java.awt.GridBagConstraints();
		constraintsJLabelAssigned.gridx = 1; constraintsJLabelAssigned.gridy = 4;
		constraintsJLabelAssigned.anchor = java.awt.GridBagConstraints.WEST;
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
	if( getJListAvailable().getSelectedIndices().length > 0 )
	{
		if( getMode() == MODE_TRANSFER )
		{
			transferSelection();
		}
		else if( getMode() == MODE_COPY )
		{
			copySelection();
		}
	}

	getJListAvailable().clearSelection();
	getJTableAssigned().clearSelection();

	revalidate();
	repaint();

	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJTableAssigned().getSelectedRows().length > 0 )
	{
		if( getMode() == MODE_TRANSFER )
		{
			Object[] items = new Object[ getJListAvailable().getModel().getSize() + getJTableAssigned().getSelectedRows().length ];
			int i = 0;
			for( i = 0; i < getJListAvailable().getModel().getSize(); i++ )
				items[i] = getJListAvailable().getModel().getElementAt(i);
			
			for( int j = (getJTableAssigned().getSelectedRows().length-1); j >= 0; j-- )		
			{
				items[i+j] = getJTableModel().getRowAt( getJTableAssigned().getSelectedRows()[j] );
				getJTableModel().removeRow( getJTableAssigned().getSelectedRows()[j] );
			}

			getJListAvailable().setListData(items);
		}
		else if( getMode() == MODE_COPY )
		{
			for( int i = (getJTableAssigned().getSelectedRows().length-1); i >= 0; i-- )		
			{
				getJTableModel().removeRow( getJTableAssigned().getSelectedRows()[i] );
			}
				
		}
	}

	getJListAvailable().clearSelection();
	getJTableAssigned().clearSelection();
	
	revalidate();
	repaint();

	return;
}
/**
 * 
 * @param newListener com.cannontech.common.gui.util.AddRemoveJTablePanelListener
 */
public void removeAddRemoveJTablePanelListener(com.cannontech.common.gui.util.AddRemoveJTablePanelListener newListener) {
	fieldAddRemoveJTablePanelListenerEventMulticaster = com.cannontech.common.gui.util.AddRemoveJTablePanelListenerEventMulticaster.remove(fieldAddRemoveJTablePanelListenerEventMulticaster, newListener);
	return;
}
/**
 * This method was created in VisualAge.
 * @param list javax.swing.JList
 */
protected void removeSelection()
{
	javax.swing.ListModel model = getJListAvailable().getModel();

	Object[] items = new Object[model.getSize()];

	for( int i = 0; i < model.getSize(); i++ )
		items[i] = model.getElementAt(i);

	int[] selectedItems = getJListAvailable().getSelectedIndices();

	for( int i = 0; i < selectedItems.length; i++ )
	{
		items[ selectedItems[i] ] = null;
	}

	Object[] itemsRemaining = new Object[ items.length - selectedItems.length ];

	int j = 0;

	for( int i = 0; i < items.length; i++ )
		if( items[i] != null )
			itemsRemaining[j++] = items[i];

	getJListAvailable().setListData(itemsRemaining);
}
/**
 * 
 * @param listData java.lang.Object[]
 */
public void setJListData(java.lang.Object[] listData) 
{
	getJListAvailable().setListData(listData);
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:31:04 AM)
 * @param model com.cannontech.common.gui.util.AddRemoveJTableModel
 */
public void setJTableModel(AddRemoveJTableModel model) 
{
	getJTableAssigned().setModel( model );
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 9:43:36 AM)
 * @param newMode int
 */
public void setMode(int newMode) {
	mode = newMode;
}
/**
 * This method was created in VisualAge.
 */
protected void transferSelection()
{		
	copySelection();

	removeSelection();
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5BF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BF8D4E5F600A029C4858D9A11D5D4DCE9E5FDEC69AE56F46B36F10BEBEC316A4A37D21B6D325D2CDFF60BAD7D0AAE7D76CFB2193CDF90E082E882F9A2F192A0D68C2FCC9E1844AC66A359902CB55CE4EE12C9E6668EF7EE1289C468B97F634E4DE4E6A238664B49FF7F73BF4E63BF677F4F796F8DA9ACDE111A14DCC8C8D2AA61BF9FA691927B91A1373D16F2DC60D64931E6E278598FD0C0EAD3CC
	D02E0536287BE24D79442C26C3BD8E6A594BE24D3B61F98749D26B6A60A1841DECE8FB4FBAFAB4BC1D8FF4BAE934DEFEAA9B4ADC00750043F25FA052BFF94A4271DBB8DEC0D2B2A15BE81F5ED37BB86EA254FDGEB816286D97FCDD0E6D04A8B6A0F71FE1FAFD9C1367C38B2B98767B14CA920F9080D9D66E589F9BCA9FF98E94D25FC621895503681G05CFA1132EA3D01E689C9DBAB520382C83125B2D38B5F9E058BAB466300EE98EE7FD3D4DEEBFA73B14F1393DDB6AF74A1D12DBF69292C967FD6ED4BA1D07
	10EE28DB9C083BF09EE92D07FA82GF188FCC9AF62BF07E78DA052A164BBD9F902735FFB65F1326DD5F7CADE8879D6BA18DCF18E3FFCD1E3EF2795513598B3214E53C7E24D55G8DGF60079GAB8176D29B18CAF98FCADB3F562438DC0A5B6671B4BA6D6A6F953B6C04E71035DF6338F37200225A89E16B3C70D4F590FE42819B37743A47B11D2CBAAA56FC7539EF130DFF7EC9F2DA901DECDCD51B9CB342F4717208DFA70C765F07243DE5E43ACDCE27FD1E98E9A75E2C242486213D6615034969235C66C77D34
	BBC6F12DDF20B6F0821E1338FDB07C2B943F5800E77659C771D8DF81EDA627300D5EDC41CB07E99949BF5C91D48F51FFE3CB9EF6B29E66B883F90922F6375A49E8E6683CBCC0716B8CF846CB8545E3FD9334353AE24D4C3E2E1A51DFD7C3FD9DC0BC40D6G0BBB56DC87506A96B676ECCA4DD73431CEC9559A479CCE3B2C92B6762AE9971432CFD325B637D7135C83F20F24BA506F3C0477D9E8C29D74CF9B97C9EDFFGE8FCAC3BE5D5529C0A9BE617DC32A62BDE56E72929G7B28B2ED6DF638E4A0607290B16F
	37CCBB20F4CADE6D8F9E3B24497E9E2C7DE1936AE4DC6A0199E182F8E6FAF9B48D75B58C724F81D82C6B21A495677BC4D6E13F282F6FD01451B18F5DC1C83C9B757C8B3AF72C046798375071A36908ABD3E24DA794B14F79546D06F91A9D83483E24CE8A9FFB69BC6E491D0A30195F748A1BE94EF918D43F529E54E690BC8A331588A550F77E094E09FC313AFC3EF7963F6DD4027B54D75FAB2E4ECAA36586674F5557EAE7A62E559340EB9CC0222E63BBD2CB184DB6CADED9D86927AA9818EC77EB1C71392CB71F
	6B9379C9657C3336C7E81B4983FBB0C0B74014C768303A37B748389B15A7388EDBA88D637C99FFC49ACCD791E6BC1F64F1592DB54B9E59ED0787AFEF3B1D5AA0775FC1C59F331462BD606FCD60E0DE9DFF2F99FDD252B45551BF2649DD3226B95CC37E7607A8AD6FD83F473FD588DAFFC64FF0E5827CDD6F7F3899FDE5C0F13BE53AE87A905E7EA475D559A93B0069A6E54C2DB17C7753505EC765C93ED7BC1D06414F38649C43FD07458ACFC8D9A8036AF001A96056C536F8500F3769318E42CA40E14C338E4A0D
	9E6113932542A74BAEBFC01E69ABCF8EF626C4BF5C1CFC629273234ECBFE1FE4BC2DCE8F4E7BF7691E4171DE46EB5E31C23467A0118B730333D9B81787E84C87D8FEC97801B5835B45DE439D20C9F1BA19BAF5BD6F4DC4BB774A1746E45822F172CFF45D9D485C9E50564375485A5F4BC4BDBA8D7B2126229EDF6B9571609A60A78EA0719253E5354AF4417A3DD5A67ABD83758D2A3F5F56E97D3ED3AE7AB5F859FC3DDE56AF9A4ACDG2C5F568A51EF2B174DA77A65EB31660B1A08957F63A02EB30D95C724F149
	E775CE002D565737D33D32F8F070F4BA77BDCD93F15F9D0D747C05FAAC40C6CD58440BB9A26E0B9ADDC1629F5A93B22E5D22B139ACE3FE1BA839C944FD5159C833975A064744BABE1CE9BCFBECDE2FB26020479EDF036EAC234DB05C052CCCA8879D2AD7EB83277609756A4BC2FE71DC64E89EA7C46F4F6632CE815DB5GD563087FC3D321017744B1F63E95C2DB8A1D6F59B20C39A5EAD0D6D6F44A6A20223AE4FB95EA56AACDE8D63A3FD8ED3439851F8F8ABFFC3B9AD2A2B252B826E990DD586DFD3600B9BAC2
	CFF27CE08F91F15E03BB9AB80FF10FADA3A72E6DC88E96F7C7643D136C99E7FA0F9E776BFF6A3B661444AFFE1B46F4F120DC68E2937459B7A1F4D15F98CC975D9342260ED51D704B42FC75BA45E174FFD2AE6247E118FB45045075490A2F276B8F0BCEB11B105BF5FD33FEFD0133B1B6B1E53252B9BE6D9F01CD03AA1C7946D4054815B6GFF0C6F7D15026FFC9F4410BE211793E65BF43ACCB757CF1A537EFE2520BD47A74E635153017941DA1FBF0609734D0CE7D85C1C5904BC477BD85C1C23630BA85EB46947
	F3BF217E0F75AAE80B1A9431DEE5AE6EA764B2588BC09DC0870093A072327063DFE6E1C29EB616EE03FCD5F5CB4E2EE9072408E933D1E6B857F87D01EC54AFBD570808996CE501FAD8F5F93A6C3181F5B6CE1DB16E5D00FAC240B8FEB6F595855FC759CA6BEB9659144EE911ABE24F7C720CF14F0C49BB121C95E24FBCF605516E3D627759EABDD7FEB18F777CE8E8DB83908710G10BD85FE8B50B1A574FFA86764EC7A2796572DB4812E954E34264058C357D7D5790D6979FFE60C8B5433F3EA7AB8263FCB0DC2
	FF7327044DEF1CB1D7A08467610DD94745CC859BB73782473DBC7555F383B62E327246787CE056F189C1790C21634817DF1DCF16434D2367FDA10C2B82E038BE9B62BA20EE7FD268BD3F89FDBD826AABGE2GB682EC8330DC95FD0AE99F0CD3B0862983FCAF409440F23D4FFD0D9703691E733DA28C7FC887CB249B41E907DF0327025292B9CBEA985D647092FD96771F2CB0FD191F5566064EA9BE4C8D1ED31C4C8D1ED3B46596B0BC4CE13CE1906D1F64064FB93AF203659C6FD0D91E5B15C76776FAE5AFF57C
	E6C913F85BAF351DA4C86EF1D6F249DC8F106F14E570BEAD1A52F4D857751226C03B0D0175DA4F57EC535519EB4576315E820CAF36C2FB61B50813811A816C57447E752B5CD94F0F96DF187363353C506B72AFF94157A5A18F575F2B0C2983F2206E7FAD4F223779F74AB6CDF6211AD81F2DF43CA629C3720C357B9D9D4F5AC20DCF4F8F353EEC8D7A4A0DEBF0C4DF03714640B5188FFA0C3E46F43F7A5A4CB5E0736D28B04EF7D45F9B22E93C738C0CDBFFCD44E3DFD49A7B5669FDDF2271C9BC745B2C773D67B4
	6620CEE9D29953201C037D1EB8ADF2CD82F58BC083C0779C96D732737549897437C1D5F20158DD9EF900344E453163B6B1F68D74DF8710G108EEDD5F3F51ACE4C817C311E1071667C9CBF1E45E23A7C0FDCC29BEC6F9AD0A1CF44DC48B6AEB91C18583176274E6F88E877FA9DC3EE594E477B4651465ABB24FEB0CAB11437553F1F6BEF9343F85E3D275618F7AF1E4BF4908365FAG562720A6D81F84A8374D95B17A5396E30CDEB80F75B9B6CF34EFB99A2C3DE31E586FD751B865A254B59D77C8B362A2209E2D
	6332DB443930F21E704F0B85217DEB28A038FF398A026FFB5E02707B5E1502D0FE41783B35D14831C5677945C6A1C77ACD82F724C948D1F813301B47AB707E845C7270621B304F3334EE575B5F281C5ECE2AC4BB0B11B71C966FE7DE86DC76FCE14B07AFE21C26470A182D5457FF2CBA6C0D52D087758C966B14B42473714D73C55EFD3681671C02FA94406A79A20674D50898F2753717110EB91F844D3B633B0FA4474EE7BE1FB05F9FC32E02981251DCEE93B44B96441ABFDF00389F5B0C360232BF17CC0895CA
	563EEDDABB2E8724DA06F66B34FC91795D1FE2EC3FB82DBD55B01E4976FC3910ED5B732B0873AFBE8AF92730F28113E99DAFBBFBCAC959AD3FCDE3FA9FEAF907840D517DDECCC41CF92ED664D664E6217B41C3287BE3D06F849886E0FD5BEAC4CCBAC76F9B37937B2E057AFA00041B757B16419077ADF4B7E0737D3BC54F91968A9A076A846E58C2E1FFFFCDED5665061D4B6D98183310664C94773768B3FA3F9FB423DDEA60F4318B717D670BB4C52B0A407E25AD4DC174E8DC0BB42E8704F49803D04A753F8F1E
	BF624F0C7E7728EF7D8269C39B23372548CF2F2DA99C3D34D9684D09E076AE6E1B61AC2FA80652ADCF1484CD172B34E10777A037231332711DEB6D1524641490F69499416CE7B9AFE3961672737475997975EC4046A55E603872066B9D477276929B5ADFDC844B5B13F47CC10A5FE040333D2A3BC16CD51B202D75CFC4BE7FFB8B9A4BEA282F830887588AE03985E279DB44FEDF303D164C920FF5AB1E8EB8901C06AD1C4977006D465E257EB0CD6C89EB7E72C170333712C2654C2D377077673CCC68AEB5441A71
	C5A8DFB4342D8158G30994034A8563CEF11106F4276EAA6DF0B5D2159B4CD9A9826B70CC2389E07BC61C7E3F042E4BBDF9BCE365AC5216EE2FECA771966C5016FD9B7D1FC772240772C4EC3623E498EED6B96093C6AEC0D20DF9D89B1D564F4FA5D112168B76C226F7EA2836937D33CAFB2107EBF6E9474A320ADB1D2507FAF0BD17E4EDB99BDBBAFFD374E244F4655569947C572FEABF839A648B83697BC540CE525D449EDE44504515F0FE5FE98747D84BDAF781C9BEF757BBB1B639F7E57B8C77E327D49C9A1
	66A83C0D1F2737794F55E24A976CE3447F4376C4A706G1C464F3205AD6FFB74DE527C6ADBC925A1E8F47339D79AE8A40CECCBE3723F50C865D7ABB90D5F60F1476D7C5EF7718C58F81B30698F779C0032C79A615E0A262D285422810977BEDD90D23907441AD46055A5FF2EB1BCFF56B05B7B4D2F830C06779B25417451E3899D337E1FA5F84CBAE0899E33BAAC61F371CB16506F7FFC96637BBF36F60F95E3FC349956AE7B76D8F3ADC0B3C06F6DE2AD4DC538167885424C3D2945BE04B9A9FEF520897A3FAA8A
	AD6B6622593FD5F82B887329FE8552A6974E2F6731C538DE1A6221C806B36DC19D290E21E10DF565784CBD6893CEF91022D98C6E2E95B138F63B58C3628F215D466A75B7E8BDDE2F2B07C259860E7963585F2210B43E4E3BFB81CCF6194610E4F12C39EC3148C9FEC0F14720FED147D97CBADAACF4F0E6975511DE6F2475F83D3EE1E7AFEF7F66FC744D1D629D7DB7C103491EDB04321B16C00E36C46863CB1748652AF45C6BAE2123BA9D77F11D98BBDF47BDFDCD4FE7F55CBE226733CBC48C74FC896236C0BD7F
	8E61F3776EB9C4C26F1F1D2A6211D5ED12E1691BE26E4BBF42CF8E398F7CECCFF928B940F35BA11EE5C1EC37845BB09F7F66810C1FEB010F8EG8FC08440F200B5G6BGB682EC86C83FB356DC82D087508A70B90093E08EC0541DC20EA7779D8CA387CFCA698622CA8E5CC2B86F773F0B77B8DEFEC6B05C770AC36FABFFD5BC7B3E7223E27A7E9676036EC98F3F77FA3E9869A80303DED95F3F5697676B38B6EF2762F560FD994BAF9654064BAF0C5F106073AA50C34C1DAC3F407AFAF8FE95F25B570808A9FE58
	07635ABB9CDE4DFF0DC3589D4A5EEAE35E3A71CEF63FCD22C45EB5D5035F280E03A1B886A4A7CBB29BA535D3F1BA86A6F93EF4B7757177818A75F12F5460DA8FAB2A63326256E60E9439561BF4AC9E465DB01649DFB32B7CD9867923E05C2AA83F7CB1D1CC7E17F4797B52287C741306403B2A393B0C77D06B22585DFEA214E42910253C887722C1100371FC1C7A30855A4B160A7C7005B27489467FFF3AE6635F880179E1B74C2990E3BE6A21FEFF9170CE1D5E66F2BF3D23E4B6FA99E168AD07B9DB78B3374D92
	9C271F7D4C75F95B2BA5E8AF8EB7182B64F4DC261FAE30A80B35271606761F6C52597DE7E7E970F3F9EF694CF33924B47839DCD1EABC17797B42D27419A1588BFA75FE35BA6E020E7B004E0938738E3BB62C630FD33E905FAAA3C10137D13EA6A82E17634E98F097443B0DD25C83C0E4F089B13633145A154369645FFE50BE8EB7EC893ABEAED09E9D9E49407797BA8E791E991FEFBE9D8C476CE62892EB6C9E677903685B416EF1E02790F7C855628EE9156E8F0F55607B9143902B6176495A53F22E3B2D495651
	5755F46E530E0EC65B39BEDB47F95B05AE5D9F53EB0F06995F7A6939364FBEBD5B9DEC066B13757EAA3F2CA3D7C25CD951DD01497A54AEA1EB7CD2F18716EE413C50F297442CG2DF7897FBBEAA53ABC17DCF8A06A77B77861469958AC2A8E5840381D872ACB8E436F91BC2358589E7C8C17BF7F77E17FBC0FDF3B3EF9CA687F8470BC3DBADC1EFE712ED0F96A8FF6059B278599477268A92A17083B826FE696D0FC54DD01F7B339967DBBFFE8B35DADE295F73508D506F55CAB3B84AE72EE917F4D3378E5DDE4F8
	AE34FC73395B7B5FA88D768EE8C135B8071EG19E381B6811822610C81E0FD9275332A99F03DGBE00E8003551620E7551EA711D5B06E8F1777D736A403B6FB709716E7A0AB5706EBBE9DA7BF72C01F75F36A4E37BBFDB836F3E45F84653765D62BEBE7F9E41D3436EC01E16CC1BD35D9D1E27153B83F91ABE7EA73BC37144567409E97E520AFC5D93BE37E063F66F3E5E3BBEE653FFC7574583B4D0F78C97C7F19100FB1308FDCA2AC0BF6167ABBD0298FE1FAF470F97C790E17FBF341ECC42C1327A57A4D5A3A9
	B63BDDA3E676A6502B12BCFDABE92367CA7C78517A141FBF1412CCCCED444C4E59B6E24243320D245233340D2440B96B73F01DA16039146A3DC42E12E48386A3FAA019298715759A499081D63D5E5D440543831468585DFC92FAFD840C66CC5B731EFD6003FF0DFB3D471BC24CED2488C32FAED9259107EC47B77B7CD37FB3A44323C0C85F85F81042CB0BD42FB66914BBC86160B97C54552B49F61B08E560DC5BC9BFCA2B41DB761AD649BB2CA16747BB08490D7F14C03633E627649E2AE1014DF1268ADB83F2CA
	68B6867C6E705F556B336CF8BA2FFCFF7B2635A9A4798C49F4AA127D0CB4G29C89B495006D559BB2CB86DBE4FE9B6D17A842EBEC632A618EE2FAEBE8AEB6D06A8C9E7FA54772D481C041CB320B61FCAEAE8CFFC93E255FB581B06A595C849EA1B067FE42128E5FA5B5CF7350A97BE5E7DF9B2C962C26292E479BCE0A603124B611C0407E6502DB2C497A78F2AACF6514D8715E1BD8372E8BF7D75F38F3EF6E2F3A2CCF60624524F550025FCFF2F33E32EFED93DF97552F732226297A1471E86316CB0F38644FEEE
	AFFE9D8813178448AC1AF4562DF7BED73B74CF5F7E0C326E9F2A11E236C8B034E6C8D6DCB53641C107DB56A7009503695F512747BE5600BE5BFDCBF6363F723BFE3030E612622A2D25FF8F533FC7706FE10AB9CCB1070F4032EDA6E1FF825BFDC2E6EA1634328BED4807CFBBFC3AA3906E9E78E326580C996606581C19F6036882FD7530D608FFAC7827088E5ACB7F3EFB5D3C632812G1EAB26730C4E0B5F13A78BA7A6872F996F1937AE30043C2B5EF09F5BEB936F735F8173EFAFCDA8DD557D3166667BC5FEBF
	110239E528BB06AE1764F4C206B9EAF35B713E21F1D2137D771455A61C5FAB7A70C656F6147E5F5008E14C7F83D0CB87883C97524C9B95GG6CB9GGD0CB818294G94G88G88G5BF854AC3C97524C9B95GG6CB9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD595GGGG
**end of data**/
}
}
