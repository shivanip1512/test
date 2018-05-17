#include "precompiled.h"

#include "port_thread_udp.h"

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
#include "socket_helper.h"
#include "std_helper.h"
#include "win_helper.h"
#include "portfield.h"

#include "connection_client.h"

#include "boostutil.h"

#include <mstcpip.h>

// Some Global Manager types to allow us some RTDB stuff.

using namespace std;

using Cti::Protocols::GpuffProtocol;
using Cti::Timing::MillisecondTimer;
using Cti::arrayToRange;
using Cti::Logging::Vector::Hex::operator<<;
using Cti::Logging::Range::Hex::operator<<;

extern CtiDeviceManager DeviceManager;

namespace Cti    {
namespace Porter {

/* Threads that handle each port for communications */
void PortUdpThread(void *pid)
{
    long portid = (long)pid;

    CtiPortSPtr Port(PortManager.getPortById(portid));

    if( Port && Port->getType() == PortTypeUdp )
    {
        ostringstream thread_name;

        thread_name << "UDP PortID " << setw(4) << setfill('0') << Port->getPortID();

        SetThreadName(-1, thread_name.str().c_str());

        Ports::UdpPortSPtr udp_port = boost::static_pointer_cast<Ports::UdpPort>(Port);

        UdpPortHandler udp(udp_port, DeviceManager);

        udp.run();

        CTILOG_INFO(dout, "Shutdown PortUdpThread for port "<< Port->getPortID() <<" / "<< Port->getName());
    }
}

UdpPortHandler::UdpPortHandler( Ports::UdpPortSPtr &udp_port, CtiDeviceManager &deviceManager ) :
    UnsolicitedHandler(boost::static_pointer_cast<CtiPort>(udp_port), deviceManager),
    _udp_port(udp_port),
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
        CTILOG_ERROR(dout, "_udp_port is Null");
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
        std::pair<type_serial_id_bimap::iterator, bool> insertResult =
            _typeAndSerial_to_id.insert(type_serial_id_bimap::value_type(makeGpuffTypeSerialPair(device), device_id));

        if (!insertResult.second)
        {
            CTILOG_ERROR(dout, "properties insert failed for device "<< device.getName() <<
                    ". Please update the type/serial values for this device to be unique.");
        }
    }
    else if( isDnpDeviceType(device.getType()) )
    {
        if ( ! _dnpLookup.addDevice(device) )
        {
            // The insert didn't occur! Complain.
            CTILOG_ERROR(dout, "properties insert failed for device "<< device.getName() <<
                    ". Please update the master/slave values for this device to be unique.");
        }
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

    _ip_addresses[device_id] = ip_string;
    _ports       [device_id] = device.getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port);

    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CTILOG_DEBUG(dout, "loading device "<< device.getName() <<" / "<< formatHostAndPort(_ip_addresses[device_id], _ports[device_id]));
    }
}


void UdpPortHandler::deleteDeviceProperties(const CtiDeviceSingle &device)
{
    const long device_id = device.getID();

    _ip_addresses.erase(device_id);
    _ports       .erase(device_id);

    _typeAndSerial_to_id.right.erase(device_id);
    _dnpLookup.deleteDevice(device_id);
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
        else
        {
            // Add the device.
            addDeviceProperties(device);
        }
    }
    else if( isDnpDeviceType(device.getType()) )
    {
        _dnpLookup.updateDevice(device);
    }
    else if( isRdsDevice(device) )
    {
        loadStaticRdsIPAndPort(device);
    }
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
        CTILOG_ERROR(dout, "Unable to load IP and Port for device "<< device.getName());
        return;
    }

    const long device_id = device.getID();
    string ip_string;
    device.getStaticInfo(CtiTableStaticPaoInfo::Key_RDS_IP_Address, ip_string);

    _ip_addresses[device_id] = ip_string;
    _ports       [device_id] = device.getStaticInfo(CtiTableStaticPaoInfo::Key_RDS_IP_Port);

    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CTILOG_DEBUG(dout, "loading device "<< device.getName() <<" / "<< formatHostAndPort(_ip_addresses[device_id], _ports[device_id]));
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

        if( _udp_sockets.areSocketsValid() )
        {
            bindSocket();
        }

        loadEncodingFilter();
    }
}


void UdpPortHandler::teardownSocket()
{
    _udp_sockets.shutdownAndClose();
}


bool UdpPortHandler::tryBindSocket( void )
{
    Cti::AddrInfo pAddrInfo = Cti::makeUdpServerSocketAddress(_udp_port->getIPPort());
    if( !pAddrInfo )
    {
        CTILOG_ERROR(dout, "failed to retrieve address info ("<< pAddrInfo.getError() <<")");
        return false;
    }

    try
    {
        // create sockets
        _udp_sockets.createSockets(pAddrInfo.get());

        //  associates a local address and port combination with the socket
        _udp_sockets.bind(pAddrInfo.get());

        // set to non blocking mode
        unsigned long on = 1;
        _udp_sockets.setIOMode(FIONBIO, &on);

        int rcvbuf;
        int rcvbuf_size = sizeof(int);

        if( rcvbuf = gConfigParms.getValueAsULong("PORTER_UDP_RCVBUF") )
        {
            _udp_sockets.setOption(SOL_SOCKET, SO_RCVBUF, (char *)&rcvbuf, rcvbuf_size);

            CTILOG_DEBUG(dout, "SO_RCVBUF is set to "<< rcvbuf);
        }
    }
    catch( const Cti::SocketException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e);

        return false;
    }

    _connected_port = _udp_port->getIPPort();

    return true;
}


void UdpPortHandler::updateDeviceIpAndPort(device_record &dr, const ip_packet &p)
{
    string  old_device_ip   = getDeviceIp  (dr.device->getID());
    u_short old_device_port = getDevicePort(dr.device->getID());

    if( old_device_ip   != p.ip ||
        old_device_port != p.port )
    {
        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CTILOG_DEBUG(dout, "IP or port mismatch for device \""<< dr.device->getName() <<"\", "
                    "updating ("<< old_device_ip <<" != "<< p.ip <<" || "<< old_device_port <<" != "<< p.port << ")");
        }

        setDeviceIp  (dr.device->getID(), p.ip);
        setDevicePort(dr.device->getID(), p.port);

        dr.device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP,   p.ip);
        dr.device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port, p.port);
    }

    //  sends IP and port as pointdata messages
    sendDeviceIpAndPort(dr.device, getDeviceIp(dr.device->getID()), getDevicePort(dr.device->getID()));
}


void UdpPortHandler::sendDeviceIpAndPort( const CtiDeviceSingleSPtr &device, string ip, u_short port )
{
    if( !device )
    {
        return;
    }

    unique_ptr<CtiReturnMsg> vgMsg(CTIDBG_new CtiReturnMsg(0));
    CtiPointSPtr point;

    if( point = device->getDevicePointOffsetTypeEqual(CtiDeviceSingle::PointOffset_Analog_IPAddress, AnalogPointType) )
    {
        ////////////// PATCH FOR DISPACHER //////////////
        // for now we remap string address into ipv4 format (compatibility mode)
        unsigned long ul_addr = 0;

        { // PATCH: convert string ip to unsigned long
            ADDRINFOA hints = {};

            // Set requirements
            hints.ai_family  = AF_UNSPEC; // IPv4 or IPv6
            hints.ai_flags  |= AI_NUMERICHOST;

            Cti::AddrInfo ai = Cti::AddrInfo(ip.c_str(), NULL, &hints);

            if(ai->ai_family == AF_INET)
            {
                SOCKADDR_IN* addr = (SOCKADDR_IN*)ai->ai_addr;
                ul_addr = ntohl(addr->sin_addr.S_un.S_addr);
            }
            else if(ai->ai_family == AF_INET6)
            {
                SOCKADDR_IN6* addr = (SOCKADDR_IN6*)ai->ai_addr;

                // in6_addr doesnt provide an array of unsigned long, so we declare a union to do it
                union IPV6_CONVERT_ADDR
                {
                    unsigned char uc_byte[16];
                    unsigned long ul_word[4];
                };

                IPV6_CONVERT_ADDR* u_tmp = (IPV6_CONVERT_ADDR*)addr->sin6_addr.u.Byte;

                if( u_tmp->ul_word[0] == 0x0 &&
                    u_tmp->ul_word[1] == 0x0 &&
                    u_tmp->ul_word[2] == 0xffff )
                {
                    // IPv4 mapped into IPv6
                    ul_addr = u_tmp->ul_word[3];
                }
                else
                {
                    // other IPv6 address are not supported
                    ul_addr = 0x0;
                }
            }
        }

        vgMsg->PointData().push_back(CTIDBG_new CtiPointDataMsg(point->getID(), ul_addr, NormalQuality, AnalogPointType));
    }

    if( point = device->getDevicePointOffsetTypeEqual(CtiDeviceSingle::PointOffset_Analog_Port, AnalogPointType) )
    {
        vgMsg->PointData().push_back(CTIDBG_new CtiPointDataMsg(point->getID(), port, NormalQuality, AnalogPointType));
    }

    if( !vgMsg->PointData().empty() )
    {
        extern CtiClientConnection VanGoghConnection;

        VanGoghConnection.WriteConnQue(vgMsg.release(), CALLSITE);
    }
}


// Return the socket and socket address for use with the given address
auto UdpPortHandler::getDestinationForAddress(const AddrInfo& address) -> OutboundDestination
{
    for( auto descriptor : _udp_sockets.getSocketDescriptors() )
    {
        auto socket   = descriptor.first;
        auto sockinfo = descriptor.second;

        if( sockinfo.family == address->ai_family )
        {
            auto addr = reinterpret_cast<const char*>(address->ai_addr);

            return { socket, { addr, addr + address->ai_addrlen } };
        }
        //  Is this a dual-stack socket?
        else if( sockinfo.family == AF_INET6 && address->ai_family == AF_INET )
        {
            //  All IPv6 ServerSockets are dual-stack, which means IPv4 addresses have to be translated to IPv6
            SOCKADDR_IN6 addr6;
            auto addr4 = reinterpret_cast<const SOCKADDR_IN*>(address->ai_addr);

            IN6_SET_ADDR_V4MAPPED(&addr6.sin6_addr, &addr4->sin_addr);
            addr6.sin6_family = AF_INET6;
            addr6.sin6_port = addr4->sin_port;
            addr6.sin6_flowinfo = 0;  //  Reserved/unused as of June 2017
            addr6.sin6_scope_id = 0;  //  Only used for link-/site-local addresses, see https://msdn.microsoft.com/en-us/library/windows/desktop/ms739166.aspx

            auto addr = reinterpret_cast<const char*>(&addr6);

            return { socket, { addr, addr + sizeof(addr6) } };
        }
    }

    throw YukonErrorException(ClientErrors::PortWrite, "failed to retrieve socket for family " + std::to_string(address->ai_family) + " for " + address.toString());
}


YukonError_t UdpPortHandler::sendOutbound( device_record &dr )
{
    string  device_ip   = getDeviceIp  (dr.device->getID());
    u_short device_port = getDevicePort(dr.device->getID());

    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CTILOG_DEBUG(dout, "sending packet to "<< formatHostAndPort(device_ip, device_port) <<
                endl << arrayToRange(dr.xfer.getOutBuffer(), dr.xfer.getOutCount()));
    }

    Cti::AddrInfo pAddrInfo = Cti::makeUdpClientSocketAddress(device_ip, device_port);
    if( !pAddrInfo )
    {
        CTILOG_ERROR(dout, "failed to retrieve address info ("<< pAddrInfo.getError() <<") for "<< formatHostAndPort(device_ip, device_port));

        return ClientErrors::PortWrite;
    }

    /* This is not tested until I get a Lantronix device. */
    vector<unsigned char> cipher;
    _encodingFilter->encode((unsigned char *)dr.xfer.getOutBuffer(),dr.xfer.getOutCount(),cipher);
    const auto cipher_data = reinterpret_cast<const char*>(cipher.data());

    try
    {
        const auto destination = getDestinationForAddress(pAddrInfo);
        const auto sockaddr_data = reinterpret_cast<const sockaddr*>(destination.sockaddr.data());

        auto send_result = sendto(destination.socket, cipher_data, cipher.size(), 0, sockaddr_data, destination.sockaddr.size());
        
        if( send_result == SOCKET_ERROR )
        {
            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                const DWORD error = WSAGetLastError();
                CTILOG_DEBUG(dout, "sendto() failed with error code "<< error <<" / "<< Cti::getSystemErrorMessage(error));
            }

            return ClientErrors::PortWrite;
        }

        dr.last_outbound = CtiTime::now();
    }
    catch( const YukonErrorException& ex )
    {
        CTILOG_EXCEPTION_ERROR(dout, ex);

        return ex.error_code;
    }

    return ClientErrors::None;
}


unsigned UdpPortHandler::getDeviceTimeout( const device_record &dr ) const
{
    unsigned portDelay    = _udp_port->getDelay(EXTRA_DELAY); // Additional timeout for the port.
    unsigned cparmTimeout = gConfigParms.getValueAsInt("PORTER_DNPUDP_TIMEOUT", 10);

    return std::max(portDelay, cparmTimeout);
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

    while( ip_packet *p = recvPacket(recv_buf.get(), max_len) )
    {
        distributePacket(p);

        if( timer.elapsed() > until )
        {
            return true;
        }
    }

    return false;
}


UdpPortHandler::ip_packet *UdpPortHandler::recvPacket(unsigned char * const recv_buf, unsigned max_len)
{
    if( !_udp_sockets.areSocketsValid() )
    {
        return 0;
    }

    Cti::SocketAddress from;

    int recv_len = SOCKET_ERROR;

    // check if we receive data from each family of sockets
    for( const auto s : _udp_sockets.getSockets() )
    {
        from.reset( Cti::SocketAddress::STORAGE_SIZE );

        recv_len = recvfrom(s, (char*)recv_buf, max_len, 0, &from._addr.sa, &from._addrlen);
        if( recv_len != SOCKET_ERROR )
        {
            break; // break from the loop
        }

        const int error = WSAGetLastError();
        if( error != WSAEWOULDBLOCK )
        {
            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CTILOG_DEBUG(dout, "recvfrom() failed with error code "<< error <<" / "<< Cti::getSystemErrorMessage(error));
            }
        }
    }

    if( recv_len == SOCKET_ERROR )
    {
        return 0;
    }

    ip_packet *p = new ip_packet;

    p->ip   = from.getIpAddress();
    p->port = from.getPort();
    p->used = 0;

    const bool dumpPacket = (gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001) ||
                             gConfigParms.isTrue("PORTER_UDP_PACKET_DUMP");

    /* This is not tested until I get a Lantronix device. */
    vector<unsigned char> pText;
    if( ! _encodingFilter->decode(recv_buf, recv_len, pText) )
    {
        {
            Cti::StreamBuffer output;
            output <<"unable to decode packet received from "<< p->describeAddress();

            if( dumpPacket )
            {
                if( recv_len )
                {
                    output << endl << arrayToRange(recv_buf, recv_len);
                }
                else
                {
                    output << endl <<"[empty]";
                }
            }

            CTILOG_ERROR(dout, output);
        }

        //  this packet was unhandled, so we trace it
        traceInbound(p->describeAddress(), ClientErrors::None, recv_buf, recv_len);

        delete p;

        return 0;
    }

    p->data = CTIDBG_new unsigned char[pText.size()];
    std::copy(pText.begin(), pText.end(), p->data);
    p->len = pText.size();

    if( dumpPacket )
    {
        Cti::StreamBuffer output;
        output <<"packet received from "<< p->describeAddress() << endl;

        if( ! pText.empty() )
        {
            output << pText << endl;
        }
        else
        {
            output <<"[empty]"<< endl;
        }

        CTILOG_DEBUG(dout, output);
    }

    validatePacket(p);

    return p;
}


bool UdpPortHandler::validatePacket(ip_packet *&p)
{
    if( Protocols::DNP::DatalinkLayer::isPacketValid(p->data, p->len) )
    {
        p->protocol = packet::ProtocolTypeDnp;
    }
    else if( GpuffProtocol::isPacketValid(p->data, p->len) )
    {
        p->protocol = packet::ProtocolTypeGpuff;
    }
    else
    {
        CTILOG_ERROR(dout, "incoming packet from "<< p->describeAddress() <<" is invalid ");

        //  this packet was unhandled, so we trace it
        traceInbound(p->describeAddress(), ClientErrors::None, p->data, p->len);

        delete p->data;
        delete p;

        p = 0;
    }

    return p;
}


void UdpPortHandler::distributePacket(ip_packet *p)
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
            CTILOG_ERROR(dout, "packet doesn't match any known protocol - discarding");
        }
    }

    if( p )
    {
        //  this packet was unhandled, so we trace it
        traceInbound(p->describeAddress(), ClientErrors::None, p->data, p->len);

        delete p->data;
        delete p;
    }
}


void UdpPortHandler::handleDnpPacket(ip_packet *&p)
{
    DnpLookup::dnp_addresses incoming_address {
        static_cast<unsigned short>(p->data[4] | (p->data[5] << 8)),
        static_cast<unsigned short>(p->data[6] | (p->data[7] << 8)) };

    auto id = _dnpLookup.getDeviceIdForAddress(incoming_address);

    if( ! id )
    {
        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CTILOG_WARN(dout, "Can't find device ID for DNP address " << incoming_address.toString());
        }
        return;
    }
    
    device_record *dr = getDeviceRecordById( *id );

    //  do we have a device yet?
    if( dr && dr->device )
    {
        if( dr->device->isInhibited() )
        {
            CTILOG_WARN(dout, "device \""<< dr->device->getName() <<"\" is inhibited, discarding packet");
        }
        else
        {
            updateDeviceIpAndPort(*dr, *p);

            addInboundWork(*dr, p);

            p = 0;
        }
    }
    else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CTILOG_DEBUG(dout, "can't find device for ID " << *id);
    }
}

void UdpPortHandler::handleGpuffPacket(ip_packet *&p)
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

    CTILOG_INFO(dout, "incoming packet from "<< p->describeAddress());

    GpuffProtocol::describeFrame(p->data, p->len, len, crc_included, ack_required, devt, ser);

    device_record *dr = getDeviceRecordByGpuffDeviceTypeSerial(devt, ser);

    try
    {
        if( dr && dr->device )
        {
            if( dr->device->isInhibited() )
            {
                CTILOG_WARN(dout, "device \""<< dr->device->getName() <<"\" is inhibited, discarding packet");
            }
            else
            {
                updateDeviceIpAndPort(*dr, *p);

                traceInbound(p->describeAddress(), ClientErrors::None, p->data, p->len);

                addInboundWork(*dr, p);

                p = 0;
            }
        }
        else
        {
            CTILOG_ERROR(dout, "no device found for GPUFF serial ("<< ser <<")");
        }
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
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
    return !_udp_sockets.areSocketsValid()
            || getDeviceIp(device_id).empty()
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


std::string UdpPortHandler::getDeviceIp( const long device_id ) const
{
    boost::optional<std::string> ip = mapFind(_ip_addresses, device_id);

    if( ! ip )
    {
        return std::string(); // return empty string
    }

    return *ip;
}

u_short UdpPortHandler::getDevicePort( const long device_id ) const
{
    return find_or_return_numeric_limits_max(_ports, device_id);
}

void UdpPortHandler::setDeviceIp( const long device_id, const string ip )
{
    _ip_addresses[device_id] = ip;
}

void UdpPortHandler::setDevicePort( const long device_id, const u_short port )
{
    _ports[device_id] = port;
}

std::string UdpPortHandler::describeDeviceAddress( const long device_id ) const
{
    string ipAddress = getDeviceIp(device_id);

    if( ipAddress.empty() )
    {
        return "(IP Address is Undefined)";
    }

    boost::optional<u_short> port = mapFind(_ports, device_id);

    if( ! port )
    {
        return ipAddress + " (Port is Undefined)";
    }

    return formatHostAndPort(ipAddress, *port);
}

void UdpPortHandler::loadEncodingFilter()
{
    _encodingFilter = EncodingFilterFactory::getEncodingFilter(_udp_port);
}

}
}

