#include "precompiled.h"

#include "connectionHandle.h"

namespace Cti {

const ConnectionHandle ConnectionHandle::none;

ConnectionHandle::ConnectionHandle()
    : _connectionId(0)
{}

ConnectionHandle::ConnectionHandle(const long connectionId)
    : _connectionId(connectionId)
{}

void ConnectionHandle::reset()
{
    _connectionId = 0;
}

ConnectionHandle::operator bool() const
{
    return _connectionId;
}

bool ConnectionHandle::operator==(const ConnectionHandle &rhs) const
{
    return _connectionId == rhs._connectionId;
}

long ConnectionHandle::getConnectionId() const
{
    return _connectionId;
}

std::string ConnectionHandle::toString() const
{
    return "[ConnectionHandle id=" + std::to_string(_connectionId) + "]";
}

}
