#include "precompiled.h"

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
#include "database_connection.h"
#include "database_reader.h"
#include "database_util.h"
#include "std_helper.h"
#include "coroutine_util.h"

using namespace std;

extern CtiDeviceManager DeviceManager;

using Cti::Protocols::GpuffProtocol;
using Cti::Timing::MillisecondTimer;
using Cti::Logging::Vector::Hex::operator<<;

namespace Cti    {
namespace Porter {

/* Threads that handle each port for communications */
void PortTcpThread(void *pid)
{
    long portid = (long)pid;

    CtiPortSPtr Port(PortManager.getPortById(portid));

    if( Port && Port->getType() == PortTypeTcp )
    {
        ostringstream thread_name;

        thread_name << "TCP PortID " << setw(4) << setfill('0') << Port->getPortID();

        SetThreadName(-1, thread_name.str().c_str());

        Ports::TcpPortSPtr tcp_port = boost::static_pointer_cast<Ports::TcpPort>(Port);

        TcpPortHandler tcp(tcp_port, DeviceManager);

        tcp.run();

        CTILOG_INFO(dout, "Shutdown PortTcpThread for port: "<< Port->getPortID() <<" / "<< Port->getName());
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
       CTILOG_ERROR(dout, "_tcp_port is Null");
    }

    return !!_tcp_port;
}


bool TcpPortHandler::manageConnections( void )
{
    if( _tcp_port->isInhibited() )
    {
        return false;
    }

    auto [connected, failed] = _connectionManager.updateConnections();

    for( const long device_id : connected )
    {
        updateDeviceCommStatus(device_id, ClientErrors::None);
    }

    for( const long device_id : failed )
    {
        updateDeviceCommStatus(device_id, ClientErrors::DeviceNotConnected);
    }

    return !connected.empty() && !failed.empty();
}


void TcpPortHandler::updateDeviceCommStatus(const long device_id, YukonError_t status)
{
    device_record *dr = getDeviceRecordById(device_id);

    if( dr && dr->device )
    {
        processCommStatus(status, device_id, device_id, false, boost::static_pointer_cast<CtiDeviceBase>(dr->device));
    }
}


void TcpPortHandler::loadDeviceProperties(const vector<const CtiDeviceSingle *> &devices)
{
    set<long> device_ids;

    for( const CtiDeviceSingle *dev : devices )
    {
        if( dev && !dev->isInhibited() )
        {
            device_ids.insert(dev->getID());
        }
    }

    {   // chunk collection so as not to break the reader
        unsigned long max_size = gConfigParms.getValueAsULong("MAX_IDS_PER_SELECT", 950);

        for( const auto& id_chunk : Cti::Coroutines::chunked(device_ids, max_size) )
        {
            std::set<long> idSubset{ id_chunk.begin(), id_chunk.end() };

            loadDeviceTcpProperties(idSubset);
        }
    }
}


void TcpPortHandler::loadDeviceTcpProperties(const set<long> &device_ids)
{
    if(DebugLevel & 0x00020000)
    {
       CTILOG_DEBUG(dout, "Looking for TCP port-related Pao Properties");
    }

    string sql = Database::Tables::PaoPropertyTable::getSQLCoreStatement() +
                 " WHERE (PPR.propertyname = 'TcpPort' OR PPR.propertyname = 'TcpIpAddress')";

    if ( ! device_ids.empty() )
    {
        sql += " AND " + Cti::Database::createIdInClause( "PPR", "paobjectid", device_ids.size() );

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr << device_ids;

        rdr.execute();

        if( ! rdr.isValid() )
        {
            CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
        }
        else if( DebugLevel & 0x00020000 )
        {
            CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
        }

        if(rdr.isValid())
        {
            map<long, unsigned short> tmp_ports;
            map<long, string>         tmp_ip_addresses;

            while( rdr() )
            {
                Database::Tables::PaoPropertyTable paoProperty(rdr);

                if( paoProperty.getPropertyName() == "TcpPort" )
                {
                    auto port = paoProperty.getPropertyValue();

                    if( ! ciStringEqual(port, "(none)") )
                    {
                        tmp_ports[paoProperty.getPaoId()] = atoi(port.c_str());
                    }
                }
                else if( paoProperty.getPropertyName() == "TcpIpAddress" )
                {
                    auto address = paoProperty.getPropertyValue();

                    if( ! ciStringEqual(address, "(none)") )
                    {
                        tmp_ip_addresses[paoProperty.getPaoId()] = address;
                    }
                }
            }

            map<long, unsigned short>::iterator port_itr = tmp_ports.begin();
            map<long, string>::iterator         ip_itr   = tmp_ip_addresses.begin();

            while( port_itr != tmp_ports.end() &&
                   ip_itr   != tmp_ip_addresses.end() )
            {
                if( port_itr->first == ip_itr->first )
                {
                    _connectionManager.connect(port_itr->first, Connections::SocketAddress(ip_itr->second, port_itr->second));
                    ++port_itr;
                    ++ip_itr;
                }
                else if( port_itr->first < ip_itr->first )
                {
                    CTILOG_WARN(dout, "orphan port record ("<< port_itr->first <<","<< port_itr->second <<") found");
                    ++port_itr;
                }
                else
                {
                    CTILOG_WARN(dout, "orphan IP record ("<< port_itr->first <<","<< port_itr->second <<") found");
                    ++ip_itr;
                }
            }
        }
    }
    else 
    {
        CTILOG_WARN(dout, "No devices on port ID " << _tcp_port->getPortID());
    }

    if(DebugLevel & 0x00020000)
    {
        CTILOG_DEBUG(dout, "Done looking for PAO Properties");
    }
}

void TcpPortHandler::addDeviceProperties(const CtiDeviceSingle &device)
{
    vector<const CtiDeviceSingle *> devices;

    devices.push_back(&device);

    loadDeviceProperties(devices);
}

void TcpPortHandler::deleteDeviceProperties(const CtiDeviceSingle &device)
{
    const long device_id = device.getID();

    _connectionManager.disconnect(device_id);
}

void TcpPortHandler::updateDeviceProperties(const CtiDeviceSingle &device)
{
    //  This is a little wrong - if the properties are removed, this won't detect it
    //
    //  We don't really handle nonexistent IPs or ports anyway - we should do that, too

    if( device.isInhibited() )
    {
        deleteDeviceProperties(device);
    }
    else
    {
        addDeviceProperties(device);
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


YukonError_t TcpPortHandler::sendOutbound( device_record &dr )
{
    TcpConnectionManager::bytes buf(dr.xfer.getOutBuffer(),
                                    dr.xfer.getOutBuffer() + dr.xfer.getOutCount());

    Connections::SocketAddress sa = getDeviceSocketAddress(dr.device->getID());

    if( gConfigParms.getValueAsULong("PORTER_TCP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CTILOG_DEBUG(dout, "sending packet to "<< sa <<
                endl << buf);
    }

    try
    {
        int bytes_sent = _connectionManager.send(dr.device->getID(), buf);

        dr.xfer.setOutCount(bytes_sent);

        dr.last_outbound = CtiTime::now();
    }
    catch( TcpConnectionManager::not_connected &ex )
    {
        updateDeviceCommStatus(dr.device->getID(), ClientErrors::DeviceNotConnected);

        return ClientErrors::DeviceNotConnected;
    }
    catch( TcpConnectionManager::write_error &ex )
    {
        updateDeviceCommStatus(dr.device->getID(), ClientErrors::TcpWrite);

        return ClientErrors::TcpWrite;
    }

    return ClientErrors::None;
}


unsigned TcpPortHandler::getDeviceTimeout( const device_record &dr ) const
{
    unsigned portDelay    = _tcp_port->getDelay(EXTRA_DELAY); // Additional timeout for the port.
    unsigned cparmTimeout = gConfigParms.getValueAsInt("PORTER_DNPUDP_TIMEOUT", 10);

    return std::max(portDelay, cparmTimeout);
}


string TcpPortHandler::describePort( void ) const
{
    ostringstream ostr;

    ostr << "TCP port " << _tcp_port->getName();

    return ostr.str();
}


bool TcpPortHandler::collectInbounds( const MillisecondTimer & timer, const unsigned long until)
{
    //  This code, as currently written, assumes that it will read from all ready_devices
    //    and does not allow for breaking out when its timer is expired.
    //  Even though the time to read from "ready" sockets should be negligible, this
    //    behavior will need to change to fully adhere to the cooperative multitasking design of UnsolicitedHandler.

    auto[ready_devices, error_devices] = _connectionManager.recv();

    for( long device_id : ready_devices )
    {
        if( device_record *dr = getDeviceRecordById(device_id) )
        {
            if( isDnpDeviceType(dr->device->getType()) )
            {
                while( auto p = findPacket(device_id, Protocols::DNP::DnpPacketFinder()) )
                {
                    p->protocol = packet::ProtocolTypeDnp;

                    addInboundWork(*dr, p);
                }
            }
            else if( isGpuffDevice(*dr->device) )
            {
                while( auto p = findPacket(device_id, GpuffProtocol::GpuffPacketFinder()) )
                {
                    p->protocol = packet::ProtocolTypeGpuff;

                    addInboundWork(*dr, p);
                }
            }
        }
    }

    for( long device_id : error_devices )
    {
        updateDeviceCommStatus(device_id, ClientErrors::TcpRead);
    }

    return !ready_devices.empty() || !error_devices.empty();
}


UnsolicitedHandler::packet *TcpPortHandler::findPacket( const long device_id, Protocols::PacketFinder &pf )
{
    if( !_connectionManager.searchStream(device_id, pf) )
    {
        return 0;
    }

    ip_packet *p = new ip_packet;

    p->protocol = packet::ProtocolTypeInvalid;  // should be set by whoever calls findPacket()

    Connections::SocketAddress sa = getDeviceSocketAddress(device_id);

    p->ip   = sa.ip;
    p->port = sa.port;

    p->data = new unsigned char[pf.size()];
    p->len  = pf.size();

    p->used = 0;

    copy(pf.begin(), pf.end(), p->data);

    return p;
}


Connections::SocketAddress TcpPortHandler::getDeviceSocketAddress( const long device_id ) const
{
    try
    {
        return _connectionManager.getAddress(device_id);
    }
    catch( TcpConnectionManager::no_record &ex )
    {
        return Connections::SocketAddress(string(), numeric_limits<u_short>::max());
    }
}

std::string TcpPortHandler::describeDeviceAddress( const long device_id ) const
{
    Connections::SocketAddress address = getDeviceSocketAddress(device_id);

    return address.toString();
}


bool TcpPortHandler::isDeviceDisconnected( const long device_id ) const
{
    return !_connectionManager.isConnected(device_id);
}


}
}

