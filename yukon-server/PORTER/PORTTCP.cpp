#pragma warning (disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   PORTTCP
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTTCP.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:19 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma title ( "Porter TCP/IP Interface Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PORTPORT.C

    Purpose:
        Provide a TCP/IP interface for port sharing over TCP/IP

    The following procedures are contained in this module:

    Initial Date:
        11/23/93

    Revision History:
    2-23-98 Added Inbound ESCA/Welco support                WRO


   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"
#include "cti_asmc.h"

#include <stdlib.h>
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>
#include <malloc.h>

/* TCP/IP Includes */
#include <winsock.h>

/* DSM/2  Include */
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "porttcp.h"
#include "tcpsup.h"

#include "portglob.h"

#include "color.h"
#include "c_port_interface.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "dev_base.h"
#include "port_base.h"

UINT getIPInitFlags(CtiPort *PortRecord);
/*
 *  This int array counts and resets on each failure from an RTU
 *  If the count exceeds 10 actions are taken
 *
 *  In a WelcoTCP installation, DCD is not immediately raised back to the requesting system.
 *  This is an attempt to let the VAX at DEC to know that the RTU should not
 *  be in the fast scan mode and is failed!
 */
static INT ConsecutiveRTUFailures[128];
#define MAX_RTU_FAILS   10

/* Routine to start a TCP/IP interface pair for a given port */
PortTCPIPStart (USHORT PortNumber)
{
   TCPIPSTRUCT *TCPIPStruct;
   ULONG i;

   /* Allocate the memory for the structure */
   if((TCPIPStruct = (TCPIPSTRUCT*)malloc (sizeof (TCPIPSTRUCT))) == NULL)
   {
      return(MEMORY);
   }

   /* Clear up a few things */
   TCPIPStruct->PortNumber = PortNumber;
   TCPIPStruct->TCPIPServerSocket = (int) NULL;
   TCPIPStruct->TCPIPSocket = (int) NULL;
   TCPIPStruct->TCPIPFailed = TRUE;
   TCPIPStruct->ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
   TCPIPStruct->Busy = 0;
   TCPIPStruct->Flags = 0;

   TCPIPStruct->MyPortInfo.LastClose = 0L;
   TCPIPStruct->MyPortInfo.Open = FALSE;
   TCPIPStruct->MyPortInfo.Connected = FALSE;
   TCPIPStruct->MyPortInfo.MyThreadID = -1;
   TCPIPStruct->MyPortInfo.NetCXAltPin = NetCXAltPin;
   TCPIPStruct->MyPortInfo.TerminalSocket = 0;

   /* Create the mutex semaphore to protect the resources */
   CTICreateMutexSem (NULL,
                      &TCPIPStruct->MyPortInfo.PortLockSem,
                      0,
                      0);


   /* Initialize the TCP/IP socket system */

   /* Based on the type crank up the Threads */
   switch(StartTCPIP)
   {
   case TCP_SES92:
      /* Crank up the SES92 TCP/IP in thread for this port */
      if(_beginthread (SES92TCPIPInThread,
                       TCPIPINTHREADSTACKSIZE,
                       (PVOID) TCPIPStruct) == -1)
      {
         printf("Error Starting SES92 TCPIP In Thread:  %2hd\n", PortNumber);
         return(!NORMAL);
      }

      /* Crank up the SES92 TCP/IP Out thread for this port */
      if(_beginthread (SES92TCPIPOutThread,
                       TCPIPOUTTHREADSTACKSIZE,
                       (PVOID) TCPIPStruct) == -1)
      {
         printf("Error Starting SES92 TCPIP Out Thread:  %2hd\n", PortNumber);
         return(!NORMAL);
      }

      break;

   case TCP_CCU710:
      /* Crank up the CCU 710 TCP/IP in thread for this port */
      if(_beginthread (CCU710TCPIPInThread,
                       TCPIPINTHREADSTACKSIZE,
                       (PVOID) TCPIPStruct) == -1)
      {
         printf("Error Starting CCU 710 TCPIP In Thread:  %2hd\n", PortNumber);
         return(!NORMAL);
      }

      /* Crank up the CCU 710 TCP/IP Out thread for this port */
      if(_beginthread (CCU710TCPIPOutThread,
                       TCPIPOUTTHREADSTACKSIZE,
                       (PVOID) TCPIPStruct) == -1)
      {
         printf("Error Starting CCU 710 TCPIP Out Thread:  %2hd\n", PortNumber);
         return(!NORMAL);
      }

      break;

   case TCP_WELCO:
      /* Crank up the ESCA/Welco TCP/IP in thread for this port */
      if(_beginthread (WelcoTCPIPInThread,
                       TCPIPINTHREADSTACKSIZE,
                       (PVOID) TCPIPStruct) == -1)
      {
         printf("Error Starting ESCA/Welco TCPIP In Thread:  %2hd\n", PortNumber);
         return(!NORMAL);
      }

      /* Crank up the ESCA/Welco TCP/IP Out thread for this port */
      if(_beginthread (WelcoTCPIPOutThread,
                       TCPIPOUTTHREADSTACKSIZE,
                       (PVOID) TCPIPStruct) == -1)
      {
         printf("Error Starting ESCA/Welco TCPIP Out Thread:  %2hd\n", PortNumber);
         return(!NORMAL);
      }

      break;

   default:
      printf("Unknown TCP/IP Interface Type:  %2hd\n", PortNumber);
      return(!NORMAL);
   }

   /* Crank up the TCP/IP Check thread for this port */
   if(_beginthread (TCPIPCheckThread,
                    TCPIPCHECKTHREADSTACKSIZE,
                    (PVOID) TCPIPStruct) == -1)
   {
      printf("Error Starting TCPIP Check Thread:  %2hd\n", PortNumber);
      return(!NORMAL);
   }

   /* Should do it */
   return(NORMAL);
}


#define SES92WAITTIME   60

/* Routine to handle SES-92 TCP/IP input stuff */
VOID SES92TCPIPInThread (PVOID Arg)
{
   INT               nRet=0;
   /* A Place for our arguments */
   TCPIPSTRUCT *TCPIPStruct = (TCPIPSTRUCT*)Arg;

   /* TCPIP Stuff */
   int ReceiveLength;
   ULONG ulReceiveLength;
   int OptVal;
   struct sockaddr_in TCPIPServer;

   /* Buffer for TCP/IP Receive */
   CHAR Buffer[300];
   CHAR *pBuffer;

   /* Buffer to generate the block to porter */
   OUTMESS *OutMessage;

   /* Misc Variables */
   ULONG PostCount;

   /* Variables for retries */
   PSZ Environment;
   USHORT CommRetries = 1;

   CTINEXUS    MyNexus;

   printf ("TCP/IP In Thread for %hd Starting as TID:  %ld\n",
           TCPIPStruct->PortNumber,
           CurrentTID ());

   if(!(CTIScanEnv ("TCPIP_COMMRETRIES", &Environment)))
   {
      CommRetries = atoi (Environment);
   }
   /* get a socket for accepting connections. */
   if((TCPIPStruct->TCPIPServerSocket = socket (AF_INET,
                                                SOCK_STREAM,
                                                0)) < 0)
   {
      printf ("Error Creating TCPIP Server Socket Socket\n");
      SocketShutdownClose(__FILE__, __LINE__, &TCPIPStruct->TCPIPServerSocket);
      CTIExit (EXIT_THREAD, WSAGetLastError ());
   }

   /* Allow Local reuse */
   OptVal = 1;
   if(setsockopt (TCPIPStruct->TCPIPServerSocket,
                  SOL_SOCKET,
                  SO_REUSEADDR,
                  (char *) &OptVal,
                  sizeof (OptVal)))
   {
      printf ("Error setting Reuse on TCPIP Server Socket Socket:  %ld\n", WSAGetLastError ());
   }

   /* Bind the socket to the server address. */
   TCPIPServer.sin_family = AF_INET;
   TCPIPServer.sin_port = htons (TCPIPBASESOCKET + (USHORT)TCPIPStruct->PortNumber);
   TCPIPServer.sin_addr.s_addr = INADDR_ANY;

   if(bind (TCPIPStruct->TCPIPServerSocket,
            (struct sockaddr *) &TCPIPServer,
            sizeof (TCPIPServer)) < 0)
   {
      printf ("Error Binding TCPIP Server Socket Socket:  %ld\n", WSAGetLastError ());
      SocketShutdownClose(__FILE__, __LINE__, &TCPIPStruct->TCPIPServerSocket);
      CTIExit (EXIT_THREAD, WSAGetLastError ());
   }

   /* make it clear who is the boss */
   CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, -10, 0);

   /*
    *  This is required so that the PortThread has an existing Socket pair to
    *  write back into.
    *
    *  InThread WILL NOT EVER use this socket connection, only PortThread, and OutThread
    *  PortThread writes into it.
    *  OutThread receives data from it.
    */

   do
   {
      nRet = CTINexusOpen(&MyNexus, "", SES92NEXUS, CTINEXUS_FLAG_READEXACTLY);
   }
   while(nRet);

   /* 3/4/99 This is a synchronization measure.
    * Wait for the nexus to get opened up this also means the CommEvent Semaphore exists
    */

   /* Sit and wait for stuff to come in */
   for(;!PorterQuit;)
   {
      if ( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
      {
         PorterQuit = TRUE;
         continue;
      }

      if(TCPIPStruct->TCPIPFailed == FALSE)
      {
         if(CTIWaitEventSem (TCPIPStruct->CommSem, SES92WAITTIME * 1000L) == NO_ERROR)
         {
            // It was posted from OutThread..  We are free to proceed
            CTIResetEventSem (TCPIPStruct->CommSem, &PostCount);
         }
         else
         {
            printf("Timed out waiting for synchronization flag\n");
            /*
             * I have no idea how long I have been waiting here.
             * I need to flush out the recv buffer on the TS.
             */
            ioctlsocket (TCPIPStruct->TCPIPSocket, FIONREAD, &ulReceiveLength);

            if((pBuffer = (CHAR*)malloc (ulReceiveLength)) != NULL)
            {
               if(recv (TCPIPStruct->TCPIPSocket, pBuffer, ReceiveLength, 0) <= 0)
               {
                  free (pBuffer);
               }
               free (pBuffer);
            }
         }

         ReceiveLength = 0;
         if((ReceiveLength = recv (TCPIPStruct->TCPIPSocket,
                                   Buffer,
                                   sizeof (Buffer),
                                   0)) <= 0)
         {
            printf ("TCPIP Connection Failed:  %ld\n", WSAGetLastError ());

            /* Check if someone else failed it */
            if(TCPIPStruct->TCPIPFailed != TRUE)
            {
               TCPIPStruct->TCPIPFailed = TRUE;
               SocketShutdownClose(__FILE__, __LINE__, &TCPIPStruct->TCPIPSocket);
               TCPIPStruct->MyPortInfo.TerminalSocket = 0;
               TCPIPStruct->TCPIPSocket = (int) NULL;
            }

            continue;
         }

         /* Let the system know we are OK */
         TCPIPStruct->TCPIPFailed = FALSE;

         /* get some memory for the block to porter */
         if((OutMessage = new OUTMESS) == NULL)
         {
            /* Not much we can do */
            printf ("Error Allocating Memory\n");
            continue;
         }

         /* Give the failed sem a kick in the butt */
         CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);

         /* Do this real fast and dirty */
         OutMessage->Port = TCPIPStruct->PortNumber;
         OutMessage->Remote = Buffer[1] >> 1;
         OutMessage->TimeOut = 1;
         OutMessage->Retry = CommRetries;
         OutMessage->Source = 0;
         OutMessage->Destination = 0;
         OutMessage->Sequence = 0;
         OutMessage->Priority = MAXPRIORITY - 1;
         OutMessage->ReturnNexus = TCPIPStruct->ReturnNexus;
         OutMessage->SaveNexus.NexusState = CTINEXUS_STATE_NULL;

         /* Figure out what the out length is */
         OutMessage->OutLength = Buffer[3] - 3;
         OutMessage->InLength = 0;

         /* Now figure out the event code */
         if(OutMessage->Remote != RTUGLOBAL)
         {
            OutMessage->EventCode = RESULT | ENCODED;
            TCPIPStruct->Busy++;
         }
         else
         {
            OutMessage->EventCode = NOWAIT | NORESULT | ENCODED;
         }

         /* Check if this is a time sync */
         if((Buffer[5] & 0x7f) == 0x52)
         {
            OutMessage->EventCode |= TSYNC;
         }

         /* Now Copy the message into the buffer */
         memcpy (OutMessage->Buffer.OutMessage, Buffer, ReceiveLength);

         /* And put it on the queue */
         if(QueueHandle(TCPIPStruct->PortNumber) != NULL)
         {
            if(PortManager.writeQueue(TCPIPStruct->PortNumber,
                           OutMessage->EventCode,
                           sizeof (*OutMessage),
                           (char *) OutMessage,
                           OutMessage->Priority))
            {
               printf ("Error Writing to Queue for Port %2hd\n", TCPIPStruct->PortNumber);
               delete (OutMessage);
            }
         }
         else
         {
            printf ("Invalid Queue Handle for Port:  %2hd\n", TCPIPStruct->PortNumber);
            delete (OutMessage);
         }
      }
      else
      {
         /* We need to establish or reestablish the connection */

         /* Listen for connections. Specify the backlog as 0. */
         if(listen (TCPIPStruct->TCPIPServerSocket, 0) != 0)
         {
            printf ("Error Listening for TCPIP Connection\n");
            SocketShutdownClose(__FILE__, __LINE__, &TCPIPStruct->TCPIPServerSocket);
            CTIExit (EXIT_THREAD, WSAGetLastError ());
         }

         /* Accept a connection. */
         ReceiveLength = sizeof (TCPIPStruct->TCPIPReceiveClient);
         if((TCPIPStruct->TCPIPSocket = accept (TCPIPStruct->TCPIPServerSocket,
                                                (struct sockaddr *) &TCPIPStruct->TCPIPReceiveClient,
                                                &ReceiveLength)) == -1)
         {
            printf ("Error Accepting TCPIP Connection\n");
            SocketShutdownClose(__FILE__, __LINE__, &TCPIPStruct->TCPIPServerSocket);
            SocketShutdownClose(__FILE__, __LINE__, &TCPIPStruct->TCPIPSocket);
            TCPIPStruct->TCPIPSocket = (int) NULL;
            TCPIPStruct->MyPortInfo.TerminalSocket = 0;
            CTIExit (EXIT_THREAD, WSAGetLastError ());
         }

         /* set up to work in blocking mode */
         ULONG ulTemp = 0L;
         if(ioctlsocket (TCPIPStruct->TCPIPSocket,
                         FIONBIO,
                         &ulTemp))
         {
            printf ("Error setting Blocking Mode on TCPIP Socket:  %ld\n", WSAGetLastError ());
         }

         TCPIPStruct->TCPIPFailed = FALSE;
         TCPIPStruct->Busy = 0;
      }
   }
}


/* Routine to handle TCP/IP Output stuff */
VOID SES92TCPIPOutThread (PVOID Arg)
{
   INT   nRet     = 0;

   /* A Place for our arguments */
   TCPIPSTRUCT *TCPIPStruct = (TCPIPSTRUCT *)Arg;

   /* Buffer to get stuff off the pipe */
   CTINEXUS  ListenNexus;

   INMESS InMessage;

   /* Misc Variables */
   ULONG BytesRead;
   ULONG PostCount;


   /* make it clear who is the boss */
   CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, -10, 0);


   printf ("TCP/IP Out Thread for %hd Starting as TID:  %ld\n",
           TCPIPStruct->PortNumber,
           CurrentTID ());

   /*
    *  Create a Semaphore to allow completion on the InPort Side
    *  This semsphore MUST be created and cleared before the
    *  ReturnNexus
    */
   if((CTICreateEventSem (NULL, &TCPIPStruct->CommSem, 0, 0)) != NORMAL)
   {
      fprintf (stderr, "Unable to Create TCPIP CommSemaphore for Port %d\n",TCPIPStruct->PortNumber);
      CTIExit (EXIT_THREAD, -1);
   }
   // Reset it so that it can be posted to
   CTIResetEventSem (TCPIPStruct->CommSem, &PostCount);



   /*
    *  4/8/99 This is the server side of a new Nexus
    *  This thread listens only once and then may shutdown the listener?????
    *
    *  The socket connection created here is then used to communicate to porter
    *    requests in the SES92 protocol!
    *
    */
   strcpy(ListenNexus.Name, "SES92 FSI Connection Server: Listener");

   if(!CTINexusCreate(&ListenNexus, SES92NEXUS))
   {
      fprintf(stderr,"Could not create SES92 Connection Server\n");
      return;
   }

   sprintf(TCPIPStruct->ReturnNexus.Name, "SES92 Nexus");

   /*
    *  Blocking wait on the listening nexus.  Will return a new nexus for the connection
    */
   nRet = CTINexusConnect(&ListenNexus, &(TCPIPStruct->ReturnNexus));

   if(nRet)
   {
      fprintf(stderr,"Error establishing SES92 Nexus to InThread\n");
      return;
   }

   /* Someone has connected to us.. */

   /*
    *  May need to put a thread out there to spawn the remainder of this thread on
    *  failure conditions.
    */
   CTINexusClose(&ListenNexus);  // Don't need this anymore.

   /* Now sit and wait for something to come in */
   for(;!PorterQuit;)
   {
      if ( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
      {
         PorterQuit = TRUE;
         continue;
      }
      /*
       *  3/4/99 CGP Trying to prevent Sequence Problems
       *
       *  The approach is that this MUST post on each pass through the loop.
       *  We will therefore do it immediately prior to CTIRead() call
       */
      CTIPostEventSem(TCPIPStruct->CommSem);

      if(CTINexusRead (&TCPIPStruct->ReturnNexus,
                       &InMessage,
                       sizeof (InMessage),
                       &BytesRead,
                       CTINEXUS_INFINITE_TIMEOUT) || BytesRead == 0)
      {
         /* This cannot be */
         fprintf(stderr,"This should never ever happen %s, %d\n", __FILE__, __LINE__);
         CTISleep(1000);
         continue;
      }

      /* We got one so see if we need to embed the error code */
      if(InMessage.EventCode & ~ENCODED)
      {
         /* We have an error of some sort */
         InMessage.IDLCStat[11] = LOBYTE (InMessage.EventCode & ~ENCODED);
         if(InMessage.InLength < 7)
         {
            InMessage.InLength = 7;
         }
      }
      else
      {
         InMessage.InLength += (2 + PREIDLEN);
      }

      /* Go ahead and send it on over */
      if(TCPIPStruct->TCPIPFailed != TRUE)
      {
         if(--TCPIPStruct->Busy)
         {
            printf ("Port %03d:  TCPIP Sequence Problem %ld\n", TCPIPStruct->PortNumber, TCPIPStruct->Busy);
         }

         if((send (TCPIPStruct->TCPIPSocket,
                   (CHAR*)(InMessage.IDLCStat + 11),
                   InMessage.InLength,
                   0)) != (INT)InMessage.InLength)
         {
            printf ("TCPIP Send Failed:  %ld\n", WSAGetLastError ());

            /* Something screwed up so try a shutdown */
            if(TCPIPStruct->TCPIPFailed != TRUE)
            {
               TCPIPStruct->TCPIPFailed = TRUE;
               SocketShutdownClose(__FILE__, __LINE__, &TCPIPStruct->TCPIPSocket);
               TCPIPStruct->TCPIPSocket = (int) NULL;
            }

            continue;
         }
      }
   }
}


/* Routine to Check TCP/IP Connection */
VOID TCPIPCheckThread (PVOID Arg)
{
   /* A Place for our arguments */
   TCPIPSTRUCT *TCPIPStruct = (TCPIPSTRUCT *)Arg;

   USHORT FailedStatus = {INDETERMINATE};
   ULONG PostCount;
   ULONG i;

   printf ("TCPIP Check Thread for %hd Starting as TID:  %ld\n",
           TCPIPStruct->PortNumber,
           CurrentTID ());

   if((i = CTICreateEventSem (NULL,
                              &TCPIPStruct->TCPIPFailedSem,
                              0,
                              0)) != NORMAL)
   {
      printf ("Unable to Create TCPIP Failed Semaphore\n");
      CTIExit (EXIT_PROCESS, i);
   }

   for(;!PorterQuit;)
   {
      if ( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
      {
         PorterQuit = TRUE;
         continue;
      }

      CTIResetEventSem (TCPIPStruct->TCPIPFailedSem,
                        &PostCount);

      /* Wait for More information, or timeout */
      if(CTIWaitEventSem (TCPIPStruct->TCPIPFailedSem,
                          900000L) == ERROR_TIMEOUT)
      {
         if(TCPIPStruct->TCPIPFailed != TRUE)
         {
            TCPIPStruct->TCPIPFailed = TRUE;
            TCPClose (&TCPIPStruct->MyPortInfo);
            fprintf (stderr, "\n\nTCP/IP Port TimeOut:  %hd\n\n", TCPIPStruct->PortNumber);
         }
      }
   }
}


/* Thread to handle CCU 710 TCP/IP Input */
VOID CCU710TCPIPInThread (PVOID Arg)
{
   /* A Place for our arguments */
   TCPIPSTRUCT *TCPIPStruct = (TCPIPSTRUCT *)Arg;

   /* TCPIP Stuff */
   int ReceiveLength;

   /* Buffer for TCP/IP Receive */
   BYTE  Buffer[300];
   ULONG MessLength;
   ULONG ReturnLength;
   ULONG MyAddress;
   ULONG TimeOut;

   /* Buffer to generate the block to porter */
   OUTMESS *OutMessage;

   /* Variables for retries */
   PSZ Environment;
   USHORT CommRetries = 0;

   /* Database records */
   CtiPort *PortRecord;

   UINT     InitFlags = 0;

   printf ("CCU 710 TCP/IP In Thread for %hd Starting as TID:  %ld\n",
           TCPIPStruct->PortNumber,
           CurrentTID());

   if(!(CTIScanEnv ("TCPIP_COMMRETRIES", &Environment)))
   {
      CommRetries = atoi (Environment);
   }

   CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, -10, 0);

   /* Wait for the pipe to get opened up */
   while(TCPIPStruct->ReturnNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      CTISleep (100L);
   }

   /* get the port record */
   // PortRecord.Port = TCPIPStruct->PortNumber;
   if(NULL == (PortRecord = PortManager.PortGetEqual (TCPIPStruct->PortNumber)))
   {
      printf ("Unable to get Port Record:  %hd\n", TCPIPStruct->PortNumber);
      CTIExit (EXIT_THREAD, -1);
   }

   InitFlags = getIPInitFlags(PortRecord);

   /* Make sure this is not a fubar */
   if(!(PortRecord->isTCPIPPort()))
   {
      /* This is bad */
      printf ("Port Must be TCP/IP Port:  %hd\n", TCPIPStruct->PortNumber);
      CTIExit (EXIT_THREAD, -1);
   }

   /* Sit and wait for stuff to come in */
   for(;!PorterQuit;)
   {
      if ( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
      {
         PorterQuit = TRUE;
         continue;
      }

      if(TCPIPStruct->TCPIPFailed == TRUE)
      {
         /* Init the port on the server one higher than the port server */
         if(TCPInitNext (&TCPIPStruct->MyPortInfo, PortRecord, InitFlags))
         {
            printf ("Unable to Open CCU 710 TCP/IP Port:  %hd\n", TCPIPStruct->PortNumber);
            CTISleep (5000L);
         }
         else
         {
            /* Let em know we are ok */
            TCPIPStruct->TCPIPSocket = TCPIPStruct->MyPortInfo.TerminalSocket;
            TCPIPStruct->TCPIPFailed = FALSE;
            CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);
         }
      }
      else
      {
         TCPInputFlush (&TCPIPStruct->MyPortInfo);

         /* Wait for the first character */
         ReceiveLength = 0;
         if((ReceiveLength = recv (TCPIPStruct->TCPIPSocket,
                                   (PCHAR)Buffer,
                                   1,
                                   0)) <= 0)
         {
            printf ("CCU 710 TCPIP Connection Failed on Read:  %ld\n", WSAGetLastError ());

            /* Check if someone else failed it */
            if(TCPIPStruct->TCPIPFailed != TRUE)
            {
               TCPIPStruct->TCPIPFailed = TRUE;
               SocketShutdownClose(__FILE__, __LINE__, &TCPIPStruct->TCPIPSocket);
               TCPIPStruct->TCPIPSocket = (int) NULL;
               TCPIPStruct->MyPortInfo.TerminalSocket = 0;
               CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);
               TCPIPStruct->Busy = 0;
            }

            continue;
         }

         /* Go Ahead and decode this particular 710 message */

         MyAddress = -1;
         MessLength = -1;
         ReturnLength = 0;

         /* Check the partity on the first byte */
         if(Buffer[0] != Parity_C (Buffer[0]))
         {
            MessLength = 0;
         }

         if(MessLength)
         {
            /* check if this is extended addressing or two byte short addressing */
            if(Buffer[0] & 0x40  ||
               Buffer[0] & 0x04  ||
               ((Buffer[0] & 0x7c) == 0x10))
            {
               /* get the next character */
               if(TCPReceiveChars (TCPIPStruct,
                                   &Buffer[1],
                                   1,
                                   3,
                                   (PULONG) &ReceiveLength))
               {
                  MessLength = 0;
               }
               else if(Buffer[0] & 0x40)
               {
                  if(Buffer[1] != Parity_C (Buffer[1]))
                  {
                     MessLength = 0;
                  }
               }
            }
         }

         /* If we get here we have either a one or two byte valid header so check the address */
         if(MessLength)
         {
            if(Buffer[0] & 0x40)
            {
               MyAddress = ((Buffer[0] & 0x03) | ((Buffer[1] & 0x38) >> 1));
            }
            else
            {
               MyAddress = (Buffer[0] & 0x03);
            }

            /* This is us so check what the message is and make sure we are length correct */
            if(Buffer[0] & 0x04)
            {
               /* This is a feeder operation so get the next character */
               if(TCPReceiveChars (TCPIPStruct,
                                   &Buffer[2],
                                   1,
                                   3,
                                   (PULONG) &ReceiveLength))
               {
                  MessLength = 0;
               }
               else
               {
                  /* Check partiy on the third byte */
                  if(Buffer[2] != Parity_C (Buffer[2]))
                  {
                     MessLength = 0;
                  }
                  else
                  {
                     /* get the rest of the bytes */
                     if(TCPReceiveChars (TCPIPStruct,
                                         &Buffer[3],
                                         Buffer[2] & 0x3f,
                                         3,
                                         (PULONG) &ReceiveLength))
                     {
                        MessLength = 0;
                     }
                     else
                     {
                        MessLength = (Buffer[2] & 0x3f) + 3;
                        /* Now calculate what the return length should be */
                        if((Buffer[3] & 0xe0) == 0x80)
                        {
                           /* This is an Outbound A word */
                           ReturnLength = 2;
                           TimeOut = TIMEOUT + (Buffer[1] & 0x07);
                        }
                        else if((Buffer[3] & 0xe0) == 0xa0)
                        {
                           /* We have to test if this is I or O */
                           if(Buffer[8] & 0x04)
                           {
                              /* This is a read so panic */
                              if(Buffer[8] & 0x08)
                              {
                                 /* This is a FUNCTION read */
                                 ReturnLength = 27;
                                 TimeOut = TIMEOUT + (4 * (Buffer[1] &0x07));
                              }
                              else
                              {
                                 /* This is a regular read */
                                 ReturnLength = 3 + (((Buffer[7] & 0x30) >> 4) * 8);
                                 TimeOut = TIMEOUT + ((((Buffer[7] & 0x30) >> 4) + 1) * (Buffer[1] &0x07));
                              }
                           }
                           else
                           {
                              /* Check for a time sync... if it is force the response and don't do it */
                              if((((Buffer[7] & 0x0f) << 4) | ((Buffer[8] &0xf0) >> 4)) == 0x49)
                              {
                                 CTISleep (150L);

                                 if(TCPIPStruct->TCPIPFailed != TRUE)
                                 {
                                    Buffer[0] = Parity_C ((CHAR)(0x40 | ((CHAR)(MyAddress & 0x03))));
                                    Buffer[1] = Parity_C ((CHAR)(0x40 | ((CHAR)(MyAddress & 0x03))));
                                    if((send (TCPIPStruct->TCPIPSocket,
                                              (PCHAR)Buffer,
                                              2,
                                              0)) != 2)
                                    {
                                       printf ("TCPIP Send Failed:  %ld\n", WSAGetLastError ());

                                       /* Something screwed up so try a shutdown */
                                       if(TCPIPStruct->TCPIPFailed != TRUE)
                                       {
                                          TCPIPStruct->TCPIPFailed = TRUE;
                                          SocketShutdownClose(__FILE__, __LINE__, &TCPIPStruct->TCPIPSocket);
                                          TCPIPStruct->TCPIPSocket = (int) NULL;
                                          CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);
                                       }
                                    }
                                 }
                                 MessLength = 0;
                                 MyAddress = -1;
                                 CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);
                              }
                              else
                              {
                                 /* This is a write so we should see two */
                                 ReturnLength = 2;
                                 TimeOut = TIMEOUT + ((((Buffer[7] & 0x30) >> 4) + 1) * (Buffer[1] &0x07));
                              }
                           }
                        }
                     }
                  }
               }
            }
            else
            {
               if(Buffer[0] & 0x40)
               {
                  /* This is the extended addressing stuff */
                  if((Buffer[0] & 0x7c) == 0x48)
                  {
                     /* Retransmit request */
                     ReturnLength = 0;
                  }
                  else if((Buffer[0] & 0x7c) == 0x50)
                  {
                     /* Loopback request */
                     /* get the next character */
                     if(TCPReceiveChars (TCPIPStruct,
                                         &Buffer[2],
                                         1,
                                         3,
                                         (PULONG) &ReceiveLength))
                     {
                        MessLength = 0;
                     }
                     else
                     {
                        /* Check partiy on the third byte */
                        if(Buffer[2] != Parity_C (Buffer[2]))
                        {
                           MessLength = 0;
                        }
                        else
                        {
                           MessLength = 3;
                           ReturnLength = 4;
                           TimeOut = TIMEOUT;
                        }
                     }
                  }
                  else if((Buffer[0] & 0x7c) == 0x58)
                  {
                     /* Retransmit request */
                     /* We have no idea what would come back */
                     MessLength = 0;
                  }
                  else if((Buffer[0] & 0x7c) == 0x60)
                  {
                     /* CCU Identification Request */
                     MessLength = 2;
                     ReturnLength = 6;
                     TimeOut = TIMEOUT;
                  }
                  else
                  {
                     /* We don't know what the hell this is */
                     MessLength = 0;
                  }
               }
               else
               {
                  /* This is the regular stuff */
                  if((Buffer[0] & 0x7c) == 0x08)
                  {
                     /* Retransmit request */
                     MessLength = 0;
                  }
                  else if((Buffer[0] & 0x7c) == 0x10)
                  {
                     /* Loopback */
                     /* get the next character */
                     if(TCPReceiveChars (TCPIPStruct,
                                         &Buffer[2],
                                         1,
                                         3,
                                         (PULONG) &ReceiveLength))
                     {
                        MessLength = 0;
                     }
                     else
                     {
                        /* Check partiy on the third byte */
                        if(Buffer[2] != Parity_C (Buffer[2]))
                        {
                           MessLength = 0;
                        }
                        else
                        {
                           MessLength = 3;
                           ReturnLength = 4;
                           TimeOut = TIMEOUT;
                        }
                     }
                  }
                  else if((Buffer[0] & 0x7c) == 0x18)
                  {
                     /* Retransmit request */
                     MessLength = 0;
                  }
                  else if((Buffer[0] & 0x7c) == 0x20)
                  {
                     /* CCU Identification Request */
                     MessLength = 1;
                     ReturnLength = 6;
                     TimeOut = TIMEOUT;
                  }
                  else
                  {
                     /* We don't know what the hell this is */
                     MessLength = 0;
                  }
               }
            }
         }

         /* If we get a mess length of 0 we will need to send a nack or two */
         if(MessLength == 0)
         {
            if(MyAddress != -1)
            {
               /* Send a nack in the future */

            }
            continue;
         }

         /* Give the failed sem a kick in the butt */
         CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);

         /* get some memory for the block to porter */
         if((OutMessage = new OUTMESS) == NULL)
         {
            /* Not much we can do */
            printf ("Error Allocating Memory\n");
            continue;
         }

         /* Do this real fast and dirty */
         OutMessage->Port = (USHORT)TCPIPStruct->PortNumber;
         OutMessage->Remote = (USHORT)MyAddress;
         OutMessage->TimeOut = (USHORT)TimeOut;
         OutMessage->Retry = CommRetries;
         OutMessage->Source = 0;
         OutMessage->Destination = 0;
         OutMessage->Sequence = 0;
         OutMessage->Priority = MAXPRIORITY - 1;
         OutMessage->ReturnNexus = TCPIPStruct->ReturnNexus;
         OutMessage->SaveNexus.NexusState = CTINEXUS_STATE_NULL;

         /* Figure out what the out length is */
         OutMessage->OutLength = MessLength + 3;
         OutMessage->InLength = ReturnLength;

         OutMessage->EventCode = RESULT | ENCODED;

         /* Now Copy the message into the buffer */
         memcpy (OutMessage->Buffer.OutMessage + PREIDLEN, Buffer, MessLength);

         /* And put it on the queue */
         if(QueueHandle(TCPIPStruct->PortNumber) != NULL)
         {
            if(PortManager.writeQueue(TCPIPStruct->PortNumber,
                           OutMessage->EventCode,
                           sizeof (*OutMessage),
                           (char *) OutMessage,
                           OutMessage->Priority))
            {
               printf ("Error Writing to Queue for Port %2hd\n", TCPIPStruct->PortNumber);
               delete (OutMessage);
            }
            else
            {
               TCPIPStruct->Busy++;
            }
         }
         else
         {
            printf ("Invalid Queue Handle for Port:  %2hd\n", TCPIPStruct->PortNumber);
            delete (OutMessage);
         }
      }
   }
}


/* Routine to handle CCU 710 TCP/IP Output stuff */
VOID CCU710TCPIPOutThread (PVOID Arg)
{
   INT         nRet = 0;
   CTINEXUS    ListenNexus;
   /* A Place for our arguments */
   TCPIPSTRUCT *TCPIPStruct = (TCPIPSTRUCT *)Arg;

   /* Buffer to get stuff off the pipe */
   INMESS InMessage;

   /* Misc Variables */
   ULONG BytesRead;

   /* make it clear who is the boss */
   CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, -10, 0);

   printf ("CCU 710 TCP/IP Out Thread for %hd Starting as TID:  %ld\n",
           TCPIPStruct->PortNumber,
           CurrentTID ());

   /*
    *  4/8/99 This is the server side of a new Nexus
    *  This thread listens only once and then may shutdown the listener?????
    *
    *  The socket connection created here is then used to communicate to porter
    *    requests in the SES92 protocol!
    *
    */
   strcpy(ListenNexus.Name, "CCU 710 FSI Connection Server: Listener");

   if(!CTINexusCreate(&ListenNexus, CCU710NEXUS))
   {
      fprintf(stderr,"Could not create CCU710 Connection Server\n");
      return;
   }

   sprintf(TCPIPStruct->ReturnNexus.Name, "CCU710 Nexus");

   /*
    *  Blocking wait on the listening nexus.  Will return a new nexus for the connection
    */
   nRet = CTINexusConnect(&ListenNexus, &(TCPIPStruct->ReturnNexus));

   if(nRet)
   {
      fprintf(stderr,"Error establishing CCU 710 Nexus to InThread\n");
      return;
   }

   /* Someone has connected to us.. */

   /*
    *  May need to put a thread out there to spawn the remainder of this thread on
    *  failure conditions.
    */
   CTINexusClose(&ListenNexus);  // Don't need this anymore.


   /* Now sit and wait for something to come in */
   for(;!PorterQuit;)
   {
      if ( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
      {
         PorterQuit = TRUE;
         continue;
      }

      if(CTINexusRead (&TCPIPStruct->ReturnNexus, &InMessage, sizeof (InMessage), &BytesRead, CTINEXUS_INFINITE_TIMEOUT) || BytesRead == 0)
      {
         /* This cannot be */
         CTISleep(1000);
         continue;
      }

      /* We got one so see if we need to embed the error code */
      if(InMessage.EventCode & ~ENCODED)
      {
         /* We have an error of some sort */

      }

      /* Go ahead and send it on over */
      if(TCPIPStruct->TCPIPFailed != TRUE)
      {
         if((send (TCPIPStruct->TCPIPSocket, (PCHAR)(InMessage.Buffer.InMessage), InMessage.InLength, 0)) != (INT)InMessage.InLength)
         {
            printf ("TCPIP Send Failed:  %ld\n", WSAGetLastError ());

            /* Something screwed up so try a shutdown */
            if(TCPIPStruct->TCPIPFailed != TRUE)
            {
               TCPIPStruct->TCPIPFailed = TRUE;
               SocketShutdownClose(__FILE__, __LINE__, &TCPIPStruct->TCPIPSocket);
               TCPIPStruct->TCPIPSocket = (int) NULL;
               TCPIPStruct->MyPortInfo.TerminalSocket = 0;
               CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);
               TCPIPStruct->Busy = 0;
            }

            continue;
         }

         if(--TCPIPStruct->Busy)
         {
            printf ("Port %03d:  TCPIP Sequence Problem\n", TCPIPStruct->PortNumber);
         }
      }
   }
}




#define WELCOREPLYTIME        60
#define WELCOSEQUENCEABORT    60

/* Routine to handle ESCA/Welco TCP/IP input stuff */
VOID WelcoTCPIPInThread (PVOID Arg)
{
   /* A Place for our arguments */
   TCPIPSTRUCT *TCPIPStruct = (TCPIPSTRUCT *)Arg;

   BOOL     bFailedRTU;

   /* TCPIP Stuff */
   int ReceiveLength;

   /* Buffer for TCP/IP Receive */
   BYTE Buffer[300];
   ULONG TimeOut = 50L;      // This will be our spin time.

   ULONG PostCount;

   /* Buffer to generate the block to porter */
   OUTMESS *OutMessage;

   /* Misc Variables */
   ULONG i;
   INT   MissedFlag;

   ULONG State;
   INT   Retry;

   struct timeb TimeB;
   LONG         AbortTime;

   /* Variables for retries */
   PSZ Environment;
   USHORT CommRetries = 0;

   /* Database records */
   CtiPort *PortRecord;

   UINT InitFlags = 0;

   printf ("ESCA/Welco TCP/IP In Thread for %hd Starting as TID:  %ld\n",
           TCPIPStruct->PortNumber,
           CurrentTID ());

   if(!(CTIScanEnv ("TCPIP_COMMRETRIES", &Environment)))
   {
      CommRetries = atoi (Environment);
   }

   /* make it clear who is the boss */
   CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, -10, 0);

   /* Wait for the pipe to get opened up */
   while(TCPIPStruct->ReturnNexus.NexusState == CTINEXUS_STATE_NULL)
   {
      CTISleep (100L);
   }

   /* get the port record */
   // PortRecord.Port = TCPIPStruct->PortNumber;
   if(NULL == (PortRecord = PortManager.PortGetEqual (TCPIPStruct->PortNumber)))
   {
      printf ("Unable to get Port Record:  %hd\n", TCPIPStruct->PortNumber);
      CTIExit (EXIT_THREAD, -1);
   }

   InitFlags = getIPInitFlags(PortRecord);

   /* Make sure this is not a fubar */
   if(!(PortRecord->isTCPIPPort()))
   {
      /* This is bad */
      printf ("Port Must be TCP/IP Port:  %hd\n", TCPIPStruct->PortNumber);
      CTIExit (EXIT_THREAD, -1);
   }

   /* Sit and wait for stuff to come in */
   for(;!PorterQuit;)
   {
      if ( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
      {
         PorterQuit = TRUE;
         continue;
      }

      // fprintf(stdout,"InThread Looping\n");
      if(TCPIPStruct->TCPIPFailed == TRUE)
      {
         /* Init the port on the server one higher than the port server */
         if(TCPInitNext (&TCPIPStruct->MyPortInfo, PortRecord, InitFlags))
         {
            printf ("Unable to Open ESCA/Welco TCP/IP Port:  %hd\n", TCPIPStruct->PortNumber);
            CTISleep (1000L);
         }
         else
         {

            /* Let em know we are ok */
            TCPIPStruct->TCPIPSocket = TCPIPStruct->MyPortInfo.TerminalSocket;
            TCPIPStruct->TCPIPFailed = FALSE;
            TCPIPStruct->Busy = 0;

            CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);

            /* Drop DTR */
            TCPLowerDTR (&TCPIPStruct->MyPortInfo);
            /* Lower RTS  == Remote is NOT CTS */
            TCPLowerRTS (&TCPIPStruct->MyPortInfo);
         }
         // fprintf(stdout,"Passed %d in %s\n",__LINE__, __FILE__);
      }
      else
      {

#define STATE_FLAG               0x0001
#define STATE_TRIPLET            0x0002
#define STATE_REMAINDER          0x0003
#define STATE_CLEANUP            0x0004
#define STATE_RTSHIGH            0x0005
#define STATE_DATA_IN            0x0006
#define STATE_WAITFOROUTTHREAD   0x0007
#define STATE_ENQUEUE            0x0008
#define STATE_DROPDTR            0x0009
#define STATE_ABNORMAL           0x2000
#define STATE_ERRABORT           0x4000
#define STATE_COMPLETE           0x8000

         State = STATE_CLEANUP;
         Retry = 100;

         do
         {
            switch(State)
            {
            case STATE_CLEANUP:
               {
                  Retry = 1;
                  TCPLowerRTS (&TCPIPStruct->MyPortInfo); // Just make damn sure. Not CTS
                  /*
                   *  Clean out the hog pen. This was moved here from 1.0 so that
                   *  any stray characters coming in are properly ignored.
                   */
                  // TCPInputFlush (&TCPIPStruct->MyPortInfo);

                  State = STATE_RTSHIGH;

                  // Fall Through to next state break;
               }
            case STATE_RTSHIGH:
               {
                  Retry = 1;
                  State = STATE_DATA_IN;

                  /* Wait for the CTS == Remote asserting RTS*/
                  i=0;
                  while(!TCPIPStruct->TCPIPFailed && !(TCPCTSTest (&TCPIPStruct->MyPortInfo)))
                  {

                     i++;
                     if(TCPIPStruct->MyPortInfo.Connected == FALSE)
                     {
                        State = STATE_ERRABORT;
                        if(TCPIPStruct->TCPIPFailed != TRUE)
                        {
                           TCPIPStruct->TCPIPFailed = TRUE;

                           TCPIPStruct->Busy = 0;
                           TCPClose(&TCPIPStruct->MyPortInfo);
                           CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);
                           break;
                        }
                     }

                     if(i > 400)
                     {
                        //fprintf(stderr,"No Source System Requests in 10 seconds \n");
                        TCPLowerRTS (&TCPIPStruct->MyPortInfo); // Just make damn sure.

                        /* Tickle DTR */
                        TCPRaiseDTR (&TCPIPStruct->MyPortInfo);
                        TCPLowerDTR (&TCPIPStruct->MyPortInfo);

//                        fprintf(stderr,"No Comm request for port %d\n",TCPIPStruct->PortNumber);
//                        fprintf(stderr,"\t%d in %s \n",__LINE__, __FILE__);

                        CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);
                        // Blast absolutely anything else in the TS or our input queue out.
                        TCPInputFlushTS(&TCPIPStruct->MyPortInfo);

                        i=0;
                     }

                     CTISleep (TimeOut);
                  }

                  if(TCPIPStruct->TCPIPFailed == TRUE)
                  {
                     State = STATE_ERRABORT;
                     fprintf(stderr,"WELCO Connection Failed... ReInitializing: %d in %s\n",__LINE__, __FILE__);
                  }

                  break;
               }
            case STATE_DATA_IN:
               {
                  /* Assert RTS  == Remote is CTS*/
                  TCPRaiseRTS (&TCPIPStruct->MyPortInfo);

                  // Longest emetcon message is 261 so I guess I need this.
                  // I may need to wait this many for the flag byte if I miss it.
                  Retry = 265;
                  MissedFlag = 0;
                  State = STATE_FLAG;     // If all goes well below we go here next...

                  /* Wait for something to come in */
                  i=0;
                  while(!TCPIPStruct->TCPIPFailed && TCPQuery (&TCPIPStruct->MyPortInfo) == 0)
                  {
                     i++;
                     if(TCPIPStruct->MyPortInfo.Connected != TRUE)
                     {
                        if(TCPIPStruct->TCPIPFailed != TRUE)
                        {
                           TCPIPStruct->TCPIPFailed = TRUE;

                           TCPIPStruct->Busy = 0;
                           TCPClose(&TCPIPStruct->MyPortInfo);
                           CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);
                           break;
                        }
                     }

                     if(i > 200)
                     {
                        fprintf(stderr,"No Data In after RTS from  port %d\n",TCPIPStruct->PortNumber);
                        fprintf(stderr,"\t%d in %s  FAILED\n",__LINE__, __FILE__);

                        State = STATE_ERRABORT;  // Restart....
                        break; // while
                        i=0;
                     }
                     CTISleep (TimeOut);
                  }

                  if(TCPIPStruct->TCPIPFailed == TRUE)
                  {
                     State = STATE_ERRABORT;
                     fprintf(stderr,"WELCO Connection Failed... ReInitializing: %d in %s\n",__LINE__, __FILE__);
                  }
                  break;
               }
            case STATE_FLAG:
               {
                  // Retry set to 265 in previous state so don't touch it here
                  /* Wait for the flag character */
                  if(TCPReceiveChars (TCPIPStruct,
                                      &Buffer[0],
                                      1,
                                      2,
                                      (PULONG) &ReceiveLength))
                  {
                     fprintf(stderr,"ESCA/Welco TCPIP Connection No FLAG Character Read %d\n",MissedFlag);
                     fprintf(stderr,"*** DATA *** %s (%d): 1 byte read failed\n",__FILE__, __LINE__);
                     State = STATE_ERRABORT;
                  }
                  else
                  {
                     /* Make sure this is the flag character */
                     if(Buffer[0] != 0x7e)
                     {
                        /* get the next one... */
                        MissedFlag++;
                        //fprintf(stderr,"*** DATA *** %s (%d): 1st byte is not 0x7E\n",__FILE__, __LINE__);
                     }
                     else
                     {
                        Retry = 1;
                        State = STATE_TRIPLET;
                     }
                  }

                  if(TCPIPStruct->TCPIPFailed == TRUE)
                  {
                     State = STATE_ERRABORT;
                     fprintf(stderr,"WELCO Connection Failed... ReInitializing: %d in %s\n",__LINE__, __FILE__);
                  }
                  break;
               }
            case STATE_TRIPLET:
               {
                  Retry = 1;     // There is no way to come back to this state, from this state...

                  /* This is it so wait for the next 3 characters which will give us the length */
                  if(TCPReceiveChars (TCPIPStruct,
                                      &Buffer[1],
                                      3,
                                      2,
                                      (PULONG) &ReceiveLength))
                  {
                     fprintf(stderr,"*** DATA *** %s (%d): 3 byte read failed\n",__FILE__, __LINE__);
                     State = STATE_ERRABORT;
                  }
                  else
                  {
                     /* Check for unsequenced */
                     if(Buffer[2] != 0x13)
                     {
                        fprintf(stderr,"*** DATA *** %s (%d): Sequenced message %1X %1X %1X %1X\n",__FILE__, __LINE__,
                                Buffer[0],
                                Buffer[1],
                                Buffer[2],
                                Buffer[3]);
                        fprintf(stderr,"*** DATA *** %s (%d): Porter will ignore this message. \n",__FILE__, __LINE__);
                        State = STATE_ERRABORT;
                     }
                     else
                     {
                        State = STATE_REMAINDER;
                     }
                  }

                  if(TCPIPStruct->TCPIPFailed == TRUE)
                  {
                     State = STATE_ERRABORT;
                     fprintf(stderr,"WELCO Connection Failed... ReInitializing: %d in %s\n",__LINE__, __FILE__);
                  }

                  break;
               }
            case STATE_REMAINDER:
               {
                  Retry = 1;     // There is no way to come back to this state, from this state...

                  /* get the rest of the characters */
                  if(TCPReceiveChars (TCPIPStruct,
                                      &Buffer[4],
                                      Buffer[3] + 2,
                                      5,
                                      (PULONG) &ReceiveLength))
                  {
                     fprintf(stderr,"*** DATA *** %s (%d): Failed message remainder read\n",__FILE__, __LINE__);
                     State = STATE_ERRABORT;
                  }
                  else
                  {
                     /* We've got an entire message from the remote requestor */
                     /* Lower RTS  == Remote is NOT CTS */
                     TCPLowerRTS (&TCPIPStruct->MyPortInfo);
                     State = STATE_ENQUEUE;
                  }

                  break;
               }
            case STATE_ENQUEUE:
               {
                  Retry = 1;

                  /*
                   * Lets take a look at this remote's history, to see if we need to let the requestor
                   * know he is failed.  If so, we stop toggling DCD to the requestor.
                   */
                  if(ConsecutiveRTUFailures[Buffer[1] >> 1] > MAX_RTU_FAILS)
                  {
                     /*
                      *  This RTU is failed,.. We need to do something different here to
                      *  keep a VAX in DE happy.  I won't rais DTR => DCD to the VAX
                      *  hopefully he thinks this means that something is broke for this RTU
                      *
                      *  I still submit this to porter so that he can see if the RTU ever comes back on
                      *  line. If he does, I will lose his 1st reply, but the next one will
                      *  happen because Failures is then reset.
                      */
                     fprintf(stderr,"Port: %hd Remote %d\n", TCPIPStruct->PortNumber, Buffer[1] >> 1);
                     fprintf(stderr,"\tTagged (by DSM/2) as failed.\n");
                     fprintf(stderr,"\tAttempting to communicate now.\n");
                     bFailedRTU = TRUE;
                  }
                  else
                  {
                     /* Assert DTR  ==> DCD */
                     TCPRaiseDTR (&TCPIPStruct->MyPortInfo);
                     bFailedRTU = FALSE;
                  }

                  /* Give the failed sem a kick in the butt */
                  CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);

                  /* get some memory for the block to porter */
                  if((OutMessage = new OUTMESS) == NULL)
                  {
                     /* Not much we can do */
                     printf ("Error Allocating Memory\n");
                     State = STATE_ABNORMAL;
                     break;
                  }

                  /* Do this real fast and dirty */
                  OutMessage->Port = TCPIPStruct->PortNumber;
                  OutMessage->Remote = Buffer[1] >> 1;
                  OutMessage->TimeOut = 1;
                  OutMessage->Retry = CommRetries;
                  OutMessage->Source = 0;
                  OutMessage->Destination = 0;
                  OutMessage->Sequence = 0;
                  OutMessage->Priority = MAXPRIORITY - 1;
                  OutMessage->ReturnNexus = TCPIPStruct->ReturnNexus;
                  OutMessage->SaveNexus.NexusState = CTINEXUS_STATE_NULL;

                  /* Figure out what the out length is */
                  OutMessage->OutLength = Buffer[3] - 3;
                  OutMessage->InLength = 0;

                  /* Now figure out the event code */
                  if(OutMessage->Remote == RTUGLOBAL)
                  {
                     State = STATE_DROPDTR;
                     OutMessage->EventCode = NOWAIT | NORESULT | ENCODED;
                  }
                  else
                  {
                     if(bFailedRTU)
                     {
                        /* It should never post or reply, so go get next request. */
                        State = STATE_COMPLETE;
                     }
                     else
                     {
                        State = STATE_WAITFOROUTTHREAD;
                     }

                     OutMessage->EventCode = RESULT | ENCODED | WAIT;
                     TCPIPStruct->Busy++;

                     // Reset it so that it can be posted to
                     CTIResetEventSem (TCPIPStruct->CommSem, &PostCount);
                     // set up the abort time
                     UCTFTime(&TimeB);
                     AbortTime = TimeB.time + WELCOREPLYTIME;
                  }

                  /* Check if this is a time sync */
                  if((Buffer[5] & 0x7f) == IDLC_TIMESYNC)
                  {
                     OutMessage->EventCode |= TSYNC;
                  }

                  /* Now Copy the message into the buffer */
                  memcpy (OutMessage->Buffer.OutMessage, Buffer, Buffer[3] + 2 + 4);

                  /* And put it on the queue */
                  if(QueueHandle(TCPIPStruct->PortNumber) != NULL)
                  {
                     TCPIPStruct->Flags &= ~TCPIP_FLAG_NEW;    // Reset this bit...

                     if(PortManager.writeQueue(TCPIPStruct->PortNumber,
                                    OutMessage->EventCode,
                                    sizeof (*OutMessage),
                                    (char *) OutMessage,
                                    OutMessage->Priority))
                     {
                        printf ("Error Writing to Queue for Port %2hd\n", TCPIPStruct->PortNumber);
                        delete (OutMessage);
                        State = STATE_DROPDTR;
                     }

                     if(State == STATE_WAITFOROUTTHREAD)
                     {
                        /*
                         *  2/4/99 CGP
                         *  Unless we are going to WAIT, the VAX is not waiting either
                         *   in this case, we should clear out any other requests.
                         *
                         * Blast absolutely anything else in the TS or our input queue out.
                         * There shouldn't be anything out there in any case.
                         */
                        TCPInputFlushTS(&TCPIPStruct->MyPortInfo);
                     }
                  }
                  else
                  {
                     printf ("Invalid Queue Handle for Port:  %2hd\n", TCPIPStruct->PortNumber);
                     delete (OutMessage);
                     State = STATE_DROPDTR;
                  }

                  break;
               }
            case STATE_WAITFOROUTTHREAD:
               {
                  Retry = 1;
                  /*
                   *  Wait for the OutThread to reply back to the requestor.
                   */

                  if(CTIWaitEventSem (TCPIPStruct->CommSem, 500L) == NO_ERROR)
                  {
                     // It was posted from OutThread..  We are completed!
                     CTIResetEventSem (TCPIPStruct->CommSem, &PostCount);
                     State = STATE_DROPDTR;
                  }
                  else
                  {
                     // fprintf(stderr,"Waiting...\n");
                     UCTFTime(&TimeB);  // Refresh this time value.

                     if(TimeB.time > AbortTime)
                     {
                        fprintf(stderr,"No reply for message on port %hd in %d seconds\n", TCPIPStruct->PortNumber, WELCOREPLYTIME);
                        State = STATE_ERRABORT;
                     }
                     else if(TCPQuery (&TCPIPStruct->MyPortInfo) > 3) // Check the Rec. buffer for a new message;
                     {
                        // A new message is in there??? Why for how come?
                        TCPIPStruct->Flags |= TCPIP_FLAG_NEW;

                        fprintf(stderr,"Port %hd:  Sequence Problem..\n\tAllowing up to %d seconds for queue purge\n", TCPIPStruct->PortNumber,WELCOSEQUENCEABORT);

                        TCPLowerDTR (&TCPIPStruct->MyPortInfo);
                        CTIWaitEventSem (TCPIPStruct->CommSem, WELCOSEQUENCEABORT * 1000L);

                        // Blast absolutely anything else in the TS or our input queue out in ERRABORT.
                        State = STATE_ERRABORT;
                     }
                  }
                  break;
               } // STATE_WAITFOROUTTHREAD

            case STATE_ERRABORT:
               {
                  TCPLowerRTS (&TCPIPStruct->MyPortInfo);

                  // Blast absolutely anything else in the TS or our input queue out.
                  TCPInputFlushTS(&TCPIPStruct->MyPortInfo);

                  // !!! THIS STATE FALLS THROUGH to COMPLETE!!!!
               }
            case STATE_DROPDTR:
               {
                  /* Drop DTR  == Remote's DCD. comms complete, or failed*/
                  TCPLowerDTR (&TCPIPStruct->MyPortInfo);
                  State = STATE_COMPLETE;

                  break;
               }
            } // switch
         } while(!TCPIPStruct->TCPIPFailed &&
                 Retry-- > 0 &&
                 State != STATE_COMPLETE);

      }  // if(!(TCPIPStruct->TCPIPFailed == TRUE))



      /*
       *  Currently we should not get here until the OutThread has completed
       *  his part of the bargain.
       */
#if 0
      CTIQueryEventSem (TCPIPStruct->CommSem, &PostCount);
      if(PostCount)
      {
         fprintf(stderr, "*** DATA *** Semaphore had mutiple postings: %s %d\n",__FILE__, __LINE__);
         CTIResetEventSem (TCPIPStruct->CommSem, &PostCount);
      }
#endif      // FIX FIX FIX ??? CGP
   } // for(;;)
}

/* Routine to handle ESCA/Welco TCP/IP Output stuff */
VOID WelcoTCPIPOutThread (PVOID Arg)
{
   INT         nRet;
   CTINEXUS    ListenNexus;
   /* A Place for our arguments */
   TCPIPSTRUCT *TCPIPStruct = (TCPIPSTRUCT *)Arg;

   INT      RemoteNumber;
   BOOL     bDoReply;

   /* Buffer to get stuff off the pipe */
   INMESS InMessage;

   /* Misc Variables */
   ULONG i, BytesRead;

   /* make it clear who is the boss */
   CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, -10, 0);

   printf ("ESCA/Welco TCP/IP Out Thread for %hd Starting as TID:  %ld\n",
           TCPIPStruct->PortNumber,
           CurrentTID ());

   /* Create a Semaphore to allow completion on the InPort Side */
   if((CTICreateEventSem (NULL, &TCPIPStruct->CommSem, 0, 0)) != NORMAL)
   {
      fprintf (stderr, "Unable to Create TCPIP CommSemaphore for Port %d\n",TCPIPStruct->PortNumber);
      CTIExit (EXIT_THREAD, -1);
   }

   /*
    *  4/8/99 This is the server side of a new Nexus
    *  This thread listens only once and then may shutdown the listener?????
    *
    *  The socket connection created here is then used to communicate to porter
    *    requests in the SES92 protocol!
    *
    */
   strcpy(ListenNexus.Name, "WELCO FSI Connection Server: Listener");

   if(!CTINexusCreate(&ListenNexus, CCU710NEXUS))
   {
      SET_FOREGROUND_BRIGHT_RED;
      fprintf(stderr,"Could not create WELCO Connection Server\n");
      SET_SCREEN_NORMAL;
      return;
   }

   sprintf(TCPIPStruct->ReturnNexus.Name, "WELCO Nexus");

   /*
    *  Blocking wait on the listening nexus.  Will return a new nexus for the connection
    */
   nRet = CTINexusConnect(&ListenNexus, &(TCPIPStruct->ReturnNexus));

   if(nRet)
   {
      fprintf(stderr,"Error establishing WELCO Nexus to InThread\n");
      return;
   }

   /* Someone has connected to us.. */

   /*
    *  May need to put a thread out there to spawn the remainder of this thread on
    *  failure conditions.
    */
   CTINexusClose(&ListenNexus);  // Don't need this anymore.


   /* Now sit and wait for something to come in */
   for(;!PorterQuit;)
   {
      if ( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
      {
         PorterQuit = TRUE;
         continue;
      }

      if(CTINexusRead (&TCPIPStruct->ReturnNexus,
                       &InMessage,
                       sizeof (InMessage),
                       &BytesRead,
                       CTINEXUS_INFINITE_TIMEOUT) || BytesRead == 0)
      {
         /* This cannot be */
         CTIPostEventSem(TCPIPStruct->CommSem);
         fprintf(stderr,"*** DATA *** Read error %s (%d)\n",__FILE__, __LINE__);
         continue;
      }

      bDoReply = TRUE;
      RemoteNumber = (InMessage.IDLCStat[12] >> 1);
      /* We got one so see if we need to embed the error code */
      if(InMessage.EventCode & ~ENCODED)
      {
         /* We have an error of some sort */
         InMessage.IDLCStat[11] = LOBYTE (InMessage.EventCode & ~ENCODED);
         if(InMessage.InLength < 7)
         {
            InMessage.InLength = 7;
         }

         // post increment, so if I'm already greater, I won't reply and post
         if(ConsecutiveRTUFailures[RemoteNumber]++ > MAX_RTU_FAILS)
         {
            bDoReply = FALSE;
         }
      }
      else
      {
         InMessage.InLength += (2 + PREIDLEN);

         if(ConsecutiveRTUFailures[RemoteNumber] > MAX_RTU_FAILS)
         {
            /*
             * This is the first good response after a string of failures.
             * DCD was not raised because of ConsecutiveRTUFailures[RemoteNumber] being
             * too high on the InThread side.  So we DO NOT reply for this one last time.
             * Since we reset the ConsecutiveRTUFailures[RemoteNumber] to zero,
             * the next request for this RTU will happen, and get proper attention w.r.t
             * DCD.
             */
            bDoReply = FALSE;
         }

         ConsecutiveRTUFailures[RemoteNumber] = 0;
      }

      /* Go ahead and send it on over */
      if(TCPIPStruct->TCPIPFailed != TRUE)
      {

         if(--TCPIPStruct->Busy > 0)
         {
            printf ("Port %03d:  TCPIP Sequence Problem:  %d\n", TCPIPStruct->PortNumber, TCPIPStruct->Busy);
         }

         if(bDoReply)
         {

            if(!(TCPIPStruct->Flags & TCPIP_FLAG_NEW))      // Nothing new has come in...
            {
               i = TCPSend (&TCPIPStruct->MyPortInfo, (InMessage.IDLCStat + 11), InMessage.InLength);

               if(i)
               {

                  /* Something screwed up so try a shutdown */
                  if(TCPIPStruct->TCPIPFailed != TRUE)
                  {
                     TCPIPStruct->TCPIPFailed = TRUE;
                     TCPIPStruct->Busy = 0;
                     TCPClose(&TCPIPStruct->MyPortInfo);
                  }
                  continue;
               }
            }

            i = CTIPostEventSem(TCPIPStruct->CommSem);
            while(i)
            {
               // if(i == ERROR_ALREADY_POSTED) break;         // FIX FIX FIX CGP
               fprintf(stderr,"Error Posting Event for Comms = %ld\n",i);
               CTISleep(50L);
               i = CTIPostEventSem(TCPIPStruct->CommSem);
            }
         }

      }
      else
      {
         // fprintf(stderr,"*** Port has been FAILED in %s at %d\n",__FILE__, __LINE__);
         CTIPostEventSem(TCPIPStruct->CommSem);
      }
   }
}

UINT getIPInitFlags(CtiPort *PortRecord)
{
   extern CtiDeviceManager DeviceManager;

   UINT uRet = 0;

   CtiRTDB<CtiDeviceBase>::CtiRTDBIterator   itr_dev(DeviceManager.getMap());

   for(; ++itr_dev ;)
   {
      CtiDeviceBase *RemoteRecord = itr_dev.value();

      if(
          PortRecord->getID() == RemoteRecord->getPortID() &&
          RemoteRecord->getType() == TYPE_TAPTERM
        )
      {
         uRet |= INITFLAG_TAPTERM | INITFLAG_PORTDELAYS;
         break;
      }
   }

   return uRet;
}
