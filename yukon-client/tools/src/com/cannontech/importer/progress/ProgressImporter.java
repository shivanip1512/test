package com.cannontech.importer.progress;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.importer.point.PointImportUtility;
import com.cannontech.yukon.IDatabaseCache;

import java.sql.Connection;

public class ProgressImporter {
    private static Connection poolConnection;
    
    private static boolean PointinList( List<LitePoint> pnts, ProgressTranslatedPoint point){
        for( LitePoint p : pnts )
        {
            String one = p.getPointName() ;
            String two = point.getPointName();
            one = one.toUpperCase();
            two = two.toUpperCase();
            if( one.compareTo(two) == 0 )
                return true;
        }
        return false;
    }
    
    private static List<ByteArrayOutputStream> generateCSV( ProgressPointList p )throws IOException {
    	ByteArrayOutputStream analogOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter analogStreamWriter = new OutputStreamWriter( analogOutputStream );
    	Writer analogWriter = new BufferedWriter(analogStreamWriter);
    	
    	ByteArrayOutputStream statusOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter statusStreamWriter = new OutputStreamWriter( statusOutputStream );
    	Writer statusWriter = new BufferedWriter(statusStreamWriter);
    	
        int i = 1;
        Iterator itr = p.iterator();
        ProgressTranslatedPoint point;
        String curDevice = new String();
        IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
        synchronized(cache)
        {
            
            while(itr.hasNext()){
                point = (ProgressTranslatedPoint)itr.next();
                VirtualDeviceTS  vs = point.getDevice();
                if( vs != null ){
                    List<LitePoint> pnts = DaoFactory.getPointDao().getLitePointsByPaObjectId(vs.getPAObjectID());
                    int highestOffset = 0;
                    for( LitePoint p2 : pnts )
                    {
                        if( p2.getPointOffset() > highestOffset)
                            highestOffset = p2.getPointOffset();
                    }
                    
                    if( pnts == null || !PointinList(pnts, point) )
                    {
                        if( curDevice.compareTo(point.getPaoName()) != 0 ){
                            curDevice = point.getPaoName();
                            i = highestOffset+1;
                        }
                        //"hackish" way to detect that we want a status point
                        if(point.getPointName().endsWith("VRD")) {
                            //status 
                			/*
                			 *PointName,PointType,DeviceName,PointOffset,StateGroupName,CtrlType,Ctrloffset,time1,time2,
                			 *Archive,State1,State2,NonUpdate,Abnormal,UnCommanded,CommandFail
                			 */
                        	statusWriter.write(point.getPointName() + ",Status," + point.getPaoName() + "," + i + "," +"TwoStateStatus,"  );
                        	statusWriter.write(",,,,,,,,,,,,\n");
                        }else {
	                        //analog
	                        analogWriter.write(point.getPointName() + ",Analog," + point.getPaoName() + "," + i + "," + point.getUoM()  );
	                        analogWriter.write(",1,0,0,1,,,,,,,NONE,,,,(none),,\n");
                        }
                        i++;
                    }else
                        System.out.println("Skipped");
                }
                else
                {
                    System.out.println("Device NULL:" + point.getPaoName() );
                }
            }
        }
        analogWriter.flush();
        statusWriter.flush();
        List<ByteArrayOutputStream> retList = new ArrayList<ByteArrayOutputStream>();
        retList.add(analogOutputStream);
        retList.add(statusOutputStream);
        return retList;
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

            //create add the virtual devices to the database  
            //If devices are already in place, the fdrtranslation portion will fail
            p.addDevicesToDatabase();

            //create CSV file for PointImportUtility
            List<ByteArrayOutputStream> outputlist = generateCSV(p);
            //run point importer on analog
            BufferedReader buffReader = new BufferedReader( new InputStreamReader( new ByteArrayInputStream(outputlist.get(0).toByteArray())) );            
            PointImportUtility.processAnalogPoints(buffReader);

            buffReader = new BufferedReader( new InputStreamReader( new ByteArrayInputStream(outputlist.get(1).toByteArray())) );            
            PointImportUtility.processStatusPoints(buffReader);
            
            //add FDR translations to the points in the database.
            p.addTranslationToPoints();
            
        }catch( Exception e ){
            CTILogger.error("Unable to do it", e);
        }

    }

}
