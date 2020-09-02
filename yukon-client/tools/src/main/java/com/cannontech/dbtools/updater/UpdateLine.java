package com.cannontech.dbtools.updater;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UpdateLine
{
	//the actual line that is to be executed on the DB
	private StringBuffer value = null;
	
	//stores (key<String>, value<String>)
	private Map<String, String> metaProps = null;

	/**
	 * 
	 */
	public UpdateLine()
	{
		super();
	}


	/**
	 * Contains (key<String>, value<String>)
	 * @return
	 */
	public Map<String, String> getMetaProps()
	{
		if( metaProps == null )
            metaProps = new LinkedHashMap<String, String>(16);

		return metaProps;
	}

	/**
	 * @return StringBuffer
	 */
	public StringBuffer getValue()
	{
		if( value == null )
			value = new StringBuffer(16);

		return value;
	}

	/**
	 * @return String
	 */
	public String toString()
	{
		if( value == null )
			return "(null)";
		else
			return value.toString();
	}
	
	/**
	 * @param string
	 */
	public void setValue(StringBuffer string)
	{
		value = string;
	}

	public String[] getPrintableText()
	{
		String[] retVals = new String[ getMetaProps().size() + 1 ];

		Iterator<Entry<String, String>> iter = getMetaProps().entrySet().iterator();
		int i = 0;
		
		while( iter.hasNext() )
		{
			Entry<String, String> entr = iter.next();
			
			retVals[i++] = 
					DBMSDefines.COMMENT_BEGIN + " " + 
					DBMSDefines.META_TAG +
					entr.getKey() + DBMSDefines.META_TOKEN + 
					entr.getValue() + " " + 
					DBMSDefines.COMMENT_END;
		}
		

		retVals[i] = getValue().toString();
		return retVals;		
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/17/00 4:28:38 PM)
	 * @return boolean
	 */
	public boolean equals(Object o)
	{
		if( o == null )
			return false;
		else if( o instanceof UpdateLine )
		{
			return  ((UpdateLine)o).getValue().toString().equalsIgnoreCase(
							this.getValue().toString() );
		}
		else
			return false;
	}

	/**
	 * keep this consistent with .equals() pleez
	 * o1.equals(o2) => o1.hashCode() == o2.hashCode()
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() 
	{
		return getValue().toString().hashCode();
	}

	/**
	 * @return
	 */
	public Boolean isSuccess()
	{
		Object o = getMetaProps().get( DBMSDefines.META_SUCCESS );
		if( o != null )
			return Boolean.valueOf(o.toString()).booleanValue();
		else
			return null;
	}

	/**
	 * @param b
	 */
	public void setSuccess(boolean b)
	{
		getMetaProps().put( DBMSDefines.META_SUCCESS, Boolean.toString(b) );
	}


	/**
	 * @return
	 */
	public boolean isIgnoreError()
	{
		Object o = getMetaProps().get( DBMSDefines.META_ERROR );
		if( o != null )
			return DBMSDefines.OPTIONS_ERROR[0].equalsIgnoreCase(o.toString());
		else
			return false;
	}
	
	/**
	 * @return
	 */
	public boolean isIgnoreRemainingErrors()
	{
		Object o = getMetaProps().get( DBMSDefines.META_ERROR );
		if( o != null )
			return DBMSDefines.OPTIONS_ERROR[3].equalsIgnoreCase(o.toString());
		else
			return false;
	}

	/**
	 * @param b
	 */
	public void setIgnoreError(boolean b)
	{
		getMetaProps().put( DBMSDefines.META_ERROR, DBMSDefines.OPTIONS_ERROR[0] );
	}

    /**
     * @return
     */
    public boolean isIgnoreBegin()
    {
        Object o = getMetaProps().get( DBMSDefines.META_ERROR );
        if( o != null )
            return DBMSDefines.OPTIONS_ERROR[4].equalsIgnoreCase(o.toString());
        else
            return false;
    }

    /**
     * @return
     */
    public boolean isIgnoreEnd()
    {
        Object o = getMetaProps().get( DBMSDefines.META_ERROR );
        if( o != null )
            return DBMSDefines.OPTIONS_ERROR[5].equalsIgnoreCase(o.toString());
        else
            return false;
    }
    
    public boolean isWarnOnce()
    {
        Object o = getMetaProps().get( DBMSDefines.META_ERROR );
        if( o != null )
            return DBMSDefines.OPTIONS_ERROR[6].equalsIgnoreCase(o.toString());
        else
            return false;
    }


    public void setWarnOnce() {
        getMetaProps().put( DBMSDefines.META_ERROR, DBMSDefines.OPTIONS_ERROR[6] );
    }


    public void setIgnoreEnd() {
        getMetaProps().put( DBMSDefines.META_ERROR, DBMSDefines.OPTIONS_ERROR[5] );
    }

    /**
     * Method to check if warning message is required or not.
     */
    public Boolean isWarning() {
        Object o = getMetaProps().get(DBMSDefines.START_WARNING);
        return o != null ? true : false;
    }

    /**
     * method to retrieve commandName from the @start-warning annotation.
     */
    public String getCommandName() {
        Object o = getMetaProps().get(DBMSDefines.START_WARNING);
        if (o != null)
            return o.toString().split("\\s+")[0];
        else
            return StringUtils.EMPTY;
    }

    /**
     * method to retrieve warning message from the @start-warning annotation.
     */
    public String getWarningMessage() {
        Object o = getMetaProps().get(DBMSDefines.START_WARNING);
        if (o != null)
            return o.toString().split("\\s+", 2)[1];
        else
            return StringUtils.EMPTY;
    }
}