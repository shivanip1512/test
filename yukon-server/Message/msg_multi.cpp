#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
* File:   msg_multi
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_multi.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/12/12 01:03:00 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\mutex.h>
#include <rw/collect.h>
#include <rw/rwtime.h>

#include "collectable.h"
#include "msg_multi.h"
#include "dlldefs.h"
#include "ctibase.h"
#include "logger.h"

RWDEFINE_COLLECTABLE( CtiMultiMsg, MSG_MULTI );

void CtiMultiMsg::restoreGuts(RWvistream& aStream)
{
   Inherited::restoreGuts( aStream );
   aStream >> _bag;
}

void CtiMultiMsg::saveGuts(RWvostream &aStream) const
{
   Inherited::saveGuts( aStream );
   aStream << _bag;
}

void CtiMultiMsg::What() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << "CtiMultiMsg.... " << endl;
}

void CtiMultiMsg::dump() const
{
   for(int i = 0; i < _bag.entries(); i++)
   {
      ((CtiMessage*)_bag[i])->dump();
   }
}

CtiMultiMsg&  CtiMultiMsg::setData(const RWOrdered& Data)
{
   for(int i = 0; i < Data.entries(); i++)
   {
      RWCollectable *pNew = Data[i]->newSpecies();

      if(pNew != NULL)
      {
         *pNew = *(Data[i]);     // Use object's copy constructor!
         _bag.insert(pNew);
      }
      else
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " setPointData failed to copy an element of type " << Data[i]->isA() << endl;
      }
   }

   return *this;
}

// Return a new'ed copy of this message!
CtiMessage* CtiMultiMsg::replicateMessage() const
{
   CtiMultiMsg *ret = CTIDBG_new CtiMultiMsg(*this);

   return( (CtiMessage*)ret );
}

CtiMessage& CtiMultiMsg::setConnectionHandle(VOID *p)
{
   ConnectionHandle = p;

   for(int i = 0; i < _bag.entries(); i++)
   {
      ((CtiMessage*)_bag[i])->setConnectionHandle(p);
   }

   return *this;
}

VOID* CtiMultiMsg::getConnectionHandle()
{
   return ConnectionHandle;
}

CtiMultiMsg::CtiMultiMsg(const RWOrdered &pointData, int Pri) :
   _bag(pointData),
   CtiMessage(Pri)
{}

CtiMultiMsg::~CtiMultiMsg()
{
   _bag.clearAndDestroy();    // Clean up any leftovers.
}

CtiMultiMsg::CtiMultiMsg(const CtiMultiMsg &aRef)
{
   *this = aRef;
}


CtiMultiMsg& CtiMultiMsg::operator=(const CtiMultiMsg& aRef)
{
   int i;

   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      _bag.clearAndDestroy();     // Make sure it is empty!

      for(int i = 0; i < aRef.getCount(); i++)
      {
         // This guy creates a copy of himself and returns a CtiMessage pointer to the copy!
         CtiMessage* newp = aRef[i]->replicateMessage();
         _bag.insert(newp);
      }
   }

   return *this;
}

int CtiMultiMsg::getCount() const       { return _bag.entries(); }

CtiPointDataMsg* CtiMultiMsg::operator[](size_t i)
{
   return (CtiPointDataMsg*)_bag[i];
}

CtiPointDataMsg* CtiMultiMsg::operator[](size_t i) const
{
   return (CtiPointDataMsg*)_bag[i];
}

// Clear out the list.
void CtiMultiMsg::clear()
{
   _bag.clearAndDestroy();
}

void CtiMultiMsg::insert(RWCollectable* a)
{
   _bag.insert(a);
}

const RWOrdered& CtiMultiMsg::getData() const     { return _bag; }
RWOrdered& CtiMultiMsg::getData()                 { return _bag; }

