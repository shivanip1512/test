/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        Ben Wallace

    FileName:
        elog_cli.c

    Purpose:
        Routines to dispatch various logger messages to the logger and log
        files.

    The following procedures are contained in this module:
        InitELog                LogEvent
        InitAudibleFlag         getAudibleFlag
        setAudibleFlag          ClearAudibleFlag
        SendProcessStart        SendProcessStop
        SendTextToLogger        InitLCFunctionVar
        setLCFunctionFlag       getLCFunctionFlag

    Initial Date:
        3/23/90

    Revision History:

         3/7/92      Initial                                             BDW
         9-6-93      Converted to 32 bit                          WRO
         03-30-99    Converted to Win32 API                       CGP

   -------------------------------------------------------------------- */
#include "yukon.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "os2_2w32.h"
#include "cticalls.h"
#include "dsm2.h"
#include "elogger.h"
#include "logger.h"

using std::ostream;
using std::string;
using std::endl;

HPIPE       ElogPipeHandle = (HPIPE) NULL;
PUSHORT     DisableAudible = (PUSHORT) NULL;
PUSHORT     LCFunctionCode = (PUSHORT) NULL;

ostream& operator<<( ostream& ostrm, SYSTEMLOGMESS &sl )
{
    ostrm << CtiTime( sl.TimeStamp ); //  << " " << sl.DeviceName << " " << sl.PointName;

    if( sl.EventLable[0] != '\0' )
    {
        ostrm << " " << sl.EventLable;
    }

    if(!sl.DeviceName.empty())
    {
        ostrm << " " << sl.DeviceName;
    }

    if(!sl.PointName.empty())
    {
        ostrm << " " << sl.PointName;
    }

    if(!sl.LogMessage1.empty())
    {
        ostrm << " " << sl.LogMessage1;
    }

    if(!sl.LogMessage2.empty())
    {
        ostrm << " " << sl.LogMessage2;
    }

    return ostrm;
}


IM_EX_CTIBASE INT LogEvent(SYSTEMLOGMESS *LogMessage)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " " << *LogMessage << endl;              // Stupid trick.
    }

    return(NORMAL);
}


/* Routine to format and send a general text message to the logger */
IM_EX_CTIBASE INT SendTextToLogger (PCHAR Source, PCHAR Message, const string& majorName, const string& minorName)
{
    SYSTEMLOGMESS LogMessage;
    ULONG i;
    PCHAR Pointer;

    /* Build this up line by line */
    LogMessage.TimeStamp = LongTime ();
    LogMessage.StatusFlag = EVENTLOG;
    LogMessage.StatusFlag = DSTSET (LogMessage.StatusFlag);

    memcpy (LogMessage.EventLable, Source, 3);
    LogMessage.EventLable[3] = ' ';

    /* Process LogMessage1 */
    LogMessage.LogMessage1 = string( Message );

    /* Process PointName */
    LogMessage.PointName = minorName;

    /* Process the DeviceName */
    LogMessage.DeviceName = majorName;

    LogMessage.EventType = LOGMESSAGE;
    LogMessage.Originator = LOGSYSTEM;

    /* Thats it so send it to elogger */
    return(LogEvent(&LogMessage));
}



