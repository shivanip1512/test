package com.cannontech.dbeditor.wizard.point;

import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.dbeditor.DatabaseEditor;

public class PointCalcBaseSettingsPanel extends DataInputPanel implements ActionListener {
    private JComboBox<String> ivjPeriodicRateComboBox = null;
    private JLabel ivjPeriodicRateLabel = null;
    private JComboBox<String> ivjUpdateTypeComboBox = null;
    private JLabel ivjUpdateTypeLabel = null;

    public PointCalcBaseSettingsPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getUpdateTypeComboBox()) {
                fireInputUpdate();
                if (((String) getUpdateTypeComboBox().getSelectedItem()).equalsIgnoreCase("On Timer") || 
                        ((String) getUpdateTypeComboBox().getSelectedItem()).equalsIgnoreCase("On Timer+Change")) {
                    getPeriodicRateLabel().setEnabled(true);
                    getPeriodicRateComboBox().setEnabled(true);
                } else {
                    getPeriodicRateLabel().setEnabled(false);
                    getPeriodicRateComboBox().setEnabled(false);
                }
            }
            
            if (e.getSource() == getPeriodicRateComboBox()) {
                fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JComboBox<String> getPeriodicRateComboBox() {
        if (ivjPeriodicRateComboBox == null) {
            try {
                ivjPeriodicRateComboBox = new JComboBox<>();
                ivjPeriodicRateComboBox.setName("PeriodicRateComboBox");
                ivjPeriodicRateComboBox.setFont(new java.awt.Font("dialog", 0, 14));

                // Load the Periodic Rate combo box with default possible values
                ivjPeriodicRateComboBox.addItem("1 second");
                ivjPeriodicRateComboBox.addItem("2 second");
                ivjPeriodicRateComboBox.addItem("5 second");
                ivjPeriodicRateComboBox.addItem("10 second");
                ivjPeriodicRateComboBox.addItem("15 second");
                ivjPeriodicRateComboBox.addItem("30 second");
                ivjPeriodicRateComboBox.addItem("1 minute");
                ivjPeriodicRateComboBox.addItem("2 minute");
                ivjPeriodicRateComboBox.addItem("3 minute");
                ivjPeriodicRateComboBox.addItem("5 minute");
                ivjPeriodicRateComboBox.addItem("10 minute");
                ivjPeriodicRateComboBox.addItem("15 minute");
                ivjPeriodicRateComboBox.addItem("30 minute");
                ivjPeriodicRateComboBox.addItem("1 hour");
                ivjPeriodicRateComboBox.addItem("2 hour");
                ivjPeriodicRateComboBox.addItem("6 hour");
                ivjPeriodicRateComboBox.addItem("12 hour");
                ivjPeriodicRateComboBox.addItem("24 hour");
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
                ivjPeriodicRateLabel.setText("Periodic Rate:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPeriodicRateLabel;
    }

    private JComboBox<String> getUpdateTypeComboBox() {
        if (ivjUpdateTypeComboBox == null) {
            try {
                ivjUpdateTypeComboBox = new JComboBox<>();
                ivjUpdateTypeComboBox.setName("UpdateTypeComboBox");
                ivjUpdateTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));

                // Load the Update Type combo box with default possible values
                ivjUpdateTypeComboBox.addItem("On First Change");
                ivjUpdateTypeComboBox.addItem("On All Change");
                ivjUpdateTypeComboBox.addItem("On Timer");
                ivjUpdateTypeComboBox.addItem("On Timer+Change");
                ivjUpdateTypeComboBox.addItem("Constant");
                ivjUpdateTypeComboBox.addItem("Historical");
                ivjUpdateTypeComboBox.addItem("Backfilling");
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

        if (val instanceof CalcStatusPoint) {
            CalcStatusPoint point = (CalcStatusPoint) val;

            point.getCalcBase().setUpdateType((String) getUpdateTypeComboBox().getSelectedItem());
            point.getCalcBase().setPeriodicRate(SwingUtil.getIntervalComboBoxSecondsValue(getPeriodicRateComboBox()));
            point.getPoint().setPointOffset(0);
            if (point.getPoint().getStateGroupID() == null) {
                point.getPoint().setStateGroupID(StateGroupUtils.STATEGROUP_TWO_STATE_STATUS);
            }
        } else {
           CalculatedPoint point = (CalculatedPoint) val;

            point.getCalcBase().setUpdateType((String) getUpdateTypeComboBox().getSelectedItem());
            point.getCalcBase().setPeriodicRate(SwingUtil.getIntervalComboBoxSecondsValue(getPeriodicRateComboBox()));
            point.getPoint().setStateGroupID(StateGroupUtils.STATEGROUP_ANALOG);
            point.getPoint().setPointOffset(new Integer(0));

            List<UnitOfMeasure> unitMeasures = UnitOfMeasure.allValidValues();
            // Better be at least 1!
            point.getPointUnit().setUomID(unitMeasures.get(0).getId());
            point.getPointUnit().setDecimalPlaces(DatabaseEditor.getDecimalPlaces());
        }
        return val;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        // com.cannontech.clientutils.CTILogger.error( exception.getMessage(),
        // exception );;
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getUpdateTypeComboBox().addActionListener(this);
        getPeriodicRateComboBox().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("PointCalcBaseSettingsPanel");
            setPreferredSize(new java.awt.Dimension(350, 200));
            setLayout(new java.awt.GridBagLayout());
            setSize(373, 225);

            java.awt.GridBagConstraints constraintsUpdateTypeLabel = new java.awt.GridBagConstraints();
            constraintsUpdateTypeLabel.gridx = 0;
            constraintsUpdateTypeLabel.gridy = 0;
            constraintsUpdateTypeLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsUpdateTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsUpdateTypeLabel.insets = new java.awt.Insets(5, 0, 5, 0);
            add(getUpdateTypeLabel(), constraintsUpdateTypeLabel);

            java.awt.GridBagConstraints constraintsPeriodicRateLabel = new java.awt.GridBagConstraints();
            constraintsPeriodicRateLabel.gridx = 0;
            constraintsPeriodicRateLabel.gridy = 1;
            constraintsPeriodicRateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsPeriodicRateLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsPeriodicRateLabel.insets = new java.awt.Insets(5, 0, 5, 0);
            add(getPeriodicRateLabel(), constraintsPeriodicRateLabel);

            java.awt.GridBagConstraints constraintsUpdateTypeComboBox = new java.awt.GridBagConstraints();
            constraintsUpdateTypeComboBox.gridx = 1;
            constraintsUpdateTypeComboBox.gridy = 0;
            constraintsUpdateTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsUpdateTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsUpdateTypeComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
            add(getUpdateTypeComboBox(), constraintsUpdateTypeComboBox);

            java.awt.GridBagConstraints constraintsPeriodicRateComboBox = new java.awt.GridBagConstraints();
            constraintsPeriodicRateComboBox.gridx = 1;
            constraintsPeriodicRateComboBox.gridy = 1;
            constraintsPeriodicRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsPeriodicRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsPeriodicRateComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
            add(getPeriodicRateComboBox(), constraintsPeriodicRateComboBox);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        getPeriodicRateLabel().setEnabled(false);
        getPeriodicRateComboBox().setEnabled(false);
    }

    @Override
    public void setValue(Object val) {
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getUpdateTypeComboBox().requestFocus();
            }
        });
    }
}