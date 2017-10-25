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

std::string ConnectionManager::ConnectionInfo::toString() const
{
    return Cti::FormattedList::of(
        "Connection instance",  instance,
        "Creation time",        created,
        "Client major version", clientVersion >> 16,
        "Client minor version", clientVersion & 0xffff,
        "Server major version", serverVersion >> 16,
        "Server minor version", serverVersion & 0xffff);
}

std::atomic_size_t ConnectionManager::ConnectionInfo::instanceCount{};

ConnectionManager::ConnectionDescriptor::ConnectionDescriptor( std::unique_ptr<SAConnection>&& conn )
    :   connection { std::move(conn) },
        info { connection }
{ }

void ConnectionManager::ConnectionReleaser::operator()(ConnectionDescriptor *desc)
{
    CtiLockGuard<CtiCriticalSection> guard(idleConnectionMutex);

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
                        CTILOG_DEBUG(dout, "Existing database connection retrieved:" << descriptor->info);

                        return LoanedConnection { descriptor.release() };
                    }
                    else
                    {
                        CTILOG_INFO(dout, "Existing database connection was not alive, releasing" << descriptor->info);
                    }
                }
                else
                {
                    CTILOG_WARN(dout, "Existing database connection was null, releasing" << descriptor->info);
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

        CTILOG_INFO(dout, "New database connection created:" << descriptor->info);

        return descriptor;
    }

    CTILOG_ERROR(dout, "Could not create a new database connection - connectionInfo.instanceCount = " << ConnectionInfo::instanceCount);

    return nullptr;
}

}
}
