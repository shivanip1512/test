package com.cannontech.importer.fdr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.importer.fdr.translation.ProgressOpcImportParserImpl;
import com.cannontech.importer.fdr.translation.ProgressTextImportParseImpl;
import com.cannontech.importer.fdr.translation.ProgressValmetImportParserImpl;
import com.cannontech.importer.fdr.translation.TranslationBase;
import com.cannontech.importer.fdr.translation.TranslationParse;
import com.cannontech.importer.point.PointImportUtility;
import com.cannontech.spring.YukonSpringHook;


public class FdrImporter {
    
    private Logger log = YukonLogManager.getLogger(FdrImporter.class);
    
    private PointDao pointDao = YukonSpringHook.getBean("pointDao",PointDao.class);
    private String fileName;
    private List<String> inputStrings;
    private TranslationParse parser;
    private List<TranslationBase> translationList;
    
    public FdrImporter(String argZero) {
        fileName = argZero;
    }
    
    public void setParser(TranslationParse parser) {
        this.parser = parser;
    }
    
    private boolean pointNameinList(List<LitePoint> pnts, String name) {
        
        for( LitePoint point : pnts ) {
            if( point.getPointName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }
    
    public void loadInputStringsFromFile() throws IOException{
        String line;
        inputStrings = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        
        while( true ){
            try{
                if( reader.ready() )
                    line = new String(reader.readLine());
                else
                    break;
            }catch(IOException e){
                System.out.print(e);

                throw e;
            }
            if( line != null ){
                inputStrings.add(line);
            }else{
                break;
            }
        }
        reader.close();
    }
    public void parseStrings() {
        translationList = new ArrayList<TranslationBase>();
                
        for(String input : inputStrings) {
            TranslationBase base = new TranslationBase(input);
            try{
                parser.parse(base);
                translationList.add(base);
            }catch(IOException e) {
                log.error("Input skipped",e);
            }
        }
    }
    public void addDevicesToDatabase(Connection dbconn) {
        DeviceDao deviceDao = YukonSpringHook.getBean("deviceDao",DeviceDao.class);
        for( TranslationBase base : translationList ) {
            ImporterVirtualDevice device = base.getDevice();
            device.setDbConnection(dbconn);
            LiteYukonPAObject obj = deviceDao.getLiteYukonPaobjectByDeviceName(device.getPAOName());
            if(obj != null) {
                //update
                device.setDeviceID(obj.getYukonID());
                try{
                    device.update();
                }catch(SQLException e) {
                    log.error("Error updating " + device.getPAOName() + " in the database.");
                }
            }else {
                //add
                try{
                    device.add();
                }catch(SQLException e) {
                    log.error("Error adding " + device.getPAOName() + " to the database.");
                }
            }
        }
    }
    public void addPoints() throws IOException {
        List<ByteArrayOutputStream> strmList = generateCSVFile();
        //run point importer on analog
        BufferedReader buffReader = new BufferedReader( new InputStreamReader( new ByteArrayInputStream(strmList.get(0).toByteArray())) );            
        PointImportUtility.processAnalogPoints(buffReader);

        buffReader = new BufferedReader( new InputStreamReader( new ByteArrayInputStream(strmList.get(1).toByteArray())) );            
        PointImportUtility.processStatusPoints(buffReader);
    }
    
    private List<ByteArrayOutputStream> generateCSVFile() throws IOException {
        ByteArrayOutputStream analogOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter analogStreamWriter = new OutputStreamWriter( analogOutputStream );
        Writer analogWriter = new BufferedWriter(analogStreamWriter);
        
        ByteArrayOutputStream statusOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter statusStreamWriter = new OutputStreamWriter( statusOutputStream );
        Writer statusWriter = new BufferedWriter(statusStreamWriter);
        
        int i = 1;

        String curDevice = new String();
        for(TranslationBase base : translationList) {
            
            ImporterVirtualDevice  vs = base.getDevice();
            if( vs != null ){
                List<LitePoint> pnts = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(vs.getPAObjectID());
                int highestOffset = 0;
                boolean statusPoint = base.getPointParameter("POINTTYPE").equalsIgnoreCase("Status");
                for( LitePoint p2 : pnts )
                {
                    if( p2.getPointOffset() > highestOffset)
                        highestOffset = p2.getPointOffset();
                }
                if( pnts == null || !pointNameinList(pnts, base.getPointParameter("PointName")) )
                {
                    if( !curDevice.equalsIgnoreCase(base.getPointParameter("PaoName")) ){
                        curDevice = base.getPointParameter("PaoName");
                        i = highestOffset+1;
                    }

                    if( statusPoint ) {

                        statusWriter.write(base.getPointParameter("PointName") + ",Status," 
                                           + base.getPointParameter("PaoName") + "," + i + "," 
                                           + "TwoStateStatus,"  );
                        statusWriter.write(",,,,,,,,,,,,\n");
                    }else {
                        //analog
                        analogWriter.write(base.getPointParameter("PointName") + ",Analog," 
                                           + base.getPointParameter("PaoName") + "," + i 
                                           + "," + base.getPointParameter("UOM")  );
                        analogWriter.write(",1,0,0,1,,,,,,,NONE,,,,(none),,\n");
                    }
                    base.getPointParametersMap().put("OFFSET", Integer.toString(i));
                    i++;
                }else {
                    System.out.println("Skipped");
                    int deviceid = base.getDevice().getPAObjectID();
                    PointType type;
                    if( statusPoint ) {
                        type = PointType.Status;
                    }else {
                        type = PointType.Analog;
                    }
                    List<LitePoint> list = pointDao.getLitePointIdByDeviceId_PointType(deviceid,type);
                    
                    if( list.size() == 0) {
                        log.error("Point not found in the database, translation cannot be added.");
                    }else {
                        LitePoint p = null;
                        for( LitePoint point : list ) {
                            if( point.getPointName().equalsIgnoreCase(base.getPointParameter("PointName"))) {
                                p = point;
                                break;
                            }
                        }
                        if( p != null) {
                            int id = p.getPointOffset();
                            base.getPointParametersMap().put("OFFSET", Integer.toString(id));
                        }else {
                            log.error("Point not found in the database, translation cannot be added.");
                        }
                    }
                }
            }
            else
            {
                System.out.println("Device NULL:" + base.getPointParameter("PaoName") );
            }
        }
        analogWriter.flush();
        statusWriter.flush();
        List<ByteArrayOutputStream> retList = new ArrayList<ByteArrayOutputStream>();
        retList.add(analogOutputStream);
        retList.add(statusOutputStream);
        return retList;
    }
    
    public void addTranslationToPoints() {
        FdrTranslationDao fdrDao = YukonSpringHook.getBean("fdrTranslationDao",FdrTranslationDao.class);

        //Need to get the point id for the new points in the DB
        
        for(TranslationBase base : translationList) {
            FdrTranslation fdr = base.getTranslation();
            PointType type = null;
            if( "Analog".equalsIgnoreCase(base.getPointParameter("POINTTYPE")) ) {
                type = PointType.Analog;
            }else {
                type = PointType.Status;
            }
            try{ 
                int offset = Integer.parseInt(base.getPointParameter("OFFSET"));
                int deviceid = base.getDevice().getDevice().getDeviceID();
                
                LitePoint point = pointDao.getLitePointIdByDeviceId_Offset_PointType(deviceid,offset,type);
                
                int liteid = point.getLiteID();
                fdr.setPointId(liteid);
                if( fdr.getPointId() != 0) {
                    try{
                        fdrDao.add(fdr);
                    }catch(DataAccessException e) {
                        log.error("Translation already in the database");
                    }
                }else {
                    log.warn("FdrTranslation not added to the database because of bad pointid:");
                    log.warn(fdr.getTranslation());
                }
            }catch( NotFoundException e ) {
                log.error("Point Name, " + base.getPointParameter("PointName") 
                                + ", not found in database cannot add translation");
            }catch( NumberFormatException e) {
                log.error("Point Name, " + base.getPointParameter("PointName") 
                                + ", Offset is incorrect, cannot find point to add translation");
            }
        }
    }
    /**
     * 
     */
    public static void main(String[] args) {
        
        Logger log = YukonLogManager.getLogger(FdrImporter.class);
        
        Connection conn = PoolManager.getYukonConnection();
        if( args.length < 2){
            log.info(" Missing input, please input filename and the number of desired input type.");
            log.info("1 : Text Import - Progress");
            log.info("2 : OPC - Progress");
            log.info("3 : Valmet - Progress");
            return;
        }else
            CTILogger.info( args[0] );

        FdrImporter importer = new FdrImporter(args[0]);
        
        try{
            importer.loadInputStringsFromFile();
        }catch(IOException e) {
            log.error("Error Reading File. ",e);
        }
        
        int parserType = Integer.parseInt(args[1]);
        
        TranslationParse parser = null;
        switch(parserType) {
            case 1: {
                parser = new ProgressTextImportParseImpl();
                break;
            }
            case 2: {
                parser = new ProgressOpcImportParserImpl();
                break;
            }
            case 3: {
                parser = new ProgressValmetImportParserImpl();
                break;
            }
            default: {
                log.error("Not a valid input type! Exiting Application.");
                return;
            }
        }
        importer.setParser(parser);
        importer.parseStrings();
        
        importer.addDevicesToDatabase(conn);
        
        try{
            importer.addPoints();
        }catch(IOException e) {
            log.error("Unable to add points, aborting.");
            return;
        }
        
        importer.addTranslationToPoints();
        
        //done
    }   
}
