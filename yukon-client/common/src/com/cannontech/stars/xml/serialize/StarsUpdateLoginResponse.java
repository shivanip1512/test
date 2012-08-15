package com.cannontech.stars.xml.serialize;

import com.cannontech.database.data.lite.LiteYukonUser;

public class StarsUpdateLoginResponse {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private LiteYukonUser _starsUser;


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
    public LiteYukonUser getStarsUser()
    {
        return this._starsUser;
    } //-- StarsUser getStarsUser() 

    /**
     * Sets the value of field 'starsUser'.
     * 
     * @param starsUser the value of field 'starsUser'.
    **/
    public void setStarsUser(LiteYukonUser starsUser)
    {
        this._starsUser = starsUser;
    } //-- void setStarsUser(StarsUser) 

}
