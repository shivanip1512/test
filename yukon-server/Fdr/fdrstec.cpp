/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrstec.cpp
*
*    DATE: 05/31/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrstec.cpp-arc  $
*    REVISION     :  $Revision: 1.5 $
*    DATE         :  $Date: 2005/02/10 23:23:51 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Stec file interface
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrstec.cpp,v $
      Revision 1.5  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.4  2004/09/29 17:47:48  dsutton
      Updated all interfaces to default the db reload rate to once a day (86400)

      Revision 1.3  2002/04/16 15:58:38  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:58  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

   
      Rev 2.6   01 Mar 2002 13:30:48   dsutton
   added link status calls updated everytime the interface attempts to retrieve the file
   
      Rev 2.5   18 Feb 2002 16:19:38   dsutton
   added a cparm for ftp download location so we can run as a service
   
      Rev 2.4   15 Feb 2002 11:22:12   dsutton
    changed the debug settings for a few of the transactions to make them more uniform throughout fdr
   
      Rev 2.3   11 Feb 2002 15:03:40   dsutton
   added event logs when the connection is established or failed, unknown points, invalid states, etc
   
      Rev 2.2   14 Dec 2001 17:17:44   dsutton
   the functions that load the lists of points noware updating point managers instead of creating separate lists of their own.  Hopefully this is easier to follow
   
      Rev 2.1   15 Nov 2001 16:16:40   dsutton
   code for multipliers and an queue for the messages to dispatch along with fixes to RCCS/INET interface. Lazy checkin
   
      Rev 2.0   06 Sep 2001 13:21:36   cplender
   Promote revision
   
      Rev 1.1   20 Jul 2001 10:02:48   dsutton
   timeouts taking an hour
   
      Rev 1.0   04 Jun 2001 09:33:14   dsutton
   Initial revision.
   
      Rev 1.1   10 May 2001 11:12:12   dsutton
   updated with new socket classes
   
      Rev 1.0   23 Apr 2001 11:17:58   dsutton
   Initial revision.
*
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/
#include "yukon.h"


#include <windows.h>
#include <wininet.h>
#include <fcntl.h>
#include <io.h>

/** include files **/
#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "cparms.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "message.h"
#include "msg_reg.h"
#include "msg_ptreg.h"
#include "msg_pdata.h"
#include "msg_signal.h"
#include "connection.h"
#include "pointtypes.h"

#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrstec.h"


CtiFDR_STEC * stecInterface;

const CHAR * CtiFDR_STEC::KEY_PORT_NUMBER = "FDR_STEC_PORT_NUMBER";
const CHAR * CtiFDR_STEC::KEY_TRIES = "FDR_STEC_NUMBER_OF_TRIES";
const CHAR * CtiFDR_STEC::KEY_INTERVAL = "FDR_STEC_DOWNLOAD_INTERVAL";
const CHAR * CtiFDR_STEC::KEY_IP_ADDRESS = "FDR_STEC_IP_ADDRESS";
const CHAR * CtiFDR_STEC::KEY_PASSWORD = "FDR_STEC_PASSWORD";
const CHAR * CtiFDR_STEC::KEY_LOGIN = "FDR_STEC_LOGIN";
const CHAR * CtiFDR_STEC::KEY_SERVER_FILENAME = "FDR_STEC_SERVER_FILE";
const CHAR * CtiFDR_STEC::KEY_DB_RELOAD_RATE = "FDR_STEC_DB_RELOAD_RATE";
const CHAR * CtiFDR_STEC::KEY_QUEUE_FLUSH_RATE = "FDR_STEC_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_STEC::KEY_FTP_DIRECTORY = "FDR_STEC_FTP_DIRECTORY";

const CHAR * CtiFDR_STEC::KEY_SYSTEM_TOTAL_LABEL = "SYSTEM LOAD";
const CHAR * CtiFDR_STEC::KEY_STEC_TOTAL_LABEL = "STEC LOAD" ;


// Constructors, Destructor, and Operators
CtiFDR_STEC::CtiFDR_STEC()
: CtiFDRFtpInterface(RWCString("STEC"))
{   
    init();
}


CtiFDR_STEC::~CtiFDR_STEC()
{
}

/*************************************************
* Function Name: CtiFDR_STEC::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDR_STEC::init( void )
{
    // init the base class
    Inherited::init();    

    if (!readConfig( ))
    {
        return FALSE;
    }

    loadTranslationLists();
    
    return TRUE;
}

/*************************************************
* Function Name: CtiFDR_STEC::run()
*
* Description: runs the interface
* 
**************************************************
*/
BOOL CtiFDR_STEC::run( void )
{

    // crank up the base class
    Inherited::run();
    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_STEC::stop()
*
* Description: stops all threads 
* 
**************************************************
*/
BOOL CtiFDR_STEC::stop( void )
{
    Inherited::stop();
    return TRUE;
}

bool CtiFDR_STEC::sendMessageToForeignSys ( CtiMessage *aMessage )
{
    // message is deleted in fdrinterface thread
    return TRUE;
}



INT CtiFDR_STEC::processMessageFromForeignSystem (CHAR *data)
{
    return NORMAL;
}

int CtiFDR_STEC::decodeFile ()
{
    int fileHandle;
    FILE *controlFile;
    int retVal = NORMAL;
    FLOAT totalSystem=0.0, stecOnly=0.0;

    CHAR buffer[300];
    int lineNumber=0, cnt;
    CHAR *ptr, *token=NULL;
    RWDate date;
    RWTime finalTime;
    RWCString           desc;
    RWCString           action;

    if ((fileHandle = _open(getLocalFileName().data(), _O_TEXT|_O_RDONLY)) != -1)
    {
        if (filelength(fileHandle) > 290 || filelength(fileHandle) < 70)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " STEC file failed number of bytes reasonability check -- " << filelength(fileHandle) << endl;
            }
            desc = getInterfaceName() + RWCString ("'s data file ") + getLocalFileName() + RWCString(" failed size reasonability check");
            logEvent (desc, action);

            retVal = !NORMAL;
        }
        else
        {

            // first of all, read file input 
            if ((controlFile = fopen(getLocalFileName().data(), "r")) == NULL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " STEC file failed to open after download " << endl;
                retVal = !NORMAL;
            }
            else
            {
                // read in the input lines
                while( fgets( (char*) buffer, 300, controlFile) != NULL )
                {
                   switch (lineNumber)
                   {
                       case 0:
                           {
                               // date
                               if (strlen (buffer) > 13)
                               {
                                   date = RWDate (atoi(buffer+9), atoi (buffer+6), (atoi(buffer+12)+2000));
                               }
                               break;
                           }
                       case 2:
                           {
                               // time
                               if (strlen (buffer) > 13)
                               {
                                   finalTime = RWTime (date,
                                                     atoi(buffer+6), 
                                                     atoi (buffer+9), 
                                                     atoi(buffer+12));

                               }
                               break;
                           }

                       case 4:
                           {
                               // this is the total system load
                               for (cnt=0; cnt < strlen(buffer); cnt++)
                               {
                                   // loop until we find a digit
                                   if (isdigit(buffer[cnt]))
                                   {
                                       ptr = &buffer[cnt];
                                       token = strtok (ptr," ");
                                       break;
                                   }
                               }

                               // now we need a decimal from a string
                               if (token != NULL)
                               {
                                    totalSystem= atof (token);     
                               }

                               break;
                           }
                       case 6:
                           {
                               // this is the stec system load
                               for (cnt=0; cnt < strlen(buffer); cnt++)
                               {
                                   // loop until we find a digit
                                   if (isdigit(buffer[cnt]))
                                   {
                                       ptr = &buffer[cnt];
                                       token = strtok (ptr," ");
                                       break;
                                   }
                               }

                               // now we need a decimal from a string
                               if (token != NULL)
                               {
                                   stecOnly = atof (token);     
                               }

                               break;
                           }
                       default:
                           break;
                   }

                   lineNumber++;
                }

                fclose (controlFile);

                retVal = sendToDispatch(finalTime, totalSystem, stecOnly);
                sendLinkState (FDR_CONNECTED);

            }
        }
        _close(fileHandle);				
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " STEC file failed to open after download " << endl;
        retVal = !NORMAL;
    }


    return NORMAL;
}


int CtiFDR_STEC::fail ()
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    RWCString           translationName;
    CtiFDRPoint         point;
    bool                 flag = true;
    RWCString desc,action;

    sendLinkState (FDR_NOT_CONNECTED);
    desc = getInterfaceName() + " failed to retrieve a new data file";
    logEvent (desc, action, true);

    // see if the point exists
    flag = findTranslationNameInList (RWCString (KEY_SYSTEM_TOTAL_LABEL), getReceiveFromList(), point);

    if (flag == true)
    {
        CtiCommandMsg *pMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if (pMsg != NULL)
        {
            pMsg->insert( -1 );			  // This is the dispatch token and is unimplemented at this time
            pMsg->insert(OP_POINTID);	  // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
            pMsg->insert(point.getPointID());			 // The id (device or point which failed)
            pMsg->insert(ScanRateGeneral);		// One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

            // consumes a delete memory
            queueMessageToDispatch(pMsg);
        }
    }

    flag = findTranslationNameInList (RWCString (KEY_STEC_TOTAL_LABEL), getReceiveFromList(), point);

    if (flag == true)
    {
        CtiCommandMsg *pMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if (pMsg != NULL)
        {
            pMsg->insert( -1 );			  // This is the dispatch token and is unimplemented at this time
            pMsg->insert(OP_POINTID);	  // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
            pMsg->insert(point.getPointID());			 // The id (device or point which failed)
            pMsg->insert(ScanRateGeneral);		// One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

            // consumes a delete memory
            queueMessageToDispatch(pMsg);
        }
    }
    return retVal;
}

int CtiFDR_STEC::sendToDispatch(RWTime aTime, FLOAT aSystemLoad, FLOAT aStecLoad)
{
    int retVal = NORMAL;
    RWCString           translationName;
    CtiFDRPoint         point;
    bool                 flag = true;

    // see if the point exists
    flag = findTranslationNameInList (RWCString (KEY_SYSTEM_TOTAL_LABEL), getReceiveFromList(), point);


    // overkill but thorough
    if ((flag == true) &&
        ((point.getPointType() == AnalogPointType) ||
         (point.getPointType() == PulseAccumulatorPointType) ||
         (point.getPointType() == DemandAccumulatorPointType) ||
         (point.getPointType() == CalculatedPointType)))

    {
        // assign last stuff	
        if (aTime != rwEpoch)
        {
            // system load should not be zero !!!
            if (aSystemLoad != 0)
            {
                aSystemLoad *= point.getMultiplier();
                aSystemLoad += point.getOffset();

                CtiPointDataMsg     *pData = new CtiPointDataMsg(point.getPointID(), 
                                            aSystemLoad, 
                                            NormalQuality, 
                                            AnalogPointType);

                pData->setTime(aTime);
                // consumes a delete memory
                queueMessageToDispatch(pData);

                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " --- Stec Interface: System Load " << aSystemLoad << endl;
                }

            }
            else
            {
                CtiCommandMsg *pCmdMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

                pCmdMsg->insert( -1 );                          // This is the dispatch token and is unimplemented at this time
                pCmdMsg->insert(OP_POINTID);                    // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                pCmdMsg->insert(point.getPointID());  // The id (device or point which failed)
                pCmdMsg->insert(ScanRateGeneral);               // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
                queueMessageToDispatch(pCmdMsg);

                if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " --- Stec Interface: System load is zero, marking as invalid" << endl;
                }
            }
        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Translation for analog point " << RWCString (KEY_SYSTEM_TOTAL_LABEL);
                dout << " from " << getInterfaceName() << " was not found" << endl;
            }
        }
        else
        {         
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Analog point " << RWCString (KEY_SYSTEM_TOTAL_LABEL);
            dout << " from " << getInterfaceName() << " was mapped incorrectly to non-analog point " << point.getPointID() << endl;
        }
    }

    flag = findTranslationNameInList (RWCString (KEY_STEC_TOTAL_LABEL), getReceiveFromList(), point);

    // overkill but thorough
    if ((flag == true) &&
        ((point.getPointType() == AnalogPointType) ||
         (point.getPointType() == PulseAccumulatorPointType) ||
         (point.getPointType() == DemandAccumulatorPointType) ||
         (point.getPointType() == CalculatedPointType)))
    {
        // assign last stuff	
        if (aTime != rwEpoch)
        {
            if (aStecLoad != 0)
            {
                aStecLoad *= point.getMultiplier();
                aStecLoad += point.getOffset();

                CtiPointDataMsg     *pData = new CtiPointDataMsg(point.getPointID(), 
                                            aStecLoad, 
                                            NormalQuality, 
                                            AnalogPointType);

                pData->setTime(aTime);

                // consumes a delete memory
                queueMessageToDispatch(pData);

                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " --- Stec Interface: Stec Load " << aStecLoad << endl;
                }

            }
            else
            {
                CtiCommandMsg *pCmdMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

                pCmdMsg->insert( -1 );                          // This is the dispatch token and is unimplemented at this time
                pCmdMsg->insert(OP_POINTID);                    // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                pCmdMsg->insert(point.getPointID());  // The id (device or point which failed)
                pCmdMsg->insert(ScanRateGeneral);               // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
                queueMessageToDispatch(pCmdMsg);

                if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " --- Stec Interface: Stec load is zero, marking as invalid" << endl;
                }
            }
        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Translation for analog point " << RWCString (KEY_STEC_TOTAL_LABEL);
                dout << " from " << getInterfaceName() << " was not found" << endl;
            }
        }
        else
        {         
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Analog point " << RWCString (KEY_STEC_TOTAL_LABEL);
            dout << " from " << getInterfaceName() << " was mapped incorrectly to non-analog point " << point.getPointID() << endl;
        }
    }

    return retVal;
}

int CtiFDR_STEC::readConfig( void )
{    
    int         successful = TRUE;
    RWCString   tempStr;

    tempStr = getCparmValueAsString(KEY_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setPort(atoi(tempStr));
    }
    else
    {
        setPort (INTERNET_DEFAULT_FTP_PORT);
    }

    tempStr = getCparmValueAsString(KEY_INTERVAL);
    if (tempStr.length() > 0)
    {
        setDownloadInterval(atoi(tempStr));
    }
    else
    {
        setDownloadInterval(900);
    }

    tempStr = getCparmValueAsString(KEY_SERVER_FILENAME);
    if (tempStr.length() > 0)
    {
        setServerFileName(tempStr);
    }
    else
    {
        setServerFileName(RWCString ("Load.txt"));
    }

    tempStr = getCparmValueAsString(KEY_FTP_DIRECTORY);
    if (tempStr.length() > 0)
    {
        setFTPDirectory(tempStr);
    }
    else
    {
        setFTPDirectory(RWCString ("\\yukon\\server\\import"));
    }

    tempStr = getCparmValueAsString(KEY_LOGIN);
    if (tempStr.length() > 0)
    {
        setLogin(tempStr);
    }
    else
    {
        setLogin(RWCString ("anonymous"));
    }

    tempStr = getCparmValueAsString(KEY_PASSWORD);
    if (tempStr.length() > 0)
    {
        setPassword(tempStr);
    }
    else
    {
        setPassword(RWCString ("YukonServerPlatform@wharton.com"));
    }

    tempStr = getCparmValueAsString(KEY_IP_ADDRESS);
    if (tempStr.length() > 0)
    {
        setIPAddress(tempStr);
    }
    else
    {
        setIPAddress(RWCString());
		  successful = false;
    }


    tempStr = getCparmValueAsString(KEY_TRIES);
    if (tempStr.length() > 0)
    {
        setTries(atoi(tempStr));
    }
    else
    {
        setTries (3);
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr));
    }
    else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr));
    }
    else
    {
        // default to one second for stec, its only 2 points
        setQueueFlushRate (1);
    }

    // default filename
    setLocalFileName(RWCString ("stec1.txt"));


    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " STEC server file name " << getServerFileName() << endl;
        dout << RWTime() << " STEC FTP directory " << getFTPDirectory() << endl;
        dout << RWTime() << " STEC download interval " << getDownloadInterval() << endl;
        dout << RWTime() << " STEC number of tries " << getTries() << endl;
        dout << RWTime() << " STEC login " << getLogin() << endl;
        dout << RWTime() << " STEC IP " << getIPAddress() << endl;
        dout << RWTime() << " STEC db reload rate " << getReloadRate() << endl;
    }


    return successful;
}

/****************************************************************************************
*
*      Here Starts some C functions that are used to Start the 
*      Interface and Stop it from the Main() of FDR.EXE.  
*
*/

#ifdef __cplusplus
extern "C" {
#endif

/************************************************************************
* Function Name: Extern C int RunInterface(void)
*
* Description: This is used to Start the Interface from the Main() 
*              of FDR.EXE. Each interface it Dynamicly loaded and 
*              this function creates a global FDRCygnet Object and then
*              calls its run method to cank it up.
* 
*************************************************************************
*/

    DLLEXPORT int RunInterface(void)
    {

        // make a point to the interface
        stecInterface = new CtiFDR_STEC();

        // now start it up
        return stecInterface->run();
    }

/************************************************************************
* Function Name: Extern C int StopInterface(void)
*
* Description: This is used to Stop the Interface from the Main() 
*              of FDR.EXE. Each interface it Dynamicly loaded and 
*              this function stops a global FDRCygnet Object and then
*              deletes it.
* 
*************************************************************************
*/
    DLLEXPORT int StopInterface( void )
    {

        stecInterface->stop();
        delete stecInterface;
        stecInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif

