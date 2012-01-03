#include "precompiled.h"

#include "pilserver.h"
#include "msg_dbchg.h"
#include "msg_cmd.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "connection.h"
#include "thread_monitor.h"
#include "portglob.h"


#include "unsolicited_handler.h"
#include "StatisticsManager.h"

using namespace std;

CtiConnection VanGoghConnection;

using namespace Cti;
using namespace Porter;

extern INT RefreshPorterRTDB(const CtiDBChangeMsg *ptr);
extern void applyPortQueueReport(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr);
extern void applyDeviceQueueReport(const long unusedid, CtiDeviceSPtr RemoteDevice, void *lprtid);
extern bool processInputFunction(CHAR Char);
extern void KickPIL();

void DispatchMsgHandlerThread(void *Arg)
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


            auto_ptr<const CtiMessage> MsgPtr(VanGoghConnection.ReadConnQue(2000L));

            TimeNow = CtiTime::now();

            auto_ptr<const CtiDBChangeMsg> dbchg;

            if(MsgPtr.get() != NULL)
            {
                switch(MsgPtr->isA())
                {
                case MSG_DBCHANGE:
                    {
                        dbchg.reset(static_cast<const CtiDBChangeMsg *>(MsgPtr.release()));

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << TimeNow << " Porter has received a " << dbchg->getCategory() << " DBCHANGE message from Dispatch." << endl;
                        }

                        break;
                    }
                case MSG_COMMAND:
                    {
                        const CtiCommandMsg* Cmd = static_cast<const CtiCommandMsg *>(MsgPtr.get());

                        switch(Cmd->getOperation())
                        {
                        case (CtiCommandMsg::Shutdown):
                            {
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

                if( dbchg.get() )
                {
                    //  RTDB has been updated, tell the unsolicited ports
                    UnsolicitedPortsQueue.sendMessageToClients(*dbchg);

                    if( dbchg->getTypeOfChange() == ChangeTypeDelete )
                    {
                        const int pao_category = resolvePAOCategory(dbchg->getCategory());

                       if( pao_category == PAO_CATEGORY_DEVICE ||
                           pao_category == PAO_CATEGORY_PORT )
                       {
                           PorterStatisticsManager.deleteRecord(dbchg->getId());
                       }
                   }
               }

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
