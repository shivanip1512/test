/*-----------------------------------------------------------------------------*
*
* File:   dnp_transport
*
* Class:  CtiDNPTransport
* Date:   5/7/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/02/10 23:23:58 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_TRANSPORT_H__
#define __DNP_TRANSPORT_H__
#pragma warning( disable : 4786)

#include "dllbase.h"
#include "dnp_datalink.h"
#include "xfer.h"

class CtiDNPTransport
{
private:
    CtiDNPDatalink  _datalink;
    unsigned char  *_outPayload,       *_inPayload;
    unsigned int    _outPayloadLen,     _inPayloadLen,
                    _outPayloadSent,    _inPayloadRecv,
                    _currentPacketLen,  _seq;
    unsigned short  _srcAddr, _dstAddr;

    enum TransportIOState
    {
        Uninitialized = 0,
        Output,
        Input,
        Complete,
        Failed
    } _ioState;

    bool _complete;

    enum
    {
        TransportHeaderLen     =   1,
        TransportMaxPayloadLen = 249
    };

    struct _transport_header_t
    {
        unsigned char seq   : 6;
        unsigned char first : 1;
        unsigned char final : 1;
    };

    struct _transport_packet_t
    {
        _transport_header_t header;
        unsigned char data[254];
    } _inPacket, _outPacket;

public:
    CtiDNPTransport();

    CtiDNPTransport(const CtiDNPTransport &aRef);

    virtual ~CtiDNPTransport();

    CtiDNPTransport &operator=(const CtiDNPTransport &aRef);

    void setAddresses(unsigned short dst, unsigned short src);
    void setOptions( int options );

    void resetLink( void );

    int initForOutput(unsigned char *buf, int len, unsigned short dstAddr, unsigned short srcAddr);
    int initForInput(unsigned char *buf);

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void );
    bool errorCondition( void );

    int  getInputSize( void );
};

#endif // #ifndef __DNP_TRANSPORT_H__
