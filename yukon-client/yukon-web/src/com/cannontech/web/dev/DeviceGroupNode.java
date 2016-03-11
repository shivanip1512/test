package com.cannontech.web.dev;

public class DeviceGroupNode {
    private String id;
    private boolean leftChild;
    private boolean rightChild;
    private String parent;
    private String rootName;

    public boolean hasLeftChild() {
        return leftChild;
    }

    public void setLeftChild() {
        this.leftChild = true;
    }

    public boolean hasRightChild() {
        return rightChild;
    }

    public void setRightChild() {
        this.rightChild = true;
    }

    public DeviceGroupNode() {
    }

    public DeviceGroupNode(String id, String parent, String rootName) {
        this.id = id;
        this.parent = parent;
        this.rootName = rootName;
    }

    public String getRootName() {
        return rootName;
    }

    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

}
