#include "yukon.h"
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

#include "configval.h"

RWDEFINE_COLLECTABLE(CtiConfigValue, 0x1235)

CtiConfigValue::CtiConfigValue() : Value()
{;}

CtiConfigValue::CtiConfigValue(RWCString key) : Value(key)
{
   // Value.toLower();
}


// Assignement operator
CtiConfigValue&
CtiConfigValue::operator=(const CtiConfigValue& val)
{
   Value = val.Value;
   return (*this);
}

RWspace
CtiConfigValue::binaryStoreSize() const
{
   return Value.binaryStoreSize();
}

int
CtiConfigValue::compareTo(const RWCollectable *X ) const
{
   RWCString aStr = ((const CtiConfigValue*)X)->Value;

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
CtiConfigValue::hash() const
{
   return Value.hash();
}

RWBoolean
CtiConfigValue::isEqual(const RWCollectable *c) const
{
   RWCString aValue = (((const CtiConfigValue*)c)->Value);
   return (Value == aValue);
}


void
CtiConfigValue::restoreGuts(RWFile& aFile)
{
   RWCollectable::restoreGuts( aFile );
   aFile >> Value;
}
void
CtiConfigValue::restoreGuts(RWvistream& aStream)
{
   RWCollectable::restoreGuts( aStream );
   aStream >> Value;
}

void
CtiConfigValue::saveGuts(RWFile &aFile) const
{
   RWCollectable::saveGuts( aFile );
   aFile << Value;
}

void
CtiConfigValue::saveGuts(RWvostream &aStream) const
{
   RWCollectable::saveGuts( aStream );
   aStream << Value;
}
