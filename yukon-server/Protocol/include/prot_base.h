#pragma once

#include <vector>
#include <string>
#include <list>

#include "msg_pdata.h"
#include "dlldefs.h"
#include "xfer.h"

namespace Cti {
namespace Protocol {

class IM_EX_PROT Interface : boost::noncopyable
{
protected:

    std::string _name;

public:

    typedef std::vector< CtiPointDataMsg * > pointlist_t;
    typedef std::vector< std::string * >     stringlist_t;

    virtual ~Interface() { }

    //  client-side (Scanner, PIL) functions
    virtual int sendCommRequest( OUTMESS *&OutMessage, std::list< OUTMESS* > &outList );
    virtual int recvCommResult ( INMESS   *InMessage,  std::list< OUTMESS* > &outList );

    virtual void getInboundPoints ( pointlist_t  &point_list );
    virtual void getInboundStrings( stringlist_t &string_list );

    //  porter-side (portfield, specificially) functions
    virtual int recvCommRequest( OUTMESS *OutMessage );

    virtual bool isTransactionComplete( void ) const;
    virtual bool errorCondition( void ) const;

    virtual int generate( CtiXfer &xfer );
    virtual int decode  ( CtiXfer &xfer, int status );

    virtual int sendCommResult( INMESS  *InMessage  );

    virtual void setName( const std::string &name );
};

}
}
