package com.cannontech.common.util;

/**
 * @author snebben
 *
 * Used to be ConfigParmsFile, changed organization and location. 3/6/03SN.
 * A file of key=value type entries.
 * The '@include' string parses for the inclusion of other keyAndValuesFile(s).
 */

public class KeysAndValuesFile extends java.io.File
{
	public static final String DEFAULT_FILENAME = "keyAndValue.dat";
	public static final char DEFAULT_SEPARATOR = '=';
	public static final char DEFAULT_COMMENT = '#';
	public static final String DEFAULT_INCLUDE = "@include";// represents that the following string is a file to be included.
	public static final char DEFAULT_VALUE_PROMPT = '?'; //the chars that follow the question mark(?), may be grouped by quotes(" or '), is the value 
														 //that will be displayed on a prompt.  The returned prompted value will replace the ?string value.
	
	private char separator = DEFAULT_SEPARATOR;	//override java.io.File.separator parameter
	private char comment = DEFAULT_COMMENT;
	
	private KeysAndValues keysAndValues = null;
	
	//A vector of CanonicalPath strings of previously read files.
	//This is very important information to keep track of.  It allows us to guard against 
	//  the infinite include file loop...so make sure this gets populated correctly!
	private java.util.Vector includedFiles = new java.util.Vector();

	/**
	 * This method was created in VisualAge.
	 */
	public KeysAndValuesFile() {
		super(DEFAULT_FILENAME);
	}

	/**
	 * ConfigParmsFile constructor comment.
	 * @param dir java.io.File
	 * @param name java.lang.String
	 */
	public KeysAndValuesFile(java.io.File dir, String name) {
		super(dir, name);
	}

	/**
	 * ConfigParmsFile constructor comment.
	 * @param path java.lang.String
	 */
	public KeysAndValuesFile(String path) {
		super(path);
	}

	/**
	 * Constructor for KeysAndValuesFile.
	 * @param uri
	 */
	public KeysAndValuesFile(java.net.URI uri)
	{
		super(uri);
	}

	/**
	 * ConfigParmsFile constructor comment.
	 * @param path java.lang.String
	 * @param name java.lang.String
	 */
	public KeysAndValuesFile(String path, String name) {
		super(path, name);
	}

	/**
	 * @see com.cannontech.message.util.ConfigParms#getKeysAndValues()
	 */
	public void retrieve()
	{
		java.util.Vector keys = new java.util.Vector();
		java.util.Vector values = new java.util.Vector();
	
		java.io.RandomAccessFile raFile = null;
		try
		{
			getIncludedFiles().add(getCanonicalPath());		
			// open file		
			if( this.exists() )
			{
				raFile = new java.io.RandomAccessFile( this, "r" );
						
				long readLinePointer = 0;
				long fileLength = raFile.length();
	
				while ( readLinePointer < fileLength )  // loop until the end of the file
				{
					String line = raFile.readLine();  // read a line in
	
//					if( line.length() > 2 )	// must have at least a=b (3 chars) for a valid length.
					{
						//Check if this line contains a comment character
						int commentPos = line.indexOf(getComment());
						if( commentPos != - 1 )
						{
							//get rid of everything after the command character
							line = line.substring(0, commentPos);
						}


						//If an include file is listed and we must parse trough that file too.
						int includeIndex = line.indexOf(DEFAULT_INCLUDE);
						if( includeIndex != -1)	//A file is listed to be included
						{
							includeIndex = includeIndex + DEFAULT_INCLUDE.length();
							line = (line.substring(includeIndex)).trim();	//trim all leading/trailing whitespace
							
							String canPath = getCanonicalPath();
							String newPath = canPath.substring(0, canPath.lastIndexOf("\\") + 1);
							newPath = newPath + line;
	
							KeysAndValuesFile cpf = new KeysAndValuesFile(newPath);
							if( !isIncluded(cpf.getCanonicalPath()))
							{						
								cpf.setIncludedFiles(getIncludedFiles());
								
								KeysAndValues tempKAV = cpf.getKeysAndValues();
								if( tempKAV != null)
								{
									for(int i = 0; i < tempKAV.getKeys().length; i++)
									{
										keys.addElement(tempKAV.getKeys()[i]);
										values.addElement(tempKAV.getValues()[i]);
									}
								}
							}
						}
						else
						{	
							int separatorIndex = line.indexOf(separator);
							if( separatorIndex != -1)	// separator character is found
							{
								String key = line.substring(0, separatorIndex);
								String value = line.substring(separatorIndex + 1);

								keys.addElement( key.trim());
								values.addElement( value.trim() );
							}
							else	//no separator found so assuming it is a value only
							{
								keys.addElement("");
								values.addElement(line);
							}
						}
					}
	
					readLinePointer = raFile.getFilePointer();
				}
			}
			else
				return;
	
			// Close file
			raFile.close();						
		}
		catch( java.io.FileNotFoundException fnfe)
		{
			com.cannontech.clientutils.CTILogger.info("*** File Not Found Exception:");
			com.cannontech.clientutils.CTILogger.info( this.getClass().getName() + ".getKeysAndValues()" );
			com.cannontech.clientutils.CTILogger.info("  " + fnfe.getMessage());
			com.cannontech.clientutils.CTILogger.error( fnfe.getMessage(), fnfe );
		}
		catch( java.io.IOException ioe )
		{
			com.cannontech.clientutils.CTILogger.error( ioe.getMessage(), ioe );
		}
		finally
		{
			try
			{
				if( raFile != null)
					raFile.close();
			}
			catch (java.io.IOException e)
			{
				e.printStackTrace();		
			}
		}	
	
		if( keys.size() != values.size() )
			return;
	
		setKeysAndValues(new KeysAndValues(keys, values));
	}
	
	/**
	 * Returns the vector of CanonicalPath strings of previously read files.
	 * @return Vector
	 */
	private java.util.Vector getIncludedFiles()
	{
		//we want to keep track of all files we have already read 
		// so we don't read them again and form an infinate loop.	
		return includedFiles;
	}	
		
	/**
	 * Method setIncludedFiles.
	 * @param files
	 */
	private void setIncludedFiles(java.util.Vector files)
	{
		includedFiles = files;
	}	
	
	/**
	 * Return true if fileString exists in Vector() includeFiles.
	 * @param fileString
	 * @return boolean
	 */
	private boolean isIncluded(String fileString)
	{
		for (int i = 0; i < getIncludedFiles().size(); i++)
		{
			if( ((String)getIncludedFiles().get(i)).equalsIgnoreCase(fileString))
				return true;
		}
	
		return false;
	}
	
	/**
	 * Return separator.
	 * @return char
	 */
	public char getSeparator() {
		return separator;
	}

	/**
	 * Set separator value.
	 * Separator between the key and value.
	 * @param sep char
	 */
	public void setSeparator(char sep) {
		separator = sep;
	}
	/**
	 * Returns the keysAndValues.
	 * @return KeysAndValues
	 */
	public KeysAndValues getKeysAndValues()
	{
		if( keysAndValues == null)
		{
			retrieve();
		}
		return keysAndValues;
	}

	/**
	 * Sets the keysAndValues.
	 * @param keysAndValues The keysAndValues to set
	 */
	public void setKeysAndValues(KeysAndValues keysAndValues)
	{
		this.keysAndValues = keysAndValues;
	}

	/**
	 * Returns the comment.
	 * @return char
	 */
	public char getComment()
	{
		return comment;
	}

	/**
	 * Sets the comment.
	 * @param comment The comment to set
	 */
	public void setComment(char comment)
	{
		this.comment = comment;
	}
	

	/**
	 * Insert the method's description here.
	 * Creation date: (5/13/2002 9:23:11 AM)
	 */
	public void writeToFile()
	{
		try
		{
			//if not there, lets create the file
			if( !exists())
				createNewFile();
			
			java.io.FileWriter writer = new java.io.FileWriter(this);
	
			for (int i = 0; i < getKeysAndValues().getKeys().length; i++)
			{
				writer.write(getKeysAndValues().getKeys()[i]);
				writer.write(separator);
				writer.write(getKeysAndValues().getValues()[i]);
				writer.write("\r\n");
			}
			writer.close();
		}
		catch ( java.io.IOException e )
		{
			System.out.print(" IOException in writeToFile");
			e.printStackTrace();
		}
		
	}
}
