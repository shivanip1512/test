#pragma once

#include "logger.h"

#include "types.h"

#include <boost/utility.hpp>

namespace Cti {
namespace Simulator {

class PortLogger : public boost::noncopyable
{
private:

    CtiLogger &_logger;
    std::string _uid;

    inline void prefix();
    static std::string formatIOBytes(const bytes &buf);

public:

    PortLogger(CtiLogger &logger, int portNumber);

    void log(const std::string &str);
    void log(const std::string &str, int i);
    void log(const std::string &str, const bytes &bytes);
};

}
}

