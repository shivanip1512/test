package com.cannontech.dbeditor.wizard.device.lmprogram;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TimeComboJPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.database.db.device.lm.LMProgramControlWindow;

public class LMProgramControlWindowPanel extends DataInputPanel implements ActionListener {
    private JPanel ivjJPanelOptionalWindow1;
    private JPanel ivjJPanelOptionalWindow2;
    private TimeComboJPanel ivjTimeComboStart1;
    private TimeComboJPanel ivjTimeComboStart2;
    private TimeComboJPanel ivjTimeComboStop1;
    private TimeComboJPanel ivjTimeComboStop2;
    private JCheckBox ivjJCheckBoxUse1;
    private JCheckBox ivjJCheckBoxUse2;
    private JToggleButton ivjwindowChangePasser;
    private boolean hasStandardControlTimes = false;

    public LMProgramControlWindowPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == getTimeComboStart1()) {
            connEtoC1(e);
        }
        if (e.getSource() == getTimeComboStop1()) {
            connEtoC2(e);
        }
        if (e.getSource() == getTimeComboStart2()) {
            connEtoC3(e);
        }
        if (e.getSource() == getTimeComboStop2()) {
            connEtoC4(e);
        }
        if (e.getSource() == getJCheckBoxUse2()) {
            connEtoC5(e);
        }
        if (e.getSource() == getJCheckBoxUse1()) {
            connEtoC6(e);
        }

        if (e.getSource() == getWindowChangePasser()) {
            setTimedOperationalStateCondition(getWindowChangePasser().isSelected());

        }
    }

    private void connEtoC1(ActionEvent arg1) {
        try {

            this.fireInputUpdate();

        } catch (Throwable ivjExc) {

            handleException(ivjExc);
        }
    }

    private void connEtoC2(ActionEvent arg1) {
        try {

            this.fireInputUpdate();

        } catch (Throwable ivjExc) {

            handleException(ivjExc);
        }
    }

    private void connEtoC3(ActionEvent arg1) {
        try {

            this.fireInputUpdate();

        } catch (Throwable ivjExc) {

            handleException(ivjExc);
        }
    }

    private void connEtoC4(ActionEvent arg1) {
        try {

            this.fireInputUpdate();

        } catch (Throwable ivjExc) {

            handleException(ivjExc);
        }
    }

    private void connEtoC5(ActionEvent arg1) {
        try {
            this.jCheckBoxUse2_ActionPerformed(arg1);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connEtoC6(ActionEvent arg1) {
        try {
            this.jCheckBoxUse1_ActionPerformed(arg1);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JCheckBox getJCheckBoxUse1() {
        if (ivjJCheckBoxUse1 == null) {
            try {
                ivjJCheckBoxUse1 = new JCheckBox();
                ivjJCheckBoxUse1.setName("JCheckBoxUse1");
                ivjJCheckBoxUse1.setMnemonic('u');
                ivjJCheckBoxUse1.setText("Use Window 1");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxUse1;
    }

    private JCheckBox getJCheckBoxUse2() {
        if (ivjJCheckBoxUse2 == null) {
            try {
                ivjJCheckBoxUse2 = new JCheckBox();
                ivjJCheckBoxUse2.setName("JCheckBoxUse2");
                ivjJCheckBoxUse2.setMnemonic('u');
                ivjJCheckBoxUse2.setText("Use Window 2");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxUse2;
    }

    private JPanel getJPanelOptionalWindow1() {
        if (ivjJPanelOptionalWindow1 == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitleFont(new Font("Arial", 1, 14));
                ivjLocalBorder.setTitle("Optional Available For Control Window #1");
                ivjJPanelOptionalWindow1 = new JPanel();
                ivjJPanelOptionalWindow1.setName("JPanelOptionalWindow1");
                ivjJPanelOptionalWindow1.setBorder(ivjLocalBorder);
                ivjJPanelOptionalWindow1.setLayout(new GridBagLayout());

                GridBagConstraints constraintsTimeComboStart1 = new GridBagConstraints();
                constraintsTimeComboStart1.gridx = 1;
                constraintsTimeComboStart1.gridy = 2;
                constraintsTimeComboStart1.fill = GridBagConstraints.BOTH;
                constraintsTimeComboStart1.anchor = GridBagConstraints.WEST;
                constraintsTimeComboStart1.weightx = 1.0;
                constraintsTimeComboStart1.weighty = 1.0;
                constraintsTimeComboStart1.ipadx = 29;
                constraintsTimeComboStart1.ipady = -7;
                constraintsTimeComboStart1.insets = new Insets(1, 31, 3, 63);
                getJPanelOptionalWindow1().add(getTimeComboStart1(), constraintsTimeComboStart1);

                GridBagConstraints constraintsJCheckBoxUse1 = new GridBagConstraints();
                constraintsJCheckBoxUse1.gridx = 1;
                constraintsJCheckBoxUse1.gridy = 1;
                constraintsJCheckBoxUse1.anchor = GridBagConstraints.WEST;
                constraintsJCheckBoxUse1.ipadx = 73;
                constraintsJCheckBoxUse1.insets = new Insets(0, 13, 0, 131);
                getJPanelOptionalWindow1().add(getJCheckBoxUse1(), constraintsJCheckBoxUse1);

                GridBagConstraints constraintsTimeComboStop1 = new GridBagConstraints();
                constraintsTimeComboStop1.gridx = 1;
                constraintsTimeComboStop1.gridy = 3;
                constraintsTimeComboStop1.fill = GridBagConstraints.BOTH;
                constraintsTimeComboStop1.anchor = GridBagConstraints.WEST;
                constraintsTimeComboStop1.weightx = 1.0;
                constraintsTimeComboStop1.weighty = 1.0;
                constraintsTimeComboStop1.ipadx = 29;
                constraintsTimeComboStop1.ipady = -7;
                constraintsTimeComboStop1.insets = new Insets(4, 31, 9, 63);
                getJPanelOptionalWindow1().add(getTimeComboStop1(), constraintsTimeComboStop1);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelOptionalWindow1;
    }

    private JPanel getJPanelOptionalWindow2() {
        if (ivjJPanelOptionalWindow2 == null) {
            try {
                TitleBorder ivjLocalBorder3;
                ivjLocalBorder3 = new TitleBorder();
                ivjLocalBorder3.setTitleFont(new Font("Arial", 1, 14));
                ivjLocalBorder3.setTitle("Optional Available For Control Window #2");
                ivjJPanelOptionalWindow2 = new JPanel();
                ivjJPanelOptionalWindow2.setName("JPanelOptionalWindow2");
                ivjJPanelOptionalWindow2.setBorder(ivjLocalBorder3);
                ivjJPanelOptionalWindow2.setLayout(new GridBagLayout());

                GridBagConstraints constraintsTimeComboStop2 = new GridBagConstraints();
                constraintsTimeComboStop2.gridx = 1;
                constraintsTimeComboStop2.gridy = 3;
                constraintsTimeComboStop2.fill = GridBagConstraints.BOTH;
                constraintsTimeComboStop2.anchor = GridBagConstraints.WEST;
                constraintsTimeComboStop2.weightx = 1.0;
                constraintsTimeComboStop2.weighty = 1.0;
                constraintsTimeComboStop2.ipadx = 29;
                constraintsTimeComboStop2.ipady = -7;
                constraintsTimeComboStop2.insets = new Insets(4, 31, 8, 63);
                getJPanelOptionalWindow2().add(getTimeComboStop2(), constraintsTimeComboStop2);

                GridBagConstraints constraintsJCheckBoxUse2 = new GridBagConstraints();
                constraintsJCheckBoxUse2.gridx = 1;
                constraintsJCheckBoxUse2.gridy = 1;
                constraintsJCheckBoxUse2.anchor = GridBagConstraints.WEST;
                constraintsJCheckBoxUse2.ipadx = 73;
                constraintsJCheckBoxUse2.insets = new Insets(0, 15, 1, 129);
                getJPanelOptionalWindow2().add(getJCheckBoxUse2(), constraintsJCheckBoxUse2);

                GridBagConstraints constraintsTimeComboStart2 = new GridBagConstraints();
                constraintsTimeComboStart2.gridx = 1;
                constraintsTimeComboStart2.gridy = 2;
                constraintsTimeComboStart2.fill = GridBagConstraints.BOTH;
                constraintsTimeComboStart2.anchor = GridBagConstraints.WEST;
                constraintsTimeComboStart2.weightx = 1.0;
                constraintsTimeComboStart2.weighty = 1.0;
                constraintsTimeComboStart2.ipadx = 29;
                constraintsTimeComboStart2.ipady = -7;
                constraintsTimeComboStart2.insets = new Insets(1, 31, 3, 63);
                getJPanelOptionalWindow2().add(getTimeComboStart2(), constraintsTimeComboStart2);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelOptionalWindow2;
    }

    private TimeComboJPanel getTimeComboStart1() {
        if (ivjTimeComboStart1 == null) {
            try {
                TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new TitleBorder();
                ivjLocalBorder1.setTitle("Start");
                ivjTimeComboStart1 = new TimeComboJPanel();
                ivjTimeComboStart1.setName("TimeComboStart1");
                ivjTimeComboStart1.setBorder(ivjLocalBorder1);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTimeComboStart1;
    }

    private TimeComboJPanel getTimeComboStart2() {
        if (ivjTimeComboStart2 == null) {
            try {
                TitleBorder ivjLocalBorder5;
                ivjLocalBorder5 = new TitleBorder();
                ivjLocalBorder5.setTitle("Start");
                ivjTimeComboStart2 = new TimeComboJPanel();
                ivjTimeComboStart2.setName("TimeComboStart2");
                ivjTimeComboStart2.setBorder(ivjLocalBorder5);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTimeComboStart2;
    }

    private TimeComboJPanel getTimeComboStop1() {
        if (ivjTimeComboStop1 == null) {
            try {
                TitleBorder ivjLocalBorder2;
                ivjLocalBorder2 = new TitleBorder();
                ivjLocalBorder2.setTitle("Stop");
                ivjTimeComboStop1 = new TimeComboJPanel();
                ivjTimeComboStop1.setName("TimeComboStop1");
                ivjTimeComboStop1.setBorder(ivjLocalBorder2);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTimeComboStop1;
    }

    private TimeComboJPanel getTimeComboStop2() {
        if (ivjTimeComboStop2 == null) {
            try {
                TitleBorder ivjLocalBorder4;
                ivjLocalBorder4 = new TitleBorder();
                ivjLocalBorder4.setTitle("Stop");
                ivjTimeComboStop2 = new TimeComboJPanel();
                ivjTimeComboStop2.setName("TimeComboStop2");
                ivjTimeComboStop2.setBorder(ivjLocalBorder4);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTimeComboStop2;
    }

    @Override
    public Object getValue(Object o) {
        LMProgramBase program = (LMProgramBase) o;
        program.getLmProgramControlWindowVector().removeAllElements();

        LMProgramControlWindow window1 = new LMProgramControlWindow();
        LMProgramControlWindow window2 = new LMProgramControlWindow();

        if (getJCheckBoxUse1().isSelected()) {
            int startTime = getTimeComboStart1().getTimeInSeconds();
            int stopTime = getTimeComboStop1().getTimeInSeconds();

            if (stopTime < startTime) {
                // make sure server knows that this is the next day
                stopTime = stopTime + 86400;
            }
            window1.setAvailableStartTime(new Integer(startTime));
            window1.setAvailableStopTime(new Integer(stopTime));

            window1.setDeviceID(program.getPAObjectID());
            window1.setWindowNumber(new Integer(1));

            // add the window
            program.getLmProgramControlWindowVector().add(window1);
        }

        if (getJCheckBoxUse2().isSelected()) {
            int startTime = getTimeComboStart2().getTimeInSeconds();
            int stopTime = getTimeComboStop2().getTimeInSeconds();

            if (stopTime < startTime) {
                stopTime = stopTime + 86400;
            }

            window2.setAvailableStartTime(new Integer(startTime));
            window2.setAvailableStopTime(new Integer(stopTime));

            window2.setDeviceID(program.getPAObjectID());
            window2.setWindowNumber(new Integer(2));

            program.getLmProgramControlWindowVector().add(window2);
        }

        return o;
    }

    public JToggleButton getWindowChangePasser() {
        if (ivjwindowChangePasser == null) {
            try {
                ivjwindowChangePasser = new JToggleButton();
                ivjwindowChangePasser.setName("windowChangePasser");
                ivjwindowChangePasser.setText("");
                ivjwindowChangePasser.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjwindowChangePasser;
    }

    private void handleException(Throwable exception) {
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    private void initConnections() throws java.lang.Exception {
        getTimeComboStart1().addActionListener(this);
        getTimeComboStop1().addActionListener(this);
        getTimeComboStart2().addActionListener(this);
        getTimeComboStop2().addActionListener(this);
        getJCheckBoxUse2().addActionListener(this);
        getJCheckBoxUse1().addActionListener(this);
        getWindowChangePasser().addActionListener(this);
    }

    private void initialize() {
        try {
            setName("LMProgramControlWindowPanel");
            setLayout(new GridBagLayout());
            setSize(332, 333);

            GridBagConstraints constraintsJPanelOptionalWindow1 = new GridBagConstraints();
            constraintsJPanelOptionalWindow1.gridx = 1;
            constraintsJPanelOptionalWindow1.gridy = 1;
            constraintsJPanelOptionalWindow1.fill = GridBagConstraints.BOTH;
            constraintsJPanelOptionalWindow1.anchor = GridBagConstraints.WEST;
            constraintsJPanelOptionalWindow1.weightx = 1.0;
            constraintsJPanelOptionalWindow1.weighty = 1.0;
            constraintsJPanelOptionalWindow1.ipadx = -10;
            constraintsJPanelOptionalWindow1.ipady = -5;
            constraintsJPanelOptionalWindow1.insets = new Insets(6, 4, 3, 5);
            add(getJPanelOptionalWindow1(), constraintsJPanelOptionalWindow1);

            GridBagConstraints constraintsJPanelOptionalWindow2 = new GridBagConstraints();
            constraintsJPanelOptionalWindow2.gridx = 1;
            constraintsJPanelOptionalWindow2.gridy = 2;
            constraintsJPanelOptionalWindow2.fill = GridBagConstraints.BOTH;
            constraintsJPanelOptionalWindow2.anchor = GridBagConstraints.WEST;
            constraintsJPanelOptionalWindow2.weightx = 1.0;
            constraintsJPanelOptionalWindow2.weighty = 1.0;
            constraintsJPanelOptionalWindow2.ipadx = -10;
            constraintsJPanelOptionalWindow2.ipady = -5;
            constraintsJPanelOptionalWindow2.insets = new Insets(3, 4, 13, 5);
            add(getJPanelOptionalWindow2(), constraintsJPanelOptionalWindow2);

            GridBagConstraints constraintswindowChangePasser = new GridBagConstraints();
            constraintswindowChangePasser.gridx = 1;
            constraintswindowChangePasser.gridy = 2;
            constraintswindowChangePasser.insets = new Insets(4, 4, 4, 4);
            add(getWindowChangePasser(), constraintswindowChangePasser);

            initConnections();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }

        jCheckBoxUse1_ActionPerformed(null);
        jCheckBoxUse2_ActionPerformed(null);

    }

    @Override
    public boolean isInputValid() {
        if (getWindowChangePasser().isSelected()) {
            int start = 0, stop = 0;

            start = getTimeComboStart1().getTimeInSeconds();
            stop = getTimeComboStop1().getTimeInSeconds();

            if (start == 0 && stop == 0) {
                setErrorString("A timed program requires a non-zero control time to be specified under the Control Window tab.");
                return false;
            }
        }
        return true;
    }

    public void jCheckBoxUse1_ActionPerformed(ActionEvent actionEvent) {
        getTimeComboStart1().setEnabled(getJCheckBoxUse1().isSelected());
        getTimeComboStop1().setEnabled(getJCheckBoxUse1().isSelected());

        fireInputUpdate();
        return;
    }

    public void jCheckBoxUse2_ActionPerformed(ActionEvent actionEvent) {
        getTimeComboStart2().setEnabled(getJCheckBoxUse2().isSelected());
        getTimeComboStop2().setEnabled(getJCheckBoxUse2().isSelected());

        fireInputUpdate();
        return;
    }

    @Override
    public void setValue(Object o) {
        if (o != null) {
            LMProgramBase program = (LMProgramBase) o;

            for (int i = 0; i < program.getLmProgramControlWindowVector().size(); i++) {
                LMProgramControlWindow window = program.getLmProgramControlWindowVector().get(i);

                if (window.getWindowNumber().intValue() == 1) {
                    if (!getJCheckBoxUse1().isSelected()) {
                        getJCheckBoxUse1().doClick();
                    }
                    int startTime = window.getAvailableStartTime().intValue();
                    int stopTime = window.getAvailableStopTime().intValue();
                    if (stopTime > 86400) {
                        stopTime = stopTime - 86400;
                    }
                    getTimeComboStart1().setTimeInSeconds(startTime);
                    getTimeComboStop1().setTimeInSeconds(stopTime);
                    hasStandardControlTimes = true;
                }

                if (window.getWindowNumber().intValue() == 2) {
                    if (!getJCheckBoxUse2().isSelected()) {
                        getJCheckBoxUse2().doClick();
                    }
                    int startTime = window.getAvailableStartTime().intValue();
                    int stopTime = window.getAvailableStopTime().intValue();
                    if (stopTime > 86400) {
                        stopTime = stopTime - 86400;
                    }
                    getTimeComboStart2().setTimeInSeconds(startTime);
                    getTimeComboStop2().setTimeInSeconds(stopTime);
                    hasStandardControlTimes = true;
                }
            }
        }
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getJCheckBoxUse1().requestFocus();
            }
        });
    }

    public void setTimedOperationalStateCondition(boolean timedOrNot) {
        if (timedOrNot) {
            TitleBorder ivjLocalBorder;
            ivjLocalBorder = new TitleBorder();
            ivjLocalBorder.setTitleFont(new Font("Arial", 1, 14));
            ivjLocalBorder.setTitle("Control Times #1");
            getJPanelOptionalWindow1().setBorder(ivjLocalBorder);

            getJCheckBoxUse2().setText("Use 2nd Control Time");

            TitleBorder ivjLocalBorder2;
            ivjLocalBorder2 = new TitleBorder();
            ivjLocalBorder2.setTitleFont(new Font("Arial", 1, 14));
            ivjLocalBorder2.setTitle("Control Times #2");
            getJPanelOptionalWindow2().setBorder(ivjLocalBorder2);

            getJCheckBoxUse1().setSelected(timedOrNot);
            getTimeComboStart1().setEnabled(timedOrNot);
            getTimeComboStop1().setEnabled(timedOrNot);
            hasStandardControlTimes = false;

        } else {
            TitleBorder ivjLocalBorder;
            ivjLocalBorder = new TitleBorder();
            ivjLocalBorder.setTitleFont(new Font("Arial", 1, 14));
            ivjLocalBorder.setTitle("Optional Available For Control Window #1");
            getJPanelOptionalWindow1().setBorder(ivjLocalBorder);

            getJCheckBoxUse2().setText("Use Window 2");

            TitleBorder ivjLocalBorder2;
            ivjLocalBorder2 = new TitleBorder();
            ivjLocalBorder2.setTitleFont(new Font("Arial", 1, 14));
            ivjLocalBorder2.setTitle("Optional Available For Control Window #2");
            getJPanelOptionalWindow2().setBorder(ivjLocalBorder2);

            if (getJCheckBoxUse1().isSelected() && !hasStandardControlTimes) {
                getJCheckBoxUse1().doClick();
            }

            if (getJCheckBoxUse2().isSelected() && !hasStandardControlTimes) {
                getJCheckBoxUse2().doClick();
            }
        }

        getJCheckBoxUse1().setVisible(!timedOrNot);

    }

}
