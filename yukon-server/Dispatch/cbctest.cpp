/*-----------------------------------------------------------------------------*
*
* File:   cbctest
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/cbctest.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:49 $
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

#include "exchange.h"
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

      unsigned                   pt = 1;
      CtiMessage                 *pMsg;
      CtiRegistrationMsg         MyMsg(argv[2], rwThreadId(), FALSE);

      RWCollectable *c;

      RWInetPort                    NetPort;
      RWInetAddr                    NetAddr;    // This one for this server!

      NetPort  = RWInetPort( 1599 );
      NetAddr  = RWInetAddr(NetPort);           // This one for this server!

      RWSocketListener SockListen(NetAddr);

      RWSocketPortal psck = SockListen();

      CtiExchange Ex( psck );

      Ex.Out() << MyMsg;
      Ex.Out().vflush();

      Ex.In() >> c;

      pMsg = (CtiMessage*) c;

      switch(pMsg->isA() )
      {
      case MSG_DBCHANGE:
         {
            {
               cout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
         }
      default:
         {
            {
               cout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
         }
      }

   }
   catch(RWxmsg &msg)
   {
      cout << "Tester Exception: ";
      cout << msg.why() << endl;
   }

   exit(0);

}
