#pragma once

#include "windef.h"  //  for INT and UINT - should be removed when they're converted to int, unsigned, etc
#include <limits.h>

class CtiParseValue
{
protected:

   std::string      _str;
   INT            _int;
   double         _dbl;

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

   CtiParseValue(const std::string str, const INT ival = INT_MIN) :
      flag(0),
      _str(str),
      _int(ival),
      _dbl(0.0)
   {
      _sValid = TRUE;
      if(_int != INT_MIN)
      {
         _iValid = TRUE;
         _dbl    = (double)_int;
         _dValid = TRUE;
      }
   }

   CtiParseValue(INT i) :
      flag(0),
      _int(i),
      _dbl((double)i)
   {
      _iValid = TRUE;
      _dValid = TRUE;
   }

   CtiParseValue(UINT ui) :
      flag(0),
      _int((INT)ui),
      _dbl((double)ui)
   {
      _iValid = TRUE;
      _dValid = TRUE;
   }

   CtiParseValue(double real) :
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
   std::string      getString() const                   { return _str; }
   std::string&     getString()                         { return _str; }
   CtiParseValue& setString(const std::string aStr)
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
   double         getReal() const                     { return _dbl; }
   double&        getReal()                           { return _dbl; }
   CtiParseValue& setReal(const double aDbl)
   {
      _dbl = aDbl;
      return *this;
   }
};
