package com.cannontech.database.db.stars.integration;

import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import java.lang.Integer;
import java.lang.String;
import java.util.Date;
import java.util.ArrayList;
import com.cannontech.common.util.CtiUtilities;

public class FailureCRSToSAM_PTJ extends DBPersistent {

	private Integer ptjID; 
    private Integer premiseNumber;
    private String debtorNumber;
    private String ptjType;
    private Date timestamp;
    private String consumptionType;
    private Character servUtilityType;
    private String notes;       
    private String streetAddress;       
    private String cityName;       
    private String stateCode;       
    private String zipCode;     
    private String firstName;       
    private String lastName;        
    private String homePhone;       
    private String workPhone;       
    private String crsContactPhone;
    private String crsLoggedUser;
    private Character presenceRequired;
    private Character airConditioner;
    private Character waterHeater;
    private String serviceNumber;
    private String meterNumber;
    private String errorMsg;
    private Date datetime; 

    public static final String CONSTRAINT_COLUMNS[] = { "PTJID" };

    public static final String SETTER_COLUMNS[] = { "PTJID", "PremiseNumber", "DebtorNumber", "PTJType", "Timestamp",
    												"ConsumptionType", "ServUtilityType", "Notes", "StreetAddress", "CityName",
    												"StateCode", "ZipCode", "FirstName", "LastName", "HomePhone", "WorkPhone", 
    												"CRSContactPhone", "CRSLoggedUser", "PresenceRequired", "AirConditioner",
    												"WaterHeater", "ServiceNumber", "MeterNumber", "ErrorMsg", "DateTime"};

    public static final String TABLE_NAME = "FailureCRSToSAM_PTJ";



public FailureCRSToSAM_PTJ() {
    super();
}
public FailureCRSToSAM_PTJ(CRSToSAM_PTJ crsToSam_ptj) {
    this();
    setPTJID(crsToSam_ptj.getPTJID());
    setPremiseNumber( crsToSam_ptj.getPremiseNumber());
    setDebtorNumber( crsToSam_ptj.getDebtorNumber());
    setPTJType( crsToSam_ptj.getPTJType());
    setTimestamp( crsToSam_ptj.getTimestamp());
    setConsumptionType( crsToSam_ptj.getConsumptionType());
    setServUtilityType( crsToSam_ptj.getServUtilityType());
    setNotes( crsToSam_ptj.getNotes());
    setStreetAddress( crsToSam_ptj.getStreetAddress() );
    setCityName( crsToSam_ptj.getCityName());
    setStateCode( crsToSam_ptj.getStateCode() );
    setZipCode( crsToSam_ptj.getZipCode() );
    setFirstName( crsToSam_ptj.getFirstName() );
    setLastName( crsToSam_ptj.getLastName());
    setHomePhone( crsToSam_ptj.getHomePhone());
    setWorkPhone( crsToSam_ptj.getWorkPhone());
    setCRSContactPhone( crsToSam_ptj.getCRSContactPhone() );
    setCRSLoggedUser( crsToSam_ptj.getCRSLoggedUser() );
    setPresenceRequired( crsToSam_ptj.getPresenceRequired());
    setAirConditioner( crsToSam_ptj.getAirConditioner());
    setWaterHeater( crsToSam_ptj.getWaterHeater() );
    setServiceNumber( crsToSam_ptj.getServiceNumber());
    setMeterNumber( crsToSam_ptj.getMeterNumber() );
}

public void add() throws java.sql.SQLException 
{
	Object setValues[] = { getPTJID(), getPremiseNumber(), getDebtorNumber(), getPTJType(), getTimestamp(), getConsumptionType(), 
    		getServUtilityType(), getNotes(), getStreetAddress(), getCityName(), getStateCode(), getZipCode(), getFirstName(), 
    		getLastName(),getHomePhone(), getWorkPhone(), getCRSContactPhone(), getCRSLoggedUser(), getPresenceRequired(), 
    		getAirConditioner(), getWaterHeater(), getServiceNumber(), getMeterNumber(), getErrorMsg(), getDatetime()};

    add( TABLE_NAME, setValues );
}

public void delete() throws java.sql.SQLException 
{
    Object constraintValues[] = { getPTJID() };

    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}

public void retrieve() throws java.sql.SQLException 
{
    Object constraintValues[] = { getPTJID() };

    Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

    if( results.length == SETTER_COLUMNS.length )
    {
        setPremiseNumber( (Integer) results[0] );
        setDebtorNumber( (String) results[1] );
        setPTJType( (String) results[2] );
        setTimestamp( (Date) results[3]);
        setConsumptionType( (String) results[4] );
        setServUtilityType( (Character) results[5] );
        setNotes( (String) results[6] );
        setStreetAddress( (String) results[7] );  
        setCityName( (String) results[8] );      
        setStateCode( (String) results[9] );      
        setZipCode( (String) results[10] );      
        setFirstName( (String) results[11] );       
        setLastName( (String) results[12] );        
        setHomePhone( (String) results[13] );       
        setWorkPhone( (String) results[14] );
        setCRSContactPhone( (String) results[15] );
        setCRSLoggedUser( (String) results[16] );
        setPresenceRequired( (Character) results[17] );
        setAirConditioner( (Character) results[18] );
        setWaterHeater( (Character) results[19] );
        setServiceNumber( (String) results[20] );
        setMeterNumber( (String) results[21] );
        setErrorMsg( (String) results[23] );
        setDatetime( (Date) results[24] );            
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getPTJID(), getPremiseNumber(), getDebtorNumber(), getPTJType(), getTimestamp(), getConsumptionType(), 
    		getServUtilityType(), getNotes(), getStreetAddress(), getCityName(), getStateCode(), getZipCode(), getFirstName(), 
    		getLastName(),getHomePhone(), getWorkPhone(), getCRSContactPhone(), getCRSLoggedUser(), getPresenceRequired(), 
    		getAirConditioner(), getWaterHeater(), getServiceNumber(), getMeterNumber(), getErrorMsg(), getDatetime()};
    
    Object constraintValues[] = { getPTJID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public static ArrayList getAllCurrentFailurePTJEntries()
{
    ArrayList changes = new ArrayList();
    
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                FailureCRSToSAM_PTJ currentEntry = new FailureCRSToSAM_PTJ();
                currentEntry.setPTJID( new Integer(stmt.getRow(i)[0].toString()));
                currentEntry.setPremiseNumber( new Integer(stmt.getRow(i)[1].toString()));
                currentEntry.setDebtorNumber( stmt.getRow(i)[2].toString());
                currentEntry.setPTJType( stmt.getRow(i)[3].toString());
                currentEntry.setTimestamp(new Date(((java.sql.Timestamp)stmt.getRow(i)[4]).getTime()));
                currentEntry.setConsumptionType( stmt.getRow(i)[5].toString());
                currentEntry.setServUtilityType( new Character(stmt.getRow(i)[6].toString().charAt(0)) );
                currentEntry.setNotes( stmt.getRow(i)[7].toString());
                currentEntry.setStreetAddress( stmt.getRow(i)[8].toString());  
                currentEntry.setCityName( stmt.getRow(i)[9].toString());      
                currentEntry.setStateCode( stmt.getRow(i)[10].toString());
                currentEntry.setZipCode( stmt.getRow(i)[11].toString());
                currentEntry.setFirstName( stmt.getRow(i)[12].toString());  
                currentEntry.setLastName( stmt.getRow(i)[13].toString());
                currentEntry.setHomePhone( stmt.getRow(i)[14].toString());
                currentEntry.setWorkPhone( stmt.getRow(i)[15].toString());
                currentEntry.setCRSContactPhone( stmt.getRow(i)[16].toString());
                currentEntry.setCRSLoggedUser( stmt.getRow(i)[17].toString());
                currentEntry.setPresenceRequired( new Character(stmt.getRow(i)[18].toString().charAt(0)) );
                currentEntry.setAirConditioner( new Character(stmt.getRow(i)[19].toString().charAt(0)) );
                currentEntry.setWaterHeater( new Character(stmt.getRow(i)[20].toString().charAt(0)) );
                currentEntry.setServiceNumber( stmt.getRow(i)[21].toString());
                currentEntry.setMeterNumber( stmt.getRow(i)[22].toString());
                currentEntry.setErrorMsg(stmt.getRow(i)[24].toString());
                currentEntry.setDatetime(new Date(((java.sql.Timestamp)stmt.getRow(i)[25]).getTime()));
                
                changes.add(currentEntry);
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return changes;
}

public Character getAirConditioner() {
	return airConditioner;
}

public void setAirConditioner(Character airConditioner) {
	this.airConditioner = airConditioner;
}

public String getCityName() {
	return cityName;
}

public void setCityName(String cityName) {
	this.cityName = cityName;
}

public String getConsumptionType() {
	return consumptionType;
}

public void setConsumptionType(String consumptionType) {
	this.consumptionType = consumptionType;
}

public String getCRSContactPhone() {
	return crsContactPhone;
}

public void setCRSContactPhone(String crsContactPhone) {
	this.crsContactPhone = crsContactPhone;
}

public String getCRSLoggedUser() {
	return crsLoggedUser;
}

public void setCRSLoggedUser(String crsLoggedUser) {
	this.crsLoggedUser = crsLoggedUser;
}

public Date getDatetime() {
	return datetime;
}

public void setDatetime(Date datetime) {
	this.datetime = datetime;
}

public String getDebtorNumber() {
	return debtorNumber;
}

public void setDebtorNumber(String debtorNumber) {
	this.debtorNumber = debtorNumber;
}

public String getErrorMsg() {
	return errorMsg;
}

public void setErrorMsg(String errorMsg) {
	this.errorMsg = errorMsg;
}

public String getFirstName() {
	return firstName;
}

public void setFirstName(String firstName) {
	this.firstName = firstName;
}

public String getHomePhone() {
	return homePhone;
}

public void setHomePhone(String homePhone) {
	this.homePhone = homePhone;
}

public String getLastName() {
	return lastName;
}

public void setLastName(String lastName) {
	this.lastName = lastName;
}

public String getMeterNumber() {
	return meterNumber;
}

public void setMeterNumber(String meterNumber) {
	this.meterNumber = meterNumber;
}

public String getNotes() {
	return notes;
}

public void setNotes(String notes) {
	this.notes = notes;
}

public Integer getPremiseNumber() {
	return premiseNumber;
}

public void setPremiseNumber(Integer premiseNumber) {
	this.premiseNumber = premiseNumber;
}

public Character getPresenceRequired() {
	return presenceRequired;
}

public void setPresenceRequired(Character presenceRequired) {
	this.presenceRequired = presenceRequired;
}

public Integer getPTJID() {
	return ptjID;
}

public void setPTJID(Integer ptjID) {
	this.ptjID = ptjID;
}

public String getPTJType() {
	return ptjType;
}

public void setPTJType(String ptjType) {
	this.ptjType = ptjType;
}

public String getServiceNumber() {
	return serviceNumber;
}

public void setServiceNumber(String serviceNumber) {
	this.serviceNumber = serviceNumber;
}

public Character getServUtilityType() {
	return servUtilityType;
}

public void setServUtilityType(Character servUtilityType) {
	this.servUtilityType = servUtilityType;
}

public String getStateCode() {
	return stateCode;
}

public void setStateCode(String stateCode) {
	this.stateCode = stateCode;
}

public String getStreetAddress() {
	return streetAddress;
}

public void setStreetAddress(String streetAddress) {
	this.streetAddress = streetAddress;
}

public Date getTimestamp() {
	return timestamp;
}

public void setTimestamp(Date timestamp) {
	this.timestamp = timestamp;
}

public Character getWaterHeater() {
	return waterHeater;
}

public void setWaterHeater(Character waterHeater) {
	this.waterHeater = waterHeater;
}

public String getWorkPhone() {
	return workPhone;
}

public void setWorkPhone(String workPhone) {
	this.workPhone = workPhone;
}

public String getZipCode() {
	return zipCode;
}

public void setZipCode(String zipCode) {
	this.zipCode = zipCode;
}
}
