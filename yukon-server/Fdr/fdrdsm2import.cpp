/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrdsm2import.cpp
*
*    DATE: 05/31/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrdsm2import.cpp-arc  $
*    REVISION     :  $Revision: 1.12.20.1 $
*    DATE         :  $Date: 2008/11/13 17:23:47 $
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
      Revision 1.12.20.1  2008/11/13 17:23:47  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.

      Revision 1.12  2007/04/10 23:42:08  tspar
      Added even more protection against bad input when tokenizing.

      Doing a ++ operation on an token iterator that is already at the end will also assert.

      Revision 1.11  2007/04/10 23:04:35  tspar
      Added some more protection against bad input when tokenizing.

      Revision 1.10  2006/06/07 22:34:04  tspar
      _snprintf  adding .c_str() to all strings. Not having this does not cause compiler errors, but does cause runtime errors. Also tweaks and fixes to FDR due to some differences in STL / RW

      Revision 1.9  2006/05/23 17:17:43  tspar
      bug fix: boost iterator used incorrectly in loop.

      Revision 1.8  2006/01/03 20:23:37  tspar
      Moved non RW string utilities from rwutil.h to utility.h

      Revision 1.7  2005/12/20 17:17:13  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.6.2.3  2005/08/12 19:53:43  jliu
      Date Time Replaced

      Revision 1.6.2.2  2005/07/14 22:26:55  jliu
      RWCStringRemoved

      Revision 1.6.2.1  2005/07/12 21:08:36  jliu
      rpStringWithoutCmpParser

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

#include <wininet.h>
#include <fcntl.h>
#include <io.h>

/** include files **/
#include "ctitime.h"
#include "ctidate.h"

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
#include "utility.h"

CtiFDR_Dsm2Import * dsm2Interface;

const CHAR * CtiFDR_Dsm2Import::KEY_INTERVAL = "FDR_DSM2IMPORT_INTERVAL";
const CHAR * CtiFDR_Dsm2Import::KEY_FILENAME = "FDR_DSM2IMPORT_FILENAME";
const CHAR * CtiFDR_Dsm2Import::KEY_DRIVE_AND_PATH = "FDR_DSM2IMPORT_DRIVE_AND_PATH";
const CHAR * CtiFDR_Dsm2Import::KEY_DB_RELOAD_RATE = "FDR_DSM2IMPORT_DB_RELOAD_RATE";
const CHAR * CtiFDR_Dsm2Import::KEY_QUEUE_FLUSH_RATE = "FDR_DSM2IMPORT_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_Dsm2Import::KEY_DELETE_FILE = "FDR_DSM2IMPORT_DELETE_FILE";


// Constructors, Destructor, and Operators
CtiFDR_Dsm2Import::CtiFDR_Dsm2Import()
: CtiFDRAsciiImportBase(string("DSM2IMPORT"))
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

CtiTime CtiFDR_Dsm2Import::Dsm2ToYukonTime (string aTime)
{
    struct tm ts;

    if (sscanf (aTime.c_str(),
                "%4ld%2ld%2ld%2ld%2ld%2ld",
                &ts.tm_year,
                &ts.tm_mon,
                &ts.tm_mday,
                &ts.tm_hour,
                &ts.tm_min,
                &ts.tm_sec) != 6)
    {
        return(CtiTime(PASTDATE));
    }

    ts.tm_year -= 1900;
    ts.tm_mon--;

    if (aTime[14] == 'D' ||
        aTime[14] == 'd')
    {
        ts.tm_isdst = TRUE;
    }
    else
    {
        ts.tm_isdst = FALSE;
    }

    CtiTime returnTime(&ts);

    // if CtiTime can't make a time ???
    if (!returnTime.isValid())
    {
        return(CtiTime(PASTDATE));
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

bool CtiFDR_Dsm2Import::validateAndDecodeLine (string &aLine, CtiMessage **retMsg)
{
    bool retCode = false;
    bool flag;
    std::transform(aLine.begin(), aLine.end(), aLine.begin(), tolower);
    string tempString1;                // Will receive each token
    boost::char_separator<char> sep(",\r\n");
    Boost_char_tokenizer cmdLine(aLine, sep);
    Boost_char_tokenizer::iterator tok_iter = cmdLine.begin();

    CtiFDRPoint         point;
    CHAR action[200];
    string desc;

    // do we have an of these
    if ( tok_iter != cmdLine.end())
    {
        tempString1 = *tok_iter; tok_iter++;
        {
            CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
            flag = findTranslationNameInList (tempString1, getReceiveFromList(), point);
        }

        string translationName=tempString1;

        if (flag == true)
        {
            //Need these nasty if's to protect from an assert.
            bool flagOk = true;
            if( flagOk && tok_iter != cmdLine.end() ){
                // now
                tempString1 = *tok_iter;tok_iter++;
            }else{
                flagOk = false;
            }if( flagOk && tok_iter != cmdLine.end() ){
                // device name
                tempString1 = *tok_iter;tok_iter++;
            }else{
                flagOk = false;
            }
            if( flagOk && tok_iter != cmdLine.end() ){
                // point name
                tempString1 = *tok_iter;tok_iter++;
            }else{
                flagOk = false;
            }
            // value
            if ( flagOk && tok_iter != cmdLine.end() )
            {
                tempString1 = *tok_iter; tok_iter++;
                float value = atof (tempString1.c_str());

                // quality
                if ( tok_iter != cmdLine.end())
                {
                    tempString1 = *tok_iter; tok_iter++;
                    int quality = Dsm2ToYukonQuality (tempString1[0]);

                    // timestamp
                    if ( tok_iter != cmdLine.end())
                    {
                        tempString1 = *tok_iter; tok_iter++;
                        CtiTime timestamp = Dsm2ToYukonTime (tempString1);

                        if (timestamp != PASTDATE)
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
                                        dout << CtiTime() << " Analog point " << translationName;
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
                                                dout << CtiTime() << " Invalid control state " << value;
                                                dout << " for " << translationName << " received from " << getInterfaceName() << endl;
                                            }
                                            CHAR state[20];
                                            _snprintf (state,20,"%.0f",value);
                                            desc = getInterfaceName() + string (" control point received with an invalid state ") + string (state);
                                            _snprintf(action,60,"%s for pointID %d",
                                                      translationName.c_str(),
                                                      point.getPointID());
                                            logEvent (desc,string (action));
                                        }

                                        if (controlState != -1)
                                        {
                                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << CtiTime() << " Control point " << translationName;
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
                                        string traceState;
                                        // assign last stuff
                                        switch ((int)value)
                                        {
                                            case Dsm2_Open:
                                                yukonValue = OPENED;
                                                traceState = string("Opened");
                                                break;
                                            case Dsm2_Closed:
                                                yukonValue = CLOSED;
                                                traceState = string("Closed");
                                                break;
                                            case Dsm2_Indeterminate:
                                                yukonValue = INDETERMINATE;
                                                traceState = string("Indeterminate");
                                                break;
                                            case Dsm2_State_Four:
                                                yukonValue = STATEFOUR;
                                                traceState = string("State Four");
                                                break;
                                            case Dsm2_State_Five:
                                                yukonValue = STATEFIVE;
                                                traceState = string("State Five");
                                                break;
                                            case Dsm2_State_Six:
                                                yukonValue = STATESIX;
                                                traceState = string("State Six");
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
                                            dout << CtiTime() << " Status point " << translationName;
                                            dout << " received an invalid state " << (int)value;
                                            dout <<" from " << getInterfaceName() << " for point " << point.getPointID() << endl;;

                                            CHAR state[20];
                                            _snprintf (state,20,"%.0f",value);
                                            desc = getInterfaceName() + string (" status point received with an invalid state ") + string (state);
                                            _snprintf(action,60,"%s for pointID %d",
                                                      translationName.c_str(),
                                                      point.getPointID());
                                            logEvent (desc,string (action));

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
                                                dout << CtiTime() << " Status point " << translationName;
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
    string   tempStr;

    tempStr = getCparmValueAsString(KEY_INTERVAL);
    if (tempStr.length() > 0)
    {
        if (atoi (tempStr.c_str()) <=1)
        {
            setImportInterval(1);
        }
        else
        {
            setImportInterval(atoi(tempStr.c_str()));
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
        setFileName(string ("dsmdata.txt"));
    }

    tempStr = getCparmValueAsString(KEY_DRIVE_AND_PATH);
    if (tempStr.length() > 0)
    {
        setDriveAndPath(tempStr);
    }
    else
    {
        setDriveAndPath(string ("\\yukon\\server\\import"));
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr.c_str()));
    }
    else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr.c_str()));
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
        if (!stringCompareIgnoreCase(tempStr,"false"))
        {
            setDeleteFileAfterImport (false);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dsm2 import file name " << getFileName() << endl;
        dout << CtiTime() << " Dsm2 import directory " << getDriveAndPath() << endl;
        dout << CtiTime() << " Dsm2 import interval " << getImportInterval() << endl;
        dout << CtiTime() << " Dsm2 import dispatch queue flush rate " << getQueueFlushRate() << endl;
        dout << CtiTime() << " Dsm2 import db reload rate " << getReloadRate() << endl;

        if (shouldDeleteFileAfterImport())
            dout << CtiTime() << " Import file will be deleted after import" << endl;
        else
            dout << CtiTime() << " Import file will NOT be deleted after import" << endl;

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

