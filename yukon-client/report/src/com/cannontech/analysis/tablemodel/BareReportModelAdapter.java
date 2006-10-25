package com.cannontech.analysis.tablemodel;

import com.cannontech.analysis.ColumnProperties;

public class BareReportModelAdapter extends ReportModelBase {

    private final BareReportModel model;
    private final ReportModelLayout layout;

    public BareReportModelAdapter(BareReportModel model, ReportModelLayout layout) {
        this.model = model;
        this.layout = layout;
    }

    public void collectData() {
        if (model instanceof CommonModelAttributes) {
            CommonModelAttributes commonModel = (CommonModelAttributes) model;
            Integer energyCompanyId2 = getEnergyCompanyID();
            if (energyCompanyId2 != null) {
                commonModel.setEnergyCompanyId(energyCompanyId2);
            }
            commonModel.setStartDate(getStartDate());
            commonModel.setStopDate(getStopDate());
        }
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

}
