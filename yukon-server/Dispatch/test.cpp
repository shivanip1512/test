/*-----------------------------------------------------------------------------*
*
* File:   test
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/test.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:50 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

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
#include "msg_email.h"
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

void DoTheNasty(int argc, char **argv);


void main(int argc, char **argv)
{
   INT point_type;

   if(argc < 5)
   {
      cout << "Arg 1:   vangogh server machine name" << endl;
      cout << "Arg 2:   this app's registration name" << endl;
      cout << "Arg 3:   # of loops to send data     " << endl;
      cout << "Arg 4:   sleep duration between loops" << endl;

      exit(-1);
   }

   if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE))
   {
      cerr << "Could not install control handler" << endl;
      return;
   }


   if(argc == 5)
   {
      RWWinSockInfo info;

      try
      {
         int Op, k;

         unsigned    timeCnt = rwEpoch;
         unsigned    pt = 1;
         CtiMessage  *pMsg;

         CtiConnection  Connect(VANGOGHNEXUS, argv[1]);


         CtiMultiMsg   *pM  = new CtiMultiMsg;

         pM->setMessagePriority(15);

         Connect.WriteConnQue(new CtiRegistrationMsg(argv[2], rwThreadId(), FALSE));
         // pM->getData().insert(new CtiRegistrationMsg(argv[2], rwThreadId(), FALSE));

         CtiPointRegistrationMsg    *PtRegMsg = new CtiPointRegistrationMsg(REG_NONE);
/*
         PtRegMsg->insert(1);
         PtRegMsg->insert(2);
         PtRegMsg->insert(3);
         PtRegMsg->insert(4);
         PtRegMsg->insert(5);
         PtRegMsg->insert(6);
         PtRegMsg->insert(7);
         PtRegMsg->insert(8);
*/

         Connect.WriteConnQue( PtRegMsg );


         Connect.setBlockingWrites(TRUE);

#if 0
         CtiEmailMsg *pEmail = new CtiEmailMsg(1L, CtiEmailMsg::CICustomerEmailType);

         pEmail->setUser("emailuser");
         pEmail->setMessagePriority(15);
         pEmail->setSubject("Test Subject");
         pEmail->setText("This is an email test.");

         Connect.WriteConnQue( pEmail->replicateMessage() );
#endif

         CtiPointDataMsg  *pData = NULL;
         CtiMultiMsg   *pChg  = new CtiMultiMsg();

         for(k = 0; !bQuit && k < atoi(argv[3]); k++)
         {
            pt = k;

            pData = new CtiPointDataMsg((pt % 5) + 1, 1.0, NormalQuality,  InvalidPointType, __FILE__);
            pData->setTime( timeCnt );
            pData->setTags( TAG_POINT_MUST_ARCHIVE );

            timeCnt += 50;

            if(pData != NULL)
            {
               if(pChg != NULL)
               {
                  // Add a single point change to the message

                  //pData->dump();
                  pChg->getData().insert(pData);
                  // pChg->getData().insert(pEmail->replicateMessage());


                  if(pt && !(pt % 4))
                  {
                     cout << "Sent Point Change " << k << endl;

                     Connect.WriteConnQue(pChg);
                     pChg = NULL;
                     pChg = new CtiMultiMsg();
                  }
                  else
                  {
                     cout << "Inserted Point Change " << k << endl;
                  }
               }

               while( NULL != (pMsg = Connect.ReadConnQue(0)))
               {
                  pMsg->dump();

                  delete pMsg;
               }

               Sleep(atoi(argv[4]));
            }
         }
         // delete pEmail;

         if(pChg != NULL)
         {
            Connect.WriteConnQue(pChg);
            pChg = NULL;
         }

         INT cnt;
         while( (cnt = Connect.outQueueCount()) > 0 )
         {
            cout << RWTime() << " **** OutQueue has **** " << cnt << " entries" << endl;
            Sleep(1000);
         }
         cout << RWTime() << " **** OutQueue is cleared" << endl;

         Sleep(30000);

         Connect.WriteConnQue(new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0));
      }
      catch(RWxmsg &msg)
      {
         cout << "Tester Exception: ";
         cout << msg.why() << endl;
      }

   }
   else
   {
      for(int i = 0; i < atoi(argv[3]); i++)
      {
         DoTheNasty(argc, argv);
         Sleep(atoi(argv[4]));
      }
   }



   exit(0);

}


void DoTheNasty(int argc, char **argv)
{
   try
   {
      int Op, k;

      unsigned    timeCnt = rwEpoch;
      unsigned    pt = 1;
      CtiMessage  *pMsg;

      CtiConnection  Connect(VANGOGHNEXUS, argv[1]);

      CtiMultiMsg   *pM  = new CtiMultiMsg;
      pM->setMessagePriority(15);
      pM->getData().insert(new CtiRegistrationMsg(argv[2], rwThreadId(), TRUE));

      CtiPointRegistrationMsg    *PtRegMsg = new CtiPointRegistrationMsg(REG_ALL_PTS_MASK);

      pM->getData().insert(PtRegMsg);

      Connect.WriteConnQue( pM );
      Connect.setBlockingWrites(TRUE);

      for(k = 0; k < atoi(argv[4]); k++ )
      {
         pMsg = Connect.ReadConnQue(1000);

         if( NULL != pMsg)
         {
            pMsg->dump();
            delete pMsg;
         }
      }

      Connect.WriteConnQue(new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0));
   }
   catch(RWxmsg &msg)
   {
      cout << "Tester Exception: ";
      cout << msg.why() << endl;
   }
}

