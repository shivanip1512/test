package com.cannontech.tdc.toolbar;

/**
 * Insert the type's description here.
 * Creation date: (4/10/00 3:03:50 PM)
 * @author: 
 * @Version: <version>
 */
public class AlarmToolBar extends javax.swing.JToolBar implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener 
{
	private javax.swing.JButton ivjJToolBarButtonAckAll = null;
	private javax.swing.JButton ivjJToolBarButtonClearViewableAlarms = null;
	private javax.swing.JButton ivjJToolBarButtonSilenceAlarms = null;
	protected transient com.cannontech.tdc.toolbar.AlarmToolBarListener fieldAlarmToolBarListenerEventMulticaster = null;
	private javax.swing.JButton ivjJToolBarButtonClear = null;
	private javax.swing.JButton ivjJToolBarButtonRefresh = null;
	private javax.swing.JComponent[] currentComponents = null;
	// All alarm buttons must be in here
	private javax.swing.JComponent[] originalComponents = null;
	public static final int ORIGINAL_COMPONENT_COUNT = 6;
	

	// The height of all the JButtons
	private final int JBUTTON_HEIGHT = getJToolBarButtonAckAll().getHeight();
	private com.klg.jclass.field.JCPopupField ivjJCPopupFieldDate = null;
	private javax.swing.JLabel ivjJLabelViewDate = null;
	private javax.swing.JSeparator ivjJSeparatorDate = null;

	public static final int COMPONENT_INDEX_CLEAR = 0;
	public static final int COMPONENT_INDEX_ACKALL = 1;
	public static final int COMPONENT_INDEX_CLEARVIEWABLE = 2;
	public static final int COMPONENT_INDEX_SEPARTORDATE = 3;
	public static final int COMPONENT_INDEX_DATELABEL = 4;
	public static final int COMPONENT_INDEX_DATE = 5;
/**
 * AlarmToolBar constructor comment.
 */
public AlarmToolBar() {
	super();
	initialize();
}
/**
 * AlarmToolBar constructor comment.
 * @param orientation int
 */
public AlarmToolBar(int orientation) {
	super(orientation);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJToolBarButtonAckAll()) 
		connEtoC1(e);
	if (e.getSource() == getJToolBarButtonClearViewableAlarms()) 
		connEtoC2(e);
	if (e.getSource() == getJToolBarButtonSilenceAlarms()) 
		connEtoC3(e);
	if (e.getSource() == getJToolBarButtonClear()) 
		connEtoC4(e);
	if (e.getSource() == getJToolBarButtonRefresh()) 
		connEtoC5(e);
	// user code begin {2}

	// make sure the event wasnt handled above first
	if (e.getSource() != getJToolBarButtonAckAll() && e.getSource() != getJToolBarButtonClearViewableAlarms()
		&& e.getSource() != getJToolBarButtonSilenceAlarms() && e.getSource() != getJToolBarButtonClear()
		&& e.getSource() != getJToolBarButtonRefresh())
	{
		for( int i = 0; i < getCurrentComponents().length; i++ )
		{
			if( e.getSource() == getCurrentComponents()[i] )
				handleTDCChildButtonPress( e, i );
		}
		
	} 

	// user code end
}
/**
 * 
 * @param newListener com.cannontech.tdc.toolbar.AlarmToolBarListener
 */
public void addAlarmToolBarListener(com.cannontech.tdc.toolbar.AlarmToolBarListener newListener) {
	fieldAlarmToolBarListenerEventMulticaster = com.cannontech.tdc.toolbar.AlarmToolBarListenerEventMulticaster.add(fieldAlarmToolBarListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC1:  (JToolBarButtonAckAll.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmToolBar.fireJToolBarButtonAckAllAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJToolBarButtonAckAllAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JToolBarButtonClearViewableAlarms.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmToolBar.fireJToolBarButtonClearViewableAlarmsAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJToolBarButtonClearViewableAlarmsAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JToolBarButtonSilenceAlarms.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmToolBar.fireJToolBarButtonSilenceAlarmsAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJToolBarButtonSilenceAlarmsAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JToolBarButtonClear.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmToolBar.fireJToolBarButtonClearAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJToolBarButtonClearAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JToolBarButtonRefresh.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmToolBar.fireJToolBarButtonRefreshAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJToolBarButtonRefreshAction_actionPerformed(new java.util.EventObject(this));
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
protected void fireJToolBarButtonAckAllAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarButtonAckAllAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJToolBarButtonClearAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarButtonClearAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJToolBarButtonClearViewableAlarmsAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarButtonClearViewableAlarmsAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJToolBarButtonDateChangedAction_actionPerformed(com.klg.jclass.util.value.JCValueEvent newEvent)
{
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarJCDateChange_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJToolBarButtonRefreshAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarButtonRefreshAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJToolBarButtonSilenceAlarmsAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarButtonSilenceAlarmsAction_actionPerformed(newEvent);
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 5:06:50 PM)
 * @return javax.swing.JButton[]
 */
public javax.swing.JComponent[] getCurrentComponents() {
	return currentComponents;
}
/**
 * Return the JCPopupFieldDate property value.
 * @return com.klg.jclass.field.JCPopupField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCPopupField getJCPopupFieldDate() {
	if (ivjJCPopupFieldDate == null) {
		try {
			ivjJCPopupFieldDate = new com.klg.jclass.field.JCPopupField();
			ivjJCPopupFieldDate.setName("JCPopupFieldDate");
			ivjJCPopupFieldDate.setPreferredSize(new java.awt.Dimension(208, 23));
			ivjJCPopupFieldDate.setBackground(java.awt.Color.white);
			ivjJCPopupFieldDate.setMinimumSize(new java.awt.Dimension(208, 23));
			ivjJCPopupFieldDate.setMaximumSize(new java.awt.Dimension(208, 23));
			// user code begin {1}

			com.klg.jclass.field.validate.JCDateValidator dv = new com.klg.jclass.field.validate.JCDateValidator();
			dv.setAllowNull(false);
			dv.setDefaultDetail(com.klg.jclass.field.validate.JCDateValidator.LONG);
			dv.setCasePolicy(com.klg.jclass.field.validate.JCDateValidator.UPPERCASE);
			dv.setMax( new java.util.Date() );

			// create the invalidinfo and set its properties
			ivjJCPopupFieldDate.getInvalidInfo().setInvalidPolicy(com.klg.jclass.field.JCInvalidInfo.RESTORE_PREVIOUS);
			ivjJCPopupFieldDate.setEnabled( false );
			//dv.setRange( new java.util.Date(), new java.util.Date() );	
			ivjJCPopupFieldDate.setValueModel( new com.klg.jclass.util.value.DateValueModel( new java.util.Date() ) );
				
			ivjJCPopupFieldDate.setValidator( dv );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCPopupFieldDate;
}
/**
 * Return the JLabelHistoryDate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelViewDate() {
	if (ivjJLabelViewDate == null) {
		try {
			ivjJLabelViewDate = new javax.swing.JLabel();
			ivjJLabelViewDate.setName("JLabelViewDate");
			ivjJLabelViewDate.setText("View Date: ");
			ivjJLabelViewDate.setMaximumSize(new java.awt.Dimension(65, 16));
			ivjJLabelViewDate.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjJLabelViewDate.setPreferredSize(new java.awt.Dimension(65, 16));
			ivjJLabelViewDate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelViewDate.setMinimumSize(new java.awt.Dimension(65, 16));
			ivjJLabelViewDate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelViewDate;
}
/**
 * Return the JSeparatorDate property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparatorDate() {
	if (ivjJSeparatorDate == null) {
		try {
			ivjJSeparatorDate = new javax.swing.JSeparator();
			ivjJSeparatorDate.setName("JSeparatorDate");
			ivjJSeparatorDate.setPreferredSize(new java.awt.Dimension(20, 0));
			ivjJSeparatorDate.setMinimumSize(new java.awt.Dimension(10, 0));
			ivjJSeparatorDate.setMaximumSize(new java.awt.Dimension(1000, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparatorDate;
}
/**
 * Return the JToolBarButtonAckAll property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJToolBarButtonAckAll() {
	if (ivjJToolBarButtonAckAll == null) {
		try {
			ivjJToolBarButtonAckAll = new javax.swing.JButton();
			ivjJToolBarButtonAckAll.setName("JToolBarButtonAckAll");
			ivjJToolBarButtonAckAll.setToolTipText("Acknowledge viewable alarms");
			ivjJToolBarButtonAckAll.setMnemonic('A');
			ivjJToolBarButtonAckAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonAckAll.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjJToolBarButtonAckAll.setMinimumSize(new java.awt.Dimension(100, 23));
			ivjJToolBarButtonAckAll.setText("Ack Viewable");
			ivjJToolBarButtonAckAll.setMaximumSize(new java.awt.Dimension(100, 23));
			ivjJToolBarButtonAckAll.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonAckAll.setBorderPainted(true);
			ivjJToolBarButtonAckAll.setIcon(null);
			ivjJToolBarButtonAckAll.setPreferredSize(new java.awt.Dimension(100, 23));
			ivjJToolBarButtonAckAll.setRolloverEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBarButtonAckAll;
}
/**
 * Return the JToolBarButtonClear property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJToolBarButtonClear() {
	if (ivjJToolBarButtonClear == null) {
		try {
			ivjJToolBarButtonClear = new javax.swing.JButton();
			ivjJToolBarButtonClear.setName("JToolBarButtonClear");
			ivjJToolBarButtonClear.setToolTipText("Clears Valid Displays");
			ivjJToolBarButtonClear.setMnemonic('C');
			ivjJToolBarButtonClear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonClear.setDisabledSelectedIcon(null);
			ivjJToolBarButtonClear.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjJToolBarButtonClear.setMinimumSize(new java.awt.Dimension(81, 23));
			ivjJToolBarButtonClear.setText("Clear Display");
			ivjJToolBarButtonClear.setMaximumSize(new java.awt.Dimension(81, 23));
			ivjJToolBarButtonClear.setDisabledIcon(null);
			ivjJToolBarButtonClear.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonClear.setIcon(null);
			ivjJToolBarButtonClear.setPreferredSize(new java.awt.Dimension(81, 23));
			ivjJToolBarButtonClear.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBarButtonClear;
}
/**
 * Return the JToolBarButtonClearViewableAlarms property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJToolBarButtonClearViewableAlarms() {
	if (ivjJToolBarButtonClearViewableAlarms == null) {
		try {
			ivjJToolBarButtonClearViewableAlarms = new javax.swing.JButton();
			ivjJToolBarButtonClearViewableAlarms.setName("JToolBarButtonClearViewableAlarms");
			ivjJToolBarButtonClearViewableAlarms.setToolTipText("Clear Viewable Alarms");
			ivjJToolBarButtonClearViewableAlarms.setMnemonic('l');
			ivjJToolBarButtonClearViewableAlarms.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonClearViewableAlarms.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjJToolBarButtonClearViewableAlarms.setMinimumSize(new java.awt.Dimension(100, 23));
			ivjJToolBarButtonClearViewableAlarms.setText("Clear Viewable");
			ivjJToolBarButtonClearViewableAlarms.setMaximumSize(new java.awt.Dimension(100, 23));
			ivjJToolBarButtonClearViewableAlarms.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonClearViewableAlarms.setIcon(null);
			ivjJToolBarButtonClearViewableAlarms.setPreferredSize(new java.awt.Dimension(100, 23));
			ivjJToolBarButtonClearViewableAlarms.setContentAreaFilled(true);
			ivjJToolBarButtonClearViewableAlarms.setRolloverEnabled(false);
			ivjJToolBarButtonClearViewableAlarms.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBarButtonClearViewableAlarms;
}
/**
 * Return the JToolBarButtonRefresh property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJToolBarButtonRefresh() {
	if (ivjJToolBarButtonRefresh == null) {
		try {
			ivjJToolBarButtonRefresh = new javax.swing.JButton();
			ivjJToolBarButtonRefresh.setName("JToolBarButtonRefresh");
			ivjJToolBarButtonRefresh.setToolTipText("Refreshes the data on the screen");
			ivjJToolBarButtonRefresh.setMnemonic('R');
			ivjJToolBarButtonRefresh.setText("Refresh");
			ivjJToolBarButtonRefresh.setMaximumSize(new java.awt.Dimension(61, 23));
			ivjJToolBarButtonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjJToolBarButtonRefresh.setIcon(null);
			ivjJToolBarButtonRefresh.setPreferredSize(new java.awt.Dimension(61, 23));
			ivjJToolBarButtonRefresh.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjJToolBarButtonRefresh.setMinimumSize(new java.awt.Dimension(61, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBarButtonRefresh;
}
/**
 * Return the JToolBarButtonSilenceAlarms property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getJToolBarButtonSilenceAlarms() {
	if (ivjJToolBarButtonSilenceAlarms == null) {
		try {
			ivjJToolBarButtonSilenceAlarms = new javax.swing.JButton();
			ivjJToolBarButtonSilenceAlarms.setName("JToolBarButtonSilenceAlarms");
			ivjJToolBarButtonSilenceAlarms.setToolTipText("Silence all alarm sounds");
			ivjJToolBarButtonSilenceAlarms.setMnemonic('S');
			ivjJToolBarButtonSilenceAlarms.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonSilenceAlarms.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjJToolBarButtonSilenceAlarms.setMinimumSize(new java.awt.Dimension(91, 23));
			ivjJToolBarButtonSilenceAlarms.setAutoscrolls(false);
			ivjJToolBarButtonSilenceAlarms.setText("Silence");
			ivjJToolBarButtonSilenceAlarms.setMaximumSize(new java.awt.Dimension(91, 23));
			ivjJToolBarButtonSilenceAlarms.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonSilenceAlarms.setIcon(null);
			ivjJToolBarButtonSilenceAlarms.setBorderPainted(true);
			ivjJToolBarButtonSilenceAlarms.setPreferredSize(new java.awt.Dimension(91, 23));
			ivjJToolBarButtonSilenceAlarms.setRolloverEnabled(false);
			ivjJToolBarButtonSilenceAlarms.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBarButtonSilenceAlarms;
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2001 12:26:25 PM)
 * @return javax.swing.JComponent[]
 */
private javax.swing.JComponent[] getOriginalComoponents() 
{
	if( originalComponents == null )
	{
		originalComponents = new javax.swing.JComponent[ORIGINAL_COMPONENT_COUNT];
		originalComponents[0] = getJToolBarButtonClear();
		originalComponents[1] = getJToolBarButtonAckAll();
		originalComponents[2] = getJToolBarButtonClearViewableAlarms();
		originalComponents[3] = getJSeparatorDate();
		originalComponents[4] = getJLabelViewDate();
		originalComponents[5] = getJCPopupFieldDate();
	}
	
	return originalComponents;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 5:37:11 PM)
 * @param e java.awt.event.ActionEvent
 */
public void handleTDCChildButtonPress(java.awt.event.ActionEvent e, int index ) 
{
	
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getJCPopupFieldDate().addValueListener( this );

	// user code end
	getJToolBarButtonAckAll().addActionListener(this);
	getJToolBarButtonClearViewableAlarms().addActionListener(this);
	getJToolBarButtonSilenceAlarms().addActionListener(this);
	getJToolBarButtonClear().addActionListener(this);
	getJToolBarButtonRefresh().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AlarmToolBar");
		setAutoscrolls(false);
		setOpaque(true);
		setSize(773, 32);
		setMargin(new java.awt.Insets(0, 0, 0, 0));
		setOrientation(javax.swing.SwingConstants.HORIZONTAL);
		addSeparator();
		add(getJToolBarButtonSilenceAlarms(), getJToolBarButtonSilenceAlarms().getName());
		add(getJToolBarButtonRefresh(), getJToolBarButtonRefresh().getName());
		addSeparator();
		add(getJToolBarButtonClear(), getJToolBarButtonClear().getName());
		add(getJToolBarButtonAckAll(), getJToolBarButtonAckAll().getName());
		add(getJToolBarButtonClearViewableAlarms(), getJToolBarButtonClearViewableAlarms().getName());
		add(getJSeparatorDate(), getJSeparatorDate().getName());
		add(getJLabelViewDate());
		add(getJCPopupFieldDate(), getJCPopupFieldDate().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	currentComponents = new javax.swing.JComponent[ getOriginalComoponents().length ];
	setOriginalButtons();

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 6:16:39 PM)
 * @return boolean
 */
private boolean isOriginalComponentsSet() 
{
	if( getOriginalComoponents().length != getCurrentComponents().length )
		return false;
		
	for( int i = 0; i < getOriginalComoponents().length; i++ )
	{
		if( getOriginalComoponents()[i].equals( getCurrentComponents()[i] ) )
			continue;
		else
			return false;
	}
	
	return false;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AlarmToolBar aAlarmToolBar;
		aAlarmToolBar = new AlarmToolBar();
		frame.setContentPane(aAlarmToolBar);
		frame.setSize(aAlarmToolBar.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JToolBar");
		exception.printStackTrace(System.out);
	}
}
/**
 * 
 * @param newListener com.cannontech.tdc.toolbar.AlarmToolBarListener
 */
public void removeAlarmToolBarListener(com.cannontech.tdc.toolbar.AlarmToolBarListener newListener) {
	fieldAlarmToolBarListenerEventMulticaster = com.cannontech.tdc.toolbar.AlarmToolBarListenerEventMulticaster.remove(fieldAlarmToolBarListenerEventMulticaster, newListener);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 5:06:50 PM)
 * @param newCurrentButtons javax.swing.JButton[]
 */
public void setCurrentComponents(javax.swing.JComponent[] newCurrentComponents ) 
{
	// remove all the previous buttons and their listeners
	removeAll();

	//add the default buttons that are on every screen
	addSeparator();
	add(getJToolBarButtonSilenceAlarms(), getJToolBarButtonSilenceAlarms().getName());
	add(getJToolBarButtonRefresh(), getJToolBarButtonRefresh().getName());
	addSeparator();		

	//We must re-add the PopUp listener
	//getJCPopupFieldDate().addValueListener( this );

	currentComponents = newCurrentComponents;

	// add the new buttons and their listeners	
	for( int i = 0; i < newCurrentComponents.length; i++ )
	{
		newCurrentComponents[i].setSize( newCurrentComponents[i].getWidth(), JBUTTON_HEIGHT );
		add( newCurrentComponents[i], newCurrentComponents[i].getName() );		
	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 5:06:50 PM)
 * @param newCurrentButtons javax.swing.JButton[]
 */
public void setJComponentEnabled( int buttonPosition, boolean enabled )
{
	if( buttonPosition >= 0 && buttonPosition < getCurrentComponents().length )
	{
		boolean lastState = getCurrentComponents()[buttonPosition].isEnabled();
		
		getCurrentComponents()[buttonPosition].setEnabled( enabled );
		
		//special case, set the Calendar to todays date if it is being disabled
		if( enabled != lastState && getCurrentComponents()[buttonPosition] instanceof com.klg.jclass.field.JCPopupField )
		{
			getJCPopupFieldDate().setValue( new java.util.Date() );
		}
		
	}
	else
		throw new IllegalArgumentException("JComponent Enablement for toolbar button["+buttonPosition+"] is not valid");
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 5:06:50 PM)
 * @param newCurrentButtons javax.swing.JButton[]
 */
public void setOriginalButtons()
{
	if( !isOriginalComponentsSet() )
		setCurrentComponents( getOriginalComoponents() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 1:25:55 PM)
 * @param event com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent event) 
{

	if (event.getSource() == getJCPopupFieldDate())
	{
		//only allow the newValue to be a java.util.Date
		if( event.getNewValue() instanceof java.util.GregorianCalendar )
			event.setNewValue( ((java.util.GregorianCalendar)event.getNewValue()).getTime() );

		//lets always make the hours,minutes,secsonds and millis zero
		java.util.GregorianCalendar newDate = new java.util.GregorianCalendar();
		newDate.setTime( (java.util.Date)event.getNewValue() );
		newDate.set( java.util.GregorianCalendar.HOUR_OF_DAY, 0 );
		newDate.set( java.util.GregorianCalendar.MINUTE, 0 );
		newDate.set( java.util.GregorianCalendar.SECOND, 0 );
		newDate.set( java.util.GregorianCalendar.MILLISECOND, 0 );
		event.setNewValue( newDate.getTime() ); //set the new object

		final java.util.GregorianCalendar today = new java.util.GregorianCalendar();
		today.setTime( new java.util.Date() );
		
		//if the date is before today, set the date to today
		if( newDate.before(today) )
		{
			fireJToolBarButtonDateChangedAction_actionPerformed( new com.klg.jclass.util.value.JCValueEvent(this, event.getOldValue(), event.getNewValue()) );
		}
		else
		{
			today.set( java.util.GregorianCalendar.HOUR_OF_DAY, 0 );
			today.set( java.util.GregorianCalendar.MINUTE, 0 );
			today.set( java.util.GregorianCalendar.SECOND, 0 );
			today.set( java.util.GregorianCalendar.MILLISECOND, 0 );

			javax.swing.SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					getJCPopupFieldDate().setValue( today.getTime() );
				}
				
			});	
			
		}
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 1:26:30 PM)
 * @param event com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent event) {}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G65F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E12DBC8DF8945535D190D183C60D9A95B5E254206913D6EC29527751941FE8E9CDDBDAE90D4AFB72B4DA5AD2CBEBD4DAA71B90C0820410C82C08ABBFCF3098927E92B1E8167C18C0501804A404D019E4A74912591D4D6CEC329BA23CF36E1DFBE7F6331BAC34793E434CB9771CF34F5F3DF36F1DD9047C776217E7D87285A1E339207F15E6884236AF84E1A8511A4FE8D1D2F236E07AFB9FE03DF05306AC38
	46C05B3C6FA4E7678A4943AB81CFGFC49FD49599B61FE037068F0C58D5C046927905A3E97DBF4E874FE2E780A753302606F376640F5ADC0A250703AD4427EBFEEAD546973F4BA038C0BA024F4A3CFF36BA61DD686F883C0B4C0FCB7651F8957D524674B8E1669FC9D57448BCB9F7F4032867518F4B2A86C2132657A55AA5C1F3128877B7A065809B29A60C9G68FC2670677A0F103F212F6753AE45D954A53ADC0ACB133AFA0BB4FBD71126A8F227289EBCE813C555598C58E1D1857E533D2873115615C407A06C
	GFCB121CDBB047DA4407DAC00791C6E7E9469450E646CDA00538E665B218F4AF55B5B4E5EAFACFB521539AE0CEFBE8775A95EE178966BFDED455BA48F0FB5E23CD321CDB8131C1D8FD08650866083F80764BF652B8F61EA6B54AA9527D3F1595C6E4332DDBD2658A5996E4149CECD27B5CADD0AEA97841A63F12D3BC258B39AD039EBAED80E46A4F1AF4BF7D2635DC26A83C7ADABC244A4B5315892F30646A2710C91935A7763E17B1EFBA6309E837BEE954CFDAF1E20E4AE8F51770CDF6E34246AFA16187A3676
	E12E5FA5394E057B8C3DBEA87D83C2371B6834B66FA7B5033886ED8B7AD8ED342DE53654E75DAC641654070CC3DC324DD2AED39B1A65E0DB9611BE23E45A67EAEE4B4B049EEF22D3DB86889D715950364D191CCD6B6B674DB8D6A7821EGB08BE0BE40D200DC977039D80D3D5D38BB429AEB90D56D3057A15BA5D52032BFEFFC8B2E12CFD345BA17C793DDDDD20B28BA44CED97288BA4FD30D98034EG390524763B200F4FA517240A1AC3F101F651A9E9126A21BCAFB62EC79ED5A22D4D8E27849DB85D82537B47
	468DF015C50FF65CED97B54960206D2FB6E2CE86449650888A601E46650D63982FAA703F83C066F1501AD05FD1C9F5C9724103750A5267F5B723A742AC9746793F495C918577F1AE966357DB10A6A84959058A53F3DFD31EC9CF15238B4D97D5BF9BE3DBBF4579F81742EA6677ED2CE62A565CA56C7A651110B5C35A95DAAB9DCA7058790868C43BA87E602136B1466DAEA574180AFC2E786918FDE4DE247E35BCD70EB64C55F830B59EE0B60F7173CDD6DA3307C50F442A74CB958ACC329B9127F63E74E93E9ECF
	34E739EE7FAA6EC7BAE9DFG3A85F7F2768E0086G1F1B45F2785336B072976B5B48D8DECF7A88563F7A5F54870DD9EAB3BE2724814925D5C9EE49E5079B0F5E76DCB376270F63EE054B3CC868EE987715B050BC1C7E07E69C1B2226290ECE2FA6B5C91A66F075986D2F103EBC5ECE37B1E5303E7E560CBEAA03B06EB9FF66F19CB3DD0A4BA51164F1913DFDF98B5601A4CBCEB03AD2713AB4CADF5D02F55FA77975B9E3DD8BAE0086C4590B738FDDAFCC97B10EEE5561041240A9CCD86A46711C4E57BB8871C043
	F54FDC38263A59589C5C4A46663621BB0459DFEF3704FA36445DD5E5A96F276329215F981B54A621B5346D59E4BE51699EEA6BB7DB10573CF4216360DE22438D3A4783CC69E7636052D6ECE7F30DBEGAA95D926E16471D55AF2B19FD23FD702A99AA73523BCE683EDF9C1EDADFA7CE87B39B60C1FEC1A8FE3D50C5FC71F3135608C30E79E4062FE9A43DD509E85C079FC950CEFA160692A41D768C94E9E76B03EC49B630B81DA2247601B8B775BB4463760B0DF03822D81402DD13E69F04D72B23E2DD50C2F90E8
	45G8DDE4A978357B4AFDBCF1EBE7FF7C1DFCF1E9187C4DF11E790EA796041A3FC71C8570E1DEDAB75B1394C4B56080F3BB0AF2787E03EG0899E0F57333B5EC0D985B97AFAC3C736FE1573F098334DE668D18568163589AF1A969B3E7B0B9FB53A04B79DFDA4C4FA91B4723F4B948A3D24F5774003A223487DB5F04EB37C375E8F5B070FDAC378F37223D788C55497A1A2265B3363EA906FEFB81968FA27D876B734DE3E610BE8B9740B513687B41E7FBF0DEA045D7C4AF8D125A2D28CE493E8323DBA48EEAC5E4
	8EAA321166EA3C5F49466A661CDAEA274422FF582BE930A8696A3349B21578BAD8EFBDD16C559CF2915176E56799A87FCFF636503CAD7B0C2D797779D8FE26EE2850FD18779FB78B6567B7D8C22D61A72EFB57A27BE8DEE2FDC6FE063F131D39781F2F2F20313A230245EAA170947AD92CA68C060AD5031F551D3774F8C8DFABE5C9D4DB9C52A00EB5B295F8AE54F10155EC290D79EE07FE293DBF33B1FB27GEDBE3777C3F7A8FBF306183DE99FFE99525EA6070C9347C5DACA6D6CB94C6C348EB1BBB3AA191DED
	C00B99E2F6B679C259B9F70855E0EF653FD603379C7ABCFCDEAE523FFBAA19FF295C3F7AAA66DF7E59646C7A334C3F6F960472EF7CD9665F83D57F1AFFA9C76BC27A57A8F52B12277722EB6D65AA66E35CD936F66AE943F5CD8A600B4EDE7CFA93A19D64F3068D9DD638277BA2D541B86E9B267B2282016D17AEA374DE931D46BD054C5908CF01365AEF58DAFEDCBB2E8716821EFB8E5601G95G2781224E31393732ED2FB046DE294E85DB9817A8B785ACFE581E248D63836B959D7FEB9B66042CD78436966C78
	EC2CBD7814F3013124F2E746141B9DA4C76BE1F1A11406F11E6268F496F9871CC5E2A794115D21998FC2DAA6BC5FCB57E33E510F8567583C7ACD0D7939B7EB5D9E4B6A9073AAE9BFCFED19F15E18D7F7F9DFDBC1FD4928C0DF161E0F3CE6E88EBF6A40E77E4AA8D8AFG148334818C8344814C0AE2F93C7C44213172C82A3CD92984DAAD2CFDAA035726BC6E77D4DCDC3E9E9CD3AEB5AAD00E46FD6EA0467DDD0CCF949BE765B654B57D124863C5651E34DD1C6D7FBB265C1CCBC259AE3A5176775037CB186D72E1
	5495F769055A3E227262ECAF98D3AE6952D036DF69E3F11FF3A933BD09680A9AF72136B7D6DD1C6D2747140B9D974A765F95206DF8261CB40E597E8A5115BBBEF25B69B94895E4CD3A8D644A81A8EDBE21B5GEE673433A7106683BC06532634A3ED8660F3B96DFEA75266833EF8BC334D276C002BF0990CE300FAGB7C0AC408C0079GD993E07E87A81F40E42EA2B2ED93685E81577B3E89F4FF00FB935CF74481BE93E081177915ABDE77CD507BC01D4CDF6CEB34F8540F520E764DB735EF9BC397C2395EF76C
	E521F21F21B0DB719902736D86934F1A886AAEDC9B6E65571970659154C201FE8DBE232073EBF4FB7833066B5AC31FB55C549E7A2C61767675148EBA4CA7102C7D1E76514FA26EEF8FF59671AE09638B9A7AB1625C618B51A969F186FBBFDB2573D4EBCAE5894F472B6B2743B55DD4AFB9170F4C31B584AD987681CFADC047A83CEE3DEDFC90CF9C60C9G3381661B5A524268224F499927F00D398CF88AA742D8812885681D481E0F55A747DC67D47BC22EF36818AE2C40B1BD917445F11D259D61EB62530E50B5
	D15501F56DD13CEA17941C77239D053C4DF8BA57E9129353C7F91A093CA62ABD52083AB9C964E9DBB8F9FBC7385A223571CC0539B676705A7849A056C662C4A38733A60E4CC7CA88DAB0AC821EF4001CAB4273DA7536FDC1BC3500F7G38CD7471F09FE74213CC77B4F7CDE44FB89B68A9D730BD475A43E6BF77F2BF3B5D6867C2130E25A16CDCF96558FEEE821EE200AA93EFDB101C2C63D1C174D8409381E68124185A96196E532F8C76F39C59F366DC151C3D69AA66672715E6BF77F1BF2F76211F3B2EB2F4D4
	DCB552276690B4B374C27BB0C08CC042A83CB37536F97AF521FEDD8A571551E0EF74C8596290B46A670F485E338A5A1B23191FD7D4197D5C4F7DFC39G77EBF613AE9F5CC76BF83CFE1D21DF6742B595E0C9083E33A6415C8230EBD2F05C57D623BD95C0EF18446C09A7E70C22D7D3BCDD2AA24B9EC11B043CB3493A82778F27B96FEF5B700CD7F10B7D38E922FC1693EC6FB9F392BB2F7876A06A6CD6C5A78C59A63754A5F4CCC65EEBCF30F35F39401B8A308CE0933415CF66FB6CF66C4389F30343A588891371
	3D7A1F6C0240467DFFB9D13722BAE08E23E7EF540EE71C4C5EB42E6B6375589F5BC4B5C9B0DB0A1A229E15E092D4CB74FC943CE36289F12E2277FB1F2FE0841F3EEF6736D5950C525FDEFD5E2A5FB0C28FB95F50F52CEE4D9F51CE4F60E8FB4F2977C34B871EDBF11BA61F45F89F6146D4091A445A7E68A5ED75E227A4233839AD25854F2D0FD4B6A8EE2F3B46A149F6EC2567A40FA866F312CDD753F312662B59B94ACF87C335777276577D6676E13DBD01376FAAB5374F545BD3F97B3A96F37B923DFDDB8CDB57
	1664E1AD14815E40E95792DA8760EECE7B95210D87BC11535EA034D90027F05A8D8758FABA15533ECBE842B5B0562EE13497487EF09760651CD6CB76AE35001F6634E777A3CD83BC1653D692FD09004F6134FAC2DBGF89A2745905AB26C77DAC65BCD7B853C76DA764C1FF5AA7CB37FC727C2BF739FBB95FA9D784429515701CF1E9A7DD93D3B825F954110AE19F8AD5ADBCD708EEE7FF29F4B57BCCE6B7631FCAD6034FBFDACDF0BB96DAFBE16AF61BAC63B2D0C65AB07537E330C652B1853127DACDFD51C36F5
	08652B015322CBC7666B41D2169BBB67FB3514654647E93BC99F5100A7F15AD9BF4B57ECCE1BCDE87381DFFA9D1B3F4E58826396E58B0C5BEDEE96371531BC167DACEE791C76C57F48BA3F331F45ED87671BDD4A62360F539616323835F15A764DEC9FAAF3DA4AFBAC16D11C365F4AE2994DE94BCE0C0CE5C3BB0BE5BC670BEBE763E18627BD574E62BB1753328AD8ACD3B92D21E0E4ACEFBE9C984B690783E379A82923A520E365750CD6C4F2128F788ECE9BA434F240CFF35ABCC253G0F6534C2D2C70908F35A
	E2BE0F4C65349C925F4500E75D40E896B21E8B815F41E9BD04368F70FACE0BA37348E940FD1C76BF04968DF892279DA07D4E86BC05534E915AA240059B99CDA4670A79006F3811456DB71501F1DBC270F2AE137F964A74829E45E9E789AD9A70F8CE1BC3E8B3G1F43E9BBB7B21FD2B86D5ABAE29760AB63783CCAE81BGAF67340CE3645C8370531CD6F70C7924F15AD4016C07G1F42E98F3432BCA4F25AAFDAD99E6645B15F37D7877A7E8941977276A74899F237A8FBF04FCA4FE163874D5F5F153DD99A723D
	B5C24ECD7A792BFE1DF579D6FD3E9CBC017DE82AD722E7C8B3CF3035CD9B70465C043467CF181F01A8E783A882206D056D66F61C1FF786349F89E8A75FBCE41B5B07835A51075D3ABC7573475B59F973329FA78A72039F07FD4F3AC077EF19FEEDE859AAEC3B727595D44F93A74D714A3D79FDCBC698BD7BEE267295B79B67567A5A4C359D2E26D748C5CD78AFECAD7143A84DD3D47BE5E3DD7B17DFB45B6A85F6761EFD844F60A2B8FB67ABA77D1D2642F221019D14C61FDFE8CB524DEC8DFC0D36C98879EEBBC8
	5F9EB2CFE0AC96F159BFF5970616A5ABB5AA57C5668DFC46EF3A0549B9248F4CBE7549BDC5E73A70C9DE540D8BB8D0E0DA4E696713B5AD6C5DFA54AD2C2E9FF4A30E5FF3B6BB5C4D30CE505761AFF68B82FB26EFA24F8F27CBF2AAAEC797DD03BFCC56CE63GE6F2DDD2BFFE27568B8B76A185D66CB22AEBD0BC8E7A1184150B6AE7B11D4B659E6A9F398F788A175AC27AADA8732D8366EFC802016AF825947D00CD084369F5B6B906A43D3FE7495CD7B1856652A92C3F975EA3E38E2C23FDDD62B0FC2D3792BD22
	AFC8CFBF493B8FF40C67FAB610F1B3A0291A23AB50DBAA73D1BB73F5BA17393F9D47CE27029F11B4088E17A661060772179CE0353510737734232D0EAE2653CBF848DA18733CC6F43AD529DBD2D549CE2CE6BA3FCF572E37427A68D6467FCE8156926EG95B03E5A05DB893B4E5FB648ECAE637CA7AB70DD02CB9A3406FCBD39C36BF5F8F66AF345A149BC460B4FE6D8B2430C717ADB756F6CEEB566C41A0F974E885C7E2BC95CFF8D908DFC14768B9F33F18A50284C1B32D1AF8F160DD6AFD4478BE40DB88F6497
	F09D9FB90DFE6B49D84D3A8D6EEFE37DBED34AE2DF4EE9DB081EFA405BEEE3712A7113EFAA71FBE917E6D3A53146A14B26BC1F7633BC0F67FA927C4C27D82E2727E0C340F832490E9E97FEEF226B39240049A45E46FC10DC068FFB49D83B87E081EFDF63B66274E46958F1DAC77CCB8379255C56AFB633FE2D37B33DDF759B7D5A49DAF09F34D55C4EE43EE9E73113B9EDEEBB0B43B027EDB8C956930047F15ABE6E671C5BD9EC1E71051E37B455984B557E1173D68721DD0EB1613AA67B471E3726717C24F13967
	485E566E70108154A46177E0123D0E0F553F7A194F0571CCE6775048396EC60F11134CD1F3C2F53C549AFA2E5BCF6AF38774350F777776E060DC7710E64427E2E36839AE064C8127C1074C755C3FBF47642B6163237B190FF11C77557D234F0B9B8E703A65B2478E844EF3DDFC1EDB40F9CE9F8BBF4F5DC3785300F7A967772D4773B6A9E0FE3BFABD2F5DBB98DFF6D5A4739B657D47DB216B2EF040086B35EF0D2C3BE904F6970E8B5E6FD3EF85666613C1E38C351175F29570B6F07EE9F5E357691CBA665FB017
	7BDB5D6879A8BC02B2982BA4AED3F8ACB09FC547D8BED26FE06352CFE80B81CF67F25F97C6567737FCC6FDBF5EBA761CE3216FBE27427EFDAA537BD5F5A4B9226FDD5A49386BGD95FD4F6C6D2A67D832EAD62997DADCBB5AC7C94153CE681E2032A38A1BCFECAA5DFE66AE735BFB8F1F13F5578618A36E61E7150D4E16604D7B342FD2391B7D57FDD0FFECDEB5ECA685417B9C3982FB4E84B3A9376EAG95G4DG721D4C3773925A08EF0B40FC1B2609DD3D642BC47692894F7B8CB29E52D25DAF1CC5CB290EC7
	246D6162839A0EC04CE9201B459E013FABF5DF5C6FDF5E740DA697F56748F3EEAAD7BA4464E2C370A482ED96C00A290D4A7516067A06FE114E172EDFF39282F5DA815F17B0321FDA20F5A48477F1340559668E12998FF89CC09240ECD39B2D6B1F676175835B87BAFEE3BF1E790E323F292C7E22393A1157595CD3663D19759FD9A177E66927F6DB666BFD27A798FB2A79E7162D20E3B47A9B015B14BA98B05670C337AA87148E7E40E3C7C9F171AE6B1E3DBB75317D50B9C3EE7E7EC86474737376C87D247C3587
	58BE19625184F7F9E5195A7174F9430EED9159CFF915C1733E1E0E0BAB85725B113B58781A635892EEECG913FDDEA52D4B01E75FF370F64D4E6B9FD4997292F5417A59906AF5F73C71E0B42ACC36E125248E370675240D8FE325C944B087A574FB27C01B9FA58EF0EAD254595DCD85E0F7413D8B6FD207B784FEC43363FEF5E9C5E360FCB28E9BA6F0F5FB35B22BF17D6983A7EF8A2F2DD316DACDE94FF361D79CD75262CB4743E1A9F395E2385A3636571937FABD9AD4D76DFD87C720782F37B27D506ED2F8C0C
	E2DB99330D4AEDD9ED48CD5A9F091C3ECE5F9F982B8F8FDC187DC3C1633CA1473023E5FD64F65C3DFEE4ECE73FC5E25B48EA4B334650FDF95DA83A8F846AFE2A6E42FC7AF37D987C073FECEE7E7298672FBE9698039B569AF65EA8C4E2A7157B5BBAC36E5B03114851796FEBBFF6485F795065115E56CD76C03095519C226C98A22FE969ECC867521F66E2FB854C259DGD1G31G09FCEEFD490FF3AB7E7AF1643A255A5EA33120307EFEB2947EDD577C2131FFA77974907EC623D351B445097A997D39A11C8CB4
	45CD081416B60403C5F57474EA14D5277F1E1C8D48D2B7A153B36577FD6C1CF94EDD7CDD8C61CB83BC6BEEC6FB023FF7A96434BF90DAB9604D1CD6CE56303D00C7F15A1504AF967004A0D9641B4EE9EF9359B90027F11A534FECD946E91308EC6EBD495956FB986D45F34C16FD1CF66FF172EE8770DECE6BECE17A06B9AD4D0334984093B9ED93FF8FB20B534A89EDA160691CE651101613181C3DAD11FDABDE1807EB5FB2401B81FCGB1G89G3392D97D748E6F9042BF1B6BC53F6255C88D29F858C6E5A613BD
	0AC7FFE6D35A2FF24357555339E357557339E83F8475506CF76B5FB57DB617FCC3515D6D11F87DAE494D6534BA8F5B07513DD119EF6C3D3FB494FE8FFC0E54DB8A44A695006AFCBE0273046C325067891B49594F4AE94959795358BE2C499F7A4C200F54D7857055F35EBBCA8377375F4663AC1D3F103E37855E711C3FB802F31823FEF606BEFD9A5B075EC0E8290027F1DD89246FEE50C3F99EA2351EFFAF547A3DCCAE6FBCF92F84F8473DCC6E0747C7BFF3FB6AB86BFFA257F395998BB3G1F4D753C61A1730C
	58555723AADE171D593F115479E260CB3F1765E8EF84675BCD87C267280F0C25427BE05C5C474FFC3450B9AAA1FDB7GDF47FDEC7C3C1F973E6EF7640DDD77A5F9216753523C1173E9F9DE687974F01EF9BE253572AC79FFBD28BFF3CA71BB52B04F2ABD3084AD4677DDD44E7216A1D7629FC52EA4D0EE7BC6C3EE5D98EBE7AAC9651A8A8D39357E511E297BF7D9CB8E70B5C3275FAA0C3A86E82C7B3C361976715D370DBEAEAD0D248FAA17345910FB7A5C16E0399AA8709DDB6C705CD6FA3600CBA03B074BBE71
	1EA1FB7B71487DFA79F864FE3DECB5596799A566FB0A4B4C7B1C273798F2798772757E08CC25A2ABEA51E02FC3536BC858E6703EF281FDEC5DEE48C59F0AC40E7EDFB28939CFE064FFA7AC772842BA4E8F63C752BC8B87769E4CFC6E4ECC0B10D5A7E453C1D3A7E4E16557894B49402893B2E1507848A93D79CF935E31F5753914C1991FB156817D8BB02BC8BE8173748B6702A5D0A687E42C6CEBB1CDA8A0DF0FF10255B62A705AC0EECDA854BF1603CEE55A315503F3DBD88DABF4815633478AEBC7075DDA6570
	38E5519FD6EE8D6DD9E753842B7E751D6431EA3D1255AEEA22D5F111FBCF17AA616C3CC2E7892D92F211B7B2F736493FFEB2696B2AFC0BE001F474CAB47E4B879DF62D17E4GD3EAA79A577FDB311391E3DFC50C9D9A8D53045518C6ABFE57F7504A5B70AFBFGFB530CE526F18C97AD36A0CC9556844CA50F5C317F79F94F34F8B2056CBA61EDFC74B6C9AA5995C878C1A17BAFC2EA04D5EE8556E278E0026277104BF20F661725FAA13F3B91FFA26F51A43B0D6DB7BCD0BE6407113B7154F7F72D68695530A0BF
	299732DC781F1988F934D996DDBD3B69666393123F8CDB85F1A4C72F018D4659B157326161F55B5FBF32E8E626E029915E149551DEA3F6419232CED82575E2C5A9325D677E0CAADAB9485E20E7023AF7387B68EC91AED73851FD3EDB2357242D293140A2C795F693CEDC8995F18EFBE52F28C2D712DA27C964AB01ED3C6D124D5A7F7D64730D9D96A1C3F792C33F536756046CEE516910E1D0E4D711191BA465A38D2CD5C51787FFC78A16D903ACE3CD3C03226B9EAB3EFE5AEB6D2483C3D4935E232184515DBD12
	625CED6B6EF638A42EG628A6A5F656A1127A808A74FF7CD4111DF3E5189F5D0A5E4BA0B0B493FA5645FBD78EF89211490CA499E886E1211A3567C975C6EE3BE13626173AC263F2886B2287D66F70FCDFD2AFC49E2702EC6D8CEFE7889C948B5383E70BABBA5F5C252756F2E0EDDF89566480D2562C33DA53EB0D50FA3A985FADB9B78344938EB63385C2B77FAAD424A9A4C0E6C6E956B74DE6B05551DE4DA17D4605504DCA33AC496CA585285757D0EC16FC2BAEDEDF20BDDD8652B6C225AC7B420A93613422AEE
	352713F420891613B5A721DB02E1F3E53D70A6CC0C1578765927E942C6C3B5795EC0EF2083C4F8837E5C64AF484DGCCBFEB5EF95E7CEEBF7D3242305FBB65BEC04F1837BDE01CB553BDDDCCBEF93788746187589AF461867C3F46423DFBE8F2426386D662FDB6179D5FC39C76EB12713D6A7579285F43F874C65AB6A59F6DBEE31279FFD0CB87884A49B047329BGGCCCEGGD0CB818294G94G88G88G65F854AC4A49B047329BGGCCCEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5
	F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6C9BGGGG
**end of data**/
}
}
