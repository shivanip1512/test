/*-----------------------------------------------------------------------------*
*
* File:   prot_base
*
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/03/10 21:15:17 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag
#include "prot_base.h"

namespace Cti       {
namespace Protocol  {


Interface::Interface()  {}

Interface::Interface(const Interface &aRef)
{
    *this = aRef;
}

Interface::~Interface() {}

Interface &Interface::operator=(const Interface &aRef)
{
    if( this != &aRef )
    {
    }

    return *this;
}


int Interface::generate( CtiXfer &xfer )              {   return 0;   }
int Interface::decode( CtiXfer &xfer, int status )    {   return 0;   }

int Interface::commOut( OUTMESS *&OutMessage )   {   return NoMethod;    }
int Interface::commIn ( INMESS   *InMessage  )   {   return NoMethod;    }

int Interface::sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )   {   return NoMethod;    }
int Interface::recvCommResult ( INMESS *InMessage,    RWTPtrSlist< OUTMESS > &outList )   {   return NoMethod;    }

int Interface::recvCommRequest( OUTMESS  *OutMessage )   {   return NoMethod;    }
int Interface::sendCommResult ( INMESS   *InMessage  )   {   return NoMethod;    }

bool Interface::isTransactionComplete( void ) {   return true;    }

bool Interface::hasInboundPoints( void )      {   return false;   }
void Interface::getInboundPoints( vector< CtiPointDataMsg > &pointList ) {   }


}
}
