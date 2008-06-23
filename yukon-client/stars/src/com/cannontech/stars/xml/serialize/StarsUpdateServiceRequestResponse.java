package com.cannontech.stars.xml.serialize;

public class StarsUpdateServiceRequestResponse {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsServiceRequest _starsServiceRequest;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateServiceRequestResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateServiceRequestResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsServiceRequest'.
     * 
     * @return the value of field 'starsServiceRequest'.
    **/
    public StarsServiceRequest getStarsServiceRequest()
    {
        return this._starsServiceRequest;
    } //-- StarsServiceRequest getStarsServiceRequest() 

    /**
     * Sets the value of field 'starsServiceRequest'.
     * 
     * @param starsServiceRequest the value of field
     * 'starsServiceRequest'.
    **/
    public void setStarsServiceRequest(StarsServiceRequest starsServiceRequest)
    {
        this._starsServiceRequest = starsServiceRequest;
    } //-- void setStarsServiceRequest(StarsServiceRequest) 

}
