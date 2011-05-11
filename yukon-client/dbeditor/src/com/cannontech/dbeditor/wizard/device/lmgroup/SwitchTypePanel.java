package com.cannontech.dbeditor.wizard.device.lmgroup;
/**
 * This type was created in VisualAge.
 */
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ClientRights;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.roles.application.DBEditorRole;

public class SwitchTypePanel extends com.cannontech.common.gui.util.DataInputPanel 
{
	private javax.swing.JLabel ivjSelectLabel = null;
	private javax.swing.JList ivjSwitchList = null;
	private javax.swing.JScrollPane ivjSwitchListScrollPane = null;
	
	//hex value representing some privileges of the user on this machine
	public static final long SHOW_PROTOCOL = Long.parseLong( 
		ClientSession.getInstance().getRolePropertyValue(
		DBEditorRole.OPTIONAL_PRODUCT_DEV, "0"), 16 );

    private static final PaoType [] GROUP_LIST = {
        PaoType.LM_GROUP_DIGI_SEP,
        PaoType.LM_GROUP_EMETCON,
        PaoType.LM_GROUP_EXPRESSCOMM,
        PaoType.LM_GROUP_GOLAY,
        PaoType.LM_GROUP_MCT,
        PaoType.LM_GROUP_POINT,
        PaoType.LM_GROUP_RIPPLE,
        PaoType.LM_GROUP_VERSACOM
    };  

	private static final PaoType[] GROUP_LIST_SA = {
	    PaoType.LM_GROUP_DIGI_SEP,
	    PaoType.LM_GROUP_EMETCON,
        PaoType.LM_GROUP_EXPRESSCOMM,
        PaoType.LM_GROUP_GOLAY,
        PaoType.LM_GROUP_MCT,
        PaoType.LM_GROUP_POINT,
        PaoType.LM_GROUP_RIPPLE,
        PaoType.LM_GROUP_SA205,
        PaoType.LM_GROUP_SA305,
        PaoType.LM_GROUP_SADIGITAL,
        PaoType.LM_GROUP_VERSACOM
    };
	
/**
 * Constructor
 */
public SwitchTypePanel() {
	super();
	initialize();
}

private void initSwitchList() {
	try {
		getSwitchList().setListData(this.getGroupList());
		getSwitchList().setSelectionInterval(0, 0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * Returns an array of available PaoTypes based on configuration settings.
 */
private PaoType[] getGroupList() {
	
	//normally we cannot show SA protocol groups, this checks the 
	//specific property.
	if((SHOW_PROTOCOL & ClientRights.SHOW_ADDITIONAL_PROTOCOLS) != 0) {
		return SwitchTypePanel.GROUP_LIST_SA;
	}
	return SwitchTypePanel.GROUP_LIST;		
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200 );
}


/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSelectLabel() {
	if (ivjSelectLabel == null) {
		try {
			ivjSelectLabel = new javax.swing.JLabel();
			ivjSelectLabel.setName("SelectLabel");
			ivjSelectLabel.setText("Select the type of switch:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectLabel;
}


/**
 * Return the SwitchList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getSwitchList() {
	if (ivjSwitchList == null) {
		try {
			ivjSwitchList = new javax.swing.JList();
			ivjSwitchList.setName("SwitchList");
			//ivjSwitchList.setPreferredSize(new java.awt.Dimension(300, 400));
			ivjSwitchList.setBounds(0, 0, 300, 122);
			// user code begin {1}

			ivjSwitchList.setFont( new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12 ) );
			ivjSwitchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			ivjSwitchList.setCellRenderer(new DefaultListCellRenderer() {
			    @Override
			    public Component getListCellRendererComponent(JList list,
			            Object value, int index, boolean isSelected,
			            boolean cellHasFocus) {
			        
			        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			        PaoType paoType = (PaoType) value;
			        label.setText(paoType.getDbString());
			        return label;
			    }
			});

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSwitchList;
}


/**
 * Return the SwitchListScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getSwitchListScrollPane() {
	if (ivjSwitchListScrollPane == null) {
		try {
			ivjSwitchListScrollPane = new javax.swing.JScrollPane();
			ivjSwitchListScrollPane.setName("SwitchListScrollPane");
			ivjSwitchListScrollPane.setPreferredSize(new java.awt.Dimension(300, 153));
			ivjSwitchListScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			getSwitchListScrollPane().setViewportView(getSwitchList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSwitchListScrollPane;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getTypeOfSwitchSelected() {
	
	//normally we cannot show SA protocol groups, this checks the 
	//specific property.
    return ((PaoType)getSwitchList().getSelectedValue()).getDeviceTypeId();
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public String getTypeOfSwitchSelectedString() 
{	
	//normally we cannot show SA protocol groups, this checks the 
	//specific property.
    return ((PaoType)getSwitchList().getSelectedValue()).getDbString();
}


/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	return LMFactory.createLoadManagement( getTypeOfSwitchSelected() );
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SwitchTypePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsSelectLabel = new java.awt.GridBagConstraints();
		constraintsSelectLabel.gridx = 1; constraintsSelectLabel.gridy = 1;
		constraintsSelectLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSelectLabel.ipady = 1;
		constraintsSelectLabel.insets = new java.awt.Insets(13, 25, 2, 183);
		add(getSelectLabel(), constraintsSelectLabel);

		java.awt.GridBagConstraints constraintsSwitchListScrollPane = new java.awt.GridBagConstraints();
		constraintsSwitchListScrollPane.gridx = 1; constraintsSwitchListScrollPane.gridy = 2;
		constraintsSwitchListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsSwitchListScrollPane.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSwitchListScrollPane.weightx = 1.0;
		constraintsSwitchListScrollPane.weighty = 1.0;
		constraintsSwitchListScrollPane.ipadx = 278;
		constraintsSwitchListScrollPane.ipady = 131;
		constraintsSwitchListScrollPane.insets = new java.awt.Insets(2, 25, 15, 25);
		add(getSwitchListScrollPane(), constraintsSwitchListScrollPane);
		initConnections();
		initSwitchList();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}


/**
 * setValue method comment.
 */
public void setValue(Object o) {
	//nothing to set
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getSwitchListScrollPane().requestFocus(); 
        } 
    });    
}

}