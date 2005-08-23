/*-----------------------------------------------------------------------------*
*
* File:   portgui
*
* Date:   04-14-1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/portgui.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/08/23 20:06:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#pragma title ( "Port Control GUI Server Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1999" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        Corey G. Plender

    FileName:
        PORTGUI.C

    Purpose:
        To handle requests and replies to a Porter GUI

    The following procedures are contained in this module:
        PorterGUIConnectionThread

    Initial Date:
        Unknown

    Revision History:

      04-14-1999     Initial Revision                          CGP

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "ilex.h"
#include "perform.h"
#include "thread_monitor.h"

#include "portglob.h"
#include "color.h"

#include "portgui.h"

VOID GUIConnectionThread (VOID *Arg);


/* Threads to field incoming messages from the pipes */


VOID PorterGUIConnectionThread (VOID *Arg)
{
   INT   iNexus   = 0;
   INT   nRet     = 0;
   INT   sanity   = 0;

   CTINEXUS  ListenNexus;
   CTINEXUS  *NewNexus;

   return;                 // CGP 012400 For now...


   strcpy(ListenNexus.Name, "PortControl GUI Connection Server: Listener");

   /*
    *  4/7/99 This is the server side of a CTIDBG_new Port Control Nexus
    *  This thread rolls off CTIDBG_new instances of this connection on an as needed basis.
    *
    *  1. Create a listener on PORTGUINEXUS for incoming connections
    *  2. Pop off a CTIDBG_new thread to manage the returned connection.
    */

   if(!ListenNexus.CTINexusCreate(PORTGUINEXUS))
   {
      for(;;)
      {

         //Thread Monitor Begins here**************************************************
         if(!(++sanity % SANITY_RATE))
         {
             {//This is not necessary and can be annoying, but if you want it (which you might) here it is.
                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                 dout << RWTime() << " Porter GUI Connection Thread. TID:  " << rwThreadId() << endl;
             }
   
             CtiThreadRegData *data = new CtiThreadRegData( GetCurrentThreadId(), "Porter GUI Connection Thread", CtiThreadRegData::None, 400 );
             ThreadMonitor.tickle( data );
         }
         //End Thread Monitor Section
        
         NewNexus = (CTINEXUS*) CTIDBG_new CTINEXUS;

         if(NewNexus == NULL)
         {
            fprintf(stderr,"Unable to acquire memory for a CTIDBG_new connection to port control\n");

            Sleep(1000);
            continue;
         }
         sprintf(NewNexus->Name, "PortControl GUI Nexus #%d", iNexus++);

         /*
          *  Blocking wait on the listening nexus.  Will return a CTIDBG_new nexus for the connection
          */
         nRet = ListenNexus.CTINexusConnect(NewNexus);

         if(!nRet)
         {
            /* Someone has connected to us.. */
            fprintf(stderr,"PortControl GUI Server: Nexus Connected\n");
            _beginthread(GUIConnectionThread, 0, (VOID*)NewNexus);

         }
         else
         {
            fprintf(stderr,"Error creating nexus\n");
            free(NewNexus);
            ListenNexus.CTINexusClose();

            break;
         }
      }
   }

   return;
}


/*
 *  This is the guy who deals with incoming data from any one else in the system.
 */
VOID GUIConnectionThread (VOID *Arg)

{
   ULONG          i;
   ULONG          BytesRead;

   CTIGUIMSG      *GUIMsg;

   CTINEXUS       *MyNexus = (CTINEXUS*)Arg;     // This is an established connection with a client!

   printf ("GUIConnectionThread Starting as TID:  %ld\n", CurrentTID ());


   /* make it clear who is boss */
   CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, 30, 0);

   /* Now sit and wait for something to come in on this instance */
   for(;;)
   {

      /* Allocate memory for block coming in */
      if((GUIMsg = (CTIGUIMSG*) CTIDBG_new CTIGUIMSG) == NULL)
      {
         printf ("Error Allocating Memory for Incoming GUI Message Block\n");
         continue;
      }

      BytesRead = 0;

      /* read whatever comes in */
      if((i = MyNexus->CTINexusRead (GUIMsg, sizeof(CTIGUIMSG), &BytesRead, CTINEXUS_INFINITE_TIMEOUT))  || BytesRead == 0)
      {
         fprintf(stderr,"GUI Thread closing.  Bytes Read = %ld\n", BytesRead);
         free (GUIMsg);
         MyNexus->CTINexusClose ();
         _endthread ();                // Hari Kari on this thread!
      }

      /*
       *  We have a valid GUI message in our side of the interface.
       */

      switch(GUIMsg->Command)
      {
      case GUICMD_MODIIFYGLOBS:
         {
            ModifyPorterGlobs(GUIMsg);
            break;
         }
      case GUICMD_KILLPORTER:
         {
            fprintf(stderr,"Attempting to shutdown porter by GUI request!\n");
            exit(-1);   // Might want to send a ctrl signal someday!
            break;
         }
      default:
         {
            printf("Unknown GUI Request\n");
            break;
         }
      }

      /* and do it all again */
   }
}

INT ModifyPorterGlobs(CTIGUIMSG *Msg)
{
   switch(Msg->Global)
   {
   case GLOB_TRACE:
      {
         TraceFlag = !TraceFlag;

         if(TraceFlag)
         {
            if(TraceErrorsOnly)
            {
               printf ("\nTrace is Now On for Errors Only\n");
            }
            else
            {
               printf ("\nTrace is Now On for All Messages\n");
            }
         }
         else
         {
            printf ("\nTrace is Now Off for All Messages\n");
         }

         break;
      }
   case GLOB_TRACEERROR:
      {
         TraceErrorsOnly = !TraceErrorsOnly;
         if(TraceErrorsOnly)
         {
            TraceFlag = TRUE;
            printf ("\nTrace is Now On for Errors Only\n");
         }
         else if(TraceFlag)
         {
            printf ("\nTrace is Now On for All Messages\n");
         }
         else
         {
            printf ("\nTrace is Now Off for All Messages\n");
         }

         break;

      }
   default:
      {
         printf("Global %d currently undefined\n");
         break;
      }
   }

   return(0);
}


