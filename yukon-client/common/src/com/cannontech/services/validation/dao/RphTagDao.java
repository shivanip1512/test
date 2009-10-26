package com.cannontech.services.validation.dao;

import java.util.Set;

import com.cannontech.services.validation.model.RphTag;

public interface RphTagDao {

    boolean insertTag(int changeId, RphTag value);
    public int clearTagsAfter(long lastChangeIdProcessed, Set<RphTag> tags);
}
