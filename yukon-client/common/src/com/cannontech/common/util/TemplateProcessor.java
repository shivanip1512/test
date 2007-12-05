package com.cannontech.common.util;

import java.util.Map;

@Deprecated
public interface TemplateProcessor {

    public abstract String process(CharSequence template,
            Map<String, ? extends Object> values);
    public boolean contains(CharSequence template, String key);
}