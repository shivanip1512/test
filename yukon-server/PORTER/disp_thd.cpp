#include "precompiled.h"

#include "pilserver.h"
#include "msg_dbchg.h"
#include "msg_cmd.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "connection_client.h"
#include "amq_constants.h"
#include "thread_monitor.h"
#include "portglob.h"
#include "GlobalSettings.h"
#include "ServiceMetricReporter.h"

#include "unsolicited_handler.h"
#include "StatisticsManager.h"
#include "MessageCounter.h"

using namespace std;

CtiClientConnection VanGoghConnection( Cti::Messaging::ActiveMQ::Queue::dispatch );

using namespace Cti;
using namespace Porter;

extern INT RefreshPorterRTDB(const CtiDBChangeMsg *ptr);
extern void applyPortQueueReport(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr);
extern void applyDeviceQueueReport(const long unusedid, CtiDeviceSPtr RemoteDevice, void *lprtid);
extern bool processInputFunction(CHAR Char);
extern void KickPIL();

void DispatchMsgHandlerThread()
{
    extern CtiPortManager PortManager;
    extern CtiDeviceManager DeviceManager;
    extern Cti::Pil::PilServer PIL;

    BOOL           bServerClosing = FALSE;

    CtiTime         TimeNow;
    CtiTime         LastThreadMonitorTime, NextThreadMonitorReportTime;
    CtiThreadMonitor::State previous;
    CtiTime         RefreshTime          = nextScheduledTimeAlignedOnRate( TimeNow, PorterRefreshRate );

    MessageCounter mc("Dispatch->Porter");

    long pointID = ThreadMonitor.getProcessPointID();
    ServiceMetrics::MetricReporter metricReporter {
        Cti::ServiceMetrics::CpuPointOffsets::Porter,
        Cti::ServiceMetrics::MemoryPointOffsets::Porter,
        "Porter",
        PORTER_APPLICATION_NAME };

    CTILOG_INFO(dout, "DispatchMsgHandlerThd started");

    SetThreadName(-1, "DispMsg  ");

    VanGoghConnection.setName("Porter to Dispatch");
    VanGoghConnection.start();
    VanGoghConnection.WriteConnQue(CTIDBG_new CtiRegistrationMsg(PORTER_REGISTRATION_NAME, GetCurrentThreadId(), false), CALLSITE);

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
                CTILOG_WARN(dout, "PIL interface is indicating a failure.  Restarting the interface.");

                KickPIL();
            }


            unique_ptr<const CtiMessage> MsgPtr(VanGoghConnection.ReadConnQue(2000L));

            TimeNow = CtiTime::now();

            mc.increment();

            unique_ptr<const CtiDBChangeMsg> dbchg;

            if(MsgPtr.get() != NULL)
            {
                switch(MsgPtr->isA())
                {
                case MSG_DBCHANGE:
                    {
                        dbchg.reset(static_cast<const CtiDBChangeMsg *>(MsgPtr.release()));
                        const auto dbCategory = resolveDBCategory(dbchg->getCategory());

                        CTILOG_INFO(dout, "Porter has received a DBCHANGE message from Dispatch, " << dbchg->getCategory() << " " << dbchg->getId() << " " <<
                            (dbchg->getTypeOfChange() == ChangeTypeAdd    ? "ADD" :
                             dbchg->getTypeOfChange() == ChangeTypeDelete ? "DELETE" :
                             dbchg->getTypeOfChange() == ChangeTypeUpdate ? "UPDATE" : "UNKNOWN"));

                        if (dbCategory == CtiDBChangeCategory::GlobalSetting)
                        {
                            GlobalSettings::reload();

                            doutManager.reloadMaxFileSize();
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
                                CTILOG_WARN(dout, "Porter has received a shutdown request from Dispatch (shutdown requests by command messages are ignored)"<<
                                        *Cmd);
                                break;
                            }
                        case (CtiCommandMsg::AreYouThere):
                            {
                                VanGoghConnection.WriteConnQue(Cmd->replicateMessage(), CALLSITE); // Copy one back
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
                                        CTILOG_INFO(dout, "PorterDebugLevel set to 0x"<< CtiNumStr(PorterDebugLevel).hex().zpad(8));
                                        break;
                                    }
                                case 0x02:
                                    {
                                        DebugLevel = Cmd->getOpArgList().at(2);
                                        CTILOG_INFO(dout, "DebugLevel set to 0x"<< CtiNumStr(DebugLevel).hex().zpad(8));
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
                                CTILOG_ERROR(dout, "Unhandled command message "<< Cmd->getOperation() <<" sent to Porter..");
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
                CTILOG_INFO(dout, "DispatchMsgHandlerThd beginning a database reload"<<
                        (dbchg.get() ? " - DBChange message" : " - No DBChange message"));

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

                CTILOG_INFO(dout, "DispatchMsgHandlerThd done reloading");
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

                        VanGoghConnection.WriteConnQue(
                            CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality,
                            StatusPointType, ThreadMonitor.getString().c_str()), CALLSITE);
                    }
                }
            }

            metricReporter.reportCheck(CompileInfo, VanGoghConnection);
        }
        catch (boost::thread_interrupted &)
        {
            CTILOG_INFO(dout, "DispatchMsgHandlerThread interrupted");
            bServerClosing = TRUE;
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    } /* End of for */

    VanGoghConnection.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15), CALLSITE);
    VanGoghConnection.close();
}
