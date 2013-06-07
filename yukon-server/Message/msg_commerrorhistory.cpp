/*-----------------------------------------------------------------------------*
*
* File:   msg_commerrorhistory
*
* Date:   9/21/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_commerrorhistory.cpp-arc  $
* REVISION     :  $Revision: 1.9.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iostream>
#include <iomanip>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "collectable.h"
#include "logger.h"
#include "msg_commerrorhistory.h"

DEFINE_COLLECTABLE( CtiCommErrorHistoryMsg, MSG_COMMERRORHISTORY );

long CtiCommErrorHistoryMsg::getCommErrorId() const
{
   return _commErrorId;
}

CtiCommErrorHistoryMsg& CtiCommErrorHistoryMsg::setCommErrorId( const long a_id )
{
   _commErrorId = a_id;
   return *this;
}

long CtiCommErrorHistoryMsg::getPAOId() const
{
   return _paoId;
}

CtiCommErrorHistoryMsg& CtiCommErrorHistoryMsg::setPAOId( const long a_id )
{
   _paoId = a_id;
   return *this;
}

const CtiTime& CtiCommErrorHistoryMsg::getDateTime() const
{
    return _dateTime;
}

CtiCommErrorHistoryMsg& CtiCommErrorHistoryMsg::setDateTime(const CtiTime& aTime)
{
    _dateTime = aTime;
    return *this;
}

int CtiCommErrorHistoryMsg::getErrorType() const
{
   return _errorType;
}

CtiCommErrorHistoryMsg& CtiCommErrorHistoryMsg::setErrorType( const int type )
{
   _errorType = type;
   return *this;
}

long CtiCommErrorHistoryMsg::getErrorNumber() const
{
   return _errorNumber;
}

CtiCommErrorHistoryMsg& CtiCommErrorHistoryMsg::setErrorNumber( const long number )
{
   _errorNumber = number;
   return *this;
}

const string& CtiCommErrorHistoryMsg::getCommand() const
{
   return _command;
}
CtiCommErrorHistoryMsg& CtiCommErrorHistoryMsg::setCommand(const string& aString)
{
   _command = aString;
   return *this;
}

const string& CtiCommErrorHistoryMsg::getOutMessage() const
{
   return _outMessage;
}
CtiCommErrorHistoryMsg& CtiCommErrorHistoryMsg::setOutMessage(const string& aString)
{
   _outMessage = aString;
   return *this;
}

const string& CtiCommErrorHistoryMsg::getInMessage() const
{
   return _inMessage;
}
CtiCommErrorHistoryMsg& CtiCommErrorHistoryMsg::setInMessage(const string& aString)
{
   _inMessage = aString;
   return *this;
}


void CtiCommErrorHistoryMsg::dump() const
{
   Inherited::dump();

   CtiLockGuard<CtiLogger> doubt_guard(dout);

   dout << " Comm Error Id                 " << getCommErrorId() << endl;
   dout << " PAO Id                        " << getPAOId() << endl;
   dout << " Date Time                     " << getDateTime() << endl;
   dout << " Error Type                    " << getErrorType() << endl;
   dout << " Error Number                  " << getErrorNumber() << endl;
   dout << " Command                       " << getCommand() << endl;
   dout << " Out Message                   " << getOutMessage() << endl;
   dout << " In Message                    " << getInMessage() << endl;
}


// Return a new'ed copy of this message!
CtiMessage* CtiCommErrorHistoryMsg::replicateMessage() const
{
   CtiCommErrorHistoryMsg *ret = CTIDBG_new CtiCommErrorHistoryMsg(*this);

   return( (CtiMessage*)ret );
}


CtiCommErrorHistoryMsg::CtiCommErrorHistoryMsg(long paoid,
                                               int type,
                                               long errnumb,
                                               string comm,
                                               string outmess,
                                               string inmess,
                                               int pri,
                                               CtiTime aTime,
                                               long ceid
                                               ) :
   Inherited(pri), _commErrorId(ceid), _paoId(paoid), _dateTime(aTime), _errorType(type),
   _errorNumber(errnumb), _command(comm), _outMessage(outmess), _inMessage(inmess)
{
   //Inherited::setPriority(pri);
   //Inherited::setSOE(soe);
   //Inherited::setUser(usr);
}

CtiCommErrorHistoryMsg::CtiCommErrorHistoryMsg(const CtiCommErrorHistoryMsg& aRef)
{
   *this = aRef;
}

CtiCommErrorHistoryMsg::~CtiCommErrorHistoryMsg() {}

CtiCommErrorHistoryMsg& CtiCommErrorHistoryMsg::operator=(const CtiCommErrorHistoryMsg& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      _commErrorId = aRef.getCommErrorId();
      _paoId = aRef.getPAOId();
      _dateTime = aRef.getDateTime();
      _errorType = aRef.getErrorType();
      _errorNumber = aRef.getErrorNumber();
      _command = aRef.getCommand();
      _outMessage = aRef.getOutMessage();
      _inMessage = aRef.getInMessage();
   }
   return *this;
}

bool CtiCommErrorHistoryMsg::isValid()
{
    return _dateTime.isValid();
}
