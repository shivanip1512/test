package com.cannontech.stars.xml.serialize;

public class StorageHeat {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StorageType storageType;

    private int peakKWCapacity;

    /**
     * keeps track of state for field: _peakKWCapacity
    **/
    private boolean has_peakKWCapacity;

    private int hoursToRecharge;

    /**
     * keeps track of state for field: _hoursToRecharge
    **/
    private boolean has_hoursToRecharge;


      //----------------/
     //- Constructors -/
    //----------------/

    public StorageHeat() {
        super();
        this.storageType = new StorageType();
    } //-- com.cannontech.stars.xml.serialize.StorageHeat()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteHoursToRecharge()
    {
        this.has_hoursToRecharge= false;
    } //-- void deleteHoursToRecharge() 

    /**
    **/
    public void deletePeakKWCapacity()
    {
        this.has_peakKWCapacity= false;
    } //-- void deletePeakKWCapacity() 

    /**
     * Returns the value of field 'hoursToRecharge'.
     * 
     * @return the value of field 'hoursToRecharge'.
    **/
    public int getHoursToRecharge()
    {
        return this.hoursToRecharge;
    } //-- int getHoursToRecharge() 

    /**
     * Returns the value of field 'peakKWCapacity'.
     * 
     * @return the value of field 'peakKWCapacity'.
    **/
    public int getPeakKWCapacity()
    {
        return this.peakKWCapacity;
    } //-- int getPeakKWCapacity() 

    /**
     * Returns the value of field 'storageType'.
     * 
     * @return the value of field 'storageType'.
    **/
    public StorageType getStorageType()
    {
        return this.storageType;
    } //-- StorageType getStorageType() 

    /**
    **/
    public boolean hasHoursToRecharge()
    {
        return this.has_hoursToRecharge;
    } //-- boolean hasHoursToRecharge() 

    /**
    **/
    public boolean hasPeakKWCapacity()
    {
        return this.has_peakKWCapacity;
    } //-- boolean hasPeakKWCapacity() 

    /**
     * Sets the value of field 'hoursToRecharge'.
     * 
     * @param hoursToRecharge the value of field 'hoursToRecharge'.
    **/
    public void setHoursToRecharge(int hoursToRecharge)
    {
        this.hoursToRecharge = hoursToRecharge;
        this.has_hoursToRecharge = true;
    } //-- void setHoursToRecharge(int) 

    /**
     * Sets the value of field 'peakKWCapacity'.
     * 
     * @param peakKWCapacity the value of field 'peakKWCapacity'.
    **/
    public void setPeakKWCapacity(int peakKWCapacity)
    {
        this.peakKWCapacity = peakKWCapacity;
        this.has_peakKWCapacity = true;
    } //-- void setPeakKWCapacity(int) 

    /**
     * Sets the value of field 'storageType'.
     * 
     * @param storageType the value of field 'storageType'.
    **/
    public void setStorageType(StorageType storageType)
    {
        this.storageType = storageType;
    } //-- void setStorageType(StorageType) 

}
