package com.cannontech.importer.fdr.translation;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.importer.fdr.ImporterVirtualDevice;

public class ProgressOpcImportParserImpl implements TranslationParse {

    public void parse(TranslationBase base) throws IOException{
        String parseValue;
        String pointName;
        String uom;
        String paoName;
        StringTokenizer token1 = new StringTokenizer(base.getInputString(),",",false);
        try{
            if( token1.countTokens() < 4)
                throw new IOException();
            parseValue = token1.nextToken();
        }catch( NoSuchElementException e) {
            throw new IOException("Bad input string. Expected XXX_XNNNN_XXX_X, Server name, Group name, Item Name. got " + base.getInputString());
        }
        StringTokenizer token2 = new StringTokenizer(parseValue,"_",false);
        if(token2.countTokens() < 3) {
            throw new IOException("Wrong format on string: " + parseValue);
        }
        String tok = token2.nextToken();
        paoName = "SUB " + tok;
        
        tok = token2.nextToken();
        pointName = tok; // add 0's if number is less than 4 digits
        if (pointName.length() < 5){
           String temp = new String();
           String front = new String(); String back = new String();
           for( int i = 0; i < 5 - pointName.length(); i++ ){
               temp += "0";
           }
           front = pointName.substring(0,1);
           back = pointName.substring(1);
           pointName = new String( front + temp + back );
        }
        
        tok = token2.nextToken();

        uom = tok;
        String rest = tok;
        while( token2.hasMoreTokens() )
        {
            rest += " " + token2.nextToken();
        }
        pointName += new String(" " + rest);
             
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
        String type;
        if( pointName.endsWith("VRD") ) {
            type = "Status";
        }else{
            type = "Analog";
        }
        pointParameters.put("POINTTYPE", type);
        
        //Create Translation
        FdrTranslation trans = base.getTranslation();
        trans.setDirection(FdrDirection.RECEIVE);
        trans.setInterfaceType(FdrInterfaceType.OPC);
        
        String serverName = token1.nextToken();
        String groupName = token1.nextToken();
        String itemName = token1.nextToken();
        
        trans.setTranslation("Server Name:" + serverName + ";OPC Group:" +groupName+";OPC Item:"+itemName+";POINTTYPE:" + type + ";");
    }  

}
