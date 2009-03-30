#include "yukon.h"

#include "portlogger.h"

#include <boost/crc.hpp>

using namespace std;

namespace Cti {
namespace Simulator {

PortLogger::PortLogger(CtiLogger &logger, int portNumber) :
    _logger(logger)
{
    ostringstream uid_generator;

    boost::crc_16_type crc16;

    crc16.process_block(&portNumber, &portNumber + 1);

    uid_generator << "[" << setw(16 / 4) << hex << crc16.checksum() << " "  << dec << setw(5) << portNumber << "] ";

    _uid = uid_generator.str();
}

void PortLogger::prefix()
{
    _logger << _uid << CtiTime() << " ";
}

void PortLogger::log(const string &str)
{
    CtiLockGuard<CtiLogger> guard(_logger);

    prefix();

    _logger << str << endl;

/*
    size_t begin = 0, end = 0;

    while( end != string::npos )
    {
        //  send lines separately
        end = str.find_first_of("\n", begin);

        prefix();

        _logger << str.substr(begin, end) << endl;

        begin = (end != string::npos)?(end + 1):(end);
    }
*/
}

void PortLogger::log(const string &str, int i)
{
    CtiLockGuard<CtiLogger> guard(_logger);

    prefix();

    _logger << str << " (" << i << ")" << endl;
}

void PortLogger::log(const string &str, const bytes &bytes)
{
    CtiLockGuard<CtiLogger> guard(_logger);

    prefix();

    _logger << str << endl;
    _logger << formatIOBytes(bytes) << endl;
}

std::string PortLogger::formatIOBytes(const bytes &buf)
{
    ostrstream bytes;

    bytes::const_iterator buf_itr = buf.begin();

    while( buf_itr != buf.end() )
    {
        bytes << setfill('0') << setw(2) << hex << static_cast<int>(*buf_itr++) << " ";
    }

    bytes.freeze();

    return string(bytes.str(), bytes.pcount());
}

}
}

