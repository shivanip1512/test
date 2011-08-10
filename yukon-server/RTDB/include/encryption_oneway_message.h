#pragma once

#include "dlldefs.h"
#include "CtiTime.h"

#include <cstddef>



IM_EX_DEVDB void GetNextSequenceValues( const CtiTime & timeNow, CtiTime * seqTime, unsigned * seqCounter );


struct IM_EX_DEVDB OneWayMsgEncryption
{
    std::size_t encryptMessage( const CtiTime       & timeNow,
                                char                * inMessage,
                                const std::size_t     msgLength,
                                char                * outMessage );
};

