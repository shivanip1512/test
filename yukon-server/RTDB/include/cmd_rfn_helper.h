#pragma once

#include "cmd_rfn.h"

#include "error_helper.h"

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

