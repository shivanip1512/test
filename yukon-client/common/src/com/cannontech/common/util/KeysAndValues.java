package com.cannontech.common.util;

import java.util.Vector;

/**
 * @author snebben
 *
 * KeysAndValues - String keys and their String values.
 * Used to store key and values from KeyAndValuesFile.	
 */
public class KeysAndValues 
{
	private Vector keysAndValues = null;	
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
		keysAndValues = new Vector(keys.length);
		for (int i = 0; i < keys.length; i++)
		{
			keysAndValues.add(new KeyAndValue(keys[i], values[i]));
		}
	}
	
	/**
	 * Constructor KeysAndValues.
	 * @param keys java.util.Vector
	 * @param values java.util.Vector
	 */
	public KeysAndValues(java.util.Vector keys, java.util.Vector values)
	{
		super();
		keysAndValues = new Vector(keys.size());
		for (int i = 0; i < keys.size(); i++)
		{
			keysAndValues.add(new KeyAndValue((String)keys.get(i), (String)values.get(i)));
		}
	}

	/**
	 * Returns the keysAndValues.
	 * @return java.lang.String[][]
	 */
	public Vector getKeysAndValues()
	{
		if( keysAndValues == null )
			keysAndValues = new Vector(10);
		return keysAndValues;
	}
	/**
	 * Sets the keysAndValues.
	 * @param keysAndVals java.lang.String[][]
	 */
	public void setKeysAndValues(Vector keyAndValVector)
	{
		keysAndValues = keyAndValVector;
	}
	
	/**
	 * Returns keysAndValues[0], the keys part of keysAndValues.
	 * @return String[]
	 */
	public String[] getKeys()
	{
		String [] keysArray = new String[keysAndValues.size()];
		for (int i = 0; i < keysAndValues.size(); i++)
		{
			keysArray[i] = ((KeyAndValue)keysAndValues.get(i)).getKey();
		}
		return keysArray;
	}
	
	/**
	 * Returns keysAndValues[1], the values part of keysAndValues.
	 * @return String[]
	 */
	public String[] getValues()
	{
		String [] valsArray = new String[keysAndValues.size()];
		for (int i = 0; i < keysAndValues.size(); i++)
		{
			valsArray[i] = ((KeyAndValue)keysAndValues.get(i)).getValue();
		}
		return valsArray;
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
			for( int i = 0; i < keysAndValues.size(); i++ )
			{
				if( ((KeyAndValue)keysAndValues.get(i)).getKey().equalsIgnoreCase(key) )
				{
					return ((KeyAndValue)keysAndValues.get(i)).getValue();
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
			for( int i = 0; i < keysAndValues.size(); i++ )
			{
				if( ((KeyAndValue)keysAndValues.get(i)).getKey().equalsIgnoreCase(key))
				{
					values.addElement( ((KeyAndValue)keysAndValues.get(i)).getValue() );
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
