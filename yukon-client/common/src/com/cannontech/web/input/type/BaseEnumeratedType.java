package com.cannontech.web.input.type;

import java.util.List;

/**
 * Base class for input types which represent a list input type.
 */
public abstract class BaseEnumeratedType<T> implements InputType<T> {

    private String renderer = "enumeratedType.jsp";

    public abstract List<String> getOptionList();

    public BaseEnumeratedType() {
        super();
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

}