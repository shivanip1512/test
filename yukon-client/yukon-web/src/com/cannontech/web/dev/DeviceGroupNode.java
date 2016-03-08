package com.cannontech.web.dev;

public class DeviceGroupNode {
    private String id;
    private String leftChild;
    private String rightChild;
    private String parent;
    private String rootName;
    
    public DeviceGroupNode() {
        super();
    }
    
    
    public DeviceGroupNode(String id, String leftChild, String rightChild, String parent, String rootName) {
        super();
        this.id = id;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.parent = parent;
        this.rootName = rootName;
    }


    /**
 * @return the rootParentName
 */
public String getRootName() {
    return rootName;
}

/**
 * @param rootParentName the rootParentName to set
 */
public void setRootName(String rootName) {
    this.rootName = rootName;
}

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return the leftChild
     */
    public String getLeftChild() {
        return leftChild;
    }
    /**
     * @param leftChild the leftChild to set
     */
    public void setLeftChild(String leftChild) {
        this.leftChild = leftChild;
    }
    /**
     * @return the rightChild
     */
    public String getRightChild() {
        return rightChild;
    }
    /**
     * @param rightChild the rightChild to set
     */
    public void setRightChild(String rightChild) {
        this.rightChild = rightChild;
    }
    /**
     * @return the parent
     */
    public String getParent() {
        return parent;
    }
    /**
     * @param parent the parent to set
     */
    public void setParent(String parent) {
        this.parent = parent;
    }

}
