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
    CTILOG_TRACE(dout, "Connection proxy " << instance << " created:" << 
        (descriptor ? descriptor->toString() : "<empty>"));
}

ConnectionProxy::~ConnectionProxy() = default;

ConnectionProxy::operator bool() const
{
    return descriptor && descriptor->connection;
}

ConnectionProxy::operator SAConnection*() const
{
    return descriptor ? descriptor->connection.get() : nullptr;
}

SAConnection* ConnectionProxy::operator->() const
{
    return static_cast<SAConnection*>(*this);
}

}
}
