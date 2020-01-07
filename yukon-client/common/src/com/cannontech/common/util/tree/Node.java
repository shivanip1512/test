package com.cannontech.common.util.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public class Node<T> {

    private T data = null;

    @JsonManagedReference
    private List<Node<T>> children = new ArrayList<>();

    @JsonBackReference
    private Node<T> parent = null;

    public Node(T data) {
        this.data = data;
    }

    public Node<T> addChild(Node<T> child) {
        child.setParent(this);
        this.children.add(child);
        return child;
    }

    public void addChildren(List<Node<T>> children) {
        children.forEach(each -> each.setParent(this));
        this.children.addAll(children);
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public Node<T> getParent() {
        return parent;
    }
    
    public String print() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }
    
    /**
     * If onlyCountNullNodes is true, returns count of only null nodes, otherwise return all node count
     */
    public int count(boolean onlyCountNullNodes) {
        AtomicInteger atomicInt = new AtomicInteger(0);
        incrementNodeCount(atomicInt, onlyCountNullNodes, this);
        count(this, atomicInt, onlyCountNullNodes);
        return atomicInt.get();

    }

    private void count(Node<T> node, AtomicInteger atomicInt, boolean onlyCountNullNodes) {
        for (Iterator<Node<T>> it = node.getChildren().iterator(); it.hasNext();) {
            Node<T> nextNode = it.next();
            incrementNodeCount(atomicInt, onlyCountNullNodes, nextNode);
            count(nextNode, atomicInt, onlyCountNullNodes);
        }
    }

    private void incrementNodeCount(AtomicInteger atomicInt, boolean onlyCountNullNodes, Node<T> node) {
        if (!onlyCountNullNodes) {
            atomicInt.incrementAndGet();
        } else if (onlyCountNullNodes && node.getData() == null) {
            atomicInt.incrementAndGet();
        }
    }

    private String print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(data);
        buffer.append('\n');
        for (Iterator<Node<T>> it = children.iterator(); it.hasNext();) {
            if (it.hasNext()) {
                 it.next().print(buffer, childrenPrefix + "+", childrenPrefix + "L   ");
             } else {
                 it.next().print(buffer, childrenPrefix + "+", childrenPrefix + "    ");
             }
        }
        return buffer.toString();
    }
}