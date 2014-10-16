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
#include "precompiled.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/collect.h>

#include "collectable.h"
#include "logger.h"
#include "msg_ptreg.h"
#include <rw\thr\mutex.h>
#include "dlldefs.h"

#include "dllbase.h"

DEFINE_COLLECTABLE( CtiPointRegistrationMsg, MSG_POINTREGISTRATION );

// Return a new'ed copy of this message!
CtiMessage* CtiPointRegistrationMsg::replicateMessage() const
{
   CtiPointRegistrationMsg *ret = CTIDBG_new CtiPointRegistrationMsg(*this);

   return( (CtiMessage*)ret );
}

std::string CtiPointRegistrationMsg::toString() const
{
   Cti::FormattedList itemList;

   itemList <<"CtiPointRegistrationMsg";
   itemList.add("Registration Flags") << RegFlags;

   std::vector<LONG>::const_iterator itr = PointList.begin();
   for( ; itr != PointList.end(); itr++)
   {
       itemList.add("Registering for Point") << *itr;
   }

   return (Inherited::toString() += itemList.toString());
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

int CtiPointRegistrationMsg::getFlags() const
{
    return RegFlags;
}

void CtiPointRegistrationMsg::setFlags(int flags)
{
    RegFlags = flags;
}

const std::vector<LONG>& CtiPointRegistrationMsg::getPointList() const
{
    return PointList;
}

void CtiPointRegistrationMsg::setPointList( const std::vector<LONG>& points )
{
    PointList.assign( points.begin(), points.end() );
}
