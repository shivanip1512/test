#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* File:   Msg_reg
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/Msg_reg.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:24 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __MSG_REG_H__
#define __MSG_REG_H__

#include <rw\rwtime.h>
#include <rw/thr/threadid.h>

#include "message.h"       // get the base class
#include "dlldefs.h"


class IM_EX_MSG CtiRegistrationMsg : public CtiMessage
{
private:

   RWCString      _appName;
   RWThreadId     _appId;
   int            _appIsUnique;

   int            _appKnownPort;
   int            _appExpirationDelay;     // How many seconds till I believe this guy is DEAD.

public:
   RWDECLARE_COLLECTABLE( CtiRegistrationMsg );

   typedef CtiMessage Inherited;

   CtiRegistrationMsg();
   CtiRegistrationMsg(RWCString str, RWThreadId id, RWBoolean bUnique, int port = -1, int delay = 900);
   CtiRegistrationMsg(const CtiRegistrationMsg &aRef);
   virtual ~CtiRegistrationMsg();
   // Assignement operator
   CtiRegistrationMsg& CtiRegistrationMsg::operator=(const CtiRegistrationMsg& aRef);

   void saveGuts(RWvostream &aStream) const;
   void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;


   RWCString   getAppName() const;
   RWCString&  getAppName();
   void        setAppName(RWCString str);

   RWThreadId  getAppId() const;
   void        setAppID(RWThreadId id);

   RWBoolean   getAppIsUnique() const;
   void        setAppIsUnique(RWBoolean b);

   int         getAppKnownPort() const;
   void        setAppKnownPort(int p);

   int         getAppExpirationDelay() const;
   void        setAppExpirationDelay(int d);

   void        What() const;

   virtual void dump() const;
};

#endif   // #ifndef __MSG_REG_H__

