#include "precompiled.h"

#include "db_connection_proxy.h"
#include "dbaccess.h"

#include "critical_section.h"
#include "guard.h"

#include <SQLAPI.h>

namespace Cti {
namespace Database {

CtiCriticalSection                            ConnectionManager::idleConnectionMutex;
std::stack<ConnectionManager::IdleConnection> ConnectionManager::idleConnections;
    
ConnectionManager::ConnectionInfo::ConnectionInfo( const std::unique_ptr<SAConnection>& connection )
    :   instance { ++instanceCount },
        clientVersion { connection ? connection->ClientVersion() : 0 },
        serverVersion { connection ? connection->ServerVersion() : 0 }
{ }

std::atomic_size_t ConnectionManager::ConnectionInfo::instanceCount{};

ConnectionManager::ConnectionDescriptor::ConnectionDescriptor( std::unique_ptr<SAConnection>&& conn )
    :   connection { std::move(conn) },
        info { connection }
{ }

std::string ConnectionManager::ConnectionDescriptor::toString() const
{
    return Cti::FormattedList::of(
        "Connection instance", info.instance,
        "Creation time", info.created,
        "Last borrowed", lastBorrowed.isValid() ? lastBorrowed.asString() : "never"
        "Last returned", lastReturned.isValid() ? lastReturned.asString() : "never"
        "Client major version", info.clientVersion >> 16,
        "Client minor version", info.clientVersion & 0xffff,
        "Server major version", info.serverVersion >> 16,
        "Server minor version", info.serverVersion & 0xffff);
}

void ConnectionManager::ConnectionReleaser::operator()(ConnectionDescriptor *desc)
{
    CtiLockGuard<CtiCriticalSection> guard(idleConnectionMutex);

    desc->lastReturned = CtiTime::now();

    //  Convert the LoanedConnecton back into an IdleConnection
    idleConnections.emplace(desc);
}

auto ConnectionManager::borrowConnection() -> LoanedConnection
{
    {
        CtiLockGuard<CtiCriticalSection> guard(idleConnectionMutex);

        while( !idleConnections.empty() )
        {
            IdleConnection descriptor{ std::move(idleConnections.top()) };
            idleConnections.pop();

            if( descriptor )
            {
                if( descriptor->connection )
                {
                    if( descriptor->connection->isAlive() )
                    {
                        CTILOG_TRACE(dout, "Existing database connection retrieved:" << *descriptor);

                        descriptor->lastBorrowed = CtiTime::now();

                        return LoanedConnection { descriptor.release() };
                    }
                    else
                    {
                        CTILOG_INFO(dout, "Existing database connection was not alive, releasing" << *descriptor);
                    }
                }
                else
                {
                    CTILOG_WARN(dout, "Existing database connection was null, releasing" << *descriptor);
                }
            }
            else
            {
                CTILOG_WARN(dout, "Existing descriptor was null");
            }
        }
    }

    // We need a new connection, no idle connections!
    if( auto dbConnection = gDatabaseConnectionFactory() )
    {
        LoanedConnection descriptor { new ConnectionDescriptor(std::unique_ptr<SAConnection>(dbConnection)) };

        CTILOG_INFO(dout, "New database connection created:" << *descriptor);

        return descriptor;
    }

    CTILOG_ERROR(dout, "Could not create a new database connection - connectionInfo.instanceCount = " << ConnectionInfo::instanceCount);

    return nullptr;
}

}
}
