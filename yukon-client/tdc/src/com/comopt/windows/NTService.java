package com.comopt.windows;

import java.util.StringTokenizer;

/**
 * @author Owner
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class NTService
{
	private int serviceID = -1;
	private String stringValue = null;	
	
	private String displayName = null;
	private String serviceName = null;
	private int serviceType = -1;
	private int currentState = -1;
   private String loadGroup = null;
	
	public NTService( String values )
	{
		super();

		setStringValue( values );
		initialize();
		
	}
   
   public int getCurrentState()
   {
      return currentState;
   }

   public String getLoadGroup()
   {
      return loadGroup;
   }

	/**
	 * Returns the currentState.
	 * @return String
	 */
	public String getCurrentStateString()
	{
		// Look at what state the service is in
		switch( currentState )
		{
			case JNTServices.SERVICE_STOPPED :
				return IServiceConstants.STATE_STOPPED;
			case JNTServices.SERVICE_RUNNING :
				return IServiceConstants.STATE_RUNNING;
				
			case JNTServices.SERVICE_START_PENDING :
				return IServiceConstants.STATE_START_PENDIONG;
			case JNTServices.SERVICE_STOP_PENDING :
				return IServiceConstants.STATE_STOP_PENDING;
				
			case JNTServices.SERVICE_CONTINUE_PENDING :
				return IServiceConstants.STATE_CONT_PENDING;
			case JNTServices.SERVICE_PAUSE_PENDING :
				return IServiceConstants.STATE_PAUSE_PENDING;

			case JNTServices.SERVICE_PAUSED :
				return IServiceConstants.STATE_PAUSED;

			default :
				return "(Unknown)";
		}
		
	}
	/**
	 * Returns the displayName.
	 * @return String
	 */
	public String getDisplayName()
	{
		return displayName;
	}
	/**
	 * Returns the serviceName.
	 * @return String
	 */
	public String getServiceName()
	{
		return serviceName;
	}
   
   public int getServiceType()
   {
      return serviceType;
   }


   public void setCurrentState( int state )
   {
      currentState = state;
   }

	/**
	 * Returns the serviceType.
	 * @return String
	 */
	public String getServiceTypeString()
	{
		// Look at service type
		switch( serviceType )
		{
			case JNTServices.SERVICE_TYPE_ADAPTER_DRIVER :
				return "Adapter Driver";
			case JNTServices.SERVICE_TYPE_ALL_PROCESS :
				return "All Process";
			case JNTServices.SERVICE_TYPE_FILE_SYSTEM_DRIVER :
				return "File System Driver";
				
			case JNTServices.SERVICE_TYPE_INTERACTIVE_PROCESS :
				return "Interactive Process";
			case JNTServices.SERVICE_TYPE_KERNEL_DRIVER :
				return "Kernel Driver";

			case JNTServices.SERVICE_TYPE_RECOGNIZER_DRIVER :
				return "Recognizer Driver";

			case JNTServices.SERVICE_TYPE_SERVICE_DRIVER :
				return "Service Driver";
			case JNTServices.SERVICE_TYPE_WIN32 :
				return "Win32";

			case JNTServices.SERVICE_TYPE_WIN32_OWN_PROCESS :
				return "Win32 Own Process";
			case JNTServices.SERVICE_TYPE_WIN32_SHARE_PROCESS :
				return "Win32 Share Prcoess";
				
			default :
				return "(Unknown)";
		}
		
	}
	/**
	 * Returns the stringValue.
	 * @return String
	 */
	public String getStringValue()
	{
		return stringValue;
	}
	private void initialize()
	{
		StringTokenizer tokenizer = 
				new StringTokenizer( getStringValue(), IServiceConstants.FIELD_DELIMITER );

		if( tokenizer.countTokens() < IServiceConstants.TOKEN_MIN_COUNT )
			throw new IllegalArgumentException( IServiceConstants.TOKEN_MIN_COUNT + " or more tokens are required to " +
				"create and instance of " + this.getClass().getName() +
				", not " + tokenizer.countTokens() );
				
		displayName = tokenizer.nextToken();
		serviceName = tokenizer.nextToken();
		serviceType = Integer.parseInt( tokenizer.nextToken() );
		currentState = Integer.parseInt( tokenizer.nextToken() );
      
      if( tokenizer.hasMoreTokens() )
         loadGroup = tokenizer.nextToken();
	}
	/**
	 * Sets the stringValue.
	 * @param stringValue The stringValue to set
	 */
	public void setStringValue(String stringValue)
	{
		this.stringValue = stringValue;
	}
   
   public static String createErrorString(int errorcode, String sServiceName)
   {
      String sErrorMessage = null;

      switch (errorcode)
      {
         case JNTServices.ERROR_ACCESS_DENIED :
            sErrorMessage = new String("Access denied to " + sServiceName);
            break;
         case JNTServices.ERROR_CIRCULAR_DEPENDENCY :
            sErrorMessage = new String("Circular dependcy to " + sServiceName);
            break;
         case JNTServices.ERROR_DEPENDENT_SERVICES_RUNNING :
            sErrorMessage = new String("Dependent service running");
            break;
         case JNTServices.ERROR_DUPLICATE_SERVICE_NAME :
            sErrorMessage = new String("Duplicate name service");
            break;
         case JNTServices.ERROR_DUP_NAME :
            sErrorMessage = new String("Duplicated name; " + sServiceName);
            break;
         case JNTServices.ERROR_FILE_NOT_FOUND :
            sErrorMessage = new String("File not found");
            break;
         case JNTServices.ERROR_INVALID_HANDLE :
            sErrorMessage = new String("Invalid handle");
            break;
         case JNTServices.ERROR_INVALID_NAME :
            sErrorMessage = new String("Invalid name");
            break;
         case JNTServices.ERROR_INVALID_PARAMETER :
            sErrorMessage = new String("Invalid parameter");
            break;
         case JNTServices.ERROR_INVALID_SERVICE_ACCOUNT :
            sErrorMessage = new String("Invalid service account");
            break;
         case JNTServices.ERROR_INVALID_SERVICE_CONTROL :
            sErrorMessage = new String("Invalid service control");
            break;
         case JNTServices.ERROR_PATH_NOT_FOUND :
            sErrorMessage = new String("Path not found");
            break;
         case JNTServices.ERROR_SERVICE_ALREADY_RUNNING :
            sErrorMessage =
               new String("Service(" + sServiceName + ") allready running");
            break;
         case JNTServices.ERROR_SERVICE_CANNOT_ACCEPT_CTRL :
            sErrorMessage = new String("Service can not accept control");
            break;
         case JNTServices.ERROR_SERVICE_DATABASE_LOCKED :
            sErrorMessage = new String("Service database locked");
            break;
         case JNTServices.ERROR_SERVICE_DEPENDENCY_DELETED :
            sErrorMessage = new String("Service dependency deleted");
            break;
         case JNTServices.ERROR_SERVICE_DEPENDENCY_FAIL :
            sErrorMessage = new String("Service dependency failed");
            break;
         case JNTServices.ERROR_SERVICE_DISABLED :
            sErrorMessage =
               new String("Service(" + sServiceName + ") disabled");
            break;
         case JNTServices.ERROR_SERVICE_DOES_NOT_EXIST :
            sErrorMessage =
               new String("Service (" + sServiceName + ") does not exist");
            break;
         case JNTServices.ERROR_SERVICE_EXISTS :
            sErrorMessage = new String("Service (" + sServiceName + ") exists");
            break;
         case JNTServices.ERROR_SERVICE_LOGON_FAILED :
            sErrorMessage = new String("Login failed");
            break;
         case JNTServices.ERROR_SERVICE_MARKED_FOR_DELETE :
            sErrorMessage =
               new String("Service (" + sServiceName + ") marked for delete");
            break;
         case JNTServices.ERROR_SERVICE_NOT_ACTIVE :
            sErrorMessage =
               new String("Service (" + sServiceName + ") not active");
            break;
         case JNTServices.ERROR_SERVICE_NO_THREAD :
            sErrorMessage = new String("No thread");
            break;
         case JNTServices.ERROR_SERVICE_REQUEST_TIMEOUT :
            sErrorMessage = new String("Request timed out");
            break;
         case -10 :
            sErrorMessage =
               new String("The arguments given were either NULL or zero length");
            break;
         default :
            sErrorMessage = new String("Errorcode; " + errorcode);
            break;
      }
      
      return sErrorMessage;
   }

}
