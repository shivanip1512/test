/*-----------------------------------------------------------------------------*
*
* File:   msg_reg
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_reg.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/collect.h>
#include <rw/rwtime.h>

#include "collectable.h"
#include "logger.h"
#include "msg_reg.h"

#include <rw\thr\mutex.h>
#include "dlldefs.h"

#include "dllbase.h"

RWDEFINE_COLLECTABLE( CtiRegistrationMsg, MSG_REGISTER );


void
CtiRegistrationMsg::restoreGuts(RWvistream& aStream)
{
   CtiMessage::restoreGuts( aStream );

   aStream >> _appName;
   aStream >> _appId;
   aStream >> _appIsUnique;
   aStream >> _appKnownPort;
   aStream >> _appExpirationDelay;
}

void
CtiRegistrationMsg::saveGuts(RWvostream &aStream) const
{
   CtiMessage::saveGuts( aStream );

   aStream << _appName;
   aStream << _appId;
   aStream << _appIsUnique;
   aStream << _appKnownPort;
   aStream << _appExpirationDelay;
}

void
CtiRegistrationMsg::What() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << "CtiRegistrationMsg.... " << endl;
}

// Return a new'ed copy of this message!
CtiMessage* CtiRegistrationMsg::replicateMessage() const
{
   CtiRegistrationMsg *ret = CTIDBG_new CtiRegistrationMsg(*this);

   return( (CtiMessage*)ret );
}


void CtiRegistrationMsg::dump() const
{
   Inherited::dump();

   CtiLockGuard<CtiLogger> doubt_guard(dout);

   dout << " Client Name                   " << _appName << endl;
   dout << " Client App Id                 " << _appId << endl;
   dout << " Is client unique?             " << _appIsUnique << endl;

   dout << " Client Known connection Port  " << _appKnownPort << endl;
   dout << " Client Expiration Delay       " << _appExpirationDelay << endl;     // How many seconds till I believe this guy is DEAD.

}

CtiRegistrationMsg::CtiRegistrationMsg() :
   CtiMessage(15),
   _appIsUnique(FALSE),
   _appId(0),
   _appKnownPort(-1),                // Not all apps will have one.
   _appExpirationDelay(900)          // 5 minute default delay...
{
   // cout << "Creating registration object" << endl;
}

CtiRegistrationMsg::CtiRegistrationMsg(RWCString str, int id, RWBoolean bUnique, int port, int delay) :
   CtiMessage(15),
   _appIsUnique(bUnique),
   _appName(str),
   _appId(id),
   _appKnownPort(port),
   _appExpirationDelay(delay)
{
   // cout << "Creating registration object" << endl;
}

CtiRegistrationMsg::CtiRegistrationMsg(const CtiRegistrationMsg &aRef)
{
   *this = aRef;
}

CtiRegistrationMsg::~CtiRegistrationMsg()
{
   // cout << "Deleting registration object" << endl;
}

// Assignement operator
CtiRegistrationMsg& CtiRegistrationMsg::operator=(const CtiRegistrationMsg& aRef)
{
   if(this != &aRef)
   {
      // From the base class
      Inherited::operator=(aRef);

      _appIsUnique          = aRef.getAppIsUnique();
      _appName              = aRef.getAppName();
      _appId                = aRef.getAppId();
      _appKnownPort         = aRef.getAppKnownPort();
      _appExpirationDelay   = aRef.getAppExpirationDelay();
   }

   return (*this);
}

RWCString   CtiRegistrationMsg::getAppName() const            { return _appName; }
RWCString&  CtiRegistrationMsg::getAppName()                  { return _appName; }
void        CtiRegistrationMsg::setAppName(RWCString str)     { _appName = str; }

int         CtiRegistrationMsg::getAppId() const              { return _appId;   }
void        CtiRegistrationMsg::setAppID(int id)              { _appId = id;    }

RWBoolean   CtiRegistrationMsg::getAppIsUnique() const        { return _appIsUnique; }
void        CtiRegistrationMsg::setAppIsUnique(RWBoolean b)   { _appIsUnique = b;    }

int         CtiRegistrationMsg::getAppKnownPort() const       { return _appKnownPort; }
void        CtiRegistrationMsg::setAppKnownPort(int p)        { _appKnownPort = p; }

int         CtiRegistrationMsg::getAppExpirationDelay() const { return _appExpirationDelay; }
void        CtiRegistrationMsg::setAppExpirationDelay(int d)  { _appExpirationDelay = d; }

