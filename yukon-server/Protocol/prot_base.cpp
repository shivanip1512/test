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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/03/17 05:22:46 $
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


int Interface::generate( CtiXfer &xfer )              {   return NoMethod;   }
int Interface::decode( CtiXfer &xfer, int status )    {   return NoMethod;   }

int Interface::commOut( OUTMESS *&OutMessage )   {   return NoMethod;    }
int Interface::commIn ( INMESS   *InMessage  )   {   return NoMethod;    }

int Interface::sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )   {   return NoMethod;    }
int Interface::recvCommResult ( INMESS *InMessage,    RWTPtrSlist< OUTMESS > &outList )   {   return NoMethod;    }

int Interface::recvCommRequest( OUTMESS  *OutMessage )   {   return NoMethod;    }
int Interface::sendCommResult ( INMESS   *InMessage  )   {   return NoMethod;    }

bool Interface::isTransactionComplete( void ) {   return true;    }

void Interface::getInboundPoints ( pointlist_t  &point_list )   {  };
void Interface::getInboundStrings( stringlist_t &string_list )  {  };


}
}
