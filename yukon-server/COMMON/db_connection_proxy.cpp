#include "precompiled.h"

#include "db_connection_proxy.h"
#include "db_connection_manager.h"

#include "logger.h"

#include <SQLAPI.h>

namespace Cti {
namespace Database {

std::atomic_size_t ConnectionProxy::instanceCount {};

ConnectionProxy::ConnectionProxy()
    :   descriptor(ConnectionManager::borrowConnection()),
        instance(++instanceCount)
{
    CTILOG_DEBUG(dout, "Connection proxy " << instance << " created:" << descriptor->info);
}

ConnectionProxy::~ConnectionProxy() = default;

ConnectionProxy::operator bool() const
{
    return !! descriptor->connection;
}

ConnectionProxy::operator SAConnection*() const
{
    return descriptor->connection.get();
}

SAConnection* ConnectionProxy::operator->() const
{
    return static_cast<SAConnection*>(*this);
}

}
}
