package com.cannontech.importer.fdr.translation;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.importer.fdr.ImporterVirtualDevice;

public class TranslationBase {
    private String inputString;

    private ImporterVirtualDevice device;
    private FdrTranslation translation;
    protected Map<String,String> pointParameters;
    
    public TranslationBase(String original) {
        inputString = original;
        pointParameters = new HashMap<String,String>();
    }
    public void setDevice( ImporterVirtualDevice vd ){
        device = vd;
    }
    
    public String getInputString() {
        return inputString;
    }
    public ImporterVirtualDevice getDevice(){
        if( device == null) {
            device = new ImporterVirtualDevice();
        }
        return device;
    }
    
    public FdrTranslation getTranslation() {
        if(translation == null) {
            translation = new FdrTranslation();
        }
        return translation;
    }

    public void setTranslation(FdrTranslation translation) {
        this.translation = translation;
    }
    
    public String getPointParameter(String param) {
        return pointParameters.get(param);
    }
    public Map<String, String> getPointParametersMap() {
        return pointParameters;
    }
}
