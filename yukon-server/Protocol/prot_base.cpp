#include "yukon.h"

#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag
#include "prot_base.h"

namespace Cti {
namespace Protocol {

int Interface::generate( CtiXfer &xfer )              {   return NoMethod;   }
int Interface::decode( CtiXfer &xfer, int status )    {   return NoMethod;   }

int Interface::sendCommRequest( OUTMESS *&OutMessage, std::list< OUTMESS* > &outList )   {   return NoMethod;    }
int Interface::recvCommResult ( INMESS *InMessage,    std::list< OUTMESS* > &outList )   {   return NoMethod;    }

int Interface::recvCommRequest( OUTMESS  *OutMessage )   {   return NoMethod;    }
int Interface::sendCommResult ( INMESS   *InMessage  )   {   return NoMethod;    }

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
