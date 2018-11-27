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
#include "desolvers.h"

#include <boost/range/adaptor/map.hpp>
#include <boost/range/adaptor/filtered.hpp>
#include <boost/algorithm/cxx11/all_of.hpp>

using namespace std;

extern CtiDeviceManager DeviceManager;

using Cti::Protocols::GpuffProtocol;
using Cti::Timing::MillisecondTimer;
using Cti::Logging::Vector::Hex::operator<<;

namespace Cti::Porter {

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

    updateCommStatuses(connected, ClientErrors::None);
    updateCommStatuses(failed,    ClientErrors::DeviceNotConnected);

    return ! connected.empty() && ! failed.empty();
}


void TcpPortHandler::updateCommStatuses(std::set<Connections::SocketAddress> addresses, YukonError_t status)
{
    for( const auto addr : addresses )
    {
        updateCommStatus(addr, status);
    }
}

void TcpPortHandler::updateCommStatus(Connections::SocketAddress addr, YukonError_t status)
{
    for( const auto device_id : _address_devices.left.equal_range(addr) | boost::adaptors::map_values )
    {
        updateDeviceCommStatus(device_id, status);
    }
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
    auto enabled = [](const CtiDeviceSingle* dev) {
        return dev && ! dev->isInhibited();
    };

    auto enabledDevices = boost::copy_range<std::vector<const CtiDeviceSingle*>>(devices | boost::adaptors::filtered(enabled));

    // chunk collection so as not to break the reader
    unsigned long max_size = gConfigParms.getValueAsULong("MAX_IDS_PER_SELECT", 950);

    for( const auto& device_chunk : Cti::Coroutines::chunked(enabledDevices, max_size) )
    {
        loadDeviceTcpProperties(device_chunk);
    }
}


template<class T>
void TcpPortHandler::loadDeviceTcpProperties(const T& devices)
{
    auto device_ids = devices | boost::adaptors::transformed([](const CtiDeviceSingle* dev) { return dev->getID(); });

    std::map<long, const CtiDeviceSingle*> deviceLookup;
    
    for( auto dev : devices )
    {
        deviceLookup.emplace(dev->getID(), dev);
    }

    if(DebugLevel & 0x00020000)
    {
       CTILOG_DEBUG(dout, "Looking for TCP port-related Pao Properties");
    }

    std::string sql = 
        "SELECT"
            " pp1.PAObjectId"
            ", pp1.PropertyValue"
            ", pp2.PropertyValue"
        " FROM"
            " PAOProperty pp1"
                " JOIN PAOProperty pp2"
                    " ON pp1.PAObjectId = pp2.PAObjectId"
        " WHERE"
            " pp1.PropertyName = 'TcpIpAddress'"
            " AND pp2.PropertyName = 'TcpPort'"
            " AND pp1.PropertyValue <> '(none)'"
            " AND pp2.PropertyValue <> '(none)'";
        
    if ( ! device_ids.empty() )
    {
        sql += " AND " + Cti::Database::createIdInClause( "pp1", "PAObjectId", device_ids.size() );

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

        if( rdr.isValid() )
        {
            while( rdr() )
            {
                const auto device_id  = rdr[0].as<long>();
                const auto ip_address = rdr[1].as<std::string>();
                const auto port       = rdr[2].as<unsigned short>();

                Connections::SocketAddress addr { ip_address, port };

                if( auto dev = mapFind(deviceLookup, device_id) )
                {
                    _connectionManager.connect(addr);

                    _address_devices.insert({ addr, device_id });

                    _socketAddresses[addr].addDevice(**dev);
                }
                else
                {
                    CTILOG_WARN(dout, "No device found for device ID " << device_id << ", " << ip_address << " port " << port);
                }
            }
        }
    }
    else 
    {
        CTILOG_WARN(dout, "No devices on port ID " << _tcp_port->getPortID());
    }

    if( DebugLevel & 0x00020000 )
    {
        CTILOG_DEBUG(dout, "Done looking for PAO Properties");
    }
}

void TcpPortHandler::addDeviceProperties(const CtiDeviceSingle &device)
{
    loadDeviceProperties({ &device });
}

void TcpPortHandler::deleteDeviceProperties(const CtiDeviceSingle &device)
{
    if( auto itr = _address_devices.right.find(device.getID()); 
        itr != _address_devices.right.end() )
    {
        const auto addr = itr->second;

        _socketAddresses[addr].deleteDevice(device.getID());

        _address_devices.right.erase(itr);

        //  If there are no other devices using this address, disconnect it.
        if( ! _address_devices.left.count(addr) )
        {
            _connectionManager.disconnect(addr);
        }
    }
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

    auto addr = getDeviceSocketAddress(dr.device->getID());

    if( gConfigParms.getValueAsULong("PORTER_TCP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CTILOG_DEBUG(dout, "sending packet to "<< addr <<
                endl << buf);
    }

    try
    {
        int bytes_sent = _connectionManager.send(addr, buf);

        dr.xfer.setOutCount(bytes_sent);

        dr.last_outbound = CtiTime::now();
    }
    catch( TcpConnectionManager::not_connected &ex )
    {
        updateCommStatus(addr, ClientErrors::DeviceNotConnected);

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

    auto [ready_addresses, error_addresses] = _connectionManager.recv();

    using boost::adaptors::transformed;
    using boost::adaptors::filtered;

    for( auto addr : ready_addresses )
    {
        auto device_ids = _address_devices.left.equal_range(addr) | boost::adaptors::map_values;

        auto device_records = 
            device_ids 
                | transformed([this](long device_id)      { return getDeviceRecordById(device_id); }) 
                | filtered   ([](const device_record* dr) { return dr && dr->device; });

        auto device_types =
            device_records 
                | transformed([this](const device_record* dr) { return dr->device->getDeviceType(); });

        if( boost::algorithm::all_of(device_types, isDnpDeviceType) )
        {
            if( const auto addressLookup = mapFind(_socketAddresses, addr) )
            {
                while( auto p = findPacket(addr, Protocols::DNP::DnpPacketFinder()) )
                {
                    p->protocol = packet::ProtocolTypeDnp;

                    auto header = reinterpret_cast<const Protocols::DNP::DatalinkPacket::dlp_header_formatted&>(*p->data);

                    const auto outstation = header.source;
                    const auto master = header.destination;

                    DnpLookup::dnp_addresses incoming_address { master, outstation };

                    if( auto device_id = addressLookup->getDeviceIdForAddress(incoming_address) )
                    {
                        if( auto dr = getDeviceRecordById(*device_id) )
                        {
                            addInboundWork(*dr, p);
                        }
                        else
                        {
                            CTILOG_WARN(dout, "No device record found for device ID " << *device_id);
                        }
                    }
                    else
                    {
                        CTILOG_WARN(dout, "No device ID found for DNP address (M:" << master << ",O:" << outstation << ")");
                    }
                }
            }
            else
            {
                CTILOG_WARN(dout, "No DNP device lookup found for TCP port " << addr);

                //  clear socket stream?
            }
        }
        else if( boost::size(device_records) == 1 && isGpuffDevice(*device_records.front()->device) )
        {
            while( auto p = findPacket(addr, GpuffProtocol::GpuffPacketFinder()) )
            {
                p->protocol = packet::ProtocolTypeGpuff;

                addInboundWork(*(device_records.front()), p);
            }
        }
        else
        {
            FormattedList l;

            l.add("Device ID") << "Device type";

            for( const auto& dr : device_records )
            {
                l.add(std::to_string(dr->device->getID())) << desolveDeviceType(dr->device->getType());
            }

            CTILOG_WARN(dout, "Unsupported combination of device types found on port:" << l);

            //  clear socket stream?
        }
    }

    updateCommStatuses(error_addresses, ClientErrors::TcpRead);

    return ! (ready_addresses.empty() && error_addresses.empty());
}


UnsolicitedHandler::packet *TcpPortHandler::findPacket( const Connections::SocketAddress addr, Protocols::PacketFinder &pf )
{
    if( !_connectionManager.searchStream(addr, pf) )
    {
        return 0;
    }

    ip_packet *p = new ip_packet;

    p->protocol = packet::ProtocolTypeInvalid;  // should be set by whoever calls findPacket()

    p->ip   = addr.ip;
    p->port = addr.port;

    p->data = new unsigned char[pf.size()];
    p->len  = pf.size();

    p->used = 0;

    copy(pf.begin(), pf.end(), p->data);

    return p;
}


Connections::SocketAddress TcpPortHandler::getDeviceSocketAddress( const long device_id ) const
{
    static const Connections::SocketAddress invalidAddress { string(), numeric_limits<u_short>::max() };

    return mapFindOrDefault(_address_devices.right, device_id, invalidAddress);
}

std::string TcpPortHandler::describeDeviceAddress( const long device_id ) const
{
    return getDeviceSocketAddress(device_id).toString();
}


bool TcpPortHandler::isDeviceDisconnected( const long device_id ) const
{
    if( auto address = mapFind(_address_devices.right, device_id) )
    {
        return ! _connectionManager.isConnected(*address);
    }

    return true;
}


}