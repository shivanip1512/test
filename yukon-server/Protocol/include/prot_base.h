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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2006/02/24 00:19:10 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_BASE_H__
#define __PROT_BASE_H__
#pragma warning( disable : 4786)

#include <rw/tpslist.h>

#include <vector>
#include <string>
#include <list>
using std::list;
using std::string;
using std::vector;

#include "dlldefs.h"
#include "msg_pdata.h"
#include "xfer.h"

namespace Cti       {
namespace Protocol  {

class IM_EX_PROT Interface
{
private:

protected:

    string _name;

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
    virtual int sendCommRequest( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
    virtual int recvCommResult ( INMESS   *InMessage,  list< OUTMESS* > &outList );

    virtual void getInboundPoints ( pointlist_t  &point_list );
    virtual void getInboundStrings( stringlist_t &string_list );

    //  porter-side (portfield, specificially) functions
    virtual int recvCommRequest( OUTMESS *OutMessage );

    virtual bool isTransactionComplete( void );

    virtual int generate( CtiXfer &xfer );
    virtual int decode  ( CtiXfer &xfer, int status );

    virtual int sendCommResult( INMESS  *InMessage  );

    virtual void setName( const string &name );
};

}
}

#endif // #ifndef __PROT_BASE_H__
