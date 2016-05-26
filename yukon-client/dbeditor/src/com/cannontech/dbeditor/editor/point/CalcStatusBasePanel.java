package com.cannontech.dbeditor.editor.point;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class CalcStatusBasePanel extends DataInputPanel implements ActionListener, ItemListener {
    private JComboBox<LiteState> ivjInitialStateComboBox = null;
    private JLabel ivjInitialStateLabel = null;
    private JComboBox<LiteStateGroup> ivjStateTableComboBox = null;
    private JLabel ivjStateTableLabel = null;
    private JCheckBox ivjArchiveCheckBox = null;
    private final IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private JComboBox<String> ivjPeriodicRateComboBox = null;
    private JLabel ivjPeriodicRateLabel = null;
    private JComboBox<String> ivjUpdateTypeComboBox = null;
    private JLabel ivjUpdateTypeLabel = null;

    class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            try {
                if (e.getSource() == CalcStatusBasePanel.this.getStateTableComboBox() || e.getSource() == CalcStatusBasePanel.this.getInitialStateComboBox() || e.getSource() == CalcStatusBasePanel.this.getArchiveCheckBox() || e.getSource() == CalcStatusBasePanel.this.getPeriodicRateComboBox()) {
                    CalcStatusBasePanel.this.fireInputUpdate();
                }

                if (e.getSource() == CalcStatusBasePanel.this.getUpdateTypeComboBox()) {
                    CalcStatusBasePanel.this.updateTypeComboBox_ActionPerformed(e);
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        };

        @Override
        public void itemStateChanged(java.awt.event.ItemEvent e) {
            if (e.getSource() == CalcStatusBasePanel.this.getStateTableComboBox()) {
                try {
                    CalcStatusBasePanel.this.stateTableComboBox_ItemStateChanged(e);
                } catch (java.lang.Throwable ivjExc) {
                    handleException(ivjExc);
                }
            }
        };
    };

    public CalcStatusBasePanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getStateTableComboBox() ||
                    e.getSource() == getInitialStateComboBox() ||
                    e.getSource() == getArchiveCheckBox()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private javax.swing.JCheckBox getArchiveCheckBox() {
        if (ivjArchiveCheckBox == null) {
            try {
                ivjArchiveCheckBox = new javax.swing.JCheckBox();
                ivjArchiveCheckBox.setName("ArchiveCheckBox");
                ivjArchiveCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjArchiveCheckBox.setText("Archive Data");
                ivjArchiveCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
                ivjArchiveCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjArchiveCheckBox;
    }

    private JComboBox<LiteState> getInitialStateComboBox() {
        if (ivjInitialStateComboBox == null) {
            try {
                ivjInitialStateComboBox = new JComboBox<LiteState>();
                ivjInitialStateComboBox.setName("InitialStateComboBox");
                ivjInitialStateComboBox.setPreferredSize(new java.awt.Dimension(75, 24));
                ivjInitialStateComboBox.setMinimumSize(new java.awt.Dimension(75, 24));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjInitialStateComboBox;
    }

    private JLabel getInitialStateLabel() {
        if (ivjInitialStateLabel == null) {
            try {
                ivjInitialStateLabel = new JLabel();
                ivjInitialStateLabel.setName("InitialStateLabel");
                ivjInitialStateLabel.setText("Initial State:");
                ivjInitialStateLabel.setMaximumSize(new java.awt.Dimension(73, 16));
                ivjInitialStateLabel.setPreferredSize(new java.awt.Dimension(73, 16));
                ivjInitialStateLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjInitialStateLabel.setMinimumSize(new java.awt.Dimension(73, 16));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjInitialStateLabel;
    }

    private JComboBox<String> getPeriodicRateComboBox() {
        if (ivjPeriodicRateComboBox == null) {
            try {
                ivjPeriodicRateComboBox = new JComboBox<String>();
                ivjPeriodicRateComboBox.setName("PeriodicRateComboBox");
                ivjPeriodicRateComboBox.setFont(new java.awt.Font("dialog", 0, 12));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPeriodicRateComboBox;
    }

    private JLabel getPeriodicRateLabel() {
        if (ivjPeriodicRateLabel == null) {
            try {
                ivjPeriodicRateLabel = new JLabel();
                ivjPeriodicRateLabel.setName("PeriodicRateLabel");
                ivjPeriodicRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjPeriodicRateLabel.setText("Rate:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPeriodicRateLabel;
    }

    private JComboBox<LiteStateGroup> getStateTableComboBox() {
        if (ivjStateTableComboBox == null) {
            try {
                ivjStateTableComboBox = new JComboBox<LiteStateGroup>();
                ivjStateTableComboBox.setName("StateTableComboBox");
                ivjStateTableComboBox.setPreferredSize(new java.awt.Dimension(125, 24));
                ivjStateTableComboBox.setMinimumSize(new java.awt.Dimension(125, 24));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjStateTableComboBox;
    }

    private JLabel getStateTableLabel() {
        if (ivjStateTableLabel == null) {
            try {
                ivjStateTableLabel = new JLabel();
                ivjStateTableLabel.setName("StateTableLabel");
                ivjStateTableLabel.setText("State Group:");
                ivjStateTableLabel.setMaximumSize(new java.awt.Dimension(77, 16));
                ivjStateTableLabel.setPreferredSize(new java.awt.Dimension(77, 16));
                ivjStateTableLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjStateTableLabel.setMinimumSize(new java.awt.Dimension(77, 16));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjStateTableLabel;
    }

    private JComboBox<String> getUpdateTypeComboBox() {
        if (ivjUpdateTypeComboBox == null) {
            try {
                ivjUpdateTypeComboBox = new JComboBox<String>();
                ivjUpdateTypeComboBox.setName("UpdateTypeComboBox");
                ivjUpdateTypeComboBox.setFont(new java.awt.Font("dialog", 0, 12));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUpdateTypeComboBox;
    }

    private JLabel getUpdateTypeLabel() {
        if (ivjUpdateTypeLabel == null) {
            try {
                ivjUpdateTypeLabel = new JLabel();
                ivjUpdateTypeLabel.setName("UpdateTypeLabel");
                ivjUpdateTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjUpdateTypeLabel.setText("Update Type:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUpdateTypeLabel;
    }

    @Override
    public Object getValue(Object val) {

        CalcStatusPoint point = (CalcStatusPoint) val;

        LiteStateGroup stateGroup = (LiteStateGroup) getStateTableComboBox().getSelectedItem();
        LiteState initialState = (LiteState) getInitialStateComboBox().getSelectedItem();

        point.getPoint().setStateGroupID(stateGroup.getStateGroupID());
        point.getPointStatus().setInitialState(initialState.getStateRawState());

        if (getArchiveCheckBox().isSelected()) {
            point.getPoint().setArchiveType(PointArchiveType.ON_CHANGE);
        } else {
            point.getPoint().setArchiveType(PointArchiveType.NONE);
        }

        point.getCalcBase().setUpdateType((String) getUpdateTypeComboBox().getSelectedItem());
        point.getCalcBase().setPeriodicRate(SwingUtil.getIntervalComboBoxSecondsValue(getPeriodicRateComboBox()));

        return point;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getStateTableComboBox().addActionListener(ivjEventHandler);
        getInitialStateComboBox().addActionListener(ivjEventHandler);
        getStateTableComboBox().addItemListener(ivjEventHandler);
        getArchiveCheckBox().addActionListener(ivjEventHandler);
        getUpdateTypeComboBox().addActionListener(ivjEventHandler);
        getPeriodicRateComboBox().addActionListener(ivjEventHandler);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("CalcStatusBasePanel");
            setPreferredSize(new java.awt.Dimension(300, 102));
            setLayout(new java.awt.GridBagLayout());
            setSize(427, 141);
            setMinimumSize(new java.awt.Dimension(0, 0));

            java.awt.GridBagConstraints constraintsStateTableLabel = new java.awt.GridBagConstraints();
            constraintsStateTableLabel.gridx = 1;
            constraintsStateTableLabel.gridy = 1;
            constraintsStateTableLabel.gridwidth = 2;
            constraintsStateTableLabel.ipadx = 22;
            constraintsStateTableLabel.insets = new java.awt.Insets(14, 7, 7, 2);
            add(getStateTableLabel(), constraintsStateTableLabel);

            java.awt.GridBagConstraints constraintsInitialStateLabel = new java.awt.GridBagConstraints();
            constraintsInitialStateLabel.gridx = 1;
            constraintsInitialStateLabel.gridy = 2;
            constraintsInitialStateLabel.gridwidth = 2;
            constraintsInitialStateLabel.ipadx = 26;
            constraintsInitialStateLabel.insets = new java.awt.Insets(8, 7, 6, 2);
            add(getInitialStateLabel(), constraintsInitialStateLabel);

            java.awt.GridBagConstraints constraintsStateTableComboBox = new java.awt.GridBagConstraints();
            constraintsStateTableComboBox.gridx = 3;
            constraintsStateTableComboBox.gridy = 1;
            constraintsStateTableComboBox.gridwidth = 3;
            constraintsStateTableComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsStateTableComboBox.weightx = 1.0;
            constraintsStateTableComboBox.ipadx = 85;
            constraintsStateTableComboBox.insets = new java.awt.Insets(10, 2, 3, 107);
            add(getStateTableComboBox(), constraintsStateTableComboBox);

            java.awt.GridBagConstraints constraintsInitialStateComboBox = new java.awt.GridBagConstraints();
            constraintsInitialStateComboBox.gridx = 3;
            constraintsInitialStateComboBox.gridy = 2;
            constraintsInitialStateComboBox.gridwidth = 3;
            constraintsInitialStateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsInitialStateComboBox.weightx = 1.0;
            constraintsInitialStateComboBox.ipadx = 135;
            constraintsInitialStateComboBox.insets = new java.awt.Insets(4, 2, 2, 107);
            add(getInitialStateComboBox(), constraintsInitialStateComboBox);

            java.awt.GridBagConstraints constraintsArchiveCheckBox = new java.awt.GridBagConstraints();
            constraintsArchiveCheckBox.gridx = 1;
            constraintsArchiveCheckBox.gridy = 3;
            constraintsArchiveCheckBox.gridwidth = 3;
            constraintsArchiveCheckBox.ipadx = 49;
            constraintsArchiveCheckBox.ipady = -7;
            constraintsArchiveCheckBox.insets = new java.awt.Insets(3, 7, 5, 70);
            add(getArchiveCheckBox(), constraintsArchiveCheckBox);

            java.awt.GridBagConstraints constraintsUpdateTypeLabel = new java.awt.GridBagConstraints();
            constraintsUpdateTypeLabel.gridx = 1;
            constraintsUpdateTypeLabel.gridy = 4;
            constraintsUpdateTypeLabel.insets = new java.awt.Insets(9, 7, 18, 4);
            add(getUpdateTypeLabel(), constraintsUpdateTypeLabel);

            java.awt.GridBagConstraints constraintsUpdateTypeComboBox = new java.awt.GridBagConstraints();
            constraintsUpdateTypeComboBox.gridx = 2;
            constraintsUpdateTypeComboBox.gridy = 4;
            constraintsUpdateTypeComboBox.gridwidth = 2;
            constraintsUpdateTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsUpdateTypeComboBox.weightx = 1.0;
            constraintsUpdateTypeComboBox.ipadx = 4;
            constraintsUpdateTypeComboBox.insets = new java.awt.Insets(6, 4, 15, 2);
            add(getUpdateTypeComboBox(), constraintsUpdateTypeComboBox);

            java.awt.GridBagConstraints constraintsPeriodicRateLabel = new java.awt.GridBagConstraints();
            constraintsPeriodicRateLabel.gridx = 4;
            constraintsPeriodicRateLabel.gridy = 4;
            constraintsPeriodicRateLabel.insets = new java.awt.Insets(9, 2, 18, 4);
            add(getPeriodicRateLabel(), constraintsPeriodicRateLabel);

            java.awt.GridBagConstraints constraintsPeriodicRateComboBox = new java.awt.GridBagConstraints();
            constraintsPeriodicRateComboBox.gridx = 5;
            constraintsPeriodicRateComboBox.gridy = 4;
            constraintsPeriodicRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsPeriodicRateComboBox.weightx = 1.0;
            constraintsPeriodicRateComboBox.ipadx = 4;
            constraintsPeriodicRateComboBox.insets = new java.awt.Insets(6, 4, 15, 21);
            add(getPeriodicRateComboBox(), constraintsPeriodicRateComboBox);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        TitledBorder border = new TitledBorder("Calculation Summary");
        border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
        setBorder(border);

        // Load the Update Type combo box with default possible values
        getUpdateTypeComboBox().addItem("On First Change");
        getUpdateTypeComboBox().addItem("On All Change");
        getUpdateTypeComboBox().addItem("On Timer");
        getUpdateTypeComboBox().addItem("On Timer+Change");
        getUpdateTypeComboBox().addItem("Historical");

        // Load the Periodic Rate combo box with default possible values
        getPeriodicRateComboBox().addItem("1 second");
        getPeriodicRateComboBox().addItem("2 second");
        getPeriodicRateComboBox().addItem("5 second");
        getPeriodicRateComboBox().addItem("10 second");
        getPeriodicRateComboBox().addItem("15 second");
        getPeriodicRateComboBox().addItem("30 second");
        getPeriodicRateComboBox().addItem("1 minute");
        getPeriodicRateComboBox().addItem("2 minute");
        getPeriodicRateComboBox().addItem("3 minute");
        getPeriodicRateComboBox().addItem("5 minute");
        getPeriodicRateComboBox().addItem("10 minute");
        getPeriodicRateComboBox().addItem("15 minute");
        getPeriodicRateComboBox().addItem("30 minute");
        getPeriodicRateComboBox().addItem("1 hour");
        getPeriodicRateComboBox().addItem("2 hour");
        getPeriodicRateComboBox().addItem("6 hour");
        getPeriodicRateComboBox().addItem("12 hour");
        getPeriodicRateComboBox().addItem("1 day");
    }

    @Override
    public boolean isInputValid() {
        return true;
    }

    @Override
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        if (e.getSource() == getStateTableComboBox()) {
            try {
                this.stateTableComboBox_ItemStateChanged(e);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
    }

    private void loadStateComboBoxes(int stateGroupID) {
        if (getInitialStateComboBox().getItemCount() > 0) {
            getInitialStateComboBox().removeAllItems();
        }

        LiteStateGroup stateGroup = YukonSpringHook.getBean(StateGroupDao.class).getAllStateGroups().get(new Integer(stateGroupID));

        List<LiteState> statesList = stateGroup.getStatesList();
        for (LiteState ls : statesList) {
            getInitialStateComboBox().addItem(ls);
        }
    }

    @Override
    public void setValue(Object val) {

        CalcStatusPoint calcPoint = (CalcStatusPoint) val;

        int stateGroupID = calcPoint.getPoint().getStateGroupID().intValue();

        // Load all the state groups
        IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteStateGroup> allStateGroups = YukonSpringHook.getBean(StateGroupDao.class).getAllStateGroups();

            // Load the state table combo box
            for (LiteStateGroup grp : allStateGroups) {
                getStateTableComboBox().addItem(grp);
                if (grp.getStateGroupID() == stateGroupID) {
                    getStateTableComboBox().setSelectedItem(grp);
                }
            }
        }

        loadStateComboBoxes(stateGroupID);

        int initialRawState = calcPoint.getPointStatus().getInitialState().intValue();

        // Select the appropriate initial state
        for (int y = 0; y < getInitialStateComboBox().getModel().getSize(); y++) {
            if (getInitialStateComboBox().getItemAt(y).getStateRawState() == initialRawState) {
                getInitialStateComboBox().setSelectedIndex(y);
                break;
            }
        }

        getArchiveCheckBox().setSelected(calcPoint.getPoint().getArchiveType() == PointArchiveType.ON_CHANGE);

        String updateType = calcPoint.getCalcBase().getUpdateType();
        Integer periodicRate = calcPoint.getCalcBase().getPeriodicRate();

        getPeriodicRateLabel().setEnabled(false);
        getPeriodicRateComboBox().setEnabled(false);

        for (int i = 0; i < getUpdateTypeComboBox().getModel().getSize(); i++) {
            if (getUpdateTypeComboBox().getItemAt(i).equalsIgnoreCase(updateType)) {
                getUpdateTypeComboBox().setSelectedIndex(i);
                if (getPeriodicRateComboBox().isEnabled())
                    SwingUtil.setIntervalComboBoxSelectedItem(getPeriodicRateComboBox(), periodicRate.intValue());
                break;
            }
        }
    }

    public void stateTableComboBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {

        if (itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            LiteStateGroup selected = (LiteStateGroup) getStateTableComboBox().getSelectedItem();
            loadStateComboBoxes(selected.getStateGroupID());
        }
    }

    public void updateTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

        String val = getUpdateTypeComboBox().getSelectedItem().toString();

        getPeriodicRateLabel().setEnabled("On Timer".equalsIgnoreCase(val) || "On Timer+Change".equalsIgnoreCase(val));
        getPeriodicRateComboBox().setEnabled("On Timer".equalsIgnoreCase(val) || "On Timer+Change".equalsIgnoreCase(val));

        fireInputUpdate();
        return;
    }
}
