/*-----------------------------------------------------------------------------*
*
* File:   sigtest
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/sigtest.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:50 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


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
#include "message.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_signal.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_pcreturn.h"
#include "collectable.h"

void main(int argc, char **argv)
{
   if(argc != 4)
   {
      cout << "Arg 1:   vangogh server machine name" << endl;
      cout << "Arg 2:   this app's registration name" << endl;
      cout << "Arg 3:   # of loops to receive data  " << endl;

      exit(-1);
   }

   try
   {
      RWWinSockInfo info;

      int Op, k;

      unsigned                   pt = 1;
      CtiMessage                 *IGMsg;
      CtiCommandMsg              NoOpMsg;     // Defaults to a NoOp request.
      CtiRegistrationMsg         RegMsg(argv[2], rwThreadId(), FALSE);

      Op = CtiCommandMsg::ClientAppShutdown;

      CtiCommandMsg      TerminateMsg(Op, 15);     // I want a Shutdown request here

      RWCollectable *c;

      RWSocketPortal psck(RWInetAddr(VANGOGHNEXUS, argv[1]));
      CtiExchange Ex(psck);

      Ex.Out() << RegMsg;
      Ex.Out().vflush();

      /*
       *  Register for a few points which "test.exe" will change.
       */
      CtiPointRegistrationMsg    PtRegMsg(REG_EVENTS | REG_ALARMS | REG_NO_UPLOAD);

      Ex.Out() << PtRegMsg;
      Ex.Out().vflush();

      for(int i=0; i < atoi(argv[3]); i++)
      {
         Ex.In() >> c;

         if(c != NULL)
         {
            if(c->isA() == MSG_MULTI)
            {
               CtiMultiMsg *pChg = (CtiMultiMsg*)c;

               for(int x = 0; x < pChg->getData().entries(); x++)
               {
                  ((CtiMessage*)(pChg->getData()[x]))->dump();
               }
            }
            else if(c->isA() == MSG_SIGNAL)
            {
               CtiSignalMsg* pSig = (CtiSignalMsg*) c;
               cout << RWTime() << " **** SIGNAL RECEIVED **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

               pSig->dump();
            }



            delete c;   //Make sure to delete this - its on the heap
         }
         else
         {
            break; // for
         }
      }

      Ex.Out() << TerminateMsg;
      Ex.Out().vflush();
   }
   catch(RWxmsg &msg)
   {
      cout << "Tester Exception: ";
      cout << msg.why() << endl;
   }

   exit(0);

}
