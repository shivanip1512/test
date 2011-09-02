#pragma once

#include <limits.h>
#include <string>
#include "hash_functions.h"

class CtiHashKey
{
private:

   bool              _bIDBased;
   std::string       _hashStr;
   unsigned long     ID;


   CtiHashKey() {}
public:

   CtiHashKey(unsigned long MyId = LONG_MAX) :
      _bIDBased(true),
      ID(MyId)
   {}

   CtiHashKey(const std::string str) :
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

   std::string      getHashStr() const                    { return _hashStr; }
   CtiHashKey&    setHashStr(const std::string &aStr)
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
