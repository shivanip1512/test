#pragma once

#include "SimulatorLogger.h"

namespace Cti {
namespace Simulator {

class ScopedLogger : public Logger
{
public:

    virtual ScopedLogger getNewScope(const std::string &str);

    virtual void log(const std::string &str);
    virtual void log(const std::string &str, int i);
    virtual void log(const std::string &str, const bytes &l_bytes);

    virtual void breadcrumbLog(const std::string &str);                      
    virtual void breadcrumbLog(const std::string &str, int i);               
    virtual void breadcrumbLog(const std::string &str, const bytes &l_bytes);

    ~ScopedLogger();

    friend class SimulatorLogger;

protected:

    Logger& _myLogger;

    void tag(const std::string &str);
    void untag();

    ScopedLogger(const std::string &str, Logger& logger);
};

}
}
