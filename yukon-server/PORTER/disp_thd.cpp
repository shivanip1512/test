/*-----------------------------------------------------------------------------*
*
* File:   disp_thd
*
* Date:   2/2/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/disp_thd.cpp-arc  $
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2004/08/10 16:53:18 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....


#include <windows.h>
#include <iomanip>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\cstring.h>
#include <rw\rwtime.h>
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

#include "portglob.h"
#include "ctibase.h"
#include "dllbase.h"

#include "logger.h"
#include "guard.h"
#include "utility.h"

CtiConnection  VanGoghConnection;

extern INT RefreshPorterRTDB(void *ptr = NULL);
extern void applyPortQueueReport(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr);
extern bool processInputFunction(CHAR Char);
extern void KickPIL();

void DispatchMsgHandlerThread(VOID *Arg)
{
    extern CtiPortManager PortManager;
    extern CtiPILServer PIL;

    BOOL           bServerClosing = FALSE;

    RWTime         TimeNow;
    RWTime         RefreshTime          = nextScheduledTimeAlignedOnRate( TimeNow, PorterRefreshRate );
    CtiMessage     *MsgPtr              = NULL;
    UINT           changeCnt = 0;
    CtiDBChangeMsg *pChg = NULL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " DispatchMsgHandlerThd started as TID " << rwThreadId() << endl;
    }

    VanGoghConnection.doConnect(VANGOGHNEXUS, VanGoghMachine);
    VanGoghConnection.setName("Dispatch");
    VanGoghConnection.WriteConnQue(CTIDBG_new CtiRegistrationMsg(PORTER_REGISTRATION_NAME, rwThreadId(), FALSE));

    RWTime nowTime;
    RWTime nextTime = nowTime + 30;
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
                nextTime = nowTime.seconds() - (nowTime.seconds() % 300) + 300;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Porter's OM Count = " << omc << endl;
                }

                PortManager.apply( applyPortQueueReport, (void*)1 );
            }

            if( PIL.isBroken() && !bServerClosing )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " PIL interface is indicating a failure.  Restarting the interface." << endl;
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
                                SetEvent(hPorterEvents[P_QUIT_EVENT]);
                                PorterQuit = TRUE;
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Porter received a shutdown message from somewhere" << endl;
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
                                            dout << RWTime() << "  PorterDebugLevel set to 0x" << (char*)CtiNumStr(PorterDebugLevel).hex().zpad(8) << endl;

                                        }
                                        break;
                                    }
                                case 0x02:
                                    {
                                        DebugLevel = Cmd->getOpArgList().at(2);
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << "  DebugLevel set to 0x" << (char*)CtiNumStr(DebugLevel).hex().zpad(8) << endl;

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

            if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 250L) )
            {
                bServerClosing = TRUE;
            }
            else if(TimeNow > RefreshTime || ( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_REFRESH_EVENT], 0L) ))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " DispatchMsgHandlerThd beginning a database reload";
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
                    dout << RWTime() << " DispatchMsgHandlerThd done reloading" << endl;
                }
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    } /* End of for */

    VanGoghConnection.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
    VanGoghConnection.ShutdownConnection();
}
