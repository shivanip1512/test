package com.cannontech.graph;

/**
 * A panel to create and edit graphs.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.ColorComboBoxCellRenderer;
import com.cannontech.common.gui.util.ColorTableCellRenderer;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.gui.util.CtiTreeCellRenderer;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.gui.util.TreeViewPanel;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.graph.GDSTypes;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.database.model.DBTreeNode;
import com.cannontech.database.model.DeviceTree_CustomPointsModel;
import com.cannontech.database.model.DummyTreeNode;
import com.cannontech.database.model.LiteBaseTreeModel;
import com.cannontech.database.model.TDCDeviceTreeModel;
import com.cannontech.graph.gds.tablemodel.GDSTableModel;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;

public class CreateGraphPanel extends DataInputPanel implements DataInputPanelListener, ActionListener {
    public static final int OK = 1;
    public static final int CANCEL = 2;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private int buttonPushed = CANCEL;
    private DeviceTree_CustomPointsModel graphPointsModel = null;
    private DeviceTree_CustomPointsModel usagePointsModel = null;
    private TDCDeviceTreeModel allTypesPointsModel = null;
    private GraphColors graphColors;
    private JButton ivjThresholdsButton = null;
    private GraphDefinition value;
    private JPanel ivjPointOptionsPanel = null;
    private JButton ivjAddGDSButton_Graph = null;
    private JPanel ivjAddRemoveButtonsPanel_Graph = null;
    private JSplitPane ivjCreateGraphSplitPane = null;
    private JLabel ivjNameLabel = null;
    private JPanel ivjNamePanel = null;
    private JTextField ivjNameTextField = null;
    private JButton ivjRemoveGDSButton_Graph = null;
    private JPanel ivjTopPanel = null;
    private JScrollPane ivjGraphGDSScrollPane = null;
    private JTable ivjGraphGDSTable = null;
    private AxisPanel ivjAxisPanel = null;
    private JPanel ivjBottomPanel = null;
    private JButton ivjCancelButton = null;
    private JButton ivjOkButton = null;
    private JPanel ivjOkCancelButtonsPanel = null;
    private JPanel ivjJPanel1 = null;
    private TreeViewPanel ivjTreeViewPanel = null;
    private JComboBox<String> ivjPrimaryPointComboBox = null;
    private JLabel ivjPrimaryPointLabel = null;

    private JComboBox<String> typeComboBox = null;

    class IvjEventHandler implements CaretListener {
        @Override
        public void caretUpdate(CaretEvent e) {
            if (e.getSource() == CreateGraphPanel.this.getNameTextField()) {
                connEtoM1(e);
            }
        };
    };

    /**
     * CreateGraphPanel constructor comment.
     */
    public CreateGraphPanel() {
        super();
        initialize();
    }

    /**
     * Insert the method's description here.
     * Creation date: (4/24/2001 10:38:52 AM)
     * 
     * @param event ActionListener
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == getRemoveGDSButton_Graph()) {
            removeGDS_ActionPerformed(getGraphGDSTable());
        }

        else if (event.getSource() == getAddGDSButton_Graph()) {
            addGDS_ActionPerformed(getTreeViewPanel().getSelectedNode());
        }

        else if (event.getSource() == getThresholdsButton()) {
            String value =
                JOptionPane.showInputDialog(
                    this,
                    "Enter the value for your Marker.\n(Marker values are visible only when inclusive of trended data.)",
                    "Value Marker", JOptionPane.QUESTION_MESSAGE);

            if (value != null) {
                PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
                LitePoint pt = pointDao.getLitePoint(PointTypes.SYS_PID_THRESHOLD);

                // Create the GDS to add in the tables.
                GraphDataSeries gds = createGDS(pt, null);
                gds.setMultiplier(new Double(value));
                GDSTableModel model = (GDSTableModel) getGraphGDSTable().getModel();
                model.addRow(gds);
            }

        } else if (event.getSource() == getTypeComboBox()) {
            if (getTypeComboBox().getModel().getSelectedItem() instanceof String) {
                String item = (String) getTypeComboBox().getModel().getSelectedItem();
                if (ServletUtil.parseDateStringLiberally(item) != null) {
                    // If item not already in ComboBox Model, add it to the model.
                    if (((DefaultComboBoxModel<String>) getTypeComboBox().getModel()).getIndexOf(item) < 0) {
                        ((DefaultComboBoxModel<String>) getTypeComboBox().getModel()).addElement(item);
                    }
                }

                if (getTypeComboBox().getSelectedItem().equals("mm/dd/yy")
                    || (ServletUtil.parseDateStringLiberally((String) getTypeComboBox().getSelectedItem()) != null)) {
                    getTypeComboBox().setEditable(true); // Date types are editable comboBox text field
                } else {
                    getTypeComboBox().setEditable(false);
                }
            }
        }

    }

    public void addGDS_ActionPerformed(DefaultMutableTreeNode node) {
        if (node == null || node.isRoot() || (node instanceof DummyTreeNode && node.getParent() == node.getRoot())) {
            return;
        }
        LitePoint pt = null;
        String deviceName = null;

        // Get the DEVICE Object selected in the tree.
        Object tempNode = node;
        while (tempNode instanceof DefaultMutableTreeNode
            && !(((DefaultMutableTreeNode) tempNode).getUserObject() instanceof LiteYukonPAObject)
            && ((DefaultMutableTreeNode) tempNode).getParent() != ((DefaultMutableTreeNode) tempNode).getRoot()) // root
                                                                                                                 // node
                                                                                                                 // is
                                                                                                                 // instance
                                                                                                                 // of
                                                                                                                 // TreeNode
        {
            tempNode = ((DefaultMutableTreeNode) tempNode).getParent();
        }
        deviceName = ((LiteYukonPAObject) ((DefaultMutableTreeNode) tempNode).getUserObject()).getPaoName();

        if (node.getUserObject() instanceof LitePoint) // The selected node is one point
        {
            pt = (LitePoint) node.getUserObject();

            // Create the GDS to add in the tables.
            GraphDataSeries gds = createGDS(pt, deviceName);
            GDSTableModel model = (GDSTableModel) getGraphGDSTable().getModel();
            model.addRow(gds);
        } else if (node instanceof DefaultMutableTreeNode) // The device and all points are added to the GDS,
                                                           // e loop through the children.
        {
            Enumeration<?> e = node.children();
            elementLoad(deviceName, e);
        } else {
            // The tree label ("Devices") was selected...not acceptable.
            return;
        }
    }

    /**
     * connEtoM1: (GraphNameTextField.caret.caretUpdate(CaretEvent) -->
     * CreateGraphPanel.fireInputUpdate()V)
     * 
     * @param arg1 CaretEvent
     */

    private void connEtoM1(CaretEvent arg1) {
        try {

            this.fireInputUpdate();

        } catch (Throwable ivjExc) {
            // user code begin {3}

            handleException(ivjExc);
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/15/2002 9:40:15 AM)
     * 
     * @return com.cannontech.database.db.graph.GraphDataSeries
     * @param point com.cannontech.database.data.lite.LitePoint
     * @param deviceName java.lang.String
     */
    public GraphDataSeries createGDS(LitePoint point, String deviceName) {
        GraphDataSeries gds = new GraphDataSeries();
        gds.setPointID(new Integer(point.getPointID()));

        String gdsLabel = point.getPointName();

        if (point.getPointID() == PointTypes.SYS_PID_THRESHOLD) {
            gdsLabel = "Marker";
        }

        if (deviceName == null) {
            gds.setDeviceName("System Device");
        } else {
            gds.setDeviceName(deviceName);
            gdsLabel += " / " + deviceName;
        }

        if (gdsLabel.length() > 40) {
            gdsLabel = gdsLabel.substring(0, 39);
        }
        gds.setLabel(gdsLabel);

        gds.setAxis(new Character('L'));
        gds.setColor(new Integer(getGraphColors().getNextLineColorID()));

        gds.setRenderer(new Integer(GraphRenderers.LINE));
        // call to obtain the type from the database (pointUnit)
        String type = getPointTypeString(point);
        gds.setType(new Integer(GDSTypesFuncs.getTypeInt(type)));

        gds.setMultiplier(new Double(1.0));

        // ADD GDS TO THE PRIMARY POINT COMBO BOX.
        getPrimaryPointComboBox().addItem(gds.getLabel().toString());

        return gds;
    }

    /**
     * @param devName
     * @param e
     */
    private void elementLoad(String devName, Enumeration<?> e) {
        LitePoint pt = null;
        while (e.hasMoreElements()) {
            Object nextElem = e.nextElement();
            if (nextElem instanceof DummyTreeNode) {
                Enumeration<?> e2 = ((DummyTreeNode) nextElem).children();
                while (e2.hasMoreElements()) {
                    pt = (LitePoint) ((DBTreeNode) e2.nextElement()).getUserObject();

                    GraphDataSeries gds = createGDS(pt, devName);
                    GDSTableModel tModel = (GDSTableModel) getGraphGDSTable().getModel();
                    tModel.addRow(gds);
                }
            } else {
                pt = (LitePoint) ((DBTreeNode) nextElem).getUserObject();

                // Create the GDS to add in the tables.
                GraphDataSeries gds = createGDS(pt, devName);
                GDSTableModel model = (GDSTableModel) getGraphGDSTable().getModel();
                model.addRow(gds);
            }
        }
    }

    /**
     * Return the JButton1 property value.
     * 
     * @return JButton
     */
    private JButton getAddGDSButton_Graph() {
        if (ivjAddGDSButton_Graph == null) {
            try {
                ivjAddGDSButton_Graph = new JButton();
                ivjAddGDSButton_Graph.setName("AddGDSButton_Graph");
                ivjAddGDSButton_Graph.setToolTipText("Add Graph Points");
                ivjAddGDSButton_Graph.setText(">>");
                ivjAddGDSButton_Graph.setActionCommand("AddPointButton");
                ivjAddGDSButton_Graph.setFont(new Font("Arial", 1, 14));
                ivjAddGDSButton_Graph.setMargin(new Insets(2, 2, 2, 2));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjAddGDSButton_Graph;
    }

    /**
     * Return the JPanel4 property value.
     * 
     * @return JPanel
     */
    private JPanel getAddRemoveButtonsPanel_Graph() {
        if (ivjAddRemoveButtonsPanel_Graph == null) {
            try {
                ivjAddRemoveButtonsPanel_Graph = new JPanel();
                ivjAddRemoveButtonsPanel_Graph.setName("AddRemoveButtonsPanel_Graph");
                ivjAddRemoveButtonsPanel_Graph.setPreferredSize(new Dimension(35, 70));
                ivjAddRemoveButtonsPanel_Graph.setLayout(new GridBagLayout());

                GridBagConstraints constraintsAddGDSButton_Graph = new GridBagConstraints();
                constraintsAddGDSButton_Graph.gridx = 0;
                constraintsAddGDSButton_Graph.gridy = 0;
                constraintsAddGDSButton_Graph.fill = GridBagConstraints.HORIZONTAL;
                constraintsAddGDSButton_Graph.insets = new Insets(4, 4, 4, 4);
                getAddRemoveButtonsPanel_Graph().add(getAddGDSButton_Graph(), constraintsAddGDSButton_Graph);

                GridBagConstraints constraintsRemoveGDSButton_Graph = new GridBagConstraints();
                constraintsRemoveGDSButton_Graph.gridx = 0;
                constraintsRemoveGDSButton_Graph.gridy = 1;
                constraintsRemoveGDSButton_Graph.fill = GridBagConstraints.HORIZONTAL;
                constraintsRemoveGDSButton_Graph.insets = new Insets(4, 4, 4, 4);
                getAddRemoveButtonsPanel_Graph().add(getRemoveGDSButton_Graph(), constraintsRemoveGDSButton_Graph);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjAddRemoveButtonsPanel_Graph;
    }

    /**
     * Return the AxisPanel1 property value.
     * 
     * @return com.cannontech.graph.AxisPanel
     */
    private AxisPanel getAxisPanel() {
        if (ivjAxisPanel == null) {
            try {
                ivjAxisPanel = new com.cannontech.graph.AxisPanel();
                ivjAxisPanel.setName("AxisPanel");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjAxisPanel;
    }

    /**
     * Return the JPanel61 property value.
     * 
     * @return JPanel
     */
    private JPanel getBottomPanel() {
        if (ivjBottomPanel == null) {
            try {
                ivjBottomPanel = new JPanel();
                ivjBottomPanel.setName("BottomPanel");
                ivjBottomPanel.setLayout(new BorderLayout());
                getBottomPanel().add(getOkCancelButtonsPanel(), "East");

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjBottomPanel;
    }

    public int getButtonPushed() {
        return buttonPushed;
    }

    /**
     * Return the CancelButton1 property value.
     * 
     * @return JButton
     */
    private JButton getCancelButton() {
        if (ivjCancelButton == null) {
            try {
                ivjCancelButton = new JButton();
                ivjCancelButton.setName("CancelButton");
                ivjCancelButton.setText("Cancel");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjCancelButton;
    }

    /**
     * Return the JSplitPane1 property value.
     * 
     * @return JSplitPane
     */
    private JSplitPane getCreateGraphSplitPane() {
        if (ivjCreateGraphSplitPane == null) {
            try {
                ivjCreateGraphSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                ivjCreateGraphSplitPane.setName("CreateGraphSplitPane");
                ivjCreateGraphSplitPane.setDividerSize(2);
                ivjCreateGraphSplitPane.setContinuousLayout(false);
                getCreateGraphSplitPane().add(getTopPanel(), "top");
                getCreateGraphSplitPane().add(getJPanel1(), "bottom");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjCreateGraphSplitPane;
    }

    public GraphColors getGraphColors() {
        if (graphColors == null) {
            graphColors = new GraphColors();
        }

        return graphColors;
    }

    private GraphDefinition getGraphDefinitionValue() {
        return value;
    }

    /**
     * Return the GraphPointsScrollPane property value.
     * 
     * @return JScrollPane
     */
    private JScrollPane getGraphGDSScrollPane() {
        if (ivjGraphGDSScrollPane == null) {
            try {
                ivjGraphGDSScrollPane = new JScrollPane();
                ivjGraphGDSScrollPane.setName("GraphGDSScrollPane");
                ivjGraphGDSScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                ivjGraphGDSScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                getGraphGDSScrollPane().setViewportView(getGraphGDSTable());

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjGraphGDSScrollPane;
    }

    /**
     * Return the ScrollPaneTable property value.
     * 
     * @return JTable
     */
    private JTable getGraphGDSTable() {
        if (ivjGraphGDSTable == null) {
            try {
                ivjGraphGDSTable = new JTable();
                ivjGraphGDSTable.setName("GraphGDSTable");
                getGraphGDSScrollPane().setColumnHeaderView(ivjGraphGDSTable.getTableHeader());
                ivjGraphGDSTable.setModel(new GDSTableModel());
                ivjGraphGDSTable.setCellSelectionEnabled(false);
                ivjGraphGDSTable.setDoubleBuffered(false);
                ivjGraphGDSTable.setBounds(0, 0, 200, 200);

                ivjGraphGDSTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                ivjGraphGDSTable.setRowSelectionAllowed(true);
                ivjGraphGDSTable.setSelectionBackground(Color.BLUE);
                ivjGraphGDSTable.setSelectionForeground(Color.WHITE);

                // Column inits.
                TableColumnModel colModel = ivjGraphGDSTable.getColumnModel();
                colModel.getColumn(GDSTableModel.DEVICE_NAME_COLUMN).setPreferredWidth(80);
                colModel.getColumn(GDSTableModel.POINT_NAME_COLUMN).setPreferredWidth(65);
                colModel.getColumn(GDSTableModel.LABEL_NAME_COLUMN).setPreferredWidth(122);
                colModel.getColumn(GDSTableModel.COLOR_NAME_COLUMN).setPreferredWidth(40);
                colModel.getColumn(GDSTableModel.AXIS_NAME_COLUMN).setPreferredWidth(8);
                colModel.getColumn(GDSTableModel.TYPE_NAME_COLUMN).setPreferredWidth(55);
                colModel.getColumn(GDSTableModel.MULT_NAME_COLUMN).setPreferredWidth(10);
                colModel.getColumn(GDSTableModel.REND_NAME_COLUMN).setPreferredWidth(60);
                // colModel.getColumn(GDSTableModel.SETUP_NAME_COLUMN).setPreferredWidth(10);

                // Color choices setup
                Color[] colors = getGraphColors().getAvailableColors();
                String[] colorStrings = new String[colors.length];

                for (int i = 0; i < colors.length; i++) {
                    colorStrings[i] =
                        Colors.getColorString(com.cannontech.common.gui.util.Colors.getColorID(colors[i]));
                }
                JComboBox<String> colorComboBox = new JComboBox<>(colorStrings);
                colorComboBox.setRenderer(new ColorComboBoxCellRenderer());
                DefaultCellEditor colorEditor = new DefaultCellEditor(colorComboBox);

                colModel.getColumn(GDSTableModel.COLOR_NAME_COLUMN).setCellEditor(colorEditor);
                TableColumn tblColumn = colModel.getColumn(GDSTableModel.COLOR_NAME_COLUMN);
                tblColumn.setCellRenderer(new ColorTableCellRenderer());

                JComboBox<String> axisComboBox = new JComboBox<>(new String[] { "Left", "Right" });
                DefaultCellEditor axisEditor = new DefaultCellEditor(axisComboBox);
                colModel.getColumn(GDSTableModel.AXIS_NAME_COLUMN).setCellEditor(axisEditor);

                DefaultCellEditor typeEditor = new DefaultCellEditor(getTypeComboBox());
                colModel.getColumn(GDSTableModel.TYPE_NAME_COLUMN).setCellEditor(typeEditor);

                JComboBox<String> rendComboBox = new JComboBox<>(GraphRenderers.AVAILABLE_RENDERERS);
                DefaultCellEditor rendEditor = new DefaultCellEditor(rendComboBox);
                colModel.getColumn(GDSTableModel.REND_NAME_COLUMN).setCellEditor(rendEditor);

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjGraphGDSTable;
    }

    private JComboBox<String> getTypeComboBox() {
        if (typeComboBox == null) {
            String[] typeStrings =
                new String[] { GDSTypes.BASIC_GRAPH_TYPE_STRING, GDSTypes.USAGE_TYPE_STRING,
                    GDSTypes.YESTERDAY_GRAPH_TYPE_STRING, GDSTypes.PEAK_GRAPH_TYPE_STRING,
                    GDSTypes.USAGE_GRAPH_TYPE_STRING, GDSTypes.DATE_TYPE_STRING, GDSTypes.MARKER_TYPE_STRING };
            typeComboBox = new JComboBox<>(typeStrings);
            typeComboBox.addActionListener(this);
        }
        return typeComboBox;
    }

    private DeviceTree_CustomPointsModel getGraphPointsModel() {
        if (graphPointsModel == null) {
            graphPointsModel = new DeviceTree_CustomPointsModel(true);
            graphPointsModel.setIncludeUOFMType(LitePoint.POINT_UOFM_GRAPH);
        }
        return graphPointsModel;
    }

    public TDCDeviceTreeModel getAllTypesPointsModel() {
        if (allTypesPointsModel == null) {
            allTypesPointsModel = new TDCDeviceTreeModel();
        }
        return allTypesPointsModel;
    }

    /**
     * Return the JPanel1 property value.
     * 
     * @return JPanel
     */

    private JPanel getJPanel1() {
        if (ivjJPanel1 == null) {
            try {
                ivjJPanel1 = new JPanel();
                ivjJPanel1.setName("JPanel1");
                ivjJPanel1.setLayout(new GridBagLayout());

                GridBagConstraints constraintsTreeViewPanel = new GridBagConstraints();
                constraintsTreeViewPanel.gridx = 0;
                constraintsTreeViewPanel.gridy = 0;
                constraintsTreeViewPanel.fill = GridBagConstraints.BOTH;
                constraintsTreeViewPanel.weighty = 1.0;
                constraintsTreeViewPanel.insets = new Insets(5, 5, 5, 0);
                getJPanel1().add(getTreeViewPanel(), constraintsTreeViewPanel);

                GridBagConstraints constraintsGraphGDSScrollPane = new GridBagConstraints();
                constraintsGraphGDSScrollPane.gridx = 2;
                constraintsGraphGDSScrollPane.gridy = 0;
                constraintsGraphGDSScrollPane.fill = GridBagConstraints.BOTH;
                constraintsGraphGDSScrollPane.weightx = 1.0;
                constraintsGraphGDSScrollPane.weighty = 1.0;
                constraintsGraphGDSScrollPane.insets = new Insets(5, 0, 5, 5);
                getJPanel1().add(getGraphGDSScrollPane(), constraintsGraphGDSScrollPane);

                GridBagConstraints constraintsAddRemoveButtonsPanel_Graph = new GridBagConstraints();
                constraintsAddRemoveButtonsPanel_Graph.gridx = 1;
                constraintsAddRemoveButtonsPanel_Graph.gridy = 0;
                constraintsAddRemoveButtonsPanel_Graph.fill = GridBagConstraints.BOTH;
                constraintsAddRemoveButtonsPanel_Graph.insets = new Insets(5, 0, 5, 0);
                getJPanel1().add(getAddRemoveButtonsPanel_Graph(), constraintsAddRemoveButtonsPanel_Graph);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjJPanel1;
    }

    /**
     * Return the GraphNameLabel property value.
     * 
     * @return JLabel
     */

    private JLabel getNameLabel() {
        if (ivjNameLabel == null) {
            try {
                ivjNameLabel = new JLabel();
                ivjNameLabel.setName("NameLabel");
                ivjNameLabel.setText("Name:");

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjNameLabel;
    }

    /**
     * Return the JPanel1 property value.
     * 
     * @return JPanel
     */

    private JPanel getNamePanel() {
        if (ivjNamePanel == null) {
            try {
                ivjNamePanel = new JPanel();
                ivjNamePanel.setName("NamePanel");
                ivjNamePanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsNameLabel = new GridBagConstraints();
                constraintsNameLabel.gridx = 0;
                constraintsNameLabel.gridy = 0;
                constraintsNameLabel.insets = new Insets(4, 4, 4, 4);
                getNamePanel().add(getNameLabel(), constraintsNameLabel);

                GridBagConstraints constraintsNameTextField = new GridBagConstraints();
                constraintsNameTextField.gridx = 1;
                constraintsNameTextField.gridy = 0;
                constraintsNameTextField.anchor = GridBagConstraints.WEST;
                constraintsNameTextField.weightx = 1.0;
                constraintsNameTextField.insets = new Insets(4, 4, 4, 4);
                getNamePanel().add(getNameTextField(), constraintsNameTextField);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjNamePanel;
    }

    /**
     * Return the GraphNameTextField property value.
     * 
     * @return JTextField
     */

    private JTextField getNameTextField() {
        if (ivjNameTextField == null) {
            try {
                ivjNameTextField = new JTextField();
                ivjNameTextField.setName("NameTextField");
                ivjNameTextField.setColumns(20);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjNameTextField;
    }

    /**
     * Return the OkButton1 property value.
     * 
     * @return JButton
     */

    private JButton getOkButton() {
        if (ivjOkButton == null) {
            try {
                ivjOkButton = new JButton();
                ivjOkButton.setName("OkButton");
                ivjOkButton.setPreferredSize(new Dimension(73, 25));
                ivjOkButton.setText("Ok");
                ivjOkButton.setEnabled(false);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjOkButton;
    }

    /**
     * Return the JPanel71 property value.
     * 
     * @return JPanel
     */

    private JPanel getOkCancelButtonsPanel() {
        if (ivjOkCancelButtonsPanel == null) {
            try {
                ivjOkCancelButtonsPanel = new JPanel();
                ivjOkCancelButtonsPanel.setName("OkCancelButtonsPanel");
                ivjOkCancelButtonsPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsOkButton = new GridBagConstraints();
                constraintsOkButton.gridx = 2;
                constraintsOkButton.gridy = 1;
                constraintsOkButton.insets = new Insets(10, 4, 10, 4);
                getOkCancelButtonsPanel().add(getOkButton(), constraintsOkButton);

                GridBagConstraints constraintsCancelButton = new GridBagConstraints();
                constraintsCancelButton.gridx = 3;
                constraintsCancelButton.gridy = 1;
                constraintsCancelButton.insets = new Insets(10, 4, 10, 15);
                getOkCancelButtonsPanel().add(getCancelButton(), constraintsCancelButton);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjOkCancelButtonsPanel;
    }

    /**
     * Return the PointOptionsPanel property value.
     * 
     * @return JPanel
     */

    private JPanel getPointOptionsPanel() {
        if (ivjPointOptionsPanel == null) {
            try {
                ivjPointOptionsPanel = new JPanel();
                ivjPointOptionsPanel.setName("PointOptionsPanel");
                ivjPointOptionsPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsThresholdsButton = new GridBagConstraints();
                constraintsThresholdsButton.gridx = 0;
                constraintsThresholdsButton.gridy = 0;
                constraintsThresholdsButton.weightx = 1.0;
                constraintsThresholdsButton.weighty = 1.0;
                constraintsThresholdsButton.insets = new Insets(4, 4, 4, 4);
                getPointOptionsPanel().add(getThresholdsButton(), constraintsThresholdsButton);

                GridBagConstraints constraintsPrimaryPointComboBox = new GridBagConstraints();
                constraintsPrimaryPointComboBox.gridx = 0;
                constraintsPrimaryPointComboBox.gridy = 2;
                constraintsPrimaryPointComboBox.weighty = 1.0;
                constraintsPrimaryPointComboBox.insets = new Insets(0, 4, 4, 4);
                getPointOptionsPanel().add(getPrimaryPointComboBox(), constraintsPrimaryPointComboBox);

                GridBagConstraints constraintsPrimaryPointLabel = new GridBagConstraints();
                constraintsPrimaryPointLabel.gridx = 0;
                constraintsPrimaryPointLabel.gridy = 1;
                constraintsPrimaryPointLabel.anchor = GridBagConstraints.SOUTH;
                constraintsPrimaryPointLabel.insets = new Insets(4, 4, 0, 4);
                getPointOptionsPanel().add(getPrimaryPointLabel(), constraintsPrimaryPointLabel);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPointOptionsPanel;
    }

    /**
     * Obtains the point tag from the LitePoint.
     * returns default value of GRAPH_SERIES
     * Creation date: (4/23/2001 8:23:06 AM)
     * 
     * @return java.lang.String
     * @param pt com.cannontech.database.data.lite.LitePoint
     */
    public String getPointTypeString(LitePoint pt) {
        if (pt.getPointID() == PointTypes.SYS_PID_THRESHOLD) {
            return GDSTypes.MARKER_TYPE_STRING;
        } else if (pt.getTags() == LitePoint.POINT_UOFM_GRAPH) {
            return GDSTypes.BASIC_GRAPH_TYPE_STRING;
        } else if (pt.getTags() == LitePoint.POINT_UOFM_USAGE) {
            return GDSTypes.USAGE_TYPE_STRING;
        } else {
            return GDSTypes.BASIC_GRAPH_TYPE_STRING; // default
        }
    }

    /**
     * Return the PrimaryPointComboBox property value.
     * 
     * @return JComboBox
     */

    private JComboBox<String> getPrimaryPointComboBox() {
        if (ivjPrimaryPointComboBox == null) {
            try {
                ivjPrimaryPointComboBox = new JComboBox<>();
                ivjPrimaryPointComboBox.setName("PrimaryPointComboBox");
                ivjPrimaryPointComboBox.setToolTipText("Optional...Primary Point to base coincidental values from.");
                ivjPrimaryPointComboBox.setPreferredSize(new Dimension(173, 25));
                ivjPrimaryPointComboBox.setMaximumSize(new Dimension(173, 25));
                ivjPrimaryPointComboBox.setMinimumSize(new Dimension(173, 25));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPrimaryPointComboBox;
    }

    /**
     * Return the PrimaryPointLabel property value.
     * 
     * @return JLabel
     */

    private JLabel getPrimaryPointLabel() {
        if (ivjPrimaryPointLabel == null) {
            try {
                ivjPrimaryPointLabel = new JLabel();
                ivjPrimaryPointLabel.setName("PrimaryPointLabel");
                ivjPrimaryPointLabel.setText("Primary Point:");
                ivjPrimaryPointLabel.setMaximumSize(new Dimension(173, 25));
                ivjPrimaryPointLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
                ivjPrimaryPointLabel.setPreferredSize(new Dimension(173, 25));
                ivjPrimaryPointLabel.setFont(new Font("dialog", 0, 12));
                ivjPrimaryPointLabel.setVerticalAlignment(SwingConstants.BOTTOM);
                ivjPrimaryPointLabel.setMinimumSize(new Dimension(173, 25));
                ivjPrimaryPointLabel.setEnabled(true);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPrimaryPointLabel;
    }

    /**
     * Return the JButton2 property value.
     * 
     * @return JButton
     */

    private JButton getRemoveGDSButton_Graph() {
        if (ivjRemoveGDSButton_Graph == null) {
            try {
                ivjRemoveGDSButton_Graph = new JButton();
                ivjRemoveGDSButton_Graph.setName("RemoveGDSButton_Graph");
                ivjRemoveGDSButton_Graph.setToolTipText("Remove Graph Points");
                ivjRemoveGDSButton_Graph.setText("<<");
                ivjRemoveGDSButton_Graph.setActionCommand("RemovePointButton");
                ivjRemoveGDSButton_Graph.setFont(new Font("Arial", 1, 14));
                ivjRemoveGDSButton_Graph.setMargin(new Insets(2, 2, 2, 2));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjRemoveGDSButton_Graph;
    }

    /**
     * Return the ThresholdsButton property value.
     * 
     * @return JButton
     */

    private JButton getThresholdsButton() {
        if (ivjThresholdsButton == null) {
            try {
                ivjThresholdsButton = new JButton();
                ivjThresholdsButton.setName("ThresholdsButton");
                ivjThresholdsButton.setText("Add Value Marker...");
                ivjThresholdsButton.setMaximumSize(new Dimension(173, 25));
                ivjThresholdsButton.setPreferredSize(new Dimension(173, 25));
                ivjThresholdsButton.setMinimumSize(new Dimension(173, 25));

                ivjThresholdsButton.addActionListener(this);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjThresholdsButton;
    }

    /**
     * Return the JPanel2 property value.
     * 
     * @return JPanel
     */

    private JPanel getTopPanel() {
        if (ivjTopPanel == null) {
            try {
                ivjTopPanel = new JPanel();
                ivjTopPanel.setName("TopPanel");
                ivjTopPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsNamePanel = new GridBagConstraints();
                constraintsNamePanel.gridx = 0;
                constraintsNamePanel.gridy = 0;
                constraintsNamePanel.gridwidth = 2;
                constraintsNamePanel.fill = GridBagConstraints.BOTH;
                constraintsNamePanel.weightx = 1.0;
                constraintsNamePanel.weighty = 1.0;
                constraintsNamePanel.insets = new Insets(4, 10, 4, 4);
                getTopPanel().add(getNamePanel(), constraintsNamePanel);

                GridBagConstraints constraintsPointOptionsPanel = new GridBagConstraints();
                constraintsPointOptionsPanel.gridx = 2;
                constraintsPointOptionsPanel.gridy = 1;
                constraintsPointOptionsPanel.fill = GridBagConstraints.BOTH;
                constraintsPointOptionsPanel.weighty = 1.0;
                constraintsPointOptionsPanel.insets = new Insets(4, 10, 4, 4);
                getTopPanel().add(getPointOptionsPanel(), constraintsPointOptionsPanel);

                GridBagConstraints constraintsAxisPanel = new GridBagConstraints();
                constraintsAxisPanel.gridx = 1;
                constraintsAxisPanel.gridy = 1;
                constraintsAxisPanel.fill = GridBagConstraints.BOTH;
                constraintsAxisPanel.weightx = 1.0;
                constraintsAxisPanel.weighty = 1.0;
                constraintsAxisPanel.insets = new Insets(4, 4, 4, 4);
                getTopPanel().add(getAxisPanel(), constraintsAxisPanel);

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjTopPanel;
    }

    /**
     * Return the PointTreeViewModel property value.
     * 
     * @return com.cannontech.common.gui.util.TreeViewPanel
     */
    public com.cannontech.common.gui.util.TreeViewPanel getTreeViewPanel() {
        if (ivjTreeViewPanel == null) {
            try {
                ivjTreeViewPanel = new com.cannontech.common.gui.util.TreeViewPanel();
                ivjTreeViewPanel.setName("TreeViewPanel");

                ivjTreeViewPanel.setTreeModels(new LiteBaseTreeModel[] { getGraphPointsModel(), getUsagePointsModel(),
                    getAllTypesPointsModel() });
                ivjTreeViewPanel.getTree().setCellRenderer(new CtiTreeCellRenderer());
                ivjTreeViewPanel.getTree().setLargeModel(true);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTreeViewPanel;
    }

    private DeviceTree_CustomPointsModel getUsagePointsModel() {
        if (usagePointsModel == null) {
            usagePointsModel = new DeviceTree_CustomPointsModel(true);
            usagePointsModel.setIncludeUOFMType(LitePoint.POINT_UOFM_USAGE);
        }
        return usagePointsModel;
    }

    /**
     * Build up a graphDefinition and return it.
     * Creation date: (10/24/00 2:36:02 PM)
     * 
     * @return java.lang.Object
     * @param o java.lang.Object
     */
    @Override
    public Object getValue(Object object) {
        GraphDefinition gDef;
        com.cannontech.database.db.graph.GraphDefinition gDefInfo;

        if (getGraphDefinitionValue() != null) {
            gDef = getGraphDefinitionValue();
            gDefInfo = gDef.getGraphDefinition();
        } else {
            gDef = new GraphDefinition();
            gDefInfo = new com.cannontech.database.db.graph.GraphDefinition();
        }

        String trendName = getNameTextField().getText().trim();
        if (trendName.length() > 40) {
            trendName = trendName.substring(0, 39);
        }

        gDefInfo.setName(trendName);

        if (!getAxisPanel().getLeftAutoScalingCheckBox().isSelected()) {
            gDefInfo.setAutoScaleLeftAxis(new Character('N'));
        } else {
            gDefInfo.setAutoScaleLeftAxis(new Character('Y'));
        }

        try {
            gDefInfo.setLeftMin(new Double(getAxisPanel().getLeftMinTextField().getText()));
            gDefInfo.setLeftMax(new Double(getAxisPanel().getLeftMaxTextField().getText()));
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        if (!getAxisPanel().getRightAutoScalingCheckBox().isSelected()) {
            gDefInfo.setAutoScaleRightAxis(new Character('N'));
        } else {
            gDefInfo.setAutoScaleRightAxis(new Character('Y'));
        }

        try {
            gDefInfo.setRightMin(new Double(getAxisPanel().getRightMinTextField().getText()));
            gDefInfo.setRightMax(new Double(getAxisPanel().getRightMaxTextField().getText()));
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        gDefInfo.setType("L");

        GDSTableModel model = (GDSTableModel) getGraphGDSTable().getModel();

        for (int i = 1; i < getPrimaryPointComboBox().getItemCount(); i++) {
            int type = model.getRow(i - 1).getType().intValue();
            // PRIMARY POINT FOUND
            if (getPrimaryPointComboBox().getSelectedIndex() == i) {
                type |= GDSTypes.PRIMARY_TYPE;
            } else {
                // check to make sure it's there if we are going to remove it
                if ((type & GDSTypes.PRIMARY_TYPE) != 0) {
                    type ^= GDSTypes.PRIMARY_TYPE;
                }
            }
            model.getRow(i - 1).setType(new Integer(type));
        }

        ArrayList<GraphDataSeries> dataSeries = new ArrayList<>(model.getRowCount());

        // Copy the graphdataseries into a temp array to loop through
        GraphDataSeries[] modelGDSArray = model.getAllDataSeries();

        for (int i = 0; i < modelGDSArray.length; i++) {
            // ADD ALL POINTS IN TABLE TO GDEF.GRAPHDATASERIES.
            modelGDSArray[i].setGraphDefinitionID(gDefInfo.getGraphDefinitionID());
            dataSeries.add(modelGDSArray[i]);
        }

        gDef.setGraphDefinition(gDefInfo);
        gDef.setGraphDataSeries(dataSeries);

        return gDef;
    }

    /**
     * Called whenever the part throws an exception.
     * 
     * @param exception Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     * 
     * @exception java.lang.Exception The exception description.
     */

    private void initConnections() throws java.lang.Exception {

        getNameTextField().addCaretListener(ivjEventHandler);
    }

    private void initialize() {
        try {
            setName("CreateGraphPanel");
            setLayout(new BorderLayout());
            setSize(651, 411);
            add(getCreateGraphSplitPane(), "Center");
            add(getBottomPanel(), "South");
            initConnections();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }

        // Setup primaryComboBox for default "no primary point"
        getPrimaryPointComboBox().addItem("no primary point");

        addDataInputPanelListener(this);

        // Add ActionListeners to the add/remove buttons.
        getAddGDSButton_Graph().addActionListener(this);
        getRemoveGDSButton_Graph().addActionListener(this);

    }

    /**
     * Insert the method's description here.
     * Creation date: (10/27/00 11:46:27 AM)
     * 
     * @param event PropertyPanelEvent
     */
    @Override
    public void inputUpdate(PropertyPanelEvent event) {
        ivjOkButton.setEnabled(isInputValid());
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/27/00 12:24:35 PM)
     * 
     * @return boolean
     */
    @Override
    public boolean isInputValid() {
        return (getNameTextField().getText().length() > 0);
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            JFrame frame = new JFrame();
            CreateGraphPanel aCreateGraphPanel;
            aCreateGraphPanel = new CreateGraphPanel();
            // frame.setIconImage(Toolkit.getDefaultToolkit().getImage(GraphClient.GRAPH_GIF));
            // frame.setTitle("Yukon Trending Reports Editor");
            frame.setContentPane(aCreateGraphPanel);
            frame.setSize(aCreateGraphPanel.getSize());

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.show();
            Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of JPanel");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Action Performed for the Remove Button on the Usage Tab.
     * Removes objects from the usagedataseriesTable
     */
    public void removeGDS_ActionPerformed(JTable table) {
        int[] rowsToRemove = table.getSelectedRows();

        // REMOVE GDS FROM PRIMARY POINT COMBO BOX.
        // FOR LOOP IS REVERSED INDEX ORDER, SO WE NEVER HIT AN INDEX ALREADY REMOVED.
        for (int i = rowsToRemove.length - 1; i >= 0; i--) {
            // IF GDS REMOVED IS PRIMARY POINT, SET PRIMARY POINT COMBO BOX TO INDEX 0 ("no primary point").
            if ((rowsToRemove[i] + 1) == getPrimaryPointComboBox().getSelectedIndex()) {
                getPrimaryPointComboBox().setSelectedIndex(0);
            }

            getPrimaryPointComboBox().removeItemAt((rowsToRemove[i] + 1));
        }

        GDSTableModel model = (GDSTableModel) table.getModel();
        model.removeRow(rowsToRemove);

        table.getSelectionModel().clearSelection();
    }

    public void setButtonPushed(int newButtonPushed) {
        buttonPushed = newButtonPushed;
    }

    public void setGraphColors(GraphColors newGraphColors) {
        graphColors = newGraphColors;
    }

    private void setGraphDefinitionValue(GraphDefinition newValue) {
        value = newValue;
    }

    /**
     * Retreive the graph data series upon the init of the createGraphPanel
     * Creation date: (10/24/00 2:35:45 PM)
     * 
     * @param val java.lang.Object
     */
    @Override
    public void setValue(Object val) {
        if (val == null) {
            return;
        }

        GraphDefinition gDef = (GraphDefinition) val;
        setGraphDefinitionValue(gDef);

        // SET GRAPH NAME
        getNameTextField().setText(gDef.getGraphDefinition().getName());

        // SET LEFT/RIGHT (AUTO)SCALE
        if (gDef.getGraphDefinition().getAutoScaleLeftAxis().charValue() == 'N'
            || gDef.getGraphDefinition().getAutoScaleLeftAxis().charValue() == 'n') {
            getAxisPanel().getLeftAutoScalingCheckBox().setSelected(false);
        } else {
            getAxisPanel().getLeftAutoScalingCheckBox().setSelected(true);
        }

        if (gDef.getGraphDefinition().getAutoScaleRightAxis().charValue() == 'N'
            || gDef.getGraphDefinition().getAutoScaleRightAxis().charValue() == 'n') {
            getAxisPanel().getRightAutoScalingCheckBox().setSelected(false);
        } else {
            getAxisPanel().getRightAutoScalingCheckBox().setSelected(true);
        }

        // SET LEFT/RIGHT MIN/MAX VALUES
        getAxisPanel().getLeftMinTextField().setText(gDef.getGraphDefinition().getLeftMin().toString());
        getAxisPanel().getLeftMaxTextField().setText(gDef.getGraphDefinition().getLeftMax().toString());

        getAxisPanel().getRightMinTextField().setText(gDef.getGraphDefinition().getRightMin().toString());
        getAxisPanel().getRightMaxTextField().setText(gDef.getGraphDefinition().getRightMax().toString());

        // INSERT ALL GDS INTO TABLE
        List<?> gds = gDef.getGraphDataSeries();
        Iterator<?> iter = gds.iterator();

        // ADD ALL SERIES TO TABLE.
        GDSTableModel model = (GDSTableModel) getGraphGDSTable().getModel();
        int currentRowIndex = 0;
        while (iter.hasNext()) {
            GraphDataSeries elem = (GraphDataSeries) iter.next();
            model.addRow(elem);

            // ADD TABLE ELEMENTS TO PRIMARY POINT COMBOBOX
            getPrimaryPointComboBox().addItem(elem.getLabel().toString());
            if (GDSTypesFuncs.isPrimaryType(elem.getType().intValue())) {
                // SELECT THE PRIMARY POINT IN THE COMBO BOX.
                getPrimaryPointComboBox().setSelectedIndex(currentRowIndex + 1);
            }
            currentRowIndex++;
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/24/00 4:07:55 PM)
     * 
     * @return com.cannontech.database.data.graph.GraphDefinition
     */
    public GraphDefinition showCreateGraphPanelDialog(Frame parent) {
        JDialog dialog = new JDialog(parent);
        dialog.setTitle("Yukon Trending Reports Editor");

        class DialogButtonListener implements ActionListener {
            JDialog dialog;

            public DialogButtonListener(JDialog d) {
                dialog = d;
            }

            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getSource() == getOkButton()) {
                    if (ivjGraphGDSTable.getCellEditor() != null) {
                        ivjGraphGDSTable.getCellEditor().stopCellEditing();
                    }

                    if (!validData()) {
                        return;
                    }

                    setButtonPushed(OK);
                } else if (event.getSource() == getCancelButton()) {
                    setButtonPushed(CANCEL);
                }

                dialog.setVisible(false);
                dialog.dispose();
            }
        }

        ActionListener listener = new DialogButtonListener(dialog);

        getOkButton().addActionListener(listener);
        getCancelButton().addActionListener(listener);

        dialog.setModal(true);
        dialog.getContentPane().add(this);
        Dimension size = new Dimension(parent.getWidth() - 50, 500);
        dialog.setSize(size);
        dialog.setVisible(true);

        getOkButton().removeActionListener(listener);
        getCancelButton().removeActionListener(listener);

        if (getButtonPushed() == CreateGraphPanel.OK) {
            return (GraphDefinition) getValue(null);
        } else {
            return null;
        }
    }

    /**
	 * 
	 */
    private boolean validData() {
        if (!getAxisPanel().getLeftAutoScalingCheckBox().isSelected()) {
            double min = new Double(getAxisPanel().getLeftMinTextField().getText()).doubleValue();
            double max = new Double(getAxisPanel().getLeftMaxTextField().getText()).doubleValue();
            if (min > max) {
                showPopupMessage("Left Axis Min value must be less than Max value.", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        if (!getAxisPanel().getRightAutoScalingCheckBox().isSelected()) {
            double min = new Double(getAxisPanel().getRightMinTextField().getText()).doubleValue();
            double max = new Double(getAxisPanel().getRightMaxTextField().getText()).doubleValue();
            if (min > max) {
                showPopupMessage("Right Axis Min value must be less than Max value.", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }

    public void showPopupMessage(String message, int messageType) {
        JFrame popupFrame = new JFrame();
        popupFrame.setIconImages(GraphClient.getIconsImages());
        JOptionPane.showMessageDialog(popupFrame, message, "Yukon Trending", messageType);
        return;
    }
}
