#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
* File:   con_mgr
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/INCLUDE/con_mgr.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:20:04 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __CON_MGR_H__
#define __CON_MGR_H__

#include <limits.h>

#include <rw/thr/mutex.h>
#include <rw/thr/thrfunc.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "connection.h"
#include "ctibase.h"

class IM_EX_CTISVR CtiConnectionManager : public CtiConnection
{
protected:

   int  _clientExpirationDelay;                  // Number of seconds until we need to be pinged

   struct         // Bit field status definitions
   {
      unsigned       ClientRegistered   : 1;
      unsigned       ClientUnique       : 1; // Is this the only allowed client of this name?
      unsigned       ClientQuestionable : 1;
   };

   RWCString         ClientName;
   RWThreadId        ClientAppId;


   CtiConnectionManager() :
      ClientRegistered(FALSE),
      ClientName("DEFAULT")
   {
      cout << "Default Constructor may break things!" << __FILE__ << " " << __LINE__ << endl;
      cout << "**** Connection Manager!!! *****" << endl;
   }

public:

   typedef CtiConnection Inherited;

   CtiConnectionManager( const INT &Port, const RWCString &HostMachine, InQ_t *inQ = NULL );/* :
      ClientName("DEFAULT"),
      ClientAppId(0),
      ClientUnique(FALSE),
      ClientQuestionable(FALSE),
      ClientRegistered(FALSE),
      _clientExpirationDelay(180),
      CtiConnection(Port, HostMachine, inQ)
   {
      // cout << "**** Connection Manager!!! *****" << endl;
   }                                                                                          */

   CtiConnectionManager(CtiExchange *xchg, InQ_t *inQ = NULL);/* :
      ClientName("DEFAULT"),
      ClientAppId(0),
      ClientUnique(FALSE),
      ClientQuestionable(FALSE),
      ClientRegistered(FALSE),
      _clientExpirationDelay(180),
      CtiConnection(xchg, inQ)
   {}                                                           */

   virtual ~CtiConnectionManager();
   RWThreadId  getClientAppId() const;//              { return ClientAppId; }
   RWThreadId  setClientAppId(RWThreadId id);//       { return ClientAppId = id; }

   RWCString   getClientName() const;//               { return ClientName; }
   void        setClientName(RWCString str);
   RWBoolean   getClientUnique() const;//             { return RWBoolean(ClientUnique);  }
   void        setClientUnique(RWBoolean b = TRUE);// { ClientUnique = RWBoolean(b);     }

   RWBoolean   getClientQuestionable() const;//             { return RWBoolean(ClientQuestionable);  }
   void        setClientQuestionable(RWBoolean b = TRUE);// { ClientQuestionable = RWBoolean(b);     }

   RWBoolean   getClientRegistered();//               { return RWBoolean(ClientRegistered);}
   void        setClientRegistered(RWBoolean b = TRUE);// { ClientRegistered = RWBoolean(b); }

   int   getClientExpirationDelay() const;//    {return _clientExpirationDelay; }
   void  setClientExpirationDelay(int p);//     {_clientExpirationDelay = p; }

   RWBoolean operator==(const CtiConnectionManager& aRef) const;
   static unsigned hash(const CtiConnectionManager& aRef);
};

#endif      // #ifndef __CON_MGR_H__


