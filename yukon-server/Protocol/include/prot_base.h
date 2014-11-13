#pragma once

#include <vector>
#include <string>
#include <list>

#include "msg_pdata.h"
#include "dlldefs.h"
#include "xfer.h"

namespace Cti {
namespace Protocols {

class IM_EX_PROT Interface : private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Interface(const Interface&);
    Interface& operator=(const Interface&);

protected:

    std::string _name;

    Interface()
    {}

public:

    typedef std::vector< CtiPointDataMsg * > pointlist_t;
    typedef std::vector< std::string * >     stringlist_t;

    virtual ~Interface() { }

    //  client-side (Scanner, PIL) functions
    virtual YukonError_t sendCommRequest( OUTMESS *&OutMessage, std::list< OUTMESS* > &outList );
    virtual YukonError_t recvCommResult ( const INMESS &InMessage, std::list< OUTMESS* > &outList );

    virtual void getInboundPoints ( pointlist_t  &point_list );
    virtual void getInboundStrings( stringlist_t &string_list );

    //  porter-side (portfield, specificially) functions
    virtual YukonError_t recvCommRequest( OUTMESS *OutMessage );

    virtual bool isTransactionComplete( void ) const;
    virtual bool errorCondition( void ) const;

    virtual YukonError_t generate( CtiXfer &xfer );
    virtual YukonError_t decode  ( CtiXfer &xfer, YukonError_t status );

    virtual YukonError_t sendCommResult( INMESS  &InMessage  );

    virtual void setName( const std::string &name );
};

}
}
