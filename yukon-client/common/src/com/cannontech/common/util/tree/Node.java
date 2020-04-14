package com.cannontech.common.util.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public class Node<T> {

    public static class TreeDebugStatistics {
        public AtomicInteger TOTAL = new AtomicInteger();
        public AtomicInteger _EMPTY_ = new AtomicInteger();
        public AtomicInteger NULL = new AtomicInteger();
        public AtomicInteger NO_LOCATION = new AtomicInteger();
        public AtomicInteger FAILED_TO_CREATE = new AtomicInteger();
        @Override
        public String toString() {
            return String.format("TOTAL:%s _EMPTY_: %s NULL:%s NO_LOCATION:%s FAILED_TO_CREATE:%s", TOTAL.get(), _EMPTY_.get(),
                    NULL.get(), NO_LOCATION.get(), FAILED_TO_CREATE.get());
        }
    }

    
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