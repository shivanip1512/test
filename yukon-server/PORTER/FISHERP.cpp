
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   FISHERP
*
* Date:         10/98 Initial Revision                                    CGP
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/FISHERP.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma title ( "Fisher Pierce Support Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1998" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        Corey G. Plender

    FileName:
        fisherp.c

    Purpose:
        Generate and send Versacom messages to various remotes

    The following procedures are contained in this module:
        FisherPierceSend
        FisherPierceSend

    Initial Date:
        Unknown

    Revision History:
        10/98 Initial Revision                                    CGP


   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <memory.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "master.h"

#include "portglob.h"
#include "mgr_device.h"
#include "mgr_port.h"

#include "logger.h"
#include "guard.h"

extern CtiPortManager PortManager;
extern CtiDeviceManager DeviceManager;


int FisherPierceSend (OUTMESS *OutMessage, PBYTE Message);
int FisherPierceMessage (OUTMESS *OutMessage);

/* Routine to create a Fisher Pierce message */
FisherPierceMessage (OUTMESS *OutMessage)
{
   BYTE     Message[30];


   FPPCCST *PCC = &(OutMessage->Buffer.FPSt.u.PCC);

   PCC->PRE[0] = '*';                        /* Select Paging Terminal header */
   memcpy(PCC->UID  , "001"     , 3);        /* Utility ID.... 001 is CP&L */
   memcpy(PCC->VID  , "001"     , 3);        /* Vendor ID      001 is FP   */
   memcpy(PCC->D    , "01"      , 2);        /* Device ID      01 is Capacitor Control */
   /* memcpy(PCC->F    , "01"      , 2);     /* Function       This is set in the calling side */
   /* memcpy(PCC->ADDRS, "0000000" , 7);     /* Address        This is set prior to send */
   memcpy(PCC->GRP  , "0000"    , 4);        /* Group Addressing ... 0000 is Individual */
   /* memcpy(PCC->VALUE, "000000"  , 6);     /* This is set on the calling side */
   PCC->POST[0] = '*';                       /* Select Paging Terminal trailer */

   /* This whole message comes to me in good form.... */
   memcpy (Message, PCC, sizeof(FPPCCST));

   /* It done so send it out appropriately */
   return(FisherPierceSend (OutMessage, Message));

}

FisherPierceSend (OUTMESS *OutMessage, PBYTE Message)
{
   OUTMESS     *MyOutMessage;

   ULONG       i;
   USHORT      Length;
   static PSZ  CapEnvironment = NULL;


   /* Allocate the memory for the queue entry */
   if((MyOutMessage = new OUTMESS(*OutMessage)) == NULL)
   {
      return(MEMORY);
   }

   MyOutMessage->EventCode = FISHERPIERCE;
   if(OutMessage->EventCode & RESULT)
   {
      MyOutMessage->EventCode |= RESULT;
   }

   CtiDevice *Dev = DeviceManager.getEqual( OutMessage->DeviceID );

   if(Dev != NULL)
   {
      switch( Dev->getType() )
      {
      case TYPE_TAPTERM:
         {
            // printf("Device is coming through:  %.20s\n",OutMessage->DeviceName);

            /* TAP - Telelocator Alphanumeric Protcol (very loose use of the word) is a method of sending a page to
            a paging supplier over LL or dialup.  TAP itself is handled elsewhere but at this point we need to:
                1.  ASCIIize the message into an ASCII hex representation of the versacom message.
                2.  Put an "H" on for a header and a "G" for the trailer of the message. VERSACOM ONLY PER WRO 10/2798 CGP...
                3.  Find the capcode for this message.  The devicename is in the devicename feild of the message.  This allows
                    us to get the device record, which in turn allow us to get the first route record, which has the capcode in
                    the first repeater name feild.  If this fails it checks for DSM2_CAPCODE for the capcode.  If that
                    fails we are screwed big time. */

            if(CapEnvironment == NULL)
            {
               /* Attempt to get the capcode from environment */
               if((CTIScanEnv ("DSM2_CAPCODE", &CapEnvironment)) != NORMAL)
               {
                  printf ("Error getting Default CapCode\n");
                  return(SYSTEM);
               }
            }

            /* Copy the sucker over */

#ifdef OLD_CODE
            for(i = 0; i < STANDNAMLEN; i++)
            {
               if(CapEnvironment[i] == '\0')
               {
                  break;
               }
               MyOutMessage->Buffer.TAPSt.CapCode[i] = CapEnvironment[i];
            }

            for(; i < STANDNAMLEN; i++)
            {
               MyOutMessage->Buffer.TAPSt.CapCode[i] = ' ';
            }
#else

            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
#endif



            MyOutMessage->Port            = OutMessage->Port;
            MyOutMessage->Remote          = OutMessage->Remote;
            MyOutMessage->Priority        = OutMessage->Priority;
            MyOutMessage->Retry           = OutMessage->Retry;
            MyOutMessage->Sequence        = OutMessage->Sequence;
            MyOutMessage->TimeOut         = 2;
            MyOutMessage->InLength        = -1;

            /* Calculate the length */
            Length = 29;                     /* Protocol does allow shortening this by a few bytes... */

            MyOutMessage->OutLength = Length;
            MyOutMessage->Buffer.TAPSt.Length = Length;

            /* Build the message */
            memcpy(MyOutMessage->Buffer.TAPSt.Message, Message, 29);   /* Standard Message */

            for(i = 0; i < Length; i++)
            {
               printf ("%c", MyOutMessage->Buffer.TAPSt.Message[i]);
            }
            printf ("\n");

            /* Put the beastie on the queue */
            if((i = PortManager.writeQueue(OutMessage->Port, MyOutMessage->EventCode, sizeof (*MyOutMessage), (char *) MyOutMessage, MyOutMessage->Priority)) != NORMAL)
            {
               printf ("\nError Placing entry onto Queue:  %ld\n", i);
               delete (MyOutMessage);
               return(SYSTEM);
            }
            break;
         }
      default:
         {
            printf ("\n%s, %s line %d:\n\tError: Remote is not a TAPTERM Device \n", __FILE__, __FILE__, __LINE__);
            delete (MyOutMessage);
            return(SYSTEM);
         }
      }
   }
   else
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " **** ACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   return(NORMAL);
}


