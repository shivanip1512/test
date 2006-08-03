#include "yukon.h"

#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <algorithm>

#include "argval.h"

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
