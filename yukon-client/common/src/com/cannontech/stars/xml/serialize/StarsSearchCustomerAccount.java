package com.cannontech.stars.xml.serialize;

public class StarsSearchCustomerAccount {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private SearchBy _searchBy;

    private java.lang.String _searchValue;
    
    private boolean _partialMatch;

    /**
     * keeps track of state for field: _partialMatch
    **/
    private boolean _has_partialMatch;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSearchCustomerAccount() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsSearchCustomerAccount()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'searchBy'.
     * 
     * @return the value of field 'searchBy'.
    **/
    public SearchBy getSearchBy()
    {
        return this._searchBy;
    } //-- SearchBy getSearchBy() 

    /**
     * Returns the value of field 'searchValue'.
     * 
     * @return the value of field 'searchValue'.
    **/
    public java.lang.String getSearchValue()
    {
        return this._searchValue;
    } //-- java.lang.String getSearchValue() 

    /**
     * Sets the value of field 'searchBy'.
     * 
     * @param searchBy the value of field 'searchBy'.
    **/
    public void setSearchBy(SearchBy searchBy)
    {
        this._searchBy = searchBy;
    } //-- void setSearchBy(SearchBy) 

    /**
     * Sets the value of field 'searchValue'.
     * 
     * @param searchValue the value of field 'searchValue'.
    **/
    public void setSearchValue(java.lang.String searchValue)
    {
        this._searchValue = searchValue;
    } //-- void setSearchValue(java.lang.String) 

    /**
     **/
     public void deletePartialMatch()
     {
         this._has_partialMatch = false;
     } //-- void deletePartialMatch() 

     /**
      * Returns the value of field 'partialMatch'.
      * 
      * @return the value of field 'partialMatch'.
     **/
     public boolean getPartialMatch()
     {
         return this._partialMatch;
     } //-- boolean getPartialMatch() 

     /**
     **/
     public boolean hasPartialMatch()
     {
         return this._has_partialMatch;
     } //-- boolean hasPartialMatch() 

     /**
      * Sets the value of field 'partialMatch'.
      * 
      * @param partialMatch the value of field
      * 'partialMatch'.
     **/
     public void setPartialMatch(boolean partialMatch)
     {
         this._partialMatch = partialMatch;
         this._has_partialMatch = true;
     } //-- void setPartialMatch(boolean) 

}
