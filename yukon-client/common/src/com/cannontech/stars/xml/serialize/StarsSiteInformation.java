/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsSiteInformation.java,v 1.96 2008/06/23 20:01:28 nmeverden Exp $
 */

package com.cannontech.stars.xml.serialize;

public class StarsSiteInformation {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _siteID;

    /**
     * keeps track of state for field: _siteID
    **/
    private boolean _has_siteID;

    private Substation _substation;

    private java.lang.String _feeder;

    private java.lang.String _pole;

    private java.lang.String _transformerSize;

    private java.lang.String _serviceVoltage;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSiteInformation() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsSiteInformation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteSiteID()
    {
        this._has_siteID= false;
    } //-- void deleteSiteID() 

    /**
     * Returns the value of field 'feeder'.
     * 
     * @return the value of field 'feeder'.
    **/
    public java.lang.String getFeeder()
    {
        return this._feeder;
    } //-- java.lang.String getFeeder() 

    /**
     * Returns the value of field 'pole'.
     * 
     * @return the value of field 'pole'.
    **/
    public java.lang.String getPole()
    {
        return this._pole;
    } //-- java.lang.String getPole() 

    /**
     * Returns the value of field 'serviceVoltage'.
     * 
     * @return the value of field 'serviceVoltage'.
    **/
    public java.lang.String getServiceVoltage()
    {
        return this._serviceVoltage;
    } //-- java.lang.String getServiceVoltage() 

    /**
     * Returns the value of field 'siteID'.
     * 
     * @return the value of field 'siteID'.
    **/
    public int getSiteID()
    {
        return this._siteID;
    } //-- int getSiteID() 

    /**
     * Returns the value of field 'substation'.
     * 
     * @return the value of field 'substation'.
    **/
    public Substation getSubstation()
    {
        return this._substation;
    } //-- Substation getSubstation() 

    /**
     * Returns the value of field 'transformerSize'.
     * 
     * @return the value of field 'transformerSize'.
    **/
    public java.lang.String getTransformerSize()
    {
        return this._transformerSize;
    } //-- java.lang.String getTransformerSize() 

    /**
    **/
    public boolean hasSiteID()
    {
        return this._has_siteID;
    } //-- boolean hasSiteID() 

    /**
     * Sets the value of field 'feeder'.
     * 
     * @param feeder the value of field 'feeder'.
    **/
    public void setFeeder(java.lang.String feeder)
    {
        this._feeder = feeder;
    } //-- void setFeeder(java.lang.String) 

    /**
     * Sets the value of field 'pole'.
     * 
     * @param pole the value of field 'pole'.
    **/
    public void setPole(java.lang.String pole)
    {
        this._pole = pole;
    } //-- void setPole(java.lang.String) 

    /**
     * Sets the value of field 'serviceVoltage'.
     * 
     * @param serviceVoltage the value of field 'serviceVoltage'.
    **/
    public void setServiceVoltage(java.lang.String serviceVoltage)
    {
        this._serviceVoltage = serviceVoltage;
    } //-- void setServiceVoltage(java.lang.String) 

    /**
     * Sets the value of field 'siteID'.
     * 
     * @param siteID the value of field 'siteID'.
    **/
    public void setSiteID(int siteID)
    {
        this._siteID = siteID;
        this._has_siteID = true;
    } //-- void setSiteID(int) 

    /**
     * Sets the value of field 'substation'.
     * 
     * @param substation the value of field 'substation'.
    **/
    public void setSubstation(Substation substation)
    {
        this._substation = substation;
    } //-- void setSubstation(Substation) 

    /**
     * Sets the value of field 'transformerSize'.
     * 
     * @param transformerSize the value of field 'transformerSize'.
    **/
    public void setTransformerSize(java.lang.String transformerSize)
    {
        this._transformerSize = transformerSize;
    } //-- void setTransformerSize(java.lang.String) 

}
