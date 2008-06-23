package com.cannontech.stars.xml.serialize;

public class StarsThermostatManualEvent extends com.cannontech.stars.xml.serialize.StarsLMCustomerEvent {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private ThermostatManualOption _thermostatManualOption;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermostatManualEvent() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatManualEvent()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'thermostatManualOption'.
     * 
     * @return the value of field 'thermostatManualOption'.
    **/
    public ThermostatManualOption getThermostatManualOption()
    {
        return this._thermostatManualOption;
    } //-- ThermostatManualOption getThermostatManualOption() 

    /**
     * Sets the value of field 'thermostatManualOption'.
     * 
     * @param thermostatManualOption the value of field
     * 'thermostatManualOption'.
    **/
    public void setThermostatManualOption(ThermostatManualOption thermostatManualOption)
    {
        this._thermostatManualOption = thermostatManualOption;
    } //-- void setThermostatManualOption(ThermostatManualOption) 

}
