package com.cannontech.clientutils.parametersfile;

/**
 * Insert the type's description here.
 * Creation date: (5/16/00 4:56:39 PM)
 * @author: 
 * @Version: <version>
 */
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class ParametersFile 
{
	private Vector parameterPairs = null;
	//private Vector newParameterValues = null;
	private final String EQUALS_DELIMITER = "=";
	
	private boolean parametersSuccessfull = true;

	private String fileName = "DefualtParameterFile.DAT";

	/**
	 * ParametersFile constructor comment.
	 */
	public ParametersFile()
	{
		super();
		
		initialize();
	}
	/**
	 * ParametersFile constructor comment.
	 */
	public ParametersFile( String parameterFileName ) 
	{
		super();
		
		fileName = parameterFileName;
	//	parameterNames = parameterList;
		
		initialize();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (5/18/00 2:13:44 PM)
	 * Version: <version>
	 * @return int
	 */
	public int getParameterCount() 
	{
		if( parameterPairs == null )
			return 0;
		else
			return parameterPairs.size();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/16/00 5:10:29 PM)
	 * Version: <version>
	 * @return java.lang.Object[]
	 */
	public int getParameterLocation ( String parameterName ) throws ParameterNotFoundException
	{
		if( parameterPairs == null )
			return -1;
	
		for( int i = 0; i < getParameterCount(); i++ )
		{
			if( getToken( parameterPairs.elementAt(i).toString(), "NAME" ).equalsIgnoreCase( parameterName ) )
				return i;			
		}
	
		// parameter not found
		//return -1;
		throw new ParameterNotFoundException("Unable to find file parameter location for " + parameterName );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/18/00 2:15:49 PM)
	 * Version: <version>
	 * @param location int
	 */
	public String getParameterNameAt(int location) throws ParameterNotFoundException
	{
		if( parameterPairs == null || location < 0 || location >= getParameterCount() )
			throw new ParameterNotFoundException("Unable to find file parameter name at location " + location);
		else
			return getToken( parameterPairs.elementAt(location).toString(), "NAME" );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/16/00 5:10:29 PM)
	 * Version: <version>
	 * @return java.lang.Object[]
	 */
	public String getParameterValue( String parameterName, String defaultValue )// throws ParameterNotFoundException
	{
		if( parameterPairs == null )
			return defaultValue;
	
		for( int i = 0; i < getParameterCount(); i++ )
		{
			if( getToken( parameterPairs.elementAt(i).toString(), "NAME" ).equalsIgnoreCase( parameterName ) )
				return getToken( parameterPairs.elementAt(i).toString(), "VALUE" );
		}
	
		// parameter not found
		//return null;
		//throw new ParameterNotFoundException("Unable to find parameter " + parameterName );
		return defaultValue;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/6/00 1:51:30 PM)
	 */
	private void updateValue( String paramName_, String value_ ) throws IOException 
	{
		boolean fnd = false;

		for( int i = 0; i < getParameterCount(); i++ )
		{	
			if( getParameterNameAt(i).toString().equalsIgnoreCase(paramName_) )
			{
				parameterPairs.setElementAt(
					getParameterNameAt(i) + EQUALS_DELIMITER + value_, i );

				fnd = true;
			}
			
		}

		//very strange, we NEED this to be in the file!
		if( !fnd )
		{
			parameterPairs.add( paramName_ + EQUALS_DELIMITER + value_ );
		}

	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (5/18/00 2:15:49 PM)
	 * Version: <version>
	 * @param location int
	 */
	public String getParameterValueAt( int location, String defaultValue )// throws ParameterNotFoundException
	{
		if( parameterPairs == null || location < 0 || location >= getParameterCount() )
			//throw new ParameterNotFoundException("Unable to find file parameter value at location " + location);
			return defaultValue;
		else
			return getToken( parameterPairs.elementAt(location).toString(), "VALUE" );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/16/00 5:10:29 PM)
	 * Version: <version>
	 * @return java.lang.Object[]
	 */
	public Object[] getParameterValues() 
	{
		if( parameterPairs == null )
			return null;
	
		return parameterPairs.toArray();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (7/21/00 3:03:24 PM)
	 * @return java.util.StringTokenizer
	 */
	private String getToken( String value, String location )
	{
		if( location == null )
			return null;
			
		StringTokenizer tokenizer = new StringTokenizer( value, EQUALS_DELIMITER );
	
		try
		{
			if( location.equalsIgnoreCase("NAME") )
			{
				return tokenizer.nextToken();
			}
			else if( location.equalsIgnoreCase("VALUE") )
			{
				tokenizer.nextToken();	
				return tokenizer.nextToken();
			}
			else
				return null;
		}
		catch( java.util.NoSuchElementException ex )
		{
			return "";
		}
		
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/16/00 5:03:57 PM)
	 * Version: <version>
	 * @param e java.lang.Exception
	 */
	private void handleException(Throwable t) 
	{
		com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/9/00 5:22:33 PM)
	 */
	private void initialize() 
	{
		parametersSuccessfull = parseInputFile();
		
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/10/00 11:03:30 AM)
	 * @return boolean
	 */
	public boolean parametersExisted() 
	{
		return parametersSuccessfull;
	}
	/**
	 * This method was created in VisualAge.
	 */
	private boolean parseInputFile()
	{
		java.io.RandomAccessFile file = null;
		
		parameterPairs = new Vector( getParameterCount() );
		
		java.io.File checkFile = new java.io.File( fileName );
		
		try
		{
			// open file		
			if( checkFile.exists() )
			{
				file = new java.io.RandomAccessFile( checkFile, "r" );
						
				long filePointer = 0;
				long length = file.length();
	
					
				while ( filePointer < length )  // loop until the end of the file
				{
						
					String line = file.readLine();  // read a line in
	
					if( line.indexOf(EQUALS_DELIMITER) <= 0 )
						return false; //no delimiter found, this must be a corrupt file

					parameterPairs.addElement( line );
	
					// set our pointer to the new position in the file
					filePointer = file.getFilePointer();
				}
			}
			else
				return false;
	
			// Close file
			file.close();						
		}
		catch(IOException ex)
		{	
			return false;
		}
		finally
		{
			try
			{
				if( checkFile.exists() )
					file.close();
			}
			catch( IOException ex )
			{}		
		}
	
		// make sure we received all lines from the parameters file
	/*	if( parameterPairs.size() != getParameterCount() )
			return false;
		else*/
		
		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/6/00 1:51:30 PM)
	 */
	public boolean updateValues( String[] names_, String[] values_ ) 
	{
		boolean retValue = false;

		//get out you INVALIDS!
		if( names_ == null || values_ == null 
			 || (names_.length != values_.length) )
			return false;

		
		try
		{
			FileWriter writer = new FileWriter( fileName );

			for( int i = 0; i < names_.length; i++ )
				updateValue( names_[i], values_[i] );

			//write the values back to the file
			for( int i = 0; i < getParameterCount(); i++ )
				writer.write( 
						parameterPairs.get(i).toString() +
						System.getProperty("line.separator") );				

			writer.close();
			retValue = true;  // successfull
		}
		catch ( IOException e )
		{
			handleException( e );
		}

		return retValue; //success or not
	}

}