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
#include <rw/tpslist.h>

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

   RWTPtrSlist< T > _orphans;

   int _dberrorcode;

public:

   typedef RWTPtrHashMap<CtiHashKey, T, my_hash<CtiHashKey> , equal_to<CtiHashKey> >::value_type   val_pair;
   typedef RWTPtrHashMapIterator<CtiHashKey, T, my_hash<CtiHashKey> , equal_to<CtiHashKey> >       CtiRTDBIterator;


   CtiRTDB() {}

   virtual ~CtiRTDB()
   {
      LockGuard guard(monitor());
      Map.clearAndDestroy();
      _orphans.clearAndDestroy();       // Clean up the leftovers if there are any.
   }

   bool orphan( long id )
   {
       bool status = false;

       T* temp = NULL;
       LockGuard  gaurd(monitor());
       CtiHashKey key(id);

       temp = (T*)Map.find( &key );
       CtiHashKey *foundKey = Map.remove( &key );
       delete foundKey;

       if(temp)
       {
           status = true;
           _orphans.insert( temp );     // Save this guy out so we know we found him
       }

       return status;
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

   int removeAndDestroy(RWBoolean (*removeFunc)(T*, void*), void* d)
   {
       int count = 0;

       RWTPtrSlist< CtiHashKey >       deleteKeys;
       RWTPtrSlist< CtiDeviceBase >    deleteObjs;

       CtiRTDBIterator mapitr(Map);
       CtiDeviceBase   *pdev = 0;
       CtiHashKey      *pkey = 0;

       for(;mapitr();)
       {
           pdev = mapitr.value();
           pkey = mapitr.key();

           if((*removeFunc)(pdev, d))
           {
               count++;
               deleteKeys.insert(pkey);
               deleteObjs.insert(pdev);
           }
       }

       for(int kpos = 0; kpos < deleteKeys.entries(); kpos++)
       {
           Map.remove(deleteKeys[kpos]);
       }

       deleteKeys.clearAndDestroy();
       deleteObjs.clearAndDestroy();

       return count;
   }

   size_t entries() const
   {
      LockGuard guard(monitor());
      return Map.entries();
   }

   RWTPtrHashMap<CtiHashKey, T, my_hash<CtiHashKey> , equal_to<CtiHashKey> > & getMap()      { return Map; }
   RWRecursiveLock<RWMutexLock> &      getMux()       { return mutex(); }

   int getErrorCode() const { return _dberrorcode; };
   int setErrorCode(int ec)
   {
       if( ec ) _dberrorcode = ec;      // Only set it if there was an error (don't re-set it)
       return ec;
   }

   void resetErrorCode()
   {
       _dberrorcode = 0;      // Only set it if there was an error (don't re-set it)
   }


};

#endif      // #ifndef __RTDB_H__

