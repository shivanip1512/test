package com.cannontech.database.model;

import javax.swing.tree.TreeNode;

public abstract class DbBackgroundTreeModel extends DBTreeModel {

    public DbBackgroundTreeModel(TreeNode root) {
        super(root);
    }

    @Override
    final public void update() {
        doUpdate(new Runnable() {
            public void run() {
            }
        });
    }
    
    @Override
    final public void update(Runnable onCompletion) {
        doUpdate(onCompletion);
    }

    protected abstract void doUpdate(Runnable onCompletion);
    

}
