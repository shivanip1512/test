#include "precompiled.h"

#include "ScopedLogger.h"

namespace Cti {
namespace Simulator {

ScopedLogger ScopedLogger::getNewScope(const std::string &str)
{
    return ScopedLogger(str, *this);
}

void ScopedLogger::log(const std::string &str)
{
    _myLogger.log(str);
}

void ScopedLogger::log(const std::string &str, int i)
{
    _myLogger.log(str, i);
}

void ScopedLogger::log(const std::string &str, const bytes &l_bytes)
{
    _myLogger.log(str, l_bytes);
}

void ScopedLogger::breadcrumbLog(const std::string &str)
{
    _myLogger.breadcrumbLog(str);
}

void ScopedLogger::breadcrumbLog(const std::string &str, int i)
{
    _myLogger.breadcrumbLog(str, i);
}

void ScopedLogger::breadcrumbLog(const std::string &str, const bytes &l_bytes)
{
    _myLogger.breadcrumbLog(str, l_bytes);
}

ScopedLogger::~ScopedLogger()
{
    untag();
}

void ScopedLogger::tag(const std::string &str)
{
    _myLogger.tag(str);
}

void ScopedLogger::untag()
{
    _myLogger.untag();
}

ScopedLogger::ScopedLogger(const std::string &str, Logger& logger) :
    _myLogger(logger)
{
    _myLogger.tag(str);
}

}
}
