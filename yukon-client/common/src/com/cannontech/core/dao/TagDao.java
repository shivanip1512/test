package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteTag;

public interface TagDao {

    /**
     * Return the LiteTag for the given tagID
     * @param tagID
     * @return
     */
    public LiteTag getLiteTag(int tagID);
    public LiteTag getLiteTag(String tagName);

}