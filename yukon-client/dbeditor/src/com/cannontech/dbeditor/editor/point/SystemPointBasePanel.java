package com.cannontech.dbeditor.editor.point;

import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.SystemPoint;

public class SystemPointBasePanel extends DataInputPanel implements ActionListener {
    private JComboBox<String> archiveIntervalComboBox = null;
    private JLabel archiveIntervalLabel = null;
    private JComboBox<String> archiveTypeComboBox = null;
    private JLabel archiveTypeLabel = null;
    private JPanel archivePanel = null;

    public SystemPointBasePanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getArchiveTypeComboBox()) {
                this.archiveTypeComboBox_ActionPerformed(e);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void archiveTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        fireInputUpdate();

        if (((String) getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointArchiveType.NONE.getPointArchiveTypeName()) || ((String) getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointArchiveType.ON_CHANGE.getPointArchiveTypeName()) || ((String) getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointArchiveType.ON_UPDATE.getPointArchiveTypeName())) {
            getArchiveIntervalLabel().setEnabled(false);
            getArchiveIntervalComboBox().setEnabled(false);
        } else {
            getArchiveIntervalLabel().setEnabled(true);
            getArchiveIntervalComboBox().setEnabled(true);
            getArchiveIntervalComboBox().setSelectedItem("5 minute");

            if (PointArchiveType.ON_TIMER_OR_UPDATE.getDisplayName().equalsIgnoreCase(((String) getArchiveTypeComboBox().getSelectedItem()))) {
                getArchiveIntervalComboBox().setSelectedItem("Daily");
            }
        }

        return;
    }

    private JComboBox<String> getArchiveIntervalComboBox() {
        if (archiveIntervalComboBox == null) {
            try {
                archiveIntervalComboBox = new JComboBox<String>();
                archiveIntervalComboBox.setName("ArchiveIntervalComboBox");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return archiveIntervalComboBox;
    }

    private javax.swing.JLabel getArchiveIntervalLabel() {
        if (archiveIntervalLabel == null) {
            try {
                archiveIntervalLabel = new javax.swing.JLabel();
                archiveIntervalLabel.setName("ArchiveIntervalLabel");
                archiveIntervalLabel.setText("Interval:");
                archiveIntervalLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return archiveIntervalLabel;
    }

    private JComboBox<String> getArchiveTypeComboBox() {
        if (archiveTypeComboBox == null) {
            try {
                archiveTypeComboBox = new JComboBox<String>();
                archiveTypeComboBox.setName("ArchiveTypeComboBox");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return archiveTypeComboBox;
    }

    private javax.swing.JLabel getArchiveTypeLabel() {
        if (archiveTypeLabel == null) {
            try {
                archiveTypeLabel = new javax.swing.JLabel();
                archiveTypeLabel.setName("ArchiveTypeLabel");
                archiveTypeLabel.setText("Data Type:");
                archiveTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return archiveTypeLabel;
    }

    private javax.swing.JPanel getJPanelArchive() {
        if (archivePanel == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
                ivjLocalBorder.setTitle("Archive");
                archivePanel = new javax.swing.JPanel();
                archivePanel.setName("JPanelArchive");
                archivePanel.setBorder(ivjLocalBorder);
                archivePanel.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsArchiveTypeLabel = new java.awt.GridBagConstraints();
                constraintsArchiveTypeLabel.gridx = 0;
                constraintsArchiveTypeLabel.gridy = 0;
                constraintsArchiveTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveTypeLabel.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveTypeLabel(), constraintsArchiveTypeLabel);

                java.awt.GridBagConstraints constraintsArchiveIntervalLabel = new java.awt.GridBagConstraints();
                constraintsArchiveIntervalLabel.gridx = 0;
                constraintsArchiveIntervalLabel.gridy = 1;
                constraintsArchiveIntervalLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveIntervalLabel.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveIntervalLabel(), constraintsArchiveIntervalLabel);

                java.awt.GridBagConstraints constraintsArchiveIntervalComboBox = new java.awt.GridBagConstraints();
                constraintsArchiveIntervalComboBox.gridx = 1;
                constraintsArchiveIntervalComboBox.gridy = 1;
                constraintsArchiveIntervalComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsArchiveIntervalComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveIntervalComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveIntervalComboBox(), constraintsArchiveIntervalComboBox);

                java.awt.GridBagConstraints constraintsArchiveTypeComboBox = new java.awt.GridBagConstraints();
                constraintsArchiveTypeComboBox.gridx = 1;
                constraintsArchiveTypeComboBox.gridy = 0;
                constraintsArchiveTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsArchiveTypeComboBox.weightx = 1.0;
                constraintsArchiveTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveTypeComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveTypeComboBox(), constraintsArchiveTypeComboBox);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return archivePanel;
    }


    @Override
    public Object getValue(Object val) {
        SystemPoint point = (SystemPoint) val;

        String selectedArchiveType = getArchiveTypeComboBox().getSelectedItem().toString();
        point.getPoint().setArchiveType(PointArchiveType.getByDisplayName(selectedArchiveType));
        point.getPoint().setArchiveInterval(SwingUtil.getIntervalComboBoxSecondsValue(getArchiveIntervalComboBox()));

        return point;
    }

    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    private void initConnections() throws java.lang.Exception {
        getArchiveTypeComboBox().addActionListener(this);
        getArchiveIntervalComboBox().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("SystemBasePanel");
            setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints constraintsJPanelArchive = new java.awt.GridBagConstraints();
            constraintsJPanelArchive.gridx = 0;
            constraintsJPanelArchive.gridy = 1;
            constraintsJPanelArchive.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelArchive.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelArchive.insets = new java.awt.Insets(0,0,0,0);
            constraintsJPanelArchive.weighty = 1.0;
            constraintsJPanelArchive.weightx = 1.0;
            add(getJPanelArchive(), constraintsJPanelArchive);

            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // Put a border around the System section of panel
        javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("System Summary");
        border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
        setBorder(border);

        // Load the Archive Type combo box with default possible values
        getArchiveTypeComboBox().addItem(PointArchiveType.NONE.getDisplayName());
        getArchiveTypeComboBox().addItem(PointArchiveType.ON_CHANGE.getDisplayName());
        getArchiveTypeComboBox().addItem(PointArchiveType.ON_TIMER.getDisplayName());
        getArchiveTypeComboBox().addItem(PointArchiveType.ON_UPDATE.getDisplayName());
        getArchiveTypeComboBox().addItem(PointArchiveType.ON_TIMER_OR_UPDATE.getDisplayName());

        // Load the Archive Interval combo box with default possible values
        getArchiveIntervalComboBox().addItem("1 second");
        getArchiveIntervalComboBox().addItem("2 second");
        getArchiveIntervalComboBox().addItem("5 second");
        getArchiveIntervalComboBox().addItem("10 second");
        getArchiveIntervalComboBox().addItem("15 second");
        getArchiveIntervalComboBox().addItem("30 second");
        getArchiveIntervalComboBox().addItem("1 minute");
        getArchiveIntervalComboBox().addItem("2 minute");
        getArchiveIntervalComboBox().addItem("3 minute");
        getArchiveIntervalComboBox().addItem("5 minute");
        getArchiveIntervalComboBox().addItem("10 minute");
        getArchiveIntervalComboBox().addItem("15 minute");
        getArchiveIntervalComboBox().addItem("30 minute");
        getArchiveIntervalComboBox().addItem("1 hour");
        getArchiveIntervalComboBox().addItem("2 hour");
        getArchiveIntervalComboBox().addItem("6 hour");
        getArchiveIntervalComboBox().addItem("12 hour");
        getArchiveIntervalComboBox().addItem("Daily");
        getArchiveIntervalComboBox().addItem("Weekly");
        getArchiveIntervalComboBox().addItem("Monthly");
        getArchiveIntervalComboBox().setSelectedItem("5 minute");
    }

    @Override
    public boolean isInputValid() {
        return true;
    }

    @Override
    public void setValue(Object val) {
        SystemPoint point = (SystemPoint) val;

        PointArchiveType archiveType = point.getPoint().getArchiveType();
        Integer archiveInteger = point.getPoint().getArchiveInterval();

        getArchiveIntervalLabel().setEnabled(false);
        getArchiveIntervalComboBox().setEnabled(false);

        for (int i = 0; i < getArchiveTypeComboBox().getModel().getSize(); i++) {
            if (getArchiveTypeComboBox().getItemAt(i).equalsIgnoreCase(archiveType.getDisplayName())) {
                getArchiveTypeComboBox().setSelectedIndex(i);
                if (getArchiveIntervalComboBox().isEnabled())
                    SwingUtil.setIntervalComboBoxSelectedItem(getArchiveIntervalComboBox(), archiveInteger.intValue());
                break;
            }
        }
    }
}