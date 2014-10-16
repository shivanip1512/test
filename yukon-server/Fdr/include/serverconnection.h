#pragma once

#include "loggable.h"
#include "string_util.h"

// forward declarations
namespace Cti {
namespace Fdr {
struct ServerConnection;
}}

std::ostream& operator<< (std::ostream&, const Cti::Fdr::ServerConnection&);

namespace Cti {
namespace Fdr {

struct ServerConnection : public Loggable
{
    virtual bool queueMessage(char *buf, unsigned len, int priority) = 0;
    virtual std::string getName() const = 0;
    virtual int getConnectionNumber() const = 0;
    virtual int getPortNumber() = 0;

    std::string toString() const
    {
        std::ostringstream oss;
        oss << *this;
        return oss.str();
    }
};

}
}

inline std::ostream& operator<< (std::ostream& os, const Cti::Fdr::ServerConnection& conn)
{
    return os << "[connection " << conn.getName() << " #" << conn.getConnectionNumber() << "]";
}

