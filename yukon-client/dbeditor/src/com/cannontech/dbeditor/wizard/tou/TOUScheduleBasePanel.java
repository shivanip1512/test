/*
 * Created on Jun 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.dbeditor.wizard.tou;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TOUScheduleBasePanel extends DataInputPanel {

	private javax.swing.JTextField jTextFieldScheduleName = null;
	private javax.swing.JComboBox jComboBoxDefaultRate = null;
	private javax.swing.JComboBox jComboBoxTuesday = null;
	private javax.swing.JComboBox jComboBoxWednesday = null;
	private javax.swing.JComboBox jComboBoxFriday = null;
	private javax.swing.JComboBox jComboBoxHoliday = null;
	private javax.swing.JComboBox jComboBoxSunday = null;
	private javax.swing.JComboBox jComboBoxMonday = null;
	private javax.swing.JComboBox jComboBoxThursday = null;
	private javax.swing.JLabel jLabelScheduleName = null;
	private javax.swing.JLabel jLabelDefaultRate = null;
	private javax.swing.JComboBox jComboBoxSaturday = null;
	private javax.swing.JLabel jLabelSunday = null;
	private javax.swing.JLabel jLabelMonday = null;
	private javax.swing.JLabel jLabelTuesday = null;
	private javax.swing.JLabel jLabelWednesday = null;
	private javax.swing.JLabel jLabelFriday = null;
	private javax.swing.JLabel jLabelSaturday = null;
	private javax.swing.JLabel jLabelHoliday = null;
	private javax.swing.JLabel jLabelThursday = null;
	private javax.swing.JPanel jPanelDayEditor = null;
	private javax.swing.JLabel jLabelDayName = null;
	private javax.swing.JTextField jTextFieldDayName = null;
	private javax.swing.JTable jTableRateOffsets = null;
	/**
	 * This method initializes 
	 * 
	 */
	public TOUScheduleBasePanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        this.setLayout(null);
        this.add(getJTextFieldScheduleName(), null);
        this.add(getJComboBoxDefaultRate(), null);
        this.add(getJComboBoxTuesday(), null);
        this.add(getJComboBoxWednesday(), null);
        this.add(getJComboBoxFriday(), null);
        this.add(getJComboBoxHoliday(), null);
        this.add(getJComboBoxSunday(), null);
        this.add(getJComboBoxMonday(), null);
        this.add(getJComboBoxThursday(), null);
        this.add(getJLabelScheduleName(), null);
        this.add(getJLabelDefaultRate(), null);
        this.add(getJComboBoxSaturday(), null);
        this.add(getJLabelSunday(), null);
        this.add(getJLabelMonday(), null);
        this.add(getJLabelTuesday(), null);
        this.add(getJLabelWednesday(), null);
        this.add(getJLabelFriday(), null);
        this.add(getJLabelSaturday(), null);
        this.add(getJLabelHoliday(), null);
        this.add(getJLabelThursday(), null);
        this.add(getJPanelDayEditor(), null);
        this.setSize(383, 404);
			
	}
	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.util.DataInputPanel#getValue(java.lang.Object)
	 */
	public Object getValue(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(java.lang.Object)
	 */
	public void setValue(Object o) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method initializes jTextFieldScheduleName
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldScheduleName() {
		if(jTextFieldScheduleName == null) {
			jTextFieldScheduleName = new javax.swing.JTextField();
			jTextFieldScheduleName.setBounds(162, 13, 183, 22);
		}
		return jTextFieldScheduleName;
	}
	/**
	 * This method initializes jComboBoxDefaultRate
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxDefaultRate() {
		if(jComboBoxDefaultRate == null) {
			jComboBoxDefaultRate = new javax.swing.JComboBox();
			jComboBoxDefaultRate.setBounds(162, 47, 95, 25);
		}
		return jComboBoxDefaultRate;
	}
	/**
	 * This method initializes jComboBoxTuesday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxTuesday() {
		if(jComboBoxTuesday == null) {
			jComboBoxTuesday = new javax.swing.JComboBox();
			jComboBoxTuesday.setSize(92, 21);
			jComboBoxTuesday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxTuesday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxTuesday.setPreferredSize(new java.awt.Dimension(92,21));
			jComboBoxTuesday.setLocation(53, 143);
		}
		return jComboBoxTuesday;
	}
	/**
	 * This method initializes jComboBoxWednesday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxWednesday() {
		if(jComboBoxWednesday == null) {
			jComboBoxWednesday = new javax.swing.JComboBox();
			jComboBoxWednesday.setSize(92, 21);
			jComboBoxWednesday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxWednesday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxWednesday.setPreferredSize(new java.awt.Dimension(92,21));
			jComboBoxWednesday.setLocation(53, 171);
		}
		return jComboBoxWednesday;
	}
	/**
	 * This method initializes jComboBoxFriday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxFriday() {
		if(jComboBoxFriday == null) {
			jComboBoxFriday = new javax.swing.JComboBox();
			jComboBoxFriday.setSize(92, 21);
			jComboBoxFriday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxFriday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxFriday.setPreferredSize(new java.awt.Dimension(92,21));
			jComboBoxFriday.setLocation(53, 232);
		}
		return jComboBoxFriday;
	}
	/**
	 * This method initializes jComboBoxHoliday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxHoliday() {
		if(jComboBoxHoliday == null) {
			jComboBoxHoliday = new javax.swing.JComboBox();
			jComboBoxHoliday.setSize(92, 21);
			jComboBoxHoliday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxHoliday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxHoliday.setPreferredSize(new java.awt.Dimension(92,21));
			jComboBoxHoliday.setLocation(53, 292);
		}
		return jComboBoxHoliday;
	}
	/**
	 * This method initializes jComboBoxSunday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxSunday() {
		if(jComboBoxSunday == null) {
			jComboBoxSunday = new javax.swing.JComboBox();
			jComboBoxSunday.setBounds(53, 85, 92, 21);
			jComboBoxSunday.setPreferredSize(new java.awt.Dimension(92,21));
			jComboBoxSunday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxSunday.setMaximumSize(new java.awt.Dimension(92,21));
		}
		return jComboBoxSunday;
	}
	/**
	 * This method initializes jComboBoxMonday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxMonday() {
		if(jComboBoxMonday == null) {
			jComboBoxMonday = new javax.swing.JComboBox();
			jComboBoxMonday.setBounds(53, 115, 92, 21);
			jComboBoxMonday.setPreferredSize(new java.awt.Dimension(92,21));
			jComboBoxMonday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxMonday.setMaximumSize(new java.awt.Dimension(92,21));
		}
		return jComboBoxMonday;
	}
	/**
	 * This method initializes jComboBoxThursday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxThursday() {
		if(jComboBoxThursday == null) {
			jComboBoxThursday = new javax.swing.JComboBox();
			jComboBoxThursday.setSize(92, 21);
			jComboBoxThursday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxThursday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxThursday.setPreferredSize(new java.awt.Dimension(92,21));
			jComboBoxThursday.setLocation(53, 201);
		}
		return jComboBoxThursday;
	}
	/**
	 * This method initializes jLabelScheduleName
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelScheduleName() {
		if(jLabelScheduleName == null) {
			jLabelScheduleName = new javax.swing.JLabel();
			jLabelScheduleName.setBounds(9, 13, 125, 20);
			jLabelScheduleName.setText("Schedule Name: ");
			jLabelScheduleName.setName("jLabelScheduleName");
		}
		return jLabelScheduleName;
	}
	/**
	 * This method initializes jLabelDefaultRate
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelDefaultRate() {
		if(jLabelDefaultRate == null) {
			jLabelDefaultRate = new javax.swing.JLabel();
			jLabelDefaultRate.setBounds(9, 47, 125, 20);
			jLabelDefaultRate.setText("Default Rate: ");
			jLabelDefaultRate.setPreferredSize(new java.awt.Dimension(125,20));
			jLabelDefaultRate.setMaximumSize(new java.awt.Dimension(125,20));
			jLabelDefaultRate.setMinimumSize(new java.awt.Dimension(125,20));
		}
		return jLabelDefaultRate;
	}
	/**
	 * This method initializes jComboBoxSaturday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxSaturday() {
		if(jComboBoxSaturday == null) {
			jComboBoxSaturday = new javax.swing.JComboBox();
			jComboBoxSaturday.setSize(92, 21);
			jComboBoxSaturday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxSaturday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxSaturday.setPreferredSize(new java.awt.Dimension(92,21));
			jComboBoxSaturday.setLocation(53, 262);
		}
		return jComboBoxSaturday;
	}
	/**
	 * This method initializes jLabelSunday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelSunday() {
		if(jLabelSunday == null) {
			jLabelSunday = new javax.swing.JLabel();
			jLabelSunday.setSize(31, 21);
			jLabelSunday.setText("Sun");
			jLabelSunday.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			jLabelSunday.setMaximumSize(new java.awt.Dimension(31,21));
			jLabelSunday.setMinimumSize(new java.awt.Dimension(31,21));
			jLabelSunday.setPreferredSize(new java.awt.Dimension(31,21));
			jLabelSunday.setLocation(9, 85);
		}
		return jLabelSunday;
	}
	/**
	 * This method initializes jLabelMonday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelMonday() {
		if(jLabelMonday == null) {
			jLabelMonday = new javax.swing.JLabel();
			jLabelMonday.setSize(31, 21);
			jLabelMonday.setText("Mon");
			jLabelMonday.setMaximumSize(new java.awt.Dimension(31,21));
			jLabelMonday.setMinimumSize(new java.awt.Dimension(31,21));
			jLabelMonday.setPreferredSize(new java.awt.Dimension(31,21));
			jLabelMonday.setLocation(9, 115);
		}
		return jLabelMonday;
	}
	/**
	 * This method initializes jLabelTuesday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelTuesday() {
		if(jLabelTuesday == null) {
			jLabelTuesday = new javax.swing.JLabel();
			jLabelTuesday.setBounds(9, 143, 31, 21);
			jLabelTuesday.setText("Tue");
			jLabelTuesday.setPreferredSize(new java.awt.Dimension(31,21));
			jLabelTuesday.setMinimumSize(new java.awt.Dimension(31,21));
			jLabelTuesday.setMaximumSize(new java.awt.Dimension(31,21));
		}
		return jLabelTuesday;
	}
	/**
	 * This method initializes jLabelWednesday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelWednesday() {
		if(jLabelWednesday == null) {
			jLabelWednesday = new javax.swing.JLabel();
			jLabelWednesday.setSize(31, 21);
			jLabelWednesday.setText("Wed");
			jLabelWednesday.setMaximumSize(new java.awt.Dimension(31,21));
			jLabelWednesday.setMinimumSize(new java.awt.Dimension(31,21));
			jLabelWednesday.setPreferredSize(new java.awt.Dimension(31,21));
			jLabelWednesday.setLocation(9, 171);
		}
		return jLabelWednesday;
	}
	/**
	 * This method initializes jLabelFriday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelFriday() {
		if(jLabelFriday == null) {
			jLabelFriday = new javax.swing.JLabel();
			jLabelFriday.setSize(31, 21);
			jLabelFriday.setText("Fri");
			jLabelFriday.setMaximumSize(new java.awt.Dimension(31,21));
			jLabelFriday.setMinimumSize(new java.awt.Dimension(31,21));
			jLabelFriday.setPreferredSize(new java.awt.Dimension(31,21));
			jLabelFriday.setLocation(9, 232);
		}
		return jLabelFriday;
	}
	/**
	 * This method initializes jLabelSaturday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelSaturday() {
		if(jLabelSaturday == null) {
			jLabelSaturday = new javax.swing.JLabel();
			jLabelSaturday.setSize(31, 21);
			jLabelSaturday.setText("Sat");
			jLabelSaturday.setPreferredSize(new java.awt.Dimension(31,21));
			jLabelSaturday.setLocation(9, 262);
			jLabelSaturday.setMinimumSize(new java.awt.Dimension(31,21));
			jLabelSaturday.setMaximumSize(new java.awt.Dimension(31,21));
		}
		return jLabelSaturday;
	}
	/**
	 * This method initializes jLabelHoliday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelHoliday() {
		if(jLabelHoliday == null) {
			jLabelHoliday = new javax.swing.JLabel();
			jLabelHoliday.setSize(31, 21);
			jLabelHoliday.setText("Hol");
			jLabelHoliday.setMaximumSize(new java.awt.Dimension(31,21));
			jLabelHoliday.setMinimumSize(new java.awt.Dimension(31,21));
			jLabelHoliday.setPreferredSize(new java.awt.Dimension(31,21));
			jLabelHoliday.setLocation(9, 292);
		}
		return jLabelHoliday;
	}
	/**
	 * This method initializes jLabelThursday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelThursday() {
		if(jLabelThursday == null) {
			jLabelThursday = new javax.swing.JLabel();
			jLabelThursday.setBounds(9, 201, 31, 21);
			jLabelThursday.setText("Thu");
			jLabelThursday.setMaximumSize(new java.awt.Dimension(31,21));
			jLabelThursday.setMinimumSize(new java.awt.Dimension(31,21));
			jLabelThursday.setPreferredSize(new java.awt.Dimension(31,21));
		}
		return jLabelThursday;
	}
	/**
	 * This method initializes jPanelDayEditor
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelDayEditor() {
		if(jPanelDayEditor == null) {
			jPanelDayEditor = new javax.swing.JPanel();
			jPanelDayEditor.setLayout(null);
			jPanelDayEditor.add(getJLabelDayName(), null);
			jPanelDayEditor.add(getJTextFieldDayName(), null);
			jPanelDayEditor.add(getJTableRateOffsets(), null);
			jPanelDayEditor.setBounds(157, 85, 220, 312);
			jPanelDayEditor.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TOU Day Editor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, null, null));
			jPanelDayEditor.setPreferredSize(new java.awt.Dimension(220,312));
			jPanelDayEditor.setMinimumSize(new java.awt.Dimension(220,312));
			jPanelDayEditor.setMaximumSize(new java.awt.Dimension(220,312));
		}
		return jPanelDayEditor;
	}
	/**
	 * This method initializes jLabelDayName
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelDayName() {
		if(jLabelDayName == null) {
			jLabelDayName = new javax.swing.JLabel();
			jLabelDayName.setBounds(16, 24, 55, 17);
			jLabelDayName.setText("Name: ");
			jLabelDayName.setMaximumSize(new java.awt.Dimension(55,17));
			jLabelDayName.setMinimumSize(new java.awt.Dimension(55,17));
		}
		return jLabelDayName;
	}
	/**
	 * This method initializes jTextFieldDayName
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldDayName() {
		if(jTextFieldDayName == null) {
			jTextFieldDayName = new javax.swing.JTextField();
			jTextFieldDayName.setBounds(84, 24, 127, 20);
			jTextFieldDayName.setPreferredSize(new java.awt.Dimension(127,20));
			jTextFieldDayName.setMinimumSize(new java.awt.Dimension(127,20));
			jTextFieldDayName.setMaximumSize(new java.awt.Dimension(127,20));
		}
		return jTextFieldDayName;
	}
	/**
	 * This method initializes jTableRateOffsets
	 * 
	 * @return javax.swing.JTable
	 */
	private javax.swing.JTable getJTableRateOffsets() {
		if(jTableRateOffsets == null) {
			jTableRateOffsets = new javax.swing.JTable();
			jTableRateOffsets.setBounds(16, 58, 190, 243);
			jTableRateOffsets.setCellSelectionEnabled(true);
		}
		return jTableRateOffsets;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
