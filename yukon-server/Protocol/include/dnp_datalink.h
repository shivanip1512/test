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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/06/24 20:00:42 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/tpslist.h>

#include "dsm2.h"
#include "xfer.h"

class CtiDNPDatalink
{
protected:
    unsigned short computeCRC(unsigned char *buf, int len);

    enum
    {
        DNPDatalinkHeaderLen  = 10,
        DNPDatalinkRetryCount =  3
    };

private:
#pragma pack( push, 1 )
    struct _dnp_datalink_header
    {
        unsigned char framing[2];
        unsigned char len;

        union _control
        {
            union
            {
                struct _primary
                {
                    unsigned char functionCode  : 4;
                    unsigned char fcv           : 1;
                    unsigned char fcb           : 1;
                    unsigned char primary       : 1;
                    unsigned char direction     : 1;
                } p;
                struct _secondary
                {
                    unsigned char functionCode  : 4;
                    unsigned char dfc           : 1;
                    unsigned char zpad          : 1;
                    unsigned char primary       : 1;
                    unsigned char direction     : 1;
                } s;
            };


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
    } _outPacket, _inPacket;

    enum DatalinkIOState
    {
        Uninitialized = 0,
        Output,
        Input,
        Complete,
        Failed
    } _ioState;

    unsigned long _outLen, _outSent, _inRecv, _inExpected, _inActual;  //  would be ints, but i have to use inLen with the trx InCountExpected
    int  _errorCount;
    bool _fcbExpected;

#pragma pack( pop )

public:
    enum ControlFunction;
    enum DatalinkError;

    CtiDNPDatalink();

    CtiDNPDatalink(const CtiDNPDatalink &aRef);

    virtual ~CtiDNPDatalink();

    CtiDNPDatalink &operator=(const CtiDNPDatalink &aRef);

    void reset( void );

    int setToOutput ( unsigned char *buf, unsigned int len, short dstAddr, short srcAddr );
    int setToInput  ( void );

    int calcPacketLength( int headerLen );

    int getOutPayloadLength( void );
    int getInPayload( unsigned char *buf );
    int getInLength ( void );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void );
    bool errorCondition( void );

    DatalinkError validateInPacket( void );
    bool          areInPacketCRCsValid( void );

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
        BadFraming,
        BadCRC,
        BadAddress,
        BadLength,
        UnknownMessage
    };
};

#endif // #ifndef __DNP_DATALINK_H__
