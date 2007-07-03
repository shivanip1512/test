package com.cannontech.common.gui.tree;

public abstract class Renderer<T> {
    private final Class<T> clazz;
    public Renderer(Class<T> clazz) {
        this.clazz = clazz;
    }
    public abstract String doRender(T o);
    public String render(Object o) {
        if (clazz.isAssignableFrom(o.getClass())) {
            T t = clazz.cast(o);
            return doRender(t);
        }
        return null;
    }
}
