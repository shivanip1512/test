#pragma warning (disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   PORTPERF
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTPERF.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma title ( "Periodic Statistic Update Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PORTPERF.C

    Purpose:
        Perform midnight and roll statistic updates

    The following procedures are contained in this module:
        PerfThread                      PerfUpdateThread

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-7-93   Converted to 32 bit                                WRO
        11-1-93  Modified to keep statistics temporarily in memory  TRH
        2-28-94  Added enviroment variables for update frequency    WRO
        6-11-96  Fixed Remote previous day statistics               WRO

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
//// #include "btrieve.h"
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <sys\timeb.h>
#include <malloc.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "ilex.h"
#include "elogger.h"

#include "portglob.h"
#include "c_port_interface.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "port_base.h"

#include "logger.h"
#include "guard.h"

/* Thread to copy statistics hourly and clear at midnight */
VOID PerfThread (VOID *Arg)
{

   struct timeb TimeB;
   struct timeb SysTimeB;
   struct tm *TStruct;
   CtiPort    *PortRecord;
   CtiDeviceBase  *RemoteRecord;
   CtiRoute   *RouteRecord;
   DEVICE DeviceRecord;
   ULONG i, j;
   USHORT Port, Remote;
   OUTMESS *OutMessage;
   ULONG CheckInterval = 5;
   PSZ Environment;
   ULONG ChangeToDSTDelay = 0;

   /* set the priority of this guy down a bit */
   CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, 0, 0);

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " PerfThread: TID:  " << CurrentTID() << endl;
   }

   /* Check for the check frequency environment variable */
   if(!(CTIScanEnv ("DSM2_REMOTECHECKRATE", &Environment)))
   {
      if((i = atoi (Environment)) != 0)
      {
         CheckInterval = i;
      }
   }

   /* Check if we need to do the DST Switch thing */
   if(AutoDSTChange)
   {
      /* get the time we think it is */
      UCTFTime (&TimeB);

      /* get the time that the system thinks it is */
      ftime (&SysTimeB);

      /* Check it out, see if sys is zero and we are not or opposite  */
      if((!(SysTimeB.dstflag) && TimeB.dstflag))
      {
         /* going off DST to standard time */

         TimeB.time -= 3600;
         TimeB.dstflag = 0;
         UCTSetFTime (&TimeB);
         SendTextToLogger ("Inf", "Adjustment made to: End Daylight Saving Time");
         UCTFTime (&TimeB);
         PutDSTTime (TimeB.time);
         ChangeToDSTDelay = TimeB.time + 3660;    /* Don't allow Change To Dst for at least an hour */
      }
      else if((SysTimeB.dstflag && !(TimeB.dstflag)))
      {
         /* do some checking to see if we just changed over by looking
            ahead 2 hours and see it things it DST */
         SysTimeB.time += 7200;
         TStruct = localtime (&SysTimeB.time);
         if(TStruct->tm_isdst)
         {
            /* it say it is so must be DST */
            SysTimeB.time -= 7200;
            UCTSetFTime (&SysTimeB);
            SendTextToLogger ("Inf", "Adjustment made to: Start Daylight Saving Time");
            UCTFTime (&TimeB);
            PutDSTTime (TimeB.time);
         }
         else
         {
            ChangeToDSTDelay = TimeB.time + 3660;    /* Don't allow Change To Dst for at least an hour */
         }
      }
   }

#ifdef OLD_WAY                                              // FIX FIX FIX CGP 7/19/99 Add this back in later
   /* do this as long as we are running */
   for(;;)
   {
      /* Update on an hour by hour basis */
      UCTFTime (&TimeB);

      CTISleep (1000L * (60L - (TimeB.time % 60L)));

      UCTFTime (&TimeB);

      /* Check if we need to do the DST Switch thing */
      if(AutoDSTChange)
      {
         /* get the time that the system thinks it is */
         ftime (&SysTimeB);

         /* Check it out, see if sys is zero and we are not or opposite  */
         if((!(SysTimeB.dstflag) && TimeB.dstflag))
         {
            /* going off DST to standard time */
            SysTimeB.time -= 3600;
            SysTimeB.dstflag = 0;
            UCTSetFTime (&SysTimeB);
            SendTextToLogger ("Inf", "Adjustment made to: End Daylight Saving Time");
            UCTFTime (&TimeB);
            PutDSTTime (TimeB.time);
            ChangeToDSTDelay = TimeB.time + 3660;    /* Don't allow Change To Dst for at least an hour */
         }
         else if((SysTimeB.dstflag && !(TimeB.dstflag)))
         {
            if(TimeB.time > (LONG)ChangeToDSTDelay)
            {
               /* add some time in for the hour when we could ossolate */
               UCTSetFTime (&SysTimeB);
               SendTextToLogger ("Inf", "Adjustment made to: Start Daylight Saving Time");
               UCTFTime (&TimeB);
               PutDSTTime (TimeB.time);
            }
         }
      }

      if(!(TimeB.time % (60L * CheckInterval)))
      {
         for(Port = 0; Port < PORTMAX; Port++)
         {
            /* Make sure this port exists */
            if( QueueHandle(Port) == NULL)
               continue;
            /* Yup so loop through Remote's */
            for(Remote = 0; Remote <= CCUGLOBAL; Remote++)
            {
               if(CCUInfo[Port][Remote] == NULL)
               {
                  continue;
               }

               if(Remote == RTUGLOBAL || Remote == CCUGLOBAL)
               {
                  continue;
               }

               /* Check if we have talked to this guy this time */
               if(CCUInfo[Port][Remote]->FiveMinuteCount <= 1)
               {
                  /* Reset the count */
                  CCUInfo[Port][Remote]->FiveMinuteCount = 0;

                  /* Check if the port is inhibited */
                  if(NULL != (PortRecord = PortManager.PortGetEqual ((LONG)Port)))
                  {
                     if(PortRecord->isInhibited())
                     {
                        break;
                     }
                  }


                  // FIX FIX FIX to CtiDeviceBase
                  /* Nope so now make sure that remote isn't inhibited */
                  if(!(DeviceManager.RemoteGetPortRemoteEqual ((LONG)Port, (LONG)Remote)))
                  {
                     if(RemoteRecord.Status & INHIBITED)
                     {
                        continue;
                     }
                  }

                  /* Poke at this remote with a sharp stick to see if it is still alive */
                  if((OutMessage = new OUTMESS) == NULL)
                  {
                     continue;
                  }

                  /* Load up the common things */
                  OutMessage->Port = Port;
                  OutMessage->Remote = Remote;
                  OutMessage->Priority = MAXPRIORITY - 2;
                  OutMessage->Retry = 0;
                  OutMessage->Sequence = 0;
                  OutMessage->ReturnNexus.NexusState =  CTINEXUS_STATE_NULL;
                  OutMessage->SaveNexus.NexusState =  CTINEXUS_STATE_NULL;

                  switch(CCUInfo[Port][Remote]->Type)
                  {
                  case TYPE_CCU700:
                  case TYPE_CCU710:
                     /* Build a loopback preamble */
                     if(ZeroRemoteAddress)
                     {
                        LPreamble (OutMessage->Buffer.OutMessage + PREIDLEN,
                                   0);
                     }
                     else
                     {
                        LPreamble (OutMessage->Buffer.OutMessage + PREIDLEN,
                                   OutMessage->Remote);
                     }

                     OutMessage->OutLength = 3 + 3;   /* Don't Ask */
                     OutMessage->InLength = 4;
                     OutMessage->TimeOut = 2;
                     OutMessage->EventCode = ENCODED | RESULT | NOWAIT;

                     break;

                  case TYPE_CCU711:
                     /* Build an IDLC loopback request */
                     OutMessage->EventCode = RESULT | RCONT | ENCODED | NOWAIT;
                     OutMessage->TimeOut = 2;
                     OutMessage->OutLength = 3;
                     OutMessage->InLength = 0;
                     OutMessage->Source = 0;
                     OutMessage->Destination = DEST_BASE;
                     OutMessage->Command = CMND_ACTIN;

                     OutMessage->Buffer.OutMessage[PREIDL] = NO_OP;

                     break;

                  case TYPE_ILEXRTU:
                     /* Build an Ilex RTU loopback (Time) request */
                     ILEXHeader (OutMessage->Buffer.OutMessage + PREIDLEN,
                                 OutMessage->Remote,
                                 ILEXTIMESYNC,
                                 TIMESYNC1,
                                 TIMESYNC2);

                     /* set the timesync function */
                     OutMessage->Buffer.OutMessage[PREIDLEN + 2] = ILEXGETTIME;

                     OutMessage->TimeOut = 2;
                     OutMessage->OutLength = ILEXTIMELENGTH;
                     OutMessage->InLength = -1;
                     OutMessage->EventCode = ENCODED | RESULT | NOWAIT;

                     break;

                  case TYPE_LCU415:
                  case TYPE_TCU5000:
                  case TYPE_LCU415LG:
                  case TYPE_LCU415ER:
                  case TYPE_TCU5500:
                     /* Build a mastercom loopback request */
                     MasterHeader (OutMessage->Buffer.OutMessage + PREIDLEN,
                                   OutMessage->Remote,
                                   MASTERLOOPBACK,
                                   0);

                     OutMessage->TimeOut = 2;
                     OutMessage->OutLength = MASTERLENGTH;
                     OutMessage->InLength = -1;
                     OutMessage->EventCode = ENCODED | RESULT | NOWAIT;

                     break;

                  default:
                     delete (OutMessage);
                     continue;
                  }

                  /* Send the message to the appropriate port queue */
                  if(PortManager.writeQueue(Port,
                                            OutMessage->EventCode,
                                            sizeof (*OutMessage),
                                            (VOID *) OutMessage,
                                            OutMessage->Priority))
                  {
                     printf ("Error Writing to Queue for Port %2hd\n", Port);
                     delete (OutMessage);
                     continue;
                  }

                  switch(CCUInfo[Port][Remote]->Type)
                  {
                  case TYPE_CCU711:
                     CCUInfo[Port][Remote]->PortQueueEnts++;
                     CCUInfo[Port][Remote]->PortQueueConts++;
                  }
               }
               else
               {
                  CCUInfo[Port][Remote]->FiveMinuteCount = 1;
               }
            }
         }
      }

      /* Check if we are on the hour mark */
      if(TimeB.time % 3600L)
         continue;

      TStruct = UCTLocalTime (TimeB.time,
                              TimeB.dstflag);

      /* Now walk down everything with statistics and copy them over */

      /* do the ports */

      if(!(PortGetFirst (&PortRecord)))
      {
         do
         {
            if(PortLock (&PortRecord))
            {
               continue;
            }

            if(PortStats[PortRecord.Port] == NULL)
            {
               PortUnLock (&PortRecord);
               continue;
            }

            if(PortRecord.Stats.ErrorResetTime != PortStats[PortRecord.Port]->Stats.ErrorResetTime)
            {
               PortStats[PortRecord.Port]->Stats.ErrorResetTime = PortRecord.Stats.ErrorResetTime;
               for(i = 0; i <= ERRLOGENTS; i++)
               {
                  PortStats[PortRecord.Port]->Stats.ErrorLog[i] = PortRecord.Stats.ErrorLog[i];
               }
            }

            UCTFTime (&TimeB);

            /* Only update the 24 hour log at midnight */
            if(!(TStruct->tm_hour))
            {
               /* Zero the 24 hour statistics */
               for(i = 0; i <= ERRLOGENTS; i++)
               {
                  PortStats[PortRecord.Port]->Stats.ErrorPrev24Log[i] = PortStats[PortRecord.Port]->Stats.Error24Log[i];
                  PortStats[PortRecord.Port]->Stats.Error24Log[i] = 0L;
               }

               /* update the reset time */
               PortStats[PortRecord.Port]->Stats.ErrorPrev24ResetTime = PortStats[PortRecord.Port]->Stats.Error24ResetTime;
               if(PortStats[PortRecord.Port]->Stats.ErrorDSTFlags & ERROR24RESETDSTMASK)
               {
                  PortStats[PortRecord.Port]->Stats.ErrorDSTFlags |= ERRORPREV24RESETDSTMASK;
               }
               else
               {
                  PortStats[PortRecord.Port]->Stats.ErrorDSTFlags &= ~ERRORPREV24RESETDSTMASK;
               }

               PortStats[PortRecord.Port]->Stats.Error24ResetTime = TimeB.time;
               if(TimeB.dstflag)
               {
                  PortStats[PortRecord.Port]->Stats.ErrorDSTFlags |= ERROR24RESETDSTMASK;
               }
               else
               {
                  PortStats[PortRecord.Port]->Stats.ErrorDSTFlags &= ~ERROR24RESETDSTMASK;
               }
            }

            PortStats[PortRecord.Port]->Stats.Error24RollTime = TimeB.time;
            if(TimeB.dstflag)
            {
               PortStats[PortRecord.Port]->Stats.ErrorDSTFlags |= ERROR24ROLLDSTMASK;
            }
            else
            {
               PortStats[PortRecord.Port]->Stats.ErrorDSTFlags &= ~ERROR24ROLLDSTMASK;
            }


            for(i = 0; i <= ERRLOGENTS; i++)
            {
               for(j = 23; j > 0; j--)
               {
                  PortStats[PortRecord.Port]->Stats.Error24Roll[j][i] = PortStats[PortRecord.Port]->Stats.Error24Roll[j - 1][i];
               }
               PortStats[PortRecord.Port]->Stats.Error24Roll[0][i] = 0L;
            }

            /* and do a fast update */
            PortRecord.Stats = PortStats[PortRecord.Port]->Stats;
            if(PortFastUpdate (&PortRecord))
            {
               PortUnLock (&PortRecord);
            }

         } while(!(PortGetGT (&PortRecord)));
      }

      /* Do the remotes */
      if(!(RemoteGetFirst (&RemoteRecord)))
      {
         do
         {
            if(RemoteRecord.Remote == 0xffff)
            {
               continue;
            }

            if(CCUInfo[RemoteRecord.Port][RemoteRecord.Remote] == NULL)
            {
               continue;
            }

            if(RemoteLock (&RemoteRecord))
            {
               continue;
            }

            if(RemoteRecord.Stats.ErrorResetTime != CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorResetTime)
            {
               CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorResetTime = RemoteRecord.Stats.ErrorResetTime;
               for(i = 0; i <= ERRLOGENTS; i++)
               {
                  CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorLog[i] = RemoteRecord.Stats.ErrorLog[i];
               }
            }

            UCTFTime (&TimeB);

            if(!(TStruct->tm_hour))
            {
               /* Zero the 24 hour statistics */
               for(i = 0; i <= ERRLOGENTS; i++)
               {
                  CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorPrev24Log[i] = CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.Error24Log[i];
                  CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.Error24Log[i] = 0L;
               }

               /* update the reset times */
               CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorPrev24ResetTime = CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.Error24ResetTime;
               if(CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorDSTFlags & ERROR24RESETDSTMASK)
               {
                  CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorDSTFlags |= ERRORPREV24RESETDSTMASK;
               }
               else
               {
                  CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorDSTFlags &= ~ERRORPREV24RESETDSTMASK;
               }

               CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.Error24ResetTime = TimeB.time;
               if(TimeB.dstflag)
               {
                  CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorDSTFlags |= ERROR24RESETDSTMASK;
               }
               else
               {
                  CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorDSTFlags &= ~ERROR24RESETDSTMASK;
               }
            }

            CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.Error24RollTime = TimeB.time;
            if(TimeB.dstflag)
            {
               CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorDSTFlags |= ERROR24ROLLDSTMASK;
            }
            else
            {
               CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorDSTFlags &= ~ERROR24ROLLDSTMASK;
            }


            for(i = 0; i <= ERRLOGENTS; i++)
            {
               for(j = 23; j > 0; j--)
               {
                  CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.Error24Roll[j][i] = CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.Error24Roll[j - 1][i];
               }
               CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.Error24Roll[0][i] = 0L;
            }

            /* and do a fast update */
            RemoteRecord.Stats = CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats;
            if(RemoteFastUpdate (&RemoteRecord))
            {
               RemoteUnLock (&RemoteRecord);
            }

         } while(!(RemoteGetGT (&RemoteRecord)));
      }

      /* And the routes */
      if(!(RouteGetFirst (&RouteRecord)))
      {
         do
         {
            if(RouteLock (&RouteRecord))
            {
               continue;
            }

            UCTFTime (&TimeB);

            if(!(TStruct->tm_hour))
            {
               /* Zero the 24 hour statistics */
               for(i = 0; i <= ERRLOGENTS; i++)
               {
                  RouteRecord.Stats.ErrorPrev24Log[i] = RouteRecord.Stats.Error24Log[i];
                  RouteRecord.Stats.Error24Log[i] = 0L;
               }

               /* update the reset time */
               RouteRecord.Stats.ErrorPrev24ResetTime = RouteRecord.Stats.Error24ResetTime;
               if(RouteRecord.Stats.ErrorDSTFlags & ERROR24RESETDSTMASK)
               {
                  RouteRecord.Stats.ErrorDSTFlags |= ERRORPREV24RESETDSTMASK;
               }
               else
               {
                  RouteRecord.Stats.ErrorDSTFlags &= ~ERRORPREV24RESETDSTMASK;
               }

               RouteRecord.Stats.Error24ResetTime = TimeB.time;
               if(TimeB.dstflag)
               {
                  RouteRecord.Stats.ErrorDSTFlags |= ERROR24RESETDSTMASK;
               }
               else
               {
                  RouteRecord.Stats.ErrorDSTFlags &= ~ERROR24RESETDSTMASK;
               }
            }

            RouteRecord.Stats.Error24RollTime = TimeB.time;
            if(TimeB.dstflag)
            {
               RouteRecord.Stats.ErrorDSTFlags |= ERROR24ROLLDSTMASK;
            }
            else
            {
               RouteRecord.Stats.ErrorDSTFlags &= ~ERROR24ROLLDSTMASK;
            }

            for(i = 0; i <= ERRLOGENTS; i++)
            {
               for(j = 23; j > 0; j--)
               {
                  RouteRecord.Stats.Error24Roll[j][i] = RouteRecord.Stats.Error24Roll[j - 1][i];
               }
               RouteRecord.Stats.Error24Roll[0][i] = 0L;
            }

            /* and do a fast update */
            if(RouteFastUpdate (&RouteRecord))
            {
               RouteUnLock (&RouteRecord);
            }

         } while(!(RouteGetGT (&RouteRecord)));
      }

      /* and the devices */
      if(!(DeviceGetFirst (&DeviceRecord)))
      {
         do
         {
            if(DeviceLock (&DeviceRecord))
            {
               continue;
            }

            UCTFTime (&TimeB);

            if(!(TStruct->tm_hour))
            {
               /* Zero the 24 hour statistics */
               for(i = 0; i <= ERRLOGENTS; i++)
               {
                  DeviceRecord.Stats.ErrorPrev24Log[i] = DeviceRecord.Stats.Error24Log[i];
                  DeviceRecord.Stats.Error24Log[i] = 0L;
               }

               /* update the reset time */
               DeviceRecord.Stats.ErrorPrev24ResetTime = DeviceRecord.Stats.Error24ResetTime;
               if(DeviceRecord.Stats.ErrorDSTFlags & ERROR24RESETDSTMASK)
               {
                  DeviceRecord.Stats.ErrorDSTFlags |= ERRORPREV24RESETDSTMASK;
               }
               else
               {
                  DeviceRecord.Stats.ErrorDSTFlags &= ~ERRORPREV24RESETDSTMASK;
               }

               DeviceRecord.Stats.Error24ResetTime = TimeB.time;
               if(TimeB.dstflag)
               {
                  DeviceRecord.Stats.ErrorDSTFlags |= ERROR24RESETDSTMASK;
               }
               else
               {
                  DeviceRecord.Stats.ErrorDSTFlags &= ~ERROR24RESETDSTMASK;
               }
            }

            DeviceRecord.Stats.Error24RollTime = TimeB.time;
            if(TimeB.dstflag)
            {
               DeviceRecord.Stats.ErrorDSTFlags |= ERROR24ROLLDSTMASK;
            }
            else
            {
               DeviceRecord.Stats.ErrorDSTFlags &= ~ERROR24ROLLDSTMASK;
            }

            for(i = 0; i <= ERRLOGENTS; i++)
            {
               for(j = 23; j > 0; j--)
               {
                  DeviceRecord.Stats.Error24Roll[j][i] = DeviceRecord.Stats.Error24Roll[j - 1][i];
               }
               DeviceRecord.Stats.Error24Roll[0][i] = 0L;
            }

            /* and do a fast update */
            if(DeviceFastUpdate (&DeviceRecord))
            {
               DeviceUnLock (&DeviceRecord);
            }

         } while(!(DeviceGetGT (&DeviceRecord)));
      }
   }
#endif

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " PerfThread: TID " << CurrentTID() << " exiting" << endl;
   }

}

/* Routine to Update statistics for Ports and Remotes every 5 minutes */
VOID PerfUpdateThread (PVOID Arg)
{
   ULONG PostCount;
   USHORT i;
   CtiPort *PortRecord;
   REMOTE RemoteRecord;
   struct timeb TimeB;
   HEV PerfUpdateSem;
   PSZ Environment;
   USHORT Rate;

   /* set the priority of this guy high */
   CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, 31, 0);

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " PerfUpdateThread started as TID:   " << CurrentTID() << endl;
   }

   /* Check for the port update frequency environment variable */
   if(!(CTIScanEnv ("DSM2_PORTSTATRATE", &Environment)))
   {
      if(!(Rate = atoi (Environment)))
      {
         PortPerfUpdateCount = Rate;
      }
   }

   /* Check for the port update frequency environment variable */
   if(!(CTIScanEnv ("DSM2_REMOTESTATRATE",
                    &Environment)))
   {
      if(!(Rate = atoi (Environment)))
      {
         RemotePerfUpdateCount = Rate;
      }
   }

   if(CTICreateEventSem ("PERFUPDATE",
                         &PerfUpdateSem,
                         0,
                         FALSE))
   {
      printf ("Error Creating Performance Update Semaphore\n");
      CTIExit (EXIT_PROCESS, -1);
   }

#ifdef OLD_WAY       // FIX FIX FIX CGP 071999 Add this back in later
   /* do this as long as we are running */
   for(;;)
   {
      /* Update Stats on a five minute basis but not on the hour mark.*/

      CTIResetEventSem (PerfUpdateSem,
                        &PostCount);

      UCTFTime (&TimeB);

      CTIWaitEventSem (PerfUpdateSem,
                       1000L * (300L - (TimeB.time % 300L)));

      UCTFTime (&TimeB);

      if(!(TimeB.time % 3600))
      {
         continue;
      }

      /* do the ports */
      if(!(PortGetFirst (&PortRecord)))
      {
         do
         {
            if(PortLock (&PortRecord))
            {
               continue;
            }

            /* Make sure that this portstat has been allocated */
            if(PortStats[PortRecord.Port] == NULL)
            {
               PortUnLock (&PortRecord);
               continue;
            }

            /* Check to see if this port data base has has been reset */
            /* Update data base with whichever stats are more current */

            if(PortRecord.Stats.ErrorResetTime != PortStats[PortRecord.Port]->Stats.ErrorResetTime)
            {
               PortStats[PortRecord.Port]->Stats.ErrorResetTime = PortRecord.Stats.ErrorResetTime;
               for(i = 0; i <= ERRLOGENTS; i++)
               {
                  PortStats[PortRecord.Port]->Stats.ErrorLog[i] = PortRecord.Stats.ErrorLog[i];
               }
            }

            PortRecord.Stats = PortStats[PortRecord.Port]->Stats;

            /* and do a fast update */
            if(PortFastUpdate (&PortRecord))
            {
               PortUnLock (&PortRecord);
            }
         } while(!(PortGetGT (&PortRecord)));
      }

      /* Do the remotes */
      if(!(RemoteGetFirst (&RemoteRecord)))
      {
         do
         {
            if(RemoteRecord.Remote == 0xffff)
            {
               continue;
            }

            if(CCUInfo[RemoteRecord.Port][RemoteRecord.Remote] == NULL)
            {
               continue;
            }

            if(RemoteLock (&RemoteRecord))
            {
               continue;
            }

            /* Check to see if this remote data base has has been reset */
            /* Update data base with whichever stats are more current */

            if(RemoteRecord.Stats.ErrorResetTime != CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorResetTime)
            {
               CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorResetTime = RemoteRecord.Stats.ErrorResetTime;
               for(i = 0; i <= ERRLOGENTS; i++)
               {
                  CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats.ErrorLog[i] = RemoteRecord.Stats.ErrorLog[i];
               }
            }

            RemoteRecord.Stats = CCUInfo[RemoteRecord.Port][RemoteRecord.Remote]->Stats;

            /* and do a fast update */
            if(RemoteFastUpdate (&RemoteRecord))
            {
               RemoteUnLock (&RemoteRecord);
            }
         } while(!(RemoteGetGT (&RemoteRecord)));
      }
   }
#endif
   // printf ("Performance Update Thread TID:  %ld exiting\n", CurrentTID ());
   {
     CtiLockGuard<CtiLogger> doubt_guard(dout);
     dout << RWTime() << " PerfUpdateThread: TID " << CurrentTID() << " exiting" << endl;
   }
}
