package com.cannontech.graph.model;

import com.cannontech.common.util.KeysAndValues;

/**
 * Insert the type's description here.
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

	private static String rangeLabel1 = "Reading";
	private static String rangeLabel2 = "Reading";
	private String domainLabel = "Date/Time";
	private String domainLabel_LD = "Percentage";

	private static long resolutionInMillis = 1;	//default to millis
	public int optionsMaskSettings = TrendModelType.NONE_MASK;
	public int viewType = TrendModelType.LINE_VIEW;
	
	public int graphDefID = -1;
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
		setPrimaryRangeLabel(primaryRangeLabel_);
		setSecondaryRangeLabel(secondaryRangeLabel_);
		setOptionsMaskSettings(optionsMask_);
		setViewType(viewType_);
		setResolutionInMillis(resolution_);
	}
	
	public void setPrimaryRangeLabel(String newLabel)
	{
		rangeLabel1 = newLabel;
	}
	public void setSecondaryRangeLabel(String newLabel)
	{
		rangeLabel2 = newLabel;
	}
	public String getPrimaryRangeLabel()
	{
		return rangeLabel1;
	}
	public String getSecondaryRangeLabel()
	{
		return rangeLabel2;
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
	 * @return
	 */
	public int getOptionsMaskSettings()
	{
		return optionsMaskSettings;
	}

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
	 * @see com.cannontech.export.ExportFormatBase#parseDatFile()
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
					setPrimaryRangeLabel(values[i].toString());
				}
				else if( keys[i].equalsIgnoreCase(RANGE_RIGHT_KEY))
				{
					setSecondaryRangeLabel(values[i].toString());
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
			}
		}
		com.cannontech.clientutils.CTILogger.info( " LOADED trending properties from file.");
	}		
	
	/**
	 * 
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
		values.add(getPrimaryRangeLabel());

		keys.add(RANGE_RIGHT_KEY); 
		values.add(getSecondaryRangeLabel());

		keys.add(OPTIONS_KEY); 
		values.add(String.valueOf(getOptionsMaskSettings()));
	
		keys.add(VIEWTYPE_KEY);
		values.add(String.valueOf(getViewType()));
	
		keys.add(RESOLUTION_KEY); 
		values.add(String.valueOf(getResolutionInMillis()));
		
		String[] keysArray = new String[keys.size()];
		keys.toArray(keysArray);
		String[] valuesArray = new String[values.size()];
		values.toArray(valuesArray);
		
		KeysAndValues keysAndValues = new KeysAndValues(keysArray, valuesArray);

		return keysAndValues;
	}
}

