#include "yukon.h"

#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <algorithm>

#include "argval.h"
#include "hash_functions.h"
#include "rwutil.h"

RWDEFINE_COLLECTABLE(CtiArgValue, 0x1235)

CtiArgValue::CtiArgValue() : Value()
{;}

CtiArgValue::CtiArgValue(char *key) : Value(key)
{
   std::transform(Value.begin(), Value.end(), Value.begin(), tolower);
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
   return ::strlen(Value.c_str());
}

int
CtiArgValue::compareTo(const RWCollectable *X ) const
{
   string aStr = ((const CtiArgValue*)X)->Value;

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
   return RSHash(Value);
}

RWBoolean
CtiArgValue::isEqual(const RWCollectable *c) const
{
   string aValue = (((const CtiArgValue*)c)->Value);
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
   aFile << Value.c_str();
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
   *opt = atoi(Value.c_str());
   return bRet;
}

int
CtiArgValue::ReturnDoubleOpt (double *opt)
{
   int bRet = TRUE;

   *opt = atof(Value.c_str());
   return bRet;
}

int
CtiArgValue::ReturnStringOpt (char *opt, int len)
{
   int bRet = TRUE;

   strncpy(opt, Value.c_str(), len);
   return bRet;
}


int
CtiArgValue::isNumeric()
{
   int     bRet = TRUE;


   if(!isdigit(Value[0]))
   {
       bRet = FALSE;
   }

   return bRet;
}
