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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/03/17 05:17:40 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_BASE_H__
#define __PROT_BASE_H__
#pragma warning( disable : 4786)

#include <rw/tpslist.h>

#include <vector>
#include <string>
using namespace std;

#include "dlldefs.h"
#include "msg_pdata.h"
#include "xfer.h"

namespace Cti       {
namespace Protocol  {

class IM_EX_PROT Interface
{
private:

protected:

    virtual int commOut( OUTMESS *&OutMessage );
    virtual int commIn ( INMESS   *InMessage  );

public:

    typedef vector< CtiPointDataMsg * > pointlist_t;
    typedef vector< string * >          stringlist_t;

    Interface();
    Interface(const Interface &aRef);

    virtual ~Interface();

    Interface &operator=(const Interface &aRef);

    //  client-side (Scanner, PIL) functions
    virtual int sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    virtual int recvCommResult ( INMESS   *InMessage,  RWTPtrSlist< OUTMESS > &outList );

    virtual void getInboundPoints ( pointlist_t  &point_list );
    virtual void getInboundStrings( stringlist_t &string_list );

    //  porter-side (portfield, specificially) functions
    virtual int recvCommRequest( OUTMESS *OutMessage );

    virtual bool isTransactionComplete( void );

    virtual int generate( CtiXfer &xfer );
    virtual int decode  ( CtiXfer &xfer, int status );

    virtual int sendCommResult( INMESS  *InMessage  );
};

}
}

#endif // #ifndef __PROT_BASE_H__
