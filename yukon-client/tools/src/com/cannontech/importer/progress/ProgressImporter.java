package com.cannontech.importer.progress;

import java.io.*;
import java.util.Iterator;
import com.cannontech.database.PoolManager;
import com.cannontech.importer.point.PointImportUtility;

import java.sql.Connection;

public class ProgressImporter {
    private static Connection poolConnection;
    
    private static void createCSV( ProgressPointList p )throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter("c:\\ProgressImport.csv"));
        int i = 1;
        Iterator itr = p.iterator();
        ProgressTranslatedPoint point;
        String curDevice = new String();
        while(itr.hasNext()){
            point = (ProgressTranslatedPoint)itr.next();
            if( curDevice.compareTo(point.getPaoName()) != 0 ){
                curDevice = point.getPaoName();
                i = 1;
            }
            out.write(point.getPointName() + ",Analog," + point.getPaoName() + "," + i + "," + point.getUoM()  );
            out.write(",1,0,0,1,,,,,,,NONE,,,,(none),,\n");
            i++;
        }
        out.close();
    }

    /**
     * 
     */
    public static void main(String[] args) {

        if( args.length < 1){
            System.out.println(" Missing args, please input filename. ");
            return;
        }else
            System.out.println( args[0] );
        
        poolConnection = PoolManager.getYukonConnection();
        ProgressPointList p = new ProgressPointList(args[0], poolConnection );
        System.out.println(p.size());

        //print out to test,  not needed in final revision
        Iterator itr = p.iterator();
        ProgressTranslatedPoint temp;
        for( ; itr.hasNext(); ){
            temp = (ProgressTranslatedPoint)itr.next();
            System.out.println( "PointName: " + temp.getPointName() + " UoM: " + temp.getUoM() + 
                                " Paoname: " + temp.getPaoName() + " DeviceName: " + temp.getDeviceName() );
        }
        try{
            //create CSV file for PointImportUtility
            createCSV(p);
            //create add the virtual devices to the database  
            //If devices are already in place, the fdrtranslation portion will fail
            p.addDevicesToDatabase();
            //run point importer
            boolean analogSuccess = com.cannontech.importer.point.PointImportUtility.processAnalogPoints("c:\\ProgressImport.csv");
            //add fdr translations to the points in the database.
            p.addTranslationToPoints();
            
        }catch( Exception e ){
            System.out.println(e);
        }

    }

}
