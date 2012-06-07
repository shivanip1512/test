package com.cannontech.stars.xml.serialize;

public class VersaCom {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _utility;
    private int _section;
    private int _classAddress;
    private int _division;

      //----------------/
     //- Constructors -/
    //----------------/

    public VersaCom() {
        super();
    } //-- com.cannontech.stars.xml.serialize.VersaCom()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'classAddress'.
     * 
     * @return the value of field 'classAddress'.
    **/
    public int getClassAddress()
    {
        return this._classAddress;
    } //-- int getClassAddress() 

    /**
     * Returns the value of field 'division'.
     * 
     * @return the value of field 'division'.
    **/
    public int getDivision()
    {
        return this._division;
    } //-- int getDivision() 

    /**
     * Returns the value of field 'section'.
     * 
     * @return the value of field 'section'.
    **/
    public int getSection()
    {
        return this._section;
    } //-- int getSection() 

    /**
     * Returns the value of field 'utility'.
     * 
     * @return the value of field 'utility'.
    **/
    public int getUtility()
    {
        return this._utility;
    } //-- int getUtility() 

    /**
     * Sets the value of field 'classAddress'.
     * 
     * @param classAddress the value of field 'classAddress'.
    **/
    public void setClassAddress(int classAddress)
    {
        this._classAddress = classAddress;
    } //-- void setClassAddress(int) 

    /**
     * Sets the value of field 'division'.
     * 
     * @param division the value of field 'division'.
    **/
    public void setDivision(int division)
    {
        this._division = division;
    } //-- void setDivision(int) 

    /**
     * Sets the value of field 'section'.
     * 
     * @param section the value of field 'section'.
    **/
    public void setSection(int section)
    {
        this._section = section;
    } //-- void setSection(int) 

    /**
     * Sets the value of field 'utility'.
     * 
     * @param utility the value of field 'utility'.
    **/
    public void setUtility(int utility)
    {
        this._utility = utility;
    } //-- void setUtility(int) 

}
