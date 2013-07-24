/*-----------------------------------------------------------------------------*
*
* File:   message
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/message.cpp-arc  $
* REVISION     :  $Revision: 1.13.10.1 $
* DATE         :  $Date: 2008/11/10 20:47:13 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <rw/collect.h>
#include "message.h"
#include "collectable.h"
#include "dllbase.h"
#include "logger.h"
#include "numstr.h"

using std::string;
using std::endl;

#define DEFAULT_SYSTEM_USER "(yukon system)"
RWDEFINE_COLLECTABLE( CtiMessage, MSG_DEFAULT );


void CtiMessage::saveGuts(RWvostream &aStream) const
{
   RWCollectable::saveGuts( aStream );

   aStream << MessageTime;
   aStream << MessagePriority;
   aStream << _soe;
   aStream << _usr;
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
   aStream >> _token;
   aStream >> _src;
}

CtiMessage* CtiMessage::replicateMessage() const
{
   return (CTIDBG_new CtiMessage(*this));
}

void CtiMessage::dump() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);

   dout << " ------- Message -------       " << typeString() << endl;
   dout << "Time                           " << MessageTime << endl;
   dout << "Priority                       " << MessagePriority << endl;
   dout << "SOE                            " << _soe << endl;
   dout << "User                           " << _usr << endl;
   //dout << "Password                       " << _pwd << endl;
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


const string& CtiMessage::getUser() const
{
   return _usr;
}
CtiMessage& CtiMessage::setUser(const string& usr)
{
   _usr  = usr;
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
   _usr(DEFAULT_SYSTEM_USER),
   //_pwd(""),
   _token(-1),
   _src("")
{}

CtiMessage::CtiMessage(const CtiMessage& aRef) :
   ConnectionHandle(NULL),
   MessagePriority(aRef.getMessagePriority()),
   _soe(0),
   _usr(DEFAULT_SYSTEM_USER),
   _token(-1),
   _src("")
{}

CtiMessage::~CtiMessage()
{
    ConnectionHandle = 0;       // deleted elsewhere...  zeroing to make lint happy
}

CtiMessage& CtiMessage::operator=(const CtiMessage& aRef)
{
   if(this != &aRef)
   {
      MessageTime       = aRef.getMessageTime();
      MessagePriority   = aRef.getMessagePriority();
      _soe              = aRef.getSOE();
      _usr              = aRef.getUser();
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
   RWBoolean bRet(FALSE);

   if(MessagePriority < aRef.getMessagePriority())
   {
      bRet = RWBoolean(TRUE);   // Higher priority is "less".  Sorts ahead of lower priority.
   }
   return bRet;
}

RWBoolean CtiMessage::operator>(const CtiMessage& aRef) const
{
   RWBoolean bRet(FALSE);

   if(MessagePriority > aRef.getMessagePriority())
   {
      bRet = RWBoolean(TRUE);   // Lower priority sorts behind greater.  Lower == is "more" in the lists.
   }

   return bRet;
}

CtiMessage& CtiMessage::setConnectionHandle(void *p)
{
   ConnectionHandle = p;
   return *this;
}

void* CtiMessage::getConnectionHandle() const
{
   return ConnectionHandle;
}

void CtiMessage::setMessagePriority(INT n)   { MessagePriority = n & 0x0000000f; }
INT  CtiMessage::getMessagePriority() const  { return MessagePriority;           }


CtiTime CtiMessage::getMessageTime() const    { return MessageTime;}
CtiMessage&  CtiMessage::setMessageTime(const CtiTime &mTime)
{
   MessageTime = mTime;
   return *this;
}

// Adjust the time to now.
void CtiMessage::resetTime()
{
    MessageTime = MessageTime.now();
}

void CtiMessage::PreInsert()
{
}


const string& CtiMessage::getSource() const
{
    return _src;
}

CtiMessage& CtiMessage::setSource(const string& src)
{
    _src = src;
    return *this;
}

bool CtiMessage::isValid()
{
    return true;
}

string CtiMessage::typeString() const
{
    string rstr = string(CtiNumStr(isA()));
    /*
    #define MSG_BASE                          1500
    #define MSG_NULL                          (MSG_BASE)
    #define MSG_DEFAULT                       ((MSG_BASE) + 5 )
    #define MSG_TRACE                         ((MSG_BASE) + 20)
    #define MSG_COMMAND                       ((MSG_BASE) + 30)
    #define MSG_REGISTER                      ((MSG_BASE) + 40)
    #define MSG_SERVER_REQUEST                ((MSG_BASE) + 50)
    #define MSG_SERVER_RESPONSE               ((MSG_BASE) + 51)
    #define MSG_POINTREGISTRATION             ((MSG_BASE) + 70)
    #define MSG_DBCHANGE                      ((MSG_BASE) + 80)
    #define MSG_PCREQUEST                     ((MSG_BASE) + 85)
    #define MSG_PCRETURN                      ((MSG_BASE) + 90)

    #define MSG_MULTI                         ((MSG_BASE) + 91)
    #define MSG_TAG                           ((MSG_BASE) + 94)
    #define MSG_POINTDATA                     ((MSG_BASE) + 95)
    #define MSG_SIGNAL                        ((MSG_BASE) + 96)
    #define MSG_EMAIL                         ((MSG_BASE) + 97)
    #define MSG_LMCONTROLHISTORY              ((MSG_BASE) + 98)
    */

    switch(isA())
    {
    case MSG_BASE:
        {
            rstr += ": MSG_BASE";
            break;
        }
    case MSG_DEFAULT:
        {
            rstr += ": MSG_DEFAULT";
            break;
        }
    case MSG_TRACE:
        {
            rstr += ": MSG_TRACE";
            break;
        }
    case MSG_COMMAND:
        {
            rstr += ": MSG_COMMAND";
            break;
        }
    case MSG_REGISTER:
        {
            rstr += ": MSG_REGISTER";
            break;
        }
    case MSG_SERVER_REQUEST:
        {
            rstr += ": MSG_SERVER_REQUEST";
            break;
        }
    case MSG_SERVER_RESPONSE:
        {
            rstr += ": MSG_SERVER_RESPONSE";
            break;
        }
    case MSG_POINTREGISTRATION:
        {
            rstr += ": MSG_POINTREGISTRATION";
            break;
        }
    case MSG_DBCHANGE:
        {
            rstr += ": MSG_DBCHANGE";
            break;
        }
    case MSG_PCREQUEST:
        {
            rstr += ": MSG_PCREQUEST";
            break;
        }
    case MSG_PCRETURN:
        {
            rstr += ": MSG_PCRETURN";
            break;
        }
    case MSG_MULTI:
        {
            rstr += ": MSG_MULTI";
            break;
        }
    case MSG_TAG:
        {
            rstr += ": MSG_TAG";
            break;
        }
    case MSG_POINTDATA:
        {
            rstr += ": MSG_POINTDATA";
            break;
        }
    case MSG_SIGNAL:
        {
            rstr += ": MSG_SIGNAL";
            break;
        }

    case MSG_LMCONTROLHISTORY:
        {
            rstr += ": MSG_LMCONTROLHISTORY";
            break;
        }
    }

    return rstr;
}
