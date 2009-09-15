package com.cannontech.common.pao;

public class DisplayablePaoBase implements DisplayablePao {
    private PaoIdentifier paoIdentifier;
    private String name;

    public DisplayablePaoBase(PaoIdentifier paoIdentifier, String name) {
        this.paoIdentifier = paoIdentifier;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
}
