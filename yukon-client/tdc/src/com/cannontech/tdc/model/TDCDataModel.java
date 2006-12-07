package com.cannontech.tdc.model;

import java.awt.Frame;



public interface TDCDataModel {

    void initModel();
    void updateModel(Object comp, String fieldNames, Object values, String cxt);
    void saveModel();
    public void setMainFrame(Frame f);
    void removeModel();
    String[] getAllRegisteredComponents();

}
