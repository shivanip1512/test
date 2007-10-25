package com.cannontech.cbc.oneline.model;

import java.util.List;
import java.util.Map;

import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxLine;

public interface OnelineObject {

    public void setPointCache(Map<Integer,List<LitePoint>> pointCache);
    
    public Map<Integer,List<LitePoint>> getPointCache();
    
    OneLineDrawing getDrawing();
    SubBus getSubBusMsg();
    LxLine getRefLnBelow();
    Integer getPaoId();
    LxLine getRefLnAbove();
    void draw();
    void addDrawing(OneLineDrawing d);


}
