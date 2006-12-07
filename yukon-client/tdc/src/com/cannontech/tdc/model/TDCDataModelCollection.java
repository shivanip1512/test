package com.cannontech.tdc.model;

import java.awt.Frame;
import java.util.Hashtable;
import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;

public class TDCDataModelCollection implements TDCDataModel {

    private Hashtable<String, TDCDataModel> models = new Hashtable<String, TDCDataModel>(10);
    private Frame mainFrame;

    private TDCDataModelCollection() {

    }

    public TDCDataModelCollection(Frame f) {
        mainFrame = f;
        
        
    }

    public void updateModel(Object comp, String fieldNames, Object values, String cxt) {
        if (comp != null)
        {
            for (Iterator iter = models.values().iterator(); iter.hasNext();) {
                TDCDataModel model = (TDCDataModel) iter.next();
                model.updateModel(comp, fieldNames, values, cxt);
            }
        }
        else
        {
            CTILogger.error("Misused TDCDataModelCollection.updateModel ()  - null objects can't be passed as swing components");
        }
    }

    public void addTDCModel(String name, TDCDataModel m) {
        models.put(name, m);
    }

    public TDCDataModel getTDCModel (String name) 
    {
        return models.get(name);
    }
   
    public void initModel() {
        for (Iterator iter = models.values().iterator(); iter.hasNext();) {
            TDCDataModel model = (TDCDataModel) iter.next();
            model.setMainFrame(mainFrame);
            model.initModel();
        }
    }

    public void setMainFrame(Frame f) {
        mainFrame = f;
    }

    public Frame getMainFrame() {
        return mainFrame;
    }

    public void saveModel() {
        for (Iterator iter = models.values().iterator(); iter.hasNext();) {
            TDCDataModel model = (TDCDataModel) iter.next();
            model.saveModel();
        }
    }

    public void removeModel() {
        for (Iterator iter = models.values().iterator(); iter.hasNext();) {
            TDCDataModel model = (TDCDataModel) iter.next();
            model.removeModel();
        }
        
    }

    public String[] getAllRegisteredComponents() {
        return null;
    }


}
