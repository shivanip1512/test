#include "yukon.h"
#pragma title ( "Route and Device Performance Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        Perform.c

    Purpose:
        Routines to perform updates and maintainence on routes and devices

    The following procedures are contained in this module:
        InitPergGlobs               PerfPrintLogOff
        PertPrintLogOn              RoutePerfUpdate
        DevicePerfUpdate            RouteFailures
        RoutePerform                Route24Perform
        DeviceFailures              DevicePerform
        Device24Perform             LogCommPercentLow
        LogTooManyFailures          LogNewSuccess


    Initial Date:
        6-92

    Revision History:
        5-93    Split out port and remote statistics        WRO
        5-93    Added Log Flag                              WRO
        9-6-93  Converted to 32 bit                             WRO


   -------------------------------------------------------------------- */
#include <windows.h>       // These next few are required for Win32
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
// #include "btrieve.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "drp.h"
#include "elogger.h"
#include "alarmlog.h"
#include "porter.h"
#include "perform.h"

IM_EX_CTIBASE PERFGLOBS    *PerfGlobs     = {NULL};
IM_EX_CTIBASE USHORT       PrintLogEvent  = {TRUE};

int PointFastUpdate(CTIPOINT *pt)
{
   return 0;
}

int PointgetEqual(CTIPOINT *pt)
{
   return 0;
}

int DeviceFastUpdate(DEVICE *pt)
{
   return 0;
}
int DevicegetEqual(DEVICE *pt)
{
   return 0;
}
int DeviceLock(DEVICE *pt)
{
   return 0;
}
int DeviceUnLock(DEVICE *pt)
{
   return 0;
}


IM_EX_CTIBASE INT InitPerfGlobs ()
{
   PSZ Environment;
   /* Attempt to get the segment */
   if(CTIgetNamedSharedMem ((PPVOID) &PerfGlobs, "PERFGLOB", PAG_READ | PAG_WRITE))
   {
      /* Memory not on system? yet so allocate it */
      if(CTIAllocSharedMem ((PPVOID) &PerfGlobs, "PERFGLOB", sizeof (*PerfGlobs), PAG_COMMIT | PAG_READ | PAG_WRITE))
      {
         /* Attempt to get the segment one last time */
         if(CTIgetNamedSharedMem ((PPVOID) &PerfGlobs, "PERFGLOB", PAG_READ | PAG_WRITE))
         {
            /* no go */
            return(MEMORY);
         }
      }
      else
      {
         if(CTIScanEnv ("MIN_COMM_PERCENT_CALC",
                        &Environment) ||
            !(PerfGlobs->MinCommPercentCalc = atoi (Environment)))
         {
            PerfGlobs->MinCommPercentCalc = 20;
         }

         if(CTIScanEnv ("PORT_MIN_PERCENT",
                        &Environment) ||
            !(PerfGlobs->PortMinPercent =  atoi (Environment)))
         {
            PerfGlobs->PortMinPercent = 95;
         }

         if(CTIScanEnv ("PORT_MAX_FAILS",
                        &Environment) ||
            !(PerfGlobs->PortMaxFails = atol (Environment)))
         {
            PerfGlobs->PortMaxFails = 10;
         }

         if(CTIScanEnv ("REMOTE_MIN_PERCENT",
                        &Environment) ||
            !(PerfGlobs->RemoteMinPercent = atoi (Environment)))
         {
            PerfGlobs->RemoteMinPercent = 95;
         }

         if(CTIScanEnv ("REMOTE_MAX_FAILS",
                        &Environment) ||
            !(PerfGlobs->RemoteMaxFails = atol (Environment)))
         {
            PerfGlobs->RemoteMaxFails = 10;
         }

         if(CTIScanEnv ("DEVICE_MIN_PERCENT",
                        &Environment) ||
            !(PerfGlobs->DeviceMinPercent = atoi (Environment)))
         {
            PerfGlobs->DeviceMinPercent = 95;
         }

         if(CTIScanEnv ("DEVICE_MAX_FAILS",
                        &Environment) ||
            !(PerfGlobs->DeviceMaxFails = atol (Environment)))
         {
            PerfGlobs->DeviceMaxFails = 10;
         }

         if(CTIScanEnv ("ROUTE_MIN_PERCENT",
                        &Environment) ||
            !(PerfGlobs->RouteMinPercent = atoi (Environment)))
         {
            PerfGlobs->RouteMinPercent = 95;
         }

         if(CTIScanEnv ("ROUTE_MAX_FAILS",
                        &Environment) ||
            !(PerfGlobs->RouteMaxFails = atol (Environment)))
         {
            PerfGlobs->RouteMaxFails = 10;
         }
      }
   }
   return(NORMAL);
}


/* Routine to disable printing of log messages generated */
IM_EX_CTIBASE INT PerfPrintLogOff ()
{
   PrintLogEvent = FALSE;
   return(NORMAL);
}


/* Routine to enable printing of log messages generated */
IM_EX_CTIBASE INT PerfPrintLogOn ()
{
   PrintLogEvent = TRUE;
   return(NORMAL);
}


/* Routine called to update the performance counts for Routes */
IM_EX_CTIBASE INT RoutePerfUpdate (ROUTEPERF *RoutePerf)
{
   ROUTE RouteRecord;
   ULONG i;
   struct timeb TimeB;
   extern PERFGLOBS *PerfGlobs;
   USHORT Percent;

   /* get the Route Record */
   memcpy (RouteRecord.RouteName, RoutePerf->RouteName, sizeof (RouteRecord.RouteName));

   if((i = RouteLock (&RouteRecord)) != NORMAL)
   {
      return(i);
   }

   /* Update the communications count */
   RouteRecord.Stats.ErrorLog[NORMAL]++;
   RouteRecord.Stats.Error24Log[NORMAL]++;
   RouteRecord.Stats.Error24Roll[0][NORMAL]++;

   /* get the time */
   UCTFTime (&TimeB);

   /* Unless its a normal update the type count */
   if(RoutePerf->Type)
   {
      RouteRecord.Stats.ErrorLog[RoutePerf->Type]++;
      if(!(RouteRecord.Stats.ErrorDSTFlags & PERCENTLOW))
      {
         if(RoutePerform (&RouteRecord.Stats,
                          &Percent))
         {
            /* Log a percentage message */
            LogCommPercentLow (RouteRecord.RouteName,
                               ROUTEPERCENTLOW,
                               &TimeB,
                               Percent,
                               PerfGlobs->PortMinPercent);

            RouteRecord.Stats.ErrorDSTFlags |= PERCENTLOW;
         }
      }

      RouteRecord.Stats.Error24Log[RoutePerf->Type]++;
      if(!(RouteRecord.Stats.ErrorDSTFlags & PERCENT24LOW))
      {
         if(Route24Perform (&RouteRecord.Stats,
                            &Percent))
         {
            /* Log a percentage message */
            LogCommPercentLow (RouteRecord.RouteName,
                               ROUTEPERCENT24LOW,
                               &TimeB,
                               Percent,
                               PerfGlobs->PortMinPercent);

            RouteRecord.Stats.ErrorDSTFlags |= PERCENT24LOW;
         }
      }

      RouteRecord.Stats.Error24Roll[0][RoutePerf->Type]++;
      RouteRecord.Stats.FailureCount++;
      if(!(RouteRecord.Stats.ErrorDSTFlags & CONSECUTIVEFAILURE))
      {
         if(RouteFailures (&RouteRecord.Stats))
         {
            /* Log it */
            LogTooManyFailures (RouteRecord.RouteName,
                                ROUTECOMMFAILURE,
                                &TimeB,
                                PerfGlobs->RouteMaxFails);

            RouteRecord.Stats.ErrorDSTFlags |= CONSECUTIVEFAILURE;
         }
      }
   }
   else
   {
      if(RouteRecord.Stats.ErrorDSTFlags & PERCENTLOW ||
         !(RouteRecord.Stats.ErrorDSTFlags & FIRSTPROCESSED))
      {
         if(!(RoutePerform (&RouteRecord.Stats,
                            &Percent)))
         {
            /* Log a percentage message */
            LogCommPercentLow (RouteRecord.RouteName,
                               ROUTEPERCENTLOW,
                               &TimeB,
                               Percent,
                               PerfGlobs->PortMinPercent);

            RouteRecord.Stats.ErrorDSTFlags &= ~PERCENTLOW;
         }
      }

      if(RouteRecord.Stats.ErrorDSTFlags & PERCENT24LOW ||
         !(RouteRecord.Stats.ErrorDSTFlags & FIRSTPROCESSED))
      {
         if(!(Route24Perform (&RouteRecord.Stats,
                              &Percent)))
         {
            /* Log a percentage message */
            LogCommPercentLow (RouteRecord.RouteName,
                               ROUTEPERCENT24LOW,
                               &TimeB,
                               Percent,
                               PerfGlobs->PortMinPercent);

            RouteRecord.Stats.ErrorDSTFlags &= ~PERCENT24LOW;
         }
      }

      if(RouteRecord.Stats.ErrorDSTFlags & CONSECUTIVEFAILURE ||
         !(RouteRecord.Stats.ErrorDSTFlags & FIRSTPROCESSED))
      {
         /* Log the success */
         LogNewSuccess (RouteRecord.RouteName,
                        ROUTECOMMSUCCESS,
                        &TimeB);

         RouteRecord.Stats.ErrorDSTFlags &= ~CONSECUTIVEFAILURE;
         RouteRecord.Stats.ErrorDSTFlags |= FIRSTPROCESSED;
      }

      RouteRecord.Stats.FailureCount = 0;
      RouteRecord.Stats.LastGoodTime = TimeB.time;
      if(TimeB.dstflag)
         RouteRecord.Stats.ErrorDSTFlags |= LASTGOODDSTMASK;
      else
         RouteRecord.Stats.ErrorDSTFlags &= ~LASTGOODDSTMASK;
   }

   /* Now update the Route Record */
   if((i = RouteFastUpdate (&RouteRecord)) != NORMAL)
      RouteUnLock (&RouteRecord);

   return(i);
}


/* Routine called to update the performance counts for Devices */
IM_EX_CTIBASE INT DevicePerfUpdate (DEVICEPERF *DevicePerf, ERRSTRUCT *ErrorRecord)
{
   DEVICE DeviceRecord;
   ULONG i;
   struct timeb TimeB;
   extern PERFGLOBS *PerfGlobs;
   USHORT Percent;

   /* get the device Record */
   memcpy (DeviceRecord.DeviceName, DevicePerf->DeviceName, STANDNAMLEN);

   if((i = DeviceLock (&DeviceRecord)) != NORMAL)
   {
      return(i);
   }

   if(DevicePerf->Error)
   {
      /* get the error record */
      ErrorRecord->Error = DevicePerf->Error;
      if((i = getError (ErrorRecord)) != NORMAL)
      {
         DeviceUnLock (&DeviceRecord);
         return(i);
      }
   }
   else
      ErrorRecord->Type = NORMAL;

   /* Update the communications count */
   DeviceRecord.Stats.ErrorLog[NORMAL]++;
   DeviceRecord.Stats.Error24Log[NORMAL]++;
   DeviceRecord.Stats.Error24Roll[0][NORMAL]++;

   /* get the time */
   UCTFTime (&TimeB);

   /* Unless its a normal update the type count */
   if(DevicePerf->Error)
   {
      DeviceRecord.Stats.ErrorLog[ErrorRecord->Type]++;
      if(!(DeviceRecord.Stats.ErrorDSTFlags & PERCENTLOW))
      {
         if(DevicePerform (&DeviceRecord.Stats,
                           &Percent))
         {
            /* Log a percentage message */
            LogCommPercentLow (DeviceRecord.DeviceName,
                               DEVICEPERCENTLOW,
                               &TimeB,
                               Percent,
                               PerfGlobs->PortMinPercent);

            DeviceRecord.Stats.ErrorDSTFlags |= PERCENTLOW;
         }
      }

      DeviceRecord.Stats.Error24Log[ErrorRecord->Type]++;
      if(!(DeviceRecord.Stats.ErrorDSTFlags & PERCENT24LOW))
      {
         if(Device24Perform (&DeviceRecord.Stats,
                             &Percent))
         {
            /* Log and/or alarm a percentage message */
            LogCommPercentLow (DeviceRecord.DeviceName,
                               DEVICEPERCENT24LOW,
                               &TimeB,
                               Percent,
                               PerfGlobs->PortMinPercent);

            DeviceRecord.Stats.ErrorDSTFlags |= PERCENT24LOW;
         }
      }

      DeviceRecord.Stats.Error24Roll[0][ErrorRecord->Type]++;
      DeviceRecord.Stats.FailureCount++;
      if(!(DeviceRecord.Stats.ErrorDSTFlags & CONSECUTIVEFAILURE))
      {
         if(DeviceFailures (&DeviceRecord.Stats))
         {
            /* Log it */
            LogTooManyFailures (DeviceRecord.DeviceName,
                                DEVICECOMMFAILURE,
                                &TimeB,
                                PerfGlobs->DeviceMaxFails);

            DeviceRecord.Stats.ErrorDSTFlags |= CONSECUTIVEFAILURE;
         }
      }
   }
   else
   {
      if(DeviceRecord.Stats.ErrorDSTFlags & PERCENTLOW ||
         !(DeviceRecord.Stats.ErrorDSTFlags & FIRSTPROCESSED))
      {
         if(!(DevicePerform (&DeviceRecord.Stats,
                             &Percent)))
         {
            /* Log a percentage message */
            LogCommPercentLow (DeviceRecord.DeviceName,
                               DEVICEPERCENTLOW,
                               &TimeB,
                               Percent,
                               PerfGlobs->PortMinPercent);

            DeviceRecord.Stats.ErrorDSTFlags &= ~PERCENTLOW;
         }
      }

      if(DeviceRecord.Stats.ErrorDSTFlags & PERCENT24LOW ||
         !(DeviceRecord.Stats.ErrorDSTFlags & FIRSTPROCESSED))
      {
         if(!(Device24Perform (&DeviceRecord.Stats,
                               &Percent)))
         {
            /* Log and/or alarm a percentage message */
            LogCommPercentLow (DeviceRecord.DeviceName,
                               DEVICEPERCENT24LOW,
                               &TimeB,
                               Percent,
                               PerfGlobs->PortMinPercent);

            DeviceRecord.Stats.ErrorDSTFlags &= ~PERCENT24LOW;
         }
      }

      if(DeviceRecord.Stats.ErrorDSTFlags & CONSECUTIVEFAILURE ||
         !(DeviceRecord.Stats.ErrorDSTFlags & FIRSTPROCESSED))
      {
         /* Log the success */
         LogNewSuccess (DeviceRecord.DeviceName,
                        DEVICECOMMSUCCESS,
                        &TimeB);

         DeviceRecord.Stats.ErrorDSTFlags &= ~CONSECUTIVEFAILURE;
         DeviceRecord.Stats.ErrorDSTFlags |= FIRSTPROCESSED;
      }

      DeviceRecord.Stats.FailureCount = 0;
      DeviceRecord.Stats.LastGoodTime = TimeB.time;
      if(TimeB.dstflag)
         DeviceRecord.Stats.ErrorDSTFlags |= LASTGOODDSTMASK;
      else
         DeviceRecord.Stats.ErrorDSTFlags &= ~LASTGOODDSTMASK;
   }

   /* Go ahead and update the database */
   if((i = DeviceFastUpdate (&DeviceRecord)) != NORMAL)
      DeviceUnLock (&DeviceRecord);

   return(i);
}


/* Routine to test if failure has occurred */
IM_EX_CTIBASE INT RouteFailures (STATS *Stats)
{
   if(PerfGlobs == NULL)
   {
      if(InitPerfGlobs ())
      {
         return(FALSE);
      }
   }

   if(Stats->FailureCount > PerfGlobs->RouteMaxFails)
      return(TRUE);
   else
      return(FALSE);

}

/* Routine to test if Route is operating below percentage */
IM_EX_CTIBASE INT RoutePerform (STATS *Stats, PUSHORT Percent)
{
   ULONG DLCAttempts;

   if(PerfGlobs == NULL)
   {
      if(InitPerfGlobs ())
      {
         return(FALSE);
      }
   }

   DLCAttempts = Stats->ErrorLog[NORMAL] - (Stats->ErrorLog[ERRTYPEPHONE] +
                                            Stats->ErrorLog[ERRTYPESYSTEM] +
                                            Stats->ErrorLog[ERRTYPEMISC]);

   if(DLCAttempts >= PerfGlobs->MinCommPercentCalc)
   {
      if((*Percent = (USHORT)(100 - (100L * Stats->ErrorLog[ERRTYPEPROTOCOL]) / DLCAttempts)) <
         PerfGlobs->RouteMinPercent)
         return(TRUE);
   }

   return(FALSE);
}


/* Routine to test if Route is operating below percentage for this 24 hour period */
IM_EX_CTIBASE INT Route24Perform (STATS *Stats, PUSHORT Percent)
{
   ULONG DLCAttempts;

   if(PerfGlobs == NULL)
   {
      if(InitPerfGlobs ())
      {
         return(FALSE);
      }
   }

   DLCAttempts = Stats->Error24Log[NORMAL] - (Stats->Error24Log[ERRTYPEPHONE] +
                                              Stats->Error24Log[ERRTYPESYSTEM] +
                                              Stats->Error24Log[ERRTYPEMISC]);

   if(DLCAttempts >= PerfGlobs->MinCommPercentCalc)
   {
      if((*Percent = (USHORT)(100 - (100L * Stats->Error24Log[ERRTYPEPROTOCOL]) / DLCAttempts)) <
         PerfGlobs->RouteMinPercent)
         return(TRUE);
   }


   return(FALSE);
}


/* Routine to test if device failure has occurred */
IM_EX_CTIBASE INT DeviceFailures (STATS *Stats)
{
   if(PerfGlobs == NULL)
   {
      if(InitPerfGlobs ())
      {
         return(FALSE);
      }
   }

   if(Stats->FailureCount > PerfGlobs->DeviceMaxFails)
      return(TRUE);
   else
      return(FALSE);
}


/* Routine to test if Device is operating below percentage */
IM_EX_CTIBASE INT DevicePerform (STATS *Stats, PUSHORT Percent)
{
   ULONG DLCAttempts;

   if(PerfGlobs == NULL)
   {
      if(InitPerfGlobs ())
      {
         return(FALSE);
      }
   }

   DLCAttempts = Stats->ErrorLog[NORMAL] - (Stats->ErrorLog[ERRTYPEPHONE] +
                                            Stats->ErrorLog[ERRTYPESYSTEM] +
                                            Stats->ErrorLog[ERRTYPEMISC]);

   if(DLCAttempts >= PerfGlobs->MinCommPercentCalc)
   {
      if((*Percent = (USHORT)(100 - (100L * Stats->ErrorLog[ERRTYPEPROTOCOL]) / DLCAttempts)) <
         PerfGlobs->DeviceMinPercent)
         return(TRUE);
   }

   return(FALSE);
}

/* Routine to test if Device is operating below percentage for this 24 hour period */
IM_EX_CTIBASE INT Device24Perform (STATS *Stats, PUSHORT Percent)
{
   ULONG DLCAttempts;

   if(PerfGlobs == NULL)
   {
      if(InitPerfGlobs ())
      {
         return(FALSE);
      }
   }

   DLCAttempts = Stats->Error24Log[NORMAL] - (Stats->Error24Log[ERRTYPEPHONE] + Stats->Error24Log[ERRTYPESYSTEM] + Stats->Error24Log[ERRTYPEMISC]);

   if(DLCAttempts >= PerfGlobs->MinCommPercentCalc)
   {
      if((*Percent = (USHORT)(100 - (100L * Stats->Error24Log[ERRTYPEPROTOCOL]) / DLCAttempts)) <
         PerfGlobs->DeviceMinPercent)
         return(TRUE);
   }


   return(FALSE);
}


IM_EX_CTIBASE INT LogCommPercentLow (PCHAR Name, USHORT LogCode, struct timeb *TimeB, USHORT Actual, USHORT Limit)
{
   ALARM_SUM_STRUCT AlarmSumRecord;
   SYSTEMLOGMESS LogMessage;
   DEVICE DeviceRecord;
   CTIPOINT PointRecord;
   DRPVALUE DRPValue;
   extern USHORT PrintLogEvent;


   /* Check if we need to generate an alarm */
   switch(LogCode)
   {
   case DEVICEPERCENTLOW:
   case REMOTEPERCENTLOW:
      memcpy (PointRecord.DeviceName, Name, STANDNAMLEN);
      memcpy (PointRecord.PointName, "PERFORMANCE         ", STANDNAMLEN);
      break;

   case DEVICEPERCENT24LOW:
   case REMOTEPERCENT24LOW:
      memcpy (PointRecord.DeviceName, Name, STANDNAMLEN);
      memcpy (PointRecord.PointName, "PERFORMANCE 24HR    ", STANDNAMLEN);
      break;

   case PORTPERCENTLOW:
   case ROUTEPERCENTLOW:
      memcpy (PointRecord.DeviceName, "$_", 2);
      memcpy (PointRecord.DeviceName + 2, Name, STANDNAMLEN - 2);
      memcpy (PointRecord.PointName, "PERFORMANCE         ", STANDNAMLEN);
      break;

   case PORTPERCENT24LOW:
   case ROUTEPERCENT24LOW:
      memcpy (PointRecord.DeviceName, "$_", 2);
      memcpy (PointRecord.DeviceName + 2, Name, STANDNAMLEN - 2);
      memcpy (PointRecord.PointName, "PERFORMANCE 24HR    ", STANDNAMLEN);
      break;
   }

   if(!(PointgetEqual (&PointRecord)))
   {
      /* We have a point record so get the device record */
      memcpy (DeviceRecord.DeviceName, PointRecord.DeviceName, STANDNAMLEN);

      if(!(DevicegetEqual (&DeviceRecord)))
      {
         /* We have device and point records */
         PointRecord.PreviousTime = PointRecord.CurrentTime;
         PointRecord.CurrentTime = TimeB->time;
         PointRecord.PreviousQuality = PointRecord.CurrentQuality;
         PointRecord.CurrentQuality = NORMAL;
         if(TimeB->dstflag)
            PointRecord.CurrentQuality |= DSTACTIVE;
         else
            PointRecord.CurrentQuality &= ~DSTACTIVE;

         PointRecord.PreviousValue = PointRecord.CurrentValue;
         PointRecord.DQCount = 0;
         if(Actual < Limit)
            PointRecord.CurrentValue = (FLOAT) CLOSED;
         else
            PointRecord.CurrentValue = (FLOAT) OPENED;

         /* Now send this to DRP */
         memcpy (DRPValue.DeviceName, PointRecord.DeviceName, STANDNAMLEN);
         memcpy (DRPValue.PointName, PointRecord.PointName, STANDNAMLEN);
         DRPValue.Quality = PointRecord.CurrentQuality;
         DRPValue.AlarmState = PointRecord.AlarmStatus;
         DRPValue.Value = PointRecord.CurrentValue;
         DRPValue.TimeStamp = PointRecord.CurrentTime;
         DRPValue.Type = DRPTYPEVALUE;

         SendDRPPoint (&DRPValue);

         /* Check on alarm */
         if(Actual >= Limit)
            return(NORMAL);

         if(!(DeviceRecord.Status & ALARMINHIBIT || PointRecord.Status & ALARMINHIBIT))
         {
            /* check if we do this at all */
            if(PointRecord.AlarmMask & (CRITCLOSED | NONCRITCLOSED))
            {
               /* Check audible flag */
               if(PointRecord.AudibleMask & ALARMLOWLIMIT)
                  AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
               else
                  AlarmSumRecord.AcknowlegeFlag = FALSE;

               AlarmSumRecord.LogCode = LogCode;

               AlarmSumRecord.TimeStamp = TimeB->time;
               if(TimeB->dstflag)
                  AlarmSumRecord.StatusFlag = DSTACTIVE;
               else
                  AlarmSumRecord.StatusFlag = 0;

               sprintf (AlarmSumRecord.AlarmText, "%5hd%% < %5hd%%", Actual, Limit);

               memset (AlarmSumRecord.AlarmText + 15, ' ', 3);

               /* Check critical alarm mask */
               if(PointRecord.AlarmMask & CRITCLOSED)
                  LogAlarm (&AlarmSumRecord,
                            CRITICALCLASS,
                            PrintLogEvent,
                            &PointRecord,
                            ALARMDRPPASSCLOSED);
               /* Check non critical mask */
               else if(PointRecord.AlarmMask & NONCRITCLOSED)
                  LogAlarm (&AlarmSumRecord,
                            NONCRITICALCLASS,
                            PrintLogEvent,
                            &PointRecord,
                            ALARMDRPPASSCLOSED);

               PointFastUpdate (&PointRecord);

               return(NORMAL);
            }
            else
               PointFastUpdate (&PointRecord);
         }
         else
            PointFastUpdate (&PointRecord);
      }
   }

   /* We are not going to generate an alarm so at least log it */
   if(Actual >= Limit)
      return(NORMAL);

   /* Build this up line by line */
   LogMessage.TimeStamp = TimeB->time;

   if(TimeB->dstflag)
      LogMessage.StatusFlag = DSTACTIVE;
   else
      LogMessage.StatusFlag = NORMAL;

   if(PrintLogEvent)
      LogMessage.StatusFlag |= EVENTLOG;

   memcpy (LogMessage.DeviceName, PointRecord.DeviceName, STANDNAMLEN);

   memcpy (LogMessage.PointName, "PERFORMANCE         ", STANDNAMLEN);

   sprintf (LogMessage.LogMessage1, "%5hd%% < %5hd%%", Actual, Limit);

   memset (LogMessage.LogMessage1 + 15, ' ', 3);

   memset (LogMessage.LogMessage2, ' ', sizeof (LogMessage.LogMessage2));

   LogMessage.EventType = COMMPERCENTEVENT;

   switch(LogCode)
   {
   case DEVICEPERCENTLOW:
      memcpy (LogMessage.EventLable, DEVICEPERCENTLOW_LABEL, 3);        // event label
      break;

   case REMOTEPERCENTLOW:
      memcpy (LogMessage.EventLable, REMOTEPERCENTLOW_LABEL, 3);        // event label
      break;

   case PORTPERCENTLOW:
      memcpy (LogMessage.EventLable, PORTPERCENTLOW_LABEL, 3);        // event label
      break;

   case ROUTEPERCENTLOW:
      memcpy (LogMessage.EventLable, ROUTEPERCENTLOW_LABEL, 3);        // event label
      break;

   case DEVICEPERCENT24LOW:
      memcpy (LogMessage.EventLable, DEVICEPERCENT24LOW_LABEL, 3);        // event label
      break;

   case REMOTEPERCENT24LOW:
      memcpy (LogMessage.EventLable, REMOTEPERCENT24LOW_LABEL, 3);        // event label
      break;

   case PORTPERCENT24LOW:
      memcpy (LogMessage.EventLable, PORTPERCENT24LOW_LABEL, 3);        // event label
      break;

   case ROUTEPERCENT24LOW:
      memcpy (LogMessage.EventLable, ROUTEPERCENT24LOW_LABEL, 3);        // event label
      break;
   }

   LogMessage.EventLable[3] = ' ';

   LogMessage.Originator = COMMSTATS;

   /* Thats it so send it to elogger */
   LogEvent (&LogMessage);

   return(NORMAL);
}


IM_EX_CTIBASE INT LogTooManyFailures (PCHAR Name,
                                   USHORT LogCode,
                                   struct timeb *TimeB,
                                   ULONG Limit)

{
   ALARM_SUM_STRUCT AlarmSumRecord;
   SYSTEMLOGMESS LogMessage;
   DEVICE DeviceRecord;
   CTIPOINT PointRecord;
   DRPVALUE DRPValue;
   extern USHORT PrintLogEvent;

   /* Check if we need to generate an alarm */
   switch(LogCode)
   {
   case DEVICECOMMFAILURE:
   case REMOTECOMMFAILURE:
      memcpy (PointRecord.DeviceName, Name, STANDNAMLEN);
      memcpy (PointRecord.PointName, "COMM STATUS         ", STANDNAMLEN);
      break;

   case PORTCOMMFAILURE:
   case ROUTECOMMFAILURE:
      memcpy (PointRecord.DeviceName, "$_", 2);
      memcpy (PointRecord.DeviceName + 2, Name, STANDNAMLEN - 2);
      memcpy (PointRecord.PointName, "COMM STATUS         ", STANDNAMLEN);
      break;
   }

   if(!(PointgetEqual (&PointRecord)))
   {
      /* We have a point record so get the device record */
      memcpy (DeviceRecord.DeviceName, PointRecord.DeviceName, STANDNAMLEN);

      if(!(DevicegetEqual (&DeviceRecord)))
      {
         /* With the change in state we need to update the point */
         PointRecord.PreviousTime = PointRecord.CurrentTime;
         PointRecord.CurrentTime = TimeB->time;
         PointRecord.PreviousQuality = PointRecord.CurrentQuality;
         PointRecord.CurrentQuality = NORMAL;
         if(TimeB->dstflag)
            PointRecord.CurrentQuality |= DSTACTIVE;
         else
            PointRecord.CurrentQuality &= ~DSTACTIVE;

         PointRecord.DQCount = 0;
         PointRecord.PreviousValue = PointRecord.CurrentValue;
         PointRecord.CurrentValue = (FLOAT) CLOSED;

         /* Now send this to DRP */
         memcpy (DRPValue.DeviceName, PointRecord.DeviceName, STANDNAMLEN);
         memcpy (DRPValue.PointName, PointRecord.PointName, STANDNAMLEN);
         DRPValue.Quality = PointRecord.CurrentQuality;
         DRPValue.AlarmState = PointRecord.AlarmStatus;
         DRPValue.Value = PointRecord.CurrentValue;
         DRPValue.TimeStamp = PointRecord.CurrentTime;
         DRPValue.Type = DRPTYPEVALUE;

         SendDRPPoint (&DRPValue);

         /* Check if we genrate an alarm */
         if(!(DeviceRecord.Status & ALARMINHIBIT || PointRecord.Status & ALARMINHIBIT))
         {
            /* check if we do this at all */
            if(PointRecord.AlarmMask & (CRITCLOSED | NONCRITCLOSED))
            {
               /* Check audible flag */
               if(PointRecord.AudibleMask & ALARMCLOSED)
                  AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
               else
                  AlarmSumRecord.AcknowlegeFlag = FALSE;

               AlarmSumRecord.LogCode = LogCode;

               AlarmSumRecord.TimeStamp = TimeB->time;
               if(TimeB->dstflag)
                  AlarmSumRecord.StatusFlag = DSTACTIVE;
               else
                  AlarmSumRecord.StatusFlag = 0;

               sprintf (AlarmSumRecord.AlarmText, "EXCEED %5ld", Limit);

               memset (AlarmSumRecord.AlarmText + 12, ' ', 6);

               /* Check critical alarm mask */
               if(PointRecord.AlarmMask & CRITCLOSED)
                  LogAlarm (&AlarmSumRecord,
                            CRITICALCLASS,
                            PrintLogEvent,
                            &PointRecord,
                            ALARMDRPPASSCLOSED);

               /* Check non critical mask */
               else if(PointRecord.AlarmMask & NONCRITCLOSED)
                  LogAlarm (&AlarmSumRecord,
                            NONCRITICALCLASS,
                            PrintLogEvent,
                            &PointRecord,
                            ALARMDRPPASSCLOSED);

               PointRecord.AlarmStatus = ALARMEDUNACK;

               PointFastUpdate (&PointRecord);

               return(NORMAL);
            }
            else
               PointFastUpdate (&PointRecord);
         }
         /* If we reach this point do an update on the point */
         else
            PointFastUpdate (&PointRecord);
      }
   }

   /* We are not going to generate an alarm so at least log it */


   /* Build this up line by line */
   LogMessage.TimeStamp = TimeB->time;

   if(TimeB->dstflag)
      LogMessage.StatusFlag = DSTACTIVE;
   else
      LogMessage.StatusFlag = NORMAL;

   if(PrintLogEvent)
      LogMessage.StatusFlag |= EVENTLOG;

   memcpy (LogMessage.DeviceName, PointRecord.DeviceName, STANDNAMLEN);

   memcpy (LogMessage.PointName, "COMM STATUS         ", STANDNAMLEN);

   sprintf (LogMessage.LogMessage1, "EXCEED %5ld", Limit);

   memset (LogMessage.LogMessage1 + 12, ' ', 6);

   memset (LogMessage.LogMessage2, ' ', sizeof (LogMessage.LogMessage2));

   LogMessage.EventType = COMMFAILUREEVENT;


   switch(LogCode)
   {
   case DEVICECOMMFAILURE:
      memcpy (LogMessage.EventLable, DEVICECOMMFAILURE_LABEL, 3);        // event label
      break;

   case REMOTECOMMFAILURE:
      memcpy (LogMessage.EventLable, REMOTECOMMFAILURE_LABEL, 3);        // event label
      break;

   case PORTCOMMFAILURE:
      memcpy (LogMessage.EventLable, PORTCOMMFAILURE_LABEL, 3);        // event label
      break;

   case ROUTECOMMFAILURE:
      memcpy (LogMessage.EventLable, ROUTECOMMFAILURE_LABEL, 3);        // event label
      break;
   }

   LogMessage.EventLable[3] = ' ';

   LogMessage.Originator = COMMSTATS;

   /* Thats it so send it to elogger */
   return(LogEvent (&LogMessage));
}


IM_EX_CTIBASE INT LogNewSuccess (PCHAR Name,
                              USHORT LogCode,
                              struct timeb *TimeB)

{
   ALARM_SUM_STRUCT AlarmSumRecord;
   SYSTEMLOGMESS LogMessage;
   DEVICE DeviceRecord;
   CTIPOINT PointRecord;
   DRPVALUE DRPValue;
   extern USHORT PrintLogEvent;

   /* Check if we need to generate an alarm */
   switch(LogCode)
   {
   case DEVICECOMMSUCCESS:
   case REMOTECOMMSUCCESS:
      memcpy (PointRecord.DeviceName, Name, STANDNAMLEN);
      memcpy (PointRecord.PointName, "COMM STATUS         ", STANDNAMLEN);
      break;

   case PORTCOMMSUCCESS:
   case ROUTECOMMSUCCESS:
      memcpy (PointRecord.DeviceName, "$_", 2);
      memcpy (PointRecord.DeviceName + 2, Name, STANDNAMLEN - 2);
      memcpy (PointRecord.PointName, "COMM STATUS         ", STANDNAMLEN);
      break;
   }

   if(!(PointgetEqual (&PointRecord)))
   {
      /* We have a point record so get the device record */
      memcpy (DeviceRecord.DeviceName, PointRecord.DeviceName, STANDNAMLEN);

      if(!(DevicegetEqual (&DeviceRecord)))
      {
         /* With the change in state we need to update the point */
         PointRecord.PreviousTime = PointRecord.CurrentTime;
         PointRecord.CurrentTime = TimeB->time;
         PointRecord.PreviousQuality = PointRecord.CurrentQuality;
         PointRecord.CurrentQuality = NORMAL;
         if(TimeB->dstflag)
            PointRecord.CurrentQuality |= DSTACTIVE;
         else
            PointRecord.CurrentQuality &= ~DSTACTIVE;

         PointRecord.DQCount = 0;
         PointRecord.PreviousValue = PointRecord.CurrentValue;
         PointRecord.CurrentValue = (FLOAT) OPENED;

         /* Now send this to DRP */
         memcpy (DRPValue.DeviceName, PointRecord.DeviceName, STANDNAMLEN);
         memcpy (DRPValue.PointName, PointRecord.PointName, STANDNAMLEN);
         DRPValue.Quality = PointRecord.CurrentQuality;
         DRPValue.AlarmState = PointRecord.AlarmStatus;
         DRPValue.Value = PointRecord.CurrentValue;
         DRPValue.TimeStamp = PointRecord.CurrentTime;
         DRPValue.Type = DRPTYPEVALUE;

         SendDRPPoint (&DRPValue);

         /* Check if we genrate an alarm */
         if(!(DeviceRecord.Status & ALARMINHIBIT || PointRecord.Status & ALARMINHIBIT))
         {
            /* check if we do this at all */
            if(PointRecord.AlarmMask & (CRITOPENED | NONCRITOPENED))
            {
               /* Check audible flag */
               if(PointRecord.AudibleMask & ALARMOPENED)
                  AlarmSumRecord.AcknowlegeFlag = AUDIBLEALARM;
               else
                  AlarmSumRecord.AcknowlegeFlag = FALSE;

               AlarmSumRecord.LogCode = LogCode;

               AlarmSumRecord.TimeStamp = TimeB->time;
               if(TimeB->dstflag)
                  AlarmSumRecord.StatusFlag = DSTACTIVE;
               else
                  AlarmSumRecord.StatusFlag = 0;

               memcpy (AlarmSumRecord.AlarmText, "SUCCESSFUL        ", sizeof (AlarmSumRecord.AlarmText));

               /* Check critical alarm mask */
               if(PointRecord.AlarmMask & CRITOPENED)
                  LogAlarm (&AlarmSumRecord,
                            CRITICALCLASS,
                            PrintLogEvent,
                            &PointRecord,
                            ALARMDRPPASSOPENED);

               /* Check non critical mask */
               else if(PointRecord.AlarmMask & NONCRITOPENED)
                  LogAlarm (&AlarmSumRecord,
                            NONCRITICALCLASS,
                            PrintLogEvent,
                            &PointRecord,
                            ALARMDRPPASSOPENED);

               PointRecord.AlarmStatus = ALARMEDUNACK;

               PointFastUpdate (&PointRecord);

               return(NORMAL);
            }
            else
               PointFastUpdate (&PointRecord);
         }
         else
            /* If we reach this point do an update on the point */
            PointFastUpdate (&PointRecord);
      }
   }

   /* We are not going to generate an alarm so at least log it */

   /* Build this up line by line */
   LogMessage.TimeStamp = TimeB->time;

   if(TimeB->dstflag)
      LogMessage.StatusFlag = DSTACTIVE;
   else
      LogMessage.StatusFlag = NORMAL;

   if(PrintLogEvent)
      LogMessage.StatusFlag |= EVENTLOG;

   memcpy (LogMessage.DeviceName, PointRecord.DeviceName, STANDNAMLEN);

   memcpy (LogMessage.PointName, "COMM STATUS         ", STANDNAMLEN);

   memcpy (LogMessage.LogMessage1, "SUCCESSFUL        ", sizeof (LogMessage.LogMessage1));

   memset (LogMessage.LogMessage2, ' ', sizeof (LogMessage.LogMessage2));

   LogMessage.EventType = COMMRESTOREEVENT;

   switch(LogCode)
   {
   case DEVICECOMMSUCCESS:
      memcpy (LogMessage.EventLable, DEVICECOMMSUCCESS_LABEL, 3);        // event label
      break;

   case REMOTECOMMSUCCESS:
      memcpy (LogMessage.EventLable, REMOTECOMMSUCCESS_LABEL, 3);        // event label
      break;

   case PORTCOMMSUCCESS:
      memcpy (LogMessage.EventLable, PORTCOMMSUCCESS_LABEL, 3);        // event label
      break;

   case ROUTECOMMSUCCESS:
      memcpy (LogMessage.EventLable, ROUTECOMMSUCCESS_LABEL, 3);        // event label
      break;
   }

   LogMessage.EventLable[3] = ' ';

   LogMessage.Originator = COMMSTATS;

   /* Thats it so send it to elogger */
   return(LogEvent (&LogMessage));
}
