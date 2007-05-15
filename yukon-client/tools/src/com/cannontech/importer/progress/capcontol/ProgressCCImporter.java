package com.cannontech.importer.progress.capcontol;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import com.cannontech.cbc.db.CapControlFactory;
import com.cannontech.cbc.model.CBCCreationModel;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.capcontrol.*;
import com.cannontech.spring.YukonSpringHook;


/*
 * CSV File format
 * One line per area, all subs feeders and capbanks are to be in the same line
 * Area Name,Number of subs,Sub name,number of Feeders,Feeder name,number of Capbanks,op_Center_abbrev,Geo_work_area ,Equipment Id,Address ,Lat,Lon ,Capbank_size ,CapBank_type ,CTL_type,etc…
 */
public class ProgressCCImporter {

    private  String fileName;
    private  ArrayList<ProgCCArea> areaList = new ArrayList<ProgCCArea>();
    BufferedReader in = null;
    boolean testrun;
    
    public ProgressCCImporter(){
        super();
        testrun = false;
    }
        
    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isTestrun() {
        return testrun;
    }

    public void setTestrun(boolean testrun) {
        this.testrun = testrun;
    }
    
    private boolean readInData( ){
        //Comma Delimited file
        String line;
        boolean result = true;
                        
        if ( in == null )
        {
            return false;
        }
        
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

            try {
                if( !processInputText(line)){
                    System.out.println(" Error processing line: ");
                    System.out.println("---Start---");
                    System.out.println(line);
                    System.out.println("---Stop ---");
                }
            } catch (Exception e) {
                System.out.println("Error while Processing Text" );
                e.printStackTrace();
            }
        }
        
        return result;
        
    }
    
    /* This is going to be clunky :( */
    public boolean processInputText( String areaText ) throws Exception
    {
        ProgCCArea area = new ProgCCArea();
        int size = 0;
        StringTokenizer st = new StringTokenizer(areaText, ",", false);
        
        //get area name
        area.setName(st.nextToken());
        
        //get number of subs
        //loop
        size = Integer.parseInt(st.nextToken());
        for( int i = size; i > 0; i-- ){
            
            ProgCCSubstation sub = new ProgCCSubstation();
            //get name 
            sub.setName(st.nextToken());
            size = Integer.parseInt(st.nextToken());
            for( int j = size; j > 0; j--){
                ProgCCFeeder feeder = new ProgCCFeeder();
                //get name
                feeder.setName(st.nextToken());
                //get number of capbanks
                //loop
                size = Integer.parseInt(st.nextToken());
                for( int k = size; k > 0; k--){
                    //getinfo from capbank
                    ProgCCCapbank bank = new ProgCCCapbank();
                    //fill capbank
                    bank.setOpCenterAbv(st.nextToken());
                    bank.setGeoWorkArea(st.nextToken());
                    bank.setEquipmentID(st.nextToken());
                    bank.setAddress(st.nextToken());
                    bank.setLat(st.nextToken());
                    bank.setLon(st.nextToken());
                    bank.setBankSize(st.nextToken());
                    bank.setBankType(st.nextToken());
                    bank.setCtlType(st.nextToken());
                    feeder.addCapbank(bank);
                }
                sub.addFeeder(feeder);
            }
            area.addSub(sub);
        }
        areaList.add(area);
        
        return true;
    }
    
    public void insertToDatabase(){
        CBCCreationModel cpf = null;
        java.sql.Connection dbConnection = null; 

        int itemId = -1;
        //counters
        int subDisplayOrder = 1;
        int feederDisplayOrder = 1;
        int capBankDisplayOrder = 1;
        SimpleJdbcOperations jdbcOps = (SimpleJdbcOperations) YukonSpringHook.getBean("simpleJdbcTemplate");
        
        //make Eliots objects and add them into the database
        for( ProgCCArea area : areaList ){
            //This will create an area and add it to the database
            cpf = new CBCCreationImpl();
            cpf.setName(area.getName());
            cpf.setWizPaoType(PAOGroups.CAP_CONTROL_AREA);
            
            
            //get the device ID and store it for linking purposes
            itemId = CapControlFactory.createCapControlObject(cpf);

            if( itemId == -1){
                //see if the object exists and set the id up            
                SqlStatementBuilder sqlStatement = new SqlStatementBuilder();
                sqlStatement.append("select paobjectid from yukonpaobject");
                sqlStatement.append("where paoname = ?");
                
                itemId = jdbcOps.queryForInt(sqlStatement.toString(), area.getName() );
            }
            if( itemId != 0 ){
                dbConnection = PoolManager.getYukonConnection();
                area.setId( itemId );    

                //counter to set sub display order
                subDisplayOrder = 1;
                for( ProgCCSubstation sub : area.getSubs() ){
                    //creates a substation
                    cpf = new CBCCreationImpl();
                    cpf.setName(sub.getName());
                    cpf.setWizPaoType(PAOGroups.CAP_CONTROL_SUBBUS);
                    itemId = CapControlFactory.createCapControlObject(cpf);
                    if( itemId == -1){
                        //see if the object exists and set the id up
                        SqlStatementBuilder sqlStatement = new SqlStatementBuilder();
                        sqlStatement.append("select paobjectid from yukonpaobject");
                        sqlStatement.append("where paoname = ?");
                        
                        itemId = jdbcOps.queryForInt(sqlStatement.toString(), sub.getName() );
                    }
                    if( itemId != 0 )
                    {
                        sub.setId( itemId );  
                        //counter to set feeder display order
                        feederDisplayOrder = 1;
                        
                        //Link Sub to Area
                        CCSubAreaAssignment ccsa = new CCSubAreaAssignment();
                        ccsa.setDbConnection(dbConnection);
                        ccsa.setAreaID(area.getId());
                        ccsa.setSubstationBusID(sub.getId());
                        ccsa.setDisplayOrder(subDisplayOrder);
                        try {
                            ccsa.add();
                        } catch (SQLException e) {
                            //e.printStackTrace();
                            System.out.println("Exception adding " + area.getName() + " to " + sub.getName() + " CCSubAreaAssignment");
                        }
                        
                        subDisplayOrder++;
                        
                        for( ProgCCFeeder feeder : sub.getCcFeederList() ){
                            //creates a feeder
                            cpf = new CBCCreationImpl();
                            cpf.setName(feeder.getName());
                            cpf.setWizPaoType(PAOGroups.CAP_CONTROL_FEEDER);
                            
                            itemId = CapControlFactory.createCapControlObject(cpf);
                            if( itemId == -1){
                                //see if the object exists and set the id up
                                //see if the object exists and set the id up
                                SqlStatementBuilder sqlStatement = new SqlStatementBuilder();
                                sqlStatement.append("select paobjectid from yukonpaobject");
                                sqlStatement.append("where paoname = ?");
                                
                                itemId = jdbcOps.queryForInt(sqlStatement.toString(), feeder.getName() );
                            }
                            if( itemId != 0 )
                            {
                                feeder.setId( itemId );
                                //counter for cap bank display order
                                capBankDisplayOrder = 1;
                                
                                //LINK Feeder to Sub
                                CCFeederSubAssignment ccfs = new CCFeederSubAssignment(feeder.getId(), sub.getId(), feederDisplayOrder);
                                ccfs.setDbConnection(dbConnection);
                                try {
                                    ccfs.add();
                                } catch (SQLException e) {
                                    //e.printStackTrace();
                                    System.out.println("Exception adding " + sub.getName() + " to " + feeder.getName() + " CCFeederSubAssignment");
                                }
                                //increment feeder order, if there is another feeder it will use the new value
                                feederDisplayOrder++;
                                for( ProgCCCapbank bank : feeder.getCcCapBanks() ){
                                     //Creates a Capbank    
                                    cpf = new CBCCreationImpl();
                                    cpf.setName(bank.getEquipmentID());
                                    cpf.setWizPaoType(PAOGroups.CAPBANK);
                                    
                                    itemId = CapControlFactory.createCapControlObject(cpf);
                                    if( itemId == -1){
                                        //see if the object exists and set the id up
                                        //see if the object exists and set the id up
                                        SqlStatementBuilder sqlStatement = new SqlStatementBuilder();
                                        sqlStatement.append("select paobjectid from yukonpaobject");
                                        sqlStatement.append("where paoname = ?");
                                        
                                        itemId = jdbcOps.queryForInt(sqlStatement.toString(), bank.getEquipmentID() );
                                    }
                                    if( itemId != 0 )
                                    {
                                        bank.setId( itemId );
                                        
                                        //create CapBank Object, fill with data, set operational state to StandAlone
                                        CapBank cb = new CapBank();
                                        cb.setDbConnection(dbConnection);
                                        cb.setDeviceID(bank.getId());
                                        cb.setOperationalState(com.cannontech.database.data.capcontrol.CapBank.STANDALONE_OPSTATE);
                                        cb.setMapLocationID(bank.getOpCenterAbv());
                                        cb.setBankSize(Integer.parseInt(bank.getBankSize()));
                                        cb.setSwitchManufacture(bank.getCtlType());
                                        try {
                                            cb.update();
                                        } catch (SQLException e) {
                                            //e.printStackTrace();
                                            System.out.println("Exception updating CapBank " + bank.getEquipmentID() );
                                        }
                                        
                                        //create CapBank Additional Object, fill with data
                                        CapBankAdditional cbadd = new CapBankAdditional();
                                        cbadd.setDbConnection(dbConnection);
                                        cbadd.setDeviceID(bank.getId());
                                        cbadd.setMaintAreaID(Integer.parseInt(bank.getGeoWorkArea()));
                                        cbadd.setDriveDir(bank.getAddress());
                                        cbadd.setLatit(Double.parseDouble(bank.getLat()));
                                        cbadd.setLongtit(Double.parseDouble(bank.getLon()));
                                        cbadd.setCapBankConfig(bank.getBankType());
                                        try {
                                            cbadd.update();
                                        } catch (SQLException e) {
                                            //e.printStackTrace();
                                            System.out.println("Exception updating CapBankAdditional " + bank.getEquipmentID() );
                                        }
                                        
                                        //LINK Capbank and Feeder
                                        CCFeederBankList ccbl = new CCFeederBankList(feeder.getId(), bank.getId(), capBankDisplayOrder,capBankDisplayOrder, feeder.getCcCapBanks().size() - (capBankDisplayOrder-1));
                                        ccbl.setDbConnection(dbConnection);
                                        try {
                                            ccbl.add();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            System.out.println("Exception adding " + feeder.getName() + " to " + bank.getEquipmentID() + " CCFeederBankList");
                                        }
                                        
                                        capBankDisplayOrder++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void print(){
        
        for( ProgCCArea area : areaList ){            
            System.out.println("Area Name: " + area.getName());
            for( ProgCCSubstation sub : area.getSubs() ){
                System.out.println("Sub Name: " + sub.getName());
                for( ProgCCFeeder feeder : sub.getCcFeederList() ){
                    System.out.println("Feeder Name: " + feeder.getName() );
                    for( ProgCCCapbank bank : feeder.getCcCapBanks() ){
                        System.out.println( bank.getString() );
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) {
        ProgressCCImporter importer = new ProgressCCImporter(); 
        boolean ret = false;
        
        if( args.length == 0){
            System.out.println(" Missing args, please input filename. ");
            System.exit(0);
        }else{
            System.out.print( args[0]+ " " );
            importer.setFileName( args[0] );
        }
        if( args.length > 1 && args[1].equalsIgnoreCase("testrun") ){
            importer.setTestrun( true );
            System.out.print( args[1] );
        }
        System.out.print("\n");
        
        try {
            importer.setIn(new BufferedReader(new FileReader(importer.getFileName())) );
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        //read in data
        if (importer.getIn() != null)
            ret = importer.readInData(); 
        
        if( importer.isTestrun() == false ){
            if ( !ret ){
                System.out.println("Error reading in Data, fix the import file before the import can proceed.");
                System.out.println("The information successfully read in: ");
                importer.print();
            }else{
                //set up Objects and add objects
                System.out.println("The Importfile was parsed without errors");
                importer.insertToDatabase();
            }
        }else{
            if( !ret ){
                System.out.println("Error reading in Data, fix the import file before the import can proceed.");
            }else{
                System.out.println("The Datafile was read in without error.");
            }
            System.out.println("The information successfully read in: ");
            importer.print();
        }
        System.exit(0);  
    }

}
