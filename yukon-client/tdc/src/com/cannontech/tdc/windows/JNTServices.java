package com.cannontech.tdc.windows;

import java.io.File;
/**
 * This class is an API for the Windows NT Services, here it is possible to
 * <ul>
 * <li> install
 * <li> uninstall
 * <li> start
 * <li> stop
 * </ul>
 * NT Services.<br>
 * The class will provide a very basic API to manage Windows NT Services, a lot more can 
 * be done with NT Services, but at the momemt this is what I need.<br>
 * Tobias Eriksson (<A HREF="mailto:tobias@comopt.com">tobias@comopt.com</A>)<br>
 * <P>
 * <br>
 * To install a SRVANY.EXE run service do the following:<br>
 *<ul>
 *  1) Install a service called 'MY OWN SERVICE NAME' with the 'SRVANY.EXE' 
 * 		executable<br>
 *  2) Set up the windows registry;<br>
 *<ul>
 *      HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\'MY OWN SERVICE NAME'\\Parameters<br>
 *<ul>
 *		Application     =   "MY SERVICE EXECUTABLE"<br>
 *		AppParameters   =   "MY SERVICE EXCUTABLE PARAMETERS"<br>
 *</ul>
 *</ul>
 *<br>
 *  3) That is it.<br>
 *</ul>
 * This makes it possible to infact install a java application as a windows NT Services.
 *
 *<br>
 * To install a Messages DLL for the EVENT system do the following:<br>
 *<ul>
 *  1) Install a service called 'MY OWN SERVICE NAME' with an executable.<br>
 *  2) Set up the registry settings:<br>
 *<ul>
 *      HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\EventLog\\Application\\'MY OWN SERVICE NAME'<br>
 *<ul>
 *          EventMessageFile    =   "A DLL OR EXE FOR EVENT HANDLING"<br>
 *          TypesSupported      =   e.g. EVENTLOG_ERROR_TYPE | EVENTLOG_WARNING_TYPE, or 1 | 2<br>
 *</ul>
 *</ul>
 *  3) That is it.<br>
 *</ul>
 *<br>
 *  Where 'TypesSupported' can be:
 * <ul>
 * <li>  EVENTLOG_ERROR_TYPE             0x0001
 * <li>  EVENTLOG_WARNING_TYPE           0x0002
 * <li>  EVENTLOG_INFORMATION_TYPE       0x0004
 *</ul>
 * The calls can give a non-sero (!=0) value back, these are the possible error codes.<br>
 *<br>
 * The following error codes can be of interrest:<br>
 *<br>
 * @author Tobias Eriksson (tobias@comopt.com) ComOpt AB
 * @version $Id: JNTServices.java,v 1.1 2002/03/07 15:21:45 tobiaseriksson Exp $
 */
public class JNTServices implements IServiceConstants
{
	private static final String DLL_FILE = "JNTServices.dll";
	private String machineName = null; //local machine

	private static boolean bLibraryLoaded = false;
   private static JNTServices scmManagerInstance = null;

	/**
	 *  This constructor will load the library "JNTServices" (JNTServices.dll)
	 *  when this object is created. This enables the user to infact include the dll
	 *  with the software e.g. InstallAnywhere and install it into c:\windows\system32
	 * and then create an object of this class which will then load the library and
	 * then you will have access to the windows NT Services API through Java.
	 * Note that the library is only loaded once for all instances of this class.
	 *
	 */
	private JNTServices()
	{
      super();

      if( System.getProperty("os.name").indexOf("Window") == -1 ) //Windows XP
         throw new IllegalArgumentException("Invalid OS of " + System.getProperty("os.name") );

		if (bLibraryLoaded == false)
		{
			//System.loadLibrary( new File(DLL_FILE).getAbsolutePath() );
			System.load( new File(DLL_FILE).getAbsolutePath() );
			bLibraryLoaded = true;
		}

	}

   public synchronized static JNTServices getInstance()
   {
      if( scmManagerInstance == null )
         scmManagerInstance = new JNTServices();

      return scmManagerInstance;      
   }
   
	public String getMachineName()
	{
		return machineName;
	}

   public void setMachineName( String machName )
   {
      machineName = machName;
   }

	/**
	 * This method will start a Windows NT Services named sServiceShortName
	 * it can be started with arguments to the command otherwise set number of
	 * args to 0.
	 * @param sServiceShortName is the name of the NT service to be started (note short name as registered upon install)
	 * @param iNumberOfArgs Set to '0' if NO arguments are given
	 * @param sArgs is an array of arguments that will be passed to the service executable
	 * @throws
	 * @return It will return >0 if there was an error, 0 on success.
	 * Possile error codes returned are:
	 * <ul>
	 * <li>ERROR_ACCESS_DENIED
	 * <li>ERROR_DATABASE_DOES_NOT_EXIST
	 * <li>ERROR_INVALID_PARAMETER
	 * <li>ERROR_INVALID_HANDLE
	 * <li>ERROR_INVALID_NAME
	 * <li>ERROR_SERVICE_DOES_NOT_EXIST
	 * <li>ERROR_PATH_NOT_FOUND
	 * <li>ERROR_SERVICE_ALREADY_RUNNING
	 * <li>ERROR_SERVICE_DATABASE_LOCKED
	 * <li>ERROR_SERVICE_DEPENDENCY_DELETED
	 * <li>ERROR_SERVICE_DEPENDENCY_FAIL
	 * <li>ERROR_SERVICE_DISABLED
	 * <li>ERROR_SERVICE_LOGON_FAILED
	 * <li>ERROR_SERVICE_MARKED_FOR_DELETE
	 * <li>ERROR_SERVICE_NO_THREAD
	 * <li>ERROR_SERVICE_REQUEST_TIMEOUT
	 * </ul>
	 */
	public native int start( String sServiceShortName,int iNumberOfArgs,
		String sArgs[], String machName);

	public int start(
		String sServiceShortName,
		int iNumberOfArgs,
		String sArgs[])
	{
		return start(sServiceShortName, iNumberOfArgs, sArgs, getMachineName());
	}

	/**
	 * This method stops a Windows NT Service from running. It will retry n times before giving up.
	 * It will return -1 if the number of retries has exceeded.
	 * It will return >0 if there was an error, 0 on success.
	 *
	 * @param sServiceShortName The short name of the NT Service as registered during the install of the service.
	 * @param iNumberOfRetries The number of times to retry stopping the service
	 * @param iTimeoutInms The timeout, between retries, recommended to 10.000 ms.
	 * @throws
	 * @returns It will return !=0 if it fails, and 0 on success.
	 * Possile error codes returned are:
	 * <ul>
	 * <li>ERROR_ACCESS_DENIED
	 * <li>ERROR_DATABASE_DOES_NOT_EXIST
	 * <li>ERROR_INVALID_PARAMETER
	 * <li>ERROR_INVALID_HANDLE
	 * <li>ERROR_INVALID_NAME
	 * <li>ERROR_SERVICE_DOES_NOT_EXIST
	 * <li>ERROR_DEPENDENT_SERVICES_RUNNING
	 * <li>ERROR_INVALID_SERVICE_CONTROL
	 * <li>ERROR_SERVICE_CANNOT_ACCEPT_CTRL
	 * <li>ERROR_SERVICE_NOT_ACTIVE
	 * <li>ERROR_SERVICE_REQUEST_TIMEOUT
	 * </ul>
	 */
	public native int stop(
		String sServiceShortName,
		int iNumberOfRetries,
		int iTimeoutInms,
		String machName);

	public int stop(
		String sServiceShortName,
		int iNumberOfRetries,
		int iTimeoutInms)
	{
		return stop(
			sServiceShortName,
			iNumberOfRetries,
			iTimeoutInms,
			getMachineName());
	}

	/**
	 * This method will installa a service.
	 *
	 * @param sServiceShortName This is the short name of the service,
	 * @param sServiceFullName This is the full name of the service as it appears in the Windows NT Services Applet, it is convenient to set it to the same name as the short name since when referenced the short name is allways used.
	 * @param sEXEPath This is the executable to be run.
	 * @param startType This is the type of start up the process will have, it can have any of the ones below, preferrably 2 and 3, as 0 and 1 are for drivers only.
	 *
	 * The following codes are possible startType options:
	 * <ul>
	 *  <li> SERVICE_BOOT_START             0x00000000
	 *  <li> SERVICE_SYSTEM_START           0x00000001
	 *  <li> SERVICE_AUTO_START             0x00000002
	 *  <li> SERVICE_DEMAND_START           0x00000003
	 *  <li> SERVICE_DISABLED               0x00000004
	 * </ul>
	 *
	 * @return It will return >0 if there was an error, and 0 on success.
	 * Possile error codes returned are:
	 * <ul>
	 * <li>ERROR_ACCESS_DENIED
	 * <li>ERROR_CIRCULAR_DEPENDENCY
	 * <li>ERROR_DUP_NAME
	 * <li>ERROR_INVALID_HANDLE
	 * <li>ERROR_INVALID_NAME
	 * <li>ERROR_INVALID_PARAMETER
	 * <li>ERROR_INVALID_SERVICE_ACCOUNT
	 * <li>ERROR_SERVICE_EXISTS
	 * <li>ERROR_DATABASE_DOES_NOT_EXIST
	 * <li>
	 * </ul>
	 */
	public native int install(
		String sServiceShortName,
		String sServiceFullName,
		String sEXEPath,
		int startType,
		String machName);

	public int install(
		String sServiceShortName,
		String sServiceFullName,
		String sEXEPath,
		int startType)
	{
		return install(sServiceShortName, sServiceFullName, sEXEPath, startType);
	}

	/**
	 * This method will uninstall a Windows NT Service.
	 * It will return >0 if there was an error, 0 on success.
	 * It will return -1 if the service was not stopped, this is required, please stop and try again.
	 * It will return 1060 if the service does not exist.
	 *
	 * @param The short name of the service to uninstall.
	 * @throws
	 * @return It will return !=0 if there was an error, 0 on success. '-1' is returned if the servies was not STOPPED.
	 * Possile error codes returned are:
	 * <ul>
	 * <li>ERROR_DATABASE_DOES_NOT_EXIST
	 * <li>ERROR_ACCESS_DENIED
	 * <li>ERROR_CIRCULAR_DEPENDENCY
	 * <li>ERROR_INVALID_PARAMETER
	 * <li>ERROR_INVALID_HANDLE
	 * <li>ERROR_INVALID_NAME
	 * <li>ERROR_SERVICE_DOES_NOT_EXIST
	 * <li>ERROR_SERVICE_MARKED_FOR_DELETE
	 * </ul>
	 */
	public native int uninstall(String sServiceShortName, String machName);

	public int uninstall(String sServiceShortName)
	{
		return uninstall(sServiceShortName, getMachineName());
	}

	/**
	 * This method will try to install a Windows NT Service even if the service is allready installed, if it is it will remove it and install the new one instead.
	 * In essence what it does is:<br>
	 * <ul>
	 * 1) Try to install<br>
	 * 2) If first install failed, then:
	 * <ul>
	 * 2.1) stop the service.<br>
	 * 2.2) remove the old service.<br>
	 * 2.3) install the service again.<br>
	 * </ul>
	 * 3) Finnished.<br>
	 * </ul>
	 * @param sServiceShortName This is the short name of the service,
	 * @param sServiceShortName This is the Full name of the service, as it is displayed in the ControlPanel applet for service.
	 * @param sEXEPath This is the executable to be run.
	 * @param startType This is the type of start up the process will have, it can have any of the ones below, preferrably 2 and 3, as 0 and 1 are for drivers only.
	 *
	 * The following codes are possible startType options:
	 * <ul>
	 *  <li> SERVICE_BOOT_START             0x00000000
	 *  <li> SERVICE_SYSTEM_START           0x00000001
	 *  <li> SERVICE_AUTO_START             0x00000002
	 *  <li> SERVICE_DEMAND_START           0x00000003
	 *  <li> SERVICE_DISABLED               0x00000004
	 * </ul>
	 *
	 * @throws
	 * @return It will return !=0 if there was an error, 0 on success. Note that the possible error codes are the same as for install, stop and uninstall above, it will return -1 if any of the argument are null.
	 */
	public int justInstall( String sServiceShortName, String sServiceFullName, String sEXEPath, int startType)
	{
		int status = 0;
		int iRetries = 12;

		//
		// Verify the input variables
		//
		if (sServiceShortName == null || sServiceShortName == null)
		{
			return -1;
		}

		//
		// Try to install the service
		//
		status = this.install(sServiceShortName, sServiceFullName, sEXEPath, startType);
		
		if( status == JNTServices.ERROR_SERVICE_EXISTS
			 || status == JNTServices.ERROR_DUPLICATE_SERVICE_NAME)
		{
			// Service is allready installed

			// stop service
			status = this.stop(sServiceShortName, iRetries, 10000);
			if (status != 0 && status != JNTServices.ERROR_SERVICE_NOT_ACTIVE)
			{
				if (status != 0)
				{
					System.out.println(
						"Could not stop service ("
							+ sServiceShortName
							+ "), tried "
							+ iRetries
							+ " times, errorcode;"
							+ status);
					return status;
				}
			}
			
			// un-install service
			if ((status = this.uninstall(sServiceShortName)) != 0)
			{
				System.out.println(
					"Error trying to uninstall service ("
						+ sServiceShortName
						+ "), errorcode;"
						+ status);
				return status;
			}
			
			// now install the service
			if( (status = install(sServiceShortName,sServiceFullName,sEXEPath,startType) )
					!= 0)
			{
				if (status == JNTServices.ERROR_SERVICE_MARKED_FOR_DELETE)
				{
					// Wait for it to be deleted
					int iLoops = 0;
					while ((status = this.uninstall(sServiceShortName))
						== JNTServices.ERROR_SERVICE_MARKED_FOR_DELETE
						&& iLoops < iRetries)
					{
						System.out.println(
							"Wating for the NT Service("
								+ sServiceShortName
								+ ") to be deleted.");
						try
						{
							Thread.sleep(1000);
						}
						catch (Exception exc)
						{
							; // Do nothing
						}
						iLoops++;
					}
					System.out.println("status=" + status);
					// try to install it again.
					if ((status =
						this.install(
							sServiceShortName,
							sServiceFullName,
							sEXEPath,
							startType))
						!= 0)
					{
						System.out.println(
							"Error trying to install service ("
								+ sServiceShortName
								+ "), errorcode;"
								+ status);
						return status;
					}
				}
				else
				{
					System.out.println(
						"Error trying to install service ("
							+ sServiceShortName
							+ "), errorcode;"
							+ status);
					return status;
				}
			}
		}
		else
		{
			if (status != 0)
			{
				System.out.println(
					"Error trying to install NT Service '"
						+ sServiceShortName
						+ "' errorcode="
						+ status);
				return status;
			}
		}
		return status;
	}

	/**
	 * This is the same method as justInstall(,,,) but here the short name is used also for the full name.
	 *
	 * @param sServiceShortName This is the short name of the service,
	 * @param sServiceShortName This is the Full name of the service, as it is displayed in the ControlPanel applet for service.
	 * @param sEXEPath This is the executable to be run.
	 * @param startType This is the type of start up the process will have, it can have any of the ones below, preferrably 2 and 3, as 0 and 1 are for drivers only.
	 * @return It will return !=0 if there was an error, 0 on success. Note that the possible error codes are the same as for install, stop and uninstall above, it will return -1 if any of the argument are null.
	 */
	public int justInstall(
		String sServiceShortName,
		String sEXEPath,
		int iStartType)
	{
		return this.justInstall(
			sServiceShortName,
			sServiceShortName,
			sEXEPath,
			iStartType);
	}

	/**
	 * This method queries the NT Services for the status of a specific service
	 *
	 * @param sServiceShortName This is the short name of the NT Service
	 * @return It will return any of the following values
	 * <ul>
	 * <li>SERVICE_STOPPED                1
	 * <li>SERVICE_START_PENDING          2
	 * <li>SERVICE_STOP_PENDING           3
	 * <li>SERVICE_RUNNING                4
	 * <li>SERVICE_CONTINUE_PENDING       5
	 * <li>SERVICE_PAUSE_PENDING          6
	 * <li>SERVICE_PAUSED                 7
	 * </ul>
	 * Or it will return the error code multiplied by -1, e.g. if it should return error code ERROR_ACCESS_DENIED (5) it would return -5.
	 */
	public native int getStatus(String sServiceShortName, String machName);

	public int getStatus(String sServiceShortName)
	{
		return getStatus(sServiceShortName, getMachineName());
	}

	/**
	 * This method returns all the servies found in the Windows NT Services manager
	 * It will return an array of strings
	 * @returns an array of String objects with the services names in
	 */
	public native String[] getAllServices(String machineName);

	public String[] getAllServices()
	{
		return getAllServices( getMachineName() );
	}

	/**
	 * This main method can test all the methods provided, i.e.
	 * instal,uninstall, start and stop a Windows NT Service.
	 *
	 * @param args is an array of argument given on the command line. This can be "[-start service | -stop service |-install service exe | -uninstall service"
	 *
	 */
	public static void main(String args[])
	{
		JNTServices nts = new JNTServices();
		int result = 0;
		if (args.length <= 0)
		{
			System.out.println(
				"Usage; prg [-start <service> | -stop <service> |-install <service> <exe> | -uninstall <service>");
			return;
		}
		if (args[0].compareToIgnoreCase("-start") == 0)
		{
			result = nts.start(args[1], 0, null);
		}
		else
		{
			if (args[0].compareToIgnoreCase("-stop") == 0)
			{
				result = nts.stop(args[1], 3, 10000);
			}
			else
			{
				if (args[0].compareToIgnoreCase("-install") == 0)
				{
					result =
						nts.install(
							args[1],
							args[1],
							args[2],
							JNTServices.SERVICE_DEMAND_START);
				}
				else
				{
					if (args[0].compareToIgnoreCase("-uninstall") == 0)
					{
						result = nts.uninstall(args[1]);
					}
					else
					{
						if (args[0].compareToIgnoreCase("-list") == 0)
						{
							String aServices[] = nts.getAllServices();
							System.out.println("The following NT Services exists;");
							int index = aServices.length - 1;
							while (index >= 0)
							{
								System.out.println(index + ": " + aServices[index]);
								index--;
							}
						}
						else
						{
							System.out.println(
								"Error; " + args[0] + " is not a valid parameter.");
						}
					}
				}
			}
			System.out.println("Status after " + args[0] + " was " + result);
		}
	}
}
