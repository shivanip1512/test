package com.cannontech.dbeditor.editor.device;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.JTextFieldTimeEntry;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.capcontrol.CapBankController6510;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.device.CCU721;
import com.cannontech.database.data.device.CCUBase;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IEDMeter;
import com.cannontech.database.data.device.IonBase;
import com.cannontech.database.data.device.LCUBase;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.RTCBase;
import com.cannontech.database.data.device.RTM;
import com.cannontech.database.data.device.RTUBase;
import com.cannontech.database.data.device.RepeaterBase;
import com.cannontech.database.data.device.Series5Base;
import com.cannontech.database.data.device.TCUBase;
import com.cannontech.database.data.device.TapTerminalBase;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.device.TwoWayLCR;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.device.DeviceWindow;

public class DeviceScanRateEditorPanel extends DataInputPanel implements ActionListener, CaretListener {
    private JCheckBox ivjAccumulatorRateCheckBox = null;
    private JCheckBox ivjIntegrityRateCheckBox = null;
    private JComboBox<String> ivjAccumulatorRateComboBox = null;
    private JComboBox<String> ivjIntegrityRateComboBox = null;
    private JComboBox<String> ivjAccumulatorGroupComboBox = null;
    private JComboBox<String> ivjIntegrityGroupComboBox = null;
    private JCheckBox ivjPeriodicHealthCheckBox = null;
    private JComboBox<String> ivjPeriodicHealthGroupComboBox = null;
    private JComboBox<String> ivjPeriodicHealthIntervalComboBox = null;
    private JComboBox<String> ivjJComboBoxAltAccRate = null;
    private JComboBox<String> ivjJComboBoxAltHealthChk = null;
    private JComboBox<String> ivjJComboBoxAltIntegrityRate = null;
    private JLabel ivjJLabelAltIntervalAcc = null;
    private JLabel ivjJLabelAltIntervalHlth = null;
    private JLabel ivjJLabelAltIntervalInt = null;
    private JLabel ivjJLabelGroupAcc = null;
    private JLabel ivjJLabelGroupHlth = null;
    private JLabel ivjJLabelGroupInt = null;
    private JLabel ivjJLabelIntervalAcc = null;
    private JLabel ivjJLabelIntervalHlth = null;
    private JLabel ivjJLabelIntervalInt = null;
    private JCheckBox ivjJCheckBoxScanWindow = null;
    private JComboBox<String> ivjJComboBoxScanWindowType = null;
    private JLabel ivjJLabelOpen = null;
    private JLabel ivjJLabelType = null;
    private JPanel ivjJPanelScanWindow = null;
    private JTextFieldTimeEntry ivjJTextFieldOpen = null;
    private JPanel ivjJPanelScanConfig = null;
    private JLabel ivjJLabelClose = null;
    private JLabel ivjJLabelhhmm = null;
    private JLabel ivjJLabelhhmm1 = null;
    private JTextFieldTimeEntry ivjJTextFieldClose = null;

    private static final String[] ALT_SELECTIONS = { "1 second", "2 second", "5 second", "10 second", "15 second",
            "30 second", "1 minute", "2 minute", "3 minute", "5 minute", "10 minute", "15 minute", "30 minute",
            "1 hour", "2 hour", "6 hour", "12 hour", "Daily" };

    public DeviceScanRateEditorPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == getPeriodicHealthCheckBox()) {
                this.fireInputUpdate();
                getPeriodicHealthIntervalComboBox().setEnabled(getPeriodicHealthCheckBox().isSelected());
                getPeriodicHealthGroupComboBox().setEnabled(getPeriodicHealthCheckBox().isSelected());
                getJComboBoxAltHealthChk().setEnabled(getPeriodicHealthCheckBox().isSelected());
            }
            if (e.getSource() == getAccumulatorRateCheckBox()) {
                this.fireInputUpdate();
                getAccumulatorRateComboBox().setEnabled(getAccumulatorRateCheckBox().isSelected());
                getAccumulatorGroupComboBox().setEnabled(getAccumulatorRateCheckBox().isSelected());
                getJComboBoxAltAccRate().setEnabled(getAccumulatorRateCheckBox().isSelected());
            }
            if (e.getSource() == getIntegrityRateCheckBox()) {
                this.fireInputUpdate();
                getIntegrityRateComboBox().setEnabled(getIntegrityRateCheckBox().isSelected());
                getIntegrityGroupComboBox().setEnabled(getIntegrityRateCheckBox().isSelected());
                getJComboBoxAltIntegrityRate().setEnabled(getIntegrityRateCheckBox().isSelected());
            }
            if (e.getSource() == getAccumulatorRateComboBox()) {
                this.fireInputUpdate();
                updateAltJComboBox(getAccumulatorRateComboBox(), getJComboBoxAltAccRate());
            }
            if (e.getSource() == getPeriodicHealthIntervalComboBox()) {
                this.fireInputUpdate();
                updateAltJComboBox(getPeriodicHealthIntervalComboBox(), getJComboBoxAltHealthChk());
            }
            if (e.getSource() == getIntegrityRateComboBox()) {
                this.fireInputUpdate();
                updateAltJComboBox(getIntegrityRateComboBox(), getJComboBoxAltIntegrityRate());
            }
            if (e.getSource() == getJComboBoxAltHealthChk() ||
                    e.getSource() == getJComboBoxAltAccRate() ||
                    e.getSource() == getJComboBoxAltIntegrityRate() ||
                    e.getSource() == getJComboBoxScanWindowType() ||
                    e.getSource() == getIntegrityGroupComboBox() ||
                    e.getSource() == getAccumulatorGroupComboBox() ||
                    e.getSource() == getPeriodicHealthGroupComboBox()) {
                this.fireInputUpdate();
            }
            if (e.getSource() == getJCheckBoxScanWindow()) {
                this.jCheckBoxScanWindow_ActionPerformed(e);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void caretUpdate(javax.swing.event.CaretEvent e) {
        if (e.getSource() == getJTextFieldOpen() ||
                e.getSource() == getJTextFieldClose()) {
            try {
                this.fireInputUpdate();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
    }

    private JComboBox<String> getAccumulatorGroupComboBox() {
        if (ivjAccumulatorGroupComboBox == null) {
            try {
                ivjAccumulatorGroupComboBox = new JComboBox<String>();
                ivjAccumulatorGroupComboBox.setName("AccumulatorGroupComboBox");
                ivjAccumulatorGroupComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
                ivjAccumulatorGroupComboBox.setMinimumSize(new java.awt.Dimension(100, 27));

                ivjAccumulatorGroupComboBox.addItem("Default");
                ivjAccumulatorGroupComboBox.addItem("FirstGroup");
                ivjAccumulatorGroupComboBox.addItem("SecondGroup");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAccumulatorGroupComboBox;
    }

    private javax.swing.JCheckBox getAccumulatorRateCheckBox() {
        if (ivjAccumulatorRateCheckBox == null) {
            try {
                ivjAccumulatorRateCheckBox = new javax.swing.JCheckBox();
                ivjAccumulatorRateCheckBox.setName("AccumulatorRateCheckBox");
                ivjAccumulatorRateCheckBox.setText("Accumulator Rate");
                ivjAccumulatorRateCheckBox.setVisible(true);
                ivjAccumulatorRateCheckBox.setActionCommand("AccumulatorRateCheckBox");
                ivjAccumulatorRateCheckBox.setFont(new java.awt.Font("Arial", 1, 12));
                ivjAccumulatorRateCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAccumulatorRateCheckBox;
    }

    private JComboBox<String> getAccumulatorRateComboBox() {
        if (ivjAccumulatorRateComboBox == null) {
            try {
                ivjAccumulatorRateComboBox = new JComboBox<String>();
                ivjAccumulatorRateComboBox.setName("AccumulatorRateComboBox");
                ivjAccumulatorRateComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
                ivjAccumulatorRateComboBox.setMinimumSize(new java.awt.Dimension(100, 27));

                // Accumulator Rate JComboBox
                ivjAccumulatorRateComboBox.addItem("1 minute");
                ivjAccumulatorRateComboBox.addItem("2 minute");
                ivjAccumulatorRateComboBox.addItem("3 minute");
                ivjAccumulatorRateComboBox.addItem("5 minute");
                ivjAccumulatorRateComboBox.addItem("10 minute");
                ivjAccumulatorRateComboBox.addItem("15 minute");
                ivjAccumulatorRateComboBox.addItem("30 minute");
                ivjAccumulatorRateComboBox.addItem("1 hour");
                ivjAccumulatorRateComboBox.addItem("2 hour");
                ivjAccumulatorRateComboBox.addItem("6 hour");
                ivjAccumulatorRateComboBox.addItem("12 hour");
                ivjAccumulatorRateComboBox.addItem("Daily");

                // default value
                ivjAccumulatorRateComboBox.setSelectedItem("Daily");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAccumulatorRateComboBox;
    }

    private JComboBox<String> getIntegrityGroupComboBox() {
        if (ivjIntegrityGroupComboBox == null) {
            try {
                ivjIntegrityGroupComboBox = new JComboBox<String>();
                ivjIntegrityGroupComboBox.setName("IntegrityGroupComboBox");
                ivjIntegrityGroupComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
                ivjIntegrityGroupComboBox.setMinimumSize(new java.awt.Dimension(100, 27));

                ivjIntegrityGroupComboBox.addItem("Default");
                ivjIntegrityGroupComboBox.addItem("FirstGroup");
                ivjIntegrityGroupComboBox.addItem("SecondGroup");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjIntegrityGroupComboBox;
    }

    private javax.swing.JCheckBox getIntegrityRateCheckBox() {
        if (ivjIntegrityRateCheckBox == null) {
            try {
                ivjIntegrityRateCheckBox = new javax.swing.JCheckBox();
                ivjIntegrityRateCheckBox.setName("IntegrityRateCheckBox");
                ivjIntegrityRateCheckBox.setText("Integrity Rate");
                ivjIntegrityRateCheckBox.setVisible(true);
                ivjIntegrityRateCheckBox.setActionCommand("IntegrityRateCheckBox");
                ivjIntegrityRateCheckBox.setFont(new java.awt.Font("Arial", 1, 12));
                ivjIntegrityRateCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjIntegrityRateCheckBox;
    }

    private JComboBox<String> getIntegrityRateComboBox() {
        if (ivjIntegrityRateComboBox == null) {
            try {
                ivjIntegrityRateComboBox = new JComboBox<String>();
                ivjIntegrityRateComboBox.setName("IntegrityRateComboBox");
                ivjIntegrityRateComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
                ivjIntegrityRateComboBox.setMinimumSize(new java.awt.Dimension(100, 27));

                // Integerity Rate JComboBox
                ivjIntegrityRateComboBox.addItem("5 seconds");
                ivjIntegrityRateComboBox.addItem("10 seconds");
                ivjIntegrityRateComboBox.addItem("15 seconds");
                ivjIntegrityRateComboBox.addItem("30 seconds");
                ivjIntegrityRateComboBox.addItem("1 minute");
                ivjIntegrityRateComboBox.addItem("2 minute");
                ivjIntegrityRateComboBox.addItem("3 minute");
                ivjIntegrityRateComboBox.addItem("5 minute");
                ivjIntegrityRateComboBox.addItem("10 minute");
                ivjIntegrityRateComboBox.addItem("15 minute");
                ivjIntegrityRateComboBox.addItem("30 minute");
                ivjIntegrityRateComboBox.addItem("1 hour");
                ivjIntegrityRateComboBox.addItem("2 hour");
                ivjIntegrityRateComboBox.addItem("6 hour");
                ivjIntegrityRateComboBox.addItem("12 hour");
                ivjIntegrityRateComboBox.addItem("Daily");

                // default value
                ivjIntegrityRateComboBox.setSelectedItem("5 minute");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjIntegrityRateComboBox;
    }

    private javax.swing.JCheckBox getJCheckBoxScanWindow() {
        if (ivjJCheckBoxScanWindow == null) {
            try {
                ivjJCheckBoxScanWindow = new javax.swing.JCheckBox();
                ivjJCheckBoxScanWindow.setName("JCheckBoxScanWindow");
                ivjJCheckBoxScanWindow.setText("Enabled");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxScanWindow;
    }

    private JComboBox<String> getJComboBoxAltAccRate() {
        if (ivjJComboBoxAltAccRate == null) {
            try {
                ivjJComboBoxAltAccRate = new JComboBox<String>();
                ivjJComboBoxAltAccRate.setName("JComboBoxAltAccRate");
                ivjJComboBoxAltAccRate.setPreferredSize(new java.awt.Dimension(100, 27));
                ivjJComboBoxAltAccRate.setMinimumSize(new java.awt.Dimension(100, 27));

                for (int i = 0; i < ALT_SELECTIONS.length; i++) {
                    ivjJComboBoxAltAccRate.addItem(ALT_SELECTIONS[i]);
                }

                // default value
                updateAltJComboBox(getAccumulatorRateComboBox(), getJComboBoxAltAccRate());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxAltAccRate;
    }

    private JComboBox<String> getJComboBoxAltHealthChk() {
        if (ivjJComboBoxAltHealthChk == null) {
            try {
                ivjJComboBoxAltHealthChk = new JComboBox<String>();
                ivjJComboBoxAltHealthChk.setName("JComboBoxAltHealthChk");
                ivjJComboBoxAltHealthChk.setPreferredSize(new java.awt.Dimension(100, 27));
                ivjJComboBoxAltHealthChk.setMinimumSize(new java.awt.Dimension(100, 27));

                for (int i = 0; i < ALT_SELECTIONS.length; i++) {
                    ivjJComboBoxAltHealthChk.addItem(ALT_SELECTIONS[i]);
                }

                // default value
                updateAltJComboBox(getPeriodicHealthIntervalComboBox(), getJComboBoxAltHealthChk());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxAltHealthChk;
    }

    private JComboBox<String> getJComboBoxAltIntegrityRate() {
        if (ivjJComboBoxAltIntegrityRate == null) {
            try {
                ivjJComboBoxAltIntegrityRate = new JComboBox<String>();
                ivjJComboBoxAltIntegrityRate.setName("JComboBoxAltIntegrityRate");
                ivjJComboBoxAltIntegrityRate.setPreferredSize(new java.awt.Dimension(100, 27));
                ivjJComboBoxAltIntegrityRate.setMinimumSize(new java.awt.Dimension(100, 27));

                for (int i = 0; i < ALT_SELECTIONS.length; i++) {
                    ivjJComboBoxAltIntegrityRate.addItem(ALT_SELECTIONS[i]);
                }

                // default value
                updateAltJComboBox(getIntegrityRateComboBox(), getJComboBoxAltIntegrityRate());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxAltIntegrityRate;
    }

    private JComboBox<String> getJComboBoxScanWindowType() {
        if (ivjJComboBoxScanWindowType == null) {
            try {
                ivjJComboBoxScanWindowType = new JComboBox<String>();
                ivjJComboBoxScanWindowType.setName("JComboBoxScanWindowType");
                ivjJComboBoxScanWindowType.setPreferredSize(new java.awt.Dimension(100, 27));
                ivjJComboBoxScanWindowType.setMinimumSize(new java.awt.Dimension(100, 27));

                ivjJComboBoxScanWindowType.addItem(DeviceWindow.TYPE_SCAN);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxScanWindowType;
    }

    private javax.swing.JLabel getJLabelAltIntervalAcc() {
        if (ivjJLabelAltIntervalAcc == null) {
            try {
                ivjJLabelAltIntervalAcc = new javax.swing.JLabel();
                ivjJLabelAltIntervalAcc.setName("JLabelAltIntervalAcc");
                ivjJLabelAltIntervalAcc.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelAltIntervalAcc.setText("Alt Interval:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelAltIntervalAcc;
    }

    private javax.swing.JLabel getJLabelAltIntervalHlth() {
        if (ivjJLabelAltIntervalHlth == null) {
            try {
                ivjJLabelAltIntervalHlth = new javax.swing.JLabel();
                ivjJLabelAltIntervalHlth.setName("JLabelAltIntervalHlth");
                ivjJLabelAltIntervalHlth.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelAltIntervalHlth.setText("Alt Interval:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelAltIntervalHlth;
    }

    private javax.swing.JLabel getJLabelAltIntervalInt() {
        if (ivjJLabelAltIntervalInt == null) {
            try {
                ivjJLabelAltIntervalInt = new javax.swing.JLabel();
                ivjJLabelAltIntervalInt.setName("JLabelAltIntervalInt");
                ivjJLabelAltIntervalInt.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelAltIntervalInt.setText("Alt Interval:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelAltIntervalInt;
    }

    private javax.swing.JLabel getJLabelClose() {
        if (ivjJLabelClose == null) {
            try {
                ivjJLabelClose = new javax.swing.JLabel();
                ivjJLabelClose.setName("JLabelClose");
                ivjJLabelClose.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelClose.setText("Close:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelClose;
    }

    private javax.swing.JLabel getJLabelGroupAcc() {
        if (ivjJLabelGroupAcc == null) {
            try {
                ivjJLabelGroupAcc = new javax.swing.JLabel();
                ivjJLabelGroupAcc.setName("JLabelGroupAcc");
                ivjJLabelGroupAcc.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelGroupAcc.setText("Group:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelGroupAcc;
    }

    private javax.swing.JLabel getJLabelGroupHlth() {
        if (ivjJLabelGroupHlth == null) {
            try {
                ivjJLabelGroupHlth = new javax.swing.JLabel();
                ivjJLabelGroupHlth.setName("JLabelGroupHlth");
                ivjJLabelGroupHlth.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelGroupHlth.setText("Group:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelGroupHlth;
    }

    private javax.swing.JLabel getJLabelGroupInt() {
        if (ivjJLabelGroupInt == null) {
            try {
                ivjJLabelGroupInt = new javax.swing.JLabel();
                ivjJLabelGroupInt.setName("JLabelGroupInt");
                ivjJLabelGroupInt.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelGroupInt.setText("Group:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelGroupInt;
    }

    private javax.swing.JLabel getJLabelhhmm() {
        if (ivjJLabelhhmm == null) {
            try {
                ivjJLabelhhmm = new javax.swing.JLabel();
                ivjJLabelhhmm.setName("JLabelhhmm");
                ivjJLabelhhmm.setFont(new java.awt.Font("dialog", 0, 10));
                ivjJLabelhhmm.setText("(HH:mm)");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelhhmm;
    }

    private javax.swing.JLabel getJLabelhhmm1() {
        if (ivjJLabelhhmm1 == null) {
            try {
                ivjJLabelhhmm1 = new javax.swing.JLabel();
                ivjJLabelhhmm1.setName("JLabelhhmm1");
                ivjJLabelhhmm1.setFont(new java.awt.Font("dialog", 0, 10));
                ivjJLabelhhmm1.setText("(HH:mm)");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelhhmm1;
    }

    private javax.swing.JLabel getJLabelIntervalAcc() {
        if (ivjJLabelIntervalAcc == null) {
            try {
                ivjJLabelIntervalAcc = new javax.swing.JLabel();
                ivjJLabelIntervalAcc.setName("JLabelIntervalAcc");
                ivjJLabelIntervalAcc.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelIntervalAcc.setText("Interval:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelIntervalAcc;
    }

    private javax.swing.JLabel getJLabelIntervalHlth() {
        if (ivjJLabelIntervalHlth == null) {
            try {
                ivjJLabelIntervalHlth = new javax.swing.JLabel();
                ivjJLabelIntervalHlth.setName("JLabelIntervalHlth");
                ivjJLabelIntervalHlth.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelIntervalHlth.setText("Interval:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelIntervalHlth;
    }

    private javax.swing.JLabel getJLabelIntervalInt() {
        if (ivjJLabelIntervalInt == null) {
            try {
                ivjJLabelIntervalInt = new javax.swing.JLabel();
                ivjJLabelIntervalInt.setName("JLabelIntervalInt");
                ivjJLabelIntervalInt.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelIntervalInt.setText("Interval:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelIntervalInt;
    }

    private javax.swing.JLabel getJLabelOpen() {
        if (ivjJLabelOpen == null) {
            try {
                ivjJLabelOpen = new javax.swing.JLabel();
                ivjJLabelOpen.setName("JLabelOpen");
                ivjJLabelOpen.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelOpen.setText("Open:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelOpen;
    }

    private javax.swing.JLabel getJLabelType() {
        if (ivjJLabelType == null) {
            try {
                ivjJLabelType = new javax.swing.JLabel();
                ivjJLabelType.setName("JLabelType");
                ivjJLabelType.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelType.setText("Type:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelType;
    }

    private javax.swing.JPanel getJPanelScanConfig() {
        if (ivjJPanelScanConfig == null) {
            try {
                ivjJPanelScanConfig = new javax.swing.JPanel();
                ivjJPanelScanConfig.setName("JPanelScanConfig");
                ivjJPanelScanConfig.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsPeriodicHealthCheckBox = new java.awt.GridBagConstraints();
                constraintsPeriodicHealthCheckBox.gridx = 1;
                constraintsPeriodicHealthCheckBox.gridy = 4;
                constraintsPeriodicHealthCheckBox.gridwidth = 2;
                constraintsPeriodicHealthCheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsPeriodicHealthCheckBox.ipadx = 40;
                constraintsPeriodicHealthCheckBox.ipady = -3;
                constraintsPeriodicHealthCheckBox.insets = new java.awt.Insets(2, 5, 0, 7);
                getJPanelScanConfig().add(getPeriodicHealthCheckBox(), constraintsPeriodicHealthCheckBox);

                java.awt.GridBagConstraints constraintsJLabelIntervalHlth = new java.awt.GridBagConstraints();
                constraintsJLabelIntervalHlth.gridx = 1;
                constraintsJLabelIntervalHlth.gridy = 5;
                constraintsJLabelIntervalHlth.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelIntervalHlth.ipadx = 4;
                constraintsJLabelIntervalHlth.insets = new java.awt.Insets(2, 22, 2, 20);
                getJPanelScanConfig().add(getJLabelIntervalHlth(), constraintsJLabelIntervalHlth);

                java.awt.GridBagConstraints constraintsJLabelAltIntervalHlth = new java.awt.GridBagConstraints();
                constraintsJLabelAltIntervalHlth.gridx = 1;
                constraintsJLabelAltIntervalHlth.gridy = 6;
                constraintsJLabelAltIntervalHlth.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelAltIntervalHlth.ipadx = 4;
                constraintsJLabelAltIntervalHlth.insets = new java.awt.Insets(4, 22, 2, 0);
                getJPanelScanConfig().add(getJLabelAltIntervalHlth(), constraintsJLabelAltIntervalHlth);

                java.awt.GridBagConstraints constraintsJComboBoxAltHealthChk = new java.awt.GridBagConstraints();
                constraintsJComboBoxAltHealthChk.gridx = 2;
                constraintsJComboBoxAltHealthChk.gridy = 6;
                constraintsJComboBoxAltHealthChk.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJComboBoxAltHealthChk.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJComboBoxAltHealthChk.weightx = 1.0;
                constraintsJComboBoxAltHealthChk.ipady = -7;
                constraintsJComboBoxAltHealthChk.insets = new java.awt.Insets(3, 0, 2, 6);
                getJPanelScanConfig().add(getJComboBoxAltHealthChk(), constraintsJComboBoxAltHealthChk);

                java.awt.GridBagConstraints constraintsPeriodicHealthIntervalComboBox = new java.awt.GridBagConstraints();
                constraintsPeriodicHealthIntervalComboBox.gridx = 2;
                constraintsPeriodicHealthIntervalComboBox.gridy = 5;
                constraintsPeriodicHealthIntervalComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsPeriodicHealthIntervalComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsPeriodicHealthIntervalComboBox.weightx = 1.0;
                constraintsPeriodicHealthIntervalComboBox.ipady = -7;
                constraintsPeriodicHealthIntervalComboBox.insets = new java.awt.Insets(1, 0, 2, 6);
                getJPanelScanConfig().add(getPeriodicHealthIntervalComboBox(), constraintsPeriodicHealthIntervalComboBox);

                java.awt.GridBagConstraints constraintsJLabelGroupHlth = new java.awt.GridBagConstraints();
                constraintsJLabelGroupHlth.gridx = 3;
                constraintsJLabelGroupHlth.gridy = 5;
                constraintsJLabelGroupHlth.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelGroupHlth.ipadx = 7;
                constraintsJLabelGroupHlth.insets = new java.awt.Insets(2, 7, 2, 0);
                getJPanelScanConfig().add(getJLabelGroupHlth(), constraintsJLabelGroupHlth);

                java.awt.GridBagConstraints constraintsPeriodicHealthGroupComboBox = new java.awt.GridBagConstraints();
                constraintsPeriodicHealthGroupComboBox.gridx = 4;
                constraintsPeriodicHealthGroupComboBox.gridy = 5;
                constraintsPeriodicHealthGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsPeriodicHealthGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsPeriodicHealthGroupComboBox.weightx = 1.0;
                constraintsPeriodicHealthGroupComboBox.ipadx = 22;
                constraintsPeriodicHealthGroupComboBox.ipady = -7;
                constraintsPeriodicHealthGroupComboBox.insets = new java.awt.Insets(1, 1, 2, 18);
                getJPanelScanConfig().add(getPeriodicHealthGroupComboBox(), constraintsPeriodicHealthGroupComboBox);

                java.awt.GridBagConstraints constraintsAccumulatorGroupComboBox = new java.awt.GridBagConstraints();
                constraintsAccumulatorGroupComboBox.gridx = 4;
                constraintsAccumulatorGroupComboBox.gridy = 8;
                constraintsAccumulatorGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsAccumulatorGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsAccumulatorGroupComboBox.weightx = 1.0;
                constraintsAccumulatorGroupComboBox.ipadx = 22;
                constraintsAccumulatorGroupComboBox.ipady = -7;
                constraintsAccumulatorGroupComboBox.insets = new java.awt.Insets(1, 1, 4, 18);
                getJPanelScanConfig().add(getAccumulatorGroupComboBox(), constraintsAccumulatorGroupComboBox);

                java.awt.GridBagConstraints constraintsJLabelGroupAcc = new java.awt.GridBagConstraints();
                constraintsJLabelGroupAcc.gridx = 3;
                constraintsJLabelGroupAcc.gridy = 8;
                constraintsJLabelGroupAcc.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelGroupAcc.ipadx = 7;
                constraintsJLabelGroupAcc.insets = new java.awt.Insets(2, 7, 4, 0);
                getJPanelScanConfig().add(getJLabelGroupAcc(), constraintsJLabelGroupAcc);

                java.awt.GridBagConstraints constraintsJLabelAltIntervalAcc = new java.awt.GridBagConstraints();
                constraintsJLabelAltIntervalAcc.gridx = 1;
                constraintsJLabelAltIntervalAcc.gridy = 9;
                constraintsJLabelAltIntervalAcc.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelAltIntervalAcc.ipadx = 4;
                constraintsJLabelAltIntervalAcc.insets = new java.awt.Insets(2, 22, 5, 0);
                getJPanelScanConfig().add(getJLabelAltIntervalAcc(), constraintsJLabelAltIntervalAcc);

                java.awt.GridBagConstraints constraintsJLabelIntervalAcc = new java.awt.GridBagConstraints();
                constraintsJLabelIntervalAcc.gridx = 1;
                constraintsJLabelIntervalAcc.gridy = 8;
                constraintsJLabelIntervalAcc.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelIntervalAcc.ipadx = 4;
                constraintsJLabelIntervalAcc.insets = new java.awt.Insets(5, 22, 1, 20);
                getJPanelScanConfig().add(getJLabelIntervalAcc(), constraintsJLabelIntervalAcc);

                java.awt.GridBagConstraints constraintsAccumulatorRateCheckBox = new java.awt.GridBagConstraints();
                constraintsAccumulatorRateCheckBox.gridx = 1;
                constraintsAccumulatorRateCheckBox.gridy = 7;
                constraintsAccumulatorRateCheckBox.gridwidth = 2;
                constraintsAccumulatorRateCheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsAccumulatorRateCheckBox.ipadx = 67;
                constraintsAccumulatorRateCheckBox.insets = new java.awt.Insets(3, 5, 0, 7);
                getJPanelScanConfig().add(getAccumulatorRateCheckBox(), constraintsAccumulatorRateCheckBox);

                java.awt.GridBagConstraints constraintsAccumulatorRateComboBox = new java.awt.GridBagConstraints();
                constraintsAccumulatorRateComboBox.gridx = 2;
                constraintsAccumulatorRateComboBox.gridy = 8;
                constraintsAccumulatorRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsAccumulatorRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsAccumulatorRateComboBox.weightx = 1.0;
                constraintsAccumulatorRateComboBox.ipady = -7;
                constraintsAccumulatorRateComboBox.insets = new java.awt.Insets(1, 0, 4, 6);
                getJPanelScanConfig().add(getAccumulatorRateComboBox(), constraintsAccumulatorRateComboBox);

                java.awt.GridBagConstraints constraintsJComboBoxAltAccRate = new java.awt.GridBagConstraints();
                constraintsJComboBoxAltAccRate.gridx = 2;
                constraintsJComboBoxAltAccRate.gridy = 9;
                constraintsJComboBoxAltAccRate.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJComboBoxAltAccRate.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJComboBoxAltAccRate.weightx = 1.0;
                constraintsJComboBoxAltAccRate.ipady = -7;
                constraintsJComboBoxAltAccRate.insets = new java.awt.Insets(1, 0, 5, 6);
                getJPanelScanConfig().add(getJComboBoxAltAccRate(), constraintsJComboBoxAltAccRate);

                java.awt.GridBagConstraints constraintsIntegrityGroupComboBox = new java.awt.GridBagConstraints();
                constraintsIntegrityGroupComboBox.gridx = 4;
                constraintsIntegrityGroupComboBox.gridy = 2;
                constraintsIntegrityGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsIntegrityGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsIntegrityGroupComboBox.weightx = 1.0;
                constraintsIntegrityGroupComboBox.ipadx = 22;
                constraintsIntegrityGroupComboBox.ipady = -7;
                constraintsIntegrityGroupComboBox.insets = new java.awt.Insets(1, 1, 4, 18);
                getJPanelScanConfig().add(getIntegrityGroupComboBox(), constraintsIntegrityGroupComboBox);

                java.awt.GridBagConstraints constraintsJLabelGroupInt = new java.awt.GridBagConstraints();
                constraintsJLabelGroupInt.gridx = 3;
                constraintsJLabelGroupInt.gridy = 2;
                constraintsJLabelGroupInt.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelGroupInt.ipadx = 7;
                constraintsJLabelGroupInt.insets = new java.awt.Insets(2, 7, 4, 0);
                getJPanelScanConfig().add(getJLabelGroupInt(), constraintsJLabelGroupInt);

                java.awt.GridBagConstraints constraintsIntegrityRateComboBox = new java.awt.GridBagConstraints();
                constraintsIntegrityRateComboBox.gridx = 2;
                constraintsIntegrityRateComboBox.gridy = 2;
                constraintsIntegrityRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsIntegrityRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsIntegrityRateComboBox.weightx = 1.0;
                constraintsIntegrityRateComboBox.ipady = -7;
                constraintsIntegrityRateComboBox.insets = new java.awt.Insets(1, 0, 4, 6);
                getJPanelScanConfig().add(getIntegrityRateComboBox(), constraintsIntegrityRateComboBox);

                java.awt.GridBagConstraints constraintsJLabelIntervalInt = new java.awt.GridBagConstraints();
                constraintsJLabelIntervalInt.gridx = 1;
                constraintsJLabelIntervalInt.gridy = 2;
                constraintsJLabelIntervalInt.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelIntervalInt.ipadx = 4;
                constraintsJLabelIntervalInt.insets = new java.awt.Insets(2, 22, 4, 20);
                getJPanelScanConfig().add(getJLabelIntervalInt(), constraintsJLabelIntervalInt);

                java.awt.GridBagConstraints constraintsJLabelAltIntervalInt = new java.awt.GridBagConstraints();
                constraintsJLabelAltIntervalInt.gridx = 1;
                constraintsJLabelAltIntervalInt.gridy = 3;
                constraintsJLabelAltIntervalInt.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelAltIntervalInt.ipadx = 4;
                constraintsJLabelAltIntervalInt.insets = new java.awt.Insets(5, 22, 1, 0);
                getJPanelScanConfig().add(getJLabelAltIntervalInt(), constraintsJLabelAltIntervalInt);

                java.awt.GridBagConstraints constraintsJComboBoxAltIntegrityRate = new java.awt.GridBagConstraints();
                constraintsJComboBoxAltIntegrityRate.gridx = 2;
                constraintsJComboBoxAltIntegrityRate.gridy = 3;
                constraintsJComboBoxAltIntegrityRate.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJComboBoxAltIntegrityRate.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJComboBoxAltIntegrityRate.weightx = 1.0;
                constraintsJComboBoxAltIntegrityRate.ipady = -7;
                constraintsJComboBoxAltIntegrityRate.insets = new java.awt.Insets(4, 0, 1, 6);
                getJPanelScanConfig().add(getJComboBoxAltIntegrityRate(), constraintsJComboBoxAltIntegrityRate);

                java.awt.GridBagConstraints constraintsIntegrityRateCheckBox = new java.awt.GridBagConstraints();
                constraintsIntegrityRateCheckBox.gridx = 1;
                constraintsIntegrityRateCheckBox.gridy = 1;
                constraintsIntegrityRateCheckBox.gridwidth = 2;
                constraintsIntegrityRateCheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsIntegrityRateCheckBox.ipadx = 94;
                constraintsIntegrityRateCheckBox.insets = new java.awt.Insets(4, 5, 0, 7);
                getJPanelScanConfig().add(getIntegrityRateCheckBox(), constraintsIntegrityRateCheckBox);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelScanConfig;
    }

    private javax.swing.JPanel getJPanelScanWindow() {
        if (ivjJPanelScanWindow == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
                ivjLocalBorder.setTitle("Optional Scan Window (Time)");
                ivjJPanelScanWindow = new javax.swing.JPanel();
                ivjJPanelScanWindow.setName("JPanelScanWindow");
                ivjJPanelScanWindow.setBorder(ivjLocalBorder);
                ivjJPanelScanWindow.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsJCheckBoxScanWindow = new java.awt.GridBagConstraints();
                constraintsJCheckBoxScanWindow.gridx = 1;
                constraintsJCheckBoxScanWindow.gridy = 1;
                constraintsJCheckBoxScanWindow.gridwidth = 2;
                constraintsJCheckBoxScanWindow.ipadx = 36;
                constraintsJCheckBoxScanWindow.insets = new java.awt.Insets(0, 22, 2, 13);
                getJPanelScanWindow().add(getJCheckBoxScanWindow(), constraintsJCheckBoxScanWindow);

                java.awt.GridBagConstraints constraintsJLabelType = new java.awt.GridBagConstraints();
                constraintsJLabelType.gridx = 4;
                constraintsJLabelType.gridy = 1;
                constraintsJLabelType.ipadx = 18;
                constraintsJLabelType.insets = new java.awt.Insets(0, 5, 5, 16);
                getJPanelScanWindow().add(getJLabelType(), constraintsJLabelType);

                java.awt.GridBagConstraints constraintsJLabelOpen = new java.awt.GridBagConstraints();
                constraintsJLabelOpen.gridx = 1;
                constraintsJLabelOpen.gridy = 2;
                constraintsJLabelOpen.ipadx = 3;
                constraintsJLabelOpen.insets = new java.awt.Insets(3, 22, 9, 13);
                getJPanelScanWindow().add(getJLabelOpen(), constraintsJLabelOpen);

                java.awt.GridBagConstraints constraintsJTextFieldOpen = new java.awt.GridBagConstraints();
                constraintsJTextFieldOpen.gridx = 2;
                constraintsJTextFieldOpen.gridy = 2;
                constraintsJTextFieldOpen.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJTextFieldOpen.weightx = 1.0;
                constraintsJTextFieldOpen.ipadx = 46;
                constraintsJTextFieldOpen.insets = new java.awt.Insets(2, 4, 9, 0);
                getJPanelScanWindow().add(getJTextFieldOpen(), constraintsJTextFieldOpen);

                java.awt.GridBagConstraints constraintsJLabelClose = new java.awt.GridBagConstraints();
                constraintsJLabelClose.gridx = 4;
                constraintsJLabelClose.gridy = 2;
                constraintsJLabelClose.ipadx = 22;
                constraintsJLabelClose.insets = new java.awt.Insets(2, 5, 10, 8);
                getJPanelScanWindow().add(getJLabelClose(), constraintsJLabelClose);

                java.awt.GridBagConstraints constraintsJTextFieldClose = new java.awt.GridBagConstraints();
                constraintsJTextFieldClose.gridx = 5;
                constraintsJTextFieldClose.gridy = 2;
                constraintsJTextFieldClose.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJTextFieldClose.weightx = 1.0;
                constraintsJTextFieldClose.ipadx = 46;
                constraintsJTextFieldClose.insets = new java.awt.Insets(2, -15, 9, 1);
                getJPanelScanWindow().add(getJTextFieldClose(), constraintsJTextFieldClose);

                java.awt.GridBagConstraints constraintsJComboBoxScanWindowType = new java.awt.GridBagConstraints();
                constraintsJComboBoxScanWindowType.gridx = 5;
                constraintsJComboBoxScanWindowType.gridy = 1;
                constraintsJComboBoxScanWindowType.gridwidth = 2;
                constraintsJComboBoxScanWindowType.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJComboBoxScanWindowType.weightx = 1.0;
                constraintsJComboBoxScanWindowType.ipadx = 13;
                constraintsJComboBoxScanWindowType.ipady = -7;
                constraintsJComboBoxScanWindowType.insets = new java.awt.Insets(0, -15, 4, 4);
                getJPanelScanWindow().add(getJComboBoxScanWindowType(), constraintsJComboBoxScanWindowType);

                java.awt.GridBagConstraints constraintsJLabelhhmm = new java.awt.GridBagConstraints();
                constraintsJLabelhhmm.gridx = 3;
                constraintsJLabelhhmm.gridy = 2;
                constraintsJLabelhhmm.ipadx = 4;
                constraintsJLabelhhmm.insets = new java.awt.Insets(5, 0, 12, 5);
                getJPanelScanWindow().add(getJLabelhhmm(), constraintsJLabelhhmm);

                java.awt.GridBagConstraints constraintsJLabelhhmm1 = new java.awt.GridBagConstraints();
                constraintsJLabelhhmm1.gridx = 6;
                constraintsJLabelhhmm1.gridy = 2;
                constraintsJLabelhhmm1.ipadx = 4;
                constraintsJLabelhhmm1.insets = new java.awt.Insets(5, 2, 12, 15);
                getJPanelScanWindow().add(getJLabelhhmm1(), constraintsJLabelhhmm1);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelScanWindow;
    }

    private JTextFieldTimeEntry getJTextFieldClose() {
        if (ivjJTextFieldClose == null) {
            try {
                ivjJTextFieldClose = new JTextFieldTimeEntry();
                ivjJTextFieldClose.setName("JTextFieldClose");
                ivjJTextFieldClose.setToolTipText("when to close window from midnight");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldClose;
    }

    private JTextFieldTimeEntry getJTextFieldOpen() {
        if (ivjJTextFieldOpen == null) {
            try {
                ivjJTextFieldOpen = new JTextFieldTimeEntry();
                ivjJTextFieldOpen.setName("JTextFieldOpen");
                ivjJTextFieldOpen.setToolTipText("when to open window from midnight");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldOpen;
    }

    private javax.swing.JCheckBox getPeriodicHealthCheckBox() {
        if (ivjPeriodicHealthCheckBox == null) {
            try {
                ivjPeriodicHealthCheckBox = new javax.swing.JCheckBox();
                ivjPeriodicHealthCheckBox.setName("PeriodicHealthCheckBox");
                ivjPeriodicHealthCheckBox.setText("Periodic Health Check");
                ivjPeriodicHealthCheckBox.setMaximumSize(new java.awt.Dimension(150, 21));
                ivjPeriodicHealthCheckBox.setActionCommand("GeneralRateCheckBox");
                ivjPeriodicHealthCheckBox.setPreferredSize(new java.awt.Dimension(150, 21));
                ivjPeriodicHealthCheckBox.setFont(new java.awt.Font("Arial", 1, 12));
                ivjPeriodicHealthCheckBox.setMinimumSize(new java.awt.Dimension(150, 21));
                ivjPeriodicHealthCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPeriodicHealthCheckBox;
    }

    private JComboBox<String> getPeriodicHealthGroupComboBox() {
        if (ivjPeriodicHealthGroupComboBox == null) {
            try {
                ivjPeriodicHealthGroupComboBox = new JComboBox<String>();
                ivjPeriodicHealthGroupComboBox.setName("PeriodicHealthGroupComboBox");
                ivjPeriodicHealthGroupComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
                ivjPeriodicHealthGroupComboBox.setMinimumSize(new java.awt.Dimension(100, 27));

                ivjPeriodicHealthGroupComboBox.addItem("Default");
                ivjPeriodicHealthGroupComboBox.addItem("FirstGroup");
                ivjPeriodicHealthGroupComboBox.addItem("SecondGroup");

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPeriodicHealthGroupComboBox;
    }

    private JComboBox<String> getPeriodicHealthIntervalComboBox() {
        if (ivjPeriodicHealthIntervalComboBox == null) {
            try {
                ivjPeriodicHealthIntervalComboBox = new JComboBox<String>();
                ivjPeriodicHealthIntervalComboBox.setName("PeriodicHealthIntervalComboBox");
                ivjPeriodicHealthIntervalComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
                ivjPeriodicHealthIntervalComboBox.setMinimumSize(new java.awt.Dimension(100, 27));

                ivjPeriodicHealthIntervalComboBox.addItem("1 second");
                ivjPeriodicHealthIntervalComboBox.addItem("2 second");
                ivjPeriodicHealthIntervalComboBox.addItem("5 second");
                ivjPeriodicHealthIntervalComboBox.addItem("10 second");
                ivjPeriodicHealthIntervalComboBox.addItem("15 second");
                ivjPeriodicHealthIntervalComboBox.addItem("30 second");
                ivjPeriodicHealthIntervalComboBox.addItem("1 minute");
                ivjPeriodicHealthIntervalComboBox.addItem("2 minute");
                ivjPeriodicHealthIntervalComboBox.addItem("3 minute");
                ivjPeriodicHealthIntervalComboBox.addItem("5 minute");
                ivjPeriodicHealthIntervalComboBox.addItem("10 minute");
                ivjPeriodicHealthIntervalComboBox.addItem("15 minute");
                ivjPeriodicHealthIntervalComboBox.addItem("30 minute");
                ivjPeriodicHealthIntervalComboBox.addItem("1 hour");
                ivjPeriodicHealthIntervalComboBox.addItem("2 hour");
                ivjPeriodicHealthIntervalComboBox.addItem("6 hour");
                ivjPeriodicHealthIntervalComboBox.addItem("12 hour");
                ivjPeriodicHealthIntervalComboBox.addItem("Daily");

                // default value
                ivjPeriodicHealthIntervalComboBox.setSelectedItem("30 minute");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPeriodicHealthIntervalComboBox;
    }

    @Override
    public Object getValue(Object device) {
        TwoWayDevice val = null;

        if (device instanceof com.cannontech.database.data.multi.MultiDBPersistent) {
            val = (TwoWayDevice) com.cannontech.database.data.multi.CommonMulti.getFirstObjectOfType(TwoWayDevice.class,
                                                                                                     (com.cannontech.database.data.multi.MultiDBPersistent) device);
        } else if (device instanceof SmartMultiDBPersistent) {
            val = (TwoWayDevice) ((SmartMultiDBPersistent) device).getOwnerDBPersistent();
        } else {
            val = (TwoWayDevice) device;
        }

        Integer deviceID = val.getDevice().getDeviceID();
        HashMap<String, DeviceScanRate> oldScanRateMap = val.getDeviceScanRateMap();
        HashMap<String, DeviceScanRate> newScanRateMap = new HashMap<String, DeviceScanRate>(3);

        if (val instanceof CCUBase || val instanceof TCUBase || val instanceof RepeaterBase || val instanceof TapTerminalBase || val instanceof RTCBase || val instanceof CCU721) {
            if (getPeriodicHealthCheckBox().isSelected() && getPeriodicHealthCheckBox().isVisible()) {
                Integer generalRate = SwingUtil.getIntervalComboBoxSecondsValue(getPeriodicHealthIntervalComboBox());
                Integer altRate = SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxAltHealthChk().getSelectedItem().equals(CtiUtilities.STRING_NONE) ? 
                        getPeriodicHealthIntervalComboBox() : getJComboBoxAltHealthChk());

                Integer generalGroup = new Integer(getPeriodicHealthGroupComboBox().getSelectedIndex());
                newScanRateMap.put(DeviceScanRate.TYPE_STATUS, new DeviceScanRate(deviceID, DeviceScanRate.TYPE_STATUS, generalRate, generalGroup, altRate));
            }
        } else if (val instanceof IEDMeter || val instanceof RTM) {
            if (getPeriodicHealthCheckBox().isSelected() && getPeriodicHealthCheckBox().isVisible()) {
                Integer generalRate = SwingUtil.getIntervalComboBoxSecondsValue(getPeriodicHealthIntervalComboBox());
                Integer altRate = SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxAltHealthChk().getSelectedItem().equals(CtiUtilities.STRING_NONE) ? 
                        getPeriodicHealthIntervalComboBox() : getJComboBoxAltHealthChk());

                Integer generalGroup = new Integer(getPeriodicHealthGroupComboBox().getSelectedIndex());
                newScanRateMap.put(DeviceScanRate.TYPE_GENERAL, new DeviceScanRate(deviceID, DeviceScanRate.TYPE_GENERAL, generalRate, generalGroup, altRate));
            }
        } else if ((val instanceof RTUBase) || (val instanceof MCTBase) || (val instanceof LCUBase) || (val instanceof CapBankController6510) || (val instanceof CapBankController702x) ||
                (val instanceof CapBankControllerDNP) || (val instanceof DNPBase) || (val instanceof Series5Base) || (val instanceof TwoWayLCR) || (val instanceof IonBase)) {
            if (getPeriodicHealthCheckBox().isSelected() && getPeriodicHealthCheckBox().isVisible()) {
                if (val instanceof MCTBase) {
                    Integer generalRate = SwingUtil.getIntervalComboBoxSecondsValue(getPeriodicHealthIntervalComboBox());
                    Integer altRate = SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxAltHealthChk().getSelectedItem().equals(CtiUtilities.STRING_NONE) ? 
                            getPeriodicHealthIntervalComboBox() : getJComboBoxAltHealthChk());

                    Integer generalGroup = new Integer(getPeriodicHealthGroupComboBox().getSelectedIndex());
                    newScanRateMap.put(DeviceScanRate.TYPE_STATUS, new DeviceScanRate(deviceID, DeviceScanRate.TYPE_STATUS, generalRate, generalGroup, altRate));
                } else {
                    Integer generalRate = SwingUtil.getIntervalComboBoxSecondsValue(getPeriodicHealthIntervalComboBox());
                    Integer altRate = SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxAltHealthChk().getSelectedItem().equals(CtiUtilities.STRING_NONE) ? 
                            getPeriodicHealthIntervalComboBox() : getJComboBoxAltHealthChk());

                    Integer generalGroup = new Integer(getPeriodicHealthGroupComboBox().getSelectedIndex());
                    newScanRateMap.put(DeviceScanRate.TYPE_EXCEPTION, new DeviceScanRate(deviceID, DeviceScanRate.TYPE_EXCEPTION, generalRate, generalGroup, altRate));
                }
            }

            if (getAccumulatorRateCheckBox().isSelected() && getAccumulatorRateCheckBox().isVisible()) {
                Integer accumulatorRate = SwingUtil.getIntervalComboBoxSecondsValue(getAccumulatorRateComboBox());
                Integer altRate = SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxAltAccRate().getSelectedItem().equals(CtiUtilities.STRING_NONE) ? 
                        getAccumulatorRateComboBox() : getJComboBoxAltAccRate());

                Integer accumulatorGroup = new Integer(getAccumulatorGroupComboBox().getSelectedIndex());
                newScanRateMap.put(DeviceScanRate.TYPE_ACCUMULATOR, new DeviceScanRate(deviceID, DeviceScanRate.TYPE_ACCUMULATOR, accumulatorRate, accumulatorGroup, altRate));
            }

            if (getIntegrityRateCheckBox().isSelected() && getIntegrityRateCheckBox().isVisible()) {
                Integer integrityRate = SwingUtil.getIntervalComboBoxSecondsValue(getIntegrityRateComboBox());
                Integer altRate = SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxAltIntegrityRate().getSelectedItem().equals(CtiUtilities.STRING_NONE) ? 
                        getIntegrityRateComboBox() : getJComboBoxAltIntegrityRate());

                Integer integrityGroup = new Integer(getIntegrityGroupComboBox().getSelectedIndex());
                newScanRateMap.put(DeviceScanRate.TYPE_INTEGRITY, new DeviceScanRate(deviceID, DeviceScanRate.TYPE_INTEGRITY, integrityRate, integrityGroup, altRate));
            }
        }

        Iterator<DeviceScanRate> newIt = newScanRateMap.values().iterator();
        Iterator<DeviceScanRate> oldIt = oldScanRateMap.values().iterator();
        while (newIt.hasNext()) {

            DeviceScanRate newRate = newIt.next();

            // be sure no scan rates are set to zero (if any rate is zero, we
            // may have a problem)
            if (newRate.getIntervalRate().intValue() == 0 || newRate.getAlternateRate().intValue() == 0) {
                while (oldIt.hasNext()) {

                    DeviceScanRate oldRate = oldIt.next();
                    if (newRate.getScanType().equalsIgnoreCase(oldRate.getScanType())) {
                        // reset to our original scan rate values
                        newRate.setIntervalRate(new Integer(oldRate.getIntervalRate().intValue()));
                        newRate.setAlternateRate(new Integer(oldRate.getAlternateRate().intValue()));
                        break;
                    }
                }
            }

        }

        val.setDeviceScanRateMap(newScanRateMap);

        // set the scan window values here! Also we must do this in the Wizard
        val.getDeviceWindow().setType(getJComboBoxScanWindowType().getSelectedItem().toString());

        if (getJCheckBoxScanWindow().isSelected()) {
            val.getDeviceWindow().setWinOpen(getJTextFieldOpen().getTimeTotalSeconds());
            val.getDeviceWindow().setAlternateOpen(getJTextFieldOpen().getTimeTotalSeconds());

            val.getDeviceWindow().setWinClose(getJTextFieldClose().getTimeTotalSeconds());
            val.getDeviceWindow().setAlternateClose(getJTextFieldClose().getTimeTotalSeconds());
        } else {
            val.getDeviceWindow().setWinOpen(new Integer(0));
            val.getDeviceWindow().setAlternateOpen(new Integer(0));

            val.getDeviceWindow().setWinClose(new Integer(0));
            val.getDeviceWindow().setAlternateClose(new Integer(0));
        }

        return device;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
        ;
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getPeriodicHealthCheckBox().addActionListener(this);
        getAccumulatorRateCheckBox().addActionListener(this);
        getIntegrityRateCheckBox().addActionListener(this);
        getAccumulatorRateComboBox().addActionListener(this);
        getPeriodicHealthIntervalComboBox().addActionListener(this);
        getIntegrityRateComboBox().addActionListener(this);
        getIntegrityGroupComboBox().addActionListener(this);
        getAccumulatorGroupComboBox().addActionListener(this);
        getPeriodicHealthGroupComboBox().addActionListener(this);
        getJComboBoxAltHealthChk().addActionListener(this);
        getJComboBoxAltAccRate().addActionListener(this);
        getJComboBoxAltIntegrityRate().addActionListener(this);
        getJCheckBoxScanWindow().addActionListener(this);
        getJTextFieldOpen().addCaretListener(this);
        getJTextFieldClose().addCaretListener(this);
        getJComboBoxScanWindowType().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("DeviceScanRateEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(428, 309);

            java.awt.GridBagConstraints constraintsJPanelScanConfig = new java.awt.GridBagConstraints();
            constraintsJPanelScanConfig.gridx = 1;
            constraintsJPanelScanConfig.gridy = 1;
            constraintsJPanelScanConfig.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelScanConfig.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelScanConfig.weightx = 1.0;
            constraintsJPanelScanConfig.weighty = 1.0;
            constraintsJPanelScanConfig.insets = new java.awt.Insets(13, 11, 4, 16);
            add(getJPanelScanConfig(), constraintsJPanelScanConfig);

            java.awt.GridBagConstraints constraintsJPanelScanWindow = new java.awt.GridBagConstraints();
            constraintsJPanelScanWindow.gridx = 1;
            constraintsJPanelScanWindow.gridy = 2;
            constraintsJPanelScanWindow.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJPanelScanWindow.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelScanWindow.weightx = 1.0;
            constraintsJPanelScanWindow.weighty = 1.0;
            constraintsJPanelScanWindow.ipadx = 23;
            constraintsJPanelScanWindow.ipady = -10;
            constraintsJPanelScanWindow.insets = new java.awt.Insets(5, 11, 6, 16);
            add(getJPanelScanWindow(), constraintsJPanelScanWindow);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        jCheckBoxScanWindow_ActionPerformed(null);
    }

    @Override
    public boolean isInputValid() {
        if (getJCheckBoxScanWindow().isSelected() && (getJTextFieldClose().getTimeTotalSeconds().intValue() == 
                getJTextFieldOpen().getTimeTotalSeconds().intValue())) {
            setErrorString("The Device Window open and close values can not be equal");
            return false;
        }

        return true;
    }

    public void jCheckBoxScanWindow_ActionPerformed(ActionEvent actionEvent) {
        fireInputUpdate();
        getJLabelOpen().setEnabled(getJCheckBoxScanWindow().isSelected());
        getJLabelClose().setEnabled(getJCheckBoxScanWindow().isSelected());
        getJLabelType().setEnabled(getJCheckBoxScanWindow().isSelected());

        getJTextFieldClose().setEnabled(getJCheckBoxScanWindow().isSelected());
        getJTextFieldOpen().setEnabled(getJCheckBoxScanWindow().isSelected());
        getJComboBoxScanWindowType().setEnabled(getJCheckBoxScanWindow().isSelected());

        return;
    }

    private void setAccumulatorObjectsVisible(boolean value) {
        getAccumulatorRateCheckBox().setVisible(value);
        getAccumulatorRateComboBox().setVisible(value);
        getAccumulatorGroupComboBox().setVisible(value);
        getJComboBoxAltAccRate().setVisible(value);

        getJLabelAltIntervalAcc().setVisible(value);
        getJLabelIntervalAcc().setVisible(value);
        getJLabelGroupAcc().setVisible(value);
    }

    private void setScanWindowObjectsVisible(boolean value) {
        getJPanelScanWindow().setVisible(value);
    }

    private void initComboBoxValues(PaoType type) {

        if (type.isCcu()) {
            getPeriodicHealthIntervalComboBox().removeAllItems();

            getPeriodicHealthIntervalComboBox().addItem("5 minute");
            getPeriodicHealthIntervalComboBox().addItem("10 minute");
            getPeriodicHealthIntervalComboBox().addItem("15 minute");
            getPeriodicHealthIntervalComboBox().addItem("30 minute");
            getPeriodicHealthIntervalComboBox().addItem("1 hour");
            getPeriodicHealthIntervalComboBox().addItem("2 hour");
            getPeriodicHealthIntervalComboBox().addItem("6 hour");
            getPeriodicHealthIntervalComboBox().addItem("12 hour");
            getPeriodicHealthIntervalComboBox().addItem("Daily");

            // default value
            getPeriodicHealthIntervalComboBox().setSelectedItem("5 minute");
        } else if (type.isMct() || type.isRepeater()) {
            getPeriodicHealthIntervalComboBox().removeAllItems();

            getPeriodicHealthIntervalComboBox().addItem("1 minute");
            getPeriodicHealthIntervalComboBox().addItem("3 minute");
            getPeriodicHealthIntervalComboBox().addItem("5 minute");
            getPeriodicHealthIntervalComboBox().addItem("10 minute");
            getPeriodicHealthIntervalComboBox().addItem("15 minute");
            getPeriodicHealthIntervalComboBox().addItem("30 minute");
            getPeriodicHealthIntervalComboBox().addItem("1 hour");
            getPeriodicHealthIntervalComboBox().addItem("2 hour");
            getPeriodicHealthIntervalComboBox().addItem("6 hour");
            getPeriodicHealthIntervalComboBox().addItem("12 hour");
            getPeriodicHealthIntervalComboBox().addItem("Daily");

            // default value
            getPeriodicHealthIntervalComboBox().setSelectedItem("5 minute");
        }

    }

    public void setDeviceType(PaoType paoType) {
        // set some devices domain of possible values to a more limited approach
        initComboBoxValues(paoType);

        setAccumulatorObjectsVisible(false);
        setIntegrityObjectsVisible(false);

        getPeriodicHealthCheckBox().setSelected(false);
        getPeriodicHealthIntervalComboBox().setEnabled(false);
        getPeriodicHealthGroupComboBox().setEnabled(false);
        getJComboBoxAltHealthChk().setEnabled(false);

        if (paoType.isCcu() || 
                paoType.isTcu() || 
                paoType.isRepeater() || 
                (paoType == PaoType.TAPTERMINAL) || 
                (paoType == PaoType.WCTP_TERMINAL)) {
            getPeriodicHealthCheckBox().setText("Periodic Health Check");

            if (paoType.isRepeater()) {
                getPeriodicHealthIntervalComboBox().setSelectedItem("1 hour");
            }
        } else if (paoType == PaoType.DAVISWEATHER || paoType.isIed()) {
            getPeriodicHealthCheckBox().setText("General Data Collection");
            getPeriodicHealthIntervalComboBox().setSelectedItem("1 hour");
        }

        if (paoType.isRtu() || paoType.isIon() || 
                paoType.isMct() || 
                paoType.isLcu() || 
                paoType.isCbc() || 
                paoType == PaoType.SERIES_5_LMI || 
                paoType.isTwoWayLcr()) {

            if (paoType.isTwoWayLcr()) {
                getIntegrityRateCheckBox().setText("Demand & Status Rate");
            } else if (DeviceTypesFuncs.isMCT3xx(paoType) || DeviceTypesFuncs.isMCT4XX(paoType)) {
                if (DeviceTypesFuncs.isMCT410(paoType))
                    getIntegrityRateCheckBox().setText("Demand & Voltage Rate");
                else
                    getIntegrityRateCheckBox().setText("Demand & Status Rate");
                getAccumulatorRateCheckBox().setText("Accumulator (Energy) Rate");
            } else if (paoType.isMct()) {
                getPeriodicHealthCheckBox().setText("Status Rate");
                getIntegrityRateCheckBox().setText("Demand Rate");
                getAccumulatorRateCheckBox().setText("Accumulator (Energy) Rate");
            } else if (paoType == PaoType.RTUILEX || paoType == PaoType.RTM) {
                getPeriodicHealthCheckBox().setText("General Scan");
                getPeriodicHealthIntervalComboBox().setSelectedItem("15 second");
                getAccumulatorRateComboBox().setSelectedItem("15 minute");
            } else if (paoType == PaoType.RTU_DNP || paoType == PaoType.RTU_DART || paoType == PaoType.DNP_CBC_6510) {
                getPeriodicHealthCheckBox().setText("Class 1,2,3 Scan");
                getPeriodicHealthIntervalComboBox().setSelectedItem("15 second");

                getIntegrityRateCheckBox().setText("Class 0,1,2,3 Scan");
                getIntegrityRateComboBox().setSelectedItem("5 minute");
            } else if (paoType == PaoType.RTUWELCO) {
                getIntegrityRateCheckBox().setText("Integrity Rate");
                getIntegrityRateComboBox().setSelectedItem("3 minute");
                getPeriodicHealthCheckBox().setText("General Scan");
                getPeriodicHealthIntervalComboBox().setSelectedItem("15 second");
                getAccumulatorRateComboBox().setSelectedItem("15 minute");
            } else if (paoType == PaoType.RTU_MODBUS) {
                getIntegrityRateCheckBox().setText("Integrity Rate");
                getIntegrityRateComboBox().setSelectedItem("3 minute");
            } else if (paoType == PaoType.LCU415) {
                getPeriodicHealthCheckBox().setText("Status & Analog");
                getAccumulatorRateCheckBox().setText("Accumulator Rate");
            } else if (paoType == PaoType.ION_7700 || paoType == PaoType.ION_7330 || paoType == PaoType.ION_8300) {
                getAccumulatorRateCheckBox().setText("Eventlog Collection");
                getAccumulatorRateComboBox().setSelectedItem("1 hour");
            } else {
                // LCU's and RTU's
                getPeriodicHealthCheckBox().setText("Report by Exception");
                getPeriodicHealthIntervalComboBox().setSelectedItem("15 second");
                getAccumulatorRateComboBox().setSelectedItem("15 minute");
                getIntegrityRateComboBox().setSelectedItem("3 minute");
            }

            setIntegrityObjectsVisible(!(paoType == PaoType.RTUILEX || paoType == PaoType.LCU415 || paoType == PaoType.RTM));

            setHealthObjectsVisible(!(paoType == PaoType.DCT_501 || 
                    DeviceTypesFuncs.isMCT3xx(paoType) || 
                    DeviceTypesFuncs.isMCT4XX(paoType) || 
                    paoType == PaoType.SERIES_5_LMI || 
                    paoType == PaoType.RTU_MODBUS || 
                    paoType.isTwoWayLcr()));

            setAccumulatorObjectsVisible(!(paoType == PaoType.DCT_501 || 
                    paoType == PaoType.DNP_CBC_6510 || 
                    paoType == PaoType.RTU_DNP || 
                    paoType == PaoType.RTU_DART || 
                    paoType == PaoType.LCU_T3026 || 
                    paoType == PaoType.SERIES_5_LMI || 
                    paoType == PaoType.RTM || 
                    paoType == PaoType.RTU_MODBUS || 
                    paoType.isTwoWayLcr()));

            setScanWindowObjectsVisible(!(paoType.isTwoWayLcr()));

            getAccumulatorRateCheckBox().setSelected(false);
            getAccumulatorRateComboBox().setEnabled(false);
            getAccumulatorGroupComboBox().setEnabled(false);
            getJComboBoxAltAccRate().setEnabled(false);

            getIntegrityRateCheckBox().setSelected(false);
            getIntegrityRateComboBox().setEnabled(false);
            getIntegrityGroupComboBox().setEnabled(false);
            getJComboBoxAltIntegrityRate().setEnabled(false);
        }

        // update all the alt JComboBoxes
        getJComboBoxAltHealthChk().setSelectedItem(CtiUtilities.STRING_NONE);
        getJComboBoxAltIntegrityRate().setSelectedItem(CtiUtilities.STRING_NONE);
        getJComboBoxAltAccRate().setSelectedItem(CtiUtilities.STRING_NONE);

    }

    private void setHealthObjectsVisible(boolean value) {
        getPeriodicHealthCheckBox().setVisible(value);
        getPeriodicHealthIntervalComboBox().setVisible(value);
        getPeriodicHealthGroupComboBox().setVisible(value);
        getJComboBoxAltHealthChk().setVisible(value);

        getJLabelAltIntervalHlth().setVisible(value);
        getJLabelGroupHlth().setVisible(value);
        getJLabelIntervalHlth().setVisible(value);
    }

    private void setIntegrityObjectsVisible(boolean value) {
        getIntegrityRateCheckBox().setVisible(value);
        getIntegrityRateComboBox().setVisible(value);
        getIntegrityGroupComboBox().setVisible(value);
        getJComboBoxAltIntegrityRate().setVisible(value);

        getJLabelAltIntervalInt().setVisible(value);
        getJLabelGroupInt().setVisible(value);
        getJLabelIntervalInt().setVisible(value);
    }

    @Override
    public void setValue(Object val) {
        setDeviceType(((DeviceBase) val).getPaoType());

        HashMap<String, DeviceScanRate> scanRateMap = ((TwoWayDevice) val).getDeviceScanRateMap();

        if (val instanceof CCUBase || val instanceof TCUBase || val instanceof RepeaterBase || val instanceof TapTerminalBase || val instanceof IEDMeter || val instanceof RTCBase || val instanceof RTM || val instanceof CCU721) {
            DeviceScanRate scanRate = scanRateMap.get(DeviceScanRate.TYPE_STATUS);
            if (scanRate == null) {
                scanRate = scanRateMap.get(DeviceScanRate.TYPE_GENERAL);
            }

            if (scanRate != null) {
                getPeriodicHealthCheckBox().setSelected(true);
                getPeriodicHealthIntervalComboBox().setEnabled(true);
                getPeriodicHealthGroupComboBox().setEnabled(true);
                getJComboBoxAltHealthChk().setEnabled(true);

                getPeriodicHealthGroupComboBox().setSelectedIndex(scanRate.getScanGroup().intValue());

                SwingUtil.setIntervalComboBoxSelectedItem(getPeriodicHealthIntervalComboBox(), scanRate.getIntervalRate().intValue());

                if (scanRate.getIntervalRate().intValue() == scanRate.getAlternateRate().intValue()) {
                    getJComboBoxAltHealthChk().setSelectedItem(CtiUtilities.STRING_NONE);
                }  else {
                    SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxAltHealthChk(), scanRate.getAlternateRate().intValue());
                }
            }
        } else if ((val instanceof RTUBase) || (val instanceof MCTBase) || (val instanceof LCUBase) || (val instanceof CapBankController6510) || (val instanceof CapBankController702x) ||
                (val instanceof CapBankControllerDNP) || (val instanceof DNPBase) || (val instanceof Series5Base) || (val instanceof TwoWayLCR) || (val instanceof IonBase)) {

            DeviceScanRate statusRate = scanRateMap.get(DeviceScanRate.TYPE_EXCEPTION);
            if (statusRate == null) {
                statusRate = scanRateMap.get(DeviceScanRate.TYPE_STATUS);
            }

            if (statusRate != null) {
                getPeriodicHealthCheckBox().setSelected(true);
                getPeriodicHealthIntervalComboBox().setEnabled(true);
                getPeriodicHealthGroupComboBox().setEnabled(true);
                getJComboBoxAltHealthChk().setEnabled(true);

                getPeriodicHealthGroupComboBox().setSelectedIndex(statusRate.getScanGroup().intValue());
                SwingUtil.setIntervalComboBoxSelectedItem(getPeriodicHealthIntervalComboBox(), statusRate.getIntervalRate().intValue());

                if (statusRate.getIntervalRate().intValue() == statusRate.getAlternateRate().intValue()) {
                    getJComboBoxAltHealthChk().setSelectedItem(CtiUtilities.STRING_NONE);
                } else {
                    SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxAltHealthChk(), statusRate.getAlternateRate().intValue());
                }
            }

            DeviceScanRate accumRate = scanRateMap.get(DeviceScanRate.TYPE_ACCUMULATOR);
            if (accumRate != null) {
                getAccumulatorRateCheckBox().setSelected(true);
                getAccumulatorRateComboBox().setEnabled(true);
                getAccumulatorGroupComboBox().setEnabled(true);
                getJComboBoxAltAccRate().setEnabled(true);

                getAccumulatorGroupComboBox().setSelectedIndex(accumRate.getScanGroup().intValue());
                SwingUtil.setIntervalComboBoxSelectedItem(getAccumulatorRateComboBox(), accumRate.getIntervalRate().intValue());

                if (accumRate.getIntervalRate().intValue() == accumRate.getAlternateRate().intValue()) {
                    getJComboBoxAltAccRate().setSelectedItem(CtiUtilities.STRING_NONE);
                } else {
                    SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxAltAccRate(), accumRate.getAlternateRate().intValue());
                }
            }

            DeviceScanRate integrityRate = scanRateMap.get(DeviceScanRate.TYPE_INTEGRITY);
            if (integrityRate != null) {
                getIntegrityRateCheckBox().setSelected(true);
                getIntegrityRateComboBox().setEnabled(true);
                getIntegrityGroupComboBox().setEnabled(true);
                getJComboBoxAltIntegrityRate().setEnabled(true);

                getIntegrityGroupComboBox().setSelectedIndex(integrityRate.getScanGroup().intValue());

                SwingUtil.setIntervalComboBoxSelectedItem(getIntegrityRateComboBox(), integrityRate.getIntervalRate().intValue());

                if (integrityRate.getIntervalRate().intValue() == integrityRate.getAlternateRate().intValue()) {
                    getJComboBoxAltIntegrityRate().setSelectedItem(CtiUtilities.STRING_NONE);
                } else {
                    SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxAltIntegrityRate(), integrityRate.getAlternateRate().intValue());
                }
            }
        }

        DeviceWindow window = ((TwoWayDevice) val).getDeviceWindow();
        if (window.getWinOpen().intValue() != 0 || window.getWinClose().intValue() != 0) {
            getJCheckBoxScanWindow().doClick();
            java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
            cal.setTime(new java.util.Date());

            // get the time for the open window
            int time = window.getWinOpen().intValue();
            int hrs = time / 3600;
            cal.set(Calendar.HOUR_OF_DAY, hrs);
            cal.set(Calendar.MINUTE, (time - (hrs * 3600)) / 60);
            cal.set(Calendar.SECOND, (time - (hrs * 3600)) % 60);
            getJTextFieldOpen().setTimeText(cal.getTime());

            // get the time for the close window
            time = window.getWinClose().intValue();
            hrs = time / 3600;
            cal.set(Calendar.HOUR_OF_DAY, hrs);
            cal.set(Calendar.MINUTE, (time - (hrs * 3600)) / 60);
            cal.set(Calendar.SECOND, (time - (hrs * 3600)) % 60);
            getJTextFieldClose().setTimeText(cal.getTime());
        }

    }

    private void updateAltJComboBox(JComboBox<String> box_, JComboBox<String> altBox_) {
        Object selItem_ = box_.getSelectedItem();
        altBox_.removeAllItems();

        for (int i = 0; i < ALT_SELECTIONS.length; i++) {
            if (ALT_SELECTIONS[i].equals(selItem_)) {
                altBox_.addItem(CtiUtilities.STRING_NONE);
                altBox_.setSelectedIndex(i);
            } else
                altBox_.addItem(ALT_SELECTIONS[i]);
        }
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getIntegrityRateCheckBox().requestFocus();
            }
        });
    }
}
