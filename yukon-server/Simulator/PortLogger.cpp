#include "precompiled.h"

#include "PortLogger.h"

#include <boost/crc.hpp>

using namespace std;

namespace Cti {
namespace Simulator {

PortLogger::PortLogger(Logging::LoggerPtr &logger, int portNumber) :
    SimulatorLogger(logger)
{
    ostringstream uid_generator;

    boost::crc_16_type crc16;

    crc16.process_block(&portNumber, &portNumber + 1);

    uid_generator << "[" << setw(16 / 4) << hex << crc16.checksum() << " "  << dec << setw(5) << portNumber << "] ";

    _uid = uid_generator.str();
}

string PortLogger::prefix()
{
    return _uid + SimulatorLogger::prefix();
}

string PortLogger::breadcrumbPrefix()
{
    return prefix();
}

}
}

