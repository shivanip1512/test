package com.cannontech.common.tdc.dao;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.tdc.model.ColumnType;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;

public interface DisplayDataDao {
    
    public enum SortBy{

        DEVICE_NAME("DeviceName", ColumnType.DEVICE_NAME),
        POINT_NAME("PointName", ColumnType.POINT_NAME),
        ADDITIONAL_INFO("AdditionalInfo", ColumnType.ADDITIONAL_INFO),
        TIME_STAMP("Timestamp", ColumnType.TIME_STAMP),
        DESCRIPTION("Description", ColumnType.DESCRIPTION),
        TEXT_MESSAGE("Description", ColumnType.TEXT_MESSAGE),
        USERNAME("Username", ColumnType.USERNAME),
        TAG("Tagname", ColumnType.TAG);
                      
        private SortBy(String dbString, ColumnType column) {
            this.dbString = dbString;
            this.column = column;
        }
        
        private final String dbString;
        private final ColumnType column;

        public String getDbString() {
            return dbString;
        }
        
        public ColumnType getColumn() {
            return column;
        }

        public static SortBy getSortBy(ColumnType column) {
            return Arrays.stream(SortBy.values()).filter(x -> x.getColumn() == column).findFirst().get();
        }
    }
    
    /**
     * Gets Custom display data
     */
    List<DisplayData> getCustomDisplayData(Display display);

    /**

    /**
     * Deletes Display2Waydata entries for display id, inserts the entries for points.
     */
    void updateDisplay2Waydata(Integer displayId, List<Integer> pointIds);

    /**
     * Gets the TAG Log Display Data (Allows for paging, sorting and a date selection)
     * @param sortBy nullable.
     * @param direction nullable.
     * @param date nullable.  If null, the current date will be used
     */
    SearchResults<DisplayData> getTagLogDisplayData(DateTimeZone timeZone, PagingParameters paging,
                                                 SortBy sortBy, Direction direction, DateTime date);
    
    /**
     * Gets the SOE Log Display Data (Allows for paging, sorting and a date selection)
     * @param sortBy nullable.
     * @param direction nullable.
     * @param date nullable.  If null, the current date will be used
     */
    SearchResults<DisplayData> getSoeLogDisplayData(DateTimeZone timeZone, PagingParameters paging,
                                                    SortBy sortBy, Direction direction, DateTime date);

    /**
     * Gets the Event Viewer Display Data (Allows for paging, sorting and a date selection)
     * @param sortBy nullable.
     * @param direction nullable.
     * @param date nullable.  If null, the current date will be used
     */
    SearchResults<DisplayData> getEventViewerDisplayData(DateTimeZone timeZone,
                                                         PagingParameters paging, SortBy sortBy,
                                                         Direction direction, DateTime date);
    /**
     * Gets the Alarm History Display Data (Allows for paging, sorting and a date selection)
     * @param sortBy nullable.
     * @param direction nullable.
     * @param date nullable.  If null, the current date will be used
     */
    SearchResults<DisplayData> getAlarmHistoryDisplayData(DateTimeZone timeZone,
                                                          PagingParameters paging, SortBy sortBy,
                                                          Direction direction, DateTime date);

}
