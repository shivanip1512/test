#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   msg_email
*
* Date:   4/2/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_email.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:23:53 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <iostream>
#include <iomanip>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "collectable.h"
#include "logger.h"
#include "msg_email.h"

RWDEFINE_COLLECTABLE( CtiEmailMsg, MSG_EMAIL );

void CtiEmailMsg::saveGuts(RWvostream &aStream) const
{

   Inherited::saveGuts(aStream);
   aStream << getID() << getType() << getSubject() << getText() << getSender();
}

void CtiEmailMsg::restoreGuts(RWvistream& aStream)
{
   Inherited::restoreGuts(aStream);
   aStream >> _id   >> _idType >> _subject >> _text >> _sender;
}


LONG CtiEmailMsg::getID() const
{
   return _id;
}
CtiEmailMsg& CtiEmailMsg::setID(const LONG id)
{
   _id = id;
   return *this;
}

INT CtiEmailMsg::getType() const
{
   return _idType;
}
CtiEmailMsg& CtiEmailMsg::setType(const INT type)
{
   _idType = type;
   return *this;
}

RWCString CtiEmailMsg::getSender() const
{
   return _sender;
}
CtiEmailMsg& CtiEmailMsg::setSender(const RWCString &str)
{
   _sender = str;
   return *this;
}

RWCString CtiEmailMsg::getSubject() const
{
   return _subject;
}
CtiEmailMsg& CtiEmailMsg::setSubject(const RWCString &str)
{
   _subject = str;
   return *this;
}

RWCString CtiEmailMsg::getText() const
{
   return _text;
}
CtiEmailMsg& CtiEmailMsg::setText(const RWCString &str)
{
   _text = str;
   return *this;
}

CtiMessage* CtiEmailMsg::replicateMessage() const
{
   CtiEmailMsg *pNew = CTIDBG_new CtiEmailMsg;

   if(pNew != NULL)
   {
      *pNew = *this;
   }

   return pNew;
}

void CtiEmailMsg::dump() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);
   Inherited::dump();

   dout << " Id                            " << getID() << endl;
   dout << " Type                          " << getType() << endl;
   dout << " Subject                       " << getSubject() << endl;
   dout << " Text                          " << getText() << endl;
   dout << " Sender                        " << getSender() << endl;
}

CtiEmailMsg::CtiEmailMsg( LONG id, INT type) :
   _id(id),
   _idType(type),
   _sender(gEmailFrom)
{
}

CtiEmailMsg::CtiEmailMsg(const CtiEmailMsg& aRef)
{
   *this = aRef;
}

CtiEmailMsg::~CtiEmailMsg() {}

CtiEmailMsg& CtiEmailMsg::operator=(const CtiEmailMsg& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      setID(aRef.getID());
      setType(aRef.getType());
      setSender(aRef.getSender());
      setSubject(aRef.getSubject());
      setText(aRef.getText());
   }
   return *this;
}
