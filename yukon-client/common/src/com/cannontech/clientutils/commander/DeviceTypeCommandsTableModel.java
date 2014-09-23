package com.cannontech.clientutils.commander;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.cannontech.common.gui.dnd.IDroppableTableModel;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.database.data.command.DeviceTypeCommand;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.spring.YukonSpringHook;

public class DeviceTypeCommandsTableModel extends AbstractTableModel implements IDroppableTableModel {
    
    private CommandDao commandDao = YukonSpringHook.getBean(CommandDao.class);
    
    public final static int LABEL_COLUMN = 0;
    public final static int COMMAND_COLUMN = 1;
    public final static int VISIBILTY_COLUMN = 2;
    public final static int CATEGORY_COLUMN = 3; // This is the command category, not deviceType
    
    public static String[] columnNames = { "Label", "Command", "Visible", "Category" };
    
    public static Class<?>[] columnTypes = { String.class, String.class, Boolean.class, String.class };
    
    public List<DeviceTypeCommand> rows = new ArrayList<>();
    
    public void addRow(DeviceTypeCommand devTypeCommand) {
        rows.add(devTypeCommand);
        fireTableDataChanged();
    }
    
    public void addRowToEnd(DeviceTypeCommand devTypeCommand) {
        rows.add(devTypeCommand);
        fireTableDataChanged();
    }
    
    public Class<?> getColumnClass(int column) {
        return getColumnTypes()[column];
    }
    
    public int getColumnCount() {
        return getColumnNames().length;
    }
    
    public String getColumnName(int col) {
        return getColumnNames()[col];
    }
    
    public String[] getColumnNames() {
        return columnNames;
    }
    
    public Class<?>[] getColumnTypes() {
        return columnTypes;
    }
    
    public Object getAttribute(int index, DeviceTypeCommand command) {
        
        LiteCommand lc = (LiteCommand) commandDao.getCommand(command.getCommandID().intValue());
        
        switch (index) {
            case LABEL_COLUMN: return lc.getLabel();
            case COMMAND_COLUMN: return lc.getCommand();
            case VISIBILTY_COLUMN: return new Boolean(command.isVisible());
            case CATEGORY_COLUMN: return lc.getCategory();
        }
        
        return null;
    }
    
    public DeviceTypeCommand getRow(int row) {
        return (row < rows.size() ? (DeviceTypeCommand) rows.get(row) : null);
    }
    
    public int getRowCount() {
        return rows.size();
    }
    
    public List<DeviceTypeCommand> getRows() {
        return rows;
    }
    
    public Object getValueAt(int row, int col) {
        if (row < rows.size() && col < getColumnCount()) {
            return getAttribute(col, (DeviceTypeCommand) rows.get(row));
        } else {
            return null;
        }
    }
    
    public boolean isCellEditable(int row, int column) {
        return (column == VISIBILTY_COLUMN);
    }

    public void removeRow(int[] row) {
        
        Object[] toRemove = new Object[row.length];
        
        for (int i = 0; i < row.length; i++)
            toRemove[i] = rows.get(row[i]);
        
        for (int i = 0; i < toRemove.length; i++) 
            rows.remove(toRemove[i]);
        
        fireTableDataChanged();
    }
    
    public void removeAllRows() {
        rows.clear();
        fireTableDataChanged();
    }
    
    public void removeRow(int row) {
        
        if (row < rows.size()) {
            rows.remove(row);
        }
        fireTableDataChanged();
    }
    
    public void setValueAt(Object value, int row, int col) {
        
        DeviceTypeCommand ldtc = getRow(row);
        
        switch (col) {
        case LABEL_COLUMN:
            ldtc.getCommand().setLabel(value.toString());
            break;
            
        case COMMAND_COLUMN:
            ldtc.getCommand().setCommand(value.toString());
            break;
            
        case VISIBILTY_COLUMN: 
            Boolean flag = (Boolean) value;
            if (flag.booleanValue()) {
                ldtc.getDeviceTypeCommand().setVisibleFlag(new Character('Y'));
            } else {
                ldtc.getDeviceTypeCommand().setVisibleFlag(new Character('N'));
            }
            break;
            
        case CATEGORY_COLUMN:
            ldtc.getCommand().setCategory(value.toString());
            break;
            
        }
        
        fireTableRowsUpdated(row, row);
    }
    
    public void insertNewRow(Object devTypeCmd) {
        rows.add((DeviceTypeCommand) devTypeCmd);
        fireTableDataChanged(); // Tell the listeners a new table has arrived.
    }
    
    public boolean objectExists(Object devTypeCmd) {
        if (devTypeCmd instanceof DeviceTypeCommand) {
            for (int i = 0; i < getRowCount(); i++) {
                Integer commandID = ((DeviceTypeCommand) getRowAt(i)).getCommandID();
                if (commandID == ((DeviceTypeCommand) devTypeCmd).getCommandID())
                    return true;
            }
        }
        return false;
    }

    public Object getRowAt(int row) {
        return rows.get(row);
    }

    public void insertRowAt(Object value, int index) {
        if (value instanceof DeviceTypeCommand) {
            rows.add(index, (DeviceTypeCommand) value);
            fireTableDataChanged();
        }

    }
}