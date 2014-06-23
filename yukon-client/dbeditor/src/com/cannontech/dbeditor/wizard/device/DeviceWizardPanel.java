package com.cannontech.dbeditor.wizard.device;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.dbeditor.editor.device.DeviceScanRateEditorPanel;
import com.cannontech.spring.YukonSpringHook;

public class DeviceWizardPanel extends WizardPanel {
    private PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);
    private DeviceNameAddressPanel deviceNameAddressPanel;
    private DevicePhoneNumberPanel devicePhoneNumberPanel;
    private DeviceRoutePanel deviceRoutePanel;
    private DeviceTypePanel deviceTypePanel;
    private DeviceCommChannelPanel deviceCommChannelPanel;
    private DeviceTapTerminalPanel deviceTapTerminalPanel;
    private DeviceTNPPTerminalPanel deviceTNPPTerminalPanel;
    private DeviceTapVerizonPanel deviceTapVerizonPanel;
    private DeviceIEDNamePanel deviceIEDNamePanel;
    private DeviceBaseNamePanel deviceBaseNamePanel;
    private DeviceGridPanel deviceGridPanel;
    private DeviceMeterNumberPanel deviceMeterNumberPanel;
    private DeviceSixnetWizardPanel deviceSixnetWizardPanel;
    private MCTBroadcastListEditorPanel mctBroadcastListEditorPanel;
    private DeviceScanRateEditorPanel deviceScanRateEditorPanel;
    private RfnBasePanel rfnBasePanel;
    private DeviceRDSTerminalPanel rdsTerminalPanel;
    private DeviceTcpTerminalPanel tcpTerminalPanel;

    public DeviceWizardPanel() {
        super();
    }

    @Override
    public java.awt.Dimension getActualSize() {
        setPreferredSize(new java.awt.Dimension(410, 480));
        return getPreferredSize();
    }

    public DeviceRDSTerminalPanel getRDSTerminalPanel() {
        if (rdsTerminalPanel == null) {
            rdsTerminalPanel = new DeviceRDSTerminalPanel();
        }
        return rdsTerminalPanel;
    }

    public DeviceTcpTerminalPanel getTcpTerminalPanel() {
        if (tcpTerminalPanel == null) {
            tcpTerminalPanel = new DeviceTcpTerminalPanel();
        }
        return tcpTerminalPanel;
    }

    protected DeviceIEDNamePanel getDeviceIEDNamePanel() {
        if (deviceIEDNamePanel == null) {
            deviceIEDNamePanel = new DeviceIEDNamePanel();
        }
        return deviceIEDNamePanel;
    }

    public DeviceMeterNumberPanel getDeviceMeterNumberPanel() {
        if (deviceMeterNumberPanel == null) {
            deviceMeterNumberPanel = new DeviceMeterNumberPanel();
        }
        return deviceMeterNumberPanel;
    }

    protected DeviceNameAddressPanel getDeviceNameAddressPanel() {
        if (deviceNameAddressPanel == null) {
            deviceNameAddressPanel = new DeviceNameAddressPanel();
        }
        return deviceNameAddressPanel;
    }

    protected DevicePhoneNumberPanel getDevicePhoneNumberPanel() {
        if (devicePhoneNumberPanel == null) {
            devicePhoneNumberPanel = new DevicePhoneNumberPanel();
        }
        return devicePhoneNumberPanel;
    }

    protected DeviceRoutePanel getDeviceRoutePanel() {
        if (deviceRoutePanel == null) {
            deviceRoutePanel = new DeviceRoutePanel();
        }
        return deviceRoutePanel;
    }

    public DeviceScanRateEditorPanel getDeviceScanRatePanel() {
        if (deviceScanRateEditorPanel == null) {
            deviceScanRateEditorPanel = new DeviceScanRateEditorPanel();
        }
        return deviceScanRateEditorPanel;
    }

    public DeviceSixnetWizardPanel getDeviceSixnetWizardPanel() {
        if (deviceSixnetWizardPanel == null) {
            deviceSixnetWizardPanel = new DeviceSixnetWizardPanel();
        }
        return deviceSixnetWizardPanel;
    }

    protected DeviceTapTerminalPanel getDeviceTapTerminalPanel() {
        if (deviceTapTerminalPanel == null) {
            deviceTapTerminalPanel = new DeviceTapTerminalPanel();
        }
        return deviceTapTerminalPanel;
    }

    protected DeviceTNPPTerminalPanel getDeviceTNPPTerminalPanel() {
        if (deviceTNPPTerminalPanel == null) {
            deviceTNPPTerminalPanel = new DeviceTNPPTerminalPanel();
        }
        return deviceTNPPTerminalPanel;
    }

    protected DeviceTapVerizonPanel getDeviceTapVerizonPanel() {
        if (deviceTapVerizonPanel == null) {
            deviceTapVerizonPanel = new DeviceTapVerizonPanel();
        }
        return deviceTapVerizonPanel;
    }

    public DeviceTypePanel getDeviceTypePanel() {
        if (this.deviceTypePanel == null) {
            this.deviceTypePanel = new DeviceTypePanel();
        }
        return deviceTypePanel;
    }

    protected DeviceBaseNamePanel getDeviceBaseNamePanel() {
        if (deviceBaseNamePanel == null) {
            deviceBaseNamePanel = new DeviceBaseNamePanel();
        }
        return deviceBaseNamePanel;
    }

    protected DeviceGridPanel getDeviceGridPanel() {
        if (deviceGridPanel == null) {
            deviceGridPanel = new DeviceGridPanel();
        }
        return deviceGridPanel;
    }

    protected DeviceCommChannelPanel getDeviceCommChannelPanel() {
        if (deviceCommChannelPanel == null) {
            deviceCommChannelPanel = new DeviceCommChannelPanel();
        }
        return deviceCommChannelPanel;
    }

    protected RfnBasePanel getRfnBasePanel() {
        if (rfnBasePanel == null) {
            rfnBasePanel = new RfnBasePanel();
        }
        return rfnBasePanel;
    }

    @Override
    protected String getHeaderText() {
        return "Device Setup";
    }

    protected MCTBroadcastListEditorPanel getMCTBroadcastListEditorPanel() {
        if (mctBroadcastListEditorPanel == null) {
            mctBroadcastListEditorPanel = new MCTBroadcastListEditorPanel();
        }
        return mctBroadcastListEditorPanel;
    }

    @Override
    public java.awt.Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {
        if (currentInputPanel == null) {
            getDeviceTypePanel().setFirstFocus();
            return getDeviceTypePanel();
        } else if (currentInputPanel == getDeviceTypePanel()) {
            PaoType devType = getDeviceTypePanel().getDeviceType();

            if (devType == PaoType.TAPTERMINAL || devType == PaoType.TNPP_TERMINAL || devType == PaoType.RDS_TERMINAL) {
                getDeviceTapTerminalPanel().setDeviceType(devType);
                getDeviceTapTerminalPanel().setFirstFocus();
                return getDeviceTapTerminalPanel();
            } else if (devType == PaoType.WCTP_TERMINAL) {
                getDeviceTapVerizonPanel().setFirstFocus();
                return getDeviceTapVerizonPanel();
            } else if (devType == PaoType.SNPP_TERMINAL) {
                getDeviceTapVerizonPanel().setFirstFocus();
                getDeviceTapVerizonPanel().setIsSNPP(true);
                return getDeviceTapVerizonPanel();
            } else if (devType == PaoType.VIRTUAL_SYSTEM || devType.isRfn()) {
                getDeviceBaseNamePanel().setDeviceType(devType);
                getDeviceBaseNamePanel().setFirstFocus();
                return getDeviceBaseNamePanel();
            } else if ((devType.isIed() && !devType.isIon()) || devType == PaoType.DAVISWEATHER) {
                getDeviceIEDNamePanel().setDeviceType(devType);
                getDeviceIEDNamePanel().setFirstFocus();
                return getDeviceIEDNamePanel();
            } else if (devType == PaoType.NEUTRAL_MONITOR || devType == PaoType.FAULT_CI) {
                getDeviceGridPanel().setDeviceType(devType);
                getDeviceGridPanel().setFirstFocus();
                return getDeviceGridPanel();
            } else {
                getDeviceNameAddressPanel().setDeviceType(devType);
                getDeviceNameAddressPanel().setFirstFocus();
                return getDeviceNameAddressPanel();
            }
        } else if (currentInputPanel == getDeviceBaseNamePanel()) {
            PaoType devType = getDeviceTypePanel().getDeviceType();
            if (devType.isIed()) {
                getDeviceMeterNumberPanel().setFirstFocus();
                return getDeviceMeterNumberPanel();
            } else {
                getRfnBasePanel().setFirstFocus();
                return getRfnBasePanel();
            }
        } else if ((currentInputPanel == getDeviceNameAddressPanel()) || (currentInputPanel == getDeviceIEDNamePanel())) {
            PaoType devType = getDeviceTypePanel().getDeviceType();

            if (devType.isIed() || devType.isMct()) {
                getDeviceMeterNumberPanel().setValue(null);
                if (DeviceTypesFuncs.isMCT2XXORMCT310XX(devType.getDeviceTypeId())) {
                    // Append "10" to the address for the desired default meter number.
                    getDeviceMeterNumberPanel().setDefaultMeterNumber("10" + getDeviceNameAddressPanel().getAddress());
                }

                if (DeviceTypesFuncs.isMCT4XX(devType.getDeviceTypeId())) {
                    getDeviceMeterNumberPanel().setDefaultMeterNumber(""); // Default the meterNumber to nothing
                }
                getDeviceMeterNumberPanel().setFirstFocus();
                return getDeviceMeterNumberPanel();
            } else if (devType == PaoType.DAVISWEATHER) {
                getDeviceScanRatePanel().setDeviceType(getDeviceTypePanel().getDeviceType());
                getDeviceScanRatePanel().setFirstFocus();
                return getDeviceScanRatePanel();
            } else if (devType == PaoType.MCTBROADCAST) {
                MCTBroadcastListEditorPanel temp = getMCTBroadcastListEditorPanel();
                temp.setValue(null);
                temp.setFirstFocus();
                return temp;
            } else if (devType == PaoType.RDS_TERMINAL) {
                return getRDSTerminalPanel();
            } else {
                getDeviceScanRatePanel().setDeviceType(getDeviceTypePanel().getDeviceType());
                getDeviceScanRatePanel().setFirstFocus();
                return getDeviceScanRatePanel();
            }
        } else if (currentInputPanel == getDeviceMeterNumberPanel()) {
            PaoType devType = getDeviceTypePanel().getDeviceType();
            if (devType.isRfn()) {
                getRfnBasePanel().setFirstFocus();
                return getRfnBasePanel();
            } else {
                getDeviceScanRatePanel().setDeviceType(devType);
                getDeviceScanRatePanel().setFirstFocus();
                return getDeviceScanRatePanel();
            }
        } else if (currentInputPanel == getDeviceScanRatePanel()) {
            PaoType devType = getDeviceTypePanel().getDeviceType();

            if (devType.isPlc() || devType.isRepeater()) {
                getDeviceRoutePanel().setValue(null);
                getDeviceRoutePanel().setFirstFocus();
                return getDeviceRoutePanel();
            } else if (devType == PaoType.SIXNET) {
                getDeviceSixnetWizardPanel().setFirstFocus();
                return getDeviceSixnetWizardPanel();
            } else if (devType.isRtu() || devType.isIon() || devType.isCcu()) {
                getDeviceCommChannelPanel().setValue(null);
                getDeviceCommChannelPanel().setAddress(new Integer(getDeviceNameAddressPanel().getAddress()).intValue());
                getDeviceCommChannelPanel().setDeviceType(getDeviceTypePanel().getDeviceType());
                getDeviceCommChannelPanel().setFirstFocus();
                return getDeviceCommChannelPanel();
            } else if (paoDefinitionDao.isTagSupported(devType, PaoTag.IPC_METER)) {
                getTcpTerminalPanel().setFirstFocus();
                return getTcpTerminalPanel();
            } else
                getDeviceCommChannelPanel().setFirstFocus();
            return getDeviceCommChannelPanel();
        } else if (currentInputPanel == getDeviceSixnetWizardPanel()) {
            getDeviceCommChannelPanel().setValue(null);
            getDeviceCommChannelPanel().setFirstFocus();
            return getDeviceCommChannelPanel();
        } else if (currentInputPanel == getDeviceTapTerminalPanel()) {
            PaoType devType = getDeviceTypePanel().getDeviceType();

            if (devType == PaoType.TAPTERMINAL) {
                getDeviceCommChannelPanel().setValue(null);
                getDeviceCommChannelPanel().setFirstFocus();
                return getDeviceCommChannelPanel();
            }
            if (devType == PaoType.TNPP_TERMINAL) {
                getDeviceTNPPTerminalPanel().setFirstFocus();
                return getDeviceTNPPTerminalPanel();
            }
            if (devType == PaoType.RDS_TERMINAL) {
                getRDSTerminalPanel().setFirstFocus();
                return getRDSTerminalPanel();
            }
            return null;
        } else if (currentInputPanel == getDeviceTapVerizonPanel() || currentInputPanel == getDeviceTNPPTerminalPanel()) {
            getDeviceCommChannelPanel().setValue(null);
            getDeviceCommChannelPanel().setFirstFocus();
            return getDeviceCommChannelPanel();
        } else if (currentInputPanel == getDeviceCommChannelPanel()) {
            // To get to this point the device must be a dialup device
            // If it isn't better go find out why we got here!
            getDeviceCommChannelPanel().setFirstFocus();
            return getDevicePhoneNumberPanel();
        } else if (currentInputPanel == getMCTBroadcastListEditorPanel()) {
            getDeviceRoutePanel().setValue(null);
            getDeviceRoutePanel().setFirstFocus();
            return getDeviceRoutePanel();
        } else if (currentInputPanel == getRDSTerminalPanel()) {
            getDeviceCommChannelPanel().setFirstFocus();
            return getDeviceCommChannelPanel();
        } else
            throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
    }

    @Override
    protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {
        return  ( currentPanel == getDevicePhoneNumberPanel() 
                || (currentPanel == getDeviceCommChannelPanel() && !getDeviceCommChannelPanel().isDialupPort())
                || currentPanel == getDeviceRoutePanel() 
                || (currentPanel == getDeviceBaseNamePanel() && getDeviceTypePanel().getDeviceType() == PaoType.VIRTUAL_SYSTEM)
                || currentPanel == getDeviceGridPanel()
                || currentPanel == getRfnBasePanel()
                || currentPanel == getTcpTerminalPanel()
                || (currentPanel == getDeviceCommChannelPanel() 
                    && 
                    (getDeviceTypePanel().getDeviceType() == PaoType.ION_7700
                     || getDeviceTypePanel().getDeviceType() == PaoType.ION_7330
                     || getDeviceTypePanel().getDeviceType() == PaoType.ION_8300) ) );
    }
}