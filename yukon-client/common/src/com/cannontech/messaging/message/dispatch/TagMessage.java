package com.cannontech.messaging.message.dispatch;

import java.util.Date;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.tags.Tag;

/**
 * Message that makes adding, removing, and updating tags for a point possible. Not to be confused with tags in the
 * pointdata/signal messages.
 * @author aaron
 */
public class TagMessage extends BaseMessage {

    // Possible values for the action field
    public static final int ADD_TAG_ACTION = 0;
    public static final int REMOVE_TAG_ACTION = 1;
    public static final int UPDATE_TAG_ACTION = 2;
    public static final int REPORT_TAG_ACTION = 3;

    private Tag _tag = new Tag();
    private int _action;
    private int _clientMessageId;

    /**
     * @return one of ADD_TAG_ACTION, REMOVE_TAG_ACTION, or UPDATE_TAG_ACTION
     */
    public int getAction() {
        return _action;
    }

    /**
     * @return Tag description
     */
    public String getDescriptionStr() {
        return _tag.getDescriptionStr();
    }

    /**
     * Every tag as a unique one of these.
     * @return instance id
     */
    public int getInstanceId() {
        return _tag.getInstanceId();
    }

    /**
     * @return id of the point this tag is for
     */
    public int getPointId() {
        return _tag.getPointId();
    }

    public String getReferenceStr() {
        return _tag.getReferenceStr();
    }

    public String getTaggedForStr() {
        return _tag.getTaggedForStr();
    }

    public int getTagId() {
        return _tag.getTagId();
    }

    public Date getTagTime() {
        return _tag.getTagTime();
    }

    /**
     * One of ADD_TAG_ACTION, REMOVE_TAG_ACTION, or UPDATE_TAG_ACTION
     * @param i
     */
    public void setAction(int action) {
        if (!(action == ADD_TAG_ACTION || action == REMOVE_TAG_ACTION || action == UPDATE_TAG_ACTION || action == REPORT_TAG_ACTION)) {
            throw new IllegalArgumentException(
                                               "Action must be one of ADD_TAG_ACTION, REMOVE_TAG_ACTION, UPDATE_TAG_ACTION, or REPORT_TAG_ACTION");
        }

        this._action = action;
    }

    /**
     * Tag description
     * @param string
     */
    public void setDescriptionStr(String description) {
        _tag.setDescriptionStr(description);
    }

    /**
     * Every tag as a unique one of these. Only dispatch (and restoreGuts) should set this.
     * @param i
     */
    public void setInstanceId(int instanceId) {
        _tag.setInstanceId(instanceId);
    }

    /**
     * The id of the point this tag is for
     * @param i
     */
    public void setPointId(int pointId) {
        _tag.setPointId(pointId);
    }

    /**
     * Job number, etc. We don't do anything with this field but pass it around and display it.
     * @param string
     */
    public void setReferenceStr(String refId) {
        _tag.setReferenceStr(refId);
    }

    /**
     * Whom this tag is for? We don't do anything with this field but pass it around and display it.
     * @param string
     */
    public void setTaggedForStr(String taggedFor) {
        _tag.setTaggedForStr(taggedFor);
    }

    /**
     * Must refer to a tag from the tag table.
     * @param i
     */
    public void setTagId(int tagId) {
        _tag.setTagId(tagId);
    }

    /**
     * When did this tag happen. Only dispatch (and restoreGuts) should set this.
     * @param date
     */
    public void setTagTime(Date tagTime) {
        _tag.setTagTime(tagTime);
    }

    public Tag getTag() {
        return _tag;
    }

    public void setTag(Tag tag) {
        _tag = tag;
    }

    public int getClientMessageId() {
        return _clientMessageId;
    }

    public void setClientMessageId(int i) {
        _clientMessageId = i;
    }

    @Override
    public void setUserName(String newUserName) {
        super.setUserName(newUserName);
        _tag.setUsername(newUserName);
    }
}
