/*-----------------------------------------------------------------------------*
*
* File:   parsevalue
*
* Class:  CtiParseValue
* Date:   2/22/2000
*
* Author: Corey G. Plender
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PARSEVALUE_H__
#define __PARSEVALUE_H__
#pragma warning( disable : 4786)


#include <limits.h>

class CtiParseValue
{
protected:

   RWCString      _str;
   INT            _int;
   DOUBLE         _dbl;

   union {
      UINT flag;

      struct {
         UINT     _sValid : 1;
         UINT     _iValid : 1;
         UINT     _dValid : 1;
      };
   };

private:

public:

   CtiParseValue() :
      _int(INT_MIN),
      _dbl(0), //DBL_MIN),
      flag(0)
   {}

   //  note the RWCSubString version of this below
   CtiParseValue(const RWCString str, const INT ival = INT_MIN) :
      flag(0),
      _str(str),
      _int(ival)
   {
      _sValid = TRUE;
      if(_int != INT_MIN)
      {
         _iValid = TRUE;
         _dbl    = (DOUBLE)_int;
         _dValid = TRUE;
      }
   }

   //  this is the RWCSubString version of the above - make sure you implement any changes to the above down here as well
   CtiParseValue(const RWCSubString substr, const INT ival = INT_MIN) :
      flag(0),
      _str(substr),
      _int(ival)
   {
      _sValid = TRUE;
      if(_int != INT_MIN)
      {
         _iValid = TRUE;
         _dbl    = (DOUBLE)_int;
         _dValid = TRUE;
      }
   }

   CtiParseValue(INT i) :
      flag(0),
      _int(i),
      _dbl((DOUBLE)i)
   {
      _iValid = TRUE;
      _dValid = TRUE;
   }

   CtiParseValue(UINT ui) :
      flag(0),
      _int((INT)ui),
      _dbl((DOUBLE)ui)
   {
      _iValid = TRUE;
      _dValid = TRUE;
   }

   CtiParseValue(DOUBLE real) :
      flag(0),
      _dbl(real),
      _int((INT)real)
   {
      _dValid = TRUE;
      _iValid = TRUE;
   }

   CtiParseValue(const CtiParseValue& aRef)
   {
      *this = aRef;
   }

   virtual ~CtiParseValue() {}

   CtiParseValue& operator=(const CtiParseValue& aRef)
   {
      if(this != &aRef)
      {
         _str     = aRef.getString();
         _int     = aRef.getInt();
         _dbl     = aRef.getReal();

         _sValid  = aRef.isStringValid();
         _iValid  = aRef.isIntValid();
         _dValid  = aRef.isRealValid();

      }
      return *this;
   }

   BOOL operator==(const CtiParseValue& aRef)
   {
      return ( _str == aRef.getString()   &&
               _int == aRef.getInt()      &&
               _dbl == aRef.getReal()
             );
   }

   UINT           isStringValid() const               { return _sValid; }
   RWCString      getString() const                   { return _str; }
   RWCString&     getString()                         { return _str; }
   CtiParseValue& setString(const RWCString aStr)
   {
      _str = aStr;
      return *this;
   }

   UINT           isIntValid() const                  { return _iValid; }
   INT            getInt() const                      { return _int; }
   INT&           getInt()                            { return _int; }
   CtiParseValue& setInt(const INT aInt)
   {
      _int = aInt;
      return *this;
   }

   UINT           isRealValid() const                 { return _dValid; }
   DOUBLE         getReal() const                     { return _dbl; }
   DOUBLE&        getReal()                           { return _dbl; }
   CtiParseValue& setReal(const DOUBLE aDbl)
   {
      _dbl = aDbl;
      return *this;
   }
};
#endif // #ifndef __PARSEVALUE_H__
