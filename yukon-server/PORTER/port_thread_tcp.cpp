#include "yukon.h"

#include <winsock2.h>

#include "port_thread_tcp.h"

#include "tbl_paoproperty.h"

#include "c_port_interface.h"
#include "portglob.h"
#include "portfield.h"
#include "cparms.h"

#include "prot_gpuff.h"
#include "prot_dnp.h"

#include "mgr_port.h"
#include "mgr_device.h"

#include "boostutil.h"
#include <boost/scoped_array.hpp>

using namespace std;

// Some Global Manager types to allow us some RTDB stuff.
extern CtiConnection VanGoghConnection;
extern CtiDeviceManager DeviceManager;

namespace Cti    {
namespace Porter {


using Protocols::GpuffProtocol;

/* Threads that handle each port for communications */
VOID PortTcpThread(void *pid)
{
    long portid = (long)pid;

    CtiPortSPtr Port(PortManager.PortGetEqual(portid));

    if( Port && Port->getType() == PortTypeTcp )
    {
        ostringstream thread_name;

        thread_name << "TCP PortID " << setw(4) << setfill('0') << Port->getPortID();

        SetThreadName(-1, thread_name.str().c_str());

        Ports::TcpPortSPtr tcp_port = boost::static_pointer_cast<Ports::TcpPort>(Port);

        TcpPortHandler tcp(tcp_port, DeviceManager);

        tcp.run();
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Shutdown PortThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
    }
}

TcpPortHandler::TcpPortHandler( Ports::TcpPortSPtr &port, CtiDeviceManager &deviceManager ) :
    UnsolicitedHandler(boost::static_pointer_cast<CtiPort>(port), deviceManager),
    _tcp_port(port)
{
}


bool TcpPortHandler::setupPort()
{
    if( !_tcp_port )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - _tcp_port == 0 in TcpPortHandler::setup() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return _tcp_port;
}


bool TcpPortHandler::manageConnections( void )
{
    bool active = false;

    vector<connection_itr> in_progress;

    const CtiTime Now;

    for( connection_itr itr = _connections.begin(); itr != _connections.end(); ++itr )
    {
        const long device_id = itr->first;
        connection &c        = itr->second;

        switch( c.state )
        {
            case connection::Disconnected:
            {
                //  only try to connect every connect_timeout seconds at max
                if( c.connect_timeout < Now )
                {
                    connectToDevice(device_id, c);

                    active = true;
                }

                break;
            }
            case connection::Connecting:
            {
                in_progress.push_back(itr);

                break;
            }
        }
    }

    checkPendingConnections(in_progress);

    return active;
}


void TcpPortHandler::checkPendingConnections(vector<connection_itr> &in_progress)
{
    vector<connection_itr>::iterator itr = in_progress.begin();

    while( itr != in_progress.end() )
    {
        fd_set writeable_sockets;

        vector<connection_itr> candidate_connections;

        FD_ZERO(&writeable_sockets);

        int socket_count = 0;

        //  check in blocks of up to FD_SETSIZE
        for( ; itr != in_progress.end() && socket_count < FD_SETSIZE; ++itr, ++socket_count )
        {
            const connection &c = (*itr)->second;

            candidate_connections.push_back(*itr);

            FD_SET(c.socket, &writeable_sockets);
        }

        checkPendingConnectionBlock(socket_count, writeable_sockets, candidate_connections);
    }
}


void TcpPortHandler::checkPendingConnectionBlock(const int socket_count, fd_set &writeable_sockets, vector<connection_itr> &candidate_connections)
{
    timeval tv = { 0, 1000 };  //  0 seconds + 1000 microseconds = 1 millisecond
    const CtiTime Now;

    //  check to see if any of the sockets are writable - a writable socket means the connection has completed
    int ready_count = select(socket_count, NULL, &writeable_sockets, NULL, &tv);

    if( ready_count == SOCKET_ERROR )
    {
        reportSocketError("checkPendingConnectionBlock", "select", 0, __FILE__, __LINE__);

        ready_count = 0;
    }

    for each( connection_itr conn_itr in candidate_connections )
    {
        const long device_id = conn_itr->first;
        connection &c        = conn_itr->second;

        if( ready_count && FD_ISSET(c.socket, &writeable_sockets) )
        {
            setConnectionOptions(device_id, c);

            c.state = connection::Connected;

            updateDeviceCommStatus(device_id, NoError);
        }
        else if( c.connect_timeout > Now )
        {
            disconnectFromDevice(device_id, c);

            updateDeviceCommStatus(device_id, ErrorDeviceNotConnected);
        }
    }
}


void TcpPortHandler::setConnectionOptions(const long device_id, connection &c)
{
    //  none of these are fatal errors, so we'll just log and continue

    //  Make sure we time out on our writes after 5 seconds
    int socket_write_timeout = gConfigParms.getValueAsInt("PORTER_SOCKET_WRITE_TIMEOUT", 5);
    if( setsockopt(c.socket, SOL_SOCKET, SO_SNDTIMEO, reinterpret_cast<char *>(&socket_write_timeout), sizeof(socket_write_timeout)) )
    {
        reportSocketError("setConnectionOptions", "setsockopt", device_id, __FILE__, __LINE__);
    }

    //  Turn on the keepalive timer
    int keepalive_timer = 1;
    if( setsockopt(c.socket, SOL_SOCKET, SO_KEEPALIVE, reinterpret_cast<char *>(&keepalive_timer), sizeof(keepalive_timer)) )
    {
        reportSocketError("setConnectionOptions", "setsockopt", device_id, __FILE__, __LINE__);
    }

    //  enable a hard close - erases all pending outbound data, sends a reset to the other side
    linger l = {1, 0};
    if( setsockopt(c.socket, SOL_SOCKET, SO_LINGER, reinterpret_cast<char *>(&l), sizeof(l)) )
    {
        reportSocketError("setConnectionOptions", "setsockopt", device_id, __FILE__, __LINE__);
    }
}



void TcpPortHandler::updateDeviceCommStatus(const long device_id, int status)
{
    device_record *dr = getDeviceRecordById(device_id);

    if( dr && dr->device )
    {
        processCommStatus(status, dr->id, dr->id, false, boost::static_pointer_cast<CtiDeviceBase>(dr->device));
    }
}


void TcpPortHandler::loadDeviceProperties(const set<long> &device_ids)
{
    for each(long device_id in device_ids)
    {
        _connections[device_id];  //  initialize the connection
    }

    loadDeviceTcpProperties(device_ids);
}

void TcpPortHandler::loadDeviceTcpProperties(const set<long> &device_ids)
{
    RWDBConnection conn   = getConnection();
    RWDBDatabase db       = getDatabase();
    RWDBSelector selector = db.selector();
    RWDBTable keyTable;
    RWDBReader rdr;

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for TCP port-related Pao Properties" << endl;
    }

    Database::Tables::PaoPropertyTable::getSQL(db, keyTable, selector);

    addIDClause(selector, keyTable["paobjectid"], device_ids);

    selector.where(selector.where() && (keyTable["propertyname"] == "TcpPort" ||
                                        keyTable["propertyname"] == "TcpIpAddress"));

    rdr = selector.reader(conn);
    if(DebugLevel & 0x00020000 || selector.status().errorCode() != RWDBStatus::ok)
    {
        string loggedSQLstring = selector.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << loggedSQLstring << endl;
        }
    }

    if(rdr.status().errorCode() == RWDBStatus::ok)
    {
        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
        {
            Database::Tables::PaoPropertyTable paoProperty(rdr);

            if( paoProperty.getPropertyName() == "TcpPort" )
            {
                _ports[paoProperty.getPaoId()] = atoi(paoProperty.getPropertyValue().c_str());
            }
            else if( paoProperty.getPropertyName() == "TcpIpAddress" )
            {
                _ip_addresses[paoProperty.getPaoId()] = resolveIp(paoProperty.getPropertyValue());
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Error reading Dynamic PAO Info from database: " << rdr.status().errorCode() << endl;
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Dynamic PAO Info" << endl;
    }
}


u_long TcpPortHandler::resolveIp(const string &ip)
{
    if( ip.empty() )
    {
        return INADDR_NONE;
    }

    //  looks like a hostname - try to resolve it
    if( isalpha(ip[0]) )
    {
        LPHOSTENT lpHostEntry = gethostbyname(ip.c_str());

        return *(u_long *)(lpHostEntry->h_addr_list[0]);
    }
    else
    {
        return inet_addr(ip.c_str());
    }
}


void TcpPortHandler::addDeviceProperties(const CtiDeviceSingle &device)
{
    set<long> paoids;

    paoids.insert(device.getID());

    loadDeviceProperties(paoids);
}

template<class Map, class Key>
void erase_from_map(Map &m, const Key &k)
{
    Map::iterator itr = m.find(k);

    if( itr != m.end() )
    {
        m.erase(itr);
    }
}

void TcpPortHandler::deleteDeviceProperties(const CtiDeviceSingle &device)
{
    const long device_id = device.getID();

    erase_from_map(_ip_addresses, device_id);
    erase_from_map(_ports,        device_id);
}


void TcpPortHandler::updateDeviceProperties(const CtiDeviceSingle &device)
{
    const long device_id = device.getID();

    u_long  old_ip   = getDeviceIp  (device_id);
    u_short old_port = getDevicePort(device_id);

    deleteDeviceProperties(device);
    addDeviceProperties   (device);

    if( old_ip   != getDeviceIp  (device_id) ||
        old_port != getDevicePort(device_id) )
    {
        connection &c = _connections[device_id];

        disconnectFromDevice(device_id, c);
        connectToDevice     (device_id, c);
    }
}


void TcpPortHandler::reportSocketError(const string method_name, const string winsock_function_name, const long device_id, const char *file, const int line)
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime() << " **** Checkpoint - " << winsock_function_name << "() returned (" << WSAGetLastError() << ") for device_id (" << device_id << ") in TcpPortHandler::" << method_name << "() **** " << file << " (" << line << ")" << endl;
}


bool TcpPortHandler::connectToDevice(const long device_id, connection &c)
{
    SOCKET s = socket(AF_INET, SOCK_STREAM, 0);

    if( s == INVALID_SOCKET)
    {
        reportSocketError("connectToDevice", "socket", device_id, __FILE__, __LINE__);

        return false;
    }

    unsigned long nonblocking = 1;
    if( ioctlsocket(s, FIONBIO, &nonblocking) )
    {
        reportSocketError("connectToDevice", "ioctlsocket", device_id, __FILE__, __LINE__);

        closesocket(s);

        return false;
    }

    const u_short port = getDevicePort(device_id);
    const u_long  ip   = getDeviceIp  (device_id);

    sockaddr_in device_address = { AF_INET, htons(port), *(in_addr*)&ip, 0 };

    if( connect(s, reinterpret_cast<sockaddr *>(&device_address), sizeof(device_address)) == SOCKET_ERROR)
    {
        if( WSAGetLastError() != WSAEWOULDBLOCK )
        {
            reportSocketError("connectToDevice", "connect", device_id, __FILE__, __LINE__);

            closesocket(s);

            return false;
        }
    }

    c.state  = connection::Connecting;
    c.socket = s;
    c.connect_timeout = CtiTime::now() + gConfigParms.getValueAsULong("PORTER_TCP_CONNECT_TIMEOUT", 15);

    return true;
}


void TcpPortHandler::disconnectFromDevice(const long device_id, connection &c)
{
    if( c.state == connection::Disconnected )
    {
        return;
    }

    if( c.state == connection::Connected )
    {
        if( shutdown(c.socket, SD_BOTH) )
        {
            reportSocketError("disconnectFromDevice", "shutdown", device_id, __FILE__, __LINE__);
        }
    }

    if( closesocket(c.socket) )
    {
        reportSocketError("disconnectFromDevice", "close", device_id, __FILE__, __LINE__);
    }

    c.socket = INVALID_SOCKET;
    c.state  = connection::Disconnected;
}


void TcpPortHandler::teardownPort()
{
    //  close all open sockets
    for( connection_itr itr = _connections.begin(); itr != _connections.end(); ++itr )
    {
        const long device_id = itr->first;
        connection &c        = itr->second;

        disconnectFromDevice(device_id, c);
    }

    _connections.clear();
}


void TcpPortHandler::sendOutbound( device_record &dr )
{
    //  if we don't have a device or anything to send, there's nothing to do here
    if( !dr.device || dr.work.xfer.getOutCount() == 0 )
    {
        return;
    }

    connection_itr itr = _connections.find(dr.id);

    if( itr == _connections.end() )
    {
        //  we don't know this ID?
        dr.work.status = IDNF;

        return;
    }

    connection &c = itr->second;

    if( c.state != connection::Connected )
    {
        dr.work.status = ErrorDeviceNotConnected;

        return;
    }

    if( gConfigParms.getValueAsULong("PORTER_TCP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::TcpPortHandler::sendOutbound() - sending packet to "
                          << getDeviceIp(dr.id) << ":" << getDevicePort(dr.id) << " "
                          << __FILE__ << " (" << __LINE__ << ")" << endl;

        for( int xx = 0; xx < dr.work.xfer.getOutCount(); xx++ )
        {
            dout << " " << CtiNumStr(dr.work.xfer.getOutBuffer()[xx]).hex().zpad(2).toString();
        }

        dout << endl;
    }

    int bytes_sent = send(c.socket, (char *)dr.work.xfer.getOutBuffer(),
                                            dr.work.xfer.getOutCount(), 0);

    if( bytes_sent == SOCKET_ERROR )
    {
        reportSocketError("sendOutbound", "send", dr.id, __FILE__, __LINE__);

        disconnectFromDevice(dr.id, c);

        updateDeviceCommStatus(dr.id, TCPWRITEERROR);

        traceOutbound(dr, WSAGetLastError());
    }
    else
    {
        dr.work.xfer.setOutCount(bytes_sent);

        traceOutbound(dr, 0);

        dr.work.last_outbound = CtiTime::now();
    }
}


string TcpPortHandler::describePort( void ) const
{
    ostringstream ostr;

    ostr << "TCP port " << _tcp_port->getName();

    return ostr.str();
}


bool TcpPortHandler::collectInbounds( void )
{
    bool data_received = false;

    connection_const_itr itr = _connections.begin();

    while( itr != _connections.end() )
    {
        int sockets;
        fd_set readable_sockets;
        vector<connection_const_itr> candidate_sockets;

        FD_ZERO(&readable_sockets);

        //  check in blocks of up to FD_SETSIZE
        for( sockets = 0; itr != _connections.end() && sockets < FD_SETSIZE; ++itr )
        {
            const connection &c = itr->second;

            if( c.state == connection::Connected )
            {
                candidate_sockets.push_back(itr);

                FD_SET(c.socket, &readable_sockets);

                sockets++;
            }
        }

        if( sockets > 0 )
        {
            timeval tv = { 0, 1000 };  //  0 seconds + 1000 microseconds = 1 millisecond

            int ready_count = select(sockets, &readable_sockets, NULL, NULL, &tv);

            if( ready_count == SOCKET_ERROR )
            {
                reportSocketError("collectInbounds", "select", 0, __FILE__, __LINE__);
            }
            else if( ready_count > 0 )
            {
                for each( connection_const_itr itr in candidate_sockets )
                {
                    const connection &c = itr->second;

                    if( FD_ISSET(c.socket, &readable_sockets) )
                    {
                        readInput(itr->first);
                    }
                }
            }
        }
    }

    return data_received;
}


void TcpPortHandler::readInput(const long device_id)
{
    device_record *dr = getDeviceRecordById(device_id);

    if( !dr || !dr->device )
    {
        return;
    }

    connection_itr itr = _connections.find(device_id);

    if( itr == _connections.end() )
    {
        return;
    }

    connection &c = itr->second;

    u_long bytes_available;

    if( ioctlsocket(c.socket, FIONREAD, &bytes_available) )
    {
        reportSocketError("readInput", "ioctlsocket", device_id, __FILE__, __LINE__);
        bytes_available = 0;
    }

    //  we got in here because the socket reported it was readable - if we get 0 bytes, the connection must be closed
    if( !bytes_available )
    {
        disconnectFromDevice(device_id, c);

        updateDeviceCommStatus(device_id, TCPREADERROR);

        return;
    }

    boost::scoped_array<char> recv_buf(new char[bytes_available]);

    int bytes_read = recv(c.socket, recv_buf.get(), bytes_available, 0);

    //  we got in here because the socket reported it was readable - if we get 0 bytes, the connection must be closed
    if( !bytes_read )
    {
        disconnectFromDevice(device_id, c);

        updateDeviceCommStatus(device_id, TCPREADERROR);

        return;
    }

    copy(recv_buf.get(),
         recv_buf.get() + bytes_read,
         back_inserter(c.stream));

    //  this didn't come together how I'd hoped - the iterator syntax almost works for unsigned char * access into the
    //    stream object, but it's a little dodgy.  I'd like to abstract that away - the translations are gross right now.

    if( isDnpDevice(*dr->device) )
    {
        unsigned char *packet_begin = &c.stream.front();
        unsigned char *packet_end   = packet_begin + c.stream.size();

        if( Protocol::DNP::Datalink::findPacket(packet_begin, packet_end) )
        {
            const int packet_size = distance(packet_begin, packet_end);

            packet *p = new packet;

            p->protocol = packet::ProtocolTypeDnp;

            p->ip   = getDeviceIp  (device_id);
            p->port = getDevicePort(device_id);

            p->data = new unsigned char[packet_size];
            p->len  = packet_size;

            p->used = 0;

            copy(packet_begin, packet_end, p->data);

            dr->work.inbound.push(p);

            c.stream.erase(c.stream.begin(), c.stream.begin() + (packet_end - &c.stream.front()));
        }
    }
    else if( isGpuffDevice(*dr->device) )
    {
        unsigned char *packet_begin = &c.stream.front();
        unsigned char *packet_end   = packet_begin + c.stream.size();

        if( GpuffProtocol::findPacket(packet_begin, packet_end) )
        {
            const int packet_size = distance(packet_begin, packet_end);

            packet *p = new packet;

            p->protocol = packet::ProtocolTypeGpuff;

            p->ip   = getDeviceIp  (device_id);
            p->port = getDevicePort(device_id);

            p->data = new unsigned char[packet_size];
            p->len  = packet_size;

            p->used = 0;

            copy(packet_begin, packet_end, p->data);

            dr->work.inbound.push(p);

            c.stream.erase(c.stream.begin(), c.stream.begin() + (packet_end - &c.stream.front()));
        }
    }
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


u_long TcpPortHandler::getDeviceIp( const long device_id ) const
{
    return find_or_return_numeric_limits_max(_ip_addresses, device_id);
}

u_short TcpPortHandler::getDevicePort( const long device_id ) const
{
    return find_or_return_numeric_limits_max(_ports, device_id);
}


string TcpPortHandler::ip_to_string(u_long ip) const
{
    ostringstream ostr;

    ostr << ((ip >>  0) & 0xff) << ".";
    ostr << ((ip >>  8) & 0xff) << ".";
    ostr << ((ip >> 16) & 0xff) << ".";
    ostr << ((ip >> 24) & 0xff);

    return ostr.str();
}


}
}

