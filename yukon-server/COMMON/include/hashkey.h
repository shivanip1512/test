#pragma warning( disable : 4786)

#ifndef __HASHKEY_H__
#define __HASHKEY_H__

#include <rw\defs.h>
#include <limits.h>

class CtiHashKey
{
private:

   bool              _bIDBased;
   RWCString         _hashStr;
   unsigned long     ID;


   CtiHashKey() {}
public:

   CtiHashKey(unsigned long MyId = LONG_MAX) :
      _bIDBased(true),
      ID(MyId)
   {}

   CtiHashKey(const RWCString str) :
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

   RWCString      getHashStr() const                    { return _hashStr; }
   CtiHashKey&    setHashStr(const RWCString &aStr)
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
         return _hashStr.hash();
      }
   }

};

#endif      // #ifndef __HASHKEY_H__


