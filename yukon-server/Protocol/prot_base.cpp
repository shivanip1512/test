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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:57 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag
#include "prot_base.h"

CtiProtocolBase::CtiProtocolBase()  {}

CtiProtocolBase::CtiProtocolBase(const CtiProtocolBase &aRef)
{
    *this = aRef;
}

CtiProtocolBase::~CtiProtocolBase() {}

CtiProtocolBase &CtiProtocolBase::operator=(const CtiProtocolBase &aRef)
{
    if( this != &aRef )
    {
    }

    return *this;
}


int CtiProtocolBase::generate( CtiXfer &xfer )              {   return 0;   }
int CtiProtocolBase::decode( CtiXfer &xfer, int status )    {   return 0;   }

int CtiProtocolBase::commOut( OUTMESS *&OutMessage )   {   return NoMethod;    }
int CtiProtocolBase::commIn ( INMESS   *InMessage  )   {   return NoMethod;    }

int CtiProtocolBase::sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )   {   return NoMethod;    }
int CtiProtocolBase::recvCommResult ( INMESS *InMessage,    RWTPtrSlist< OUTMESS > &outList )   {   return NoMethod;    }

int CtiProtocolBase::recvCommRequest( OUTMESS  *OutMessage )   {   return NoMethod;    }
int CtiProtocolBase::sendCommResult ( INMESS   *InMessage  )   {   return NoMethod;    }

bool CtiProtocolBase::isTransactionComplete( void ) {   return true;    }

