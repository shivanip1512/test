#pragma once

#include "SimulatorLogger.h"

namespace Cti {
namespace Simulator {

class PortLogger : public SimulatorLogger
{
public:

    PortLogger(CtiLogger &logger, int portNumber);

protected:

    std::string _uid;

    std::string prefix();
    std::string breadcrumbPrefix();
};

}
}
