/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.xml.serialize;

public class StarsThermostatSchedule {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings _day;

    private org.exolab.castor.types.Time _time1;

    private int coolTemperature1;
    private int heatTemperature1;

    /**
     * keeps track of state for field: _temperature1
    **/
    private boolean _has_temperature1;

    private org.exolab.castor.types.Time _time2;

    private int coolTemperature2;
    private int heatTemperature2;

    /**
     * keeps track of state for field: _temperature2
    **/
    private boolean _has_temperature2;

    private org.exolab.castor.types.Time _time3;

    private int coolTemperature3;
    private int heatTemperature3;

    /**
     * keeps track of state for field: _temperature3
    **/
    private boolean _has_temperature3;

    private org.exolab.castor.types.Time _time4;

    private int coolTemperature4;
    private int heatTemperature4;

    /**
     * keeps track of state for field: _temperature4
    **/
    private boolean _has_temperature4;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermostatSchedule() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatSchedule()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'day'.
     * 
     * @return the value of field 'day'.
    **/
    public com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings getDay()
    {
        return this._day;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings getDay() 

    /**
     * Returns the value of field 'temperature1'.
     * 
     * @return the value of field 'temperature1'.
    **/
    public int getCoolTemperature1()
    {
        return this.coolTemperature1;
    } //-- int getTemperature1()
    
    public int getHeatTemperature1() {
		return heatTemperature1;
	}

    /**
     * Returns the value of field 'temperature2'.
     * 
     * @return the value of field 'temperature2'.
    **/
    public int getCoolTemperature2()
    {
        return this.coolTemperature2;
    } //-- int getTemperature2()
    
    public int getHeatTemperature2() {
		return heatTemperature2;
	}

    /**
     * Returns the value of field 'temperature3'.
     * 
     * @return the value of field 'temperature3'.
    **/
    public int getCoolTemperature3()
    {
        return this.coolTemperature3;
    } //-- int getTemperature3() 

    public int getHeatTemperature3() {
		return heatTemperature3;
	}
    
    /**
     * Returns the value of field 'temperature4'.
     * 
     * @return the value of field 'temperature4'.
    **/
    public int getCoolTemperature4()
    {
        return this.coolTemperature4;
    } //-- int getTemperature4()
    
    public int getHeatTemperature4() {
		return heatTemperature4;
	}

    /**
     * Returns the value of field 'time1'.
     * 
     * @return the value of field 'time1'.
    **/
    public org.exolab.castor.types.Time getTime1()
    {
        return this._time1;
    } //-- org.exolab.castor.types.Time getTime1() 

    /**
     * Returns the value of field 'time2'.
     * 
     * @return the value of field 'time2'.
    **/
    public org.exolab.castor.types.Time getTime2()
    {
        return this._time2;
    } //-- org.exolab.castor.types.Time getTime2() 

    /**
     * Returns the value of field 'time3'.
     * 
     * @return the value of field 'time3'.
    **/
    public org.exolab.castor.types.Time getTime3()
    {
        return this._time3;
    } //-- org.exolab.castor.types.Time getTime3() 

    /**
     * Returns the value of field 'time4'.
     * 
     * @return the value of field 'time4'.
    **/
    public org.exolab.castor.types.Time getTime4()
    {
        return this._time4;
    } //-- org.exolab.castor.types.Time getTime4() 

    /**
    **/
    public boolean hasTemperature1()
    {
        return this._has_temperature1;
    } //-- boolean hasTemperature1() 

    /**
    **/
    public boolean hasTemperature2()
    {
        return this._has_temperature2;
    } //-- boolean hasTemperature2() 

    /**
    **/
    public boolean hasTemperature3()
    {
        return this._has_temperature3;
    } //-- boolean hasTemperature3() 

    /**
    **/
    public boolean hasTemperature4()
    {
        return this._has_temperature4;
    } //-- boolean hasTemperature4() 

    /**
     * Sets the value of field 'day'.
     * 
     * @param day the value of field 'day'.
    **/
    public void setDay(com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings day)
    {
        this._day = day;
    } //-- void setDay(com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings) 

    /**
     * Sets the value of field 'temperature1'.
     * 
     * @param coolTemperature1 the value of field 'temperature1'.
    **/
    public void setCoolTemperature1(int coolTemperature1)
    {
        this.coolTemperature1 = coolTemperature1;
        this._has_temperature1 = true;
    } //-- void setTemperature1(int) 

    public void setHeatTemperature1(int heatTemperature1)
    {
    	this.heatTemperature1 = heatTemperature1;
    	this._has_temperature1 = true;
    } 

    /**
     * Sets the value of field 'temperature2'.
     * 
     * @param coolTemperature2 the value of field 'temperature2'.
    **/
    public void setCoolTemperature2(int coolTemperature2)
    {
        this.coolTemperature2 = coolTemperature2;
        this._has_temperature2 = true;
    } //-- void setTemperature2(int)
    
    public void setHeatTemperature2(int heatTemperature2)
    {
    	this.heatTemperature2 = heatTemperature2;
    	this._has_temperature2 = true;
    } 

    /**
     * Sets the value of field 'temperature3'.
     * 
     * @param coolTemperature3 the value of field 'temperature3'.
    **/
    public void setCoolTemperature3(int coolTemperature3)
    {
        this.coolTemperature3 = coolTemperature3;
        this._has_temperature3 = true;
    } //-- void setTemperature3(int)
    
    public void setHeatTemperature3(int heatTemperature3)
    {
    	this.heatTemperature3 = heatTemperature3;
    	this._has_temperature3 = true;
    } 

    /**
     * Sets the value of field 'temperature4'.
     * 
     * @param coolTemperature4 the value of field 'temperature4'.
    **/
    public void setCoolTemperature4(int coolTemperature4)
    {
        this.coolTemperature4 = coolTemperature4;
        this._has_temperature4 = true;
    } //-- void setTemperature4(int)
    
    public void setHeatTemperature4(int heatTemperature4)
    {
    	this.heatTemperature4 = heatTemperature4;
    	this._has_temperature4 = true;
    } 

    /**
     * Sets the value of field 'time1'.
     * 
     * @param time1 the value of field 'time1'.
    **/
    public void setTime1(org.exolab.castor.types.Time time1)
    {
        this._time1 = time1;
    } //-- void setTime1(org.exolab.castor.types.Time) 

    /**
     * Sets the value of field 'time2'.
     * 
     * @param time2 the value of field 'time2'.
    **/
    public void setTime2(org.exolab.castor.types.Time time2)
    {
        this._time2 = time2;
    } //-- void setTime2(org.exolab.castor.types.Time) 

    /**
     * Sets the value of field 'time3'.
     * 
     * @param time3 the value of field 'time3'.
    **/
    public void setTime3(org.exolab.castor.types.Time time3)
    {
        this._time3 = time3;
    } //-- void setTime3(org.exolab.castor.types.Time) 

    /**
     * Sets the value of field 'time4'.
     * 
     * @param time4 the value of field 'time4'.
    **/
    public void setTime4(org.exolab.castor.types.Time time4)
    {
        this._time4 = time4;
    } //-- void setTime4(org.exolab.castor.types.Time) 

}
