/*-----------------------------------------------------------------------------*
*
* File:   cmdtest
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/cmdtest.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:49 $
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
#include "message.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "connection.h"
#include "pointtypes.h"

BOOL           bQuit = FALSE;

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
   INT point_type;

   {
      cout << "Commands: " << endl;
      cout << "  Loopback Server             : " << CtiCommandMsg::LoopClient << endl;
      cout << "  Clear Alarms                : " << CtiCommandMsg::ClearAlarm << endl;
      cout << "  Ack   Alarms                : " << CtiCommandMsg::AcknowledgeAlarm << endl;
      cout << endl;
   }

   if(argc < 2)
   {
      cout << "Arg 1:   dispatch server machine name" << endl;
      cout << "Arg 2:   Command Integer             " << endl;
      cout << "Arg 3:   Integer argument 1          " << endl;
      cout << "Arg 4:   Integer argument 2...       " << endl;

      exit(-1);
   }

   try
   {
      int i;

      if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE))
      {
         cerr << "Could not install control handler" << endl;
         return;
      }


      CtiConnection  Connect(VANGOGHNEXUS, argv[1]);

      Connect.WriteConnQue( CTIDBG_new CtiRegistrationMsg("CommandSender", rwThreadId(), FALSE) );

      Sleep(1000);

      CtiCommandMsg *Cmd = CTIDBG_new CtiCommandMsg( atoi(argv[2]));

      for(i = 3; i < argc; i++)
      {
         Cmd->getOpArgList().insert( atoi(argv[i]) );
      }

      Connect.WriteConnQue( Cmd );

      Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0));

      Sleep(3000);

      Connect.ShutdownConnection();
   }
   catch(RWxmsg &msg)
   {
      cout << "Tester Exception: " << msg.why() << endl;
   }

   exit(0);

}




