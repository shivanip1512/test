#include "yukon.h"

#pragma title ( "Porter Process Interface Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PEXEC.C

    Purpose:
        Routines to allow process's to make request of porter

    The following procedures are contained in this module:
        xIOInit                     PortPipeInit
        PortPipeCleanup             nrexec
        nvexec                      naexec
        nb1exec                     nb2exec
        nb2exexsq                   nb2execs
        nb2execr                    nv2query
        ReturnThread                LoopBack
        CCUReset                    SwitchControl


    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        8-30-93     Converted to 32 bit                         WRO

   -------------------------------------------------------------------- */

#include <windows.h>       // These next few are required for Win32
#include <process.h>
#include <iostream>
using namespace std;

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include <rw\thr\mutex.h>
#include <rw/toolpro/winsock.h>

#include "os2_2w32.h"
#include "cticalls.h"
#include "ctinexus.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "dllbase.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "lm_auto.h"
#include "perform.h"
#include "dialup.h"
#include "c_port_interface.h"
#include "logger.h"

extern CTINEXUS   PorterNexus;            // declared in dllmain.cpp


HCTIQUEUE   MyWaitQueueHandle = {(HCTIQUEUE) NULL};
HCTIQUEUE   MyQueuedQueueHandle = {(HCTIQUEUE) NULL};
USHORT      FUBAR;
CHAR        MyServerName[20];
USHORT      ExitListLoaded = {FALSE};


INT sendToPorter(OUTMESS *OutMessage, ULONG &BytesWritten);

/* Routine to intialize process I/O with porter */
IM_EX_CTIBASE INT xIOInit(PCHAR ServerName)
{
   VOID ReturnThread (VOID *Arg);
   ULONG i;

   /* Save the server name */
   if(ServerName != NULL && ServerName[0] != ' ' && ServerName[0] != '\0')
      strcpy (MyServerName, ServerName);
   else
      MyServerName[0] = '\0';

   /* Clear the FUBAR flag */
   FUBAR = 0;

   {
      RWMutexLock::LockGuard  guard(coutMux);
      cout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   if((i = CreateQueue (&MyWaitQueueHandle, QUE_FIFO)) != NORMAL)
   {
      printf ("Error Opening Wait Queue:  %ld\n", i);
      CTISleep (2000L);
      CTIExit (EXIT_PROCESS, i);
   }

   /* Create the queued entry return queue */
   if((i = CreateQueue (&MyQueuedQueueHandle, QUE_PRIORITY)) != NORMAL)
   {
      printf ("Error Opening Queued Queue:  %ld\n", i);
      CTISleep (2000L);
      CTIExit (EXIT_PROCESS, i);
   }

   if(_beginthread (ReturnThread,
                    RETURN_THREAD_STACK_SIZE,
                    NULL) == -1)
   {
      printf ("Error starting return thread:  %ld\n", _errno);
      CTISleep (2000L);
      CTIExit (EXIT_PROCESS, i);
   }

   CTISleep (1500L);

   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL) return(PIPEOPEN);

   return(NORMAL);
}


/* Routine to initialize pipe to porter */
IM_EX_CTIBASE INT PortPipeInit (USHORT Wait)
{
   ULONG i = NOTNORMAL, j = 0;
   PCHAR ServerName = NULL;
   PSZ EnvServerName;
   CHAR Name[100];


   /* Check if we need to close the pipe */
   PorterNexus.CTINexusClose();     // Close it if it is open.

   /*
    *  OK, this is the client side of a Nexus to Port Control.
    */

   if(CTIScanEnv ("PORTSERVER", &EnvServerName))
   {
      ServerName = NULL;
   }
   else
   {
      ServerName = (PCHAR) EnvServerName;
   }

   if(ServerName == NULL)
   {
      strcpy (Name, "127.0.0.1");   // ME ME ME in gethostbyname call.
   }
   else
   {
      strcpy (Name, ServerName);
   }

   sprintf(PorterNexus.Name, "pexec nexus from client to %s port control", Name);

   while((i = PorterNexus.CTINexusOpen(Name, PORTCONTROLNEXUS, CTINEXUS_FLAG_READEXACTLY)) != NORMAL)
   {
      if(Wait != WAIT)
      {
         PorterNexus.CTINexusClose();
         i = PIPEOPEN;
         break;      // the while loop
      }
      else
      {
         if(!(++j % 60))
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could not connect to Port Control " << i << "   " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }

         CTISleep(1000L);
      }
   }

   return(i);
}

/* Routine that gets run when we go tits up */
IM_EX_CTIBASE VOID PortPipeCleanup (ULONG Reason)
{
   PorterNexus.CTINexusClose();     // Close it if it is open.
}


/* Thread to field return messages from pipe and send them to queue */
VOID ReturnThread (VOID *Arg)
{
   INMESS *InMessage;
   ULONG  BytesRead;

   /* Up this threads priority a notch over the main proc */
   CTISetPriority (PRTYS_THREAD, PRTYC_NOCHANGE, 10, 0);

   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
      PortPipeInit (WAIT);

   /* sit and wait forever for goodies to get here */
   for(;;)
   {
      /* Allocate the memory for returned message */
      {
         RWMutexLock::LockGuard  guard(coutMux);
         cout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
      if((InMessage = (INMESS*)malloc (sizeof (*InMessage))) == NULL)
      {
         continue;
      }

      // Make the DeviceID hold a marker from me if there is no Device Set....
      InMessage->DeviceID = PEXEC_DEVID;

      /* see if the pipe handle is still in shape */
      if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
         PortPipeInit(WAIT);

      if(PorterNexus.CTINexusRead (InMessage,
                                   sizeof (*InMessage),
                                   &BytesRead,
                                   CTINEXUS_INFINITE_TIMEOUT) || BytesRead == 0)
      {
         PorterNexus.CTINexusClose();     // Close it if it is open.

         FUBAR = TRUE;
         free (InMessage);
         continue;
      }

      /* We have a return message send it on */
      if(InMessage->Sequence)
      {
         /* this is a queued message */
         WriteQueue (MyQueuedQueueHandle,
                     InMessage->EventCode,
                     sizeof (*InMessage),
                     (PBYTE) InMessage,
                     (BYTE) InMessage->Priority);
      }
      else
      {
         /* this is a wait message */
         WriteQueue (MyWaitQueueHandle,
                     InMessage->EventCode,
                     sizeof (*InMessage),
                     (PBYTE) InMessage,
                     (BYTE) InMessage->Priority);
      }
   }
}

/* Loopback routine */
IM_EX_CTIBASE INT LoopBack (USHORT Port, USHORT Remote)
{
   ULONG i;
   INMESS *InMessage;
   OUTMESS OutMessage;
   ULONG BytesWritten;
   REQUESTDATA ReadResult;
   BYTE ReadPriority;
   ULONG ReadLength;
   ULONG QueueElements;

   OutMessage.Port = Port;
   OutMessage.Remote = Remote;
   OutMessage.Priority = MAXPRIORITY;
   OutMessage.Retry = 0;
   OutMessage.Sequence = 0;
   OutMessage.EventCode = COMMANDCODE | LOOPBACKCOMMAND | RESULT | WAIT;

   if( (i = sendToPorter(&OutMessage,BytesWritten) != NORMAL) )
   {
       return i;
   }

   /* Wait till something is on the return queue or we lose the pipe */
   do
   {
      CTISleep (100L);
      if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
         return(PIPEBROKEN);

      if(FUBAR)
         return(PIPEWASBROKEN);

      QueueElements = 0;
      QueryQueue (MyWaitQueueHandle, &QueueElements);

   } while(!QueueElements);

   /* result on wait queue */
   if(ReadQueue (MyWaitQueueHandle, &ReadResult,  &ReadLength, (PVOID FAR *) &InMessage, 0, DCWW_NOWAIT, &ReadPriority))
   {
      return(QUEUE_READ);
   }

   i = InMessage->EventCode & ~ENCODED;

   free (InMessage);

   return(i);
}

VOID IM_EX_CTIBASE resetOutMess(OUTMESS *oM)
{
   memset(oM, 0L, sizeof(OUTMESS));
}

INT sendToPorter(OUTMESS *OutMessage, ULONG &BytesWritten)
{
   INT status = NORMAL;

   BytesWritten = 0;


   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit(NOWAIT))
      {
         status = BADSOCK;
      }
   }

   /* And send them to porter */
   if(PorterNexus.CTINexusWrite(OutMessage, sizeof(OUTMESS), &BytesWritten, 30L) || BytesWritten == 0)
   {
      PorterNexus.CTINexusClose();     // Close it if it is open.
      FUBAR = TRUE;
      status = SOCKWRITE;
   }

   return status;
}


#if 0

/* Routine to reset a CCU */
IM_EX_CTIBASE INT CCUReset (USHORT Port, USHORT Remote)
{
   OUTMESS OutMessage;
   ULONG BytesWritten;

   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* Load up the structure */
   OutMessage.Port = Port;
   OutMessage.Remote = Remote;
   OutMessage.TimeOut = TIMEOUT;
   OutMessage.Retry = 0;
   OutMessage.OutLength = 3;
   OutMessage.InLength = 0;
   OutMessage.Source = 0;
   OutMessage.Destination = DEST_BASE;
   OutMessage.Command = CMND_ACTIN;
   OutMessage.Sequence = 0;
   OutMessage.Priority = MAXPRIORITY;
   OutMessage.EventCode = NOWAIT | NORESULT | ENCODED;
   OutMessage.Buffer.OutMessage[6] = COLD;
   OutMessage.ReturnNexus = NULL;
   OutMessage.SaveNexus  = NULL;

   /* Thats it so send the message */
   return sendToPorter(&OutMessage, BytesWritten);
}


/* Switch Control Routine */
IM_EX_CTIBASE INT SwitchControl (CSTRUCT *ControlStruct)
{
   ULONG BytesWritten;
   OUTMESS OutMessage;

   /* if pipe shut down return message */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* Load up the peices of the structure */
   OutMessage.EventCode = NORESULT | NOWAIT | COMMANDCODE | REMOTECONTROL;
   OutMessage.Buffer.CSt = *ControlStruct;
   OutMessage.ReturnNexus = NULL;
   OutMessage.SaveNexus  = NULL;

   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}


/* Routine to set the lockout bit in an LCU */
IM_EX_CTIBASE INT LCULockOutset (USHORT Port, USHORT Remote)
{
   OUTMESS OutMessage;
   ULONG BytesWritten, i;

   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* Load up the structure */
   if((i = MasterHeader (OutMessage.Buffer.OutMessage + PREIDLEN,
                         Remote,
                         MASTERLOCKOUTSET,
                         0)) != NORMAL)
      return(i);

   /* Load all the other stuff that is needed */
   OutMessage.Port = Port;
   OutMessage.Remote = Remote;
   OutMessage.Priority = MAXPRIORITY - 1;
   OutMessage.TimeOut = 2;
   OutMessage.OutLength = 4;
   OutMessage.InLength = -1;
   OutMessage.EventCode = RESULT | ENCODED;
   OutMessage.Sequence = 0;
   OutMessage.Retry = 2;


   /* Thats it so send the message */
   return sendToPorter(&OutMessage, BytesWritten);
}


/* Routine to reset the lockout bit in an LCU */
IM_EX_CTIBASE INT LCULockOutReset (USHORT Port, USHORT Remote)
{
   OUTMESS OutMessage;
   ULONG BytesWritten, i;

   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* Load up the structure */
   if((i = MasterHeader (OutMessage.Buffer.OutMessage + PREIDLEN,
                         Remote,
                         MASTERLOCKOUTRESET,
                         0)) != NORMAL)
      return(i);

   /* Load all the other stuff that is needed */
   OutMessage.Port = Port;
   OutMessage.Remote = Remote;
   OutMessage.Priority = MAXPRIORITY - 1;
   OutMessage.TimeOut = 2;
   OutMessage.OutLength = 4;
   OutMessage.InLength = -1;
   OutMessage.EventCode = RESULT | ENCODED;
   OutMessage.Sequence = 0;
   OutMessage.Retry = 2;


   /* Thats it so send the message */
   return sendToPorter(&OutMessage, BytesWritten);
}


/* routine to do a Emetcon 2-way funciton read */
IM_EX_CTIBASE INT QueueFunctionRead (BSTRUCT *BSt, USHORT CCUQFlag)   /* B word structure */
{
   OUTMESS OutMessage;
   ULONG BytesWritten;

   /* if pipe shut down return message */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* No two way till FUBAR cleared by query queue */
   if(FUBAR)
   {
      return(PIPEWASBROKEN);
   }

   /* set as a function read */
   BSt->IO = 3;

   /* Load up the pieces of the structure */
   OutMessage.Port = BSt->Port;
   OutMessage.Remote = BSt->Remote;
   OutMessage.Priority = 10; // (UCHAR)BSt->Priority;          // FIX FIX FIX CGP 080701
   OutMessage.Retry = 2; // BSt->Retry;                        // FIX FIX FIX CGP 080701

   // OutMessage.Sequence = (USHORT)BSt->Sequence = (USHORT)Sequence++;

   if(Sequence == 0 || Sequence & 0x8000)
      Sequence = 1;


   if(CCUQFlag == TRUE)
   {
      /* send it out queued into CCU */
      OutMessage.EventCode = BWORD | NOWAIT | RESULT;
   }
   else
   {
      /* send it out DTRAN */
      OutMessage.EventCode = BWORD | NOWAIT | RESULT | DTRAN;
   }

   OutMessage.Buffer.BSt = *BSt;

   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}

/* routine to do a Emetcon 1-way funciton write */
IM_EX_CTIBASE INT QueueFunctionWrite (BSTRUCT *BSt)
{
   ULONG BytesWritten;
   OUTMESS OutMessage;

   /* if pipe shut down return message */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* load the fixed information for this type of operation */
   BSt->IO = 2;        /* this value defines a function write */

   /* Load up the peices of the structure */
   OutMessage.Port = BSt->Port;
   OutMessage.Remote = BSt->Remote;
   OutMessage.Priority = 10; // FIX FIX FIX CGP (UCHAR)BSt->Priority;
   OutMessage.Retry = 2;  // FIX FIX FIX CGP BSt->Retry;
   OutMessage.EventCode = BWORD | NOWAIT | NORESULT | QUEUED;
   OutMessage.Sequence = 0;
   OutMessage.ReturnNexus = NULL;
   OutMessage.SaveNexus  = NULL;

   OutMessage.Buffer.BSt = *BSt;

   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}

IM_EX_CTIBASE INT DecodeDialupData(INMESS *InMessage, DIALUPSTRUCT *DUPst) // Decide what I've got.. and sort it out.
{
   // This is the function which decides how much data to throw away....
   // What to an how to return is a "sub protocol" between this func and M&C

   USHORT   Request;
   USHORT   RequestType;
   INT      retval = NORMAL;


   /*
    *  OK, first we will copy over the request structure
    */
   memcpy(&DUPst->Request, &InMessage->Buffer.DUPSt.DUPRep.ReqSt, sizeof(DIALUPREQUEST));

   switch(InMessage->Buffer.DUPSt.DUPRep.ReqSt.Identity)
   {
       case IDENT_ALPHA_PPLUS:
       case IDENT_ALPHA_A1:
       case IDENT_VECTRON:
       case IDENT_FULCRUM:
       case IDENT_QUANTUM:
       case IDENT_LGS4:
      {
          {
             RWMutexLock::LockGuard  guard(coutMux);
             cout << "**** Checkpoint How did we get here? **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
          }
         break;
      }
/*
   case IDENT_ALPHA_PPLUS:
      {

         AlphaClass72Parts *ptr = (AlphaClass72Parts *)(&InMessage->Buffer.DUPSt.DUPRep.Message);

         RequestType = (USHORT)(InMessage->Buffer.DUPSt.DUPRep.ReqSt.Request[4] & ~ALPHA_NO_COMM_COMMAND);
         Request     = (USHORT)(InMessage->Buffer.DUPSt.DUPRep.ReqSt.Request[0]);

         if(RequestType == ALPHA_READREQ)
         {
            DUPst->IResponse[DIAL_I_ACKNAK_STATUS ] = InMessage->Buffer.DUPSt.DUPRep.AckNak;
            DUPst->IResponse[DIAL_I_METER_STATUS  ] = InMessage->Buffer.DUPSt.DUPRep.Status;
            DUPst->IResponse[DIAL_I_COMP_STATE    ] = InMessage->Buffer.DUPSt.DUPRep.CompFlag;
            DUPst->IResponse[DIAL_I_CLASS_NUMBER  ] = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1];

            DUPst->IResponse[DIAL_I_CUMOUT        ] = ptr->Real.CUMOUT;
            DUPst->IResponse[DIAL_I_CUMDR         ] = ptr->Real.CUMDR;
            DUPst->IResponse[DIAL_I_SEARAT        ] = ptr->Real.SEARAT;
            DUPst->IResponse[DIAL_I_SYSTAT        ] = ptr->Real.SYSTAT;
            DUPst->IResponse[DIAL_I_DATE_YEAR     ] = ptr->Real.TD.Year;
            DUPst->IResponse[DIAL_I_DATE_MONTH    ] = ptr->Real.TD.Month;
            DUPst->IResponse[DIAL_I_DATE_DAY      ] = ptr->Real.TD.Day;
            DUPst->IResponse[DIAL_I_DATE_HOUR     ] = ptr->Real.TD.Hour;
            DUPst->IResponse[DIAL_I_DATE_MINUTE   ] = ptr->Real.TD.Minute;
            DUPst->IResponse[DIAL_I_DATE_SECOND   ] = ptr->Real.SECONDS;

            DUPst->IResponse[DIAL_I_BLK1_PKA_YEAR ] = ptr->Real.ATD1.Year  ;
            DUPst->IResponse[DIAL_I_BLK1_PKA_MONTH] = ptr->Real.ATD1.Month ;
            DUPst->IResponse[DIAL_I_BLK1_PKA_DAY  ] = ptr->Real.ATD1.Day   ;
            DUPst->IResponse[DIAL_I_BLK1_PKA_HR   ] = ptr->Real.ATD1.Hour  ;
            DUPst->IResponse[DIAL_I_BLK1_PKA_MN   ] = ptr->Real.ATD1.Minute;
            DUPst->IResponse[DIAL_I_BLK2_PKA_YEAR ] = ptr->Real.ATD2.Year  ;
            DUPst->IResponse[DIAL_I_BLK2_PKA_MONTH] = ptr->Real.ATD2.Month ;
            DUPst->IResponse[DIAL_I_BLK2_PKA_DAY  ] = ptr->Real.ATD2.Day   ;
            DUPst->IResponse[DIAL_I_BLK2_PKA_HR   ] = ptr->Real.ATD2.Hour  ;
            DUPst->IResponse[DIAL_I_BLK2_PKA_MN   ] = ptr->Real.ATD2.Minute;
            DUPst->IResponse[DIAL_I_BLK1_PKB_YEAR ] = ptr->Real.BTD1.Year  ;
            DUPst->IResponse[DIAL_I_BLK1_PKB_MONTH] = ptr->Real.BTD1.Month ;
            DUPst->IResponse[DIAL_I_BLK1_PKB_DAY  ] = ptr->Real.BTD1.Day   ;
            DUPst->IResponse[DIAL_I_BLK1_PKB_HR   ] = ptr->Real.BTD1.Hour  ;
            DUPst->IResponse[DIAL_I_BLK1_PKB_MN   ] = ptr->Real.BTD1.Minute;
            DUPst->IResponse[DIAL_I_BLK2_PKB_YEAR ] = ptr->Real.BTD2.Year  ;
            DUPst->IResponse[DIAL_I_BLK2_PKB_MONTH] = ptr->Real.BTD2.Month ;
            DUPst->IResponse[DIAL_I_BLK2_PKB_DAY  ] = ptr->Real.BTD2.Day   ;
            DUPst->IResponse[DIAL_I_BLK2_PKB_HR   ] = ptr->Real.BTD2.Hour  ;
            DUPst->IResponse[DIAL_I_BLK2_PKB_MN   ] = ptr->Real.BTD2.Minute;
            DUPst->IResponse[DIAL_I_BLK1_PKC_YEAR ] = ptr->Real.CTD1.Year  ;
            DUPst->IResponse[DIAL_I_BLK1_PKC_MONTH] = ptr->Real.CTD1.Month ;
            DUPst->IResponse[DIAL_I_BLK1_PKC_DAY  ] = ptr->Real.CTD1.Day   ;
            DUPst->IResponse[DIAL_I_BLK1_PKC_HR   ] = ptr->Real.CTD1.Hour  ;
            DUPst->IResponse[DIAL_I_BLK1_PKC_MN   ] = ptr->Real.CTD1.Minute;
            DUPst->IResponse[DIAL_I_BLK2_PKC_YEAR ] = ptr->Real.CTD2.Year  ;
            DUPst->IResponse[DIAL_I_BLK2_PKC_MONTH] = ptr->Real.CTD2.Month ;
            DUPst->IResponse[DIAL_I_BLK2_PKC_DAY  ] = ptr->Real.CTD2.Day   ;
            DUPst->IResponse[DIAL_I_BLK2_PKC_HR   ] = ptr->Real.CTD2.Hour  ;
            DUPst->IResponse[DIAL_I_BLK2_PKC_MN   ] = ptr->Real.CTD2.Minute;
            DUPst->IResponse[DIAL_I_BLK1_PKD_YEAR ] = ptr->Real.DTD1.Year  ;
            DUPst->IResponse[DIAL_I_BLK1_PKD_MONTH] = ptr->Real.DTD1.Month ;
            DUPst->IResponse[DIAL_I_BLK1_PKD_DAY  ] = ptr->Real.DTD1.Day   ;
            DUPst->IResponse[DIAL_I_BLK1_PKD_HR   ] = ptr->Real.DTD1.Hour  ;
            DUPst->IResponse[DIAL_I_BLK1_PKD_MN   ] = ptr->Real.DTD1.Minute;
            DUPst->IResponse[DIAL_I_BLK2_PKD_YEAR ] = ptr->Real.DTD2.Year  ;
            DUPst->IResponse[DIAL_I_BLK2_PKD_MONTH] = ptr->Real.DTD2.Month ;
            DUPst->IResponse[DIAL_I_BLK2_PKD_DAY  ] = ptr->Real.DTD2.Day   ;
            DUPst->IResponse[DIAL_I_BLK2_PKD_HR   ] = ptr->Real.DTD2.Hour  ;
            DUPst->IResponse[DIAL_I_BLK2_PKD_MN   ] = ptr->Real.DTD2.Minute;

            DUPst->FResponse[DIAL_F_AVGPF         ] = ptr->Real.EAVGPF;
            DUPst->FResponse[DIAL_F_ETKWH1        ] = ptr->Real.ETKWH1;
            DUPst->FResponse[DIAL_F_AKW1          ] = ptr->Real.AKW1 ;
            DUPst->FResponse[DIAL_F_BKW1          ] = ptr->Real.BKW1 ;
            DUPst->FResponse[DIAL_F_CKW1          ] = ptr->Real.CKW1 ;
            DUPst->FResponse[DIAL_F_DKW1          ] = ptr->Real.DKW1 ;
            DUPst->FResponse[DIAL_F_AKWH1         ] = ptr->Real.AKWH1;
            DUPst->FResponse[DIAL_F_BKWH1         ] = ptr->Real.BKWH1;
            DUPst->FResponse[DIAL_F_CKWH1         ] = ptr->Real.CKWH1;
            DUPst->FResponse[DIAL_F_DKWH1         ] = ptr->Real.DKWH1;
            DUPst->FResponse[DIAL_F_BLK1          ] = ptr->Real.BLK1;
            DUPst->FResponse[DIAL_F_ETKWH2        ] = ptr->Real.ETKWH2;
            DUPst->FResponse[DIAL_F_AKW2          ] = ptr->Real.AKW2 ;
            DUPst->FResponse[DIAL_F_BKW2          ] = ptr->Real.BKW2 ;
            DUPst->FResponse[DIAL_F_CKW2          ] = ptr->Real.CKW2 ;
            DUPst->FResponse[DIAL_F_DKW2          ] = ptr->Real.DKW2 ;
            DUPst->FResponse[DIAL_F_AKWH2         ] = ptr->Real.AKWH2;
            DUPst->FResponse[DIAL_F_BKWH2         ] = ptr->Real.BKWH2;
            DUPst->FResponse[DIAL_F_CKWH2         ] = ptr->Real.CKWH2;
            DUPst->FResponse[DIAL_F_DKWH2         ] = ptr->Real.DKWH2;
            DUPst->FResponse[DIAL_F_BLK2          ] = ptr->Real.BLK2;
            DUPst->FResponse[DIAL_F_AKWCUM1       ] = ptr->Real.AKWCUM1;
            DUPst->FResponse[DIAL_F_BKWCUM1       ] = ptr->Real.BKWCUM1;
            DUPst->FResponse[DIAL_F_CKWCUM1       ] = ptr->Real.CKWCUM1;
            DUPst->FResponse[DIAL_F_DKWCUM1       ] = ptr->Real.DKWCUM1;
            DUPst->FResponse[DIAL_F_EKVARH1       ] = ptr->Real.EKVARH1;
            DUPst->FResponse[DIAL_F_AKWC1         ] = ptr->Real.AKWC1  ;
            DUPst->FResponse[DIAL_F_BKWC1         ] = ptr->Real.BKWC1  ;
            DUPst->FResponse[DIAL_F_CKWC1         ] = ptr->Real.CKWC1  ;
            DUPst->FResponse[DIAL_F_DKWC1         ] = ptr->Real.DKWC1  ;
            DUPst->FResponse[DIAL_F_EKVARH2       ] = ptr->Real.EKVARH2;
            DUPst->FResponse[DIAL_F_AKWCUM2       ] = ptr->Real.AKWCUM2;
            DUPst->FResponse[DIAL_F_BKWCUM2       ] = ptr->Real.BKWCUM2;
            DUPst->FResponse[DIAL_F_CKWCUM2       ] = ptr->Real.CKWCUM2;
            DUPst->FResponse[DIAL_F_DKWCUM2       ] = ptr->Real.DKWCUM2;
            DUPst->FResponse[DIAL_F_EKVARH3       ] = ptr->Real.EKVARH3;
            DUPst->FResponse[DIAL_F_AKWC2         ] = ptr->Real.AKWC2  ;
            DUPst->FResponse[DIAL_F_BKWC2         ] = ptr->Real.BKWC2  ;
            DUPst->FResponse[DIAL_F_CKWC2         ] = ptr->Real.CKWC2  ;
            DUPst->FResponse[DIAL_F_DKWC2         ] = ptr->Real.DKWC2  ;
            DUPst->FResponse[DIAL_F_EKVARH4       ] = ptr->Real.EKVARH4;
         }
         break;
      }

      case IDENT_FULCRUM:
//   case IDENT_SCHLUMBERGER:
      {
         FulcrumScanData_t  *ptr = (FulcrumScanData_t *) (&InMessage->Buffer.DUPSt.DUPRep.Message);

         if(InMessage->Buffer.DUPSt.DUPRep.CompFlag & DIALUP_COMP_SUCCESS)
         {
            DUPst->IResponse[DIAL_I_ACKNAK_STATUS ] = InMessage->Buffer.DUPSt.DUPRep.AckNak;
            DUPst->IResponse[DIAL_I_METER_STATUS  ] = InMessage->Buffer.DUPSt.DUPRep.Status;
            DUPst->IResponse[DIAL_I_COMP_STATE    ] = InMessage->Buffer.DUPSt.DUPRep.CompFlag;
            DUPst->IResponse[DIAL_I_CLASS_NUMBER  ] = 0;

            DUPst->IResponse[DIAL_I_CUMOUT        ] = ptr->OutageCount;
            DUPst->IResponse[DIAL_I_CUMDR         ] = ptr->DemandResetCount;
            DUPst->IResponse[DIAL_I_SEARAT        ] = 0;
            DUPst->IResponse[DIAL_I_SYSTAT        ] = ptr->ActivePhaseStatus;
            DUPst->IResponse[DIAL_I_DATE_YEAR     ] = ptr->TimeDate.Year;
            DUPst->IResponse[DIAL_I_DATE_MONTH    ] = ptr->TimeDate.Month;
            DUPst->IResponse[DIAL_I_DATE_DAY      ] = ptr->TimeDate.DayOfMonth;
            DUPst->IResponse[DIAL_I_DATE_HOUR     ] = ptr->TimeDate.Hours;
            DUPst->IResponse[DIAL_I_DATE_MINUTE   ] = ptr->TimeDate.Minutes;
            DUPst->IResponse[DIAL_I_DATE_SECOND   ] = ptr->TimeDate.Seconds;

            DUPst->IResponse[DIAL_I_BLK1_PKA_YEAR ] = ptr->WattsDemand.A_Maximum.Year  ;
            DUPst->IResponse[DIAL_I_BLK1_PKA_MONTH] = ptr->WattsDemand.A_Maximum.Month ;
            DUPst->IResponse[DIAL_I_BLK1_PKA_DAY  ] = ptr->WattsDemand.A_Maximum.Day   ;
            DUPst->IResponse[DIAL_I_BLK1_PKA_HR   ] = ptr->WattsDemand.A_Maximum.Hour  ;
            DUPst->IResponse[DIAL_I_BLK1_PKA_MN   ] = ptr->WattsDemand.A_Maximum.Minute;
            DUPst->IResponse[DIAL_I_BLK2_PKA_YEAR ] = ptr->VADemand.A_Maximum.Year  ;
            DUPst->IResponse[DIAL_I_BLK2_PKA_MONTH] = ptr->VADemand.A_Maximum.Month ;
            DUPst->IResponse[DIAL_I_BLK2_PKA_DAY  ] = ptr->VADemand.A_Maximum.Day   ;
            DUPst->IResponse[DIAL_I_BLK2_PKA_HR   ] = ptr->VADemand.A_Maximum.Hour  ;
            DUPst->IResponse[DIAL_I_BLK2_PKA_MN   ] = ptr->VADemand.A_Maximum.Minute;
            DUPst->IResponse[DIAL_I_BLK1_PKB_YEAR ] = ptr->WattsDemand.B_Maximum.Year  ;
            DUPst->IResponse[DIAL_I_BLK1_PKB_MONTH] = ptr->WattsDemand.B_Maximum.Month ;
            DUPst->IResponse[DIAL_I_BLK1_PKB_DAY  ] = ptr->WattsDemand.B_Maximum.Day   ;
            DUPst->IResponse[DIAL_I_BLK1_PKB_HR   ] = ptr->WattsDemand.B_Maximum.Hour  ;
            DUPst->IResponse[DIAL_I_BLK1_PKB_MN   ] = ptr->WattsDemand.B_Maximum.Minute;
            DUPst->IResponse[DIAL_I_BLK2_PKB_YEAR ] = ptr->VADemand.B_Maximum.Year  ;
            DUPst->IResponse[DIAL_I_BLK2_PKB_MONTH] = ptr->VADemand.B_Maximum.Month ;
            DUPst->IResponse[DIAL_I_BLK2_PKB_DAY  ] = ptr->VADemand.B_Maximum.Day   ;
            DUPst->IResponse[DIAL_I_BLK2_PKB_HR   ] = ptr->VADemand.B_Maximum.Hour  ;
            DUPst->IResponse[DIAL_I_BLK2_PKB_MN   ] = ptr->VADemand.B_Maximum.Minute;
            DUPst->IResponse[DIAL_I_BLK1_PKC_YEAR ] = ptr->WattsDemand.C_Maximum.Year  ;
            DUPst->IResponse[DIAL_I_BLK1_PKC_MONTH] = ptr->WattsDemand.C_Maximum.Month ;
            DUPst->IResponse[DIAL_I_BLK1_PKC_DAY  ] = ptr->WattsDemand.C_Maximum.Day   ;
            DUPst->IResponse[DIAL_I_BLK1_PKC_HR   ] = ptr->WattsDemand.C_Maximum.Hour  ;
            DUPst->IResponse[DIAL_I_BLK1_PKC_MN   ] = ptr->WattsDemand.C_Maximum.Minute;
            DUPst->IResponse[DIAL_I_BLK2_PKC_YEAR ] = ptr->VADemand.C_Maximum.Year  ;
            DUPst->IResponse[DIAL_I_BLK2_PKC_MONTH] = ptr->VADemand.C_Maximum.Month ;
            DUPst->IResponse[DIAL_I_BLK2_PKC_DAY  ] = ptr->VADemand.C_Maximum.Day   ;
            DUPst->IResponse[DIAL_I_BLK2_PKC_HR   ] = ptr->VADemand.C_Maximum.Hour  ;
            DUPst->IResponse[DIAL_I_BLK2_PKC_MN   ] = ptr->VADemand.C_Maximum.Minute;
            DUPst->IResponse[DIAL_I_BLK1_PKD_YEAR ] = ptr->WattsDemand.D_Maximum.Year  ;
            DUPst->IResponse[DIAL_I_BLK1_PKD_MONTH] = ptr->WattsDemand.D_Maximum.Month ;
            DUPst->IResponse[DIAL_I_BLK1_PKD_DAY  ] = ptr->WattsDemand.D_Maximum.Day   ;
            DUPst->IResponse[DIAL_I_BLK1_PKD_HR   ] = ptr->WattsDemand.D_Maximum.Hour  ;
            DUPst->IResponse[DIAL_I_BLK1_PKD_MN   ] = ptr->WattsDemand.D_Maximum.Minute;
            DUPst->IResponse[DIAL_I_BLK2_PKD_YEAR ] = ptr->VADemand.D_Maximum.Year  ;
            DUPst->IResponse[DIAL_I_BLK2_PKD_MONTH] = ptr->VADemand.D_Maximum.Month ;
            DUPst->IResponse[DIAL_I_BLK2_PKD_DAY  ] = ptr->VADemand.D_Maximum.Day   ;
            DUPst->IResponse[DIAL_I_BLK2_PKD_HR   ] = ptr->VADemand.D_Maximum.Hour  ;
            DUPst->IResponse[DIAL_I_BLK2_PKD_MN   ] = ptr->VADemand.D_Maximum.Minute;

            DUPst->FResponse[DIAL_F_AVGPF         ] = ptr->PowerFactor.AVGPF;
            DUPst->FResponse[DIAL_F_ETKWH1        ] = ptr->Watthour.Energy.RateE_Energy + ptr->Watthour.RateE_IntEnergy;
            DUPst->FResponse[DIAL_F_AKW1          ] = ptr->WattsDemand.A_Maximum.PeakValue;
            DUPst->FResponse[DIAL_F_BKW1          ] = ptr->WattsDemand.B_Maximum.PeakValue;
            DUPst->FResponse[DIAL_F_CKW1          ] = ptr->WattsDemand.C_Maximum.PeakValue;
            DUPst->FResponse[DIAL_F_DKW1          ] = ptr->WattsDemand.D_Maximum.PeakValue;
            DUPst->FResponse[DIAL_F_AKWH1         ] = ptr->Watthour.Energy.RateA_Energy;
            DUPst->FResponse[DIAL_F_BKWH1         ] = ptr->Watthour.Energy.RateB_Energy;
            DUPst->FResponse[DIAL_F_CKWH1         ] = ptr->Watthour.Energy.RateC_Energy;
            DUPst->FResponse[DIAL_F_DKWH1         ] = ptr->Watthour.Energy.RateD_Energy;
            DUPst->FResponse[DIAL_F_BLK1          ] = ptr->WattsDemand.Instantaneous;
            DUPst->FResponse[DIAL_F_ETKWH2        ] = ptr->VAhour.Energy.RateE_Energy + ptr->VAhour.RateE_IntEnergy;;
            DUPst->FResponse[DIAL_F_AKW2          ] = ptr->VADemand.A_Maximum.PeakValue;
            DUPst->FResponse[DIAL_F_BKW2          ] = ptr->VADemand.B_Maximum.PeakValue;
            DUPst->FResponse[DIAL_F_CKW2          ] = ptr->VADemand.C_Maximum.PeakValue;
            DUPst->FResponse[DIAL_F_DKW2          ] = ptr->VADemand.D_Maximum.PeakValue;
            DUPst->FResponse[DIAL_F_AKWH2         ] = ptr->VAhour.Energy.RateA_Energy;
            DUPst->FResponse[DIAL_F_BKWH2         ] = ptr->VAhour.Energy.RateB_Energy;
            DUPst->FResponse[DIAL_F_CKWH2         ] = ptr->VAhour.Energy.RateC_Energy;
            DUPst->FResponse[DIAL_F_DKWH2         ] = ptr->VAhour.Energy.RateD_Energy;
            DUPst->FResponse[DIAL_F_BLK2          ] = ptr->VADemand.Instantaneous;
            DUPst->FResponse[DIAL_F_AKWCUM1       ] = ptr->WattsDemand.A_Cumulative;
            DUPst->FResponse[DIAL_F_BKWCUM1       ] = ptr->WattsDemand.B_Cumulative;
            DUPst->FResponse[DIAL_F_CKWCUM1       ] = ptr->WattsDemand.C_Cumulative;
            DUPst->FResponse[DIAL_F_DKWCUM1       ] = ptr->WattsDemand.D_Cumulative;
            DUPst->FResponse[DIAL_F_EKVARH1       ] = 0.0;
            DUPst->FResponse[DIAL_F_AKWC1         ] = ptr->WattsDemand.A_ContinuousCumulative;
            DUPst->FResponse[DIAL_F_BKWC1         ] = ptr->WattsDemand.B_ContinuousCumulative;
            DUPst->FResponse[DIAL_F_CKWC1         ] = ptr->WattsDemand.C_ContinuousCumulative;
            DUPst->FResponse[DIAL_F_DKWC1         ] = ptr->WattsDemand.D_ContinuousCumulative;
            DUPst->FResponse[DIAL_F_EKVARH2       ] = 0.0;
            DUPst->FResponse[DIAL_F_AKWCUM2       ] = ptr->VADemand.A_Cumulative;
            DUPst->FResponse[DIAL_F_BKWCUM2       ] = ptr->VADemand.B_Cumulative;
            DUPst->FResponse[DIAL_F_CKWCUM2       ] = ptr->VADemand.C_Cumulative;
            DUPst->FResponse[DIAL_F_DKWCUM2       ] = ptr->VADemand.D_Cumulative;
            DUPst->FResponse[DIAL_F_EKVARH3       ] = 0.0;
            DUPst->FResponse[DIAL_F_AKWC2         ] = ptr->VADemand.A_ContinuousCumulative;
            DUPst->FResponse[DIAL_F_BKWC2         ] = ptr->VADemand.B_ContinuousCumulative;
            DUPst->FResponse[DIAL_F_CKWC2         ] = ptr->VADemand.C_ContinuousCumulative;
            DUPst->FResponse[DIAL_F_DKWC2         ] = ptr->VADemand.D_ContinuousCumulative;
            DUPst->FResponse[DIAL_F_EKVARH4       ] = 0.0;

         }
         break;
      }
*/

   default:
      retval = NOTNORMAL;
      break;
   }

   return retval;
}

/* Routine to execute a versacom message */
IM_EX_CTIBASE INT nfpexec ( USHORT Function, USHORT PointOffset, DEVICE * DeviceRecord )       /* Fisher Pierce structure */
{
   ULONG         BytesWritten;
   OUTMESS       OutMessage;
   FPSTRUCT      FPSt;
   ROUTE         RouteRecord;
   REMOTE        RemoteRecord;
   ULONG         SerialNumber = DeviceRecord->Address;

   memcpy(RouteRecord.RouteName, DeviceRecord->RouteName[0], STANDNAMLEN);

#if OLD_CRAP
   if(!RoutegetEqual(&RouteRecord))
   {
      memcpy(RemoteRecord.RemoteName, RouteRecord.RemoteName, STANDNAMLEN);
      if(RemotegetEqual(&RemoteRecord))
      {
         // I don't know nuthin'
         //printf("%s failed to find remote  <%.20s>\n", __FUNCTION__, RemoteRecord.RemoteName);
         return(!NORMAL);
      }
   }
#else
   if(1)
   {
      memcpy(RemoteRecord.RemoteName, RouteRecord.RemoteName, STANDNAMLEN);
      printf("FIX IFX FIX CGP\n");
   }
#endif
   else
   {
      // I don't know nuthin'
      //printf("%s failed to find route <%.20s>\n", __FUNCTION__, RouteRecord.RouteName);
      return(NOTNORMAL);
   }

   /* if pipe shut down return the error */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /*
    * This is a bit odd v. the std. calls, we build the FPSt here....
    */

   memset(&FPSt,'0',sizeof(FPSTRUCT));


   if(Function == OPENED && PointOffset == 1)
   {
      FPSt.u.PCC.F[0] = '0';    /* Trip command */
      FPSt.u.PCC.F[1] = '2';
   }
   else if(Function == CLOSED && PointOffset == 1)     /* Must be closed */
   {
      FPSt.u.PCC.F[0] = '0';    /* Close command */
      FPSt.u.PCC.F[1] = '3';
   }
   else if(Function == OPENED && PointOffset == 2)     /* Must be Enable Voltage */
   {
      FPSt.u.PCC.F[0] = '0';    /* Close command */
      FPSt.u.PCC.F[1] = '6';
   }
   else if(Function == CLOSED && PointOffset == 2)     /* Must be Disable Voltage*/
   {
      FPSt.u.PCC.F[0] = '0';    /* Close command */
      FPSt.u.PCC.F[1] = '7';
   }
   else if(Function == OPENED && PointOffset == 100)     /* Group Must be Enable Voltage */
   {
      FPSt.u.PCC.F[0] = '0';    /* Close command */
      FPSt.u.PCC.F[1] = '6';
   }
   else if(Function == CLOSED && PointOffset == 100)     /* Group Must be Disable Voltage*/
   {
      FPSt.u.PCC.F[0] = '0';    /* Close command */
      FPSt.u.PCC.F[1] = '7';
   }
   else
   {
      FPSt.u.PCC.F[0] = '0';    /* Test, NO-OP command */
      FPSt.u.PCC.F[1] = '1';
   }


   if(PointOffset == 100)
   {
      /* this is how we tell it to do a group command
         We stuffed a group address into the address field */
      sprintf((CHAR*)FPSt.u.PCC.GRP, "%04ld", SerialNumber);
      /* printf("Function = %d, Grp:%4ld", Function, SerialNumber); */
   }
   else
   {
      /* Group Must be Disable Voltage*/
      sprintf((CHAR*)FPSt.u.PCC.ADDRS, "%07ld", SerialNumber);
      /* printf("Function = %d, SN:%7ld", Function, SerialNumber); */
   }

   // printf("Remote Port = %d Address = %d\n",RemoteRecord.Port, RemoteRecord.Remote);

   /* Load up the pieces of the structure */
   OutMessage.Port              = RemoteRecord.Port;
   OutMessage.Remote            = RemoteRecord.Remote;
   OutMessage.Priority          = 9;                 /* This is FBONEWAYPRIORITY */
   OutMessage.Retry             = 2;
   OutMessage.EventCode         = NOWAIT | NORESULT | ENCODED | FISHERPIERCE;
   OutMessage.Sequence          = 0;
   OutMessage.ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
   OutMessage.SaveNexus.NexusState = CTINEXUS_STATE_NULL;

   OutMessage.Buffer.FPSt       = FPSt;                         /* This is a copy OK,.... */

   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}


/* Routine to execute a ripple message */
IM_EX_CTIBASE INT nrexec (RSTRUCT *RSt)       /* Ripple structure */
{
   ULONG BytesWritten;
   OUTMESS OutMessage;

   /* if pipe shut down return the error */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* Load up the pieces of the structure */
   OutMessage.Port = RSt->Port;
   OutMessage.Remote = RSt->Remote;
   OutMessage.Priority = (UCHAR)RSt->Priority;
   OutMessage.Retry = RSt->Retry;
   OutMessage.EventCode = RIPPLE | NOWAIT | NORESULT;
   OutMessage.Sequence = 0;
   OutMessage.ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
   OutMessage.SaveNexus.NexusState  = CTINEXUS_STATE_NULL;

   OutMessage.Buffer.RSt = *RSt;

   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}


/* Routine to execute a versacom message */
IM_EX_CTIBASE INT nvexec (VSTRUCT *VSt)       /* Ripple structure */
{
   ULONG BytesWritten;
   OUTMESS OutMessage;

   /* if pipe shut down return the error */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* Load up the pieces of the structure */
   OutMessage.Port = VSt->Port;
   OutMessage.Remote = VSt->Remote;
   OutMessage.Priority = (UCHAR)VSt->Priority;
   OutMessage.Retry = VSt->Retry;
   OutMessage.EventCode = VERSACOM | NOWAIT | NORESULT | QUEUED;
   OutMessage.Sequence = 0;
   OutMessage.ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
   OutMessage.SaveNexus.NexusState  = CTINEXUS_STATE_NULL;

   OutMessage.Buffer.VSt = *VSt;

   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}


/* A word execute routine */
IM_EX_CTIBASE INT naexec (ASTRUCT *ASt)       /* structure for A word */
{
   ULONG BytesWritten;
   OUTMESS OutMessage;

   /* if pipe shut down return message */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* Load up the peices of the structure */
   OutMessage.Port = ASt->Port;
   OutMessage.Remote = ASt->Remote;
   OutMessage.Priority = (UCHAR)ASt->Priority;
   OutMessage.Retry = ASt->Retry;
   OutMessage.EventCode = AWORD | ACTIN | NOWAIT | NORESULT;
   OutMessage.Sequence = 0;
   OutMessage.ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
   OutMessage.SaveNexus.NexusState  = CTINEXUS_STATE_NULL;

   OutMessage.Buffer.ASt = *ASt;

   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}


/* B word write or function (1 way) execute routine*/
IM_EX_CTIBASE INT nb1exec (BSTRUCT *BSt)
{
   ULONG BytesWritten;
   OUTMESS OutMessage;

   /* if pipe shut down return message */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* load the fixed information for this type of operation */
   BSt->IO = 0;

   /* Load up the peices of the structure */
   OutMessage.Port = BSt->Port;
   OutMessage.Remote = BSt->Remote;
   OutMessage.Priority = (UCHAR)BSt->Priority;
   OutMessage.Retry = BSt->Retry;
   OutMessage.EventCode = BWORD | NOWAIT | NORESULT | QUEUED;
   OutMessage.Sequence = 0;
   OutMessage.ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
   OutMessage.SaveNexus.NexusState  = CTINEXUS_STATE_NULL;

   OutMessage.Buffer.BSt = *BSt;

   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}


/* B word read (2 way) execute routine */
IM_EX_CTIBASE INT nb2exec (BSTRUCT *BSt,    /* B word structure */
                        DSTRUCT *DSt)    /* D word structure */
{
   ULONG i;
   INMESS *InMessage;
   OUTMESS OutMessage;
   ULONG BytesWritten;
   REQUESTDATA ReadResult;
   BYTE ReadPriority;
   ULONG ReadLength;
   ULONG QueueElements;

   /* if pipe shut down return message */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* No two way till FUBAR cleared by call to queue query */
   if(FUBAR)
   {
      return(PIPEWASBROKEN);
   }

   /* check for a function read */
   if(BSt->Length == 9 &&
      (BSt->Function == 0x90 ||
       BSt->Function == 0x91))
   {
      BSt->IO = 3;
   }
   else
   {
      BSt->IO = 1;
   }

   /* Load up the peices of the structure */
   OutMessage.Port = BSt->Port;
   OutMessage.Remote = BSt->Remote;
   OutMessage.Priority = (UCHAR)BSt->Priority;
   OutMessage.Retry = BSt->Retry;
   OutMessage.Sequence = BSt->Sequence = 0;
   OutMessage.EventCode = BWORD | WAIT | RESULT | DTRAN;
   memcpy (OutMessage.DeviceName, BSt->DeviceName, STANDNAMLEN);
   memcpy (OutMessage.PointName, BSt->PointName, STANDNAMLEN);

   OutMessage.Buffer.BSt = *BSt;

   /* And send them to porter */
   sendToPorter(&OutMessage, BytesWritten);

   /* Wait till something is on the return queue or we lose the pipe */
   do
   {
      CTISleep (100L);
      if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
         return(PIPEBROKEN);

      if(FUBAR)
      {
         return(PIPEWASBROKEN);
      }

      QueueElements = 0;
      QueryQueue (MyWaitQueueHandle, &QueueElements);

   } while(!QueueElements);

   /* result on wait queue */
   if(ReadQueue (MyWaitQueueHandle, &ReadResult, &ReadLength, (PVOID FAR *) &InMessage, 0, DCWW_NOWAIT, &ReadPriority))
   {
      return(QUEUE_READ);
   }

   *DSt = InMessage->Buffer.DSt;
   DSt->Sequence = InMessage->Sequence;
   DSt->Time = InMessage->Time;
   if(InMessage->MilliTime & DSTACTIVE)
      DSt->DSTFlag = TRUE;
   else
      DSt->DSTFlag = FALSE;

   i = InMessage->EventCode & ~ENCODED;

   free (InMessage);

   return(i);
}


/* Variable for sequencing queued up commands */
ULONG Sequence = {1};

/* B word queued read command output */
IM_EX_CTIBASE INT nb2execsq (BSTRUCT *BSt)          /* B word structure */
{
   OUTMESS OutMessage;
   ULONG BytesWritten;

   /* if pipe shut down return message */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* No two way till FUBAR cleared by query queue */
   if(FUBAR)
   {
      return(PIPEWASBROKEN);
   }

   /* check for a function read */
   if(BSt->Length == 9 &&
      (BSt->Function == 0x90 ||
       BSt->Function == 0x91))
   {
      BSt->IO = 3;
   }
   else
   {
      BSt->IO = 1;
   }

   /* Load up the peices of the structure */
   OutMessage.Port = BSt->Port;
   OutMessage.Remote = BSt->Remote;
   OutMessage.Priority = (UCHAR)BSt->Priority;
   OutMessage.Retry = BSt->Retry;
   OutMessage.Sequence = (USHORT)BSt->Sequence = (USHORT)Sequence++;
   if(Sequence == 0 || Sequence & 0x8000)
      Sequence = 1;
   OutMessage.EventCode = BWORD | NOWAIT | RESULT;
   memcpy (OutMessage.DeviceName, BSt->DeviceName, STANDNAMLEN);
   memcpy (OutMessage.PointName, BSt->PointName, STANDNAMLEN);

   OutMessage.Buffer.BSt = *BSt;

   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}


IM_EX_CTIBASE INT nb2execs (BSTRUCT *BSt)   /* B word structure */
{
   OUTMESS OutMessage;
   ULONG BytesWritten;

   /* if pipe shut down return message */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* No two way till FUBAR cleared by query queue */
   if(FUBAR)
   {
      return(PIPEWASBROKEN);
   }

   /* check for a function read */
   if(BSt->Length == 9 &&
      (BSt->Function == 0x90 ||
       BSt->Function == 0x91))
   {
      BSt->IO = 3;
   }
   else
   {
      BSt->IO = 1;
   }

   /* Load up the peices of the structure */
   OutMessage.Port = BSt->Port;
   OutMessage.Remote = BSt->Remote;
   OutMessage.Priority = (UCHAR)BSt->Priority;
   OutMessage.Retry = BSt->Retry;
   OutMessage.Sequence = (USHORT)BSt->Sequence = (USHORT)Sequence++;
   if(Sequence == 0 || Sequence & 0x8000)
      Sequence = 1;
   OutMessage.EventCode = BWORD | NOWAIT | RESULT | DTRAN;
   memcpy (OutMessage.DeviceName, BSt->DeviceName, STANDNAMLEN);
   memcpy (OutMessage.PointName, BSt->PointName, STANDNAMLEN);

   OutMessage.Buffer.BSt = *BSt;

   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}


/* Routine to take the top entry off the return queue */
IM_EX_CTIBASE INT nb2execr (DSTRUCT *DSt)
{
   ULONG i;
   INMESS *InMessage;
   REQUESTDATA ReadResult;
   BYTE ReadPriority;
   ULONG ReadLength;

   if(ReadQueue (MyQueuedQueueHandle,
                 &ReadResult,
                 &ReadLength,
                 (PVOID FAR *) &InMessage,
                 0,
                 DCWW_NOWAIT,
                 &ReadPriority))
   {
      return(QUEUE_READ);
   }


   *DSt = InMessage->Buffer.DSt;
   DSt->Sequence = InMessage->Sequence;
   DSt->Time = InMessage->Time;
   if(InMessage->MilliTime & DSTACTIVE)
      DSt->DSTFlag = TRUE;
   else
      DSt->DSTFlag = FALSE;

   i = InMessage->EventCode & ~ENCODED;

   free (InMessage);

   return(i);
}


/* routine to check if any results waiting on queue */
IM_EX_CTIBASE INT nb2query ()
{
   ULONG FarElements = 0;

   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      FarElements = 0xffff;
   }
   else if(FUBAR)
   {
      FarElements = 0xffff;
      FUBAR = FALSE;
   }
   else
   {
      QueryQueue (MyQueuedQueueHandle,
                  &FarElements);
   }

   return(FarElements);
}

/*  (1 way) execute routine*/
IM_EX_CTIBASE INT nd1execx (DIALUPSTRUCT *DUPst)
{
   ULONG BytesWritten;
   OUTMESS OutMessage;

   /* if pipe shut down return message */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* Load up the peices of the structure */
   OutMessage.Port = DUPst->Request.Port;
   OutMessage.Remote = DUPst->Request.Remote;
   OutMessage.Priority = (UCHAR)DUPst->Request.Priority;
   OutMessage.Retry = DUPst->Request.Retry;
   OutMessage.EventCode = NOWAIT | NORESULT | QUEUED | ENCODED;
   OutMessage.Sequence = 0;
   OutMessage.ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
   OutMessage.SaveNexus.NexusState  = CTINEXUS_STATE_NULL;

   memcpy(&OutMessage.Buffer.DUPReq, &DUPst->Request, sizeof(DIALUPREQUEST));

   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}


IM_EX_CTIBASE INT nd2execs (DIALUPSTRUCT *DUPOst)
{
   OUTMESS OutMessage;
   ULONG BytesWritten;

   /* if pipe shut down return message */
   if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      if(PortPipeInit (NOWAIT))
      {
         return(PIPEWASBROKEN);
      }
   }

   /* No two way till FUBAR cleared by query queue */
   if(FUBAR)
   {
      return(PIPEWASBROKEN);
   }

   /* Load up the pieces of the structure */
   OutMessage.Port        = DUPOst->Request.Port;
   OutMessage.Remote      = DUPOst->Request.Remote;
   OutMessage.Priority    = (UCHAR)DUPOst->Request.Priority;
   OutMessage.Retry       = DUPOst->Request.Retry;
   OutMessage.Sequence    = (USHORT)DUPOst->Request.Sequence = (USHORT)Sequence++;

   if(Sequence == 0 || Sequence & 0x8000) Sequence = 1;

   OutMessage.EventCode   = NOWAIT | RESULT | ENCODED;

   memcpy (OutMessage.DeviceName, DUPOst->Request.DeviceName, STANDNAMLEN);
   memcpy (OutMessage.PointName , DUPOst->Request.PointName , STANDNAMLEN);
   memcpy (&(OutMessage.Buffer.DUPReq), &DUPOst->Request, sizeof(DIALUPREQUEST));       /* Point back at the source for all else */


   /* And send them to porter */
   return sendToPorter(&OutMessage, BytesWritten);
}

/* Routine to take the top entry off the return queue */
IM_EX_CTIBASE INT nd2execr (DSTRUCT *DSt, DIALUPSTRUCT *DUPst)
{
   ULONG         i;
   INMESS        *InMessage;
   REQUESTDATA   ReadResult;
   BYTE          ReadPriority;
   ULONG         ReadLength;

   if(ReadQueue (MyQueuedQueueHandle, &ReadResult, &ReadLength, (PVOID FAR *) &InMessage, 0, DCWW_NOWAIT, &ReadPriority))
   {
      return(QUEUE_READ);
   }

   DecodeDialupData(InMessage, DUPst); // Decide what I've got, what I asked for.. and sort it out.

   *DSt   = InMessage->Buffer.DSt; /* This is in theory a memcpy of the structure.... */

   DSt->Sequence = InMessage->Sequence;
   DSt->Time = InMessage->Time;
   if(InMessage->MilliTime & DSTACTIVE)
      DSt->DSTFlag = TRUE;
   else
      DSt->DSTFlag = FALSE;

   i = InMessage->EventCode & ~ENCODED;

   free (InMessage);

   return(i);
}

#endif
