/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrdsm2import.cpp
*
*    DATE: 05/31/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrdsm2import.cpp-arc  $
*    REVISION     :  $Revision: 1.6 $
*    DATE         :  $Date: 2005/02/10 23:23:50 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Dsm2 ascii import
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrdsm2import.cpp,v $
      Revision 1.6  2005/02/10 23:23:50  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.5  2004/09/29 17:47:47  dsutton
      Updated all interfaces to default the db reload rate to once a day (86400)

      Revision 1.4  2003/10/20 20:28:17  dsutton
      Bug:  The application wasn't locking the point list down before looking for a
      point.  The database reload does lock the list down before reloading.  If the
      lookup and the reload happened at the same time,  the lookup lost and threw
      and exception

      Revision 1.3  2002/04/16 15:58:32  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:55  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

   
      Rev 1.1   10 Apr 2002 16:34:10   dsutton
   no longer setting the time when creating the point data msg because the only
   person using this, san bernard, is sending data from ilex that reads 1998
   
      Rev 1.0   12 Mar 2002 10:36:18   dsutton
   Initial revision.
   
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
#include "fdrdsm2import.h"


CtiFDR_Dsm2Import * dsm2Interface;

const CHAR * CtiFDR_Dsm2Import::KEY_INTERVAL = "FDR_DSM2IMPORT_INTERVAL";
const CHAR * CtiFDR_Dsm2Import::KEY_FILENAME = "FDR_DSM2IMPORT_FILENAME";
const CHAR * CtiFDR_Dsm2Import::KEY_DRIVE_AND_PATH = "FDR_DSM2IMPORT_DRIVE_AND_PATH";
const CHAR * CtiFDR_Dsm2Import::KEY_DB_RELOAD_RATE = "FDR_DSM2IMPORT_DB_RELOAD_RATE";
const CHAR * CtiFDR_Dsm2Import::KEY_QUEUE_FLUSH_RATE = "FDR_DSM2IMPORT_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_Dsm2Import::KEY_DELETE_FILE = "FDR_DSM2IMPORT_DELETE_FILE";


// Constructors, Destructor, and Operators
CtiFDR_Dsm2Import::CtiFDR_Dsm2Import()
: CtiFDRAsciiImportBase(RWCString("DSM2IMPORT"))
{   
    init();
}

CtiFDR_Dsm2Import::~CtiFDR_Dsm2Import()
{
}

/*************************************************
* Function Name: CtiFDR_Dsm2Import::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDR_Dsm2Import::init( void )
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
* Function Name: CtiFDR_Dsm2Import::run()
*
* Description: runs the interface
* 
**************************************************
*/
BOOL CtiFDR_Dsm2Import::run( void )
{

    // crank up the base class
    Inherited::run();
    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_Dsm2Import::stop()
*
* Description: stops all threads 
* 
**************************************************
*/
BOOL CtiFDR_Dsm2Import::stop( void )
{
    Inherited::stop();
    return TRUE;
}

bool CtiFDR_Dsm2Import::sendMessageToForeignSys ( CtiMessage *aMessage )
{
    // message is deleted in fdrinterface thread
    return TRUE;
}

INT CtiFDR_Dsm2Import::processMessageFromForeignSystem (CHAR *data)
{
    return NORMAL;
}

RWTime CtiFDR_Dsm2Import::Dsm2ToYukonTime (RWCString aTime)
{
    struct tm ts;

    if (sscanf (aTime.data(),
                "%4ld%2ld%2ld%2ld%2ld%2ld",
                &ts.tm_year,
                &ts.tm_mon,
                &ts.tm_mday,
                &ts.tm_hour,
                &ts.tm_min,
                &ts.tm_sec) != 6)
    {
        return(RWTime(rwEpoch));
    }

    ts.tm_year -= 1900;
    ts.tm_mon--;

    if (aTime.data()[14] == 'D' ||
        aTime.data()[14] == 'd')
    {
        ts.tm_isdst = TRUE;
    }
    else
    {
        ts.tm_isdst = FALSE;
    }

    RWTime returnTime(&ts);

    // if RWTime can't make a time ???
    if (!returnTime.isValid())
    {
        return(RWTime(rwEpoch));
    }

    return returnTime;
}

USHORT CtiFDR_Dsm2Import::Dsm2ToYukonQuality (CHAR aQuality)
{
    USHORT Quality = NormalQuality;

    // Test for the various dsm2 qualities
    if (aQuality == 'I')
        Quality = InvalidQuality;

    if (aQuality == 'U')
        Quality = AbnormalQuality;

    if (aQuality == '#')
        Quality = NonUpdatedQuality;

    if (aQuality == 'M')
        Quality = ManualQuality;

    if (aQuality == 'O')
        Quality = UnknownQuality;

	return(Quality);
}

bool CtiFDR_Dsm2Import::validateAndDecodeLine (RWCString &aLine, CtiMessage **retMsg)
{
	bool retCode = false;
    bool flag;
    aLine.toLower();
    RWCString tempString1;                // Will receive each token
    RWCTokenizer cmdLine(aLine);           // Tokenize the string a
    CtiFDRPoint         point;
    CHAR action[200];
    RWCString desc;

    // do we have an of these
    if (!(tempString1 = cmdLine(",\r\n")).isNull())
    {
        {
            CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());  
            flag = findTranslationNameInList (tempString1, getReceiveFromList(), point);
        }

        RWCString translationName=tempString1;

        if (flag == true)
        {
            // now
            tempString1 = cmdLine(",\r\n");
            // device name
            tempString1 = cmdLine(",\r\n");
            // point name
            tempString1 = cmdLine(",\r\n");

            // value
            if (!(tempString1 = cmdLine(",\r\n")).isNull())
            {
                float value = atof (tempString1.data());

                // quality
                if (!(tempString1 = cmdLine(",\r\n")).isNull())
                {
                    int quality = Dsm2ToYukonQuality (tempString1.data()[0]);

                    // timestamp
                    if (!(tempString1 = cmdLine(",\r\n")).isNull())
                    {
                        RWTime timestamp = Dsm2ToYukonTime (tempString1);

                        if (timestamp != rwEpoch)
                        {
                            // figure out what it should be now
                            switch (point.getPointType())
                            {
                                case AnalogPointType:
                                case PulseAccumulatorPointType:
                                case DemandAccumulatorPointType:
                                case CalculatedPointType:
                                {
                                    *retMsg = new CtiPointDataMsg(point.getPointID(), 
                                                                value, 
                                                                quality, 
                                                                AnalogPointType);
//                                    ((CtiPointDataMsg *)*retMsg)->setTime(timestamp);

                                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Analog point " << translationName;
                                        dout << " value " << value << " from " << getInterfaceName() << " assigned to point " << point.getPointID() << endl;;
                                    }
                                    retCode = true;
                                    break;
                                }
                                case StatusPointType:
                                {

                                    // check for control functions
                                    if (point.isControllable())
                                    {
                                        int controlState=-1; 

                                        // make sure the value is valid
                                        if (value == Dsm2_Open)
                                        {
                                            controlState = OPENED;
                                        } 
                                        else if (value == Dsm2_Closed)
                                        {
                                            controlState = CLOSED;
                                        }
                                        else          
                                        {
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << RWTime() << " Invalid control state " << value;
                                                dout << " for " << translationName << " received from " << getInterfaceName() << endl;
                                            }
                                            CHAR state[20];
                                            _snprintf (state,20,"%.0f",value);
                                            desc = getInterfaceName() + RWCString (" control point received with an invalid state ") + RWCString (state);
                                            _snprintf(action,60,"%s for pointID %d", 
                                                      translationName,
                                                      point.getPointID());
                                            logEvent (desc,RWCString (action));
                                        }

                                        if (controlState != -1)
                                        {
                                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << RWTime() << " Control point " << translationName;
                                                if (controlState == OPENED)
                                                {
                                                    dout << " control: Open " ;
                                                }
                                                else
                                                {
                                                    dout << " control: Closed " ;
                                                }

                                                dout <<" from " << getInterfaceName() << " and processed for point " << point.getPointID() << endl;;
                                            }

                                            // build the command message and send the control
                                            *retMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);

                                            ((CtiCommandMsg *)*retMsg)->insert( -1 );                // This is the dispatch token and is unimplemented at this time
                                            ((CtiCommandMsg *)*retMsg)->insert(0);                   // device id, unknown at this point, dispatch will find it
                                            ((CtiCommandMsg *)*retMsg)->insert(point.getPointID());  // point for control
                                            ((CtiCommandMsg *)*retMsg)->insert(controlState);       
                                            retCode = true;
                                        }
                                    }
                                    else
                                    {
                                        int yukonValue;
                                        RWCString traceState;
                                        // assign last stuff	
                                        switch ((int)value)
                                        {
                                            case Dsm2_Open:
                                                yukonValue = OPENED;
                                                traceState = RWCString("Opened");
                                                break;
                                            case Dsm2_Closed:
                                                yukonValue = CLOSED;
                                                traceState = RWCString("Closed");
                                                break;
                                            case Dsm2_Indeterminate:
                                                yukonValue = INDETERMINATE;
                                                traceState = RWCString("Indeterminate");
                                                break;
                                            case Dsm2_State_Four:
                                                yukonValue = STATEFOUR;
                                                traceState = RWCString("State Four");
                                                break;
                                            case Dsm2_State_Five:
                                                yukonValue = STATEFIVE;
                                                traceState = RWCString("State Five");
                                                break;
                                            case Dsm2_State_Six:
                                                yukonValue = STATESIX;
                                                traceState = RWCString("State Six");
                                                break;
                                            case Dsm2_Invalid:
                                            default:
                                                {
                                                    yukonValue = INVALID;
                                                }
                                        }

                                        if (yukonValue == INVALID)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Status point " << translationName;
                                            dout << " received an invalid state " << (int)value;
                                            dout <<" from " << getInterfaceName() << " for point " << point.getPointID() << endl;;

                                            CHAR state[20];
                                            _snprintf (state,20,"%.0f",value);
                                            desc = getInterfaceName() + RWCString (" status point received with an invalid state ") + RWCString (state);
                                            _snprintf(action,60,"%s for pointID %d", 
                                                      translationName,
                                                      point.getPointID());
                                            logEvent (desc,RWCString (action));

                                        }
                                        else
                                        {
                                            *retMsg = new CtiPointDataMsg(point.getPointID(), 
                                                                        yukonValue, 
                                                                        quality, 
                                                                        StatusPointType);
//                                            ((CtiPointDataMsg*)*retMsg)->setTime(timestamp);
                                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << RWTime() << " Status point " << translationName;
                                                dout << " new state: " << traceState;
                                                dout <<" from " << getInterfaceName() << " assigned to point " << point.getPointID() << endl;;
                                            }
                                            retCode = true;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    return retCode;
}



int CtiFDR_Dsm2Import::readConfig( void )
{    
    int         successful = TRUE;
    RWCString   tempStr;

    tempStr = getCparmValueAsString(KEY_INTERVAL);
    if (tempStr.length() > 0)
    {
        if (atoi (tempStr) <=1)
        {
            setImportInterval(1);
        }
        else
        {
            setImportInterval(atoi(tempStr));
        }
    }
    else
    {
        setImportInterval(900);
    }

    tempStr = getCparmValueAsString(KEY_FILENAME);
    if (tempStr.length() > 0)
    {
        setFileName(tempStr);
    }
    else
    {
        setFileName(RWCString ("dsmdata.txt"));
    }

    tempStr = getCparmValueAsString(KEY_DRIVE_AND_PATH);
    if (tempStr.length() > 0)
    {
        setDriveAndPath(tempStr);
    }
    else
    {
        setDriveAndPath(RWCString ("\\yukon\\server\\import"));
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

    setDeleteFileAfterImport(true);
    tempStr = getCparmValueAsString(KEY_DELETE_FILE);
    if (tempStr.length() > 0)
    {
        if (!tempStr.compareTo ("false",RWCString::ignoreCase))
        {
            setDeleteFileAfterImport (false);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Dsm2 import file name " << getFileName() << endl;
        dout << RWTime() << " Dsm2 import directory " << getDriveAndPath() << endl;
        dout << RWTime() << " Dsm2 import interval " << getImportInterval() << endl;
        dout << RWTime() << " Dsm2 import dispatch queue flush rate " << getQueueFlushRate() << endl;
        dout << RWTime() << " Dsm2 import db reload rate " << getReloadRate() << endl;

        if (shouldDeleteFileAfterImport())
            dout << RWTime() << " Import file will be deleted after import" << endl;
        else
            dout << RWTime() << " Import file will NOT be deleted after import" << endl;

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
        dsm2Interface = new CtiFDR_Dsm2Import();

        // now start it up
        return dsm2Interface->run();
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

        dsm2Interface->stop();
        delete dsm2Interface;
        dsm2Interface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif

