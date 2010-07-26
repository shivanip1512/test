package com.cannontech.dbeditor.wizard.device;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.CrfBase;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.spring.YukonSpringHook;

public class CrfMeterPanel extends DataInputPanel implements CaretListener {
    private JLabel manufacturerLabel;
    private JLabel modelLabel;
    private JLabel serialNumberLabel;
    private JTextField manufacturerTextField;
    private JTextField modelTextField;
    private JTextField serialNumberTextField;
    private Font textFont = new Font("dialog", 0, 14);
    private JCheckBox createPointsCheckBox;

    public CrfMeterPanel() {
        super();
        initialize();
    }
    
    private void initialize() {
        setLayout(new GridBagLayout());
        setSize(351, 264);
        setPreferredSize(new Dimension(350, 260));
        setBorder(new TitledBorder("CRF Meter Settings"));

        GridBagConstraints manufacturerLabelConstraint = new GridBagConstraints();
        manufacturerLabelConstraint.gridx = 0; manufacturerLabelConstraint.gridy = 0;
        manufacturerLabelConstraint.anchor = GridBagConstraints.WEST;
        manufacturerLabelConstraint.insets = new Insets(3, 3, 3, 3);
        add(getManufacturerLabel(), manufacturerLabelConstraint);
        
        GridBagConstraints manufacturerTextFieldConstraint = new GridBagConstraints();
        manufacturerTextFieldConstraint.gridx = 1; manufacturerTextFieldConstraint.gridy = 0;
        manufacturerTextFieldConstraint.weightx = 1.0;
        manufacturerTextFieldConstraint.fill = GridBagConstraints.BOTH;
        manufacturerTextFieldConstraint.anchor = GridBagConstraints.WEST;
        manufacturerTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
        add(getManufacturerTextField(), manufacturerTextFieldConstraint);
        
        GridBagConstraints modelLabelConstraint = new GridBagConstraints();
        modelLabelConstraint.gridx = 0; modelLabelConstraint.gridy = 1;
        modelLabelConstraint.anchor = GridBagConstraints.WEST;
        modelLabelConstraint.insets = new Insets(3, 3, 3, 3);
        add(getModelLabel(), modelLabelConstraint);
        
        GridBagConstraints modelTextFieldConstraint = new GridBagConstraints();
        modelTextFieldConstraint.gridx = 1; modelTextFieldConstraint.gridy = 1;
        modelTextFieldConstraint.fill = GridBagConstraints.BOTH;
        modelTextFieldConstraint.anchor = GridBagConstraints.WEST;
        modelTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
        add(getModelTextField(), modelTextFieldConstraint);
        
        GridBagConstraints serialNumberLabelConstraint = new GridBagConstraints();
        serialNumberLabelConstraint.gridx = 0; serialNumberLabelConstraint.gridy = 2;
        serialNumberLabelConstraint.anchor = GridBagConstraints.WEST;
        serialNumberLabelConstraint.insets = new Insets(3, 3, 3, 3);
        add(getSerialNumberLabel(), serialNumberLabelConstraint);
        
        GridBagConstraints serialNumberTextFieldConstraint = new GridBagConstraints();
        serialNumberTextFieldConstraint.gridx = 1; serialNumberTextFieldConstraint.gridy = 2;
        serialNumberTextFieldConstraint.fill = GridBagConstraints.BOTH;
        serialNumberTextFieldConstraint.anchor = GridBagConstraints.WEST;
        serialNumberTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
        add(getSerialNumberTextField(), serialNumberTextFieldConstraint);
        
        GridBagConstraints createPointsCheckBoxConstraint = new GridBagConstraints();
        createPointsCheckBoxConstraint.gridx = 0; createPointsCheckBoxConstraint.gridy = 3;
        createPointsCheckBoxConstraint.gridwidth = 2;
        createPointsCheckBoxConstraint.anchor = GridBagConstraints.WEST;
        createPointsCheckBoxConstraint.insets = new Insets(3, 3, 3, 3);
        add(getCreatePointsCheckBox(), createPointsCheckBoxConstraint);
        
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
            public void run() {
                getManufacturerTextField().requestFocus();
            }
        });
    }
    
    @Override
    public boolean isInputValid() {
        String serialNumber = getSerialNumberTextField().getText();
        String manufacturer = getManufacturerTextField().getText();
        String model = getModelTextField().getText();
        
        /* Valid: 
         *     1. All fields are blank.
         *     2. All fields are filled in.
         * Invalid: 
         *     1. One or two of the three fields are blank.
         */
        if(StringUtils.isBlank(serialNumber) && StringUtils.isBlank(manufacturer) && StringUtils.isBlank(model)) {
            return true;
        } else if(StringUtils.isNotBlank(serialNumber) && StringUtils.isNotBlank(manufacturer) && StringUtils.isNotBlank(model)) {
            return true;
        } else {
            setErrorString("Serial Number, Manufacturer, and Model fields must all be empty or all be filled in.");
            return false;
        }
    }

    public void caretUpdate(CaretEvent e) {
        fireInputUpdate();
    }

    @Override
    public Object getValue(Object o) throws EditorInputValidationException {
        CrfBase crfMeter = (CrfBase)o;
        
        crfMeter.getCrfAddress().setManufacturer(StringUtils.isBlank(getManufacturerTextField().getText()) ? null : getManufacturerTextField().getText());
        crfMeter.getCrfAddress().setModel(StringUtils.isBlank(getModelTextField().getText()) ? null : getModelTextField().getText());
        crfMeter.getCrfAddress().setSerialNumber(StringUtils.isBlank(getSerialNumberTextField().getText()) ? null : getSerialNumberTextField().getText());
        
        if (createPointsCheckBox.isSelected()) {
            PaoDao paoDao = (PaoDao) YukonSpringHook.getBean("paoDao");
            crfMeter.setDeviceID(paoDao.getNextPaoId());

            PaoDefinitionService paoDefinitionService = (PaoDefinitionService) YukonSpringHook.getBean("paoDefinitionService");
            DeviceDao deviceDao = (DeviceDao) YukonSpringHook.getBean("deviceDao");
            SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(crfMeter);
            List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(yukonDevice);

            SmartMultiDBPersistent persistant = new SmartMultiDBPersistent();
            persistant.addOwnerDBPersistent(crfMeter);
            for (PointBase point : defaultPoints) {
                persistant.addDBPersistent(point);
            }
            return persistant;
        }

        return crfMeter;
    }

    @Override
    public void setValue(Object o) {/* Nothing to setup when showing this panel. */}

    public JCheckBox getCreatePointsCheckBox() {
        if(createPointsCheckBox == null) {
            createPointsCheckBox = new JCheckBox("Create default points", true);
            createPointsCheckBox.setFont(textFont);
        }
        return createPointsCheckBox;
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
            manufacturerTextField.setColumns(80);
        }
        return manufacturerTextField;
    }

    public JTextField getModelTextField() {
        if(modelTextField == null) {
            modelTextField = new JTextField();
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