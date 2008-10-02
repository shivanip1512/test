/*-----------------------------------------------------------------------------*
*
* File:   test
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/test.cpp-arc  $
* REVISION     :  $Revision: 1.48 $
* DATE         :  $Date: 2008/10/02 18:27:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <crtdbg.h>
#include <windows.h>
#include <iostream>

#include <rw/thr/thrfunc.h>
#include <rw/thr/mutex.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "thread.h"
#include "queue.h"
#include "exchange.h"
#include "netports.h"
#include "numstr.h"
#include "message.h"
#include "mgr_point.h"
#include "msg_cmd.h"
#include "msg_dbchg.h"
#include "msg_reg.h"
#include "msg_pcreturn.h"
#include "msg_pcrequest.h"
#include "msg_signal.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_tag.h"
#include "msg_commerrorhistory.h"
#include "msg_lmcontrolhistory.h"
#include "connection.h"
#include "counter.h"
#include "pointtypes.h"
#include "msg_notif_email.h"
#include "msg_notif_email_attachment.h"
#include "thread_monitor.h"
#include "thread_register_data.h"

#include "ctitime.h"
#include "ctidate.h"

using namespace std;

BOOL bQuit = FALSE;

void shutdown(int argc, char **argv);
void multiExecute(int argc, char **argv);
void multiHelp();
void historyExecute(int argc, char **argv);
void historyHelp();
void dbtimeExecute(int argc, char **argv);
void dbtimeHelp();
int tagProcessInbounds(CtiMessage *&pMsg, int clientId);
void tagExecute(int argc, char **argv);
void tagHelp();
void defaultExecute(int argc, char **argv);
void defaultHelp();
void notifEmailExecute( int argc, char **argv );
void seasonExecute(int argc, char **argv);
void lmExecute(int argc, char **argv);
void lmHelp();
void dbchangeExecute(int argc, char **argv);
void socketExecute(int argc, char **argv);
void testThreads( int argc, char **argv );
void DoTheNasty(int argc, char **argv);
void booya( void *haha );
void ha( void *la );
void pilExecute(int argc, char **argv);
void pilHelp();
void pointReqExecute(int argc, char **argv);
void pointReqHelp();

typedef void (*XFUNC)(int argc, char **argv);       // Execution function
typedef void (*HFUNC)();                           // Help Function

typedef struct
{
    char    cmd[24];
    XFUNC   xecute;
    HFUNC   help;

} TESTFUNC_t;


TESTFUNC_t testfunction[] = {
    {"shutdown", shutdown, defaultHelp},
    {"seasonreset", seasonExecute, defaultHelp},
    {"tags", tagExecute, tagHelp},
    {"multi", multiExecute, multiHelp},
    {"dbchange", dbchangeExecute, defaultHelp},
    {"default", defaultExecute, defaultHelp},
    {"notif", notifEmailExecute, defaultHelp},
    {"lm", lmExecute, lmHelp},
    {"socket", socketExecute, lmHelp},
    {"thread", testThreads, defaultHelp },
    {"history", historyExecute, historyHelp},
    {"dbtime", dbtimeExecute, dbtimeHelp},
    {"pil", pilExecute, pilHelp},
    {"ptreq", pointReqExecute, pointReqHelp},
    {"", 0, 0}
};



//===========================================================================================================
//===========================================================================================================

class EThread : public CtiThread
{
public:

   EThread( string n, CtiThreadRegData::Behaviours type, int t = 10 );
   ~EThread();
   void logOut( void );

protected:

   virtual void run( void );

private:

   CtiThreadRegData::Behaviours  _type;
   string                        _name;
   int                           _heart_beat;
   bool                          _log_out;
   bool                          _comm;
};

EThread::EThread( string n, CtiThreadRegData::Behaviours type, int t ) :
   _type( type ),
   _log_out( false ),
   _comm( true ),
   _name( n )
{
   _heart_beat = t;     //how often we'll tickle the monitor
}

EThread::~EThread()
{
}

void EThread::logOut( void )
{
   _log_out = true;
}

void EThread::run( void )
{
   int cnt = 0;

   while( !isSet( SHUTDOWN ) )
   {
      sleep( 1000 );

      if( cnt++ == _heart_beat - 4 )
      {
         if( _comm )
         {
            if( _log_out )
            {
               {
                  CtiLockGuard<CtiLogger> doubt_guard( dout );
                  dout << "*************************** " << _name << " logging out" << endl;
               }

               CtiThreadRegData *data = new CtiThreadRegData( getID(), _name, CtiThreadRegData::LogOut, _heart_beat, ha, 0 );
               _comm = false;
               ThreadMonitor.tickle( data );
            }
            else
            {
               CtiThreadRegData *data = new CtiThreadRegData( getID(), _name, _type, _heart_beat, ha, 0 );
               ThreadMonitor.tickle( data );
            }

            cnt = 0;
         }
      }
/*
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << _name << " working" << endl;
      }
*/
   }
   set( SHUTDOWN, false ); //reset the flag so we can start it again
}

//===========================================================================================================
//===========================================================================================================

void testThreads( int argc, char **argv )
{
   EThread  *bob = 0;
   EThread  *sam = 0;
   EThread  *joe = 0;
   EThread  *rat = 0;
   EThread  *sal = 0;
   int      index = 0;

   ThreadMonitor.start();

   Sleep( 3000 );

   if( sal != NULL )
   {
      if( !( sal->isRunning() ) )
      {
         sal->start();
      }
   }
   else
   {
      sal = new EThread( "sal", CtiThreadRegData::Action );
      sal->start();
   }

   for( ;; )
   {
      Sleep( 1000 );
      index++;

      if( !( index % 33 ))
      {
         if( sal && sal->isRunning() )
         {
            sal->logOut();
         }
      }

      if( !( index % 25 ))
      {
         if( bob && bob->isRunning() )
         {
            bob->interrupt( CtiThread::SHUTDOWN );
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << "bob exiting" << endl;
            }
         }
         else
         {
            delete bob;
            bob = new EThread( "bob", CtiThreadRegData::Action );
            bob->start();
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << "bob starting" << endl;
            }
         }
      }

      if( !( index % 35 ))
      {
         if( sam && sam->isRunning() )
         {
            sam->interrupt( CtiThread::SHUTDOWN );
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << "sam exiting" << endl;
            }
         }
         else
         {
            delete sam;
            sam = new EThread( "sam", CtiThreadRegData::None, 25 );
            sam->start();
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << "sam starting" << endl;
            }
         }
      }

      if( !( index % 94 ))
      {
         if( joe && joe->isRunning() )
         {
            joe->interrupt( CtiThread::SHUTDOWN );
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << "joe exiting" << endl;
            }
         }
         else
         {
            delete joe;
            joe = new EThread( "joe", CtiThreadRegData::Action, 45 );
            joe->start();
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << "joe starting" << endl;
            }
         }
      }

      if( !( index % 61 ))
      {
         if( rat && rat->isRunning() )
         {
            rat->interrupt( CtiThread::SHUTDOWN );
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << "rat exiting" << endl;
            }
         }
         else
         {
            delete rat;
            rat = new EThread( "rat", CtiThreadRegData::Action, 80 );
            rat->start();
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << "rat starting" << endl;
            }
         }
      }
/*
      if( !( index % 10 ))
      {
         ThreadMonitor.dump();
      }
*/
   }
}

//===========================================================================================================
//===========================================================================================================

void ha( void *la )
{
   if( la != NULL )
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << "I'm the Alternate function w/" << la << endl;
   }
   else
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << "I'm the Alternate function..." << endl;
   }
}

//===========================================================================================================
//===========================================================================================================


void booya( void *haha )
{
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << "I'm the function of DEATH! Buahahahaha" << endl;
   }
}

//===========================================================================================================
//===========================================================================================================

static double GetPointValue(int pointtype)
{
    static bool laststat = false;
    double value = 1.0;

    switch(pointtype)
    {
    case CalculatedStatusPointType:
    case StatusPointType:
        {
            laststat = !laststat;
            value = (double)(laststat ? 1.0 : 0.0);
            break;
        }
    case AnalogPointType:
    case PulseAccumulatorPointType:
    case DemandAccumulatorPointType:
    case CalculatedPointType:
    case StatusOutputPointType:
    case AnalogOutputPointType:
    case SystemPointType:
        {
            value = (double)rand() * 1000.0;
            break;
        }
    }

    return value;
}

void main(int argc, char **argv)
{
    int i;
    INT point_type;

    RWWinSockInfo info;

    dout.start();     // fire up the logger thread
    dout.setOutputPath(gLogDirectory);
    dout.setOutputFile(argv[0]);
    dout.setToStdOut(true);
    dout.setWriteInterval(0);

    if(argc > 1)
    {
        for(i = 0; testfunction[i].xecute != 0 && i < sizeof(testfunction) / sizeof(TESTFUNC_t); i++ )
        {
            if(!strnicmp(testfunction[i].cmd, argv[1], strlen(testfunction[i].cmd)))
            {
                try
                {
                    (*testfunction[i].xecute)(argc, argv);
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                break;
            }
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " You must supply at least one argument to indicate the test to run." << endl;
        }
        for(i = 0; testfunction[i].xecute != 0 && i < sizeof(testfunction) / sizeof(TESTFUNC_t); i++ )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "   Function : " << testfunction[i].cmd << endl;
            }
        }
    }

    // Make sure all the logs get output and done!
    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();


    exit(0);
}


void DoTheNasty(int argc, char **argv)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    try
    {
        int Op, k;

        //ts//unsigned    timeCnt = 0;
        unsigned    pt = 1;
        CtiMessage  *pMsg;

        CtiConnection  Connect(VANGOGHNEXUS, argv[2]);

        CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;
        pM->setMessagePriority(15);
        pM->getData().push_back(CTIDBG_new CtiRegistrationMsg(argv[3], rwThreadId(), TRUE));

        CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_ALL_PTS_MASK);

        pM->getData().push_back(PtRegMsg);

        Connect.WriteConnQue( pM );

        Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ResetControlHours, 0));

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        for(k = 0; k < atoi(argv[5]); k++ )
        {
            pMsg = Connect.ReadConnQue(1000);

            if( NULL != pMsg)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    pMsg->dump();
                }
                delete pMsg;
            }
        }

        Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0));
    }
    catch(RWxmsg &msg)
    {
        cout << "Tester Exception: ";
        cout << msg.why() << endl;
    }
}

//===========================================================================================================
//===========================================================================================================

void notifEmailExecute( int argc, char **argv )
{
   int i = 123;

   #if 1
   CtiPointManager PointMgr;
   PointMgr.refreshList();     // This should give me all the points in the box.
   PointMgr.DumpList();     // This should give me all the points in the box.
#else
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }
   Sleep(5000);
   #endif

   CtiConnection Connect( 1515, argv[2] );

   CtiNotifEmailMsg  *message = new CtiNotifEmailMsg;
   message->setSubject( string( "Doom" ) );
   message->setTo( string( "eric@cannontech.com" ) );
   message->setBody( string( "Wanted to let you know that the world is ending..." ) );
   message->setNotifGroupId( 0 );
   message->setToCC( string( "ericschmit@mn.rr.com" ) );
   message->setToBCC( string( "ericschmit@mn.rr.com" ) );

   message->setMessagePriority(15);
   Connect.WriteConnQue( message->replicateMessage() );
   Connect.WriteConnQue( message );


   Sleep( 1000 );


   Connect.ShutdownConnection();
   Sleep( 2500 );
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   return;
}

void tagExecute(int argc, char **argv)
{
    int Op, k;

    //ts//unsigned    timeCnt = 0;
    unsigned    pt = 1;
    CtiMessage  *pMsg;

    CtiPointManager PointMgr;

    try
    {
        int Op, k;

        //ts//unsigned    timeCnt = 0;
        unsigned    pt = 1;
        CtiMessage  *pMsg;


        srand(1);   // This is replicable.

        PointMgr.refreshList();     // This should give me all the points in the box.
        CtiConnection  Connect(1515, argv[2]);

        CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;

        pM->setMessagePriority(15);

        //Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg(argv[3], rwThreadId(), FALSE));
        CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_ALL_PTS_MASK | REG_ALARMS);
        PtRegMsg->setMessagePriority(15);
        //Connect.WriteConnQue( PtRegMsg );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Reading inbound messages from registration" << endl;
        }

        while( NULL != (pMsg = Connect.ReadConnQue(1000)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                pMsg->dump();
            }

            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Done reading registration messages" << endl;
        }

        CtiNotifEmailMsg  *message = new CtiNotifEmailMsg;
        message->setSubject( string( "Doom" ) );
        message->setTo( string( "eric@cannontech.com" ) );
        message->setBody( string( "Wanted to let you know that the world is ending..." ) );
        message->setNotifGroupId( 0 );
        message->setToCC( string( "ericschmit@mn.rr.com" ) );
        message->setToBCC( string( "ericschmit@mn.rr.com" ) );

        message->setMessagePriority(15);
        Connect.WriteConnQue( message->replicateMessage() );
        Connect.WriteConnQue( message );

        #if 0
        CtiTagMsg *pTag = 0;

        pTag = CTIDBG_new CtiTagMsg;

        pTag->setTagID(1);
        pTag->setPointID(1);
        pTag->setAction(CtiTagMsg::AddAction);
        pTag->setDescriptionStr(CtiTime().asString() + " TEST");
        pTag->setSource("Corey's Tester");
        pTag->setClientMsgId(21);
        pTag->setUser("Marley's Ghost");

        pTag->dump();
        Connect.WriteConnQue(pTag);


        // Wait for our message back with the instance id...

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Looking for a response from dispatch!" << endl;
        }

        int instance = 0;
        while( NULL != (pMsg = Connect.ReadConnQue(30000)) )
        {
            instance = tagProcessInbounds( pMsg, 21 );
            delete pMsg;

            if(instance != 0)
            {
                break;
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Dispatch responded or timed out!" << endl;
        }

        if(instance != 0)
        {
            pTag = CTIDBG_new CtiTagMsg;

            pTag->setInstanceID(instance);
            pTag->setTagID(1);
            pTag->setPointID(1);
            pTag->setAction(CtiTagMsg::UpdateAction);
            pTag->setDescriptionStr(CtiTime().asString() + " TEST UPDATE");
            pTag->setSource("Corey's Tester");
            pTag->setClientMsgId(22);
            pTag->setUser("Harley's Ghost");
            pTag->setReferenceStr("What is a reference string");

            pTag->dump();
            Connect.WriteConnQue(pTag);

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  TAG UPDATED!" << endl;
            }

            // Remove

            pTag = CTIDBG_new CtiTagMsg;
            pTag->setInstanceID(instance);
            pTag->setPointID(1);
            pTag->setAction(CtiTagMsg::RemoveAction);
            pTag->setDescriptionStr(CtiTime().asString() + " TEST REMOVE");
            pTag->setReferenceStr("This string is a reference string");
            pTag->dump();
            Connect.WriteConnQue(pTag);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  TAG REMOVED!" << endl;
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "   UNABLE TO DETERMINE INSTANCE ID!! " << endl;
            }

            Sleep(3000);
        }

        #endif
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Reading inbound messages caused by tag message submission." << endl;
        }

        while( NULL != (pMsg = Connect.ReadConnQue(2500)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                pMsg->dump();
            }

            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Request application shutdown." << endl;
        }

        Sleep(1000);
        Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
        Connect.ShutdownConnection();
        Sleep(2500);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return;
}

void tagHelp()
{
    return;
}

void defaultHelp()
{
    return;
}

void defaultExecute(int argc, char **argv)
{
    CtiPointManager PointMgr;

    if(argc < 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Arg 1:   vangogh server machine name" << endl;
        dout << "Arg 2:   this app's registration name" << endl;
        dout << "Arg 3:   # of loops to send data     " << endl;
        dout << "Arg 4:   sleep duration between loops" << endl;
    }

    if(argc == 5)
    {
        RWWinSockInfo info;

        try
        {
            int Op, k;

            unsigned    timeCnt = PASTDATE;
            unsigned    pt = 1;
            CtiMessage  *pMsg;


            srand(1);   // This is replicable.

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Loading points...." << endl;
            }
            PointMgr.refreshList();     // This should give me all the points in the box.
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " .... Done" << endl;
            }

            CtiConnection  Connect(VANGOGHNEXUS, argv[1]);


            CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;

            pM->setMessagePriority(15);

            Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg(argv[2], rwThreadId(), FALSE));
            CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_ALL_PTS_MASK);
            PtRegMsg->setMessagePriority(15);

            Connect.WriteConnQue( PtRegMsg );

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            while( NULL != (pMsg = Connect.ReadConnQue(5000)))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    pMsg->dump();
                }

                delete pMsg;
            }

            CtiTagMsg *pTag = 0;

            if(atoi(argv[4]) == 0)
            {
                pTag = CTIDBG_new CtiTagMsg;

                pTag->setTagID(1);
                pTag->setPointID(1);
                pTag->setAction(CtiTagMsg::AddAction);
                pTag->setDescriptionStr(CtiTime().asString().c_str() + string(" TEST"));
                pTag->setSource("Corey's Tester");
                pTag->setClientMsgId(57);
                pTag->setUser("Marley's Ghost");

                pTag->dump();
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Removing tag " << atoi(argv[4]) << endl;
                }

                pTag = CTIDBG_new CtiTagMsg;
                pTag->setInstanceID(atoi(argv[4]));
                pTag->setAction(CtiTagMsg::RemoveAction);
                pTag->dump();
            }

            Connect.WriteConnQue(pTag);

            CtiPointDataMsg  *pData = NULL;
            CtiMultiMsg   *pChg  = CTIDBG_new CtiMultiMsg();
            CtiCommErrorHistoryMsg *pCEHMsg = 0;
            CtiLMControlHistoryMsg *pLMCMsg = 0;

            CtiPointManager::spiterator itr = PointMgr.begin();
            CtiPointManager::spiterator end = PointMgr.end();

            for(k = 0; !bQuit && PointMgr.entries() > 0 && k < atoi(argv[3]); k++)
            {
                itr++;
                if(  itr == end )
                {
                    itr = PointMgr.begin();    // We just want a sequential walk up the point id list!
                }

                CtiPointSPtr pPoint = itr->second;

                while(pPoint->getID() <= 0)
                {
                    itr++;
                    if(  itr == end )
                    {
                        itr = PointMgr.begin();    // We just want a sequential walk up the point id list!
                    }
                    pPoint = itr->second;
                }

                pt = k;

                Connect.WriteConnQue(CTIDBG_new CtiCommErrorHistoryMsg( pPoint->getDeviceID(), SYSTEM, 0, "ERROR TEST"));
                Connect.WriteConnQue(CTIDBG_new CtiLMControlHistoryMsg( pPoint->getDeviceID(), 1, 0, CtiTime(), -1, 100 ));
                Connect.WriteConnQue(CTIDBG_new CtiPointDataMsg( pPoint->getID(), 1, NormalQuality,  pPoint->getType(), __FILE__));


                pData = CTIDBG_new CtiPointDataMsg( pPoint->getID(), GetPointValue(pPoint->getType()), NormalQuality,  pPoint->getType(), __FILE__);

                pData->setTime( timeCnt );
                pData->setTags( TAG_POINT_MUST_ARCHIVE );

                timeCnt += 5;

                if(pData != NULL)
                {
                    if(pChg != NULL)
                    {
                        // Add a single point change to the message

                        if(0)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            pData->dump();
                        }
                        pChg->getData().push_back(pData);
                        pChg->getData().push_back( CTIDBG_new CtiSignalMsg() );

                        // pChg->getData().insert(pEmail->replicateMessage());


                        if(pt && !(pt % 250))
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Sent Point Change " << k << endl;
                            }

                            Connect.WriteConnQue(pChg);
                            pChg = NULL;
                            pChg = CTIDBG_new CtiMultiMsg();
                        }
                        else
                        {

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Inserted Point Change " << k << endl;
                            }
                        }
                    }

                    while( NULL != (pMsg = Connect.ReadConnQue(0)))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            pMsg->dump();
                        }

                        delete pMsg;
                    }

                    Sleep(atoi(argv[4]));
                }
            }

            if(pChg != NULL)
            {
                Connect.WriteConnQue(pChg);
                pChg = NULL;
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            INT cnt;
            while( (cnt = Connect.outQueueCount()) > 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** OutQueue has **** " << cnt << " entries" << endl;
                }
                Sleep(1000);
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime()  << " **** OutQueue is cleared" << endl;
            }

            while( NULL != (pMsg = Connect.ReadConnQue(2500)))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    pMsg->dump();
                }

                delete pMsg;
            }

            Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0));

            Sleep(5000);
        }
        catch(RWxmsg &msg)
        {
            cout << "Tester Exception: ";
            cout << msg.why() << endl;
        }

    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        for(int i = 0; i < atoi(argv[4]); i++)
        {
            DoTheNasty(argc, argv);
            Sleep(atoi(argv[4]));
        }
    }
}


int tagProcessInbounds(CtiMessage *&pMsg, int clientId)
{
    int instance = 0;
    CtiTagMsg *pTag;

    pMsg->dump();

    if(pMsg->isA() == MSG_TAG)
    {
        pTag = (CtiTagMsg*)pMsg;

        if(pTag->getClientMsgId() == clientId)
        {
            instance = pTag->getInstanceID();
        }
    }
    else if(pMsg->isA() == MSG_MULTI)
    {
        CtiMessage *pMyMsg;
        CtiMultiMsg *pMulti = (CtiMultiMsg *)pMsg;

        CtiMultiMsg_vec::iterator itr = pMulti->getData().begin();

        for( ; pMulti->getData().end() != itr;  itr++ )
        {
            pMyMsg = (CtiMessage*)*itr;
            instance = tagProcessInbounds(pMyMsg, clientId);
            if(instance == clientId)
            {
                break;
            }
        }

    }

    return instance;
}

void seasonExecute(int argc, char **argv)
{
    int Op, k;

    //ts//unsigned    timeCnt = 0;
    unsigned    pt = 1;
    CtiMessage  *pMsg;

    CtiPointManager PointMgr;

    try
    {
        int Op, k;

        //ts//unsigned    timeCnt = 0;
        unsigned    pt = 1;
        CtiMessage  *pMsg;


        srand(1);   // This is replicable.

        PointMgr.refreshList();     // This should give me all the points in the box.
        CtiConnection  Connect(VANGOGHNEXUS, "127.0.0.1");

        CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;

        pM->setMessagePriority(15);

        Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg("SeasonReset", rwThreadId(), FALSE));
        CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_ALL_PTS_MASK | REG_ALARMS);
        PtRegMsg->setMessagePriority(15);
        Connect.WriteConnQue( PtRegMsg );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Reading inbound messages from registration" << endl;
        }

        while( NULL != (pMsg = Connect.ReadConnQue(1000)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                pMsg->dump();
            }

            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Done reading registration messages" << endl;
        }

        CtiCommandMsg *pCmd = CTIDBG_new CtiCommandMsg(CtiCommandMsg::ResetControlHours, 7);
        pCmd->insert(-1);
        Connect.WriteConnQue(pCmd);


        // Wait for our message back with the instance id...

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Looking for a response from dispatch!" << endl;
        }


        while( NULL != (pMsg = Connect.ReadConnQue(2500)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                pMsg->dump();
            }

            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Request application shutdown." << endl;
        }

        Sleep(1000);
        Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
        Connect.ShutdownConnection();
        Sleep(2500);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return;
}

void lmHelp()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Arg 1:   vangogh server machine name" << endl;
        dout << "Arg 2:   this app's registration name" << endl;
        dout << "Arg 3:   # of loops to send data     " << endl;
        dout << "Arg 4:   sleep duration between loops" << endl;
    }

    return;
}

void lmExecute(int argc, char **argv)
{
    CtiPointManager PointMgr;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Running the LM ramp module" << endl;
    }

    if(argc < 7)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Arg 2:   vangogh server machine name" << endl;
        dout << "Arg 3:   this app's registration name" << endl;
        dout << "Arg 4:   Point Id to tickle" << endl;
        dout << "Arg 5:   steps 1 to n to send as point value" << endl;
        dout << "Arg 6:   max seconds randomization on sleep (5 min + 0 to n rand)" << endl;
    }

    if(argc == 7)
    {
        RWWinSockInfo info;

        try
        {
            int i, steps, k;

            //ts//unsigned    timeCnt = 0;
            unsigned    pt = atoi(argv[4]);
            CtiMessage  *pMsg;


            steps = atoi(argv[5]);
            int maxrand = atoi(argv[6]);

            srand(1);   // This is replicable.

            PointMgr.refreshList();     // This should give me all the points in the box.

            CtiConnection  Connect(VANGOGHNEXUS, argv[2]);

            CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;

            pM->setMessagePriority(15);

            Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg(argv[3], rwThreadId(), FALSE));
            CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_ALL_PTS_MASK);
            PtRegMsg->setMessagePriority(15);

            Connect.WriteConnQue( PtRegMsg );

            while( NULL != (pMsg = Connect.ReadConnQue(1000)))
            {
                delete pMsg;
            }

            CtiPointDataMsg  *pData = NULL;
            CtiMultiMsg   *pChg  = CTIDBG_new CtiMultiMsg();

            CtiPointSPtr pPoint = PointMgr.getEqual( pt );


            for(i = 1; i <= steps && !bQuit; i++ )
            {
                pData = CTIDBG_new CtiPointDataMsg( pPoint->getID(), i, NormalQuality,  pPoint->getType(), __FILE__);

                if(pData != NULL)
                {
                    if(pChg != NULL)
                    {
                        // Add a single point change to the message
                        pChg->getData().push_back(pData);

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Message Sent: Id " << pData->getId() << " value = " << pData->getValue() << endl;
                        }

                        Connect.WriteConnQue(pChg);

                        pChg = NULL;
                        pChg = CTIDBG_new CtiMultiMsg();
                    }

                    while( NULL != (pMsg = Connect.ReadConnQue(0)))
                    {
                        delete pMsg;
                    }

                    unsigned long stime = 300 * 1000 + (((double)rand() / (double)RAND_MAX) * maxrand) * 1000.0;

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Sleeping for " << stime << " millis" << endl;
                    }

                    Sleep(stime);
                }
            }


            INT cnt;
            while( (cnt = Connect.outQueueCount()) > 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** OutQueue has **** " << cnt << " entries" << endl;
                }
                Sleep(1000);
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime()  << " **** OutQueue is cleared" << endl;
            }

            while( NULL != (pMsg = Connect.ReadConnQue(2500)))
            {
                delete pMsg;
            }

            Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0));

            Sleep(5000);
        }
        catch(RWxmsg &msg)
        {
            cout << "Tester Exception: ";
            cout << msg.why() << endl;
        }

    }
}


void  dbchangeExecute(int argc, char **argv)
{
    int Op, k;

    //ts//unsigned    timeCnt = 0;
    unsigned    pt = 1;
    CtiMessage  *pMsg;

    CtiPointManager PointMgr;

    try
    {
        int id = 0, k;

        //ts//unsigned    timeCnt = 0;
        unsigned    pt = 1;
        CtiMessage  *pMsg;


        PointMgr.refreshList();     // This should give me all the points in the box.
        CtiConnection  Connect(VANGOGHNEXUS, argv[2]);

        CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;

        pM->setMessagePriority(15);

        Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg(argv[3], rwThreadId(), FALSE));
        CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_NONE | REG_ALARMS);
        PtRegMsg->setMessagePriority(15);
        Connect.WriteConnQue( PtRegMsg );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Reading inbound messages from registration" << endl;
        }

        while( NULL != (pMsg = Connect.ReadConnQue(500)))
        {
            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Done reading registration messages" << endl;
        }

        CtiDBChangeMsg *pChg = 0;

        if(argc >= 5)
        {
            id = atoi(argv[4]);
        }

        pChg = CTIDBG_new CtiDBChangeMsg(id, ChangePAODb, "Device", "Device", ChangeTypeUpdate);

        pChg->setSource("VgSrctest");

        pChg->dump();
        Connect.WriteConnQue(pChg);


        // Wait for our message back with the instance id...

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Looking for a response from dispatch!" << endl;
        }

        int instance = 0;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Reading inbound messages caused by tag message submission." << endl;
        }

        while( NULL != (pMsg = Connect.ReadConnQue(2500)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Request application shutdown." << endl;
        }

        Sleep(1000);
        Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
        Connect.ShutdownConnection();
        Sleep(2500);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return;
}

void socketExecute(int argc, char **argv)
{
    unsigned int cnt = 0;
    SOCKET tempSock;
    typedef vector< SOCKET > SV_t;
    SV_t sockvector;

    RWWinSockInfo info;

    WSASetLastError(0);

    while((tempSock = socket (AF_INET, SOCK_STREAM, 0)) != INVALID_SOCKET)
    {
        cnt++;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "  SOCKET " << cnt << " returned = " << tempSock << endl;
        }

        sockvector.push_back(tempSock);
    }


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << CtiTime() << " Error getting Socket:  " << WSAGetLastError() << endl;
    }


    while(!sockvector.empty())
    {
        tempSock = sockvector.back();
        closesocket(tempSock);
        sockvector.pop_back();
    }



}


void shutdown(int argc, char **argv)
{
    CtiPointManager PointMgr;

    {
        RWWinSockInfo info;

        try
        {
            int Op, k;

            //ts//unsigned    timeCnt = 0;
            unsigned    pt = 1;
            CtiMessage  *pMsg;

            srand(1);   // This is replicable.

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Loading points...." << endl;
            }
            PointMgr.refreshList();     // This should give me all the points in the box.
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " .... Done" << endl;
            }

            CtiConnection  Connect(VANGOGHNEXUS, argv[2]);


            CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;

            pM->setMessagePriority(15);

            Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg(argv[3], rwThreadId(), FALSE));
            CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_ALL_PTS_MASK);
            PtRegMsg->setMessagePriority(15);

            Connect.WriteConnQue( PtRegMsg );

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            while( NULL != (pMsg = Connect.ReadConnQue(5000)))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    pMsg->dump();
                }

                delete pMsg;
            }


            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            INT cnt;
            while( (cnt = Connect.outQueueCount()) > 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** OutQueue has **** " << cnt << " entries" << endl;
                }
                Sleep(1000);
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime()  << " **** OutQueue is cleared" << endl;
            }

            while( NULL != (pMsg = Connect.ReadConnQue(2500)))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    pMsg->dump();
                }

                delete pMsg;
            }

            Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::Shutdown, 15));
            Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0));

            while( (cnt = Connect.outQueueCount()) > 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** OutQueue has **** " << cnt << " entries" << endl;
                }
                Sleep(1000);
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime()  << " **** OutQueue is cleared" << endl;
            }

            while( NULL != (pMsg = Connect.ReadConnQue(2500)))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    pMsg->dump();
                }

                delete pMsg;
            }

            Sleep(5000);
        }
        catch(RWxmsg &msg)
        {
            cout << "Tester Exception: ";
            cout << msg.why() << endl;
        }

    }
}


void multiExecute(int argc, char **argv)
{
    int Op, k;

    //ts//unsigned    timeCnt = 0;
    unsigned    pt = 1;
    CtiMessage  *pMsg;

    CtiPointManager PointMgr;

    try
    {
        int Op, k;

        //ts//unsigned    timeCnt = 0;
        unsigned    pt = 1;
        CtiMessage  *pMsg;

        if(argc < 5)
        {
            multiHelp();
            return;
        }

        // PointMgr.refreshList();     // This should give me all the points in the box.
        CtiConnection  Connect(VANGOGHNEXUS, argv[2]);

        CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;

        pM->setMessagePriority(15);

        Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg("multiExecute", rwThreadId(), FALSE));
        CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_NONE);
        PtRegMsg->setMessagePriority(15);
        Connect.WriteConnQue( PtRegMsg );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Reading inbound messages from registration" << endl;
        }

        while( NULL != (pMsg = Connect.ReadConnQue(1000)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                pMsg->dump();
            }

            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Done reading registration messages" << endl;
        }


        //
        // ACH
        //

        pM->insert(CTIDBG_new CtiPointDataMsg( atoi(argv[3]), atoi(argv[4]), NormalQuality,  InvalidPointType, __FILE__));
        pM->insert(CTIDBG_new CtiPointDataMsg( atoi(argv[3]), atoi(argv[4]), NormalQuality,  InvalidPointType, __FILE__));

        Connect.WriteConnQue( pM );
        pM = 0;


        while( NULL != (pMsg = Connect.ReadConnQue(2500)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                pMsg->dump();
            }

            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Request application shutdown." << endl;
        }

        Sleep(1000);
        Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
        Connect.ShutdownConnection();
        Sleep(2500);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return;
}

void multiHelp()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " multiExecute - Help" << endl;
        dout << " Arg1: multi " << endl <<
            " Arg2: <dispatch_machine_or_ip> " <<
            " Arg3: <point id to send> " <<
            " Arg4: <point value> " <<
            endl;
    }
    return;
}


void historyExecute(int argc, char **argv)
{
    int Op, k;

    //ts//unsigned    timeCnt = 0;
    unsigned    pt = 1;

    try
    {
        int Op, k;

        //ts//unsigned    timeCnt = 0;
        unsigned    pt = 1;
        unsigned    rows = 0;
        CtiMessage  *pMsg;

        typedef map< pair<unsigned long, unsigned long>, double > historyCol_t;

        historyCol_t histCol;

        if(argc < 4)
        {
            historyHelp();
            return;
        }

        pt = atoi(argv[2]);
        rows = atoi(argv[3]);

        CtiTime dbtnow;
        CtiTime dbtpast;


        while(histCol.size() < rows)
        {
            dbtpast.addDays(-1);        // Back up one day from now.
            string sql = "select * from rawpointhistory where pointid = " + CtiNumStr(pt) + " and timestamp > '" + dbtpast.date().asString() + "' and timestamp <= '" + dbtnow.date().asString() + "' order by timestamp desc";
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << sql << endl;
            }

            {
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();

                RWDBReader rdr = ExecuteQuery( conn, sql );

                //Assume there is only one?
                while( rdr() && histCol.size() < rows )
                {
                    long cid;
                    long pid;
                    CtiTime ts;
                    long qual;
                    double val;
                    long milli;

                    rdr >> cid;
                    rdr >> pid;
                    rdr >> ts;
                    rdr >> qual;
                    rdr >> val;
                    rdr >> milli;

                    historyCol_t::_Pairib inspair = histCol.insert( historyCol_t::value_type( make_pair(ts.seconds(), milli), val) );

                    if(inspair.second == false)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << " Failed insert on duplicate " << cid << " " << ts << "." << CtiNumStr(milli).zpad(3).toString() << " " << val << endl;
                        }
                    }
                }
            }

            dbtnow.addDays(-1);        // Back up one day from last select..
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " There are " << histCol.size() << " entries found" << endl;
        }

        return;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}

void historyHelp()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " historyExecute - Help" << endl;
        dout << " Arg1: history " << endl <<
            " Arg2: <pointid to search> " << endl <<
            " Arg3: <number of history points> " <<
            endl;
    }
    return;
}

void dbtimeExecute(int argc, char **argv)
{
    try
    {
        CtiTime now;
        CtiTime ttime;

        CtiTime dbt(CtiDate(29, 10, 2005), 23, 30, 0);
        CtiTime tt = dbt;

        int period = 300;

        for(int i = 0; i < 250; i++)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << i << "  DBTime " << dbt.asString() << " Time " << tt.asString() << endl;
            }

            tt = tt.now();
            dbt =  CtiTime(dbt.seconds() + period);
            if(dbt < tt)
            {
                dbt =  tt;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}

void dbtimeHelp()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " dbtimeExecute - Help" << endl;
        dout << " Arg1: dbtime " << endl << endl;
    }
    return;
}


void pilExecute(int argc, char **argv)
{
    int Op, k;

    unsigned    timeCnt = 0;
    unsigned    pt = 1;
    CtiMessage  *pMsg;

    CtiPointManager PointMgr;

    try
    {
        int Op, k;

        unsigned    timeCnt = 0;
        unsigned    pt = 1;
        CtiMessage  *pMsg;

        if(argc < 3)
        {
            pilHelp();
            return;
        }

        // PointMgr.refreshList();     // This should give me all the points in the box.
        CtiConnection  Connect(PORTERINTERFACENEXUS, argv[2]);

        CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;

        pM->setMessagePriority(15);

        Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg("pilExecute", rwThreadId(), FALSE));

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Reading inbound messages from registration" << endl;
        }

        while( NULL != (pMsg = Connect.ReadConnQue(1000)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                pMsg->dump();
            }

            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Done reading registration messages" << endl;
        }


        /*
            CtiRequestMsg(long device_id,
                     const CtiString& command_string = CtiString(),
                     long user_message_id = 0L,
                     long transmission_id = 0L,
                     long route_id        = 0L,
                     int macro_offset     = 0,
                     int attempt_num      = 0,
                     int options_field    = 0,
                     int priority_base    = 7);
        */

        #if 0
        pM->insert( CTIDBG_new CtiRequestMsg(1117, "control close",1 , 1) );
        pM->insert( CTIDBG_new CtiRequestMsg(1119, "control open",1 , 1) );
        pM->insert( CTIDBG_new CtiRequestMsg(1113, "control close",1 , 1) );
        pM->insert( CTIDBG_new CtiRequestMsg(1115, "control open",1 , 1) );
        pM->insert( CTIDBG_new CtiRequestMsg(1111, "control close",1 , 1) );
        pM->insert( CTIDBG_new CtiRequestMsg(1121, "control close",1 , 1) );
        pM->insert( CTIDBG_new CtiRequestMsg(1123, "control close",1 , 1) );
        pM->insert( CTIDBG_new CtiRequestMsg(1125, "control close",1 , 1) );
        pM->insert( CTIDBG_new CtiRequestMsg(1127, "control open",1 , 1) );
        pM->insert( CTIDBG_new CtiRequestMsg(1129, "control open",1 , 1) );

        Connect.WriteConnQue( pM );
        #else
        Connect.WriteConnQue( CTIDBG_new CtiRequestMsg(1117, "control close",1 , 1) );
        Sleep(1000);
        Connect.WriteConnQue( CTIDBG_new CtiRequestMsg(1119, "control open",1 , 1) );
        Sleep(1000);
        Connect.WriteConnQue( CTIDBG_new CtiRequestMsg(1113, "control close",1 , 1) );
        Sleep(1000);
        Connect.WriteConnQue( CTIDBG_new CtiRequestMsg(1115, "control open",1 , 1) );
        Sleep(1000);
        Connect.WriteConnQue( CTIDBG_new CtiRequestMsg(1111, "control close",1 , 1) );
        Sleep(1000);
        Connect.WriteConnQue( CTIDBG_new CtiRequestMsg(1121, "control close",1 , 1) );
        Sleep(1000);
        Connect.WriteConnQue( CTIDBG_new CtiRequestMsg(1123, "control close",1 , 1) );
        Sleep(1000);
        Connect.WriteConnQue( CTIDBG_new CtiRequestMsg(1125, "control close",1 , 1) );
        Sleep(1000);
        Connect.WriteConnQue( CTIDBG_new CtiRequestMsg(1127, "control open",1 , 1) );
        Sleep(1000);
        Connect.WriteConnQue( CTIDBG_new CtiRequestMsg(1129, "control open",1 , 1) );

        #endif
        pM = 0;

        while( NULL != (pMsg = Connect.ReadConnQue(2500)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                pMsg->dump();
            }

            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Request application shutdown." << endl;
        }

        Sleep(1000);
        Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
        Connect.ShutdownConnection();
        Sleep(2500);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return;
}

void pilHelp()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " pilExecute - Help" << endl;
        dout << " Arg1: multi " << endl <<
            " Arg2: <porter_machine_or_ip> " <<
            endl;
    }
    return;
}

void pointReqExecute(int argc, char **argv)
{
    int Op, k;

    unsigned    timeCnt = 0;
    unsigned    pt = 1;
    CtiMessage  *pMsg;

    CtiPointManager PointMgr;

    try
    {
        int Op, k;

        unsigned    timeCnt = 0;
        unsigned    pt = 1;
        CtiMessage  *pMsg;

        if(argc < 3)
        {
            pointReqHelp();
            return;
        }


        int lp = 1;

        if(argc >= 4)
        {
            lp = atoi(argv[3]);
        }


        for(int i = 0; i < lp; i++)
        {
            CtiConnection  Connect(VANGOGHNEXUS, argv[2]);
            CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;

            pM->setMessagePriority(15);

            Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg("pointReqExecute_"+CtiNumStr(rwThreadId()), rwThreadId(), FALSE));
            CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_ALL_PTS_MASK);
            PtRegMsg->setMessagePriority(15);
            Connect.WriteConnQue( PtRegMsg );

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Reading inbound messages from registration" << endl;
            }

            while( NULL != (pMsg = Connect.ReadConnQue(5000)))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    pMsg->dump();
                }

                delete pMsg;
                pMsg = 0;
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Done reading registration messages" << endl;
            }


            //
            // ACH
            //

            CtiCommandMsg *pCmd = CTIDBG_new CtiCommandMsg(CtiCommandMsg::PointDataRequest, 15);

            // Ask for the first ten points.
            pCmd->insert(1);
            pCmd->insert(2);
            pCmd->insert(3);
            pCmd->insert(4);
            pCmd->insert(5);
            pCmd->insert(6);
            pCmd->insert(7);
            pCmd->insert(8);
            pCmd->insert(9);
            pCmd->insert(10);

            Connect.WriteConnQue( pCmd );
            pCmd = 0;


            while( NULL != (pMsg = Connect.ReadConnQue(1500)))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    pMsg->dump();
                }

                delete pMsg;
                pMsg = 0;
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Request application shutdown." << endl;
            }

            Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
            Sleep(2500);
            Connect.ShutdownConnection();
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return;
}

void pointReqHelp()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " pointReqExecute - Help" << endl;
        dout << " Arg1: <dispatch_machine_or_ip> " << endl;
    }
    return;
}

