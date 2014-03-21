package com.cannontech.common.tdc.dao;

import java.util.List;

import org.joda.time.DateTimeZone;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;

public interface DisplayDataDao {

    /**
     * Gets Event Viewer display data
     */
    List<DisplayData> getEventViewerDisplayData(DateTimeZone timeZone, PagingParameters paging);

    /**
     * Gets Custom display data
     */
    List<DisplayData> getCustomDisplayData(Display display);

    /**
     * Gets SOE log display data
     */
    List<DisplayData> getSoeLogDisplayData(DateTimeZone timeZone, PagingParameters paging);

    /**
     * Gets TAG log display data
     */
    List<DisplayData> getTagLogDisplayData(DateTimeZone timeZone, PagingParameters paging);

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

}
