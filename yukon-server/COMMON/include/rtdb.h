#ifndef __RTDB_H__
#define __RTDB_H__

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*************************************************************************
 *
 * rtdb.h      7/7/99
 *
 *****
 *
 *  A templatized run/real time data base class.  Initially done with a
 *  RogueWave singly linked list solely for the benefits of the apply and
 *  find functionality without the need for Standard C++ library.
 *
 * Originated by:
 *     Corey G. Plender    7/7/99
 *
 *
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/
#include <windows.h>
#include <iostream>
#include <functional>
using namespace std;

#include <rw/tphdict.h>


#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <rw\thr\mutex.h>
#include <rw\cstring.h>

#include "dlldefs.h"
#include "hashkey.h"

/*
 *  These are the Configuration Parameters for the Port Real Time Database
 */

#include "dllbase.h"

template <class K>
struct my_hash
{
   unsigned long operator() (const K x) const { return x.hash(); }
};

template < class T >
class IM_EX_CTIBASE CtiRTDB : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

   // This is a keyed Mapping which does not allow duplicates!
   RWTPtrHashMap<CtiHashKey, T, my_hash<CtiHashKey> , equal_to<CtiHashKey> > Map;

public:

   typedef RWTPtrHashMap<CtiHashKey, T, my_hash<CtiHashKey> , equal_to<CtiHashKey> >::value_type   val_pair;
   typedef RWTPtrHashMapIterator<CtiHashKey, T, my_hash<CtiHashKey> , equal_to<CtiHashKey> >       CtiRTDBIterator;


   CtiRTDB() {}

   virtual ~CtiRTDB()
   {
      LockGuard guard(monitor());
      Map.clearAndDestroy();
   }

   void apply(void (*applyFun)(const CtiHashKey*, T*&, void*), void* d)
   {
      LockGuard  gaurd(monitor());
      Map.apply(applyFun, d);
   }

   /*
    *  This operation will return a value to us by
    *
    *  Function should have the following definition
    *
    *    RWBoolean yourTester(T*, void* d);
    */

   T* find(RWBoolean (*testFun)(T*, void*),void* d)
   {
      LockGuard  gaurd(monitor());

      CtiRTDBIterator   itr(Map);

      for(; ++itr ;)
      {
         if(testFun(itr.value(), d))
            return itr.value();
      }

      return NULL;
   }

   T* remove(RWBoolean (*testFun)(T*, void*), void* d)
   {
      T* temp = NULL;
      LockGuard  gaurd(monitor());

      CtiRTDBIterator   itr(Map);

      for(; ++itr ;)
      {
         if(testFun(itr.value(), d))
         {
            temp = itr.value();
            CtiHashKey *key = (CtiHashKey *)Map.remove(itr.key());

            // Make sure the key gets cleaned up!
            delete key;
            break;
         }
      }

      return temp;
   }

   size_t entries() const
   {
      LockGuard guard(monitor());
      return Map.entries();
   }

   RWTPtrHashMap<CtiHashKey, T, my_hash<CtiHashKey> , equal_to<CtiHashKey> > & getMap()      { return Map; }
   RWRecursiveLock<RWMutexLock> &      getMux()       { return mutex(); }

};

#endif      // #ifndef __RTDB_H__

