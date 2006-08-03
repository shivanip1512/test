#ifndef  __CtiARGKEY_H__
#define  __CtiARGKEY_H__

#include <string>
#include <map>

using std::string;
using std::map;

class CtiArgKey
{
public:
   CtiArgKey();
   CtiArgKey(char key);
   ~CtiArgKey() {}

   CtiArgKey& operator=(const CtiArgKey& key);

   string&  getKey()    { return Key; }

private:
   string   Key;
};

#endif //#ifndef  __CtiARGKEY_H__

