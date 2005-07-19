/*-----------------------------------------------------------------------------*
*
* File:   sigsrctest
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/sigsrctest.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/07/19 22:48:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <crtdbg.h>
#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include <rw/thr/mutex.h>
#include <rw\cstring.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "queue.h"
#include "exchange.h"
#include "netports.h"
#include "logger.h"
#include "message.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pcreturn.h"
#include "msg_signal.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "connection.h"
#include "pointtypes.h"

BOOL           bQuit = FALSE;

void ReadTheInboundAndDispose(CtiConnection &Connect);

BOOL MyCtrlHandler(DWORD fdwCtrlType)
{
   switch(fdwCtrlType)
   {

   /* Handle the CTRL+C signal. */

   case CTRL_C_EVENT:

   case CTRL_CLOSE_EVENT:

   case CTRL_BREAK_EVENT:

   case CTRL_LOGOFF_EVENT:

   case CTRL_SHUTDOWN_EVENT:

   default:

      bQuit = TRUE;
      return TRUE;

   }
}


void main(int argc, char **argv)
{
   char  actn[80];
   char  desc[80];

   if(argc < 6)
   {
      cout << "Arg 1:   dispatch server machine name " << endl;
      cout << "Arg 2:   Minimum point to error       " << endl;
      cout << "Arg 3:   Maximum point to error       " << endl;
      cout << "Arg 4:   UnAck'd (1)/ Ack'd (0) Alarm " << endl;
      cout << "Arg 5:   Loops " << endl;

      exit(-1);
   }

   INT min = atoi(argv[2]);
   INT max = atoi(argv[3]);
   INT unack = atoi(argv[4]);
   int loops = atoi(argv[5]);

   INT tag = unack ? (TAG_UNACKNOWLEDGED_ALARM | TAG_ACTIVE_ALARM) : (TAG_ACTIVE_ALARM);


   try
   {
      int Op, k;
      CtiMultiMsg *pMulti = NULL;

      unsigned    pt = 1;

      if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE))
      {
         cerr << "Could not install control handler" << endl;
         return;
      }


      CtiConnection  Connect(VANGOGHNEXUS, argv[1]);


      // Insert a registration message into the multi
      Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg("SignalSource", rwThreadId(), TRUE));


      CtiPointDataMsg   *pDat = NULL;
      CtiSignalMsg      *pSig = NULL;
/*      CtiEmailMsg       *pEmail = CTIDBG_new CtiEmailMsg(1, CtiEmailMsg::CICustomerEmailType);

      Connect.WriteConnQue(pEmail);
*/
      for(int s = 0; s < loops; s++)
      {
         pMulti = CTIDBG_new CtiMultiMsg;

         for(k = min; !bQuit && k <= max; k++)
         {
            if(pMulti != NULL)
            {
               sprintf(desc, " POINT %d ERROR FORCED BY %s", k, argv[0]);
               sprintf(actn, " POINT ERROR FORCED");

               pSig = CTIDBG_new CtiSignalMsg(k, 0, RWTime().asString() + RWCString(desc), RWCString(actn), GeneralLogType, SignalEvent, "sigsrctest.cpp", tag);

               pMulti->insert(pSig);
            }
            else
            {
               exit(-1);
            }
         }

         Connect.WriteConnQue(pMulti);

         ReadTheInboundAndDispose(Connect);

         Sleep(5000);

      }

      Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0));
      Connect.ShutdownConnection();

   }
   catch(RWxmsg &msg)
   {
      cout << "Tester Exception: ";
      cout << msg.why() << endl;
   }

   exit(0);
}



void ReadTheInboundAndDispose(CtiConnection &Connect)
{
   CtiMessage  *pMsg;

   while( NULL != (pMsg = Connect.ReadConnQue(100)))
   {
      CtiMultiMsg *pMulti = NULL;

      if(pMsg->isA() == MSG_MULTI)
      {
         pMulti = (CtiMultiMsg*)pMsg;
      }

      {
         cout << "Received a nice little message " << __FILE__ << " (" << __LINE__ << ")" << endl;

         if(pMulti)
         {
            cout << "Contains " << pMulti->getCount() << " messages inside" << endl;
         }
      }

      delete pMsg;
   }

   return;
}
