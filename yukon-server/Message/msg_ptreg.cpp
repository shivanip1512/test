/*-----------------------------------------------------------------------------*
*
* File:   msg_ptreg
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_ptreg.cpp-arc  $
* REVISION     :  $Revision: 1.8.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/collect.h>

#include "collectable.h"
#include "logger.h"
#include "msg_ptreg.h"
#include <rw\thr\mutex.h>
#include "dlldefs.h"

#include "dllbase.h"

RWDEFINE_COLLECTABLE( CtiPointRegistrationMsg, MSG_POINTREGISTRATION );

void
CtiPointRegistrationMsg::restoreGuts(RWvistream& aStream)
{
   int         ptcnt;
   unsigned    utemp;
   double      dtemp;
   LONG        itemp;

   CtiMessage::restoreGuts( aStream );

   aStream >> RegFlags;
   aStream >> ptcnt;

   for(int i = 0; i < ptcnt; i++)
   {
      aStream >> itemp;

      PointList.push_back(itemp);
   }
}

void
CtiPointRegistrationMsg::saveGuts(RWvostream &aStream) const
{
   CtiMessage::saveGuts( aStream );

   aStream << RegFlags;
   aStream << PointList.size();              // How many are on their way....

   std::vector<LONG>::const_iterator itr = PointList.begin();
   for( ; itr != PointList.end(); itr++)
   {
      aStream << *itr;
   }
}

// Return a new'ed copy of this message!
CtiMessage* CtiPointRegistrationMsg::replicateMessage() const
{
   CtiPointRegistrationMsg *ret = CTIDBG_new CtiPointRegistrationMsg(*this);

   return( (CtiMessage*)ret );
}

void CtiPointRegistrationMsg::dump() const
{
   Inherited::dump();

   CtiLockGuard<CtiLogger> doubt_guard(dout);

   dout << " Registration Flags            " << RegFlags << endl;

   std::vector<LONG>::const_iterator itr = PointList.begin();
   for( ; itr != PointList.end(); itr++)
   {
      dout << " Registering for Point         " << *itr << endl;
   }
}

CtiPointRegistrationMsg::CtiPointRegistrationMsg(int Flag, int Pri) :
   RegFlags(Flag),
   CtiMessage(Pri)
{}

CtiPointRegistrationMsg::~CtiPointRegistrationMsg()
{
   PointList.clear();
}

CtiPointRegistrationMsg::CtiPointRegistrationMsg(const CtiPointRegistrationMsg &aRef)
{
   *this = aRef;
}


CtiPointRegistrationMsg& CtiPointRegistrationMsg::operator=(const CtiPointRegistrationMsg& aRef)
{
   int i;

   if(this != &aRef)
   {
      Inherited::operator=(aRef);
      RegFlags    = aRef.getFlags();

      PointList.clear();                  // Get me empty..

      for(i = 0; i < aRef.getCount(); i++)     // Copy them in..
      {
         insert(aRef[i]);
      }
   }
   return *this;
}

// If list is empty, I assume you wanted them all!.
int CtiPointRegistrationMsg::getCount() const       { return PointList.size(); }

LONG& CtiPointRegistrationMsg::operator[](size_t i)
{
   return PointList[i];
}

LONG CtiPointRegistrationMsg::operator[](size_t i) const
{
   return PointList[i];
}

// Clear out the list.
void CtiPointRegistrationMsg::clear()
{
   PointList.clear();
}

void CtiPointRegistrationMsg::insert(const LONG& a)
{
   PointList.push_back(a);
}

int CtiPointRegistrationMsg::getFlags() const { return RegFlags; }

