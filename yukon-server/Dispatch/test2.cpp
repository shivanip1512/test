/*-----------------------------------------------------------------------------*
*
* File:   test2
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/test2.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/17 19:02:58 $
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
            cout << "PointData:  point " << pData->getId( ) << ", value: " << pData->getValue( ) << endl;
            retval = 1;
            break;

        case MSG_MULTI:
            msgMulti = (CtiMultiMsg *)message;
            cout << "MultiMsg - contains " << msgMulti->getData( ).entries( ) << " messages" << endl;
            retval = 0;
            for( x = 0; x < msgMulti->getData( ).entries( ); x++ )
            {
                cout << "    multimsg submessage " << (x+1) << endl;
                cout << "        ";
                retval += inspectMessage( (CtiMessage *)(msgMulti->getData( )[x]) );
            }
            break;

        default:
            cout << __FILE__ << " (" << __LINE__ << ") I don't know how to handle messages of type \"" << message->stringID( ) << "\";  skipping" << endl;
    }
    return retval;
}

void main(int argc, char **argv)
{
   if(argc < 5)
   {
      cout << "Arg 1:   vangogh server machine name" << endl;
      cout << "Arg 2:   this app's registration name" << endl;
      cout << "Arg 3:   First point to register for" << endl;
      cout << "Arg 4:   Last point to register for" << endl;
      cout << "Arg 5:   # of messages to receive" << endl;

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
      cout << "Just got the MOAUpload" << endl;
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

               for(int x = 0; x < pChg->getData().entries(); x++)
               {
                  ((CtiPointDataMsg*)(pChg->getData()[x]))->dump();
                  cout << endl;

                  cout << "I just got message #" << i << endl;

                  i++; // Make these count one for each
               }

               i--; // get rid of the miscount!
            }
            else if(c->isA() == MSG_POINTDATA)
            {
               cout << "I just got message #" << i << endl;

               CtiPointDataMsg *pDat = (CtiPointDataMsg *)c;

               pDat->dump();
               cout << endl;
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
      cout << msg.why() << endl;
   }

   exit(0);

}
