#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
* File:   msg_signal
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <iostream>
#include <iomanip>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "collectable.h"
#include "logger.h"
#include "msg_signal.h"
#include "yukon.h"

RWDEFINE_COLLECTABLE( CtiSignalMsg, MSG_SIGNAL );

void CtiSignalMsg::saveGuts(RWvostream &aStream) const
{
    Inherited::saveGuts(aStream);
    aStream << getId() << getLogType() << getSignalGroup() << getText() << getAdditionalInfo() << getTags();
}

void CtiSignalMsg::restoreGuts(RWvistream& aStream)
{
    Inherited::restoreGuts(aStream);

    aStream >> _id   >> _logType >> _signalGroup >> _text >> _additional >> _tags;
}

long CtiSignalMsg::getId() const
{
   return _id;
}

CtiSignalMsg& CtiSignalMsg::setId( const long a_id )
{
   _id = a_id;
   return *this;
}

const RWCString& CtiSignalMsg::getText() const
{
   return _text;
}
CtiSignalMsg& CtiSignalMsg::setText(const RWCString& string)
{
   _text = string;
   return *this;
}

unsigned CtiSignalMsg::getSignalGroup() const
{
   return _signalGroup;
}
CtiSignalMsg& CtiSignalMsg::setSignalGroup(const unsigned id)
{
   _signalGroup = id;
   return *this;
}

unsigned CtiSignalMsg::getTags() const
{
   return _tags;
}
CtiSignalMsg& CtiSignalMsg::setTags(const unsigned s)
{
   _tags |= s;
   return *this;
}
CtiSignalMsg& CtiSignalMsg::resetTags(const unsigned s)
{
   _tags &= ~(s);
   return *this;
}

void CtiSignalMsg::dump() const
{
   Inherited::dump();

   CtiLockGuard<CtiLogger> doubt_guard(dout);

   dout << " Id                            " << getId() << endl;
   dout << " Log Type                      " << getLogType() << endl;
   dout << " Signal Group                  " << getSignalGroup() << endl;
   dout << " Text                          " << getText() << endl;
   dout << " Additional Info Text          " << getAdditionalInfo() << endl;
   CHAR oldfill = dout.fill();
   dout.fill('0');
   dout << " Tags                          0x" << hex << setw(8) <<  getTags() << dec << endl;
   dout.fill(oldfill);
}


// Return a new'ed copy of this message!
CtiMessage* CtiSignalMsg::replicateMessage() const
{
   CtiSignalMsg *ret = new CtiSignalMsg(*this);

   return( (CtiMessage*)ret );
}


BOOL CtiSignalMsg::isAlarm() const
{
   return (_signalGroup > SignalEvent);  // it is indeed an alarm!
}

BOOL CtiSignalMsg::isEvent() const
{
   return (_signalGroup == SignalEvent);
}

int CtiSignalMsg::getLogType() const
{
   return _logType;
}
CtiSignalMsg& CtiSignalMsg::setLogType(const int lt)
{
   _logType = lt;
   return *this;
}

const RWCString& CtiSignalMsg::getAdditionalInfo() const
{
   return _additional;
}

CtiSignalMsg& CtiSignalMsg::setAdditionalInfo(const RWCString& string)
{
   _additional = string;
   return *this;
}

CtiSignalMsg::CtiSignalMsg(long pid, int soe, RWCString text, RWCString addl, int lt, unsigned cls, RWCString usr, unsigned tag, int pri) :
   Inherited(pri),
   _id(pid),
   _logType(lt),
   _signalGroup(cls),
   _text(text),
   _additional(addl),
   _tags(tag)
{
   Inherited::setSOE(soe);
   Inherited::setUser(usr);
}

CtiSignalMsg::CtiSignalMsg(const CtiSignalMsg& aRef)
{
   *this = aRef;
}

CtiSignalMsg::~CtiSignalMsg() {}

CtiSignalMsg& CtiSignalMsg::operator=(const CtiSignalMsg& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      _id            = aRef.getId();
      _text          = aRef.getText();
      _signalGroup   = aRef.getSignalGroup();
      _tags          = aRef.getTags();
      _logType       = aRef.getLogType();
      _additional    = aRef.getAdditionalInfo();
   }
   return *this;
}

