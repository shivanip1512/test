#pragma warning( disable : 4786 )

/*-----------------------------------------------------------------------------*
*
* File:   message
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/message.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/05/19 14:48:30 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __CTIMESSAGE_H__
#define __CTIMESSAGE_H__

#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/collect.h>
#include <rw/rwtime.h>

#include "ctidbgmem.h" // defines CTIDBG_new

#include "collectable.h"

#include <rw\thr\mutex.h>
#include "dlldefs.h"

class CtiMessage;    // Forward reference...

/******************************************************************************
 *  This is a base class which comprises an executable (Dispatch-able) message
 *  type.
 *
 *  DERIVED classes MUST DECLARE AND DEFINE THEMSELVES COLLECTIBLE!
 *
 ******************************************************************************/
class IM_EX_MSG CtiMessage : public RWCollectable
{
protected:

   RWTime      MessageTime;         // set to current during construction.
   INT         MessagePriority;
   int         _soe;             // An ID to group events.. Default to zero if not used
   RWCString   _usr;
   RWCString   _pwd;
   int         _token;
   RWCString   _src;

   /*
    *  Allows a message to mark its return path.. This is a bread crumb.
    *   This pointer is never worried about by this class, it is just a carrier
    *   location as the message goes through the machinery on the server....
    */
   VOID        *ConnectionHandle;

public:
   RWDECLARE_COLLECTABLE( CtiMessage );


   CtiMessage(int Pri = 7);

   CtiMessage(const CtiMessage& aRef);

   virtual ~CtiMessage();

   CtiMessage& CtiMessage::operator=(const CtiMessage& aRef);
   RWBoolean operator==(const CtiMessage &aRef) const;
   RWBoolean virtual operator<(const CtiMessage& aRef) const;
   RWBoolean virtual operator>(const CtiMessage& aRef) const;
   virtual CtiMessage& setConnectionHandle(VOID *p);
   virtual VOID* getConnectionHandle();
   VOID setMessagePriority(INT n);
   INT  getMessagePriority() const;
   INT  getSOE() const;
   CtiMessage& setSOE( const INT & soe );


   RWTime getMessageTime() const;
   CtiMessage&   setMessageTime(const RWTime &mTime);
   const RWCString& getUser() const;
   CtiMessage& setUser(const RWCString& usr);

   const RWCString& getPassword() const;
   CtiMessage& setPassword(const RWCString& pwd);

   int getToken() const;
   CtiMessage& setToken(const int& tok);

   const RWCString& getSource() const;
   CtiMessage& setSource(const RWCString& src);

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);

   virtual CtiMessage* replicateMessage() const;

   // Adjust the time to now.
   void resetTime();

   virtual void What() const;

   virtual void PreInsert();

   virtual void dump() const;
   RWCString typeString() const;

   virtual bool isValid();
};

#endif      // #ifndef __CTIMESSAGE_H__

