package com.cannontech.stars.xml.serialize;

public class StarsUpdateLoginResponse {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsUser _starsUser;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateLoginResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateLoginResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsUser'.
     * 
     * @return the value of field 'starsUser'.
    **/
    public StarsUser getStarsUser()
    {
        return this._starsUser;
    } //-- StarsUser getStarsUser() 

    /**
     * Sets the value of field 'starsUser'.
     * 
     * @param starsUser the value of field 'starsUser'.
    **/
    public void setStarsUser(StarsUser starsUser)
    {
        this._starsUser = starsUser;
    } //-- void setStarsUser(StarsUser) 

}
