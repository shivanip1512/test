package com.cannontech.analysis.tablemodel;

/**
 * This is the most basic interface needed to use the BareReportModelAdapter.
 * 
 * If you want to change this, you have to give a two-hour presentation to the group
 * that explains why!
 * 
 * @see com.cannontech.analysis.tablemodel.BareReportModelBase
 */
public interface BareReportModel {
    public Object getValueAt(int rowIndex, int columnIndex);
    public int getRowCount();
    public int getColumnCount();
    public String getColumnName(int columnIndex);
    /**
     * @param columnIndex
     * @return a non-primitive type
     */
    public Class<?> getColumnClass(int columnIndex);
    public String getTitle();
}
