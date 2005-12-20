#pragma warning( disable : 4786)

#ifndef __HASHKEY_H__
#define __HASHKEY_H__

#include <rw\defs.h>
#include <limits.h>
#include <string>
#include "hash_functions.h"

using std::string;

class CtiHashKey
{
private:

   bool              _bIDBased;
   string         _hashStr;
   unsigned long     ID;


   CtiHashKey() {}
public:

   CtiHashKey(unsigned long MyId = LONG_MAX) :
      _bIDBased(true),
      ID(MyId)
   {}

   CtiHashKey(const string str) :
      _bIDBased(false),
      _hashStr(str),
      ID(LONG_MAX)
   {}

   RWBoolean operator < ( const CtiHashKey &aRef) const
   {
      if(_bIDBased)
      {
         return (ID < aRef.getID());
      }
      else
      {
         return( _hashStr < aRef.getHashStr() );
      }
   }

   RWBoolean virtual operator==(const CtiHashKey& aRef) const
   {
      if(_bIDBased)
      {
         return (ID == aRef.getID());
      }
      else
      {
         return (_hashStr == aRef.getHashStr() );
      }
   }

   unsigned long  getID() const                    { return ID; }
   CtiHashKey&    setID(const unsigned long id)
   {
      ID = id;
      return *this;
   }

   string      getHashStr() const                    { return _hashStr; }
   CtiHashKey&    setHashStr(const string &aStr)
   {
      _hashStr = aStr;
      return *this;
   }

   unsigned long  hash() const
   {
      if(_bIDBased)
      {
         return ID;
      }
      else
      {
         return RSHash(_hashStr);
      }
   }

};

#endif      // #ifndef __HASHKEY_H__


