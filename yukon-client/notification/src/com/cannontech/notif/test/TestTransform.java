package com.cannontech.notif.test;

import com.cannontech.notif.outputs.Notification;


public class TestTransform {
    public static void main(String[] args) {
        Notification notif = new Notification("alarm");
        
        notif.put("pointname", "Toms Point");
        notif.put("value", "3.345");
        notif.put("unitofmeasure", "kV");

    }
}
