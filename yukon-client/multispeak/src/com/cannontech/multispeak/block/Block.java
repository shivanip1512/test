package com.cannontech.multispeak.block;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.syntax.SyntaxItem;

public interface Block {
    public String defaultDateFormat = "MM/dd/yyyy HH:mm:ss zzz";
    public String getField(SyntaxItem syntaxItem);
    public void populate(Meter meter);
    public void populate(Meter meter, PointValueHolder pointValue);
    public boolean hasData();
}
