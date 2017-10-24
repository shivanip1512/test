#pragma once

#include "dlldefs.h"

#include "logger.h"

#include <atomic>
#include <memory>
#include <stack>

class SAConnection;

namespace Cti {
namespace Database {

class ConnectionManager
{
public:

    struct ConnectionInfo : Cti::Loggable
    {
        ConnectionInfo(const std::unique_ptr<SAConnection>& connection);

        const long clientVersion;
        const long serverVersion;
        const size_t instance;
        const CtiTime created;

        static std::atomic_size_t instanceCount;

        std::string toString() const override;
    };

    struct ConnectionDescriptor
    {
        ConnectionDescriptor(std::unique_ptr<SAConnection>&& conn);
            
        const std::unique_ptr<SAConnection> connection;
        const ConnectionInfo info;
    };

    struct ConnectionReleaser
    {
        void operator()(ConnectionDescriptor *desc);
    };

    using IdleConnection   = std::unique_ptr<ConnectionDescriptor>;
    using LoanedConnection = std::unique_ptr<ConnectionDescriptor, ConnectionReleaser>;

    static LoanedConnection borrowConnection();

private:

    static CtiCriticalSection idleConnectionMutex;
    static std::stack<IdleConnection> idleConnections;
};

}
}
