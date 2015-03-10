package com.cannontech.dbeditor.wizard.device.lmscenario;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.table.MultiJComboCellEditor;
import com.cannontech.common.gui.table.MultiJComboCellRenderer;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.JTextFieldTimeEntry;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.gui.util.TreeFindPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class LMScenarioProgramSettingsPanel extends DataInputPanel {
    
    private LMGearDao lmGearDao = YukonSpringHook.getBean(LMGearDao.class);
    private IDatabaseCache cache = DefaultDatabaseCache.getInstance();
    
    private IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private JScrollPane ivjProgramsScrollPane = null;
    private JTable ivjProgramsTable = null;
    private LMControlScenarioProgramTableModel tableModel = null;
    private JList<LiteYukonPAObject> ivjAvailableList = null;
    private JButton ivjJButtonAdd = null;
    private JButton ivjJButtonRemove = null;
    private JScrollPane ivjJScrollPaneAvailable = null;
    private JLabel ivjNameJLabel = null;
    private JTextField ivjNameJTextField = null;
    private Vector<LiteGear> allGears = null;
    private static OkCancelDialog dialog = null;
    private static final TreeFindPanel FND_PANEL = new TreeFindPanel();

    class IvjEventHandler implements ActionListener, MouseListener, CaretListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == getJButtonAdd()) {
                connEtoC2();
            }
            if (e.getSource() == getJButtonRemove()) {
                connEtoC3();
            }
        };

        @Override
        public void caretUpdate(CaretEvent e) {
            if (e.getSource() == getNameJTextField()) {
                connEtoC4();
            }
        };

        @Override
        public void mouseClicked(MouseEvent e) {
            // We just care about pressed.
        };

        @Override
        public void mouseEntered(MouseEvent e) {
            // We just care about pressed.
        };

        @Override
        public void mouseExited(MouseEvent e) {
            // We just care about pressed.
        };

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() == getProgramsTable()) {
                connEtoC1();
            }
        };

        @Override
        public void mouseReleased(MouseEvent e) {
            // We just care about pressed.
        };
    };

    private JLabel ivjJLabelSearchInstructions = null;

    public LMScenarioProgramSettingsPanel() {
        initialize();
    }

    private void connEtoC1() {
        try {
            programsTable_MousePressed();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connEtoC2() {
        try {
            jButtonAdd_ActionPerformed();
            fireInputUpdate();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connEtoC3() {
        try {
            jButtonRemove_ActionPerformed();
            fireInputUpdate();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connEtoC4() {
        try {
            fireInputUpdate();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JList<LiteYukonPAObject> getAvailableList() {
        if (ivjAvailableList == null) {
            try {
                ivjAvailableList = new JList<>();
                ivjAvailableList.setName("AvailableList");
                ivjAvailableList.setBounds(0, 0, 160, 120);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAvailableList;
    }

    private JButton getJButtonAdd() {
        if (ivjJButtonAdd == null) {
            try {
                ivjJButtonAdd = new JButton();
                ivjJButtonAdd.setName("JButtonAdd");
                ivjJButtonAdd.setFont(new Font("Arial", 1, 12));
                ivjJButtonAdd.setText("Assign to Scenario");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJButtonAdd;
    }

    private JButton getJButtonRemove() {
        if (ivjJButtonRemove == null) {
            try {
                ivjJButtonRemove = new JButton();
                ivjJButtonRemove.setName("JButtonRemove");
                ivjJButtonRemove.setFont(new Font("Arial", 1, 12));
                ivjJButtonRemove.setText("Remove from Scenario");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJButtonRemove;
    }

    private JLabel getJLabelSearchInstructions() {
        if (ivjJLabelSearchInstructions == null) {
            try {
                ivjJLabelSearchInstructions = new JLabel();
                ivjJLabelSearchInstructions.setName("JLabelSearchInstructions");
                ivjJLabelSearchInstructions.setFont(new Font("Arial", 1, 10));
                ivjJLabelSearchInstructions.setText("Click the table and press Alt + S to search");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelSearchInstructions;
    }

    private JScrollPane getJScrollPaneAvailable() {
        if (ivjJScrollPaneAvailable == null) {
            try {
                TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new TitleBorder();
                ivjLocalBorder1.setTitleFont(new Font("Arial", 1, 12));
                ivjLocalBorder1.setTitle("Available Programs (must belong to a Control Area to be listed)");
                ivjJScrollPaneAvailable = new JScrollPane();
                ivjJScrollPaneAvailable.setName("JScrollPaneAvailable");
                ivjJScrollPaneAvailable.setPreferredSize(new Dimension(404, 130));
                ivjJScrollPaneAvailable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                ivjJScrollPaneAvailable.setBorder(ivjLocalBorder1);
                ivjJScrollPaneAvailable.setMinimumSize(new Dimension(404, 130));
                getJScrollPaneAvailable().setViewportView(getAvailableList());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJScrollPaneAvailable;
    }

    private JLabel getNameJLabel() {
        if (ivjNameJLabel == null) {
            try {
                ivjNameJLabel = new JLabel();
                ivjNameJLabel.setName("NameJLabel");
                ivjNameJLabel.setFont(new Font("Arial", 1, 12));
                ivjNameJLabel.setText("Scenario Name: ");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNameJLabel;
    }

    private JTextField getNameJTextField() {
        if (ivjNameJTextField == null) {
            try {
                ivjNameJTextField = new JTextField();
                ivjNameJTextField.setName("NameJTextField");
                ivjNameJTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
                    PaoUtils.ILLEGAL_NAME_CHARS));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNameJTextField;
    }

    private JScrollPane getProgramsScrollPane() {
        if (ivjProgramsScrollPane == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitleFont(new Font("Arial", 1, 12));
                ivjLocalBorder.setTitle("Assigned Programs");
                ivjProgramsScrollPane = new JScrollPane();
                ivjProgramsScrollPane.setName("ProgramsScrollPane");
                ivjProgramsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                ivjProgramsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                ivjProgramsScrollPane.setBorder(ivjLocalBorder);
                ivjProgramsScrollPane.setPreferredSize(new Dimension(404, 155));
                ivjProgramsScrollPane.setFont(new Font("dialog", 0, 14));
                ivjProgramsScrollPane.setMinimumSize(new Dimension(404, 155));
                getProgramsScrollPane().setViewportView(getProgramsTable());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjProgramsScrollPane;
    }

    private JTable getProgramsTable() {
        if (ivjProgramsTable == null) {
            try {
                ivjProgramsTable = new JTable();
                ivjProgramsTable.setName("ProgramsTable");
                getProgramsScrollPane().setColumnHeaderView(ivjProgramsTable.getTableHeader());

                ivjProgramsTable.setAutoCreateColumnsFromModel(true);
                ivjProgramsTable.setModel(getTableModel());
                ivjProgramsTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
                ivjProgramsTable.setPreferredSize(new Dimension(385, 5000));
                ivjProgramsTable.setBounds(0, 0, 385, 5000);
                ivjProgramsTable.setMaximumSize(new Dimension(32767, 32767));
                ivjProgramsTable.setPreferredScrollableViewportSize(new Dimension(200, 8000));
                ivjProgramsTable.setGridColor(Color.black);
                ivjProgramsTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                ivjProgramsTable.setRowHeight(20);

                // Do any column specific initialization here, with the exception of the gear column.
                TableColumn nameColumn =
                    getProgramsTable().getColumnModel().getColumn(
                        LMControlScenarioProgramTableModel.PROGRAMLITEPAO_COLUMN);
                TableColumn startOffsetColumn =
                    getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTOFFSET_COLUMN);
                TableColumn stopOffsetColumn =
                    getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STOPOFFSET_COLUMN);

                nameColumn.setPreferredWidth(100);
                startOffsetColumn.setPreferredWidth(60);

                // create our editor for the time fields
                JTextFieldTimeEntry field = new JTextFieldTimeEntry();
                field.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        fireInputUpdate();
                    };
                });

                field.setHorizontalAlignment(JTextField.CENTER);
                DefaultCellEditor ed = new DefaultCellEditor(field);
                ed.setClickCountToStart(1);
                startOffsetColumn.setCellEditor(ed);
                stopOffsetColumn.setCellEditor(ed);

                // create our renderer for the Integer fields
                DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
                rend.setHorizontalAlignment(field.getHorizontalAlignment());
                startOffsetColumn.setCellRenderer(rend);
                stopOffsetColumn.setCellRenderer(rend);

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjProgramsTable;
    }

    private LMControlScenarioProgramTableModel getTableModel() {
        if (tableModel == null) {
            tableModel = new LMControlScenarioProgramTableModel();
        }

        return tableModel;
    }

    @Override
    public Object getValue(Object obj) {
        // make sure cells get saved even though they might be currently being edited
        if (getProgramsTable().isEditing()) {
            getProgramsTable().getCellEditor().stopCellEditing();
        }

        LMScenario scenario = (LMScenario) obj;

        if (scenario == null) {
            scenario = (LMScenario) LMFactory.createLoadManagement(PaoType.LM_SCENARIO);
        }

        scenario.setScenarioName(getNameJTextField().getText());

        Vector<LMControlScenarioProgram> assignedPrograms = new Vector<>();

        for (int j = 0; j < getProgramsTable().getRowCount(); j++) {
            LMControlScenarioProgram newScenarioProgram = new LMControlScenarioProgram();

            // program name needs to be converted to id for storage
            LiteYukonPAObject thePAO = getTableModel().getProgramLitePAOAt(j);

            newScenarioProgram.setProgramID(thePAO.getLiteID());

            newScenarioProgram.setStartOffset(JTextFieldTimeEntry.getTimeTotalSeconds(getTableModel().getStartOffsetAt(
                j)));

            newScenarioProgram.setStopOffset(JTextFieldTimeEntry.getTimeTotalSeconds(getTableModel().getStopOffsetAt(j)));

            newScenarioProgram.setStartGear(getTableModel().getStartGearAt(j).getGearNumber());

            assignedPrograms.addElement(newScenarioProgram);
        }

        scenario.setAllThePrograms(assignedPrograms);

        return scenario;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error(exception.getMessage(), exception);
    }

    private void initConnections() throws Exception {
        dialog = new OkCancelDialog(SwingUtil.getParentFrame(this), "Search", true, FND_PANEL);

        final AbstractAction searchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!dialog.isShowing()) {
                    dialog.setSize(250, 120);
                    dialog.setLocationRelativeTo(LMScenarioProgramSettingsPanel.this);
                    dialog.setVisible(true);

                    if (dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED) {
                        Object value = FND_PANEL.getValue(null);
                        boolean found = false;

                        if (value != null) {
                            int numberOfRows = getTableModel().getRowCount();
                            for (int j = 0; j < numberOfRows; j++) {
                                String programName = ((LiteBase) getTableModel().getValueAt(j, 0)).toString();
                                if (programName.compareTo(value.toString()) == 0) {
                                    getProgramsTable().setRowSelectionInterval(j, j);
                                    getProgramsTable().scrollRectToVisible(
                                        new Rectangle(0, getProgramsTable().getRowHeight() * (j + 1)
                                            - getProgramsTable().getRowHeight(), // just an estimate that
                                                                                 // works!!
                                            100, 100));
                                    found = true;
                                    break;
                                }
                                // in case they don't know the full name and just entered a partial
                                if (programName.indexOf(value.toString()) > -1
                                    && programName.indexOf(value.toString()) < 2) {
                                    getProgramsTable().setRowSelectionInterval(j, j);
                                    getProgramsTable().scrollRectToVisible(
                                        new Rectangle(0, getProgramsTable().getRowHeight() * (j + 1)
                                            - getProgramsTable().getRowHeight(), // just an estimate that
                                                                                 // works!!
                                            100, 100));
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                JOptionPane.showMessageDialog(LMScenarioProgramSettingsPanel.this,
                                    "Unable to find your selected item", "Item Not Found",
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                    dialog.setVisible(false);
                }
            }
        };

        final AbstractAction searchActionJList = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!dialog.isShowing()) {
                    dialog.setSize(250, 120);
                    dialog.setLocationRelativeTo(LMScenarioProgramSettingsPanel.this);
                    dialog.setVisible(true);

                    if (dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED) {
                        Object value = FND_PANEL.getValue(null);
                        boolean found = false;

                        if (value != null) {
                            int numberOfRows = getAvailableList().getModel().getSize();
                            for (int j = 0; j < numberOfRows; j++) {
                                String programName =
                                    ((LiteBase) getAvailableList().getModel().getElementAt(j)).toString();
                                if (programName.compareTo(value.toString()) == 0) {
                                    getAvailableList().setSelectedIndex(j);
                                    getAvailableList().scrollRectToVisible(
                                        new Rectangle(0, getAvailableList().getHeight() * (j + 1)
                                            - getAvailableList().getHeight(), // just an estimate that works!!
                                            100, 100));
                                    found = true;
                                    break;
                                }
                                // in case they don't know the full name and just entered a partial
                                if (programName.indexOf(value.toString()) > -1
                                    && programName.indexOf(value.toString()) < 2) {
                                    getAvailableList().setSelectedIndex(j);
                                    getAvailableList().scrollRectToVisible(
                                        new Rectangle(0, getAvailableList().getHeight() * (j + 1)
                                            - getAvailableList().getHeight(), // just an estimate that works!!
                                            100, 100));
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                JOptionPane.showMessageDialog(LMScenarioProgramSettingsPanel.this,
                                    "Unable to find your selected item", "Item Not Found",
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                    dialog.setVisible(false);
                }
            }
        };

        // do the secret magic key combo: ALT + S
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK, true);
        getProgramsTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "FindAction");
        getProgramsTable().getActionMap().put("FindAction", searchAction);
        getAvailableList().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "FindAction");
        getAvailableList().getActionMap().put("FindAction", searchActionJList);

        getProgramsTable().addMouseListener(ivjEventHandler);
        getJButtonAdd().addActionListener(ivjEventHandler);
        getJButtonRemove().addActionListener(ivjEventHandler);
        getNameJTextField().addCaretListener(ivjEventHandler);
    }

    private void initialize() {
        try {
            setName("LMScenarioProgramSettingsPanel");
            setPreferredSize(new Dimension(420, 360));
            setLayout(new GridBagLayout());
            setSize(420, 374);
            setMinimumSize(new Dimension(420, 360));
            setMaximumSize(new Dimension(420, 360));

            GridBagConstraints constraintsProgramsScrollPane = new GridBagConstraints();
            constraintsProgramsScrollPane.gridx = 1;
            constraintsProgramsScrollPane.gridy = 4;
            constraintsProgramsScrollPane.gridwidth = 3;
            constraintsProgramsScrollPane.fill = GridBagConstraints.BOTH;
            constraintsProgramsScrollPane.weightx = 1.0;
            constraintsProgramsScrollPane.weighty = 1.0;
            constraintsProgramsScrollPane.insets = new Insets(1, 8, 1, 8);
            add(getProgramsScrollPane(), constraintsProgramsScrollPane);

            GridBagConstraints constraintsJButtonAdd = new GridBagConstraints();
            constraintsJButtonAdd.gridx = 1;
            constraintsJButtonAdd.gridy = 3;
            constraintsJButtonAdd.gridwidth = 2;
            constraintsJButtonAdd.ipadx = 44;
            constraintsJButtonAdd.insets = new Insets(2, 12, 0, 7);
            add(getJButtonAdd(), constraintsJButtonAdd);

            GridBagConstraints constraintsJButtonRemove = new GridBagConstraints();
            constraintsJButtonRemove.gridx = 3;
            constraintsJButtonRemove.gridy = 3;
            constraintsJButtonRemove.ipadx = 20;
            constraintsJButtonRemove.insets = new Insets(2, 7, 0, 24);
            add(getJButtonRemove(), constraintsJButtonRemove);

            GridBagConstraints constraintsJScrollPaneAvailable = new GridBagConstraints();
            constraintsJScrollPaneAvailable.gridx = 1;
            constraintsJScrollPaneAvailable.gridy = 2;
            constraintsJScrollPaneAvailable.gridwidth = 3;
            constraintsJScrollPaneAvailable.fill = GridBagConstraints.BOTH;
            constraintsJScrollPaneAvailable.weightx = 1.0;
            constraintsJScrollPaneAvailable.weighty = 1.0;
            constraintsJScrollPaneAvailable.insets = new Insets(4, 8, 2, 8);
            add(getJScrollPaneAvailable(), constraintsJScrollPaneAvailable);

            GridBagConstraints constraintsNameJTextField = new GridBagConstraints();
            constraintsNameJTextField.gridx = 2;
            constraintsNameJTextField.gridy = 1;
            constraintsNameJTextField.gridwidth = 2;
            constraintsNameJTextField.fill = GridBagConstraints.HORIZONTAL;
            constraintsNameJTextField.weightx = 1.0;
            constraintsNameJTextField.ipadx = 195;
            constraintsNameJTextField.insets = new Insets(7, 3, 4, 98);
            add(getNameJTextField(), constraintsNameJTextField);

            GridBagConstraints constraintsNameJLabel = new GridBagConstraints();
            constraintsNameJLabel.gridx = 1;
            constraintsNameJLabel.gridy = 1;
            constraintsNameJLabel.ipadx = 9;
            constraintsNameJLabel.insets = new Insets(11, 16, 6, 2);
            add(getNameJLabel(), constraintsNameJLabel);

            GridBagConstraints constraintsJLabelSearchInstructions = new GridBagConstraints();
            constraintsJLabelSearchInstructions.gridx = 1;
            constraintsJLabelSearchInstructions.gridy = 5;
            constraintsJLabelSearchInstructions.gridwidth = 3;
            constraintsJLabelSearchInstructions.ipadx = 50;
            constraintsJLabelSearchInstructions.ipady = 3;
            constraintsJLabelSearchInstructions.insets = new Insets(2, 12, 7, 152);
            add(getJLabelSearchInstructions(), constraintsJLabelSearchInstructions);
            initConnections();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public boolean isInputValid() {
        if (getNameJTextField().getText() == null || getNameJTextField().getText().length() <= 0) {
            setErrorString("The Name text field must be filled in");
            return false;
        }

        return true;
    }

    public void jButtonAdd_ActionPerformed() {
        List<LiteYukonPAObject> availablePrograms = getAvailableList().getSelectedValuesList();

        // also need to update the available programs list
        Vector<LiteYukonPAObject> allAvailable = new Vector<>(getAvailableList().getModel().getSize());
        for (int i = 0; i < getAvailableList().getModel().getSize(); i++) {
            allAvailable.add(getAvailableList().getModel().getElementAt(i));
        }

        for (LiteYukonPAObject availableProgram : availablePrograms) {
            int programId = availableProgram.getLiteID();
            LiteYukonPAObject thePAO = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(programId);

            DefaultComboBoxModel<LiteGear> comboModel = new DefaultComboBoxModel<>();
            // do the gears, man
            LiteGear startingGear = null;
            for (int d = 0; d < allGears.size(); d++) {
                if (allGears.elementAt(d).getOwnerID() == programId) {
                    // add gear to the combobox model for the table
                    comboModel.addElement(allGears.elementAt(d));

                    if (allGears.elementAt(d).getGearNumber() == 1) {
                        // it's a newly added program; just take the first gear for the startgear
                        startingGear = allGears.elementAt(d);
                        comboModel.setSelectedItem(startingGear);
                    }
                }
            }
            // add the new row
            getTableModel().addRowValue(thePAO, "0:00", "0:00", startingGear);

            @SuppressWarnings("unchecked")
            MultiJComboCellEditor<LiteGear> cellEditor =
                (MultiJComboCellEditor<LiteGear>) getProgramsTable().getCellEditor(getTableModel().getRowCount() + 1, 3);
            cellEditor.addModel(comboModel);

            // autoscroll to show new additions
            getProgramsTable().scrollRectToVisible(
                new Rectangle(0, getProgramsTable().getRowHeight() * (getProgramsTable().getRowCount() - 4)
                    - getProgramsTable().getRowHeight(), // just an estimate that works!!
                    100, 100));

            // update the available programs list
            for (int y = 0; y < allAvailable.size(); y++) {
                if (programId == (allAvailable.elementAt(y).getLiteID())) {
                    allAvailable.removeElementAt(y);
                }
            }
        }
        // update the available programs list
        getAvailableList().setListData(allAvailable);

        repaint();
        return;
    }

    public void jButtonRemove_ActionPerformed() {
        int[] selectedRows = getProgramsTable().getSelectedRows();
        Vector<LiteYukonPAObject> allAvailable =
            new Vector<>(getAvailableList().getModel().getSize() + selectedRows.length);
        for (int i = 0; i < getAvailableList().getModel().getSize(); i++) {
            allAvailable.add(getAvailableList().getModel().getElementAt(i));
        }

        for (int index = selectedRows.length - 1; index >= 0; index--) {
            LiteYukonPAObject thePAO = getTableModel().getProgramLitePAOAt(selectedRows[index]);

            allAvailable.addElement(thePAO);
            // renderer is also automatically updated since it uses the same vector of combo box models
            MultiJComboCellEditor<?> cellEditor = (MultiJComboCellEditor<?>) getProgramsTable().getCellEditor(index, 3);
            cellEditor.removeModel(selectedRows[index]);
            getTableModel().removeRowValue(selectedRows[index]);
        }

        getAvailableList().setListData(allAvailable);
        repaint();

        return;
    }

    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame();
            LMScenarioProgramSettingsPanel aLMScenarioProgramSettingsPanel;
            aLMScenarioProgramSettingsPanel = new LMScenarioProgramSettingsPanel();
            frame.setContentPane(aLMScenarioProgramSettingsPanel);
            frame.setSize(aLMScenarioProgramSettingsPanel.getSize());
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.setVisible(true);
            Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of DataInputPanel");
            exception.printStackTrace(System.out);
        }
    }

    public void populateAvailableList() {
        Vector<LiteYukonPAObject> availablePrograms = new Vector<>();

        if (allGears == null) {
            allGears = new Vector<>();
        }

        synchronized (cache) {
            List<LiteYukonPAObject> progs = cache.getAllLoadManagement();
            allGears.addAll(lmGearDao.getAllLiteGears());
            Vector<Integer> programsInAControlArea = LMControlAreaProgram.getAllProgramsInControlAreas();
            try {
                for (LiteYukonPAObject program : progs) {
                    Integer progID = program.getLiteID();

                    for (int j = 0; j < programsInAControlArea.size(); j++) {
                        if (progID.compareTo(programsInAControlArea.elementAt(j)) == 0) {
                            if (program.getPaoType().isDirectProgram()) {
                                availablePrograms.addElement(program);
                                programsInAControlArea.removeElementAt(j);
                            }
                        }
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace(); // something is up
            }
        }
        getAvailableList().setListData(availablePrograms);

    }

    private void programsTable_MousePressed() {
        fireInputUpdate();
    }

    @Override
    public void setValue(Object obj) {
        LMScenario scenario = (LMScenario) obj;

        if (scenario == null) {
            scenario = (LMScenario) LMFactory.createLoadManagement(PaoType.LM_SCENARIO);
        }

        getNameJTextField().setText(scenario.getScenarioName());

        populateAvailableList();

        Vector<LMControlScenarioProgram> assignedPrograms = scenario.getAllThePrograms();
        TableColumn startGearColumn =
            getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);
        startGearColumn.setPreferredWidth(100);
        Vector<DefaultComboBoxModel<LiteGear>> models = new Vector<>();

        // also need to update the available programs list
        Vector<LiteYukonPAObject> allAvailable = new Vector<>(getAvailableList().getModel().getSize());
        for (int i = 0; i < getAvailableList().getModel().getSize(); i++) {
            allAvailable.add(getAvailableList().getModel().getElementAt(i));
        }

        for (int j = 0; j < assignedPrograms.size(); j++) {
            LMControlScenarioProgram lightProgram = assignedPrograms.elementAt(j);
            int progID = lightProgram.getProgramID();
            LiteYukonPAObject thePAO = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(progID);
            LiteGear startingGear = null;

            // do the gears, man
            DefaultComboBoxModel<LiteGear> comboModel = new DefaultComboBoxModel<>();
            for (int y = 0; y < allGears.size(); y++) {
                if (allGears.elementAt(y).getOwnerID() == progID) {
                    // add gear to the combobox model for the table
                    comboModel.addElement(allGears.elementAt(y));

                    if (allGears.elementAt(y).getGearNumber() == lightProgram.getStartGear().intValue()) {
                        // find the startgear
                        startingGear = allGears.elementAt(y);
                        comboModel.setSelectedItem(startingGear);
                    }
                }
            }
            models.addElement(comboModel);

            // add the new row
            getTableModel().addRowValue(thePAO, JTextFieldTimeEntry.getTimeTextForField(lightProgram.getStartOffset()),
                JTextFieldTimeEntry.getTimeTextForField(lightProgram.getStopOffset()), startingGear);

            // make sure that the available programs list is not showing these assigned programs
            for (int y = 0; y < allAvailable.size(); y++) {
                if (progID == (allAvailable.elementAt(y).getLiteID())) {
                    allAvailable.removeElementAt(y);
                }
            }
        }
        // update the available programs list
        getAvailableList().setListData(allAvailable);

        // set up the combo box renderers and editors for the gear column
        startGearColumn.setCellEditor(new MultiJComboCellEditor<LiteGear>(models));
        startGearColumn.setCellRenderer(new MultiJComboCellRenderer(models));
    }
}
