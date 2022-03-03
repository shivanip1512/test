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
#include "precompiled.h"


#include "connection_client.h"
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

void PortPoolDialoutThread(void *pid)
{
    extern CtiClientConnection  VanGoghConnection;
    extern CtiPortManager PortManager;
    extern std::atomic_int PortManagerThreadCount;

    INT            i, status = ClientErrors::None;
    LONG           portid = (LONG)pid;      // NASTY CAST HERE!!!
    CtiPortSPtr    ParentPort( PortManager.getPortById( portid ) );      // Bump the reference count on the shared object!

    OUTMESS        *OutMessage = 0;
    BYTE           ReadPriority;
    ULONG          MSecs, QueEntries, ReadLength;

    CtiTime         lastQueueReportTime;
    CtiDeviceSPtr  Device;

    if(!ParentPort)
    {
        CTILOG_ERROR(dout, "PortPoolDialoutThread for port: "<< ParentPort->getPortID() <<" / "<< ParentPort->getName() <<" - Unable to start!");
        return;
    }
    else if(ParentPort->getType() != PortTypePoolDialout )
    {
        CTILOG_ERROR(dout, "PortPoolDialoutThread for port: "<< ParentPort->getPortID() <<" / "<< ParentPort->getName() <<" - Not poolable");
        return;
    }

    PortManagerThreadCount++;

    CTILOG_INFO(dout, "PortPoolDialoutThread for port: "<< ParentPort->getPortID() <<" / "<< ParentPort->getName() <<" - Started");

    ParentPort->startLog();

    sgPoolDebugLevel = gConfigParms.getValueAsULong("PORTPOOL_DEBUGLEVEL", 0, 16);

    while(!PorterQuit)
    {
        OutMessage = 0;

        if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
        {
            PorterQuit = TRUE;
            continue;
        }

        ParentPort->getPortLog()->poke();  //  called 2x/second if idle (see WaitForSingleObject below)

        if( status = ParentPort->readQueue( &ReadLength, (PPVOID) &OutMessage, DCWW_WAIT, &ReadPriority, &QueEntries) )
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
                CTILOG_ERROR(dout, "Could not read from parent port queue");
            }
            continue;
        }

        CtiTime starttime;

        if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POOLQUEUE)
        {
            CTILOG_DEBUG(dout, ParentPort->getName() <<" has just read performed a readQueue().  OM Priority "<< OutMessage->Priority);
        }

        if(PorterDebugLevel & PORTER_DEBUG_PORTQUEREAD)
        {
            CtiDeviceSPtr tempDev = DeviceManager.getDeviceByID(OutMessage->TargetID ? OutMessage->TargetID : OutMessage->DeviceID);

            if(tempDev)
            {
                CTILOG_DEBUG(dout, "Port "<< ParentPort->getName() <<" read an outmessage for "<< tempDev->getName() <<" at priority "<< OutMessage->Priority <<" retries = "<< OutMessage->Retry <<
                        endl <<"Port has "<< QueEntries <<" pending OUTMESS requests");
            }
        }


        if(QueEntries > 5000 && CtiTime() > lastQueueReportTime)  // Ok, we may have an issue here....
        {
            CTILOG_INFO(dout, "ParentPort "<< ParentPort->getName() <<" has "<< QueEntries <<" pending OUTMESS requests");

            lastQueueReportTime = CtiTime() + 300;
        }

        if(PorterDebugLevel & PORTER_DEBUG_VERBOSE)
        {
            CTILOG_DEBUG(dout, ParentPort->getName() <<" Portpool read: OutMessage->DeviceID / Remote / Port / Priority = "<< OutMessage->DeviceID <<" / "<< OutMessage->Remote <<" / "<< OutMessage->Port <<" / "<< OutMessage->Priority);
        }

        if(OutMessage->DeviceID == 0 && OutMessage->Remote != 0 && OutMessage->Port != 0)
        {
            if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
            {
                CTILOG_DEBUG(dout, "looking for new deviceID...");
            }

            Device = DeviceManager.RemoteGetPortRemoteEqual(OutMessage->Port, OutMessage->Remote);

            if( Device )
            {
                OutMessage->DeviceID = Device->getID();

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CTILOG_DEBUG(dout, "assigned new deviceID = "<< Device->getID());
                }
            }
            else
            {
                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CTILOG_DEBUG(dout, "did not assign new deviceID");
                }

                SendError(OutMessage, ClientErrors::IdNotFound);
                continue;
            }
        }
        else
        {
            /* get the device record for this id */
            Device = DeviceManager.getDeviceByID(OutMessage->DeviceID);

            if(!Device)
            {
                CTILOG_WARN(dout, "ParentPort "<< ParentPort->getName() <<" just received a message for device id "<< OutMessage->DeviceID <<
                        " - Porter does not seem to know about the device and is throwing away the message!");

                try
                {
                    SendError(OutMessage, ClientErrors::IdNotFound);
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                continue;
            }


            if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POOLQUEUE)
            {
                CTILOG_DEBUG(dout, Device->getName() <<" has been submitted on the pool port. Priority "<< OutMessage->Priority);
            }
        }

        CtiPortManager::ptr_type childport = ((CtiPortPoolDialout*)ParentPort.get())->getAvailableChildPort(Device);
        CtiTime stoptime;

        if(stoptime.seconds() - starttime.seconds() > 2)
        {
            CTILOG_WARN(dout, ParentPort->getName() <<" Way too slow!");
        }

        if(childport)
        {
            childport->writeQueue(OutMessage);
        }
        else if(OutMessage != NULL)
        {
            // Plop it back onto the main pool queue.
            ParentPort->writeQueue(OutMessage);
        }

        OutMessage = 0; // It is not ours anymore to touch!

        // Now we need to do a pool-port-queue-sweep looking for OutMessages which can be allocated onto any child port.
        starttime = starttime.now();
        status = AllocateOutMessagesToChildPorts(ParentPort);
        stoptime = stoptime.now();

        if(stoptime.seconds() - starttime.seconds() > 2)
        {
            CTILOG_WARN(dout, ParentPort->getName() <<" Way too slow!");
        }

        if(status == CtiPortPoolDialout::PPSC_AllChildrenBusy)
        {
            if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POOLQUEUE)
            {
                CTILOG_DEBUG(dout, "Port "<< ParentPort->getName() <<" - ALL CHILDREN BUSY");
            }

            if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_QUEUEDUMPS)
            {
                PortManager.apply( applyPortQueueReport, NULL );

                CTILOG_DEBUG(dout, "There are "<< OutMessageCount() <<" OutMessages held by Port Control.");
            }

            if( ParentPort->waitForPost(hPorterEvents[P_QUIT_EVENT], 15000) && (sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POSTSTOPARENT))
            {
                CTILOG_DEBUG(dout, ParentPort->getName() <<" awakened by a postParent()");
            }
        }
        else if(status == CtiPortPoolDialout::PPSC_ChildReady)
        {
            if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POOLQUEUE)
            {
                CTILOG_DEBUG(dout, "Port "<< ParentPort->getName() <<" - Child Ports available");
            }
        }
        else if(status == CtiPortPoolDialout::PPSC_ParentQueueEmpty)
        {
            if(sgPoolDebugLevel & PORTPOOL_DEBUGLEVL_POOLQUEUE)
            {
                CTILOG_DEBUG(dout, "Port "<< ParentPort->getName() <<" - QUEUE EMPTY");
            }
        }
    }

    CTILOG_INFO(dout, "Shutdown PortPoolDialoutThread for port: "<< ParentPort->getPortID() <<" / "<< ParentPort->getName());

    PortManagerThreadCount--;
}


INT AllocateOutMessagesToChildPorts(CtiPortSPtr &ParentPort)
{
    INT status = ClientErrors::None;

    try
    {
        status = ((CtiPortPoolDialout*)ParentPort.get())->allocateQueueEntsToChildPort();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return status;
}
