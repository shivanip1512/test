#include "yukon.h"

#include <string.h>
#include <stdlib.h>
#include <ctype.h>

#include "argval.h"

RWDEFINE_COLLECTABLE(CtiArgValue, 0x1235)

CtiArgValue::CtiArgValue() : Value()
{;}

CtiArgValue::CtiArgValue(char *key) : Value(key)
{
   Value.toLower();
}


// Assignement operator
CtiArgValue&
CtiArgValue::operator=(const CtiArgValue& val)
{
   Value = val.Value;
   return (*this);
}

RWspace
CtiArgValue::binaryStoreSize() const
{
   return Value.binaryStoreSize();
}

int
CtiArgValue::compareTo(const RWCollectable *X ) const
{
   RWCString aStr = ((const CtiArgValue*)X)->Value;

   if(Value == aStr) return 0;

   if(Value > aStr)
   {
      return 1;
   }
   else
   {
      return -1;
   }
}

unsigned
CtiArgValue::hash() const
{
   return Value.hash();
}

RWBoolean
CtiArgValue::isEqual(const RWCollectable *c) const
{
   RWCString aValue = (((const CtiArgValue*)c)->Value);
   return (Value == aValue);
}


void
CtiArgValue::restoreGuts(RWFile& aFile)
{
   RWCollectable::restoreGuts( aFile );
   aFile >> Value;
}
void
CtiArgValue::restoreGuts(RWvistream& aStream)
{
   RWCollectable::restoreGuts( aStream );
   aStream >> Value;
}

void
CtiArgValue::saveGuts(RWFile &aFile) const
{
   RWCollectable::saveGuts( aFile );
   aFile << Value;
}

void
CtiArgValue::saveGuts(RWvostream &aStream) const
{
   RWCollectable::saveGuts( aStream );
   aStream << Value;
}


int
CtiArgValue::ReturnIntOpt(int *opt)
{
   int bRet = TRUE;
   *opt = atoi(Value);
   return bRet;
}

int
CtiArgValue::ReturnDoubleOpt (double *opt)
{
   int bRet = TRUE;

   *opt = atof(Value);
   return bRet;
}

int
CtiArgValue::ReturnStringOpt (char *opt, int len)
{
   int bRet = TRUE;

   strncpy(opt, Value, len);
   return bRet;
}


int
CtiArgValue::isNumeric()
{
   int     bRet = TRUE;


   if(!isdigit(Value(0)))
   {
       bRet = FALSE;
   }

   return bRet;
}
