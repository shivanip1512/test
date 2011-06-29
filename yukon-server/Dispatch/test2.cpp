/*-----------------------------------------------------------------------------*
*
* File:   test2
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/test2.cpp-arc  $
* REVISION     :  $Revision: 1.8.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include <rw/thr/mutex.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "queue.h"
#include "connection.h"
#include "netports.h"
#include "message.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_pcreturn.h"
#include "collectable.h"

int inspectMessage( CtiMessage *message )
{
    CtiMultiMsg *msgMulti;
    CtiPointDataMsg *pData;
    int x, retval;

    switch( message->isA( ) )
    {
        case MSG_POINTDATA:
            pData = (CtiPointDataMsg *)message;
            cout << "PointData:  point " << pData->getId( ) << ", value: " << pData->getValue( ) << Cti::endl;
            retval = 1;
            break;

        case MSG_MULTI:
            msgMulti = (CtiMultiMsg *)message;
            cout << "MultiMsg - contains " << msgMulti->getData( ).size( ) << " messages" << Cti::endl;
            retval = 0;
            for( x = 0; x < msgMulti->getData( ).size( ); x++ )
            {
                cout << "    multimsg submessage " << (x+1) << Cti::endl;
                cout << "        ";
                retval += inspectMessage( (CtiMessage *)(msgMulti->getData( )[x]) );
            }
            break;

        default:
          {
             retval = -1;
            cout << __FILE__ << " (" << __LINE__ << ") I don't know how to handle messages of type \"" << message->stringID( ) << "\";  skipping" << Cti::endl;
    }
    }
    return retval;
}

void main(int argc, char **argv)
{
   if(argc < 5)
   {
      cout << "Arg 1:   vangogh server machine name" << Cti::endl;
      cout << "Arg 2:   this app's registration name" << Cti::endl;
      cout << "Arg 3:   First point to register for" << Cti::endl;
      cout << "Arg 4:   Last point to register for" << Cti::endl;
      cout << "Arg 5:   # of messages to receive" << Cti::endl;

      exit(-1);
   }

   try
   {
      RWWinSockInfo info;

      int i;
      int Op, k;

      unsigned                   pt = 1;
      CtiMessage                 *IGMsg;
      CtiCommandMsg              NoOpMsg;     // Defaults to a NoOp request.
      CtiRegistrationMsg         RegMsg(argv[2], rwThreadId(), FALSE);

      Op = CtiCommandMsg::ClientAppShutdown;

      CtiCommandMsg      TerminateMsg(Op, 15);     // I want a Shutdown request here

      RWCollectable *c;

      CtiConnection  Connect(VANGOGHNEXUS, argv[1]);

      Connect.WriteConnQue( RegMsg.replicateMessage() );

      /*
       *  Register for a few points which "test.exe" will change.
       */
      CtiPointRegistrationMsg    PtRegMsg(REG_NONE);

      for(i = atoi(argv[3]); i < atoi(argv[4]); i++)
      {
         PtRegMsg.insert(i);
      }

      Connect.WriteConnQue(PtRegMsg.replicateMessage());


      c = Connect.ReadConnQue();
      cout << "Just got the MOAUpload" << Cti::endl;
      ((CtiMessage*)c)->dump();
      delete c;


      for(i=0; i < atoi(argv[5]); i++)
      {
         c = Connect.ReadConnQue(60000);

         if(c != NULL)
         {
            if(c->isA() == MSG_MULTI)
            {
               CtiMultiMsg *pChg = (CtiMultiMsg*)c;

               for(int x = 0; x < pChg->getData().size(); x++)
               {
                  ((CtiPointDataMsg*)(pChg->getData()[x]))->dump();
                  cout << Cti::endl;

                  cout << "I just got message #" << i << Cti::endl;

                  i++; // Make these count one for each
               }

               i--; // get rid of the miscount!
            }
            else if(c->isA() == MSG_POINTDATA)
            {
               cout << "I just got message #" << i << Cti::endl;

               CtiPointDataMsg *pDat = (CtiPointDataMsg *)c;

               pDat->dump();
               cout << Cti::endl;
            }


            delete c;   //Make sure to delete this - its on the heap
         }
         else
         {
            Connect.WriteConnQue( CTIDBG_new CtiCommandMsg(CtiCommandMsg::LoopClient,15) );
            // break; // for
         }
      }

      Connect.WriteConnQue( TerminateMsg.replicateMessage() );
   }
   catch(RWxmsg &msg)
   {
      cout << "Tester Exception: ";
      cout << msg.why() << Cti::endl;
   }

   exit(0);

}
