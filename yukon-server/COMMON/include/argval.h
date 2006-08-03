#ifndef  __CtiARGVAL_H__
#define  __CtiARGVAL_H__

#include <string>
#include <map>

using std::string;
using std::map;


class CtiArgValue
{
public:
   CtiArgValue();
   CtiArgValue(char* val);
   ~CtiArgValue() {}

   CtiArgValue& operator=(const CtiArgValue& key);

   int ReturnIntOpt    (int *opt);
   int ReturnDoubleOpt (double *opt);
   int ReturnStringOpt (char *opt, int len);

   string&  getValue()    { return Value; }
   int         isNumeric();

private:
   string Value;
};


#endif // #ifndef  __CtiARGVAL_H__

