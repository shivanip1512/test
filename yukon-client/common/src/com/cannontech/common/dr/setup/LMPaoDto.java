package com.cannontech.common.dr.setup;

import com.cannontech.common.pao.PaoType;

public class LMPaoDto extends LMDto {

    private PaoType type;

    public LMPaoDto() {
        super();
    }

    public LMPaoDto(Integer id, String name, PaoType type) {
        super(id, name);
        this.type = type;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }
}
