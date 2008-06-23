package com.cannontech.stars.xml.serialize;

public class StarsUpdateCallReportResponse {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsCallReport _starsCallReport;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateCallReportResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateCallReportResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsCallReport'.
     * 
     * @return the value of field 'starsCallReport'.
    **/
    public StarsCallReport getStarsCallReport()
    {
        return this._starsCallReport;
    } //-- StarsCallReport getStarsCallReport() 

    /**
     * Sets the value of field 'starsCallReport'.
     * 
     * @param starsCallReport the value of field 'starsCallReport'.
    **/
    public void setStarsCallReport(StarsCallReport starsCallReport)
    {
        this._starsCallReport = starsCallReport;
    } //-- void setStarsCallReport(StarsCallReport) 

}
