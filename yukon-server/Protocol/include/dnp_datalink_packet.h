/*-----------------------------------------------------------------------------*
*
* File:   dnp_datalink_packet
*
* Date:   1/23/2003
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/03/10 21:06:34 $
*
* Copyright (c) 2003 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_DATALINK_PACKET_H__
#define __DNP_DATALINK_PACKET_H__
#pragma warning( disable : 4786)

namespace Cti       {
namespace Protocol  {
namespace DNP       {

#pragma pack( push, 1 )

namespace DatalinkPacket
{
    enum
    {
        FramingLength =   2,
        HeaderLength  =  10,
        DataLength    = 282,
        BlockCount    =  16,
        BlockLength   =  16,
        CRCLength     =   2
    };

    //  the primary and secondary control byte
    struct header_control_primary
    {
        unsigned char functionCode  : 4;
        unsigned char fcv           : 1;
        unsigned char fcb           : 1;
        unsigned char primary       : 1;
        unsigned char direction     : 1;
    };

    struct header_control_secondary
    {
        unsigned char functionCode  : 4;
        unsigned char dfc           : 1;
        unsigned char zpad          : 1;
        unsigned char primary       : 1;
        unsigned char direction     : 1;
    };


    //  the formatted and raw structure of the header
    struct header_formatted
    {
        unsigned char framing[FramingLength];
        unsigned char len;

        union _control
        {
            header_control_primary   p;
            header_control_secondary s;
        } control;

        unsigned short destination;
        unsigned short source;
        unsigned short crc;
    };

    //  the header combines both formatted and raw for clearer access
    union header
    {
        header_formatted fmt;
        unsigned char    raw[HeaderLength];
    };


    //  in case we ever need to access it in a non-block-oriented fashion
    union data
    {
        //  this union runds up to 288 (16*18) because of the block array;
        //    the total usable size is 282, so the last block is only 14 instead of 18

        //  unsigned char raw[DataLength];
        unsigned char blocks[BlockCount][BlockLength + CRCLength];
    };
}

//  the packet combines all of the previous into one big blob
struct datalink_packet
{
    DatalinkPacket::header header;
    DatalinkPacket::data   data;
};

#pragma pack( pop )

}
}
}

#endif // #ifndef __DNP_DATALINK_PACKET_H__
