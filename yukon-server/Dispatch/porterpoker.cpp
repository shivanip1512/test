#include "precompiled.h"

#include "queue.h"
#include "message.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_dbchg.h"
#include "connection_client.h"
#include "counter.h"
#include "pointtypes.h"
#include "numstr.h"
#include "pt_accum.h"
#include "amq_constants.h"
#include "logManager.h"

#include <crtdbg.h>

#include <windows.h>
#include <iostream>
using namespace std;

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
    // fire up the logger thread
    doutManager.setOutputPath    ( gLogDirectory );
    doutManager.setOutputFile    ( argv[0] );
    doutManager.setToStdOut      ( true );
    doutManager.start();

/*
    CTILOG_INFO(dout,
            endl <<"Alt - C     Reset (cold start) all CCU 711's in system"<<
            endl <<"Alt - D     Send Emetcon \"Doubles\" to field devices"<<
            endl <<"Alt - E     Trace error communications only"<<
            endl <<"Alt - F     Toggle trace filtering off, or reload from environment."<<
            endl <<"             PORTER_TRACE_PORT"<<
            endl <<"             PORTER_TRACE_REMOTE"<<
            endl <<"Alt - H     This help screen"<<
            endl <<"Alt - L     Toggle printer logging"<<
            endl <<"Alt - P     Purge all port queues. (Careful)"<<
            endl <<"Alt - Q     Display port queue counts / stats"<<
            endl <<"Alt - R     Download all CCU Default Routes"<<
            endl <<"Alt - S     Issue a system wide timesync"<<
            endl <<"Alt - T     Trace all communications"<<
            endl <<"Alt - V     Issue Device DBChange"<<
            endl <<"Alt - W     Issue Point DBChange"
            );
*/


    if(argc < 3)
    {
        CTILOG_INFO(dout,
            endl <<
            endl <<
            endl <<"Arg 1:  DISPATCH.EXE server machine name"<<
            endl <<"Arg 2:  Command to request of porter"<<
            endl <<
            endl <<"         0x74 - Trace all communications"<<
            endl <<"         0x65 - Trace error communications only"<<
            endl <<"         0x72 - Download all CCU Default Routes"<<
            endl <<"         0x64 - Send Emetcon \"Doubles\" to field devices"<<
            endl <<"         0x6d - Dump memory (if compiled in)"<<
            endl <<"         0x6c - Toggle printer logging"<<
            endl <<"         0x63 - Reset (cold start) all CCU 711's in system"<<
            endl <<"         0x6b - Reset the PIL interface"<<
            endl <<"         0x73 - Timesync all syncable devices"<<
            endl <<"         0x70 - Purge port queues"<<
            endl <<"         0x71 - Print port queue information"<<
            endl <<"         0x01 0xXXXXXXXX - Set Porter's PorterDebugLevel"<<
            endl <<"         0x02 0xXXXXXXXX - Set Porter's DBDEBUGLEVEL"<<
            endl <<"         0x03 0xXXXXXXXX - Send DISPATCH a device DBChange"<<
            endl <<"         0x04 0xXXXXXXXX - Send DISPATCH a point DBChange"<<
            endl <<"         0x05 0xXXXXXXXX - Send DISPATCH a config config DBChange"<<
            endl <<"         0x06 0xXXXXXXXX - Send DISPATCH a config device DBChange"<<
            endl <<"         0x07 (0x01 analog 0x02 status) 0xXXXXXXXXX Send 25k messages to pointid 0xXXXXXX"
            );
    }

    if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE))
    {
        cerr << "Could not install control handler" << endl;
        return;
    }


    if(argc >= 3)
    {
        try
        {
            char *pch;

            int command = (int)strtoul(argv[2], &pch, 0);
            int dblvl = 0;

            // Create client cticonnection
            CtiClientConnection Connect( Cti::Messaging::ActiveMQ::Queue::dispatch );

            // start the connection
            Connect.start();

            Connect.WriteConnQue(new CtiRegistrationMsg(argv[0], GetCurrentThreadId(), false), CALLSITE);
            Connect.WriteConnQue(std::make_unique<CtiPointRegistrationMsg>(REG_NOTHING), CALLSITE);

            if( !(command == 0x03 || command == 0x04 || command == 0x05 || command == 0x06 || command == 0x07) )
            {
                CtiCommandMsg *pCmd = new CtiCommandMsg(CtiCommandMsg::PorterConsoleInput, 15);

                pCmd->getOpArgList().push_back(-1);    // Token
                pCmd->getOpArgList().push_back(command);

                if( (command == 0x01 || command == 0x02) )
                {
                    if( argc >= 4 )
                    {
                        dblvl = strtoul(argv[3], &pch, 0);
                        pCmd->getOpArgList().push_back(dblvl);
                    }
                    else
                    {
                        pCmd->getOpArgList().push_back(0x00000000);
                    }
                }

                Connect.WriteConnQue(pCmd, CALLSITE);
            }
            else
            {
                if( argc >= 4 )
                {
                    dblvl = strtoul(argv[3], &pch, 0);
                }
                else
                {
                    dblvl = 0;
                }

                if( command == 0x03 )
                {
                    CtiDBChangeMsg *chg = new CtiDBChangeMsg(dblvl, ChangePAODb, "device", "", ChangeTypeUpdate);
                    Connect.WriteConnQue(chg, CALLSITE);
                }
                else if( command == 0x04 )
                {
                    CtiDBChangeMsg *chg = new CtiDBChangeMsg(dblvl, ChangePointDb, "point", "", ChangeTypeUpdate);
                    Connect.WriteConnQue(chg, CALLSITE);
                }
                else if( command == 0x05 )
                {
                    CtiDBChangeMsg *chg = new CtiDBChangeMsg(dblvl, ChangeConfigDb, "device config", "config", ChangeTypeUpdate);

                    Connect.WriteConnQue(chg, CALLSITE);
                }
                else if( command == 0x06 )
                {
                    CtiDBChangeMsg *chg = new CtiDBChangeMsg(dblvl, ChangeConfigDb, "device config", "device", ChangeTypeUpdate);
                    Connect.WriteConnQue(chg, CALLSITE);
                }
                else if( command == 0x07 )
                {
                    int analogOrStat, pointID;
                    if( argc >= 4 )
                    {
                        analogOrStat = strtoul(argv[3], &pch, 0);
                        pointID = strtoul(argv[4], &pch, 0);

                        CTILOG_INFO(dout, "pt id "<< pointID <<" analogStuff "<< analogOrStat);

                        for( int i=0; i<=25000; i++ )
                        {
                            CtiPointDataMsg *pData = new CtiPointDataMsg(pointID, i, NormalQuality, analogOrStat == 0x01 ? AnalogPointType : StatusPointType);
                            Connect.WriteConnQue(pData, CALLSITE);
                            if( i%1000 == 0 )
                            {
                                CTILOG_INFO(dout, i <<" messages sent");
                            }
                        }
                    }

                    CTILOG_INFO(dout, "Entering 25 second sleep to help prevent Dispatch Purge");

                    Sleep(25000);
                }
            }

            Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0), CALLSITE);

            CtiMessage *pMsg;
            while( 0 != (pMsg = Connect.ReadConnQue(1000)) )
            {
                delete pMsg;
                pMsg = 0;
            }

            Sleep(2500);
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }

    }

    exit(0);
}
