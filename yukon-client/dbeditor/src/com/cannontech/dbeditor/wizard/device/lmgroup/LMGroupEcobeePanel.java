package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.database.data.device.lm.LMGroupEcobee;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;

public class LMGroupEcobeePanel extends DataInputPanel implements ActionListener, CaretListener {

    private static final long serialVersionUID = 1L;
    private JPanel deviceClassPanel = null;

    public LMGroupEcobeePanel() {
        super();
        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());

        GridBagConstraints constraintsJPanelGeoAddressUsage = new GridBagConstraints();
        constraintsJPanelGeoAddressUsage.gridx = 0;
        constraintsJPanelGeoAddressUsage.gridy = 0;
        constraintsJPanelGeoAddressUsage.fill = GridBagConstraints.BOTH;
        constraintsJPanelGeoAddressUsage.weightx = 1.0;
        constraintsJPanelGeoAddressUsage.weighty = 1.0;
        constraintsJPanelGeoAddressUsage.ipady = 5;
        constraintsJPanelGeoAddressUsage.insets = new Insets(0, 2, 2, 0);
        add(getDeviceClassPanel(), constraintsJPanelGeoAddressUsage);

        GridBagConstraints constraintsJPanelEnrollmentPanel = new GridBagConstraints();
        constraintsJPanelEnrollmentPanel.gridx = 0;
        constraintsJPanelEnrollmentPanel.gridy = 1;
        constraintsJPanelEnrollmentPanel.fill = GridBagConstraints.BOTH;
        constraintsJPanelEnrollmentPanel.ipady = 5;
        constraintsJPanelEnrollmentPanel.insets = new Insets(0, 2, 2, 0);
        
    }

    
    
    private JPanel getDeviceClassPanel() {
        if (deviceClassPanel == null) {
            deviceClassPanel = new JPanel();
            deviceClassPanel.setLayout(new GridBagLayout());

            TitleBorder ivjLocalBorder;
            ivjLocalBorder = new TitleBorder();
            ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
            ivjLocalBorder.setTitle("Device Class");
            deviceClassPanel.setBorder(ivjLocalBorder);

        }
        return deviceClassPanel;
    }
    
    @Override
    public boolean isInputValid() {
        return true;
    }

    // Get values from fields and put into the database.
    @Override
    public Object getValue(Object o) {
        Object value = null;
        if (o instanceof SmartMultiDBPersistent) {
            value = ((SmartMultiDBPersistent) o).getOwnerDBPersistent();
        } else {
            value = o;
        }
        if (value instanceof LMGroupEcobee) {
            LMGroupEcobee group = (LMGroupEcobee) value;
        }
        return o;
    }

    // Set the values in the fields from the database.
    @Override
    public void setValue(Object o) {
        Object value = null;
        if (o instanceof SmartMultiDBPersistent) {
            value = ((SmartMultiDBPersistent) o).getOwnerDBPersistent();
        } else {
            value = o;
        }
        if (value instanceof LMGroupEcobee) {
            LMGroupEcobee group = (LMGroupEcobee) value;

        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        fireInputUpdate();
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        fireInputUpdate();
    }

}