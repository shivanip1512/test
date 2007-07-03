package com.cannontech.common.gui.tree;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class CustomRenderJTree extends JTree {
    private Set<Renderer> renderers = new HashSet<Renderer>();
    
    public CustomRenderJTree() {
    }
    
    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value != null && renderers != null) {
            String result;
            for (Renderer renderer : renderers) {
                // first try rendering the raw value
                result = renderer.render(value);
                if (result != null){
                    return result;
                }
                // if we got here, see if the node has an object
                if (value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    Object userObject = node.getUserObject();
                    result = renderer.render(userObject);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        // still nothing?, default to superclass
        return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
    }

    public void addRenderer(Renderer renderer) {
        this.renderers.add(renderer);
    }
}
