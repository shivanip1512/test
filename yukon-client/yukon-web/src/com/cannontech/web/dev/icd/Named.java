package com.cannontech.web.dev.icd;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class Named<T extends PointDefinition> {

    public String name;
    public T value;

    public Named() {
        this.name = "none";
        this.value = null;
    }

    @JsonAnySetter
    void set(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }
}