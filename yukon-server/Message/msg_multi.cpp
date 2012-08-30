/*-----------------------------------------------------------------------------*
*
* File:   msg_multi
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_multi.cpp-arc  $
* REVISION     :  $Revision: 1.10.4.1 $
* DATE         :  $Date: 2008/11/13 17:23:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\mutex.h>
#include <rw/collect.h>


#include "collectable.h"
#include "msg_multi.h"
#include "dlldefs.h"
#include "ctibase.h"
#include "logger.h"
#include "utility.h"

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

void CtiMultiMsg::dump() const
{
   Inherited::dump();

   for(int i = 0; i < _bag.size(); i++)
   {
      ((CtiMessage*)_bag[i])->dump();
   }
}

CtiMultiMsg&  CtiMultiMsg::setData(const CtiMultiMsg_vec& Data)
{
   for(int i = 0; i < Data.size(); i++)
   {
      RWCollectable *pNew = Data[i]->copy();

      if(pNew != NULL)
      {
         _bag.push_back(pNew);
      }
      else
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " setPointData failed to copy an element of type " << Data[i]->isA() << endl;
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

CtiMessage& CtiMultiMsg::setConnectionHandle(void *p)
{
   ConnectionHandle = p;

   for(int i = 0; i < _bag.size(); i++)
   {
      ((CtiMessage*)_bag[i])->setConnectionHandle(p);
   }

   return *this;
}

void* CtiMultiMsg::getConnectionHandle()
{
   return ConnectionHandle;
}

CtiMultiMsg::CtiMultiMsg(CtiMultiMsg_vec& pointData, int Pri) :
   CtiMessage(Pri)
{
    CtiMultiMsg_vec::iterator itr;
    itr = pointData.begin();
    for(;itr != pointData.end(); itr++ )
        _bag.push_back(*itr);
}

CtiMultiMsg::~CtiMultiMsg()
{
   delete_container(_bag);
   _bag.clear();    // Clean up any leftovers.
}

CtiMultiMsg::CtiMultiMsg(const CtiMultiMsg &aRef)
{
   *this = aRef;
}


CtiMultiMsg& CtiMultiMsg::operator=(const CtiMultiMsg& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
      delete_container(_bag);
      _bag.clear();     // Make sure it is empty!

      for(int i = 0; i < aRef.getCount(); i++)
      {
         // This guy creates a copy of himself and returns a CtiMessage pointer to the copy!
         CtiMessage* newp = aRef[i]->replicateMessage();
         _bag.push_back(newp);
      }
   }

   return *this;
}

int CtiMultiMsg::getCount() const       { return _bag.size(); }

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
    delete_container(_bag);
   _bag.clear();
}

void CtiMultiMsg::insert(RWCollectable* a)
{
   _bag.push_back(a);
}

const CtiMultiMsg_vec& CtiMultiMsg::getData() const     { return _bag; }
CtiMultiMsg_vec& CtiMultiMsg::getData()                 { return _bag; }

