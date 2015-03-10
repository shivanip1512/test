package com.cannontech.loadcontrol.popup;

import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.plaf.metal.MetalComboBoxEditor;

import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.yukon.IDatabaseCache;

public class GroupMsgPanel extends DataInputPanel {
    private JLabel ivjPercentLabel = null;
    private JComboBox<LiteYukonPAObject> ivjJComboBoxAltRoute = null;
    private JComboBox<Integer> ivjJComboBoxPeriodCnt = null;
    private JComboBox<String> ivjJComboBoxPeriodLength = null;
    private JLabel ivjJLabelAltRoute = null;
    private JLabel ivjJLabelCyclePercent = null;
    private JLabel ivjJLabelPeriodCount = null;
    private JLabel ivjJLabelPeriodLength = null;
    private JTextField ivjJTextFieldCyclePercent = null;

    public GroupMsgPanel() {
        super();
        initialize();
    }

    private JComboBox<LiteYukonPAObject> getJComboBoxAltRoute() {
        if (ivjJComboBoxAltRoute == null) {
            try {
                ivjJComboBoxAltRoute = new JComboBox<LiteYukonPAObject>();
                ivjJComboBoxAltRoute.setName("JComboBoxAltRoute");
                ivjJComboBoxAltRoute.setEditor(new MetalComboBoxEditor.UIResource());
                ivjJComboBoxAltRoute.setRenderer(new BasicComboBoxRenderer.UIResource());

                IDatabaseCache cache = DefaultDatabaseCache.getInstance();
                synchronized (cache) {
                    List<LiteYukonPAObject> routes = cache.getAllRoutes();
                   
                    for (LiteYukonPAObject route : routes) {
                        ivjJComboBoxAltRoute.addItem(route);
                    }
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxAltRoute;
    }

    private JComboBox<Integer> getJComboBoxPeriodCnt() {
        if (ivjJComboBoxPeriodCnt == null) {
            try {
                ivjJComboBoxPeriodCnt = new JComboBox<Integer>();
                ivjJComboBoxPeriodCnt.setName("JComboBoxPeriodCnt");
                ivjJComboBoxPeriodCnt.setEditor(new MetalComboBoxEditor.UIResource());
                ivjJComboBoxPeriodCnt.setRenderer(new BasicComboBoxRenderer.UIResource());

                // period count
                for (int i = 1; i < 49; i++) {
                    ivjJComboBoxPeriodCnt.addItem(i);
                }

                // default the period
                ivjJComboBoxPeriodCnt.setSelectedItem(new Integer(8));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxPeriodCnt;
    }

    private JComboBox<String> getJComboBoxPeriodLength() {
        if (ivjJComboBoxPeriodLength == null) {
            try {
                ivjJComboBoxPeriodLength = new JComboBox<String>();
                ivjJComboBoxPeriodLength.setName("JComboBoxPeriodLength");
                ivjJComboBoxPeriodLength.setEditor(new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource());
                ivjJComboBoxPeriodLength.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource());

                // 1 ~ 60 minutes
                for (int i = 1; i <= 60; i++) {
                    ivjJComboBoxPeriodLength.addItem(i + (i == 1 ? " minute" : " minutes"));
                }

                // default
                ivjJComboBoxPeriodLength.setSelectedItem("30 minutes");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxPeriodLength;
    }

    private JLabel getJLabelAltRoute() {
        if (ivjJLabelAltRoute == null) {
            try {
                ivjJLabelAltRoute = new JLabel();
                ivjJLabelAltRoute.setName("JLabelAltRoute");
                ivjJLabelAltRoute.setText("Alternate Route:");
                ivjJLabelAltRoute.setMaximumSize(new java.awt.Dimension(103, 19));
                ivjJLabelAltRoute.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelAltRoute.setMinimumSize(new java.awt.Dimension(103, 19));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelAltRoute;
    }

    private JLabel getJLabelCyclePercent() {
        if (ivjJLabelCyclePercent == null) {
            try {
                ivjJLabelCyclePercent = new JLabel();
                ivjJLabelCyclePercent.setName("JLabelCyclePercent");
                ivjJLabelCyclePercent.setText("Cycle Percent:");
                ivjJLabelCyclePercent.setMaximumSize(new java.awt.Dimension(92, 19));
                ivjJLabelCyclePercent.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelCyclePercent.setMinimumSize(new java.awt.Dimension(92, 19));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelCyclePercent;
    }

    private JLabel getJLabelPeriodCount() {
        if (ivjJLabelPeriodCount == null) {
            try {
                ivjJLabelPeriodCount = new JLabel();
                ivjJLabelPeriodCount.setName("JLabelPeriodCount");
                ivjJLabelPeriodCount.setText("Period Count:");
                ivjJLabelPeriodCount.setMaximumSize(new java.awt.Dimension(87, 19));
                ivjJLabelPeriodCount.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelPeriodCount.setMinimumSize(new java.awt.Dimension(87, 19));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelPeriodCount;
    }

    private JLabel getJLabelPeriodLength() {
        if (ivjJLabelPeriodLength == null) {
            try {
                ivjJLabelPeriodLength = new JLabel();
                ivjJLabelPeriodLength.setName("JLabelPeriodLength");
                ivjJLabelPeriodLength.setText("Period Length:");
                ivjJLabelPeriodLength.setMaximumSize(new java.awt.Dimension(93, 19));
                ivjJLabelPeriodLength.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelPeriodLength.setMinimumSize(new java.awt.Dimension(93, 19));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelPeriodLength;
    }

    private JTextField getJTextFieldCyclePercent() {
        if (ivjJTextFieldCyclePercent == null) {
            try {
                ivjJTextFieldCyclePercent = new JTextField();
                ivjJTextFieldCyclePercent.setName("JTextFieldCyclePercent");
                ivjJTextFieldCyclePercent.setHighlighter(new BasicTextUI.BasicHighlighter());
                ivjJTextFieldCyclePercent.setColumns(3);
                ivjJTextFieldCyclePercent.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjJTextFieldCyclePercent.setMinimumSize(new java.awt.Dimension(4, 23));

                ivjJTextFieldCyclePercent.setDocument(new LongRangeDocument(0, 100));

                // default
                ivjJTextFieldCyclePercent.setText("50");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldCyclePercent;
    }

    private JLabel getPercentLabel() {
        if (ivjPercentLabel == null) {
            try {
                ivjPercentLabel = new JLabel();
                ivjPercentLabel.setName("PercentLabel");
                ivjPercentLabel.setText("%");
                ivjPercentLabel.setMaximumSize(new java.awt.Dimension(12, 19));
                ivjPercentLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjPercentLabel.setMinimumSize(new java.awt.Dimension(12, 19));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPercentLabel;
    }

    @Override
    public Object getValue(Object val) {
        LMCommand cmd = (LMCommand) val;
        if (cmd == null) {
            cmd = new LMCommand();
        }

        Integer cnt = (Integer) getJComboBoxPeriodCnt().getSelectedItem();
        Integer length = SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxPeriodLength());
        Integer percent = Integer.valueOf(getJTextFieldCyclePercent().getText());

        // the alt route may or may not be available
        LiteYukonPAObject liteRoute = null;
        if (getJComboBoxAltRoute().isVisible()) {
            liteRoute = (LiteYukonPAObject) getJComboBoxAltRoute().getSelectedItem();
        }

        if (getJComboBoxPeriodCnt().isVisible()) {
            cmd.setNumber(percent.intValue()); // cycle percent
        }

        if (getJComboBoxPeriodLength().isVisible()) {
            cmd.setValue(length.doubleValue()); // period length in seconds
        }

        if (getJTextFieldCyclePercent().isVisible()) {
            cmd.setCount(cnt.intValue()); // number of cycle periods
        }

        cmd.setAuxid((liteRoute == null ? 0 : liteRoute.getYukonID())); // this auxid will be used for the alt routeID soon
        return cmd;
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
     * Initialize the class.
     */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("GroupMsgPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(317, 107);

            java.awt.GridBagConstraints constraintsJTextFieldCyclePercent = new java.awt.GridBagConstraints();
            constraintsJTextFieldCyclePercent.gridx = 2;
            constraintsJTextFieldCyclePercent.gridy = 1;
            constraintsJTextFieldCyclePercent.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJTextFieldCyclePercent.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJTextFieldCyclePercent.weightx = 1.0;
            constraintsJTextFieldCyclePercent.ipadx = 43;
            constraintsJTextFieldCyclePercent.insets = new java.awt.Insets(4, 5, 1, 2);
            add(getJTextFieldCyclePercent(), constraintsJTextFieldCyclePercent);

            java.awt.GridBagConstraints constraintsPercentLabel = new java.awt.GridBagConstraints();
            constraintsPercentLabel.gridx = 3;
            constraintsPercentLabel.gridy = 1;
            constraintsPercentLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsPercentLabel.insets = new java.awt.Insets(6, 3, 3, 126);
            add(getPercentLabel(), constraintsPercentLabel);

            java.awt.GridBagConstraints constraintsJLabelCyclePercent = new java.awt.GridBagConstraints();
            constraintsJLabelCyclePercent.gridx = 1;
            constraintsJLabelCyclePercent.gridy = 1;
            constraintsJLabelCyclePercent.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelCyclePercent.ipadx = 20;
            constraintsJLabelCyclePercent.insets = new java.awt.Insets(6, 6, 3, 4);
            add(getJLabelCyclePercent(), constraintsJLabelCyclePercent);

            java.awt.GridBagConstraints constraintsJLabelPeriodCount = new java.awt.GridBagConstraints();
            constraintsJLabelPeriodCount.gridx = 1;
            constraintsJLabelPeriodCount.gridy = 3;
            constraintsJLabelPeriodCount.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelPeriodCount.ipadx = 25;
            constraintsJLabelPeriodCount.insets = new java.awt.Insets(3, 6, 3, 4);
            add(getJLabelPeriodCount(), constraintsJLabelPeriodCount);

            java.awt.GridBagConstraints constraintsJLabelPeriodLength = new java.awt.GridBagConstraints();
            constraintsJLabelPeriodLength.gridx = 1;
            constraintsJLabelPeriodLength.gridy = 2;
            constraintsJLabelPeriodLength.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelPeriodLength.ipadx = 19;
            constraintsJLabelPeriodLength.insets = new java.awt.Insets(3, 6, 3, 4);
            add(getJLabelPeriodLength(), constraintsJLabelPeriodLength);

            java.awt.GridBagConstraints constraintsJLabelAltRoute = new java.awt.GridBagConstraints();
            constraintsJLabelAltRoute.gridx = 1;
            constraintsJLabelAltRoute.gridy = 4;
            constraintsJLabelAltRoute.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelAltRoute.ipadx = 9;
            constraintsJLabelAltRoute.insets = new java.awt.Insets(3, 6, 7, 4);
            add(getJLabelAltRoute(), constraintsJLabelAltRoute);

            java.awt.GridBagConstraints constraintsJComboBoxPeriodLength = new java.awt.GridBagConstraints();
            constraintsJComboBoxPeriodLength.gridx = 2;
            constraintsJComboBoxPeriodLength.gridy = 2;
            constraintsJComboBoxPeriodLength.gridwidth = 2;
            constraintsJComboBoxPeriodLength.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxPeriodLength.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxPeriodLength.weightx = 1.0;
            constraintsJComboBoxPeriodLength.ipadx = 57;
            constraintsJComboBoxPeriodLength.insets = new java.awt.Insets(1, 5, 1, 7);
            add(getJComboBoxPeriodLength(), constraintsJComboBoxPeriodLength);

            java.awt.GridBagConstraints constraintsJComboBoxPeriodCnt = new java.awt.GridBagConstraints();
            constraintsJComboBoxPeriodCnt.gridx = 2;
            constraintsJComboBoxPeriodCnt.gridy = 3;
            constraintsJComboBoxPeriodCnt.gridwidth = 2;
            constraintsJComboBoxPeriodCnt.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxPeriodCnt.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxPeriodCnt.weightx = 1.0;
            constraintsJComboBoxPeriodCnt.ipadx = 57;
            constraintsJComboBoxPeriodCnt.insets = new java.awt.Insets(1, 5, 1, 7);
            add(getJComboBoxPeriodCnt(), constraintsJComboBoxPeriodCnt);

            java.awt.GridBagConstraints constraintsJComboBoxAltRoute = new java.awt.GridBagConstraints();
            constraintsJComboBoxAltRoute.gridx = 2;
            constraintsJComboBoxAltRoute.gridy = 4;
            constraintsJComboBoxAltRoute.gridwidth = 2;
            constraintsJComboBoxAltRoute.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxAltRoute.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxAltRoute.weightx = 1.0;
            constraintsJComboBoxAltRoute.ipadx = 57;
            constraintsJComboBoxAltRoute.insets = new java.awt.Insets(1, 5, 5, 7);
            add(getJComboBoxAltRoute(), constraintsJComboBoxAltRoute);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // do not show the route info by default
        isRouteVisible(false);
    }

    public void isRouteVisible(boolean val_) {
        getJLabelAltRoute().setVisible(val_);
        getJComboBoxAltRoute().setVisible(val_);
    }

    @Override
    public void setValue(Object val) {
    }
}