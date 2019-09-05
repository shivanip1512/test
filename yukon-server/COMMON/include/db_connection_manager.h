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

    struct ConnectionInfo
    {
        ConnectionInfo(const std::unique_ptr<SAConnection>& connection);

        const long clientVersion;
        const long serverVersion;
        const size_t instance;
        const CtiTime created;

        static std::atomic_size_t instanceCount;
    };

    struct ConnectionDescriptor : Cti::Loggable
    {
        ConnectionDescriptor(std::unique_ptr<SAConnection>&& conn);
            
        const std::unique_ptr<SAConnection> connection;
        const ConnectionInfo info;
        CtiTime lastBorrowed { CtiTime::not_a_time };
        CtiTime lastReturned { CtiTime::not_a_time };

        std::string toString() const override;
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
