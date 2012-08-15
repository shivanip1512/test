package com.cannontech.stars.xml.serialize;

import com.cannontech.database.data.lite.LiteYukonUser;

public class StarsLoginResponse {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private LiteYukonUser _starsUser;


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
