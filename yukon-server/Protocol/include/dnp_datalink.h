#pragma warning( disable : 4786)
#ifndef __DNP_DATALINK_H__
#define __DNP_DATALINK_H__

/*-----------------------------------------------------------------------------*
*
* File:   dnp_datalink
*
* Class:  CtiDNPDatalink
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/30 15:11:26 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/tpslist.h>

#include "dsm2.h"

class CtiDNPDatalink
{
protected:
    unsigned short computeCRC(unsigned char *buf, int len);

private:
    #pragma pack( push, 1 )
    struct _dnp_datalink_header
    {
        unsigned char framing[2];
        unsigned char len;

        union
        {
            unsigned char functionCode  : 4;

            union
            {
                struct
                {
                    unsigned char fcv       : 1;
                    unsigned char fcb       : 1;
                    unsigned char primary   : 1;
                } p;
                struct
                {
                    unsigned char dfc       : 1;
                    unsigned char zpad      : 2;
                } s;
            };

            unsigned char direction     : 1;

        } control;

        unsigned short destination;
        unsigned short source;
        unsigned short crc;
    };

    struct _dnp_datalink_data
    {
        union
        {
            unsigned char raw[282];        //  rounds up to 288 due to block-level access
            unsigned char blocks[16][18];  //    note the last block is only 14 instead of 18
        };
    };

    struct _dnp_datalink_message
    {
        struct _dnp_datalink_header header;
        struct _dnp_datalink_data   data;
    } _packet;

    int _fcbExpected;
    #pragma pack( pop )

public:
    enum ControlFunction;
    enum DatalinkError;

    CtiDNPDatalink();

    CtiDNPDatalink(const CtiDNPDatalink &aRef);

    virtual ~CtiDNPDatalink();

    CtiDNPDatalink &operator=(const CtiDNPDatalink &aRef);

    unsigned short getSourceAddress(void);
    void           setSourceAddress(unsigned short addr);

    unsigned short getDestinationAddress(void);
    void           setDestinationAddress(unsigned short addr);

    unsigned short getLength(void);

    unsigned char *getMessage(void);
    int            setMessage(unsigned char *buf, unsigned char len);

    int commOut( RWTPtrSlist< OUTMESS > &outList, OUTMESS *OutMessage );
    int commIn ( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList );

    DatalinkError  isValid(void);
    int            areCRCsValid(void);

    enum ControlFunction
    {
        Primary_ResetLink           = 0x0,
        Primary_ResetProcess        = 0x1,
        Primary_TestLink            = 0x2,
        Primary_UserDataConfirmed   = 0x3,
        Primary_UserDataUnconfirmed = 0x4,
        Primary_LinkStatus          = 0x9,

        Secondary_ACK               = 0x0,
        Secondary_NACK              = 0x1,
        Secondary_LinkStatus        = 0xb
    };

    enum DatalinkError
    {
        NoError    = 0,
        BadFraming = 1,
        BadCRC,
        BadAddress,
        BadLength,
        UnknownMessage
    };
};

#endif // #ifndef __DNP_DATALINK_H__
