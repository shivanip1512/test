package com.cannontech.common.tag.service;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tags.Tag;

public interface TagService {

    /**
     * Method to add a tag
     * @param pointId
     * @param tagId
     * @param description
     * @param user
     * @throws Exception
     */
    public void createTag(int pointId, int tagId, String description, LiteYukonUser user)
            throws Exception;

    /**
     * Method to remove a tag
     * @param pointId
     * @param instanceId
     * @param user
     * @throws Exception
     */
    public void removeTag(int pointId, int instanceId, LiteYukonUser user) throws Exception;

    /**
     * Method to return update a tag
     * @param tag
     * @param user
     * @throws Exception
     */

    public void updateTag(Tag tag, LiteYukonUser user) throws Exception;

    /**
     * Method to return list of tags for a point id
     * @param pointId
     * @return
     */
    public List<Tag> getTags(int pointId);
}
