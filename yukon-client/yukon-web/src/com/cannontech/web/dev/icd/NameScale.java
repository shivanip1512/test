package com.cannontech.web.dev.icd;

public class NameScale {
    public NameScale(String name, double multiplier) {
        this.name = name;
        this.multiplier = multiplier;
    }
    String name;
    double multiplier;
    @Override
    public String toString() {
        return name + ":" + multiplier;
    }
}