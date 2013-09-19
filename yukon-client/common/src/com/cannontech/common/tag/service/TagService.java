package com.cannontech.common.tag.service;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tags.Tag;

public interface TagService {

    /**
     * Adds a tag
     */
    public void createTag(int pointId, int tagId, String description, LiteYukonUser user)
            throws Exception;

    /**
     * Removes a tag
     */
    public void removeTag(int pointId, int instanceId, LiteYukonUser user) throws Exception;

    /**
     * Updates a tag
     */

    public void updateTag(Tag tag, LiteYukonUser user) throws Exception;

    /**
     * Returns list of tags for a point id
     */
    public List<Tag> getTags(int pointId);
}
