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

using namespace std;

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
    if( _tcp_port->isInhibited() )
    {
        return false;
    }

    TcpConnectionManager::id_set connected, failed;

    _connectionManager.checkPendingConnections(connected, failed);

    for each( const long device_id in connected )
    {
        updateDeviceCommStatus(device_id, NoError);
    }

    for each( const long device_id in failed )
    {
        updateDeviceCommStatus(device_id, ErrorDeviceNotConnected);
    }

    return !connected.empty() && !failed.empty();
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
    loadDeviceTcpProperties(device_ids);

    for each( long device_id in device_ids )
    {
        _connectionManager.connect(device_id, getDeviceIp(device_id), getDevicePort(device_id));
    }
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

void TcpPortHandler::deleteDeviceProperties(const CtiDeviceSingle &device)
{
    const long device_id = device.getID();

    _ip_addresses.erase(device_id);
    _ports.erase(device_id);

    _connectionManager.disconnect(device_id);
}

void TcpPortHandler::updateDeviceProperties(const CtiDeviceSingle &device)
{
    const long device_id = device.getID();

    //  This is a little wrong - if the properties are removed, this won't detect it
    //    We don't really handle nonexistent IPs or ports anyway - we should do that, too
    addDeviceProperties(device);

    if( device.isInhibited() )
    {
        _connectionManager.disconnect(device_id);
    }
    else
    {
        _connectionManager.connect(device_id, getDeviceIp(device_id), getDevicePort(device_id));
    }
}


void TcpPortHandler::updatePortProperties( void )
{
    if( _tcp_port->isInhibited() )
    {
        _connectionManager.disable();
    }
    else
    {
        _connectionManager.enable();
    }
}


void TcpPortHandler::teardownPort( void )
{
    //  close all open sockets
    _connectionManager.disconnectAll();
}


void TcpPortHandler::sendOutbound( device_record &dr )
{
    //  if we don't have a device or anything to send, there's nothing to do here
    if( !dr.device || dr.work.xfer.getOutCount() == 0 )
    {
        return;
    }

    if( !_connectionManager.isConnected(dr.id) )
    {
        dr.work.status = ErrorDeviceNotConnected;

        return;
    }

    if( gConfigParms.getValueAsULong("PORTER_TCP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::TcpPortHandler::sendOutbound() - sending packet to "
                          << ip_to_string(getDeviceIp(dr.id)) << ":" << getDevicePort(dr.id) << " "
                          << __FILE__ << " (" << __LINE__ << ")" << endl;

        for( int xx = 0; xx < dr.work.xfer.getOutCount(); xx++ )
        {
            dout << " " << CtiNumStr(dr.work.xfer.getOutBuffer()[xx]).hex().zpad(2).toString();
        }

        dout << endl;
    }

    TcpConnectionManager::bytes buf;

    buf.reserve(dr.work.xfer.getOutCount());

    copy(dr.work.xfer.getOutBuffer(),
         dr.work.xfer.getOutBuffer() + dr.work.xfer.getOutCount(),
         back_inserter(buf));

    int bytes_sent = _connectionManager.send(dr.id, buf);

    if( bytes_sent == SOCKET_ERROR )
    {
        updateDeviceCommStatus(dr.id, TCPWRITEERROR);

        traceOutbound(dr, TCPWRITEERROR);
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

    TcpConnectionManager::id_set ready_devices, error_devices;

    _connectionManager.recv(ready_devices, error_devices);

    for each( long device_id in ready_devices )
    {
        device_record *dr = getDeviceRecordById(device_id);

        if( !dr )
        {
            continue;
        }

        packet *p = 0;

        if( isDnpDevice(*dr->device) )
        {
            while( p = findPacket(device_id, Protocol::DNP::DnpPacketFinder()) )
            {
                p->protocol = packet::ProtocolTypeDnp;

                addInboundWork(dr, p);
            }
        }
        else if( isGpuffDevice(*dr->device) )
        {
            while( p = findPacket(device_id, GpuffProtocol::GpuffPacketFinder()) )
            {
                p->protocol = packet::ProtocolTypeGpuff;

                addInboundWork(dr, p);
            }
        }
    }

    for each( long device_id in error_devices )
    {
        updateDeviceCommStatus(device_id, TCPREADERROR);
    }

    return !ready_devices.empty() || !error_devices.empty();
}


UnsolicitedHandler::packet *TcpPortHandler::findPacket( const long device_id, Protocols::PacketFinder &pf )
{
    if( !_connectionManager.searchStream(device_id, pf) )
    {
        return 0;
    }

    packet *p = new packet;

    p->protocol = packet::ProtocolTypeInvalid;  // should be set by whoever calls findPacket()

    p->ip   = getDeviceIp  (device_id);
    p->port = getDevicePort(device_id);

    p->data = new unsigned char[pf.size()];
    p->len  = pf.size();

    p->used = 0;

    copy(pf.begin(), pf.end(), p->data);

    return p;
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


bool TcpPortHandler::isDeviceDisconnected( const long device_id ) const
{
    return !_connectionManager.isConnected(device_id);
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

