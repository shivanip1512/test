package com.cannontech.stars.database.db.integration;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

public class CRSToSAM_PTJ extends DBPersistent {

    private Integer ptjID = null; 
    private Integer premiseNumber = null;
    private String debtorNumber = null;
    private String ptjType = "";
    private Date timestamp = null;
    private String consumptionType = "";
    private Character servUtilityType = null;
    private String notes = "";
    private String streetAddress1 = "";       
    private String streetAddress2 = "";
    private String cityName = "";
    private String stateCode = "";       
    private String zipCode = "";     
    private String firstName = "";       
    private String lastName = "";        
    private String homePhone = "";       
    private String workPhone = "";       
    private String crsContactPhone = "";
    private String crsLoggedUser = "";
    private Character presenceRequired = null;
    private Character airConditioner = null;
    private Character waterHeater = null;
    private String serviceNumber = "";
    private String meterNumber = "";
    private String siteNumber = "";

    private List<CRSToSAM_PTJAdditionalMeters> additionalMeters;
    public static final String CONSTRAINT_COLUMNS[] = { "PTJID" };

    public static final String SETTER_COLUMNS[] = { "PTJID", "PremiseNumber", "DebtorNumber", "PTJType", "Timestamp",
    												"ConsumptionType", "ServUtilityType", "Notes", "StreetAddress1", "StreetAddress2",
    												"CityName", "StateCode", "ZipCode", "FirstName", "LastName", "HomePhone", "WorkPhone", 
    												"CRSContactPhone", "CRSLoggedUser", "PresenceRequired", "AirConditioner",
    												"WaterHeater", "ServiceNumber", "MeterNumber", "SiteNumber"};

    public static final String TABLE_NAME = "CRSToSAM_PTJ";


public CRSToSAM_PTJ() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getPTJID(), getPremiseNumber(), getDebtorNumber(), getPTJType(), getTimestamp(), getConsumptionType(), 
    		getServUtilityType(), getNotes(), getStreetAddress1(), getStreetAddress2(), getCityName(), getStateCode(), getZipCode(), getFirstName(), 
    		getLastName(),getHomePhone(), getWorkPhone(), getCRSContactPhone(), getCRSLoggedUser(), getPresenceRequired(), 
    		getAirConditioner(), getWaterHeater(), getServiceNumber(), getMeterNumber(), getSiteNumber()};

    add( TABLE_NAME, setValues );
}


public void setDbConnection(Connection newValue) {
	super.setDbConnection(newValue);
	for (int i = 0; i < getAdditionalMeters().size(); i++)
	{
		CRSToSAM_PTJAdditionalMeters addtlMeter = getAdditionalMeters().get(i);
		addtlMeter.setDbConnection(newValue);
	}

}
public void delete() throws java.sql.SQLException 
{
	delete( CRSToSAM_PTJAdditionalMeters.TABLE_NAME, "PTJID", getPTJID());
	
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
        setStreetAddress1( (String) results[7] );  
        setStreetAddress2( (String) results[8] );
        setCityName( (String) results[9] );      
        setStateCode( (String) results[10] );      
        setZipCode( (String) results[11] );      
        setFirstName( (String) results[12] );       
        setLastName( (String) results[13] );        
        setHomePhone( (String) results[14] );       
        setWorkPhone( (String) results[15] );
        setCRSContactPhone( (String) results[16] );
        setCRSLoggedUser( (String) results[17] );
        setPresenceRequired( (Character) results[18] );
        setAirConditioner( (Character) results[19] );
        setWaterHeater( (Character) results[20] );
        setServiceNumber( (String) results[21] );
        setMeterNumber( (String) results[22] );
        setSiteNumber( (String) results[23] );
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}


public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getPTJID(), getPremiseNumber(), getDebtorNumber(), getPTJType(), getTimestamp(), getConsumptionType(), 
    		getServUtilityType(), getNotes(), getStreetAddress1(), getStreetAddress2(), getCityName(), getStateCode(), getZipCode(), getFirstName(), 
    		getLastName(),getHomePhone(), getWorkPhone(), getCRSContactPhone(), getCRSLoggedUser(), getPresenceRequired(), 
    		getAirConditioner(), getWaterHeater(), getServiceNumber(), getMeterNumber(), getSiteNumber()}; 
    		
    		Object constraintValues[] = { getPTJID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public static List<CRSToSAM_PTJ> getAllCurrentPTJEntries()
{
    ArrayList<CRSToSAM_PTJ> changes = new ArrayList<CRSToSAM_PTJ>();
    
    //Join the additional meter number table to load all additional meter numbers too.
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
        	Map<Integer, List<CRSToSAM_PTJAdditionalMeters>> ptjToAddtlMetersMap = 
        		CRSToSAM_PTJAdditionalMeters.retrieveAllCurrentPTJAdditionalMeterEntriesMap();
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
            	CRSToSAM_PTJ currentEntry = new CRSToSAM_PTJ();
            	if (stmt.getRow(i)[0] != null)
            		currentEntry.setPTJID( new Integer(stmt.getRow(i)[0].toString()));
            	if (stmt.getRow(i)[1] != null)
            		currentEntry.setPremiseNumber( new Integer(stmt.getRow(i)[1].toString()));
            	if (stmt.getRow(i)[2] != null)
            		currentEntry.setDebtorNumber( stmt.getRow(i)[2].toString());
            	if (stmt.getRow(i)[3] != null)
            		currentEntry.setPTJType( stmt.getRow(i)[3].toString());
            	if (stmt.getRow(i)[4] != null)
            		currentEntry.setTimestamp(new Date(((java.sql.Timestamp)stmt.getRow(i)[4]).getTime()));
            	if (stmt.getRow(i)[5] != null)
            		currentEntry.setConsumptionType( stmt.getRow(i)[5].toString());
            	if (stmt.getRow(i)[6] != null)
            		currentEntry.setServUtilityType( new Character(stmt.getRow(i)[6].toString().charAt(0)) );
            	if (stmt.getRow(i)[7] != null)
            		currentEntry.setNotes( stmt.getRow(i)[7].toString());
            	if (stmt.getRow(i)[8] != null)
            		currentEntry.setStreetAddress1( stmt.getRow(i)[8].toString());
            	if (stmt.getRow(i)[9] != null)
            		currentEntry.setStreetAddress2( stmt.getRow(i)[9].toString());
            	if (stmt.getRow(i)[10] != null)
            		currentEntry.setCityName( stmt.getRow(i)[10].toString());
            	if (stmt.getRow(i)[11] != null)
            		currentEntry.setStateCode( stmt.getRow(i)[11].toString());
            	if (stmt.getRow(i)[12] != null)
            		currentEntry.setZipCode( stmt.getRow(i)[12].toString());
            	if (stmt.getRow(i)[13] != null)
            		currentEntry.setFirstName( stmt.getRow(i)[13].toString());
            	if (stmt.getRow(i)[14] != null)
            		currentEntry.setLastName( stmt.getRow(i)[14].toString());
            	if (stmt.getRow(i)[15] != null)
            		currentEntry.setHomePhone( stmt.getRow(i)[15].toString());
            	if (stmt.getRow(i)[16] != null)
            		currentEntry.setWorkPhone( stmt.getRow(i)[16].toString());
            	if (stmt.getRow(i)[17] != null)
            		currentEntry.setCRSContactPhone( stmt.getRow(i)[17].toString());
            	if (stmt.getRow(i)[18] != null)
            		currentEntry.setCRSLoggedUser( stmt.getRow(i)[18].toString());
            	if (stmt.getRow(i)[19] != null)
            		currentEntry.setPresenceRequired( new Character(stmt.getRow(i)[19].toString().charAt(0)) );
            	if (stmt.getRow(i)[20] != null)
            		currentEntry.setAirConditioner( new Character(stmt.getRow(i)[20].toString().charAt(0)) );
            	if (stmt.getRow(i)[21] != null)
            		currentEntry.setWaterHeater( new Character(stmt.getRow(i)[21].toString().charAt(0)) );
            	if (stmt.getRow(i)[22] != null)
            		currentEntry.setServiceNumber( stmt.getRow(i)[22].toString());
            	if (stmt.getRow(i)[23] != null)
            		currentEntry.setMeterNumber( stmt.getRow(i)[23].toString());
            	if (stmt.getRow(i)[24] != null)
            	    currentEntry.setSiteNumber( stmt.getRow(i)[24].toString());
                
                List<CRSToSAM_PTJAdditionalMeters> addtlMeters = ptjToAddtlMetersMap.get(currentEntry.getPTJID());
                if( addtlMeters != null)	//found an ArrayList of CRSToSam_PTJAdditionalMeters
                	currentEntry.setAdditionalMeters(addtlMeters);
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

public String getDebtorNumber() {
	return debtorNumber;
}

public void setDebtorNumber(String debtorNumber) {
	this.debtorNumber = debtorNumber;
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

public String getSiteNumber() {
    return siteNumber;
}

public void setSiteNumber(String siteNumber) {
    this.siteNumber = siteNumber;
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

public String getStreetAddress1() {
	return streetAddress1;
}

public void setStreetAddress1(String streetAddress) {
	this.streetAddress1 = streetAddress;
}

public String getStreetAddress2() {
	return streetAddress2;
}

public void setStreetAddress2(String streetAddress) {
	this.streetAddress2 = streetAddress;
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

public List<CRSToSAM_PTJAdditionalMeters> getAdditionalMeters() {
	if( additionalMeters == null)
		additionalMeters = new ArrayList<CRSToSAM_PTJAdditionalMeters>();
	return additionalMeters;
}

public void setAdditionalMeters(List<CRSToSAM_PTJAdditionalMeters> additionalMeters) {
	this.additionalMeters = additionalMeters;
}
}
