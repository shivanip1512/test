package com.cannontech.dbeditor.editor.point;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JComboBox;

import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.gui.util.CalcComponentTableModel;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class PointCalcComponentEditorPanel extends DataInputPanel implements ActionListener, MouseListener {

    private class DevicePointOperandLite {
        private LiteYukonPAObject liteDevice = null;
        private LitePoint litePoint = null;

        public DevicePointOperandLite(LiteYukonPAObject ld, LitePoint lp) {
            liteDevice = ld;
            litePoint = lp;
        }

        public LiteYukonPAObject getLiteDevice() {
            return this.liteDevice;
        }

        public LitePoint getLitePoint() {
            return this.litePoint;
        }

        @Override
        public String toString() {
            return getLiteDevice().toString() + "/" + getLitePoint().toString();
        }
    }

    private javax.swing.JButton ivjAddComponentButton = null;
    private javax.swing.JButton ivjRemoveComponentButton = null;
    private JComboBox<String> ivjComponentTypeComboBox = null;
    private JComboBox<LiteYukonPAObject> ivjDeviceComboBox = null;
    private JComboBox<LitePoint> ivjPointComboBox = null;
    private javax.swing.JScrollPane ivjComponentsScrollPane = null;
    private javax.swing.JTable ivjComponentsTable = null;
    private javax.swing.JTextField ivjConstantTextField = null;
    private javax.swing.JButton ivjEditComponentButton = null;
    private JComboBox<String> ivjOperationFunctionComboBox = null;
    private JComboBox<LiteBaseline> selectBaselineComboBox = null;
    private javax.swing.JLabel ivjOperandLabel = null;
    private javax.swing.JLabel ivjOperationFunctionLabel = null;
    private javax.swing.JLabel ivjTypeLabel = null;
    // private java.util.List points = null;
    private javax.swing.JCheckBox ivjUsePointCheckBox = null;
    private javax.swing.JPanel ivjJPanelOperations = null;
    private java.util.Vector<LiteBaseline> basilHolder = null;
    private Integer currentlyMappedBaselineID = null;

    public PointCalcComponentEditorPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getComponentTypeComboBox()) {
                this.componentTypeComboBox_ActionPerformed(e);
            }
            if (e.getSource() == getDeviceComboBox()) {
                this.deviceComboBox_ActionPerformed(e);
            }
            if (e.getSource() == getUsePointCheckBox()) {
                this.usePointCheckBox_ActionPerformed(e);
            }
            if (e.getSource() == getEditComponentButton()) {
                this.editComponentButton_ActionPerformed(e);
            }
            if (e.getSource() == getRemoveComponentButton()) {
                this.removeComponentButton_ActionPerformed(e);
            }
            if (e.getSource() == getAddComponentButton()) {
                this.addComponentButton_ActionPerformed(e);
            }
            if (e.getSource() == getOperationFunctionComboBox())
                functionComboBox_ActionPerformed(e);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void addComponentButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

        boolean componentValid = false;
        java.util.Vector calcComponentVector = new java.util.Vector(3);

        calcComponentVector.addElement(getComponentTypeComboBox().getSelectedItem());
        String selType = getComponentTypeComboBox().getSelectedItem().toString();

        if (CalcComponentTypes.OPERATION_COMP_TYPE.equalsIgnoreCase(selType)) {
            calcComponentVector.addElement(new DevicePointOperandLite((LiteYukonPAObject) getDeviceComboBox().getSelectedItem(),
                                                                      (LitePoint) getPointComboBox().getSelectedItem()));
            componentValid = true;
        } else if (CalcComponentTypes.CONSTANT_COMP_TYPE.equalsIgnoreCase(selType)) {
            try {
                Double constantValue = new Double(getConstantTextField().getText());
                calcComponentVector.addElement(constantValue.toString());
                componentValid = true;
            } catch (NumberFormatException nfe) {
                com.cannontech.clientutils.CTILogger.error(nfe.getMessage(), nfe);
            }
        } else {
            if (getUsePointCheckBox().isSelected())
                calcComponentVector.addElement(new DevicePointOperandLite((LiteYukonPAObject) getDeviceComboBox().getSelectedItem(),
                                                                          (LitePoint) getPointComboBox().getSelectedItem()));
            else
                calcComponentVector.addElement("");

            componentValid = true;
        }

        if (((String) getOperationFunctionComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION)) {
            currentlyMappedBaselineID = new Integer(((com.cannontech.database.data.lite.LiteBaseline) getSelectBaselineComboBox().getSelectedItem()).getBaselineID());

        }

        calcComponentVector.addElement(getOperationFunctionComboBox().getSelectedItem());

        if (componentValid) {
            getTableModel().addRow(calcComponentVector);
            fireInputUpdate();
            repaint();
        }
    }

    public void componentsTable_MousePressed(java.awt.event.MouseEvent mouseEvent) {
        if (getComponentsTable().getSelectedRowCount() == 1) {
            int selectedRow = getComponentsTable().getSelectedRow();
            String type = (String) getComponentsTable().getModel().getValueAt(selectedRow, 0);
            Object operand = getComponentsTable().getModel().getValueAt(selectedRow, 1);
            String operation = (String) getComponentsTable().getModel().getValueAt(selectedRow, 2);
            getComponentTypeComboBox().setSelectedItem(type);

            if (CalcComponentTypes.OPERATION_COMP_TYPE.equalsIgnoreCase(type)) {
                getDeviceComboBox().setSelectedItem(((DevicePointOperandLite) operand).getLiteDevice());
                getPointComboBox().setSelectedItem(((DevicePointOperandLite) operand).getLitePoint());
            } else if (CalcComponentTypes.CONSTANT_COMP_TYPE.equalsIgnoreCase(type)) {
                getConstantTextField().setText(operand.toString());
            } else if (CalcComponentTypes.FUNCTION_COMP_TYPE.equalsIgnoreCase(type)) {
                if (operand instanceof DevicePointOperandLite) {
                    getDeviceComboBox().setSelectedItem(((DevicePointOperandLite) operand).getLiteDevice());
                    getPointComboBox().setSelectedItem(((DevicePointOperandLite) operand).getLitePoint());
                } else
                    getUsePointCheckBox().setSelected(false);
            }
            getOperationFunctionComboBox().setSelectedItem(operation);
        }
    }

    public void componentTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

        if (getOperationFunctionComboBox().getModel().getSize() > 0)
            getOperationFunctionComboBox().removeAllItems();

        if (((String) getComponentTypeComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.OPERATION_COMP_TYPE)) {
            getDeviceComboBox().setVisible(true);
            getPointComboBox().setVisible(true);
            getDeviceComboBox().setEnabled(true);
            getPointComboBox().setEnabled(true);
            getConstantTextField().setVisible(false);
            getUsePointCheckBox().setVisible(false);

            for (int i = 0; i < CalcComponentTypes.CALC_OPERATIONS.length; i++)
                getOperationFunctionComboBox().addItem(CalcComponentTypes.CALC_OPERATIONS[i]);

            revalidate();
            repaint();
        } else if (((String) getComponentTypeComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.CONSTANT_COMP_TYPE)) {
            // we want the textfield and the comboBox to be the same size so the
            // layout manager does not need to do any work!
            getConstantTextField().setSize(getDeviceComboBox().getSize());
            getPointComboBox().setVisible(true);
            getPointComboBox().setEnabled(false);
            getConstantTextField().setVisible(true);
            getDeviceComboBox().setVisible(false);
            getDeviceComboBox().setEnabled(true);
            getUsePointCheckBox().setVisible(false);

            for (int i = 0; i < CalcComponentTypes.CALC_OPERATIONS.length; i++)
                getOperationFunctionComboBox().addItem(CalcComponentTypes.CALC_OPERATIONS[i]);

            revalidate();
            repaint();
        } else if (((String) getComponentTypeComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.FUNCTION_COMP_TYPE)) {
            getDeviceComboBox().setVisible(true);
            getPointComboBox().setVisible(true);
            getConstantTextField().setVisible(false);
            getUsePointCheckBox().setVisible(true);
            getPointComboBox().setEnabled(true);
            getUsePointCheckBox().setSelected(true);

            YukonSelectionList funcList = YukonSpringHook.getBean(YukonListDao.class)
                                                         .getYukonSelectionList(CalcComponentTypes.CALC_FUNCTION_LIST_ID);
            if (funcList == null) {
                for (int i = 0; i < CalcComponentTypes.CALC_FUNCTIONS.length; i++)
                    getOperationFunctionComboBox().addItem(CalcComponentTypes.CALC_FUNCTIONS[i]);
            } else {
                for (int i = 0; i < funcList.getYukonListEntries().size(); i++)
                    getOperationFunctionComboBox().addItem(funcList.getYukonListEntries().get(i).toString());
            }

            revalidate();
            repaint();
        }

        return;
    }

    public void functionComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

        if (getOperationFunctionComboBox().getModel().getSize() > 0) {
            if (((String) getOperationFunctionComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION)) {
                getSelectBaselineComboBox().removeAllItems();

                for (int i = 0; i < getBasilHolder().size(); i++) {
                    getSelectBaselineComboBox().addItem(getBasilHolder().elementAt(i));
                }
                getSelectBaselineComboBox().setVisible(true);

                if (currentlyMappedBaselineID != null) {
                    for (int j = 0; j < getBasilHolder().size(); j++) {
                        if (getBasilHolder().get(j).getBaselineID() == currentlyMappedBaselineID.intValue()) {
                            getSelectBaselineComboBox().setSelectedIndex(j);
                        }
                    }
                }

                revalidate();
                repaint();
            } else {
                getSelectBaselineComboBox().setVisible(false);
                revalidate();
                repaint();
            }
        }
        return;
    }

    /**
     * Comment
     */
    public void deviceComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        int deviceID = ((LiteYukonPAObject) getDeviceComboBox().getSelectedItem()).getYukonID();

        if (getPointComboBox().getModel().getSize() > 0)
            getPointComboBox().removeAllItems();

        List<LitePoint> paoPoints = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(deviceID);
        for (LitePoint point : paoPoints) {
            getPointComboBox().addItem(point);
        }
    }

    /**
     * Comment
     */
    public void editComponentButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

        boolean componentValid = false;
        int selectedRow = getComponentsTable().getSelectedRow();
        java.util.Vector calcComponentVector = new java.util.Vector(3);

        calcComponentVector.addElement(getComponentTypeComboBox().getSelectedItem());
        String selType = getComponentTypeComboBox().getSelectedItem().toString();

        if (CalcComponentTypes.OPERATION_COMP_TYPE.equalsIgnoreCase(selType)) {
            calcComponentVector.addElement(new DevicePointOperandLite((LiteYukonPAObject) getDeviceComboBox().getSelectedItem(),
                                                                      (LitePoint) getPointComboBox().getSelectedItem()));
            componentValid = true;
        } else if (CalcComponentTypes.CONSTANT_COMP_TYPE.equalsIgnoreCase(selType)) {
            try {
                Double constantValue = new Double(getConstantTextField().getText());
                calcComponentVector.addElement(constantValue.toString());
                componentValid = true;
            } catch (NumberFormatException nfe) {
                com.cannontech.clientutils.CTILogger.error(nfe.getMessage(), nfe);
            }
        } else {
            if (getUsePointCheckBox().isSelected())
                calcComponentVector.addElement(new DevicePointOperandLite((LiteYukonPAObject) getDeviceComboBox().getSelectedItem(),
                                                                          (LitePoint) getPointComboBox().getSelectedItem()));
            else
                calcComponentVector.addElement("");

            componentValid = true;
        }
        calcComponentVector.addElement(getOperationFunctionComboBox().getSelectedItem());

        if (((String) getOperationFunctionComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION)) {
            currentlyMappedBaselineID = new Integer(((com.cannontech.database.data.lite.LiteBaseline) getSelectBaselineComboBox().getSelectedItem()).getBaselineID());
        } else
            currentlyMappedBaselineID = null;

        if (componentValid) {
            getTableModel().editRow(selectedRow, calcComponentVector);
            fireInputUpdate();
            repaint();
        }
    }

    private javax.swing.JButton getAddComponentButton() {
        if (ivjAddComponentButton == null) {
            try {
                ivjAddComponentButton = new javax.swing.JButton();
                ivjAddComponentButton.setName("AddComponentButton");
                ivjAddComponentButton.setFont(new java.awt.Font("dialog", 0, 14));
                ivjAddComponentButton.setText("Add");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAddComponentButton;
    }

    private javax.swing.JScrollPane getComponentsScrollPane() {
        if (ivjComponentsScrollPane == null) {
            try {
                ivjComponentsScrollPane = new javax.swing.JScrollPane();
                ivjComponentsScrollPane.setName("ComponentsScrollPane");
                ivjComponentsScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                ivjComponentsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                ivjComponentsScrollPane.setPreferredSize(new java.awt.Dimension(200, 180));
                ivjComponentsScrollPane.setFont(new java.awt.Font("dialog", 0, 14));
                ivjComponentsScrollPane.setMinimumSize(new java.awt.Dimension(200, 180));
                getComponentsScrollPane().setViewportView(getComponentsTable());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjComponentsScrollPane;
    }

    private javax.swing.JTable getComponentsTable() {
        if (ivjComponentsTable == null) {
            try {
                ivjComponentsTable = new javax.swing.JTable();
                ivjComponentsTable.setName("ComponentsTable");
                getComponentsScrollPane().setColumnHeaderView(ivjComponentsTable.getTableHeader());
                ivjComponentsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
                ivjComponentsTable.setPreferredSize(new java.awt.Dimension(200, 8000));
                ivjComponentsTable.setBounds(0, 0, 396, 177);
                ivjComponentsTable.setMaximumSize(new java.awt.Dimension(32767, 32767));
                ivjComponentsTable.setPreferredScrollableViewportSize(new java.awt.Dimension(200, 8000));
                ivjComponentsTable.createDefaultColumnsFromModel();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjComponentsTable;
    }

    private JComboBox<String> getComponentTypeComboBox() {
        if (ivjComponentTypeComboBox == null) {
            try {
                ivjComponentTypeComboBox = new JComboBox<String>();
                ivjComponentTypeComboBox.setName("ComponentTypeComboBox");
                ivjComponentTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjComponentTypeComboBox.setMinimumSize(new java.awt.Dimension(130, 28));
                ivjComponentTypeComboBox.setMaximumSize(new java.awt.Dimension(130, 28));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjComponentTypeComboBox;
    }

    private javax.swing.JTextField getConstantTextField() {
        if (ivjConstantTextField == null) {
            try {
                ivjConstantTextField = new javax.swing.JTextField();
                ivjConstantTextField.setName("ConstantTextField");
                ivjConstantTextField.setMaximumSize(new java.awt.Dimension(130, 24));
                ivjConstantTextField.setColumns(0);
                ivjConstantTextField.setPreferredSize(new java.awt.Dimension(130, 24));
                ivjConstantTextField.setFont(new java.awt.Font("dialog", 0, 14));
                ivjConstantTextField.setMinimumSize(new java.awt.Dimension(130, 24));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjConstantTextField;
    }

    private JComboBox<LiteYukonPAObject> getDeviceComboBox() {
        if (ivjDeviceComboBox == null) {
            try {
                ivjDeviceComboBox = new JComboBox<LiteYukonPAObject>();
                ivjDeviceComboBox.setName("DeviceComboBox");
                ivjDeviceComboBox.setOpaque(true);
                ivjDeviceComboBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjDeviceComboBox.setMinimumSize(new java.awt.Dimension(130, 28));

                IDatabaseCache cache = DefaultDatabaseCache.getInstance();
                synchronized (cache) {
                    List<LiteYukonPAObject> devices = cache.getAllDevices();

                    for (int i = 0; i < devices.size(); i++) {
                        LiteYukonPAObject pao = devices.get(i);

                        ivjDeviceComboBox.addItem(pao);

                        if (i == 0) {
                            List<LitePoint> paoPoints = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(pao.getYukonID());
                            for (LitePoint point : paoPoints) {
                                getPointComboBox().addItem(point);
                            }
                        }
                    }
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDeviceComboBox;
    }

    private javax.swing.JButton getEditComponentButton() {
        if (ivjEditComponentButton == null) {
            try {
                ivjEditComponentButton = new javax.swing.JButton();
                ivjEditComponentButton.setName("EditComponentButton");
                ivjEditComponentButton.setText("Update");
                ivjEditComponentButton.setMaximumSize(new java.awt.Dimension(150, 31));
                ivjEditComponentButton.setPreferredSize(new java.awt.Dimension(130, 31));
                ivjEditComponentButton.setFont(new java.awt.Font("dialog", 0, 14));
                ivjEditComponentButton.setContentAreaFilled(true);
                ivjEditComponentButton.setMinimumSize(new java.awt.Dimension(130, 31));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjEditComponentButton;
    }

    private javax.swing.JPanel getJPanelOperations() {
        if (ivjJPanelOperations == null) {
            try {
                ivjJPanelOperations = new javax.swing.JPanel();
                ivjJPanelOperations.setName("JPanelOperations");
                ivjJPanelOperations.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsAddComponentButton = new java.awt.GridBagConstraints();
                constraintsAddComponentButton.gridx = 1;
                constraintsAddComponentButton.gridy = 4;
                constraintsAddComponentButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsAddComponentButton.ipadx = 71;
                constraintsAddComponentButton.insets = new java.awt.Insets(6, 5, 4, 5);
                getJPanelOperations().add(getAddComponentButton(), constraintsAddComponentButton);

                java.awt.GridBagConstraints constraintsRemoveComponentButton = new java.awt.GridBagConstraints();
                constraintsRemoveComponentButton.gridx = 3;
                constraintsRemoveComponentButton.gridy = 4;
                constraintsRemoveComponentButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRemoveComponentButton.ipadx = 43;
                constraintsRemoveComponentButton.insets = new java.awt.Insets(6, 5, 4, 3);
                getJPanelOperations().add(getRemoveComponentButton(), constraintsRemoveComponentButton);

                java.awt.GridBagConstraints constraintsComponentTypeComboBox = new java.awt.GridBagConstraints();
                constraintsComponentTypeComboBox.gridx = 1;
                constraintsComponentTypeComboBox.gridy = 2;
                constraintsComponentTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsComponentTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsComponentTypeComboBox.weightx = 1.0;
                constraintsComponentTypeComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOperations().add(getComponentTypeComboBox(), constraintsComponentTypeComboBox);

                java.awt.GridBagConstraints constraintsDeviceComboBox = new java.awt.GridBagConstraints();
                constraintsDeviceComboBox.gridx = 2;
                constraintsDeviceComboBox.gridy = 2;
                constraintsDeviceComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsDeviceComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsDeviceComboBox.weightx = 1.0;
                constraintsDeviceComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOperations().add(getDeviceComboBox(), constraintsDeviceComboBox);

                java.awt.GridBagConstraints constraintsPointComboBox = new java.awt.GridBagConstraints();
                constraintsPointComboBox.gridx = 2;
                constraintsPointComboBox.gridy = 3;
                constraintsPointComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsPointComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsPointComboBox.weightx = 1.0;
                constraintsPointComboBox.ipady = 1;
                constraintsPointComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOperations().add(getPointComboBox(), constraintsPointComboBox);

                java.awt.GridBagConstraints constraintsOperationFunctionComboBox = new java.awt.GridBagConstraints();
                constraintsOperationFunctionComboBox.gridx = 3;
                constraintsOperationFunctionComboBox.gridy = 2;
                constraintsOperationFunctionComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsOperationFunctionComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsOperationFunctionComboBox.weightx = 1.0;
                constraintsOperationFunctionComboBox.insets = new java.awt.Insets(5, 5, 5, 3);
                getJPanelOperations().add(getOperationFunctionComboBox(), constraintsOperationFunctionComboBox);

                java.awt.GridBagConstraints constraintsSelectBaselineComboBox = new java.awt.GridBagConstraints();
                constraintsSelectBaselineComboBox.gridx = 3;
                constraintsOperationFunctionComboBox.gridy = 3;
                constraintsSelectBaselineComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsSelectBaselineComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsSelectBaselineComboBox.weightx = 1.0;
                constraintsSelectBaselineComboBox.insets = new java.awt.Insets(5, 5, 5, 3);
                getJPanelOperations().add(getSelectBaselineComboBox(), constraintsSelectBaselineComboBox);

                java.awt.GridBagConstraints constraintsEditComponentButton = new java.awt.GridBagConstraints();
                constraintsEditComponentButton.gridx = 2;
                constraintsEditComponentButton.gridy = 4;
                constraintsEditComponentButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsEditComponentButton.insets = new java.awt.Insets(5, 5, 3, 5);
                getJPanelOperations().add(getEditComponentButton(), constraintsEditComponentButton);

                java.awt.GridBagConstraints constraintsConstantTextField = new java.awt.GridBagConstraints();
                constraintsConstantTextField.gridx = 2;
                constraintsConstantTextField.gridy = 2;
                constraintsConstantTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsConstantTextField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsConstantTextField.weightx = 1.0;
                constraintsConstantTextField.insets = new java.awt.Insets(7, 5, 7, 5);
                getJPanelOperations().add(getConstantTextField(), constraintsConstantTextField);

                java.awt.GridBagConstraints constraintsTypeLabel = new java.awt.GridBagConstraints();
                constraintsTypeLabel.gridx = 1;
                constraintsTypeLabel.gridy = 1;
                constraintsTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsTypeLabel.ipadx = 98;
                constraintsTypeLabel.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOperations().add(getTypeLabel(), constraintsTypeLabel);

                java.awt.GridBagConstraints constraintsOperandLabel = new java.awt.GridBagConstraints();
                constraintsOperandLabel.gridx = 2;
                constraintsOperandLabel.gridy = 1;
                constraintsOperandLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsOperandLabel.ipadx = 74;
                constraintsOperandLabel.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOperations().add(getOperandLabel(), constraintsOperandLabel);

                java.awt.GridBagConstraints constraintsOperationFunctionLabel = new java.awt.GridBagConstraints();
                constraintsOperationFunctionLabel.gridx = 3;
                constraintsOperationFunctionLabel.gridy = 1;
                constraintsOperationFunctionLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsOperationFunctionLabel.ipadx = 67;
                constraintsOperationFunctionLabel.insets = new java.awt.Insets(5, 5, 5, 3);
                getJPanelOperations().add(getOperationFunctionLabel(), constraintsOperationFunctionLabel);

                java.awt.GridBagConstraints constraintsUsePointCheckBox = new java.awt.GridBagConstraints();
                constraintsUsePointCheckBox.gridx = 1;
                constraintsUsePointCheckBox.gridy = 3;
                constraintsUsePointCheckBox.ipadx = 45;
                constraintsUsePointCheckBox.insets = new java.awt.Insets(7, 5, 8, 5);
                getJPanelOperations().add(getUsePointCheckBox(), constraintsUsePointCheckBox);
                getSelectBaselineComboBox().setVisible(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelOperations;
    }

    private javax.swing.JLabel getOperandLabel() {
        if (ivjOperandLabel == null) {
            try {
                ivjOperandLabel = new javax.swing.JLabel();
                ivjOperandLabel.setName("OperandLabel");
                ivjOperandLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjOperandLabel.setText("Operand");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjOperandLabel;
    }

    private JComboBox<String> getOperationFunctionComboBox() {
        if (ivjOperationFunctionComboBox == null) {
            try {
                ivjOperationFunctionComboBox = new JComboBox<String>();
                ivjOperationFunctionComboBox.setName("OperationFunctionComboBox");
                ivjOperationFunctionComboBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjOperationFunctionComboBox.setMinimumSize(new java.awt.Dimension(130, 28));
                ivjOperationFunctionComboBox.setMaximumSize(new java.awt.Dimension(130, 28));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjOperationFunctionComboBox;
    }

    private JComboBox<LiteBaseline> getSelectBaselineComboBox() {
        if (selectBaselineComboBox == null) {
            try {
                selectBaselineComboBox = new JComboBox<LiteBaseline>();
                selectBaselineComboBox.setName("SelectBaselineComboBox");
                selectBaselineComboBox.setFont(new java.awt.Font("dialog", 0, 14));
                selectBaselineComboBox.setMinimumSize(new java.awt.Dimension(130, 28));
                selectBaselineComboBox.setMaximumSize(new java.awt.Dimension(130, 28));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return selectBaselineComboBox;
    }

    private java.util.Vector<LiteBaseline> getBasilHolder() {
        if (basilHolder == null)
            basilHolder = new java.util.Vector<LiteBaseline>();
        return basilHolder;
    }

    private javax.swing.JLabel getOperationFunctionLabel() {
        if (ivjOperationFunctionLabel == null) {
            try {
                ivjOperationFunctionLabel = new javax.swing.JLabel();
                ivjOperationFunctionLabel.setName("OperationFunctionLabel");
                ivjOperationFunctionLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjOperationFunctionLabel.setText("Operation");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjOperationFunctionLabel;
    }

    private JComboBox<LitePoint> getPointComboBox() {
        if (ivjPointComboBox == null) {
            try {
                ivjPointComboBox = new JComboBox<LitePoint>();
                ivjPointComboBox.setName("PointComboBox");
                ivjPointComboBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjPointComboBox.setMinimumSize(new java.awt.Dimension(130, 27));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPointComboBox;
    }

    private javax.swing.JButton getRemoveComponentButton() {
        if (ivjRemoveComponentButton == null) {
            try {
                ivjRemoveComponentButton = new javax.swing.JButton();
                ivjRemoveComponentButton.setName("RemoveComponentButton");
                ivjRemoveComponentButton.setFont(new java.awt.Font("dialog", 0, 14));
                ivjRemoveComponentButton.setText("Remove");
                ivjRemoveComponentButton.setContentAreaFilled(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRemoveComponentButton;
    }

    private CalcComponentTableModel getTableModel() {
        return ((CalcComponentTableModel) getComponentsTable().getModel());
    }

    private javax.swing.JLabel getTypeLabel() {
        if (ivjTypeLabel == null) {
            try {
                ivjTypeLabel = new javax.swing.JLabel();
                ivjTypeLabel.setName("TypeLabel");
                ivjTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjTypeLabel.setText("Type");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTypeLabel;
    }

    private javax.swing.JCheckBox getUsePointCheckBox() {
        if (ivjUsePointCheckBox == null) {
            try {
                ivjUsePointCheckBox = new javax.swing.JCheckBox();
                ivjUsePointCheckBox.setName("UsePointCheckBox");
                ivjUsePointCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjUsePointCheckBox.setText("Use Point");
                ivjUsePointCheckBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUsePointCheckBox;
    }

    @Override
    public Object getValue(Object val) {
        com.cannontech.database.data.point.PointBase calcPoint;

        if (val instanceof CalcStatusPoint) {
            calcPoint = (CalcStatusPoint) val;
        } else {
            calcPoint = (CalculatedPoint) val;
        }

        Integer pointID = calcPoint.getPoint().getPointID();
        CalcComponent newCalcComponent = null;
        List<CalcComponent> calcComponents = null;

        if (getComponentsTable().getRowCount() > 0) {
            calcComponents = Lists.newArrayList();
            String type = null;
            Object operand = null;
            String operation = null;

            for (int i = 0; i < getComponentsTable().getRowCount(); i++) {
                type = (String) getComponentsTable().getModel().getValueAt(i, 0);
                operand = getComponentsTable().getModel().getValueAt(i, 1);
                operation = (String) getComponentsTable().getModel().getValueAt(i, 2);

                newCalcComponent = new CalcComponent(pointID,
                                                     new Integer(i + 1),
                                                     type,
                                                     new Integer(0),
                                                     CtiUtilities.STRING_NONE,
                                                     new Double(0.0),
                                                     CtiUtilities.STRING_NONE);

                if (CalcComponentTypes.OPERATION_COMP_TYPE.equalsIgnoreCase(type)) {
                    newCalcComponent.setComponentPointID(new Integer(((DevicePointOperandLite) operand).getLitePoint().getPointID()));
                    newCalcComponent.setOperation(operation);
                } else if (CalcComponentTypes.CONSTANT_COMP_TYPE.equalsIgnoreCase(type)) {
                    newCalcComponent.setConstant(new Double(operand.toString()));
                    newCalcComponent.setOperation(operation);
                } else if (CalcComponentTypes.FUNCTION_COMP_TYPE.equalsIgnoreCase(type)) {
                    if (operand instanceof DevicePointOperandLite)
                        newCalcComponent.setComponentPointID(new Integer(((DevicePointOperandLite) operand).getLitePoint().getPointID()));

                    newCalcComponent.setFunctionName(operation);

                    if (newCalcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION) && currentlyMappedBaselineID != null) {
                        if (val instanceof CalcStatusPoint) {
                            ((CalcStatusPoint) calcPoint).getCalcBaselinePoint().setBaselineID(currentlyMappedBaselineID);
                            ((CalcStatusPoint) calcPoint).setBaselineAssigned(true);
                        } else {
                            ((CalculatedPoint) calcPoint).getCalcBaselinePoint().setBaselineID(currentlyMappedBaselineID);
                            ((CalculatedPoint) calcPoint).setBaselineAssigned(true);
                        }
                    }

                }

                calcComponents.add(newCalcComponent);
            }
        }

        if (calcPoint instanceof CalcStatusPoint)
            ((CalcStatusPoint) calcPoint).setCalcComponents(calcComponents);
        else
            ((CalculatedPoint) calcPoint).setCalcComponents(calcComponents);

        return val;

    }

    private void handleException(Throwable exception) {
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getComponentTypeComboBox().addActionListener(this);
        getDeviceComboBox().addActionListener(this);
        getUsePointCheckBox().addActionListener(this);
        getEditComponentButton().addActionListener(this);
        getRemoveComponentButton().addActionListener(this);
        getAddComponentButton().addActionListener(this);
        getOperationFunctionComboBox().addActionListener(this);
        getComponentsTable().addMouseListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("PointCalcComponentPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(427, 366);

            java.awt.GridBagConstraints constraintsJPanelOperations = new java.awt.GridBagConstraints();
            constraintsJPanelOperations.gridx = 1;
            constraintsJPanelOperations.gridy = 2;
            constraintsJPanelOperations.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelOperations.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelOperations.weightx = 1.0;
            constraintsJPanelOperations.weighty = 1.0;
            constraintsJPanelOperations.insets = new java.awt.Insets(8, 4, 15, 5);
            add(getJPanelOperations(), constraintsJPanelOperations);

            java.awt.GridBagConstraints constraintsComponentsScrollPane = new java.awt.GridBagConstraints();
            constraintsComponentsScrollPane.gridx = 1;
            constraintsComponentsScrollPane.gridy = 1;
            constraintsComponentsScrollPane.fill = java.awt.GridBagConstraints.BOTH;
            constraintsComponentsScrollPane.anchor = java.awt.GridBagConstraints.WEST;
            constraintsComponentsScrollPane.weightx = 1.0;
            constraintsComponentsScrollPane.weighty = 1.0;
            constraintsComponentsScrollPane.ipadx = 218;
            constraintsComponentsScrollPane.insets = new java.awt.Insets(11, 4, 8, 5);
            add(getComponentsScrollPane(), constraintsComponentsScrollPane);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // fill up Component Type combo box
        getComponentTypeComboBox().addItem(CalcComponentTypes.OPERATION_COMP_TYPE);
        getComponentTypeComboBox().addItem(CalcComponentTypes.CONSTANT_COMP_TYPE);
        getComponentTypeComboBox().addItem(CalcComponentTypes.FUNCTION_COMP_TYPE);

        getComponentsTable().setModel(new com.cannontech.common.gui.util.CalcComponentTableModel());
        ((com.cannontech.common.gui.util.CalcComponentTableModel) getComponentsTable().getModel()).makeTable();

        getComponentsTable().getColumnModel().getColumn(1).setWidth(200);
        getComponentsTable().getColumnModel().getColumn(1).setPreferredWidth(200);
        getComponentsTable().revalidate();
        getComponentsTable().repaint();
    }

    @Override
    public boolean isInputValid() {
        return true;
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        if (e.getSource() == getComponentsTable()) {
            try {
                this.componentsTable_MousePressed(e);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
    }

    public void removeComponentButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

        int selectedRowCount = getComponentsTable().getSelectedRowCount();

        if (((String) getOperationFunctionComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION)) {
            currentlyMappedBaselineID = null;
        }

        if (selectedRowCount > 0) {
            int selectedRow = getComponentsTable().getSelectedRows()[0];
            for (int i = 0; i < selectedRowCount; i++)
                getTableModel().removeRow(selectedRow);
            fireInputUpdate();
            repaint();
        }
    }

    @Override
    public void setValue(Object val) {
        com.cannontech.database.data.point.PointBase calcPoint;

        if (val instanceof CalcStatusPoint) {
            calcPoint = (CalcStatusPoint) val;
        } else {
            calcPoint = (CalculatedPoint) val;
        }

        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {

            // fill in the calc components of this point
            List<CalcComponent> calcComponents;

            if (calcPoint instanceof CalcStatusPoint)
                calcComponents = ((CalcStatusPoint) calcPoint).getCalcComponents();
            else
                calcComponents = ((CalculatedPoint) calcPoint).getCalcComponents();
            LiteBaseline temp = new LiteBaseline();
            basilHolder = temp.getAllBaselines();

            String type = null;
            Object operand = null;
            LitePoint litePoint = null;

            int componentPointID = 0;
            java.util.Vector calcComponentEntry = null;
            CalcComponent singleCalcComponent = null;

            for (int i = 0; i < calcComponents.size(); i++) {
                calcComponentEntry = new java.util.Vector(3);
                singleCalcComponent = calcComponents.get(i);

                // get and add the type
                type = singleCalcComponent.getComponentType();
                calcComponentEntry.addElement(type);

                if (CalcComponentTypes.OPERATION_COMP_TYPE.equalsIgnoreCase(type)) {
                    componentPointID = singleCalcComponent.getComponentPointID().intValue();

                    litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint(componentPointID);

                    operand = new DevicePointOperandLite(YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(litePoint.getPaobjectID()),
                                                         litePoint);

                    calcComponentEntry.addElement(operand);

                    calcComponentEntry.addElement(singleCalcComponent.getOperation());
                } else if (CalcComponentTypes.CONSTANT_COMP_TYPE.equalsIgnoreCase(type)) {
                    operand = calcComponents.get(i).getConstant().toString();
                    calcComponentEntry.addElement(operand);

                    calcComponentEntry.addElement(singleCalcComponent.getOperation());
                } else if (CalcComponentTypes.FUNCTION_COMP_TYPE.equalsIgnoreCase(type)) {

                    componentPointID = singleCalcComponent.getComponentPointID().intValue();
                    if (componentPointID > 0) {
                        litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint(componentPointID);

                        operand = new DevicePointOperandLite(YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(litePoint.getPaobjectID()), litePoint);

                    } else {
                        operand = "";
                    }

                    calcComponentEntry.addElement(operand);
                    calcComponentEntry.addElement(singleCalcComponent.getFunctionName());

                    if (singleCalcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION)) {
                        if (calcPoint instanceof CalcStatusPoint)
                            currentlyMappedBaselineID = ((CalcStatusPoint) calcPoint).getCalcBaselinePoint().getBaselineID();
                        else
                            currentlyMappedBaselineID = ((CalculatedPoint) calcPoint).getCalcBaselinePoint().getBaselineID();
                    }
                }
                getTableModel().addRow(calcComponentEntry);
            }
        }
        fireInputUpdate();
        repaint();
    }

    public void usePointCheckBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        if (getUsePointCheckBox().isSelected()) {
            getDeviceComboBox().setEnabled(true);
            getPointComboBox().setEnabled(true);
        } else {
            getDeviceComboBox().setEnabled(false);
            getPointComboBox().setEnabled(false);
        }
    }
}