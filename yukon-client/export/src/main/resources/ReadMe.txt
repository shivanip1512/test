//-------------------------------------------------------------------------------------------------------------//
Yukon Export Custom Application.
An attempt to make Export more understandable for all users...an attempt!

SNebben, April 2003
Cannon Technologies, Inc.
//-------------------------------------------------------------------------------------------------------------//

Export.jar is a standalone java application that may be run :
1.  As a user interface with main clas com.cannontech.export.gui.ExportGui.java.
    1a. (default) Start up as a normal java application using 
        cd C:\Yukon\Runtime\bin
        java -jar C:\Yukon\Client\bin\export.jar
        The default startup will include ONLY formats 0 and 1 as export options.

    1b. Start up as a normal java application using 
        cd C:Yukon\Runtime\bin
        java -jar C:\Yukon\Client\bin\export.jar #
        The sharp sign(#) represents the number of format id you wish to use.
        You may choose for multiple formats to be options simply by separating the formatids by spaces
        as in # # #. (eg. 0 3 4 ).

2.  From command line using main class com.cannontech.export.ExportFormatBase.java
    - cd C:\Yukon\Runtime\bin
    - java -jar C:\Yukon\Client\bin\wrapper.jar -[c/i/r] C:\Yukon\Client\bin\[*].conf

//-------------------------------------------------------------------------------------------------------------//
Valid FORMATIDs (com.cannontech.export.ExportFormatTypes.java):
CSVBILLING_FORMAT = 0          //csv billing (default for Xcel EBB).
DBPURGE_FORMAT = 1             //system log database purge (default for We-Energies dbpurge).
IONEVENTLOG_FORMAT = 2         //system log export of ion events (default for Duke).
LMCTRLHIST_EXPORT_FORMAT = 3   //lm control history export (default for Burt Co. - runs at Burt Co.).
LMCTRLHIST_IMPORT_FORMAT = 4   //lm control history import (default for Burt Co. - runs at USI (Cannon)).

//-------------------------------------------------------------------------------------------------------------//
About Yukon Export:
- The user interface allows for selection of an export format (pending gui startup procedure selected from above)
  and gives 3 options for running: (1)run one time, (2)run continually in console mode, (3)or run continually
  as an NT Service.  With the NT Service option, the user is then given options for install/remove and 
  start/stop of the service.
  
  About options for running (with the press of the 'Generate' button):
  (1) - Generates and exports the file one time using the parameters selected in the Export gui.
  (2) - Creates a batch file that starts the application as a command line program and uses a *.dat file the
        GUI builds (based on the parameters selected from it) and continually runs the export application
        in console mode based on the time interval entered in the GUI's Run Interval text box.  The Export gui
        will be shut down when the generated batch file starts running.
        The wrapper.jar utility is used here with it's console parameter (-c) and it's respective *.conf file
        for the export format selected.
  (3) - Creates a batch file that installs/removes, starts/stops the Export application as an NT Service.  The
        batch file is generated and uses a *.dat file the GUI builds (based on the parameters selected from it)
        and continually runs (if installed and started) the export application based on the time interval entered
        in the GUI's Run Interval text box.  The Export gui will be shut down when the generated batch file starts
        running.
        The wrapper.jar utility is used here with it's install/remove parameter (-i/-r) and it's respective *.conf
        file for the export format selected.
       
- Depending on the format selected, advanced options may exist.  These options are specific to each format
  and can be viewed and set by pressing the 'Advanced...' button.
  
- If run option (2) or (3) is selected, the user may modify the Run Interval (in minutes) parameter.
  (This is the same parameter as INT listed in (some) *.dat files below).
  
- The export directory is edittable for all export formats. (This is the same parameter as DIR listed in the 
  *.dat files below).  The 'Browse' button may be used to select the export directory.
  
- The option to append to a file is also choosable for all export formats.

- The fore mentioned *.conf files are supplied with Yukon and may be found in \yukon\client\bin\.
  csvwrapper.conf       // CSVBILLING_FORMAT
  dbwrapper.confg       // DBPURGE_FORMAT
  ionwrapper.conf       // IONEVENTLOG_FORMAT
  lmchewrapper.conf     // LMCTRLHIST_EXPORT_FORMAT
  lmchiwrapper.conf     // LMCTRLHIST_IMPORT_FORMAT

- The fore mentioned *.dat files 
  - Property files created by the Export GUI or by an advanced user.  Must be present in \yukon\client\config\.
  
  - CSVBilling - csvprop.dat
    FORMAT=0                                                //DO NOT EDIT
    DIR=c:\yukon\client\export\                             //The directory to export to (filename is dynamic based on input data).
    (DECREMENTED)ENERGYFILE=c:/yukon/client/config/EnergyNumbers.txt     //A file containing <customernumber>|<energydebtornum>|<energypremisenum>.
    START=03/16/2003                                        //Minimum date to accept valid data.
    STOP=03/17/2003                                         //Maximum date to accept valid data.
    DELIMITER=|                                             //Output data seperator. (default '|')
    HEADINGS=false                                          //Display the headings row.

  - DBPurge - dbprop.dat
    FORMAT=1                                                //DO NOT EDIT
    DIR=c:\yukon\client\export\                             //The directory to export to (filename is dynamic based on input data).
    DAYS=90                                                 //The number of days to retain in systemlog.
    HOUR=1                                                  //The hour (0-23) to run at.
    PURGE=false                                             //Purge the system log after reporting on it.

  - IONEventLog - ionprop.dat
    FORMAT=2                                                //DO NOT EDIT
    DIR=c:\yukon\client\export\                             //The directory to export to.
    FILE=ioneventlog.csv                                    //The filename to write data to.
    INT=1440                                                //The run time interval (in minutes).
    APPEND=false                                            //Append to the file if it already exists.

  - LMCtrlHistExport - lmcheprop.dat
    FORMAT=3                                                //DO NOT EDIT
    DIR=c:\yukon\client\export\                             //The directory to export to.
    FILE=lmctrlhist.csv                                     //The filename to write data to.
    INT=60                                                  //The run time interval (in minutes).

  - LMCtrlHistImport - lmchiprop.dat
    FORMAT=4                                                //DO NOT EDIT
    DIR=c:\yukon\client\export\                             //The directory to export to.
    FILE=lmctrlhistEx.csv                                   //The filename to write data to.
    IMPDIR=c:\yukon\client\export\                          //The directory to read the import file from.
    IMPFILE=lmctrlhist.csv                                  //The filename to read for importing data.
    INT=1440                                                //The run time interval (in minutes).