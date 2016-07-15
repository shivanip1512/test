package com.cannontech.dbeditor.wizard.changetype.device;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService.ChangeDeviceTypeInfo;
import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.RfnBase;
import com.cannontech.database.data.device.RfnMeterBase;
import com.cannontech.spring.YukonSpringHook;

public class RfnOptionPanel extends DataInputPanel implements CaretListener {
    
    private int currentDeviceId;
    private JLabel manufacturerLabel;
    private JLabel modelLabel;
    private JLabel serialNumberLabel;
    private JTextField manufacturerTextField;
    private JTextField modelTextField;
    private JTextField serialNumberTextField;
    private Font textFont = new Font("dialog", 0, 14);

    public RfnOptionPanel() {
        super();
        initialize();
    }
    
    private void initialize() {
        setLayout(new GridBagLayout());
        setSize(351, 264);
        setPreferredSize(new Dimension(350, 260));
        setBorder(new TitledBorder("RFN Meter Settings"));

        GridBagConstraints serialNumberLabelConstraint = new GridBagConstraints();
        serialNumberLabelConstraint.gridx = 0; serialNumberLabelConstraint.gridy = 0;
        serialNumberLabelConstraint.anchor = GridBagConstraints.WEST;
        serialNumberLabelConstraint.insets = new Insets(3, 3, 3, 3);
        add(getSerialNumberLabel(), serialNumberLabelConstraint);
        
        GridBagConstraints serialNumberTextFieldConstraint = new GridBagConstraints();
        serialNumberTextFieldConstraint.gridx = 1; serialNumberTextFieldConstraint.gridy = 0;
        serialNumberTextFieldConstraint.fill = GridBagConstraints.BOTH;
        serialNumberTextFieldConstraint.anchor = GridBagConstraints.WEST;
        serialNumberTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
        add(getSerialNumberTextField(), serialNumberTextFieldConstraint);

        GridBagConstraints manufacturerLabelConstraint = new GridBagConstraints();
        manufacturerLabelConstraint.gridx = 0; manufacturerLabelConstraint.gridy = 1;
        manufacturerLabelConstraint.anchor = GridBagConstraints.WEST;
        manufacturerLabelConstraint.insets = new Insets(3, 3, 3, 3);
        add(getManufacturerLabel(), manufacturerLabelConstraint);
        
        GridBagConstraints manufacturerTextFieldConstraint = new GridBagConstraints();
        manufacturerTextFieldConstraint.gridx = 1; manufacturerTextFieldConstraint.gridy = 1;
        manufacturerTextFieldConstraint.weightx = 1.0;
        manufacturerTextFieldConstraint.fill = GridBagConstraints.BOTH;
        manufacturerTextFieldConstraint.anchor = GridBagConstraints.WEST;
        manufacturerTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
        add(getManufacturerTextField(), manufacturerTextFieldConstraint);
        
        GridBagConstraints modelLabelConstraint = new GridBagConstraints();
        modelLabelConstraint.gridx = 0; modelLabelConstraint.gridy = 2;
        modelLabelConstraint.anchor = GridBagConstraints.WEST;
        modelLabelConstraint.insets = new Insets(3, 3, 3, 3);
        add(getModelLabel(), modelLabelConstraint);
        
        GridBagConstraints modelTextFieldConstraint = new GridBagConstraints();
        modelTextFieldConstraint.gridx = 1; modelTextFieldConstraint.gridy = 2;
        modelTextFieldConstraint.fill = GridBagConstraints.BOTH;
        modelTextFieldConstraint.anchor = GridBagConstraints.WEST;
        modelTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
        add(getModelTextField(), modelTextFieldConstraint);
                
        initConnections();
    }
    
    private void initConnections() {
        getManufacturerTextField().addCaretListener(this);
        getModelTextField().addCaretListener(this);
        getSerialNumberTextField().addCaretListener(this);
    }
    
    @Override
    public void setFirstFocus() {
        /* Make sure that when its time to display this panel, the focus starts in the top component */
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                getSerialNumberTextField().requestFocus();
            }
        });
    }
    
    @Override
    public void caretUpdate(CaretEvent e) {
        fireInputUpdate();
    }

    @Override
    public Object getValue(Object o) throws EditorInputValidationException {
      
        RfnDeviceDao rfnDeviceDao = YukonSpringHook.getBean("rfnDeviceDao", RfnDeviceDao.class);
        
        String serialNumber = StringUtils.trimToNull(getSerialNumberTextField().getText()); // Don't respect leading or trailing spaces
        String manufacturer = StringUtils.isBlank(getManufacturerTextField().getText()) ? null : getManufacturerTextField().getText();
        String model = StringUtils.isBlank(getModelTextField().getText()) ? null : getModelTextField().getText();
        
        RfnIdentifier rfnIdentifier = new RfnIdentifier(serialNumber, manufacturer, model);
        if (!rfnIdentifier.isBlank() && !rfnIdentifier.isNotBlank())
            throw new Error("Serial Number, Manufacturer, and Model fields must all be empty or all be filled in.");
                
        /* Check for duplicates */
        try {
            RfnDevice existingRfnDevice = rfnDeviceDao.getDeviceForExactIdentifier(rfnIdentifier);
            if (currentDeviceId != existingRfnDevice.getPaoIdentifier().getPaoId()) {
                throw new EditorInputValidationException("Serial Number, Manufacturer, and Model fields must be unique among RFN devices.");
            }
        } catch (NotFoundException e) { /* IGNORE */ };
        
        ChangeDeviceTypeInfo info = new ChangeDeviceTypeInfo(rfnIdentifier);
        return info;
    }

    @Override
    public void setValue(Object o) {
        if (o instanceof RfnMeterBase) {
            RfnMeterBase rfn = (RfnMeterBase)o;
            currentDeviceId = rfn.getPAObjectID();
            getManufacturerTextField().setText(rfn.getRfnAddress().getManufacturer());
            getModelTextField().setText(rfn.getRfnAddress().getModel());
            getSerialNumberTextField().setText(rfn.getRfnAddress().getSerialNumber());
        }
    }

    public JLabel getManufacturerLabel() {
        if(manufacturerLabel == null) {
            manufacturerLabel = new JLabel();
            manufacturerLabel.setFont(textFont);
            manufacturerLabel.setText("Manufacturer:");
        }
        return manufacturerLabel;
    }

    public JTextField getManufacturerTextField() {
        if(manufacturerTextField == null) {
            manufacturerTextField = new JTextField();
            manufacturerTextField.setName("ManufacturerTextField");
            manufacturerTextField.setColumns(80);
        }
        return manufacturerTextField;
    }

    public JTextField getModelTextField() {
        if(modelTextField == null) {
            modelTextField = new JTextField();
            modelTextField.setName("ModelTextField");
            modelTextField.setColumns(80);
        }
        return modelTextField;
    }

    public JLabel getModelLabel() {
        if(modelLabel == null) {
            modelLabel = new JLabel();
            modelLabel.setFont(textFont);
            modelLabel.setText("Model:");
        }
        return modelLabel;
    }

    public JTextField getSerialNumberTextField() {
        if(serialNumberTextField == null) {
            serialNumberTextField = new JTextField();
            serialNumberTextField.setName("SerialNumberTextField");
            serialNumberTextField.setColumns(30);
        }
        return serialNumberTextField;
    }

    public JLabel getSerialNumberLabel() {
        if(serialNumberLabel == null) {
            serialNumberLabel = new JLabel();
            serialNumberLabel.setFont(textFont);
            serialNumberLabel.setText("Serial Number:");
        }
        return serialNumberLabel;
    }
}