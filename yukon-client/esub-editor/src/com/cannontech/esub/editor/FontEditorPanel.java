package com.cannontech.esub.editor;

/**
 * Insert the type's description here.
 * Creation date: (2/7/00 11:13:37 AM)
 * @author: 
 */

import java.awt.Font;

public class FontEditorPanel extends javax.swing.JPanel {
	private Font selectedFont = null;
	private java.awt.GraphicsEnvironment graphEnv = null;
	private javax.swing.JComboBox ivjJComboBoxName = null;
	private javax.swing.JComboBox ivjJComboBoxSize = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JLabel ivjJLabelSize = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	protected transient com.cannontech.tdc.utils.FontEditorPanelListener fieldFontEditorPanelListenerEventMulticaster = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == FontEditorPanel.this.getJComboBoxName()) 
				connEtoC3(e);
			if (e.getSource() == FontEditorPanel.this.getJComboBoxSize()) 
				connEtoC5(e);
			if (e.getSource() == FontEditorPanel.this.getJButtonCancel()) 
				connEtoC1(e);
			if (e.getSource() == FontEditorPanel.this.getJButtonOk()) 
				connEtoC2(e);
		};
	};
/**
 * FontEditorPanel constructor comment.
 */
public FontEditorPanel() {
	super();
	initialize();
}
/**
 * FontEditorPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public FontEditorPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * FontEditorPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public FontEditorPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * FontEditorPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public FontEditorPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * 
 * @param newListener com.cannontech.tdc.utils.FontEditorPanelListener
 */
public void addFontEditorPanelListener(com.cannontech.tdc.utils.FontEditorPanelListener newListener) {
	fieldFontEditorPanelListenerEventMulticaster = com.cannontech.tdc.utils.FontEditorPanelListenerEventMulticaster.add(fieldFontEditorPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC1:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> FontEditorPanel.fireJButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonCancelAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> FontEditorPanel.fireJButtonOkAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonOkAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JComboBoxName.action.actionPerformed(java.awt.event.ActionEvent) --> FontEditorPanel.fontChanged()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fontChanged();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JComboBoxSize.action.actionPerformed(java.awt.event.ActionEvent) --> FontEditorPanel.fontChanged()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fontChanged();
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
protected void fireJButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldFontEditorPanelListenerEventMulticaster == null) {
		return;
	};
	fieldFontEditorPanelListenerEventMulticaster.JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldFontEditorPanelListenerEventMulticaster == null) {
		return;
	};
	fieldFontEditorPanelListenerEventMulticaster.JButtonOkAction_actionPerformed(newEvent);
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/00 11:50:28 AM)
 */
private void fontChanged() 
{
	selectedFont = new Font( getJComboBoxName().getSelectedItem().toString(),
									Font.PLAIN,
									new Integer( getJComboBoxSize().getSelectedItem().toString() ).intValue() );
	
	getJLabel1().setFont( selectedFont );
	
	getJLabel1().invalidate();	
	getJLabel1().repaint();
}
/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic('C');
			ivjJButtonCancel.setText("Cancel");
			ivjJButtonCancel.setMaximumSize(new java.awt.Dimension(100, 27));
			ivjJButtonCancel.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjJButtonCancel.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}
/**
 * Return the JButtonOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('O');
			ivjJButtonOk.setText("Ok");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}
/**
 * Return the JComboBoxName property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxName() {
	if (ivjJComboBoxName == null) {
		try {
			ivjJComboBoxName = new javax.swing.JComboBox();
			ivjJComboBoxName.setName("JComboBoxName");
			ivjJComboBoxName.setBackground(java.awt.Color.white);
			ivjJComboBoxName.setMaximumRowCount(7);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxName;
}
/**
 * Return the JComboBoxSize property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxSize() {
	if (ivjJComboBoxSize == null) {
		try {
			ivjJComboBoxSize = new javax.swing.JComboBox();
			ivjJComboBoxSize.setName("JComboBoxSize");
			ivjJComboBoxSize.setBackground(java.awt.Color.white);
			// user code begin {1}

			ivjJComboBoxSize.addItem("10");
			ivjJComboBoxSize.addItem("12");
			ivjJComboBoxSize.addItem("14");
			ivjJComboBoxSize.addItem("18");
			ivjJComboBoxSize.addItem("21");
			ivjJComboBoxSize.addItem("23");
			ivjJComboBoxSize.addItem("26");
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxSize;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("This is some sample text.");
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabelName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setText("Name");
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
 * Return the JLabelSize property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSize() {
	if (ivjJLabelSize == null) {
		try {
			ivjJLabelSize = new javax.swing.JLabel();
			ivjJLabelSize.setName("JLabelSize");
			ivjJLabelSize.setText("Size");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSize;
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/00 1:09:17 PM)
 * @return java.awt.Font
 */
public Font getSelectedFont() {
	return selectedFont;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBoxName().addActionListener(ivjEventHandler);
	getJComboBoxSize().addActionListener(ivjEventHandler);
	getJButtonCancel().addActionListener(ivjEventHandler);
	getJButtonOk().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("FontEditorPanel");
		setPreferredSize(new java.awt.Dimension(417, 150));
		setLayout(new java.awt.GridBagLayout());
		setSize(406, 189);
		setMinimumSize(new java.awt.Dimension(413, 150));
		setMaximumSize(new java.awt.Dimension(417, 150));

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelName.ipadx = 12;
		constraintsJLabelName.insets = new java.awt.Insets(14, 12, 25, 74);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJComboBoxName = new java.awt.GridBagConstraints();
		constraintsJComboBoxName.gridx = 1; constraintsJComboBoxName.gridy = 1;
		constraintsJComboBoxName.gridwidth = 2;
		constraintsJComboBoxName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxName.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJComboBoxName.weightx = 1.0;
		constraintsJComboBoxName.insets = new java.awt.Insets(12, 52, 25, 16);
		add(getJComboBoxName(), constraintsJComboBoxName);

		java.awt.GridBagConstraints constraintsJLabelSize = new java.awt.GridBagConstraints();
		constraintsJLabelSize.gridx = 3; constraintsJLabelSize.gridy = 1;
		constraintsJLabelSize.anchor = java.awt.GridBagConstraints.NORTHEAST;
		constraintsJLabelSize.ipadx = 8;
		constraintsJLabelSize.insets = new java.awt.Insets(14, 16, 25, 12);
		add(getJLabelSize(), constraintsJLabelSize);

		java.awt.GridBagConstraints constraintsJComboBoxSize = new java.awt.GridBagConstraints();
		constraintsJComboBoxSize.gridx = 3; constraintsJComboBoxSize.gridy = 1;
		constraintsJComboBoxSize.gridwidth = 2;
		constraintsJComboBoxSize.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxSize.anchor = java.awt.GridBagConstraints.NORTHEAST;
		constraintsJComboBoxSize.weightx = 1.0;
		constraintsJComboBoxSize.insets = new java.awt.Insets(12, 45, 25, 15);
		add(getJComboBoxSize(), constraintsJComboBoxSize);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 1; constraintsJLabel1.gridy = 2;
		constraintsJLabel1.gridwidth = 4;
		constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel1.insets = new java.awt.Insets(26, 6, 17, 15);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJButtonOk = new java.awt.GridBagConstraints();
		constraintsJButtonOk.gridx = 2; constraintsJButtonOk.gridy = 3;
		constraintsJButtonOk.gridwidth = 2;
		constraintsJButtonOk.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		constraintsJButtonOk.ipadx = 34;
		constraintsJButtonOk.insets = new java.awt.Insets(17, 74, 18, 8);
		add(getJButtonOk(), constraintsJButtonOk);

		java.awt.GridBagConstraints constraintsJButtonCancel = new java.awt.GridBagConstraints();
		constraintsJButtonCancel.gridx = 4; constraintsJButtonCancel.gridy = 3;
		constraintsJButtonCancel.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		constraintsJButtonCancel.insets = new java.awt.Insets(17, 0, 18, 15);
		add(getJButtonCancel(), constraintsJButtonCancel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Initialize the names.
 */

public void initializeFontNames() 
{

	if( graphEnv == null )
		graphEnv = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
			
	initNameComboBox();

}
/**
 * Insert the method's description here.
 * Creation date: (2/7/00 11:22:48 AM)
 */
private void initNameComboBox() 
{
	int fontCount = graphEnv.getAvailableFontFamilyNames().length;
	
	// Visual Age takes forever to return all fonts
	if( System.getProperty("java.vm.name").equalsIgnoreCase("IBM VisualAge VM") )
		fontCount = 5;
			
	for( int i = 0; i < fontCount; i++ )
		getJComboBoxName().addItem( graphEnv.getAvailableFontFamilyNames()[i] );
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		FontEditorPanel aFontEditorPanel;
		aFontEditorPanel = new FontEditorPanel();
		frame.setContentPane(aFontEditorPanel);
		frame.setSize(aFontEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * 
 * @param newListener com.cannontech.tdc.utils.FontEditorPanelListener
 */
public void removeFontEditorPanelListener(com.cannontech.tdc.utils.FontEditorPanelListener newListener) {
	fieldFontEditorPanelListenerEventMulticaster = com.cannontech.tdc.utils.FontEditorPanelListenerEventMulticaster.remove(fieldFontEditorPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 11:02:52 AM)
 * Version: <version>
 * @param newFont java.awt.Font
 */
public void setSelectedFont(Font newFont) 
{
	getJComboBoxName().setSelectedItem( newFont.getFontName() );
	getJComboBoxSize().setSelectedItem( String.valueOf( newFont.getSize() ) );
}
}
