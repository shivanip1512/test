#include "precompiled.h"

#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag
#include "prot_base.h"

namespace Cti {
namespace Protocols {

YukonError_t Interface::generate( CtiXfer &xfer )                       {   return ClientErrors::NoMethod;   }
YukonError_t Interface::decode  ( CtiXfer &xfer, YukonError_t status )  {   return ClientErrors::NoMethod;   }

YukonError_t Interface::sendCommRequest( OUTMESS *&OutMessage,    std::list< OUTMESS* > &outList )   {   return ClientErrors::NoMethod;    }
YukonError_t Interface::recvCommResult ( const INMESS &InMessage, std::list< OUTMESS* > &outList )   {   return ClientErrors::NoMethod;    }

YukonError_t Interface::recvCommRequest( OUTMESS  *OutMessage )   {   return ClientErrors::NoMethod;    }
YukonError_t Interface::sendCommResult ( INMESS   &InMessage  )   {   return ClientErrors::NoMethod;    }

bool Interface::isTransactionComplete( void ) const {   return true;    }
bool Interface::errorCondition       ( void ) const {   return false;   }

void Interface::getInboundPoints ( pointlist_t  &point_list )   {  };
void Interface::getInboundStrings( stringlist_t &string_list )  {  };

void Interface::setName( const std::string &name )
{
    _name = name;
}

}
}
