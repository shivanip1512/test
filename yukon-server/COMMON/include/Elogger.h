

/*  ELOGGER.H

    Include file for ELOGGER and routines using
    this process.
    by:  Ben    3/23/90
        updated: 3/04/92   bdw
        Revised for Win32  3/30/99 CGP
*/

#ifndef ELOGGER_H
#define ELOGGER_H

//#ifdef __cplusplus
//extern "C" {
//#endif

#ifndef NORMAL
    #define NORMAL      0
#endif

#include <windows.h>
#include <iostream>
using namespace std;

#include <rw\cstring.h>
#include <rw\rwtime.h>


#define SERVERTHREADSTACKSIZE 32768
#define CLEANUPTHREADSTACKSIZE 32768
#define AUDIBLETHREADSTACKSIZE 32768
#define UNACKTHREADSTACKSIZE 32768
#define ELOGGERPIPENM "\\PIPE\\ELOGGER"
#define DISABLEAUDIBLE "DAUDIBLE"
#define LOGGER_VAR "E_LOGGER"
#define CLEANUP_VAR "CLEANUPSERVER"
#define VALID_DEVICE_LIST "lpt1lpt2lpt3lpt4com1com2com3com4"

#define ELOGFILENAME "LOGHIST\\EVENTLOG.BDB"

#define EVENTLOG 0x0001                 // send log to the logger
#define AUDIBLEALARM 0x0002             // this is creates a audible tone only
#define ACKAUDIBLEALARM 0x0004          // this is Acknowleges an audible tone only
#define NONCRITAUDIBLE  0x0008          // this creates a Single audible tone and stops
#define LOGMESSAGE 0

#define ACKNOWLEGETIMEOUT_TEXT "UNACK-ALAR TIMEOUT"

/* these are the current originator codes */
#define ALARMSYSTEM 0                   // general alarms
#define FBLC 1
#define MCONTROL 2
#define PORTER 3
#define SCANNER 4
#define DRP 5
#define CLVC 6
#define LSURVEY 7
#define COMMSTATS 8
#define LOGSYSTEM 9                     // General Logging Messages


/* set up file layout structures */

class SYSTEMLOGMESS
{
public:
    ULONG TimeStamp;
    USHORT StatusFlag;                      // bit 1 logged on logger, bit 16 DST
    RWCString DeviceName;
    RWCString PointName;                    // this can be a point name or route name
    RWCString LogMessage1;                   // first log it point name is used
    RWCString LogMessage2;                   // first log it point name is used
    USHORT EventType;                       // type of logged event
    CHAR EventLable[5];                     // event label for logging only use 3 char
    USHORT Originator;                      // who logged the event

    SYSTEMLOGMESS()
    {
        memset( EventLable, '\0', sizeof(EventLable) );
    }
};

typedef struct _logline
{
    CHAR LogLine1[80];                      // first log
    CHAR LogLine2[80];                      // second log line
} LOGLINE;


/* Prototypes from ELUPDATE.C */
int IM_EX_CTIBASE InitELogDB (void);
int IM_EX_CTIBASE CloseEventLogDB (void);
int IM_EX_CTIBASE EventLogAdd (SYSTEMLOGMESS *);
int IM_EX_CTIBASE EventLogGetFirstTime (SYSTEMLOGMESS *);
int IM_EX_CTIBASE EventLogGetNextTime (SYSTEMLOGMESS *);
int IM_EX_CTIBASE EventLogGetLastTime (SYSTEMLOGMESS *);
int IM_EX_CTIBASE EventLogxGetLastTime (SYSTEMLOGMESS *, PBYTE);
int IM_EX_CTIBASE EventLogGetPrevTime (SYSTEMLOGMESS *);
int IM_EX_CTIBASE EventLogGetGTTime (SYSTEMLOGMESS *);
int IM_EX_CTIBASE EventLogDeleteRange (ULONG);
int IM_EX_CTIBASE EventLogRecords (PULONG);
int IM_EX_CTIBASE EventLogxRecords (PULONG, PBYTE);
int IM_EX_CTIBASE FO_CopyEventLogDB (PBYTE, PCHAR, PULONG);
int IM_EX_CTIBASE FO_CopyNewEventLogRecs (PBYTE, PULONG, PULONG);
int IM_EX_CTIBASE FO_OpenEventLogDB (PCHAR, PBYTE);
int IM_EX_CTIBASE FO_InitEventLogDB (PCHAR PathName);
int IM_EX_CTIBASE EventLogGetLastTimeAddr (SYSTEMLOGMESS *, PBYTE, PULONG);

int IM_EX_CTIBASE Check4EventLogDBChange (ULONG, PCHAR);

/* Prototypes from ELOG_CLI.C */
IM_EX_CTIBASE ostream& operator<<( ostream& ostrm, SYSTEMLOGMESS &sl );

int IM_EX_CTIBASE InitELog (VOID);
int IM_EX_CTIBASE CreateEventLogDB (PCHAR);
int IM_EX_CTIBASE LogEvent (SYSTEMLOGMESS *);
int IM_EX_CTIBASE InitAudibleFlag (VOID);
int IM_EX_CTIBASE GetAudibleFlag (VOID);
int IM_EX_CTIBASE setAudibleFlag (VOID);
int IM_EX_CTIBASE ClearAudibleFlag (VOID);
int IM_EX_CTIBASE SendProcessStart (PCHAR ProcessName);
// VOID APIENTRY SendProcessStop (ULONG);    // CGP
VOID IM_EX_CTIBASE SendProcessStop (ULONG);
IM_EX_CTIBASE int SendTextToLogger (PCHAR Source,
                                    PCHAR Message = NULL,
                                    RWCString majorName = RWCString(""),
                                    RWCString minorName = RWCString(""));

int IM_EX_CTIBASE InitLCFunctionVar (USHORT);
int IM_EX_CTIBASE setLCFunctionFlag (USHORT);
int IM_EX_CTIBASE GetLCFunctionFlag (VOID);
IM_EX_CTIBASE int  Send4PartToLogger (PCHAR Source, RWCString MajorName, RWCString MinorName, RWCString Message1 = RWCString(""), RWCString Message2 = RWCString(""));

/* Prototypes from ELOGGER.C */
VOID ServerThread (PVOID);
VOID CleanupThread (PVOID);
VOID AudibleThread (PVOID);
VOID UnackAlarmThread (PVOID);

//   #ifdef __cplusplus
//}
//   #endif

#endif      // #ifndef ELOGGER_H





