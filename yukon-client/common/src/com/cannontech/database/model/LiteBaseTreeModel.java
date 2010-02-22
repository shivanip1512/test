package com.cannontech.database.model;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.cannontech.database.data.lite.LiteBase;

public interface LiteBaseTreeModel extends TreeModel
{
    boolean insertTreeObject(LiteBase lb);
    
    boolean isLiteTypeSupported(int liteType);
    
    /**
     * Returns true if this is the primary tree used for displaying
     * the LiteBase. By default, this method may delegate to the 
     * isLiteTypeSupported method, but should be overridden as appropriate
     * (e.g. for PAOs).
     * @param lb the LiteBase to test
     * @return
     */
    boolean isTreePrimaryForObject(LiteBase lb);
    
    public void updateIfNothingHasBeenLoaded();
    
    public boolean isUpdateNeeded();
    
    boolean removeTreeObject(LiteBase lb);
    
    void treePathWillExpand(TreePath path);

    public void update(final Runnable onCompletion);
    
    public void update();

    boolean updateTreeObject(LiteBase lb);
}
