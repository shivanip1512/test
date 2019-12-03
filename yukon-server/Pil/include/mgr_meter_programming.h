#pragma once

#include "dlldefs.h"
#include "rfn_identifier.h"

#include <vector>

class CtiDeviceManager;

namespace Cti::Pil {

class IM_EX_CTIPIL MeterProgrammingManager
{
public:
    MeterProgrammingManager(CtiDeviceManager& deviceManager);

    using Bytes = std::vector<unsigned char>;

    Bytes getProgram(const std::string guid);

    bool isUploading(const RfnIdentifier rfnIdentifier, const std::string guid);

    void updateMeterProgrammingStatus(RfnIdentifier rfnIdentifier, std::string guid, size_t size, size_t totalSize);

private:

    std::map<std::string, Bytes> _programs;
    CtiDeviceManager& _deviceManager;
};

}