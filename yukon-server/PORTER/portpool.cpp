/*-----------------------------------------------------------------------------*
*
* File:   portpool
*
* Date:   3/4/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.21 $
* DATE         :  $Date: 2008/10/22 21:16:43 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "connection.h"
#include "cparms.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "port_base.h"
#include "port_pool_out.h"
#include "portdecl.h"
#include "portglob.h"

using namespace std;

extern CtiDeviceManager DeviceManager;
extern void applyPortQueueReport(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr);

INT AllocateOutMessagesToChildPorts(CtiPortSPtr &ParentPort);

static INT sgPoolDebugLevel = 0;

VOID PortPoolDialoutThread(void *pid)
{
    extern CtiConnection  VanGoghConnection;
    extern CtiPortManager PortManager;

    INT            i, status = NORMAL;
    LONG           portid = (LONG)pid;      // NASTY CAST HERE!!!
    CtiPortSPtr    ParentPort( PortManager.PortGetEqual( portid ) );      // Bump the reference count on the shared object!

    OUTMESS        *OutMessage = 0;
    BYTE           ReadPriority;
    ULONG          MSecs, QueEntries, ReadLength;

    CtiTime         lastQueueReportTime;
    CtiDeviceSPtr  Device;

    /* make it clear who is the boss */
    CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_HIGHEST);

    if(!ParentPort)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " PortPoolDialoutThread TID: " << CurrentTID () << " for port: " << setw(4) << ParentPort->getPortID() << " / " << ParentPort->getName() << " UNABLE TO START!" << endl;
        }

        return;
    }
    else if(ParentPort->getType() != PortTypePoolDialout )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " PortPoolDialoutThread TID: " << CurrentTID () << " for port: " << setw(4) << ParentPort->getPortID() << " / " << ParentPort->getName() << " NOT POOLABLE." << endl;
        }

        return;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PortPoolDialoutThread TID: " << CurrentTID () << " for port: " << setw(4) << ParentPort->getPortID() << " / " << ParentPort->getName() << endl;
    }

    sgPoolDebugLevel = gConfigParms.getValueAsULong("PORTPOOL_DEBUGLEVEL", 0, 16);

    while(!PorterQuit)
    {
        OutMessage = 0;

        if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
        {
            PorterQuit = TRUE;
            continue;
        }

        if((status = ParentPort->readQueue( &ReadLength, (PPVOID) &OutMessage, DCWW_WAIT, &ReadPriority, &QueEntries)) != NORMAL )
        {
            /*
             *  This is a Read from the CTI queueing structures which will originate from
             *  some other requestor.  This is where this thread blocks and waits if there are
             *  no entries on the queue.  One may think of the "above" call as "cleanup" for the
             *  previous ReadQueue's operation. Note that the ReadQueue call allocs space for the
             *  OutMessage pointer, and fills it from it's queue entries!
             */

            if(status == ERROR_QUE_EMPTY)
            {
                if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 500L) )
                {
                    PorterQuit = TRUE;
                }
            }
            else if( status != ERROR_QUE_UNABLE_TO_ACCESS)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Error Reading ParentPort Queue" << endl;
                }
            }
            continue;
        }

        CtiTime starttime;

        if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POOLQUEUE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << starttime << " " << ParentPort->getName() << " has just read performed a readQueue().  OM Priority " << OutMessage->Priority << endl;
        }

        if(PorterDebugLevel & PORTER_DEBUG_PORTQUEREAD)
        {
            CtiDeviceSPtr tempDev = DeviceManager.getDeviceByID(OutMessage->TargetID ? OutMessage->TargetID : OutMessage->DeviceID);

            if(tempDev)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port " << ParentPort->getName() << " read an outmessage for " << tempDev->getName();
                dout << " at priority " << OutMessage->Priority << " retries = " << OutMessage->Retry << endl;
                dout << CtiTime() << " Port has " << QueEntries << " pending OUTMESS requests " << endl;
            }
        }


        if(QueEntries > 5000 && CtiTime() > lastQueueReportTime)  // Ok, we may have an issue here....
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ParentPort " << ParentPort->getName() << " has " << QueEntries << " pending OUTMESS requests " << endl;
            }
            lastQueueReportTime = CtiTime() + 300;
        }

        if(PorterDebugLevel & PORTER_DEBUG_VERBOSE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << ParentPort->getName() << " Portpool read: OutMessage->DeviceID / Remote / Port / Priority = " << OutMessage->DeviceID << " / " << OutMessage->Remote << " / " << OutMessage->Port << " / " << OutMessage->Priority << endl;
        }

        if(OutMessage->DeviceID == 0 && OutMessage->Remote != 0 && OutMessage->Port != 0)
        {
            if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " looking for new deviceID..." << endl;
            }

            Device = DeviceManager.RemoteGetPortRemoteEqual(OutMessage->Port, OutMessage->Remote);

            if( Device )
            {
                OutMessage->DeviceID = Device->getID();

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " assigned new deviceID = " << Device->getID() << endl;
                }
            }
            else
            {
                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " did not assign new deviceID" << endl;
                }

                SendError(OutMessage, status);
                continue;
            }
        }
        else
        {
            /* get the device record for this id */
            Device = DeviceManager.getDeviceByID(OutMessage->DeviceID);

            if(!Device)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " ParentPort " << ParentPort->getName() << " just received a message for device id " << OutMessage->DeviceID << endl << \
                    " Porter does not seem to know about him and is throwing away the message!" << endl;
                }

                try
                {
                    SendError(OutMessage, status);
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                continue;
            }


            if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POOLQUEUE)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << Device->getName() << " has been submitted on the pool port.  Priority " << OutMessage->Priority << endl;
            }
        }

        CtiPortManager::ptr_type childport = ((CtiPortPoolDialout*)ParentPort.get())->getAvailableChildPort(Device);
        CtiTime stoptime;

        if(stoptime.seconds() - starttime.seconds() > 2)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << ParentPort->getName() << " Way too slow!" << endl;
            }
        }

        if(childport)
        {
            childport->writeQueue(OutMessage->Request.GrpMsgID, sizeof(*OutMessage), (char *) OutMessage, OutMessage->Priority);
        }
        else if(OutMessage != NULL)
        {
            // Plop it back onto the main pool queue.
            ParentPort->writeQueue(OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority );
        }

        OutMessage = 0; // It is not ours anymore to touch!

        // Now we need to do a pool-port-queue-sweep looking for OutMessages which can be allocated onto any child port.
        starttime = starttime.now();
        status = AllocateOutMessagesToChildPorts(ParentPort);
        stoptime = stoptime.now();

        if(stoptime.seconds() - starttime.seconds() > 2)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << ParentPort->getName() << " Way too slow!" << endl;
            }
        }

        if(status == CtiPortPoolDialout::PPSC_AllChildrenBusy)
        {
            if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POOLQUEUE)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port " << ParentPort->getName() << " **** ALL CHILDREN BUSY **** " << endl;
            }

            if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_QUEUEDUMPS)
            {
                PortManager.apply( applyPortQueueReport, NULL );
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << endl << CtiTime() << " There are " << OutMessageCount() << " OutMessages held by Port Control." << endl << endl;
                }
            }

            if( ParentPort->waitForPost(hPorterEvents[P_QUIT_EVENT], 15000) && (sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POSTSTOPARENT))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << ParentPort->getName() << " awakened by a postParent() " << endl;
                }

            }
        }
        else if(status == CtiPortPoolDialout::PPSC_ChildReady)
        {
            if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POOLQUEUE)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port " << ParentPort->getName() << " **** Child Ports available **** " << endl;
            }
        }
        else if(status == CtiPortPoolDialout::PPSC_ParentQueueEmpty)
        {
            if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POOLQUEUE)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port " << ParentPort->getName() << " **** QUEUE EMPTY **** " << endl;
            }
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Shutdown PortPoolDialoutThread TID: " << CurrentTID () << " for port: " << setw(4) << ParentPort->getPortID() << " / " << ParentPort->getName() << endl;
    }


    return;
}


INT AllocateOutMessagesToChildPorts(CtiPortSPtr &ParentPort)
{
    INT status = NORMAL;

    try
    {
        status = ((CtiPortPoolDialout*)ParentPort.get())->allocateQueueEntsToChildPort();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}
