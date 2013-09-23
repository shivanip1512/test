package com.cannontech.common.tdc.dao;

import java.util.List;

import org.joda.time.DateTimeZone;

import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;

public interface DisplayDataDao {

    /**
     * Gets Event Viewer display data
     */
    public List<DisplayData> getEventViewerDisplayData(DateTimeZone timeZone);

    /**
     * Gets Custom display data
     */
    public List<DisplayData> getCustomDisplayData(Display display, List<DisplayData> alarms);

    /**
     * Gets SOE log display data
     */
    public List<DisplayData> getSoeLogDisplayData(DateTimeZone timeZone);

    /**
     * Gets TAG log display data
     */
    public List<DisplayData> getTagLogDisplayData(DateTimeZone timeZone);

}
