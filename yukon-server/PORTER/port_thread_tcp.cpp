#include "yukon.h"

#include "port_thread_tcp.h"

#include <boost/shared_ptr.hpp>
#include <boost/scoped_array.hpp>
#include "boostutil.h"

#include "c_port_interface.h"

#include "prot_gpuff.h"

#include "portglob.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "dev_dnp.h"
#include "dev_gridadvisor.h"
#include "cparms.h"
#include "numstr.h"
#include "portdecl.h"  //  for statisticsNewCompletion

#include "portfield.h"

// Some Global Manager types to allow us some RTDB stuff.

using namespace std;

extern CtiConnection VanGoghConnection;
extern CtiDeviceManager DeviceManager;

namespace Cti    {
namespace Porter {


using Protocols::GpuffProtocol;

/* Threads that handle each port for communications */
VOID PortTcpThread(void *pid)
{
#if 0
    long portid = (long)pid;

    CtiPortSPtr Port(PortManager.PortGetEqual(portid));

    if( Port && Port->getType() == PortTypeUdp )
    {
        Ports::UdpPortSPtr udp_port = boost::static_pointer_cast<Ports::UdpPort>(Port);

        UdpPortHandler udp(udp_port);

        udp.run();
    }

    INT            status;

    CtiTime        nowTime, nextExpireTime;
    ULONG          i;
    INMESS         InMessage;
    OUTMESS        *OutMessage = 0;
    ULONG          QueEntries;



    bool           profiling = (portid == gConfigParms.getValueAsULong("PORTER_PORT_PROFILING"));
    LONG           expirationRate = gConfigParms.getValueAsULong("QUEUE_EXPIRE_TIMES_PER_DAY", 0);
    DWORD          ticks;

    CtiDeviceSPtr  Device;
    CtiDeviceSPtr  LastExclusionDevice;

    /* make it clear who is the boss */
    CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_HIGHEST);

    // Let the threads get up and running....
    WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 2500L);

    getNextExpirationTime(expirationRate, nextExpireTime);

    if( !Port )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Port == 0 in PortThread() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " PortThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
        }

        string thread_name = "Port " + CtiNumStr(Port->getPortID()).zpad(4);

        SetThreadName(-1, thread_name.c_str());

        /* and wait for something to come in */
        for(;!PorterQuit;)
        {
            OutMessage = 0;
            nowTime = nowTime.now();

            if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
            {
                PorterQuit = TRUE;
                continue;
            }

            if( nowTime > nextExpireTime )
            {
                int entries = purgeExpiredQueueEntries(Port);
                getNextExpirationTime(expirationRate, nextExpireTime);

                if( entries > 0 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Port "  << Port->getName() << " purged " << CtiNumStr(entries) << " expired OM's " << endl;
                }
            }

            if( Port->isInhibited() )
            {
                Sleep(5000L);
                continue;
            }

            if( !Port->isValid() && !PortManager.PortGetEqual(portid) )
            {
                //  we've been deleted - exit the thread
                break;
            }

            if( CONTINUE_LOOP == (status = ResetChannel(Port, Device)) )
            {
                //  we're busted - don't make anyone else wait on our priorities
                DeviceManager.setDevicePrioritiesForPort(portid, CtiDeviceManager::device_priorities_t());

                Sleep(50);
                status = 0;
                continue;
            }

            Device = DeviceManager.chooseExclusionDevice( Port->getPortID() );

            if(Device)
            {
                Device->getOutMessage(OutMessage);
            }

            if(profiling)
            {
                ticks = GetTickCount();
            }

            if( !OutMessage && (status = GetWork( Port, OutMessage, QueEntries )) != NORMAL )
            {
                if( profiling )
                {
                    ticks = GetTickCount() - ticks;

                    if( ticks > 1000 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Profiling - getWork() took " << ticks << " ms **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                Sleep(250);

                continue;
            }
            else
            {
                if( profiling )
                {
                    ticks = GetTickCount() - ticks;

                    if( ticks > 1000 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Profiling - getWork() took " << ticks << " ms **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                if(PorterDebugLevel & PORTER_DEBUG_PORTQUEREAD)
                {
                    CtiDeviceSPtr tempDev = DeviceManager.getDeviceByID(OutMessage->TargetID ? OutMessage->TargetID : OutMessage->DeviceID);

                    if(tempDev)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Port " << Port->getName() << " read an outmessage for " << tempDev->getName();
                        dout << " at priority " << OutMessage->Priority << " retries = " << OutMessage->Retry << endl;
                        if(strlen(OutMessage->Request.CommandStr) > 0) dout << CtiTime() << " Command : " << OutMessage->Request.CommandStr << endl;
                        if(QueEntries > 50) dout << CtiTime() << " Port has " << QueEntries << " pending OUTMESS requests " << endl;
                    }
                }
            }


            /*
             *  Must verify that the outmessage has not expired.  The OM will be consumed and error returned to any
             *   requesting client.
             */
            if( CheckIfOutMessageIsExpired(OutMessage) != NORMAL )
            {
                continue;
            }

            if(Port->getConnectedDevice() != OutMessage->DeviceID)
            {
                if(Device && Device->hasExclusions())
                    DeviceManager.removeInfiniteExclusion(Device);
            }

            /*
             *  This is the call which establishes the OutMessage's DeviceID as the Device we are operating upon.
             *  Upon successful return, the Device pointer is set to nonNull.
             */
            if( CONTINUE_LOOP == IdentifyDeviceFromOutMessage(Port, OutMessage, Device) )
            {
                continue;
            }

            if(PorterDebugLevel & PORTER_DEBUG_VERBOSE)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << Port->getName() << " PortThread read: OutMessage->DeviceID / Remote / Port / Priority = " << OutMessage->DeviceID << " / " << OutMessage->Remote << " / " << OutMessage->Port << " / " << OutMessage->Priority << endl;
            }

            // Copy a good portion of the OutMessage to the to-be-formed InMessage
            OutEchoToIN(OutMessage, &InMessage);

            if((status = CheckInhibitedState(Port, &InMessage, OutMessage, Device)) != NORMAL)
            {
                SendError(OutMessage, status);
                continue;
            }

            /* Make sure everything is A-OK with this device */
            if((status = ValidateDevice(Port, Device, OutMessage)) != NORMAL)
            {
                RequeueReportError(status, OutMessage);
                continue;
            }

            //  See if there is a reason to proceed...  Note that this is where OMs can be queued onto devices
            if((status = DevicePreprocessing(Port, OutMessage, Device)) != NORMAL)   /* do any preprocessing according to type */
            {
                RequeueReportError(status, OutMessage);
                continue;
            }

            /* Check if this port is dial up and initiate connection. */
            if((status = EstablishConnection(Port, &InMessage, OutMessage, Device)) != NORMAL)
            {
                if(status != RETRY_SUBMITTED)
                {
                    Port->reset(TraceFlag);
                }

                RequeueReportError(status, OutMessage);
                continue;
            }

            ticks = GetTickCount();

            Port->setPortCommunicating();
            try
            {
                /* Execute based on wrap protocol.  Sends OutMessage and fills in InMessage */
                i = CommunicateDevice(Port, &InMessage, OutMessage, Device);
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint port " << Port->getName() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            Port->addDeviceQueuedWork( Device->getID(), Device->queuedWorkCount() );

            ticks = GetTickCount() - ticks;
            if( profiling )
            {
                if( ticks > 1000 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Profiling - CommunicateDevice took " << ticks << " ms for \"" << Device->getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            Port->setPortCommunicating(false, ticks);

            //  if the device needs to schedule more work
            if( Device->hasPreloadWork() )
            {
                Port->setDevicePreload(Device->getID());

                processPreloads(Port);

                DeviceManager.addPortExclusion(Device->getID());
            }

            /* Non wrap protcol specific communications stuff */
            if(!i)      // No error yet.
            {
                i = NonWrapDecode(&InMessage, Device);
            }

            /*
             * Check if we need to do a retry on this command. Returns RETRY_SUBMITTED if the message has
             * been requeued, or the CommunicateDevice returned otherwise
             */
            LONG did = OutMessage->DeviceID;
            LONG tid = OutMessage->TargetID;
            bool rgtz = OutMessage->Retry > 0;

            if(CheckAndRetryMessage(i, Port, &InMessage, OutMessage, Device) == RETRY_SUBMITTED)
            {
                continue;  // It has been re-queued!
            }
            else   /* we are either successful or retried out */
            {
                if((status = DoProcessInMessage(i, Port, &InMessage, OutMessage, Device)) != NORMAL)
                {
                    RequeueReportError(status, OutMessage);
                    continue;
                }
            }

            if((status = ReturnResultMessage(i, &InMessage, OutMessage)) != NORMAL)
            {
                RequeueReportError(status, OutMessage);
                continue;
            }

            if(OutMessage != NULL)
            {
                delete OutMessage; /* free up the OutMessage, it made a successful run! */
                OutMessage = NULL;
            }
        }  /* and do it all again */
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Shutdown PortThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
    }
}

UdpPortHandler::UdpPortHandler( Ports::UdpPortSPtr &port ) :
    UnsolicitedHandler(boost::static_pointer_cast<CtiPort>(port)),
    _udp_port(port)
{
    _encodingFilter = EncodingFilterFactory::getEncodingFilter(port);
}


bool UdpPortHandler::setup()
{
    if( !_udp_port )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - _udp_port == 0 in UdpPortHandler::setup() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return false;
    }

    while( !bindSocket() )
    {
        Sleep(10000);

        if( PorterQuit )
        {
            return false;
        }
    }

    udp_load_info uli(_udp_port->getPortID(), _devices, _addresses, _types_serials);

    //  grab all of the stored UDP info from the DB
    DeviceManager.apply(applyGetUDPInfo, (void *)&uli);

    return true;
}


void UdpPortHandler::teardown()
{
}


bool UdpPortHandler::bindSocket( void )
{
    sockaddr_in local;

    _udp_socket = socket(AF_INET, SOCK_DGRAM, 0);    // UDP socket for outbound.

    if( _udp_socket == INVALID_SOCKET )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UDP::UdpPortHandler::bindSocket() - **** Checkpoint - socket() failed with error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return false;
    }

    local.sin_family      = AF_INET;
    local.sin_addr.s_addr = INADDR_ANY;
    local.sin_port        = htons(_udp_port->getIPPort());

    //  bind() associates a local address and port combination with the socket
    if( bind(_udp_socket, (sockaddr *)&local, sizeof(local)) == SOCKET_ERROR )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UDP::UdpPortHandler::bindSocket() - **** Checkpoint - bind() failed with error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return false;
    }

    unsigned long on = 1;

    ioctlsocket(_udp_socket, FIONBIO, &on);

    return true;
}


void UdpPortHandler::updateDeviceRecord(device_record &dr, const packet &p)
{
    if( dr.ip   != p.ip ||
        dr.port != p.port )
    {
        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UdpPortHandler::updateDeviceRecord() - IP or port mismatch for device \"" << dr.device->getName() << "\", updating (" << dr.ip << " != " << p.ip << " || " << dr.port << " != " << p.port << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        dr.ip   = p.ip;
        dr.port = p.port;

        dr.device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP,   ip_to_string(dr.ip));
        dr.device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port, dr.port);
    }

    //  sends IP and port as pointdata messages
    sendDeviceInfo(dr);
}


void UdpPortHandler::sendDeviceInfo( const device_record &dr )
{
    if( !dr.device )
    {
        return;
    }

    auto_ptr<CtiReturnMsg> vgMsg(CTIDBG_new CtiReturnMsg(0));
    CtiPointSPtr point;

    if( point = dr.device->getDevicePointOffsetTypeEqual(CtiDeviceSingle::PointOffset_Analog_IPAddress, AnalogPointType) )
    {
        vgMsg->PointData().push_back(CTIDBG_new CtiPointDataMsg(point->getID(), dr.ip, NormalQuality, AnalogPointType));
    }

    if( point = dr.device->getDevicePointOffsetTypeEqual(CtiDeviceSingle::PointOffset_Analog_Port, AnalogPointType) )
    {
        vgMsg->PointData().push_back(CTIDBG_new CtiPointDataMsg(point->getID(), dr.port, NormalQuality, AnalogPointType));
    }

    if( !vgMsg->PointData().empty() )
    {
        VanGoghConnection.WriteConnQue(vgMsg.release());
    }
}


void UdpPortHandler::sendOutbound( device_record &dr )
{
    //  if we don't have a device or anything to send, there's nothing to do here
    if( !dr.device || dr.work.xfer.getOutCount() == 0 )
    {
        return;
    }

    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UdpPortHandler::generateOutbound() - sending packet to "
                          << ip_to_string(dr.ip) << ":" << (dr.port) << " "
                          << __FILE__ << " (" << __LINE__ << ")" << endl;

        for( int xx = 0; xx < dr.work.xfer.getOutCount(); xx++ )
        {
            dout << " " << CtiNumStr(dr.work.xfer.getOutBuffer()[xx]).hex().zpad(2).toString();
        }

        dout << endl;
    }

    sockaddr_in to;

    to.sin_family           = AF_INET;
    to.sin_addr.S_un.S_addr = htonl(dr.ip);
    to.sin_port             = htons(dr.port);

    /* This is not tested until I get a Lantronix device. */
    vector<unsigned char> cipher;
    _encodingFilter->encode((unsigned char *)dr.work.xfer.getOutBuffer(),dr.work.xfer.getOutCount(),cipher);

    int err = sendto(_udp_socket, (const char*) &*cipher.begin(), cipher.size(), 0, (sockaddr *)&to, sizeof(to));

    if( SOCKET_ERROR == err )
    {
        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UdpPortHandler::sendOutbound() - **** SENDTO: Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        traceOutbound(dr, WSAGetLastError(), _traceList);
    }
    else
    {
        traceOutbound(dr, 0, _traceList);

        dr.work.last_outbound = CtiTime::now().seconds();
    }
}


string UdpPortHandler::describePort( void ) const
{
    ostringstream ostr;

    ostr << "UDP port " << setw(5) << _udp_port->getIPPort();

    return ostr.str();
}


void UdpPortHandler::collectInbounds()
{
    const unsigned max_len = 16000;  //  should be big enough for any incoming packet
    boost::scoped_array<unsigned char> recv_buf(CTIDBG_new unsigned char[max_len]);

    while( packet *p = recvPacket(recv_buf.get(), max_len) )
    {
        if( gConfigParms.isTrue("PORTER_UDP_PACKET_DUMP") )
        {
            string rawBytes;
            for( int xx = 0; xx < p->len; xx++ )
            {
                if( xx == 0 ) rawBytes += CtiNumStr(p->data[xx]).hex().zpad(2).toString();
                else rawBytes += " " + CtiNumStr(p->data[xx]).hex().zpad(2).toString();
            }

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Inbound Packet " << rawBytes << endl;
        }

        if( p->len >= Protocol::DNP::DatalinkPacket::HeaderLength
            && p->data[0] == 0x05
            && p->data[1] == 0x64 )
        {
            handleDnpPacket(p);
        }
        else if( p->len >= Protocols::GpuffProtocol::HeaderLength
                 && p->data[0] == 0xa5
                 && p->data[1] == 0x96 )
        {
            handleGpuffPacket(p);
        }
        else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UdpPortHandler::collectInbounds() - packet doesn't match any known protocol - discarding " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if( p )
        {
            //  this packet was unhandled, so we trace it
            traceInbound(p->ip, p->port, 0, p->data, p->len, _traceList);

            delete p->data;
            delete p;
        }
    }
}


UdpPortHandler::packet *UdpPortHandler::recvPacket(unsigned char * const recv_buf, unsigned max_len)
{
    sockaddr_in from;

    int fromlen = sizeof(from);
    int recv_len = recvfrom(_udp_socket, (char *)recv_buf, max_len, 0, (sockaddr *)&from, &fromlen);

    if( recv_len == SOCKET_ERROR )
    {
        if( WSAGetLastError() != WSAEWOULDBLOCK )
        {
            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Cti::Porter::UdpPortHandler::recvPacket() - **** Checkpoint - error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        return 0;
    }

    packet *p = CTIDBG_new packet;

    if( !p )
    {
        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UdpPortHandler::recvPacket() - **** Checkpoint - unable to allocate packet **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return 0;
    }

    p->ip   = ntohl(from.sin_addr.S_un.S_addr);
    p->port = ntohs(from.sin_port);

    p->len  = recv_len;
    p->used = 0;

    /* This is not tested until I get a Lantronix device. */
    vector<unsigned char> pText;
    _encodingFilter->decode(recv_buf,recv_len,pText);

    p->data = CTIDBG_new unsigned char[pText.size()];
    memcpy(p->data,(const char*) &*pText.begin(),pText.size());
    p->len = pText.size();

    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << CtiTime() << " Cti::Porter::UdpPortHandler::collectInbounds() - packet received from "
             << ip_to_string(p->ip) << ":" << p->port << " "
             << __FILE__ << " (" << __LINE__ << ")" << endl;

        for( int xx = 0; xx < pText.size(); xx++ )
        {
            dout << " " << CtiNumStr(p->data[xx]).hex().zpad(2).toString();
        }

        dout << endl;
    }

    return p;
}


void UdpPortHandler::applyGetUDPInfo(const long unusedid, CtiDeviceSPtr RemoteDevice, void *uli)
{
    udp_load_info *info = (udp_load_info *)uli;

    if( !info || info->portid != RemoteDevice->getPortID() )
    {
        return;
    }

    if( !RemoteDevice->hasDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP) ||
        !RemoteDevice->hasDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port) )
    {
        return;
    }

    device_record *dr = CTIDBG_new device_record;

    dr->device = boost::static_pointer_cast<CtiDeviceSingle>(RemoteDevice);

    dr->id     = dr->device->getID();
    dr->dirty  = dr->device->isDirty();

    dr->master = dr->device->getMasterAddress();
    dr->slave  = dr->device->getAddress();

    string ip_string;
    dr->device->getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP, ip_string);

    dr->ip   = string_to_ip(ip_string);
    dr->port = dr->device->getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port);

    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UdpPortHandler::applyGetUDPInfo - loading device "
             << dr->device->getName() << " "
             << ip_to_string(dr->ip) << ":" << dr->port << " "
             << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    dr->work.timeout = CtiTime(YUKONEOT).seconds();

    info->devices.insert(make_pair(dr->device->getID(), dr));

    /* is GPUFF device */
    if( dr->device->getType() == TYPE_FCI )
    {
        info->types_serials.insert(make_pair(make_pair(1, dr->slave), dr));
    }
    else if( dr->device->getType() == TYPE_NEUTRAL_MONITOR )
    {
        info->types_serials.insert(make_pair(make_pair(2, dr->slave), dr));
    }
    else
    {
        //  assume DNP
        info->addresses.insert(make_pair(make_pair(dr->master, dr->slave), dr));
    }
#endif
}


}
}

