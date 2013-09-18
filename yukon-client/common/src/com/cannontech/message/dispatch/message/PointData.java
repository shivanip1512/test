package com.cannontech.message.dispatch.message;

import org.joda.time.Instant;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.point.PointType;


public class PointData extends com.cannontech.message.util.Message implements PointValueQualityHolder
{
    private int id;
    private int type;
    private PointQuality pointQuality;
    private long tags;
    private long limit;
    private double value;
    private java.lang.String str = "";
    private java.util.Date time = new java.util.Date();
    private long forced;

    private long millis;

    //Tags from yukon-server common/include/pointdefs.h
    private static final long TAG_POINT_DO_NOT_REPORT = 0x00000200;        // Do not route this pointData onto others	
    private static final long TAG_POINT_MUST_ARCHIVE = 0x00002000;        // This data will archive no matter how the point is set up
    private static final long TAG_POINT_LOAD_PROFILE_DATA = 0x00008000;        // This data will archive to raw point history
    private static final long TAG_POINT_OLD_TIMESTAMP = 0x00100000;

    // Point Types
    /* DEFINED IN com.cannontech.database.data.point.PointTypes */

    /**
     * PointData constructor comment.
     */
    public PointData() {
        super();
    }

    public long getForced() {
        return forced;
    }

    public int getId() {
        return id;
    }

    public long getLimit() {
        return limit;
    }

    /**
     * Time that this point read/gathered. This is NOT the time this message was created!
     * @return java.util.Date
     */
    public java.util.Date getPointDataTimeStamp() {
        return time;
    }

    public PointQuality getPointQuality() {
        return pointQuality;
    }

    public java.lang.String getStr() {
        return str;
    }

    public long getTags() {
        return tags;
    }

    public boolean getTagsOldTimestamp() {
        return (tags & TAG_POINT_OLD_TIMESTAMP) != 0;
    }

    public int getType() {
        return type;
    }

    @Override
    public PointType getPointType() {
        return PointType.getForId(type);
    }

    public double getValue() {
        return value;
    }

    public void setForced(long newForced) {
        forced = newForced;
    }

    public void setId(int newId) {
        id = newId;
    }

    public void setLimit(long newLimit) {
        limit = newLimit;
    }

    public void setPointQuality(PointQuality pointQuality) {
        this.pointQuality = pointQuality;
    }

    public void setStr(java.lang.String newStr) {
        str = newStr;
    }

    /***
     * This should not generally be used.
     * You are probably looking for the setTags* family of methods, such as
     * setTagsLoadProfileData() and 
     * @param newTags
     */
    public void setTags(long newTags) {
        tags = newTags;
    }

    public void setTime(java.util.Date newTime) {
        time = newTime;
    }

    public void setType(int newType) {
        type = newType;
    }

    public void setValue(double newValue) {
        value = newValue;
    }

    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("id", getId());
        tsc.append("value", getValue());
        tsc.append("timestamp", new Instant(getPointDataTimeStamp()));
        tsc.append("type", getType());
        tsc.append("quality", getPointQuality().getQuality());
        return tsc.toString(); 
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long l) {
        millis = l;
    }

    public void setTagsPointMustArchive(boolean b) {
        if (b) {
            tags |= TAG_POINT_MUST_ARCHIVE;
        } else {
            tags &= ~TAG_POINT_MUST_ARCHIVE;
        }
    }

    public void setTagsLoadProfileData(boolean b) {
        if (b) {
            tags |= TAG_POINT_LOAD_PROFILE_DATA;
        } else {
            tags &= ~TAG_POINT_LOAD_PROFILE_DATA;
        }
    }

    public void setTagsDoNotReport(boolean b) {
        if (b) {
            tags |= TAG_POINT_DO_NOT_REPORT;
        } else {
            tags &= ~TAG_POINT_DO_NOT_REPORT;
        }
    }
}
