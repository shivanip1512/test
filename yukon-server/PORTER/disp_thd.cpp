/*-----------------------------------------------------------------------------*
*
* File:   disp_thd
*
* Date:   2/2/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/disp_thd.cpp-arc  $
* REVISION     :  $Revision: 1.32 $
* DATE         :  $Date: 2008/07/14 14:49:55 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
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

#include "port_udp.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

CtiConnection VanGoghConnection;

namespace Cti { namespace Porter {

    extern UDPMessenger UDPInterfaceQueue;
}
}

extern INT RefreshPorterRTDB(void *ptr = NULL);
extern void applyPortQueueReport(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr);
extern void applyDeviceQueueReport(const long unusedid, CtiDeviceSPtr RemoteDevice, void *lprtid);
extern bool processInputFunction(CHAR Char);
extern void KickPIL();

void DispatchMsgHandlerThread(VOID *Arg)
{
    extern CtiPortManager PortManager;
    extern CtiDeviceManager DeviceManager;
    extern CtiPILServer PIL;

    BOOL           bServerClosing = FALSE;

    CtiTime         TimeNow;
    CtiTime         LastThreadMonitorTime;
    CtiThreadMonitor::State previous;
    CtiTime         RefreshTime          = nextScheduledTimeAlignedOnRate( TimeNow, PorterRefreshRate );
    CtiMessage     *MsgPtr              = NULL;
    UINT           changeCnt = 0;
    UCHAR          checkCount = 0;
    CtiDBChangeMsg *pChg = NULL;
    long pointID = ThreadMonitor.getPointIDFromOffset(CtiThreadMonitor::PointOffsets::Porter);

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
    ULONG omc;
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


            MsgPtr = VanGoghConnection.ReadConnQue(2000L);

            TimeNow = TimeNow.now();

            if(MsgPtr != NULL)
            {
                switch(MsgPtr->isA())
                {
                case MSG_DBCHANGE:
                    {
                        changeCnt++;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << TimeNow << " Porter has received a " << ((CtiDBChangeMsg*)MsgPtr)->getCategory() << " DBCHANGE message from Dispatch." << endl;
                        }

                        const CtiDBChangeMsg *dbchg = (CtiDBChangeMsg *)MsgPtr;

                        if( dbchg->getDatabase() == ChangePAODb )
                        {
                            CtiDeviceSPtr dev;

                            if( dev = DeviceManager.getEqual(dbchg->getId()) )
                            {
                                dev->setDirty(true);
                            }

                            //  the UDP thread needs to reload each item individually, so we can't ever discard any
                            Cti::Porter::UDPInterfaceQueue.push_back(MsgPtr->replicateMessage());
                        }

                        if(pChg)
                        {
                            delete pChg;
                            pChg = NULL;

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << TimeNow << "  Pending DBCHANGE message has been preempted." << endl;
                            }
                        }

                        if(changeCnt < 2)           // If we have more than one change we must reload all items.
                        {
                            pChg = (CtiDBChangeMsg *)MsgPtr->replicateMessage();
                        }

                        RefreshTime = TimeNow;        // We will update two minutes after db changes cease!
                        SetEvent(hPorterEvents[P_REFRESH_EVENT]);

                        break;
                    }
                case MSG_COMMAND:
                    {
                        CtiCommandMsg* Cmd = (CtiCommandMsg*)MsgPtr;

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

                delete MsgPtr;
            }

            if(TimeNow > RefreshTime || ( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_REFRESH_EVENT], 0L) ))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " DispatchMsgHandlerThd beginning a database reload";
                    if(pChg)
                    {
                        dout << " - DBChange message." << endl;
                    }
                    else
                    {
                        dout << " - No DBChange message." << endl;
                    }
                }
                ResetEvent(hPorterEvents[P_REFRESH_EVENT]);
                RefreshPorterRTDB((void*)pChg);                 // Deletes the message!
                RefreshTime = nextScheduledTimeAlignedOnRate( TimeNow, PorterRefreshRate );

                changeCnt = 0;
                pChg = NULL;

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
            if((LastThreadMonitorTime.now().minute() - LastThreadMonitorTime.minute()) >= 1)
            {
                if(pointID!=0)
                {
                    CtiThreadMonitor::State next;
                    LastThreadMonitorTime = LastThreadMonitorTime.now();
                    if((next = ThreadMonitor.getState()) != previous || checkCount++ >=3)
                    {
                        previous = next;
                        checkCount = 0;

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
