package com.cannontech.common.tdc.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;

public interface DisplayDataDao {
    
    public enum SortBy{

        PAO_CATEGORY("y.Category"),
        PAO_CLASS("y.PAOClass"),
        PAO_DESCRIPTION("y.Description"),
        PAO_DEVICE_NAME("y.DeviceName"),
        PAO_DISABLED_FLAG("y.DisableFlag"),
        PAO_ID("y.PAObjectID"),
        PAO_NAME("y.PAOName"),
        PAO_STATISTICS("y.PAOStatistics"),
        PAO_TYPE("y.Type"),
        
        POINT_ID("p.PointId"),
        POINT_NAME("p.Pointname"),
        POINT_PAO_ID("p.PAObjectId"),

        SYS_LOG_ACTION("s.Action"),
        SYS_LOG_DATE_TIME("s.Datetime"),
        SYS_LOG_DESCRIPTION("s.Description"),
        SYS_LOG_MILLLIS("s.Millis"),
        SYS_LOG_POINT_ID("s.PointId"),
        SYS_LOG_SOE_TAG("s.SOE_TAG"),
        SYS_LOG_USERNAME("s.USERNAME"),

        TAGS_TAG_NAME("t.Tagname"),
        
        TAG_LOG_TAG_TIME("l.Tagtime"),
        TAG_LOG_DESCRIPTION("l.Description"),
        TAG_LOG_ACTION("l.Action"),
        TAG_LOG_USERNAME("l.Username");       
        
        private SortBy(String dbString) {
            this.dbString = dbString;
        }
        
        private final String dbString;

        public String getDbString() {
            return dbString;
        }
    }
    
    /**
     * Gets Custom display data
     */
    List<DisplayData> getCustomDisplayData(Display display);

    /**
     * Gets number of SOE log Display Data count
     */
    int getSoeLogDisplayDataCount(DateTimeZone timeZone);

    /**
     * Gets number of TAG log Display Data count
     */
    int getTagLogDisplayDataCount(DateTimeZone timeZone);

    /**
     * Gets number of System log Display Data count
     */
    int getEventViewerDisplayDataCount(DateTimeZone timeZone);

    /**
     * Deletes Display2Waydata entries for display id, inserts the entries for points.
     */
    void updateDisplay2Waydata(Integer displayId, List<Integer> pointIds);

    /**
     * @param sortBy nullable.
     * @param direction nullable.
     */
    SearchResults<DisplayData> getTagLogDisplayData(DateTimeZone timeZone, PagingParameters paging,
                                                 SortBy sortBy, Direction direction);
    
    /**
     * @param sortBy nullable.
     * @param direction nullable.
     */
    SearchResults<DisplayData> getSoeLogDisplayData(DateTimeZone timeZone, PagingParameters paging,
                                                    SortBy sortBy, Direction direction);

    /**
     * @param sortBy nullable.
     * @param direction nullable.
     */
    SearchResults<DisplayData> getEventViewerDisplayData(DateTimeZone timeZone,
                                                         PagingParameters paging, SortBy sortBy,
                                                         Direction direction, DateTime date);

    int getEventViewerDisplayDataCount(DateTime date);

}
