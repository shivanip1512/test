

/*-----------------------------------------------------------------------------*
*
* File:   portpool
*
* Date:   3/4/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/03/06 18:03:09 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "connection.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "port_base.h"
#include "portdecl.h"
#include "portglob.h"

extern CtiDeviceManager DeviceManager;
static ULONG   gQueSlot = 0;

VOID PortPoolDialoutThread(void *pid)
{
    extern CtiConnection  VanGoghConnection;
    extern CtiPortManager PortManager;

    INT            i, status = NORMAL;
    LONG           portid = (LONG)pid;      // NASTY CAST HERE!!!
    CtiPortSPtr    Port( PortManager.PortGetEqual( portid ) );      // Bump the reference count on the shared object!

    OUTMESS        *OutMessage;
    REQUESTDATA    ReadResult;
    BYTE           ReadPriority;
    ULONG          MSecs, QueEntries, ReadLength;

    RWTime         lastQueueReportTime;
    CtiDeviceBase  *Device = 0;


    if(!Port)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " PortPoolDialoutThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << " UNABLE TO START!" << endl;
        }

        return;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PortPoolDialoutThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
    }

    while(!PorterQuit)
    {
        if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
        {
            PorterQuit = TRUE;
            continue;
        }

        /*
         *  This is a Read from the CTI queueing structures which will originate from
         *  some other requestor.  This is where this thread blocks and waits if there are
         *  no entries on the queue.  One may think of the "above" call as "cleanup" for the
         *  previous ReadQueue's operation. Note that the ReadQueue call mallocs space for the
         *  OutMessage pointer, and fills it from it's queue entries!
         */
        if((status = ReadQueue (Port->getPortQueueHandle(), &ReadResult, &ReadLength, (PPVOID) &OutMessage, gQueSlot, DCWW_WAIT, &ReadPriority, &QueEntries)) != NORMAL )
        {
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
                    dout << RWTime() << " Error Reading Port Queue" << endl;
                }
            }
            continue;
        }
        else if(PorterDebugLevel & PORTER_DEBUG_PORTQUEREAD)
        {
            CtiDeviceBase *tempDev = DeviceManager.getEqual(OutMessage->TargetID);

            if(tempDev)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << Port->getName() << " read an outmessage for " << tempDev->getName();
                dout << " at priority " << OutMessage->Priority << " retries = " << OutMessage->Retry << endl;
                dout << RWTime() << " Port has " << QueEntries << " pending OUTMESS requests " << endl;
            }
        }

        if(QueEntries > 5000 && RWTime() > lastQueueReportTime)  // Ok, we may have an issue here....
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << Port->getName() << " has " << QueEntries << " pending OUTMESS requests " << endl;
            }
            lastQueueReportTime = RWTime() + 300;
        }

        statisticsNewRequest(OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID);

        if(PorterDebugLevel & PORTER_DEBUG_VERBOSE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Portfield connection read OutMessage->DeviceID = " << OutMessage->DeviceID << endl;
            dout << RWTime() << "                           OutMessage->Remote   = " << OutMessage->Remote << endl;
            dout << RWTime() << "                           OutMessage->Port     = " << OutMessage->Port << endl;
        }

        if(OutMessage->DeviceID == 0 && OutMessage->Remote != 0 && OutMessage->Port != 0)
        {
            if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " looking for new deviceID..." << endl;
            }

            Device = DeviceManager.RemoteGetPortRemoteEqual(OutMessage->Port, OutMessage->Remote);

            if( Device != NULL )
            {
                OutMessage->DeviceID = Device->getID();

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " assigned new deviceID = " << Device->getID() << endl;
                }
            }
            else
            {
                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " did not assign new deviceID" << endl;
                }

                SendError(OutMessage, status);
                continue;
            }
        }
        else
        {
            /* get the device record for this id */
            Device = DeviceManager.RemoteGetEqual(OutMessage->DeviceID);

            if(Device == NULL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Port " << Port->getPortID() << " just received a message for device id " << OutMessage->DeviceID << endl << \
                    " Porter does not seem to know about him and is throwing away the message!" << endl;
                }

                SendError(OutMessage, status);
                continue;
            }
        }


        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** A.C.H. A.C.H.  Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }


        if(OutMessage != NULL)
        {
            delete OutMessage; /* free up the OutMessage, it made a successful run! */
            OutMessage = NULL;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Shutdown PortPoolDialoutThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
    }


    return;
}

