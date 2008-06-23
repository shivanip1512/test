package com.cannontech.stars.xml.serialize;

public class StarsYukonSwitchCommand {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Enable programs of a customer account
    **/
    private StarsEnableService _starsEnableService;

    /**
     * Disable programs of a customer account
    **/
    private StarsDisableService _starsDisableService;

    private StarsConfig _starsConfig;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsYukonSwitchCommand() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsYukonSwitchCommand()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsConfig'.
     * 
     * @return the value of field 'starsConfig'.
    **/
    public StarsConfig getStarsConfig()
    {
        return this._starsConfig;
    } //-- StarsConfig getStarsConfig() 

    /**
     * Returns the value of field 'starsDisableService'. The field
     * 'starsDisableService' has the following description: Disable
     * programs of a customer account
     * 
     * @return the value of field 'starsDisableService'.
    **/
    public StarsDisableService getStarsDisableService()
    {
        return this._starsDisableService;
    } //-- StarsDisableService getStarsDisableService() 

    /**
     * Returns the value of field 'starsEnableService'. The field
     * 'starsEnableService' has the following description: Enable
     * programs of a customer account
     * 
     * @return the value of field 'starsEnableService'.
    **/
    public StarsEnableService getStarsEnableService()
    {
        return this._starsEnableService;
    } //-- StarsEnableService getStarsEnableService() 

    /**
     * Sets the value of field 'starsConfig'.
     * 
     * @param starsConfig the value of field 'starsConfig'.
    **/
    public void setStarsConfig(StarsConfig starsConfig)
    {
        this._starsConfig = starsConfig;
    } //-- void setStarsConfig(StarsConfig) 

    /**
     * Sets the value of field 'starsDisableService'. The field
     * 'starsDisableService' has the following description: Disable
     * programs of a customer account
     * 
     * @param starsDisableService the value of field
     * 'starsDisableService'.
    **/
    public void setStarsDisableService(StarsDisableService starsDisableService)
    {
        this._starsDisableService = starsDisableService;
    } //-- void setStarsDisableService(StarsDisableService) 

    /**
     * Sets the value of field 'starsEnableService'. The field
     * 'starsEnableService' has the following description: Enable
     * programs of a customer account
     * 
     * @param starsEnableService the value of field
     * 'starsEnableService'.
    **/
    public void setStarsEnableService(StarsEnableService starsEnableService)
    {
        this._starsEnableService = starsEnableService;
    } //-- void setStarsEnableService(StarsEnableService) 

}
