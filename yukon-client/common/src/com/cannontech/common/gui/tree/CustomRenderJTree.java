package com.cannontech.common.gui.tree;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class CustomRenderJTree<T> extends JTree {
    private Renderer<T> renderer;
    private final Class<T> clazz;
    
    public CustomRenderJTree(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value != null && clazz != null) {
            if (clazz.isAssignableFrom(value.getClass())){
                return renderer.render(clazz.cast(value));
            } else if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();
                if (clazz.isAssignableFrom(userObject.getClass())) {
                    return renderer.render(clazz.cast(userObject));
                }
            }
        }
        return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
    }

    public void setRenderer(Renderer<T> renderer) {
        this.renderer = renderer;
    }
}
