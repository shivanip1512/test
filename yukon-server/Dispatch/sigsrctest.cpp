#include "precompiled.h"

#include "queue.h"
#include "logger.h"
#include "message.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pcreturn.h"
#include "msg_signal.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "connection_client.h"
#include "pointtypes.h"
#include "amq_constants.h"

#include <crtdbg.h>
#include <iostream>

using namespace std;

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
   if( argc != 5 )
   {
      cout << "Arg 1:   Minimum point to error       " << endl;
      cout << "Arg 2:   Maximum point to error       " << endl;
      cout << "Arg 3:   UnAck'd (1)/ Ack'd (0) Alarm " << endl;
      cout << "Arg 4:   Loops " << endl;

      exit(-1);
   }

   INT minErr = atoi(argv[1]);
   INT maxErr = atoi(argv[2]);
   INT unack  = atoi(argv[3]);
   int loops  = atoi(argv[4]);

   INT tag = unack ? (TAG_UNACKNOWLEDGED_ALARM | TAG_ACTIVE_ALARM) : (TAG_ACTIVE_ALARM);

   try
   {
      if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE))
      {
         cerr << "Could not install control handler" << endl;
         return;
      }

      // Create client cticonnection
      CtiClientConnection Connect( Cti::Messaging::ActiveMQ::Queue::dispatch );

      // start the connection
      Connect.setName( "SignalSourceTest" );
      Connect.start();

      // Insert a registration message into the multi
      Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg("SignalSource", GetCurrentThreadId(), true), CALLSITE);

/*      CtiEmailMsg       *pEmail = CTIDBG_new CtiEmailMsg(1, CtiEmailMsg::CICustomerEmailType);
      Connect.WriteConnQue(pEmail);
*/
      for( int s = 0; s < loops; s++ )
      {
         auto_ptr<CtiMultiMsg> pMulti( CTIDBG_new CtiMultiMsg );

         for( int k = minErr; !bQuit && k <= maxErr; k++ )
         {
            string desc = " POINT " + CtiNumStr(k) + " ERROR FORCED BY " + argv[0];
            string actn = " POINT ERROR FORCED";

            pMulti->insert( CTIDBG_new CtiSignalMsg( k,
                                                     0,
                                                     CtiTime().asString().c_str() + desc,
                                                     actn,
                                                     GeneralLogType,
                                                     SignalEvent,
                                                     "sigsrctest.cpp",
                                                     tag ));
         }

         Connect.WriteConnQue(pMulti.release(), CALLSITE);

         ReadTheInboundAndDispose( Connect );

         Sleep( 5000 );
      }

      cout << CtiTime() << " Ending Test." << endl;

      Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0), CALLSITE);
      Connect.close();
   }
   catch(...)
   {
       cout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
         pMulti = dynamic_cast<CtiMultiMsg*>(pMsg);
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
