#include "precompiled.h"

#include "SimulatorLogger.h"
#include "ScopedLogger.h"

using namespace std;

namespace Cti {
namespace Simulator {

SimulatorLogger::SimulatorLogger(CtiLogger &logger) :
    _logger(logger),
    _scope(0)
{
}

void SimulatorLogger::tag(const string &str)
{
    if( !str.empty() )
    {
        _tags.push_back(str);
        _scope++;
    }
}

void SimulatorLogger::untag()
{
    if( !_tags.empty() )
    {
        _scope--;
        _tags.pop_back();
    }
}

SimulatorLogger::~SimulatorLogger()
{
    untag();
}

ScopedLogger SimulatorLogger::getNewScope(const string &str)
{
    return ScopedLogger(str, *this);
}

void SimulatorLogger::log(const string &str)
{
    CtiLockGuard<CtiLogger> guard(_logger);

    _logger << prefix() << str << endl;
}

void SimulatorLogger::log(const string &str, int i)
{
    log(string(str + " (" + CtiNumStr(i) + ")\n"));
}

void SimulatorLogger::log(const string &str, const bytes &l_bytes)
{
    log(string(str + "\n" + formatIOBytes(l_bytes) + "\n"));
}

void SimulatorLogger::breadcrumbLog(const string &str)
{
    CtiLockGuard<CtiLogger> guard(_logger);

    _logger << breadcrumbPrefix() << str << endl;
}

void SimulatorLogger::breadcrumbLog(const string &str, int i)
{
    breadcrumbLog(string(str + " (" + CtiNumStr(i) + ")\n"));
}

void SimulatorLogger::breadcrumbLog(const string &str, const bytes &l_bytes)
{
    breadcrumbLog(string(str + "\n" + formatIOBytes(l_bytes) + "\n"));
}

string SimulatorLogger::formatIOBytes(const bytes &buf)
{
    std::ostrstream bytes;

    bytes::const_iterator buf_itr = buf.begin();

    while( buf_itr != buf.end() )
    {
        bytes << setfill('0') << setw(2) << hex << static_cast<int>(*buf_itr++) << " ";
    }

    bytes.freeze();

    return string(bytes.str(), bytes.pcount());
}

string SimulatorLogger::breadcrumbPrefix()
{
    string prefix = CtiTime::now().asString() + " ";

    if( !_tags.empty() )
    {
        string tags = "[ ";
        for each( const string &str in _tags )
        {
            tags += str + " ";
        }
        tags += "] ";

        prefix += tags;
    }

    return prefix;
}

string SimulatorLogger::prefix()
{
    string prefix = CtiTime::now().asString() + " ";

    if( !_tags.empty() )
    {
        string tags = "[ ";
        for( int i = 0; i < _scope; i++ )
        {
           tags += _tags[i] + " ";
        }
        tags += "] ";

        prefix += tags;
    }

    return prefix;
}

}
}
