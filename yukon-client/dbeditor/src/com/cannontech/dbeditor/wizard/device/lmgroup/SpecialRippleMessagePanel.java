package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
/**
 * Insert the type's description here.
 * Creation date: (10/12/2004 4:55:17 PM)
 * @author: 
 */
public class SpecialRippleMessagePanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjJLabelLG = null;
	private JComboBox<String> ivjJComboBoxBitsAreaCode = null;
	private javax.swing.JLabel ivjJLabelBitsAreaCode = null;
	private javax.swing.JPanel ivjJPanelBitsDoubleOrder = null;
	private JComboBox<String> ivjJComboBoxBitsGroup = null;
	private javax.swing.JLabel ivjJLabelBitsGroup = null;
	private JComboBox<String> ivjShedTimeComboBox = null;
	private javax.swing.JLabel ivjShedTimeLabel = null;
	public static final String CONT_LATCH = "Continuous Latch";
	Map<String, String> MINNKOTADEFS = new HashMap<>(42);
	private SpecialDoubleOrderBitPanel ivjControlDoubleOrderPanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SpecialDoubleOrderBitPanel ivjRestoreDoubleOrderPanel = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SpecialRippleMessagePanel.this.getShedTimeComboBox()) 
				connEtoC3(e);
			if (e.getSource() == SpecialRippleMessagePanel.this.getJComboBoxBitsGroup()) 
				connEtoC4(e);
			if (e.getSource() == SpecialRippleMessagePanel.this.getJComboBoxBitsAreaCode()) 
				connEtoC5(e);
			if (e.getSource() == getControlDoubleOrderPanel().getActionPasser()
				|| e.getSource() == getRestoreDoubleOrderPanel().getActionPasser()) 
				connEtoC1(e);
		};
	};
/**
 * SpecialRippleMessagePanel constructor comment.
 */
public SpecialRippleMessagePanel() {
	super();
	initialize();
}
public void buildDefs()
{
	MINNKOTADEFS.put("TST", "1000000000");
	MINNKOTADEFS.put("1.00", "0100100100");
	MINNKOTADEFS.put("1.01", "0110100000");
	MINNKOTADEFS.put("1.02", "0000110100");
	MINNKOTADEFS.put("2.00", "0010010010");
	MINNKOTADEFS.put("2.01", "0000011010");
	MINNKOTADEFS.put("2.02", "0010000011");
	MINNKOTADEFS.put("2.03", "0011010000");
	MINNKOTADEFS.put("2.04", "0110000010");
	MINNKOTADEFS.put("3.00", "0001001001");
	MINNKOTADEFS.put("3.01", "0001000101");
	MINNKOTADEFS.put("3.06", "0011000001");
	MINNKOTADEFS.put("3.07", "0100001001");
	MINNKOTADEFS.put("3.09", "0001001100");
	MINNKOTADEFS.put("3.01 & 3.09", "0001001101");
	MINNKOTADEFS.put("4.00", "0100010001");
	MINNKOTADEFS.put("4.01", "0000110001");
	MINNKOTADEFS.put("4.02", "0100000011");
	MINNKOTADEFS.put("6.00", "0010001100");
	MINNKOTADEFS.put("6.01", "0110001000");
	MINNKOTADEFS.put("6.06", "0010101000");
	
	MINNKOTADEFS.put("1000000000", "TST");
	MINNKOTADEFS.put("0100100100", "1.00");
	MINNKOTADEFS.put("0110100000", "1.01");
	MINNKOTADEFS.put("0000110100", "1.02");
	MINNKOTADEFS.put("0010010010", "2.00");
	MINNKOTADEFS.put("0000011010", "2.01");
	MINNKOTADEFS.put("0010000011", "2.02");
	MINNKOTADEFS.put("0011010000", "2.03");
	MINNKOTADEFS.put("0110000010", "2.04");
	MINNKOTADEFS.put("0001001001", "3.00");
	MINNKOTADEFS.put("0001000101", "3.01");
	MINNKOTADEFS.put("0011000001", "3.06");
	MINNKOTADEFS.put("0100001001", "3.07");
	MINNKOTADEFS.put("0001001100", "3.09");
	MINNKOTADEFS.put("0001001101", "3.01 & 3.09");
	MINNKOTADEFS.put("0100010001", "4.00");
	MINNKOTADEFS.put("0000110001", "4.01");
	MINNKOTADEFS.put("0100000011", "4.02");
	MINNKOTADEFS.put("0010001100", "6.00");
	MINNKOTADEFS.put("0110001000", "6.01");
	MINNKOTADEFS.put("0010101000", "6.06");
	
	getJComboBoxBitsGroup().addItem("TST");
	getJComboBoxBitsGroup().addItem("1.00");
	getJComboBoxBitsGroup().addItem("1.01");
	getJComboBoxBitsGroup().addItem("1.02");
	getJComboBoxBitsGroup().addItem("2.00");
	getJComboBoxBitsGroup().addItem("2.01");
	getJComboBoxBitsGroup().addItem("2.02");
	getJComboBoxBitsGroup().addItem("2.03");
	getJComboBoxBitsGroup().addItem("2.04");
	getJComboBoxBitsGroup().addItem("3.00");
	getJComboBoxBitsGroup().addItem("3.01");
	getJComboBoxBitsGroup().addItem("3.06");
	getJComboBoxBitsGroup().addItem("3.07");
	getJComboBoxBitsGroup().addItem("3.09");
	getJComboBoxBitsGroup().addItem("3.01 & 3.09");
	getJComboBoxBitsGroup().addItem("4.00");
	getJComboBoxBitsGroup().addItem("4.01");
	getJComboBoxBitsGroup().addItem("4.02");
	getJComboBoxBitsGroup().addItem("6.00");
	getJComboBoxBitsGroup().addItem("6.01");
	getJComboBoxBitsGroup().addItem("6.06");
		
	MINNKOTADEFS.put("000000", "Universal");
	MINNKOTADEFS.put("100110", "Minnkota");
	MINNKOTADEFS.put("100101", "Beltrami");
	MINNKOTADEFS.put("100011", "Cass County");
	MINNKOTADEFS.put("010110", "Cavalier Rural");
	MINNKOTADEFS.put("010101", "Clearwater-Polk");
	MINNKOTADEFS.put("010011", "NoDak Rural");
	MINNKOTADEFS.put("001110", "North Star");
	MINNKOTADEFS.put("001101", "PKM Electric");
	MINNKOTADEFS.put("001011", "Red Lake");
	MINNKOTADEFS.put("110100", "Red River Valley");
	MINNKOTADEFS.put("101100", "Roseau Electric");
	MINNKOTADEFS.put("011100", "Sheyenne Valley");
	MINNKOTADEFS.put("110010", "Wild Rice");
	MINNKOTADEFS.put("101010", "NMPA");
	
	MINNKOTADEFS.put("Universal", "000000");
	MINNKOTADEFS.put("Minnkota", "100110");
	MINNKOTADEFS.put("Beltrami", "100101");
	MINNKOTADEFS.put("Cass County", "100011");
	MINNKOTADEFS.put("Cavalier Rural", "010110");
	MINNKOTADEFS.put("Clearwater-Polk", "010101");
	MINNKOTADEFS.put("NoDak Rural", "010011");
	MINNKOTADEFS.put("North Star", "001110");
	MINNKOTADEFS.put("PKM Electric", "001101");
	MINNKOTADEFS.put("Red Lake", "001011");
	MINNKOTADEFS.put("Red River Valley", "110100");
	MINNKOTADEFS.put("Roseau Electric", "101100");
	MINNKOTADEFS.put("Sheyenne Valley", "011100");
	MINNKOTADEFS.put("Wild Rice", "110010");
	MINNKOTADEFS.put("NMPA", "101010");
	
	getJComboBoxBitsAreaCode().addItem("Beltrami");
	getJComboBoxBitsAreaCode().addItem("Cass County");
	getJComboBoxBitsAreaCode().addItem("Cavalier Rural");
	getJComboBoxBitsAreaCode().addItem("Clearwater-Polk");
	getJComboBoxBitsAreaCode().addItem("Minnkota");
	getJComboBoxBitsAreaCode().addItem("NMPA");
	getJComboBoxBitsAreaCode().addItem("NoDak Rural");
	getJComboBoxBitsAreaCode().addItem("North Star");
	getJComboBoxBitsAreaCode().addItem("PKM Electric");
	getJComboBoxBitsAreaCode().addItem("Red Lake");
	getJComboBoxBitsAreaCode().addItem("Red River Valley");
	getJComboBoxBitsAreaCode().addItem("Roseau Electric");
	getJComboBoxBitsAreaCode().addItem("Sheyenne Valley");
	getJComboBoxBitsAreaCode().addItem("Universal");
	getJComboBoxBitsAreaCode().addItem("Wild Rice");



	


}
/**
 * connEtoC1:  (ControlDoubleOrderPanel.mouse.mousePressed(java.awt.event.MouseEvent) --> SpecialRippleMessagePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (ShedTimeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialRippleMessagePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JComboBoxBitsGroup.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialRippleMessagePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JComboBoxBitsAreaCode.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialRippleMessagePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Return the ControlDoubleOrderPanel property value.
 * @return com.cannontech.dbeditor.wizard.device.lmgroup.SpecialDoubleOrderBitPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SpecialDoubleOrderBitPanel getControlDoubleOrderPanel() {
	if (ivjControlDoubleOrderPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder1.setTitle("CONTROL");
			ivjControlDoubleOrderPanel = new com.cannontech.dbeditor.wizard.device.lmgroup.SpecialDoubleOrderBitPanel();
			ivjControlDoubleOrderPanel.setName("ControlDoubleOrderPanel");
			ivjControlDoubleOrderPanel.setBorder(ivjLocalBorder1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlDoubleOrderPanel;
}
/**
 * Return the JComboBoxBitsAreaCode property value.
 * @return javax.swing.JComboBox
 */
private JComboBox<String> getJComboBoxBitsAreaCode() {
	if (ivjJComboBoxBitsAreaCode == null) {
		try {
			ivjJComboBoxBitsAreaCode = new JComboBox<>();
			ivjJComboBoxBitsAreaCode.setName("JComboBoxBitsAreaCode");
			ivjJComboBoxBitsAreaCode.setPreferredSize(new java.awt.Dimension(120, 23));
			ivjJComboBoxBitsAreaCode.setMaximumSize(new java.awt.Dimension(120, 23));
			ivjJComboBoxBitsAreaCode.setMinimumSize(new java.awt.Dimension(120, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxBitsAreaCode;
}
/**
 * Return the JComboBoxBitsName property value.
 * @return javax.swing.JComboBox
 */
private JComboBox<String> getJComboBoxBitsGroup() {
	if (ivjJComboBoxBitsGroup == null) {
		try {
			ivjJComboBoxBitsGroup = new JComboBox<>();
			ivjJComboBoxBitsGroup.setName("JComboBoxBitsGroup");
			ivjJComboBoxBitsGroup.setPreferredSize(new java.awt.Dimension(75, 23));
			ivjJComboBoxBitsGroup.setMaximumSize(new java.awt.Dimension(75, 23));
			ivjJComboBoxBitsGroup.setMinimumSize(new java.awt.Dimension(75, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxBitsGroup;
}
/**
 * Return the JLabelBitsAreaCode property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelBitsAreaCode() {
	if (ivjJLabelBitsAreaCode == null) {
		try {
			ivjJLabelBitsAreaCode = new javax.swing.JLabel();
			ivjJLabelBitsAreaCode.setName("JLabelBitsAreaCode");
			ivjJLabelBitsAreaCode.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelBitsAreaCode.setText("Area Code: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelBitsAreaCode;
}
/**
 * Return the JLabelBitsName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelBitsGroup() {
	if (ivjJLabelBitsGroup == null) {
		try {
			ivjJLabelBitsGroup = new javax.swing.JLabel();
			ivjJLabelBitsGroup.setName("JLabelBitsGroup");
			ivjJLabelBitsGroup.setText("Group: ");
			ivjJLabelBitsGroup.setMaximumSize(new java.awt.Dimension(65, 19));
			ivjJLabelBitsGroup.setPreferredSize(new java.awt.Dimension(65, 19));
			ivjJLabelBitsGroup.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelBitsGroup.setMinimumSize(new java.awt.Dimension(65, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelBitsGroup;
}
/**
 * Return the JLabelLG property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLG() {
	if (ivjJLabelLG == null) {
		try {
			ivjJLabelLG = new javax.swing.JLabel();
			ivjJLabelLG.setName("JLabelLG");
			ivjJLabelLG.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelLG.setText("LG");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLG;
}
/**
 * Return the JPanelBitsDoubleOrder property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelBitsDoubleOrder() {
	if (ivjJPanelBitsDoubleOrder == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Double Orders");
			ivjJPanelBitsDoubleOrder = new javax.swing.JPanel();
			ivjJPanelBitsDoubleOrder.setName("JPanelBitsDoubleOrder");
			ivjJPanelBitsDoubleOrder.setBorder(ivjLocalBorder);
			ivjJPanelBitsDoubleOrder.setLayout(new java.awt.GridBagLayout());
			ivjJPanelBitsDoubleOrder.setMaximumSize(new java.awt.Dimension(341, 243));
			ivjJPanelBitsDoubleOrder.setPreferredSize(new java.awt.Dimension(341, 243));
			ivjJPanelBitsDoubleOrder.setMinimumSize(new java.awt.Dimension(341, 243));

			java.awt.GridBagConstraints constraintsControlDoubleOrderPanel = new java.awt.GridBagConstraints();
			constraintsControlDoubleOrderPanel.gridx = 1; constraintsControlDoubleOrderPanel.gridy = 1;
			constraintsControlDoubleOrderPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsControlDoubleOrderPanel.weightx = 1.0;
			constraintsControlDoubleOrderPanel.weighty = 1.0;
			constraintsControlDoubleOrderPanel.ipadx = 3;
			constraintsControlDoubleOrderPanel.ipady = -36;
			constraintsControlDoubleOrderPanel.insets = new java.awt.Insets(25, 5, 8, 6);
			getJPanelBitsDoubleOrder().add(getControlDoubleOrderPanel(), constraintsControlDoubleOrderPanel);

			java.awt.GridBagConstraints constraintsRestoreDoubleOrderPanel = new java.awt.GridBagConstraints();
			constraintsRestoreDoubleOrderPanel.gridx = 1; constraintsRestoreDoubleOrderPanel.gridy = 2;
			constraintsRestoreDoubleOrderPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRestoreDoubleOrderPanel.weightx = 1.0;
			constraintsRestoreDoubleOrderPanel.weighty = 1.0;
			constraintsRestoreDoubleOrderPanel.ipadx = 3;
			constraintsRestoreDoubleOrderPanel.ipady = -36;
			constraintsRestoreDoubleOrderPanel.insets = new java.awt.Insets(8, 5, 15, 6);
			getJPanelBitsDoubleOrder().add(getRestoreDoubleOrderPanel(), constraintsRestoreDoubleOrderPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelBitsDoubleOrder;
}
/**
 * Return the ControlDoubleOrderPanel1 property value.
 * @return com.cannontech.dbeditor.wizard.device.lmgroup.SpecialDoubleOrderBitPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SpecialDoubleOrderBitPanel getRestoreDoubleOrderPanel() {
	if (ivjRestoreDoubleOrderPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder2.setTitle("RESTORE");
			ivjRestoreDoubleOrderPanel = new com.cannontech.dbeditor.wizard.device.lmgroup.SpecialDoubleOrderBitPanel();
			ivjRestoreDoubleOrderPanel.setName("RestoreDoubleOrderPanel");
			ivjRestoreDoubleOrderPanel.setBorder(ivjLocalBorder2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRestoreDoubleOrderPanel;
}
/**
 * Return the ShedTimeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JComboBox<String> getShedTimeComboBox() {
	if (ivjShedTimeComboBox == null) {
		try {
			javax.swing.plaf.metal.MetalComboBoxEditor.UIResource ivjLocalEditor;
			ivjLocalEditor = new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource();
			ivjLocalEditor.setItem("7.5");
			javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource ivjLocalRenderer;
			ivjLocalRenderer = new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource();
			ivjLocalRenderer.setName("LocalRenderer");
			ivjLocalRenderer.setText("Never");
			ivjLocalRenderer.setMaximumSize(new java.awt.Dimension(35, 16));
			ivjLocalRenderer.setMinimumSize(new java.awt.Dimension(35, 16));
			ivjLocalRenderer.setForeground(new java.awt.Color(0, 0, 0));
			ivjShedTimeComboBox = new JComboBox<>();
			ivjShedTimeComboBox.setName("ShedTimeComboBox");
			ivjShedTimeComboBox.setEditor(ivjLocalEditor);
			ivjShedTimeComboBox.setRenderer(ivjLocalRenderer);
			ivjShedTimeComboBox.setSelectedIndex(-1);
			ivjShedTimeComboBox.setMaximumSize(new java.awt.Dimension(120, 23));
			ivjShedTimeComboBox.setPreferredSize(new java.awt.Dimension(120, 23));
			ivjShedTimeComboBox.setMinimumSize(new java.awt.Dimension(120, 23));
			// user code begin {1}
			ivjShedTimeComboBox.addItem( "7.5 minute" );
			ivjShedTimeComboBox.addItem( "15  minute" );
			ivjShedTimeComboBox.addItem( "30  minute" );
			ivjShedTimeComboBox.addItem( "60  minute" );
			ivjShedTimeComboBox.addItem( CONT_LATCH );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShedTimeComboBox;
}
/**
 * Return the ShedTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getShedTimeLabel() {
	if (ivjShedTimeLabel == null) {
		try {
			ivjShedTimeLabel = new javax.swing.JLabel();
			ivjShedTimeLabel.setName("ShedTimeLabel");
			ivjShedTimeLabel.setText("Shed Time:");
			ivjShedTimeLabel.setMaximumSize(new java.awt.Dimension(70, 19));
			ivjShedTimeLabel.setPreferredSize(new java.awt.Dimension(65, 19));
			ivjShedTimeLabel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjShedTimeLabel.setMinimumSize(new java.awt.Dimension(65, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShedTimeLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMGroupRipple group = null;
	
	if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		group = (com.cannontech.database.data.device.lm.LMGroupRipple)
					((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	else
		group = (com.cannontech.database.data.device.lm.LMGroupRipple) o;
		
	//shed time is 0(Infinite) if the selected shed is the string
	Integer shedTime = new Integer(0);
	String val = getShedTimeComboBox().getSelectedItem().toString();
		
	if( !CONT_LATCH.equalsIgnoreCase(val) )
		shedTime = CtiUtilities.getIntervalSecondsValueFromDecimal( 
			getShedTimeComboBox().getSelectedItem().toString() );
			
	StringBuffer controlBuffer = new StringBuffer();
	StringBuffer restoreBuffer = new StringBuffer();
	
	//Group name
	controlBuffer.append(MINNKOTADEFS.get(getJComboBoxBitsGroup().getSelectedItem()));	
	restoreBuffer.append(MINNKOTADEFS.get(getJComboBoxBitsGroup().getSelectedItem()));
	
	//Area code
	controlBuffer.append(MINNKOTADEFS.get(getJComboBoxBitsAreaCode().getSelectedItem()));	
	restoreBuffer.append(MINNKOTADEFS.get(getJComboBoxBitsAreaCode().getSelectedItem()));
	
	//Double orders
	controlBuffer.append(getControlDoubleOrderPanel().getDoubleOrderBitString());
	restoreBuffer.append(getRestoreDoubleOrderPanel().getDoubleOrderBitString());
	
	//do we want only 48 bits out of the 50 total for Minnkota?
	/*controlBuffer.append("00");
	restoreBuffer.append("00");*/
	
	group.getLmGroupRipple().setControl(controlBuffer.toString());
	group.getLmGroupRipple().setRestore(restoreBuffer.toString());
	group.getLmGroupRipple().setShedTime(shedTime);
	
	return o;		
	
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
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
	getShedTimeComboBox().addActionListener(ivjEventHandler);
	getJComboBoxBitsGroup().addActionListener(ivjEventHandler);
	getJComboBoxBitsAreaCode().addActionListener(ivjEventHandler);
	
	getControlDoubleOrderPanel().getActionPasser().addActionListener(ivjEventHandler);
	getRestoreDoubleOrderPanel().getActionPasser().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SpecialRippleMessagePanel");
		setPreferredSize(new java.awt.Dimension(353, 378));
		setLayout(new java.awt.GridBagLayout());
		setSize(372, 360);
		setMaximumSize(new java.awt.Dimension(353, 378));
		setMinimumSize(new java.awt.Dimension(353, 378));

		java.awt.GridBagConstraints constraintsJLabelBitsGroup = new java.awt.GridBagConstraints();
		constraintsJLabelBitsGroup.gridx = 1; constraintsJLabelBitsGroup.gridy = 2;
		constraintsJLabelBitsGroup.insets = new java.awt.Insets(10, 24, 7, 0);
		add(getJLabelBitsGroup(), constraintsJLabelBitsGroup);

		java.awt.GridBagConstraints constraintsJLabelLG = new java.awt.GridBagConstraints();
		constraintsJLabelLG.gridx = 2; constraintsJLabelLG.gridy = 2;
		constraintsJLabelLG.ipadx = 5;
		constraintsJLabelLG.ipady = 1;
		constraintsJLabelLG.insets = new java.awt.Insets(9, 0, 10, 1);
		add(getJLabelLG(), constraintsJLabelLG);

		java.awt.GridBagConstraints constraintsJComboBoxBitsGroup = new java.awt.GridBagConstraints();
		constraintsJComboBoxBitsGroup.gridx = 3; constraintsJComboBoxBitsGroup.gridy = 2;
		constraintsJComboBoxBitsGroup.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxBitsGroup.weightx = 1.0;
		constraintsJComboBoxBitsGroup.insets = new java.awt.Insets(7, 1, 6, 182);
		add(getJComboBoxBitsGroup(), constraintsJComboBoxBitsGroup);

		java.awt.GridBagConstraints constraintsJLabelBitsAreaCode = new java.awt.GridBagConstraints();
		constraintsJLabelBitsAreaCode.gridx = 1; constraintsJLabelBitsAreaCode.gridy = 3;
		constraintsJLabelBitsAreaCode.ipady = 5;
		constraintsJLabelBitsAreaCode.insets = new java.awt.Insets(9, 24, 9, 0);
		add(getJLabelBitsAreaCode(), constraintsJLabelBitsAreaCode);

		java.awt.GridBagConstraints constraintsJComboBoxBitsAreaCode = new java.awt.GridBagConstraints();
		constraintsJComboBoxBitsAreaCode.gridx = 3; constraintsJComboBoxBitsAreaCode.gridy = 3;
		constraintsJComboBoxBitsAreaCode.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxBitsAreaCode.weightx = 1.0;
		constraintsJComboBoxBitsAreaCode.insets = new java.awt.Insets(6, 1, 8, 137);
		add(getJComboBoxBitsAreaCode(), constraintsJComboBoxBitsAreaCode);

		java.awt.GridBagConstraints constraintsJPanelBitsDoubleOrder = new java.awt.GridBagConstraints();
		constraintsJPanelBitsDoubleOrder.gridx = 1; constraintsJPanelBitsDoubleOrder.gridy = 4;
		constraintsJPanelBitsDoubleOrder.gridwidth = 3;
		constraintsJPanelBitsDoubleOrder.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelBitsDoubleOrder.weightx = 1.0;
		constraintsJPanelBitsDoubleOrder.weighty = 1.0;
		constraintsJPanelBitsDoubleOrder.ipadx = 23;
		constraintsJPanelBitsDoubleOrder.ipady = -19;
		constraintsJPanelBitsDoubleOrder.insets = new java.awt.Insets(8, 3, 5, 5);
		add(getJPanelBitsDoubleOrder(), constraintsJPanelBitsDoubleOrder);

		java.awt.GridBagConstraints constraintsShedTimeComboBox = new java.awt.GridBagConstraints();
		constraintsShedTimeComboBox.gridx = 3; constraintsShedTimeComboBox.gridy = 1;
		constraintsShedTimeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsShedTimeComboBox.weightx = 1.0;
		constraintsShedTimeComboBox.insets = new java.awt.Insets(19, 1, 8, 137);
		add(getShedTimeComboBox(), constraintsShedTimeComboBox);

		java.awt.GridBagConstraints constraintsShedTimeLabel = new java.awt.GridBagConstraints();
		constraintsShedTimeLabel.gridx = 1; constraintsShedTimeLabel.gridy = 1;
		constraintsShedTimeLabel.insets = new java.awt.Insets(25, 24, 6, 0);
		add(getShedTimeLabel(), constraintsShedTimeLabel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	buildDefs();
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SpecialRippleMessagePanel aSpecialRippleMessagePanel;
		aSpecialRippleMessagePanel = new SpecialRippleMessagePanel();
		frame.setContentPane(aSpecialRippleMessagePanel);
		frame.setSize(aSpecialRippleMessagePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	if (o instanceof com.cannontech.database.data.device.lm.LMGroupRipple)
	{
		Integer shedTimeSec = ((com.cannontech.database.data.device.lm.LMGroupRipple) o).getLmGroupRipple().getShedTime();
		StringBuffer control = new StringBuffer(((com.cannontech.database.data.device.lm.LMGroupRipple) o).getLmGroupRipple().getControl());
		StringBuffer restore = new StringBuffer(((com.cannontech.database.data.device.lm.LMGroupRipple) o).getLmGroupRipple().getRestore());

	  	if(shedTimeSec.intValue() == 0)
	  		getShedTimeComboBox().setSelectedItem(CONT_LATCH);
	  	else
            SwingUtil.setIntervalComboBoxSelectedItem(getShedTimeComboBox(), shedTimeSec.intValue());

		//Group name
		getJComboBoxBitsGroup().setSelectedItem(MINNKOTADEFS.get(control.subSequence(0, 10).toString()));
		
		//Area code
		getJComboBoxBitsAreaCode().setSelectedItem(MINNKOTADEFS.get(control.subSequence(10, 16).toString()));
		
		//Double Orders
		getControlDoubleOrderPanel().setDoubleOrderBitString(control.substring(16).toString());
		getRestoreDoubleOrderPanel().setDoubleOrderBitString(restore.substring(16).toString());
	}
	
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getJComboBoxBitsGroup().requestFocus(); 
        } 
    });    
}

}
