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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/30 15:11:26 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_datalink.h"
#include "xfer.h"

class CtiDNPTransport
{
protected:
    CtiDNPDatalink _datalink;
    unsigned char *_appLayer;
    int            _appLayerLen;

private:
    struct _transport_header_t
    {
        unsigned char sequence  : 6;
        unsigned char first     : 1;
        unsigned char final     : 1;
    };

public:
    CtiDNPTransport();

    CtiDNPTransport(const CtiDNPTransport &aRef);

    virtual ~CtiDNPTransport();

    CtiDNPTransport &operator=(const CtiDNPTransport &aRef);

    void reset( void );
    int initForOutput(unsigned char *buf, int len);
    int initForInput(void);

    int commOut( OUTMESS *OutMessage, RWTPtrSlist< OUTMESS > &outList );
    int commIn ( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList );

    bool sendComplete( void );
    bool inputComplete( void );

    int bufferSize( void );
    void retrieveBuffer( unsigned char *buf );
};

#endif // #ifndef __DNP_TRANSPORT_H__
