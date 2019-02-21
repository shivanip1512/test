package com.cannontech.tools.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.popup.PopUpMenuShower;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.dbconverter.converter.DBConverter;
import com.cannontech.dbtools.DBCompare.DBCompare;
import com.cannontech.dbtools.image.ImageInserter;
import com.cannontech.dbtools.tools.ModifyConstraints;
import com.cannontech.dbtools.updater.DBUpdater;

/**
 * This is just a GUI interface for all tools that need an output panel
 * Creation date: (7/11/2001 9:49:29 AM)
 * @author: Eric Schmit
 */
class DBToolsFrame extends JFrame implements IMessageFrame, ActionListener, PopupMenuListener {

    static {
        CtiUtilities.setClientAppName(ApplicationId.DB_TOOLS_FRAME);
    }
    private static final String DEF_PATH =
        System.getProperty("user.dir") + IRunnableDBTool.FS;

    /**
     * All the possible tools available for use, do not change the order of this!
     * Add any new items to the end of the list.
     */
    private static final IRunnableDBTool[] ALL_TOOLS = {
        new DBUpdater(),
        new DBConverter(),
        new ModifyConstraints(),
        new ImageInserter(),
        new DBCompare()
    };

    private TextMsgPanePopUp textMsgPanePopUp = null;
    private ButtonGroup buttGroup = new ButtonGroup();
    private JPanel ivjMainPanel = null;
    private JTextField ivjPathField = null;
    private JButton ivjStartButton = null;
    private JPanel ivjButtonPanel = null;
    private JScrollPane ivjOutputScrollPane = null;
    private JTextArea ivjMessageArea = null;
    private JButton ivjBrowseButton = null;
    private JButton ivjBrowseXMLButton = null;
    private JButton ivjSaveButton = null;
    private JLabel ivjJLabelMsgs = null;
    private JLabel ivjJLabelOption = null;
    private JPanel ivjJPanelTools = null;
    private JRadioButton ivjJRadioButton0 = null;
    private JRadioButton ivjJRadioButton1 = null;
    private JRadioButton ivjJRadioButton2 = null;
    private JRadioButton ivjJRadioButton3 = null;
    private JRadioButton ivjJRadioButton4 = null;
    private JRadioButton ivjJRadioButton5 = null;

    private DBToolsFrame() {
        initialize();
    }

    private DBToolsFrame(String title) {
        super(title);
        initialize();
    }

    /**
     * Method to handle events for the ActionListener interface.
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getStartButton()) {
            connEtoC1(e);
        }
        if (e.getSource() == getBrowseButton()) {
            connEtoC2(e);
        }
        if (e.getSource() == getBrowseXMLButton()) {
            connEtoC3(e);
        }
        if (e.getSource() == getSaveButton()) {
            connEtoC4(e);
        }
    }

    /**
     * Here we pass our messages from dbconverter off to the eventqueue thread so they can
     * get drawn before the whole damn thing is done.
     */
    @Override
    public void addOutput(final String output) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getMessageArea().append(output + IRunnableDBTool.LF);
            }
        });
    }

    @Override
    public void addOutputNoLN(final String output) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getMessageArea().append(output);
            }
        });
    }

    public void browseButton_ActionPerformed(ActionEvent actionEvent) {
        getChooser();
    }

    public void browseXMLButton_ActionPerformed(ActionEvent actionEvent) {
        getBrowseXMLChooser();
    }

    public void saveButton_ActionPerformed(ActionEvent actionEvent) {
        getSaveChooser();
    }

    /**
     * connEtoC1: (StartButton.action.actionPerformed(ActionEvent) -->
     * DBToolsFrame.startButton_ActionPerformed(LActionEvent;)V)
     */
    private void connEtoC1(ActionEvent arg1) {
        try {
            this.startButton_ActionPerformed(arg1);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2: (BrowseButton.action.actionPerformed(ActionEvent) -->
     * DBToolsFrame.browseButton_ActionPerformed(LActionEvent;)V)
     */
    private void connEtoC2(ActionEvent arg1) {
        try {
            this.browseButton_ActionPerformed(arg1);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3: (BrowseButton.action.actionPerformed(ActionEvent) -->
     * DBToolsFrame.browseXMLButton_ActionPerformed(LActionEvent;)V)
     */
    private void connEtoC3(ActionEvent arg1) {
        try {
            this.browseXMLButton_ActionPerformed(arg1);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4: (BrowseButton.action.actionPerformed(ActionEvent) -->
     * DBToolsFrame.saveButton_ActionPerformed(LActionEvent;)V)
     * @param arg1 ActionEvent
     */
    private void connEtoC4(ActionEvent arg1) {
        try {
            this.saveButton_ActionPerformed(arg1);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void finish(final String msg) {
        JFrame box = new JFrame("Complete");

        box.setResizable(false);
        box.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("ctismall.gif"));

        JOptionPane.showMessageDialog(box, (msg == null ? "Tool Operation Completed" : msg));
    }

    private JButton getBrowseButton() {
        if (ivjBrowseButton == null) {
            try {
                ivjBrowseButton = new JButton();
                ivjBrowseButton.setName("BrowseButton");
                ivjBrowseButton.setText("Browse");
                ivjBrowseButton.setMinimumSize(new java.awt.Dimension(90, 25));
                ivjBrowseButton.setMaximumSize(new java.awt.Dimension(90, 25));
                ivjBrowseButton.setPreferredSize(new java.awt.Dimension(90, 25));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjBrowseButton;
    }

    private JButton getBrowseXMLButton() {
        if (ivjBrowseXMLButton == null) {
            try {
                ivjBrowseXMLButton = new JButton();
                ivjBrowseXMLButton.setName("BrowseXMLButton");
                ivjBrowseXMLButton.setText("Browse");
                ivjBrowseXMLButton.setMinimumSize(new java.awt.Dimension(90, 25));
                ivjBrowseXMLButton.setMaximumSize(new java.awt.Dimension(90, 25));
                ivjBrowseXMLButton.setPreferredSize(new java.awt.Dimension(90, 25));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjBrowseXMLButton;
    }

    private JButton getSaveButton() {
        if (ivjSaveButton == null) {
            try {
                ivjSaveButton = new JButton();
                ivjSaveButton.setName("SaveButton");
                ivjSaveButton.setText("Save...");
                ivjSaveButton.setMinimumSize(new java.awt.Dimension(90, 25));
                ivjSaveButton.setMaximumSize(new java.awt.Dimension(90, 25));
                ivjSaveButton.setPreferredSize(new java.awt.Dimension(90, 25));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSaveButton;
    }

    private JPanel getButtonPanel() {
        if (ivjButtonPanel == null) {
            try {
                ivjButtonPanel = new JPanel();
                ivjButtonPanel.setName("ButtonPanel");
                ivjButtonPanel.setBorder(new EtchedBorder());
                ivjButtonPanel.setLayout(new java.awt.GridBagLayout());
                ivjButtonPanel.setMaximumSize(new java.awt.Dimension(32767, 32767));

                java.awt.GridBagConstraints constraintsStartButton = new java.awt.GridBagConstraints();
                constraintsStartButton.gridx = 2;
                constraintsStartButton.gridy = 2;
                constraintsStartButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsStartButton.insets = new java.awt.Insets(4, 3, 10, 6);
                constraintsStartButton.weightx = 1.0;
                getButtonPanel().add(getStartButton(), constraintsStartButton);

                java.awt.GridBagConstraints constraintsSaveButton = new java.awt.GridBagConstraints();
                constraintsSaveButton.gridx = 3;
                constraintsSaveButton.gridy = 2;
                constraintsSaveButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsSaveButton.insets = new java.awt.Insets(5, 3, 10, 6);
                constraintsSaveButton.weightx = 1.0;
                getButtonPanel().add(getSaveButton(), constraintsSaveButton);

                java.awt.GridBagConstraints constraintsPathField = new java.awt.GridBagConstraints();
                constraintsPathField.gridx = 2;
                constraintsPathField.gridy = 1;
                constraintsPathField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsPathField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsPathField.weightx = 1.0;
                constraintsPathField.insets = new java.awt.Insets(14, 3, 4, 6);
                getButtonPanel().add(getPathField(), constraintsPathField);

                java.awt.GridBagConstraints constraintsBrowseButton = new java.awt.GridBagConstraints();
                constraintsBrowseButton.gridx = 3;
                constraintsBrowseButton.gridy = 1;
                constraintsBrowseButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsBrowseButton.insets = new java.awt.Insets(12, 6, 6, 71);
                getButtonPanel().add(getBrowseButton(), constraintsBrowseButton);

                java.awt.GridBagConstraints constraintsBrowseXMLButton = new java.awt.GridBagConstraints();
                constraintsBrowseXMLButton.gridx = 3;
                constraintsBrowseXMLButton.gridy = 1;
                constraintsBrowseXMLButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsBrowseXMLButton.insets = new java.awt.Insets(12, 6, 6, 71);
                getButtonPanel().add(getBrowseXMLButton(), constraintsBrowseXMLButton);

                java.awt.GridBagConstraints constraintsJLabelOption = new java.awt.GridBagConstraints();
                constraintsJLabelOption.gridx = 1;
                constraintsJLabelOption.gridy = 1;
                constraintsJLabelOption.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelOption.insets = new java.awt.Insets(17, 12, 4, 3);
                getButtonPanel().add(getJLabelOption(), constraintsJLabelOption);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjButtonPanel;
    }

    public void getChooser() {
        // This will need to be updated someday for a new version of swing
        Frame parent = SwingUtil.getParentFrame(this);
        JFileChooser fileChooser = new JFileChooser();

        // we set the chooser so it will only look for dirs
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setApproveButtonText("Select");
        fileChooser.setApproveButtonMnemonic('s');

        // allow them to see only directories
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            };

            @Override
            public String getDescription() {
                return "Directories Only";
            };
        });

        // set the chooser to the current location
        fileChooser.setCurrentDirectory(new File(getPathField().getText()));

        int res = fileChooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                // thePath = fileChooser.getSelectedFile().getPath();
                getPathField().setText(fileChooser.getSelectedFile().getPath() + IRunnableDBTool.FS);

                CTILogger.info("** Chooser path was: " + getPathField().getText());
            } catch (Exception exep) {
                JOptionPane.showMessageDialog(parent, "An error occurred opening file",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void getBrowseXMLChooser()
    {
        Frame parent = SwingUtil.getParentFrame(this);
        JFileChooser fileChooser = new JFileChooser();

        // We set the chooser so it will only look for dirs
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setApproveButtonText("Select");
        fileChooser.setApproveButtonMnemonic('s');

        // Allow them to see only directories and XML Files
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getAbsolutePath().endsWith(".xml") || f.isDirectory();
            };

            @Override
            public String getDescription() {
                return "XML Files Only";
            };
        });

        // set the chooser to the current location
        fileChooser.setCurrentDirectory(new File(getPathField().getText()));

        int res = fileChooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                getPathField().setText(fileChooser.getSelectedFile().getPath());

                CTILogger.info("** Chooser path was: " + getPathField().getText());
            } catch (Exception exep) {
                JOptionPane.showMessageDialog(parent, "An error occurred opening file", "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void getXMLFileChooser() {
        // This will need to be updated someday for a new version of swing
        Frame parent = SwingUtil.getParentFrame(this);
        JFileChooser fileChooser = new JFileChooser();

        // we set the chooser so it will only look for dirs
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
        fileChooser.setApproveButtonText("Select");
        fileChooser.setApproveButtonMnemonic('s');

        // allow them to see only directories
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".xml");
            };

            @Override
            public String getDescription() {
                return "XML Files Only";
            };
        });

        // set the chooser to the current location
        fileChooser.setCurrentDirectory(
            new File(getPathField().getText()));

        int res = fileChooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                getPathField().setText(fileChooser.getSelectedFile().getPath() + IRunnableDBTool.FS);

                CTILogger.info("** Chooser path was: " + getPathField().getText());
            } catch (Exception exep) {
                JOptionPane.showMessageDialog(parent, "An error occurred opening file", "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void getSaveChooser() {
        // File temp = new File(shorterPath);

        // This will need to be updated someday for a new version of swing
        Frame parent = SwingUtil.getParentFrame(this);
        JFileChooser fileChooser = new JFileChooser();

        // we set the chooser so it will only look for dirs
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
        fileChooser.setApproveButtonText("Save");
        fileChooser.setApproveButtonMnemonic('s');

        // set the chooser to the current location
        // fileChooser.setCurrentDirectory(
        // new File(getPathField().getText()) );

        int res = fileChooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                // thePath = fileChooser.getSelectedFile().getPath();
                String path = fileChooser.getSelectedFile().getPath();

                String text = getMessageArea().getText();
                FileWriter fileWriter = new FileWriter(path);
                fileWriter.write(text);
                fileWriter.close();

                CTILogger.info("** Saving screen results to " + path);
            } catch (Exception exep) {
                JOptionPane.showMessageDialog(parent, "An error occurred while saving the file", "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JLabel getJLabelMsgs() {
        if (ivjJLabelMsgs == null) {
            try {
                ivjJLabelMsgs = new JLabel();
                ivjJLabelMsgs.setName("JLabelMsgs");
                ivjJLabelMsgs.setPreferredSize(new Dimension(250, 14));
                ivjJLabelMsgs.setText("Output Messages");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelMsgs;
    }

    private JLabel getJLabelOption() {
        if (ivjJLabelOption == null) {
            try {
                ivjJLabelOption = new JLabel();
                ivjJLabelOption.setName("JLabelOption");
                ivjJLabelOption.setText("Directory:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelOption;
    }

    private JPanel getJPanelTools() {
        if (ivjJPanelTools == null) {
            try {
                TitleBorder ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitleFont(new Font("Arial", 1, 14));
                ivjLocalBorder.setTitle("Available Tools");
                ivjJPanelTools = new JPanel();
                ivjJPanelTools.setName("JPanelTools");
                ivjJPanelTools.setBorder(ivjLocalBorder);
                ivjJPanelTools.setLayout(getJPanelToolsFlowLayout());

                ivjJPanelTools.setMinimumSize(new Dimension(130, 90));
                ivjJPanelTools.setMaximumSize(new Dimension(130, 90));
                ivjJPanelTools.setPreferredSize(new Dimension(130, 90));

                getJPanelTools().add(getJRadioButton0(), getJRadioButton0().getName());
                getJPanelTools().add(getJRadioButton1(), getJRadioButton1().getName());
                getJPanelTools().add(getJRadioButton2(), getJRadioButton2().getName());
                getJPanelTools().add(getJRadioButton3(), getJRadioButton3().getName());
                getJPanelTools().add(getJRadioButton4(), getJRadioButton4().getName());
                getJPanelTools().add(getJRadioButton5(), getJRadioButton5().getName());

                // this listener will say what to do on a selection
                final ActionListener al = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() instanceof JRadioButton) {
                            JRadioButton button = (JRadioButton) e.getSource();

                            // store the tool in the radio button
                            IRunnableDBTool tool = (IRunnableDBTool) button.getClientProperty(button);

                            getJLabelOption().setText(tool.getParamText());

                            if (tool.getDefaultValue() != null) {
                                getPathField().setText(tool.getDefaultValue());
                            }

                            if (!(tool instanceof ModifyConstraints)) {
                                // Turn on the browse functionality to look for XML Files.
                                if (tool instanceof DBCompare) {
                                    getBrowseButton().setVisible(false);
                                    getBrowseXMLButton().setVisible(true);
                                    // Revert back to browsing for directories.
                                } else {
                                    getBrowseButton().setVisible(true);
                                    getBrowseXMLButton().setVisible(false);
                                }
                            }
                        }
                    }
                };

                for (int i = 0; i < getJPanelTools().getComponentCount(); i++) {
                    if (getJPanelTools().getComponent(i) instanceof JRadioButton) {
                        JRadioButton button = (JRadioButton) getJPanelTools().getComponent(i);

                        if (i < ALL_TOOLS.length) {
                            buttGroup.add(button);
                            button.setText(ALL_TOOLS[i].getName());
                            button.addActionListener(al);

                            // store the tool in the radio button
                            button.putClientProperty(button, ALL_TOOLS[i]);
                        } else {
                            button.setVisible(false);
                        }
                    }
                }

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelTools;
    }

    private FlowLayout getJPanelToolsFlowLayout() {
        FlowLayout ivjJPanelToolsFlowLayout = null;
        try {
            /* Create part */
            ivjJPanelToolsFlowLayout = new FlowLayout();
            ivjJPanelToolsFlowLayout.setAlignment(FlowLayout.LEFT);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
        return ivjJPanelToolsFlowLayout;
    }

    private JRadioButton getJRadioButton0() {
        if (ivjJRadioButton0 == null) {
            try {
                ivjJRadioButton0 = new JRadioButton();
                ivjJRadioButton0.setName("JRadioButton0");
                ivjJRadioButton0.setText("JRadioButton0");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButton0;
    }

    private JRadioButton getJRadioButton1() {
        if (ivjJRadioButton1 == null) {
            try {
                ivjJRadioButton1 = new JRadioButton();
                ivjJRadioButton1.setName("JRadioButton1");
                ivjJRadioButton1.setText("JRadioButton1");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButton1;
    }

    private JRadioButton getJRadioButton2() {
        if (ivjJRadioButton2 == null) {
            try {
                ivjJRadioButton2 = new JRadioButton();
                ivjJRadioButton2.setName("JRadioButton2");
                ivjJRadioButton2.setText("JRadioButton2");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButton2;
    }

    private JRadioButton getJRadioButton3() {
        if (ivjJRadioButton3 == null) {
            try {
                ivjJRadioButton3 = new JRadioButton();
                ivjJRadioButton3.setName("JRadioButton3");
                ivjJRadioButton3.setText("JRadioButton3");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButton3;
    }

    private JRadioButton getJRadioButton4() {
        if (ivjJRadioButton4 == null) {
            try {
                ivjJRadioButton4 = new JRadioButton();
                ivjJRadioButton4.setName("JRadioButton4");
                ivjJRadioButton4.setText("JRadioButton4");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButton4;
    }

    private JRadioButton getJRadioButton5() {
        if (ivjJRadioButton5 == null) {
            try {
                ivjJRadioButton5 = new JRadioButton();
                ivjJRadioButton5.setName("JRadioButton5");
                ivjJRadioButton5.setText("JRadioButton5");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButton5;
    }

    private JPanel getMainPanel() {
        if (ivjMainPanel == null) {
            try {
                ivjMainPanel = new JPanel();
                ivjMainPanel.setName("MainPanel");
                ivjMainPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsJLabelMsgs = new GridBagConstraints();
                constraintsJLabelMsgs.gridx = 1;
                constraintsJLabelMsgs.gridy = 1;
                constraintsJLabelMsgs.anchor = GridBagConstraints.WEST;
                constraintsJLabelMsgs.insets = new Insets(11, 16, 2, 2);
                getMainPanel().add(getJLabelMsgs(), constraintsJLabelMsgs);

                GridBagConstraints constraintsOutputScrollPane = new GridBagConstraints();
                constraintsOutputScrollPane.gridx = 1;
                constraintsOutputScrollPane.gridy = 2;
                constraintsOutputScrollPane.fill = GridBagConstraints.BOTH;
                constraintsOutputScrollPane.anchor = GridBagConstraints.WEST;
                constraintsOutputScrollPane.weightx = 1.0;
                constraintsOutputScrollPane.weighty = 1.0;
                constraintsOutputScrollPane.insets = new Insets(2, 12, 2, 2);
                getMainPanel().add(getOutputScrollPane(), constraintsOutputScrollPane);

                GridBagConstraints constraintsButtonPanel = new GridBagConstraints();
                constraintsButtonPanel.gridx = 1;
                constraintsButtonPanel.gridy = 3;
                constraintsButtonPanel.gridwidth = 2;
                constraintsButtonPanel.fill = GridBagConstraints.HORIZONTAL;
                constraintsButtonPanel.anchor = GridBagConstraints.SOUTH;
                constraintsButtonPanel.weightx = 0.0;
                constraintsButtonPanel.weighty = 0.0;
                constraintsButtonPanel.insets = new Insets(3, 12, 10, 9);
                getMainPanel().add(getButtonPanel(), constraintsButtonPanel);

                GridBagConstraints constraintsJPanelTools = new GridBagConstraints();
                constraintsJPanelTools.gridx = 2;
                constraintsJPanelTools.gridy = 2;
                constraintsJPanelTools.fill = GridBagConstraints.BOTH;
                constraintsJPanelTools.anchor = GridBagConstraints.EAST;
                constraintsJPanelTools.weightx = 0.0;
                constraintsJPanelTools.weighty = 0.0;
                constraintsJPanelTools.insets = new Insets(2, 2, 2, 8);
                getMainPanel().add(getJPanelTools(), constraintsJPanelTools);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjMainPanel;
    }

    private JTextArea getMessageArea() {
        if (ivjMessageArea == null) {
            try {
                ivjMessageArea = new JTextArea();
                ivjMessageArea.setName("MessageArea");

                ivjMessageArea.setBounds(0, 0, 160, 120);

                ivjMessageArea.setEditable(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjMessageArea;
    }

    private JScrollPane getOutputScrollPane() {
        if (ivjOutputScrollPane == null) {
            try {
                ivjOutputScrollPane = new JScrollPane();
                ivjOutputScrollPane.setName("OutputScrollPane");
                ivjOutputScrollPane.setAutoscrolls(true);
                ivjOutputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                ivjOutputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                getOutputScrollPane().setViewportView(getMessageArea());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjOutputScrollPane;
    }

    private JTextField getPathField() {
        if (ivjPathField == null) {
            try {
                ivjPathField = new JTextField();
                ivjPathField.setName("PathField");
                ivjPathField.setFont(new Font("Arial", 1, 12));
                ivjPathField.setEditable(true);

                ivjPathField.setText(DEF_PATH);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPathField;
    }

    private JButton getStartButton() {
        if (ivjStartButton == null) {
            try {
                ivjStartButton = new JButton();
                ivjStartButton.setName("StartButton");
                ivjStartButton.setText("Start...");

                ivjStartButton.setMinimumSize(new Dimension(90, 25));
                ivjStartButton.setMaximumSize(new Dimension(90, 25));
                ivjStartButton.setPreferredSize(new Dimension(90, 25));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjStartButton;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error(exception.getMessage(), exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws Exception {
        // init the popup box connections for the MsgPanel
        MouseListener msgList = new PopUpMenuShower(getTextMsgPanePopUp());
        getMessageArea().addMouseListener(msgList);
        getTextMsgPanePopUp().addPopupMenuListener(this);

        getStartButton().addActionListener(this);
        getBrowseButton().addActionListener(this);
        getBrowseXMLButton().addActionListener(this);
        getSaveButton().addActionListener(this);
    }

    private TextMsgPanePopUp getTextMsgPanePopUp() {
        if (textMsgPanePopUp == null) {
            textMsgPanePopUp = new TextMsgPanePopUp();
        }

        return textMsgPanePopUp;
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("ConverterFrame");
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setSize(703, 439);
            setTitle("Tools Control Panel");
            setContentPane(getMainPanel());
            initConnections();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }

        getJRadioButton0().doClick();

    }

    /**
     * main entry point - starts the part when it is run as an application
     */
    public static void main(String[] args) {
        try {

            DBToolsFrame aConverterFrame;
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            aConverterFrame = new DBToolsFrame();
            aConverterFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                };
            });
            aConverterFrame.setVisible(true);
            Insets insets = aConverterFrame.getInsets();
            aConverterFrame.setSize(aConverterFrame.getWidth() + insets.left + insets.right,
                aConverterFrame.getHeight() + insets.top + insets.bottom);
            aConverterFrame.setLocation(220, 150);

            aConverterFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("ctismall.gif"));

            aConverterFrame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of DBToolsFrame");
            CTILogger.error(exception.getMessage(), exception);
        }
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        if (e.getSource() == getTextMsgPanePopUp()) {
            getTextMsgPanePopUp().setTextArea(getMessageArea());
        }
    }

    /**
     * Use this method to set all properties needed by any of the tools.
     * This method takes the user entered values and stores them in the System space.
     */
    private void setProps() {
        // get the text from the input field
        System.setProperty(IRunnableDBTool.PROP_VALUE, getPathField().getText());
    }

    private JRadioButton getSelectedButton() {
        for (int i = 0; i < getJPanelTools().getComponentCount(); i++) {
            if (getJPanelTools().getComponent(i) instanceof JRadioButton) {
                if (((JRadioButton) getJPanelTools().getComponent(i)).isSelected()) {
                    return (JRadioButton) getJPanelTools().getComponent(i);
                }
            }
        }

        return null;
    }

    /**
     * We null out any previous threads that might exist, then make a new one, and start the ball rollin'.
     */
    public void startButton_ActionPerformed(ActionEvent actionEvent) {
        setProps();

        // store the tool in the radio button
        JRadioButton button = getSelectedButton();

        if (button != null) {
            IRunnableDBTool tool = (IRunnableDBTool) button.getClientProperty(button);

            tool.setIMessageFrame(this);

            Thread thread = new Thread(tool);
            thread.setName("DBTool");
            thread.start();
        }
    }
}
