package com.comopt.windows;

/**
 * @author Owner
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface IServiceConstants
{   
   public static final String FIELD_DELIMITER = "|";
   public static final int TOKEN_MIN_COUNT = 4;
   public static final String SERVICE_GROUP = "YUKON GROUP";
   
   public static final String STATE_STOPPED        = "Stopped";
   public static final String STATE_START_PENDIONG = "Start Pending";
   public static final String STATE_STOP_PENDING   = "Stop Pending";
   public static final String STATE_RUNNING        = "Running";
   public static final String STATE_CONT_PENDING   = "Continue Pending";
   public static final String STATE_PAUSE_PENDING  = "Pause Pending";
   public static final String STATE_PAUSED         = "Paused";

   
	/* Service Error Codes */
	public static final int ERROR_FILE_NOT_FOUND = 2;
	public static final int ERROR_PATH_NOT_FOUND = 3;
	public static final int ERROR_ACCESS_DENIED = 5;
	public static final int ERROR_INVALID_HANDLE = 6;
	public static final int ERROR_DUP_NAME = 52;
	public static final int ERROR_INVALID_PARAMETER = 87;
	public static final int ERROR_INVALID_NAME = 123;
	public static final int ERROR_DEPENDENT_SERVICES_RUNNING = 1051;
	public static final int ERROR_INVALID_SERVICE_CONTROL = 1052;
	public static final int ERROR_SERVICE_REQUEST_TIMEOUT = 1053;
	public static final int ERROR_SERVICE_NO_THREAD = 1054;
	public static final int ERROR_SERVICE_DATABASE_LOCKED = 1055;
	public static final int ERROR_SERVICE_ALREADY_RUNNING = 1056;
	public static final int ERROR_INVALID_SERVICE_ACCOUNT = 1057;
	public static final int ERROR_SERVICE_DISABLED = 1058;
	public static final int ERROR_CIRCULAR_DEPENDENCY = 1059;
	public static final int ERROR_SERVICE_DOES_NOT_EXIST = 1060;
	public static final int ERROR_SERVICE_CANNOT_ACCEPT_CTRL = 1061;
	public static final int ERROR_SERVICE_NOT_ACTIVE = 1062;
	public static final int ERROR_SERVICE_DEPENDENCY_FAIL = 1068;
	public static final int ERROR_SERVICE_LOGON_FAILED = 1069;
	public static final int ERROR_SERVICE_MARKED_FOR_DELETE = 1072;
	public static final int ERROR_SERVICE_EXISTS = 1073;
	public static final int ERROR_SERVICE_DEPENDENCY_DELETED = 1075;
	public static final int ERROR_DUPLICATE_SERVICE_NAME = 1078;

	/* Service Start Modes */
	public static final int SERVICE_BOOT_START = 0;
	public static final int SERVICE_SYSTEM_START = 1;
	public static final int SERVICE_AUTO_START = 2;
	public static final int SERVICE_DEMAND_START = 3;
	public static final int SERVICE_DISABLED = 4;

	/* Service Status Codes */
	public static final int SERVICE_STOPPED = 1;
	public static final int SERVICE_START_PENDING = 2;
	public static final int SERVICE_STOP_PENDING = 3;
	public static final int SERVICE_RUNNING = 4;
	public static final int SERVICE_CONTINUE_PENDING = 5;
	public static final int SERVICE_PAUSE_PENDING = 6;
	public static final int SERVICE_PAUSED = 7;

	/* Service Type Codes */
	public static final int SERVICE_TYPE_KERNEL_DRIVER = 0x1;
	public static final int SERVICE_TYPE_FILE_SYSTEM_DRIVER = 0x2;
	public static final int SERVICE_TYPE_ADAPTER_DRIVER = 0x4;
	public static final int SERVICE_TYPE_RECOGNIZER_DRIVER = 0x8;
	public static final int SERVICE_TYPE_SERVICE_DRIVER =
		(SERVICE_TYPE_KERNEL_DRIVER
			| SERVICE_TYPE_FILE_SYSTEM_DRIVER
			| SERVICE_TYPE_RECOGNIZER_DRIVER);
			
	public static final int SERVICE_TYPE_WIN32_OWN_PROCESS = 0x10;
	public static final int SERVICE_TYPE_WIN32_SHARE_PROCESS = 0x20;
	public static final int SERVICE_TYPE_WIN32 =
		(SERVICE_TYPE_WIN32_OWN_PROCESS | SERVICE_TYPE_WIN32_SHARE_PROCESS);
		
	public static final int SERVICE_TYPE_INTERACTIVE_PROCESS = 0x100;
	public static final int SERVICE_TYPE_ALL_PROCESS =
		(SERVICE_TYPE_WIN32
			| SERVICE_TYPE_ADAPTER_DRIVER
			| SERVICE_TYPE_SERVICE_DRIVER
			| SERVICE_TYPE_INTERACTIVE_PROCESS);

}
