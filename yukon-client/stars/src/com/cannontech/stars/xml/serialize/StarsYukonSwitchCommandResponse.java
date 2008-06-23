package com.cannontech.stars.xml.serialize;

public class StarsYukonSwitchCommandResponse {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsInventory _starsInventory;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsYukonSwitchCommandResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsYukonSwitchCommandResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsInventory'.
     * 
     * @return the value of field 'starsInventory'.
    **/
    public StarsInventory getStarsInventory()
    {
        return this._starsInventory;
    } //-- StarsInventory getStarsInventory() 

    /**
     * Sets the value of field 'starsInventory'.
     * 
     * @param starsInventory the value of field 'starsInventory'.
    **/
    public void setStarsInventory(StarsInventory starsInventory)
    {
        this._starsInventory = starsInventory;
    } //-- void setStarsInventory(StarsInventory) 

}
