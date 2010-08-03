package com.cannontech.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupPredicateEnum;
import com.cannontech.common.util.predicate.AggregateAndPredicate;
import com.cannontech.common.util.predicate.Predicate;

public class ExtTreeNode {

    private Map<String, Object> attributes;
    private List<ExtTreeNode> children;
    private String nodePath;

    // constructor
    public ExtTreeNode() {
        super();
        this.attributes = new HashMap<String, Object>();
        this.children = new ArrayList<ExtTreeNode>();
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
    public void addChild(ExtTreeNode node) {
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
        for (ExtTreeNode c : getChildren()) {
            children.add(c.toMap());
            
        }
        map.put("children", children);
        
        return map;
    }

    // setter and getters
    public List<ExtTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<ExtTreeNode> children) {
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
    public static void setLeaf(ExtTreeNode node) {
        
        // leaf? (must be after child groups are added)
        if (node.hasChildren()) {
            node.setAttribute("leaf", false);
        }
        else {
            node.setAttribute("leaf", true);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static void addToNodeInfo(ExtTreeNode node, String key, String data) {
        
        Map<String, String> info = new HashMap<String, String>(); 
        
        if (node.getAttributes().keySet().contains("info")) {
            info = (Map<String, String>)node.getAttributes().get("info");
        }
        
        info.put(key, data);
        node.setAttribute("info", info);
    }
    
    public static AggregateAndPredicate<DeviceGroup> getAggregratePredicateFromString(String predicatesStr) {
        
        String[] predicateStrs = StringUtils.split(predicatesStr, ",");
        List<Predicate<DeviceGroup>> predicates = new ArrayList<Predicate<DeviceGroup>>();
        for (String predicateStr : predicateStrs) {
            Predicate<DeviceGroup> predicate = DeviceGroupPredicateEnum.valueOf(predicateStr.trim()).getPredicate();
            predicates.add(predicate);
        }
        AggregateAndPredicate<DeviceGroup> aggregatePredicate = new AggregateAndPredicate<DeviceGroup>(predicates);
        
        return aggregatePredicate;
    }
	
}
