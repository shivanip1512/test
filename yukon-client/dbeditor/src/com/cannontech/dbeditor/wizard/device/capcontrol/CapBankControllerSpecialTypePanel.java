/*
 * Created on Apr 29, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.dbeditor.wizard.device.capcontrol;

import com.cannontech.common.gui.util.DataInputPanel;

/*
 * @author jdayton
 *
 * GUI NOTE: Websphere GUI builder over a previous Visual Age code shell
 */
public class CapBankControllerSpecialTypePanel extends DataInputPanel {

	private javax.swing.ButtonGroup buttonGroup = null;
	private javax.swing.JRadioButton jRadioButton7020 = null;
	private javax.swing.JRadioButton jRadioButton7025 = null;
	private javax.swing.JRadioButton jRadioButton7030 = null;
	private javax.swing.JRadioButton jRadioButton7010 = null;
	/**
	 * This method initializes 
	 * 
	 */
	public CapBankControllerSpecialTypePanel() {
		super();
		initialize();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        java.awt.GridBagConstraints consGridBagConstraints12 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints13 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints15 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints14 = new java.awt.GridBagConstraints();
        consGridBagConstraints12.insets = new java.awt.Insets(5,90,4,113);
        consGridBagConstraints12.ipady = -3;
        consGridBagConstraints12.ipadx = 67;
        consGridBagConstraints12.gridy = 1;
        consGridBagConstraints12.gridx = 0;
        consGridBagConstraints15.insets = new java.awt.Insets(36,90,4,113);
        consGridBagConstraints15.ipady = -3;
        consGridBagConstraints15.ipadx = 67;
        consGridBagConstraints15.gridy = 0;
        consGridBagConstraints15.gridx = 0;
        consGridBagConstraints13.insets = new java.awt.Insets(5,90,4,113);
        consGridBagConstraints13.ipady = -3;
        consGridBagConstraints13.ipadx = 67;
        consGridBagConstraints13.gridy = 2;
        consGridBagConstraints13.gridx = 0;
        consGridBagConstraints14.insets = new java.awt.Insets(5,90,106,113);
        consGridBagConstraints14.ipady = -3;
        consGridBagConstraints14.ipadx = 67;
        consGridBagConstraints14.gridy = 3;
        consGridBagConstraints14.gridx = 0;
        this.setLayout(new java.awt.GridBagLayout());
        this.add(getJRadioButton7020(), consGridBagConstraints12);
        this.add(getJRadioButton7025(), consGridBagConstraints13);
        this.add(getJRadioButton7030(), consGridBagConstraints14);
        this.add(getJRadioButton7010(), consGridBagConstraints15);
        this.setSize(350, 253);
			
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
	 * This method initializes jRadioButton7020
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton7020() {
		if(jRadioButton7020 == null) {
			jRadioButton7020 = new javax.swing.JRadioButton();
			jRadioButton7020.setText("CBC 7020");
			jRadioButton7020.setEnabled(false);
			
			getButtonGroup().add(jRadioButton7020);
		}
		return jRadioButton7020;
	}
	/**
	 * This method initializes jRadioButton7025
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton7025() {
		if(jRadioButton7025 == null) {
			jRadioButton7025 = new javax.swing.JRadioButton();
			jRadioButton7025.setText("CBC 7025");
			jRadioButton7025.setEnabled(false);
			
			getButtonGroup().add(jRadioButton7025);
		}
		return jRadioButton7025;
	}
	/**
	 * This method initializes jRadioButton7030
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton7030() {
		if(jRadioButton7030 == null) {
			jRadioButton7030 = new javax.swing.JRadioButton();
			jRadioButton7030.setText("CBC 7030");
			jRadioButton7030.setEnabled(false);
			
			getButtonGroup().add(jRadioButton7030);
		}
		return jRadioButton7030;
	}
	/**
	 * This method initializes jRadioButton7010
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton7010() {
		if(jRadioButton7010 == null) {
			jRadioButton7010 = new javax.swing.JRadioButton();
			jRadioButton7010.setText("CBC 7010");
			jRadioButton7010.setSelected(true);
			
			getButtonGroup().add(jRadioButton7010);
		}
		return jRadioButton7010;
	}
	
	public javax.swing.ButtonGroup getButtonGroup() 
	{
		if( buttonGroup == null )
			buttonGroup = new javax.swing.ButtonGroup();
		
		return buttonGroup;
	}
	
	public int getSelectedType() 
	{
		if( getJRadioButton7010().isSelected() )
			return com.cannontech.database.data.pao.DeviceTypes.CBC_7010;
		/*else if( getJRadioButtonCBC7000Series().isSelected() )
			return com.cannontech.database.data.pao.PAOGroups.CBC_7000SERIES;
		if( getJRadioButtonExpresscomCBC().isSelected() )
			return com.cannontech.database.data.pao.PAOGroups.CBC_EXPRESSCOM;*/
		else //oops! slacker!!!
			return com.cannontech.database.data.pao.PAOGroups.INVALID;

	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
