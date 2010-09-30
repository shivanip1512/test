package com.cannontech.web.capcontrol.ivvc.models;

import java.util.List;

public class VfGraphSettings {
    
    int width;
    List<VfLineSettings> lines;
    
    public List<VfLineSettings> getLines() {
        return lines;
    }

    public void setLines(List<VfLineSettings> lines) {
        this.lines = lines;
    }
    
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
