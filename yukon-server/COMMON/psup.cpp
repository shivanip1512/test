#include "yukon.h"


#pragma title ( "Various Support Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Orignal Programmer:
        Ben Wallace

    FileName:
        PSUP.C

    Purpose:
        Various Support Rotines for the programs including that easy to
        follow, funky in function CheckDataStateQuality Routine.

    The following procedures are contained in this module:
        CheckDataStateQuality           SavePointHistory
        LogPointValue                   Float2CharFormat
        getaUOMName                      getxxxDQLabel
        setPlugValue                    Long2CharFormat
        Short2CharFormat                getShedTime
        getCommandName                  VersacomWordFill
        VPercentControl                 ClearAlarmStatus

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-6-93  Converted to 32 bit                             WRO
        9-14-93 Added CurrentTID Function                       WRO
        5-14-96 Added Alternate Plug Method for Marshall        BDW

   -------------------------------------------------------------------- */

#include <windows.h>       // These next few are required for Win32
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
// #include "btrieve.h"

#include "queues.h"
#include "dllbase.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "drp.h"
#include "device.h"
#include "elogger.h"
#include "alarmlog.h"
#include "lm_auto.h"
#include "group.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "logger.h"
// #include "scanner.h"    // FIX FIX FIX SCANNERSEM is it I think!

#include "c_port_interface.h"

/* global variable for scanner function */
HEV ScannerSem = (HEV) NULL;

int setNextTimeInterval (struct timeb *, ULONG, ULONG *);
int ForceScanCheck (void);

INT NormalLogEvent(SYSTEMLOGMESS *LogMessage, USHORT i)
{
    printf("This MUST BE FIX FIX FIX ed CGP \n");
    return 0;
}

INT StateNameGetEqual (STATENAMETABLE *StateNameRecord)
{
    printf("This MUST BE FIX FIX FIX ed CGP \n");
    return 0;
}

INT VConfigLocate (VERSACONFIG* vcfg, GROUP* grp)
{
    printf("This MUST BE FIX FIX FIX ed CGP \n");
    return 0;
}

INT LogAlarm (ALARM_SUM_STRUCT *a, USHORT b, USHORT c, CTIPOINT *d, USHORT e)
{
    printf("This MUST BE FIX FIX FIX ed CGP \n");
    return 0;
}

IM_EX_CTIBASE INT CheckDataStateQuality (DEVICE *DeviceRecord,
                                         CTIPOINT *PointRecord,
                                         FLOAT *Value,
                                         USHORT DataQual,
                                         ULONG TimeStamp,
                                         USHORT Dst,
                                         USHORT InvalidCode,
                                         USHORT LogFlag)

{
#if 1
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
#else
    USHORT LoggedEvent = FALSE;
    USHORT NewDQCode = NORMALDATA;
    USHORT OldDQCode, AlarmInhibit, Inhibited;
    ULONG Result;
    ALARM_SUM_STRUCT AlarmSumRecord;
    SYSTEMLOGMESS LogMessage;
    CHAR TempMessage[20];
    STATENAMETABLE StateNameRecord;

    /* set to spaces        */
    memset (&AlarmSumRecord, 32, sizeof(AlarmSumRecord));
    memset (&LogMessage, 32, sizeof(LogMessage));

    OldDQCode = PointRecord->CurrentQuality;          // save current quality
    AlarmSumRecord.LogCode = FALSE;                     // clear data quality

    /* make current data the previous data */
    PointRecord->PreviousValue = PointRecord->CurrentValue;
    PointRecord->PreviousQuality = PointRecord->CurrentQuality;
    PointRecord->PreviousTime = PointRecord->CurrentTime;

    if(DeviceRecord->Status & ALARMINHIBIT || PointRecord->Status & ALARMINHIBIT)
    {
        PointRecord->AlarmStatus = 0; // clear the alarm state
        AlarmInhibit = TRUE;      // dont alarm point
    }
    else
        AlarmInhibit = FALSE;      // its ok to alarm point if needed

    if(DeviceRecord->Status & INHIBITED || PointRecord->Status & INHIBITED)
        Inhibited = TRUE;      // point is inhibited
    else
        Inhibited = FALSE;      // its ok to alarm point if needed

    /* set alarm device-point name and also set all time stamps */
    AlarmSumRecord.TimeStamp = TimeStamp;
    PointRecord->CurrentTime = TimeStamp;
    LogMessage.TimeStamp = TimeStamp;
    LogMessage.Originator = FALSE;      // don't care about this

    /* this serves as an init and sets the dst flag */
    if(Dst)
        AlarmSumRecord.StatusFlag = DSTACTIVE;
    else
        AlarmSumRecord.StatusFlag = FALSE;

    /* set dst flag for normal log message */
    LogMessage.StatusFlag = AlarmSumRecord.StatusFlag;

    switch(PointRecord->PointType)
    {
    /* handle status type point checks */
    case TWOSTATEPOINT:
    case THREESTATEPOINT:
    case PSEUDOSTATUSPOINT:
        if(Inhibited)
        {
            /* point is inhibited  */
            memcpy(AlarmSumRecord.AlarmText, "POINT INHIBITED", 15);
            *Value = PointRecord->CurrentValue;     /* value must stay the same */
            if(!(PointRecord->CurrentQuality & OUTOFSCAN))
            {
                /* not in this quality so set */
                PointRecord->CurrentQuality &= (DSTACTIVE | DSTACTIVE2);
                PointRecord->CurrentQuality |= OUTOFSCAN;
                NewDQCode = PointRecord->CurrentQuality;
                PointRecord->DQCount = 0;
            }
            else
            {

            }
            NewDQCode = PointRecord->CurrentQuality;
        }
        else if(DataQual & PLUGGED)
        {
            /* data is plugged */
            memcpy(AlarmSumRecord.AlarmText, "PLUGGED STATUS", 14);
            if(PointRecord->CurrentQuality & PLUGGED)
            {
                /* current quality is plugged so increment count */
                if(PointRecord->DQCount < DQCOUNTMAX)
                    ++PointRecord->DQCount;
            }
            else
            {      // dq change so reset count
                PointRecord->CurrentQuality |= PLUGGED;
                PointRecord->DQCount = 1;
            }

            if(!AlarmInhibit)
            {
                /* NOT in alarm inhibit mode so we check for alarm */
                if(OldDQCode == PointRecord->CurrentQuality)
                    /* already in  this quality */
                    LoggedEvent = TRUE;               // make sure not to log again for state change
                else
                {
                    /* check audible flag */
                    if(PointRecord->AudibleMask & ALARMPLUGGED)
                        AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                    else
                        AlarmSumRecord.AcknowlegeFlag = FALSE;

                    /* now see if we wanted an alarm on this guy */
                    if(PointRecord->AlarmMask & CRITPLUGGED)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        AlarmSumRecord.LogCode = PLUGGED;
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        Result = LogAlarm (&AlarmSumRecord,
                                           CRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSPLUGGED);
                    }
                    else if(PointRecord->AlarmMask & NONCRITPLUGGED)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        AlarmSumRecord.LogCode = PLUGGED;
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        Result = LogAlarm (&AlarmSumRecord,
                                           NONCRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSPLUGGED);
                    }
                }  // close alarm inhibit check
            }      // close plug check
        }
        else
        {
            if(PointRecord->CurrentQuality & ~(DSTACTIVE | DSTACTIVE2))
            {
                /* clear dq for normal scan but must presurve dst reset dq count*/
                PointRecord->CurrentQuality &= (DSTACTIVE | DSTACTIVE2);
                PointRecord->DQCount = 1;
            }
            else
            {
                /* current quality was normal so increment count */
                if(PointRecord->DQCount < DQCOUNTMAX)
                    ++PointRecord->DQCount;
            }

            /* check for Current state */
            if(*Value == OPENED && PointRecord->CurrentValue != OPENED)
            {
                /* only check alarming if its not in that state */
                StateNameRecord.StateNameNumber = PointRecord->StateNameNumber;
                if(StateNameRecord.StateNameNumber == -1 || StateNameGetEqual (&StateNameRecord))
                    /* no state name found use default */
                    memcpy(AlarmSumRecord.AlarmText, "?OPEN STATE   ", 14);
                else
                    memcpy(AlarmSumRecord.AlarmText, StateNameRecord.StateMess[0].StateMessage, sizeof(StateNameRecord.StateMess[OPENED - 1].StateMessage));

                if(!AlarmInhibit)
                {
                    /* NOT in alarm inhibit mode so we check for alarm */
                    /* check audible flag */
                    if(PointRecord->AudibleMask & ALARMSTATE1)
                        AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                    else
                        AlarmSumRecord.AcknowlegeFlag = FALSE;

                    /* now see if we wanted an alarm on this guy */
                    if(PointRecord->AlarmMask & CRITSTATE1)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           CRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE1);
                    }
                    else if(PointRecord->AlarmMask & NONCRITSTATE1)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           NONCRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE1);
                    }
                }  // close alarm inhibit check
            }   // close open check
            else if(*Value == CLOSED && PointRecord->CurrentValue != CLOSED)
            {
                /* must add here in the future to get a the message for state name table */
                StateNameRecord.StateNameNumber = PointRecord->StateNameNumber;
                if(StateNameRecord.StateNameNumber == -1 || StateNameGetEqual (&StateNameRecord))
                    /* no state name found use default */
                    memcpy(AlarmSumRecord.AlarmText, "?CLOSED STATE ", 14);
                else
                    memcpy(AlarmSumRecord.AlarmText, StateNameRecord.StateMess[CLOSED - 1].StateMessage, sizeof(StateNameRecord.StateMess[0].StateMessage));

                if(!AlarmInhibit)
                {
                    /* NOT in alarm inhibit mode so we check for alarm */

                    /* check audible flag */
                    if(PointRecord->AudibleMask & ALARMSTATE2)
                        AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                    else
                        AlarmSumRecord.AcknowlegeFlag = FALSE;

                    /* now see if we wanted an alarm on this guy */
                    if(PointRecord->AlarmMask & CRITSTATE2)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           CRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE2);
                    }
                    else if(PointRecord->AlarmMask & NONCRITSTATE2)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           NONCRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE2);
                    }
                } // close alarm inhibit check
            }  // close the close state check
            else if(*Value == INDETERMINATE && PointRecord->PointType != TWOSTATEPOINT && PointRecord->CurrentValue != INDETERMINATE)
            {
                /* must add here in the future to get a the message for state name table */
                StateNameRecord.StateNameNumber = PointRecord->StateNameNumber;
                if(StateNameRecord.StateNameNumber == -1 || StateNameGetEqual (&StateNameRecord))
                    /* no state name found use default */
                    memcpy(AlarmSumRecord.AlarmText, "?STATE THREE  ", 14);
                else
                    memcpy(AlarmSumRecord.AlarmText, StateNameRecord.StateMess[INDETERMINATE - 1].StateMessage, sizeof(StateNameRecord.StateMess[0].StateMessage));

                if(!AlarmInhibit)
                {
                    /* NOT in alarm inhibit mode so we check for alarm */

                    /* check audible flag */
                    if(PointRecord->AudibleMask & ALARMSTATE3)
                        AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                    else
                        AlarmSumRecord.AcknowlegeFlag = FALSE;

                    /* now see if we wanted an alarm on this guy */
                    if(PointRecord->AlarmMask & CRITSTATE3)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           CRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE3);
                    }
                    else if(PointRecord->AlarmMask & NONCRITSTATE3)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           NONCRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE3);
                    }
                } // close alarm inhibit check
            }  // close state 3 check
            else if(*Value == STATEFOUR && PointRecord->CurrentValue != STATEFOUR && PointRecord->PointType == PSEUDOSTATUSPOINT)
            {
                /* must add here in the future to get a the message for state name table */
                StateNameRecord.StateNameNumber = PointRecord->StateNameNumber;
                if(StateNameRecord.StateNameNumber == -1 || StateNameGetEqual (&StateNameRecord))
                    /* no state name found use default */
                    memcpy(AlarmSumRecord.AlarmText, "?STATE FOUR   ", 14);
                else
                    memcpy(AlarmSumRecord.AlarmText, StateNameRecord.StateMess[STATEFOUR - 1].StateMessage, sizeof(StateNameRecord.StateMess[0].StateMessage));

                if(!AlarmInhibit)
                {
                    /* NOT in alarm inhibit mode so we check for alarm */

                    /* check audible flag */
                    if(PointRecord->AudibleMask & ALARMSTATE4)
                        AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                    else
                        AlarmSumRecord.AcknowlegeFlag = FALSE;

                    /* now see if we wanted an alarm on this guy */
                    if(PointRecord->AlarmMask & CRITSTATE4)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           CRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE4);
                    }
                    else if(PointRecord->AlarmMask & NONCRITSTATE4)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           NONCRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE4);
                    }
                } // close alarm inhibit check
            }  // close state five check
            else if(*Value == STATEFIVE && PointRecord->CurrentValue != STATEFIVE && PointRecord->PointType == PSEUDOSTATUSPOINT)
            {
                /* must add here in the future to get a the message for state name table */
                StateNameRecord.StateNameNumber = PointRecord->StateNameNumber;
                if(StateNameRecord.StateNameNumber == -1 || StateNameGetEqual (&StateNameRecord))
                    /* no state name found use default */
                    memcpy(AlarmSumRecord.AlarmText, "?STATE FIVE   ", 14);
                else
                    memcpy(AlarmSumRecord.AlarmText, StateNameRecord.StateMess[STATEFIVE - 1].StateMessage, sizeof(StateNameRecord.StateMess[0].StateMessage));

                if(!AlarmInhibit)
                {
                    /* NOT in alarm inhibit mode so we check for alarm */

                    /* check audible flag */
                    if(PointRecord->AudibleMask & ALARMSTATE5)
                        AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                    else
                        AlarmSumRecord.AcknowlegeFlag = FALSE;

                    /* now see if we wanted an alarm on this guy */
                    if(PointRecord->AlarmMask & CRITSTATE5)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           CRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE5);
                    }
                    else if(PointRecord->AlarmMask & NONCRITSTATE5)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           NONCRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE5);
                    }
                } // close alarm inhibit check
            }  // close state 5 check
            else if(*Value == STATESIX && PointRecord->CurrentValue != STATESIX && PointRecord->PointType == PSEUDOSTATUSPOINT)
            {
                /* must add here in the future to get a the message for state name table */
                StateNameRecord.StateNameNumber = PointRecord->StateNameNumber;
                if(StateNameRecord.StateNameNumber == -1 || StateNameGetEqual (&StateNameRecord))
                    /* no state name found use default */
                    memcpy(AlarmSumRecord.AlarmText, "?STATE SIX    ", 14);
                else
                    memcpy(AlarmSumRecord.AlarmText, StateNameRecord.StateMess[STATESIX - 1].StateMessage, sizeof(StateNameRecord.StateMess[0].StateMessage));

                if(!AlarmInhibit)
                {
                    /* NOT in alarm inhibit mode so we check for alarm */

                    /* check audible flag */
                    if(PointRecord->AudibleMask & ALARMSTATE6)
                        AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                    else
                        AlarmSumRecord.AcknowlegeFlag = FALSE;

                    /* now see if we wanted an alarm on this guy */
                    if(PointRecord->AlarmMask & CRITSTATE6)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           CRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE6);
                    }
                    else if(PointRecord->AlarmMask & NONCRITSTATE6)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = STATEALARM;
                        Result = LogAlarm (&AlarmSumRecord,
                                           NONCRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSSTATE6);
                    }
                }  // close alarm inhibit check
            }  // state 6
            else if(*Value != PointRecord->CurrentValue)
            {
                memcpy(AlarmSumRecord.AlarmText, "INVALID STATE ", 14);

                if(!AlarmInhibit)
                {
                    /* NOT in alarm inhibit mode so we check for alarm */

                    /* check audible flag */
                    if(PointRecord->AudibleMask & ALARMINVALIDSTATE)
                        AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                    else
                        AlarmSumRecord.AcknowlegeFlag = FALSE;

                    /* now see if we wanted an alarm on this guy */
                    if(PointRecord->AlarmMask & CRITINVALIDSTATE)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = INVALID;
                        Result = LogAlarm (&AlarmSumRecord,
                                           CRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSINVALIDSTATE);
                    }
                    else if(PointRecord->AlarmMask & NONCRITINVALIDSTATE)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        AlarmSumRecord.LogCode = INVALID;
                        Result = LogAlarm (&AlarmSumRecord,
                                           NONCRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSINVALIDSTATE);
                    }
                }  // close alarm inhibit check
            }  // close the invalid state

            /* check for abnormal state OpenTime is used in a status point
               for the normal state -1 means not used */
            if(PointRecord->OpenTime != -1 && *Value != PointRecord->OpenTime && LoggedEvent == FALSE && AlarmInhibit == FALSE)
            {
                /* check audible flag */
                if(*Value != PointRecord->CurrentValue)
                {
                    /* just went to abnormal state so check for alarm */
                    if(PointRecord->AudibleMask & ALARMABNORMAL)
                        AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                    else
                        AlarmSumRecord.AcknowlegeFlag = FALSE;

                    if(PointRecord->AlarmMask & CRITABNORMAL)
                    {
                        memcpy(AlarmSumRecord.AlarmText, "ABNORMAL STATE", 14);
                        AlarmSumRecord.LogCode = ABNORMALSTATEALARM;
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        Result = LogAlarm (&AlarmSumRecord,
                                           CRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSABNORMAL);
                        LoggedEvent = TRUE;
                    }
                    else if(PointRecord->AlarmMask & NONCRITABNORMAL)
                    {
                        memcpy(AlarmSumRecord.AlarmText, "ABNORMAL STATE", 14);
                        AlarmSumRecord.LogCode = ABNORMALSTATEALARM;
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        Result = LogAlarm (&AlarmSumRecord,
                                           NONCRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSABNORMAL);
                        LoggedEvent = TRUE;
                    }
                }
            }
        }

        /* state change check for alarm and log event if not logged yet */
        if(*Value != PointRecord->CurrentValue && LoggedEvent == FALSE && AlarmInhibit == FALSE)
        {
            if(AlarmSumRecord.AlarmText[0] == ' ')
                memcpy(AlarmSumRecord.AlarmText, "SC NORMAL SCAN", 14);
            else
            {
                memcpy(TempMessage, "SC ", 3);
                memcpy(TempMessage + 3, AlarmSumRecord.AlarmText, 15);
                memcpy(AlarmSumRecord.AlarmText, TempMessage, 18);
            }
            if(PointRecord->AudibleMask & ALARMSTATECHANGE)
                AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
            else
                AlarmSumRecord.AcknowlegeFlag = FALSE;

            if(PointRecord->AlarmMask & CRITSTATECHANGE)
            {
                AlarmSumRecord.LogCode = STATECHANGE;
                PointRecord->AlarmStatus = ALARMEDUNACK;
                Result = LogAlarm (&AlarmSumRecord,
                                   CRITICALCLASS,
                                   LogFlag,
                                   PointRecord,
                                   ALARMDRPPASSSTATECHANGE);
            }
            else if(PointRecord->AlarmMask & NONCRITSTATECHANGE)
            {
                AlarmSumRecord.LogCode = STATECHANGE;
                PointRecord->AlarmStatus = ALARMEDUNACK;
                Result = LogAlarm (&AlarmSumRecord,
                                   NONCRITICALCLASS,
                                   LogFlag,
                                   PointRecord,
                                   ALARMDRPPASSSTATECHANGE);
            }
            else
            {
                /* log a non-alarm state change */
                memcpy(LogMessage.DeviceName, PointRecord->DeviceName, sizeof(LogMessage.DeviceName));
                memcpy(LogMessage.PointName, PointRecord->PointName, sizeof(LogMessage.PointName));
                memcpy(LogMessage.LogMessage1, AlarmSumRecord.AlarmText, sizeof(LogMessage.LogMessage1));
                LogMessage.EventType = STATECHANGE;
                Result = NormalLogEvent(&LogMessage, LogFlag);
            }
        }
        else if(OldDQCode != PointRecord->CurrentQuality && LoggedEvent == FALSE && AlarmInhibit == FALSE)
        {
            /* state change of data quality */
            if(PointRecord->CurrentQuality == NORMALDATA)
                memcpy(AlarmSumRecord.AlarmText, "SC NORMAL DATA", 14);
            else
            {
                memcpy(TempMessage, "SC ", 3);
                memcpy(TempMessage + 3, AlarmSumRecord.AlarmText, 15);
                memcpy(AlarmSumRecord.AlarmText, TempMessage, 18);
            }

            if(PointRecord->AudibleMask & ALARMSTATECHANGE)
                AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
            else
                AlarmSumRecord.AcknowlegeFlag = FALSE;

            if(PointRecord->AlarmMask & CRITSTATECHANGE)
            {
                AlarmSumRecord.LogCode = STATECHANGE;
                PointRecord->AlarmStatus = ALARMEDUNACK;
                Result = LogAlarm (&AlarmSumRecord,
                                   CRITICALCLASS,
                                   LogFlag,
                                   PointRecord,
                                   ALARMDRPPASSSTATECHANGE);
            }
            else if(PointRecord->AlarmMask & NONCRITSTATECHANGE)
            {
                AlarmSumRecord.LogCode = STATECHANGE;
                PointRecord->AlarmStatus = ALARMEDUNACK;
                Result = LogAlarm (&AlarmSumRecord,
                                   NONCRITICALCLASS,
                                   LogFlag,
                                   PointRecord,
                                   ALARMDRPPASSSTATECHANGE);
            }
            else
            {
                /* log a non-alarm state change */
                memcpy(LogMessage.DeviceName, PointRecord->DeviceName, sizeof(LogMessage.DeviceName));
                memcpy(LogMessage.PointName, PointRecord->PointName, sizeof(LogMessage.PointName));
                memcpy(LogMessage.LogMessage1, AlarmSumRecord.AlarmText, sizeof(LogMessage.LogMessage1));
                LogMessage.EventType = STATECHANGE;
                Result = NormalLogEvent(&LogMessage, LogFlag);
            }
        }

        if(!(PointRecord->CurrentQuality & (PLUGGED | OUTOFSCAN)))
        {
            /* set the new value */
            if(PointRecord->CurrentValue != *Value)
            {
                /* we had a change in data lets check to archive it */
                PointRecord->CurrentValue = *Value;  /* update the value so it archive the new value */
                switch((SHORT)*Value)
                {

                case OPENED:
                case CLOSED:
                case INDETERMINATE:
                case STATEFOUR:
                case STATEFIVE:
                case STATESIX:
                    /* only archive for these states not invalids */
                    SavePointHistory (PointRecord);      /*  archive any state changes */
                    break;

                }
            }
        }

        /*
          set the dst flag here and not above because it makes the quality
          checking easier because of all the bit manipulation
        */
        if(Dst)
            PointRecord->CurrentQuality |= DSTACTIVE;
        else
            PointRecord->CurrentQuality &= ~DSTACTIVE;

        break;

    default:           // this handles all value type data
        if(Inhibited)
        {
            /* point is inhibited  */
            memcpy(AlarmSumRecord.AlarmText, "POINT INHIBITED", 15);
            NewDQCode = TRUE;
            *Value = PointRecord->CurrentValue;     /* value must stay the same */
            if(!(PointRecord->CurrentQuality & OUTOFSCAN))
            {
                /* not in this quality so set */
                PointRecord->CurrentQuality &= ~QUALITYMASK;
                PointRecord->CurrentQuality |= OUTOFSCAN;
                PointRecord->DQCount = 1;
            }
        }
        else if(InvalidCode && !(PointRecord->CurrentQuality & INVALIDDATA))
        {
            /* its invalid so find out what invalid message to use */
            switch(InvalidCode)
            {

            case DATAOVERFLOW:
                memcpy(AlarmSumRecord.AlarmText, "VALUE OVERFLOW", 14);
                break;

            case DATADEVICEFILLER:
                memcpy(AlarmSumRecord.AlarmText, "DEVICE FILLER ", 14);
                break;

            case DATABADAD:
                memcpy(AlarmSumRecord.AlarmText, "BAD DATA A/D  ", 14);
                break;

            case DATAPOWERFAIL:
                memcpy(AlarmSumRecord.AlarmText, "PF INTERVAL   ", 14);
                break;

            case DATAFREEZEFAIL:
                memcpy(AlarmSumRecord.AlarmText, "FREEZE FAILURE", 14);
                break;

            default:
                memcpy(AlarmSumRecord.AlarmText, "INVALID DATA  ", 14);
                break;
            }

            if(PointRecord->PointType != PSEUDOVALUEPOINT)
            {
                if(PointRecord->PlugValue > PointRecord->CurrentValue)
                {
                    /* insert plug value and clear quality status */
                    *Value = PointRecord->PlugValue;
                    PointRecord->CurrentQuality &= (DSTACTIVE | DSTACTIVE2);
                    PointRecord->CurrentQuality |= INVALIDDATA;
                }
                else
                {
                    *Value = PointRecord->CurrentValue;
                    PointRecord->CurrentQuality &= (DSTACTIVE | DSTACTIVE2 | QUALITYSTATUSMASK);
                    PointRecord->CurrentQuality |= INVALIDDATA;
                }
            }

            /* check audible flag */
            if(PointRecord->AudibleMask & ALARMINVALIDSTATE)
                AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
            else
                AlarmSumRecord.AcknowlegeFlag = FALSE;

            /* now see if we wanted an alarm on this guy */
            if(PointRecord->AlarmMask & CRITINVALIDSTATE && AlarmInhibit == FALSE)
            {
                LoggedEvent = TRUE;               // make sure not to log again for state change
                AlarmSumRecord.LogCode = INVALIDDATA;
                PointRecord->AlarmStatus = ALARMEDUNACK;
                Result = LogAlarm (&AlarmSumRecord,
                                   CRITICALCLASS,
                                   LogFlag,
                                   PointRecord,
                                   ALARMDRPPASSINVALIDSTATE);
            }
            else if(PointRecord->AlarmMask & NONCRITINVALIDSTATE && AlarmInhibit == FALSE)
            {
                LoggedEvent = TRUE;               // make sure not to log again for state change
                AlarmSumRecord.LogCode = INVALIDDATA;
                PointRecord->AlarmStatus = ALARMEDUNACK;
                Result = LogAlarm (&AlarmSumRecord,
                                   NONCRITICALCLASS,
                                   LogFlag,
                                   PointRecord,
                                   ALARMDRPPASSINVALIDSTATE);
            }
        }  // close invalid check
        else if(DataQual & PLUGGED)
        {
            if(!(PointRecord->CurrentQuality & PLUGGED))
            {
                /* data is plugged and changed state */
                PointRecord->DQCount = 1;       // reset dq count
                memcpy(AlarmSumRecord.AlarmText, "PLUGGED DATA  ", 14);
                if(PointRecord->PointType == PSEUDOVALUEPOINT)
                {
                    /* dont mess with plug value for pseudo points */
                    PointRecord->CurrentQuality &= (DSTACTIVE | DSTACTIVE2);
                    PointRecord->CurrentQuality |= PLUGGED;
                }
                else
                {
                    /* this will set the proper plug value for this point */
                    setPlugValue (FALSE, Value, PointRecord);
                    PointRecord->CurrentQuality &= (DSTACTIVE | DSTACTIVE2 | QUALITYSTATUSMASK);
                    PointRecord->CurrentQuality |= PLUGGED;
                    NewDQCode = PointRecord->CurrentQuality;
                }

                if(!AlarmInhibit)
                {
                    /* NOT in alarm inhibit mode so we check for alarm */

                    /* check audible flag */
                    if(PointRecord->AudibleMask & ALARMPLUGGED)
                        AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                    else
                        AlarmSumRecord.AcknowlegeFlag = FALSE;

                    /* now see if we wanted an alarm on this guy */
                    if(PointRecord->AlarmMask & CRITPLUGGED)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        AlarmSumRecord.LogCode = PLUGGED;
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        Result = LogAlarm (&AlarmSumRecord,
                                           CRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSPLUGGED);
                    }
                    else if(PointRecord->AlarmMask & NONCRITPLUGGED)
                    {
                        LoggedEvent = TRUE;               // make sure not to log again for state change
                        AlarmSumRecord.LogCode = PLUGGED;
                        PointRecord->AlarmStatus = ALARMEDUNACK;
                        Result = LogAlarm (&AlarmSumRecord,
                                           NONCRITICALCLASS,
                                           LogFlag,
                                           PointRecord,
                                           ALARMDRPPASSPLUGGED);
                    }
                }  // close alarm inhibit check
            }
            else
            {
                if(PointRecord->PointType != PSEUDOVALUEPOINT)
                {
                    /* can't plug pseudo points */
                    setPlugValue (FALSE, Value, PointRecord);
                    PointRecord->CurrentQuality &= (DSTACTIVE | DSTACTIVE2 | QUALITYMASK);
                }

                /* its already plugged so ckeck for none critical alarm
                   if its not alarm inhibited */
                if(!AlarmInhibit && PointRecord->AlarmMask & NONCRITPLUGGED)
                {
                    if(PointRecord->AlarmStatus == ALARMEDUNACK
                       && PointRecord->AudibleMask & ALARMPLUGGED)
                    {
                        /* its unacknowledged and audible is on
                           so makes some more noise */
                        LogMessage.StatusFlag = NONCRITAUDIBLE;

                        /* set message to event log to make some noise */
                        Result = LogEvent(&LogMessage);
                    }
                }
            }
        }  // close plug checking
        else if(InvalidCode == FALSE && DataQual == FALSE)
            /* this means data is valid */
            PointRecord->CurrentQuality &= (DSTACTIVE | DSTACTIVE2 | QUALITYSTATUSMASK); // clear quality to normal
        else if(PointRecord->PointType == PSEUDOVALUEPOINT)
        {
            /* must set pseudo point data quality which is from the source */
            if(!(DataQual & PLUGGED) && !(DataQual & INVALIDDATA))
            {
                /* clear out of scan flag if needed */
                if(DataQual & OUTOFSCAN)
                {
                    memcpy(AlarmSumRecord.AlarmText, "POINT ENABLED  ", 15);
                    DataQual &= ~OUTOFSCAN;
                }
                /* set to the quality send from app */
                PointRecord->CurrentQuality = DataQual;
            }
        }
        else
        {
            /* set value if already pluged or invalid for check and not a pseudo */
            if(DataQual & OUTOFSCAN)
            {
                /* clear inhibit flag if it was set */
                memcpy(AlarmSumRecord.AlarmText, "POINT ENABLED  ", 15);
                DataQual &= ~OUTOFSCAN;
            }

            if((PointRecord->PlugValue == -1) || (PointRecord->PlugValue < PointRecord->CurrentValue))
            {
                *Value = PointRecord->CurrentValue;
            }
            else
            {
                /* insert plug value and clear quality status */
                *Value = PointRecord->PlugValue;
            }
        }

        /* check for other limits even if plugged */
        if(NewDQCode == NORMALDATA && PointRecord->HiLimit < HILIMITMAX)
        {
            if(*Value > PointRecord->HiLimit)
            {
                if((PointRecord->CurrentQuality & QUALITYSTATUSMASK) == HLDATA)
                {
                    NewDQCode = HLDATA;       // already in high limit
                    setPlugValue (PLUGLIMITS, Value, PointRecord);
                    LoggedEvent = TRUE;                       // make sure not to log this change
                }
                else
                {
                    memcpy(AlarmSumRecord.AlarmText, "HIGH LIMIT    ", 14);
                    NewDQCode = HLDATA;
                    PointRecord->CurrentQuality &= ~QUALITYSTATUSMASK;
                    PointRecord->CurrentQuality |= HLDATA;
                    setPlugValue (PLUGLIMITS, Value, PointRecord);

                    if(!AlarmInhibit)
                    {
                        /* NOT in alarm inhibit mode so we check for alarm */

                        /* check audible flag */
                        if(PointRecord->AudibleMask & ALARMHILIMIT)
                            AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                        else
                            AlarmSumRecord.AcknowlegeFlag = FALSE;

                        /* now see if we wanted an alarm on this guy */
                        if(PointRecord->AlarmMask & CRITHILIMIT)
                        {
                            LoggedEvent = TRUE;               // make sure not to log again for state change
                            PointRecord->AlarmStatus = ALARMEDUNACK;
                            AlarmSumRecord.LogCode = HLDATA;
                            Result = LogAlarm (&AlarmSumRecord,
                                               CRITICALCLASS,
                                               LogFlag,
                                               PointRecord,
                                               ALARMDRPPASSHILIMIT);
                        }
                        else if(PointRecord->AlarmMask & NONCRITHILIMIT)
                        {
                            LoggedEvent = TRUE;               // make sure not to log again for state change
                            PointRecord->AlarmStatus = ALARMEDUNACK;
                            AlarmSumRecord.LogCode = HLDATA;
                            Result = LogAlarm (&AlarmSumRecord,
                                               NONCRITICALCLASS,
                                               LogFlag,
                                               PointRecord,
                                               ALARMDRPPASSHILIMIT);
                        }
                    }  // close alarm inhibit check
                }
            } // close high limit check
        }

        /* low limit is next priority */
        if(NewDQCode == NORMALDATA && PointRecord->LowLimit > LOWLIMITMAX)
        {
            if(*Value < PointRecord->LowLimit)
            {
                if((PointRecord->CurrentQuality & QUALITYSTATUSMASK) == LLDATA)
                {
                    NewDQCode = LLDATA;       // already in low limit
                    setPlugValue (PLUGLIMITS, Value, PointRecord);
                    LoggedEvent = TRUE;                       // make sure not to log this change
                }
                else
                {
                    NewDQCode = LLDATA;
                    memcpy(AlarmSumRecord.AlarmText, "LOW LIMIT     ", 14);
                    PointRecord->CurrentQuality &= ~QUALITYSTATUSMASK;
                    PointRecord->CurrentQuality |= LLDATA;
                    setPlugValue (PLUGLIMITS, Value, PointRecord);

                    if(!AlarmInhibit)
                    {
                        /* NOT in alarm inhibit mode so we check for alarm */

                        /* check audible flag */
                        if(PointRecord->AudibleMask & ALARMLOWLIMIT)
                            AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                        else
                            AlarmSumRecord.AcknowlegeFlag = FALSE;

                        /* now see if we wanted an alarm on this guy */
                        if(PointRecord->AlarmMask & CRITLOWLIMIT)
                        {
                            LoggedEvent = TRUE;               // make sure not to log again for state change
                            PointRecord->AlarmStatus = ALARMEDUNACK;
                            AlarmSumRecord.LogCode = LLDATA;
                            Result = LogAlarm (&AlarmSumRecord,
                                               CRITICALCLASS,
                                               LogFlag,
                                               PointRecord,
                                               ALARMDRPPASSLOWLIMIT);
                        }
                        else if(PointRecord->AlarmMask & NONCRITLOWLIMIT)
                        {
                            LoggedEvent = TRUE;               // make sure not to log again for state change
                            PointRecord->AlarmStatus = ALARMEDUNACK;
                            AlarmSumRecord.LogCode = LLDATA;
                            Result = LogAlarm (&AlarmSumRecord,
                                               NONCRITICALCLASS,
                                               LogFlag,
                                               PointRecord,
                                               ALARMDRPPASSLOWLIMIT);
                        }
                    } // close alarm inhibit check
                }
            }  // close low limit check
        }

        /* High Warnings is next priority */
        if(NewDQCode == NORMALDATA && PointRecord->HiWarning < HILIMITMAX)
        {
            if(*Value > PointRecord->HiWarning)
            {
                if((PointRecord->CurrentQuality & QUALITYSTATUSMASK) == HLDATA)
                {
                    NewDQCode = HWDATA;
                    setPlugValue (PLUGWARNINGS, Value, PointRecord);
                    PointRecord->CurrentQuality &= ~HLDATA;     // clear low warning
                    PointRecord->CurrentQuality |= HWDATA;
                    LoggedEvent = TRUE;                       // make sure not to log this change
                }
                else if((PointRecord->CurrentQuality & QUALITYSTATUSMASK) == HWDATA)
                {
                    NewDQCode = HWDATA;
                    setPlugValue (PLUGWARNINGS, Value, PointRecord);
                    LoggedEvent = TRUE;                       // make sure not to log this change
                }
                else
                {
                    NewDQCode = HWDATA;
                    memcpy(AlarmSumRecord.AlarmText, "HIGH WARNING  ", 14);
                    PointRecord->CurrentQuality &= ~QUALITYSTATUSMASK;
                    PointRecord->CurrentQuality |= HWDATA;
                    setPlugValue (PLUGWARNINGS, Value, PointRecord);

                    if(!AlarmInhibit)
                    {
                        /* NOT in alarm inhibit mode so we check for alarm */

                        /* check audible flag */
                        if(PointRecord->AudibleMask & ALARMHIWARNING)
                            AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                        else
                            AlarmSumRecord.AcknowlegeFlag = FALSE;

                        /* now see if we wanted an alarm on this guy */
                        if(PointRecord->AlarmMask & CRITHIWARNING)
                        {
                            LoggedEvent = TRUE;               // make sure not to log again for state change
                            PointRecord->AlarmStatus = ALARMEDUNACK;
                            AlarmSumRecord.LogCode |= HWDATA;
                            Result = LogAlarm (&AlarmSumRecord,
                                               CRITICALCLASS,
                                               LogFlag,
                                               PointRecord,
                                               ALARMDRPPASSHIWARNING);
                        }
                        else if(PointRecord->AlarmMask & NONCRITHIWARNING)
                        {
                            LoggedEvent = TRUE;               // make sure not to log again for state change
                            PointRecord->AlarmStatus = ALARMEDUNACK;
                            AlarmSumRecord.LogCode |= HWDATA;
                            Result = LogAlarm (&AlarmSumRecord,
                                               NONCRITICALCLASS,
                                               LogFlag,
                                               PointRecord,
                                               ALARMDRPPASSHIWARNING);
                        }
                    }
                }
            }
        }

        /* Low Warning is next priority */
        if(NewDQCode == NORMALDATA && PointRecord->LowWarning > LOWLIMITMAX)
        {
            if(*Value < PointRecord->LowWarning)
            {
                /* now check if old DQ is low limit and do not alarm on the way up */
                if((PointRecord->CurrentQuality & QUALITYSTATUSMASK) == LLDATA)
                {
                    NewDQCode = LWDATA;
                    setPlugValue (PLUGWARNINGS, Value, PointRecord);
                    PointRecord->CurrentQuality &= ~LLDATA;     // clear low warning
                    PointRecord->CurrentQuality |= LWDATA;
                    LoggedEvent = TRUE;                       // make sure not to log this change
                }
                else if((PointRecord->CurrentQuality & QUALITYSTATUSMASK) == LWDATA)
                {
                    NewDQCode = LWDATA;
                    setPlugValue (PLUGWARNINGS, Value, PointRecord);
                    LoggedEvent = TRUE;                       // make sure not to log this change
                }
                else
                {
                    NewDQCode = LWDATA;
                    memcpy(AlarmSumRecord.AlarmText, "LOW WARNING   ", 14);
                    PointRecord->CurrentQuality &= ~QUALITYSTATUSMASK;
                    PointRecord->CurrentQuality |= LWDATA;
                    setPlugValue (PLUGWARNINGS, Value, PointRecord);

                    if(!AlarmInhibit)
                    {
                        /* NOT in alarm inhibit mode so we check for alarm */

                        /* check audible flag */
                        if(PointRecord->AudibleMask & ALARMLOWWARNING)
                            AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
                        else
                            AlarmSumRecord.AcknowlegeFlag = FALSE;

                        /* now see if we wanted an alarm on this guy */
                        if(PointRecord->AlarmMask & CRITLOWWARNING)
                        {
                            LoggedEvent = TRUE;               // make sure not to log again for state change
                            PointRecord->AlarmStatus = ALARMEDUNACK;
                            AlarmSumRecord.LogCode |= LWDATA;
                            Result = LogAlarm (&AlarmSumRecord,
                                               CRITICALCLASS,
                                               LogFlag,
                                               PointRecord,
                                               ALARMDRPPASSLOWWARNING);
                        }
                        else if(PointRecord->AlarmMask & NONCRITLOWWARNING)
                        {
                            LoggedEvent = TRUE;               // make sure not to log again for state change
                            PointRecord->AlarmStatus = ALARMEDUNACK;
                            AlarmSumRecord.LogCode |= LWDATA;
                            Result = LogAlarm (&AlarmSumRecord,
                                               NONCRITICALCLASS,
                                               LogFlag,
                                               PointRecord,
                                               ALARMDRPPASSLOWWARNING);
                        }
                    } // close alarm inhibit check
                }
            } // close low warning check
        }

        /* if no limits were found must plug in good data */
        if(NewDQCode == NORMALDATA)
        {
            PointRecord->CurrentValue = *Value;
            PointRecord->CurrentQuality &= ~QUALITYSTATUSMASK;
        }

        /* set dq count */
        if(OldDQCode != PointRecord->CurrentQuality && !(PointRecord->CurrentQuality & OUTOFSCAN))
        {
            /* state change so reset dq count */
            PointRecord->DQCount = 1;
        }
        else if(!(PointRecord->CurrentQuality & OUTOFSCAN))
        {
            /* clear the alarm state to no alarm */
            /* quality is not out-of-scan so and is the same so increment count */
            if(PointRecord->DQCount < DQCOUNTMAX)
                ++PointRecord->DQCount;
        }

        /* state change check for alarm and log event if not logged yet */
        if(OldDQCode != PointRecord->CurrentQuality && LoggedEvent == FALSE && AlarmInhibit == FALSE)
        {
            /* check audible flag */
            if(PointRecord->AudibleMask & ALARMSTATECHANGE)
                AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
            else
                AlarmSumRecord.AcknowlegeFlag = FALSE;

            if((PointRecord->CurrentQuality & ~(DSTACTIVE | DSTACTIVE2)) == NORMALDATA)
                memcpy(AlarmSumRecord.AlarmText, "SC NORMAL DATA", 14);
            else
            {
                memcpy(TempMessage, "SC ", 3);
                memcpy(TempMessage + 3, AlarmSumRecord.AlarmText, 15);
                memcpy(AlarmSumRecord.AlarmText, TempMessage, 18);
            }

            if(PointRecord->AlarmMask & CRITSTATECHANGE)
            {
                AlarmSumRecord.LogCode |= STATECHANGE;
                PointRecord->AlarmStatus = ALARMEDUNACK;
                Result = LogAlarm (&AlarmSumRecord,
                                   CRITICALCLASS,
                                   LogFlag,
                                   PointRecord,
                                   ALARMDRPPASSSTATECHANGE);
            }
            else if(PointRecord->AlarmMask & NONCRITSTATECHANGE)
            {
                AlarmSumRecord.LogCode |= STATECHANGE;
                PointRecord->AlarmStatus = ALARMEDUNACK;
                Result = LogAlarm (&AlarmSumRecord,
                                   NONCRITICALCLASS,
                                   LogFlag,
                                   PointRecord,
                                   ALARMDRPPASSSTATECHANGE);
            }
            else
            {
                /* log a non-alarm state change */
                memcpy(LogMessage.DeviceName, PointRecord->DeviceName, sizeof(LogMessage.DeviceName));
                memcpy(LogMessage.PointName, PointRecord->PointName, sizeof(LogMessage.PointName));
                memcpy(LogMessage.LogMessage1, AlarmSumRecord.AlarmText, sizeof(LogMessage.LogMessage1));
                LogMessage.EventType |= STATECHANGE;
                Result = NormalLogEvent(&LogMessage, LogFlag);
            }
        }

        /*
        set the dst flag here and not above because it makes the quality
        checking easier because of all the bit manipulation
        */
        if(Dst)
            PointRecord->CurrentQuality |= DSTACTIVE;
        else
            PointRecord->CurrentQuality &= ~DSTACTIVE;

        ClearAlarmStatus (PointRecord);             // clear the alarm status
        SavePointHistory (PointRecord);             // check for archive
        LogPointValue (PointRecord);                // check for logging

    }

    /* update into the database */
    Result = PointFastUpdate(PointRecord);

#endif

    return(NORMAL);
}


/* function to check for archiving point data */
IM_EX_CTIBASE INT SavePointHistory (CTIPOINT *PointRecord)
{

    LSFILETYPE ArchiveRecord;
    CHAR FileName[100], TempFileName[33];
    ULONG Result;
    HANDLE hf;
    ULONG Action, BytesRead;
    ULONG fileptr;
    struct timeb TimeBStruct;

    UCTFTime (&TimeBStruct);
    if(PointRecord->ArchiveInterval != -1 && TimeBStruct.time > (LONG)PointRecord->NextArchiveTime)
    {
        /* no faster than every minute */
        ultoa (PointRecord->FileNumber, TempFileName, 10);
        strcpy (FileName, "READHIST\\");
        strcat (FileName, TempFileName);
        strcat (FileName, ".DAT");
        Result = CTIOpen(FileName, &hf, &Action, 0L, FILE_NORMAL, FILE_OPEN | FILE_CREATE,
                         OPEN_ACCESS_WRITEONLY | OPEN_SHARE_DENYNONE, 0L);

        if(!Result)
        {
            /* no errors write data */
            ArchiveRecord.TimeStamp = PointRecord->CurrentTime;
            ArchiveRecord.Value = PointRecord->CurrentValue;
            ArchiveRecord.DataQuality = PointRecord->CurrentQuality;

            if(Action == FILE_EXISTED)
                /* file existed must move to the end of the file */
                CTISetFilePtr (hf, 0L, FILE_END, &fileptr);

            /* write data */
            Result = CTIWrite(hf,                 // file handle
                              &ArchiveRecord,         // address of buffer
                              sizeof(ArchiveRecord),  // size of buffer
                              &BytesRead);          // address for number of bytes read
            CTIClose(hf);
        }
        else
        {
            printf("Yep, I kaked here\n");
        }

        /* now set the next log interval */
        if(PointRecord->PointType == TWOSTATEPOINT ||
           PointRecord->PointType == THREESTATEPOINT ||
           PointRecord->PointType == PSEUDOSTATUSPOINT)
        {
            /* force it to update next state change */
            PointRecord->NextArchiveTime = 0L;
        }
        else
        {
            if(PointRecord->ArchiveInterval < 60L)
            {
                /* never archive more the once a minute */
                PointRecord->NextArchiveTime = setNextInterval (TimeBStruct.time, 60L);
            }
            else
            {
                /* set the next time to log this point */
                setNextTimeInterval (&TimeBStruct,
                                     PointRecord->ArchiveInterval,
                                     &PointRecord->NextArchiveTime);
            }
        }
    }
    else if(PointRecord->ArchiveInterval > 59 && (PointRecord->NextArchiveTime - TimeBStruct.time) > PointRecord->ArchiveInterval)
    {
        /* log interval has changed to so reset time */
        setNextTimeInterval (&TimeBStruct,
                             PointRecord->ArchiveInterval,
                             &PointRecord->NextArchiveTime);
    }

    return(NORMAL);
}

INT SaveLSHistory (CTIPOINT  *PointRecord,
                   ULONG  LSInterval,
                   FLOAT  Value,
                   USHORT Quality,
                   ULONG  TimeStamp)
{

    INT            i = 0;
    ULONG          TempInterval = 0;
    LSFILETYPE     ArchiveRecord;
    CHAR           FileName[100], TempFileName[33];
    ULONG          Result;
    HANDLE          hf;
    ULONG          Action, BytesRead;
    ULONG          fileptr;

    strcpy (FileName, "LSHIST\\");
    ultoa (PointRecord->FileNumber, TempFileName, 10);
    strcat (FileName, TempFileName);
    strcat (FileName, ".");
    ultoa (LSInterval, TempFileName, 10);
    strcat (FileName, TempFileName);






    Result = CTIOpen(FileName, &hf, &Action, 0L, FILE_NORMAL, FILE_OPEN,
                     OPEN_ACCESS_READONLY | OPEN_SHARE_DENYNONE, 0L);
    while(Result)
    {
        if(Result == ERROR_FILE_NOT_FOUND)
        {
            printf("File Does Not EXIST\n");
            break;
        }

        // printf("CTIOpen(%s) Error = %ld\n",FileName,ulrc);
        i++;
        CTISleep(100);
        if(i > 10)
        {
            // printf("Could not open ARCHIVE.  No load profile data will be acquired\n");
            break;
        }
        Result = CTIOpen(FileName, &hf, &Action, 0L, FILE_NORMAL, FILE_OPEN,
                         OPEN_ACCESS_READONLY | OPEN_SHARE_DENYNONE, 0L);
    }

    if(!Result)
    {
        Result = CTISetFilePtr( hf, -(LONG)((sizeof(LSFILETYPE))), FILE_END, &fileptr);

        if(!Result)
        {
            Result = CTIRead(hf, &ArchiveRecord, sizeof(LSFILETYPE), &BytesRead);
            if(!Result && BytesRead == sizeof(LSFILETYPE))
            {
                TempInterval = ArchiveRecord.TimeStamp; // Last Read
            }
        }

        CTIClose(hf);
    }


    Result = CTIOpen(FileName,
                     &hf,
                     &Action,
                     0L,
                     FILE_NORMAL, FILE_OPEN | FILE_CREATE,
                     OPEN_ACCESS_WRITEONLY | OPEN_SHARE_DENYNONE,
                     0L);

    if(!Result)
    {

        if(Action == FILE_EXISTED)
        {
            /* file existed must move to the end of the file */
            CTISetFilePtr (hf, 0L, FILE_END, &fileptr);
        }

        if(TimeStamp > TempInterval)
        {
            /*
             *  Data is Newer than the last record....
             */

            /* no errors write data */
            ArchiveRecord.TimeStamp    = TimeStamp;
            ArchiveRecord.Value        = Value;
            ArchiveRecord.DataQuality  = Quality;

            /* write data */
            Result = CTIWrite(hf,                 // file handle
                              &ArchiveRecord,         // address of buffer
                              sizeof(ArchiveRecord),  // size of buffer
                              &BytesRead);          // address for number of bytes read
        }
        CTIClose(hf);
    }
    else
    {
        fprintf(stderr,"*** ERROR %s : %d, %ld\n",__FILE__, __LINE__, Result);
    }


    return(NORMAL);
}


/* this sub returns a uom label based on the uom number */
void IM_EX_CTIBASE getUomLabel (USHORT UOMCode, PCHAR MyLabelName)
{

    switch(UOMCode)
    {
    case 1:
        strcpy (MyLabelName, "MWH   ");
        break;

    case 2:
        strcpy (MyLabelName, "MW    ");
        break;

    case 3:
        strcpy (MyLabelName, "KWH   ");
        break;

    case 4:
        strcpy (MyLabelName, "KW    ");
        break;

    case 5:
        strcpy (MyLabelName, "KQH   ");
        break;

    case 6:
        strcpy (MyLabelName, "KQ    ");
        break;

    case 7:
        strcpy (MyLabelName, "KVARH ");
        break;

    case 8:
        strcpy (MyLabelName, "KVAR  ");
        break;

    case 9:
        strcpy (MyLabelName, "VH    ");
        break;

    case 10:
        strcpy (MyLabelName, "V2H   ");
        break;

    case 11:
        strcpy (MyLabelName, "VOLTS ");
        break;

    case 12:
        strcpy (MyLabelName, "AMPS  ");
        break;

    case 13:
        strcpy (MyLabelName, "PF    ");
        break;

    case 14:
        strcpy (MyLabelName, "TEMP-F");
        break;

    case 15:
        strcpy (MyLabelName, "TEMP-C");
        break;

    case 16:
        strcpy (MyLabelName, "WTRCFT");
        break;

    case 17:
        strcpy (MyLabelName, "GASCFT");
        break;

    case 18:
        strcpy (MyLabelName, "FEET  ");
        break;

    case 19:
        strcpy (MyLabelName, "GAL/PM");
        break;

    case 20:
        strcpy (MyLabelName, "PERCNT");
        break;

    case 21:
        strcpy (MyLabelName, "PULSES");
        break;

    case 22:
        strcpy (MyLabelName, "LEVEL ");
        break;

    case 23:
        strcpy (MyLabelName, "KVA   ");
        break;

    case 24:
        strcpy (MyLabelName, "PSI   ");
        break;

    case 25:
        strcpy (MyLabelName, "HOURS ");
        break;

    case 26:
        strcpy (MyLabelName, "MVARH ");
        break;

    case 27:
        strcpy (MyLabelName, "MVAR  ");
        break;

    default:
        strcpy (MyLabelName, "UNDEF ");
    }
}


/* function to check for logging point data */
IM_EX_CTIBASE INT LogPointValue (CTIPOINT *PointRecord)
{

    CHAR DqLabel[8];
    CHAR ValueString[100];
    ULONG Result;
    SYSTEMLOGMESS LogMessage;
    struct timeb TimeBStruct;

    UCTFTime (&TimeBStruct);
    if(PointRecord->LogInterval != -1 && TimeBStruct.time > (LONG)PointRecord->NextLogTime)
    {
        /* no faster than every minute */
        LogMessage.TimeStamp = TimeBStruct.time;
        LogMessage.StatusFlag = FALSE;
        if(TimeBStruct.dstflag)
        {
            LogMessage.StatusFlag |= DSTACTIVE;
        }

        // memcpy(LogMessage.DeviceName, PointRecord->DeviceName, sizeof(LogMessage.DeviceName));
        // memcpy(LogMessage.PointName, PointRecord->PointName, sizeof(LogMessage.PointName));
        LogMessage.EventType = FALSE;
        LogMessage.Originator = FALSE;      // don't care about this

        GetDQLabel (PointRecord->CurrentQuality, DqLabel);
        if(PointRecord->CurrentValue > 10)
        {
            Float2CharFormat (PointRecord->CurrentValue, ValueString, 6, 1);
        }
        else
        {
            Float2CharFormat (PointRecord->CurrentValue, ValueString, 5, 2);
        }

        LogMessage.LogMessage1 =  ValueString;

        if(PointRecord->PointType == ANALOGPOINT || PointRecord->PointType == PSEUDOVALUEPOINT)
        {
            getUomLabel (PointRecord->MeasuredUOM, ValueString);
        }
        else
        {
            getUomLabel (PointRecord->CalculatedUOM, ValueString);
        }
        LogMessage.LogMessage1 =  ValueString;

        #if 0
        strcat (LogMessage.LogMessage1, " ");
        memcpy (LogMessage.LogMessage1 + 15, DqLabel, 3);
        LogMessage.LogMessage2[0] = ' ';                        // this causes second line not to be logged
        #endif

        Result = NormalLogEvent (&LogMessage, TRUE);

        /* now set the next log interval */
        if(PointRecord->LogInterval < 60L)
        {
            /* never Log more the once a minute */
            PointRecord->NextLogTime = setNextInterval (TimeBStruct.time, 60L);
        }
        else
        {
            /* set the next time to log this point */
            setNextTimeInterval (&TimeBStruct, PointRecord->LogInterval, &PointRecord->NextLogTime);
        }
    }
    else if(PointRecord->LogInterval > 59 && (PointRecord->NextLogTime - TimeBStruct.time) > PointRecord->LogInterval)
    {
        /* log interval has changed to so reset time */
        setNextTimeInterval (&TimeBStruct, PointRecord->LogInterval, &PointRecord->NextLogTime);
    }

    return(NORMAL);
}


/* function format a string from a float */
IM_EX_CTIBASE INT Float2CharFormat (FLOAT Value, PCHAR MyString, USHORT DidgitPlaces, USHORT DecimalPlaces)
{

    int sDecimal, sSign, sStringSize;
    char *pnumstr, numbuf[50] = "", tmpbuf[50];

    /* use info from string to format */
    pnumstr = fcvt ((double) Value, (int) DecimalPlaces + 1, &sDecimal, &sSign);

    /* start with sign */
    if(sSign)
    {
        strcat (numbuf, "-");
        DidgitPlaces --;
        sSign = 1;          /* also used as an offset */
    }
    else
        sSign = 0;          /* also used as an offset */


    if(sDecimal <= 0)
    {
        /* if decimal is to the left of first digit add leading 0 */
        memset (numbuf + sSign, ' ', (size_t) (DidgitPlaces - 1));        // pad digits part

        if(Value == (float)0 && DecimalPlaces == 0)
            strcat (numbuf, "0");
        else
            strcat (numbuf, "0.");

        memset (tmpbuf, '0', (size_t) abs (sDecimal));
        tmpbuf[abs (sDecimal)] = 0;
        sStringSize = (SHORT) strlen (tmpbuf);
        if(sStringSize >= DecimalPlaces)
            strncat (numbuf, tmpbuf, (size_t) DecimalPlaces);
        else
        {
            strcat (numbuf, tmpbuf);
            DecimalPlaces -= sStringSize;     // ajust the string for so far
            sStringSize = (SHORT) strlen (pnumstr);
            if(sStringSize > DecimalPlaces)
                pnumstr[DecimalPlaces] = '\0';     // terminate for truncation
            strcat (numbuf, pnumstr);
        }
    }
    else
    {
        /* if decimal is to the right of first digit, put in leading
         * digits.  Then add decimal and trailing digits
         */
        if(sDecimal == DidgitPlaces)
            strncat (numbuf, pnumstr, (size_t) sDecimal);
        else if(sDecimal > DidgitPlaces)
        {
            strcat (numbuf, "%");
            strncat (numbuf, pnumstr + 1, (size_t) DidgitPlaces);
        }
        else
        {
            memset (numbuf + sSign, ' ', (size_t) (DidgitPlaces - sDecimal));
            strncat (numbuf, pnumstr, (size_t) sDecimal);
        }

        if(DecimalPlaces > 0)
        {
            strcat (numbuf, ".");
            sStringSize = (SHORT) strlen (pnumstr + sDecimal);
            if(sStringSize > DecimalPlaces)
                strncat (numbuf, pnumstr + sDecimal, (size_t) DecimalPlaces);
            else if(sStringSize == DecimalPlaces)
                strcat (numbuf, pnumstr + sDecimal);
            else
            {
                strcat (numbuf, pnumstr + sDecimal);
                memset (numbuf + sStringSize + DidgitPlaces + 1, ' ', (size_t) (DecimalPlaces - sStringSize));
            }
        }
    }

    strcpy (MyString, numbuf);

    return(NORMAL);
}


/* this returns the dq label */
IM_EX_CTIBASE INT GetDQLabel (USHORT DqFlag, PCHAR DqLabel)
{
    DqLabel[0] = '\0';

    if(DqFlag & DATAINVALID)
        strcat (DqLabel, "I");
    else if(DqFlag & PLUGGED)
        strcat (DqLabel, "P");
    else if(DqFlag & MANUAL)
        strcat (DqLabel, "M");
    else if(DqFlag & OUTOFSCAN)
        strcat (DqLabel, "O");
    else
        strcat (DqLabel, "N");


    /* now check the normal status */
    DqFlag &= QUALITYSTATUSMASK;
    switch(DqFlag)
    {

    case HLDATA:
        strcat (DqLabel, "hl");
        break;

    case LLDATA:
        strcat (DqLabel, "ll");
        break;

    case HWDATA:
        strcat (DqLabel, "hw");
        break;

    case LWDATA:
        strcat (DqLabel, "lw");
        break;

    default:
        strcat (DqLabel, "  ");
    }

    return(NORMAL);
}

/* this plugs the current reading or uses the need value */
IM_EX_CTIBASE INT setPlugValue (USHORT PlugFlag, FLOAT *Value, CTIPOINT *PointRecord)
{

    static int AlternatePlug = {-1};
    PSZ Environment;

    if(AlternatePlug == -1)
    {
        /* check for Alternate plug value method (i.e. City of Marshall) */
        /* Check if we need to start the TCP/IP Interface */
        if(!(CTIScanEnv ("DSM2_ALTERNATE_PLUG",
                         &Environment)) &&
           (!(stricmp ("YES", Environment))))
        {
            AlternatePlug = TRUE;
        }
        else
        {
            /* normal plugging method */
            AlternatePlug = FALSE;
        }
    }

    if(PlugFlag == FALSE || PointRecord->Status & PlugFlag)
    {
        /* this means that the plug limit-warning flag is set
           or if it is false it's just a plain old plug (missed reading) */
        if(PointRecord->PlugValue != -1)
        {
            if(AlternatePlug == TRUE)
            {
                /* use the alternate plug method which always uses the
                   plugged value */
                PointRecord->CurrentValue = PointRecord->PlugValue;
            }
            else if(PointRecord->PlugValue > PointRecord->CurrentValue)
            {
                /* normal plugging method is to check which is bigger value */
                PointRecord->CurrentValue = PointRecord->PlugValue;
            }
        }

        if(PlugFlag == FALSE)
        {
            /* move the current value to the value holder when its a straight
               plug for reason's to long to explain in this little comment */
            *Value = PointRecord->CurrentValue;
        }
    }
    else
    {
        PointRecord->CurrentValue = *Value;
    }

    return(NORMAL);
}


/* function to format a string from a long */
IM_EX_CTIBASE INT Long2CharFormat (LONG Value, PCHAR MyString, USHORT DidgitPlaces)
{

    SHORT sStringSize;
    char numbuf[50] = "";

    MyString[0] = '\0';     // init the first char to a null

    if(Value == -1L)
        strncat(MyString, "*********", DidgitPlaces);
    else
    {
        ltoa(Value, numbuf, 10);
        sStringSize = strlen (numbuf);
        if(sStringSize > DidgitPlaces)
        {
            /* number was to big flag it */
            strcpy (MyString, "%");
            strncat(MyString, numbuf, (DidgitPlaces - 1));
        }
        else if(sStringSize < DidgitPlaces)
        {
            /* number was to small pad with spaces */
            memset (MyString, ' ', (DidgitPlaces - sStringSize));
            strcat(MyString, numbuf);
        }
        else
            /* they are equal so copy */
            strcpy (MyString, numbuf);
    }

    return(NORMAL);
}


/* function to format a string from a long */
IM_EX_CTIBASE INT Short2CharFormat (SHORT Value, PCHAR MyString, USHORT DidgitPlaces)
{

    SHORT sStringSize;
    char numbuf[50] = "";


    MyString[0] = '\0';     // init the last char to a null

    if(Value == -1)
        strncat(MyString, "*********", DidgitPlaces);
    else
    {
        itoa(Value, numbuf, 10);
        sStringSize = strlen (numbuf);
        if(sStringSize > DidgitPlaces)
        {
            /* number was to big flag it */
            strcpy (MyString, "%");
            strncat(MyString, numbuf, (DidgitPlaces - 1));
        }
        else if(sStringSize < DidgitPlaces)
        {
            /* number was to small pad with spaces */
            memset (MyString, ' ', (size_t) (DidgitPlaces - sStringSize));        // pad digits part
            MyString[DidgitPlaces - sStringSize] = '\0';
            strcat(MyString, numbuf);
        }
        else
            /* they are equal so copy */
            strcpy (MyString, numbuf);
    }

    return(NORMAL);
}


/* this function calls getShedTime to get Text into myshedtime */
/* this function copies shed time text to myshedtime
   based on the number value in shedcode */
void IM_EX_CTIBASE getShedTime (USHORT ShedCode, PCHAR MyShedTime)
{

    switch(ShedCode)
    {

    case 1:
        strcpy (MyShedTime, "1s ");
        break;

    case 2:
        strcpy (MyShedTime, "1m ");
        break;

    case 3:
        strcpy (MyShedTime, "5m ");
        break;

    case 4:
        strcpy (MyShedTime, "7«M");
        break;

    case 5:
        strcpy (MyShedTime, "10m");
        break;

    case 6:
        strcpy (MyShedTime, "15m");
        break;

    case 7:
        strcpy (MyShedTime, "20m");
        break;

    case 8:
        strcpy (MyShedTime, "30m");
        break;

    case 9:
        strcpy (MyShedTime, "40m");
        break;

    case 10:
        strcpy (MyShedTime, "45m");
        break;

    case 11:
        strcpy (MyShedTime, "60m");
        break;

    case 12:
        strcpy (MyShedTime, "2h ");
        break;

    case 13:
        strcpy (MyShedTime, "3h ");
        break;

    case 14:
        strcpy (MyShedTime, "4h ");
        break;

    case 15:
        strcpy (MyShedTime, "8h ");
        break;

    default:
        strcpy (MyShedTime, "   ");
    }

}


/* this function returns a command label for off-on and time schedule */
void IM_EX_CTIBASE getCommandName (SHORT CommandCode,
                                   USHORT RestoredFlag,
                                   PCHAR MyCommandName)
{

    if(RestoredFlag)
    {
        /* return the opposite command */
        switch(CommandCode)
        {

        case 1:
        case 3:
        case 5:
        case 11:
            ++CommandCode;      /* next command is opossite */
            break;

        case 2:
        case 4:
        case 12:
            --CommandCode;      /* previous command is opossite */
            break;

        case 7:
            CommandCode = 10;   /* vnorm is step-3 */
            break;

        case 8:
        case 9:
        case 10:
            CommandCode = 7;    /* VNORM command is opossite */
            break;

        case 14:
        case 15:
            CommandCode = 16;   /* terminate is opossite */
            break;

        case -5:
            strcpy (MyCommandName, "DEACTV");
            return;

        }
    }


    switch(CommandCode)
    {

    case 1:
        strcpy (MyCommandName, "SHED  ");
        break;

    case 2:
        strcpy (MyCommandName, "REST  ");
        break;

    case 3:
        strcpy (MyCommandName, "CLOSE ");
        break;

    case 4:
        strcpy (MyCommandName, "OPEN  ");
        break;

    case 5:
        strcpy (MyCommandName, "RAISE ");
        break;

    case 6:
        strcpy (MyCommandName, "LOWER ");
        break;

    case 7:
        strcpy (MyCommandName, "VNORM ");
        break;

    case 8:
        strcpy (MyCommandName, "STEPA ");
        break;

    case 9:
        strcpy (MyCommandName, "STEPB ");
        break;

    case 10:
        strcpy (MyCommandName, "STEPC ");
        break;

    case 11:
        strcpy (MyCommandName, "ONPEAK");
        break;

    case 12:
        strcpy (MyCommandName, "OFF-PK");
        break;

    case 13:
        strcpy (MyCommandName, "TOGGLE");
        break;

    case 14:
        strcpy (MyCommandName, "D-PROP");
        break;

    case 15:
        strcpy (MyCommandName, "I-PROP");
        break;

    case 16:
        strcpy (MyCommandName, "T-PROP");
        break;

    case -5:
        strcpy (MyCommandName, "ACTIV.");
        break;

    default:
        strcpy (MyCommandName, "      ");
    }

}


/* this proc fills in the versacomm word struct with addressing including E-word */
IM_EX_CTIBASE INT VersacomWordFill (VSTRUCT *Vst,
                                    GROUP   *GroupRecord,
                                    USHORT  VCommandType,
                                    USHORT  ControlAmount,
                                    USHORT  Period,
                                    ULONG   DiscreteShedTime,
                                    ULONG   DailyStartTime)
{

    INT i;


#define VSHED              1
#define VCYCLE             2
#define VRESTORE           3
#define VTERMINATE         4
#define VINITIATOR         5
#define VCONFIGCODE        6
#define VPROP              9
#define VCONFIGLIGHTS      10
#define EVCYCLE            12
#define EVSHED             13
#define VCAP_CONTROL       14
#define VCAP_VOLTCONTROL   15

#define VDATAMESS          20
#define VSERVICETOUT       21
#define VSERVICETIN        22
#define VSERVICEOUT        24
#define VSERVICEIN         25



    VERSACONFIG VConfigRecord;

#ifdef OLD_WAY // This must be done prior to the psup call!
    if(GroupRecord->VersaComm.Unique)
    {
        /* command by Serial number */
        Vst->UtilityID = 0;
        Vst->Section = 0;
        Vst->Class = 0;
        Vst->Division = 0;
        Vst->Address = GroupRecord->VersaComm.Unique;
    }
    else
    {
        /* normal group addressing */
        Vst->Address = 0L;
        Vst->UtilityID = GroupRecord->VersaComm.UtilityID;
        Vst->Section = GroupRecord->VersaComm.Section;
        Vst->Class = GroupRecord->VersaComm.Class;
        Vst->Division = GroupRecord->VersaComm.Division;
    }
#endif


    switch(VCommandType)
    {
    case VSHED:
        {
            Vst->CommandType = VCONTROL;           //         ' load control
            // MESS-4 Relay (Command type 4)
            Vst->Load[0].ControlType = TRUE;             // discrete shed
            Vst->Load[0].TimeCode    = ControlAmount; // control time
            for(i = 0; i < 3; i++ )
            {
                if(GroupRecord->VersaComm.Load == i)
                {
                    Vst->Load[0].Relay[i] = TRUE;
                }
                else
                {
                    Vst->Load[0].Relay[i] = FALSE;
                }
            }
            break;
        }
    case VCYCLE:
        {
            Vst->CommandType = VCONTROL;                       // load control
            //' MESS-4 Relay (Command type 4)
            Vst->Load[0].ControlType  = FALSE;          // cycle command
            Vst->Load[0].TimeCode     = ControlAmount;   // control time
            for(i = 0; i < 3; i++)
            {
                if(GroupRecord->VersaComm.Load == i)
                {
                    Vst->Load[0].Relay[i] = TRUE;
                }
                else
                {
                    Vst->Load[0].Relay[i] = FALSE;
                }
            }
            break;
        }
    case EVCYCLE:
        {
            /* E-MESSAGE Cycle command */
            Vst->CommandType = VECONTROL;                       /* load control    */
            Vst->ELoad.ControlType = 0;                         /* cycle command   */
            Vst->ELoad.CycleType = 0;                           /* cycle (not a bump) command */
            Vst->ELoad.Relay = GroupRecord->VersaComm.Load;     /* relay number  */
            Vst->ELoad.Percent = ControlAmount;                 /* cycle rate    */

            if(Period < 1)
                /* minimum period is less the 1 default to 30 min */
                Vst->ELoad.Window = 1800;
            else
                /* period length in min. must be converted to seconds */
                Vst->ELoad.Window = Period * 60;

            if(VConfigLocate (&VConfigRecord, GroupRecord))
                /* no verscom config found so default cycle count */
                Vst->ELoad.Count = 8;
            else
            {
                /* found the config */
                if(GroupRecord->VersaComm.Load > 0 && GroupRecord->VersaComm.Load < 4)
                    Vst->ELoad.Count = VConfigRecord.LoadConfig[GroupRecord->VersaComm.Load - 1].CycleCount;
                else
                    Vst->ELoad.Count = 8;
            }
            Vst->ELoad.DelayUntil = 0L;                  /* no delay before starting for now */
            break;
        }
    case EVSHED:
        {
            /* E-MESSAGE Discrete Shed Command */
            Vst->CommandType = VECONTROL;                       /* load control    */
            Vst->ELoad.ControlType = 1;                         /* discrete command   */
            Vst->ELoad.CycleType = 0;                           /* cycle (not a bump) command */
            Vst->ELoad.Relay = GroupRecord->VersaComm.Load;     /* relay number  */
            Vst->ELoad.ControlTime = DiscreteShedTime;          /* amount of time to shed in seconds */
            Vst->ELoad.RandomTime = 120L;                       /* 2min random start time */
            Vst->ELoad.DelayUntil = 0L;                         /* no delay before starting for now */
            break;
        }
    case VCAP_CONTROL:
        /* Cap Control Command via Data Word */
        Vst->CommandType = VDATA;                       /* Data Word    */
        Vst->VData.DataType = 0;
        Vst->VData.Data[2] = 0;
        Vst->VData.Data[3] = 0;
        Vst->VData.Data[4] = 0;
        Vst->VData.Data[5] = 0;
        Vst->VData.Data[0] = 0x71;
        if(DiscreteShedTime == 1L)
            /* Trip command */
            Vst->VData.Data[1] = 0x40;
        else
            /* Close command */
            Vst->VData.Data[1] = 0x80;
        break;

    case VCAP_VOLTCONTROL:
        {
            /* Cap Control Enable Voltage Command via Data Word */
            Vst->CommandType = VDATA;                       /* Data Word    */
            Vst->VData.DataType = 0;
            Vst->VData.Data[2] = 0;
            Vst->VData.Data[3] = 0;
            Vst->VData.Data[4] = 0;
            Vst->VData.Data[5] = 0;
            Vst->VData.Data[0] = 0x71;
            if(DiscreteShedTime == 1L)
                /* ENABLE command */
                Vst->VData.Data[1] = 0xE0;
            else
                /* DISABLE command */
                Vst->VData.Data[1] = 0xD0;
            break;
        }
    case VRESTORE:
    case VTERMINATE:
        {
            Vst->CommandType = VCONTROL;            // load control
            // MESS-4 Relay (Command type 4)
            if(VCommandType == VRESTORE)
            {
                Vst->Load[0].ControlType = 1;
            }
            else
            {
                Vst->Load[0].ControlType = 0;
            }

            Vst->Load[0].TimeCode     = 0;   // control time
            for(i = 0; i < 3; i++)
            {
                if(GroupRecord->VersaComm.Load == i)
                {
                    Vst->Load[0].Relay[i] = TRUE;
                }
                else
                {
                    Vst->Load[0].Relay[i] = FALSE;
                }
            }
            break;
        }
    case VINITIATOR:
        {
            Vst->CommandType = VCONFIG; //         ' Command initiator
            Vst->Initiator = ControlAmount;
            break;
        }
    case VCONFIGCODE:
        {
            Vst->CommandType = VCONFIG; //         ' Configuration Command
            break;
        }
    case VCONFIGLIGHTS:
        {
            Vst->CommandType = VCONFIG; //         ' Configuration Command
            break;
        }
    case VSERVICEOUT:
        {
            Vst->CommandType = VSERVICE; //        ' Contractual Out-of-Service Command
            Vst->Service = 4;
            break;
        }
    case VSERVICETOUT:
        {
            Vst->CommandType = VSERVICE; //         ' Temporary Out-of-Service Command
            Vst->Service = 1;
            break;
        }
    case VSERVICEIN:
        {
            Vst->CommandType = VSERVICE; //         ' Contractual IN Service Command
            Vst->Service = 8;
            break;
        }
    case VSERVICETIN:
        {
            Vst->CommandType = VSERVICE; //         ' Contractual IN Service Command
            Vst->Service = 2;
            break;
        }
    case VPROP:
        {
            Vst->CommandType = VPROPOGATION; //        ' prop test
            if(ControlAmount == 1)
            {
                Vst->PropDIT = 2;
            }
            else if(ControlAmount == 2)
            {
                //'display
                Vst->PropDIT = 4;
            }
            else
            {
                //'terminate
                Vst->PropDIT = 1;
            }
            break;
        }
    case VCOUNTRESET:
        {
            Vst->CommandType = VCOUNTRESET;         //' Counter reset
            Vst->CountReset = ControlAmount;

            break;
        }

    case VDATAMESS:
        {
            // ' DATA WORD
            Vst->CommandType = VDATA;
            Vst->VData.DataType = 0;
        }
    }

    return(NORMAL);
}


/* this return the control duration for 1 period */
IM_EX_CTIBASE INT VPercentControl (USHORT CycleRate, USHORT PeriodTime)
{
    FLOAT WorkVar;
    ULONG TempTime;

    if(CycleRate > 99)
    {
        /* cycled the whole period */
        return((ULONG)PeriodTime * 60L);
    }

    WorkVar = (FLOAT)CycleRate * .01F;
    TempTime = (ULONG)((FLOAT)((ULONG)PeriodTime * 60L) * WorkVar);
    return(TempTime);

}


/* this clears the alarm status if not in an alarm state */
IM_EX_CTIBASE INT ClearAlarmStatus (CTIPOINT *PointRecord)
{

    USHORT AlarmedStateFlag = FALSE;    /* assume not in alarmed state */

    if(PointRecord->CurrentQuality & DATAINVALID)
    {
        if(PointRecord->AlarmMask & CRITINVALIDSTATE ||
           PointRecord->AlarmMask & NONCRITINVALIDSTATE)
            /* in alarm condition set flag */
            AlarmedStateFlag = TRUE;
    }
    else if(PointRecord->CurrentQuality & PLUGGED)
    {
        if(PointRecord->AlarmMask & CRITPLUGGED ||
           PointRecord->AlarmMask & NONCRITPLUGGED)
            /* in alarm condition set flag */
            AlarmedStateFlag = TRUE;
    }


    /* now check the normal status */
    switch((PointRecord->CurrentQuality & QUALITYSTATUSMASK))
    {

    case HLDATA:
        if(PointRecord->AlarmMask & CRITHILIMIT ||
           PointRecord->AlarmMask & NONCRITHILIMIT)
            /* in alarm condition set flag */
            AlarmedStateFlag = TRUE;

        break;

    case LLDATA:
        if(PointRecord->AlarmMask & CRITLOWLIMIT ||
           PointRecord->AlarmMask & NONCRITLOWLIMIT)
            /* in alarm condition set flag */
            AlarmedStateFlag = TRUE;

        break;

    case HWDATA:
        if(PointRecord->AlarmMask & CRITHIWARNING ||
           PointRecord->AlarmMask & NONCRITHIWARNING)
            /* in alarm condition set flag */
            AlarmedStateFlag = TRUE;

        break;

    case LWDATA:
        if(PointRecord->AlarmMask & CRITLOWWARNING ||
           PointRecord->AlarmMask & NONCRITLOWWARNING)
            /* in alarm condition set flag */
            AlarmedStateFlag = TRUE;

        break;
    }

    /* not in alarm condition or always clear if out of service */
    if(PointRecord->CurrentQuality & OUTOFSCAN || AlarmedStateFlag == FALSE)
    {
        /* not in alarm condition clear flag */
        PointRecord->AlarmStatus = 0;
    }

    return(NORMAL);
}


/* this function set the next time to do something based on an interval */
int setNextTimeInterval (struct timeb *TimeBStruct, ULONG Interval, ULONG *TargetTime)
{
    ULONG MyMidnightwas, Temp;


    if(Interval > 3600L)
    {
        /* log time it greater than an hour set from midnight */
        MyMidnightwas = MidNightWas(TimeBStruct->time, TimeBStruct->dstflag);
        Temp = ((TimeBStruct->time - MyMidnightwas) / Interval) + 1L;
        *TargetTime = MyMidnightwas + (Interval * Temp);
    }
    else
    {
        /* smaller log times */
        *TargetTime = setNextInterval (TimeBStruct->time, Interval);
    }

    return(NORMAL);
}


/* function to wake scanner up and force a scan update */
/* this funtion posts a semaphore that wakes up scanner to force a scan */
INT ForceScanCheck (void)
{

    /* Open semaphore to scanner if its not open */
    if(ScannerSem == (HEV) NULL)
    {
        if(CTIOpenEventSem (SCANNERSEM, &ScannerSem, MUTEX_ALL_ACCESS))
        {
            return(NOTNORMAL);
        }
    }

    if(CTIPostEventSem (ScannerSem))
    {
        CTICloseEventSem (&ScannerSem);
        ScannerSem = (HEV) NULL;
        return(NOTNORMAL);
    }

    return(NORMAL);
}

