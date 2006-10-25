package com.cannontech.analysis.tablemodel;

import java.util.List;

/**
 * A starter ABC for using the BareReportModel. This class introduces some
 * row and column conventions that make it easier to use. Instead of dealing
 * with column indexes in the getValueAt() method, extenders need only return
 * a model object. The class of this model object must be declared by implementing
 * the getRowClass() method. To specify the columns, a list of ColumnData objects
 * is returned by implementing the getColumnData() method. Currently, only the 
 * ColumnDataField class is useful.
 */
public abstract class BareReportModelBase<T> implements BareReportModel {

    public BareReportModelBase() {
        super();
    }
    
    protected abstract List<ColumnData> getColumnData();

    /**
     * @param rowIndex
     * @return an object that is assignable to the class returned by getRowClass()
     */
    abstract protected T getRow(int rowIndex);
    abstract protected Class<T> getRowClass();
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        return getColumnData().get(columnIndex).getColumnValue(getRow(rowIndex));
    }

    public int getColumnCount() {
        return getColumnData().size();
    }

    public String getColumnName(int columnIndex) {
        return getColumnData().get(columnIndex).getColumnName();
    }

    public Class<?> getColumnClass(int columnIndex) {
        return getColumnData().get(columnIndex).getColumnType(getRowClass());
    }

}
