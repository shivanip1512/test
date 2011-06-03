/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrtextimport.cpp
*
*    DATE: 04/11/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrtextimport.cpp-arc  $
*    REVISION     :  $Revision: 1.30.2.1 $
*    DATE         :  $Date: 2008/11/13 17:23:48 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE:  ascii import
*
*    DESCRIPTION:
*
*    ---------------------------------------------------
*    History:
      $Log: fdrtextimport.cpp,v $
      Revision 1.30.2.1  2008/11/13 17:23:48  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.

      Revision 1.30  2008/10/16 22:16:01  tspar
      YUK-4691 FDR Text Import UTC conversion broken after fall DST change

      Removed. To be re added if needed, and using boost timezones

      Revision 1.29  2008/10/02 23:57:15  tspar
      YUK-5013 Full FDR reload should not happen with every point

      YUKRV-325  review changes

      Revision 1.28  2008/09/23 15:14:58  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.

      Revision 1.27  2008/09/15 21:08:48  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.26  2008/06/03 17:42:04  tspar
      YUK-4360 FDR crashes when the comma separated txt file has an unrecognized format

      Added error handling to the parsing function for text import. It will now state when it is not sending a point update, and leave clues in the log as to what caused it.

      Revision 1.25  2008/03/20 21:27:14  tspar
      YUK-5541 FDR Textimport and other interfaces incorrectly use the boost tokenizer.

      Changed all uses of the tokenizer to have a local copy of the string being tokenized.

      Revision 1.24  2007/08/30 17:03:29  tspar
      YUK-4318

      Fixed the way we read from files to be more efficient, and changed code flow to allow for more unit testing.

      Changed cparms to more show an intuitive difference between them.
      FDR_TEXTIMPORT_IMPORT_PATH is now
      FDR_TEXTIMPORT_DEFAULT_POINTIMPORT_PATH

      Changed the processe function that was causing a large memory leak over runtime.
      -Changed the code so it doesn't leak anymore, worked a long time trying to pin down the actual reason before just re-working the code.

      Changed the delete cparm to no longer default to true since we have two options, delete or rename.
      How it works now:
      1)specifying rename and delete will default to rename.
      2)not specifying either will default to delete.
      3)specifying one or the other will work as expected.

      Revision 1.23  2007/08/08 14:10:05  tspar
      YUK-4192

      This was limited to lodestar and textimport only.  Text time format was changed and these functions expected a certain format.
      Changed both to be more adaptable, also put the seconds back into the filename to keep names unique. In case import cycles are ever less than 60 seconds.

      Revision 1.22  2007/07/19 19:41:48  tspar
      Fixed replacement of '/' characters in the time string. Rather than expecting them to be in certain positions in the string.

      Revision 1.21  2007/05/11 19:05:06  tspar
      YUK-3880

      Refactored a few interfaces.

      Revision 1.20  2007/05/11 14:53:10  tspar
      YUK-3839 FDR Text Import UTC time

      Added a UTC time conversion. We can now accept UTC time by adding a 'U' flag in place of the normal DST flag. FDR will convert the time given to local time before returning it.

      Revision 1.19  2007/02/15 23:22:08  jrichter
      took out check for sign.

      Revision 1.18  2007/01/10 21:23:19  tspar
      Changed the tokenizer so it will keep empty tokens.

      Revision 1.17  2007/01/10 00:20:00  tspar
      Regular expression was not matching, fixed. merged from 3.2

      Cleaned up the code a little by removing CtiTokenizer by making calls to "getTranslationValue(string)"

      Added .c_str()  to all the strings in _snprintf calls to allow them to work properly.

      Revision 1.16  2006/12/22 19:40:23  jrichter
      Bug Id: 716
      -check first character to see if it is digit, + or - sign before calling atof function
      -also merged thain's changes to head..

      Revision 1.15  2006/12/20 22:29:22  jrichter
      BUG FIX: 716
      -check first character to see if it is digit, + or - sign before calling atof function.

      Revision 1.14  2006/06/07 22:34:04  tspar
      _snprintf  adding .c_str() to all strings. Not having this does not cause compiler errors, but does cause runtime errors. Also tweaks and fixes to FDR due to some differences in STL / RW

      Revision 1.13  2006/05/23 17:17:43  tspar
      bug fix: boost iterator used incorrectly in loop.

      Revision 1.12  2006/04/24 14:47:33  tspar
      RWreplace: replacing a few missed or new Rogue Wave elements

      Revision 1.11  2006/01/03 20:23:38  tspar
      Moved non RW string utilities from rwutil.h to utility.h

      Revision 1.10  2005/12/20 17:17:15  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.9  2005/10/19 16:54:02  dsutton
      Changed the logging to the system log so we don't log every unknown
      point as it comes in from the foreign system.  It will now log these points
      only if a debug level is set.

      Revision 1.8  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.7  2004/09/29 17:47:48  dsutton
      Updated all interfaces to default the db reload rate to once a day (86400)

      Revision 1.6  2004/09/24 14:36:53  eschmit
      Added Boost includes and libraries, misc fixes for ptime support

      Revision 1.5  2003/10/31 21:16:17  dsutton
      Interface would apply a multiplier and offset to a status point if one was
      imported.  Probably wouldn't cause a problem but was moved anyway.

      Revision 1.2.2.1  2003/10/20 20:19:42  dsutton
      Bug:  The application wasn't locking the point list down before looking for a
      point.  The database reload does lock the list down before reloading.  If the
      lookup and the reload happened at the same time,  the lookup lost and threw
      and exception

      Revision 1.3  2003/06/04 21:18:51  dsutton
      Interface wasn't applying the multiplier or offset to the incoming points

      Revision 1.2  2003/04/22 20:44:45  dsutton
      Interfaces FDRTextExport and FDRTextImport and all the pieces needed
      to make them compile and work

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
#include <time.h>
#include <string>
#include <fstream>

/** include files **/
#include "ctitime.h"
#include "ctidate.h"
#include "ctistring.h"

#include "cparms.h"
#include "msg_cmd.h"
#include "pointtypes.h"

#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrtextfilebase.h"
#include "fdrtextimport.h"
#include "utility.h"

using std::vector;
using std::list;

CtiFDR_TextImport * textImportInterface;

const CHAR * CtiFDR_TextImport::KEY_INTERVAL = "FDR_TEXTIMPORT_INTERVAL";
const CHAR * CtiFDR_TextImport::KEY_FILENAME = "FDR_TEXTIMPORT_FILENAME";
const CHAR * CtiFDR_TextImport::KEY_LEGACY_DRIVE_AND_PATH = "FDR_TEXTIMPORT_DRIVE_AND_PATH";
const CHAR * CtiFDR_TextImport::KEY_DB_RELOAD_RATE = "FDR_TEXTIMPORT_DB_RELOAD_RATE";
const CHAR * CtiFDR_TextImport::KEY_QUEUE_FLUSH_RATE = "FDR_TEXTIMPORT_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_TextImport::KEY_DELETE_FILE = "FDR_TEXTIMPORT_DELETE_FILE";
const CHAR * CtiFDR_TextImport::KEY_POINTIMPORT_DEFAULT_PATH = "FDR_TEXTIMPORT_DEFAULT_POINTIMPORT_PATH";//changed
const CHAR * CtiFDR_TextImport::KEY_RENAME_SAVE_FILE = "FDR_TEXTIMPORT_RENAME_SAVE_FILE";


// Constructors, Destructor, and Operators
CtiFDR_TextImport::CtiFDR_TextImport() :
    CtiFDRTextFileBase(string("TEXTIMPORT")),
    _deleteFileAfterImportFlag(false),
    _renameSaveFileAfterImportFlag(false),
    _legacyDrivePath(false)
{

}

CtiFDR_TextImport::~CtiFDR_TextImport()
{
}


bool CtiFDR_TextImport::shouldDeleteFileAfterImport() const
{
    return _deleteFileAfterImportFlag;
}

bool CtiFDR_TextImport::shouldRenameSaveFileAfterImport() const
{
    return _renameSaveFileAfterImportFlag;
}

CtiFDR_TextImport &CtiFDR_TextImport::setRenameSaveFileAfterImport (bool aFlag)
{
    _renameSaveFileAfterImportFlag = aFlag;
    return *this;
}


CtiFDR_TextImport &CtiFDR_TextImport::setDeleteFileAfterImport (bool aFlag)
{
    _deleteFileAfterImportFlag = aFlag;
    return *this;
}

BOOL CtiFDR_TextImport::init( void )
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE));
    getReceiveFromList().setPointList (recList);
    recList = NULL;

    // init the base class
    Inherited::init();
    _threadReadFromFile = rwMakeThreadFunction(*this,
                                               &CtiFDR_TextImport::threadFunctionReadFromFile);

    _legacyDrivePath = false;
    if (!readConfig( ))
    {
        return FALSE;
    }

    loadTranslationLists();
    return TRUE;
}
/*************************************************
* Function Name: CtiFDR_TextImport::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDR_TextImport::run( void )
{
    // crank up the base class
    Inherited::run();

    // startup our interfaces
    _threadReadFromFile.start();

    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_TextImport::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDR_TextImport::stop( void )
{
    _threadReadFromFile.requestCancellation();
    Inherited::stop();
    return TRUE;
}


CtiTime CtiFDR_TextImport::ForeignToYukonTime (string& aTime, CHAR aDstFlag)
{
    struct tm ts;
    CtiTime retVal;
    int month,day,year,hour,minute,sec;

    if (aTime.length() == 19)
    {
        if (sscanf (aTime.c_str(),
                    "%2ld/%2ld/%4ld %2ld:%2ld:%2ld",
                    &month,
                    &day,
                    &year,
                    &hour,
                    &minute,
                    &sec) != 6)
        {
            retVal = PASTDATE;
        }
        else
        {
            //Constructing a date since boost will exception and return 1/1/1970 if it is invalid.
            CtiDate dateValidCheck = CtiDate(day,month,year);

            if (dateValidCheck == CtiDate(1,1,1970))
            {
                return PASTDATE;
            }

            ts.tm_mon = month - 1;
            ts.tm_mday = day;
            ts.tm_year = year - 1900;
            ts.tm_hour = hour;
            ts.tm_min = minute;
            ts.tm_sec = sec;

            if( aDstFlag == 'D' || aDstFlag == 'd' )
            {
                ts.tm_hour;
                ts.tm_isdst = 1;//true
            }
            else if( aDstFlag == 'S' || aDstFlag == 's' )
            {
                ts.tm_isdst = 0;//false
            }
            else
            {
                //Error Case. No dst flag
                retVal = PASTDATE;
                return retVal;
            }

            try
            {
                retVal = CtiTime(&ts);

                // if CtiTime can't make a time ???
                if (!retVal.isValid())
                {
                    retVal = PASTDATE;
                }
            }
            catch (...)
            {
                retVal = PASTDATE;
            }
        }
    }
    else
    {
        retVal = PASTDATE;
    }
    return retVal;
}

USHORT CtiFDR_TextImport::ForeignToYukonQuality (char aQuality)
{

    if (aQuality == 'G' || aQuality == 'g')
        return NormalQuality;
    if (aQuality == 'B' || aQuality == 'b')
        return NonUpdatedQuality;
    if (aQuality == 'M' || aQuality == 'm')
        return ManualQuality;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " WARNING: Quality not recognized. Defaulting to NonUpdated. Q: " << aQuality << endl;
    }
    return NonUpdatedQuality;
}

bool CtiFDR_TextImport::processFunctionOne (Tokenizer& cmdLine, CtiMessage **aRetMsg)
{
    bool retCode = false;
    bool pointValidFlag=true;

    CtiFDRPoint point;
    CtiFDRPointSPtr pointPtr;
    int fieldNumber=1,quality;
    double value;
    string action;
    string linetimestamp,translationName,desc;
    CtiTime pointtimestamp;
    string tempString1;

    /****************************
    * function 1 is of the following format
    * function,id,value,quality,timestamp,daylight savings flag
    *****************************
    */
    try{
        Tokenizer::iterator tok_iter = cmdLine.begin();
        ++tok_iter;
        /** Error Check: unexpected end of input */
        if (tok_iter == cmdLine.end())
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " ERROR: Incorrect number of parameters in input line from file." << endl;
            return retCode;
        }
                tempString1 = string(*tok_iter);
        ++tok_iter;
        //Param 2 - Point Name
        translationName = tempString1;
        {
            CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
            std::transform(translationName.begin(), translationName.end(), translationName.begin(), toupper);
            std::map<string,int>::iterator iter = nameToPointId.find(translationName);
            if( iter != nameToPointId.end() ) {
                pointPtr = getReceiveFromList().getPointList()->findFDRPointID(iter->second);
                point = *pointPtr;
                pointValidFlag = true;
            }
            else
            {
                pointValidFlag = false;
            }
        }
        if (pointValidFlag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Translation for point " << translationName;
                    dout << " from " << getFileName() << " was not found" << endl;
                }
                desc = getFileName() + string (" point is not listed in the translation table");
                action = translationName;
                logEvent (desc,action);
            }
        }
        else
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Translation for point " << translationName;
                    dout << " from " << getFileName() << " was found" << endl;
                }
            }
            //Param 3 - Value
            /** Error Check: unexpected end of input */
            if (tok_iter == cmdLine.end())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ERROR: Incorrect number of parameters in input line from file." << endl;
                return retCode;
            }
            tempString1 = string(*tok_iter);
            ++tok_iter;
            value = atof(tempString1.c_str());
            //Test if the Value is Zero. if it is zero and tempString1[0] is not 0 then this could indicate a problem
            if (value == 0 && tempString1[0] != '0')
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " WARNING: Possible bad input from import file. Received a 0.0 value from " << tempString1 << endl;
            }
            //Param 4 - quality
            /** Error Check: unexpected end of input */
            if (tok_iter == cmdLine.end())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ERROR: Incorrect number of parameters in input line from file." << endl;
                return retCode;
            }
            tempString1 = string(*tok_iter);
            ++tok_iter;
            quality = ForeignToYukonQuality(tempString1[0]);
            //Input check inside function call.

            //Param 5 - Timestamp | Param 6 - DST Flag
            /** Error Check: unexpected end of input */
            if (tok_iter == cmdLine.end())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ERROR: Incorrect number of parameters in input line from file." << endl;
                return retCode;
            }
            tempString1 = string(*tok_iter);
            ++tok_iter;
            linetimestamp = tempString1;
            /** Error Check: unexpected end of input */
            if (tok_iter == cmdLine.end())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ERROR: Incorrect number of parameters in input line from file." << endl;
                return retCode;
            }
            tempString1 = string(*tok_iter);
            pointtimestamp = ForeignToYukonTime(linetimestamp, tempString1[0]);

            if (pointtimestamp != PASTDATE && pointValidFlag == true)
            {
                retCode = buildAndAddPoint (point,value,pointtimestamp,quality,translationName,aRetMsg);
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                if (pointtimestamp == PASTDATE)
                {
                    dout << CtiTime() << " ERROR: Timestamp or DST Flag is incorrect: " << linetimestamp << " " << tempString1[0] << endl;
                }
                dout << CtiTime() << " ERROR: Input for point invalid, not sending the update. Check log for errors." << endl;
           }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error Parsing data. Bad input from the file.\n";
    }

    return retCode;
}


bool CtiFDR_TextImport::buildAndAddPoint (CtiFDRPoint &aPoint,
                                          DOUBLE aValue,
                                          CtiTime aTimestamp,
                                          int aQuality,
                                          string aTranslationName,
                                          CtiMessage **aRetMsg)
{
    string desc;
    CHAR   action[60];
    bool retCode = false;

    // figure out what it should be now
    switch (aPoint.getPointType())
    {
    case AnalogPointType:
    case PulseAccumulatorPointType:
    case DemandAccumulatorPointType:
    case CalculatedPointType:
        {
            DOUBLE value = aValue;

            if (aPoint.isControllable())
            {
                CtiCommandMsg *aoMsg = createAnalogOutputMessage(aPoint.getPointID(), aTranslationName, value);
                sendMessageToDispatch(aoMsg);
            }

            value *= aPoint.getMultiplier();
            value += aPoint.getOffset();

            *aRetMsg = new CtiPointDataMsg(aPoint.getPointID(),
                                           value,
                                           aQuality,
                                           AnalogPointType);
            ((CtiPointDataMsg *)*aRetMsg)->setTime(aTimestamp);

            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Analog point " << aTranslationName;
                dout << " value " << value << " from " << getFileName() << " assigned to point " << aPoint.getPointID() << endl;;
            }
            retCode = true;
            break;
        }
    case StatusPointType:
        {
            // check for control functions
            if (aPoint.isControllable())
            {
                // make sure the value is valid
                if ((aValue == OPENED) || (aValue == CLOSED))
                {
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Control point " << aTranslationName;
                        if (aValue == OPENED)
                        {
                            dout << " control: Open " ;
                        } else
                        {
                            dout << " control: Closed " ;
                        }

                        dout <<" from " << getFileName() << " and processed for point " << aPoint.getPointID() << endl;;
                    }

                    // build the command message and send the control
                    *aRetMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);

                    ((CtiCommandMsg *)*aRetMsg)->insert( -1 );                // This is the dispatch token and is unimplemented at this time
                    ((CtiCommandMsg *)*aRetMsg)->insert(0);                   // device id, unknown at this point, dispatch will find it
                    ((CtiCommandMsg *)*aRetMsg)->insert(aPoint.getPointID());  // point for control
                    ((CtiCommandMsg *)*aRetMsg)->insert(aValue);
                    retCode = true;

                } else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Invalid control state " << aValue;
                        dout << " for " << aTranslationName << " received from " << getFileName() << endl;
                    }
                    CHAR state[20];
                    _snprintf (state,20,"%.0f",aValue);
                    desc = getFileName() + string (" control point received with an invalid state ") + string (state);
                    _snprintf(action,60,"%s for pointID %d",
                              aTranslationName.c_str(),
                              aPoint.getPointID());
                    logEvent (desc,string (action));
                }
            } else
            {
                if ((aValue == OPENED) || (aValue == CLOSED))
                {
                    string tracestate;
                    if (aValue == OPENED)
                    {
                        tracestate = string ("Open");
                    } else
                    {
                        tracestate = string ("Closed");
                    }
                    *aRetMsg = new CtiPointDataMsg(aPoint.getPointID(),
                                                   aValue,
                                                   aQuality,
                                                   StatusPointType);
                    ((CtiPointDataMsg*)*aRetMsg)->setTime(aTimestamp);
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Status point " << aTranslationName;
                        dout << " new state: " << tracestate;
                        dout <<" from " << getFileName() << " assigned to point " << aPoint.getPointID() << endl;;
                    }
                    retCode = true;
                } else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Status point " << aTranslationName;
                        dout << " received an invalid state " << (int)aValue;
                        dout <<" from " << getFileName() << " for point " << aPoint.getPointID() << endl;;
                    }
                    CHAR state[20];
                    _snprintf (state,20,"%.0f",aValue);
                    desc = getFileName() + string (" status point received with an invalid state ") + string (state);
                    _snprintf(action,60,"%s for pointID %d",
                              aTranslationName.c_str(),
                              aPoint.getPointID());
                    logEvent (desc,string (action));

                }
            }
            break;
        }
    }
    return retCode;
}


bool CtiFDR_TextImport::validateAndDecodeLine (string &aLine, CtiMessage **aRetMsg)
{
    bool retCode = false;
    bool flag;

    std::transform(aLine.begin(), aLine.end(), aLine.begin(), tolower);
    string tempString1;// Will receive each token

    Separator sep(",\r\n", "", boost::keep_empty_tokens);
    Tokenizer cmdLine(aLine, sep);
    Tokenizer::iterator tok_iter = cmdLine.begin();

    CtiFDRPoint         point;
    int function;

    // grab the function number
    if ( tok_iter != cmdLine.end() )
    {
        tempString1 = *tok_iter;
        function = atoi (tempString1.c_str());

        // check the function number
        switch (function)
        {
        case 1:
            {
                /****************************
                * function 1 is of the following format
                * id,value,quality,timestamp,daylight savings flag
                *****************************
                */
                retCode=processFunctionOne (cmdLine,aRetMsg);
                break;
            }
        default:
            break;
        }
    }
    return retCode;
}



int CtiFDR_TextImport::readConfig( void )
{
    int         successful = TRUE;
    string   tempStr;

    tempStr = getCparmValueAsString(KEY_INTERVAL);
    if (tempStr.length() > 0)
    {
        if (atoi (tempStr.c_str()) <=1)
        {
            setInterval(1);
        } else
        {
            setInterval(atoi(tempStr.c_str()));
        }
    } else
    {
        setInterval(900);
    }

    tempStr = getCparmValueAsString(KEY_FILENAME);
    if (tempStr.length() > 0)
    {
        setFileName(tempStr);
    } else
    {
        setFileName(string ("yukon.txt"));
    }
    tempStr = getCparmValueAsString(KEY_POINTIMPORT_DEFAULT_PATH);
    if (tempStr.length() > 0)
    {
        setFileImportBaseDrivePath(tempStr);
    } else
    {
        setFileImportBaseDrivePath(CtiString ("c:\\yukon\\server\\import"));
    }

    tempStr = getCparmValueAsString(KEY_LEGACY_DRIVE_AND_PATH);
    if (tempStr.length() > 0)
    {
        _legacyDrivePath = true;
        int pos = tempStr.find_last_of("\\");
        if( pos != tempStr.size()-1 )
        {
            tempStr += "\\";
        }
        setDriveAndPath(tempStr);
    } else
    {
        setDriveAndPath(string ("\\yukon\\server\\import"));
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr.c_str()));
    } else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr.c_str()));
    } else
    {
        // default to one second for stec, its only 2 points
        setQueueFlushRate (1);
    }

    setDeleteFileAfterImport(true);
    tempStr = getCparmValueAsString(KEY_DELETE_FILE);
    if (tempStr.length() > 0)
    {
        if (string_equal(tempStr,"false"))
        {
            setDeleteFileAfterImport (false);
        }
    }

    setRenameSaveFileAfterImport(false);
    tempStr = getCparmValueAsString(KEY_RENAME_SAVE_FILE);
    if (tempStr.length() > 0)
    {
        if (string_equal(tempStr,"true"))
        {
            setRenameSaveFileAfterImport (true);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        if ( _legacyDrivePath )
        {
            dout << CtiTime() << " Legacy method of setting the Drive Path. Please update to the new Method using the Points." << endl;
        }
        dout << CtiTime() << " Text import file name " << getFileName() << endl;
        dout << CtiTime() << " Text import directory " << getDriveAndPath() << endl;//obsolete?
        dout << CtiTime() << " Text import interval " << getInterval() << endl;
        dout << CtiTime() << " Text import dispatch queue flush rate " << getQueueFlushRate() << endl;
        dout << CtiTime() << " Text import db reload rate " << getReloadRate() << endl;

        if( shouldDeleteFileAfterImport() && shouldRenameSaveFileAfterImport() )
        {
            dout << CtiTime() << " Configuration Error cannot rename AND delete the input file. Defaulting to rename.\n";
            setRenameSaveFileAfterImport(true);
            setDeleteFileAfterImport(false);
        }else if( !shouldDeleteFileAfterImport() && !shouldRenameSaveFileAfterImport() )
        {
            dout << CtiTime() << " Configuration Error please specify what to do with the file after the import. Defaulting to delete.\n";
            setDeleteFileAfterImport(true);
        }
        if (shouldDeleteFileAfterImport() )
            dout << CtiTime() << " Import file will be deleted after import" << endl;
        if (shouldRenameSaveFileAfterImport() )
            dout << CtiTime() << " Import file will be renamed after import" << endl;

    }

    return successful;
}


/************************************************************************
* Function Name: CtiFDRTextFileBase::loadTranslationLists()
*
* Description: Creates a collection of points and their translations for the
*                               specified direction
*
*************************************************************************
*/
bool CtiFDR_TextImport::loadTranslationLists()
{
    bool foundPoint = false;
    bool successful = false;

    try
    {
        // make a list with all received points
        CtiFDRManager *pointList = new CtiFDRManager(getInterfaceName(), string (FDR_INTERFACE_RECEIVE));

        // if status is ok, we were able to read the database at least
        if ( pointList->loadPointList() )
        {
            /**************************************
            * seeing occasional problems where we get empty data sets back
            * and there should be info in them,  we're checking this to see if
            * is reasonable if the list may now be empty
            * the 2 entry thing is completly arbitrary
            ***************************************
            */
            if (((pointList->entries() == 0) && (getReceiveFromList().getPointList()->entries() <= 2)) ||
                (pointList->entries() > 0))
            {
                if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "pointList->entries() " << pointList->entries()<< endl;
                }

                // get iterator on send list
                CtiFDRManager::spiterator  myIterator = pointList->getMap().begin();
                int x;
                nameToPointId.clear();
                for ( ; myIterator != pointList->getMap().end(); ++myIterator)
                {
                    foundPoint = true;
                    successful = translateSinglePoint(myIterator->second);
                }   // end for interator

                // lock the receive list and remove the old one
                {
                    CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
                    if (getReceiveFromList().getPointList() != NULL)
                    {
                        getReceiveFromList().deletePointList();
                    }
                    getReceiveFromList().setPointList (pointList);
                }

                pointList=NULL;
                if (!successful)
                {
                    if (!foundPoint)
                    {
                        // means there was nothing in the list, wait until next db change or reload
                        successful = true;
                        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " No points defined for use by interface " << getInterfaceName() << endl;
                        }
                    }
                }
                setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
            } else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Error loading (Receive) points for " << getInterfaceName() << " : Empty data set returned " << endl;
                successful = false;
            }
        } else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            successful = false;
        }

    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error loading translation lists for " << getInterfaceName() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error loading translation lists for " << getInterfaceName() << endl;
    }

    return successful;
}

bool CtiFDR_TextImport::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool successful = false;

    string tempString1;
    string tempString2;
    string pointID;
    CtiString translationName;
    CtiString translationDrivePath;
    CtiString translationFilename;
    CtiString translationFolderName;

    translationDrivePath = getFileImportBaseDrivePath();
    translationFilename = getFileName();

    if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " translationPoint " << translationPoint->getPointID() <<endl;
        dout << CtiTime() << " translationFolderName " << translationFolderName <<endl;
        dout << CtiTime() << " translationDrivePath " << translationDrivePath <<endl;
    }


    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Parsing Yukon Point ID " << translationPoint->getPointID();
            dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
        }
        translationName = "";

        pointID = translationPoint->getDestinationList()[x].getTranslationValue("Point ID");

        /* For optimized find**/
        string pointName = pointID;
        std::transform(pointName.begin(), pointName.end(), pointName.begin(), toupper);
        nameToPointId.insert(std::pair<string,int>(pointName,translationPoint->getPointID()));
        /***/

        // now we have a point id
        if ( !pointID.empty() )
        {
            successful = true;

            translationName += " ";
            translationName += pointID;
            translationName.toUpper();

            tempString1 = translationPoint->getDestinationList()[x].getTranslationValue("DrivePath");
            if (!tempString1.empty())
            {
                // now we have a Drive/Path
                translationFolderName = tempString1;
                translationFolderName.toLower();

                if ((translationDrivePath = translationFolderName.match(boost::regex("([A-Z]|[a-z]):\\\\"))).empty())
                {
                    translationDrivePath = getFileImportBaseDrivePath();
                    translationDrivePath.toUpper();
                } else
                    translationDrivePath = translationFolderName;

                if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " translationFolderName " << translationFolderName <<endl;
                    dout << CtiTime() << " translationDrivePath " << translationDrivePath <<endl;
                }

                translationName += " ";
                translationName += tempString1;
                translationName.toUpper();

                tempString1 = translationPoint->getDestinationList()[x].getTranslationValue("Filename");
                if ( !tempString1.empty() )
                {
                    translationFilename = tempString1;
                    translationFilename.toLower();

                    translationName += " ";
                    translationName += translationFilename;
                    translationName.toUpper();
                }
            }
            translationPoint->getDestinationList()[x].setTranslation (pointID);//ts

            if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << " translationFilename ** "<< translationFilename<<" translationDrivePath ** "<<translationDrivePath<< endl;
            }

            //check if its already there before putting it in the list.
            std::vector<CtiFDRTextFileInterfaceParts> *fInfoList = getFileInfoList();
            std::vector<CtiFDRTextFileInterfaceParts>::iterator itr = fInfoList->begin();

            string fn = translationDrivePath + translationFilename;

            bool found = false;
            for( ; itr != fInfoList->end(); itr++ )
            {
                string fn2 = itr->getDriveAndPath() + itr->getFileName();
                if( fn2.compare(fn) == 0 ) {
                    found = true;
                    break;
                }
            }

            if( !found ) {//unique
                CtiFDRTextFileInterfaceParts tempFileInfoList ( translationFilename, translationDrivePath, 0);
                fInfoList->push_back(tempFileInfoList);
            }

            if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Point ID " << translationPoint->getPointID();
                dout << " translated: " << translationName << endl;
                dout << " FILE INFO LIST SIZE = " << getFileInfoList()->size() << endl;
                dout << " Translation... = " << translationPoint->getDestinationList()[x].getTranslation() << endl;
            }
        }
    }

    return successful;
}

void CtiFDR_TextImport::cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)
{
    if (recvList)
    {
        if (translationPoint.get() == NULL)
        {
            return;
        }

        int size = translationPoint->getDestinationList().size();
        for ( int i = 0 ; i < size; i++) {
            string str = translationPoint->getDestinationList()[i].getTranslation();
            if (str != "")
            {
                std::transform(str.begin(), str.end(), str.begin(), toupper);
                nameToPointId.erase(str);
            }
        }
    }

    return;
}


/***
* Function Name: list<string> GetFileNames()
*
* Depending on which method will determine if there could be more than one file.
*
*/

std::list<string> CtiFDR_TextImport::getFileNames()
{
    list<string> lst;

    if( getLegacy() )
    {
        string fn = getDriveAndPath() + getFileName();
        lst.push_back(fn);
    }
    else
    {
        vector <CtiFDRTextFileInterfaceParts> *fInfoList = getFileInfoList();
        std::vector <CtiFDRTextFileInterfaceParts>::iterator itr;
        for( itr = fInfoList->begin(); itr != fInfoList->end(); itr++ )
        {
            string fn = itr->getDriveAndPath() + itr->getFileName();
            lst.push_back(fn);
        }
    }

    return lst;
}


/*
* Function Name: List<Point> parseFiles
*
*/
std::list<string> CtiFDR_TextImport::parseFiles()
{
    std::list<string> lst;

    std::list<string> fileList = getFileNames();
    for( std::list<string>::iterator itr = fileList.begin(); itr != fileList.end(); itr++ )
    {
        string fname(*itr);

        std::fstream* strm = new std::fstream();
        strm->open( (*itr).c_str(), std::fstream::in );
        if( !(*strm).is_open() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Error opening File: " << (*itr) << "\n";
            delete strm;
        }else
        {
            string input;
            while( std::getline( *((std::iostream*)strm), input ) )
            {
                //Test on input to see if its ok?  >1 : <500 : invalid characters?
                lst.push_back(input);
            }
            //handle file close and stuff here
            strm->close();
            delete strm;
            handleFilePostOp((*itr));
        }
    }

    return lst;
}

/**************************************************************************
* Function Name: CtiFDRTextFileBase::threadFunctionReadFromFile (void )
*
* Description: thread that waits and then grabs the file for processing
*
* This works in two ways, Legacy and Point. Legacy gets the one singular file from
*       the cparms in master config. Point will read through each point and open the file name
*       listed on that point and process all points in that file.
*       Point: Loop through finding all unique file names listed under points.
*              Process each of those files input. loop back and sleep.
*
****************************************************************************/
void CtiFDR_TextImport::threadFunctionReadFromFile( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    CtiTime         timeNow;
    CtiTime         refreshTime(PASTDATE);

    try
    {
        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);

            timeNow = CtiTime();

            // now is the time to get the file
            if (timeNow >= refreshTime)
            {
                //parse files
                std::list<string> pointUpdates = parseFiles();

                for( std::list<std::string>::iterator itr3 =  pointUpdates.begin(); itr3 != pointUpdates.end(); itr3++ )
                {
                    CtiMessage* msg = NULL;
                    bool ok = validateAndDecodeLine( *itr3, &msg );

                    if ( ok )
                    {
                        queueMessageToDispatch( msg );
                    }
                }

                pointUpdates.clear();
                refreshTime = CtiTime() - (CtiTime::now().seconds() % getInterval()) + getInterval();
            }//end if (timeNow >= refreshTime)
        }//end for

    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION CtiFDRTextImport::threadFunctionReadFromFile in interface " << getInterfaceName() << endl;
    }
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error: CtiFDRTextIMport::threadFunctionReadFromFile: " << getInterfaceName() << " is offline " << endl;
    }
}
void CtiFDR_TextImport::handleFilePostOp( string fileName )
{
    //Change to do all files in fileList
    if ( shouldRenameSaveFileAfterImport() && !shouldDeleteFileAfterImport() )
    {
        moveFile( fileName );
    }
    else if( shouldDeleteFileAfterImport() && !shouldRenameSaveFileAfterImport() )
    {
        deleteFile( fileName );
    }
    else if( shouldDeleteFileAfterImport() && shouldRenameSaveFileAfterImport() )
    {
        //both specified. default to move, and not delete( since they were moved )
        moveFile( fileName );
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " master.cfg configuration error cannot rename AND delete the input file. Defaulting to rename.\n";
    }
}

bool CtiFDR_TextImport::moveFiles( std::list<string>& fileNames )
{
    bool noErrors = true;
    for( std::list<string>::iterator itr = fileNames.begin(); itr != fileNames.end(); itr++)
    {
        if( !moveFile(*itr) ) {
            noErrors = false;
        }
    }
    return noErrors;
}
bool CtiFDR_TextImport::moveFile( string fileName )
{
    bool noErrors = true;
    string fileNameAndPath = fileName;
    int pos = fileNameAndPath.rfind(".");
    //get just the fileName, no path.
    string oldFileName(fileNameAndPath,0,pos);
    string tempTime = CtiTime().asString();

    //remove slashes and : in the time
    bool slashesInString = true;
    while( slashesInString )
    {
        int pos2 = tempTime.find("/");
        if( pos2 == string::npos )
        {
            slashesInString = false;
            pos2 = tempTime.find(":");
            while (pos2 != string::npos )
            {
                tempTime = tempTime.erase(pos2,1);
                pos2 = tempTime.find(":");
            }
        }
        else
        {
            tempTime = tempTime.replace(pos2,1,"_");
        }
    }

    string newFilename = oldFileName + "." + tempTime + ".txt";

    bool success = MoveFileEx( fileNameAndPath.c_str(),newFilename.c_str(), MOVEFILE_REPLACE_EXISTING | MOVEFILE_COPY_ALLOWED);
    if ( !success )
    {
        noErrors = false;
        DWORD lastError = GetLastError();
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error moving file " <<  fileNameAndPath << ". Code: " << lastError << " for MoveFile()" << endl;
    }
    return noErrors;
}

bool CtiFDR_TextImport::deleteFiles( std::list<string>& fileNames )
{
    bool noErrors = true;
    for( std::list<string>::iterator itr = fileNames.begin(); itr != fileNames.end(); itr++)
    {
        if( !deleteFile(*itr) ) {
            noErrors = false;
        }
    }
    return noErrors;
}

bool CtiFDR_TextImport::deleteFile( string fileName )
{
    bool noErrors = true;
    string fileNameAndPath = fileName;
    bool success = DeleteFile( fileNameAndPath.c_str() );
    if ( !success )
    {
        noErrors = false;
        DWORD lastError = GetLastError();
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error deleting " <<  fileNameAndPath << ". Code: " << lastError << " for DeleteFile()" << endl;
    }
    return noErrors;
}
CtiString& CtiFDR_TextImport::getFileImportBaseDrivePath()
{
    return _fileImportBaseDrivePath;
}

CtiString& CtiFDR_TextImport::setFileImportBaseDrivePath(CtiString importBase)
{
    _fileImportBaseDrivePath = importBase;
    return _fileImportBaseDrivePath;
}

bool CtiFDR_TextImport::getLegacy()
{
    return _legacyDrivePath;
}
void CtiFDR_TextImport::setLegacy( bool val )
{
    _legacyDrivePath = val;
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
        textImportInterface = new CtiFDR_TextImport();
        textImportInterface->init();
        // now start it up
        return textImportInterface->run();
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

        textImportInterface->stop();
        delete textImportInterface;
        textImportInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif


