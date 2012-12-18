package com.cannontech.common.validation.dao;

import java.util.Set;

import com.cannontech.common.validation.model.RphTag;

public interface RphTagDao {

    boolean insertTag(long changeId, RphTag value);
    public int clearTagsAfter(long lastChangeIdProcessed, Set<RphTag> tags);
}
