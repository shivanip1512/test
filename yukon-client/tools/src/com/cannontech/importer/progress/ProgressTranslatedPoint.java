package com.cannontech.importer.progress;

import java.util.StringTokenizer;

public class ProgressTranslatedPoint {
    private String original;
    private String pointName;
    private String uom;
    private String deviceName;
    private String paoName;
    private String translation;
    VirtualDeviceTS device;
    
    ProgressTranslatedPoint(String Original){
        this.original = Original;
        parse();
    }

    public String getPointName(){
        return pointName;
    }

    public String getUoM(){
        return uom;
    }

    public String getDeviceName(){
        return deviceName;
    }
    public void setDevice( VirtualDeviceTS vd ){
        device = vd;
    }

    public String getPaoName(){
        return paoName;
    }
    
    public VirtualDeviceTS getDevice(){
        return device;
    }
    public String getTranslation(){
        return translation;
    }
    private boolean parse(){
        boolean ret = true;
        StringTokenizer st = new StringTokenizer(original,"_",false);
        
        String tok = st.nextToken();
        paoName = "SUB " + tok;
        
        tok = st.nextToken();
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
        
        tok = st.nextToken();
        uom = tok;
        String rest = tok;
        while( st.hasMoreTokens() )
        {
        	rest += " " + st.nextToken();
        }
        pointName += new String(" " + rest);
     
        deviceName = new String("dunno");
        
        //create device.
        device = new VirtualDeviceTS();
        device.setPAOCategory("DEVICE");
        device.setPAOName(paoName);
        device.setDeviceType("VIRTUAL SYSTEM");
        
        translation = new String("Point ID:" + original + ";DrivePath:;Filename:;POINTTYPE:ANALOG;");
        
        return ret;
    }

}
