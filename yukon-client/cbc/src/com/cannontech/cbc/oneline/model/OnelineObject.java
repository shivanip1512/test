package com.cannontech.cbc.oneline.model;

import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxLine;

public interface OnelineObject {


    OneLineDrawing getDrawing();
    SubBus getSubBusMsg();
    LxLine getRefLnBelow();
    Integer getPaoId();
    LxLine getRefLnAbove();
    void draw();
    void addDrawing(OneLineDrawing d);


}
