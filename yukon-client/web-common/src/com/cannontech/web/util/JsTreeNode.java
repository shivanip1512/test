package com.cannontech.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsTreeNode {

    private Map<String, Object> attributes;
    private List<JsTreeNode> children;
    private String nodePath;

    // constructor
    public JsTreeNode() {
        super();
        this.attributes = new HashMap<String, Object>();
        this.children = new ArrayList<JsTreeNode>();
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
    public void addChild(JsTreeNode node) {
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
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap() {
        
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.putAll(getAttributes());
        
        Map<String, Object> attr = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> metadata = new HashMap<String, Object>();
        
        attr.put("id", map.get("id"));
        data.put("title", map.get("text"));
        data.put("icon", map.get("iconCls") + " yukon");
        metadata.put("id", map.get("id"));
        
        if (map.keySet().contains("info")) {
            metadata.put("groupName", ((Map<String, String>)map.get("info")).get("groupName"));
        }
        
        map.put("attr", attr);
        map.put("data", data);
        map.put("metadata", metadata);
        
        List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
        for (JsTreeNode c : getChildren()) {
            children.add(c.toMap());
            
        }
        map.put("children", children);
        
        return map;
    }

    // setter and getters
    public List<JsTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<JsTreeNode> children) {
        this.children = children;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

	public String getNodePath() {
		return nodePath;
	}

	public void setNodePath(String nodePath) {
		this.nodePath = nodePath;
	}
    
	
    /**
     * To be called on node after tree hierarchy has been constructed
     * @param node
     */
    public static void setLeaf(JsTreeNode node) {
        
        // leaf? (must be after child groups are added)
        if (node.hasChildren()) {
            node.setAttribute("leaf", false);
        }
        else {
            node.setAttribute("leaf", true);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static void addToNodeInfo(JsTreeNode node, String key, String data) {
        
        Map<String, String> info = new HashMap<String, String>(); 
        
        if (node.getAttributes().keySet().contains("info")) {
            info = (Map<String, String>)node.getAttributes().get("info");
        }
        
        info.put(key, data);
        node.setAttribute("info", info);
    }
	
}
