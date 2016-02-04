#pragma once

#include "dlldefs.h"
#include "loggable.h"

namespace Cti {

class IM_EX_CTIBASE ConnectionHandle : public Loggable
{
public:
    ConnectionHandle();
    explicit ConnectionHandle(const long connectionId);

    void reset();

    bool operator==(const ConnectionHandle &rhs) const;

    explicit operator bool() const;

    long getConnectionId() const;

    std::string toString() const override;

    static const ConnectionHandle none;

private:
    long _connectionId;
};

}
