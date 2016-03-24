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

BaseConnection::BaseConnection()
{
    CtiLockGuard<CtiMutex> guard(g_mux);

    g_connections.insert(this);
}

BaseConnection::~BaseConnection()
{
    CTILOG_DEBUG( dout, "BaseConnection::~BaseConnection()" );

    CtiLockGuard<CtiMutex> guard(g_mux);

    g_connections.erase(this);
}

void BaseConnection::close()
{
}

void BaseConnection::closeAll()
{
    CtiLockGuard<CtiMutex> guard(g_mux);

    typedef std::set<BaseConnection*>::iterator iterator_t;

    for(iterator_t itr=g_connections.begin(); itr!=g_connections.end(); ++itr)
    {
        try
        {
            (*itr)->close();
        }
        catch(...)
        {

        }
    }
}

}
}
