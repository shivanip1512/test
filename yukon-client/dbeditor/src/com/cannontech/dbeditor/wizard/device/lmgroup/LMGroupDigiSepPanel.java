package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.database.data.device.lm.LMGroupDigiSep;
import com.cannontech.database.data.device.lm.SepDeviceClass;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;

public class LMGroupDigiSepPanel extends DataInputPanel implements ActionListener, CaretListener {

    private static final long serialVersionUID = -2252114675970615944L;
    private JPanel deviceClassPanel = null;
    private JCheckBox hvacCheckBox = null;
    private JCheckBox baseboardCheckBox = null;
    private JCheckBox waterHeaterCheckBox = null;
    private JCheckBox poolPumpCheckBox = null;
    private JCheckBox smartApplianceCheckBox = null;
    private JCheckBox irrigationPumpCheckBox = null;
    private JCheckBox commercialCheckBox = null;
    private JCheckBox simpleResidentialCheckBox = null;
    private JCheckBox exteriorLightCheckBox = null;
    private JCheckBox interiorLightCheckBox = null;
    private JCheckBox electricVehicleCheckBox = null;
    private JCheckBox generationCheckBox = null;
    private JTextField enrollmentGroupTextField = null;
    private JTextField rampInTextField = null;
    private JTextField rampOutTextField = null;
    private JLabel enrollmentGroupLabel = null;
    private JPanel enrollmentPanel = null;
    private JPanel rampingPanel = null;
    private JLabel rampInLabel = null;
    private JLabel rampOutLabel = null;

    public LMGroupDigiSepPanel() {
        super();
        initialize();
    }

    private void initialize() {
        getBaseboardCheckBox().addActionListener(this);
        getHvacCheckBox().addActionListener(this);
        getWaterHeaterCheckBox().addActionListener(this);
        getPoolPumpCheckBox().addActionListener(this);
        getSmartApplianceCheckBox().addActionListener(this);
        getIrrigationPumpCheckBox().addActionListener(this);
        getCommercialCheckBox().addActionListener(this);
        getInteriorLightCheckBox().addActionListener(this);
        getExteriorLightCheckBox().addActionListener(this);
        getSimpleResidentialCheckBox().addActionListener(this);
        getElectricVehicleCheckBox().addActionListener(this);
        getGenerationCheckBox().addActionListener(this);
        getUtilityEnerollmentGroupTextField().addCaretListener(this);
        getRampInTextField().addCaretListener(this);
        getRampOutTextField().addCaretListener(this);

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
        add(getEnrollmentPanel(), constraintsJPanelEnrollmentPanel);
        
        GridBagConstraints constraintsJPanelRampingPanel = new GridBagConstraints();
        constraintsJPanelRampingPanel.gridx = 0;
        constraintsJPanelRampingPanel.gridy = 2;
        constraintsJPanelRampingPanel.fill = GridBagConstraints.BOTH;
        constraintsJPanelRampingPanel.ipady = 5;
        constraintsJPanelRampingPanel.insets = new Insets(0, 2, 2, 0);
        add(getRampingPanel(), constraintsJPanelRampingPanel);

    }

    private JPanel getEnrollmentPanel() {
        if (enrollmentPanel == null) {
            enrollmentPanel = new JPanel();
            enrollmentPanel.setLayout(new GridBagLayout());

            TitleBorder border;
            border = new TitleBorder();
            border.setTitleFont(new java.awt.Font("Arial", 1, 14));
            border.setTitle("Enrollment");
            enrollmentPanel.setBorder(border);

            GridBagConstraints constraintsEnrollmentLabel = new GridBagConstraints();
            constraintsEnrollmentLabel.gridx = 0;
            constraintsEnrollmentLabel.gridy = 0;
            constraintsEnrollmentLabel.insets = new Insets(0, 2, 2, 0);
            constraintsEnrollmentLabel.anchor = GridBagConstraints.WEST;
            enrollmentPanel.add(getUtilityEnerollmentGroupLabel(), constraintsEnrollmentLabel);

            GridBagConstraints constraintsEnrollmentTextField = new GridBagConstraints();
            constraintsEnrollmentTextField.gridx = 1;
            constraintsEnrollmentTextField.gridy = 0;
            constraintsEnrollmentTextField.insets = new Insets(0, 2, 2, 0);
            constraintsEnrollmentTextField.anchor = GridBagConstraints.NORTHWEST;
            constraintsEnrollmentTextField.weightx = 1.0d;
            enrollmentPanel.add(getUtilityEnerollmentGroupTextField(), constraintsEnrollmentTextField);
        }
        return enrollmentPanel;
    }
    
    private JPanel getRampingPanel() {
        if (rampingPanel == null) {
            rampingPanel = new JPanel();
            rampingPanel.setLayout(new GridBagLayout());
            
            TitleBorder border;
            border = new TitleBorder();
            border.setTitleFont(new java.awt.Font("Arial", 1, 14));
            border.setTitle("Timing");
            rampingPanel.setBorder(border);
            
            GridBagConstraints constraintsRampInLabel = new GridBagConstraints();
            constraintsRampInLabel.gridx = 0;
            constraintsRampInLabel.gridy = 0;
            constraintsRampInLabel.insets = new Insets(0, 2, 2, 0);
            constraintsRampInLabel.anchor = GridBagConstraints.WEST;
            rampingPanel.add(getRampInLabel(), constraintsRampInLabel);
            
            GridBagConstraints constraintsRampInTextField = new GridBagConstraints();
            constraintsRampInTextField.gridx = 1;
            constraintsRampInTextField.gridy = 0;
            constraintsRampInTextField.insets = new Insets(0, 2, 2, 0);
            constraintsRampInTextField.anchor = GridBagConstraints.NORTHWEST;
            constraintsRampInTextField.weightx = 1.0d;
            rampingPanel.add(getRampInTextField(), constraintsRampInTextField);
            
            GridBagConstraints constraintsRampOutLabel = new GridBagConstraints();
            constraintsRampOutLabel.gridx = 0;
            constraintsRampOutLabel.gridy = 1;
            constraintsRampOutLabel.insets = new Insets(0, 2, 2, 0);
            constraintsRampOutLabel.anchor = GridBagConstraints.WEST;
            rampingPanel.add(getRampOutLabel(), constraintsRampOutLabel);
            
            GridBagConstraints constraintsRampOutTextField = new GridBagConstraints();
            constraintsRampOutTextField.gridx = 1;
            constraintsRampOutTextField.gridy = 1;
            constraintsRampOutTextField.insets = new Insets(0, 2, 2, 0);
            constraintsRampOutTextField.anchor = GridBagConstraints.NORTHWEST;
            constraintsRampOutTextField.weightx = 1.0d;
            rampingPanel.add(getRampOutTextField(), constraintsRampOutTextField);
            
            getRampInTextField().setText("30");
            getRampOutTextField().setText("30");
        }
        
        return rampingPanel;
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

            GridBagConstraints constraintsCheckboxHVAC = new GridBagConstraints();
            constraintsCheckboxHVAC.gridx = 0;
            constraintsCheckboxHVAC.gridy = 0;
            constraintsCheckboxHVAC.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxHVAC.anchor = GridBagConstraints.NORTHWEST;
            deviceClassPanel.add(getHvacCheckBox(), constraintsCheckboxHVAC);

            GridBagConstraints constraintsCheckboxBaseboard = new GridBagConstraints();
            constraintsCheckboxBaseboard.gridx = 1;
            constraintsCheckboxBaseboard.gridy = 0;
            constraintsCheckboxBaseboard.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxBaseboard.anchor = GridBagConstraints.NORTHWEST;
            deviceClassPanel.add(getBaseboardCheckBox(), constraintsCheckboxBaseboard);

            GridBagConstraints constraintsCheckboxWaterHeater = new GridBagConstraints();
            constraintsCheckboxWaterHeater.gridx = 0;
            constraintsCheckboxWaterHeater.gridy = 1;
            constraintsCheckboxWaterHeater.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxWaterHeater.anchor = GridBagConstraints.NORTHWEST;
            deviceClassPanel.add(getWaterHeaterCheckBox(), constraintsCheckboxWaterHeater);

            GridBagConstraints constraintsCheckboxPoolPump = new GridBagConstraints();
            constraintsCheckboxPoolPump.gridx = 1;
            constraintsCheckboxPoolPump.gridy = 1;
            constraintsCheckboxPoolPump.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxPoolPump.anchor = GridBagConstraints.NORTHWEST;
            deviceClassPanel.add(getPoolPumpCheckBox(), constraintsCheckboxPoolPump);

            GridBagConstraints constraintsCheckboxSmartAppliance = new GridBagConstraints();
            constraintsCheckboxSmartAppliance.gridx = 0;
            constraintsCheckboxSmartAppliance.gridy = 2;
            constraintsCheckboxSmartAppliance.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxSmartAppliance.anchor = GridBagConstraints.NORTHWEST;
            deviceClassPanel.add(getSmartApplianceCheckBox(), constraintsCheckboxSmartAppliance);

            GridBagConstraints constraintsCheckboxIrrigationPump = new GridBagConstraints();
            constraintsCheckboxIrrigationPump.gridx = 1;
            constraintsCheckboxIrrigationPump.gridy = 2;
            constraintsCheckboxIrrigationPump.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxIrrigationPump.anchor = GridBagConstraints.NORTHWEST;
            deviceClassPanel.add(getIrrigationPumpCheckBox(), constraintsCheckboxIrrigationPump);

            GridBagConstraints constraintsCheckboxCommercial = new GridBagConstraints();
            constraintsCheckboxCommercial.gridx = 0;
            constraintsCheckboxCommercial.gridy = 3;
            constraintsCheckboxCommercial.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxCommercial.anchor = GridBagConstraints.NORTHWEST;
            constraintsCheckboxCommercial.weightx = 1.0d;
            constraintsCheckboxCommercial.fill = GridBagConstraints.BOTH;
            deviceClassPanel.add(getCommercialCheckBox(), constraintsCheckboxCommercial);

            GridBagConstraints constraintsCheckboxSimpleResidential = new GridBagConstraints();
            constraintsCheckboxSimpleResidential.gridx = 1;
            constraintsCheckboxSimpleResidential.gridy = 3;
            constraintsCheckboxSimpleResidential.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxSimpleResidential.anchor = GridBagConstraints.NORTHWEST;
            deviceClassPanel.add(getSimpleResidentialCheckBox(), constraintsCheckboxSimpleResidential);

            GridBagConstraints constraintsCheckboxExteriorLight = new GridBagConstraints();
            constraintsCheckboxExteriorLight.gridx = 0;
            constraintsCheckboxExteriorLight.gridy = 4;
            constraintsCheckboxExteriorLight.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxExteriorLight.anchor = GridBagConstraints.NORTHWEST;
            deviceClassPanel.add(getExteriorLightCheckBox(), constraintsCheckboxExteriorLight);

            GridBagConstraints constraintsCheckboxInteriorLight = new GridBagConstraints();
            constraintsCheckboxInteriorLight.gridx = 1;
            constraintsCheckboxInteriorLight.gridy = 4;
            constraintsCheckboxInteriorLight.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxInteriorLight.anchor = GridBagConstraints.NORTHWEST;
            deviceClassPanel.add(getInteriorLightCheckBox(), constraintsCheckboxInteriorLight);

            GridBagConstraints constraintsCheckboxElectricVehicle = new GridBagConstraints();
            constraintsCheckboxElectricVehicle.gridx = 0;
            constraintsCheckboxElectricVehicle.gridy = 5;
            constraintsCheckboxElectricVehicle.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxElectricVehicle.anchor = GridBagConstraints.NORTHWEST;
            deviceClassPanel.add(getElectricVehicleCheckBox(), constraintsCheckboxElectricVehicle);

            GridBagConstraints constraintsCheckboxGeneration = new GridBagConstraints();
            constraintsCheckboxGeneration.gridx = 1;
            constraintsCheckboxGeneration.gridy = 5;
            constraintsCheckboxGeneration.insets = new Insets(0, 2, 2, 0);
            constraintsCheckboxGeneration.anchor = GridBagConstraints.NORTHWEST;
            deviceClassPanel.add(getGenerationCheckBox(), constraintsCheckboxGeneration);
        }
        return deviceClassPanel;
    }
    
    @Override
    public boolean isInputValid() {
        int enrollmentGroup;
        try {
            enrollmentGroup = Integer.parseInt(getUtilityEnerollmentGroupTextField().getText());
        } catch (NumberFormatException n) {
            setErrorString("Utility Enrollment Group must be between 1 and 255");
            return false;
        }
        
        if (enrollmentGroup < 1 || enrollmentGroup > 255) {
            String specialPrefix = "";
            if (enrollmentGroup == 0) {
                specialPrefix = "A value of 0 means all devices in the group...";
            }
            setErrorString(specialPrefix + "Utility Enrollment Group must be between 1 and 255.");
            return false;
        }

        // One of these must be selected.
        if (!(getBaseboardCheckBox().isSelected() || getHvacCheckBox().isSelected()
              || getWaterHeaterCheckBox().isSelected() || getPoolPumpCheckBox().isSelected()
              || getSmartApplianceCheckBox().isSelected() || getIrrigationPumpCheckBox().isSelected()
              || getCommercialCheckBox().isSelected() || getInteriorLightCheckBox().isSelected()
              || getExteriorLightCheckBox().isSelected() || getSimpleResidentialCheckBox().isSelected()
              || getElectricVehicleCheckBox().isSelected() || getGenerationCheckBox().isSelected())) {
            setErrorString("At least one device class must be selected");
            return false;
        }
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
        if (value instanceof LMGroupDigiSep) {
            LMGroupDigiSep group = (LMGroupDigiSep) value;

            if (getBaseboardCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.BASEBOARD_HEAT);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.BASEBOARD_HEAT);
            if (getHvacCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.HVAC_COMPRESSOR_FURNACE);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.HVAC_COMPRESSOR_FURNACE);
            if (getWaterHeaterCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.WATER_HEATER);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.WATER_HEATER);
            if (getPoolPumpCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.POOL_PUMP);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.POOL_PUMP);
            if (getSmartApplianceCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.SMART_APPLIANCE);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.SMART_APPLIANCE);
            if (getIrrigationPumpCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.IRRIGATION_PUMP);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.IRRIGATION_PUMP);
            if (getCommercialCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.MANAGED_COMMERCIAL_INDUSTRIAL);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.MANAGED_COMMERCIAL_INDUSTRIAL);
            if (getInteriorLightCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.INTERIOR_LIGHTING);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.INTERIOR_LIGHTING);
            if (getExteriorLightCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.EXTERIOR_LIGHTING);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.EXTERIOR_LIGHTING);
            if (getSimpleResidentialCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.SIMPLE_RESIDENTIAL_ON_OFF);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.SIMPLE_RESIDENTIAL_ON_OFF);
            if (getElectricVehicleCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.ELECTRIC_VEHICLE);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.ELECTRIC_VEHICLE);
            if (getGenerationCheckBox().isSelected())
                group.getLmGroupSep().addDeviceClass(SepDeviceClass.GENERATION_SYSTEMS);
            else
                group.getLmGroupSep().removeDeviceClass(SepDeviceClass.GENERATION_SYSTEMS);

            try {
                Integer enrollment = Integer.parseInt(getUtilityEnerollmentGroupTextField().getText());
                group.getLmGroupSep().setUtilityEnrollmentGroup(enrollment);
            } catch (NumberFormatException n) {
                // This should never occur, if it does log it and do not set 0
                // as that means "all" groups.
                CTILogger.error(n.getMessage(), n);
            }
            
            group.getLmGroupSep().setRampInMinutes(Integer.parseInt(getRampInTextField().getText()));
            group.getLmGroupSep().setRampOutMinutes(Integer.parseInt(getRampOutTextField().getText()));
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
        if (value instanceof LMGroupDigiSep) {
            LMGroupDigiSep group = (LMGroupDigiSep) value;

            getBaseboardCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.BASEBOARD_HEAT));
            getHvacCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.HVAC_COMPRESSOR_FURNACE));
            getWaterHeaterCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.WATER_HEATER));
            getPoolPumpCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.POOL_PUMP));
            getSmartApplianceCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.SMART_APPLIANCE));
            getIrrigationPumpCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.IRRIGATION_PUMP));
            getCommercialCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.MANAGED_COMMERCIAL_INDUSTRIAL));
            getSimpleResidentialCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.SIMPLE_RESIDENTIAL_ON_OFF));
            getExteriorLightCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.EXTERIOR_LIGHTING));
            getInteriorLightCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.INTERIOR_LIGHTING));
            getElectricVehicleCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.ELECTRIC_VEHICLE));
            getGenerationCheckBox().setSelected(group.getLmGroupSep().hasDeviceClass(SepDeviceClass.GENERATION_SYSTEMS));

            getUtilityEnerollmentGroupTextField().setText(group.getLmGroupSep().getUtilityEnrollmentGroup().toString());
            
            getRampInTextField().setText(group.getLmGroupSep().getRampInMinutes().toString());
            getRampOutTextField().setText(group.getLmGroupSep().getRampOutMinutes().toString());
        }
    }

    private JCheckBox getHvacCheckBox() {
        if (hvacCheckBox == null) {
            hvacCheckBox = new javax.swing.JCheckBox();
            hvacCheckBox.setName("HVACCheckBox");
            hvacCheckBox.setText("HVAC Compressor or Furnace");
            hvacCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return hvacCheckBox;
    }

    private JCheckBox getBaseboardCheckBox() {
        if (baseboardCheckBox == null) {
            baseboardCheckBox = new javax.swing.JCheckBox();
            baseboardCheckBox.setName("BBCheckBox");
            baseboardCheckBox.setText("Strip Heaters/Baseboard Heaters");
            baseboardCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return baseboardCheckBox;
    }

    private JCheckBox getWaterHeaterCheckBox() {
        if (waterHeaterCheckBox == null) {
            waterHeaterCheckBox = new javax.swing.JCheckBox();
            waterHeaterCheckBox.setName("WaterHeaterCheckBox");
            waterHeaterCheckBox.setText("Water Heater");
            waterHeaterCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return waterHeaterCheckBox;
    }

    private JCheckBox getPoolPumpCheckBox() {
        if (poolPumpCheckBox == null) {
            poolPumpCheckBox = new javax.swing.JCheckBox();
            poolPumpCheckBox.setName("PoolPumpCheckBox");
            poolPumpCheckBox.setText("Pool Pump/Spa/Jacuzzi");
            poolPumpCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return poolPumpCheckBox;
    }

    private JCheckBox getSmartApplianceCheckBox() {
        if (smartApplianceCheckBox == null) {
            smartApplianceCheckBox = new javax.swing.JCheckBox();
            smartApplianceCheckBox.setName("ApplianceCheckBox");
            smartApplianceCheckBox.setText("Smart Appliances");
            smartApplianceCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return smartApplianceCheckBox;
    }

    private JCheckBox getIrrigationPumpCheckBox() {
        if (irrigationPumpCheckBox == null) {
            irrigationPumpCheckBox = new javax.swing.JCheckBox();
            irrigationPumpCheckBox.setName("IrrigationCheckBox");
            irrigationPumpCheckBox.setText("Irrigation Pump");
            irrigationPumpCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return irrigationPumpCheckBox;
    }

    private JCheckBox getCommercialCheckBox() {
        if (commercialCheckBox == null) {
            commercialCheckBox = new javax.swing.JCheckBox();
            commercialCheckBox.setName("CommercialCheckBox");
            commercialCheckBox.setText("Managed Commercial & Industrial Loads");
            commercialCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return commercialCheckBox;
    }

    private JCheckBox getSimpleResidentialCheckBox() {
        if (simpleResidentialCheckBox == null) {
            simpleResidentialCheckBox = new javax.swing.JCheckBox();
            simpleResidentialCheckBox.setName("SimpleResidentialCheckBox");
            simpleResidentialCheckBox.setText("Simple Misc, Residential On/Off Loads");
            simpleResidentialCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return simpleResidentialCheckBox;
    }

    private JCheckBox getExteriorLightCheckBox() {
        if (exteriorLightCheckBox == null) {
            exteriorLightCheckBox = new javax.swing.JCheckBox();
            exteriorLightCheckBox.setName("ExteriorLightCheckBox");
            exteriorLightCheckBox.setText("Exterior Lighting");
            exteriorLightCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return exteriorLightCheckBox;
    }

    private JCheckBox getInteriorLightCheckBox() {
        if (interiorLightCheckBox == null) {
            interiorLightCheckBox = new javax.swing.JCheckBox();
            interiorLightCheckBox.setName("InteriorLightCheckBox");
            interiorLightCheckBox.setText("Interior Lighting");
            interiorLightCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return interiorLightCheckBox;
    }

    private JCheckBox getElectricVehicleCheckBox() {
        if (electricVehicleCheckBox == null) {
            electricVehicleCheckBox = new javax.swing.JCheckBox();
            electricVehicleCheckBox.setName("ElectricVehicleCheckBox");
            electricVehicleCheckBox.setText("Electric Vehicle");
            electricVehicleCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return electricVehicleCheckBox;
    }

    private JCheckBox getGenerationCheckBox() {
        if (generationCheckBox == null) {
            generationCheckBox = new javax.swing.JCheckBox();
            generationCheckBox.setName("GenerationCheckBox");
            generationCheckBox.setText("Generation Systems");
            generationCheckBox.setFont(new java.awt.Font("dialog", 0, 10));
        }
        return generationCheckBox;
    }

    private JTextField getUtilityEnerollmentGroupTextField() {
        if (enrollmentGroupTextField == null) {
            enrollmentGroupTextField = new JTextField();
            enrollmentGroupTextField.setColumns(3);
            enrollmentGroupTextField.setPreferredSize(new Dimension(60, 20));
            enrollmentGroupTextField.setMaximumSize(new Dimension(60, 20));
        }
        return enrollmentGroupTextField;
    }
    
    private JTextField getRampInTextField() {
        if (rampInTextField == null) {
            rampInTextField = new JTextField();
            rampInTextField.setColumns(3);
            rampInTextField.setPreferredSize(new Dimension(60, 20));
            rampInTextField.setMaximumSize(new Dimension(60, 20));
        }
        return rampInTextField;
    }
    
    private JTextField getRampOutTextField() {
        if (rampOutTextField == null) {
            rampOutTextField = new JTextField();
            rampOutTextField.setColumns(3);
            rampOutTextField.setPreferredSize(new Dimension(60, 20));
            rampOutTextField.setMaximumSize(new Dimension(60, 20));
        }
        return rampOutTextField;
    }

    private JLabel getUtilityEnerollmentGroupLabel() {
        if (enrollmentGroupLabel == null) {
            enrollmentGroupLabel = new JLabel("Utility Enrollment Group:");
        }
        return enrollmentGroupLabel;
    }
    
    private JLabel getRampInLabel() {
        if (rampInLabel == null) {
            rampInLabel = new JLabel("Ramp In Time (min): ");
        }
        return rampInLabel;
    }
    
    private JLabel getRampOutLabel() {
        if (rampOutLabel == null) {
            rampOutLabel = new JLabel("Ramp Out Time (min): ");
        }
        return rampOutLabel;
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