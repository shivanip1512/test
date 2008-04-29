package com.cannontech.web.input.type;


public class RadioEnumeratedType<T> extends DelegatingEnumeratedType<T> {

    private String renderer = "radioEnumeratedType.jsp";
    
    @Override
    public String getRenderer() {
        return this.renderer;
    }
    
}
