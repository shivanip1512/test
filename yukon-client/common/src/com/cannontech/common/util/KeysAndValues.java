package com.cannontech.common.util;

/**
 * @author snebben
 *
 * KeysAndValues - String keys and their String values.
 * Used to store key and values from KeyAndValuesFile.	
 */
public class KeysAndValues
{
	private String[][] keysAndValues = null;	
	
	/**
	 * Constructor for KeysAndValues.
	 */
	public KeysAndValues()
	{
		super();
	}
	
	/**
	 * Constructor for KeysAndValues.
	 * @param keys java.lang.String[]
	 * @param values java.lang.String[]
	 */
	public KeysAndValues(String[] keys, String[] values)
	{
		super();
		keysAndValues = new String[2][keys.length];
		keysAndValues[0] = keys;
		keysAndValues[1] = values;
	}
	
	/**
	 * Constructor KeysAndValues.
	 * @param keys java.util.Vector
	 * @param values java.util.Vector
	 */
	public KeysAndValues(java.util.Vector keys, java.util.Vector values)
	{
		super();
		keysAndValues = new String[2][keys.size()];
		keys.copyInto(keysAndValues[0]);
		values.copyInto(keysAndValues[1]);
	}

	/**
	 * Returns the keysAndValues.
	 * @return java.lang.String[][]
	 */
	public String[][] getKeysAndValues()
	{
		return keysAndValues;
	}
	/**
	 * Sets the keysAndValues.
	 * @param keysAndVals java.lang.String[][]
	 */
	public void setKeysAndValues(String[][] keysAndVals)
	{
		keysAndValues = keysAndVals;
	}
	
	/**
	 * Returns keysAndValues[0], the keys part of keysAndValues.
	 * @return String[]
	 */
	public String[] getKeys()
	{
		return keysAndValues[0];
	}
	
	/**
	 * Returns keysAndValues[1], the values part of keysAndValues.
	 * @return String[]
	 */
	public String[] getValues()
	{
		return keysAndValues[1];
	}

	/**
	 * Returns the value found for key.
	 * @param key java.lang.String
	 * @return String
	 */
	public String getValue(String key)
	{
		if( keysAndValues != null)
		{
			for( int i = 0; i < keysAndValues[0].length; i++ )
			{
				if( keysAndValues[0][i].equalsIgnoreCase(key) )
				{
					return keysAndValues[1][i];
				}
			}
		}	
		return null;
	}
	
	/**
	 * Return a String[] of values found for key.
	 * @param key java.lang.String
	 * @return String[]
	 */
	public String[] getValues(String key)
	{
		if( keysAndValues != null)
		{
			java.util.Vector values = new java.util.Vector();
			for( int i = 0; i < keysAndValues[0].length; i++ )
			{
				if( keysAndValues[0][i].equalsIgnoreCase(key))
				{
					values.addElement( keysAndValues[1][i] );
				}
			}
		
			if( values.size() > 0 )
			{
				String[] retVal = new String[values.size()];
				values.copyInto( retVal );
				return retVal;
			}
		}
		return null;
	}
}
