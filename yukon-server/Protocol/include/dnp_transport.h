/*-----------------------------------------------------------------------------*
*
* File:   dnp_transport
*
* Namespace: CtiDNP
* Class:     Transport
* Date:   5/7/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/03/10 21:01:29 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_TRANSPORT_H__
#define __DNP_TRANSPORT_H__
#pragma warning( disable : 4786)


#include "dnp_datalink.h"
#include "xfer.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

class Transport
{
private:
    Datalink  _datalink;

    struct payload_t
    {
        unsigned char *data;
        unsigned int   length;

        union
        {
            unsigned int sent;
            unsigned int received;
        };
    };

    payload_t _payload_in,
              _payload_out;

    unsigned int    _current_packet_length,  _sequence;
    unsigned short  _source_address, _destination_address;

    enum IOState
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
        HeaderLen     =   1,
        MaxPayloadLen = 249
    };

    struct header_t
    {
        unsigned char seq   : 6;
        unsigned char first : 1;
        unsigned char final : 1;
    };

    struct packet_t
    {
        header_t header;
        unsigned char data[254];
    } _in_packet, _out_packet;

public:
    Transport();

    Transport( const Transport &aRef );

    virtual ~Transport();

    Transport &operator=( const Transport &aRef );

    void setAddresses( unsigned short dst, unsigned short src );
    void setOptions  ( int options );

    void resetLink( void );

    int initForOutput( unsigned char *buf, int len, unsigned short dstAddr, unsigned short srcAddr );
    int initForInput ( unsigned char *buf );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void );
    bool errorCondition( void );

    int  getInputSize( void );
};

}
}
}

#endif // #ifndef __DNP_TRANSPORT_H__
