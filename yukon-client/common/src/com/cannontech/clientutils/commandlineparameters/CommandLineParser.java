package com.cannontech.clientutils.commandlineparameters;

/**
 * Insert the type's description here.
 * Creation date: (10/12/00 3:44:18 PM)
 * @author: 
 */
public class CommandLineParser 
{
	public static final String ASSIGNMENT = "=";
	private String[] parameterNames = null;
/**
 * CommandLineParser constructor comment.
 */
private CommandLineParser() {
	super();
}
/**
 * CommandLineParser constructor comment.
 */
public CommandLineParser( String[] paramNames )
{
	super();

	setParameterNames( paramNames );
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 4:42:30 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getParameterNames() {
	return parameterNames;
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 4:53:53 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{	
	String[] paramsNames = 
	{
		"port",
		"value",
		"host"		
	};

	String[] paramsValues = 
	{
		"port=333333",
		"value=1",
		"host=CRAPULENCE"		
	};

	CommandLineParser pars = new CommandLineParser( paramsNames );

	String[] data = pars.parseArgs( paramsValues );

	for( int i = 0; i < data.length; i++ )
		System.out.println(data[i]);
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 3:45:41 PM)
 * @return java.lang.String[]
 * @param parameters java.lang.String[]
 */
public String[] parseArgs(String[] parameters) 
{
	if( parameters != null )
	{
		String[] tempHolder = new String[getParameterNames().length];

		for( int j = 0; j < getParameterNames().length; j++ )
		{
			for( int i = 0; i < parameters.length; i++ )
			{
				if( parameters[i].lastIndexOf(getParameterNames()[j]) > -1 )
				{
					// found the parameters name and value
					int lastLoc = parameters[i].indexOf(ASSIGNMENT) + 1;
					tempHolder[j] = parameters[i].substring(lastLoc);
					break;
				}
			}
		}
			
		return tempHolder;	
	}
	
		
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 4:42:30 PM)
 * @param newParameterNames java.lang.String[]
 */
private void setParameterNames(java.lang.String[] newParameterNames) {
	parameterNames = newParameterNames;
}
}
