#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
* File:   message
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/message.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/04/22 17:46:36 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/collect.h>
#include "message.h"
#include "collectable.h"
#include "dllbase.h"
#include "logger.h"


RWDEFINE_COLLECTABLE( CtiMessage, MSG_DEFAULT );


void CtiMessage::saveGuts(RWvostream &aStream) const
{
   RWCollectable::saveGuts( aStream );

   aStream << MessageTime;
   aStream << MessagePriority;
   aStream << _soe;
   aStream << _usr;
   aStream << _pwd;
   aStream << _token;
   aStream << _src;
}

void CtiMessage::restoreGuts(RWvistream& aStream)
{
   RWCollectable::restoreGuts( aStream );

   aStream >> MessageTime;
   aStream >> MessagePriority;
   aStream >> _soe;
   aStream >> _usr;
   aStream >> _pwd;
   aStream >> _token;
   aStream >> _src;
}

CtiMessage* CtiMessage::replicateMessage() const
{
   return (new CtiMessage(*this));
}

void CtiMessage::dump() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);

   dout << " ------- Message -------       " << isA() << endl;
   dout << "Time                           " << MessageTime << endl;
   dout << "Priority                       " << MessagePriority << endl;
   dout << "SOE                            " << _soe << endl;
   dout << "User                           " << _usr << endl;
   dout << "Password                       " << _pwd << endl;
   dout << "Token                          " << _token << endl;
   dout << "Source                         " << _src << endl;

}
INT  CtiMessage::getSOE() const
{
   return _soe;
}

CtiMessage& CtiMessage::setSOE( const INT & soe )
{
   _soe = soe;
   return *this;
}


const RWCString& CtiMessage::getUser() const
{
   return _usr;
}
CtiMessage& CtiMessage::setUser(const RWCString& usr)
{
   _usr  = usr;
   return *this;
}

const RWCString& CtiMessage::getPassword() const
{
   return _pwd;
}
CtiMessage& CtiMessage::setPassword(const RWCString& pwd)
{
   _pwd = pwd;
   return *this;
}

int CtiMessage::getToken() const
{
   return _token;
}
CtiMessage& CtiMessage::setToken(const int& tok)
{
   _token = tok;
   return *this;
}

CtiMessage::CtiMessage(int Pri) :
   ConnectionHandle(NULL),
   MessagePriority(Pri & 0x0000000f),
   _soe(0),
   _usr(""),
   _pwd(""),
   _token(-1),
   _src("")
{}

CtiMessage::CtiMessage(const CtiMessage& aRef) :
   ConnectionHandle(NULL),
   MessagePriority(aRef.getMessagePriority()),
   _soe(0),
   _usr(""),
   _pwd(""),
   _token(-1),
   _src("")
{}

CtiMessage::~CtiMessage() {};                                 // Destructor

CtiMessage& CtiMessage::operator=(const CtiMessage& aRef)
{
   if(this != &aRef)
   {
      MessageTime       = aRef.getMessageTime();
      MessagePriority   = aRef.getMessagePriority();
      _soe              = aRef.getSOE();
      _usr              = aRef.getUser();
      _pwd              = aRef.getPassword();
      _token            = aRef.getToken();
      _src              = aRef.getSource();
   }

   return *this;
}

RWBoolean CtiMessage::operator==(const CtiMessage &aRef) const
{
   return (this == &aRef);
}

RWBoolean CtiMessage::operator<(const CtiMessage& aRef) const
{
   RWBoolean bRet(TRUE);

   if(MessagePriority < aRef.getMessagePriority())
   {
      bRet = RWBoolean(FALSE);   // Higher priority is "less"
   }
   else if(MessagePriority == aRef.getMessagePriority())
   {
      if(MessageTime >= aRef.getMessageTime())   // Higher time is "greater"
      {
         bRet = RWBoolean(FALSE);
      }
   }
   return bRet;
}

RWBoolean CtiMessage::operator>(const CtiMessage& aRef) const
{
   RWBoolean bRet(TRUE);

   if(MessagePriority > aRef.getMessagePriority())
   {
      bRet = RWBoolean(FALSE);   // Higher priority is "less"
   }
   else if(MessagePriority == aRef.getMessagePriority())
   {
      if(MessageTime < aRef.getMessageTime())   // Higher time is "greater"
      {
         bRet = RWBoolean(FALSE);
      }
   }
   return bRet;
}

CtiMessage& CtiMessage::setConnectionHandle(VOID *p)
{
   ConnectionHandle = p;
   return *this;
}

VOID* CtiMessage::getConnectionHandle()
{
   return ConnectionHandle;
}

VOID CtiMessage::setMessagePriority(INT n)   { MessagePriority = n & 0x0000000f; }
INT  CtiMessage::getMessagePriority() const  { return MessagePriority;           }
INT  CtiMessage::getSOE() const;

RWTime CtiMessage::getMessageTime() const    { return MessageTime;}
CtiMessage&  CtiMessage::setMessageTime(const RWTime &mTime)
{
   MessageTime = mTime;
   return *this;
}

// Adjust the time to now.
void CtiMessage::resetTime()
{
    MessageTime = MessageTime.now();
}

void CtiMessage::What() const
{
}

void CtiMessage::PreInsert()
{
}


const RWCString& CtiMessage::getSource() const
{
    return _src;
}

CtiMessage& CtiMessage::setSource(const RWCString& src)
{
    _src = src;
    return *this;
}

bool CtiMessage::isValid()
{
    return true;
}

