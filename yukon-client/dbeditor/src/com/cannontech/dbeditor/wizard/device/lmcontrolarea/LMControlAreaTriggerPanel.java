package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.device.lm.LMControlArea;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;

public class LMControlAreaTriggerPanel extends DataInputPanel implements DataInputPanelListener, ActionListener,
        ItemListener {
    public static final int MAX_TRIGGER_COUNT = 2;

    private JButton ivjJButtonDeleteTrigger;
    private JButton ivjJButtonNewTrigger;
    private JComboBox<LMControlAreaTrigger> ivjJComboBoxTrigger;
    private JLabel ivjJLabelTrigger;
    private JPanel ivjJPanelCurrTrig;
    private LMControlAreaTriggerModifyPanel ivjLMControlAreaTriggerModifyPanel;
    private JPanel ivjJPanelButtons;

    public LMControlAreaTriggerPanel() {
        initialize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getJButtonDeleteTrigger()) {
            connEtoC1(e);
        }
        if (e.getSource() == getJButtonNewTrigger()) {
            connEtoC2(e);
        }
    }

    private void connEtoC1(ActionEvent arg1) {
        try {
            this.jButtonDeleteTrigger_ActionPerformed(arg1);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connEtoC2(ActionEvent arg1) {
        try {
            this.jButtonNewTrigger_ActionPerformed(arg1);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connEtoC5(ItemEvent arg1) {
        try {
            this.jComboBoxTrigger_ItemStateChanged(arg1);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JButton getJButtonDeleteTrigger() {
        if (ivjJButtonDeleteTrigger == null) {
            try {
                ivjJButtonDeleteTrigger = new JButton();
                ivjJButtonDeleteTrigger.setName("JButtonDeleteTrigger");
                ivjJButtonDeleteTrigger.setMnemonic('d');
                ivjJButtonDeleteTrigger.setText("Delete Trigger");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJButtonDeleteTrigger;
    }

    private JButton getJButtonNewTrigger() {
        if (ivjJButtonNewTrigger == null) {
            try {
                ivjJButtonNewTrigger = new JButton();
                ivjJButtonNewTrigger.setName("JButtonNewTrigger");
                ivjJButtonNewTrigger.setMnemonic('n');
                ivjJButtonNewTrigger.setText("New Trigger...");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJButtonNewTrigger;
    }

    private JComboBox<LMControlAreaTrigger> getJComboBoxTrigger() {
        if (ivjJComboBoxTrigger == null) {
            try {
                ivjJComboBoxTrigger = new JComboBox<>();
                ivjJComboBoxTrigger.setName("JComboBoxTrigger");
                ivjJComboBoxTrigger.setToolTipText("The trigger you want to view, modify or delete");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxTrigger;
    }

    private JLabel getJLabelTrigger() {
        if (ivjJLabelTrigger == null) {
            try {
                ivjJLabelTrigger = new JLabel();
                ivjJLabelTrigger.setName("JLabelTrigger");
                ivjJLabelTrigger.setFont(new Font("dialog", 0, 14));
                ivjJLabelTrigger.setText("Trigger:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelTrigger;
    }

    private JPanel getJPanelButtons() {
        if (ivjJPanelButtons == null) {
            try {
                ivjJPanelButtons = new JPanel();
                ivjJPanelButtons.setName("JPanelButtons");
                ivjJPanelButtons.setLayout(new GridBagLayout());

                GridBagConstraints constraintsJButtonNewTrigger = new GridBagConstraints();
                constraintsJButtonNewTrigger.gridx = 1;
                constraintsJButtonNewTrigger.gridy = 1;
                constraintsJButtonNewTrigger.anchor = GridBagConstraints.WEST;
                constraintsJButtonNewTrigger.ipadx = 3;
                constraintsJButtonNewTrigger.insets = new Insets(2, 4, 5, 6);
                getJPanelButtons().add(getJButtonNewTrigger(), constraintsJButtonNewTrigger);

                GridBagConstraints constraintsJButtonDeleteTrigger = new GridBagConstraints();
                constraintsJButtonDeleteTrigger.gridx = 2;
                constraintsJButtonDeleteTrigger.gridy = 1;
                constraintsJButtonDeleteTrigger.anchor = GridBagConstraints.WEST;
                constraintsJButtonDeleteTrigger.ipadx = 1;
                constraintsJButtonDeleteTrigger.insets = new Insets(2, 6, 5, 69);
                getJPanelButtons().add(getJButtonDeleteTrigger(), constraintsJButtonDeleteTrigger);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelButtons;
    }

    private JPanel getJPanelCurrTrig() {
        if (ivjJPanelCurrTrig == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitleFont(new Font("dialog", 0, 14));
                ivjLocalBorder.setTitle("Current Trigger");
                ivjJPanelCurrTrig = new JPanel();
                ivjJPanelCurrTrig.setName("JPanelCurrTrig");
                ivjJPanelCurrTrig.setBorder(ivjLocalBorder);
                ivjJPanelCurrTrig.setLayout(new GridBagLayout());

                GridBagConstraints constraintsJLabelTrigger = new GridBagConstraints();
                constraintsJLabelTrigger.gridx = 1;
                constraintsJLabelTrigger.gridy = 1;
                constraintsJLabelTrigger.anchor = GridBagConstraints.WEST;
                constraintsJLabelTrigger.ipadx = 10;
                constraintsJLabelTrigger.insets = new Insets(2, 5, 4, 1);
                getJPanelCurrTrig().add(getJLabelTrigger(), constraintsJLabelTrigger);

                GridBagConstraints constraintsJComboBoxTrigger = new GridBagConstraints();
                constraintsJComboBoxTrigger.gridx = 2;
                constraintsJComboBoxTrigger.gridy = 1;
                constraintsJComboBoxTrigger.fill = GridBagConstraints.HORIZONTAL;
                constraintsJComboBoxTrigger.anchor = GridBagConstraints.WEST;
                constraintsJComboBoxTrigger.weightx = 1.0;
                constraintsJComboBoxTrigger.ipadx = 105;
                constraintsJComboBoxTrigger.insets = new Insets(2, 1, 1, 20);
                getJPanelCurrTrig().add(getJComboBoxTrigger(), constraintsJComboBoxTrigger);

                GridBagConstraints constraintsLMControlAreaTriggerModifyPanel = new GridBagConstraints();
                constraintsLMControlAreaTriggerModifyPanel.gridx = 1;
                constraintsLMControlAreaTriggerModifyPanel.gridy = 2;
                constraintsLMControlAreaTriggerModifyPanel.gridwidth = 2;
                constraintsLMControlAreaTriggerModifyPanel.fill = GridBagConstraints.BOTH;
                constraintsLMControlAreaTriggerModifyPanel.anchor = GridBagConstraints.WEST;
                constraintsLMControlAreaTriggerModifyPanel.weightx = 1.0;
                constraintsLMControlAreaTriggerModifyPanel.weighty = 1.0;
                constraintsLMControlAreaTriggerModifyPanel.ipadx = 295;
                constraintsLMControlAreaTriggerModifyPanel.ipady = 233;
                constraintsLMControlAreaTriggerModifyPanel.insets = new Insets(2, 5, 3, 8);
                getJPanelCurrTrig().add(getLMControlAreaTriggerModifyPanel(),
                    constraintsLMControlAreaTriggerModifyPanel);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelCurrTrig;
    }

    private LMControlAreaTriggerModifyPanel getLMControlAreaTriggerModifyPanel() {
        if (ivjLMControlAreaTriggerModifyPanel == null) {
            try {
                ivjLMControlAreaTriggerModifyPanel = new LMControlAreaTriggerModifyPanel();
                ivjLMControlAreaTriggerModifyPanel.setName("LMControlAreaTriggerModifyPanel");

                ivjLMControlAreaTriggerModifyPanel.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLMControlAreaTriggerModifyPanel;
    }

    @Override
    public Object getValue(Object obj) {
        // this stores any changes that are made to the current trigger
        if (getJComboBoxTrigger().getSelectedItem() != null) {
            getLMControlAreaTriggerModifyPanel().getValue(getJComboBoxTrigger().getSelectedItem());
        }

        LMControlArea controlArea = (LMControlArea) obj;

        // just in case we are editing a LMControlArea with existing triggers
        controlArea.getLmControlAreaTriggerVector().removeAllElements();

        for (int i = 0; i < getJComboBoxTrigger().getItemCount(); i++) {
            LMControlAreaTrigger trigger = getJComboBoxTrigger().getItemAt(i);

            trigger.setTriggerNumber(new Integer(i + 1)); // trigers start at 1
            trigger.setDeviceID(controlArea.getPAObjectID());

            controlArea.getLmControlAreaTriggerVector().addElement(trigger);
        }

        return obj;
    }

    private void handleException(Throwable exception) {
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error(exception.getMessage(), exception);
    }

    private void initConnections() throws Exception {
        getLMControlAreaTriggerModifyPanel().addDataInputPanelListener(this);

        getJButtonDeleteTrigger().addActionListener(this);
        getJButtonNewTrigger().addActionListener(this);
        getJComboBoxTrigger().addItemListener(this);
    }

    private void initialize() {
        try {
            setName("LMControlAreaTriggerPanel");
            setToolTipText("");
            setLayout(new GridBagLayout());
            setSize(338, 348);

            GridBagConstraints constraintsJPanelCurrTrig = new GridBagConstraints();
            constraintsJPanelCurrTrig.gridx = 1;
            constraintsJPanelCurrTrig.gridy = 1;
            constraintsJPanelCurrTrig.fill = GridBagConstraints.BOTH;
            constraintsJPanelCurrTrig.anchor = GridBagConstraints.WEST;
            constraintsJPanelCurrTrig.weightx = 1.0;
            constraintsJPanelCurrTrig.weighty = 1.0;
            constraintsJPanelCurrTrig.ipadx = -4;
            constraintsJPanelCurrTrig.ipady = -4;
            constraintsJPanelCurrTrig.insets = new Insets(5, 5, 1, 9);
            add(getJPanelCurrTrig(), constraintsJPanelCurrTrig);

            GridBagConstraints constraintsJPanelButtons = new GridBagConstraints();
            constraintsJPanelButtons.gridx = 1;
            constraintsJPanelButtons.gridy = 2;
            constraintsJPanelButtons.fill = GridBagConstraints.HORIZONTAL;
            constraintsJPanelButtons.anchor = GridBagConstraints.WEST;
            constraintsJPanelButtons.ipadx = 3;
            constraintsJPanelButtons.ipady = 4;
            constraintsJPanelButtons.insets = new Insets(1, 5, 6, 13);
            add(getJPanelButtons(), constraintsJPanelButtons);
            initConnections();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void inputUpdate(PropertyPanelEvent event) {
        fireInputUpdate();
    }

    @Override
    public boolean isInputValid() {
        if (getJComboBoxTrigger().getSelectedItem() != null && !getLMControlAreaTriggerModifyPanel().isInputValid()) {
            setErrorString(getLMControlAreaTriggerModifyPanel().getErrorString());
            return false;
        }

        return true;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == getJComboBoxTrigger()) {
            connEtoC5(e);
        }
    }

    public void jButtonDeleteTrigger_ActionPerformed(ActionEvent actionEvent) {
        if (getJComboBoxTrigger().getSelectedItem() != null) {
            getJComboBoxTrigger().removeItemAt(getJComboBoxTrigger().getSelectedIndex());

            fireInputUpdate();

            // we may need to hide the LMControlAreaTrigger panel
            getLMControlAreaTriggerModifyPanel().setVisible(getJComboBoxTrigger().getItemCount() > 0);
        } else {
            JOptionPane.showMessageDialog(this, "A trigger must be selected in the Trigger drop down box.",
                "Unable to Delete Trigger", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void jButtonNewTrigger_ActionPerformed(ActionEvent actionEvent) {
        // only allow the user to define at most MAX_TRIGGER_COUNT Triggers
        if (getJComboBoxTrigger().getItemCount() >= MAX_TRIGGER_COUNT) {
            JOptionPane.showMessageDialog(this, "A control area is only allowed " + MAX_TRIGGER_COUNT
                + " or less triggers to be defined for it.", "Trigger Limit Exceeded", JOptionPane.INFORMATION_MESSAGE);

            return;
        }

        LMControlAreaTriggerModifyPanel p = new LMControlAreaTriggerModifyPanel();
        OkCancelDialog d = new OkCancelDialog(SwingUtil.getParentFrame(this), "Trigger Creation", true, p);

        d.setSize((int) p.getSize().getWidth(), (int) p.getSize().getHeight());
        // d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);

        if (d.getButtonPressed() == d.OK_PRESSED) {
            LMControlAreaTrigger t = (LMControlAreaTrigger) p.getValue(null);
            getJComboBoxTrigger().addItem(t);
            getJComboBoxTrigger().setSelectedItem(t);

            fireInputUpdate();

            // we may need to show the LMControlAreaTrigger panel
            getLMControlAreaTriggerModifyPanel().setVisible(getJComboBoxTrigger().getItemCount() > 0);
        }

        d.dispose();
        return;
    }

    public void jComboBoxTrigger_ItemStateChanged(ItemEvent itemEvent) {
        if (itemEvent != null) {
            if (itemEvent.getStateChange() == itemEvent.DESELECTED && isInputValid()) {
                getLMControlAreaTriggerModifyPanel().getValue(itemEvent.getItem());
            }

            if (itemEvent.getStateChange() == itemEvent.SELECTED) {
                getLMControlAreaTriggerModifyPanel().setValue(itemEvent.getItem());
            }

            if (getJComboBoxTrigger().getSelectedItem() != null) {
                for (int i = 0; i < getJComboBoxTrigger().getItemCount(); i++) {
                    getJComboBoxTrigger().getItemAt(i).clearNames();
                }
            }
        }

        return;
    }

    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame();
            LMControlAreaTriggerPanel aLMControlAreaTriggerPanel;
            aLMControlAreaTriggerPanel = new LMControlAreaTriggerPanel();
            frame.setContentPane(aLMControlAreaTriggerPanel);
            frame.setSize(aLMControlAreaTriggerPanel.getSize());
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
    public void setValue(Object obj) {
        if (obj == null) {
            return;
        }

        LMControlArea controlArea = (LMControlArea) obj;

        for (int i = 0; i < controlArea.getLmControlAreaTriggerVector().size(); i++) {
            LMControlAreaTrigger trigger = controlArea.getLmControlAreaTriggerVector().elementAt(i);
            getJComboBoxTrigger().addItem(trigger);

        }

        if (getJComboBoxTrigger().getItemCount() > 0) {
            getJComboBoxTrigger().setSelectedIndex(0);
        }

        // we may need to show the LMControlAreaTrigger panel
        getLMControlAreaTriggerModifyPanel().setVisible(getJComboBoxTrigger().getItemCount() > 0);
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getJComboBoxTrigger().requestFocus();
            }
        });
    }
}
