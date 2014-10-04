package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteTag;

public interface TagDao {

    LiteTag getLiteTag(int tagId);
    
    LiteTag getLiteTag(String tagName);
    
    List<LiteTag> getAllTags();

}