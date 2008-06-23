package com.cannontech.stars.xml.serialize;

public class StorageHeat {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StorageType _storageType;

    private int _peakKWCapacity;

    /**
     * keeps track of state for field: _peakKWCapacity
    **/
    private boolean _has_peakKWCapacity;

    private int _hoursToRecharge;

    /**
     * keeps track of state for field: _hoursToRecharge
    **/
    private boolean _has_hoursToRecharge;


      //----------------/
     //- Constructors -/
    //----------------/

    public StorageHeat() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StorageHeat()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteHoursToRecharge()
    {
        this._has_hoursToRecharge= false;
    } //-- void deleteHoursToRecharge() 

    /**
    **/
    public void deletePeakKWCapacity()
    {
        this._has_peakKWCapacity= false;
    } //-- void deletePeakKWCapacity() 

    /**
     * Returns the value of field 'hoursToRecharge'.
     * 
     * @return the value of field 'hoursToRecharge'.
    **/
    public int getHoursToRecharge()
    {
        return this._hoursToRecharge;
    } //-- int getHoursToRecharge() 

    /**
     * Returns the value of field 'peakKWCapacity'.
     * 
     * @return the value of field 'peakKWCapacity'.
    **/
    public int getPeakKWCapacity()
    {
        return this._peakKWCapacity;
    } //-- int getPeakKWCapacity() 

    /**
     * Returns the value of field 'storageType'.
     * 
     * @return the value of field 'storageType'.
    **/
    public StorageType getStorageType()
    {
        return this._storageType;
    } //-- StorageType getStorageType() 

    /**
    **/
    public boolean hasHoursToRecharge()
    {
        return this._has_hoursToRecharge;
    } //-- boolean hasHoursToRecharge() 

    /**
    **/
    public boolean hasPeakKWCapacity()
    {
        return this._has_peakKWCapacity;
    } //-- boolean hasPeakKWCapacity() 

    /**
     * Sets the value of field 'hoursToRecharge'.
     * 
     * @param hoursToRecharge the value of field 'hoursToRecharge'.
    **/
    public void setHoursToRecharge(int hoursToRecharge)
    {
        this._hoursToRecharge = hoursToRecharge;
        this._has_hoursToRecharge = true;
    } //-- void setHoursToRecharge(int) 

    /**
     * Sets the value of field 'peakKWCapacity'.
     * 
     * @param peakKWCapacity the value of field 'peakKWCapacity'.
    **/
    public void setPeakKWCapacity(int peakKWCapacity)
    {
        this._peakKWCapacity = peakKWCapacity;
        this._has_peakKWCapacity = true;
    } //-- void setPeakKWCapacity(int) 

    /**
     * Sets the value of field 'storageType'.
     * 
     * @param storageType the value of field 'storageType'.
    **/
    public void setStorageType(StorageType storageType)
    {
        this._storageType = storageType;
    } //-- void setStorageType(StorageType) 

}
