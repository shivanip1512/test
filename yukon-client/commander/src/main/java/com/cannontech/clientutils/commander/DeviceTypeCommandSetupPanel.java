package com.cannontech.clientutils.commander;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.dnd.DragAndDropListener;
import com.cannontech.common.gui.dnd.DragAndDropTable;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.command.DeviceTypeCommand;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.command.Command;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.database.db.command.CommandCategoryUtil;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.BasicServerConnection;
import com.cannontech.yukon.conns.ConnPool;

public class DeviceTypeCommandSetupPanel extends JPanel implements DragAndDropListener, ActionListener, ListSelectionListener {
    
    private CommandDao commandDao = YukonSpringHook.getBean(CommandDao.class);
    private PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
    
    private JDialog dialog;
    private String dialogTitle;
    private JButton ivjCancelButton;
    private JButton ivjOkButton;
    private JPanel ivjOkCancelButtonPanel;
    private String deviceType;
    private DragAndDropTable ivjDandDCommandTable;
    private JScrollPane ivjDandDCommandTableScrollPane;
    private JButton ivjAddCommandButton;
    private JButton ivjRemoveCommandButton;
    private JButton ivjEditCommandButton;
    private JPanel ivjAddRemovePanel;
    private JPanel ivjDeviceTypeCommandSetupPanel;
    private JList<String> ivjCategoryList;
    private JScrollPane ivjCategoryListScrollPane;
    
    public DeviceTypeCommandSetupPanel() {
        super();
        initialize();
    }
    
    public DeviceTypeCommandSetupPanel(String deviceType_) {
        super();
        initialize();
        setDeviceType(deviceType_);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        
        if (event.getSource() == getAddCommandButton()) {
            
            CommandSetupPanel selectCreatePanel = new CommandSetupPanel();
            selectCreatePanel.setDialogTitle("Commands");
            // should return a Command value
            Object o = selectCreatePanel.showAdvancedOptions(this.dialog);
            
            if (o != null) {
                /** Contains Command value */
                if (o instanceof Command) {
                    ((Command) o).setCategory(getDeviceType());
                    writeDBChange((Command) o, TransactionType.INSERT);
                    Command cmd = (Command) o;
                    System.out.println(getDeviceType());
                    if (CommandCategoryUtil.isCommandCategory(getDeviceType())) {
                        // The deviceType is actually a category, not a
                        // deviceType from YukonPaobject.paoType column
                        List<PaoType> paoTypes = CommandCategoryUtil.getAllTypesForCategory(CommandCategory.getForDbString(getDeviceType()));
                        DeviceTypeCommand dbP = null;
                        for (PaoType paoType : paoTypes) {
                            // Add to DeviceTypeCommand table, entries for all
                            // deviceTypes! yikes...I know
                            dbP = new DeviceTypeCommand();
                            dbP.setDeviceCommandID(com.cannontech.database.db.command.DeviceTypeCommand.getNextID(CtiUtilities.getDatabaseAlias()));
                            dbP.setDeviceType(paoType.getDbString());
                            
                            // Hey, default it, we're going to update it in a bit anyway right?
                            dbP.setDisplayOrder(new Integer(20));
                            
                            dbP.setVisibleFlag(new Character('Y'));
                            dbP.setCommand(cmd);
                            // TODO - Change to be the RoleProperty id for this user
                            dbP.setCommandGroupID(new Integer(com.cannontech.database.db.command.DeviceTypeCommand.DEFAULT_COMMANDS_GROUP_ID));
                            writeDBChange(dbP, TransactionType.INSERT);
                        }
                        if (dbP != null)
                            ((DeviceTypeCommandsTableModel) getDandDCommandTable().getModel()).addRowToEnd(dbP);
                    } else {
                        // Add to DeviceTypeCommand table, entries for all
                        // deviceTypes! yikes...I know
                        DeviceTypeCommand dbP = new DeviceTypeCommand();
                        dbP.setDeviceCommandID(com.cannontech.database.db.command.DeviceTypeCommand.getNextID(CtiUtilities.getDatabaseAlias()));
                        dbP.setDeviceType(getDeviceType());
                        
                        // Hey, default it, we're going to update it in a bit anyway right?
                        dbP.setDisplayOrder(new Integer(20));
                        
                        dbP.setVisibleFlag(new Character('Y'));
                        dbP.setCommand(cmd);

                        // TODO - Change to be the RoleProperty id for this user
                        dbP.setCommandGroupID(new Integer(com.cannontech.database.db.command.DeviceTypeCommand.DEFAULT_COMMANDS_GROUP_ID));
                        writeDBChange(dbP, TransactionType.INSERT);

                        ((DeviceTypeCommandsTableModel) getDandDCommandTable().getModel()).addRowToEnd(dbP);
                    }
                }
            }
        } else if (event.getSource() == getRemoveCommandButton()) {
            
            String message = "Are you sure you want to delete this Entry?";
            if (CommandCategoryUtil.isCommandCategory(getDeviceType())) {
                message = "This entry will be deleted from all associated device types, do you want to continue?";
            }
            
            JFrame popupFrame = new JFrame();
            popupFrame.setIconImages(YukonCommander.getIconsImages());
            int response = JOptionPane.showConfirmDialog(popupFrame, message, "Verify Command Deletion", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.OK_OPTION) {
                // DELETE THE DEVICETYPECOMMAND FOR ALL DEVICETYPES PER THIS "CATEGORY"
                int rowToRemove = getDandDCommandTable().getSelectedRow();

                DeviceTypeCommand dbP = ((DeviceTypeCommandsTableModel) getDandDCommandTable().getModel()).getRow(rowToRemove);
                if (dbP.getDeviceCommandID() == null) {
                    // A non-category entry, (category entries do not have a deviceCommandID,
                    // remove the command and associatively any deviceTypeCommands too.
                    Command cmd = dbP.getCommand();
                    writeDBChange(cmd, TransactionType.DELETE);
                } else { 
                    // Remove just the deviceTypeCommand, Command will "linger" in the database for now TODO
                    writeDBChange(dbP, TransactionType.DELETE);
                }
                ((DeviceTypeCommandsTableModel) getDandDCommandTable().getModel()).removeRow(rowToRemove);
                getDandDCommandTable().getSelectionModel().clearSelection();
            }
        } else if (event.getSource() == getEditCommandButton()) {
            
            int rowToEdit = getDandDCommandTable().getSelectedRow();
            DeviceTypeCommand dbP = ((DeviceTypeCommandsTableModel) getDandDCommandTable().getModel()).getRow(rowToEdit);
            Command cmd = dbP.getCommand();
            
            try {
                Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, cmd);
                cmd = (Command) t.execute();
            } catch (TransactionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            CommandSetupPanel selectCreatePanel = new CommandSetupPanel(cmd);
            selectCreatePanel.setDialogTitle("Edit Command");
            // should return a Command value
            Object o = selectCreatePanel.showAdvancedOptions(this.dialog);
            if (o != null) {
                /** Contains Command value */
                if (o instanceof Command) {
                    cmd = (Command) o;
                    writeDBChange(cmd, TransactionType.UPDATE);
                    ((DeviceTypeCommandsTableModel) getDandDCommandTable().getModel()).fireTableRowsUpdated(rowToEdit, rowToEdit);
                }
            }
        } else if (event.getSource() == getOkButton()) {
            saveChanges();
            exit();
        } else if (event.getSource() == getCancelButton()) {
            exit();
        }
    }
    
    @Override
    public void drop_actionPerformed(EventObject newEvent) { }
    
    public void exit() {
        removeAll();
        setVisible(false);
        dialog.dispose();
    }
    
    private JButton getAddCommandButton() {
        
        if (ivjAddCommandButton == null) {
            ivjAddCommandButton = new JButton();
            ivjAddCommandButton.setName("AddCommandButton");
            ivjAddCommandButton.setText("Add");
            ivjAddCommandButton.addActionListener(this);
        }
        
        return ivjAddCommandButton;
    }
    
    private JPanel getAddRemovePanel() {
        
        if (ivjAddRemovePanel == null) {
            
            ivjAddRemovePanel = new JPanel();
            ivjAddRemovePanel.setName("AddRemovePanel");
            ivjAddRemovePanel.setLayout(new GridBagLayout());
            
            GridBagConstraints constraintsAddCommandButton = new GridBagConstraints();
            constraintsAddCommandButton.gridx = 0;
            constraintsAddCommandButton.gridy = 0;
            constraintsAddCommandButton.fill = GridBagConstraints.HORIZONTAL;
            constraintsAddCommandButton.insets = new Insets(5, 5, 5, 5);
            getAddRemovePanel().add(getAddCommandButton(), constraintsAddCommandButton);
            
            GridBagConstraints constraintsEditCommandButton = new GridBagConstraints();
            constraintsEditCommandButton.gridx = 0;
            constraintsEditCommandButton.gridy = 1;
            constraintsEditCommandButton.insets = new Insets(5, 5, 5, 5);
            getAddRemovePanel().add(getEditCommandButton(), constraintsEditCommandButton);
            
            GridBagConstraints constraintsRemoveCommandButton = new GridBagConstraints();
            constraintsRemoveCommandButton.gridx = 0;
            constraintsRemoveCommandButton.gridy = 2;
            constraintsRemoveCommandButton.insets = new Insets(5, 5, 5, 5);
            getAddRemovePanel().add(getRemoveCommandButton(), constraintsRemoveCommandButton);
        }
        
        return ivjAddRemovePanel;
    }
    
    public JButton getCancelButton() {
        
        if (ivjCancelButton == null) {
            ivjCancelButton = new JButton();
            ivjCancelButton.setName("CancelButton");
            ivjCancelButton.setText("Cancel");
            ivjCancelButton.addActionListener(this);
        }
        
        return ivjCancelButton;
    }
    
    private JList<String> getCategoryList() {
        
        if (ivjCategoryList == null) {
            ivjCategoryList = new JList<String>();
            ivjCategoryList.setName("CategoryList");
            ivjCategoryList.setBounds(0, 0, 137, 332);
            ivjCategoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            // Add all the "Group" categories
            DefaultListModel<String> model = new DefaultListModel<>();
            for (CommandCategory category : CommandCategoryUtil.getAllCategories()) {
                model.addElement(category.getDbString());
            }
            
            // Add "String" type categories, ex. LCRSerial
            model.addElement(CommandCategory.DEVICE_GROUP.getDbString());
            model.addElement(CommandCategory.EXPRESSCOM_SERIAL.getDbString());
            model.addElement(CommandCategory.SERIALNUMBER.getDbString());
            model.addElement(CommandCategory.VERSACOM_SERIAL.getDbString());
            
            List<PaoType> existingPaoTypes = paoDao.getExistingPaoTypes();
            // Add the distinct device types
            for (PaoType paoType : existingPaoTypes) { 
                model.addElement(paoType.getDbString());
            }
            // TODO sort the elements??
            ivjCategoryList.setModel(model);
            ivjCategoryList.addListSelectionListener(this);
        }
        
        return ivjCategoryList;
    }
    
    private JScrollPane getCategoryListScrollPane() {
        
        if (ivjCategoryListScrollPane == null) {
            ivjCategoryListScrollPane = new JScrollPane();
            ivjCategoryListScrollPane.setName("CategoryListScrollPane");
            getCategoryListScrollPane().setViewportView(getCategoryList());
        }
        
        return ivjCategoryListScrollPane;
    }
    
    protected BasicServerConnection getClientConnection() {
        return ConnPool.getInstance().getDefDispatchConn();
    }
    
    private DragAndDropTable getDandDCommandTable() {
        
        if (ivjDandDCommandTable == null) {
            
            ivjDandDCommandTable = new DragAndDropTable(new DeviceTypeCommandTableRenderer());
            
            ivjDandDCommandTable.setName("DandDCommandTable");
            getDandDCommandTableScrollPane().setColumnHeaderView(ivjDandDCommandTable.getTableHeader());
            
            ivjDandDCommandTable.setModel(new DeviceTypeCommandsTableModel());
            ivjDandDCommandTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
            ivjDandDCommandTable.setBounds(0, 0, 200, 200);
            getDandDCommandTableScrollPane().getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
            
            ivjDandDCommandTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            ivjDandDCommandTable.setToolTipText("Click-and-drag to reorder");
            
            ivjDandDCommandTable.getColumnModel().getColumn(DeviceTypeCommandsTableModel.LABEL_COLUMN).setPreferredWidth(150);
            ivjDandDCommandTable.getColumnModel().getColumn(DeviceTypeCommandsTableModel.COMMAND_COLUMN).setPreferredWidth(150);
            ivjDandDCommandTable.getColumnModel().getColumn(DeviceTypeCommandsTableModel.VISIBILTY_COLUMN).setPreferredWidth(15);
            ivjDandDCommandTable.getColumnModel().getColumn(DeviceTypeCommandsTableModel.CATEGORY_COLUMN).setPreferredWidth(50);
            
            ivjDandDCommandTable.addDragAndDropListener(this);
            ivjDandDCommandTable.getSelectionModel().addListSelectionListener(this);
        }
        
        return ivjDandDCommandTable;
    }
    
    private JScrollPane getDandDCommandTableScrollPane() {
        
        if (ivjDandDCommandTableScrollPane == null) {
            ivjDandDCommandTableScrollPane = new JScrollPane();
            ivjDandDCommandTableScrollPane.setName("DandDCommandTableScrollPane");
            ivjDandDCommandTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            ivjDandDCommandTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            getDandDCommandTableScrollPane().setViewportView(getDandDCommandTable());
        }
        
        return ivjDandDCommandTableScrollPane;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    private JPanel getDeviceTypeCommandSetupPanel() {
        
        if (ivjDeviceTypeCommandSetupPanel == null) {
            ivjDeviceTypeCommandSetupPanel = new JPanel();
            ivjDeviceTypeCommandSetupPanel.setName("DeviceTypeCommandSetupPanel");
            ivjDeviceTypeCommandSetupPanel.setLayout(new GridBagLayout());

            GridBagConstraints constraintsOkCancelButtonPanel = new GridBagConstraints();
            constraintsOkCancelButtonPanel.gridx = 0;
            constraintsOkCancelButtonPanel.gridy = 1;
            constraintsOkCancelButtonPanel.gridwidth = 4;
            constraintsOkCancelButtonPanel.fill = GridBagConstraints.HORIZONTAL;
            constraintsOkCancelButtonPanel.anchor = GridBagConstraints.SOUTH;
            constraintsOkCancelButtonPanel.insets = new Insets(5, 5, 5, 5);
            getDeviceTypeCommandSetupPanel().add(getOkCancelButtonPanel(), constraintsOkCancelButtonPanel);

            GridBagConstraints constraintsDandDCommandTableScrollPane = new GridBagConstraints();
            constraintsDandDCommandTableScrollPane.gridx = 2;
            constraintsDandDCommandTableScrollPane.gridy = 0;
            constraintsDandDCommandTableScrollPane.fill = GridBagConstraints.BOTH;
            constraintsDandDCommandTableScrollPane.weightx = 3.0;
            constraintsDandDCommandTableScrollPane.weighty = 1.0;
            constraintsDandDCommandTableScrollPane.insets = new Insets(5, 5, 5, 5);
            getDeviceTypeCommandSetupPanel().add(getDandDCommandTableScrollPane(), constraintsDandDCommandTableScrollPane);

            GridBagConstraints constraintsAddRemovePanel = new GridBagConstraints();
            constraintsAddRemovePanel.gridx = 3;
            constraintsAddRemovePanel.gridy = 0;
            constraintsAddRemovePanel.fill = GridBagConstraints.BOTH;
            constraintsAddRemovePanel.weighty = 1.0;
            constraintsAddRemovePanel.insets = new Insets(4, 4, 4, 4);
            getDeviceTypeCommandSetupPanel().add(getAddRemovePanel(), constraintsAddRemovePanel);

            GridBagConstraints constraintsCategoryListScrollPane = new GridBagConstraints();
            constraintsCategoryListScrollPane.gridx = 0;
            constraintsCategoryListScrollPane.gridy = 0;
            constraintsCategoryListScrollPane.fill = GridBagConstraints.BOTH;
            constraintsCategoryListScrollPane.weightx = 1.0;
            constraintsCategoryListScrollPane.weighty = 1.0;
            constraintsCategoryListScrollPane.insets = new Insets(5, 5, 5, 5);
            getDeviceTypeCommandSetupPanel().add(getCategoryListScrollPane(), constraintsCategoryListScrollPane);
        }
        
        return ivjDeviceTypeCommandSetupPanel;
    }
    
    public String getDialogTitle() {
        
        if (dialogTitle == null) {
            dialogTitle = "Edit Custom Command File";
        }
        
        return dialogTitle;
    }
    
    public JButton getOkButton() {
        
        if (ivjOkButton == null) {
            ivjOkButton = new JButton();
            ivjOkButton.setName("OkButton");
            ivjOkButton.setPreferredSize(new Dimension(73, 25));
            ivjOkButton.setText("OK");
            ivjOkButton.setMaximumSize(new Dimension(73, 25));
            ivjOkButton.setMinimumSize(new Dimension(73, 25));
            // This listener is not used because it's calling class implements it instead
            ivjOkButton.addActionListener(this);
        }
        
        return ivjOkButton;
    }
    
    private JPanel getOkCancelButtonPanel() {
        
        if (ivjOkCancelButtonPanel == null) {
            ivjOkCancelButtonPanel = new JPanel();
            ivjOkCancelButtonPanel.setName("OkCancelButtonPanel");
            ivjOkCancelButtonPanel.setLayout(new GridBagLayout());

            GridBagConstraints constraintsCancelButton = new GridBagConstraints();
            constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 0;
            constraintsCancelButton.insets = new Insets(10, 20, 10, 20);
            getOkCancelButtonPanel().add(getCancelButton(), constraintsCancelButton);

            GridBagConstraints constraintsOkButton = new GridBagConstraints();
            constraintsOkButton.gridx = 0; constraintsOkButton.gridy = 0;
            constraintsOkButton.insets = new Insets(10, 20, 10, 20);
            getOkCancelButtonPanel().add(getOkButton(), constraintsOkButton);
        }
        
        return ivjOkCancelButtonPanel;
    }
    
    private JButton getRemoveCommandButton() {
        
        if (ivjRemoveCommandButton == null) {
            ivjRemoveCommandButton = new JButton();
            ivjRemoveCommandButton.setName("RemoveCommandButton");
            ivjRemoveCommandButton.setText("Remove");
            ivjRemoveCommandButton.setEnabled(false);
            ivjRemoveCommandButton.addActionListener(this);
        }
        
        return ivjRemoveCommandButton;
    }
    
    private JButton getEditCommandButton() {
        
        if (ivjEditCommandButton == null) {
            ivjEditCommandButton = new JButton();
            ivjEditCommandButton.setName("EditCommandButton");
            ivjEditCommandButton.setText("Edit");
            ivjEditCommandButton.setEnabled(false);
            ivjEditCommandButton.addActionListener(this);
        }
        
        return ivjEditCommandButton;
    }
    
    private void initialize() {
        // set the app to start as close to the center as you can....
        // only works with small gui interfaces.
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int) (d.width * .3), (int) (d.height * .2));
        setName("DeviceTypeCommandSetupFrame");
        setLayout(new GridBagLayout());
        setSize(841, 328);
        setVisible(true);
        
        GridBagConstraints constraintsDeviceTypeCommandSetupPanel = new GridBagConstraints();
        constraintsDeviceTypeCommandSetupPanel.gridx = 0;
        constraintsDeviceTypeCommandSetupPanel.gridy = 0;
        constraintsDeviceTypeCommandSetupPanel.gridwidth = 2;
        constraintsDeviceTypeCommandSetupPanel.gridheight = 6;
        constraintsDeviceTypeCommandSetupPanel.fill = GridBagConstraints.BOTH;
        constraintsDeviceTypeCommandSetupPanel.weightx = 1.0;
        constraintsDeviceTypeCommandSetupPanel.weighty = 1.0;
        constraintsDeviceTypeCommandSetupPanel.insets = new Insets(5, 5, 5, 5);
        add(getDeviceTypeCommandSetupPanel(), constraintsDeviceTypeCommandSetupPanel);
    }
    
    public void saveChanges() {
        
        int rowCount = getDandDCommandTable().getModel().getRowCount();
        if (!CommandCategoryUtil.isCommandCategory(getDeviceType())) {
            DeviceTypeCommandsTableModel model = (DeviceTypeCommandsTableModel) getDandDCommandTable().getModel();
            for (int i = 0; i < rowCount; i++) {
                int newIndex = i + 1;
                DeviceTypeCommand tempValue = model.getRow(i);

                // Get the deviceTypeCommand from cache to see if it has changed at all.
                LiteDeviceTypeCommand cacheLdtc = commandDao.getDeviceTypeCommand(tempValue.getDeviceCommandID().intValue());
                // Store the original value for comparison, this way we only
                // save to db those that have actually changed.
                int origOrder = tempValue.getDeviceTypeCommand().getDisplayOrder().intValue();
                if (origOrder != newIndex || tempValue.getVisibleFlag().charValue() != cacheLdtc.getVisibleFlag()) {
                    // Set the DisplayOrder of the objects based on their order in the table.
                    tempValue.getDeviceTypeCommand().setDisplayOrder(new Integer(newIndex));
                    writeDBChange(tempValue, TransactionType.UPDATE);
                }
            }
        }
    }
    
    public void writeDBChange(DBPersistent item, TransactionType transactionType) {
        
        if (item != null) {
            try {
                Transaction t = Transaction.createTransaction(transactionType, item);
                item = t.execute();

                // write the DBChangeMessage out to Dispatch since it was a
                // Successful ADD
                DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance()
                        .createDBChangeMessages((CTIDbChange) item, transactionType.getDbChangeType());
                
                for (int i = 0; i < dbChange.length; i++) {
                    DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
                    getClientConnection().write(dbChange[i]);
                }
            } catch (TransactionException e) {
                CTILogger.error(e.getMessage(), e);
            } catch (NullPointerException e) {
                CTILogger.error(e.getMessage(), e);
            }
            
        }
    }
    
    public void setDeviceType(String string) {
        deviceType = string;
        getCategoryList().setSelectedValue(getDeviceType(), true);
    }

    public void setDialogTitle(String string) {
        dialogTitle = string;
    }
    
    public void setCommands(List<? extends LiteBase> commands) {
        
        /** Remove all existing rows before adding any rows on */
        ((DeviceTypeCommandsTableModel)getDandDCommandTable().getModel()).removeAllRows();
        
        /** Setting this variable also removes all previous entries in the tableModel
         * and re-adds them back in to catch any changes in the vector */
        for (LiteBase lite : commands) {
            DeviceTypeCommand dtc = null;
            if (lite instanceof LiteDeviceTypeCommand) {
                dtc = (DeviceTypeCommand)LiteFactory.createDBPersistent(lite);
            } else if (lite instanceof LiteCommand) {
                //We have to create a "fake" DeviceTypeCommand.  This will NOT be entered into the table,
                // but rather used as a template for all deviceTypes that fit into the CATEGORY!
                dtc = new DeviceTypeCommand();
                dtc.setCommand((Command)LiteFactory.createDBPersistent(lite));
            }
            //TODO - change to set CommandGroupID correctly based on either lite object or current user.
            dtc.setCommandGroupID(new Integer(com.cannontech.database.db.command.DeviceTypeCommand.DEFAULT_COMMANDS_GROUP_ID));
            ((DeviceTypeCommandsTableModel)getDandDCommandTable().getModel()).addRowToEnd(dtc);
        }
    }
    
    public void showCommandSetup(JFrame parent) {
        
        dialog = new JDialog(parent);
        dialog.setTitle(getDialogTitle());
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setModal(true);
        dialog.setContentPane(getDeviceTypeCommandSetupPanel());
        dialog.setModal(true);
        dialog.getContentPane().add(this);
        dialog.setSize(841, 328);

        // Add a keyListener to the Escape key.
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
        dialog.getRootPane().getInputMap().put(ks, "CloseAction");
        dialog.getRootPane().getActionMap().put("CloseAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                exit();
            }
        });

        // Add a window closeing event, even though I think it's already handled
        // by setDefaultCloseOperation(..)
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            };
        });
        
        dialog.setVisible(true);
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        
        if (e.getValueIsAdjusting()) return; // not yet!
        
        if (e.getSource() == getCategoryList()) {
            
            setDeviceType(getCategoryList().getSelectedValue());
            List<? extends LiteBase> objects = null;
            if (CommandCategoryUtil.isCommandCategory(getDeviceType())) {
                objects = commandDao.getAllCommandsByCategory(getDeviceType());
            } else {
                objects = commandDao.getAllDevTypeCommands(deviceType);
            }
            setCommands(objects);
            
        } else if (e.getSource() == getDandDCommandTable().getSelectionModel()) {
            
            int selectedRow = getDandDCommandTable().getSelectedRow();
            if (selectedRow > -1) {
                
                DeviceTypeCommand dtc = ((DeviceTypeCommandsTableModel) getDandDCommandTable().getModel()).getRow(selectedRow);
                
                if (dtc.getCommandID().intValue() <= 0) {
                    getRemoveCommandButton().setEnabled(false);
                    getEditCommandButton().setEnabled(false);
                } else {
                    getRemoveCommandButton().setEnabled(true);
                    getEditCommandButton().setEnabled(true);
                }
            }
        } 
    }
    
}