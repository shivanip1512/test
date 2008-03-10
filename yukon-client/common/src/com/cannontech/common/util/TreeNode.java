package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeNode {

    private Map<String, Object> attributes;
    private List<TreeNode> children;

    // constructor
    public TreeNode() {
        super();
        this.attributes = new HashMap<String, Object>();
        this.children = new ArrayList<TreeNode>();
    }
    
    /**
     * Add a value to this node attribute map.
     * @param name
     * @param value
     */
    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }
   
    /**
     * A a node to this node list of child nodes.
     * @param node
     */
    public void addChild(TreeNode node) {
        this.children.add(node);
    }
    
    /**
     * Determine if this node has any child nodes.
     * @return
     */
    public Boolean hasChildren() {
        return this.children.size() > 0 ? true : false;
    }
    

    /**
     * Convert self to a map of values containing all attributes.
     * Children are represented as a nested list of themselves as maps.
     * @return
     */
    public Map<String, Object> toMap() {
        
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.putAll(getAttributes());
        
        List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
        for (TreeNode c : getChildren()) {
            children.add(c.toMap());
            
        }
        map.put("children", children);
        
        return map;
    }

    // setter and getters
    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
}
