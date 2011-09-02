#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <iostream>
#include <functional>
#include <list>
#include <map>

#include <rw/tphdict.h>

#include <rw/thr/recursiv.h>

#include <rw\thr\mutex.h>

#include <boost/utility.hpp>

#include "dlldefs.h"
#include "hashkey.h"
#include "utility.h"
#include "string_utility.h"

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
class CtiRTDB : public boost::noncopyable
{
private:
    mutable CtiMutex _classMutex;
protected:

   // This is a keyed Mapping which does not allow duplicates!
   std::map<long, T* > Map;

   std::list< T* >     _orphans;

   int _dberrorcode;

public:

   typedef typename std::map< long, T* >::value_type       val_pair;
   typedef typename std::map< long, T* >::iterator       MapIterator;


   CtiRTDB() {}

   virtual ~CtiRTDB()
   {
      CtiLockGuard<CtiMutex> guard(_classMutex);
      delete_container(_orphans);
      _orphans.clear();       // Clean up the leftovers if there are any.
      //deletes memory pointed to in the value pointer
      for (MapIterator itr = Map.begin(); itr != Map.end(); itr++) {
          delete (*itr).second;
      }
      //clear out the remaining
      Map.clear();

   }

   bool orphan( long id )
   {
       bool status = false;

       T* temp = NULL;
       MapIterator itr;

       CtiLockGuard<CtiMutex> guard(_classMutex);
       //CtiHashKey key(id);

       itr = Map.find( id );
       if ( itr != Map.end() ) {
           long foundKey = (*itr).first;
           temp = (*itr).second;

           Map.erase( id );
       }else
           temp = NULL;

       if(temp)
       {
           status = true;
           _orphans.push_back( temp );     // Save this guy out so we know we found him
       }
       else
       {
           std::cout << CtiTime() << " **** Checkpoint **** " << FO(__FILE__) << " (" << __LINE__ << ")" << endl;
       }

       return status;
   }

   /*
    *  This operation will return a value to us by
    *
    *  Function should have the following definition
    *
    *    RWBoolean yourTester(T*, void* d);
    */

   T* find(bool (*testFun)(T*, void*),void* d)
   {
      CtiLockGuard<CtiMutex> guard(_classMutex);

      MapIterator   itr = Map.begin();

      for( ; itr != Map.end() ; ++itr)
      {
         if( testFun((*itr).second, d) )
            return (*itr).second;
      }

      return NULL;
   }

   T* remove(bool (*testFun)(T*, void*), void* d)
   {
      T* temp = NULL;
      CtiLockGuard<CtiMutex> guard(_classMutex);

      MapIterator   itr = Map.begin();

      for( ; itr != Map.end() ; ++itr )
      {
         if(testFun( (*itr).second, d))
         {
            temp = (*itr).second;
            long key = (*itr).first;
            Map.erase( (*itr).first );

            break;
         }
      }

      return temp;
   }

   int removeAndDestroy(bool (*removeFunc)(T*, void*), void* d)
   {
       int count = 0;

       std::list< long >       deleteKeys;
       std::list< T* >         deleteObjs;

       MapIterator mapitr = Map.begin();
       T   *pdev = 0;
       long             pkey = 0;

       for(; mapitr != Map.end() ; ++mapitr )
       {
           pdev = (*mapitr).second;
           pkey = (*mapitr).first;

           if((*removeFunc)(pdev, d))
           {
               count++;
               deleteKeys.push_back(pkey);
               deleteObjs.push_back(pdev);
           }
       }
       std::list<long>::const_iterator itr = deleteKeys.begin();
       while( itr != deleteKeys.end() )
       //for(int kpos = 0; kpos < deleteKeys.entries(); kpos++)
       {
           Map.erase(*itr);
           itr++;
       }//TSFLAG

       delete_container(deleteObjs);
       deleteKeys.clear();
       deleteObjs.clear();

       return count;
   }

   size_t entries() const
   {
      CtiLockGuard<CtiMutex> guard(_classMutex);
      return Map.size();
   }

   std::map<long , T* > & getMap()      { return Map; }
   CtiMutex &      getMux()       { return _classMutex; }

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
