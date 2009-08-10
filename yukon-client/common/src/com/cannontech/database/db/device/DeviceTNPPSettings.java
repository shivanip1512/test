package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */
public class DeviceTNPPSettings extends com.cannontech.database.db.DBPersistent {

    private Integer deviceId = null;
    private Integer inertia = null;
    private Integer destinationAddress = null;
    private Integer originAddress = null;
    private Character identifierFormat = null;
    private Character protocol = null;
    private Character dataFormat = null;
    private Character channel = null;
    private Character zone = null;
    private Character functionCode = null;
    private String pagerId = null;
    
    /* 64 is 01000000 in binary. */ 
    private static int binaryStartOffset = 64;
    
    private String tableName = "DeviceTNPPSettings";
    
    public DeviceTNPPSettings() {
        super();
    }
    
    /**
     * 
     * @param deviceId
     */
    public DeviceTNPPSettings(Integer deviceId) {
        super();
        this.deviceId = deviceId;
    }
    
    /**
     * 
     * @param deviceId
     * @param inertia
     * @param destinationAddress
     * @param originAddress
     * @param identifierFormat
     * @param protocol
     * @param dataFormat
     * @param channel
     * @param zone
     * @param functionCode
     * @param pagerId
     */
    public DeviceTNPPSettings(Integer deviceId, Integer inertia,
            Integer destinationAddress, Integer originAddress,
            Character identifierFormat, Character protocol, Character dataFormat,
            Character channel, Character zone, Character functionCode,
            String pagerId) {
        super();
        this.deviceId = deviceId;
        this.inertia = inertia;
        this.destinationAddress = destinationAddress;
        this.originAddress = originAddress;
        this.identifierFormat = identifierFormat;
        this.protocol = protocol;
        this.dataFormat = dataFormat;
        this.channel = channel;
        this.zone = zone;
        this.functionCode = functionCode;
        this.pagerId = pagerId;
    }
    
    /**
     * Adds the object to the database.
     */
    public void add() throws java.sql.SQLException {
        Object[] addValues = { getDeviceId(), getInertia(), getDestinationAddress(),
                               getOriginAddress(), getIdentifierFormat(), getProtocol(),
                               getDataFormat(), getChannel(), getZone(),
                               getFunctionCode(), getPagerId() };
    
        add( this.tableName, addValues );
    }
    
    /**
     * Deletes the object from the database.
     */
    public void delete() throws java.sql.SQLException {
    
        delete( this.tableName, "deviceId", getDeviceId() );
        
    }
    
    /**
     * Retrieves the object from the database.
     */
    public void retrieve() throws java.sql.SQLException {
    
        String selectColumns[] = { "Inertia", "DestinationAddress","OriginAddress",
                                   "IdentifierFormat", "Protocol", "DataFormat",
                                   "Channel", "Zone", "FunctionCode", "PagerId" };
        
        String constraintColumns[] = { "deviceId" };
        Object constraintValues[] = { getDeviceId() };
    
        Object results[] = retrieve( selectColumns, this.tableName, constraintColumns, constraintValues );
    
        if( results.length == selectColumns.length )
        {
            setInertia( (Integer) results[0] );
            setDestinationAddress( (Integer) results[1] );
            setOriginAddress( (Integer) results[2] );
            setIdentifierFormat( ((String) results[3]).charAt(0) );
            setProtocol( ((String) results[4]).charAt(0) );
            setDataFormat( ((String) results[5]).charAt(0) );
            setChannel( ((String) results[6]).charAt(0) );
            setZone( ((String) results[7]).charAt(0) );
            setFunctionCode( ((String) results[8]).charAt(0) );
            setPagerId( (String) results[9] );
        }
    }

    /**
     * Updates the object in the database.
     */
    public void update() throws java.sql.SQLException {
    
        String setColumns[] = { "Inertia", "DestinationAddress","OriginAddress",
                                "IdentifierFormat", "Protocol", "DataFormat",
                                "Channel", "Zone", "FunctionCode", "PagerId" };
        
        Object setValues[]= { getInertia(), getDestinationAddress(),
                              getOriginAddress(), getIdentifierFormat(), getProtocol(),
                              getDataFormat(), getChannel(), getZone(),
                              getFunctionCode(), getPagerId() };
    
        String constraintColumns[] = { "DeviceID" };
        Object constraintValues[] = { getDeviceId() };
    
        update( this.tableName, setColumns, setValues, constraintColumns, constraintValues );
    }
    
    /* Setters and Getters */
    public Integer getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(Integer deviceID) {
        this.deviceId = deviceID;
    }

    public Integer getInertia() {
        return inertia;
    }
    public void setInertia(Integer inertia) {
        this.inertia = inertia;
    }

    public Integer getDestinationAddress() {
        return destinationAddress;
    }
    public void setDestinationAddress(Integer destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Integer getOriginAddress() {
        return originAddress;
    }
    public void setOriginAddress(Integer originAddress) {
        this.originAddress = originAddress;
    }

    public Character getIdentifierFormat() {
        return identifierFormat;
    }
    public void setIdentifierFormat(Character identifierFormat) {
        this.identifierFormat = identifierFormat;
    }

    public Character getProtocol() {
        return protocol;
    }
    public void setProtocol(Character protocol) {
        this.protocol = protocol;
    }

    public Character getDataFormat() {
        return dataFormat;
    }
    public void setDataFormat(Character dataFormat) {
        this.dataFormat = dataFormat;
    }

    public Character getChannel() {
        return channel;
    }
    public void setChannel(Character channel) {
        this.channel = channel;
    }
    
    public Character getZone() {
        return zone;
    }
    public void setZone(Character zone) {
        this.zone = zone;
    }

    public Character getFunctionCode() {
        return functionCode;
    }
    public void setFunctionCode(Character functionCode) {
        this.functionCode = functionCode;
    }

    public String getPagerId() {
        return pagerId;
    }
    public void setPagerId(String pagerId) {
        this.pagerId = pagerId;
    }

    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    /**
     * Handles converting an int to a char, which includes adding the needed mask.  This method
     * is needed to handle the channel, function, and zone values that are held in the database
     * as a character
     * 
     * @param suppliedOffset
     * @return
     */
    public static char addMaskAndConvertToChar(int suppliedOffset){
        return ((char)(binaryStartOffset+suppliedOffset));
    }
    
    /**
     * Handles converting a char to an int, which includes removing the needed mask.  This method
     * is needed to handle the channel, function, and zone values that are held in the database
     * as a character
     *  
     * @param suppliedChar
     * @return
     */
    public static int convertToIntAndRemoveMask(char suppliedChar){
        return (suppliedChar- binaryStartOffset);
    }
}