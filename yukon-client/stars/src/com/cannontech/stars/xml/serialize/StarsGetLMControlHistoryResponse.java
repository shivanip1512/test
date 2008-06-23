package com.cannontech.stars.xml.serialize;

public class StarsGetLMControlHistoryResponse {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsLMControlHistory _starsLMControlHistory;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetLMControlHistoryResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsGetLMControlHistoryResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsLMControlHistory'.
     * 
     * @return the value of field 'starsLMControlHistory'.
    **/
    public StarsLMControlHistory getStarsLMControlHistory()
    {
        return this._starsLMControlHistory;
    } //-- StarsLMControlHistory getStarsLMControlHistory() 

    /**
     * Sets the value of field 'starsLMControlHistory'.
     * 
     * @param starsLMControlHistory the value of field
     * 'starsLMControlHistory'.
    **/
    public void setStarsLMControlHistory(StarsLMControlHistory starsLMControlHistory)
    {
        this._starsLMControlHistory = starsLMControlHistory;
    } //-- void setStarsLMControlHistory(StarsLMControlHistory) 

}
