package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 8:36:18 AM)
 * 
 * @author:
 */
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.math.NumberUtils;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupRenderer;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupTreeFactory;
import com.cannontech.common.device.groups.service.NonHiddenDeviceGroupPredicate;
import com.cannontech.common.gui.tree.CustomRenderJTree;
import com.cannontech.common.gui.util.DateComboBox;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class BillingFileFormatPanel extends JPanel implements ActionListener, FocusListener, Observer {
    private BillingFileDefaults billingFileDefaults;
    private JComboBox<String> ivjFileFormatComboBox;
    private JLabel ivjFileFormatLabel;
    private JPanel ivjFileFormatPanel;
    private JTree ivjGroupTree;
    private JScrollPane ivjGroupListScrollPane;
    private JPanel ivjGenerateFilePanel;
    private JLabel ivjOutputFileLabel;
    private JButton ivjOutputFileBrowseButton;
    private JTextField ivjDemandDaysPreviousTextBox;
    private JTextField ivjEnergyDaysPreviousTextBox;
    private JLabel ivjBillingEndDateLabel;
    private JLabel ivjDemandDaysPreviousLabel;
    private JLabel ivjEnergyDaysPreviousLabel;
    private JTextField ivjOutputFileTextField;
    private JToggleButton ivjGenerateFileToggleButton;
    private JLabel ivjTimerLabel;
    private JLabel ivjTimeElapsedLabel;
    private JLabel ivjDemandStartDateLabel;
    private JLabel ivjEnergyStartDateLabel;
    private Thread timerThread;
    private BillingFile billingFile;
    private SimpleDateFormat startDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private JCheckBox ivjIsAppendingCheckBox;
    private String inputFileText = "";
    private DateComboBox ivjDateComboBox;
    private JSeparator ivjSeparator;
    private JCheckBox ivjRemoveMultiplierCheckBox;

    public BillingFileFormatPanel() {
        initialize();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == getDateComboBox()) {
            getBillingDefaults().setEndDate(getDateComboBox().getSelectedDate());

            getDemandStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getDemandStartDate()));
            getEnergyStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getEnergyStartDate()));
        } else if (event.getSource() == getGenerateFileToggleButton()) {
            generateFile();
            getBillingDefaults().writeDefaultsFile();
            repaint();
        } else if (event.getSource() == getFileFormatComboBox()) {
            String formatSelected = (String) getFileFormatComboBox().getSelectedItem();
            getBillingDefaults().setFormatID(FileFormatTypes.getFormatID(formatSelected));
        } else if (event.getSource() == getOutputFileBrowseButton()) {
            String file = browseOutput();
            if (file != null) {
                getOutputFileTextField().setText(file);
                getBillingDefaults().setOutputFileDir(file);
            }
            repaint();
        } else if (event.getSource() == getDemandDaysPreviousTextBox()) {
            getBillingDefaults().setDemandDaysPrev(Integer.parseInt(getDemandDaysPreviousTextBox().getText()));
            getDemandStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getDemandStartDate()));
        } else if (event.getSource() == getEnergyDaysPreviousTextBox()) {
            getBillingDefaults().setEnergyDaysPrev(Integer.parseInt(getEnergyDaysPreviousTextBox().getText()));
            getEnergyStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getEnergyStartDate()));
        }
    }

    private String browseOutput() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(getBillingDefaults().getOutputFileDir()));
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getPath();
        }
        return null;
    }

    private void enableTimer(boolean enable) {
        if (enable) {
            getTimerLabel().setText("0 sec");
        }

        getTimeElapsedLabel().setEnabled(enable);
        getTimerLabel().setEnabled(enable);
    }

    @Override
    public void focusGained(FocusEvent event) {
        // Not used
    }

    @Override
    public void focusLost(FocusEvent event) {
        if (event.getSource() == getDemandDaysPreviousTextBox()) {
            getBillingDefaults().setDemandDaysPrev(Integer.parseInt(getDemandDaysPreviousTextBox().getText()));
            getDemandStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getDemandStartDate()));
        } else if (event.getSource() == getEnergyDaysPreviousTextBox()) {
            getBillingDefaults().setEnergyDaysPrev(Integer.parseInt(getEnergyDaysPreviousTextBox().getText()));
            getEnergyStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getEnergyStartDate()));
        }
    }

    private void generateFile() {
        if (getGenerateFileToggleButton().isSelected()) {
            getGenerateFileToggleButton().setText("Cancel Generation");
        } else {
            getGenerateFileToggleButton().setText("Generate File");

            // Interrupt billing file generation.
            if (getBillingFile().getSimpleBillingFormat() != null) {
                update(billingFile, "User canceled billing process");
            } // else //TODO - How to interrupt the BillingFormatter

            return;
        }

        // Gather new billing defaults and write them to the properties file.
        BillingFileDefaults defaults = retrieveBillingDefaultsFromGui();
        if (defaults == null) {
            return;
        }

        setBillingDefaults(defaults);
        try {
            setBillingFormatter(getBillingDefaults());
        } catch (Error e) {
            // We must be using a billing formatter instead.
        }

        if (getBillingFile().getSimpleBillingFormat() != null) {
            getBillingFile().addObserver(this);

            Thread billingThread = new Thread(getBillingFile(), "BillingFileThread");
            billingThread.setDaemon(true);

            enableTimer(true);

            CTILogger.info("Started " + FileFormatTypes.getFormatType(getBillingDefaults().getFormatID())
                + " format at: " + new Date());

            getTimerThread().start();

            billingThread.start();
        } else {
            JOptionPane.showMessageDialog(this.getParent().getComponent(0), getBillingDefaults().getFormatID()
                + " unrecognized file format id", "Yukon Billing File Generator", javax.swing.JOptionPane.ERROR_MESSAGE);
            CTILogger.info(getBillingDefaults().getFormatID() + " unrecognized file format id");
        }
    }

    public BillingFileDefaults getBillingDefaults() {
        if (billingFileDefaults == null) {
            billingFileDefaults = new BillingFileDefaults();
        }
        return billingFileDefaults;
    }

    private JLabel getBillingEndDateLabel() {
        if (ivjBillingEndDateLabel == null) {
            try {
                ivjBillingEndDateLabel = new JLabel();
                ivjBillingEndDateLabel.setName("BillingEndDateLabel");
                ivjBillingEndDateLabel.setToolTipText("");
                ivjBillingEndDateLabel.setFont(new Font("dialog", 0, 12));
                ivjBillingEndDateLabel.setText("Billing End Date:");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjBillingEndDateLabel;
    }

    private BillingFile getBillingFile() {
        return billingFile;
    }

    private DateComboBox getDateComboBox() {
        if (ivjDateComboBox == null) {
            try {
                ivjDateComboBox = new DateComboBox();
                ivjDateComboBox.setName("DateComboBox");

                ivjDateComboBox.addActionListener(this);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjDateComboBox;
    }

    private JLabel getDemandDaysPreviousLabel() {
        if (ivjDemandDaysPreviousLabel == null) {
            try {
                ivjDemandDaysPreviousLabel = new JLabel();
                ivjDemandDaysPreviousLabel.setName("DemandDaysPreviousLabel");
                ivjDemandDaysPreviousLabel.setFont(new Font("dialog", 0, 12));
                ivjDemandDaysPreviousLabel.setText("Demand Days Previous:");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjDemandDaysPreviousLabel;
    }

    private JTextField getDemandDaysPreviousTextBox() {
        if (ivjDemandDaysPreviousTextBox == null) {
            try {
                ivjDemandDaysPreviousTextBox = new JTextField();
                ivjDemandDaysPreviousTextBox.setName("DemandDaysPreviousTextBox");
                ivjDemandDaysPreviousTextBox.setToolTipText("Number of Days Previous To Search Demand Readings.");
                ivjDemandDaysPreviousTextBox.setMargin(new Insets(0, 0, 0, 0));

                // set default value
                ivjDemandDaysPreviousTextBox.setText(String.valueOf(getBillingDefaults().getDemandDaysPrev()));
                ivjDemandDaysPreviousTextBox.addActionListener(this);
                ivjDemandDaysPreviousTextBox.addFocusListener(this);
                // ivjDemandDaysPreviousTextBox.getDocument().addDocumentListener(this);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjDemandDaysPreviousTextBox;
    }

    private JLabel getDemandStartDateLabel() {
        if (ivjDemandStartDateLabel == null) {
            try {
                ivjDemandStartDateLabel = new JLabel();
                ivjDemandStartDateLabel.setName("DemandStartDateLabel");
                ivjDemandStartDateLabel.setFont(new Font("dialog", 0, 12));
                ivjDemandStartDateLabel.setText("mm/dd/yyyy");

                ivjDemandStartDateLabel.setText(startDateFormat.format(getBillingDefaults().getDemandStartDate()));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjDemandStartDateLabel;
    }

    private JLabel getEnergyDaysPreviousLabel() {
        if (ivjEnergyDaysPreviousLabel == null) {
            try {
                ivjEnergyDaysPreviousLabel = new JLabel();
                ivjEnergyDaysPreviousLabel.setName("EnergyDaysPreviousLabel");
                ivjEnergyDaysPreviousLabel.setFont(new Font("dialog", 0, 12));
                ivjEnergyDaysPreviousLabel.setText("Energy Days Previous:");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjEnergyDaysPreviousLabel;
    }

    /**
     * Return the Energ property value.
     * 
     * @return javax.swing.JTextField
     */

    private JTextField getEnergyDaysPreviousTextBox() {
        if (ivjEnergyDaysPreviousTextBox == null) {
            try {
                ivjEnergyDaysPreviousTextBox = new JTextField();
                ivjEnergyDaysPreviousTextBox.setName("EnergyDaysPreviousTextBox");
                ivjEnergyDaysPreviousTextBox.setToolTipText("Number of Days Previous To Search Energy Readings.");

                // set default value
                ivjEnergyDaysPreviousTextBox.setText(String.valueOf(getBillingDefaults().getEnergyDaysPrev()));
                ivjEnergyDaysPreviousTextBox.addActionListener(this);
                ivjEnergyDaysPreviousTextBox.addFocusListener(this);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjEnergyDaysPreviousTextBox;
    }

    /**
     * Return the EnergyStartDateLabel property value.
     * 
     * @return javax.swing.JLabel
     */

    private JLabel getEnergyStartDateLabel() {
        if (ivjEnergyStartDateLabel == null) {
            try {
                ivjEnergyStartDateLabel = new JLabel();
                ivjEnergyStartDateLabel.setName("EnergyStartDateLabel");
                ivjEnergyStartDateLabel.setFont(new Font("dialog", 0, 12));
                ivjEnergyStartDateLabel.setText("mm/dd/yyyy");

                ivjEnergyStartDateLabel.setText(startDateFormat.format(getBillingDefaults().getEnergyStartDate()));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjEnergyStartDateLabel;
    }

    private JComboBox<String> getFileFormatComboBox() {
        if (ivjFileFormatComboBox == null) {
            try {
                ivjFileFormatComboBox = new JComboBox<String>();
                ivjFileFormatComboBox.setName("FileFormatComboBox");
                ivjFileFormatComboBox.setToolTipText("Select Billing File Format.");

                String[] formats = FileFormatTypes.getValidFormatTypes();
                for (int i = 0; i < formats.length; i++) {
                    ivjFileFormatComboBox.addItem(formats[i]);
                }

                // set default value
                ivjFileFormatComboBox.setSelectedItem(FileFormatTypes.getFormatType(getBillingDefaults().getFormatID()));
                setBillingFormatter(getBillingDefaults());
                // enableComponents();

                ivjFileFormatComboBox.addActionListener(this);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjFileFormatComboBox;
    }

    private JLabel getFileFormatLabel() {
        if (ivjFileFormatLabel == null) {
            try {
                ivjFileFormatLabel = new JLabel();
                ivjFileFormatLabel.setName("FileFormatLabel");
                ivjFileFormatLabel.setFont(new Font("dialog", 0, 12));
                ivjFileFormatLabel.setText("File Format:");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjFileFormatLabel;
    }

    private JPanel getFileFormatPanel() {
        if (ivjFileFormatPanel == null) {
            try {
                ivjFileFormatPanel = new JPanel();
                ivjFileFormatPanel.setName("FileFormatPanel");
                ivjFileFormatPanel.setBorder(new TitleBorder());
                ivjFileFormatPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsFileFormatLabel = new GridBagConstraints();
                constraintsFileFormatLabel.gridx = 0;
                constraintsFileFormatLabel.gridy = 0;
                constraintsFileFormatLabel.anchor = java.awt.GridBagConstraints.EAST;
                constraintsFileFormatLabel.insets = new Insets(5, 5, 5, 5);
                getFileFormatPanel().add(getFileFormatLabel(), constraintsFileFormatLabel);

                GridBagConstraints constraintsFileFormatComboBox = new GridBagConstraints();
                constraintsFileFormatComboBox.gridx = 1;
                constraintsFileFormatComboBox.gridy = 0;
                constraintsFileFormatComboBox.gridwidth = 2;
                constraintsFileFormatComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsFileFormatComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsFileFormatComboBox.weightx = 1.0;
                constraintsFileFormatComboBox.insets = new Insets(5, 5, 5, 5);
                getFileFormatPanel().add(getFileFormatComboBox(), constraintsFileFormatComboBox);

                GridBagConstraints constraintsDemandDaysPreviousTextBox = new GridBagConstraints();
                constraintsDemandDaysPreviousTextBox.gridx = 1;
                constraintsDemandDaysPreviousTextBox.gridy = 2;
                constraintsDemandDaysPreviousTextBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsDemandDaysPreviousTextBox.ipadx = 75;
                constraintsDemandDaysPreviousTextBox.insets = new Insets(5, 5, 5, 5);
                getFileFormatPanel().add(getDemandDaysPreviousTextBox(), constraintsDemandDaysPreviousTextBox);

                GridBagConstraints constraintsDemandDaysPreviousLabel = new GridBagConstraints();
                constraintsDemandDaysPreviousLabel.gridx = 0;
                constraintsDemandDaysPreviousLabel.gridy = 2;
                constraintsDemandDaysPreviousLabel.anchor = java.awt.GridBagConstraints.EAST;
                constraintsDemandDaysPreviousLabel.insets = new Insets(5, 5, 5, 5);
                getFileFormatPanel().add(getDemandDaysPreviousLabel(), constraintsDemandDaysPreviousLabel);

                GridBagConstraints constraintsBillingEndDateLabel = new GridBagConstraints();
                constraintsBillingEndDateLabel.gridx = 0;
                constraintsBillingEndDateLabel.gridy = 1;
                constraintsBillingEndDateLabel.anchor = java.awt.GridBagConstraints.EAST;
                constraintsBillingEndDateLabel.insets = new Insets(5, 5, 5, 5);
                getFileFormatPanel().add(getBillingEndDateLabel(), constraintsBillingEndDateLabel);

                GridBagConstraints constraintsEnergyDaysPreviousLabel = new GridBagConstraints();
                constraintsEnergyDaysPreviousLabel.gridx = 0;
                constraintsEnergyDaysPreviousLabel.gridy = 3;
                constraintsEnergyDaysPreviousLabel.anchor = java.awt.GridBagConstraints.EAST;
                constraintsEnergyDaysPreviousLabel.insets = new Insets(5, 5, 5, 5);
                getFileFormatPanel().add(getEnergyDaysPreviousLabel(), constraintsEnergyDaysPreviousLabel);

                GridBagConstraints constraintsEnergyDaysPreviousTextBox = new GridBagConstraints();
                constraintsEnergyDaysPreviousTextBox.gridx = 1;
                constraintsEnergyDaysPreviousTextBox.gridy = 3;
                constraintsEnergyDaysPreviousTextBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsEnergyDaysPreviousTextBox.ipadx = 75;
                constraintsEnergyDaysPreviousTextBox.insets = new Insets(5, 5, 5, 5);
                getFileFormatPanel().add(getEnergyDaysPreviousTextBox(), constraintsEnergyDaysPreviousTextBox);

                GridBagConstraints constraintsDemandStartDateLabel = new GridBagConstraints();
                constraintsDemandStartDateLabel.gridx = 2;
                constraintsDemandStartDateLabel.gridy = 2;
                constraintsDemandStartDateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsDemandStartDateLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsDemandStartDateLabel.weightx = 1.0;
                constraintsDemandStartDateLabel.insets = new Insets(5, 5, 5, 5);
                getFileFormatPanel().add(getDemandStartDateLabel(), constraintsDemandStartDateLabel);

                GridBagConstraints constraintsEnergyStartDateLabel = new GridBagConstraints();
                constraintsEnergyStartDateLabel.gridx = 2;
                constraintsEnergyStartDateLabel.gridy = 3;
                constraintsEnergyStartDateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsEnergyStartDateLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsEnergyStartDateLabel.weightx = 1.0;
                constraintsEnergyStartDateLabel.insets = new Insets(5, 5, 5, 5);
                getFileFormatPanel().add(getEnergyStartDateLabel(), constraintsEnergyStartDateLabel);

                GridBagConstraints constraintsDateComboBox = new GridBagConstraints();
                constraintsDateComboBox.gridx = 1;
                constraintsDateComboBox.gridy = 1;
                constraintsDateComboBox.gridwidth = 2;
                constraintsDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsDateComboBox.weightx = 1.0;
                constraintsDateComboBox.insets = new Insets(5, 5, 5, 5);
                getFileFormatPanel().add(getDateComboBox(), constraintsDateComboBox);

                GridBagConstraints constraintsGroupListScrollPane = new GridBagConstraints();
                constraintsGroupListScrollPane.gridx = 0;
                constraintsGroupListScrollPane.gridy = 6;
                constraintsGroupListScrollPane.gridwidth = 3;
                constraintsGroupListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
                constraintsGroupListScrollPane.weightx = 1.0;
                constraintsGroupListScrollPane.weighty = 1.0;
                constraintsGroupListScrollPane.insets = new Insets(5, 5, 5, 5);
                getFileFormatPanel().add(getGroupListScrollPane(), constraintsGroupListScrollPane);

                GridBagConstraints constraintsSeparator = new GridBagConstraints();
                constraintsSeparator.gridx = 0;
                constraintsSeparator.gridy = 4;
                constraintsSeparator.gridwidth = 3;
                constraintsSeparator.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsSeparator.insets = new Insets(4, 4, 4, 4);
                getFileFormatPanel().add(getSeparator(), constraintsSeparator);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjFileFormatPanel;
    }

    private JPanel getGenerateFilePanel() {
        if (ivjGenerateFilePanel == null) {
            try {
                ivjGenerateFilePanel = new JPanel();
                ivjGenerateFilePanel.setName("GenerateFilePanel");
                ivjGenerateFilePanel.setBorder(new TitleBorder());
                ivjGenerateFilePanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsOutputFileTextField = new GridBagConstraints();
                constraintsOutputFileTextField.gridx = 0;
                constraintsOutputFileTextField.gridy = 1;
                constraintsOutputFileTextField.gridwidth = 2;
                constraintsOutputFileTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsOutputFileTextField.weightx = 1.0;
                constraintsOutputFileTextField.insets = new Insets(0, 5, 0, 5);
                getGenerateFilePanel().add(getOutputFileTextField(), constraintsOutputFileTextField);

                GridBagConstraints constraintsOutputFileBrowseButton = new GridBagConstraints();
                constraintsOutputFileBrowseButton.gridx = 2;
                constraintsOutputFileBrowseButton.gridy = 1;
                constraintsOutputFileBrowseButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsOutputFileBrowseButton.insets = new Insets(0, 5, 0, 5);
                getGenerateFilePanel().add(getOutputFileBrowseButton(), constraintsOutputFileBrowseButton);

                GridBagConstraints constraintsGenerateFileToggleButton = new GridBagConstraints();
                constraintsGenerateFileToggleButton.gridx = 1;
                constraintsGenerateFileToggleButton.gridy = 3;
                constraintsGenerateFileToggleButton.insets = new Insets(0, 5, 0, 5);
                getGenerateFilePanel().add(getGenerateFileToggleButton(), constraintsGenerateFileToggleButton);

                GridBagConstraints constraintsTimerLabel = new GridBagConstraints();
                constraintsTimerLabel.gridx = 2;
                constraintsTimerLabel.gridy = 3;
                constraintsTimerLabel.insets = new Insets(12, 5, 0, 5);
                getGenerateFilePanel().add(getTimerLabel(), constraintsTimerLabel);

                GridBagConstraints constraintsTimeElapsedLabel = new GridBagConstraints();
                constraintsTimeElapsedLabel.gridx = 2;
                constraintsTimeElapsedLabel.gridy = 3;
                constraintsTimeElapsedLabel.anchor = java.awt.GridBagConstraints.NORTH;
                constraintsTimeElapsedLabel.insets = new Insets(0, 5, 0, 5);
                getGenerateFilePanel().add(getTimeElapsedLabel(), constraintsTimeElapsedLabel);

                GridBagConstraints constraintsOutputFileLabel = new GridBagConstraints();
                constraintsOutputFileLabel.gridx = 0;
                constraintsOutputFileLabel.gridy = 0;
                constraintsOutputFileLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsOutputFileLabel.insets = new Insets(0, 5, 0, 5);
                getGenerateFilePanel().add(getOutputFileLabel(), constraintsOutputFileLabel);

                GridBagConstraints constraintsIsAppendingCheckBox = new GridBagConstraints();
                constraintsIsAppendingCheckBox.gridx = 0;
                constraintsIsAppendingCheckBox.gridy = 2;
                constraintsIsAppendingCheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsIsAppendingCheckBox.insets = new Insets(0, 5, 0, 5);
                getGenerateFilePanel().add(getIsAppendingCheckBox(), constraintsIsAppendingCheckBox);

                GridBagConstraints constraintsRemoveMultiplierCheckBox = new GridBagConstraints();
                constraintsRemoveMultiplierCheckBox.gridx = 0;
                constraintsRemoveMultiplierCheckBox.gridy = 3;
                constraintsRemoveMultiplierCheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRemoveMultiplierCheckBox.insets = new Insets(0, 5, 0, 5);
                getGenerateFilePanel().add(getRemoveMultiplierCheckBox(), constraintsRemoveMultiplierCheckBox);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjGenerateFilePanel;
    }

    private JToggleButton getGenerateFileToggleButton() {
        if (ivjGenerateFileToggleButton == null) {
            try {
                ivjGenerateFileToggleButton = new JToggleButton();
                ivjGenerateFileToggleButton.setName("GenerateFileToggleButton");
                ivjGenerateFileToggleButton.setText("Generate File");
                ivjGenerateFileToggleButton.setForeground(java.awt.Color.black);

                ivjGenerateFileToggleButton.addActionListener(this);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjGenerateFileToggleButton;
    }

    private JTree getGroupList() {
        if (ivjGroupTree == null) {
            DeviceGroupTreeFactory modelFactory =
                YukonSpringHook.getBean("deviceGroupTreeFactory", DeviceGroupTreeFactory.class);
            DeviceGroupService deviceGroupService =
                YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
            try {
                CustomRenderJTree customTree = new CustomRenderJTree();
                customTree.addRenderer(new DeviceGroupRenderer());
                ivjGroupTree = customTree;
                ivjGroupTree.setName("GroupList");
                ivjGroupTree.setBounds(0, 0, 160, 120);
                ivjGroupTree.setShowsRootHandles(true);
                ivjGroupTree.setRootVisible(false);

                TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
                selectionModel.clearSelection();
                selectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
                ivjGroupTree.setSelectionModel(selectionModel);
                TreeModel model = modelFactory.getModel(new NonHiddenDeviceGroupPredicate());
                ivjGroupTree.setModel(model);

                // Select one/multiple billing groups.
                List<String> selectedGroups = getBillingDefaults().getDeviceGroups();
                for (String groupName : selectedGroups) {
                    DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
                    TreePath pathForGroup = modelFactory.getPathForGroup((TreeNode) model.getRoot(), group);
                    selectionModel.addSelectionPath(pathForGroup);
                    ivjGroupTree.makeVisible(pathForGroup);
                }

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjGroupTree;
    }

    private JScrollPane getGroupListScrollPane() {
        if (ivjGroupListScrollPane == null) {
            try {
                ivjGroupListScrollPane = new JScrollPane();
                ivjGroupListScrollPane.setName("GroupListScrollPane");
                ivjGroupListScrollPane.setToolTipText("Select Billing Collection Group(s).");
                ivjGroupListScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                getGroupListScrollPane().setViewportView(getGroupList());

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjGroupListScrollPane;
    }

    private String getInputFileText() {
        if (inputFileText == "") {
            try {
                inputFileText = YukonSpringHook.getBean(GlobalSettingDao.class).getString(GlobalSettingType.INPUT_FILE);
            } catch (Exception e) {
                inputFileText = "C:\\yukon\\client\\config\\input.txt";
                CTILogger.info("[" + new Date()
                    + "]  Data Export Input File is not a valid global setting value, defaulted to " + inputFileText);
                CTILogger.info("["
                    + new Date()
                    + "]  Update the global setting Data Export > Input File value with the proper billing file location.");
            }
        }
        return inputFileText;
    }

    private JCheckBox getIsAppendingCheckBox() {
        if (ivjIsAppendingCheckBox == null) {
            try {
                ivjIsAppendingCheckBox = new JCheckBox();
                ivjIsAppendingCheckBox.setName("IsAppendingCheckBox");
                ivjIsAppendingCheckBox.setFont(new Font("dialog", 0, 12));
                ivjIsAppendingCheckBox.setText("Append to File");

                ivjIsAppendingCheckBox.setSelected(getBillingDefaults().isAppendToFile());
                ivjIsAppendingCheckBox.addActionListener(this);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjIsAppendingCheckBox;
    }

    private JButton getOutputFileBrowseButton() {
        if (ivjOutputFileBrowseButton == null) {
            try {
                ivjOutputFileBrowseButton = new JButton();
                ivjOutputFileBrowseButton.setName("OutputFileBrowseButton");
                ivjOutputFileBrowseButton.setText("...");

                ivjOutputFileBrowseButton.addActionListener(this);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjOutputFileBrowseButton;
    }

    private JLabel getOutputFileLabel() {
        if (ivjOutputFileLabel == null) {
            try {
                ivjOutputFileLabel = new JLabel();
                ivjOutputFileLabel.setName("OutputFileLabel");
                ivjOutputFileLabel.setFont(new Font("dialog", 0, 12));
                ivjOutputFileLabel.setText("Output File Directory:");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjOutputFileLabel;
    }

    private JTextField getOutputFileTextField() {
        if (ivjOutputFileTextField == null) {
            try {
                ivjOutputFileTextField = new JTextField();
                ivjOutputFileTextField.setName("OutputFileTextField");

                // set default value
                ivjOutputFileTextField.setText(getBillingDefaults().getOutputFileDir());

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjOutputFileTextField;
    }

    private JCheckBox getRemoveMultiplierCheckBox() {
        if (ivjRemoveMultiplierCheckBox == null) {
            try {
                ivjRemoveMultiplierCheckBox = new JCheckBox();
                ivjRemoveMultiplierCheckBox.setName("RemoveMultiplierCheckBox");
                ivjRemoveMultiplierCheckBox.setFont(new Font("dialog", 0, 12));
                ivjRemoveMultiplierCheckBox.setText("Remove Multiplier");
                ivjRemoveMultiplierCheckBox.setSelected(getBillingDefaults().isRemoveMultiplier());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRemoveMultiplierCheckBox;
    }

    /**
     * Return the Separator property value.
     * 
     * @return javax.swing.JSeparator
     */

    private JSeparator getSeparator() {
        if (ivjSeparator == null) {
            try {
                ivjSeparator = new JSeparator();
                ivjSeparator.setName("Separator");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjSeparator;
    }

    private JLabel getTimeElapsedLabel() {
        if (ivjTimeElapsedLabel == null) {
            try {
                ivjTimeElapsedLabel = new JLabel();
                ivjTimeElapsedLabel.setName("TimeElapsedLabel");
                ivjTimeElapsedLabel.setText("Time Elapsed");
                ivjTimeElapsedLabel.setForeground(java.awt.Color.red);
                ivjTimeElapsedLabel.setFont(new Font("dialog", 0, 12));
                ivjTimeElapsedLabel.setEnabled(false);
                ivjTimeElapsedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjTimeElapsedLabel;
    }

    private JLabel getTimerLabel() {
        if (ivjTimerLabel == null) {
            try {
                ivjTimerLabel = new JLabel();
                ivjTimerLabel.setName("TimerLabel");
                ivjTimerLabel.setText("0 sec");
                ivjTimerLabel.setVisible(true);
                ivjTimerLabel.setForeground(java.awt.Color.red);
                ivjTimerLabel.setFont(new Font("dialog", 0, 12));
                ivjTimerLabel.setEnabled(false);
                ivjTimerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTimerLabel;
    }

    public Thread getTimerThread() {
        if (timerThread == null) {
            timerThread = new Thread("SecondsCounterThread") {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                            String count =
                                getTimerLabel().getText().substring(0, getTimerLabel().getText().indexOf(" "));
                            getTimerLabel().setText(String.valueOf(Integer.parseInt(count) + 1) + " sec");
                        } catch (InterruptedException e) {
                            return;
                        }
                    }

                }

            };
        }

        return timerThread;
    }

    /**
     * Called whenever the part throws an exception.
     * 
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {
        CTILogger.error(exception);
    }

    private void initialize() {
        try {

            // Get the billing file defaults from the text config file.
            // ** Do this before initializing any JComponents. **
            // This application stores it's own default text config file,
            // defined in billingFileDefaults class.
            billingFile = new BillingFile();

            setName("BillingFile");
            setLayout(new GridBagLayout());
            setSize(410, 461);

            GridBagConstraints constraintsFileFormatPanel = new GridBagConstraints();
            constraintsFileFormatPanel.gridx = 0;
            constraintsFileFormatPanel.gridy = 0;
            constraintsFileFormatPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsFileFormatPanel.weightx = 1.0;
            constraintsFileFormatPanel.weighty = 1.0;
            constraintsFileFormatPanel.insets = new Insets(5, 5, 5, 5);
            add(getFileFormatPanel(), constraintsFileFormatPanel);

            GridBagConstraints constraintsGenerateFilePanel = new GridBagConstraints();
            constraintsGenerateFilePanel.gridx = 0;
            constraintsGenerateFilePanel.gridy = 1;
            constraintsGenerateFilePanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsGenerateFilePanel.insets = new Insets(5, 5, 5, 5);
            add(getGenerateFilePanel(), constraintsGenerateFilePanel);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {
        try {
            System.setProperty("cti.app.name", "Billing");
            CTILogger.info("Billing starting...");
            YukonSpringHook.setDefaultContext("com.cannontech.context.billing");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            final JFrame frame = new JFrame();

            ClientSession session = ClientSession.getInstance();
            if (!session.establishSession(frame)) {
                System.exit(-1);
            }

            if (!session.checkRole(YukonRole.APPLICATION_BILLING.getRoleId())) {
                JOptionPane.showMessageDialog(null, "User: '" + session.getUser().getUsername()
                    + "' is not authorized to use this application, exiting.", "Access Denied",
                    JOptionPane.WARNING_MESSAGE);
                System.exit(-1);
            }

            // Create a menuBar for running standalone.
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            fileMenu.setMnemonic('f');
            JMenuItem exitMenuItem = new JMenuItem("Exit", 'e');
            exitMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            fileMenu.add(exitMenuItem);
            menuBar.add(fileMenu);
            JMenu helpMenu = new JMenu("Help");
            helpMenu.setMnemonic('h');
            JMenuItem aboutMenuItem = new JMenuItem("About", 'a');
            aboutMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame popupFrame = new JFrame();
                    popupFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("ctismall.gif"));
                    JOptionPane.showMessageDialog(popupFrame, "This is version " + VersionTools.getYUKON_VERSION()
                        + "\nCopyright (C) 1999-2003 Cannon Technologies.", "About Yukon Export Client",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            });
            helpMenu.add(aboutMenuItem);
            menuBar.add(helpMenu);
            frame.setJMenuBar(menuBar);
            // End menuBar setup

            BillingFileFormatPanel aBillingFileFormatPanel = new BillingFileFormatPanel();
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                };
            });

            frame.setContentPane(aBillingFileFormatPanel);
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage("ctismall.gif"));
            frame.setSize(aBillingFileFormatPanel.getSize());
            frame.getInsets();
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation((int) (d.width * .2), (int) (d.height * .1));

            frame.show();
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            CTILogger.error(exception);
        }
    }

    private BillingFileDefaults retrieveBillingDefaultsFromGui() {

        DeviceGroupTreeFactory modelFactory =
            YukonSpringHook.getBean("deviceGroupTreeFactory", DeviceGroupTreeFactory.class);

        // Get all selected collection groups from the groupList scroll panel.
        List<String> deviceGroups = new ArrayList<String>(getGroupList().getSelectionCount());
        TreePath[] selectedPaths = getGroupList().getSelectionPaths();
        if (selectedPaths != null) {
            for (TreePath path : selectedPaths) {
                DeviceGroup groupForPath = modelFactory.getGroupForPath(path);
                String fullName = groupForPath.getFullName();
                deviceGroups.add(fullName);
            }
        }

        if (deviceGroups.isEmpty()) {
            update(billingFile, "Please make a billing group selection.");
            return null;
        }

        int formatID = FileFormatTypes.getFormatID(getFileFormatComboBox().getSelectedItem().toString());
        int demandDaysPrevious = NumberUtils.toInt(getDemandDaysPreviousTextBox().getText(), 0);
        int energyDaysPrevious = NumberUtils.toInt(getEnergyDaysPreviousTextBox().getText(), 0);
        // FormatID, demandDays, energyDays, collectionGrpVector, outputFile, inputFile
        BillingFileDefaults newDefaults =
            new BillingFileDefaults(formatID, demandDaysPrevious, energyDaysPrevious, deviceGroups,
                getOutputFileTextField().getText(), getRemoveMultiplierCheckBox().isSelected(), getInputFileText(),
                getDateComboBox().getSelectedDate(), getIsAppendingCheckBox().isSelected());
        return newDefaults;
    }

    private void setBillingDefaults(BillingFileDefaults newDefaults) {
        this.billingFileDefaults = newDefaults;
    }

    /**
     * @param int formatID
     */
    private void setBillingFormatter(BillingFileDefaults billingFileDefaults) {
        getBillingFile().setBillingFormatter(billingFileDefaults);
    }

    @Override
    public synchronized void update(Observable obs, Object data) {
        if (obs instanceof BillingFile) {
            CTILogger.info("...Ended format at: " + new Date());

            // kill our timerThread
            getTimerThread().interrupt();
            timerThread = null;

            BillingFile src = (BillingFile) obs;
            src.deleteObserver(this);
            enableTimer(false);

            getGenerateFileToggleButton().setSelected(false);
            getGenerateFileToggleButton().setText("Generate File");

            JOptionPane.showMessageDialog(null, data.toString(), "Yukon Billing File Generator Results",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
