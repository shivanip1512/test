/*-----------------------------------------------------------------------------*
*
* File:   portdnpudp
*
* Date:   2004-feb-16
*
* Author: Matt Fisher
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/04/05 16:56:04 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <iostream>
#include <map>
#include <queue>

using namespace std;

#include "cparms.h"
#include "dlldefs.h"
#include "dllyukon.h"
#include "dllbase.h"
#include "mgr_device.h"
#include "dev_dnp.h"
#include "guard.h"
#include "logger.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "mgr_route.h"
#include "numstr.h"
#include "portdecl.h"
#include "portglob.h"
#include "port_base.h"
#include "queue.h"
#include "numstr.h"
#include "sema.h"


#ifdef VSLICK_TAG_WORKAROUND
typedef CtiDeviceSingle *CtiDeviceSingleSPtr;
#else
typedef shared_ptr<CtiDeviceSingle> CtiDeviceSingleSPtr;
#endif


extern CtiPortManager PortManager;
extern CtiDeviceManager DeviceManager;

extern INT ReturnResultMessage(INT CommResult, INMESS *InMessage, OUTMESS *&OutMessage);

CtiQueue< CtiOutMessage, less<CtiOutMessage> > DNPUDPOutMessageQueue;

static const int DNPUDP_DEBUG_OUTPUT = 1;

// Some Global Manager types to allow us some RTDB stuff.

static CtiMutex     access_mux;
static CtiSemaphore work_flag(0, 16000);  //  so, presumably, we could never have more than 16000 consecutive operations waiting at once...

struct packet
{
    char *data;
    int   len;
    int   used;
    int   status;
};

typedef queue< CtiOutMessage * > om_queue;

struct om_tracker
{
    om_queue       work_queue;
    CtiOutMessage *active_om;
    CtiXfer        xfer;
    u_long         time;
};

typedef map< u_long, CtiDeviceSingleSPtr > dnp_ip_map;
typedef map< long, om_tracker >            dnp_om_queuemap;
typedef queue< pair< long, packet > >      packet_queue;


static dnp_ip_map      ip_mapping;
static dnp_om_queuemap active_devices;
static packet_queue    packets;

static SOCKET udp_socket;

void DNPUDPInboundThread ( void *Dummy );
void DNPUDPOutboundThread( void *Dummy );
void DNPUDPExecuteThread ( void *Dummy );




void DNPUDPInboundThread( void *Dummy )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " DNPUDPInboundThread started as TID  " << CurrentTID() << endl;
    }

    sockaddr_in local,
                from;

    const int DNPHeaderLength = 10;

    int   recv_len;
    char *recv_buf;

    CtiDeviceSingleSPtr dev_single;


    udp_socket = socket(AF_INET, SOCK_DGRAM, 0); // UDP socket

    if( udp_socket == INVALID_SOCKET )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - DNPUDPInboundThread failed - socket() failed with error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return;
    }

    local.sin_family      = AF_INET;
    local.sin_addr.s_addr = INADDR_ANY;
    local.sin_port        = htons(gConfigParms.getValueAsInt("PORTER_DNPUDP_PORT", 5500));

    // bind() associates a local address and port combination with the
    //   socket just created.
    if( bind(udp_socket, (sockaddr *)&local, sizeof(local)) == SOCKET_ERROR )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - DNPUDPInboundThread failed - bind() failed with error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return;
    }

    unsigned long nonblock = TRUE;
    ioctlsocket(udp_socket, FIONBIO, &nonblock);

    _beginthread(DNPUDPExecuteThread, 0, NULL);
    _beginthread(DNPUDPOutboundThread, 0, NULL);

    recv_buf = CTIDBG_new char[16000];  //  should be big enough for any incoming packet

    while( !PorterQuit )
    {
        recv_len = recvfrom(udp_socket, recv_buf, 16000, 0, (sockaddr *)&from, 0);

        if(recv_len == SOCKET_ERROR)
        {
            if( WSAGetLastError() == WSAEWOULDBLOCK)
            {
                Sleep(100);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - DNPUDPInboundThread had error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                Sleep(500);
            }

            continue;
        }

        if( DNPUDP_DEBUG_OUTPUT )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " DNP packet received via UDP connection." << endl;
        }

        {
            CtiLockGuard< CtiMutex > guard(access_mux, 15000);

            if( guard.isAcquired() )
            {
                dnp_ip_map::iterator      ip_itr;
                dnp_om_queuemap::iterator active_itr;

                short dnp_slave_address = 0;

                if( recv_len >= DNPHeaderLength )
                {
                    //  check the framing bytes
                    if( recv_buf[0] == 0x05 && recv_buf[1] == 0x64 )
                    {
                        dnp_slave_address = (recv_buf[7] << 8) | recv_buf[6];

                        ip_itr = ip_mapping.find(from.sin_addr.S_un.S_addr);

                        dev_single.reset();

                        //  if we were able to find this device
                        if( ip_itr != ip_mapping.end() )
                        {
                            if( dev_single = ip_itr->second )
                            {
                                active_itr = active_devices.find(dev_single->getID());

                                //  make sure the device is working on an outmessage (the assumption being that it's waiting for input)
                                if( active_itr != active_devices.end() && (*active_itr).second.active_om )
                                {
                                    //  ACH:  add a CRC check prior to this - the UDP stuff doesn't have one, so this check may not always be reliable
                                    if( dev_single->getAddress() == dnp_slave_address )
                                    {
                                        packet in_packet;

                                        in_packet.data   = CTIDBG_new char[recv_len];
                                        in_packet.len    = recv_len;
                                        in_packet.used   = 0;
                                        in_packet.status = 0;

                                        memcpy(in_packet.data, recv_buf, recv_len);

                                        packets.push(make_pair(dev_single->getID(), in_packet));

                                        //  let the outbound thread know there's new work to be done
                                        if( !work_flag.release() )
                                        {
                                            //  this happens if the semaphore can't increase its release count - this may cause
                                            //    a delay in the processing loop later on
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        }
                                    }
                                    else if(DeviceManager.RemoteGetPortRemoteTypeEqual(gConfigParms.getValueAsInt("PORTER_DNPUDP_DB_PORTID", 0), dnp_slave_address, TYPE_DNPRTU))
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " **** Checkpoint - incoming address (" << dnp_slave_address << ") trumps old address (" << dev_single->getAddress() << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        }

                                        while( !(*active_itr).second.work_queue.empty() )
                                        {
                                            INMESS im;

                                            //  return a "No Config Data" error
                                            ReturnResultMessage(NoConfigData, &im, (*active_itr).second.work_queue.front());

                                            (*active_itr).second.work_queue.pop();
                                        }

                                        //  ACH:  should we allow multiple addresses on a single IP?
                                        dev_single.reset();

                                        ip_mapping.erase(ip_itr);
                                    }
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint - unexpected inbound message from device \"" << dev_single->getName() << "\", discarding **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint - null entry in DNPUDPInboundThread::ip_mapping, discarding **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }

                                ip_mapping.erase(ip_itr);
                            }
                        }

                        if( !dev_single )
                        {
                            boost::shared_ptr<CtiDeviceBase> dev_base;

                            //  we didn't have this device in the mapping table, so look it up
                            if( dev_base = DeviceManager.RemoteGetPortRemoteTypeEqual(gConfigParms.getValueAsInt("PORTER_DNPUDP_DB_PORTID", 0), dnp_slave_address, TYPE_DNPRTU) )
                            {
                                dev_single = boost::static_pointer_cast<CtiDeviceSingle>(dev_base);

                                if( DNPUDP_DEBUG_OUTPUT )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << " Inserting device \"" << dev_base->getName() << "\" in list " << endl;
                                }

                                //  we found it, insert the IP/device pair
                                ip_mapping.insert(make_pair(from.sin_addr.S_un.S_addr, dev_single));
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - can't find DNP slave (" << dnp_slave_address << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - bad header (" << CtiNumStr(recv_buf[0]).xhex().zpad(2) << " "
                                                                              << CtiNumStr(recv_buf[1]).xhex().zpad(2) << ") on inbound DNP message, cannot assign **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - read " << recv_len << " bytes, not enough for a full header - discarding **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - access_mux not aquired **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    delete [] recv_buf;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " DNPUDPInboundThread shutdown." << endl;
    }

    return;
}


VOID DNPUDPOutboundThread(void *Dummy)
{
    extern CtiConnection VanGoghConnection;

    dnp_om_queuemap::iterator dev_om_itr;

    CtiDeviceSingleSPtr dev_single;
    long device_id;
    int status;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " DNPOutboundUDPThread started as TID  " << CurrentTID() << endl;
    }

    while(!PorterQuit)
    {
        try
        {
            if( !work_flag.acquire(2000) && DNPUDP_DEBUG_OUTPUT )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - no work for the last 2 seconds **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            {
                CtiLockGuard< CtiMutex > guard(access_mux, 15000);

                if(guard.isAcquired())
                {
                    if( !packets.empty() )
                    {
                        device_id            = packets.front().first;
                        packet  &work_packet = packets.front().second;

                        dev_om_itr = active_devices.find(device_id);

                        //  make sure he's expecting an inbound packet
                        if( dev_om_itr != active_devices.end() && (*dev_om_itr).second.active_om )
                        {
                            int byte_count = work_packet.len - work_packet.used;
                            int status;

                            om_tracker &tracker = (*dev_om_itr).second;
                            CtiXfer    &xfer    = tracker.xfer;

                            if( byte_count > xfer.getInCountExpected() )
                            {
                                byte_count = xfer.getInCountExpected();
                            }

                            memcpy(xfer.getInBuffer(), work_packet.data + work_packet.used, byte_count);

                            xfer.setInCountActual(byte_count);

                            work_packet.used += byte_count;

                            status = dev_single->decode(xfer, work_packet.status);

                            if( dev_single->isTransactionComplete() )
                            {
                                INMESS im;

                                work_packet.used = work_packet.len;

                                //  send real pointdata messages here
                                dev_single->sendDispatchResults(VanGoghConnection);

                                //  send text results to Commander here via return string
                                dev_single->sendCommResult(&im);

                                im.EventCode = status;

                                ReturnResultMessage(status, &im, tracker.active_om);

                                //  set the active om to 0

                                //  this should never happen (see below for only other place active_om is assigned), but it makes me a little nervous
                                if( tracker.active_om != tracker.work_queue.front() )
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }
                                }

                                delete tracker.active_om;

                                tracker.work_queue.pop();

                                tracker.active_om = 0;
                            }
                            else
                            {
                                dnp_ip_map::iterator ip_itr = ip_mapping.begin();
                                sockaddr_in to;

                                while( ip_itr != ip_mapping.end() && (*ip_itr).second != dev_single )
                                {
                                    ip_itr++;
                                }

                                if( ip_itr != ip_mapping.end() )
                                {
                                    to.sin_addr.S_un.S_addr = (*ip_itr).first;
                                    to.sin_family           = AF_INET;
                                    to.sin_port             = gConfigParms.getValueAsInt("PORTER_DNPUDP_PORT", 5500);

                                    dev_single->generate(xfer);

                                    tracker.time = RWTime().seconds();

                                    sendto(udp_socket, (char *)xfer.getOutBuffer(), xfer.getOutCount(), 0, (sockaddr *)&to, sizeof(to));
                                }
                                else
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint - can't find device address **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }
                                }
                            }

                            if( work_packet.used == work_packet.len )
                            {
                                delete [] work_packet.data;
                                packets.pop();
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - device \"" << dev_single->getName() << "\" is not active or cannot be found - discarding packet **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            delete [] work_packet.data;
                            packets.pop();
                        }
                    }

                    RWTime Now;

                    for( dev_om_itr = active_devices.begin(); dev_om_itr != active_devices.end(); dev_om_itr++ )
                    {
                        device_id               = dev_om_itr->first;
                        om_tracker &dev_om_list = dev_om_itr->second;

                        //  are you idle?  do you have work waiting?
                        if( !dev_om_list.active_om && !dev_om_list.work_queue.empty() )
                        {
                            dev_om_list.active_om = dev_om_list.work_queue.front();

                            dev_single->recvCommRequest(dev_om_list.active_om);
                        }
                        //  do we need to send you a timeout packet?
                        else if( (dev_om_list.time + gConfigParms.getValueAsInt("PORTER_DNPUDP_TIMEOUT", 3)) < Now.seconds() )
                        {
                            packet timeout_packet;

                            timeout_packet.data   = 0;
                            timeout_packet.len    = 0;
                            timeout_packet.used   = 0;
                            timeout_packet.status = READTIMEOUT;

                            //  this is a fake packet that just says we timed out
                            packets.push(make_pair(device_id, timeout_packet));

                            //  make sure we loop back around and catch this right away
                            work_flag.release();
                        }
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - access_mux not aquired **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " DNPOutboundUDPThread shutdown." << endl;
    }

    return;
}



VOID DNPUDPExecuteThread(void *Dummy)
{
    OUTMESS *om;
    shared_ptr<CtiDeviceSingle> dev_single;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " DNPUDPExecuteThread started as TID  " << CurrentTID() << endl;
    }

    while(!PorterQuit)
    {
        try
        {
            om = DNPUDPOutMessageQueue.getQueue( 2500 );

            if( om )
            {
                CtiLockGuard< CtiMutex > guard(access_mux, 15000);

                if(guard.isAcquired())
                {
                    dnp_ip_map::iterator ip_itr;

                    for( ip_itr = ip_mapping.begin(); ip_itr != ip_mapping.end(); ip_itr++ )
                    {
                        if( ip_itr->second->getID() == om->TargetID )
                        {
                            break;
                        }
                    }

                    if( ip_itr != ip_mapping.end() )
                    {
                        dnp_om_queuemap::iterator active_itr = active_devices.find(om->TargetID);

                        if( active_itr != active_devices.end() )
                        {
                            active_itr->second.work_queue.push(om);

                            om = 0;
                        }
                        else
                        {
                            om_tracker new_tracker;

                            new_tracker.work_queue.push(om);
                            new_tracker.active_om = 0;

                            active_devices.insert(make_pair(om->TargetID, new_tracker));

                            om = 0;
                        }

                        //  let the outbound thread know there's new work to be done
                        if( !work_flag.release() )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    else
                    {
                        INMESS im;

                        //  return a "No Config Data" error
                        ReturnResultMessage(NoConfigData, &im, om);
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - access_mux not aquired **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            if(om)
            {
                delete om;
                om = 0;
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " DNPUDPExecuteThread shutdown." << endl;
    }

    return;
}

