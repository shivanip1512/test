package com.cannontech.graph.model;

import com.cannontech.common.util.KeysAndValues;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.graph.GraphDefines;

/**
 * Properties for a Trend.
 * Properties are read from a file, if exists, or defaulted to best guess.
 * Properties can be written to a file for next startup feature.
 * Creation date: (7/10/2001 5:01:43 PM)
 * @author: 
 */
public class TrendProperties
{
	public static final String TREND_DEFAULTS_FILENAME = "/TrendOptions.DAT";
	public static final String TREND_DEFAULTS_DIRECTORY = com.cannontech.common.util.CtiUtilities.getConfigDirPath();

	private final String DOMAIN_KEY = "DOMAIN";
	private final String DOMAIN_LD_KEY = "DOMAIN_LD";
	private final String RANGE_LEFT_KEY = "RANGE_LEFT";
	private final String RANGE_RIGHT_KEY = "RANGE_RIGHT";
	private final String OPTIONS_KEY = "OPTIONS";
	private final String VIEWTYPE_KEY = "VIEWTYPE";
	private final String RESOLUTION_KEY = "RESOLUTION";
	private final String GDEF_NAME_KEY = "NAME";

	//currently limited to only 2 range axis.
	private static String[] rangeLabel = new String[]{
		"Reading", "Reading"
	};
	private String domainLabel = "Date/Time";
	private String domainLabel_LD = "Percentage";

	private static long resolutionInMillis = 1;	//default to millis
	public int optionsMaskSettings = GraphRenderers.NONE_MASK;
	public int viewType = GraphRenderers.LINE;
	public String gdefName = "";
	
	/**
	 * TrendPropertiesconstructor comment.
	 */
	public TrendProperties() {
		this(true);
	}

	/**
	 * TrendPropertiesconstructor comment.
	 */
	public TrendProperties(boolean useSavedData) {
		super();
		if( useSavedData )
			parseDatFile();
	}

	/**
	 * TrendProperties constructor comment.
	 */
	public TrendProperties(String domainLabel_, String domainLabel_LD_, String primaryRangeLabel_, String secondaryRangeLabel_, 
							int optionsMask_, int viewType_, long resolution_) 
	{
		super();
		setDomainLabel(domainLabel_);
		setDomainLabel_LD(domainLabel_LD_);
		setRangeLabel(primaryRangeLabel_, GraphDefines.PRIMARY_AXIS);
		setRangeLabel(secondaryRangeLabel_, GraphDefines.SECONDARY_AXIS);
		setOptionsMaskSettings(optionsMask_);
		setViewType(viewType_);
		setResolutionInMillis(resolution_);
	}
	
	/**
	 * @param newLabel
	 */
	public void setRangeLabel(String newLabel, int axisIndex)
	{
		if(axisIndex >= 0 && axisIndex < rangeLabel.length)
			rangeLabel[axisIndex] = newLabel;
	}
	
	/**
	 * The label used for the Primary (left by default) Range axis.
	 * @return rangeLabel1
	 */
	public String getRangeLabel(int axisIndex)
	{
		if(axisIndex >= 0 && axisIndex < rangeLabel.length)
			return rangeLabel[GraphDefines.PRIMARY_AXIS];
		
		return rangeLabel[axisIndex];
	}

	/**
	 * Returns the resolutionInMillis.
	 * @return long
	 */
	public long getResolutionInMillis()
	{
		return resolutionInMillis;
	}

	/**
	 * Sets the resolutionInMillis.
	 * @param resolutionInMillis The resolutionInMillis to set
	 */
	public void setResolutionInMillis(long resolutionInMillis)
	{
		TrendProperties.resolutionInMillis = resolutionInMillis;
	}

	/**
	 * The label used for the Domain axis. 
	 * @return
	 */
	public String getDomainLabel()
	{
		return domainLabel;
	}

	/**
	 * @param string
	 */
	public void setDomainLabel(String string)
	{
		domainLabel = string;
	}

	/**
	 * The label used for the Domain axis during Load Duration data display.
	 * @return
	 */
	public String getDomainLabel_LD()
	{
		return domainLabel_LD;
	}

	/**
	 * @param string
	 */
	public void setDomainLabel_LD(String string)
	{
		domainLabel_LD = string;
	}

	/**
	 * An options bit mask.
	 * See com.cannontech.graph.model.TrendModelType for valid option masks. 
	 * @return
	 */
	public int getOptionsMaskSettings()
	{
		return optionsMaskSettings;
	}

	/**
	 * @param newMask Bit mask to apply.
	 * @param addMask Boolean value to add/remove (T/F) a masked bit. 
	 */
	public void updateOptionsMaskSettings(int newMask, boolean addMask)
	{
		// when setMasked = true, the newMask will be added to the options_mask
		// when setMasked = false, the newMask will be removed from the options_mask
		if( addMask)
			optionsMaskSettings |= newMask;
		else
		{
			//check to make sure it's there if we are going to remove it
			if( (optionsMaskSettings & newMask) != 0)
			{
				optionsMaskSettings ^= newMask;
			}
		}
	}


	/**
	 * @param i
	 */
	public void setOptionsMaskSettings(int i)
	{
		optionsMaskSettings = i;
	}
	/**
	 * The view (bar, line, etc.) type for a trend.
	 * See com.cannontech.graph.model.TrendModelType for valid view types.
	 * @return
	 */
	public int getViewType()
	{
		return viewType;
	}

	/**
	 * @param i
	 */
	public void setViewType(int i)
	{
		viewType = i;
	}
	
	/**
	 * The last selected graph name.
	 * GraphDefinition.Name table-column value.
	 * @return
	 */
	public String getGdefName()
	{
		return gdefName;
	}

	/**
	 * @param string
	 */
	public void setGdefName(String string)
	{
		gdefName = string;
	}

	/**
	 * Write the TrendProperties to a file, TREND_DEFAULTS_DIRECTORY+TREND_DEFAULTS_FILENAME, 
	 * in a Keys=Values format (com.cannontech.common.util.KeysAndValues)
	 */
	public void writeDefaultsFile()
	{
		try
		{
			java.io.File file = new java.io.File(TREND_DEFAULTS_DIRECTORY);
			file.mkdirs();

			java.io.FileWriter writer = new java.io.FileWriter( file.getPath() + TREND_DEFAULTS_FILENAME);
			KeysAndValues keysAndValues = buildKeysAndValues();
			
			String keys[] = keysAndValues.getKeys();
			String values[] = keysAndValues.getValues();

			String endline = "\r\n";

			for (int i = 0; i < keys.length; i++)
			{
				writer.write(keys[i] + "=" + values[i] + endline);
			}
			writer.close();
		}
		catch ( java.io.IOException e )
		{
			System.out.print(" IOException in writeDatFile");
			e.printStackTrace();
		}
	}		
	
	/**
	 * Parse the properties file (TREND_DEFAULTS_DIRECTORY+TREND_DEFAULTS_FILENAME, if exists)
	 *  for properties last saved values.
	 */
	public void parseDatFile()
	{
		com.cannontech.common.util.KeysAndValuesFile kavFile = new com.cannontech.common.util.KeysAndValuesFile(TREND_DEFAULTS_DIRECTORY + TREND_DEFAULTS_FILENAME);
		com.cannontech.common.util.KeysAndValues keysAndValues = kavFile.getKeysAndValues();
		
		if( keysAndValues != null )
		{
			String keys[] = keysAndValues.getKeys();
			String values[] = keysAndValues.getValues();
			for (int i = 0; i < keys.length; i++)
			{
				if(keys[i].equalsIgnoreCase(DOMAIN_KEY))
				{
					setDomainLabel(values[i].toString());
				}
				else if( keys[i].equalsIgnoreCase(DOMAIN_LD_KEY))
				{
					setDomainLabel_LD(values[i].toString());
				}
				else if( keys[i].equalsIgnoreCase(RANGE_LEFT_KEY))
				{
					setRangeLabel(values[i].toString(), GraphDefines.PRIMARY_AXIS);
				}
				else if( keys[i].equalsIgnoreCase(RANGE_RIGHT_KEY))
				{
					setRangeLabel(values[i].toString(), GraphDefines.SECONDARY_AXIS);
				}
				else if( keys[i].equalsIgnoreCase(OPTIONS_KEY))
				{
					setOptionsMaskSettings(Integer.parseInt(values[i]));
				}
				else if( keys[i].equalsIgnoreCase(VIEWTYPE_KEY))
				{
					setViewType(Integer.parseInt(values[i]));
				}
				else if( keys[i].equalsIgnoreCase(RESOLUTION_KEY))
				{
					setResolutionInMillis(Long.parseLong(values[i]));
				}
				else if( keys[i].equalsIgnoreCase(GDEF_NAME_KEY))
				{
					setGdefName(values[i].toString());
				}
			}
		}
		com.cannontech.clientutils.CTILogger.info( " LOADED trending properties from file.");
	}		
	
	/**
	 * Builds a KeysAndValues class with all trendProperties and their current values.
	 * Keys=Values format (com.cannontech.common.util.KeysAndValues)
	 * @return KeysAndValues
	 */
	public KeysAndValues buildKeysAndValues()
	{
		java.util.Vector keys = new java.util.Vector(10);
		java.util.Vector values = new java.util.Vector(10);

		keys.add(DOMAIN_KEY); 
		values.add(getDomainLabel());
		
		keys.add(DOMAIN_LD_KEY); 
		values.add(getDomainLabel_LD());

		keys.add(RANGE_LEFT_KEY); 
		values.add(getRangeLabel(GraphDefines.PRIMARY_AXIS));

		keys.add(RANGE_RIGHT_KEY); 
		values.add(getRangeLabel(GraphDefines.SECONDARY_AXIS));

		keys.add(OPTIONS_KEY); 
		values.add(String.valueOf(getOptionsMaskSettings()));
	
		keys.add(VIEWTYPE_KEY);
		values.add(String.valueOf(getViewType()));
	
		keys.add(RESOLUTION_KEY); 
		values.add(String.valueOf(getResolutionInMillis()));
		
		keys.add(GDEF_NAME_KEY); 
		values.add(getGdefName());

		String[] keysArray = new String[keys.size()];
		keys.toArray(keysArray);
		String[] valuesArray = new String[values.size()];
		values.toArray(valuesArray);
		
		KeysAndValues keysAndValues = new KeysAndValues(keysArray, valuesArray);

		return keysAndValues;
	}
}

