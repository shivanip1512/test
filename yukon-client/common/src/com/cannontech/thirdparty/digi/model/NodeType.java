package com.cannontech.thirdparty.digi.model;

import com.cannontech.core.dao.NotFoundException;

public enum NodeType {

    // 0 = coordinator, 1 = router (UPRO is this), 2 = End Node, -1 = Unknown
    Unknown(-1),
    Coordinator(0),
    Router(1),
    EndNode(2);
    
    private int value;
    
    private NodeType(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static NodeType getNodeType(int value) {
        for (NodeType nodeType : values()) {
            if (nodeType.getValue() == value) {
                return nodeType;
            }
        }
        throw new NotFoundException("Node Type not found:" + value);
    }
}
