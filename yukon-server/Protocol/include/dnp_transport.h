#pragma warning( disable : 4786)

#ifndef __DNP_TRANSPORT_H__
#define __DNP_TRANSPORT_H__

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/06/11 21:14:04 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_datalink.h"
#include "xfer.h"

class CtiDNPTransport
{
private:
    CtiDNPDatalink _datalink;
    unsigned char *_outAppLayer,     *_inAppLayer;
    unsigned int   _outAppLayerLen,   _inAppLayerLen,
                   _outAppLayerSent,  _inAppLayerRecv,
                   _seq;
    unsigned short _srcAddr, _dstAddr;

    enum TransportIOState
    {
        Uninitialized = 0,
        Output,
        Input,
        Failed,
        Complete
    } _ioState;

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

    void reset( void );
    int initForOutput(unsigned char *buf, int len, unsigned short dstAddr, unsigned short srcAddr);
    int initForInput(unsigned char *buf);

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool sendComplete( void );
    bool recvComplete( void );

    int  getInputSize( void );
};

#endif // #ifndef __DNP_TRANSPORT_H__
