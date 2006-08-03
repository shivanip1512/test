#ifndef  __CONFIGKEY_H__
#define  __CONFIGKEY_H__

#include <rw/collect.h>

#include <rw/pstream.h>
#include <rw/rstream.h>

#include <string>

using std::string;

class CtiConfigKey
{
private:
   string   Key;

public:

   CtiConfigKey();
   CtiConfigKey(const string& key);
   ~CtiConfigKey() {}

   
   CtiConfigKey& operator=(const CtiConfigKey& key);

   string   getKey() const { return Key; }
   string&   getKey()        { return Key; }
};

#endif //#ifndef  __CONFIGKEY_H__

