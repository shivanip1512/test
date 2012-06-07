package com.cannontech.stars.xml.serialize;

public class StarsGetCustomerAccountResponse {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsCustAccountInformation _starsCustAccountInformation;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetCustomerAccountResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsGetCustomerAccountResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsCustAccountInformation'.
     * 
     * @return the value of field 'starsCustAccountInformation'.
    **/
    public StarsCustAccountInformation getStarsCustAccountInformation()
    {
        return this._starsCustAccountInformation;
    } //-- StarsCustAccountInformation getStarsCustAccountInformation() 

    /**
     * Sets the value of field 'starsCustAccountInformation'.
     * 
     * @param starsCustAccountInformation the value of field
     * 'starsCustAccountInformation'.
    **/
    public void setStarsCustAccountInformation(StarsCustAccountInformation starsCustAccountInformation)
    {
        this._starsCustAccountInformation = starsCustAccountInformation;
    } //-- void setStarsCustAccountInformation(StarsCustAccountInformation) 

}
