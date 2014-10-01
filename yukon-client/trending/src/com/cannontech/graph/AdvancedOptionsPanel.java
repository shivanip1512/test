package com.cannontech.graph;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.graph.model.TrendProperties;

public class AdvancedOptionsPanel extends DataInputPanel implements ActionListener {
    private JDialog dialog;
    private TrendProperties trendProperties;
    private JButton ivjCancelButton;
    private JButton ivjOkButton;
    private JPanel ivjOkCancelButtonPanel;
    private Panel ivjAdvancedOptionsPanel;
    private int CANCEL = 0;
    private int OK = 1;
    private int buttonPushed = CANCEL;
    private JPanel ivjDomainAxisPanel;
    private JLabel ivjDomainLabel;
    private JLabel ivjDomainLabelLoadDuration;
    private JTextField ivjDomainLabelLoadDurationTextField;
    private JTextField ivjDomainLabelTextField;
    private JLabel ivjLeftRangeLabel;
    private JTextField ivjLeftRangeTextField;
    private JPanel ivjRangeAxisPanel;
    private JLabel ivjRightRangeLabel;
    private JTextField ivjRightRangeTextField;
    private JPanel ivjJPanel1;

    public AdvancedOptionsPanel() {
        initialize();
    }

    public AdvancedOptionsPanel(TrendProperties props) {
        initialize();
        setValue(props);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == getOkButton()) {
            setButtonPushed(OK);
            exit();
        } else if (event.getSource() == getCancelButton()) {
            setButtonPushed(CANCEL);
            exit();
        }
    }

    public void exit() {
        removeAll();
        setVisible(false);
        dialog.dispose();
    }

    private Panel getAdvancedOptionsPanel() {
        if (ivjAdvancedOptionsPanel == null) {
            try {
                ivjAdvancedOptionsPanel = new Panel();
                ivjAdvancedOptionsPanel.setName("AdvancedOptionsPanel");
                ivjAdvancedOptionsPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsDomainAxisPanel = new GridBagConstraints();
                constraintsDomainAxisPanel.gridx = 0;
                constraintsDomainAxisPanel.gridy = 0;
                constraintsDomainAxisPanel.fill = java.awt.GridBagConstraints.BOTH;
                constraintsDomainAxisPanel.weightx = 1.0;
                constraintsDomainAxisPanel.weighty = 1.0;
                constraintsDomainAxisPanel.insets = new Insets(5, 5, 5, 5);
                getAdvancedOptionsPanel().add(getDomainAxisPanel(), constraintsDomainAxisPanel);

                GridBagConstraints constraintsRangeAxisPanel = new GridBagConstraints();
                constraintsRangeAxisPanel.gridx = 0;
                constraintsRangeAxisPanel.gridy = 1;
                constraintsRangeAxisPanel.fill = java.awt.GridBagConstraints.BOTH;
                constraintsRangeAxisPanel.weightx = 1.0;
                constraintsRangeAxisPanel.weighty = 1.0;
                constraintsRangeAxisPanel.insets = new Insets(5, 5, 5, 5);
                getAdvancedOptionsPanel().add(getRangeAxisPanel(), constraintsRangeAxisPanel);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjAdvancedOptionsPanel;
    }

    private int getButtonPushed() {
        return buttonPushed;
    }

    public JButton getCancelButton() {
        if (ivjCancelButton == null) {
            try {
                ivjCancelButton = new JButton();
                ivjCancelButton.setName("CancelButton");
                ivjCancelButton.setText("Cancel");

                ivjCancelButton.addActionListener(this);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjCancelButton;
    }

    private JPanel getDomainAxisPanel() {
        if (ivjDomainAxisPanel == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitle("Domain Axis Properties");
                ivjDomainAxisPanel = new JPanel();
                ivjDomainAxisPanel.setName("DomainAxisPanel");
                ivjDomainAxisPanel.setBorder(ivjLocalBorder);
                ivjDomainAxisPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsDomainLabel = new GridBagConstraints();
                constraintsDomainLabel.gridx = 0;
                constraintsDomainLabel.gridy = 0;
                constraintsDomainLabel.anchor = java.awt.GridBagConstraints.EAST;
                constraintsDomainLabel.insets = new Insets(4, 4, 4, 4);
                getDomainAxisPanel().add(getDomainLabel(), constraintsDomainLabel);

                GridBagConstraints constraintsDomainLabelTextField = new GridBagConstraints();
                constraintsDomainLabelTextField.gridx = 1;
                constraintsDomainLabelTextField.gridy = 0;
                constraintsDomainLabelTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsDomainLabelTextField.weightx = 1.0;
                constraintsDomainLabelTextField.insets = new Insets(4, 4, 4, 4);
                getDomainAxisPanel().add(getDomainLabelTextField(), constraintsDomainLabelTextField);

                GridBagConstraints constraintsDomainLabelLoadDuration = new GridBagConstraints();
                constraintsDomainLabelLoadDuration.gridx = 0;
                constraintsDomainLabelLoadDuration.gridy = 1;
                constraintsDomainLabelLoadDuration.anchor = java.awt.GridBagConstraints.EAST;
                constraintsDomainLabelLoadDuration.insets = new Insets(4, 4, 4, 4);
                getDomainAxisPanel().add(getDomainLabelLoadDuration(), constraintsDomainLabelLoadDuration);

                GridBagConstraints constraintsDomainLabelLoadDurationTextField = new GridBagConstraints();
                constraintsDomainLabelLoadDurationTextField.gridx = 1;
                constraintsDomainLabelLoadDurationTextField.gridy = 1;
                constraintsDomainLabelLoadDurationTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsDomainLabelLoadDurationTextField.weightx = 1.0;
                constraintsDomainLabelLoadDurationTextField.insets = new Insets(4, 4, 4, 4);
                getDomainAxisPanel().add(getDomainLabelLoadDurationTextField(),
                    constraintsDomainLabelLoadDurationTextField);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjDomainAxisPanel;
    }

    private JLabel getDomainLabel() {
        if (ivjDomainLabel == null) {
            try {
                ivjDomainLabel = new JLabel();
                ivjDomainLabel.setName("DomainLabel");
                ivjDomainLabel.setText("Label:");
                ivjDomainLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjDomainLabel;
    }

    private JLabel getDomainLabelLoadDuration() {
        if (ivjDomainLabelLoadDuration == null) {
            try {
                ivjDomainLabelLoadDuration = new JLabel();
                ivjDomainLabelLoadDuration.setName("DomainLabelLoadDuration");
                ivjDomainLabelLoadDuration.setText("Load Duration Label:");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjDomainLabelLoadDuration;
    }

    private JTextField getDomainLabelLoadDurationTextField() {
        if (ivjDomainLabelLoadDurationTextField == null) {
            try {
                ivjDomainLabelLoadDurationTextField = new JTextField();
                ivjDomainLabelLoadDurationTextField.setName("DomainLabelLoadDurationTextField");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjDomainLabelLoadDurationTextField;
    }

    private JTextField getDomainLabelTextField() {
        if (ivjDomainLabelTextField == null) {
            try {
                ivjDomainLabelTextField = new JTextField();
                ivjDomainLabelTextField.setName("DomainLabelTextField");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjDomainLabelTextField;
    }

    private JPanel getJPanel1() {
        if (ivjJPanel1 == null) {
            try {
                ivjJPanel1 = new JPanel();
                ivjJPanel1.setName("JPanel1");
                ivjJPanel1.setLayout(new GridBagLayout());

                GridBagConstraints constraintsAdvancedOptionsPanel = new GridBagConstraints();
                constraintsAdvancedOptionsPanel.gridx = 0;
                constraintsAdvancedOptionsPanel.gridy = 0;
                constraintsAdvancedOptionsPanel.fill = java.awt.GridBagConstraints.BOTH;
                constraintsAdvancedOptionsPanel.weightx = 1.0;
                constraintsAdvancedOptionsPanel.weighty = 1.0;
                constraintsAdvancedOptionsPanel.insets = new Insets(4, 4, 4, 4);
                getJPanel1().add(getAdvancedOptionsPanel(), constraintsAdvancedOptionsPanel);

                GridBagConstraints constraintsOkCancelButtonPanel = new GridBagConstraints();
                constraintsOkCancelButtonPanel.gridx = 0;
                constraintsOkCancelButtonPanel.gridy = 1;
                constraintsOkCancelButtonPanel.fill = java.awt.GridBagConstraints.BOTH;
                constraintsOkCancelButtonPanel.anchor = java.awt.GridBagConstraints.SOUTH;
                constraintsOkCancelButtonPanel.weightx = 1.0;
                constraintsOkCancelButtonPanel.weighty = 1.0;
                constraintsOkCancelButtonPanel.insets = new Insets(5, 5, 5, 5);
                getJPanel1().add(getOkCancelButtonPanel(), constraintsOkCancelButtonPanel);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjJPanel1;
    }

    private JLabel getLeftRangeLabel() {
        if (ivjLeftRangeLabel == null) {
            try {
                ivjLeftRangeLabel = new JLabel();
                ivjLeftRangeLabel.setName("LeftRangeLabel");
                ivjLeftRangeLabel.setText("Left Label:");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjLeftRangeLabel;
    }

    private JTextField getLeftRangeTextField() {
        if (ivjLeftRangeTextField == null) {
            try {
                ivjLeftRangeTextField = new JTextField();
                ivjLeftRangeTextField.setName("LeftRangeTextField");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjLeftRangeTextField;
    }

    public JButton getOkButton() {
        if (ivjOkButton == null) {
            try {
                ivjOkButton = new JButton();
                ivjOkButton.setName("OkButton");
                ivjOkButton.setPreferredSize(new Dimension(73, 25));
                ivjOkButton.setText("OK");
                ivjOkButton.setMaximumSize(new Dimension(73, 25));
                ivjOkButton.setMinimumSize(new Dimension(73, 25));

                ivjOkButton.addActionListener(this);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjOkButton;
    }

    private JPanel getOkCancelButtonPanel() {
        if (ivjOkCancelButtonPanel == null) {
            try {
                ivjOkCancelButtonPanel = new JPanel();
                ivjOkCancelButtonPanel.setName("OkCancelButtonPanel");
                ivjOkCancelButtonPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsCancelButton = new GridBagConstraints();
                constraintsCancelButton.gridx = 1;
                constraintsCancelButton.gridy = 0;
                constraintsCancelButton.insets = new Insets(10, 20, 10, 20);
                getOkCancelButtonPanel().add(getCancelButton(), constraintsCancelButton);

                GridBagConstraints constraintsOkButton = new GridBagConstraints();
                constraintsOkButton.gridx = 0;
                constraintsOkButton.gridy = 0;
                constraintsOkButton.insets = new Insets(10, 20, 10, 20);
                getOkCancelButtonPanel().add(getOkButton(), constraintsOkButton);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjOkCancelButtonPanel;
    }

    private JPanel getRangeAxisPanel() {
        if (ivjRangeAxisPanel == null) {
            try {
                TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new TitleBorder();
                ivjLocalBorder1.setTitle("Range Axis Properties");
                ivjRangeAxisPanel = new JPanel();
                ivjRangeAxisPanel.setName("RangeAxisPanel");
                ivjRangeAxisPanel.setBorder(ivjLocalBorder1);
                ivjRangeAxisPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsLeftRangeLabel = new GridBagConstraints();
                constraintsLeftRangeLabel.gridx = 0;
                constraintsLeftRangeLabel.gridy = 0;
                constraintsLeftRangeLabel.anchor = java.awt.GridBagConstraints.EAST;
                constraintsLeftRangeLabel.insets = new Insets(5, 5, 5, 5);
                getRangeAxisPanel().add(getLeftRangeLabel(), constraintsLeftRangeLabel);

                GridBagConstraints constraintsRightRangeLabel = new GridBagConstraints();
                constraintsRightRangeLabel.gridx = 0;
                constraintsRightRangeLabel.gridy = 1;
                constraintsRightRangeLabel.anchor = java.awt.GridBagConstraints.EAST;
                constraintsRightRangeLabel.insets = new Insets(5, 5, 5, 5);
                getRangeAxisPanel().add(getRightRangeLabel(), constraintsRightRangeLabel);

                GridBagConstraints constraintsLeftRangeTextField = new GridBagConstraints();
                constraintsLeftRangeTextField.gridx = 1;
                constraintsLeftRangeTextField.gridy = 0;
                constraintsLeftRangeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsLeftRangeTextField.weightx = 1.0;
                constraintsLeftRangeTextField.insets = new Insets(4, 4, 4, 4);
                getRangeAxisPanel().add(getLeftRangeTextField(), constraintsLeftRangeTextField);

                GridBagConstraints constraintsRightRangeTextField = new GridBagConstraints();
                constraintsRightRangeTextField.gridx = 1;
                constraintsRightRangeTextField.gridy = 1;
                constraintsRightRangeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsRightRangeTextField.weightx = 1.0;
                constraintsRightRangeTextField.insets = new Insets(4, 4, 4, 4);
                getRangeAxisPanel().add(getRightRangeTextField(), constraintsRightRangeTextField);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjRangeAxisPanel;
    }

    private JLabel getRightRangeLabel() {
        if (ivjRightRangeLabel == null) {
            try {
                ivjRightRangeLabel = new JLabel();
                ivjRightRangeLabel.setName("RightRangeLabel");
                ivjRightRangeLabel.setText("Right Label:");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjRightRangeLabel;
    }

    private JTextField getRightRangeTextField() {
        if (ivjRightRangeTextField == null) {
            try {
                ivjRightRangeTextField = new JTextField();
                ivjRightRangeTextField.setName("RightRangeTextField");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjRightRangeTextField;
    }

    /**
     * @see com.cannontech.common.gui.util.DataInputPanel#getValue(Object)
     */
    @Override
    public Object getValue(Object o) {
        trendProperties.setDomainLabel(getDomainLabelTextField().getText().toString());
        trendProperties.setDomainLabel_LD(getDomainLabelLoadDurationTextField().getText().toString());
        trendProperties.setRangeLabel(getLeftRangeTextField().getText().toString(), GraphDefines.PRIMARY_AXIS);
        trendProperties.setRangeLabel(getRightRangeTextField().getText().toString(), GraphDefines.SECONDARY_AXIS);

        trendProperties.writeDefaultsFile();
        return trendProperties;
    }

    private void handleException(Throwable exception) {

        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    private void initialize() {
        try {

            // set the app to start as close to the center as you can....
            // only works with small gui interfaces.
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation((int) (d.width * .3), (int) (d.height * .2));

            setName("AdvancedOptionsFrame");
            setLayout(new BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
            setSize(404, 283);
            setVisible(true);
            add(getJPanel1(), getJPanel1().getName());
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }

    }

    public static void main(String[] args) {
        try {
            AdvancedOptionsPanel aAdvancedOptionsPanel;
            aAdvancedOptionsPanel = new AdvancedOptionsPanel();
            aAdvancedOptionsPanel.showAdvancedOptions(new JFrame());
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            exception.printStackTrace(System.out);
        }
    }

    private void setButtonPushed(int buttonPushed) {
        this.buttonPushed = buttonPushed;
    }

    /**
     * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
     */
    @Override
    public void setValue(Object o) {
        if (o == null || !(o instanceof TrendProperties)) {
            return;
        }

        trendProperties = (TrendProperties) o;

        getDomainLabelTextField().setText(String.valueOf(trendProperties.getDomainLabel()));
        getDomainLabelLoadDurationTextField().setText(String.valueOf(trendProperties.getDomainLabel_LD()));
        getLeftRangeTextField().setText(String.valueOf(trendProperties.getRangeLabel(GraphDefines.PRIMARY_AXIS)));
        getRightRangeTextField().setText(String.valueOf(trendProperties.getRangeLabel(GraphDefines.SECONDARY_AXIS)));

    }

    /**
     * Show AdvancedOptionsPanel with a JDialog to control the closing time.
     * 
     * @param parent javax.swing.JFrame
     */
    public TrendProperties showAdvancedOptions(JFrame parent) {
        dialog = new JDialog(parent);
        dialog.setTitle("Trending Properties Advanced Options");
        dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setModal(true);
        dialog.setContentPane(getJPanel1());
        dialog.setModal(true);
        dialog.getContentPane().add(this);
        dialog.setSize(379, 283);

        // Add a keyListener to the Escape key.
        KeyStroke ks = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true);
        dialog.getRootPane().getInputMap().put(ks, "CloseAction");
        dialog.getRootPane().getActionMap().put("CloseAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setButtonPushed(CANCEL);
                exit();
            }
        });

        // Add a window closeing event, even though I think it's already handled by
        // setDefaultCloseOperation(..)
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setButtonPushed(CANCEL);
                exit();
            };
        });

        dialog.show();
        if (getButtonPushed() == this.OK) {
            return (TrendProperties) getValue(null);
        }
        return null;
    }
}
