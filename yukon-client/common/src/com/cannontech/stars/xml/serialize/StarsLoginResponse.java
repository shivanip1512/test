package com.cannontech.stars.xml.serialize;

public class StarsLoginResponse {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsUser _starsUser;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLoginResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsLoginResponse()


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
