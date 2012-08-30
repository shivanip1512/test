#pragma once

namespace Cti {
namespace Fdr {

struct IM_EX_FDRBASE ServerConnection
{
    virtual bool queueMessage(char *buf, unsigned len, int priority) = 0;
    virtual std::string getName() const = 0;
    virtual int getConnectionNumber() const = 0;
    virtual int getPortNumber() = 0;
};

}
}

inline std::ostream& operator<< (std::ostream& os, const Cti::Fdr::ServerConnection& conn)
{
    return os << "[connection " << conn.getName() << " #" << conn.getConnectionNumber() << "]";
}

