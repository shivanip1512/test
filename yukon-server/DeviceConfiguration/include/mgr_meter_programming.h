#pragma once

#include "dlldefs.h"
#include "rfn_identifier.h"

#include "error_helper.h"

#include <vector>

namespace Cti::MeterProgramming {

struct ProgramDescriptor;
    
class IM_EX_CONFIG MeterProgrammingManager
{
public:
    using Bytes = std::vector<unsigned char>;

    ErrorOr<Bytes> getProgram(const std::string guid);

    ErrorOr<ProgramDescriptor> describeAssignedProgram(const RfnIdentifier rfnIdentifier);

    bool isAssigned(const RfnIdentifier rfnIdentifier, const std::string guid);

    double calculateMeterProgrammingProgress(RfnIdentifier rfnIdentifier, std::string guid, size_t size);

    virtual ~MeterProgrammingManager() = default;

protected:

    struct RawProgram
    {
        Bytes program;
        Bytes password;
    };
    virtual ErrorOr<RawProgram> loadRawProgram(const std::string guid);

private:

    ErrorOr<Bytes> loadProgram(const std::string guid);
    ErrorOr<size_t> getProgramSize(const std::string guid);
    
    Bytes convertRawProgram(const RawProgram& raw, const std::string guid);

    std::map<std::string, Bytes> _programs;
};

struct ProgramDescriptor
{
    std::string guid;
    std::size_t length;
};

extern IM_EX_CONFIG std::unique_ptr<MeterProgrammingManager> gMeterProgrammingManager;

}