#include "yukon.h"
#pragma title ( "Logger Client Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
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

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "os2_2w32.h"
#include "cticalls.h"
// #include "btrieve.h"
#include "dsm2.h"
#include "elogger.h"
#include "logger.h"


HPIPE       ElogPipeHandle = (HPIPE) NULL;
PUSHORT     DisableAudible = (PUSHORT) NULL;
PUSHORT     LCFunctionCode = (PUSHORT) NULL;

ostream& operator<<( ostream& ostrm, SYSTEMLOGMESS &sl )
{
    ostrm << RWTime( sl.TimeStamp + rwEpoch ); //  << " " << sl.DeviceName << " " << sl.PointName;

    if( sl.EventLable[0] != '\0' )
    {
        ostrm << " " << sl.EventLable;
    }

    if(!sl.DeviceName.isNull())
    {
        ostrm << " " << sl.DeviceName;
    }

    if(!sl.PointName.isNull())
    {
        ostrm << " " << sl.PointName;
    }

    if(!sl.LogMessage1.isNull())
    {
        ostrm << " " << sl.LogMessage1;
    }

    if(!sl.LogMessage2.isNull())
    {
        ostrm << " " << sl.LogMessage2;
    }

    return ostrm;
}


IM_EX_CTIBASE INT InitELog ()
{

    CHAR Name[100];
    PSZ ServerName;
    ULONG Action;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return(NORMAL);

}

IM_EX_CTIBASE INT LogEvent(SYSTEMLOGMESS *LogMessage)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " " << *LogMessage << endl;              // Stupid trick.
    }

    return(NORMAL);
}


/* gets handle to audible flag */
IM_EX_CTIBASE INT InitAudibleFlag ()
{

    /* get a Shared Memory for disabling aubible sounds */
    if(CTIGetNamedSharedMem ((PPVOID) &DisableAudible, DISABLEAUDIBLE, PAG_READ | PAG_WRITE))
    {
        return(MEMORY);
    }

    return(NORMAL);
}


/* returns audible flag */
IM_EX_CTIBASE INT getAudibleFlag ()
{
    ULONG i;

    if(DisableAudible == NULL)
        if((i = InitAudibleFlag ()) != NORMAL)       // bad pointer try to get it again
            return(i);                  // never got pointer audible is off

    return(*DisableAudible);

}


IM_EX_CTIBASE INT setAudibleFlag ()
{
    ULONG i;

    if(DisableAudible == NULL)
        if((i = InitAudibleFlag ()) != NORMAL)   // bad pointer try to get it again
            return(FALSE);             // never got pointer

    *DisableAudible = TRUE;
    return(*DisableAudible);
}


IM_EX_CTIBASE INT ClearAudibleFlag ()
{
    ULONG i;

    if(DisableAudible == NULL)
        if((i = InitAudibleFlag ()) != NORMAL)   // bad pointer try to get it again
            return(FALSE);      // never got pointer

    *DisableAudible = FALSE;
    return(*DisableAudible);
}


/* Save area for the process name */
CHAR MyProcessName[30];

/* Routine to send a process start message to logger */
IM_EX_CTIBASE INT SendProcessStart (PCHAR ProcessName)
{
    CHAR Message[50];

    strcpy (Message, "Process Starting: ");
    strcat (Message, ProcessName);
    SendTextToLogger ("Str", Message);

    /* Save the process name */
    strcpy (MyProcessName, ProcessName);

    /* set up the exit handler */
    // CTIExitList (EXLST_ADD, SendProcessStop);    // FIX FIX FIX CGP
    // atexit(SendProcessStop);    // FIX FIX FIX CGP

    return(NORMAL);
}


/* Routine to send a process stop message to logger */
// VOID APIENTRY SendProcessStop (ULONG Reason)     IM_EX_CTIBASE
IM_EX_CTIBASE VOID SendProcessStop (ULONG Reason)
{
    CHAR Message[50];

    strcpy (Message, "Process Stopping: ");
    strcat (Message, MyProcessName);
    SendTextToLogger ("Stp", Message);

    /* Message sent so continue to exit */
    //  CTIExitList (EXLST_EXIT, NULL);                 // FIX FIX FIX CGP
}


/* Routine to format and send a general text message to the logger */
IM_EX_CTIBASE INT SendTextToLogger (PCHAR Source, PCHAR Message, RWCString majorName, RWCString minorName)
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
    LogMessage.LogMessage1 = RWCString( Message );

    /* Process PointName */
    LogMessage.PointName = minorName;

    /* Process the DeviceName */
    LogMessage.DeviceName = majorName;

    LogMessage.EventType = LOGMESSAGE;
    LogMessage.Originator = LOGSYSTEM;

    /* Thats it so send it to elogger */
    return(LogEvent(&LogMessage));
}


/* Routine to send up to four part message to logger */
IM_EX_CTIBASE INT Send4PartToLogger (PCHAR Source, RWCString MajorName, RWCString MinorName, RWCString Message1, RWCString Message2)
{
    SYSTEMLOGMESS LogMessage;
    ULONG i;

    /* Build this up line by line */
    LogMessage.TimeStamp = LongTime ();
    LogMessage.StatusFlag = EVENTLOG;
    LogMessage.StatusFlag = DSTSET (LogMessage.StatusFlag);

    memcpy (LogMessage.EventLable, Source, 3);
    LogMessage.EventLable[3] = ' ';

    /* Process LogMessage */
    LogMessage.LogMessage2 = Message2;

    /* Process LogMessage1 */
    LogMessage.LogMessage1 = Message1;

    LogMessage.DeviceName = MajorName;
    LogMessage.PointName = MinorName;

    LogMessage.EventType = LOGMESSAGE;
    LogMessage.Originator = LOGSYSTEM;

    /* Thats it so send it to elogger */
    return(LogEvent (&LogMessage));
}


/* gets handle to a shared memory flag */
IM_EX_CTIBASE INT InitLCFunctionVar (USHORT CreateFlag)
{

    ULONG Result;

    /* get a Shared Memory for disabling aubible sounds */
    if((Result = CTIGetNamedSharedMem ((PPVOID) &LCFunctionCode, "LC_FUNC", PAG_READ | PAG_WRITE)) != NORMAL)
    {
        if(CreateFlag)
        {
            if((Result = CTIAllocSharedMem ((PPVOID) &LCFunctionCode, "LC_FUNC", sizeof (USHORT), OBJ_GETTABLE | PAG_READ | PAG_WRITE)) != NORMAL)
            {
                return(MEMORY);
            }
        }

        if(Result)
            return(MEMORY);        // memory not found yet and not allowed to create
    }

    return(NORMAL);

}


/* set the lc function number */
IM_EX_CTIBASE INT setLCFunctionFlag (USHORT FuncValue)
{

    if(LCFunctionCode == NULL)
        if(InitLCFunctionVar (TRUE)) // bad pointer try to get it again
            return(!NORMAL);             // never got pointer

    *LCFunctionCode = FuncValue;

    return(NORMAL);
}


/* get the lc function number */
IM_EX_CTIBASE INT getLCFunctionFlag ()
{
    ULONG i;

    if(LCFunctionCode == NULL)
        if((i = InitLCFunctionVar (FALSE)) != NORMAL)        // bad pointer try to get it again
            return(i);                   // never got pointer

    return(*LCFunctionCode);
}
