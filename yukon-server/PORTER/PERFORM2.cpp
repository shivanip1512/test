#pragma title ( "Port and Remote Performance Routines" )
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   PERFORM2
*
* Date:          5-93
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PERFORM2.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        Perform2.c

    Purpose:
        Routines to maintain and update statistics on ports and remotes

    The following procedures are contained in this module:
        RemotePerfUpdate                PortPerform
        Port24Perform                   PortFailures
        RemotePerform                   Remote24Perform
        RemoteFailures

    Initial Date:
        5-93

    Revision History:
        Unknown prior to 8-93
        9-6-93  Converted to 32 bit                             WRO
        11-1-93   Modified to keep stats temporarily in memory    TRH

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

/* Routines used for performance calculations for ports and remotes */

#include <stdlib.h>
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>
#include <math.h>
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "drp.h"
#include "elogger.h"
#include "alarmlog.h"
#include "porter.h"

#include "portglob.h"
#include "c_port_interface.h"

#ifndef IMPORT
   #define IMPORT
   #include "perform.h"
   IM_EX_CTIBASE extern PERFGLOBS *PerfGlobs;
   #undef IMPORT
#else
   #include "perform.h"
   IM_EX_CTIBASE extern PERFGLOBS *PerfGlobs;
#endif


/* Routine called to update the performance counts for Remotes */
RemotePerfUpdate (REMOTEPERF *RemotePerf, ERRSTRUCT *ErrorRecord)
{
    REMOTE RemoteRecord;
    CtiPort PortRecord;
    ULONG i;
    USHORT Percent;
    struct timeb TimeB;
    CHAR PortName[STANDNAMLEN];

    /* Process the error */
    ErrorRecord->Error = RemotePerf->Error;
    if (ErrorRecord->Error) {
        /* get the error record */
        if ((i = getError (ErrorRecord)) != NORMAL) {
            return (i);
        }
    }
    else {
        ErrorRecord->Type = NORMAL;
    }

    /* Use RemoteRecord->Stats from memory (not disk) */
    RemoteRecord.Port = RemotePerf->Port;
    RemoteRecord.Remote = RemotePerf->Remote;

    /* If this is one of the 0xffff ones ignore it */
    if (RemoteRecord.Remote == 0xffff) {
        return (NORMAL);
    }

    /* Check if this is a valid record */
    if (CCUInfo.findValue(RemoteRecord.Device) == NULL) {
        return (BADCCU);
    }

    /* Only worry about port if not a broadcast */
    if (RemoteRecord.Remote != CCUGLOBAL && RemoteRecord.Remote != RTUGLOBAL) {
        /* Use PortRecord->Stats from memory for this remote */
        PortRecord.Port = RemoteRecord.Port;
    }


    /* Update the communications count */
    CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorLog[NORMAL]++;
    CCUInfo.findValue(RemoteRecord.Device)->Stats.Error24Log[NORMAL]++;
    CCUInfo.findValue(RemoteRecord.Device)->Stats.Error24Roll[0][NORMAL]++;
    if (RemoteRecord.Remote != CCUGLOBAL && RemoteRecord.Remote != RTUGLOBAL) {
        PortStats[PortRecord.Port]->Stats.ErrorLog[NORMAL]++;
        PortStats[PortRecord.Port]->Stats.Error24Log[NORMAL]++;
        PortStats[PortRecord.Port]->Stats.Error24Roll[0][NORMAL]++;
    }

    UCTFTime (&TimeB);

    /* Unless its a normal update the type count */
    if (RemotePerf->Error) {
        CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorLog[ErrorRecord->Type]++;
        if (!(CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags & PERCENTLOW)) {
            if (RemotePerform (&CCUInfo.findValue(RemoteRecord.Device)->Stats, &Percent)) {

                /* Load the complete Remote Record */
                if ((i = RemoteGetPortRemoteEqual (&RemoteRecord)) != NORMAL)
                    return (i);
                if ((i = RemoteLock (&RemoteRecord)) != NORMAL)
                    return (i);
                if (RemoteRecord.Stats.ErrorResetTime != CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime) {
                    CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime = RemoteRecord.Stats.ErrorResetTime;
                    for (i = 0; i <= ERRLOGENTS; i++) {
                         CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorLog[i] = RemoteRecord.Stats.ErrorLog[i];
                    }
                }

                /* Log a percentage message */
                LogCommPercentLow (RemoteRecord.RemoteName,
                                   REMOTEPERCENTLOW,
                                   &TimeB,
                                   Percent,
                                   PerfGlobs->PortMinPercent);

                CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags |= PERCENTLOW;
                RemoteRecord.Stats = CCUInfo.findValue(RemoteRecord.Device)->Stats;
                if (RemoteFastUpdate (&RemoteRecord)) {
                    RemoteUnLock (&RemoteRecord);
                    return (UPDATEERROR);
                }
            }
        }

        CCUInfo.findValue(RemoteRecord.Device)->Stats.Error24Log[ErrorRecord->Type]++;
        if (!(CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags & PERCENT24LOW)) {
            if (Remote24Perform (&CCUInfo.findValue(RemoteRecord.Device)->Stats,
                                 &Percent)) {
                /* Load the complete Remote Record */
                if ((i = RemoteGetPortRemoteEqual (&RemoteRecord)) != NORMAL)
                    return (i);
                if ((i = RemoteLock (&RemoteRecord)) != NORMAL)
                    return (i);
                if (RemoteRecord.Stats.ErrorResetTime != CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime) {
                    CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime = RemoteRecord.Stats.ErrorResetTime;
                    for (i = 0; i <= ERRLOGENTS; i++) {
                         CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorLog[i] = RemoteRecord.Stats.ErrorLog[i];
                    }
                }

                /* Log a percentage message */
                LogCommPercentLow (RemoteRecord.RemoteName,
                                     REMOTEPERCENT24LOW,
                                     &TimeB,
                                     Percent,
                                     PerfGlobs->PortMinPercent);

                CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags |= PERCENT24LOW;
                RemoteRecord.Stats = CCUInfo.findValue(RemoteRecord.Device)->Stats;
                if (RemoteFastUpdate (&RemoteRecord)) {
                    RemoteUnLock (&RemoteRecord);
                    return (UPDATEERROR);
                }
            }
        }

        CCUInfo.findValue(RemoteRecord.Device)->Stats.Error24Roll[0][ErrorRecord->Type]++;
        CCUInfo.findValue(RemoteRecord.Device)->Stats.FailureCount++;

        if (!(CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags & CONSECUTIVEFAILURE)) {
            if (RemoteFailures (&CCUInfo.findValue(RemoteRecord.Device)->Stats)) {
                /* Load the complete Remote Record */
                if ((i = RemoteGetPortRemoteEqual (&RemoteRecord)) != NORMAL)
                    return (i);
                if ((i = RemoteLock (&RemoteRecord)) != NORMAL)
                    return (i);
                if (RemoteRecord.Stats.ErrorResetTime != CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime) {
                    CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime = RemoteRecord.Stats.ErrorResetTime;
                    for (i = 0; i <= ERRLOGENTS; i++) {
                         CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorLog[i] = RemoteRecord.Stats.ErrorLog[i];
                    }
                }

                /* Log it */
                LogTooManyFailures (RemoteRecord.RemoteName,
                                    REMOTECOMMFAILURE,
                                    &TimeB,
                                    PerfGlobs->RemoteMaxFails);

                CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags |= CONSECUTIVEFAILURE;
                switch (RemoteRecord.Type) {
                    case TYPE_CCU711:
                        CCUQueueFlush (RemoteRecord.Port,
                                       RemoteRecord.Remote);

                }
                RemoteRecord.Stats = CCUInfo.findValue(RemoteRecord.Device)->Stats;
                if (RemoteFastUpdate (&RemoteRecord)) {
                    RemoteUnLock (&RemoteRecord);
                    return (UPDATEERROR);
                }
            }
        }

        if (RemoteRecord.Remote != CCUGLOBAL && RemoteRecord.Remote != RTUGLOBAL) {
            PortStats[PortRecord.Port]->Stats.ErrorLog[ErrorRecord->Type]++;
            if (!(PortStats[PortRecord.Port]->Stats.ErrorDSTFlags & PERCENTLOW)) {
                if (PortPerform (&PortStats[PortRecord.Port]->Stats,
                                 &Percent)) {
                    /* Load the complete Port Record */
                    if ((i = PortLock (&PortRecord)) != NORMAL)
                        return (i);
                    if (PortRecord.Stats.ErrorResetTime != PortStats[PortRecord.Port]->Stats.ErrorResetTime) {
                        PortStats[PortRecord.Port]->Stats.ErrorResetTime = PortRecord.Stats.ErrorResetTime;
                        for (i = 0; i <= ERRLOGENTS; i++) {
                             PortStats[PortRecord.Port]->Stats.ErrorLog[i] = PortRecord.Stats.ErrorLog[i];
                        }
                    }

                    /* Figure out what to use for a port name */
                    if (PortRecord.Description[0] != ' ')
                        memcpy (PortName, PortRecord.Description, STANDNAMLEN);
                    else
                        memcpy (PortName, PortRecord.PortName, STANDNAMLEN);

                    /* Log a percentage message */
                    LogCommPercentLow (PortName,
                                       PORTPERCENTLOW,
                                       &TimeB,
                                       Percent,
                                       PerfGlobs->PortMinPercent);

                    PortStats[PortRecord.Port]->Stats.ErrorDSTFlags |= PERCENTLOW;
                    PortRecord.Stats = PortStats[PortRecord.Port]->Stats;
                    if (PortFastUpdate (&PortRecord)) {
                        PortUnLock (&PortRecord);
                        return (UPDATEERROR);
                    }
                }
            }

            PortStats[PortRecord.Port]->Stats.Error24Log[ErrorRecord->Type]++;
            if (!(PortStats[PortRecord.Port]->Stats.ErrorDSTFlags & PERCENT24LOW)) {
                if (Port24Perform (&PortStats[PortRecord.Port]->Stats,
                                   &Percent)) {
                    /* Load the complete Port Record */
                    if ((i = PortLock (&PortRecord)) != NORMAL)
                        return (i);
                    if (PortRecord.Stats.ErrorResetTime != PortStats[PortRecord.Port]->Stats.ErrorResetTime) {
                        PortStats[PortRecord.Port]->Stats.ErrorResetTime = PortRecord.Stats.ErrorResetTime;
                        for (i = 0; i <= ERRLOGENTS; i++) {
                             PortStats[PortRecord.Port]->Stats.ErrorLog[i] = PortRecord.Stats.ErrorLog[i];
                        }
                    }

                    /* Figure out what to use for a port name */
                    if (PortRecord.Description[0] != ' ')
                        memcpy (PortName, PortRecord.Description, STANDNAMLEN);
                    else
                        memcpy (PortName, PortRecord.PortName, STANDNAMLEN);

                    /* Log a percentage message */
                    LogCommPercentLow (PortName,
                                       PORTPERCENT24LOW,
                                       &TimeB,
                                       Percent,
                                       PerfGlobs->PortMinPercent);

                    PortStats[PortRecord.Port]->Stats.ErrorDSTFlags |= PERCENT24LOW;
                    PortRecord.Stats = PortStats[PortRecord.Port]->Stats;
                    if (PortFastUpdate (&PortRecord)) {
                        PortUnLock (&PortRecord);
                        return (UPDATEERROR);
                    }
                }
            }

            PortStats[PortRecord.Port]->Stats.Error24Roll[0][ErrorRecord->Type]++;
            if (ErrorRecord->Type == ERRTYPEPHONE)
                PortStats[PortRecord.Port]->Stats.FailureCount++;
            if (!(PortStats[PortRecord.Port]->Stats.ErrorDSTFlags & CONSECUTIVEFAILURE)) {
                if (PortFailures (&PortStats[PortRecord.Port]->Stats)) {
                    /* Load the complete Port Record */
                    if ((i = PortLock (&PortRecord)) != NORMAL)
                        return (i);
                    if (PortRecord.Stats.ErrorResetTime != PortStats[PortRecord.Port]->Stats.ErrorResetTime) {
                        PortStats[PortRecord.Port]->Stats.ErrorResetTime = PortRecord.Stats.ErrorResetTime;
                        for (i = 0; i <= ERRLOGENTS; i++) {
                             PortStats[PortRecord.Port]->Stats.ErrorLog[i] = PortRecord.Stats.ErrorLog[i];
                        }
                    }

                    /* Figure out what to use for a port name */
                    if (PortRecord.Description[0] != ' ')
                        memcpy (PortName, PortRecord.Description, STANDNAMLEN);
                    else
                        memcpy (PortName, PortRecord.PortName, STANDNAMLEN);

                    /* Log it */
                    LogTooManyFailures (PortName,
                                        PORTCOMMFAILURE,
                                        &TimeB,
                                        PerfGlobs->PortMaxFails);

                    PortStats[PortRecord.Port]->Stats.ErrorDSTFlags |= CONSECUTIVEFAILURE;
                    PortRecord.Stats = PortStats[PortRecord.Port]->Stats;
                    if (PortFastUpdate (&PortRecord)) {
                        PortUnLock (&PortRecord);
                        return (UPDATEERROR);
                    }
                }
            }
        }
    }
    else {
        /* Check if we need to change percentage stats on Remote */
        if (CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags & PERCENTLOW ||
                !(CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags & FIRSTPROCESSED)) {
            if (!(RemotePerform (&CCUInfo.findValue(RemoteRecord.Device)->Stats,
                                 &Percent))) {
                /* Load the complete Remote Record */
                if ((i = RemoteGetPortRemoteEqual (&RemoteRecord)) != NORMAL)
                    return (i);
                if ((i = RemoteLock (&RemoteRecord)) != NORMAL)
                    return (i);
                if (RemoteRecord.Stats.ErrorResetTime != CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime) {
                    CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime = RemoteRecord.Stats.ErrorResetTime;
                    for (i = 0; i <= ERRLOGENTS; i++) {
                         CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorLog[i] = RemoteRecord.Stats.ErrorLog[i];
                    }
                }

                /* Clear this mother */
                LogCommPercentLow (RemoteRecord.RemoteName,
                                   REMOTEPERCENTLOW,
                                   &TimeB,
                                   Percent,
                                   PerfGlobs->RemoteMinPercent);

                CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags &= ~ PERCENTLOW;
                RemoteRecord.Stats = CCUInfo.findValue(RemoteRecord.Device)->Stats;
                if (RemoteFastUpdate (&RemoteRecord)) {
                    RemoteUnLock (&RemoteRecord);
                    return (UPDATEERROR);
                }
            }
        }

        if (CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags & PERCENT24LOW ||
                !(CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags & FIRSTPROCESSED)) {
            if (!(Remote24Perform (&CCUInfo.findValue(RemoteRecord.Device)->Stats,
                                   &Percent))) {
                /* Load the complete Remote Record */
                if ((i = RemoteGetPortRemoteEqual (&RemoteRecord)) != NORMAL)
                    return (i);
                if ((i = RemoteLock (&RemoteRecord)) != NORMAL)
                    return (i);
                if (RemoteRecord.Stats.ErrorResetTime != CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime) {
                    CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime = RemoteRecord.Stats.ErrorResetTime;
                    for (i = 0; i <= ERRLOGENTS; i++) {
                         CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorLog[i] = RemoteRecord.Stats.ErrorLog[i];
                    }
                }

                /* Clear this mother */
                LogCommPercentLow (RemoteRecord.RemoteName,
                                   REMOTEPERCENT24LOW,
                                   &TimeB,
                                   Percent,
                                   PerfGlobs->RemoteMinPercent);

                CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags &= ~ PERCENT24LOW;
                RemoteRecord.Stats = CCUInfo.findValue(RemoteRecord.Device)->Stats;
                if (RemoteFastUpdate (&RemoteRecord)) {
                    RemoteUnLock (&RemoteRecord);
                    return (UPDATEERROR);
                }
            }
        }

        if (CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags & CONSECUTIVEFAILURE ||
                !(CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags & FIRSTPROCESSED)) {
            /* Load the complete Remote Record */
            if ((i = RemoteGetPortRemoteEqual (&RemoteRecord)) != NORMAL)
                return (i);
            if ((i = RemoteLock (&RemoteRecord)) != NORMAL)
                return (i);
            if (RemoteRecord.Stats.ErrorResetTime != CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime) {
                CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime = RemoteRecord.Stats.ErrorResetTime;
                for (i = 0; i <= ERRLOGENTS; i++) {
                     CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorLog[i] = RemoteRecord.Stats.ErrorLog[i];
                }
            }

            /* Log the success */
            LogNewSuccess (RemoteRecord.RemoteName,
                           REMOTECOMMSUCCESS,
                           &TimeB);

            CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags &= ~CONSECUTIVEFAILURE;
            CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags |= FIRSTPROCESSED;
            RemoteRecord.Stats = CCUInfo.findValue(RemoteRecord.Device)->Stats;
            if (RemoteFastUpdate (&RemoteRecord)) {
                RemoteUnLock (&RemoteRecord);
                return (UPDATEERROR);
            }
        }
        CCUInfo.findValue(RemoteRecord.Device)->Stats.FailureCount = 0L;
        CCUInfo.findValue(RemoteRecord.Device)->Stats.LastGoodTime = TimeB.time;

        if (TimeB.dstflag) {
            CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags |= LASTGOODDSTMASK;
            if (RemoteRecord.Remote != CCUGLOBAL && RemoteRecord.Remote != RTUGLOBAL)
                PortStats[PortRecord.Port]->Stats.ErrorDSTFlags |= LASTGOODDSTMASK;
        }
        else {
            CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorDSTFlags &= ~LASTGOODDSTMASK;
            if (RemoteRecord.Remote != CCUGLOBAL && RemoteRecord.Remote != RTUGLOBAL)
                PortStats[PortRecord.Port]->Stats.ErrorDSTFlags &= ~LASTGOODDSTMASK;
        }
    }

    if (RemoteRecord.Remote != CCUGLOBAL && RemoteRecord.Remote != RTUGLOBAL) {
        if (ErrorRecord->Type != ERRTYPEPHONE) {
            if (PortStats[PortRecord.Port]->Stats.ErrorDSTFlags & PERCENTLOW ||
                    !(PortStats[PortRecord.Port]->Stats.ErrorDSTFlags & FIRSTPROCESSED)) {
                if (!(PortPerform (&PortStats[PortRecord.Port]->Stats,
                                   &Percent))) {
                    /* Load the complete Port Record */
                    if ((i = PortLock (&PortRecord)) != NORMAL)
                        return (i);
                    if (PortRecord.Stats.ErrorResetTime != PortStats[PortRecord.Port]->Stats.ErrorResetTime) {
                        PortStats[PortRecord.Port]->Stats.ErrorResetTime = PortRecord.Stats.ErrorResetTime;
                        for (i = 0; i <= ERRLOGENTS; i++) {
                             PortStats[PortRecord.Port]->Stats.ErrorLog[i] = PortRecord.Stats.ErrorLog[i];
                        }
                    }

                    if (PortRecord.Description[0] != ' ')
                        memcpy (PortName, PortRecord.Description, STANDNAMLEN);
                    else
                        memcpy (PortName, PortRecord.PortName, STANDNAMLEN);

                    /* Clear this mother */
                    LogCommPercentLow (PortName,
                                       PORTPERCENTLOW,
                                       &TimeB,
                                       Percent,
                                       PerfGlobs->PortMinPercent);

                    PortStats[PortRecord.Port]->Stats.ErrorDSTFlags &= ~ PERCENTLOW;
                    PortRecord.Stats = PortStats[PortRecord.Port]->Stats;
                    if (PortFastUpdate (&PortRecord)) {
                        PortUnLock (&PortRecord);
                        return (UPDATEERROR);
                    }
                }
            }

            if (PortStats[PortRecord.Port]->Stats.ErrorDSTFlags & PERCENT24LOW ||
                    !(PortStats[PortRecord.Port]->Stats.ErrorDSTFlags & FIRSTPROCESSED)) {
                if (!(Port24Perform (&PortStats[PortRecord.Port]->Stats,
                                     &Percent))) {
                    /* Load the complete Port Record */
                    if ((i = PortLock (&PortRecord)) != NORMAL)
                        return (i);
                    if (PortRecord.Stats.ErrorResetTime != PortStats[PortRecord.Port]->Stats.ErrorResetTime) {
                        PortStats[PortRecord.Port]->Stats.ErrorResetTime = PortRecord.Stats.ErrorResetTime;
                        for (i = 0; i <= ERRLOGENTS; i++) {
                             PortStats[PortRecord.Port]->Stats.ErrorLog[i] = PortRecord.Stats.ErrorLog[i];
                        }
                    }

                    if (PortRecord.Description[0] != ' ')
                        memcpy (PortName, PortRecord.Description, STANDNAMLEN);
                    else
                        memcpy (PortName, PortRecord.PortName, STANDNAMLEN);

                    /* Clear this mother */
                    LogCommPercentLow (PortName,
                                       PORTPERCENT24LOW,
                                       &TimeB,
                                       Percent,
                                       PerfGlobs->PortMinPercent);

                    PortStats[PortRecord.Port]->Stats.ErrorDSTFlags &= ~ PERCENT24LOW;
                    PortRecord.Stats = PortStats[PortRecord.Port]->Stats;
                    if (PortFastUpdate (&PortRecord)) {
                        PortUnLock (&PortRecord);
                        return (UPDATEERROR);
                    }
                }
            }

            if (PortStats[PortRecord.Port]->Stats.ErrorDSTFlags & CONSECUTIVEFAILURE ||
                    !(PortStats[PortRecord.Port]->Stats.ErrorDSTFlags & FIRSTPROCESSED)) {
                /* Load the complete Port Record */
                if ((i = PortLock (&PortRecord)) != NORMAL)
                    return (i);
                if (PortRecord.Stats.ErrorResetTime != PortStats[PortRecord.Port]->Stats.ErrorResetTime) {
                    PortStats[PortRecord.Port]->Stats.ErrorResetTime = PortRecord.Stats.ErrorResetTime;
                    for (i = 0; i <= ERRLOGENTS; i++) {
                         PortStats[PortRecord.Port]->Stats.ErrorLog[i] = PortRecord.Stats.ErrorLog[i];
                    }
                }

                /* Figure out what to use for a port name */
                if (PortRecord.Description[0] != ' ')
                    memcpy (PortName, PortRecord.Description, STANDNAMLEN);
                else
                    memcpy (PortName, PortRecord.PortName, STANDNAMLEN);

                /* Log the success */
                LogNewSuccess (PortName,
                               PORTCOMMSUCCESS,
                               &TimeB);

                PortStats[PortRecord.Port]->Stats.ErrorDSTFlags &= ~CONSECUTIVEFAILURE;
                PortStats[PortRecord.Port]->Stats.ErrorDSTFlags |= FIRSTPROCESSED;
                PortRecord.Stats = PortStats[PortRecord.Port]->Stats;
                if (PortFastUpdate (&PortRecord)) {
                    PortUnLock (&PortRecord);
                    return (UPDATEERROR);
                }
            }
            PortStats[PortRecord.Port]->Stats.FailureCount = 0L;
            PortStats[PortRecord.Port]->Stats.LastGoodTime = TimeB.time;
        }
    }

    /* Now update the RemoteRecord */

    i = NORMAL;
    if (!(CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorLog[NORMAL] % RemotePerfUpdateCount)) {
        if ((i = RemoteGetPortRemoteEqual (&RemoteRecord)) != NORMAL)
            return (i);
        if ((i = RemoteLock (&RemoteRecord)) != NORMAL)
            return (i);
        if (RemoteRecord.Stats.ErrorResetTime != CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime) {
           CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorResetTime = RemoteRecord.Stats.ErrorResetTime;
           for (i = 0; i <= ERRLOGENTS; i++) {
               CCUInfo.findValue(RemoteRecord.Device)->Stats.ErrorLog[i] = RemoteRecord.Stats.ErrorLog[i];
           }
        }
        RemoteRecord.Stats = CCUInfo.findValue(RemoteRecord.Device)->Stats;
        if (RemoteFastUpdate (&RemoteRecord)) {
            RemoteUnLock (&RemoteRecord);
            return (UPDATEERROR);
        }
    }

    /* And the port record */
    if (RemoteRecord.Remote != CCUGLOBAL && RemoteRecord.Remote != RTUGLOBAL) {
        if (!(PortStats[PortRecord.Port]->Stats.ErrorLog[NORMAL] % PortPerfUpdateCount)) {
            if ((i = PortLock (&PortRecord)) != NORMAL)
                return (i);
            if (PortRecord.Stats.ErrorResetTime != PortStats[PortRecord.Port]->Stats.ErrorResetTime) {
                PortStats[PortRecord.Port]->Stats.ErrorResetTime = PortRecord.Stats.ErrorResetTime;
                for (i = 0; i <= ERRLOGENTS; i++) {
                    PortStats[PortRecord.Port]->Stats.ErrorLog[i] = PortRecord.Stats.ErrorLog[i];
                }
            }
            PortRecord.Stats = PortStats[PortRecord.Port]->Stats;
            if (PortFastUpdate (&PortRecord)) {
                PortUnLock (&PortRecord);
                return (UPDATEERROR);
            }
        }
    }
    return (NORMAL);
}

/* Routine to test if port failure has occurred */
PortFailures (STATS *Stats)
{
    if (PerfGlobs == NULL) {
        if (InitPerfGlobs ()) {
            return (FALSE);
        }
    }

    if (Stats->FailureCount > PerfGlobs->PortMaxFails)
        return (TRUE);
    else
        return (FALSE);

}


/* Routine to test if port is operating below percentage */
PortPerform (STATS *Stats,
             PUSHORT Percent)

{
    if (PerfGlobs == NULL) {
        if (InitPerfGlobs ()) {
            return (FALSE);
        }
    }

    if (Stats->ErrorLog[NORMAL] >= PerfGlobs->MinCommPercentCalc) {
        if ((*Percent = (USHORT)(100 - (100L * Stats->ErrorLog[ERRTYPEPHONE]) / Stats->ErrorLog[NORMAL])) <
                PerfGlobs->PortMinPercent)
            return (TRUE);
    }

    return (FALSE);
}


/* Routine to test if port is operating below percentage for this 24 hour period */
Port24Perform (STATS *Stats,
               PUSHORT Percent)

{
    if (PerfGlobs == NULL) {
        if (InitPerfGlobs ()) {
            return (FALSE);
        }
    }

    if (Stats->Error24Log[NORMAL] >= PerfGlobs->MinCommPercentCalc) {
        if ((*Percent = (USHORT)(100 - (100L * Stats->Error24Log[ERRTYPEPHONE]) / Stats->Error24Log[NORMAL])) <
                PerfGlobs->PortMinPercent)
            return (TRUE);
    }

    return (FALSE);
}


/* Routine to test if  remote failure has occurred */
RemoteFailures (STATS *Stats)
{
    if (PerfGlobs == NULL) {
        if (InitPerfGlobs ()) {
            return (FALSE);
        }
    }

    if (Stats->FailureCount > PerfGlobs->RemoteMaxFails)
        return (TRUE);
    else
        return (FALSE);

}


/* Routine to test if Remote is operating below percentage */
RemotePerform (STATS *Stats,
               PUSHORT Percent)

{
    if (PerfGlobs == NULL) {
        if (InitPerfGlobs ()) {
            return (FALSE);
        }
    }

    if (Stats->ErrorLog[NORMAL] >= PerfGlobs->MinCommPercentCalc) {
        if ((*Percent = (USHORT)(100 - (100L * Stats->ErrorLog[ERRTYPEPHONE]) / Stats->ErrorLog[NORMAL])) <
                PerfGlobs->RemoteMinPercent)
            return (TRUE);
    }

    return (FALSE);
}


/* Routine to test if Remote is operating below percentage for this 24 hour period */
Remote24Perform (STATS *Stats,
                 PUSHORT Percent)

{
    if (PerfGlobs == NULL) {
        if (InitPerfGlobs ()) {
            return (FALSE);
        }
    }

    if (Stats->Error24Log[NORMAL] >= PerfGlobs->MinCommPercentCalc) {
        if ((*Percent = (USHORT)(100 - (100L * Stats->Error24Log[ERRTYPEPHONE]) / Stats->Error24Log[NORMAL])) <
                PerfGlobs->RemoteMinPercent)
            return (TRUE);
    }

    return (FALSE);
}



