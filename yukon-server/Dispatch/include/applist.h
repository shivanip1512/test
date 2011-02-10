#ifndef __CtiAPPLIST_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   applist
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/applist.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:58 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __CtiAPPLIST_H__

#include <rw/collect.h>

#include <rw/pstream.h>
#include <rw/rstream.h>
#include <rw/hashdict.h>

#include <rw/tphasht.h>

#include "con_mgr.h"

template <class T, class H, class EQ>
class CtiApplList
{
private:

   // A Hash Dictionary which holds pointers
   RWTPtrHashMultiSet<T, H, EQ>          mHash;

public:
   CtiApplList();
   CtiApplList(const CtiApplList &aRef);
   ~CtiApplList();

   int                     Add(T* ConnMan);
   int                     Remove(T* ConnMan);
   int                     getCount();// { return mHash.entries(); }
   void                    Puke();

   T*  getByAppId(RWThreadId &TID);
   RWTPtrHashTable<T, H, EQ>& getTable();//  { return mHash; }

   // Error enumerations.
   enum
   {
      NORMAL = 0,
      INSERTFAILED,
      KEYNOTFOUND       // get failed because the key was not found
   };
};

#endif         // #ifndef __CtiAPPLIST__

