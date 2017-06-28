#pragma once

#include <atomic>

namespace Cti {
namespace Messaging {

class IM_EX_MSG BaseConnection
{
protected:

    BaseConnection();

    static std::atomic_bool _stopping;

public:

    virtual ~BaseConnection();

    virtual void close() = 0;

    static void closeAll();

    static void stopReconnects();
};


struct AutoCloseAllConnections
{
    ~AutoCloseAllConnections()
    {
        BaseConnection::closeAll();
    }
};


}
}
