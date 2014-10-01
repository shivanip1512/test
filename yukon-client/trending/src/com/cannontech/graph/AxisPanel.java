package com.cannontech.graph;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.TitleBorder;

public class AxisPanel extends JPanel implements ItemListener {
    private JPanel ivjLeftAxisPanel;
    private JPanel ivjRightAxisPanel;
    private JLabel ivjLeftMaxLabel;
    private JLabel ivjLeftMinLabel;
    private JLabel ivjRightMaxLabel;
    private JLabel ivjRightMinLabel;
    private JTextField ivjLeftMaxTextField;
    private JTextField ivjLeftMinTextField;
    private JTextField ivjRightMaxTextField;
    private JTextField ivjRightMinTextField;
    private JCheckBox ivjLeftAutoScalingCheckBox;
    private JCheckBox ivjRightAutoScalingCheckBox;

    /**
     * AxisPanel constructor comment.
     */
    public AxisPanel() {
        super();
        initialize();
    }

    /**
     * Return the JCheckBox1 property value.
     * 
     * @return javax.swing.JCheckBox
     */
    public JCheckBox getLeftAutoScalingCheckBox() {
        if (ivjLeftAutoScalingCheckBox == null) {
            try {
                ivjLeftAutoScalingCheckBox = new JCheckBox();
                ivjLeftAutoScalingCheckBox.setName("LeftAutoScalingCheckBox");
                ivjLeftAutoScalingCheckBox.setSelected(true);
                ivjLeftAutoScalingCheckBox.setText("Auto Scaling");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLeftAutoScalingCheckBox;
    }

    /**
     * Return the LeftAxisPanel property value.
     * 
     * @return javax.swing.JPanel
     */

    private JPanel getLeftAxisPanel() {
        if (ivjLeftAxisPanel == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitle("Left Axis");
                ivjLeftAxisPanel = new JPanel();
                ivjLeftAxisPanel.setName("LeftAxisPanel");
                ivjLeftAxisPanel.setBorder(ivjLocalBorder);
                ivjLeftAxisPanel.setLayout(new GridBagLayout());
                ivjLeftAxisPanel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
                ivjLeftAxisPanel.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);

                GridBagConstraints constraintsLeftAutoScalingCheckBox = new GridBagConstraints();
                constraintsLeftAutoScalingCheckBox.gridx = 0;
                constraintsLeftAutoScalingCheckBox.gridy = 0;
                constraintsLeftAutoScalingCheckBox.gridwidth = 2;
                constraintsLeftAutoScalingCheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsLeftAutoScalingCheckBox.insets = new Insets(4, 4, 4, 4);
                getLeftAxisPanel().add(getLeftAutoScalingCheckBox(), constraintsLeftAutoScalingCheckBox);

                GridBagConstraints constraintsLeftMinLabel = new GridBagConstraints();
                constraintsLeftMinLabel.gridx = 0;
                constraintsLeftMinLabel.gridy = 1;
                constraintsLeftMinLabel.insets = new Insets(4, 4, 4, 4);
                getLeftAxisPanel().add(getLeftMinLabel(), constraintsLeftMinLabel);

                GridBagConstraints constraintsLeftMaxLabel = new GridBagConstraints();
                constraintsLeftMaxLabel.gridx = 0;
                constraintsLeftMaxLabel.gridy = 2;
                constraintsLeftMaxLabel.insets = new Insets(4, 4, 4, 4);
                getLeftAxisPanel().add(getLeftMaxLabel(), constraintsLeftMaxLabel);

                GridBagConstraints constraintsLeftMinTextField = new GridBagConstraints();
                constraintsLeftMinTextField.gridx = 1;
                constraintsLeftMinTextField.gridy = 1;
                constraintsLeftMinTextField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsLeftMinTextField.weightx = 1.0;
                constraintsLeftMinTextField.insets = new Insets(4, 4, 4, 4);
                getLeftAxisPanel().add(getLeftMinTextField(), constraintsLeftMinTextField);

                GridBagConstraints constraintsLeftMaxTextField = new GridBagConstraints();
                constraintsLeftMaxTextField.gridx = 1;
                constraintsLeftMaxTextField.gridy = 2;
                constraintsLeftMaxTextField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsLeftMaxTextField.weightx = 1.0;
                constraintsLeftMaxTextField.insets = new Insets(4, 4, 4, 4);
                getLeftAxisPanel().add(getLeftMaxTextField(), constraintsLeftMaxTextField);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjLeftAxisPanel;
    }

    /**
     * Return the JLabel2 property value.
     * 
     * @return javax.swing.JLabel
     */

    private JLabel getLeftMaxLabel() {
        if (ivjLeftMaxLabel == null) {
            try {
                ivjLeftMaxLabel = new JLabel();
                ivjLeftMaxLabel.setName("LeftMaxLabel");
                ivjLeftMaxLabel.setText("Max:");
                ivjLeftMaxLabel.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLeftMaxLabel;
    }

    /**
     * Return the JTextField2 property value.
     * 
     * @return javax.swing.JTextField
     */
    public JTextField getLeftMaxTextField() {
        if (ivjLeftMaxTextField == null) {
            try {
                ivjLeftMaxTextField = new JTextField();
                ivjLeftMaxTextField.setName("LeftMaxTextField");
                ivjLeftMaxTextField.setPreferredSize(new Dimension(44, 20));
                ivjLeftMaxTextField.setMinimumSize(new Dimension(44, 20));
                ivjLeftMaxTextField.setText("100.0");
                ivjLeftMaxTextField.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLeftMaxTextField;
    }

    /**
     * Return the JLabel1 property value.
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getLeftMinLabel() {
        if (ivjLeftMinLabel == null) {
            try {
                ivjLeftMinLabel = new JLabel();
                ivjLeftMinLabel.setName("LeftMinLabel");
                ivjLeftMinLabel.setText("Min:");
                ivjLeftMinLabel.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLeftMinLabel;
    }

    public JTextField getLeftMinTextField() {
        if (ivjLeftMinTextField == null) {
            try {
                ivjLeftMinTextField = new JTextField();
                ivjLeftMinTextField.setName("LeftMinTextField");
                ivjLeftMinTextField.setPreferredSize(new Dimension(44, 20));
                ivjLeftMinTextField.setMinimumSize(new Dimension(44, 20));
                ivjLeftMinTextField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                ivjLeftMinTextField.setText("0.0");
                ivjLeftMinTextField.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLeftMinTextField;
    }

    /**
     * Return the JCheckBox2 property value.
     * 
     * @return javax.swing.JCheckBox
     */
    public JCheckBox getRightAutoScalingCheckBox() {
        if (ivjRightAutoScalingCheckBox == null) {
            try {
                ivjRightAutoScalingCheckBox = new JCheckBox();
                ivjRightAutoScalingCheckBox.setName("RightAutoScalingCheckBox");
                ivjRightAutoScalingCheckBox.setSelected(true);
                ivjRightAutoScalingCheckBox.setText("Auto Scaling");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRightAutoScalingCheckBox;
    }

    /**
     * Return the RightAxisPanel property value.
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRightAxisPanel() {
        if (ivjRightAxisPanel == null) {
            try {
                TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new TitleBorder();
                ivjLocalBorder1.setTitle("Right Axis");
                ivjRightAxisPanel = new JPanel();
                ivjRightAxisPanel.setName("RightAxisPanel");
                ivjRightAxisPanel.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
                ivjRightAxisPanel.setLayout(new GridBagLayout());
                ivjRightAxisPanel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
                ivjRightAxisPanel.setBorder(ivjLocalBorder1);

                GridBagConstraints constraintsRightAutoScalingCheckBox = new GridBagConstraints();
                constraintsRightAutoScalingCheckBox.gridx = 0;
                constraintsRightAutoScalingCheckBox.gridy = 0;
                constraintsRightAutoScalingCheckBox.gridwidth = 0;
                constraintsRightAutoScalingCheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRightAutoScalingCheckBox.insets = new Insets(4, 4, 4, 4);
                getRightAxisPanel().add(getRightAutoScalingCheckBox(), constraintsRightAutoScalingCheckBox);

                GridBagConstraints constraintsRightMinLabel = new GridBagConstraints();
                constraintsRightMinLabel.gridx = 0;
                constraintsRightMinLabel.gridy = 1;
                constraintsRightMinLabel.insets = new Insets(4, 4, 4, 4);
                getRightAxisPanel().add(getRightMinLabel(), constraintsRightMinLabel);

                GridBagConstraints constraintsRightMaxLabel = new GridBagConstraints();
                constraintsRightMaxLabel.gridx = 0;
                constraintsRightMaxLabel.gridy = 2;
                constraintsRightMaxLabel.insets = new Insets(4, 4, 4, 4);
                getRightAxisPanel().add(getRightMaxLabel(), constraintsRightMaxLabel);

                GridBagConstraints constraintsRightMinTextField = new GridBagConstraints();
                constraintsRightMinTextField.gridx = 1;
                constraintsRightMinTextField.gridy = 1;
                constraintsRightMinTextField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRightMinTextField.weightx = 1.0;
                constraintsRightMinTextField.insets = new Insets(4, 4, 4, 4);
                getRightAxisPanel().add(getRightMinTextField(), constraintsRightMinTextField);

                GridBagConstraints constraintsRightMaxTextField = new GridBagConstraints();
                constraintsRightMaxTextField.gridx = 1;
                constraintsRightMaxTextField.gridy = 2;
                constraintsRightMaxTextField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRightMaxTextField.weightx = 1.0;
                constraintsRightMaxTextField.insets = new Insets(4, 4, 4, 4);
                getRightAxisPanel().add(getRightMaxTextField(), constraintsRightMaxTextField);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjRightAxisPanel;
    }

    /**
     * Return the JLabel4 property value.
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getRightMaxLabel() {
        if (ivjRightMaxLabel == null) {
            try {
                ivjRightMaxLabel = new JLabel();
                ivjRightMaxLabel.setName("RightMaxLabel");
                ivjRightMaxLabel.setText("Max:");
                ivjRightMaxLabel.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRightMaxLabel;
    }

    /**
     * Return the JTextField4 property value.
     * 
     * @return javax.swing.JTextField
     */
    public JTextField getRightMaxTextField() {
        if (ivjRightMaxTextField == null) {
            try {
                ivjRightMaxTextField = new JTextField();
                ivjRightMaxTextField.setName("RightMaxTextField");
                ivjRightMaxTextField.setPreferredSize(new Dimension(44, 20));
                ivjRightMaxTextField.setMinimumSize(new Dimension(44, 20));
                ivjRightMaxTextField.setText("100.0");
                ivjRightMaxTextField.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRightMaxTextField;
    }

    /**
     * Return the JLabel3 property value.
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getRightMinLabel() {
        if (ivjRightMinLabel == null) {
            try {
                ivjRightMinLabel = new JLabel();
                ivjRightMinLabel.setName("RightMinLabel");
                ivjRightMinLabel.setText("Min:");
                ivjRightMinLabel.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRightMinLabel;
    }

    /**
     * Return the JTextField3 property value.
     * 
     * @return javax.swing.JTextField
     */
    public JTextField getRightMinTextField() {
        if (ivjRightMinTextField == null) {
            try {
                ivjRightMinTextField = new JTextField();
                ivjRightMinTextField.setName("RightMinTextField");
                ivjRightMinTextField.setPreferredSize(new Dimension(44, 20));
                ivjRightMinTextField.setMinimumSize(new Dimension(44, 20));
                ivjRightMinTextField.setText("0.0");
                ivjRightMinTextField.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRightMinTextField;
    }

    private void handleException(Throwable exception) {
        // does nothing
    }

    private void initConnections() throws java.lang.Exception {
        getLeftAutoScalingCheckBox().addItemListener(this);
        getRightAutoScalingCheckBox().addItemListener(this);
    }

    private void initialize() {
        try {

            setName("AxisPanel");
            setLayout(new GridLayout());
            setSize(387, 152);
            add(getLeftAxisPanel(), getLeftAxisPanel().getName());
            add(getRightAxisPanel(), getRightAxisPanel().getName());
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }

        try {
            initConnections();
        } catch (Exception e) {
            CTILogger.info(" Error initializing Event listeners");
            e.printStackTrace();
        }

    }

    /**
     * Insert the method's description here.
     * Creation date: (1/7/2002 11:05:58 AM)
     * 
     * @param event java.awt.event.ItemEvent
     */
    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == getLeftAutoScalingCheckBox()) {
            boolean enable = !getLeftAutoScalingCheckBox().isSelected();
            getLeftMinLabel().setEnabled(enable);
            getLeftMinTextField().setEnabled(enable);
            getLeftMaxLabel().setEnabled(enable);
            getLeftMaxTextField().setEnabled(enable);

        }

        else if (event.getSource() == getRightAutoScalingCheckBox()) {
            boolean enable = !getRightAutoScalingCheckBox().isSelected();
            getRightMinLabel().setEnabled(enable);
            getRightMinTextField().setEnabled(enable);
            getRightMaxLabel().setEnabled(enable);
            getRightMaxTextField().setEnabled(enable);
        }
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame();
            AxisPanel aAxisPanel;
            aAxisPanel = new AxisPanel();
            frame.setContentPane(aAxisPanel);
            frame.setSize(aAxisPanel.getSize());
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            frame.show();
            Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }
}
