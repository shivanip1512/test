
/*-----------------------------------------------------------------------------*
*
* File:   test
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/test.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2003/12/30 21:50:27 $
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
#include "mgr_point.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_email.h"
#include "msg_tag.h"
#include "msg_commerrorhistory.h"
#include "msg_lmcontrolhistory.h"
#include "connection.h"
#include "counter.h"
#include "pointtypes.h"

BOOL           bQuit = FALSE;

void tagExecute(int argc, char **argv);
void tagHelp();
void defaultExecute(int argc, char **argv);
void defaultHelp();

typedef void (*XFUNC)(int argc, char **argv);       // Execution function
typedef void (*HFUNC)();                           // Help Function



typedef struct
{
    char    cmd[24];
    XFUNC   xecute;
    HFUNC   help;

} TESTFUNC_t;

TESTFUNC_t testfunction[] = {
    {"tags", tagExecute, tagHelp},
    {"default", defaultExecute, defaultHelp},
    {"", 0, 0}
};



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

static double GetPointValue(int pointtype)
{
    static bool laststat = false;
    double value = 1.0;

    switch(pointtype)
    {
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

    dout.start();     // fire up the logger thread
    dout.setOutputPath(gLogDirectory.data());
    dout.setOutputFile(argv[0]);
    dout.setToStdOut(true);
    dout.setWriteInterval(0);

    if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE))
    {
        cerr << "Could not install control handler" << endl;
        return;
    }

    for(i = 0; testfunction[i].xecute != 0 && i < sizeof(testfunction) / sizeof(TESTFUNC_t); i++ )
    {
        if(!strnicmp(testfunction[i].cmd, argv[1], strlen(testfunction[i].cmd)))
        {
            (*testfunction[i].xecute)(argc, argv);
            break;
        }
    }


    // Make sure all the logs get output and done!
    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();


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

        CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;
        pM->setMessagePriority(15);
        pM->getData().insert(CTIDBG_new CtiRegistrationMsg(argv[2], rwThreadId(), TRUE));

        CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_ALL_PTS_MASK);

        pM->getData().insert(PtRegMsg);

        Connect.WriteConnQue( pM );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        for(k = 0; k < atoi(argv[4]); k++ )
        {
            pMsg = Connect.ReadConnQue(1000);

            if( NULL != pMsg)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

void tagExecute(int argc, char **argv)
{
    int Op, k;

    unsigned    timeCnt = rwEpoch;
    unsigned    pt = 1;
    CtiMessage  *pMsg;

    CtiPointManager PointMgr;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    try
    {
        int Op, k;

        unsigned    timeCnt = rwEpoch;
        unsigned    pt = 1;
        CtiMessage  *pMsg;


        srand(1);   // This is replicable.

        PointMgr.refreshList();     // This should give me all the points in the box.
        CtiConnection  Connect(VANGOGHNEXUS, argv[2]);

        CtiMultiMsg   *pM  = CTIDBG_new CtiMultiMsg;

        pM->setMessagePriority(15);

        Connect.WriteConnQue(CTIDBG_new CtiRegistrationMsg(argv[3], rwThreadId(), FALSE));
        CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_ALL_PTS_MASK);
        PtRegMsg->setMessagePriority(15);
        Connect.WriteConnQue( PtRegMsg );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Reading inbound messages from registration" << endl;
        }

        while( NULL != (pMsg = Connect.ReadConnQue(1000)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                pMsg->dump();
            }

            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Done reading registration messages" << endl;
        }

        CtiTagMsg *pTag = 0;

        if(argc >= 7)
        {
            // argv[6] is a new string for msg 5
            pTag = CTIDBG_new CtiTagMsg;

            pTag->setInstanceID(atoi(argv[5]));
            pTag->setTagID(1);
            pTag->setPointID(1);
            pTag->setAction(CtiTagMsg::UpdateAction);
            pTag->setDescriptionStr(RWTime().asString() + " TEST");
            pTag->setSource("Corey's Tester");
            pTag->setClientMsgId(52);
            pTag->setUser("Harley's Ghost");

            pTag->dump();
        }
        else if(atoi(argv[5]) == 0)
        {
            pTag = CTIDBG_new CtiTagMsg;

            pTag->setTagID(1);
            pTag->setPointID(1);
            pTag->setAction(CtiTagMsg::AddAction);
            pTag->setDescriptionStr(RWTime().asString() + " TEST");
            pTag->setSource("Corey's Tester");
            pTag->setClientMsgId(57);
            pTag->setUser("Marley's Ghost");

            pTag->dump();
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Removing tag " << atoi(argv[5]) << endl;
            }

            pTag = CTIDBG_new CtiTagMsg;
            pTag->setInstanceID(atoi(argv[5]));
            pTag->setAction(CtiTagMsg::RemoveAction);
            pTag->dump();
        }

        Connect.WriteConnQue(pTag);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Reading inbound messages caused by tag message submission." << endl;
        }

        while( NULL != (pMsg = Connect.ReadConnQue(2500)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                pMsg->dump();
            }

            delete pMsg;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Request application shutdown." << endl;
        }

        Sleep(1000);
        Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
        Connect.ShutdownConnection();
        Sleep(2500);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        cout << "Arg 1:   vangogh server machine name" << endl;
        cout << "Arg 2:   this app's registration name" << endl;
        cout << "Arg 3:   # of loops to send data     " << endl;
        cout << "Arg 4:   sleep duration between loops" << endl;

        exit(-1);
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


            srand(1);   // This is replicable.

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Loading points...." << endl;
            }
            PointMgr.refreshList();     // This should give me all the points in the box.
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " .... Done" << endl;
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
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            while( NULL != (pMsg = Connect.ReadConnQue(5000)))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                pTag->setDescriptionStr(RWTime().asString() + " TEST");
                pTag->setSource("Corey's Tester");
                pTag->setClientMsgId(57);
                pTag->setUser("Marley's Ghost");

                pTag->dump();
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

            CtiPointManager::CtiRTDBIterator itr(PointMgr.getMap());

            for(k = 0; !bQuit && PointMgr.entries() > 0 && k < atoi(argv[3]); k++)
            {
                if( !itr() )
                {
                    itr.reset();    // We just want a sequential walk up the point id list!
                    itr();
                }

                CtiPoint *pPoint = itr.value();

                while(pPoint->getID() <= 0)
                {
                    if( !itr() )
                    {
                        itr.reset();    // We just want a sequential walk up the point id list!
                        itr();
                    }
                    pPoint = itr.value();
                }

                pt = k;

                Connect.WriteConnQue(CTIDBG_new CtiCommErrorHistoryMsg( pPoint->getDeviceID(), SYSTEM, 0, "ERROR TEST"));
                Connect.WriteConnQue(CTIDBG_new CtiLMControlHistoryMsg( pPoint->getDeviceID(), 1, 0, RWTime(), -1, 100 ));
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
                        pChg->getData().insert(pData);
                        pChg->getData().insert( CTIDBG_new CtiSignalMsg() );

                        // pChg->getData().insert(pEmail->replicateMessage());


                        if(pt && !(pt % 250))
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Sent Point Change " << k << endl;
                            }

                            Connect.WriteConnQue(pChg);
                            pChg = NULL;
                            pChg = CTIDBG_new CtiMultiMsg();
                        }
                        else
                        {

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Inserted Point Change " << k << endl;
                            }
                        }
                    }

                    while( NULL != (pMsg = Connect.ReadConnQue(0)))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            INT cnt;
            while( (cnt = Connect.outQueueCount()) > 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** OutQueue has **** " << cnt << " entries" << endl;
                }
                Sleep(1000);
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime()  << " **** OutQueue is cleared" << endl;
            }

            while( NULL != (pMsg = Connect.ReadConnQue(2500)))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Inbound message Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        cout << " This is interesting " << endl;
        for(int i = 0; i < atoi(argv[3]); i++)
        {
            DoTheNasty(argc, argv);
            Sleep(atoi(argv[4]));
        }
    }
}
