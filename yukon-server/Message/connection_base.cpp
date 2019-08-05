#include "precompiled.h"

#include "mutex.h"
#include "guard.h"
#include "connection_base.h"

namespace Cti {
namespace Messaging {

namespace { // anonymous

std::set<BaseConnection*> g_connections;

CtiMutex g_mux;

} // anonymous

std::atomic_bool BaseConnection::_stopping { false };

BaseConnection::BaseConnection()
{
    CTILOCKGUARD(CtiMutex, guard, g_mux);

    g_connections.insert(this);
}

BaseConnection::~BaseConnection()
{
    CTILOG_DEBUG( dout, "BaseConnection::~BaseConnection()" );

    CTILOCKGUARD(CtiMutex, guard, g_mux);

    g_connections.erase(this);
}

void BaseConnection::close()
{
}

void BaseConnection::closeAll()
{
    CTILOCKGUARD(CtiMutex, guard, g_mux);

    for( auto connection : g_connections )
    {
        try
        {
            connection->close();
        }
        catch(...)
        {

        }
    }
}

void BaseConnection::stopReconnects()
{
    _stopping = true;
}

}
}
