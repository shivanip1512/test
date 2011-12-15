package com.cannontech.util;

import java.util.Map;

public class JsTreeBuilderUtil {
    
    
    public static String createUniqueNodeId(String nodeName, Map<String, Integer> nodeIdHistory) {
        
        String simpleNodeIdChoice = nodeName.replaceAll("[^a-zA-Z0-9]","");
        
        if (nodeIdHistory.containsKey(simpleNodeIdChoice)) {
            
            // find out the last number
            int lastSuffix = nodeIdHistory.get(simpleNodeIdChoice);
            int newSuffix = lastSuffix + 1;
            nodeIdHistory.put(simpleNodeIdChoice, newSuffix);
            return simpleNodeIdChoice + "_" + newSuffix;
            
        } else {
            
            nodeIdHistory.put(simpleNodeIdChoice, 1);
            return simpleNodeIdChoice;
        }
    }
    
}