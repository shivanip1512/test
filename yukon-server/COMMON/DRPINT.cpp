#include "yukon.h"
#pragma title ( "DRP Interface Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1992-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        DRPINT.C

    Purpose:
        set of routines to allow DSM2 process' to send and receive
        blocks of data from DRP.

    The following procedures are contained in this module:
        xDRPInit                getDRPPoint
        SendDRPPoint            DRPThread
        DRPQueryQueue           xSendDRPTextAlarm
        SendDRPFBLCMode         xSendDRPTextMessage
        DRPIntCleanUp           SendDRP

    Initial Date:
        6-92

    Revision History:
        Unknown prior to 8-93
        9-6-93  Converted to 32 bit                             WRO


   -------------------------------------------------------------------- */

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <malloc.h>
#include <process.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include "queues.h"
#include "dsm2.h"
#include "drp.h"


#define THREAD_STACK_SIZE 32768


HPIPE          DRPPipeWriteHandle   = (HPIPE) NULL;
HPIPE          DRPPipeReadHandle    = (HPIPE) NULL;
HCTIQUEUE      DRPQueueHandle       = (HCTIQUEUE) NULL;
CHAR           MyProcess[DESTSIZE];         /* Basic style process name */
CHAR           DRPWritePipeName[50];

/* Routine to initialize DRP interface */
IM_EX_CTIBASE INT xInitDRP (PCHAR ProcessName)
{
   ULONG i, Action;
   DRPNULL DRPNull;
   ULONG BytesWritten;

   /* set up the exit list handler */
   // DosExitList (EXLST_ADD, DRPIntCleanUp);     // FIX FIX FIX CGP

   /* Save the basic version of the string */
   for(i = 0; i < DESTSIZE; i++)
   {
      if(ProcessName[i] == '\0')
      {
         break;
      }
      else
      {
         MyProcess[i] = ProcessName[i];
      }
   }

   memset (MyProcess + i,
           ' ',
           DESTSIZE - i);

   /* Create the queue for the messages coming in */
   if((i = CreateQueue (&DRPQueueHandle,
                        QUE_FIFO)) != NORMAL)
   {
      printf ("Error Creating DRP Queue\n");
      CTIExit (EXIT_PROCESS, i);
   }

   /* get the name right */
   strcpy (DRPWritePipeName, "\\PIPE\\DRP");

   /* Start up the thread */
   if(_beginthread (DRPThread, THREAD_STACK_SIZE, NULL) == -1)
   {
      printf ("Error Starting DRP Thread\n");
      CTIExit ( EXIT_PROCESS, -1);
   }

   /* Now attempt to open the pipe to the DRP */
   if(CTIOpen (DRPWritePipeName,
               &DRPPipeWriteHandle,
               &Action,
               0L,
               FILE_NORMAL,
               FILE_OPEN,
               OPEN_ACCESS_WRITEONLY | OPEN_SHARE_DENYREADWRITE,
               0L))
   {
      /* Don't worry about it it will eventually get written */
      DRPPipeWriteHandle = (HPIPE) NULL;
   }

   if(DRPPipeWriteHandle != (HPIPE) NULL)
   {
      /* Write a null message to DRP */
      DRPNull.Type = DRPTYPENULL;
      memcpy (DRPNull.SourceName, MyProcess, DESTSIZE);

      if(CTIWrite (DRPPipeWriteHandle,
                   &DRPNull,
                   sizeof (DRPNull),
                   &BytesWritten) || BytesWritten == 0)
      {
         if(DRPPipeWriteHandle != (HPIPE) NULL)
         {
            CTIClose (DRPPipeWriteHandle);
            DRPPipeWriteHandle = (HPIPE) NULL;
         }
      }
   }

   /* That all the initialization needed */
   return(NORMAL);
}

IM_EX_CTIBASE INT xInitDRPOutOnly (PCHAR ProcessName)
{
   PSZ DRPServer;
   ULONG i, Action;

   /* set up the exit list handler */
   // CTIExitList (EXLST_ADD, DRPIntCleanUp);        // CGP FIX FIX FIX

   /* Save the basic version of the string */
   for(i = 0; i < DESTSIZE; i++)
   {
      if(ProcessName[i] == '\0')
      {
         break;
      }
      else
      {
         MyProcess[i] = ProcessName[i];
      }
   }

   memset (MyProcess + i,
           ' ',
           DESTSIZE - i);

   /* Check if this is a network connection */
   if(CTIScanEnv ("DRPSERVER",
                  &DRPServer))
   {
      strcpy (DRPWritePipeName, "\\PIPE\\DRPIN");
   }
   else
   {
      strcpy (DRPWritePipeName, "\\\\");
      strcat (DRPWritePipeName, DRPServer);
      strcat (DRPWritePipeName, "\\PIPE\\DRPIN");
   }

   /* Now attempt to open the pipe to the DRP */
   if(CTIOpen (DRPWritePipeName,
               &DRPPipeWriteHandle,
               &Action,
               0L,
               FILE_NORMAL,
               FILE_OPEN,
               OPEN_ACCESS_WRITEONLY | OPEN_SHARE_DENYREADWRITE,
               0L))
   {
      /* Don't worry about it it will eventually get written */
      DRPPipeWriteHandle = (HPIPE) NULL;
   }

   /* That all the initialization needed */
   return(NORMAL);
}


/* Routine to initialize DAD interface */
IM_EX_CTIBASE INT xInitDRPDAD ()
{
   ULONG i;

   /* set up the exit list handler */
   // CTIExitList (EXLST_ADD, DRPIntCleanUp);      // CGP FIX FIX FIX

   /* Create the queue for the messages coming in */
   if((i = CreateQueue (&DRPQueueHandle,
                        QUE_FIFO)) != NORMAL)
   {
      printf ("Error Creating Scada Queue\n");
      CTIExit (EXIT_PROCESS, i);
   }

   /* Start up the thread */
   if(_beginthread (DRPDADInThread,
                    THREAD_STACK_SIZE,
                    NULL) == -1)
   {
      printf ("Error Starting DRP Thread\n");
      CTIExit ( EXIT_PROCESS, -1);
   }

   /* That all the initialization needed */
   return(NORMAL);
}


/* Routine to get Point Data */
IM_EX_CTIBASE INT getDRPPoint (DRPVALUE *DRPValue)
{
   DRPVALUE *DRPIn;
   REQUESTDATA ReadResult;
   ULONG ReadLength;
   BYTE ReadPriority;

   if(ReadQueue (DRPQueueHandle,
                 &ReadResult,
                 &ReadLength,
                 (PPVOID) &DRPIn,
                 0x0000,
                 DCWW_NOWAIT,
                 &ReadPriority))
      return(QUEUE_READ);

   *DRPValue = *DRPIn;

   free (DRPIn);

   return(NORMAL);
}


/* Routine to wait for Point Data */
IM_EX_CTIBASE INT WaitDRPPoint (DRPVALUE *DRPValue)
{
   DRPVALUE *DRPIn;
   REQUESTDATA ReadResult;
   ULONG ReadLength;
   BYTE ReadPriority;

   if(ReadQueue (DRPQueueHandle,
                 &ReadResult,
                 &ReadLength,
                 (PPVOID) &DRPIn,
                 0x0000,
                 DCWW_WAIT,
                 &ReadPriority))
      return(QUEUE_READ);

   *DRPValue = *DRPIn;

   free (DRPIn);

   return(NORMAL);
}

/* Routine to send a demand message to DRP system */
IM_EX_CTIBASE INT SendDRPPoint (DRPVALUE *DRPValue)
{
   /* set the name of the source process */
   memcpy (DRPValue->SourceName, MyProcess, DESTSIZE);

   /* Check the type */
   if(DRPValue->Type != DRPTYPEVALUE &&
      DRPValue->Type != DRPTYPEALARM)
      return(BADTYPE);

   return(SendDRP (DRPValue,
                   sizeof (*DRPValue)));
}


/* Thread to retrieve values coming in from DRP */
VOID DRPThread (PVOID Arg)
{
   DRPVALUE *DRPValue;
   ULONG i, ReadCount;
   CHAR Name[50];

   /* create the pipe name */
   strcpy (Name, "\\PIPE\\");
   for(i = 0; i < DESTSIZE; i++)
   {
      if(MyProcess[i] == ' ')
      {
         Name[i + 6] = '\0';
         break;
      }
      Name[i + 6] = MyProcess[i];
   }

   /* create an instance of the scada pipe */
   if(CTICreateNPipe (Name,
                      &DRPPipeReadHandle,
                      NP_ACCESS_INBOUND,
                      NP_WAIT | NP_READMODE_MESSAGE | NP_TYPE_MESSAGE | 1,
                      65000,
                      65000,
                      1000L))
   {
      printf ("Error Creating Inbound Pipe\n");
      CTIExit (EXIT_PROCESS, -1);
   }

   /* now wait for someone to hook up */
   if(CTIConnectNPipe (DRPPipeReadHandle))
   {
      printf ("Error Connection Inbound Pipe\n");
      CTIExit (EXIT_PROCESS, -1);
   }

   /* sit and wait for something to come in */
   for(;;)
   {
      /* Allocate an area of memory */
      if((DRPValue = (DRPVALUE*)malloc (sizeof (*DRPValue))) == NULL)
      {
         continue;
      }

      /* Wait for a demand to come in */
      if(DRPPipeReadHandle == (HPIPE) NULL ||
         CTIRead (DRPPipeReadHandle,
                  DRPValue,
                  sizeof (*DRPValue),
                  &ReadCount) || ReadCount == 0)
      {
         /* Close the pipe (s) */
         if(DRPPipeReadHandle != (HPIPE) NULL)
         {
            CTIClose (DRPPipeReadHandle);
            DRPPipeReadHandle = (HPIPE) NULL;

            if(DRPPipeWriteHandle != (HPIPE) NULL)
            {
               CTIDisConnectNPipe (DRPPipeWriteHandle);
               DRPPipeWriteHandle = (HPIPE) NULL;
            }
         }

         /* create the pipe again */
         CTISleep (1000L);
         while(CTICreateNPipe (Name,
                               &DRPPipeReadHandle,
                               NP_ACCESS_INBOUND,
                               NP_WAIT | NP_READMODE_MESSAGE | NP_TYPE_MESSAGE | 1,
                               65000,
                               65000,
                               1000L))
         {
            CTISleep (5000L);
         }

         /* now wait for someone to hook up */
         if(CTIConnectNPipe (DRPPipeReadHandle))
         {
            DRPPipeReadHandle = (HPIPE) NULL;
            free (DRPValue);
            continue;
         }

         continue;
      }

      /* Filter out the nulls */
      if(DRPValue->Type == DRPTYPENULL)
      {
         free (DRPValue);
         continue;
      }

      /* We got a value so put it on the queue */
      if(WriteQueue (DRPQueueHandle,
                     0,
                     sizeof (*DRPValue),
                     (PBYTE) DRPValue,
                     0))
      {
         free (DRPValue);
         continue;
      }
   }
}

/* Thread to retrieve values coming in for DAD */
VOID DRPDADInThread (PVOID Arg)
{
   PSZ DRPServer;
   DRPVALUE *DRPValue;
   ULONG ReadCount;
   ULONG Action;
   CHAR Name[50];

   if(CTIScanEnv ("DRPSERVER",
                  &DRPServer))
   {
      strcpy (Name, "\\PIPE\\DRPDAD");
   }
   else
   {
      strcpy (Name, "\\\\");
      strcat (Name, DRPServer);
      strcat (Name, "\\PIPE\\DRPDAD");
   }

   /* sit and wait for something to come in */
   for(;;)
   {
      if(DRPPipeReadHandle == (HPIPE) NULL)
      {
         /* Attempt to open it */
         if(CTIOpen (Name,
                     &DRPPipeReadHandle,
                     &Action,
                     0L,
                     FILE_NORMAL,
                     FILE_OPEN,
                     OPEN_ACCESS_READONLY | OPEN_SHARE_DENYREADWRITE,
                     0L))
         {
            /* Didnt get it open yet */
            DRPPipeReadHandle = (HPIPE) NULL;
            CTISleep (1000L);
            continue;
         }
      }

      /* Allocate an area of memory */
      if((DRPValue = (DRPVALUE*)malloc (sizeof (*DRPValue))) == NULL)
      {
         continue;
      }

      /* Wait for a demand to come in */
      if(CTIRead (DRPPipeReadHandle,
                  DRPValue,
                  sizeof (*DRPValue),
                  &ReadCount) || ReadCount == 0)
      {
         /* Close the pipe */
         if(DRPPipeReadHandle != (HPIPE) NULL)
         {
            CTIClose (DRPPipeReadHandle);
            DRPPipeReadHandle = (HPIPE) NULL;
         }

         continue;
      }

      /* Filter out the nulls */
      if(DRPValue->Type == DRPTYPENULL)
      {
         free (DRPValue);
         continue;
      }

      /* We got a value so put it on the queue */
      if(WriteQueue (DRPQueueHandle,
                     0,
                     sizeof (*DRPValue),
                     (PBYTE) DRPValue,
                     0))
      {
         free (DRPValue);
         continue;
      }
   }
}


/* routine to check if any results waiting on queue */
IM_EX_CTIBASE INT DRPQueryQueue ()
{
   ULONG FarElements;

   QueryQueue (DRPQueueHandle,
               &FarElements);

   return(FarElements);
}

/* Routine to send an alarm text message */
IM_EX_CTIBASE INT xSendDRPTextAlarm (PCHAR Message)
{
   DRPTEXT DRPText;
   ULONG i;

   DRPText.Type = DRPTYPETEXTALARM;

   memcpy (DRPText.SourceName, MyProcess, DESTSIZE);

   /* Load the Message */
   strncpy (DRPText.Message, Message, sizeof (DRPText.Message));

   /* Make sure blank padding intact */
   for(i = 0; i < sizeof (DRPText.Message); i++)
   {
      if(DRPText.Message[i] == '\0')
      {
         DRPText.Message[i] = ' ';
      }
   }

   return(SendDRP (&DRPText,
                   sizeof (DRPText)));
}

/* Routine to send an FBLC Mode message */
IM_EX_CTIBASE INT SendDRPFBLCMode (USHORT Mode)
{
   DRPFBLCMODE DRPFBLCMode;

   DRPFBLCMode.Type = DRPTYPEFBLCMODE;

   memcpy (DRPFBLCMode.SourceName, MyProcess, DESTSIZE);

   /* Load the Mode */
   DRPFBLCMode.Mode = Mode;

   return(SendDRP (&DRPFBLCMode,
                   sizeof (DRPFBLCMode)));
}

/* Routine to send a text message to DRP */
IM_EX_CTIBASE INT xSendDRPTextMessage (PCHAR Message)
{
   DRPTEXT DRPText;
   ULONG i;

   DRPText.Type = DRPTYPETEXTMESSAGE;

   memcpy (DRPText.SourceName, MyProcess, DESTSIZE);

   /* Load the Message */
   strncpy (DRPText.Message, Message, sizeof (DRPText.Message));

   /* Make sure blank padding intact */
   for(i = 0; i < sizeof (DRPText.Message); i++)
   {
      if(DRPText.Message[i] == '\0')
      {
         DRPText.Message[i] = ' ';
      }
   }

   return(SendDRP (&DRPText, sizeof (DRPText)));
}

/* Exit function */
VOID IM_EX_CTIBASE DRPIntCleanUp (ULONG Reason)
{
   DRPSHUTDOWN DRPShutDown;
   ULONG BytesWritten;

   /* Attempt to send a disconnect message to drp */
   if(DRPPipeWriteHandle != (HPIPE) NULL)
   {
      /* set up the shutdown message */
      DRPShutDown.Type = DRPTYPESHUTDOWN;
      memcpy (DRPShutDown.SourceName, MyProcess, DESTSIZE);

      /* Attempt to write it... can't do much if it fails */
      CTIWrite (DRPPipeWriteHandle,
                &DRPShutDown,
                sizeof (DRPShutDown),
                &BytesWritten);

   }

   /* Now close down the various handles */
   if(DRPPipeReadHandle != (HPIPE) NULL)
   {
      CTIDisConnectNPipe (DRPPipeReadHandle);
   }

   if(DRPPipeWriteHandle != (HPIPE) NULL)
   {
      CTIClose (DRPPipeWriteHandle);
   }

   if(DRPQueueHandle != (HQUEUE) NULL)
   {
      CloseQueue (DRPQueueHandle);
   }

   // CTIExitList (EXLST_EXIT, NULL);    // FIX FIX FIX CGP
}


/* Routine to actually send message to DRP */
IM_EX_CTIBASE INT SendDRP (PVOID Message, USHORT Length)
{
   ULONG BytesWritten;
   ULONG Action;

   /* If pipe open just send it */
   if(DRPPipeWriteHandle != (HPIPE) NULL)
   {
      if(CTIWrite (DRPPipeWriteHandle,
                   Message,
                   Length,
                   &BytesWritten) || BytesWritten == 0)
      {
         if(DRPPipeWriteHandle != (HPIPE) NULL)
         {
            CTIClose (DRPPipeWriteHandle);
            DRPPipeWriteHandle = (HPIPE) NULL;
         }
      }
   }

   /* if already null or above fails take one crack at it */
   if(DRPPipeWriteHandle == (HPIPE) NULL)
   {
      if(CTIOpen (DRPWritePipeName,
                  &DRPPipeWriteHandle,
                  &Action,
                  0L,
                  FILE_NORMAL,
                  FILE_OPEN,
                  OPEN_ACCESS_WRITEONLY | OPEN_SHARE_DENYREADWRITE,
                  0L))
      {
         DRPPipeWriteHandle = (HPIPE) NULL;

         return(PIPEWRITE);
      }

      if(CTIWrite (DRPPipeWriteHandle,
                   Message,
                   Length,
                   &BytesWritten) || BytesWritten == 0)
      {
         if(DRPPipeWriteHandle != (HPIPE) NULL)
         {
            CTIClose (DRPPipeWriteHandle);
            DRPPipeWriteHandle = (HPIPE) NULL;
         }

         return(PIPEWRITE);
      }
   }

   return(NORMAL);
}
