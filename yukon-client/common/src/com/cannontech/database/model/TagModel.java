/*
 * Created on Jan 16, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

import java.util.Collections;
import java.util.List;

import com.cannontech.core.dao.TagDao;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.spring.YukonSpringHook;

public class TagModel extends DBTreeModel {
    
    private TagDao tagDao = YukonSpringHook.getBean(TagDao.class);
    
    public TagModel() {
        super(new DBTreeNode("Tag"));
    }
    
    public boolean isLiteTypeSupported(int liteType) {
        return liteType == LiteTypes.TAG;
    }
    
    public void update() {
        
        List<LiteTag> tags = tagDao.getAllTags();
        Collections.sort(tags, LiteComparators.liteStringComparator);
        
        DBTreeNode rootNode = (DBTreeNode) getRoot();
        rootNode.removeAllChildren();
        
        for (LiteTag tag : tags) {
            DBTreeNode tagNode = new DBTreeNode(tag);
            
            if (tag.getTagId() < 0) tagNode.setIsSystemReserved(true);
            
            rootNode.add(tagNode);
        }
        
        reload();
    }
    
}