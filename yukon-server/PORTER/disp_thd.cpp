/*-----------------------------------------------------------------------------*
*
* File:   disp_thd
*
* Date:   2/2/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/disp_thd.cpp-arc  $
* REVISION     :  $Revision: 1.34.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <iomanip>
#include <iostream>

#include "ctitime.h"
#include <rw\thr\thrfunc.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw/toolpro/inetaddr.h>
#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include "dsm2.h"
#include "ctinexus.h"
#include "porter.h"

#include "cparms.h"
#include "netports.h"
#include "queent.h"
#include "pil_conmgr.h"
#include "pil_exefct.h"
#include "pilserver.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_dbchg.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "dlldefs.h"
#include "connection.h"
#include "numstr.h"
#include "thread_monitor.h"
#include "mgr_point.h"
#include "pt_base.h"

#include "portglob.h"
#include "ctibase.h"
#include "dllbase.h"

#include "logger.h"
#include "guard.h"
#include "utility.h"

#include "unsolicited_handler.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

CtiConnection VanGoghConnection;

using namespace Cti;
using namespace Porter;

extern INT RefreshPorterRTDB(const CtiDBChangeMsg *ptr);
extern void applyPortQueueReport(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr);
extern void applyDeviceQueueReport(const long unusedid, CtiDeviceSPtr RemoteDevice, void *lprtid);
extern bool processInputFunction(CHAR Char);
extern void KickPIL();
extern void deletePaoStatistics( const long paoId );

void DispatchMsgHandlerThread(VOID *Arg)
{
    extern CtiPortManager PortManager;
    extern CtiDeviceManager DeviceManager;
    extern CtiPILServer PIL;

    BOOL           bServerClosing = FALSE;

    CtiTime         TimeNow;
    CtiTime         LastThreadMonitorTime, NextThreadMonitorReportTime;
    CtiThreadMonitor::State previous;
    CtiTime         RefreshTime          = nextScheduledTimeAlignedOnRate( TimeNow, PorterRefreshRate );

    long pointID = ThreadMonitor.getPointIDFromOffset(CtiThreadMonitor::Porter);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " DispatchMsgHandlerThd started as TID " << rwThreadId() << endl;
    }

    SetThreadName(-1, "DispMsg  ");

    VanGoghConnection.doConnect(VANGOGHNEXUS, VanGoghMachine);
    VanGoghConnection.setName("Porter to Dispatch");
    VanGoghConnection.WriteConnQue(CTIDBG_new CtiRegistrationMsg(PORTER_REGISTRATION_NAME, rwThreadId(), FALSE));

    LastThreadMonitorTime = LastThreadMonitorTime.now();

    CtiTime nowTime;
    CtiTime nextTime = nowTime + 30;
    LONG omc;
    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        try
        {
            omc = OutMessageCount();
            nowTime = nowTime.now();

            if(omc > 10 && nowTime > nextTime)
            {
                nextTime = nextScheduledTimeAlignedOnRate(nowTime, gConfigParms.getValueAsULong("PORTER_ALTQ_RATE", 300));
                processInputFunction(0x71);  //  do an alt-q every n seconds
            }

            if( PIL.isBroken() && !bServerClosing )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " PIL interface is indicating a failure.  Restarting the interface." << endl;
                }
                KickPIL();
            }


            auto_ptr<CtiMessage> MsgPtr(VanGoghConnection.ReadConnQue(2000L));

            TimeNow = CtiTime::now();

            auto_ptr<CtiDBChangeMsg> dbchg;

            if(MsgPtr.get() != NULL)
            {
                switch(MsgPtr->isA())
                {
                case MSG_DBCHANGE:
                    {
                        //  grab a copy of the message, since it'll be deleted at the end of the loop
                        dbchg.reset(static_cast<CtiDBChangeMsg *>(MsgPtr->replicateMessage()));

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << TimeNow << " Porter has received a " << dbchg->getCategory() << " DBCHANGE message from Dispatch." << endl;
                        }

                        if ( dbchg->getTypeOfChange() == ChangeTypeDelete )
                        {
                            deletePaoStatistics( dbchg->getId() );
                        }

                        break;
                    }
                case MSG_COMMAND:
                    {
                        CtiCommandMsg* Cmd = (CtiCommandMsg*)MsgPtr.get();

                        switch(Cmd->getOperation())
                        {
                        case (CtiCommandMsg::Shutdown):
                            {
                                //SetEvent(hPorterEvents[P_QUIT_EVENT]);
                                //PorterQuit = TRUE;
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                                Cmd->dump();
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Shutdown requests by command messages are ignored." << endl;
                                }
                                break;
                            }
                        case (CtiCommandMsg::AreYouThere):
                            {
                                VanGoghConnection.WriteConnQue(Cmd->replicateMessage()); // Copy one back
                                break;
                            }
                        case (CtiCommandMsg::PorterConsoleInput):
                            {
                                CHAR operation = Cmd->getOpArgList().at(1);

                                switch(operation)
                                {
                                case 0x01:
                                    {
                                        PorterDebugLevel = Cmd->getOpArgList().at(2);
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << "  PorterDebugLevel set to 0x" << CtiNumStr(PorterDebugLevel).hex().zpad(8).toString() << endl;

                                        }
                                        break;
                                    }
                                case 0x02:
                                    {
                                        DebugLevel = Cmd->getOpArgList().at(2);
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << "  DebugLevel set to 0x" << CtiNumStr(DebugLevel).hex().zpad(8).toString() << endl;

                                        }
                                        break;
                                    }
                                default:
                                    {
                                        processInputFunction(operation);
                                        break;
                                    }
                                }
                                break;
                            }
                        default:
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "Unhandled command message " << Cmd->getOperation() << " sent to Porter.." << endl;
                            }
                        }
                        break;
                    }

                default:
                    {
                        break;
                    }
                }
            }

            if(TimeNow > RefreshTime || dbchg.get())
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " DispatchMsgHandlerThd beginning a database reload";

                    if(dbchg.get())
                    {
                        dout << " - DBChange message." << endl;
                    }
                    else
                    {
                        dout << " - No DBChange message." << endl;
                    }
                }

                RefreshPorterRTDB(dbchg.get());

                //  RTDB has been updated, tell the unsolicited ports
                UnsolicitedPortsQueue.sendMessageToClients(dbchg.get());

                RefreshTime = nextScheduledTimeAlignedOnRate( TimeNow, PorterRefreshRate );

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " DispatchMsgHandlerThd done reloading" << endl;
                }
            }
            else if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 250L) )
            {
                bServerClosing = TRUE;
            }

            //  Check thread watcher status
            if( pointID != 0 )
            {
                if( (LastThreadMonitorTime.now().seconds() - LastThreadMonitorTime.seconds()) >= 2 )
                {
                    CtiThreadMonitor::State next;
                    LastThreadMonitorTime = LastThreadMonitorTime.now();
                    if( (next = ThreadMonitor.getState()) != previous || LastThreadMonitorTime > NextThreadMonitorReportTime )
                    {
                        previous = next;
                        NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate( LastThreadMonitorTime, CtiThreadMonitor::StandardMonitorTime / 2 );
    
                        VanGoghConnection.WriteConnQue(CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, StatusPointType, ThreadMonitor.getString().c_str()));
                    }
                }
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    } /* End of for */

    VanGoghConnection.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
    VanGoghConnection.ShutdownConnection();
}
