
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   scan_dlc
*
* Date:           8-29-93
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SCANNER/scan_dlc.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma title ( "SCAN_DLC -- Scan DLC" )
#pragma subtitle ( "CTI Copyright (c) 1990-1998" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1998 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        Ben D. Wallace

    FileName:
        SCAN_DLC.C

    Purpose:
        This is the scan table and status table scanning routines
        from the load control program converted from BASIC TO C.


    The following procedures are contained in this module:

        Time2ScanDLCValue       RequestDLCValue
        ProcessDLCValue         BPreamble
        InsertPlugData          InitOneScanPoint
        InitDLCScanning         InitOneStatusPoint
        RequestDLCStatus        TranslateStatusValue
        ProcessDLCStatus        Time2ScanDLCStatus


    Initial Date:
        8-29-93

    Revision History:
        12-28-93    Modified into 32 bit scanner                    WRO
        6-17-96  Added TARDY_TIME_DELAY to be 3600 seconds in DLC   BDW
        8-4-97   Added support for MCT3XX function reads            JRC

   -------------------------------------------------------------------- */
#include <windows.h>       // These next few are required for Win32
#include "os2_2w32.h"
#include "cticalls.h"
//#include <btrapi.h>

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

// // #include "btrieve.h"

#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "drp.h"
#include "elogger.h"
#include "alarmlog.h"
#include "routes.h"
#include "queues.h"
#include "porter.h"
#include "lm_auto.h"
#include "perform.h"
#include "scanner.h"

#include "scanglob.h"

#include "scansup.h"
#include "dlldefs.h"


/* this proc inits all DLC scanning at start up or during a reset */
DLLEXPORT INT InitDLCScanning (void)
{
   #if 0

   SCANPOINT ScanRecord;
   STATPOINT StatusRecord;
   DEVICE DeviceRecord;
   int AccumulatorPoint = {FALSE};

   DeviceRecord.DeviceName[0] = ' ';

   /* init the scan (analogs and PA's) */
   if(!(ScanPointGetFirst (&ScanRecord)))
   {
      /* found a scan point now check all */
      do
      {

         if(DeviceRecord.DeviceName[0] != ' ' && strncmp (DeviceRecord.DeviceName, ScanRecord.DeviceName, sizeof(ScanRecord.DeviceName)))
         {
            /* device name is not blank and the scan device
               is different the previous device */
            if(AccumulatorPoint)
            {
               /* it has an accumulator point so mark it to us the meter reading */
               DeviceRecord.ScanStatus |= METERREAD;
               DeviceFastUpdate (&DeviceRecord);
            }
            else if(DeviceRecord.ScanStatus & METERREAD)
            {
               /* meter reading flag is set and does not need to be */
               DeviceRecord.ScanStatus &= ~METERREAD;
               DeviceFastUpdate (&DeviceRecord);
            }
            AccumulatorPoint = FALSE;  /* reset the flag for the new device */
         }

         /* Init and validate a scan point */
         ScanPointLock (&ScanRecord);

         /* initialize the point and get is point type */
         if(InitOneScanPoint (&ScanRecord, &DeviceRecord) == ACCUMULATORPOINT)
         {
            AccumulatorPoint = TRUE;
         }

         /* save the status point record */
         ScanPointFastUpdate (&ScanRecord);

      } while(!(ScanPointgetNext (&ScanRecord)));
   }

   /* when we fall through the loop we have not done the last device -- if there was one */
   if(DeviceRecord.DeviceName[0] != ' ')
   {
      /* device name is not blank and the scan device
        is different the previous device */
      if(AccumulatorPoint)
      {
         /* it has an accumulator point so mark it to us the meter reading */
         DeviceRecord.ScanStatus |= METERREAD;
         DeviceFastUpdate (&DeviceRecord);
      }
      else if(DeviceRecord.ScanStatus & METERREAD)
      {
         /* meter reading flag is set and does not need to be so clear it */
         DeviceRecord.ScanStatus &= ~METERREAD;
         DeviceFastUpdate (&DeviceRecord);
      }
   }


   /* init the status (DLC statuses) */
   if(!(StatPointGetFirst (&StatusRecord)))
   {
      /* found a status point now check all */
      do
      {
         /* Init and validate a status point */
         StatPointLock (&StatusRecord);
         InitOneStatusPoint(&StatusRecord);
         StatPointFastUpdate (&StatusRecord);
      } while(!(StatPointgetNext (&StatusRecord)));
   }

   #endif

   return(NORMAL);
}


/* this checks if any DLC status points need to be scanned */
DLLEXPORT INT Time2ScanDLCStatus (RWTime &TimeNow, const RWTime &MyTime)
{
   #if 0

   STATPOINT StatusRecord;
   CTIPOINT PointRecord;
   DEVICE DeviceRecord;
   CHAR SavePointName[STANDNAMLEN];
   struct timeb TimeB;
   int i, rc;
   USHORT MySequence;

   *ScanSleep = setNextInterval (MyTime + 1, 60L);

   /* keep looping until all no more to scan */
   for(;;)
   {


      /* start of changed code - changed like the BASIC version */
      if(StatPointGetFirstScan (&StatusRecord))
      {
         /* nothing to scan */
         return(NORMAL);
      }


      UCTFTime (&TimeB);

      if(StatusRecord.NextScan == 0L)
      {
         /* 0 means new point or scan rate changed */
         rc = StatPointLock (&StatusRecord);
         if(rc == RECORD_IN_USE)
         {
            /* try lock again */
            rc = StatPointLock (&StatusRecord);
         }

         if(rc == NORMAL)
         {
            /* init the new scan and update */
            InitOneStatusPoint (&StatusRecord);
            StatPointFastUpdate (&StatusRecord);
         }
         else if(rc == RECORD_IN_USE)
         {
            printf ("%0.8s Record was in use DLC device:  %0.20s \n", UCTAscTime (LongTime (), (USHORT)DSTFlag ()) + 11, StatusRecord.DeviceName);
            *ScanSleep = TimeB.time + 5;
            return(NORMAL);
         }
         else
         {
            printf ("%0.8s Record lock err %ld DLC device:  %0.20s \n", UCTAscTime (LongTime (), (USHORT)DSTFlag ()) + 11, rc, StatusRecord.DeviceName);
            return(NORMAL);
         }

      }
      else if(StatusRecord.NextScan > (ULONG)TimeB.time)
      {
         /* need to wait for a while to before we scan again */
         break;
      }
      else
      {
         /* queue up this point */
         memcpy (DeviceRecord.DeviceName, StatusRecord.DeviceName, sizeof (DeviceRecord.DeviceName));

         if(DevicegetEqual (&DeviceRecord))
         {
            /* problem getting this device */
            rc = StatPointLock (&StatusRecord);
            if(rc == NORMAL)
            {
               /* do not scan this device again */
               StatusRecord.NextScan = NEVER_SCAN_TIME;
               StatPointFastUpdate (&StatusRecord);
               continue;
            }
            else
            {
               printf ("%0.8s Record lock err %ld DLC device:  %0.20s \n", UCTAscTime (LongTime (), (USHORT)DSTFlag ()) + 11, rc, StatusRecord.DeviceName);
               *ScanSleep = TimeB.time + 5;
               return(NORMAL);
            }
         }

         /* check if device is out of scan */
         if(DeviceRecord.Status & INHIBITED)
         {
            /* no scanning device is out of scan */
            InsertPlugStatus (&DeviceRecord);
            continue;
         }


         /* problem getting this device */
         rc = StatPointLock (&StatusRecord);
         if(rc != NORMAL)
         {
            /* could not get this device so lock record */
            printf ("%0.8s Record lock err %ld DLC device:  %0.20s \n", UCTAscTime (LongTime (), (USHORT)DSTFlag ()) + 11, rc, StatusRecord.DeviceName);
            *ScanSleep = TimeB.time + 5;
            return(NORMAL);
         }

         if(StatusRecord.QueueStat == POINT_QUEUED && (StatusRecord.QueueTime + TARDY_TIME_DELAY) > (ULONG)TimeB.time)
         {
            /* point is still queued on so see how long it been waiting */
            StatusRecord.NextScan = StatusRecord.QueueTime + TARDY_TIME_DELAY + 1L;
            StatPointFastUpdate (&StatusRecord);
            continue;
         }

         /* queue up the request to porter */
         i = RequestDLCStatus (&DeviceRecord, &StatusRecord);

         UCTFTime (&TimeB);
         if(i == REQUEUE_DEVICE)
         {
            /* need to reque the point */
            StatusRecord.NextScan = TimeB.time + 1L;
            StatusRecord.QueueStat = REQUEUE_POINT;
            StatPointFastUpdate (&StatusRecord);
         }
         else if(i == FAILED_NO_MORE)
         {
            /* need to wait until next scan */
            StatPointUnLock (&StatusRecord);
            InsertPlugStatus (&DeviceRecord);
            continue;
         }
         else
         {
            StatusRecord.QueueTime = TimeB.time;
            StatusRecord.RetryCount = 0;
            StatusRecord.QueueStat = POINT_QUEUED;

            /* save the sequence number for the other points */
            MySequence = StatusRecord.SeqNumber;

            StatPointFastUpdate (&StatusRecord);

            if(DeviceRecord.DeviceType != TYPE_REPEATER900 &&
               DeviceRecord.DeviceType != TYPE_REPEATER800)
            {
               /* must find all status points as being scanned */
               memcpy (SavePointName, StatusRecord.PointName, sizeof (StatusRecord.PointName));
               memcpy (PointRecord.DeviceName, StatusRecord.DeviceName, sizeof (StatusRecord.DeviceName));

               if(!(PointgetDeviceFirst (&PointRecord)))
               {
                  do
                  {
                     switch(PointRecord.PointType)
                     {
                     case TWOSTATEPOINT:
                     case THREESTATEPOINT:
                        if(strncmp (PointRecord.PointName, SavePointName, sizeof (PointRecord.PointName)))
                        {

                           /* not the same get the scan table point  */
                           memcpy (StatusRecord.DeviceName, PointRecord.DeviceName, sizeof (PointRecord.DeviceName));
                           memcpy (StatusRecord.PointName, PointRecord.PointName, sizeof (PointRecord.PointName));

                           /* mark it as being scanned */
                           if(StatPointLock (&StatusRecord) == NORMAL)
                           {
                              /* make sure all points have the
                                 same sequence number */
                              StatusRecord.SeqNumber = MySequence;

                              StatusRecord.QueueStat = POINT_QUEUED;
                              StatusRecord.NextScan = NEVER_SCAN_TIME;
                              StatPointFastUpdate (&StatusRecord);
                           }

                        }
                        break;

                     default:
                        continue;
                     }
                  } while(!(PointgetDeviceNext (&PointRecord)));
               }
            }
         }
      }
   }   /* end of for next loop */


   /* Check when the next scan is */
   if(StatusRecord.NextScan < *ScanSleep)
   {
      /* need to check sooner than 1-minute */
      *ScanSleep = StatusRecord.NextScan;
   }

   #endif

   return(NORMAL);
}

/* Checks if DLC analog or PA points need to be scanned */
DLLEXPORT INT Time2ScanDLCValue (RWTime &TimeNow, const RWTime &MyTime)
{
   #if 0

   ULONG i, rc;
   SCANPOINT ScanRecord;
   CTIPOINT PointRecord;
   CtiDeviceBase *DeviceRecord;
   CHAR SavePointName[STANDNAMLEN];
   struct timeb TimeB;

   /* this line was added to act like the BASIC version
      it sets the default check time of 1 - minute     */
   *ScanSleep = setNextInterval (MyTime + 1, 60L);

   /* keep looping until all no more to scan */
   for(;;)
   {

      /* start of changed code - changed like the BASIC version */
      if(ScanPointGetFirstScan (&ScanRecord))
      {
         /* nothing to scan */
         return(NORMAL);
      }

      UCTFTime (&TimeB);

      /* check if we need to scan or update */
      if(ScanRecord.NextScan == 0)
      {
         /* 0 means new point or scan rate changed */
#ifdef DEBUG2
         printf ("%0.8s Initializing DLC device:  %0.20s \n", UCTAscTime (LongTime (), DSTFlag ()) + 11, ScanRecord.DeviceName);
#endif
         rc = ScanPointLock (&ScanRecord);
         if(rc == NORMAL)
         {
            /* set the next scan time for this new record */
            InitOneScanPoint (&ScanRecord, &DeviceRecord);
            ScanPointFastUpdate (&ScanRecord);
         }
         else if(rc == RECORD_IN_USE)
         {
            printf ("%0.8s Record was in use DLC device:  %0.20s \n", UCTAscTime (LongTime (), (USHORT)DSTFlag ()) + 11, ScanRecord.DeviceName);
            *ScanSleep = TimeB.time + 5;
            return(NORMAL);
         }
         else
         {
            printf ("%0.8s Record lock err %ld DLC device:  %0.20s \n", UCTAscTime (LongTime (), (USHORT)DSTFlag ()) + 11, rc, ScanRecord.DeviceName);
            return(NORMAL);
         }

      }
      else if(ScanRecord.NextScan > (ULONG)TimeB.time)
      {
         /* need to wait for a while to before we scan again */
#ifdef DEBUG2
         printf ("%0.8s Done checking DLC devices:  %0.20s \n", UCTAscTime (LongTime (), (USHORT)DSTFlag ()) + 11, ScanRecord.DeviceName);
#endif

         break;
      }
      else
      {
         /* queue up this point */
#ifdef DEBUG2
         printf ("%0.8s Queuing DLC device:  %0.20s \n", UCTAscTime (LongTime (), (USHORT)DSTFlag ()) + 11, ScanRecord.DeviceName);
#endif

         memcpy (DeviceRecord.DeviceName, ScanRecord.DeviceName, sizeof (DeviceRecord.DeviceName));

         if(DeviceGetEqual (&DeviceRecord))
         {
            /* problem getting this device */
            rc = ScanPointLock (&ScanRecord);
            ScanRecord.NextScan = NEVER_SCAN_TIME;
            ScanPointFastUpdate (&ScanRecord);
            continue;
         }

         /* check if device is out of scan */
         if(DeviceRecord.Status & INHIBITED)
         {
            /* no scanning device */
#ifdef DEBUG2
            printf ("%0.8s Plugging Inhibited DLC device:  %0.20s \n", UCTAscTime (LongTime (), (USHORT)DSTFlag ()) + 11, ScanRecord.DeviceName);
#endif

            InsertPlugData (&DeviceRecord);
            continue;
         }

         /* lock the record for the code below */
         rc = ScanPointLock (&ScanRecord);
         if(rc == RECORD_IN_USE)
         {
            printf ("%0.8s Record was in use DLC device:  %0.20s \n", UCTAscTime (LongTime (), (USHORT)DSTFlag ()) + 11, ScanRecord.DeviceName);
            *ScanSleep = TimeB.time + 5;
            return(NORMAL);
         }
         else if(rc != NORMAL)
         {
            printf ("%0.8s Record lock err %ld DLC device:  %0.20s \n", UCTAscTime (LongTime (), (USHORT)DSTFlag ()) + 11, rc, ScanRecord.DeviceName);
            return(NORMAL);
         }


         /* Scan record is now locked  */
         if(ScanRecord.QueueStat == POINT_QUEUED && (ScanRecord.QueueTime + TARDY_TIME_DELAY) > (ULONG)TimeB.time)
         {
            /* point is still queued on so see how long it been waiting */
            ScanRecord.NextScan = ScanRecord.QueueTime + TARDY_TIME_DELAY + 1L;
            ScanPointFastUpdate (&ScanRecord);
            continue;
         }

         i = RequestDLCValue (&DeviceRecord, &ScanRecord);
         UCTFTime (&TimeB);

         if(i == REQUEUE_DEVICE)
         {
            /* need to reque the point */
            ScanRecord.NextScan = TimeB.time + 1L;
            ScanRecord.QueueStat = REQUEUE_POINT;
            ScanPointFastUpdate (&ScanRecord);
         }
         else if(i == FAILED_NO_MORE)
         {
            /* need to wait until next scan */
            ScanPointUnLock (&ScanRecord);
            InsertPlugData (&DeviceRecord);
            continue;
         }
         else
         {
            ScanRecord.QueueTime = TimeB.time;
            ScanRecord.RetryCount = 0;
            ScanRecord.QueueStat = POINT_QUEUED;

            ScanPointFastUpdate (&ScanRecord);

            if(DeviceRecord.DeviceType == TYPEDCT501)
            {
               /* must find all dct points on each of the 4 channels
               a flag them as being scanned */

               memcpy (SavePointName, ScanRecord.PointName, sizeof (ScanRecord.PointName));

               for(i = 1; i < 5; i++)
               {
                  /* set the search criteria */
                  memcpy (PointRecord.DeviceName, DeviceRecord.DeviceName, sizeof (DeviceRecord.DeviceName));

                  PointRecord.PointType = ANALOGPOINT;
                  PointRecord.PointOffset = (USHORT)i;

                  if(!(PointgetOffset (&PointRecord)))
                  {
                     /* found a point lets make sure its not the point
                     we already update when we found the scan point */
                     if(strncmp (PointRecord.PointName, SavePointName, sizeof (PointRecord.PointName)))
                     {
                        /* not the same get the scan table point  */
                        memcpy (ScanRecord.DeviceName, PointRecord.DeviceName, sizeof (PointRecord.DeviceName));
                        memcpy (ScanRecord.PointName, PointRecord.PointName, sizeof (PointRecord.PointName));

                        rc = ScanPointLock (&ScanRecord);
                        if(rc == NORMAL)
                        {
                           /* we locked a scan point and marked it as being scanned */
                           ScanRecord.QueueStat = POINT_QUEUED;
                           ScanRecord.NextScan = NEVER_SCAN_TIME;
                           ScanPointFastUpdate (&ScanRecord);
                        }

                     }
                  }
               }   /* end of point for next loop */
            } /* end of if TYPEDCT501 */

            if((DeviceRecord.DeviceType == TYPEMCT318) ||
               (DeviceRecord.DeviceType == TYPEMCT360)  ||
               (DeviceRecord.DeviceType == TYPEMCT370))
            {
               /* must find all mct points on each of the 3 kyz channels
               and flag them as being scanned */

               memcpy (SavePointName, ScanRecord.PointName, sizeof (ScanRecord.PointName));

               for(i = 1; i < 4; i++)
               {
                  /* set the search criteria */
                  memcpy (PointRecord.DeviceName, DeviceRecord.DeviceName, sizeof (DeviceRecord.DeviceName));

                  if((DeviceRecord.DeviceType == TYPEMCT360 || DeviceRecord.DeviceType == TYPEMCT370)
                     && DeviceRecord.SecurityLevel == 1)
                  {
                     if(i > 2)
                     {
                        /* only 2 points for Alpha */
                        break;
                     }

                     /* Alpha Meter Points are Analog */
                     PointRecord.PointType = ANALOGPOINT;
                     PointRecord.PointOffset = (USHORT)(i * 10);
                  }
                  else
                  {
                     PointRecord.PointType = DEMANDACCUMPOINT;
                     PointRecord.PointOffset = (USHORT)i;
                  }

                  if(!(PointgetOffset (&PointRecord)))
                  {
                     /* found a point lets make sure its not the point
                     we already update when we found the scan point */
                     if(strncmp (PointRecord.PointName, SavePointName, sizeof (PointRecord.PointName)))
                     {
                        /* not the same get the scan table point  */
                        memcpy (ScanRecord.DeviceName, PointRecord.DeviceName, sizeof (PointRecord.DeviceName));
                        memcpy (ScanRecord.PointName, PointRecord.PointName, sizeof (PointRecord.PointName));

                        rc = ScanPointLock (&ScanRecord);
                        if(rc == NORMAL)
                        {
                           /* we found and locked scan point mark as being scanned */
                           ScanRecord.QueueStat = POINT_QUEUED;
                           ScanRecord.NextScan = NEVER_SCAN_TIME;
                           ScanPointFastUpdate (&ScanRecord);
                        }
                     }
                  }
               }   /* end of point for next loop */
            } /* end of if TYPEMCT318, 360, 370 */

         } /* end of else */
      }
   }   /* end of for next loop */

   /* Check when the next scan is */
   if(ScanRecord.NextScan < *ScanSleep)
   {
      /* need to check sooner than 1-minute */
      *ScanSleep = ScanRecord.NextScan;
   }

   #endif

   return(NORMAL);
}

#ifdef OLD_CRAP

/* this function builds a request for porter to scan a DLC Analog or Pulse accumulator point(s) */
DLLEXPORT INT  RequestDLCValue (DEVICE *DeviceRecord, SCANPOINT *ScanRecord)

{
   BSTRUCT BSt;
   ROUTE RouteRecord;
   ERRSTRUCT ErrorRecord;
   DEVICEPERF DevicePerform;
   ROUTEPERF RoutePerform;
   COMM_ERROR_LOG_STRUCT ComErrorRecord;
   struct timeb TimeB;
   BOOL IsFunctionRead = FALSE;  /* flag used to tell queueing routine when we're doing a ftn. read */

   /* set the fixed values */
   BSt.Priority = DLCValuePriority;
   BSt.Retry = RETRY_SCAN;
   BSt.Address = DeviceRecord->Address;

   /* set the request address and length */
   switch(DeviceRecord->DeviceType)
   {

   case TYPELMT2:
   case TYPEMCT212:
   case TYPEMCT224:
   case TYPEMCT226:
      /* this is the non-feedback register
         which is to accumulators 5-min apart
         and must be subtracted from each other */
      BSt.Function = GENERAL_ACCUM_ADDRESS;
      BSt.Length = GENERAL_ACCUM_LENGTH;
      break;

   case TYPEDCT501:
      /* first analog channel possible with DCT,
         always read all four */
      BSt.Function = DCT_ANALOG_ADDRESS;
      BSt.Length = DCT_ANALOG_LENGTH;
      break;

   case TYPEMCT210:
      /* feedback register fixed at 5 minutes */
      if(DeviceRecord->ScanStatus & METERREAD)
      {
         /* this tells us we care about accumulator points so use them */
         BSt.Function = MCT210_ACCUM_ADDRESS;
         BSt.Length = MCT210_ACCUM_LENGTH;
      }
      else
      {
         BSt.Function = MCT210_DEMAND_ADDRESS;
         BSt.Length = MCT210_DEMAND_LENGTH;
      }
      break;

   case TYPEMCT213:
   case TYPEMCT260:
      if(DeviceRecord->SSpec == 74)
      {
         /* isn't this nice, same device type
            but different RAM map. this is 22x map */
         /* I am sure as hell glad that someone with a
            systems point of view designed this */
         BSt.Function = GENERAL_ACCUM_ADDRESS;
         BSt.Length = GENERAL_ACCUM_LENGTH;
      }
      else
      {
         /* mct-210 s-spec */
         BSt.Function = MCT210_DEMAND_ADDRESS;
         BSt.Length = MCT210_DEMAND_LENGTH;
      }
      break;

   case TYPEMCT310:
      BSt.Function = MCT310_DEMAND_ADDRESS;
      BSt.Length = MCT310_DEMAND_LENGTH;
      break;

   case TYPEMCT240:
   case TYPEMCT242:
   case TYPEMCT248:
   case TYPEMCT250:
      if(DeviceRecord->ScanStatus & METERREAD)
      {
         /* this tells us we care about accumulator points so use them */
         BSt.Function = GENERAL_ACCUM_ADDRESS;
         BSt.Length = GENERAL_ACCUM_LENGTH;
      }
      else
      {
         BSt.Function = GENERAL_DEMAND_ADDRESS;
         BSt.Length = GENERAL_DEMAND_LENGTH;
      }
      break;

   case TYPEMCT318:
      /* Function read brings back all status and value points in a single read */
      BSt.Function = MCT3XX_FUNCTION_READ;
      BSt.Length = MCT3XX_FUNCREAD_LENGTH;
      IsFunctionRead = TRUE;
      break;

   case TYPEMCT360:
   case TYPEMCT370:
      /* Function read brings back all status and value points in a single read
         Check if we are getting data from Alpha Meter */
      if(DeviceRecord->SecurityLevel == 1)
      {
         /* this is an Alpha and we need to scan it */
         BSt.Function = MCTALPHA_FUNCTION_READ;
      }
      else
      {
         /* scan the PI's and Statuses */
         BSt.Function = MCT3XX_FUNCTION_READ;
      }
      BSt.Length = MCT3XX_FUNCREAD_LENGTH;
      IsFunctionRead = TRUE;
      break;

   default:
      /* default to general accum address */
      BSt.Function = GENERAL_ACCUM_ADDRESS;
      BSt.Length = GENERAL_ACCUM_LENGTH;
   }

   /* get the Route */
   if(GetRoute (DeviceRecord->RouteName, ScanRecord->RetryCount, RouteRecord.RouteName) || RoutegetEqual (&RouteRecord))
   {
      /* could not get the route */
      return(REQUEUE_DEVICE);
   }

   /* Save the device name and point name */
   memcpy (BSt.DeviceName, DeviceRecord->DeviceName, STANDNAMLEN);
   memcpy (BSt.PointName, ScanRecord->PointName, STANDNAMLEN);

   /* set the request address and length */
   switch(BPreambleLoad (&RouteRecord, &BSt))
   {
   case TYPE_CCU700:
   case TYPE_CCU710:
   case TYPE_CCU711:
      /* it's a ccu so put on the queue */
      UCTFTime (&TimeB);
      /* queue in the CCU-711 */
      if(IsFunctionRead)
      {
         /* call special routine to do a ftn read */
         DevicePerform.Error = QueueFunctionRead (&BSt, QDLCValues);
      }
      else
      {
         if(QDLCValues)
         {
            DevicePerform.Error = nb2execsq (&BSt);
         }
         else
         {
            DevicePerform.Error = nb2execs (&BSt);
         }
      }

      if(DevicePerform.Error)
      {
         /* Queueing Error update performance */
         memcpy (DevicePerform.DeviceName, DeviceRecord->DeviceName, sizeof (DeviceRecord->DeviceName));

         DevicePerfUpdate (&DevicePerform, &ErrorRecord);

         memcpy (RoutePerform.RouteName, RouteRecord.RouteName, sizeof (RouteRecord.RouteName));

         RoutePerform.Type = ErrorRecord.Type;
         RoutePerfUpdate (&RoutePerform);
         memcpy (ComErrorRecord.DeviceName, DeviceRecord->DeviceName, sizeof (DeviceRecord->DeviceName));

         memcpy (ComErrorRecord.RouteName, RouteRecord.RouteName, sizeof (ComErrorRecord.RouteName));

         ComErrorRecord.TimeStamp = TimeB.time;
         if(TimeB.dstflag)
         {
            ComErrorRecord.StatusFlag = DSTACTIVE;
         }
         else
         {
            ComErrorRecord.StatusFlag = 0;
         }

         ComErrorLogAdd (&ComErrorRecord, &ErrorRecord, FALSE);

         /* added this line to increment the counter to the next route */
         GetNextRoute (DeviceRecord->RouteName, &ScanRecord->RetryCount, RouteRecord.RouteName);

         if(RouteRecord.RouteName[0] == ' ')
         {
            /* no more routes so quit wait til next interval */
            return(FAILED_NO_MORE);
         }

         return(REQUEUE_DEVICE);
      }
      else
      {
         /* its queued ok - set next scan for 60 minutes from now */
         ScanRecord->NextScan = TimeB.time + TARDY_TIME_DELAY;
      }

      break;

   default:
      /* remote was not found or is not a ccu */
      /* added this line to increment the counter to the next route */
      GetNextRoute (DeviceRecord->RouteName, &ScanRecord->RetryCount, RouteRecord.RouteName);

      if(RouteRecord.RouteName[0] == ' ')
      {
         /* no more routes so quit wait til next interval */
         return(FAILED_NO_MORE);
      }
      return(REQUEUE_DEVICE);
   }

   /* made it here all is well */
   return(NORMAL);
}


/* this proc processes a return message for a dlc value request */
DLLEXPORT INT ProcessDLCValue (USHORT ComReturn, DSTRUCT *DSt, DEVICE *DeviceRecord)
{
   ULONG RecentValue;
   ULONG PreviousValue;
   USHORT Pulses;
   USHORT InValidValue = FALSE;
   USHORT Point, x;
   USHORT TempDevType;
   USHORT SaveCount;
   SCANPOINT ScanRecord;
   ERRSTRUCT ErrorRecord;
   DEVICEPERF DevicePerform;
   ROUTEPERF RoutePerform;
   COMM_ERROR_LOG_STRUCT ComErrorRecord;
   CTIPOINT PointRecord;
   FLOAT Value, TempValue;
   struct timeb TimeB;
   DRPVALUE DRPValue;
   CHAR RouteName[STANDNAMLEN];
   int rc;
   BOOL AlphaMeterData = FALSE;
   BOOL UpdatePerfData = TRUE;
   int ByteOffset, HourMult;
   SYSTEMLOGMESS LogMessage;

   /* get the scan point first */
   memcpy (ScanRecord.DeviceName,   DSt->DeviceName, sizeof (DSt->DeviceName));
   memcpy (ScanRecord.PointName,    DSt->PointName, sizeof (DSt->PointName));

   if(ScanPointgetEqual (&ScanRecord))
   {
      /* bad news if can't find point punt, unless 31X Device, it's ok not to find a point for them.  */
      if((DeviceRecord->DeviceType != TYPEMCT318) && (DeviceRecord->DeviceType != TYPEMCT360) && (DeviceRecord->DeviceType != TYPEMCT370))
      {
         return(!NORMAL);
      }
   }

   if(!(ComReturn))
   {
      /* verify we heard back from the correct device (only if we heard it)

         Note:  The returned address from the device is only the lower 13 bits,
                which means we would not have to mask of the Dst address, but for some
                reason when the reading is queued into the CCU711 we get the whole address
                returned.  So by comparing only 13 bit for both it will not break in
                either case.
      */
      if((DeviceRecord->Address & 0x1fff) != (DSt->Address & 0x1fff))
      {
         /* Address did not match so its a com error */
         ComReturn = WRONGADDRESS;
         printf ("Wrong Address:  %0.20s Rec. Address: %d \n", DeviceRecord->DeviceName, DSt->Address);
      }
      else if((DeviceRecord->DeviceType == TYPEMCT360 || DeviceRecord->DeviceType == TYPEMCT370) && DeviceRecord->SecurityLevel == 1)
      {
         /* this indicates that the data we have back is the
            Alpha Meter Demand Data and we need to make sure
            that the data buffer is good (if each byte is the
            same and not 0 its bad).
         */
         AlphaMeterData = TRUE;
         if(DSt->Message[1] != 0)
         {
            /* check to make sure that all values are not the same */
            for(x = 2; x < DSt->Length; x++)
            {
               if(DSt->Message[x - 1] != DSt->Message[x])
               {
                  /* values are different buffer is good */
                  break;
               }
            }

            if(x == DSt->Length)
            {
               /* went through the whole loop and all is was the same */
               ComReturn = ALPHABUFFERERROR;
            }
         }
      }
   }

   /* update performace stats for device and route */
   if(DeviceRecord->DeviceType == TYPEMCT318   || DeviceRecord->DeviceType == TYPEMCT360 || DeviceRecord->DeviceType == TYPEMCT370)
   {
      /* its a 31x type of device see if the Status point cause the scan */
      if(DeviceRecord->ScanRate < 60L || DeviceRecord->ScanRate == NEVER_SCAN_TIME)
      {
         /* no scan rate for Values so Status point cause the Scan
            and it has already updated the performance */
         UpdatePerfData = FALSE;
      }
   }

   memcpy (DevicePerform.DeviceName, DeviceRecord->DeviceName, sizeof (DeviceRecord->DeviceName));

   DevicePerform.Error = ComReturn;

   if(UpdatePerfData == TRUE)
   {
      /* this does not get updated for 318, 360, 370 when the status
          point triggered the Scan */
      DevicePerfUpdate (&DevicePerform, &ErrorRecord);

      /* Update the route statistics */
      if(!(GetRoute (DeviceRecord->RouteName, ScanRecord.RetryCount, RoutePerform.RouteName)))
      {
         RoutePerform.Type = ErrorRecord.Type;
         RoutePerfUpdate (&RoutePerform);
      }
   }


   UCTFTime (&TimeB);

   /* check for communication failure */
   if(ComReturn)
   {
      memcpy (ComErrorRecord.DeviceName, DeviceRecord->DeviceName, sizeof (DeviceRecord->DeviceName));
      memcpy (ComErrorRecord.RouteName, RoutePerform.RouteName, sizeof (ComErrorRecord.RouteName));

      ComErrorRecord.Error = ComReturn;
      ComErrorRecord.TimeStamp = TimeB.time;

      if(TimeB.dstflag)
      {
         ComErrorRecord.StatusFlag = DSTACTIVE;
      }
      else
      {
         ComErrorRecord.StatusFlag = 0;
      }

      /* Update the comm error database and future log flag here */
      ComErrorLogAdd (&ComErrorRecord, &ErrorRecord, FALSE);

      /* get the next route */
      if(DeviceRecord->Status & INHIBITED ||
         GetNextRoute (DeviceRecord->RouteName, &ScanRecord.RetryCount, RouteName) ||
         RouteName[0] == ' ')
      {
         /* we are done */
         InsertPlugData (DeviceRecord);
      }
      else
      {
         /* Put a lock on this record  but save the count because lock reloads the record */
         SaveCount = ScanRecord.RetryCount;
         ScanPointLock (&ScanRecord);
         ScanRecord.RetryCount = SaveCount;  /* lock reloads the record */

         /* queue up the request to porter */
         rc = RequestDLCValue (DeviceRecord, &ScanRecord);

         if(rc == REQUEUE_DEVICE)
         {
            /* need to reque the point */
            ScanRecord.NextScan = TimeB.time + 1L;
            ScanRecord.QueueStat = REQUEUE_POINT;
         }
         else if(rc == FAILED_NO_MORE)
         {
            ScanPointUnLock (&ScanRecord);
            InsertPlugData (DeviceRecord);
            return(NORMAL);
         }
         ScanPointFastUpdate (&ScanRecord);
      }

      return(NORMAL);
   }

   /* try to find the first point if not reading multiple points */
   if((DeviceRecord->DeviceType != TYPEDCT501) &&
      (DeviceRecord->DeviceType != TYPEMCT318) &&
      (DeviceRecord->DeviceType != TYPEMCT360) &&
      (DeviceRecord->DeviceType != TYPEMCT370))
   {

      memcpy (PointRecord.DeviceName, DeviceRecord->DeviceName, sizeof (DeviceRecord->DeviceName));

      if(DeviceRecord->ScanStatus & METERREAD)
      {
         /* this means we are getting accumulator point */
         PointRecord.PointType = ACCUMULATORPOINT;
      }
      else
      {
         PointRecord.PointType = DEMANDACCUMPOINT;
      }
      PointRecord.PointOffset = 1;


      if(PointgetOffset (&PointRecord))
      {
         /* big problem if point is not found */
         return(NORMAL);
      }
   }

   /* reset the starting values */
   RecentValue = 0L;
   PreviousValue = 0L;

   /* use a temp variable for they to handle mix ram maps */
   TempDevType = DeviceRecord->DeviceType;
   if(DeviceRecord->DeviceType == TYPEMCT213 || DeviceRecord->DeviceType == TYPEMCT260)
   {
      /* check the s-spec for these device types */
      if(DeviceRecord->SSpec == 74)
      {
         /* same s-spec as a mct224 fake it out */
         TempDevType = TYPEMCT224;
      }
   }
   else if(DeviceRecord->ScanStatus & METERREAD)
   {
      /* if we read the accumulator treat the device as an MCT-226 */
      TempDevType = TYPEMCT224;
   }


   /* process data by device type */
   switch(TempDevType)
   {
   case TYPELMT2:
   case TYPEMCT212:
   case TYPEMCT224:
   case TYPEMCT226:
      /* this is the non-feedback register
         which is 2 accumulators 5-min apart
         and must be subtracted from each other */

      /* this is a 24 bit value from the MCT which is previous 5-min */
      PreviousValue = MAKEULONG (MAKEUSHORT (DSt->Message[2], DSt->Message[1]), (USHORT)(DSt->Message[0]));

      /* this is a 24 bit value from the MCT which is current 5-min */
      RecentValue = MAKEULONG (MAKEUSHORT (DSt->Message[5], DSt->Message[4]), (USHORT)(DSt->Message[3]));


      if(DeviceRecord->ScanStatus & METERREAD)
      {
         /* process the accumulator point first */
         Value = (FLOAT)RecentValue;
      }
      else
      {
         /* calculate the demand for a fixed five minutes */
         if(RecentValue < PreviousValue)
         {
            /* roll over has occured adjust if pi rollover */
            RecentValue += 10000000;
         }

         /*  set the current pulse value */
         Value = (FLOAT)((RecentValue - PreviousValue) * 12L);
      }
      break;

   case TYPEDCT501:
      /* process 4 analog channels for DCT's,
         we always read all four */
      for(Point = 1; Point < 5; Point++)
      {
         /* look for each analog point */
         memcpy (PointRecord.DeviceName,
                 DeviceRecord->DeviceName,
                 sizeof (DeviceRecord->DeviceName));

         PointRecord.PointType = ANALOGPOINT;
         PointRecord.PointOffset = Point;
         if(!(PointgetOffset (&PointRecord)))
         {
            /* found this point process data */

            memcpy (ScanRecord.DeviceName,  PointRecord.DeviceName, sizeof (PointRecord.DeviceName));
            memcpy (ScanRecord.PointName, PointRecord.PointName, sizeof (PointRecord.PointName));

            if(!(ScanPointgetEqual (&ScanRecord)))
            {
               /* Straighten out the next scan time */
               ScanPointLock (&ScanRecord);

               ScanRecord.RetryCount = 0;
               ScanRecord.QueueStat = POINT_NOT_QUEUED;
               ScanRecord.QueueTime = 0L;

               ScanRecord.NextScan = NextScan (TimeB.time, DeviceRecord->ScanRate, DeviceRecord->DeviceType);

               ScanPointFastUpdate (&ScanRecord);

               /* now convert the data */
               x = (Point * 2) - 1;
               Pulses = MAKEUSHORT (DSt->Message[x], DSt->Message[x - 1]);

               /* check data for any error codes */
               if(Pulses >= DEV_CODE_OVERFLOW)
               {
                  /* its an error code */
                  if(Pulses == DEV_CODE_OVERFLOW)
                  {
                     InValidValue = DATAOVERFLOW;
                  }
                  else if(Pulses == DATADEVICEFILLER)
                  {
                     InValidValue = DEV_CODE_FILLER;
                  }
                  else
                  {
                     InValidValue = UNKNOWN_INVALID;
                  }
               }
               else
               {
                  /* good data */
                  Value = (FLOAT) (Pulses) * PointRecord.Multiplier + PointRecord.Offset;
               }

               PointLock (&PointRecord);

               /* do the big data quality check */
               CheckDataStateQuality (DeviceRecord,
                                      &PointRecord,
                                      &Value,
                                      FALSE,
                                      TimeB.time,
                                      TimeB.dstflag,
                                      InValidValue,
                                      FALSE);

               /* Load up the common parts of the DRP message */
               memcpy (DRPValue.DeviceName,
                       PointRecord.DeviceName,
                       STANDNAMLEN);

               memcpy (DRPValue.PointName,
                       PointRecord.PointName,
                       STANDNAMLEN);

               DRPValue.TimeStamp = TimeB.time;

               DRPValue.Type = DRPTYPEVALUE;

               /* Send the point to drp */
               DRPValue.Value = PointRecord.CurrentValue;
               DRPValue.Quality = PointRecord.CurrentQuality;
               DRPValue.AlarmState = PointRecord.AlarmStatus;

               SendDRPPoint (&DRPValue);
            }
         }
      }

      return(NORMAL);

   case TYPEMCT318:
   case TYPEMCT360:
   case TYPEMCT370:
      /* process 3 KYZ channels for MCT3XX function read,
         we always read all three + all the status points as well which are processed
         separately in ProcessDLCStatus */


      for(Point = 1; Point < 4; Point++)
      {
         /* look for each KYZ point */
         memcpy (PointRecord.DeviceName,
                 DeviceRecord->DeviceName,
                 sizeof (DeviceRecord->DeviceName));

         if(AlphaMeterData)
         {
            /* Alpha Meter Points and Analog */
            PointRecord.PointType = ANALOGPOINT;
            PointRecord.PointOffset = Point * 10;
         }
         else
         {
            PointRecord.PointType = DEMANDACCUMPOINT;
            PointRecord.PointOffset = Point;
         }

         if(!(PointgetOffset (&PointRecord)))
         {
            /* found this point process data */

            memcpy (ScanRecord.DeviceName,
                    PointRecord.DeviceName,
                    sizeof (PointRecord.DeviceName));

            memcpy (ScanRecord.PointName,
                    PointRecord.PointName,
                    sizeof (PointRecord.PointName));

            if(!(ScanPointgetEqual (&ScanRecord)))
            {
               /* Straighten out the next scan time */
               if(DeviceRecord->ScanRate > 59L && DeviceRecord->ScanRate != NEVER_SCAN_TIME)
               {
                  /* we only do this if the is a device scan rate
                   * otherwise the status point is causing us to
                   * scan.
                   */
                  ScanPointLock (&ScanRecord);

                  ScanRecord.RetryCount = 0;
                  ScanRecord.QueueStat = POINT_NOT_QUEUED;
                  ScanRecord.QueueTime = 0L;

                  ScanRecord.NextScan = NextScan (TimeB.time,
                                                  DeviceRecord->ScanRate,
                                                  DeviceRecord->DeviceType);

                  ScanPointFastUpdate (&ScanRecord);
               }
            } /* end of scanpointgetequal */

            /* now convert the data; note that the data is stored thus:
               byte 1,2 is first demand point, byte 3,4 is second, byte 5,6 is third
               byte 0 is the 8 status points which are processed elsewhere */

            /* Alpha data is BCD so we convertion is different */
            if(AlphaMeterData)
            {
               /* convert from BCD */
               if(Point == 2)
               {
                  /* 5th byte will be second points start of data */
                  ByteOffset = 5;
               }
               else
               {
                  ByteOffset = 1;
               }

               Value = 0;
               for(x = 0; x < 3; x++)
               {

                  Value *= 10.0;

                  /* convert the upper bcd nibble */
                  Value += (FLOAT) ((DSt->Message[ByteOffset + x] >> 4) & 0x0f);
                  Value *= 10.0;

                  /* convert the lower bcd nibble */
                  Value += (FLOAT) (DSt->Message[ByteOffset + x] & 0x0f);
               }

               /* We use 3 Fixed place Decimals at this time */
               Value *= (FLOAT).001;

               /* Apply the multiplier - will normally be set to 1 */
               Value *= PointRecord.Multiplier;
            }
            else
            {
               x = (Point * 2) - 1;
               Pulses = MAKEUSHORT (DSt->Message[x+1], DSt->Message[x]);

               /* check data for any error codes */
               if(DSt->Message[x] & 0xc0)
               {
                  /* alarm bits for power fail ect. */
                  InValidValue = DATAPOWERFAIL;
                  Value = 0;
               }
               else if(Pulses >= DEV_CODE_OVERFLOW)
               {
                  /* its an error code */
                  if(Pulses == DEV_CODE_OVERFLOW)
                  {
                     InValidValue = DATAOVERFLOW;
                  }
                  else if(Pulses == DATADEVICEFILLER)
                  {
                     InValidValue = DEV_CODE_FILLER;
                  }
                  else
                  {
                     InValidValue = UNKNOWN_INVALID;
                  }

                  Value = 0;
               }
               else if(DeviceRecord->LSInterval != 5 &&
                       DeviceRecord->LSInterval > 0 &&
                       DeviceRecord->LSInterval < 61)
               {
                  HourMult = 60 / DeviceRecord->LSInterval;


                  /* Amps A/D filter for PECO large readings
                     Note the value is max pulse in one hour so we
                     divide it down to our interval */
                  if(PointRecord.CalculatedUOM == UOM_AMPS &&
                     Pulses >= (PECO_MAX_AMP_PULSES / HourMult))
                  {
                     /* this is a little above the max pulse rate for PECO's
                        analog to pulse Amp transducers so use the last read */
                     Value = PointRecord.CurrentValue;
                  }
                  else
                  {
                     /* LSInterval is used for settible demand interval */
                     Value = (FLOAT) (Pulses) * (PointRecord.Multiplier + PointRecord.Offset) * (FLOAT)HourMult;
                  }
               }
               else
               {
                  HourMult = 12;  /* 5 minute interval */

                  /* Amps A/D filter for PECO large readings
                     Note the value is max pulse in one hour so we
                     divide it down to our 5 minute interval */

                  if(PointRecord.CalculatedUOM == UOM_AMPS &&
                     Pulses >= (PECO_MAX_AMP_PULSES / HourMult))
                  {
                     /* this is a little above the max pulse rate for PECO's
                        analog to pulse Amp transducers so use the last read */
                     Value = PointRecord.CurrentValue;

                     /* Build a log message */
                     memset (&LogMessage, ' ', sizeof(LogMessage));
                     LogMessage.TimeStamp = LongTime ();
                     LogMessage.StatusFlag = 0;
                     if(DSTFlag())
                     {
                        LogMessage.StatusFlag = DSTACTIVE;
                     }

                     memcpy (LogMessage.DeviceName,
                             PointRecord.DeviceName,
                             STANDNAMLEN);

                     memcpy (LogMessage.PointName,
                             PointRecord.PointName,
                             STANDNAMLEN);

                     sprintf(LogMessage.LogMessage1,
                             "Amp OVRF= %ld",
                             Pulses);

                     LogMessage.EventType = LOGMESSAGE;
                     LogMessage.Originator = LOGSYSTEM;

                     /* Thats it so send it to elogger */
                     LogEvent(&LogMessage);

                  }
                  else
                  {
                     /* good data, the 12 is because interval is assumed fixed at 5 minutes */
                     Value = (FLOAT) (Pulses) * (PointRecord.Multiplier + PointRecord.Offset) * HourMult;
                  }
               }

            }    /* endif AlphaData Check */


            /* do the big data quality check */
            PointLock (&PointRecord);
            CheckDataStateQuality (DeviceRecord,
                                   &PointRecord,
                                   &Value,
                                   FALSE,
                                   TimeB.time,
                                   TimeB.dstflag,
                                   InValidValue,
                                   FALSE);

            /* Load up the common parts of the DRP message */
            memcpy (DRPValue.DeviceName,
                    PointRecord.DeviceName,
                    STANDNAMLEN);

            memcpy (DRPValue.PointName,
                    PointRecord.PointName,
                    STANDNAMLEN);

            DRPValue.TimeStamp = TimeB.time;

            DRPValue.Type = DRPTYPEVALUE;

            /* Send the point to drp */
            DRPValue.Value = PointRecord.CurrentValue;
            DRPValue.Quality = PointRecord.CurrentQuality;
            DRPValue.AlarmState = PointRecord.AlarmStatus;

            SendDRPPoint (&DRPValue);

         }   /* endif:  PointgetOffset */

         if(AlphaMeterData && (Point > 1))
         {
            /* Only support 2 points of data for Alpha at this time */
            break;
         }


      } /* end of for-next for point offsets */

      return(NORMAL);

   case TYPEMCT210:
   case TYPEMCT213:
   case TYPEMCT310:
      /* feedback register fixed at 5 minutes */
      if(DSt->Message[0] & 0xc0)
      {
         /* alarm bits for power fail ect. */
         InValidValue = DATAPOWERFAIL;
      }
      else
      {
         /* calculate the pulses */
         Pulses = MAKEUSHORT (DSt->Message[1], DSt->Message[0]);

         /* check data for any error codes */
         if(Pulses >= DEV_CODE_OVERFLOW)
         {
            /* its an error code */
            if(Pulses == DEV_CODE_OVERFLOW)
            {
               InValidValue = DATAOVERFLOW;
            }
            else if(Pulses == DATADEVICEFILLER)
            {
               InValidValue = DEV_CODE_FILLER;
            }
            else
            {
               InValidValue = UNKNOWN_INVALID;
            }
         }
         else
         {
            /* good data */
            if(TempDevType == TYPEMCT310
               && DeviceRecord->LSInterval > 1
               && DeviceRecord->LSInterval < 61)
            {
               /* For MCT310's the Demand Interval is set in DB 1-60 minutes */
               Value = (FLOAT)((FLOAT)(Pulses) * (FLOAT)(60 / DeviceRecord->LSInterval));
            }
            else
            {
               /* assume fixed demand interval of 5 minutes */
               Value = (FLOAT)((FLOAT)(Pulses) * 12.0);
            }
         }
      }

      break;

   case TYPEMCT240:
   case TYPEMCT242:
   case TYPEMCT248:
   case TYPEMCT250:
      /* feedback register fixed at 5 minutes */
      if(DSt->Message[0] & 0xc0)
      {
         /* alarm bits for power fail ect. */
         InValidValue = DATAPOWERFAIL;
      }
      else
      {
         /* calculate the pulses */
         Pulses = MAKEUSHORT (DSt->Message[1], DSt->Message[0]);

         /* check data for any error codes */
         if(Pulses >= DEV_CODE_OVERFLOW)
         {
            /* its an error code */
            if(Pulses == DEV_CODE_OVERFLOW)
            {
               InValidValue = DATAOVERFLOW;
            }
            else if(Pulses == DATADEVICEFILLER)
            {
               InValidValue = DEV_CODE_FILLER;
            }
            else
            {
               InValidValue = UNKNOWN_INVALID;
            }
         }
         else
         {
            /* good data */
            if(DeviceRecord->SSpec == 121 || DeviceRecord->SSpec == 111)
            {
               /* setable feedback interval and 3 byte is interval */
               TempValue = (FLOAT) MAKEUSHORT (DSt->Message[2], 0);

               if(TempValue > 0.0)
               {
                  Value = (FLOAT)((FLOAT) Pulses * (60.0 / (TempValue / 4.0)));
               }
               else
               {
                  Value = (FLOAT)((FLOAT) Pulses * 12.0);
               }
            }
            else
            {
               /* type is fixed at 5 minutes */
               Value = (FLOAT)((FLOAT) Pulses * 12.0);
            }
         }
      }

      break;

   }   /* end of device type switch */

   /* must finish calculation if not a DCT */
   /* handle each uom */
   switch(PointRecord.CalculatedUOM)
   {
   case UOM_MW:
   case UOM_KW:
   case UOM_KQ:
   case UOM_KVAR:
   case UOM_AMPS:
   case UOM_PFACTOR:
   case UOM_TEMPF:
   case UOM_TEMPC:
   case UOM_WATER_CF:
   case UOM_GAS_CF:
   case UOM_FEET:
   case UOM_GAL_PM:
      /* calulate a demand value for 5 minutes */
      Value = (Value * PointRecord.Multiplier) + PointRecord.Offset;
      break;

   case UOM_VOLTS:
      /* volts must see if base is V2H */
      if(PointRecord.MeasuredUOM == UOM_V2H)
      {
         /* the base value is from V2H */
         Value = (FLOAT) sqrt (fabs ((DOUBLE) (Value * PointRecord.Multiplier)));
      }
      else
      {
         /* its must be a pulser board */
         Value = (Value * PointRecord.Multiplier) + PointRecord.Offset;
      }

      break;

   default:
      /* only need the multiplier here */
      Value *= PointRecord.Multiplier;
   }

   /* now update the data for non-DCT  point */
   memcpy (ScanRecord.DeviceName,
           PointRecord.DeviceName,
           sizeof (PointRecord.DeviceName));

   memcpy (ScanRecord.PointName,
           PointRecord.PointName,
           sizeof (PointRecord.PointName));

   if(!(ScanPointgetEqual (&ScanRecord)))
   {
      /* found the scan point from the table */
      ScanPointLock (&ScanRecord);

      ScanRecord.RetryCount = 0;
      ScanRecord.QueueStat = POINT_NOT_QUEUED;
      ScanRecord.QueueTime = 0L;

      ScanRecord.NextScan = NextScan (TimeB.time,
                                      DeviceRecord->ScanRate,
                                      DeviceRecord->DeviceType);

      ScanPointFastUpdate (&ScanRecord);
      PointLock (&PointRecord);

      /* do the big data quality check */
      CheckDataStateQuality (DeviceRecord,
                             &PointRecord,
                             &Value,
                             FALSE,
                             TimeB.time,
                             TimeB.dstflag,
                             InValidValue,
                             LogFlag);

      /* Load up the common parts of the DRP message */
      memcpy (DRPValue.DeviceName,
              PointRecord.DeviceName,
              STANDNAMLEN);

      memcpy (DRPValue.PointName,
              PointRecord.PointName,
              STANDNAMLEN);

      DRPValue.TimeStamp = TimeB.time;

      DRPValue.Type = DRPTYPEVALUE;

      /* Send the point to drp */
      DRPValue.Value = PointRecord.CurrentValue;
      DRPValue.Quality = PointRecord.CurrentQuality;
      DRPValue.AlarmState = PointRecord.AlarmStatus;

      SendDRPPoint (&DRPValue);
   }

   return(NORMAL);
}


/* this routine inserts the route's port#, CCU#, and Feeder# for a DLC B-Word */
DLLEXPORT INT BPreambleLoad (ROUTE *RouteRecord, BSTRUCT *BSt)
{
   REMOTE RemoteRecord;

   /* insert proper values */
   BSt->Feeder   = RouteRecord->BusNum - 1;      /* Bus is set up 1-8 by user but sent 0-7 */
   BSt->Amp      = RouteRecord->AmpNum - 1;         /* Amp Cards  0-1 */
   BSt->RepFixed = RouteRecord->FixBit;        /* insert fixed bits */
   BSt->RepVar   = RouteRecord->VarBit;          /* insert variable bits */
   BSt->Stages   = RouteRecord->getStages();      /* set the number of stages */

   /* get the port info */
   memcpy (RemoteRecord.RemoteName, RouteRecord->RemoteName, sizeof (RouteRecord->RemoteName));

   if(RemotegetEqual (&RemoteRecord))
   {
      /* remote not found */
      return(INVALID_REMOTE);
   }

   BSt->Port   = RemoteRecord.Port;
   BSt->Remote = RemoteRecord.Remote;

   return(RemoteRecord.Type);
}


/* handles failed communication which needs to be plugged or updated */
DLLEXPORT INT InsertPlugData (CtiDeviceBase *DeviceRecord)
{

#if 0
   SCANPOINT ScanRecord;
   CTIPOINT PointRecord;
   ULONG rc;
   USHORT Quality;
   DRPVALUE DRPValue;
   FLOAT Value = 0;

   int PointMaxForType = 1;   /* most MCT's only have 1 Value Point */
   int myPointOffset = 1;
   int Count;

   struct timeb TimeB;

   UCTFTime (&TimeB);

   if(DeviceRecord->Status & INHIBITED)
   {
      /* it was not a plug but is inhibited */
      Quality = FALSE;
   }
   else
   {
      Quality = PLUGGED;
   }

   if(DeviceRecord->DeviceType == TYPEDCT501)
   {
      PointRecord.PointType = ANALOGPOINT;
      PointMaxForType = 4;
   }
   else if(DeviceRecord->DeviceType == TYPEMCT360 ||
           DeviceRecord->DeviceType == TYPEMCT370)
   {
      if(DeviceRecord->SecurityLevel == 1)
      {
         PointRecord.PointType = ANALOGPOINT;
         PointMaxForType = 3;
         myPointOffset = 10;

      }
      else
      {
         PointMaxForType = 3;
         PointRecord.PointType = DEMANDACCUMPOINT;
      }

   }
   else if(DeviceRecord->DeviceType == TYPEMCT318)
   {
      PointMaxForType = 3;
      PointRecord.PointType = DEMANDACCUMPOINT;
   }
   else
   {
      PointRecord.PointType = DEMANDACCUMPOINT;
   }

   if(DeviceRecord->ScanStatus & METERREAD &&
      PointRecord.PointType == DEMANDACCUMPOINT)
   {
      /* this one is reading an accumulator points - thing like run-time, etc. */
      PointRecord.PointType = ACCUMULATORPOINT;
   }


   /* process 4 analog channels for DCT's we always read all four */
   for(Count = 1; Count <= PointMaxForType; Count++)
   {
      /* look for each analog point */
      memcpy (PointRecord.DeviceName,
              DeviceRecord->DeviceName,
              sizeof (DeviceRecord->DeviceName));

      /* try to get the Point Offset (number) */
      PointRecord.PointOffset = myPointOffset;

#ifdef DEBUG2
      UCTFTime (&TimeB);
      printf ("%0.8s Looking for device to plug: %0.20s Type= %hd Offset= %hd \n",
              UCTAscTime (LongTime (), DSTFlag ()) + 11,
              PointRecord.DeviceName,
              PointRecord.PointType,
              PointRecord.PointOffset);
#endif

      if(!(PointgetOffset (&PointRecord)))
      {
         /* found this point process data */

         memcpy (ScanRecord.DeviceName,
                 PointRecord.DeviceName,
                 sizeof (PointRecord.DeviceName));

         memcpy (ScanRecord.PointName,
                 PointRecord.PointName,
                 sizeof (PointRecord.PointName));

         if(!(ScanPointgetEqual (&ScanRecord)))
         {
            /* found the scan point from the table */
            ScanPointLock (&ScanRecord);
            if(DeviceRecord->ScanRate < 60L || DeviceRecord->ScanRate == NEVER_SCAN_TIME)
            {
               /* we do not want to scan again if there is
                  not scan rate
                */
               ScanRecord.NextScan = NEVER_SCAN_TIME;
            }
            else
            {
               ScanRecord.NextScan = NextScan (TimeB.time,
                                               DeviceRecord->ScanRate,
                                               DeviceRecord->DeviceType);
            }


            ScanRecord.RetryCount = 0;
            ScanRecord.QueueStat = POINT_NOT_QUEUED;
            ScanRecord.QueueTime = 0L;


            rc = ScanPointFastUpdate (&ScanRecord);

            /* lock the point and update the value */
            PointLock (&PointRecord);

            /* do the big data quality check */
            CheckDataStateQuality (DeviceRecord,
                                   &PointRecord,
                                   &Value,
                                   Quality,
                                   TimeB.time,
                                   TimeB.dstflag,
                                   FALSE,
                                   LogFlag);

            /* Load up the common parts of the DRP message */
            memcpy (DRPValue.DeviceName,
                    PointRecord.DeviceName,
                    STANDNAMLEN);

            memcpy (DRPValue.PointName,
                    PointRecord.PointName,
                    STANDNAMLEN);

            DRPValue.TimeStamp = TimeB.time;

            DRPValue.Type = DRPTYPEVALUE;

            /* Send the point to drp */
            DRPValue.Value = PointRecord.CurrentValue;
            DRPValue.Quality = PointRecord.CurrentQuality;
            DRPValue.AlarmState = PointRecord.AlarmStatus;

            SendDRPPoint (&DRPValue);
         }

      }

      /* check if we are scanning an Alpha Meter */
      if( (DeviceRecord->DeviceType == TYPEMCT360     ||
           DeviceRecord->DeviceType == TYPEMCT370)   &&
          DeviceRecord->SecurityLevel == 1)
      {
         /* jump to next point which is Kvar or Kva */
         myPointOffset += 10;
      }
      else
      {
         /* next point number */
         ++myPointOffset;
      }


   }  /* end FOR Loop */
#endif

   return(NORMAL);
}



/* this function initalizes a scan point and returns the point type */
DLLEXPORT INT InitOneScanPoint (SCANPOINT *ScanRecord, DEVICE *DeviceRecord)
{
   CTIPOINT PointRecord;
   struct timeb TimeB;

   UCTFTime (&TimeB);

   /* find the scan point in the point database */
   memcpy (PointRecord.DeviceName, ScanRecord->DeviceName, sizeof (ScanRecord->DeviceName));
   memcpy (PointRecord.PointName, ScanRecord->PointName, sizeof (ScanRecord->PointName));

   if(!(PointgetEqual (&PointRecord)))
   {
      /* found the point in point table now grab device type for setting next scan */
      if(PointRecord.PointType != PSEUDOVALUEPOINT)
      {
         /* get the device from the device database */
         memcpy (DeviceRecord->DeviceName, PointRecord.DeviceName, sizeof (PointRecord.DeviceName));

         if(!(DevicegetEqual (DeviceRecord)))
         {
            /* Found device now set scan rate */
            switch(DeviceRecord->DeviceType)
            {
            case TYPELMT2:
            case TYPEMCT212:
            case TYPEMCT224:
            case TYPEMCT226:
            case TYPEDCT501:
            case TYPEMCT210:
            case TYPEMCT213:
            case TYPEMCT260:
            case TYPEMCT310:
            case TYPEMCT240:
            case TYPEMCT242:
            case TYPEMCT248:
            case TYPEMCT250:
            case TYPEMCT318:
            case TYPEMCT360:
            case TYPEMCT370:
               ScanRecord->RetryCount = 0;
               ScanRecord->QueueStat = POINT_NOT_QUEUED;
               ScanRecord->QueueTime = 0L;
               if(DeviceRecord->ScanRate < 60L || DeviceRecord->ScanRate == NEVER_SCAN_TIME)
               {
                  /* do allow scan rates less than 60 seconds for DLC */
                  ScanRecord->NextScan = NEVER_SCAN_TIME;
               }
               else
               {
                  ScanRecord->NextScan = NextScan (TimeB.time,  DeviceRecord->ScanRate, DeviceRecord->DeviceType);
               }
               break;

            default:
               /* not a dlc device */
               ScanRecord->NextScan = NEVER_SCAN_TIME;
            }
         }
         else
         {
            /* device is not found */
            ScanRecord->NextScan = NEVER_SCAN_TIME;
         }
      }
      else
      {
         /* its a pseudo so no need to scan */
         memcpy (DeviceRecord->DeviceName, PointRecord.DeviceName, sizeof (PointRecord.DeviceName));
         DevicegetEqual (DeviceRecord);
         ScanRecord->NextScan = NEVER_SCAN_TIME;
      }

      /* return the point type */
      return(PointRecord.PointType);
   }

   /* point not defined so no need to scan and return normal for point type which is no point type */
   ScanRecord->NextScan = NEVER_SCAN_TIME;
   return(NORMAL);
}


/* this function initalizes the one DLC status point */
DLLEXPORT INT InitOneStatusPoint (STATPOINT *StatusRecord)
{
   DEVICE DeviceRecord;
   CTIPOINT PointRecord;
   struct timeb TimeB;

   UCTFTime (&TimeB);

   /* find the status point in the point database */
   memcpy (PointRecord.DeviceName, StatusRecord->DeviceName, sizeof (StatusRecord->DeviceName));
   memcpy (PointRecord.PointName, StatusRecord->PointName, sizeof (StatusRecord->PointName));

   if(!(PointgetEqual (&PointRecord)))
   {
      /* found the point in point table now grab device type for setting next scan */
      if(PointRecord.PointType != PSEUDOVALUEPOINT)
      {
         /* get the device from the device database */
         memcpy (DeviceRecord.DeviceName, PointRecord.DeviceName, sizeof (PointRecord.DeviceName));

         if(!(DevicegetEqual (&DeviceRecord)))
         {
            /* Found device now set scan rate */
            switch(DeviceRecord.DeviceType)
            {
            case TYPELMT2:
            case TYPEMCT212:
            case TYPEMCT224:
            case TYPEMCT226:
            case TYPEDCT501:
            case TYPEMCT210:
            case TYPEMCT213:
            case TYPEMCT260:
            case TYPEMCT310:
            case TYPEMCT240:
            case TYPEMCT242:
            case TYPEMCT248:
            case TYPEMCT250:
            case TYPEMCT318:
            case TYPEMCT360:
            case TYPEMCT370:
            case TYPE_REPEATER800:
            case TYPE_REPEATER900:
               /* DLC device must set scan time for now */
               StatusRecord->RetryCount = 0;
               StatusRecord->QueueStat = POINT_NOT_QUEUED;
               StatusRecord->QueueTime = 0L;
               if(DeviceRecord.AccumScanRate < 60L || DeviceRecord.AccumScanRate == NEVER_SCAN_TIME)
               {
                  /* don't scan if less than 1 minute or its *** for scan rate */
                  StatusRecord->NextScan = NEVER_SCAN_TIME;
               }
               else
               {
                  StatusRecord->NextScan = NextScan (TimeB.time, DeviceRecord.AccumScanRate, 0);
               }
               break;

            default:
               /* not a dlc device */
               StatusRecord->NextScan = NEVER_SCAN_TIME;
            }
         }
         else
         {
            /* device is not found */
            StatusRecord->NextScan = NEVER_SCAN_TIME;
         }
      }
      else
      {
         /* its a pseudo so no need to scan */
         StatusRecord->NextScan = NEVER_SCAN_TIME;
      }
   }
   else
   {
      /* point no defined so no need to scan */
      StatusRecord->NextScan = NEVER_SCAN_TIME;
   }

   return(NORMAL);
}


/* this proc builds a request for porter to scan a DLC Analog or PA point(s) */
DLLEXPORT INT RequestDLCStatus (DEVICE *DeviceRecord, STATPOINT *StatusRecord)
{
   BSTRUCT BSt;
   ROUTE RouteRecord;
   ERRSTRUCT ErrorRecord;
   DEVICEPERF DevicePerform;
   ROUTEPERF RoutePerform;
   COMM_ERROR_LOG_STRUCT ComErrorRecord;
   struct timeb TimeB;
   BOOL IsFunctionRead = TRUE; /* flag used to tell queueing routine about a ftn read */

   if(StatusRecord->QueueStat == FORCED_DLC_SCAN)
   {
      /* set forced scan to high priority */
      BSt.Priority = PRIORITY_STATUS_FORCED;          /* set the point queue priority */
   }
   else
   {
      BSt.Priority = DLCStatusPriority;          /* set the point queue priority */
   }

   BSt.Retry = RETRY_STATUS;                /* set ccu retry count */
   BSt.Address = DeviceRecord->Address;    /* set device address */

   /* clear the sequence number only use when doing a forced scan */
   StatusRecord->SeqNumber = 0;

   /* set the request address and length */
   switch(DeviceRecord->DeviceType)
   {
   case TYPELMT2:
   case TYPEMCT212:
   case TYPEMCT224:
   case TYPEMCT226:
   case TYPEDCT501:
   case TYPEMCT240:
   case TYPEMCT242:
   case TYPEMCT250:
      /* this is the standard location for statuses info */
      BSt.Function = GENERAL_STATUS_ADDRESS;
      BSt.Length = GENERAL_STATUS_LENGTH;
      break;

   case TYPE_REPEATER800:
   case TYPE_REPEATER900:
      /* this is the just a verification test */
      BSt.Function = RPT_STATUS_ADDRESS;
      BSt.Length = RPT_STATUS_LENGTH;
      --BSt.Priority;          /* set the queue priority at one
                                less for repeaters */
      break;

   case TYPEMCT210:
      /* feedback register fixed at 5 minutes */
      BSt.Function = MCT210_STATUS_ADDRESS;
      BSt.Length = MCT210_STATUS_LENGTH;
      break;

   case TYPEMCT213:
      if(DeviceRecord->SSpec == 74)
      {
         /* isn't this nice, same device type but different RAM map. this is 22x map */
         BSt.Function = GENERAL_STATUS_ADDRESS;
         BSt.Length = GENERAL_STATUS_LENGTH;
      }
      else
      {
         /* mct-210 s-spec */
         BSt.Function = MCT210_STATUS_ADDRESS;
         BSt.Length = MCT210_STATUS_LENGTH;
      }
      break;

   case TYPEMCT310:
      BSt.Function = MCT310_STATUS_ADDRESS;
      BSt.Length = MCT310_STATUS_LENGTH;
      break;

   case TYPEMCT248:
      BSt.Function = MCT248_STATUS_ADDRESS;
      BSt.Length = MCT248_STATUS_LENGTH;
      break;

   case TYPEMCT318:
   case TYPEMCT360:
   case TYPEMCT370:
      /* Function read brings back all status and value points in a single read */
      BSt.Function = MCT3XX_FUNCTION_READ;
      BSt.Length = MCT3XX_FUNCREAD_LENGTH;
      IsFunctionRead = TRUE;
      break;

   default:
      /* default to general status address */
      BSt.Function = GENERAL_STATUS_ADDRESS;
      BSt.Length = GENERAL_STATUS_LENGTH;
   }

   /* get the Route */
   if(GetRoute (DeviceRecord->RouteName,
                StatusRecord->RetryCount,
                RouteRecord.RouteName) ||
      RoutegetEqual (&RouteRecord))
   {
      /* could not get the route */
      return(REQUEUE_DEVICE);
   }

   /* Save the device name and type of data */
   memcpy (BSt.DeviceName,
           DeviceRecord->DeviceName,
           sizeof (DeviceRecord->DeviceName));

   memcpy (BSt.PointName,
           StatusRecord->PointName,
           sizeof (StatusRecord->PointName));

   /* set the request address and length */
   switch(BPreambleLoad (&RouteRecord,
                         &BSt))
   {
   case TYPE_CCU700:
   case TYPE_CCU710:
   case TYPE_CCU711:
      /* it's a ccu so put on the queue */
      UCTFTime (&TimeB);
      if(IsFunctionRead)
      {
         /* call special routine to do a ftn read */
         DevicePerform.Error = QueueFunctionRead (&BSt, QDLCStatus);
      }
      else
      {
         if(QDLCStatus)
         {
            /* queue in the CCU-711 */
            DevicePerform.Error = nb2execsq (&BSt);
         }
         else
         {
            DevicePerform.Error = nb2execs (&BSt);
         }
      }

      if(DevicePerform.Error)
      {
         /* Queueing Error update performance */
         memcpy (DevicePerform.DeviceName,
                 DeviceRecord->DeviceName,
                 sizeof (DeviceRecord->DeviceName));

         DevicePerfUpdate (&DevicePerform,
                           &ErrorRecord);

         memcpy (RoutePerform.RouteName,
                 RouteRecord.RouteName,
                 sizeof (RouteRecord.RouteName));

         RoutePerform.Type = ErrorRecord.Type;
         RoutePerfUpdate (&RoutePerform);
         memcpy (ComErrorRecord.DeviceName,
                 DeviceRecord->DeviceName,
                 sizeof (DeviceRecord->DeviceName));

         memcpy (ComErrorRecord.RouteName,
                 RouteRecord.RouteName,
                 sizeof (ComErrorRecord.RouteName));

         ComErrorRecord.TimeStamp = TimeB.time;
         if(TimeB.dstflag)
            ComErrorRecord.StatusFlag = DSTACTIVE;
         else
            ComErrorRecord.StatusFlag = 0;

         /* Update the comm error database */
         ComErrorLogAdd (&ComErrorRecord,
                         &ErrorRecord,
                         FALSE);


         GetNextRoute (DeviceRecord->RouteName,
                       &StatusRecord->RetryCount,
                       RouteRecord.RouteName);

         if(RouteRecord.RouteName[0] == ' ')
         {
            /* no more routes so quit wait til next interval */
            return(FAILED_NO_MORE);
         }

         return(REQUEUE_DEVICE);
      }
      else
      {
         /* its queued ok */
         StatusRecord->NextScan = TimeB.time + TARDY_TIME_DELAY;
         if(StatusRecord->QueueStat == FORCED_DLC_SCAN)
         {
            /* on a forced scan we will use sequence number to
               throw away a previously queued message if it comes
               in before the forced scan we are sending now */
            StatusRecord->SeqNumber = BSt.Sequence;
         }
      }

      break;

   default:
      /* remote was not found or is not a ccu */
      GetNextRoute (DeviceRecord->RouteName,
                    &StatusRecord->RetryCount,
                    RouteRecord.RouteName);

      if(RouteRecord.RouteName[0] == ' ')
      {
         /* no more routes so quit wait til next interval */
         return(FAILED_NO_MORE);
      }

      return(REQUEUE_DEVICE);
   }

   /* made it here all is well */
   return(NORMAL);
}


/* this proc interpets a status value(s) for a device/point */
DLLEXPORT FLOAT TranslateStatusValue (USHORT DeviceType,
                                      CTIPOINT *PointRecord,
                                      PUSHORT DataValue)
{
   /* key off the point offset */
   switch(PointRecord->PointOffset)
   {
   case 1:
      /* first physical point */
      switch(DeviceType)
      {
      case TYPELMT2:
      case TYPEDCT501:
      case TYPEMCT250:
         /* only this point is a legal 3-state 2 of the types */
         if(PointRecord->PointType == THREESTATEPOINT)
         {
            /* its a three state status */
            if(DataValue[4] & STATUS1_BIT && DataValue[4] & STATUS2_BIT)
            {
               return((FLOAT) STATEFOUR);
            }
            else if(DataValue[4] & STATUS1_BIT)
            {
               return((FLOAT) CLOSED);
            }
            else if(DataValue[4] & STATUS2_BIT)
            {
               return((FLOAT) OPENED);
            }
            else
            {
               return((FLOAT) INDETERMINATE);
            }
         }
         else
         {
            if(DataValue[4] & STATUS1_BIT)
            {
               return((FLOAT) CLOSED);
            }
            else
            {
               return((FLOAT) OPENED);
            }
         }
      case TYPEMCT248:
         if(DataValue[7] == 66)
         {
            return((FLOAT) CLOSED);
         }

         if(DataValue[7] == 65)
         {
            return((FLOAT) OPENED);
         }

         return((FLOAT) INVALID);

      case TYPEMCT318:
      case TYPEMCT360:
      case TYPEMCT370:
         if(DataValue[0] & STATUS1_BIT_MCT3XX)
         {
            return((FLOAT) CLOSED);
         }
         else
         {
            return((FLOAT) OPENED);
         }

      default:
         return((FLOAT) INVALID);

      }

   case 2:
      /* second physical point */
      switch(DeviceType)
      {
      case TYPELMT2:
      case TYPEDCT501:
      case TYPEMCT250:
         if(DataValue[4] & STATUS2_BIT)
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);

      case TYPEMCT318:
      case TYPEMCT360:
      case TYPEMCT370:
         if(DataValue[0] & STATUS2_BIT_MCT3XX)
         {
            return((FLOAT) CLOSED);
         }
         else
         {
            return((FLOAT) OPENED);
         }


      default:
         return((FLOAT) INVALID);

      }

   case 3:
      /* second physical point */
      switch(DeviceType)
      {
      case TYPEMCT250:
         if(PointRecord->PointType == THREESTATEPOINT)
         {
            /* its a three state status */
            if(DataValue[6] & STATUS3_BIT && DataValue[6] & STATUS4_BIT)
            {
               return((FLOAT) STATEFOUR);
            }
            else if(DataValue[6] & STATUS3_BIT)
            {
               return((FLOAT) CLOSED);
            }
            else if(DataValue[6] & STATUS4_BIT)
            {
               return((FLOAT) OPENED);
            }
            else
            {
               return((FLOAT) INDETERMINATE);
            }
         }

         if(DataValue[6] & STATUS3_BIT)
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);

      case TYPEMCT318:
      case TYPEMCT360:
      case TYPEMCT370:
         if(DataValue[0] & STATUS3_BIT_MCT3XX)
         {
            return((FLOAT) CLOSED);
         }
         else
         {
            return((FLOAT) OPENED);
         }

      default:
         return((FLOAT) INVALID);
      }

   case 4:
      /* fourth physical point */
      switch(DeviceType)
      {
      case TYPEMCT250:
         if(DataValue[6] & STATUS4_BIT)
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);

      case TYPEMCT318:
      case TYPEMCT360:
      case TYPEMCT370:
         if(DataValue[0] & STATUS4_BIT_MCT3XX)
         {
            return((FLOAT) CLOSED);
         }
         else
         {
            return((FLOAT) OPENED);
         }

      default:
         return((FLOAT) INVALID);
      }

   case 5:
      /* fifth physical point */
      switch(DeviceType)
      {

      case TYPEMCT318:
      case TYPEMCT360:
      case TYPEMCT370:
         if(DataValue[0] & STATUS5_BIT_MCT3XX)
         {
            return((FLOAT) CLOSED);
         }
         else
         {
            return((FLOAT) OPENED);
         }

      default:
         return((FLOAT) INVALID);
      }

   case 6:
      /* sixth physical point */
      switch(DeviceType)
      {

      case TYPEMCT318:
      case TYPEMCT360:
      case TYPEMCT370:
         if(DataValue[0] & STATUS6_BIT_MCT3XX)
         {
            return((FLOAT) CLOSED);
         }
         else
         {
            return((FLOAT) OPENED);
         }

      default:
         return((FLOAT) INVALID);
      }

   case 7:
      /* seventh physical point */
      switch(DeviceType)
      {

      case TYPEMCT318:
      case TYPEMCT360:
      case TYPEMCT370:
         if(DataValue[0] & STATUS7_BIT_MCT3XX)
         {
            return((FLOAT) CLOSED);
         }
         else
         {
            return((FLOAT) OPENED);
         }

      default:
         return((FLOAT) INVALID);
      }

   case 8:
      /* eighth physical point */
      switch(DeviceType)
      {

      case TYPEMCT318:
      case TYPEMCT360:
      case TYPEMCT370:
         if(DataValue[0] & STATUS8_BIT_MCT3XX)
         {
            return((FLOAT) CLOSED);
         }
         else
         {
            return((FLOAT) OPENED);
         }

      default:
         return((FLOAT) INVALID);
      }

   case 9:
      /* time sync flag */
      switch(DeviceType)
      {
      case TYPELMT2:
      case TYPEDCT501:
      case TYPEMCT212:
      case TYPEMCT224:
      case TYPEMCT226:
      case TYPEMCT240:
      case TYPEMCT242:
      case TYPEMCT250:
         if(DataValue[0])
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);

      default:
         return((FLOAT) INVALID);
      }

   case 10:
      /* Long Power Fail Flag */
      switch(DeviceType)
      {
      case TYPELMT2:
      case TYPEDCT501:
      case TYPEMCT212:
      case TYPEMCT224:
      case TYPEMCT226:
      case TYPEMCT240:
      case TYPEMCT242:
      case TYPEMCT250:
         if(DataValue[4] & L_PWRFAIL_BIT)
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);

      case TYPEMCT310:
         if(DataValue[4] & L_PWRFAIL310_BIT)
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);

      default:
         return((FLOAT) INVALID);
      }

   case 11:
      /* Short Power Fail Flag */
      if(DeviceType == TYPEMCT310)
      {
         /* special bit for mct310 */
         if(DataValue[4] & S_PWRFAIL310_BIT)
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);
      }
      else
      {
         if(DataValue[4] & S_PWRFAIL_BIT)
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);
      }

   case 12:
      /* reading overflow Flag */
      if(DeviceType == TYPEMCT310)
      {
         /* special bit for mct310 */
         if(DataValue[4] & OVERFLOW310_BIT)
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);
      }
      else
      {
         if(DataValue[4] & OVERFLOW_BIT)
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);
      }

   case 13:
   case 15:
      /* load survey or Tou active Flag */
      switch(DeviceType)
      {
      case TYPELMT2:
      case TYPEDCT501:
      case TYPEMCT240:
      case TYPEMCT242:
      case TYPEMCT250:
         /* load survey halt flag */
         if(DataValue[3])
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);

      default:
         return((FLOAT) INVALID);
      }

   case 14:
      /* Tamper or override/local-remote flag */
      switch(DeviceType)
      {
      case TYPEDCT501:
      case TYPELMT2:
         if(DataValue[2])
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);

      case TYPEMCT210:
      case TYPEMCT212:
      case TYPEMCT213:
      case TYPEMCT224:
      case TYPEMCT226:
      case TYPEMCT240:
      case TYPEMCT242:
      case TYPEMCT248:
      case TYPEMCT250:
         /* This is the tamper flag */
         if(DataValue[4] & TAMPER_BIT)
         {
            return((FLOAT) CLOSED);
         }

         return((FLOAT) OPENED);

      default:
         return((FLOAT) INVALID);
      }

   case 16:
      /* tou halt flag */
      switch(DeviceType)
      {
      case TYPELMT2:
      case TYPEDCT501:
         if(DataValue[1])
         {
            return((FLOAT)  CLOSED);
         }
         else
         {
            return((FLOAT)  OPENED);
         }

         break;

      default:
         return((FLOAT)  INVALID);
      }
   }

   return(NORMAL);
}


/* this proc processes a return message for a dlc Status request */
DLLEXPORT INT ProcessDLCStatus (INT ComReturn, DSTRUCT *DSt, CtiDeviceBase *DeviceRecord)
{

#if 0
   ULONG i;
   USHORT StatusData[8];
   USHORT SaveCount;
   STATPOINT StatusRecord;
   ERRSTRUCT ErrorRecord;
   DEVICEPERF DevicePerform;
   ROUTEPERF RoutePerform;
   COMM_ERROR_LOG_STRUCT ComErrorRecord;
   CTIPOINT PointRecord;
   FLOAT Value;
   struct timeb TimeB;
   DRPVALUE DRPValue;
   CHAR RouteName[STANDNAMLEN];
   int rc;

   /* get the status point first */
   memcpy (StatusRecord.DeviceName, DSt->DeviceName, sizeof (DSt->DeviceName));
   memcpy (StatusRecord.PointName, DSt->PointName, sizeof (DSt->PointName));

   if(StatPointGetEqual (&StatusRecord))
   {
      /* bad news if can't find point punt */
      return(!NORMAL);
   }

   if(!(ComReturn))
   {
      /* verify we heard back from the correct device (only if we heard it)

         Note:  The returned address from the device is only the lower 13 bits,
                which means we would not have to mask of the Dst address, but for some
                reason when the reading is queued into the CCU711 we get the whole address
                returned.  So by comparing only 13 bit for both it will not break in
                either case.
      */

      if((DeviceRecord->Address & 0x1fff) != (DSt->Address & 0x1fff))
      {
         /* Address did not match so its a com error */
         ComReturn = WRONGADDRESS;
      }
   }

   /* update performace stats for device and route */
   memcpy (DevicePerform.DeviceName, DeviceRecord->DeviceName,sizeof (DeviceRecord->DeviceName));

   DevicePerform.Error = ComReturn;

   DevicePerfUpdate (&DevicePerform, &ErrorRecord);

   /* Update the route statistics */
   if(!(GetRoute (DeviceRecord->RouteName, StatusRecord.RetryCount, RoutePerform.RouteName)))
   {
      RoutePerform.Type = ErrorRecord.Type;
      RoutePerfUpdate (&RoutePerform);
   }

   UCTFTime (&TimeB);

   /* check for communication failure */
   if(ComReturn)
   {
      memcpy (ComErrorRecord.DeviceName, DeviceRecord->DeviceName, sizeof (DeviceRecord->DeviceName));
      memcpy (ComErrorRecord.RouteName, RoutePerform.RouteName, sizeof (ComErrorRecord.RouteName));

      ComErrorRecord.TimeStamp = TimeB.time;
      ComErrorRecord.Error = ComReturn;

      if(TimeB.dstflag)
      {
         ComErrorRecord.StatusFlag = DSTACTIVE;
      }
      else
      {
         ComErrorRecord.StatusFlag = 0;
      }

      /* Update the comm error database */
      ComErrorLogAdd (&ComErrorRecord, &ErrorRecord, FALSE);

      /* get the next route */
      if(DeviceRecord->Status & INHIBITED ||
         GetNextRoute (DeviceRecord->RouteName, &StatusRecord.RetryCount, RouteName) ||
         RouteName[0] == ' ')
      {
         /* we are done */
         InsertPlugStatus (DeviceRecord);
         return(FAILED_NO_MORE);
      }

      /* queue up the request to porter */
      SaveCount = StatusRecord.RetryCount;
      StatPointLock (&StatusRecord);
      StatusRecord.RetryCount = SaveCount;  /* lock reloads the record */

      /* queue up the request to porter */
      rc = RequestDLCStatus (DeviceRecord, &StatusRecord);

      if(rc == REQUEUE_DEVICE)
      {
         /* need to reque the point */
         StatusRecord.NextScan = TimeB.time + 1L;
         StatusRecord.QueueStat = REQUEUE_POINT;
      }
      else if(rc == FAILED_NO_MORE)
      {
         StatPointUnLock (&StatusRecord);
         InsertPlugStatus (DeviceRecord);
         return(FAILED_NO_MORE);
      }

      StatPointFastUpdate (&StatusRecord);
      return(REQUEUE_DEVICE);
   }

   /* do a quick check of the sequence number to see if we want the message */
   if(StatusRecord.SeqNumber == 0 &&
      StatusRecord.QueueStat == FORCED_DLC_SCAN)
   {
      /* throw the message away we need to do a forced scan NOW */
      return(NORMAL);
   }
   else if(StatusRecord.SeqNumber != 0 &&
           StatusRecord.SeqNumber != DSt->Sequence)
   {
      /* throw the message away we were doing a force scan
         so there is another scan pending */
      return(NORMAL);
   }

   /* extract status data by device type */
   switch(DeviceRecord->DeviceType)
   {
   case TYPEMCT210:
   case TYPEMCT310:
      /* only 2 bytes for these guys */
      StatusData[4] = MAKESHORT(DSt->Message[0], 0);
      StatusData[5] = MAKESHORT(DSt->Message[1], 0);
      break;

   case TYPEMCT318:
   case TYPEMCT360:
   case TYPEMCT370:
      /* only 1 byte of status data */
      StatusData[0] = MAKESHORT (DSt->Message[0], 0);
      break;

   case TYPEMCT248:
      /* 248'S must be change around alittle so it looks like the
      other devices because we must read more data to get the
      mct248 relay status */

      for(i = 0; i < 6; i++)
      {
         /* put the data in order for translating */
         StatusData[i] = MAKESHORT(DSt->Message[i + 7], 0);
      }

      /* make the cap status the last byte */
      StatusData[7] = MAKESHORT(DSt->Message[0], 0);
      break;

   case TYPE_REPEATER800:
   case TYPE_REPEATER900:
      /* repeaters are only for comm status
      so we just throw away the communication */
      return(NORMAL);

   default:
      for(i = 0; i < 7; i++)
      {
         /* put the data in order for translating */
         StatusData[i] = MAKESHORT(DSt->Message[i], 0);

      }
      StatusData[7] = 0;

   }   /* end of device type switch */

   /* must find all status points on any possible input and update them */
   memcpy (PointRecord.DeviceName,
           StatusRecord.DeviceName,
           sizeof (StatusRecord.DeviceName));

   if(!(PointgetDeviceFirst (&PointRecord)))
   {
      do
      {
         switch(PointRecord.PointType)
         {
         case TWOSTATEPOINT:
         case THREESTATEPOINT:
            /* get the status point record */
            memcpy (StatusRecord.DeviceName,
                    PointRecord.DeviceName,
                    sizeof (PointRecord.DeviceName));

            memcpy (StatusRecord.PointName,
                    PointRecord.PointName,
                    sizeof (PointRecord.PointName));

            if(!(StatPointLock (&StatusRecord)))
            {
               /* we found and locked a Status point,
                  mark its next scan time to be scanned */
               StatusRecord.RetryCount = 0;
               StatusRecord.QueueStat = POINT_NOT_QUEUED;
               StatusRecord.QueueTime = 0L;
               StatusRecord.NextScan = NextScan (TimeB.time,
                                                 DeviceRecord->AccumScanRate,
                                                 0);

               StatPointFastUpdate (&StatusRecord);
            }

            /* offset 20 is for verification and so
               we throw the status away a let the perf
               routines take care of it */
            if(PointRecord.PointOffset != 20)
            {
               /* good data must translate it */
               PointLock (&PointRecord);

               Value = TranslateStatusValue (DeviceRecord->DeviceType,
                                             &PointRecord,
                                             StatusData);

               /* do the big data quality check */
               CheckDataStateQuality (DeviceRecord,
                                      &PointRecord,
                                      &Value,
                                      FALSE,
                                      TimeB.time,
                                      TimeB.dstflag,
                                      FALSE,
                                      FALSE);

               /* Load up the common parts of the DRP message */
               memcpy (DRPValue.DeviceName,
                       PointRecord.DeviceName,
                       STANDNAMLEN);
               memcpy (DRPValue.PointName,
                       PointRecord.PointName,
                       STANDNAMLEN);

               DRPValue.TimeStamp = TimeB.time;

               DRPValue.Type = DRPTYPEVALUE;

               /* Send the point to drp */
               DRPValue.Value = PointRecord.CurrentValue;
               DRPValue.Quality = PointRecord.CurrentQuality;
               DRPValue.AlarmState = PointRecord.AlarmStatus;

               SendDRPPoint (&DRPValue);
            }

            break;

         default:
            break;
         }
      } while(!(PointgetDeviceNext (&PointRecord)));
   }

#endif
   return(NORMAL);
}


/* handles failed communication which needs to be plugged or updated */
DLLEXPORT INT InsertPlugStatus (DEVICE *DeviceRecord)
{
   STATPOINT StatusRecord;
   CTIPOINT PointRecord;
   USHORT Quality;
   DRPVALUE DRPValue;
   FLOAT Value;
   struct timeb TimeB;

   UCTFTime (&TimeB);

   if(DeviceRecord->Status & INHIBITED)
   {
      /* it was not a plug but is inhibited */
      Quality = FALSE;
   }
   else
   {
      Quality = PLUGGED;
   }

   /* must find all status points on any possible input and update them */
   memcpy (PointRecord.DeviceName,
           DeviceRecord->DeviceName,
           STANDNAMLEN);

   if(!(PointgetDeviceFirst (&PointRecord)))
   {
      do
      {
         switch(PointRecord.PointType)
         {
         case TWOSTATEPOINT:
         case THREESTATEPOINT:
            /* get the status point record */
            memcpy (StatusRecord.DeviceName,
                    PointRecord.DeviceName,
                    sizeof (PointRecord.DeviceName));

            memcpy (StatusRecord.PointName,
                    PointRecord.PointName,
                    sizeof (PointRecord.PointName));

            if(!(StatPointGetEqual (&StatusRecord)))
            {
               /* found the scan point from the table */
               StatPointLock (&StatusRecord);

               StatusRecord.RetryCount = 0;
               StatusRecord.QueueStat = POINT_NOT_QUEUED;
               StatusRecord.QueueTime = 0L;

               StatusRecord.NextScan = NextScan (TimeB.time,
                                                 DeviceRecord->AccumScanRate,
                                                 0);

               StatPointFastUpdate (&StatusRecord);
               if(PointRecord.PointOffset != 20)
               {
                  /* only process none comm status points */
                  PointLock (&PointRecord);

                  Value = 0.0;

                  /* do the big data quality check */
                  CheckDataStateQuality (DeviceRecord,
                                         &PointRecord,
                                         &Value,
                                         Quality,
                                         TimeB.time,
                                         TimeB.dstflag,
                                         FALSE,
                                         FALSE);

                  /* Load up the common parts of the DRP message */
                  memcpy (DRPValue.DeviceName,
                          PointRecord.DeviceName,
                          STANDNAMLEN);

                  memcpy (DRPValue.PointName,
                          PointRecord.PointName,
                          STANDNAMLEN);

                  DRPValue.TimeStamp = TimeB.time;

                  DRPValue.Type = DRPTYPEVALUE;

                  /* Send the point to drp */
                  DRPValue.Value = PointRecord.CurrentValue;
                  DRPValue.Quality = PointRecord.CurrentQuality;
                  DRPValue.AlarmState = PointRecord.AlarmStatus;

                  SendDRPPoint (&DRPValue);
               }
            }

            break;

         default:
            break;
         }
      } while(!(PointgetDeviceNext (&PointRecord)));
   }


   return(NORMAL);
}


/* Routine to calculate next scan time */
DLLEXPORT ULONG NextScan (ULONG TimeNow, ULONG ScanRate, USHORT DeviceType)
{
   ULONG MyNextScan;

   if(ScanRate == 0L)
   {
      return(0xFFFFFFFF);
   }

   if(ScanRate < 60L)
   {
      /* Do not allow DLC device to scan faster than 60 seconds */
      MyNextScan = setNextInterval (TimeNow, 60L);
      if(MyNextScan == TimeNow)
      {
         /* set it ahead a little so we catch the next interval */
         MyNextScan = setNextInterval (TimeNow + 1, 60L);
      }
   }
   else
   {
      MyNextScan = setNextInterval (TimeNow, ScanRate);
      if(MyNextScan == TimeNow)
      {
         /* set it ahead a little so we catch the next interval */
         MyNextScan = setNextInterval (TimeNow + 1, ScanRate);
      }
   }

   /* Some devices we need to wait till half past the minute */
   switch(DeviceType)
   {
   case TYPELMT2:
   case TYPEMCT212:
   case TYPEMCT224:
   case TYPEMCT226:
      MyNextScan += 30L;
   }

   return(MyNextScan);
}

#endif
