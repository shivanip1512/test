#include "precompiled.h"

#include "SimulatorLogger.h"
#include "ScopedLogger.h"

using namespace std;

namespace Cti {
namespace Simulator {

SimulatorLogger::SimulatorLogger(Logging::LoggerPtr &logger) :
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
    CTILOG_INFO(dout, prefix() << str);
}

void SimulatorLogger::log(const string &str, int i)
{
    CTILOG_INFO(dout, prefix() << str <<" ("<< i <<")");
}

void SimulatorLogger::log(const string &str, const bytes &l_bytes)
{
    CTILOG_INFO(dout, prefix() <<
            endl << formatIOBytes(l_bytes));
}

void SimulatorLogger::breadcrumbLog(const string &str)
{
    CTILOG_INFO(dout, breadcrumbPrefix() << str);
}

void SimulatorLogger::breadcrumbLog(const string &str, int i)
{
    CTILOG_INFO(dout, breadcrumbPrefix() << str <<" ("<< i <<")");
}

void SimulatorLogger::breadcrumbLog(const string &str, const bytes &l_bytes)
{
    CTILOG_INFO(dout, breadcrumbPrefix() <<
            endl << formatIOBytes(l_bytes));
}

string SimulatorLogger::formatIOBytes(const bytes &buf)
{
    std::ostringstream bytes;

    string result(buf.size() * 3, ' ');

    string::iterator itr = result.begin();

    for each( const unsigned char &byte in buf )
    {
        *(itr++) = toAsciiHex(byte / 16);
        *(itr++) = toAsciiHex(byte % 16);

        itr++;
    }

    return result;
}

string SimulatorLogger::breadcrumbPrefix()
{
    string prefix;

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
    string prefix;

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
