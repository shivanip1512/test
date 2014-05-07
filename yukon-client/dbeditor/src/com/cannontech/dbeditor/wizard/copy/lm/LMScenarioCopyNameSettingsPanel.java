package com.cannontech.dbeditor.wizard.copy.lm;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.spring.YukonSpringHook;

public class LMScenarioCopyNameSettingsPanel extends DataInputPanel implements CaretListener {
    private JLabel ivjJLabelName = null;
    private JTextField ivjJTextFieldName = null;
    private IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private JCheckBox ivjJCheckBoxCopyLoadPrograms = null;

    private class IvjEventHandler implements ActionListener, CaretListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == getJCheckBoxCopyLoadPrograms()) {
                connEtoC1();
            }
        };

        @Override
        public void caretUpdate(CaretEvent e) {
            if (e.getSource() == getJTextFieldName()) {
                connEtoC2();
            }
        };
    };

    public LMScenarioCopyNameSettingsPanel() {
        initialize();
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (e.getSource() == getJTextFieldName()) {
            connEtoC2();
        }
    }

    private void connEtoC1() {
        try {
            fireInputUpdate();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connEtoC2() {
        try {
            fireInputUpdate();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JCheckBox getJCheckBoxCopyLoadPrograms() {
        if (ivjJCheckBoxCopyLoadPrograms == null) {
            try {
                ivjJCheckBoxCopyLoadPrograms = new JCheckBox();
                ivjJCheckBoxCopyLoadPrograms.setName("JCheckBoxCopyLoadPrograms");
                ivjJCheckBoxCopyLoadPrograms.setSelected(true);
                ivjJCheckBoxCopyLoadPrograms.setFont(new Font("dialog", 0, 14));
                ivjJCheckBoxCopyLoadPrograms.setText("Copy Load Programs");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxCopyLoadPrograms;
    }

    private JLabel getJLabelName() {
        if (ivjJLabelName == null) {
            try {
                ivjJLabelName = new JLabel();
                ivjJLabelName.setName("JLabelName");
                ivjJLabelName.setFont(new Font("dialog", 0, 14));
                ivjJLabelName.setText("New Name:");
                ivjJLabelName.setHorizontalAlignment(SwingConstants.LEFT);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelName;
    }

    private JTextField getJTextFieldName() {
        if (ivjJTextFieldName == null) {
            try {
                ivjJTextFieldName = new JTextField();
                ivjJTextFieldName.setName("JTextFieldName");
                ivjJTextFieldName.setToolTipText("Name of Scenario");

                ivjJTextFieldName.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
                    PaoUtils.ILLEGAL_NAME_CHARS));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldName;
    }

    @Override
    public Object getValue(Object obj) {
        LMScenario scenario = (LMScenario) obj;

        PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
        scenario.setScenarioID(paoDao.getNextPaoId());

        scenario.setScenarioName(getJTextFieldName().getText());

        if (!getJCheckBoxCopyLoadPrograms().isSelected()) {
            scenario.getAllThePrograms().removeAllElements();
        }

        return obj;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error(exception.getMessage(), exception);
    }

    private void initConnections() throws Exception {
        getJTextFieldName().addCaretListener(ivjEventHandler);
        getJCheckBoxCopyLoadPrograms().addActionListener(ivjEventHandler);
    }

    private void initialize() {
        try {
            setName("LMScenarioCopyNameSettingsPanel");
            setLayout(new GridBagLayout());
            setSize(369, 392);

            GridBagConstraints constraintsJLabelName = new GridBagConstraints();
            constraintsJLabelName.gridx = 0;
            constraintsJLabelName.gridy = 0;
            constraintsJLabelName.ipadx = 10;
            constraintsJLabelName.ipady = -3;
            constraintsJLabelName.insets = new Insets(55, 9, 14, 1);
            add(getJLabelName(), constraintsJLabelName);

            GridBagConstraints constraintsJTextFieldName = new GridBagConstraints();
            constraintsJTextFieldName.gridx = 1;
            constraintsJTextFieldName.gridy = 0;
            constraintsJTextFieldName.gridwidth = 3;
            constraintsJTextFieldName.fill = GridBagConstraints.HORIZONTAL;
            constraintsJTextFieldName.weightx = 1.0;
            constraintsJTextFieldName.ipadx = 260;
            constraintsJTextFieldName.insets = new Insets(55, 2, 10, 13);
            add(getJTextFieldName(), constraintsJTextFieldName);

            GridBagConstraints constraintsJCheckBoxCopyLoadScenarios = new GridBagConstraints();
            constraintsJCheckBoxCopyLoadScenarios.gridx = 0;
            constraintsJCheckBoxCopyLoadScenarios.gridy = 1;
            constraintsJCheckBoxCopyLoadScenarios.gridwidth = 4;
            constraintsJCheckBoxCopyLoadScenarios.ipadx = 18;
            constraintsJCheckBoxCopyLoadScenarios.ipady = -5;
            constraintsJCheckBoxCopyLoadScenarios.insets = new Insets(7, 9, 4, 197);
            add(getJCheckBoxCopyLoadPrograms(), constraintsJCheckBoxCopyLoadScenarios);
            initConnections();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public boolean isInputValid() {
        if (getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0) {
            setErrorString("The Name text field must be filled in");
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame();
            LMScenarioCopyNameSettingsPanel aLMScenarioBasePanel;
            aLMScenarioBasePanel = new LMScenarioCopyNameSettingsPanel();
            frame.setContentPane(aLMScenarioBasePanel);
            frame.setSize(aLMScenarioBasePanel.getSize());
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.setVisible(true);
            Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of DataInputPanel");
            CTILogger.error(exception.getMessage(), exception);
        }
    }

    @Override
    public void setValue(Object o) {
        LMScenario scenario = (LMScenario) o;

        getJTextFieldName().setText(scenario.getPAOName() + "(copy)");
    }
}
