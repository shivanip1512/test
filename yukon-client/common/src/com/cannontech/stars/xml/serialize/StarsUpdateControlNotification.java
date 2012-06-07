package com.cannontech.stars.xml.serialize;

public class StarsUpdateControlNotification {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private ContactNotification _contactNotification;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateControlNotification() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateControlNotification()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'contactNotification'.
     * 
     * @return the value of field 'contactNotification'.
    **/
    public ContactNotification getContactNotification()
    {
        return this._contactNotification;
    } //-- ContactNotification getContactNotification() 

    /**
     * Sets the value of field 'contactNotification'.
     * 
     * @param contactNotification the value of field
     * 'contactNotification'.
    **/
    public void setContactNotification(ContactNotification contactNotification)
    {
        this._contactNotification = contactNotification;
    } //-- void setContactNotification(ContactNotification) 

}
