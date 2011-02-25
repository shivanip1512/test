/*-----------------------------------------------------------------------------*
*
* File:   con_mgr
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/INCLUDE/con_mgr.h-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2007/10/19 21:08:31 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __CON_MGR_H__
#define __CON_MGR_H__
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <limits.h>

#include <rw/thr/mutex.h>
#include <rw/thr/thrfunc.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "connection.h"
#include "ctibase.h"
#include "string_utility.h"

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

   string         ClientName;
   int               ClientAppId;

   int              _serverRequestId;

   CtiConnectionManager() :
      ClientRegistered(FALSE),
      ClientName("DEFAULT")
   {
      std::cout << "Default Constructor may break things!" << FO(__FILE__) << " " << __LINE__ << endl;
      std::cout << "**** Connection Manager!!! *****" << endl;
   }

   virtual void writeIncomingMessageToQueue(CtiMessage *msgPtr);
public:

   typedef CtiConnection Inherited;

   CtiConnectionManager( const INT &Port, const string &HostMachine, Que_t *inQ = NULL );

   CtiConnectionManager(CtiExchange *xchg, Que_t *inQ = NULL);

   virtual ~CtiConnectionManager();
   int         getClientAppId() const;//              { return ClientAppId; }
   int         setClientAppId(int id);//       { return ClientAppId = id; }

   string   getClientName() const;//               { return ClientName; }
   void        setClientName(string str);
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

   int getRequestId() const;
   void setRequestId(int rid);

   int WriteConnQue(CtiMessage*, unsigned millitimeout = 0, bool cleaniftimedout = true, int payload_status = 0, string payload_string = string() );
};

#endif      // #ifndef __CON_MGR_H__


