#pragma once

#include "dlldefs.h"

namespace Cti {
namespace Protocols {
namespace DNP {
namespace DatalinkPacket {

    enum
    {
        FramingLength       =   2,
        HeaderLength        =  10,
        HeaderCountedLength =   5,
        BlockCountMax       =  16,
        BlockLength         =  16,
        CRCLength           =   2,
        DataLengthMax       = 282,
        PayloadLengthMax    = 250,
    };

    IM_EX_PROT unsigned calcPacketLength( unsigned headerLen );

#pragma pack( push, 1 )

    //  the primary and secondary control byte
    struct dlp_control_primary
    {
        unsigned char functionCode  : 4;
        unsigned char fcv           : 1;
        unsigned char fcb           : 1;
        unsigned char primary       : 1;
        unsigned char direction     : 1;
    };

    struct dlp_control_secondary
    {
        unsigned char functionCode  : 4;
        unsigned char dfc           : 1;
        unsigned char zpad          : 1;
        unsigned char primary       : 1;
        unsigned char direction     : 1;
    };


    //  the formatted and raw structure of the header
    struct dlp_header_formatted
    {
        unsigned char framing[FramingLength];
        unsigned char len;

        union _control
        {
            dlp_control_primary   p;
            dlp_control_secondary s;
        } control;

        unsigned short destination;
        unsigned short source;
        unsigned short crc;
    };


    //  the header combines both formatted and raw for clearer access
    union dlp_header
    {
        dlp_header_formatted fmt;
        unsigned char        raw[HeaderLength];
    };


    //  in case we ever need to access it in a non-block-oriented fashion
    union dlp_data
    {
        //  this union runds up to 288 (16*18) because of the block array;
        //    the total usable size is 282, so the last block is only 14 instead of 18

        //  unsigned char raw[DataLength];
        unsigned char blocks[BlockCountMax][BlockLength + CRCLength];
    };

    //  the packet combines all of the previous into one big blob
    struct dl_packet
    {
        dlp_header header;
        dlp_data   data;
    };

#pragma pack( pop )

};
};
};
};
