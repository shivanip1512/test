#pragma once

#include "dlldefs.h"

#include "db_connection_manager.h"

#include <atomic>

class SAConnection;

namespace Cti {
namespace Database {

class IM_EX_CTIBASE ConnectionProxy
{
public:
    ConnectionProxy();
    ~ConnectionProxy();

    operator bool() const;
    operator SAConnection*() const;
    SAConnection* operator->() const;

    const size_t instance;

private:
    ConnectionManager::LoanedConnection descriptor;

    static std::atomic_size_t instanceCount;
};

}
}
