/*-----------------------------------------------------------------------------*
*
* File:   portdnpudp
*
* Date:   2004-feb-16
*
* Author: Matt Fisher
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/12/06 23:18:04 $
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
#include "thread_monitor.h"


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

typedef map< u_long, unsigned short >      ipport_map;     // Map keyed on dnp pao id gives pair< ip, port >
typedef map< u_long, CtiDeviceSingleSPtr > dnp_ip_map;
typedef map< long, om_tracker >            dnp_om_queuemap;
typedef queue< pair< long, packet > >      packet_queue;

static dnp_ip_map      ip_mapping;
static ipport_map      port_mapping;
static dnp_om_queuemap active_devices;
static packet_queue    packets;

static SOCKET udp_socket;
static SOCKET udp_out_socket;

void DNPUDPInboundThread ( void *Dummy );
void DNPUDPOutboundThread( void *Dummy );
void DNPUDPExecuteThread ( void *Dummy );

static CtiDeviceSingleSPtr DNPDeviceByID( long device_id );
static u_long DNPIPByPao( long device_id );
static u_short DNPPortByIP( u_long ip );




void DNPUDPInboundThread( void *Dummy )
{
    UINT sanity = 0;
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


    udp_out_socket = socket(AF_INET, SOCK_DGRAM, 0);    // UDP socket for outbound.
    udp_socket = socket(AF_INET, SOCK_DGRAM, 0);        // UDP socket

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
    // ioctlsocket(udp_socket, FIONBIO, &nonblock);

    _beginthread(DNPUDPExecuteThread, 0, NULL);
    _beginthread(DNPUDPOutboundThread, 0, NULL);

    recv_buf = CTIDBG_new char[16000];  //  should be big enough for any incoming packet

    while( !PorterQuit )
    {
        //Thread Monitor Begins here**************************************************
        if(!(++sanity % SANITY_RATE))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " DNP Inbound thread active. TID:  " << rwThreadId() << endl;
            }

            CtiThreadRegData *data = new CtiThreadRegData( GetCurrentThreadId(), "DNP Inbound Thread", CtiThreadRegData::None, 300 );
            ThreadMonitor.tickle( data );
        }
        //End Thread Monitor Section

        int fromlen = sizeof(from);
        recv_len = recvfrom(udp_socket, recv_buf, 16000, 0, (sockaddr *)&from, &fromlen);

        if(recv_len == SOCKET_ERROR)
        {
            if( WSAGetLastError() == WSAEWOULDBLOCK)
            {
                Sleep(100);
            }
            else
            {
                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
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
            for(int xx = 0; xx < recv_len; xx++)
            {
                dout << " " << RWCString(CtiNumStr((BYTE)recv_buf[xx]).hex().zpad(2));
            }

            dout << endl;
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

                        ip_itr = ip_mapping.find(ntohl(from.sin_addr.S_un.S_addr));
                        dev_single.reset();

                        if(gConfigParms.getValueAsULong("DNPUDP_DEBUGLEVEL",0) & 0x00000001)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") PORT: " << ntohs(from.sin_port) << endl;
                        }

                        //  if we were able to find this device
                        if( ip_itr != ip_mapping.end() )
                        {
                            // Update the port information no matter what!
                            ipport_map::iterator pt_itr = port_mapping.find( ip_itr->first );

                            if(pt_itr != port_mapping.end())
                            {
                                (*pt_itr).second = ntohs(from.sin_port);
                            }


                            if( ip_itr->second )
                            {
                                dev_single = ip_itr->second;
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
                                            ReturnResultMessage(NoConfigData, &im, (*active_itr).second.work_queue.front());  // This method WILL delete the om passed into it.
                                            (*active_itr).second.work_queue.pop();
                                        }

                                        //  ACH:  should we allow multiple addresses on a single IP?
                                        dev_single.reset();
                                        port_mapping.erase(ip_itr->first);
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
                                    dout << RWTime() << " Inserting device \"" << dev_base->getName() << "\" in list " << endl;
                                }

                                //  we found it, insert the IP/device pair
                                ip_mapping.insert(make_pair(ntohl(from.sin_addr.S_un.S_addr), dev_single));
                                port_mapping.insert(make_pair(ntohl(from.sin_addr.S_un.S_addr), ntohs(from.sin_port)));
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
    UINT sanity=0;

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
            //Thread Monitor Begins here**************************************************
            if(!(++sanity % SANITY_RATE_MED_SLEEPERS))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " DNP Outbound thread active. TID:  " << rwThreadId() << endl;
                }

                CtiThreadRegData *data = new CtiThreadRegData( GetCurrentThreadId(), "DNP Outbound Thread", CtiThreadRegData::None, 300 );
                ThreadMonitor.tickle( data );
            }
            //End Thread Monitor Section

            if( !work_flag.acquire(2000) && (getDebugLevel() & DEBUGLEVEL_LUDICROUS) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - no work for the last 2 seconds **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            {
                CtiLockGuard< CtiMutex > guard(access_mux, 15000);

                if(guard.isAcquired())
                {
                    /*
                     *  This conditional processes any inbound data from the UDP port.  Yes, inbound packets.
                     *  A packet may need to be falsified to start a transaction?
                     */
                    if( !packets.empty() )
                    {
                        device_id            = packets.front().first;
                        packet  &work_packet = packets.front().second;

                        dev_om_itr = active_devices.find(device_id);
                        dev_single = DNPDeviceByID(device_id);

                        //  make sure he's expecting an inbound packet
                        if( dev_single && dev_om_itr != active_devices.end() && (*dev_om_itr).second.active_om )
                        {
                            int byte_count = work_packet.len - work_packet.used;
                            int status;

                            om_tracker &tracker = (*dev_om_itr).second;
                            CtiXfer    &xfer    = tracker.xfer;
                            status              = work_packet.status;

                            if(work_packet.data != 0 && work_packet.len > 0)        // this block calls 'decode" if we have already begun the transaction!// decode only if there is data to decode
                            {

                                if( byte_count > xfer.getInCountExpected() )
                                {
                                    byte_count = xfer.getInCountExpected();
                                }

                                // Mode the UDP data into the xfer structure shich the protocol knows and loves.
                                memcpy(xfer.getInBuffer(), work_packet.data + work_packet.used, byte_count);
                                xfer.setInCountActual(byte_count);
                                work_packet.used += byte_count;
                                // Ask the protocol object to decode it.
                                status = dev_single->decode(xfer, work_packet.status);
                            }

                            if( work_packet.status == READTIMEOUT || dev_single->isTransactionComplete() )
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " " << dev_single->getName() << " Transaction is " << (status == NORMAL ? "COMPLETE " : "TIMEDOUT ") << endl;
                                }

                                INMESS im;

                                work_packet.used = work_packet.len;

                                //  send real pointdata messages here
                                dev_single->sendDispatchResults(VanGoghConnection);

                                //  send text results to Commander here via return string
                                dev_single->sendCommResult(&im);

                                im.EventCode = status;

                                ReturnResultMessage(status, &im, tracker.active_om);        // This method may delete the om!

                                //  this should never happen (see below for only other place active_om is assigned), but it makes me a little nervous
                                if( tracker.active_om && tracker.active_om != tracker.work_queue.front() )
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        dout << " " << tracker.active_om << endl;
                                        dout << " " << tracker.work_queue.front() << endl;
                                    }
                                }

                                //  set the active_om to 0
                                delete tracker.active_om;
                                tracker.work_queue.pop();
                                tracker.active_om = 0;
                                tracker.time = RWTime(YUKONEOT).seconds();        // Keep this from twitching every 3 seconds.
                            }
                            else
                            {
                                if(gConfigParms.getValueAsULong("DNPUDP_DEBUGLEVEL",0) & 0x00000001)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Transaction is INCOMPLETE " << dev_single->getName() << endl;
                                }

                                // Must recover the IP address.
                                sockaddr_in to;
                                u_long ip = DNPIPByPao(dev_single->getID());
                                u_short port = DNPPortByIP(ip);

                                if(!port) port = gConfigParms.getValueAsInt("PORTER_DNPUDP_PORT", 5500);

                                if( ip )
                                {
                                    to.sin_addr.S_un.S_addr = htonl(ip);
                                    to.sin_family           = AF_INET;
                                    to.sin_port             = htons(port);

                                    dev_single->generate(xfer);

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " DNP packet being sent via UDP connection." << endl;
                                        for(int xx = 0; xx < xfer.getOutCount(); xx++)
                                        {
                                            dout << " " << RWCString(CtiNumStr(xfer.getOutBuffer()[xx]).hex().zpad(2));
                                        }

                                        dout << endl;
                                    }

                                    if(SOCKET_ERROR == sendto(udp_socket, (char *)xfer.getOutBuffer(), xfer.getOutCount(), 0, (sockaddr *)&to, sizeof(to)))
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " **** SENDTO: Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        }
                                    }
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
/*
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") PACKET POPPED HERE" << endl;
                                }
*/
                                delete [] work_packet.data;
                                packets.pop();
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            if(dev_single)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - device \"" << dev_single->getName() << "\" is not active or cannot be found - discarding packet **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - device id \"" << device_id << "\" is not active or cannot be found - discarding packet **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                        dev_single = DNPDeviceByID(device_id);

                        if(dev_single)
                        {
                            //  are you idle?  do you have work waiting?
                            if( !dev_om_list.active_om && !dev_om_list.work_queue.empty() )
                            {
                                dev_om_list.time = RWTime().seconds();                      // This command is ready to go.
                                dev_om_list.active_om = dev_om_list.work_queue.front();
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " " << dev_single->getName() << ": IDLE device needs work.  Generating a trigger packet: " << endl;
                                }
                                dev_single->recvCommRequest(dev_om_list.active_om);

                                packet trigger_packet;
                                trigger_packet.data   = 0;
                                trigger_packet.len    = 0;
                                trigger_packet.used   = 0;
                                trigger_packet.status = NORMAL;

                                packets.push(make_pair(device_id, trigger_packet));  //  this is a fake packet that just says GO!!!
                            }
                            //  do we need to send you a timeout packet?
                            else if( (dev_om_list.time + gConfigParms.getValueAsInt("PORTER_DNPUDP_TIMEOUT", 30)) < Now.seconds() )
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

                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " " << dev_single->getName() << ": TIMEOUT PACKET generated." << endl;
                                }
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** ACH ACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    UINT sanity = 0;
    shared_ptr<CtiDeviceSingle> dev_single;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " DNPUDPExecuteThread started as TID  " << CurrentTID() << endl;
    }

    while(!PorterQuit)
    {
        try
        {
            //Thread Monitor Begins here**************************************************
            if(!(++sanity % SANITY_RATE_MED_SLEEPERS))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " DNP Execute thread active. TID:  " << rwThreadId() << endl;
                }

                CtiThreadRegData *data = new CtiThreadRegData( GetCurrentThreadId(), "DNP Execute Thread", CtiThreadRegData::None, 300 );
                ThreadMonitor.tickle( data );
            }
            //End Thread Monitor Section

            om = DNPUDPOutMessageQueue.getQueue( 2500 );

            if( om )
            {
                CtiLockGuard< CtiMutex > guard(access_mux, 15000);

                if(guard.isAcquired())
                {
                    dnp_ip_map::iterator ip_itr;
                    CtiDeviceSingleSPtr dev_single = DNPDeviceByID(om->TargetID);

                    if( dev_single )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Queuing work and making device active.  " << dev_single->getName() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        dnp_om_queuemap::iterator active_itr = active_devices.find(om->TargetID);

                        if( active_itr != active_devices.end() )        // This device is already active.  Add the new om to the exisitng queue.
                        {
                            active_itr->second.work_queue.push(om);
                            om = 0;
                        }
                        else                                            // This device is not yet active.  Create queues and add the new om.
                        {
                            om_tracker new_tracker;

                            new_tracker.work_queue.push(om);
                            new_tracker.active_om = 0;

                            dnp_om_queuemap::_Pairib inspair = active_devices.insert(make_pair(om->TargetID, new_tracker));

                            if(inspair.second != true)
                            {
                                delete om;
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

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
                        ReturnResultMessage(NoConfigData, &im, om);     // This method will delete the om!
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Device id " << om->TargetID << " not found on DNP connection" << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
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


CtiDeviceSingleSPtr DNPDeviceByID( long device_id )
{
    CtiDeviceSingleSPtr dev_single;

    // Find dev_single in the list based upon the deviceid in the message?!
    dnp_ip_map::iterator ip_itr;
    for(ip_itr = ip_mapping.begin(); ip_itr != ip_mapping.end(); ip_itr++)
    {
        if((*ip_itr).second->getID() == device_id)
        {
            dev_single = (*ip_itr).second;
            break;
        }
    }

    if(ip_itr == ip_mapping.end())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Device is not found! ID = " << device_id << " Check memory leak here." << endl;
        }
    }

    return dev_single;
}

u_long DNPIPByPao( long device_id )
{
    u_long ip = 0;

    // Find dev_single in the list based upon the deviceid in the message?!
    dnp_ip_map::iterator ip_itr;
    for(ip_itr = ip_mapping.begin(); ip_itr != ip_mapping.end(); ip_itr++)
    {
        if((*ip_itr).second->getID() == device_id)
        {
            ip = (*ip_itr).first;
            break;
        }
    }

    if(ip_itr == ip_mapping.end())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Device IP is not found! ID = " << device_id << " Check memory leak here." << endl;
        }
    }

    return ip;
}

u_short DNPPortByIP( u_long ip )
{
    u_short port = 0;
    ipport_map::iterator pt_itr = port_mapping.find( ip );

    if(pt_itr != port_mapping.end())
    {
        port = pt_itr->second;

        if(gConfigParms.getValueAsULong("DNPUDP_DEBUGLEVEL",0) & 0x00000001)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") PORT is " << port << endl;
        }
    }

    return port;
}
