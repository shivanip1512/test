package com.cannontech.stars.xml.serialize;

public class StarsNewCustomerAccount {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsCustomerAccount _starsCustomerAccount;

    private StarsSULMPrograms _starsSULMPrograms;

    private StarsUpdateLogin _starsUpdateLogin;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsNewCustomerAccount() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsNewCustomerAccount()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsCustomerAccount'.
     * 
     * @return the value of field 'starsCustomerAccount'.
    **/
    public StarsCustomerAccount getStarsCustomerAccount()
    {
        return this._starsCustomerAccount;
    } //-- StarsCustomerAccount getStarsCustomerAccount() 

    /**
     * Returns the value of field 'starsSULMPrograms'.
     * 
     * @return the value of field 'starsSULMPrograms'.
    **/
    public StarsSULMPrograms getStarsSULMPrograms()
    {
        return this._starsSULMPrograms;
    } //-- StarsSULMPrograms getStarsSULMPrograms() 

    /**
     * Returns the value of field 'starsUpdateLogin'.
     * 
     * @return the value of field 'starsUpdateLogin'.
    **/
    public StarsUpdateLogin getStarsUpdateLogin()
    {
        return this._starsUpdateLogin;
    } //-- StarsUpdateLogin getStarsUpdateLogin() 

    /**
     * Sets the value of field 'starsCustomerAccount'.
     * 
     * @param starsCustomerAccount the value of field
     * 'starsCustomerAccount'.
    **/
    public void setStarsCustomerAccount(StarsCustomerAccount starsCustomerAccount)
    {
        this._starsCustomerAccount = starsCustomerAccount;
    } //-- void setStarsCustomerAccount(StarsCustomerAccount) 

    /**
     * Sets the value of field 'starsSULMPrograms'.
     * 
     * @param starsSULMPrograms the value of field
     * 'starsSULMPrograms'.
    **/
    public void setStarsSULMPrograms(StarsSULMPrograms starsSULMPrograms)
    {
        this._starsSULMPrograms = starsSULMPrograms;
    } //-- void setStarsSULMPrograms(StarsSULMPrograms) 

    /**
     * Sets the value of field 'starsUpdateLogin'.
     * 
     * @param starsUpdateLogin the value of field 'starsUpdateLogin'
    **/
    public void setStarsUpdateLogin(StarsUpdateLogin starsUpdateLogin)
    {
        this._starsUpdateLogin = starsUpdateLogin;
    } //-- void setStarsUpdateLogin(StarsUpdateLogin) 

}
