package com.cannontech.notif.test;

import java.io.File;

import javax.swing.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.notif.voice.*;

public class TestCaller {

    public static void main(String[] args) {
        TestCustomer guy1 = new TestCustomer("7632535523,6128029298", "Tom Mack");
        TestCustomer guy3 = new TestCustomer("763-253-5523", "Some Office Worker");
        
        
        Dialer dialer = new TestDialer();
        //Dialer dialer = new VocomoDialer("10.100.2.100", "notification");
        dialer.setPhonePrefix("9");
        CallPool callPool = new CallPool(dialer, 2, 180000);
        NotificationQueue queue = new NotificationQueue(callPool);
        
        //WorkerThread worker = new WorkerThread(queue, callPool);
        //worker.start();
        
        // select base directory
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(null);
        File baseStlDirectory = chooser.getSelectedFile();
        System.out.println("URI: " + baseStlDirectory.toURI());
        try {
            System.out.println("URL: " + baseStlDirectory.toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            Thread.sleep(1500);
            queue.add(new SingleNotification(guy1, "mesg 1"));
            Thread.sleep(1500);
            //queue.add(new SingleNotification(guy3, "mesg 2"));
            Thread.sleep(1500);
            //queue.add(new SingleNotification(guy3, "mesg 3"));
            System.in.read();
            //Thread.sleep(30000);
            //CTILogger.info("Shuting down worker");
            //worker.shutdown();
            CTILogger.info("Shuting down pool");
            queue.shutdown();
            callPool.shutdown();
            CTILogger.info("All shutdown!");
            
        } catch (Exception e) {
            CTILogger.warn("Got an exception", e);
        }
    }
}
