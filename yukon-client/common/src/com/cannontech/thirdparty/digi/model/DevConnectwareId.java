package com.cannontech.thirdparty.digi.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.IllegalDataException;

public class DevConnectwareId {
    
    //Two represenations of the same data.
    private String macAddress;
    private String devConnectwareId;
    
    //For Validation:
    final private static String regexForMac = "([\\da-fA-F]{2}:){5}[\\da-fA-F]{2}";
    final private static Pattern macPattern = Pattern.compile(regexForMac);
    
    final private static String regexForConnectware = "([\\da-fA-F]{8}-){3}[\\da-fA-F]{8}";
    final private static Pattern connectwarePattern = Pattern.compile(regexForConnectware);
    
    /**
     * This is an object to represent a Digi Gateway MAC address.
     * 
     * Digi has a shortened mac they label their gateways with.
     * 
     * macAddress is formatted with ':' separating 6 pairs of hex digits
     * devConnectwareId is formatted in 4 sets of 8 hex digits separated by "-"
     * 
     * @throws IllegalDataException if the string is not formatted in either format.
     * 
     * @param address
     */
    public DevConnectwareId(String address) {
        Matcher matcher = macPattern.matcher(address);
        if (matcher.matches()) {
            //address was a macAddress
            this.macAddress = address;
            this.devConnectwareId = convertMacAddresstoDigi(address);
            
            return;
        }
        
        matcher = connectwarePattern.matcher(address);
        if (matcher.matches()) {
            //address was a connectwareId
            this.devConnectwareId = address;
            this.macAddress = convertDigitoMacAddress(address);
            
            return;
        }
        
        throw new IllegalDataException("String address not in a correct format.");
    }

    public String getMacAddress() {
        return macAddress;
    }
    
    public String getDevConnectwareId() {
        return devConnectwareId;
    }
    
    private static String convertMacAddresstoDigi(String mac) {
        String digiMac = mac.replaceAll(":","");
        
        digiMac = "00000000-00000000-" + digiMac.substring(0,6) + "FF-FF" + digiMac.substring(6);
        
        return digiMac;
    }
    
    private static String convertDigitoMacAddress(String connectwareId) {
        String macAddress = connectwareId.replaceAll("-","");
        
        macAddress =  macAddress.substring(16,18) + ":" 
                          + macAddress.substring(18,20) + ":"
                          + macAddress.substring(20,22) + ":"
                          + macAddress.substring(26,28) + ":"
                          + macAddress.substring(28,30) + ":"
                          + macAddress.substring(30,32) + ":";
        
        return macAddress;
    }
}
