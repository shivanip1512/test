/*  ELOGGER.H

    Include file for ELOGGER and routines using
    this process.
    by:  Ben    3/23/90
        updated: 3/04/92   bdw
        Revised for Win32  3/30/99 CGP
*/

#ifndef ELOGGER_H
#define ELOGGER_H

#ifndef NORMAL
    #define NORMAL      0
#endif


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <iostream>
#include <string.h>

#define EVENTLOG 0x0001                 // send log to the logger
#define AUDIBLEALARM 0x0002             // this is creates a audible tone only
#define ACKAUDIBLEALARM 0x0004          // this is Acknowleges an audible tone only
#define NONCRITAUDIBLE  0x0008          // this creates a Single audible tone and stops
#define LOGMESSAGE 0

/* these are the current originator codes */
#define LOGSYSTEM 9                     // General Logging Messages


/* set up file layout structures */

class SYSTEMLOGMESS
{
public:
    ULONG TimeStamp;
    USHORT StatusFlag;                      // bit 1 logged on logger, bit 16 DST
    std::string DeviceName;
    std::string PointName;                    // this can be a point name or route name
    std::string LogMessage1;                   // first log it point name is used
    std::string LogMessage2;                   // first log it point name is used
    USHORT EventType;                       // type of logged event
    CHAR EventLable[5];                     // event label for logging only use 3 char
    USHORT Originator;                      // who logged the event

    SYSTEMLOGMESS()
    {
        ::memset( EventLable, '\0', sizeof(EventLable) );
    }
};

/* Prototypes from ELOG_CLI.C */
IM_EX_CTIBASE std::ostream& operator<<( std::ostream& ostrm, SYSTEMLOGMESS &sl );

int IM_EX_CTIBASE LogEvent (SYSTEMLOGMESS *);
IM_EX_CTIBASE int SendTextToLogger (PCHAR Source,
                                    PCHAR Message = NULL,
                                    const std::string& majorName = std::string(""),
                                    const std::string& minorName = std::string(""));

#endif      // #ifndef ELOGGER_H

