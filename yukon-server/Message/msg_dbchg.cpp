/*-----------------------------------------------------------------------------*
*
* File:   msg_dbchg
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_dbchg.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:23:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\mutex.h>
#include <rw/collect.h>
#include <rw/rwtime.h>


#include "collectable.h"
#include "msg_dbchg.h"
#include "dlldefs.h"
#include "ctibase.h"
#include "logger.h"

RWDEFINE_COLLECTABLE( CtiDBChangeMsg, MSG_DBCHANGE );

void
CtiDBChangeMsg::restoreGuts(RWvistream& aStream)
{
   CtiMessage::restoreGuts( aStream );         // Base class is not really a RWCollectible, but could be.

   aStream >> _id
           >> _database
           >> _category
           >> _objecttype
           >> _typeofchange;
}

void
CtiDBChangeMsg::saveGuts(RWvostream &aStream) const
{
   CtiMessage::saveGuts( aStream );            // Base class is not really a RWCollectible, but could be.

   aStream << _id
           << _database
           << _category
           << _objecttype
           << _typeofchange;
}

void
CtiDBChangeMsg::What() const
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "CtiDBChangeMsg.... " << _id << " " << _database << " " << _category << " "
           << _objecttype << " " << _typeofchange << endl;
   }
}

// Return a new'ed copy of this message!
CtiMessage* CtiDBChangeMsg::replicateMessage() const
{
   CtiDBChangeMsg *ret = CTIDBG_new CtiDBChangeMsg(*this);

   return( (CtiMessage*)ret );
}

void CtiDBChangeMsg::dump() const
{
   Inherited::dump();

   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " Object Id:          " << _id           << endl;
   dout << " Object Database:    " << _database     << endl;
   dout << " Object Category:    " << _category     << endl;
   dout << " Object Type:        " << _objecttype   << endl;
   dout << " Type of Change:     " << _typeofchange << endl;
}

CtiDBChangeMsg::CtiDBChangeMsg(LONG id,INT database, RWCString category, RWCString objecttype, INT typeofchange) :
   _id(id),
   _database(database),
   _category(category),
   _objecttype(objecttype),
   _typeofchange(typeofchange),
   CtiMessage(15)
{}

// Default Constructor needed for Roque Wave !!!!!!!!!!NEVER USE!!!!!!!!!!!
CtiDBChangeMsg::CtiDBChangeMsg()
{}
// Default Constructor needed for Roque Wave !!!!!!!!!!NEVER USE!!!!!!!!!!!

CtiDBChangeMsg::CtiDBChangeMsg(const CtiDBChangeMsg& aRef)
{
   *this = aRef;
}

CtiDBChangeMsg::~CtiDBChangeMsg() {}

CtiDBChangeMsg& CtiDBChangeMsg::operator=(const CtiDBChangeMsg& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      _id             = aRef.getId();
      _database       = aRef.getDatabase();
      _category       = aRef.getCategory();
      _objecttype     = aRef.getObjectType();
      _typeofchange   = aRef.getTypeOfChange();
   }
   return *this;
}

LONG         CtiDBChangeMsg::getId() const              { return _id; }
INT          CtiDBChangeMsg::getDatabase() const        { return _database; }
RWCString    CtiDBChangeMsg::getCategory() const        { return _category; }
RWCString    CtiDBChangeMsg::getObjectType() const      { return _objecttype; }
INT          CtiDBChangeMsg::getTypeOfChange() const    { return _typeofchange; }


