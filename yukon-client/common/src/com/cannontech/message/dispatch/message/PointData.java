package com.cannontech.message.dispatch.message;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.point.PointType;


public class PointData extends com.cannontech.message.util.Message implements PointValueQualityHolder
{
    private static final long serialVersionUID = -2987200556094493247L;

    private static final String TRACKING_DELIMITER = " trkid ";

    private int id;
    private int type;
    private PointQuality pointQuality;
    private long tags;
    private double value;
    private String str = "";
    private Date time = new Date();
    private transient String trackingId;  //  just for memoization

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
    
    @Override 
    public String getTrackingId() {
        //  extract on demand, cache/memoize into trackingId
        if (trackingId == null) {
            trackingId = StringUtils.substringAfter(getSource(), TRACKING_DELIMITER);
        }
        return trackingId;
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
     * @deprecated
     * @param newTags all point tags as a bitfield
     */
    @Deprecated(since="2013-09-18")
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

    /**
     * Appends the tracking ID to the Source message field.
     * @param id the tracking ID to append
     */
    public void setTrackingId(String id) {
        trackingId = id;  //  memoize into trackingId
        var bareSource = StringUtils.substringBefore(getSource(), TRACKING_DELIMITER);
        setSource(bareSource + TRACKING_DELIMITER + id); 
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + (int) (millis ^ (millis >>> 32));
        result = prime * result + ((pointQuality == null) ? 0 : pointQuality.hashCode());
        result = prime * result + ((str == null) ? 0 : str.hashCode());
        result = prime * result + (int) (tags ^ (tags >>> 32));
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + type;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PointData other = (PointData) obj;
        if (id != other.id) {
            return false;
        }
        if (millis != other.millis) {
            return false;
        }
        if (pointQuality != other.pointQuality) {
            return false;
        }
        if (str == null) {
            if (other.str != null) {
                return false;
            }
        } else if (!str.equals(other.str)) {
            return false;
        }
        if (tags != other.tags) {
            return false;
        }
        if (time == null) {
            if (other.time != null) {
                return false;
            }
        } else if (!time.equals(other.time)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        return true;
    }
}
