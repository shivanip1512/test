#pragma warning( disable : 4786)

#ifndef __HASHKEY_H__
#define __HASHKEY_H__


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
      _hashStr(str)
   {
       ID = hash();
   }

   bool operator < ( const CtiHashKey &aRef) const
   {
      if(_bIDBased)
      {
         return (ID < aRef.getID());
      }
      else
      {
          if( ID == aRef.ID )
          {
              return (_hashStr < aRef.getHashStr() );
          }
          else
          {
              return ID < aRef.ID;
          }
      }
   }

   bool virtual operator==(const CtiHashKey& aRef) const
   {
      if(_bIDBased)
      {
         return (ID == aRef.getID());
      }
      else
      {
          if( ID == aRef.ID )
          {
              return (_hashStr == aRef.getHashStr());
          }
          else
          {
              return false;
          }
         
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


