/*-----------------------------------------------------------------------------*
*
* File:   con_mgr
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/con_mgr.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:24:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/toolpro/neterr.h>

#include "dlldefs.h"
#include "collectable.h"
#include "con_mgr.h"


CtiConnectionManager::CtiConnectionManager( const INT &Port, const RWCString &HostMachine, InQ_t *inQ) :
   ClientName("DEFAULT"),
   ClientAppId(0),
   ClientUnique(FALSE),
   ClientQuestionable(FALSE),
   ClientRegistered(FALSE),
   _clientExpirationDelay(180),
   CtiConnection(Port, HostMachine, inQ)
{
   // cout << "**** Connection Manager!!! *****" << endl;
}

CtiConnectionManager::CtiConnectionManager(CtiExchange *xchg, InQ_t *inQ) :
   ClientName("DEFAULT"),
   ClientAppId(0),
   ClientUnique(FALSE),
   ClientQuestionable(FALSE),
   ClientRegistered(FALSE),
   _clientExpirationDelay(180),
   CtiConnection(xchg, inQ)
{}

CtiConnectionManager::~CtiConnectionManager()
{
   // We better just do the normal stuff one at a time... Make sure this is virtual
   // so base destructor gets called in all cases.

   // Inherited::ShutdownConnection(); // Handled in the base class...
}

int CtiConnectionManager::getClientAppId() const              { return ClientAppId; }
int CtiConnectionManager::setClientAppId(int id)       { return ClientAppId = id; }

RWCString   CtiConnectionManager::getClientName() const               { return ClientName; }
void        CtiConnectionManager::setClientName(RWCString str)
{
   ClientName = str;
   Inherited::setName( str );
}

RWBoolean   CtiConnectionManager::getClientUnique() const             { return RWBoolean(ClientUnique);  }
void        CtiConnectionManager::setClientUnique(RWBoolean b) { ClientUnique = RWBoolean(b);     }

RWBoolean   CtiConnectionManager::getClientQuestionable() const             { return RWBoolean(ClientQuestionable);  }
void        CtiConnectionManager::setClientQuestionable(RWBoolean b) { ClientQuestionable = RWBoolean(b);     }

RWBoolean   CtiConnectionManager::getClientRegistered()               { return RWBoolean(ClientRegistered);}
void        CtiConnectionManager::setClientRegistered(RWBoolean b) { ClientRegistered = RWBoolean(b); }

int   CtiConnectionManager::getClientExpirationDelay() const    {return _clientExpirationDelay; }
void  CtiConnectionManager::setClientExpirationDelay(int p)     {_clientExpirationDelay = p; }

RWBoolean CtiConnectionManager::operator==(const CtiConnectionManager& aRef) const
{
   return (this == &aRef);
}

unsigned CtiConnectionManager::hash(const CtiConnectionManager& aRef)
{
   return (unsigned)&aRef;            // The address of the Object?
}

