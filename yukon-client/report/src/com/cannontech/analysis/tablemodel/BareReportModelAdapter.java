package com.cannontech.analysis.tablemodel;

import java.util.Date;

import com.cannontech.analysis.ColumnProperties;

public final class BareReportModelAdapter extends ReportModelBase {

    private final BareReportModel model;
    private final ReportModelLayout layout;

    public BareReportModelAdapter(BareReportModel model, ReportModelLayout layout) {
        this.model = model;
        this.layout = layout;
    }
    
    @Override
    public void collectData() {
        if (model instanceof LoadableModel) {
            LoadableModel loadableBase = (LoadableModel) model;
            loadableBase.loadData();
        }
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return model.getValueAt(rowIndex, columnIndex);
    }
    
    @Override
    public int getRowCount() {
        return model.getRowCount();
    }

    public Object getAttribute(int columnIndex, Object o) {
        // I'm not sure where this is ever called.
        throw new UnsupportedOperationException();
    }

    public String[] getColumnNames() {
        String[] columnNames = new String[getColumnCount()];
        for (int i = 0; i < getColumnCount(); ++i) {
            columnNames[i] = model.getColumnName(i);
        }
        return columnNames;
    }
    
    @Override
    public String getColumnName(int column) {
        return model.getColumnName(column);
    }
    
    @Override
    public int getColumnCount() {
        return model.getColumnCount();
    }

    public Class<?>[] getColumnTypes() {
        Class<?>[] columnClasses = new Class<?>[getColumnCount()];
        for (int i = 0; i < getColumnCount(); ++i) {
            columnClasses[i] = model.getColumnClass(i);
        }
        return columnClasses;
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        return model.getColumnClass(column);
    }

    public ColumnProperties[] getColumnProperties() {
        ColumnProperties[] columnLayouts = new ColumnProperties[getColumnCount()];
        for (int i = 0; i < getColumnCount(); ++i) {
            columnLayouts[i] = layout.getColumnProperties(i);
        }
        return columnLayouts;
    }

    public String getTitleString() {
        return model.getTitle();
    }
    
    @Override
    public Date getStartDate() {
        throw new UnsupportedOperationException("getStartDate() is not supported, please override getDateRangeString() in your report");
    }
    
    @Override
    public Date getStopDate() {
        throw new UnsupportedOperationException("getStopDate() is not supported, please override getDateRangeString() in your report");
    }
    
    @Override
    public String getDateRangeString() {
        throw new UnsupportedOperationException("getDateRangeString() is not supported, please override getDateRangeString() in your report");
    }
    
    @Override
    public boolean useStartDate() {
        return model instanceof DatedModelAttributes;
    }
    
    @Override
    public boolean useStopDate() {
        return model instanceof DatedModelAttributes;
    }

}
