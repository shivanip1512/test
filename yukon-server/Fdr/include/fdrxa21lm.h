#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrxa21lm.h
*
*    DATE: 11/20/2004
*
*    AUTHOR: Aaron Lauinger
*
*    PURPOSE: Interface to the CTI XA/21 lms engine.
*
*    DESCRIPTION: This interface listens for a connection from lmsdlnk running
*                 on XA/21.  It has two main functions.
*                 1) Receive MPC commands originating from lmsengine and send
*                    the appropriate control commands on to dispatch.
*                 2) Receive ripple group control status point changes and
*                    translate this into MPC status messages so that lmseengine
*                    can properly do load group accounting. (Regardless of where
*                    the control command originates)*
*
*    Copyright (C) 2004 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRXA21LM_H__
#define __FDRXA21LM_H__

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
#include <rw/cstring.h>
#include <rw/tpslist.h>

#include "dlldefs.h"
#include "queues.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "device.h"             // get the raw states
#include "fdrsinglesocket.h"
#include "critical_section.h"
#include "guard.h"
#include "string_util.h"

// global defines
#define XA21LMPORTNUMBER        1027

/*

NOTE:  All data limit violations will be handled by the receiving system
*/

#pragma pack(push, xa21lm_packing, 1)

/* Define the LMS status values */

#define STARTED                 1
#define COMPLETED               2
#define STOPPED                 3
#define REJECTED                4
#define ACKNOWLEDGE             5

/* Define the various function codes for harris interface */
#define NICKTESTSTART                   301
#define NICKTESTSTATUS                  302
#define NICKTESTSTOP                    303
#define STARTNEWSEASON                  402
#define LMSSTRATEGYSTART                501
#define LMSSTRATEGYSTOP                 503
#define LOADSCRAM                       505
#define LOADSCRAMSTATUS                 506
#define LOADSCRAMSTOP                   507
#define TOPOLOGY                        508
#define LMSSTRATEGYSTATUS               510
#define LMSTYPEACCOUNTING               512
#define LMSRETRIES                      530

#define MPCSTATUS                       1
#define MPCCOMPLETE                     2

#define MPCNICK                         1000
#define MPCNICKSTATUS                   MPCNICK + MPCSTATUS
#define MPCNICKCOMPLETE                 MPCNICK + MPCCOMPLETE

#define MPCSCRAM                        1100
#define MPCSCRAMSTATUS                  MPCSCRAM + MPCSTATUS
#define MPCSCRAMCOMPLETE                MPCSCRAM + MPCCOMPLETE

#define MPCCONFIRMCTRL                  1150
#define MPCCONFIRMCTRLSTATUS            MPCCONFIRMCTRL + MPCSTATUS
#define MPCCONFIRMCTRLCOMPLETE          MPCCONFIRMCTRL + MPCCOMPLETE
#define MPCCONFIRM_CONTROL_ACK          1155

#define MPCIMMEDIATECTRL                1170
#define MPCIMMEDIATECTRLSTATUS          MPCIMMEDIATECTRL + MPCSTATUS
#define MPCIMMEDIATECTRLCOMPLETE        MPCIMMEDIATECTRL + MPCCOMPLETE
#define MPCIMMEDIATECTRL_CONTROL_ACK    1175

#define MPCUNSOLICITEDCTRLSTATUS        1180

#define MPCSTRATEGY                     1200
#define MPCSTRATEGYSTATUS               MPCSTRATEGY + MPCSTATUS
#define MPCSTRATEGYCOMPLETE             MPCSTRATEGY + MPCCOMPLETE

#define MPCDAILYSTRAT                   1300
#define MPCDAILYSTRATSTATUS             MPCDAILYSTRAT + MPCSTATUS
#define MPCDAILYSTRATCOMPLETE           MPCDAILYSTRAT + MPCCOMPLETE

#define MPCWASDOINNOTHIN                1400

#define REPORTSTRATEGY                  10000
#define REPORTGROUPHIST                 10100
#define REPORTTIMESCHED                 10200

#define MPCMAXGROUPS                    300

#define MAXTYPES    30
#define MAXHOURS    24
#define MAXTOWERS   30

/* Masks for MPC Group Status */
#define MPCSHED                  0x0001      // SHED
#define MPCREJECTED              0x0002      // Control Group was Rejected
#define MPCINPROGRESS            0x0004      // Control Group is being processed
#define MPCERROR                 0x0008      // Error occured, retrying
#define MPCBLOCK                 0x0010      // Problem encountered while controlling group
#define MPCFINISHED              0x0020      // Processing on group completed
#define MPCRESTORE               0x0040      // restore

/* Structures used to decode messages recieved from Harris system. Any message
   recieved in a buffer declared with this union will have the type available
   for determining which type of message to decode.  Also by default the
   buffer is guarenteed to be large enough to accept the largest message */

typedef struct _XA21TIME {
    ULONG Time;                 // Seconds
    USHORT MilliSeconds;        // MilliSeconds
    USHORT DSTFlag;             // DST Flag
} XA21TIME;

/* Message type 302 - Nick test status
   This is sent to lmsdlnk as a nick acknowledgment, trick or treat */
typedef struct _NICKTESTSTAT {
    ULONG Function;
    XA21TIME Time;
    USHORT State;
} NICKTESTSTAT;

/* Message type 506 - Load SCRAM status
   This is sent to lmsdlnk as a scram acknowledgment, nothing more. */
typedef struct _LOADSCRAMSTAT {
    ULONG Function;
    XA21TIME Time;
    USHORT State;
} LOADSCRAMSTAT;

typedef struct _LOADRETRIES {
    ULONG Function;
    XA21TIME Time;
    USHORT Retries;
} LOADRETRIES;

/*
  Note, we only handle MPC messages.
*/
typedef struct _XASTRATEGY {
    CHAR StrategyName[8 + 1];                   // Name of this Strategy table
    CHAR ParamLinkName[8 + 1];                  // Name of the parameter table this strat is linked to
    USHORT StratType;                           // type of strategy
    USHORT Priority;                            // Priority 1 to 99
    USHORT WeekDays;                            // bit 1-7 rep sun-sat, set to 1is ctr day 8 is holidays
    ULONG StartTime[3];                         // Start Time 1 to 3
    ULONG StopTime[3];                          // Stop Time 1 to 3
    ULONG Offset[3];                            // 0 = Start offset, 1 = Stop offset, 2 = Restore offset
    USHORT TimeInterval;                        // period or interval between sends
    SHORT SetupIntensity;                       // Percent or number of Groups used in setup
    USHORT ShedTime;                            // Shed Time or cycle rate for Versacom
    USHORT StatusFlag;                          // Status bits
    USHORT OffOnCommand;                        // command for off-on
    SHORT Intensity;                            // intensity we are controlling
    USHORT CurrentStatus;                       // current state of this strategy
} XASTRATEGY;

#define XAMAXSTRATEGY   ((8192 - sizeof (ULONG) - sizeof (XA21TIME) - sizeof (USHORT)) / sizeof (XASTRATEGY))

typedef struct _XAGROUP_HIST_REPORT {
    CHAR GroupName[STANDNAMLEN + 1];
    CHAR MonitorName[8 + 1];
    USHORT StatusFlag;
    ULONG ControlStartTime;
    ULONG ControlStopTime;
    ULONG DailyTotalTime;
    ULONG SeasonTotalTime;
    ULONG StartHourTime;           // time the current hour started
    ULONG PeriodStartTime;         // time the current period started
    SHORT ActualPeriod;            // current period in minutes
    ULONG AdditionalDailyTotal;    // this is the additional daily total if we k
    USHORT RepeatCount;
    SHORT CycleRate;
    SHORT Period;
} XAGROUP_HIST_REPORT;

#define XAMAXGROUPHIST  ((8192 - sizeof (ULONG) - sizeof (XA21TIME) - sizeof (USHORT)) / sizeof (XAGROUP_HIST_REPORT))

typedef struct _XATIMESCHEDULE {
    CHAR ScheduleName[STANDNAMLEN + 1];         // name of time schedule
    CHAR GroupName[STANDNAMLEN + 1];            // name of group or macro
    USHORT WeekDays;                            // bit 1-7 rep sun-sat, set 1- 8 is holidays
    ULONG StartTime;                            // Start Time
    ULONG StopTime;                             // Stop Time
    ULONG SendInterval;                         // send interval
    USHORT StatusFlag;                          // status flags
    USHORT Command;                             // command
    USHORT ShedTime;                            // shed time
    USHORT RepeatCount;                         // Start of Dynamic Data Calculated repeats
    USHORT CurrentState;                        // Current State of schedule
} XATIMESCHEDULE;

#define XAMAXTIMESCHED  ((8192 - sizeof (ULONG) - sizeof (XA21TIME) - sizeof (USHORT)) / sizeof (XATIMESCHEDULE))

typedef struct _XA21LMMESS {
    ULONG Function;             // Function Code
    XA21TIME Time;              // Harris time stamp
    union {
        /* Message Type 1000 thru 1302 - MPC Messages */
        struct {
            USHORT AreaCode;
            USHORT IDNumber;
            struct {
                CHAR GroupName[21];
                USHORT State;
                USHORT ShedSequence;
            } Group[MPCMAXGROUPS];
        } MPC;
    } Message;
} XA21LMMESS;


/* Message type 0 - Null Message */
typedef struct _XNULL {
    ULONG Function;
} XNULL;

#pragma pack(pop, xa21lm_packing)     // Restore the prior packing alignment..

class RWTime;

/*
 * CurrentControl is a utility class to help keep track of pending controls.
 */
class CurrentControl
{
public:
    ULONG getMPCFunction() const;
    vector<string> getPendingGroups() const;
    vector<string> getCompletedGroups() const;
    
    unsigned getNumCompletedGroups() const;
    unsigned getNumPendingGroups() const;
    
    int getCommandedState(const string& group_name) const;
    time_t getExpirationTime() const;

    bool isGroupPending(const string& group_name) const;
    
    bool setGroupCompleted(const string& group_name);

    static CurrentControl createCurrentControl(XA21LMMESS* lm_msg);

    void dump();
    
private:
    ULONG _mpc_function;
    time_t _expiration_time;
    vector< pair<string, unsigned> > _pending_groups;
    vector< pair<string, unsigned> > _completed_groups;
};

class IM_EX_FDRXA21LM CtiFDR_XA21LM : public CtiFDRSingleSocket
{                                    
    typedef CtiFDRSingleSocket Inherited;

    public:
        // constructors and destructors
        CtiFDR_XA21LM(); 

        virtual ~CtiFDR_XA21LM();

        virtual int processMessageFromForeignSystem (CHAR *data);
        virtual CHAR *buildForeignSystemHeartbeatMsg (void);
        virtual CHAR *buildForeignSystemMsg (CtiFDRPoint &aPoint);
        virtual int getMessageSize(CHAR *data);
        virtual RWCString decodeClientName(CHAR *data);
	
        virtual int readConfig( void );

        virtual int processRegistrationMessage(CHAR *data);
        virtual int processControlMessage(CHAR *data);

	CHAR* buildMPCStatus(const CurrentControl& ctrl, const string& group_name);
	CHAR* buildMPCUnsolicited(const string& group_name, unsigned state);
	
	void saveControl(XA21LMMESS* ctrl_msg);
	void cleanupCurrentControls();
	
	void MPCStatusUpdateThr();
	
        ULONG       ForeignToYukonQuality (ULONG aQuality);
        ULONG       ForeignToYukonStatus (ULONG aStatus);
        RWTime      ForeignToYukonTime (PCHAR aTime);

        // end getters and setters
        static const CHAR * KEY_LISTEN_PORT_NUMBER;
        static const CHAR * KEY_TIMESTAMP_WINDOW;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_QUEUE_FLUSH_RATE;
        static const CHAR * KEY_DEBUG_MODE;
        static const CHAR * KEY_OUTBOUND_SEND_RATE;
        static const CHAR * KEY_OUTBOUND_SEND_INTERVAL;

        RWCString   YukonToForeignTime (RWTime aTimeStamp);
        ULONG         YukonToForeignQuality (ULONG aQuality);
        ULONG         YukonToForeignStatus (ULONG aStatus);
	ULONG YukonToXA21Time(time_t time, bool is_dst, XA21TIME* xa21_time);
	
        enum {XA21LM_Open = 0, XA21LM_Closed = 1, XA21LM_Invalid=99};
        virtual bool translateAndUpdatePoint(CtiFDRPoint *translationPoint, int aDestinationIndex);
        virtual bool isRegistrationNeeded(void);

	void dumpXA21LMMessage(XA21LMMESS* lm_msg);
	    
private:

	int stripAreaCode(string& group_name);
	
	/* A new MPC control will create an entry in this vector
	   Either all the groups have to switch to the correct
	   state or the control must timeout to be removed from here */
	vector<CurrentControl> _current_controls;
	CtiCriticalSection _control_cs;
};


#endif  //  #ifndef __FDRXA21LM_H__

