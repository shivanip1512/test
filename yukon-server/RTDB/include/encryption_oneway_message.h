#pragma once

#include "dlldefs.h"
#include "CtiTime.h"

#include <cstddef>



IM_EX_DEVDB void GetNextSequenceValues( const CtiTime & timeNow, CtiTime * seqTime, unsigned * seqCounter );


class IM_EX_DEVDB OneWayMsgEncryption
{
public:

    enum OutputFormat
    {
        Binary,
        Ascii
    };

    std::size_t encryptMessage( const CtiTime       & timeNow,
                                char                * inMessage,
                                const std::size_t     msgLength,
                                char                * outMessage,
                                const OutputFormat    format );

protected:

    std::size_t convertToAscii( const unsigned char * fromBuffer,
                                const std::size_t     byteCount,
                                unsigned char       * toBuffer );


};

