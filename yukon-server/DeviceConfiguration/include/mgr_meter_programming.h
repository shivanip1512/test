#pragma once

#include "dlldefs.h"
#include "rfn_identifier.h"

#include <vector>

namespace Cti::MeterProgramming {

struct ProgramDescriptor;
    
class IM_EX_CONFIG MeterProgrammingManager
{
public:
    using Bytes = std::vector<unsigned char>;

    Bytes getProgram(const std::string guid);

    std::optional<ProgramDescriptor> describeAssignedProgram(const RfnIdentifier rfnIdentifier);

    bool isUploading(const RfnIdentifier rfnIdentifier, const std::string guid);

    double calculateMeterProgrammingProgress(RfnIdentifier rfnIdentifier, std::string guid, size_t size);

protected:

    struct RawProgram
    {
        Bytes program;
        Bytes password;
    };
    virtual RawProgram loadRawProgram(const std::string guid);

private:

    Bytes loadProgram(const std::string guid);
    size_t getProgramSize(const std::string guid);

    std::map<std::string, Bytes> _programs;
};

struct ProgramDescriptor
{
    std::string guid;
    std::size_t length;
};

extern IM_EX_CONFIG std::unique_ptr<MeterProgrammingManager> gMeterProgrammingManager;

}