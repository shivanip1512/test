/*-----------------------------------------------------------------------------*
*
* File:   prot_base
*
* Class:  CtiProtocolBase
* Date:   2002-oct-01
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/03/18 19:54:09 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_BASE_H__
#define __PROT_BASE_H__
#pragma warning( disable : 4786)

#include <rw/tpslist.h>

#include "dlldefs.h"
#include "msg_pdata.h"
#include "xfer.h"

class IM_EX_PROT CtiProtocolBase
{
private:

protected:

    virtual int commOut( OUTMESS *&OutMessage );
    virtual int commIn ( INMESS   *InMessage  );

public:

    CtiProtocolBase();
    CtiProtocolBase(const CtiProtocolBase &aRef);

    virtual ~CtiProtocolBase();

    CtiProtocolBase &operator=(const CtiProtocolBase &aRef);

    //  client-side (Scanner, PIL) functions
    virtual int sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    virtual int recvCommResult ( INMESS   *InMessage,  RWTPtrSlist< OUTMESS > &outList );

    virtual bool hasInboundPoints( void );
    virtual void getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList );

    //  porter-side (portfield, specificially) functions
    virtual int recvCommRequest( OUTMESS *OutMessage );

    virtual bool isTransactionComplete( void );

    virtual int generate( CtiXfer &xfer );
    virtual int decode  ( CtiXfer &xfer, int status );

    virtual int sendCommResult ( INMESS  *InMessage  );
};

#endif // #ifndef __PROT_BASE_H__
