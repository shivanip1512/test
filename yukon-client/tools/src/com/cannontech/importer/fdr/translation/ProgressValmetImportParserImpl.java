package com.cannontech.importer.fdr.translation;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;

import com.cannontech.importer.fdr.ImporterVirtualDevice;

public class ProgressValmetImportParserImpl  implements TranslationParse {

    private Logger log = YukonLogManager.getLogger(ProgressValmetImportParserImpl.class); 
    
    public void parse(TranslationBase base) throws IOException{
        StringTokenizer tokenizer = new StringTokenizer(base.getInputString(),",",false);

        if (tokenizer.countTokens() == 3) {
            String valmetPointId = tokenizer.nextToken();
            String yukonPointName = tokenizer.nextToken();
            String feederId = tokenizer.nextToken();
            
            tokenizer = new StringTokenizer(yukonPointName,"_",false);
            
            if (tokenizer.countTokens() == 4) {
                tokenizer.nextToken();
                tokenizer.nextToken();
                String uom = tokenizer.nextToken();
                String phase = tokenizer.nextToken();
                
                String pointName = feederId + "_" + uom + "_" + phase;
                String paoName = "SUB_" + feederId.substring(0,feederId.indexOf("B"));
                
                //Create device.
                ImporterVirtualDevice dev = base.getDevice();
                dev.setPAOCategory("DEVICE");
                dev.setPAOName(paoName);
                dev.setDeviceType("VIRTUAL SYSTEM");
                
                //Create Point Parameters
                Map<String,String> pointParameters = base.getPointParametersMap();
                pointParameters.put("PointName", pointName);
                pointParameters.put("PaoName", paoName);
                pointParameters.put("UOM", uom);
                
                //Only analog right now.
                String type = "Analog";

                pointParameters.put("POINTTYPE", type);
                
                //Create Translation
                FdrTranslation trans = base.getTranslation();
                trans.setDirection(FdrDirection.Receive);
                trans.setInterfaceType(FdrInterfaceType.VALMET);
                trans.setTranslation("Point:" + valmetPointId + ";POINTTYPE:" + type + ";");
                
                return;
            }
        }
        
        log.error("Input data invalid.");
        throw new IOException("Input data invalid.");
    }  
}
