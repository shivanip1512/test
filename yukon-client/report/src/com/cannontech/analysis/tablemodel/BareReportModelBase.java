package com.cannontech.analysis.tablemodel;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

/**
 * A starter ABC for using the BareReportModel. This class introduces some
 * row and column conventions that make it easier to use. Instead of dealing
 * with column indexes in the getValueAt() method, extenders need only return
 * a model object. The class of this model object must be declared by implementing
 * the getRowClass() method. To specify the columns, a list of ColumnData objects
 * is returned by implementing the getColumnData() method. Currently, only the 
 * ColumnDataField class is useful.
 */
public abstract class BareReportModelBase<T> implements BareReportModel, LoadableModel {
    protected final static String baseKey = "yukon.common.reports.";
    private List<Field> columnData = null;
    private Logger log = YukonLogManager.getLogger(this.getClass());
    private Date loadDate = null;
    
    public BareReportModelBase() {
        super();
    }
        
    /**
     * @param rowIndex
     * @return an object that is assignable to the class returned by getRowClass()
     */
    abstract protected T getRow(int rowIndex);
    abstract protected Class<T> getRowClass();
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        T row = getRow(rowIndex);
        Field field = getColumnData().get(columnIndex);
        Object object;
        try {
            object = field.get(row);
        } catch (IllegalArgumentException e) {
            log.warn("Couldn't get value for (" + rowIndex + "," + columnIndex + ")", e);
            return null;
        } catch (IllegalAccessException e) {
            log.warn("Couldn't get value for (" + rowIndex + "," + columnIndex + ")", e);
            return null;
        }
        return object;
    }

    public int getColumnCount() {
        return getColumnData().size();
    }

    public String getColumnName(int columnIndex) {
        Field field = getColumnData().get(columnIndex);
        return field.getName();
    }

    public Class<?> getColumnClass(int columnIndex) {
        Field field = getColumnData().get(columnIndex);
        if (field.getType().isPrimitive()) {
            throw new IllegalArgumentException("Using primitive types in the model is not supported: " + field.getDeclaringClass());
        }
        return field.getType();
    }
    
    private List<Field> getColumnData() {
        if (columnData == null) {
            Class<T> rowClass = getRowClass();
            Field[] declaredFields = rowClass.getDeclaredFields();

            columnData = Arrays.asList(declaredFields);
        }
        return columnData;
    }
    
    public final void loadData() {
        doLoadData();
        loadDate = new Date();
    }

    protected void doLoadData() {
    }

    public Date getLoadDate() {
        return loadDate;
    }
    
}
