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
    private double value;
    private java.lang.String str = "";
    private java.util.Date time = new java.util.Date();

    private long millis;

    //Tags from yukon-server common/include/pointdefs.h
    private static final long TAG_POINT_DATA_TIMESTAMP_VALID = 0x00000100;  // This point data message's timestamp comes from the device! (SOE data)
  //private static final long TAG_POINT_DATA_UNSOLICITED     = 0x00000200;  // This point data message was an unsolicited report from a device
  //private static final long TAG_POINT_MOA_REPORT           = 0x00000400;  // This point data message is the result of a registration
  //private static final long TAG_POINT_DELAYED_UPDATE       = 0x00000800;  // Dispatch delay this point data until the time specified in the message!

  //private static final long TAG_POINT_FORCE_UPDATE         = 0x00001000;  // Dispatch will no matter what copy this into his RT memory
    private static final long TAG_POINT_MUST_ARCHIVE         = 0x00002000;  // This data will archive no matter how the point is set up
  //private static final long TAG_POINT_UNUSED               = 0x00004000;  // Unused, formerly EXEMPTED
    private static final long TAG_POINT_LOAD_PROFILE_DATA    = 0x00008000;  // This data will archive to raw point history

    private static final long TAG_POINT_OLD_TIMESTAMP        = 0x00100000;  // The timestamp on this point is older than the most recent timestamp.

    // Point Types
    /* DEFINED IN com.cannontech.database.data.point.PointTypes */

    /**
     * PointData constructor comment.
     */
    public PointData() {
        super();
    }

    public static PointData of(PointValueQualityHolder pvqh) {
        PointData pointData = new PointData();
        
        pointData.setId(pvqh.getId());
        pointData.setType(pvqh.getType());
        pointData.setPointQuality(pvqh.getPointQuality());
        pointData.setValue(pvqh.getValue());
        pointData.setTime(pvqh.getPointDataTimeStamp());
        
        return pointData;
    }

    @Override
    public int getId() {
        return id;
    }

    /**
     * Time that this point read/gathered. This is NOT the time this message was created!
     * @return java.util.Date
     */
    @Override
    public java.util.Date getPointDataTimeStamp() {
        return time;
    }

    @Override
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

    @Override
    public int getType() {
        return type;
    }

    @Override
    public PointType getPointType() {
        return PointType.getForId(type);
    }

    @Override
    public double getValue() {
        return value;
    }

    public void setId(int newId) {
        id = newId;
    }

    public void setPointQuality(PointQuality pointQuality) {
        this.pointQuality = pointQuality;
    }

    public void setStr(java.lang.String newStr) {
        str = newStr;
    }

    /***
     * This should not generally be used.  It is only to be used for setting ALL tags from a raw 
     * bitfield value, such as when they are received in a message.<br>
     * If you want to set or clear individual tags, such as Load Profile Data or Must Archive, you 
     * should use the setTags...() family of methods instead.
     * @see setTagsLoadProfileData(), setTagsMustArchive()
     * @param newTags all point tags as a bitfield
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

    @Override
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
    
    public void setTagsDataTimestampValid(boolean b) {
        if (b) {
            tags |= TAG_POINT_DATA_TIMESTAMP_VALID;
        } else {
            tags &= ~TAG_POINT_DATA_TIMESTAMP_VALID;
        }
    }
}
