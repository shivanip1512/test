#pragma warning( disable : 4786)
#ifndef __DNP_DATALINK_PACKET_H__
#define __DNP_DATALINK_PACKET_H__

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/05 23:54:49 $
*
* Copyright (c) 2003 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma pack( push, 1 )

//  the primary and secondary control byte
struct dnp_datalink_packet_header_control_primary
{
    unsigned char functionCode  : 4;
    unsigned char fcv           : 1;
    unsigned char fcb           : 1;
    unsigned char primary       : 1;
    unsigned char direction     : 1;
};

struct dnp_datalink_packet_header_control_secondary
{
    unsigned char functionCode  : 4;
    unsigned char dfc           : 1;
    unsigned char zpad          : 1;
    unsigned char primary       : 1;
    unsigned char direction     : 1;
};


//  the formatted and raw structure of the header
struct dnp_datalink_packet_header_formatted
{
    unsigned char framing[2];
    unsigned char len;

    union _control
    {
        dnp_datalink_packet_header_control_primary   p;
        dnp_datalink_packet_header_control_secondary s;
    } control;

    unsigned short destination;
    unsigned short source;
    unsigned short crc;
};

/*
struct dnp_datalink_packet_header_raw
{
    unsigned char  buf[8];
    unsigned short crc;
};
*/

//  the header combines both formatted and raw for clearer access
union dnp_datalink_packet_header
{
    dnp_datalink_packet_header_formatted fmt;
    unsigned char                        raw[10];
};


//  in case we ever need to access it in a non-block-oriented fashion
union dnp_datalink_packet_data
{
//    unsigned char raw[282];        //  rounds up to 288 due to block-level access
    unsigned char blocks[16][18];  //    note the last block is only 14 instead of 18
};


//  the packet combines all of the previous into one big blob
struct dnp_datalink_packet
{
    dnp_datalink_packet_header header;
    dnp_datalink_packet_data   data;
};

#pragma pack( pop )

#endif // #ifndef __DNP_DATALINK_PACKET_H__
