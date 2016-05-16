package com.cannontech.common.gui.editor.state;

/**
 * This type was created in VisualAge.
 */

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.YukonImagePanel;
import com.cannontech.common.gui.util.ColorComboBoxCellRenderer;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.data.state.GroupState;
import com.cannontech.database.data.state.State;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.database.db.state.YukonImage;
import com.cannontech.yukon.IDatabaseCache;
import com.klg.jclass.util.value.JCValueEvent;
import com.klg.jclass.util.value.JCValueListener;

public class GroupStateEditorPanel extends DataInputPanel implements JCValueListener, ActionListener, ItemListener,
        CaretListener, DataInputPanelListener {
    // this must be changed whenever the number or states are changed
    public static final int STATE_COUNT = 22;

    private JLabel[] rawStateLabels = null;
    private JTextField[] stateNameTextFields = null;
    private JComboBox[] foregroundColorComboBoxes = null;
    private JButton[] imageButtons = null;
    private JLabel ivjStateGroupNameLabel = null;
    private JTextField ivjStateGroupNameTextField = null;
    private JLabel ivjStateNumberLabel = null;
    private com.klg.jclass.field.JCSpinField ivjStateNumberSpinner = null;
    private JLabel ivjRepeaterLabel = null;
    private JLabel ivjVariableBitsLabel = null;
    private JLabel ivjRawStateColumnLabel = null;
    private JPanel ivjIdentificationPanel = null;
    private JPanel ivjStatesPanel = null;
    private JScrollPane ivjJScrollPane = null;
    private JLabel ivjJLabelImage = null;

    /**
     * Constructor
     */
    public GroupStateEditorPanel() {
        super();
        initialize();
    }

    /**
     * Method to handle events for the ActionListener interface.
     * imageButtons
     * 
     * @param e java.awt.event.ActionEvent
     */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        for (JButton imageButton : imageButtons) {
            if (e.getSource() == imageButton) {
                try {
                    jButtonImage_ActionPerformed(e);
                } catch (java.lang.Throwable ivjExc) {
                    handleException(ivjExc);
                }
            }
        }
    }

    /**
     * Method to handle events for the CaretListener interface.
     * stateNameTextFields, stateGropuNameTextField
     * 
     * @param e javax.swing.event.CaretEvent
     */
    @Override
    public void caretUpdate(CaretEvent e) {

        if (e.getSource() == getStateGroupNameTextField()) {
            handleCaretEvent();
        }

        for (JTextField stateNameTextField : stateNameTextFields) {
            if (e.getSource() == stateNameTextField) {
                handleCaretEvent();
            }
        }
    }

    private void handleCaretEvent() {
        try {
            fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Return a ForegroundColorComboBox property value.
     * 
     * @return JComboBox
     */
    private JComboBox buildForegroundColorComboBox() {
        JComboBox jComboBox = new JComboBox();
        jComboBox.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBox.setFont(new java.awt.Font("dialog", 0, 12));
        jComboBox.setMinimumSize(new java.awt.Dimension(120, 25));
        jComboBox.setRenderer(new ColorComboBoxCellRenderer());

        jComboBox.addItem(Colors.GREEN_STR_ID);
        jComboBox.addItem(Colors.RED_STR_ID);
        jComboBox.addItem(Colors.WHITE_STR_ID);
        jComboBox.addItem(Colors.YELLOW_STR_ID);
        jComboBox.addItem(Colors.BLUE_STR_ID);
        jComboBox.addItem(Colors.CYAN_STR_ID);
        jComboBox.addItem(Colors.BLACK_STR_ID);
        jComboBox.addItem(Colors.ORANGE_STR_ID);
        jComboBox.addItem(Colors.MAGENTA_STR_ID);
        jComboBox.addItem(Colors.GRAY_STR_ID);
        jComboBox.addItem(Colors.PINK_STR_ID);

        return jComboBox;
    }

    /**
     * Return the IdentificationPanel property value.
     * 
     * @return JPanel
     */
    private JPanel getIdentificationPanel() {
        if (ivjIdentificationPanel == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 12));
                ivjLocalBorder.setTitle("Identification");
                ivjIdentificationPanel = new JPanel();
                ivjIdentificationPanel.setName("IdentificationPanel");
                ivjIdentificationPanel.setBorder(ivjLocalBorder);
                ivjIdentificationPanel.setLayout(null);
                getIdentificationPanel().add(getStateGroupNameLabel(), getStateGroupNameLabel().getName());
                getIdentificationPanel().add(getStateGroupNameTextField(), getStateGroupNameTextField().getName());
                getIdentificationPanel().add(getStateNumberLabel(), getStateNumberLabel().getName());
                getIdentificationPanel().add(getStateNumberSpinner(), getStateNumberSpinner().getName());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjIdentificationPanel;
    }

    /**
     * Return a JButtonImage property value.
     * 
     * @return JButton
     */
    private JButton buildJButtonImage() {
        JButton jButton = new JButton();
        jButton.setText("Image...");
        jButton.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton.setFont(new java.awt.Font("Arial", 1, 10));
        jButton.setMargin(new java.awt.Insets(2, 3, 2, 3));
        jButton.setHorizontalAlignment(SwingConstants.CENTER);
        jButton.setEnabled(false);
        return jButton;
    }

    /**
     * Return the JLabelImage property value.
     * 
     * @return JLabel
     */
    private JLabel getJLabelImage() {
        if (ivjJLabelImage == null) {
            try {
                ivjJLabelImage = new JLabel();
                ivjJLabelImage.setName("JLabelImage");
                ivjJLabelImage.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelImage.setText("Image");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelImage;
    }

    /**
     * Return the JScrollPane property value.
     * 
     * @return JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (ivjJScrollPane == null) {
            try {
                TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new TitleBorder();
                ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 12));
                ivjLocalBorder1.setTitle("Configuration");
                ivjJScrollPane = new JScrollPane();
                ivjJScrollPane.setName("JScrollPane");
                ivjJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                ivjJScrollPane.setBorder(ivjLocalBorder1);
                ivjJScrollPane.setDoubleBuffered(false);
                ivjJScrollPane.setPreferredSize(new java.awt.Dimension(383, 235));
                ivjJScrollPane.setMinimumSize(new java.awt.Dimension(383, 235));
                getJScrollPane().setViewportView(getStatesPanel());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJScrollPane;
    }

    /**
     * Return the RepeaterLabel1 property value.
     * 
     * @return JLabel
     */
    private JLabel getRawStateColumnLabel() {
        if (ivjRawStateColumnLabel == null) {
            try {
                ivjRawStateColumnLabel = new JLabel();
                ivjRawStateColumnLabel.setName("RawStateColumnLabel");
                ivjRawStateColumnLabel.setFont(new java.awt.Font("dialog", 0, 12));
                ivjRawStateColumnLabel.setText("Raw State");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRawStateColumnLabel;
    }

    /**
     * Return a RawStateLabel property value.
     * 
     * @return JLabel
     */
    private JLabel buildRawStateLabel(int rawState) {
        JLabel jLabel = new JLabel();
        jLabel.setFont(new java.awt.Font("dialog", 0, 12));

        // special parsing for states 0 and 1 text
        if (rawState == 0) {
            jLabel.setText(rawState + " (Open)");
        } else if (rawState == 1) {
            jLabel.setText(rawState + " (Close)");
        } else {
            jLabel.setText(String.valueOf(rawState));
        }
        return jLabel;
    }

    /**
     * Return the RepeaterLabel property value.
     * 
     * @return JLabel
     */
    private JLabel getRepeaterLabel() {
        if (ivjRepeaterLabel == null) {
            try {
                ivjRepeaterLabel = new JLabel();
                ivjRepeaterLabel.setName("RepeaterLabel");
                ivjRepeaterLabel.setFont(new java.awt.Font("dialog", 0, 12));
                ivjRepeaterLabel.setText("State Text");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeaterLabel;
    }

    /**
     * Return the StateGroupNameLabel property value.
     * 
     * @return JLabel
     */
    private JLabel getStateGroupNameLabel() {
        if (ivjStateGroupNameLabel == null) {
            try {
                ivjStateGroupNameLabel = new JLabel();
                ivjStateGroupNameLabel.setName("StateGroupNameLabel");
                ivjStateGroupNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjStateGroupNameLabel.setText("State Group Name:");
                ivjStateGroupNameLabel.setBounds(10, 25, 121, 19);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjStateGroupNameLabel;
    }

    /**
     * Return the StateGroupNameTextField property value.
     * 
     * @return JTextField
     */
    private JTextField getStateGroupNameTextField() {
        if (ivjStateGroupNameTextField == null) {
            try {
                ivjStateGroupNameTextField = new JTextField();
                ivjStateGroupNameTextField.setName("StateGroupNameTextField");
                ivjStateGroupNameTextField.setPreferredSize(new java.awt.Dimension(150, 21));
                ivjStateGroupNameTextField.setBounds(141, 24, 150, 21);
                ivjStateGroupNameTextField.setMinimumSize(new java.awt.Dimension(150, 21));
                ivjStateGroupNameTextField.setDocument(new TextFieldDocument(
                    TextFieldDocument.MAX_STATE_GROUP_NAME_LENGTH));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjStateGroupNameTextField;
    }

    /**
     * Return a StateNameTextField property value.
     * 
     * @return JTextField
     */
    private JTextField buildStateNameTextField() {
        JTextField stateNameTextField = new JTextField();
        stateNameTextField.setPreferredSize(new java.awt.Dimension(130, 22));
        stateNameTextField.setMinimumSize(new java.awt.Dimension(130, 22));
        stateNameTextField.setColumns(0);
        stateNameTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_STATE_NAME_LENGTH));
        return stateNameTextField;
    }

    /**
     * Return the StateNumberLabel property value.
     * 
     * @return JLabel
     */
    private JLabel getStateNumberLabel() {
        if (ivjStateNumberLabel == null) {
            try {
                ivjStateNumberLabel = new JLabel();
                ivjStateNumberLabel.setName("StateNumberLabel");
                ivjStateNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjStateNumberLabel.setText("Number of States:");
                ivjStateNumberLabel.setBounds(10, 52, 113, 19);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjStateNumberLabel;
    }

    /**
     * Return the StateNumberSpinner property value.
     * 
     * @return com.klg.jclass.field.JCSpinField
     */
    private com.klg.jclass.field.JCSpinField getStateNumberSpinner() {
        if (ivjStateNumberSpinner == null) {
            try {
                ivjStateNumberSpinner = new com.klg.jclass.field.JCSpinField();
                ivjStateNumberSpinner.setName("StateNumberSpinner");
                ivjStateNumberSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
                ivjStateNumberSpinner.setBounds(141, 51, 50, 22);
                ivjStateNumberSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
                ivjStateNumberSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(
                    new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1),
                        new Integer(STATE_COUNT), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false,
                        false, false, null, new Integer(2)), new com.klg.jclass.util.value.MutableValueModel(
                        java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2,
                        new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjStateNumberSpinner;
    }

    /**
     * Return the JPanel1 property value.
     * 
     * @return JPanel
     */
    private JPanel getStatesPanel() {
        if (ivjStatesPanel == null) {
            try {
                ivjStatesPanel = new JPanel();
                ivjStatesPanel.setName("StatesPanel");
                ivjStatesPanel.setLayout(new java.awt.GridBagLayout());
                ivjStatesPanel.setMaximumSize(new java.awt.Dimension(353, 690));
                ivjStatesPanel.setPreferredSize(new java.awt.Dimension(353, 690));
                ivjStatesPanel.setBounds(0, 0, 352, 370);
                ivjStatesPanel.setMinimumSize(new java.awt.Dimension(353, 690));

                java.awt.GridBagConstraints constraintsRepeaterLabel = new java.awt.GridBagConstraints();
                constraintsRepeaterLabel.gridx = 3;
                constraintsRepeaterLabel.gridy = 1;
                constraintsRepeaterLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRepeaterLabel.insets = new java.awt.Insets(0, 5, 5, 0);
                getStatesPanel().add(getRepeaterLabel(), constraintsRepeaterLabel);

                java.awt.GridBagConstraints constraintsVariableBitsLabel = new java.awt.GridBagConstraints();
                constraintsVariableBitsLabel.gridx = 4;
                constraintsVariableBitsLabel.gridy = 1;
                constraintsVariableBitsLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsVariableBitsLabel.insets = new java.awt.Insets(0, 5, 5, 0);
                getStatesPanel().add(getVariableBitsLabel(), constraintsVariableBitsLabel);

                java.awt.GridBagConstraints constraintsRawStateColumnLabel = new java.awt.GridBagConstraints();
                constraintsRawStateColumnLabel.gridx = 1;
                constraintsRawStateColumnLabel.gridy = 1;
                constraintsRawStateColumnLabel.gridwidth = 2;
                constraintsRawStateColumnLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRawStateColumnLabel.insets = new java.awt.Insets(0, 5, 5, 0);
                getStatesPanel().add(getRawStateColumnLabel(), constraintsRawStateColumnLabel);

                java.awt.GridBagConstraints constraintsJLabelImage = new java.awt.GridBagConstraints();
                constraintsJLabelImage.gridx = 5;
                constraintsJLabelImage.gridy = 1;
                constraintsJLabelImage.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelImage.insets = new java.awt.Insets(0, 5, 5, 0);
                getStatesPanel().add(getJLabelImage(), constraintsJLabelImage);

                for (int i = 0; i < STATE_COUNT; i++) {
                    java.awt.GridBagConstraints constraintsStateNameTextField = new java.awt.GridBagConstraints();
                    constraintsStateNameTextField.gridx = 2;
                    constraintsStateNameTextField.gridy = i + 2;
                    constraintsStateNameTextField.gridwidth = 2;
                    constraintsStateNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    constraintsStateNameTextField.anchor = java.awt.GridBagConstraints.WEST;
                    constraintsStateNameTextField.weightx = 1.0;
                    constraintsStateNameTextField.insets = new java.awt.Insets(0, 5, 5, 0);
                    getStatesPanel().add(stateNameTextFields[i], constraintsStateNameTextField);

                    java.awt.GridBagConstraints constraintsRawStateLabel = new java.awt.GridBagConstraints();
                    constraintsRawStateLabel.gridx = 1;
                    constraintsRawStateLabel.gridy = i + 2;
                    constraintsRawStateLabel.anchor = java.awt.GridBagConstraints.WEST;
                    constraintsRawStateLabel.insets = new java.awt.Insets(0, 5, 5, 10);
                    getStatesPanel().add(rawStateLabels[i], constraintsRawStateLabel);

                    java.awt.GridBagConstraints constraintsForegroundColorComboBox = new java.awt.GridBagConstraints();
                    constraintsForegroundColorComboBox.gridx = 4;
                    constraintsForegroundColorComboBox.gridy = i + 2;
                    constraintsForegroundColorComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    constraintsForegroundColorComboBox.anchor = java.awt.GridBagConstraints.WEST;
                    constraintsForegroundColorComboBox.weightx = 1.0;
                    constraintsForegroundColorComboBox.insets = new java.awt.Insets(0, 5, 5, 0);
                    getStatesPanel().add(foregroundColorComboBoxes[i], constraintsForegroundColorComboBox);

                    java.awt.GridBagConstraints constraintsJButtonImage = new java.awt.GridBagConstraints();
                    constraintsJButtonImage.gridx = 5;
                    constraintsJButtonImage.gridy = i + 2;
                    constraintsJButtonImage.anchor = java.awt.GridBagConstraints.WEST;
                    constraintsJButtonImage.insets = new java.awt.Insets(0, 5, 5, 0);
                    getStatesPanel().add(imageButtons[i], constraintsJButtonImage);
                }

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjStatesPanel;
    }

    /**
     * getValue method comment.
     */
    @Override
    public Object getValue(Object val) {

        GroupState groupState = (GroupState) val;

        Object stateNumberSpinVal = getStateNumberSpinner().getValue();
        int numberOfStates = 0;
        if (stateNumberSpinVal instanceof Long) {
            numberOfStates = ((Long) stateNumberSpinVal).intValue();
        } else if (stateNumberSpinVal instanceof Integer) {
            numberOfStates = ((Integer) stateNumberSpinVal);
        }

        String stateGroupName = getStateGroupNameTextField().getText();
        if (stateGroupName != null) {
            groupState.getStateGroup().setName(stateGroupName);
        }

        groupState.getStatesVector().removeAllElements();

        State tempStateData = null;

        for (int i = 0; i < numberOfStates; i++) {
            Integer yukImgId =
                (imageButtons[i].getClientProperty("LiteYukonImage") == null) ? new Integer(YukonImage.NONE_IMAGE_ID)
                    : new Integer(((LiteYukonImage) imageButtons[i].getClientProperty("LiteYukonImage")).getImageID());

            tempStateData = new State();
            tempStateData.setState(new com.cannontech.database.db.state.State(
                groupState.getStateGroup().getStateGroupID(), new Integer(i), stateNameTextFields[i].getText(),
                new Integer(foregroundColorComboBoxes[i].getSelectedIndex()), new Integer(Colors.BLACK_ID), yukImgId));

            groupState.getStatesVector().add(tempStateData);
        }
        return val;
    }

    /**
     * Return the VariableBitsLabel property value.
     * 
     * @return JLabel
     */
    private JLabel getVariableBitsLabel() {
        if (ivjVariableBitsLabel == null) {
            try {
                ivjVariableBitsLabel = new JLabel();
                ivjVariableBitsLabel.setName("VariableBitsLabel");
                ivjVariableBitsLabel.setFont(new java.awt.Font("dialog", 0, 12));
                ivjVariableBitsLabel.setText("Color");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjVariableBitsLabel;
    }

    /**
     * Called whenever the part throws an exception.
     * 
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     * 
     * @exception java.lang.Exception The exception description.
     */
    private void initConnections() throws java.lang.Exception {

        getStateNumberSpinner().addValueListener(this);
        getStateGroupNameTextField().addCaretListener(this);
        for (int i = 0; i < STATE_COUNT; i++) {
            stateNameTextFields[i].addCaretListener(this);
            foregroundColorComboBoxes[i].addItemListener(this);
            imageButtons[i].addActionListener(this);
        }
    }

    /**
     * Initialize the class.
     */
    private void initialize() {

        rawStateLabels = new JLabel[STATE_COUNT];
        stateNameTextFields = new JTextField[STATE_COUNT];
        foregroundColorComboBoxes = new JComboBox[STATE_COUNT];
        imageButtons = new JButton[STATE_COUNT];

        for (int i = 0; i < STATE_COUNT; i++) {
            rawStateLabels[i] = buildRawStateLabel(i);
            stateNameTextFields[i] = buildStateNameTextField();
            foregroundColorComboBoxes[i] = buildForegroundColorComboBox();
            imageButtons[i] = buildJButtonImage();
        }

        try {
            setName("VersacomAddressingEditorPanel");
            setPreferredSize(new java.awt.Dimension(381, 348));
            setLayout(new java.awt.GridBagLayout());
            setSize(407, 348);
            setMinimumSize(new java.awt.Dimension(381, 348));
            setMaximumSize(new java.awt.Dimension(381, 348));

            java.awt.GridBagConstraints constraintsIdentificationPanel = new java.awt.GridBagConstraints();
            constraintsIdentificationPanel.gridx = 1;
            constraintsIdentificationPanel.gridy = 1;
            constraintsIdentificationPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsIdentificationPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsIdentificationPanel.weightx = 1.0;
            constraintsIdentificationPanel.weighty = 1.0;
            constraintsIdentificationPanel.ipadx = 397;
            constraintsIdentificationPanel.ipady = 83;
            constraintsIdentificationPanel.insets = new java.awt.Insets(9, 5, 1, 5);
            add(getIdentificationPanel(), constraintsIdentificationPanel);

            java.awt.GridBagConstraints constraintsJScrollPane = new java.awt.GridBagConstraints();
            constraintsJScrollPane.gridx = 1;
            constraintsJScrollPane.gridy = 2;
            constraintsJScrollPane.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJScrollPane.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJScrollPane.weightx = 1.0;
            constraintsJScrollPane.weighty = 1.0;
            constraintsJScrollPane.ipadx = 14;
            constraintsJScrollPane.insets = new java.awt.Insets(2, 5, 18, 5);
            add(getJScrollPane(), constraintsJScrollPane);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Method to handle events for the ItemListener interface.
     * 
     * @param e java.awt.event.ItemEvent
     */
    @Override
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        for (JComboBox foregroundColorComboBox : foregroundColorComboBoxes) {
            if (e.getSource() == foregroundColorComboBox) {
                try {
                    fireInputUpdate();
                } catch (java.lang.Throwable ivjExc) {
                    handleException(ivjExc);
                }
            }
        }
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            JFrame frame = new JFrame();
            GroupStateEditorPanel aGroupStateEditorPanel;
            aGroupStateEditorPanel = new GroupStateEditorPanel();
            frame.setContentPane(aGroupStateEditorPanel);
            frame.setSize(aGroupStateEditorPanel.getSize());
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of DataInputPanel");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * setValue method comment.
     */
    @Override
    public void setValue(Object val) {
        GroupState gs = (GroupState) val;

        String groupName = gs.getStateGroup().getName();
        Vector<State> statesVector = gs.getStatesVector();

        if (groupName != null) {
            getStateGroupNameTextField().setText(groupName);
        }

        // if we are a system reserved group or an analog group, do not allow the number of states
        // to be modified
        if (gs.getStateGroup().getStateGroupID().intValue() <= StateGroupUtils.SYSTEM_STATEGROUPID
            || gs.getStateGroup().getGroupType().equalsIgnoreCase(StateGroupUtils.GROUP_TYPE_ANALOG)) {
            getStateNumberSpinner().setEnabled(false);
            getStateNumberSpinner().setToolTipText(
                "The number of states can NOT be changed for SYSTEM RESERVED or Analog state groups");
            getStateNumberLabel().setToolTipText(
                "The number of states can NOT be changed for SYSTEM RESERVED or Analog state groups");
        }

        getStateNumberSpinner().setValue(new Integer(statesVector.size()));

        for (int i = 0; i < statesVector.size() && i < STATE_COUNT; i++) {
            rawStateLabels[i].setEnabled(true);
            stateNameTextFields[i].setEnabled(true);
            stateNameTextFields[i].setText(statesVector.get(i).getState().getText());

            foregroundColorComboBoxes[i].setEnabled(true);

            // 11 is chosen here because it is the number of color options in combo box. We'll just start
            // reusing colors if we run out.
            foregroundColorComboBoxes[i].setSelectedIndex(statesVector.get(i).getState().getForegroundColor().intValue() % 11);

            imageButtons[i].setEnabled(true);

            // set up all the Images for each state that has one
            int yukImgID = statesVector.get(i).getState().getImageID().intValue();
            if (yukImgID != YukonImage.NONE_IMAGE_ID) {
                IDatabaseCache cache = DefaultDatabaseCache.getInstance();
                LiteYukonImage liteYukImg = cache.getImages().get(yukImgID);
                // be sure we have found a matching LiteYukonImage
                if (liteYukImg != null) {
                    setImageButton(imageButtons[i], new ImageIcon(liteYukImg.getImageValue()), liteYukImg);
                }
            }
        }
    }

    /**
     * stateNumberSpinnerChanged:
     * (StateNumberSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) -->
     * GroupStateEditorPanel.fireInputUpdate()V)
     * 
     * @param arg1 com.klg.jclass.util.value.JCValueEvent
     */
    private void stateNumberSpinnerChanged(JCValueEvent arg1) {
        try {
            fireInputUpdate();

            Object stateNumberSpinVal = getStateNumberSpinner().getValue();
            Integer numberOfStates = null;
            if (stateNumberSpinVal instanceof Long) {
                numberOfStates = new Integer(((Long) stateNumberSpinVal).intValue());
            } else if (stateNumberSpinVal instanceof Integer) {
                numberOfStates = new Integer(((Integer) stateNumberSpinVal).intValue());
            }

            if (numberOfStates != null) {
                for (int i = 0; i < numberOfStates.intValue(); i++) {
                    rawStateLabels[i].setEnabled(true);
                    stateNameTextFields[i].setEnabled(true);
                    foregroundColorComboBoxes[i].setEnabled(true);
                    imageButtons[i].setEnabled(true);

                    if (stateNameTextFields[i].getText().equals("")) {
                        stateNameTextFields[i].setText("DefaultStateName" + (Integer.toString(i + 1)));
                    }
                }

                for (int i = numberOfStates.intValue(); i < stateNameTextFields.length; i++) {
                    rawStateLabels[i].setEnabled(false);
                    stateNameTextFields[i].setEnabled(false);
                    foregroundColorComboBoxes[i].setEnabled(false);
                    imageButtons[i].setEnabled(false);

                    if (stateNameTextFields[i].getText().equals("DefaultStateName" + (Integer.toString(i + 1)))) {
                        stateNameTextFields[i].setText("");
                    }
                }
            }
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Method to handle events for the JCValueListener interface.
     * 
     * @param arg1 com.klg.jclass.util.value.JCValueEvent
     */
    @Override
    public void valueChanged(JCValueEvent argValue) {
        if (argValue.getSource() == getStateNumberSpinner()) {
            stateNumberSpinnerChanged(argValue);
        }
    }

    /**
     * Method to handle events for the JCValueListener interface.
     * 
     * @param arg1 com.klg.jclass.util.value.JCValueEvent
     */
    @Override
    public void valueChanging(JCValueEvent argValue) {

    }

    private void jButtonImage_ActionPerformed(ActionEvent actionEvent) {
        JButton button = (JButton) actionEvent.getSource();

        LiteYukonImage[] yukonImages = null;
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache) {
            List<LiteYukonImage> imgList = new ArrayList<>();
            
            for (LiteYukonImage image : cache.getImages().values()) {
                if (image.getImageValue() != null) {
                    imgList.add(image);
                }
            }
            yukonImages = imgList.toArray(new LiteYukonImage[imgList.size()]);
        }

        final JDialog d = new JDialog();

        YukonImagePanel yPanel = new YukonImagePanel(yukonImages) {
            @Override
            public void disposePanel() {
                d.setVisible(false);
            }
        };

        yPanel.addDataInputPanelListener(this);

        // get our selected image id with the JButton
        LiteYukonImage liteImg = (LiteYukonImage) button.getClientProperty("LiteYukonImage");
        if (liteImg != null) {
            yPanel.setSelectedLiteYukonImage(liteImg);
        }

        d.setModal(true);
        d.setTitle("Image Selection");
        d.getContentPane().add(yPanel);
        d.setSize(800, 600);

        // set the location of the dialog to the center of the screen
        d.setLocation((getToolkit().getScreenSize().width - d.getSize().width) / 2,
            (getToolkit().getScreenSize().height - d.getSize().height) / 2);
        d.show();

        if (yPanel.getReturnResult() == YukonImagePanel.OK_OPTION) {
            setImageButton(button, yPanel.getSelectedImageIcon(), yPanel.getSelectedLiteImage());
        }

        fireInputUpdate();
    }

    @Override
    public void inputUpdate(PropertyPanelEvent event) {
        if (event.getID() == PropertyPanelEvent.EVENT_DB_INSERT || event.getID() == PropertyPanelEvent.EVENT_DB_UPDATE
            || event.getID() == PropertyPanelEvent.EVENT_DB_DELETE) {
            fireInputDataPanelEvent(event);
        } else {
            fireInputUpdate();
        }
    }

    private void setImageButton(JButton button, ImageIcon img, LiteYukonImage liteYuk) {
        if (img == null || liteYuk == null) {
            button.setText("Image...");
            button.setIcon(null);

            liteYuk = LiteYukonImage.NONE_IMAGE;
        } else {
            // strange, this will preserve the size of the button
            int width = (int) button.getPreferredSize().getWidth() - 12;

            // strange, this will preserve the size of the button
            int height = (int) button.getPreferredSize().getHeight() - 9;

            img.setImage(img.getImage().getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING));

            button.setText(null);
            button.setIcon(img);
        }

        // store our selected image id with the JButton
        button.putClientProperty("LiteYukonImage", liteYuk);
    }

}