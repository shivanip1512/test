#pragma once

namespace Cti::MeterProgramming {

enum class GuidPrefixes : char {
    YukonProgrammed = 'R',
    OpticallyProgrammed = 'P',
    UnknownProgram = 'N',
    Unprogrammed = 'U',
    InsufficientMeterHardwareFirmware = 'X'
};

}