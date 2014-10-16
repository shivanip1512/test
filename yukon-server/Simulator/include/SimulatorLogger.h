#pragma once

#include "logger.h"
#include "types.h"

namespace Cti {
namespace Simulator {

class ScopedLogger;

class Logger
{
public:

    virtual void log(const std::string &str) = 0;
    virtual void log(const std::string &str, int i) = 0;
    virtual void log(const std::string &str, const bytes &l_bytes) = 0;

    virtual void breadcrumbLog(const std::string &str) = 0;                      
    virtual void breadcrumbLog(const std::string &str, int i) = 0;               
    virtual void breadcrumbLog(const std::string &str, const bytes &l_bytes) = 0;

    virtual ScopedLogger getNewScope(const std::string &str) = 0;

protected:
    
    friend class ScopedLogger;

    virtual void tag(const std::string &str) = 0;
    virtual void untag() = 0;
};

class SimulatorLogger : public Logger
{
public:

    SimulatorLogger(Logging::LoggerPtr &logger);
    ~SimulatorLogger();

    virtual void log(const std::string &str);
    virtual void log(const std::string &str, int i);
    virtual void log(const std::string &str, const bytes &l_bytes);

    virtual void breadcrumbLog(const std::string &str);                      
    virtual void breadcrumbLog(const std::string &str, int i);               
    virtual void breadcrumbLog(const std::string &str, const bytes &l_bytes);

    virtual ScopedLogger getNewScope(const std::string &str);

protected:

    void tag(const std::string &str);
    void untag();

    virtual std::string prefix();
    virtual std::string breadcrumbPrefix();

    static std::string formatIOBytes(const bytes &buf);

    std::vector<std::string> _tags;
    int _scope;

private:

    Logging::LoggerPtr &_logger;
};

}
}
