#include "precompiled.h"

#include "port_thread_udp.h"

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
#include "dev_rds.h"
#include "cparms.h"
#include "numstr.h"

#include "portfield.h"

// Some Global Manager types to allow us some RTDB stuff.

using namespace std;

using Cti::Protocols::GpuffProtocol;
using Cti::Timing::MillisecondTimer;

extern CtiDeviceManager DeviceManager;

namespace Cti    {
namespace Porter {

/* Threads that handle each port for communications */
void PortUdpThread(void *pid)
{
    long portid = (long)pid;

    CtiPortSPtr Port(PortManager.PortGetEqual(portid));

    if( Port && Port->getType() == PortTypeUdp )
    {
        ostringstream thread_name;

        thread_name << "UDP PortID " << setw(4) << setfill('0') << Port->getPortID();

        SetThreadName(-1, thread_name.str().c_str());

        Ports::UdpPortSPtr udp_port = boost::static_pointer_cast<Ports::UdpPort>(Port);

        UdpPortHandler udp(udp_port, DeviceManager);

        udp.run();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Shutdown PortThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
        }
    }
}

UdpPortHandler::UdpPortHandler( Ports::UdpPortSPtr &udp_port, CtiDeviceManager &deviceManager ) :
    UnsolicitedHandler(boost::static_pointer_cast<CtiPort>(udp_port), deviceManager),
    _udp_port(udp_port),
    _udp_socket(INVALID_SOCKET),
    _connected_port(0)
{
    loadEncodingFilter();
}


UdpPortHandler::~UdpPortHandler()
{
    teardownSocket();
}


bool UdpPortHandler::setupPort()
{
    if( !_udp_port )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - _udp_port == 0 in UdpPortHandler::setup() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return false;
    }

    return bindSocket();
}


bool UdpPortHandler::bindSocket()
{
    while( !tryBindSocket() )
    {
        Sleep(10000);

        if( PorterQuit )
        {
            return false;
        }
    }

    return true;
}


bool UdpPortHandler::manageConnections( void )
{
    //  nothing to manage - the UDP port is connectionless

    return false;
}


void UdpPortHandler::loadDeviceProperties(const vector<const CtiDeviceSingle *> &devices)
{
    for each( const CtiDeviceSingle *dev in devices )
    {
        if( dev )
        {
            addDeviceProperties(*dev);
        }
    }
}


void UdpPortHandler::addDeviceProperties(const CtiDeviceSingle &device)
{
    const long device_id = device.getID();

    if( isGpuffDevice(device) )
    {
        _typeAndSerial_to_id.insert(type_serial_id_bimap::value_type(makeGpuffTypeSerialPair(device), device_id));
    }
    else if( isDnpDevice(device) )
    {
        _dnpAddress_to_id.insert(dnp_address_id_bimap::value_type(makeDnpAddressPair(device), device_id));
    }
    else if( isRdsDevice(device) )
    {
        loadStaticRdsIPAndPort(device);
    }

    if( !device.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP) ||
        !device.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port) )
    {
        return;
    }

    string ip_string;
    device.getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP, ip_string);

    _ip_addresses[device_id] = string_to_ip(ip_string);
    _ports       [device_id] = device.getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port);

    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UdpPortHandler::addDeviceProperties - loading device "
             << device.getName() << " "
             << ip_to_string(_ip_addresses[device_id]) << ":" << _ports[device_id] << " "
             << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


void UdpPortHandler::deleteDeviceProperties(const CtiDeviceSingle &device)
{
    const long device_id = device.getID();

    _ip_addresses.erase(device_id);
    _ports       .erase(device_id);

    _typeAndSerial_to_id.right.erase(device_id);
    _dnpAddress_to_id   .right.erase(device_id);
}


void UdpPortHandler::updateDeviceProperties(const CtiDeviceSingle &device)
{
    const long device_id = device.getID();

    if( isGpuffDevice(device) )
    {
        type_serial_id_bimap::right_iterator itr = _typeAndSerial_to_id.right.find(device_id);

        if( itr != _typeAndSerial_to_id.right.end() )
        {
            const gpuff_type_serial_pair old_typeAndSerial = itr->second;
            const gpuff_type_serial_pair new_typeAndSerial = makeGpuffTypeSerialPair(device);

            //  have the addresses changed?
            if( old_typeAndSerial != new_typeAndSerial )
            {
                _typeAndSerial_to_id.right.erase(itr);

                _typeAndSerial_to_id.insert(type_serial_id_bimap::value_type(new_typeAndSerial, device_id));
            }
        }
    }
    else if( isDnpDevice(device) )
    {
        dnp_address_id_bimap::right_iterator itr = _dnpAddress_to_id.right.find(device_id);

        if( itr != _dnpAddress_to_id.right.end() )
        {
            const dnp_address_pair old_address = itr->second;
            const dnp_address_pair new_address = makeDnpAddressPair(device);

            //  have the addresses changed?
            if( old_address != new_address )
            {
                _dnpAddress_to_id.right.erase(itr);

                _dnpAddress_to_id.insert(dnp_address_id_bimap::value_type(new_address, device_id));
            }
        }
    }
    else if( isRdsDevice(device) )
    {
        loadStaticRdsIPAndPort(device);

    }
}


UdpPortHandler::dnp_address_pair UdpPortHandler::makeDnpAddressPair(const CtiDeviceSingle &device)
{
    return dnp_address_pair(device.getMasterAddress(),
                            device.getAddress());
}


UdpPortHandler::gpuff_type_serial_pair UdpPortHandler::makeGpuffTypeSerialPair(const CtiDeviceSingle &device)
{
    return gpuff_type_serial_pair(device.getType(),
                                  device.getAddress());
}

void UdpPortHandler::loadStaticRdsIPAndPort(const CtiDeviceSingle &device)
{
    if( !device.hasStaticInfo(CtiTableStaticPaoInfo::Key_RDS_IP_Address) ||
        !device.hasStaticInfo(CtiTableStaticPaoInfo::Key_RDS_IP_Port) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Unable to load devices IP and Port " << device.getName() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
        return;
    }

    const long device_id = device.getID();
    string ip_string;
    device.getStaticInfo(CtiTableStaticPaoInfo::Key_RDS_IP_Address, ip_string);

    _ip_addresses[device_id] = string_to_ip(ip_string);
    _ports       [device_id] = device.getStaticInfo(CtiTableStaticPaoInfo::Key_RDS_IP_Port);

    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UdpPortHandler::addDeviceProperties - loading device "
             << device.getName() << " "
             << ip_to_string(_ip_addresses[device_id]) << ":" << _ports[device_id] << " "
             << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


void UdpPortHandler::updatePortProperties( void )
{
    if( _udp_port->isInhibited() )
    {
        teardownSocket();
    }
    else
    {
        //  if port changed, close and reopen socket on the new port
        if( _udp_port->getIPPort() != _connected_port )
        {
            teardownSocket();
        }

        if( _udp_socket == INVALID_SOCKET )
        {
            bindSocket();
        }

        loadEncodingFilter();
    }
}


void UdpPortHandler::teardownSocket()
{
    if( _udp_socket != INVALID_SOCKET )
    {
        closesocket(_udp_socket);

        _udp_socket = INVALID_SOCKET;
    }
}


bool UdpPortHandler::tryBindSocket( void )
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

        teardownSocket();

        return false;
    }

    unsigned long on = 1;

    ioctlsocket(_udp_socket, FIONBIO, &on);

    _connected_port = _udp_port->getIPPort();

    return true;
}


void UdpPortHandler::updateDeviceIpAndPort(device_record &dr, const packet &p)
{
    u_long  old_device_ip   = getDeviceIp  (dr.device->getID());
    u_short old_device_port = getDevicePort(dr.device->getID());

    if( old_device_ip   != p.ip ||
        old_device_port != p.port )
    {
        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UdpPortHandler::updateDeviceRecord() - IP or port mismatch for device \"" << dr.device->getName() << "\", updating (" << old_device_ip << " != " << p.ip << " || " << old_device_port << " != " << p.port << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        setDeviceIp  (dr.device->getID(), p.ip);
        setDevicePort(dr.device->getID(), p.port);

        dr.device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP,   ip_to_string(p.ip));
        dr.device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port, p.port);
    }

    //  sends IP and port as pointdata messages
    sendDeviceIpAndPort(dr.device, getDeviceIp(dr.device->getID()), getDevicePort(dr.device->getID()));
}


void UdpPortHandler::sendDeviceIpAndPort( const CtiDeviceSingleSPtr &device, u_long ip, u_short port )
{
    if( !device )
    {
        return;
    }

    auto_ptr<CtiReturnMsg> vgMsg(CTIDBG_new CtiReturnMsg(0));
    CtiPointSPtr point;

    if( point = device->getDevicePointOffsetTypeEqual(CtiDeviceSingle::PointOffset_Analog_IPAddress, AnalogPointType) )
    {
        vgMsg->PointData().push_back(CTIDBG_new CtiPointDataMsg(point->getID(), ip, NormalQuality, AnalogPointType));
    }

    if( point = device->getDevicePointOffsetTypeEqual(CtiDeviceSingle::PointOffset_Analog_Port, AnalogPointType) )
    {
        vgMsg->PointData().push_back(CTIDBG_new CtiPointDataMsg(point->getID(), port, NormalQuality, AnalogPointType));
    }

    if( !vgMsg->PointData().empty() )
    {
        extern CtiConnection VanGoghConnection;

        VanGoghConnection.WriteConnQue(vgMsg.release());
    }
}


int UdpPortHandler::sendOutbound( device_record &dr )
{
    u_long  device_ip   = getDeviceIp  (dr.device->getID());
    u_short device_port = getDevicePort(dr.device->getID());

    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UdpPortHandler::generateOutbound() - sending packet to "
                          << ip_to_string(device_ip) << ":" << device_port << " "
                          << __FILE__ << " (" << __LINE__ << ")" << endl;

        for( int xx = 0; xx < dr.xfer.getOutCount(); xx++ )
        {
            dout << " " << CtiNumStr(dr.xfer.getOutBuffer()[xx]).hex().zpad(2).toString();
        }

        dout << endl;
    }

    sockaddr_in to;

    to.sin_family           = AF_INET;
    to.sin_addr.S_un.S_addr = htonl(device_ip);
    to.sin_port             = htons(device_port);

    /* This is not tested until I get a Lantronix device. */
    vector<unsigned char> cipher;
    _encodingFilter->encode((unsigned char *)dr.xfer.getOutBuffer(),dr.xfer.getOutCount(),cipher);

    int err = sendto(_udp_socket, (const char*) &*cipher.begin(), cipher.size(), 0, (sockaddr *)&to, sizeof(to));

    if( SOCKET_ERROR == err )
    {
        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UdpPortHandler::sendOutbound() - **** SENDTO: Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return PORTWRITE;
    }

    dr.last_outbound = CtiTime::now();

    return NoError;
}


string UdpPortHandler::describePort( void ) const
{
    ostringstream ostr;

    ostr << "UDP port " << setw(5) << _udp_port->getIPPort() << " " << _udp_port->getName();

    return ostr.str();
}


bool UdpPortHandler::collectInbounds( const MillisecondTimer & timer, const unsigned long until)
{
    const unsigned max_len = 16000;  //  should be big enough for any incoming packet
    boost::scoped_array<unsigned char> recv_buf(CTIDBG_new unsigned char[max_len]);

    while( packet *p = recvPacket(recv_buf.get(), max_len) )
    {
        distributePacket(p);

        if( timer.elapsed() > until )
        {
            return true;
        }
    }

    return false;
}


UdpPortHandler::packet *UdpPortHandler::recvPacket(unsigned char * const recv_buf, unsigned max_len)
{
    if( _udp_socket == INVALID_SOCKET )
    {
        return 0;
    }

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

    if( (gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001) ||
        gConfigParms.isTrue("PORTER_UDP_PACKET_DUMP"))
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << CtiTime() << " Cti::Porter::UdpPortHandler::recvPacket() - packet received from "
             << ip_to_string(p->ip) << ":" << p->port << " "
             << __FILE__ << " (" << __LINE__ << ")" << endl;

        for( int xx = 0; xx < pText.size(); xx++ )
        {
            dout << " " << CtiNumStr(p->data[xx]).hex().zpad(2).toString();
        }

        dout << endl;
    }

    validatePacket(p);

    return p;
}


bool UdpPortHandler::validatePacket(packet *&p)
{
    if( Protocol::DNP::DatalinkLayer::isPacketValid(p->data, p->len) )
    {
        p->protocol = packet::ProtocolTypeDnp;
    }
    else if( GpuffProtocol::isPacketValid(p->data, p->len) )
    {
        p->protocol = packet::ProtocolTypeGpuff;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << __FUNCTION__ << "() - incoming packet from " << ip_to_string(p->ip) <<  ":" << p->port << " is invalid " << __FILE__ << "(" << __LINE__ << ")" << endl;
        }

        //  this packet was unhandled, so we trace it
        traceInbound(p->ip, p->port, 0, p->data, p->len);

        delete p->data;
        delete p;

        p = 0;
    }

    return p;
}


void UdpPortHandler::distributePacket(packet *p)
{
    if( !p )  return;

    switch( p->protocol )
    {
        case packet::ProtocolTypeDnp:
        {
            handleDnpPacket(p);
            break;
        }
        case packet::ProtocolTypeGpuff:
        {
            handleGpuffPacket(p);
            break;
        }
        default:
        {
            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Cti::Porter::UdpPortHandler::collectInbounds() - packet doesn't match any known protocol - discarding " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    if( p )
    {
        //  this packet was unhandled, so we trace it
        traceInbound(p->ip, p->port, 0, p->data, p->len);

        delete p->data;
        delete p;
    }
}


void UdpPortHandler::handleDnpPacket(packet *&p)
{
    unsigned short slave_address  = p->data[6] | (p->data[7] << 8);
    unsigned short master_address = p->data[4] | (p->data[5] << 8);

    device_record *dr = getDeviceRecordByDnpAddress(master_address, slave_address);

    //  do we have a device yet?
    if( dr && dr->device )
    {
        if( dr->device->isInhibited() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UdpPortHandler::handleDnpPacket - device \"" << dr->device->getName() << "\" is inhibited, discarding packet " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        else
        {
            updateDeviceIpAndPort(*dr, *p);

            addInboundWork(dr, p);

            p = 0;
        }
    }
    else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UdpPortHandler::handleDnpPacket - can't find DNP master/slave (" << master_address << "/" << slave_address << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

void UdpPortHandler::handleGpuffPacket(packet *&p)
{
    unsigned len, devt, ser;
    bool crc_included, ack_required;

    crc_included = p->data[2] & 0x80;
    ack_required = p->data[2] & 0x40;

    len  = ((p->data[2] & 0x03) << 8) | p->data[3];

    devt = (p->data[8] << 8) | p->data[9];

    ser  = (p->data[11] << 24) |
           (p->data[12] << 16) |
           (p->data[13] <<  8) |  p->data[14];

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - incoming packet from " << ip_to_string(p->ip) << ":" << p->port << ": " << endl;
    }

    GpuffProtocol::describeFrame(p->data, p->len, len, crc_included, ack_required, devt, ser);

    device_record *dr = getDeviceRecordByGpuffDeviceTypeSerial(devt, ser);

    try
    {
        if( dr && dr->device )
        {
            if( dr->device->isInhibited() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Cti::Porter::UdpPortHandler::handleDnpPacket - device \"" << dr->device->getName() << "\" is inhibited, discarding packet " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            else
            {
                updateDeviceIpAndPort(*dr, *p);

                traceInbound(p->ip, p->port, 0, p->data, p->len);

                addInboundWork(dr, p);
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no device found for GPUFF serial (" << ser << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    catch( ... )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


UdpPortHandler::device_record *UdpPortHandler::getDeviceRecordByDnpAddress( unsigned short master, unsigned short slave )
{
    dnp_address_id_bimap::left_const_iterator itr = _dnpAddress_to_id.left.find(make_pair(master, slave));

    if( itr == _dnpAddress_to_id.left.end() )
    {
        return 0;
    }

    return getDeviceRecordById(itr->second);
}


UdpPortHandler::device_record *UdpPortHandler::getDeviceRecordByGpuffDeviceTypeSerial( unsigned short device_type, unsigned long serial )
{
    int type = 0;

    switch( device_type )
    {
        case 3:
        case 1:  type = TYPE_FCI;              break;
        case 2:  type = TYPE_NEUTRAL_MONITOR;  break;
        default:
        {
            return 0;
        }
    }

    type_serial_id_bimap::left_const_iterator itr = _typeAndSerial_to_id.left.find(make_pair(type, serial));

    if( itr == _typeAndSerial_to_id.left.end() )
    {
        return 0;
    }

    return getDeviceRecordById(itr->second);
}


bool UdpPortHandler::isDeviceDisconnected( const long device_id ) const
{
    return _udp_socket == INVALID_SOCKET
            || getDeviceIp  (device_id) < 0
            || getDevicePort(device_id) < 0;
}


template<class Map>
typename Map::value_type::second_type find_or_return_numeric_limits_max(const Map &m, const typename Map::key_type &k)
{
    typename Map::const_iterator itr = m.find(k);

    if( itr != m.end() )
    {
        return itr->second;
    }

    return numeric_limits<typename Map::value_type::second_type>::max();
}


u_long UdpPortHandler::getDeviceIp( const long device_id ) const
{
    return find_or_return_numeric_limits_max(_ip_addresses, device_id);
}

u_short UdpPortHandler::getDevicePort( const long device_id ) const
{
    return find_or_return_numeric_limits_max(_ports, device_id);
}

void UdpPortHandler::setDeviceIp( const long device_id, const u_long ip )
{
    _ip_addresses[device_id] = ip;
}

void UdpPortHandler::setDevicePort( const long device_id, const u_short port )
{
    _ports[device_id] = port;
}


string UdpPortHandler::ip_to_string(u_long ip) const
{
    ostringstream ostr;

    ostr << ((ip >> 24) & 0xff) << ".";
    ostr << ((ip >> 16) & 0xff) << ".";
    ostr << ((ip >>  8) & 0xff) << ".";
    ostr << ((ip >>  0) & 0xff);

    return ostr.str();
}

u_long UdpPortHandler::string_to_ip(string ip_string)
{
    int pos = 0;
    u_long ip = 0;

    while( pos < ip_string.length() )
    {
        ip <<= 8;

        ip |= atoi(ip_string.c_str() + pos);

        pos = ip_string.find_first_of(".", pos + 1);

        if( pos != string::npos )
        {
            pos++;  //  move past the dot if we found one
        }
    }

    return ip;
}

void UdpPortHandler::loadEncodingFilter()
{
    _encodingFilter = EncodingFilterFactory::getEncodingFilter(_udp_port);
}

}
}

