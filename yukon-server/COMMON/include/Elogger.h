#pragma once

#include "loggable.h"
#include "StreamBuffer.h"
#include "ctitime.h"

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

class SYSTEMLOGMESS : public Cti::Loggable
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

    virtual std::string toString() const override
    {
        Cti::StreamBuffer out;

        out << CtiTime( TimeStamp ); //  << " " << sl.DeviceName << " " << sl.PointName;

        if( EventLable[0] != '\0' )
        {
            out << " " << EventLable;
        }

        if( ! DeviceName.empty() )
        {
            out << " " << DeviceName;
        }

        if(! PointName.empty() )
        {
            out << " " << PointName;
        }

        if( ! LogMessage1.empty() )
        {
            out << " " << LogMessage1;
        }

        if( ! LogMessage2.empty() )
        {
            out << " " << LogMessage2;
        }

        return out;
    }
};

int IM_EX_CTIBASE LogEvent (SYSTEMLOGMESS *);

