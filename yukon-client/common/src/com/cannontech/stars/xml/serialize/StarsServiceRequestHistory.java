package com.cannontech.stars.xml.serialize;

import java.util.Vector;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsServiceRequestHistory {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsServiceRequestList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsServiceRequestHistory() {
        super();
        _starsServiceRequestList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsServiceRequestHistory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsServiceRequest
    **/
    public void addStarsServiceRequest(StarsServiceRequest vStarsServiceRequest)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsServiceRequestList.addElement(vStarsServiceRequest);
    } //-- void addStarsServiceRequest(StarsServiceRequest) 

    /**
     * 
     * 
     * @param index
     * @param vStarsServiceRequest
    **/
    public void addStarsServiceRequest(int index, StarsServiceRequest vStarsServiceRequest)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsServiceRequestList.insertElementAt(vStarsServiceRequest, index);
    } //-- void addStarsServiceRequest(int, StarsServiceRequest) 

    /**
    **/
    public java.util.Enumeration enumerateStarsServiceRequest()
    {
        return _starsServiceRequestList.elements();
    } //-- java.util.Enumeration enumerateStarsServiceRequest() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsServiceRequest getStarsServiceRequest(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsServiceRequestList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsServiceRequest) _starsServiceRequestList.elementAt(index);
    } //-- StarsServiceRequest getStarsServiceRequest(int) 

    /**
    **/
    public StarsServiceRequest[] getStarsServiceRequest()
    {
        int size = _starsServiceRequestList.size();
        StarsServiceRequest[] mArray = new StarsServiceRequest[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsServiceRequest) _starsServiceRequestList.elementAt(index);
        }
        return mArray;
    } //-- StarsServiceRequest[] getStarsServiceRequest() 

    /**
    **/
    public int getStarsServiceRequestCount()
    {
        return _starsServiceRequestList.size();
    } //-- int getStarsServiceRequestCount() 

    /**
    **/
    public void removeAllStarsServiceRequest()
    {
        _starsServiceRequestList.removeAllElements();
    } //-- void removeAllStarsServiceRequest() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsServiceRequest removeStarsServiceRequest(int index)
    {
        java.lang.Object obj = _starsServiceRequestList.elementAt(index);
        _starsServiceRequestList.removeElementAt(index);
        return (StarsServiceRequest) obj;
    } //-- StarsServiceRequest removeStarsServiceRequest(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsServiceRequest
    **/
    public void setStarsServiceRequest(int index, StarsServiceRequest vStarsServiceRequest)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsServiceRequestList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsServiceRequestList.setElementAt(vStarsServiceRequest, index);
    } //-- void setStarsServiceRequest(int, StarsServiceRequest) 

    /**
     * 
     * 
     * @param starsServiceRequestArray
    **/
    public void setStarsServiceRequest(StarsServiceRequest[] starsServiceRequestArray)
    {
        //-- copy array
        _starsServiceRequestList.removeAllElements();
        for (int i = 0; i < starsServiceRequestArray.length; i++) {
            _starsServiceRequestList.addElement(starsServiceRequestArray[i]);
        }
    } //-- void setStarsServiceRequest(StarsServiceRequest) 

}
