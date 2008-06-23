package com.cannontech.stars.xml.serialize;

public abstract class StarsThermoManOpt {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _temperature;

    /**
     * keeps track of state for field: _temperature
    **/
    private boolean _has_temperature;

    private boolean _hold;

    /**
     * keeps track of state for field: _hold
    **/
    private boolean _has_hold;

    private com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings _mode;

    private com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings _fan;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermoManOpt() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsThermoManOpt()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'fan'.
     * 
     * @return the value of field 'fan'.
    **/
    public com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings getFan()
    {
        return this._fan;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings getFan() 

    /**
     * Returns the value of field 'hold'.
     * 
     * @return the value of field 'hold'.
    **/
    public boolean getHold()
    {
        return this._hold;
    } //-- boolean getHold() 

    /**
     * Returns the value of field 'mode'.
     * 
     * @return the value of field 'mode'.
    **/
    public com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings getMode()
    {
        return this._mode;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings getMode() 

    /**
     * Returns the value of field 'temperature'.
     * 
     * @return the value of field 'temperature'.
    **/
    public int getTemperature()
    {
        return this._temperature;
    } //-- int getTemperature() 

    /**
    **/
    public boolean hasHold()
    {
        return this._has_hold;
    } //-- boolean hasHold() 

    /**
    **/
    public boolean hasTemperature()
    {
        return this._has_temperature;
    } //-- boolean hasTemperature() 

    /**
     * Sets the value of field 'fan'.
     * 
     * @param fan the value of field 'fan'.
    **/
    public void setFan(com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings fan)
    {
        this._fan = fan;
    } //-- void setFan(com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings) 

    /**
     * Sets the value of field 'hold'.
     * 
     * @param hold the value of field 'hold'.
    **/
    public void setHold(boolean hold)
    {
        this._hold = hold;
        this._has_hold = true;
    } //-- void setHold(boolean) 

    /**
     * Sets the value of field 'mode'.
     * 
     * @param mode the value of field 'mode'.
    **/
    public void setMode(com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings mode)
    {
        this._mode = mode;
    } //-- void setMode(com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings) 

    /**
     * Sets the value of field 'temperature'.
     * 
     * @param temperature the value of field 'temperature'.
    **/
    public void setTemperature(int temperature)
    {
        this._temperature = temperature;
        this._has_temperature = true;
    } //-- void setTemperature(int) 

}
