/*-----------------------------------------------------------------------------*
*
* File:   message
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/message.h-arc  $
* REVISION     :  $Revision: 1.10.22.2 $
* DATE         :  $Date: 2008/11/13 17:23:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __CTIMESSAGE_H__
#define __CTIMESSAGE_H__
#pragma warning( disable : 4786 )


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <iostream>
#include <string>

#include <rw/collect.h>
#include <rw/vstream.h>
#include "ctitime.h"
#include "ctidbgmem.h" // defines CTIDBG_new
#include "collectable.h"

#include <rw\thr\mutex.h>
#include "dlldefs.h"
#include "rwutil.h"

class CtiMessage;    // Forward reference...

//typedef std::vector<CtiMessage*> CtiMultiMsg_vec;
typedef std::vector<RWCollectable*> CtiMultiMsg_vec;

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

   CtiTime      MessageTime;         // set to current during construction.
   INT          MessagePriority;
   int          _soe;             // An ID to group events.. Default to zero if not used
   std::string  _usr;
   int          _token;
   std::string  _src;

   /*
    *  Allows a message to mark its return path.. This is a bread crumb.
    *   This pointer is never worried about by this class, it is just a carrier
    *   location as the message goes through the machinery on the server....
    */
   VOID  *ConnectionHandle;

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


   CtiTime getMessageTime() const;
   CtiMessage&   setMessageTime(const CtiTime &mTime);
   const std::string& getUser() const;
   CtiMessage&   setUser(const std::string& usr);

   int getToken() const;
   CtiMessage& setToken(const int& tok);

   const std::string& getSource() const;
   CtiMessage&   setSource(const std::string& src);

   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);

   virtual CtiMessage* replicateMessage() const;

   // Adjust the time to now.
   void resetTime();

   virtual void PreInsert();

   virtual void dump() const;
   std::string typeString() const;

   virtual bool isValid();
};

// This will be used to sort these things in a CtiQueue.
namespace std
{
  template <> struct greater<CtiMessage*>
  {
    bool operator()(CtiMessage const* p1, CtiMessage const* p2)
    {
      if(!p1)
        return true;
      if(!p2)
        return false;
      return *p1 > *p2;
    }
  };
};


#endif      // #ifndef __CTIMESSAGE_H__

