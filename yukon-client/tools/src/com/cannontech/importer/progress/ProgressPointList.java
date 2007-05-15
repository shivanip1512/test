package com.cannontech.importer.progress;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.io.*;
import java.util.StringTokenizer;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.spring.YukonSpringHook;



public class ProgressPointList extends LinkedList {
    
    private LinkedList ll;
    private LinkedList deviceList;
    private Connection poolConnection;
        
    public ProgressPointList(){}
    
    public ProgressPointList(String FileName, Connection p) {
        boolean success = false;
        ll = new LinkedList();
        deviceList = new LinkedList();
        poolConnection = p;
        
        try{
            BufferedReader in = new BufferedReader(new FileReader(FileName));

            success = LoadListFromFile(in);
            
            in.close();

            if( success == false ){
                ll.clear();
            }else{
                success = translateListFromFile();
                if ( success == false ){
                    ll.clear();
                    super.clear();
                }
            }
            
        }catch( Exception e ){
            System.err.println(e);
            ll.clear();
            super.clear();
        }
    }

    public ProgressPointList(Collection arg0) {
        super(arg0);
    }
    
    private boolean LoadListFromFile(BufferedReader in){
        String line;
        boolean result = true;
        
        while( true ){
            try{
                if( in.ready() )
                    line = new String(in.readLine());
                else
                    break;
            }catch(IOException e){
                System.out.print(e);
                result = false;
                break;
            }
            if( line == null ){
                break;
            }else{
                StringTokenizer st = new StringTokenizer(line, ",", false);
                st.nextToken();
                ll.add(st.nextToken());
            }
        }
        return result;
    }
    
    private boolean translateListFromFile(){
        boolean ret = true;
        String tester = new String();
        
        Iterator itr_ll = ll.iterator();
        for( ; itr_ll.hasNext(); ){
            super.add( new ProgressTranslatedPoint( (String)itr_ll.next() ));
        }
        
        Iterator itr = super.iterator();
        for(  ; itr.hasNext(); ){
            if(((ProgressTranslatedPoint)itr.next()).getUoM() == tester ){
                ret = false;
            }
        }
        
        return ret;
    }
    
    public boolean addDevicesToDatabase() throws Exception {
        boolean ret = true;
        VirtualDeviceTS vd;
        
        try{
            Iterator itr = super.iterator();
            for(  ; itr.hasNext(); ){
                vd = ((ProgressTranslatedPoint)itr.next()).getDevice();
                if( !deviceList.contains(vd) ){
                    deviceList.add(vd);
                }
            }
            
            if( deviceList.size() > 0 ){
                itr = deviceList.iterator();
                while( itr.hasNext() ){
                    vd = ((VirtualDeviceTS)itr.next());
                    vd.setDbConnection(poolConnection);
                    try {
                        vd.add();
                    } catch (SQLException e) {
                        System.out.println("Error adding " + vd.getPAOName() );
                    }
                }
    
            }
        }catch(DataAccessResourceFailureException e){
            System.out.println("Unable to Get database Connection. Abort");
            throw new Exception("Abort, unable to get DB connection. ");
        }
        return ret;
    }
    public void addTranslationToPoints() throws SQLException{

        //get PointID  and translation
        //Directiontype and Interface and Destination are all defaulted
        String DirectionType = new String("Receive");
        String Interface = new String("TEXTIMPORT");
        String Destination = new String("TEXTIMPORT");
        String translation;
        int pid;
        ProgressTranslatedPoint ptp;
        VirtualDeviceTS vd;
        
        
        Iterator itr;// = super.iterator();
        Iterator itr2 = deviceList.iterator();
        for(;itr2.hasNext();){
            itr = super.iterator();
            vd = (VirtualDeviceTS)itr2.next();
            for(;itr.hasNext();){
                ptp = (ProgressTranslatedPoint)itr.next();
                if( ptp.getPaoName().compareTo(vd.getPAOName()) == 0 ){
                    ptp.setDevice( vd );
                }
            }
        }
       
        itr = super.iterator();
        for(;itr.hasNext();){
            pid = 0;
            ptp = (ProgressTranslatedPoint)itr.next();
            translation = ptp.getTranslation();
            //get pid from database here.
            SimpleJdbcOperations jdbcOps = (SimpleJdbcOperations) YukonSpringHook.getBean("simpleJdbcTemplate");
            List<LitePoint> allPointsOnPao = DaoFactory.getPointDao().getLitePointsByPaObjectId( ptp.getDevice().getPAObjectID() );
            for (LitePoint point : allPointsOnPao) {
                if ( 0 == point.getPointName().compareTo( ptp.getPointName()) ){
                    pid = point.getLiteID();
                    break;
                }
            }
            //populate columns in table.
            /*
             INSERT INTO fdrtranslation ( POINTID,DIRECTIONTYPE,InterfaceType,DESTINATION,TRANSLATION )
             VALUES(6288,'Receive','TEXTIMPORT','TEXTIMPORT','Point ID:ALDM_C5013_kvar;DrivePath:;Filename:;POINTTYPE:ANALOG;')
             */
            if( pid != 0 )
                jdbcOps.update("insert into fdrtranslation VALUES (?,?,?,?,?)", pid, DirectionType, Interface, Destination, translation );
            
        }
        
    }
}
