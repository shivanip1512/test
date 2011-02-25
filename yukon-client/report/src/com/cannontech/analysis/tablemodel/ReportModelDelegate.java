package com.cannontech.analysis.tablemodel;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;

/**
 * This is a helper class for using the SimpleYukonReportBase.
 * It should not need to be used directly.
 * @author tmack
 *
 */
public final class ReportModelDelegate extends ReportModelBase {
    private static final long serialVersionUID = -27241128506844073L;
    private final ReportModelBase reportModelBase;
    private final ReportModelLayout reportModelLayout;
    private ColumnProperties[] cachedColumnProperties;

    public ReportModelDelegate(ReportModelBase reportModelBase, ReportModelLayout reportModelLayout) {
        this.reportModelBase = reportModelBase;
        this.reportModelLayout = reportModelLayout;
    }
    
    public ColumnProperties[] getColumnProperties() {
        if (cachedColumnProperties == null) {
            for (int i = 0; i < getColumnCount(); i++) {
                cachedColumnProperties[i] = getColumnProperties(i);
            }
        }
        return cachedColumnProperties;
    }

    public ColumnProperties getColumnProperties(int column) {
        return reportModelLayout.getColumnProperties(column);
    }

    
    // everything below here is just a simple delegate

    public void addTableModelListener(TableModelListener l) {
        reportModelBase.addTableModelListener(l);
    }

    public void buildByteStream(OutputStream out) throws IOException {
        reportModelBase.buildByteStream(out);
    }

    public void collectData() {
        reportModelBase.collectData();
    }

    public boolean equals(Object obj) {
        return reportModelBase.equals(obj);
    }

    public int findColumn(String columnName) {
        return reportModelBase.findColumn(columnName);
    }

    public void fireTableCellUpdated(int row, int column) {
        reportModelBase.fireTableCellUpdated(row, column);
    }

    public void fireTableChanged(TableModelEvent e) {
        reportModelBase.fireTableChanged(e);
    }

    public void fireTableDataChanged() {
        reportModelBase.fireTableDataChanged();
    }

    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        reportModelBase.fireTableRowsDeleted(firstRow, lastRow);
    }

    public void fireTableRowsInserted(int firstRow, int lastRow) {
        reportModelBase.fireTableRowsInserted(firstRow, lastRow);
    }

    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        reportModelBase.fireTableRowsUpdated(firstRow, lastRow);
    }

    public void fireTableStructureChanged() {
        reportModelBase.fireTableStructureChanged();
    }

    public Object getAttribute(int columnIndex, Object o) {
        return reportModelBase.getAttribute(columnIndex, o);
    }

    public String[] getBillingGroups() {
        return reportModelBase.getBillingGroups();
    }

    public Class<?> getColumnClass(int column) {
        return reportModelBase.getColumnClass(column);
    }

    public int getColumnCount() {
        return reportModelBase.getColumnCount();
    }

    public String getColumnName(int column) {
        return reportModelBase.getColumnName(column);
    }

    public String[] getColumnNames() {
        return reportModelBase.getColumnNames();
    }

    public Class[] getColumnTypes() {
        return reportModelBase.getColumnTypes();
    }

    public Vector getData() {
        return reportModelBase.getData();
    }

    public SimpleDateFormat getDateFormat() {
        return reportModelBase.getDateFormat();
    }

    public String getDateRangeString() {
        return reportModelBase.getDateRangeString();
    }

    public Integer getEnergyCompanyID() {
        return reportModelBase.getEnergyCompanyID();
    }

    public String getFieldSeparator() {
        return reportModelBase.getFieldSeparator();
    }

    public ReportFilter getFilterModelType() {
        return reportModelBase.getFilterModelType();
    }

    public ReportFilter[] getFilterModelTypes() {
        return reportModelBase.getFilterModelTypes();
    }

    public String getHTMLOptionsTable() {
        return reportModelBase.getHTMLOptionsTable();
    }

    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return reportModelBase.getListeners(listenerType);
    }

    public int[] getPaoIDs() {
        return reportModelBase.getPaoIDs();
    }

    public int getRowCount() {
        return reportModelBase.getRowCount();
    }

    public int getSortOrder() {
        return reportModelBase.getSortOrder();
    }

    public String getSortOrderString(int sortOrder) {
        return reportModelBase.getSortOrderString(sortOrder);
    }

    public Date getStartDate() {
        return reportModelBase.getStartDate();
    }

    public Date getStopDate() {
        return reportModelBase.getStopDate();
    }

    public TableModelListener[] getTableModelListeners() {
        return reportModelBase.getTableModelListeners();
    }

    public TimeZone getTimeZone() {
        return reportModelBase.getTimeZone();
    }

    public String getTitleString() {
        return reportModelBase.getTitleString();
    }

    public Integer getUserID() {
        return reportModelBase.getUserID();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return reportModelBase.getValueAt(rowIndex, columnIndex);
    }

    public int hashCode() {
        return reportModelBase.hashCode();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return reportModelBase.isCellEditable(rowIndex, columnIndex);
    }

    public void removeTableModelListener(TableModelListener l) {
        reportModelBase.removeTableModelListener(l);
    }

    public void setBillingGroups(String[] strings) {
        reportModelBase.setBillingGroups(strings);
    }

    public void setColumnProperties(ColumnProperties[] properties_) {
        reportModelBase.setColumnProperties(properties_);
    }

    public void setData(Vector vector) {
        reportModelBase.setData(vector);
    }

    public void setDateFormat(SimpleDateFormat format) {
        reportModelBase.setDateFormat(format);
    }

    public void setEnergyCompanyID(Integer ecID) {
        reportModelBase.setEnergyCompanyID(ecID);
    }

    public void setFieldSeparator(String string) {
        reportModelBase.setFieldSeparator(string);
    }

    public void setFilterModelType(ReportFilter modelType) {
        reportModelBase.setFilterModelType(modelType);
    }

    public void setFilterModelTypes(ReportFilter[] models) {
        reportModelBase.setFilterModelTypes(models);
    }

    public void setPaoIDs(int[] is) {
        reportModelBase.setPaoIDs(is);
    }

    public void setParameters(HttpServletRequest req) {
        reportModelBase.setParameters(req);
    }

    public void setSortOrder(int i) {
        reportModelBase.setSortOrder(i);
    }

    public void setStartDate(Date startDate_) {
        reportModelBase.setStartDate(startDate_);
    }

    public void setStopDate(Date stopDate_) {
        reportModelBase.setStopDate(stopDate_);
    }

    public void setTimeZone(TimeZone tz) {
        reportModelBase.setTimeZone(tz);
    }

    public void setUserID(Integer userID) {
        reportModelBase.setUserID(userID);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        reportModelBase.setValueAt(aValue, rowIndex, columnIndex);
    }

    public String toString() {
        return reportModelBase.toString();
    }

    public boolean useStartDate() {
        return reportModelBase.useStartDate();
    }

    public boolean useStopDate() {
        return reportModelBase.useStopDate();
    }


}
