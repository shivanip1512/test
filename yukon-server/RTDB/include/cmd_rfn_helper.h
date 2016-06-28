#pragma once

#include "cmd_helper.h"
#include "cmd_rfn.h"
#include <string>
#include <sstream>

#include <boost/scoped_ptr.hpp>

namespace Cti {
namespace Devices {
namespace Commands {


boost::optional<std::string> findDescriptionForAscAsq( const unsigned char asc, const unsigned char asq );

unsigned getValueFromBytes_bEndian(const DeviceCommand::Bytes &data, unsigned offset, unsigned len);

template <unsigned byteNbr>
void insertValue_bEndian(DeviceCommand::Bytes &data, unsigned val);

}
}
}

